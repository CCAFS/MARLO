/*Clear institution when is a crp_program*/
UPDATE liaison_institutions SET `institution_id`=NULL WHERE (`crp_program` IS NOT NULL);