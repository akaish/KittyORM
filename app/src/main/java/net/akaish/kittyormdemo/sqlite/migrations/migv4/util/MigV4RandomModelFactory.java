
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

package net.akaish.kittyormdemo.sqlite.migrations.migv4.util;

import android.content.Context;
import android.util.SparseArray;

import net.akaish.kittyormdemo.gsonmodels.AnimalSounds;
import net.akaish.kittyormdemo.sqlite.migrations.migv4.MigFourModel;
import net.akaish.kittyormdemo.sqlite.migrations.migv4.MigThreeModel;
import net.akaish.kittyormdemo.sqlite.migrations.migv4.MigTwoModel;
import net.akaish.kittyormdemo.sqlite.misc.Animals;

import java.util.Date;
import java.util.List;
import java.util.Random;

/**
 * Created by akaish on 07.10.18.
 * @author akaish (Denis Bogomolov)
 */

public class MigV4RandomModelFactory {

    final Context context;
    final Random rnd;

    private final SparseArray<String> randomAnimalSays = new SparseArray<>();
    private final SparseArray<String> randomAnimalLocalizedName = new SparseArray<>();

    public MigV4RandomModelFactory(Context ctx) {
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

    public MigTwoModel newM2RndModel() {
        MigTwoModel model = new MigTwoModel();
        model.someAnimal = Animals.rndAnimal(rnd);
        model.migOneReference = rnd.nextLong();
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
        model.randomLong = rnd.nextLong();
        return model;
    }

    public MigFourModel newM4RndModel(List<MigThreeModel> mig3Models, List<MigTwoModel> mig2Models) {
        if(mig2Models == null || mig3Models == null) {
            throw new IllegalArgumentException("M4RMF#newM4RndModel bad collections provided!");
        }
        if(mig2Models.size() == 0 || mig3Models.size() == 0) {
            throw new IllegalArgumentException("M4RMF#newM4RndModel bad collections provided!");
        }
        return newM4RndModel(
                rnd.nextBoolean(),
                mig2Models.get(rnd.nextInt(mig2Models.size())).id,
                mig3Models.get(rnd.nextInt(mig3Models.size())).id
        );
    }

    public MigFourModel newM4RndModel(boolean setDefaultValue, Long mig2reference, Long mig3reference) {
        if(mig2reference == null || mig3reference == null)
            throw new IllegalArgumentException("M4RMF#newM4RndModel bad references id provided!");
        MigFourModel model = new MigFourModel();
        model.migThreeReference = mig3reference;
        model.migTwoReference = mig2reference;
        if(setDefaultValue)
            model.setFieldExclusion("creationDate");
        else
            model.creationDate = new Date(System.currentTimeMillis());
        return model;
    }
}
