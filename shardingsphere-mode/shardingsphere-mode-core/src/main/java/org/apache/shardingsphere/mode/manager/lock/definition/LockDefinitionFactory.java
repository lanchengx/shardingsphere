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

package org.apache.shardingsphere.mode.manager.lock.definition;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.apache.shardingsphere.infra.lock.LockMode;
import org.apache.shardingsphere.infra.lock.LockDefinition;

/**
 * Lock definition factory.
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class LockDefinitionFactory {
    
    /**
     * New database lock definition.
     *
     * @param databaseName database name
     * @return database lock definition
     */
    public static LockDefinition newDatabaseLockDefinition(final String databaseName) {
        return new DatabaseLockDefinition(LockMode.READ, new DatabaseLockNameDefinition(databaseName));
    }
}
