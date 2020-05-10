
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

package net.akaish.kittyormdemo.lessons;

import net.akaish.kittyormdemo.R;

import java.util.LinkedList;

/**
 * Created by akaish on 12.11.18.
 * @author akaish (Denis Bogomolov)
 */
public class ContextFabMenus {

    public static final int MAIN_MENU_FAB = -1;

    public static final LinkedList<Integer> mainMenuFab() {
        LinkedList<Integer> out = new LinkedList<>();
        out.addLast(R.id.mm_license_fab_layout);
        out.addLast(R.id.mm_share_fab_layout);
        out.addLast(R.id.mm_follow_fab_layout);
        out.addLast(R.id.mm_mail_fab_layout);
        return out;
    }

    public static final LinkedList<Integer> tutorialSchemaCodeFab() {
        LinkedList<Integer> out = new LinkedList<>();
        out.addLast(R.id.regular_lesson_fab2_layout);
        out.addLast(R.id.regular_lesson_fab3_layout);
        out.addLast(R.id.regular_lesson_fab1_layout);
        return out;
    }

    public static final LinkedList<Integer> tutorialCodeFab() {
        LinkedList<Integer> out = new LinkedList<>();
        out.addLast(R.id.regular_lesson_fab2_layout);
        out.addLast(R.id.regular_lesson_fab1_layout);
        return out;
    }

    public static final LinkedList<Integer> useShareCommitSponsor() {
        LinkedList<Integer> out = new LinkedList<>();
        out.addLast(R.id.donate_fab_layout);
        out.addLast(R.id.commit_fab_layout);
        out.addLast(R.id.share_fab_layout);
        out.addLast(R.id.use_fab_layout);
        return out;
    }

    public static final LinkedList<Integer> mailLegal() {
        LinkedList<Integer> out = new LinkedList<>();
        out.addLast(R.id.licence_fab_layout);
        out.addLast(R.id.mail_fab_layout);
        return out;
    }

    public static final LinkedList<Integer> mailFollowLegal() {
        LinkedList<Integer> out = new LinkedList<>();
        out.addLast(R.id.licence_fab_layout);
        out.addLast(R.id.follow_fab_layout);
        out.addLast(R.id.mail_fab_layout);
        return out;
    }

    public static final LinkedList<Integer> getFabLayout(int lessonId) {
        switch (lessonId) {
            case MAIN_MENU_FAB:
                return mainMenuFab();
            case 6:
                return mailFollowLegal();
            case 5:
                return useShareCommitSponsor();
            case 3:
                return tutorialCodeFab();
            default:
                return tutorialSchemaCodeFab();
        }
    }

    public static final int getFabLayoutResource(int lessonId) {
        switch (lessonId) {
            case MAIN_MENU_FAB:
                return R.layout.main_menu_tab_fab;
            case 6:
                return R.layout.contact_license_tab_fab;
            case 5:
                return R.layout.make_kitty_great_tab_fab;
            default:
                return R.layout.regular_lesson_tab_fab;
        }
    }
}
