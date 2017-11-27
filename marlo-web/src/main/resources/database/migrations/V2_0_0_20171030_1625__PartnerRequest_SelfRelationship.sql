ALTER TABLE `partner_requests`
ADD COLUMN `partner_request_id` bigint(20) NULL AFTER `crp_id`,
ADD COLUMN `is_modified` tinyint(1) NULL AFTER `partner_request_id`,
ADD COLUMN `acepted_date`  timestamp NULL AFTER `acepted`,
ADD COLUMN `rejected_date`  timestamp NULL AFTER `modification_justification`;

ALTER TABLE `partner_requests` ADD CONSTRAINT `p_request_request_id` FOREIGN KEY (`partner_request_id`) REFERENCES `partner_requests` (`id`);

--Replicate database existing request
CREATE TEMPORARY TABLE tmp SELECT * from partner_requests WHERE is_active=1 AND is_office=0 AND partner_request_id IS NULL;
UPDATE tmp SET partner_request_id=tmp.id;

INSERT INTO partner_requests 
(partner_name, acronym, institution_type_id, loc_element_id, web_page,is_office,institution_id,request_source
,crp_id,partner_request_id, is_modified,acepted,acepted_date,is_active,active_since,created_by,modified_by
,modification_justification,rejected_date,rejected_by,reject_justification) 
SELECT tmp.partner_name,tmp.acronym,tmp.institution_type_id,tmp.loc_element_id,tmp.web_page,tmp.is_office,tmp.institution_id,tmp.request_source
,tmp.crp_id,tmp.partner_request_id,tmp. is_modified,tmp.acepted,tmp.acepted_date,tmp.is_active,tmp.active_since,tmp.created_by,tmp.modified_by
,tmp.modification_justification,tmp.rejected_date,tmp.rejected_by,tmp.reject_justification FROM tmp;
DROP TABLE tmp;