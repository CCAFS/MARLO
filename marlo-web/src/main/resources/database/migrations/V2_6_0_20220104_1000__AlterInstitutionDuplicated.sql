Alter table institutions add parent_id bigint(20) NULL;

Alter table institutions add `is_parent` tinyint(1) DEFAULT 1;