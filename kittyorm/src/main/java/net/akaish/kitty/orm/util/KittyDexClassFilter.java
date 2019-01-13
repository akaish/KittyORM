
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

/**
 * Class filter class;
 * Created by akaish on 16.02.18.
 * @author akaish (Denis Bogomolov)
 */
public class KittyDexClassFilter {
    boolean skipEnums = false;
    boolean skipAnnotations = false;
    boolean skipAbstract = false;
    boolean skipInterfaces = false;
    boolean skipArrays = false;
    boolean skipLocalClass = false;
    boolean skipMemberClass = false;
    boolean skipAnonymousClass = false;
    boolean skipPrimitives = false;
    String[] packageNamesFilter = null;
    Class[] onlyAssignableFromSuperTypes = null;

    public KittyDexClassFilter(boolean skipEnums, boolean skipAnnotations, boolean skipAbstract,
                               boolean skipInterfaces, boolean skipArrays, boolean skipLocalClass,
                               boolean skipMemberClass, boolean skipAnonymousClass, boolean skipPrimitives,
                               String[] packageNamesFilter, Class[] onlyAssignableFromSuperTypes) {
        super();
        this.skipEnums = skipEnums;
        this.skipAnnotations = skipAnnotations;
        this.skipAbstract = skipAbstract;
        this.skipInterfaces = skipInterfaces;
        this.skipArrays = skipArrays;
        this.skipLocalClass = skipLocalClass;
        this.skipMemberClass = skipMemberClass;
        this.skipAnonymousClass = skipAnonymousClass;
        this.skipPrimitives = skipPrimitives;
        this.packageNamesFilter = packageNamesFilter;
        this.onlyAssignableFromSuperTypes = onlyAssignableFromSuperTypes;
    }

    public KittyDexClassFilter() {
        super();
    }

    public KittyDexClassFilter setSkipEnums(boolean skipEnums) {
        this.skipEnums = skipEnums;
        return this;
    }

    public KittyDexClassFilter setSkipAnnotations(boolean skipAnnotations) {
        this.skipAnnotations = skipAnnotations;
        return this;
    }

    public KittyDexClassFilter setSkipAbstract(boolean skipAbstract) {
        this.skipAbstract = skipAbstract;
        return this;
    }

    public KittyDexClassFilter setSkipInterfaces(boolean skipInterfaces) {
        this.skipInterfaces = skipInterfaces;
        return this;
    }

    public KittyDexClassFilter setSkipArrays(boolean skipArrays) {
        this.skipArrays = skipArrays;
        return this;
    }

    public KittyDexClassFilter setSkipLocalClass(boolean skipLocalClass) {
        this.skipLocalClass = skipLocalClass;
        return this;
    }

    public KittyDexClassFilter setSkipMemberClass(boolean skipMemberClass) {
        this.skipMemberClass = skipMemberClass;
        return this;
    }

    public KittyDexClassFilter setSkipAnonymousClass(boolean skipAnonymousClass) {
        this.skipAnonymousClass = skipAnonymousClass;
        return this;
    }

    public KittyDexClassFilter setSkipPrimitives(boolean skipPrimitives) {
        this.skipPrimitives = skipPrimitives;
        return this;
    }

    public KittyDexClassFilter setPackageNamesFilter(String... packageNamesFilter) {
        this.packageNamesFilter = packageNamesFilter;
        return this;
    }

    public KittyDexClassFilter setOnlyAssignableFromSuperTypes(Class... onlyAssignableFromSuperTypes) {
        this.onlyAssignableFromSuperTypes = onlyAssignableFromSuperTypes;
        return this;
    }
}
