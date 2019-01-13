
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

package net.akaish.kittyormdemo.lessons.five;

import android.view.MotionEvent;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import net.akaish.kittyormdemo.KittyTutorialActivity;
import net.akaish.kittyormdemo.LessonDetailActivity;
import net.akaish.kittyormdemo.R;
import net.akaish.kittyormdemo.lessons.LessonBaseFragment;
import net.akaish.kittyormdemo.lessons.LessonTabFragmentOnVisibleAction;
import net.akaish.kittyormdemo.lessons.adapters.BasicArrayAdapter;
import net.akaish.kittyormdemo.lessons.three.Lesson3DatatypesAndConstraints;

import java.util.LinkedList;

import static java.text.MessageFormat.format;

/**
 * Created by akaish on 11.09.18.
 * @author akaish (Denis Bogomolov)
 */

public abstract class Lesson5BaseFragment extends LessonBaseFragment implements LessonTabFragmentOnVisibleAction {

    public static final String M1M1TN = "mig.mig_one";
    public static final String M1M2TN = "mig.mig_two";
    public static final String M1M3TN = "mig.mig_three";
    public static final String M1M4TN = "mig.mig_four";

    protected ListView events;
    protected TextView expandedTitle;
    protected String expandeddTitlePattern;

    protected BasicArrayAdapter expandedAdapter;

    protected long eventCounter = 0;

    protected void setUpExpandedList(View rootView, int eventsId, int eventsTitleId, int eventTitleStringPattern) {
        events = (ListView) rootView.findViewById(eventsId);
        expandedTitle = (TextView) rootView.findViewById(eventsTitleId);
        expandeddTitlePattern = getString(eventTitleStringPattern);

        expandedTitle.setText(format(expandeddTitlePattern, eventCounter));

        if(expandedAdapter == null) {
            expandedAdapter = new BasicArrayAdapter(getContext(), new LinkedList<String>());
        }

        events.setAdapter(expandedAdapter);
        events.setOnTouchListener(new View.OnTouchListener() {

            // Setting on Touch Listener for handling the touch inside ScrollView
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                // Disallow the touch request for parent scroll on touch of child view
                v.getParent().requestDisallowInterceptTouchEvent(true);
                return false;
            }
        });
    }

    void addNewEventToExpandedPanel(String eventText) {
        if(events != null && expandedTitle != null && expandeddTitlePattern != null) {
            eventCounter++;
            expandedTitle.setText(format(expandeddTitlePattern, eventCounter));
            expandedAdapter.addItemFirst(format(getString(R.string._l2_t1_expanded_event_pattern), eventCounter, eventText));
            expandedAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onVisible() {

    }

    KittyTutorialActivity getLessonActivity() {
        return ((Lesson5Migrations)getParentFragment()).getLessonActiity();
    }

}
