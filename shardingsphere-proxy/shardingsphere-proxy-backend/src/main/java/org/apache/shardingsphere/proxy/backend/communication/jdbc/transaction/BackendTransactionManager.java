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

package org.apache.shardingsphere.proxy.backend.communication.jdbc.transaction;

import org.apache.shardingsphere.transaction.TransactionHolder;
import org.apache.shardingsphere.proxy.backend.communication.jdbc.connection.BackendConnection;
import org.apache.shardingsphere.proxy.backend.context.ProxyContext;
import org.apache.shardingsphere.transaction.ShardingSphereTransactionManagerEngine;
import org.apache.shardingsphere.transaction.core.TransactionType;
import org.apache.shardingsphere.transaction.spi.ShardingSphereTransactionManager;

import java.sql.SQLException;

/**
 * Backend transaction manager.
 */
public final class BackendTransactionManager implements TransactionManager {
    
    private final BackendConnection connection;
    
    private final TransactionType transactionType;
    
    private final LocalTransactionManager localTransactionManager;
    
    private final ShardingSphereTransactionManager shardingSphereTransactionManager;
    
    public BackendTransactionManager(final BackendConnection backendConnection) {
        connection = backendConnection;
        transactionType = connection.getTransactionStatus().getTransactionType();
        localTransactionManager = new LocalTransactionManager(backendConnection);
        ShardingSphereTransactionManagerEngine engine = ProxyContext.getInstance().getContextManager().getTransactionContexts().getEngines().get(connection.getSchemaName());
        shardingSphereTransactionManager = null == engine ? null : engine.getTransactionManager(transactionType);
    }
    
    @Override
    public void begin() throws SQLException {
        if (!connection.getTransactionStatus().isInTransaction()) {
            connection.getTransactionStatus().setInTransaction(true);
            TransactionHolder.setInTransaction();
            connection.closeDatabaseCommunicationEngines(true);
            connection.closeConnections(false);
        }
        if (TransactionType.LOCAL == transactionType || null == shardingSphereTransactionManager) {
            localTransactionManager.begin();
        } else {
            shardingSphereTransactionManager.begin();
        }
    }
    
    @Override
    public void commit() throws SQLException {
        if (connection.getTransactionStatus().isInTransaction()) {
            try {
                if (TransactionType.LOCAL == transactionType || null == shardingSphereTransactionManager) {
                    localTransactionManager.commit();
                } else {
                    shardingSphereTransactionManager.commit();
                }
            } finally {
                connection.getTransactionStatus().setInTransaction(false);
                TransactionHolder.clear();
            }
        }
    }
    
    @Override
    public void rollback() throws SQLException {
        if (connection.getTransactionStatus().isInTransaction()) {
            try {
                if (TransactionType.LOCAL == transactionType || null == shardingSphereTransactionManager) {
                    localTransactionManager.rollback();
                } else {
                    shardingSphereTransactionManager.rollback();
                }
            } finally {
                connection.getTransactionStatus().setInTransaction(false);
                TransactionHolder.clear();
            }
        }
    }
    
    @Override
    public void setSavepoint(final String savepointName) throws SQLException {
        if (!connection.getTransactionStatus().isInTransaction()) {
            return;
        }
        if (TransactionType.LOCAL == transactionType || null == shardingSphereTransactionManager) {
            localTransactionManager.setSavepoint(savepointName);
        }
        // TODO Non-local transaction manager
    }
    
    @Override
    public void rollbackTo(final String savepointName) throws SQLException {
        if (!connection.getTransactionStatus().isInTransaction()) {
            return;
        }
        if (TransactionType.LOCAL == transactionType || null == shardingSphereTransactionManager) {
            localTransactionManager.rollbackTo(savepointName);
        }
        // TODO Non-local transaction manager
    }
    
    @Override
    public void releaseSavepoint(final String savepointName) throws SQLException {
        if (!connection.getTransactionStatus().isInTransaction()) {
            return;
        }
        if (TransactionType.LOCAL == transactionType || null == shardingSphereTransactionManager) {
            localTransactionManager.releaseSavepoint(savepointName);
        }
        // TODO Non-local transaction manager
    }
}
