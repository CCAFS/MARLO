CREATE table if not exists oc_workpackage_science_group  (
id bigint(20) NOT NULL AUTO_INCREMENT,
workpackage_id varchar(20) not null,
science_group_id bigint(20) NOT NULL,
PRIMARY KEY (id),
INDEX `oc_workpackage_science_group_id` (`id`) USING BTREE,
INDEX `oc_science_group_id` (`id`) USING BTREE,
CONSTRAINT `oc_science_group_id_id_ibfk_1` FOREIGN KEY (`science_group_id`) REFERENCES `oc_science_group` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE table if not exists oc_workpackage_impact_areas (
id bigint(20) NOT NULL AUTO_INCREMENT,
workpackage_id varchar(20) not null,
impact_area_id bigint(20) NOT NULL,
PRIMARY KEY (id),
INDEX `oc_workpackage_science_group_id` (`id`) USING BTREE,
INDEX `st_impact_area_id` (`impact_area_id`) USING BTREE,
CONSTRAINT `impact_area_id_ibfk_1` FOREIGN KEY (`impact_area_id`) REFERENCES `st_impact_areas` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE table if not exists oc_workpackage_sdg (
id bigint(20) NOT NULL AUTO_INCREMENT,
workpackage_id varchar(20) not null,
sdg_id bigint(20) NOT NULL,
PRIMARY KEY (id),
INDEX `oc_workpackage_science_group_id` (`id`) USING BTREE,
INDEX `sdg_id` (`sdg_id`) USING BTREE,
CONSTRAINT `sdg_id_ibfk_1` FOREIGN KEY (`sdg_id`) REFERENCES `sustainable_development_goals` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;