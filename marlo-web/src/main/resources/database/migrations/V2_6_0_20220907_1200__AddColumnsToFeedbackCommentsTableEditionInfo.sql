ALTER TABLE feedback_qa_comments ADD user_editor_id bigint(20) NULL;
ALTER TABLE feedback_qa_comments ADD CONSTRAINT feedback_qa_comments_FK_8 FOREIGN KEY (user_editor_id) REFERENCES users(id) ON DELETE RESTRICT ON UPDATE RESTRICT;
ALTER TABLE feedback_qa_comments ADD edition_date timestamp NULL;