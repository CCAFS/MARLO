ALTER TABLE `capacity_development` 
CHANGE COLUMN `crp` `crp` bigint(20) NULL DEFAULT NULL ;
ALTER TABLE `capacity_development` 
ADD CONSTRAINT `capdev_crp_fk`
  FOREIGN KEY (`crp`)
  REFERENCES `global_units` (`id`)
  ON DELETE NO ACTION
  ON UPDATE NO ACTION;