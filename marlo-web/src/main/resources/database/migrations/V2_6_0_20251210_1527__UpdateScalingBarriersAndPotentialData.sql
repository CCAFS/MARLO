-- Ensure Scaling Barriers answers reflect saved narratives
UPDATE project_innovation_info
SET is_foresee_barriers = 1
WHERE knowledge_tool_uses_narrative IS NOT NULL
  AND CHAR_LENGTH(TRIM(knowledge_tool_uses_narrative)) > 1
  AND (is_foresee_barriers IS NULL OR is_foresee_barriers = 0);

-- Ensure Scaling Potential answers reflect saved narratives (Yes with adaptations)
UPDATE project_innovation_info
SET has_knowledge_potential_id = 2
WHERE reason_knowledge_potential IS NOT NULL
  AND CHAR_LENGTH(TRIM(reason_knowledge_potential)) > 1
  AND (has_knowledge_potential_id IS NULL OR has_knowledge_potential_id <> 2);
