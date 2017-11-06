UPDATE 
deliverables
SET `status`='4'
WHERE
  new_expected_year >= `year`
AND `status` = 2
AND is_active
AND new_expected_year IS NOT NULL;