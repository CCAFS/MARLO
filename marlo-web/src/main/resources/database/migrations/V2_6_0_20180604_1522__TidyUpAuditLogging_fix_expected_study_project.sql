#Create new audit columns for expected_study_projects table
DROP PROCEDURE IF EXISTS `add_audit_columns_to_expected_study_projects`;
DELIMITER ;;
CREATE PROCEDURE `add_audit_columns_to_expected_study_projects`() 
BEGIN 
IF (SELECT EXISTS (
SELECT * FROM information_schema.COLUMNS WHERE column_name='created_by' AND table_name='expected_study_projects' 
AND table_schema = DATABASE())) = 0
THEN
    ALTER TABLE `expected_study_projects` ADD COLUMN `created_by` bigint(20) NOT NULL DEFAULT 3;
    ALTER TABLE `expected_study_projects` ADD COLUMN `active_since` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP;
    ALTER TABLE `expected_study_projects` ADD COLUMN `is_active` tinyint(1) NOT NULL DEFAULT 1;
    ALTER TABLE `expected_study_projects` ADD COLUMN `modified_by` bigint(20);
    ALTER TABLE `expected_study_projects` ADD COLUMN `modification_justification` text;
    
    ALTER TABLE `expected_study_projects` ADD CONSTRAINT `FK_expected_study_projects_users_created_by` FOREIGN KEY (`created_by`) REFERENCES `users`(`id`) ON DELETE RESTRICT ON UPDATE CASCADE;
    ALTER TABLE `expected_study_projects` ADD CONSTRAINT `FK_expected_study_projects_users_modified_by` FOREIGN KEY (`modified_by`) REFERENCES `users`(`id`) ON DELETE RESTRICT ON UPDATE CASCADE;

END IF; 
END ;;
DELIMITER ;
CALL add_audit_columns_to_expected_study_projects();
DROP PROCEDURE `add_audit_columns_to_expected_study_projects`;

#Now remove the DEFAULT from the created_by column
DROP PROCEDURE IF EXISTS `remove_default_from_created_by_column`;
DELIMITER ;;
CREATE PROCEDURE `remove_default_from_created_by_column`() 
BEGIN 
IF (SELECT EXISTS (
SELECT * FROM information_schema.COLUMNS WHERE column_name='created_by' AND table_name='expected_study_projects' 
AND COLUMN_DEFAULT = 3 AND table_schema = DATABASE())) = 1
THEN
    ALTER TABLE `expected_study_projects` MODIFY COLUMN `created_by` bigint(20) NOT NULL;   
END IF; 
END ;;
DELIMITER ;
CALL remove_default_from_created_by_column();
DROP PROCEDURE `remove_default_from_created_by_column`;


