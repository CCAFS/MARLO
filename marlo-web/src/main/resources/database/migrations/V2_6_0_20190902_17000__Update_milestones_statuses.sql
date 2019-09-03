insert into general_statuses (id,name,is_active,active_since,created_by)
values (6,'Changed',1,sysdate(),1057);

insert into general_statuses_table (table_name,general_status_id,is_active,active_since,created_by)
values('report_synthesis_flagship_progress_milestones',6,1,sysdate(),1081);

update general_statuses_table
set table_name='report_synthesis_flagship_progress_outcome_milestones'
where table_name = 'report_synthesis_flagship_progress_milestones';

-- update Changed status 
update report_synthesis_flagship_progress_outcome_milestones
set milestones_status = 6
where milestones_status = 4 
;
-- update Cancelled status 
update report_synthesis_flagship_progress_outcome_milestones
set milestones_status = 5
where milestones_status = 3  
;
-- update Extended status
update report_synthesis_flagship_progress_outcome_milestones
set milestones_status = 4
where milestones_status = 2
;
-- update Complete status
update report_synthesis_flagship_progress_outcome_milestones
set milestones_status = 3
where milestones_status = 1
;