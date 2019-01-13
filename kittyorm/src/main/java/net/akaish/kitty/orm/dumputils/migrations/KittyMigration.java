
/*
 * ---
 *
 *  Copyright (c) 2018 Denis Bogomolov (akaish)
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

package net.akaish.kitty.orm.dumputils.migrations;

import net.akaish.kitty.orm.query.KittySQLiteQuery;

import java.util.LinkedList;
import java.util.List;

/**
 * Migration class
 * Created by akaish on 05.03.18.
 * @author akaish (Denis Bogomolov)
 */
public class KittyMigration {
    protected final int minVersionLower;
    protected final int minVersionUpper;
    protected final int maxVersionLower;
    protected final int maxVersionUpper;
    protected final LinkedList<KittySQLiteQuery> migrationScript;
    protected final String databaseName;

    public KittyMigration(int minVersionLower, int minVersionUpper, int maxVersionLower,
                          int maxVersionUpper, LinkedList<KittySQLiteQuery> migrationScript, String databaseName) {
        this.minVersionLower = minVersionLower;
        this.minVersionUpper = minVersionUpper;
        this.maxVersionLower = maxVersionLower;
        this.maxVersionUpper = maxVersionUpper;
        this.migrationScript = migrationScript;
        this.databaseName = databaseName;
    }

    public String getDatabaseName() {
        return databaseName;
    }

    public int getMinVersionLower() {
        return minVersionLower;
    }

    public int getMinVersionUpper() {
        return minVersionUpper;
    }

    public int getMaxVersionLower() {
        return maxVersionLower;
    }

    public int getMaxVersionUpper() {
        return maxVersionUpper;
    }

    public LinkedList<KittySQLiteQuery> getMigrationScript() {
        return migrationScript;
    }
}
