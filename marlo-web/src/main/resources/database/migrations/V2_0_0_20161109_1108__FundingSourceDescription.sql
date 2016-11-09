ALTER TABLE `funding_sources`
CHANGE COLUMN `description` `title`  text CHARACTER SET utf8 COLLATE utf8_general_ci NULL AFTER `id`,

ADD COLUMN `description`  text NULL AFTER `title`;
