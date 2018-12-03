UPDATE center_outputs_outcomes
SET is_active = 0
WHERE
  id IN (
    SELECT
      id
    FROM
      (
        SELECT
          o.id
        FROM
          center_outputs_outcomes o
        WHERE
          o.outcome_id NOT IN (56, 57, 58, 70)
      ) AS temp_tbl
  );
  
UPDATE center_outcomes
SET is_active = 0
WHERE
  id IN (
    SELECT
      id
    FROM
      (
        SELECT
          center_outcomes.id
        FROM
          center_outcomes
        INNER JOIN center_topics ON center_outcomes.research_topic_id = center_topics.id
        WHERE
          center_topics.program_id = 131
        AND center_outcomes.id NOT IN (56, 57, 58, 70)
      ) AS temp_tbl_out
  );