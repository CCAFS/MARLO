UPDATE project_partner_persons AS ppp1
INNER JOIN (
  SELECT DISTINCT
    ppp.id as id,
    dp.division_id as division
  FROM
    deliverable_partnerships AS dp
  INNER JOIN project_partner_persons AS ppp ON dp.partner_person_id = ppp.id
  INNER JOIN users ON ppp.user_id = users.id
  WHERE
    ppp.is_active = 1
  AND dp.division_id IS NOT NULL
  AND dp.is_active = 1
) as tbl1
set ppp1.partner_division_id=tbl1.division
WHERE
ppp1.id = tbl1.id