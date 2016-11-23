DROP VIEW IF EXISTS `budgetsbycoas_view`;
  CREATE  VIEW `budgetsbycoas_view` AS SELECT
  (
    CASE
    WHEN pbc.amount IS NULL THEN
      - 1
    ELSE
      FORMAT(pbc.amount, 2)
    END
  ) AS budget,
  (
    CASE
    WHEN pbc.gender_percentage IS NULL THEN
      - 1
    ELSE
      FORMAT(pbc.gender_percentage, 2)
    END
  ) AS gender,
  pbc.cluster_activity_id,
  pbc.is_active,
  pbc.`year`,
  pbc.project_id,
  pbc.budget_type
FROM
  project_budgets_cluser_actvities AS pbc;
  
  DROP VIEW IF EXISTS `budgetsbypartners_view`;  
  CREATE  VIEW `budgetsbypartners_view` AS SELECT
  (
    CASE
    WHEN pb.amount IS NULL THEN
      - 1
    ELSE
      FORMAT(pb.amount, 2)
    END
  ) AS budget,
  (
    CASE
    WHEN pb.gender_percentage IS NULL THEN
      - 1
    ELSE
      FORMAT(pb.gender_percentage, 2)
    END
  ) AS gender,
  pb.project_id,
  pb.budget_type,
  pb.institution_id,
  pb.`year`,
  pb.is_active,
  pb.amount
FROM
  project_budgets AS pb;
  
  DROP VIEW IF EXISTS `crp_programs_view`;  
  CREATE  VIEW `crp_programs_view` AS SELECT
  cp.acronym AS cpacronym,
  cp.`name` AS cpname,
  cp.program_type AS cptype,
  p.id AS project_id,
  cp.id AS cpid
FROM
  projects p
LEFT JOIN project_focuses pf ON pf.project_id = p.id
AND pf.is_active = 1
LEFT JOIN crp_programs cp ON cp.id = pf.program_id
AND cp.is_active = 1
WHERE
  p.is_active = 1;
  
  DROP VIEW IF EXISTS `partners_contacts`;  
  CREATE  VIEW `partners_contacts` AS SELECT
  pp.id,
  (
    CASE
    WHEN u.id = 0
    OR u.id IS NULL THEN
      NULL
    ELSE
      concat(
        u.last_name,
        ', ',
        u.first_name,
        '\n',
        '&lt;',
        u.email,
        '&gt;'
      )
    END
  ) AS contact,
  (
    CASE
    WHEN ppp.responsibilities IS NULL
    OR ppp.responsibilities = '' THEN
      NULL
    ELSE
      ppp.responsibilities
    END
  ) AS responsibilities,
  CASE ppp.contact_type
WHEN 'PL' THEN
  'Project Leader'
WHEN 'PC' THEN
  'Project Coordinator'
WHEN 'CP' THEN
  'Partner'
ELSE
  NULL
END AS Type,
 (
  CASE
  WHEN (
    ins.city = ''
    OR ins.city IS NULL
  )
  AND (
    le.`name` = ''
    OR le.`name` IS NULL
  ) THEN
    NULL
  WHEN (
    ins.city = ''
    OR ins.city IS NULL
  )
  AND (
    le.`name` != ''
    AND le.`name` IS NOT NULL
  ) THEN
    le.`name`
  ELSE
    CONCAT(ins.city, ', ', le.`name`)
  END
) AS 'Branch'
FROM
  project_partners AS pp
INNER JOIN project_partner_persons ppp ON ppp.project_partner_id = pp.id
AND ppp.is_active = 1
INNER JOIN users u ON u.id = ppp.user_id
LEFT JOIN institutions ins ON ins.id = ppp.institution_id
LEFT JOIN loc_elements le ON le.id = ins.country_id
AND le.is_active = 1;


 DROP VIEW IF EXISTS `partners_view`;  
  CREATE  VIEW `partners_view` AS SELECT
  pp.id AS pp_id,
  (
    CASE
    WHEN (
      ins.acronym IS NULL
      OR ins.acronym = ''
    )
    AND (
      ins. NAME IS NULL
      OR ins. NAME = ''
    ) THEN
      NULL
    WHEN (
      ins.acronym IS NULL
      OR ins.acronym = ''
    )
    AND ins. NAME IS NOT NULL THEN
      ins. NAME
    ELSE
      CONCAT(ins.acronym, ' - ', ins. NAME)
    END
  ) AS 'Institution',
  ppp.contact_type,
  pp.project_id
FROM
  project_partners AS pp
INNER JOIN project_partner_persons ppp ON ppp.project_partner_id = pp.id
AND ppp.is_active = 1
INNER JOIN institutions ins ON ins.id = pp.institution_id
WHERE
  pp.is_active = 1 ;
  