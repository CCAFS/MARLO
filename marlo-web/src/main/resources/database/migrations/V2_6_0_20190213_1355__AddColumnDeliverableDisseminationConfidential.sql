ALTER TABLE `deliverable_dissemination`
ADD COLUMN `confidential`  tinyint(1) NULL DEFAULT NULL AFTER `synced`,
ADD COLUMN `confidential_url`  varchar(500) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL AFTER `confidential`;