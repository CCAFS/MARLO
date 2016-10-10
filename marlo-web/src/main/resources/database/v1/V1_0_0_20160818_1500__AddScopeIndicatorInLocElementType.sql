ALTER TABLE `loc_element_types`
ADD COLUMN `is_scope`  tinyint(1) NOT NULL DEFAULT 0 AFTER `modification_justification`;