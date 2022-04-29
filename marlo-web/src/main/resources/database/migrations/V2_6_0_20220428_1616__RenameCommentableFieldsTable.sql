ALTER TABLE internal_qa_commentable_fields
  RENAME TO feedback_qa_commentable_fields;
ALTER TABLE feedback_comments
  RENAME TO feedback_qa_replies;