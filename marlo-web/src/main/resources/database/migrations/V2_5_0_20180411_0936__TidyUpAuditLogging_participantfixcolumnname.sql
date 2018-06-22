#modify column acive_since to active_since in participant table
DROP PROCEDURE IF EXISTS `modify_acive_since_column_in_participant`;
DELIMITER ;;
CREATE PROCEDURE `modify_acive_since_column_in_participant`() 
BEGIN 
IF (SELECT EXISTS (
SELECT * FROM information_schema.COLUMNS WHERE column_name='active_since' AND table_name='participant' AND table_schema = DATABASE())) = 0
THEN
    ALTER TABLE `participant`
    CHANGE `acive_since` `active_since` timestamp DEFAULT CURRENT_TIMESTAMP NOT NULL;
END IF; 
END ;;
DELIMITER ;
CALL modify_acive_since_column_in_participant();
DROP PROCEDURE `modify_acive_since_column_in_participant`;
