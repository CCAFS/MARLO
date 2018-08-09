SET FOREIGN_KEY_CHECKS=0;

-- ----------------------------
-- Change Table structure for upkeep phases 
-- ----------------------------
ALTER TABLE `phases`
ADD COLUMN `name`  text NOT NULL AFTER `id`,
ADD COLUMN `upkeep`  tinyint(1) NOT NULL AFTER `year`;

-- ----------------------------
-- Update phases
-- ----------------------------
SET @reporting_phase = 'Reporting';
SET @planning_phase = 'Planning';
SET @powb_name='POWB';
SET @ar_name='AR';
SET @upkeep_name='UpKeep';
SET @year_2022='2022';


UPDATE `phases` SET `name`=@powb_name WHERE `description`=@planning_phase AND year<=2017;
UPDATE `phases` SET `name`=@ar_name WHERE `description`=@reporting_phase AND year<=2017;
UPDATE `phases` SET `start_date`='2018-04-01', `end_date`='2018-06-15' WHERE (`description`=@reporting_phase and `year`=2017);
UPDATE `phases` SET `name`=@powb_name,`start_date`='2018-01-01', `end_date`='2018-04-01' WHERE (`description`=@planning_phase and `year`=2018);
UPDATE `phases` SET `name`=@upkeep_name,`upkeep`='1',`description`=@planning_phase,`start_date`='2018-07-01', `end_date`='2018-09-01' WHERE (`description`=@reporting_phase and `year`=2018);
UPDATE `phases` SET `name`=@ar_name,`description`=@reporting_phase, `year`='2018',`start_date`='2019-01-01', `end_date`='2019-04-01' WHERE (`description`=@planning_phase and `year`=2019);
UPDATE `phases` SET `name`=@powb_name,`description`=@planning_phase, `year`='2019',`start_date`='2018-09-01', `end_date`='2018-12-31' WHERE (`description`=@reporting_phase and `year`=2019);
UPDATE `phases` SET `name`=@upkeep_name,`upkeep`='1',`year`='2019',`start_date`='2019-01-01', `end_date`='2019-09-01' WHERE (`description`=@planning_phase and `year`=2020);
UPDATE `phases` SET `name`=@ar_name,`year`='2019',`start_date`='2020-01-01', `end_date`='2020-04-01' WHERE (`description`=@reporting_phase and `year`=2020);
UPDATE `phases` SET `name`=@powb_name,`year`='2020',`start_date`='2019-09-01', `end_date`='2019-12-31' WHERE (`description`=@planning_phase and `year`=2021);
UPDATE `phases` SET `name`=@upkeep_name,`description`=@planning_phase,`upkeep`='1',`year`='2020',`start_date`='2020-01-01', `end_date`='2020-09-01' WHERE (`description`=@reporting_phase and `year`=2021);
UPDATE `phases` SET `name`=@ar_name,`description`=@reporting_phase,`year`='2020',`start_date`='2021-01-01', `end_date`='2021-04-01' WHERE (`description`=@planning_phase and `year`=2022);
UPDATE `phases` SET `name`=@powb_name,`description`=@planning_phase,`year`='2021',`start_date`='2020-09-01', `end_date`='2020-12-31' WHERE (`description`=@reporting_phase and `year`=2022);

-- ----------------------------
-- Insert/Update remaining phases
-- ----------------------------

/**
 * CCAFS
 **/

SET @global_unit_ccafs='1';
SET @ccafs_phase_id = (SELECT max(id)+1 FROM phases p);
SET @ccafs_phase_2022=(SELECT  p.id FROM  phases p WHERE p.global_unit_id = @global_unit_ccafs AND p.description = @planning_phase AND p.`year` = 2021);

INSERT INTO `phases` (`id`, `name`, `description`, `year`, `upkeep`, `start_date`, `end_date`, `editable`, `visible`, `next_phase`, `global_unit_id`) 
VALUES (@ccafs_phase_id+4, @ar_name, @reporting_phase, '2022', '0','2023-01-01', '2023-04-01', '0', '0', NULL, @global_unit_ccafs);
INSERT INTO `phases` (`id`, `name`, `description`, `year`, `upkeep`, `start_date`, `end_date`, `editable`, `visible`, `next_phase`, `global_unit_id`) 
VALUES (@ccafs_phase_id+3, @upkeep_name, @planning_phase, '2022', '1','2022-01-01', '2022-09-01', '0', '0', @ccafs_phase_id+4, @global_unit_ccafs);
INSERT INTO `phases` (`id`, `name`, `description`, `year`, `upkeep`, `start_date`, `end_date`, `editable`, `visible`, `next_phase`, `global_unit_id`) 
VALUES (@ccafs_phase_id+2, @powb_name, @planning_phase, '2022', '0','2021-09-01', '2021-12-31', '0', '0', @ccafs_phase_id+3, @global_unit_ccafs);
INSERT INTO `phases` (`id`, `name`, `description`, `year`, `upkeep`, `start_date`, `end_date`, `editable`, `visible`, `next_phase`, `global_unit_id`) 
VALUES (@ccafs_phase_id+1, @ar_name, @reporting_phase, '2021', '0','2022-01-01', '2022-04-01', '0', '0', @ccafs_phase_id+2, @global_unit_ccafs);
INSERT INTO `phases` (`id`, `name`, `description`, `year`, `upkeep`, `start_date`, `end_date`, `editable`, `visible`, `next_phase`, `global_unit_id`) 
VALUES (@ccafs_phase_id, @upkeep_name, @planning_phase, '2021', '1','2021-01-01', '2021-09-01', '0', '0', @ccafs_phase_id+1, @global_unit_ccafs);
UPDATE `phases` SET `next_phase`=@ccafs_phase_id WHERE (`id`= @ccafs_phase_2022);

/**
 * PIM
 **/

SET @global_unit_pim='3';
SET @pim_phase_id = @ccafs_phase_id+5;
SET @pim_phase_2022=(SELECT p.id FROM  phases p WHERE p.global_unit_id = @global_unit_pim AND p.description = @planning_phase AND p.`year` = 2021);

INSERT INTO `phases` (`id`, `name`, `description`, `year`, `upkeep`, `start_date`, `end_date`, `editable`, `visible`, `next_phase`, `global_unit_id`) 
VALUES (@pim_phase_id+4, @ar_name, @reporting_phase, '2022', '0','2023-01-01', '2023-04-01', '0', '0', NULL, @global_unit_pim);
INSERT INTO `phases` (`id`, `name`, `description`, `year`, `upkeep`, `start_date`, `end_date`, `editable`, `visible`, `next_phase`, `global_unit_id`) 
VALUES (@pim_phase_id+3, @upkeep_name, @planning_phase, '2022', '1','2022-01-01', '2022-09-01', '0', '0', @pim_phase_id+4, @global_unit_pim);
INSERT INTO `phases` (`id`, `name`, `description`, `year`, `upkeep`, `start_date`, `end_date`, `editable`, `visible`, `next_phase`, `global_unit_id`) 
VALUES (@pim_phase_id+2, @powb_name, @planning_phase, '2022', '0','2021-09-01', '2021-12-31', '0', '0', @pim_phase_id+3, @global_unit_pim);
INSERT INTO `phases` (`id`, `name`, `description`, `year`, `upkeep`, `start_date`, `end_date`, `editable`, `visible`, `next_phase`, `global_unit_id`) 
VALUES (@pim_phase_id+1, @ar_name, @reporting_phase, '2021', '0','2022-01-01', '2022-04-01', '0', '0', @pim_phase_id+2, @global_unit_pim);
INSERT INTO `phases` (`id`, `name`, `description`, `year`, `upkeep`, `start_date`, `end_date`, `editable`, `visible`, `next_phase`, `global_unit_id`) 
VALUES (@pim_phase_id, @upkeep_name, @planning_phase, '2021', '1','2021-01-01', '2021-09-01', '0', '0', @pim_phase_id+1, @global_unit_pim);
UPDATE `phases` SET `next_phase`=@pim_phase_id WHERE (`id`= @pim_phase_2022);


/**
 * WLE
 **/

SET @global_unit_wle='4';
SET @wle_phase_id = @pim_phase_id+5;
SET @wle_phase_2022=(SELECT p.id FROM phases p WHERE p.global_unit_id = @global_unit_wle AND p.description = @planning_phase AND p.`year` = 2021);

INSERT INTO `phases` (`id`, `name`, `description`, `year`, `upkeep`, `start_date`, `end_date`, `editable`, `visible`, `next_phase`, `global_unit_id`) 
VALUES (@wle_phase_id+4, @ar_name, @reporting_phase, '2022', '0','2023-01-01', '2023-04-01', '0', '0', NULL, @global_unit_wle);
INSERT INTO `phases` (`id`, `name`, `description`, `year`, `upkeep`, `start_date`, `end_date`, `editable`, `visible`, `next_phase`, `global_unit_id`) 
VALUES (@wle_phase_id+3, @upkeep_name, @planning_phase, '2022', '1','2022-01-01', '2022-09-01', '0', '0', @wle_phase_id+4, @global_unit_wle);
INSERT INTO `phases` (`id`, `name`, `description`, `year`, `upkeep`, `start_date`, `end_date`, `editable`, `visible`, `next_phase`, `global_unit_id`) 
VALUES (@wle_phase_id+2, @powb_name, @planning_phase, '2022', '0','2021-09-01', '2021-12-31', '0', '0', @wle_phase_id+3, @global_unit_wle);
INSERT INTO `phases` (`id`, `name`, `description`, `year`, `upkeep`, `start_date`, `end_date`, `editable`, `visible`, `next_phase`, `global_unit_id`) 
VALUES (@wle_phase_id+1, @ar_name, @reporting_phase, '2021', '0','2022-01-01', '2022-04-01', '0', '0', @wle_phase_id+2, @global_unit_wle);
INSERT INTO `phases` (`id`, `name`, `description`, `year`, `upkeep`, `start_date`, `end_date`, `editable`, `visible`, `next_phase`, `global_unit_id`) 
VALUES (@wle_phase_id, @upkeep_name, @planning_phase, '2021', '1','2021-01-01', '2021-09-01', '0', '0', @wle_phase_id+1, @global_unit_wle);
UPDATE `phases` SET `next_phase`=@wle_phase_id WHERE (`id`=@wle_phase_2022);

/**
 * A4NH
 **/

SET @global_unit_a4nh='5';
SET @a4nh_phase_id = @wle_phase_id+5;
SET @a4nh_phase_2022=(SELECT p.id FROM phases p WHERE p.global_unit_id = @global_unit_a4nh AND p.description = @planning_phase AND p.`year` = 2021);

INSERT INTO `phases` (`id`, `name`, `description`, `year`, `upkeep`, `start_date`, `end_date`, `editable`, `visible`, `next_phase`, `global_unit_id`) 
VALUES (@a4nh_phase_id+4, @ar_name, @reporting_phase, '2022', '0','2023-01-01', '2023-04-01', '0', '0', NULL, @global_unit_a4nh);
INSERT INTO `phases` (`id`, `name`, `description`, `year`, `upkeep`, `start_date`, `end_date`, `editable`, `visible`, `next_phase`, `global_unit_id`) 
VALUES (@a4nh_phase_id+3, @upkeep_name, @planning_phase, '2022', '1','2022-01-01', '2022-09-01', '0', '0', @a4nh_phase_id+4, @global_unit_a4nh);
INSERT INTO `phases` (`id`, `name`, `description`, `year`, `upkeep`, `start_date`, `end_date`, `editable`, `visible`, `next_phase`, `global_unit_id`) 
VALUES (@a4nh_phase_id+2, @powb_name, @planning_phase, '2022', '0','2021-09-01', '2021-12-31', '0', '0', @a4nh_phase_id+3, @global_unit_a4nh);
INSERT INTO `phases` (`id`, `name`, `description`, `year`, `upkeep`, `start_date`, `end_date`, `editable`, `visible`, `next_phase`, `global_unit_id`) 
VALUES (@a4nh_phase_id+1, @ar_name, @reporting_phase, '2021', '0','2022-01-01', '2022-04-01', '0', '0', @a4nh_phase_id+2, @global_unit_a4nh);
INSERT INTO `phases` (`id`, `name`, `description`, `year`, `upkeep`, `start_date`, `end_date`, `editable`, `visible`, `next_phase`, `global_unit_id`) 
VALUES (@a4nh_phase_id, @upkeep_name, @planning_phase, '2021', '1','2021-01-01', '2021-09-01', '0', '0', @a4nh_phase_id+1, @global_unit_a4nh);
UPDATE `phases` SET `next_phase`=@a4nh_phase_id WHERE (`id`=@a4nh_phase_2022);

/**
 * Livestock
 **/

SET @global_unit_livestock='7';
SET @livestock_phase_id = @a4nh_phase_id+5;
SET @livestock_phase_2022=(SELECT p.id FROM phases p WHERE p.global_unit_id = @global_unit_livestock AND p.description = @planning_phase AND p.`year` = 2021);

INSERT INTO `phases` (`id`, `name`, `description`, `year`, `upkeep`, `start_date`, `end_date`, `editable`, `visible`, `next_phase`, `global_unit_id`) 
VALUES (@livestock_phase_id+4, @ar_name, @reporting_phase, '2022', '0','2023-01-01', '2023-04-01', '0', '0', NULL, @global_unit_livestock);
INSERT INTO `phases` (`id`, `name`, `description`, `year`, `upkeep`, `start_date`, `end_date`, `editable`, `visible`, `next_phase`, `global_unit_id`) 
VALUES (@livestock_phase_id+3, @upkeep_name, @planning_phase, '2022', '1','2022-01-01', '2022-09-01', '0', '0', @livestock_phase_id+4, @global_unit_livestock);
INSERT INTO `phases` (`id`, `name`, `description`, `year`, `upkeep`, `start_date`, `end_date`, `editable`, `visible`, `next_phase`, `global_unit_id`) 
VALUES (@livestock_phase_id+2, @powb_name, @planning_phase, '2022', '0','2021-09-01', '2021-12-31', '0', '0', @livestock_phase_id+3, @global_unit_livestock);
INSERT INTO `phases` (`id`, `name`, `description`, `year`, `upkeep`, `start_date`, `end_date`, `editable`, `visible`, `next_phase`, `global_unit_id`) 
VALUES (@livestock_phase_id+1, @ar_name, @reporting_phase, '2021', '0','2022-01-01', '2022-04-01', '0', '0', @livestock_phase_id+2, @global_unit_livestock);
INSERT INTO `phases` (`id`, `name`, `description`, `year`, `upkeep`, `start_date`, `end_date`, `editable`, `visible`, `next_phase`, `global_unit_id`) 
VALUES (@livestock_phase_id, @upkeep_name, @planning_phase, '2021', '1','2021-01-01', '2021-09-01', '0', '0', @livestock_phase_id+1, @global_unit_livestock);
UPDATE `phases` SET `next_phase`=@livestock_phase_id WHERE (`id`=@livestock_phase_2022);

/**
 * FTA
 **/

SET @global_unit_fta='11';
SET @fta_phase_id = @livestock_phase_id+5;
SET @fta_phase_2022=(SELECT p.id FROM phases p WHERE p.global_unit_id = @global_unit_fta AND p.description = @planning_phase AND p.`year` = 2021);

INSERT INTO `phases` (`id`, `name`, `description`, `year`, `upkeep`, `start_date`, `end_date`, `editable`, `visible`, `next_phase`, `global_unit_id`) 
VALUES (@fta_phase_id+4, @ar_name, @reporting_phase, '2022', '0','2023-01-01', '2023-04-01', '0', '0', NULL, @global_unit_fta);
INSERT INTO `phases` (`id`, `name`, `description`, `year`, `upkeep`, `start_date`, `end_date`, `editable`, `visible`, `next_phase`, `global_unit_id`) 
VALUES (@fta_phase_id+3, @upkeep_name, @planning_phase, '2022', '1','2022-01-01', '2022-09-01', '0', '0', @fta_phase_id+4, @global_unit_fta);
INSERT INTO `phases` (`id`, `name`, `description`, `year`, `upkeep`, `start_date`, `end_date`, `editable`, `visible`, `next_phase`, `global_unit_id`) 
VALUES (@fta_phase_id+2, @powb_name, @planning_phase, '2022', '0','2021-09-01', '2021-12-31', '0', '0', @fta_phase_id+3, @global_unit_fta);
INSERT INTO `phases` (`id`, `name`, `description`, `year`, `upkeep`, `start_date`, `end_date`, `editable`, `visible`, `next_phase`, `global_unit_id`) 
VALUES (@fta_phase_id+1, @ar_name, @reporting_phase, '2021', '0','2022-01-01', '2022-04-01', '0', '0', @fta_phase_id+2, @global_unit_fta);
INSERT INTO `phases` (`id`, `name`, `description`, `year`, `upkeep`, `start_date`, `end_date`, `editable`, `visible`, `next_phase`, `global_unit_id`) 
VALUES (@fta_phase_id, @upkeep_name, @planning_phase, '2021', '1','2021-01-01', '2021-09-01', '0', '0', @fta_phase_id+1, @global_unit_fta);
UPDATE `phases` SET `next_phase`=@fta_phase_id WHERE (`id`=@fta_phase_2022);

/**
 * Wheat
 **/

SET @global_unit_wheat='21';
SET @wheat_phase_id = @fta_phase_id+5;
SET @wheat_phase_2022=(SELECT p.id FROM phases p WHERE p.global_unit_id = @global_unit_wheat AND p.description = @planning_phase AND p.`year` = 2021);

INSERT INTO `phases` (`id`, `name`, `description`, `year`, `upkeep`, `start_date`, `end_date`, `editable`, `visible`, `next_phase`, `global_unit_id`) 
VALUES (@wheat_phase_id+4, @ar_name, @reporting_phase, '2022', '0','2023-01-01', '2023-04-01', '0', '0', NULL, @global_unit_wheat);
INSERT INTO `phases` (`id`, `name`, `description`, `year`, `upkeep`, `start_date`, `end_date`, `editable`, `visible`, `next_phase`, `global_unit_id`) 
VALUES (@wheat_phase_id+3, @upkeep_name, @planning_phase, '2022', '1','2022-01-01', '2022-09-01', '0', '0', @wheat_phase_id+4, @global_unit_wheat);
INSERT INTO `phases` (`id`, `name`, `description`, `year`, `upkeep`, `start_date`, `end_date`, `editable`, `visible`, `next_phase`, `global_unit_id`) 
VALUES (@wheat_phase_id+2, @powb_name, @planning_phase, '2022', '0','2021-09-01', '2021-12-31', '0', '0', @wheat_phase_id+3, @global_unit_wheat);
INSERT INTO `phases` (`id`, `name`, `description`, `year`, `upkeep`, `start_date`, `end_date`, `editable`, `visible`, `next_phase`, `global_unit_id`) 
VALUES (@wheat_phase_id+1, @ar_name, @reporting_phase, '2021', '0','2022-01-01', '2022-04-01', '0', '0', @wheat_phase_id+2, @global_unit_wheat);
INSERT INTO `phases` (`id`, `name`, `description`, `year`, `upkeep`, `start_date`, `end_date`, `editable`, `visible`, `next_phase`, `global_unit_id`) 
VALUES (@wheat_phase_id, @upkeep_name, @planning_phase, '2021', '1','2021-01-01', '2021-09-01', '0', '0', @wheat_phase_id+1, @global_unit_wheat);
UPDATE `phases` SET `next_phase`=@wheat_phase_id WHERE (`id`=@wheat_phase_2022);

/**
 * Maize
 **/

SET @global_unit_maize='22';
SET @maize_phase_id = @wheat_phase_id+5;
SET @maize_phase_2022=(SELECT p.id FROM phases p WHERE p.global_unit_id = @global_unit_maize AND p.description = @planning_phase AND p.`year` = 2021);

INSERT INTO `phases` (`id`, `name`, `description`, `year`, `upkeep`, `start_date`, `end_date`, `editable`, `visible`, `next_phase`, `global_unit_id`) 
VALUES (@maize_phase_id+4, @ar_name, @reporting_phase, '2022', '0','2023-01-01', '2023-04-01', '0', '0', NULL, @global_unit_maize);
INSERT INTO `phases` (`id`, `name`, `description`, `year`, `upkeep`, `start_date`, `end_date`, `editable`, `visible`, `next_phase`, `global_unit_id`) 
VALUES (@maize_phase_id+3, @upkeep_name, @planning_phase, '2022', '1','2022-01-01', '2022-09-01', '0', '0', @maize_phase_id+4, @global_unit_maize);
INSERT INTO `phases` (`id`, `name`, `description`, `year`, `upkeep`, `start_date`, `end_date`, `editable`, `visible`, `next_phase`, `global_unit_id`) 
VALUES (@maize_phase_id+2, @powb_name, @planning_phase, '2022', '0','2021-09-01', '2021-12-31', '0', '0', @maize_phase_id+3, @global_unit_maize);
INSERT INTO `phases` (`id`, `name`, `description`, `year`, `upkeep`, `start_date`, `end_date`, `editable`, `visible`, `next_phase`, `global_unit_id`) 
VALUES (@maize_phase_id+1, @ar_name, @reporting_phase, '2021', '0','2022-01-01', '2022-04-01', '0', '0', @maize_phase_id+2, @global_unit_maize);
INSERT INTO `phases` (`id`, `name`, `description`, `year`, `upkeep`, `start_date`, `end_date`, `editable`, `visible`, `next_phase`, `global_unit_id`) 
VALUES (@maize_phase_id, @upkeep_name, @planning_phase, '2021', '1','2021-01-01', '2021-09-01', '0', '0', @maize_phase_id+1, @global_unit_maize);
UPDATE `phases` SET `next_phase`=@maize_phase_id WHERE (`id`=@maize_phase_2022);


/**
 * BigData
 **/

SET @global_unit_bigdata='24';
SET @bigdata_phase_id = @maize_phase_id+5;
SET @bigdata_phase_2022=(SELECT p.id FROM phases p WHERE p.global_unit_id = @global_unit_bigdata AND p.description = @planning_phase AND p.`year` = 2021);

INSERT INTO `phases` (`id`, `name`, `description`, `year`, `upkeep`, `start_date`, `end_date`, `editable`, `visible`, `next_phase`, `global_unit_id`) 
VALUES (@bigdata_phase_id+4, @ar_name, @reporting_phase, '2022', '0','2023-01-01', '2023-04-01', '0', '0', NULL, @global_unit_bigdata);
INSERT INTO `phases` (`id`, `name`, `description`, `year`, `upkeep`, `start_date`, `end_date`, `editable`, `visible`, `next_phase`, `global_unit_id`) 
VALUES (@bigdata_phase_id+3, @upkeep_name, @planning_phase, '2022', '1','2022-01-01', '2022-09-01', '0', '0', @bigdata_phase_id+4, @global_unit_bigdata);
INSERT INTO `phases` (`id`, `name`, `description`, `year`, `upkeep`, `start_date`, `end_date`, `editable`, `visible`, `next_phase`, `global_unit_id`) 
VALUES (@bigdata_phase_id+2, @powb_name, @planning_phase, '2022', '0','2021-09-01', '2021-12-31', '0', '0', @bigdata_phase_id+3, @global_unit_bigdata);
INSERT INTO `phases` (`id`, `name`, `description`, `year`, `upkeep`, `start_date`, `end_date`, `editable`, `visible`, `next_phase`, `global_unit_id`) 
VALUES (@bigdata_phase_id+1, @ar_name, @reporting_phase, '2021', '0','2022-01-01', '2022-04-01', '0', '0', @bigdata_phase_id+2, @global_unit_bigdata);
INSERT INTO `phases` (`id`, `name`, `description`, `year`, `upkeep`, `start_date`, `end_date`, `editable`, `visible`, `next_phase`, `global_unit_id`) 
VALUES (@bigdata_phase_id, @upkeep_name, @planning_phase, '2021', '1','2021-01-01', '2021-09-01', '0', '0', @bigdata_phase_id+1, @global_unit_bigdata);
UPDATE `phases` SET `next_phase`=@bigdata_phase_id WHERE (`id`=@bigdata_phase_2022);

/**
 * EiB
 **/

SET @global_unit_eib='25';
SET @eib_phase_id = @bigdata_phase_id+5;
SET @eib_phase_2022=(SELECT p.id FROM phases p WHERE p.global_unit_id = @global_unit_eib AND p.description = @planning_phase AND p.`year` = 2021);

INSERT INTO `phases` (`id`, `name`, `description`, `year`, `upkeep`, `start_date`, `end_date`, `editable`, `visible`, `next_phase`, `global_unit_id`) 
VALUES (@eib_phase_id+4, @ar_name, @reporting_phase, '2022', '0','2023-01-01', '2023-04-01', '0', '0', NULL, @global_unit_eib);
INSERT INTO `phases` (`id`, `name`, `description`, `year`, `upkeep`, `start_date`, `end_date`, `editable`, `visible`, `next_phase`, `global_unit_id`) 
VALUES (@eib_phase_id+3, @upkeep_name, @planning_phase, '2022', '1','2022-01-01', '2022-09-01', '0', '0', @eib_phase_id+4, @global_unit_eib);
INSERT INTO `phases` (`id`, `name`, `description`, `year`, `upkeep`, `start_date`, `end_date`, `editable`, `visible`, `next_phase`, `global_unit_id`) 
VALUES (@eib_phase_id+2, @powb_name, @planning_phase, '2022', '0','2021-09-01', '2021-12-31', '0', '0', @eib_phase_id+3, @global_unit_eib);
INSERT INTO `phases` (`id`, `name`, `description`, `year`, `upkeep`, `start_date`, `end_date`, `editable`, `visible`, `next_phase`, `global_unit_id`) 
VALUES (@eib_phase_id+1, @ar_name, @reporting_phase, '2021', '0','2022-01-01', '2022-04-01', '0', '0', @eib_phase_id+2, @global_unit_eib);
INSERT INTO `phases` (`id`, `name`, `description`, `year`, `upkeep`, `start_date`, `end_date`, `editable`, `visible`, `next_phase`, `global_unit_id`) 
VALUES (@eib_phase_id, @upkeep_name, @planning_phase, '2021', '1','2021-01-01', '2021-09-01', '0', '0', @eib_phase_id+1, @global_unit_eib);
UPDATE `phases` SET `next_phase`=@eib_phase_id WHERE (`id`=@eib_phase_2022);


/**
 * CIAT
 **/

SET @global_unit_ciat='29';
SET @ciat_phase_id = @eib_phase_id+5;
SET @ciat_phase_2022=(SELECT p.id FROM phases p WHERE p.global_unit_id = @global_unit_ciat AND p.description = @planning_phase AND p.`year` = 2021);

INSERT INTO `phases` (`id`, `name`, `description`, `year`, `upkeep`, `start_date`, `end_date`, `editable`, `visible`, `next_phase`, `global_unit_id`) 
VALUES (@ciat_phase_id+4, @ar_name, @reporting_phase, '2022', '0','2023-01-01', '2023-04-01', '0', '0', NULL, @global_unit_ciat);
INSERT INTO `phases` (`id`, `name`, `description`, `year`, `upkeep`, `start_date`, `end_date`, `editable`, `visible`, `next_phase`, `global_unit_id`) 
VALUES (@ciat_phase_id+3, @upkeep_name, @planning_phase, '2022', '1','2022-01-01', '2022-09-01', '0', '0', @ciat_phase_id+4, @global_unit_ciat);
INSERT INTO `phases` (`id`, `name`, `description`, `year`, `upkeep`, `start_date`, `end_date`, `editable`, `visible`, `next_phase`, `global_unit_id`) 
VALUES (@ciat_phase_id+2, @powb_name, @planning_phase, '2022', '0','2021-09-01', '2021-12-31', '0', '0', @ciat_phase_id+3, @global_unit_ciat);
INSERT INTO `phases` (`id`, `name`, `description`, `year`, `upkeep`, `start_date`, `end_date`, `editable`, `visible`, `next_phase`, `global_unit_id`) 
VALUES (@ciat_phase_id+1, @ar_name, @reporting_phase, '2021', '0','2022-01-01', '2022-04-01', '0', '0', @ciat_phase_id+2, @global_unit_ciat);
INSERT INTO `phases` (`id`, `name`, `description`, `year`, `upkeep`, `start_date`, `end_date`, `editable`, `visible`, `next_phase`, `global_unit_id`) 
VALUES (@ciat_phase_id, @upkeep_name, @planning_phase, '2021', '1','2021-01-01', '2021-09-01', '0', '0', @ciat_phase_id+1, @global_unit_ciat);
UPDATE `phases` SET `next_phase`=@ciat_phase_id WHERE (`id`=@ciat_phase_2022);


/**
 * Rice
 **/

SET @global_unit_rice='16';
SET @rice_phase_id = @ciat_phase_id+5;
SET @rice_phase_2022=(SELECT p.id FROM phases p WHERE p.global_unit_id = @global_unit_rice AND p.description = @planning_phase AND p.`year` = 2021);


INSERT INTO `phases` (`id`, `name`, `description`, `year`, `upkeep`, `start_date`, `end_date`, `editable`, `visible`, `next_phase`, `global_unit_id`) 
VALUES (@rice_phase_id+4, @ar_name, @reporting_phase, '2022', '0','2023-01-01', '2023-04-01', '0', '0', NULL, @global_unit_rice);
INSERT INTO `phases` (`id`, `name`, `description`, `year`, `upkeep`, `start_date`, `end_date`, `editable`, `visible`, `next_phase`, `global_unit_id`) 
VALUES (@rice_phase_id+3, @upkeep_name, @planning_phase, '2022', '1','2022-01-01', '2022-09-01', '0', '0', @rice_phase_id+4, @global_unit_rice);
INSERT INTO `phases` (`id`, `name`, `description`, `year`, `upkeep`, `start_date`, `end_date`, `editable`, `visible`, `next_phase`, `global_unit_id`) 
VALUES (@rice_phase_id+2, @powb_name, @planning_phase, '2022', '0','2021-09-01', '2021-12-31', '0', '0', @rice_phase_id+3, @global_unit_rice);
INSERT INTO `phases` (`id`, `name`, `description`, `year`, `upkeep`, `start_date`, `end_date`, `editable`, `visible`, `next_phase`, `global_unit_id`) 
VALUES (@rice_phase_id+1, @ar_name, @reporting_phase, '2021', '0','2022-01-01', '2022-04-01', '0', '0', @rice_phase_id+2, @global_unit_rice);
INSERT INTO `phases` (`id`, `name`, `description`, `year`, `upkeep`, `start_date`, `end_date`, `editable`, `visible`, `next_phase`, `global_unit_id`) 
VALUES (@rice_phase_id, @upkeep_name, @planning_phase, '2021', '1','2021-01-01', '2021-09-01', '0', '0', @rice_phase_id+1, @global_unit_rice);
UPDATE `phases` SET `next_phase`=@rice_phase_id WHERE (`id`=@rice_phase_2022);

/**
 * RTB
 **/

SET @global_unit_rtb='17';
SET @rtb_phase_id = @rice_phase_id+5;
SET @rtb_phase_2022=(SELECT p.id FROM phases p WHERE p.global_unit_id = @global_unit_rtb AND p.description = @planning_phase AND p.`year` = 2021);

INSERT INTO `phases` (`id`, `name`, `description`, `year`, `upkeep`, `start_date`, `end_date`, `editable`, `visible`, `next_phase`, `global_unit_id`) 
VALUES (@rtb_phase_id+4, @ar_name, @reporting_phase, '2022', '0','2023-01-01', '2023-04-01', '0', '0', NULL, @global_unit_rtb);
INSERT INTO `phases` (`id`, `name`, `description`, `year`, `upkeep`, `start_date`, `end_date`, `editable`, `visible`, `next_phase`, `global_unit_id`) 
VALUES (@rtb_phase_id+3, @upkeep_name, @planning_phase, '2022', '1','2022-01-01', '2022-09-01', '0', '0', @rtb_phase_id+4, @global_unit_rtb);
INSERT INTO `phases` (`id`, `name`, `description`, `year`, `upkeep`, `start_date`, `end_date`, `editable`, `visible`, `next_phase`, `global_unit_id`) 
VALUES (@rtb_phase_id+2, @powb_name, @planning_phase, '2022', '0','2021-09-01', '2021-12-31', '0', '0', @rtb_phase_id+3, @global_unit_rtb);
INSERT INTO `phases` (`id`, `name`, `description`, `year`, `upkeep`, `start_date`, `end_date`, `editable`, `visible`, `next_phase`, `global_unit_id`) 
VALUES (@rtb_phase_id+1, @ar_name, @reporting_phase, '2021', '0','2022-01-01', '2022-04-01', '0', '0', @rtb_phase_id+2, @global_unit_rtb);
INSERT INTO `phases` (`id`, `name`, `description`, `year`, `upkeep`, `start_date`, `end_date`, `editable`, `visible`, `next_phase`, `global_unit_id`) 
VALUES (@rtb_phase_id, @upkeep_name, @planning_phase, '2021', '1','2021-01-01', '2021-09-01', '0', '0', @rtb_phase_id+1, @global_unit_rtb);
UPDATE `phases` SET `next_phase`=@rtb_phase_id WHERE (`id`=@rtb_phase_2022);

/**
 * Fish
 **/

SET @global_unit_fish='27';
SET @fish_phase_id = @rtb_phase_id+5;
SET @fish_phase_2022=(SELECT p.id FROM phases p WHERE p.global_unit_id = @global_unit_fish AND p.description = @planning_phase AND p.`year` = 2021);

INSERT INTO `phases` (`id`, `name`, `description`, `year`, `upkeep`, `start_date`, `end_date`, `editable`, `visible`, `next_phase`, `global_unit_id`) 
VALUES (@fish_phase_id+4, @ar_name, @reporting_phase, '2022', '0','2023-01-01', '2023-04-01', '0', '0', NULL, @global_unit_fish);
INSERT INTO `phases` (`id`, `name`, `description`, `year`, `upkeep`, `start_date`, `end_date`, `editable`, `visible`, `next_phase`, `global_unit_id`) 
VALUES (@fish_phase_id+3, @upkeep_name, @planning_phase, '2022', '1','2022-01-01', '2022-09-01', '0', '0', @fish_phase_id+4, @global_unit_fish);
INSERT INTO `phases` (`id`, `name`, `description`, `year`, `upkeep`, `start_date`, `end_date`, `editable`, `visible`, `next_phase`, `global_unit_id`) 
VALUES (@fish_phase_id+2, @powb_name, @planning_phase, '2022', '0','2021-09-01', '2021-12-31', '0', '0', @fish_phase_id+3, @global_unit_fish);
INSERT INTO `phases` (`id`, `name`, `description`, `year`, `upkeep`, `start_date`, `end_date`, `editable`, `visible`, `next_phase`, `global_unit_id`) 
VALUES (@fish_phase_id+1, @ar_name, @reporting_phase, '2021', '0','2022-01-01', '2022-04-01', '0', '0', @fish_phase_id+2, @global_unit_fish);
INSERT INTO `phases` (`id`, `name`, `description`, `year`, `upkeep`, `start_date`, `end_date`, `editable`, `visible`, `next_phase`, `global_unit_id`) 
VALUES (@fish_phase_id, @upkeep_name, @planning_phase, '2021', '1','2021-01-01', '2021-09-01', '0', '0', @fish_phase_id+1, @global_unit_fish);
UPDATE `phases` SET `next_phase`=@fish_phase_id WHERE (`id`=@fish_phase_2022);


/**
 * GLDC
 **/

SET @global_unit_gldc='28';
SET @gldc_phase_id = @fish_phase_id+5;
SET @gldc_phase_2022=(SELECT p.id FROM phases p WHERE p.global_unit_id = @global_unit_gldc AND p.description = @planning_phase AND p.`year` = 2021);

INSERT INTO `phases` (`id`, `name`, `description`, `year`, `upkeep`, `start_date`, `end_date`, `editable`, `visible`, `next_phase`, `global_unit_id`) 
VALUES (@gldc_phase_id+4, @ar_name, @reporting_phase, '2022', '0','2023-01-01', '2023-04-01', '0', '0', NULL, @global_unit_gldc);
INSERT INTO `phases` (`id`, `name`, `description`, `year`, `upkeep`, `start_date`, `end_date`, `editable`, `visible`, `next_phase`, `global_unit_id`) 
VALUES (@gldc_phase_id+3, @upkeep_name, @planning_phase, '2022', '1','2022-01-01', '2022-09-01', '0', '0', @gldc_phase_id+4, @global_unit_gldc);
INSERT INTO `phases` (`id`, `name`, `description`, `year`, `upkeep`, `start_date`, `end_date`, `editable`, `visible`, `next_phase`, `global_unit_id`) 
VALUES (@gldc_phase_id+2, @powb_name, @planning_phase, '2022', '0','2021-09-01', '2021-12-31', '0', '0', @gldc_phase_id+3, @global_unit_gldc);
INSERT INTO `phases` (`id`, `name`, `description`, `year`, `upkeep`, `start_date`, `end_date`, `editable`, `visible`, `next_phase`, `global_unit_id`) 
VALUES (@gldc_phase_id+1, @ar_name, @reporting_phase, '2021', '0','2022-01-01', '2022-04-01', '0', '0', @gldc_phase_id+2, @global_unit_gldc);
INSERT INTO `phases` (`id`, `name`, `description`, `year`, `upkeep`, `start_date`, `end_date`, `editable`, `visible`, `next_phase`, `global_unit_id`) 
VALUES (@gldc_phase_id, @upkeep_name, @planning_phase, '2021', '1','2021-01-01', '2021-09-01', '0', '0', @gldc_phase_id+1, @global_unit_gldc);
UPDATE `phases` SET `next_phase`=@gldc_phase_id WHERE (`id`=@gldc_phase_2022);