#Create new column created_by in funding_sources_info table
DROP PROCEDURE IF EXISTS `add_created_by_column_to_funding_sources_info`;
DELIMITER ;;
CREATE PROCEDURE `add_created_by_column_to_funding_sources_info`() 
BEGIN 
IF (SELECT EXISTS (
SELECT * FROM information_schema.COLUMNS WHERE column_name='created_by' AND table_name='funding_sources_info' AND table_schema = DATABASE())) = 0
THEN
    ALTER TABLE `funding_sources_info`
    ADD COLUMN `created_by` bigint(20) ;
END IF; 
END ;;
DELIMITER ;
CALL add_created_by_column_to_funding_sources_info();
DROP PROCEDURE `add_created_by_column_to_funding_sources_info`;

#Create new column is_active to funding_sources_info table
DROP PROCEDURE IF EXISTS `add_is_active_column_to_funding_sources_info`;
DELIMITER ;;
CREATE PROCEDURE `add_is_active_column_to_funding_sources_info`() 
BEGIN 
IF (SELECT EXISTS (
SELECT * FROM information_schema.COLUMNS WHERE column_name='is_active' AND table_name='funding_sources_info' AND table_schema = DATABASE())) = 0
THEN
    ALTER TABLE `funding_sources_info`
    ADD COLUMN `is_active` TINYINT(1)  DEFAULT 1 NOT NULL;
END IF; 
END ;;
DELIMITER ;
CALL add_is_active_column_to_funding_sources_info();
DROP PROCEDURE `add_is_active_column_to_funding_sources_info`;

#Create new column active_since in funding_sources_info table
DROP PROCEDURE IF EXISTS `add_active_since_column_to_funding_sources_info`;
DELIMITER ;;
CREATE PROCEDURE `add_active_since_column_to_funding_sources_info`() 
BEGIN 
IF (SELECT EXISTS (
SELECT * FROM information_schema.COLUMNS WHERE column_name='active_since' AND table_name='funding_sources_info' AND table_schema = DATABASE())) = 0
THEN
    ALTER TABLE `funding_sources_info`
    ADD COLUMN `active_since` timestamp DEFAULT CURRENT_TIMESTAMP NOT NULL;
END IF; 
END ;;
DELIMITER ;
CALL add_active_since_column_to_funding_sources_info();
DROP PROCEDURE `add_active_since_column_to_funding_sources_info`;


