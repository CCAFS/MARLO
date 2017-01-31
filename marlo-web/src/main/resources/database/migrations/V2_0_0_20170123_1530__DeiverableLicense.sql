ALTER TABLE `deliverables`
ADD COLUMN `license`  text NULL AFTER `cross_cutting_na`,
ADD COLUMN `allow_modifications`  tinyint(1) NULL AFTER `license`;