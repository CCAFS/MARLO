-- A2-2437: record "Yes" for the indicators that demonstrably already have disaggregations.
--
-- V2_6_0_20260907_1500 added has_disaggregations and deliberately left every row null, so no
-- answer was invented. PMU asked for the completeness report not to start at 0%, and for the
-- indicators that already carry disaggregations the answer is not a guess: the rows are there,
-- and outcomes.ftl has always displayed them as "Yes".
--
-- What this does NOT do, on purpose:
--   * It never writes 0. An indicator without disaggregations may simply not have been asked
--     yet, and recording "answered No" would destroy the very distinction the column exists
--     for. Those rows stay null until somebody answers.
--   * It never touches a row that already has an answer (has_disaggregations IS NOT NULL).
--   * It is limited to the global units whose form actually asks the question
--     (BaseAction.isAiccra(), i.e. global_unit_id >= 45). Recording an answer for a unit that
--     never shows the control would be inventing one.
--
-- "Has disaggregations" mirrors the template's own rule exactly: outcomes.ftl groups the
-- milestones by trimmed title, treats each distinct statement as a row, and shows the block
-- when more than one exists. COALESCE matches its `((m.title)!"")?trim`, so a null title
-- counts as the empty statement rather than being skipped.
--
-- Each phase keeps its own row, so the update is per phase; nothing is replicated here.

UPDATE crp_program_outcomes o
  JOIN crp_programs p ON p.id = o.crp_program_id
   SET o.has_disaggregations = 1
 WHERE o.has_disaggregations IS NULL
   AND o.is_active = 1
   AND p.global_unit_id >= 45
   AND (SELECT COUNT(DISTINCT TRIM(COALESCE(m.title, '')))
          FROM crp_milestones m
         WHERE m.crp_program_outcome_id = o.id
           AND m.is_active = 1) > 1;
