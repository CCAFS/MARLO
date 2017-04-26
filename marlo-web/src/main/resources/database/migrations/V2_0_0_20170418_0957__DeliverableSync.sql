ALTER TABLE `deliverable_dissemination`
ADD COLUMN `synced`  tinyint(1) NULL AFTER `dissemination_channel_name`;

ALTER TABLE `deliverable_metadata_elements`
ADD COLUMN `hide`  tinyint(1) NULL AFTER `element_value`;

