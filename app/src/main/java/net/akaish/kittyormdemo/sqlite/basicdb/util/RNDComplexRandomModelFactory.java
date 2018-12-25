
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

package net.akaish.kittyormdemo.sqlite.basicdb.util;

import android.content.Context;
import android.net.Uri;
import android.util.SparseArray;

import net.akaish.kittyormdemo.gsonmodels.AnimalSounds;
import net.akaish.kittyormdemo.sqlite.basicdb.ComplexRandomModel;
import net.akaish.kittyormdemo.sqlite.misc.Animals;
import net.akaish.kittyormdemo.sqlite.misc.SomeColours;

import java.io.File;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Currency;
import java.util.Date;
import java.util.Random;

import static java.lang.System.currentTimeMillis;

/**
 * Created by akaish on 29.07.18.
 * @author akaish (Denis Bogomolov)
 */

public class RNDComplexRandomModelFactory {

    private final Random randomizer;

    private final SparseArray<String> randomAnimalSays = new SparseArray<>();
    private final SparseArray<String> randomAnimalLocalizedName = new SparseArray<>();

    private final Context context;

    private final static String[] ISO4217_EXAMPLE_CODES = {"USD", "GBP", "EUR", "CNY", "JPY", "MYR", "AUD", "HKD", "PHP"}; // "KOR" currency code not present at 4.4
    private final static String[] URI_EXAMPLES = {"http://example.com", "file:///usr/somefile", "http://example.org"};
    private final static String[] FILE_EXAPLES = {"/file/one", "/file/two/some.txt", "/file/three/sys.iso"};

    public RNDComplexRandomModelFactory(Context context) {
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

    public ComplexRandomModel newComplexRandomModel() {
        ComplexRandomModel model = new ComplexRandomModel();

        model.boolF = randomizer.nextBoolean();
        model.randomInt = randomizer.nextInt();
        model.byteF = (byte) randomizer.nextInt(128);
        model.doubleF = randomizer.nextDouble();
        model.longF = randomizer.nextLong();
        model.shortF = (short) randomizer.nextInt(1000);
        model.floatF = randomizer.nextFloat();
        model.byteArray = new byte[8];
        randomizer.nextBytes(model.byteArray);

        StringBuffer rndStrBuffer = new StringBuffer(8);
        for(int i = 0; i < model.byteArray.length; i++) {
            rndStrBuffer.append((char) model.byteArray[i]);
        }
        model.stringF = rndStrBuffer.toString();
        model.bigDecimalF = new BigDecimal(randomizer.nextDouble());
        model.bigIntegerF = BigInteger.valueOf(randomizer.nextLong());
        model.randomAnimal = Animals.rndAnimal(randomizer);
        model.uriF = Uri.parse(URI_EXAMPLES[randomizer.nextInt(URI_EXAMPLES.length)]);
        model.fileF = new File(FILE_EXAPLES[randomizer.nextInt(FILE_EXAPLES.length)]);
        model.currencyF = Currency.getInstance(ISO4217_EXAMPLE_CODES[randomizer.nextInt(ISO4217_EXAMPLE_CODES.length)]);

        AnimalSounds animalSounds = new AnimalSounds();
        animalSounds.animalName = randomAnimalLocalizedName.get(Animals.getLocalizedAnimalNameResource(model.randomAnimal));
        animalSounds.animalSounds = randomAnimalSays.get(Animals.getLocalizedAnimalSaysResource(model.randomAnimal));
        model.stringSDF = animalSounds;

        model.bitmapColour = SomeColours.rndColour(randomizer);
        model.byteArraySDF = SomeColours.getSomeColourBitmpap(
                SomeColours.getSomeColoursBitmapResource(model.bitmapColour), context
        );

        model.boolFF = Boolean.valueOf(randomizer.nextBoolean());
        model.randomInteger = Integer.valueOf(randomizer.nextInt());
        model.randomAnimalName = context.getString(Animals.getLocalizedAnimalNameResource(model.randomAnimal));
        model.byteFF = Byte.valueOf((byte) randomizer.nextInt(128));
        model.doubleFF = Double.valueOf(randomizer.nextDouble());
        model.shortFF = new Short((short) randomizer.nextInt(10000));
        model.floatFF = Float.valueOf(randomizer.nextFloat());

        model.longFF = currentTimeMillis();
        model.dateF = new Date(model.longFF);
        model.calendarF = Calendar.getInstance();
        model.calendarF.setTimeInMillis(model.longFF);
        model.timestampF = new Timestamp(model.longFF);

        return model;
    }
}
