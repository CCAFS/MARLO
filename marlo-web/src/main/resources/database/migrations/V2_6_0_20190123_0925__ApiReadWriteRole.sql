INSERT INTO roles (description, acronym, `order`, global_unit_id)
SELECT
'API Read-Write',
'ARW',
17,
g.id
FROM
global_units AS g
WHERE
g.is_active = 1 AND
g.is_marlo = 1 AND

