/*
 * The timeline rows that exist at this point belong to AICCRA (global unit 45). AICCRA is not
 * created by the migrations, so the backfill is guarded: on a database built from scratch this
 * update matches no rows instead of failing against timeline_global_units_FK. The table happens to
 * be empty on a fresh database today, which is the only reason the unguarded version survived, so
 * the guard also protects against a future migration seeding it.
 */

UPDATE timeline
	SET global_unit_id = 45
	WHERE EXISTS (SELECT 1 FROM global_units WHERE id = 45);
