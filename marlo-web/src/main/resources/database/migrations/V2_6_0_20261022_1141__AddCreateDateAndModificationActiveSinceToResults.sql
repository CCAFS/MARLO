-- Deliverables, innovations and OICRs (project_expected_studies): active_since becomes the date of the last
-- modification of the record, and the creation date is kept in create_date.
--
-- Until now active_since meant different things per table. Hibernate never writes it on update
-- (update="false"), so it only moved where the column has ON UPDATE CURRENT_TIMESTAMP: deliverables and
-- project_innovations in the existing databases, not project_expected_studies. No migration ever added that
-- ON UPDATE, so a database built from the migrations alone does not have it. From this release the
-- application sets active_since on every save of these three entities (AuditColumnHibernateListener and the
-- save actions), and the mappings no longer exclude it from updates.
--
-- create_date is added with DEFAULT NULL first and gets DEFAULT CURRENT_TIMESTAMP afterwards: adding it with
-- the default straight away would stamp the time of this migration on every existing row.

-- OICRs: until this release active_since was their creation date, and this is the last moment it still is.
-- active_since = active_since keeps an ON UPDATE CURRENT_TIMESTAMP from stamping the time of this migration.
ALTER TABLE `project_expected_studies` ADD COLUMN `create_date` TIMESTAMP NULL DEFAULT NULL AFTER `active_since`;
UPDATE `project_expected_studies` SET `create_date` = `active_since`, `active_since` = `active_since`
WHERE `create_date` IS NULL;
ALTER TABLE `project_expected_studies` MODIFY COLUMN `create_date` TIMESTAMP NULL DEFAULT CURRENT_TIMESTAMP;

-- Innovations: active_since was already overwritten on every save, so the creation date of the existing
-- innovations is unknown and stays NULL. New ones get it from the default.
ALTER TABLE `project_innovations` ADD COLUMN `create_date` TIMESTAMP NULL DEFAULT NULL AFTER `active_since`;
ALTER TABLE `project_innovations` MODIFY COLUMN `create_date` TIMESTAMP NULL DEFAULT CURRENT_TIMESTAMP;

-- Same active_since definition in the three tables, so a direct SQL update also moves it. The application
-- writes the column explicitly, so this does not change what a save from MARLO stores.
ALTER TABLE `deliverables`
  MODIFY COLUMN `active_since` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP;
ALTER TABLE `project_innovations`
  MODIFY COLUMN `active_since` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP;
ALTER TABLE `project_expected_studies`
  MODIFY COLUMN `active_since` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP;
