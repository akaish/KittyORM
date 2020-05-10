
/*
 * ---
 *
 *  Copyright (c) 2018-2020 Denis Bogomolov (akaish)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * This file is a part of KittyORM project (KittyORM library), more information at
 * https://akaish.github.io/KittyORMPages/license/
 *
 * ---
 */

package net.akaish.kitty.orm.query;

import java.text.MessageFormat;

/**
 * Wrapper for query that selects all table names in specified database
 * Created by akaish on 01.07.2018.
 * @author akaish (Denis Bogomolov)
 */
public class SelectTableNamesQuery extends BaseKittyQuery {
    static final String SELECT_FROM_0_SQLITE_MASTER_WHERE_TYPE_TABLE = "SELECT * FROM {0}.sqlite_master WHERE type='table';";
    private String schemaName;
    static final String IA_SCHEMA_NOT_SET = "Schema name has to be set to use SelectTableNamesQuery for KittyORM!";

    public SelectTableNamesQuery(String tableName) {
        super(tableName);
    }

    public void setSchemaName(String schemaName) {
        this.schemaName = schemaName;
    }

    @Override
    String getMainClause() {
        return SELECT_FROM_0_SQLITE_MASTER_WHERE_TYPE_TABLE;
    }

    @Override
    public String getQueryStart() {
        if(schemaName == null) {
            throw new IllegalArgumentException(IA_SCHEMA_NOT_SET);
        }
        return MessageFormat.format(SELECT_FROM_0_SQLITE_MASTER_WHERE_TYPE_TABLE, schemaName);
    }
}
