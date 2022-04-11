CREATE TABLE internal_qa_commentable_fields (
  id bigint(20) auto_increment NOT NULL,
  section_name text NOT NULL,
  parent_name text NULL,
  parent_id bigint(20) NULL,
  table_name text NULL,
  field_name text NULL,
  front_name text NULL,
  CONSTRAINT `PRIMARY` PRIMARY KEY (id)
)
ENGINE=InnoDB
DEFAULT CHARSET=utf8
COLLATE=utf8_general_ci;