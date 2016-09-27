START TRANSACTION;
INSERT INTO `permissions` VALUES ('443', 'crp:{0}:project:{1}:budgetByCoAs:canEdit', 'Can update the planning project budget section in reporting round', '1');

INSERT INTO `role_permissions` VALUES ('1796', '7', '443');
INSERT INTO `role_permissions` VALUES ('1797', '12', '443');
INSERT INTO `role_permissions` VALUES ('1798', '4', '443');
INSERT INTO `role_permissions` VALUES ('1799', '2', '443');
INSERT INTO `role_permissions` VALUES ('1800', '9', '443');

COMMIT;