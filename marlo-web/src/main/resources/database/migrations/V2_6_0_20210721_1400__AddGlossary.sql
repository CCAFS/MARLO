create table glossary (
id bigint(20) not null AUTO_INCREMENT,
applicationName varchar(100) not null,
title varchar(100) not null,
definition text null, 
primary key(id) )
ENGINE=InnoDB
DEFAULT CHARACTER SET=utf8 COLLATE=utf8_general_ci;