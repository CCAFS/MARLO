Delete from roles;

INSERT INTO `roles` VALUES ('1', 'Administrator', 'Admin', '1');
INSERT INTO `roles` VALUES ('2', 'CIAT Program Coordinator', 'Coord', '1');
INSERT INTO `roles` VALUES ('3', 'Research Area Director', 'RAD', '1');
INSERT INTO `roles` VALUES ('4', 'Research Program Leader', 'RPL', '1');
INSERT INTO `roles` VALUES ('5', 'Guest', 'G', '1');
INSERT INTO `roles` VALUES ('6', 'Super Administrator', 'Superadmin', '1');

ALTER TABLE roles AUTO_INCREMENT = 7;