ALTER TABLE `crp_cluster_key_outputs`
MODIFY COLUMN `composed_id`  varchar(40) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL AFTER `modification_justification`;