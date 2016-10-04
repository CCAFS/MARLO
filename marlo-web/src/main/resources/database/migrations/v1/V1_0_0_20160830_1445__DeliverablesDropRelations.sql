START TRANSACTION;
ALTER TABLE `deliverables` DROP FOREIGN KEY `deliverables_cluster_activities_fk`;

ALTER TABLE `deliverables` DROP FOREIGN KEY `deliverables_program_fk`;

ALTER TABLE `deliverables`
DROP COLUMN `program_id`,
DROP COLUMN `cluster_activity_id`;
COMMIT;