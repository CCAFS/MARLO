#Create new column is_active
DROP PROCEDURE IF EXISTS `add_column_to_countries_agreement`;
DELIMITER ;;
CREATE PROCEDURE `add_column_to_countries_agreement`() 
BEGIN 
IF (SELECT EXISTS (
SELECT * FROM information_schema.COLUMNS WHERE column_name='is_active' AND table_name='countries_agreement' AND table_schema = DATABASE())) = 0
THEN
    ALTER TABLE `countries_agreement`
    ADD COLUMN `is_active` TINYINT(1)  DEFAULT 1 NOT NULL;
END IF; 
END ;;
DELIMITER ;
CALL add_column_to_countries_agreement();
DROP PROCEDURE `add_column_to_countries_agreement`;

#Made some changes to the entity structure so no longer need the active column.
ALTER TABLE `countries_agreement` DROP COLUMN `is_active`;


#Create new column modified_by in deliverables table
DROP PROCEDURE IF EXISTS `add_modified_by_column_to_deliverables`;
DELIMITER ;;
CREATE PROCEDURE `add_modified_by_column_to_deliverables`() 
BEGIN 
IF (SELECT EXISTS (
SELECT * FROM information_schema.COLUMNS WHERE column_name='modified_by' AND table_name='deliverables' AND table_schema = DATABASE())) = 0
THEN
    ALTER TABLE `deliverables`
    ADD COLUMN `modified_by` bigint(20) ;
END IF; 
END ;;
DELIMITER ;
CALL add_modified_by_column_to_deliverables();
DROP PROCEDURE `add_modified_by_column_to_deliverables`;

#Create new column modification_justification in deliverables table
DROP PROCEDURE IF EXISTS `add_modification_justification_column_to_deliverables`;
DELIMITER ;;
CREATE PROCEDURE `add_modification_justification_column_to_deliverables`() 
BEGIN 
IF (SELECT EXISTS (
SELECT * FROM information_schema.COLUMNS WHERE column_name='modification_justification' AND table_name='deliverables' AND table_schema = DATABASE())) = 0
THEN
    ALTER TABLE `deliverables`
    ADD COLUMN `modification_justification` text ;
END IF; 
END ;;
DELIMITER ;
CALL add_modification_justification_column_to_deliverables();
DROP PROCEDURE `add_modification_justification_column_to_deliverables`;

#Create new column created_by in deliverables_info table
DROP PROCEDURE IF EXISTS `add_created_by_column_to_deliverables_info`;
DELIMITER ;;
CREATE PROCEDURE `add_created_by_column_to_deliverables_info`() 
BEGIN 
IF (SELECT EXISTS (
SELECT * FROM information_schema.COLUMNS WHERE column_name='created_by' AND table_name='deliverables_info' AND table_schema = DATABASE())) = 0
THEN
    ALTER TABLE `deliverables_info`
    ADD COLUMN `created_by` bigint(20) ;
END IF; 
END ;;
DELIMITER ;
CALL add_created_by_column_to_deliverables_info();
DROP PROCEDURE `add_created_by_column_to_deliverables_info`;

#Create new column active_since in deliverables_info table
DROP PROCEDURE IF EXISTS `add_active_since_column_to_deliverables_info`;
DELIMITER ;;
CREATE PROCEDURE `add_active_since_column_to_deliverables_info`() 
BEGIN 
IF (SELECT EXISTS (
SELECT * FROM information_schema.COLUMNS WHERE column_name='active_since' AND table_name='deliverables_info' AND table_schema = DATABASE())) = 0
THEN
    ALTER TABLE `deliverables_info`
    ADD COLUMN `active_since` timestamp DEFAULT CURRENT_TIMESTAMP NOT NULL;
END IF; 
END ;;
DELIMITER ;
CALL add_active_since_column_to_deliverables_info();
DROP PROCEDURE `add_active_since_column_to_deliverables_info`;

#Create new column is_active to deliverables_info table
DROP PROCEDURE IF EXISTS `add_is_active_column_to_deliverables_info`;
DELIMITER ;;
CREATE PROCEDURE `add_is_active_column_to_deliverables_info`() 
BEGIN 
IF (SELECT EXISTS (
SELECT * FROM information_schema.COLUMNS WHERE column_name='is_active' AND table_name='deliverables_info' AND table_schema = DATABASE())) = 0
THEN
    ALTER TABLE `deliverables_info`
    ADD COLUMN `is_active` TINYINT(1)  DEFAULT 1 NOT NULL;
END IF; 
END ;;
DELIMITER ;
CALL add_is_active_column_to_deliverables_info();
DROP PROCEDURE `add_is_active_column_to_deliverables_info`;
