-- CHG-OTP-LOGIN-001-T01 -- storage and specificity catalog for the Center email one-time-code login.
--
-- 1. Three tables of throw-away authentication state. Their rows are short-lived and are never joined to
--    users, global_units or any other table, so they carry no foreign key and this migration assumes no
--    Global Unit, user or role row (design.md section 3, DA-001).
--      * otp_challenges   one row per code request, decoys included (DD-5); supersede reuses consumed_at (DD-6).
--      * otp_rate_limits  fixed-window counters shared by every application instance (DD-8, DA-003).
--      * otp_cooldowns    sliding minimum spacing between requests for one email (DD-8).
--    The hash and handle columns are compared byte for byte, so they use the utf8mb4_bin collation: the
--    default case-insensitive one would make two different base64url nonces collide on the UNIQUE index.
--
-- 2. The allow-list specificity 'crp_otp_allowed_email_domains' (DA-002, FN-001). One parameters row is
--    inserted for each of the Global Unit types 1, 2, 3 and 4 that EXISTS in global_unit_types, selected from
--    that table, so a database missing a type gets no row and no foreign key failure (parameters has an FK to
--    global_unit_types). A fixed-id VALUES insert would break there: migrations alone seed type 2 'Center', and
--    the later seed of type 4 is skipped when a type named 'center' already exists.
--    The flag ships OFF: default_value is empty and NO custom_parameters row is inserted. An empty or absent
--    list disables the feature for that Global Unit, so deploying this migration changes nobody's login.
--
-- Idempotent: CREATE TABLE IF NOT EXISTS, and the seed is guarded by WHERE NOT EXISTS (parameters has no unique
-- index on `key`, so an unguarded re-run would duplicate rows). The DDL comes first and the seed is a single
-- statement, so a failure leaves nothing dangling.

CREATE TABLE IF NOT EXISTS otp_challenges (
  id BIGINT NOT NULL AUTO_INCREMENT,
  nonce VARCHAR(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NOT NULL,
  email_hmac CHAR(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NOT NULL,
  global_unit_id BIGINT NOT NULL,
  code_hmac CHAR(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NOT NULL,
  expires_at DATETIME(3) NOT NULL,
  attempts TINYINT NOT NULL DEFAULT 0,
  consumed_at DATETIME(3) NULL DEFAULT NULL,
  created_at DATETIME(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3),
  PRIMARY KEY (id),
  UNIQUE KEY uq_otp_challenges_nonce (nonce),
  KEY idx_otp_challenges_email_hmac (email_hmac),
  KEY idx_otp_challenges_expires_at (expires_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- The window_start and last_request_at indexes serve the purge, which deletes by those columns.
CREATE TABLE IF NOT EXISTS otp_rate_limits (
  bucket CHAR(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NOT NULL,
  window_start DATETIME NOT NULL,
  hits INT NOT NULL,
  PRIMARY KEY (bucket, window_start),
  KEY idx_otp_rate_limits_window_start (window_start)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS otp_cooldowns (
  bucket CHAR(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NOT NULL,
  last_request_at DATETIME(3) NOT NULL,
  PRIMARY KEY (bucket),
  KEY idx_otp_cooldowns_last_request_at (last_request_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

INSERT INTO parameters (global_unit_type_id, `key`, `description`, `format`, default_value, category)
SELECT gut.id, 'crp_otp_allowed_email_domains',
  'Email domains allowed to sign in with a one-time code, comma separated. Empty means code sign-in is off for this Global Unit',
  4, '', 2
FROM global_unit_types gut
WHERE gut.id IN (1, 2, 3, 4)
AND NOT EXISTS (
  SELECT 1 FROM parameters existing_parameter
  WHERE existing_parameter.global_unit_type_id = gut.id
  AND existing_parameter.`key` = 'crp_otp_allowed_email_domains'
);
