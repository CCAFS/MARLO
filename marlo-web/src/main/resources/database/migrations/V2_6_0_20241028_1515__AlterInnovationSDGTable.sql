ALTER TABLE project_innovation_sdgs ADD is_active tinyint(1) NOT NULL;
ALTER TABLE project_innovation_sdgs ADD active_since timestamp DEFAULT CURRENT_TIMESTAMP  on update CURRENT_TIMESTAMP NULL;
ALTER TABLE project_innovation_sdgs ADD created_by bigint(20) NULL;
ALTER TABLE project_innovation_sdgs ADD modified_by bigint(20) NULL;
ALTER TABLE project_innovation_sdgs ADD modification_justification text CHARACTER SET utf8 COLLATE utf8_general_ci NULL;
ALTER TABLE project_innovation_sdgs ADD CONSTRAINT innovation_sdgs_users_FK FOREIGN KEY (created_by) REFERENCES users(id) ON DELETE RESTRICT ON UPDATE RESTRICT;
ALTER TABLE project_innovation_sdgs ADD CONSTRAINT innovation_sdgs_users_FK_1 FOREIGN KEY (modified_by) REFERENCES users(id) ON DELETE RESTRICT ON UPDATE RESTRICT;