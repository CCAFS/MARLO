INSERT INTO `role_permissions` (`role_id`, `permission_id`) VALUES ('12', '423');
INSERT INTO `role_permissions` (`role_id`, `permission_id`) VALUES ('12', '424');
INSERT INTO `role_permissions` (`role_id`, `permission_id`) VALUES ('22', '423');
INSERT INTO `role_permissions` (`role_id`, `permission_id`) VALUES ('22', '424');
INSERT INTO `role_permissions` (`role_id`, `permission_id`) VALUES ('29', '423');
INSERT INTO `role_permissions` (`role_id`, `permission_id`) VALUES ('29', '424');
INSERT INTO `role_permissions` (`role_id`, `permission_id`) VALUES ('35', '423');
INSERT INTO `role_permissions` (`role_id`, `permission_id`) VALUES ('35', '424');

UPDATE `permissions` SET `permission`='crp:{0}:impactPathway:{1}:*', `type`='3' WHERE (`id`='423');
UPDATE `permissions` SET `permission`='crp:{0}:impactPathway:{1}:canAcess', `type`='3' WHERE (`id`='424');



DROP VIEW IF EXISTS `user_permissions`;

CREATE  VIEW `user_permissions` AS 

SELECT    `u`.`id`                                       AS `id`, 
          `r`.`acronym`                                  AS `ro_acronym`, 
          `r`.`id`                                       AS `role_id`, 
          replace(`p`.`permission`,'{0}',`cp`.`acronym`) AS `permission`, 
          `cp`.`acronym`                                 AS `crp_acronym` 
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
       `cp`.`acronym`                                                             AS `crp_acronym`
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
          `cp`.`acronym`                                 AS `crp_acronym` 
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

select u.id,r.acronym,r.id 'rolId',
 replace(replace(`p`.`permission`,'{0}',cp.acronym),'{1}',pro.id),cp.acronym as 'crp_acronym' 
  from 
users u inner join user_roles ro on ro.user_id=u.id
inner join roles r on r.id=ro.role_id
inner join role_permissions rp on rp.role_id=r.id
inner join permissions p on p.id=rp.permission_id
inner join crp_users crp on u.id=crp.user_id and crp.crp_id=r.crp_id
INNER JOIN crp_program_leaders cprog on cprog.user_id=u.id and cprog.is_active=1
inner JOIN crp_programs pro on pro.id=cprog.crp_program_id and pro.program_type=1 and pro.is_active=1
inner  join crps cp on cp.id=crp.crp_id
where p.type=3 and r.acronym ='FPL';

