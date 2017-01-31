
ALTER TABLE `project_outcomes_pandr`
ADD COLUMN `file_id`  bigint(20) NULL AFTER `comunication`;

ALTER TABLE `project_outcomes_pandr` ADD FOREIGN KEY (`file_id`) REFERENCES `files` (`id`);



INSERT INTO files (file_name,token_id)
select file, '' from project_outcomes_pandr where file  is not null and file<>'';
UPDATE project_outcomes_pandr set file_id= (select  id from files where file_name=file limit 1) where file is not null and file<>'';

ALTER TABLE `project_outcomes_pandr`
DROP COLUMN `file`;
