CREATE TABLE `user_roles` (
`id`  bigint NOT NULL AUTO_INCREMENT ,
`user_id`  bigint NOT NULL ,
`role_id`  bigint NOT NULL ,
PRIMARY KEY (`id`),
FOREIGN KEY (`user_id`) REFERENCES `users` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
FOREIGN KEY (`role_id`) REFERENCES `roles` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
)
;

