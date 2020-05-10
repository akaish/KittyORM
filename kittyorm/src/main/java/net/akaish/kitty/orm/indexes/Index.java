
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

package net.akaish.kitty.orm.indexes;

import net.akaish.kitty.orm.annotations.column.SingleColumnIndex;
import net.akaish.kitty.orm.annotations.table.index.TableIndex;
import net.akaish.kitty.orm.annotations.table.index.TableIndexEntree;
import net.akaish.kitty.orm.enums.AscDesc;
import net.akaish.kitty.orm.enums.Keywords;
import net.akaish.kitty.orm.exceptions.KittyRuntimeException;

import java.text.MessageFormat;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static net.akaish.kitty.orm.util.KittyConstants.DOT;
import static net.akaish.kitty.orm.util.KittyConstants.SEMICOLON;
import static net.akaish.kitty.orm.util.KittyConstants.WHITESPACE;
import static net.akaish.kitty.orm.util.KittyNamingUtils.generateIndexName;
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
    protected final IndexEntry[] indexColumns;
    protected final String whereExpression;
    protected final HashSet<IndexEntry> uniqueIndexColumnEntries;

    protected static final String AME_INDEX_BAD_COLUMNS = "Index columns for index {0} at {1}.{2} has duplicate column names!";

    public Index(boolean unique, boolean ifNotExists, String schemaName, String indexName,
                 String tableName, IndexEntry[] indexColumns, String whereExpression) {
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
        uniqueIndexColumnEntries = new HashSet<>();
        uniqueIndexColumnEntries.addAll(Arrays.asList(indexColumns));
        if(uniqueIndexColumnEntries.size() != indexColumns.length)
            throw new KittyRuntimeException(MessageFormat.format(AME_INDEX_BAD_COLUMNS, this.indexName, schemaName, tableName));
    }

    public Index(TableIndex tableIndex, String schemaName, String tableName) {
        this(tableIndex.unique(), tableIndex.ifNotExists(), schemaName, tableIndex.name().length() == 0 ? null : tableIndex.name(),
                tableName, annoToObject(tableIndex.columns()), tableIndex.whereExpression().length() == 0 ? null : tableIndex.whereExpression());
    }

    public Index(SingleColumnIndex index, String indexColumn, String schemaName, String tableName) {
        this(index.unique(), index.ifNotExists(), schemaName, index.name().length() == 0 ? null : index.name(),
                tableName, new IndexEntry[]{new IndexEntry(indexColumn, index.order())}, index.whereExpression().length() == 0 ? null : index.whereExpression());
    }

    private final static IndexEntry[] annoToObject(TableIndexEntree[] indexColumns) {
        IndexEntry[] out = new IndexEntry[indexColumns.length];
        for(int i = 0; i < out.length; i++)
            out[i] = new IndexEntry(indexColumns[i].name(), indexColumns[i].sortingOrder());
        return out;
    }

    public final Set<String> getIndexColumsSet() {
        Set<String> out = new HashSet<>();
        for(IndexEntry ie : indexColumns)
            out.add(ie.columnName);
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

    public static class IndexEntry {
        public final String columnName;
        final AscDesc sortingOrder;

        IndexEntry(String columnName, AscDesc sortingOrder) {
            this.columnName = columnName;
            this.sortingOrder = sortingOrder;
        }

        IndexEntry(TableIndexEntree indexEntry) {
            this.columnName = indexEntry.name();
            this.sortingOrder = indexEntry.sortingOrder();
        }

        @Override
        public String toString() {
            StringBuilder sb = new StringBuilder();
            sb.append(columnName);
            if(sortingOrder != AscDesc.NOT_SET_SKIP_OR_DEFAULT)
                sb.append(WHITESPACE).append(sortingOrder.toString());
            return sb.toString();
        }
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
        List indexColumnsToImplode = Arrays.asList(indexColumns);
        isb.append(indexName).append(WHITESPACE).append(Keywords.ON)
                .append(WHITESPACE).append(tableName)
                .append(WHITESPACE).append(implodeWithCommaInBKT(indexColumnsToImplode));
        if(whereExpression != null) {
            isb.append(WHITESPACE).append(Keywords.WHERE).append(WHITESPACE).append(whereExpression);
        }
        isb.append(SEMICOLON);
        return isb.toString();
    }
}
