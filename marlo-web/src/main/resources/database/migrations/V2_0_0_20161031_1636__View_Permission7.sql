  DROP VIEW IF EXISTS `user_permissions`;
  CREATE  VIEW `user_permissions` AS
  SELECT    `u`.`id`                                       AS `id`, 
          `r`.`acronym`                                  AS `ro_acronym`, 
          `r`.`id`                                       AS `role_id`, 
          replace(`p`.`permission`,'{0}',`cp`.`acronym`) AS `permission`, 
          NULL                                           AS `project_id`, 
          `cp`.`acronym`                                 AS `crp_acronym`, 
          `p`.`id`                                       AS `permission_id` 
FROM      ((((((`users` `u` 
LEFT JOIN `user_roles` `ro` 
ON       (( 
                              `ro`.`user_id` = `u`.`id`))) 
JOIN      `roles` `r` 
ON       (( 
                              `r`.`id` = `ro`.`role_id`))) 
JOIN      `role_permissions` `rp` 
ON       (( 
                              `rp`.`role_id` = `r`.`id`))) 
JOIN      `permissions` `p` 
ON       (( 
                              `p`.`id` = `rp`.`permission_id`))) 
JOIN      `crp_users` `crp` 
ON       ((( 
                                        `u`.`id` = `crp`.`user_id`) 
                    AND       ( 
                                        `crp`.`crp_id` = `r`.`crp_id`)))) 
JOIN      `crps` `cp` 
ON       (( 
                              `cp`.`id` = `crp`.`crp_id`))) 
WHERE     (( 
                              `p`.`type` = 0) 
          AND       ( 
                              `r`.`id` <> 17)) 
UNION 
SELECT    `u`.`id`                                       AS `id`, 
          `r`.`acronym`                                  AS `ro_acronym`, 
          `r`.`id`                                       AS `role_id`, 
          replace(`p`.`permission`,'{0}',`cp`.`acronym`) AS `permission`, 
          NULL                                           AS `project_id`, 
          `cp`.`acronym`                                 AS `crp_acronym`, 
          `p`.`id`                                       AS `permission_id` 
FROM      (((((`users` `u` 
LEFT JOIN `user_roles` `ro` 
ON       (( 
                              `ro`.`user_id` = `u`.`id`))) 
JOIN      `roles` `r` 
ON       (( 
                              `r`.`id` = `ro`.`role_id`))) 
JOIN      `role_permissions` `rp` 
ON       (( 
                              `rp`.`role_id` = `r`.`id`))) 
JOIN      `permissions` `p` 
ON       (( 
                              `p`.`id` = `rp`.`permission_id`))) 
JOIN      `crps` `cp`) 
WHERE     (( 
                              `p`.`type` = 0) 
          AND       ( 
                              `r`.`id` = 17)) 
UNION 
SELECT `u`.`id`                                                                 AS `id`, 
       `r`.`acronym`                                                            AS `acronym`,
       `r`.`id`                                                                 AS `rolid`, 
       replace(replace(`p`.`permission`,'{0}',`cp`.`acronym`),'{1}',`pro`.`id`) AS `replace(replace(``p``.``permission``,'{0}',cp.acronym),'{1}',pro.id)`,
       NULL                                                                     AS `project_id`,
       `cp`.`acronym`                                                           AS `crp_acronym`,
       `p`.`id`                                                                 AS `permission_id`
FROM   ((((((((`users` `u` 
JOIN   `user_roles` `ro` 
ON    (( 
                     `ro`.`user_id` = `u`.`id`))) 
JOIN   `roles` `r` 
ON    (( 
                     `r`.`id` = `ro`.`role_id`))) 
JOIN   `role_permissions` `rp` 
ON    (( 
                     `rp`.`role_id` = `r`.`id`))) 
JOIN   `permissions` `p` 
ON    (( 
                     `p`.`id` = `rp`.`permission_id`))) 
JOIN   `crp_users` `crp` 
ON    ((( 
                            `u`.`id` = `crp`.`user_id`) 
              AND    ( 
                            `crp`.`crp_id` = `r`.`crp_id`)))) 
JOIN   `crp_program_leaders` `cprog` 
ON    ((( 
                            `cprog`.`user_id` = `u`.`id`) 
              AND    ( 
                            `cprog`.`is_active` = 1)))) 
JOIN   `crp_programs` `pro` 
ON    ((( 
                            `pro`.`id` = `cprog`.`crp_program_id`) 
              AND    ( 
                            `pro`.`program_type` = 1) 
              AND    ( 
                            `pro`.`is_active` = 1)))) 
JOIN   `crps` `cp` 
ON    ((( 
                            `cp`.`id` = `crp`.`crp_id`) 
              AND    ( 
                            `pro`.`crp_id` = `cp`.`id`)))) 
WHERE  (( 
                     `p`.`type` = 3) 
       AND    ( 
                     `r`.`acronym` = 'FPL')) 
UNION 
SELECT `u`.`id`                                                                 AS `id`, 
       `r`.`acronym`                                                            AS `acronym`,
       `r`.`id`                                                                 AS `rolid`, 
       replace(replace(`p`.`permission`,'{0}',`cp`.`acronym`),'{1}',`pro`.`id`) AS `name_exp_32`,
       `pro`.`id`                                                               AS `project_id`,
       `cp`.`acronym`                                                           AS `crp_acronym`,
       `p`.`id`                                                                 AS `permission_id`
FROM   (((((((((`users` `u` 
JOIN   `user_roles` `ro` 
ON    (( 
                     `ro`.`user_id` = `u`.`id`))) 
JOIN   `roles` `r` 
ON    (( 
                     `r`.`id` = `ro`.`role_id`))) 
JOIN   `role_permissions` `rp` 
ON    (( 
                     `rp`.`role_id` = `r`.`id`))) 
JOIN   `permissions` `p` 
ON    (( 
                     `p`.`id` = `rp`.`permission_id`))) 
JOIN   `crp_users` `crp` 
ON    ((( 
                            `u`.`id` = `crp`.`user_id`) 
              AND    ( 
                            `crp`.`crp_id` = `r`.`crp_id`)))) 
JOIN   `crps` `cp` 
ON    (( 
                     `cp`.`id` = `crp`.`crp_id`))) 
JOIN   `projects` `pro` 
ON    ((( 
                            `pro`.`crp_id` = `cp`.`id`) 
              AND    ( 
                            `pro`.`is_active` = 1)))) 
JOIN   `liaison_institutions` `lin` 
ON    (( 
                     `lin`.`id` = `pro`.`liaison_institution_id`))) 
JOIN   `liaison_users` `lus` 
ON    ((( 
                            `lus`.`institution_id` = `lin`.`id`) 
              AND    ( 
                            `u`.`id` = `lus`.`user_id`) 
              AND    ( 
                            `lus`.`is_active` = 1)))) 
WHERE  (( 
                     `p`.`type` = 1) 
       AND    ( 
                     `r`.`acronym` IN ('PMU', 
                                       'ML', 
                                       'CP', 
                                       'FPL', 
                                       'RPL'))) 
UNION 
SELECT `u`.`id`                                                                 AS `id`, 
       `r`.`acronym`                                                            AS `acronym`,
       `r`.`id`                                                                 AS `rolid`, 
       replace(replace(`p`.`permission`,'{0}',`cp`.`acronym`),'{1}',`pro`.`id`) AS `name_exp_39`,
       `pro`.`id`                                                               AS `project_id`,
       `cp`.`acronym`                                                           AS `crp_acronym`,
       `p`.`id`                                                                 AS `permission_id`
FROM   ((((((((`users` `u` 
JOIN   `user_roles` `ro` 
ON    (( 
                     `ro`.`user_id` = `u`.`id`))) 
JOIN   `roles` `r` 
ON    (( 
                     `r`.`id` = `ro`.`role_id`))) 
JOIN   `role_permissions` `rp` 
ON    (( 
                     `rp`.`role_id` = `r`.`id`))) 
JOIN   `permissions` `p` 
ON    (( 
                     `p`.`id` = `rp`.`permission_id`))) 
JOIN   `crp_users` `crp` 
ON    ((( 
                            `u`.`id` = `crp`.`user_id`) 
              AND    ( 
                            `crp`.`crp_id` = `r`.`crp_id`)))) 
JOIN   `crps` `cp` 
ON    (( 
                     `cp`.`id` = `crp`.`crp_id`))) 
JOIN   `projects` `pro` 
ON    ((( 
                            `pro`.`crp_id` = `cp`.`id`) 
              AND    ( 
                            `pro`.`is_active` = 1)))) 
JOIN   `liaison_users` `lus` 
ON    ((( 
                            `lus`.`id` = `pro`.`liaison_user_id`) 
              AND    ( 
                            `u`.`id` = `lus`.`user_id`) 
              AND    ( 
                            `lus`.`is_active` = 1)))) 
WHERE  (( 
                     `p`.`type` = 1) 
       AND    ( 
                     `r`.`acronym` IN ('PMU', 
                                       'ML', 
                                       'CP', 
                                       'FPL', 
                                       'RPL'))) 
UNION 
SELECT `u`.`id`                                                                 AS `id`, 
       `r`.`acronym`                                                            AS `acronym`,
       `r`.`id`                                                                 AS `rolid`, 
       replace(replace(`p`.`permission`,'{0}',`cp`.`acronym`),'{1}',`pro`.`id`) AS `name_exp_46`,
       `pro`.`id`                                                               AS `project_id`,
       `cp`.`acronym`                                                           AS `crp_acronym`,
       `p`.`id`                                                                 AS `permission_id`
FROM   (((((((((`users` `u` 
JOIN   `user_roles` `ro` 
ON    (( 
                     `ro`.`user_id` = `u`.`id`))) 
JOIN   `roles` `r` 
ON    (( 
                     `r`.`id` = `ro`.`role_id`))) 
JOIN   `role_permissions` `rp` 
ON    (( 
                     `rp`.`role_id` = `r`.`id`))) 
JOIN   `permissions` `p` 
ON    (( 
                     `p`.`id` = `rp`.`permission_id`))) 
JOIN   `crp_users` `crp` 
ON    ((( 
                            `u`.`id` = `crp`.`user_id`) 
              AND    ( 
                            `crp`.`crp_id` = `r`.`crp_id`)))) 
JOIN   `crps` `cp` 
ON    (( 
                     `cp`.`id` = `crp`.`crp_id`))) 
JOIN   `projects` `pro` 
ON    ((( 
                            `pro`.`crp_id` = `cp`.`id`) 
              AND    ( 
                            `pro`.`is_active` = 1)))) 
JOIN   `project_partners` `pp` 
ON    ((( 
                            `pp`.`project_id` = `pro`.`id`) 
              AND    ( 
                            `pp`.`is_active` = 1)))) 
JOIN   `project_partner_persons` `pers` 
ON    ((( 
                            `pp`.`id` = `pers`.`project_partner_id`) 
              AND    ( 
                            `pers`.`is_active` = 1) 
              AND    ( 
                            `pers`.`contact_type` = 'PL') 
              AND    ( 
                            `pers`.`user_id` = `u`.`id`)))) 
WHERE  (( 
                     `p`.`type` = 1) 
       AND    ( 
                     `r`.`acronym` = 'PL')) 
UNION 
SELECT `u`.`id`                                                                 AS `id`, 
       `r`.`acronym`                                                            AS `acronym`,
       `r`.`id`                                                                 AS `rolid`, 
       replace(replace(`p`.`permission`,'{0}',`cp`.`acronym`),'{1}',`pro`.`id`) AS `name_exp_46`,
       `pro`.`id`                                                               AS `project_id`,
       `cp`.`acronym`                                                           AS `crp_acronym`,
       `p`.`id`                                                                 AS `permission_id`
FROM   (((((((((`users` `u` 
JOIN   `user_roles` `ro` 
ON    (( 
                     `ro`.`user_id` = `u`.`id`))) 
JOIN   `roles` `r` 
ON    (( 
                     `r`.`id` = `ro`.`role_id`))) 
JOIN   `role_permissions` `rp` 
ON    (( 
                     `rp`.`role_id` = `r`.`id`))) 
JOIN   `permissions` `p` 
ON    (( 
                     `p`.`id` = `rp`.`permission_id`))) 
JOIN   `crp_users` `crp` 
ON    ((( 
                            `u`.`id` = `crp`.`user_id`) 
              AND    ( 
                            `crp`.`crp_id` = `r`.`crp_id`)))) 
JOIN   `crps` `cp` 
ON    (( 
                     `cp`.`id` = `crp`.`crp_id`))) 
JOIN   `projects` `pro` 
ON    ((( 
                            `pro`.`crp_id` = `cp`.`id`) 
              AND    ( 
                            `pro`.`is_active` = 1)))) 
JOIN   `project_partners` `pp` 
ON    ((( 
                            `pp`.`project_id` = `pro`.`id`) 
              AND    ( 
                            `pp`.`is_active` = 1)))) 
JOIN   `project_partner_persons` `pers` 
ON    ((( 
                            `pp`.`id` = `pers`.`project_partner_id`) 
              AND    ( 
                            `pers`.`is_active` = 1) 
              AND    ( 
                            `pers`.`contact_type` = 'PC') 
              AND    ( 
                            `pers`.`user_id` = `u`.`id`)))) 
WHERE  (( 
                     `p`.`type` = 1) 
       AND    ( 
                     `r`.`acronym` = 'PC')) 
;