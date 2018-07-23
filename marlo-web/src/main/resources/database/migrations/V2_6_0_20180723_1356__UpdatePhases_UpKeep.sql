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

UPDATE `phases` SET `name`=@powb_name WHERE `description`=@planning_phase AND year<=2017;
UPDATE `phases` SET `name`=@ar_name WHERE `description`=@reporting_phase AND year<=2017;
UPDATE `phases` SET `name`=@powb_name WHERE (`description`=@planning_phase and `year`=2018);
UPDATE `phases` SET `name`=@upkeep_name,`upkeep`='1',`description`=@planning_phase WHERE (`description`=@reporting_phase and `year`=2018);
UPDATE `phases` SET `name`=@ar_name,`description`=@reporting_phase, `year`='2018' WHERE (`description`=@planning_phase and `year`=2019);
UPDATE `phases` SET `name`=@powb_name,`description`=@planning_phase, `year`='2019' WHERE (`description`=@reporting_phase and `year`=2019);
UPDATE `phases` SET `name`=@upkeep_name,`upkeep`='1',`year`='2019' WHERE (`description`=@planning_phase and `year`=2020);
UPDATE `phases` SET `name`=@ar_name,`year`='2019' WHERE (`description`=@reporting_phase and `year`=2020);
UPDATE `phases` SET `name`=@powb_name,`year`='2020' WHERE (`description`=@planning_phase and `year`=2021);
UPDATE `phases` SET `name`=@upkeep_name,`description`=@planning_phase,`upkeep`='1',`year`='2020' WHERE (`description`=@reporting_phase and `year`=2021);
UPDATE `phases` SET `name`=@ar_name,`description`=@reporting_phase,`year`='2020' WHERE (`description`=@planning_phase and `year`=2022);
UPDATE `phases` SET `name`=@powb_name,`description`=@planning_phase,`year`='2021' WHERE (`description`=@reporting_phase and `year`=2022);

-- ----------------------------
-- Insert/Update remaining phases
-- ----------------------------

/**
 * CCAFS
 **/
INSERT INTO `phases` (`id`, `name`, `description`, `year`, `upkeep`, `start_date`, `end_date`, `editable`, `visible`, `next_phase`, `global_unit_id`) 
VALUES ('199', @ar_name, @reporting_phase, '2022', '0', NULL, NULL, '0', '0', NULL, '1');
INSERT INTO `phases` (`id`, `name`, `description`, `year`, `upkeep`, `start_date`, `end_date`, `editable`, `visible`, `next_phase`, `global_unit_id`) 
VALUES ('198', @upkeep_name, @planning_phase, '2022', '1', NULL, NULL, '0', '0', '199', '1');
INSERT INTO `phases` (`id`, `name`, `description`, `year`, `upkeep`, `start_date`, `end_date`, `editable`, `visible`, `next_phase`, `global_unit_id`) 
VALUES ('197', @powb_name, @planning_phase, '2022', '0', NULL, NULL, '0', '0', '198', '1');
INSERT INTO `phases` (`id`, `name`, `description`, `year`, `upkeep`, `start_date`, `end_date`, `editable`, `visible`, `next_phase`, `global_unit_id`) 
VALUES ('196', @ar_name, @reporting_phase, '2021', '0', NULL, NULL, '0', '0', '197', '1');
INSERT INTO `phases` (`id`, `name`, `description`, `year`, `upkeep`, `start_date`, `end_date`, `editable`, `visible`, `next_phase`, `global_unit_id`) 
VALUES ('195', @upkeep_name, @planning_phase, '2021', '1', NULL, NULL, '0', '0', '196', '1');
UPDATE `phases` SET `next_phase`='195' WHERE (`id`='161');

/**
 * PIM
 **/
INSERT INTO `phases` (`id`, `name`, `description`, `year`, `upkeep`, `start_date`, `end_date`, `editable`, `visible`, `next_phase`, `global_unit_id`) 
VALUES ('204', @ar_name, @reporting_phase, '2022', '0', NULL, NULL, '0', '0', NULL, '3');
INSERT INTO `phases` (`id`, `name`, `description`, `year`, `upkeep`, `start_date`, `end_date`, `editable`, `visible`, `next_phase`, `global_unit_id`) 
VALUES ('203', @upkeep_name, @planning_phase, '2022', '1', NULL, NULL, '0', '0', '204', '3');
INSERT INTO `phases` (`id`, `name`, `description`, `year`, `upkeep`, `start_date`, `end_date`, `editable`, `visible`, `next_phase`, `global_unit_id`) 
VALUES ('202', @powb_name, @planning_phase, '2022', '0', NULL, NULL, '0', '0', '203', '3');
INSERT INTO `phases` (`id`, `name`, `description`, `year`, `upkeep`, `start_date`, `end_date`, `editable`, `visible`, `next_phase`, `global_unit_id`) 
VALUES ('201', @ar_name, @reporting_phase, '2021', '0', NULL, NULL, '0', '0', '202', '3');
INSERT INTO `phases` (`id`, `name`, `description`, `year`, `upkeep`, `start_date`, `end_date`, `editable`, `visible`, `next_phase`, `global_unit_id`) 
VALUES ('200', @upkeep_name, @planning_phase, '2021', '1', NULL, NULL, '0', '0', '201', '3');
UPDATE `phases` SET `next_phase`='200' WHERE (`id`='162');


/**
 * WLE
 **/
INSERT INTO `phases` (`id`, `name`, `description`, `year`, `upkeep`, `start_date`, `end_date`, `editable`, `visible`, `next_phase`, `global_unit_id`) 
VALUES ('209', @ar_name, @reporting_phase, '2022', '0', NULL, NULL, '0', '0', NULL, '4');
INSERT INTO `phases` (`id`, `name`, `description`, `year`, `upkeep`, `start_date`, `end_date`, `editable`, `visible`, `next_phase`, `global_unit_id`) 
VALUES ('208', @upkeep_name, @planning_phase, '2022', '1', NULL, NULL, '0', '0', '209', '4');
INSERT INTO `phases` (`id`, `name`, `description`, `year`, `upkeep`, `start_date`, `end_date`, `editable`, `visible`, `next_phase`, `global_unit_id`) 
VALUES ('207', @powb_name, @planning_phase, '2022', '0', NULL, NULL, '0', '0', '208', '4');
INSERT INTO `phases` (`id`, `name`, `description`, `year`, `upkeep`, `start_date`, `end_date`, `editable`, `visible`, `next_phase`, `global_unit_id`) 
VALUES ('206', @ar_name, @reporting_phase, '2021', '0', NULL, NULL, '0', '0', '207', '4');
INSERT INTO `phases` (`id`, `name`, `description`, `year`, `upkeep`, `start_date`, `end_date`, `editable`, `visible`, `next_phase`, `global_unit_id`) 
VALUES ('205', @upkeep_name, @planning_phase, '2021', '1', NULL, NULL, '0', '0', '206', '4');
UPDATE `phases` SET `next_phase`='205' WHERE (`id`='163');

/**
 * A4NH
 **/
INSERT INTO `phases` (`id`, `name`, `description`, `year`, `upkeep`, `start_date`, `end_date`, `editable`, `visible`, `next_phase`, `global_unit_id`) 
VALUES ('214', @ar_name, @reporting_phase, '2022', '0', NULL, NULL, '0', '0', NULL, '5');
INSERT INTO `phases` (`id`, `name`, `description`, `year`, `upkeep`, `start_date`, `end_date`, `editable`, `visible`, `next_phase`, `global_unit_id`) 
VALUES ('213', @upkeep_name, @planning_phase, '2022', '1', NULL, NULL, '0', '0', '214', '5');
INSERT INTO `phases` (`id`, `name`, `description`, `year`, `upkeep`, `start_date`, `end_date`, `editable`, `visible`, `next_phase`, `global_unit_id`) 
VALUES ('212', @powb_name, @planning_phase, '2022', '0', NULL, NULL, '0', '0', '213', '5');
INSERT INTO `phases` (`id`, `name`, `description`, `year`, `upkeep`, `start_date`, `end_date`, `editable`, `visible`, `next_phase`, `global_unit_id`) 
VALUES ('211', @ar_name, @reporting_phase, '2021', '0', NULL, NULL, '0', '0', '212', '5');
INSERT INTO `phases` (`id`, `name`, `description`, `year`, `upkeep`, `start_date`, `end_date`, `editable`, `visible`, `next_phase`, `global_unit_id`) 
VALUES ('210', @upkeep_name, @planning_phase, '2021', '1', NULL, NULL, '0', '0', '211', '5');
UPDATE `phases` SET `next_phase`='210' WHERE (`id`='164');

/**
 * Livestock
 **/
INSERT INTO `phases` (`id`, `name`, `description`, `year`, `upkeep`, `start_date`, `end_date`, `editable`, `visible`, `next_phase`, `global_unit_id`) 
VALUES ('219', @ar_name, @reporting_phase, '2022', '0', NULL, NULL, '0', '0', NULL, '7');
INSERT INTO `phases` (`id`, `name`, `description`, `year`, `upkeep`, `start_date`, `end_date`, `editable`, `visible`, `next_phase`, `global_unit_id`) 
VALUES ('218', @upkeep_name, @planning_phase, '2022', '1', NULL, NULL, '0', '0', '219', '7');
INSERT INTO `phases` (`id`, `name`, `description`, `year`, `upkeep`, `start_date`, `end_date`, `editable`, `visible`, `next_phase`, `global_unit_id`) 
VALUES ('217', @powb_name, @planning_phase, '2022', '0', NULL, NULL, '0', '0', '218', '7');
INSERT INTO `phases` (`id`, `name`, `description`, `year`, `upkeep`, `start_date`, `end_date`, `editable`, `visible`, `next_phase`, `global_unit_id`) 
VALUES ('216', @ar_name, @reporting_phase, '2021', '0', NULL, NULL, '0', '0', '217', '7');
INSERT INTO `phases` (`id`, `name`, `description`, `year`, `upkeep`, `start_date`, `end_date`, `editable`, `visible`, `next_phase`, `global_unit_id`) 
VALUES ('215', @upkeep_name, @planning_phase, '2021', '1', NULL, NULL, '0', '0', '216', '7');
UPDATE `phases` SET `next_phase`='215' WHERE (`id`='165');

/**
 * FTA
 **/
INSERT INTO `phases` (`id`, `name`, `description`, `year`, `upkeep`, `start_date`, `end_date`, `editable`, `visible`, `next_phase`, `global_unit_id`) 
VALUES ('224', @ar_name, @reporting_phase, '2022', '0', NULL, NULL, '0', '0', NULL, '11');
INSERT INTO `phases` (`id`, `name`, `description`, `year`, `upkeep`, `start_date`, `end_date`, `editable`, `visible`, `next_phase`, `global_unit_id`) 
VALUES ('223', @upkeep_name, @planning_phase, '2022', '1', NULL, NULL, '0', '0', '224', '11');
INSERT INTO `phases` (`id`, `name`, `description`, `year`, `upkeep`, `start_date`, `end_date`, `editable`, `visible`, `next_phase`, `global_unit_id`) 
VALUES ('222', @powb_name, @planning_phase, '2022', '0', NULL, NULL, '0', '0', '223', '11');
INSERT INTO `phases` (`id`, `name`, `description`, `year`, `upkeep`, `start_date`, `end_date`, `editable`, `visible`, `next_phase`, `global_unit_id`) 
VALUES ('221', @ar_name, @reporting_phase, '2021', '0', NULL, NULL, '0', '0', '222', '11');
INSERT INTO `phases` (`id`, `name`, `description`, `year`, `upkeep`, `start_date`, `end_date`, `editable`, `visible`, `next_phase`, `global_unit_id`) 
VALUES ('220', @upkeep_name, @planning_phase, '2021', '1', NULL, NULL, '0', '0', '221', '11');
UPDATE `phases` SET `next_phase`='220' WHERE (`id`='166');

/**
 * Wheat
 **/
INSERT INTO `phases` (`id`, `name`, `description`, `year`, `upkeep`, `start_date`, `end_date`, `editable`, `visible`, `next_phase`, `global_unit_id`) 
VALUES ('229', @ar_name, @reporting_phase, '2022', '0', NULL, NULL, '0', '0', NULL, '21');
INSERT INTO `phases` (`id`, `name`, `description`, `year`, `upkeep`, `start_date`, `end_date`, `editable`, `visible`, `next_phase`, `global_unit_id`) 
VALUES ('228', @upkeep_name, @planning_phase, '2022', '1', NULL, NULL, '0', '0', '229', '21');
INSERT INTO `phases` (`id`, `name`, `description`, `year`, `upkeep`, `start_date`, `end_date`, `editable`, `visible`, `next_phase`, `global_unit_id`) 
VALUES ('227', @powb_name, @planning_phase, '2022', '0', NULL, NULL, '0', '0', '228', '21');
INSERT INTO `phases` (`id`, `name`, `description`, `year`, `upkeep`, `start_date`, `end_date`, `editable`, `visible`, `next_phase`, `global_unit_id`) 
VALUES ('226', @ar_name, @reporting_phase, '2021', '0', NULL, NULL, '0', '0', '227', '21');
INSERT INTO `phases` (`id`, `name`, `description`, `year`, `upkeep`, `start_date`, `end_date`, `editable`, `visible`, `next_phase`, `global_unit_id`) 
VALUES ('225', @upkeep_name, @planning_phase, '2021', '1', NULL, NULL, '0', '0', '226', '21');
UPDATE `phases` SET `next_phase`='225' WHERE (`id`='167');

/**
 * Maize
 **/
INSERT INTO `phases` (`id`, `name`, `description`, `year`, `upkeep`, `start_date`, `end_date`, `editable`, `visible`, `next_phase`, `global_unit_id`) 
VALUES ('234', @ar_name, @reporting_phase, '2022', '0', NULL, NULL, '0', '0', NULL, '22');
INSERT INTO `phases` (`id`, `name`, `description`, `year`, `upkeep`, `start_date`, `end_date`, `editable`, `visible`, `next_phase`, `global_unit_id`) 
VALUES ('233', @upkeep_name, @planning_phase, '2022', '1', NULL, NULL, '0', '0', '234', '22');
INSERT INTO `phases` (`id`, `name`, `description`, `year`, `upkeep`, `start_date`, `end_date`, `editable`, `visible`, `next_phase`, `global_unit_id`) 
VALUES ('232', @powb_name, @planning_phase, '2022', '0', NULL, NULL, '0', '0', '233', '22');
INSERT INTO `phases` (`id`, `name`, `description`, `year`, `upkeep`, `start_date`, `end_date`, `editable`, `visible`, `next_phase`, `global_unit_id`) 
VALUES ('231', @ar_name, @reporting_phase, '2021', '0', NULL, NULL, '0', '0', '232', '22');
INSERT INTO `phases` (`id`, `name`, `description`, `year`, `upkeep`, `start_date`, `end_date`, `editable`, `visible`, `next_phase`, `global_unit_id`) 
VALUES ('230', @upkeep_name, @planning_phase, '2021', '1', NULL, NULL, '0', '0', '231', '22');
UPDATE `phases` SET `next_phase`='230' WHERE (`id`='168');


/**
 * BigData
 **/
INSERT INTO `phases` (`id`, `name`, `description`, `year`, `upkeep`, `start_date`, `end_date`, `editable`, `visible`, `next_phase`, `global_unit_id`) 
VALUES ('239', @ar_name, @reporting_phase, '2022', '0', NULL, NULL, '0', '0', NULL, '24');
INSERT INTO `phases` (`id`, `name`, `description`, `year`, `upkeep`, `start_date`, `end_date`, `editable`, `visible`, `next_phase`, `global_unit_id`) 
VALUES ('238', @upkeep_name, @planning_phase, '2022', '1', NULL, NULL, '0', '0', '239', '24');
INSERT INTO `phases` (`id`, `name`, `description`, `year`, `upkeep`, `start_date`, `end_date`, `editable`, `visible`, `next_phase`, `global_unit_id`) 
VALUES ('237', @powb_name, @planning_phase, '2022', '0', NULL, NULL, '0', '0', '238', '24');
INSERT INTO `phases` (`id`, `name`, `description`, `year`, `upkeep`, `start_date`, `end_date`, `editable`, `visible`, `next_phase`, `global_unit_id`) 
VALUES ('236', @ar_name, @reporting_phase, '2021', '0', NULL, NULL, '0', '0', '237', '24');
INSERT INTO `phases` (`id`, `name`, `description`, `year`, `upkeep`, `start_date`, `end_date`, `editable`, `visible`, `next_phase`, `global_unit_id`) 
VALUES ('235', @upkeep_name, @planning_phase, '2021', '1', NULL, NULL, '0', '0', '236', '24');
UPDATE `phases` SET `next_phase`='235' WHERE (`id`='170');

/**
 * EiB
 **/
INSERT INTO `phases` (`id`, `name`, `description`, `year`, `upkeep`, `start_date`, `end_date`, `editable`, `visible`, `next_phase`, `global_unit_id`) 
VALUES ('244', @ar_name, @reporting_phase, '2022', '0', NULL, NULL, '0', '0', NULL, '25');
INSERT INTO `phases` (`id`, `name`, `description`, `year`, `upkeep`, `start_date`, `end_date`, `editable`, `visible`, `next_phase`, `global_unit_id`) 
VALUES ('243', @upkeep_name, @planning_phase, '2022', '1', NULL, NULL, '0', '0', '244', '25');
INSERT INTO `phases` (`id`, `name`, `description`, `year`, `upkeep`, `start_date`, `end_date`, `editable`, `visible`, `next_phase`, `global_unit_id`) 
VALUES ('242', @powb_name, @planning_phase, '2022', '0', NULL, NULL, '0', '0', '243', '25');
INSERT INTO `phases` (`id`, `name`, `description`, `year`, `upkeep`, `start_date`, `end_date`, `editable`, `visible`, `next_phase`, `global_unit_id`) 
VALUES ('241', @ar_name, @reporting_phase, '2021', '0', NULL, NULL, '0', '0', '242', '25');
INSERT INTO `phases` (`id`, `name`, `description`, `year`, `upkeep`, `start_date`, `end_date`, `editable`, `visible`, `next_phase`, `global_unit_id`) 
VALUES ('240', @upkeep_name, @planning_phase, '2021', '1', NULL, NULL, '0', '0', '241', '25');
UPDATE `phases` SET `next_phase`='240' WHERE (`id`='171');


/**
 * CIAT
 **/
INSERT INTO `phases` (`id`, `name`, `description`, `year`, `upkeep`, `start_date`, `end_date`, `editable`, `visible`, `next_phase`, `global_unit_id`) 
VALUES ('249', @ar_name, @reporting_phase, '2022', '0', NULL, NULL, '0', '0', NULL, '29');
INSERT INTO `phases` (`id`, `name`, `description`, `year`, `upkeep`, `start_date`, `end_date`, `editable`, `visible`, `next_phase`, `global_unit_id`) 
VALUES ('248', @upkeep_name, @planning_phase, '2022', '1', NULL, NULL, '0', '0', '249', '29');
INSERT INTO `phases` (`id`, `name`, `description`, `year`, `upkeep`, `start_date`, `end_date`, `editable`, `visible`, `next_phase`, `global_unit_id`) 
VALUES ('247', @powb_name, @planning_phase, '2022', '0', NULL, NULL, '0', '0', '248', '29');
INSERT INTO `phases` (`id`, `name`, `description`, `year`, `upkeep`, `start_date`, `end_date`, `editable`, `visible`, `next_phase`, `global_unit_id`) 
VALUES ('246', @ar_name, @reporting_phase, '2021', '0', NULL, NULL, '0', '0', '247', '29');
INSERT INTO `phases` (`id`, `name`, `description`, `year`, `upkeep`, `start_date`, `end_date`, `editable`, `visible`, `next_phase`, `global_unit_id`) 
VALUES ('245', @upkeep_name, @planning_phase, '2021', '1', NULL, NULL, '0', '0', '246', '29');
UPDATE `phases` SET `next_phase`='245' WHERE (`id`='184');


/**
 * Rice
 **/
INSERT INTO `phases` (`id`, `name`, `description`, `year`, `upkeep`, `start_date`, `end_date`, `editable`, `visible`, `next_phase`, `global_unit_id`) 
VALUES ('254', @ar_name, @reporting_phase, '2022', '0', NULL, NULL, '0', '0', NULL, '16');
INSERT INTO `phases` (`id`, `name`, `description`, `year`, `upkeep`, `start_date`, `end_date`, `editable`, `visible`, `next_phase`, `global_unit_id`) 
VALUES ('253', @upkeep_name, @planning_phase, '2022', '1', NULL, NULL, '0', '0', '254', '16');
INSERT INTO `phases` (`id`, `name`, `description`, `year`, `upkeep`, `start_date`, `end_date`, `editable`, `visible`, `next_phase`, `global_unit_id`) 
VALUES ('252', @powb_name, @planning_phase, '2022', '0', NULL, NULL, '0', '0', '253', '16');
INSERT INTO `phases` (`id`, `name`, `description`, `year`, `upkeep`, `start_date`, `end_date`, `editable`, `visible`, `next_phase`, `global_unit_id`) 
VALUES ('251', @ar_name, @reporting_phase, '2021', '0', NULL, NULL, '0', '0', '252', '16');
INSERT INTO `phases` (`id`, `name`, `description`, `year`, `upkeep`, `start_date`, `end_date`, `editable`, `visible`, `next_phase`, `global_unit_id`) 
VALUES ('250', @upkeep_name, @planning_phase, '2021', '1', NULL, NULL, '0', '0', '251', '16');
UPDATE `phases` SET `next_phase`='250' WHERE (`id`='194');