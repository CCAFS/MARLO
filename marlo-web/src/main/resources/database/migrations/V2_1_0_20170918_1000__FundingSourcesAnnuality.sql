SET FOREIGN_KEY_CHECKS = 0;
/* CREATE: funding sources info table*/
DROP TABLE
IF EXISTS `funding_sources_info`;

CREATE TABLE `funding_sources_info` (
  `id` BIGINT (20) NOT NULL AUTO_INCREMENT,
  `title` text CHARACTER
SET utf8 COLLATE utf8_general_ci NULL,
 `description` text CHARACTER
SET utf8 COLLATE utf8_general_ci NULL,
 `start_date` date DEFAULT NULL,
 `end_date` date DEFAULT NULL,
 `finance_code` text CHARACTER
SET utf8 COLLATE utf8_general_ci NULL,
 `contact_person_name` text CHARACTER
SET utf8 COLLATE utf8_general_ci NULL,
 `contact_person_email` text CHARACTER
SET utf8 COLLATE utf8_general_ci NULL,
 `file` BIGINT (20) DEFAULT NULL,
 `donor` BIGINT (20) DEFAULT NULL,
 direct_donor BIGINT (20) DEFAULT NULL,
 `status` INT (11) NULL DEFAULT NULL,
 `type` BIGINT (20) DEFAULT NULL,
 `division_id` BIGINT (20) DEFAULT NULL,
 `modified_by` BIGINT (20) NOT NULL,
 `modification_justification` text CHARACTER
SET utf8 COLLATE utf8_general_ci NOT NULL,
 `global` TINYINT (1) DEFAULT '0',
 `w1w2` TINYINT (1) DEFAULT '0',
 `id_phase` BIGINT (20) NOT NULL,
 `funding_source_id` BIGINT (20) NOT NULL,
 `sync` tinyint(1) DEFAULT NULL,
  `extended_date` date DEFAULT NULL,
  `syn_date` date DEFAULT NULL,
 PRIMARY KEY (`id`),
 FOREIGN KEY (`funding_source_id`) REFERENCES `funding_sources` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT,
 FOREIGN KEY (`id_phase`) REFERENCES `phases` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT,
 FOREIGN KEY (`division_id`) REFERENCES `partner_divisions` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT,
 FOREIGN KEY (`donor`) REFERENCES `institutions` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT,
  FOREIGN KEY (`direct_donor`) REFERENCES `institutions` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT,

 FOREIGN KEY (`modified_by`) REFERENCES `users` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT,
 FOREIGN KEY (`type`) REFERENCES `budget_types` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT,
 FOREIGN KEY (`file`) REFERENCES `files` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT
);

/*INSERT: Current funding_sources records to  'funding_sources_info' */
INSERT INTO `funding_sources_info` (
  funding_source_id,
  title,
  description,
  start_date,
  end_date,
  finance_code,
  contact_person_name,
  contact_person_email,
  file,
  donor,direct_donor,
  `status`,
  type,
  division_id,
  modified_by,
  modification_justification,
  `global`,
  w1w2,
  id_phase,
  sync,
  extended_date,
  syn_date
) SELECT
  fs.id,
  fs.title,
  fs.description,
  fs.start_date,
  fs.end_date,
  fs.finance_code,
  fs.contact_person_name,
  fs.contact_person_email,
  fs.file,
  fs.donor,fs.direct_donor,
  fs.`status`,
  fs.type,
  fs.division_id,
  fs.modified_by,
  fs.modification_justification,
  fs.`global`,
  fs.w1w2,
  p.id,
   fs.sync,
  fs.extended_date,
  fs.syn_date
FROM
  funding_sources fs
INNER JOIN phases p ON fs.crp_id = p.crp_id
AND fs.end_date IS NOT NULL
AND YEAR (fs.end_date) >= p.`year`
UNION
  SELECT
    fs.id,
    fs.title,
    fs.description,
    fs.start_date,
    fs.end_date,
    fs.finance_code,
    fs.contact_person_name,
    fs.contact_person_email,
    fs.file,
    fs.donor,fs.direct_donor,
    fs.`status`,
    fs.type,
    fs.division_id,
    fs.modified_by,
    fs.modification_justification,
    fs.`global`,
    fs.w1w2,
    p.id,   fs.sync,
  fs.extended_date,
  fs.syn_date
  FROM
    funding_sources fs
  INNER JOIN phases p ON fs.crp_id = p.crp_id
  AND (
    fs.end_date IS NULL
    OR YEAR (fs.end_date) < 2016
  )
  AND (
    p.`year` = 2017
    AND p.description = 'Planning'
  );

/*DROP: Foreign keys*/
/*division_id -> partner_divisions*/
ALTER TABLE `funding_sources` DROP FOREIGN KEY `funding_sources_division_id_fk`;

/*donor -> institutions*/
ALTER TABLE `funding_sources` DROP FOREIGN KEY `funding_sources_ibfk_2`;

/*modified_by -> users*/
ALTER TABLE `funding_sources` DROP FOREIGN KEY `funding_sources_ibfk_4`;

/*type -> budget_types*/
ALTER TABLE `funding_sources` DROP FOREIGN KEY `funding_sources_ibfk_6`;

/*file -> files*/
ALTER TABLE `funding_sources` DROP FOREIGN KEY `funding_sources_ibfk_8`;

/* DROP: remaining columns*/
ALTER TABLE `funding_sources`
 DROP COLUMN `title`,
 DROP COLUMN `description`,
 DROP COLUMN `start_date`,
 DROP COLUMN `end_date`,
 DROP COLUMN `finance_code`,
 DROP COLUMN `contact_person_name`,
 DROP COLUMN `contact_person_email`,
 DROP COLUMN `file`,
 DROP COLUMN `donor`,
  DROP COLUMN `direct_donor`,

 DROP COLUMN `status`,
 DROP COLUMN `type`,
 DROP COLUMN `division_id`,
 DROP COLUMN `modification_justification`,
 DROP COLUMN `global`,
 DROP COLUMN sync,
 DROP COLUMN extended_date,
 DROP COLUMN syn_date,
 DROP COLUMN `w1w2`;