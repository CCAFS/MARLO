ALTER TABLE `crp_users` DROP FOREIGN KEY `crp_users_ibfk_2`;

ALTER TABLE `crp_users`
DROP COLUMN `crp_id`,
MODIFY COLUMN `global_unit_id`  bigint(20) NOT NULL AFTER `modification_justification`;