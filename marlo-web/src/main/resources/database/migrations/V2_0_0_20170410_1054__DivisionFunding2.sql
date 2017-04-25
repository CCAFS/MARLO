UPDATE `crp_parameters` SET `value`='1' WHERE (`crp_id`=5 and `key`='crp_division_fs' );
ALTER TABLE `deliverables`
ADD COLUMN `division`  varchar(1000) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL AFTER `crp_id`;

