ALTER TABLE `deliverable_user_partnerships` DROP FOREIGN KEY `deliverable_user_partnerships_ibfk_4`;

ALTER TABLE `deliverable_user_partnerships` DROP FOREIGN KEY `deliverable_user_partnerships_ibfk_8`;

ALTER TABLE `deliverable_user_partnerships`
DROP COLUMN `user_id`,
DROP COLUMN `division_id`;