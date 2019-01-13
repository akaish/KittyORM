
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
 * This file is a part of KittyORM project (KittyORM Demo), more information at
 * https://akaish.github.io/KittyORMPages/license/
 *
 * ---
 */

package net.akaish.kittyormdemo.lessons.one;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import net.akaish.kittyormdemo.KittyTutorialActivity;
import net.akaish.kittyormdemo.LessonDetailActivity;
import net.akaish.kittyormdemo.R;
import net.akaish.kittyormdemo.lessons.FabContainer;
import net.akaish.kittyormdemo.lessons.LessonFabBaseFragment;
import net.akaish.kittyormdemo.lessons.LessonHostFragment;
import net.akaish.kittyormdemo.lessons.LessonTabAdapter;
import net.akaish.kittyormdemo.lessons.LessonTabFragmentOnVisibleAction;
import net.akaish.kittyormdemo.lessons.RegularLessonBaseFragment;

import java.util.LinkedList;

/**
 * Created by akaish on 21.08.18.
 * @author akaish (Denis Bogomolov)
 */

public class Lesson1KittyORMIntroduction extends Fragment implements LessonFabBaseFragment, LessonHostFragment {

    LessonTabAdapter lesson1TabAdapter;
    ViewPager viewPager;
    boolean initialFabDrawCall = true;

    public Lesson1KittyORMIntroduction() {};

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.lesson_tab_fragment, container, false);
        viewPager = (ViewPager) rootView.findViewById(R.id.lt_viewpager);

        lesson1TabAdapter = new LessonTabAdapter(this, getChildFragmentManager());
        lesson1TabAdapter.addFragment(new Lesson1Tab1Introduction(), getContext().getString(R.string._l1_tab_intro));
        lesson1TabAdapter.addFragment(new Lesson1Tab2GettingStarted(), getContext().getString(R.string._l1_tab_getting_started));
        viewPager.setAdapter(lesson1TabAdapter);
        TabLayout tabs = (TabLayout) rootView.findViewById(R.id.lt_result_tabs);
        tabs.setupWithViewPager(viewPager);
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                // do nothing
            }

            @Override
            public void onPageSelected(int position) {
                try {
                    LessonTabFragmentOnVisibleAction fragment = (LessonTabFragmentOnVisibleAction) lesson1TabAdapter.instantiateItem(viewPager, position);
                    if (fragment != null) {
                        fragment.onVisible();
                    }
                    // for some reason that I do not want to figure out, sometimes on swiping tabs fab menu
                    // not loaded properly, but while switching tabs all ok so just load it here too
                    // yeah, it is dirty hack, and maybe it would be better rewrite it with usage of
                    // ViewPager.OnPageChangeListener() instead of current realization but no
                    ((KittyTutorialActivity) getActivity()).initialFragmentLoadedByActivity = false;
                    ((KittyTutorialActivity) getActivity()).notifyToChangeFabMenu(((LessonFabBaseFragment)fragment).getFragmentSpecificFabLayoutResource(), ((LessonFabBaseFragment)fragment).getFragmentSpecificFabOnClickListeners(), ((LessonFabBaseFragment)fragment).getFragmentSpecificListOfFabsLayouts());
                } catch (Exception e) {
                    Log.w("KittyORM Demo", "Some error on tab pager, lesson 1", e);
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {
                // do nothing
            }
        });
        return rootView;
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onResume() {
        super.onResume();
    }


    public void notifyFragmentOnViewPagerChanged() {
        if(initialFabDrawCall && !((KittyTutorialActivity)getActivity()).twoPane) {
            ((KittyTutorialActivity) getActivity()).notifyToChangeFabMenu(
                    getFragmentSpecificFabLayoutResource(),
                    getFragmentSpecificFabOnClickListeners(),
                    getFragmentSpecificListOfFabsLayouts()
            );
        }
        initialFabDrawCall = false;
    }

    @Override
    public RegularLessonBaseFragment getCurrentTabFragment() {
        return (RegularLessonBaseFragment) lesson1TabAdapter.currentFragment;
    }

    @Override
    public int getFragmentSpecificFabLayoutResource() {
        return lesson1TabAdapter.currentFragment.getFragmentSpecificFabLayoutResource();
    }

    @Override
    public LinkedList<FabContainer> getFragmentSpecificFabOnClickListeners() {
        return lesson1TabAdapter.currentFragment.getFragmentSpecificFabOnClickListeners();
    }

    @Override
    public LinkedList<Integer> getFragmentSpecificListOfFabsLayouts() {
        return lesson1TabAdapter.currentFragment.getFragmentSpecificListOfFabsLayouts();
    }
}
