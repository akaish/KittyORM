
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

package net.akaish.kittyormdemo.lessons.two;

import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import net.akaish.kitty.orm.query.conditions.SQLiteCondition;
import net.akaish.kitty.orm.query.conditions.SQLiteConditionBuilder;
import net.akaish.kitty.orm.query.conditions.SQLiteOperator;
import net.akaish.kittyormdemo.KittyTutorialActivity;
import net.akaish.kittyormdemo.R;
import net.akaish.kittyormdemo.lessons.adapters.BasicArrayAdapter;
import net.akaish.kittyormdemo.lessons.LessonBaseFragment;
import net.akaish.kittyormdemo.lessons.LessonTabFragmentOnVisibleAction;
import net.akaish.kittyormdemo.sqlite.basicdb.BasicDatabase;
import net.akaish.kittyormdemo.sqlite.basicdb.RandomMapper;
import net.akaish.kittyormdemo.sqlite.basicdb.RandomModel;

import java.util.LinkedList;

import static java.text.MessageFormat.format;

/**
 * Created by akaish on 28.08.18.
 * @author akaish (Denis Bogomolov)
 */

public abstract class Lesson2BaseFragment extends LessonBaseFragment implements LessonTabFragmentOnVisibleAction {

    protected ArrayAdapter<String> animalAdapter;
    protected Spinner animalSpinner;

    protected ListView events;
    protected TextView expandedTitle;
    protected String expandedTitlePattern;

    protected BasicArrayAdapter expandedAdapter;

    protected long eventCounter = 0;

    protected void setAnimalSpinner(View rootView, int spinnerId, AdapterView.OnItemSelectedListener onItemSelectedListener) {
        animalSpinner = (Spinner) rootView.findViewById(spinnerId);
        animalAdapter = newAnimalAdapter();
        animalSpinner.setAdapter(animalAdapter);
        animalSpinner.setSelection(animalAdapter.getCount()); //display hint
        animalSpinner.setOnItemSelectedListener(onItemSelectedListener);
    }

    protected void setUpExpandedList(View rootView, int eventsId, int eventsTitleId, int eventTitleStringPattern) {
        events = (ListView) rootView.findViewById(eventsId);
        expandedTitle = (TextView) rootView.findViewById(eventsTitleId);
        expandedTitlePattern = getString(eventTitleStringPattern);

        expandedTitle.setText(format(expandedTitlePattern, eventCounter));

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
        if(events != null && expandedTitle != null && expandedTitlePattern != null) {
            eventCounter++;
            expandedTitle.setText(format(expandedTitlePattern, eventCounter));
            expandedAdapter.addItemFirst(format(getString(R.string._l2_t1_expanded_event_pattern), eventCounter, eventText));
            expandedAdapter.notifyDataSetChanged();
        }
    }

    protected ArrayAdapter<String> newAnimalAdapter() {
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_spinner_dropdown_item) {

            @Override
            public View getView(int position, View convertView, ViewGroup parent) {

                View v = super.getView(position, convertView, parent);
                if (position == getCount()) {
                    ((TextView)v.findViewById(android.R.id.text1)).setText("");
                    ((TextView)v.findViewById(android.R.id.text1)).setHint(getItem(getCount())); //"Hint to be displayed"
                }

                return v;
            }

            @Override
            public int getCount() {
                return super.getCount()-1;
            }

        };

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        String[] adapterStrings = getContext().getResources().getStringArray(R.array.animal_enum);
        for(int i = 0; i < adapterStrings.length; i++) {
            adapter.add(adapterStrings[i]);
        }
        adapter.add(getContext().getString(R.string._l2_t1_random_animal_hint));
        return adapter;
    }

    boolean isLoadedModelExists() {
        if(getLoadedModelId() == null)
            return false;
        SQLiteCondition condition = new SQLiteConditionBuilder()
                                        .addField("id")
                                        .addSQLOperator(SQLiteOperator.EQUAL)
                                        .addValue(getLoadedModelId())
                                        .build();
        RandomMapper mapper = getMapper();
        boolean isExists = mapper.countWhere(condition) == 1;
        mapper.close();
        return isExists;
    }

    void setLoadedModelId(Long modelId) {
        ((Lesson2KittyORMBasicImplementation)getParentFragment()).setLoadedModelId(modelId);
    }

    Long getLoadedModelId() {
        return ((Lesson2KittyORMBasicImplementation)getParentFragment()).getLoadedModelId();
    }

    BasicDatabase getDb() {
        return ((Lesson2KittyORMBasicImplementation)getParentFragment()).getDb();
    }

    KittyTutorialActivity getLessonActivity() {
        return ((Lesson2KittyORMBasicImplementation)getParentFragment()).getLessonActiity();
    }

    @Override
    public void onVisible() {
        // do nothing
    }

    RandomMapper getMapper() {
        return (RandomMapper) getDb().getMapper(RandomModel.class);
    }

}
