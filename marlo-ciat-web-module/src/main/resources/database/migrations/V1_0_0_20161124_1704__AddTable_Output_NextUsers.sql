SET FOREIGN_KEY_CHECKS=0;
DROP TABLE IF EXISTS `output_nextusers`;

CREATE TABLE `output_nextusers` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `name` varchar(60) NOT NULL,
  PRIMARY KEY (`id`,`name`)
);