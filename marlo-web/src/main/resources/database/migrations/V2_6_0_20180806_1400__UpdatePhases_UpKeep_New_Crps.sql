SET FOREIGN_KEY_CHECKS=0;

-- ----------------------------
-- Update phases
-- ----------------------------
SET @reporting_phase = 'Reporting';
SET @planning_phase = 'Planning';
SET @powb_name='POWB';
SET @ar_name='AR';
SET @upkeep_name='UpKeep';
SET @global_unit_rtb='17';
SET @global_unit_fish='27';
SET @global_unit_gldc='28';

-- ----------------------------
-- Insert/Update remaining phases
-- ----------------------------

/**
 * RTB
 **/
INSERT INTO `phases` (`id`, `name`, `description`, `year`, `upkeep`, `start_date`, `end_date`, `editable`, `visible`, `next_phase`, `global_unit_id`) 
VALUES ('289', @ar_name, @reporting_phase, '2022', '0','2023-01-01', '2023-04-01', '0', '0', NULL, @global_unit_rtb);
INSERT INTO `phases` (`id`, `name`, `description`, `year`, `upkeep`, `start_date`, `end_date`, `editable`, `visible`, `next_phase`, `global_unit_id`) 
VALUES ('288', @upkeep_name, @planning_phase, '2022', '1','2022-01-01', '2022-09-01', '0', '0', '289', @global_unit_rtb);
INSERT INTO `phases` (`id`, `name`, `description`, `year`, `upkeep`, `start_date`, `end_date`, `editable`, `visible`, `next_phase`, `global_unit_id`) 
VALUES ('287', @powb_name, @planning_phase, '2022', '0','2021-09-01', '2021-12-31', '0', '0', '288', @global_unit_rtb);
INSERT INTO `phases` (`id`, `name`, `description`, `year`, `upkeep`, `start_date`, `end_date`, `editable`, `visible`, `next_phase`, `global_unit_id`) 
VALUES ('286', @ar_name, @reporting_phase, '2021', '0','2022-01-01', '2022-04-01', '0', '0', '287', @global_unit_rtb);
INSERT INTO `phases` (`id`, `name`, `description`, `year`, `upkeep`, `start_date`, `end_date`, `editable`, `visible`, `next_phase`, `global_unit_id`) 
VALUES ('285', @upkeep_name, @planning_phase, '2021', '1','2021-01-01', '2021-09-01', '0', '0', '286', @global_unit_rtb);
UPDATE `phases` SET `next_phase`='285' WHERE (`id`='264');

/**
 * Fish
 **/
INSERT INTO `phases` (`id`, `name`, `description`, `year`, `upkeep`, `start_date`, `end_date`, `editable`, `visible`, `next_phase`, `global_unit_id`) 
VALUES ('294', @ar_name, @reporting_phase, '2022', '0','2023-01-01', '2023-04-01', '0', '0', NULL, @global_unit_fish);
INSERT INTO `phases` (`id`, `name`, `description`, `year`, `upkeep`, `start_date`, `end_date`, `editable`, `visible`, `next_phase`, `global_unit_id`) 
VALUES ('293', @upkeep_name, @planning_phase, '2022', '1','2022-01-01', '2022-09-01', '0', '0', '294', @global_unit_fish);
INSERT INTO `phases` (`id`, `name`, `description`, `year`, `upkeep`, `start_date`, `end_date`, `editable`, `visible`, `next_phase`, `global_unit_id`) 
VALUES ('292', @powb_name, @planning_phase, '2022', '0','2021-09-01', '2021-12-31', '0', '0', '293', @global_unit_fish);
INSERT INTO `phases` (`id`, `name`, `description`, `year`, `upkeep`, `start_date`, `end_date`, `editable`, `visible`, `next_phase`, `global_unit_id`) 
VALUES ('291', @ar_name, @reporting_phase, '2021', '0','2022-01-01', '2022-04-01', '0', '0', '292', @global_unit_fish);
INSERT INTO `phases` (`id`, `name`, `description`, `year`, `upkeep`, `start_date`, `end_date`, `editable`, `visible`, `next_phase`, `global_unit_id`) 
VALUES ('290', @upkeep_name, @planning_phase, '2021', '1','2021-01-01', '2021-09-01', '0', '0', '291', @global_unit_fish);
UPDATE `phases` SET `next_phase`='290' WHERE (`id`='274');


/**
 * GLDC
 **/
INSERT INTO `phases` (`id`, `name`, `description`, `year`, `upkeep`, `start_date`, `end_date`, `editable`, `visible`, `next_phase`, `global_unit_id`) 
VALUES ('299', @ar_name, @reporting_phase, '2022', '0','2023-01-01', '2023-04-01', '0', '0', NULL, @global_unit_gldc);
INSERT INTO `phases` (`id`, `name`, `description`, `year`, `upkeep`, `start_date`, `end_date`, `editable`, `visible`, `next_phase`, `global_unit_id`) 
VALUES ('298', @upkeep_name, @planning_phase, '2022', '1','2022-01-01', '2022-09-01', '0', '0', '299', @global_unit_gldc);
INSERT INTO `phases` (`id`, `name`, `description`, `year`, `upkeep`, `start_date`, `end_date`, `editable`, `visible`, `next_phase`, `global_unit_id`) 
VALUES ('297', @powb_name, @planning_phase, '2022', '0','2021-09-01', '2021-12-31', '0', '0', '298', @global_unit_gldc);
INSERT INTO `phases` (`id`, `name`, `description`, `year`, `upkeep`, `start_date`, `end_date`, `editable`, `visible`, `next_phase`, `global_unit_id`) 
VALUES ('296', @ar_name, @reporting_phase, '2021', '0','2022-01-01', '2022-04-01', '0', '0', '297', @global_unit_gldc);
INSERT INTO `phases` (`id`, `name`, `description`, `year`, `upkeep`, `start_date`, `end_date`, `editable`, `visible`, `next_phase`, `global_unit_id`) 
VALUES ('295', @upkeep_name, @planning_phase, '2021', '1','2021-01-01', '2021-09-01', '0', '0', '296', @global_unit_gldc);
UPDATE `phases` SET `next_phase`='295' WHERE (`id`='284');
