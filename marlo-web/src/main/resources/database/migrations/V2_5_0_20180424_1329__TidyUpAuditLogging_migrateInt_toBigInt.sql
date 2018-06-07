#Change metadata_elements id column to use bigint instead of int
DROP PROCEDURE IF EXISTS `modify_id_field_metadata_elements`;
DELIMITER ;;
CREATE PROCEDURE `modify_id_field_metadata_elements`() 
BEGIN 
IF (SELECT EXISTS (
SELECT * FROM information_schema.COLUMNS WHERE column_name='id' AND table_name='metadata_elements' 
AND data_type = 'int' AND table_schema = DATABASE())) = 1
THEN
  ALTER TABLE `deliverable_metadata_elements` DROP FOREIGN KEY `deliverable_metadata_elements_ibfk_2`;  
  ALTER TABLE `metadata_elements` MODIFY `id` bigint(20) NOT NULL AUTO_INCREMENT;
  ALTER TABLE `deliverable_metadata_elements` MODIFY `element_id` bigint(20);
  ALTER TABLE `deliverable_metadata_elements` ADD CONSTRAINT `FK_deliverable_metadata_elements_metadata_elements` FOREIGN KEY (`element_id`) REFERENCES `metadata_elements`(`id`);
END IF; 
END ;;
DELIMITER ;
CALL modify_id_field_metadata_elements();
DROP PROCEDURE `modify_id_field_metadata_elements`;


#Change deliverable_metadata_elements id column to use bigint instead of int
DROP PROCEDURE IF EXISTS `modify_id_field_deliverable_metadata_elements`;
DELIMITER ;;
CREATE PROCEDURE `modify_id_field_deliverable_metadata_elements`() 
BEGIN 
IF (SELECT EXISTS (
SELECT * FROM information_schema.COLUMNS WHERE column_name='id' AND table_name='deliverable_metadata_elements' 
AND data_type = 'int' AND table_schema = DATABASE())) = 1
THEN
  ALTER TABLE `deliverable_metadata_elements` MODIFY `id` bigint(20) NOT NULL AUTO_INCREMENT;
END IF; 
END ;;
DELIMITER ;
CALL modify_id_field_deliverable_metadata_elements();
DROP PROCEDURE `modify_id_field_deliverable_metadata_elements`;


#Change deliverable_data_sharing id column to use bigint instead of int
DROP PROCEDURE IF EXISTS `modify_id_field_deliverable_data_sharing`;
DELIMITER ;;
CREATE PROCEDURE `modify_id_field_deliverable_data_sharing`() 
BEGIN 
IF (SELECT EXISTS (
SELECT * FROM information_schema.COLUMNS WHERE column_name='id' AND table_name='deliverable_data_sharing' 
AND data_type = 'int' AND table_schema = DATABASE())) = 1
THEN
  ALTER TABLE `deliverable_data_sharing` MODIFY `id` bigint(20) NOT NULL AUTO_INCREMENT;
  ALTER TABLE `deliverable_data_sharing_file` MODIFY `id` bigint(20) NOT NULL AUTO_INCREMENT;
  ALTER TABLE `deliverable_dissemination` MODIFY `id` bigint(20) NOT NULL AUTO_INCREMENT;

END IF; 
END ;;
DELIMITER ;
CALL modify_id_field_deliverable_data_sharing();
DROP PROCEDURE `modify_id_field_deliverable_data_sharing`;

#Change ip_program_element_relation_types id column to use bigint instead of int
DROP PROCEDURE IF EXISTS `modify_id_field_ip_program_element_relation_types`;
DELIMITER ;;
CREATE PROCEDURE `modify_id_field_ip_program_element_relation_types`() 
BEGIN 
IF (SELECT EXISTS (
SELECT * FROM information_schema.COLUMNS WHERE column_name='id' AND table_name='ip_program_element_relation_types' 
AND data_type = 'int' AND table_schema = DATABASE())) = 1
THEN
  ALTER TABLE `ip_program_elements` DROP FOREIGN KEY `ip_program_elements_ibfk_2`; 
  ALTER TABLE `ip_program_element_relation_types` MODIFY `id` bigint(20) NOT NULL AUTO_INCREMENT;
  ALTER TABLE `other_contributions` MODIFY `id` bigint(20) NOT NULL AUTO_INCREMENT;
  ALTER TABLE `project_highlights_country` MODIFY `id` bigint(20) NOT NULL AUTO_INCREMENT;
  ALTER TABLE `project_highlights_types` MODIFY `id` bigint(20) NOT NULL AUTO_INCREMENT;
  ALTER TABLE `ip_program_elements` MODIFY `relation_type_id` bigint(20);
  ALTER TABLE `ip_program_elements` ADD CONSTRAINT `FK_ip_program_elements_ip_program_element_relation_types` FOREIGN KEY (`relation_type_id`) REFERENCES `ip_program_element_relation_types`(`id`);

END IF; 
END ;;
DELIMITER ;
CALL modify_id_field_ip_program_element_relation_types();
DROP PROCEDURE `modify_id_field_ip_program_element_relation_types`;


