DELETE t1 FROM funding_source_institutions t1
INNER JOIN funding_source_institutions t2
WHERE t1.id < t2.id 
AND t1.funding_source_id = t2.funding_source_id
AND t1.institution_id = t2.institution_id
AND t1.id_phase = t2.id_phase;

INSERT INTO `funding_source_institutions`
(`funding_source_id`,
`institution_id`,
`id_phase`)
VALUES
(2240,
2076,
46);

INSERT INTO `funding_source_institutions`
(`funding_source_id`,
`institution_id`,
`id_phase`)
VALUES
(2243,
2076,
46);

INSERT INTO `funding_source_institutions`
(`funding_source_id`,
`institution_id`,
`id_phase`)
VALUES
(2244,
2076,
46);

