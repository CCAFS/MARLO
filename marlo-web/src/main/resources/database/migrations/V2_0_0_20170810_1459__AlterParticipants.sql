ALTER TABLE `participant` 
CHANGE COLUMN `highest_degree` `highest_degree` BIGINT(20) NULL  ,
CHANGE COLUMN `fellowship` `fellowship` BIGINT(20) NULL ;

ALTER TABLE `participant` 
ADD CONSTRAINT `participant_highest_degree_fk`
  FOREIGN KEY (`highest_degree`)
  REFERENCES `capdev_highest_degree` (`id`),
ADD CONSTRAINT `participant_founding_type_fk`
  FOREIGN KEY (`fellowship`)
  REFERENCES `capdev_founding_type` (`id`);