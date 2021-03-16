
CREATE TABLE IF NOT EXISTS st_action_areas (
 id  bigint(20) NOT NULL AUTO_INCREMENT,
 name varchar(100) DEFAULT null, 
 description text DEFAULT null,
 PRIMARY KEY (`id`)
 ) ENGINE=InnoDB
DEFAULT CHARACTER SET=utf8 COLLATE=utf8_general_ci;

CREATE TABLE IF NOT EXISTS st_impact_areas (
 id  bigint(20) NOT NULL AUTO_INCREMENT,
 name varchar(100) DEFAULT null, 
 description text DEFAULT null,
 PRIMARY KEY (`id`)
) ENGINE=InnoDB
DEFAULT CHARACTER SET=utf8 COLLATE=utf8_general_ci;

CREATE TABLE IF NOT EXISTS st_impact_areas_indicators (
 id  bigint(20) NOT NULL AUTO_INCREMENT,
 indicatorStatement varchar(100) DEFAULT null, 
 st_impact_areas_id bigint(20) null,
 PRIMARY KEY (`id`),
 CONSTRAINT `st_impact_area_fk` FOREIGN KEY (`st_impact_areas_id`) REFERENCES `st_impact_areas` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT,
 INDEX `st_impact_areas_id` (`st_impact_areas_id`) USING BTREE
) ENGINE=InnoDB
DEFAULT CHARACTER SET=utf8 COLLATE=utf8_general_ci;


INSERT INTO st_action_areas (name,description) values ('Systems Transformation','Action Area 1: Systems Transformation');
INSERT INTO st_action_areas (name,description) values ('Sustainable Production','Action Area 2: Sustainable Production');
INSERT INTO st_action_areas (name,description) values (' Genetic Gains','Action Area 3: Genetic Gains');


insert into st_impact_areas (name,description) values ('Nutrition, health and food security','End hunger for all and enable affordable healthy diets for the 3 billion people who do not currently have access to safe and nutritious food. Reduce cases of foodborne illness (600 million annually) and zoonotic disease (1 billion annually) by one third.');
insert into st_impact_areas (name,description) values ('Poverty reduction, livelihoods and jobs','Lift at least 500 million people living in rural areas above the extreme poverty line of US $1.90 perday (2011 PPP). Reduce by at least half the proportion of men, women and children of all ages living in poverty in all its dimensions according to national definitions.');
insert into st_impact_areas (name,description) values ('Gender equality, youth and social inclusion','Close the gender gap in rights to economic resources, access to ownership and control over land and natural resources for over 500 million women who work in food, land & water systems. Offer rewardable opportunities to 267 million young people who are not in employment, education or training.');
insert into st_impact_areas (name,description) values ('Climate adaptation and mitigation','Implement all National adaptation Plans (NAP) and Nationally Determined Contributions (NDC) to the Paris Agreement. Equip 500 million small-scale producers to be more resilient to climate shocks, with climate adaptation solutions available through national innovation systems. Turn agriculture and forest systems into a net sink for carbon by 2050, with emissions from agriculture decreasing by 1 Gt per year by 2030 and reaching a floor of 5 Gt per year by 2050.');
insert into st_impact_areas (name,description) values ('Environmental health and biodiversity','Stay within planetary and regional environmental boundaries: consumptive water use in agriculture of under 2500 km3 per year (with a focus on the most stressed basins), zero net deforestation, nitrogen application of 90 Tg per year (with a redistribution towards low-input farming system) and increased use efficiency; and phosphorus application of 10 Tg per year. Maintain the genetic diversity of seeds, cultivated plants and farmed and domesticated animals and their related wild species, including through soundly managed genebanks at the national, regional and international levels.');



insert into st_impact_areas_indicators (indicatorStatement, st_impact_areas_id) values ('#people benefiting from relevant CGIAR innovations',1);
insert into st_impact_areas_indicators (indicatorStatement, st_impact_areas_id) values ('#people meeting minimum dietary energy requirements',1);
insert into st_impact_areas_indicators (indicatorStatement, st_impact_areas_id) values ('#people meeting minimum micronutrient requirements',1);
insert into st_impact_areas_indicators (indicatorStatement, st_impact_areas_id) values ('#cases communicable and noncommunicable diseases',1);
insert into st_impact_areas_indicators (indicatorStatement, st_impact_areas_id) values ('#people benefiting from relevant CGIAR innovations',2);
insert into st_impact_areas_indicators (indicatorStatement, st_impact_areas_id) values ('#people assisted to exit poverty',2);
insert into st_impact_areas_indicators (indicatorStatement, st_impact_areas_id) values ('#women’s empowerment and inclusion in the agricultural sector',3);
insert into st_impact_areas_indicators (indicatorStatement, st_impact_areas_id) values ('#women benefiting from relevant CGIAR innovations',3);
insert into st_impact_areas_indicators (indicatorStatement, st_impact_areas_id) values ('#youth benefiting fromrelevant CGIAR innovations',3);
insert into st_impact_areas_indicators (indicatorStatement, st_impact_areas_id) values ('#women assisted to exit poverty',3);
insert into st_impact_areas_indicators (indicatorStatement, st_impact_areas_id) values ('#tonnes CO2 equivalent emissions',4);
insert into st_impact_areas_indicators (indicatorStatement, st_impact_areas_id) values ('#plans with evidence of implementation',4);
insert into st_impact_areas_indicators (indicatorStatement, st_impact_areas_id) values ('#$ climate adaptation investments',4);
insert into st_impact_areas_indicators (indicatorStatement, st_impact_areas_id) values ('#people benefiting from climate-adapted innovations',4);
insert into st_impact_areas_indicators (indicatorStatement, st_impact_areas_id) values ('#ha under improved management',5);
insert into st_impact_areas_indicators (indicatorStatement, st_impact_areas_id) values ('#km3 consumptive water use',5);
insert into st_impact_areas_indicators (indicatorStatement, st_impact_areas_id) values ('#ha deforestation',5);
insert into st_impact_areas_indicators (indicatorStatement, st_impact_areas_id) values ('#Tg nitrogen application',5);
insert into st_impact_areas_indicators (indicatorStatement, st_impact_areas_id) values ('#plant genetic accessions available and safely duplicated',5);

