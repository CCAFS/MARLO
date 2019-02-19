UPDATE project_partners pp
INNER JOIN project_partner_partnerships ON project_partner_partnerships.project_partner = pp.id
SET pp.responsibilities = CONCAT(
  pp.responsibilities, CHAR (13), '** Partnerships **',
  CHAR (13),
  project_partner_partnerships.main_area
)