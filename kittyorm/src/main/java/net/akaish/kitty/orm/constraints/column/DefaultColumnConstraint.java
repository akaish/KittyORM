
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

import net.akaish.kitty.orm.annotations.column.constraints.DEFAULT;
import net.akaish.kitty.orm.enums.Keywords;
import net.akaish.kitty.orm.enums.LiteralValues;

import static net.akaish.kitty.orm.util.KittyConstants.LEFT_BKT;
import static net.akaish.kitty.orm.util.KittyConstants.RIGHT_BKT;
import static net.akaish.kitty.orm.util.KittyConstants.WHITESPACE;

/**
 * DEFAULT column constraint wrapper
 * Created by akaish on 30.04.2018.
 * @author akaish (Denis Bogomolov)
 */
public class DefaultColumnConstraint {

    protected final boolean isExpression;
    protected final String defaultText;

    public DefaultColumnConstraint(boolean isExpression, String defaultText) {
        this.isExpression = isExpression;
        this.defaultText = defaultText;
    }

    /**
     * <br> Configurator would parse this annotation in following order:
     *
     * <br>Fetching {@link DEFAULT#expression()}, if not set (default value) than
     * next step, otherwise creating DEFAULT constraint with this expression.
     *
     * <br>Fetching {@link DEFAULT#literalValue()}, if not set (default value)
     * than next step, otherwise creating DEFAULT constraint with this value.
     *
     * <br>Fetching {@link DEFAULT#predefinedLiteralValue()}, if not set
     * (default value) than next step, otherwise using one of predefined literals such as TRUE or
     * CURRENT_DATE defined in {@link LiteralValues}.
     *
     * <br>Fetching {@link DEFAULT#signedInteger()}, if not set than would be
     * created DEFAULT constraint with value 0 (DEFAULT 0)
     *
     * @param defaultAnnotation annotation to process
     */
    public DefaultColumnConstraint(DEFAULT defaultAnnotation) {
        if(defaultAnnotation.expression().length() != 0) {
            isExpression = true;
            defaultText = defaultAnnotation.expression();
        } else {
            isExpression = false;
            if(defaultAnnotation.literalValue().length() != 0) {
                defaultText = defaultAnnotation.literalValue();
            } else if(!defaultAnnotation.predefinedLiteralValue().equals(LiteralValues.NOT_SET_SKIP_THIS_LITERAL)) {
                defaultText = defaultAnnotation.predefinedLiteralValue().toString();
            } else {
                defaultText = Integer.toString(defaultAnnotation.signedInteger());
            }
        }
    }

    @Override
    public String toString() {
        if(isExpression)
            return new StringBuffer(32)
                    .append(Keywords.DEFAULT)
                    .append(WHITESPACE)
                    .append(LEFT_BKT)
                    .append(defaultText)
                    .append(RIGHT_BKT).toString();
        return new StringBuffer(32)
                .append(Keywords.DEFAULT)
                .append(WHITESPACE)
                .append(defaultText).toString();
    }
}
