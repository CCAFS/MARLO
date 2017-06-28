SET FOREIGN_KEY_CHECKS=0;
CREATE TABLE `nextuser_types` (
  `id` INT(11) NOT NULL AUTO_INCREMENT,
  `name` VARCHAR(60) NOT NULL,
  `active_since` TIMESTAMP NULL DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP,
  `is_active` TINYINT(4) NULL DEFAULT NULL,
  `created_by` BIGINT(20) NULL DEFAULT NULL,
  `modified_by` BIGINT(20) NULL DEFAULT NULL,
  `modification_justification` TEXT NULL,
  `parent_type_id` INT(11) NULL DEFAULT NULL,
  PRIMARY KEY (`id`, `name`),
  INDEX `fk_nextusertypes_created_by` (`created_by`),
  INDEX `fk_nextusertypes_modified_by` (`modified_by`),
  INDEX `fk_nextusertypes_types` (`parent_type_id`),
  CONSTRAINT `fk_nextusertypes_created_by` FOREIGN KEY (`created_by`) REFERENCES `users` (`id`) ON UPDATE NO ACTION ON DELETE NO ACTION,
  CONSTRAINT `fk_nextusertypes_modified_by` FOREIGN KEY (`modified_by`) REFERENCES `users` (`id`) ON UPDATE NO ACTION ON DELETE NO ACTION,
  CONSTRAINT `fk_nextusertypes_types` FOREIGN KEY (`parent_type_id`) REFERENCES `nextuser_types` (`id`) ON UPDATE NO ACTION ON DELETE NO ACTION
);