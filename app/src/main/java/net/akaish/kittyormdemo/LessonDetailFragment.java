
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

package net.akaish.kittyormdemo;

import android.app.Activity;
import android.support.design.widget.CollapsingToolbarLayout;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;


import net.akaish.kittyormdemo.menu.MainMenu;

import static net.akaish.kittyormdemo.menu.MainMenu.LESSON_ID;

/**
 * A fragment representing a single Lesson detail screen.
 * This fragment is either contained in a {@link LessonListActivity}
 * in two-pane mode (on tablets) or a {@link LessonDetailActivity}
 * on handsets.
 * @author akaish (Denis Bogomolov)
 */
public class LessonDetailFragment extends Fragment {


    /**
     * Menu item
     */
    private MainMenu.LessonItem lessonItem;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public LessonDetailFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments().containsKey(LESSON_ID)) {
            // Load the dummy contentFragment specified by the fragment
            // arguments. In a real-world scenario, use a Loader
            // to load contentFragment from a contentFragment provider.
            lessonItem = MainMenu.LESSON_ITEMS.get(getArguments().getInt(LESSON_ID));

            Activity activity = this.getActivity();
            Toolbar appBarLayout = (Toolbar) activity.findViewById(R.id.detail_toolbar);
            if (appBarLayout != null) {
                appBarLayout.setTitle(lessonItem.title);
            }
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.lesson_detail, container, false);

        // Show the dummy contentFragment as text in a TextView.
        if (lessonItem != null) {
            //((TextView) rootView.findViewById(R.id.lesson_detail)).setText(lessonItem.details);
        }

        return rootView;
    }
}
