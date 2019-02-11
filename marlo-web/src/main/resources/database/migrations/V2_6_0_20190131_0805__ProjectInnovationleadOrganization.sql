ALTER TABLE `project_innovation_info`
ADD COLUMN `lead_organization_id`  bigint(20) NULL AFTER `youth_explaniation`,
ADD INDEX `lead_organization_id` (`project_innovation_id`) USING BTREE ;

ALTER TABLE `project_innovation_info` ADD CONSTRAINT `project_innovation_info_ibfk_13` FOREIGN KEY (`lead_organization_id`) REFERENCES `institutions` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT;