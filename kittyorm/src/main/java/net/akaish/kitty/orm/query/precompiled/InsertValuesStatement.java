
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

package net.akaish.kitty.orm.query.precompiled;

import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;

import net.akaish.kitty.orm.configuration.conf.KittyTableConfiguration;

import java.util.Iterator;
import java.util.List;

import static java.text.MessageFormat.format;
import static net.akaish.kitty.orm.util.KittyConstants.COMMA;
import static net.akaish.kitty.orm.util.KittyConstants.QSIGN;
import static net.akaish.kitty.orm.util.KittyConstants.WHITESPACE;

/**
 * Created by akaish on 08.09.18.
 * @author akaish (Denis Bogomolov)
 */

public class InsertValuesStatement extends PrecompiledSQLiteStatement {

    private final String INSERT_PATTERN = "INSERT INTO {0} ({1}) VALUES ({2})";

    public InsertValuesStatement(KittyTableConfiguration configuration, List<String> exclusions) {
        super(configuration, exclusions);
    }

    @Override
    protected String generateSQLStatement() {
        int columnsCount = columns.size();
        StringBuilder columnNames = new StringBuilder(columnsCount*8);
        StringBuilder bindHolders = new StringBuilder(columnsCount*4);
        Iterator<String> columnNamesIterator = columns.iterator();
        boolean first = true;
        while (columnNamesIterator.hasNext()) {
            if(first) {
                first = false;
            } else {
                columnNames.append(COMMA);
                columnNames.append(WHITESPACE);
                bindHolders.append(WHITESPACE);
                bindHolders.append(COMMA);
            }
            columnNames.append(columnNamesIterator.next());
            bindHolders.append(QSIGN);
        }
        return format(
                INSERT_PATTERN,
                tableName,
                columnNames.toString(),
                bindHolders.toString()
        );
    }

    @Override
    public SQLiteStatement getStatement(SQLiteDatabase database) {
        return database.compileStatement(statement);
    }

}
