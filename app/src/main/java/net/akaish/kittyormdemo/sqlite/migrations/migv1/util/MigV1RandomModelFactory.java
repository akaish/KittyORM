
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
 * This file is a part of KittyORM project (KittyORM Demo), more information at
 * https://akaish.github.io/KittyORMPages/license/
 *
 * ---
 */

package net.akaish.kittyormdemo.sqlite.migrations.migv1.util;

import android.content.Context;

import net.akaish.kittyormdemo.sqlite.migrations.migv1.MigOneModel;

import java.util.Date;
import java.util.Random;

/**
 * Created by akaish on 07.10.18.
 * @author akaish (Denis Bogomolov)
 */

public class MigV1RandomModelFactory {
    final Context ctx;
    final Random rnd;

    public MigV1RandomModelFactory(Context ctx) {
        this.ctx = ctx;
        this.rnd = new Random();
    }

    public MigOneModel newM1RndModel() {
        MigOneModel model = new MigOneModel();
        model.creationDate = new Date(System.currentTimeMillis()).toString();
        model.someInteger = rnd.nextInt();
        return model;
    }
}
