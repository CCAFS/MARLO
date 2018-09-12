SET FOREIGN_KEY_CHECKS=0;
DROP TABLE IF EXISTS `sustainable_development_goals`;
CREATE TABLE sustainable_development_goals
(id BIGINT(20) NOT NULL PRIMARY KEY AUTO_INCREMENT,
smo_code BIGINT(20),
sdg_name text,
icon text,
CONSTRAINT sustainable_unique_1 UNIQUE(smo_code)
);

insert into sustainable_development_goals(smo_code, sdg_name) values(1, 'Goal 1: No Poverty');
insert into sustainable_development_goals(smo_code, sdg_name) values(2, 'Goal 2: Zero Hunger');
insert into sustainable_development_goals(smo_code, sdg_name) values(3, 'Goal 3: Good Health and Well-Being for People');
insert into sustainable_development_goals(smo_code, sdg_name) values(4, 'Goal 4: Quality Education');
insert into sustainable_development_goals(smo_code, sdg_name) values(5, 'Goal 5: Gender Equality');
insert into sustainable_development_goals(smo_code, sdg_name) values(6, 'Goal 6: Clean Water and Sanitation');
insert into sustainable_development_goals(smo_code, sdg_name) values(7, 'Goal 7: Affordable and Clean Energy');
insert into sustainable_development_goals(smo_code, sdg_name) values(8, 'Goal 8: Decent Work and Economic Growth');
insert into sustainable_development_goals(smo_code, sdg_name) values(9, 'Goal 9: Industry, Innovation, and Infrastructure');
insert into sustainable_development_goals(smo_code, sdg_name) values(10, 'Goal 10: Reducing Inequalities');
insert into sustainable_development_goals(smo_code, sdg_name) values(11, 'Goal 11: Sustainable Cities and Communities');
insert into sustainable_development_goals(smo_code, sdg_name) values(12, 'Goal 12: Responsible Consumption and Production');
insert into sustainable_development_goals(smo_code, sdg_name) values(13, 'Goal 13: Climate Action');
insert into sustainable_development_goals(smo_code, sdg_name) values(14, 'Goal 14: Life Below Water');
insert into sustainable_development_goals(smo_code, sdg_name) values(15, 'Goal 15: Life on Land');
insert into sustainable_development_goals(smo_code, sdg_name) values(16, 'Goal 16: Peace, Justice and Strong Institutions');
insert into sustainable_development_goals(smo_code, sdg_name) values(17, 'Goal 17: Partnerships for the goals');