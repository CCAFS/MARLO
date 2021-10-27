create table oc_accounts (
id bigint(20) NOT NULL AUTO_INCREMENT,
financial_code VARCHAR(20) NOT NULL,
description TEXT,
account_type_id BIGINT NOT NULL,
parent_id BIGINT,
PRIMARY KEY (id),
KEY `account_type_id` (account_type_id) USING BTREE,
KEY `parent_id` (parent_id),
INDEX `oc_account_id` (`id`) USING BTREE,
CONSTRAINT `oc_accounts_ibfk_1` FOREIGN KEY (`account_type_id`) REFERENCES `oc_account_types` (`id`),
CONSTRAINT `oc_accounts_ibfk_2` FOREIGN KEY (`parent_id`) REFERENCES `oc_accounts` (`id`)
)ENGINE=InnoDB
DEFAULT CHARACTER SET=utf8;

insert into oc_account_types (name) values ('A1 Main Group');
insert into oc_account_types (name) values ('A2 Category');
insert into oc_account_types (name) values ('A3 Main Account');
insert into oc_account_types (name) values ('A4 Sub Accounts');