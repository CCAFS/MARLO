#script migration bi-parameters global unit scoping

/*
 * bi_parameters was created as a platform-wide configuration table, so every global unit shared the
 * same values. It now follows the same scoping already used by bi_reports: every row belongs to the
 * global unit set in global_unit_id.
 */

ALTER TABLE `bi_parameters`
ADD COLUMN `global_unit_id`  BIGINT (20) NULL AFTER `parameter_value`;

ALTER TABLE `bi_parameters` ADD CONSTRAINT `bi_parameters_ibfk_1` FOREIGN KEY (`global_unit_id`) REFERENCES `global_units` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT;

/*
 * The BI configuration that already exists belongs to AICCRA (global unit 45), the only global unit
 * using the BI module so far. AICCRA is not created by the migrations, so the backfill only runs
 * when that global unit is present: on a database built from scratch the rows are left with no
 * global unit instead of failing the migration with a foreign key error.
 */
UPDATE `bi_parameters`
SET `global_unit_id` = 45
WHERE `global_unit_id` IS NULL
AND EXISTS (SELECT 1 FROM `global_units` WHERE `id` = 45);

/*
 * bi_reports already had global_unit_id, but it was never used to filter the reports, so the rows
 * either kept the global unit of the migration that seeded the table (global unit 1) or were left
 * with no global unit at all. Both cases are the reports AICCRA uses, so they are moved to global
 * unit 45. Rows pointing at any other global unit are left untouched.
 *
 * The OR is parenthesised on purpose: AND binds tighter than OR, so without the parentheses the
 * global unit 45 check would only guard the IS NULL branch and the global unit 1 rows would be
 * updated even when AICCRA does not exist, failing with a foreign key error.
 */
UPDATE `bi_reports`
SET `global_unit_id` = 45
WHERE (`global_unit_id` = 1 OR `global_unit_id` IS NULL)
AND EXISTS (SELECT 1 FROM `global_units` WHERE `id` = 45);
