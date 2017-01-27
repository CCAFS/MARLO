ALTER TABLE `deliverables`
MODIFY COLUMN `project_id`  bigint(20) NULL AFTER `id`,
ADD COLUMN `is_publication`  tinyint(1) NULL AFTER `allow_modifications`;

