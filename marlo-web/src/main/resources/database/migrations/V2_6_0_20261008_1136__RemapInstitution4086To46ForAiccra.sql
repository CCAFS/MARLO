-- Institution 4086 ('Alliance of Bioversity International & the International Center for Tropical Agriculture
-- (CIAT)', no acronym in MARLO, 'CIAT-BIOVERSITY' in CLARISA) is the third Alliance record listed in A2-2510,
-- after 49 (V2_6_0_20261005_0927) and 7320 (V2_6_0_20261005_1018). AICCRA uses it almost only through the Web
-- of Science ingest, which mapped published affiliations to it, while the record users are meant to select is
-- 46 ('Alliance of Bioversity and CIAT - Regional Hub (Centro Internacional de Agricultura Tropical)',
-- 'CIAT (Alliance)'). This migration moves AICCRA's and AICCRA III's ACTIVE references from 4086 to 46 and
-- then marks 4086 inactive.
--
-- Decisions (2026-10-08), following the 7320 migration:
--   * Scope is AICCRA (global unit 45) and AICCRA III (47).
--   * Only ACTIVE rows are remapped. Inactive rows keep institution 4086. For the tables without an is_active
--     column, "active" means the parent is active: the funding source, the expected study or the innovation.
--   * Institution 4086 is marked inactive afterwards. This is what makes the remap stick: CLARISA code 4086 is
--     still valid, and the Web of Science ingest only skips institutions that MARLO holds inactive
--     (InstitutionManager.getActiveInstitutionById, ff8b43d3c6). Without it, the next synchronization would put
--     4086 back. Once inactive, an affiliation CLARISA matches to 4086 is no longer mapped (a warning is logged),
--     and 4086 disappears from the institution search (aed93b4cec).
--   * A row that would end up next to an existing institution 46 in the same parent and phase is not remapped:
--     it is recorded as SKIP and keeps 4086, which also keeps 4086 active (note 3). Deliverable affiliations are
--     the exception, see note 2.
--
-- Measured on production copies (aiccradb1-4, 2026-10-08), every column in the schema that references
-- institutions or institutions_locations was checked. Rows on 4086 in AICCRA, AICCRA III:
--
--   deliverable_affiliations                    890 rows, 597 active, 50 deliverables, 24 phases (all AICCRA)
--                                                 37 active rows collide with 46 -> DEACTIVATE (they include
--                                                 the only pair repeating 4086 in one deliverable+phase)
--   project_innovation_contributing_organizations 11   (all under active innovations)
--   project_innovation_info.lead_organization_id   7   (all under active innovations)
--   the 12 tables of the 7320 migration            0   (included anyway: production keeps moving after the copy)
--   7 more institution pickers                      0   (alliance organizations of innovations, study institutions,
--                                                       policy centers, leverages, funding source donor and lead
--                                                       center, synthesis partnerships; AICCRA was still adding rows to
--                                                       them in May 2026, after the copy, so they are remapped too)
--   project_partner_locations on 4086's office     0   (office 4544, Italy, 4086's only office)
--
-- The location / office part. 4086 has one office in MARLO and in CLARISA: institutions_locations 4544, Italy,
-- headquarters. 46 has no office in Italy; its headquarters is Colombia (Palmira). A remapped project partner
-- whose active office is 4086's moves to 46's headquarters, or has that office deactivated when it already has
-- 46's headquarters as an active office -- the rule of the 7320 migration. No production partner needs it
-- today; the block exists so that a partner added after the copy is not left with an office of an institution
-- it no longer belongs to. Teams can then choose the real 46 office on screen.
--
-- 4086's own office (institutions_locations 4544) is kept, attached to 4086, on purpose. institutions_locations
-- has no is_active column, and every read path reaches an office through its institution (the partner search,
-- which now skips inactive institutions; the office list of the partner already selected; the summaries), so an
-- inactive 4086 hides its office with it. Deleting the row would leave the inactive 4086 rows that stay behind
-- without their office, and cannot be rolled back from the work table. Moving it to 46 would give 46 a second
-- headquarters, in a country where CLARISA lists no office for 46.
--
-- Not touched on purpose:
--   * deliverable_affiliations_not_mapped (1,040 rows): the ingest's own record of affiliations it could not
--     map, with 4086 as the "possible institution" suggested by the matcher. It is evidence of what CLARISA
--     proposed, the ingest rewrites it on every synchronization, and no screen maps from it.
--   * intellectual_property_rights_institutions row 3: a fixed option of the innovation IP picker with its own
--     custom_name ('Bioversity International and International Center for Tropical Agriculture - CIAT'), not a
--     program reference. Row 2 (7320) was left the same way.
--   * project_innovation_info.intellectual_property_institution_id: no row holds 4086 (the column stores the
--     id of the picker option, 1-4, not an institution id).
--   * Legacy or program-less references, which are only CHECKED before deactivating 4086, never remapped:
--     powb_collaboration_global_units, report_synthesis_key_partnership_external_institutions, partner_requests,
--     capdev_partners, center_project_partners, ip_liaison_institutions, deliverable_leaders,
--     global_units.institution_id and institutions.parent_id. No row holds 4086 in any copy; if production has
--     one, 4086 stays active and the row stays as it is. participant.institution is left out: its type differs
--     between the migrations (varchar) and the AICCRA databases (bigint), and it holds no 4086 in any copy.
--
-- Notes:
--
-- 1. The work table is the audit trail and the rollback path: every row written is recorded first with its
--    previous institution_id (for a partner office, its previous location and is_active; for a deactivated
--    affiliation, its previous is_active; for institution 4086 itself, its previous is_active).
--
-- 2. Deliverable affiliations. A remap that would list the same institution twice for one deliverable and
--    phase deactivates the row instead, as the 49 migration does: when the deliverable+phase already has an
--    active affiliation to 46, or when another active 4086 row with a lower id sits in the same deliverable+phase
--    (that one is remapped, this one deactivated). The deactivated rows keep institution 4086 and their
--    institution_name_web_of_science. deliverable_affiliations.active_since is ON UPDATE CURRENT_TIMESTAMP; the
--    UPDATEs assign it to itself, which keeps the original stamp, and the work table stores it as well.
--
-- 3. Guards. Every statement that writes data only acts when ALL of these hold; otherwise it affects 0 rows
--    without an error, so no environment's startup is blocked (MarloFlywayConfiguration runs repair() and
--    migrate() on every boot):
--      * institution 4086 exists with the name above -- the record this migration is meant for. The name, not
--        the acronym, because MARLO holds no acronym for it and a CLARISA refresh could add one;
--      * institution 46 exists and has a headquarters office -- the remapped tables carry foreign keys to
--        institutions, and partner offices are moved to that headquarters;
--      * the global unit of each row is 45 with acronym 'AICCRA' or 47 with acronym 'AICCRA_III'. Migrations
--        only seed global_units up to id 28, so on a clean database, or where 45/47 is another program, nothing
--        is selected.
--
-- 4. Institution 4086 is deactivated only while no active reference to it remains in any of the tables this
--    migration remaps, in any program, and only where global unit 45 is 'AICCRA'. A skipped collision, a row
--    of another program, or a database of another program keeps 4086 active.
--
-- 5. Re-run safe: CREATE TABLE IF NOT EXISTS, the unique key with INSERT IGNORE, and the institution_id = 4086
--    / is_active = 1 filter on every UPDATE mean a second run records nothing new and writes nothing already
--    written. Only columns created by the migrations in this repository (or present in the baseline the 49 and
--    7320 migrations already rely on) are read.
--
-- 6. Rollback: for each source_table, set the column back to old_institution_id (or institution_loc_id back to
--    old_location_id, is_active back to old_is_active) where planned_action is not SKIP.

CREATE TABLE IF NOT EXISTS `institution_remap_4086_46_aiccra` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `source_table` varchar(64) NOT NULL,
  `record_id` bigint NOT NULL,
  `old_institution_id` bigint DEFAULT NULL,
  `old_location_id` bigint DEFAULT NULL,
  `old_is_active` tinyint(1) DEFAULT NULL,
  `old_active_since` timestamp NULL DEFAULT NULL,
  `planned_action` varchar(16) NOT NULL,
  `applied_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_remap_4086_source_record` (`source_table`, `record_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ---------------------------------------------------------------------------------------------------------
-- 1. Plan: record every active row in scope, and decide REMAP, DEACTIVATE or SKIP
-- ---------------------------------------------------------------------------------------------------------

-- Expected on aiccradb2: 597 rows, 37 DEACTIVATE and 560 REMAP.
INSERT IGNORE INTO `institution_remap_4086_46_aiccra`
  (`source_table`, `record_id`, `old_institution_id`, `old_is_active`, `old_active_since`, `planned_action`)
SELECT 'deliverable_affiliations', x.id, x.institution_id, x.is_active, x.active_since,
  CASE
    WHEN EXISTS (SELECT 1 FROM deliverable_affiliations b
                 WHERE b.deliverable_id = x.deliverable_id AND b.id_phase = x.id_phase
                   AND b.institution_id = 46 AND b.is_active = 1) THEN 'DEACTIVATE'
    WHEN EXISTS (SELECT 1 FROM deliverable_affiliations b
                 WHERE b.deliverable_id = x.deliverable_id AND b.id_phase = x.id_phase
                   AND b.institution_id = 4086 AND b.is_active = 1 AND b.id < x.id) THEN 'DEACTIVATE'
    ELSE 'REMAP'
  END
FROM deliverable_affiliations x
JOIN phases p ON p.id = x.id_phase
WHERE x.institution_id = 4086 AND x.is_active = 1
  AND p.global_unit_id IN (SELECT g.id FROM global_units g
                           WHERE (g.id = 45 AND g.acronym = 'AICCRA') OR (g.id = 47 AND g.acronym = 'AICCRA_III'))
  AND EXISTS (SELECT 1 FROM institutions i WHERE i.id = 4086
              AND i.name = 'Alliance of Bioversity International & the International Center for Tropical Agriculture (CIAT)')
  AND EXISTS (SELECT 1 FROM institutions_locations il46 WHERE il46.institution_id = 46 AND il46.is_headquater = 1);

INSERT IGNORE INTO `institution_remap_4086_46_aiccra` (`source_table`, `record_id`, `old_institution_id`, `planned_action`)
SELECT 'deliverable_user_partnerships', x.id, x.institution_id,
  CASE WHEN EXISTS (SELECT 1 FROM deliverable_user_partnerships b
                    WHERE b.deliverable_id = x.deliverable_id AND b.id_phase = x.id_phase
                      AND b.deliverable_partner_type_id <=> x.deliverable_partner_type_id
                      AND b.institution_id = 46 AND b.is_active = 1) THEN 'SKIP' ELSE 'REMAP' END
FROM deliverable_user_partnerships x
JOIN phases p ON p.id = x.id_phase
WHERE x.institution_id = 4086 AND x.is_active = 1
  AND p.global_unit_id IN (SELECT g.id FROM global_units g
                           WHERE (g.id = 45 AND g.acronym = 'AICCRA') OR (g.id = 47 AND g.acronym = 'AICCRA_III'))
  AND EXISTS (SELECT 1 FROM institutions i WHERE i.id = 4086
              AND i.name = 'Alliance of Bioversity International & the International Center for Tropical Agriculture (CIAT)')
  AND EXISTS (SELECT 1 FROM institutions_locations il46 WHERE il46.institution_id = 46 AND il46.is_headquater = 1);

INSERT IGNORE INTO `institution_remap_4086_46_aiccra` (`source_table`, `record_id`, `old_institution_id`, `planned_action`)
SELECT 'project_expected_study_partnerships', x.id, x.institution_id,
  CASE WHEN EXISTS (SELECT 1 FROM project_expected_study_partnerships b
                    WHERE b.expected_id = x.expected_id AND b.id_phase = x.id_phase
                      AND b.expected_study_partner_type_id <=> x.expected_study_partner_type_id
                      AND b.institution_id = 46 AND b.is_active = 1) THEN 'SKIP' ELSE 'REMAP' END
FROM project_expected_study_partnerships x
JOIN phases p ON p.id = x.id_phase
WHERE x.institution_id = 4086 AND x.is_active = 1
  AND p.global_unit_id IN (SELECT g.id FROM global_units g
                           WHERE (g.id = 45 AND g.acronym = 'AICCRA') OR (g.id = 47 AND g.acronym = 'AICCRA_III'))
  AND EXISTS (SELECT 1 FROM institutions i WHERE i.id = 4086
              AND i.name = 'Alliance of Bioversity International & the International Center for Tropical Agriculture (CIAT)')
  AND EXISTS (SELECT 1 FROM institutions_locations il46 WHERE il46.institution_id = 46 AND il46.is_headquater = 1);

INSERT IGNORE INTO `institution_remap_4086_46_aiccra` (`source_table`, `record_id`, `old_institution_id`, `planned_action`)
SELECT 'project_innovation_partnerships', x.id, x.institution_id,
  CASE WHEN EXISTS (SELECT 1 FROM project_innovation_partnerships b
                    WHERE b.project_innovation_id = x.project_innovation_id AND b.id_phase = x.id_phase
                      AND b.innovation_partner_type_id <=> x.innovation_partner_type_id
                      AND b.institution_id = 46 AND b.is_active = 1) THEN 'SKIP' ELSE 'REMAP' END
FROM project_innovation_partnerships x
JOIN phases p ON p.id = x.id_phase
WHERE x.institution_id = 4086 AND x.is_active = 1
  AND p.global_unit_id IN (SELECT g.id FROM global_units g
                           WHERE (g.id = 45 AND g.acronym = 'AICCRA') OR (g.id = 47 AND g.acronym = 'AICCRA_III'))
  AND EXISTS (SELECT 1 FROM institutions i WHERE i.id = 4086
              AND i.name = 'Alliance of Bioversity International & the International Center for Tropical Agriculture (CIAT)')
  AND EXISTS (SELECT 1 FROM institutions_locations il46 WHERE il46.institution_id = 46 AND il46.is_headquater = 1);

-- A project that already has 46 as an active partner in the phase is skipped: its persons, contributions and
-- deliverable partnerships hang off the partner row, so merging two partner rows is not a remap.
INSERT IGNORE INTO `institution_remap_4086_46_aiccra` (`source_table`, `record_id`, `old_institution_id`, `planned_action`)
SELECT 'project_partners', x.id, x.institution_id,
  CASE WHEN EXISTS (SELECT 1 FROM project_partners b
                    WHERE b.project_id = x.project_id AND b.id_phase = x.id_phase
                      AND b.institution_id = 46 AND b.is_active = 1) THEN 'SKIP' ELSE 'REMAP' END
FROM project_partners x
JOIN phases p ON p.id = x.id_phase
WHERE x.institution_id = 4086 AND x.is_active = 1
  AND p.global_unit_id IN (SELECT g.id FROM global_units g
                           WHERE (g.id = 45 AND g.acronym = 'AICCRA') OR (g.id = 47 AND g.acronym = 'AICCRA_III'))
  AND EXISTS (SELECT 1 FROM institutions i WHERE i.id = 4086
              AND i.name = 'Alliance of Bioversity International & the International Center for Tropical Agriculture (CIAT)')
  AND EXISTS (SELECT 1 FROM institutions_locations il46 WHERE il46.institution_id = 46 AND il46.is_headquater = 1);

INSERT IGNORE INTO `institution_remap_4086_46_aiccra` (`source_table`, `record_id`, `old_institution_id`, `planned_action`)
SELECT 'project_budgets', x.id, x.institution_id,
  CASE WHEN EXISTS (SELECT 1 FROM project_budgets b
                    WHERE b.project_id = x.project_id AND b.id_phase = x.id_phase AND b.year <=> x.year
                      AND b.budget_type <=> x.budget_type AND b.funding_source_id <=> x.funding_source_id
                      AND b.institution_id = 46 AND b.is_active = 1) THEN 'SKIP' ELSE 'REMAP' END
FROM project_budgets x
JOIN phases p ON p.id = x.id_phase
WHERE x.institution_id = 4086 AND x.is_active = 1
  AND p.global_unit_id IN (SELECT g.id FROM global_units g
                           WHERE (g.id = 45 AND g.acronym = 'AICCRA') OR (g.id = 47 AND g.acronym = 'AICCRA_III'))
  AND EXISTS (SELECT 1 FROM institutions i WHERE i.id = 4086
              AND i.name = 'Alliance of Bioversity International & the International Center for Tropical Agriculture (CIAT)')
  AND EXISTS (SELECT 1 FROM institutions_locations il46 WHERE il46.institution_id = 46 AND il46.is_headquater = 1);

INSERT IGNORE INTO `institution_remap_4086_46_aiccra` (`source_table`, `record_id`, `old_institution_id`, `planned_action`)
SELECT 'project_budget_executions', x.id, x.institution_id,
  CASE WHEN EXISTS (SELECT 1 FROM project_budget_executions b
                    WHERE b.project_id = x.project_id AND b.phase_id = x.phase_id AND b.year <=> x.year
                      AND b.budget_type_id <=> x.budget_type_id
                      AND b.institution_id = 46 AND b.is_active = 1) THEN 'SKIP' ELSE 'REMAP' END
FROM project_budget_executions x
JOIN phases p ON p.id = x.phase_id
WHERE x.institution_id = 4086 AND x.is_active = 1
  AND p.global_unit_id IN (SELECT g.id FROM global_units g
                           WHERE (g.id = 45 AND g.acronym = 'AICCRA') OR (g.id = 47 AND g.acronym = 'AICCRA_III'))
  AND EXISTS (SELECT 1 FROM institutions i WHERE i.id = 4086
              AND i.name = 'Alliance of Bioversity International & the International Center for Tropical Agriculture (CIAT)')
  AND EXISTS (SELECT 1 FROM institutions_locations il46 WHERE il46.institution_id = 46 AND il46.is_headquater = 1);

INSERT IGNORE INTO `institution_remap_4086_46_aiccra` (`source_table`, `record_id`, `old_institution_id`, `planned_action`)
SELECT 'crp_ppa_partners', x.id, x.institution_id,
  CASE WHEN EXISTS (SELECT 1 FROM crp_ppa_partners b
                    WHERE b.global_unit_id = x.global_unit_id AND b.id_phase <=> x.id_phase
                      AND b.institution_id = 46 AND b.is_active = 1) THEN 'SKIP' ELSE 'REMAP' END
FROM crp_ppa_partners x
WHERE x.institution_id = 4086 AND x.is_active = 1
  AND x.global_unit_id IN (SELECT g.id FROM global_units g
                           WHERE (g.id = 45 AND g.acronym = 'AICCRA') OR (g.id = 47 AND g.acronym = 'AICCRA_III'))
  AND EXISTS (SELECT 1 FROM institutions i WHERE i.id = 4086
              AND i.name = 'Alliance of Bioversity International & the International Center for Tropical Agriculture (CIAT)')
  AND EXISTS (SELECT 1 FROM institutions_locations il46 WHERE il46.institution_id = 46 AND il46.is_headquater = 1);

INSERT IGNORE INTO `institution_remap_4086_46_aiccra` (`source_table`, `record_id`, `old_institution_id`, `planned_action`)
SELECT 'funding_sources_info', x.id, x.direct_donor, 'REMAP'
FROM funding_sources_info x
JOIN phases p ON p.id = x.id_phase
WHERE x.direct_donor = 4086 AND x.is_active = 1
  AND p.global_unit_id IN (SELECT g.id FROM global_units g
                           WHERE (g.id = 45 AND g.acronym = 'AICCRA') OR (g.id = 47 AND g.acronym = 'AICCRA_III'))
  AND EXISTS (SELECT 1 FROM institutions i WHERE i.id = 4086
              AND i.name = 'Alliance of Bioversity International & the International Center for Tropical Agriculture (CIAT)')
  AND EXISTS (SELECT 1 FROM institutions_locations il46 WHERE il46.institution_id = 46 AND il46.is_headquater = 1);

INSERT IGNORE INTO `institution_remap_4086_46_aiccra` (`source_table`, `record_id`, `old_institution_id`, `planned_action`)
SELECT 'funding_source_institutions', x.id, x.institution_id,
  CASE WHEN EXISTS (SELECT 1 FROM funding_source_institutions b
                    WHERE b.funding_source_id = x.funding_source_id AND b.id_phase = x.id_phase
                      AND b.institution_id = 46) THEN 'SKIP' ELSE 'REMAP' END
FROM funding_source_institutions x
JOIN phases p ON p.id = x.id_phase
JOIN funding_sources fs ON fs.id = x.funding_source_id
WHERE x.institution_id = 4086 AND fs.is_active = 1
  AND p.global_unit_id IN (SELECT g.id FROM global_units g
                           WHERE (g.id = 45 AND g.acronym = 'AICCRA') OR (g.id = 47 AND g.acronym = 'AICCRA_III'))
  AND EXISTS (SELECT 1 FROM institutions i WHERE i.id = 4086
              AND i.name = 'Alliance of Bioversity International & the International Center for Tropical Agriculture (CIAT)')
  AND EXISTS (SELECT 1 FROM institutions_locations il46 WHERE il46.institution_id = 46 AND il46.is_headquater = 1);

INSERT IGNORE INTO `institution_remap_4086_46_aiccra` (`source_table`, `record_id`, `old_institution_id`, `planned_action`)
SELECT 'project_expected_study_centers', x.id, x.institution_id,
  CASE WHEN EXISTS (SELECT 1 FROM project_expected_study_centers b
                    WHERE b.expected_id = x.expected_id AND b.id_phase = x.id_phase
                      AND b.institution_id = 46) THEN 'SKIP' ELSE 'REMAP' END
FROM project_expected_study_centers x
JOIN phases p ON p.id = x.id_phase
JOIN project_expected_studies s ON s.id = x.expected_id
WHERE x.institution_id = 4086 AND s.is_active = 1
  AND p.global_unit_id IN (SELECT g.id FROM global_units g
                           WHERE (g.id = 45 AND g.acronym = 'AICCRA') OR (g.id = 47 AND g.acronym = 'AICCRA_III'))
  AND EXISTS (SELECT 1 FROM institutions i WHERE i.id = 4086
              AND i.name = 'Alliance of Bioversity International & the International Center for Tropical Agriculture (CIAT)')
  AND EXISTS (SELECT 1 FROM institutions_locations il46 WHERE il46.institution_id = 46 AND il46.is_headquater = 1);

INSERT IGNORE INTO `institution_remap_4086_46_aiccra` (`source_table`, `record_id`, `old_institution_id`, `planned_action`)
SELECT 'project_innovation_centers', x.id, x.institution_id,
  CASE WHEN EXISTS (SELECT 1 FROM project_innovation_centers b
                    WHERE b.project_innovation_id = x.project_innovation_id AND b.id_phase = x.id_phase
                      AND b.institution_id = 46) THEN 'SKIP' ELSE 'REMAP' END
FROM project_innovation_centers x
JOIN phases p ON p.id = x.id_phase
JOIN project_innovations s ON s.id = x.project_innovation_id
WHERE x.institution_id = 4086 AND s.is_active = 1
  AND p.global_unit_id IN (SELECT g.id FROM global_units g
                           WHERE (g.id = 45 AND g.acronym = 'AICCRA') OR (g.id = 47 AND g.acronym = 'AICCRA_III'))
  AND EXISTS (SELECT 1 FROM institutions i WHERE i.id = 4086
              AND i.name = 'Alliance of Bioversity International & the International Center for Tropical Agriculture (CIAT)')
  AND EXISTS (SELECT 1 FROM institutions_locations il46 WHERE il46.institution_id = 46 AND il46.is_headquater = 1);

-- Expected on aiccradb2: 11 rows, all REMAP (no innovation lists 46 as a contributing organization).
INSERT IGNORE INTO `institution_remap_4086_46_aiccra` (`source_table`, `record_id`, `old_institution_id`, `planned_action`)
SELECT 'project_innovation_contributing_organizations', x.id, x.institution_id,
  CASE WHEN EXISTS (SELECT 1 FROM project_innovation_contributing_organizations b
                    WHERE b.project_innovation_id = x.project_innovation_id AND b.id_phase = x.id_phase
                      AND b.institution_id = 46) THEN 'SKIP' ELSE 'REMAP' END
FROM project_innovation_contributing_organizations x
JOIN phases p ON p.id = x.id_phase
JOIN project_innovations s ON s.id = x.project_innovation_id
WHERE x.institution_id = 4086 AND s.is_active = 1
  AND p.global_unit_id IN (SELECT g.id FROM global_units g
                           WHERE (g.id = 45 AND g.acronym = 'AICCRA') OR (g.id = 47 AND g.acronym = 'AICCRA_III'))
  AND EXISTS (SELECT 1 FROM institutions i WHERE i.id = 4086
              AND i.name = 'Alliance of Bioversity International & the International Center for Tropical Agriculture (CIAT)')
  AND EXISTS (SELECT 1 FROM institutions_locations il46 WHERE il46.institution_id = 46 AND il46.is_headquater = 1);

-- Expected on aiccradb2: 7 rows, all REMAP. The lead organization is a single value per row, so it cannot
-- collide.
INSERT IGNORE INTO `institution_remap_4086_46_aiccra` (`source_table`, `record_id`, `old_institution_id`, `planned_action`)
SELECT 'project_innovation_info', x.id, x.lead_organization_id, 'REMAP'
FROM project_innovation_info x
JOIN phases p ON p.id = x.id_phase
JOIN project_innovations s ON s.id = x.project_innovation_id
WHERE x.lead_organization_id = 4086 AND s.is_active = 1
  AND p.global_unit_id IN (SELECT g.id FROM global_units g
                           WHERE (g.id = 45 AND g.acronym = 'AICCRA') OR (g.id = 47 AND g.acronym = 'AICCRA_III'))
  AND EXISTS (SELECT 1 FROM institutions i WHERE i.id = 4086
              AND i.name = 'Alliance of Bioversity International & the International Center for Tropical Agriculture (CIAT)')
  AND EXISTS (SELECT 1 FROM institutions_locations il46 WHERE il46.institution_id = 46 AND il46.is_headquater = 1);

-- The institution pickers of innovations, studies, policies, leverages, funding sources and the synthesis
-- partnerships. No row holds 4086 in the copies, but these lists stayed open after the copy was taken, and 4086
-- was selectable in them until it is deactivated here.
INSERT IGNORE INTO `institution_remap_4086_46_aiccra`
  (`source_table`, `record_id`, `old_institution_id`, `old_active_since`, `planned_action`)
SELECT 'project_innovation_alliance_organizations', x.id, x.institution_id, x.active_since,
  CASE WHEN EXISTS (SELECT 1 FROM project_innovation_alliance_organizations b
                    WHERE b.project_innovation_id = x.project_innovation_id AND b.id_phase = x.id_phase
                      AND b.institution_id = 46 AND b.is_active = 1) THEN 'SKIP' ELSE 'REMAP' END
FROM project_innovation_alliance_organizations x
JOIN phases p ON p.id = x.id_phase
WHERE x.institution_id = 4086 AND x.is_active = 1
  AND p.global_unit_id IN (SELECT g.id FROM global_units g
                           WHERE (g.id = 45 AND g.acronym = 'AICCRA') OR (g.id = 47 AND g.acronym = 'AICCRA_III'))
  AND EXISTS (SELECT 1 FROM institutions i WHERE i.id = 4086
              AND i.name = 'Alliance of Bioversity International & the International Center for Tropical Agriculture (CIAT)')
  AND EXISTS (SELECT 1 FROM institutions_locations il46 WHERE il46.institution_id = 46 AND il46.is_headquater = 1);

INSERT IGNORE INTO `institution_remap_4086_46_aiccra` (`source_table`, `record_id`, `old_institution_id`, `planned_action`)
SELECT 'project_expected_study_institutions', x.id, x.institution_id,
  CASE WHEN EXISTS (SELECT 1 FROM project_expected_study_institutions b
                    WHERE b.expected_id = x.expected_id AND b.id_phase = x.id_phase
                      AND b.institution_id = 46) THEN 'SKIP' ELSE 'REMAP' END
FROM project_expected_study_institutions x
JOIN phases p ON p.id = x.id_phase
JOIN project_expected_studies s ON s.id = x.expected_id
WHERE x.institution_id = 4086 AND s.is_active = 1
  AND p.global_unit_id IN (SELECT g.id FROM global_units g
                           WHERE (g.id = 45 AND g.acronym = 'AICCRA') OR (g.id = 47 AND g.acronym = 'AICCRA_III'))
  AND EXISTS (SELECT 1 FROM institutions i WHERE i.id = 4086
              AND i.name = 'Alliance of Bioversity International & the International Center for Tropical Agriculture (CIAT)')
  AND EXISTS (SELECT 1 FROM institutions_locations il46 WHERE il46.institution_id = 46 AND il46.is_headquater = 1);

INSERT IGNORE INTO `institution_remap_4086_46_aiccra` (`source_table`, `record_id`, `old_institution_id`, `planned_action`)
SELECT 'project_policy_centers', x.id, x.institution_id,
  CASE WHEN EXISTS (SELECT 1 FROM project_policy_centers b
                    WHERE b.project_policy_id = x.project_policy_id AND b.id_phase = x.id_phase
                      AND b.institution_id = 46) THEN 'SKIP' ELSE 'REMAP' END
FROM project_policy_centers x
JOIN phases p ON p.id = x.id_phase
JOIN project_policies s ON s.id = x.project_policy_id
WHERE x.institution_id = 4086 AND s.is_active = 1
  AND p.global_unit_id IN (SELECT g.id FROM global_units g
                           WHERE (g.id = 45 AND g.acronym = 'AICCRA') OR (g.id = 47 AND g.acronym = 'AICCRA_III'))
  AND EXISTS (SELECT 1 FROM institutions i WHERE i.id = 4086
              AND i.name = 'Alliance of Bioversity International & the International Center for Tropical Agriculture (CIAT)')
  AND EXISTS (SELECT 1 FROM institutions_locations il46 WHERE il46.institution_id = 46 AND il46.is_headquater = 1);

INSERT IGNORE INTO `institution_remap_4086_46_aiccra` (`source_table`, `record_id`, `old_institution_id`, `planned_action`)
SELECT 'project_leverage', x.id, x.institution, 'REMAP'
FROM project_leverage x
JOIN phases p ON p.id = x.id_phase
WHERE x.institution = 4086 AND x.is_active = 1
  AND p.global_unit_id IN (SELECT g.id FROM global_units g
                           WHERE (g.id = 45 AND g.acronym = 'AICCRA') OR (g.id = 47 AND g.acronym = 'AICCRA_III'))
  AND EXISTS (SELECT 1 FROM institutions i WHERE i.id = 4086
              AND i.name = 'Alliance of Bioversity International & the International Center for Tropical Agriculture (CIAT)')
  AND EXISTS (SELECT 1 FROM institutions_locations il46 WHERE il46.institution_id = 46 AND il46.is_headquater = 1);

INSERT IGNORE INTO `institution_remap_4086_46_aiccra` (`source_table`, `record_id`, `old_institution_id`, `planned_action`)
SELECT 'funding_sources_info.donor', x.id, x.donor, 'REMAP'
FROM funding_sources_info x
JOIN phases p ON p.id = x.id_phase
WHERE x.donor = 4086 AND x.is_active = 1
  AND p.global_unit_id IN (SELECT g.id FROM global_units g
                           WHERE (g.id = 45 AND g.acronym = 'AICCRA') OR (g.id = 47 AND g.acronym = 'AICCRA_III'))
  AND EXISTS (SELECT 1 FROM institutions i WHERE i.id = 4086
              AND i.name = 'Alliance of Bioversity International & the International Center for Tropical Agriculture (CIAT)')
  AND EXISTS (SELECT 1 FROM institutions_locations il46 WHERE il46.institution_id = 46 AND il46.is_headquater = 1);

INSERT IGNORE INTO `institution_remap_4086_46_aiccra` (`source_table`, `record_id`, `old_institution_id`, `planned_action`)
SELECT 'funding_sources_info.lead_center_id', x.id, x.lead_center_id, 'REMAP'
FROM funding_sources_info x
JOIN phases p ON p.id = x.id_phase
WHERE x.lead_center_id = 4086 AND x.is_active = 1
  AND p.global_unit_id IN (SELECT g.id FROM global_units g
                           WHERE (g.id = 45 AND g.acronym = 'AICCRA') OR (g.id = 47 AND g.acronym = 'AICCRA_III'))
  AND EXISTS (SELECT 1 FROM institutions i WHERE i.id = 4086
              AND i.name = 'Alliance of Bioversity International & the International Center for Tropical Agriculture (CIAT)')
  AND EXISTS (SELECT 1 FROM institutions_locations il46 WHERE il46.institution_id = 46 AND il46.is_headquater = 1);

INSERT IGNORE INTO `institution_remap_4086_46_aiccra` (`source_table`, `record_id`, `old_institution_id`, `planned_action`)
SELECT 'report_synthesis_partnerships', x.id, x.institution_id,
  CASE WHEN EXISTS (SELECT 1 FROM report_synthesis_partnerships b
                    WHERE b.phase_id = x.phase_id AND b.institution_id = 46 AND b.is_active = 1) THEN 'SKIP'
       ELSE 'REMAP' END
FROM report_synthesis_partnerships x
JOIN phases p ON p.id = x.phase_id
WHERE x.institution_id = 4086 AND x.is_active = 1
  AND p.global_unit_id IN (SELECT g.id FROM global_units g
                           WHERE (g.id = 45 AND g.acronym = 'AICCRA') OR (g.id = 47 AND g.acronym = 'AICCRA_III'))
  AND EXISTS (SELECT 1 FROM institutions i WHERE i.id = 4086
              AND i.name = 'Alliance of Bioversity International & the International Center for Tropical Agriculture (CIAT)')
  AND EXISTS (SELECT 1 FROM institutions_locations il46 WHERE il46.institution_id = 46 AND il46.is_headquater = 1);

INSERT IGNORE INTO `institution_remap_4086_46_aiccra` (`source_table`, `record_id`, `old_institution_id`, `planned_action`)
SELECT 'liaison_institutions', x.id, x.institution_id,
  CASE WHEN EXISTS (SELECT 1 FROM liaison_institutions b
                    WHERE b.global_unit_id = x.global_unit_id AND b.crp_program <=> x.crp_program
                      AND b.institution_id = 46 AND b.is_active = 1) THEN 'SKIP' ELSE 'REMAP' END
FROM liaison_institutions x
WHERE x.institution_id = 4086 AND x.is_active = 1
  AND x.global_unit_id IN (SELECT g.id FROM global_units g
                           WHERE (g.id = 45 AND g.acronym = 'AICCRA') OR (g.id = 47 AND g.acronym = 'AICCRA_III'))
  AND EXISTS (SELECT 1 FROM institutions i WHERE i.id = 4086
              AND i.name = 'Alliance of Bioversity International & the International Center for Tropical Agriculture (CIAT)')
  AND EXISTS (SELECT 1 FROM institutions_locations il46 WHERE il46.institution_id = 46 AND il46.is_headquater = 1);

-- The active offices of the partners planned for REMAP above that point at an office of 4086. 4086 has a single
-- office (Italy), and 46 has none there, so they move to 46's headquarters. A partner that already has 46's
-- headquarters as an active office gets the 4086 one deactivated instead, so the office is not listed twice.
INSERT IGNORE INTO `institution_remap_4086_46_aiccra` (`source_table`, `record_id`, `old_location_id`, `old_is_active`, `planned_action`)
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
JOIN `institution_remap_4086_46_aiccra` w
  ON w.source_table = 'project_partners' AND w.record_id = l.project_partner_id AND w.planned_action = 'REMAP'
JOIN institutions_locations il ON il.id = l.institution_loc_id AND il.institution_id = 4086
WHERE l.is_active = 1
  AND EXISTS (SELECT 1 FROM institutions i WHERE i.id = 4086
              AND i.name = 'Alliance of Bioversity International & the International Center for Tropical Agriculture (CIAT)')
  AND EXISTS (SELECT 1 FROM institutions_locations il46 WHERE il46.institution_id = 46 AND il46.is_headquater = 1);

-- ---------------------------------------------------------------------------------------------------------
-- 2. Apply the plan
-- ---------------------------------------------------------------------------------------------------------

-- Deactivate first: once the REMAP rows carry institution 46 the collision rule no longer describes the data.
-- active_since is assigned to itself so that ON UPDATE CURRENT_TIMESTAMP keeps the original stamp.
UPDATE deliverable_affiliations x
JOIN `institution_remap_4086_46_aiccra` w
  ON w.source_table = 'deliverable_affiliations' AND w.record_id = x.id AND w.planned_action = 'DEACTIVATE'
SET x.is_active = 0, x.active_since = x.active_since
WHERE x.institution_id = 4086 AND x.is_active = 1
  AND EXISTS (SELECT 1 FROM institutions i WHERE i.id = 4086
              AND i.name = 'Alliance of Bioversity International & the International Center for Tropical Agriculture (CIAT)')
  AND EXISTS (SELECT 1 FROM institutions_locations il46 WHERE il46.institution_id = 46 AND il46.is_headquater = 1);

UPDATE deliverable_affiliations x
JOIN `institution_remap_4086_46_aiccra` w
  ON w.source_table = 'deliverable_affiliations' AND w.record_id = x.id AND w.planned_action = 'REMAP'
SET x.institution_id = 46, x.active_since = x.active_since
WHERE x.institution_id = 4086
  AND EXISTS (SELECT 1 FROM institutions i WHERE i.id = 4086
              AND i.name = 'Alliance of Bioversity International & the International Center for Tropical Agriculture (CIAT)')
  AND EXISTS (SELECT 1 FROM institutions_locations il46 WHERE il46.institution_id = 46 AND il46.is_headquater = 1);

UPDATE deliverable_user_partnerships x
JOIN `institution_remap_4086_46_aiccra` w
  ON w.source_table = 'deliverable_user_partnerships' AND w.record_id = x.id AND w.planned_action = 'REMAP'
SET x.institution_id = 46
WHERE x.institution_id = 4086
  AND EXISTS (SELECT 1 FROM institutions i WHERE i.id = 4086
              AND i.name = 'Alliance of Bioversity International & the International Center for Tropical Agriculture (CIAT)')
  AND EXISTS (SELECT 1 FROM institutions_locations il46 WHERE il46.institution_id = 46 AND il46.is_headquater = 1);

UPDATE project_expected_study_partnerships x
JOIN `institution_remap_4086_46_aiccra` w
  ON w.source_table = 'project_expected_study_partnerships' AND w.record_id = x.id AND w.planned_action = 'REMAP'
SET x.institution_id = 46
WHERE x.institution_id = 4086
  AND EXISTS (SELECT 1 FROM institutions i WHERE i.id = 4086
              AND i.name = 'Alliance of Bioversity International & the International Center for Tropical Agriculture (CIAT)')
  AND EXISTS (SELECT 1 FROM institutions_locations il46 WHERE il46.institution_id = 46 AND il46.is_headquater = 1);

UPDATE project_innovation_partnerships x
JOIN `institution_remap_4086_46_aiccra` w
  ON w.source_table = 'project_innovation_partnerships' AND w.record_id = x.id AND w.planned_action = 'REMAP'
SET x.institution_id = 46
WHERE x.institution_id = 4086
  AND EXISTS (SELECT 1 FROM institutions i WHERE i.id = 4086
              AND i.name = 'Alliance of Bioversity International & the International Center for Tropical Agriculture (CIAT)')
  AND EXISTS (SELECT 1 FROM institutions_locations il46 WHERE il46.institution_id = 46 AND il46.is_headquater = 1);

UPDATE project_partners x
JOIN `institution_remap_4086_46_aiccra` w
  ON w.source_table = 'project_partners' AND w.record_id = x.id AND w.planned_action = 'REMAP'
SET x.institution_id = 46
WHERE x.institution_id = 4086
  AND EXISTS (SELECT 1 FROM institutions i WHERE i.id = 4086
              AND i.name = 'Alliance of Bioversity International & the International Center for Tropical Agriculture (CIAT)')
  AND EXISTS (SELECT 1 FROM institutions_locations il46 WHERE il46.institution_id = 46 AND il46.is_headquater = 1);

-- Deactivate first, then relocate, so the duplicate check above still describes the data.
UPDATE project_partner_locations l
JOIN `institution_remap_4086_46_aiccra` w
  ON w.source_table = 'project_partner_locations' AND w.record_id = l.id AND w.planned_action = 'DEACTIVATE'
SET l.is_active = 0
WHERE l.is_active = 1 AND l.institution_loc_id = w.old_location_id
  AND EXISTS (SELECT 1 FROM institutions i WHERE i.id = 4086
              AND i.name = 'Alliance of Bioversity International & the International Center for Tropical Agriculture (CIAT)')
  AND EXISTS (SELECT 1 FROM institutions_locations il46 WHERE il46.institution_id = 46 AND il46.is_headquater = 1);

UPDATE project_partner_locations l
JOIN `institution_remap_4086_46_aiccra` w
  ON w.source_table = 'project_partner_locations' AND w.record_id = l.id AND w.planned_action = 'RELOCATE'
SET l.institution_loc_id = (SELECT MIN(hq.id) FROM institutions_locations hq
                            WHERE hq.institution_id = 46 AND hq.is_headquater = 1)
WHERE l.institution_loc_id = w.old_location_id
  AND EXISTS (SELECT 1 FROM institutions i WHERE i.id = 4086
              AND i.name = 'Alliance of Bioversity International & the International Center for Tropical Agriculture (CIAT)')
  AND EXISTS (SELECT 1 FROM institutions_locations il46 WHERE il46.institution_id = 46 AND il46.is_headquater = 1);

UPDATE project_budgets x
JOIN `institution_remap_4086_46_aiccra` w
  ON w.source_table = 'project_budgets' AND w.record_id = x.id AND w.planned_action = 'REMAP'
SET x.institution_id = 46
WHERE x.institution_id = 4086
  AND EXISTS (SELECT 1 FROM institutions i WHERE i.id = 4086
              AND i.name = 'Alliance of Bioversity International & the International Center for Tropical Agriculture (CIAT)')
  AND EXISTS (SELECT 1 FROM institutions_locations il46 WHERE il46.institution_id = 46 AND il46.is_headquater = 1);

UPDATE project_budget_executions x
JOIN `institution_remap_4086_46_aiccra` w
  ON w.source_table = 'project_budget_executions' AND w.record_id = x.id AND w.planned_action = 'REMAP'
SET x.institution_id = 46
WHERE x.institution_id = 4086
  AND EXISTS (SELECT 1 FROM institutions i WHERE i.id = 4086
              AND i.name = 'Alliance of Bioversity International & the International Center for Tropical Agriculture (CIAT)')
  AND EXISTS (SELECT 1 FROM institutions_locations il46 WHERE il46.institution_id = 46 AND il46.is_headquater = 1);

UPDATE crp_ppa_partners x
JOIN `institution_remap_4086_46_aiccra` w
  ON w.source_table = 'crp_ppa_partners' AND w.record_id = x.id AND w.planned_action = 'REMAP'
SET x.institution_id = 46
WHERE x.institution_id = 4086
  AND EXISTS (SELECT 1 FROM institutions i WHERE i.id = 4086
              AND i.name = 'Alliance of Bioversity International & the International Center for Tropical Agriculture (CIAT)')
  AND EXISTS (SELECT 1 FROM institutions_locations il46 WHERE il46.institution_id = 46 AND il46.is_headquater = 1);

UPDATE funding_sources_info x
JOIN `institution_remap_4086_46_aiccra` w
  ON w.source_table = 'funding_sources_info' AND w.record_id = x.id AND w.planned_action = 'REMAP'
SET x.direct_donor = 46
WHERE x.direct_donor = 4086
  AND EXISTS (SELECT 1 FROM institutions i WHERE i.id = 4086
              AND i.name = 'Alliance of Bioversity International & the International Center for Tropical Agriculture (CIAT)')
  AND EXISTS (SELECT 1 FROM institutions_locations il46 WHERE il46.institution_id = 46 AND il46.is_headquater = 1);

UPDATE funding_source_institutions x
JOIN `institution_remap_4086_46_aiccra` w
  ON w.source_table = 'funding_source_institutions' AND w.record_id = x.id AND w.planned_action = 'REMAP'
SET x.institution_id = 46
WHERE x.institution_id = 4086
  AND EXISTS (SELECT 1 FROM institutions i WHERE i.id = 4086
              AND i.name = 'Alliance of Bioversity International & the International Center for Tropical Agriculture (CIAT)')
  AND EXISTS (SELECT 1 FROM institutions_locations il46 WHERE il46.institution_id = 46 AND il46.is_headquater = 1);

UPDATE project_expected_study_centers x
JOIN `institution_remap_4086_46_aiccra` w
  ON w.source_table = 'project_expected_study_centers' AND w.record_id = x.id AND w.planned_action = 'REMAP'
SET x.institution_id = 46
WHERE x.institution_id = 4086
  AND EXISTS (SELECT 1 FROM institutions i WHERE i.id = 4086
              AND i.name = 'Alliance of Bioversity International & the International Center for Tropical Agriculture (CIAT)')
  AND EXISTS (SELECT 1 FROM institutions_locations il46 WHERE il46.institution_id = 46 AND il46.is_headquater = 1);

UPDATE project_innovation_centers x
JOIN `institution_remap_4086_46_aiccra` w
  ON w.source_table = 'project_innovation_centers' AND w.record_id = x.id AND w.planned_action = 'REMAP'
SET x.institution_id = 46
WHERE x.institution_id = 4086
  AND EXISTS (SELECT 1 FROM institutions i WHERE i.id = 4086
              AND i.name = 'Alliance of Bioversity International & the International Center for Tropical Agriculture (CIAT)')
  AND EXISTS (SELECT 1 FROM institutions_locations il46 WHERE il46.institution_id = 46 AND il46.is_headquater = 1);

UPDATE project_innovation_contributing_organizations x
JOIN `institution_remap_4086_46_aiccra` w
  ON w.source_table = 'project_innovation_contributing_organizations' AND w.record_id = x.id
  AND w.planned_action = 'REMAP'
SET x.institution_id = 46
WHERE x.institution_id = 4086
  AND EXISTS (SELECT 1 FROM institutions i WHERE i.id = 4086
              AND i.name = 'Alliance of Bioversity International & the International Center for Tropical Agriculture (CIAT)')
  AND EXISTS (SELECT 1 FROM institutions_locations il46 WHERE il46.institution_id = 46 AND il46.is_headquater = 1);

UPDATE project_innovation_info x
JOIN `institution_remap_4086_46_aiccra` w
  ON w.source_table = 'project_innovation_info' AND w.record_id = x.id AND w.planned_action = 'REMAP'
SET x.lead_organization_id = 46
WHERE x.lead_organization_id = 4086
  AND EXISTS (SELECT 1 FROM institutions i WHERE i.id = 4086
              AND i.name = 'Alliance of Bioversity International & the International Center for Tropical Agriculture (CIAT)')
  AND EXISTS (SELECT 1 FROM institutions_locations il46 WHERE il46.institution_id = 46 AND il46.is_headquater = 1);

UPDATE project_innovation_alliance_organizations x
JOIN `institution_remap_4086_46_aiccra` w
  ON w.source_table = 'project_innovation_alliance_organizations' AND w.record_id = x.id AND w.planned_action = 'REMAP'
SET x.institution_id = 46, x.active_since = x.active_since
WHERE x.institution_id = 4086
  AND EXISTS (SELECT 1 FROM institutions i WHERE i.id = 4086
              AND i.name = 'Alliance of Bioversity International & the International Center for Tropical Agriculture (CIAT)')
  AND EXISTS (SELECT 1 FROM institutions_locations il46 WHERE il46.institution_id = 46 AND il46.is_headquater = 1);

UPDATE project_expected_study_institutions x
JOIN `institution_remap_4086_46_aiccra` w
  ON w.source_table = 'project_expected_study_institutions' AND w.record_id = x.id AND w.planned_action = 'REMAP'
SET x.institution_id = 46
WHERE x.institution_id = 4086
  AND EXISTS (SELECT 1 FROM institutions i WHERE i.id = 4086
              AND i.name = 'Alliance of Bioversity International & the International Center for Tropical Agriculture (CIAT)')
  AND EXISTS (SELECT 1 FROM institutions_locations il46 WHERE il46.institution_id = 46 AND il46.is_headquater = 1);

UPDATE project_policy_centers x
JOIN `institution_remap_4086_46_aiccra` w
  ON w.source_table = 'project_policy_centers' AND w.record_id = x.id AND w.planned_action = 'REMAP'
SET x.institution_id = 46
WHERE x.institution_id = 4086
  AND EXISTS (SELECT 1 FROM institutions i WHERE i.id = 4086
              AND i.name = 'Alliance of Bioversity International & the International Center for Tropical Agriculture (CIAT)')
  AND EXISTS (SELECT 1 FROM institutions_locations il46 WHERE il46.institution_id = 46 AND il46.is_headquater = 1);

UPDATE project_leverage x
JOIN `institution_remap_4086_46_aiccra` w
  ON w.source_table = 'project_leverage' AND w.record_id = x.id AND w.planned_action = 'REMAP'
SET x.institution = 46
WHERE x.institution = 4086
  AND EXISTS (SELECT 1 FROM institutions i WHERE i.id = 4086
              AND i.name = 'Alliance of Bioversity International & the International Center for Tropical Agriculture (CIAT)')
  AND EXISTS (SELECT 1 FROM institutions_locations il46 WHERE il46.institution_id = 46 AND il46.is_headquater = 1);

UPDATE funding_sources_info x
JOIN `institution_remap_4086_46_aiccra` w
  ON w.source_table = 'funding_sources_info.donor' AND w.record_id = x.id AND w.planned_action = 'REMAP'
SET x.donor = 46
WHERE x.donor = 4086
  AND EXISTS (SELECT 1 FROM institutions i WHERE i.id = 4086
              AND i.name = 'Alliance of Bioversity International & the International Center for Tropical Agriculture (CIAT)')
  AND EXISTS (SELECT 1 FROM institutions_locations il46 WHERE il46.institution_id = 46 AND il46.is_headquater = 1);

UPDATE funding_sources_info x
JOIN `institution_remap_4086_46_aiccra` w
  ON w.source_table = 'funding_sources_info.lead_center_id' AND w.record_id = x.id AND w.planned_action = 'REMAP'
SET x.lead_center_id = 46
WHERE x.lead_center_id = 4086
  AND EXISTS (SELECT 1 FROM institutions i WHERE i.id = 4086
              AND i.name = 'Alliance of Bioversity International & the International Center for Tropical Agriculture (CIAT)')
  AND EXISTS (SELECT 1 FROM institutions_locations il46 WHERE il46.institution_id = 46 AND il46.is_headquater = 1);

UPDATE report_synthesis_partnerships x
JOIN `institution_remap_4086_46_aiccra` w
  ON w.source_table = 'report_synthesis_partnerships' AND w.record_id = x.id AND w.planned_action = 'REMAP'
SET x.institution_id = 46
WHERE x.institution_id = 4086
  AND EXISTS (SELECT 1 FROM institutions i WHERE i.id = 4086
              AND i.name = 'Alliance of Bioversity International & the International Center for Tropical Agriculture (CIAT)')
  AND EXISTS (SELECT 1 FROM institutions_locations il46 WHERE il46.institution_id = 46 AND il46.is_headquater = 1);

UPDATE liaison_institutions x
JOIN `institution_remap_4086_46_aiccra` w
  ON w.source_table = 'liaison_institutions' AND w.record_id = x.id AND w.planned_action = 'REMAP'
SET x.institution_id = 46
WHERE x.institution_id = 4086
  AND EXISTS (SELECT 1 FROM institutions i WHERE i.id = 4086
              AND i.name = 'Alliance of Bioversity International & the International Center for Tropical Agriculture (CIAT)')
  AND EXISTS (SELECT 1 FROM institutions_locations il46 WHERE il46.institution_id = 46 AND il46.is_headquater = 1);

-- ---------------------------------------------------------------------------------------------------------
-- 3. Deactivate institution 4086, only once no active reference to it remains in any program
-- ---------------------------------------------------------------------------------------------------------

-- institutions.is_active is not created by any migration in this repository (it comes with the database), so
-- every read and write of it below goes through a prepared statement that becomes DO 0 where the column is
-- absent. The decision itself is plain SQL: it records the DEACTIVATE row without reading is_active.
INSERT IGNORE INTO `institution_remap_4086_46_aiccra`
  (`source_table`, `record_id`, `old_institution_id`, `planned_action`)
SELECT 'institutions', i.id, NULL, 'DEACTIVATE'
FROM institutions i
WHERE i.id = 4086
  AND i.name = 'Alliance of Bioversity International & the International Center for Tropical Agriculture (CIAT)'
  AND EXISTS (SELECT 1 FROM global_units g WHERE g.id = 45 AND g.acronym = 'AICCRA')
  AND EXISTS (SELECT 1 FROM institutions_locations il46 WHERE il46.institution_id = 46 AND il46.is_headquater = 1)
  AND NOT EXISTS (SELECT 1 FROM deliverable_affiliations x WHERE x.institution_id = 4086 AND x.is_active = 1)
  AND NOT EXISTS (SELECT 1 FROM deliverable_user_partnerships x WHERE x.institution_id = 4086 AND x.is_active = 1)
  AND NOT EXISTS (SELECT 1 FROM project_expected_study_partnerships x WHERE x.institution_id = 4086 AND x.is_active = 1)
  AND NOT EXISTS (SELECT 1 FROM project_innovation_partnerships x WHERE x.institution_id = 4086 AND x.is_active = 1)
  AND NOT EXISTS (SELECT 1 FROM project_partners x WHERE x.institution_id = 4086 AND x.is_active = 1)
  AND NOT EXISTS (SELECT 1 FROM project_budgets x WHERE x.institution_id = 4086 AND x.is_active = 1)
  AND NOT EXISTS (SELECT 1 FROM project_budget_executions x WHERE x.institution_id = 4086 AND x.is_active = 1)
  AND NOT EXISTS (SELECT 1 FROM crp_ppa_partners x WHERE x.institution_id = 4086 AND x.is_active = 1)
  AND NOT EXISTS (SELECT 1 FROM funding_sources_info x WHERE x.direct_donor = 4086 AND x.is_active = 1)
  AND NOT EXISTS (SELECT 1 FROM liaison_institutions x WHERE x.institution_id = 4086 AND x.is_active = 1)
  AND NOT EXISTS (SELECT 1 FROM funding_source_institutions x JOIN funding_sources fs ON fs.id = x.funding_source_id
                  WHERE x.institution_id = 4086 AND fs.is_active = 1)
  AND NOT EXISTS (SELECT 1 FROM project_expected_study_centers x JOIN project_expected_studies s ON s.id = x.expected_id
                  WHERE x.institution_id = 4086 AND s.is_active = 1)
  AND NOT EXISTS (SELECT 1 FROM project_innovation_centers x JOIN project_innovations s ON s.id = x.project_innovation_id
                  WHERE x.institution_id = 4086 AND s.is_active = 1)
  AND NOT EXISTS (SELECT 1 FROM project_innovation_contributing_organizations x
                  JOIN project_innovations s ON s.id = x.project_innovation_id
                  WHERE x.institution_id = 4086 AND s.is_active = 1)
  AND NOT EXISTS (SELECT 1 FROM project_innovation_info x JOIN project_innovations s ON s.id = x.project_innovation_id
                  WHERE x.lead_organization_id = 4086 AND s.is_active = 1)
  AND NOT EXISTS (SELECT 1 FROM project_innovation_alliance_organizations x
                  WHERE x.institution_id = 4086 AND x.is_active = 1)
  AND NOT EXISTS (SELECT 1 FROM project_expected_study_institutions x JOIN project_expected_studies s ON s.id = x.expected_id
                  WHERE x.institution_id = 4086 AND s.is_active = 1)
  AND NOT EXISTS (SELECT 1 FROM project_policy_centers x JOIN project_policies s ON s.id = x.project_policy_id
                  WHERE x.institution_id = 4086 AND s.is_active = 1)
  AND NOT EXISTS (SELECT 1 FROM project_leverage x WHERE x.institution = 4086 AND x.is_active = 1)
  AND NOT EXISTS (SELECT 1 FROM funding_sources_info x WHERE x.donor = 4086 AND x.is_active = 1)
  AND NOT EXISTS (SELECT 1 FROM funding_sources_info x WHERE x.lead_center_id = 4086 AND x.is_active = 1)
  AND NOT EXISTS (SELECT 1 FROM report_synthesis_partnerships x WHERE x.institution_id = 4086 AND x.is_active = 1)
  AND NOT EXISTS (SELECT 1 FROM powb_collaboration_global_units x WHERE x.institution_id = 4086 AND x.is_active = 1)
  AND NOT EXISTS (SELECT 1 FROM report_synthesis_key_partnership_external_institutions x WHERE x.institution_id = 4086)
  AND NOT EXISTS (SELECT 1 FROM partner_requests x WHERE x.institution_id = 4086 AND x.is_active = 1)
  AND NOT EXISTS (SELECT 1 FROM capdev_partners x WHERE x.institution_id = 4086 AND x.is_active = 1)
  AND NOT EXISTS (SELECT 1 FROM center_project_partners x WHERE x.institution_id = 4086 AND x.is_active = 1)
  AND NOT EXISTS (SELECT 1 FROM ip_liaison_institutions x WHERE x.institution_id = 4086)
  AND NOT EXISTS (SELECT 1 FROM deliverable_leaders x WHERE x.instituion_id = 4086)
  AND NOT EXISTS (SELECT 1 FROM global_units x WHERE x.institution_id = 4086)
  AND NOT EXISTS (SELECT 1 FROM institutions x WHERE x.parent_id = 4086);

SET @remap_4086_has_is_active = (SELECT COUNT(*) FROM information_schema.columns
                                  WHERE table_schema = DATABASE() AND table_name = 'institutions'
                                    AND column_name = 'is_active');

-- Record the previous value once; a re-run finds it filled and leaves it alone.
SET @remap_4086_sql = IF(@remap_4086_has_is_active = 1,
  'UPDATE `institution_remap_4086_46_aiccra` w JOIN institutions i ON i.id = w.record_id
   SET w.old_is_active = i.is_active
   WHERE w.source_table = ''institutions'' AND w.planned_action = ''DEACTIVATE'' AND w.old_is_active IS NULL',
  'DO 0');
PREPARE remap_4086_stmt FROM @remap_4086_sql;
EXECUTE remap_4086_stmt;
DEALLOCATE PREPARE remap_4086_stmt;

SET @remap_4086_sql = IF(@remap_4086_has_is_active = 1,
  'UPDATE institutions i JOIN `institution_remap_4086_46_aiccra` w
     ON w.source_table = ''institutions'' AND w.record_id = i.id AND w.planned_action = ''DEACTIVATE''
   SET i.is_active = 0
   WHERE i.id = 4086 AND i.is_active = 1',
  'DO 0');
PREPARE remap_4086_stmt FROM @remap_4086_sql;
EXECUTE remap_4086_stmt;
DEALLOCATE PREPARE remap_4086_stmt;
