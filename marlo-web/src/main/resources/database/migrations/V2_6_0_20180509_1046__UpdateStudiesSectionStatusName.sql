delete from section_statuses where section_name = 'expectedStudies' and project_expected_id is null;

update section_statuses set section_name= 'studies' where section_name='expectedStudies'