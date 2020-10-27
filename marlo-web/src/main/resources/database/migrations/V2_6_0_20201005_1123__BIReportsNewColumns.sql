ALTER TABLE bi_reports
ADD COLUMN report_title VARCHAR(15) AFTER report_name;



ALTER TABLE bi_reports
ADD COLUMN report_description VARCHAR(140) AFTER report_title;