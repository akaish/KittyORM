
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
 * This file is a part of KittyORM project (KittyORM Demo), more information at
 * https://akaish.github.io/KittyORMPages/license/
 *
 * ---
 */

package net.akaish.kittyormdemo.sqlite.migrations.migv3;

import android.content.Context;

import net.akaish.kitty.orm.annotations.KITTY_DATABASE;
import net.akaish.kitty.orm.KittyDatabase;
import net.akaish.kitty.orm.annotations.KITTY_DATABASE_HELPER;
import net.akaish.kitty.orm.annotations.KITTY_DATABASE_REGISTRY;

/**
 * Created by akaish on 03.10.18.
 * @author akaish (Denis Bogomolov)
 */
@KITTY_DATABASE(
        isLoggingOn = true,
        isProductionOn = false,
        isKittyDexUtilLoggingEnabled = false,
        logTag = MigrationDBv3.LTAG,
        databaseName = "mig",
        databaseVersion = 3,
        domainPackageNames = {"net.akaish.kittyormdemo.sqlite.migrations.migv3"}
)
@KITTY_DATABASE_REGISTRY(
        domainModels = {
                net.akaish.kittyormdemo.sqlite.migrations.migv3.MigOneModel.class,
                net.akaish.kittyormdemo.sqlite.migrations.migv3.MigTwoModel.class,
                net.akaish.kittyormdemo.sqlite.migrations.migv3.MigThreeModel.class
        }
)
@KITTY_DATABASE_HELPER(
        onUpgradeBehavior = KITTY_DATABASE_HELPER.UpgradeBehavior.USE_SIMPLE_MIGRATIONS
)
public class MigrationDBv3 extends KittyDatabase {

    public static final String LTAG = "MIGv3";
    /**
     * KittyORM main database class that represents bootstrap and holder for all related with database
     * components.
     *
     * @param ctx
     */
    public MigrationDBv3(Context ctx) {
        super(ctx);
    }
}
