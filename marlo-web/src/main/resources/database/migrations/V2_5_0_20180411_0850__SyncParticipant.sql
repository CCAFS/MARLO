ALTER TABLE `participant`
ADD COLUMN `sync`  tinyint(1) NULL DEFAULT NULL AFTER `institutions_suggested`,
ADD COLUMN `syn_date`  date NULL DEFAULT NULL AFTER `sync`;