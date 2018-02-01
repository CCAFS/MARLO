ALTER TABLE `capdev_supporting_docs` 
ADD COLUMN `deliverable_subtype` BIGINT(20) NULL AFTER `deliverable_type`;
ALTER TABLE `capdev_supporting_docs` 
ADD CONSTRAINT `capdev_supp_docs_deliverable_subtype_fk`
  FOREIGN KEY (`deliverable_subtype`)
  REFERENCES `center_deliverable_types` (`id`);
