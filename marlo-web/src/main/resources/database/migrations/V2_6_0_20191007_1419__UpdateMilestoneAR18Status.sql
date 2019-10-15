UPDATE crp_milestones as crpm
INNER JOIN (SELECT
cm.id,
cm.composed_id,
rspm.milestones_status,
co.id_phase,
p.description,
p.year,
gu.acronym
FROM
report_synthesis_flagship_progress_outcome_milestones AS rspm
INNER JOIN crp_milestones AS cm ON rspm.crp_milestone_id = cm.id
INNER JOIN crp_program_outcomes AS co ON cm.crp_program_outcome_id = co.id
INNER JOIN phases AS p ON co.id_phase = p.id
INNER JOIN global_units AS gu ON p.global_unit_id = gu.id
ORDER BY
cm.composed_id ASC) as tmp ON crpm.id = tmp.id
SET crpm.milestone_status = tmp.milestones_status
WHERE
tmp.composed_id = crpm.composed_id;