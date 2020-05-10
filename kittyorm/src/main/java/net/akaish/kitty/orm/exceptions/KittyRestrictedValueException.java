
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

package net.akaish.kitty.orm.exceptions;

import net.akaish.kitty.orm.annotations.column.AcceptValues;

import static net.akaish.kitty.orm.util.KittyConstants.DOT;

/**
 * Runtime exception that should be thrown if provided data not in defined by
 * {@link AcceptValues#bytes()} (or other) values
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
