--liquibase formatted sql

--changeset tinder4dogs:004-legacy-dog-row
--comment: one row from before the age validation existed. It is here on purpose.
--
-- DogController rejects a negative age today (@field:Min(0)), so the API can
-- no longer produce this row. It is the kind of data that arrives from an
-- import, a seed, or a column that was added without a backfill, and it is
-- still sitting in the table long after the code that allowed it was fixed.
--
-- Nothing at the database level stops it either: the `age` column is a plain
-- INT NOT NULL with no CHECK. That is deliberate and it is the subject of
-- Module 4: a single bad row takes down an endpoint that reads every row.
--
-- If you are here because you want the endpoint to stop returning 500, the
-- fix does not belong in this file.
INSERT INTO dog (name, breed, gender, age)
VALUES ('Nonna', 'Beagle', 'FEMALE', -3);

INSERT INTO dog_preference (dog_id, preference)
SELECT id, 'naps' FROM dog WHERE name = 'Nonna';
--rollback DELETE FROM dog WHERE name = 'Nonna';
