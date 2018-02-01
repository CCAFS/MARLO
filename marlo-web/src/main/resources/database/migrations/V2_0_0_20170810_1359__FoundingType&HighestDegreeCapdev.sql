DROP TABLE IF EXISTS `capdev_founding_type`;

CREATE TABLE `capdev_founding_type` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `name` varchar(200) NULL,
  `description` text NULL,
  PRIMARY KEY (`id`));


INSERT INTO `capdev_founding_type` (`id`, `name`) VALUES ('1', 'CIAT');
INSERT INTO `capdev_founding_type` (`id`, `name`) VALUES ('2', 'Scholarship');
INSERT INTO `capdev_founding_type` (`id`, `name`) VALUES ('3', 'Volunteer');




DROP TABLE IF EXISTS `capdev_highest_degree`;
CREATE TABLE `capdev_highest_degree` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `name` varchar(200) NULL,
  `description` text NULL,
  `acronym` varchar(45) NULL,
  PRIMARY KEY (`id`));


INSERT INTO `capdev_highest_degree` (`id`, `name`, `acronym`) VALUES ('1', 'Bachelor', 'B');
INSERT INTO `capdev_highest_degree` (`id`, `name`, `acronym`) VALUES ('2', 'Bachelor of Science', 'BSc');
INSERT INTO `capdev_highest_degree` (`id`, `name`, `acronym`) VALUES ('3', 'Master of Science', 'MSc');
INSERT INTO `capdev_highest_degree` (`id`, `name`, `acronym`) VALUES ('4', 'Doctor of Philosophy', 'PhD');
INSERT INTO `capdev_highest_degree` (`id`, `name`, `acronym`) VALUES ('5', 'Technician', 'Tech');