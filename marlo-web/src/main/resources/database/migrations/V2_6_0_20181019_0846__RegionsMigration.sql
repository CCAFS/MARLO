/* *************************************************************************************
** Regions migration from WB to UN
** 
**************************************************************************************/   

/*Activate all the UN Regions and inactivate teh WB Regions */
update loc_elements set is_active = 0 where element_type_id = 1;

UPDATE loc_elements SET REP_IND_REGIONs_ID =19 , is_active =1 where id = 700;
UPDATE loc_elements SET REP_IND_REGIONs_ID =1, is_active =1 where id = 701;
UPDATE loc_elements SET REP_IND_REGIONs_ID =20 , is_active =1 where id = 702;
UPDATE loc_elements SET REP_IND_REGIONs_ID =22 , is_active =1 where id = 703;
UPDATE loc_elements SET REP_IND_REGIONs_ID =24 , is_active =1 where id = 704;
UPDATE loc_elements SET REP_IND_REGIONs_ID =23 , is_active =1 where id = 705;
UPDATE loc_elements SET REP_IND_REGIONs_ID =4 , is_active =1 where id =706 ;
UPDATE loc_elements SET REP_IND_REGIONs_ID =5 , is_active =1 where id = 707;
UPDATE loc_elements SET REP_IND_REGIONs_ID =6 , is_active =1 where id = 708;
UPDATE loc_elements SET REP_IND_REGIONs_ID =9 , is_active =1 where id = 709;
UPDATE loc_elements SET REP_IND_REGIONs_ID =10 , is_active =1 where id = 710;
UPDATE loc_elements SET REP_IND_REGIONs_ID =11 , is_active =1 where id = 711;
UPDATE loc_elements SET REP_IND_REGIONs_ID =12 , is_active =1 where id = 712;
UPDATE loc_elements SET REP_IND_REGIONs_ID =13 , is_active =1 where id =713 ;
UPDATE loc_elements SET REP_IND_REGIONs_ID =14 , is_active =1 where id = 714;
UPDATE loc_elements SET REP_IND_REGIONs_ID =15 , is_active =1 where id = 715;   
UPDATE loc_elements SET REP_IND_REGIONs_ID =16 , is_active =1 where id = 716;   
UPDATE loc_elements SET REP_IND_REGIONs_ID =17 , is_active =1 where id =717 ;   
UPDATE loc_elements SET REP_IND_REGIONs_ID =3 , is_active =1 where id =825; 
UPDATE loc_elements SET REP_IND_REGIONs_ID =2 , is_active =1 where id = 829;   
UPDATE loc_elements SET REP_IND_REGIONs_ID =18 , is_active =1 where id =1;   
UPDATE loc_elements SET REP_IND_REGIONs_ID =21 , is_active =1 where id =2 ;
UPDATE loc_elements SET REP_IND_REGIONs_ID =8 , is_active =1 where id =3 ; 
UPDATE loc_elements SET REP_IND_REGIONs_ID =7 , is_active =1 where id = 5;
/*Create Antartica*/
   
/*Temporary homologation table*/
 CREATE TABLE tmp_homo_regions (
  un_region BIGINT(20) NOT NULL,
  wb_region BIGINT(20) NULL,
  un_name text NULL,
  wb_name text NULL,
  PRIMARY KEY (un_region));
  
     
/*Region homologation between WB and UN*/
insert into tmp_homo_regions(wb_region,wb_name,un_region,un_name) values (823,'East Asia and Pacific',708,'Eastern Asia');
insert into tmp_homo_regions(wb_region,wb_name,un_region,un_name) values (823,'East Asia and Pacific',5,'South-eastern Asia');
insert into tmp_homo_regions(wb_region,wb_name,un_region,un_name) values (823,'East Asia and Pacific',714,'Australia and New Zealand');
insert into tmp_homo_regions(wb_region,wb_name,un_region,un_name) values (823,'East Asia and Pacific',715,'Melanesia');
insert into tmp_homo_regions(wb_region,wb_name,un_region,un_name) values (823,'East Asia and Pacific',716,'Micronesia');
insert into tmp_homo_regions(wb_region,wb_name,un_region,un_name) values (823,'East Asia and Pacific',717,'Polynesia');
insert into tmp_homo_regions(wb_region,wb_name,un_region,un_name) values (824,'Europe and Central Asia',707,'Central Asia');
insert into tmp_homo_regions(wb_region,wb_name,un_region,un_name) values (824,'Europe and Central Asia',710,'Eastern Europe');
insert into tmp_homo_regions(wb_region,wb_name,un_region,un_name) values (824,'Europe and Central Asia',711,'Northern Europe');
insert into tmp_homo_regions(wb_region,wb_name,un_region,un_name) values (824,'Europe and Central Asia',712,'Southern Europe');
insert into tmp_homo_regions(wb_region,wb_name,un_region,un_name) values (824,'Europe and Central Asia',713,'Western Europe');
insert into tmp_homo_regions(wb_region,wb_name,un_region,un_name) values (825,'Latin America & the Caribbean',703,'Latin America and the Caribbean / Caribbean');
insert into tmp_homo_regions(wb_region,wb_name,un_region,un_name) values (825,'Latin America & the Caribbean',705,'Latin America and the Caribbean / South America');
insert into tmp_homo_regions(wb_region,wb_name,un_region,un_name) values (825,'Latin America & the Caribbean',704,'Latin America and the Caribbean / Central America');
insert into tmp_homo_regions(wb_region,wb_name,un_region,un_name) values (826,'Middle East and North Africa',701,'Northern Africa');
insert into tmp_homo_regions(wb_region,wb_name,un_region,un_name) values (826,'Middle East and North Africa',709,'Western Asia');
insert into tmp_homo_regions(wb_region,wb_name,un_region,un_name) values (827,'North America',706,'Northern America');
insert into tmp_homo_regions(wb_region,wb_name,un_region,un_name) values (828,'South Asia',3,'Southern Asia');
insert into tmp_homo_regions(wb_region,wb_name,un_region,un_name) values (829,'Sub-Saharan Africa',1,'Sub-Saharan Africa / Eastern Africa');
insert into tmp_homo_regions(wb_region,wb_name,un_region,un_name) values (829,'Sub-Saharan Africa',700,'Sub-Saharan Africa / Middle Africa');
insert into tmp_homo_regions(wb_region,wb_name,un_region,un_name) values (829,'Sub-Saharan Africa',702,'Sub-Saharan Africa / Southern Africa');
insert into tmp_homo_regions(wb_region,wb_name,un_region,un_name) values (829,'Sub-Saharan Africa',2,'Sub-Saharan Africa / Western Africa');


/* *****  CAPDEV LOCATIONS MIGRATION *****/ 
/* Disable the actual locations*/
update capdev_locations loc 
set is_active =0, modified_by=1082, modification_justification='Regions migration'
where is_active =1  and exists (select 1 from tmp_homo_regions where wb_region = loc_element_id);

/* insert all regions related to wb regions*/
insert into capdev_locations (capdev_id,loc_element_id,is_active,active_since,created_by)
select l.capdev_id, h.un_region ,1,sysdate(),1082
from capdev_locations l 
join tmp_homo_regions h on h.wb_region = l.loc_element_id
where is_active =0 and modified_by=1082 and modification_justification='Regions migration';

/* *****  FOUNDING SOURCES LOCATIONS MIGRATION *****/ 
/* Disable the actual locations*/
update funding_source_locations loc 
set is_active =0, modified_by=1082, modification_justification='Regions migration'
where is_active =1  and exists (select 1 from tmp_homo_regions where wb_region = loc_element_id)
;

/* insert all regions related to wb regions*/
insert into funding_source_locations (funding_source_id,loc_element_id,is_active,active_since,created_by,id_phase,percentage)
select f.funding_source_id, h.un_region ,1,sysdate(),1082,f.id_phase,f.percentage
from funding_source_locations f 
join tmp_homo_regions h on h.wb_region = f.loc_element_id
where is_active =0 and modified_by=1082 and modification_justification='Regions migration'
;

/* *****  PROJECT LOCATIONS *****/ 
/* Disable the actual locations*/
update project_locations loc 
set is_active =0, modified_by=1082, modification_justification='Regions migration'
where is_active =1  and exists (select 1 from tmp_homo_regions where wb_region = loc_element_id)
;

insert into project_locations (project_id, loc_element_id,is_active,active_since,created_by,id_phase)
select f.project_id, h.un_region ,1,sysdate(),1082,f.id_phase
from project_locations f 
join tmp_homo_regions h on h.wb_region = f.loc_element_id
where is_active =0 and modified_by=1082 and modification_justification='Regions migration'
;

drop table tmp_homo_regions;