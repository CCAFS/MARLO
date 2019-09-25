alter table project_expected_study_info add external_link text;
alter table project_innovation_info add external_link text;
alter table report_synthesis_cross_cutting_dimensions add   trainees_phd_female double  AFTER trainees_long_term_male;  
alter table report_synthesis_cross_cutting_dimensions add  trainees_phd_male double  AFTER trainees_phd_female;
