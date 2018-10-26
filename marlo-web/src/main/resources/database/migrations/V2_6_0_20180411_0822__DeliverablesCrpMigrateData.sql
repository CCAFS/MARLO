/* IP Program -> Crp Program*/
UPDATE deliverable_crps dc
INNER JOIN ip_programs ip ON ip.id = dc.ip_program
INNER JOIN crp_programs cp ON cp.id = ip.crp_program_id
SET dc.crp_program = cp.id;

/* Crp Pandr -> GlobalUnit*/
UPDATE deliverable_crps dc SET dc.global_unit = CASE
    WHEN dc.crp_pandr = 1 THEN 5
    WHEN dc.crp_pandr = 2 THEN 8
    WHEN dc.crp_pandr = 3 THEN 1
    WHEN dc.crp_pandr = 4 THEN 9
    WHEN dc.crp_pandr = 5 THEN 10
    WHEN dc.crp_pandr = 6 THEN 11
    WHEN dc.crp_pandr = 7 THEN 12
    WHEN dc.crp_pandr = 8 THEN 13
    WHEN dc.crp_pandr = 9 THEN 7
    WHEN dc.crp_pandr = 10 THEN 3
    WHEN dc.crp_pandr = 11 THEN 22
    WHEN dc.crp_pandr = 12 THEN 16
    WHEN dc.crp_pandr = 13 THEN 17
    WHEN dc.crp_pandr = 14 THEN 4
    WHEN dc.crp_pandr = 15 THEN 21
    WHEN dc.crp_pandr = 16 THEN 20
    END
WHERE dc.crp_pandr  in (1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16);

/* Null global unit when crp program exists */

UPDATE deliverable_crps dc
SET dc.global_unit = NULL WHERE dc.global_unit IS NOT NULL AND dc.crp_program IS NOT NULL;

/* Delete old relations: ip_program, crp_pandr*/
ALTER TABLE `deliverable_crps` DROP FOREIGN KEY `deliverable_crps_ibfk_2`;

ALTER TABLE `deliverable_crps` DROP FOREIGN KEY `deliverable_crps_ibfk_3`;

ALTER TABLE `deliverable_crps`
DROP COLUMN `crp_pandr`,
DROP COLUMN `ip_program`,
DROP INDEX `deliverable_crps_ibfk_2`,
DROP INDEX `deliverable_crps_ibfk_3`;

