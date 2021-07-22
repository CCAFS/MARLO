create table region_types (
id bigint(20) not null AUTO_INCREMENT,
name varchar(100) not null,
description text null, 
primary key(id) )
ENGINE=InnoDB
DEFAULT CHARACTER SET=utf8 COLLATE=utf8_general_ci;

create table regions (
id bigint(20) not null AUTO_INCREMENT,
iso_numeric int(20) not null,
name varchar(100) not null,
region_type_id bigint not null, 
primary key (id),
CONSTRAINT `region_types_fk` FOREIGN KEY (`region_type_id`) REFERENCES `region_types` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT,
INDEX `region_id` (`id`) USING BTREE ,
INDEX `region_type_id` (`region_type_id`) USING BTREE )
ENGINE=InnoDB
DEFAULT CHARACTER SET=utf8 COLLATE=utf8_general_ci;

create table loc_elements_regions (
id bigint(20) not null AUTO_INCREMENT,
loc_element_id bigint(20),
region_id bigint(20),
primary key (id),
CONSTRAINT `loc_element_id_fk` FOREIGN KEY (`loc_element_id`) REFERENCES `loc_elements` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT,
CONSTRAINT `region_id_fk` FOREIGN KEY (`region_id`) REFERENCES `regions` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT
)ENGINE=InnoDB
DEFAULT CHARACTER SET=utf8 COLLATE=utf8_general_ci;


