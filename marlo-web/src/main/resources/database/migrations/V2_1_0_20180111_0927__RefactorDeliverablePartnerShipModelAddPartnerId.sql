ALTER TABLE deliverable_partnerships ADD COLUMN project_partner_id bigint(20) NULL AFTER `id_phase`;
ALTER TABLE deliverable_partnerships ADD CONSTRAINT FK_deliverable_partnerships_project_partner_id_idx FOREIGN KEY (project_partner_id) REFERENCES project_partners (id) ON DELETE RESTRICT ON UPDATE RESTRICT;

ALTER TABLE deliverable_partnerships MODIFY partner_person_id bigint(20) NULL;

UPDATE deliverable_partnerships dp SET dp.project_partner_id = (SELECT ppp.project_partner_id FROM project_partner_persons ppp WHERE ppp.id=dp.partner_person_id);

