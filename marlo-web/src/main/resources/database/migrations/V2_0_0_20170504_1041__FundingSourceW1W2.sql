ALTER TABLE `funding_sources`
ADD COLUMN `w1w2`  tinyint(1) NULL DEFAULT 0 AFTER `modification_justification`;

