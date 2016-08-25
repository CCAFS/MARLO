START TRANSACTION;
ALTER TABLE `project_component_lessons`
ADD COLUMN `project_outcome_id`  bigint NULL AFTER `ip_program_id`;

ALTER TABLE `project_component_lessons` ADD FOREIGN KEY (`project_outcome_id`) REFERENCES `project_outcomes` (`id`);

COMMIT;