ALTER TABLE `participant`
MODIFY COLUMN `age`  bigint(20) NULL DEFAULT NULL AFTER `gender`;

ALTER TABLE `participant` ADD CONSTRAINT `participant_age_fk` FOREIGN KEY (`age`) REFERENCES `capdev_range_age` (`id`);