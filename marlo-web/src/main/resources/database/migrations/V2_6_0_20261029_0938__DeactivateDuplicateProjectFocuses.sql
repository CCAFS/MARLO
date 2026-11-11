-- Deactivates the duplicate project focuses (a cluster's components and regions) left by the forward replication
-- bug fixed in ProjectFocusManagerImpl.addProjectFocusPhase. When a new cluster was saved in a Planning phase,
-- ProjectInfoManagerImpl.saveInfoPhase saved its focuses once more for every later phase it created, and each of
-- those saves copied them again into every later phase, because the walk checked a Phase.projectFocuses
-- collection that Hibernate never refreshed with the rows it had just inserted. The clusters list then showed each
-- component and region once per copy: 18 times on the AICCRA_III clusters, which have 18 phases.
--
-- Measured on the local copy (aiccradb1, 2026-10-29): 34 project/phase/program groups with more than one active
-- row, 578 surplus rows, all on AICCRA_III (global unit 47), projects 102093 and 102094. Two active rows for the
-- same project, phase and program are never valid in any global unit, so the clean-up is not scoped to one.
--
-- Notes:
--
-- 1. The oldest row of each group (lowest id) is kept: in the phase that was saved it is the original, and in the
--    later phases it is the copy made by the first replication pass.
--
-- 2. Nothing is deleted. Every row deactivated here is recorded first in project_focuses_dedup_20261029 with the
--    id of the row that was kept. Rollback: set is_active = 1 again on the rows listed there.
--
-- 3. Safe to run anywhere: a database without duplicates selects nothing, and a second run finds none, so no
--    environment's startup is blocked (MarloFlywayConfiguration runs repair() and migrate() on every boot).

CREATE TABLE IF NOT EXISTS project_focuses_dedup_20261029 (
  project_focus_id BIGINT NOT NULL,
  kept_project_focus_id BIGINT NOT NULL,
  deactivated_on TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (project_focus_id)
);

INSERT IGNORE INTO project_focuses_dedup_20261029 (project_focus_id, kept_project_focus_id)
SELECT pf.id, dup.kept_id
FROM project_focuses pf
INNER JOIN (
  SELECT project_id, id_phase, program_id, MIN(id) AS kept_id
  FROM project_focuses
  WHERE is_active = 1
  GROUP BY project_id, id_phase, program_id
  HAVING COUNT(*) > 1
) dup
  ON dup.project_id = pf.project_id
  AND dup.id_phase <=> pf.id_phase
  AND dup.program_id = pf.program_id
WHERE pf.is_active = 1
  AND pf.id <> dup.kept_id;

UPDATE project_focuses pf
INNER JOIN project_focuses_dedup_20261029 dedup ON dedup.project_focus_id = pf.id
SET pf.is_active = 0
WHERE pf.is_active = 1;
