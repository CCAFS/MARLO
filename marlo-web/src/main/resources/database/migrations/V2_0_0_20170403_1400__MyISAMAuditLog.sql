CREATE TABLE `auditlog_temp` (
`AUDIT_LOG_ID`  bigint(20) UNSIGNED NOT NULL AUTO_INCREMENT ,
`ACTION`  varchar(100) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL ,
`DETAIL`  text CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL ,
`CREATED_DATE`  timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ,
`ENTITY_ID`  varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL ,
`ENTITY_NAME`  varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL ,
`Entity_json`  text CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL ,
`user_id`  bigint(20) NOT NULL ,
`main`  bigint(20) NULL DEFAULT NULL ,
`transaction_id`  text CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL ,
`relation_name`  varchar(500) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL ,
`modification_justification`  text CHARACTER SET utf8 COLLATE utf8_general_ci NULL ,
PRIMARY KEY (`AUDIT_LOG_ID`)
)ENGINE = MyISAM ;



insert into auditlog_temp
select * from auditlog
;



drop table auditlog;


RENAME TABLE `auditlog_temp` TO `auditlog`;