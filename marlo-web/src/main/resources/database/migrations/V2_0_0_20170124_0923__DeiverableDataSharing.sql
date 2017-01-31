ALTER TABLE `deliverable_data_sharing_file`
ADD COLUMN `type_id`  int NULL AFTER `type`,
ADD COLUMN `file_id`  bigint(20) NULL AFTER `type_id`,
ADD COLUMN `external_file`  varchar(500) NULL AFTER `file_id`;

ALTER TABLE `deliverable_data_sharing_file` ADD FOREIGN KEY (`file_id`) REFERENCES `files` (`id`);

UPDATE deliverable_data_sharing_file
set type_id=1
where type='Locally';

UPDATE deliverable_data_sharing_file
set type_id=1
where type='Locally';
UPDATE deliverable_data_sharing_file
set type_id=2
where type='Externally';



UPDATE deliverable_data_sharing_file
set external_file=file
where type='Externally';

ALTER TABLE `files`
MODIFY COLUMN `file_name`  varchar(500) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL AFTER `id`;



INSERT INTO files (file_name,token_id)
select file, '' from deliverable_data_sharing_file where file  is not null and file<>'' and type_id=1 ;
UPDATE deliverable_data_sharing_file set file_id= (select  id from files where file_name=file limit 1) where file is not null and file<>'' and type_id=1;


ALTER TABLE `deliverable_data_sharing_file`
DROP COLUMN `file`,
DROP COLUMN `type`

;
