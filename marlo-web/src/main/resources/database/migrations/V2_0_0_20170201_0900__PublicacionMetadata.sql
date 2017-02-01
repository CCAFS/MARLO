ALTER TABLE `deliverable_publications_metada`
MODIFY COLUMN `isi_publication`  tinyint(1) NULL DEFAULT NULL AFTER `journal`,
MODIFY COLUMN `nasr`  tinyint(1) NULL DEFAULT NULL AFTER `isi_publication`,
MODIFY COLUMN `co_author`  tinyint(1) NULL DEFAULT NULL AFTER `nasr`,
ADD COLUMN `publication_acknowledge`  tinyint(1) NULL AFTER `co_author`;