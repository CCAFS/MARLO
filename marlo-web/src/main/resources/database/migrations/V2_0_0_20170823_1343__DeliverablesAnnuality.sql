SET FOREIGN_KEY_CHECKS = 0;
CREATE TABLE `deliverables_info` (
`id`  bigint(20) NOT NULL AUTO_INCREMENT ,
`title`  text CHARACTER SET utf8 COLLATE utf8_general_ci NULL ,
`description`  text CHARACTER SET utf8 COLLATE utf8_general_ci NULL ,
`type_id`  bigint(20) NULL DEFAULT NULL ,
`type_other`  text CHARACTER SET utf8 COLLATE utf8_general_ci NULL COMMENT 'Other type defined by the user.' ,
`new_expected_year`  int(11) NULL DEFAULT NULL ,
`year`  int(11) NOT NULL ,
`status`  int(11) NULL DEFAULT NULL ,
`status_description`  text CHARACTER SET utf8 COLLATE utf8_general_ci NULL ,
`modified_by`  bigint(20) NOT NULL ,
`modification_justification`  text CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL ,
`outcome_id`  bigint(20) NULL DEFAULT NULL ,
`deliverable_id`  bigint(20) NULL DEFAULT NULL ,
`key_output_id`  bigint(20) NULL DEFAULT NULL ,
`cross_cutting_gender`  tinyint(1) NULL DEFAULT NULL ,
`cross_cutting_youth`  tinyint(1) NULL DEFAULT NULL ,
`cross_cutting_capacity`  tinyint(1) NULL DEFAULT NULL ,
`cross_cutting_na`  tinyint(1) NULL DEFAULT NULL ,
`adopted_license`  tinyint(1) NULL DEFAULT NULL ,
`license`  text CHARACTER SET utf8 COLLATE utf8_general_ci NULL ,
`other_license`  text CHARACTER SET utf8 COLLATE utf8_general_ci NULL ,
`allow_modifications`  tinyint(1) NULL DEFAULT NULL ,
PRIMARY KEY (`id`),
FOREIGN KEY (`type_id`) REFERENCES `deliverable_types` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
FOREIGN KEY (`modified_by`) REFERENCES `users` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
FOREIGN KEY (`key_output_id`) REFERENCES `crp_cluster_key_outputs` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT,
FOREIGN KEY (`outcome_id`) REFERENCES `crp_program_outcomes` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT,
FOREIGN KEY (`deliverable_id`) REFERENCES `deliverables` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT

)ENGINE=InnoDB
;


ALTER TABLE `deliverables_info`
ADD COLUMN `id_phase`  bigint(20) NULL AFTER `deliverable_id`;

ALTER TABLE `deliverables_info` ADD FOREIGN KEY (`id_phase`) REFERENCES `phases` (`id`);


INSERT INTO deliverables_info (
  title,
  description,
  type_id,
  type_other,
  new_expected_year,
  YEAR,
  STATUS,
  status_description,
  modified_by,
  modification_justification,
  id_phase,
  outcome_id,
  deliverable_id,
  key_output_id,
  cross_cutting_gender,
  cross_cutting_youth,
  cross_cutting_capacity,
  cross_cutting_na,
  adopted_license,
  license,
  other_license,
  allow_modifications
) SELECT
  d.title,
  d.description,
  d.type_id,
  d.type_other,
  d.new_expected_year,
  d. YEAR,
  d. STATUS,
  d.status_description,
  d.modified_by,
  d.modification_justification,
  p.id_phase,
  d.outcome_id,
  d.id,
  d.key_output_id,
  d.cross_cutting_gender,
  d.cross_cutting_youth,
  d.cross_cutting_capacity,
  d.cross_cutting_na,
  d.adopted_license,
  d.license,
  d.other_license,
  d.allow_modifications
FROM
  deliverables d
INNER JOIN project_phases p ON d.project_id = p.project_id
WHERE
  d.project_id IS NOT NULL
UNION
  SELECT
    d.title,
    d.description,
    d.type_id,
    d.type_other,
    d.new_expected_year,
    d. YEAR,
    d. STATUS,
    d.status_description,
    d.modified_by,
    d.modification_justification,
    p.id,
    d.outcome_id,
    d.id,
    d.key_output_id,
    d.cross_cutting_gender,
    d.cross_cutting_youth,
    d.cross_cutting_capacity,
    d.cross_cutting_na,
    d.adopted_license,
    d.license,
    d.other_license,
    d.allow_modifications
  FROM
    deliverables d
  INNER JOIN phases p ON d.crp_id = p.crp_id
  WHERE
    d.crp_id IS NOT NULL;
    
    ALTER TABLE `deliverables` DROP FOREIGN KEY `deliverables_ibfk_1`;

ALTER TABLE `deliverables` DROP FOREIGN KEY `deliverables_ibfk_4`;

ALTER TABLE `deliverables` DROP FOREIGN KEY `deliverables_ibfk_5`;

ALTER TABLE `deliverables` DROP FOREIGN KEY `deliverables_ibfk_6`;

ALTER TABLE `deliverables`
DROP COLUMN `title`,
DROP COLUMN `description`,
DROP COLUMN `type_id`,
DROP COLUMN `type_other`,
DROP COLUMN `new_expected_year`,
DROP COLUMN `year`,
DROP COLUMN `status`,
DROP COLUMN `status_description`,
DROP COLUMN `modified_by`,
DROP COLUMN `modification_justification`,
DROP COLUMN `outcome_id`,
DROP COLUMN `key_output_id`,
DROP COLUMN `create_date`,
DROP COLUMN `cross_cutting_gender`,
DROP COLUMN `cross_cutting_youth`,
DROP COLUMN `cross_cutting_capacity`,
DROP COLUMN `cross_cutting_na`,
DROP COLUMN `adopted_license`,
DROP COLUMN `license`,
DROP COLUMN `other_license`,
DROP COLUMN `allow_modifications`;

-- deliverable_users
CREATE TEMPORARY TABLE
IF NOT EXISTS table_deliverable_users AS (SELECT * FROM deliverable_users);

TRUNCATE TABLE deliverable_users;


ALTER TABLE `deliverable_users`
ADD COLUMN `id_phase`  bigint(20) NULL AFTER `deliverable_id`;

ALTER TABLE `deliverable_users` ADD FOREIGN KEY (`id_phase`) REFERENCES `phases` (`id`);

INSERT INTO deliverable_users (
deliverable_id,
first_name,
last_name,
element_id,

id_phase
) SELECT 

t2.deliverable_id,
t2.first_name,
t2.last_name,
t2.element_id,ph.id
FROM
  table_deliverable_users t2
 left join deliverables d on d.id=t2.deliverable_id
left JOIN project_phases pp ON pp.project_id = d.project_id
left JOIN phases ph ON ph.id = pp.id_phase
;

-- deliverable_quality_checks

CREATE TEMPORARY TABLE
IF NOT EXISTS table_deliverable_quality_checks AS (SELECT * FROM deliverable_quality_checks);

TRUNCATE TABLE deliverable_quality_checks;


ALTER TABLE `deliverable_quality_checks`
ADD COLUMN `id_phase`  bigint(20) NULL AFTER `deliverable_id`;

ALTER TABLE `deliverable_quality_checks` ADD FOREIGN KEY (`id_phase`) REFERENCES `phases` (`id`);

INSERT INTO deliverable_quality_checks (
deliverable_id,
quality_assurance,
file_assurance,
link_assurance,
data_dictionary,
file_dictionary,
link_dictionary,
data_tools,
file_tools,
link_tools,
is_active,
active_since,
modified_by,
created_by,
modification_justification,

id_phase
) SELECT 

t2.deliverable_id,
t2.quality_assurance,
t2.file_assurance,
t2.link_assurance,
t2.data_dictionary,
t2.file_dictionary,
t2.link_dictionary,
t2.data_tools,
t2.file_tools,
t2.link_tools,
t2.is_active,
t2.active_since,
t2.modified_by,
t2.created_by,
t2.modification_justification,
ph.id
FROM
  table_deliverable_quality_checks t2
 left join deliverables d on d.id=t2.deliverable_id
left JOIN project_phases pp ON pp.project_id = d.project_id
left JOIN phases ph ON ph.id = pp.id_phase
;



-- deliverable_publications_metada

CREATE TEMPORARY TABLE
IF NOT EXISTS table_deliverable_publications_metada AS (SELECT * FROM deliverable_publications_metada);

TRUNCATE TABLE deliverable_publications_metada;


ALTER TABLE `deliverable_publications_metada`
ADD COLUMN `id_phase`  bigint(20) NULL AFTER `deliverable_id`;

ALTER TABLE `deliverable_publications_metada` ADD FOREIGN KEY (`id_phase`) REFERENCES `phases` (`id`);

INSERT INTO deliverable_publications_metada (
deliverable_id,
volume,
issue,
pages,
journal,
isi_publication,
nasr,
co_author,
publication_acknowledge,


id_phase
) SELECT 

t2.deliverable_id,
t2.volume,
t2.issue,
t2.pages,
t2.journal,
t2.isi_publication,
t2.nasr,
t2.co_author,
t2.publication_acknowledge,

ph.id
FROM
  table_deliverable_publications_metada t2
 left join deliverables d on d.id=t2.deliverable_id
left JOIN project_phases pp ON pp.project_id = d.project_id
left JOIN phases ph ON ph.id = pp.id_phase
;

-- deliverable_programs

CREATE TEMPORARY TABLE
IF NOT EXISTS table_deliverable_programs AS (SELECT * FROM deliverable_programs);

TRUNCATE TABLE deliverable_programs;


ALTER TABLE `deliverable_programs`
ADD COLUMN `id_phase`  bigint(20) NULL AFTER `deliverable_id`;

ALTER TABLE `deliverable_programs` ADD FOREIGN KEY (`id_phase`) REFERENCES `phases` (`id`);

INSERT INTO deliverable_programs (
deliverable_id,
ip_program_id,
id_phase
) SELECT 

t2.deliverable_id,
t2.ip_program_id,
ph.id
FROM
  table_deliverable_programs t2
 left join deliverables d on d.id=t2.deliverable_id
left JOIN project_phases pp ON pp.project_id = d.project_id
left JOIN phases ph ON ph.id = pp.id_phase
;

-- deliverable_partnerships

CREATE TEMPORARY TABLE
IF NOT EXISTS table_deliverable_partnerships AS (SELECT * FROM deliverable_partnerships);

TRUNCATE TABLE deliverable_partnerships;


ALTER TABLE `deliverable_partnerships`
ADD COLUMN `id_phase`  bigint(20) NULL AFTER `deliverable_id`;

ALTER TABLE `deliverable_partnerships` ADD FOREIGN KEY (`id_phase`) REFERENCES `phases` (`id`);

INSERT INTO deliverable_partnerships (
deliverable_id,
partner_person_id,
partner_type,
is_active,
active_since,
created_by,
modified_by,
modification_justification,
division_id,

id_phase
) SELECT 

t2.deliverable_id,
t2.partner_person_id,
t2.partner_type,
t2.is_active,
t2.active_since,
t2.created_by,
t2.modified_by,
t2.modification_justification,
t2.division_id,
ph.id
FROM
  table_deliverable_partnerships t2
 left join deliverables d on d.id=t2.deliverable_id
left JOIN project_phases pp ON pp.project_id = d.project_id
left JOIN phases ph ON ph.id = pp.id_phase
;


-- deliverable_metadata_elements

CREATE TEMPORARY TABLE
IF NOT EXISTS table_deliverable_metadata_elements AS (SELECT * FROM deliverable_metadata_elements);

TRUNCATE TABLE deliverable_metadata_elements;


ALTER TABLE `deliverable_metadata_elements`
ADD COLUMN `id_phase`  bigint(20) NULL AFTER `deliverable_id`;

ALTER TABLE `deliverable_metadata_elements` ADD FOREIGN KEY (`id_phase`) REFERENCES `phases` (`id`);

INSERT INTO deliverable_metadata_elements (
deliverable_id,
element_id,
element_value,
hide,


id_phase
) SELECT 

t2.deliverable_id,
t2.element_id,
t2.element_value,
t2.hide,

ph.id
FROM
  table_deliverable_metadata_elements t2
 left join deliverables d on d.id=t2.deliverable_id
left JOIN project_phases pp ON pp.project_id = d.project_id
left JOIN phases ph ON ph.id = pp.id_phase
;

-- deliverable_leaders

CREATE TEMPORARY TABLE
IF NOT EXISTS table_deliverable_leaders AS (SELECT * FROM deliverable_leaders);

TRUNCATE TABLE deliverable_leaders;


ALTER TABLE `deliverable_leaders`
ADD COLUMN `id_phase`  bigint(20) NULL AFTER `deliverable_id`;

ALTER TABLE `deliverable_leaders` ADD FOREIGN KEY (`id_phase`) REFERENCES `phases` (`id`);

INSERT INTO deliverable_leaders (
deliverable_id,
instituion_id,



id_phase
) SELECT 

t2.deliverable_id,
t2.instituion_id,
ph.id
FROM
  table_deliverable_leaders t2
 left join deliverables d on d.id=t2.deliverable_id
left JOIN project_phases pp ON pp.project_id = d.project_id
left JOIN phases ph ON ph.id = pp.id_phase
;


-- deliverable_gender_levels

CREATE TEMPORARY TABLE
IF NOT EXISTS table_deliverable_gender_levels AS (SELECT * FROM deliverable_gender_levels);

TRUNCATE TABLE deliverable_gender_levels;


ALTER TABLE `deliverable_gender_levels`
ADD COLUMN `id_phase`  bigint(20) NULL AFTER `deliverable_id`;

ALTER TABLE `deliverable_gender_levels` ADD FOREIGN KEY (`id_phase`) REFERENCES `phases` (`id`);

INSERT INTO deliverable_gender_levels (
deliverable_id,
gender_level,
is_active,
active_since,
created_by,
modified_by,
modification_justification,

id_phase
) SELECT 
t2.deliverable_id,
t2.gender_level,
t2.is_active,
t2.active_since,
t2.created_by,
t2.modified_by,
t2.modification_justification,
ph.id
FROM
  table_deliverable_gender_levels t2
 left join deliverables d on d.id=t2.deliverable_id
left JOIN project_phases pp ON pp.project_id = d.project_id
left JOIN phases ph ON ph.id = pp.id_phase
;

-- deliverable_funding_sources

CREATE TEMPORARY TABLE
IF NOT EXISTS table_deliverable_funding_sources AS (SELECT * FROM deliverable_funding_sources);

TRUNCATE TABLE deliverable_funding_sources;


ALTER TABLE `deliverable_funding_sources`
ADD COLUMN `id_phase`  bigint(20) NULL AFTER `deliverable_id`;

ALTER TABLE `deliverable_funding_sources` ADD FOREIGN KEY (`id_phase`) REFERENCES `phases` (`id`);

INSERT INTO deliverable_funding_sources (
deliverable_id,
funding_source_id,
is_active,
active_since,
created_by,
modified_by,
modification_justification,


id_phase
) SELECT 
t2.deliverable_id,
t2.funding_source_id,
t2.is_active,
t2.active_since,
t2.created_by,
t2.modified_by,
t2.modification_justification,

ph.id
FROM
  table_deliverable_funding_sources t2
 left join deliverables d on d.id=t2.deliverable_id
left JOIN project_phases pp ON pp.project_id = d.project_id
left JOIN phases ph ON ph.id = pp.id_phase
;



-- deliverable_dissemination

CREATE TEMPORARY TABLE
IF NOT EXISTS table_deliverable_dissemination AS (SELECT * FROM deliverable_dissemination);

TRUNCATE TABLE deliverable_dissemination;


ALTER TABLE `deliverable_dissemination`
ADD COLUMN `id_phase`  bigint(20) NULL AFTER `deliverable_id`;

ALTER TABLE `deliverable_dissemination` ADD FOREIGN KEY (`id_phase`) REFERENCES `phases` (`id`);

INSERT INTO deliverable_dissemination (
deliverable_id,
is_open_access,
intellectual_property,
limited_exclusivity,
restricted_use_agreement,
restricted_access_until,
effective_date_restriction,
restricted_embargoed,
not_disseminated,
already_disseminated,
dissemination_channel,
dissemination_URL,
dissemination_channel_name,
synced,


id_phase
) SELECT 
t2.deliverable_id,
t2.is_open_access,
t2.intellectual_property,
t2.limited_exclusivity,
t2.restricted_use_agreement,
t2.restricted_access_until,
t2.effective_date_restriction,
t2.restricted_embargoed,
t2.not_disseminated,
t2.already_disseminated,
t2.dissemination_channel,
t2.dissemination_URL,
t2.dissemination_channel_name,
t2.synced,

ph.id
FROM
  table_deliverable_dissemination t2
 left join deliverables d on d.id=t2.deliverable_id
left JOIN project_phases pp ON pp.project_id = d.project_id
left JOIN phases ph ON ph.id = pp.id_phase
;


-- deliverable_data_sharing


CREATE TEMPORARY TABLE
IF NOT EXISTS table_deliverable_data_sharing AS (SELECT * FROM deliverable_data_sharing);

TRUNCATE TABLE deliverable_data_sharing;


ALTER TABLE `deliverable_data_sharing`
ADD COLUMN `id_phase`  bigint(20) NULL AFTER `deliverable_id`;

ALTER TABLE `deliverable_data_sharing` ADD FOREIGN KEY (`id_phase`) REFERENCES `phases` (`id`);

INSERT INTO deliverable_data_sharing (
deliverable_id,
institutional_repository,
link_institutional_repository,
ccfas_host_greater,
link_ccfas_host_greater,
ccfas_host_smaller,


id_phase
) SELECT 
t2.deliverable_id,
t2.institutional_repository,
t2.link_institutional_repository,
t2.ccfas_host_greater,
t2.link_ccfas_host_greater,
t2.ccfas_host_smaller,


ph.id
FROM
  table_deliverable_data_sharing t2
 left join deliverables d on d.id=t2.deliverable_id
left JOIN project_phases pp ON pp.project_id = d.project_id
left JOIN phases ph ON ph.id = pp.id_phase
;

-- deliverable_data_sharing_file

CREATE TEMPORARY TABLE
IF NOT EXISTS table_deliverable_data_sharing_file AS (SELECT * FROM deliverable_data_sharing_file);

TRUNCATE TABLE deliverable_data_sharing_file;


ALTER TABLE `deliverable_data_sharing_file`
ADD COLUMN `id_phase`  bigint(20) NULL AFTER `deliverable_id`;

ALTER TABLE `deliverable_data_sharing_file` ADD FOREIGN KEY (`id_phase`) REFERENCES `phases` (`id`);

INSERT INTO deliverable_data_sharing_file (
deliverable_id,
type_id,
file_id,
external_file,



id_phase
) SELECT 
t2.deliverable_id,
t2.type_id,
t2.file_id,
t2.external_file,

ph.id
FROM
  table_deliverable_data_sharing_file t2
 left join deliverables d on d.id=t2.deliverable_id
left JOIN project_phases pp ON pp.project_id = d.project_id
left JOIN phases ph ON ph.id = pp.id_phase
;


-- deliverable_crps

CREATE TEMPORARY TABLE
IF NOT EXISTS table_deliverable_crps AS (SELECT * FROM deliverable_crps);

TRUNCATE TABLE deliverable_crps;


ALTER TABLE `deliverable_crps`
ADD COLUMN `id_phase`  bigint(20) NULL AFTER `deliverable_id`;

ALTER TABLE `deliverable_crps` ADD FOREIGN KEY (`id_phase`) REFERENCES `phases` (`id`);

INSERT INTO deliverable_crps (
deliverable_id,
crp_id,
crp_program,

id_phase
) SELECT 
t2.deliverable_id,
t2.crp_id,
t2.crp_program,
ph.id
FROM
  table_deliverable_crps t2
 left join deliverables d on d.id=t2.deliverable_id
left JOIN project_phases pp ON pp.project_id = d.project_id
left JOIN phases ph ON ph.id = pp.id_phase
;


-- deliverable_activities

CREATE TEMPORARY TABLE
IF NOT EXISTS table_deliverable_activities AS (SELECT * FROM deliverable_activities);

TRUNCATE TABLE deliverable_activities;


ALTER TABLE `deliverable_activities`
ADD COLUMN `id_phase`  bigint(20) NULL AFTER `deliverable_id`;

ALTER TABLE `deliverable_activities` ADD FOREIGN KEY (`id_phase`) REFERENCES `phases` (`id`);

INSERT INTO deliverable_activities (
deliverable_id,
activity_id,
is_active,
active_since,
created_by,
modified_by,
modification_justification,

id_phase
) SELECT 
t2.deliverable_id,
t2.activity_id,
t2.is_active,
t2.active_since,
t2.created_by,
t2.modified_by,
t2.modification_justification,

ph.id
FROM
  table_deliverable_activities t2
 left join deliverables d on d.id=t2.deliverable_id
left JOIN project_phases pp ON pp.project_id = d.project_id
left JOIN phases ph ON ph.id = pp.id_phase
;

