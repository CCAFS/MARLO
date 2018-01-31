DROP TABLE IF EXISTS `capdev_participant`;

CREATE TABLE `capdev_participant` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `capdev_id` bigint(20) NULL,
  `participant_id` bigint(20) NULL,
  `is_active` tinyint(1) NOT NULL,
  `active_since` timestamp NULL,
  `created_by` bigint(20) NOT NULL,
  `modified_by` bigint(20) NULL,
  `modification_justification` text NULL,
  PRIMARY KEY (`id`),
  CONSTRAINT `capdev_partct_capdevid_fk`  FOREIGN KEY (`capdev_id`)       REFERENCES `capacity_development` (`id`),
  CONSTRAINT `capdev_partct_partctid_fk`  FOREIGN KEY (`participant_id`)  REFERENCES `participant` (`id`),
  CONSTRAINT `capdev_partct_created_fk`   FOREIGN KEY (`created_by`)      REFERENCES `users` (`id`),
  CONSTRAINT `capdev_partct_modified_fk`  FOREIGN KEY (`modified_by`)     REFERENCES `users` (`id`));