INSERT INTO aiccradb2.report_configurations (name,description)
  VALUES ('OICRs_reportName','OICRs filename for report microservice');
UPDATE aiccradb2.report_configurations
  SET name='OICRs_templateData'
  WHERE id=1;