SET FOREIGN_KEY_CHECKS = 0;

ALTER TABLE `crp_ppa_partners`
ADD COLUMN `id_phase`  bigint(20) NULL AFTER `modification_justification`;

ALTER TABLE `crp_ppa_partners` ADD FOREIGN KEY (`id_phase`) REFERENCES `phases` (`id`);

CREATE TEMPORARY TABLE
IF NOT EXISTS table_crp_ppa_partners AS (SELECT * FROM crp_ppa_partners);

truncate table crp_ppa_partners;

INSERT INTO crp_ppa_partners (
 
crp_id,
institution_id,
is_active,
created_by,
active_since,
modified_by,
modification_justification,
id_phase
) SELECT 

t2.crp_id,
t2.institution_id,
t2.is_active,
t2.created_by,
t2.active_since,
t2.modified_by,
t2.modification_justification,
 ph.id
FROM
  table_crp_ppa_partners t2
 
inner JOIN phases ph ON ph.crp_id=t2.crp_id
;