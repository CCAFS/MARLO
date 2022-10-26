ALTER TABLE feedback_qa_comments ADD draft_action_user_id bigint(20) NULL;
ALTER TABLE feedback_qa_comments ADD draft_action_date timestamp NULL;
ALTER TABLE feedback_qa_comments ADD CONSTRAINT feedback_qa_comments_FK_9 FOREIGN KEY (draft_action_user_id) REFERENCES users(id) ON DELETE RESTRICT ON UPDATE RESTRICT;