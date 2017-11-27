drop FUNCTION if exists  devPpa;

DELIMITER $$
 
CREATE FUNCTION devPpa(devId BIGINT, crpId BIGINT) RETURNS VARCHAR(21845)
    DETERMINISTIC
BEGIN
    DECLARE institutionsValue varchar(21845);
 
 
SELECT GROUP_CONCAT(DISTINCT t ORDER BY t SEPARATOR ', ' )
INTO institutionsValue
 FROM (
SELECT
          DISTINCT (
        CASE
        WHEN i4.acronym IS NULL
        OR i4.acronym = '' THEN
          i4. NAME
        ELSE
          i4.acronym
        END
    ) as 't'
  FROM
    project_partner_contributions ppc
  INNER JOIN project_partners pp4 ON ppc.project_partner_contributor_id = pp4.id
  AND pp4.is_active
  INNER JOIN institutions i4 ON i4.id = pp4.institution_id
  WHERE
    ppc.project_partner_id IN (
      SELECT
        ppp.project_partner_id
      FROM
        deliverable_partnerships dpp
      INNER JOIN project_partner_persons ppp ON ppp.id = partner_person_id
      WHERE
        dpp.is_active = 1
      AND dpp.deliverable_id = devId
    )
UNION DISTINCT
SELECT 
(CASE WHEN i5.acronym IS NULL
          OR i5.acronym = '' THEN
            i5. NAME
          ELSE
            i5.acronym END) as 't'
FROM crp_ppa_partners cpp 
LEFT JOIN institutions i5 ON i5.id=cpp.institution_id
WHERE cpp.is_active AND cpp.crp_id=crpId
AND cpp.institution_id IN (SELECT
        pp.institution_id
      FROM
        deliverable_partnerships dpp
      INNER JOIN project_partner_persons ppp ON ppp.id = partner_person_id
      INNER JOIN project_partners pp ON pp.id = ppp.project_partner_id
      WHERE
        dpp.is_active = 1
      AND dpp.deliverable_id = devId
)
)t2
;
 
 RETURN (institutionsValue);
END