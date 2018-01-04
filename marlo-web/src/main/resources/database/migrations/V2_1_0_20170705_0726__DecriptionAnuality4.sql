set global tmp_table_size=268435456;
CREATE TEMPORARY TABLE
IF NOT EXISTS table_audt AS (SELECT * FROM auditlog);

TRUNCATE TABLE auditlog;



ALTER TABLE `auditlog`
ADD COLUMN `id_phase`  bigint(20) NULL AFTER `modification_justification`;

