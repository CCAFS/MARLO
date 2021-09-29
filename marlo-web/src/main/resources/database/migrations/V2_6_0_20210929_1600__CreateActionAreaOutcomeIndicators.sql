CREATE TABLE st_outcome_indicators (
id bigint(20) NOT NULL AUTO_INCREMENT,
outcome_indicator_statement TEXT NULL,
PRIMARY KEY (id),
INDEX `outcome_indicator_id` (`id`) USING BTREE
)ENGINE=InnoDB
DEFAULT CHARACTER SET=utf8 COLLATE=utf8_general_ci;

insert into st_outcome_indicators (id,outcome_indicator_statement) values (1,'STi 1.1 - Number of farmers using climate smart practices disaggregated by gender');
insert into st_outcome_indicators (id,outcome_indicator_statement) values (2,'STi 1.2 - Number of farmers using agroecological practices disaggregated by gender');
insert into st_outcome_indicators (id,outcome_indicator_statement) values (3,'STi 1.3- Measurable implications of adoptions such as production, profitability, input use, product quality and associated price, environmental and health damage avoided, livelihood, employment and so forth.');
insert into st_outcome_indicators (id,outcome_indicator_statement) values (4,'STi 2.1 Diet quality score');
insert into st_outcome_indicators (id,outcome_indicator_statement) values (5,'STi 3.1 Area of land under improved mitigation plans (or area that is decreasing in net carbon emissions – more ambitious and longer term)');
insert into st_outcome_indicators (id,outcome_indicator_statement) values (6,'STi 3.2 Area under improved water use plans (or water use efficiency measures – more ambitious and longer term)');
insert into st_outcome_indicators (id,outcome_indicator_statement) values (7,'STi 3.3 Trends in measures of non-point pollution where available.');
insert into st_outcome_indicators (id,outcome_indicator_statement) values (8,'STi 4.1 Number of commodity value chain x country combinations that use tested innovations to improve efficiency, inclusion, sustainability and nutrition objectives.');
insert into st_outcome_indicators (id,outcome_indicator_statement) values (9,'STi 4.2 Gaps between farm/processor gate and consumer prices (with some measures focused on smallholder farmers if possible)');
insert into st_outcome_indicators (id,outcome_indicator_statement) values (10,'STi 4.3 Domestic market price integration, both spatial and temporal');
insert into st_outcome_indicators (id,outcome_indicator_statement) values (11,'STi 4.4 Improved international price and exchange rate transmission');
insert into st_outcome_indicators (id,outcome_indicator_statement) values (12,'STi 4.5 Trends in relative prices of healthy to unhealthy foods');
insert into st_outcome_indicators (id,outcome_indicator_statement) values (13,'STRAFSi 1.1 Number of smallholder farmers who have implemented new practices that mitigate climate change risks, disaggregated by gender and type of practice.');
insert into st_outcome_indicators (id,outcome_indicator_statement) values (14,'STRAFSi 2.1 Number of policies/ strategies/ laws/ regulations/ budgets/ investments/ curricula (and similar) at different scales that were modified in design or implementation, with evidence that the change was informed by CGIAR research');
insert into st_outcome_indicators (id,outcome_indicator_statement) values (15,'STRAFSGIi 1.1 Positive trends in the Women’s Empowerment in Agriculture Index (WEIA) at various scales including nationally');
insert into st_outcome_indicators (id,outcome_indicator_statement) values (16,'RAFSi 1.1 Number of resource-efficient and climate-smart technologies at stage IV (uptake by next user), disaggregated by type');
insert into st_outcome_indicators (id,outcome_indicator_statement) values (17,'RAFSi 2.1 Number of organizations.');
insert into st_outcome_indicators (id,outcome_indicator_statement) values (18,'RAFSi 3.1 Total amount (USD) invested in climate smart business models.');
insert into st_outcome_indicators (id,outcome_indicator_statement) values (19,'STRAFSGIi 1.2 Number of women, youth and people from marginalized groups who report input into productive decisions, ownership of assets, access to and decisions on credit, control over use of income, work balance, and visiting important locations');
insert into st_outcome_indicators (id,outcome_indicator_statement) values (20,'GIi 1.1 Number of accessions data used at various levels of the breeding pipeline (level of use: used in crosses, backcrosses, incorporated in elite germplasm)');
insert into st_outcome_indicators (id,outcome_indicator_statement) values (21,'GIi 2.1 Number of new varieties produced by breeding programs');
insert into st_outcome_indicators (id,outcome_indicator_statement) values (22,'Gii 3.1 Number of breeding programs who have attained an international performance standard');
insert into st_outcome_indicators (id,outcome_indicator_statement) values (23,'Gii 3.2 Change in the capacity of key Organizations');
insert into st_outcome_indicators (id,outcome_indicator_statement) values (24,'Gii 3.1 Number of breeding programs who have attained an international performance standard.');
insert into st_outcome_indicators (id,outcome_indicator_statement) values (25,'Gii 4.2Change in the capacity of key Organizations');
insert into st_outcome_indicators (id,outcome_indicator_statement) values (26,'Gii 5.1 Number of genetic innovations commercialized through public/private sector cooperation agreements.');
insert into st_outcome_indicators (id,outcome_indicator_statement) values (27,'Gii 5.2 Number of public/private sector cooperation agreements');
insert into st_outcome_indicators (id,outcome_indicator_statement) values (28,'Gii 6.1 Amount invested in impactful breeding pipelines.');
insert into st_outcome_indicators (id,outcome_indicator_statement) values (29,'GII 6.2 Production volumes of seed or clones by Seed system actors.');
insert into st_outcome_indicators (id,outcome_indicator_statement) values (30,'GII 7.1 Number of farmers who grow climate-smart crop varieties, disaggregated by gender.');
insert into st_outcome_indicators (id,outcome_indicator_statement) values (31,'GII 7.2 Number of farmers who grow crop varieties with increased nutritional content.');
insert into st_outcome_indicators (id,outcome_indicator_statement) values (32,'STRAFSGIi 1.3 Number of farmers who grow climate-smart crop varieties, disaggregated by gender.');
insert into st_outcome_indicators (id,outcome_indicator_statement) values (33,'STRAFSGIi 1.4 Percentage of female headed farm households that use an improved crop variety.');




CREATE TABLE st_action_area_outcome_indicators (
id bigint(20) NOT NULL AUTO_INCREMENT,
action_area_outcome_id bigint(20),
outcome_indicator_id bigint(20),
PRIMARY KEY (id),
CONSTRAINT `action_area_outcome_fk1` FOREIGN KEY (`action_area_outcome_id`) REFERENCES `st_action_area_outcomes` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT,
CONSTRAINT `outcome_indicator_fk1` FOREIGN KEY (`outcome_indicator_id`) REFERENCES `st_outcome_indicators` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT,
INDEX `action_area_outcome_indicator_id` (`id`) USING BTREE
)ENGINE=InnoDB
DEFAULT CHARACTER SET=utf8 COLLATE=utf8_general_ci;

insert into st_action_area_outcome_indicators (id,action_area_outcome_id,outcome_indicator_id) values (1,1,1);
insert into st_action_area_outcome_indicators (id,action_area_outcome_id,outcome_indicator_id) values (2,1,2);
insert into st_action_area_outcome_indicators (id,action_area_outcome_id,outcome_indicator_id) values (3,1,3);
insert into st_action_area_outcome_indicators (id,action_area_outcome_id,outcome_indicator_id) values (4,2,4);
insert into st_action_area_outcome_indicators (id,action_area_outcome_id,outcome_indicator_id) values (5,3,5);
insert into st_action_area_outcome_indicators (id,action_area_outcome_id,outcome_indicator_id) values (6,3,6);
insert into st_action_area_outcome_indicators (id,action_area_outcome_id,outcome_indicator_id) values (7,3,7);
insert into st_action_area_outcome_indicators (id,action_area_outcome_id,outcome_indicator_id) values (8,4,8);
insert into st_action_area_outcome_indicators (id,action_area_outcome_id,outcome_indicator_id) values (9,4,9);
insert into st_action_area_outcome_indicators (id,action_area_outcome_id,outcome_indicator_id) values (10,4,10);
insert into st_action_area_outcome_indicators (id,action_area_outcome_id,outcome_indicator_id) values (11,4,11);
insert into st_action_area_outcome_indicators (id,action_area_outcome_id,outcome_indicator_id) values (12,4,12);
insert into st_action_area_outcome_indicators (id,action_area_outcome_id,outcome_indicator_id) values (13,5,13);
insert into st_action_area_outcome_indicators (id,action_area_outcome_id,outcome_indicator_id) values (14,6,14);
insert into st_action_area_outcome_indicators (id,action_area_outcome_id,outcome_indicator_id) values (15,7,1);
insert into st_action_area_outcome_indicators (id,action_area_outcome_id,outcome_indicator_id) values (16,7,2);
insert into st_action_area_outcome_indicators (id,action_area_outcome_id,outcome_indicator_id) values (17,7,15);
insert into st_action_area_outcome_indicators (id,action_area_outcome_id,outcome_indicator_id) values (18,8,16);
insert into st_action_area_outcome_indicators (id,action_area_outcome_id,outcome_indicator_id) values (19,9,17);
insert into st_action_area_outcome_indicators (id,action_area_outcome_id,outcome_indicator_id) values (20,10,18);
insert into st_action_area_outcome_indicators (id,action_area_outcome_id,outcome_indicator_id) values (21,11,13);
insert into st_action_area_outcome_indicators (id,action_area_outcome_id,outcome_indicator_id) values (22,12,14);
insert into st_action_area_outcome_indicators (id,action_area_outcome_id,outcome_indicator_id) values (23,13,19);
insert into st_action_area_outcome_indicators (id,action_area_outcome_id,outcome_indicator_id) values (24,14,20);
insert into st_action_area_outcome_indicators (id,action_area_outcome_id,outcome_indicator_id) values (25,15,21);
insert into st_action_area_outcome_indicators (id,action_area_outcome_id,outcome_indicator_id) values (26,17,22);
insert into st_action_area_outcome_indicators (id,action_area_outcome_id,outcome_indicator_id) values (27,17,23);
insert into st_action_area_outcome_indicators (id,action_area_outcome_id,outcome_indicator_id) values (28,19,24);
insert into st_action_area_outcome_indicators (id,action_area_outcome_id,outcome_indicator_id) values (29,19,25);
insert into st_action_area_outcome_indicators (id,action_area_outcome_id,outcome_indicator_id) values (30,20,26);
insert into st_action_area_outcome_indicators (id,action_area_outcome_id,outcome_indicator_id) values (31,20,27);
insert into st_action_area_outcome_indicators (id,action_area_outcome_id,outcome_indicator_id) values (32,22,28);
insert into st_action_area_outcome_indicators (id,action_area_outcome_id,outcome_indicator_id) values (33,22,29);
insert into st_action_area_outcome_indicators (id,action_area_outcome_id,outcome_indicator_id) values (34,23,30);
insert into st_action_area_outcome_indicators (id,action_area_outcome_id,outcome_indicator_id) values (35,23,31);
insert into st_action_area_outcome_indicators (id,action_area_outcome_id,outcome_indicator_id) values (36,24,32);
insert into st_action_area_outcome_indicators (id,action_area_outcome_id,outcome_indicator_id) values (37,24,33);