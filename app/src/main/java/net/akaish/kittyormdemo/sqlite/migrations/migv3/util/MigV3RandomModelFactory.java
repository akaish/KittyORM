
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

package net.akaish.kittyormdemo.sqlite.migrations.migv3.util;

import android.content.Context;
import android.util.SparseArray;

import net.akaish.kittyormdemo.gsonmodels.AnimalSounds;
import net.akaish.kittyormdemo.sqlite.migrations.migv3.MigOneModel;
import net.akaish.kittyormdemo.sqlite.migrations.migv3.MigThreeModel;
import net.akaish.kittyormdemo.sqlite.migrations.migv3.MigTwoModel;
import net.akaish.kittyormdemo.sqlite.misc.Animals;

import java.util.ArrayList;
import java.util.Date;
import java.util.Random;

/**
 * Created by akaish on 07.10.18.
 * @author akaish (Denis Bogomolov)
 */

public class MigV3RandomModelFactory {
    final Context context;
    final Random rnd;

    private final SparseArray<String> randomAnimalSays = new SparseArray<>();
    private final SparseArray<String> randomAnimalLocalizedName = new SparseArray<>();

    public MigV3RandomModelFactory(Context ctx) {
        this.context = ctx;
        this.rnd = new Random();

        // Lol, getContext().getString() method is fucking slow, calling for each new random model this method twice causes 55% of all execution time of generating new random model (!)
        // Right now getting those string causes only 14% of execution time
        randomAnimalSays.append(Animals.getLocalizedAnimalSaysResource(Animals.BEAR), context.getString(Animals.getLocalizedAnimalSaysResource(Animals.BEAR)));
        randomAnimalSays.append(Animals.getLocalizedAnimalSaysResource(Animals.CAT), context.getString(Animals.getLocalizedAnimalSaysResource(Animals.CAT)));
        randomAnimalSays.append(Animals.getLocalizedAnimalSaysResource(Animals.DOG), context.getString(Animals.getLocalizedAnimalSaysResource(Animals.DOG)));
        randomAnimalSays.append(Animals.getLocalizedAnimalSaysResource(Animals.GOAT), context.getString(Animals.getLocalizedAnimalSaysResource(Animals.GOAT)));
        randomAnimalSays.append(Animals.getLocalizedAnimalSaysResource(Animals.LION), context.getString(Animals.getLocalizedAnimalSaysResource(Animals.LION)));
        randomAnimalSays.append(Animals.getLocalizedAnimalSaysResource(Animals.SHEEP), context.getString(Animals.getLocalizedAnimalSaysResource(Animals.SHEEP)));
        randomAnimalSays.append(Animals.getLocalizedAnimalSaysResource(Animals.TIGER), context.getString(Animals.getLocalizedAnimalSaysResource(Animals.TIGER)));
        randomAnimalSays.append(Animals.getLocalizedAnimalSaysResource(Animals.WOLF), context.getString(Animals.getLocalizedAnimalSaysResource(Animals.WOLF)));


        randomAnimalLocalizedName.append(Animals.getLocalizedAnimalNameResource(Animals.BEAR), context.getString(Animals.getLocalizedAnimalNameResource(Animals.BEAR)));
        randomAnimalLocalizedName.append(Animals.getLocalizedAnimalNameResource(Animals.CAT), context.getString(Animals.getLocalizedAnimalNameResource(Animals.CAT)));
        randomAnimalLocalizedName.append(Animals.getLocalizedAnimalNameResource(Animals.DOG), context.getString(Animals.getLocalizedAnimalNameResource(Animals.DOG)));
        randomAnimalLocalizedName.append(Animals.getLocalizedAnimalNameResource(Animals.GOAT), context.getString(Animals.getLocalizedAnimalNameResource(Animals.GOAT)));
        randomAnimalLocalizedName.append(Animals.getLocalizedAnimalNameResource(Animals.LION), context.getString(Animals.getLocalizedAnimalNameResource(Animals.LION)));
        randomAnimalLocalizedName.append(Animals.getLocalizedAnimalNameResource(Animals.SHEEP), context.getString(Animals.getLocalizedAnimalNameResource(Animals.SHEEP)));
        randomAnimalLocalizedName.append(Animals.getLocalizedAnimalNameResource(Animals.TIGER), context.getString(Animals.getLocalizedAnimalNameResource(Animals.TIGER)));
        randomAnimalLocalizedName.append(Animals.getLocalizedAnimalNameResource(Animals.WOLF), context.getString(Animals.getLocalizedAnimalNameResource(Animals.WOLF)));
    }

    public MigOneModel newM1RndModel() {
        return newM1RndModel(rnd.nextBoolean(), rnd.nextBoolean());
    }

    public MigOneModel newM1RndModel(boolean setCDDefault, boolean setDefaultInteger) {
        MigOneModel model = new MigOneModel();
        if(setCDDefault)
            model.setFieldExclusion("creationDate");
        else
            model.creationDate = new Date(System.currentTimeMillis()).toString();
        if(setCDDefault)
            model.setFieldExclusion("defaultInteger");
        else
            model.defaultInteger = rnd.nextInt();
        return model;
    }

    public MigTwoModel newM2RndModel(ArrayList<MigOneModel> models) {
        if(models == null)
            throw new IllegalArgumentException("M3RMF#newM2RndModel bad model collection provided!");
        if(models.size() == 0)
            throw new IllegalArgumentException("M3RMF#newM2RndModel bad model collection provided!");
        int mlSize = models.size();
        return newM2RndModel(models.get(rnd.nextInt(mlSize)).id);
    }

    public MigTwoModel newM2RndModel(Long migOneReference) {
        if(migOneReference == null)
            throw new IllegalArgumentException("M3RMF#newM2RndModel bad reference id provided!");
        MigTwoModel model = new MigTwoModel();
        model.someAnimal = Animals.rndAnimal(rnd);
        model.migOneReference = migOneReference;
        AnimalSounds animalSounds = new AnimalSounds();
        animalSounds.animalName = randomAnimalLocalizedName.get(Animals.getLocalizedAnimalNameResource(model.someAnimal));
        animalSounds.animalSounds = randomAnimalSays.get(Animals.getLocalizedAnimalSaysResource(model.someAnimal));
        model.someAnimalSound = animalSounds;
        return model;
    }

    static final String[] M3_SOME_VALUES = {"One", "Apple", "Wolf", "Plane", "Name", "Fear of being alone", "Despair", "Death", "Do not look for meaning where it is not"};

    public MigThreeModel newM3RndModel() {
        return newM3RndModel(rnd.nextBoolean());
    }

    public MigThreeModel newM3RndModel(boolean setDefaultValue) {
        MigThreeModel model = new MigThreeModel();
        if(setDefaultValue)
            model.setFieldExclusion("someValue");
        else
            model.someValue = M3_SOME_VALUES[rnd.nextInt(M3_SOME_VALUES.length)];
        return model;
    }
}
