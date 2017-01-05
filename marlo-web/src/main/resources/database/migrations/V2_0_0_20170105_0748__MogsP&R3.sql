/*
Navicat MySQL Data Transfer

Source Server         : local
Source Server Version : 50617
Source Host           : localhost:3306
Source Database       : ccafs_marlo

Target Server Type    : MYSQL
Target Server Version : 50617
File Encoding         : 65001

Date: 2017-01-05 07:48:51
*/

SET FOREIGN_KEY_CHECKS=0;

-- ----------------------------
-- Table structure for `ip_relationships`
-- ----------------------------
DROP TABLE IF EXISTS `ip_relationships`;
CREATE TABLE `ip_relationships` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `parent_id` bigint(20) NOT NULL,
  `child_id` bigint(20) NOT NULL,
  `relation_type_id` bigint(20) NOT NULL DEFAULT '1' COMMENT 'Foreign key to the table ip_relationship_type, by default the value is 1 (''Contributes to''  type)',
  PRIMARY KEY (`id`),
  UNIQUE KEY `KEY_unique_index` (`parent_id`,`child_id`) USING BTREE,
  KEY `FK_element_relations_child_idx` (`parent_id`) USING BTREE,
  KEY `test_idx` (`child_id`) USING BTREE,
  KEY `FK_element_relations_relationship_types_idx` (`relation_type_id`) USING BTREE,
  CONSTRAINT `ip_relationships_ibfk_1` FOREIGN KEY (`parent_id`) REFERENCES `ip_elements` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `ip_relationships_ibfk_2` FOREIGN KEY (`child_id`) REFERENCES `ip_elements` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `ip_relationships_ibfk_3` FOREIGN KEY (`relation_type_id`) REFERENCES `ip_relationship_type` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB AUTO_INCREMENT=2831 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of ip_relationships
-- ----------------------------
INSERT INTO `ip_relationships` VALUES ('95', '38', '40', '1');
INSERT INTO `ip_relationships` VALUES ('96', '38', '41', '1');
INSERT INTO `ip_relationships` VALUES ('97', '39', '42', '1');
INSERT INTO `ip_relationships` VALUES ('116', '1', '43', '1');
INSERT INTO `ip_relationships` VALUES ('118', '2', '43', '1');
INSERT INTO `ip_relationships` VALUES ('120', '5', '43', '1');
INSERT INTO `ip_relationships` VALUES ('122', '6', '43', '1');
INSERT INTO `ip_relationships` VALUES ('124', '7', '43', '1');
INSERT INTO `ip_relationships` VALUES ('144', '5', '37', '1');
INSERT INTO `ip_relationships` VALUES ('146', '6', '37', '1');
INSERT INTO `ip_relationships` VALUES ('148', '8', '37', '1');
INSERT INTO `ip_relationships` VALUES ('150', '9', '37', '1');
INSERT INTO `ip_relationships` VALUES ('152', '11', '37', '1');
INSERT INTO `ip_relationships` VALUES ('175', '44', '46', '1');
INSERT INTO `ip_relationships` VALUES ('177', '44', '47', '1');
INSERT INTO `ip_relationships` VALUES ('179', '44', '48', '1');
INSERT INTO `ip_relationships` VALUES ('181', '44', '49', '1');
INSERT INTO `ip_relationships` VALUES ('183', '45', '50', '1');
INSERT INTO `ip_relationships` VALUES ('185', '45', '51', '1');
INSERT INTO `ip_relationships` VALUES ('612', '2', '23', '1');
INSERT INTO `ip_relationships` VALUES ('614', '5', '23', '1');
INSERT INTO `ip_relationships` VALUES ('616', '7', '23', '1');
INSERT INTO `ip_relationships` VALUES ('618', '8', '23', '1');
INSERT INTO `ip_relationships` VALUES ('1447', '106', '108', '1');
INSERT INTO `ip_relationships` VALUES ('1449', '13', '108', '2');
INSERT INTO `ip_relationships` VALUES ('1451', '14', '108', '2');
INSERT INTO `ip_relationships` VALUES ('1453', '106', '115', '1');
INSERT INTO `ip_relationships` VALUES ('1455', '44', '115', '2');
INSERT INTO `ip_relationships` VALUES ('1457', '45', '115', '2');
INSERT INTO `ip_relationships` VALUES ('1459', '106', '121', '1');
INSERT INTO `ip_relationships` VALUES ('1461', '38', '121', '2');
INSERT INTO `ip_relationships` VALUES ('1463', '39', '121', '2');
INSERT INTO `ip_relationships` VALUES ('1465', '106', '126', '1');
INSERT INTO `ip_relationships` VALUES ('1467', '24', '126', '2');
INSERT INTO `ip_relationships` VALUES ('1469', '25', '126', '2');
INSERT INTO `ip_relationships` VALUES ('1623', '86', '87', '1');
INSERT INTO `ip_relationships` VALUES ('1625', '13', '87', '2');
INSERT INTO `ip_relationships` VALUES ('1627', '14', '87', '2');
INSERT INTO `ip_relationships` VALUES ('1629', '86', '88', '1');
INSERT INTO `ip_relationships` VALUES ('1631', '14', '88', '2');
INSERT INTO `ip_relationships` VALUES ('1633', '86', '89', '1');
INSERT INTO `ip_relationships` VALUES ('1635', '44', '89', '2');
INSERT INTO `ip_relationships` VALUES ('1637', '45', '89', '2');
INSERT INTO `ip_relationships` VALUES ('1639', '86', '90', '1');
INSERT INTO `ip_relationships` VALUES ('1641', '38', '90', '2');
INSERT INTO `ip_relationships` VALUES ('1643', '39', '90', '2');
INSERT INTO `ip_relationships` VALUES ('1645', '86', '91', '1');
INSERT INTO `ip_relationships` VALUES ('1647', '24', '91', '2');
INSERT INTO `ip_relationships` VALUES ('1649', '25', '91', '2');
INSERT INTO `ip_relationships` VALUES ('1749', '37', '38', '1');
INSERT INTO `ip_relationships` VALUES ('1751', '37', '39', '1');
INSERT INTO `ip_relationships` VALUES ('1809', '52', '53', '1');
INSERT INTO `ip_relationships` VALUES ('1811', '13', '53', '2');
INSERT INTO `ip_relationships` VALUES ('1813', '14', '53', '2');
INSERT INTO `ip_relationships` VALUES ('1815', '52', '54', '1');
INSERT INTO `ip_relationships` VALUES ('1817', '44', '54', '2');
INSERT INTO `ip_relationships` VALUES ('1819', '52', '55', '1');
INSERT INTO `ip_relationships` VALUES ('1821', '24', '55', '2');
INSERT INTO `ip_relationships` VALUES ('1940', '54', '46', '1');
INSERT INTO `ip_relationships` VALUES ('1941', '46', '59', '2');
INSERT INTO `ip_relationships` VALUES ('1942', '54', '47', '1');
INSERT INTO `ip_relationships` VALUES ('1943', '47', '60', '2');
INSERT INTO `ip_relationships` VALUES ('1944', '54', '48', '1');
INSERT INTO `ip_relationships` VALUES ('1945', '48', '61', '2');
INSERT INTO `ip_relationships` VALUES ('1960', '68', '46', '1');
INSERT INTO `ip_relationships` VALUES ('1961', '46', '76', '2');
INSERT INTO `ip_relationships` VALUES ('1962', '68', '47', '1');
INSERT INTO `ip_relationships` VALUES ('1963', '47', '77', '2');
INSERT INTO `ip_relationships` VALUES ('1964', '68', '48', '1');
INSERT INTO `ip_relationships` VALUES ('1965', '48', '78', '2');
INSERT INTO `ip_relationships` VALUES ('1966', '68', '49', '1');
INSERT INTO `ip_relationships` VALUES ('1967', '49', '79', '2');
INSERT INTO `ip_relationships` VALUES ('1968', '69', '40', '1');
INSERT INTO `ip_relationships` VALUES ('1969', '40', '80', '2');
INSERT INTO `ip_relationships` VALUES ('1970', '69', '41', '1');
INSERT INTO `ip_relationships` VALUES ('1971', '41', '81', '2');
INSERT INTO `ip_relationships` VALUES ('1974', '70', '83', '1');
INSERT INTO `ip_relationships` VALUES ('1978', '70', '132', '1');
INSERT INTO `ip_relationships` VALUES ('2174', '148', '46', '1');
INSERT INTO `ip_relationships` VALUES ('2175', '46', '151', '2');
INSERT INTO `ip_relationships` VALUES ('2176', '148', '47', '1');
INSERT INTO `ip_relationships` VALUES ('2177', '47', '152', '2');
INSERT INTO `ip_relationships` VALUES ('2178', '148', '49', '1');
INSERT INTO `ip_relationships` VALUES ('2179', '49', '153', '2');
INSERT INTO `ip_relationships` VALUES ('2180', '148', '48', '1');
INSERT INTO `ip_relationships` VALUES ('2181', '48', '154', '2');
INSERT INTO `ip_relationships` VALUES ('2182', '149', '41', '1');
INSERT INTO `ip_relationships` VALUES ('2183', '41', '155', '2');
INSERT INTO `ip_relationships` VALUES ('2184', '149', '40', '1');
INSERT INTO `ip_relationships` VALUES ('2185', '40', '156', '2');
INSERT INTO `ip_relationships` VALUES ('2186', '149', '157', '1');
INSERT INTO `ip_relationships` VALUES ('2187', '41', '157', '2');
INSERT INTO `ip_relationships` VALUES ('2190', '150', '159', '1');
INSERT INTO `ip_relationships` VALUES ('2208', '115', '46', '1');
INSERT INTO `ip_relationships` VALUES ('2209', '46', '116', '2');
INSERT INTO `ip_relationships` VALUES ('2210', '115', '47', '1');
INSERT INTO `ip_relationships` VALUES ('2211', '47', '117', '2');
INSERT INTO `ip_relationships` VALUES ('2212', '115', '48', '1');
INSERT INTO `ip_relationships` VALUES ('2213', '48', '118', '2');
INSERT INTO `ip_relationships` VALUES ('2214', '115', '49', '1');
INSERT INTO `ip_relationships` VALUES ('2215', '49', '119', '2');
INSERT INTO `ip_relationships` VALUES ('2216', '115', '50', '1');
INSERT INTO `ip_relationships` VALUES ('2217', '50', '120', '2');
INSERT INTO `ip_relationships` VALUES ('2218', '121', '40', '1');
INSERT INTO `ip_relationships` VALUES ('2219', '40', '122', '2');
INSERT INTO `ip_relationships` VALUES ('2220', '121', '41', '1');
INSERT INTO `ip_relationships` VALUES ('2221', '41', '123', '2');
INSERT INTO `ip_relationships` VALUES ('2222', '121', '42', '1');
INSERT INTO `ip_relationships` VALUES ('2223', '42', '124', '2');
INSERT INTO `ip_relationships` VALUES ('2224', '121', '125', '1');
INSERT INTO `ip_relationships` VALUES ('2225', '42', '125', '2');
INSERT INTO `ip_relationships` VALUES ('2232', '126', '130', '1');
INSERT INTO `ip_relationships` VALUES ('2234', '126', '131', '1');
INSERT INTO `ip_relationships` VALUES ('2236', '89', '47', '1');
INSERT INTO `ip_relationships` VALUES ('2237', '47', '95', '2');
INSERT INTO `ip_relationships` VALUES ('2238', '89', '46', '1');
INSERT INTO `ip_relationships` VALUES ('2239', '46', '96', '2');
INSERT INTO `ip_relationships` VALUES ('2240', '89', '97', '1');
INSERT INTO `ip_relationships` VALUES ('2241', '47', '97', '2');
INSERT INTO `ip_relationships` VALUES ('2242', '90', '40', '1');
INSERT INTO `ip_relationships` VALUES ('2243', '40', '98', '2');
INSERT INTO `ip_relationships` VALUES ('2244', '90', '41', '1');
INSERT INTO `ip_relationships` VALUES ('2245', '41', '99', '2');
INSERT INTO `ip_relationships` VALUES ('2246', '90', '42', '1');
INSERT INTO `ip_relationships` VALUES ('2247', '42', '100', '2');
INSERT INTO `ip_relationships` VALUES ('2248', '90', '101', '1');
INSERT INTO `ip_relationships` VALUES ('2249', '41', '101', '2');
INSERT INTO `ip_relationships` VALUES ('2254', '91', '104', '1');
INSERT INTO `ip_relationships` VALUES ('2272', '91', '163', '1');
INSERT INTO `ip_relationships` VALUES ('2276', '12', '13', '1');
INSERT INTO `ip_relationships` VALUES ('2277', '12', '14', '1');
INSERT INTO `ip_relationships` VALUES ('2350', '1', '12', '1');
INSERT INTO `ip_relationships` VALUES ('2351', '2', '12', '1');
INSERT INTO `ip_relationships` VALUES ('2352', '5', '12', '1');
INSERT INTO `ip_relationships` VALUES ('2353', '7', '12', '1');
INSERT INTO `ip_relationships` VALUES ('2354', '8', '12', '1');
INSERT INTO `ip_relationships` VALUES ('2355', '10', '12', '1');
INSERT INTO `ip_relationships` VALUES ('2406', '43', '44', '1');
INSERT INTO `ip_relationships` VALUES ('2407', '43', '45', '1');
INSERT INTO `ip_relationships` VALUES ('2657', '24', '176', '1');
INSERT INTO `ip_relationships` VALUES ('2658', '55', '176', '1');
INSERT INTO `ip_relationships` VALUES ('2659', '70', '176', '1');
INSERT INTO `ip_relationships` VALUES ('2660', '87', '176', '1');
INSERT INTO `ip_relationships` VALUES ('2661', '91', '176', '1');
INSERT INTO `ip_relationships` VALUES ('2662', '126', '176', '1');
INSERT INTO `ip_relationships` VALUES ('2663', '150', '176', '1');
INSERT INTO `ip_relationships` VALUES ('2664', '24', '177', '1');
INSERT INTO `ip_relationships` VALUES ('2665', '54', '177', '1');
INSERT INTO `ip_relationships` VALUES ('2666', '70', '177', '1');
INSERT INTO `ip_relationships` VALUES ('2667', '91', '177', '1');
INSERT INTO `ip_relationships` VALUES ('2668', '126', '177', '1');
INSERT INTO `ip_relationships` VALUES ('2669', '25', '178', '1');
INSERT INTO `ip_relationships` VALUES ('2670', '71', '178', '1');
INSERT INTO `ip_relationships` VALUES ('2671', '150', '178', '1');
INSERT INTO `ip_relationships` VALUES ('2672', '25', '179', '1');
INSERT INTO `ip_relationships` VALUES ('2743', '23', '24', '1');
INSERT INTO `ip_relationships` VALUES ('2744', '23', '25', '1');
INSERT INTO `ip_relationships` VALUES ('2758', '65', '66', '1');
INSERT INTO `ip_relationships` VALUES ('2759', '13', '66', '2');
INSERT INTO `ip_relationships` VALUES ('2760', '65', '67', '1');
INSERT INTO `ip_relationships` VALUES ('2761', '14', '67', '2');
INSERT INTO `ip_relationships` VALUES ('2762', '65', '68', '1');
INSERT INTO `ip_relationships` VALUES ('2763', '44', '68', '2');
INSERT INTO `ip_relationships` VALUES ('2764', '65', '69', '1');
INSERT INTO `ip_relationships` VALUES ('2765', '38', '69', '2');
INSERT INTO `ip_relationships` VALUES ('2766', '65', '70', '1');
INSERT INTO `ip_relationships` VALUES ('2767', '24', '70', '2');
INSERT INTO `ip_relationships` VALUES ('2768', '65', '71', '1');
INSERT INTO `ip_relationships` VALUES ('2769', '25', '71', '2');
INSERT INTO `ip_relationships` VALUES ('2770', '13', '171', '1');
INSERT INTO `ip_relationships` VALUES ('2771', '53', '171', '1');
INSERT INTO `ip_relationships` VALUES ('2772', '66', '171', '1');
INSERT INTO `ip_relationships` VALUES ('2773', '87', '171', '1');
INSERT INTO `ip_relationships` VALUES ('2774', '108', '171', '1');
INSERT INTO `ip_relationships` VALUES ('2775', '143', '171', '1');
INSERT INTO `ip_relationships` VALUES ('2776', '13', '172', '1');
INSERT INTO `ip_relationships` VALUES ('2777', '14', '172', '1');
INSERT INTO `ip_relationships` VALUES ('2778', '53', '172', '1');
INSERT INTO `ip_relationships` VALUES ('2779', '67', '172', '1');
INSERT INTO `ip_relationships` VALUES ('2780', '87', '172', '1');
INSERT INTO `ip_relationships` VALUES ('2781', '88', '172', '1');
INSERT INTO `ip_relationships` VALUES ('2782', '108', '172', '1');
INSERT INTO `ip_relationships` VALUES ('2783', '13', '173', '1');
INSERT INTO `ip_relationships` VALUES ('2784', '53', '173', '1');
INSERT INTO `ip_relationships` VALUES ('2785', '66', '173', '1');
INSERT INTO `ip_relationships` VALUES ('2786', '88', '173', '1');
INSERT INTO `ip_relationships` VALUES ('2787', '13', '174', '1');
INSERT INTO `ip_relationships` VALUES ('2788', '67', '174', '1');
INSERT INTO `ip_relationships` VALUES ('2789', '87', '174', '1');
INSERT INTO `ip_relationships` VALUES ('2790', '108', '174', '1');
INSERT INTO `ip_relationships` VALUES ('2791', '14', '175', '1');
INSERT INTO `ip_relationships` VALUES ('2792', '88', '175', '1');
INSERT INTO `ip_relationships` VALUES ('2793', '108', '175', '1');
INSERT INTO `ip_relationships` VALUES ('2794', '143', '175', '1');
INSERT INTO `ip_relationships` VALUES ('2822', '142', '143', '1');
INSERT INTO `ip_relationships` VALUES ('2823', '13', '143', '2');
INSERT INTO `ip_relationships` VALUES ('2824', '142', '148', '1');
INSERT INTO `ip_relationships` VALUES ('2825', '44', '148', '2');
INSERT INTO `ip_relationships` VALUES ('2826', '45', '148', '2');
INSERT INTO `ip_relationships` VALUES ('2827', '142', '149', '1');
INSERT INTO `ip_relationships` VALUES ('2828', '38', '149', '2');
INSERT INTO `ip_relationships` VALUES ('2829', '142', '150', '1');
INSERT INTO `ip_relationships` VALUES ('2830', '24', '150', '2');
