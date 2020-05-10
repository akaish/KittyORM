
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

package net.akaish.kitty.orm.dumputils.scripts;

import net.akaish.kitty.orm.exceptions.KittyRuntimeException;
import net.akaish.kitty.orm.query.KittySQLiteQuery;

import java.util.LinkedList;

/**
 * Abstract class for dumped sqlite scripts
 * Created by akaish on 06.03.18.
 * @author akaish (Denis Bogomolov)
 */
public abstract class KittySQLiteDumpScript implements KittySQLiteScript {

    protected LinkedList<KittySQLiteQuery> sqlScript;

    public KittySQLiteDumpScript(Object... params) {
        sqlScript = readFromDump(params);
    }

    /**
     * Saves input string representation of SQLite sql script to specified in child location
     * @param sqlScript sqlite script to save
     */
    public abstract void saveToDump(LinkedList<KittySQLiteQuery> sqlScript);

    /**
     * Reads sql dump from specified in child to object
     * @throws KittyRuntimeException if errors
     * @param params misc parameters
     * @return string with sql script or null if specified location has no data
     */
    public abstract LinkedList<KittySQLiteQuery> readFromDump(Object... params);

    @Override
    public LinkedList<KittySQLiteQuery> getSqlScript() {
        return sqlScript;
    }
}
