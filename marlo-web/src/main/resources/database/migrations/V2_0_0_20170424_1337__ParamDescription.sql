ALTER TABLE `crp_parameters`
ADD COLUMN `description`  text NULL AFTER `modification_justification`,
ADD COLUMN `type`  int NULL AFTER `description`;

