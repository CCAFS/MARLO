DROP TABLE IF EXISTS `capdev_partners`;

CREATE TABLE `capdev_partners` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `capdev_id` bigint(20) NOT NULL,
  `institution_id` bigint(20) NULL,
  `is_active` tinyint(1) NOT NULL,
  `active_since` timestamp NULL,
  `created_by` bigint(20) NULL,
  `modified_by` bigint(20) NULL,
  `modification_justification` text NULL,
  PRIMARY KEY (`id`),
  CONSTRAINT `capdev_partner_capdevid_fk`         FOREIGN KEY (`capdev_id`)         REFERENCES `capacity_development` (`id`),
  CONSTRAINT `capdev_partner_institucionid_fk`    FOREIGN KEY (`institution_id`)    REFERENCES `institutions` (`id`),
  CONSTRAINT `capdev_partner_createdby_fk`        FOREIGN KEY (`created_by`)        REFERENCES `users` (`id`),
  CONSTRAINT `capdev_partner_modifiedby_fk`       FOREIGN KEY (`modified_by`)       REFERENCES `users` (`id`));



DROP TABLE IF EXISTS `capdev_outputs`;

CREATE TABLE `capdev_outputs` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `capdev_id` bigint(20) NOT NULL,
  `output_id` int(11) NULL,
  `is_active` tinyint(1) NOT NULL,
  `active_since` timestamp NULL,
  `created_by` bigint(20) NULL,
  `modified_by` bigint(20) NULL,
  `modification_justification` text NULL,
  PRIMARY KEY (`id`),
  CONSTRAINT `capdev_output_capdevid_fk`      FOREIGN KEY (`capdev_id`)     REFERENCES `capacity_development` (`id`),
  CONSTRAINT `capdev_output_outputid_fk`      FOREIGN KEY (`output_id`)     REFERENCES `center_outputs` (`id`),
  CONSTRAINT `capdev_output_createdby_fk`     FOREIGN KEY (`created_by`)    REFERENCES `users` (`id`),
  CONSTRAINT `capdev_output_modifiedby_fk`    FOREIGN KEY (`modified_by`)   REFERENCES `users` (`id`));
