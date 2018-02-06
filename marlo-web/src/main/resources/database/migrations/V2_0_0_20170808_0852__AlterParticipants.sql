ALTER TABLE `participant` 
CHANGE COLUMN `citizenship` `citizenship` bigint(20) NULL,
CHANGE COLUMN `country_of_institucion` `country_of_institucion` bigint(20) NULL,
CHANGE COLUMN `institution` `institution` bigint(20) NULL, 
ADD CONSTRAINT `participant_citizenship_fk`  FOREIGN KEY (`citizenship`)  REFERENCES `loc_elements` (`id`),
ADD CONSTRAINT `participant_institution_fk`  FOREIGN KEY (`institution`)  REFERENCES `institutions` (`id`),
ADD CONSTRAINT `participant_county_of_inst_fk`  FOREIGN KEY (`country_of_institucion`)  REFERENCES `loc_elements` (`id`);

