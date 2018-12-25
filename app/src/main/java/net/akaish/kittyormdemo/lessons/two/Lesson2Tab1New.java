
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

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import net.akaish.kitty.orm.KittyMapper;
import net.akaish.kittyormdemo.KittyTutorialActivity;
import net.akaish.kittyormdemo.R;
import net.akaish.kittyormdemo.lessons.LessonsUriConstants;
import net.akaish.kittyormdemo.sqlite.basicdb.BasicDatabase;
import net.akaish.kittyormdemo.sqlite.basicdb.RandomMapper;
import net.akaish.kittyormdemo.sqlite.basicdb.RandomModel;
import net.akaish.kittyormdemo.sqlite.introductiondb.util.RandomSimpleExampleModelUtil;
import net.akaish.kittyormdemo.sqlite.misc.Animals;

import static java.text.MessageFormat.format;

/**
 * Created by akaish on 03.08.18.
 * @author akaish (Denis Bogomolov)
 */

public class Lesson2Tab1New extends Lesson2BaseFragment {

    private BasicDatabase db;

    private EditText randomIntET;
    private EditText randomIntegerET;

    private EditText randomAnimalNameET;
    private EditText randomAnimalSaysET;



    private Button saveButton;


    public Lesson2Tab1New() {};

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.lesson2_tab1_new, container, false);


        randomAnimalNameET = (EditText) rootView.findViewById(R.id.l2_t1_et_animal_localised_name);
        randomAnimalSaysET = (EditText) rootView.findViewById(R.id.l2_t1_et_animal_says);

        randomIntET = (EditText) rootView.findViewById(R.id.l2_t1_et_random_int);
        randomIntegerET = (EditText) rootView.findViewById(R.id.l2_t1_et_random_integer);



        saveButton = (Button) rootView.findViewById(R.id._l2_t1_save_button);
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                go();
            }
        });

        setUpExpandedList(
                rootView,
                R.id._l2_t1_expanded_panel_lw,
                R.id._l2_t1_expanded_panel_text,
                R.string._l2_t1_expanded_panel_text_pattern
        );

        setAnimalSpinner(rootView, R.id.l2_t1_spinner, new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String enumStringName = (String) animalSpinner.getAdapter().getItem(position);
                if(enumStringName.equals(animalAdapter.getItem(animalAdapter.getCount()))) {
                    // do nothing, skip spinner hint
                } else {
                    Animals animal = Animals.valueOf(enumStringName);
                    randomAnimalNameET.setText(Animals.getLocalizedAnimalNameResource(animal));
                    randomAnimalSaysET.setText(Animals.getLocalizedAnimalSaysResource(animal));
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


        getDb().printPregeneratedCreateSchemaToLog("KittyORM BDB C");
        getDb().printPregeneratedDropSchemaToLog("KittyORM BDB D");

        return rootView;
    }

    void textWarnDialog() {
        getLessonActivity().showWarningDialog(R.string._warning_dialog_title, R.string._warning_dialog_test_message_big, R.string._warning_dialog_ok_button_text);
    }

    void go() {
        String randomInt = randomIntET.getText().toString();
        String randomInteger = randomIntegerET.getText().toString();
        String animalEnumStringValue = animalSpinner.getSelectedItem().toString();

        if(animalEnumStringValue.equals(animalAdapter.getItem(animalAdapter.getCount()))) {
            getLessonActivity().showWarningDialog(R.string._warning_dialog_title, R.string._l2_t1_warning_text, R.string._warning_dialog_ok_button_text);
            return;
        }
        if(randomInt == null || randomInteger == null) {
            getLessonActivity().showWarningDialog(R.string._warning_dialog_title, R.string._l2_t1_warning_text, R.string._warning_dialog_ok_button_text);
            return;
        }
        if(randomInt.length() == 0 || randomInteger.length() == 0) {
            getLessonActivity().showWarningDialog(R.string._warning_dialog_title, R.string._l2_t1_warning_text, R.string._warning_dialog_ok_button_text);
            return;
        }
        int rndInt = 0; Integer rndInteger = null;
        try {
            rndInt = Integer.parseInt(randomInt);
            rndInteger = Integer.valueOf(randomInteger);
        } catch (Exception e) {
            getLessonActivity().showWarningDialog(R.string._warning_dialog_title, R.string._l2_t1_warning_bad_input, R.string._warning_dialog_ok_button_text);
            return;
        }
        RandomModel toInsert = new RandomModel();
        toInsert.randomInt = rndInt;
        toInsert.randomInteger = rndInteger;
        Animals animal = Animals.valueOf(animalEnumStringValue);
        toInsert.randomAnimal = animal;
        toInsert.randomAnimalName = getString(Animals.getLocalizedAnimalNameResource(animal));
        toInsert.randomAnimalSays = getString(Animals.getLocalizedAnimalSaysResource(animal));
        KittyMapper mapper = getMapper();
        long rowid = mapper.insert(toInsert);
        if(rowid > 0) {
            addNewEventToExpandedPanel(format(getString(R.string._l2_t1_expanded_added), rowid, toInsert, mapper.countAll()));
        } else {
            addNewEventToExpandedPanel(format(getString(R.string._l2_t1_expanded_error), toInsert));
        }
        mapper.close();
    }

    @Override
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
    }

    // Fab menu section

    @Override
    public View.OnClickListener helpFabMenuAction() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((KittyTutorialActivity)getActivity()).showWebViewDialog(LessonsUriConstants.L2_T1_TUTORIAL);
            }
        };
    }

    @Override
    public View.OnClickListener sourceFabMenuAction() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((KittyTutorialActivity)getActivity()).showWebViewDialog(LessonsUriConstants.L2_T1_SOURCE);
            }
        };
    }

    @Override
    public View.OnClickListener schemaFabMenuAction() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((KittyTutorialActivity)getActivity()).showWebViewDialog(LessonsUriConstants.L2_T1_SCHEMA);
            }
        };
    }

    @Override
    protected int snackbarMessageResource() {
        return R.string._l2_t1_snackbar_message;
    }
}
