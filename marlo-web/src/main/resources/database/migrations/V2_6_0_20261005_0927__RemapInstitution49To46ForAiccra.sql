-- Institution 49 ('Alliance of Bioversity and CIAT - Headquarter (Bioversity International)' in CLARISA)
-- carries an empty name and an empty acronym in MARLO's institutions table, and was deactivated on
-- 2025-07-02. It is the only row out of 11,829 with a blank name, so wherever AICCRA data still points at
-- it the screens, the Reporting Summary and the v2 REST payloads render a nameless institution. This
-- migration moves AICCRA's references to institution 46 ('Alliance ... - Regional Hub (CIAT)').
--
-- Scope is deliberately AICCRA only (global_unit_id = 45). Institution 49 holds 19,931 further rows that
-- belong to CCAFS, A4NH, WLE, FTA, PIM, BigData, RTB and the other CRPs; those are somebody else's data
-- and closed phases, and they are not touched here.
--
-- Measured on a production copy (aiccradb2, 2026-09-23) the AICCRA footprint is 467 rows:
--
--   deliverable_affiliations       279 rows   14 deliverables
--   funding_source_institutions    181 rows   10 funding sources
--   project_partners                 6 rows    1 project (102082), all already inactive
--   liaison_institutions             1 row     id 358 -- NOT part of this migration, see note 4
--
-- Four things this file does that the statements alone do not explain:
--
-- 1. The work table is the audit trail and the rollback path. Every row that will be written is recorded
--    first, with its previous institution_id, is_active and active_since. It is also a technical
--    necessity: MySQL forbids a subquery on the table being updated, and the rule that decides between
--    remapping and deactivating a deliverable affiliation has to read deliverable_affiliations itself.
--    Deciding once, into a separate table, is what makes the UPDATEs expressible and deterministic.
--
-- 2. 53 affiliations are deactivated instead of remapped. Those rows (deliverables 26459, 25734 and
--    26852) sit in a deliverable+phase that already carries an active affiliation to institution 46.
--    Remapping them would leave the same institution listed twice for one deliverable, with no unique
--    index to stop it. Nothing renders that duplicate today -- deliverable_affiliations has no read path
--    in the application: every usage in marlo-web is inside the two Web of Science write paths, and no
--    DTO, mapper, Pentaho report or database view selects from it. The duplicate would only surface in
--    an external export or in whatever reads this table next, which is precisely the kind of latent
--    inconsistency worth not creating. The rows keep institution_id = 49 and keep their
--    institution_name_web_of_science, which is the evidence of why the row ever existed.
--
-- 3. deliverable_affiliations.active_since is ON UPDATE CURRENT_TIMESTAMP, so every row this migration
--    writes loses its original stamp. That is why the work table stores it. project_partners.active_since
--    has no ON UPDATE clause and funding_source_institutions has no audit columns, so neither is affected.
--
-- 4. liaison_institutions row 358 is left alone on purpose. It is not a mapping error: it is AICCRA's own
--    liaison entry, named 'Bioversity International' in its own name column, already inactive, and AICCRA
--    has no liaison row for institution 46. Pointing it at 46 would produce a liaison institution called
--    'Bioversity International' backed by CIAT, which is worse than what is there now.
--
-- ENVIRONMENT ASSUMPTION -- global_unit_id 45 and institution ids 46 and 49 are hardcoded here, and
-- migrations only seed global_units up to id 28. In a database where AICCRA (45) does not exist, every
-- statement below selects and updates 0 rows and MySQL reports success: the migration passes without
-- doing anything. Nothing here tightens a constraint, so there is no half-applied state and no risk of
-- blocking later migrations. Verify the row counts against the target database before and after running.
--
-- 5. Re-run safety is mandatory here, not a nicety. MarloFlywayConfiguration calls repair() on every
--    startup before migrate(), which clears a failed migration from the schema history, so a migration
--    that dies halfway is re-run from the top on the next boot. Hence CREATE TABLE IF NOT EXISTS, the
--    unique key with INSERT IGNORE, and the WHERE institution_id = 49 guard on every UPDATE: a second
--    run records nothing new and writes nothing that was already written.
--
-- 6. Every statement only acts while institution 49 still has an empty name. That empty name is what
--    identifies the record this migration is meant for: it is the only institution with a blank name, and
--    it was blanked on purpose on 2025-07-02. If a database holds a named institution under id 49 -- a
--    different record, or this one with its CLARISA name restored -- remapping its references to 46 would
--    be wrong, so every INSERT and UPDATE below is guarded by the same EXISTS check and affects 0 rows.
--    The guard fails silently by design: a hard error here would fail Flyway on every startup of every
--    environment where id 49 is named. Check the "before" counts first to tell "nothing to do" from
--    "guard not met".
--
-- 7. Every statement also requires global unit 45 to exist and to be AICCRA (acronym 'AICCRA'). Migrations
--    only seed global_units up to id 28, so on a clean database, or one where id 45 belongs to another
--    program, this guard is not met and every statement affects 0 rows without raising an error. Like
--    note 6, it fails silently so that no environment's startup is blocked by it.
--
-- 8. Every statement also requires institution 46 to exist. funding_source_institutions and
--    project_partners carry a foreign key to institutions, so remapping onto a missing 46 would fail with
--    a constraint error. Because MarloFlywayConfiguration runs repair() and migrate() on every startup,
--    that error would repeat on every boot of that environment. With this guard, a database without 46
--    records nothing and updates nothing instead.
--
-- SCOPE DECISION -- validated with Cristian Gamboa (CLARISA) on 2026-10-05: for now only institution 49
-- is remapped. Institutions 7320 and 4086, also listed in A2-2510, are out of scope for this migration.
--
-- ORDER OF OPERATIONS -- run this AFTER the Web of Science ingestion is fixed, not before. Two write
-- paths resolve an affiliation's institution from the CLARISA code without checking isActive, and CLARISA
-- code 49 is valid and active, so both undo this migration in different ways:
--
--   * DeliverableMetadataByWOS matches an existing row by external source + affiliation text, then
--     overwrites its institution and calls setActive(true) -- it puts institution 49 straight back on the
--     rows this migration remapped or deactivated.
--   * DeliverablesItem (the superadmin bulk synchronization) matches by ACTIVE affiliation + institution
--     id. After the remap it finds nothing for code 49, so it creates a NEW row on institution 49 and
--     replicates it forward -- leaving the deliverable with both the remapped 46 row and a fresh 49 one.
--
-- Running this migration first does no damage that a second run cannot redo, but it will be silently
-- undone, and in the bulk-synchronization case it leaves behind exactly the duplicate that note 2 avoids.

CREATE TABLE IF NOT EXISTS `institution_remap_49_46_aiccra` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `source_table` varchar(64) NOT NULL,
  `record_id` bigint NOT NULL,
  `id_phase` bigint DEFAULT NULL,
  `old_institution_id` bigint NOT NULL,
  `old_is_active` tinyint(1) DEFAULT NULL,
  `old_active_since` timestamp NULL DEFAULT NULL,
  `planned_action` varchar(16) NOT NULL,
  `applied_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_remap_source_record` (`source_table`, `record_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- Expected: 279 rows, of which 53 are DEACTIVATE and 226 are REMAP.
INSERT IGNORE INTO `institution_remap_49_46_aiccra`
  (`source_table`, `record_id`, `id_phase`, `old_institution_id`, `old_is_active`, `old_active_since`, `planned_action`)
SELECT 'deliverable_affiliations', a.id, a.id_phase, a.institution_id, a.is_active, a.active_since,
  CASE
    WHEN a.is_active = 1 AND EXISTS (
      SELECT 1 FROM deliverable_affiliations b
      WHERE b.deliverable_id = a.deliverable_id AND b.id_phase = a.id_phase
        AND b.institution_id = 46 AND b.is_active = 1
    ) THEN 'DEACTIVATE'
    ELSE 'REMAP'
  END
FROM deliverable_affiliations a
JOIN phases p ON p.id = a.id_phase
WHERE a.institution_id = 49 AND p.global_unit_id = 45
  AND EXISTS (SELECT 1 FROM institutions i WHERE i.id = 49 AND TRIM(COALESCE(i.name, '')) = '')
  AND EXISTS (SELECT 1 FROM global_units g WHERE g.id = 45 AND g.acronym = 'AICCRA')
  AND EXISTS (SELECT 1 FROM institutions i46 WHERE i46.id = 46);

-- Expected: 181 rows, all REMAP. Institution 46 is absent from every AICCRA funding source, so none of
-- these can collide.
INSERT IGNORE INTO `institution_remap_49_46_aiccra`
  (`source_table`, `record_id`, `id_phase`, `old_institution_id`, `old_is_active`, `old_active_since`, `planned_action`)
SELECT 'funding_source_institutions', f.id, f.id_phase, f.institution_id, NULL, NULL, 'REMAP'
FROM funding_source_institutions f
JOIN phases p ON p.id = f.id_phase
WHERE f.institution_id = 49 AND p.global_unit_id = 45
  AND EXISTS (SELECT 1 FROM institutions i WHERE i.id = 49 AND TRIM(COALESCE(i.name, '')) = '')
  AND EXISTS (SELECT 1 FROM global_units g WHERE g.id = 45 AND g.acronym = 'AICCRA')
  AND EXISTS (SELECT 1 FROM institutions i46 WHERE i46.id = 46);

-- Expected: 6 rows, all REMAP. Project 102082 has no partner row for institution 46 in any phase.
INSERT IGNORE INTO `institution_remap_49_46_aiccra`
  (`source_table`, `record_id`, `id_phase`, `old_institution_id`, `old_is_active`, `old_active_since`, `planned_action`)
SELECT 'project_partners', t.id, t.id_phase, t.institution_id, t.is_active, t.active_since, 'REMAP'
FROM project_partners t
JOIN phases p ON p.id = t.id_phase
WHERE t.institution_id = 49 AND p.global_unit_id = 45
  AND EXISTS (SELECT 1 FROM institutions i WHERE i.id = 49 AND TRIM(COALESCE(i.name, '')) = '')
  AND EXISTS (SELECT 1 FROM global_units g WHERE g.id = 45 AND g.acronym = 'AICCRA')
  AND EXISTS (SELECT 1 FROM institutions i46 WHERE i46.id = 46);

-- Deactivate first: once the REMAP rows carry institution 46 the collision rule no longer holds.
-- Expected: 53 rows.
UPDATE deliverable_affiliations a
JOIN `institution_remap_49_46_aiccra` w
  ON w.source_table = 'deliverable_affiliations' AND w.record_id = a.id AND w.planned_action = 'DEACTIVATE'
SET a.is_active = 0
WHERE a.institution_id = 49
  AND EXISTS (SELECT 1 FROM institutions i WHERE i.id = 49 AND TRIM(COALESCE(i.name, '')) = '')
  AND EXISTS (SELECT 1 FROM global_units g WHERE g.id = 45 AND g.acronym = 'AICCRA')
  AND EXISTS (SELECT 1 FROM institutions i46 WHERE i46.id = 46);

-- Expected: 226 rows (127 active, 99 already inactive).
UPDATE deliverable_affiliations a
JOIN `institution_remap_49_46_aiccra` w
  ON w.source_table = 'deliverable_affiliations' AND w.record_id = a.id AND w.planned_action = 'REMAP'
SET a.institution_id = 46
WHERE a.institution_id = 49
  AND EXISTS (SELECT 1 FROM institutions i WHERE i.id = 49 AND TRIM(COALESCE(i.name, '')) = '')
  AND EXISTS (SELECT 1 FROM global_units g WHERE g.id = 45 AND g.acronym = 'AICCRA')
  AND EXISTS (SELECT 1 FROM institutions i46 WHERE i46.id = 46);

-- Expected: 181 rows.
UPDATE funding_source_institutions f
JOIN `institution_remap_49_46_aiccra` w
  ON w.source_table = 'funding_source_institutions' AND w.record_id = f.id AND w.planned_action = 'REMAP'
SET f.institution_id = 46
WHERE f.institution_id = 49
  AND EXISTS (SELECT 1 FROM institutions i WHERE i.id = 49 AND TRIM(COALESCE(i.name, '')) = '')
  AND EXISTS (SELECT 1 FROM global_units g WHERE g.id = 45 AND g.acronym = 'AICCRA')
  AND EXISTS (SELECT 1 FROM institutions i46 WHERE i46.id = 46);

-- Expected: 6 rows.
UPDATE project_partners t
JOIN `institution_remap_49_46_aiccra` w
  ON w.source_table = 'project_partners' AND w.record_id = t.id AND w.planned_action = 'REMAP'
SET t.institution_id = 46
WHERE t.institution_id = 49
  AND EXISTS (SELECT 1 FROM institutions i WHERE i.id = 49 AND TRIM(COALESCE(i.name, '')) = '')
  AND EXISTS (SELECT 1 FROM global_units g WHERE g.id = 45 AND g.acronym = 'AICCRA')
  AND EXISTS (SELECT 1 FROM institutions i46 WHERE i46.id = 46);
