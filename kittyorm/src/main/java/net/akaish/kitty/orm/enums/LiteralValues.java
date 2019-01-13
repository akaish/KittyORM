
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

package net.akaish.kitty.orm.enums;

/**
 * Enumeration with predefined literal values for use, for example, in generating DEFAULT column constraint
 * Created by akaish on 30.04.2018.
 * @author akaish (Denis Bogomolov)
 */
public enum LiteralValues {
    NULL(" NULL "),
    TRUE(" TRUE "),
    FALSE(" FALSE "),
    CURRENT_TIME(" CURRENT_TIME "),
    CURRENT_DATE(" CURRENT_DATE "),
    CURRENT_TIMESTAMP(" CURRENT_TIMESTAMP "),

    /**
     * Default option, when on than would be used defined string\numeric\blob literal or signed integer
     */
    NOT_SET_SKIP_THIS_LITERAL(" N\\A ");

    private final String sqlText;

    LiteralValues(String sqlTextRep) {
        sqlText = sqlTextRep;
    }

    public boolean equalsName(String sqlText) {
        // (otherName == null) check is not needed because name.equals(null) returns false
        return this.sqlText.equals(sqlText);
    }

    @Override
    public String toString() {
        return this.sqlText;
    }
}
