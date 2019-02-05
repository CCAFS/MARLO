ALTER TABLE `project_lp6_contribution`
ADD COLUMN `geographic_scope_id`  bigint(20) NULL AFTER `initiative_related`,
ADD COLUMN `region_id`  bigint(20) NULL AFTER `geographic_scope_id`,
ADD INDEX `rep_ind_geographic_scope` (`geographic_scope_id`) ;

ALTER TABLE `project_lp6_contribution` ADD CONSTRAINT `fk_project_lp6_regional` FOREIGN KEY (`geographic_scope_id`) REFERENCES `rep_ind_geographic_scopes` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT;

ALTER TABLE `project_lp6_contribution` ADD CONSTRAINT `fk_project_lp6_regions` FOREIGN KEY (`region_id`) REFERENCES `rep_ind_regions` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT;