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

package org.apache.shardingsphere.authority.checker;

import org.apache.shardingsphere.authority.config.AuthorityRuleConfiguration;
import org.apache.shardingsphere.authority.rule.AuthorityRule;
import org.apache.shardingsphere.infra.config.algorithm.ShardingSphereAlgorithmConfiguration;
import org.apache.shardingsphere.infra.executor.check.SQLChecker;
import org.apache.shardingsphere.infra.executor.check.SQLCheckerFactory;
import org.apache.shardingsphere.infra.metadata.database.ShardingSphereDatabase;
import org.apache.shardingsphere.infra.metadata.user.Grantee;
import org.apache.shardingsphere.infra.metadata.user.ShardingSphereUser;
import org.apache.shardingsphere.sql.parser.sql.common.statement.ddl.CreateTableStatement;
import org.apache.shardingsphere.sql.parser.sql.common.statement.dml.InsertStatement;
import org.apache.shardingsphere.sql.parser.sql.common.statement.dml.SelectStatement;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Answers;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.Properties;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;

@RunWith(MockitoJUnitRunner.class)
public final class AuthorityCheckerTest {
    
    @Mock(answer = Answers.RETURNS_DEEP_STUBS)
    private ShardingSphereDatabase database;
    
    @SuppressWarnings("unchecked")
    @Test
    public void assertCheckSchemaByAllPrivilegesPermitted() {
        Collection<ShardingSphereUser> users = new LinkedList<>();
        ShardingSphereUser root = new ShardingSphereUser("root", "", "localhost");
        users.add(root);
        AuthorityRuleConfiguration ruleConfig = new AuthorityRuleConfiguration(users, new ShardingSphereAlgorithmConfiguration("ALL_PERMITTED", new Properties()));
        AuthorityRule rule = new AuthorityRule(ruleConfig, Collections.emptyMap());
        SQLChecker<AuthorityRule> sqlChecker = SQLCheckerFactory.getInstance(Collections.singleton(rule)).get(rule);
        assertTrue(sqlChecker.check("db0", new Grantee("root", "localhost"), rule));
    }
    
    @SuppressWarnings("unchecked")
    @Test
    public void assertCheckUser() {
        Collection<ShardingSphereUser> users = new LinkedList<>();
        ShardingSphereUser root = new ShardingSphereUser("root", "", "localhost");
        users.add(root);
        AuthorityRuleConfiguration ruleConfig = new AuthorityRuleConfiguration(users, new ShardingSphereAlgorithmConfiguration("ALL_PERMITTED", new Properties()));
        AuthorityRule rule = new AuthorityRule(ruleConfig, Collections.emptyMap());
        SQLChecker<AuthorityRule> sqlChecker = SQLCheckerFactory.getInstance(Collections.singleton(rule)).get(rule);
        assertTrue(sqlChecker.check(new Grantee("root", "localhost"), rule));
        assertFalse(sqlChecker.check(new Grantee("root", "192.168.0.1"), rule));
        assertFalse(sqlChecker.check(new Grantee("admin", "localhost"), rule));
    }
    
    @SuppressWarnings("unchecked")
    @Test
    public void assertCheckSQLStatement() {
        Collection<ShardingSphereUser> users = new LinkedList<>();
        ShardingSphereUser root = new ShardingSphereUser("root", "", "localhost");
        users.add(root);
        AuthorityRuleConfiguration ruleConfig = new AuthorityRuleConfiguration(users, new ShardingSphereAlgorithmConfiguration("ALL_PERMITTED", new Properties()));
        AuthorityRule rule = new AuthorityRule(ruleConfig, Collections.emptyMap());
        SQLChecker<AuthorityRule> sqlChecker = SQLCheckerFactory.getInstance(Collections.singleton(rule)).get(rule);
        SelectStatement selectStatement = mock(SelectStatement.class);
        CreateTableStatement createTableStatement = mock(CreateTableStatement.class);
        InsertStatement insertStatement = mock(InsertStatement.class);
        assertTrue(sqlChecker.check(selectStatement, Collections.emptyList(), new Grantee("root", "localhost"), "db0", Collections.emptyMap(), rule).isPassed());
        assertTrue(sqlChecker.check(insertStatement, Collections.emptyList(), new Grantee("root", "localhost"), "db0", Collections.emptyMap(), rule).isPassed());
        assertTrue(sqlChecker.check(createTableStatement, Collections.emptyList(), new Grantee("root", "localhost"), "db0", Collections.emptyMap(), rule).isPassed());
    }
}
