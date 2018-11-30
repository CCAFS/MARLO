CREATE TABLE `capdev_geographic_scopes` (
  `id` BIGINT(20) NOT NULL,
  `capdev_id` BIGINT(20) NULL,
  `loc_element_id` BIGINT(20) NULL,
  PRIMARY KEY (`id`),
  CONSTRAINT `capdev_geographical_scope_ibfk_1`
    FOREIGN KEY (`id`)
    REFERENCES `deliverable_geographic_regions` (`id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `capdev_geographical_scope_ibfk_2`
    FOREIGN KEY (`id`)
    REFERENCES `loc_elements` (`id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION);