CREATE TABLE `parameters` (
`id`  bigint(20) NOT NULL AUTO_INCREMENT ,
`key`  varchar(500) NULL ,
`description`  text  NULL ,
`format`  int NULL ,
`default_value`  varchar(500) NULL ,
`category`  int NULL ,
PRIMARY KEY (`id`)
)ENGINE=InnoDB
;

CREATE TABLE `custom_parameters` (
`id`  bigint(20) NOT NULL AUTO_INCREMENT ,
`parameter_id`  bigint(20) NOT NULL ,
`crp_id`  bigint(20) NOT NULL ,
`value`  varchar(500)  NULL ,
`created_by`  bigint(20) NULL DEFAULT NULL COMMENT 'foreign key to the table users' ,
`is_active`  tinyint(1) NOT NULL DEFAULT 1 ,
`active_since`  timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ,
`modified_by`  bigint(20) NOT NULL ,
`modification_justification`  text CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL ,
PRIMARY KEY (`id`),
FOREIGN KEY (`parameter_id`) REFERENCES `parameters` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT,
FOREIGN KEY (`crp_id`) REFERENCES `crps` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT,
FOREIGN KEY (`created_by`) REFERENCES `users` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT,
FOREIGN KEY (`modified_by`) REFERENCES `users` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT
)ENGINE=InnoDB
;



insert into parameters(`key`,description,format,default_value,category)

select DISTINCT `key`,description,null,null,
case `type`
when 1 then 3 -- rol 
when 2 then 1 -- true or false
when 3 then 4 --  1 or 0 
when 4 then 2 -- other 
end 
 
from crp_parameters;

insert into custom_parameters(parameter_id,crp_id,`value`,`created_by`,is_active,active_since,modified_by,modification_justification)
SELECT pa.id,cp.crp_id,cp.`value`,3,1,now(),3,'' FROM crp_parameters cp INNER JOIN parameters pa on pa.key=cp.key ;





