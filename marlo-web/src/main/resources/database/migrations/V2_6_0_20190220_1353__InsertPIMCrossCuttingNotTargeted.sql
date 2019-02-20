/* Create not-targeted deliverable cross cuttings for non-created deliverables' cross cutting */

/* 1. Gender */
INSERT INTO deliverable_cross_cutting_markers (
  deliverable_id,
  id_phase,
  cgiar_cross_cutting_marker_id,
  rep_ind_gender_youth_focus_level_id
) SELECT
  di.deliverable_id,
  di.id_phase,
  1,
  1
FROM
  deliverables_info di
LEFT JOIN deliverable_cross_cutting_markers dc ON dc.deliverable_id = di.deliverable_id
AND dc.id_phase = di.id_phase
AND dc.cgiar_cross_cutting_marker_id = 1
INNER JOIN phases ph ON ph.id = di.id_phase
AND ph.global_unit_id = 3
WHERE
  dc.id IS NULL;

/* 2. Youth */
INSERT INTO deliverable_cross_cutting_markers (
  deliverable_id,
  id_phase,
  cgiar_cross_cutting_marker_id,
  rep_ind_gender_youth_focus_level_id
) SELECT
  di.deliverable_id,
  di.id_phase,
  2,
  1
FROM
  deliverables_info di
LEFT JOIN deliverable_cross_cutting_markers dc ON dc.deliverable_id = di.deliverable_id
AND dc.id_phase = di.id_phase
AND dc.cgiar_cross_cutting_marker_id = 2
INNER JOIN phases ph ON ph.id = di.id_phase
AND ph.global_unit_id = 3
WHERE
  dc.id IS NULL;
/* 3. CapDev */
INSERT INTO deliverable_cross_cutting_markers (
  deliverable_id,
  id_phase,
  cgiar_cross_cutting_marker_id,
  rep_ind_gender_youth_focus_level_id
) SELECT
  di.deliverable_id,
  di.id_phase,
  3,
  1
FROM
  deliverables_info di
LEFT JOIN deliverable_cross_cutting_markers dc ON dc.deliverable_id = di.deliverable_id
AND dc.id_phase = di.id_phase
AND dc.cgiar_cross_cutting_marker_id = 3
INNER JOIN phases ph ON ph.id = di.id_phase
AND ph.global_unit_id = 3
WHERE
  dc.id IS NULL;

/* 4. Climate Change */
INSERT INTO deliverable_cross_cutting_markers (
  deliverable_id,
  id_phase,
  cgiar_cross_cutting_marker_id,
  rep_ind_gender_youth_focus_level_id
) SELECT
  di.deliverable_id,
  di.id_phase,
  4,
  1
FROM
  deliverables_info di
LEFT JOIN deliverable_cross_cutting_markers dc ON dc.deliverable_id = di.deliverable_id
AND dc.id_phase = di.id_phase
AND dc.cgiar_cross_cutting_marker_id = 4
INNER JOIN phases ph ON ph.id = di.id_phase
AND ph.global_unit_id = 3
WHERE
  dc.id IS NULL;