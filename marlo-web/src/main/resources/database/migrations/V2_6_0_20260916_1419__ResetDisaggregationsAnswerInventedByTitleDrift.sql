-- A2-2437: undo the "Yes" that V2_6_0_20260907_1620 recorded for indicators whose extra
-- disaggregation row never existed.
--
-- That backfill answered "Does this indicator have disaggregations?" with Yes wherever an
-- outcome had more than one distinct milestone statement:
--
--     COUNT(DISTINCT TRIM(COALESCE(m.title, ''))) > 1
--
-- which mirrored how outcomes.ftl grouped the period-target matrix at the time. The grouping
-- was wrong. Statements reach crp_milestones by copy/paste, and the same sentence is stored
-- with a doubled space in one year and a trailing zero-width space in another; comparing the
-- titles verbatim split one statement into two rows. The two then shared the year columns
-- between them, so a year that had a target captured rendered with no input at all.
--
-- outcomes.ftl now groups on a normalised key (opiStmtKey: non-breaking and zero-width
-- characters folded away, whitespace runs collapsed, trimmed) and gives a milestone whose row
-- already holds its year a further row of its own. Those indicators therefore render a single
-- row again -- but the stored Yes stayed behind, so the toggle claims disaggregations that are
-- not on screen. This removes exactly that answer.
--
-- Back to NULL, not to 0. NULL is "never answered", which is what these rows were before the
-- backfill ran; writing 0 would record an explicit No that nobody gave, and destroy the very
-- distinction V2_6_0_20260907_1500 added the column for.
--
-- The three conditions are deliberately narrow, so only what the backfill could have written
-- is undone:
--   1. The old rule held (more than one distinct trimmed title) -- otherwise the backfill
--      never touched the row and the Yes came from a person.
--   2. The normalised titles collapse to exactly one.
--   3. No (statement, year) holds two milestones. Without this the update would also catch the
--      indicators whose two rows are real -- a genuine pair, codes 1.0 and 1.1, whose
--      statements happen to match. outcomes.ftl opens a second row for those, so their Yes is
--      correct. On the verification database this is the difference between 67 rows and 54.
--
-- Known limitation: has_disaggregations carries no audit trail, so an indicator whose titles
-- drifted AND whose owner had already answered Yes by hand is indistinguishable from one the
-- backfill wrote. Such a row is reset here and has to be answered again. The alternative --
-- leaving every invented Yes in place -- was judged worse, since the answer contradicts what
-- the form now shows.
--
-- Scoped to global_unit_id >= 45 (BaseAction.isAiccra), matching the backfill: those are the
-- units whose form asks the question and renders the matrix at all.
--
-- Each phase keeps its own row, so this is per phase; nothing is replicated forward.

UPDATE crp_program_outcomes o
  JOIN crp_programs p ON p.id = o.crp_program_id
   SET o.has_disaggregations = NULL
 WHERE o.has_disaggregations = 1
   AND o.is_active = 1
   AND p.global_unit_id >= 45
   AND (SELECT COUNT(DISTINCT TRIM(COALESCE(m.title, '')))
          FROM crp_milestones m
         WHERE m.crp_program_outcome_id = o.id
           AND m.is_active = 1) > 1
   AND (SELECT COUNT(DISTINCT BINARY TRIM(REGEXP_REPLACE(
          REPLACE(REPLACE(REPLACE(REPLACE(REPLACE(COALESCE(m.title, ''),
            CHAR(0xC2A0 USING utf8mb4), ' '),
            CHAR(0xE2808B USING utf8mb4), ''),
            CHAR(0xE2808C USING utf8mb4), ''),
            CHAR(0xE2808D USING utf8mb4), ''),
            CHAR(0xEFBBBF USING utf8mb4), ''),
          '[[:space:]]+', ' ')))
          FROM crp_milestones m
         WHERE m.crp_program_outcome_id = o.id
           AND m.is_active = 1) = 1
   AND NOT EXISTS (
         SELECT 1
           FROM crp_milestones m
          WHERE m.crp_program_outcome_id = o.id
            AND m.is_active = 1
          GROUP BY BINARY TRIM(REGEXP_REPLACE(
            REPLACE(REPLACE(REPLACE(REPLACE(REPLACE(COALESCE(m.title, ''),
              CHAR(0xC2A0 USING utf8mb4), ' '),
              CHAR(0xE2808B USING utf8mb4), ''),
              CHAR(0xE2808C USING utf8mb4), ''),
              CHAR(0xE2808D USING utf8mb4), ''),
              CHAR(0xEFBBBF USING utf8mb4), ''),
            '[[:space:]]+', ' ')), m.year
         HAVING COUNT(*) > 1);
