INSERT INTO roles (description, acronym, `order`, global_unit_id)
SELECT
'API Read',
'AR',
16,
g.id
FROM
global_units AS g
WHERE
g.is_active = 1 AND
g.is_marlo = 1 AND
g.id NOT IN (
SELECT
g2.id
FROM
global_units AS g2
INNER JOIN roles ON roles.global_unit_id = g2.id
WHERE
roles.acronym = 'AR'
)
