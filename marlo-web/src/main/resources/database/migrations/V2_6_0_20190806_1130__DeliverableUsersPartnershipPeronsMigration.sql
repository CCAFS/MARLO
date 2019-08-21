INSERT INTO deliverable_user_partnership_persons (
  user_partnership_id,
  user_id,
  division_id,
  is_active,
  active_since,
  created_by,
  modified_by,
  modification_justification
) SELECT DISTINCT
  deliverable_user_partnerships.id,
  deliverable_user_partnerships.user_id,
  deliverable_user_partnerships.division_id,
  deliverable_user_partnerships.is_active,
  deliverable_user_partnerships.active_since,
  deliverable_user_partnerships.created_by,
  deliverable_user_partnerships.modified_by,
  deliverable_user_partnerships.modification_justification
FROM
  deliverable_user_partnerships
ORDER BY
  deliverable_user_partnerships.deliverable_id ASC;