DELETE from deliverable_user_partnership_persons;
DELETE from deliverable_user_partnerships;

ALTER TABLE deliverable_user_partnership_persons AUTO_INCREMENT = 1;
ALTER TABLE deliverable_user_partnerships AUTO_INCREMENT = 1;

INSERT INTO deliverable_user_partnerships (
  deliverable_id,
  id_phase,
  institution_id,
  deliverable_partner_type_id,
  is_active,
  active_since,
  created_by,
  modified_by,
  modification_justification
) SELECT DISTINCT
  dp.deliverable_id,
  dp.id_phase,
  pp.institution_id,
  CASE
WHEN dp.partner_type = 'Resp' THEN
  1
WHEN dp.partner_type = 'Other' THEN
  2
END AS p_type,
 dp.is_active,
 NOW(),
 1057,
 1057,
 ''
FROM
  deliverable_partnerships AS dp
INNER JOIN project_partners AS pp ON dp.project_partner_id = pp.id
INNER JOIN project_partner_persons AS ppp ON ppp.project_partner_id = pp.id
AND dp.partner_person_id = ppp.id
WHERE
  dp.is_active = 1;
  
  INSERT INTO deliverable_user_partnership_persons (
  user_partnership_id,
  user_id,
  division_id,
  is_active,
  active_since,
  created_by,
  modified_by,
  modification_justification
) SELECT
  dup.id,
  ppp.user_id,
  dp.division_id,
  1,
  NOW(),
  1057,
  1057,
  ''
FROM
  deliverable_user_partnerships AS dup
INNER JOIN deliverable_partner_type AS dpt ON dup.deliverable_partner_type_id = dpt.id,
 deliverable_partnerships AS dp
INNER JOIN project_partner_persons AS ppp ON dp.partner_person_id = ppp.id
WHERE
  dup.deliverable_id = dp.deliverable_id
AND dup.id_phase = dp.id_phase
AND dpt.`name` = dp.partner_type
AND dup.is_active = 1
AND dp.is_active = 1;