ALTER TABLE project_innovation_references ADD impact_area_id bigint(20) NULL;
ALTER TABLE project_innovation_references ADD CONSTRAINT project_innovation_references_st_impact_areas_FK FOREIGN KEY (impact_area_id) REFERENCES st_impact_areas(id) ON DELETE RESTRICT ON UPDATE RESTRICT;

ALTER TABLE st_impact_areas ADD tag text NULL;
