ALTER TABLE `crp_outcome_sub_idos`
ADD COLUMN `is_primary`  tinyint(1) DEFAULT 0 AFTER `crp_program_outcome_id`;
