CREATE TABLE feedback_qa_comments (
  id bigint(20) auto_increment NOT NULL,
  id_phase bigint(20) NULL,
  field_id bigint(20) NOT NULL,
  screen_id bigint(20) NULL,
  object_id bigint(20) NULL,
  comment text NULL,
  status text NULL,
  reply_id bigint(20) NULL,
  CONSTRAINT feedback_qa_comments_pk PRIMARY KEY (id),
  CONSTRAINT feedback_qa_comments_FK FOREIGN KEY (id_phase) REFERENCES phases(id) ON DELETE RESTRICT ON UPDATE RESTRICT,
  CONSTRAINT feedback_qa_comments_FK_1 FOREIGN KEY (field_id) REFERENCES internal_qa_commentable_fields(id) ON DELETE RESTRICT ON UPDATE RESTRICT,
  CONSTRAINT feedback_qa_comments_FK_2 FOREIGN KEY (reply_id) REFERENCES feedback_comments(id) ON DELETE RESTRICT ON UPDATE RESTRICT
)
ENGINE=InnoDB
DEFAULT CHARSET=utf8
COLLATE=utf8_general_ci;
CREATE UNIQUE INDEX feedback_qa_comments_id_IDX USING BTREE ON feedback_qa_comments (id);