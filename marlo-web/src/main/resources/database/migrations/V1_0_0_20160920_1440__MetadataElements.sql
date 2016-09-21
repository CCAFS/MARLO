SET FOREIGN_KEY_CHECKS=0;

-- ----------------------------
-- Table structure for metadata_elements
-- ----------------------------
DROP TABLE IF EXISTS `metadata_elements`;
CREATE TABLE `metadata_elements` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `schema` varchar(20) DEFAULT NULL,
  `element` varchar(100) DEFAULT NULL,
  `qualifier` varchar(100) DEFAULT NULL,
  `econded_name` varchar(100) DEFAULT NULL,
  `status` varchar(100) DEFAULT NULL,
  `vocabulary` varchar(200) DEFAULT NULL,
  `definitation` mediumtext,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=35 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of metadata_elements
-- ----------------------------
INSERT INTO `metadata_elements` VALUES ('1', 'DC', 'Title', '', 'dc.title', 'Required', '', 'Official or unofficial title of the document, data set, image, etc.');
INSERT INTO `metadata_elements` VALUES ('2', 'DC', 'Creator', '', 'dc:creator', 'Required', '', 'Creators of the item—typically a person. Could be an organization in case of corporate authors (e.g. Center reports)');
INSERT INTO `metadata_elements` VALUES ('3', 'CG', 'Creator', 'ID', 'cg.creator.ID', 'Req. when applicable', '', 'Used if ORCID, SCOPUS, or other type of creator ID scheme is in use. Used in parallel with cg.creator.ID.type');
INSERT INTO `metadata_elements` VALUES ('4', 'CG', 'Creator', 'ID Type', 'cg.creator.ID.type', 'Req. when applicable', '', 'Used to indicate the type of Creator ID – ex: SCOPUS, ORCID, etc.');
INSERT INTO `metadata_elements` VALUES ('5', 'DC', 'Subject', '', 'dc.subject', 'Required', '', 'Subject matter of the research, technologies tested, etc.');
INSERT INTO `metadata_elements` VALUES ('6', 'CG', 'Subject', 'AGROVOC', 'cg.subject.agrovoc', 'Optional', 'AGROVOC', 'AGROVOC subject matter or research area');
INSERT INTO `metadata_elements` VALUES ('7', 'CG', 'Subject', 'Domain Specific', 'cg.subject.domain-specific', 'Optional', 'Domain-specific (ex: MeSH)', 'Subject matter or research area from domain-specific vocabularies, if missing from AGROVOC');
INSERT INTO `metadata_elements` VALUES ('8', 'DC', 'Description', '', 'dc.description.abstract', 'Optional', '', 'Abstract or other description of the item');
INSERT INTO `metadata_elements` VALUES ('9', 'DC', 'Publisher', '', 'dc.publisher', 'Req. when applicable', '', 'Entity responsible for publication, distribution, or imprint');
INSERT INTO `metadata_elements` VALUES ('10', 'DC', 'Contributor', '', 'dc.contributor', 'Required', '', 'Person, organization, or service making contributions to resource content; CGIAR affiliation');
INSERT INTO `metadata_elements` VALUES ('11', 'CG', 'Contributor', 'Center', 'cg.contributor.center', 'Required', 'CGIAR', 'Research Centers and offices with which creator(s) are affiliated');
INSERT INTO `metadata_elements` VALUES ('12', 'CG', 'Contributor', 'CRP', 'cg.contributor.crp', 'Required', 'CGIAR', 'CGIAR Research Program with which the research is affiliated');
INSERT INTO `metadata_elements` VALUES ('13', 'CG', 'Contributor', 'Funder', 'cg.contributor.funder', 'Required', 'CGIAR', 'Funder, funding agency or sponsor');
INSERT INTO `metadata_elements` VALUES ('14', 'CG', 'Contributor', 'Partner', 'cg.contributor.partnerId', 'Required', 'CGIAR', 'Partners, funding agencies, other CGIAR centers');
INSERT INTO `metadata_elements` VALUES ('15', 'CG', 'Contributor', 'Project', 'cg.contributor.project', 'Required', 'CGIAR', 'Name of project with which the research is affiliated');
INSERT INTO `metadata_elements` VALUES ('16', 'CG', 'Contributor', 'Project Lead Institution', 'cg.contributor.project-lead-institute', 'Req. when applicable', 'CGIAR', 'The lead institution for the project (CGIAR or otherwise) connected to the research output being described');
INSERT INTO `metadata_elements` VALUES ('17', 'DC', 'Date', '', 'dc.date', 'Required', '', 'Publication or creation date');
INSERT INTO `metadata_elements` VALUES ('18', 'CG', 'Date', 'Embargo End Date', 'cg:date.embargo-end-date', 'Req. when applicable', '', 'Used when an item has an embargo by publisher (ex: 6 or 12-month embargo)');
INSERT INTO `metadata_elements` VALUES ('19', 'DC', 'Type', '', 'dc.type', 'Required', 'CGIAR', 'Nature or genre of item/content; e.g., poster, data set');
INSERT INTO `metadata_elements` VALUES ('20', 'DC', 'Format', '', 'dc.format', 'Required', 'CGIAR', 'File format of item e.g.: PDF; jpg etc.');
INSERT INTO `metadata_elements` VALUES ('21', 'DC', 'Identifier', '', 'dc.identifier', 'Required', '', 'Unambiguous reference to resource such as doi, uri');
INSERT INTO `metadata_elements` VALUES ('22', 'DC', 'Identifier', 'Citation', 'dc.identifier.citation', 'Optional', '', 'Human-readable, standard bibliographic citation for the item');
INSERT INTO `metadata_elements` VALUES ('23', 'DC', 'Source', '', 'dc.source', 'Req. when applicable', '', 'Journal/conference title; vol., no. (year)');
INSERT INTO `metadata_elements` VALUES ('24', 'DC', 'Language', '', 'dc.language', 'Optional', 'ISO 639-1 or ISO 639-2', 'Language of the item; use ISO 639-1 (alpha-2) or ISO 639-2 (alpha-3).');
INSERT INTO `metadata_elements` VALUES ('25', 'DC', 'Relation', '', 'dc.relation', 'Optional', '', 'Supplemental files, e.g. data sets related to publications or larger “whole”');
INSERT INTO `metadata_elements` VALUES ('26', 'DC', 'Coverage', '', 'dc.coverage', 'Req. when applicable', 'CGIAR Vocabulary', 'Geospatial coordinates, countries, regions, sub-regions, chronological period');
INSERT INTO `metadata_elements` VALUES ('27', 'CG', 'Coverage', 'Region', 'cg.coverage.region', 'Req. when applicable', 'UN Stats', 'Supra-national areas (above country level) related to the item being described');
INSERT INTO `metadata_elements` VALUES ('28', 'CG', 'Coverage', 'Country', 'cg:coverage.country', 'Req. when applicable', 'ISO 3166', 'Country/countries related to the data which was collected in resource');
INSERT INTO `metadata_elements` VALUES ('29', 'CG', 'Coverage', 'Admin. Unit', 'cg:coverage.admin-unit', 'Req. when applicable', 'GAUL', 'Sub-national administrative areas such as provinces, states, or districts');
INSERT INTO `metadata_elements` VALUES ('30', 'CG', 'Coverage', 'Geolocation', 'cg.coverage.geolocation', 'Req. when applicable', '', 'Coordinates or polygon points for boundaries of area where research was conducted');
INSERT INTO `metadata_elements` VALUES ('31', 'CG', 'Coverage', 'Start Date', 'cg.coverage.start-date', 'Req. when applicable', 'CGIAR Vocabulary', 'Chronological period: start date of activity described in resource');
INSERT INTO `metadata_elements` VALUES ('32', 'CG', 'Coverage', 'End Date', 'cg.coverage.end-date', 'Req. when applicable', 'CGIAR Vocabulary', 'Chronological period: end date of activity described in resource');
INSERT INTO `metadata_elements` VALUES ('33', 'DC', 'Rights', '', 'dc.rights', 'Required', '', 'Rights, licensing, or permission statement');
INSERT INTO `metadata_elements` VALUES ('34', 'CG', 'Contact', '', 'cg.contact', 'Optional', '', 'For data: email address for group or department to contact in case of questions');
