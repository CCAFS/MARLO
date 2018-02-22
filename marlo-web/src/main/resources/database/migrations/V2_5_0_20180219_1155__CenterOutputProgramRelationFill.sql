DROP PROCEDURE
IF EXISTS `cur_output_center`;
DELIMITER ;;
CREATE PROCEDURE `cur_output_center`()
BEGIN
  DECLARE
    done INT DEFAULT FALSE;

DECLARE
  a INT;

DECLARE
  b INT;

DECLARE
  cur1 CURSOR FOR (
    SELECT
      coo.output_id,
      ct.research_program_id
    FROM
      center_outputs_outcomes AS coo
    INNER JOIN center_outcomes AS co ON coo.outcome_id = co.id
    INNER JOIN center_topics AS ct ON co.research_topic_id = ct.id
    WHERE
      ct.is_active = 1
    AND co.is_active = 1
    AND coo.is_active = 1
  );

DECLARE
  CONTINUE HANDLER FOR NOT FOUND
SET done = TRUE;

OPEN cur1;

read_loop :
LOOP
  FETCH cur1 INTO a,
  b;


IF done = 1 THEN
  LEAVE read_loop;
END IF;

UPDATE center_outputs
SET center_program_id = b
WHERE
  id = a;


END
LOOP
;

CLOSE cur1;
END;;
DELIMITER;
CALL cur_output_center();

DROP PROCEDURE
IF EXISTS `cur_output_center`;
