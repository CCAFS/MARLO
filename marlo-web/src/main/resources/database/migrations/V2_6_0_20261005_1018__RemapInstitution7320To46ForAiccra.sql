-- Institution 7320 ('Alliance of Bioversity International & the International Center for Tropical
-- Agriculture (CIAT)', acronym 'CGIAR ALLIANCE') is a duplicate Alliance record created in July 2020 and
-- physically deleted in CLARISA since. AICCRA and AICCRA III used it as "the Alliance" everywhere --
-- deliverable partners, study and innovation partnerships, budgets, project partners, managing partners and
-- the management liaison -- while the record users are meant to select is 46 ('Alliance of Bioversity and
-- CIAT - Regional Hub (Centro Internacional de Agricultura Tropical)', 'CIAT (Alliance)'). This migration
-- moves AICCRA's and AICCRA III's ACTIVE references from 7320 to 46 and then marks 7320 inactive. A2-2510.
--
-- Decisions (2026-10-05):
--   * Scope is AICCRA (global unit 45) and AICCRA III (47). No other program references 7320 actively.
--   * Only ACTIVE rows are remapped. Inactive rows keep institution 7320. For the three tables without an
--     is_active column, "active" means the parent is active: the funding source, the expected study or the
--     innovation.
--   * Institution 7320 is marked inactive afterwards.
--   * The offices of the remapped project partners move to 46's headquarters (Palmira, Colombia). Every
--     one of them points at 7320's only office, in Italy, where 46 has no office in MARLO or in CLARISA.
--     Italy was never a real choice: it was the only office 7320 offered. Teams can pick the actual 46
--     office (Nairobi, Addis Ababa, ...) on screen afterwards, which only works once the office is one of
--     46's.
--
-- Measured on a production copy (aiccradb2, 2026-10-05), active rows in scope:
--
--   deliverable_user_partnerships          14,354
--   project_expected_study_partnerships       872
--   project_budgets                           676
--   project_expected_study_centers            377   (511 rows, 134 under an inactive study)
--   project_innovation_partnerships           424
--   project_innovation_centers                287   (294 rows, 7 under an inactive innovation)
--   project_partners                          285   (267 AICCRA + 18 AICCRA III)
--   funding_source_institutions               218
--   project_budget_executions                  96
--   crp_ppa_partners                           39   (21 AICCRA + 18 AICCRA III; the managing partners)
--   funding_sources_info.direct_donor           8
--   liaison_institutions                        2   (357 AICCRA, 395 AICCRA III; the management liaison)
--   project_partner_locations                 285   (the office of every remapped partner; moved to 46's HQ)
--
-- No collisions: institution 46 is not used by AICCRA or AICCRA III in any of these tables in the same
-- parent and phase, so a remap never lists the same institution twice. The other child tables of a partner
-- (persons, contributions, deliverable partnerships) hang off the partner row id, not the institution, and
-- are not touched. None of the remapped tables has an ON UPDATE column, so no audit stamp is lost there
-- (institutions.updated_at: see note 1).
--
-- Not touched on purpose: the staging tables of the AICCRA to STAR migration (STAR_MARLO_basic_metadata,
-- STAR_MARLO_parters), AICCRA_inno_concacts, intellectual_property_rights_institutions row 2 (no program
-- scope, custom name 'Bioversity International'), and the institution's own institutions_locations row.
--
-- Notes:
--
-- 1. The work table is the audit trail and the rollback path: every row written is recorded first with its
--    previous institution_id (for a partner office, its previous institution location and is_active; for
--    institution 7320 itself, its previous is_active). Only columns created by the migrations in this
--    repository are read: institutions.updated_at exists in the AICCRA databases but no migration creates
--    it, so reading it would fail -- and block every startup -- wherever it is absent. Where it does exist
--    it is ON UPDATE CURRENT_TIMESTAMP, so deactivating 7320 refreshes it; that stamp is not restored by a
--    rollback.
--
-- 2. Guards. Every statement that writes data only acts when ALL of these hold; otherwise it affects 0 rows
--    without an error, so no environment's startup is blocked (MarloFlywayConfiguration runs repair() and
--    migrate() on every boot):
--      * institution 7320 exists with acronym 'CGIAR ALLIANCE' -- the record this migration is meant for;
--      * institution 46 exists and has a headquarters office -- several of these tables carry a foreign key
--        to institutions, and the partner offices are moved to that headquarters. Requiring the office
--        everywhere keeps partners and their offices together: without it, partners would be remapped while
--        their offices stayed on 7320;
--      * the global unit of each row is 45 with acronym 'AICCRA' or 47 with acronym 'AICCRA_III'. Migrations
--        only seed global_units up to id 28, so on a clean database, or where 45/47 is another program,
--        nothing is selected.
--
-- 3. Institution 7320 is deactivated only while no active reference to it remains in any of the remapped
--    tables, in any program (for the three tables without is_active: none under an active funding source,
--    study or innovation). If some other program in some other database still uses it, it stays active. It
--    also requires global unit 45 'AICCRA': in a database of another program, where nothing was remapped,
--    7320 is left as it is even when nothing references it.
--
-- 4. Re-run safe: CREATE TABLE IF NOT EXISTS, the unique key with INSERT IGNORE, and the WHERE
--    institution_id = 7320 / is_active = 1 filter on every UPDATE mean a second run records nothing new and
--    writes nothing already written.
--
-- 5. Selecting 7320 again. Institution.isActive() returns a hardcoded true (A2-2535), so the inactive flag
--    does not remove 7320 from the institution pickers yet; users can still select it until A2-2535 lands.
--    Unlike institution 49, it cannot come back through the Web of Science ingest: CLARISA no longer has
--    code 7320.

CREATE TABLE IF NOT EXISTS `institution_remap_7320_46_aiccra` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `source_table` varchar(64) NOT NULL,
  `record_id` bigint NOT NULL,
  `old_institution_id` bigint DEFAULT NULL,
  `old_location_id` bigint DEFAULT NULL,
  `old_is_active` tinyint(1) DEFAULT NULL,
  `planned_action` varchar(16) NOT NULL,
  `applied_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_remap_7320_source_record` (`source_table`, `record_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ---------------------------------------------------------------------------------------------------------
-- 1. Plan: record every active row in scope
-- ---------------------------------------------------------------------------------------------------------

INSERT IGNORE INTO `institution_remap_7320_46_aiccra` (`source_table`, `record_id`, `old_institution_id`, `planned_action`)
SELECT 'deliverable_user_partnerships', x.id, x.institution_id, 'REMAP'
FROM deliverable_user_partnerships x
JOIN phases p ON p.id = x.id_phase
WHERE x.institution_id = 7320 AND x.is_active = 1
  AND p.global_unit_id IN (SELECT g.id FROM global_units g
                           WHERE (g.id = 45 AND g.acronym = 'AICCRA') OR (g.id = 47 AND g.acronym = 'AICCRA_III'))
  AND EXISTS (SELECT 1 FROM institutions i WHERE i.id = 7320 AND i.acronym = 'CGIAR ALLIANCE')
  AND EXISTS (SELECT 1 FROM institutions_locations il46 WHERE il46.institution_id = 46 AND il46.is_headquater = 1);

INSERT IGNORE INTO `institution_remap_7320_46_aiccra` (`source_table`, `record_id`, `old_institution_id`, `planned_action`)
SELECT 'project_expected_study_partnerships', x.id, x.institution_id, 'REMAP'
FROM project_expected_study_partnerships x
JOIN phases p ON p.id = x.id_phase
WHERE x.institution_id = 7320 AND x.is_active = 1
  AND p.global_unit_id IN (SELECT g.id FROM global_units g
                           WHERE (g.id = 45 AND g.acronym = 'AICCRA') OR (g.id = 47 AND g.acronym = 'AICCRA_III'))
  AND EXISTS (SELECT 1 FROM institutions i WHERE i.id = 7320 AND i.acronym = 'CGIAR ALLIANCE')
  AND EXISTS (SELECT 1 FROM institutions_locations il46 WHERE il46.institution_id = 46 AND il46.is_headquater = 1);

INSERT IGNORE INTO `institution_remap_7320_46_aiccra` (`source_table`, `record_id`, `old_institution_id`, `planned_action`)
SELECT 'project_innovation_partnerships', x.id, x.institution_id, 'REMAP'
FROM project_innovation_partnerships x
JOIN phases p ON p.id = x.id_phase
WHERE x.institution_id = 7320 AND x.is_active = 1
  AND p.global_unit_id IN (SELECT g.id FROM global_units g
                           WHERE (g.id = 45 AND g.acronym = 'AICCRA') OR (g.id = 47 AND g.acronym = 'AICCRA_III'))
  AND EXISTS (SELECT 1 FROM institutions i WHERE i.id = 7320 AND i.acronym = 'CGIAR ALLIANCE')
  AND EXISTS (SELECT 1 FROM institutions_locations il46 WHERE il46.institution_id = 46 AND il46.is_headquater = 1);

INSERT IGNORE INTO `institution_remap_7320_46_aiccra` (`source_table`, `record_id`, `old_institution_id`, `planned_action`)
SELECT 'project_partners', x.id, x.institution_id, 'REMAP'
FROM project_partners x
JOIN phases p ON p.id = x.id_phase
WHERE x.institution_id = 7320 AND x.is_active = 1
  AND p.global_unit_id IN (SELECT g.id FROM global_units g
                           WHERE (g.id = 45 AND g.acronym = 'AICCRA') OR (g.id = 47 AND g.acronym = 'AICCRA_III'))
  AND EXISTS (SELECT 1 FROM institutions i WHERE i.id = 7320 AND i.acronym = 'CGIAR ALLIANCE')
  AND EXISTS (SELECT 1 FROM institutions_locations il46 WHERE il46.institution_id = 46 AND il46.is_headquater = 1);

INSERT IGNORE INTO `institution_remap_7320_46_aiccra` (`source_table`, `record_id`, `old_institution_id`, `planned_action`)
SELECT 'project_budgets', x.id, x.institution_id, 'REMAP'
FROM project_budgets x
JOIN phases p ON p.id = x.id_phase
WHERE x.institution_id = 7320 AND x.is_active = 1
  AND p.global_unit_id IN (SELECT g.id FROM global_units g
                           WHERE (g.id = 45 AND g.acronym = 'AICCRA') OR (g.id = 47 AND g.acronym = 'AICCRA_III'))
  AND EXISTS (SELECT 1 FROM institutions i WHERE i.id = 7320 AND i.acronym = 'CGIAR ALLIANCE')
  AND EXISTS (SELECT 1 FROM institutions_locations il46 WHERE il46.institution_id = 46 AND il46.is_headquater = 1);

INSERT IGNORE INTO `institution_remap_7320_46_aiccra` (`source_table`, `record_id`, `old_institution_id`, `planned_action`)
SELECT 'project_budget_executions', x.id, x.institution_id, 'REMAP'
FROM project_budget_executions x
JOIN phases p ON p.id = x.phase_id
WHERE x.institution_id = 7320 AND x.is_active = 1
  AND p.global_unit_id IN (SELECT g.id FROM global_units g
                           WHERE (g.id = 45 AND g.acronym = 'AICCRA') OR (g.id = 47 AND g.acronym = 'AICCRA_III'))
  AND EXISTS (SELECT 1 FROM institutions i WHERE i.id = 7320 AND i.acronym = 'CGIAR ALLIANCE')
  AND EXISTS (SELECT 1 FROM institutions_locations il46 WHERE il46.institution_id = 46 AND il46.is_headquater = 1);

INSERT IGNORE INTO `institution_remap_7320_46_aiccra` (`source_table`, `record_id`, `old_institution_id`, `planned_action`)
SELECT 'crp_ppa_partners', x.id, x.institution_id, 'REMAP'
FROM crp_ppa_partners x
WHERE x.institution_id = 7320 AND x.is_active = 1
  AND x.global_unit_id IN (SELECT g.id FROM global_units g
                           WHERE (g.id = 45 AND g.acronym = 'AICCRA') OR (g.id = 47 AND g.acronym = 'AICCRA_III'))
  AND EXISTS (SELECT 1 FROM institutions i WHERE i.id = 7320 AND i.acronym = 'CGIAR ALLIANCE')
  AND EXISTS (SELECT 1 FROM institutions_locations il46 WHERE il46.institution_id = 46 AND il46.is_headquater = 1);

INSERT IGNORE INTO `institution_remap_7320_46_aiccra` (`source_table`, `record_id`, `old_institution_id`, `planned_action`)
SELECT 'funding_sources_info', x.id, x.direct_donor, 'REMAP'
FROM funding_sources_info x
JOIN phases p ON p.id = x.id_phase
WHERE x.direct_donor = 7320 AND x.is_active = 1
  AND p.global_unit_id IN (SELECT g.id FROM global_units g
                           WHERE (g.id = 45 AND g.acronym = 'AICCRA') OR (g.id = 47 AND g.acronym = 'AICCRA_III'))
  AND EXISTS (SELECT 1 FROM institutions i WHERE i.id = 7320 AND i.acronym = 'CGIAR ALLIANCE')
  AND EXISTS (SELECT 1 FROM institutions_locations il46 WHERE il46.institution_id = 46 AND il46.is_headquater = 1);

INSERT IGNORE INTO `institution_remap_7320_46_aiccra` (`source_table`, `record_id`, `old_institution_id`, `planned_action`)
SELECT 'funding_source_institutions', x.id, x.institution_id, 'REMAP'
FROM funding_source_institutions x
JOIN phases p ON p.id = x.id_phase
JOIN funding_sources fs ON fs.id = x.funding_source_id
WHERE x.institution_id = 7320 AND fs.is_active = 1
  AND p.global_unit_id IN (SELECT g.id FROM global_units g
                           WHERE (g.id = 45 AND g.acronym = 'AICCRA') OR (g.id = 47 AND g.acronym = 'AICCRA_III'))
  AND EXISTS (SELECT 1 FROM institutions i WHERE i.id = 7320 AND i.acronym = 'CGIAR ALLIANCE')
  AND EXISTS (SELECT 1 FROM institutions_locations il46 WHERE il46.institution_id = 46 AND il46.is_headquater = 1);

INSERT IGNORE INTO `institution_remap_7320_46_aiccra` (`source_table`, `record_id`, `old_institution_id`, `planned_action`)
SELECT 'project_expected_study_centers', x.id, x.institution_id, 'REMAP'
FROM project_expected_study_centers x
JOIN phases p ON p.id = x.id_phase
JOIN project_expected_studies s ON s.id = x.expected_id
WHERE x.institution_id = 7320 AND s.is_active = 1
  AND p.global_unit_id IN (SELECT g.id FROM global_units g
                           WHERE (g.id = 45 AND g.acronym = 'AICCRA') OR (g.id = 47 AND g.acronym = 'AICCRA_III'))
  AND EXISTS (SELECT 1 FROM institutions i WHERE i.id = 7320 AND i.acronym = 'CGIAR ALLIANCE')
  AND EXISTS (SELECT 1 FROM institutions_locations il46 WHERE il46.institution_id = 46 AND il46.is_headquater = 1);

INSERT IGNORE INTO `institution_remap_7320_46_aiccra` (`source_table`, `record_id`, `old_institution_id`, `planned_action`)
SELECT 'project_innovation_centers', x.id, x.institution_id, 'REMAP'
FROM project_innovation_centers x
JOIN phases p ON p.id = x.id_phase
JOIN project_innovations s ON s.id = x.project_innovation_id
WHERE x.institution_id = 7320 AND s.is_active = 1
  AND p.global_unit_id IN (SELECT g.id FROM global_units g
                           WHERE (g.id = 45 AND g.acronym = 'AICCRA') OR (g.id = 47 AND g.acronym = 'AICCRA_III'))
  AND EXISTS (SELECT 1 FROM institutions i WHERE i.id = 7320 AND i.acronym = 'CGIAR ALLIANCE')
  AND EXISTS (SELECT 1 FROM institutions_locations il46 WHERE il46.institution_id = 46 AND il46.is_headquater = 1);

INSERT IGNORE INTO `institution_remap_7320_46_aiccra` (`source_table`, `record_id`, `old_institution_id`, `planned_action`)
SELECT 'liaison_institutions', x.id, x.institution_id, 'REMAP'
FROM liaison_institutions x
WHERE x.institution_id = 7320 AND x.is_active = 1
  AND x.global_unit_id IN (SELECT g.id FROM global_units g
                           WHERE (g.id = 45 AND g.acronym = 'AICCRA') OR (g.id = 47 AND g.acronym = 'AICCRA_III'))
  AND EXISTS (SELECT 1 FROM institutions i WHERE i.id = 7320 AND i.acronym = 'CGIAR ALLIANCE')
  AND EXISTS (SELECT 1 FROM institutions_locations il46 WHERE il46.institution_id = 46 AND il46.is_headquater = 1);

-- The active offices of the partners recorded above that point at an office of 7320. 7320 has a single
-- office (Italy), and 46 has none there, so they move to 46's headquarters. A partner that already has 46's
-- headquarters as an active office gets the 7320 one deactivated instead, so the office is not listed twice.
INSERT IGNORE INTO `institution_remap_7320_46_aiccra` (`source_table`, `record_id`, `old_location_id`, `old_is_active`, `planned_action`)
SELECT 'project_partner_locations', l.id, l.institution_loc_id, l.is_active,
  CASE
    WHEN EXISTS (
      SELECT 1 FROM project_partner_locations l2
      JOIN institutions_locations hq ON hq.id = l2.institution_loc_id AND hq.institution_id = 46 AND hq.is_headquater = 1
      WHERE l2.project_partner_id = l.project_partner_id AND l2.is_active = 1
    ) THEN 'DEACTIVATE'
    ELSE 'RELOCATE'
  END
FROM project_partner_locations l
JOIN `institution_remap_7320_46_aiccra` w ON w.source_table = 'project_partners' AND w.record_id = l.project_partner_id
JOIN institutions_locations il ON il.id = l.institution_loc_id AND il.institution_id = 7320
WHERE l.is_active = 1
  AND EXISTS (SELECT 1 FROM institutions i WHERE i.id = 7320 AND i.acronym = 'CGIAR ALLIANCE')
  AND EXISTS (SELECT 1 FROM institutions_locations il46 WHERE il46.institution_id = 46 AND il46.is_headquater = 1);

-- ---------------------------------------------------------------------------------------------------------
-- 2. Remap the recorded rows to institution 46
-- ---------------------------------------------------------------------------------------------------------

UPDATE deliverable_user_partnerships x
JOIN `institution_remap_7320_46_aiccra` w ON w.source_table = 'deliverable_user_partnerships' AND w.record_id = x.id
SET x.institution_id = 46
WHERE x.institution_id = 7320
  AND EXISTS (SELECT 1 FROM institutions i WHERE i.id = 7320 AND i.acronym = 'CGIAR ALLIANCE')
  AND EXISTS (SELECT 1 FROM institutions_locations il46 WHERE il46.institution_id = 46 AND il46.is_headquater = 1);

UPDATE project_expected_study_partnerships x
JOIN `institution_remap_7320_46_aiccra` w ON w.source_table = 'project_expected_study_partnerships' AND w.record_id = x.id
SET x.institution_id = 46
WHERE x.institution_id = 7320
  AND EXISTS (SELECT 1 FROM institutions i WHERE i.id = 7320 AND i.acronym = 'CGIAR ALLIANCE')
  AND EXISTS (SELECT 1 FROM institutions_locations il46 WHERE il46.institution_id = 46 AND il46.is_headquater = 1);

UPDATE project_innovation_partnerships x
JOIN `institution_remap_7320_46_aiccra` w ON w.source_table = 'project_innovation_partnerships' AND w.record_id = x.id
SET x.institution_id = 46
WHERE x.institution_id = 7320
  AND EXISTS (SELECT 1 FROM institutions i WHERE i.id = 7320 AND i.acronym = 'CGIAR ALLIANCE')
  AND EXISTS (SELECT 1 FROM institutions_locations il46 WHERE il46.institution_id = 46 AND il46.is_headquater = 1);

UPDATE project_partners x
JOIN `institution_remap_7320_46_aiccra` w ON w.source_table = 'project_partners' AND w.record_id = x.id
SET x.institution_id = 46
WHERE x.institution_id = 7320
  AND EXISTS (SELECT 1 FROM institutions i WHERE i.id = 7320 AND i.acronym = 'CGIAR ALLIANCE')
  AND EXISTS (SELECT 1 FROM institutions_locations il46 WHERE il46.institution_id = 46 AND il46.is_headquater = 1);

-- Deactivate first, then relocate, so the duplicate check above still describes the data.
UPDATE project_partner_locations l
JOIN `institution_remap_7320_46_aiccra` w ON w.source_table = 'project_partner_locations' AND w.record_id = l.id
  AND w.planned_action = 'DEACTIVATE'
SET l.is_active = 0
WHERE l.is_active = 1 AND l.institution_loc_id = w.old_location_id
  AND EXISTS (SELECT 1 FROM institutions i WHERE i.id = 7320 AND i.acronym = 'CGIAR ALLIANCE')
  AND EXISTS (SELECT 1 FROM institutions_locations il46 WHERE il46.institution_id = 46 AND il46.is_headquater = 1);

UPDATE project_partner_locations l
JOIN `institution_remap_7320_46_aiccra` w ON w.source_table = 'project_partner_locations' AND w.record_id = l.id
  AND w.planned_action = 'RELOCATE'
SET l.institution_loc_id = (SELECT MIN(hq.id) FROM institutions_locations hq
                            WHERE hq.institution_id = 46 AND hq.is_headquater = 1)
WHERE l.institution_loc_id = w.old_location_id
  AND EXISTS (SELECT 1 FROM institutions i WHERE i.id = 7320 AND i.acronym = 'CGIAR ALLIANCE')
  AND EXISTS (SELECT 1 FROM institutions_locations il46 WHERE il46.institution_id = 46 AND il46.is_headquater = 1);

UPDATE project_budgets x
JOIN `institution_remap_7320_46_aiccra` w ON w.source_table = 'project_budgets' AND w.record_id = x.id
SET x.institution_id = 46
WHERE x.institution_id = 7320
  AND EXISTS (SELECT 1 FROM institutions i WHERE i.id = 7320 AND i.acronym = 'CGIAR ALLIANCE')
  AND EXISTS (SELECT 1 FROM institutions_locations il46 WHERE il46.institution_id = 46 AND il46.is_headquater = 1);

UPDATE project_budget_executions x
JOIN `institution_remap_7320_46_aiccra` w ON w.source_table = 'project_budget_executions' AND w.record_id = x.id
SET x.institution_id = 46
WHERE x.institution_id = 7320
  AND EXISTS (SELECT 1 FROM institutions i WHERE i.id = 7320 AND i.acronym = 'CGIAR ALLIANCE')
  AND EXISTS (SELECT 1 FROM institutions_locations il46 WHERE il46.institution_id = 46 AND il46.is_headquater = 1);

UPDATE crp_ppa_partners x
JOIN `institution_remap_7320_46_aiccra` w ON w.source_table = 'crp_ppa_partners' AND w.record_id = x.id
SET x.institution_id = 46
WHERE x.institution_id = 7320
  AND EXISTS (SELECT 1 FROM institutions i WHERE i.id = 7320 AND i.acronym = 'CGIAR ALLIANCE')
  AND EXISTS (SELECT 1 FROM institutions_locations il46 WHERE il46.institution_id = 46 AND il46.is_headquater = 1);

UPDATE funding_sources_info x
JOIN `institution_remap_7320_46_aiccra` w ON w.source_table = 'funding_sources_info' AND w.record_id = x.id
SET x.direct_donor = 46
WHERE x.direct_donor = 7320
  AND EXISTS (SELECT 1 FROM institutions i WHERE i.id = 7320 AND i.acronym = 'CGIAR ALLIANCE')
  AND EXISTS (SELECT 1 FROM institutions_locations il46 WHERE il46.institution_id = 46 AND il46.is_headquater = 1);

UPDATE funding_source_institutions x
JOIN `institution_remap_7320_46_aiccra` w ON w.source_table = 'funding_source_institutions' AND w.record_id = x.id
SET x.institution_id = 46
WHERE x.institution_id = 7320
  AND EXISTS (SELECT 1 FROM institutions i WHERE i.id = 7320 AND i.acronym = 'CGIAR ALLIANCE')
  AND EXISTS (SELECT 1 FROM institutions_locations il46 WHERE il46.institution_id = 46 AND il46.is_headquater = 1);

UPDATE project_expected_study_centers x
JOIN `institution_remap_7320_46_aiccra` w ON w.source_table = 'project_expected_study_centers' AND w.record_id = x.id
SET x.institution_id = 46
WHERE x.institution_id = 7320
  AND EXISTS (SELECT 1 FROM institutions i WHERE i.id = 7320 AND i.acronym = 'CGIAR ALLIANCE')
  AND EXISTS (SELECT 1 FROM institutions_locations il46 WHERE il46.institution_id = 46 AND il46.is_headquater = 1);

UPDATE project_innovation_centers x
JOIN `institution_remap_7320_46_aiccra` w ON w.source_table = 'project_innovation_centers' AND w.record_id = x.id
SET x.institution_id = 46
WHERE x.institution_id = 7320
  AND EXISTS (SELECT 1 FROM institutions i WHERE i.id = 7320 AND i.acronym = 'CGIAR ALLIANCE')
  AND EXISTS (SELECT 1 FROM institutions_locations il46 WHERE il46.institution_id = 46 AND il46.is_headquater = 1);

UPDATE liaison_institutions x
JOIN `institution_remap_7320_46_aiccra` w ON w.source_table = 'liaison_institutions' AND w.record_id = x.id
SET x.institution_id = 46
WHERE x.institution_id = 7320
  AND EXISTS (SELECT 1 FROM institutions i WHERE i.id = 7320 AND i.acronym = 'CGIAR ALLIANCE')
  AND EXISTS (SELECT 1 FROM institutions_locations il46 WHERE il46.institution_id = 46 AND il46.is_headquater = 1);

-- ---------------------------------------------------------------------------------------------------------
-- 3. Deactivate institution 7320, only once no active reference to it remains in any program
-- ---------------------------------------------------------------------------------------------------------

INSERT IGNORE INTO `institution_remap_7320_46_aiccra`
  (`source_table`, `record_id`, `old_institution_id`, `old_is_active`, `planned_action`)
SELECT 'institutions', i.id, NULL, i.is_active, 'DEACTIVATE'
FROM institutions i
WHERE i.id = 7320 AND i.acronym = 'CGIAR ALLIANCE' AND i.is_active = 1
  AND EXISTS (SELECT 1 FROM global_units g WHERE g.id = 45 AND g.acronym = 'AICCRA')
  AND EXISTS (SELECT 1 FROM institutions_locations il46 WHERE il46.institution_id = 46 AND il46.is_headquater = 1)
  AND NOT EXISTS (SELECT 1 FROM funding_source_institutions x JOIN funding_sources fs ON fs.id = x.funding_source_id
                  WHERE x.institution_id = 7320 AND fs.is_active = 1)
  AND NOT EXISTS (SELECT 1 FROM project_expected_study_centers x JOIN project_expected_studies s ON s.id = x.expected_id
                  WHERE x.institution_id = 7320 AND s.is_active = 1)
  AND NOT EXISTS (SELECT 1 FROM project_innovation_centers x JOIN project_innovations s ON s.id = x.project_innovation_id
                  WHERE x.institution_id = 7320 AND s.is_active = 1)
  AND NOT EXISTS (SELECT 1 FROM deliverable_user_partnerships x WHERE x.institution_id = 7320 AND x.is_active = 1)
  AND NOT EXISTS (SELECT 1 FROM project_expected_study_partnerships x WHERE x.institution_id = 7320 AND x.is_active = 1)
  AND NOT EXISTS (SELECT 1 FROM project_innovation_partnerships x WHERE x.institution_id = 7320 AND x.is_active = 1)
  AND NOT EXISTS (SELECT 1 FROM project_partners x WHERE x.institution_id = 7320 AND x.is_active = 1)
  AND NOT EXISTS (SELECT 1 FROM project_budgets x WHERE x.institution_id = 7320 AND x.is_active = 1)
  AND NOT EXISTS (SELECT 1 FROM project_budget_executions x WHERE x.institution_id = 7320 AND x.is_active = 1)
  AND NOT EXISTS (SELECT 1 FROM crp_ppa_partners x WHERE x.institution_id = 7320 AND x.is_active = 1)
  AND NOT EXISTS (SELECT 1 FROM funding_sources_info x WHERE x.direct_donor = 7320 AND x.is_active = 1)
  AND NOT EXISTS (SELECT 1 FROM liaison_institutions x WHERE x.institution_id = 7320 AND x.is_active = 1);

UPDATE institutions i
JOIN `institution_remap_7320_46_aiccra` w ON w.source_table = 'institutions' AND w.record_id = i.id
  AND w.planned_action = 'DEACTIVATE'
SET i.is_active = 0
WHERE i.id = 7320 AND i.is_active = 1;
