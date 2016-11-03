DROP VIEW IF EXISTS `budgets_view`;
  CREATE  VIEW `budgets_view` AS 
  SELECT
  p.id AS 'project_id',
  (
    CASE
    WHEN p.title IS NULL
    OR p.title = '' THEN
      ''
    ELSE
      p.title
    END
  ) AS title,
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
      ''
    WHEN (
      ins.acronym IS NULL
      OR ins.acronym = ''
    )
    AND ins. NAME IS NOT NULL THEN
      ins. NAME
    ELSE
      CONCAT(ins.acronym, ' - ', ins. NAME)
    END
  ) AS 'Partner',
  (
    SELECT
      (
        CASE
        WHEN pb2.amount IS NULL THEN
          0
        ELSE
          sum(pb2.amount)
        END
      ) AS budget
    FROM
      project_budgets AS pb2
    WHERE
      pb2.project_id = p.id
    AND pb2.budget_type = 1
    AND pb2.institution_id = ins.id
    AND pb2.`year` = pb.`year`
    AND pb2.is_active = 1
  ) AS 'budget w1/w2',
  (
    SELECT
      (
        CASE
        WHEN pb2.amount IS NULL THEN
          0
        ELSE
          sum(
            (
              pb2.gender_percentage * pb2.amount
            ) / 100
          )
        END
      ) AS budget
    FROM
      project_budgets AS pb2
    WHERE
      pb2.project_id = p.id
    AND pb2.budget_type = 1
    AND pb2.institution_id = ins.id
    AND pb2.`year` = pb.`year`
    AND pb2.is_active = 1
  ) AS 'gender w1/w2',
  (
    SELECT
      (
        CASE
        WHEN pb2.amount IS NULL THEN
          0
        ELSE
          sum(pb2.amount)
        END
      ) AS budget
    FROM
      project_budgets AS pb2
    WHERE
      pb2.project_id = p.id
    AND pb2.budget_type = 2
    AND pb2.institution_id = ins.id
    AND pb2.`year` = pb.`year`
    AND pb2.is_active = 1
  ) AS 'budget w3',
  (
    SELECT
      (
        CASE
        WHEN pb2.amount IS NULL THEN
          0
        ELSE
          sum(
            (
              pb2.gender_percentage * pb2.amount
            ) / 100
          )
        END
      ) AS budget
    FROM
      project_budgets AS pb2
    WHERE
      pb2.project_id = p.id
    AND pb2.budget_type = 2
    AND pb2.institution_id = ins.id
    AND pb2.`year` = pb.`year`
    AND pb2.is_active = 1
  ) AS 'gender w3',
  (
    SELECT
      (
        CASE
        WHEN pb2.amount IS NULL THEN
          0
        ELSE
          sum(pb2.amount)
        END
      ) AS budget
    FROM
      project_budgets AS pb2
    WHERE
      pb2.project_id = p.id
    AND pb2.budget_type = 3
    AND pb2.institution_id = ins.id
    AND pb2.`year` = pb.`year`
    AND pb2.is_active = 1
  ) AS 'budget bilateral',
  (
    SELECT
      (
        CASE
        WHEN pb2.amount IS NULL THEN
          0
        ELSE
          sum(
            (
              pb2.gender_percentage * pb2.amount
            ) / 100
          )
        END
      ) AS budget
    FROM
      project_budgets AS pb2
    WHERE
      pb2.project_id = p.id
    AND pb2.budget_type = 3
    AND pb2.institution_id = ins.id
    AND pb2.`year` = pb.`year`
    AND pb2.is_active = 1
  ) AS 'gender bilateral',
  count(*) AS 'Total Funding sources',
  pb.`year`,
  p.crp_id
FROM
  project_budgets AS pb
LEFT JOIN institutions ins ON ins.id = pb.institution_id
INNER JOIN projects p ON p.id = pb.project_id
AND p.is_active = 1
WHERE
  pb.is_active = 1
GROUP BY
  p.id,
  ins.id,
  pb.`year`
ORDER BY
  pb.project_id