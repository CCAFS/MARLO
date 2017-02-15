delete from crp_indicator_reports where year=2016;
INSERT into 
crp_indicator_reports(target,next_target,actual,support_links,deviation,liaison_institution_id,indicator_id,year,last_update)

select next_target,NULL,NULL,null,null,liaison_institution_id,indicator_id,2016,now() from crp_indicator_reports

where `year`=2015;