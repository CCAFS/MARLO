CREATE TABLE st_action_area_outcomes (
id bigint(20) NOT NULL AUTO_INCREMENT,
action_area_id bigint(20),
outcome_statement TEXT NULL,
PRIMARY KEY (id),
CONSTRAINT `action_area_fk1` FOREIGN KEY (`action_area_id`) REFERENCES `st_action_areas` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT,
INDEX `st_action_area_outcomes_id` (`id`) USING BTREE
)ENGINE=InnoDB
DEFAULT CHARACTER SET=utf8 COLLATE=utf8_general_ci;

insert into `st_action_area_outcomes` (`id`, `action_area_id`, `outcome_statement`) values('1','1','ST 1 - Farmers use technologies or practices that contribute to improved livelihoods, enhance environmental health and biodiversity, are apt in a context of climate change, and sustain natural resources.');
insert into `st_action_area_outcomes` (`id`, `action_area_id`, `outcome_statement`) values('2','1','ST 2 - Consumers have the information, incentives and wherewithal to choose healthy diets.');
insert into `st_action_area_outcomes` (`id`, `action_area_id`, `outcome_statement`) values('3','1','ST 3 - Governments and other actors take decisions to reduce the environmental footprint of food systems from damaging to nature positive.');
insert into `st_action_area_outcomes` (`id`, `action_area_id`, `outcome_statement`) values('4','1','ST 4 - Food system markets and value chains function more efficiently, equitably, and sustainably and lead towards healthier diets');
insert into `st_action_area_outcomes` (`id`, `action_area_id`, `outcome_statement`) values('5','1','ST & RAFS 1 - Smallholder farmers implement new practices that mitigate risks associated with extreme climate change and environmental conditions and achieve more resilient livelihoods');
insert into `st_action_area_outcomes` (`id`, `action_area_id`, `outcome_statement`) values('6','1','ST & RAFS 2 - National and local governments utilize enhanced capacity (skills, systems and culture) to assess and apply research evidence and data in policy making process');
insert into `st_action_area_outcomes` (`id`, `action_area_id`, `outcome_statement`) values('7','1','ST & RAFS & GI 1 Women and youth are empowered to be more active in decision making in food, land and water systems ');
insert into `st_action_area_outcomes` (`id`, `action_area_id`, `outcome_statement`) values('8','2','RAFS 1 - Smallholder farmers use resource-efficient and climate-smart technologies and practices to enhance their livelihoods, environmental health and biodiversity');
insert into `st_action_area_outcomes` (`id`, `action_area_id`, `outcome_statement`) values('9','2','RAFS 2 - Research and scaling organizations enhance their capabilities to develop and disseminate RAFS-related innovations');
insert into `st_action_area_outcomes` (`id`, `action_area_id`, `outcome_statement`) values('10','2','RAFS 3 - Public and private financial resources are invested to fund climate-smart business models. ');
insert into `st_action_area_outcomes` (`id`, `action_area_id`, `outcome_statement`) values('11','2','ST & RAFS 1 - Smallholder farmers implement new practices that mitigate risks associated with extreme climate change and environmental conditions and achieve more resilient livelihoods');
insert into `st_action_area_outcomes` (`id`, `action_area_id`, `outcome_statement`) values('12','2','ST & RAFS 2 - National and local governments utilize enhanced capacity (skills, systems and culture) to assess and apply research evidence and data in policy making process');
insert into `st_action_area_outcomes` (`id`, `action_area_id`, `outcome_statement`) values('13','2','ST & RAFS & GI 1 Women and youth are empowered to be more active in decision making in food, land and water systems ');
insert into `st_action_area_outcomes` (`id`, `action_area_id`, `outcome_statement`) values('14','3','GI 1 - Researchers and breeders use high-quality accessions data to efficiently access genetic resources from genebank collections operating to international performance standards');
insert into `st_action_area_outcomes` (`id`, `action_area_id`, `outcome_statement`) values('15','3','GI 2 - CGIAR & partners use high-quality market intelligence to guide the development of  new varieties to meet the needs and expectations of a wide-range of users, with special attention to marginalized groups.');
insert into `st_action_area_outcomes` (`id`, `action_area_id`, `outcome_statement`) values('17','3','GI 3 - CGIAR & partner breeding programs use state-of-the art technologies to accelerate variety development and quality.');
insert into `st_action_area_outcomes` (`id`, `action_area_id`, `outcome_statement`) values('19','3','GI 4 - CGIAR & partner breeding programs use best practices and shared services to rapidly and efficiently produce new varieties with in-demand traits.');
insert into `st_action_area_outcomes` (`id`, `action_area_id`, `outcome_statement`) values('20','3','GI 5 - Cooperation and co-investment by CGIAR, public- and private-sector seed-system actors  supports coordinated and effective research and investment in the sector');
insert into `st_action_area_outcomes` (`id`, `action_area_id`, `outcome_statement`) values('22','3','GI 6 - Seed-sector actors’ investments pipelines are profitable and effective in scaling-up new varieties from CGIAR breeding.');
insert into `st_action_area_outcomes` (`id`, `action_area_id`, `outcome_statement`) values('23','3','GI 7 - Farmers have access to and use climate-resilient, nutritious, market-demanded crop varieties.');
insert into `st_action_area_outcomes` (`id`, `action_area_id`, `outcome_statement`) values('24','3','ST & RAFS & GI 1 Women and youth are empowered to be more active in decision making in food, land and water systems ');
