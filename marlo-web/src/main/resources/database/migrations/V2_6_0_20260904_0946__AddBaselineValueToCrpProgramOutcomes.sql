-- A2-2437: add the Baseline value field to the Overall Performance Indicators form.
--
-- The redesigned section shows a Baseline value beside the Baseline year, but there was no
-- column behind it, so the field was rendered read-only and never submitted. This adds the
-- column so it can be captured like the closing Target Value it sits next to.
--
-- Mirrors `value` (the closing target) in type and precision: both hold an indicator target,
-- and reports compare them against each other.
--
-- Nullable on purpose: the indicators already loaded have no baseline recorded, and making it
-- NOT NULL would either need an arbitrary backfill or flag every existing row as incomplete.
-- OutcomeValidator does not require it yet; that can be tightened once the data is captured.

ALTER TABLE crp_program_outcomes
  ADD baseline_value decimal(20,2) NULL AFTER start_year;
