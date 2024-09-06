ALTER TABLE project_expected_study_quantifications ADD quantification_type_id bigint(20) NULL;
ALTER TABLE project_expected_study_quantifications ADD CONSTRAINT project_expected_study_quantifications_quantification_types_FK FOREIGN KEY (quantification_type_id) REFERENCES quantification_types(id) ON DELETE RESTRICT ON UPDATE RESTRICT;