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
ON    (( 
                     `cp`.`id` = `crp`.`crp_id`))) 
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
                            `lus`.`institution_id` = `lin`.`institution_id`) 
              AND    ( 
                            `u`.`id` = `lus`.`user_id`)))) 
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
                            `u`.`id` = `lus`.`user_id`)))) 
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
UNION 
SELECT `u`.`id`                                                                 AS `id`, 
       `r`.`acronym`                                                            AS `acronym`,
       `r`.`id`                                                                 AS `rolid`, 
       replace(replace(`p`.`permission`,'{0}',`cp`.`acronym`),'{1}',`pro`.`id`) AS `name_exp_46`,
       `pro`.`id`                                                               AS `project_id`,
       `cp`.`acronym`                                                           AS `crp_acronym`,
       `p`.`id`                                                                 AS `permission_id`
FROM   (((((((`users` `u` 
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
JOIN   `projects_bilateral_cofinancing` `pro` 
ON    ((( 
                            `pro`.`crp_id` = `cp`.`id`) 
              AND    ( 
                            `pro`.`is_active` = 1)))) 
WHERE  (( 
                     `p`.`type` = 1) 
       AND    ( 
                     `p`.`id` = 438));
                     
                     /*
Navicat MySQL Data Transfer

Source Server         : local
Source Server Version : 50617
Source Host           : localhost:3306
Source Database       : ccafs_marlo

Target Server Type    : MYSQL
Target Server Version : 50617
File Encoding         : 65001

Date: 2016-10-25 12:06:01
*/

SET FOREIGN_KEY_CHECKS=0;

-- ----------------------------
-- Table structure for `role_permissions`
-- ----------------------------
DROP TABLE IF EXISTS `role_permissions`;
CREATE TABLE `role_permissions` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `role_id` bigint(20) NOT NULL,
  `permission_id` bigint(20) NOT NULL,
  PRIMARY KEY (`id`),
  KEY `roles_permission_roles_idx` (`role_id`) USING BTREE,
  KEY `roles_permission_user_permission_idx` (`permission_id`) USING BTREE,
  CONSTRAINT `role_permissions_ibfk_1` FOREIGN KEY (`permission_id`) REFERENCES `permissions` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `role_permissions_ibfk_2` FOREIGN KEY (`role_id`) REFERENCES `roles` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=2863 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of role_permissions
-- ----------------------------
INSERT INTO `role_permissions` VALUES ('1', '1', '1');
INSERT INTO `role_permissions` VALUES ('84', '2', '62');
INSERT INTO `role_permissions` VALUES ('140', '2', '67');
INSERT INTO `role_permissions` VALUES ('141', '4', '67');
INSERT INTO `role_permissions` VALUES ('142', '10', '67');
INSERT INTO `role_permissions` VALUES ('143', '2', '68');
INSERT INTO `role_permissions` VALUES ('144', '4', '68');
INSERT INTO `role_permissions` VALUES ('145', '10', '68');
INSERT INTO `role_permissions` VALUES ('146', '7', '68');
INSERT INTO `role_permissions` VALUES ('163', '11', '62');
INSERT INTO `role_permissions` VALUES ('164', '11', '67');
INSERT INTO `role_permissions` VALUES ('165', '11', '68');
INSERT INTO `role_permissions` VALUES ('181', '12', '62');
INSERT INTO `role_permissions` VALUES ('182', '12', '67');
INSERT INTO `role_permissions` VALUES ('183', '12', '68');
INSERT INTO `role_permissions` VALUES ('197', '2', '69');
INSERT INTO `role_permissions` VALUES ('199', '4', '69');
INSERT INTO `role_permissions` VALUES ('206', '7', '73');
INSERT INTO `role_permissions` VALUES ('207', '7', '70');
INSERT INTO `role_permissions` VALUES ('208', '7', '80');
INSERT INTO `role_permissions` VALUES ('209', '7', '83');
INSERT INTO `role_permissions` VALUES ('216', '9', '73');
INSERT INTO `role_permissions` VALUES ('217', '9', '70');
INSERT INTO `role_permissions` VALUES ('218', '9', '80');
INSERT INTO `role_permissions` VALUES ('219', '9', '83');
INSERT INTO `role_permissions` VALUES ('223', '2', '89');
INSERT INTO `role_permissions` VALUES ('224', '2', '90');
INSERT INTO `role_permissions` VALUES ('225', '2', '92');
INSERT INTO `role_permissions` VALUES ('229', '4', '89');
INSERT INTO `role_permissions` VALUES ('230', '4', '90');
INSERT INTO `role_permissions` VALUES ('231', '4', '92');
INSERT INTO `role_permissions` VALUES ('234', '7', '89');
INSERT INTO `role_permissions` VALUES ('235', '7', '92');
INSERT INTO `role_permissions` VALUES ('237', '9', '92');
INSERT INTO `role_permissions` VALUES ('240', '10', '91');
INSERT INTO `role_permissions` VALUES ('241', '10', '92');
INSERT INTO `role_permissions` VALUES ('246', '2', '93');
INSERT INTO `role_permissions` VALUES ('247', '4', '93');
INSERT INTO `role_permissions` VALUES ('248', '7', '93');
INSERT INTO `role_permissions` VALUES ('249', '9', '93');
INSERT INTO `role_permissions` VALUES ('269', '2', '100');
INSERT INTO `role_permissions` VALUES ('270', '4', '100');
INSERT INTO `role_permissions` VALUES ('271', '7', '100');
INSERT INTO `role_permissions` VALUES ('349', '2', '118');
INSERT INTO `role_permissions` VALUES ('350', '4', '118');
INSERT INTO `role_permissions` VALUES ('351', '7', '118');
INSERT INTO `role_permissions` VALUES ('352', '9', '118');
INSERT INTO `role_permissions` VALUES ('353', '2', '120');
INSERT INTO `role_permissions` VALUES ('354', '4', '120');
INSERT INTO `role_permissions` VALUES ('355', '7', '120');
INSERT INTO `role_permissions` VALUES ('356', '9', '120');
INSERT INTO `role_permissions` VALUES ('357', '2', '121');
INSERT INTO `role_permissions` VALUES ('358', '4', '121');
INSERT INTO `role_permissions` VALUES ('359', '7', '121');
INSERT INTO `role_permissions` VALUES ('360', '9', '121');
INSERT INTO `role_permissions` VALUES ('361', '2', '122');
INSERT INTO `role_permissions` VALUES ('362', '4', '122');
INSERT INTO `role_permissions` VALUES ('363', '7', '122');
INSERT INTO `role_permissions` VALUES ('364', '9', '122');
INSERT INTO `role_permissions` VALUES ('413', '2', '146');
INSERT INTO `role_permissions` VALUES ('414', '4', '146');
INSERT INTO `role_permissions` VALUES ('415', '7', '146');
INSERT INTO `role_permissions` VALUES ('416', '9', '146');
INSERT INTO `role_permissions` VALUES ('417', '2', '148');
INSERT INTO `role_permissions` VALUES ('418', '4', '148');
INSERT INTO `role_permissions` VALUES ('419', '7', '148');
INSERT INTO `role_permissions` VALUES ('420', '9', '148');
INSERT INTO `role_permissions` VALUES ('421', '2', '149');
INSERT INTO `role_permissions` VALUES ('422', '4', '149');
INSERT INTO `role_permissions` VALUES ('423', '7', '149');
INSERT INTO `role_permissions` VALUES ('424', '9', '149');
INSERT INTO `role_permissions` VALUES ('425', '2', '150');
INSERT INTO `role_permissions` VALUES ('426', '4', '150');
INSERT INTO `role_permissions` VALUES ('427', '7', '150');
INSERT INTO `role_permissions` VALUES ('428', '9', '150');
INSERT INTO `role_permissions` VALUES ('429', '2', '151');
INSERT INTO `role_permissions` VALUES ('430', '4', '151');
INSERT INTO `role_permissions` VALUES ('431', '7', '151');
INSERT INTO `role_permissions` VALUES ('432', '9', '151');
INSERT INTO `role_permissions` VALUES ('433', '2', '153');
INSERT INTO `role_permissions` VALUES ('434', '4', '153');
INSERT INTO `role_permissions` VALUES ('435', '7', '153');
INSERT INTO `role_permissions` VALUES ('436', '9', '153');
INSERT INTO `role_permissions` VALUES ('449', '2', '160');
INSERT INTO `role_permissions` VALUES ('450', '4', '160');
INSERT INTO `role_permissions` VALUES ('451', '7', '160');
INSERT INTO `role_permissions` VALUES ('452', '9', '160');
INSERT INTO `role_permissions` VALUES ('453', '2', '163');
INSERT INTO `role_permissions` VALUES ('454', '4', '163');
INSERT INTO `role_permissions` VALUES ('455', '7', '163');
INSERT INTO `role_permissions` VALUES ('456', '9', '163');
INSERT INTO `role_permissions` VALUES ('457', '2', '164');
INSERT INTO `role_permissions` VALUES ('458', '4', '164');
INSERT INTO `role_permissions` VALUES ('459', '7', '164');
INSERT INTO `role_permissions` VALUES ('460', '9', '164');
INSERT INTO `role_permissions` VALUES ('469', '2', '168');
INSERT INTO `role_permissions` VALUES ('470', '4', '168');
INSERT INTO `role_permissions` VALUES ('471', '7', '168');
INSERT INTO `role_permissions` VALUES ('472', '9', '168');
INSERT INTO `role_permissions` VALUES ('473', '2', '169');
INSERT INTO `role_permissions` VALUES ('474', '4', '169');
INSERT INTO `role_permissions` VALUES ('475', '7', '169');
INSERT INTO `role_permissions` VALUES ('476', '9', '169');
INSERT INTO `role_permissions` VALUES ('489', '2', '174');
INSERT INTO `role_permissions` VALUES ('490', '4', '174');
INSERT INTO `role_permissions` VALUES ('491', '7', '174');
INSERT INTO `role_permissions` VALUES ('492', '9', '174');
INSERT INTO `role_permissions` VALUES ('493', '2', '175');
INSERT INTO `role_permissions` VALUES ('494', '2', '176');
INSERT INTO `role_permissions` VALUES ('495', '4', '176');
INSERT INTO `role_permissions` VALUES ('496', '7', '176');
INSERT INTO `role_permissions` VALUES ('497', '9', '176');
INSERT INTO `role_permissions` VALUES ('502', '2', '182');
INSERT INTO `role_permissions` VALUES ('503', '4', '182');
INSERT INTO `role_permissions` VALUES ('504', '7', '182');
INSERT INTO `role_permissions` VALUES ('505', '9', '182');
INSERT INTO `role_permissions` VALUES ('506', '2', '183');
INSERT INTO `role_permissions` VALUES ('507', '4', '183');
INSERT INTO `role_permissions` VALUES ('508', '7', '183');
INSERT INTO `role_permissions` VALUES ('509', '9', '183');
INSERT INTO `role_permissions` VALUES ('510', '2', '184');
INSERT INTO `role_permissions` VALUES ('511', '4', '184');
INSERT INTO `role_permissions` VALUES ('512', '7', '184');
INSERT INTO `role_permissions` VALUES ('513', '9', '184');
INSERT INTO `role_permissions` VALUES ('546', '2', '110');
INSERT INTO `role_permissions` VALUES ('547', '4', '110');
INSERT INTO `role_permissions` VALUES ('548', '7', '195');
INSERT INTO `role_permissions` VALUES ('549', '9', '195');
INSERT INTO `role_permissions` VALUES ('550', '7', '199');
INSERT INTO `role_permissions` VALUES ('551', '9', '199');
INSERT INTO `role_permissions` VALUES ('552', '7', '200');
INSERT INTO `role_permissions` VALUES ('553', '9', '200');
INSERT INTO `role_permissions` VALUES ('554', '7', '201');
INSERT INTO `role_permissions` VALUES ('555', '9', '201');
INSERT INTO `role_permissions` VALUES ('556', '7', '202');
INSERT INTO `role_permissions` VALUES ('557', '9', '202');
INSERT INTO `role_permissions` VALUES ('558', '7', '203');
INSERT INTO `role_permissions` VALUES ('559', '9', '203');
INSERT INTO `role_permissions` VALUES ('569', '10', '52');
INSERT INTO `role_permissions` VALUES ('575', '4', '211');
INSERT INTO `role_permissions` VALUES ('576', '12', '211');
INSERT INTO `role_permissions` VALUES ('577', '11', '213');
INSERT INTO `role_permissions` VALUES ('578', '12', '213');
INSERT INTO `role_permissions` VALUES ('579', '11', '214');
INSERT INTO `role_permissions` VALUES ('580', '12', '215');
INSERT INTO `role_permissions` VALUES ('581', '11', '217');
INSERT INTO `role_permissions` VALUES ('582', '12', '217');
INSERT INTO `role_permissions` VALUES ('584', '11', '211');
INSERT INTO `role_permissions` VALUES ('591', '7', '220');
INSERT INTO `role_permissions` VALUES ('592', '11', '220');
INSERT INTO `role_permissions` VALUES ('593', '12', '220');
INSERT INTO `role_permissions` VALUES ('595', '13', '220');
INSERT INTO `role_permissions` VALUES ('596', '14', '220');
INSERT INTO `role_permissions` VALUES ('597', '11', '221');
INSERT INTO `role_permissions` VALUES ('598', '12', '221');
INSERT INTO `role_permissions` VALUES ('600', '14', '221');
INSERT INTO `role_permissions` VALUES ('601', '12', '222');
INSERT INTO `role_permissions` VALUES ('602', '14', '222');
INSERT INTO `role_permissions` VALUES ('603', '14', '223');
INSERT INTO `role_permissions` VALUES ('604', '11', '224');
INSERT INTO `role_permissions` VALUES ('605', '12', '224');
INSERT INTO `role_permissions` VALUES ('607', '14', '224');
INSERT INTO `role_permissions` VALUES ('608', '12', '225');
INSERT INTO `role_permissions` VALUES ('609', '14', '225');
INSERT INTO `role_permissions` VALUES ('610', '1', '421');
INSERT INTO `role_permissions` VALUES ('611', '17', '419');
INSERT INTO `role_permissions` VALUES ('612', '17', '420');
INSERT INTO `role_permissions` VALUES ('613', '1', '422');
INSERT INTO `role_permissions` VALUES ('614', '1', '423');
INSERT INTO `role_permissions` VALUES ('615', '1', '424');
INSERT INTO `role_permissions` VALUES ('616', '19', '1');
INSERT INTO `role_permissions` VALUES ('617', '19', '421');
INSERT INTO `role_permissions` VALUES ('618', '19', '422');
INSERT INTO `role_permissions` VALUES ('619', '19', '423');
INSERT INTO `role_permissions` VALUES ('620', '19', '424');
INSERT INTO `role_permissions` VALUES ('621', '26', '1');
INSERT INTO `role_permissions` VALUES ('622', '26', '421');
INSERT INTO `role_permissions` VALUES ('623', '26', '422');
INSERT INTO `role_permissions` VALUES ('624', '26', '423');
INSERT INTO `role_permissions` VALUES ('625', '26', '424');
INSERT INTO `role_permissions` VALUES ('627', '32', '1');
INSERT INTO `role_permissions` VALUES ('628', '32', '421');
INSERT INTO `role_permissions` VALUES ('629', '32', '422');
INSERT INTO `role_permissions` VALUES ('630', '32', '423');
INSERT INTO `role_permissions` VALUES ('631', '32', '424');
INSERT INTO `role_permissions` VALUES ('633', '12', '423');
INSERT INTO `role_permissions` VALUES ('634', '12', '424');
INSERT INTO `role_permissions` VALUES ('635', '22', '423');
INSERT INTO `role_permissions` VALUES ('636', '22', '424');
INSERT INTO `role_permissions` VALUES ('637', '29', '423');
INSERT INTO `role_permissions` VALUES ('638', '29', '424');
INSERT INTO `role_permissions` VALUES ('639', '35', '423');
INSERT INTO `role_permissions` VALUES ('640', '35', '424');
INSERT INTO `role_permissions` VALUES ('641', '14', '425');
INSERT INTO `role_permissions` VALUES ('642', '20', '425');
INSERT INTO `role_permissions` VALUES ('643', '27', '425');
INSERT INTO `role_permissions` VALUES ('644', '33', '425');
INSERT INTO `role_permissions` VALUES ('645', '12', '425');
INSERT INTO `role_permissions` VALUES ('646', '22', '425');
INSERT INTO `role_permissions` VALUES ('647', '29', '425');
INSERT INTO `role_permissions` VALUES ('648', '35', '425');
INSERT INTO `role_permissions` VALUES ('649', '37', '425');
INSERT INTO `role_permissions` VALUES ('650', '31', '425');
INSERT INTO `role_permissions` VALUES ('651', '24', '425');
INSERT INTO `role_permissions` VALUES ('652', '18', '425');
INSERT INTO `role_permissions` VALUES ('653', '11', '425');
INSERT INTO `role_permissions` VALUES ('654', '21', '425');
INSERT INTO `role_permissions` VALUES ('655', '28', '425');
INSERT INTO `role_permissions` VALUES ('656', '34', '425');
INSERT INTO `role_permissions` VALUES ('657', '43', '421');
INSERT INTO `role_permissions` VALUES ('658', '43', '422');
INSERT INTO `role_permissions` VALUES ('659', '43', '423');
INSERT INTO `role_permissions` VALUES ('660', '43', '424');
INSERT INTO `role_permissions` VALUES ('661', '43', '425');
INSERT INTO `role_permissions` VALUES ('662', '38', '426');
INSERT INTO `role_permissions` VALUES ('663', '14', '426');
INSERT INTO `role_permissions` VALUES ('664', '20', '426');
INSERT INTO `role_permissions` VALUES ('665', '27', '426');
INSERT INTO `role_permissions` VALUES ('667', '33', '426');
INSERT INTO `role_permissions` VALUES ('668', '1', '427');
INSERT INTO `role_permissions` VALUES ('669', '19', '427');
INSERT INTO `role_permissions` VALUES ('670', '26', '427');
INSERT INTO `role_permissions` VALUES ('671', '32', '427');
INSERT INTO `role_permissions` VALUES ('672', '43', '427');
INSERT INTO `role_permissions` VALUES ('678', '2', '429');
INSERT INTO `role_permissions` VALUES ('679', '48', '429');
INSERT INTO `role_permissions` VALUES ('680', '49', '429');
INSERT INTO `role_permissions` VALUES ('681', '50', '429');
INSERT INTO `role_permissions` VALUES ('682', '51', '429');
INSERT INTO `role_permissions` VALUES ('683', '12', '62');
INSERT INTO `role_permissions` VALUES ('684', '12', '67');
INSERT INTO `role_permissions` VALUES ('685', '12', '68');
INSERT INTO `role_permissions` VALUES ('686', '12', '69');
INSERT INTO `role_permissions` VALUES ('687', '12', '89');
INSERT INTO `role_permissions` VALUES ('688', '12', '90');
INSERT INTO `role_permissions` VALUES ('689', '12', '92');
INSERT INTO `role_permissions` VALUES ('690', '12', '93');
INSERT INTO `role_permissions` VALUES ('691', '12', '100');
INSERT INTO `role_permissions` VALUES ('692', '12', '118');
INSERT INTO `role_permissions` VALUES ('693', '12', '120');
INSERT INTO `role_permissions` VALUES ('694', '12', '121');
INSERT INTO `role_permissions` VALUES ('695', '12', '122');
INSERT INTO `role_permissions` VALUES ('700', '12', '146');
INSERT INTO `role_permissions` VALUES ('701', '12', '148');
INSERT INTO `role_permissions` VALUES ('702', '12', '149');
INSERT INTO `role_permissions` VALUES ('703', '12', '150');
INSERT INTO `role_permissions` VALUES ('704', '12', '151');
INSERT INTO `role_permissions` VALUES ('705', '12', '153');
INSERT INTO `role_permissions` VALUES ('706', '12', '160');
INSERT INTO `role_permissions` VALUES ('707', '12', '163');
INSERT INTO `role_permissions` VALUES ('708', '12', '164');
INSERT INTO `role_permissions` VALUES ('709', '12', '168');
INSERT INTO `role_permissions` VALUES ('710', '12', '169');
INSERT INTO `role_permissions` VALUES ('711', '12', '174');
INSERT INTO `role_permissions` VALUES ('712', '12', '175');
INSERT INTO `role_permissions` VALUES ('713', '12', '176');
INSERT INTO `role_permissions` VALUES ('714', '12', '182');
INSERT INTO `role_permissions` VALUES ('715', '12', '183');
INSERT INTO `role_permissions` VALUES ('716', '12', '184');
INSERT INTO `role_permissions` VALUES ('717', '12', '110');
INSERT INTO `role_permissions` VALUES ('718', '12', '429');
INSERT INTO `role_permissions` VALUES ('719', '11', '62');
INSERT INTO `role_permissions` VALUES ('720', '11', '67');
INSERT INTO `role_permissions` VALUES ('721', '11', '68');
INSERT INTO `role_permissions` VALUES ('722', '11', '69');
INSERT INTO `role_permissions` VALUES ('723', '11', '89');
INSERT INTO `role_permissions` VALUES ('724', '11', '90');
INSERT INTO `role_permissions` VALUES ('725', '11', '92');
INSERT INTO `role_permissions` VALUES ('726', '11', '93');
INSERT INTO `role_permissions` VALUES ('727', '11', '100');
INSERT INTO `role_permissions` VALUES ('728', '11', '118');
INSERT INTO `role_permissions` VALUES ('729', '11', '120');
INSERT INTO `role_permissions` VALUES ('730', '11', '121');
INSERT INTO `role_permissions` VALUES ('731', '11', '122');
INSERT INTO `role_permissions` VALUES ('736', '11', '146');
INSERT INTO `role_permissions` VALUES ('737', '11', '148');
INSERT INTO `role_permissions` VALUES ('738', '11', '149');
INSERT INTO `role_permissions` VALUES ('739', '11', '150');
INSERT INTO `role_permissions` VALUES ('740', '11', '151');
INSERT INTO `role_permissions` VALUES ('741', '11', '153');
INSERT INTO `role_permissions` VALUES ('742', '11', '160');
INSERT INTO `role_permissions` VALUES ('743', '11', '163');
INSERT INTO `role_permissions` VALUES ('744', '11', '164');
INSERT INTO `role_permissions` VALUES ('745', '11', '168');
INSERT INTO `role_permissions` VALUES ('746', '11', '169');
INSERT INTO `role_permissions` VALUES ('747', '11', '174');
INSERT INTO `role_permissions` VALUES ('748', '11', '175');
INSERT INTO `role_permissions` VALUES ('749', '11', '176');
INSERT INTO `role_permissions` VALUES ('750', '11', '182');
INSERT INTO `role_permissions` VALUES ('751', '11', '183');
INSERT INTO `role_permissions` VALUES ('752', '11', '184');
INSERT INTO `role_permissions` VALUES ('753', '11', '110');
INSERT INTO `role_permissions` VALUES ('754', '11', '429');
INSERT INTO `role_permissions` VALUES ('810', '48', '62');
INSERT INTO `role_permissions` VALUES ('811', '48', '67');
INSERT INTO `role_permissions` VALUES ('812', '48', '68');
INSERT INTO `role_permissions` VALUES ('813', '48', '69');
INSERT INTO `role_permissions` VALUES ('814', '48', '89');
INSERT INTO `role_permissions` VALUES ('815', '48', '90');
INSERT INTO `role_permissions` VALUES ('816', '48', '92');
INSERT INTO `role_permissions` VALUES ('817', '48', '93');
INSERT INTO `role_permissions` VALUES ('818', '48', '100');
INSERT INTO `role_permissions` VALUES ('819', '48', '118');
INSERT INTO `role_permissions` VALUES ('820', '48', '120');
INSERT INTO `role_permissions` VALUES ('821', '48', '121');
INSERT INTO `role_permissions` VALUES ('822', '48', '122');
INSERT INTO `role_permissions` VALUES ('827', '48', '146');
INSERT INTO `role_permissions` VALUES ('828', '48', '148');
INSERT INTO `role_permissions` VALUES ('829', '48', '149');
INSERT INTO `role_permissions` VALUES ('830', '48', '150');
INSERT INTO `role_permissions` VALUES ('831', '48', '151');
INSERT INTO `role_permissions` VALUES ('832', '48', '153');
INSERT INTO `role_permissions` VALUES ('833', '48', '160');
INSERT INTO `role_permissions` VALUES ('834', '48', '163');
INSERT INTO `role_permissions` VALUES ('835', '48', '164');
INSERT INTO `role_permissions` VALUES ('836', '48', '168');
INSERT INTO `role_permissions` VALUES ('837', '48', '169');
INSERT INTO `role_permissions` VALUES ('838', '48', '174');
INSERT INTO `role_permissions` VALUES ('839', '48', '175');
INSERT INTO `role_permissions` VALUES ('840', '48', '176');
INSERT INTO `role_permissions` VALUES ('841', '48', '182');
INSERT INTO `role_permissions` VALUES ('842', '48', '183');
INSERT INTO `role_permissions` VALUES ('843', '48', '184');
INSERT INTO `role_permissions` VALUES ('844', '48', '110');
INSERT INTO `role_permissions` VALUES ('845', '48', '429');
INSERT INTO `role_permissions` VALUES ('873', '49', '62');
INSERT INTO `role_permissions` VALUES ('874', '49', '67');
INSERT INTO `role_permissions` VALUES ('875', '49', '68');
INSERT INTO `role_permissions` VALUES ('876', '49', '69');
INSERT INTO `role_permissions` VALUES ('877', '49', '89');
INSERT INTO `role_permissions` VALUES ('878', '49', '90');
INSERT INTO `role_permissions` VALUES ('879', '49', '92');
INSERT INTO `role_permissions` VALUES ('880', '49', '93');
INSERT INTO `role_permissions` VALUES ('881', '49', '100');
INSERT INTO `role_permissions` VALUES ('882', '49', '118');
INSERT INTO `role_permissions` VALUES ('883', '49', '120');
INSERT INTO `role_permissions` VALUES ('884', '49', '121');
INSERT INTO `role_permissions` VALUES ('885', '49', '122');
INSERT INTO `role_permissions` VALUES ('890', '49', '146');
INSERT INTO `role_permissions` VALUES ('891', '49', '148');
INSERT INTO `role_permissions` VALUES ('892', '49', '149');
INSERT INTO `role_permissions` VALUES ('893', '49', '150');
INSERT INTO `role_permissions` VALUES ('894', '49', '151');
INSERT INTO `role_permissions` VALUES ('895', '49', '153');
INSERT INTO `role_permissions` VALUES ('896', '49', '160');
INSERT INTO `role_permissions` VALUES ('897', '49', '163');
INSERT INTO `role_permissions` VALUES ('898', '49', '164');
INSERT INTO `role_permissions` VALUES ('899', '49', '168');
INSERT INTO `role_permissions` VALUES ('900', '49', '169');
INSERT INTO `role_permissions` VALUES ('901', '49', '174');
INSERT INTO `role_permissions` VALUES ('902', '49', '175');
INSERT INTO `role_permissions` VALUES ('903', '49', '176');
INSERT INTO `role_permissions` VALUES ('904', '49', '182');
INSERT INTO `role_permissions` VALUES ('905', '49', '183');
INSERT INTO `role_permissions` VALUES ('906', '49', '184');
INSERT INTO `role_permissions` VALUES ('907', '49', '110');
INSERT INTO `role_permissions` VALUES ('908', '49', '429');
INSERT INTO `role_permissions` VALUES ('936', '50', '62');
INSERT INTO `role_permissions` VALUES ('937', '50', '67');
INSERT INTO `role_permissions` VALUES ('938', '50', '68');
INSERT INTO `role_permissions` VALUES ('939', '50', '69');
INSERT INTO `role_permissions` VALUES ('940', '50', '89');
INSERT INTO `role_permissions` VALUES ('941', '50', '90');
INSERT INTO `role_permissions` VALUES ('942', '50', '92');
INSERT INTO `role_permissions` VALUES ('943', '50', '93');
INSERT INTO `role_permissions` VALUES ('944', '50', '100');
INSERT INTO `role_permissions` VALUES ('945', '50', '118');
INSERT INTO `role_permissions` VALUES ('946', '50', '120');
INSERT INTO `role_permissions` VALUES ('947', '50', '121');
INSERT INTO `role_permissions` VALUES ('948', '50', '122');
INSERT INTO `role_permissions` VALUES ('953', '50', '146');
INSERT INTO `role_permissions` VALUES ('954', '50', '148');
INSERT INTO `role_permissions` VALUES ('955', '50', '149');
INSERT INTO `role_permissions` VALUES ('956', '50', '150');
INSERT INTO `role_permissions` VALUES ('957', '50', '151');
INSERT INTO `role_permissions` VALUES ('958', '50', '153');
INSERT INTO `role_permissions` VALUES ('959', '50', '160');
INSERT INTO `role_permissions` VALUES ('960', '50', '163');
INSERT INTO `role_permissions` VALUES ('961', '50', '164');
INSERT INTO `role_permissions` VALUES ('962', '50', '168');
INSERT INTO `role_permissions` VALUES ('963', '50', '169');
INSERT INTO `role_permissions` VALUES ('964', '50', '174');
INSERT INTO `role_permissions` VALUES ('965', '50', '175');
INSERT INTO `role_permissions` VALUES ('966', '50', '176');
INSERT INTO `role_permissions` VALUES ('967', '50', '182');
INSERT INTO `role_permissions` VALUES ('968', '50', '183');
INSERT INTO `role_permissions` VALUES ('969', '50', '184');
INSERT INTO `role_permissions` VALUES ('970', '50', '110');
INSERT INTO `role_permissions` VALUES ('971', '50', '429');
INSERT INTO `role_permissions` VALUES ('999', '51', '62');
INSERT INTO `role_permissions` VALUES ('1000', '51', '67');
INSERT INTO `role_permissions` VALUES ('1001', '51', '68');
INSERT INTO `role_permissions` VALUES ('1002', '51', '69');
INSERT INTO `role_permissions` VALUES ('1003', '51', '89');
INSERT INTO `role_permissions` VALUES ('1004', '51', '90');
INSERT INTO `role_permissions` VALUES ('1005', '51', '92');
INSERT INTO `role_permissions` VALUES ('1006', '51', '93');
INSERT INTO `role_permissions` VALUES ('1007', '51', '100');
INSERT INTO `role_permissions` VALUES ('1008', '51', '118');
INSERT INTO `role_permissions` VALUES ('1009', '51', '120');
INSERT INTO `role_permissions` VALUES ('1010', '51', '121');
INSERT INTO `role_permissions` VALUES ('1011', '51', '122');
INSERT INTO `role_permissions` VALUES ('1016', '51', '146');
INSERT INTO `role_permissions` VALUES ('1017', '51', '148');
INSERT INTO `role_permissions` VALUES ('1018', '51', '149');
INSERT INTO `role_permissions` VALUES ('1019', '51', '150');
INSERT INTO `role_permissions` VALUES ('1020', '51', '151');
INSERT INTO `role_permissions` VALUES ('1021', '51', '153');
INSERT INTO `role_permissions` VALUES ('1022', '51', '160');
INSERT INTO `role_permissions` VALUES ('1023', '51', '163');
INSERT INTO `role_permissions` VALUES ('1024', '51', '164');
INSERT INTO `role_permissions` VALUES ('1025', '51', '168');
INSERT INTO `role_permissions` VALUES ('1026', '51', '169');
INSERT INTO `role_permissions` VALUES ('1027', '51', '174');
INSERT INTO `role_permissions` VALUES ('1028', '51', '175');
INSERT INTO `role_permissions` VALUES ('1029', '51', '176');
INSERT INTO `role_permissions` VALUES ('1030', '51', '182');
INSERT INTO `role_permissions` VALUES ('1031', '51', '183');
INSERT INTO `role_permissions` VALUES ('1032', '51', '184');
INSERT INTO `role_permissions` VALUES ('1033', '51', '110');
INSERT INTO `role_permissions` VALUES ('1034', '51', '429');
INSERT INTO `role_permissions` VALUES ('1062', '21', '62');
INSERT INTO `role_permissions` VALUES ('1063', '21', '67');
INSERT INTO `role_permissions` VALUES ('1064', '21', '68');
INSERT INTO `role_permissions` VALUES ('1065', '21', '69');
INSERT INTO `role_permissions` VALUES ('1066', '21', '89');
INSERT INTO `role_permissions` VALUES ('1067', '21', '90');
INSERT INTO `role_permissions` VALUES ('1068', '21', '92');
INSERT INTO `role_permissions` VALUES ('1069', '21', '93');
INSERT INTO `role_permissions` VALUES ('1070', '21', '100');
INSERT INTO `role_permissions` VALUES ('1071', '21', '118');
INSERT INTO `role_permissions` VALUES ('1072', '21', '120');
INSERT INTO `role_permissions` VALUES ('1073', '21', '121');
INSERT INTO `role_permissions` VALUES ('1074', '21', '122');
INSERT INTO `role_permissions` VALUES ('1079', '21', '146');
INSERT INTO `role_permissions` VALUES ('1080', '21', '148');
INSERT INTO `role_permissions` VALUES ('1081', '21', '149');
INSERT INTO `role_permissions` VALUES ('1082', '21', '150');
INSERT INTO `role_permissions` VALUES ('1083', '21', '151');
INSERT INTO `role_permissions` VALUES ('1084', '21', '153');
INSERT INTO `role_permissions` VALUES ('1085', '21', '160');
INSERT INTO `role_permissions` VALUES ('1086', '21', '163');
INSERT INTO `role_permissions` VALUES ('1087', '21', '164');
INSERT INTO `role_permissions` VALUES ('1088', '21', '168');
INSERT INTO `role_permissions` VALUES ('1089', '21', '169');
INSERT INTO `role_permissions` VALUES ('1090', '21', '174');
INSERT INTO `role_permissions` VALUES ('1091', '21', '175');
INSERT INTO `role_permissions` VALUES ('1092', '21', '176');
INSERT INTO `role_permissions` VALUES ('1093', '21', '182');
INSERT INTO `role_permissions` VALUES ('1094', '21', '183');
INSERT INTO `role_permissions` VALUES ('1095', '21', '184');
INSERT INTO `role_permissions` VALUES ('1096', '21', '110');
INSERT INTO `role_permissions` VALUES ('1097', '21', '429');
INSERT INTO `role_permissions` VALUES ('1125', '22', '62');
INSERT INTO `role_permissions` VALUES ('1126', '22', '67');
INSERT INTO `role_permissions` VALUES ('1127', '22', '68');
INSERT INTO `role_permissions` VALUES ('1128', '22', '69');
INSERT INTO `role_permissions` VALUES ('1129', '22', '89');
INSERT INTO `role_permissions` VALUES ('1130', '22', '90');
INSERT INTO `role_permissions` VALUES ('1131', '22', '92');
INSERT INTO `role_permissions` VALUES ('1132', '22', '93');
INSERT INTO `role_permissions` VALUES ('1133', '22', '100');
INSERT INTO `role_permissions` VALUES ('1134', '22', '118');
INSERT INTO `role_permissions` VALUES ('1135', '22', '120');
INSERT INTO `role_permissions` VALUES ('1136', '22', '121');
INSERT INTO `role_permissions` VALUES ('1137', '22', '122');
INSERT INTO `role_permissions` VALUES ('1142', '22', '146');
INSERT INTO `role_permissions` VALUES ('1143', '22', '148');
INSERT INTO `role_permissions` VALUES ('1144', '22', '149');
INSERT INTO `role_permissions` VALUES ('1145', '22', '150');
INSERT INTO `role_permissions` VALUES ('1146', '22', '151');
INSERT INTO `role_permissions` VALUES ('1147', '22', '153');
INSERT INTO `role_permissions` VALUES ('1148', '22', '160');
INSERT INTO `role_permissions` VALUES ('1149', '22', '163');
INSERT INTO `role_permissions` VALUES ('1150', '22', '164');
INSERT INTO `role_permissions` VALUES ('1151', '22', '168');
INSERT INTO `role_permissions` VALUES ('1152', '22', '169');
INSERT INTO `role_permissions` VALUES ('1153', '22', '174');
INSERT INTO `role_permissions` VALUES ('1154', '22', '175');
INSERT INTO `role_permissions` VALUES ('1155', '22', '176');
INSERT INTO `role_permissions` VALUES ('1156', '22', '182');
INSERT INTO `role_permissions` VALUES ('1157', '22', '183');
INSERT INTO `role_permissions` VALUES ('1158', '22', '184');
INSERT INTO `role_permissions` VALUES ('1159', '22', '110');
INSERT INTO `role_permissions` VALUES ('1160', '22', '429');
INSERT INTO `role_permissions` VALUES ('1251', '28', '62');
INSERT INTO `role_permissions` VALUES ('1252', '28', '67');
INSERT INTO `role_permissions` VALUES ('1253', '28', '68');
INSERT INTO `role_permissions` VALUES ('1254', '28', '69');
INSERT INTO `role_permissions` VALUES ('1255', '28', '89');
INSERT INTO `role_permissions` VALUES ('1256', '28', '90');
INSERT INTO `role_permissions` VALUES ('1257', '28', '92');
INSERT INTO `role_permissions` VALUES ('1258', '28', '93');
INSERT INTO `role_permissions` VALUES ('1259', '28', '100');
INSERT INTO `role_permissions` VALUES ('1260', '28', '118');
INSERT INTO `role_permissions` VALUES ('1261', '28', '120');
INSERT INTO `role_permissions` VALUES ('1262', '28', '121');
INSERT INTO `role_permissions` VALUES ('1263', '28', '122');
INSERT INTO `role_permissions` VALUES ('1268', '28', '146');
INSERT INTO `role_permissions` VALUES ('1269', '28', '148');
INSERT INTO `role_permissions` VALUES ('1270', '28', '149');
INSERT INTO `role_permissions` VALUES ('1271', '28', '150');
INSERT INTO `role_permissions` VALUES ('1272', '28', '151');
INSERT INTO `role_permissions` VALUES ('1273', '28', '153');
INSERT INTO `role_permissions` VALUES ('1274', '28', '160');
INSERT INTO `role_permissions` VALUES ('1275', '28', '163');
INSERT INTO `role_permissions` VALUES ('1276', '28', '164');
INSERT INTO `role_permissions` VALUES ('1277', '28', '168');
INSERT INTO `role_permissions` VALUES ('1278', '28', '169');
INSERT INTO `role_permissions` VALUES ('1279', '28', '174');
INSERT INTO `role_permissions` VALUES ('1280', '28', '175');
INSERT INTO `role_permissions` VALUES ('1281', '28', '176');
INSERT INTO `role_permissions` VALUES ('1282', '28', '182');
INSERT INTO `role_permissions` VALUES ('1283', '28', '183');
INSERT INTO `role_permissions` VALUES ('1284', '28', '184');
INSERT INTO `role_permissions` VALUES ('1285', '28', '110');
INSERT INTO `role_permissions` VALUES ('1286', '28', '429');
INSERT INTO `role_permissions` VALUES ('1314', '29', '62');
INSERT INTO `role_permissions` VALUES ('1315', '29', '67');
INSERT INTO `role_permissions` VALUES ('1316', '29', '68');
INSERT INTO `role_permissions` VALUES ('1317', '29', '69');
INSERT INTO `role_permissions` VALUES ('1318', '29', '89');
INSERT INTO `role_permissions` VALUES ('1319', '29', '90');
INSERT INTO `role_permissions` VALUES ('1320', '29', '92');
INSERT INTO `role_permissions` VALUES ('1321', '29', '93');
INSERT INTO `role_permissions` VALUES ('1322', '29', '100');
INSERT INTO `role_permissions` VALUES ('1323', '29', '118');
INSERT INTO `role_permissions` VALUES ('1324', '29', '120');
INSERT INTO `role_permissions` VALUES ('1325', '29', '121');
INSERT INTO `role_permissions` VALUES ('1326', '29', '122');
INSERT INTO `role_permissions` VALUES ('1331', '29', '146');
INSERT INTO `role_permissions` VALUES ('1332', '29', '148');
INSERT INTO `role_permissions` VALUES ('1333', '29', '149');
INSERT INTO `role_permissions` VALUES ('1334', '29', '150');
INSERT INTO `role_permissions` VALUES ('1335', '29', '151');
INSERT INTO `role_permissions` VALUES ('1336', '29', '153');
INSERT INTO `role_permissions` VALUES ('1337', '29', '160');
INSERT INTO `role_permissions` VALUES ('1338', '29', '163');
INSERT INTO `role_permissions` VALUES ('1339', '29', '164');
INSERT INTO `role_permissions` VALUES ('1340', '29', '168');
INSERT INTO `role_permissions` VALUES ('1341', '29', '169');
INSERT INTO `role_permissions` VALUES ('1342', '29', '174');
INSERT INTO `role_permissions` VALUES ('1343', '29', '175');
INSERT INTO `role_permissions` VALUES ('1344', '29', '176');
INSERT INTO `role_permissions` VALUES ('1345', '29', '182');
INSERT INTO `role_permissions` VALUES ('1346', '29', '183');
INSERT INTO `role_permissions` VALUES ('1347', '29', '184');
INSERT INTO `role_permissions` VALUES ('1348', '29', '110');
INSERT INTO `role_permissions` VALUES ('1349', '29', '429');
INSERT INTO `role_permissions` VALUES ('1377', '34', '62');
INSERT INTO `role_permissions` VALUES ('1378', '34', '67');
INSERT INTO `role_permissions` VALUES ('1379', '34', '68');
INSERT INTO `role_permissions` VALUES ('1380', '34', '69');
INSERT INTO `role_permissions` VALUES ('1381', '34', '89');
INSERT INTO `role_permissions` VALUES ('1382', '34', '90');
INSERT INTO `role_permissions` VALUES ('1383', '34', '92');
INSERT INTO `role_permissions` VALUES ('1384', '34', '93');
INSERT INTO `role_permissions` VALUES ('1385', '34', '100');
INSERT INTO `role_permissions` VALUES ('1386', '34', '118');
INSERT INTO `role_permissions` VALUES ('1387', '34', '120');
INSERT INTO `role_permissions` VALUES ('1388', '34', '121');
INSERT INTO `role_permissions` VALUES ('1389', '34', '122');
INSERT INTO `role_permissions` VALUES ('1394', '34', '146');
INSERT INTO `role_permissions` VALUES ('1395', '34', '148');
INSERT INTO `role_permissions` VALUES ('1396', '34', '149');
INSERT INTO `role_permissions` VALUES ('1397', '34', '150');
INSERT INTO `role_permissions` VALUES ('1398', '34', '151');
INSERT INTO `role_permissions` VALUES ('1399', '34', '153');
INSERT INTO `role_permissions` VALUES ('1400', '34', '160');
INSERT INTO `role_permissions` VALUES ('1401', '34', '163');
INSERT INTO `role_permissions` VALUES ('1402', '34', '164');
INSERT INTO `role_permissions` VALUES ('1403', '34', '168');
INSERT INTO `role_permissions` VALUES ('1404', '34', '169');
INSERT INTO `role_permissions` VALUES ('1405', '34', '174');
INSERT INTO `role_permissions` VALUES ('1406', '34', '175');
INSERT INTO `role_permissions` VALUES ('1407', '34', '176');
INSERT INTO `role_permissions` VALUES ('1408', '34', '182');
INSERT INTO `role_permissions` VALUES ('1409', '34', '183');
INSERT INTO `role_permissions` VALUES ('1410', '34', '184');
INSERT INTO `role_permissions` VALUES ('1411', '34', '110');
INSERT INTO `role_permissions` VALUES ('1412', '34', '429');
INSERT INTO `role_permissions` VALUES ('1440', '35', '62');
INSERT INTO `role_permissions` VALUES ('1441', '35', '67');
INSERT INTO `role_permissions` VALUES ('1442', '35', '68');
INSERT INTO `role_permissions` VALUES ('1443', '35', '69');
INSERT INTO `role_permissions` VALUES ('1444', '35', '89');
INSERT INTO `role_permissions` VALUES ('1445', '35', '90');
INSERT INTO `role_permissions` VALUES ('1446', '35', '92');
INSERT INTO `role_permissions` VALUES ('1447', '35', '93');
INSERT INTO `role_permissions` VALUES ('1448', '35', '100');
INSERT INTO `role_permissions` VALUES ('1449', '35', '118');
INSERT INTO `role_permissions` VALUES ('1450', '35', '120');
INSERT INTO `role_permissions` VALUES ('1451', '35', '121');
INSERT INTO `role_permissions` VALUES ('1452', '35', '122');
INSERT INTO `role_permissions` VALUES ('1457', '35', '146');
INSERT INTO `role_permissions` VALUES ('1458', '35', '148');
INSERT INTO `role_permissions` VALUES ('1459', '35', '149');
INSERT INTO `role_permissions` VALUES ('1460', '35', '150');
INSERT INTO `role_permissions` VALUES ('1461', '35', '151');
INSERT INTO `role_permissions` VALUES ('1462', '35', '153');
INSERT INTO `role_permissions` VALUES ('1463', '35', '160');
INSERT INTO `role_permissions` VALUES ('1464', '35', '163');
INSERT INTO `role_permissions` VALUES ('1465', '35', '164');
INSERT INTO `role_permissions` VALUES ('1466', '35', '168');
INSERT INTO `role_permissions` VALUES ('1467', '35', '169');
INSERT INTO `role_permissions` VALUES ('1468', '35', '174');
INSERT INTO `role_permissions` VALUES ('1469', '35', '175');
INSERT INTO `role_permissions` VALUES ('1470', '35', '176');
INSERT INTO `role_permissions` VALUES ('1471', '35', '182');
INSERT INTO `role_permissions` VALUES ('1472', '35', '183');
INSERT INTO `role_permissions` VALUES ('1473', '35', '184');
INSERT INTO `role_permissions` VALUES ('1474', '35', '110');
INSERT INTO `role_permissions` VALUES ('1475', '35', '429');
INSERT INTO `role_permissions` VALUES ('1503', '39', '62');
INSERT INTO `role_permissions` VALUES ('1504', '39', '67');
INSERT INTO `role_permissions` VALUES ('1505', '39', '68');
INSERT INTO `role_permissions` VALUES ('1506', '39', '69');
INSERT INTO `role_permissions` VALUES ('1507', '39', '89');
INSERT INTO `role_permissions` VALUES ('1508', '39', '90');
INSERT INTO `role_permissions` VALUES ('1509', '39', '92');
INSERT INTO `role_permissions` VALUES ('1510', '39', '93');
INSERT INTO `role_permissions` VALUES ('1511', '39', '100');
INSERT INTO `role_permissions` VALUES ('1512', '39', '118');
INSERT INTO `role_permissions` VALUES ('1513', '39', '120');
INSERT INTO `role_permissions` VALUES ('1514', '39', '121');
INSERT INTO `role_permissions` VALUES ('1515', '39', '122');
INSERT INTO `role_permissions` VALUES ('1520', '39', '146');
INSERT INTO `role_permissions` VALUES ('1521', '39', '148');
INSERT INTO `role_permissions` VALUES ('1522', '39', '149');
INSERT INTO `role_permissions` VALUES ('1523', '39', '150');
INSERT INTO `role_permissions` VALUES ('1524', '39', '151');
INSERT INTO `role_permissions` VALUES ('1525', '39', '153');
INSERT INTO `role_permissions` VALUES ('1526', '39', '160');
INSERT INTO `role_permissions` VALUES ('1527', '39', '163');
INSERT INTO `role_permissions` VALUES ('1528', '39', '164');
INSERT INTO `role_permissions` VALUES ('1529', '39', '168');
INSERT INTO `role_permissions` VALUES ('1530', '39', '169');
INSERT INTO `role_permissions` VALUES ('1531', '39', '174');
INSERT INTO `role_permissions` VALUES ('1532', '39', '175');
INSERT INTO `role_permissions` VALUES ('1533', '39', '176');
INSERT INTO `role_permissions` VALUES ('1534', '39', '182');
INSERT INTO `role_permissions` VALUES ('1535', '39', '183');
INSERT INTO `role_permissions` VALUES ('1536', '39', '184');
INSERT INTO `role_permissions` VALUES ('1537', '39', '110');
INSERT INTO `role_permissions` VALUES ('1538', '39', '429');
INSERT INTO `role_permissions` VALUES ('1566', '40', '62');
INSERT INTO `role_permissions` VALUES ('1567', '40', '67');
INSERT INTO `role_permissions` VALUES ('1568', '40', '68');
INSERT INTO `role_permissions` VALUES ('1569', '40', '69');
INSERT INTO `role_permissions` VALUES ('1570', '40', '89');
INSERT INTO `role_permissions` VALUES ('1571', '40', '90');
INSERT INTO `role_permissions` VALUES ('1572', '40', '92');
INSERT INTO `role_permissions` VALUES ('1573', '40', '93');
INSERT INTO `role_permissions` VALUES ('1574', '40', '100');
INSERT INTO `role_permissions` VALUES ('1575', '40', '118');
INSERT INTO `role_permissions` VALUES ('1576', '40', '120');
INSERT INTO `role_permissions` VALUES ('1577', '40', '121');
INSERT INTO `role_permissions` VALUES ('1578', '40', '122');
INSERT INTO `role_permissions` VALUES ('1583', '40', '146');
INSERT INTO `role_permissions` VALUES ('1584', '40', '148');
INSERT INTO `role_permissions` VALUES ('1585', '40', '149');
INSERT INTO `role_permissions` VALUES ('1586', '40', '150');
INSERT INTO `role_permissions` VALUES ('1587', '40', '151');
INSERT INTO `role_permissions` VALUES ('1588', '40', '153');
INSERT INTO `role_permissions` VALUES ('1589', '40', '160');
INSERT INTO `role_permissions` VALUES ('1590', '40', '163');
INSERT INTO `role_permissions` VALUES ('1591', '40', '164');
INSERT INTO `role_permissions` VALUES ('1592', '40', '168');
INSERT INTO `role_permissions` VALUES ('1593', '40', '169');
INSERT INTO `role_permissions` VALUES ('1594', '40', '174');
INSERT INTO `role_permissions` VALUES ('1595', '40', '175');
INSERT INTO `role_permissions` VALUES ('1596', '40', '176');
INSERT INTO `role_permissions` VALUES ('1597', '40', '182');
INSERT INTO `role_permissions` VALUES ('1598', '40', '183');
INSERT INTO `role_permissions` VALUES ('1599', '40', '184');
INSERT INTO `role_permissions` VALUES ('1600', '40', '110');
INSERT INTO `role_permissions` VALUES ('1601', '40', '429');
INSERT INTO `role_permissions` VALUES ('1603', '2', '91');
INSERT INTO `role_permissions` VALUES ('1604', '2', '91');
INSERT INTO `role_permissions` VALUES ('1605', '12', '91');
INSERT INTO `role_permissions` VALUES ('1606', '11', '91');
INSERT INTO `role_permissions` VALUES ('1608', '2', '430');
INSERT INTO `role_permissions` VALUES ('1609', '11', '430');
INSERT INTO `role_permissions` VALUES ('1610', '12', '430');
INSERT INTO `role_permissions` VALUES ('1611', '7', '431');
INSERT INTO `role_permissions` VALUES ('1612', '9', '431');
INSERT INTO `role_permissions` VALUES ('1613', '7', '432');
INSERT INTO `role_permissions` VALUES ('1614', '9', '432');
INSERT INTO `role_permissions` VALUES ('1616', '2', '82');
INSERT INTO `role_permissions` VALUES ('1617', '2', '197');
INSERT INTO `role_permissions` VALUES ('1619', '2', '84');
INSERT INTO `role_permissions` VALUES ('1620', '2', '82');
INSERT INTO `role_permissions` VALUES ('1621', '2', '81');
INSERT INTO `role_permissions` VALUES ('1622', '2', '80');
INSERT INTO `role_permissions` VALUES ('1623', '2', '79');
INSERT INTO `role_permissions` VALUES ('1624', '2', '78');
INSERT INTO `role_permissions` VALUES ('1625', '2', '77');
INSERT INTO `role_permissions` VALUES ('1626', '2', '76');
INSERT INTO `role_permissions` VALUES ('1627', '2', '75');
INSERT INTO `role_permissions` VALUES ('1628', '2', '74');
INSERT INTO `role_permissions` VALUES ('1629', '2', '73');
INSERT INTO `role_permissions` VALUES ('1630', '11', '82');
INSERT INTO `role_permissions` VALUES ('1631', '11', '197');
INSERT INTO `role_permissions` VALUES ('1633', '11', '84');
INSERT INTO `role_permissions` VALUES ('1634', '11', '82');
INSERT INTO `role_permissions` VALUES ('1635', '11', '81');
INSERT INTO `role_permissions` VALUES ('1636', '11', '80');
INSERT INTO `role_permissions` VALUES ('1637', '11', '79');
INSERT INTO `role_permissions` VALUES ('1638', '11', '78');
INSERT INTO `role_permissions` VALUES ('1639', '11', '77');
INSERT INTO `role_permissions` VALUES ('1640', '11', '76');
INSERT INTO `role_permissions` VALUES ('1641', '11', '75');
INSERT INTO `role_permissions` VALUES ('1642', '11', '74');
INSERT INTO `role_permissions` VALUES ('1643', '11', '73');
INSERT INTO `role_permissions` VALUES ('1644', '12', '82');
INSERT INTO `role_permissions` VALUES ('1645', '12', '197');
INSERT INTO `role_permissions` VALUES ('1647', '12', '84');
INSERT INTO `role_permissions` VALUES ('1648', '12', '82');
INSERT INTO `role_permissions` VALUES ('1649', '12', '81');
INSERT INTO `role_permissions` VALUES ('1650', '12', '80');
INSERT INTO `role_permissions` VALUES ('1651', '12', '79');
INSERT INTO `role_permissions` VALUES ('1652', '12', '78');
INSERT INTO `role_permissions` VALUES ('1653', '12', '77');
INSERT INTO `role_permissions` VALUES ('1654', '12', '76');
INSERT INTO `role_permissions` VALUES ('1655', '12', '75');
INSERT INTO `role_permissions` VALUES ('1656', '12', '74');
INSERT INTO `role_permissions` VALUES ('1657', '12', '73');
INSERT INTO `role_permissions` VALUES ('1658', '2', '82');
INSERT INTO `role_permissions` VALUES ('1659', '2', '197');
INSERT INTO `role_permissions` VALUES ('1661', '2', '84');
INSERT INTO `role_permissions` VALUES ('1662', '2', '82');
INSERT INTO `role_permissions` VALUES ('1663', '2', '81');
INSERT INTO `role_permissions` VALUES ('1664', '2', '80');
INSERT INTO `role_permissions` VALUES ('1665', '2', '79');
INSERT INTO `role_permissions` VALUES ('1666', '2', '78');
INSERT INTO `role_permissions` VALUES ('1667', '2', '77');
INSERT INTO `role_permissions` VALUES ('1668', '2', '76');
INSERT INTO `role_permissions` VALUES ('1669', '2', '75');
INSERT INTO `role_permissions` VALUES ('1670', '2', '74');
INSERT INTO `role_permissions` VALUES ('1671', '2', '73');
INSERT INTO `role_permissions` VALUES ('1672', '11', '82');
INSERT INTO `role_permissions` VALUES ('1673', '11', '197');
INSERT INTO `role_permissions` VALUES ('1675', '11', '84');
INSERT INTO `role_permissions` VALUES ('1676', '11', '82');
INSERT INTO `role_permissions` VALUES ('1677', '11', '81');
INSERT INTO `role_permissions` VALUES ('1678', '11', '80');
INSERT INTO `role_permissions` VALUES ('1679', '11', '79');
INSERT INTO `role_permissions` VALUES ('1680', '11', '78');
INSERT INTO `role_permissions` VALUES ('1681', '11', '77');
INSERT INTO `role_permissions` VALUES ('1682', '11', '76');
INSERT INTO `role_permissions` VALUES ('1683', '11', '75');
INSERT INTO `role_permissions` VALUES ('1684', '11', '74');
INSERT INTO `role_permissions` VALUES ('1685', '11', '73');
INSERT INTO `role_permissions` VALUES ('1686', '12', '82');
INSERT INTO `role_permissions` VALUES ('1687', '12', '197');
INSERT INTO `role_permissions` VALUES ('1689', '12', '84');
INSERT INTO `role_permissions` VALUES ('1690', '12', '82');
INSERT INTO `role_permissions` VALUES ('1691', '12', '81');
INSERT INTO `role_permissions` VALUES ('1692', '12', '80');
INSERT INTO `role_permissions` VALUES ('1693', '12', '79');
INSERT INTO `role_permissions` VALUES ('1694', '12', '78');
INSERT INTO `role_permissions` VALUES ('1695', '12', '77');
INSERT INTO `role_permissions` VALUES ('1696', '12', '76');
INSERT INTO `role_permissions` VALUES ('1697', '12', '75');
INSERT INTO `role_permissions` VALUES ('1698', '12', '74');
INSERT INTO `role_permissions` VALUES ('1699', '12', '73');
INSERT INTO `role_permissions` VALUES ('1700', '2', '101');
INSERT INTO `role_permissions` VALUES ('1701', '7', '101');
INSERT INTO `role_permissions` VALUES ('1702', '9', '101');
INSERT INTO `role_permissions` VALUES ('1703', '11', '101');
INSERT INTO `role_permissions` VALUES ('1704', '12', '101');
INSERT INTO `role_permissions` VALUES ('1705', '2', '130');
INSERT INTO `role_permissions` VALUES ('1706', '7', '130');
INSERT INTO `role_permissions` VALUES ('1707', '9', '130');
INSERT INTO `role_permissions` VALUES ('1708', '11', '130');
INSERT INTO `role_permissions` VALUES ('1709', '12', '130');
INSERT INTO `role_permissions` VALUES ('1710', '2', '131');
INSERT INTO `role_permissions` VALUES ('1711', '7', '131');
INSERT INTO `role_permissions` VALUES ('1712', '9', '131');
INSERT INTO `role_permissions` VALUES ('1713', '11', '131');
INSERT INTO `role_permissions` VALUES ('1714', '12', '131');
INSERT INTO `role_permissions` VALUES ('1715', '2', '132');
INSERT INTO `role_permissions` VALUES ('1716', '7', '132');
INSERT INTO `role_permissions` VALUES ('1717', '9', '132');
INSERT INTO `role_permissions` VALUES ('1718', '11', '132');
INSERT INTO `role_permissions` VALUES ('1719', '12', '132');
INSERT INTO `role_permissions` VALUES ('1720', '2', '133');
INSERT INTO `role_permissions` VALUES ('1721', '7', '133');
INSERT INTO `role_permissions` VALUES ('1722', '9', '133');
INSERT INTO `role_permissions` VALUES ('1723', '11', '133');
INSERT INTO `role_permissions` VALUES ('1724', '12', '133');
INSERT INTO `role_permissions` VALUES ('1725', '2', '134');
INSERT INTO `role_permissions` VALUES ('1726', '7', '134');
INSERT INTO `role_permissions` VALUES ('1727', '9', '134');
INSERT INTO `role_permissions` VALUES ('1728', '11', '134');
INSERT INTO `role_permissions` VALUES ('1729', '12', '134');
INSERT INTO `role_permissions` VALUES ('1730', '2', '135');
INSERT INTO `role_permissions` VALUES ('1731', '7', '135');
INSERT INTO `role_permissions` VALUES ('1732', '9', '135');
INSERT INTO `role_permissions` VALUES ('1733', '11', '135');
INSERT INTO `role_permissions` VALUES ('1734', '12', '135');
INSERT INTO `role_permissions` VALUES ('1735', '2', '136');
INSERT INTO `role_permissions` VALUES ('1736', '7', '136');
INSERT INTO `role_permissions` VALUES ('1737', '9', '136');
INSERT INTO `role_permissions` VALUES ('1738', '11', '136');
INSERT INTO `role_permissions` VALUES ('1739', '12', '136');
INSERT INTO `role_permissions` VALUES ('1740', '2', '433');
INSERT INTO `role_permissions` VALUES ('1741', '7', '433');
INSERT INTO `role_permissions` VALUES ('1742', '9', '433');
INSERT INTO `role_permissions` VALUES ('1743', '11', '433');
INSERT INTO `role_permissions` VALUES ('1744', '12', '433');
INSERT INTO `role_permissions` VALUES ('1745', '2', '434');
INSERT INTO `role_permissions` VALUES ('1746', '7', '434');
INSERT INTO `role_permissions` VALUES ('1747', '9', '434');
INSERT INTO `role_permissions` VALUES ('1748', '11', '434');
INSERT INTO `role_permissions` VALUES ('1749', '12', '434');
INSERT INTO `role_permissions` VALUES ('1750', '2', '435');
INSERT INTO `role_permissions` VALUES ('1751', '7', '435');
INSERT INTO `role_permissions` VALUES ('1752', '9', '435');
INSERT INTO `role_permissions` VALUES ('1753', '11', '435');
INSERT INTO `role_permissions` VALUES ('1754', '12', '435');
INSERT INTO `role_permissions` VALUES ('1755', '2', '436');
INSERT INTO `role_permissions` VALUES ('1756', '7', '436');
INSERT INTO `role_permissions` VALUES ('1757', '9', '436');
INSERT INTO `role_permissions` VALUES ('1758', '11', '436');
INSERT INTO `role_permissions` VALUES ('1759', '12', '436');
INSERT INTO `role_permissions` VALUES ('1763', '2', '437');
INSERT INTO `role_permissions` VALUES ('1764', '7', '437');
INSERT INTO `role_permissions` VALUES ('1765', '9', '437');
INSERT INTO `role_permissions` VALUES ('1766', '11', '437');
INSERT INTO `role_permissions` VALUES ('1767', '12', '437');
INSERT INTO `role_permissions` VALUES ('1768', '2', '438');
INSERT INTO `role_permissions` VALUES ('1769', '4', '438');
INSERT INTO `role_permissions` VALUES ('1770', '48', '438');
INSERT INTO `role_permissions` VALUES ('1771', '20', '438');
INSERT INTO `role_permissions` VALUES ('1772', '49', '438');
INSERT INTO `role_permissions` VALUES ('1773', '27', '438');
INSERT INTO `role_permissions` VALUES ('1774', '50', '438');
INSERT INTO `role_permissions` VALUES ('1775', '33', '438');
INSERT INTO `role_permissions` VALUES ('1776', '51', '438');
INSERT INTO `role_permissions` VALUES ('1777', '38', '438');
INSERT INTO `role_permissions` VALUES ('1778', '2', '97');
INSERT INTO `role_permissions` VALUES ('1779', '2', '98');
INSERT INTO `role_permissions` VALUES ('1780', '10', '206');
INSERT INTO `role_permissions` VALUES ('1781', '7', '206');
INSERT INTO `role_permissions` VALUES ('1782', '12', '206');
INSERT INTO `role_permissions` VALUES ('1783', '4', '206');
INSERT INTO `role_permissions` VALUES ('1784', '2', '206');
INSERT INTO `role_permissions` VALUES ('1785', '10', '208');
INSERT INTO `role_permissions` VALUES ('1786', '9', '206');
INSERT INTO `role_permissions` VALUES ('1787', '7', '439');
INSERT INTO `role_permissions` VALUES ('1788', '9', '439');
INSERT INTO `role_permissions` VALUES ('1789', '7', '440');
INSERT INTO `role_permissions` VALUES ('1790', '9', '440');
INSERT INTO `role_permissions` VALUES ('1791', '4', '207');
INSERT INTO `role_permissions` VALUES ('1792', '4', '441');
INSERT INTO `role_permissions` VALUES ('1793', '12', '441');
INSERT INTO `role_permissions` VALUES ('1794', '2', '441');
INSERT INTO `role_permissions` VALUES ('1795', '7', '442');
INSERT INTO `role_permissions` VALUES ('1796', '7', '443');
INSERT INTO `role_permissions` VALUES ('1797', '12', '443');
INSERT INTO `role_permissions` VALUES ('1798', '4', '443');
INSERT INTO `role_permissions` VALUES ('1799', '2', '443');
INSERT INTO `role_permissions` VALUES ('1800', '9', '443');
INSERT INTO `role_permissions` VALUES ('1801', '48', '97');
INSERT INTO `role_permissions` VALUES ('1802', '49', '97');
INSERT INTO `role_permissions` VALUES ('1803', '50', '97');
INSERT INTO `role_permissions` VALUES ('1804', '51', '97');
INSERT INTO `role_permissions` VALUES ('1805', '48', '98');
INSERT INTO `role_permissions` VALUES ('1806', '49', '98');
INSERT INTO `role_permissions` VALUES ('1807', '50', '98');
INSERT INTO `role_permissions` VALUES ('1808', '51', '98');
INSERT INTO `role_permissions` VALUES ('1809', '12', '97');
INSERT INTO `role_permissions` VALUES ('1810', '22', '97');
INSERT INTO `role_permissions` VALUES ('1811', '29', '97');
INSERT INTO `role_permissions` VALUES ('1812', '35', '97');
INSERT INTO `role_permissions` VALUES ('1813', '40', '97');
INSERT INTO `role_permissions` VALUES ('1814', '12', '98');
INSERT INTO `role_permissions` VALUES ('1815', '22', '98');
INSERT INTO `role_permissions` VALUES ('1816', '29', '98');
INSERT INTO `role_permissions` VALUES ('1817', '35', '98');
INSERT INTO `role_permissions` VALUES ('1818', '40', '98');
INSERT INTO `role_permissions` VALUES ('1819', '52', '68');
INSERT INTO `role_permissions` VALUES ('1820', '52', '73');
INSERT INTO `role_permissions` VALUES ('1821', '52', '70');
INSERT INTO `role_permissions` VALUES ('1822', '52', '80');
INSERT INTO `role_permissions` VALUES ('1823', '52', '83');
INSERT INTO `role_permissions` VALUES ('1824', '52', '89');
INSERT INTO `role_permissions` VALUES ('1825', '52', '92');
INSERT INTO `role_permissions` VALUES ('1826', '52', '93');
INSERT INTO `role_permissions` VALUES ('1827', '52', '100');
INSERT INTO `role_permissions` VALUES ('1828', '52', '118');
INSERT INTO `role_permissions` VALUES ('1829', '52', '120');
INSERT INTO `role_permissions` VALUES ('1830', '52', '121');
INSERT INTO `role_permissions` VALUES ('1831', '52', '122');
INSERT INTO `role_permissions` VALUES ('1832', '52', '146');
INSERT INTO `role_permissions` VALUES ('1833', '52', '148');
INSERT INTO `role_permissions` VALUES ('1834', '52', '149');
INSERT INTO `role_permissions` VALUES ('1835', '52', '150');
INSERT INTO `role_permissions` VALUES ('1836', '52', '151');
INSERT INTO `role_permissions` VALUES ('1837', '52', '153');
INSERT INTO `role_permissions` VALUES ('1838', '52', '160');
INSERT INTO `role_permissions` VALUES ('1839', '52', '163');
INSERT INTO `role_permissions` VALUES ('1840', '52', '164');
INSERT INTO `role_permissions` VALUES ('1841', '52', '168');
INSERT INTO `role_permissions` VALUES ('1842', '52', '169');
INSERT INTO `role_permissions` VALUES ('1843', '52', '174');
INSERT INTO `role_permissions` VALUES ('1844', '52', '176');
INSERT INTO `role_permissions` VALUES ('1845', '52', '182');
INSERT INTO `role_permissions` VALUES ('1846', '52', '183');
INSERT INTO `role_permissions` VALUES ('1847', '52', '184');
INSERT INTO `role_permissions` VALUES ('1848', '52', '195');
INSERT INTO `role_permissions` VALUES ('1849', '52', '199');
INSERT INTO `role_permissions` VALUES ('1850', '52', '200');
INSERT INTO `role_permissions` VALUES ('1851', '52', '201');
INSERT INTO `role_permissions` VALUES ('1852', '52', '202');
INSERT INTO `role_permissions` VALUES ('1853', '52', '203');
INSERT INTO `role_permissions` VALUES ('1854', '52', '220');
INSERT INTO `role_permissions` VALUES ('1855', '52', '431');
INSERT INTO `role_permissions` VALUES ('1856', '52', '432');
INSERT INTO `role_permissions` VALUES ('1857', '52', '101');
INSERT INTO `role_permissions` VALUES ('1858', '52', '130');
INSERT INTO `role_permissions` VALUES ('1859', '52', '131');
INSERT INTO `role_permissions` VALUES ('1860', '52', '132');
INSERT INTO `role_permissions` VALUES ('1861', '52', '133');
INSERT INTO `role_permissions` VALUES ('1862', '52', '134');
INSERT INTO `role_permissions` VALUES ('1863', '52', '135');
INSERT INTO `role_permissions` VALUES ('1864', '52', '136');
INSERT INTO `role_permissions` VALUES ('1865', '52', '433');
INSERT INTO `role_permissions` VALUES ('1866', '52', '434');
INSERT INTO `role_permissions` VALUES ('1867', '52', '435');
INSERT INTO `role_permissions` VALUES ('1868', '52', '436');
INSERT INTO `role_permissions` VALUES ('1869', '52', '437');
INSERT INTO `role_permissions` VALUES ('1870', '52', '206');
INSERT INTO `role_permissions` VALUES ('1871', '52', '439');
INSERT INTO `role_permissions` VALUES ('1872', '52', '440');
INSERT INTO `role_permissions` VALUES ('1873', '52', '442');
INSERT INTO `role_permissions` VALUES ('1874', '52', '443');
INSERT INTO `role_permissions` VALUES ('1875', '57', '68');
INSERT INTO `role_permissions` VALUES ('1876', '57', '73');
INSERT INTO `role_permissions` VALUES ('1877', '57', '70');
INSERT INTO `role_permissions` VALUES ('1878', '57', '80');
INSERT INTO `role_permissions` VALUES ('1879', '57', '83');
INSERT INTO `role_permissions` VALUES ('1880', '57', '89');
INSERT INTO `role_permissions` VALUES ('1881', '57', '92');
INSERT INTO `role_permissions` VALUES ('1882', '57', '93');
INSERT INTO `role_permissions` VALUES ('1883', '57', '100');
INSERT INTO `role_permissions` VALUES ('1884', '57', '118');
INSERT INTO `role_permissions` VALUES ('1885', '57', '120');
INSERT INTO `role_permissions` VALUES ('1886', '57', '121');
INSERT INTO `role_permissions` VALUES ('1887', '57', '122');
INSERT INTO `role_permissions` VALUES ('1888', '57', '146');
INSERT INTO `role_permissions` VALUES ('1889', '57', '148');
INSERT INTO `role_permissions` VALUES ('1890', '57', '149');
INSERT INTO `role_permissions` VALUES ('1891', '57', '150');
INSERT INTO `role_permissions` VALUES ('1892', '57', '151');
INSERT INTO `role_permissions` VALUES ('1893', '57', '153');
INSERT INTO `role_permissions` VALUES ('1894', '57', '160');
INSERT INTO `role_permissions` VALUES ('1895', '57', '163');
INSERT INTO `role_permissions` VALUES ('1896', '57', '164');
INSERT INTO `role_permissions` VALUES ('1897', '57', '168');
INSERT INTO `role_permissions` VALUES ('1898', '57', '169');
INSERT INTO `role_permissions` VALUES ('1899', '57', '174');
INSERT INTO `role_permissions` VALUES ('1900', '57', '176');
INSERT INTO `role_permissions` VALUES ('1901', '57', '182');
INSERT INTO `role_permissions` VALUES ('1902', '57', '183');
INSERT INTO `role_permissions` VALUES ('1903', '57', '184');
INSERT INTO `role_permissions` VALUES ('1904', '57', '195');
INSERT INTO `role_permissions` VALUES ('1905', '57', '199');
INSERT INTO `role_permissions` VALUES ('1906', '57', '200');
INSERT INTO `role_permissions` VALUES ('1907', '57', '201');
INSERT INTO `role_permissions` VALUES ('1908', '57', '202');
INSERT INTO `role_permissions` VALUES ('1909', '57', '203');
INSERT INTO `role_permissions` VALUES ('1910', '57', '220');
INSERT INTO `role_permissions` VALUES ('1911', '57', '431');
INSERT INTO `role_permissions` VALUES ('1912', '57', '432');
INSERT INTO `role_permissions` VALUES ('1913', '57', '101');
INSERT INTO `role_permissions` VALUES ('1914', '57', '130');
INSERT INTO `role_permissions` VALUES ('1915', '57', '131');
INSERT INTO `role_permissions` VALUES ('1916', '57', '132');
INSERT INTO `role_permissions` VALUES ('1917', '57', '133');
INSERT INTO `role_permissions` VALUES ('1918', '57', '134');
INSERT INTO `role_permissions` VALUES ('1919', '57', '135');
INSERT INTO `role_permissions` VALUES ('1920', '57', '136');
INSERT INTO `role_permissions` VALUES ('1921', '57', '433');
INSERT INTO `role_permissions` VALUES ('1922', '57', '434');
INSERT INTO `role_permissions` VALUES ('1923', '57', '435');
INSERT INTO `role_permissions` VALUES ('1924', '57', '436');
INSERT INTO `role_permissions` VALUES ('1925', '57', '437');
INSERT INTO `role_permissions` VALUES ('1926', '57', '206');
INSERT INTO `role_permissions` VALUES ('1927', '57', '439');
INSERT INTO `role_permissions` VALUES ('1928', '57', '440');
INSERT INTO `role_permissions` VALUES ('1929', '57', '442');
INSERT INTO `role_permissions` VALUES ('1930', '57', '443');
INSERT INTO `role_permissions` VALUES ('1931', '58', '68');
INSERT INTO `role_permissions` VALUES ('1932', '58', '73');
INSERT INTO `role_permissions` VALUES ('1933', '58', '70');
INSERT INTO `role_permissions` VALUES ('1934', '58', '80');
INSERT INTO `role_permissions` VALUES ('1935', '58', '83');
INSERT INTO `role_permissions` VALUES ('1936', '58', '89');
INSERT INTO `role_permissions` VALUES ('1937', '58', '92');
INSERT INTO `role_permissions` VALUES ('1938', '58', '93');
INSERT INTO `role_permissions` VALUES ('1939', '58', '100');
INSERT INTO `role_permissions` VALUES ('1940', '58', '118');
INSERT INTO `role_permissions` VALUES ('1941', '58', '120');
INSERT INTO `role_permissions` VALUES ('1942', '58', '121');
INSERT INTO `role_permissions` VALUES ('1943', '58', '122');
INSERT INTO `role_permissions` VALUES ('1944', '58', '146');
INSERT INTO `role_permissions` VALUES ('1945', '58', '148');
INSERT INTO `role_permissions` VALUES ('1946', '58', '149');
INSERT INTO `role_permissions` VALUES ('1947', '58', '150');
INSERT INTO `role_permissions` VALUES ('1948', '58', '151');
INSERT INTO `role_permissions` VALUES ('1949', '58', '153');
INSERT INTO `role_permissions` VALUES ('1950', '58', '160');
INSERT INTO `role_permissions` VALUES ('1951', '58', '163');
INSERT INTO `role_permissions` VALUES ('1952', '58', '164');
INSERT INTO `role_permissions` VALUES ('1953', '58', '168');
INSERT INTO `role_permissions` VALUES ('1954', '58', '169');
INSERT INTO `role_permissions` VALUES ('1955', '58', '174');
INSERT INTO `role_permissions` VALUES ('1956', '58', '176');
INSERT INTO `role_permissions` VALUES ('1957', '58', '182');
INSERT INTO `role_permissions` VALUES ('1958', '58', '183');
INSERT INTO `role_permissions` VALUES ('1959', '58', '184');
INSERT INTO `role_permissions` VALUES ('1960', '58', '195');
INSERT INTO `role_permissions` VALUES ('1961', '58', '199');
INSERT INTO `role_permissions` VALUES ('1962', '58', '200');
INSERT INTO `role_permissions` VALUES ('1963', '58', '201');
INSERT INTO `role_permissions` VALUES ('1964', '58', '202');
INSERT INTO `role_permissions` VALUES ('1965', '58', '203');
INSERT INTO `role_permissions` VALUES ('1966', '58', '220');
INSERT INTO `role_permissions` VALUES ('1967', '58', '431');
INSERT INTO `role_permissions` VALUES ('1968', '58', '432');
INSERT INTO `role_permissions` VALUES ('1969', '58', '101');
INSERT INTO `role_permissions` VALUES ('1970', '58', '130');
INSERT INTO `role_permissions` VALUES ('1971', '58', '131');
INSERT INTO `role_permissions` VALUES ('1972', '58', '132');
INSERT INTO `role_permissions` VALUES ('1973', '58', '133');
INSERT INTO `role_permissions` VALUES ('1974', '58', '134');
INSERT INTO `role_permissions` VALUES ('1975', '58', '135');
INSERT INTO `role_permissions` VALUES ('1976', '58', '136');
INSERT INTO `role_permissions` VALUES ('1977', '58', '433');
INSERT INTO `role_permissions` VALUES ('1978', '58', '434');
INSERT INTO `role_permissions` VALUES ('1979', '58', '435');
INSERT INTO `role_permissions` VALUES ('1980', '58', '436');
INSERT INTO `role_permissions` VALUES ('1981', '58', '437');
INSERT INTO `role_permissions` VALUES ('1982', '58', '206');
INSERT INTO `role_permissions` VALUES ('1983', '58', '439');
INSERT INTO `role_permissions` VALUES ('1984', '58', '440');
INSERT INTO `role_permissions` VALUES ('1985', '58', '442');
INSERT INTO `role_permissions` VALUES ('1986', '58', '443');
INSERT INTO `role_permissions` VALUES ('1987', '59', '68');
INSERT INTO `role_permissions` VALUES ('1988', '59', '73');
INSERT INTO `role_permissions` VALUES ('1989', '59', '70');
INSERT INTO `role_permissions` VALUES ('1990', '59', '80');
INSERT INTO `role_permissions` VALUES ('1991', '59', '83');
INSERT INTO `role_permissions` VALUES ('1992', '59', '89');
INSERT INTO `role_permissions` VALUES ('1993', '59', '92');
INSERT INTO `role_permissions` VALUES ('1994', '59', '93');
INSERT INTO `role_permissions` VALUES ('1995', '59', '100');
INSERT INTO `role_permissions` VALUES ('1996', '59', '118');
INSERT INTO `role_permissions` VALUES ('1997', '59', '120');
INSERT INTO `role_permissions` VALUES ('1998', '59', '121');
INSERT INTO `role_permissions` VALUES ('1999', '59', '122');
INSERT INTO `role_permissions` VALUES ('2000', '59', '146');
INSERT INTO `role_permissions` VALUES ('2001', '59', '148');
INSERT INTO `role_permissions` VALUES ('2002', '59', '149');
INSERT INTO `role_permissions` VALUES ('2003', '59', '150');
INSERT INTO `role_permissions` VALUES ('2004', '59', '151');
INSERT INTO `role_permissions` VALUES ('2005', '59', '153');
INSERT INTO `role_permissions` VALUES ('2006', '59', '160');
INSERT INTO `role_permissions` VALUES ('2007', '59', '163');
INSERT INTO `role_permissions` VALUES ('2008', '59', '164');
INSERT INTO `role_permissions` VALUES ('2009', '59', '168');
INSERT INTO `role_permissions` VALUES ('2010', '59', '169');
INSERT INTO `role_permissions` VALUES ('2011', '59', '174');
INSERT INTO `role_permissions` VALUES ('2012', '59', '176');
INSERT INTO `role_permissions` VALUES ('2013', '59', '182');
INSERT INTO `role_permissions` VALUES ('2014', '59', '183');
INSERT INTO `role_permissions` VALUES ('2015', '59', '184');
INSERT INTO `role_permissions` VALUES ('2016', '59', '195');
INSERT INTO `role_permissions` VALUES ('2017', '59', '199');
INSERT INTO `role_permissions` VALUES ('2018', '59', '200');
INSERT INTO `role_permissions` VALUES ('2019', '59', '201');
INSERT INTO `role_permissions` VALUES ('2020', '59', '202');
INSERT INTO `role_permissions` VALUES ('2021', '59', '203');
INSERT INTO `role_permissions` VALUES ('2022', '59', '220');
INSERT INTO `role_permissions` VALUES ('2023', '59', '431');
INSERT INTO `role_permissions` VALUES ('2024', '59', '432');
INSERT INTO `role_permissions` VALUES ('2025', '59', '101');
INSERT INTO `role_permissions` VALUES ('2026', '59', '130');
INSERT INTO `role_permissions` VALUES ('2027', '59', '131');
INSERT INTO `role_permissions` VALUES ('2028', '59', '132');
INSERT INTO `role_permissions` VALUES ('2029', '59', '133');
INSERT INTO `role_permissions` VALUES ('2030', '59', '134');
INSERT INTO `role_permissions` VALUES ('2031', '59', '135');
INSERT INTO `role_permissions` VALUES ('2032', '59', '136');
INSERT INTO `role_permissions` VALUES ('2033', '59', '433');
INSERT INTO `role_permissions` VALUES ('2034', '59', '434');
INSERT INTO `role_permissions` VALUES ('2035', '59', '435');
INSERT INTO `role_permissions` VALUES ('2036', '59', '436');
INSERT INTO `role_permissions` VALUES ('2037', '59', '437');
INSERT INTO `role_permissions` VALUES ('2038', '59', '206');
INSERT INTO `role_permissions` VALUES ('2039', '59', '439');
INSERT INTO `role_permissions` VALUES ('2040', '59', '440');
INSERT INTO `role_permissions` VALUES ('2041', '59', '442');
INSERT INTO `role_permissions` VALUES ('2042', '59', '443');
INSERT INTO `role_permissions` VALUES ('2043', '53', '73');
INSERT INTO `role_permissions` VALUES ('2044', '53', '70');
INSERT INTO `role_permissions` VALUES ('2045', '53', '80');
INSERT INTO `role_permissions` VALUES ('2046', '53', '83');
INSERT INTO `role_permissions` VALUES ('2047', '53', '92');
INSERT INTO `role_permissions` VALUES ('2048', '53', '93');
INSERT INTO `role_permissions` VALUES ('2049', '53', '118');
INSERT INTO `role_permissions` VALUES ('2050', '53', '120');
INSERT INTO `role_permissions` VALUES ('2051', '53', '121');
INSERT INTO `role_permissions` VALUES ('2052', '53', '122');
INSERT INTO `role_permissions` VALUES ('2053', '53', '146');
INSERT INTO `role_permissions` VALUES ('2054', '53', '148');
INSERT INTO `role_permissions` VALUES ('2055', '53', '149');
INSERT INTO `role_permissions` VALUES ('2056', '53', '150');
INSERT INTO `role_permissions` VALUES ('2057', '53', '151');
INSERT INTO `role_permissions` VALUES ('2058', '53', '153');
INSERT INTO `role_permissions` VALUES ('2059', '53', '160');
INSERT INTO `role_permissions` VALUES ('2060', '53', '163');
INSERT INTO `role_permissions` VALUES ('2061', '53', '164');
INSERT INTO `role_permissions` VALUES ('2062', '53', '168');
INSERT INTO `role_permissions` VALUES ('2063', '53', '169');
INSERT INTO `role_permissions` VALUES ('2064', '53', '174');
INSERT INTO `role_permissions` VALUES ('2065', '53', '176');
INSERT INTO `role_permissions` VALUES ('2066', '53', '182');
INSERT INTO `role_permissions` VALUES ('2067', '53', '183');
INSERT INTO `role_permissions` VALUES ('2068', '53', '184');
INSERT INTO `role_permissions` VALUES ('2069', '53', '195');
INSERT INTO `role_permissions` VALUES ('2070', '53', '199');
INSERT INTO `role_permissions` VALUES ('2071', '53', '200');
INSERT INTO `role_permissions` VALUES ('2072', '53', '201');
INSERT INTO `role_permissions` VALUES ('2073', '53', '202');
INSERT INTO `role_permissions` VALUES ('2074', '53', '203');
INSERT INTO `role_permissions` VALUES ('2075', '53', '431');
INSERT INTO `role_permissions` VALUES ('2076', '53', '432');
INSERT INTO `role_permissions` VALUES ('2077', '53', '101');
INSERT INTO `role_permissions` VALUES ('2078', '53', '130');
INSERT INTO `role_permissions` VALUES ('2079', '53', '131');
INSERT INTO `role_permissions` VALUES ('2080', '53', '132');
INSERT INTO `role_permissions` VALUES ('2081', '53', '133');
INSERT INTO `role_permissions` VALUES ('2082', '53', '134');
INSERT INTO `role_permissions` VALUES ('2083', '53', '135');
INSERT INTO `role_permissions` VALUES ('2084', '53', '136');
INSERT INTO `role_permissions` VALUES ('2085', '53', '433');
INSERT INTO `role_permissions` VALUES ('2086', '53', '434');
INSERT INTO `role_permissions` VALUES ('2087', '53', '435');
INSERT INTO `role_permissions` VALUES ('2088', '53', '436');
INSERT INTO `role_permissions` VALUES ('2089', '53', '437');
INSERT INTO `role_permissions` VALUES ('2090', '53', '206');
INSERT INTO `role_permissions` VALUES ('2091', '53', '439');
INSERT INTO `role_permissions` VALUES ('2092', '53', '440');
INSERT INTO `role_permissions` VALUES ('2093', '53', '443');
INSERT INTO `role_permissions` VALUES ('2094', '54', '73');
INSERT INTO `role_permissions` VALUES ('2095', '54', '70');
INSERT INTO `role_permissions` VALUES ('2096', '54', '80');
INSERT INTO `role_permissions` VALUES ('2097', '54', '83');
INSERT INTO `role_permissions` VALUES ('2098', '54', '92');
INSERT INTO `role_permissions` VALUES ('2099', '54', '93');
INSERT INTO `role_permissions` VALUES ('2100', '54', '118');
INSERT INTO `role_permissions` VALUES ('2101', '54', '120');
INSERT INTO `role_permissions` VALUES ('2102', '54', '121');
INSERT INTO `role_permissions` VALUES ('2103', '54', '122');
INSERT INTO `role_permissions` VALUES ('2104', '54', '146');
INSERT INTO `role_permissions` VALUES ('2105', '54', '148');
INSERT INTO `role_permissions` VALUES ('2106', '54', '149');
INSERT INTO `role_permissions` VALUES ('2107', '54', '150');
INSERT INTO `role_permissions` VALUES ('2108', '54', '151');
INSERT INTO `role_permissions` VALUES ('2109', '54', '153');
INSERT INTO `role_permissions` VALUES ('2110', '54', '160');
INSERT INTO `role_permissions` VALUES ('2111', '54', '163');
INSERT INTO `role_permissions` VALUES ('2112', '54', '164');
INSERT INTO `role_permissions` VALUES ('2113', '54', '168');
INSERT INTO `role_permissions` VALUES ('2114', '54', '169');
INSERT INTO `role_permissions` VALUES ('2115', '54', '174');
INSERT INTO `role_permissions` VALUES ('2116', '54', '176');
INSERT INTO `role_permissions` VALUES ('2117', '54', '182');
INSERT INTO `role_permissions` VALUES ('2118', '54', '183');
INSERT INTO `role_permissions` VALUES ('2119', '54', '184');
INSERT INTO `role_permissions` VALUES ('2120', '54', '195');
INSERT INTO `role_permissions` VALUES ('2121', '54', '199');
INSERT INTO `role_permissions` VALUES ('2122', '54', '200');
INSERT INTO `role_permissions` VALUES ('2123', '54', '201');
INSERT INTO `role_permissions` VALUES ('2124', '54', '202');
INSERT INTO `role_permissions` VALUES ('2125', '54', '203');
INSERT INTO `role_permissions` VALUES ('2126', '54', '431');
INSERT INTO `role_permissions` VALUES ('2127', '54', '432');
INSERT INTO `role_permissions` VALUES ('2128', '54', '101');
INSERT INTO `role_permissions` VALUES ('2129', '54', '130');
INSERT INTO `role_permissions` VALUES ('2130', '54', '131');
INSERT INTO `role_permissions` VALUES ('2131', '54', '132');
INSERT INTO `role_permissions` VALUES ('2132', '54', '133');
INSERT INTO `role_permissions` VALUES ('2133', '54', '134');
INSERT INTO `role_permissions` VALUES ('2134', '54', '135');
INSERT INTO `role_permissions` VALUES ('2135', '54', '136');
INSERT INTO `role_permissions` VALUES ('2136', '54', '433');
INSERT INTO `role_permissions` VALUES ('2137', '54', '434');
INSERT INTO `role_permissions` VALUES ('2138', '54', '435');
INSERT INTO `role_permissions` VALUES ('2139', '54', '436');
INSERT INTO `role_permissions` VALUES ('2140', '54', '437');
INSERT INTO `role_permissions` VALUES ('2141', '54', '206');
INSERT INTO `role_permissions` VALUES ('2142', '54', '439');
INSERT INTO `role_permissions` VALUES ('2143', '54', '440');
INSERT INTO `role_permissions` VALUES ('2144', '54', '443');
INSERT INTO `role_permissions` VALUES ('2145', '55', '73');
INSERT INTO `role_permissions` VALUES ('2146', '55', '70');
INSERT INTO `role_permissions` VALUES ('2147', '55', '80');
INSERT INTO `role_permissions` VALUES ('2148', '55', '83');
INSERT INTO `role_permissions` VALUES ('2149', '55', '92');
INSERT INTO `role_permissions` VALUES ('2150', '55', '93');
INSERT INTO `role_permissions` VALUES ('2151', '55', '118');
INSERT INTO `role_permissions` VALUES ('2152', '55', '120');
INSERT INTO `role_permissions` VALUES ('2153', '55', '121');
INSERT INTO `role_permissions` VALUES ('2154', '55', '122');
INSERT INTO `role_permissions` VALUES ('2155', '55', '146');
INSERT INTO `role_permissions` VALUES ('2156', '55', '148');
INSERT INTO `role_permissions` VALUES ('2157', '55', '149');
INSERT INTO `role_permissions` VALUES ('2158', '55', '150');
INSERT INTO `role_permissions` VALUES ('2159', '55', '151');
INSERT INTO `role_permissions` VALUES ('2160', '55', '153');
INSERT INTO `role_permissions` VALUES ('2161', '55', '160');
INSERT INTO `role_permissions` VALUES ('2162', '55', '163');
INSERT INTO `role_permissions` VALUES ('2163', '55', '164');
INSERT INTO `role_permissions` VALUES ('2164', '55', '168');
INSERT INTO `role_permissions` VALUES ('2165', '55', '169');
INSERT INTO `role_permissions` VALUES ('2166', '55', '174');
INSERT INTO `role_permissions` VALUES ('2167', '55', '176');
INSERT INTO `role_permissions` VALUES ('2168', '55', '182');
INSERT INTO `role_permissions` VALUES ('2169', '55', '183');
INSERT INTO `role_permissions` VALUES ('2170', '55', '184');
INSERT INTO `role_permissions` VALUES ('2171', '55', '195');
INSERT INTO `role_permissions` VALUES ('2172', '55', '199');
INSERT INTO `role_permissions` VALUES ('2173', '55', '200');
INSERT INTO `role_permissions` VALUES ('2174', '55', '201');
INSERT INTO `role_permissions` VALUES ('2175', '55', '202');
INSERT INTO `role_permissions` VALUES ('2176', '55', '203');
INSERT INTO `role_permissions` VALUES ('2177', '55', '431');
INSERT INTO `role_permissions` VALUES ('2178', '55', '432');
INSERT INTO `role_permissions` VALUES ('2179', '55', '101');
INSERT INTO `role_permissions` VALUES ('2180', '55', '130');
INSERT INTO `role_permissions` VALUES ('2181', '55', '131');
INSERT INTO `role_permissions` VALUES ('2182', '55', '132');
INSERT INTO `role_permissions` VALUES ('2183', '55', '133');
INSERT INTO `role_permissions` VALUES ('2184', '55', '134');
INSERT INTO `role_permissions` VALUES ('2185', '55', '135');
INSERT INTO `role_permissions` VALUES ('2186', '55', '136');
INSERT INTO `role_permissions` VALUES ('2187', '55', '433');
INSERT INTO `role_permissions` VALUES ('2188', '55', '434');
INSERT INTO `role_permissions` VALUES ('2189', '55', '435');
INSERT INTO `role_permissions` VALUES ('2190', '55', '436');
INSERT INTO `role_permissions` VALUES ('2191', '55', '437');
INSERT INTO `role_permissions` VALUES ('2192', '55', '206');
INSERT INTO `role_permissions` VALUES ('2193', '55', '439');
INSERT INTO `role_permissions` VALUES ('2194', '55', '440');
INSERT INTO `role_permissions` VALUES ('2195', '55', '443');
INSERT INTO `role_permissions` VALUES ('2196', '56', '73');
INSERT INTO `role_permissions` VALUES ('2197', '56', '70');
INSERT INTO `role_permissions` VALUES ('2198', '56', '80');
INSERT INTO `role_permissions` VALUES ('2199', '56', '83');
INSERT INTO `role_permissions` VALUES ('2200', '56', '92');
INSERT INTO `role_permissions` VALUES ('2201', '56', '93');
INSERT INTO `role_permissions` VALUES ('2202', '56', '118');
INSERT INTO `role_permissions` VALUES ('2203', '56', '120');
INSERT INTO `role_permissions` VALUES ('2204', '56', '121');
INSERT INTO `role_permissions` VALUES ('2205', '56', '122');
INSERT INTO `role_permissions` VALUES ('2206', '56', '146');
INSERT INTO `role_permissions` VALUES ('2207', '56', '148');
INSERT INTO `role_permissions` VALUES ('2208', '56', '149');
INSERT INTO `role_permissions` VALUES ('2209', '56', '150');
INSERT INTO `role_permissions` VALUES ('2210', '56', '151');
INSERT INTO `role_permissions` VALUES ('2211', '56', '153');
INSERT INTO `role_permissions` VALUES ('2212', '56', '160');
INSERT INTO `role_permissions` VALUES ('2213', '56', '163');
INSERT INTO `role_permissions` VALUES ('2214', '56', '164');
INSERT INTO `role_permissions` VALUES ('2215', '56', '168');
INSERT INTO `role_permissions` VALUES ('2216', '56', '169');
INSERT INTO `role_permissions` VALUES ('2217', '56', '174');
INSERT INTO `role_permissions` VALUES ('2218', '56', '176');
INSERT INTO `role_permissions` VALUES ('2219', '56', '182');
INSERT INTO `role_permissions` VALUES ('2220', '56', '183');
INSERT INTO `role_permissions` VALUES ('2221', '56', '184');
INSERT INTO `role_permissions` VALUES ('2222', '56', '195');
INSERT INTO `role_permissions` VALUES ('2223', '56', '199');
INSERT INTO `role_permissions` VALUES ('2224', '56', '200');
INSERT INTO `role_permissions` VALUES ('2225', '56', '201');
INSERT INTO `role_permissions` VALUES ('2226', '56', '202');
INSERT INTO `role_permissions` VALUES ('2227', '56', '203');
INSERT INTO `role_permissions` VALUES ('2228', '56', '431');
INSERT INTO `role_permissions` VALUES ('2229', '56', '432');
INSERT INTO `role_permissions` VALUES ('2230', '56', '101');
INSERT INTO `role_permissions` VALUES ('2231', '56', '130');
INSERT INTO `role_permissions` VALUES ('2232', '56', '131');
INSERT INTO `role_permissions` VALUES ('2233', '56', '132');
INSERT INTO `role_permissions` VALUES ('2234', '56', '133');
INSERT INTO `role_permissions` VALUES ('2235', '56', '134');
INSERT INTO `role_permissions` VALUES ('2236', '56', '135');
INSERT INTO `role_permissions` VALUES ('2237', '56', '136');
INSERT INTO `role_permissions` VALUES ('2238', '56', '433');
INSERT INTO `role_permissions` VALUES ('2239', '56', '434');
INSERT INTO `role_permissions` VALUES ('2240', '56', '435');
INSERT INTO `role_permissions` VALUES ('2241', '56', '436');
INSERT INTO `role_permissions` VALUES ('2242', '56', '437');
INSERT INTO `role_permissions` VALUES ('2243', '56', '206');
INSERT INTO `role_permissions` VALUES ('2244', '56', '439');
INSERT INTO `role_permissions` VALUES ('2245', '56', '440');
INSERT INTO `role_permissions` VALUES ('2246', '56', '443');
INSERT INTO `role_permissions` VALUES ('2330', '52', '68');
INSERT INTO `role_permissions` VALUES ('2331', '52', '73');
INSERT INTO `role_permissions` VALUES ('2332', '52', '70');
INSERT INTO `role_permissions` VALUES ('2333', '52', '80');
INSERT INTO `role_permissions` VALUES ('2334', '52', '83');
INSERT INTO `role_permissions` VALUES ('2335', '52', '89');
INSERT INTO `role_permissions` VALUES ('2336', '52', '92');
INSERT INTO `role_permissions` VALUES ('2337', '52', '93');
INSERT INTO `role_permissions` VALUES ('2338', '52', '100');
INSERT INTO `role_permissions` VALUES ('2339', '52', '118');
INSERT INTO `role_permissions` VALUES ('2340', '52', '120');
INSERT INTO `role_permissions` VALUES ('2341', '52', '121');
INSERT INTO `role_permissions` VALUES ('2342', '52', '122');
INSERT INTO `role_permissions` VALUES ('2343', '52', '146');
INSERT INTO `role_permissions` VALUES ('2344', '52', '148');
INSERT INTO `role_permissions` VALUES ('2345', '52', '149');
INSERT INTO `role_permissions` VALUES ('2346', '52', '150');
INSERT INTO `role_permissions` VALUES ('2347', '52', '151');
INSERT INTO `role_permissions` VALUES ('2348', '52', '153');
INSERT INTO `role_permissions` VALUES ('2349', '52', '160');
INSERT INTO `role_permissions` VALUES ('2350', '52', '163');
INSERT INTO `role_permissions` VALUES ('2351', '52', '164');
INSERT INTO `role_permissions` VALUES ('2352', '52', '168');
INSERT INTO `role_permissions` VALUES ('2353', '52', '169');
INSERT INTO `role_permissions` VALUES ('2354', '52', '174');
INSERT INTO `role_permissions` VALUES ('2355', '52', '176');
INSERT INTO `role_permissions` VALUES ('2356', '52', '182');
INSERT INTO `role_permissions` VALUES ('2357', '52', '183');
INSERT INTO `role_permissions` VALUES ('2358', '52', '184');
INSERT INTO `role_permissions` VALUES ('2359', '52', '195');
INSERT INTO `role_permissions` VALUES ('2360', '52', '199');
INSERT INTO `role_permissions` VALUES ('2361', '52', '200');
INSERT INTO `role_permissions` VALUES ('2362', '52', '201');
INSERT INTO `role_permissions` VALUES ('2363', '52', '202');
INSERT INTO `role_permissions` VALUES ('2364', '52', '203');
INSERT INTO `role_permissions` VALUES ('2365', '52', '220');
INSERT INTO `role_permissions` VALUES ('2366', '52', '431');
INSERT INTO `role_permissions` VALUES ('2367', '52', '432');
INSERT INTO `role_permissions` VALUES ('2368', '52', '101');
INSERT INTO `role_permissions` VALUES ('2369', '52', '130');
INSERT INTO `role_permissions` VALUES ('2370', '52', '131');
INSERT INTO `role_permissions` VALUES ('2371', '52', '132');
INSERT INTO `role_permissions` VALUES ('2372', '52', '133');
INSERT INTO `role_permissions` VALUES ('2373', '52', '134');
INSERT INTO `role_permissions` VALUES ('2374', '52', '135');
INSERT INTO `role_permissions` VALUES ('2375', '52', '136');
INSERT INTO `role_permissions` VALUES ('2376', '52', '433');
INSERT INTO `role_permissions` VALUES ('2377', '52', '434');
INSERT INTO `role_permissions` VALUES ('2378', '52', '435');
INSERT INTO `role_permissions` VALUES ('2379', '52', '436');
INSERT INTO `role_permissions` VALUES ('2380', '52', '437');
INSERT INTO `role_permissions` VALUES ('2381', '52', '206');
INSERT INTO `role_permissions` VALUES ('2382', '52', '439');
INSERT INTO `role_permissions` VALUES ('2383', '52', '440');
INSERT INTO `role_permissions` VALUES ('2384', '52', '442');
INSERT INTO `role_permissions` VALUES ('2385', '52', '443');
INSERT INTO `role_permissions` VALUES ('2386', '57', '68');
INSERT INTO `role_permissions` VALUES ('2387', '57', '73');
INSERT INTO `role_permissions` VALUES ('2388', '57', '70');
INSERT INTO `role_permissions` VALUES ('2389', '57', '80');
INSERT INTO `role_permissions` VALUES ('2390', '57', '83');
INSERT INTO `role_permissions` VALUES ('2391', '57', '89');
INSERT INTO `role_permissions` VALUES ('2392', '57', '92');
INSERT INTO `role_permissions` VALUES ('2393', '57', '93');
INSERT INTO `role_permissions` VALUES ('2394', '57', '100');
INSERT INTO `role_permissions` VALUES ('2395', '57', '118');
INSERT INTO `role_permissions` VALUES ('2396', '57', '120');
INSERT INTO `role_permissions` VALUES ('2397', '57', '121');
INSERT INTO `role_permissions` VALUES ('2398', '57', '122');
INSERT INTO `role_permissions` VALUES ('2399', '57', '146');
INSERT INTO `role_permissions` VALUES ('2400', '57', '148');
INSERT INTO `role_permissions` VALUES ('2401', '57', '149');
INSERT INTO `role_permissions` VALUES ('2402', '57', '150');
INSERT INTO `role_permissions` VALUES ('2403', '57', '151');
INSERT INTO `role_permissions` VALUES ('2404', '57', '153');
INSERT INTO `role_permissions` VALUES ('2405', '57', '160');
INSERT INTO `role_permissions` VALUES ('2406', '57', '163');
INSERT INTO `role_permissions` VALUES ('2407', '57', '164');
INSERT INTO `role_permissions` VALUES ('2408', '57', '168');
INSERT INTO `role_permissions` VALUES ('2409', '57', '169');
INSERT INTO `role_permissions` VALUES ('2410', '57', '174');
INSERT INTO `role_permissions` VALUES ('2411', '57', '176');
INSERT INTO `role_permissions` VALUES ('2412', '57', '182');
INSERT INTO `role_permissions` VALUES ('2413', '57', '183');
INSERT INTO `role_permissions` VALUES ('2414', '57', '184');
INSERT INTO `role_permissions` VALUES ('2415', '57', '195');
INSERT INTO `role_permissions` VALUES ('2416', '57', '199');
INSERT INTO `role_permissions` VALUES ('2417', '57', '200');
INSERT INTO `role_permissions` VALUES ('2418', '57', '201');
INSERT INTO `role_permissions` VALUES ('2419', '57', '202');
INSERT INTO `role_permissions` VALUES ('2420', '57', '203');
INSERT INTO `role_permissions` VALUES ('2421', '57', '220');
INSERT INTO `role_permissions` VALUES ('2422', '57', '431');
INSERT INTO `role_permissions` VALUES ('2423', '57', '432');
INSERT INTO `role_permissions` VALUES ('2424', '57', '101');
INSERT INTO `role_permissions` VALUES ('2425', '57', '130');
INSERT INTO `role_permissions` VALUES ('2426', '57', '131');
INSERT INTO `role_permissions` VALUES ('2427', '57', '132');
INSERT INTO `role_permissions` VALUES ('2428', '57', '133');
INSERT INTO `role_permissions` VALUES ('2429', '57', '134');
INSERT INTO `role_permissions` VALUES ('2430', '57', '135');
INSERT INTO `role_permissions` VALUES ('2431', '57', '136');
INSERT INTO `role_permissions` VALUES ('2432', '57', '433');
INSERT INTO `role_permissions` VALUES ('2433', '57', '434');
INSERT INTO `role_permissions` VALUES ('2434', '57', '435');
INSERT INTO `role_permissions` VALUES ('2435', '57', '436');
INSERT INTO `role_permissions` VALUES ('2436', '57', '437');
INSERT INTO `role_permissions` VALUES ('2437', '57', '206');
INSERT INTO `role_permissions` VALUES ('2438', '57', '439');
INSERT INTO `role_permissions` VALUES ('2439', '57', '440');
INSERT INTO `role_permissions` VALUES ('2440', '57', '442');
INSERT INTO `role_permissions` VALUES ('2441', '57', '443');
INSERT INTO `role_permissions` VALUES ('2442', '58', '68');
INSERT INTO `role_permissions` VALUES ('2443', '58', '73');
INSERT INTO `role_permissions` VALUES ('2444', '58', '70');
INSERT INTO `role_permissions` VALUES ('2445', '58', '80');
INSERT INTO `role_permissions` VALUES ('2446', '58', '83');
INSERT INTO `role_permissions` VALUES ('2447', '58', '89');
INSERT INTO `role_permissions` VALUES ('2448', '58', '92');
INSERT INTO `role_permissions` VALUES ('2449', '58', '93');
INSERT INTO `role_permissions` VALUES ('2450', '58', '100');
INSERT INTO `role_permissions` VALUES ('2451', '58', '118');
INSERT INTO `role_permissions` VALUES ('2452', '58', '120');
INSERT INTO `role_permissions` VALUES ('2453', '58', '121');
INSERT INTO `role_permissions` VALUES ('2454', '58', '122');
INSERT INTO `role_permissions` VALUES ('2455', '58', '146');
INSERT INTO `role_permissions` VALUES ('2456', '58', '148');
INSERT INTO `role_permissions` VALUES ('2457', '58', '149');
INSERT INTO `role_permissions` VALUES ('2458', '58', '150');
INSERT INTO `role_permissions` VALUES ('2459', '58', '151');
INSERT INTO `role_permissions` VALUES ('2460', '58', '153');
INSERT INTO `role_permissions` VALUES ('2461', '58', '160');
INSERT INTO `role_permissions` VALUES ('2462', '58', '163');
INSERT INTO `role_permissions` VALUES ('2463', '58', '164');
INSERT INTO `role_permissions` VALUES ('2464', '58', '168');
INSERT INTO `role_permissions` VALUES ('2465', '58', '169');
INSERT INTO `role_permissions` VALUES ('2466', '58', '174');
INSERT INTO `role_permissions` VALUES ('2467', '58', '176');
INSERT INTO `role_permissions` VALUES ('2468', '58', '182');
INSERT INTO `role_permissions` VALUES ('2469', '58', '183');
INSERT INTO `role_permissions` VALUES ('2470', '58', '184');
INSERT INTO `role_permissions` VALUES ('2471', '58', '195');
INSERT INTO `role_permissions` VALUES ('2472', '58', '199');
INSERT INTO `role_permissions` VALUES ('2473', '58', '200');
INSERT INTO `role_permissions` VALUES ('2474', '58', '201');
INSERT INTO `role_permissions` VALUES ('2475', '58', '202');
INSERT INTO `role_permissions` VALUES ('2476', '58', '203');
INSERT INTO `role_permissions` VALUES ('2477', '58', '220');
INSERT INTO `role_permissions` VALUES ('2478', '58', '431');
INSERT INTO `role_permissions` VALUES ('2479', '58', '432');
INSERT INTO `role_permissions` VALUES ('2480', '58', '101');
INSERT INTO `role_permissions` VALUES ('2481', '58', '130');
INSERT INTO `role_permissions` VALUES ('2482', '58', '131');
INSERT INTO `role_permissions` VALUES ('2483', '58', '132');
INSERT INTO `role_permissions` VALUES ('2484', '58', '133');
INSERT INTO `role_permissions` VALUES ('2485', '58', '134');
INSERT INTO `role_permissions` VALUES ('2486', '58', '135');
INSERT INTO `role_permissions` VALUES ('2487', '58', '136');
INSERT INTO `role_permissions` VALUES ('2488', '58', '433');
INSERT INTO `role_permissions` VALUES ('2489', '58', '434');
INSERT INTO `role_permissions` VALUES ('2490', '58', '435');
INSERT INTO `role_permissions` VALUES ('2491', '58', '436');
INSERT INTO `role_permissions` VALUES ('2492', '58', '437');
INSERT INTO `role_permissions` VALUES ('2493', '58', '206');
INSERT INTO `role_permissions` VALUES ('2494', '58', '439');
INSERT INTO `role_permissions` VALUES ('2495', '58', '440');
INSERT INTO `role_permissions` VALUES ('2496', '58', '442');
INSERT INTO `role_permissions` VALUES ('2497', '58', '443');
INSERT INTO `role_permissions` VALUES ('2498', '59', '68');
INSERT INTO `role_permissions` VALUES ('2499', '59', '73');
INSERT INTO `role_permissions` VALUES ('2500', '59', '70');
INSERT INTO `role_permissions` VALUES ('2501', '59', '80');
INSERT INTO `role_permissions` VALUES ('2502', '59', '83');
INSERT INTO `role_permissions` VALUES ('2503', '59', '89');
INSERT INTO `role_permissions` VALUES ('2504', '59', '92');
INSERT INTO `role_permissions` VALUES ('2505', '59', '93');
INSERT INTO `role_permissions` VALUES ('2506', '59', '100');
INSERT INTO `role_permissions` VALUES ('2507', '59', '118');
INSERT INTO `role_permissions` VALUES ('2508', '59', '120');
INSERT INTO `role_permissions` VALUES ('2509', '59', '121');
INSERT INTO `role_permissions` VALUES ('2510', '59', '122');
INSERT INTO `role_permissions` VALUES ('2511', '59', '146');
INSERT INTO `role_permissions` VALUES ('2512', '59', '148');
INSERT INTO `role_permissions` VALUES ('2513', '59', '149');
INSERT INTO `role_permissions` VALUES ('2514', '59', '150');
INSERT INTO `role_permissions` VALUES ('2515', '59', '151');
INSERT INTO `role_permissions` VALUES ('2516', '59', '153');
INSERT INTO `role_permissions` VALUES ('2517', '59', '160');
INSERT INTO `role_permissions` VALUES ('2518', '59', '163');
INSERT INTO `role_permissions` VALUES ('2519', '59', '164');
INSERT INTO `role_permissions` VALUES ('2520', '59', '168');
INSERT INTO `role_permissions` VALUES ('2521', '59', '169');
INSERT INTO `role_permissions` VALUES ('2522', '59', '174');
INSERT INTO `role_permissions` VALUES ('2523', '59', '176');
INSERT INTO `role_permissions` VALUES ('2524', '59', '182');
INSERT INTO `role_permissions` VALUES ('2525', '59', '183');
INSERT INTO `role_permissions` VALUES ('2526', '59', '184');
INSERT INTO `role_permissions` VALUES ('2527', '59', '195');
INSERT INTO `role_permissions` VALUES ('2528', '59', '199');
INSERT INTO `role_permissions` VALUES ('2529', '59', '200');
INSERT INTO `role_permissions` VALUES ('2530', '59', '201');
INSERT INTO `role_permissions` VALUES ('2531', '59', '202');
INSERT INTO `role_permissions` VALUES ('2532', '59', '203');
INSERT INTO `role_permissions` VALUES ('2533', '59', '220');
INSERT INTO `role_permissions` VALUES ('2534', '59', '431');
INSERT INTO `role_permissions` VALUES ('2535', '59', '432');
INSERT INTO `role_permissions` VALUES ('2536', '59', '101');
INSERT INTO `role_permissions` VALUES ('2537', '59', '130');
INSERT INTO `role_permissions` VALUES ('2538', '59', '131');
INSERT INTO `role_permissions` VALUES ('2539', '59', '132');
INSERT INTO `role_permissions` VALUES ('2540', '59', '133');
INSERT INTO `role_permissions` VALUES ('2541', '59', '134');
INSERT INTO `role_permissions` VALUES ('2542', '59', '135');
INSERT INTO `role_permissions` VALUES ('2543', '59', '136');
INSERT INTO `role_permissions` VALUES ('2544', '59', '433');
INSERT INTO `role_permissions` VALUES ('2545', '59', '434');
INSERT INTO `role_permissions` VALUES ('2546', '59', '435');
INSERT INTO `role_permissions` VALUES ('2547', '59', '436');
INSERT INTO `role_permissions` VALUES ('2548', '59', '437');
INSERT INTO `role_permissions` VALUES ('2549', '59', '206');
INSERT INTO `role_permissions` VALUES ('2550', '59', '439');
INSERT INTO `role_permissions` VALUES ('2551', '59', '440');
INSERT INTO `role_permissions` VALUES ('2552', '59', '442');
INSERT INTO `role_permissions` VALUES ('2553', '59', '443');
INSERT INTO `role_permissions` VALUES ('2554', '53', '73');
INSERT INTO `role_permissions` VALUES ('2555', '53', '70');
INSERT INTO `role_permissions` VALUES ('2556', '53', '80');
INSERT INTO `role_permissions` VALUES ('2557', '53', '83');
INSERT INTO `role_permissions` VALUES ('2558', '53', '92');
INSERT INTO `role_permissions` VALUES ('2559', '53', '93');
INSERT INTO `role_permissions` VALUES ('2560', '53', '118');
INSERT INTO `role_permissions` VALUES ('2561', '53', '120');
INSERT INTO `role_permissions` VALUES ('2562', '53', '121');
INSERT INTO `role_permissions` VALUES ('2563', '53', '122');
INSERT INTO `role_permissions` VALUES ('2564', '53', '146');
INSERT INTO `role_permissions` VALUES ('2565', '53', '148');
INSERT INTO `role_permissions` VALUES ('2566', '53', '149');
INSERT INTO `role_permissions` VALUES ('2567', '53', '150');
INSERT INTO `role_permissions` VALUES ('2568', '53', '151');
INSERT INTO `role_permissions` VALUES ('2569', '53', '153');
INSERT INTO `role_permissions` VALUES ('2570', '53', '160');
INSERT INTO `role_permissions` VALUES ('2571', '53', '163');
INSERT INTO `role_permissions` VALUES ('2572', '53', '164');
INSERT INTO `role_permissions` VALUES ('2573', '53', '168');
INSERT INTO `role_permissions` VALUES ('2574', '53', '169');
INSERT INTO `role_permissions` VALUES ('2575', '53', '174');
INSERT INTO `role_permissions` VALUES ('2576', '53', '176');
INSERT INTO `role_permissions` VALUES ('2577', '53', '182');
INSERT INTO `role_permissions` VALUES ('2578', '53', '183');
INSERT INTO `role_permissions` VALUES ('2579', '53', '184');
INSERT INTO `role_permissions` VALUES ('2580', '53', '195');
INSERT INTO `role_permissions` VALUES ('2581', '53', '199');
INSERT INTO `role_permissions` VALUES ('2582', '53', '200');
INSERT INTO `role_permissions` VALUES ('2583', '53', '201');
INSERT INTO `role_permissions` VALUES ('2584', '53', '202');
INSERT INTO `role_permissions` VALUES ('2585', '53', '203');
INSERT INTO `role_permissions` VALUES ('2586', '53', '431');
INSERT INTO `role_permissions` VALUES ('2587', '53', '432');
INSERT INTO `role_permissions` VALUES ('2588', '53', '101');
INSERT INTO `role_permissions` VALUES ('2589', '53', '130');
INSERT INTO `role_permissions` VALUES ('2590', '53', '131');
INSERT INTO `role_permissions` VALUES ('2591', '53', '132');
INSERT INTO `role_permissions` VALUES ('2592', '53', '133');
INSERT INTO `role_permissions` VALUES ('2593', '53', '134');
INSERT INTO `role_permissions` VALUES ('2594', '53', '135');
INSERT INTO `role_permissions` VALUES ('2595', '53', '136');
INSERT INTO `role_permissions` VALUES ('2596', '53', '433');
INSERT INTO `role_permissions` VALUES ('2597', '53', '434');
INSERT INTO `role_permissions` VALUES ('2598', '53', '435');
INSERT INTO `role_permissions` VALUES ('2599', '53', '436');
INSERT INTO `role_permissions` VALUES ('2600', '53', '437');
INSERT INTO `role_permissions` VALUES ('2601', '53', '206');
INSERT INTO `role_permissions` VALUES ('2602', '53', '439');
INSERT INTO `role_permissions` VALUES ('2603', '53', '440');
INSERT INTO `role_permissions` VALUES ('2604', '53', '443');
INSERT INTO `role_permissions` VALUES ('2605', '54', '73');
INSERT INTO `role_permissions` VALUES ('2606', '54', '70');
INSERT INTO `role_permissions` VALUES ('2607', '54', '80');
INSERT INTO `role_permissions` VALUES ('2608', '54', '83');
INSERT INTO `role_permissions` VALUES ('2609', '54', '92');
INSERT INTO `role_permissions` VALUES ('2610', '54', '93');
INSERT INTO `role_permissions` VALUES ('2611', '54', '118');
INSERT INTO `role_permissions` VALUES ('2612', '54', '120');
INSERT INTO `role_permissions` VALUES ('2613', '54', '121');
INSERT INTO `role_permissions` VALUES ('2614', '54', '122');
INSERT INTO `role_permissions` VALUES ('2615', '54', '146');
INSERT INTO `role_permissions` VALUES ('2616', '54', '148');
INSERT INTO `role_permissions` VALUES ('2617', '54', '149');
INSERT INTO `role_permissions` VALUES ('2618', '54', '150');
INSERT INTO `role_permissions` VALUES ('2619', '54', '151');
INSERT INTO `role_permissions` VALUES ('2620', '54', '153');
INSERT INTO `role_permissions` VALUES ('2621', '54', '160');
INSERT INTO `role_permissions` VALUES ('2622', '54', '163');
INSERT INTO `role_permissions` VALUES ('2623', '54', '164');
INSERT INTO `role_permissions` VALUES ('2624', '54', '168');
INSERT INTO `role_permissions` VALUES ('2625', '54', '169');
INSERT INTO `role_permissions` VALUES ('2626', '54', '174');
INSERT INTO `role_permissions` VALUES ('2627', '54', '176');
INSERT INTO `role_permissions` VALUES ('2628', '54', '182');
INSERT INTO `role_permissions` VALUES ('2629', '54', '183');
INSERT INTO `role_permissions` VALUES ('2630', '54', '184');
INSERT INTO `role_permissions` VALUES ('2631', '54', '195');
INSERT INTO `role_permissions` VALUES ('2632', '54', '199');
INSERT INTO `role_permissions` VALUES ('2633', '54', '200');
INSERT INTO `role_permissions` VALUES ('2634', '54', '201');
INSERT INTO `role_permissions` VALUES ('2635', '54', '202');
INSERT INTO `role_permissions` VALUES ('2636', '54', '203');
INSERT INTO `role_permissions` VALUES ('2637', '54', '431');
INSERT INTO `role_permissions` VALUES ('2638', '54', '432');
INSERT INTO `role_permissions` VALUES ('2639', '54', '101');
INSERT INTO `role_permissions` VALUES ('2640', '54', '130');
INSERT INTO `role_permissions` VALUES ('2641', '54', '131');
INSERT INTO `role_permissions` VALUES ('2642', '54', '132');
INSERT INTO `role_permissions` VALUES ('2643', '54', '133');
INSERT INTO `role_permissions` VALUES ('2644', '54', '134');
INSERT INTO `role_permissions` VALUES ('2645', '54', '135');
INSERT INTO `role_permissions` VALUES ('2646', '54', '136');
INSERT INTO `role_permissions` VALUES ('2647', '54', '433');
INSERT INTO `role_permissions` VALUES ('2648', '54', '434');
INSERT INTO `role_permissions` VALUES ('2649', '54', '435');
INSERT INTO `role_permissions` VALUES ('2650', '54', '436');
INSERT INTO `role_permissions` VALUES ('2651', '54', '437');
INSERT INTO `role_permissions` VALUES ('2652', '54', '206');
INSERT INTO `role_permissions` VALUES ('2653', '54', '439');
INSERT INTO `role_permissions` VALUES ('2654', '54', '440');
INSERT INTO `role_permissions` VALUES ('2655', '54', '443');
INSERT INTO `role_permissions` VALUES ('2656', '55', '73');
INSERT INTO `role_permissions` VALUES ('2657', '55', '70');
INSERT INTO `role_permissions` VALUES ('2658', '55', '80');
INSERT INTO `role_permissions` VALUES ('2659', '55', '83');
INSERT INTO `role_permissions` VALUES ('2660', '55', '92');
INSERT INTO `role_permissions` VALUES ('2661', '55', '93');
INSERT INTO `role_permissions` VALUES ('2662', '55', '118');
INSERT INTO `role_permissions` VALUES ('2663', '55', '120');
INSERT INTO `role_permissions` VALUES ('2664', '55', '121');
INSERT INTO `role_permissions` VALUES ('2665', '55', '122');
INSERT INTO `role_permissions` VALUES ('2666', '55', '146');
INSERT INTO `role_permissions` VALUES ('2667', '55', '148');
INSERT INTO `role_permissions` VALUES ('2668', '55', '149');
INSERT INTO `role_permissions` VALUES ('2669', '55', '150');
INSERT INTO `role_permissions` VALUES ('2670', '55', '151');
INSERT INTO `role_permissions` VALUES ('2671', '55', '153');
INSERT INTO `role_permissions` VALUES ('2672', '55', '160');
INSERT INTO `role_permissions` VALUES ('2673', '55', '163');
INSERT INTO `role_permissions` VALUES ('2674', '55', '164');
INSERT INTO `role_permissions` VALUES ('2675', '55', '168');
INSERT INTO `role_permissions` VALUES ('2676', '55', '169');
INSERT INTO `role_permissions` VALUES ('2677', '55', '174');
INSERT INTO `role_permissions` VALUES ('2678', '55', '176');
INSERT INTO `role_permissions` VALUES ('2679', '55', '182');
INSERT INTO `role_permissions` VALUES ('2680', '55', '183');
INSERT INTO `role_permissions` VALUES ('2681', '55', '184');
INSERT INTO `role_permissions` VALUES ('2682', '55', '195');
INSERT INTO `role_permissions` VALUES ('2683', '55', '199');
INSERT INTO `role_permissions` VALUES ('2684', '55', '200');
INSERT INTO `role_permissions` VALUES ('2685', '55', '201');
INSERT INTO `role_permissions` VALUES ('2686', '55', '202');
INSERT INTO `role_permissions` VALUES ('2687', '55', '203');
INSERT INTO `role_permissions` VALUES ('2688', '55', '431');
INSERT INTO `role_permissions` VALUES ('2689', '55', '432');
INSERT INTO `role_permissions` VALUES ('2690', '55', '101');
INSERT INTO `role_permissions` VALUES ('2691', '55', '130');
INSERT INTO `role_permissions` VALUES ('2692', '55', '131');
INSERT INTO `role_permissions` VALUES ('2693', '55', '132');
INSERT INTO `role_permissions` VALUES ('2694', '55', '133');
INSERT INTO `role_permissions` VALUES ('2695', '55', '134');
INSERT INTO `role_permissions` VALUES ('2696', '55', '135');
INSERT INTO `role_permissions` VALUES ('2697', '55', '136');
INSERT INTO `role_permissions` VALUES ('2698', '55', '433');
INSERT INTO `role_permissions` VALUES ('2699', '55', '434');
INSERT INTO `role_permissions` VALUES ('2700', '55', '435');
INSERT INTO `role_permissions` VALUES ('2701', '55', '436');
INSERT INTO `role_permissions` VALUES ('2702', '55', '437');
INSERT INTO `role_permissions` VALUES ('2703', '55', '206');
INSERT INTO `role_permissions` VALUES ('2704', '55', '439');
INSERT INTO `role_permissions` VALUES ('2705', '55', '440');
INSERT INTO `role_permissions` VALUES ('2706', '55', '443');
INSERT INTO `role_permissions` VALUES ('2707', '56', '73');
INSERT INTO `role_permissions` VALUES ('2708', '56', '70');
INSERT INTO `role_permissions` VALUES ('2709', '56', '80');
INSERT INTO `role_permissions` VALUES ('2710', '56', '83');
INSERT INTO `role_permissions` VALUES ('2711', '56', '92');
INSERT INTO `role_permissions` VALUES ('2712', '56', '93');
INSERT INTO `role_permissions` VALUES ('2713', '56', '118');
INSERT INTO `role_permissions` VALUES ('2714', '56', '120');
INSERT INTO `role_permissions` VALUES ('2715', '56', '121');
INSERT INTO `role_permissions` VALUES ('2716', '56', '122');
INSERT INTO `role_permissions` VALUES ('2717', '56', '146');
INSERT INTO `role_permissions` VALUES ('2718', '56', '148');
INSERT INTO `role_permissions` VALUES ('2719', '56', '149');
INSERT INTO `role_permissions` VALUES ('2720', '56', '150');
INSERT INTO `role_permissions` VALUES ('2721', '56', '151');
INSERT INTO `role_permissions` VALUES ('2722', '56', '153');
INSERT INTO `role_permissions` VALUES ('2723', '56', '160');
INSERT INTO `role_permissions` VALUES ('2724', '56', '163');
INSERT INTO `role_permissions` VALUES ('2725', '56', '164');
INSERT INTO `role_permissions` VALUES ('2726', '56', '168');
INSERT INTO `role_permissions` VALUES ('2727', '56', '169');
INSERT INTO `role_permissions` VALUES ('2728', '56', '174');
INSERT INTO `role_permissions` VALUES ('2729', '56', '176');
INSERT INTO `role_permissions` VALUES ('2730', '56', '182');
INSERT INTO `role_permissions` VALUES ('2731', '56', '183');
INSERT INTO `role_permissions` VALUES ('2732', '56', '184');
INSERT INTO `role_permissions` VALUES ('2733', '56', '195');
INSERT INTO `role_permissions` VALUES ('2734', '56', '199');
INSERT INTO `role_permissions` VALUES ('2735', '56', '200');
INSERT INTO `role_permissions` VALUES ('2736', '56', '201');
INSERT INTO `role_permissions` VALUES ('2737', '56', '202');
INSERT INTO `role_permissions` VALUES ('2738', '56', '203');
INSERT INTO `role_permissions` VALUES ('2739', '56', '431');
INSERT INTO `role_permissions` VALUES ('2740', '56', '432');
INSERT INTO `role_permissions` VALUES ('2741', '56', '101');
INSERT INTO `role_permissions` VALUES ('2742', '56', '130');
INSERT INTO `role_permissions` VALUES ('2743', '56', '131');
INSERT INTO `role_permissions` VALUES ('2744', '56', '132');
INSERT INTO `role_permissions` VALUES ('2745', '56', '133');
INSERT INTO `role_permissions` VALUES ('2746', '56', '134');
INSERT INTO `role_permissions` VALUES ('2747', '56', '135');
INSERT INTO `role_permissions` VALUES ('2748', '56', '136');
INSERT INTO `role_permissions` VALUES ('2749', '56', '433');
INSERT INTO `role_permissions` VALUES ('2750', '56', '434');
INSERT INTO `role_permissions` VALUES ('2751', '56', '435');
INSERT INTO `role_permissions` VALUES ('2752', '56', '436');
INSERT INTO `role_permissions` VALUES ('2753', '56', '437');
INSERT INTO `role_permissions` VALUES ('2754', '56', '206');
INSERT INTO `role_permissions` VALUES ('2755', '56', '439');
INSERT INTO `role_permissions` VALUES ('2756', '56', '440');
INSERT INTO `role_permissions` VALUES ('2757', '56', '443');
INSERT INTO `role_permissions` VALUES ('2841', '2', '99');
INSERT INTO `role_permissions` VALUES ('2842', '48', '99');
INSERT INTO `role_permissions` VALUES ('2843', '49', '99');
INSERT INTO `role_permissions` VALUES ('2844', '50', '99');
INSERT INTO `role_permissions` VALUES ('2845', '51', '99');
INSERT INTO `role_permissions` VALUES ('2846', '12', '99');
INSERT INTO `role_permissions` VALUES ('2847', '22', '99');
INSERT INTO `role_permissions` VALUES ('2848', '29', '99');
INSERT INTO `role_permissions` VALUES ('2849', '35', '99');
INSERT INTO `role_permissions` VALUES ('2850', '40', '99');
INSERT INTO `role_permissions` VALUES ('2852', '11', '99');
INSERT INTO `role_permissions` VALUES ('2853', '21', '99');
INSERT INTO `role_permissions` VALUES ('2854', '28', '99');
INSERT INTO `role_permissions` VALUES ('2855', '34', '99');
INSERT INTO `role_permissions` VALUES ('2856', '39', '99');
INSERT INTO `role_permissions` VALUES ('2857', '40', '425');
INSERT INTO `role_permissions` VALUES ('2858', '40', '424');
INSERT INTO `role_permissions` VALUES ('2859', '39', '425');
INSERT INTO `role_permissions` VALUES ('2860', '39', '424');
INSERT INTO `role_permissions` VALUES ('2861', '40', '423');
INSERT INTO `role_permissions` VALUES ('2862', '39', '423');
