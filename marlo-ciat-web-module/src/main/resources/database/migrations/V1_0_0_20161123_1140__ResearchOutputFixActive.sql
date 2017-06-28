ALTER TABLE `research_output_partner_persons`
MODIFY COLUMN `is_active`  tinyint(1) NULL DEFAULT NULL AFTER `outcome_partner_id`;