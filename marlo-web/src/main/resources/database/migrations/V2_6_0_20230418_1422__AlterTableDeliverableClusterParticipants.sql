ALTER TABLE deliverable_cluster_participants ADD is_active tinyint(1) NOT NULL;
ALTER TABLE deliverable_cluster_participants ADD active_since timestamp NOT NULL;
ALTER TABLE deliverable_cluster_participants ADD created_by bigint(20) NOT NULL;
ALTER TABLE deliverable_cluster_participants ADD modified_by bigint(20) NULL;
ALTER TABLE deliverable_cluster_participants ADD modification_justification text NULL;
ALTER TABLE deliverable_cluster_participants ADD CONSTRAINT deliverable_cluster_participants_FK_3 FOREIGN KEY (created_by) REFERENCES users(id) ON DELETE RESTRICT ON UPDATE RESTRICT;
ALTER TABLE deliverable_cluster_participants ADD CONSTRAINT deliverable_cluster_participants_FK_4 FOREIGN KEY (modified_by) REFERENCES users(id) ON DELETE RESTRICT ON UPDATE RESTRICT;