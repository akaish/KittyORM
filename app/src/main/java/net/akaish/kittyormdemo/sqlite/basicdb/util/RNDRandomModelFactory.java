
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

package net.akaish.kittyormdemo.sqlite.basicdb.util;

import android.content.Context;
import android.util.SparseArray;

import net.akaish.kittyormdemo.sqlite.basicdb.RandomModel;
import net.akaish.kittyormdemo.sqlite.misc.Animals;

import java.util.Random;

/**
 * Created by akaish on 04.09.18.
 * @author akaish (Denis Bogomolov)
 */

public class RNDRandomModelFactory {

    private final Context context;
    private final Random randomizer;

    private final SparseArray<String> randomAnimalSays = new SparseArray<>();
    private final SparseArray<String> randomAnimalLocalizedName = new SparseArray<>();

    public RNDRandomModelFactory(Context context) {
        super();
        this.context = context;
        this.randomizer = new Random();

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

    public RandomModel newRandomModel() {
        RandomModel out = new RandomModel();
        out.randomInt = randomizer.nextInt();
        out.randomInteger = randomizer.nextInt();
        out.randomAnimal = Animals.rndAnimal(randomizer);
        out.randomAnimalSays = randomAnimalSays.get(Animals.getLocalizedAnimalSaysResource(out.randomAnimal));
        out.randomAnimalName = randomAnimalLocalizedName.get(Animals.getLocalizedAnimalNameResource(out.randomAnimal));
        return out;
    }
}
