ALTER TABLE `funding_sources`
ADD COLUMN `create_date`  timestamp NULL DEFAULT NULL AFTER `global_unit_id`;

update funding_sources set create_date='2018-01-01';
