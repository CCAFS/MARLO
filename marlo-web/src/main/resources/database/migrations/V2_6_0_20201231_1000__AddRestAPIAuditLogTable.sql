CREATE TABLE 
IF NOT EXISTS `rest_api_auditlog` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `action` varchar(128) NOT NULL,
  `detail` text NOT NULL,
  `created_date` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `entity_id` bigint NOT NULL,
  `entity_name` varchar(512) NOT NULL,
  `entity_json` text NOT NULL,
  `user_id` bigint NOT NULL,
  `main` bigint DEFAULT NULL,
  `modification_justification` text,
  `phase` bigint DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `fk_rest_api_auditlog_user_idx` (`user_id`),
  CONSTRAINT `fk_rest_api_auditlog_user` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`)
)
ENGINE=InnoDB
DEFAULT CHARACTER SET=utf8 COLLATE=utf8_general_ci
;

