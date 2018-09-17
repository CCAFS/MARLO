SET FOREIGN_KEY_CHECKS=0;
DROP TABLE IF EXISTS `sub_ido_sdg_target`;
CREATE TABLE `sub_ido_sdg_target` (
  `id` INT NOT NULL AUTO_INCREMENT,
  `cross_cutting_id` BIGINT(20) NULL,
  `slo_id` BIGINT(20) NULL,
  `ido_id` BIGINT(20) NULL,
  `sub_ido_id` BIGINT(20) NULL,
  `sdg_target_id` INT(11) NULL,
  PRIMARY KEY (`id`),
  INDEX `sub_ido_sdg_targets_ido_fk_idx` (`ido_id` ASC),
  INDEX `sub_ido_sdg_targets_cross_cutting_fk_idx` (`cross_cutting_id` ASC),
  INDEX `sub_ido_sdg_targets_slo_fk_idx` (`slo_id` ASC),
  INDEX `sub_ido_sdg_targets_subido_fk_idx` (`sub_ido_id` ASC),
  INDEX `sub_ido_sdg_targets_sdg_target_fk_idx` (`sdg_target_id` ASC),
  CONSTRAINT `sub_ido_sdg_targets_ido_fk`
    FOREIGN KEY (`ido_id`)
    REFERENCES `ccafs_marlo`.`srf_idos` (`id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `sub_ido_sdg_targets_cross_cutting_fk`
    FOREIGN KEY (`cross_cutting_id`)
    REFERENCES `ccafs_marlo`.`srf_cross_cutting_issues` (`id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `sub_ido_sdg_targets_slo_fk`
    FOREIGN KEY (`slo_id`)
    REFERENCES `ccafs_marlo`.`srf_slos` (`id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `sub_ido_sdg_targets_subido_fk`
    FOREIGN KEY (`sub_ido_id`)
    REFERENCES `ccafs_marlo`.`srf_sub_idos` (`id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `sub_ido_sdg_targets_sdg_target_fk`
    FOREIGN KEY (`sdg_target_id`)
    REFERENCES `ccafs_marlo`.`sdg_targets` (`id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION);