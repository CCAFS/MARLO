ALTER TABLE `projects_info`
ADD COLUMN `cross_cutting_climate`  tinyint(1) NULL AFTER `cross_cutting_capacity`;

ALTER TABLE `deliverables_info`
ADD COLUMN `cross_cutting_climate`  tinyint(1) NULL AFTER `cross_cutting_capacity`;

ALTER TABLE `deliverables_info`
ADD COLUMN `cross_cutting_score_climate`  bigint(20) NULL AFTER `cross_cutting_score_capacity`;