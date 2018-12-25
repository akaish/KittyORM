
/*
 * ---
 *
 *  Copyright (c) 2018 Denis Bogomolov (akaish)
 *
 * This work is licensed under a
 * Creative Commons Attribution-NonCommercial-NoDerivatives 4.0 International License (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://creativecommons.org/licenses/by-nc-nd/4.0/
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
public enum OnUpdateDeleteActions {
    CASCADE("CASCADE"),
    NO_ACTION("NO ACTION"),
    RESTRICT("RESTRICT"),
    SET_NULL("SET NULL"),
    SET_DEFAULT("SET DEFAULT"),

    NOT_SET_SKIP_THIS_FIELD("N\\A");

    private final String sqltext;

    OnUpdateDeleteActions(String sqlText) {
        this.sqltext = sqlText;
    }

    public boolean equalsName(String sqlText) {
        // (otherName == null) check is not needed because name.equals(null) returns false
        return sqltext.equals(sqlText);
    }

    @Override
    public String toString() {
        return this.sqltext;
    }
}
