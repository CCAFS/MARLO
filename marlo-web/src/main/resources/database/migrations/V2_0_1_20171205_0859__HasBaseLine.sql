ALTER TABLE `crp_programs`
ADD COLUMN `base_line`  tinyint(1) NULL DEFAULT 0 AFTER `modification_justification`;

UPDATE `crp_programs` SET `base_line`='1' WHERE (`id`='86');
