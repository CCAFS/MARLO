ALTER TABLE report_configurations ADD oicr_template_data text NULL;
ALTER TABLE report_configurations DROP COLUMN name;
ALTER TABLE report_configurations DROP COLUMN description;
ALTER TABLE report_configurations DROP COLUMN value;