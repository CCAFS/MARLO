DROP TABLE IF EXISTS `capdev_targetgroup`;

CREATE TABLE `capdev_targetgroup` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `capdev_id` bigint(20) NULL,
  `target_group_id` bigint(20) NULL,
  `is_active` tinyint(1) NULL,
  `active_since` timestamp NULL,
  `created_by` bigint(20) NULL,
  `modified_by` bigint(20) NULL,
  `modification_justification` text NULL,
  PRIMARY KEY (`id`),
  CONSTRAINT `capdev_trgtgrp_capdevid_fk`    FOREIGN KEY (`capdev_id`)        REFERENCES `capacity_development` (`id`),
  CONSTRAINT `capdev_trgtgrp_targetgrpid_fk` FOREIGN KEY (`target_group_id`)  REFERENCES `target_groups` (`id`),
  CONSTRAINT `capdev_trgtgrp_created_fk`     FOREIGN KEY (`created_by`)       REFERENCES `users` (`id`),
  CONSTRAINT `capdev_trgtgrp_modified_fk`    FOREIGN KEY (`modified_by`)      REFERENCES `users` (`id`));