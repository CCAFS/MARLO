alter table glossary add column `active_since` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP;
alter table glossary add column `is_active` tinyint(1) DEFAULT '1';