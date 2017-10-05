DROP VIEW
IF EXISTS `center_user_permissions`;

CREATE VIEW `center_user_permissions` AS SELECT
  `u`.`id` AS `id`,
  `r`.`acronym` AS `ro_acronym`,
  `r`.`id` AS `role_id`,
  REPLACE (
    `p`.`permission`,
    '{0}',
    `cp`.`acronym`
  ) AS `permission`,
  `cp`.`acronym` AS `center_acronym`,
  `p`.`id` AS `permission_id`
FROM
  (
    (
      (
        (
          (
            `users` `u`
            LEFT JOIN `center_user_roles` `ro` ON ((`ro`.`user_id` = `u`.`id`))
          )
          JOIN `center_roles` `r` ON ((`r`.`id` = `ro`.`role_id`))
        )
        JOIN `center_role_permissions` `rp` ON ((`rp`.`role_id` = `r`.`id`))
      )
      JOIN `permissions` `p` ON (
        (
          `p`.`id` = `rp`.`permission_id`
        )
      )
    )
    JOIN `centers` `cp`
  )
WHERE
  (
    (`p`.`type` = 0)
    AND (`r`.`id` = 6)
  )
UNION
SELECT
  `u`.`id` AS `id`,
  `r`.`acronym` AS `ro_acronym`,
  `r`.`id` AS `role_id`,
  REPLACE (
    `p`.`permission`,
    '{0}',
    `cp`.`acronym`
  ) AS `permission`,
  `cp`.`acronym` AS `center_acronym`,
  `p`.`id` AS `permission_id`
FROM
  (
    (
      (
        (
          (
            `users` `u`
            LEFT JOIN `center_user_roles` `ro` ON ((`ro`.`user_id` = `u`.`id`))
          )
          JOIN `center_roles` `r` ON ((`r`.`id` = `ro`.`role_id`))
        )
        JOIN `center_role_permissions` `rp` ON ((`rp`.`role_id` = `r`.`id`))
      )
      JOIN `permissions` `p` ON (
        (
          `p`.`id` = `rp`.`permission_id`
        )
      )
    )
    JOIN `centers` `cp`
  )
WHERE
  (
    (`p`.`type` = 0)
    AND (`r`.`acronym` = 'Admin')
  )
UNION
  SELECT
    `u`.`id` AS `id`,
    `r`.`acronym` AS `acronym`,
    `r`.`id` AS `rolid`,
    REPLACE (
      REPLACE (
        `p`.`permission`,
        '{0}',
        `cp`.`acronym`
      ),
      '{1}',
      `ar`.`id`
    ) AS `permission`,
    `cp`.`acronym` AS `crp_acronym`,
    `p`.`id` AS `permission_id`
  FROM
    (
      (
        (
          (
            (
              (
                (
                  (
                    `users` `u`
                    JOIN `center_user_roles` `ro` ON ((`ro`.`user_id` = `u`.`id`))
                  )
                  JOIN `center_roles` `r` ON ((`r`.`id` = `ro`.`role_id`))
                )
                JOIN `center_role_permissions` `rp` ON ((`rp`.`role_id` = `r`.`id`))
              )
              JOIN `permissions` `p` ON (
                (
                  `p`.`id` = `rp`.`permission_id`
                )
              )
            )
            JOIN `center_users` `crp` ON (
              (
                (`u`.`id` = `crp`.`user_id`)
                AND (
                  `crp`.`center_id` = `r`.`center_id`
                )
              )
            )
          )
          JOIN `centers` `cp` ON (
            (
              `cp`.`id` = `crp`.`center_id`
            )
          )
        )
        JOIN `center_areas` `ar` ON (
          (
            (
              `ar`.`research_center_id` = `cp`.`id`
            )
            AND (`ar`.`is_active` = 1)
          )
        )
      )
    )
  WHERE
    (
      (`p`.`type` = 0)
      AND (`r`.`acronym` = 'Coord')
    )
  UNION
    SELECT
      `u`.`id` AS `id`,
      `r`.`acronym` AS `acronym`,
      `r`.`id` AS `rolid`,
      REPLACE (
        REPLACE (
          `p`.`permission`,
          '{0}',
          `cp`.`acronym`
        ),
        '{1}',
        `ar`.`id`
      ) AS `permission`,
      `cp`.`acronym` AS `crp_acronym`,
      `p`.`id` AS `permission_id`
    FROM
      (
        (
          (
            (
              (
                (
                  (
                    (
                      `users` `u`
                      JOIN `center_user_roles` `ro` ON ((`ro`.`user_id` = `u`.`id`))
                    )
                    JOIN `center_roles` `r` ON ((`r`.`id` = `ro`.`role_id`))
                  )
                  JOIN `center_role_permissions` `rp` ON ((`rp`.`role_id` = `r`.`id`))
                )
                JOIN `permissions` `p` ON (
                  (
                    `p`.`id` = `rp`.`permission_id`
                  )
                )
              )
              JOIN `center_users` `crp` ON (
                (
                  (`u`.`id` = `crp`.`user_id`)
                  AND (
                    `crp`.`center_id` = `r`.`center_id`
                  )
                )
              )
            )
            JOIN `centers` `cp` ON (
              (
                `cp`.`id` = `crp`.`center_id`
              )
            )
          )
          JOIN `center_areas` `ar` ON (
            (
              (
                `ar`.`research_center_id` = `cp`.`id`
              )
              AND (`ar`.`is_active` = 1)
            )
          )
        )
        JOIN `center_leaders` `cprog` ON (
          (
            (`cprog`.`user_id` = `u`.`id`)
            AND (`cprog`.`is_active` = 1)
            AND (
              `cprog`.`research_area_id` = `ar`.`id`
            )
          )
        )
      )
    WHERE
      (
        (`p`.`type` = 0)
        AND (`r`.`acronym` = 'RAD')
      )
    UNION
      SELECT
        `u`.`id` AS `id`,
        `r`.`acronym` AS `acronym`,
        `r`.`id` AS `rolid`,
        REPLACE (
          REPLACE (
            REPLACE (
              `p`.`permission`,
              '{0}',
              `cp`.`acronym`
            ),
            '{1}',
            `ar`.`id`
          ),
          '{2}',
          `rprog`.`id`
        ) AS `permission`,
        `cp`.`acronym` AS `crp_acronym`,
        `p`.`id` AS `permission_id`
      FROM
        (
          (
            (
              (
                (
                  (
                    (
                      (
                        (
                          `users` `u`
                          JOIN `center_user_roles` `ro` ON ((`ro`.`user_id` = `u`.`id`))
                        )
                        JOIN `center_roles` `r` ON ((`r`.`id` = `ro`.`role_id`))
                      )
                      JOIN `center_role_permissions` `rp` ON ((`rp`.`role_id` = `r`.`id`))
                    )
                    JOIN `permissions` `p` ON (
                      (
                        `p`.`id` = `rp`.`permission_id`
                      )
                    )
                  )
                  JOIN `center_users` `crp` ON (
                    (
                      (`u`.`id` = `crp`.`user_id`)
                      AND (
                        `crp`.`center_id` = `r`.`center_id`
                      )
                    )
                  )
                )
                JOIN `centers` `cp` ON (
                  (
                    `cp`.`id` = `crp`.`center_id`
                  )
                )
              )
              JOIN `center_areas` `ar` ON (
                (
                  (
                    `ar`.`research_center_id` = `cp`.`id`
                  )
                  AND (`ar`.`is_active` = 1)
                )
              )
            )
            JOIN `center_programs` `rprog` ON (
              (
                (
                  `rprog`.`research_area_id` = `ar`.`id`
                )
                AND (`ar`.`is_active` = 1)
              )
            )
          )
          JOIN `center_leaders` `cprog` ON (
            (
              (`cprog`.`user_id` = `u`.`id`)
              AND (`cprog`.`is_active` = 1)
              AND (
                `cprog`.`research_program_id` = `rprog`.`id`
              )
            )
          )
        )
      WHERE
        (
          (`p`.`type` = 0)
          AND (`r`.`acronym` = 'RPL')
        )