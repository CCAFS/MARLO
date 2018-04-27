SET FOREIGN_KEY_CHECKS = 0;

-- deliverable_metadata_elements

INSERT INTO deliverable_metadata_elements (
deliverable_id,
element_id,
element_value,
hide,
id_phase
)
SELECT
  dme.deliverable_id,
  dme.element_id,
  dme.element_value,
  dme.hide,
  di.id_phase 
FROM
  deliverable_metadata_elements AS dme
  INNER JOIN deliverables AS d ON dme.deliverable_id = d.id 
  AND d.is_publication = 1
  INNER JOIN deliverables_info AS di ON di.deliverable_id = d.id
  INNER JOIN phases AS p ON di.id_phase = p.id;
  
DELETE dme.* FROM deliverable_metadata_elements dme
INNER JOIN deliverables AS d ON dme.deliverable_id = d.id 
  AND d.is_publication = 1
WHERE dme.id_phase IS NULL;

-- deliverable_dissemination

INSERT INTO deliverable_dissemination (
deliverable_id,
id_phase,
is_open_access,
intellectual_property,
limited_exclusivity,
restricted_use_agreement,
restricted_access_until,
effective_date_restriction,
restricted_embargoed,
not_disseminated,
already_disseminated,
dissemination_channel,
dissemination_URL,
dissemination_channel_name,
synced
)
SELECT
  dd.deliverable_id,
  dd.id_phase,
  dd.is_open_access,
  dd.intellectual_property,
  dd.limited_exclusivity,
  dd.restricted_use_agreement,
  dd.restricted_access_until,
  dd.effective_date_restriction,
  dd.restricted_embargoed,
  dd.not_disseminated,
  dd.already_disseminated,
  dd.dissemination_channel,
  dd.dissemination_URL,
  dd.dissemination_channel_name,
  dd.synced 
FROM
  deliverable_dissemination AS dd
  INNER JOIN deliverables AS d ON dd.deliverable_id = d.id 
  AND d.is_publication = 1
  INNER JOIN deliverables_info AS di ON di.deliverable_id = d.id
  INNER JOIN phases AS p ON di.id_phase = p.id;
  
DELETE  dd.* FROM deliverable_dissemination dd
INNER JOIN deliverables AS d ON dd.deliverable_id = d.id 
  AND d.is_publication = 1
WHERE dd.id_phase IS NULL;

-- deliverable_publications_metada

INSERT INTO deliverable_publications_metada (
deliverable_id,
id_phase,
volume,
issue,
pages,
journal,
isi_publication,
nasr,
co_author,
publication_acknowledge
)
SELECT
dpm.deliverable_id,
dpm.id_phase,
dpm.volume,
dpm.issue,
dpm.pages,
dpm.journal,
dpm.isi_publication,
dpm.nasr,
dpm.co_author,
dpm.publication_acknowledge 
FROM
  deliverable_publications_metada AS dpm
  INNER JOIN deliverables AS d ON dpm.deliverable_id = d.id 
  AND d.is_publication = 1
  INNER JOIN deliverables_info AS di ON di.deliverable_id = d.id
  INNER JOIN phases AS p ON di.id_phase = p.id;
  
DELETE  dpm.* FROM deliverable_publications_metada dpm
INNER JOIN deliverables AS d ON dpm.deliverable_id = d.id 
  AND d.is_publication = 1
WHERE dpm.id_phase IS NULL;

-- deliverable_crps

INSERT INTO deliverable_crps (
deliverable_id,
id_phase,
global_unit,
crp_program
)
SELECT
dc.deliverable_id,
dc.id_phase,
dc.global_unit,
dc.crp_program
FROM
  deliverable_crps AS dc
  INNER JOIN deliverables AS d ON dc.deliverable_id = d.id 
  AND d.is_publication = 1
  INNER JOIN deliverables_info AS di ON di.deliverable_id = d.id
  INNER JOIN phases AS p ON di.id_phase = p.id;
  
DELETE  dc.* FROM
  deliverable_crps dc
  INNER JOIN deliverables AS d ON dc.deliverable_id = d.id 
  AND d.is_publication = 1 
WHERE
  dc.id_phase IS NULL;
  
-- deliverable_data_sharing_file

INSERT INTO deliverable_data_sharing_file (
deliverable_id,
id_phase,
type_id,
file_id,
external_file
)
SELECT
dsf.deliverable_id,
dsf.id_phase,
dsf.type_id,
dsf.file_id,
dsf.external_file
FROM
  deliverable_data_sharing_file AS dsf
  INNER JOIN deliverables AS d ON dsf.deliverable_id = d.id 
  AND d.is_publication = 1
  INNER JOIN deliverables_info AS di ON di.deliverable_id = d.id
  INNER JOIN phases AS p ON di.id_phase = p.id;
  
DELETE  dsf.* FROM
  deliverable_data_sharing_file dsf
  INNER JOIN deliverables AS d ON dsf.deliverable_id = d.id 
  AND d.is_publication = 1 
WHERE
  dsf.id_phase IS NULL;