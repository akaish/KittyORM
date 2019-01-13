
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

package net.akaish.kitty.orm.constraints.column;

import net.akaish.kitty.orm.annotations.column.constraints.PRIMARY_KEY;
import net.akaish.kitty.orm.enums.AscDesc;
import net.akaish.kitty.orm.enums.ConflictClauses;
import net.akaish.kitty.orm.enums.Keywords;

import static net.akaish.kitty.orm.enums.Keywords.AUTOINCREMENT;
import static net.akaish.kitty.orm.util.KittyConstants.EMPTY_STRING;
import static net.akaish.kitty.orm.util.KittyConstants.WHITESPACE;

/**
 * Wrapper for PRIMARY KEY column constraint
 * Created by akaish on 30.04.2018.
 * @author akaish (Denis Bogomolov)
 */
public class PrimaryKeyColumnConstraint {

    protected final String ascDesc;
    protected final String conflictClause;
    protected final String autoincrement;

    public PrimaryKeyColumnConstraint(AscDesc ascDesc, ConflictClauses conflictClause, boolean autoincrement) {
        if(ascDesc == null)
            this.ascDesc = EMPTY_STRING;
        else if(ascDesc.equals(AscDesc.NOT_SET_SKIP_OR_DEFAULT))
            this.ascDesc = EMPTY_STRING;
        else
            this.ascDesc = ascDesc.toString();
        if(conflictClause == null)
            this.conflictClause = EMPTY_STRING;
        else if(conflictClause.equals(ConflictClauses.CONFLICT_CLAUSE_NOT_SET))
            this.conflictClause = EMPTY_STRING;
        else
            this.conflictClause = conflictClause.toString();
        if(!autoincrement)
            this.autoincrement = EMPTY_STRING;
        else
            this.autoincrement = AUTOINCREMENT.toString();
    }

    public PrimaryKeyColumnConstraint(PRIMARY_KEY pkAnnotation) {
        this(pkAnnotation.orderAscDesc(), pkAnnotation.onConflictAction(), pkAnnotation.autoincrement());
    }

    @Override
    public String toString() {
        return new StringBuffer(32)
                .append(Keywords.PRIMARY_KEY).append(WHITESPACE).append(ascDesc).append(conflictClause).append(WHITESPACE).append(autoincrement).toString().trim().replaceAll("\\s+", " ");
    }
}
