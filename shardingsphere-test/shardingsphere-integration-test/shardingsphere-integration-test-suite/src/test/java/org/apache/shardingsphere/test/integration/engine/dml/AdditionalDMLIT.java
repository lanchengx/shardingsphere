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

package org.apache.shardingsphere.test.integration.engine.dml;

import org.apache.shardingsphere.test.integration.cases.SQLCommandType;
import org.apache.shardingsphere.test.integration.cases.SQLExecuteType;
import org.apache.shardingsphere.test.integration.cases.value.SQLValue;
import org.apache.shardingsphere.test.integration.env.IntegrationTestEnvironment;
import org.apache.shardingsphere.test.integration.framework.param.array.ParameterizedArrayFactory;
import org.apache.shardingsphere.test.integration.framework.param.model.AssertionParameterizedArray;
import org.apache.shardingsphere.test.integration.framework.runner.parallel.annotaion.ParallelLevel;
import org.apache.shardingsphere.test.integration.framework.runner.parallel.annotaion.ParallelRuntimeStrategy;
import org.junit.Test;
import org.junit.runners.Parameterized.Parameters;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.ParseException;
import java.util.Collection;
import java.util.Collections;

import static org.junit.Assert.assertFalse;

@ParallelRuntimeStrategy(ParallelLevel.SCENARIO)
public final class AdditionalDMLIT extends BaseDMLIT {
    
    public AdditionalDMLIT(final AssertionParameterizedArray parameterizedArray) {
        super(parameterizedArray);
    }
    
    @Parameters(name = "{0}")
    public static Collection<AssertionParameterizedArray> getParameters() {
        if (IntegrationTestEnvironment.getInstance().isRunAdditionalTestCases()) {
            return ParameterizedArrayFactory.getAssertionParameterized(SQLCommandType.DML);
        }
        return Collections.emptyList();
    }
    
    @Test
    public void executeUpdateWithAutoGeneratedKeys() throws SQLException, ParseException {
        // TODO fix replica_query
        if ("replica_query".equals(getScenario())) {
            return;
        }
        int actualUpdateCount;
        try (Connection connection = getTargetDataSource().getConnection()) {
            actualUpdateCount = SQLExecuteType.Literal == getSqlExecuteType()
                    ? executeUpdateForStatementWithAutoGeneratedKeys(connection) : executeUpdateForPreparedStatementWithAutoGeneratedKeys(connection);
        }
        assertDataSet(actualUpdateCount);
    }
    
    private int executeUpdateForStatementWithAutoGeneratedKeys(final Connection connection) throws SQLException, ParseException {
        try (Statement statement = connection.createStatement()) {
            return statement.executeUpdate(String.format(getSQL(), getAssertion().getSQLValues().toArray()), Statement.NO_GENERATED_KEYS);
        }
    }
    
    private int executeUpdateForPreparedStatementWithAutoGeneratedKeys(final Connection connection) throws SQLException, ParseException {
        try (PreparedStatement preparedStatement = connection.prepareStatement(getSQL(), Statement.NO_GENERATED_KEYS)) {
            for (SQLValue each : getAssertion().getSQLValues()) {
                preparedStatement.setObject(each.getIndex(), each.getValue());
            }
            return preparedStatement.executeUpdate();
        }
    }
    
    @Test
    public void assertExecuteUpdateWithColumnIndexes() throws SQLException, ParseException {
        // TODO fix replica_query
        if ("PostgreSQL".equals(getDatabaseType().getName()) || "replica_query".equals(getScenario())) {
            return;
        }
        int actualUpdateCount;
        try (Connection connection = getTargetDataSource().getConnection()) {
            actualUpdateCount = SQLExecuteType.Literal == getSqlExecuteType() ? executeUpdateForStatementWithColumnIndexes(connection) : executeUpdateForPreparedStatementWithColumnIndexes(connection);
        }
        assertDataSet(actualUpdateCount);
    }
    
    private int executeUpdateForStatementWithColumnIndexes(final Connection connection) throws SQLException, ParseException {
        try (Statement statement = connection.createStatement()) {
            return statement.executeUpdate(String.format(getSQL(), getAssertion().getSQLValues().toArray()), new int[]{1});
        }
    }
    
    private int executeUpdateForPreparedStatementWithColumnIndexes(final Connection connection) throws SQLException, ParseException {
        try (PreparedStatement preparedStatement = connection.prepareStatement(getSQL(), new int[]{1})) {
            for (SQLValue each : getAssertion().getSQLValues()) {
                preparedStatement.setObject(each.getIndex(), each.getValue());
            }
            return preparedStatement.executeUpdate();
        }
    }
    
    @Test
    public void assertExecuteUpdateWithColumnNames() throws SQLException, ParseException {
        // TODO fix replica_query
        if ("PostgreSQL".equals(getDatabaseType().getName()) || "replica_query".equals(getScenario())) {
            return;
        }
        int actualUpdateCount;
        try (Connection connection = getTargetDataSource().getConnection()) {
            actualUpdateCount = SQLExecuteType.Literal == getSqlExecuteType() ? executeUpdateForStatementWithColumnNames(connection) : executeUpdateForPreparedStatementWithColumnNames(connection);
        }
        assertDataSet(actualUpdateCount);
    }
    
    private int executeUpdateForStatementWithColumnNames(final Connection connection) throws SQLException, ParseException {
        try (Statement statement = connection.createStatement()) {
            return statement.executeUpdate(String.format(getSQL(), getAssertion().getSQLValues().toArray()));
        }
    }
    
    private int executeUpdateForPreparedStatementWithColumnNames(final Connection connection) throws SQLException, ParseException {
        try (PreparedStatement preparedStatement = connection.prepareStatement(getSQL(), new String[]{"TODO"})) {
            for (SQLValue each : getAssertion().getSQLValues()) {
                preparedStatement.setObject(each.getIndex(), each.getValue());
            }
            return preparedStatement.executeUpdate();
        }
    }
    
    @Test
    public void assertExecuteWithoutAutoGeneratedKeys() throws SQLException, ParseException {
        // TODO fix replica_query
        if ("replica_query".equals(getScenario())) {
            return;
        }
        int actualUpdateCount;
        try (Connection connection = getTargetDataSource().getConnection()) {
            actualUpdateCount = SQLExecuteType.Literal == getSqlExecuteType()
                    ? executeForStatementWithoutAutoGeneratedKeys(connection) : executeForPreparedStatementWithoutAutoGeneratedKeys(connection);
        }
        assertDataSet(actualUpdateCount);
    }
    
    private int executeForStatementWithoutAutoGeneratedKeys(final Connection connection) throws SQLException, ParseException {
        try (Statement statement = connection.createStatement()) {
            assertFalse("Not a DML statement.", statement.execute(String.format(getSQL(), getAssertion().getSQLValues().toArray()), Statement.NO_GENERATED_KEYS));
            return statement.getUpdateCount();
        }
    }
    
    private int executeForPreparedStatementWithoutAutoGeneratedKeys(final Connection connection) throws SQLException, ParseException {
        try (PreparedStatement preparedStatement = connection.prepareStatement(getSQL(), Statement.NO_GENERATED_KEYS)) {
            for (SQLValue each : getAssertion().getSQLValues()) {
                preparedStatement.setObject(each.getIndex(), each.getValue());
            }
            assertFalse("Not a DML statement.", preparedStatement.execute());
            return preparedStatement.getUpdateCount();
        }
    }
    
    @Test
    public void assertExecuteWithAutoGeneratedKeys() throws SQLException, ParseException {
        // TODO fix replica_query
        if ("replica_query".equals(getScenario())) {
            return;
        }
        int actualUpdateCount;
        try (Connection connection = getTargetDataSource().getConnection()) {
            actualUpdateCount = SQLExecuteType.Literal == getSqlExecuteType() ? executeForStatementWithAutoGeneratedKeys(connection) : executeForPreparedStatementWithAutoGeneratedKeys(connection);
        }
        assertDataSet(actualUpdateCount);
    }
    
    private int executeForStatementWithAutoGeneratedKeys(final Connection connection) throws SQLException, ParseException {
        try (Statement statement = connection.createStatement()) {
            assertFalse("Not a DML statement.", statement.execute(String.format(getSQL(), getAssertion().getSQLValues().toArray()), Statement.RETURN_GENERATED_KEYS));
            return statement.getUpdateCount();
            // TODO assert statement.getGeneratedKeys();
        }
    }
    
    private int executeForPreparedStatementWithAutoGeneratedKeys(final Connection connection) throws SQLException, ParseException {
        try (PreparedStatement preparedStatement = connection.prepareStatement(getSQL(), Statement.RETURN_GENERATED_KEYS)) {
            for (SQLValue each : getAssertion().getSQLValues()) {
                preparedStatement.setObject(each.getIndex(), each.getValue());
            }
            assertFalse("Not a DML statement.", preparedStatement.execute());
            return preparedStatement.getUpdateCount();
            // TODO assert preparedStatement.getGeneratedKeys();
        }
    }
    
    @Test
    public void assertExecuteWithColumnIndexes() throws SQLException, ParseException {
        // TODO fix replica_query
        if ("PostgreSQL".equals(getDatabaseType().getName()) || "replica_query".equals(getScenario())) {
            return;
        }
        int actualUpdateCount;
        try (Connection connection = getTargetDataSource().getConnection()) {
            actualUpdateCount = SQLExecuteType.Literal == getSqlExecuteType() ? executeForStatementWithColumnIndexes(connection) : executeForPreparedStatementWithColumnIndexes(connection);
        }
        assertDataSet(actualUpdateCount);
    }
    
    private int executeForStatementWithColumnIndexes(final Connection connection) throws SQLException, ParseException {
        try (Statement statement = connection.createStatement()) {
            assertFalse("Not a DML statement.", statement.execute(String.format(getSQL(), getAssertion().getSQLValues().toArray()), new int[]{1}));
            return statement.getUpdateCount();
        }
    }
    
    private int executeForPreparedStatementWithColumnIndexes(final Connection connection) throws SQLException, ParseException {
        try (PreparedStatement preparedStatement = connection.prepareStatement(getSQL(), new int[]{1})) {
            for (SQLValue each : getAssertion().getSQLValues()) {
                preparedStatement.setObject(each.getIndex(), each.getValue());
            }
            assertFalse("Not a DML statement.", preparedStatement.execute());
            return preparedStatement.getUpdateCount();
        }
    }
    
    @Test
    public void assertExecuteWithColumnNames() throws SQLException, ParseException {
        // TODO fix replica_query
        if ("PostgreSQL".equals(getDatabaseType().getName()) || "replica_query".equals(getScenario())) {
            return;
        }
        int actualUpdateCount;
        try (Connection connection = getTargetDataSource().getConnection()) {
            actualUpdateCount = SQLExecuteType.Literal == getSqlExecuteType() ? executeForStatementWithColumnNames(connection) : executeForPreparedStatementWithColumnNames(connection);
        }
        assertDataSet(actualUpdateCount);
    }
    
    private int executeForStatementWithColumnNames(final Connection connection) throws SQLException, ParseException {
        try (Statement statement = connection.createStatement()) {
            assertFalse("Not a DML statement.", statement.execute(String.format(getSQL(), getAssertion().getSQLValues().toArray()), new String[]{"TODO"}));
            return statement.getUpdateCount();
        }
    }
    
    private int executeForPreparedStatementWithColumnNames(final Connection connection) throws SQLException, ParseException {
        try (PreparedStatement preparedStatement = connection.prepareStatement(getSQL(), new String[]{"TODO"})) {
            for (SQLValue each : getAssertion().getSQLValues()) {
                preparedStatement.setObject(each.getIndex(), each.getValue());
            }
            assertFalse("Not a DML statement.", preparedStatement.execute());
            return preparedStatement.getUpdateCount();
        }
    }
}
