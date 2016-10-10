ALTER TABLE `project_crp_contributions`
MODIFY COLUMN `crp_id`  bigint(20) NULL DEFAULT NULL AFTER `project_id`;

ALTER TABLE `project_crp_contributions` ADD FOREIGN KEY (`crp_id`) REFERENCES `crps` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT;

