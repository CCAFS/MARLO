ALTER TABLE `phases`
ADD COLUMN `start_date`  date NULL AFTER `crp_id`,
ADD COLUMN `end_date`  date NULL AFTER `start_date`;

