
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

package net.akaish.kitty.orm.query;

import net.akaish.kitty.orm.util.KittyUtils;

import static net.akaish.kitty.orm.util.KittyConstants.WHITESPACE;

/**
 * Created by akaish on 07.02.18.
 * @author akaish (Denis Bogomolov)
 */

public class KittySQLiteQuery {
    private final String sql;
    private String[] conditionValues;

    public KittySQLiteQuery(String sql, String[] conditionValues) {
        this.sql = sql;
        this.conditionValues = conditionValues;
    }

    public String getSql() {
        return sql;
    }

    public String[] getConditionValues() {
        return conditionValues;
    }

    @Override
    public String toString() {
        StringBuffer sb = new StringBuffer(sql);
        if(conditionValues != null) {
            sb.append(WHITESPACE);
            sb.append(KittyUtils.implodeWithCommaInBKT(conditionValues));
        }
        return sb.toString();
    }
}
