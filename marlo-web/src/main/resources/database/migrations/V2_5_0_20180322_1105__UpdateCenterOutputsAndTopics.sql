UPDATE center_outputs,
  crp_programs AS crp_p,
              center_programs AS cen_p
set center_outputs.program_id = crp_p.id
            WHERE
              center_outputs.center_program_id = cen_p.id AND
              crp_p.acronym = cen_p.acronym;
              
UPDATE center_topics,
  crp_programs AS crp_p,
              center_programs AS cen_p
set center_topics.program_id = crp_p.id
            WHERE
              center_topics.research_program_id = cen_p.id AND
              crp_p.acronym = cen_p.acronym;       