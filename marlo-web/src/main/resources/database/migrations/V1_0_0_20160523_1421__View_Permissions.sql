DROP VIEWIF EXISTS `user_permissions`;CREATE algorithm=undefined definer=`root`@`localhost` sql security definer VIEW `user_permissions` ASSELECT `u`.`id`         AS `id`,
       `r`.`acronym`    AS `acronym`, 
       `p`.`permission` AS `permission` 
FROM   ((((`users` `u` 
JOIN   `user_roles` `ro` 
ON    (( 
                     `ro`.`user_id` = `u`.`id`))) 
JOIN   `roles` `r` 
ON    (( 
                     `r`.`id` = `ro`.`id`))) 
JOIN   `role_permissions` `rp` 
ON    (( 
                     `rp`.`role_id` = `r`.`id`))) 
JOIN   `permissions` `p` 
ON    (( 
                     `p`.`id` = `rp`.`permission_id`))) 
WHERE  ( 
              `p`.`type` = 0) 
UNION 
SELECT `u`.`id`                          AS `id`, 
       `r`.`acronym`                     AS `acronym`, 
       replace(`p`.`permission`,'{0}',1) AS `replace(p.permission,'{0}',1)` 
FROM   ((((`users` `u` 
JOIN   `user_roles` `ro` 
ON    (( 
                     `ro`.`user_id` = `u`.`id`))) 
JOIN   `roles` `r` 
ON    (( 
                     `r`.`id` = `ro`.`id`))) 
JOIN   `role_permissions` `rp` 
ON    (( 
                     `rp`.`role_id` = `r`.`id`))) 
JOIN   `permissions` `p` 
ON    (( 
                     `p`.`id` = `rp`.`permission_id`))) 
WHERE  ( 
              `p`.`type` = 1) ;