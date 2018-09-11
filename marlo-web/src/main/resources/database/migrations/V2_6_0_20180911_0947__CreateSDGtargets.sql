CREATE TABLE `sdg_targets` (
  `id` INT NOT NULL AUTO_INCREMENT,
  `sdg_target_code` VARCHAR(5) NOT NULL,
  `sdg_target` VARCHAR(700) NULL,
  `sdg_id` BIGINT(20) NOT NULL,
  PRIMARY KEY (`id`),
  INDEX `sdgtargets_sdg_id_fk_idx` (`sdg_id` ASC),
  CONSTRAINT `sdgtargets_sdg_id_fk`
    FOREIGN KEY (`sdg_id`)
    REFERENCES `sustainable_development_goals` (`id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION);
 