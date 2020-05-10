
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

import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.View;

import net.akaish.kittyormdemo.KittyTutorialActivity;
import net.akaish.kittyormdemo.R;
import net.akaish.kittyormdemo.fabmenu.FabOnClickListenerFabMenuStateSpecific;

import java.util.LinkedList;

/**
 * Created by akaish on 08.08.18.
 * @author akaish (Denis Bogomolov)
 */

public abstract class RegularLessonBaseFragment extends Fragment implements LessonFabBaseFragment {

    @Override
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
    }

    Snackbar snackbar;

    public abstract View.OnClickListener helpFabMenuAction();

    public abstract View.OnClickListener sourceFabMenuAction();

    public abstract View.OnClickListener schemaFabMenuAction();

    protected abstract int snackbarMessageResource();

    protected View.OnClickListener regularHostFabAction() {
        return new FabOnClickListenerFabMenuStateSpecific() {

            @Override
            public void onClick(View v) {
                if (!isFabMenuOpen)
                    ((KittyTutorialActivity) getActivity()).showSnackbar(snackbarMessageResource());
                else
                    ((KittyTutorialActivity) getActivity()).dismissSnackbarIfOpen();
            }
        };
    }

    public void dismissSnackbarIfOpen() {
        if(snackbar != null)
            snackbar.dismiss();
    }

    @Override
    public int getFragmentSpecificFabLayoutResource() {
        return R.layout.regular_lesson_tab_fab;
    }

    @Override
    public LinkedList<FabContainer> getFragmentSpecificFabOnClickListeners() {
        LinkedList<FabContainer> out = new LinkedList<>();
        //((KittyTutorialActivity) getActivity()).dismissSnackbarIfOpen();
        out.addLast(new FabContainer(0, R.id.regular_lesson_fab, regularHostFabAction()));

        out.addLast(new FabContainer(1, R.id.regular_lesson_fab2_sourcecode, sourceFabMenuAction()));
        out.addLast(new FabContainer(2, R.id.regular_lesson_fab3_dbschema, schemaFabMenuAction()));
        out.addLast(new FabContainer(3, R.id.regular_lesson_fab1_help, helpFabMenuAction()));
        return out;
    }

    @Override
    public LinkedList<Integer> getFragmentSpecificListOfFabsLayouts() {
        LinkedList<Integer> out = new LinkedList<>();

        out.addLast(R.id.regular_lesson_fab2_layout);
        out.addLast(R.id.regular_lesson_fab3_layout);
        out.addLast(R.id.regular_lesson_fab1_layout);
        return out;
    }

}
