SET FOREIGN_KEY_CHECKS=0;
DROP TABLE IF EXISTS `research_output_nextusers`;

 CREATE TABLE `research_output_nextusers` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `research_output_id` int(11) NOT NULL,
  `nextuser_id` int(11) NOT NULL,
  PRIMARY KEY (`id`),
  KEY `fk_output_nextusers` (`nextuser_id`),
  KEY `fk_outputs_researchoutput` (`research_output_id`),
  CONSTRAINT `fk_output_nextusers` FOREIGN KEY (`nextuser_id`) REFERENCES `output_nextusers` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT `fk_outputs_researchoutput` FOREIGN KEY (`research_output_id`) REFERENCES `research_outputs` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION
);