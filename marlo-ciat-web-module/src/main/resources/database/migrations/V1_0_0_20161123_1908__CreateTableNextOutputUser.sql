  CREATE  TABLE `nextoutput_user` (
  `id` BIGINT NOT NULL AUTO_INCREMENT ,
  `name` VARCHAR(60) NOT NULL ,
  `next_userid` BIGINT NULL ,
  PRIMARY KEY (`id`, `name`) );