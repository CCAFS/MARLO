/************************************************************
* 
****************************************/
CREATE TABLE IF NOT EXISTS `report_synthesis_policies` (
  `id` BIGINT(20) NOT NULL AUTO_INCREMENT,
  `tittle` TEXT NULL,
  `expected_study_id` BIGINT(20) NOT NULL,
  `id_phase` BIGINT(20) NOT NULL,
  PRIMARY KEY (`id`),
  INDEX `fk_report_synthesis_policies_project_expected_studies1_idx` (`expected_study_id` ASC),
  INDEX `fk_report_synthesis_policies_phases1_idx` (`id_phase` ASC),
  CONSTRAINT `fk_report_synthesis_policies_project_expected_studies1`
    FOREIGN KEY (`expected_study_id`)
    REFERENCES `project_expected_studies` (`id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `fk_report_synthesis_policies_phases1`
    FOREIGN KEY (`id_phase`)
    REFERENCES phases` (`id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB
;

insert into report_synthesis_policies(tittle, expected_study_id,id_phase)
values ('The African Union’s Comprehensive Africa Agriculture Development Programme (CAADP) biennial reporting framework and guidelines in 2017 recommended the use of the WEAI. ',2008,12);
insert into report_synthesis_policies(tittle, expected_study_id,id_phase)
values ('The African Land Policy Centre approved the Monitoring and Evaluation for Land in Africa (MELA) framework for dissemination in 12 African Countries.',2015,12);
insert into report_synthesis_policies(tittle, expected_study_id,id_phase)
values ('Joint Village Land Use Planning methodology was used by the government to increase tenure for pastoralists in Tanzania.',2033,12);
insert into report_synthesis_policies(tittle, expected_study_id,id_phase)
values ('GIZ used research results to include tenure into future land restoration programs.',2036,12);
insert into report_synthesis_policies(tittle, expected_study_id,id_phase)
values ('ASTI data contributed to a policy that prioritized higher education training for agricultural researchers in Swaziland, and was used in presentations, posters, and meeting communications to advocate for a reform of the national agricultural research system.',2095,12);
insert into report_synthesis_policies(tittle, expected_study_id,id_phase)
values ('ASTI data contributed to justify the need for the merger of several institutions to form the Kenya Agriculture and Livestock Research Organization (KALRO), for improved capacity and performance.  ',2096,12);
insert into report_synthesis_policies(tittle, expected_study_id,id_phase)
values ('Egypt’s Ministry of Social Solidarity (MOSS) used results of the impact evaluation of the Takaful and Karama program to inform changes in the eligibility criteria for the program and the decision to add messages promoting women’s empowerment. ',2171,12);
insert into report_synthesis_policies(tittle, expected_study_id,id_phase)
values ('Based on PIM’s research, BKC WeatherSys augmented their advisory application with a feature allowing farmers to take smartphone pictures to collect additional training data for picture-based advisory/insurance services. ',2173,12);
insert into report_synthesis_policies(tittle, expected_study_id,id_phase)
values ('HDFC, an Indian insurance company, is investing staff time and resources towards developing and testing picture-based insurance products as part of the partnership with IFPRI. ',2173,12);
insert into report_synthesis_policies(tittle, expected_study_id,id_phase)
values ('Investment by Brazil and Ghana in the new phase of Agricultural Mechanization Services Enterprise Centers in Ghana were based on recommendations from PIM’s research on mechanization. ',2189,12);
insert into report_synthesis_policies(tittle, expected_study_id,id_phase)
values ('Contributions from the Nigeria Strategy Support Program to the Agricultural Sector Food Security and Nutrition Strategy (2016-2025) were acknowledged by the Federal Ministry of Agriculture and Rural Development.',2190,12);
insert into report_synthesis_policies(tittle, expected_study_id,id_phase)
values ('SPEED data was used to inform World Bank loans and government investments in Burkina Faso, Malawi, Mali, Nigeria, Togo, and Zambia. ',2192,12);
insert into report_synthesis_policies(tittle, expected_study_id,id_phase)
values ('The Nepal Ministry of Agricultural Development took up suggestions on food technology and quality control and the structure of agricultural training centers. ',2192,12);
insert into report_synthesis_policies(tittle, expected_study_id,id_phase)
values ('AGRA used the results of PIM’s research on agricultural transformation to inform its strategy.',2193,12);
insert into report_synthesis_policies(tittle, expected_study_id,id_phase)
values ('IFAD used the Rural Investment and Policy Analysis (RIAPA) model to help inform investment priorities.',2193,12);
insert into report_synthesis_policies(tittle, expected_study_id,id_phase)
values ('PIM’s research on structural transformation contributed to the 2017 DFID Economic Development Strategy.  ',2193,12);
insert into report_synthesis_policies(tittle, expected_study_id,id_phase)
values ('Research on Ethiopia’s health insurance and safety net programs informed the decision by the government to closely integrate two programs (PNSP and CBHI).',2198,12);
insert into report_synthesis_policies(tittle, expected_study_id,id_phase)
values ('PIM research was used extensively in an EU report evaluating different effects of alternative agreements to support trade negotiations between the European Union and two African Regional Economic Communities.  ',2199,12);
insert into report_synthesis_policies(tittle, expected_study_id,id_phase)
values ('The Ag-incentive consortium (FAO, IFPRI, Inter-American Development Bank, OECD, World Bank) implemented the strategy to harmonize data on agricultural distortions and publish these data.  ',2201,12);
insert into report_synthesis_policies(tittle, expected_study_id,id_phase)
values ('The Collaborating for Resilience approach was used by International Land Coalition’s National Engagement Strategy (NES).',2203,12);