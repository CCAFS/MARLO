ALTER TABLE project_innovation_references ADD has_evidence_by_deliverable tinyint(1) NULL;
ALTER TABLE project_innovation_references ADD deliverable_id bigint(20) NULL;
ALTER TABLE project_innovation_references ADD CONSTRAINT project_innovation_references_deliverables_FK FOREIGN KEY (deliverable_id) REFERENCES deliverables(id) ON DELETE RESTRICT ON UPDATE RESTRICT;

ALTER TABLE project_innovation_reference_urls CHANGE is_external_author has_evidence_by_deliverable tinyint(1) NULL;
ALTER TABLE project_innovation_reference_urls ADD deliverable_id bigint(20) NULL;
ALTER TABLE project_innovation_reference_urls ADD CONSTRAINT innovation_reference_urls_deliverables_FK FOREIGN KEY (deliverable_id) REFERENCES deliverables(id) ON DELETE RESTRICT ON UPDATE RESTRICT;

ALTER TABLE project_innovation_reference_complementary_solutions CHANGE is_external_author has_evidence_by_deliverable tinyint(1) NULL;
ALTER TABLE project_innovation_reference_complementary_solutions ADD deliverable_id bigint(20) NULL;
ALTER TABLE project_innovation_reference_complementary_solutions ADD CONSTRAINT innovation_complementary_deliverables_FK FOREIGN KEY (deliverable_id) REFERENCES deliverables(id) ON DELETE RESTRICT ON UPDATE RESTRICT;