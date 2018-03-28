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

