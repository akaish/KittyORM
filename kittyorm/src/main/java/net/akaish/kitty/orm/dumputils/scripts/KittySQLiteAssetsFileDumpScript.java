
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
 * This file is a part of KittyORM project (KittyORM library), more information at
 * https://akaish.github.io/KittyORMPages/license/
 *
 * ---
 */

package net.akaish.kitty.orm.dumputils.scripts;

import android.content.Context;
import android.util.Log;

import net.akaish.kitty.orm.exceptions.KittyRuntimeException;
import net.akaish.kitty.orm.query.KittySQLiteQuery;
import net.akaish.kitty.orm.util.KittyUtils;

import java.io.IOException;
import java.text.MessageFormat;
import java.util.Iterator;
import java.util.LinkedList;

import static net.akaish.kitty.orm.util.KittyConstants.EMPTY_STRING;
import static net.akaish.kitty.orm.util.KittyConstants.SQL_COMMENT_START;

/**
 * Created by akaish on 22.10.18.
 * @author akaish (Denis Bogomolov)
 */

public class KittySQLiteAssetsFileDumpScript extends KittySQLiteDumpScript {


    private static String IA_UNABLE_TO_SAVE = "Unable to save sql dump to assets location {0}, reason: assets dumps are read-only!";
    private static String IE_UNABLE_TO_READ = "Unable to read sql dump from assets location {0}, see exception details!";
    private static String IA_WRONG_AMOUNT_OF_PARAMETERS = "Wrong amount of parameters passed into #readFromDump(Object... parameters), 2 objects are expected!";
    private static String IA_WRONG_PARAMETERS_TYPES = "Wrong parameter types passed into into #readFromDump(Object... parameters), [0] = String path representation to relative assets file; [1] = Android Context are expected!";

    public KittySQLiteAssetsFileDumpScript(String assetsDumpLocation, Context ctx) {
        super(assetsDumpLocation, ctx);
    }


    /**
     * Saves input string representation of SQLite sql script to specified path
     * <br> Doesn't work in this implementation, because it is impossible to writ to assets
     * @param sqlScript sqlite script to save
     */
    @Override
    public void saveToDump(LinkedList<KittySQLiteQuery> sqlScript) {
        throw new IllegalArgumentException(IA_UNABLE_TO_SAVE);
    }

    /**
     * Reads sql dump from specified file to object
     *
     * @param params misc parameters
     * @return string with sql script or null if specified location has no data
     * @throws KittyRuntimeException if errors
     */
    @Override
    public LinkedList<KittySQLiteQuery> readFromDump(Object... params) {
        if(params.length!=2)
            throw new IllegalArgumentException(IA_WRONG_AMOUNT_OF_PARAMETERS);
        if(!(params[0] instanceof String) || !(params[1] instanceof Context)) {
            throw new IllegalArgumentException(IA_WRONG_PARAMETERS_TYPES);
        }
        String assetsDumpLocation = (String) params[0];
        Context ctx = (Context) params[1];

        try {
            LinkedList<String> sqlScript = KittyUtils.readFileFromAssetsToLinkedList(ctx, (String) params[0]);
            if (sqlScript == null) return null;
            if (sqlScript.size() == 0) return null;
            LinkedList<KittySQLiteQuery> outQueries = new LinkedList<>();
            Iterator<String> queryIterator = sqlScript.iterator();
            while (queryIterator.hasNext()) {
                String query = queryIterator.next().trim();
                if(query.startsWith(SQL_COMMENT_START)) continue; // skip comments
                if(query.trim().equals(EMPTY_STRING)) continue; // skip empty strings
                outQueries.add(new KittySQLiteQuery(query, null));
            }
            return outQueries;
        } catch (IOException e) {
            throw new KittyRuntimeException(MessageFormat.format(IE_UNABLE_TO_READ, assetsDumpLocation), e);
        } catch (SecurityException se) {
            throw new KittyRuntimeException(MessageFormat.format(IE_UNABLE_TO_READ, assetsDumpLocation), se);
        }

    }
}

