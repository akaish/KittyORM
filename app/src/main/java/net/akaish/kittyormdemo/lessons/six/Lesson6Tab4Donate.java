
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

package net.akaish.kittyormdemo.lessons.six;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;

import net.akaish.kittyormdemo.R;
import net.akaish.kittyormdemo.lessons.InAppTutorialSitePathHelper;

import java.util.Locale;

import static net.akaish.kittyormdemo.Constants.LOG_TAG_APPLICATION;
import static net.akaish.kittyormdemo.lessons.LessonsUriConstants.L6_T4_WV;

/**
 * Created by akaish on 11.09.18.
 * @author akaish (Denis Bogomolov)
 */

public class Lesson6Tab4Donate  extends Lesson6BaseFragment {

    public Lesson6Tab4Donate() {}

    private WebView tutorialView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.lesson6_tab4_sponsor, container, false);

        rootView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tutorialView = getView().findViewById(R.id._l6_t4_web_view);
                tutorialView.loadUrl(InAppTutorialSitePathHelper.getPathToLocalizedInAppTutorialSitePage(L6_T4_WV, Locale.getDefault().getLanguage(), getContext(), LOG_TAG_APPLICATION));
            }
        });
        ((WebView)rootView.findViewById(R.id._l6_t4_web_view)).loadUrl(InAppTutorialSitePathHelper.getPathToLocalizedInAppTutorialSitePage(L6_T4_WV, Locale.getDefault().getLanguage(), getContext(), LOG_TAG_APPLICATION));
        return rootView;
    }

    @Override
    protected int snackbarMessageResource() {
        return R.string._l6_t4_snackbar_message;
    }
}