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

package org.apache.shardingsphere.proxy.backend.text.distsql.ral;

import com.google.common.base.Preconditions;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.apache.shardingsphere.distsql.parser.statement.ral.RALStatement;
import org.apache.shardingsphere.infra.database.type.DatabaseType;
import org.apache.shardingsphere.mode.manager.ContextManager;
import org.apache.shardingsphere.proxy.backend.context.ProxyContext;
import org.apache.shardingsphere.proxy.backend.response.header.ResponseHeader;
import org.apache.shardingsphere.proxy.backend.session.ConnectionSession;
import org.apache.shardingsphere.proxy.backend.text.TextProtocolBackendHandler;

import java.sql.SQLException;

/**
 * RAL backend handler .
 */
@NoArgsConstructor
public abstract class RALBackendHandler<E extends RALStatement, R extends RALBackendHandler> implements TextProtocolBackendHandler {
    
    @Getter
    private E sqlStatement;
    
    @Override
    public final ResponseHeader execute() throws SQLException {
        Preconditions.checkArgument(null != sqlStatement, "sql statement cannot be empty.");
        ContextManager contextManager = ProxyContext.getInstance().getContextManager();
        return handle(contextManager, sqlStatement);
    }
    
    protected abstract R init(HandlerParameter<E> parameter);
    
    protected abstract ResponseHeader handle(ContextManager contextManager, E sqlStatement);
    
    protected final void setSqlStatement(final E sqlStatement) {
        this.sqlStatement = sqlStatement;
    }
    
    @Getter
    @Builder
    protected static class HandlerParameter<E extends RALStatement> {
        
        private E statement;
        
        private DatabaseType databaseType;
        
        private ConnectionSession connectionSession;
    }
}
