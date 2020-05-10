
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

package net.akaish.kittyormdemo.sqlite.introductiondb.util;

import net.akaish.kittyormdemo.sqlite.introductiondb.SimpleExampleModel;

import java.util.Random;

/**
 * Created by akaish on 21.08.18.
 * @author akaish (Denis Bogomolov)
 */

public class RandomSimpleExampleModelUtil {

    private static String NAMES[] = new String[] {"Adam", "Ada", "Joseph", "Michel", "Mickie", "Boris", "Denis", "Denise", "Alexander", "Irina"};

    public static SimpleExampleModel randomSEModel() {
        SimpleExampleModel m = new SimpleExampleModel();
        Random rnd = new Random();
        m.randomInteger = rnd.nextInt(1000);
        m.firstName = NAMES[rnd.nextInt(10)];
        return m;
    }
}
