
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
 * Created by akaish on 01.05.2018.
 * @author akaish (Denis Bogomolov)
 */

public enum DeferrableOptions {
    DEFERRABLE_INITIALLY_DEFERRED("DEFERRABLE INITIALLY DEFERRED"),
    NOT_DEFERRABLE_INITIALLY_DEFERRED ("NOT DEFERRABLE INITIALLY DEFERRED"),
    NOT_DEFERRABLE_INITIALLY_IMMEDIATE("NOT DEFERRABLE INITIALLY IMMEDIATE"),
    NOT_DEFERRABLE("NOT DEFERRABLE"),
    DEFERRABLE_INITIALLY_IMMEDIATE("DEFERRABLE INITIALLY IMMEDIATE"),
    DEFERRABLE("DEFERRABLE"),

    /**
     * Default option, when on than would be used defined string\numeric\blob literal or signed integer
     */
    NOT_SET_IGNORE_OPTION(" N\\A ");

    private final String sqlText;

    DeferrableOptions(String sqlTextRep) {
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
