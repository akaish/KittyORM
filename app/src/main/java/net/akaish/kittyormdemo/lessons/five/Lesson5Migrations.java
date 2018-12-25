
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

package net.akaish.kittyormdemo.lessons.five;

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
 * Created by akaish on 10.09.18.
 * @author akaish (Denis Bogomolov)
 */

public class Lesson5Migrations  extends Fragment implements LessonFabBaseFragment, LessonHostFragment {

    LessonTabAdapter lesson5TabAdapter;
    private ViewPager viewPager;

    boolean initialFabDrawCall = true;


    public Lesson5Migrations() {};

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

        lesson5TabAdapter = new LessonTabAdapter(this, getChildFragmentManager());
        Fragment initialFragment = new Lesson5Tab1InitialDatabase();
        lesson5TabAdapter.addFragment(initialFragment, getContext().getString(R.string._l5_tab_initial_db));
        lesson5TabAdapter.addFragment(new Lesson5Tab2DCMigrations(), getContext().getString(R.string._l5_tab_cd_migration));
        lesson5TabAdapter.addFragment(new Lesson5Tab3AutogenMigration(), getContext().getString(R.string._l5_tab_auto_migration));
        lesson5TabAdapter.addFragment(new Lesson5Tab4FilescriptMigration(), getContext().getString(R.string._l5_tab_fs_migrations));
        viewPager.setAdapter(lesson5TabAdapter);

        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                // do nothing
            }

            @Override
            public void onPageSelected(int position) {
                try {
                    LessonTabFragmentOnVisibleAction fragment = (LessonTabFragmentOnVisibleAction) lesson5TabAdapter.instantiateItem(viewPager, position);
                    if (fragment != null) {
                        fragment.onVisible();
                    }
                    // for some reason that I do not want to figure out, sometimes on swiping tabs fab menu
                    // not loaded properly, but while switching tabs all ok so just load it here too
                    // yeah, it is dirty hack, and maybe it would be better rewrite it with usage of
                    // ViewPager.OnPageChangeListener() instead of current realization but no
                    ((KittyTutorialActivity) getActivity()).initialFragmentLoadedByActivity = false;
                    ((KittyTutorialActivity) getActivity()).notifyToChangeFabMenu(((LessonFabBaseFragment)fragment).getFragmentSpecificFabLayoutResource(),((LessonFabBaseFragment)fragment).getFragmentSpecificFabOnClickListeners(), ((LessonFabBaseFragment)fragment).getFragmentSpecificListOfFabsLayouts());
                } catch (Exception e) {
                    Log.w("KittyORM Demo", "Some error on tab pager, lesson 5", e);
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {
                // do nothing
            }
        });
        TabLayout tabs = (TabLayout) rootView.findViewById(R.id.lt_result_tabs);
        tabs.setupWithViewPager(viewPager);
        viewPager.computeScroll();
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

    KittyTutorialActivity getLessonActiity() {
        return ((KittyTutorialActivity) getActivity());
    }

    /**
     * For some reason that I can't figure out on swiping tabs fab menu randomly doen't render without any reason and log E\W
     * But it renders if notification was from ViewPager.OnPageChangeListener()
     * Fucken magic or not enough knowledge, don't care
     */
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
        return (RegularLessonBaseFragment) lesson5TabAdapter.currentFragment;
    }

    @Override
    public int getFragmentSpecificFabLayoutResource() {
        return lesson5TabAdapter.currentFragment.getFragmentSpecificFabLayoutResource();

    }

    @Override
    public LinkedList<FabContainer> getFragmentSpecificFabOnClickListeners() {
        return lesson5TabAdapter.currentFragment.getFragmentSpecificFabOnClickListeners();
    }

    @Override
    public LinkedList<Integer> getFragmentSpecificListOfFabsLayouts() {
        return lesson5TabAdapter.currentFragment.getFragmentSpecificListOfFabsLayouts();
    }
}
