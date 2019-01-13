
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

/**
 * Abstract class for migration factories
 * Created by akaish on 05.03.18.
 * @author akaish (Denis Bogomolov)
 */
public abstract class KittyMigrationFactory {

    protected final String databaseName;
    protected final String logTag;
    protected final boolean logOn;

    protected int oldVersionFilter = -1;
    protected int currentVersionFilter = -1;

    protected static String IA_FILTER = "oldVersion and currentVersion can't be negative or equal each other as well as oldVersion can't be higher than currentVersion";

    public KittyMigrationFactory(String databaseName, String logTag, boolean logOn) {
        this.databaseName = databaseName;
        this.logTag = logTag;
        this.logOn = logOn;
    }

    /**
     * Sets filter. If migration's minLower is lower than oldVersion or currentVersion is higher, than
     * maxUpper, it would be skipped.
     * @param oldVersion
     * @param currentVersion
     */
    public void setVersionFilter(int oldVersion, int currentVersion) {
        if(oldVersion < 0 || currentVersion < 0 || oldVersion >= currentVersion)
            throw new IllegalArgumentException(IA_FILTER);
        this.oldVersionFilter = oldVersion;
        this.currentVersionFilter = currentVersion;
    }

    /**
     * Returns new {@link KittyMigration} instance or null if errors
     * Use logging on if you need to debug it
     * @return
     */
    public abstract KittyMigration newMigration();

}
