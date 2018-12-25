
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

package net.akaish.kitty.orm.constraints.table;

import static net.akaish.kitty.orm.enums.Keywords.CONSTRAINT;
import static net.akaish.kitty.orm.util.KittyConstants.UNDERSCORE;
import static net.akaish.kitty.orm.util.KittyConstants.WHITESPACE;
import static net.akaish.kitty.orm.util.KittyUtils.implode;

/**
 * Created by akaish on 02.05.2018.
 * @author akaish (Denis Bogomolov)
 */
public abstract class TableConstraint {

    protected final String[] columns;
    protected final String constraintName;

    protected int constraintOrder = 0;
    protected String tableName = null;

    public TableConstraint(String constraintName, String[] columns) {
        this.constraintName = constraintName;
        this.columns = columns;
    }

    public final void setCounterForAutoGenName(int counter) {
        constraintOrder = counter;
    }

    public final void setTableNameForAutoGenName(String tableName) {
        this.tableName = tableName;
    }


    protected final String generateConstraintName() {
        StringBuffer out = new StringBuffer(64);
        return out.append(implode(columns, UNDERSCORE))
                .append(UNDERSCORE)
                .append(getClass().getName())
                .append(UNDERSCORE)
                .append(constraintOrder).toString();
    }

    @Override
    public String toString() {
        return new StringBuffer(32).append(CONSTRAINT)
                .append(WHITESPACE)
                .append(constraintName == null ? generateConstraintName() : constraintName)
                .toString().trim();
    }
}
