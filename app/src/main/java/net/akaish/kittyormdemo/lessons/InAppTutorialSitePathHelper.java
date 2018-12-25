
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

package net.akaish.kittyormdemo.lessons;

import android.content.Context;
import android.util.Log;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Locale;

import static java.text.MessageFormat.format;

/**
 * Used for providing android assets uri string to localized pages of inApp tutorial site.
 * Created by akaish on 21.08.18.
 * Locale.getDefault().getLanguage()       ---> en
 * Locale.getDefault().getISO3Language()   ---> eng
 * Locale.getDefault().getCountry()        ---> US
 * Locale.getDefault().getISO3Country()    ---> USA
 * Locale.getDefault().getDisplayCountry() ---> United States
 * Locale.getDefault().getDisplayName()    ---> English (United States)
 * Locale.getDefault().toString()          ---> en_US
 * Locale.getDefault().getDisplayLanguage()---> English
 * @author akaish (Denis Bogomolov)
 */
public class InAppTutorialSitePathHelper {

    private static String LOG_UNABLE_TO_FIND_DEFAULT_PATH = "Unable to find requested path for inApp tutorial site (default locale en requested), requested path: ";
    private static String LOG_UNABLE_TO_FIND_REQUESTED_PATH = "Unable to find requested path for inApp tutorial site (locale {0} requested), requested path: {1} , switching to default locale!";

    public static final String ANDROID_ASSETS = "file:///android_asset/";
    public static final String IN_APP_SITE_DIRECTORY = "inapptut/";
    public static final String LESSONS_DIRECTORY = "lessons/";
    public static final String INDEX_FILE_NAME = "index.html";

    public static final String EN = "en";

    /**
     * Returns android assets uri string to localized pages of inApp tutorial site. If unable to return
     * requested page in requested language and in default language (en) null would be returned.
     * @param pageName requested page name
     * @param twoLiteralLanguageCode language code acquired with getLanguage() method of {@link Locale#getDefault()}
     * @param ctx application contec
     * @param logTag logtag, may me null (if null - no logging)
     * @return
     */
    public static final String getPathToLocalizedInAppTutorialSitePage(String pageName, String twoLiteralLanguageCode, Context ctx, String logTag) {
        StringBuilder androidAssetUri = new StringBuilder(64).append(ANDROID_ASSETS);
        StringBuilder htmlFileLocation = new StringBuilder(64);
        htmlFileLocation.append(IN_APP_SITE_DIRECTORY);
        if(!twoLiteralLanguageCode.equalsIgnoreCase(EN))
            htmlFileLocation.append(twoLiteralLanguageCode.toLowerCase()).append(File.separatorChar);
        htmlFileLocation
                .append(LESSONS_DIRECTORY)
                .append(pageName)
                .append(File.separatorChar)
                .append(INDEX_FILE_NAME);
        try {
            InputStream is = ctx.getAssets().open(htmlFileLocation.toString());
            is.close();
            return androidAssetUri.append(htmlFileLocation.toString()).toString();
        } catch (IOException e) {
            if(twoLiteralLanguageCode.equalsIgnoreCase(EN)) {
                if (logTag != null) {
                    Log.e(logTag, LOG_UNABLE_TO_FIND_DEFAULT_PATH+pageName, e);
                }
                return null;
            } else {
                if (logTag != null) {
                    Log.d(logTag, format(LOG_UNABLE_TO_FIND_REQUESTED_PATH, twoLiteralLanguageCode, pageName), e);
                }
                htmlFileLocation = new StringBuilder(64);
                htmlFileLocation
                        .append(IN_APP_SITE_DIRECTORY)
                        .append(LESSONS_DIRECTORY)
                        .append(pageName)
                        .append(File.separatorChar)
                        .append(INDEX_FILE_NAME);
                try {
                    InputStream is = ctx.getAssets().open(htmlFileLocation.toString());
                    is.close();
                    return androidAssetUri.append(htmlFileLocation.toString()).toString();
                } catch (IOException e2) {
                    if (logTag != null) {
                        Log.e(logTag, LOG_UNABLE_TO_FIND_DEFAULT_PATH+pageName, e);
                    }
                    return null;
                }
            }
        }
    }
}
