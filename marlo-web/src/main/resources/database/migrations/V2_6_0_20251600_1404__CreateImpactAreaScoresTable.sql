CREATE TABLE impact_area_scores (
  id bigint(20) auto_increment NOT NULL PRIMARY KEY,
  description text CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL,
  complete_description text CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NULL
)
ENGINE=InnoDB
DEFAULT CHARSET=utf8mb4
COLLATE=utf8mb4_0900_ai_ci;