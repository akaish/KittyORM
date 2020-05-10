
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

package net.akaish.kitty.orm.constraints.table;

import net.akaish.kitty.orm.annotations.table.constraints.TableForeignKey;
import net.akaish.kitty.orm.constraints.ForeignKeyReference;
import net.akaish.kitty.orm.enums.Keywords;
import net.akaish.kitty.orm.util.KittyUtils;

import static net.akaish.kitty.orm.util.KittyConstants.WHITESPACE;

/**
 * Created by akaish on 02.05.2018.
 * @author akaish (Denis Bogomolov)
 */

public class ForeignKeyTableConstraint extends TableConstraint {

    protected final ForeignKeyReference foreignKeyReference;

    public ForeignKeyTableConstraint(String constraintName, ForeignKeyReference foreignKeyReference, String... columns) {
        super(constraintName, columns);
        this.foreignKeyReference = foreignKeyReference;
    }

    public ForeignKeyTableConstraint(TableForeignKey fkTAnnotation) {
        this(fkTAnnotation.name().length() == 0 ? null : fkTAnnotation.name(), new ForeignKeyReference(fkTAnnotation.reference()), fkTAnnotation.columns());
    }

    public String toString() {
        return new StringBuffer(128)
                .append(super.toString())
                .append(WHITESPACE)
                .append(Keywords.FOREIGN_KEY)
                .append(KittyUtils.implodeWithCommaInBKT(columns))
                .append(WHITESPACE)
                .append(foreignKeyReference)
                .toString();
    }
}
