# Center Email OTP Login — Execution Log

## Document Control

| Item | Value |
|---|---|
| Spec ID | `CHG-OTP-LOGIN-001` |
| Spec path | `docs/specs/changes/center-email-otp-login` |
| Branch | `A2-2631-Implement-Center-Email-OTP-Authentication` (0 commits behind `origin/staging` at start) |
| Approval Mode | `gated` |
| Leader | Claude Code, `opus` (T1) |
| Implementer / Reviewer | `akili-implementer` (`sonnet`, T2) / `akili-reviewer` (`opus`, T3) |
| Started | 2026-10-09 |
| Budget (design §18) | 16 tasks · ~2,300 LOC · 23 review rounds |

---

## Task Execution History

### CHG-OTP-LOGIN-001-T01 — Migration: tables and catalog seed

- **Status:** PASS (attempt 1, two continuations; first-attempt blocker escalated and resolved by user decisions)
- **Date:** 2026-10-09
- **Requirements:** DA-001, DA-002, DA-003, FN-001
- **Skills assigned:** `marlo-migration`, `marlo-verify` (as listed in the task). Effort `high`.

#### Attempt 1

- **Files changed:** `marlo-web/src/main/resources/database/migrations/V2_6_0_20261009_1025__AddEmailOtpLogin.sql` (new; timestamp from `date`).
- **Verification on `otp_t01_copy` (dump of `aiccradb1`):**
  - Run 1: 3 OTP tables created; `parameters` 295 → 299 (one row each for types 1–4, format 4, category 2, `default_value` '').
  - Run 2: no error; still 299 rows.
  - `custom_parameters` rows for the key: 0 after both runs.
  - `SHOW CREATE TABLE`: matches design §3 (`uq_otp_challenges_nonce`, `idx_otp_challenges_email_hmac`, `idx_otp_challenges_expires_at`, PK `(bucket, window_start)`). `utf8mb4_bin` on nonce, HMAC and bucket columns; `Ab`/`aB` nonces inserted as two rows.
- **Falsifier (synthetic database `otp_t01_synth`, types 1–3 only, real `parameters` FK):**
  - Fixed-id insert for type 4: `ERROR 1452 (23000) ... CONSTRAINT parameters_global_unit_id_fk FOREIGN KEY (global_unit_type_id) REFERENCES global_unit_types (id)`.
  - The migration ran twice without error and seeded one row each for types 1, 2 and 3.
- **P-23:** on the `aiccradb1` copy, types 1–5 exist (2 is "Old Center", inactive); 1–4 seeded. On a clean database: **not measured**. From source, `V2_5_0_20180320_0915` inserts id 4 unconditionally, which contradicts the P-23 concern.
- **Blocker (reported verbatim):**
  - A migrations-only build fails at Flyway file #1: `V2_0_0_20161004_1347__UserPermission.sql` — `ERROR 1146 (42S02) at line 2: Table 'otp_t01_clean.users' doesn't exist`. The base schema is in `database/v1`, which Flyway does not run.
  - v1 + migrations through the `mysql` client fails at v1 file #15: `v1/V1_0_0_20160523_1120__MigrateRolesCCFAS.sql`, `ERROR 1064 (42000) at line 19: ... near '-\nINSERT INTO role_permissions VALUES ('1','1','1')'`.
- **Not Done / Assumptions (verbatim from the report):** "The clean-database evidence is BLOCKED, so the disqualifier applies and that part is inconclusive." Judgment calls: type 2 seeded although inactive on `aiccradb1`; `default_value` '' where existing format-4 rows use NULL; description text authored by the Implementer.
- **Throwaway databases:** all dropped; only the original schemas remain.
- **Evidence re-run:** not run. The task is not complete, so no re-run and no Reviewer yet.
- **runtime events:** none.
- **spawns:** implementer 22 calls, 98,845 tokens, ended partial.

#### Leader notes

- The Implementer cited `requirements.md` line 366 ("DA-002 seeds only existing types 1, 3, 4") as a spec conflict. That line is a historical decision-log entry, superseded by the later entry "DA-002 includes type 2" and by DA-002 itself. **No conflict, no edit.**
- Leader hypothesis for the v1 failure: the line `---` is a comment for Flyway but not for the `mysql` client, which needs `-- ` with a trailing space. Running the same files through Flyway's own parser (flyway-core 4.0.1 is in `~/.m2`) may build the clean database. Not yet probed.

#### Attempt 1 — continuation 1 (user-approved: clean database through Flyway's parser)

- **Route:** a scratchpad Java runner with flyway-core 4.0.1 and mysql-connector-j 8.4.0, locations `database/v1` + `database/migrations`, MARLO's `$[ ]` placeholders, no baseline (MARLO's 2.0 baseline would skip v1), on an empty throwaway `otp_t01_clean`.
- **Leader hypothesis confirmed:** `V1_0_0_20160523_1120__MigrateRolesCCFAS.sql` passes under Flyway; the earlier failure came from the `mysql` client.
- **New blocker (verbatim):**
  ```
  Migration V1_0_0_20160615_0901__Institutions.sql failed
  SQL State  : 22001
  Error Code : 1292
  Message    : Data truncation: Incorrect datetime value: '0000-00-00 00:00:00' for column 'added' at row 1
  Line       : 30
  ```
  Cause per the Implementer: the server's default MySQL 8 `sql_mode` (`STRICT_TRANS_TABLES`, `NO_ZERO_DATE`) rejects the legacy zero dates. Nothing in the repo relaxes it.
- **Not Done:** P-23 types, `parameters` SELECT, `custom_parameters` count, second run and falsifier on the clean database were not run. No other migration edited; `otp_t01_clean` dropped.
- **Proposed next step (Implementer):** rerun with the runner's session `sql_mode=''` through the JDBC URL (`sessionVariables=sql_mode=''`). This changes no repo file and no server setting.
- **continuations:** 1 (clean-database evidence)
- **spawns:** implementer 6 calls, tokens not reported separately by host (cumulative 107,216), ended partial.

#### Attempt 1 — continuation 2 (user-approved: session `sql_mode=''` on the runner only)

- `V1_0_0_20160615_0901__Institutions.sql` now passes; 158 migrations applied before the next failure.
- **Blocker (verbatim):**
  ```
  Migration V1_0_0_20160819_0951__Project_types.sql failed
  SQL State  : 42S21
  Error Code : 1060
  Message    : Duplicate column name 'is_regional'
  ```
  The legacy v1 history conflicts with itself. No other migration edited; `otp_t01_clean` dropped.
- **continuations:** 2 (clean-database evidence)
- **spawns:** implementer 2 calls, tokens not reported separately by host (cumulative 110,461), ended partial.

#### Leader decision — clean-database evidence (user decision, recorded before continuation 2)

The user decided in advance: if the `sql_mode=''` rerun also failed, the synthetic database is accepted as the clean-database evidence and T01 goes to review. **Execute-time spec edit:** `tasks.md` T01 *Disqualifier* amended to record this, and the *P-23 outcome* line added. Neither changes an approved requirement: DA-001 and AC-17 ask for safe re-application and for a database without Global Units 45/47, and both are still evidenced. Legacy-history finding for the user: MARLO's schema cannot be rebuilt from `database/v1` + `database/migrations` on MySQL 8. Recorded only; out of this spec's scope.

#### Evidence re-run (Leader inline, non-author) — VERIFIED

- Throwaway `otp_v_copy` (mysqldump of `aiccradb1`, 616 tables, types 1–5), migration applied twice with the `mysql` client:
  - run 1 exit 0: key rows 4 (types 1,2,3,4), `custom_parameters` for key 0, `parameters` 299;
  - run 2 exit 0: identical (4 · 1,2,3,4 · 0 · 299);
  - `otp_challenges`, `otp_cooldowns`, `otp_rate_limits` present.
- Synthetic `otp_v_syn` (real `global_unit_types` + `parameters` DDL from `aiccradb1`, types 1–3 only, `@@foreign_key_checks`=1):
  - fixed-id type 4: `ERROR 1452 (23000) at line 1: Cannot add or update a child row: a foreign key constraint fails (`otp_v_syn`.`parameters`, CONSTRAINT `parameters_global_unit_id_fk` FOREIGN KEY (`global_unit_type_id`) REFERENCES `global_unit_types` (`id`))`;
  - migration run 1 exit 0 → 3 rows (1,2,3); run 2 exit 0 → 3 rows (1,2,3).
- Both throwaway schemas dropped. Outputs match the Implementer's report.

#### Reviewer verdict — attempt 1: `PASS` (`akili-reviewer`, `opus`; Implementer `sonnet`)

- **Summary:** the single file matches design §3 column for column, including the indexes, the composite PK, no FK on `global_unit_id`, and `utf8mb4_bin` on the hash and handle columns. The seed matches DA-002: the existing types among 1–4, format 4, category 2, empty default, no `custom_parameters` row. The filename follows Hard rule 5 and sorts last. `INSERT … SELECT` with `NOT EXISTS` on the same table is valid MySQL.
- **Named check (Disqualifier amendment):** it changes no approved requirement's meaning. The synthetic database tests DA-001's no-assumed-rows rule more strictly, DA-002's missing-type branch, and AC-17's database without Global Units 45/47. Residual gap: the synthetic DDL was copied from `aiccradb1`, not built from migrations. Low risk.
- **Type 2:** "exists" in DA-002 does not mean "active". Seeding inactive type 2 conforms; type 5 correctly gets no row.
- **ADVISORY (recorded, not gating):**
  - *Reliability:* `default_value` '' versus NULL. T03 `allowListFor` must treat NULL, ''/whitespace-only, and no catalog row (type 5) all as "off"; add a test case for each. **Forward pointer to T03.**
  - *Risk:* design.md §17 row P-23 still reads `UNVERIFIED` and cites only `V2_6_0_20260409_1645`. The outcome is recorded in tasks.md T01; the ledger row is left for `/akili-validate` or `/akili-archive`.
  - *Readability:* requirements.md Decision Log line "DA-002 seeds only existing types 1, 3, 4" is superseded history. A future closure sweep should also search for `1, 3, 4`.

#### T01 closing record

- **Final status:** PASS
- **Attempts:** 1 (two continuations; checkpoints: none)
- **continuations:** 2 (clean-database evidence; clean-database evidence with relaxed `sql_mode`)
- **spawns:** implementer 30 calls, 110,461 tokens cumulative, ended partial (resolved by user decision); reviewer 17 calls, 53,789 tokens, ended complete
- **Requirements covered:** DA-001, DA-002 (seed; constants are T02), DA-003 (storage), FN-001 (seed off)
- **Decisions made:** Disqualifier amendment and P-23 outcome line in `tasks.md` T01 (user decision, 2026-10-09)
- **Issues encountered:** MARLO's schema cannot be rebuilt from `database/v1` + `database/migrations` on MySQL 8 (three legacy breaks). Out of scope; recorded for the user.
- **Final verification:** Leader evidence re-run VERIFIED (see above)
- **Forward pointers:** T03 — the '' / NULL / missing-row allow-list cases (Reviewer advisory)

#### T01 reopened — user-approved design amendment (2026-10-09)

- **Trigger:** after the PASS, before the T01 commit, the user asked whether the design was optimal. The Leader named a gap: the purge deletes `otp_rate_limits` by `window_start` and `otp_cooldowns` by `last_request_at`, and no index serves either (the `otp_rate_limits` PK leads with `bucket`). The user approved adding both indexes.
- **Execute-time spec edit:** design.md §3 now adds `idx_otp_rate_limits_window_start (window_start)` and `idx_otp_cooldowns_last_request_at (last_request_at)`. No approved requirement changes meaning: DA-001 and DA-003 are unaffected, and OPS-003 is better served.
- **Not a FAIL:** the earlier PASS stands for the original scope. Attempt 2 is a user-approved scope amendment, consumes no rework attempt and gets a full review.
- `tasks.md` T01 is back to `[~]` until the amendment passes review.
- **Forward pointer to T03/T04 (Leader, from the same review of the design):** use one time source for `expires_at`, the cooldown and `window_start` writes and their comparisons. Either the database `NOW(3)` everywhere, or a Java-supplied UTC `:now` everywhere, never mixed. A mismatch between application and database time zones shifts expiry by hours.

#### Attempt 2 — user-approved index amendment

- **Files changed:** the same migration file (still uncommitted). It adds `KEY idx_otp_rate_limits_window_start (window_start)`, `KEY idx_otp_cooldowns_last_request_at (last_request_at)` and one comment line.
- **Implementer verification (fresh `aiccradb1` copy):** applied twice with no error; key rows 4; `custom_parameters` 0. `SHOW CREATE TABLE` shows both keys. `EXPLAIN DELETE` by `window_start` and by `last_request_at` picks each new index (key_len 5 / 7). The tables were empty, so this proves usability, not behaviour under load.
- **Evidence re-run (Leader inline, non-author): VERIFIED.** Fresh copy: run 1 and run 2 exit 0, 4 rows (types 1,2,3,4). `SHOW INDEX` lists both new keys beside the PKs. Both `EXPLAIN DELETE` statements use the new key. Throwaway schema dropped.
- **Reviewer verdict: `PASS`** (`akili-reviewer`, `opus`). The migration matches §3 as amended exactly; the rest of the file is unchanged from the attempt-1 PASS. No approved requirement changes meaning: DA-001 (no FK, still idempotent), DA-003 (shared table), OPS-003 (purge cadence and retention unchanged). Issues: none.
  - Reviewer note: a database that already applied the attempt-1 file would not get the indexes, because of `IF NOT EXISTS`, and Flyway would report a checksum mismatch. Leader check: the attempt-1 file was applied only to throwaway schemas, all dropped. The live local `aiccradb_actsave` never ran it, so the note has no target.
- **runtime events:** none.
- **spawns:** implementer 4 calls (cumulative 114,722 tokens), ended complete; reviewer 8 calls, 38,408 tokens, ended complete.

#### T01 closing record (supersedes the attempt-1 closing record)

- **Final status:** PASS (attempt 2, user-approved amendment)
- **Requirements covered:** DA-001, DA-002 (seed), DA-003 (storage), FN-001 (seed off); OPS-003 purge supported by the indexes
- **Decisions made:** the T01 Disqualifier amendment and P-23 outcome line in `tasks.md`; the design.md §3 index amendment (both user decisions, 2026-10-09)
- **Forward pointers:** T03 handles the `''`, NULL and missing-row allow-list cases. T03/T04 use one time source for every `DATETIME` write and comparison.

### CHG-OTP-LOGIN-001-T02 — Constants, configuration and `OtpKeys`

- **Status:** PASS (attempt 1)
- **Date:** 2026-10-09
- **Requirements:** SEC-008, DA-002 (constants in both files), NF-002
- **Skills assigned:** `marlo-verify`, `tdd` (as listed). Effort `high`. Review: lens checklist; override (f) applies (secret handling).

#### Attempt 1

- **Files changed:**
  - `marlo-utils/.../utils/APConfig.java` (CRLF kept): `otp.hmac.secret`, `otp.trusted.proxies` and `otp.smtp.timeout.ms` as `${key:}`, with getters.
  - `marlo-data/.../config/APConstants.java`: adds `OTP_ALLOWED_EMAIL_DOMAINS`.
  - `marlo-web/.../config/APConstants.java`: adds `OTP_ALLOWED_EMAIL_DOMAINS` and `OTP_PENDING_CHALLENGE = "otpPendingChallenge"`.
  - `marlo-web/.../security/otp/OtpKeys.java` (new).
  - `marlo-web/src/test/.../security/otp/OtpKeysTest.java` (new).
- **P-18: settled — the environment variable reaches the getter.** Route: `./scripts/run-marlo-java17.sh` with `OTP_HMAC_SECRET` exported (`openssl rand -base64 32`), nothing in `marlo-dev.properties`. Getter length 44 with the variable, 0 without. The temporary probe (an `InitializingBean` plus `System.out` in `APConfig`) was removed; `APConfig` was restored from a clean copy. The run applied the T01 migration to the local `aiccradb_actsave`, as approved. The app was left stopped.
- **Red run:** `OtpKeysTest.java:[72,56] cannot find symbol  symbol: class OtpKeys`.
- **Green:** `Tests run: 13, Failures: 0, Errors: 0, Skipped: 0`.
- **Falsifier (length floor 16 bytes):** `Failures: 3`, including `thirtyOneDecodedBytesIsUnconfigured`. Floor restored to 32.
- **Compile:** exit 0; 6 / 2405 / 1051 / 57 files. **Checkstyle:** HEAD 9, tree 9, delta 0 (pre-existing `APConfig` method names).
- **Consumers:** `grep APConstants.OTP_` is empty; both constant values equal `crp_otp_allowed_email_domains`, the T01 key.
- **Implementer assumptions (verbatim gist):**
  - derivation tags `otp-email` / `otp-code` / `otp-bucket`, pinned by external HMAC vectors;
  - `OtpKeys(APConfig)` is public and `OtpKeys(String)` package-private;
  - the accessors return copies, and an unconfigured instance throws `IllegalStateException`;
  - one WARN `auth.otp.keys outcome=unconfigured`, carrying no value and no length;
  - `getOtpSmtpTimeoutMs()` returns a raw trimmed String, which T09 parses with a default of 10000;
  - the getters reuse the private `cognitoSetting` helper;
  - decoding is strict standard base64.
- **Issues encountered:**
  - The first test-compile failed transiently with "class file for BaseAction not found"; the retry succeeded. A VS Code Java language server was running.
  - The no-variable restart answered HTTP 404 on `/marlo-web/`.
- **Evidence re-run (Leader inline, non-author): VERIFIED.**
  - Clean compile exit 0 (6/2405/1051/57).
  - `mvn -o -pl marlo-web -am test -Dtest=OtpKeysTest` exit 0, `Tests run: 13, Failures: 0`.
  - `checkstyle.sh` on the 5 files: 9 violations, all pre-existing `APConfig` `MethodName`.
- **Reviewer verdict: `PASS`** (`akili-reviewer`, `opus`; Implementer `sonnet`).
  - The scope items are met.
  - The constants are byte-identical to the migration key.
  - `OtpKeys` follows design §5.3 and DD-10: strict base64 of at least 32 bytes, unconfigured otherwise, one WARN with no value and no fallback key, and HMAC-SHA256 derivation with three distinct tags.
  - The GPL headers are present.
  - The Kaizen checks hold: the expected values are external vectors.
  - P-18 was settled without the disqualifier.
- **ADVISORY (recorded, not gating):**
  - *Readability:* the `OTP_PENDING_CHALLENGE` comment cites "Cognito parity, DD-4", but this spec's DD-4 is "Pre-registered only". The intended reference is CHG-COGNITO-AUTH-001 DD-4.
  - *Readability:* the `cognitoSetting` helper name and its javadoc now also serve the OTP getters. A rename is out of scope here.
  - *Risk:* the no-variable 404 proves nothing about startup. **Forward pointer to T15 (AC-12):** check a known 2xx/3xx route, such as the login page, with no secret set. Do not cite this run.
  - *Reliability:* strict decoding rejects wrapped base64 (`openssl rand -base64 64` or more wraps). **Forward pointer to T16:** name the `openssl rand -base64 32`/`48` single-line recipe in the runbook and go-live checklist.
  - *Reliability:* no test exercises the public `OtpKeys(APConfig)` constructor. T09 or T15 will cover it once it is wired.
- **runtime events:** none.
- **spawns:** implementer 38 calls, 131,295 tokens, ended complete; reviewer 14 calls, 53,973 tokens, ended complete.
- **Final verification:** VERIFIED; Reviewer PASS.

### CHG-OTP-LOGIN-001-T03 — `OtpChallenge` persistence and manager

- **Status:** PASS (attempt 1)
- **Date:** 2026-10-09
- **Requirements:** FN-007, FN-010, FN-012, FN-013, FN-014 (storage side); SEC-001, SEC-002; OPS-003; FN-002 (uncached read)
- **Skills assigned:** `marlo-verify`, `error-handling-patterns` (listed), plus `tdd` for the manager-branching tests. Reason: logic-heavy branching that test-first pays for; the SQL itself is proven by the scripted run. Effort `xhigh` (concurrency). Review: parallel lens reviewers (concurrency, security).
- **Forward pointers carried in the brief:** T01 advisory ('' / NULL / missing row = off); one time source.

#### Attempt 1

- **Files changed:**
  - new in `marlo-data/.../data/`: `model/OtpChallenge.java`, `dao/OtpChallengeDAO.java`, `dao/mysql/OtpChallengeMySQLDAO.java`, `manager/OtpChallengeManager.java`, `manager/impl/OtpChallengeManagerImpl.java`;
  - new `marlo-data/src/main/resources/xmls/OtpChallenges.hbm.xml`;
  - `hibernate.cfg.xml`: one mapping line, LF kept;
  - new test `marlo-web/src/test/.../data/manager/impl/OtpChallengeManagerImplTest.java`.
- **Time source:** Java UTC everywhere. The manager holds one `Clock.systemUTC()`, and every `DATETIME` is bound as UTC text. There is no `NOW()` and no `CURRENT_TIMESTAMP`; `created_at` is set explicitly. The local database runs at -05. The stored text is identical under JVM time zones Tokyo, Bogota and New York, and database session time zones +09:00 and -11:00 change no count.
- **Allow-list "off" cases:** NULL, '' and whitespace-only collapse to `""`, and so does a unit absent from the result. Tests: `aNullValueIsOff`, `anEmptyValueIsOff`, `aWhitespaceOnlyValueIsOff`, `aGlobalUnitAbsentFromTheStoreResultIsOff`. An active custom row wins even when blank, and an inactive custom row is ignored. The read is native SQL with no entity and no `setCacheable`.
- **Scripted SQL** (DAO statement text, `PREPARE`/`EXECUTE`, `ROW_COUNT()` in the same session, autocommit on):
  - Red runs: without `attempts<3`, exhausted → **1**; without the expiry term, expired → 1; without `consumed_at IS NULL`, consumed → 1.
  - Real consume: exhausted **0**, open **1**, second **0**, expired 0, expiring exactly now 0, attempts=2 1, missing 0.
  - Increment: 1, 1, 1, then 0.
  - Supersede, 2 open rows of the email: 2. Purge, 1-h cutoff: 1.
- **`registerMismatch`:** returns `MismatchOutcome` (`MISMATCH` / `EXHAUSTED` / `INVALID`). With 1 row affected, attempts of 3 or more → `EXHAUSTED`. With 0 rows, it re-reads: missing or consumed → `INVALID`, attempts of 3 or more → `EXHAUSTED`, anything else → `INVALID`.
- **`@Transactional`:** `issue`, `consume`, `registerMismatch`, `purgeExpired` (`REQUIRES_NEW`). Reads carry none.
- **Compile:** exit 0; 2410 / 1051 / 58. **Checkstyle:** 0 on the new files. **Tests:** the full `marlo-web -am` suite, 442 tests, 0 failures. The new class has 32 tests, observed red then green; 6 mutants were killed.
- **Consumers:** `grep OtpChallenge` finds only the 7 new files and `hibernate.cfg.xml`.
- **Implementer-declared gaps (verbatim gist):**
  1. The design gives no return type for `registerMismatch`, so it returns an enum.
  2. §5.2 step 6 lists consumed/expired before exhausted; the code follows RJ-3 and step 4, so a row that is both exhausted and expired → `EXHAUSTED`.
  3. DD-12 says "fall back to the default" while §9 says the row wins; the code implements row-wins.
  4. Supersede touches every open row of the email.
  5. Inserts are native SQL; the entity is a read mapping.
  6. The `otp_challenges` query space is declared on native DML, to avoid evicting every L2 cache region. Not measured.
  7. Two first-ever concurrent issues for one email can deadlock on the gap lock; the loser answers `unavailable`. Not tested.
  8. `REQUIRES_NEW` is asserted as an annotation only.
  9. The throwaway copy held 4 tables plus the migration.
  10. One read-only grep of `tasks.md`.
- **Evidence re-run (Leader inline, non-author): VERIFIED.**
  - Clean compile exit 0 (2410/1051/58); `OtpChallengeManagerImplTest` 32/32; Checkstyle 0 on the new files.
  - On a fresh throwaway database (real `global_unit_types` and `parameters` DDL plus the T01 migration), with the exact DAO text:
    - consume: the no-`attempts<3` falsifier on the exhausted row → 1; real consume exhausted 0, open 1, again 0, expired 0, attempts=2 1;
    - increment: 1, 1, 1, 0.
  - Throwaway dropped.

#### Reviewer verdicts — attempt 1 (parallel lenses, `opus`; Implementer `sonnet`)

- **Concurrency lens: `PASS`.**
  - The statements match DD-3 and §5.2 steps 6–7. `issue` is one transaction (FN-007), the purge runs in its own transaction, and the clock is single.
  - Gap 7 is no spec violation: the loser rolls back whole, at most one open row remains, and the failure maps to `unavailable` per §5.1 step 7. It requires a cooldown bypass. Accepted; T15 exercises it.
  - The first-level cache is not stale: `findByNonce` evicts the row it loads.
  - Gap 2: RJ-3 wins. The step 6 wording should be clarified in the design, which is not a defect of this task.
- **Security lens: `PASS`.**
  - SEC-001 and SEC-002 hold: only MACs are stored, there is no cache region, and exception messages carry no values.
  - Every value is bound; the only concatenation is an `int` constant.
  - No write path misses `@Transactional`. `toString` exposes no MAC.
  - Row-wins matches §9 and the `CognitoAuthSpecificity.isActiveFor` shape, and it is fail-closed, so it is consistent with DD-12 and FN-001.
  - With several custom rows, the newest `cp.id` wins deterministically.
  - The empty `IN` list is guarded by the manager.
- **Decisions made (adjudicated by the Leader from both verdicts):**
  - `registerMismatch` returns `MismatchOutcome` instead of "post-update attempts", carrying the same classification.
  - A row that is both exhausted and expired → `EXHAUSTED`, per RJ-3 and step 4.
  - Allow-list precedence is row-wins, and a blank active custom value means off.
  - The design.md §5.2 step 6 wording is left for `/akili-validate`; no execute-time edit, since RJ-3 already governs.
- **ADVISORY (recorded, not gating):**
  - *Resilience:* the 0-row re-read in `registerMismatch` is a snapshot read. If it shares a transaction with step 4's `findByNonce` under REPEATABLE-READ, a challenge exhausted concurrently answers `INVALID` instead of `EXHAUSTED`. This fails closed. Possible fix: `SELECT … FOR SHARE`.
  - *Resilience:* a range-DELETE purge can deadlock with an issue when every row is past the cutoff. It fails closed (`unavailable`).
  - *Reliability:* reads go through Hibernate `LocalDateTime` → `Timestamp` in the JVM time zone, and `hibernate.jdbc.time_zone` is unset. On a JVM in a daylight-saving zone, a UTC wall time in a spring-forward gap reads back shifted by 1 h.
  - *Risk:* the allow-list joins custom rows through the unit's own type parameter, while the cached lookup matches on key alone. A mismatched row resolves to off, which is fail-closed but can disagree with the admin screen.
  - *Readability:* "newest row wins" depends on the `ORDER BY` plus a map overwrite; add a comment.
  - *Risk:* a duplicate-nonce MySQL error message contains the nonce. Callers must not log the exception message.
  - *Reliability:* the native allow-list SELECT has no query space, so in AUTO flush mode it flushes the session. Harmless before authentication.
- **Forward pointers (Leader):**
  - **T10:** compute `expiresAt` from the same UTC clock as `now` (or let `issue` compute it). Never log a store exception's message, because a duplicate-nonce error carries the nonce.
  - **T11:** the §5.2 step 4 `now > expires_at` check uses the same UTC clock. Be aware of the snapshot re-read advisory when step 4 and `registerMismatch` share a transaction.
  - **T15:** probe concurrent first-ever issues for one email (deadlock → one `unavailable`, at most one open row), purge against issue, and REQUIRES_NEW behaviour in a real Spring context.
  - **T16:** go-live premises: the production JVM time zone is UTC, or `hibernate.jdbc.time_zone=UTC` is set; and the allow-list is set on the unit's own type parameter.
- **runtime events:** none.
- **spawns:** implementer 62 calls, 272,929 tokens, ended complete; reviewer (concurrency) 12 calls, 84,812 tokens, ended complete; reviewer (security) 14 calls, 83,430 tokens, ended complete.
- **Final verification:** VERIFIED; both lenses PASS.

---

## Pre-commit safety check: T01–T03 (user request, 2026-10-09)

The user asked to verify that every change so far is safe before the T03 commit.

### Mechanical checks (Leader inline)

- **Full suite:** `mvn -o -pl marlo-web -am test` exited 0 with `Tests run: 442, Failures: 0, Errors: 0, Skipped: 0`.
- **Debug leftovers:** `grep System.out|printStackTrace` over the new and changed main sources found nothing.
- **Local DB (`aiccradb_actsave`), where the T02 run applied the T01 migration through Flyway:**
  - `schema_version` holds `2.6.0.20261009.1025`, `success=1`;
  - there are 0 failed migrations;
  - 4 catalog rows exist and 0 `custom_parameters` rows;
  - the 3 OTP tables are present.
- **Startup smoke:** `scripts/run-marlo-java17.sh` was run with no `OTP_HMAC_SECRET`, with the uncommitted T03 hbm mapping included.
  - The log has 0 lines matching `SEVERE`, `Error creating bean`, `HibernateException` or `MappingException`.
  - `/marlo-web/` returns 302 and `/marlo-web/login.do` returns 200.
  - The only OTP-related log line is Tomcat's generic resource-cache warning, which every migration file triggers.
  - The app was left running.
- **Existing password login (local test account, localhost only):**
  - `POST login.do` returned 302 to `/marlo-web/AICCRA/crpDashboard.do`, and the dashboard returned 200.
  - Cognito login is not reproducible locally and was not exercised.

### Independent branch-wide review (`akili-reviewer`, `opus`, author of none of T01–T03): `SAFE WITH NOTES`

**Affects existing behaviour now (notes):**
1. The admin specificities screen lists `crp_otp_allowed_email_domains`. A value an admin saves now would switch the feature on once later PRs deploy, provided the secret is also set.
2. The `INSERT … SELECT` on `parameters` takes millisecond next-key locks. It may log a STATEMENT-binlog "unsafe" warning. No fix needed.
3. `CREATE TABLE IF NOT EXISTS` would keep a wrong-shaped pre-existing table. Check `SHOW CREATE TABLE` on staging before production.

**Checked safe:**
- Startup: `hbm2ddl` validation is off, and the `@Value` defaults are empty.
- `OtpKeys` is not a bean, and the DAO and manager constructors touch no database.
- No L2 cache region is evicted, because the native writes declare the `otp_challenges` space.
- No credentials and no debug output in the diff; GPL headers present.
- All values are bound and only MACs are stored.

**Affects the feature once enabled:**
4. *Should-fix before the request action ships.* Concurrent `issue` calls for one email can deadlock under REPEATABLE-READ. Unless the action catches it, the result is a 500 with a timing difference. Under READ COMMITTED both calls could commit and leave two open rows.
5. The constant-time MAC compare (`MessageDigest.isEqual`) belongs to the verify flow.
6. A line-wrapped base64 secret fails closed, so the runbook must give the `-base64 48` / `-A` recipe.
7. The `expiresAt` read-back can shift by 1 h on a non-UTC JVM during a DST gap. Expiry is decided in SQL, so never decide it from `getExpiresAt()` in Java.
8. `REQUIRES_NEW` on purge can block on its own caller if that caller holds `otp_challenges` locks. Call it outside such a transaction.
9. With duplicate active `custom_parameters` rows, the highest `cp.id` wins, which may differ from `BaseAction`.
10. A comment in `OtpChallengeMySQLDAO` refers to an external probe script.

### Leader adjudication

No blocker. The T03 commit is safe. Forward pointers added:
- **T10:** catch lock and deadlock exceptions from `issue` and answer through the neutral or `unavailable` path, never a 500 (finding 4). Call manager writes only from `execute()`, never from `validate()`. Run `purgeExpired` outside any transaction that holds `otp_challenges` locks (finding 8).
- **T11:** compare MACs with `MessageDigest.isEqual` on decoded bytes (finding 5). Do not decide expiry from `getExpiresAt()` in Java; rely on the SQL condition (finding 7).
- **T15:** the concurrency probe records the isolation level and covers concurrent first-ever issues for one email (finding 4).
- **T16:** the runbook gives the single-line secret recipe (finding 6), and the rollout checklist covers finding 1 (no admin value before release) and finding 3 (`SHOW CREATE TABLE` on staging).
