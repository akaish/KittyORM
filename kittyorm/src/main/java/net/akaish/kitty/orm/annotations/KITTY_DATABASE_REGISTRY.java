
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

package net.akaish.kitty.orm.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation for defining KittyORM registry
 * Created by akaish on 08.09.18.
 * @author akaish (Denis Bogomolov)
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface KITTY_DATABASE_REGISTRY {

    /**
     * List (array) of domain models, all models in this list would be checked and added to KittyORM registry
     * for annotated database. If model would be annotated {@link KITTY_EXTENDED_CRUD} that extended
     * CRUD would be added as mapper for this model, otherwise would be added {@link net.akaish.kitty.orm.KittyMapper}
     * <br> Has lower priority than {@link #domainPairs()}
     * @return
     */
    Class[] domainModels() default {};

    /**
     * List (array) of domain pairs (model to mapper), all pairs in this list would be checked and added
     * to KittyORM registry for annotated database. Any {@link KITTY_EXTENDED_CRUD} annotations would be ignored,
     * to set extended CRUD controller you have to define {@link KITTY_REGISTRY_PAIR#mapper()}.
     * <br> Has higher priority than {@link #domainModels()}
     * @return
     */
    KITTY_REGISTRY_PAIR[] domainPairs() default {};
}
