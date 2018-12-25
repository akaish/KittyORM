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
-- add index on M3.random_long
CREATE INDEX IF NOT EXISTS m3_rnd_long ON mig_three (random_long);