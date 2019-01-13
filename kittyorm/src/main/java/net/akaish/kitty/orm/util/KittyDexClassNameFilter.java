
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

import android.util.Log;

/**
 * Class for filtering class names before their initialization by {@link KittyDexUtils}
 * Created by akaish on 16.02.18.
 * @author akaish (Denis Bogomolov)
 */
public class KittyDexClassNameFilter {
    String[] patterns = null;
    String[] startWith = null;

    public KittyDexClassNameFilter(String[] patterns, String[] startWith) {
        super();
        this.patterns = patterns;
        this.startWith = startWith;
    }

    public KittyDexClassNameFilter() {
        super();
    }

    public boolean shouldBeSkipped(String clsName) {
        if(patterns!=null) {
            for(String pattern : patterns) {
                if(clsName.matches(pattern))
                    return true;
            }
        }
        if(startWith!=null) {
            for(String start : startWith) {
                if(clsName.startsWith(start))
                    return true;
            }
        }
        return false;
    }
}