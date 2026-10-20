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
| Budget (design §18) | 22 tasks executed (16 budgeted; T17–T22 were added later) · ~2,300 LOC budgeted · 23 review rounds budgeted, extended to 28 at T11 (see the Leader note of 2026-10-18 at the end) |

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

### CHG-OTP-LOGIN-001-T04 — Rate limiting and cooldown persistence

- **Status:** in progress (`[~]`). Escalated to the user over a design gap in the attempt 1 report.
- **Date:** 2026-10-09
- **Skills assigned:** `marlo-verify`, `tdd` (listed). Effort `xhigh`: concurrency, despite the S size.
- **Forward pointers carried in the brief:** one UTC clock; `@Transactional` writes, never called from `validate()`; lock or deadlock errors count as a fail-closed store failure, logged without the message.

#### Attempt 1 (Implementer report; no review yet)

- **Files (all new):**
  - `marlo-data`: `OtpRateLimitDAO`, `OtpRateLimitMySQLDAO`, `OtpRateLimitManager`, `OtpRateLimitManagerImpl`.
  - `marlo-web`: `security/otp/OtpRateLimiter.java`.
  - Tests: `OtpRateLimiterTest` (31) and `OtpRateLimitManagerImplTest` (11). The second is not in the task's file list; it covers the threshold arithmetic and the annotations.
- **Buckets:** `request|email` 5, `request|ip` 20, `verify|email` 10, `verify|ip` 50, per 15-min UTC window. Each key is HMAC'd with `k_bucket`. The cooldown is `cooldown|email`, sliding 30 s, computed as `notAfter = now - 30 s` in Java, followed by a conditional upsert and a read-back compared with `now`.
- **Red run:** a fixed 30-s cooldown failed 7 tests, including `aRequestOneSecondAfterAnotherIsLimitedAcrossAMinuteBoundary` (`expected:<LIMITED> but was:<ALLOWED>`).
- **Green:** 42/42 in the new tests; full suite 484/484. Falsifier (fixed window) failed 7; 13 mutants killed.
- **Straddle SQL** (DAO text, same session, autocommit): 12:07:59.500 accepted; 12:08:00.500 rejected; +29.9 s rejected; +30.0 s accepted. Session time zones made no difference.
- **Concurrency:** `RuntimeException` → `UNAVAILABLE`, logged by class only. In 6 rounds of 20 parallel window upserts: 0 errors, and the read-backs were exactly 1..20.
- **Purge:** runs after counting, at most once per 10 min per instance, with `REQUIRES_NEW`. Failures are caught. `EXPLAIN` uses the T01 indexes.
- **Compile:** 2414 / 1052 / 60. **Checkstyle:** 0.
- **Implementer-declared gaps (verbatim gist):**
  1. **Same-millisecond cooldown bypass.** Parallel requests that carry the same `now` text are all accepted: 20 of 20 in the probe, against 1 of 20 with distinct values. The 5-per-15-min email window caps the damage. Closing it needs a design change.
  2. A store failure yields `UNAVAILABLE`, which design §5.3 names, rather than "limited" as the brief said. **The design wins; no issue.**
  3. When the secret is unconfigured, the limiter returns `UNAVAILABLE`. The action must check `isConfigured` first or map that outcome.
  4. The counting order, the purge throttle and `REQUIRES_NEW` are judgment calls.
  5. No Spring bean for the limiter yet; wiring is left for the action task.
  6. The app was not started.
  7. The Implementer printed the Leader's scratchpad helper, which holds the local dev MySQL password, into its own transcript once. Nothing was committed.

#### Leader adjudication

- Gap 1 is a **spec gap**, not an implementation defect. Design §5.1 step 1 says the read-back "equals `now` only when this request was accepted", which is false when two requests compute the same millisecond. FN-008 (30-s minimum spacing per email) can then be bypassed by a parallel burst; the SEC-010 email window still caps it at 5 per 15 min. Per the Pivot rule, no review is spent until the user decides.

## Pivot Record: CHG-OTP-LOGIN-001-T04

- **Date:** 2026-10-13
- **Blocker:** design §5.1 step 1 decided the cooldown winner by reading `last_request_at` back and comparing it with the request's own `now`. Parallel requests that compute the same millisecond all match, so all of them pass. The Implementer's probe: 20 of 20 accepted, against 1 of 20 when the values differ. This defeats FN-008 (at least 30 s apart per email) for a parallel burst.
- **Alternatives presented to the user:**
  1. A row lock: an exclusive no-op upsert, then `SELECT … FOR UPDATE`, then the decision in Java. No schema change.
  2. A tie-break token column, which needs a new migration.
  3. Accept the gap, capped by the 5-per-15-min email window.
  4. Pause.
- **User decision:** option 1, the row lock.
- **Revised direction:** design §5.1 step 1 and the §5.3 `OtpRateLimiter` row are amended. The lock is taken exclusively from the first statement, never upgraded from shared. A lock timeout or deadlock is a store failure (`unavailable`). `tasks.md` T04: the Cooldown scope line is amended, and a "Same-millisecond case" line requires a parallel probe of at least 20 sessions that yields exactly one acceptance.
- **Requirements meaning:** unchanged. FN-008 is now actually met. No ADR in the TRD is affected.
- **Closure sweep:**
  - Forward: `grep "upsert-and-read-back|reads the value back"` over the spec folder (excluding `execution.md`) found no remaining hits.
  - Backward: DD-8 and design.md:153 cite the sliding interval and RJ-1 only, and both still hold. No other document cites the mechanism.
  - Diff check: the only removed lines are the two old bullets and the old §5.3 phrase.
- **Effect on the window buckets:** none. Their upsert-and-read-back of `hits` is unaffected, because each request increments, so equal timestamps cannot collide.
- **Re-issued brief:** the attempt 1 Implementer is resumed with the amended text. This counts as a post-Pivot attempt 2, not a FAIL, and it is reviewed in full.

#### Attempt 2 — post-Pivot row-lock cooldown

- **Files changed:**
  - `OtpRateLimitDAO`, `OtpRateLimitMySQLDAO`, `OtpRateLimitManager` (javadoc) and `OtpRateLimitManagerImpl`, under `marlo-data`;
  - `OtpRateLimiter`, under `marlo-web` (comments only);
  - both tests.
  - All of these files are new relative to HEAD.
- **Cooldown, in one `REQUIRES_NEW` transaction:**
  1. `INSERT … VALUES (:bucket, :sentinel) ON DUPLICATE KEY UPDATE last_request_at = last_request_at` (the sentinel is the epoch);
  2. `SELECT CAST(last_request_at AS CHAR) … FOR UPDATE`;
  3. the decision is made in Java: `UPDATE … SET last_request_at = :now` runs only when the stored value is at least 30 s older;
  4. an UPDATE count other than 1 throws `IllegalStateException`, which yields `UNAVAILABLE`.
  - The window buckets are unchanged.
- **Red / green / falsifier:**
  - Red: 11 assertion failures. The key case is a stored value equal to now, which was `ALLOWED` under the old read-back.
  - Green: 49 tests.
  - Falsifier: the fixed window turns 7 tests red; 20 mutants were killed.
- **Straddle** (real DAO and manager through Hibernate, throwaway DB):

  | Request | Result |
  |---|---|
  | 12:07:59.500 | accepted |
  | +1.0 s | rejected |
  | +29.9 s | rejected |
  | +30.0 s | accepted |

  The JVM time zone (Tokyo or Bogota) made no difference.
- **Parallel probe:** 20 sessions with the same `now`, behind a barrier.
  - Fresh bucket, rounds 1–5: 1 accepted, 19 rejected, 0 unavailable each round.
  - Existing bucket 45 s old, rounds 6–10: the same.
  - Old read-back statement in the same probe: 20 of 20 accepted.
  - 0 deadlocks and 0 lock timeouts.
- **Gates:** compile 2414/1052/60; Checkstyle 0; full suite 491/0.
- **Implementer-declared gaps:**
  - `@Transactional` was checked by annotation only;
  - an unconfigured secret returns `UNAVAILABLE` before §5.1 step 3, so T10 maps it;
  - with the default `innodb_lock_wait_timeout` of 50 s, a queued request can wait up to 50 s.
- **Evidence re-run (Leader inline, non-author): VERIFIED.**
  - Clean compile exit 0 (2414/1052/60).
  - `mvn -o -pl marlo-web -am test` exit 0, 491 tests, 0 failures.
  - An independent parallel CLI probe on a throwaway DB used the exact DAO cooldown statements, with the decision emulated in the same transaction. Across 3 rounds on a fresh bucket and 3 on a 45-s-old bucket, every round of 20 sessions gave 1 ACCEPT and 19 REJECT, with 0 errors.

#### Reviewer verdicts — attempt 2 (parallel lenses, `opus`; Implementer `sonnet`)

- **Concurrency lens: `PASS`.**
  - The design as amended is matched.
  - The window upsert holds its X lock until commit, so its read-back is exact, and there is no same-timestamp issue there.
  - There is one clock read per check, aligned to UTC.
  - The amendment changes no requirement's meaning: FN-008 is now met, and a deadlock or timeout leads to `unavailable`, the existing store-failure contract.
- **Security lens: `PASS`.**
  - SEC-010 limits and order hold, and nothing reads account state before counting.
  - Buckets are HMAC'd, with the key returned as a clone.
  - Every failure fails closed, and logs carry the class name only, never a bucket, email or IP.
  - All values are bound, and all four writes run under `REQUIRES_NEW`.
  - The amendment keeps the meaning of SEC-010, FN-008 and DA-003.
- **Execute-time spec edits made while closing the review (meaning-preserving; carried into the next Reviewer brief):**
  - The Pivot closure sweep missed one site: design.md §12 "Load" row still read "1 cooldown upsert". It now reads "the cooldown's locking upsert, `SELECT … FOR UPDATE` and conditional update".
  - tasks.md T04 said "Store failure → limited" and "store exception → limited". Both now read `unavailable`, aligned to design §4 and §5.3, which the code already follows.
- **ADVISORY (recorded, not gating):**
  - *Resilience:* the purge DELETE locks the secondary index and then the PK, while a cooldown accept locks the PK and then the secondary index. A user whose row is more than 1 h old, requesting exactly while the purge runs, can deadlock; this happens at most once per 10 min per instance. The loser gets `unavailable` (fail closed), or the purge is skipped. Possible fix: delete by PK in batches with `FOR UPDATE SKIP LOCKED`.
  - *Resilience:* the 50-s default lock wait can hold a pool connection. A short query timeout (2–3 s) on the cooldown statements would bound it.
  - *Risk:* clock skew between instances can delay acceptance by the skew. Worth one line under the accepted risks.
  - *Reliability:* if `purgeWindowsBefore` throws, the cooldown purge is skipped until the next cycle.
  - *Note:* a request rejected by the cooldown still uses up its window quota. This is consistent with SEC-010's "count before lookup".
  - *Risk:* the limiter logs its own `auth.otp.ratelimit` line, outside `OtpLog`. The content is safe; record it as accepted or route it through `OtpLog`.
  - *Risk:* a null `clientIp` would share a single IP bucket across clients.
- **Forward pointers (Leader):**
  - **T10:**
    - check `OtpKeys.isConfigured()` before the limiter, so that an unconfigured secret answers `domainNotEnabled` per §4 and not `unavailable`;
    - wire `OtpRateLimiter` as a singleton bean so the purge throttle holds;
    - guarantee a non-null client IP from `ClientIpResolver`;
    - call the limiter first, before any entity load, and never from `validate()`;
    - consider a 2–3 s query timeout on the cooldown statements.
  - **T15:** probe a cooldown accept against a concurrent purge, and lock waits under load.
  - **T16:** name the accepted risks of cross-instance clock skew and a purge deadlock answering `unavailable`.
- **runtime events:** none.
- **spawns:**
  - implementer: attempt 1, 41 calls, 246,226 tokens, ended partial (spec gap); attempt 2, cumulative 60 calls, 323,066 tokens, ended complete;
  - reviewer (concurrency): 10 calls, 75,269 tokens, ended complete;
  - reviewer (security): 14 calls, 96,211 tokens, ended complete.
- **Final status:** PASS (attempt 2, post-Pivot). **Final verification:** VERIFIED; both lenses PASS.

### CHG-OTP-LOGIN-001-T05 — Pure OTP helpers

- **Status:** PASS (attempt 1)
- **Date:** 2026-10-13
- **Requirements:** FN-003, FN-009, SEC-001, SEC-003, SEC-004, SEC-009, SEC-011, OPS-001 (line shape)
- **Skills assigned:** `tdd`, `marlo-verify` (listed). Effort `high`. Review: checklist; override (f) applies (security helpers).
- **Forward pointer carried in the brief:** `ClientIpResolver` never returns null (T04 security review).

#### Attempt 1

- **Files (new, `marlo-web/.../security/otp/`):** `OtpCodes`, `OtpDomainPolicy`, `ClientIpResolver`, `OtpPending`, `OtpLog`, with a test for each.
- **API, as reported:**
  - `OtpCodes` (`newCode`, `newNonce`, `mac`, `matches`) uses `MessageDigest.isEqual` on decoded bytes and returns false on any shape error.
  - `OtpDomainPolicy` is static and implements FN-003.
  - `ClientIpResolver` turns a null or blank remote address into `UNKNOWN`. It reads `X-Forwarded-For` from the right and matches proxies by CIDR, IPv4 and IPv6.
  - `OtpPending` is `Serializable` with 5 fields and holds no code and no MAC.
  - `OtpLog` writes the request, verify and delivery lines through `LogSanitizer`.
- **Red run:** `Tests run: 170, Failures: 66, Errors: 5`. The 5 errors are NPEs where tests dereference the null a skeleton returned. **Green:** 76 own tests; the task command ran 170 with 0 failures; the full suite ran 567 with 0 failures.
- **Falsifier (`endsWith`):** `suffixLookalikeIsNotAllowed` and `unlistedSubdomainIsNotAllowed` went red; the code was restored byte-identical.
- **Compile:** 2414/1057/65. **Checkstyle:** 0. **Hygiene:** no FIX.
- **Gaps:**
  - The "guide §6.2" citation cannot be resolved because the guide is not in the repo; FN-003 and the task vectors were implemented.
  - `OtpCodes` carries its own private HMAC.
  - `OtpLog` has no guard for a null outcome.
- **Evidence re-run (Leader inline, non-author): VERIFIED.** Clean compile exit 0 (2414/1057/65); `mvn -o -pl marlo-web -am test` exit 0 with 567 tests and 0 failures; Checkstyle 0 on the new files.

#### Reviewer verdict — attempt 1: `PASS` (`akili-reviewer`, `opus`; Implementer `sonnet`)

- **Summary:** all five helpers match design §5.3 and DD-9, and every task vector is present with a literal expectation.
  - The compare is constant time; its length guard leaks nothing.
  - `nextInt(1_000_000)` is unbiased, and the test asserts its argument.
  - The allow-list follows FN-003; `cgiar.org` is dropped on exact match, which is consistent with SEC-009.
  - The IP walk is right-to-left with CIDR for IPv4 and IPv6, and untrusted headers are ignored.
  - `OtpPending` has exactly five fields, pinned by reflection.
  - The `OtpLog` shape is exact.
  - The Kaizen checks hold.
  - The red run with 5 NPEs is accepted: it is a contract failure, not a setup error.
- **ADVISORY (recorded, not gating):**
  - **RISK (high value):** `normaliseEmail` uses `toLowerCase(Locale.ROOT)`, which folds the Kelvin sign U+212A into an ASCII `k`.
    - Suppose the email is sent to the *raw* submitted address. An attacker who submits a lookalike non-ASCII address could then receive the code for the ASCII account, which is an account takeover.
    - Remediation: send only to the normalised `pending.email`, or reject non-ASCII email.
    - Design §5.3 L305 says "the recipient is always the submitted address", which is ambiguous between raw and normalised.
  - **RISK:** on `denied_domain`, the attacker-controlled `domain` is stripped of CR/LF only. A value such as `evil.org outcome=ok` forges a key/value pair inside the line. Remediation: replace any character outside `[a-z0-9.-]` in `domain` and `gu` before logging.
  - **RELIABILITY:** `parseAddress` can fall through to a DNS lookup for an odd hop such as `.:`. That hop is reachable only from a trusted peer. Remediation: require the first character to be a hex digit or `:`.
  - **RELIABILITY:** `OtpLog` throws an NPE on a null outcome. A null-safe `internal_error` would keep the one-line-per-call rule.
  - **READABILITY:** one `OtpCodesTest` literal is 49 characters, not 64.
  - **RESILIENCE (out of scope):** IPv6 clients can rotate addresses within their /64. Bucketing by /64 would close that.
- **Forward pointer (Leader, binding regardless of the user's choice below):** **T09/T10** send the code only to the normalised email held in `OtpPending` (`pending.email`), never to the raw request parameter.
- **runtime events:** none.
- **spawns:** implementer 25 calls, 146,176 tokens, ended complete; reviewer 12 calls, 85,657 tokens, ended complete.
- **Final verification:** VERIFIED; Reviewer PASS.

#### T05 reopened — user-approved hardening amendment (2026-10-13)

- **User decision:** apply all four Reviewer advisories before the commit.
- **Execute-time spec edits:**
  - requirements.md FN-003 adds: reject any email or entry with a character outside printable ASCII. A Decision Log entry is added.
  - design.md §5.3 is amended in three rows:
    - `OtpDomainPolicy`: rejects non-ASCII input;
    - `OtpLog`: `domain` and `gu` are reduced to `[a-z0-9.-]`, and a null outcome is logged as `internal_error`;
    - `ClientIpResolver`: parses IP literals only, never through DNS.
  - tasks.md T05 gains a "Hardening" test list.
- **Requirement meaning:** FN-003 is narrowed. Non-ASCII input is now refused. This is a user-approved requirement change, recorded in the Decision Log; no other requirement changes. AC-2 still holds.
- **Not a FAIL:** the attempt 1 PASS stands for the original scope. Attempt 2 is the amendment and gets a full review. T05 is back to `[~]`.

#### Attempt 2 — hardening amendment

- **Files changed:** `OtpDomainPolicy`, `OtpLog`, `ClientIpResolver`; tests `OtpDomainPolicyTest`, `OtpLogTest`, `ClientIpResolverTest`, `OtpCodesTest`.
- **Red run:** 185 run, 16 failures, 3 errors.
  - Kelvin sign: `kelvinSignEmailIsRejectedAndDoesNotFoldOntoAnotherDomain`.
  - Log forgery: `expected:<...gu=[ccafs domain=evil.org_outcome_]ok> but was:<...gu=[CCAFS domain=evil.org outcome=]ok>`.
  - DNS: a child JVM's `jdk.net.hosts.file` mapped `.:` to 192.0.2.7, and the old code returned the hop.
  - Null outcome: NPE.
- **Green:** 91 own tests; task command 185/0; full suite 582/0.
- **Fix 3:** a literal-only IPv4/IPv6 parser. `InetAddress` appears only in Javadoc.
- **Gaps:**
  - `gu` is now logged in lower case.
  - `normaliseEmail` returns `""` for rejected input.
  - A null delivery outcome logs `internal_error` at WARN.
- **Evidence re-run (Leader inline, non-author): VERIFIED.** Clean compile exit 0 (2414/1057/65); full suite exit 0, 582 tests, 0 failures; Checkstyle 0.
- **Reviewer verdict: `PASS`** (`akili-reviewer`, `opus`; Implementer `sonnet`).
  - **FN-003:** the ASCII check runs before and after lower-casing, and `""` never counts as valid.
  - **Log values:** reduction to `[a-z0-9.-]` runs after `LogSanitizer`, so `outcome` and `ms` cannot be forged.
  - **IP parsing:** no DNS. The IPv6 parser handles `::`, a second `::`, groups longer than 4, embedded IPv4, and zone IDs (refused). IPv4 refuses values above 255, leading zeros and short addresses. CIDR checks the length first.
  - **Security:** no input makes an untrusted hop trusted, and every mis-parse falls back to `getRemoteAddr()`.
  - **Kaizen:** expected values are literals and test-double arguments are asserted.
- **ADVISORY (recorded, not gating):**
  - *Reliability:* a link-local trusted proxy address (`fe80::1%2`) is refused, so all users would share the proxy's bucket. This fails safe but hurts availability.
  - *Risk:* hops are not canonicalised before hashing (`2001:DB8::1` and `2001:db8:0::1`). The hop is written by the trusted proxy, so this is low risk.
  - *Readability:* `trim()` removes leading control characters before the ASCII check, so FN-003's "before normalisation" wording differs slightly from the code. There is no folding risk.
  - *Risk:* callers must decide through `domainOf`/`isAllowed` before they store `normaliseEmail(...)`.
  - *Readability:* `internal_error` lies outside the closed `DeliveryOutcome` set; dashboards should know.
  - *Reliability:* the DNS probe test compares the child JVM's whole output, so `JAVA_TOOL_OPTIONS` on a CI host would make it fail spuriously.
  - Cosmetic: import order and field placement.
- **Forward pointers (Leader):**
  - **T06, T10, T11:** decide eligibility through `OtpDomainPolicy.domainOf`/`isAllowed`. Never use or store `normaliseEmail(...)` before that check passes, because it returns `""` for rejected input.
  - **T16 runbook:** `otp.trusted.proxies` must not rely on link-local addresses. Tell log dashboards about `internal_error` on delivery lines.
- **runtime events:** none.
- **spawns:** implementer, attempt 2: 15 calls, cumulative 181,280 tokens, ended complete. Reviewer: 11 calls, 86,473 tokens, ended complete.
- **Final status:** PASS (attempt 2, user-approved amendment). **Final verification:** VERIFIED; Reviewer PASS.

### CHG-OTP-LOGIN-001-T06 — `OtpEligibility`

- **Status:** PASS (attempt 1)
- **Date:** 2026-10-13
- **Requirements:** FN-001 (domain plus configured), FN-004, FN-005 (predicate), FN-015, SEC-009
- **Skills assigned:** `tdd`, `marlo-verify` (as listed in the task). Effort `high`. Review: checklist; override (f) applies, because this is authentication eligibility.
- **Forward pointers carried in the brief:**
  - T05: decide through `domainOf` / `isAllowed` before calling `normaliseEmail`.
  - T03: read the allow-list uncached.
  - Never call this from `validate()`.

#### Attempt 1

- **Files (new):** `marlo-web/.../security/otp/OtpEligibility.java`, `OtpEligibilityTest.java`.
- **API:**
  - `isEligible(String email, long globalUnitId)`.
  - `otpEnabledFor(String email, boolean cgiarUser, Collection<GlobalUnit> units)` makes one `allowListFor` call, and none when the feature is unconfigured or the email has no domain.
  - "CGIAR user" means `User.isCgiarUser()`. The Cognito guard calls `CognitoAuthSpecificity.isActiveFor`.
  - "Configured" means `OtpKeys.isConfigured()` plus a non-blank allow-list. The Implementer applied this to both predicates.
- **Red:** 21 tests, 12 failures and 3 errors against a stub. The first failure: `unknownUserIsRefused: expected:<[user@partner.org]> but was:<[]>`.
- **Green:** 21/21 own tests; full suite 603/0.
- **Falsifier:** with `existCrpUser` replaced by `false`, 3 tests go red, including `nonMemberIsRefused`.
- **Argument assertions:**
  - `getUserByEmail("user@partner.org")`, `getGlobalUnitById(7)`, `allowListFor(key, [7] | [7, 8])`, `existCrpUser 42:7`, and the `cognito_auth_active` lookup with the unit id.
  - Zero-call assertions for a non-ASCII email, an email with no domain, and an unconfigured feature.
- **Compile:** 2414/1058/66. **Checkstyle:** 0.
- **Evidence re-run (Leader inline, non-author): VERIFIED.** Clean compile exit 0 (2414/1058/66). Full suite exit 0, 603 tests, 0 failures. Checkstyle 0.

#### Reviewer verdict — attempt 1: `PASS` (`akili-reviewer`, `opus`; Implementer `sonnet`)

- **Summary:**
  - Each refusal reason, on its own, yields false. OQ-4 is covered.
  - The variant reads no user or membership, so it is not a disclosure oracle (§6, DD-4, JI-11).
  - The cached Cognito read is allowed by DD-12.
  - Applying "configured" to the full predicate conforms to §9 and FN-001.
  - The T05 ordering is proven by the zero-call assertions.
  - The fakes answer from argument-keyed data, which clears the disqualifier.
- **ADVISORY (recorded, not gating):**
  - *Risk:* the FN-015 SEC-009 re-check reads `cognito_auth_active` from the cache. On another instance, a CGIAR user can therefore stay eligible for up to about 1 h after Cognito is enabled. DD-12 allows this, but it is worth listing as a residual in design §16.
  - *Wiring:* neither `OtpEligibility` nor `OtpKeys` is a bean. If actions build `new OtpKeys(config)` per request, the "WARN once" message repeats on every request.
  - *Resilience:* exceptions depend on account state. For example, `CognitoAuthSpecificity` throws an NPE on a null `GlobalUnitType`. T10 must map every predicate exception to one response that does not depend on account state.
  - *Readability:* the test contains a literal U+212A; use the `K` escape instead.
  - *Coverage:* no test covers Cognito being active through the catalog default alone.
- **Forward pointers (Leader):**
  - **T10:**
    - Register `OtpKeys`, `OtpEligibility`, `OtpRateLimiter` and `OtpCodes` as singletons (`@Named` or Spring config), or share one instance. Never build them per request.
    - Map every exception from `OtpEligibility` to the same neutral or `unavailable` path, independent of account state.
  - **T11** (verify re-check): the same exception mapping applies.
  - **T13:** use the `otpEnabledFor` variant only.
  - **T16:** list the cached-Cognito re-check lag (up to about 1 h, cross-instance) as an accepted residual.
- **runtime events:** none.
- **spawns:** implementer 25 calls, 126,210 tokens, ended complete. Reviewer 17 calls, 66,677 tokens, ended complete.
- **Final verification:** VERIFIED; Reviewer PASS.

### CHG-OTP-LOGIN-001-T07 — Shiro token and realm branch

- **Status:** PASS (attempt 1)
- **Date:** 2026-10-13
- **Requirements:** FN-011 (authentication), SEC-007, FN-019
- **Skills assigned:** `marlo-verify` (from the task) plus `tdd`. Reason for `tdd`: the task requires the red test first. Effort `high`. Review: `full`, as a lens checklist, because the change touches shared state.

#### Attempt 1

- **Files:**
  - `marlo-data/.../security/OtpAuthenticationToken.java` (new, LF).
  - `marlo-data/.../security/APCustomRealm.java` (CRLF preserved; 11 lines added, 2 removed). `supports()` now also accepts `OtpAuthenticationToken`, and a branch above the cast returns `SimpleAuthenticationInfo(userId, token, getName())`.
  - `marlo-web/src/test/.../APCustomRealmDispatchTest.java` (+3 tests; no existing method changed).
- **Credentials:** the token itself, per design §5.4. It holds no secret.
- **Red:** `supports()` returned false ("Shiro must accept this token type…"), and `Subject.login` reported "does not support authentication token".
- **Falsifier (branch moved below the cast):** `ClassCastException: …OtpAuthenticationToken cannot be cast to class org.apache.shiro.authc.UsernamePasswordToken`.
- **Green:** full suite 606/0. Named consumers, all with 0 failures and 0 errors:

  | Test class | Tests |
  |---|---|
  | `APCustomRealmDispatchTest` | 8 |
  | `CognitoCallbackActionTest` | 30 |
  | `CognitoLogHygieneTest` | 17 |
  | `LoginActionCgiarGuardTest` | 6 |
  | `LoginActionLogoutTest` | 3 |
  | `ValidateUserActionGuardTest` | 7 |
  | `ShiroRequestSessionCacheResetterTest` | 6 |
  | `CognitoUnloggedStackReachabilityTest` | 1 |
  | `CognitoAssertionTest` | 10 |
  | `CognitoIdentityMappingTest` | 9 |
  | `UserMySQLDAOEmailNormalizationTest` | 5 |

  `LoginActionFinishLoginTest` and `CognitoLoginActionTest` are also green.
- **Compile:** 2415/1058/66. **Checkstyle:** HEAD 0, now 0.
- **Issue encountered:** the VS Code Java language server (Eclipse JDT) rewrote `target/classes` after a clean compile. Two intermediate runs then showed 177 `Unresolved compilation problem` errors. The fix is to wipe, compile and test in a single command.
- **Evidence re-run (Leader inline, non-author): VERIFIED.** One command wiped the classes and ran `mvn -o -pl marlo-data,marlo-web -am test`: exit 0, 2415/1058/66 compiled, 606 tests, 0 failures, 0 errors, 0 `Unresolved compilation` lines. Checkstyle 0 on the 3 files; `APCustomRealm.java` is still CRLF.

#### Reviewer verdict — attempt 1: `PASS` (`akili-reviewer`, `opus`; Implementer `sonnet`)

- **Summary:**
  - The existing branches are byte-identical. The only changes are the inserted block, the `supports()` line, and one word of Javadoc.
  - The unknown-token refusal is already covered by `anUnknownTokenTypeIsStillRefusedByTheRealm`.
  - Using the token as credentials is safe: the matcher is `AllowAllCredentialsMatcher`, authentication caching is off, and the session keeps only the `Long` principal.
  - SEC-007 holds.
  - The test expectations are literals.
- **ADVISORY (recorded, not gating):**
  - *Reliability:* the OTP dispatch test does not assert `assertSame(token, info.getCredentials())`, unlike its Cognito sibling.
  - *Readability:* the test class Javadoc and the `ExplodingUserManager` messages still mention only the Cognito path.
  - *Risk:* with an allow-all matcher, any code that builds `new OtpAuthenticationToken(id)` and calls `Subject.login` logs in as that user.
- **Forward pointer (Leader):** **T11** must be the only site in the codebase that builds `OtpAuthenticationToken`, and it may do so only after the consume (§5.2 step 7) and the eligibility re-check (step 8). The T11 Reviewer verifies this with `grep -rn "new OtpAuthenticationToken" marlo-*/src/main`.
- **runtime events:** none.
- **spawns:** implementer 25 calls, 113,355 tokens, ended complete. Reviewer 8 calls, 56,733 tokens, ended complete.
- **Final verification:** VERIFIED; Reviewer PASS.

### CHG-OTP-LOGIN-001-T08 — Extract `ReturnUrls.sameOriginOrNull`

- **Status:** PASS (attempt 1)
- **Date:** 2026-10-13
- **Requirements:** FN-011 (return URL, landing parity), FN-019
- **Skills assigned:** `marlo-verify` (listed) plus `tdd`, because the task orders the red test first. Effort `medium`. Review: checklist; override (f) applies (open-redirect guard).
- **Leader brief constraint:** `CognitoLoginActionTest.java:158` reaches the private `sameOriginOrNull(String)` by reflection. The action therefore keeps that method as a one-line delegation, and the test stays untouched.

#### Attempt 1

- **Files:**
  - `marlo-web/.../security/ReturnUrls.java` (new): `public static String sameOriginOrNull(String candidate, String baseUrl)`, plus `isAuthenticationEndpoint` and `AUTHENTICATION_ENDPOINT_PATHS`, moved verbatim as private static members.
  - `CognitoLoginAction.java` (+2/−83): the private method now delegates with `ReturnUrls.sameOriginOrNull(candidate, this.getBaseUrl())`; 7 unused imports were removed.
  - `ReturnUrlsTest.java` (new, 11 tests).
  - All files are LF.
- **Red:** test-compile failed with `cannot find symbol: ReturnUrls`. This is a compile red; the falsifier below proves the assertions.
- **Falsifier (origin check replaced by `if (false)`):** 2 of 11 tests failed in `ReturnUrlsTest` and 4 of 34 in `CognitoLoginActionTest`. The failing `CognitoLoginActionTest` cases were `anExternalOriginIsStillRejected`, `anOffSiteReturnUrlIsDiscardedAndAnOnSiteOneSurvives` and both trailing-slash cases. The change was then restored.
- **Green:** the `-Dtest` run passed 45/0; the full suite passed 617/0. `CognitoLoginActionTest` has no diff.
- **Compile:** 2415/1059/67. **Checkstyle:** HEAD 0, now 0.
- **Evidence re-run (Leader inline, non-author): VERIFIED.** One command did the wipe, compile and test: exit 0, 2415/1059/67, 617 tests, 0 failures. `ReturnUrlsTest` passed 11/0 and `CognitoLoginActionTest` 34/0. Checkstyle 0; only 3 paths changed.

#### Reviewer verdict — attempt 1: `PASS` (`akili-reviewer`, `opus`; Implementer `sonnet`)

- **Behaviour identity:** a line-by-line comparison found no change. Every normalisation and fail-closed branch is preserved. `getBaseUrl()` is still read at call time.
- **Reversion challenge (DD-11):** the only consumer is `CognitoLoginAction:241`. That path is pinned by the unchanged suite, which is green.
- **Tests:** the test literals cover every required case. The malformed inputs really reach the `URISyntaxException` branch.
- **Imports:** removing the unused imports is inert.
- **ADVISORY (recorded, not gating):**
  - *Risk/readability:* the moved log messages say "Cognito login:" and now log under the `ReturnUrls` logger. Once OTP reuses the class, an OTP rejection would be labelled as Cognito.
  - *Readability:* the Javadoc `{@link}` references at `CognitoLoginActionTest` lines 569 and 640 now dangle. This breaks javadoc generation, not the compile. The test must stay unchanged, so this is a follow-up.
- **Forward pointer (Leader):** **T10/T11**, when reusing `ReturnUrls`, make the log label caller-neutral by dropping the "Cognito login:" prefix or passing a label. The change must keep `CognitoLoginActionTest` green and unchanged.
- **runtime events:** none.
- **spawns:**
  - implementer: 17 calls, 93,050 tokens, ended complete;
  - reviewer: 8 calls, 47,413 tokens, ended complete.
- **Final verification:** VERIFIED; Reviewer PASS.

### CHG-OTP-LOGIN-001-T09 — `OtpMailSender` and email copy

- **Status:** PASS (attempt 2, after a Reviewer FAIL on attempt 1)
- **Date:** 2026-10-13
- **Skills assigned:** `marlo-verify`, `error-handling-patterns` (listed), plus `tdd` because the task orders the tests first. Effort `high`. Review: `full`.

#### Attempt 1

- **Files:**
  - `marlo-web/.../utils/OtpMailSender.java` (new)
  - `OtpMailSenderTest.java` (new)
  - `global.properties` (+7 lines: `login.otp.email.appName`, `subject`, `text`, `html`; the file stays pure ASCII)
- **Behaviour:**
  - The JavaMail session is built per send, with connect, read and write timeouts. The value is parsed from `getOtpSmtpTimeoutMs`, default 10000.
  - The executor runs 2 daemon `otp-mail-N` threads with a queue of 100 and `AbortPolicy`. A rejection is caught and logged as `email_failed`.
  - The body is rendered on the caller thread.
  - The recipient must be exactly one bare ASCII address; 16 bad recipients are tested.
  - Nothing goes to `email_log`. The notification and redirect flags are never read.
- **Red:** 10 of 11 tests failed against a no-op stub (`expected:<1> but was:<0>`). **Green:** 11/11, full suite 628/0.
- **Falsifier deviation:** the task's runtime falsifier (route the send through `SendMailS`, then watch the `EmailLogManager` fake) cannot attach, because the mailer has no `EmailLogManager` dependency. It was replaced by a static scan for `SendMailS`, `ThreadSendMail`, `EmailLog*`, `email_log`, `isProduction`, the flag and redirect methods, and `CustomParameter`. The scan went red when a `SendMailS` reference was added.
- **Queue-full proof:** a latch holds 2 workers busy with 100 sends queued. The 103rd submission is rejected and logs a WARN before the latch is released.
- **Compile:** 2415/1060/68. **Checkstyle:** 0. **i18n check:** no FIX findings.
- **Evidence re-run (Leader inline, non-author): VERIFIED.**
  - wipe, compile and test in one command: exit 0, 628/0;
  - Checkstyle 0; i18n check with no FIX findings;
  - the added keys hold no unpaired `'` and no non-numeric `{…}`; the properties diff adds 7 lines only.
- **Reviewer verdict: `FAIL`** (`akili-reviewer`, `opus`; Implementer `sonnet`). Verbatim:

  > 1. **Discovered Issue:** Nothing makes the "2 threads, queue of 100" limit apply across the whole application. `OtpMailSender` is not a bean: it has no `@Named`, no `@Inject` constructor and no lifecycle hook. "The app must hold a single instance" is only a Javadoc request. No task in the plan wires it: design §3 lists the class as new with no wiring, and T10's Files list is the action and its test only. T10 is a Struts action, created per request, so it can only call `new OtpMailSender(config)` (a new pool every request, each leaving a core thread that never ends) or edit T09's file outside its scope. Also, `shutdown()` has no caller. The workers are daemon threads, but they keep the webapp classloader alive across Tomcat redeploys.
  >    * **Violated Rule:** design.md §5.3, `OtpMailSender` row: "a dedicated executor: 2 daemon threads, a bounded queue of 100". That limit only holds if there is exactly one instance. Also NF-001 / OPS-002, whose rejection path depends on that one bounded queue.
  >    * **Remediation Suggestion:** Make it a container singleton in this task, following the `SendMailS` pattern (`@Named` at `SendMailS.java:58`). Add `@Named` (with `@Singleton` if the scan does not default to singleton), put `@Inject` on the `OtpMailSender(APConfig)` constructor, and annotate `shutdown()` with `@PreDestroy`, or register an equivalent destroy hook. Keep the package-private constructor that takes the transport seam for tests. Add a test asserting the annotations are present and that `shutdown` is the destroy hook.

  - **Passed on review:** SEC-001, FN-017, FN-018/OQ-5, recipient validation, thread safety, i18n. The falsifier substitute is accepted and recorded; the runtime proof is AC-11's `email_log` SELECT in T15.
  - **ADVISORY:**
    - the timeout has no upper cap (suggested 30000);
    - `Error` is not caught on the worker;
    - T10 should derive `expiresAt` from the same 5-minute constant the email cites;
    - the charset is spelled two ways;
    - the static scan is relative to the base directory.
- **Leader adjudication:** the FAIL is in scope. The design's single bounded pool requires a single instance, and no later task owns the wiring. The rework is attempt 2, with effort raised to `xhigh`. Because `@Named` enters component scan, the rework must prove the Spring context still starts.
- **spawns:** implementer 29 calls, 144,839 tokens, ended complete; reviewer 17 calls, 75,588 tokens, ended complete (FAIL).

#### Attempt 2 — rework after the FAIL (effort `xhigh`)

- **Changes:**
  - `OtpMailSender` is `@Named`; the public `OtpMailSender(APConfig)` constructor is `@Inject`; `shutdown()` is `@PreDestroy`. The test-seam constructor stays package-private and has no `@Inject`.
  - Advisories also applied:
    - the parsed timeout is clamped to 30000 ms;
    - the worker catches `Throwable` and logs the class only;
    - one `CHARSET = "UTF-8"` constant.
- **Scope, checked at source:**
  - `ApplicationContextConfig` and `MarloDatabaseConfiguration` both scan the class into the same root context, which yields one bean.
  - There is no `@Scope`, so it is a singleton.
  - `@PreDestroy` is honoured through `CommonAnnotationBeanPostProcessor`; javax.annotation-api 1.3.2 is present.
- **Red:** 4 of 14 failed, including `exactly one bean, found by @Named expected:<1> but was:<0>`. **Green:** 14/14; full suite 631/0.
- **Startup proof:** `./scripts/run-marlo-java17.sh` without `OTP_HMAC_SECRET`.
  - `login.do` returned HTTP 200.
  - `grep -cE "SEVERE|Error creating bean|OtpMailSender"` on the run log returned 0.
  - The app was stopped afterwards (`kill -9` after a slow Cargo shutdown).
- **Compile:** 2415/1060/68. **Checkstyle:** 0. **i18n:** no FIX findings.
- **Evidence re-run (Leader inline, non-author): VERIFIED.**
  - The first run failed with dirty-tree compile errors: `cannot access User` and `User cannot be converted to User`. That is the concurrent-writer signature; the VS Code JDT language server (PID 2745) was writing `target/`.
  - One retry, with wipe, compile and test in a single command: exit 0, 2415/1060/68, 631 tests, 0 failures, 0 "cannot access" lines.
  - Checkstyle 0. No cargo process left running.
- **Reviewer verdict: `PASS`** (`akili-reviewer`, `opus`; Implementer `sonnet`).
  - The FAIL is resolved: one container instance (the dispatcher context scans `.rest` only), no other `new OtpMailSender(`, an unambiguous `@Inject` constructor, and a `@PreDestroy` proven by behaviour in a Spring test.
  - Catching `Throwable` keeps exactly one delivery line per hand-off.
  - The Spring test is contained.
  - No attempt-1 PASS item regressed.
- **Decisions made:**
  - The SMTP timeout clamp of 30000 ms is a deviation from design §5.3. The design names only the 10000 default and no cap; the clamp only tightens NF-001. Recorded here; it goes into T16's runbook next to `otp.smtp.timeout.ms`.
  - The falsifier substitute (a static scan, not the runtime `EmailLogManager` fake) is accepted. The runtime proof is AC-11's `email_log` SELECT in T15.
- **ADVISORY (recorded, not gating):**
  - A value above 30000 is clamped silently; a one-time WARN is optional.
  - On the caller thread, `sendAsync` catches `MessagingException | RuntimeException` but not `Error`, which contradicts the "never throws" Javadoc.
  - T10 must inject the mailer, never `new` it.
- **Forward pointers (Leader):**
  - **T10:**
    - Inject `OtpMailSender` with `@Inject`; never construct it.
    - Pass `pending.email` as the recipient.
    - Derive `expiresAt` from the same 5-minute constant the email cites (`OtpMailSender.CODE_VALIDITY_MINUTES` or a shared constant), so the copy and the real expiry cannot drift.
    - Map any `Error` from `sendAsync` to the neutral response.
  - **T15:** AC-11 is the runtime proof that nothing reaches `email_log`.
  - **T16:** document `otp.smtp.timeout.ms` (default 10000, clamped to 30000).
- **runtime events:** none (the dirty-tree compile is an environmental retry of the Leader's re-run, not a worker event).
- **spawns:** implementer attempt 2, 17 calls, cumulative 176,546 tokens, ended complete; reviewer 9 calls, 66,038 tokens, ended complete.
- **Final verification:** VERIFIED; Reviewer PASS.

### CHG-OTP-LOGIN-001-T10 — `OtpRequestCodeAction`

- **Status:** PASS (attempt 1)
- **Date:** 2026-10-13
- **User-approved scope amendment, made before the spawn:** no task owned the Spring wiring of `OtpKeys`, `OtpRateLimiter`, `OtpCodes` and `OtpEligibility`. That is the same gap as the T09 FAIL. Building them per Struts request would reset the purge throttle and repeat the unconfigured WARN. The user chose to widen T10 rather than add a separate task. `tasks.md` T10 gains the four files: `@Named` plus `@Inject`, and a startup proof. Requirements are unchanged.

#### Attempt 1

- **Files:**
  - New: `marlo-web/.../action/home/OtpRequestCodeAction.java`, and `OtpRequestCodeActionTest.java` with 40 tests.
  - Changed: `@Named` plus `@Inject` on `security/otp/OtpKeys`, `OtpRateLimiter`, `OtpCodes` and `OtpEligibility`, +35 lines (the amended scope).
- **Surface:**
  - Extends `BaseAction`, with setters `email`, `globalUnitId` and `agree`.
  - The `response` is `{status, destination?}` and defaults to `unavailable`. `execute()` always returns SUCCESS.
  - Seams: a `CodeSender`, plus a package-private constructor that takes a `ClientIpResolver` and a `Clock`.
- **Forward pointers:**
  - Met: 1–5, 7–11. Pointer 10 is met by mapping a predicate exception to the neutral `sent` (logged `internal_error`), because `unavailable` would be an oracle.
  - Not met: 6, the cooldown query timeout, which lives in marlo-data outside the file list. And 12, the `ReturnUrls` "Cognito login:" label, which spans 3 literals.
- **FN-005 mapping:**
  - `everyIneligibleStateAnswersExactlyAsTheEligibleOneApartFromTheMaskedEmail`
  - `aRowIsIssuedInEveryAccountStateAndOnlyTheEligibleOneIsMailed`
  - `aResendIsAnsweredLikeAnEligibleOneForAnIneligibleAccountToo`
  - `noResponseValueNamesTheCause…`
  - `anAccountThatCannotBeEvaluated…`
  - The verify half belongs to T11.
- **Red:** 34 of 40 tests failed, for example `aRowIsIssuedInEveryAccountStateAndOnlyTheEligibleOneIsMailed … expected:<1> but was:<0>`. **Green:** 40/0; full suite 671/0.
- **Falsifier F1** (skip `issue()` for ineligible accounts): 5 tests went red. All 14 mutants were killed.
- **Startup proof:**
  - `login.do` returned 200 without the secret.
  - The `OtpKeys` unconfigured WARN appeared once and stayed at 1 after 3 more requests, which proves the bean is a singleton.
  - SEVERE and "Error creating bean": 0. The app was stopped afterwards.
- **Compile:** 2415/1061/69. **Checkstyle:** 0.
- **Implementer-declared gaps:**
  - `BaseAction`, not `LoginAction`.
  - `ClientIpResolver` is built per request.
  - Each request makes 2 allow-list reads.
  - The pending value lives in the Shiro session.
  - If the session write fails after the mail is handed off, the response is `unavailable` even though a code was mailed.
  - Struts registration and the parameter allow-list are T12's work.
- **Evidence re-run (Leader inline, non-author): VERIFIED.** One wipe+compile+test command: exit 0, 2415/1061/69, 671 tests, 0 failures, 0 dirty-tree lines. Checkstyle 0 on the 6 files.

#### Reviewer verdicts — attempt 1 (parallel lenses, `opus`; Implementer `sonnet`)

- **Anti-enumeration and security lens: `PASS`.**
  - Eligible and ineligible accounts get the same map, key order, HTTP status, row issue, limiter consumption and session write.
  - Only two things differ, both accepted: the mail hand-off is a timing residual (§13, RJ-10), and the outcome is server-side log only.
  - A session failure after the mail is state-independent: only the mailbox owner can see it.
  - Answering `sent` on a predicate exception conforms. The shared "" bucket cannot collide with a real email.
  - Running the unconfigured check first conforms to SEC-008 and §4.
  - SEC-001 holds, and the recipient is `pending.getEmail()`.
- **Correctness lens: `PASS`.**
  - §5.1 steps 1–11 are as specified.
  - DD-5 holds.
  - The issue is `REQUIRED` and the purge `REQUIRES_NEW`; a purge failure still yields `sent`.
  - There is exactly one request line on every path.
  - Expiry and purge use a single clock.
  - There is no `validate()`.
  - Each of the four beans has exactly one instance, there is no cycle, and nothing calls `new`.
  - The session write uses `setAttribute` on the Shiro session, which replaces any earlier value; this matches DD-2.
  - The exception mapping follows §4.
- **Execute-time spec edits (recorded now, meaning-preserving; carried into the next Reviewer brief):**
  - design.md §2: `OtpRequestCodeAction` extends `BaseAction`, and only `OtpVerifyCodeAction` extends `LoginAction`.
  - design.md §5.1 step 3: the configured check runs **before** step 1. This resolves an inconsistency inside the design, because the limiter answers `unavailable` when there are no keys, which contradicts §4.
  - design.md §12 "Load": the allow-list is read 2 times, not 1.
- **ADVISORY (recorded, not gating):**
  - *Reliability:* `isEligible` catches only `RuntimeException`, so an `Error` from the predicate would become a 500. Catch `RuntimeException | Error`, as `handOff` does.
  - *Reliability:* `ClientIpResolver` is built per request, so a malformed proxy-list WARN repeats on every request. Candidate for a fifth bean.
  - *Resilience:* "after the issue commits" is not proven at runtime. `MARLOCustomPersistFilter` opens a request-wide transaction; if `issue` joins it, the purge (`REQUIRES_NEW`) could wait on the supersede's locks.
  - *Readability:* `logFault` writes a second line, `auth.otp.fault`, on fault paths.
  - *UX:* a request refused with `termsRequired` still starts the 30-s cooldown.
  - *Resilience:* a session failure after the mail costs usability only.
- **Forward pointers (Leader):**
  - **T11:**
    - Read `OtpPending` from the **Shiro Subject session** under `APConstants.OTP_PENDING_CHALLENGE`.
    - Catch `RuntimeException | Error` around the eligibility re-check.
    - Inject the beans; never `new` them.
    - Consider making `ClientIpResolver` a bean only if it is in T11's file list; otherwise leave it as recorded.
  - **T12:** the parameter allow-list permits `email`, `globalUnitId` and `agree` (Strings) on `otpRequestCode`. This is the first runtime proof of the action's Spring wiring.
  - **T14:** block an unchecked-terms submit on the client, because the cooldown starts even when the server refuses for `termsRequired`.
  - **T15:**
    - Prove `issue` commits before the purge (no request-wide transaction holding the supersede's locks).
    - Confirm that AC-11 counts `request` lines, not `auth.otp.fault`.
  - **T16:** note that a session failure after the mail yields `unavailable` with a valid code already mailed; a retry supersedes it.
- **runtime events:** none.
- **spawns:** implementer 94 calls, 360,532 tokens, ended complete; reviewer (enumeration) 16 calls, 76,084 tokens, ended complete; reviewer (correctness) 27 calls, 94,848 tokens, ended complete.
- **Final status:** PASS (attempt 1). **Final verification:** VERIFIED; both lenses PASS.

### CHG-OTP-LOGIN-001-T11 — `OtpVerifyCodeAction`

- **Status:** PASS (attempt 1)
- **Date:** 2026-10-13
- **Requirements:** FN-011–FN-016, SEC-004, SEC-005, SEC-006 (verify side), SEC-007, OPS-001
- **Skills assigned:** `marlo-verify`, `tdd`, `error-handling-patterns` (listed). Effort `xhigh`. Review: `lenses`, with three parallel reviewers (security, concurrency, session parity).
- **Forward pointers carried in the brief:** 1–10, compiled from T03, T05, T06, T07, T08 and T10.

#### Attempt 1

- **Files (new):** `marlo-web/.../action/home/OtpVerifyCodeAction.java` (442 lines) and `OtpVerifyCodeActionTest.java` (1706 lines, 47 tests).
- **Surface:**
  - The class extends `LoginAction`. It exposes `setCode`, `getResponse` and `execute`; `execute` always returns SUCCESS.
  - One `@Inject` constructor takes 10 parameters. A package-private seam takes `ClientIpResolver` and `Clock`.
  - Statuses: `ok`, `mismatch`, `exhausted`, `invalid`, `rateLimited`, `unavailable`, `failed`.
- **Steps:**
  - Steps 1–3 run in `process()`.
  - Step 4 is the Java pre-checks. They can only refuse.
  - Steps 5–6 call `OtpCodes.matches` and then `registerMismatch`.
  - Step 7 is `consume`.
  - Step 8 removes the pending challenge and then calls `isEligible`.
  - Steps 9–10 are `signIn()`.
  - Step 11 writes one `OtpLog.verify` line.
- **Transactions:** the action opens no transaction. `findByNonce` is a plain read. `registerMismatch` and `consume` are each `@Transactional`.
- **Forward pointers:**
  - 1–8 are met. The only `new OtpAuthenticationToken` in `src/main` is at `:406`, after consume and the re-check, and a test pins it. Expiry is never accepted on Java's word alone (2 tests). The re-check catches `RuntimeException | Error`.
  - 9 does not apply (`ReturnUrls` is unused). 10 is left as is (`ClientIpResolver` per request).
- **Clause mapping:**
  - FN-012: `wrongCodesCountAndTheThird…`, `anExhaustedChallengeWithTheCorrectCode…`
  - FN-013: `aSecondSubmissionOfTheSameCode…`, `aConcurrentConsumeThatWon…`, `aWrongCodeOnARowConsumedMeanwhile…`
  - FN-014: `anExpiredChallenge…`, `aSupersededConsumedOrPurged…`, `expiryIsNeverAcceptedOnJavasWord…`
  - FN-015: `anAccountThatLostEligibility…` (5 conditions), `aDecoyGuessed…`, `everyExceptionOfTheEligibilityRecheck…`
  - FN-016: `noPendingChallenge…`, `afterAnyRequestTheVerifySequenceIsTheSame…`
  - SEC-005: `theSessionIdIsRotated…`, `theStoppedSessionView…`, `aPostActionAccess…`
- **Red (gate-4, real `LoginAction.finishLogin` with fake managers):** `expected:<{status=failed, redirect=…authError=cognitoFailed}> but was:<{status=unavailable}>`.
- **Falsifier (`setUser(detached)` deleted):** 3 tests go red, and the gate-4 test reports `unexpected fault: [auth.otp.fault stage=verify cause=NullPointerException]`. 14 further mutants were all killed.
- **Green:**
  - own tests 47/0;
  - `CognitoCallbackActionTest` 30, `CognitoLogHygieneTest` 17, `LoginActionFinishLoginTest` 17, all with 0 failures;
  - full suite 718/0.
- **Compile:** 2415/1062/70. **Checkstyle:** 0.
- **Issues encountered:** two of the Implementer's Maven runs died on JDT `target/` interference. Both were re-run clean.
- **Evidence re-run (Leader inline, non-author): VERIFIED.**
  - One wipe+compile+test command: exit 0, 2415/1062/70, 718 tests, 0 failures, 0 dirty-tree lines.
  - Checkstyle 0.
  - `grep -rn "new OtpAuthenticationToken" marlo-*/src/main` returns exactly `OtpVerifyCodeAction.java:406`.
- **Execute-time spec edit, made before the reviews and named in all three Reviewer briefs:** design §5.2 and tasks T11 now give the `failed` redirect as `authError=cognitoFailed` (`LoginAction.AUTH_ERROR_FAILED`, Cognito V-7 parity; `CognitoCallbackAction:552`).
  - The design's literal `authError=failed` was shorthand. `loginForm.ftl` renders only `cognitoFailed`.
  - The sweep found no `authError=failed` left in the spec folder.

#### Reviewer verdicts — attempt 1 (three parallel lenses, `opus`; Implementer `sonnet`)

- **Security lens: `PASS`.**
  - `process()` is linear. Only `consume == true` and then `isEligible == true` reach `signIn`, and every earlier exception answers `unavailable` with no login.
  - Decoy and real challenges, and eligible and ineligible accounts, answer identically: an ineligible account or a predicate failure answers `invalid`, the same as an expired code.
  - The pending challenge is removed right after a successful consume, and a failed re-check leaves the row consumed.
  - The unit, email, return URL and agree flag all come from `OtpPending`.
  - No code, nonce, MAC, email or session id appears anywhere.
- **Concurrency lens: `PASS`.**
  - Mixed bursts are serialised by the row lock, and every order is consistent with FN-012.
  - Of two correct submissions, exactly one wins and the loser gets `invalid`.
  - `MARLOCustomPersistFilter` most likely never wraps `.do` (match-after, and struts2 ends the chain), so each manager call commits on its own under autocommit.
  - The step-8 ordering leaves no window. The limiter runs before any lookup and fails closed.
- **Session-parity lens: `PASS`.**
  - The sequence matches `CognitoCallbackAction:473-558` step by step. The justified differences are:
    - the user is loaded by the pending email;
    - `agree_terms` is set only when `pending.agree`;
    - there is no `applyNamesFromToken`;
    - the redirect is built directly, which is equivalent to `${crpSession}`.
  - P-27 is confirmed: `setUser` gets the detached copy and `finishLogin` gets the loaded entity.
  - Every failed path goes through the same `cognitoFailed` redirect.
  - SEC-007 holds: the only writes are 2 `saveLastLogin` calls.
- **ADVISORY (recorded, not gating):**
  - *Risk (security):*
    - A `RuntimeException` after `Subject.login` (for example in `finishLogin`'s custom parameter read, or an NPE on a null `GlobalUnitType`) answers `unavailable` while the subject stays authenticated.
    - The unmapped-unit-type `INPUT` branch of `finishLogin` also never logs out.
    - Both are parity with Cognito's accepted "route C" gap. Suggested hardening: call `Subject.logout()` on any failure after login.
  - *Reliability:* a user deactivated between the re-check and `signIn`'s reload would still get in. The window is milliseconds, the same as password login.
  - *Risk (drift):* `freshSessionMap` and the session-rotation block now exist in two copies (OTP and Cognito). Extract them into a `LoginAction` helper, or cross-reference them.
  - *Concurrency:* an email-limited request is not counted against the IP bucket. That is T04 behaviour.
  - *Concurrency:* a wrong code that crosses expiry answers `mismatch`, which conforms.
  - *Concurrency:* a late `invalid` in a double-submit tab should be ignored by the client after a redirect (T14).
  - *Readability:* the success redirect could use `getCrpSession()`.
- **Forward pointers (Leader):**
  - **T12:** `ParameterNameAware` on both actions. It must refuse the `LoginAction` setters (`url`, `user`, `crp`, `globalUnit`) and every other parameter except `code` on `otpVerifyCode`, and except `email`, `globalUnitId` and `agree` on `otpRequestCode`.
  - **T14:** after a redirect starts, ignore any late verify response.
  - **T15:**
    - Confirm at runtime that `MARLOCustomPersistFilter` does not run for `/otpVerifyCode.do` and `/otpRequestCode.do`, and that connections are in autocommit outside `@Transactional`.
    - Run the AC-7 and FN-012 mixed-burst probes against the real database.
    - Probe a non-UTC JVM for false refusals.
    - Probe the post-login failure path: does a failure after `Subject.login` leave an authenticated session?
  - **T16:** record the post-login failure (`unavailable` or `failed` while authenticated) as a known gap shared with Cognito route C, unless the user approves the `logout()` hardening.
- **runtime events:** none. The Implementer's two JDT-interference reruns were environmental retries, not worker events.
- **spawns:**
  - Implementer: 75 calls, 378,763 tokens, ended complete.
  - Reviewer (security): 17 calls, 76,782 tokens, ended complete.
  - Reviewer (concurrency): 20 calls, 85,358 tokens, ended complete.
  - Reviewer (parity): 13 calls, 73,644 tokens, ended complete.
- **Final verification:** VERIFIED; all three lenses PASS.

#### Budget note (design §18 tripwire)

- **Review rounds:** 19 of the 23 budgeted are used after T11, with 11 of 16 tasks done.
- **Projection:** T12–T16 need at least 5 rounds, with 4 left, so the spec will exceed its budget by at least 1 round even if every task passes first time. Flagged to the user at the T11 gate, before the budget is exceeded.

#### User decisions at the T11 gate (2026-10-13)

- **Budget:** the user approved extending the review-round budget from 23 to **28**. The cause of the overrun is recorded above: quality rounds the user chose (the amendments in T01, T05 and T10, the T04 Pivot), the real FAIL in T09, and the parallel-lens reviews in T03, T04, T10 and T11. A new tripwire stop fires if 28 is exceeded.
- **Post-login failure gap:** not hardened in T11. **Pending item for T16:** record it in the runbook as a known gap shared with Cognito route C: a `RuntimeException` after `Subject.login`, or the unmapped-unit-type `INPUT` branch, leaves an authenticated session behind an `unavailable` or `failed` answer. A `logout()` hardening, if wanted, belongs in a separate task that covers both flows.

#### Off-state audit before T12 (2026-10-13, user request)

The user asked to confirm, before T12, that the feature is a specificity and that, when it is off, login behaves exactly as before the change. Leader audit of the committed code (T01–T11):

- **Specificity:** `crp_otp_allowed_email_domains` is a `parameters` row per Global Unit type (DA-002), with constants equal to the key in both `APConstants.java` files. The per-unit value comes from `custom_parameters`. `OtpChallengeMySQLDAO.ALLOW_LIST_SQL` resolves it the MARLO way: an active `custom_parameters` row wins, otherwise `parameters.default_value`. The read is uncached, so a change applies on the next request (FN-002).
- **Shipped off:** the migration seeds `default_value = ''` and inserts no `custom_parameters` row. An empty list is off for that unit (FN-001).
- **Two switches:** per unit, the allow-list. Global, `otp.hmac.secret`: unset makes `OtpKeys.isConfigured()` false. `OtpRequestCodeAction` then refuses before any database access; `OtpEligibility` returns false; `OtpRateLimiter` answers unavailable.
- **Off at the server:** with the secret set but the unit's list empty, a direct `otpRequestCode` call is refused with `domain_not_enabled`. No challenge row, no email, no session write. Only the rate-limit counters are touched, by design (step 1 limits before any lookup).
- **Existing login paths:**
  - `LoginAction` and `ValidateUserAction` are not modified.
  - `APCustomRealm` gains only a `supports()` term and a branch reachable solely through `OtpAuthenticationToken`, which is built in one place: `OtpVerifyCodeAction` after a consumed code.
  - `CognitoLoginAction` changed by the T08 behaviour-preserving extraction to `ReturnUrls`, covered by its existing tests.
- **Idle cost:** `OtpMailSender` creates its worker threads lazily, on the first send. With the feature off, no thread runs.
- **Remaining off-state obligations, carried into the open tasks:**
  - **T12:** the new endpoints refuse with the feature off. Adding the `homeOtp` package must not alter any existing package or stack.
  - **T13:** when unconfigured, no allow-list query runs. An allow-list read failure must fail closed (every flag `false`) and must never break `crpByEmail.do`. Every existing key stays unchanged.
  - **T14:** with `otpEnabled` false, the page is visibly and behaviourally identical. No OTP control shows, and no extra request is sent.
  - **T15:** run AC-1 (every list empty, existing login tests unchanged, no OTP control).
- **Conclusion:** the off-state contract (FN-001, FN-019, the §8 rollback) holds for T01–T11. No spec change is needed.

### CHG-OTP-LOGIN-001-T12 — Struts `homeOtp` package

- **Attempt 1 — Implementer:** STATUS DONE.
  - `struts-home.xml`: +49/-0, `struts.xml` untouched. The `homeOtp` package (`namespace="/"`, `extends="marlo-default,json-default"`); `otpUnloggedStack` = `logErrorStatus` → `i18nFile` → `defaultStack`; a global `input` result plus a `success` result per action, all `json` with `root=response`, `noCache` and `excludeNullProperties`.
  - `OtpRequestCodeAction` and `OtpVerifyCodeAction` implement `ParameterNameAware`. The exact allow-lists are `{email, globalUnitId, agree}` and `{code}`.
  - New `OtpStrutsPackageTest` (8 tests); `ErrorStatusLogStackCoverageTest` gains 1 homeOtp guard.
- **P-26 settled:** the multi-parent `extends="marlo-default,json-default"` loads. Proven by `homeOtpIsRegisteredAndExtendsBothMarloDefaultAndJsonDefault`, which loads the real `struts-default.xml`, the json plugin's `struts-plugin.xml` and `struts.xml`, and by the live curl. The DD-1 fallback was not needed.
- **P-21 settled:** neither consumer test pins an action list. `ErrorStatusLogStackCoverageTest` enumerates every `struts*.xml` generically (≥440 actions; exemptions only `homeJson/crpByEmail`, `homeJson/validateUser`, `api/institutions/*`), and it now guards that both OTP actions stay on `otpUnloggedStack` and are not exempted. `CognitoUnloggedStackReachabilityTest` pins only `ValidCrpActionInterceptor` against `cognitoLogin`; it is unchanged and green.
- **"Invalid conversion" bullet:** unreachable by construction. Every accepted setter (`setEmail`, `setGlobalUnitId`, `setAgree`, `setCode`) takes `String` and the allow-list is exact, so no accepted parameter can fail conversion. The substitute test (a field error in `prepare()`, the real `workflow` returns `input`, the package's global JSON result renders `{"status":"unavailable"}`) proves what RJ-9 needs. The Reviewer accepted it on that basis.
- **Red run:** before the package: `Tests run: 8, Failures: 3, Errors: 4` ("struts-home.xml must declare the package homeOtp (DD-1)"; `default-action-ref` fell through to `LoginAction`).
- **Falsifier:** removing `root=response` gave `Failures: 3`, including "root must be response expected:<response> but was:<null>". The XML was then restored.
- **Implementer verification:**
  - Full suite 727/727 (718 + 9), BUILD SUCCESS.
  - Compile: 2415 / 1062 / 71 files.
  - Checkstyle delta 0 on all four changed files.
- **Live run** (`scripts/run-marlo-java17.sh`, no `OTP_HMAC_SECRET`):
  - Spring wiring succeeded; the log shows `OtpKeys ... outcome=unconfigured`.
  - `otpRequestCode.do` answers 200 `{"status":"domainNotEnabled"}` with no parameters, with valid parameters, and with injected `crpSession`/`user.email`/`url`.
  - `otpVerifyCode.do` answers 200 `{"status":"unavailable"}`.
  - `login.do` answers 200.
  - Read-only `otp_challenges`/`otp_rate_limits`/`otp_cooldowns` counts were 0/0/0 before and after.
  - The app was stopped afterwards.
- **Off-state:** confirmed. The XML diff is purely additive, and with the feature unconfigured the endpoints refuse and write nothing.
- **Evidence re-run (Leader, non-author):**
  - Clean wipe and compile: 2415/1062/71 files.
  - Tests: `OtpStrutsPackageTest` 8, `ErrorStatusLogStackCoverageTest` 4, `CognitoUnloggedStackReachabilityTest` 1, `OtpRequestCodeActionTest` 40, `OtpVerifyCodeActionTest` 47; 100/100 green, BUILD SUCCESS.
  - Result: **VERIFIED**.
- **Attempt 1 — Reviewer (full):** **PASS**. Conditions (bookkeeping, recorded above): P-21/P-26 settlement, and the conversion bullet recorded as unreachable by construction.
- **Advisories (not rework):**
  - *Readability:* rename `anInvalidConversionYieldsTheJsonInputResult` to say "workflow input interruption"; replace inline fully qualified names with imports.
  - *Reliability:* no test shows an *accepted* parameter reaching the action through the package. Unconfigured, both actions answer before reading `email`/`code`.
  - *Risk:* `marlo-default`'s global exception mapping means an exception thrown by an interceptor (for example `i18nFile`) answers the HTML error page, not JSON. `cognitoUnloggedStack` shares the exposure.
- **Pre-existing, outside scope:** `crpByEmail.do` without `userEmail` throws `IndexOutOfBounds` in `CrpByUserEmailAction.prepare` (line 155). T13 touches this file; it is noted for its brief, not fixed here.
- **Observation:** unconfigured verify logs `auth.otp.verify outcome=internal_error` (T11 behaviour, limiter UNAVAILABLE), while unconfigured request logs `unconfigured`.
- **Forward pointers (Leader):**
  - **T13:** the `CrpByUserEmailAction.prepare:155` IndexOutOfBounds on a missing `userEmail` predates this spec. Keep it out of scope unless the user approves; T13 must not make it worse.
  - **T14:** the client must handle a non-JSON (HTML error page) answer from both endpoints as `unavailable`.
  - **T15:** with the secret configured, prove that `email`/`globalUnitId`/`agree` and `code` bind through the real package (a sent code on an allowed domain; a verify that reaches the store).
- **runtime events:** none.
- **spawns:** Implementer, 53 calls, 193,322 tokens, ended complete. Reviewer, 19 calls, 70,661 tokens, ended complete.
- **Review rounds:** 20 of 28.

### CHG-OTP-LOGIN-001-T13 — `crpByEmail.do` `otpEnabled` flag

- **Attempt 1 — Implementer:** STATUS DONE.
  - `CrpByUserEmailAction` takes `OtpEligibility` as a sixth constructor argument. A private `otpEnabledByUnit` calls `otpEnabledFor(userEmail, usrDB.isCgiarUser(), globalUnits)` once per request, before the unit loop.
  - Any `RuntimeException` is caught and logged null-safely (fixed message plus the exception), and an empty map is returned, so every flag is `false`.
  - Each unit entry gains `otpEnabled` after the existing keys. `prepare()` is untouched.
  - `CrpByUserEmailActionTest` gains 8 tests (13 in the class). The stub `getGlobalUnitById` now records calls and returns the listed unit; the Reviewer confirmed no pre-existing assertion depended on its throw.
- **Red run:** compile error only ("actual and formal argument lists differ in length"). It is weak, and the falsifier carries the behavioural proof.
- **Falsifier:** computing the flag with `isEligible` per unit turned 5 of 13 red, including `otpEnabledIsIdenticalForAnActiveAndAnInactiveUserAndForMemberAndNonMemberUnits` ("expected:<{AAA=true, BBB=false}> but was:<{AAA=false, BBB=false}>") and `oneAllowListQueryIsMadePerCallWhateverTheNumberOfUnits` (expected 1, was 2). Production code restored afterwards.
- **Implementer verification:** full suite 735/735, BUILD SUCCESS. Checkstyle delta 0.
- **`crpUsers.js` read (Done-when):** `findUserEmail()` (~lines 211–245) posts `userEmail`, checks `data.user == null`, loops `data.crps` comparing `crp.acronym`, and reads `data.user.name`. It never iterates an entry's keys, so the additive `otpEnabled` key cannot affect it. The Implementer read it, and the Reviewer re-read lines 229–232.
- **Off-state / fail-closed:**
  - `unconfiguredMakesNoAllowListQueryAndEveryFlagIsFalse`: 0 queries, all `false`.
  - `aFailingAllowListReadFailsClosedAndLeavesTheLoginLookupUnchanged`: all `false`, normal response, `cognitoEnabled` unchanged.
  - `everyPreExistingKeyAndValueIsUnchangedOnAndOff`: the complete key sets of `user` and each `crps[]` entry.
- **Evidence re-run (Leader, non-author):** after a clean wipe, `CrpByUserEmailActionTest`, `CognitoLoginActionTest`, `LoginActionCgiarGuardTest`, `ValidateUserActionGuardTest`, `ErrorStatusLogStackCoverageTest` and `OtpEligibilityTest` ran 85/85 green, BUILD SUCCESS. **VERIFIED.**
- **Live check (Leader):** `scripts/run-marlo-java17.sh` without `OTP_HMAC_SECRET`.
  - There were no bean-creation errors, so Spring builds the 6-argument constructor.
  - `crpByEmail.do` was called for an active member account: `user` keys are `agree`, `isCgiarUser`, `name`. Each unit (AICCRA, AICCRA_III) has keys `acronym, cognitoEnabled, id, idType, name, otpEnabled, type`, with `otpEnabled=false`.
  - Read-only `otp_*` counts: 0/0/0.
  - The app was stopped afterwards.
- **Attempt 1 — Reviewer (checklist):** **PASS**. No findings. The constructor-wiring gap was closed by the live check above.
- **Note:** username input (no `@`) gives every flag `false`, so OTP is offered only to an email entry. The logger declared against `FlaghshipsByCrpAction.class` predates this change.
- **runtime events:** none.
- **spawns:** Implementer, 19 calls, 108,132 tokens, ended complete. Reviewer, 9 calls, 49,792 tokens, ended complete.
- **Review rounds:** 21 of 28.

### CHG-OTP-LOGIN-001-T14 — Login page: FTL, JS and copy

- **User decisions (2026-10-13), before the walkthrough:**
  - The feature-on walkthrough may write a **temporary** allow-list row to `custom_parameters` in the local `aiccradb_actsave` for AICCRA. The row is removed afterwards, and the before and after states are recorded here.
  - A temporary `OTP_HMAC_SECRET` lives in the environment only.
  - The success state uses a non-CGIAR test mailbox that the user supplies and reads.
- **Attempt 1 — Implementer:** STATUS DONE.
  - `loginForm.ftl`: hidden `#login-step-otp`, `#login-otp-request-container`, `.otpRequestError` and the `#login-otp-copy` spans.
  - `login.js`: +285 lines, 1 changed line.
  - `global.properties`: 17 `login.otp.*` keys.
  - The cache-buster `?20261013` is in `login.ftl` and `error/401.ftl`.
  - Evidence: `node --check` OK; `i18n-check` no findings; clean compile 2415/1062; off-state live smoke (`login.do` 200, bumped JS served, OTP markup hidden, `otpEnabled=false`, `otpRequestCode` answers `domainNotEnabled`).
- **Evidence re-run (Leader, non-author):** `node --check` OK; the diff stat matches (+358/-3); no line-ending change; no apostrophe in the added properties lines. **VERIFIED**.
- **Attempt 1 — Reviewer (full):** **FAIL**.
  - Issue 1: "Go back" stays active while a code request is in flight. `showProjectStep()` does not bump `otpRequestSeq`, so a late `sent` jumps from the project step to the code step for the unit the user left. Violates design §6 "Request sequencing" ("Stale responses are dropped") and UI-004.
  - Remediation: guard Go back on `otpRequestInFlight || otpVerifyInFlight`; bump `otpRequestSeq` and clear the in-flight state in `showProjectStep()`.
  - Advisories:
    - "Use a different email" stays enabled during a redirect.
    - `loadAvailableItems` clicks the AICCRA card before rewriting its `data-*`. The same flaw pre-exists for `cognitoEnabled`.
    - `.replace("__DEST__", …)` interprets `$` patterns.
    - `btn-link` colour and the `label` weight need the walkthrough.
    - The `failed` copy says "CGIAR sign-in" (a T11/design copy gap).
- **Attempt 2 — Implementer:** STATUS DONE, a delta of about 25 lines in `login.js` only.
  - The Go-back handler returns while `isSubmitting || otpRequestInFlight || otpVerifyInFlight`.
  - `showProjectStep()` bumps `otpRequestSeq`, clears both in-flight flags and refreshes the buttons.
  - `#login-otp-different` is a no-op and disabled while `otpRedirecting`.
  - `__DEST__` and `__N__` use function replacers.
  - Extra: `requestOtpCode` records `stepAtStart`, and a late answer is dropped if the step changed or a password submit started.
- **Evidence re-run (Leader):** `node --check` OK. **VERIFIED**.
- **Attempt 2 — Reviewer (full):** **PASS**.
  - Finding 1 is fixed on both sides (guard and invalidation).
  - The off-state is identical to HEAD: with both flags false, the Go-back guard reduces to HEAD's `if(isSubmitting)`, and the new lines touch only hidden OTP elements.
  - No stuck state: the 20 s timeout and the reset paths always clear the in-flight flag.
- **Advisory applied (Leader decision):** the same `stepAtStart`/`isSubmitting` drop rule is added to `failedRequest`, so a failed code request that lands during a password submit shows nothing.
- **Remaining advisories, for the walkthrough to judge:**
  - the `loadAvailableItems` ordering (pre-existing, unchanged);
  - the `btn-link` colour and the `label` weight;
  - the "CGIAR sign-in" wording on a `failed` answer.
- **Walkthrough checklist (Reviewer):**
  - Go back during an in-flight request with several units.
  - "Log in" during an in-flight request.
  - "Use a different email" disabled after an `ok` verify.
  - The `requestSeq` falsifier.
  - A double click, and Enter held down, each giving 1 request.
  - Every UI-002 state, with a real ellipsis and the 30→0 countdown.
  - Focus on open.
  - A code entered with reCAPTCHA showing.
  - Password-only and Cognito units identical to HEAD, including terms unchecked.
- **Status:** `[~]` until the feature-on walkthrough artifacts are recorded (Done-when).
- **Review rounds:** 23 of 28.
- **Feature-on walkthrough (Leader, claude-in-chrome, 2026-10-13):**
  - **Setup:**
    - Temporary `custom_parameters` row id 2609 (`parameter_id` 397, value `outlook.com`, AICCRA 45).
    - A random `OTP_HMAC_SECRET` in the environment only.
    - Test account id 3862 (`probe.user@outlook.com` (real address replaced)): active, non-CGIAR, active AICCRA member.
  - **SMTP:** the first attempt logged `auth.otp.delivery outcome=email_failed` (`SendFailedException`). Cause: the local `email.host` is CGIAR's inbound MessageLabs gateway, which refuses to relay to external domains (`453 … as a relay … (#4.7.1)`, probed with RCPT only, no message sent). This is an environment limit, not a code defect.
    - With the user's approval, the app was restarted with `EMAIL_HOST`/`EMAIL_PORT` (and the backups) pointing at a local SMTP sink, `scratchpad/smtp_sink.py` on 127.0.0.1:2525, set through environment variables only.
    - Nothing left the machine.
  - **Results:**
    - **UI-001:** "Email me a sign-in code" shows for the allowed unit.
    - **Terms:** with terms unchecked, the click shows the terms message and sends 0 requests.
    - **UI-004:** a double click on Request sent 1 `otpRequestCode.do` request.
    - **Code step:** the input is focused; the status reads "…a code is on its way to k***@outlook.com"; Resend counts down from 30 s.
    - **UI-003:** `inputmode=numeric`, `autocomplete=one-time-code`, `maxlength=6`, a `<label for>`, no `name`; `#login-otp-status` has `role=status aria-live=polite`; `#login-otp-error` has `role=alert`.
    - **Short code:** "123" shows "Please enter the 6-digit code." and sends 0 requests.
    - **Wrong code:** a wrong code with Enter pressed ×3 sent 1 verify request, showed "That code is not correct. Please try again.", cleared and refocused the input, and did not submit the password form.
    - **Captured email:** subject "Your MARLO sign-in code", envelope and `To` `probe.user@outlook.com` (real address replaced), text and HTML parts, 5-minute expiry copy.
    - **Success:** the correct code with a double click on Verify sent 1 verify request (`auth.otp.verify outcome=ok`) and redirected to `AICCRA/crpDashboard.do`.
    - **Database after success:** challenge consumed; `otp_challenges` holds only 64-character HMACs (searching for the code or `outlook` matched 0 rows); `agree_terms` 0→1 and `last_login` updated, as in a normal login.
    - **Exhausted state:** three wrong codes gave mismatch, mismatch, exhausted ("Too many incorrect attempts. Please request a new code."), and Resend was enabled at once.
    - **Stale response:** jQuery callbacks were delayed 3 s in-page, then Resend was clicked followed by "Use a different email". The late answer was dropped: the page stayed on the email step, the OTP step stayed hidden, and no request error showed. The late answer was `rate_limited` (the 30 s cooldown); its drop is the same `requestSeq` path a late `sent` takes.
    - **Off-state for other paths:** for a CGIAR user (`k.tanaka@cgiar.org`), every card has `data-otp-enabled=false` (SEC-009). The AICCRA (Cognito-flagged) card opened its usual password and terms step with no OTP control.
  - **Server log of the run:**

    | Event | Outcome | Count |
    |---|---|---|
    | `auth.otp.request` | `sent` | 2 |
    | `auth.otp.request` | `rate_limited` | 1 |
    | `auth.otp.delivery` | `delivered` | 2 |
    | `auth.otp.verify` | `ok` | 1 |
    | `auth.otp.verify` | `mismatch` | 2 |
    | `auth.otp.verify` | `exhausted` | 1 |

  - **Not exercised:**
    - **Removal-based `requestSeq` falsifier:** it needs a modified `login.js` to be served. The positive drop test above stands in for it.
    - **Go back during an in-flight request with several units:** the test account has a single unit, whose path goes through `showEmailStep`.
    - **"Log in" during an in-flight request.**
    - **Expired (`invalid`) state:** needs a 5-minute wait. It is covered by the T11 unit tests.
    - **reCAPTCHA overlap.**
    - **Visual comparison of password and Cognito units against a HEAD build:** the code-level identity is established by the Reviewer.
    - These items go to T15.
  - **Artifact:** the GIF (50 frames, 29 MB) is `~/Downloads/otp_login_walkthrough_t14.gif`. It is kept out of git because of its size.
  - **Cleanup:**
    - Deleted `custom_parameters` row 2609.
    - Deleted the walkthrough rows in `otp_challenges`, `otp_rate_limits` and `otp_cooldowns`.
    - Final counts: OTP `custom_parameters` 0, total `custom_parameters` 1623 (as before), `otp_*` 0/0/0.
    - The app, the SMTP sink and the captured mailbox file are stopped or removed.
    - `agree_terms=1` on user 3862 is kept: the user accepted the terms in the flow.
- **Forward pointers (Leader):**
  - **T15:** run the items listed under "Not exercised" above.
  - **T16 runbook:** local and non-production SMTP may refuse external recipients. A local walkthrough needs a sink, set through `EMAIL_HOST`/`EMAIL_PORT`.
- **Final:** Reviewer PASS (attempt 2) and walkthrough recorded. T14 **done**.

### CHG-OTP-LOGIN-001-T15 — End-to-end verification

- **User decision (2026-10-13), before the run:** T15 runs on a throwaway copy. `aiccradb_actsave` is cloned into `aiccradb_otp_probe`, the app is pointed at the copy with environment variables only, and the copy is dropped at the end.
- **Attempt 1 — Implementer:** STATUS DONE, no production code changed.
  - New untracked folder `probes/`: `concurrency-probe.sh` (HTTP level), `concurrency-probe-sql.sh` (statement level), `ac-probe.sh`, `tools/SessDump.java` (reads Shiro sessions over JDI), `README.md` with a verdict per AC, and 13 recorded runs in `output/`.
  - Claimed: AC-2 to AC-15 and AC-17 PASS; AC-1 PASS except the live Cognito hand-off (not configured locally); AC-16 PASS except reCAPTCHA overlap and screen-reader behaviour.
  - Falsifier: `CONSUME_SQL` without `AND attempts < 3` turns S3, S3b and HTTP case 3b red; reverted, `git diff --stat` empty.
  - Five findings handed to the Leader, none fixed:
    1. A post-login failure leaves an authenticated session (the known gap shared with Cognito route C).
    2. "Log in" is not disabled while a code request is in flight.
    3. `crp_users.is_active = 0` does not stop a code login.
    4. `requestSeq` is redundant.
    5. Supersede plus insert without the cooldown deadlocks at statement level.
- **Leader checks after the report:** only `?? probes/` in the tree; `aiccradb_otp_probe` dropped; `aiccradb_actsave` holds 0/0/0 `otp_*` rows, 0 OTP `custom_parameters`, 1623 in total, no `global_unit_types` 99; nothing listens on 8080 or 2525.
- **Leader edit:** the real test mailbox address was replaced with `probe.user@outlook.com` (masked `p***@outlook.com`) in `ac-probe.sh`, `README.md` and two output files, and the README notes it. The repository is public.
- **Evidence re-run (Leader, non-author):** a minimal throwaway database `otp_probe_rerun` with only the three OTP tables from the migration.
  - `concurrency-probe-sql.sh`: `SUMMARY: 4 checks, 0 failed`.
  - The falsifier, run against a scratch copy of `OtpChallengeMySQLDAO.java` through `PROBE_DAO`: `S3: FAIL (consumes=10)`, `S3b: FAIL (violations=24 of 30)`, while S1 and S2 pass.
  - The database was dropped and `src` was not touched. **VERIFIED**.
- **Attempt 1 — Reviewer (full):** **FAIL**. Blocking issues:
  1. **AC-9 PASS rests on a removal MARLO never performs.** The probe hard-deletes the `crp_users` row. MARLO's removal is a soft delete: `CrpUserMySQLDAO.deleteCrpUser` sets `active=false`, and every admin path calls it. With `is_active=0` the code login answers `ok`, and the request side mails a real code to a deactivated member. That breaks FN-015 and FN-005 ("not a member of G"). The implementation follows design §5.1 step 6 (`existCrpUser`, P-2 parity with the password login), so this is a spec conflict, not an Implementer defect. It needs a user decision, not just a T16 note.
  2. The isolation level is not recorded, which execution.md (T03 adjudication) said T15 would do.
  3. The README marks the T04 "cooldown accept against a concurrent purge" pointer closed, but case 4 seeds only an expired `otp_challenges` row. No stale `otp_cooldowns` or `otp_rate_limits` row was purged during an accept.
  4. `output/ac-probe.shortsecret.out` is identical to `nosecret.out` and does not contain the 16-byte-secret WARN the README cites. The `ps eww` check is not recorded.
  5. `tools/SessDump.java` has no GPL header and has braceless `if`/`for` bodies (Hard rules 6 and 7).
  - On the Implementer findings: 1 is a T16 item already decided at the T11 gate; 2 is not a UI-004 violation (a T16 or UX note); 3 is blocking issue 1; 4 is advisory, and it means the T14 `requestSeq` falsifier as written cannot fail; 5 is the accepted T03 "gap 7", fail-closed and unreachable over HTTP.
  - Advisories:
    - The "probe" guard checks only the database `PROBE_MYSQL` opens, not the app's database. Assert the first `issue()` row lands in the probe copy.
    - User id 3862 and the real address are still paired in the tracked T14 entry of this file, which is already pushed.
    - Local `/Users/...` log paths remain in two output files.
    - AC-1: record PASS with an environment note, because the live Cognito hand-off is not an accepted gap in requirements §6.
    - AC-4: cite T11's decoy unit test for "no recorded challenge can authenticate".
    - README "spread 0.4 to 2.8 ms": 2.8 appears in no output.
    - `ac17-migration.out` does not record the invocation or the database names.
    - `ac-probe.sh` has dead code (`:600`, `:435`, `:592`), and `stage_smtp` restores `'true'` instead of the saved value.
- **Leader check of blocking issue 1:** confirmed. `deleteCrpUser` is a soft delete, and `existCrpUser` has no `is_active` condition. The Leader also wrote that `existActiveCrpUser` has inverted semantics. **That was a misreading** of two concatenated line ranges, corrected in the T06 rework entry below: the method returns `true` when an active row exists.
- **Status:** `[~]`, waiting for the user's decision on issue 1 and on the address already in the pushed history.
- **User decisions (2026-10-13), after the FAIL:**
  - **Issue 1: fix in the OTP path only.** `OtpEligibility` must require an **active** membership, so a member removed through MARLO's soft delete neither receives a code nor signs in with one (FN-005, FN-015). This deliberately departs from P-2/P-13 parity: the code login becomes stricter than the password login. The password-login gap (`finishLogin` and the session interceptors use `existCrpUser`, which ignores `is_active`) is out of scope and is tracked separately. T06 is reopened for this fix, then T15 re-runs AC-9 on a fresh throwaway copy.
  - **Personal address:** replaced in this file from now on. The pushed history (`d743181e58`) is not rewritten.

### CHG-OTP-LOGIN-001-T06 — rework: active membership gate (reopened by the T15 FAIL)

- **Attempt 1 — Implementer:** STATUS DONE.
  - `OtpEligibility.java:128` now calls `existActiveCrpUser`, a one-token swap, and the Javadoc is fixed. No DAO or manager change.
  - The Implementer corrected the Leader's premise: `existActiveCrpUser` filters `is_active = 1` and returns `true` when a row exists. It is not inverted.
  - Callers: `OtpRequestCodeAction` and the `OtpVerifyCodeAction` FN-015 re-check both go through `isEligible`. `CrpByUserEmailAction` uses only `otpEnabledFor`, which never reads membership (JI-11). `finishLogin` gate 4 keeps `existCrpUser`.
  - Tests: two new `OtpEligibilityTest` cases, and a `State.SOFT_DELETED` value in the request and verify suites, so every `State.values()` loop covers it. Every stub now answers on `existActiveCrpUser`, and the old `existCrpUser` is an activity-blind double.
  - Spec: design §5.1 step 6, DD-4 and the P-13 row; the requirements line-26 wording and a Decision Log entry.
  - Gates claimed: clean compile, Checkstyle delta none, `Tests run: 737, Failures: 0`.
- **Evidence re-run (Leader, non-author):**
  - Clean compile: 2415 / 1062 / 71 files.
  - The four affected classes: 123 tests, 0 failures.
  - Falsifier, call reverted to `existCrpUser` temporarily: 11 tests red (5 / 4 / 2), including "soft-deleted … expected invalid but was ok". Restored; the diff is unchanged.
  - **VERIFIED**.
- **Attempt 1 — Reviewer (full):** **FAIL**. The code fix is correct and complete on both OTP sides, and AC-4 holds for the soft-deleted state.
  - Issue 1: `OtpVerifyCodeActionTest.java:585` is about 153 characters, a new line-length violation. The claimed Checkstyle delta had covered only the main class.
  - Issue 2: `tasks.md` T06 still names `existCrpUser` in the predicate and the falsifier, and lists no soft-deleted test.
  - Advisories: Decision Log order; a misleading test name; "tracked separately" names no tracker.
- **Attempt 2 — Leader, mechanical fixes, with the Reviewer gate kept:**
  - Wrapped the long line.
  - Updated T06 in `tasks.md`: the predicate, the soft-deleted test and the falsifier.
  - Moved the Decision Log entry after the T05 entry.
  - Renamed the test to `softDeletedHereAndActiveElsewhereIsRefusedForThisUnit`.
  - Pointed DD-4 and the Decision Log to an A2 Jira issue still to be filed.
- **Evidence re-run (Leader):**
  - Checkstyle 8.18 `--baseline` on all five Java files: HEAD=0, tree=0, delta=0.
  - Clean build: the first try hit a stale MapStruct `generated-sources` NPE (environment, `PublicationAltmetricsWOSMapperImpl`). After wiping `generated-sources`: 2415 / 1062 / 71 files, `Tests run: 737, Failures: 0, Errors: 0, Skipped: 0`, BUILD SUCCESS.
  - **VERIFIED**.
- **Attempt 2 — Reviewer:** **PASS**. Every remaining `existCrpUser` mention in the spec is intentional: the departure note, P-2, P-13 and the falsifier. Advisory: file the A2 issue for the password-login gap and put its key in the Decision Log before `/akili-archive`.
- **Forward pointer:** T15 must re-run AC-9 with a real soft delete (`is_active = 0`) on a fresh throwaway copy.

### CHG-OTP-LOGIN-001-T15 — End-to-end verification (continued after the T06 rework)

- **Attempt 2 — Implementer:** STATUS DONE, no `src/main` changes, HEAD `ece163ef2e` (T06 fix included).
  - **AC-9** now uses MARLO's soft delete (`is_active = 0`):
    - A code issued while the member was active and verified after the soft delete answers `invalid`. The challenge is consumed and the session is not authenticated.
    - A request after the soft delete gets the neutral answer and 0 mails.
    - The hard delete is kept as supporting evidence, and an "active again" control signs in.
  - **AC-4** has six states, the soft-deleted one included.
  - **Isolation** is REPEATABLE-READ, both global and per session, and the live app pool shows REPEATABLE-READ x15. The app sets none of its own.
  - **Case 4 is made deterministic for the throttled limiter purge** (it runs first after a fresh start). A 2-hour-old `otp_cooldowns` row and a 2-day-old `otp_rate_limits` window are purged and the in-retention controls are kept, with 1 `sent` and 0 `unavailable`. A negative control run inside the throttle window fails as expected (`throttle-not-due.out`).
  - **`shortsecret.out`** holds the real WARN line and the `ps eww` result, value redacted.
  - **`SessDump.java`** has the GPL header and braces, and compiles.
  - **Advisories applied:**
    - a probe-DB guard in the app-driven probes;
    - AC-1 recorded as PASS with an environment note;
    - AC-4 cites `aDecoyGuessed`;
    - the spread text is fixed;
    - `ac17-migration.sh` records its invocation;
    - dead code removed;
    - `stage_smtp` restores the saved value;
    - the T14 `requestSeq` note;
    - `/Users` paths replaced by `<logs>`.
  - **Final runs:** `ac-probe.sh` 59 checks, 0 failed; HTTP concurrency 13 checks and SQL concurrency 4 checks, all green; SQL falsifier `S3 consumes=10`, `S3b 25/30`; suite 737 tests, 0 failures.
  - **Run discarded:** one accidental double run, recorded in README §2.
- **Leader checks and evidence re-run (non-author):**
  - **Environment:** only `?? probes/` in the tree; no `*probe*` database; ports 8080 and 2525 free.
  - **`aiccradb_actsave`:** `otp_*` 0/0/0, OTP `custom_parameters` 0, 1623 in total. User 3862's `last_login` is still `2026-10-13 12:56:27`, and its AICCRA `crp_users.is_active` is 1.
  - **Probe folder:** no personal address, `/Users` path or DB password. `bash -n` passes on all 4 scripts, and `javac SessDump.java` is clean.
  - **SQL probe on a fresh minimal database:** it prints the isolation lines and reports `4 checks, 0 failed`. The falsifier through `PROBE_DAO` reports `S3 consumes=10` and `S3b 24/30`.
  - **VERIFIED**.
- **Attempt 2 — Reviewer (full):** **PASS**. All five blocking issues are fixed and every README figure matches the output files.
  - Advisories:
    - The probe-DB guard fires only after the first `sent`, so a password login in `stage_session` and `stage_smtp` could write `last_login` to a misconfigured database before the abort. The README §2 `ps eww` check covers it in practice.
    - The README wording on the interceptors was wrong. The Leader fixed it: `ValidPhaseInterceptor` already uses `existActiveCrpUser`.
    - The address in pushed history was already decided by the user and is not rewritten.
- **Handed to T16:**
  - the post-login failure gap, which leaves an authenticated session and is shared with Cognito route C;
  - "Log in" stays enabled while a code request is in flight;
  - the accepted "purge deadlock means `unavailable`" risk;
  - deliverability through the real relay;
  - the reCAPTCHA overlap and screen-reader behaviour, which were not verified locally.
- **Open before `/akili-archive`:** file the A2 Jira issue for the password-login soft-delete gap, after the user confirms the parent epic, and put its key in the requirements Decision Log.

### CHG-OTP-LOGIN-001-T16 — Runbook and rollout checklist

- **User decision (2026-10-13):** the verification walk runs on a throwaway copy (`aiccradb_otp_probe`), the same recipe as T15, dropped at the end.
- **Attempt 1 — Implementer:** STATUS DONE. New `runbook.md`:
  - Part A: the runbook (A1 to A9).
  - Part B: the rollout checklist, with an owner and a blocking scope per premise and OQ.
  - Part C: the walk record.
  - Part D: pending shared-file items for `/akili-archive` on `staging` (`docs/trd/trd.md` §8.1 and §9.5, `docs/infrastructure.md` §6).
  - **Walk:**
    - An admin UI enable (`/superadmin/marloParameters.do`, the row shown as `system_otp_allowed_email_domains`) gave `otpEnabled:true` with no restart.
    - Code sign-in: `request sent`, `delivery delivered`, `verify ok`, then the dashboard.
    - An admin UI disable gave `otpEnabled:false` and `domainNotEnabled` on the next request.
  - **Falsifier (raw SQL):** the change applies on the next request, with the same pid. The DD-12 claim stands.
  - **Corrections to the Leader's brief, applied in the runbook:**
    - the domain-not-allowed case is not neutral (FN-006);
    - rate limits are shared across instances through the database (DD-8);
    - design §14 overstated the rollback;
    - the admin screen writes blank rows for the other units;
    - the env names `OTP_SMTP_TIMEOUT_MS` were not run.
- **Leader edits:**
  - design §14 rollback: only emptying the value applies on the next request. The secret is read once at start by the `OtpKeys` singleton, so unsetting it needs a restart.
  - Two runbook notes that referred to the task brief were reworded.
- **Leader checks and evidence re-run:**
  - Cleanup confirmed: no probe database, ports free. `aiccradb_actsave` is unchanged: `otp_*` 0/0/0, OTP `custom_parameters` 0, 1623 in total, `last_login` unchanged.
  - The runbook holds no personal data, `/Users` path or secret.
  - Spot checks against the code match: limits 5/20/10/50 per 15 min, cooldown 30 s, retention 1 h and 1 day, purge every 10 min, timeout 10000 (max 30000), queue 100.
  - The walk cannot be re-run without the stack and is accepted as recorded evidence.
  - **VERIFIED** for what can be checked.
- **Attempt 1 — Reviewer (full depth, last task):** **FAIL**. Blocking issues:
  1. `runbook.md:76`: after a rotation or rolling restart, old codes fail as `mismatch` (the nonce lookup is unkeyed and the MAC compare under the new key fails), then `exhausted`, not `invalid`. A lost session also gives `mismatch`. A5 Step 3 must add "secret rotated or instance restarted" to `mismatch`.
  2. `runbook.md:128` and `:150`: when the eligibility check throws, the log says `internal_error` but the user sees the neutral `sent` (request) or `invalid` (verify), not "unavailable". This is deliberate under SEC-006. Support must never mention an `internal_error` to the user.
  - **Advisories:**
    - `tasks.md:595` still lists the secret rollback with no restart.
    - The Part B rule overstates which rows block go-live.
    - The allow-list is comma-only: an inner space drops the entry and a semicolon never matches.
    - The secret must be a single line with no trailing newline.
    - A full queue logs `email_failed` at once, and the cause is on a separate `auth.otp.mail outcome=send_failed` line.
    - A6: reply only to the registered mailbox.
    - Part D §9.5 wording.
    - Meta and process references remain.
    - The `openssl rand -base64 48` (design §5.3) and `32` (runbook) recipes differ.
    - `OTP_TRUSTED_PROXIES` env mapping not run.
- **Leader edit:** `tasks.md:595`, rollback step 2 now says "and restart every instance (the secret is read once at start)".
- **Attempt 2 — Implementer:** STATUS DONE, `runbook.md` only. Each change is checked against the code.
  - Rotation (`:74`) and A5 `mismatch` (`:142`): old codes give `mismatch`, then `exhausted`. A restart that loses the session also gives `mismatch`.
  - `internal_error` rows (`:126`, `:148`): when `stage=eligibility`, the user saw "code sent" or "Invalid or expired". A6 says never to tell the user.
  - All advisories applied: commas only, `-base64 48` on a single line, the `auth.otp.mail send_failed` cause line, replies only to the registered mailbox, the reworded Part B rule, Part D §9.5 "Add a bullet", meta references removed, `OTP_TRUSTED_PROXIES` marked as not run.
- **Evidence re-run (Leader, non-author):** `OtpVerifyCodeAction.java:326-327` (no pending gives `MISMATCH`), `:344-350` (MAC mismatch calls `registerMismatch`), `:359-362` (`STATUS_INVALID` with `INTERNAL_ERROR`); `OtpRequestCodeAction.java:413-416` (the `sent` map, then `INTERNAL_ERROR`). The runbook rows match. No personal data and no shared file touched. **VERIFIED**.
- **Attempt 2 — Reviewer:** **PASS**. Advisory: the A7 Source column cites task ids, which is acceptable as traceability.
- **Done-when:** the runbook was walked (Part C), the DD-12 falsifier held, and every open premise and OQ has a named owner and a Blocks scope: P-4, P-6, P-25, OQ-1, OQ-2 and OQ-5 block go-live, not merge.
- **Carried to `/akili-archive` on `staging`:** runbook Part D (`docs/trd/trd.md` §8.1 and §9.5, `docs/infrastructure.md` §6), plus the A2 Jira key for the password-login soft-delete gap in the requirements Decision Log.

### Follow-up — password-login soft-delete gap (A2-2637)

- **User request (2026-10-13):** probe the password-login gap live, then file the Jira issue under the epic "Bugs 2026" (A2-2054).
- **Live probe:** run on a throwaway copy `aiccradb_pwgap_probe`, dropped afterwards. `aiccradb_actsave` unchanged: `crp_users` 5136, both test memberships `is_active=1`, roles intact.
  - A removed member still signs in with a password. A direct `POST login.do` with `crp=AICCRA` answers 302 to the dashboard, which loads; the login page hides the unit because `crpByEmail` filters inactive memberships.
  - A Project Leader removed through the admin UI loses edit access only at the next sign-in. A session already open keeps edit access.
  - With the membership inactive but roles left in place, edit access stays with no time limit.
  - Cause: `finishLogin` gate 4 uses `existCrpUser`; `ValidPhaseInterceptor` checks `existActiveCrpUser` only for another unit's phase; `getPermissions` ignores `is_active`; the Shiro authorization cache is cleared only at the user's own next sign-in.
- **Filed:** A2-2637 (Bug, parent A2-2054, Priority, Complexity and Readiness set, assigned). The key is recorded in the requirements Decision Log, design DD-4 and runbook A9 and B1. The runbook Part D placeholder row is removed.

### CHG-OTP-LOGIN-001-T17 — Step 3 method choice (UI-006)

- **Origin (2026-10-14):** in a local walk the user, as an external Center member with no password, read the password field and the code button side by side as a two-factor step. After comparing the PRMS login (method first) and two recorded prototypes, the user chose the PRMS-style choice placed **after** the Global Unit, since Cognito and the code are enabled per unit. Cognito stays exclusive for CGIAR users (re-confirmed). Spec amended by the Leader before dispatch: UI-006 and the Decision Log entry in `requirements.md`, AC-16 extended, design §6 rows, T17 added to `tasks.md` (graph and DoD).
- **Attempt 1 — Implementer (T2):** STATUS DONE. `loginForm.ftl` (choice block, code panel, password-panel back link; `#login-otp-button` moved into the code panel as "Send code"; `#login-password` stays the last child), `login.js` (`methodView`, `showMethodView`, `resetMethodView`, `methodNotPassword`, `otpMaskedEmail`; Enter guard as a delegated keydown on `.loginForm` plus an early return in the `.login-input` keyup relay; reset in `showEmailStep`/`showProjectStep` and the COGNITO arm), `customLogin.css`, six `login.otp.method.*` keys in `global.properties`, cache-busters `20261014` (`login.ftl`, `401.ftl`, the `customLogin.css` import in `global.css`, `global.css` in `header.ftl`). Gates: clean compile exit 0; Checkstyle n/a (no Java); i18n check clean; `node --check` clean. Noted that the code step has no "use my password instead" link (the T17 Consumers line was wrong).
- **Evidence re-run (Leader, non-author):** classes wiped, `mvn -q -o -pl marlo-data,marlo-web -am test-compile -DskipTests` exit 0.
- **Attempt 1 — Reviewer (T3):** **PASS**. Advisories, not gating: `aria-describedby` from "Send code" to the masked-destination text and a `role="group"` on the choice; `login.otp.button` now unused; the T17 Consumers wording; one new literal colour; a password typed then abandoned stays in the hidden input (harmless behind the guard).
- **Walkthrough (Leader):** `probes/output/t17-walkthrough.out`. Every branch passes: Cognito unchanged, password-only unchanged, the choice, the password panel (sign-in with a password), the code panel (mask, focus, Send), step 3 reopening on the choice after "Use a different email", and the re-request path through to the dashboard. Enter guard measured with dispatched events, with its falsifier red. Two automation artefacts are recorded there: a concurrent session of the user in the same browser (a logout.do the walkthrough did not issue) explains the one `mismatch`, and key events that stopped reaching the page were discarded and re-measured. Throwaway copy and sink only; `aiccradb_actsave` not touched.
- **Leader edit:** `tasks.md` T17 Consumers line corrected (no "use my password instead" handler exists on the code step).

### CHG-OTP-LOGIN-001-T18 — Record code emails in `email_logs` for support (OPS-005)

- **Origin (2026-10-14):** while reviewing which specificities the code email honours, the user asked that code emails be recorded in `email_logs` for support. Spec amended by the Leader before dispatch: OPS-005, AC-18 and the AC-11 amendment, the Decision Log entry in `requirements.md`; design §5.3, §11 and DD-7; runbook A4 and A5; T18 added to `tasks.md` (graph, coverage, DoD).
- **Attempt 1 — Implementer (T2, effort high):** STATUS DONE. `OtpMailSender` injects `EmailLogManager` and, on the worker thread after the delivery line, saves one row (placeholder message, `source_action = /otpRequestCode`, unit, `succes_email`, class-name error); a save failure logs `auth.otp.mail outcome=log_failed` and is swallowed; a rejected hand-off writes no row. `CodeSender` carries the unit id. `SendFailEmailAction.selectedEmailLogs()` drops code-email rows by id and in bulk. Tests: 10 new in `OtpMailSenderTest`, new `SendFailEmailActionTest` (5), the T09 static lock narrowed; `OtpStrutsPackageTest` and `OtpVerifyCodeActionTest` lambdas updated for the seam signature. Falsifiers red (code in the row, source filter removed).
- **Evidence re-run (Leader, non-author):** classes wiped, 2415 / 1062 / 72 files, six test classes, 130 tests, 0 failures, BUILD SUCCESS.
- **Attempt 1 — Reviewer (T3):** **FAIL**, spec consistency only; every code point held (SEC-001, SEC-006, rejected hand-off, swallowed save failure, resend exclusion, transaction on the worker thread). Findings: (1) the SEC-001 scenario still forbade the full email in `email_log`; (2) `design.md` §13 and the T09 test and falsifier lines still said the mailer never writes `email_log`; (3) the AC-11 probe required 0 `email_logs` rows and AC-18 had no probe; (4) no live evidence for the T18 Verification.
- **Leader edits:** findings 1 and 2 fixed in `requirements.md`, `design.md` and `tasks.md`; the T18 Files list now names `probes/ac-probe.sh` and `probes/README.md`.
- **Attempt 2 — Implementer (T2, effort xhigh):** STATUS DONE. `leak` rewritten for the amended AC-11; new stage `emaillog` for AC-18 (delivered, failed, sends-nothing, resend as a super admin with a control row; `NOT RUN` counted on its own); the order assertion counts delivery lines inside the save proxy; the static lock forbids the two specificity constants and keys. Gates green; order and static-lock falsifiers red. Probe logic dry-run against a fake app only.
- **Live run (Leader):** clean build of the tree started on `aiccradb_otp_probe` (`ps eww`: `MYSQL_DATABASE=aiccradb_otp_probe`, `EMAIL_HOST=127.0.0.1`, `EMAIL_PORT=2525`, a fresh `OTP_HMAC_SECRET`), JDWP on. The copy had been recreated for T17, so the T15 fixtures were inserted again, and a test password was set for one super admin of the copy. Two harness problems, neither in the code: hand-inserted users with NULL `agree_terms` made the eligibility-loaded user dirty, and the audit listener threw at the `issue` commit (`unavailable`; found with `jdb`; no real row has NULL `agree_terms`; fixtures set to 0); the `emaillog` control resent by id before listing, which marked it sent (probe reordered by the Leader). Recorded run: `SUMMARY: 32 checks, 0 failed`, `probes/output/ac-probe.t18-leak-emaillog.out` (paths redacted, no credential); ledger rows AC-11 and AC-18 and README §7 and §9 updated.
- **Attempt 2 — Reviewer (T3):** **FAIL**, three documentation lines: the AC-18 row said 15 checks (14); the §7 recipe lacked `PROBE_SINK_STOP`; runbook A5 "No row" omitted the in-flight and `log_failed` cases. Every code, test and evidence claim held.
- **Leader edits:** the three lines fixed; advisories applied (`requirements.md` §6 gate row, the console-log label in the output file).
- **Attempt 3 — Reviewer (T3):** **PASS**. No code, test or probe-script change since attempt 2.
- **Environment state after the task:** app running on 8080 against `aiccradb_otp_probe`, sink on 2525; the copy holds the re-added fixtures and the super admin test password. `aiccradb_actsave` and `aiccradb1`-`4` were not written to.

---

## CHG-OTP-LOGIN-001-T19 — Align the code email with MARLO's email rules (2026-10-15)

- **Origin:** user decisions of 2026-10-15, taken while reviewing which rules the code email follows.
  1. Outside production, no email may reach external users: OQ-5 is resolved as the support mailbox, matching `SendMailS.send`.
  2. The `email_logs` row stores the full body, code included, accepted as a deviation from guide §3.2.
  3. Outside production only, the log carries the code, accepted as a deviation from guide §3.11.
- **Leader spec edits:** the Leader amended FN-018, SEC-001 and its scenario, OPS-001, OPS-005, AC-11 and AC-18, added AC-19, and wrote three Decision Log entries. Design: §5.3, §11, §13, §14 and DD-7. Runbook: A4, A5, A7 and B4. Tasks: the T19 block.
- **Merge first:** a merge of `staging` (`ed87f3955f`) was in progress. The Leader resolved its two conflicts:
  - `header.ftl`: cache-buster `?20261015`, because both sides changed `global.css`;
  - `global.properties`: both appended blocks kept, no new duplicate keys.

  The merged tree compiled clean (2415/1063/77) with 779 tests green. The user committed the merge.
- **Implementer (T2), attempt 1:** `OtpMailSender` reads `isProduction()` once per send. Outside production the recipient is `email.notification` only (fail closed, no fallback to the user), with the `(TESTING ENVIRONMENT) ` prefix and a recipient header. The row holds the submitted address in `to_email`, the support mailbox in `bbc_email`, the subject without the prefix and the production HTML body. Outside production there is one `auth.otp.code` line. `PLACEHOLDER_MESSAGE` is removed. The scope addition for the code line arrived mid-task by message. The probe now finds codes by the body header and gains the stages `nonprod` and `nonprodblank`.
- **Implementer evidence:**
  - Checkstyle delta 0;
  - full suite 792 tests, 0 failures;
  - red run 10 of 32;
  - seven falsifiers red (F1–F7);
  - live run on a rebuilt throwaway copy with the sink and `EMAIL_NOTIFICATION=support.probe@sink.test`: `nonprod leak emaillog` 43/0, `enum` 11/0, `nonprodblank` 4/0;
  - environment cleaned afterwards.

  The production half is covered by unit tests only.
- **Leader re-run (non-author):** clean compile 2415/1063/77; 137 tests in five OTP classes green; private-data grep clean; no listener on 8080 or 2525; no copy left.
- **Attempt 1, Reviewer (T3):** **FAIL**. The code was correct; the text was not:
  1. the SEC-001 scenario, AC-11 and runbook A5 still excluded the full email from every log line;
  2. the overview bullet said only super admins can read the code;
  3. the traceability matrix was stale.
- **Leader edits:** the three findings fixed. Advisories applied: the `sendAsync` Javadoc, `PROBE_SUPPORT` required in `emaillog`, the BCC wording, the Decision Log bullet, the AC order, the §13 wording and the A4 whitespace note. Not taken: the flip-test strengthening (the same-value clause is proved by reading the code) and the expired code on the recorded output's `needles:` line.
- **Attempt 2, Reviewer (T3):** **PASS**. Its two Javadoc advisories (the `recipient` parameter and the no-send list) were applied by the Leader, comment-only.
- **Environment state after the task:** nothing running; `aiccradb_otp_probe` dropped; `aiccradb_actsave` read only; scratch credentials deleted.

---

## CHG-OTP-LOGIN-001-T20 — Branded format for the code email (2026-10-15)

- **Origin:** the user asked to apply the email format implemented on `dev` to the code email. That format is A2-2484, already in this branch through the staging merge. It provides `EmailLayout.wrap` with the unit's `email.layout` shell, and per-unit copy.
- **Leader finding:** before sign-in the session holds no Global Unit, so the request's provider resolves only `global.properties`, where the shell is `{0}`. Applied as-is, the format would have been a no-op.
- **Leader spec edits:** FN-017 and UI-005 amended, AC-20 added, a Decision Log entry, a DD-7 amendment and the T20 block. After the Implementer's report, the `test` unit's subject prefix was set to its own `[MARLO]`.
- **Implementer (T2), attempt 1:**
  - **New `UnitTextProvider`:** the selected unit's `custom/<crp_custom_file>` over `global`, formatted with `MessageFormat`, with a validated file name. It touches no shared provider and no session.
  - **`OtpRequestCodeAction`:** it reads the custom file for every request that reaches the send decision, before the issue (SEC-006). A failed read falls back to the request provider.
  - **`OtpMailSender`:** it wraps only the HTML part, puts the non-production header before the wrap, and stores the wrapped HTML without the header in the row.
  - **Copy:** per-unit copy in `aicrra`, `aiccra3` and `test`.
- **Evidence:**
  - **Implementer:** clean compile 2415/1064/78; Checkstyle delta 0; i18n gate clean; full suite 811 tests, 0 failures; five falsifiers red.
  - **Visual check:** an AICCRA code email on a throwaway copy with the local sink, rendered and screenshotted (scratchpad, not committed); `ac-probe.sh nonprod` 8/8. Environment cleaned afterwards.
  - **Leader re-run (non-author):** clean compile; 161 tests in seven classes green; private-data grep clean; screenshot viewed.
- **Attempt 1, Reviewer (T3):** **PASS**. Advisories not applied:
  - import order and the placement of a constant in `OtpMailSenderTest`;
  - the shell's hard-coded support address against `footer.contactEmail`, identical today;
  - no explicit test that the shared `LocalizedTextProvider` is untouched. It holds by construction, since the class has no reference to it.
- **Environment state after the task:** nothing running; copy dropped; `aiccradb_actsave` not written.


---

## CHG-OTP-LOGIN-001-T21 — Per-unit switch and domain exclusion (2026-10-15)

- **Origin:** the user redefined the specificity: code sign-in is switched on per Global Unit for every account with access, with an optional per-unit domain exclusion, and no CGIAR rule ("hazlo asi"). The definition was written and committed first (`ac4dcc50fd`), with OQ-9 approved by the user, then implemented on "implementalo".
- **Implementer (T2), attempt 1:**
  - **Migration** `V2_6_0_20261015_1418__ReplaceOtpAllowListWithSwitchAndExclusion.sql`: seeds `system_otp_login_active` and `system_otp_excluded_email_domains` for the existing types among 1–4 with the DA-002 texts, then deletes the `crp_otp_allowed_email_domains` child and parent rows. DML only, idempotent, no assumed unit id.
  - **Constants:** `OTP_LOGIN_ACTIVE` and `OTP_EXCLUDED_EMAIL_DOMAINS` in both `APConstants.java`; `OTP_ALLOWED_EMAIL_DOMAINS` removed.
  - **Reads:** one uncached native query returns both keys for every listed unit; a missing row reads as off.
  - **Rule:** `OtpDomainPolicy.isOffered` (switch on and domain not excluded); `OtpEligibility` no longer reads `is_cgiar_user` or `CognitoAuthSpecificity`; `OtpRequestCodeAction` step 4 and `CrpByUserEmailAction` use it.
  - **UI:** on a Cognito unit with the code on, step 3 opens on the choice "Sign in with CGIAR" / "Email me a sign-in code"; the password input is removed from the form. `login.js?20261015`.
  - **Probes:** standing fixture in `ac-probe.sh`; new `ac21-migration.sh`; outputs under `probes/output/`.
- **Implementer evidence:**
  - clean compile 2415/1064/78; Checkstyle delta 0; full suite 832 tests, 0 failures (811 before);
  - migration gate 17/0 (twice on a copy with an old-key child row, twice without units 45/47, twice without unit types; then applied by Flyway at app start);
  - live stack on a throwaway copy with the sink: `enum` 18/0, `live` 7/0, `attempts removed limits nonprod postlogin emaillog` 30/0 (1 not run: the AC-18 resend half, covered by `SendFailEmailActionTest`), `session leak` 20/0;
  - browser walkthrough of external, CGIAR on Cognito with the code, `cgiar.org` excluded live, and switch off. The Cognito redirect to the identity provider was not followed;
  - falsifiers red: CGIAR check kept (7 failures), allow-list semantics (80 failures, 5 errors), remembered read (live-change unit test), `cgiar.org` drop (9 failures), Hibernate query cache on the live stack (`live` 3/7 red);
  - environment cleaned; `aiccradb_actsave` unchanged; `marlo-dev.properties` checksum unchanged.
- **Deviation accepted:** the Cognito choice reuses the step-3 password panel; "Sign in with CGIAR" in the choice starts the redirect directly after the terms check. Design §6 rows amended by the Leader.
- **Not re-run:** `expiry`, `resend`, `smtp`, `blackhole`, `tz`, `nosecret`, `nonprodblank` and the concurrency probes; no code they exercise changed.
- **Leader re-run (non-author):** clean compile 2415/1064/78; 509 tests in the OTP, Cognito, login and `crpByEmail` classes green; no private data or local paths in the diff.
- **Attempt 1, Reviewer (T3):** **PASS**. Advisories:
  - stale allow-list and old CGIAR wording in design, requirements and runbook: swept by the Leader (FN-015 amended, glossary "Exclusion list", personas row, §5.3, §6, DD-12 scope, load row, relay note);
  - `ac21-migration.sh` prints "run N exit 0" under `set -e`, so those lines cannot show a failure; the script's final exit and row checks are the evidence. Not changed;
  - the fixture `otp.staff@cgiar.org` is safe only with a `probe` database and the local sink. Kept as is.
- **Environment state after the task:** nothing running; copies dropped.

## CHG-OTP-LOGIN-001-T22 — Require POST on the code endpoints; catch `Error` in request; password-off copy (2026-10-18)

- **Origin:** `/akili-validate` of 2026-10-18 (`validation-report.md`):
  - V-1 (FAIL): both endpoints accepted any HTTP method;
  - V-4: `OtpRequestCodeAction` caught only `RuntimeException`;
  - V-2: the `unavailable` copy says "use your password" on password-off units.
  The Leader wrote the definition into `tasks.md`; the user approved it ("si, apruebo").
- **Implementer (T2, `sonnet`), attempt 1:** implemented as defined; detailed notes in `tasks.md` T22.
  - **Environment note:** test-compile failed in the checkout during the run (`cannot find symbol OtpLog`, `cannot access User`), with or without the change. The Implementer therefore ran the Maven gates in a scratch mirror. That is the known dirty-`target/` symptom.
  - **Leader re-run in the real checkout, after the Implementer stopped:** `generated-sources` and the classes were wiped and the clean recompile ran: 2415 / 1066 / 89, BUILD SUCCESS. So the failure was environmental, not caused by the code.
- **Leader re-run (non-author):**
  - full `mvn -o -pl marlo-web -am test`: 994 / 0 / 0 (986 before);
  - Checkstyle delta 0 on the 6 changed Java files;
  - `i18n-check.sh`: no FIX.
- **Manual falsifier recorded (`login.js`):** the real `otpShowError` was run under node with stubs. With `passwordEnabled === false` it returns `unavailable` at HEAD and `unavailablePasswordOff` after the change. With `passwordEnabled === true` it returns `unavailable` both times. There is no JS test runner in the repo (an open TRD question).
- **Attempt 1, Reviewer (T3, `opus`, read-only):** **PASS**. All four declared deviations accepted (`this.request`, the replaced null-request test, extra OPS-001 / step-11 text, the node falsifier). Advisories, deferred to the documentation commit:
  - design §5.1 step 1 still says the rate limits run "before anything else", and the `OtpRequestCodeAction` class Javadoc says the unconfigured check "comes first". Both should read "first after step 0";
  - the `i18nFile` interceptor still runs before step 0 for every method. That is pre-existing and the same for every account; it means the design §4 claim "no session read" holds for the action, not for the whole stack.
- **Validation findings closed by this task:** V-1, V-4 and V-2. Still open, for the documentation commit: V-3 and V-5 to V-11.
- **Environment state after the task:** nothing running; no database touched.

---

## Leader note — documentation remediation after `/akili-validate` (2026-10-18)

Closes validation findings V-3, V-5 to V-11 (`validation-report.md`), with the two T22 Reviewer advisories. No product behaviour changed. The only Java edit is the Javadoc comment of `OtpRequestCodeAction` ("comes first" now reads "first after step 0", as design §5.1 step 1 does).

**Files traced outside this spec's task list (V-9).** Each was read during validation. None is a defect, and each has an owner:

| Item | Owner | Where it is recorded |
|---|---|---|
| `CrpAdminManagmentAction`, `CrpPpaPartnersAction`, `CrpProgamRegionsAction`, `CrpSiteIntegrationAction`, `CrpUsersAction`, `ClusterActivitiesAction`, `ProjectPartnerAction`, `ValidateUserAction`, `LoginAction`, `SignInMethodRule`, `WelcomeSignInText`, `V2_6_0_20261016_1525__AddPasswordLoginSwitch.sql`, and the password-off changes in `OtpEligibility`, `OtpRequestCodeAction`, `OtpVerifyCodeAction`, `CrpByUserEmailAction` | `CHG-PWD-SWITCH-001` (archived at `docs/specs/archive/2026-10-18-changes--password-login-switch/`) | Design §2, "Files outside the original footprint" |
| `marlo-web/src/main/resources/config/marlo-test.properties` (the three `otp.*` keys, empty) | Requirements Decision Log 2026-10-16 (commit `160ec7dc4a`); no task owns it | Design §2 |
| Commit `89ac516f89` (hover rule in `customLogin.css`, `global.css`, `header.ftl`): no `[SPEC:]` prefix and no entry here | Not part of this spec | Design §2 |
| `utils/UnitTextProvider.java` | T20 | Design §2 |

**Historical citations that this log leaves as written.** Entries of T01 to T21 name the allow-list, `OTP_ALLOWED_EMAIL_DOMAINS`, `crp_otp_allowed_email_domains` and the `is_cgiar_user`-free rule. They were true when written. T21 superseded the allow-list. `CHG-PWD-SWITCH-001` T03 later restored a Cognito read on password-off units and replaced the T21 static lock `theCodePathNeverReadsIsCgiarUserOrTheCognitoSpecificity` with counting-double tests (the T21 entry above says "no longer reads `is_cgiar_user` or `CognitoAuthSpecificity`"; that holds for password-on units only). `tasks.md` T21 carries the same notes.

**Review-round tripwire (V-10).** The budget was 23 rounds, extended to 28 by the user at the T11 gate (entry above), and the tally stopped at "23 of 28" after T14. Counting the Reviewer verdict lines recorded after it (T15 to T22), there are 15: T15 attempt 1 (FAIL); T06 rework attempts 1 and 2; T15 continued attempt 2; T16 attempts 1 and 2; T17; T18 attempts 1, 2 and 3; T19 attempts 1 and 2; T20; T21; T22. That makes about 38 recorded rounds, so the 28th round was reached at T16 attempt 1 and the tripwire was passed during T15 to T21 **without a recorded stop or a second extension**. This is a count of verdict lines, not a measured figure, and a task that ran a second lens in one round would be counted once. The cause is the same as at T11: amendments the user chose (T17 to T22) and real FAILs (T15, T16, T18, T19). Nothing is retroactively approved by this note.
