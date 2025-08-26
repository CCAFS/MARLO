INSERT INTO portfolios (name,created_by,global_unit_id)
	VALUES ('AICCRA',1,45);
INSERT INTO portfolios (name,created_by,global_unit_id)
	VALUES ('AICCRA AF',1,45);
INSERT INTO portfolios (name,created_by,global_unit_id)
	VALUES ('AICCRA 3',1,45);

ALTER TABLE crp_program_outcomes ADD CONSTRAINT crp_program_outcomes_portfolios_FK FOREIGN KEY (portfolio_id) REFERENCES portfolios(id) ON DELETE RESTRICT ON UPDATE RESTRICT;