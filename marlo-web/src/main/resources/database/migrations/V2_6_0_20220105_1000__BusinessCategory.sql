CREATE TABLE oc_business_category (
id bigint(20) NOT NULL AUTO_INCREMENT,
name TEXT,
PRIMARY KEY (id),
INDEX `oc_business_category_id` (`id`) USING BTREE
)ENGINE=InnoDB
DEFAULT CHARACTER SET=utf8 COLLATE=utf8_general_ci;

Insert into oc_business_category(id,name) values (1,'Design');
Insert into oc_business_category(id,name) values (2,'Device'); 
Insert into oc_business_category(id,name) values (3,'Facility'); 
Insert into oc_business_category(id,name) values (4,'Material'); 
Insert into oc_business_category(id,name) values (5,'Process'); 
Insert into oc_business_category(id,name) values (6,'System or software');  

CREATE TABLE oc_technical_field (
id bigint(20) NOT NULL AUTO_INCREMENT,
name TEXT,
PRIMARY KEY (id),
INDEX `oc_technical_field_id` (`id`) USING BTREE
)ENGINE=InnoDB
DEFAULT CHARACTER SET=utf8 COLLATE=utf8_general_ci;

Insert into oc_technical_field(id,name) values (1,'Building & Construction');
Insert into oc_technical_field(id,name) values (2,'Energy'); 
Insert into oc_technical_field(id,name) values (3,'Farming & Forestry'); 
Insert into oc_technical_field(id,name) values (4,'Pollution & Waste'); 
Insert into oc_technical_field(id,name) values (5,'Product'); 
Insert into oc_technical_field(id,name) values (6,'Materials and processes');
Insert into oc_technical_field(id,name) values (7,'Transportation');  
Insert into oc_technical_field(id,name) values (8,'Water');
