
/*
 * ---
 *
 *  Copyright (c) 2019 Denis Bogomolov (akaish)
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

import static java.text.MessageFormat.format;
import static net.akaish.kitty.orm.util.KittyStringUtils.versionsArrayString;

public class KittyExternalDBHasNotSupportedUserVersionException extends KittyRuntimeException {

    protected final static String EXTERNAL_DB_VERSION_MISMATCH = "Requested database [{0}] has user version {1} but this KittyORM schema supports only following versions: {2}";

    public KittyExternalDBHasNotSupportedUserVersionException(String databaseFilepath, int databaseVersion, int[] supportedExternalDatabaseVersionNumbers) {
        super(format(
                EXTERNAL_DB_VERSION_MISMATCH,
                databaseFilepath,
                databaseVersion,
                versionsArrayString(supportedExternalDatabaseVersionNumbers)));
    }

}
