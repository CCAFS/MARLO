ALTER TABLE `capacity_development` 
CHANGE COLUMN `capdev_type` `capdev_type` bigint(20) NOT NULL ,
ADD INDEX `capdev_capdevtype_fk_idx` (`capdev_type` ASC);


ALTER TABLE `capacity_development` 
ADD CONSTRAINT `capdev_capdevtype_fk`
  FOREIGN KEY (`capdev_type`)
  REFERENCES `capacity_development_types` (`id`)
  ON DELETE NO ACTION
  ON UPDATE NO ACTION;
