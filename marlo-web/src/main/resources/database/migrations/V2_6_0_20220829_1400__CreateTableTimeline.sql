CREATE TABLE timeline (
  id bigint(20) auto_increment NOT NULL,
  description TEXT NULL,
  start_date TIMESTAMP NULL,
  end_date TIMESTAMP NULL,
  order_index DOUBLE(5,0) NULL,
  CONSTRAINT timeline_pk PRIMARY KEY (id)
)
ENGINE=InnoDB
DEFAULT CHARSET=utf8
COLLATE=utf8_general_ci;