
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

package net.akaish.kittyormdemo.sqlite.introductiondb;

import android.content.Context;

import net.akaish.kitty.orm.annotations.KittyDatabase;

/**
 * Created by akaish on 09.08.18.
 * @author akaish (Denis Bogomolov)
 */
@KittyDatabase(
        isLoggingOn = true,
        isProductionOn = false
        //, setReturnNullInsteadEmptyCollection = true
)
public class SimpleDatabase extends net.akaish.kitty.orm.KittyDatabase {



    /**
     * KittyORM main database class that represents bootstrap and holder for all related with database
     * components.
     * <br> See {@link net.akaish.kitty.orm.KittyDatabase#KittyDatabase(Context, String)} for more info.
     *
     * @param ctx
     */
    public SimpleDatabase(Context ctx) {
        super(ctx);
    }
}
