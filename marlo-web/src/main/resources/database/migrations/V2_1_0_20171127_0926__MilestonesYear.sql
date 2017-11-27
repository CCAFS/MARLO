UPDATE 
  crp_milestones mil
INNER JOIN crp_program_outcomes po ON po.id = mil.crp_program_outcome_id
inner JOIN phases ph on ph.id=po.id_phase
set mil.is_active=0
where mil.`year`<ph.`year`;

 update project_milestones pm INNER JOIN crp_milestones mil on mil.id=pm.crp_milestone_id
set pm.is_active=0
where mil.is_active=0 and pm.is_active=1

;