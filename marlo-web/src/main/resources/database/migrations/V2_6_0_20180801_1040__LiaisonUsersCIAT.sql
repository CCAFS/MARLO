INSERT INTO liaison_users (user_id, institution_id, is_active, global_unit_id)
SELECT
pl.user_id,
li.id,
1,
p.global_unit_id
FROM
crp_programs AS p
INNER JOIN crp_program_leaders AS pl ON pl.crp_program_id = p.id
INNER JOIN liaison_institutions AS li ON li.crp_program = p.id
WHERE
p.global_unit_id = 29
and pl.manager = 0;