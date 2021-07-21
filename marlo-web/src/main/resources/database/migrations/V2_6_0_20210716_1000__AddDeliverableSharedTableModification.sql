ALTER TABLE `project_deliverable_shared`
MODIFY COLUMN `active_since`  timestamp NOT NULL AFTER `created_by`;