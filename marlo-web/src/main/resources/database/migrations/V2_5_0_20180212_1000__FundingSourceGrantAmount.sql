ALTER TABLE `funding_sources_info`
ADD COLUMN `grant_amount`  double(20,5) NULL DEFAULT 0 AFTER `has_file_research`;