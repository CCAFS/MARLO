ALTER TABLE `powb_crp_staffing_categories`
MODIFY COLUMN `id`  bigint(20) NOT NULL AUTO_INCREMENT FIRST ;
INSERT INTO `powb_crp_staffing_categories` ( `category`, `is_active`, `active_since`, `created_by`, `modified_by`, `modification_justification`) VALUES ('Program director & flagship leaders', '1', NULL, '3', '3', ' ');
INSERT INTO `powb_crp_staffing_categories` ( `category`, `is_active`, `active_since`, `created_by`, `modified_by`, `modification_justification`) VALUES ('Principal Investigators', '1', NULL, '3', '3', ' ');
INSERT INTO `powb_crp_staffing_categories` ( `category`, `is_active`, `active_since`, `created_by`, `modified_by`, `modification_justification`) VALUES ('Other Senior Scientists (not PIs)', '1', NULL, '3', '3', NULL);
INSERT INTO `powb_crp_staffing_categories` ( `category`, `is_active`, `active_since`, `created_by`, `modified_by`, `modification_justification`) VALUES ('Post-docs / junior scientists', '1', NULL, '3', '3', NULL);
INSERT INTO `powb_crp_staffing_categories` ( `category`, `is_active`, `active_since`, `created_by`, `modified_by`, `modification_justification`) VALUES ('Research fellows', '1', NULL, '3', '3', NULL);
INSERT INTO `powb_crp_staffing_categories` ( `category`, `is_active`, `active_since`, `created_by`, `modified_by`, `modification_justification`) VALUES ('Other science support staff', '1', NULL, '3', '3', NULL);
INSERT INTO `powb_crp_staffing_categories` ( `category`, `is_active`, `active_since`, `created_by`, `modified_by`, `modification_justification`) VALUES ('TOTAL CRP', '1', NULL, '3', '3', NULL);