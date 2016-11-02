
UPDATE 
funding_sources fu  
set fu.center_type=1
where fu.type in (2,3);


UPDATE 
funding_sources fu  
set fu.center_type=2
where fu.type in (1);