alter table st_action_area_outcome_indicators add action_area_id BIGINT(20);

delete from st_action_area_outcome_indicators;

alter table st_action_area_outcomes DROP FOREIGN KEY action_area_fk1;
alter table st_action_area_outcomes DROP key action_area_fk1;
alter table st_action_area_outcomes drop COLUMN action_area_id;

delete from st_action_area_outcomes;

INSERT INTO `st_action_area_outcomes` (`id`, `smocode`, `outcome_statement`) VALUES (1, 'ST 1', 'Farmers use technologies or practices that contribute to improved livelihoods, enhance environmental health and biodiversity, are apt in a context of climate change, and sustain natural resources.');
INSERT INTO `st_action_area_outcomes` (`id`, `smocode`, `outcome_statement`) VALUES (2, 'ST 2', 'Consumers have the information, incentives and wherewithal to choose healthy diets.');
INSERT INTO `st_action_area_outcomes` (`id`, `smocode`, `outcome_statement`) VALUES (3, 'ST 3', 'Governments and other actors take decisions to reduce the environmental footprint of food systems from damaging to nature positive.');
INSERT INTO `st_action_area_outcomes` (`id`, `smocode`, `outcome_statement`) VALUES (4, 'ST 4 ', 'Food system markets and value chains function more efficiently, equitably, and sustainably and lead towards healthier diets');
INSERT INTO `st_action_area_outcomes` (`id`, `smocode`, `outcome_statement`) VALUES (5, 'ST & RAFS 1', 'Smallholder farmers implement new practices that mitigate risks associated with extreme climate change and environmental conditions and achieve more resilient livelihoods');
INSERT INTO `st_action_area_outcomes` (`id`, `smocode`, `outcome_statement`) VALUES (6, 'ST & RAFS 2', 'National and local governments utilize enhanced capacity (skills, systems and culture) to assess and apply research evidence and data in policy making process');
INSERT INTO `st_action_area_outcomes` (`id`, `smocode`, `outcome_statement`) VALUES (7, 'ST & RAFS & GI 1', 'Women and youth are empowered to be more active in decision making in food, land and water systems ');
INSERT INTO `st_action_area_outcomes` (`id`, `smocode`, `outcome_statement`) VALUES (8, 'RAFS 1', 'Smallholder farmers use resource-efficient and climate-smart technologies and practices to enhance their livelihoods, environmental health and biodiversity');
INSERT INTO `st_action_area_outcomes` (`id`, `smocode`, `outcome_statement`) VALUES (9, 'RAFS 2 ', 'Research and scaling organizations enhance their capabilities to develop and disseminate RAFS-related innovations');
INSERT INTO `st_action_area_outcomes` (`id`, `smocode`, `outcome_statement`) VALUES (10, 'RAFS 3', 'Public and private financial resources are invested to fund climate-smart business models. ');
INSERT INTO `st_action_area_outcomes` (`id`, `smocode`, `outcome_statement`) VALUES (11, 'GI 1', 'Researchers and breeders use high-quality accessions data to efficiently access genetic resources from genebank collections operating to international performance standards');
INSERT INTO `st_action_area_outcomes` (`id`, `smocode`, `outcome_statement`) VALUES (12, 'GI 2', 'CGIAR & partners use high-quality market intelligence to guide the development of  new varieties to meet the needs and expectations of a wide-range of users, with special attention to marginalized groups.');
INSERT INTO `st_action_area_outcomes` (`id`, `smocode`, `outcome_statement`) VALUES (13, 'GI 3', 'CGIAR & partner breeding programs use state-of-the art technologies to accelerate variety development and quality.');
INSERT INTO `st_action_area_outcomes` (`id`, `smocode`, `outcome_statement`) VALUES (14, 'GI 4', 'CGIAR & partner breeding programs use best practices and shared services to rapidly and efficiently produce new varieties with in-demand traits.');
INSERT INTO `st_action_area_outcomes` (`id`, `smocode`, `outcome_statement`) VALUES (15, 'GI 5', 'Cooperation and co-investment by CGIAR, public- and private-sector seed-system actors  supports coordinated and effective research and investment in the sector');
INSERT INTO `st_action_area_outcomes` (`id`, `smocode`, `outcome_statement`) VALUES (16, 'GI 6', 'Seed-sector actors� investments pipelines are profitable and effective in scaling-up new varieties from CGIAR breeding.');
INSERT INTO `st_action_area_outcomes` (`id`, `smocode`, `outcome_statement`) VALUES (17, 'GI 7', 'Farmers have access to and use climate-resilient, nutritious, market-demanded crop varieties.');

alter table st_action_area_outcome_indicators ADD CONSTRAINT action_area_fk2 foreign key (action_area_id) references st_action_areas(id);

INSERT INTO `st_action_area_outcome_indicators` (`id`, `action_area_outcome_id`, `outcome_indicator_id`, `action_area_id`) VALUES (1, 1, 1, 1);
INSERT INTO `st_action_area_outcome_indicators` (`id`, `action_area_outcome_id`, `outcome_indicator_id`, `action_area_id`) VALUES (2, 1, 2, 1);
INSERT INTO `st_action_area_outcome_indicators` (`id`, `action_area_outcome_id`, `outcome_indicator_id`, `action_area_id`) VALUES (3, 1, 3, 1);
INSERT INTO `st_action_area_outcome_indicators` (`id`, `action_area_outcome_id`, `outcome_indicator_id`, `action_area_id`) VALUES (4, 2, 4, 1);
INSERT INTO `st_action_area_outcome_indicators` (`id`, `action_area_outcome_id`, `outcome_indicator_id`, `action_area_id`) VALUES (5, 3, 5, 1);
INSERT INTO `st_action_area_outcome_indicators` (`id`, `action_area_outcome_id`, `outcome_indicator_id`, `action_area_id`) VALUES (6, 3, 6, 1);
INSERT INTO `st_action_area_outcome_indicators` (`id`, `action_area_outcome_id`, `outcome_indicator_id`, `action_area_id`) VALUES (7, 3, 7, 1);
INSERT INTO `st_action_area_outcome_indicators` (`id`, `action_area_outcome_id`, `outcome_indicator_id`, `action_area_id`) VALUES (8, 4, 8, 1);
INSERT INTO `st_action_area_outcome_indicators` (`id`, `action_area_outcome_id`, `outcome_indicator_id`, `action_area_id`) VALUES (9, 4, 9, 1);
INSERT INTO `st_action_area_outcome_indicators` (`id`, `action_area_outcome_id`, `outcome_indicator_id`, `action_area_id`) VALUES (10, 4, 10, 1);
INSERT INTO `st_action_area_outcome_indicators` (`id`, `action_area_outcome_id`, `outcome_indicator_id`, `action_area_id`) VALUES (11, 4, 11, 1);
INSERT INTO `st_action_area_outcome_indicators` (`id`, `action_area_outcome_id`, `outcome_indicator_id`, `action_area_id`) VALUES (12, 4, 12, 1);
INSERT INTO `st_action_area_outcome_indicators` (`id`, `action_area_outcome_id`, `outcome_indicator_id`, `action_area_id`) VALUES (13, 5, 13, 1);
INSERT INTO `st_action_area_outcome_indicators` (`id`, `action_area_outcome_id`, `outcome_indicator_id`, `action_area_id`) VALUES (14, 6, 14, 1);
INSERT INTO `st_action_area_outcome_indicators` (`id`, `action_area_outcome_id`, `outcome_indicator_id`, `action_area_id`) VALUES (15, 7, 1, 1);
INSERT INTO `st_action_area_outcome_indicators` (`id`, `action_area_outcome_id`, `outcome_indicator_id`, `action_area_id`) VALUES (16, 7, 2, 1);
INSERT INTO `st_action_area_outcome_indicators` (`id`, `action_area_outcome_id`, `outcome_indicator_id`, `action_area_id`) VALUES (17, 7, 15, 1);
INSERT INTO `st_action_area_outcome_indicators` (`id`, `action_area_outcome_id`, `outcome_indicator_id`, `action_area_id`) VALUES (18, 8, 16, 2);
INSERT INTO `st_action_area_outcome_indicators` (`id`, `action_area_outcome_id`, `outcome_indicator_id`, `action_area_id`) VALUES (19, 9, 17, 2);
INSERT INTO `st_action_area_outcome_indicators` (`id`, `action_area_outcome_id`, `outcome_indicator_id`, `action_area_id`) VALUES (20, 10, 18, 2);
INSERT INTO `st_action_area_outcome_indicators` (`id`, `action_area_outcome_id`, `outcome_indicator_id`, `action_area_id`) VALUES (21, 5, 13, 2);
INSERT INTO `st_action_area_outcome_indicators` (`id`, `action_area_outcome_id`, `outcome_indicator_id`, `action_area_id`) VALUES (22, 6, 14, 2);
INSERT INTO `st_action_area_outcome_indicators` (`id`, `action_area_outcome_id`, `outcome_indicator_id`, `action_area_id`) VALUES (23, 7, 19, 2);
INSERT INTO `st_action_area_outcome_indicators` (`id`, `action_area_outcome_id`, `outcome_indicator_id`, `action_area_id`) VALUES (24, 11, 20, 3);
INSERT INTO `st_action_area_outcome_indicators` (`id`, `action_area_outcome_id`, `outcome_indicator_id`, `action_area_id`) VALUES (25, 12, 21, 3);
INSERT INTO `st_action_area_outcome_indicators` (`id`, `action_area_outcome_id`, `outcome_indicator_id`, `action_area_id`) VALUES (26, 13, 22, 3);
INSERT INTO `st_action_area_outcome_indicators` (`id`, `action_area_outcome_id`, `outcome_indicator_id`, `action_area_id`) VALUES (27, 13, 23, 3);
INSERT INTO `st_action_area_outcome_indicators` (`id`, `action_area_outcome_id`, `outcome_indicator_id`, `action_area_id`) VALUES (28, 14, 24, 3);
INSERT INTO `st_action_area_outcome_indicators` (`id`, `action_area_outcome_id`, `outcome_indicator_id`, `action_area_id`) VALUES (29, 14, 25, 3);
INSERT INTO `st_action_area_outcome_indicators` (`id`, `action_area_outcome_id`, `outcome_indicator_id`, `action_area_id`) VALUES (30, 15, 26, 3);
INSERT INTO `st_action_area_outcome_indicators` (`id`, `action_area_outcome_id`, `outcome_indicator_id`, `action_area_id`) VALUES (31, 15, 27, 3);
INSERT INTO `st_action_area_outcome_indicators` (`id`, `action_area_outcome_id`, `outcome_indicator_id`, `action_area_id`) VALUES (32, 16, 28, 3);
INSERT INTO `st_action_area_outcome_indicators` (`id`, `action_area_outcome_id`, `outcome_indicator_id`, `action_area_id`) VALUES (33, 16, 29, 3);
INSERT INTO `st_action_area_outcome_indicators` (`id`, `action_area_outcome_id`, `outcome_indicator_id`, `action_area_id`) VALUES (34, 17, 30, 3);
INSERT INTO `st_action_area_outcome_indicators` (`id`, `action_area_outcome_id`, `outcome_indicator_id`, `action_area_id`) VALUES (35, 17, 31, 3);
INSERT INTO `st_action_area_outcome_indicators` (`id`, `action_area_outcome_id`, `outcome_indicator_id`, `action_area_id`) VALUES (36, 7, 32, 3);
INSERT INTO `st_action_area_outcome_indicators` (`id`, `action_area_outcome_id`, `outcome_indicator_id`, `action_area_id`) VALUES (37, 7, 33, 3);
