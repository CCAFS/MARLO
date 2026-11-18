-- A2-2613: text corrections in two innovation catalogs (Knowledge & Scaling), shared by every global unit.
-- * Rule 6.1: readiness level 9 ("Proven Innovation") described the innovation as "validated for its ability to
--   achieve a specific innovation"; it is "a specific impact".
-- * Rule 2.6: the actor type options give their examples with "i.e."; they are examples, so "e.g.". Only the
--   abbreviation changes; the options and their examples stay the same.
--
-- Safe in every environment: rows are matched by their text, so an environment that already has the corrected text,
-- or never had these rows, is left as it is. No id is assumed.

UPDATE scaling_readiness
SET description = REPLACE(description, 'achieve a specific innovation.', 'achieve a specific impact.')
WHERE description LIKE '%achieve a specific innovation.%';

UPDATE actors
SET prms_name_equivalent = REPLACE(prms_name_equivalent, '(i.e. ', '(e.g. ')
WHERE prms_name_equivalent LIKE '%(i.e. %';
