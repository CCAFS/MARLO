  CREATE  TABLE `research_output_nextusers` (
  `id` BIGINT NOT NULL AUTO_INCREMENT ,
  `research_output_id` BIGINT NOT NULL ,
  `nextuser_id` BIGINT NOT NULL ,
  PRIMARY KEY (`id`) );