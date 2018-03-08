ALTER TABLE `submissions` ADD CONSTRAINT `submissions_ibfk_5` FOREIGN KEY (`powb_synthesis_id`) REFERENCES `powb_synthesis` (`id`);
