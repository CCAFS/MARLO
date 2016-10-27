ALTER TABLE `project_budgets`
ADD COLUMN `funding_source_id`  bigint(20) NULL DEFAULT NULL AFTER `year`;

ALTER TABLE `project_budgets` ADD FOREIGN KEY (`funding_source_id`) REFERENCES `funding_sources` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT;




UPDATE project_budgets pb  INNER JOIN funding_sources ff on ff.id=pb.project_id
set funding_source_id=ff.id
where budget_type=2;


UPDATE project_budgets pb  INNER JOIN funding_sources ff on ff.id=pb.cofinance_project_id
set funding_source_id=ff.id
where budget_type=3;
 

UPDATE project_budgets pb  INNER JOIN funding_sources ff on ff.id=pb.project_id
set funding_source_id=248
where budget_type=1;
 

UPDATE project_budgets pb 
set is_active=0
where budget_type=4;

UPDATE project_budgets pb 
set is_active=0
where funding_source_id is null;
ALTER TABLE `project_budgets` DROP FOREIGN KEY `project_budgets_ibfk_4`;

ALTER TABLE `project_budgets`
DROP COLUMN `cofinance_project_id`;




ALTER TABLE `section_statuses` DROP FOREIGN KEY `section_statuses_ibfk_5`;


ALTER TABLE `section_statuses`
DROP COLUMN `project_cofunded_id`;
drop table projects_bilateral_cofinancing;