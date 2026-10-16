# Password Login Switch — Execution Log

## Document Control

| Field | Value |
|---|---|
| Spec ID | `CHG-PWD-SWITCH-001` |
| Spec path | `docs/specs/changes/password-login-switch` |
| Branch | `A2-2631-Implement-Center-Email-OTP-Authentication` |
| Approval Mode | `gated` (from `proposal.md`) |
| Leader | Claude Code session, `opus` (T1) |
| Implementer / Reviewer wrappers | `akili-implementer` (`sonnet`, T2) / `akili-reviewer` (`opus`, T3, read-only) |
| Budget (design §18) | 8 tasks · ~1,300 LOC · 10 review rounds |
| Started | 2026-10-16 |

## Budget Tracking

| After task | Tasks done | Review rounds used | Notes |
|---|---|---|---|
| T01 | 1 / 8 | 1 / 10 | — |

---

## Task Execution History

### CHG-PWD-SWITCH-001-T01 — Constants and migration

- **Final status:** PASS
- **Date:** 2026-10-16
- **Implementer attempts:** 1
- **Effort:** `high` (migration touching auth settings; a step below `max` because it is DML-only seeding with no data rewrite)
- **Skills assigned:** `marlo-migration`, `marlo-verify` (as listed in the task; no deviation)

#### Attempt 1

- **Files changed:**
  - `marlo-data/src/main/java/org/cgiar/ccafs/marlo/config/APConstants.java` (+3: `PASSWORD_LOGIN_ACTIVE = "system_password_login_active"`)
  - `marlo-web/src/main/java/org/cgiar/ccafs/marlo/config/APConstants.java` (+3, identical constant)
  - `marlo-web/src/main/resources/database/migrations/V2_6_0_20261016_1525__AddPasswordLoginSwitch.sql` (new)
  - `docs/specs/changes/password-login-switch/probes/ac1-ac14-migration.sh` (new)
  - `docs/specs/changes/password-login-switch/probes/output/ac1-ac14-migration.green.out` (new)
  - `docs/specs/changes/password-login-switch/probes/output/ac1-ac14-migration.FALSIFIER-word-red.out` (new)
  - `docs/specs/changes/password-login-switch/probes/output/ac1-ac14-migration.FALSIFIER-notexists-red.out` (new)
- **Implementer verification:**
  - Compile gate (classes wiped): Maven exit 0, BUILD SUCCESS; 2415 files marlo-data, 1064 marlo-web, 78 test.
  - Checkstyle 8.18 on both `APConstants`: HEAD 0, tree 0 → delta 0. Java hygiene check: no findings.
  - Migration gate `probes/ac1-ac14-migration.sh`: `RESULT: 0 failed` — (a) clone of the four touched tables from a full copy, applied twice, 4/4/4 rows, whole `parameters` checksum identical after run 2, type 5 not seeded, no `custom_parameters` row; other rows unchanged by checksum; (b) database without units 45/47, twice, no failure; (c) database without unit types or parameters, twice, 0 rows; three descriptions byte for byte against `requirements.md` DA-002 and `design.md` §3.
  - Falsifiers (scratch SQL copies outside the repo): one-word change ("sign in" → "log in") → 2 failed; `NOT EXISTS` dropped → run 2 gives 8 rows instead of 4, 4 failed.
- **Evidence re-run (Leader-inline, non-author):** **VERIFIED.**
  - Compile gate re-run: `MVN_EXIT=0`, BUILD SUCCESS, 2415 / 1064 / 78 files — same as reported.
  - Probe re-run on a freshly built `aiccradb_pwd_probe` (dump of `aiccradb_actsave` + `V2_6_0_20261009_1025` + `V2_6_0_20261015_1418`): exit 0, 31 PASS, `RESULT: 0 failed`; output identical to the recorded green file apart from exit/checksum/drop lines. `aiccradb_pwd_probe` dropped afterwards; no probe database remains.
- **Reviewer verdict:** **PASS** — migration, both constants and the probe meet DA-001, DA-002, DA-003, FN-001 and the T01 Scope, Tests, Falsifier and Disqualifier rules. The Reviewer compared the three seeded texts with `requirements.md` §3.4 and `design.md` §3 directly: identical once Markdown wraps are joined; every wrap falls between whole words, so the one-sided un-wrap hides nothing. Type ids 1–4 are unit *type* ids as DA-001 requires; `FROM global_unit_types` skips missing types.
- **runtime events:** none
- **spawns:** implementer 23 calls, 110656 tokens, ended partial (report carried `Not Done / Assumptions`, assumptions only — see below); reviewer 10 calls, 59365 tokens, ended complete

#### Not Done / Assumptions (Implementer, verbatim)

> - **Expected texts:** the probe reads them from `requirements.md` DA-002 and `design.md` §3, never from the migration. The only transformation is un-wrapping the Markdown source lines (strip indentation, join with one space). The database side is compared exactly as stored, so the disqualifier on normalizing both sides does not apply.
> - **First probe attempt:** it failed once on my own fixture, because the section (b) inserts ran with foreign-key checks on. I fixed the fixture and added a guard that fails if the fixture loads empty. The green output is from the final script.
> - **Cloned tables:** section (a) runs on a clone of the four touched tables taken from the full copy, so the script can be re-run and the falsifiers have a clean start. The full copy itself was not migrated.
> - **Section (c):** the empty-database shape only proves no failure and 0 rows.
> - **Left alone:** `center-email-otp-login/probes/ac21-migration.sh` and its expected texts are untouched. It needs a re-run after T08 rewords DA-002.
> - **AC-1:** only the data half is proven here (no `custom_parameters` row is seeded and the default is `true`). The resolver reading a missing value as on belongs to a later task.
> - **Probe README:** I did not write one for this spec's `probes/`. The script header documents its environment.

Leader adjudication: the field names no blocker and no owed item of T01 (the AC-1 resolver half is T02's scope; a probe README is not in T01's Files list). Assumptions-only → no continuation; task closable.

#### ADVISORY (Reviewer, 4R — recorded, not acted on)

- **Risk (traceability):** AC-1's second half ("a unit with no `custom_parameters` row reads it as on") depends on the resolver; T02's `isPasswordSwitchOn` `null`/`""` tests cover it but are not tagged AC-1.
- **Reliability:** after this migration runs, `CHG-OTP-LOGIN-001` `probes/ac21-migration.sh` fails on the two reworded OTP descriptions until T08 amends that spec's DA-002 — a known red, expected per `tasks.md` T01 Consumers, not a regression.
- **Readability:** the constant comment cites a requirement (`CHG-PWD-SWITCH-001-FN-001`) where neighbours cite a task.
- **Risk (low):** both falsifier outputs record an absolute local scratchpad path on line 5.
- **Resilience:** run (a) uses a clone of the four touched tables, not the full copy — sufficient because the migration reads/writes only `parameters` and `global_unit_types`, but the Tests field's "throwaway copy" is met through a subset clone.

#### Forward pointers

- **→ T02:** tag the `isPasswordSwitchOn` `null` / `""` / missing-value tests as covering **AC-1** (resolver half), so `/akili-validate` finds AC-1 fully proven.
- **→ T08:** re-run `CHG-OTP-LOGIN-001` `probes/ac21-migration.sh` after amending its DA-002; until then its red on the two descriptions is expected.

- **Requirements covered:** FN-001 (data half), DA-001, DA-002, DA-003; AC-14; AC-1 (data half).
- **Decisions made:** T01 and T02 run sequentially despite being independent — both need Maven builds on one `target/` tree (CLAUDE.md *Concurrency*). No execute-time spec edits.
- **Issues encountered:** the Implementer's first probe run failed on its own fixture (FK checks during section (b) inserts); fixed inside the attempt, not a reported verification failure.
- **Final verification:** compile gate green, Checkstyle delta 0, migration gate green, both falsifiers red — re-run VERIFIED.
- **Continue gate:** pending user (gated mode).
