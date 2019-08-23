INSERT INTO deliverable_user_partnerships (
  deliverable_id,
  id_phase,
  institution_id,
  user_id,
  deliverable_partner_type_id,
  division_id,
  is_active,
  active_since,
  created_by,
  modified_by,
  modification_justification
) SELECT
  dp.deliverable_id,
  dp.id_phase,
  pp.institution_id,
  ppp.user_id,
  CASE
WHEN dp.partner_type = 'Resp' THEN
  1
WHEN dp.partner_type = 'Other' THEN
  2
END AS p_type,
 dp.division_id,
 dp.is_active,
 dp.active_since,
 dp.created_by,
 dp.modified_by,
 dp.modification_justification
FROM
  deliverable_partnerships AS dp
INNER JOIN project_partners AS pp ON dp.project_partner_id = pp.id
INNER JOIN project_partner_persons AS ppp ON ppp.project_partner_id = pp.id
AND dp.partner_person_id = ppp.id
WHERE
  dp.is_active = 1;