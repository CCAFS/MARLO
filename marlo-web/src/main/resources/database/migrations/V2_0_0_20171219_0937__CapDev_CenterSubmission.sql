ALTER TABLE `center_submissions`
ADD COLUMN `capdev_id`  bigint(20) NULL AFTER `project_id`;

ALTER TABLE `center_submissions` ADD CONSTRAINT `submissions_capdev_fk` FOREIGN KEY (`capdev_id`) REFERENCES `capacity_development` (`id`);

