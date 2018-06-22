#Create new column created_by in ip_programs table
DROP PROCEDURE IF EXISTS `add_created_by_column_to_ip_programs`;
DELIMITER ;;
CREATE PROCEDURE `add_created_by_column_to_ip_programs`() 
BEGIN 
IF (SELECT EXISTS (
SELECT * FROM information_schema.COLUMNS WHERE column_name='created_by' AND table_name='ip_programs' AND table_schema = DATABASE())) = 0
THEN
    ALTER TABLE `ip_programs`
    ADD COLUMN `created_by` bigint(20) ;
END IF; 
END ;;
DELIMITER ;
CALL add_created_by_column_to_ip_programs();
DROP PROCEDURE `add_created_by_column_to_ip_programs`;

#Create new column is_active to ip_programs table
DROP PROCEDURE IF EXISTS `add_is_active_column_to_ip_programs`;
DELIMITER ;;
CREATE PROCEDURE `add_is_active_column_to_ip_programs`() 
BEGIN 
IF (SELECT EXISTS (
SELECT * FROM information_schema.COLUMNS WHERE column_name='is_active' AND table_name='ip_programs' AND table_schema = DATABASE())) = 0
THEN
    ALTER TABLE `ip_programs`
    ADD COLUMN `is_active` TINYINT(1)  DEFAULT 1 NOT NULL;
END IF; 
END ;;
DELIMITER ;
CALL add_is_active_column_to_ip_programs();
DROP PROCEDURE `add_is_active_column_to_ip_programs`;

#Create new column modification_justification in ip_programs table
DROP PROCEDURE IF EXISTS `add_modification_justification_column_to_ip_programs`;
DELIMITER ;;
CREATE PROCEDURE `add_modification_justification_column_to_ip_programs`() 
BEGIN 
IF (SELECT EXISTS (
SELECT * FROM information_schema.COLUMNS WHERE column_name='modification_justification' AND table_name='ip_programs' AND table_schema = DATABASE())) = 0
THEN
    ALTER TABLE `ip_programs`
    ADD COLUMN `modification_justification` text NOT NULL;
END IF; 
END ;;
DELIMITER ;
CALL add_modification_justification_column_to_ip_programs();
DROP PROCEDURE `add_modification_justification_column_to_ip_programs`;


