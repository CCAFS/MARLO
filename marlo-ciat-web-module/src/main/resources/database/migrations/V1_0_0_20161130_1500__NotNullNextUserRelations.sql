ALTER TABLE `research_outputs_next_users`
MODIFY COLUMN `research_output_id`  int(11) NULL AFTER `id`,
MODIFY COLUMN `nextuser_type_id`  int(11) NULL AFTER `research_output_id`;