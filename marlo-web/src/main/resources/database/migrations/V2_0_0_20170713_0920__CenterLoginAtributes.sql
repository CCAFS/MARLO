ALTER TABLE `centers`
ADD COLUMN `is_marlo`  tinyint(1) NOT NULL AFTER `is_active`,
ADD COLUMN `login`  tinyint(4) NULL AFTER `is_marlo`;

UPDATE CENTERS set is_marlo=1;
UPDATE CENTERS set login=1;