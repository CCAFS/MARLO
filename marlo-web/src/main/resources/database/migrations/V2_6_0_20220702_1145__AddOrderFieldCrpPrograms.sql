ALTER TABLE `crp_programs`
ADD COLUMN `order_index`  int(2) NULL DEFAULT 0 AFTER `global_unit_id`;

update crp_programs set order_index = 0 where id = 186;
update crp_programs set order_index = 1 where id = 180;
update crp_programs set order_index = 2 where id = 181;
update crp_programs set order_index = 3 where id = 182;