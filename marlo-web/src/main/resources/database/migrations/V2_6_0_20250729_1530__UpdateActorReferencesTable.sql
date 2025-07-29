-- 1. Researchers → General researchers
UPDATE project_innovation_actors
SET actor_id = (
  SELECT id FROM actors
  WHERE name = 'Researchers' AND prms_name_equivalent = 'General researchers'
  LIMIT 1
)
WHERE actor_id IN (
  SELECT id FROM actors
  WHERE name = 'Researchers'
);

-- 2. Policy actors → General policy actors
UPDATE project_innovation_actors
SET actor_id = (
  SELECT id FROM actors
  WHERE name = 'Policy actors (public or private)' AND prms_name_equivalent = 'General policy actors'
  LIMIT 1
)
WHERE actor_id IN (
  SELECT id FROM actors
  WHERE name = 'Policy actors (public or private)'
);

-- 3. Banks/Investors → Formal banks
UPDATE project_innovation_actors
SET actor_id = (
  SELECT id FROM actors
  WHERE name = 'Banks/Investors' AND prms_name_equivalent = 'Formal banks'
  LIMIT 1
)
WHERE actor_id IN (
  SELECT id FROM actors
  WHERE name = 'Banks/Investors'
);