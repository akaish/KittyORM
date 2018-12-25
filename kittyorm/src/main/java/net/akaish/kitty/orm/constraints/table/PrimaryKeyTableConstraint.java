
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

import net.akaish.kitty.orm.annotations.table.constraints.PRIMARY_KEY_T;
import net.akaish.kitty.orm.enums.ConflictClauses;
import net.akaish.kitty.orm.enums.Keywords;

import static net.akaish.kitty.orm.util.KittyConstants.EMPTY_STRING;
import static net.akaish.kitty.orm.util.KittyConstants.WHITESPACE;
import static net.akaish.kitty.orm.util.KittyUtils.implodeWithCommaInBKT;

/**
 * Created by akaish on 02.05.2018.
 * @author akaish (Denis Bogomolov)
 */

public class PrimaryKeyTableConstraint extends TableConstraint {

    protected final String onConflict;

    public PrimaryKeyTableConstraint(String constraintName, ConflictClauses conflictClauses, String... columns) {
        super(constraintName, columns);
        if(conflictClauses.equals(ConflictClauses.CONFLICT_CLAUSE_NOT_SET))
            onConflict = EMPTY_STRING;
        else
            onConflict = conflictClauses.toString();
    }

    public PrimaryKeyTableConstraint(PRIMARY_KEY_T pkTAnnotation) {
        this(pkTAnnotation.name().length() == 0 ? null : pkTAnnotation.name(), pkTAnnotation.onConflict(), pkTAnnotation.columns());
    }

    public final String[] getColumns() {
        return columns;
    }

    public String toString() {
        return new StringBuffer(64)
                .append(super.toString())
                .append(WHITESPACE)
                .append(Keywords.PRIMARY_KEY)
                .append(WHITESPACE)
                .append(implodeWithCommaInBKT(columns))
                .append(WHITESPACE)
                .append(onConflict).toString().trim();
    }
}
