DROP VIEW IF EXISTS `partners_contacts`;
CREATE VIEW `partners_contacts` AS
SELECT
  `pp`.`id` AS `id`,
  (
    CASE
    WHEN (
      (`u`.`id` = 0)
      OR isnull(`u`.`id`)
    ) THEN
      NULL
    ELSE
      concat(
        `u`.`last_name`,
        ', ',
        `u`.`first_name`,
        '\n                    ',
        '&lt;',
        `u`.`email`,
        '&gt;'
      )
    END
  ) AS `contact`,
  '' AS `responsibilities`,
  (
    CASE `ppp`.`contact_type`
    WHEN 'PL' THEN
      'Project Leader'
    WHEN 'PC' THEN
      'Project Coordinator'
    WHEN 'CP' THEN
      'Partner'
    ELSE
      NULL
    END
  ) AS `Type`
FROM
  (
    (
      `project_partners` `pp`
      JOIN `project_partner_persons` `ppp` ON (
        (
          (
            `ppp`.`project_partner_id` = `pp`.`id`
          )
          AND (`ppp`.`is_active` = 1)
        )
      )
    )
    JOIN `users` `u` ON ((`u`.`id` = `ppp`.`user_id`))
  )

