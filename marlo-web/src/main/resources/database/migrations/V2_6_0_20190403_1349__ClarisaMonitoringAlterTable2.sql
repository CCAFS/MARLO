ALTER TABLE `clarisa_monitoring`
ADD COLUMN `loc_element_id`  bigint(20) NULL AFTER `user_ip`;

ALTER TABLE `clarisa_monitoring` ADD FOREIGN KEY (`user_id`) REFERENCES `users` (`id`);

ALTER TABLE `clarisa_monitoring` ADD FOREIGN KEY (`loc_element_id`) REFERENCES `loc_elements` (`id`);

ALTER TABLE `clarisa_monitoring` ADD FOREIGN KEY (`global_unit_id`) REFERENCES `global_units` (`id`);

