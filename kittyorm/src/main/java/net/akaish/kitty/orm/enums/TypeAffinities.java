
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

package net.akaish.kitty.orm.enums;

/**
 * Created by akaish on 30.04.2018.
 * @author akaish (Denis Bogomolov)
 */
public enum TypeAffinities {
    INTEGER("INTEGER"),
    NONE("NONE"),
    REAL("REAL"),
    NUMERIC("NUMERIC"),
    TEXT("TEXT"),
    /**
     * Use {@link TypeAffinities#NONE} instead, cause blob is data type, not  affinity, I
     * just added it cause it is really painful for me that for each type affinity corresponding
     * with data type there is data type called exactly like type affinity (e.g. NUMERIC data type
     * for NUMERIC affinity type), but no data type NONE for type affinity NONE exists.
     */
    BLOB("BLOB"),
    /**
     * Default option, when on, boolean, byte, integer, long and short primitives would be auto mapped to INTEGER
     * byte[] to NONE, float and double to REAL and String to TEXT
     */
    NOT_SET_USE_DEFAULT_MAPPING(" N\\A ");

    private final String sqlText;

    TypeAffinities(String sqlTextRep) { sqlText = sqlTextRep; }

    public boolean equalsName(String sqlText) {
        // (otherName == null) check is not needed because name.equals(null) returns false
        return this.sqlText.equals(sqlText);
    }

    @Override public String toString() { return this.sqlText; }
}
