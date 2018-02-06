DROP TABLE IF EXISTS `capdev_locations`;

CREATE TABLE `capdev_locations` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `capdev_id` bigint(20) NULL,
  `loc_element_id` bigint(20) NULL,
  `is_active` tinyint(1) NOT NULL,
  `active_since` timestamp NOT NULL,
  `created_by` bigint(20) NULL,
  `modified_by` bigint(20) NULL,
  `modification_justification` text NULL,
  PRIMARY KEY (`id`), 
  CONSTRAINT `capdev_location_created_fk`  FOREIGN KEY (`created_by`)     REFERENCES  `users` (`id`),
  CONSTRAINT `capdev_location_modified_fk` FOREIGN KEY (`modified_by`)    REFERENCES  `users` (`id`),
  CONSTRAINT `capdev_location_id_fk`       FOREIGN KEY (`capdev_id`)      REFERENCES  `capacity_development` (`id`),
  CONSTRAINT `capdev_location_element_fk`  FOREIGN KEY (`loc_element_id`) REFERENCES  `loc_elements` (`id`)
  )
;