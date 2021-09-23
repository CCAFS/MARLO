CREATE TABLE st_action_area_outcomes (
id bigint(20) NOT NULL AUTO_INCREMENT,
action_area_id bigint(20),
outcome_statement TEXT NULL,
PRIMARY KEY (id),
CONSTRAINT `action_area_fk1` FOREIGN KEY (`action_area_id`) REFERENCES `st_action_areas` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT,
INDEX `st_action_area_outcomes_id` (`id`) USING BTREE
)ENGINE=InnoDB
DEFAULT CHARACTER SET=utf8 COLLATE=utf8_general_ci;

Insert into st_action_area_outcomes (action_area_id,outcome_statement) values (1,'Demand and scaling partners use knowledge gained from science-based assessments to implement agroecological options that are economically viable, environmentally sound and socially inclusive');

Insert into st_action_area_outcomes (action_area_id,outcome_statement) values (1,'Carbon sequestration in agrifood systems is increased and green energy is widely used');

Insert into st_action_area_outcomes (action_area_id,outcome_statement) values (1,'Water use is deliberate and efficient reflecting national priorities and regional equity');

Insert into st_action_area_outcomes (action_area_id,outcome_statement) values (1,'National and subnational stakeholders lead food, land, and water system transformation and have the means to advance livelihoods, nutrition, environment, and inclusion objectives');

Insert into st_action_area_outcomes (action_area_id,outcome_statement) values (1,'Smallholder farmers and value chain actors have increased means and skills for adapting to climate change');

Insert into st_action_area_outcomes (action_area_id,outcome_statement) values (1,'National policymakers, international organizations, and market actors rely on innovative tools for decision-making');

Insert into st_action_area_outcomes (action_area_id,outcome_statement) values (1,'National agencies, civil society networks, and private actors have incorporated gender and inclusive transformative strategies');

Insert into st_action_area_outcomes (action_area_id,outcome_statement) values (2,'Smallholder farmers and their organizations adopt resource-efficient and climate-smart technologies and practices and use digital services to enhance their capacity and skills');

Insert into st_action_area_outcomes (action_area_id,outcome_statement) values (2,'Smallholder farmers have increased capacity to cope with climate risks and extremes through diversification, access to climate information, insurance and credit products and services');

Insert into st_action_area_outcomes (action_area_id,outcome_statement) values (2,'Women, youth, and marginalized groups participate in and benefit from improved value chains, farming systems and AFS');

Insert into st_action_area_outcomes (action_area_id,outcome_statement) values (2,'Our research and scaling partners use available data, new tools, and turnkey solutions to co-create resilient and inclusive AFS');

Insert into st_action_area_outcomes (action_area_id,outcome_statement) values (2,'Public, private and finance sector invest in climate smart and more inclusive agri-business models and support services');

Insert into st_action_area_outcomes (action_area_id,outcome_statement) values (2,'National and local decision makers adopt decision support tools and design enabling policies and incentive systems based on scientific evidence');

Insert into st_action_area_outcomes (action_area_id,outcome_statement) values (3,'Researchers, breeders and other users access genebank collections where use is facilitated with more data associated with accessions');

Insert into st_action_area_outcomes (action_area_id,outcome_statement) values (3,'CGIAR & partners produce better, demand-driven, more impactful varieties defined by multidisciplinary, holistic market intelligence');

Insert into st_action_area_outcomes (action_area_id,outcome_statement) values (3,'CGIAR & partner breeding programs increase their efficiency and speed of variety development by using best practices and shared services');

Insert into st_action_area_outcomes (action_area_id,outcome_statement) values (3,'CGIAR & partner breeding programs accelerate variety development and quality by securing access and using novel, cutting-edge technologies');

Insert into st_action_area_outcomes (action_area_id,outcome_statement) values (3,'Public and private sector partners increase co-ownership and co-implementation of research and investment decisions');

Insert into st_action_area_outcomes (action_area_id,outcome_statement) values (3,'Seed sector actors increase their investments in scaling-up new varieties from CGIAR breeding pipelines');

Insert into st_action_area_outcomes (action_area_id,outcome_statement) values (3,'Farmers adopt climate-resilient, nutritious, market-demanded varieties more broadly and rapidly');