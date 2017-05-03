ALTER TABLE `project_partners`
ADD COLUMN `responsibilities`  text CHARACTER SET utf8 COLLATE utf8_general_ci NULL AFTER `modification_justification`;
SET sql_mode = '';
SET group_concat_max_len=150000;
CREATE TEMPORARY TABLE IF NOT EXISTS partner_responsabilities AS
select pp.id,GROUP_CONCAT(
    CONCAT(
      u.first_name,
      ' ',
      u.last_name,
      ': ',
      ppp.responsibilities
    ) SEPARATOR "\r\n\r\n"
  )'responsabilities'
from 
  project_partners pp
INNER JOIN project_partner_persons ppp ON pp.id = ppp.project_partner_id
INNER JOIN users u ON u.id = ppp.user_id
AND ppp.is_active = 1
WHERE
  pp.is_active = 1
GROUP BY
  pp.id;
update 
project_partners pp 
INNER JOIN  partner_responsabilities pr on pp.id=pr.id
set pp.responsibilities=pr.responsabilities;

ALTER TABLE `project_partner_persons`
DROP COLUMN `responsibilities`;

