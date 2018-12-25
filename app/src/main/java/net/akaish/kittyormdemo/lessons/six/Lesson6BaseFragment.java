
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

package net.akaish.kittyormdemo.lessons.six;

import android.support.design.widget.Snackbar;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import net.akaish.kittyormdemo.KittyTutorialActivity;
import net.akaish.kittyormdemo.R;
import net.akaish.kittyormdemo.fabmenu.FabOnClickListenerFabMenuStateSpecific;
import net.akaish.kittyormdemo.lessons.FabContainer;
import net.akaish.kittyormdemo.lessons.LessonBaseFragment;
import net.akaish.kittyormdemo.lessons.LessonTabFragmentOnVisibleAction;
import net.akaish.kittyormdemo.lessons.LessonsUriConstants;
import net.akaish.kittyormdemo.lessons.adapters.BasicArrayAdapter;

import java.util.LinkedList;

import static java.text.MessageFormat.format;

/**
 * Created by akaish on 11.09.18.
 * @author akaish (Denis Bogomolov)
 */

public abstract class Lesson6BaseFragment extends LessonBaseFragment implements LessonTabFragmentOnVisibleAction {

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

    @Override
    public View.OnClickListener helpFabMenuAction() {
        return null;
    }

    @Override
    public View.OnClickListener sourceFabMenuAction() {
        return null;
    }

    @Override
    public View.OnClickListener schemaFabMenuAction() {
        return null;
    }

    public View.OnClickListener useFabMenuAction() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((KittyTutorialActivity)getActivity()).fireWebIntent(LessonsUriConstants.L6_TRY_URI);
            }
        };
    }

    public View.OnClickListener shareFabMenuAction() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((KittyTutorialActivity)getActivity()).fireShareLinkIntent(
                        LessonsUriConstants.L6_SHARE,
                        getString(R.string._menu_share_link_subject),
                        getString(R.string._menu_share_link_chooser_title)
                );
            }
        };
    }

    public View.OnClickListener developFabMenuAction() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((KittyTutorialActivity)getActivity()).fireWebIntent(LessonsUriConstants.L6_DEVELOP);
            }
        };
    }

    public View.OnClickListener donateFabMenuAction() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((KittyTutorialActivity)getActivity()).fireWebIntent(LessonsUriConstants.L6_SPONSOR);
            }
        };
    }

    @Override
    public int getFragmentSpecificFabLayoutResource() {
        return R.layout.make_kitty_great_tab_fab;
    }

    @Override
    public LinkedList<FabContainer> getFragmentSpecificFabOnClickListeners() {
        LinkedList<FabContainer> out = new LinkedList<>();
        out.addLast(new FabContainer(0, R.id.make_kitty_great_fab, regularHostFabAction()));

        out.addLast(new FabContainer(1, R.id.donate_fab, donateFabMenuAction()));
        out.addLast(new FabContainer(2, R.id.commit_fab, developFabMenuAction()));
        out.addLast(new FabContainer(3, R.id.share_fab, shareFabMenuAction()));
        out.addLast(new FabContainer(4, R.id.use_fab, useFabMenuAction()));
        return out;
    }

    @Override
    public LinkedList<Integer> getFragmentSpecificListOfFabsLayouts() {
        LinkedList<Integer> out = new LinkedList<>();

        out.addLast(R.id.donate_fab_layout);
        out.addLast(R.id.commit_fab_layout);
        out.addLast(R.id.share_fab_layout);
        out.addLast(R.id.use_fab_layout);
        return out;
    }
}

