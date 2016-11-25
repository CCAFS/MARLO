update deliverables 
set status=2
where `year`>=2016 and `status` is null or `status`=0;