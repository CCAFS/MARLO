-- The parameter key was misspelt 'crp_taw_api' when it was first seeded in 2017 -- the service is Tawk.to,
-- so the segment is 'tawk'. Renaming the key is enough: custom_parameters joins parameters by parameter_id,
-- so every Global Unit keeps its value without being touched, and the superadmin screen shows the new name
-- (as 'system_tawk_api', since that view relabels the 'crp_' prefix for display only).
--
-- Three safety notes, because none of them is visible in the statement:
--
-- 1. parameters has no unique index on `key` -- the only indexes are the primary key and the
--    global_unit_type_id foreign key. A second row with the same key and type would therefore be created
--    silently rather than rejected. The LEFT JOIN below is that missing constraint: a row is renamed only
--    when no 'crp_tawk_api' row already holds its Global Unit type. Re-running the migration renames
--    nothing, and a database where the rename was already applied by hand is left alone.
--
-- 2. The earlier migrations that reference 'crp_taw_api' (V2_0_0_20170824_1037, V2_5_0_20180130_0840,
--    V2_6_0_20180806_1123, V2_6_0_20181126_1049) are NOT edited. They describe what happened when they ran,
--    and rewriting applied history is what makes a Flyway baseline untrustworthy -- the more so here, where
--    MarloFlywayConfiguration calls repair() on every startup, so a rewritten file would be quietly
--    re-checksummed instead of failing loudly.
--
-- 3. MarloFlywayConfiguration also sets outOfOrder(true), so a migration added later with an earlier
--    timestamp still runs, after this one. That is why the companion migration
--    V2_6_0_20260922_0756__DocumentTawktoWidgetIdInParameterDescription matches both spellings of the key
--    instead of only the old one: neither of the two migrations depends on running before the other.

UPDATE parameters p
LEFT JOIN (
  SELECT global_unit_type_id, 1 AS found FROM parameters WHERE `key` = 'crp_tawk_api'
) AS existing ON existing.global_unit_type_id <=> p.global_unit_type_id
SET p.`key` = 'crp_tawk_api'
WHERE p.`key` = 'crp_taw_api' AND existing.found IS NULL;
