CREATE TABLE feedback_comments (
  id bigint(20) auto_increment NOT NULL,
  comment text NULL,
  CONSTRAINT `PRIMARY` PRIMARY KEY (id)
)
ENGINE=InnoDB
DEFAULT CHARSET=utf8
COLLATE=utf8_general_ci;
CREATE UNIQUE INDEX feedback_comments_id_IDX USING BTREE ON feedback_comments (id);