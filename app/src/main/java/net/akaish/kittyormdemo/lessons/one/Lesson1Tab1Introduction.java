
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

package net.akaish.kittyormdemo.lessons.one;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;

import net.akaish.kittyormdemo.KittyTutorialActivity;
import net.akaish.kittyormdemo.R;
import net.akaish.kittyormdemo.lessons.InAppTutorialSitePathHelper;
import net.akaish.kittyormdemo.lessons.LessonBaseFragment;
import net.akaish.kittyormdemo.lessons.LessonTabFragmentOnVisibleAction;

import java.util.Locale;

import static net.akaish.kittyormdemo.Constants.LOG_TAG_APPLICATION;
import static net.akaish.kittyormdemo.lessons.LessonsUriConstants.L1_T1_SCHEMA;
import static net.akaish.kittyormdemo.lessons.LessonsUriConstants.L1_T1_SOURCE;
import static net.akaish.kittyormdemo.lessons.LessonsUriConstants.L1_T1_TUTORIAL;

/**
 * Created by akaish on 21.08.18.
 * @author akaish (Denis Bogomolov)
 */

public class Lesson1Tab1Introduction extends LessonBaseFragment implements LessonTabFragmentOnVisibleAction {

    private WebView intro;


    public Lesson1Tab1Introduction() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.lesson1_tab1_intro, container, false);

        rootView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                intro = getView().findViewById(R.id._l1_t1_web_view);
                intro.loadUrl(InAppTutorialSitePathHelper.getPathToLocalizedInAppTutorialSitePage(L1_T1_TUTORIAL, Locale.getDefault().getLanguage(), getContext(), LOG_TAG_APPLICATION));
            }
        });
        ((WebView)rootView.findViewById(R.id._l1_t1_web_view)).loadUrl(InAppTutorialSitePathHelper.getPathToLocalizedInAppTutorialSitePage(L1_T1_TUTORIAL, Locale.getDefault().getLanguage(), getContext(), LOG_TAG_APPLICATION));
        //intro.loadUrl(InAppTutorialSitePathHelper.getPathToLocalizedInAppTutorialSitePage(tutorialPageName, Locale.getDefault().getLanguage(), getContext(), LOG_TAG_APPLICATION));
        return rootView;
    }

    @Override
    public void onStart() {
        super.onStart();

    }

    // Fab menu section

    @Override
    public View.OnClickListener helpFabMenuAction() {
        return new View.OnClickListener() {

            /**
             * Called when a view has been clicked.
             *
             * @param v The view that was clicked.
             */
            @Override
            public void onClick(View v) {
                ((KittyTutorialActivity) getParentFragment().getActivity()).showWebViewDialog(L1_T1_TUTORIAL);
            }
        };
    }

    @Override
    public View.OnClickListener sourceFabMenuAction() {
        return new View.OnClickListener() {

            /**
             * Called when a view has been clicked.
             *
             * @param v The view that was clicked.
             */
            @Override
            public void onClick(View v) {
                ((KittyTutorialActivity) getParentFragment().getActivity()).showWebViewDialog(L1_T1_SOURCE);
            }
        };
    }

    @Override
    public View.OnClickListener schemaFabMenuAction() {
        return new View.OnClickListener() {

            /**
             * Called when a view has been clicked.
             *
             * @param v The view that was clicked.
             */
            @Override
            public void onClick(View v) {
                ((KittyTutorialActivity) getParentFragment().getActivity()).showWebViewDialog(L1_T1_SCHEMA);
            }
        };
    }

    @Override
    protected int snackbarMessageResource() {
        return R.string._l1_t1_snackbar_message;
    }

    @Override
    public void onVisible() {

    }
}
