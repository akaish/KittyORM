
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

package net.akaish.kittyormdemo.menu;

import android.support.v4.app.Fragment;
import android.util.SparseArray;

import net.akaish.kittyormdemo.R;
import net.akaish.kittyormdemo.lessons.five.Lesson5Migrations;
import net.akaish.kittyormdemo.lessons.four.Lesson4OptimiseDebugEncrypt;
import net.akaish.kittyormdemo.lessons.one.Lesson1KittyORMIntroduction;
import net.akaish.kittyormdemo.lessons.seven.Lesson7ContactsAndLicense;
import net.akaish.kittyormdemo.lessons.six.Lesson6MakeKittiORMGreat;
import net.akaish.kittyormdemo.lessons.three.Lesson3DatatypesAndConstraints;
import net.akaish.kittyormdemo.lessons.two.Lesson2KittyORMBasicImplementation;

/**
 * Created by akaish on 03.08.18.
 * @author akaish (Denis Bogomolov)
 */

public class MainMenu {

    public static final SparseArray<LessonItem> LESSON_ITEMS = new SparseArray<>();

    public static final String LESSON_ID = "LessonID";

    public static final int LESSON_1_INTRODUCTION = 0;
    public static final int LESSON_2_BASIC_IMPLEMENTATION = 1;
    public static final int LESSON_3_DATATYPES_AND_CONSTRAINS = 2;
    public static final int LESSON_4_OPTIMISATION_DEBUGGING_ENCRYPTION = 3;
    public static final int LESSON_5_DATABASE_VERSION_MIGRATIONS = 4;
    public static final int LESSON_6_HELP_TO_IMPROVE_KITTY_ORM = 5;
    public static final int LESSON_7_CONTACT_LICENSE = 6;

    static {
        LESSON_ITEMS.append(LESSON_1_INTRODUCTION, new LessonItem(LESSON_1_INTRODUCTION, R.string._menu_lesson_1_title_introduction));
        LESSON_ITEMS.append(LESSON_2_BASIC_IMPLEMENTATION, new LessonItem(LESSON_2_BASIC_IMPLEMENTATION, R.string._menu_lesson_2_title_basic_implementation));
        LESSON_ITEMS.append(LESSON_3_DATATYPES_AND_CONSTRAINS, new LessonItem(LESSON_3_DATATYPES_AND_CONSTRAINS, R.string._menu_lesson_3_title_datatypes_and_constraints));
        LESSON_ITEMS.append(LESSON_4_OPTIMISATION_DEBUGGING_ENCRYPTION, new LessonItem(LESSON_4_OPTIMISATION_DEBUGGING_ENCRYPTION, R.string._menu_lesson_4_title_optimisation_debug_and_ecyptyon));
        LESSON_ITEMS.append(LESSON_5_DATABASE_VERSION_MIGRATIONS, new LessonItem(LESSON_5_DATABASE_VERSION_MIGRATIONS, R.string._menu_lesson_5_title_migrations));
        LESSON_ITEMS.append(LESSON_6_HELP_TO_IMPROVE_KITTY_ORM, new LessonItem(LESSON_6_HELP_TO_IMPROVE_KITTY_ORM, R.string._menu_lesson_6_make_kitty_orm_greater));
        LESSON_ITEMS.append(LESSON_7_CONTACT_LICENSE, new LessonItem(LESSON_7_CONTACT_LICENSE, R.string._menu_lesson_7_contact_and_license));
    }

    public static class LessonItem {
        public final int id;
        public final int title;

        public LessonItem(int id, int title) {
            this.id = id;
            this.title = title;
        }

        public Fragment newLessonFragment() {
            return getFragment(id);
        }

        public static final Fragment getFragment(int lessonItemId) {
            switch (lessonItemId) {
                case LESSON_1_INTRODUCTION:
                    return new Lesson1KittyORMIntroduction();
                case LESSON_2_BASIC_IMPLEMENTATION:
                    return new Lesson2KittyORMBasicImplementation();
                case LESSON_3_DATATYPES_AND_CONSTRAINS:
                    return new Lesson3DatatypesAndConstraints();
                case LESSON_4_OPTIMISATION_DEBUGGING_ENCRYPTION:
                    return new Lesson4OptimiseDebugEncrypt();
                case LESSON_5_DATABASE_VERSION_MIGRATIONS:
                    return new Lesson5Migrations();
                case LESSON_6_HELP_TO_IMPROVE_KITTY_ORM:
                    return new Lesson6MakeKittiORMGreat();
                case LESSON_7_CONTACT_LICENSE:
                    return new Lesson7ContactsAndLicense();
                default:
                    return null;
            }

        }
    }
}
