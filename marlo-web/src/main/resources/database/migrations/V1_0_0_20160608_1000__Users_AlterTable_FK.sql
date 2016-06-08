update users Set modified_by=1;
alter table  users ADD CONSTRAINT fk_users_created_by_users_id FOREIGN KEY (created_by) REFERENCES users(id);
alter table  users ADD CONSTRAINT fk_users_modified_by_users_id FOREIGN KEY (modified_by) REFERENCES users(id);