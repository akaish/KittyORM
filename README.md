[KittyORM Project pages](https://akaish.github.io/KittyORMPages/)  
[KittyORM Demo and tutorial application](https://play.google.com/store/apps/details?id=net.akaish.kittyormdemo)  
Current version: [0.1.2](http://repo1.maven.org/maven2/net/akaish/kitty/orm/kitty-orm/)


### What is KittyORM?

KittyORM is an Object-Relational Mapping library designed for use with Android and SQLite. It implements Data Mapper pattern design and its main purpose is to simplify interaction with SQLite database in Android applications. Written in Java 7 it supports devices from API level 9 Android. 

### Main features we want to achieve with KittyORM are:

* simple and clear API that handles database creation, version management and interaction with database tables;
* high flexibility of working with model POJO files via database mappers that grants you an ability to focus on your business processes not on working with raw SQL queries;
* full support of all features to create your SQLite schema via built-in annotations. That means that you can use all SQLite features described at SQLite documentation to create your schema only with usage of KittyORM annotations (indexes, constraints etc);
* flexible way to manage all things you may want to change or implement. Typical KittyORM database consists of database bootstrap class implementation that handles all actions to get all stuff working, database helper implementation and list of models and data mappers stored in map, all of those are friendly for customization;
* quite good performance speed of executing business logic that achieved with on start generation of database configuration that helps to avoid a lot of reflection calls.

[Read more...](https://akaish.github.io/KittyORMPages/)

## Getting started with KittyORM

### Gradle setup
First step is to add KittyORM via Gradle to your app `build.gradle`:
```gradle
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

[Read more...](https://akaish.github.io/KittyORMPages/getting_started/)

[KittyORM license](https://akaish.github.io/KittyORMPages/license/)
