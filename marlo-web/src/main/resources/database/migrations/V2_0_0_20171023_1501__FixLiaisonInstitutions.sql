/* Clear PMU, FL and Region institutions*/
UPDATE liaison_institutions SET `institution_id`=NULL WHERE (`acronym`='PMU');
UPDATE liaison_institutions SET `institution_id`=NULL WHERE (`acronym`='F2');
UPDATE liaison_institutions SET `institution_id`=NULL WHERE (`acronym`='F4');
UPDATE liaison_institutions SET `institution_id`=NULL WHERE (`acronym`='F3');
UPDATE liaison_institutions SET `institution_id`=NULL WHERE (`acronym`='F1');
UPDATE liaison_institutions SET `institution_id`=NULL WHERE (`acronym`='RP EA');
UPDATE liaison_institutions SET `institution_id`=NULL WHERE (`acronym`='RP LAM');
UPDATE liaison_institutions SET `institution_id`=NULL WHERE (`acronym`='RP SAs');
UPDATE liaison_institutions SET `institution_id`=NULL WHERE (`acronym`='RP SEA');
UPDATE liaison_institutions SET `institution_id`=NULL WHERE (`acronym`='RP WA');

/* Copy CCAFS liaison institutions to WLE (taking in account the liaison_users with WLE)*/
INSERT INTO liaison_institutions (institution_id,NAME,acronym,crp_program,crp_id,is_active) 
SELECT li.institution_id, li.`name`,  li.acronym, li.crp_program,
  (SELECT id FROM crps c WHERE c.is_active AND c.acronym = "WLE"),
  li.is_active
FROM
  liaison_users lu
INNER JOIN liaison_institutions li ON li.id = lu.institution_id
WHERE
  lu.crp_id = (SELECT id FROM crps c WHERE c.is_active AND c.acronym = "WLE")
AND li.institution_id IS NOT NULL
GROUP BY
  li.institution_id;

/* Update liaison_users to match added liaison_institutions */
UPDATE liaison_users lu
INNER JOIN liaison_institutions li ON li.id = lu.institution_id
INNER JOIN liaison_institutions li2 ON li2.institution_id = li.institution_id
AND li2.crp_id = (SELECT  id FROM crps c WHERE c.is_active AND c.acronym = "WLE")
SET lu.institution_id = li2.id
WHERE
  li2.institution_id IS NOT NULL
AND lu.crp_id = (SELECT id FROM crps c WHERE c.is_active AND c.acronym = "WLE");