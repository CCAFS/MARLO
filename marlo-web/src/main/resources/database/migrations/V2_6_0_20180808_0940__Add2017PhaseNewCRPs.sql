UPDATE phases set `year`=2017 where id = 185;
UPDATE phases set `year`=2017 where id = 186;

UPDATE phases set `year`=2018 where id = 187;
UPDATE phases set `year`=2018 where id = 188;

UPDATE phases set `year`=2019 where id = 189;
UPDATE phases set `year`=2019 where id = 190;

UPDATE phases set `year`=2020 where id = 191;
UPDATE phases set `year`=2020 where id = 192;

UPDATE phases set `year`=2021 where id = 193;
UPDATE phases set `year`=2021 where id = 194;

INSERT INTO phases (id, description, `year`, start_date, end_date, editable, visible, next_phase, global_unit_id)
VALUES (286, 'Reporting', 2022, '2021-10-01', '2023-01-15', 0,0,null,16);
INSERT INTO phases (id, description, `year`, start_date, end_date, editable, visible, next_phase, global_unit_id)
VALUES (285, 'Planning', 2022, '2021-10-01', '2023-01-15', 0,0,286,16);
UPDATE phases set `next_phase`=285 where id = 194;

--

UPDATE phases set `year`=2017 where id = 255;
UPDATE phases set `year`=2017 where id = 256;

UPDATE phases set `year`=2018 where id = 257;
UPDATE phases set `year`=2018 where id = 258;

UPDATE phases set `year`=2019 where id = 259;
UPDATE phases set `year`=2019 where id = 260;

UPDATE phases set `year`=2020 where id = 261;
UPDATE phases set `year`=2020 where id = 262;

UPDATE phases set `year`=2021 where id = 263;
UPDATE phases set `year`=2021 where id = 264;

INSERT INTO phases (id, description, `year`, start_date, end_date, editable, visible, next_phase, global_unit_id)
VALUES (288, 'Reporting', 2022, '2021-10-01', '2023-01-15', 0,0,null,17);
INSERT INTO phases (id, description, `year`, start_date, end_date, editable, visible, next_phase, global_unit_id)
VALUES (287, 'Planning', 2022, '2021-10-01', '2023-01-15', 0,0,288,17);
UPDATE phases set `next_phase`=287 where id = 264;

--

UPDATE phases set `year`=2017 where id = 265;
UPDATE phases set `year`=2017 where id = 266;

UPDATE phases set `year`=2018 where id = 267;
UPDATE phases set `year`=2018 where id = 268;

UPDATE phases set `year`=2019 where id = 269;
UPDATE phases set `year`=2019 where id = 270;

UPDATE phases set `year`=2020 where id = 271;
UPDATE phases set `year`=2020 where id = 272;

UPDATE phases set `year`=2021 where id = 273;
UPDATE phases set `year`=2021 where id = 274;

INSERT INTO phases (id, description, `year`, start_date, end_date, editable, visible, next_phase, global_unit_id)
VALUES (290, 'Reporting', 2022, '2021-10-01', '2023-01-15', 0,0,null,27);
INSERT INTO phases (id, description, `year`, start_date, end_date, editable, visible, next_phase, global_unit_id)
VALUES (289, 'Planning', 2022, '2021-10-01', '2023-01-15', 0,0,290,27);
UPDATE phases set `next_phase`=287 where id = 274;

--

UPDATE phases set `year`=2017 where id = 275;
UPDATE phases set `year`=2017 where id = 276;

UPDATE phases set `year`=2018 where id = 277;
UPDATE phases set `year`=2018 where id = 278;

UPDATE phases set `year`=2019 where id = 279;
UPDATE phases set `year`=2019 where id = 280;

UPDATE phases set `year`=2020 where id = 281;
UPDATE phases set `year`=2020 where id = 282;

UPDATE phases set `year`=2021 where id = 283;
UPDATE phases set `year`=2021 where id = 284;

INSERT INTO phases (id, description, `year`, start_date, end_date, editable, visible, next_phase, global_unit_id)
VALUES (292, 'Reporting', 2022, '2021-10-01', '2023-01-15', 0,0,null,28);
INSERT INTO phases (id, description, `year`, start_date, end_date, editable, visible, next_phase, global_unit_id)
VALUES (291, 'Planning', 2022, '2021-10-01', '2023-01-15', 0,0,292,28);
UPDATE phases set `next_phase`=291 where id = 284;