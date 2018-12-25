
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

import net.akaish.kitty.orm.annotations.table.constraints.CHECK_T;
import net.akaish.kitty.orm.enums.Keywords;

import static net.akaish.kitty.orm.util.KittyConstants.LEFT_BKT;
import static net.akaish.kitty.orm.util.KittyConstants.RIGHT_BKT;
import static net.akaish.kitty.orm.util.KittyConstants.WHITESPACE;

/**
 * Created by akaish on 02.05.2018.
 * @author akaish (Denis Bogomolov)
 */

public class CheckTableConstraint extends TableConstraint {

    protected final String checkExpression;

    public CheckTableConstraint(String constraintName, String checkExpression) {
        super(constraintName, null);
        this.checkExpression = checkExpression;
    }

    public CheckTableConstraint(CHECK_T checkTAnnotation) {
        this(checkTAnnotation.name().length() == 0 ? null : checkTAnnotation.name(), checkTAnnotation.checkExpression());
    }

    public String toString() {
        return new StringBuffer(64).append(super.toString())
                                            .append(WHITESPACE)
                                            .append(Keywords.CHECK)
                                            .append(WHITESPACE)
                                            .append(LEFT_BKT)
                                            .append(RIGHT_BKT)
                                            .toString();
    }
}
