SET FOREIGN_KEY_CHECKS=0;

-- ----------------------------
-- Table structure for rep_ind_innovation_types
-- ----------------------------
DROP TABLE IF EXISTS `rep_ind_innovation_types`;
CREATE TABLE `rep_ind_innovation_types` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `name` text,
  `definition` text,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=7 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of rep_ind_innovation_types
-- ----------------------------
INSERT INTO `rep_ind_innovation_types` VALUES ('1', 'Genetic (varieties and breeds)', 'Genetic (varieties and breeds) include new and adapted varieties, cultivars, lines, and breeds. For example, innovations derived by conventional plant breeding, transgenic plant breeding, and animal breeding. Also includes more upstream genetic work like identifying genes.  It’s important to distinguish between pre-breeding lines from ‘candidate’ varieties, where program participants co-develop with or provide to partners ‘candidate’ varieties that may be released by them.  The stages of reporting (detailed below) in this category include the successful production of a new or adapted variety that has, potentially, a substantial impact (whether through tolerance, efficiency, productivity or other) as evidenced in a paper due for publication (Stage 1); which are then tested more widely (Stage 2), if successfully tested then are ready for uptake - certification and licensing taken care of as necessary (Stage 3) and are then take up and used by the next user (Stage 4). Counting varieties and breeds will be by trait by agro-ecological zone.  That is to say each unique trait in a zone can be counted as one innovation.  The assumption being that the trait will be adapted sufficiently for each zone to be considered a separate innovation.');
INSERT INTO `rep_ind_innovation_types` VALUES ('2', 'Production systems and Management practices', '\"Production Systems research includes Integrated Pest Management (including grafting), Sustainable Intensification (e.g. mechanization, small-scale irrigation, planting schedules, soil management, etc.), Livestock Management, Post-Harvest technologies or management practices for feed or food, Natural Resource Management, Vaccines and Animal Health Services. The innovation should be assessed and counted in terms of whether the research firstly identifies potential practices and system components to generate new/improved system components or management practices with the potential to be superior to current farmer practices in field testing under end-user conditions (Stage 1), which are then tested more widely (Stage 2), if successfully tested then are ready for uptake - certification and licensing taken care of as necessary (Stage 3) and are then take up and used by the next user (Stage 4).');
INSERT INTO `rep_ind_innovation_types` VALUES ('3', 'Social Science', '\"Social science includes research concerning the effectiveness of agricultural policy options (policy research); research on the socio-behavioral, socioeconomic, or sociopolitical factors that influence decision-making; research concerning economic forces affecting farmer choices, technology and management practice adoption, etc. (economic research); Research or creation of new/improved tools for market access, including financial and insurance products (market access); and nutrition research. Further examples of innovation in social science research may include evidence and recommendations that program participants provide to influence changes in policy and regulatory frameworks; or methods, decision-support tools and models to design/improve programs and projects or to develop value chains, land use planning approaches, etc; or even the development of a new curriculum or development of behavioral change models that ultimately influence the design of educational/extension programs.  The determination of a social science research innovation is around the uniqueness or value of the research finding(s) as evidenced in an article submitted for publication, working or conference paper (Stage 1). Replicating the research in other sites and/or testing it through examining causality and proxy / intermediate results through quasi-experimental or experimental (randomized trials) techniques (Stage 2). The availability of the research to next users in an accessible form represents Stage 3, while the 4th Stage is demonstrated uptake includes any support for, or adoption by, the public and/or private sectors at any point during the reporting period.');
INSERT INTO `rep_ind_innovation_types` VALUES ('4', 'Biophysical Research', 'Biophysical Research is an interdisciplinary science that applies the approaches and methods of physical sciences to study biological systems and may include computational biology, decision support tools, and geospatial analysis. The uptake and reporting pathway is similar to previous categories of research innovation; with findings from biophysical research that are considered potentially innovative outlined in a paper as the 1st Stage (excluding breeding and production systems research captured elsewhere); the wider testing of this research (Stage 2); availability for uptake (Stage 3) and actual proven uptake and application (Stage 4).');
INSERT INTO `rep_ind_innovation_types` VALUES ('5', 'Research and Communication Methodologies and Tools', 'Research and Communication Methodologies and Tools includes new or improved research and communication tools including Information Communication Technology (ICT) such as seed catalogues and nutrient content databases that are used to disseminate scientific information and research findings to the public and private sectors. Communication tools include approaches and tools that have innovations embedded within them. For example, apps or platforms that can be applied in novel or different ways or generate new types of information. This does not include all deliverables (websites, databases, videos,  guides, etc.), but pertains to products which offer special and novel methods of information sharing, training, research methodologies, and/or communication of research. The stages of reporting are as per the previous examples, so don’t require further detailing here.');

-- ----------------------------
-- Table structure for rep_ind_land_use_types
-- ----------------------------
DROP TABLE IF EXISTS `rep_ind_land_use_types`;
CREATE TABLE `rep_ind_land_use_types` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `name` text,
  `definition` text,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=10 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of rep_ind_land_use_types
-- ----------------------------
INSERT INTO `rep_ind_land_use_types` VALUES ('1', 'Cropland', 'Cropland includes annual cropping (irrigated and rainfed), perennial cropping (irrigated and rainfed), and tree and shrub cropping.');
INSERT INTO `rep_ind_land_use_types` VALUES ('2', 'Grazing land', 'Grazing land includes extensive grazing (nomandic, semi-nomadic, and ranching) and intensive grazing (cut-and-carry/zero grazing, and improved pasture).');
INSERT INTO `rep_ind_land_use_types` VALUES ('3', 'Forest/woodland', 'Forest woodland includes natural forest/woodland (selective felling, clear felling, shifting cultivation) and tree plantations.');
INSERT INTO `rep_ind_land_use_types` VALUES ('4', 'Mixed crop-grazing-trees', 'Mixed crop-grazing-trees includes agroforestry, agro-pastoralism, agro-silvopastoralism, and silvo-pastoralism.');
INSERT INTO `rep_ind_land_use_types` VALUES ('5', 'Settlements/infrastructure', 'Settlements/infrastructure includes settlements, buildings, traffic routes, and energy lines.');
INSERT INTO `rep_ind_land_use_types` VALUES ('6', 'Waterways/waterbodies/wetlands', 'Waterways/waterbodies/wetlands includes drainage lines, oceans, lakes, rivers, streams, ponds, dams, and swamps.');
INSERT INTO `rep_ind_land_use_types` VALUES ('7', 'Mines/extractive industries', 'Mines/extractive industries includes landscapes devoted to mining, drilling, and other extraction methods. ');
INSERT INTO `rep_ind_land_use_types` VALUES ('8', 'Unproductive land', 'Unproductive land includes desert, tundra, and polar landscapes; land which is neither habitable nor cultivable.');
INSERT INTO `rep_ind_land_use_types` VALUES ('9', 'Other', 'Includes any land use type not listed in other categories.');

-- ----------------------------
-- Table structure for rep_ind_organization_types
-- ----------------------------
DROP TABLE IF EXISTS `rep_ind_organization_types`;
CREATE TABLE `rep_ind_organization_types` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `name` text,
  `definition` text,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=12 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of rep_ind_organization_types
-- ----------------------------
INSERT INTO `rep_ind_organization_types` VALUES ('1', 'CGIAR', 'Includes all CGIAR centers, CRPs and platforms. Any CGIAR entity. Technically, these are a subset of Academic and Research institutions, but for CGIAR purposes, CGIAR must be tagged specifically.');
INSERT INTO `rep_ind_organization_types` VALUES ('2', 'Academic and Research', 'Includes universities, colleges or other teaching institutions, research institutes or think tanks. Also includes non-CGIAR international agricultural research centers and research networks.');
INSERT INTO `rep_ind_organization_types` VALUES ('3', 'Development organizations (NGOs, networks and regional organizations)', 'Includes large or small, formal or informal, flexible or bureaucratic non-governmental entities operating within multiple areas of public action such as development, humanitarian action, human rights, environment, etc. Includes international, national, and regional NGOs; regional networks, and organizations not funded by the government.');
INSERT INTO `rep_ind_organization_types` VALUES ('4', 'NARES/NARS (National agricultural research and extension systems or National agricultural research systems)', 'Includes organizations and institutions created and/or funded by the government as a support for the national program of agricultural development with the purpose of improving agricultural research, management, financing, and service delivery (extension services). They comprise a variety of public or private stakeholders (universities, civil society, farmers’ groups, private sector) engaged in agricultural research and which promote linkages with institutions at national, regional and international level. It is important to distinguish these from academic and research institutes in-general as funders like to know the status of NARS/NARES partnerships specifically.');
INSERT INTO `rep_ind_organization_types` VALUES ('5', 'CBOs (Community based organizations) and farmers\' groups', 'Includes not-for-profit groups representing community based interests. These are generally created by the public and represent communities by addressing issues such as human services, urban safety, affordable housing, clean water, etc. These may also include farmer groups, working collaboratively to support members’ interests and needs, such as advocacy, mediation, information sharing, advice, access to markets, and inputs.');
INSERT INTO `rep_ind_organization_types` VALUES ('6', 'Private sector', 'Includes all for-profit organizations owned and managed by private individuals or enterprises. In contrast with the public sector, the private sector usually provides goods and services only for those who pay them. Includes private sector partners in recipent, donor, and partner countries. This category also includes individual farmers.');
INSERT INTO `rep_ind_organization_types` VALUES ('7', 'Foundations and Financial Institutions', 'Includes foundations and institutions that conduct financial transactions such as loans, deposits, investments and currency exchange. These can be national, regional, or international. ');
INSERT INTO `rep_ind_organization_types` VALUES ('8', 'Government', 'Primarily refers to aid recipient governments or any government in-general. Does not include governments acting in a philanthropic capacity (such as bilateral aid agencies).');
INSERT INTO `rep_ind_organization_types` VALUES ('9', 'Bilateral and Donor governments', 'Includes donor governments and bilateral development agencies such as DFID, USAID, and IDRC.');
INSERT INTO `rep_ind_organization_types` VALUES ('10', 'Multilateral', 'Includes agencies comprised of multiple country members with the objective of providing socially and environmentally responsible investment and research. Examples include FAO, World Bank, Asian Development Bank, UNICEF, etc.');
INSERT INTO `rep_ind_organization_types` VALUES ('11', 'Other ', 'Allows users to add an \'other\' category');

-- ----------------------------
-- Table structure for rep_ind_phase_research_partnerships
-- ----------------------------
DROP TABLE IF EXISTS `rep_ind_phase_research_partnerships`;
CREATE TABLE `rep_ind_phase_research_partnerships` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `name` text,
  `definition` text,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of rep_ind_phase_research_partnerships
-- ----------------------------
INSERT INTO `rep_ind_phase_research_partnerships` VALUES ('1', 'Phase 1: Research (Discovery/Proof of Concept)', 'Partnerships focused on the discovery/proof of concept phase of research.  This includes a) the exploratory phase of participatory research, policy research and innovation platforms; b) research undertaken in controlled conditions such as laboratory, greenhouse or on-station; c) the development of research methods or tools created to accelerate research and used by other researchers.');
INSERT INTO `rep_ind_phase_research_partnerships` VALUES ('2', 'Phase 2: Piloting ', 'Partnerships focused on the piloting phase of research, i.e. when a promising technology, practice or policy is tested under \'real world\' conditions. (This may not be relevant to all types of research.)');
INSERT INTO `rep_ind_phase_research_partnerships` VALUES ('3', 'Phase 3/4: Scaling up and scaling out', 'Partnerships focused on the scaling/uptake phase, i.e when technologies, management practices or knowledge are in the process of being taken up or adopted at a wider scale. Partners may provide a wide variety of services and inputs that promote adoption. Some examples are: a) financial support b)  institutionalization into policy or legal frameworks; c) market introduction, communication or extension; d) training d) distribution or delivery e) production or multiplication of seeds or other inputs');

-- ----------------------------
-- Table structure for rep_ind_policy_investiment_types
-- ----------------------------
DROP TABLE IF EXISTS `rep_ind_policy_investiment_types`;
CREATE TABLE `rep_ind_policy_investiment_types` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `name` text,
  `definition` text,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of rep_ind_policy_investiment_types
-- ----------------------------
INSERT INTO `rep_ind_policy_investiment_types` VALUES ('1', 'Policy or Strategy', 'A policy or strategy could be a written decision or commitment to a particular course of action by an institution (policy); or a (government, NGO, private sector) high level plan outlining how a particular course of action will be carried out (strategy).');
INSERT INTO `rep_ind_policy_investiment_types` VALUES ('2', 'Legal instrument', 'Legal instruments include laws, defined as a Bill passed into law by highest elected body (Parliament, Congress or equivalent); or regulations, defined as a rule or norm adopted by government and backed up by some threat of consequences, usually negative ones in the form of penalties.');
INSERT INTO `rep_ind_policy_investiment_types` VALUES ('3', 'Budget or Investment', 'A budget or investment is an estimate of funds allocated for development.');
INSERT INTO `rep_ind_policy_investiment_types` VALUES ('4', 'Curriculum', 'Curriculum refers to the planned means and materials with which students will interact for the purpose of achieving identified educational outcomes. This can be at any level of education and target group, ranging from university degree course to farmer-field school. ');


-- ----------------------------
-- Table structure for rep_ind_regions
-- ----------------------------
DROP TABLE IF EXISTS `rep_ind_regions`;
CREATE TABLE `rep_ind_regions` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `name` text,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=18 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of rep_ind_regions
-- ----------------------------
INSERT INTO `rep_ind_regions` VALUES ('1', 'Northern Africa');
INSERT INTO `rep_ind_regions` VALUES ('2', 'Sub-Saharan Africa');
INSERT INTO `rep_ind_regions` VALUES ('3', 'Latin America and the Caribbean');
INSERT INTO `rep_ind_regions` VALUES ('4', 'Northern America');
INSERT INTO `rep_ind_regions` VALUES ('5', 'Central Asia');
INSERT INTO `rep_ind_regions` VALUES ('6', 'Eastern Asia');
INSERT INTO `rep_ind_regions` VALUES ('7', 'South-eastern Asia');
INSERT INTO `rep_ind_regions` VALUES ('8', 'Southern Asia');
INSERT INTO `rep_ind_regions` VALUES ('9', 'Western Asia');
INSERT INTO `rep_ind_regions` VALUES ('10', 'Eastern Europe');
INSERT INTO `rep_ind_regions` VALUES ('11', 'Northern Europe');
INSERT INTO `rep_ind_regions` VALUES ('12', 'Southern Europe');
INSERT INTO `rep_ind_regions` VALUES ('13', 'Western Europe');
INSERT INTO `rep_ind_regions` VALUES ('14', 'Australia and New Zealand');
INSERT INTO `rep_ind_regions` VALUES ('15', 'Melanesia');
INSERT INTO `rep_ind_regions` VALUES ('16', 'Micronesia');
INSERT INTO `rep_ind_regions` VALUES ('17', 'Polynesia');

-- ----------------------------
-- Table structure for rep_ind_stage_innovations
-- ----------------------------
DROP TABLE IF EXISTS `rep_ind_stage_innovations`;
CREATE TABLE `rep_ind_stage_innovations` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `name` text,
  `definition` text,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of rep_ind_stage_innovations
-- ----------------------------
INSERT INTO `rep_ind_stage_innovations` VALUES ('1', 'Stage 1: End of research phase (Discovery/Proof of Concept)', 'The innovation has completed the initial research phase.  For policy research, this could include the identification and testing of policy options.  For technologies and varieties, this means that the innovation has been tested under ideal or controlled conditions such as laboratory, greenhouse or confined settings (for example on-station).');
INSERT INTO `rep_ind_stage_innovations` VALUES ('2', 'Stage 2: End of piloting phase', '(not relevant for all innovations) - The innovation has completed the piloting phase.   The technology or practice has successfully completed broader testing under conditions intended to resemble those that the potential users of the new technology will encounter. The innovation has achieved a documented ‘real world’ assessment of potential performance and feasibility.  This may or may not mean that the innovation is immediately available for use.  ');
INSERT INTO `rep_ind_stage_innovations` VALUES ('3', 'Stage 3: Available for uptake', 'The innovation is ready to be taken up by next users or end users. All conditions, such as licensing, certification, and regulatory approvals have been met so that end users (e.g. farmers, service providers) are able to use and disseminate the innovation legally. If an innovation made available for uptake in a previous year has increased its geographic scope (e.g. certification and release in a new country) it can also be included here.  ');
INSERT INTO `rep_ind_stage_innovations` VALUES ('4', 'Stage 4: Uptake by next user', 'The innovation has demonstrated uptake by next users. This includes any support for, or adoption by public, private or non-governmental institutions. It does not include uptake by end users (the underlying logic here is that next users are much easier to count/evidence, and that it would be very unusual to have uptake by end users at scale without involvement of any next users).  To report stage 4 requires an outcome case study .');

-- ----------------------------
-- Table structure for rep_ind_type_activities
-- ----------------------------
DROP TABLE IF EXISTS `rep_ind_type_activities`;
CREATE TABLE `rep_ind_type_activities` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `name` text,
  `definition` text,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=10 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of rep_ind_type_activities
-- ----------------------------
INSERT INTO `rep_ind_type_activities` VALUES ('1', 'Formal training: Academic Degree', 'A PhD, Masters, or other type of student (fellowship, postdoc, BSc, etc.) For inclusion in this category, the trainee must have a formal agreement with the host institution and defined learning objectives. ');
INSERT INTO `rep_ind_type_activities` VALUES ('2', 'Formal Training: One-off training event', 'This might include a one-off multi-day workshop, seminar, or 1-2 day training event. These can be online or face-to-face. For inclusion in this category, the activity must have written training objectives.');
INSERT INTO `rep_ind_type_activities` VALUES ('3', 'Formal Training: Long training course/activity', 'This includes training events with multiple (more than two) sessions often spread out over a longer period of time. For example, a distance course or series of seminars. These can be online or face-to-face. For inclusion in this category, the activity must have written training objectives.');
INSERT INTO `rep_ind_type_activities` VALUES ('4', 'Formal Training: Research placement or training visit', 'This includes sabbaticals, interns, and researchers and related professions (e.g. comms, finance) visiting from other institutions. For inclusion in this category, the trainee must have a formal agreement with the host institution and defined learning objectives. ');
INSERT INTO `rep_ind_type_activities` VALUES ('5', 'Co-creation event', 'This includes events such as learning platforms, multi-stakeholder platforms, innovation platforms, Learning Alliances, the co-design of projects, writeshops, prototyping events, virtual meeting events, or hackathons. These activities may indirectly have some capacity development outcomes but are distinguished from previous categories by not having significant, written, capdev/training objectives. Co-Creation events are distinguished from knowledge exchanges by having defined end products, which are created jointly.');
INSERT INTO `rep_ind_type_activities` VALUES ('6', 'Knowledge exchange', 'Knowledge exchange activities might include an open house (e.g. for farmers, schools, partners, alumni, or community members), tour (lab tour, visiting partners for research, or staff capacity building event), conference, focus group activity, field event, or workshop, webinars. These activities may indirectly have some capacity development outcomes but are distinguished from previous categories by not having significant, written, capdev/training objectives. Knowledge exchanges do not normally result in a defined end product.');
INSERT INTO `rep_ind_type_activities` VALUES ('7', 'Scaling activities', 'Includes participants receiving technical assistance (e.g. extension services, farmer field schools, nutrition-related education, etc.) or direct participants in input distribution activities such as seed or fertilizer distribution activities.');
INSERT INTO `rep_ind_type_activities` VALUES ('8', 'Trials and studies', 'This includes participants in lab and field trials (including on-farm trials), and direct participants in nutrition studies, impact evaluations and other research studies (soil research, integrated pest management, etc.).');
INSERT INTO `rep_ind_type_activities` VALUES ('9', 'Other', 'CRPs should include an opportunity/text box for suggestions here. If there are many suggestions of the same type, we can create a new category. If not, we revise existing categories to incorporate suggestions.');

-- ----------------------------
-- Table structure for rep_ind_type_participants
-- ----------------------------
DROP TABLE IF EXISTS `rep_ind_type_participants`;
CREATE TABLE `rep_ind_type_participants` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `name` text,
  `definition` text,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=8 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of rep_ind_type_participants
-- ----------------------------
INSERT INTO `rep_ind_type_participants` VALUES ('1', 'Farmer/Producer', 'Includes farmers, smallholders, and agri-business producers.');
INSERT INTO `rep_ind_type_participants` VALUES ('2', 'Value chain actor', 'Includes input and output suppliers (agrodealers), aggregators, processors, storage suppliers, insurance suppliers, energy companies, etc. ');
INSERT INTO `rep_ind_type_participants` VALUES ('3', 'Policy Maker', 'Includes politicians, government representatives, representatives from institutions with policy-making capacity');
INSERT INTO `rep_ind_type_participants` VALUES ('4', 'Researcher', 'Includes both CGIAR and non-CGIAR researchers, including partner researchers and visiting researchers such as interns, MScs, PhDs, or researchers on sabbatical.');
INSERT INTO `rep_ind_type_participants` VALUES ('5', 'Consumer', 'Includes users of goods and services at the final point of consumption. Consumers are only counted here where they are active participants in CGIAR activities, not simply as consumers of CGIAR research outputs, varieties, etc.');
INSERT INTO `rep_ind_type_participants` VALUES ('6', 'Funder', 'Includes funders/donors of all types, including government representatives participating in activities as funders');
INSERT INTO `rep_ind_type_participants` VALUES ('7', 'Other', 'CRPs should include an opportunity/text box for suggestions here. If there are many suggestions of the same type, we can create a new category. If not, we revise existing categories to incorporate suggestions.');


-- ----------------------------
-- Table structure for rep_ind_gender_youth_focus_levels
-- ----------------------------
DROP TABLE IF EXISTS `rep_ind_gender_youth_focus_levels`;
CREATE TABLE `rep_ind_gender_youth_focus_levels` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `name` text,
  `definition` text,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of rep_ind_gender_youth_focus_levels
-- ----------------------------
INSERT INTO `rep_ind_gender_youth_focus_levels` VALUES ('1', '0 - Not Targeted', 'The score “not targeted” means that the activity was examined but found not to target the policy objective.');
INSERT INTO `rep_ind_gender_youth_focus_levels` VALUES ('2', '1 - Significant objective', 'Significant (secondary) policy objectives are those which, although important, were not the prime motivation for undertaking the activity.');
INSERT INTO `rep_ind_gender_youth_focus_levels` VALUES ('3', '2 - Principal objective', 'Principal (primary) policy objectives are those which can be identified as being fundamental in the design and impact of the activity and which are an explicit objective of the activity. They may be selected by answering the question “Would the activity have been undertaken without this objective?”');
INSERT INTO `rep_ind_gender_youth_focus_levels` VALUES ('4', '? - Too early to tell', 'The innovation has not reached a stage where gender/youth focus is clear.');

-- ----------------------------
-- Table structure for rep_ind_geographic_scopes
-- ----------------------------
DROP TABLE IF EXISTS `rep_ind_geographic_scopes`;
CREATE TABLE `rep_ind_geographic_scopes` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `name` text,
  `iati_name` text,
  `definition` text,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of rep_ind_geographic_scopes
-- ----------------------------
INSERT INTO `rep_ind_geographic_scopes` VALUES ('1', 'Global', 'Global', 'The activity scope is global');
INSERT INTO `rep_ind_geographic_scopes` VALUES ('2', 'Regional', 'Regional', 'The activity scope is a supranational region ');
INSERT INTO `rep_ind_geographic_scopes` VALUES ('3', 'Multi-national', 'Multi-national', 'The activity scope covers multiple countries, that don’t constitute a region');
INSERT INTO `rep_ind_geographic_scopes` VALUES ('4', 'National', 'National', 'The activity scope covers one country');
INSERT INTO `rep_ind_geographic_scopes` VALUES ('5', 'Sub-national', '', '');

