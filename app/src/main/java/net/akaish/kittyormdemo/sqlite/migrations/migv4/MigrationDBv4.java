
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
 * This file is a part of KittyORM project (KittyORM Demo), more information at
 * https://akaish.github.io/KittyORMPages/license/
 *
 * ---
 */

package net.akaish.kittyormdemo.sqlite.migrations.migv4;

import android.content.Context;

import net.akaish.kitty.orm.annotations.KittyDatabase;
import net.akaish.kitty.orm.annotations.KittyDatabaseHelper;
import net.akaish.kitty.orm.annotations.KittyDatabaseRegistry;
import net.akaish.kitty.orm.annotations.RegistryPair;
import net.akaish.kitty.orm.dumputils.scripts.KittySQLiteDumpScript;
import net.akaish.kitty.orm.query.KittySQLiteQuery;

import java.util.LinkedList;

/**
 * Created by akaish on 03.10.18.
 * @author akaish (Denis Bogomolov)
 */
@KittyDatabase(
        isLoggingOn = false,
        isProductionOn = true,
        name = "mig",
        version = 4,
        logTag = MigrationDBv4.LTAG
)
@KittyDatabaseRegistry(
        domainPairs = {
                @RegistryPair(model = net.akaish.kittyormdemo.sqlite.migrations.migv4.MigTwoModel.class),
                @RegistryPair(model = net.akaish.kittyormdemo.sqlite.migrations.migv4.MigThreeModel.class),
                @RegistryPair(model = net.akaish.kittyormdemo.sqlite.migrations.migv4.MigFourModel.class)
        }
)
@KittyDatabaseHelper(
        onUpgradeBehavior = KittyDatabaseHelper.UpgradeBehavior.USE_FILE_MIGRATIONS
//        migrationScriptsPath = KittyNamingUtils.INTERNAL_MEM_URI_START + "kittysqliteorm/mig/version_migrations",
//        createScriptPath =  KittyNamingUtils.INTERNAL_MEM_URI_START + "kittysqliteorm/mig/mig-v-4-create.sql",
//        dropScriptPath = KittyNamingUtils.INTERNAL_MEM_URI_START + "kittysqliteorm/mig/mig-v-4-drop.sql",
//        afterMigrateScriptPath = KittyNamingUtils.INTERNAL_MEM_URI_START + "kittysqliteorm/mig/mig-v-4-after_create.sql",
//        afterCreateScriptPath = KittyNamingUtils.INTERNAL_MEM_URI_START + "kittysqliteorm/mig/mig-v-4-after_migrate.sql"
)
public class MigrationDBv4 extends net.akaish.kitty.orm.KittyDatabase {

    public static final String LTAG = "MIGv4";

    /**
     * KittyORM main database class that represents bootstrap and holder for all related with database
     * components.
     *
     * @param ctx
     */
    public MigrationDBv4(Context ctx) {
        super(ctx);
    }

    //@Override
    public KittySQLiteDumpScript afterCreateScriptS() {
        return new KittySQLiteDumpScript() {
            @Override
            public void saveToDump(LinkedList<KittySQLiteQuery> sqlScript) {
                throw new RuntimeException("Not supported");
            }

            @Override
            public LinkedList<KittySQLiteQuery> readFromDump(Object... params) {
                LinkedList<KittySQLiteQuery> out = new LinkedList<>();
                out.addLast(new KittySQLiteQuery("INSERT INTO mig_three (new_sv_name, random_long) VALUES ('AFTER CREATE SCRIPT DB SCRIPT', 0);", null));
                out.addLast(new KittySQLiteQuery("INSERT INTO mig_three (new_sv_name, random_long) VALUES ('AFTER CREATE SCRIPT DB SCRIPT', 1);", null));
                out.addLast(new KittySQLiteQuery("INSERT INTO mig_three (new_sv_name, random_long) VALUES ('AFTER CREATE SCRIPT DB SCRIPT', 10);", null));
                out.addLast(new KittySQLiteQuery("INSERT INTO mig_three (new_sv_name, random_long) VALUES ('AFTER CREATE SCRIPT DB SCRIPT', 11);", null));
                out.addLast(new KittySQLiteQuery("INSERT INTO mig_three (new_sv_name, random_long) VALUES ('AFTER CREATE SCRIPT DB SCRIPT', 100);", null));
                out.addLast(new KittySQLiteQuery("INSERT INTO mig_three (new_sv_name, random_long) VALUES ('AFTER CREATE SCRIPT DB SCRIPT', 101);", null));
                out.addLast(new KittySQLiteQuery("INSERT INTO mig_three (new_sv_name, random_long) VALUES ('AFTER CREATE SCRIPT DB SCRIPT', 111);", null));
                out.addLast(new KittySQLiteQuery("INSERT INTO mig_three (new_sv_name, random_long) VALUES ('AFTER CREATE SCRIPT DB SCRIPT', 1000);", null));
                out.addLast(new KittySQLiteQuery("INSERT INTO mig_three (new_sv_name, random_long) VALUES ('AFTER CREATE SCRIPT DB SCRIPT', 1001);", null));
                return out;
            }
        };
    }

    //@Override
    public KittySQLiteDumpScript afterMigrateScriptS() {
        return new KittySQLiteDumpScript() {
            @Override
            public void saveToDump(LinkedList<KittySQLiteQuery> sqlScript) {
                throw new RuntimeException("Not supported");
            }

            @Override
            public LinkedList<KittySQLiteQuery> readFromDump(Object... params) {
                LinkedList<KittySQLiteQuery> out = new LinkedList<>();
                out.addLast(new KittySQLiteQuery("INSERT INTO mig_three (new_sv_name, random_long) VALUES ('AFTER MIGRATE SCRIPT DB SCRIPT', 0);", null));
                out.addLast(new KittySQLiteQuery("INSERT INTO mig_three (new_sv_name, random_long) VALUES ('AFTER MIGRATE SCRIPT DB SCRIPT', -1);", null));
                out.addLast(new KittySQLiteQuery("INSERT INTO mig_three (new_sv_name, random_long) VALUES ('AFTER MIGRATE SCRIPT DB SCRIPT', -10);", null));
                out.addLast(new KittySQLiteQuery("INSERT INTO mig_three (new_sv_name, random_long) VALUES ('AFTER MIGRATE SCRIPT DB SCRIPT', -11);", null));
                out.addLast(new KittySQLiteQuery("INSERT INTO mig_three (new_sv_name, random_long) VALUES ('AFTER MIGRATE SCRIPT DB SCRIPT', -100);", null));
                out.addLast(new KittySQLiteQuery("INSERT INTO mig_three (new_sv_name, random_long) VALUES ('AFTER MIGRATE SCRIPT DB SCRIPT', -101);", null));
                out.addLast(new KittySQLiteQuery("INSERT INTO mig_three (new_sv_name, random_long) VALUES ('AFTER MIGRATE SCRIPT DB SCRIPT', -111);", null));
                out.addLast(new KittySQLiteQuery("INSERT INTO mig_three (new_sv_name, random_long) VALUES ('AFTER MIGRATE SCRIPT DB SCRIPT', -1000);", null));
                out.addLast(new KittySQLiteQuery("INSERT INTO mig_three (new_sv_name, random_long) VALUES ('AFTER MIGRATE SCRIPT DB SCRIPT', -1001);", null));
                return out;
            }
        };
    }
}
