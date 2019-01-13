
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

package net.akaish.kitty.orm.indexes;

import net.akaish.kitty.orm.annotations.column.ONE_COLUMN_INDEX;
import net.akaish.kitty.orm.annotations.table.index.INDEX;
import net.akaish.kitty.orm.enums.Keywords;
import net.akaish.kitty.orm.exceptions.KittyRuntimeException;

import java.text.MessageFormat;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import static net.akaish.kitty.orm.util.KittyConstants.DOT;
import static net.akaish.kitty.orm.util.KittyConstants.SEMICOLON;
import static net.akaish.kitty.orm.util.KittyConstants.UNDERSCORE;
import static net.akaish.kitty.orm.util.KittyConstants.WHITESPACE;
import static net.akaish.kitty.orm.util.KittyUtils.implode;
import static net.akaish.kitty.orm.util.KittyUtils.implodeWithCommaInBKT;

/**
 * Created by akaish on 03.05.2018.
 * @author akaish (Denis Bogomolov)
 */

public class Index {
    protected final boolean unique;
    protected final boolean ifNotExists;
    protected final String schemaName;
    protected final String indexName;
    protected final String tableName;
    protected final String[] indexColumns;
    protected final String whereExpression;
    protected final HashSet<String> uniqueIndexColumns;

    protected static final String AME_INDEX_BAD_COLUMNS = "Index columns for index {0} at {1}.{2} has duplicate column names!";

    public Index(boolean unique, boolean ifNotExists, String schemaName, String indexName,
                 String tableName, String[] indexColumns, String whereExpression) {
        this.unique = unique;
        this.ifNotExists = ifNotExists;
        this.schemaName = schemaName;
        if(indexName == null)
            this.indexName = generateIndexName(schemaName, tableName, indexColumns, unique);
        else
            this.indexName = indexName;
        this.tableName = tableName;
        this.indexColumns = indexColumns;
        if(whereExpression != null)
            this.whereExpression = whereExpression;
        else
            this.whereExpression = null;
        uniqueIndexColumns = new HashSet<>();
        uniqueIndexColumns.addAll(Arrays.asList(indexColumns));
        if(uniqueIndexColumns.size() != indexColumns.length)
            throw new KittyRuntimeException(MessageFormat.format(AME_INDEX_BAD_COLUMNS, this.indexName, schemaName, tableName));
    }

    public Index(INDEX index, String schemaName, String tableName) {
        this(index.unique(), index.ifNotExists(), schemaName, index.indexName().length() == 0 ? null : index.indexName(),
                tableName, index.indexColumns(), index.whereExpression().length() == 0 ? null : index.whereExpression());
    }

    public Index(ONE_COLUMN_INDEX index, String indexColumn, String schemaName, String tableName) {
        this(index.unique(), index.ifNotExists(), schemaName, index.indexName().length() == 0 ? null : index.indexName(),
                tableName, new String[]{indexColumn}, index.whereExpression().length() == 0 ? null : index.whereExpression());
    }

    protected String generateIndexName(String schemaName, String tableName, String[] indexColumns, boolean unique) {
        String[] parts;
        if(unique) {
            parts = new String[4+indexColumns.length];
        } else {
            parts = new String[3+indexColumns.length];
        }
        parts[0] = schemaName;
        parts[1] = tableName;
        System.arraycopy(indexColumns, 0, parts, 2, indexColumns.length);
        if(unique) {
            parts[parts.length - 2] = Keywords.UNIQUE.toString();
        }
        parts[parts.length - 1] = Keywords.INDEX.toString();
        return implode(parts, UNDERSCORE);
    }

    public final Set<String> getIndexColumsSet() {
        Set<String> out = new HashSet<>();
        out.addAll(Arrays.asList(indexColumns));
        return out;
    }

    public final String getIndexName() {
        return indexName;
    }

    public final String getSchemaName() {
        return schemaName;
    }

    public final String getTableName() {
        return tableName;
    }

    @Override
    public String toString() {
        return toString(false);
    }

    public String toString(boolean skipSchemaName) {
        StringBuffer isb = new StringBuffer(128);
        isb.append(Keywords.CREATE).append(WHITESPACE);
        if(unique)
            isb.append(Keywords.UNIQUE).append(WHITESPACE);
        isb.append(Keywords.INDEX).append(WHITESPACE);
        if(ifNotExists)
            isb.append(Keywords.IF_NOT_EXISTS).append(WHITESPACE);
        if(!skipSchemaName)
            isb.append(schemaName).append(DOT);
        isb.append(indexName).append(WHITESPACE).append(Keywords.ON)
                .append(WHITESPACE).append(tableName)
                .append(WHITESPACE).append(implodeWithCommaInBKT(indexColumns));
        if(whereExpression != null) {
            isb.append(WHITESPACE).append(Keywords.WHERE).append(WHITESPACE).append(whereExpression);
        }
        isb.append(SEMICOLON);
        return isb.toString();
    }
}
