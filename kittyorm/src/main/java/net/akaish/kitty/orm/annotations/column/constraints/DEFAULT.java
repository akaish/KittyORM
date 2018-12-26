
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

package net.akaish.kitty.orm.annotations.column.constraints;

import net.akaish.kitty.orm.enums.LiteralValues;

import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import static net.akaish.kitty.orm.util.KittyConstants.ZERO_LENGTH_STRING;


/**
 * Annotation for column constraint DEFAULT.
 * <br> Configurator would parse this annotation in following order:
 * <br>Fetching {@link #expression()}, if not set (default value) than next step, otherwise creating DEFAULT constraint with this expression.
 * <br>Fetching {@link #literalValue()}, if not set (default value) than next step, otherwise creating DEFAULT constraint with this value.
 * <br>Fetching {@link #predefinedLiteralValue()}, if not set (default value) than next step, otherwise using one of predefined literals such as TRUE or CURRENT_DATE defined in
 * {@link LiteralValues}.
 * <br>Fetching {@link #signedInteger()}, if not set than would be created DEFAULT constraint with value 0 (DEFAULT 0)
 * Created by akaish on 30.04.2018.
 * @author akaish (Denis Bogomolov)
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
@Inherited
public @interface DEFAULT {

    /**
     * Predefined literal value, see {@link LiteralValues}
     * @return predefined literal or {@link LiteralValues#NOT_SET_SKIP_THIS_LITERAL}
     * if this field should be treated as null (not set)
     */
    LiteralValues predefinedLiteralValue() default LiteralValues.NOT_SET_SKIP_THIS_LITERAL;

    /**
     * Any literal value that should be used in DEFAULT constraint.
     * @return literal value or {@link net.akaish.kitty.orm.util.KittyConstants#ZERO_LENGTH_STRING} (if not set)
     */
    String literalValue() default ZERO_LENGTH_STRING;

    /**
     * Any expression to be used.
     * @return expression to be used with DEFAULT constraint or
     */
    String expression() default ZERO_LENGTH_STRING;

    /**
     * Signed int value for DEFAULT constraint.
     * @return Signed int value for DEFAULT constraint. default value is 0 (zero).
     */
    int signedInteger() default 0;

}
