ALTER TABLE `crp_program_outcomes`
MODIFY COLUMN `description`  text CHARACTER SET utf8 COLLATE utf8_general_ci NULL AFTER `id`,
MODIFY COLUMN `year`  int(11) NULL AFTER `description`,
MODIFY COLUMN `value`  decimal(10,2) NULL AFTER `year`,
MODIFY COLUMN `target_unit_id`  bigint(20) NULL AFTER `value`;

ALTER TABLE `crp_outcome_sub_idos`
MODIFY COLUMN `contribution`  decimal(10,2) NULL AFTER `id`,
MODIFY COLUMN `srf_sub_ido_id`  bigint(20) NULL AFTER `contribution`;

ALTER TABLE `crp_milestones`
MODIFY COLUMN `title`  text CHARACTER SET utf8 COLLATE utf8_general_ci NULL AFTER `id`,
MODIFY COLUMN `year`  int(11) NULL AFTER `title`,
MODIFY COLUMN `value`  decimal(10,2) NULL AFTER `year`,
MODIFY COLUMN `target_unit_id`  bigint(20) NULL AFTER `value`;

