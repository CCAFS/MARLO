alter table deliverable_affiliations add is_active tinyint(1) NOT NULL DEFAULT '1';
alter table deliverable_affiliations add active_since timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP;
alter table deliverable_affiliations add created_by bigint NOT NULL;
alter table deliverable_affiliations add create_date timestamp NULL DEFAULT NULL;
update deliverable_affiliations set created_by = 1082 where created_by = 0;

alter table deliverable_metadata_external_sources add is_active tinyint(1) NOT NULL DEFAULT '1';
alter table deliverable_metadata_external_sources add active_since timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP;
alter table deliverable_metadata_external_sources add created_by bigint NOT NULL;
alter table deliverable_metadata_external_sources add create_date timestamp NULL DEFAULT NULL;

alter table deliverable_affiliations_not_mapped add is_active tinyint(1) NOT NULL DEFAULT '1';
alter table deliverable_affiliations_not_mapped add active_since timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP;
alter table deliverable_affiliations_not_mapped add created_by bigint NOT NULL;
alter table deliverable_affiliations_not_mapped add create_date timestamp NULL DEFAULT NULL;

alter table external_source_author add is_active tinyint(1) NOT NULL DEFAULT '1';
alter table external_source_author add active_since timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP;
alter table external_source_author add created_by bigint NOT NULL;
alter table external_source_author add create_date timestamp NULL DEFAULT NULL;

alter table deliverable_affiliations add CONSTRAINT `da_ibfk_8` FOREIGN KEY (`created_by`) REFERENCES `users` (`id`) ON DELETE CASCADE ON UPDATE CASCADE;
alter table deliverable_metadata_external_sources add CONSTRAINT `dmes_ibfk_3` FOREIGN KEY (`created_by`) REFERENCES `users` (`id`) ON DELETE CASCADE ON UPDATE CASCADE;
alter table deliverable_affiliations_not_mapped add CONSTRAINT `danm_ibfk_3` FOREIGN KEY (`created_by`) REFERENCES `users` (`id`) ON DELETE CASCADE ON UPDATE CASCADE;
alter table external_source_author add CONSTRAINT `esa_ibfk_2` FOREIGN KEY (`created_by`) REFERENCES `users` (`id`) ON DELETE CASCADE ON UPDATE CASCADE;