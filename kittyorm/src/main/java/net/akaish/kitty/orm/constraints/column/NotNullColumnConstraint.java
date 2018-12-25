
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

package net.akaish.kitty.orm.constraints.column;

import net.akaish.kitty.orm.annotations.column.constraints.NOT_NULL;
import net.akaish.kitty.orm.enums.ConflictClauses;

import static net.akaish.kitty.orm.enums.Keywords.NOT;
import static net.akaish.kitty.orm.enums.Keywords.NULL;
import static net.akaish.kitty.orm.util.KittyConstants.WHITESPACE;

/**
 * Wrapper for NOT NULL column data constraint.
 * Created by akaish on 30.04.2018.
 * @author akaish (Denis Bogomolov)
 */

public class NotNullColumnConstraint {

    protected final String onConflictAction;

    public NotNullColumnConstraint(ConflictClauses onConflictAction) {
        if(onConflictAction.equals(ConflictClauses.CONFLICT_CLAUSE_NOT_SET)) {
            this.onConflictAction = null;
        } else {
            this.onConflictAction = onConflictAction.toString();
        }
    }

    public NotNullColumnConstraint(NOT_NULL nnAnnotation) {
        this(nnAnnotation.onConflict());
    }

    @Override
    public String toString() {
        if(onConflictAction == null)
            return new StringBuffer(8).append(NOT).append(WHITESPACE).append(NULL).toString();
        return new StringBuffer(32)
                .append(NOT).append(WHITESPACE).append(NULL).append(WHITESPACE).append(onConflictAction).toString().trim().replaceAll("\\s+", " ");
    }
}
