DELETE FROM powb_cross_cutting_dimensions;

ALTER TABLE `powb_cross_cutting_dimensions`
MODIFY COLUMN `id`  bigint(20) NOT NULL FIRST ;

ALTER TABLE `powb_cross_cutting_dimensions` ADD CONSTRAINT `cross_cutting_fk_synthesis` FOREIGN KEY (`id`) REFERENCES `powb_synthesis` (`id`);