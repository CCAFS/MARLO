SET @preparedStatement = (SELECT IF(
    (SELECT COUNT(*)
        FROM INFORMATION_SCHEMA.COLUMNS
        WHERE  table_name = 'auditlog'
        AND table_schema = DATABASE()
        AND column_name = 'id_phase'
    ) > 0,
    "SELECT 1",
    "ALTER TABLE `auditlog` ADD `id_phase` BIGINT(20) NULL ;"
));

PREPARE alterIfNotExists FROM @preparedStatement;
EXECUTE alterIfNotExists;
DEALLOCATE PREPARE alterIfNotExists;

