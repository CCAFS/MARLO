ALTER TABLE `crp_program_leaders`
ADD COLUMN `manager`  tinyint(1) NOT NULL DEFAULT 0 AFTER `is_active`;

