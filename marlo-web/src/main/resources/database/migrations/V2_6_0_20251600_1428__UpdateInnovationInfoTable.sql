ALTER TABLE project_innovation_info CHANGE has_knowledge_potential has_knowledge_potential_id bigint(1) NULL;
ALTER TABLE project_innovation_info MODIFY COLUMN has_knowledge_potential_id bigint(1) NULL;
ALTER TABLE project_innovation_info ADD CONSTRAINT project_innovation_info_rep_ind_options_FK FOREIGN KEY (has_knowledge_potential_id) REFERENCES rep_ind_options(id) ON DELETE RESTRICT ON UPDATE RESTRICT;
