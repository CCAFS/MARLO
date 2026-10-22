-- A2-2613 (rule 4.2): Co-developing African institution(s) and Institutional level of a bundled innovation, for the
-- IRI 1.5 indicator (bundles co-developed by at least one Lead Center and one African regional or national
-- institution).
-- * project_innovation_co_developing_institutions: the institutions that co-developed the bundle, one row per phase,
--   replicated to the next phases by ProjectInnovationCoDeveloperManagerImpl. They are always contributing external
--   partners of the innovation: the form only offers those, and the save drops any other.
-- * project_innovation_info.institutional_level: 'regional', 'national' or 'joint'.
--
-- Specificity: innovation_co_developing_institutions_active
-- ON  -> Bundle composition asks for both fields when "Bundled innovation" is selected, and Check for missing fields
--        validates them.
-- OFF -> the fields are not shown, saved or validated; stored values are kept.
-- Rollout: ON for AICCRA-FSRP IV (global unit acronym AICCRA_III), OFF for every other unit. To turn it on for
-- another unit, set its row to 'true', for example:
--   UPDATE custom_parameters cp INNER JOIN parameters p ON p.id = cp.parameter_id
--   INNER JOIN global_units g ON g.id = cp.global_unit_id
--   SET cp.`value` = 'true'
--   WHERE p.`key` = 'innovation_co_developing_institutions_active' AND g.acronym = 'AICCRA';
--
-- Safe in every environment:
-- * tables and column are created only when missing;
-- * the parameter inserts are guarded, because parameters has no unique index on `key`;
-- * no global unit, parameter or user id is assumed: units are matched by global unit type, FSRP IV by acronym;
-- * every unit gets an explicit row ('true' or 'false'): ValidSessionCrpInterceptor only replaces the keys the new
--   unit has rows for, so a missing row would keep a stale 'true' in the session after switching units;
-- * created_by and modified_by resolve the seed account 3 when it exists and NULL when it does not.

CREATE TABLE IF NOT EXISTS project_innovation_co_developing_institutions (
  id bigint(20) NOT NULL AUTO_INCREMENT,
  innovation_id bigint(20) NOT NULL,
  institution_id bigint(20) NOT NULL,
  id_phase bigint(20) NOT NULL,
  is_active tinyint(1) NOT NULL DEFAULT 1,
  active_since timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  created_by bigint(20) NULL,
  modified_by bigint(20) NULL,
  modification_justification text NULL,
  CONSTRAINT project_innovation_co_developing_institutions_pk PRIMARY KEY (id),
  CONSTRAINT project_innovation_co_dev_innovation_fk FOREIGN KEY (innovation_id) REFERENCES project_innovations(id),
  CONSTRAINT project_innovation_co_dev_institution_fk FOREIGN KEY (institution_id) REFERENCES institutions(id),
  CONSTRAINT project_innovation_co_dev_phase_fk FOREIGN KEY (id_phase) REFERENCES phases(id),
  CONSTRAINT project_innovation_co_dev_created_by_fk FOREIGN KEY (created_by) REFERENCES users(id),
  CONSTRAINT project_innovation_co_dev_modified_by_fk FOREIGN KEY (modified_by) REFERENCES users(id),
  INDEX project_innovation_co_dev_innovation_phase_idx (innovation_id, id_phase)
)
ENGINE=InnoDB
DEFAULT CHARSET=utf8mb4
COLLATE=utf8mb4_0900_ai_ci;

SET @has_institutional_level = (SELECT COUNT(*) FROM information_schema.COLUMNS
  WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 'project_innovation_info' AND COLUMN_NAME = 'institutional_level');
SET @add_institutional_level = IF(@has_institutional_level = 0,
  'ALTER TABLE project_innovation_info ADD COLUMN institutional_level varchar(20) NULL',
  'SELECT 1');
PREPARE add_institutional_level FROM @add_institutional_level;
EXECUTE add_institutional_level;
DEALLOCATE PREPARE add_institutional_level;

INSERT INTO parameters (global_unit_type_id, `key`, `description`, `format`, default_value, category)
SELECT '1', 'innovation_co_developing_institutions_active', 'Ask a bundled innovation for its co-developing institutions and their institutional level (IRI 1.5), and validate them.', '1', 'false', '2'
WHERE NOT EXISTS (SELECT 1 FROM parameters WHERE `key` = 'innovation_co_developing_institutions_active' AND global_unit_type_id = '1');

INSERT INTO parameters (global_unit_type_id, `key`, `description`, `format`, default_value, category)
SELECT '3', 'innovation_co_developing_institutions_active', 'Ask a bundled innovation for its co-developing institutions and their institutional level (IRI 1.5), and validate them.', '1', 'false', '2'
WHERE NOT EXISTS (SELECT 1 FROM parameters WHERE `key` = 'innovation_co_developing_institutions_active' AND global_unit_type_id = '3');

INSERT INTO parameters (global_unit_type_id, `key`, `description`, `format`, default_value, category)
SELECT '4', 'innovation_co_developing_institutions_active', 'Ask a bundled innovation for its co-developing institutions and their institutional level (IRI 1.5), and validate them.', '1', 'false', '2'
WHERE NOT EXISTS (SELECT 1 FROM parameters WHERE `key` = 'innovation_co_developing_institutions_active' AND global_unit_type_id = '4');

INSERT INTO custom_parameters (parameter_id, `value`, created_by, is_active, active_since, modified_by, modification_justification, global_unit_id)
SELECT p.id, IF(g.acronym = 'AICCRA_III', 'true', 'false'), (SELECT u.id FROM users u WHERE u.id = 3), 1, NOW(),
  (SELECT u.id FROM users u WHERE u.id = 3), 'A2-2613: co-developing institutions of a bundle (IRI 1.5)', g.id
FROM global_units AS g
INNER JOIN parameters AS p ON p.global_unit_type_id = g.global_unit_type_id
WHERE p.`key` = 'innovation_co_developing_institutions_active'
  AND NOT EXISTS (SELECT 1 FROM custom_parameters cp WHERE cp.parameter_id = p.id AND cp.global_unit_id = g.id);

-- QA comments: commentable wherever the innovation title already is, one row per global unit, as the other
-- innovation fields. field_name holds the label, field_description the form field name the feedback module matches.
INSERT INTO feedback_qa_commentable_fields
  (section_name, section_description, parent_field_description, parent_field_identifier, field_name, field_description,
   is_active, global_unit_id)
SELECT DISTINCT 'innovation', 'Innovation', 'innovationID', NULL, seed.label, seed.field, 1, f.global_unit_id
FROM feedback_qa_commentable_fields f
INNER JOIN (
  SELECT 'Co-developing African institution(s)' AS label, 'innovation.coDevelopers' AS field
  UNION ALL SELECT 'Institutional level', 'innovation.projectInnovationInfo.institutionalLevel'
) AS seed
WHERE f.section_name = 'innovation'
  AND f.field_description = 'innovation.projectInnovationInfo.title'
  AND NOT EXISTS (SELECT 1 FROM feedback_qa_commentable_fields x
                  WHERE x.section_name = 'innovation' AND x.field_description = seed.field
                    AND x.global_unit_id <=> f.global_unit_id);
