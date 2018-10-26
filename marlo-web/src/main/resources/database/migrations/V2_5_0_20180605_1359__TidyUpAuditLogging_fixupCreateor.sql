#Rename column is_active back to creator.
DROP PROCEDURE IF EXISTS `rename_column_case_study_projects_2`;

DELIMITER ;;
CREATE PROCEDURE `rename_column_case_study_projects_2`() 
BEGIN 
IF (SELECT EXISTS (
    SELECT * FROM information_schema.COLUMNS WHERE column_name='creator' 
    AND table_name='case_study_projects' AND table_schema = DATABASE()))  = 0
  THEN ALTER TABLE `case_study_projects`
    CHANGE COLUMN `is_active` `creator` TINYINT(1) DEFAULT 1 NOT NULL;
END IF; 
END ;;
DELIMITER ;
CALL rename_column_case_study_projects_2();
DROP PROCEDURE `rename_column_case_study_projects_2`;

