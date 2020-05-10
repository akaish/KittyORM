
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

package net.akaish.kittyormdemo.sqlite.migrations.migv2.util;

import android.content.Context;

import net.akaish.kittyormdemo.sqlite.migrations.migv2.MigOneModel;
import net.akaish.kittyormdemo.sqlite.migrations.migv2.MigTwoModel;
import net.akaish.kittyormdemo.sqlite.misc.Animals;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.Random;

/**
 * Created by akaish on 07.10.18.
 * @author akaish (Denis Bogomolov)
 */

public class MigV2RandomModelFactory {
    final Context ctx;
    final Random rnd;

    public MigV2RandomModelFactory(Context ctx) {
        this.ctx = ctx;
        this.rnd = new Random();
    }

    public MigOneModel newM1RndModel() {
        return newM1RndModel(rnd.nextBoolean());
    }

    public MigOneModel newM1RndModel(boolean setCDDefault) {
        MigOneModel model = new MigOneModel();
        if(setCDDefault)
            model.setFieldExclusion("creationDate");
        else
            model.creationDate = new Date(System.currentTimeMillis()).toString();
        model.defaultInteger = rnd.nextInt();
        model.currentTimestamp = new Timestamp(System.currentTimeMillis());
        return model;
    }

    public MigTwoModel newM2RndModel(ArrayList<MigOneModel> models) {
        int mlSize = models.size();
        return newM2RndModel(models.get(rnd.nextInt(mlSize)).id);
    }

    public MigTwoModel newM2RndModel(Long migOneReference) {
        MigTwoModel model = new MigTwoModel();
        model.someAnimal = Animals.rndAnimal(rnd);
        model.migOneReference = migOneReference;
        return model;
    }

}
