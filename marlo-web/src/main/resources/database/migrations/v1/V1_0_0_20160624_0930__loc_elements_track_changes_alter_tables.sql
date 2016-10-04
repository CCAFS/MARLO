update loc_elements Set modified_by=1;
update loc_element_types Set modified_by=1;
update loc_geopositions Set modified_by=1;

update loc_elements Set is_active=1;
update loc_element_types Set is_active=1;
update loc_geopositions Set is_active=1;

alter table  loc_elements ADD CONSTRAINT fk_loc_elements_created_by_users_id FOREIGN KEY (created_by) REFERENCES users(id);
alter table  loc_element_types ADD CONSTRAINT fk_loc_element_types_created_by_users_id FOREIGN KEY (created_by) REFERENCES users(id);
alter table  loc_geopositions ADD CONSTRAINT fk_loc_geopositions_created_by_users_id FOREIGN KEY (created_by) REFERENCES users(id);


alter table  loc_element_types ADD CONSTRAINT fk_loc_element_types_modified_by_users_id FOREIGN KEY (modified_by) REFERENCES users(id);
alter table  loc_elements ADD CONSTRAINT fk_loc_elements_modified_by_users_id FOREIGN KEY (modified_by) REFERENCES users(id);
alter table  loc_geopositions ADD CONSTRAINT fk_loc_geopositions_modified_by_users_id FOREIGN KEY (modified_by) REFERENCES users(id);