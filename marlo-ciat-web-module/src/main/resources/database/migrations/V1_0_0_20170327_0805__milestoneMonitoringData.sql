ALTER TABLE `research_milestones`
ADD COLUMN `is_impact_pathway`  tinyint(1) NULL AFTER `impact_outcome_id`;

update research_milestones
set is_impact_pathway = 1;