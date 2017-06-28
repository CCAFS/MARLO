ALTER TABLE `reserach_outputs`
CHANGE COLUMN `output` `title`  text CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL AFTER `id`;