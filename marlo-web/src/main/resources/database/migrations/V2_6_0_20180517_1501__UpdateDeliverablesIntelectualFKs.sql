ALTER TABLE `deliverable_intellectual_assets` DROP FOREIGN KEY `intellectual_assets_ibfk_1`;
ALTER TABLE `deliverable_intellectual_assets` DROP FOREIGN KEY `intellectual_assets_ibfk_2`;
ALTER TABLE `deliverable_intellectual_assets` ADD FOREIGN KEY (`deliverable`) REFERENCES `deliverables` (`id`);
ALTER TABLE `deliverable_intellectual_assets` ADD FOREIGN KEY (`phase`) REFERENCES `phases` (`id`);