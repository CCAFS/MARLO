ALTER TABLE `deliverable_participants`
MODIFY COLUMN `participants`  double NULL AFTER `academic_degree`,
MODIFY COLUMN `females`  double NULL AFTER `estimate_participants`;