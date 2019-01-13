
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

package net.akaish.kitty.orm.util;

import net.akaish.kitty.orm.configuration.conf.KittyColumnConfiguration;

import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by akaish on 02.10.18.
 * @author akaish (Denis Bogomolov)
 */

public final class KittyColumnsKey {

    final static int BITS = 32;

    public final static KittyArrayKey generateKittyArrayKey(LinkedList<KittyColumnConfiguration> columns, List<String> fieldExclusions) {
        return new KittyArrayKey(generateArrayKey(columns, fieldExclusions));
    }

    public static final int[] generateArrayKey(LinkedList<KittyColumnConfiguration> columns, List<String> fieldExclusions) {
        if(fieldExclusions == null) {
            return defaultArrayKey(columns.size());
        } else {
            if(fieldExclusions.size() == 0) {
                return defaultArrayKey(columns.size());
            } else {
                Iterator<KittyColumnConfiguration> kcIterator = columns.iterator();
                final int arrayKeySize = arrayKeySize(columns.size());
                int counter = 0; int position = 0; int key = 0;
                int[] arrayKey = new int[arrayKeySize];
                while (kcIterator.hasNext()) {
                    if(counter == BITS) {
                        arrayKey[position] = key;
                        key=0;
                        counter=0;
                        position++;
                        continue;
                    }
                    counter++;
                    boolean exclude = fieldExclusions.contains(kcIterator.next().mainConfiguration.columnField.getName());
                    key = (key << 1) + (exclude ? 0 : 1);
                }
                arrayKey[position] = key;
                return arrayKey;
            }
        }
    }

    private final static int arrayKeySize(int columnsAmount) {
        int arrayKeySize = columnsAmount / BITS;
        return  (columnsAmount % BITS) > 0 ? arrayKeySize + 1 : arrayKeySize;
    }

    private final static int[] defaultArrayKey(int columnsAmount) {
        final int arrayKeySize = arrayKeySize(columnsAmount);
        int counter = 0; int position = 0; int key = 0;
        int[] arrayKey = new int[arrayKeySize];
        for(int i = 0; i < columnsAmount; i++) {
            if(counter == BITS) {
                arrayKey[position] = key;
                key=0;
                counter=0;
                position++;
                i--;
                continue;
            }
            counter++;
            key = (key << 1) + 1;
        }
        arrayKey[position] = key;
        return arrayKey;
    }
}
