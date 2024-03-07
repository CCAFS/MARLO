CREATE TABLE tip_parameters (
  id bigint(20) NOT NULL AUTO_INCREMENT,
  tip_token_service text,
  tip_login_service text,
  tip_status_service text,
  token_value text,
  token_due_date TIMESTAMP NULL DEFAULT NULL,
  private_key text,
  tip_base_url text,
  CONSTRAINT `PRIMARY` PRIMARY KEY (id)
) 
ENGINE=InnoDB
DEFAULT CHARSET=utf8
COLLATE=utf8_general_ci;