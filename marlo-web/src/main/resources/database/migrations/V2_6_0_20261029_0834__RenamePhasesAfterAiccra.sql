-- ENH-PHASE-TYPE-UNIFICATION-001-T13 (DA-002, FN-006, INT-1, OPS-001, OPS-004): renames the stored `name` of the
-- plan, progress and reporting phases of every Global Unit created after AICCRA to Planning / Progress / Reporting.
--
-- DEPENDENCIES (OPS-001): this migration MUST run after
--   * T11 and the other code tasks: the application no longer decides a phase's type from its `name`, it asks the
--     Phase methods (isPlanningPhase / isProgressPhase / isReportingPhase), which read `description` + `upkeep`.
--     Applying this rename to a database served by older code would break every name-based type check.
--   * M1 (V2_6_0_20261028_1817__FixPlaningPhaseDescription.sql): the type guard below reads `description`.
-- Do not release this file ahead of the code, and do not reorder it before M1.
--
-- Which rows change. A unit after AICCRA holds one of two naming families, depending on the template it was copied
-- from: POWB / UpKeep / AR, or AWPB / Progress / AR. Its phases become:
--   plan      (name POWB or AWPB,  description Planning,  upkeep 0)  ->  Planning
--   progress  (name UpKeep,        description Planning,  upkeep 1)  ->  Progress
--   reporting (name AR,            description Reporting, upkeep 0)  ->  Reporting
-- A progress phase named `Progress` already has the target name and is not touched. A row is changed only when its
-- `description` and `upkeep` match the type its current name stands for; an inconsistent row keeps its name. The
-- misspelled description `Planing` is read as `Planning`, so the guard does not depend on M1 having been applied.
--
-- Which units. The cutoff is the lowest `global_units.id` whose acronym is AICCRA, computed here by SQL. Only units
-- with a larger id are renamed, and every unit whose acronym is AICCRA is excluded, whatever its id. Phases of
-- AICCRA and of earlier units, and phases with no unit, keep their names.
--
-- Safety. No id literal of any kind. A database with no AICCRA row has a NULL cutoff, the comparison is never true
-- and the statement matches no row. DML only, so Flyway's transaction covers it. Idempotent: a renamed row no
-- longer matches any name in the guard, and a second run changes 0 rows. Only `name` is written; no foreign key or
-- other column is touched.
--
-- OPS-004: before this migration runs on a real database, capture the rows it will change and record them in the
-- release notes. The (id, name) list is the only way to reverse this migration, because two naming families map to
-- the same new name:
--
--   SELECT p.id, p.name
--   FROM phases p
--   JOIN (SELECT MIN(id) AS cutoff FROM global_units WHERE acronym = 'AICCRA') c ON p.global_unit_id > c.cutoff
--   WHERE p.global_unit_id NOT IN (SELECT id FROM global_units WHERE acronym = 'AICCRA')
--     AND ((BINARY p.name IN ('POWB', 'AWPB') AND p.description IN ('Planning', 'Planing') AND p.upkeep = 0)
--       OR (BINARY p.name = 'UpKeep' AND p.description IN ('Planning', 'Planing') AND p.upkeep = 1)
--       OR (BINARY p.name = 'AR' AND p.description = 'Reporting' AND p.upkeep = 0))
--   ORDER BY p.id;
--
-- Also list the autosave drafts of the renamed units (OPS-002); see the release checklist of the spec.

UPDATE phases p
JOIN (SELECT MIN(id) AS cutoff FROM global_units WHERE acronym = 'AICCRA') c ON p.global_unit_id > c.cutoff
SET p.name = CASE
  WHEN BINARY p.name IN ('POWB', 'AWPB') THEN 'Planning'
  WHEN BINARY p.name = 'UpKeep' THEN 'Progress'
  ELSE 'Reporting'
END
WHERE p.global_unit_id NOT IN (SELECT id FROM global_units WHERE acronym = 'AICCRA')
  AND ((BINARY p.name IN ('POWB', 'AWPB') AND p.description IN ('Planning', 'Planing') AND p.upkeep = 0)
    OR (BINARY p.name = 'UpKeep' AND p.description IN ('Planning', 'Planing') AND p.upkeep = 1)
    OR (BINARY p.name = 'AR' AND p.description = 'Reporting' AND p.upkeep = 0));
