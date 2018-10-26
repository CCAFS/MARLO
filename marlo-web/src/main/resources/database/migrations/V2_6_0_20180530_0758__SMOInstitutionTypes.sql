SET FOREIGN_KEY_CHECKS=0;

-- ----------------------------
-- Change Table structure for institution_types
-- ----------------------------
ALTER TABLE `institution_types`
ADD COLUMN `rep_ind_organization_type_id`  bigint(20) NULL COMMENT 'SMO Types' AFTER `description`,
ADD INDEX `idx_institution_types_id` (`id`) USING BTREE ,
ADD INDEX `idx_institution_types_rep_ind_organization_type_id` (`rep_ind_organization_type_id`) USING BTREE ;

ALTER TABLE `institution_types` ADD CONSTRAINT `institution_types_ibfk_1` FOREIGN KEY (`rep_ind_organization_type_id`)
REFERENCES `rep_ind_organization_types` (`id`) ON DELETE CASCADE ON UPDATE CASCADE;

-- ----------------------------
-- Add SMO Types for institution_types
-- ----------------------------
UPDATE `institution_types` SET `rep_ind_organization_type_id`='1' WHERE (`id`='3');
UPDATE `institution_types` SET `rep_ind_organization_type_id`='3' WHERE (`id`='11');
UPDATE `institution_types` SET `rep_ind_organization_type_id`='11' WHERE (`id`='18');
UPDATE `institution_types` SET `rep_ind_organization_type_id`='4' WHERE (`id`='19');
UPDATE `institution_types` SET `rep_ind_organization_type_id`='3' WHERE (`id`='20');
UPDATE `institution_types` SET `rep_ind_organization_type_id`='9' WHERE (`id`='21');
UPDATE `institution_types` SET `rep_ind_organization_type_id`='5' WHERE (`id`='23');
UPDATE `institution_types` SET `rep_ind_organization_type_id`='7' WHERE (`id`='24');
UPDATE `institution_types` SET `rep_ind_organization_type_id`='8' WHERE (`id`='25');
UPDATE `institution_types` SET `rep_ind_organization_type_id`='7' WHERE (`id`='26');
UPDATE `institution_types` SET `rep_ind_organization_type_id`='3' WHERE (`id`='27');
UPDATE `institution_types` SET `rep_ind_organization_type_id`='2' WHERE (`id`='28');
UPDATE `institution_types` SET `rep_ind_organization_type_id`='7' WHERE (`id`='29');
UPDATE `institution_types` SET `rep_ind_organization_type_id`='3' WHERE (`id`='30');
UPDATE `institution_types` SET `rep_ind_organization_type_id`='2' WHERE (`id`='31');
UPDATE `institution_types` SET `rep_ind_organization_type_id`='3' WHERE (`id`='32');
UPDATE `institution_types` SET `rep_ind_organization_type_id`='6' WHERE (`id`='34');
UPDATE `institution_types` SET `rep_ind_organization_type_id`='2' WHERE (`id`='36');
