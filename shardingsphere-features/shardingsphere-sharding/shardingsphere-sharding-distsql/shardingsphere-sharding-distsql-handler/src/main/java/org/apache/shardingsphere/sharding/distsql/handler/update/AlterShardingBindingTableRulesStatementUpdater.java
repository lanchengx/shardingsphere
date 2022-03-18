/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.apache.shardingsphere.sharding.distsql.handler.update;

import com.google.common.base.Splitter;
import org.apache.shardingsphere.infra.config.RuleConfiguration;
import org.apache.shardingsphere.infra.distsql.exception.DistSQLException;
import org.apache.shardingsphere.infra.distsql.exception.rule.DuplicateRuleException;
import org.apache.shardingsphere.infra.distsql.exception.rule.InvalidRuleConfigurationException;
import org.apache.shardingsphere.infra.distsql.exception.rule.RequiredRuleMissedException;
import org.apache.shardingsphere.infra.distsql.update.RuleDefinitionAlterUpdater;
import org.apache.shardingsphere.infra.metadata.ShardingSphereMetaData;
import org.apache.shardingsphere.sharding.api.config.ShardingRuleConfiguration;
import org.apache.shardingsphere.sharding.api.config.rule.ShardingAutoTableRuleConfiguration;
import org.apache.shardingsphere.sharding.api.config.rule.ShardingTableRuleConfiguration;
import org.apache.shardingsphere.sharding.api.config.strategy.sharding.ComplexShardingStrategyConfiguration;
import org.apache.shardingsphere.sharding.api.config.strategy.sharding.NoneShardingStrategyConfiguration;
import org.apache.shardingsphere.sharding.api.config.strategy.sharding.ShardingStrategyConfiguration;
import org.apache.shardingsphere.sharding.api.config.strategy.sharding.StandardShardingStrategyConfiguration;
import org.apache.shardingsphere.sharding.distsql.parser.segment.BindingTableRuleSegment;
import org.apache.shardingsphere.sharding.distsql.parser.statement.AlterShardingBindingTableRulesStatement;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Alter sharding binding table rule statement updater.
 */
public final class AlterShardingBindingTableRulesStatementUpdater implements RuleDefinitionAlterUpdater<AlterShardingBindingTableRulesStatement, ShardingRuleConfiguration> {
    
    @Override
    public void checkSQLStatement(final ShardingSphereMetaData shardingSphereMetaData, final AlterShardingBindingTableRulesStatement sqlStatement, 
                                  final ShardingRuleConfiguration currentRuleConfig) throws DistSQLException {
        String schemaName = shardingSphereMetaData.getName();
        checkCurrentRuleConfiguration(schemaName, currentRuleConfig);
        checkToBeAlertedBindingTables(schemaName, sqlStatement, currentRuleConfig);
        checkToBeAlteredDuplicateBindingTables(schemaName, sqlStatement);
        checkToBeAlteredCanBindBindingTables(sqlStatement, currentRuleConfig);
    }
    
    private void checkCurrentRuleConfiguration(final String schemaName, final ShardingRuleConfiguration currentRuleConfig) throws RequiredRuleMissedException {
        if (null == currentRuleConfig) {
            throw new RequiredRuleMissedException("Sharding", schemaName);
        }
    }
    
    private void checkToBeAlertedBindingTables(final String schemaName, final AlterShardingBindingTableRulesStatement sqlStatement, 
                                               final ShardingRuleConfiguration currentRuleConfig) throws DuplicateRuleException {
        Collection<String> currentLogicTables = getCurrentLogicTables(currentRuleConfig);
        Collection<String> notExistedBindingTables = sqlStatement.getBindingTables().stream().filter(each -> !currentLogicTables.contains(each)).collect(Collectors.toSet());
        if (!notExistedBindingTables.isEmpty()) {
            throw new DuplicateRuleException("binding", schemaName, notExistedBindingTables);
        }
    }
    
    private Collection<String> getCurrentLogicTables(final ShardingRuleConfiguration currentRuleConfig) {
        Collection<String> result = new HashSet<>();
        result.addAll(currentRuleConfig.getTables().stream().map(ShardingTableRuleConfiguration::getLogicTable).collect(Collectors.toSet()));
        result.addAll(currentRuleConfig.getAutoTables().stream().map(ShardingAutoTableRuleConfiguration::getLogicTable).collect(Collectors.toSet()));
        return result;
    }
    
    private void checkToBeAlteredDuplicateBindingTables(final String schemaName, final AlterShardingBindingTableRulesStatement sqlStatement) throws DuplicateRuleException {
        Collection<String> toBeAlteredBindingTables = new HashSet<>();
        Collection<String> duplicateBindingTables = sqlStatement.getBindingTables().stream().filter(each -> !toBeAlteredBindingTables.add(each)).collect(Collectors.toSet());
        if (!duplicateBindingTables.isEmpty()) {
            throw new DuplicateRuleException("binding", schemaName, duplicateBindingTables);
        }
    }
    
    private void checkToBeAlteredCanBindBindingTables(final AlterShardingBindingTableRulesStatement sqlStatement, final ShardingRuleConfiguration currentRuleConfig) throws DistSQLException {
        Collection<String> cannotBindRules = sqlStatement.getRules().stream().map(BindingTableRuleSegment::getTableGroups)
                .filter(each -> !canBind(currentRuleConfig, each)).collect(Collectors.toCollection(LinkedList::new));
        DistSQLException.predictionThrow(cannotBindRules.isEmpty(),
            () -> new InvalidRuleConfigurationException("binding", cannotBindRules, Collections.singleton("Unable to bind with different sharding strategies")));
    }
    
    private boolean canBind(final ShardingRuleConfiguration currentRuleConfig, final String bindingRule) {
        LinkedList<String> shardingTables = new LinkedList<>(Splitter.on(",").trimResults().splitToList(bindingRule));
        Collection<String> bindableShardingTables = getBindableShardingTable(currentRuleConfig, shardingTables.getFirst());
        return bindableShardingTables.containsAll(shardingTables);
    }
    
    private Collection<String> getBindableShardingTable(final ShardingRuleConfiguration currentRuleConfig, final String shardingTable) {
        Collection<String> result = new ArrayList<>();
        Optional<ShardingTableRuleConfiguration> tableRule = getFromTable(currentRuleConfig.getTables(), shardingTable);
        tableRule.ifPresent(op -> result.addAll(getBindableShardingTable(currentRuleConfig, op)));
        Optional<ShardingAutoTableRuleConfiguration> autoTableRule = getFromAutoTable(currentRuleConfig.getAutoTables(), shardingTable);
        autoTableRule.ifPresent(op -> result.addAll(getBindableShardingAutoTable(currentRuleConfig, op)));
        return result;
    }
    
    private Collection<String> getBindableShardingTable(final ShardingRuleConfiguration currentRuleConfig, final ShardingTableRuleConfiguration tableRule) {
        return currentRuleConfig.getTables().stream()
                .filter(each -> hasSameShardingStrategy(each.getTableShardingStrategy(), tableRule.getTableShardingStrategy()))
                .filter(each -> hasSameShardingStrategy(each.getDatabaseShardingStrategy(), tableRule.getDatabaseShardingStrategy()))
                .map(ShardingTableRuleConfiguration::getLogicTable).collect(Collectors.toSet());
    }
    
    private Collection<String> getBindableShardingAutoTable(final ShardingRuleConfiguration currentRuleConfig, final ShardingAutoTableRuleConfiguration autoTableRule) {
        return currentRuleConfig.getAutoTables().stream()
                .filter(each -> hasSameShardingStrategy(each.getShardingStrategy(), autoTableRule.getShardingStrategy()))
                .map(ShardingAutoTableRuleConfiguration::getLogicTable).collect(Collectors.toSet());
    }
    
    private Optional<ShardingAutoTableRuleConfiguration> getFromAutoTable(final Collection<ShardingAutoTableRuleConfiguration> autoTableConfigurations, final String tableName) {
        return autoTableConfigurations.stream().filter(each -> each.getLogicTable().equals(tableName)).findAny();
    }
    
    private Optional<ShardingTableRuleConfiguration> getFromTable(final Collection<ShardingTableRuleConfiguration> tableConfigurations, final String tableName) {
        return tableConfigurations.stream().filter(each -> each.getLogicTable().equals(tableName)).findAny();
    }
    
    private boolean hasSameShardingStrategy(final ShardingStrategyConfiguration boundTableShardingStrategy, final ShardingStrategyConfiguration matchedTableShardingStrategy) {
        if (null == boundTableShardingStrategy && null == matchedTableShardingStrategy) {
            return true;
        }
        if (null != boundTableShardingStrategy && null != matchedTableShardingStrategy) {
            if (!boundTableShardingStrategy.getClass().getCanonicalName().equals(matchedTableShardingStrategy.getClass().getCanonicalName())) {
                return false;
            }
            String boundTableColumn;
            String matchTableColumn;
            if (boundTableShardingStrategy instanceof StandardShardingStrategyConfiguration) {
                boundTableColumn = ((StandardShardingStrategyConfiguration) boundTableShardingStrategy).getShardingColumn();
                matchTableColumn = ((StandardShardingStrategyConfiguration) matchedTableShardingStrategy).getShardingColumn();
            } else if (boundTableShardingStrategy instanceof ComplexShardingStrategyConfiguration) {
                boundTableColumn = ((ComplexShardingStrategyConfiguration) boundTableShardingStrategy).getShardingColumns();
                matchTableColumn = ((ComplexShardingStrategyConfiguration) matchedTableShardingStrategy).getShardingColumns();
            } else {
                return boundTableShardingStrategy instanceof NoneShardingStrategyConfiguration;
            }
            return boundTableColumn.equals(matchTableColumn);
        }
        return false;
    }
    
    @Override
    public RuleConfiguration buildToBeAlteredRuleConfiguration(final AlterShardingBindingTableRulesStatement sqlStatement) {
        ShardingRuleConfiguration result = new ShardingRuleConfiguration();
        for (BindingTableRuleSegment each : sqlStatement.getRules()) {
            result.getBindingTableGroups().add(each.getTableGroups());
        }
        return result;
    }
    
    @Override
    public void updateCurrentRuleConfiguration(final ShardingRuleConfiguration currentRuleConfig, final ShardingRuleConfiguration toBeAlteredRuleConfig) {
        dropRuleConfiguration(currentRuleConfig);
        addRuleConfiguration(currentRuleConfig, toBeAlteredRuleConfig);
    }
    
    private void dropRuleConfiguration(final ShardingRuleConfiguration currentRuleConfig) {
        currentRuleConfig.getBindingTableGroups().clear();
    }
    
    private void addRuleConfiguration(final ShardingRuleConfiguration currentRuleConfig, final ShardingRuleConfiguration toBeAlteredRuleConfig) {
        currentRuleConfig.getBindingTableGroups().addAll(toBeAlteredRuleConfig.getBindingTableGroups());
    }
    
    @Override
    public Class<ShardingRuleConfiguration> getRuleConfigurationClass() {
        return ShardingRuleConfiguration.class;
    }
    
    @Override
    public String getType() {
        return AlterShardingBindingTableRulesStatement.class.getName();
    }
}
