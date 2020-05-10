
/*
 * ---
 *
 *  Copyright (c) 2019-2020 Denis Bogomolov (akaish)
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

/**
 * Created by akaish on 01.06.2019.
 * @author akaish (Denis Bogomolov)
 */
public class PragmaTableInfoQuery extends BaseKittyQuery {

    static final String PRAGMA_TABLE_INFO = "PRAGMA table_info({0})";

    public PragmaTableInfoQuery(String tableName) {
        super(tableName);
    }

    @Override
    String getMainClause() {
        return PRAGMA_TABLE_INFO;
    }
}
