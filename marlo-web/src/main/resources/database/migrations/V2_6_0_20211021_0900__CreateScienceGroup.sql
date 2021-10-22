create table oc_science_group (
id bigint(20) NOT NULL AUTO_INCREMENT,
financial_code varchar(10) not null,
description TEXT null,
parent_id bigint(20),
PRIMARY KEY (id),
INDEX `oc_science_group_id` (`id`) USING BTREE
)ENGINE=InnoDB
DEFAULT CHARACTER SET=utf8 COLLATE=utf8_general_ci;

insert into oc_science_group (financial_code,description,parent_id) values ('S1-01','Research',null);
insert into oc_science_group (financial_code,description,parent_id) values ('S1-02','Non Research',null);
insert into oc_science_group (financial_code,description,parent_id) values ('S2-10','System Transformation',null);
insert into oc_science_group (financial_code,description,parent_id) values ('S2-11','Resilient Agri -food',null);
insert into oc_science_group (financial_code,description,parent_id) values ('S2-12','Genetic Innovation',null);
insert into oc_science_group (financial_code,description,parent_id) values ('S2-20','Central Funds',null);