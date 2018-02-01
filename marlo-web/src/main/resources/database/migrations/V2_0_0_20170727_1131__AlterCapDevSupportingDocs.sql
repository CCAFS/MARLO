ALTER TABLE `capdev_supporting_docs` 
CHANGE COLUMN `publication_date` `publication_date` date NULL DEFAULT NULL ,
CHANGE COLUMN `active_since` `active_since` date NULL DEFAULT NULL ;




ALTER TABLE `capdev_supp_docs_documents` 
CHANGE COLUMN `active_since` `active_since` DATE NULL DEFAULT NULL ;