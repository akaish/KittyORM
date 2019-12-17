
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

package net.akaish.kitty.orm.query;

import net.akaish.kitty.orm.enums.AscDesc;
import net.akaish.kitty.orm.exceptions.KittyRuntimeException;
import net.akaish.kitty.orm.query.conditions.SQLiteCondition;
import net.akaish.kitty.orm.util.KittyUtils;

import java.text.MessageFormat;

import static net.akaish.kitty.orm.enums.AscDesc.ASCENDING;
import static net.akaish.kitty.orm.enums.AscDesc.DESCENDING;
import static net.akaish.kitty.orm.util.KittyConstants.COMMA_SEPARATOR;
import static net.akaish.kitty.orm.util.KittyConstants.GROUP_BY;
import static net.akaish.kitty.orm.util.KittyConstants.LIMIT;
import static net.akaish.kitty.orm.util.KittyConstants.OFFSET;
import static net.akaish.kitty.orm.util.KittyConstants.ORDER_BY;
import static net.akaish.kitty.orm.util.KittyConstants.WHERE;
import static net.akaish.kitty.orm.util.KittyConstants.WHITESPACE;

/**
 * Simple builder to build wrappers for common queries used in KittyORM.
 * Created by akaish on 06.02.18.
 * @author akaish (Denis Bogomolov)
 */
public class KittyQueryBuilder {

    private final static String QWXCEPTION = "Illegal query type passed: {0}!";
    private static final String AME_NO_SUM_COLUMN = "No sum column defined!";

    private boolean isOrderBy = false;
    private String[] orderColumns = null;
    private AscDesc orderByAscDesc = ASCENDING;

    private boolean isGroupBy = false;
    private String[] groupByColumns = null;

    private boolean isLimit = false;
    private Long limit = null;

    private boolean isOffset = false;
    private Long offset = null;

    private boolean isWhereClause = false;
    private SQLiteCondition whereClause = null;

    private String sumColumn = null;

    private String schemaName = null;

    public final static int SB_SQL_SIZE = 256;
    private final int sbSize;

    public enum QUERY_TYPE {
        DELETE, SELECT, SELECT_COUNT, SELECT_SUM, SELECT_TABLE_NAMES
    }

    private final QUERY_TYPE queryType;
    private final BaseKittyQuery query;
    private final String tableName;

    public KittyQueryBuilder(QUERY_TYPE queryType, String tableName) {
        this(queryType, tableName, SB_SQL_SIZE);
    }

    public KittyQueryBuilder(QUERY_TYPE queryType, String tableName, int sbSize) {
        this.sbSize = sbSize;
        this.tableName = tableName;
        this.queryType = queryType;
        switch (queryType) {
            case DELETE:
                query = new DeleteQuery(tableName);
                break;
            case SELECT:
                query = new SelectQuery(tableName);
                break;
            case SELECT_COUNT:
                query = new SelectCountQuery(tableName);
                break;
            case SELECT_SUM:
                query = new SelectSumQuery(tableName);
                break;
            case SELECT_TABLE_NAMES:
                query = new SelectTableNamesQuery(tableName);
                break;
            default:
                throw new KittyRuntimeException(MessageFormat.format(QWXCEPTION, queryType.toString()));
        }
    }

    /**
     * Sets sum column name for {@link SelectCountQuery} : {@link QUERY_TYPE#SELECT_SUM} (only). Values set via this method in other
     * queries would be ignored.
     * @param sumColumn column on which sum operation should be performed.
     * @return
     */
    public KittyQueryBuilder setSumColumn(String sumColumn) {
        this.sumColumn = sumColumn;
        return this;
    }

    /**
     * Sets schema name for {@link SelectTableNamesQuery} : {@link QUERY_TYPE#SELECT_TABLE_NAMES} (only). Values set via this method in
     * other queries would be ignored.
     * @param schemaName schema name from what should table names to be retrieved.
     * @return
     */
    public KittyQueryBuilder setSchemaName(String schemaName) {
        this.schemaName = schemaName;
        return this;
    }

    public KittyQueryBuilder setOrderBy(String column, AscDesc asc_desc) {
        return setOrderBy(asc_desc, column);
    }

    public KittyQueryBuilder setOrderBy(AscDesc ascDesc, String... columns) {
        this.orderByAscDesc = ascDesc;
        this.orderColumns = columns;
        this.isOrderBy = true;
        return this;
    }

    public KittyQueryBuilder setQueryParameters(QueryParameters queryParameters) {
        if(queryParameters == null)
            return this;
        if(queryParameters.getLimit() != null)
            this.setLimit(queryParameters.getLimit());
        if(queryParameters.getGroupByColumns() != null)
            this.setGroupBy(queryParameters.getGroupByColumns());
        if(queryParameters.getOffset() != null)
            this.setOffset(queryParameters.getOffset());
        if(queryParameters.getOrderByColumns() != null) {
            this.setOrderBy(queryParameters.getOrderAscDesc(), queryParameters.getOrderByColumns());
        }
        return this;
    }

    public KittyQueryBuilder setRowIDSupport(boolean rowIdOn) {
        if(rowIdOn && (query instanceof SelectQuery)) {
            ((SelectQuery) query).setRowIdSupport(rowIdOn);
        }
        return this;
    }

    public KittyQueryBuilder setGroupBy(String... columns) {
        groupByColumns = columns;
        isGroupBy = true;
        return this;
    }

    public KittyQueryBuilder setLimit(Long limit) {
        isLimit = true;
        this.limit = limit;
        return this;
    }

    public KittyQueryBuilder setOffset(Long offset) {
        isOffset = true;
        this.offset = offset;
        return this;
    }

    public KittyQueryBuilder setWhereClause(SQLiteCondition condition) {
        if(condition == null)
            return this;
        whereClause = condition;
        isWhereClause = true;
        return this;
    }

    public KittySQLiteQuery buildSQLQuery() {
        StringBuffer sqlSB = new StringBuffer(sbSize);
        if(query instanceof SelectSumQuery) {
            if (sumColumn == null)
                throw new KittyRuntimeException(AME_NO_SUM_COLUMN);
            else
                ((SelectSumQuery) query).setSumColumn(sumColumn);
        }
        if(query instanceof SelectTableNamesQuery) {
            if (schemaName == null)
                throw new KittyRuntimeException(SelectTableNamesQuery.IA_SCHEMA_NOT_SET);
            else {
                ((SelectTableNamesQuery) query).setSchemaName(schemaName);
                sqlSB.append(query.getQueryStart());
                return new KittySQLiteQuery(sqlSB.toString(), null);
            }
        }
        sqlSB.append(query.getQueryStart());
        if(isWhereClause) {
            sqlSB.append(WHITESPACE);
            sqlSB.append(MessageFormat.format(WHERE, whereClause.getCondition()));
        }
        if(isOrderBy) {
            sqlSB.append(WHITESPACE);
            sqlSB.append(ORDER_BY);
            sqlSB.append(WHITESPACE);
            sqlSB.append(KittyUtils.implode(orderColumns, COMMA_SEPARATOR));
            sqlSB.append(WHITESPACE);
            switch(orderByAscDesc) {
                case ASCENDING:
                    sqlSB.append(ASCENDING.toString());
                    break;
                case DESCENDING:
                    sqlSB.append(DESCENDING.toString());
                    break;
                default:
                    sqlSB.append(ASCENDING.toString());
                    break;
            }
        }
        if(isGroupBy) {
            sqlSB.append(WHITESPACE);
            sqlSB.append(GROUP_BY);
            sqlSB.append(WHITESPACE);
            sqlSB.append(KittyUtils.implode(groupByColumns, COMMA_SEPARATOR));
        }
        if(isLimit) {
            sqlSB.append(WHITESPACE);
            sqlSB.append(LIMIT);
            sqlSB.append(WHITESPACE);
            sqlSB.append(Long.toString(limit));
        }
        if (isOffset) {
            sqlSB.append(WHITESPACE);
            sqlSB.append(OFFSET);
            sqlSB.append(WHITESPACE);
            sqlSB.append(Long.toString(offset));
        }
        if(isWhereClause) {
            return new KittySQLiteQuery(sqlSB.toString(), whereClause.getValues());
        } else {
            return new KittySQLiteQuery(sqlSB.toString(), null);
        }
    }
}
