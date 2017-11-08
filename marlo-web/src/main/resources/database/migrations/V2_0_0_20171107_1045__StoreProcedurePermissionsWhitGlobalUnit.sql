
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Procedure structure for getPermissions
-- ----------------------------
DROP PROCEDURE
IF EXISTS `getPermissions`;
DELIMITER ;;


CREATE PROCEDURE `getPermissions` (IN v_user_id BIGINT(20))
BEGIN
  DROP TABLE
IF EXISTS user_permission ; CREATE TEMPORARY TABLE user_permission (
  id VARCHAR (500),
  ro_acronym VARCHAR (500),
  role_id VARCHAR (500),
  permission VARCHAR (500),
  project_id VARCHAR (500),
  crp_acronym VARCHAR (500),
  permission_id VARCHAR (500)
) ENGINE = MyISAM ; INSERT INTO user_permission SELECT
  `u`.`id` AS `id`,
  `r`.`acronym` AS `ro_acronym`,
  `r`.`id` AS `role_id`,
  REPLACE (
    `p`.`permission`,
    '{0}',
    `cp`.`acronym`
  ) AS `permission`,
  NULL AS `project_id`,
  `cp`.`acronym` AS `crp_acronym`,
  `p`.`id` AS `permission_id`
FROM
  (
    (
      (
        (
          (
            (
              `users` `u`
              LEFT JOIN `user_roles` `ro` ON ((`ro`.`user_id` = `u`.`id`))
            )
            JOIN `roles` `r` ON ((`r`.`id` = `ro`.`role_id`))
          )
          JOIN `role_permissions` `rp` ON ((`rp`.`role_id` = `r`.`id`))
        )
        JOIN `permissions` `p` ON (
          (
            `p`.`id` = `rp`.`permission_id`
          )
        )
      )
      JOIN `crp_users` `crp` ON (
        (
          (`u`.`id` = `crp`.`user_id`)
          AND (
            `crp`.`crp_id` = `r`.`crp_id`
          )
        )
      )
    )
    JOIN `global_units` `cp` ON ((`cp`.`id` = `crp`.`crp_id`))
  )
WHERE
  (
    (`p`.`type` = 0)
    AND (
      `r`.`id` <> 17
      AND r.acronym != 'FM'
    )
  )
AND u.id = v_user_id
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
    NULL AS `project_id`,
    `cp`.`acronym` AS `crp_acronym`,
    `p`.`id` AS `permission_id`
  FROM
    (
      (
        (
          (
            (
              `users` `u`
              LEFT JOIN `user_roles` `ro` ON ((`ro`.`user_id` = `u`.`id`))
            )
            JOIN `roles` `r` ON ((`r`.`id` = `ro`.`role_id`))
          )
          JOIN `role_permissions` `rp` ON ((`rp`.`role_id` = `r`.`id`))
        )
        JOIN `permissions` `p` ON (
          (
            `p`.`id` = `rp`.`permission_id`
          )
        )
      )
      JOIN `global_units` `cp`
    )
  WHERE
    (
      (`p`.`type` = 0)
      AND (`r`.`id` = 17)
    )
  AND u.id = v_user_id
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
        `pro`.`id`
      ) AS `replace(replace(``p``.``permission``,'{0}',cp.acronym),'{1}',pro.id)`,
      NULL AS `project_id`,
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
                      JOIN `user_roles` `ro` ON ((`ro`.`user_id` = `u`.`id`))
                    )
                    JOIN `roles` `r` ON ((`r`.`id` = `ro`.`role_id`))
                  )
                  JOIN `role_permissions` `rp` ON ((`rp`.`role_id` = `r`.`id`))
                )
                JOIN `permissions` `p` ON (
                  (
                    `p`.`id` = `rp`.`permission_id`
                  )
                )
              )
              JOIN `crp_users` `crp` ON (
                (
                  (`u`.`id` = `crp`.`user_id`)
                  AND (
                    `crp`.`crp_id` = `r`.`crp_id`
                  )
                )
              )
            )
            JOIN `crp_program_leaders` `cprog` ON (
              (
                (`cprog`.`user_id` = `u`.`id`)
                AND (`cprog`.`is_active` = 1)
              )
            )
          )
          JOIN `crp_programs` `pro` ON (
            (
              (
                `pro`.`id` = `cprog`.`crp_program_id`
              )
              AND (`pro`.`program_type` = 1)
              AND (`pro`.`is_active` = 1)
            )
          )
        )
        JOIN `global_units` `cp` ON (
          (
            (`cp`.`id` = `crp`.`crp_id`)
            AND (`pro`.`crp_id` = `cp`.`id`)
          )
        )
      )
    WHERE
      (
        (`p`.`type` = 3)
        AND (
          `r`.`acronym` IN ('FPL', 'FPM')
        )
      )
    AND u.id = v_user_id
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
          `pro`.`id`
        ) AS `name_exp_32`,
        `pro`.`id` AS `project_id`,
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
                          JOIN `user_roles` `ro` ON ((`ro`.`user_id` = `u`.`id`))
                        )
                        JOIN `roles` `r` ON ((`r`.`id` = `ro`.`role_id`))
                      )
                      JOIN `role_permissions` `rp` ON ((`rp`.`role_id` = `r`.`id`))
                    )
                    JOIN `permissions` `p` ON (
                      (
                        `p`.`id` = `rp`.`permission_id`
                      )
                    )
                  )
                  JOIN `crp_users` `crp` ON (
                    (
                      (`u`.`id` = `crp`.`user_id`)
                      AND (
                        `crp`.`crp_id` = `r`.`crp_id`
                      )
                    )
                  )
                )
                JOIN `global_units` `cp` ON ((`cp`.`id` = `crp`.`crp_id`))
              )
              JOIN `projects` `pro` ON (
                (
                  (`pro`.`crp_id` = `cp`.`id`)
                  AND (`pro`.`is_active` = 1)
                )
              )
            )
            JOIN `liaison_institutions` `lin` ON (
              (
                `lin`.`id` = `pro`.`liaison_institution_id`
              )
            )
          )
          JOIN `liaison_users` `lus` ON (
            (
              (
                `lus`.`institution_id` = `lin`.`id`
              )
              AND (`u`.`id` = `lus`.`user_id`)
              AND (`lus`.`is_active` = 1)
              AND lus.crp_id = cp.id
            )
          )
        )
      WHERE
        (
          (`p`.`type` = 1)
          AND (
            `r`.`acronym` IN (
              'PMU',
              'ML',
              'CP',
              'FPL',
              'FPM',
              'RPM',
              'RPL'
            )
          )
        )
      AND u.id = v_user_id
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
            `pro`.`id`
          ) AS `name_exp_32`,
          `pro`.`id` AS `project_id`,
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
                            JOIN `user_roles` `ro` ON ((`ro`.`user_id` = `u`.`id`))
                          )
                          JOIN `roles` `r` ON ((`r`.`id` = `ro`.`role_id`))
                        )
                        JOIN `role_permissions` `rp` ON ((`rp`.`role_id` = `r`.`id`))
                      )
                      JOIN `permissions` `p` ON (
                        (
                          `p`.`id` = `rp`.`permission_id`
                        )
                      )
                    )
                    JOIN `crp_users` `crp` ON (
                      (
                        (`u`.`id` = `crp`.`user_id`)
                        AND (
                          `crp`.`crp_id` = `r`.`crp_id`
                        )
                      )
                    )
                  )
                  JOIN `global_units` `cp` ON ((`cp`.`id` = `crp`.`crp_id`))
                )
                JOIN `projects` `pro` ON (
                  (
                    (`pro`.`crp_id` = `cp`.`id`)
                    AND (`pro`.`is_active` = 1)
                  )
                )
              )
              JOIN `liaison_institutions` `lin` ON (
                (
                  `lin`.`id` = `pro`.`liaison_institution_id`
                )
              )
            )
            JOIN `crp_program_leaders` `lus` ON (
              (
                (
                  `lus`.`crp_program_id` = `lin`.`crp_program`
                )
                AND lus.manager = 1
                AND (`u`.`id` = `lus`.`user_id`)
                AND (`lus`.`is_active` = 1)
              )
            )
          )
        WHERE
          (
            (`p`.`type` = 1)
            AND (
              `r`.`acronym` IN (
                'PMU',
                'ML',
                'CP',
                'FPL',
                'FPM',
                'RPM',
                'RPL'
              )
            )
          )
        AND u.id = v_user_id
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
              `pro`.`id`
            ) AS `name_exp_32`,
            `pro`.`id` AS `project_id`,
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
                              JOIN `user_roles` `ro` ON ((`ro`.`user_id` = `u`.`id`))
                            )
                            JOIN `roles` `r` ON ((`r`.`id` = `ro`.`role_id`))
                          )
                          JOIN `role_permissions` `rp` ON ((`rp`.`role_id` = `r`.`id`))
                        )
                        JOIN `permissions` `p` ON (
                          (
                            `p`.`id` = `rp`.`permission_id`
                          )
                        )
                      )
                      JOIN `crp_users` `crp` ON (
                        (
                          (`u`.`id` = `crp`.`user_id`)
                          AND (
                            `crp`.`crp_id` = `r`.`crp_id`
                          )
                        )
                      )
                    )
                    JOIN `global_units` `cp` ON ((`cp`.`id` = `crp`.`crp_id`))
                  )
                  JOIN `projects` `pro` ON (
                    (
                      (`pro`.`crp_id` = `cp`.`id`)
                      AND (`pro`.`is_active` = 1)
                    )
                  )
                )
                JOIN `project_cluster_activities` `lin` ON (
                  (
                    `lin`.`project_id` = `pro`.`id`
                    AND lin.is_active = 1
                  )
                )
              )
              JOIN `crp_cluster_activity_leaders` `lus` ON (
                (
                  (
                    `lus`.`cluster_activity_id` = `lin`.`cluster_activity_id`
                  )
                  AND (`u`.`id` = `lus`.`user_id`)
                  AND (`lus`.`is_active` = 1)
                )
              )
            )
          WHERE
            (
              (`p`.`type` = 1)
              AND (`r`.`acronym` IN('CL'))
            )
          AND u.id = v_user_id
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
                `pro`.`id`
              ) AS `name_exp_39`,
              `pro`.`id` AS `project_id`,
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
                              JOIN `user_roles` `ro` ON ((`ro`.`user_id` = `u`.`id`))
                            )
                            JOIN `roles` `r` ON ((`r`.`id` = `ro`.`role_id`))
                          )
                          JOIN `role_permissions` `rp` ON ((`rp`.`role_id` = `r`.`id`))
                        )
                        JOIN `permissions` `p` ON (
                          (
                            `p`.`id` = `rp`.`permission_id`
                          )
                        )
                      )
                      JOIN `crp_users` `crp` ON (
                        (
                          (`u`.`id` = `crp`.`user_id`)
                          AND (
                            `crp`.`crp_id` = `r`.`crp_id`
                          )
                        )
                      )
                    )
                    JOIN `global_units` `cp` ON ((`cp`.`id` = `crp`.`crp_id`))
                  )
                  JOIN `projects` `pro` ON (
                    (
                      (`pro`.`crp_id` = `cp`.`id`)
                      AND (`pro`.`is_active` = 1)
                    )
                  )
                )
                JOIN `liaison_users` `lus` ON (
                  (
                    (
                      `lus`.`id` = `pro`.`liaison_user_id`
                    )
                    AND (`u`.`id` = `lus`.`user_id`)
                    AND (`lus`.`is_active` = 1)
                    AND lus.crp_id = cp.id
                  )
                )
              )
            WHERE
              (
                (`p`.`type` = 1)
                AND (
                  `r`.`acronym` IN (
                    'PMU',
                    'ML',
                    'CP',
                    'FPL',
                    'FPM',
                    'RPM',
                    'RPL'
                  )
                )
              )
            AND u.id = v_user_id
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
                  `pro`.`id`
                ) AS `name_exp_39`,
                `pro`.`id` AS `project_id`,
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
                                JOIN `user_roles` `ro` ON ((`ro`.`user_id` = `u`.`id`))
                              )
                              JOIN `roles` `r` ON ((`r`.`id` = `ro`.`role_id`))
                            )
                            JOIN `role_permissions` `rp` ON ((`rp`.`role_id` = `r`.`id`))
                          )
                          JOIN `permissions` `p` ON (
                            (
                              `p`.`id` = `rp`.`permission_id`
                            )
                          )
                        )
                        JOIN `crp_users` `crp` ON (
                          (
                            (`u`.`id` = `crp`.`user_id`)
                            AND (
                              `crp`.`crp_id` = `r`.`crp_id`
                            )
                          )
                        )
                      )
                      JOIN `global_units` `cp` ON ((`cp`.`id` = `crp`.`crp_id`))
                    )
                    JOIN `projects` `pro` ON (
                      (
                        (`pro`.`crp_id` = `cp`.`id`)
                        AND (`pro`.`is_active` = 1)
                      )
                    )
                  )
                )
              WHERE
                (
                  (`p`.`type` = 1 AND p.id = 2)
                  AND (`r`.`acronym` IN('PMU'))
                )
              AND u.id = v_user_id
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
                    `pro`.`id`
                  ) AS `name_exp_46`,
                  `pro`.`id` AS `project_id`,
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
                                    JOIN `user_roles` `ro` ON ((`ro`.`user_id` = `u`.`id`))
                                  )
                                  JOIN `roles` `r` ON ((`r`.`id` = `ro`.`role_id`))
                                )
                                JOIN `role_permissions` `rp` ON ((`rp`.`role_id` = `r`.`id`))
                              )
                              JOIN `permissions` `p` ON (
                                (
                                  `p`.`id` = `rp`.`permission_id`
                                )
                              )
                            )
                            JOIN `crp_users` `crp` ON (
                              (
                                (`u`.`id` = `crp`.`user_id`)
                                AND (
                                  `crp`.`crp_id` = `r`.`crp_id`
                                )
                              )
                            )
                          )
                          JOIN `global_units` `cp` ON ((`cp`.`id` = `crp`.`crp_id`))
                        )
                        JOIN `projects` `pro` ON (
                          (
                            (`pro`.`crp_id` = `cp`.`id`)
                            AND (`pro`.`is_active` = 1)
                          )
                        )
                      )
                      JOIN `project_partners` `pp` ON (
                        (
                          (
                            `pp`.`project_id` = `pro`.`id`
                          )
                          AND (`pp`.`is_active` = 1)
                        )
                      )
                    )
                    JOIN `project_partner_persons` `pers` ON (
                      (
                        (
                          `pp`.`id` = `pers`.`project_partner_id`
                        )
                        AND (`pers`.`is_active` = 1)
                        AND (`pers`.`contact_type` = 'PL')
                        AND (`pers`.`user_id` = `u`.`id`)
                      )
                    )
                  )
                WHERE
                  (
                    (`p`.`type` = 1)
                    AND (`r`.`acronym` = 'PL')
                  )
                AND u.id = v_user_id
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
                      `pro`.`id`
                    ) AS `name_exp_46`,
                    `pro`.`id` AS `project_id`,
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
                                      JOIN `user_roles` `ro` ON ((`ro`.`user_id` = `u`.`id`))
                                    )
                                    JOIN `roles` `r` ON ((`r`.`id` = `ro`.`role_id`))
                                  )
                                  JOIN `role_permissions` `rp` ON ((`rp`.`role_id` = `r`.`id`))
                                )
                                JOIN `permissions` `p` ON (
                                  (
                                    `p`.`id` = `rp`.`permission_id`
                                  )
                                )
                              )
                              JOIN `crp_users` `crp` ON (
                                (
                                  (`u`.`id` = `crp`.`user_id`)
                                  AND (
                                    `crp`.`crp_id` = `r`.`crp_id`
                                  )
                                )
                              )
                            )
                            JOIN `global_units` `cp` ON ((`cp`.`id` = `crp`.`crp_id`))
                          )
                          JOIN `projects` `pro` ON (
                            (
                              (`pro`.`crp_id` = `cp`.`id`)
                              AND (`pro`.`is_active` = 1)
                            )
                          )
                        )
                        JOIN `project_partners` `pp` ON (
                          (
                            (
                              `pp`.`project_id` = `pro`.`id`
                            )
                            AND (`pp`.`is_active` = 1)
                          )
                        )
                      )
                      JOIN `project_partner_persons` `pers` ON (
                        (
                          (
                            `pp`.`id` = `pers`.`project_partner_id`
                          )
                          AND (`pers`.`is_active` = 1)
                          AND (`pers`.`contact_type` = 'PC')
                          AND (`pers`.`user_id` = `u`.`id`)
                        )
                      )
                    )
                  WHERE
                    (
                      (`p`.`type` = 1)
                      AND (`r`.`acronym` = 'PC')
                    )
                  AND u.id = v_user_id
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
                        `pro`.`id`
                      ) AS `name_exp_46`,
                      `pro`.`id` AS `project_id`,
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
                                    `users` `u`
                                    JOIN `user_roles` `ro` ON ((`ro`.`user_id` = `u`.`id`))
                                  )
                                  JOIN `roles` `r` ON ((`r`.`id` = `ro`.`role_id`))
                                )
                                JOIN `role_permissions` `rp` ON ((`rp`.`role_id` = `r`.`id`))
                              )
                              JOIN `permissions` `p` ON (
                                (
                                  `p`.`id` = `rp`.`permission_id`
                                )
                              )
                            )
                            JOIN `crp_users` `crp` ON (
                              (
                                (`u`.`id` = `crp`.`user_id`)
                                AND (
                                  `crp`.`crp_id` = `r`.`crp_id`
                                )
                              )
                            )
                          )
                          JOIN `global_units` `cp` ON ((`cp`.`id` = `crp`.`crp_id`))
                        )
                        JOIN `funding_sources` `pro` ON (
                          (
                            (`pro`.`crp_id` = `cp`.`id`)
                            AND (`pro`.`is_active` = 1)
                          )
                        )
                      )
                    WHERE
                      `p`.`id` IN (438, 462)
                    AND (
                      pro.type <> 1
                      OR pro.type IS NULL
                    )
                    AND r.`acronym` IN (
                      'PMU',
                      'ML',
                      'FPL',
                      'FPM',
                      'RPM',
                      'RPL'
                    )
                    AND u.id = v_user_id
                    UNION
                      SELECT
                        u.id,
                        `ro`.`acronym` AS `acronym`,
                        `ro`.`id` AS `rolid`,
                        REPLACE (
                          REPLACE (
                            `per`.`permission`,
                            '{0}',
                            `cp`.`acronym`
                          ),
                          '{1}',
                          pro.id
                        ) AS `name_exp_46`,
                        pro.id AS `project_id`,
                        `cp`.`acronym` AS `crp_acronym`,
                        `per`.`id` AS `permission_id`
                      FROM
                        users u
                      INNER JOIN user_roles urol ON urol.user_id = u.id
                      INNER JOIN roles ro ON ro.id = urol.role_id
                      AND ro.acronym = 'FM'
                      INNER JOIN crp_users cpu ON cpu.user_id = u.id
                      AND cpu.is_active = 1
                      INNER JOIN global_units cp ON cp.id = cpu.crp_id
                      INNER JOIN funding_sources pro ON pro.crp_id = cp.id
                      AND pro.is_active = 1
                      INNER JOIN role_permissions rp ON rp.role_id = ro.id
                      INNER JOIN permissions per ON per.id = rp.permission_id
                      WHERE
                        u.is_active = 1
                      AND per.id IN (438, 462)
                      AND u.id = v_user_id
                      UNION
                        SELECT
                          u.id,
                          `ro`.`acronym` AS `acronym`,
                          `ro`.`id` AS `rolid`,
                          REPLACE (
                            REPLACE (
                              `per`.`permission`,
                              '{0}',
                              `cp`.`acronym`
                            ),
                            '{1}',
                            pro.id
                          ) AS `name_exp_46`,
                          pro.id AS `project_id`,
                          `cp`.`acronym` AS `crp_acronym`,
                          `per`.`id` AS `permission_id`
                        FROM
                          users u
                        INNER JOIN user_roles urol ON urol.user_id = u.id
                        INNER JOIN roles ro ON ro.id = urol.role_id
                        AND ro.acronym = 'FM'
                        INNER JOIN crp_users cpu ON cpu.user_id = u.id
                        AND cpu.is_active = 1
                        INNER JOIN global_units cp ON cp.id = cpu.crp_id
                        INNER JOIN projects pro ON pro.crp_id = cp.id
                        INNER JOIN role_permissions rp ON rp.role_id = ro.id
                        INNER JOIN permissions per ON per.id = rp.permission_id
                        WHERE
                          u.is_active = 1
                        AND per.id NOT IN (438, 462)
                        AND pro.is_active = 1
                        AND u.id = v_user_id
                        UNION
                          SELECT
                            u.id,
                            `ro`.`acronym` AS `acronym`,
                            `ro`.`id` AS `rolid`,
                            REPLACE (
                              REPLACE (
                                `per`.`permission`,
                                '{0}',
                                `cp`.`acronym`
                              ),
                              '{1}',
                              pro.id
                            ) AS `name_exp_46`,
                            pro.id AS `project_id`,
                            `cp`.`acronym` AS `crp_acronym`,
                            `per`.`id` AS `permission_id`
                          FROM
                            users u
                          INNER JOIN user_roles urol ON urol.user_id = u.id
                          INNER JOIN roles ro ON ro.id = urol.role_id
                          AND ro.acronym = 'FM'
                          INNER JOIN crp_users cpu ON cpu.user_id = u.id
                          AND cpu.is_active = 1
                          INNER JOIN global_units cp ON cp.id = cpu.crp_id
                          INNER JOIN funding_sources pro ON pro.crp_id = cp.id
                          INNER JOIN role_permissions rp ON rp.role_id = ro.id
                          INNER JOIN permissions per ON per.id = rp.permission_id
                          WHERE
                            u.is_active = 1
                          AND per.id IN (438, 462)
                          AND pro.is_active = 1
                          AND u.id = v_user_id
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
                                  `pro`.`id`
                                ),
                                '{2}',
                                lin.institution_id
                              ) AS `name_exp_32`,
                              `pro`.`id` AS `project_id`,
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
                                                JOIN `user_roles` `ro` ON ((`ro`.`user_id` = `u`.`id`))
                                              )
                                              JOIN `roles` `r` ON ((`r`.`id` = `ro`.`role_id`))
                                            )
                                            JOIN `role_permissions` `rp` ON ((`rp`.`role_id` = `r`.`id`))
                                          )
                                          JOIN `permissions` `p` ON (
                                            (
                                              `p`.`id` = `rp`.`permission_id`
                                            )
                                          )
                                        )
                                        JOIN `crp_users` `crp` ON (
                                          (
                                            (`u`.`id` = `crp`.`user_id`)
                                            AND (
                                              `crp`.`crp_id` = `r`.`crp_id`
                                            )
                                          )
                                        )
                                      )
                                      JOIN `global_units` `cp` ON ((`cp`.`id` = `crp`.`crp_id`))
                                    )
                                    JOIN `projects` `pro` ON (
                                      (
                                        (`pro`.`crp_id` = `cp`.`id`)
                                        AND (`pro`.`is_active` = 1)
                                      )
                                    )
                                  )
                                  JOIN `liaison_users` `lus` ON (
                                    (
                                      (`u`.`id` = `lus`.`user_id`)
                                      AND (`lus`.`is_active` = 1)
                                      AND lus.crp_id = cp.id
                                    )
                                  )
                                )
                                JOIN `liaison_institutions` `lin` ON (
                                  (
                                    `lus`.`institution_id` = `lin`.`id`
                                  )
                                )
                              )
                            INNER JOIN project_partners pbi ON pbi.institution_id = lin.institution_id
                            AND pro.id = pbi.project_id
                            AND pbi.is_active = 1
                            WHERE
                              (
                                (`p`.`type` = 1)
                                AND (`r`.`acronym` IN('CP'))
                              )
                            AND p.id IN (447, 206, 429)
                            AND u.id = v_user_id
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
                                  `pro`.`id`
                                ) AS `name_exp_46`,
                                `pro`.`id` AS `project_id`,
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
                                              `users` `u`
                                              JOIN `user_roles` `ro` ON ((`ro`.`user_id` = `u`.`id`))
                                            )
                                            JOIN `roles` `r` ON ((`r`.`id` = `ro`.`role_id`))
                                          )
                                          JOIN `role_permissions` `rp` ON ((`rp`.`role_id` = `r`.`id`))
                                        )
                                        JOIN `permissions` `p` ON (
                                          (
                                            `p`.`id` = `rp`.`permission_id`
                                          )
                                        )
                                      )
                                      JOIN `crp_users` `crp` ON (
                                        (
                                          (`u`.`id` = `crp`.`user_id`)
                                          AND (
                                            `crp`.`crp_id` = `r`.`crp_id`
                                          )
                                        )
                                      )
                                    )
                                    JOIN `global_units` `cp` ON ((`cp`.`id` = `crp`.`crp_id`))
                                  )
                                  JOIN `funding_sources` `pro` ON (
                                    (
                                      (`pro`.`crp_id` = `cp`.`id`)
                                      AND (`pro`.`is_active` = 1)
                                    )
                                  )
                                )
                              INNER JOIN funding_source_institutions fin ON fin.funding_source_id = pro.id
                              INNER JOIN liaison_users lu ON lu.user_id = u.id
                              AND lu.crp_id = cp.id
                              INNER JOIN liaison_institutions li ON li.id = lu.institution_id
                              AND li.institution_id = fin.institution_id
                              WHERE
                                `p`.`id` IN (438, 462)
                              AND (
                                pro.type <> 1
                                OR pro.type IS NULL
                              )
                              AND r.`acronym` IN ('CP')
                              AND u.id = v_user_id
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
                                    `pro`.`id`
                                  ) AS `name_exp_46`,
                                  NULL AS `project_id`,
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
                                                `users` `u`
                                                JOIN `user_roles` `ro` ON ((`ro`.`user_id` = `u`.`id`))
                                              )
                                              JOIN `roles` `r` ON ((`r`.`id` = `ro`.`role_id`))
                                            )
                                            JOIN `role_permissions` `rp` ON ((`rp`.`role_id` = `r`.`id`))
                                          )
                                          JOIN `permissions` `p` ON (
                                            (
                                              `p`.`id` = `rp`.`permission_id`
                                            )
                                          )
                                        )
                                        JOIN `crp_users` `crp` ON (
                                          (
                                            (`u`.`id` = `crp`.`user_id`)
                                            AND (
                                              `crp`.`crp_id` = `r`.`crp_id`
                                            )
                                          )
                                        )
                                      )
                                      JOIN `global_units` `cp` ON ((`cp`.`id` = `crp`.`crp_id`))
                                    )
                                    JOIN `deliverables` `pro` ON (
                                      (
                                        (`pro`.`crp_id` = `cp`.`id`)
                                        AND (`pro`.`is_active` = 1)
                                      )
                                    )
                                  )
                                INNER JOIN deliverable_leaders fin ON fin.deliverable_id = pro.id
                                INNER JOIN liaison_users lu ON lu.user_id = u.id
                                AND lu.crp_id = cp.id
                                INNER JOIN liaison_institutions li ON li.id = lu.institution_id
                                AND li.institution_id = fin.instituion_id
                                WHERE
                                  `p`.`id` = 467
                                AND r.`acronym` IN ('CP')
                                AND u.id = v_user_id
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
                                      `li`.`ip_program`
                                    ) AS `name_exp_46`,
                                    NULL AS `project_id`,
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
                                                  `users` `u`
                                                  JOIN `user_roles` `ro` ON ((`ro`.`user_id` = `u`.`id`))
                                                )
                                                JOIN `roles` `r` ON ((`r`.`id` = `ro`.`role_id`))
                                              )
                                              JOIN `role_permissions` `rp` ON ((`rp`.`role_id` = `r`.`id`))
                                            )
                                            JOIN `permissions` `p` ON (
                                              (
                                                `p`.`id` = `rp`.`permission_id`
                                              )
                                            )
                                          )
                                          JOIN `crp_users` `crp` ON (
                                            (
                                              (`u`.`id` = `crp`.`user_id`)
                                              AND (
                                                `crp`.`crp_id` = `r`.`crp_id`
                                              )
                                            )
                                          )
                                        )
                                        JOIN `global_units` `cp` ON ((`cp`.`id` = `crp`.`crp_id`))
                                      )
                                    )
                                  INNER JOIN ip_liaison_users lu ON lu.user_id = u.id
                                  INNER JOIN ip_liaison_institutions li ON li.id = lu.institution_id
                                  WHERE
                                    `p`.`id` = 464
                                  AND r.`acronym` IN ('FPL', 'FPM', 'RPM', 'RPL')
                                  AND u.id = v_user_id
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
                                        `li`.`id`
                                      ) AS `name_exp_46`,
                                      NULL AS `project_id`,
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
                                                    `users` `u`
                                                    JOIN `user_roles` `ro` ON ((`ro`.`user_id` = `u`.`id`))
                                                  )
                                                  JOIN `roles` `r` ON ((`r`.`id` = `ro`.`role_id`))
                                                )
                                                JOIN `role_permissions` `rp` ON ((`rp`.`role_id` = `r`.`id`))
                                              )
                                              JOIN `permissions` `p` ON (
                                                (
                                                  `p`.`id` = `rp`.`permission_id`
                                                )
                                              )
                                            )
                                            JOIN `crp_users` `crp` ON (
                                              (
                                                (`u`.`id` = `crp`.`user_id`)
                                                AND (
                                                  `crp`.`crp_id` = `r`.`crp_id`
                                                )
                                              )
                                            )
                                          )
                                          JOIN `global_units` `cp` ON ((`cp`.`id` = `crp`.`crp_id`))
                                        )
                                      )
                                    INNER JOIN liaison_users lu ON lu.user_id = u.id
                                    AND lu.crp_id = cp.id
                                    INNER JOIN liaison_institutions li ON li.id = lu.institution_id
                                    WHERE
                                      `p`.`id` = 468
                                    AND r.`acronym` IN (
                                      'FPL',
                                      'FPM',
                                      'RPM',
                                      'RPL',
                                      'CP'
                                    )
                                    AND u.id = v_user_id
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
                                          `pro`.`id`
                                        ) AS `name_exp_46`,
                                        `pro`.`id` AS `project_id`,
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
                                                      `users` `u`
                                                      JOIN `user_roles` `ro` ON ((`ro`.`user_id` = `u`.`id`))
                                                    )
                                                    JOIN `roles` `r` ON ((`r`.`id` = `ro`.`role_id`))
                                                  )
                                                  JOIN `role_permissions` `rp` ON ((`rp`.`role_id` = `r`.`id`))
                                                )
                                                JOIN `permissions` `p` ON (
                                                  (
                                                    `p`.`id` = `rp`.`permission_id`
                                                  )
                                                )
                                              )
                                              JOIN `crp_users` `crp` ON (
                                                (
                                                  (`u`.`id` = `crp`.`user_id`)
                                                  AND (
                                                    `crp`.`crp_id` = `r`.`crp_id`
                                                  )
                                                )
                                              )
                                            )
                                            JOIN `global_units` `cp` ON ((`cp`.`id` = `crp`.`crp_id`))
                                          )
                                          JOIN `funding_sources` `pro` ON (
                                            (
                                              (`pro`.`crp_id` = `cp`.`id`)
                                              AND (`pro`.`is_active` = 1)
                                            )
                                          )
                                        )
                                      WHERE
                                        `p`.`id` IN (438, 462)
                                      AND pro.crp_id = 5
                                      AND r.`acronym` IN (
                                        'PMU',
                                        'ML',
                                        'FPL',
                                        'FPM',
                                        'RPM',
                                        'RPL'
                                      )
                                      AND u.id = v_user_id ;
                                      END;;
DELIMITER ;