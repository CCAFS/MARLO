ALTER TABLE activities_titles ADD global_unit_id bigint(10) NULL;
ALTER TABLE activities_titles ADD CONSTRAINT activities_titles_global_units_FK FOREIGN KEY (global_unit_id) REFERENCES global_units(id) ON DELETE RESTRICT ON UPDATE RESTRICT;

/*
 * The activity titles seeded by V2_6_0_20210510_1708__InsertActivitiesTitle.sql (28 rows) and
 * V2_6_0_20210615_0743__InsertActivitiesTitle2.sql (1 row) belong to AICCRA (global unit 45).
 * AICCRA is not created by the migrations, so the backfill is guarded: on a database built from
 * scratch those 29 rows keep a null global unit instead of failing against
 * activities_titles_global_units_FK and aborting the whole Flyway run.
 */
UPDATE activities_titles
SET global_unit_id = 45
WHERE global_unit_id IS NULL
AND EXISTS (SELECT 1 FROM global_units WHERE id = 45);
