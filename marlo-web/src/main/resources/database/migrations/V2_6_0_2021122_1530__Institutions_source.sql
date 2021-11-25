CREATE TABLE institutions_source (
id bigint(20) NOT NULL AUTO_INCREMENT, 
source_name TEXT null,
PRIMARY KEY (id),
INDEX `institutions_source_id` (`id`) USING BTREE
)ENGINE=InnoDB
DEFAULT CHARACTER SET=utf8 COLLATE=utf8_general_ci;

insert into institutions_source(source_name) values ('MEL');
insert into institutions_source(source_name) values ('Alliance Agresso');


CREATE TABLE institution_dictionary (
id bigint(20) NOT NULL AUTO_INCREMENT, 
institution_id bigint(20) NOT NULL,
source_id bigint(2) NOT NULL,
institution_source_id TEXT null,
institution_source_name TEXT null,
PRIMARY KEY (id),
CONSTRAINT `institutions_dictonary_fk1` FOREIGN KEY (`institution_id`) REFERENCES `institutions` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT,
CONSTRAINT `institutions_source_fk1` FOREIGN KEY (`source_id`) REFERENCES `institutions_source` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT,
INDEX `institutions_dictonary_id` (`id`) USING BTREE
)ENGINE=InnoDB
DEFAULT CHARACTER SET=utf8 COLLATE=utf8_general_ci;