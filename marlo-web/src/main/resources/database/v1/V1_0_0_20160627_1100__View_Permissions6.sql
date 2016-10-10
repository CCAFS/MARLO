DROP VIEW IF EXISTS `user_permissions`;

CREATE  VIEW `user_permissions` AS 
SELECT    `u`.`id`                                       AS `id`, 
          `r`.`acronym`                                  AS `ro_acronym`, 
          `r`.`id`                                       AS `role_id`, 
          replace(`p`.`permission`,'{0}',`cp`.`acronym`) AS `permission`, 
          `cp`.`acronym`                                 AS `crp_acronym` ,
      `p`.`id`                     AS `permission_id`
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
SELECT `u`.`id`                                                                   AS `id`, 
       `r`.`acronym`                                                              AS `acronym`,
       `r`.`id`                                                                   AS `rolid`,
       replace(replace(`p`.`permission`,'{0}',`cp`.`acronym`),'{1}','project_id') AS `name_exp_9`,
       `cp`.`acronym`                                                             AS `crp_acronym`,
      `p`.`id`                     AS `permission_id`
FROM   ((((((`users` `u` 
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
WHERE  (( 
                     `p`.`type` = 1) 
       AND    ( 
                     `r`.`id` <> 17)) 
UNION 
SELECT    `u`.`id`                                       AS `id`, 
          `r`.`acronym`                                  AS `ro_acronym`, 
          `r`.`id`                                       AS `role_id`, 
          replace(`p`.`permission`,'{0}',`cp`.`acronym`) AS `permission`, 
          `cp`.`acronym`                                 AS `crp_acronym` ,
      `p`.`id`                     AS `permission_id`
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
       `cp`.`acronym`                                                           AS `crp_acronym`,
      `p`.`id`                     AS `permission_id`
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
ON    (( 
                     `cp`.`id` = `crp`.`crp_id`))) 
WHERE  (( 
                     `p`.`type` = 3) 
       AND    ( 
                     `r`.`acronym` = 'FPL'))