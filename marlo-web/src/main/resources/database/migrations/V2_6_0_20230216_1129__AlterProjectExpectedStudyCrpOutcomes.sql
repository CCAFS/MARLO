ALTER TABLE project_expected_study_crp_outcomes DROP FOREIGN KEY project_expected_study_crp_outcomes_FK;

ALTER TABLE project_expected_study_crp_outcomes MODIFY COLUMN id bigint(20) auto_increment NOT NULL;

ALTER TABLE project_expected_study_crp_outcomes ADD CONSTRAINT project_expected_study_crp_outcomes_FK_3 FOREIGN KEY (crp_outcome_id) REFERENCES crp_program_outcomes(id) ON DELETE RESTRICT ON UPDATE RESTRICT;
