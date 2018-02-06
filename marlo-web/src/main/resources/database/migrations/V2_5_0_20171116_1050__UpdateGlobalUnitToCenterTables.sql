Update center_areas set global_unit_id = (select id from global_units where acronym = 'CIAT');
Update center_objectives set global_unit_id = (select id from global_units where acronym = 'CIAT');
Update center_leaders set global_unit_id = (select id from global_units where acronym = 'CIAT');