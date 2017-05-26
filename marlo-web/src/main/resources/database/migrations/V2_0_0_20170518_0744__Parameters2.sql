DROP TABLE crp_parameters ;
update parameters set category =2 where category is null;
update parameters set format =1 where format is null;
