ALTER TABLE `clarisa_monitoring`
CHANGE COLUMN `service_args` `service_url`  text CHARACTER SET utf8 COLLATE utf8_general_ci NULL AFTER `service_name`,
ADD COLUMN `user_ip`  text NULL AFTER `user_id`;