/*
Navicat MySQL Data Transfer

Source Server         : LocalHost
Source Server Version : 50712
Source Host           : localhost:3306
Source Database       : ccafspr_marlo

Target Server Type    : MYSQL
Target Server Version : 50712
File Encoding         : 65001

Date: 2016-07-21 16:25:47
*/

SET FOREIGN_KEY_CHECKS=0;

-- ----------------------------
-- Table structure for role_permissions
-- ----------------------------
DROP TABLE IF EXISTS `role_permissions`;
CREATE TABLE `role_permissions` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `role_id` bigint(20) NOT NULL,
  `permission_id` bigint(20) NOT NULL,
  PRIMARY KEY (`id`),
  KEY `roles_permission_roles_idx` (`role_id`) USING BTREE,
  KEY `roles_permission_user_permission_idx` (`permission_id`) USING BTREE,
  CONSTRAINT `role_permissions_ibfk_1` FOREIGN KEY (`permission_id`) REFERENCES `permissions` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `role_permissions_ibfk_2` FOREIGN KEY (`role_id`) REFERENCES `roles` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=657 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of role_permissions
-- ----------------------------
INSERT INTO `role_permissions` VALUES ('1', '1', '1');
INSERT INTO `role_permissions` VALUES ('84', '2', '62');
INSERT INTO `role_permissions` VALUES ('140', '2', '67');
INSERT INTO `role_permissions` VALUES ('141', '4', '67');
INSERT INTO `role_permissions` VALUES ('142', '10', '67');
INSERT INTO `role_permissions` VALUES ('143', '2', '68');
INSERT INTO `role_permissions` VALUES ('144', '4', '68');
INSERT INTO `role_permissions` VALUES ('145', '10', '68');
INSERT INTO `role_permissions` VALUES ('146', '7', '68');
INSERT INTO `role_permissions` VALUES ('163', '11', '62');
INSERT INTO `role_permissions` VALUES ('164', '11', '67');
INSERT INTO `role_permissions` VALUES ('165', '11', '68');
INSERT INTO `role_permissions` VALUES ('181', '12', '62');
INSERT INTO `role_permissions` VALUES ('182', '12', '67');
INSERT INTO `role_permissions` VALUES ('183', '12', '68');
INSERT INTO `role_permissions` VALUES ('197', '2', '69');
INSERT INTO `role_permissions` VALUES ('199', '4', '69');
INSERT INTO `role_permissions` VALUES ('206', '7', '73');
INSERT INTO `role_permissions` VALUES ('207', '7', '70');
INSERT INTO `role_permissions` VALUES ('208', '7', '80');
INSERT INTO `role_permissions` VALUES ('209', '7', '83');
INSERT INTO `role_permissions` VALUES ('216', '9', '73');
INSERT INTO `role_permissions` VALUES ('217', '9', '70');
INSERT INTO `role_permissions` VALUES ('218', '9', '80');
INSERT INTO `role_permissions` VALUES ('219', '9', '83');
INSERT INTO `role_permissions` VALUES ('223', '2', '89');
INSERT INTO `role_permissions` VALUES ('224', '2', '90');
INSERT INTO `role_permissions` VALUES ('225', '2', '92');
INSERT INTO `role_permissions` VALUES ('229', '4', '89');
INSERT INTO `role_permissions` VALUES ('230', '4', '90');
INSERT INTO `role_permissions` VALUES ('231', '4', '92');
INSERT INTO `role_permissions` VALUES ('234', '7', '89');
INSERT INTO `role_permissions` VALUES ('235', '7', '92');
INSERT INTO `role_permissions` VALUES ('237', '9', '92');
INSERT INTO `role_permissions` VALUES ('240', '10', '91');
INSERT INTO `role_permissions` VALUES ('241', '10', '92');
INSERT INTO `role_permissions` VALUES ('246', '2', '93');
INSERT INTO `role_permissions` VALUES ('247', '4', '93');
INSERT INTO `role_permissions` VALUES ('248', '7', '93');
INSERT INTO `role_permissions` VALUES ('249', '9', '93');
INSERT INTO `role_permissions` VALUES ('269', '2', '100');
INSERT INTO `role_permissions` VALUES ('270', '4', '100');
INSERT INTO `role_permissions` VALUES ('271', '7', '100');
INSERT INTO `role_permissions` VALUES ('349', '2', '118');
INSERT INTO `role_permissions` VALUES ('350', '4', '118');
INSERT INTO `role_permissions` VALUES ('351', '7', '118');
INSERT INTO `role_permissions` VALUES ('352', '9', '118');
INSERT INTO `role_permissions` VALUES ('353', '2', '120');
INSERT INTO `role_permissions` VALUES ('354', '4', '120');
INSERT INTO `role_permissions` VALUES ('355', '7', '120');
INSERT INTO `role_permissions` VALUES ('356', '9', '120');
INSERT INTO `role_permissions` VALUES ('357', '2', '121');
INSERT INTO `role_permissions` VALUES ('358', '4', '121');
INSERT INTO `role_permissions` VALUES ('359', '7', '121');
INSERT INTO `role_permissions` VALUES ('360', '9', '121');
INSERT INTO `role_permissions` VALUES ('361', '2', '122');
INSERT INTO `role_permissions` VALUES ('362', '4', '122');
INSERT INTO `role_permissions` VALUES ('363', '7', '122');
INSERT INTO `role_permissions` VALUES ('364', '9', '122');
INSERT INTO `role_permissions` VALUES ('381', '2', '130');
INSERT INTO `role_permissions` VALUES ('382', '4', '130');
INSERT INTO `role_permissions` VALUES ('383', '7', '130');
INSERT INTO `role_permissions` VALUES ('384', '9', '130');
INSERT INTO `role_permissions` VALUES ('385', '2', '132');
INSERT INTO `role_permissions` VALUES ('386', '4', '132');
INSERT INTO `role_permissions` VALUES ('387', '7', '132');
INSERT INTO `role_permissions` VALUES ('388', '9', '132');
INSERT INTO `role_permissions` VALUES ('389', '2', '134');
INSERT INTO `role_permissions` VALUES ('390', '4', '134');
INSERT INTO `role_permissions` VALUES ('391', '7', '134');
INSERT INTO `role_permissions` VALUES ('392', '9', '134');
INSERT INTO `role_permissions` VALUES ('393', '2', '136');
INSERT INTO `role_permissions` VALUES ('394', '4', '136');
INSERT INTO `role_permissions` VALUES ('395', '7', '136');
INSERT INTO `role_permissions` VALUES ('396', '9', '136');
INSERT INTO `role_permissions` VALUES ('413', '2', '146');
INSERT INTO `role_permissions` VALUES ('414', '4', '146');
INSERT INTO `role_permissions` VALUES ('415', '7', '146');
INSERT INTO `role_permissions` VALUES ('416', '9', '146');
INSERT INTO `role_permissions` VALUES ('417', '2', '148');
INSERT INTO `role_permissions` VALUES ('418', '4', '148');
INSERT INTO `role_permissions` VALUES ('419', '7', '148');
INSERT INTO `role_permissions` VALUES ('420', '9', '148');
INSERT INTO `role_permissions` VALUES ('421', '2', '149');
INSERT INTO `role_permissions` VALUES ('422', '4', '149');
INSERT INTO `role_permissions` VALUES ('423', '7', '149');
INSERT INTO `role_permissions` VALUES ('424', '9', '149');
INSERT INTO `role_permissions` VALUES ('425', '2', '150');
INSERT INTO `role_permissions` VALUES ('426', '4', '150');
INSERT INTO `role_permissions` VALUES ('427', '7', '150');
INSERT INTO `role_permissions` VALUES ('428', '9', '150');
INSERT INTO `role_permissions` VALUES ('429', '2', '151');
INSERT INTO `role_permissions` VALUES ('430', '4', '151');
INSERT INTO `role_permissions` VALUES ('431', '7', '151');
INSERT INTO `role_permissions` VALUES ('432', '9', '151');
INSERT INTO `role_permissions` VALUES ('433', '2', '153');
INSERT INTO `role_permissions` VALUES ('434', '4', '153');
INSERT INTO `role_permissions` VALUES ('435', '7', '153');
INSERT INTO `role_permissions` VALUES ('436', '9', '153');
INSERT INTO `role_permissions` VALUES ('449', '2', '160');
INSERT INTO `role_permissions` VALUES ('450', '4', '160');
INSERT INTO `role_permissions` VALUES ('451', '7', '160');
INSERT INTO `role_permissions` VALUES ('452', '9', '160');
INSERT INTO `role_permissions` VALUES ('453', '2', '163');
INSERT INTO `role_permissions` VALUES ('454', '4', '163');
INSERT INTO `role_permissions` VALUES ('455', '7', '163');
INSERT INTO `role_permissions` VALUES ('456', '9', '163');
INSERT INTO `role_permissions` VALUES ('457', '2', '164');
INSERT INTO `role_permissions` VALUES ('458', '4', '164');
INSERT INTO `role_permissions` VALUES ('459', '7', '164');
INSERT INTO `role_permissions` VALUES ('460', '9', '164');
INSERT INTO `role_permissions` VALUES ('469', '2', '168');
INSERT INTO `role_permissions` VALUES ('470', '4', '168');
INSERT INTO `role_permissions` VALUES ('471', '7', '168');
INSERT INTO `role_permissions` VALUES ('472', '9', '168');
INSERT INTO `role_permissions` VALUES ('473', '2', '169');
INSERT INTO `role_permissions` VALUES ('474', '4', '169');
INSERT INTO `role_permissions` VALUES ('475', '7', '169');
INSERT INTO `role_permissions` VALUES ('476', '9', '169');
INSERT INTO `role_permissions` VALUES ('489', '2', '174');
INSERT INTO `role_permissions` VALUES ('490', '4', '174');
INSERT INTO `role_permissions` VALUES ('491', '7', '174');
INSERT INTO `role_permissions` VALUES ('492', '9', '174');
INSERT INTO `role_permissions` VALUES ('493', '2', '175');
INSERT INTO `role_permissions` VALUES ('494', '2', '176');
INSERT INTO `role_permissions` VALUES ('495', '4', '176');
INSERT INTO `role_permissions` VALUES ('496', '7', '176');
INSERT INTO `role_permissions` VALUES ('497', '9', '176');
INSERT INTO `role_permissions` VALUES ('502', '2', '182');
INSERT INTO `role_permissions` VALUES ('503', '4', '182');
INSERT INTO `role_permissions` VALUES ('504', '7', '182');
INSERT INTO `role_permissions` VALUES ('505', '9', '182');
INSERT INTO `role_permissions` VALUES ('506', '2', '183');
INSERT INTO `role_permissions` VALUES ('507', '4', '183');
INSERT INTO `role_permissions` VALUES ('508', '7', '183');
INSERT INTO `role_permissions` VALUES ('509', '9', '183');
INSERT INTO `role_permissions` VALUES ('510', '2', '184');
INSERT INTO `role_permissions` VALUES ('511', '4', '184');
INSERT INTO `role_permissions` VALUES ('512', '7', '184');
INSERT INTO `role_permissions` VALUES ('513', '9', '184');
INSERT INTO `role_permissions` VALUES ('546', '2', '110');
INSERT INTO `role_permissions` VALUES ('547', '4', '110');
INSERT INTO `role_permissions` VALUES ('548', '7', '195');
INSERT INTO `role_permissions` VALUES ('549', '9', '195');
INSERT INTO `role_permissions` VALUES ('550', '7', '199');
INSERT INTO `role_permissions` VALUES ('551', '9', '199');
INSERT INTO `role_permissions` VALUES ('552', '7', '200');
INSERT INTO `role_permissions` VALUES ('553', '9', '200');
INSERT INTO `role_permissions` VALUES ('554', '7', '201');
INSERT INTO `role_permissions` VALUES ('555', '9', '201');
INSERT INTO `role_permissions` VALUES ('556', '7', '202');
INSERT INTO `role_permissions` VALUES ('557', '9', '202');
INSERT INTO `role_permissions` VALUES ('558', '7', '203');
INSERT INTO `role_permissions` VALUES ('559', '9', '203');
INSERT INTO `role_permissions` VALUES ('569', '10', '52');
INSERT INTO `role_permissions` VALUES ('575', '4', '211');
INSERT INTO `role_permissions` VALUES ('576', '12', '211');
INSERT INTO `role_permissions` VALUES ('577', '11', '213');
INSERT INTO `role_permissions` VALUES ('578', '12', '213');
INSERT INTO `role_permissions` VALUES ('579', '11', '214');
INSERT INTO `role_permissions` VALUES ('580', '12', '215');
INSERT INTO `role_permissions` VALUES ('581', '11', '217');
INSERT INTO `role_permissions` VALUES ('582', '12', '217');
INSERT INTO `role_permissions` VALUES ('584', '11', '211');
INSERT INTO `role_permissions` VALUES ('591', '7', '220');
INSERT INTO `role_permissions` VALUES ('592', '11', '220');
INSERT INTO `role_permissions` VALUES ('593', '12', '220');
INSERT INTO `role_permissions` VALUES ('595', '13', '220');
INSERT INTO `role_permissions` VALUES ('596', '14', '220');
INSERT INTO `role_permissions` VALUES ('597', '11', '221');
INSERT INTO `role_permissions` VALUES ('598', '12', '221');
INSERT INTO `role_permissions` VALUES ('600', '14', '221');
INSERT INTO `role_permissions` VALUES ('601', '12', '222');
INSERT INTO `role_permissions` VALUES ('602', '14', '222');
INSERT INTO `role_permissions` VALUES ('603', '14', '223');
INSERT INTO `role_permissions` VALUES ('604', '11', '224');
INSERT INTO `role_permissions` VALUES ('605', '12', '224');
INSERT INTO `role_permissions` VALUES ('607', '14', '224');
INSERT INTO `role_permissions` VALUES ('608', '12', '225');
INSERT INTO `role_permissions` VALUES ('609', '14', '225');
INSERT INTO `role_permissions` VALUES ('610', '1', '421');
INSERT INTO `role_permissions` VALUES ('611', '17', '419');
INSERT INTO `role_permissions` VALUES ('612', '17', '420');
INSERT INTO `role_permissions` VALUES ('613', '1', '422');
INSERT INTO `role_permissions` VALUES ('614', '1', '423');
INSERT INTO `role_permissions` VALUES ('615', '1', '424');
INSERT INTO `role_permissions` VALUES ('616', '19', '1');
INSERT INTO `role_permissions` VALUES ('617', '19', '421');
INSERT INTO `role_permissions` VALUES ('618', '19', '422');
INSERT INTO `role_permissions` VALUES ('619', '19', '423');
INSERT INTO `role_permissions` VALUES ('620', '19', '424');
INSERT INTO `role_permissions` VALUES ('621', '26', '1');
INSERT INTO `role_permissions` VALUES ('622', '26', '421');
INSERT INTO `role_permissions` VALUES ('623', '26', '422');
INSERT INTO `role_permissions` VALUES ('624', '26', '423');
INSERT INTO `role_permissions` VALUES ('625', '26', '424');
INSERT INTO `role_permissions` VALUES ('627', '32', '1');
INSERT INTO `role_permissions` VALUES ('628', '32', '421');
INSERT INTO `role_permissions` VALUES ('629', '32', '422');
INSERT INTO `role_permissions` VALUES ('630', '32', '423');
INSERT INTO `role_permissions` VALUES ('631', '32', '424');
INSERT INTO `role_permissions` VALUES ('633', '12', '423');
INSERT INTO `role_permissions` VALUES ('634', '12', '424');
INSERT INTO `role_permissions` VALUES ('635', '22', '423');
INSERT INTO `role_permissions` VALUES ('636', '22', '424');
INSERT INTO `role_permissions` VALUES ('637', '29', '423');
INSERT INTO `role_permissions` VALUES ('638', '29', '424');
INSERT INTO `role_permissions` VALUES ('639', '35', '423');
INSERT INTO `role_permissions` VALUES ('640', '35', '424');
INSERT INTO `role_permissions` VALUES ('641', '14', '425');
INSERT INTO `role_permissions` VALUES ('642', '20', '425');
INSERT INTO `role_permissions` VALUES ('643', '27', '425');
INSERT INTO `role_permissions` VALUES ('644', '33', '425');
INSERT INTO `role_permissions` VALUES ('645', '12', '425');
INSERT INTO `role_permissions` VALUES ('646', '22', '425');
INSERT INTO `role_permissions` VALUES ('647', '29', '425');
INSERT INTO `role_permissions` VALUES ('648', '35', '425');
INSERT INTO `role_permissions` VALUES ('649', '37', '425');
INSERT INTO `role_permissions` VALUES ('650', '31', '425');
INSERT INTO `role_permissions` VALUES ('651', '24', '425');
INSERT INTO `role_permissions` VALUES ('652', '18', '425');
INSERT INTO `role_permissions` VALUES ('653', '11', '425');
INSERT INTO `role_permissions` VALUES ('654', '21', '425');
INSERT INTO `role_permissions` VALUES ('655', '28', '425');
INSERT INTO `role_permissions` VALUES ('656', '34', '425');
