alter table loc_elements  ADD COLUMN `is_active`  tinyint(1) NOT NULL,
ADD COLUMN `created_by`  bigint(20) NULL DEFAULT NULL AFTER `is_active`,
ADD COLUMN `active_since`  timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP AFTER `created_by`,
ADD COLUMN `modified_by`  bigint(20) NOT NULL AFTER `active_since`,
ADD COLUMN `modification_justification`  text CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL AFTER `modified_by`;

alter table loc_element_types  ADD COLUMN `is_active`  tinyint(1) NOT NULL,
ADD COLUMN `created_by`  bigint(20) NULL DEFAULT NULL AFTER `is_active`,
ADD COLUMN `active_since`  timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP AFTER `created_by`,
ADD COLUMN `modified_by`  bigint(20) NOT NULL AFTER `active_since`,
ADD COLUMN `modification_justification`  text CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL AFTER `modified_by`;

alter table loc_geopositions  ADD COLUMN `is_active`  tinyint(1) NOT NULL,
ADD COLUMN `created_by`  bigint(20) NULL DEFAULT NULL AFTER `is_active`,
ADD COLUMN `active_since`  timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP AFTER `created_by`,
ADD COLUMN `modified_by`  bigint(20) NOT NULL AFTER `active_since`,
ADD COLUMN `modification_justification`  text CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL AFTER `modified_by`;
