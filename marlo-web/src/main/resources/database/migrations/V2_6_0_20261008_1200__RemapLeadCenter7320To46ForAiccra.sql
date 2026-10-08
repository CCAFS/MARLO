-- Follow-up to V2_6_0_20261005_1018__RemapInstitution7320To46ForAiccra.sql. That migration remapped the funding
-- sources' direct donor from 7320 to 46 but not their lead center (funding_sources_info.lead_center_id), and its
-- deactivation check did not look at that column either, so institution 7320 was marked inactive while 8 active
-- funding source rows still name it as lead center.
--
-- Measured on production copies (aiccradb1-4, 2026-10-08): all 8 rows belong to funding source 5007 ('Alliance
-- Bioversity-CIAT'), AICCRA (global unit 45), phases Planning/Reporting 2021-2023. In each of them the direct
-- donor is already 46, so the same row shows donor 46 and lead center 7320 ('CGIAR ALLIANCE') in the funding
-- source list and form. No other program and no other funding source uses 7320 as lead center.
--
-- This migration moves those ACTIVE rows of AICCRA (45) and AICCRA III (47) to lead center 46. The lead center
-- is a single value per row, so the remap cannot list an institution twice.
--
-- Notes:
--
-- 1. Audit trail and rollback: every row written is recorded first in institution_remap_7320_46_aiccra, the work
--    table of the 7320 migration, under source_table 'funding_sources_info.lead_center_id' with its previous
--    value. The CREATE TABLE below is the same definition as there, so it does nothing where that migration ran
--    and creates the table where it did not. Rollback: set lead_center_id back to old_institution_id for those
--    rows.
--
-- 2. Guards. Every statement that writes data only acts when ALL of these hold; otherwise it affects 0 rows
--    without an error, so no environment's startup is blocked (MarloFlywayConfiguration runs repair() and
--    migrate() on every boot):
--      * institution 7320 exists with acronym 'CGIAR ALLIANCE' -- the record the 7320 migration was meant for;
--      * institution 46 exists -- lead_center_id carries a foreign key to institutions;
--      * the global unit of each row is 45 with acronym 'AICCRA' or 47 with acronym 'AICCRA_III'. Migrations only
--        seed global_units up to id 28, so on a clean database, or where 45/47 is another program, nothing is
--        selected.
--
-- 3. Re-run safe: CREATE TABLE IF NOT EXISTS, the unique key with INSERT IGNORE, and the lead_center_id = 7320
--    filter on the UPDATE mean a second run records nothing new and writes nothing already written.
--    funding_sources_info has no ON UPDATE column, so no audit stamp is lost.
--
-- 4. Closed phases are rewritten, as in the 49 and 7320 migrations: 2021-2023 reports will show the Alliance
--    (CIAT) record as lead center instead of the inactive 7320.

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

-- Expected on aiccradb2: 8 rows, all REMAP.
INSERT IGNORE INTO `institution_remap_7320_46_aiccra` (`source_table`, `record_id`, `old_institution_id`, `planned_action`)
SELECT 'funding_sources_info.lead_center_id', x.id, x.lead_center_id, 'REMAP'
FROM funding_sources_info x
JOIN phases p ON p.id = x.id_phase
WHERE x.lead_center_id = 7320 AND x.is_active = 1
  AND p.global_unit_id IN (SELECT g.id FROM global_units g
                           WHERE (g.id = 45 AND g.acronym = 'AICCRA') OR (g.id = 47 AND g.acronym = 'AICCRA_III'))
  AND EXISTS (SELECT 1 FROM institutions i WHERE i.id = 7320 AND i.acronym = 'CGIAR ALLIANCE')
  AND EXISTS (SELECT 1 FROM institutions i46 WHERE i46.id = 46);

-- Expected on aiccradb2: 8 rows.
UPDATE funding_sources_info x
JOIN `institution_remap_7320_46_aiccra` w
  ON w.source_table = 'funding_sources_info.lead_center_id' AND w.record_id = x.id AND w.planned_action = 'REMAP'
SET x.lead_center_id = 46
WHERE x.lead_center_id = 7320
  AND EXISTS (SELECT 1 FROM institutions i WHERE i.id = 7320 AND i.acronym = 'CGIAR ALLIANCE')
  AND EXISTS (SELECT 1 FROM institutions i46 WHERE i46.id = 46);
