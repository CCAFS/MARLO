ALTER TABLE `funding_sources`
ADD COLUMN `global`  tinyint(1) NULL DEFAULT 0 AFTER `modification_justification`;
