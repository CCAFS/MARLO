CREATE TABLE `general_statuses_table` (
`id`  bigint(20) NOT NULL AUTO_INCREMENT ,
`table_name`  varchar(255) NOT NULL ,
`general_status_id`  bigint(20) NOT NULL ,
`is_active`  tinyint(1) NOT NULL ,
PRIMARY KEY (`id`),
CONSTRAINT `fk_general_status_id` FOREIGN KEY (`general_status_id`) REFERENCES `general_statuses` (`id`)
);

insert into general_statuses_table (table_name,general_status_id,is_active) values ('report_synthesis_flagship_progress_milestones',3,1);
insert into general_statuses_table (table_name,general_status_id,is_active) values ('report_synthesis_flagship_progress_milestones',4,1);
insert into general_statuses_table (table_name,general_status_id,is_active) values ('report_synthesis_flagship_progress_milestones',5,1);

insert into general_statuses_table (table_name,general_status_id,is_active) values ('project_expected_study_info',1,1);
insert into general_statuses_table (table_name,general_status_id,is_active) values ('project_expected_study_info',2,1);
insert into general_statuses_table (table_name,general_status_id,is_active) values ('project_expected_study_info',3,1);
insert into general_statuses_table (table_name,general_status_id,is_active) values ('project_expected_study_info',4,1);
insert into general_statuses_table (table_name,general_status_id,is_active) values ('project_expected_study_info',5,1);