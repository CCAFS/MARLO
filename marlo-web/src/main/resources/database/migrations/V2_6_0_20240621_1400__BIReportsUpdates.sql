/* bi_reports table - changes elements */

UPDATE bi_reports
	SET embed_url='aiccra-bi-module'
	WHERE id=7;

UPDATE bi_reports
	SET embed_url='aiccra_feedback_BI_module'
	WHERE id=8;

UPDATE bi_reports
SET embed_url='aiccra_feedback_consolidation'
	WHERE id=9;

ALTER TABLE bi_reports CHANGE embed_url embed_report varchar(1000) CHARACTER SET utf8 COLLATE utf8_general_ci NULL;

DELETE FROM bi_reports WHERE id=1;
DELETE FROM bi_reports WHERE id=2;
DELETE FROM bi_reports WHERE id=3;
DELETE FROM bi_reports WHERE id=4;
DELETE FROM bi_reports WHERE id=5;
DELETE FROM bi_reports WHERE id=6;

ALTER TABLE bi_reports DROP COLUMN report_id;
ALTER TABLE bi_reports DROP COLUMN dataset_id;

/* bi_parameters table - changes elements */

INSERT INTO bi_parameters (parameter_name,parameter_value)
	VALUES ('bi_widget_url','https://bi.prms.cgiar.org/widget/main.js');

DELETE FROM bi_parameters WHERE id=1;
DELETE FROM bi_parameters WHERE id=2;
DELETE FROM bi_parameters WHERE id=3;
DELETE FROM bi_parameters WHERE id=4;
DELETE FROM bi_parameters WHERE id=5;
DELETE FROM bi_parameters WHERE id=6;