alter table  crp_ppa_partners ADD COLUMN `is_active`  tinyint(1) NOT NULL,
ADD COLUMN `created_by`  bigint(20) NULL DEFAULT NULL AFTER `is_active`,
ADD COLUMN `active_since`  timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP AFTER `created_by`,
ADD COLUMN `modified_by`  bigint(20) NOT NULL AFTER `active_since`,
ADD COLUMN `modification_justification`  text CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL AFTER `modified_by`;

alter table  crp_ppa_partners ADD CONSTRAINT fk_crp_ppa_partners_created_by_users_id FOREIGN KEY (created_by) REFERENCES users(id);
alter table  crp_ppa_partners ADD CONSTRAINT fk_crp_ppa_partners_modified_by_users_id FOREIGN KEY (modified_by) REFERENCES users(id);