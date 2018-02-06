ALTER TABLE `capacity_development` 
ADD CONSTRAINT `capdev_researhArea_fk`
  FOREIGN KEY (`research_area`)
  REFERENCES `center_areas` (`id`)
  ON DELETE NO ACTION
  ON UPDATE NO ACTION,
ADD CONSTRAINT `capdev_researchProgram_fk`
  FOREIGN KEY (`research_program`)
  REFERENCES `center_programs` (`id`)
  ON DELETE NO ACTION
  ON UPDATE NO ACTION,
ADD CONSTRAINT `capdev_project_fk`
  FOREIGN KEY (`project`)
  REFERENCES `center_projects` (`id`)
  ON DELETE NO ACTION
  ON UPDATE NO ACTION;