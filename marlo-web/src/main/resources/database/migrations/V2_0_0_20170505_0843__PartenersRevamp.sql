CREATE TABLE `institutions_locations` (
`id`  bigint(20) NOT NULL AUTO_INCREMENT ,
`institution_id`  bigint(20) NOT NULL ,
`loc_element_id`  bigint(20) NOT NULL ,
`is_headquater`  tinyint(1) NOT NULL ,
PRIMARY KEY (`id`),
FOREIGN KEY (`institution_id`) REFERENCES `institutions` (`id`),
FOREIGN KEY (`loc_element_id`) REFERENCES `loc_elements` (`id`)
)ENGINE=InnoDB
;

UPDATE `institutions` SET `country_id`='47' WHERE (`id`='1668');



INSERT into institutions_locations(institution_id,loc_element_id,is_headquater)
select  id,country_id,1 from institutions where headquarter is null and country_id is not  null ;

INSERT into institutions_locations(institution_id,loc_element_id,is_headquater)
select  headquarter,country_id,0 from institutions where headquarter is not  null and country_id is not  null ;




CREATE TABLE `project_partner_locations` (
`id`  bigint(20) NOT NULL AUTO_INCREMENT ,
`project_partner_id`  bigint(20) NOT NULL ,
`institution_loc_id`  bigint(20) NOT NULL ,
`is_active`  tinyint(1) NOT NULL DEFAULT 1 ,
`active_since`  timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ,
`created_by`  bigint(20) NOT NULL ,
`modified_by`  bigint(20) NOT NULL ,
`modification_justification`  text CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL ,
PRIMARY KEY (`id`),
FOREIGN KEY (`project_partner_id`) REFERENCES `project_partners` (`id`),
FOREIGN KEY (`institution_loc_id`) REFERENCES `institutions_locations` (`id`)
)ENGINE=InnoDB
;


insert into project_partner_locations(project_partner_id,institution_loc_id,is_active,active_since,created_by,modified_by,modification_justification)

select project_partner_id,loc.id,1,now(),3,3,'' from project_partner_persons ppp
INNER JOIN institutions inst on ppp.institution_id=inst.id
INNER JOIN institutions_locations loc on inst.id=loc.institution_id
where inst.headquarter is null  and loc.is_headquater=1 and ppp.is_active=1;


insert into project_partner_locations(project_partner_id,institution_loc_id,is_active,active_since,created_by,modified_by,modification_justification)

select project_partner_id,loc.id,1,now(),3,3,'' from project_partner_persons ppp
INNER JOIN institutions inst on ppp.institution_id=inst.id
INNER JOIN institutions_locations loc on inst.headquarter=loc.institution_id and loc.loc_element_id=inst.country_id
where inst.headquarter is not null   and loc.is_headquater=1 and ppp.is_active=1;



ALTER TABLE `institutions` DROP FOREIGN KEY `institutions_ibfk_2`;

ALTER TABLE `institutions` DROP FOREIGN KEY `institutions_ibfk_4`;

ALTER TABLE `institutions`
DROP COLUMN `country_id`,
DROP COLUMN `headquarter`;

ALTER TABLE `project_partner_persons` DROP FOREIGN KEY `project_partner_persons_ibfk_5`;

ALTER TABLE `project_partner_persons`
DROP COLUMN `institution_id`;

