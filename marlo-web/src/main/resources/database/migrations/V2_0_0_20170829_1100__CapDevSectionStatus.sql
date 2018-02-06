ALTER TABLE `center_section_statuses`
ADD COLUMN `capdev_id`  bigint(20) NULL AFTER `deliverable_id`;

ALTER TABLE `center_section_statuses` ADD CONSTRAINT `center_section_statuses_ibfk_6` FOREIGN KEY (`capdev_id`) REFERENCES `capacity_development` (`id`);