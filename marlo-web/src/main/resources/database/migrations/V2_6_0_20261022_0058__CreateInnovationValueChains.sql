-- A2-2613: Value chain(s) of an innovation (General Information, after Innovation type).
-- * value_chains: the catalog, based on the priority value chains in the AICCRA-FSRP IV PIM. "Not value-chain
--   specific" (is_not_specific) is exclusive of every other option; "Other" (is_other) asks for the value chain in
--   project_innovation_info.other_value_chain.
-- * project_innovation_value_chains: the value chains selected for an innovation, one row per phase, replicated to
--   the next phases by ProjectInnovationValueChainManagerImpl like the other innovation lists.
-- Existing innovations get no value chain: the field is reported as missing in the active phase (no data migration).
--
-- Safe in every environment: tables and column are created only when missing, and the catalog rows are inserted
-- only when a row with the same name does not exist yet. No global unit, phase or user id is assumed.

CREATE TABLE IF NOT EXISTS value_chains (
  id bigint(20) NOT NULL AUTO_INCREMENT,
  name varchar(255) NOT NULL,
  sort_order int(11) NULL,
  is_not_specific tinyint(1) NOT NULL DEFAULT 0,
  is_other tinyint(1) NOT NULL DEFAULT 0,
  is_active tinyint(1) NOT NULL DEFAULT 1,
  active_since timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  created_by bigint(20) NULL,
  modified_by bigint(20) NULL,
  modification_justification text NULL,
  CONSTRAINT value_chains_pk PRIMARY KEY (id),
  CONSTRAINT value_chains_created_by_fk FOREIGN KEY (created_by) REFERENCES users(id),
  CONSTRAINT value_chains_modified_by_fk FOREIGN KEY (modified_by) REFERENCES users(id)
)
ENGINE=InnoDB
DEFAULT CHARSET=utf8mb4
COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE IF NOT EXISTS project_innovation_value_chains (
  id bigint(20) NOT NULL AUTO_INCREMENT,
  innovation_id bigint(20) NOT NULL,
  value_chain_id bigint(20) NOT NULL,
  id_phase bigint(20) NOT NULL,
  is_active tinyint(1) NOT NULL DEFAULT 1,
  active_since timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  created_by bigint(20) NULL,
  modified_by bigint(20) NULL,
  modification_justification text NULL,
  CONSTRAINT project_innovation_value_chains_pk PRIMARY KEY (id),
  CONSTRAINT project_innovation_value_chains_innovation_fk FOREIGN KEY (innovation_id) REFERENCES project_innovations(id),
  CONSTRAINT project_innovation_value_chains_value_chain_fk FOREIGN KEY (value_chain_id) REFERENCES value_chains(id),
  CONSTRAINT project_innovation_value_chains_phase_fk FOREIGN KEY (id_phase) REFERENCES phases(id),
  CONSTRAINT project_innovation_value_chains_created_by_fk FOREIGN KEY (created_by) REFERENCES users(id),
  CONSTRAINT project_innovation_value_chains_modified_by_fk FOREIGN KEY (modified_by) REFERENCES users(id),
  INDEX project_innovation_value_chains_innovation_phase_idx (innovation_id, id_phase)
)
ENGINE=InnoDB
DEFAULT CHARSET=utf8mb4
COLLATE=utf8mb4_0900_ai_ci;

INSERT INTO value_chains (name, sort_order, is_not_specific, is_other, is_active, modification_justification)
SELECT seed.name, seed.sort_order, seed.is_not_specific, seed.is_other, 1, 'A2-2613: value chain catalog'
FROM (
  SELECT 'Maize' AS name, 1 AS sort_order, 0 AS is_not_specific, 0 AS is_other
  UNION ALL SELECT 'Rice', 2, 0, 0
  UNION ALL SELECT 'Legumes', 3, 0, 0
  UNION ALL SELECT 'Horticulture', 4, 0, 0
  UNION ALL SELECT 'Livestock', 5, 0, 0
  UNION ALL SELECT 'Other', 6, 0, 1
  UNION ALL SELECT 'Not value-chain specific', 7, 1, 0
) AS seed
WHERE NOT EXISTS (SELECT 1 FROM value_chains vc WHERE vc.name = seed.name);

-- The value chain written when "Other" is selected (rule 4.1.3, max 10 words; validated in the form)
SET @has_other_value_chain = (SELECT COUNT(*) FROM information_schema.COLUMNS
  WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 'project_innovation_info' AND COLUMN_NAME = 'other_value_chain');
SET @add_other_value_chain = IF(@has_other_value_chain = 0,
  'ALTER TABLE project_innovation_info ADD COLUMN other_value_chain text NULL',
  'SELECT 1');
PREPARE add_other_value_chain FROM @add_other_value_chain;
EXECUTE add_other_value_chain;
DEALLOCATE PREPARE add_other_value_chain;

-- QA comments: the value chains (and the other value chain) are commentable wherever the innovation title already
-- is, one row per global unit, as the other innovation fields. field_name holds the label, field_description the
-- form field name the feedback module matches.
INSERT INTO feedback_qa_commentable_fields
  (section_name, section_description, parent_field_description, parent_field_identifier, field_name, field_description,
   is_active, global_unit_id)
SELECT DISTINCT 'innovation', 'Innovation', 'innovationID', NULL, seed.label, seed.field, 1, f.global_unit_id
FROM feedback_qa_commentable_fields f
INNER JOIN (
  SELECT 'Value chain(s)' AS label, 'innovation.valueChains' AS field
  UNION ALL SELECT 'Other value chain', 'innovation.projectInnovationInfo.otherValueChain'
) AS seed
WHERE f.section_name = 'innovation'
  AND f.field_description = 'innovation.projectInnovationInfo.title'
  AND NOT EXISTS (SELECT 1 FROM feedback_qa_commentable_fields x
                  WHERE x.section_name = 'innovation' AND x.field_description = seed.field
                    AND x.global_unit_id <=> f.global_unit_id);
