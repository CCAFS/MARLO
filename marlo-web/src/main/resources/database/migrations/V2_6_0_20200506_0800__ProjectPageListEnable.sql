create table project_webpage (
`id`  bigint(20) NOT NULL AUTO_INCREMENT ,
`project_id` bigint(20) DEFAULT NULL,
`is_active` tinyint(1) NOT NULL DEFAULT '1',
PRIMARY KEY (`id`),
KEY `project_webpage_ibfk_1` (`project_id`),
CONSTRAINT `project_webpage_ibfk_1` FOREIGN KEY (`project_id`) REFERENCES `projects` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) DEFAULT CHARSET=utf8;