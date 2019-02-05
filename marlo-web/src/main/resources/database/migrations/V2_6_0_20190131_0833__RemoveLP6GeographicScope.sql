SET FOREIGN_KEY_CHECKS=0;

ALTER TABLE `project_lp6_contribution` DROP FOREIGN KEY `fk_project_lp6_regional`;

ALTER TABLE `project_lp6_contribution`
DROP COLUMN `geographic_scope_id`,
DROP INDEX `rep_ind_geographic_scope`,
DEFAULT CHARACTER SET=utf8 COLLATE=utf8_general_ci;

DROP TABLE IF EXISTS `lp6_contribution_geographic_scope`;

SET FOREIGN_KEY_CHECKS=1;