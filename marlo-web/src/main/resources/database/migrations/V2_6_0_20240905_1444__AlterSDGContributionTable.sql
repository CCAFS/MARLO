ALTER TABLE sdg_contributions ADD sdg_id bigint(20) NULL;
ALTER TABLE sdg_contributions ADD CONSTRAINT sdg_contributions_sustainable_development_goals_FK FOREIGN KEY (sdg_id) REFERENCES sustainable_development_goals(id);
