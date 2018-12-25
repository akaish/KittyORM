---
title: "Lesson 4 tab 2 sources"
date: 2018-11-15T06:50:21+03:00
draft: false
layout: "lesson"
---
**Example of KittyORM logging setup:**
{{< highlight java "linenos=inline, linenostart=1">}}
@KITTY_DATABASE(
        isLoggingOn = true, // Base logging flag
        isProductionOn = false, // Production logging flag
        isKittyDexUtilLoggingEnabled = false, // dex logging flag
        logTag = MigrationDBv3.LTAG, // log tag
        databaseName = "mig", // database name
        databaseVersion = 3, // database version
        ...
)

public class MigrationDBv3 extends KittyDatabase {

    public static final String LTAG = "MIGv3";
    
    ...
}
{{< /highlight >}}

**Example of `KittyModel` implementaion `toLogString()` method overload:**

{{< highlight java "linenos=inline, linenostart=1">}}
@KITTY_TABLE
public class SimpleExampleModel extends KittyModel {
    public SimpleExampleModel() {
        super();
    }

    @KITTY_COLUMN(
            isIPK = true,
            columnOrder = 0
    )
    public Long id;

    @KITTY_COLUMN(columnOrder = 1)
    public int randomInteger;

    @KITTY_COLUMN(columnOrder = 2)
    public String firstName;

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder(64);
        return sb.append("[ rowid = ")
                    .append(getRowID())
                    .append(" ; id = ")
                    .append(id)
                    .append(" ; randomInteger = ")
                    .append(randomInteger)
                    .append(" ; firstName = ")
                    .append(firstName)
                    .append(" ]")
                    .toString();
    }

    public String toLogString() {
        return toString();
    }
}
{{< /highlight >}}

This page is a part of KittyORM project (KittyORM Documentation) and licensed under a Creative Commons Attribution-ShareAlike 4.0 International License. To view a copy of this license, visit http://creativecommons.org/licenses/by-sa/4.0/ or send a letter to Creative Commons, PO Box 1866, Mountain View, CA 94042, US., more information at https://akaish.github.io/KittyORMPages/license/
