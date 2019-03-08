/* new SMO code field on flagships*/
ALTER TABLE crp_programs
ADD COLUMN smo_code TEXT NULL AFTER id;
/*
# fish - 27
# EiB - 25
# GLDC - 28
*/
/* FISH Flagships*/
INSERT INTO crp_programs 
(smo_code, name, acronym, program_type, is_active, created_by, active_since, global_unit_id) 
VALUES ('111','Sustainable Aquaculture', 'FP1', '1', '1', '1082', sysdate(), '27');

INSERT INTO crp_programs 
(smo_code, name, acronym, program_type, is_active, created_by, active_since, global_unit_id) 
VALUES ('111','Sustaining small-scale fisheries', 'FP2', '1', '1', '1082', sysdate(), '27');

/*GLDC*/
INSERT INTO crp_programs (smo_code, name, acronym, program_type, is_active, created_by, active_since, global_unit_id) 
VALUES ('181','Priority setting & impact acceleration', 'FP1', '1', '1', '1082', sysdate(), '28');
INSERT INTO crp_programs (smo_code, name, acronym, program_type, is_active, created_by, active_since, global_unit_id) 
VALUES ('182','Transforming Agri-food systems', 'FP2', '1', '1', '1082', sysdate(), '28');
INSERT INTO crp_programs (smo_code, name, acronym, program_type, is_active, created_by, active_since, global_unit_id) 
VALUES ('183','Integrated Farm and Household Management', 'FP3', '1', '1', '1082', sysdate(), '28');
INSERT INTO crp_programs (smo_code, name, acronym, program_type, is_active, created_by, active_since, global_unit_id) 
VALUES ('184','Variety and Hybrid Development', 'FP4', '1', '1', '1082', sysdate(), '28');
INSERT INTO crp_programs (smo_code, name, acronym, program_type, is_active, created_by, active_since, global_unit_id) 
VALUES ('185','Pre-breeding and Trait Discovery', 'FP5', '1', '1', '1082', sysdate(), '28');

/*EiB*/
INSERT INTO crp_programs (smo_code, name, acronym, program_type, is_active, created_by, active_since, global_unit_id) 
VALUES ('311','Breeding Excellence', 'MDL-1', '1', '1', '1082', sysdate(), '25');
INSERT INTO crp_programs (smo_code, name, acronym, program_type, is_active, created_by, active_since, global_unit_id) 
VALUES ('312','Optimizing Breeding Scheme', 'MDL-2', '1', '1', '1082', sysdate(), '25');
INSERT INTO crp_programs (smo_code, name, acronym, program_type, is_active, created_by, active_since, global_unit_id) 
VALUES ('313','Genotyping/sequencing tools and services', 'F3', '1', '1', '1082', sysdate(), '25');
INSERT INTO crp_programs (smo_code, name, acronym, program_type, is_active, created_by, active_since, global_unit_id) 
VALUES ('314','Phenotyping tools and services', 'MDL-4', '1', '1', '1082', sysdate(), '25');
INSERT INTO crp_programs (smo_code, name, acronym, program_type, is_active, created_by, active_since, global_unit_id) 
VALUES ('315','Bioinformatics and data management tools and services', 'MDL-5', '1', '1', '1082', sysdate(), '25');


/*include all SMO codes*/
update  crp_programs set smo_code = 224 where id =84 ;
update  crp_programs set smo_code = 222 where id =85 ;
update  crp_programs set smo_code = 223 where id =86 ;
update  crp_programs set smo_code = 221 where id =87 ;
update  crp_programs set smo_code = 211 where id =93 ;
update  crp_programs set smo_code = 212 where id =94 ;
update  crp_programs set smo_code = 213 where id =95 ;
update  crp_programs set smo_code = 214 where id =96 ;
update  crp_programs set smo_code = 215 where id =97 ;
update  crp_programs set smo_code = 241 where id =98 ;
update  crp_programs set smo_code = 242 where id =99 ;
update  crp_programs set smo_code = 243 where id =100 ;
update  crp_programs set smo_code = 244 where id =101 ;
update  crp_programs set smo_code = 245 where id =102 ;
update  crp_programs set smo_code = 131 where id =103 ;
update  crp_programs set smo_code = 132 where id =104 ;
update  crp_programs set smo_code = 133 where id =105 ;
update  crp_programs set smo_code = 134 where id =106 ;
update  crp_programs set smo_code = 135 where id =107 ;
update  crp_programs set smo_code = 231 where id =108 ;
update  crp_programs set smo_code = 232 where id =109 ;
update  crp_programs set smo_code = 233 where id =110 ;
update  crp_programs set smo_code = 234 where id =111 ;
update  crp_programs set smo_code = 235 where id =112 ;
update  crp_programs set smo_code = 236 where id =113 ;
update  crp_programs set smo_code = 141 where id =114 ;
update  crp_programs set smo_code = 142 where id =115 ;
update  crp_programs set smo_code = 143 where id =120 ;
update  crp_programs set smo_code = 144 where id =121 ;
update  crp_programs set smo_code = 171 where id =116 ;
update  crp_programs set smo_code = 172 where id =117 ;
update  crp_programs set smo_code = 173 where id =118 ;
update  crp_programs set smo_code = 174 where id =119 ;
update  crp_programs set smo_code = 121 where id =122 ;
update  crp_programs set smo_code = 122 where id =123 ;
update  crp_programs set smo_code = 123 where id =124 ;
update  crp_programs set smo_code = 124 where id =125 ;
update  crp_programs set smo_code = 125 where id =126 ;
update  crp_programs set smo_code = 321 where id =127 ;
update  crp_programs set smo_code = 322 where id =128 ;
update  crp_programs set smo_code = 323 where id =129 ;
update  crp_programs set smo_code = 151 where id =145 ;
update  crp_programs set smo_code = 152 where id =146 ;
update  crp_programs set smo_code = 153 where id =147 ;
update  crp_programs set smo_code = 154 where id =148 ;
update  crp_programs set smo_code = 155 where id =149 ;
update  crp_programs set smo_code = 161 where id =150 ;
update  crp_programs set smo_code = 162 where id =151 ;
update  crp_programs set smo_code = 163 where id =152 ;
update  crp_programs set smo_code = 164 where id =153 ;
update  crp_programs set smo_code = 165 where id =154 ;
update  crp_programs set smo_code = 331 where id =164 ;
update  crp_programs set smo_code = 332 where id =165 ;
update  crp_programs set smo_code = 333 where id =166 ;