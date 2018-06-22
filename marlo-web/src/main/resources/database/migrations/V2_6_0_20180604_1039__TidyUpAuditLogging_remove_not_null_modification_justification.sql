DROP PROCEDURE IF EXISTS `remove_not_nullable_from_modification_justification_columns`;
DELIMITER ;;
CREATE PROCEDURE `remove_not_nullable_from_modification_justification_columns`()
BEGIN
	
DECLARE done BIT default false;
DECLARE tname CHAR(255);

DECLARE cur1 CURSOR for SELECT `TABLE_NAME` FROM INFORMATION_SCHEMA.COLUMNS 
  WHERE COLUMN_NAME = 'modification_justification' AND IS_NULLABLE = 'NO' AND TABLE_SCHEMA = DATABASE();
  DECLARE CONTINUE HANDLER FOR NOT FOUND SET done = TRUE;

IF (
SELECT COUNT(`TABLE_NAME`) FROM INFORMATION_SCHEMA.COLUMNS 
        WHERE COLUMN_NAME = 'modification_justification' 
        AND IS_NULLABLE = 'NO' 
        AND table_schema = DATABASE()) > 0
THEN
    
    open cur1;

    myloop: LOOP
        FETCH cur1 INTO tname;
        IF done THEN
            LEAVE myloop;
        END IF;
        SET @sql = CONCAT('ALTER TABLE ', DATABASE(), '.', tname, ' MODIFY COLUMN `modification_justification` text');
        PREPARE stmt FROM @sql;
        EXECUTE stmt;
        DROP PREPARE stmt;
    END LOOP;

    CLOSE cur1;
END IF;
END ;;
DELIMITER ;

CALL remove_not_nullable_from_modification_justification_columns();

DROP PROCEDURE `remove_not_nullable_from_modification_justification_columns`;