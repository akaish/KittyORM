
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

package net.akaish.kitty.orm.exceptions;

import net.akaish.kitty.orm.annotations.column.KITTY_COLUMN_ACCEPTED_VALUES;

import static net.akaish.kitty.orm.util.KittyConstants.DOT;

/**
 * Runtime exception that should be thrown if provided data not in defined by
 * {@link KITTY_COLUMN_ACCEPTED_VALUES#acceptedValuesByte()} (or other) values
 * Created by akaish on 07.02.18.
 * @author akaish (Denis Bogomolov)
 */
public class KittyRestrictedValueException extends RuntimeException {

    private static final String ERROR = "Error, provided values restricted by ";
    private static final String WSP_LBKT = " (";
    private static final String RBKT_EXCLP = ")!";

    public KittyRestrictedValueException(String methodName, Class callerClass, String fieldName) {
        super(
                new StringBuffer(64)
                        .append(ERROR)
                        .append(methodName)
                        .append(WSP_LBKT)
                        .append(callerClass.getCanonicalName())
                        .append(DOT)
                        .append(fieldName)
                        .append(RBKT_EXCLP)
                        .toString()
        );
    }
}
