set global tmp_table_size=268435456;
CREATE TEMPORARY TABLE
IF NOT EXISTS table_audt AS (SELECT * FROM auditlog);

TRUNCATE TABLE auditlog;



ALTER TABLE `auditlog`
ADD COLUMN `id_phase`  bigint(20) NULL AFTER `modification_justification`;


INSERT INTO auditlog (
 
  ACTION,
DETAIL,
CREATED_DATE,
ENTITY_ID,
ENTITY_NAME,
Entity_json,
user_id,
main,
transaction_id,
relation_name,
modification_justification,

  id_phase
)SELECT 

     ACTION,
DETAIL,
CREATED_DATE,
ENTITY_ID,
ENTITY_NAME,
Entity_json,
user_id,
main,
transaction_id,
relation_name,
t2.modification_justification,
  ph.id
FROM
  table_audt t2
INNER JOIN crps cp on  t2.DETAIL like  CONCAT('Action: ',cp.acronym ,'%')
INNER JOIN phases ph ON ph.crp_id = cp.id
where ph.description='Planning' and ph.`year`=2017
;
