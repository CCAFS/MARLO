ALTER TABLE `section_statuses`
ADD COLUMN `project_lp6_contribution_id`  bigint(20) NULL AFTER `project_policy_id`;

ALTER TABLE `section_statuses` ADD FOREIGN KEY (`project_lp6_contribution_id`) REFERENCES `project_lp6_contribution` (`id`);

