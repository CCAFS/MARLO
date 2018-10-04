SET FOREIGN_KEY_CHECKS=0;

/* Migrate Participant Geographic Scope and Regions*/

UPDATE deliverables_info di
INNER JOIN deliverable_participants dp ON di.deliverable_id = dp.deliverable_id
AND dp.phase_id = di.id_phase
AND dp.rep_ind_geographic_scope_id IS NOT NULL
AND dp.is_active
SET di.geographic_scope_id = dp.rep_ind_geographic_scope_id,
 di.region_id = dp.rep_ind_region_id
WHERE
  di.is_active;
  
/* Migrate Participant Locations */
  INSERT INTO deliverable_locations (
  deliverable_id,
  loc_element_id,
  id_phase
) SELECT
  dp.deliverable_id,
  dpl.loc_element_id,
  dp.phase_id
FROM
  deliverable_participant_locations dpl
INNER JOIN deliverable_participants dp ON dp.id = dpl.deliverable_participant_id
AND dp.is_active
AND dp.rep_ind_geographic_scope_id IN (3, 4, 5)
WHERE
  dpl.is_active;
  
/* Drop Deliverable Participant Geographic Scope and Locations*/
DROP TABLE IF EXISTS deliverable_participant_locations;

ALTER TABLE `deliverable_participants` DROP FOREIGN KEY `deliverable_participants_ibfk_5`;
ALTER TABLE `deliverable_participants` DROP FOREIGN KEY `deliverable_participants_ibfk_6`;
ALTER TABLE `deliverable_participants`
DROP COLUMN `rep_ind_geographic_scope_id`,
DROP COLUMN `rep_ind_region_id`,
DROP INDEX `idx_deliverable_participants_geographic_scope`,
DROP INDEX `idx_deliverable_participants_region`;