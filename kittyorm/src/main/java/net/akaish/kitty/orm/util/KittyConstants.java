
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

package net.akaish.kitty.orm.util;

/**
 * Class for storing constants useful in query creation and other routine.
 * Created by akaish on 07.02.18.
 * @author akaish (Denis Bogomolov)
 */
public class KittyConstants {

    // CHAR SECTION
    public static final char WHITESPACE = ' ';
    public static final char QUOTE = '\'';
    public static final char DOUBLE_QUOTE = '"';
    public static final char LEFT_BKT = '(';
    public static final char RIGHT_BKT = ')';
    public static final char COMMA = ',';
    public static final char QSIGN = '?';
    public static final char SEMICOLON = ';';
    public static final char DOT = '.';
    public static final char UNDERSCORE = '_';

    // STRING SECTION SQL
    public static final String COMMA_SEPARATOR = ", ";
    public static final String EQUAL_QSIGN = " = ?";
    public static final String EQUAL_SIGN = " = ";
    public static final String WHERE = "WHERE {0}";
    public static final String ORDER_BY = "ORDER BY";
    public static final String GROUP_BY = "GROUP BY";
    public static final String LIMIT = "LIMIT";
    public static final String OFFSET = "OFFSET";
    public static final String NULL = "NULL";

    // STRING SECTION MISC
    public static final String MODEL_END = "Model";
    public static final String RECORD_END = "Record";
    public static final String EMPTY_STRING = "";
    public static final String SQL_COMMENT_START = "--";
    public static final String ROWID = "rowid";
    public static final String TYPE_NAME_COLUMN = "type";


    public static final String DEFAULT_LOG_TAG = "KittySQLiteORM";
    public static final String DEFAULT_MODEL_MAPPER_IMPLEMENTATIONS_PACKAGE = "net.akaish.kittyimp.schema";

    public static final String ZERO_LENGTH_STRING = "";

}
