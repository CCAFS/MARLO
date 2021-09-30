CREATE TABLE st_projected_benefits (
id bigint(20) NOT NULL AUTO_INCREMENT,
impact_area_indicator_id bigint(20) not null,
description TEXT,
PRIMARY KEY (id),
CONSTRAINT impact_areas_indicator_fk FOREIGN KEY (impact_area_indicator_id) REFERENCES st_impact_areas_indicators (id) ON DELETE RESTRICT ON UPDATE RESTRICT,
INDEX projected_benefits_id (id) USING BTREE ,
INDEX impact_areas_indicator_idx (impact_area_indicator_id) USING BTREE
)
ENGINE=InnoDB
DEFAULT CHARACTER SET=utf8 COLLATE=utf8_general_ci;

insert into st_projected_benefits ( impact_area_indicator_id) values(1);
insert into st_projected_benefits ( impact_area_indicator_id) values(2);
insert into st_projected_benefits ( impact_area_indicator_id) values(3);
insert into st_projected_benefits ( impact_area_indicator_id) values(4);
insert into st_projected_benefits ( impact_area_indicator_id) values(6);
insert into st_projected_benefits ( impact_area_indicator_id) values(5);
insert into st_projected_benefits ( impact_area_indicator_id) values(8);
insert into st_projected_benefits ( impact_area_indicator_id) values(9);
insert into st_projected_benefits ( impact_area_indicator_id) values(10);
insert into st_projected_benefits ( impact_area_indicator_id) values(7);
insert into st_projected_benefits ( impact_area_indicator_id) values(11);
insert into st_projected_benefits ( impact_area_indicator_id) values(20);
insert into st_projected_benefits ( impact_area_indicator_id) values(13);
insert into st_projected_benefits ( impact_area_indicator_id) values(14);
insert into st_projected_benefits ( impact_area_indicator_id) values(12);
insert into st_projected_benefits ( impact_area_indicator_id) values(15);
insert into st_projected_benefits ( impact_area_indicator_id) values(16);
insert into st_projected_benefits ( impact_area_indicator_id) values(17);
insert into st_projected_benefits ( impact_area_indicator_id) values(18);
insert into st_projected_benefits ( impact_area_indicator_id) values(19);


CREATE TABLE st_depths_description (
id bigint(20) NOT NULL AUTO_INCREMENT,
name TEXT NULL,
PRIMARY KEY (id),
INDEX `depths_description_id` (`id`) USING BTREE 
)ENGINE=InnoDB
DEFAULT CHARACTER SET=utf8 COLLATE=utf8_general_ci;

INSERT INTO st_depths_description (name) values ('Income');
INSERT INTO st_depths_description (name) values ('Health');
INSERT INTO st_depths_description (name) values ('Other (See Annex 2)');
INSERT INTO st_depths_description (name) values ('Not Applicable');

CREATE TABLE st_projected_benefits_weight_description (
id bigint(20) NOT NULL AUTO_INCREMENT,
description TEXT NULL,
PRIMARY KEY (id),
INDEX `weight_description_id` (`id`) USING BTREE 
)ENGINE=InnoDB
DEFAULT CHARACTER SET=utf8 COLLATE=utf8_general_ci;

INSERT INTO st_projected_benefits_weight_description (description) values ('Life saving');
INSERT INTO st_projected_benefits_weight_description (description) values ('Transformative');
INSERT INTO st_projected_benefits_weight_description (description) values ('Substantial');
INSERT INTO st_projected_benefits_weight_description (description) values ('Significant');
INSERT INTO st_projected_benefits_weight_description (description) values ('Perceptible');



CREATE TABLE st_projected_benefits_depths (
id bigint(20) NOT NULL AUTO_INCREMENT,
projected_benefits_id bigint(20),
depth_description_id bigint(20),
PRIMARY KEY (id),
CONSTRAINT `projected_benefits_id_fk1` FOREIGN KEY (`projected_benefits_id`) REFERENCES `st_projected_benefits` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT,
CONSTRAINT `depths_description_fk` FOREIGN KEY (`depth_description_id`) REFERENCES `st_depths_description` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT,
INDEX `projected_benefits_index` (`id`) USING BTREE ,
INDEX `depth_description_index` (`depth_description_id`) USING BTREE 
)
ENGINE=InnoDB
DEFAULT CHARACTER SET=utf8 COLLATE=utf8_general_ci;

INSERT into st_projected_benefits_depths (projected_benefits_id,depth_description_id) values (1,1);
INSERT into st_projected_benefits_depths (projected_benefits_id,depth_description_id) values (1,2);
INSERT into st_projected_benefits_depths (projected_benefits_id,depth_description_id) values (2,1);
INSERT into st_projected_benefits_depths (projected_benefits_id,depth_description_id) values (2,2);
INSERT into st_projected_benefits_depths (projected_benefits_id,depth_description_id) values (3,1);
INSERT into st_projected_benefits_depths (projected_benefits_id,depth_description_id) values (3,2);
INSERT into st_projected_benefits_depths (projected_benefits_id,depth_description_id) values (4,1);
INSERT into st_projected_benefits_depths (projected_benefits_id,depth_description_id) values (4,2);
INSERT into st_projected_benefits_depths (projected_benefits_id,depth_description_id) values (5,4);
INSERT into st_projected_benefits_depths (projected_benefits_id,depth_description_id) values (6,1);
INSERT into st_projected_benefits_depths (projected_benefits_id,depth_description_id) values (6,2);
INSERT into st_projected_benefits_depths (projected_benefits_id,depth_description_id) values (7,3);
INSERT into st_projected_benefits_depths (projected_benefits_id,depth_description_id) values (8,1);
INSERT into st_projected_benefits_depths (projected_benefits_id,depth_description_id) values (8,2);
INSERT into st_projected_benefits_depths (projected_benefits_id,depth_description_id) values (9,4);
INSERT into st_projected_benefits_depths (projected_benefits_id,depth_description_id) values (11,4);
INSERT into st_projected_benefits_depths (projected_benefits_id,depth_description_id) values (12,1);
INSERT into st_projected_benefits_depths (projected_benefits_id,depth_description_id) values (12,2);
INSERT into st_projected_benefits_depths (projected_benefits_id,depth_description_id) values (13,1);
INSERT into st_projected_benefits_depths (projected_benefits_id,depth_description_id) values (13,2);
INSERT into st_projected_benefits_depths (projected_benefits_id,depth_description_id) values (14,1);
INSERT into st_projected_benefits_depths (projected_benefits_id,depth_description_id) values (14,2);
INSERT into st_projected_benefits_depths (projected_benefits_id,depth_description_id) values (16,3);
INSERT into st_projected_benefits_depths (projected_benefits_id,depth_description_id) values (17,3);
INSERT into st_projected_benefits_depths (projected_benefits_id,depth_description_id) values (18,3);
INSERT into st_projected_benefits_depths (projected_benefits_id,depth_description_id) values (19,3);
INSERT into st_projected_benefits_depths (projected_benefits_id,depth_description_id) values (20,4);


CREATE TABLE st_projected_benefits_weighting (
id bigint(20) NOT NULL AUTO_INCREMENT,
projected_benefits_id bigint(20),
weight_description_id bigint(20),
weight_value varchar(50),
PRIMARY KEY (id),
CONSTRAINT `projected_benefits_id_fk2` FOREIGN KEY (`projected_benefits_id`) REFERENCES `st_projected_benefits` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT,
CONSTRAINT `projected_benefits_weight_description_fk` FOREIGN KEY (`weight_description_id`) REFERENCES `st_projected_benefits_weight_description` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT,
INDEX `projected_benefits_index` (`id`) USING BTREE ,
INDEX `weight_description_index` (`weight_description_id`) USING BTREE 
)
ENGINE=InnoDB
DEFAULT CHARACTER SET=utf8 COLLATE=utf8_general_ci;

INSERT into st_projected_benefits_weighting (projected_benefits_id,weight_description_id,weight_value) values (1,1,'50');
INSERT into st_projected_benefits_weighting (projected_benefits_id,weight_description_id,weight_value) values (1,2,'10');
INSERT into st_projected_benefits_weighting (projected_benefits_id,weight_description_id,weight_value) values (1,3,'5');
INSERT into st_projected_benefits_weighting (projected_benefits_id,weight_description_id,weight_value) values (2,1,'50');
INSERT into st_projected_benefits_weighting (projected_benefits_id,weight_description_id,weight_value) values (2,2,'10');
INSERT into st_projected_benefits_weighting (projected_benefits_id,weight_description_id,weight_value) values (2,3,'5');
INSERT into st_projected_benefits_weighting (projected_benefits_id,weight_description_id,weight_value) values (2,4,'1');
INSERT into st_projected_benefits_weighting (projected_benefits_id,weight_description_id,weight_value) values (2,5,'0.1-0.5');
INSERT into st_projected_benefits_weighting (projected_benefits_id,weight_description_id,weight_value) values (3,1,'50');
INSERT into st_projected_benefits_weighting (projected_benefits_id,weight_description_id,weight_value) values (3,2,'10');
INSERT into st_projected_benefits_weighting (projected_benefits_id,weight_description_id,weight_value) values (3,3,'5');
INSERT into st_projected_benefits_weighting (projected_benefits_id,weight_description_id,weight_value) values (3,4,'1');
INSERT into st_projected_benefits_weighting (projected_benefits_id,weight_description_id,weight_value) values (3,5,'0.1-0.5');
INSERT into st_projected_benefits_weighting (projected_benefits_id,weight_description_id,weight_value) values (4,1,'50');
INSERT into st_projected_benefits_weighting (projected_benefits_id,weight_description_id,weight_value) values (4,2,'10');
INSERT into st_projected_benefits_weighting (projected_benefits_id,weight_description_id,weight_value) values (4,3,'5');
INSERT into st_projected_benefits_weighting (projected_benefits_id,weight_description_id,weight_value) values (4,4,'1');
INSERT into st_projected_benefits_weighting (projected_benefits_id,weight_description_id,weight_value) values (4,5,'0.1-0.5');
INSERT into st_projected_benefits_weighting (projected_benefits_id,weight_description_id,weight_value) values (6,1,'50');
INSERT into st_projected_benefits_weighting (projected_benefits_id,weight_description_id,weight_value) values (6,2,'10');
INSERT into st_projected_benefits_weighting (projected_benefits_id,weight_description_id,weight_value) values (6,3,'5');
INSERT into st_projected_benefits_weighting (projected_benefits_id,weight_description_id,weight_value) values (6,4,'1');
INSERT into st_projected_benefits_weighting (projected_benefits_id,weight_description_id,weight_value) values (6,5,'0.1-0.5');
INSERT into st_projected_benefits_weighting (projected_benefits_id,weight_description_id,weight_value) values (7,2,'50');
INSERT into st_projected_benefits_weighting (projected_benefits_id,weight_description_id,weight_value) values (7,3,'10');
INSERT into st_projected_benefits_weighting (projected_benefits_id,weight_description_id,weight_value) values (8,1,'50');
INSERT into st_projected_benefits_weighting (projected_benefits_id,weight_description_id,weight_value) values (8,2,'10');
INSERT into st_projected_benefits_weighting (projected_benefits_id,weight_description_id,weight_value) values (8,3,'5');
INSERT into st_projected_benefits_weighting (projected_benefits_id,weight_description_id,weight_value) values (8,4,'1');
INSERT into st_projected_benefits_weighting (projected_benefits_id,weight_description_id,weight_value) values (8,5,'0.1-0.5');
INSERT into st_projected_benefits_weighting (projected_benefits_id,weight_description_id,weight_value) values (12,1,'50');
INSERT into st_projected_benefits_weighting (projected_benefits_id,weight_description_id,weight_value) values (12,2,'10');
INSERT into st_projected_benefits_weighting (projected_benefits_id,weight_description_id,weight_value) values (12,3,'5');
INSERT into st_projected_benefits_weighting (projected_benefits_id,weight_description_id,weight_value) values (12,4,'1');
INSERT into st_projected_benefits_weighting (projected_benefits_id,weight_description_id,weight_value) values (12,5,'0.1-0.5');
INSERT into st_projected_benefits_weighting (projected_benefits_id,weight_description_id,weight_value) values (13,2,'10');
INSERT into st_projected_benefits_weighting (projected_benefits_id,weight_description_id,weight_value) values (13,3,'5');
INSERT into st_projected_benefits_weighting (projected_benefits_id,weight_description_id,weight_value) values (13,4,'1');
INSERT into st_projected_benefits_weighting (projected_benefits_id,weight_description_id,weight_value) values (13,5,'0.1-0.5');
INSERT into st_projected_benefits_weighting (projected_benefits_id,weight_description_id,weight_value) values (14,1,'50');
INSERT into st_projected_benefits_weighting (projected_benefits_id,weight_description_id,weight_value) values (14,2,'10');
INSERT into st_projected_benefits_weighting (projected_benefits_id,weight_description_id,weight_value) values (14,3,'5');
INSERT into st_projected_benefits_weighting (projected_benefits_id,weight_description_id,weight_value) values (14,4,'1');
INSERT into st_projected_benefits_weighting (projected_benefits_id,weight_description_id,weight_value) values (16,2,'10');
INSERT into st_projected_benefits_weighting (projected_benefits_id,weight_description_id,weight_value) values (16,3,'5');
INSERT into st_projected_benefits_weighting (projected_benefits_id,weight_description_id,weight_value) values (16,4,'1');
INSERT into st_projected_benefits_weighting (projected_benefits_id,weight_description_id,weight_value) values (17,2,'10');
INSERT into st_projected_benefits_weighting (projected_benefits_id,weight_description_id,weight_value) values (17,3,'5');
INSERT into st_projected_benefits_weighting (projected_benefits_id,weight_description_id,weight_value) values (17,4,'1');
INSERT into st_projected_benefits_weighting (projected_benefits_id,weight_description_id,weight_value) values (18,2,'10');
INSERT into st_projected_benefits_weighting (projected_benefits_id,weight_description_id,weight_value) values (18,3,'5');
INSERT into st_projected_benefits_weighting (projected_benefits_id,weight_description_id,weight_value) values (18,4,'1');
INSERT into st_projected_benefits_weighting (projected_benefits_id,weight_description_id,weight_value) values (19,2,'10');
INSERT into st_projected_benefits_weighting (projected_benefits_id,weight_description_id,weight_value) values (19,3,'5');
INSERT into st_projected_benefits_weighting (projected_benefits_id,weight_description_id,weight_value) values (19,4,'1');

CREATE TABLE st_projected_benefits_probabilites (
id bigint(20) NOT NULL AUTO_INCREMENT,
name TEXT NULL,
description TEXT NULL,
PRIMARY KEY (id),
INDEX `st_projected_benefits_probabilites_id` (`id`) USING BTREE 
)ENGINE=InnoDB
DEFAULT CHARACTER SET=utf8 COLLATE=utf8_general_ci;

Insert Into st_projected_benefits_probabilites (name, description) values ('Very high certainty','>80% expectation of achieving these impacts by 2030, at this point in the design process');
Insert Into st_projected_benefits_probabilites (name, description) values ('High certainty','60%-80% expectation of achieving these impacts by 2030, at this point');
Insert Into st_projected_benefits_probabilites (name, description) values ('Medium certainty','40%-60% expectation of achieving these impacts by 2030, at this point');
Insert Into st_projected_benefits_probabilites (name, description) values ('Lower certainty','<40% expectation of achieving these impacts by 2030, at this point');
