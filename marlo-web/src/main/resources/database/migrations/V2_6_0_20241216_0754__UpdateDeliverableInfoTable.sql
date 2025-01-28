ALTER TABLE deliverables_info ADD commissioning_study text NULL;
ALTER TABLE deliverables_info ADD activity_description text NULL;
ALTER TABLE deliverables_info ADD study_type_id bigint(20) NULL;
ALTER TABLE deliverables_info ADD CONSTRAINT deliverables_info_study_types_FK FOREIGN KEY (study_type_id) REFERENCES study_types(id) ON DELETE RESTRICT ON UPDATE RESTRICT;