

SET FOREIGN_KEY_CHECKS=0;

-- ----------------------------
-- Table structure for `users`
-- ----------------------------
DROP TABLE IF EXISTS `users`;
CREATE TABLE `users` (
`id`  bigint(20) NOT NULL AUTO_INCREMENT ,
`first_name`  varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL ,
`last_name`  varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL ,
`username`  varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL ,
`email`  varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL ,
`password`  varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL ,
`is_ccafs_user`  tinyint(4) NOT NULL DEFAULT 0 ,
`created_by`  bigint(20) NULL DEFAULT NULL ,
`is_active`  tinyint(4) NOT NULL DEFAULT 1 ,
`last_login`  timestamp NULL DEFAULT CURRENT_TIMESTAMP ,
PRIMARY KEY (`id`),
UNIQUE INDEX `email` (`email`) USING BTREE ,
UNIQUE INDEX `username_UNIQUE` (`username`) USING BTREE ,
INDEX `FK_users_user_id_idx` (`created_by`) USING BTREE 
)
ENGINE=InnoDB
DEFAULT CHARACTER SET=utf8 COLLATE=utf8_general_ci


;
