ALTER TABLE feedback_statuses ADD CONSTRAINT feedback_statuses_un UNIQUE KEY (id);

ALTER TABLE feedback_qa_comments ADD status_id bigint(20) NULL;
