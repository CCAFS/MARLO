CREATE TABLE aiccradb.project_expected_study_tags (
  id bigint(20) auto_increment NOT NULL,
  tag_name text NULL,
  tag_description text NULL,
  CONSTRAINT project_expected_study_tags_pk PRIMARY KEY (id)
)
ENGINE=InnoDB
DEFAULT CHARSET=utf8
COLLATE=utf8_general_ci;