create table oc_unit_types (
id bigint(20) NOT NULL AUTO_INCREMENT,
acronym VARCHAR(20) NOT NULL,
description TEXT,
PRIMARY KEY (id),
INDEX `oc_unit_id` (`id`) USING BTREE
)ENGINE=InnoDB
DEFAULT CHARACTER SET=utf8;

insert into oc_unit_types(acronym, description) value ('U1', 'Division');
insert into oc_unit_types(acronym, description) value ('U2', 'Global Group');
insert into oc_unit_types(acronym, description) value ('U3', 'Units');
