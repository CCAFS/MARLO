ALTER TABLE `centers`
ADD COLUMN `is_marlo`  tinyint(1) NOT NULL AFTER `is_active`,
ADD COLUMN `login`  tinyint(4) NULL AFTER `is_marlo`;

UPDATE `centers` set is_marlo=1;
UPDATE `centers` set login=1;