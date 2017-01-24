DROP VIEW IF EXISTS `budgets_view`;
CREATE  VIEW `budgets_view` AS SELECT
  `p`.`id` AS `project_id`,
  (
    CASE
    WHEN (
      isnull(`p`.`title`)
      OR (`p`.`title` = '')
    ) THEN
      ''
    ELSE
      `p`.`title`
    END
  ) AS `title`,
  (
    CASE
    WHEN (
      (
        isnull(`ins`.`acronym`)
        OR (`ins`.`acronym` = '')
      )
      AND (
        isnull(`ins`.`name`)
        OR (`ins`.`name` = '')
      )
    ) THEN
      ''
    WHEN (
      (
        isnull(`ins`.`acronym`)
        OR (`ins`.`acronym` = '')
      )
      AND (`ins`.`name` IS NOT NULL)
    ) THEN
      `ins`.`name`
    ELSE
      concat(
        `ins`.`acronym`,
        ' - ',
        `ins`.`name`
      )
    END
  ) AS `Partner`,
  (
    SELECT
      (
        CASE
        WHEN isnull(`pb2`.`amount`) THEN
          0
        ELSE
          sum(`pb2`.`amount`)
        END
      ) AS `budget`
    FROM
      `project_budgets` `pb2`
    WHERE
      (
        (
          `pb2`.`project_id` = `p`.`id`
        )
        AND (`pb2`.`budget_type` = 1)
        AND (
          `pb2`.`institution_id` = `ins`.`id`
        )
        AND (`pb2`.`year` = `pb`.`year`)
        AND (`pb2`.`is_active` = 1)
      )
  ) AS `budget w1/w2`,
  (
    SELECT
      (
        CASE
        WHEN isnull(`pb2`.`amount`) THEN
          0
        ELSE
          sum(
            (
              (
                `pb2`.`gender_percentage` * `pb2`.`amount`
              ) / 100
            )
          )
        END
      ) AS `budget`
    FROM
      `project_budgets` `pb2`
    WHERE
      (
        (
          `pb2`.`project_id` = `p`.`id`
        )
        AND (`pb2`.`budget_type` = 1)
        AND (
          `pb2`.`institution_id` = `ins`.`id`
        )
        AND (`pb2`.`year` = `pb`.`year`)
        AND (`pb2`.`is_active` = 1)
      )
  ) AS `gender w1/w2`,
  (
    SELECT
      (
        CASE
        WHEN isnull(`pb2`.`amount`) THEN
          0
        ELSE
          sum(`pb2`.`amount`)
        END
      ) AS `budget`
    FROM
      `project_budgets` `pb2`
    WHERE
      (
        (
          `pb2`.`project_id` = `p`.`id`
        )
        AND (`pb2`.`budget_type` = 2)
        AND (
          `pb2`.`institution_id` = `ins`.`id`
        )
        AND (`pb2`.`year` = `pb`.`year`)
        AND (`pb2`.`is_active` = 1)
      )
  ) AS `budget w3`,
  (
    SELECT
      (
        CASE
        WHEN isnull(`pb2`.`amount`) THEN
          0
        ELSE
          sum(
            (
              (
                `pb2`.`gender_percentage` * `pb2`.`amount`
              ) / 100
            )
          )
        END
      ) AS `budget`
    FROM
      `project_budgets` `pb2`
    WHERE
      (
        (
          `pb2`.`project_id` = `p`.`id`
        )
        AND (`pb2`.`budget_type` = 2)
        AND (
          `pb2`.`institution_id` = `ins`.`id`
        )
        AND (`pb2`.`year` = `pb`.`year`)
        AND (`pb2`.`is_active` = 1)
      )
  ) AS `gender w3`,
  (
    SELECT
      (
        CASE
        WHEN isnull(`pb2`.`amount`) THEN
          0
        ELSE
          sum(`pb2`.`amount`)
        END
      ) AS `budget`
    FROM
      `project_budgets` `pb2`
    WHERE
      (
        (
          `pb2`.`project_id` = `p`.`id`
        )
        AND (`pb2`.`budget_type` = 3)
        AND (
          `pb2`.`institution_id` = `ins`.`id`
        )
        AND (`pb2`.`year` = `pb`.`year`)
        AND (`pb2`.`is_active` = 1)
      )
  ) AS `budget bilateral`,
  (
    SELECT
      (
        CASE
        WHEN isnull(`pb2`.`amount`) THEN
          0
        ELSE
          sum(
            (
              (
                `pb2`.`gender_percentage` * `pb2`.`amount`
              ) / 100
            )
          )
        END
      ) AS `budget`
    FROM
      `project_budgets` `pb2`
    WHERE
      (
        (
          `pb2`.`project_id` = `p`.`id`
        )
        AND (`pb2`.`budget_type` = 3)
        AND (
          `pb2`.`institution_id` = `ins`.`id`
        )
        AND (`pb2`.`year` = `pb`.`year`)
        AND (`pb2`.`is_active` = 1)
      )
  ) AS `gender bilateral`,
(
    SELECT
      (
        CASE
        WHEN isnull(`pb2`.`amount`) THEN
          0
        ELSE
          sum(`pb2`.`amount`)
        END
      ) AS `budget`
    FROM
      `project_budgets` `pb2`
    WHERE
      (
        (
          `pb2`.`project_id` = `p`.`id`
        )
        AND (`pb2`.`budget_type` = 4)
        AND (
          `pb2`.`institution_id` = `ins`.`id`
        )
        AND (`pb2`.`year` = `pb`.`year`)
        AND (`pb2`.`is_active` = 1)
      )
  ) AS `budget center`,
  (
    SELECT
      (
        CASE
        WHEN isnull(`pb2`.`amount`) THEN
          0
        ELSE
          sum(
            (
              (
                `pb2`.`gender_percentage` * `pb2`.`amount`
              ) / 100
            )
          )
        END
      ) AS `budget`
    FROM
      `project_budgets` `pb2`
    WHERE
      (
        (
          `pb2`.`project_id` = `p`.`id`
        )
        AND (`pb2`.`budget_type` = 4)
        AND (
          `pb2`.`institution_id` = `ins`.`id`
        )
        AND (`pb2`.`year` = `pb`.`year`)
        AND (`pb2`.`is_active` = 1)
      )
  ) AS `gender center`,
  count(0) AS `Total Funding sources`,
  `pb`.`year` AS `year`,
  `p`.`crp_id` AS `crp_id`
FROM
  (
    (
      `project_budgets` `pb`
      LEFT JOIN `institutions` `ins` ON (
        (
          `ins`.`id` = `pb`.`institution_id`
        )
      )
    )
    JOIN `projects` `p` ON (
      (
        (`p`.`id` = `pb`.`project_id`)
        AND (`p`.`is_active` = 1)
      )
    )
  )
WHERE
  (`pb`.`is_active` = 1)
GROUP BY
  `p`.`id`,
  `ins`.`id`,
  `pb`.`year`
ORDER BY
  `pb`.`project_id`