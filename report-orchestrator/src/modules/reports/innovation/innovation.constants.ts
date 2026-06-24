export const INSTITUTION_COMPOSED_NAME_SQL = `
  CASE
    WHEN i.acronym IS NOT NULL AND TRIM(i.acronym) != '' THEN CONCAT(i.acronym, ' - ', i.name)
    ELSE i.name
  END
`;

export const INSTITUTION_HQ_SQL = `
  COALESCE(
    (
      SELECT le.name
      FROM institutions_locations il
      INNER JOIN loc_elements le ON le.id = il.loc_element_id
      WHERE il.institution_id = i.id
        AND il.is_headquater = 1
      LIMIT 1
    ),
    'Not available'
  )
`;
