ALTER TABLE `crps_sites_integration` ADD CONSTRAINT `fk_crp_id_site_integration` FOREIGN KEY (`crp_id`) REFERENCES `crps` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT;
