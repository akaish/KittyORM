---
title: "Lesson 4 tab 3 sources"
date: 2018-11-15T06:50:21+03:00
draft: false
layout: "lesson"
---
**Example script for changing all imports at KittyORM that import Android database classes to corresponding classes of SQLCipher:**
{{< highlight sh >}}
#!/bin/bash
find . -name '*.java' -exec sed -i -e 's/android.database.sqlite/net.sqlcipher.database/g' {} \;
find . -name '*.java' -exec sed -i -e 's/android.database/net.sqlcipher/g' {} \;
{{< /highlight >}} 
**Modifying methods of `KittyDatabaseHelper.class` for encryption support:**
{{< highlight java "linenos=inline, linenostart=1">}}
public SQLiteDatabase getWritableDatabase(String pwd) {
    return super.getWritableDatabase(pwd);
}
    
public SQLiteDatabase getReadableDatabase(String pwd) {
    return super.getReadableDatabase(pwd);
}
{{< /highlight >}} 
**Modifying сonstructor of `KittyDatabase.class` for adding support of database encryption:**
{{< highlight java "linenos=inline, linenostart=1">}}
public KittyDatabase(Context ctx, String databasePassword) {
    net.sqlcipher.database.SQLiteDatabase.loadLibs(ctx);

    ... // Old constructor code 
}
{{< /highlight >}} 

This page is a part of KittyORM project (KittyORM Documentation) and licensed under a Creative Commons Attribution-ShareAlike 4.0 International License. To view a copy of this license, visit http://creativecommons.org/licenses/by-sa/4.0/ or send a letter to Creative Commons, PO Box 1866, Mountain View, CA 94042, US., more information at https://akaish.github.io/KittyORMPages/license/
