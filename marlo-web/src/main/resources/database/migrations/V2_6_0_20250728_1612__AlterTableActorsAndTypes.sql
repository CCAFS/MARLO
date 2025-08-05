ALTER TABLE project_innovation_actors ADD other text NULL;
ALTER TABLE rep_ind_innovation_types ADD prms_name_equivalent text NULL;
ALTER TABLE rep_ind_innovation_types CHANGE clarisa_id_equivalent prms_id_equivalent bigint NULL;