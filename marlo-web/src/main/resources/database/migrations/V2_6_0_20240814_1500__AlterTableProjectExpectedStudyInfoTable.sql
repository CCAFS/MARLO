ALTER TABLE project_expected_study_info MODIFY COLUMN tag_id bigint(20) NULL;
ALTER TABLE project_expected_study_info ADD CONSTRAINT project_expected_study_info_ibfk_18 FOREIGN KEY (tag_id) 
REFERENCES project_expected_study_tags(id) ON DELETE RESTRICT ON UPDATE RESTRICT;