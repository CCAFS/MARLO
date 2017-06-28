SET FOREIGN_KEY_CHECKS=0;

CREATE TABLE `research_outputs_next_users` (
  `id` INT(11) NOT NULL AUTO_INCREMENT,
  `research_output_id` INT(11) NOT NULL,
  `nextuser_type_id` INT(11) NOT NULL,
  `active_since` TIMESTAMP NULL DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP,
  `is_active` TINYINT(4) NULL DEFAULT NULL,
  `created_by` BIGINT(20) NULL DEFAULT NULL,
  `modified_by` BIGINT(20) NULL DEFAULT NULL,
  `modification_justification` TEXT NULL,
  PRIMARY KEY (`id`),
  INDEX `fk_output_nextsubuser` (`nextuser_type_id`),
  INDEX `fk_output_researchoutput` (`research_output_id`),
  INDEX `fk_outputnextsubusers_created_by` (`created_by`),
  INDEX `fk_outputnextsubusers_modified_by` (`modified_by`),
  CONSTRAINT `fk_output_nextuser_createdby` FOREIGN KEY (`created_by`) REFERENCES `users` (`id`) ON UPDATE NO ACTION ON DELETE NO ACTION,
  CONSTRAINT `fk_output_nextuser_modifiedby` FOREIGN KEY (`modified_by`) REFERENCES `users` (`id`) ON UPDATE NO ACTION ON DELETE NO ACTION,
  CONSTRAINT `fk_output_nextuser_resoutput` FOREIGN KEY (`research_output_id`) REFERENCES `research_outputs` (`id`) ON UPDATE NO ACTION ON DELETE NO ACTION,
  CONSTRAINT `fk_output_nextuser_type` FOREIGN KEY (`nextuser_type_id`) REFERENCES `nextuser_types` (`id`) ON UPDATE NO ACTION ON DELETE NO ACTION
);
