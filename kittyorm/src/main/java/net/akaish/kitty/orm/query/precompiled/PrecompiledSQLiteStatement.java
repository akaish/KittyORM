
/*
 * ---
 *
 *  Copyright (c) 2018 Denis Bogomolov (akaish)
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

package net.akaish.kitty.orm.query.precompiled;

import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;

import net.akaish.kitty.orm.configuration.conf.KittyColumnConfiguration;
import net.akaish.kitty.orm.configuration.conf.KittyTableConfiguration;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by akaish on 08.09.18.
 * @author akaish (Denis Bogomolov)
 */

public abstract class PrecompiledSQLiteStatement {

    final LinkedList<String> columns;
    final String statement;
    final String tableName;
    final String schemaName;

    public PrecompiledSQLiteStatement(KittyTableConfiguration configuration, List<String> exclusions) {
        columns = new LinkedList<>();
        Iterator<KittyColumnConfiguration> iterator = configuration.sortedColumns.iterator();
        while (iterator.hasNext()) {
            KittyColumnConfiguration cf = iterator.next();
            if(exclusions != null)
                if(exclusions.contains(cf.mainConfiguration.columnField.getName()))
                    continue;
            columns.addLast(cf.mainConfiguration.columnName);
        }
        tableName = configuration.tableName;
        schemaName = configuration.schemaName;
        statement = generateSQLStatement();
    }

    protected abstract String generateSQLStatement();

    public abstract SQLiteStatement getStatement(SQLiteDatabase database);
}
