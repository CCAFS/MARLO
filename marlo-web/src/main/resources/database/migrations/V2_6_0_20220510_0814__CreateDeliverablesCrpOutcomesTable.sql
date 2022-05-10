CREATE TABLE `deliverable_crp_outcomes`  (
  `id` bigint(20) NOT NULL,
  `deliverable_id` bigint(20) NOT NULL,
  `crp_outcome_id` bigint(20) NOT NULL,
  `id_phase` bigint(20) NULL,
  PRIMARY KEY (`id`),
  INDEX `deliverable_id`(`deliverable_id`) USING BTREE,
  INDEX `crp_outcome_id`(`crp_outcome_id`) USING BTREE,
  INDEX `id_phase`(`id_phase`) USING BTREE,
  CONSTRAINT `deliverable_crp_outcome_ibfk_1` FOREIGN KEY (`deliverable_id`) REFERENCES `deliverables` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT,
  CONSTRAINT `deliverable_crp_outcome_ibfk_2` FOREIGN KEY (`crp_outcome_id`) REFERENCES `crp_program_outcomes` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT,
  CONSTRAINT `deliverable_crp_outcome_ibfk_3` FOREIGN KEY (`id_phase`) REFERENCES `phases` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_general_ci;