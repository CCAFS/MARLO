CREATE TABLE st_global_targets (
id bigint(20) NOT NULL AUTO_INCREMENT,
st_impact_areas_id bigint(20),
global_target TEXT,
PRIMARY KEY (id),
CONSTRAINT `globlal_target_impact_area_fk1` FOREIGN KEY (`st_impact_areas_id`) REFERENCES `st_impact_areas` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT,
INDEX `globlal_target_impact_area_id` (`id`) USING BTREE
)ENGINE=InnoDB
DEFAULT CHARACTER SET=utf8 COLLATE=utf8_general_ci;

insert into st_global_targets(st_impact_areas_id, global_target) value (1, 'End hunger for all and enable affordable healthy diets for the 3 billion people who do not currently have access to safe and nutritious food.');
insert into st_global_targets(st_impact_areas_id, global_target) value (1, 'Reduce cases of foodborne illness (600 million annually) and zoonotic disease (1 billion annually) by one third.');
insert into st_global_targets(st_impact_areas_id, global_target) value (2, 'Lift at least 500 million people living in rural areas above the extreme poverty line of US $1.90 per day (2011 PPP).');
insert into st_global_targets(st_impact_areas_id, global_target) value (2, 'Reduce by at least half the proportion of men, women and children of all ages living in poverty in all its dimensions according to national definitions.');
insert into st_global_targets(st_impact_areas_id, global_target) value (3, 'Close the gender gap in rights to economic resources, access to ownership and control over land and natural resources for over 500 million women who work in food, land and water systems.');
insert into st_global_targets(st_impact_areas_id, global_target) value (3, 'Offer rewardable opportunities to 267 million young people who are not in employment, education or training');
insert into st_global_targets(st_impact_areas_id, global_target) value (4, 'Implement all National adaptation Plans (NAP) and Nationally Determined Contributions (NDC) to the Paris Agreement.');
insert into st_global_targets(st_impact_areas_id, global_target) value (4, 'Equip 500 million small-scale producers to be more resilient to climate shocks, with climate adaptation solutions available through national innovation systems.');
insert into st_global_targets(st_impact_areas_id, global_target) value (4, 'Turn agriculture and forest systems into a net sink for carbon by 2050, with emissions from agriculture decreasing by 1 Gt per year by 2030 and reaching a floor of 5 Gt per year by 2050');
insert into st_global_targets(st_impact_areas_id, global_target) value (5, 'Stay within planetary and regional environmental boundaries: consumptive water use in food production of less than 2500 km3 per year (with a focus on the most stressed basins), zero net deforestation, nitrogen application of 90 Tg per year (with a redistribution towards low-input farming system) and increased use efficiency; and phosphorus application of 10 Tg per year.');
insert into st_global_targets(st_impact_areas_id, global_target) value (5, 'Maintain the genetic diversity of seeds, cultivated plants and farmed and domesticated animals and their related wild species, including through soundly managed genebanks at the national, regional, and international levels.');