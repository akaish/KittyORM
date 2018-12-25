
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

package net.akaish.kittyormdemo.sqlite.basicdb;

import android.content.Context;

import net.akaish.kitty.orm.KittyDatabase;
import net.akaish.kitty.orm.KittyMMEntry;
import net.akaish.kitty.orm.KittyMapper;
import net.akaish.kitty.orm.KittyModel;
import net.akaish.kitty.orm.annotations.KITTY_DATABASE;
import net.akaish.kitty.orm.annotations.KITTY_DATABASE_REGISTRY;
import net.akaish.kitty.orm.annotations.KITTY_REGISTRY_PAIR;

import java.util.HashMap;
import java.util.Map;

import static net.akaish.kittyormdemo.sqlite.basicdb.BasicDatabase.LOG_TAG;

/**
 * Created by akaish on 29.07.18.
 * @author akaish (Denis Bogomolov)
 */
@KITTY_DATABASE(
        databaseName = "basic_database",
        domainPackageNames = {"net.akaish.kittyormdemo.sqlite.basicdb"},
        logTag = LOG_TAG,
        isLoggingOn = true,
        isProductionOn = true,
        isPragmaOn = true
)
@KITTY_DATABASE_REGISTRY(
        domainModels = {
                ComplexRandomModel.class,
                IndexesAndConstraintsModel.class,
                RandomModel.class
        },
        domainPairs = {
                @KITTY_REGISTRY_PAIR(model = ComplexRandomModel.class, mapper = ComplexRandomMapper.class),
                @KITTY_REGISTRY_PAIR(model = IndexesAndConstraintsModel.class),
                @KITTY_REGISTRY_PAIR(model = RandomModel.class, mapper = RandomMapper.class)
        }
)
public class BasicDatabase extends KittyDatabase {

    public static final String LOG_TAG = "BASIC DB DEMO";

    /**
     * KittyORM main database class that represents bootstrap and holder for all related with database
     * components.
     * <br> See {@link KittyDatabase#KittyDatabase(Context, String)} for more info.
     *
     * @param ctx
     */
    public BasicDatabase(Context ctx) {
        super(ctx);
    }

}
