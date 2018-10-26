ALTER TABLE `project_expected_studies`
ADD COLUMN `stament`  text NULL AFTER `comments`,
ADD COLUMN `stage`  bigint(20) NULL AFTER `stament`,
ADD COLUMN `elaboration`  text NULL AFTER `stage`,
ADD COLUMN `references_cited`  text NULL AFTER `elaboration`,
ADD COLUMN `quantification`  text NULL AFTER `references_cited`,
ADD COLUMN `gender_relevance`  int NULL AFTER `quantification`,
ADD COLUMN `gender_describe`  text NULL AFTER `gender_relevance`,
ADD COLUMN `youth_relevance`  int NULL AFTER `modification_justification`,
ADD COLUMN `youth_describe`  text NULL AFTER `youth_relevance`,
ADD COLUMN `capdev_relevance`  int NULL AFTER `youth_describe`,
ADD COLUMN `capdev_describe`  text NULL AFTER `capdev_relevance`;

