ALTER TABLE `deliverable_types`
ADD COLUMN `admin_type`  tinyint(1) NULL DEFAULT 0 AFTER `crp_id`;

UPDATE `deliverable_types` SET `crp_id`=NULL WHERE (`id`='84');
UPDATE `deliverable_types` SET `admin_type`='1' WHERE (`id`='84');