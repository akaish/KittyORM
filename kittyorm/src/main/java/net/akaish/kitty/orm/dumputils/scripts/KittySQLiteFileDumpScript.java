
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
import net.akaish.kitty.orm.util.KittyNamingUtils;
import net.akaish.kitty.orm.util.KittyUtils;
import net.akaish.kitty.orm.query.KittySQLiteQuery;

import java.io.File;
import java.io.IOException;
import java.text.MessageFormat;
import java.util.Iterator;
import java.util.LinkedList;

import static net.akaish.kitty.orm.util.KittyConstants.EMPTY_STRING;
import static net.akaish.kitty.orm.util.KittyConstants.SQL_COMMENT_START;

/**
 * SQLite scripts file dump implementation
 * Created by akaish on 06.03.18.
 * @author akaish (Denis Bogomolov)
 */
public class KittySQLiteFileDumpScript extends KittySQLiteDumpScript {

    /**
     * File location for SQLite dump
     */
    protected String dumpLocationUri;

    /**
     * Flag that shows if it is not necessary to read dump from file
     * (if dump new than there is no need to read it from filesystem)
     */
    protected boolean newDump;

    protected final Context ctx;

    private static String IE_UNABLE_TO_SAVE = "Unable to save sql dump to File {0}, see exception details!";
    private static String IE_UNABLE_TO_READ = "Unable to read sql dump from File {0}, see exception details!";
    private static String IA_WRONG_AMOUNT_OF_PARAMETERS = "Wrong amount of parameters passed into #readFromDump(Object... parameters), 3 objects are expected!";
    private static String IA_WRONG_PARAMETERS_TYPES = "Wrong parameter types passed into into #readFromDump(Object... parameters), [0] = File [1] = boolean [2] = Context are expected!";

    public KittySQLiteFileDumpScript(String dumpLocationUri, boolean newDump, Context ctx) {
        super(dumpLocationUri, newDump, ctx);
        this.dumpLocationUri = dumpLocationUri;
        this.newDump = newDump;
        this.ctx = ctx;
    }

    /**
     * Saves input string representation of SQLite sql script to specified file (append: false
     * ; create if not exists: true ; makedirs: false)
     *
     * @param sqlScript sqlite script to save
     * @throws KittyRuntimeException if errors while saving sql to file
     */
//    @Override
//    public void saveToDump(List<KittySQLiteQuery> sqlScript) {
//
//    }

    /**
     * Saves input string representation of SQLite sql script to specified in child location
     *
     * @param sqlScript sqlite script to save
     */
    @Override
    public void saveToDump(LinkedList<KittySQLiteQuery> sqlScript) {
        // TODO check it in future
        LinkedList<String> toWrite = new LinkedList<>();
        String newLine = System.getProperty("line.separator");
        for(KittySQLiteQuery query : sqlScript) {
            String toWriteLine = query.getSql();
            if(toWrite == null || toWrite.size() == 0)
                continue;
            StringBuilder sb = new StringBuilder(128);
            sb.append(toWriteLine).append(newLine);
            toWrite.add(sb.toString());
        }
        try {
            KittyUtils.writeStringsToFile(KittyNamingUtils.getScriptFile(dumpLocationUri, ctx), toWrite, false, true);
        } catch (IOException e) {
            throw new KittyRuntimeException(MessageFormat.format(IE_UNABLE_TO_SAVE, dumpLocationUri), e);
        }
    }

    /**
     * Reads sql dump from specified file to object
     *
     * <br> param[2] Context
     * <br> param[1] boolean new dump flag
     * <br> param[0] File with file dump
     * @param params misc parameters
     * @return string with sql script or null if specified location has no data
     * @throws KittyRuntimeException if errors
     */
    @Override
    public LinkedList<KittySQLiteQuery> readFromDump(Object... params) {
        if(params.length!=3)
            throw new IllegalArgumentException(IA_WRONG_AMOUNT_OF_PARAMETERS);
        if(!(params[1] instanceof Boolean)
                || !(params[0] instanceof String) || !(params[2] instanceof Context)) {
            if(!(params[1] instanceof Boolean)) Log.e("!!!!!!!", "!!!!!!!!");
            if (!(params[0] instanceof String)) Log.e("11111111111111111111", "1111111111111111111");
            if (!(params[2] instanceof Context)) Log.e("@@@@@@@@@@@@@", "@@@@@@@@@@@@@@@@@@@@@@");
            throw new IllegalArgumentException(IA_WRONG_PARAMETERS_TYPES);
        }
        if(((boolean)params[1])) {
            return null;
        } else {
            File scriptFilepath =  KittyNamingUtils.getScriptFile((String) params[0], (Context) params[2]);
            try {
                LinkedList<String> sqlScript = KittyUtils.readFileToLinkedList(scriptFilepath);
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
                throw new KittyRuntimeException(MessageFormat.format(IE_UNABLE_TO_READ, scriptFilepath.getAbsolutePath()), e);
            } catch (SecurityException se) {
                throw new KittyRuntimeException(MessageFormat.format(IE_UNABLE_TO_READ, scriptFilepath.getAbsolutePath()), se);
            }
        }
    }
}
