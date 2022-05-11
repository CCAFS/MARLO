CREATE TABLE oc_study_types (
id bigint(20) NOT NULL AUTO_INCREMENT,
name TEXT,
description TEXT,
norder int(2),
PRIMARY KEY (id),
INDEX `oc_study_types_id` (`id`) USING BTREE
)ENGINE=InnoDB
DEFAULT CHARACTER SET=utf8 COLLATE=utf8_general_ci;

Insert into oc_study_types(id,name,description,norder) values (1,'Ex-ante, baseline and/or foresight study','',1);
Insert into oc_study_types(id,name,description,norder) values (2,'Adoption or diffusion studies addressing learning questions on the TOC','',2); 
Insert into oc_study_types(id,name,description,norder) values (3,'Scaling readiness assessment','',3); 
Insert into oc_study_types(id,name,description,norder) values (4,'Tracing of scaling activities, as base for long-term, large scale impact studies','',4); 
Insert into oc_study_types(id,name,description,norder) values (5,'Causal Impact Assessment learning studies','',5); 
Insert into oc_study_types(id,name,description,norder) values (6,'Program/project evaluation or review','',6);  
Insert into oc_study_types(id,name,description,norder) values (7,'Qualitative outcome study','',7);  
Insert into oc_study_types(id,name,description,norder) values (8,'Other MELIA activity','',20);  