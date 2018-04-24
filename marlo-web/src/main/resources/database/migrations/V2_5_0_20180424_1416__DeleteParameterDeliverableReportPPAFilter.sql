DELETE FROM custom_parameters WHERE parameter_id=(select id from parameters where `key`="crp_report_deliverable_ppa_filter");
DELETE FROM parameters WHERE `key`="crp_report_deliverable_ppa_filter";