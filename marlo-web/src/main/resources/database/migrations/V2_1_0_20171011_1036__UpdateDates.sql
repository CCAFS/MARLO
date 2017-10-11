UPDATE `phases` SET `visible`='0' ;
UPDATE `phases` SET `visible`='1' where description='Planning' and `year`=2017 ;
UPDATE `phases` SET `visible`='1' where description='Planning' and `year`=2018 ;
UPDATE `phases` SET `visible`='1' where description='Reporting' and `year`=2017 ;



UPDATE phases set start_date=CONCAT((`year`-1),'-10-01')
where description='Planning';
UPDATE phases set end_date=CONCAT((`year`-1),'-11-30')
where description='Planning';

UPDATE phases set start_date=CONCAT((`year`+1),'-01-15')
where description='Reporting';

UPDATE phases set end_date=CONCAT((`year`+1),'-03-15')
where description='Reporting';
