
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
 * This file is a part of KittyORM project (KittyORM library), more information at
 * https://akaish.github.io/KittyORMPages/license/
 *
 * ---
 */

package net.akaish.kitty.orm.util;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.util.Log;

import net.akaish.kitty.orm.KittyMapper;
import net.akaish.kitty.orm.KittyModel;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Modifier;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;

import dalvik.system.DexFile;

/**
 * Class for getting classes list for Android application
 * <br> I like Android, it gives you pointless challenges, you can't just get system class loader
 * <br> No, it would be too easy, you have scan application directories and seek for classes yourself
 * <br> Why? Cause why not. I'm not addicted, getContext().getClassLoader() useless on new devices.
 * <br> Thanks to <a href=https://stackoverflow.com/questions/26623905/android-multidex-list-all-classes/26892658#26892658>StackOverflow: android-multidex-list-all-classes xudshen's answer</a>
 * for all paths where classes can be located
 * Created by akaish on 14.02.18.
 * @author akaish (Denis Bogomolov)
 */
public class KittyDexUtils {

    // Extensions and SharedPreferences file and key for MultiDex
    private final static String CLASSES_EXT = ".classes";
    private final static String TEMP_EXT = ".tmp";
    private final static String ZIP_EXT = ".zip";
    private final static String MDEX_SPREFS_VERSION_FILE = "multidex.version";
    private final static String DEX_NUMBER_SPREFS = "dex.number";

    // Shared preferences stuff
    private static int HONEYCOMB = Build.VERSION_CODES.HONEYCOMB;

    private static int PREFS_MODE = Context.MODE_PRIVATE;

    static {
        if(Build.VERSION.SDK_INT >= HONEYCOMB) {
            try {
                PREFS_MODE = Context.MODE_MULTI_PROCESS;
            } finally {}
        }
    }

    // Section for additional sources paths
    private static StringBuffer sb;

    static {
        sb = new StringBuffer(32);
        sb.append("files").append(File.separator).append("instant-run").append(File.separator).append("dex").append(File.separator);
    }

    private final static String INSTANT_RUN_DIRECTORY = sb.toString();

    static {
        sb = new StringBuffer(32);
        sb.append("code_cache").append(File.separator).append("secondary-dexes");
    }

    private final static String SECONDARY_DIRECTORY = sb.toString();

    // Exception patterns section
    private static final int MAX_LOG_TAG_LENGTH = 23;
    private static final String CONTEXT_NULL_IAEXCEPTION = "Context passed in KittyDexUtils#getInstance(Context context) is NULL!";
    private static final String LOG_TAG_TOO_LONG_IAEXCEPTION = "Log value can not be longer than 23 chars, but passed logTag ({0}) has length {1}!";
    private static final String LOG_LEVEL_IAEXCEPTION = "Log Level can be equal only following values: KittyDexUtils#LOG_LEVEL_INFO (int 1), KittyDexUtils#LOG_LEVEL_WARNING (int 2) or KittyDexUtils#LOG_LEVEL_ERROR (int 3) but value {0} found!";
    private static final String UNABLE_TO_LOAD_DEX_IOEXCEPTION = "Unable to load dex file located at {0}!";
    private static final String UNABLE_TO_GET_SOURCES_PATH_EXCEPTION = "Unable to get dex sources paths, got {0} exception!";

    // Log patterns section
    private static final String LOG_I_ADDED_DEX_LOCATION = "Added dex path at {0}!";
    private static final String LOG_I_PROCESSING_CLASS = "Processing class {0} from path {1}!";
    private static final String LOG_W_CLASS_FOUND_BUT_FAILED_TO_LOAD = "Class {0} was found at file {1}, but failed to be loaded by system loader!";

    // Double check singleton stuff
    private static final Object lock = new Object();
    private static volatile KittyDexUtils instance;

    // Common filters
    /**
     * Only regular classes filter
     */
    public static final KittyDexClassFilter ONLY_REGULAR_CLASSES_FILTER =
            new KittyDexClassFilter(true, true, true, true,
                    true, true, true, true,
                    true, null, null);

    public static final KittyDexClassNameFilter NO_ANDROID_CLASSES_FILTER =
            new KittyDexClassNameFilter(null, new String[]{"android.", "com.google"});

    /**
     * TODO change to one more filter in future releases
     */
    public static final Class[] CHILD_CLASSES_TO_SEEK = {KittyModel.class, KittyMapper.class};

    private final Context context;
    private final List<Class> appClasses;

    public static int LOG_LEVEL_INFO = 1;
    public static int LOG_LEVEL_WARNING = 2;
    public static int LOG_LEVEL_ERROR = 3;

    public static final String DEFAULT_LOG_TAG = "KittyDexUtils";

    private KittyDexClassNameFilter classNameFilter = NO_ANDROID_CLASSES_FILTER;

    private boolean skipExceptions = false;
    private boolean logOn = false;
    private String lTag = DEFAULT_LOG_TAG;
    private int logLevel = LOG_LEVEL_INFO;

    private KittyDexUtils(Context ctx) {
        if(ctx == null) throw new IllegalArgumentException(CONTEXT_NULL_IAEXCEPTION);
        context = ctx;
        appClasses = getAllClasses(skipExceptions, logOn, lTag, logLevel);
    }

    private KittyDexUtils(Context ctx, KittyDexClassNameFilter filter, boolean skipExceptions, boolean logOn, String lTag, int logLevel) {
        if(ctx == null) throw new IllegalArgumentException(CONTEXT_NULL_IAEXCEPTION);
        context = ctx;
        setClassNameFilter(filter);
        setSkipExceptions(skipExceptions);
        setLogOn(logOn);
        setlTag(lTag);
        setLogLevel(logLevel);

        appClasses = getAllClasses(skipExceptions, logOn, lTag, logLevel);
    }

    private void setClassNameFilter(KittyDexClassNameFilter filter) {classNameFilter = filter;}

    private void setSkipExceptions(boolean skipExceptions) {
        this.skipExceptions = skipExceptions;
    }

    private void setLogOn(boolean logOn) {
        this.logOn = logOn;
    }

    private void setlTag(String lTag) {
        if(lTag.length()>MAX_LOG_TAG_LENGTH)
            throw new IllegalArgumentException(MessageFormat.format(LOG_TAG_TOO_LONG_IAEXCEPTION, lTag, Integer.toString(lTag.length())));
        this.lTag = lTag;
    }

    private void setLogLevel(int logLevel) {
        if(logLevel>LOG_LEVEL_ERROR || logLevel<LOG_LEVEL_INFO)
            throw new IllegalArgumentException(MessageFormat.format(LOG_LEVEL_IAEXCEPTION, Integer.toString(logLevel)));
        this.logLevel = logLevel;
    }

    /**
     * Double check singleton get instance for getting {@link KittyDexUtils} instance
     * <br> Would be creating instance with following parameters
     * <br> <b>KittyDexClassNameFilter filter</b> = {@link #NO_ANDROID_CLASSES_FILTER}
     * <br> <b>skipExceptions</b> = false
     * <br> <b>logOn</b> = false
     * <br> <b>lTag</b> = {@link #DEFAULT_LOG_TAG}
     * <br> <b>logLevel</b> = {@link #LOG_LEVEL_INFO}
     * @param ctx application's context, can't be null
     * @return
     */
    public static KittyDexUtils getInstance(Context ctx) {
        KittyDexUtils r = instance;
        if (r == null) {
            synchronized (lock) {
                r = instance;
                if (r == null) {
                    r = new KittyDexUtils(ctx);
                    instance = r;
                }
            }
        }
        return r;
    }

    /**
     * Double check singleton get instance for getting parametrized {@link KittyDexUtils} instance
     * @param ctx application's context, can't be null
     * @param filter class filename filter, can be null, but it is highly recommended to set
     *               it to something sensible, cause without filter it would be created all classes of
     *               all android classes packaged in application, so use either default filter ({@link #NO_ANDROID_CLASSES_FILTER})
     *               or create your own instance of {@link KittyDexClassNameFilter} that would suit your needs
     * @param skipExceptions with this flag no predicted exceptions would be thrown except unpredicted ones and {@link IllegalArgumentException}
     *                      on bad parameters passed into {@link #getInstance(Context, KittyDexClassNameFilter, boolean, boolean, String, int)}
     * @param logOn logging flag
     * @param lTag log tag, must not be longer 23 chars length
     * @param logLevel log level, only three log levels are supported: {@link #LOG_LEVEL_INFO}, {@link #LOG_LEVEL_WARNING} and {@link #LOG_LEVEL_ERROR}
     * @return instance of {@link KittyDexUtils}
     */
    public static KittyDexUtils getInstance(Context ctx, KittyDexClassNameFilter filter, boolean skipExceptions, boolean logOn,
                                            String lTag, int logLevel) {
        KittyDexUtils r = instance;
        if (r == null) {
            synchronized (lock) {
                r = instance;
                if (r == null) {
                    r = new KittyDexUtils(ctx, filter, skipExceptions, logOn, lTag, logLevel);
                    instance = r;
                }
            }
        }
        return r;
    }

    /**
     *  Returns filepathes where dex can be located
     * @return
     * @throws PackageManager.NameNotFoundException
     * @throws IOException
     */
    private File[] getSourcePaths() throws PackageManager.NameNotFoundException, IOException {
        // Setting paths for dex directories
        ApplicationInfo applicationInfo = context.getApplicationInfo();
        File apkDirectory = new File(applicationInfo.sourceDir);
        File dexDirectory = new File(applicationInfo.dataDir, SECONDARY_DIRECTORY);
        File instantRunDirectory = new File(applicationInfo.dataDir, INSTANT_RUN_DIRECTORY);
        ArrayList<File> sourcePaths = new ArrayList<File>();
        sourcePaths.add(new File(applicationInfo.sourceDir));
        if (instantRunDirectory.exists()) {
            for(File dexFile : instantRunDirectory.listFiles()) {
                sourcePaths.add(dexFile);
            }
        }
        // Setting prefix for extracted file
        String extractedFilePrefix = apkDirectory.getName() + CLASSES_EXT;
        // Multidex check
        int totalDexNumber = context
                .getSharedPreferences(MDEX_SPREFS_VERSION_FILE, PREFS_MODE)
                .getInt(DEX_NUMBER_SPREFS, 1);
        for (int secondaryNumber = 0; secondaryNumber <= totalDexNumber; secondaryNumber++) {
            StringBuffer fName = new StringBuffer(32);
            fName.append(extractedFilePrefix).append(secondaryNumber).append(ZIP_EXT);
            File extractedFile = new File(dexDirectory, fName.toString());
            if (extractedFile.isFile()) {
                sourcePaths.add(extractedFile);
            }
        }
        return sourcePaths.toArray(new File[sourcePaths.size()]);
    }

    /**
     * Returns filtered list of classes
     * @param filter filter object
     * @param classes list of classes to filter
     * @return filtered list of classes
     */
    public static List<Class> filterClassesList(KittyDexClassFilter filter, List<Class> classes) {
        return filterClassesList(classes, filter.skipEnums, filter.skipAnnotations, filter.skipAbstract,
                filter.skipInterfaces, filter.skipArrays, filter.skipLocalClass, filter.skipMemberClass,
                filter.skipAnonymousClass, filter.skipPrimitives, filter.packageNamesFilter,
                filter.onlyAssignableFromSuperTypes);
    }

    /**
     * Returns list of classes for this Android application
     * @return list of classes for this Android application
     */
    public List<Class> getAppClasses() {
        return appClasses;
    }

    /**
     * Returns list of filtered app classes
     * @param filter
     * @return
     */
    public List<Class> getFilteredAppClasses(KittyDexClassFilter filter) {
        return filterClassesList(filter, appClasses);
    }

    /**
     * Returns provided list filtered with provided filter
     * @param filter
     * @param classesToFilter
     * @return
     */
    public List<Class> filterProvidedClasses(KittyDexClassFilter filter, List<Class> classesToFilter) {
        return  filterClassesList(filter, classesToFilter);
    }

    /**
     * Returns filtered classes list
     * @param classes
     * @param skipEnums
     * @param skipAnnotations
     * @param skipAbstract
     * @param skipInterfaces
     * @param skipArrays
     * @param skipLocalClass
     * @param skipMemberClass
     * @param skipAnonymousClass
     * @param skipPrimitives
     * @param packageNamesFilter
     * @param onlyAssignableFromSuperTypes
     * @return
     */
    private static List<Class> filterClassesList(List<Class> classes, boolean skipEnums, boolean skipAnnotations,
                                                 boolean skipAbstract, boolean skipInterfaces,
                                                 boolean skipArrays, boolean skipLocalClass,
                                                 boolean skipMemberClass, boolean skipAnonymousClass,
                                                 boolean skipPrimitives,
                                                 String[] packageNamesFilter, Class... onlyAssignableFromSuperTypes) {
        // firstly we check flags
        // enums
        classes = Collections.synchronizedList(classes);
        List<Class> out = new ArrayList<>();
        synchronized (classes) {
            Iterator<Class> classIterator = classes.iterator();
            while (classIterator.hasNext()) {
                Class cls = classIterator.next();
                if(shouldSkipClass(cls, skipEnums, skipAnnotations, skipAbstract, skipInterfaces,
                        skipArrays, skipLocalClass, skipMemberClass, skipAnonymousClass, skipPrimitives)) {
                    continue;
                }
                out.add(cls);
            }
        }
        return filterClassListByPackageNamesAndSupertypes(out, onlyAssignableFromSuperTypes, packageNamesFilter);
    }

    /**
     * This method is here to tell you if provided class fits your requirements set in all those booleans
     * @param cls
     * @param skipEnums
     * @param skipAnnotations
     * @param skipAbstract
     * @param skipInterfaces
     * @param skipArrays
     * @param skipLocalClass
     * @param skipMemberClass
     * @param skipAnonymousClass
     * @param skipPrimitives
     * @return
     */
    private static boolean shouldSkipClass(Class cls, boolean skipEnums, boolean skipAnnotations,
                                           boolean skipAbstract, boolean skipInterfaces,
                                           boolean skipArrays, boolean skipLocalClass,
                                           boolean skipMemberClass, boolean skipAnonymousClass,
                                           boolean skipPrimitives) {
        if (skipEnums)
            if (cls.isEnum()) {
                return true;
            }
        if (skipAnnotations)
            if (cls.isAnnotation()) {
                return true;
            }
        if (skipAbstract)
            if (Modifier.isAbstract(cls.getModifiers())) {
                return true;
            }
        if (skipInterfaces)
            if (cls.isInterface()) {
                return true;
            }
        if (skipArrays)
            if (cls.isArray()) {
                return true;
            }
        if (skipLocalClass)
            if (cls.isLocalClass()) {
                return true;
            }
        if (skipMemberClass)
            if (cls.isMemberClass()) {
                return true;
            }
        if (skipAnonymousClass) {
            if (cls.isAnonymousClass()) {
                return true;
            }
        }
        if (skipPrimitives) {
            if (cls.isPrimitive()) {
                return true;
            }
        }
        return false;
    }


    /**
     * Filters and returns provided list of classes with provided classesList and packageName filters
     * @param classesList list of classes to filter
     * @param superTypes list of superTypes that all classes from list should be able to be assigned from
     * @param packageNames list of packageNames that all classes from list should be belong to
     * @return filtered list
     */
    private static List<Class> filterClassListByPackageNamesAndSupertypes(List<Class> classesList,
                                                                          Class[] superTypes, String... packageNames) {
        classesList = Collections.synchronizedList(classesList);
        List<Class> out = new ArrayList<>();
        synchronized (classesList) {
            Iterator<Class> classIterator = classesList.iterator();
            while (classIterator.hasNext()) {
                Class cls = classIterator.next();
                if(shouldDeletePackageNameMismatch(cls, packageNames)) {
                    classIterator.remove();
                    continue;
                }
                if(shouldDeleteSupertypeMismatch(cls, superTypes)) {
                    classIterator.remove();
                    continue;
                }
                out.add(cls);
            }
        }
        return out;
    }

    /**
     * Returns false if provided class can be assigned from any of provided superTypes
     * <br> Returns false if superTypes NULL or empty
     * <br> In other cases returns true
     * @param cls
     * @param superTypes
     * @return
     */
    private static boolean shouldDeleteSupertypeMismatch(Class cls, Class... superTypes) {
        if(superTypes!=null) {
            if (superTypes.length > 0) {
                for(Class superType : superTypes) {
                    if(superType.isAssignableFrom(cls))
                        return false;
                }
            }
            return true;
        }
        return false;
    }

    /**
     * Returns false if provided class has package equal to any in packageNames
     * <br> Returns false if packageNames NULL or empty
     * <br> In all other cases returns true
     * @param cls
     * @param packageNames
     * @return
     */
    private static boolean shouldDeletePackageNameMismatch(Class cls, String... packageNames) {
        if(packageNames!=null) {
            if (packageNames.length > 0) {
                for(String packageName : packageNames) {
                    if(cls.getPackage().getName().equals(packageName))
                        return false;
                }
            }
            return true;
        }
        return false;
    }

    /**
     * Returns list of classes available for current package
     * @param skipExceptions
     * @param log
     * @param logTag
     * @param logLevelFilter
     * @return - list of classes available for current package
     * @throws DexRuntimeException if failed and skipExceptions flag = false
     */
    public List<Class> getAllClasses(boolean skipExceptions, boolean log,
                                     int logLevelFilter, String logTag) {
        setLogLevel(logLevelFilter);
        setLogOn(log);
        setlTag(logTag);
        setSkipExceptions(skipExceptions);
        return getAllClasses(skipExceptions, log, logTag, logLevelFilter);
    }

    /**
     * Returns list of classes available for current package
     * @return - list of classes available for current package
     * @throws DexRuntimeException if failed and skipExceptions flag = false
     */
    public List<Class> getAllClasses() {
        return getAllClasses(skipExceptions, logOn, lTag, logLevel);
    }

    /**
     * Returns list of classes available for current package
     * @param skipExceptions
     * @param log
     * @param logTag
     * @param logLevelFilter
     * @return list of classes available for current package
     * @throws DexRuntimeException if failed and skipExceptions flag = false
     */
    private List<Class> getAllClasses(boolean skipExceptions, boolean log,
                                      String logTag, int logLevelFilter) {
        if(appClasses!=null) if(appClasses.size() > 0) return appClasses;
        List<Class> classNames = new ArrayList<>();
        File[] sources = null;
        try {
            sources = getSourcePaths();
            if(log && logLevelFilter == LOG_LEVEL_INFO) {
                for (File f : sources)
                    Log.i(logTag, MessageFormat.format(LOG_I_ADDED_DEX_LOCATION, f.getAbsolutePath()));
            }
        } catch (IOException ioe) {
            String msg = MessageFormat.format(UNABLE_TO_GET_SOURCES_PATH_EXCEPTION, ioe.getClass().getCanonicalName());
            if(log) Log.e(logTag, msg, ioe);
            if(!skipExceptions) throw new KittyDexUtils.DexRuntimeException(msg, ioe);
            return classNames; // return blank list
        } catch (PackageManager.NameNotFoundException nnfe) {
            String msg = MessageFormat.format(UNABLE_TO_GET_SOURCES_PATH_EXCEPTION, nnfe.getClass().getCanonicalName());
            if(log) Log.e(logTag, msg, nnfe);
            if(!skipExceptions) throw new KittyDexUtils.DexRuntimeException(msg, nnfe);
            return classNames; // return blank list
        }
        for (File path : sources) {
            try {
                DexFile dexfile;
                if (path.getAbsolutePath().endsWith(ZIP_EXT)) {
                    StringBuffer outputPathNameSB = new StringBuffer(32);
                    outputPathNameSB.append(path.getAbsolutePath())
                            .append(TEMP_EXT);
                    dexfile = DexFile.loadDex(path.getAbsolutePath(), outputPathNameSB.toString(), 0);
                } else {
                    dexfile = new DexFile(path);
                }
                Enumeration<String> dexEntries = dexfile.entries();
                while (dexEntries.hasMoreElements()) {
                    String className = dexEntries.nextElement();
                    if(log && logLevelFilter == LOG_LEVEL_INFO)
                        Log.i(logTag, MessageFormat.format(LOG_I_PROCESSING_CLASS, path.getAbsolutePath(), className));
                    // Loading class without initialization
                    try {
                        if(classNameFilter!=null) {
                            if(!classNameFilter.shouldBeSkipped(className)) {
                                Class cls = Class.forName(className, false, context.getClassLoader());
                                for(Class toSeek : CHILD_CLASSES_TO_SEEK) {
                                    if(toSeek.isAssignableFrom(cls)) {
                                        classNames.add(cls);
                                        break;
                                    }
                                }
                            }
                        } else {
                            Class cls = Class.forName(className, false, context.getClassLoader());
                            classNames.add(cls);
                        }
                    } catch (ClassNotFoundException cnfe) {
                        if(log && logLevelFilter > LOG_LEVEL_INFO) {
                            Log.w(logTag,
                                    MessageFormat.format(LOG_W_CLASS_FOUND_BUT_FAILED_TO_LOAD,
                                            className, path.getAbsolutePath()), cnfe);
                        }
                    } catch (java.lang.ExceptionInInitializerError error) {
                        Log.w(logTag,
                                MessageFormat.format(LOG_W_CLASS_FOUND_BUT_FAILED_TO_LOAD,
                                        className, path.getAbsolutePath()), error);
                    } catch (java.lang.NoClassDefFoundError noClsError) {
                        Log.w(logTag,
                                MessageFormat.format(LOG_W_CLASS_FOUND_BUT_FAILED_TO_LOAD,
                                        className, path.getAbsolutePath()), noClsError);
                    }
                }
            } catch (IOException ie) {
                String msg = MessageFormat.format(UNABLE_TO_LOAD_DEX_IOEXCEPTION, path.getAbsolutePath());
                if(log) Log.e(logTag, msg, ie);
                if(!skipExceptions) throw new DexRuntimeException(msg, ie);
            }
        }
        return classNames;
    }

    /**
     * Runtime exception for rethrowing
     */
    public class DexRuntimeException extends RuntimeException {
        private final Exception nestedException;

        public DexRuntimeException(String msg, Exception nestedException) {
            super(msg);
            this.nestedException = nestedException;
        }

        /**
         * Returns nested exception (e.g. exception that caused KittyDexUtils fail)
         * @return
         */
        public Exception getNestedException() {
            return nestedException;
        }
    }
}