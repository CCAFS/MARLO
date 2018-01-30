update 
 funding_sources fs
inner JOIN funding_sources_info fi on fi.funding_source_id=fs.id
inner JOIN phases ph on ph.id=fi.id_phase
set fi.`status`=3
where fs.is_active=1  and ph.`year`>2017
and YEAR(fi.end_date)< ph.`year`