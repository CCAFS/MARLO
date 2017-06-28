ALTER TABLE `research_outputs`
MODIFY COLUMN `date_added`  timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP AFTER `title`;

