ALTER TABLE `phases`
ADD COLUMN `editable`  tinyint(1) NULL DEFAULT 1 AFTER `crp_id`,
ADD COLUMN `visible`  tinyint(1) NULL DEFAULT 1 AFTER `editable`;

