### Update funding_source_budgets

UPDATE funding_source_budgets fsb
INNER JOIN funding_sources fs ON fsb.funding_source_id = fs.id
SET fsb.is_active = 0
WHERE fsb.is_active = 1 AND fs.is_active = 0;

### Update funding_source_locations

UPDATE funding_source_locations fsl
INNER JOIN funding_sources fs ON fsl.funding_source_id = fs.id
SET fsl.is_active = 0
WHERE fsl.is_active = 1 AND fs.is_active = 0;