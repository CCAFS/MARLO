CREATE TABLE feedback_statuses (
  id bigint(20) NOT NULL,
  status_name text NULL,
  status_description text NULL,
  CONSTRAINT feedback_statuses_pk PRIMARY KEY (id)
)
ENGINE=InnoDB
DEFAULT CHARSET=utf8
COLLATE=utf8_general_ci;