CREATE TABLE button_guide_content (
  id BIGINT(10) auto_increment NOT NULL,
  content TEXT NULL,
  `action_name` text NULL,
  `section_name` text NULL,
  `identifier` text NULL,
  CONSTRAINT button_guide_content_pk PRIMARY KEY (id)
)
ENGINE=InnoDB
DEFAULT CHARSET=utf8
COLLATE=utf8_general_ci;