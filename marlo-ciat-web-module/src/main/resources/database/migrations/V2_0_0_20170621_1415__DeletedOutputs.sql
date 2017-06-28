UPDATE project_outputs 
INNER JOIN research_outputs ON project_outputs.output_id = research_outputs.id AND research_outputs.is_active = 0 AND project_outputs.is_active = 1
SET project_outputs.is_active=0;