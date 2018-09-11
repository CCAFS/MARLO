CREATE TABLE `sgd_indicators` (
  `id` INT NOT NULL AUTO_INCREMENT,
  `unsd_indicator_codes` VARCHAR(45) NOT NULL,
  `sdg_indicator_codes` VARCHAR(45) NOT NULL,
  `sdg_indicator` VARCHAR(45) NOT NULL,
  `sdg_target_id` INT(10) NULL,
  PRIMARY KEY (`id`),
  INDEX `sgd_indicator_sdg_target_fk_idx` (`sdg_target_id` ASC),
  CONSTRAINT `sgd_indicator_sdg_target_fk`
    FOREIGN KEY (`sdg_target_id`)
    REFERENCES `sdg_targets` (`id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION);
