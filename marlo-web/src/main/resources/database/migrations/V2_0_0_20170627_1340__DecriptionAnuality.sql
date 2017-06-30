
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for projects
-- ----------------------------
DROP TABLE
IF EXISTS `projects_info`;

CREATE TABLE `projects_info` (
  `id` BIGINT (20) NOT NULL AUTO_INCREMENT,
  `title` text,
  `project_id` BIGINT (20) DEFAULT NULL,
  `summary` text,
  `start_date` date DEFAULT NULL,
  `end_date` date DEFAULT NULL,
  `type` text,
  `is_cofinancing` TINYINT (1) NOT NULL DEFAULT '0',
  `leader_responsabilities` text,
  `liaison_institution_id` BIGINT (20) DEFAULT NULL,
  `liaison_user_id` BIGINT (20) DEFAULT NULL COMMENT 'foreign key to the table liaison_users',
  `modified_by` BIGINT (20) NOT NULL,
  `modification_justification` text NOT NULL,
  `is_project_leader_edit` TINYINT (1) DEFAULT '1',
  `scale` INT (1) NOT NULL DEFAULT '0',
  `no_regional` TINYINT (1) DEFAULT NULL,
  `preset_date` TIMESTAMP NULL DEFAULT NULL,
  `is_location_global` TINYINT (1) DEFAULT NULL,
  `status` BIGINT (20) DEFAULT NULL,
  `id_phase` BIGINT (20) DEFAULT NULL,
  `status_justification` text,
  `gender_analysis` text,
  `cross_cutting_gender` TINYINT (1) DEFAULT NULL,
  `cross_cutting_youth` TINYINT (1) DEFAULT NULL,
  `cross_cutting_capacity` TINYINT (1) DEFAULT NULL,
  `cross_cutting_na` TINYINT (1) DEFAULT NULL,
  `dimension` text,
  `administrative` TINYINT (1) DEFAULT NULL,
  `reporting` TINYINT (1) DEFAULT '0',
  
  PRIMARY KEY (`id`),
  KEY `FK_projectsinfo_liaison_liaison_users_idx` (`liaison_user_id`) USING BTREE,
  KEY `FK_projectsinfo_users_modified_by` (`modified_by`) USING BTREE,
  KEY `institution_id` (`liaison_institution_id`) USING BTREE,
  CONSTRAINT `projects_info_ibfk_2` FOREIGN KEY (`modified_by`) REFERENCES `users` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `projects_info_ibfk_3` FOREIGN KEY (`liaison_user_id`) REFERENCES `liaison_users` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `projects_info_ibfk_4` FOREIGN KEY (`liaison_institution_id`) REFERENCES `liaison_institutions` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `projects_info_ibfk_5` FOREIGN KEY (`id_phase`) REFERENCES `phases` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `projects_info_ibfk_6` FOREIGN KEY (`project_id`) REFERENCES `projects` (`id`) ON DELETE CASCADE ON UPDATE CASCADE

) ENGINE = INNODB DEFAULT CHARSET = utf8;


SET FOREIGN_KEY_CHECKS = 0;

INSERT INTO projects_info (
  project_id,
  title,
  summary,
  start_date,
  end_date,
  type,
  is_cofinancing,
  leader_responsabilities,
  liaison_institution_id,
  liaison_user_id,
  modified_by,
  modification_justification,
  is_project_leader_edit,
  scale,
  no_regional,
  preset_date,
  is_location_global,
  STATUS,
  status_justification,
  gender_analysis,
  cross_cutting_gender,
  cross_cutting_youth,
  cross_cutting_capacity,
  cross_cutting_na,
  dimension,
  administrative,
  reporting,
  id_phase
) SELECT
  p.id,
  title,
  summary,
  start_date,
  end_date,
  type,
  is_cofinancing,
  leader_responsabilities,
  liaison_institution_id,
  liaison_user_id,
  modified_by,
  modification_justification,
  is_project_leader_edit,
  scale,
  no_regional,
  preset_date,
  is_location_global,
  STATUS,
  status_justification,
  gender_analysis,
  cross_cutting_gender,
  cross_cutting_youth,
  cross_cutting_capacity,
  cross_cutting_na,
  dimension,
  administrative,
  reporting,
  ph.id
FROM
  projects p INNER JOIN project_phases pp on pp.project_id=p.id
INNER JOIN phases ph on ph.id=pp.id_phase 

where p.is_active=1 ;




ALTER TABLE `projects` DROP FOREIGN KEY `projects_ibfk_2`;

ALTER TABLE `projects` DROP FOREIGN KEY `projects_ibfk_3`;

ALTER TABLE `projects` DROP FOREIGN KEY `projects_ibfk_4`;

ALTER TABLE `projects` DROP FOREIGN KEY `projects_ibfk_6`;

ALTER TABLE `projects` DROP FOREIGN KEY `projects_ibfk_7`;

ALTER TABLE `projects` DROP FOREIGN KEY `projects_ibfk_8`;

ALTER TABLE `projects`
DROP COLUMN `title`,
DROP COLUMN `summary`,
DROP COLUMN `start_date`,
DROP COLUMN `end_date`,
DROP COLUMN `type`,
DROP COLUMN `is_cofinancing`,
DROP COLUMN `leader_responsabilities`,
DROP COLUMN `liaison_institution_id`,
DROP COLUMN `liaison_user_id`,
DROP COLUMN `requires_workplan_upload`,
DROP COLUMN `modification_justification`,
DROP COLUMN `is_project_leader_edit`,
DROP COLUMN `file_workplan`,
DROP COLUMN `file_bilateral_contract_name`,
DROP COLUMN `file_annual_report_to_donnor`,
DROP COLUMN `scale`,
DROP COLUMN `no_regional`,
DROP COLUMN `preset_date`,
DROP COLUMN `is_location_global`,
DROP COLUMN `status`,
DROP COLUMN `status_justification`,
DROP COLUMN `gender_analysis`,
DROP COLUMN `cross_cutting_gender`,
DROP COLUMN `cross_cutting_youth`,
DROP COLUMN `cross_cutting_capacity`,
DROP COLUMN `cross_cutting_na`,
DROP COLUMN `dimension`,
DROP COLUMN `administrative`,
DROP COLUMN `reporting`;

