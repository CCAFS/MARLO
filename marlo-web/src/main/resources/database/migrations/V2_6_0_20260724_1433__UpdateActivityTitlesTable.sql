ALTER TABLE activities_titles ADD global_unit_id bigint(10) NULL;
ALTER TABLE activities_titles ADD CONSTRAINT activities_titles_global_units_FK FOREIGN KEY (global_unit_id) REFERENCES global_units(id) ON DELETE RESTRICT ON UPDATE RESTRICT;

UPDATE activities_titles
SET global_unit_id = 45
WHERE global_unit_id IS NULL;