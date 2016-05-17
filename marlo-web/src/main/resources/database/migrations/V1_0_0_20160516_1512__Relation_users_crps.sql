CREATE TABLE `crp_users` (
`id`  bigint NOT NULL ,
`user_id`  bigint NOT NULL ,
`crp_id`  bigint NOT NULL ,
PRIMARY KEY (`id`),
FOREIGN KEY (`user_id`) REFERENCES `users` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
FOREIGN KEY (`crp_id`) REFERENCES `crps` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
)
;

