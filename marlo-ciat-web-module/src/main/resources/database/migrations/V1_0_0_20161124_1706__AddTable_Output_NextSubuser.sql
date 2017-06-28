SET FOREIGN_KEY_CHECKS=0;
DROP TABLE IF EXISTS `output_nextsubuser`;

 CREATE TABLE `output_nextsubuser` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `name` varchar(60) NOT NULL,
  `next_userid` int(11) DEFAULT NULL,
  PRIMARY KEY (`id`,`name`),
  KEY `fk_nextsubuser_nextuser` (`next_userid`),
  CONSTRAINT `fk_nextsubuser_nextuser` FOREIGN KEY (`next_userid`) REFERENCES `output_nextusers` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION
);