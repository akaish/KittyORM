
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

import java.text.MessageFormat;

/**
 * Created by akaish on 10.10.18.
 * @author akaish (Denis Bogomolov)
 */
public class KittyDBVersionMismatchException extends KittyRuntimeException {

    private static final String DB_ERR_EXC_MESSAGE = "Database versions mismatch, old database version ({0}) is higher than new (current) database version ({1}) for schema {2}!";

    public KittyDBVersionMismatchException(int oldDbVerCode, int newDbVerCode, String schemaName) {
        super(MessageFormat.format(DB_ERR_EXC_MESSAGE, oldDbVerCode, newDbVerCode, schemaName));
    }
}
