create table oc_account_types (
id bigint(20) NOT NULL AUTO_INCREMENT,
name TEXT NOT NULL,
PRIMARY KEY (id),
INDEX `oc_account_types_id` (`id`) USING BTREE
)ENGINE=InnoDB
DEFAULT CHARACTER SET=utf8mb4 COLLATE=utf8_unicode_ci;

insert into oc_account_types (name) values ('A1 Main Group');
insert into oc_account_types (name) values ('A2 Category');
insert into oc_account_types (name) values ('A3 Main Account');
insert into oc_account_types (name) values ('A4 Sub Accounts');