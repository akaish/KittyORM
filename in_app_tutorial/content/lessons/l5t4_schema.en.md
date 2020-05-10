---
title: "Lesson5 tab 4 DB schema"
date: 2018-11-15T06:50:21+03:00
draft: false
layout: "lesson"
---
### Create schema script for database `mig` version `4`
{{< highlight sql >}}
CREATE TABLE IF NOT EXISTS mig_four (id INTEGER NOT NULL PRIMARY KEY ASC, mig_three_reference INTEGER NOT NULL REFERENCES mig_three (id) ON UPDATE NO ACTION ON DELETE NO ACTION, mig_two_reference INTEGER NOT NULL REFERENCES mig_two (id) ON UPDATE NO ACTION ON DELETE NO ACTION, creation_date INTEGER NOT NULL DEFAULT  CURRENT_DATE );

CREATE TABLE IF NOT EXISTS mig_three (id INTEGER NOT NULL PRIMARY KEY ASC, new_sv_name TEXT NOT NULL DEFAULT 'Something random', random_long INTEGER DEFAULT 22);

CREATE INDEX IF NOT EXISTS m3_rnd_long ON mig_three (random_long);

CREATE TABLE IF NOT EXISTS mig_two (id INTEGER NOT NULL PRIMARY KEY ASC, mig_one_reference INTEGER, some_animal TEXT, some_animal_sound TEXT);
{{< /highlight >}} 
### Drop schema script for database `mig` version `4`
{{< highlight sql >}}
DROP TABLE IF EXISTS mig_four;

DROP TABLE IF EXISTS mig_three;

DROP TABLE IF EXISTS mig_two;
{{< /highlight >}} 
### Migration script for database `mig` from version `3` to version `4` (Filescript migrator)
{{< highlight sql >}}
-- minus FK M2.migOneReference
ALTER TABLE mig_two RENAME TO mig_two_old_t;

CREATE TABLE IF NOT EXISTS mig_two (id INTEGER NOT NULL PRIMARY KEY ASC, mig_one_reference INTEGER, some_animal TEXT, some_animal_sound TEXT);

INSERT INTO mig_two (id, mig_one_reference, some_animal, some_animal_sound) SELECT id, mig_one_reference, some_animal, some_animal_sound FROM mig_two_old_t;

DROP TABLE IF EXISTS mig_two_old_t;

-- minus M1 table
DROP TABLE IF EXISTS mig_one;

-- plus M4 table
CREATE TABLE IF NOT EXISTS mig_four (id INTEGER NOT NULL PRIMARY KEY ASC, mig_three_reference INTEGER NOT NULL REFERENCES mig_three (id) ON UPDATE NO ACTION ON DELETE NO ACTION, mig_two_reference INTEGER NOT NULL REFERENCES mig_two (id) ON UPDATE NO ACTION ON DELETE NO ACTION, creation_date INTEGER NOT NULL DEFAULT  CURRENT_DATE );

-- rename M3.some_value to M3.new_sv_name and add M3.random_long
ALTER TABLE mig_three RENAME TO mig_three_old_t;

CREATE TABLE IF NOT EXISTS mig_three (id INTEGER NOT NULL PRIMARY KEY ASC, new_sv_name TEXT NOT NULL DEFAULT 'Something random', random_long INTEGER DEFAULT 22);

INSERT INTO mig_three (id, new_sv_name) SELECT id, some_value FROM mig_three_old_t;

DROP TABLE IF EXISTS mig_three_old_t;

-- add tableIndex on M3.random_long
CREATE INDEX IF NOT EXISTS m3_rnd_long ON mig_three (random_long);
{{< /highlight >}} 
### AfterCreate script for database `mig` version `4`
{{< highlight sql >}}
INSERT INTO mig_three (new_sv_name, random_long) VALUES ('AFTER CREATE SCRIPT', 1);
INSERT INTO mig_three (new_sv_name, random_long) VALUES ('AFTER CREATE SCRIPT', 2);
INSERT INTO mig_three (new_sv_name, random_long) VALUES ('AFTER CREATE SCRIPT', 3);
INSERT INTO mig_three (new_sv_name, random_long) VALUES ('AFTER CREATE SCRIPT', 4);
INSERT INTO mig_three (new_sv_name, random_long) VALUES ('AFTER CREATE SCRIPT', 5);
INSERT INTO mig_three (new_sv_name, random_long) VALUES ('AFTER CREATE SCRIPT', 6);
INSERT INTO mig_three (new_sv_name, random_long) VALUES ('AFTER CREATE SCRIPT', 7);
INSERT INTO mig_three (new_sv_name, random_long) VALUES ('AFTER CREATE SCRIPT', 8);
INSERT INTO mig_three (new_sv_name, random_long) VALUES ('AFTER CREATE SCRIPT', 9);
INSERT INTO mig_three (new_sv_name, random_long) VALUES ('AFTER CREATE SCRIPT', 10);
INSERT INTO mig_three (new_sv_name, random_long) VALUES ('AFTER CREATE SCRIPT', 11);
INSERT INTO mig_three (new_sv_name, random_long) VALUES ('AFTER CREATE SCRIPT', 12);
INSERT INTO mig_three (new_sv_name, random_long) VALUES ('AFTER CREATE SCRIPT', 13);
{{< /highlight >}} 
### AfterMigrate script for database `mig` version `4`
{{< highlight sql >}}
INSERT INTO mig_three (new_sv_name, random_long) VALUES ('AFTER MIGRATE SCRIPT', 1);
INSERT INTO mig_three (new_sv_name, random_long) VALUES ('AFTER MIGRATE SCRIPT', 11);
INSERT INTO mig_three (new_sv_name, random_long) VALUES ('AFTER MIGRATE SCRIPT', 111);
INSERT INTO mig_three (new_sv_name, random_long) VALUES ('AFTER MIGRATE SCRIPT', 1111);
INSERT INTO mig_three (new_sv_name, random_long) VALUES ('AFTER MIGRATE SCRIPT', 11111);
INSERT INTO mig_three (new_sv_name, random_long) VALUES ('AFTER MIGRATE SCRIPT', 111111);
INSERT INTO mig_three (new_sv_name, random_long) VALUES ('AFTER MIGRATE SCRIPT', 1111111);
INSERT INTO mig_three (new_sv_name, random_long) VALUES ('AFTER MIGRATE SCRIPT', 11111111);
INSERT INTO mig_three (new_sv_name, random_long) VALUES ('AFTER MIGRATE SCRIPT', 1111111);
INSERT INTO mig_three (new_sv_name, random_long) VALUES ('AFTER MIGRATE SCRIPT', 111111);
INSERT INTO mig_three (new_sv_name, random_long) VALUES ('AFTER MIGRATE SCRIPT', 11111);
INSERT INTO mig_three (new_sv_name, random_long) VALUES ('AFTER MIGRATE SCRIPT', 1111);
INSERT INTO mig_three (new_sv_name, random_long) VALUES ('AFTER MIGRATE SCRIPT', 111);
INSERT INTO mig_three (new_sv_name, random_long) VALUES ('AFTER MIGRATE SCRIPT', 11);
INSERT INTO mig_three (new_sv_name, random_long) VALUES ('AFTER MIGRATE SCRIPT', 1);
{{< /highlight >}} 

This page is a part of KittyORM project (KittyORM Documentation) and licensed under a Creative Commons Attribution-ShareAlike 4.0 International License. To view a copy of this license, visit http://creativecommons.org/licenses/by-sa/4.0/ or send a letter to Creative Commons, PO Box 1866, Mountain View, CA 94042, US., more information at https://akaish.github.io/KittyORMPages/license/
