-- -----------------------------------------------------
-- Table report_synthesis_partnerships
-- -----------------------------------------------------
DROP TABLE IF EXISTS report_synthesis_partnerships_countries ;
DROP TABLE IF EXISTS report_synthesis_partnerships_flagships ;
DROP TABLE IF EXISTS report_synthesis_partnerships_phases_research ;
DROP TABLE IF EXISTS report_synthesis_partnerships ;
DROP TABLE IF EXISTS report_synthesis_partnerships_ind;

DROP TABLE IF EXISTS report_synthesis_trainees_ind;
DROP TABLE IF EXISTS report_synthesis_participants_ind;
DROP TABLE IF EXISTS rep_ind_training_terms;
DROP TABLE IF EXISTS rep_ind_participant_user_types;


-- -----------------------------------------------------
-- Table report_synthesis_partnerships
-- -----------------------------------------------------
create or replace view v_project_expected_studies_info_reported as
select pesi.*
from project_expected_studies pes 
INNER JOIN project_expected_study_info pesi ON pesi.project_expected_study_id = pes.id 
INNER join phases ph on ph.id = pesi.id_phase
LEFT join projects_info pinf on pinf.project_id = pes.project_id and pinf.id_phase = pesi.id_phase
where NOT EXISTS (
  SELECT *
  FROM report_synthesis_crp_progress_studies rs
  WHERE rs.project_expected_studies_id = pes.id
  AND rs.is_active
)
and pes.is_active =1 
and pes.year = ph.year   
and !IFNULL(pinf.administrative,0)
and ph.name = 'AR'
and pesi.study_type_id = 1
and ph.year < year(sysdate())
;

/**************************************************************
* * * * *              PARTNERSHIPS                   * * * * *
***************************************************************/
-- -----------------------------------------------------
-- Table report_synthesis_partnerships
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS report_synthesis_partnerships (
  id BIGINT(20) NOT NULL AUTO_INCREMENT,
  institution_id BIGINT(20) NOT NULL,
  phase_id BIGINT(20) NOT NULL,
  main_area TEXT NULL,
  geographic_scope_id BIGINT(20),
  region_id BIGINT(20) ,
  is_active TINYINT(1) NOT NULL,
  active_since TIMESTAMP NULL,
  created_by BIGINT(20) NOT NULL,
  modified_by BIGINT(20),
  modification_justification TEXT NULL,
  PRIMARY KEY (id),
  INDEX fk_report_synthesis_partnerships_institutions1_idx (institution_id ASC),
  INDEX fk_report_synthesis_partnerships_phases1_idx (phase_id ASC),
  INDEX fk_report_synthesis_partnerships_users2_idx (modified_by ASC),
  INDEX fk_report_synthesis_partnerships_users1_idx (created_by ASC),
  INDEX fk_report_synthesis_partnerships_rep_ind_geographic_scopes1_idx (geographic_scope_id ASC),
  INDEX fk_report_synthesis_partnerships_rep_ind_regions1_idx (region_id ASC),
  CONSTRAINT fk_report_synthesis_partnerships_institutions1
    FOREIGN KEY (institution_id)
    REFERENCES institutions (id)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT fk_report_synthesis_partnerships_phases1
    FOREIGN KEY (phase_id)
    REFERENCES phases (id)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT fk_report_synthesis_partnerships_users2
    FOREIGN KEY (modified_by)
    REFERENCES users (id)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT fk_report_synthesis_partnerships_users1
    FOREIGN KEY (created_by)
    REFERENCES users (id)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT fk_report_synthesis_partnerships_rep_ind_geographic_scopes1
    FOREIGN KEY (geographic_scope_id)
    REFERENCES rep_ind_geographic_scopes (id)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT fk_report_synthesis_partnerships_rep_ind_regions1
    FOREIGN KEY (region_id)
    REFERENCES rep_ind_regions (id)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table report_synthesis_partnerships_flagships
-- -----------------------------------------------------


CREATE TABLE IF NOT EXISTS report_synthesis_partnerships_flagships (
  id BIGINT(20) NOT NULL AUTO_INCREMENT,
  partnership_id BIGINT(20) NOT NULL,
  crp_program_id BIGINT(20) NOT NULL,
  PRIMARY KEY (id),
  INDEX fk_report_synthesis_partnerships_flagships_crp_programs1_idx (crp_program_id ASC),
  INDEX fk_report_synthesis_partnerships_flagships_report_synthesis_idx (partnership_id ASC),
  CONSTRAINT fk_report_synthesis_partnerships_flagships_crp_programs1
    FOREIGN KEY (crp_program_id)
    REFERENCES crp_programs (id)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT fk_report_synthesis_partnerships_flagships_report_synthesis_p1
    FOREIGN KEY (partnership_id)
    REFERENCES report_synthesis_partnerships (id)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table report_synthesis_partnerships_phases_research
-- -----------------------------------------------------

CREATE TABLE IF NOT EXISTS report_synthesis_partnerships_phases_research (
  id BIGINT(20) NOT NULL AUTO_INCREMENT,
  partnership_id BIGINT(20) NOT NULL,
  phase_research_id BIGINT(20) NOT NULL,
  INDEX fk_report_synthesis_partnerships_phases_research_rep_ind_ph_idx (phase_research_id ASC),
  PRIMARY KEY (id),
  INDEX fk_report_synthesis_partnerships_phases_research_report_syn_idx (partnership_id ASC),
  CONSTRAINT fk_report_synthesis_partnerships_phases_research_rep_ind_phas1
    FOREIGN KEY (phase_research_id)
    REFERENCES rep_ind_phase_research_partnerships (id)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT fk_report_synthesis_partnerships_phases_research_report_synth1
    FOREIGN KEY (partnership_id)
    REFERENCES report_synthesis_partnerships (id)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table report_synthesis_partnerships_countries
-- -----------------------------------------------------


CREATE TABLE IF NOT EXISTS report_synthesis_partnerships_countries (
  id BIGINT(20) NOT NULL AUTO_INCREMENT,
  partnership_id BIGINT(20) NOT NULL,
  loc_element_id BIGINT(20) NOT NULL,
  PRIMARY KEY (id),
  INDEX fk_report_synthesis_partnerships_countries_report_synthesis_idx (partnership_id ASC),
  INDEX fk_report_synthesis_partnerships_countries_loc_elements1_idx (loc_element_id ASC),
  CONSTRAINT fk_report_synthesis_partnerships_countries_report_synthesis_p1
    FOREIGN KEY (partnership_id)
    REFERENCES report_synthesis_partnerships (id)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT fk_report_synthesis_partnerships_countries_loc_elements1
    FOREIGN KEY (loc_element_id)
    REFERENCES loc_elements (id)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB;

-- -----------------------------------------------------
-- Table report_synthesis_partnerships_ind
-- -----------------------------------------------------
# drop table report_synthesis_partnerships_ind;

CREATE TABLE IF NOT EXISTS report_synthesis_partnerships_ind (
  id BIGINT(20) NOT NULL AUTO_INCREMENT,
  phase_id BIGINT(20) NOT NULL,
  phase_research_partnerships_id BIGINT(20) NOT NULL,
  quantity INT NOT NULL,
  created_by BIGINT(20) NOT NULL,
  modificated_by BIGINT(20) NULL,
  modification_justification TEXT NULL,
  PRIMARY KEY (id),
  INDEX fk_report_synthesis_partnerships_ind_phases1_idx (phase_id ASC),
  INDEX fk_report_synthesis_partnerships_ind_rep_ind_phase_research_idx (phase_research_partnerships_id ASC),
  INDEX fk_report_synthesis_partnerships_ind_users1_idx (created_by ASC),
  INDEX fk_report_synthesis_partnerships_ind_users2_idx (modificated_by ASC),
  CONSTRAINT fk_report_synthesis_partnerships_ind_phases1
    FOREIGN KEY (phase_id)
    REFERENCES phases (id)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT fk_report_synthesis_partnerships_ind_rep_ind_phase_research_p1
    FOREIGN KEY (phase_research_partnerships_id)
    REFERENCES rep_ind_phase_research_partnerships (id)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT fk_report_synthesis_partnerships_ind_users1
    FOREIGN KEY (created_by)
    REFERENCES users (id)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT fk_report_synthesis_partnerships_ind_users2
    FOREIGN KEY (modificated_by)
    REFERENCES users (id)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB
;

-- -----------------------------------------------------
-- Table rep_ind_participant_user_types
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS rep_ind_participant_user_types (
  id BIGINT(20) NOT NULL AUTO_INCREMENT,
  name TEXT NULL,
  definition TEXT NULL,
  PRIMARY KEY (id))
ENGINE = InnoDB
;

-- -----------------------------------------------------
-- Table rep_ind_training_terms
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS rep_ind_training_terms (
  id BIGINT(20) NOT NULL AUTO_INCREMENT,
  name TEXT NULL,
  definition TEXT NULL,
  PRIMARY KEY (id))
ENGINE = InnoDB
;

-- -----------------------------------------------------
-- Table report_synthesis_participants_ind
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS report_synthesis_participants_ind (
  id BIGINT(20) NOT NULL AUTO_INCREMENT,
  phase_id BIGINT(20) NOT NULL,
  participant_user_types_id BIGINT(20) NULL,
  total_quantity INT NOT NULL,
  woman_quantity INT NULL,
  created_by BIGINT(20) NOT NULL,
  modified_by BIGINT(20)  NULL,
  modification_justification VARCHAR(45) NULL,
  PRIMARY KEY (id),
  INDEX fk_report_synthesis_participants_ind_rep_ind_participant_us_idx (participant_user_types_id ASC),
  INDEX fk_report_synthesis_participants_ind_users1_idx (created_by ASC),
  INDEX fk_report_synthesis_participants_ind_users2_idx (modified_by ASC),
  INDEX fk_report_synthesis_participants_ind_phases1_idx (phase_id ASC),
  CONSTRAINT fk_report_synthesis_participants_ind_rep_ind_participant_user1
    FOREIGN KEY (participant_user_types_id)
    REFERENCES rep_ind_participant_user_types (id)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT fk_report_synthesis_participants_ind_users1
    FOREIGN KEY (created_by)
    REFERENCES users (id)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT fk_report_synthesis_participants_ind_users2
    FOREIGN KEY (modified_by)
    REFERENCES users (id)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT fk_report_synthesis_participants_ind_phases1
    FOREIGN KEY (phase_id)
    REFERENCES phases (id)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB
;

-- -----------------------------------------------------
-- Table report_synthesis_trainees_ind
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS report_synthesis_trainees_ind (
  id BIGINT(20) NOT NULL AUTO_INCREMENT,
  phase_id BIGINT(20) NOT NULL,
  training_term_id BIGINT(20) NOT NULL,
  total_quantity INT NOT NULL,
  woman_quantity INT NULL,
  created_by BIGINT(20) NOT NULL,
  modified_by BIGINT(20) NULL,
  modification_justification TEXT NULL,
  PRIMARY KEY (id),
  INDEX fk_report_synthesis_trainees_participants_ind_users1_idx (created_by ASC),
  INDEX fk_report_synthesis_trainees_participants_ind_users2_idx (modified_by ASC),
  INDEX fk_report_synthesis_trainees_participants_ind_rep_ind_train_idx1 (training_term_id ASC),
  INDEX fk_report_synthesis_trainees_ind_phases1_idx (phase_id ASC),
  CONSTRAINT fk_report_synthesis_trainees_participants_ind_users1
    FOREIGN KEY (created_by)
    REFERENCES users (id)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT fk_report_synthesis_trainees_participants_ind_users2
    FOREIGN KEY (modified_by)
    REFERENCES users (id)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT fk_report_synthesis_trainees_participants_ind_rep_ind_trainin1
    FOREIGN KEY (training_term_id)
    REFERENCES rep_ind_training_terms (id)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT fk_report_synthesis_trainees_ind_phases1
    FOREIGN KEY (phase_id)
    REFERENCES phases (id)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB

/********************************************************************************************************************
**********************                            DML                                          **********************
*********************************************************************************************************************/
;
truncate table report_synthesis_partnerships_ind;
delete from report_synthesis_partnerships;
delete from report_synthesis_partnerships_flagships;
delete from report_synthesis_partnerships_phases_research;
delete from report_synthesis_partnerships_countries;


delete from  report_synthesis_trainees_ind;
delete from  report_synthesis_participants_ind;

delete from rep_ind_training_terms;
delete from rep_ind_participant_user_types;

/********************** report_synthesis_partnerships_ind  ************************************/

insert into report_synthesis_partnerships_ind (phase_id,phase_research_partnerships_id,quantity,created_by) values (17,1,111,1082);
insert into report_synthesis_partnerships_ind (phase_id,phase_research_partnerships_id,quantity,created_by) values (17,2,18,1082);
insert into report_synthesis_partnerships_ind (phase_id,phase_research_partnerships_id,quantity,created_by) values (17,3,69,1082);

insert into report_synthesis_partnerships_ind (phase_id,phase_research_partnerships_id,quantity,created_by) values (186,1,254,1082);
insert into report_synthesis_partnerships_ind (phase_id,phase_research_partnerships_id,quantity,created_by) values (186,2,18,1082);
insert into report_synthesis_partnerships_ind (phase_id,phase_research_partnerships_id,quantity,created_by) values (186,3,91,1082);

insert into report_synthesis_partnerships_ind (phase_id,phase_research_partnerships_id,quantity,created_by) values (18,1,120,1082);
insert into report_synthesis_partnerships_ind (phase_id,phase_research_partnerships_id,quantity,created_by) values (18,2,28,1082);
insert into report_synthesis_partnerships_ind (phase_id,phase_research_partnerships_id,quantity,created_by) values (18,3,103,1082);

insert into report_synthesis_partnerships_ind (phase_id,phase_research_partnerships_id,quantity,created_by) values (256,1,77,1082);
insert into report_synthesis_partnerships_ind (phase_id,phase_research_partnerships_id,quantity,created_by) values (256,2,37,1082);
insert into report_synthesis_partnerships_ind (phase_id,phase_research_partnerships_id,quantity,created_by) values (256,3,104,1082);

insert into report_synthesis_partnerships_ind (phase_id,phase_research_partnerships_id,quantity,created_by) values (12,1,125,1082);
insert into report_synthesis_partnerships_ind (phase_id,phase_research_partnerships_id,quantity,created_by) values (12,2,18,1082);
insert into report_synthesis_partnerships_ind (phase_id,phase_research_partnerships_id,quantity,created_by) values (12,3,94,1082);

insert into report_synthesis_partnerships_ind (phase_id,phase_research_partnerships_id,quantity,created_by) values (266,1,19,1082);
insert into report_synthesis_partnerships_ind (phase_id,phase_research_partnerships_id,quantity,created_by) values (266,2,33,1082);
insert into report_synthesis_partnerships_ind (phase_id,phase_research_partnerships_id,quantity,created_by) values (266,3,10,1082);

insert into report_synthesis_partnerships_ind (phase_id,phase_research_partnerships_id,quantity,created_by) values (15,1,12,1082);
insert into report_synthesis_partnerships_ind (phase_id,phase_research_partnerships_id,quantity,created_by) values (15,2,2,1082);
insert into report_synthesis_partnerships_ind (phase_id,phase_research_partnerships_id,quantity,created_by) values (15,3,2,1082);

insert into report_synthesis_partnerships_ind (phase_id,phase_research_partnerships_id,quantity,created_by) values (13,1,103,1082);
insert into report_synthesis_partnerships_ind (phase_id,phase_research_partnerships_id,quantity,created_by) values (13,2,22,1082);
insert into report_synthesis_partnerships_ind (phase_id,phase_research_partnerships_id,quantity,created_by) values (13,3,38,1082);

insert into report_synthesis_partnerships_ind (phase_id,phase_research_partnerships_id,quantity,created_by) values (14,1,29,1082);
insert into report_synthesis_partnerships_ind (phase_id,phase_research_partnerships_id,quantity,created_by) values (14,2,49,1082);
insert into report_synthesis_partnerships_ind (phase_id,phase_research_partnerships_id,quantity,created_by) values (14,3,31,1082);

insert into report_synthesis_partnerships_ind (phase_id,phase_research_partnerships_id,quantity,created_by) values (11,1,75,1082);
insert into report_synthesis_partnerships_ind (phase_id,phase_research_partnerships_id,quantity,created_by) values (11,2,24,1082);
insert into report_synthesis_partnerships_ind (phase_id,phase_research_partnerships_id,quantity,created_by) values (11,3,96,1082);

insert into report_synthesis_partnerships_ind (phase_id,phase_research_partnerships_id,quantity,created_by) values (16,1,81,1082);
insert into report_synthesis_partnerships_ind (phase_id,phase_research_partnerships_id,quantity,created_by) values (16,2,8,1082);
insert into report_synthesis_partnerships_ind (phase_id,phase_research_partnerships_id,quantity,created_by) values (16,3,22,1082);


/********************** rep_ind_training_terms  ************************************/
insert into rep_ind_training_terms (name,definition) values ('Short term','');
insert into rep_ind_training_terms (name,definition) values ('Long term','');
insert into rep_ind_training_terms (name,definition) values ('Not defined','');

/********************** rep_ind_participant_user_types  ************************************/
insert into rep_ind_participant_user_types (name,definition) values ('End users','');
insert into rep_ind_participant_user_types (name,definition) values ('Next users','');
insert into rep_ind_participant_user_types (name,definition) values ('Not defined','');

/********************** report_synthesis_participants_ind  ************************************/

insert into report_synthesis_participants_ind (phase_id, participant_user_types_id, total_quantity, woman_quantity, created_by) values(17, 1, 18609, 2977, 1082);
insert into report_synthesis_participants_ind (phase_id, participant_user_types_id, total_quantity, woman_quantity, created_by) values(17,2,1158,231,1082);

insert into report_synthesis_participants_ind (phase_id, participant_user_types_id, total_quantity, woman_quantity, created_by) values(186,1,46682,25208.28,1082);
insert into report_synthesis_participants_ind (phase_id, participant_user_types_id, total_quantity, woman_quantity, created_by) values(186,2,11671,6302,1082);

insert into report_synthesis_participants_ind (phase_id, participant_user_types_id, total_quantity, woman_quantity, created_by) values(18,1,44714,10731,1082);
insert into report_synthesis_participants_ind (phase_id, participant_user_types_id, total_quantity, woman_quantity, created_by) values(18,2,4937,3358,1082);

insert into report_synthesis_participants_ind (phase_id, participant_user_types_id, total_quantity, woman_quantity, created_by) values(256,3,67814,null,1082);

insert into report_synthesis_participants_ind (phase_id, participant_user_types_id, total_quantity, woman_quantity, created_by) values(12,1,44335,21280,1082);

insert into report_synthesis_participants_ind (phase_id, participant_user_types_id, total_quantity, woman_quantity, created_by) values(266,1,55385,33785,1082);
insert into report_synthesis_participants_ind (phase_id, participant_user_types_id, total_quantity, woman_quantity, created_by) values(266,2,1537,538,1082);

insert into report_synthesis_participants_ind (phase_id, participant_user_types_id, total_quantity, woman_quantity, created_by) values(15,1,8706,696.48,1082);
insert into report_synthesis_participants_ind (phase_id, participant_user_types_id, total_quantity, woman_quantity, created_by) values(15,2,1539,615.6,1082);

insert into report_synthesis_participants_ind (phase_id, participant_user_types_id, total_quantity, woman_quantity, created_by) values(13,3,1525,458,1082);

insert into report_synthesis_participants_ind (phase_id, participant_user_types_id, total_quantity, woman_quantity, created_by) values(14,3,3318526,null,1082);
insert into report_synthesis_participants_ind (phase_id, participant_user_types_id, total_quantity, woman_quantity, created_by) values(11,3,14217,58289,1082);
insert into report_synthesis_participants_ind (phase_id, participant_user_types_id, total_quantity, woman_quantity, created_by) values(16,3,65826,16456,1082);

/********************** report_synthesis_partnerships  ************************************/

insert into report_synthesis_trainees_ind (phase_id, training_term_id, total_quantity, woman_quantity, created_by) values(17,2,72,28.08,1082);
insert into report_synthesis_trainees_ind (phase_id, training_term_id, total_quantity, woman_quantity, created_by) values(17,1,28464,3843,1082);

insert into report_synthesis_trainees_ind (phase_id, training_term_id, total_quantity, woman_quantity, created_by) values(186,2,541,281.32,1082);
insert into report_synthesis_trainees_ind (phase_id, training_term_id, total_quantity, woman_quantity, created_by) values(186,1,19451,10503.54,1082);

insert into report_synthesis_trainees_ind (phase_id, training_term_id, total_quantity, woman_quantity, created_by) values(18,2,120,49,1082);
insert into report_synthesis_trainees_ind (phase_id, training_term_id, total_quantity, woman_quantity, created_by) values(18,1,46666,11666,1082);

insert into report_synthesis_trainees_ind (phase_id, training_term_id, total_quantity, woman_quantity, created_by) values(256,2,222,95,1082);
insert into report_synthesis_trainees_ind (phase_id, training_term_id, total_quantity, woman_quantity, created_by) values(256,1,53500,null,1082);

insert into report_synthesis_trainees_ind (phase_id, training_term_id, total_quantity, woman_quantity, created_by) values(12,2,9,4,1082);
insert into report_synthesis_trainees_ind (phase_id, training_term_id, total_quantity, woman_quantity, created_by) values(12,1,5740,2296,1082);

insert into report_synthesis_trainees_ind (phase_id, training_term_id, total_quantity, woman_quantity, created_by) values(15,2,63,25,1082);
insert into report_synthesis_trainees_ind (phase_id, training_term_id, total_quantity, woman_quantity, created_by) values(15,1,1471,338,1082);

insert into report_synthesis_trainees_ind (phase_id, training_term_id, total_quantity, woman_quantity, created_by) values(13,2,355,25,1082);
insert into report_synthesis_trainees_ind (phase_id, training_term_id, total_quantity, woman_quantity, created_by) values(13,1,257,89.95,1082);

insert into report_synthesis_trainees_ind (phase_id, training_term_id, total_quantity, woman_quantity, created_by) values(16,2,318,124,1082);
insert into report_synthesis_trainees_ind (phase_id, training_term_id, total_quantity, woman_quantity, created_by) values(16,1,47359,14681.29,1082);

insert into report_synthesis_trainees_ind (phase_id, training_term_id, total_quantity, woman_quantity, created_by) values(256,3,75,31,1082);

insert into report_synthesis_trainees_ind (phase_id, training_term_id, total_quantity, woman_quantity, created_by) values(266,3,74583,31,1082);

insert into report_synthesis_trainees_ind (phase_id, training_term_id, total_quantity, woman_quantity, created_by) values(14,3,121149,31,1082);

insert into report_synthesis_trainees_ind (phase_id, training_term_id, total_quantity, woman_quantity, created_by) values(11,3,2012,31,1082);


/********************** report_synthesis_partnerships  ************************************/
INSERT INTO report_synthesis_partnerships (institution_id, phase_id,created_by,modified_by,active_since,main_area, is_active) 
VALUES (1, 11,1082,null, sysdate(),'The CCAFS PMU was established at WUR and a number of areas of partnership were developed, including work on sustainable intensification of plant production in Sub-Saharan Africa, business models and financial incentives for CSA, biological nitrogen fixation for smallholders, and modelling of CCAFS scenarios.', 1);
INSERT INTO report_synthesis_partnerships (institution_id, phase_id,created_by,modified_by,active_since,main_area, is_active) 
VALUES (17, 11,1082,null, sysdate(),'Development, validation and scaling up of climate resilient farming systems.Capacity development and policy engagement for scaling investments for CSA. Development of post-flood management strategy, including index-based flood insurance.', 1);
INSERT INTO report_synthesis_partnerships (institution_id, phase_id,created_by,modified_by,active_since,main_area, is_active) 
VALUES (69, 11,1082,null, sysdate(),'Research, capacity development and policy engagement. Included joint engagement in UNFCCC processes, scaling out CSA at the regional and global levels, elaborating agriculture components of the NAPs and emissions from the livestock sector.', 1);
INSERT INTO report_synthesis_partnerships (institution_id, phase_id,created_by,modified_by,active_since,main_area, is_active) 
VALUES (76, 11,1082,null, sysdate(),'Joint work on GIS, setting up pilots in provinces, and policy engagement at the national level.', 1);
INSERT INTO report_synthesis_partnerships (institution_id, phase_id,created_by,modified_by,active_since,main_area, is_active) 
VALUES (100, 11,1082,null, sysdate(),'Scientific and partnership backstopping to the development of inclusive agricultural insurance products, including index-based insurance (piloting implementation in Honduras).Strategic and applied research, education, capacity building, and provision of forecasts and information products with an emphasis on practical and verifiable utility, and partnership.Participatory evaluation of CSA in different agro-ecological zones of SA.Installation and backstopping of ENACTS in national hydro-meteorological services (Ghana, Mali, Senegal), and yield forecasting with CRAFT.', 1);
INSERT INTO report_synthesis_partnerships (institution_id, phase_id,created_by,modified_by,active_since,main_area, is_active) 
VALUES (110, 11,1082,null, sysdate(),'Field-level testing of CSA options, and scaling up of efforts including through state funded programs.', 1);
INSERT INTO report_synthesis_partnerships (institution_id, phase_id,created_by,modified_by,active_since,main_area, is_active) 
VALUES (125, 11,1082,null, sysdate(),'Linking CCAFS research to World Bank CSA investments, and exploring opportunities to test innovative finance mechanisms for incentivizing CSA activities.', 1);
INSERT INTO report_synthesis_partnerships (institution_id, phase_id,created_by,modified_by,active_since,main_area, is_active) 
VALUES (126, 11,1082,null, sysdate(),'Formulation, coordination and adoption of the policies, plans, programs and projects for the Agricultural, Fisheries and Rural Development sector, including scaling up of climate actions in agriculture. ', 1);
INSERT INTO report_synthesis_partnerships (institution_id, phase_id,created_by,modified_by,active_since,main_area, is_active) 
VALUES (282, 11,1082,null, sysdate(),'Development of agro-climate zoning maps and training local partners on seasonal forecasting in Dien Bien and Ha Tinh provinces, and sharing national climate data for the last 40 years.', 1);
INSERT INTO report_synthesis_partnerships (institution_id, phase_id,created_by,modified_by,active_since,main_area, is_active) 
VALUES (436, 11,1082,null, sysdate(),'Joint agro-meteorological analyses at project sites, capacitation of national hydro-meteorological services in data management and analysis, ENACTS, PICSA, and (with ICRISAT) the Joint Agro-Meteorological Business Services Incubator (JAMSi).', 1);
INSERT INTO report_synthesis_partnerships (institution_id, phase_id,created_by,modified_by,active_since,main_area, is_active) 
VALUES (455, 11,1082,null, sysdate(),'Field implementation, evaluation and promotion of CSA practices and implementing PICSA. Focus on gender and social inclusion.', 1);
INSERT INTO report_synthesis_partnerships (institution_id, phase_id,created_by,modified_by,active_since,main_area, is_active) 
VALUES (521, 11,1082,null, sysdate(),'Design and implementation of activities that promote the translation of CSA actions into policies, and facilitation of stakeholder engagement in Ghana. ', 1);
INSERT INTO report_synthesis_partnerships (institution_id, phase_id,created_by,modified_by,active_since,main_area, is_active) 
VALUES (546, 11,1082,null, sysdate(),'Development and scaling out of insurance products and engagement with the Ministry of Agriculture, Farmers and Welfare.', 1);
INSERT INTO report_synthesis_partnerships (institution_id, phase_id,created_by,modified_by,active_since,main_area, is_active) 
VALUES (1043, 11,1082,null, sysdate(),'Incorporation of CCAFS climate science into the Rainforest Alliance voluntary certification scheme.', 1);
INSERT INTO report_synthesis_partnerships (institution_id, phase_id,created_by,modified_by,active_since,main_area, is_active) 
VALUES (1046, 11,1082,null, sysdate(),'Integrating CSA approaches and exposure maps into decision-making by the Council on Smallholder Agricultural Finance responsible for approximately USD 750 million in annual lending to producer organizations. ', 1);
INSERT INTO report_synthesis_partnerships (institution_id, phase_id,created_by,modified_by,active_since,main_area, is_active) 
VALUES (1084, 11,1082,null, sysdate(),'Piloting and implementing CSA technologies and practices in the Philippines and Myanmar, and scaling up best practices to the national level.', 1);
INSERT INTO report_synthesis_partnerships (institution_id, phase_id,created_by,modified_by,active_since,main_area, is_active) 
VALUES (1133, 11,1082,null, sysdate(),'Encapsulation of climate information and knowledge content generation inside business driven approaches utilizing the AIRMAP, BRISK and TARGET risk control modules of the agCelerant phygital value chain orchestration platform.', 1);
INSERT INTO report_synthesis_partnerships (institution_id, phase_id,created_by,modified_by,active_since,main_area, is_active) 
VALUES (285, 11,1082,null, sysdate(),'Learning alliance for adaptation in agriculture, and joint efforts focused on climate-nutrition interface, scaling up private sector investments, gender transformative approaches, and South-South cooperation.', 1);
INSERT INTO report_synthesis_partnerships (institution_id, phase_id,created_by,modified_by,active_since,main_area, is_active) 
VALUES (229, 11,1082,null, sysdate(),'Research on gender transformative approaches within IFAD’s Adaptation for Smallholders in Agriculture program, and joint communications and engagement based on results.', 1);
INSERT INTO report_synthesis_partnerships (institution_id, phase_id,created_by,modified_by,active_since,main_area, is_active) 
VALUES (1305, 11,1082,null, sysdate(),'Develop radio campaigns for information dissemination and increased awareness on climate change and CSA in the Philippines.', 1);
INSERT INTO report_synthesis_partnerships (institution_id, phase_id,created_by,modified_by,active_since,main_area, is_active) 
VALUES (1187, 11,1082,null, sysdate(),'Supporting Kenya Dairy Board and State Department of Livestock in developing a dairy NAMA and for submission to the GCF.', 1);
INSERT INTO report_synthesis_partnerships (institution_id, phase_id,created_by,modified_by,active_since,main_area, is_active) 
VALUES (600, 11,1082,null, sysdate(),'Shaping the global initiative, the Food Water Energy Knowledge Action Network.', 1);


/********************** report_synthesis_partnerships_flagships  ************************************/
INSERT INTO report_synthesis_partnerships_flagships (partnership_id,crp_program_id)
values ((select id from report_synthesis_partnerships where institution_id=1 and phase_id =11),87);
INSERT INTO report_synthesis_partnerships_flagships (partnership_id,crp_program_id)
values ((select id from report_synthesis_partnerships where institution_id=1 and phase_id =11),85);
INSERT INTO report_synthesis_partnerships_flagships (partnership_id,crp_program_id)
values ((select id from report_synthesis_partnerships where institution_id=1 and phase_id =11),86);
INSERT INTO report_synthesis_partnerships_flagships (partnership_id,crp_program_id)
values ((select id from report_synthesis_partnerships where institution_id=17 and phase_id =11),85);
INSERT INTO report_synthesis_partnerships_flagships (partnership_id,crp_program_id)
values ((select id from report_synthesis_partnerships where institution_id=17 and phase_id =11),84);
INSERT INTO report_synthesis_partnerships_flagships (partnership_id,crp_program_id)
values ((select id from report_synthesis_partnerships where institution_id=69 and phase_id =11),87);
INSERT INTO report_synthesis_partnerships_flagships (partnership_id,crp_program_id)
values ((select id from report_synthesis_partnerships where institution_id=69 and phase_id =11),86);
INSERT INTO report_synthesis_partnerships_flagships (partnership_id,crp_program_id)
values ((select id from report_synthesis_partnerships where institution_id=76 and phase_id =11),86);
INSERT INTO report_synthesis_partnerships_flagships (partnership_id,crp_program_id)
values ((select id from report_synthesis_partnerships where institution_id=100 and phase_id =11),84);
INSERT INTO report_synthesis_partnerships_flagships (partnership_id,crp_program_id)
values ((select id from report_synthesis_partnerships where institution_id=110 and phase_id =11),87);
INSERT INTO report_synthesis_partnerships_flagships (partnership_id,crp_program_id)
values ((select id from report_synthesis_partnerships where institution_id=110 and phase_id =11),85);
INSERT INTO report_synthesis_partnerships_flagships (partnership_id,crp_program_id)
values ((select id from report_synthesis_partnerships where institution_id=110 and phase_id =11),84);
INSERT INTO report_synthesis_partnerships_flagships (partnership_id,crp_program_id)
values ((select id from report_synthesis_partnerships where institution_id=125 and phase_id =11),85);
INSERT INTO report_synthesis_partnerships_flagships (partnership_id,crp_program_id)
values ((select id from report_synthesis_partnerships where institution_id=126 and phase_id =11),87);
INSERT INTO report_synthesis_partnerships_flagships (partnership_id,crp_program_id)
values ((select id from report_synthesis_partnerships where institution_id=126 and phase_id =11),86);
INSERT INTO report_synthesis_partnerships_flagships (partnership_id,crp_program_id)
values ((select id from report_synthesis_partnerships where institution_id=282 and phase_id =11),84);
INSERT INTO report_synthesis_partnerships_flagships (partnership_id,crp_program_id)
values ((select id from report_synthesis_partnerships where institution_id=436 and phase_id =11),84);
INSERT INTO report_synthesis_partnerships_flagships (partnership_id,crp_program_id)
values ((select id from report_synthesis_partnerships where institution_id=455 and phase_id =11),85);
INSERT INTO report_synthesis_partnerships_flagships (partnership_id,crp_program_id)
values ((select id from report_synthesis_partnerships where institution_id=521 and phase_id =11),87);
INSERT INTO report_synthesis_partnerships_flagships (partnership_id,crp_program_id)
values ((select id from report_synthesis_partnerships where institution_id=521 and phase_id =11),85);
INSERT INTO report_synthesis_partnerships_flagships (partnership_id,crp_program_id)
values ((select id from report_synthesis_partnerships where institution_id=521 and phase_id =11),86);
INSERT INTO report_synthesis_partnerships_flagships (partnership_id,crp_program_id)
values ((select id from report_synthesis_partnerships where institution_id=546 and phase_id =11),84);
INSERT INTO report_synthesis_partnerships_flagships (partnership_id,crp_program_id)
values ((select id from report_synthesis_partnerships where institution_id=1043 and phase_id =11),85);
INSERT INTO report_synthesis_partnerships_flagships (partnership_id,crp_program_id)
values ((select id from report_synthesis_partnerships where institution_id=1046 and phase_id =11),85);
INSERT INTO report_synthesis_partnerships_flagships (partnership_id,crp_program_id)
values ((select id from report_synthesis_partnerships where institution_id=1084 and phase_id =11),87);
INSERT INTO report_synthesis_partnerships_flagships (partnership_id,crp_program_id)
values ((select id from report_synthesis_partnerships where institution_id=1084 and phase_id =11),85);
INSERT INTO report_synthesis_partnerships_flagships (partnership_id,crp_program_id)
values ((select id from report_synthesis_partnerships where institution_id=1084 and phase_id =11),86);
INSERT INTO report_synthesis_partnerships_flagships (partnership_id,crp_program_id)
values ((select id from report_synthesis_partnerships where institution_id=1084 and phase_id =11),84);
INSERT INTO report_synthesis_partnerships_flagships (partnership_id,crp_program_id)
values ((select id from report_synthesis_partnerships where institution_id=1133 and phase_id =11),84);
INSERT INTO report_synthesis_partnerships_flagships (partnership_id,crp_program_id)
values ((select id from report_synthesis_partnerships where institution_id=285 and phase_id =11),87);
INSERT INTO report_synthesis_partnerships_flagships (partnership_id,crp_program_id)
values ((select id from report_synthesis_partnerships where institution_id=285 and phase_id =11),85);
INSERT INTO report_synthesis_partnerships_flagships (partnership_id,crp_program_id)
values ((select id from report_synthesis_partnerships where institution_id=285 and phase_id =11),86);
INSERT INTO report_synthesis_partnerships_flagships (partnership_id,crp_program_id)
values ((select id from report_synthesis_partnerships where institution_id=285 and phase_id =11),84);
INSERT INTO report_synthesis_partnerships_flagships (partnership_id,crp_program_id)
values ((select id from report_synthesis_partnerships where institution_id=229 and phase_id =11),84);
INSERT INTO report_synthesis_partnerships_flagships (partnership_id,crp_program_id)
values ((select id from report_synthesis_partnerships where institution_id=1305 and phase_id =11),87);
INSERT INTO report_synthesis_partnerships_flagships (partnership_id,crp_program_id)
values ((select id from report_synthesis_partnerships where institution_id=1305 and phase_id =11),85);
INSERT INTO report_synthesis_partnerships_flagships (partnership_id,crp_program_id)
values ((select id from report_synthesis_partnerships where institution_id=1305 and phase_id =11),86);
INSERT INTO report_synthesis_partnerships_flagships (partnership_id,crp_program_id)
values ((select id from report_synthesis_partnerships where institution_id=1305 and phase_id =11),84);
INSERT INTO report_synthesis_partnerships_flagships (partnership_id,crp_program_id)
values ((select id from report_synthesis_partnerships where institution_id=1187 and phase_id =11),86);
INSERT INTO report_synthesis_partnerships_flagships (partnership_id,crp_program_id)
values ((select id from report_synthesis_partnerships where institution_id=600 and phase_id =11),87);

/********************** report_synthesis_partnerships_phases_research  ************************************/
INSERT INTO report_synthesis_partnerships_phases_research (partnership_id,phase_research_id)
values ((select id from report_synthesis_partnerships where institution_id=1 and phase_id =11),1);
INSERT INTO report_synthesis_partnerships_phases_research (partnership_id,phase_research_id)
values ((select id from report_synthesis_partnerships where institution_id=1 and phase_id =11),3);
INSERT INTO report_synthesis_partnerships_phases_research (partnership_id,phase_research_id)
values ((select id from report_synthesis_partnerships where institution_id=17 and phase_id =11),1);
INSERT INTO report_synthesis_partnerships_phases_research (partnership_id,phase_research_id)
values ((select id from report_synthesis_partnerships where institution_id=17 and phase_id =11),2);
INSERT INTO report_synthesis_partnerships_phases_research (partnership_id,phase_research_id)
values ((select id from report_synthesis_partnerships where institution_id=17 and phase_id =11),3);
INSERT INTO report_synthesis_partnerships_phases_research (partnership_id,phase_research_id)
values ((select id from report_synthesis_partnerships where institution_id=69 and phase_id =11),3);
INSERT INTO report_synthesis_partnerships_phases_research (partnership_id,phase_research_id)
values ((select id from report_synthesis_partnerships where institution_id=76 and phase_id =11),1);
INSERT INTO report_synthesis_partnerships_phases_research (partnership_id,phase_research_id)
values ((select id from report_synthesis_partnerships where institution_id=76 and phase_id =11),2);
INSERT INTO report_synthesis_partnerships_phases_research (partnership_id,phase_research_id)
values ((select id from report_synthesis_partnerships where institution_id=100 and phase_id =11),1);
INSERT INTO report_synthesis_partnerships_phases_research (partnership_id,phase_research_id)
values ((select id from report_synthesis_partnerships where institution_id=100 and phase_id =11),2);
INSERT INTO report_synthesis_partnerships_phases_research (partnership_id,phase_research_id)
values ((select id from report_synthesis_partnerships where institution_id=100 and phase_id =11),3);
INSERT INTO report_synthesis_partnerships_phases_research (partnership_id,phase_research_id)
values ((select id from report_synthesis_partnerships where institution_id=110 and phase_id =11),1);
INSERT INTO report_synthesis_partnerships_phases_research (partnership_id,phase_research_id)
values ((select id from report_synthesis_partnerships where institution_id=110 and phase_id =11),2);
INSERT INTO report_synthesis_partnerships_phases_research (partnership_id,phase_research_id)
values ((select id from report_synthesis_partnerships where institution_id=125 and phase_id =11),3);
INSERT INTO report_synthesis_partnerships_phases_research (partnership_id,phase_research_id)
values ((select id from report_synthesis_partnerships where institution_id=126 and phase_id =11),3);
INSERT INTO report_synthesis_partnerships_phases_research (partnership_id,phase_research_id)
values ((select id from report_synthesis_partnerships where institution_id=282 and phase_id =11),1);
INSERT INTO report_synthesis_partnerships_phases_research (partnership_id,phase_research_id)
values ((select id from report_synthesis_partnerships where institution_id=436 and phase_id =11),2);
INSERT INTO report_synthesis_partnerships_phases_research (partnership_id,phase_research_id)
values ((select id from report_synthesis_partnerships where institution_id=455 and phase_id =11),1);
INSERT INTO report_synthesis_partnerships_phases_research (partnership_id,phase_research_id)
values ((select id from report_synthesis_partnerships where institution_id=455 and phase_id =11),2);
INSERT INTO report_synthesis_partnerships_phases_research (partnership_id,phase_research_id)
values ((select id from report_synthesis_partnerships where institution_id=521 and phase_id =11),3);
INSERT INTO report_synthesis_partnerships_phases_research (partnership_id,phase_research_id)
values ((select id from report_synthesis_partnerships where institution_id=546 and phase_id =11),2);
INSERT INTO report_synthesis_partnerships_phases_research (partnership_id,phase_research_id)
values ((select id from report_synthesis_partnerships where institution_id=1043 and phase_id =11),3);
INSERT INTO report_synthesis_partnerships_phases_research (partnership_id,phase_research_id)
values ((select id from report_synthesis_partnerships where institution_id=1046 and phase_id =11),2);
INSERT INTO report_synthesis_partnerships_phases_research (partnership_id,phase_research_id)
values ((select id from report_synthesis_partnerships where institution_id=1046 and phase_id =11),3);
INSERT INTO report_synthesis_partnerships_phases_research (partnership_id,phase_research_id)
values ((select id from report_synthesis_partnerships where institution_id=1084 and phase_id =11),1);
INSERT INTO report_synthesis_partnerships_phases_research (partnership_id,phase_research_id)
values ((select id from report_synthesis_partnerships where institution_id=1084 and phase_id =11),3);
INSERT INTO report_synthesis_partnerships_phases_research (partnership_id,phase_research_id)
values ((select id from report_synthesis_partnerships where institution_id=1133 and phase_id =11),2);
INSERT INTO report_synthesis_partnerships_phases_research (partnership_id,phase_research_id)
values ((select id from report_synthesis_partnerships where institution_id=1133 and phase_id =11),3);
INSERT INTO report_synthesis_partnerships_phases_research (partnership_id,phase_research_id)
values ((select id from report_synthesis_partnerships where institution_id=285 and phase_id =11),3);
INSERT INTO report_synthesis_partnerships_phases_research (partnership_id,phase_research_id)
values ((select id from report_synthesis_partnerships where institution_id=229 and phase_id =11),1);
INSERT INTO report_synthesis_partnerships_phases_research (partnership_id,phase_research_id)
values ((select id from report_synthesis_partnerships where institution_id=229 and phase_id =11),3);
INSERT INTO report_synthesis_partnerships_phases_research (partnership_id,phase_research_id)
values ((select id from report_synthesis_partnerships where institution_id=1305 and phase_id =11),2);
INSERT INTO report_synthesis_partnerships_phases_research (partnership_id,phase_research_id)
values ((select id from report_synthesis_partnerships where institution_id=1305 and phase_id =11),3);
INSERT INTO report_synthesis_partnerships_phases_research (partnership_id,phase_research_id)
values ((select id from report_synthesis_partnerships where institution_id=1187 and phase_id =11),2);
INSERT INTO report_synthesis_partnerships_phases_research (partnership_id,phase_research_id)
values ((select id from report_synthesis_partnerships where institution_id=1187 and phase_id =11),3);
INSERT INTO report_synthesis_partnerships_phases_research (partnership_id,phase_research_id)
values ((select id from report_synthesis_partnerships where institution_id=600 and phase_id =11),2);

/**** Exclude the null OICS of supplementary */
insert into report_synthesis_crp_progress_studies (report_synhtesis_crp_progress_id, project_expected_studies_id, is_active, created_by, active_since) 
values (3, 2013, 1,1082, sysdate());
insert into report_synthesis_crp_progress_studies (report_synhtesis_crp_progress_id, project_expected_studies_id, is_active, created_by, active_since) 
values (3, 2014, 1, 1082, sysdate());
insert into report_synthesis_crp_progress_studies (report_synhtesis_crp_progress_id, project_expected_studies_id, is_active, created_by, active_since) 
values (3, 2017, 1,1082,  sysdate());
insert into report_synthesis_crp_progress_studies (report_synhtesis_crp_progress_id, project_expected_studies_id, is_active, created_by, active_since) 
values (3, 2018, 1, 1082,sysdate());
insert into report_synthesis_crp_progress_studies (report_synhtesis_crp_progress_id, project_expected_studies_id, is_active, created_by, active_since) 
values (4, 2018, 1, 1082,sysdate());

insert into report_synthesis_crp_progress_studies (report_synhtesis_crp_progress_id, project_expected_studies_id, is_active, created_by, active_since) 
values (3, 2037, 1, 1082,sysdate());

