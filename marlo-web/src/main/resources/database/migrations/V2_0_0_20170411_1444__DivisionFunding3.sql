ALTER TABLE `deliverables`
DROP COLUMN `division`;

ALTER TABLE `deliverable_partnerships`
ADD COLUMN `division`  varchar(1000) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL ;
