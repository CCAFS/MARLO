-- A2-2437: store the answer to "Does this indicator have disaggregations?".
--
-- The redesigned Overall Performance Indicators form asks the question, but the answer had no
-- column: outcomes.ftl recomputed it from the milestone rows (more than one distinct statement
-- meant "Yes"). That could not tell "answered No" apart from "never answered", which PMU needs
-- for completeness reporting.
--
-- Deliberately tri-state, hence nullable:
--   NULL -> the question was never answered (every row already loaded, and every global unit
--           whose form does not show the question at all)
--   0    -> answered No
--   1    -> answered Yes
-- A backfill was intentionally not run: marking the existing indicators that happen to have
-- disaggregations as "answered Yes" would record an answer nobody actually gave.
--
-- The milestone rows remain the source of truth for what is *displayed*: outcomes.ftl shows the
-- disaggregations whenever they exist, even against a stored 0, so a stale flag can never hide
-- real data. The column records the answer; it does not override the data it describes.
--
-- tinyint(1) NULL mirrors the other has_* answer columns in this schema (has_participants,
-- has_doi, has_patent_pvp).

ALTER TABLE crp_program_outcomes
  ADD has_disaggregations tinyint(1) NULL AFTER baseline_value;
