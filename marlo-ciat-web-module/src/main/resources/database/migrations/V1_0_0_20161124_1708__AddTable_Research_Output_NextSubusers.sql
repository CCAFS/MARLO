SET FOREIGN_KEY_CHECKS=0;
DROP TABLE IF EXISTS `research_output_nextsubusers`;

 CREATE TABLE `research_output_nextsubusers` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `research_output_id` int(11) NOT NULL,
  `nextsubuser_id` int(11) NOT NULL,
  PRIMARY KEY (`id`),
  KEY `fk_output_nextsubuser` (`nextsubuser_id`),
  KEY `fk_output_researchoutput` (`research_output_id`),
  CONSTRAINT `fk_output_nextsubuser` FOREIGN KEY (`nextsubuser_id`) REFERENCES `output_nextsubuser` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT `fk_output_researchoutput` FOREIGN KEY (`research_output_id`) REFERENCES `research_outputs` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION
);