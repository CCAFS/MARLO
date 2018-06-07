#Create new column is_active
DROP PROCEDURE IF EXISTS `add_column_to_center_section_statuses`;
DELIMITER ;;
CREATE PROCEDURE `add_column_to_center_section_statuses`() 
BEGIN 
IF (SELECT EXISTS (
SELECT * FROM information_schema.COLUMNS WHERE column_name='is_active' AND table_name='center_section_statuses' AND table_schema = DATABASE())) = 0
THEN
    ALTER TABLE `center_section_statuses`
    ADD COLUMN `is_active` TINYINT(1)  DEFAULT 1 NOT NULL;
END IF; 
END ;;
DELIMITER ;
CALL add_column_to_center_section_statuses();
DROP PROCEDURE `add_column_to_center_section_statuses`;

#Create new column is_active
DROP PROCEDURE IF EXISTS `add_column_to_center_submissions`;
DELIMITER ;;
CREATE PROCEDURE `add_column_to_center_submissions`() 
BEGIN 
IF (SELECT EXISTS (
SELECT * FROM information_schema.COLUMNS WHERE column_name='is_active' AND table_name='center_submissions' AND table_schema = DATABASE())) = 0
THEN
    ALTER TABLE `center_submissions`
    ADD COLUMN `is_active` TINYINT(1)  DEFAULT 1 NOT NULL;
END IF; 
END ;;
DELIMITER ;
CALL add_column_to_center_submissions();
DROP PROCEDURE `add_column_to_center_submissions`;

#Rename column is_acive to is_active
DROP PROCEDURE IF EXISTS `rename_column_center_project_types`;

DELIMITER ;;
CREATE PROCEDURE `rename_column_center_project_types`() 
BEGIN 
IF (SELECT EXISTS (
    SELECT * FROM information_schema.COLUMNS WHERE column_name='is_acive' AND table_name='center_project_types' AND table_schema = DATABASE()))  = 1
  THEN ALTER TABLE `center_project_types`
    CHANGE COLUMN `is_acive` `is_active` TINYINT(1) DEFAULT 1 NOT NULL;
END IF; 
END ;;
DELIMITER ;
CALL rename_column_center_project_types();
DROP PROCEDURE `rename_column_center_project_types`;

