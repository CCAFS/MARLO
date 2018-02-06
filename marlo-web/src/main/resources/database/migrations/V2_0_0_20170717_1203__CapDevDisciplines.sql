DROP TABLE IF EXISTS `capdev_discipline`;

CREATE TABLE `capdev_discipline` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `capdev_id` bigint(20) NULL,
  `discipline_id` bigint(20) NULL,
  `is_active` tinyint(1) NOT NULL,
  `active_since` timestamp NOT NULL,
  `created_by` bigint(20) NULL,
  `modified_by` bigint(20) NULL,
  `modification_justification` text NULL,
  PRIMARY KEY (`id`),
  CONSTRAINT `capdev_discipline_capdevid_fk` FOREIGN KEY (`capdev_id`)      REFERENCES `capacity_development` (`id`),
  CONSTRAINT `capdev_discipline_disci_fk`    FOREIGN KEY (`discipline_id`)  REFERENCES `disciplines` (`id`),
  CONSTRAINT `capdev_discipline_created_fk`  FOREIGN KEY (`created_by`)     REFERENCES `users` (`id`),
  CONSTRAINT `capdev_discipline_modified_fk` FOREIGN KEY (`modified_by`)    REFERENCES `users` (`id`));
