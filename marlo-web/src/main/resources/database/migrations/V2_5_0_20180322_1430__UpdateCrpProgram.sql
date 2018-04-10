/*
Navicat MySQL Data Transfer

Source Server         : LOCALHOST
Source Server Version : 50718
Source Host           : localhost:3306
Source Database       : marlo_ciat

Target Server Type    : MYSQL
Target Server Version : 50718
File Encoding         : 65001

Date: 2018-03-22 14:27:37
*/

SET FOREIGN_KEY_CHECKS=0;

-- ----------------------------
-- Table structure for crp_programs
-- ----------------------------
DROP TABLE IF EXISTS `crp_programs`;
CREATE TABLE `crp_programs` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `name` text NOT NULL,
  `acronym` varchar(50) NOT NULL,
  `program_type` int(11) NOT NULL,
  `color` varchar(8) DEFAULT NULL,
  `area_id` int(11) DEFAULT NULL,
  `is_active` tinyint(1) NOT NULL,
  `created_by` bigint(20) DEFAULT NULL,
  `active_since` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `modified_by` bigint(20) NOT NULL,
  `modification_justification` text NOT NULL,
  `base_line` tinyint(1) DEFAULT '0',
  `global_unit_id` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `fk_crp_programs_created_by_users_id` (`created_by`) USING BTREE,
  KEY `fk_crp_programs_modified_by_users_id` (`modified_by`) USING BTREE,
  KEY `crp_programs_global_unit_fk` (`global_unit_id`) USING BTREE,
  KEY `crp_programs_ibfk_4` (`area_id`),
  CONSTRAINT `crp_programs_ibfk_1` FOREIGN KEY (`global_unit_id`) REFERENCES `global_units` (`id`),
  CONSTRAINT `crp_programs_ibfk_2` FOREIGN KEY (`created_by`) REFERENCES `users` (`id`),
  CONSTRAINT `crp_programs_ibfk_3` FOREIGN KEY (`modified_by`) REFERENCES `users` (`id`),
  CONSTRAINT `crp_programs_ibfk_4` FOREIGN KEY (`area_id`) REFERENCES `center_areas` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=145 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of crp_programs
-- ----------------------------
INSERT INTO `crp_programs` VALUES ('84', 'Climate services and safety nets', 'F4', '1', '#8139B6', null, '1', '843', '2018-03-08 14:18:44', '1', '', null, '1');
INSERT INTO `crp_programs` VALUES ('85', 'Climate-Smart Technologies and Practices', 'F2', '1', '#f39c12', null, '1', '843', '2018-02-02 14:41:22', '1', '', null, '1');
INSERT INTO `crp_programs` VALUES ('86', 'Low emissions development', 'F3', '1', '#71CE48', null, '1', '843', '2018-02-02 16:59:31', '1', '', '1', '1');
INSERT INTO `crp_programs` VALUES ('87', 'Priorities and Policies for CSA', 'F1', '1', '#4B91D7', null, '1', '843', '2018-02-06 01:37:58', '1', '', null, '1');
INSERT INTO `crp_programs` VALUES ('88', 'Latin America', 'LAM', '2', '#5F688B', null, '1', '843', '2016-07-11 10:30:34', '1', '', '0', '1');
INSERT INTO `crp_programs` VALUES ('89', 'East Africa', 'EA', '2', '#6267B0', null, '1', '843', '2016-07-11 10:30:34', '1', '', '0', '1');
INSERT INTO `crp_programs` VALUES ('90', 'West Africa', 'WA', '2', '#0ECF3B', null, '1', '843', '2016-07-11 10:33:34', '1', '', '0', '1');
INSERT INTO `crp_programs` VALUES ('91', 'Southeast Asia', 'SEA', '2', '#F54423', null, '1', '843', '2016-07-11 10:33:34', '1', '', '0', '1');
INSERT INTO `crp_programs` VALUES ('92', 'South Asia', 'SAs', '2', '#956611', null, '1', '843', '2016-07-11 10:33:34', '1', '', '0', '1');
INSERT INTO `crp_programs` VALUES ('93', 'Food Systems for Healthier Diets', 'F1', '1', '#54D189', null, '1', '988', '2018-02-06 05:45:00', '988', '', null, '5');
INSERT INTO `crp_programs` VALUES ('94', 'Biofortification', 'F2', '1', '#908BD6', null, '1', '988', '2018-03-06 15:30:30', '988', '', null, '5');
INSERT INTO `crp_programs` VALUES ('95', 'Food Safety', 'F3', '1', '#FC7C72', null, '1', '988', '2018-02-15 03:34:45', '988', '', null, '5');
INSERT INTO `crp_programs` VALUES ('96', 'Supporting Policies, Programs, and Enabling Action through Research (SPEAR)', 'F4', '1', '#BA8D6B', null, '1', '988', '2017-08-01 16:36:37', '988', '', null, '5');
INSERT INTO `crp_programs` VALUES ('97', 'Improving Human Health', 'F5', '1', '#2AA4EA', null, '1', '988', '2018-03-01 21:46:39', '988', '', null, '5');
INSERT INTO `crp_programs` VALUES ('98', 'Restoring Degraded Landscapes (RDL)', 'F1', '1', '#d35400', null, '1', '1224', '2018-03-13 22:53:20', '1224', '', null, '4');
INSERT INTO `crp_programs` VALUES ('99', 'Land and Water Solutions for Sustainable Intensification (LWS)', 'F2', '1', '#3498db', null, '1', '1224', '2018-03-16 05:06:44', '1224', '', null, '4');
INSERT INTO `crp_programs` VALUES ('100', 'Sustaining Rural-Urban Linkages (RUL)', 'F3', '1', '#bdc3c7', null, '1', '1224', '2018-03-15 01:32:27', '1224', '', null, '4');
INSERT INTO `crp_programs` VALUES ('101', 'Managing Resource Variability, Risks and Competing Uses for Increased Resilience (VCR)', 'F4', '1', '#27ae60', null, '1', '1224', '2018-03-14 00:46:02', '1224', '', null, '4');
INSERT INTO `crp_programs` VALUES ('102', 'Enhancing Sustainability Across Agricultural Systems (ESA)', 'F5', '1', '#8e44ad', null, '1', '1224', '2018-03-15 00:59:15', '1224', '', null, '4');
INSERT INTO `crp_programs` VALUES ('103', 'Livestock Genetics', 'F1', '1', '#B47B94', null, '1', '1', '2018-03-07 03:44:28', '1332', '', null, '7');
INSERT INTO `crp_programs` VALUES ('104', 'Livestock Health', 'F2', '1', '#28D7CA', null, '1', '1', '2018-03-09 04:16:25', '1332', '', null, '7');
INSERT INTO `crp_programs` VALUES ('105', 'Livestock Feeds and Forages', 'F3', '1', '#354E6F', null, '1', '1', '2018-03-19 08:26:07', '1426', '', null, '7');
INSERT INTO `crp_programs` VALUES ('106', 'Livestock and the Environment', 'F4', '1', '#F8785C', null, '1', '1', '2018-03-09 04:29:15', '1332', '', null, '7');
INSERT INTO `crp_programs` VALUES ('107', 'Livestock and Livelihoods and Agri-Food Systems', 'F5', '1', '#148E9E', null, '1', '1', '2018-03-13 03:53:53', '1332', '', null, '7');
INSERT INTO `crp_programs` VALUES ('108', 'Technological Innovation and Sustainable Intensification', 'F1', '1', '#3945B0', null, '1', '1361', '2018-02-21 17:04:06', '1320', '', null, '3');
INSERT INTO `crp_programs` VALUES ('109', 'Economywide Factors Affecting Agricultural Growth and Rural Transformation', 'F2', '1', '#AE28B4', null, '1', '1361', '2018-02-21 16:45:37', '1320', '', null, '3');
INSERT INTO `crp_programs` VALUES ('110', 'Inclusive and Efficient Value Chains', 'F3', '1', '#58B37F', null, '1', '1361', '2018-02-21 16:44:20', '1320', '', null, '3');
INSERT INTO `crp_programs` VALUES ('111', 'Social Protection for Agriculture and Resilience', 'F4', '1', '#F01145', null, '1', '1361', '2018-02-21 16:50:19', '1320', '', null, '3');
INSERT INTO `crp_programs` VALUES ('112', 'Governance of Natural Resources', 'F5', '1', '#4174F3', null, '1', '1361', '2018-02-21 17:02:19', '1320', '', null, '3');
INSERT INTO `crp_programs` VALUES ('113', 'Cross-cutting Gender Research and Coordination', 'F6', '1', '#C1B255', null, '1', '1361', '2018-02-21 17:22:19', '1320', '', null, '3');
INSERT INTO `crp_programs` VALUES ('114', 'Enhancing MAIZE\'s R4D Strategy for Impact', 'FP1', '1', '#95E57E', null, '1', '1408', '2017-12-22 13:45:30', '1408', '', null, '22');
INSERT INTO `crp_programs` VALUES ('115', 'Novel Diversity and Tools for Increasing Genetic Gains', 'FP2', '1', '#090FB7', null, '1', '1408', '2018-01-12 12:34:14', '1408', '', null, '22');
INSERT INTO `crp_programs` VALUES ('116', 'Enhancing WHEAT\'s R4D Strategy for Impact', 'FP1', '1', '#653097', null, '1', '1408', '2017-12-22 15:28:17', '1408', '', null, '21');
INSERT INTO `crp_programs` VALUES ('117', 'Novel diversity and tools for improving genetic gains and breeding efficiency', 'FP2', '1', '#32E4B2', null, '1', '1408', '2018-02-15 11:23:02', '1408', '', null, '21');
INSERT INTO `crp_programs` VALUES ('118', 'Better varieties reach farmers faster', 'FP3', '1', '#2D0284', null, '1', '1408', '2018-03-15 16:54:04', '1408', '', null, '21');
INSERT INTO `crp_programs` VALUES ('119', 'Sustainable intensification of wheat-based farming systems', 'FP4', '1', '#1188FF', null, '1', '1408', '2018-02-21 12:54:42', '1408', '', null, '21');
INSERT INTO `crp_programs` VALUES ('120', 'Stress Tolerant and Nutritious Maize', 'FP3', '1', '#EAB57D', null, '1', '1408', '2018-01-31 16:31:18', '1408', '', null, '22');
INSERT INTO `crp_programs` VALUES ('121', 'Sustainable intensification of maize-based systems for improved smallholder livelihoods', 'FP4', '1', '#9D7FB6', null, '1', '1408', '2018-03-15 14:39:37', '1408', '', null, '22');
INSERT INTO `crp_programs` VALUES ('122', 'Tree genetic resources to bridge production gaps and promote resilience', 'FP1', '1', '#3498db', null, '1', '1618', '2018-03-18 22:38:59', '1718', '', '0', '11');
INSERT INTO `crp_programs` VALUES ('123', 'Enhancing how trees and forests contribute to smallholder livelihoods', 'FP2', '1', '#e74c3c', null, '1', '1618', '2018-02-27 04:04:17', '1718', '', '0', '11');
INSERT INTO `crp_programs` VALUES ('124', 'Sustainable global value chains and investments for supporting forest conservation and equitable development', 'FP3', '1', '#41BB76', null, '1', '1618', '2018-03-13 06:17:47', '1618', '', '0', '11');
INSERT INTO `crp_programs` VALUES ('125', 'Landscape dynamics, productivity and resilience', 'FP4', '1', '#8e44ad', null, '1', '1618', '2018-03-18 22:55:59', '1718', '', '0', '11');
INSERT INTO `crp_programs` VALUES ('126', 'Climate change mitigation and adaptation opportunities in forests, trees and agroforestry', 'FP5', '1', '#C1FA18', null, '1', '1618', '2018-02-27 03:42:58', '1718', '', '0', '11');
INSERT INTO `crp_programs` VALUES ('127', 'Organize', 'M1', '1', '#3498db', null, '1', '1', '2018-03-16 10:20:41', '508', '', null, '24');
INSERT INTO `crp_programs` VALUES ('128', 'Convene', 'M2', '1', '#31831A', null, '1', '1', '2018-03-15 09:10:08', '1938', '', null, '24');
INSERT INTO `crp_programs` VALUES ('129', 'Inspire', 'M3', '1', '#f39c12', null, '1', '1', '2018-03-15 09:10:54', '1938', '', null, '24');
INSERT INTO `crp_programs` VALUES ('130', 'Sustainable Food Systems', 'SFS', '1', '#D5D5D5', '4', '1', '3', '2018-03-21 15:02:15', '3', '', '0', '29');
INSERT INTO `crp_programs` VALUES ('131', 'Climate Change', 'CC', '1', '#D5D5D5', '4', '1', '3', '2018-03-21 15:02:15', '3', '', '0', '29');
INSERT INTO `crp_programs` VALUES ('132', 'Ecosystem Services', 'ES', '1', '#D5D5D5', '4', '1', '3', '2018-03-21 15:02:15', '3', '', '0', '29');
INSERT INTO `crp_programs` VALUES ('133', 'Cassava', 'C', '1', '#D5D5D5', '5', '1', '3', '2018-03-21 15:02:15', '3', '', '0', '29');
INSERT INTO `crp_programs` VALUES ('134', 'Bean', 'B', '1', '#D5D5D5', '5', '1', '3', '2018-03-21 15:02:15', '3', '', '0', '29');
INSERT INTO `crp_programs` VALUES ('135', 'Rice', 'R', '1', '#D5D5D5', '5', '1', '3', '2018-03-21 15:02:15', '3', '', '0', '29');
INSERT INTO `crp_programs` VALUES ('136', 'Genetic Resources', 'GR', '1', '#D5D5D5', '5', '1', '3', '2018-03-21 15:02:15', '3', '', '0', '29');
INSERT INTO `crp_programs` VALUES ('137', 'Tropical Forages', 'TF', '1', '#D5D5D5', '5', '1', '3', '2018-03-21 15:02:15', '3', '', '0', '29');
INSERT INTO `crp_programs` VALUES ('138', 'Soils and Climate Change', 'SCC', '1', '#D5D5D5', '6', '1', '3', '2018-03-21 15:02:15', '3', '', '0', '29');
INSERT INTO `crp_programs` VALUES ('139', 'Restoring Degraded Land', 'RDL', '1', '#D5D5D5', '6', '1', '3', '2018-03-21 15:02:15', '3', '', '0', '29');
INSERT INTO `crp_programs` VALUES ('140', 'Sustaining Soil Fertility and Health', 'SH', '1', '#D5D5D5', '6', '1', '3', '2018-03-21 15:02:15', '3', '', '0', '29');
