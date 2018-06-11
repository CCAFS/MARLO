#Fix broken index on auditlog table
DROP PROCEDURE IF EXISTS `fix_broken_index_on_audit_log_table`;
DELIMITER ;;
CREATE PROCEDURE `fix_broken_index_on_audit_log_table`() 
BEGIN 
IF (SELECT EXISTS (
SELECT * FROM information_schema.COLUMNS WHERE column_name='entity_id' AND table_name='auditlog' 
AND DATA_TYPE = 'varchar' AND table_schema = DATABASE())) = 1
THEN
  ALTER TABLE `auditlog` DROP INDEX `ENTITY_ID`;  
  ALTER TABLE `auditlog` MODIFY COLUMN `entity_id` bigint(20) NOT NULL;
  CREATE INDEX `ENTITY_ID` ON `auditlog`(`entity_id`);
END IF; 
END ;;
DELIMITER ;
CALL fix_broken_index_on_audit_log_table();
DROP PROCEDURE `fix_broken_index_on_audit_log_table`;