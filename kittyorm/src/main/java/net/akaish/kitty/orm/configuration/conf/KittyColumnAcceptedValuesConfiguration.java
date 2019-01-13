
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
 * This file is a part of KittyORM project (KittyORM library), more information at
 * https://akaish.github.io/KittyORMPages/license/
 *
 * ---
 */

package net.akaish.kitty.orm.configuration.conf;

/**
 * Created by akaish on 07.02.18.
 * @author akaish (Denis Bogomolov)
 */

public class KittyColumnAcceptedValuesConfiguration {
    public final String[] acceptedValuesString;
    public final int[] acceptedValuesInt;
    public final long[] acceptedValuesLong;
    public final short[] acceptedValuesShort;
    public final byte[] acceptedValuesByte;
    public final float[] acceptedValuesFloat;
    public final double[] acceptedValuesDouble;

    public KittyColumnAcceptedValuesConfiguration(String[] acceptedValuesString, int[] acceptedValuesInt,
                                                  long[] acceptedValuesLong, short[] acceptedValuesShort,
                                                  byte[] acceptedValuesByte, float[] acceptedValuesFloat,
                                                  double[] acceptedValuesDouble) {
        this.acceptedValuesString = acceptedValuesString;
        this.acceptedValuesInt = acceptedValuesInt;
        this.acceptedValuesLong = acceptedValuesLong;
        this.acceptedValuesShort = acceptedValuesShort;
        this.acceptedValuesByte = acceptedValuesByte;
        this.acceptedValuesFloat = acceptedValuesFloat;
        this.acceptedValuesDouble = acceptedValuesDouble;
    }
}
