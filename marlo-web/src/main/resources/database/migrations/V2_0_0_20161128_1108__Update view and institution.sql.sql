UPDATE `institutions` SET `city`='San José' WHERE `id`='725';
DROP VIEW IF EXISTS `partners_contacts`;
CREATE  VIEW `partners_contacts` AS SELECT 
        `pp`.`id` AS `id`,
        (CASE
            WHEN ((`u`.`id` = 0) OR ISNULL(`u`.`id`)) THEN NULL
            ELSE CONCAT(`u`.`last_name`,
                    ', ',
                    `u`.`first_name`,
                    '
                    ',
                    '&lt;',
                    `u`.`email`,
                    '&gt;')
        END) AS `contact`,
        (CASE
            WHEN
                (ISNULL(`ppp`.`responsibilities`)
                    OR (`ppp`.`responsibilities` = ''))
            THEN
                NULL
            ELSE `ppp`.`responsibilities`
        END) AS `responsibilities`,
        (CASE `ppp`.`contact_type`
            WHEN 'PL' THEN 'Project Leader'
            WHEN 'PC' THEN 'Project Coordinator'
            WHEN 'CP' THEN 'Partner'
            ELSE NULL
        END) AS `Type`,
        (CASE
            WHEN ISNULL(`ins`.`headquarter`) THEN 'HQ'
            WHEN
                (((`ins`.`city` = '')
                    OR ISNULL(`ins`.`city`))
                    AND ((`le`.`name` = '')
                    OR ISNULL(`le`.`name`)))
            THEN
                NULL
            WHEN
                (((`ins`.`city` = '')
                    OR ISNULL(`ins`.`city`))
                    AND (`le`.`name` <> '')
                    AND (`le`.`name` IS NOT NULL))
            THEN
                `le`.`name`
            ELSE CONCAT(`ins`.`city`, ', ', `le`.`name`)
        END) AS `Branch`
    FROM
        ((((`project_partners` `pp`
        JOIN `project_partner_persons` `ppp` ON (((`ppp`.`project_partner_id` = `pp`.`id`)
            AND (`ppp`.`is_active` = 1))))
        JOIN `users` `u` ON ((`u`.`id` = `ppp`.`user_id`)))
        LEFT JOIN `institutions` `ins` ON ((`ins`.`id` = `ppp`.`institution_id`)))
        LEFT JOIN `loc_elements` `le` ON (((`le`.`id` = `ins`.`country_id`)
            AND (`le`.`is_active` = 1))))