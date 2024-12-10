ALTER TABLE project_innovation_references ADD type_id bigint NULL;
ALTER TABLE project_innovation_references ADD CONSTRAINT project_innovation_references_deliverable_types_FK FOREIGN KEY (id) REFERENCES deliverable_types(id) ON DELETE RESTRICT ON UPDATE RESTRICT;
ALTER TABLE project_innovation_reference_urls ADD type_id bigint NULL;
ALTER TABLE project_innovation_reference_urls ADD CONSTRAINT innovation_reference_urls_del_types_FK FOREIGN KEY (type_id) REFERENCES deliverable_types(id) ON DELETE RESTRICT ON UPDATE RESTRICT;
ALTER TABLE project_innovation_reference_complementary_solutions ADD type_id bigint NULL;
ALTER TABLE project_innovation_reference_complementary_solutions ADD CONSTRAINT complementary_solutions_deliverable_types_FK FOREIGN KEY (type_id) REFERENCES deliverable_types(id) ON DELETE RESTRICT ON UPDATE RESTRICT;