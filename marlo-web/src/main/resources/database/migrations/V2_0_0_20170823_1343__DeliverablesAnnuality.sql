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

)
;


ALTER TABLE `deliverables_info`
ADD COLUMN `id_phase`  bigint(20) NULL AFTER `modification_justification`;

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
ADD COLUMN `id_phase`  bigint(20) NULL AFTER `modification_justification`;

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
t2.element_idph.id
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
ADD COLUMN `id_phase`  bigint(20) NULL AFTER `modification_justification`;

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

-- deliverable_quality_answers 

CREATE TEMPORARY TABLE
IF NOT EXISTS table_deliverable_quality_answers AS (SELECT * FROM deliverable_quality_answers);

TRUNCATE TABLE deliverable_quality_answers;


ALTER TABLE `deliverable_quality_answers`
ADD COLUMN `id_phase`  bigint(20) NULL AFTER `modification_justification`;

ALTER TABLE `deliverable_quality_answers` ADD FOREIGN KEY (`id_phase`) REFERENCES `phases` (`id`);

INSERT INTO deliverable_quality_answers (
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

name,
is_active,
modified_by,
created_by,
modification_justification,
active_since,

ph.id
FROM
  table_deliverable_quality_answers t2
 left join deliverables d on d.id=t2.deliverable_id
left JOIN project_phases pp ON pp.project_id = d.project_id
left JOIN phases ph ON ph.id = pp.id_phase
;