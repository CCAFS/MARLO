ALTER TABLE `roles` DROP FOREIGN KEY `roles_ibfk_1`;

ALTER TABLE `roles`
DROP COLUMN `crp_id`,
MODIFY COLUMN `global_unit_id`  bigint(20) NOT NULL AFTER `order`;

