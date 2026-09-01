/*
 * The feedback configuration that exists at this point belongs to AICCRA (global unit 45), the only
 * global unit using the feedback module. AICCRA is not created by the migrations -- they only seed
 * global_units up to 28 -- so the backfill is guarded: on a database built from scratch these
 * updates match no rows instead of failing with a foreign key error against
 * feedback_qa_commentable_fields_global_units_FK / feedback_roles_permissions_global_units_FK and
 * aborting the whole Flyway run.
 */

UPDATE feedback_qa_commentable_fields
SET global_unit_id = 45
WHERE EXISTS (SELECT 1 FROM global_units WHERE id = 45);

UPDATE feedback_roles_permissions
SET global_unit_id = 45
WHERE EXISTS (SELECT 1 FROM global_units WHERE id = 45);
