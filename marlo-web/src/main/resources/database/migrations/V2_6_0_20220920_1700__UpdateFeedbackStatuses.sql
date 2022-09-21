ALTER TABLE feedback_statuses ADD is_public tinyint(1) NOT NULL;

update feedback_statuses set status_name = 'Agreed', status_description = 'Comment approved by CL', is_public = 1 where id = 1;
update feedback_statuses set is_public = 1 where id = 2;
update feedback_statuses set status_name = 'Pending', status_description = 'Comment without status (draft comment)', is_public = 0 where id = 3;
update feedback_statuses set status_name = 'Admitted', status_description = 'Comment admitted by PMC (no draft comment)', is_public = 0 where id = 4;
update feedback_statuses set status_name = 'Disagreed', status_description = 'Comment disagreed by CL', is_public = 1 where id = 5;
update feedback_statuses set status_name = 'Dismissed', status_description = 'Comment dismissed by PMC (no draft comment)', is_public = 0 where id = 6;