-- project_innovation_info.environmental_score_id holds an impact_area_scores id (ProjectInnovationInfo.hbm.xml maps it
-- to ImpactAreaScore), but V2_6_0_20251600_1405__UpdateInnovationInfoTable.sql gave it a second foreign key to
-- activities(id). Saving an Environmental score then only works where an activity happens to share the score id;
-- elsewhere every innovation save fails with MySQL 1452. The correct key,
-- project_innovation_info_impact_area_scores_FK_3, stays, and so does its index.
--
-- The drop runs only where the wrong key exists, so the migration is a no-op on databases that never had it and on a
-- second run.
SET @has_wrong_fk = (SELECT COUNT(*) FROM information_schema.TABLE_CONSTRAINTS
  WHERE CONSTRAINT_SCHEMA = DATABASE() AND TABLE_NAME = 'project_innovation_info'
    AND CONSTRAINT_NAME = 'project_innovation_info_activities_FK' AND CONSTRAINT_TYPE = 'FOREIGN KEY');
SET @drop_wrong_fk = IF(@has_wrong_fk > 0,
  'ALTER TABLE project_innovation_info DROP FOREIGN KEY project_innovation_info_activities_FK',
  'SELECT 1');
PREPARE drop_wrong_fk FROM @drop_wrong_fk;
EXECUTE drop_wrong_fk;
DEALLOCATE PREPARE drop_wrong_fk;
