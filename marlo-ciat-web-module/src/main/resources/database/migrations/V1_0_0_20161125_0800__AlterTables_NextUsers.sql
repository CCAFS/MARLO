SET FOREIGN_KEY_CHECKS=0;
ALTER TABLE `output_nextusers` ADD COLUMN `active_since` TIMESTAMP NULL DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP  AFTER `name` ,ADD COLUMN `is_active` TINYINT NULL DEFAULT NULL  AFTER `active_since` ,ADD COLUMN `created_by` BIGINT(20) NULL DEFAULT NULL  AFTER `is_active` ,ADD COLUMN `modified_by` BIGINT(20) NULL DEFAULT NULL  AFTER `created_by`, ADD COLUMN `modification_justification` TEXT NULL  AFTER `modified_by` ,  
  ADD CONSTRAINT `fk_nextusers_created_by`
  FOREIGN KEY (`created_by` )
  REFERENCES `users` (`id` )
  ON DELETE NO ACTION
  ON UPDATE NO ACTION, 
  ADD CONSTRAINT `fk_nextusers_modified_by`
  FOREIGN KEY (`modified_by` )
  REFERENCES `users` (`id` )
  ON DELETE NO ACTION
  ON UPDATE NO ACTION
, ADD INDEX `fk_nextusers_created_by` (`created_by` ASC) 
, ADD INDEX `fk_nextusers_modified_by` (`modified_by` ASC) ;

ALTER TABLE `output_nextsubuser` ADD COLUMN `active_since` TIMESTAMP NULL DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP  AFTER `name` ,ADD COLUMN `is_active` TINYINT NULL DEFAULT NULL  AFTER `active_since` ,ADD COLUMN `created_by` BIGINT(20) NULL DEFAULT NULL  AFTER `is_active` ,ADD COLUMN `modified_by` BIGINT(20) NULL DEFAULT NULL  AFTER `created_by`, ADD COLUMN `modification_justification` TEXT NULL  AFTER `modified_by` ,  
  ADD CONSTRAINT `fk_nextsubuser_created_by`
  FOREIGN KEY (`created_by` )
  REFERENCES `users` (`id` )
  ON DELETE NO ACTION
  ON UPDATE NO ACTION, 
  ADD CONSTRAINT `fk_nextsubuser_modified_by`
  FOREIGN KEY (`modified_by` )
  REFERENCES `users` (`id` )
  ON DELETE NO ACTION
  ON UPDATE NO ACTION
, ADD INDEX `fk_nextsubuser_created_by` (`created_by` ASC) 
, ADD INDEX `fk_nextsubuser_modified_by` (`modified_by` ASC) ;

ALTER TABLE `research_output_nextsubusers` ADD COLUMN `active_since` TIMESTAMP NULL DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP  AFTER `nextsubuser_id` ,ADD COLUMN `is_active` TINYINT NULL DEFAULT NULL  AFTER `active_since` ,ADD COLUMN `created_by` BIGINT(20) NULL DEFAULT NULL  AFTER `is_active` ,ADD COLUMN `modified_by` BIGINT(20) NULL DEFAULT NULL  AFTER `created_by`, ADD COLUMN `modification_justification` TEXT NULL  AFTER `modified_by` ,  
  ADD CONSTRAINT `fk_outputnextsubusers_created_by`
  FOREIGN KEY (`created_by` )
  REFERENCES `users` (`id` )
  ON DELETE NO ACTION
  ON UPDATE NO ACTION, 
  ADD CONSTRAINT `fk_outputnextsubusers_modified_by`
  FOREIGN KEY (`modified_by` )
  REFERENCES `users` (`id` )
  ON DELETE NO ACTION
  ON UPDATE NO ACTION
, ADD INDEX `fk_outputnextsubusers_created_by` (`created_by` ASC) 
, ADD INDEX `fk_outputnextsubusers_modified_by` (`modified_by` ASC) ;

ALTER TABLE `research_output_nextusers` ADD COLUMN `active_since` TIMESTAMP NULL DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP  AFTER `nextuser_id` ,ADD COLUMN `is_active` TINYINT NULL DEFAULT NULL  AFTER `active_since` ,ADD COLUMN `created_by` BIGINT(20) NULL DEFAULT NULL  AFTER `is_active` ,ADD COLUMN `modified_by` BIGINT(20) NULL DEFAULT NULL  AFTER `created_by`, ADD COLUMN `modification_justification` TEXT NULL  AFTER `modified_by` ,  
  ADD CONSTRAINT `fk_outputnextusers_created_by`
  FOREIGN KEY (`created_by` )
  REFERENCES `users` (`id` )
  ON DELETE NO ACTION
  ON UPDATE NO ACTION, 
  ADD CONSTRAINT `fk_outputnextusers_modified_by`
  FOREIGN KEY (`modified_by` )
  REFERENCES `users` (`id` )
  ON DELETE NO ACTION
  ON UPDATE NO ACTION
, ADD INDEX `fk_outputnextusers_created_by` (`created_by` ASC) 
, ADD INDEX `fk_outputnextusers_modified_by` (`modified_by` ASC) ;