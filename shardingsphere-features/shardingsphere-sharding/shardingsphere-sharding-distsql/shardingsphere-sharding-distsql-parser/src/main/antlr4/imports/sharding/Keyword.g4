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

lexer grammar Keyword;

import Alphabet;

WS
    : [ \t\r\n] + ->skip
    ;

CREATE
    : C R E A T E
    ;

ALTER
    : A L T E R
    ;

DROP
    : D R O P
    ;

SHOW
    : S H O W
    ;

SHARDING
    : S H A R D I N G
    ;

RULE
    :  R U L E
    ;

FROM
    : F R O M
    ;

RESOURCES
    : R E S O U R C E S
    ;

GENERATED_KEY
    : G E N E R A T E D UL_ K E Y
    ;

DEFAULT_TABLE_STRATEGY
    : D E F A U L T UL_ T A B L E UL_ S T R A T E G Y
    ;

TABLE
    : T A B L E
    ;

SHARDING_COLUMN
    : S H A R D I N G UL_ C O L U M N
    ;

TYPE
    : T Y P E
    ;

NAME
    : N A M E
    ;

PROPERTIES
    : P R O P E R T I E S
    ;

COLUMN
    : C O L U M N
    ;

BINDING
    : B I N D I N G
    ;

BROADCAST
    : B R O A D C A S T
    ;

RULES
    : R U L E S
    ;

COLUMNS
    : C O L U M N S
    ;

ALGORITHM
    : A L G O R I T H M
    ;

ALGORITHMS
    : A L G O R I T H M S
    ;

HINT
    : H I N T
    ;

SET
    : S E T
    ;

ADD
    : A D D
    ;

DATABASE_VALUE
    : D A T A B A S E UL_ V A L U E
    ;

TABLE_VALUE
    : T A B L E UL_ V A L U E
    ;

STATUS
    : S T A T U S
    ;

CLEAR
    : C L E A R
    ;

DEFAULT
    : D E F A U L T
    ;
    
DATABASE
    : D A T A B A S E
    ;

SHARDING_ALGORITHM
    : S H A R D I N G UL_ A L G O R I T H M
    ;

STRATEGY
    : S T R A T E G Y
    ;
        
DATANODES
    : D A T A N O D E S
    ;
    
DATABASE_STRATEGY
    : D A T A B A S E UL_ S T R A T E G Y
    ;
    
TABLE_STRATEGY
    : T A B L E UL_ S T R A T E G Y
    ;

COUNT
    : C O U N T
    ;


TABLES
    : T A B L E S
    ;

NODES
    : N O D E S
    ;

KEY
    : K E Y
    ;

GENERATOR
    : G E N E R A T O R
    ;
