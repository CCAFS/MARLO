ALTER TABLE `phases`
ADD COLUMN `next_phase`  bigint(20) NULL AFTER `visible`;

ALTER TABLE `phases` ADD FOREIGN KEY (`next_phase`) REFERENCES `phases` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT;

update  phases p
INNER JOIN phases p2 on  p2.crp_id=p.crp_id and p2.description='Reporting' and p2.`year`=p.`year`
set p.next_phase= p2.id where p.description='Planning';

update  phases p
INNER JOIN phases p2 on  p2.crp_id=p.crp_id and p2.description='Planning' and p2.`year`=(p.`year`+1)
set p.next_phase= p2.id where p.description='Reporting';


