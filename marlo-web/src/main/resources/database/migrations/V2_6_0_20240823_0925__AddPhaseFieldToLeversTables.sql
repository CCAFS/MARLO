ALTER TABLE primary_alliance_levers ADD id_phase bigint(20) NULL;
ALTER TABLE primary_alliance_levers ADD CONSTRAINT primary_alliance_levers_phases_FK FOREIGN KEY (id_phase) REFERENCES phases(id) ON DELETE RESTRICT ON UPDATE RESTRICT;

ALTER TABLE primary_alliance_strategic_outcomes ADD id_phase bigint(20) NULL;
ALTER TABLE primary_alliance_strategic_outcomes ADD CONSTRAINT primary_alliance_strategic_outcomes_phases_FK FOREIGN KEY (id_phase) REFERENCES phases(id) ON DELETE RESTRICT ON UPDATE RESTRICT;

ALTER TABLE related_alliance_levers ADD id_phase bigint(20) NULL;
ALTER TABLE related_alliance_levers ADD CONSTRAINT related_alliance_levers_phases_FK FOREIGN KEY (id_phase) REFERENCES phases(id) ON DELETE RESTRICT ON UPDATE RESTRICT;

ALTER TABLE related_alliance_lever_sdg_contributions ADD id_phase bigint(20) NULL;
ALTER TABLE related_alliance_lever_sdg_contributions ADD CONSTRAINT related_alliance_lever_sdg_contributions_phases_FK FOREIGN KEY (id_phase) REFERENCES phases(id) ON DELETE RESTRICT ON UPDATE RESTRICT;