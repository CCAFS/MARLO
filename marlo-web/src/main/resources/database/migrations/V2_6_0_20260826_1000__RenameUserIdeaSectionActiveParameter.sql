-- Rename the AI section specificity so the parameter key reflects the actual section content.
-- The section gated by this parameter is the AI dashboard (/ai), not the legacy "user idea" form.
-- custom_parameters references parameters by id, so existing per-Global Unit values are preserved.
UPDATE `parameters`
SET `key` = 'ai_section_active',
    `description` = 'Activate the AI section'
WHERE `key` = 'user_idea_section_active';
