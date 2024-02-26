ALTER TABLE shfrm_priority_actions MODIFY COLUMN is_active tinyint(1) DEFAULT 1 NOT NULL;

ALTER TABLE shfrm_sub_actions MODIFY COLUMN is_active tinyint(1) DEFAULT 1 NOT NULL;

ALTER TABLE deliverable_shfrm_priority_action MODIFY COLUMN is_active tinyint(1) DEFAULT 1 NOT NULL;

ALTER TABLE deliverable_shfmr_sub_action MODIFY COLUMN is_active tinyint(1) DEFAULT 1 NOT NULL;



