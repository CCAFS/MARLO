
SET FOREIGN_KEY_CHECKS = 0;


CREATE TEMPORARY TABLE
IF NOT EXISTS table_funding_source_budgets AS (SELECT * FROM funding_source_budgets);
TRUNCATE TABLE funding_source_budgets;

ALTER TABLE `funding_source_budgets`
ADD COLUMN `id_phase`  bigint(20) NULL AFTER `modification_justification`;

ALTER TABLE `funding_source_budgets` ADD FOREIGN KEY (`id_phase`) REFERENCES `phases` (`id`);

INSERT INTO funding_source_budgets (
funding_source_id,
budget,
year,
is_active,
created_by,
active_since,
modified_by,
modification_justification,
id_phase
) SELECT 
distinct
temp.funding_source_id,
temp.budget,
temp.year,
temp.is_active,
temp.created_by,
temp.active_since,
temp.modified_by,
temp.modification_justification,
inf.id_phase

from table_funding_source_budgets temp 
INNER JOIN funding_sources fs on fs.id=temp.funding_source_id
INNER JOIN funding_sources_info inf ON fs.id=inf.funding_source_id
;



CREATE TEMPORARY TABLE
IF NOT EXISTS table_funding_source_locations AS (SELECT * FROM funding_source_locations);
TRUNCATE TABLE funding_source_locations;

ALTER TABLE `funding_source_locations`
ADD COLUMN `id_phase`  bigint(20) NULL AFTER `modification_justification`;

ALTER TABLE `funding_source_locations` ADD FOREIGN KEY (`id_phase`) REFERENCES `phases` (`id`);

INSERT INTO funding_source_locations (
funding_source_id,
loc_element_id,
loc_element_type_id,
is_active,
active_since,
created_by,
modified_by,
modification_justification,
id_phase
)  SELECT 
distinct
temp.funding_source_id,
temp.loc_element_id,
temp.loc_element_type_id,
temp.is_active,
temp.active_since,
temp.created_by,
temp.modified_by,
temp.modification_justification,

inf.id_phase

from table_funding_source_locations temp 
INNER JOIN funding_sources fs on fs.id=temp.funding_source_id
INNER JOIN funding_sources_info inf ON fs.id=inf.funding_source_id
;




CREATE TEMPORARY TABLE
IF NOT EXISTS table_funding_source_institutions AS (SELECT * FROM funding_source_institutions);
TRUNCATE TABLE funding_source_institutions;

ALTER TABLE `funding_source_institutions`
ADD COLUMN `id_phase`  bigint(20) NULL AFTER `institution_id`;

ALTER TABLE `funding_source_institutions` ADD FOREIGN KEY (`id_phase`) REFERENCES `phases` (`id`);

INSERT INTO funding_source_institutions (
funding_source_id,
institution_id,

id_phase
)  SELECT 
distinct
temp.funding_source_id,
temp.institution_id,

inf.id_phase

from table_funding_source_institutions temp 
INNER JOIN funding_sources fs on fs.id=temp.funding_source_id
INNER JOIN funding_sources_info inf ON fs.id=inf.funding_source_id
;
