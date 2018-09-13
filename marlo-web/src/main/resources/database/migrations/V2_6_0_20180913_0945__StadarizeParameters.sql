Delete from custom_parameters where global_unit_id = 23;
Delete from parameters where global_unit_type_id = 2;

INSERT INTO parameters (global_unit_type_id, `key`, description, format, category)
VALUES (3, 'crp_show_project_outcome_communications', 'Ask for communications in Project Outcomes', 1,2);

INSERT INTO parameters (global_unit_type_id, `key`, description, format, category)
VALUES (3, 'crp_contact_point_edit_project', 'Contact Point Can Edit Projects', 1,2);

INSERT INTO parameters (global_unit_type_id, `key`, description, format, category)
VALUES (4, 'crp_show_project_outcome_communications', 'Ask for communications in Project Outcomes', 1,2);

INSERT INTO custom_parameters (parameter_id, `value`, created_by, is_active, active_since, modified_by, modification_justification, global_unit_id)
SELECT p.id, 'false', 3,1,NOW(),3,'',24 from parameters p where p.`key` = 'crp_show_project_outcome_communications' and p.global_unit_type_id = 3;

INSERT INTO custom_parameters (parameter_id, `value`, created_by, is_active, active_since, modified_by, modification_justification, global_unit_id)
SELECT p.id, 'false', 3,1,NOW(),3,'',25 from parameters p where p.`key` = 'crp_show_project_outcome_communications' and p.global_unit_type_id = 3;

INSERT INTO custom_parameters (parameter_id, `value`, created_by, is_active, active_since, modified_by, modification_justification, global_unit_id)
SELECT p.id, 'false', 3,1,NOW(),3,'',24 from parameters p where p.`key` = 'crp_contact_point_edit_project' and p.global_unit_type_id = 3;

INSERT INTO custom_parameters (parameter_id, `value`, created_by, is_active, active_since, modified_by, modification_justification, global_unit_id)
SELECT p.id, 'false', 3,1,NOW(),3,'',25 from parameters p where p.`key` = 'crp_contact_point_edit_project' and p.global_unit_type_id = 3;

INSERT INTO custom_parameters (parameter_id, `value`, created_by, is_active, active_since, modified_by, modification_justification, global_unit_id)
SELECT p.id, 'false', 3,1,NOW(),3,'',29 from parameters p where p.`key` = 'crp_show_project_outcome_communications' and p.global_unit_type_id = 4;

UPDATE parameters SET global_unit_type_id=1 where id=129;

INSERT INTO parameters (global_unit_type_id, `key`, description, format, category)
VALUES (3, 'crp_has_research_human', 'Ask if the study involves research with human subjects', 1,2);

INSERT INTO parameters (global_unit_type_id, `key`, description, format, category)
VALUES (4, 'crp_has_research_human', 'Ask if the study involves research with human subjects', 1,2);

INSERT INTO custom_parameters (parameter_id, `value`, created_by, is_active, active_since, modified_by, modification_justification, global_unit_id)
SELECT p.id, 'false', 3,1,NOW(),3,'',24 from parameters p where p.`key` = 'crp_has_research_human' and p.global_unit_type_id = 3;

INSERT INTO custom_parameters (parameter_id, `value`, created_by, is_active, active_since, modified_by, modification_justification, global_unit_id)
SELECT p.id, 'false', 3,1,NOW(),3,'',25 from parameters p where p.`key` = 'crp_has_research_human' and p.global_unit_type_id = 3;

INSERT INTO custom_parameters (parameter_id, `value`, created_by, is_active, active_since, modified_by, modification_justification, global_unit_id)
SELECT p.id, 'false', 3,1,NOW(),3,'',29 from parameters p where p.`key` = 'crp_has_research_human' and p.global_unit_type_id = 4;

INSERT INTO custom_parameters (parameter_id, `value`, created_by, is_active, active_since, modified_by, modification_justification, global_unit_id)
SELECT p.id, 'false', 3,1,NOW(),3,'',1 from parameters p where p.`key` = 'crp_has_research_human' and p.global_unit_type_id = 1;

INSERT INTO custom_parameters (parameter_id, `value`, created_by, is_active, active_since, modified_by, modification_justification, global_unit_id)
SELECT p.id, 'false', 3,1,NOW(),3,'',4 from parameters p where p.`key` = 'crp_has_research_human' and p.global_unit_type_id = 1;

INSERT INTO custom_parameters (parameter_id, `value`, created_by, is_active, active_since, modified_by, modification_justification, global_unit_id)
SELECT p.id, 'false', 3,1,NOW(),3,'',7 from parameters p where p.`key` = 'crp_has_research_human' and p.global_unit_type_id = 1;

INSERT INTO custom_parameters (parameter_id, `value`, created_by, is_active, active_since, modified_by, modification_justification, global_unit_id)
SELECT p.id, 'false', 3,1,NOW(),3,'',11 from parameters p where p.`key` = 'crp_has_research_human' and p.global_unit_type_id = 1;

INSERT INTO custom_parameters (parameter_id, `value`, created_by, is_active, active_since, modified_by, modification_justification, global_unit_id)
SELECT p.id, 'false', 3,1,NOW(),3,'',21 from parameters p where p.`key` = 'crp_has_research_human' and p.global_unit_type_id = 1;

INSERT INTO custom_parameters (parameter_id, `value`, created_by, is_active, active_since, modified_by, modification_justification, global_unit_id)
SELECT p.id, 'false', 3,1,NOW(),3,'',22 from parameters p where p.`key` = 'crp_has_research_human' and p.global_unit_type_id = 1;

INSERT INTO custom_parameters (parameter_id, `value`, created_by, is_active, active_since, modified_by, modification_justification, global_unit_id)
SELECT p.id, 'false', 3,1,NOW(),3,'',17 from parameters p where p.`key` = 'crp_has_research_human' and p.global_unit_type_id = 1;

INSERT INTO custom_parameters (parameter_id, `value`, created_by, is_active, active_since, modified_by, modification_justification, global_unit_id)
SELECT p.id, 'false', 3,1,NOW(),3,'',27 from parameters p where p.`key` = 'crp_has_research_human' and p.global_unit_type_id = 1;

INSERT INTO custom_parameters (parameter_id, `value`, created_by, is_active, active_since, modified_by, modification_justification, global_unit_id)
SELECT p.id, 'false', 3,1,NOW(),3,'',28 from parameters p where p.`key` = 'crp_has_research_human' and p.global_unit_type_id = 1;

DELETE FROM custom_parameters where id  = 952;

INSERT INTO custom_parameters (parameter_id, `value`, created_by, is_active, active_since, modified_by, modification_justification, global_unit_id)
SELECT p.id, 'false', 3,1,NOW(),3,'',16 from parameters p where p.`key` = 'crp_has_research_human' and p.global_unit_type_id = 1;