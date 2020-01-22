ALTER TABLE `policy_milestones`
DROP INDEX `created_by`,
DROP INDEX `modified_by`;

ALTER TABLE `policy_milestones`
DROP COLUMN `active_since`,
DROP COLUMN `created_by`,
DROP COLUMN `modified_by`,
DROP COLUMN `modification_justification`;