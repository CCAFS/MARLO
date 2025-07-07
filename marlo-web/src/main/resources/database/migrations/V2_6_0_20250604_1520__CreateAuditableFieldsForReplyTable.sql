ALTER TABLE feedback_qa_replies ADD approval_date timestamp NULL;
ALTER TABLE feedback_qa_replies ADD user_approval_id bigint NULL;
ALTER TABLE feedback_qa_replies ADD CONSTRAINT feedback_qa_replies_users_FK FOREIGN KEY (user_approval_id) REFERENCES users(id) ON DELETE RESTRICT ON UPDATE RESTRICT;
ALTER TABLE feedback_qa_replies ADD id_phase bigint NULL;
ALTER TABLE feedback_qa_replies ADD CONSTRAINT feedback_qa_replies_phases_FK FOREIGN KEY (id_phase) REFERENCES phases(id) ON DELETE RESTRICT ON UPDATE RESTRICT;