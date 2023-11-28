CREATE TABLE tip_parameters (
  id bigint(20) auto_increment NOT NULL,
  tip_token_service text NULL,
  tip_login_service text NULL,
  tip_status_service text NULL,
  token_value text NULL,
  token_due_date TIMESTAMP NULL,
  CONSTRAINT `PRIMARY` PRIMARY KEY (id)
)
ENGINE=InnoDB
DEFAULT CHARSET=utf8
COLLATE=utf8_general_ci;