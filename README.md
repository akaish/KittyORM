**KittyORM** is an Object-Relational Mapping library designed for use with Android and SQLite. It implements Data Mapper pattern design and its main purpose is to simplify interaction with SQLite database in Android applications. Written in Java 7 it supports devices from API level 9 Android.

You can find KittyORM Demo application that shows KittyORM capabilities and contains tutorials and example code snippets at Play Market: https://play.google.com/store/apps/details?id=net.akaish.kittyormdemo

![alt text](https://akaish.github.io/KittyORMPages/hidden/android_src/qr.png)

[KittyORM Project pages](https://akaish.github.io/KittyORMPages/)  
[KittyORM Project at GitHub](https://github.com/akaish/KittyORM)  
[KittyORM Demo and tutorial application](https://play.google.com/store/apps/details?id=net.akaish.kittyormdemo)  
Current version: [0.1.2](http://repo1.maven.org/maven2/net/akaish/kitty/orm/kitty-orm/)

## Table of contents
1. [Introduction to KittyORM](#introduction-to-kittyorm)
2. [Quick start](#quick-start)
3. [Further reading](#further-reading)

## Introduction to KittyORM

### Main features we want to achieve with KittyORM are:

* simple and clear API that handles database creation, version management and interaction with database tables;
* high flexibility of working with model POJO files via database mappers that grants you an ability to focus on your business processes not on working with raw SQL queries;
* full support of all features to create your SQLite schema via built-in annotations. That means that you can use all SQLite features described at SQLite documentation to create your schema only with usage of KittyORM annotations (indexes, constraints etc);
* flexible way to manage all things you may want to change or implement. Typical KittyORM database consists of database bootstrap class implementation that handles all actions to get all stuff working, database helper implementation and list of models and data mappers stored in map, all of those are friendly for customization;
* quite good performance speed of executing business logic that achieved with on start generation of database configuration that helps to avoid a lot of reflection calls.

Main idea of creating KittyORM is to offer a tool that would suit both people who want to use all power of SQLite and people who want just to work with simple database of few tables to store data without messing with raw SQL.

### So, what KittyORM has right now?

* Full support of SQLite syntax to create a database schema implemented via KittyORM annotations.
* Supporting of mapping SQLite affinities to Java’s primitives, primitive wrappers and common objects such `Date`, `BigInteger` etc. Also, enumerations supported as well as user defined mapping (for example, `NONE` → `Bitmap` and back).
* Basic CRUD controller that can handle all typical CRUD operations.
* Support of extending default CRUD controller.
* `QueryBuilder` that offers simple creation of some extended queries to be used standalone or as part of extended CRUD controller.
* POJO models inheritance supported, that means that you can use abstract POJO class that would be inherited in child implementations. Also, KittyORM supports temporary tables and non-schema POJO models (those models can be used for querying database but wouldn’t be used at schema generation).
* Multidomain support (e.g. you can use as many databases in your application as you want).
* Not bad performance. You can tune your KittyORM database to avoid a big amount of reflection calls by setting your KittyORM database class by your own and placing it into Android Application class instance.
* Support of database version management. KittyORM provides you three migration options: `DropCreate` Migrator, `FileScript` Migrator and `SimpleMigrationScriptGenerator` Migrator.
* Ready for database encryption implementation.
* Simple but really flexible API. Practically, most components of KittyORM can be customized to suit your needs.
* Good documentation contains tutorial with code snippets, javadoc and demo application available at [KittyORM project page](https://akaish.github.io/KittyORMPages).

### Things to do in future releases:

* One-To-One and One-To-Many relation handling via implementing SQLite queries using `JOIN` operator.
* KittyORM standalone static code generator application to provide generation of mappers and models based on KittyORM implementation that would not use reflection calls.
* Partial standalone database encryption with AES.

And some other features as well.

## Quick start
### Gradle setup
First step is to add KittyORM via Gradle to your app `build.gradle`:
```Java
dependencies {
    compile 'net.akaish.kitty.orm:kitty-orm:0.1.2'
}
```

### KittyORM configuration and implementation
Create package for storing your POJO models, KittyORM database class, KittyORM helper class (if necessary) and KittyORM extended mappers (if necessary).

**First step:** extend `KittyDatabase` class, implement default constructor and annotate it with `@KITTY_DATABASE` annotation (and, if necessary, with `@KITTY_DATABASE_HELPER`).

**Second step:** create your first POJO model by extending `KittyModel` class, implement default constructor and annotate it with `@KITTY_TABLE` annotation. Each model field of KittyModel POJO implementation that corresponds database table column also has to be annotated with `@KITTY_COLUMN` annotation.

**Third step (optional):** create extended CRUD controller by extending `KittyMapper` class, implementing default constructor and adding business logic. To make what CRUD controller you want to use with given POJO model you can just use default naming rules (`SomeModel.class`, `Somemodel.class` and even `Some.class` POJO would use `SomeMapper.class` extended controller if found) or (better choice) annotate model POJO with `@EXTENDED_CRUD` linked to actual extended CRUD controller class implementation.

**Fourth step (optional):** create extended database helper by extending `KittyDatabaseHelper` class and make sure that your KittyDatabase class implementation would return new instance of your extended database helper via `KittyDatabase.newDatabaseHelper()` method.

In this lesson we create simple database that contains only one table and would interact with it using default CRUD controller. This demo contains many database domains, so database domain was set in `@KITTY_DATABASE`. Also it is better to set this value if your application uses a lot of libraries so KittyORM would seek POJO and CRUD classes related to KittyORM only in specified location(s).
### Working with entities

This example shows basic KittyORM usage when you just want to store some information in your database. Just very simple database to go. Database would be created at first call of `getMapper(Class<M> recordClass)` method of  `SimpleDatabase.class` instance, it would be named **simple_database** and would contain only one table called **simple_example**. This database would have **version 1** by default.

`SimpleDatabase.class`:
```Java
package net.akaish.kittyormdemo.sqlite.introductiondb;

import android.content.Context;

import net.akaish.kitty.orm.KittyDatabase;
import net.akaish.kitty.orm.annotations.KITTY_DATABASE;

@KITTY_DATABASE(
        domainPackageNames = {"net.akaish.kittyormdemo.sqlite.introductiondb"}
)
public class SimpleDatabase extends KittyDatabase {

    public SimpleDatabase(Context ctx) {
        super(ctx);
    }
}
```


`SimpleExampleModel.class`:
```Java
package net.akaish.kittyormdemo.sqlite.introductiondb;

import net.akaish.kitty.orm.KittyModel;
import net.akaish.kitty.orm.annotations.column.KITTY_COLUMN;
import net.akaish.kitty.orm.annotations.table.KITTY_TABLE;

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
}
```

By default, all names in KittyORM if they weren’t specified explicitly in annotations would be generated from class names and field names. For database name it would be used `KittyUtils.fieldNameToLowerCaseUnderScore(String fieldName)` method where `fieldName` is database implementation class name. For table names would be used `KittyUtils.fieldNameToLowerCaseUnderScore(String fieldName)` where `fieldName` is POJO class name without Model\model ending (if ending exists) and for column names would be used `KittyUtils.fieldNameToLowerCaseUnderScore(String fieldName)`.

> Do not use primitives for `PrimaryKeys`, because uninitialized primitive field returns 0 not `NULL` via reflection calls and KittyORM wouldn’t know what to do with such POJO.

### CRUD Usage
We are ready to go, just get `KittyMapper` from instance of `SimpleDatabase` with `getMapper(SimpleExampleModel.class)` and perform any basic RW operations.

> Do not forget to call `KittyMapper.close()` method on your `KittyMapper` instance after you did all database operations you wanted.

* Inserting new record associated with new model into database table:

```Java
SimpleExampleModel alex = new SimpleExampleModel();

alex.randomInteger = 545141;
alex.firstName = "Alex";

SimpleExampleModel marina = new SimpleExampleModel();

marina.randomInteger = 228;
marina.firstName = "Marina";

// save model with save method
mapper.save(alex);
// or use insert method if you want to get rowid
long marinaRowid = mapper.insert(marina);
```

* Finding record in table and returning its content as POJO model:

```Java
// find with row id
SimpleExampleModel model1 = mapper.findByRowID(0l);

// find with INTEGER PRIMARY KEY
SimpleExampleModel model2 = mapper.findByIPK(0l);

// find with KittyPrimaryKey
KittyPrimaryKeyBuilder pkBuilder = new KittyPrimaryKeyBuilder();
pkBuilder.addKeyColumnValue("id", "0");
SimpleExampleModel model3 = mapper.findByPK(pkBuilder.build());

List<SimpleExampleModel> marinas;

// find with condition
SQLiteConditionBuilder builder = new SQLiteConditionBuilder();
builder.addColumn("first_name")
       .addSQLOperator(SQLiteOperator.EQUAL)
       .addValue("Marina");
marinas = mapper.findWhere(builder.build());

// find with condition (you may use shorter syntax)
SQLiteConditionBuilder builder = new SQLiteConditionBuilder();
builder.addColumn("first_name")
       .addSQLOperator("=") // You may use string operators instead SQLiteOperator enum element
       .addValue("Marina");
marinas = mapper.findWhere(builder.build());

// find with condition (without query builder)
marinas = mapper.findWhere("first_name = ?", "Marina");

// find with condition (pass POJO field name as parameter, in #?fieldName form)
marinas = mapper.findWhere("#?firstName; = ?", "Marina");

List<SimpleExampleModel> randModels = new LinkedList<>();
for(int i = 0; i < 10; i++)
    randModels.add(RandomSimpleExampleModelUtil.randomSEModel());
mapper.save(randModels);
```

* Inserting 10 generated records into database table:

```Java
List<SimpleExampleModel> randModels = new LinkedList<>();
for(int i = 0; i < 10; i++)
    randModels.add(RandomSimpleExampleModelUtil.randomSEModel());
mapper.save(randModels);
```

* Deleting some models:

```Java
// deleting entity
mapper.delete(alex);

// deleting from database with condition
mapper.deleteWhere("first_name = ?", "Alex");
```

* Updating some models:

```Java
// updating current model
// if model has RowId or IPK or PrimaryKey values set (3 is slowest) just
marina.randomInteger = 1337;
mapper.update(marina);

// or just
mapper.save(marina)

// another option is updating with query-like method
SimpleExampleModel update = new SimpleExampleModel();
update.randomInteger = 121212;
builder = new SQLiteConditionBuilder();
builder.addColumn("first_name")
       .addSQLOperator("=")
       .addColumn("Marina");
mapper.update(update, builder.build(), new String[]{"randomInteger"}, CVUtils.INCLUDE_ONLY_SELECTED_FIELDS);
```

* Bulk operations in transaction mode:

```Java
randModels = new LinkedList<>();
for(int i = 0; i < 10; i++)
    randModels.add(RandomSimpleExampleModelUtil.randomSEModel());
mapper.saveInTransaction(randModels);
```

## Further reading

**KittyORM** support a lot of cool features and can do a lot of fancy things. Please refer official KittyORM project pages located at https://akaish.github.io/KittyORMPages/ for more info.

[KittyORM license](https://akaish.github.io/KittyORMPages/license/)
