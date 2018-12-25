
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

package net.akaish.kittyormdemo.sqlite.misc;

import java.util.Random;

import net.akaish.kittyormdemo.R;

/**
 * Created by akaish on 28.07.18.
 * @author akaish (Denis Bogomolov)
 */

public enum Animals {

    DOG ("DOG"),
    CAT ("CAT"),
    GOAT ("GOAT"),
    SHEEP ("SHEEP"),
    TIGER ("TIGER"),
    LION ("LION"),
    WOLF ("WOLF"),
    BEAR ("BEAR");

    Animals(String animalName) {
        animalText = animalName;
    }

    private final String animalText;

    public boolean equalsName(String sqlText) {
        // (otherName == null) check is not needed because name.equals(null) returns false
        return this.animalText.equals(sqlText);
    }

    @Override
    public String toString() {
        return this.animalText;
    }

    public static int getLocalizedAnimalSaysResource(Animals animal) {
        switch (animal) {
            case CAT:
                return R.string._a_animal_cat_says;
            case DOG:
                return R.string._a_animal_DOG_says;
            case BEAR:
                return R.string._a_animal_BEAR_says;
            case GOAT:
                return R.string._a_animal_GOAT_says;
            case LION:
                return R.string._a_animal_LION_says;
            case WOLF:
                return R.string._a_animal_WOLF_says;
            case SHEEP:
                return R.string._a_animal_SHEEP_says;
            case TIGER:
                return R.string._a_animal_TIGER_says;
        }
        return -1;
    }

    public static int getLocalizedAnimalNameResource(Animals animal) {
        switch (animal) {
            case CAT:
                return R.string._a_animal_CAT;
            case DOG:
                return R.string._a_animal_DOG;
            case BEAR:
                return R.string._a_animal_BEAR;
            case GOAT:
                return R.string._a_animal_GOAT;
            case LION:
                return R.string._a_animal_LION;
            case WOLF:
                return R.string._a_animal_WOLF;
            case SHEEP:
                return R.string._a_animal_SHEEP;
            case TIGER:
                return R.string._a_animal_TIGER;
        }
        return -1;
    }

    public static final Animals rndAnimal(Random rnd) {
        switch (rnd.nextInt(8)) {
            case 0:
                return DOG;
            case 1:
                return CAT;
            case 2:
                return GOAT;
            case 3:
                return SHEEP;
            case 4:
                return TIGER;
            case 5:
                return LION;
            case 6:
                return WOLF;
            case 7:
                return BEAR;
        }
        return null;
    }
}
