#Rename column creator to is_active
DROP PROCEDURE IF EXISTS `rename_column_case_study_projects`;

DELIMITER ;;
CREATE PROCEDURE `rename_column_case_study_projects`() 
BEGIN 
IF (SELECT EXISTS (
    SELECT * FROM information_schema.COLUMNS WHERE column_name='creator' AND table_name='case_study_projects' AND table_schema = DATABASE()))  = 1
  THEN ALTER TABLE `case_study_projects`
    CHANGE COLUMN `creator` `is_active` TINYINT(1) DEFAULT 1 NOT NULL;
END IF; 
END ;;
DELIMITER ;
CALL rename_column_case_study_projects();
DROP PROCEDURE `rename_column_case_study_projects`;


#Create new column is_active
DROP PROCEDURE IF EXISTS `add_column_to_case_studie_indicators`;
DELIMITER ;;
CREATE PROCEDURE `add_column_to_case_studie_indicators`() 
BEGIN 
IF (SELECT EXISTS (
SELECT * FROM information_schema.COLUMNS WHERE column_name='is_active' AND table_name='case_studie_indicators' AND table_schema = DATABASE())) = 0
THEN
    ALTER TABLE `case_studie_indicators`
    ADD COLUMN `is_active` TINYINT(1)  DEFAULT 1 NOT NULL;
END IF; 
END ;;
DELIMITER ;
CALL add_column_to_case_studie_indicators();
DROP PROCEDURE `add_column_to_case_studie_indicators`;



