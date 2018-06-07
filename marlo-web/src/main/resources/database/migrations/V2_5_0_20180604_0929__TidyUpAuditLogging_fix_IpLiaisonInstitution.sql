#Create new column created_by in ip_liaison_institutions table
DROP PROCEDURE IF EXISTS `add_created_by_column_to_ip_liaison_institutions`;
DELIMITER ;;
CREATE PROCEDURE `add_created_by_column_to_ip_liaison_institutions`() 
BEGIN 
IF (SELECT EXISTS (
SELECT * FROM information_schema.COLUMNS WHERE column_name='created_by' AND table_name='ip_liaison_institutions' AND table_schema = DATABASE())) = 0
THEN
    ALTER TABLE `ip_liaison_institutions` ADD COLUMN `created_by` bigint(20) DEFAULT 3;
    ALTER TABLE `ip_liaison_institutions` ADD CONSTRAINT `FK_ip_liaison_institutions_users_created_by` 
      FOREIGN KEY (`created_by`) REFERENCES `users`(`id`) ON DELETE RESTRICT ON UPDATE CASCADE;
END IF; 
END ;;
DELIMITER ;
CALL add_created_by_column_to_ip_liaison_institutions();
DROP PROCEDURE `add_created_by_column_to_ip_liaison_institutions`;