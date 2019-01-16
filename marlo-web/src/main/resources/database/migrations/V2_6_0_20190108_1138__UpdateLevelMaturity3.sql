UPDATE rep_ind_stage_process set `year`=2017 where id=1;
UPDATE rep_ind_stage_process set `year`=2017 where id=2;

INSERT INTO rep_ind_stage_process(`name`,description, `year`, rep_ind_stage_studies_id)
VALUES ('Level 1', 'Research taken up by next user (decision maker or intermediary) = Level 1 of outcome/impact case', 2018, 1);

INSERT INTO rep_ind_stage_process(`name`,description, `year`, rep_ind_stage_studies_id)
VALUES ('Level 2', 'Policy/Law etc. Enacted = Level 2 of Outcome/Impact Case', 2018, 2);

INSERT INTO rep_ind_stage_process(`name`,description, `year`, rep_ind_stage_studies_id)
VALUES ('Level 3', 'Evidence of impact on people and/or natural environment of the changed policy or investment = Level 3 of Outcome/Impact Case', 2018, 3);
