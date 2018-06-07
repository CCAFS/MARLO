#Create new column modification_justification in projects table
DROP PROCEDURE IF EXISTS `add_modification_justification_column_to_projects`;
DELIMITER ;;
CREATE PROCEDURE `add_modification_justification_column_to_projects`() 
BEGIN 
IF (SELECT EXISTS (
SELECT * FROM information_schema.COLUMNS WHERE column_name='modification_justification' AND table_name='projects' AND table_schema = DATABASE())) = 0
THEN
    ALTER TABLE `projects`
    ADD COLUMN `modification_justification` text NOT NULL;
END IF; 
END ;;
DELIMITER ;
CALL add_modification_justification_column_to_projects();
DROP PROCEDURE `add_modification_justification_column_to_projects`;

#Create new column created_by in projects_info table
DROP PROCEDURE IF EXISTS `add_created_by_column_to_projects_info`;
DELIMITER ;;
CREATE PROCEDURE `add_created_by_column_to_projects_info`() 
BEGIN 
IF (SELECT EXISTS (
SELECT * FROM information_schema.COLUMNS WHERE column_name='created_by' AND table_name='projects_info' AND table_schema = DATABASE())) = 0
THEN
    ALTER TABLE `projects_info`
    ADD COLUMN `created_by` bigint(20) ;
END IF; 
END ;;
DELIMITER ;
CALL add_created_by_column_to_projects_info();
DROP PROCEDURE `add_created_by_column_to_projects_info`;

#Create new column is_active to projects_info table
DROP PROCEDURE IF EXISTS `add_is_active_column_to_projects_info`;
DELIMITER ;;
CREATE PROCEDURE `add_is_active_column_to_projects_info`() 
BEGIN 
IF (SELECT EXISTS (
SELECT * FROM information_schema.COLUMNS WHERE column_name='is_active' AND table_name='projects_info' AND table_schema = DATABASE())) = 0
THEN
    ALTER TABLE `projects_info`
    ADD COLUMN `is_active` TINYINT(1)  DEFAULT 1 NOT NULL;
END IF; 
END ;;
DELIMITER ;
CALL add_is_active_column_to_projects_info();
DROP PROCEDURE `add_is_active_column_to_projects_info`;

#Create new column active_since in projects_info table
DROP PROCEDURE IF EXISTS `add_active_since_column_to_projects_info`;
DELIMITER ;;
CREATE PROCEDURE `add_active_since_column_to_projects_info`() 
BEGIN 
IF (SELECT EXISTS (
SELECT * FROM information_schema.COLUMNS WHERE column_name='active_since' AND table_name='projects_info' AND table_schema = DATABASE())) = 0
THEN
    ALTER TABLE `projects_info`
    ADD COLUMN `active_since` timestamp DEFAULT CURRENT_TIMESTAMP NOT NULL;
END IF; 
END ;;
DELIMITER ;
CALL add_active_since_column_to_projects_info();
DROP PROCEDURE `add_active_since_column_to_projects_info`;

