ALTER TABLE `deliverable_dissemination`
ADD COLUMN `has_doi`  tinyint(1) NULL AFTER `article_url`;
