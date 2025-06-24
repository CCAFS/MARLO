UPDATE feedback_qa_replies r
JOIN feedback_qa_comments c ON r.id = c.reply_id
SET r.comment_id = c.id
WHERE r.comment_id IS NULL OR r.comment_id <> c.id;