
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

package net.akaish.kittyormdemo.lessons.four;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;

import net.akaish.kittyormdemo.KittyTutorialActivity;
import net.akaish.kittyormdemo.R;
import net.akaish.kittyormdemo.lessons.InAppTutorialSitePathHelper;
import net.akaish.kittyormdemo.lessons.LessonsUriConstants;

import java.util.Locale;

import static net.akaish.kittyormdemo.Constants.LOG_TAG_APPLICATION;
import static net.akaish.kittyormdemo.lessons.LessonsUriConstants.L4_T3_TUTORIAL;

/**
 * Created by akaish on 11.09.18.
 * @author akaish (Denis Bogomolov)
 */

public class Lesson4Tab3Encryption extends Lesson4BaseFragment {

    public Lesson4Tab3Encryption() {}

    private WebView tutorialView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.lesson4_tab3_encrypt, container, false);

        rootView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tutorialView = getView().findViewById(R.id._l4_t3_web_view);
                tutorialView.loadUrl(InAppTutorialSitePathHelper.getPathToLocalizedInAppTutorialSitePage(L4_T3_TUTORIAL, Locale.getDefault().getLanguage(), getContext(), LOG_TAG_APPLICATION));
            }
        });
        ((WebView)rootView.findViewById(R.id._l4_t3_web_view)).loadUrl(InAppTutorialSitePathHelper.getPathToLocalizedInAppTutorialSitePage(L4_T3_TUTORIAL, Locale.getDefault().getLanguage(), getContext(), LOG_TAG_APPLICATION));
        return rootView;
    }

    @Override
    public View.OnClickListener helpFabMenuAction() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((KittyTutorialActivity)getActivity()).showWebViewDialog(LessonsUriConstants.L4_T3_TUTORIAL);
            }
        };
    }

    @Override
    public View.OnClickListener sourceFabMenuAction() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((KittyTutorialActivity)getActivity()).showWebViewDialog(LessonsUriConstants.L4_T3_SOURCE);
            }
        };
    }

    @Override
    protected int snackbarMessageResource() {
        return R.string._l4_t3_snackbar_message;
    }
}
