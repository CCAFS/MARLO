-- A2-2433: make the AI section content per Global Unit.
--
-- ai_report_configuration had no global_unit_id, so every Global Unit with the ai_section_active specificity
-- enabled rendered the same AI tool cards. The rows seeded so far are AICCRA specific.
--
-- The column is added as NULL first so the existing rows can be backfilled, and only then set to NOT NULL:
-- MySQL cannot add a NOT NULL column with a foreign key to a table that already holds rows.

ALTER TABLE ai_report_configuration ADD global_unit_id bigint NULL AFTER id;

-- Backfill: the rows that exist today are the AICCRA tools (Report Generator, Chatbot, Innovation Metadata
-- Extractor). Global Unit 45 is AICCRA. Without this the cards would disappear from the section after deploy,
-- because the new query filters by Global Unit.
-- The EXISTS guard matters: Global Unit 45 lives only in the real MARLO databases, while the three rows are
-- seeded unconditionally by V2_6_0_20251113_1400__UpdateAITable.sql. On a database that has the rows but not
-- the Global Unit, an unguarded backfill would write an orphan 45 and the foreign key below would then fail
-- with MySQL error 1452 - leaving this migration half applied, since DDL here is not transactional.
UPDATE ai_report_configuration
SET global_unit_id = 45
WHERE global_unit_id IS NULL
	AND EXISTS (SELECT 1 FROM global_units WHERE id = 45);

-- Any row still unowned belongs to a Global Unit this database does not have. It would be unreachable by the
-- per-tenant query anyway, and it would block the NOT NULL and the foreign key below.
DELETE FROM ai_report_configuration WHERE global_unit_id IS NULL;

ALTER TABLE ai_report_configuration MODIFY global_unit_id bigint NOT NULL;

ALTER TABLE ai_report_configuration ADD CONSTRAINT ai_report_configuration_global_units_FK
	FOREIGN KEY (global_unit_id) REFERENCES global_units(id) ON DELETE RESTRICT ON UPDATE RESTRICT;

CREATE INDEX ai_report_configuration_global_unit_id_IDX ON ai_report_configuration (global_unit_id);
