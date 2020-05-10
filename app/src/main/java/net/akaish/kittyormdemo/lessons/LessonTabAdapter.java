
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

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.view.ViewGroup;

import net.akaish.kittyormdemo.lessons.LessonFabBaseFragment;
import net.akaish.kittyormdemo.lessons.two.Lesson2KittyORMBasicImplementation;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by akaish on 22.08.18.
 * @author akaish (Denis Bogomolov)
 */
public class LessonTabAdapter extends FragmentPagerAdapter {
    private LessonHostFragment lesson2KittyORMBasicImplementation;
    private final ArrayList<Fragment> fragmentList = new ArrayList<>();
    private final List<String> fragmentTitleList = new ArrayList<>();

    public LessonFabBaseFragment currentFragment;

    public LessonFabBaseFragment getCurrentFragment() {
        return currentFragment;
    }

    public LessonTabAdapter(LessonHostFragment lesson2KittyORMBasicImplementation, FragmentManager manager) {
        super(manager);
        this.lesson2KittyORMBasicImplementation = lesson2KittyORMBasicImplementation;
    }

    @Override
    public Fragment getItem(int position) {
        return fragmentList.get(position);
    }

    @Override
    public int getCount() {
        return fragmentList.size();
    }

    public void addFragment(Fragment fragment, String title) {
        fragmentList.add(fragment);
        fragmentTitleList.add(title);
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return fragmentTitleList.get(position);
    }

    @Override
    public void setPrimaryItem(ViewGroup container, int position, Object object) {
        if (currentFragment == null) {
            currentFragment = (LessonFabBaseFragment) object;
            lesson2KittyORMBasicImplementation.notifyFragmentOnViewPagerChanged();
        } else {
            if (!object.getClass().equals(currentFragment.getClass())) {
                currentFragment = (LessonFabBaseFragment) object;
                lesson2KittyORMBasicImplementation.notifyFragmentOnViewPagerChanged();
                super.setPrimaryItem(container, position, object);
                return;
            }
        }
        currentFragment = (LessonFabBaseFragment) object;
        super.setPrimaryItem(container, position, object);
    }

}
