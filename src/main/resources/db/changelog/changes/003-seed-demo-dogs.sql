--liquibase formatted sql

--changeset tinder4dogs:003-seed-demo-dogs
--comment: six dogs to match against, so the endpoints return something worth looking at
--
-- Ids are left to the identity column and the preferences are attached by
-- name, rather than by hard-coded ids. Writing the ids out by hand would work
-- once and then leave the identity sequence behind the highest row, so the
-- next INSERT from the application would collide with a key that already
-- exists. This shape has no such trap.
INSERT INTO dog (name, breed, gender, age) VALUES
    ('Bella', 'Labrador',      'FEMALE', 4),
    ('Rex',   'Labrador',      'MALE',   5),
    ('Luna',  'Beagle',        'FEMALE', 3),
    ('Max',   'Labrador',      'MALE',   9),
    ('Ace',   'Border Collie', 'MALE',   2),
    ('Duke',  'Beagle',        'MALE',   7);

INSERT INTO dog_preference (dog_id, preference)
SELECT d.id, p.preference
FROM dog d
JOIN (VALUES
        ('Bella', 'parks'), ('Bella', 'fetch'),
        ('Rex',   'parks'), ('Rex',   'fetch'), ('Rex', 'naps'),
        ('Luna',  'parks'), ('Luna',  'naps'),
        ('Max',   'fetch'), ('Max',   'sofa'),
        ('Ace',   'parks'), ('Ace',   'cars'),
        ('Duke',  'sofa'),  ('Duke',  'food')
     ) AS p(name, preference) ON p.name = d.name;
--rollback DELETE FROM dog WHERE name IN ('Bella','Rex','Luna','Max','Ace','Duke');
