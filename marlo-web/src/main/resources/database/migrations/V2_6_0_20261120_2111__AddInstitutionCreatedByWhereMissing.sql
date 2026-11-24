-- Institutions.hbm.xml now maps institutions.created_by, so MARLO writes the approver when it accepts a partner request
-- (the column is NOT NULL without a default in the real databases, and an insert that omits it fails on a MySQL server
-- in strict mode). No migration ever created the column: it was added directly to the real databases, together with
-- the CLARISA synchronization. A database built from the migrations alone would therefore not have it, and every
-- query Hibernate runs on institutions would fail once the column is mapped.
--
-- The column is added only where it is missing. It is nullable here: a database that gets it from this migration has
-- no CLARISA synchronization filling it, and the rows it already holds have no creator to record. The real databases
-- keep their column as it is.
SET @has_created_by = (SELECT COUNT(*) FROM information_schema.COLUMNS
  WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 'institutions' AND COLUMN_NAME = 'created_by');
SET @has_institutions = (SELECT COUNT(*) FROM information_schema.TABLES
  WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 'institutions');
SET @add_created_by = IF(@has_institutions = 1 AND @has_created_by = 0,
  'ALTER TABLE institutions ADD COLUMN created_by bigint NULL',
  'SELECT 1');
PREPARE add_created_by FROM @add_created_by;
EXECUTE add_created_by;
DEALLOCATE PREPARE add_created_by;
