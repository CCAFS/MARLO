ALTER TABLE `deliverables`
ADD COLUMN `adopted_license`  tinyint(1) NULL AFTER `cross_cutting_na`;

UPDATE deliverables SET adopted_license=0;