ALter table rep_ind_innovation_types add is_onecgiar tinyint(1) DEFAULT 0;
ALter table rep_ind_innovation_types add is_marlo tinyint(1) DEFAULT 1;

Insert into rep_ind_innovation_types(id,name,is_onecgiar,is_marlo) values (7,'Technology',1,0);
Insert into rep_ind_innovation_types(id,name,is_onecgiar,is_marlo) values (8,'Product',1,0);
Insert into rep_ind_innovation_types(id,name,is_onecgiar,is_marlo) values (9,'Service',1,0);
Insert into rep_ind_innovation_types(id,name,is_onecgiar,is_marlo) values (10,'Organizational or Institutional Arrangement',1,0);
update rep_ind_innovation_types set is_onecgiar=1 where id=6;


create table oc_governance_type (
id bigint(20) NOT NULL AUTO_INCREMENT,
name TEXT,
PRIMARY KEY (id),
INDEX `oc_governance_type_id` (`id`) USING BTREE
)ENGINE=InnoDB
DEFAULT CHARACTER SET=utf8 COLLATE=utf8_general_ci;

Insert into oc_governance_type(id,name) values (1,'Private');
Insert into oc_governance_type(id,name) values (2,'Public'); 
Insert into oc_governance_type(id,name) values (3,'Shared Private and Public'); 

create table oc_environmental_benefits (
id bigint(20) NOT NULL AUTO_INCREMENT,
name TEXT,
PRIMARY KEY (id),
INDEX `oc_environmental_benefits_id` (`id`) USING BTREE
)ENGINE=InnoDB
DEFAULT CHARACTER SET=utf8 COLLATE=utf8_general_ci;

Insert into oc_environmental_benefits(id,name) values (1,'Air');
Insert into oc_environmental_benefits(id,name) values (2,'Biodiversity and ecosystems'); 
Insert into oc_environmental_benefits(id,name) values (3,'Electricity');
Insert into oc_environmental_benefits(id,name) values (4,'Food'); 
Insert into oc_environmental_benefits(id,name) values (5,'Forest'); 
Insert into oc_environmental_benefits(id,name) values (6,'Greenhouse gases'); 
Insert into oc_environmental_benefits(id,name) values (7,'Land'); 
Insert into oc_environmental_benefits(id,name) values (8,'Mining and metals');
Insert into oc_environmental_benefits(id,name) values (9,'Natural gas');
Insert into oc_environmental_benefits(id,name) values (10,'Oil');
Insert into oc_environmental_benefits(id,name) values (11,'Sea');
Insert into oc_environmental_benefits(id,name) values (12,'Solid fuel');
Insert into oc_environmental_benefits(id,name) values (13,'Water');
Insert into oc_environmental_benefits(id,name) values (14,'Other');


create table oc_technology_development_stage (
id bigint(20) NOT NULL AUTO_INCREMENT,
official_code TEXT,
name TEXT,
PRIMARY KEY (id),
INDEX `oc_technology_development_stage_id` (`id`) USING BTREE
)ENGINE=InnoDB
DEFAULT CHARACTER SET=utf8 COLLATE=utf8_general_ci;

Insert into oc_technology_development_stage(id,official_code,name) values (1,'TRL 1','Basic principles observed');
Insert into oc_technology_development_stage(id,official_code,name) values (2,'TRL 2','Technology concept formulated');
Insert into oc_technology_development_stage(id,official_code,name) values (3,'TRL 3','Experimental proof of concept');
Insert into oc_technology_development_stage(id,official_code,name) values (4,'TRL 4','Technology validated in lab');
Insert into oc_technology_development_stage(id,official_code,name) values (5,'TRL 5','Technology validated in relevant environment (industrially relevant environment in the case of key enabling technologies)');
Insert into oc_technology_development_stage(id,official_code,name) values (6,'TRL 6','Technology demonstrated in relevant environment (industrially relevant environment in the case of key enabling technologies)');
Insert into oc_technology_development_stage(id,official_code,name) values (7,'TRL 7','System prototype demonstration in operational environment');
Insert into oc_technology_development_stage(id,official_code,name) values (8,'TRL 8','System complete and qualified');
Insert into oc_technology_development_stage(id,official_code,name) values (9,'TRL 9','Actual system proven in operational environment (competitive manufacturing in the case of key enabling technologies; or in space)');

create table oc_innovation_readiness_levels (
id bigint(20) NOT NULL AUTO_INCREMENT,
name TEXT,
PRIMARY KEY (id),
INDEX `oc_innovation_readiness_levels_id` (`id`) USING BTREE
)ENGINE=InnoDB
DEFAULT CHARACTER SET=utf8 COLLATE=utf8_general_ci;

Insert into oc_innovation_readiness_levels(id,name) values (1,'Idea');
Insert into oc_innovation_readiness_levels(id,name) values (2,'Hypothesis (proven)');
Insert into oc_innovation_readiness_levels(id,name) values (3,'Basic Model (unproven)');
Insert into oc_innovation_readiness_levels(id,name) values (4,'Basic Model (proven)');
Insert into oc_innovation_readiness_levels(id,name) values (5,'Application Model (unproven)');
Insert into oc_innovation_readiness_levels(id,name) values (6,'Application Model (proven)');
Insert into oc_innovation_readiness_levels(id,name) values (7,'Application (unproven)');
Insert into oc_innovation_readiness_levels(id,name) values (8,'Application (proven)');
Insert into oc_innovation_readiness_levels(id,name) values (9,'Innovation (unproven)');
Insert into oc_innovation_readiness_levels(id,name) values (10,'Innovation (proven)');


create table oc_users (
id bigint(20) NOT NULL AUTO_INCREMENT,
name TEXT,
PRIMARY KEY (id),
INDEX `oc_users_id` (`id`) USING BTREE
)ENGINE=InnoDB
DEFAULT CHARACTER SET=utf8 COLLATE=utf8_general_ci;

Insert into oc_users(id,name) values (1,'Farmers (small-scale or commercial farmers)');
Insert into oc_users(id,name) values (2,'Community-based Organizations');
Insert into oc_users(id,name) values (3,'Private Sector');
Insert into oc_users(id,name) values (4,'Researchers');
Insert into oc_users(id,name) values (5,'NARES/NARS');
Insert into oc_users(id,name) values (6,'Extension Agents');
Insert into oc_users(id,name) values (7,'Government');
Insert into oc_users(id,name) values (8,'Traders');
Insert into oc_users(id,name) values (9,'Foundations');
Insert into oc_users(id,name) values (10,'Financial Institutions');
Insert into oc_users(id,name) values (11,'Multilateral');
Insert into oc_users(id,name) values (12,'Agro-manufacturers');
Insert into oc_users(id,name) values (13,'Agro-dealers');
Insert into oc_users(id,name) values (14,'Land users');
Insert into oc_users(id,name) values (15,'Bilateral And Donor');
Insert into oc_users(id,name) values (16,'Women');
Insert into oc_users(id,name) values (17,'Youth');
Insert into oc_users(id,name) values (18,'Other');

create table oc_investment_types (
id bigint(20) NOT NULL AUTO_INCREMENT,
name TEXT,
PRIMARY KEY (id),
INDEX `oc_investment_type_id` (`id`) USING BTREE
)ENGINE=InnoDB
DEFAULT CHARACTER SET=utf8 COLLATE=utf8_general_ci;

Insert into oc_investment_types(id,name) values (1,'Bonds');
Insert into oc_investment_types(id,name) values (2,'Grant');
Insert into oc_investment_types(id,name) values (3,'Guarantee');
Insert into oc_investment_types(id,name) values (4,'Private Equity');
Insert into oc_investment_types(id,name) values (5,'Venture Capital');
Insert into oc_investment_types(id,name) values (6,'Other');

create table oc_innovation_use_levels (
id bigint(20) NOT NULL AUTO_INCREMENT,
name TEXT,
PRIMARY KEY (id),
INDEX `oc_innovation_use_levels_id` (`id`) USING BTREE
)ENGINE=InnoDB
DEFAULT CHARACTER SET=utf8 COLLATE=utf8_general_ci;

Insert into oc_innovation_use_levels(id,name) values (1,'Team');
Insert into oc_innovation_use_levels(id,name) values (2,'Partners (rare)');
Insert into oc_innovation_use_levels(id,name) values (3,'Partners (common)');
Insert into oc_innovation_use_levels(id,name) values (4,'Unconnected designers and developers (rare)');
Insert into oc_innovation_use_levels(id,name) values (5,'Unconnected designers and developers (common)');
Insert into oc_innovation_use_levels(id,name) values (6,'Unconnected delivery and use support stakeholders (rare)');
Insert into oc_innovation_use_levels(id,name) values (7,'Unconnected delivery and use support stakeholders (common)');
Insert into oc_innovation_use_levels(id,name) values (8,'Unconnected end-users (rare)');
Insert into oc_innovation_use_levels(id,name) values (9,'Unconnected end-users (common)');


