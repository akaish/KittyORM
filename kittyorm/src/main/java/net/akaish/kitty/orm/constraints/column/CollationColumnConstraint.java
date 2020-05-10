
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

package net.akaish.kitty.orm.constraints.column;

import net.akaish.kitty.orm.annotations.column.constraints.Collate;
import net.akaish.kitty.orm.enums.BuiltInCollations;
import net.akaish.kitty.orm.enums.Keywords;

import static net.akaish.kitty.orm.util.KittyConstants.WHITESPACE;

/**
 * Wrapper for collation constraint
 * Created by akaish on 30.04.2018.
 * @author akaish (Denis Bogomolov)
 */

public class CollationColumnConstraint {
    protected final String collationFunctionName;

    public CollationColumnConstraint(BuiltInCollations collation) {
        this.collationFunctionName = collation.toString();
    }

    public CollationColumnConstraint(Collate collationAnnotation) {
        this.collationFunctionName = collationAnnotation.collation().toString();
    }

    @Override public String toString() {
        return new StringBuffer(32).append(Keywords.COLLATE)
                .append(WHITESPACE).append(collationFunctionName).toString();
    }
}
