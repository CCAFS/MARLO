INSERT INTO phases (`name`, description, `year`, upkeep, start_date, end_date, editable, visible, next_phase, global_unit_id)
SELECT p.`name`, p.description, p.`year`, p.upkeep, p.start_date, p.end_date, p.editable, p.visible, p.next_phase, 26 from phases p
where p.global_unit_id = 24;