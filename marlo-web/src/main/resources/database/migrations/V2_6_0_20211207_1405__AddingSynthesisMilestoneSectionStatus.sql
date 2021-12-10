alter table section_statuses add column synthesis_milestone_id BIGINT default null;
alter table section_statuses add KEY synthesis_milestone_id (synthesis_milestone_id);
alter table section_statuses add CONSTRAINT `section_statuses_synthesis_milestone_fk` FOREIGN KEY (synthesis_milestone_id) REFERENCES report_synthesis_flagship_progress_outcome_milestones (`id`); 