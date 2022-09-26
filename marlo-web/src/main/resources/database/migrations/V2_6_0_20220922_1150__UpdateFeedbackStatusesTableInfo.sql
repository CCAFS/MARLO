update feedback_statuses set status_consolidation_name = status_name, visibility = 2 where id = 1;
update feedback_statuses set status_consolidation_name = status_name, visibility = 2 where id = 2;
update feedback_statuses set status_name = 'Draft', status_consolidation_name = 'Draft', visibility = 1 where id = 3;
update feedback_statuses set status_consolidation_name = 'Admitted', status_name = 'Pending', visibility = 3 where id = 4;
update feedback_statuses set status_consolidation_name =  status_name, visibility = 2 where id = 5;
update feedback_statuses set status_consolidation_name =  status_name, visibility = 1 where id = 6;