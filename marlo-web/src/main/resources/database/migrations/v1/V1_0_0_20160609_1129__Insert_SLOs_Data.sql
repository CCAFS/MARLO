-- SLOs
INSERT INTO `srf_slos` (`id`, `title`, `description`, `is_active`, `created_by`, `active_since`, `modified_by`, `modification_justification`) VALUES
(1, 'REDUCE POVERTY', 'The pathways to reducing poverty differ across contexts, reflecting the diversity of livelihood strategies pursued by the poor. Where the poor are predominately smallholder farmers, increased productivity and resilience to shocks is a key pathway to poverty reduction. Productivity increases can be achieved through conservation and effective use of genetic resources. Access to productive assets such as land and water will unlock further productive potential, especially for women. Diversifying opportunities, such as moving into higher value products like livestock, fish, vegetables, fruit or other tree crops, can increase income. Improving access to markets,  trengthening financial and other services, or local processing of produce also increase livelihood opportunities for smallholders. CGIAR  esearch recognizes that no single pathway to poverty reduction will hold across all settings. It is vital to rigorously assess the full  ange of outcomes from our work – and to learn from experience. ', '1', '1', CURRENT_TIMESTAMP, '1', 'Initial Data'),
(2, 'IMPROVE FOOD AND NUTRITION SECURITY', 'Food security encompasses the availability, access, utilization and stability of a healthy food supply. We have seen that crop and commodity improvement can dramatically increase food supply, and that better farm production practices and technology can help with affordability and food safety. For nutrition, we can introduce diverse nutrient-rich foods into farming systems and diets such as bio-fortified crops, fruits, vegetables, legumes, livestock and fish. CGIAR will harness increased expertise in nutrition through partnerships and coalitions with a wide range of partners, including leading NGOs and private-sector companies as well as government agencies. Alignment with key national, regional and international processes is critical, since the means for overcoming malnutrition depends upon a broad set of conditions. ', '1', '1', CURRENT_TIMESTAMP, '1', 'Initial Data'),
(3, 'IMPROVE NATURAL RESOURCES AND ECOSYSTEM SERVICES', 'Multi-functional agriculture can enhance benefits from ecosystem goods and services and a build more productive agricultural sector in the long run. We need agricultural systems that are diversified in ways that protect soils and water – helping to control soil erosion and improving its organic content, while also ensuring biomass for storing carbon and mitigating climate change. Natural capital must be enhanced and protected, from climate  change as well as from overexploitation and other forms of abuse. In high-risk areas, enhanced conservation of habitats and resources is needed. Finally, we will restore degraded agro-ecosystems, and more sustainably manage them for increased resilience of those ecosystems and the communities that depend on them. ', '1', '1', CURRENT_TIMESTAMP, '1', 'Initial Data');


-- Cross Cutting Issues
INSERT INTO `srf_cross_cutting_issues` (`id`, `name`, `is_active`, `created_by`, `active_since`, `modified_by`, `modification_justification`) VALUES
(1, 'CLIMATE CHANGE', '1', '1', CURRENT_TIMESTAMP, '1', 'Initial Data'),
(2, 'GENDER AND YOUTH', '1', '1', CURRENT_TIMESTAMP, '1', 'Initial Data'),
(3, 'POLICIES AND INSTITUTIONS', '1', '1', CURRENT_TIMESTAMP, '1', 'Initial Data'),
(4, 'CAPACITY DEVELOPMENT', '1', '1', CURRENT_TIMESTAMP, '1', 'Initial Data');


-- IDOs
INSERT INTO `srf_idos` (`id`, `description`, `is_cross_cutting`, `cross_cutting_issue`, `is_active`, `created_by`, `active_since`, `modified_by`, `modification_justification`) VALUES
(1, 'Increased resilience of the poor to climate change and other shocks', 0, NULL, '1', '1', CURRENT_TIMESTAMP, '1', 'Initial Data'),
(2, 'Enhanced smallholder market access', 0, NULL, '1', '1', CURRENT_TIMESTAMP, '1', 'Initial Data'),
(3, 'Increased incomes and employment', 0, NULL, '1', '1', CURRENT_TIMESTAMP, '1', 'Initial Data'),
(4, 'Increased productivity', 0, NULL, '1', '1', CURRENT_TIMESTAMP, '1', 'Initial Data'),
(5, 'Improved diets for poor and vulnerable people', 0, NULL, '1', '1', CURRENT_TIMESTAMP, '1', 'Initial Data'),
(6, 'Improved food security', 0, NULL, '1', '1', CURRENT_TIMESTAMP, '1', 'Initial Data'),
(7, 'Improved human and animal health through better agricultural practices', 0, NULL, '1', '1', CURRENT_TIMESTAMP, '1', 'Initial Data'),
(8, 'Natural capital enhanced and protected, especially from climate change', 0, NULL, '1', '1', CURRENT_TIMESTAMP, '1', 'Initial Data'),
(9, 'Enhanced benefits from ecosystem goods and services', 0, NULL, '1', '1', CURRENT_TIMESTAMP, '1', 'Initial Data'),
(10, 'More sustainably managed agroecosystems', 0, NULL, '1', '1', CURRENT_TIMESTAMP, '1', 'Initial Data'),
(11, 'Mitigation and adaptation achieved', 1, 1, '1', '1', CURRENT_TIMESTAMP, '1', 'Initial Data'),
(12, 'Equity and inclusion achieved', 1, 2, '1', '1', CURRENT_TIMESTAMP, '1', 'Initial Data'),
(13, 'Enabling environment improved', 1, 3, '1', '1', CURRENT_TIMESTAMP, '1', 'Initial Data'),
(14, 'National partners and beneficiaries enabled', 1, 4, '1', '1', CURRENT_TIMESTAMP, '1', 'Initial Data');


-- Relations SLO -> IDOs
INSERT INTO `srf_slo_idos` (`id`, `slo_id`, `ido_id`, `is_active`, `created_by`, `active_since`, `modified_by`, `modification_justification`) VALUES
(1, 1, 1, '1', '1', CURRENT_TIMESTAMP, '1', 'Initial Data'),
(2, 1, 2, '1', '1', CURRENT_TIMESTAMP, '1', 'Initial Data'),
(3, 1, 3, '1', '1', CURRENT_TIMESTAMP, '1', 'Initial Data'),
(4, 1, 4, '1', '1', CURRENT_TIMESTAMP, '1', 'Initial Data'),
(5, 2, 4, '1', '1', CURRENT_TIMESTAMP, '1', 'Initial Data'),
(6, 2, 5, '1', '1', CURRENT_TIMESTAMP, '1', 'Initial Data'),
(7, 2, 6, '1', '1', CURRENT_TIMESTAMP, '1', 'Initial Data'),
(8, 2, 7, '1', '1', CURRENT_TIMESTAMP, '1', 'Initial Data'),
(9, 3, 8, '1', '1', CURRENT_TIMESTAMP, '1', 'Initial Data'),
(10, 3, 9, '1', '1', CURRENT_TIMESTAMP, '1', 'Initial Data'),
(11, 3, 10, '1', '1', CURRENT_TIMESTAMP, '1', 'Initial Data');


-- Sub-IDOs
INSERT INTO `srf_sub_idos` (`id`, `description`, `ido_id`, `is_active`, `created_by`, `active_since`, `modified_by`, `modification_justification`) VALUES
(1, 'Increased household capacity to cope with shocks', 1, '1', '1', CURRENT_TIMESTAMP, '1', 'Initial Data'),
(2, 'Reduced smallholders production risk', 1, '1', '1', CURRENT_TIMESTAMP, '1', 'Initial Data'),
(3, 'Improved access to financial and other services', 2, '1', '1', CURRENT_TIMESTAMP, '1', 'Initial Data'),
(4, 'Reduced market barriers', 2, '1', '1', CURRENT_TIMESTAMP, '1', 'Initial Data'),
(5, 'Diversified enterprise opportunities', 3, '1', '1', CURRENT_TIMESTAMP, '1', 'Initial Data'),
(6, 'Increased livelihood opportunities', 3, '1', '1', CURRENT_TIMESTAMP, '1', 'Initial Data'),
(7, 'Increased value capture by producers', 3, '1', '1', CURRENT_TIMESTAMP, '1', 'Initial Data'),
(8, 'More efficient use of inputs', 3, '1', '1', CURRENT_TIMESTAMP, '1', 'Initial Data'),
(9, 'Reduce pre- and post-harvest losses, including those caused by climate change', 4, '1', '1', CURRENT_TIMESTAMP, '1', 'Initial Data'),
(10, 'Closed yield gaps through improved agronomic and animal husbandry practices', 4, '1', '1', CURRENT_TIMESTAMP, '1', 'Initial Data'),
(11, 'Enhanced genetic gain', 4, '1', '1', CURRENT_TIMESTAMP, '1', 'Initial Data'),
(12, 'Increased conservation and use of genetic resources', 4, '1', '1', CURRENT_TIMESTAMP, '1', 'Initial Data'),
(13, 'Increased access to productive assets, including natural resources', 4, '1', '1', CURRENT_TIMESTAMP, '1', 'Initial Data'),
(14, 'Increased availability of diverse nutrient-rich foods', 5, '1', '1', CURRENT_TIMESTAMP, '1', 'Initial Data'),
(15, 'Increased access to diverse nutrient-rich foods', 5, '1', '1', CURRENT_TIMESTAMP, '1', 'Initial Data'),
(16, 'Optimized consumption of diverse nutrient-rich foods', 5, '1', '1', CURRENT_TIMESTAMP, '1', 'Initial Data'),
(17, 'Reduced biological and chemical hazards in the food system', 6, '1', '1', CURRENT_TIMESTAMP, '1', 'Initial Data'),
(18, 'Appropriate regulatory environment for food safety', 6, '1', '1', CURRENT_TIMESTAMP, '1', 'Initial Data'),
(19, 'Improved water quality', 7, '1', '1', CURRENT_TIMESTAMP, '1', 'Initial Data'),
(20, 'Enhanced conservation of habitats and resources', 7, '1', '1', CURRENT_TIMESTAMP, '1', 'Initial Data'),
(21, 'Enhanced genetic diversity of agricultural and associated landscapes', 7, '1', '1', CURRENT_TIMESTAMP, '1', 'Initial Data'),
(22, 'Land, water and forest degradation (Including deforestation) minimized and reversed', 8, '1', '1', CURRENT_TIMESTAMP, '1', 'Initial Data'),
(23, 'Enhanced conservation of habitats and resources', 8, '1', '1', CURRENT_TIMESTAMP, '1', 'Initial Data'),
(24, 'Increased genetic diversity of agricultural and associated landscapes', 8, '1', '1', CURRENT_TIMESTAMP, '1', 'Initial Data'),
(25, 'More productive and equitable management of natural resources', 9, '1', '1', CURRENT_TIMESTAMP, '1', 'Initial Data'),
(26, 'Agricultural systems diversified and intensified in ways that protect soils and water', 9, '1', '1', CURRENT_TIMESTAMP, '1', 'Initial Data'),
(27, 'Enrichment of plant and animal biodiversity for multiple goods and services', 9, '1', '1', CURRENT_TIMESTAMP, '1', 'Initial Data'),
(28, 'Increased resilience of agro-ecosystems and communities, especially those including Reduced smallholders', 10, '1', '1', CURRENT_TIMESTAMP, '1', 'Initial Data'),
(29, 'Enhanced adaptive capacity to climate risks', 10, '1', '1', CURRENT_TIMESTAMP, '1', 'Initial Data'),
(30, 'Reduce net greenhouse gas emissions from agriculture, forests and other forms of land-use', 10, '1', '1', CURRENT_TIMESTAMP, '1', 'Initial Data'),
(31, 'Reduced net greenhouse gas emissions from agriculture, forests and other forms of land-use', 11, '1', '1', CURRENT_TIMESTAMP, '1', 'Initial Data'),
(32, 'Increased above- and below-ground biomass for carbon sequestration', 11, '1', '1', CURRENT_TIMESTAMP, '1', 'Initial Data'),
(33, 'Improved forecasting of impacts of climate change and targeted technology development', 11, '1', '1', CURRENT_TIMESTAMP, '1', 'Initial Data'),
(34, 'Enhanced capacity to deal with climactic risks and extremes', 11, '1', '1', CURRENT_TIMESTAMP, '1', 'Initial Data'),
(35, 'Enabled environment for climate resilience', 11, '1', '1', CURRENT_TIMESTAMP, '1', 'Initial Data'),
(36, 'Gender-equitable control of productive assets and resources', 12, '1', '1', CURRENT_TIMESTAMP, '1', 'Initial Data'),
(37, 'Technologies that reduce women’s labor and energy expenditure developed and disseminated', 12, '1', '1', CURRENT_TIMESTAMP, '1', 'Initial Data'),
(38, 'Improved capacity of women and young people to participate in decision-making', 12, '1', '1', CURRENT_TIMESTAMP, '1', 'Initial Data'),
(39, 'Increase capacity of beneficiaries to adopt research outputs', 13, '1', '1', CURRENT_TIMESTAMP, '1', 'Initial Data'),
(40, 'Increased capacity of partner organizations, as evidenced by rate of investments in agricultural research', 13, '1', '1', CURRENT_TIMESTAMP, '1', 'Initial Data'),
(41, 'Conducive agricultural policy environment', 13, '1', '1', CURRENT_TIMESTAMP, '1', 'Initial Data'),
(42, 'Conducive environment for managing shocks and vulnerability, as evidenced in rapid response mechanisms', 13, '1', '1', CURRENT_TIMESTAMP, '1', 'Initial Data'),
(43, 'Enhanced institutional capacity of partner research organizations', 14, '1', '1', CURRENT_TIMESTAMP, '1', 'Initial Data'),
(44, 'Enhanced individual capacity in partner research organizations through training and exchange', 14, '1', '1', CURRENT_TIMESTAMP, '1', 'Initial Data'),
(45, 'Increased capacity for innovations in partner research organizations', 14, '1', '1', CURRENT_TIMESTAMP, '1', 'Initial Data'),
(46, 'Increased capacity for innovation in partner development organizations and in poor and vulnerable communities', 14, '1', '1', CURRENT_TIMESTAMP, '1', 'Initial Data');
