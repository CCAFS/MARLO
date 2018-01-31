DROP TABLE IF EXISTS `capdev_supporting_docs`;

CREATE TABLE `capdev_supporting_docs` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `title` varchar(200) NULL,
  `capdev_id` bigint(20) NULL,
  `deliverable_type` bigint(20) NULL,
  `publication_date` timestamp NULL,
  `is_active` tinyint(1) NULL,
  `active_since` timestamp NULL,
  `created_by` bigint(20) NULL,
  `modified_by` bigint(20) NULL,
  `modification_justification` text NULL,
  PRIMARY KEY (`id`),
  CONSTRAINT `capdev_supp_docs_deliverable_type_fk`    FOREIGN KEY (`deliverable_type`)    REFERENCES `center_deliverable_types` (`id`),
  CONSTRAINT `capdev_supp_docs_capdev_id_fk`           FOREIGN KEY (`capdev_id`)           REFERENCES `capacity_development` (`id`),
  CONSTRAINT `capdev_supp_docs_createdby_fk`           FOREIGN KEY (`created_by`)          REFERENCES `users` (`id`),
  CONSTRAINT `capdev_supp_docs_modified_fk`            FOREIGN KEY (`modified_by`)         REFERENCES `users` (`id`));






