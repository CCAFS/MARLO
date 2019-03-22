INSERT INTO permissions (permission, description, type)
VALUES ('crp:{0}:reportSynthesis:{1}:submit', 'Can edit in Annual Report Synthesis Srf Progress', 3);
--
INSERT INTO role_permissions (role_id, permission_id)
SELECT r.id, p.id FROM roles r, permissions p
WHERE 
r.acronym in( 'FPL','PMU')
AND
p.permission = 'crp:{0}:reportSynthesis:{1}:submit';




ALTER TABLE `submissions`
ADD COLUMN `report_synthesis_id`  bigint(20) NULL AFTER `powb_synthesis_id`;

ALTER TABLE `submissions` ADD FOREIGN KEY (`report_synthesis_id`) REFERENCES `report_synthesis` (`id`);
