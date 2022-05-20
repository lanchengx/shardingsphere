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

package org.apache.shardingsphere.sharding.distsql.parser.statement;

import org.apache.shardingsphere.distsql.parser.statement.rql.show.ShowRulesStatement;
import org.apache.shardingsphere.distsql.parser.subject.impl.ShardingSubjectSupplier;
import org.apache.shardingsphere.sql.parser.sql.common.segment.generic.DatabaseSegment;

import java.util.Optional;

/**
 * Show sharding table rules used key generator statement.
 */
public final class ShowShardingTableRulesUsedKeyGeneratorStatement extends ShowRulesStatement implements ShardingSubjectSupplier {
    
    private final String keyGeneratorName;
    
    public ShowShardingTableRulesUsedKeyGeneratorStatement(final String keyGeneratorName, final DatabaseSegment schema) {
        super(schema);
        this.keyGeneratorName = keyGeneratorName;
    }
    
    /**
     * Get key generator name.
     *
     * @return key generator name.
     */
    public Optional<String> getKeyGeneratorName() {
        return Optional.ofNullable(keyGeneratorName);
    }
}
