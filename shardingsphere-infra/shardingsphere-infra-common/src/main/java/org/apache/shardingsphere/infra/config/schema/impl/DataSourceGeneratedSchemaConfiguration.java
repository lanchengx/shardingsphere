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

package org.apache.shardingsphere.infra.config.schema.impl;

import com.zaxxer.hikari.HikariDataSource;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.apache.shardingsphere.infra.config.RuleConfiguration;
import org.apache.shardingsphere.infra.config.schema.SchemaConfiguration;
import org.apache.shardingsphere.infra.datasource.config.DataSourceConfiguration;
import org.apache.shardingsphere.infra.datasource.pool.creator.DataSourcePoolCreator;
import org.apache.shardingsphere.infra.datasource.props.DataSourceProperties;
import org.apache.shardingsphere.infra.datasource.props.DataSourcePropertiesCreator;

import javax.sql.DataSource;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;

/**
 * Data source generated schema configuration.
 */
@RequiredArgsConstructor
public final class DataSourceGeneratedSchemaConfiguration implements SchemaConfiguration {
    
    private final Map<String, DataSourceConfiguration> dataSources;
    
    @Getter
    private final Collection<RuleConfiguration> ruleConfigurations;
    
    @Override
    public Map<String, DataSource> getDataSources() {
        // TODO convert to DataSource by DataSourceConfiguration directly
        return DataSourcePoolCreator.create(createDataSourcePropertiesMap());
    }
    
    private Map<String, DataSourceProperties> createDataSourcePropertiesMap() {
        return dataSources.entrySet().stream().collect(Collectors.toMap(Entry::getKey,
            entry -> DataSourcePropertiesCreator.create(HikariDataSource.class.getName(), entry.getValue()), (oldValue, currentValue) -> oldValue, LinkedHashMap::new));
    }
}
