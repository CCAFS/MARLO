/* Add crp_program */
ALTER TABLE `deliverable_programs` 
ADD COLUMN `crp_program` bigint(20) NULL AFTER `deliverable_id`,
ADD INDEX `crp_program`(`crp_program`) USING BTREE,
ADD CONSTRAINT `deliverable_programs_ibfk_4` FOREIGN KEY (`crp_program`) 
REFERENCES `crp_programs` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT;

/* IP Program -> Crp Program*/
UPDATE deliverable_programs dp
INNER JOIN ip_programs ip ON ip.id = dp.ip_program_id
INNER JOIN crp_programs cp ON cp.id = ip.crp_program_id
SET dp.crp_program = cp.id;

/* Add location global to deliverables */ 
ALTER TABLE `deliverables_info` 
ADD COLUMN `is_location_global` tinyint(1) NULL DEFAULT NULL AFTER `cross_cutting_score_capacity`;

/* Set global deliverables */
UPDATE deliverables_info di
INNER JOIN deliverable_programs dp ON dp.deliverable_id = di.deliverable_id AND dp.ip_program_id=11
SET di.is_location_global = 1;

/* Delete Global deliverable_programs*/
DELETE FROM deliverable_programs WHERE ip_program_id = 11;

/* Delete ip_program */
ALTER TABLE `deliverable_programs` DROP FOREIGN KEY `deliverable_programs_ibfk_2`;

ALTER TABLE `deliverable_programs` 
DROP COLUMN `ip_program_id`,
MODIFY COLUMN `crp_program` bigint(20) NOT NULL AFTER `deliverable_id`,
DROP INDEX `ip_program_id`;