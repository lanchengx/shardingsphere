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

package org.apache.shardingsphere.test.sql.parser.parameterized.asserts.statement.distsql.rql.impl.rule;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.apache.shardingsphere.shadow.distsql.parser.statement.ShowShadowRulesStatement;
import org.apache.shardingsphere.test.sql.parser.parameterized.asserts.SQLCaseAssertContext;
import org.apache.shardingsphere.test.sql.parser.parameterized.asserts.segment.schema.DatabaseAssert;
import org.apache.shardingsphere.test.sql.parser.parameterized.jaxb.cases.domain.statement.distsql.rql.ShowShadowRulesStatementTestCase;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * Show shadow rules statement assert.
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class ShowShadowRulesStatementAssert {
    
    /**
     * Assert show shadow rule statement is correct with expected parser result.
     *
     * @param assertContext assert context
     * @param actual actual show shadow rules statement
     * @param expected expected show shadow rules statement test case
     */
    public static void assertIs(final SQLCaseAssertContext assertContext, final ShowShadowRulesStatement actual, final ShowShadowRulesStatementTestCase expected) {
        if (null != expected.getSchema()) {
            assertTrue(assertContext.getText("Actual schema should exist."), actual.getDatabase().isPresent());
            DatabaseAssert.assertIs(assertContext, actual.getDatabase().get(), expected.getSchema());
        } else {
            assertFalse(assertContext.getText("Actual schema should not exist."), actual.getDatabase().isPresent());
        }
    }
}
