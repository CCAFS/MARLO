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
