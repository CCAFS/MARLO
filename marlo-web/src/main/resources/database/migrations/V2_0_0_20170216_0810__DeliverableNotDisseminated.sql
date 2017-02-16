ALTER TABLE `deliverable_dissemination`
ADD COLUMN `not_disseminated`  bit(1) NULL AFTER `restricted_embargoed`;

UPDATE deliverable_dissemination set not_disseminated=0;