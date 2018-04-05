ALTER TABLE `center_section_statuses`
ADD COLUMN `program_id`  bigint(20) NULL AFTER `research_program_id`;

ALTER TABLE `center_section_statuses` ADD CONSTRAINT `center_section_statuses_ibfk_7` FOREIGN KEY (`program_id`) REFERENCES `crp_programs` (`id`);



UPDATE center_section_statuses,
  crp_programs AS crp_p,
              center_programs AS cen_p
set center_section_statuses.program_id = crp_p.id
            WHERE
              center_section_statuses.research_program_id = cen_p.id AND
              crp_p.acronym = cen_p.acronym;    