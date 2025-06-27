ALTER TABLE feedback_qa_replies ADD comment_id bigint(20) NULL;
ALTER TABLE feedback_qa_replies ADD CONSTRAINT replies_feedback_qa_comments_FK FOREIGN KEY (comment_id) REFERENCES feedback_qa_comments(id) ON DELETE CASCADE ON UPDATE CASCADE;
ALTER TABLE feedback_qa_replies ADD status_id bigint(20) NULL;
ALTER TABLE feedback_qa_replies ADD CONSTRAINT replies_feedback_statuses_FK FOREIGN KEY (status_id) REFERENCES feedback_statuses(id) ON DELETE RESTRICT ON UPDATE RESTRICT;