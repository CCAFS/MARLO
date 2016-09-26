START TRANSACTION;

INSERT INTO `permissions` VALUES ('439', 'crp:{0}:project:{1}:budgetByPartners:gender', 'Can update W1/W2 gender %', '1');
INSERT INTO `permissions` VALUES ('440', 'crp:{0}:project:{1}:budgetByPartners:cofunded', 'Can select Project Cofunded', '1');
INSERT INTO `permissions` VALUES ('441', 'crp:{0}:project:{1}:budgetByPartners:cofundedNew', 'Can Create Project CoFunded', '1');
INSERT INTO `permissions` VALUES ('442', 'crp:{0}:project:{1}:budgetByPartners:centerFounds', 'Can update Center Found', '1');

INSERT INTO `role_permissions` VALUES ('1780', '10', '206');
INSERT INTO `role_permissions` VALUES ('1781', '7', '206');
INSERT INTO `role_permissions` VALUES ('1782', '12', '206');
INSERT INTO `role_permissions` VALUES ('1783', '4', '206');
INSERT INTO `role_permissions` VALUES ('1784', '2', '206');
INSERT INTO `role_permissions` VALUES ('1785', '10', '208');
INSERT INTO `role_permissions` VALUES ('1786', '9', '206');
INSERT INTO `role_permissions` VALUES ('1787', '7', '439');
INSERT INTO `role_permissions` VALUES ('1788', '9', '439');
INSERT INTO `role_permissions` VALUES ('1789', '7', '440');
INSERT INTO `role_permissions` VALUES ('1790', '9', '440');
INSERT INTO `role_permissions` VALUES ('1791', '4', '207');
INSERT INTO `role_permissions` VALUES ('1792', '4', '441');
INSERT INTO `role_permissions` VALUES ('1793', '12', '441');
INSERT INTO `role_permissions` VALUES ('1794', '2', '441');
INSERT INTO `role_permissions` VALUES ('1795', '7', '442');

COMMIT;