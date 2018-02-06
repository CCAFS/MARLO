/*###------------------------------------------------------------*/

DROP TABLE IF EXISTS `capdev_supp_docs_documents`;

CREATE TABLE `capdev_supp_docs_documents` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `capdev_supporting_docs_id` bigint(20) NULL,
  `link` varchar(500) NULL,
  `is_active` tinyint(1) NULL,
  `active_since` timestamp NULL,
  `created_by` bigint(20) NULL,
  `modified_by` bigint(20) NULL,
  `modification_justification` text NULL,
  PRIMARY KEY (`id`),
  CONSTRAINT `capdev_supp_documents_docs_id`       FOREIGN KEY (`capdev_supporting_docs_id`)     REFERENCES `capdev_supporting_docs` (`id`),
  CONSTRAINT `capdev_supp_documents_created_fk`    FOREIGN KEY (`created_by`)                    REFERENCES `users` (`id`),
  CONSTRAINT `capdev_supp_documents_modified_fk`   FOREIGN KEY (`modified_by`)                   REFERENCES `users` (`id`));