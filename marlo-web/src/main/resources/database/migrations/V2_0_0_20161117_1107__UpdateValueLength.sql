ALTER TABLE `crp_program_outcomes`
MODIFY COLUMN `value`  decimal(20,2) NULL DEFAULT NULL AFTER `year`;

ALTER TABLE `crp_milestones`
MODIFY COLUMN `value`  decimal(20,2) NULL DEFAULT NULL AFTER `year`;

