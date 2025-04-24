ALTER TABLE project_innovation_references DROP FOREIGN KEY project_innovation_references_st_impact_areas_FK;
ALTER TABLE project_innovation_references DROP COLUMN impact_area_id;
ALTER TABLE project_innovation_references ADD is_gender tinyint(1) NULL;
ALTER TABLE project_innovation_references ADD is_climate_change tinyint(1) NULL;
ALTER TABLE project_innovation_references ADD is_nutrition tinyint(1) NULL;
ALTER TABLE project_innovation_references ADD is_environmental tinyint(1) NULL;
ALTER TABLE project_innovation_references ADD is_poverty tinyint(1) NULL;
ALTER TABLE project_innovation_references ADD is_innovation_readiness tinyint(1) NULL;
ALTER TABLE project_innovation_references ADD evidence_source text NULL;