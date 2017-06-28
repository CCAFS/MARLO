Delete from permissions;

INSERT INTO `permissions` VALUES ('1', '*', 'Full privileges on all the platform', '0');
INSERT INTO `permissions` VALUES ('2', 'center:{0}:*', 'Full privileges on the Research Center', '0');
INSERT INTO `permissions` VALUES ('3', 'center:{0}:area:{1}:*', 'Full privileges on the Research Area', '0');
INSERT INTO `permissions` VALUES ('4', 'center:{0}:area:{1}:program:{2}:*', 'Full privileges on the Research Program', '0');

ALTER TABLE permissions AUTO_INCREMENT = 5;