ALTER TABLE `crps`
MODIFY COLUMN `name`  text CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL AFTER `id`,
ADD COLUMN `acronym`  varchar(50) NOT NULL AFTER `name`;

