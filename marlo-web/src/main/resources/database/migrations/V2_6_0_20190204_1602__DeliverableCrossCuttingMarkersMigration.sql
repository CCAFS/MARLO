/* Gender */
INSERT INTO deliverable_cross_cutting_markers (
  deliverable_id,
  id_phase,
  cgiar_cross_cutting_marker_id,
  rep_ind_gender_youth_focus_level_id
) SELECT
  di.deliverable_id,
  di.id_phase,
  1,
  (
    CASE
    WHEN di.cross_cutting_score_gender IS NULL THEN
      4
    WHEN di.cross_cutting_score_gender = 0 THEN
      1
    WHEN di.cross_cutting_score_gender = 1 THEN
      2
    WHEN di.cross_cutting_score_gender = 2 THEN
      3
    END
  )
FROM
  deliverables_info di
WHERE
  di.cross_cutting_gender;

/* Youth */
INSERT INTO deliverable_cross_cutting_markers (
  deliverable_id,
  id_phase,
  cgiar_cross_cutting_marker_id,
  rep_ind_gender_youth_focus_level_id
) SELECT
  di.deliverable_id,
  di.id_phase,
  2,
  (
    CASE
    WHEN di.cross_cutting_score_youth IS NULL THEN
      4
    WHEN di.cross_cutting_score_youth = 0 THEN
      1
    WHEN di.cross_cutting_score_youth = 1 THEN
      2
    WHEN di.cross_cutting_score_youth = 2 THEN
      3
    END
  )
FROM
  deliverables_info di
WHERE
  di.cross_cutting_youth;

/* Cap Dev */
INSERT INTO deliverable_cross_cutting_markers (
  deliverable_id,
  id_phase,
  cgiar_cross_cutting_marker_id,
  rep_ind_gender_youth_focus_level_id
) SELECT
  di.deliverable_id,
  di.id_phase,
  3,
  (
    CASE
    WHEN di.cross_cutting_score_capacity IS NULL THEN
      4
    WHEN di.cross_cutting_score_capacity = 0 THEN
      1
    WHEN di.cross_cutting_score_capacity = 1 THEN
      2
    WHEN di.cross_cutting_score_capacity = 2 THEN
      3
    END
  )
FROM
  deliverables_info di
WHERE
  di.cross_cutting_capacity;

/* Climate Change */
INSERT INTO deliverable_cross_cutting_markers (
  deliverable_id,
  id_phase,
  cgiar_cross_cutting_marker_id,
  rep_ind_gender_youth_focus_level_id
) SELECT
  di.deliverable_id,
  di.id_phase,
  4,
  (
    CASE
    WHEN di.cross_cutting_score_climate IS NULL THEN
      4
    WHEN di.cross_cutting_score_climate = 0 THEN
      1
    WHEN di.cross_cutting_score_climate = 1 THEN
      2
    WHEN di.cross_cutting_score_climate = 2 THEN
      3
    END
  )
FROM
  deliverables_info di
WHERE
  di.cross_cutting_climate;