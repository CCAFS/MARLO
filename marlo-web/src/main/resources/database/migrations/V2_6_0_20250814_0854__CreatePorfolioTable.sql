CREATE TABLE IF NOT EXISTS portfolios (
  id BIGINT(20) NOT NULL AUTO_INCREMENT,
  name TEXT NULL,
  start_date DATE NULL,
  end_date DATE NULL,
  is_active TINYINT(1) NOT NULL DEFAULT 1,
  active_since TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  created_by BIGINT NOT NULL,
  modified_by BIGINT NULL,
  modification_justification TEXT NULL,
  global_unit_id BIGINT(20) NULL,

  CONSTRAINT portfolios_pk PRIMARY KEY (id),

  /* FKs */
  CONSTRAINT portfolios_users_created_by_FK  FOREIGN KEY (created_by)   REFERENCES users(id)
    ON DELETE RESTRICT ON UPDATE RESTRICT,
  CONSTRAINT portfolios_users_modified_by_FK FOREIGN KEY (modified_by)  REFERENCES users(id)
    ON DELETE RESTRICT ON UPDATE RESTRICT,
  CONSTRAINT portfolios_global_units_FK      FOREIGN KEY (global_unit_id) REFERENCES global_units(id)
    ON DELETE RESTRICT ON UPDATE RESTRICT,

  KEY idx_portfolios_created_by     (created_by),
  KEY idx_portfolios_modified_by    (modified_by),
  KEY idx_portfolios_global_unit_id (global_unit_id)
)
ENGINE=InnoDB
DEFAULT CHARSET=utf8mb4
COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE IF NOT EXISTS portfolio_phases (
  id BIGINT(20) NOT NULL AUTO_INCREMENT,
  portfolio_id BIGINT(20) NOT NULL,
  id_phase BIGINT(20) NOT NULL,
  is_active TINYINT(1) NOT NULL DEFAULT 1,
  active_since TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  created_by BIGINT NOT NULL,
  modified_by BIGINT NULL,
  modification_justification TEXT NULL,

  CONSTRAINT portfolio_phases_pk PRIMARY KEY (id),

  CONSTRAINT portfolio_phases_portfolios_FK FOREIGN KEY (portfolio_id) REFERENCES portfolios(id)
    ON DELETE RESTRICT ON UPDATE RESTRICT,
  CONSTRAINT portfolio_phases_phases_FK    FOREIGN KEY (id_phase)     REFERENCES phases(id)
    ON DELETE RESTRICT ON UPDATE RESTRICT,
  CONSTRAINT portfolio_phases_users_cb_FK  FOREIGN KEY (created_by)   REFERENCES users(id)
    ON DELETE RESTRICT ON UPDATE RESTRICT,
  CONSTRAINT portfolio_phases_users_mb_FK  FOREIGN KEY (modified_by)  REFERENCES users(id)
    ON DELETE RESTRICT ON UPDATE RESTRICT,

  KEY idx_portfolio_phases_portfolio_id (portfolio_id),
  KEY idx_portfolio_phases_id_phase     (id_phase),
  KEY idx_portfolio_phases_created_by   (created_by),
  KEY idx_portfolio_phases_modified_by  (modified_by)
)
ENGINE=InnoDB
DEFAULT CHARSET=utf8mb4
COLLATE=utf8mb4_0900_ai_ci;
