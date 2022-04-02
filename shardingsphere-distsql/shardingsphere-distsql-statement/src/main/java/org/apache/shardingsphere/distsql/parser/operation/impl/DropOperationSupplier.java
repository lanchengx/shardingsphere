/*
 * Copyright © 2022，Beijing Sifei Software Technology Co., LTD.
 * All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential
 */

package org.apache.shardingsphere.distsql.parser.operation.impl;

import org.apache.shardingsphere.distsql.parser.operation.DistSQLOperationSupplier;
import org.apache.shardingsphere.distsql.parser.operation.DistSQLOperationTypeEnum;

/**
 * Drop privilege supplier.
 */
public interface DropOperationSupplier extends DistSQLOperationSupplier {
    
    @Override
    default DistSQLOperationTypeEnum getOperationType() {
        return DistSQLOperationTypeEnum.DROP;
    }
}
