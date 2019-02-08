SET FOREIGN_KEY_CHECKS=0;

-- ----------------------------
-- Drop column for deliverable_info
-- ----------------------------
ALTER TABLE `deliverables_info`
DROP COLUMN `cross_cutting_gender`,
DROP COLUMN `cross_cutting_youth`,
DROP COLUMN `cross_cutting_capacity`,
DROP COLUMN `cross_cutting_climate`,
DROP COLUMN `cross_cutting_na`,
DROP COLUMN `cross_cutting_score_gender`,
DROP COLUMN `cross_cutting_score_youth`,
DROP COLUMN `cross_cutting_score_capacity`,
DROP COLUMN `cross_cutting_score_climate`;

SET FOREIGN_KEY_CHECKS=1;
