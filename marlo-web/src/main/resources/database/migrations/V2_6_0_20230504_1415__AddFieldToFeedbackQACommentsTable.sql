ALTER TABLE feedback_qa_comments ADD is_tracking tinyint(1) NULL;
ALTER TABLE feedback_qa_comments ADD start_track_date timestamp NULL;
ALTER TABLE feedback_qa_comments ADD end_track_date timestamp NULL;