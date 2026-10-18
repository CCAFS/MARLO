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
| T02 | 2 / 8 | 2 / 10 | — |
| T03 | 3 / 8 | 4 / 10 | two parallel lens verdicts (security; reliability + test validity) |
| T04 | 4 / 8 | 5 / 10 | — |
| T05 (attempt 1) | 4 / 8 | 7 / 10 | two parallel lens verdicts: security FAIL, reliability PASS |
| T05 (attempt 2) | 4 / 8 | 9 / 10 | security FAIL, reliability PASS → **budget tripwire** |
| T05 (attempt 3) | 5 / 8 | 11 / 16 | ceiling raised to 16 by the user; both lenses PASS |
| T06 (attempt 1) | 5 / 8 | 12 / 16 | Reviewer FAIL (falsifier evidence) |
| T06 (attempt 2) | 6 / 8 | 13 / 16 | Reviewer PASS |
| T07 (attempt 1) | 6 / 8 | 14 / 16 | Reviewer FAIL (copy not compared with the approved mockup) |
| T07 (attempt 2) | 6 / 8 | 15 / 16 | Reviewer PASS; closure waits for the user's read of the copy |
| T07 (attempt 3, reopened) | 7 / 8 | 16 / 18 | ceiling raised to 18 by the user; Reviewer PASS |
| T08 (attempt 1) | 7 / 8 | 17 / 18 | Reviewer FAIL (Cognito refusal stated without the override qualifier) |
| T08 (attempt 2) | 7 / 8 | 18 / 18 | Reviewer FAIL (2 surviving siblings) → **budget tripwire**; ceiling raised to 19 by the user |
| T08 (attempt 3) | 8 / 8 | 19 / 19 | Reviewer PASS |

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
- **Continue gate:** user approved commit and continue to T02 (2026-10-16); committed `1550c6708c`.

### CHG-PWD-SWITCH-001-T02 — The method rule

- **Final status:** PASS
- **Date:** 2026-10-16
- **Implementer attempts:** 1
- **Effort:** `xhigh` (the rule decides who can authenticate — security per the effort dial)
- **Skills assigned:** `tdd`, `marlo-verify` (as listed in the task; no deviation)

#### Attempt 1

- **Files changed:**
  - `marlo-web/src/main/java/org/cgiar/ccafs/marlo/security/otp/SignInMethodRule.java` (new, GPL header): `methodsFor(...)`, `Methods{cognito, code, password, needsEmail}`, `isPasswordSwitchOn(String)`
  - `marlo-web/src/test/java/org/cgiar/ccafs/marlo/security/otp/SignInMethodRuleTest.java` (new, 21 tests)
- **Implementer verification:**
  - Oracle: 64 literal rows (K C S X P E) written by hand from design §1, plus raw-spelling and invariant tests (≥1 method or `needsEmail`; Cognito never password; at most two methods; no code for a username or without the key). AC-3…AC-7, FN-010, FN-003 named tests; `null`/`""` switch tests tagged AC-1 (resolver half) — T01 forward pointer closed.
  - Red run (stub with T21 behaviour, no override): 7 of 21 failed on behavioural assertions; the enumeration named exactly the 8 override rows `1 0 S X 0 E` (e.g. `1 0 0 0 0 1 expected code but was password`). The stub's `isPasswordSwitchOn` was already correct, so those tests were seen red only through falsifier (b).
  - Falsifier (a), override dropped: exit 1, 7 failures (`ac4…` `expected:<[cod]e> but was:<[non]e>`; table `1 0 0 0 0 1 expected code but was none`).
  - Falsifier (b), `isPasswordSwitchOn` → `OtpDomainPolicy.isSwitchOn`: 5 failures (`ac1EmptyPasswordSwitchValueIsOn`, `ac1NullPasswordSwitchValueIsOn`, `ac1MissingPasswordSwitchReadsAsOn…`, `passwordSwitchValueThatIsNotExactlyFalseIsOn`, raw-spelling enumeration).
  - Compile gate exit 0 (2415 / 1065 / 79); `SignInMethodRuleTest` 21 run, 0 failures; Checkstyle 8.18 delta 0; hygiene ok.
- **Evidence re-run (Leader-inline, non-author):** **VERIFIED** — classes wiped, `mvn -o -pl marlo-data,marlo-web -am test -Dtest=SignInMethodRuleTest -DfailIfNoTests=false -Dsurefire.failIfNoSpecifiedTests=false` → `MVN_EXIT=0`, compiled 2415 / 1065 / 79, `Tests run: 21, Failures: 0, Errors: 0, Skipped: 0`.
- **Reviewer verdict:** **PASS** — rule is `cognito = C`, `password = !C && (P || !K)`, `code = (K && isOffered(S, X, id)) || (K && !P && !C && isEmail)`, `needsEmail = K && !P && !C && !isEmail`, matching design §1 rows and FN-010. Reviewer derived all 64 rows independently: all match, all literals. Reuse of `OtpDomainPolicy.isOffered` is faithful (code switch on + has domain + not excluded, nothing else). Cognito never gets password or override code (SEC-003, FN-005). `isPasswordSwitchOn` per DD-2 with every required spelling.
- **runtime events:** none
- **spawns:** implementer 31 calls, 156276 tokens, ended partial (`Not Done / Assumptions`, assumptions only); reviewer 11 calls, 67181 tokens, ended complete

#### Not Done / Assumptions (Implementer, verbatim)

> - The VS Code Java language server (pid 2745) writes ECJ stub classes into `marlo-web/target/classes`. My first in-tree run of falsifier (b) read those stubs, "Unresolved compilation problems", so I discarded it. I reran (b) with `javac` and JUnitCore on a scratch copy in the scratchpad. The same harness gave `OK (21 tests)` on the real source. The in-tree source was never left mutated; it matches the verified copy.
> - "Email" means `OtpDomainPolicy.domainOf(identifier) != null`. A `null` or empty identifier is treated as a username. `needsEmail` is true exactly when the password-off override applies (key configured, password off, no Cognito) and the identifier is not an email.

Leader adjudication: no blocker, no owed item — assumptions only; the email definition was checked by the Reviewer against FN-010. Task closable.

#### ADVISORY (Reviewer, 4R — recorded, not acted on)

- **Readability:** class Javadoc uses `<table summary="the rule">`; `summary` is not valid HTML5 and may trip Java 17 doclint (build skips javadoc). Suggest `<caption>` or a list.
- **Reliability:** a malformed identifier (`ana@`, `@x.org`) counts as a username and gets `needsEmail` under the override — safe, but the FN-010 message also covers a mistyped email; T05/T06 wording should keep that in mind.
- **Risk (low):** `Methods` has no `toString`; consumers logging a decision (§11) will format it themselves.

#### Forward pointers

- **→ T06:** the FN-010 message also appears for a mistyped email (`ana@`), not only a username — consider when wording the i18n key (advisory).

- **Requirements covered:** FN-003, FN-004, FN-005, FN-006, FN-007, FN-010 (rule part); SEC-003; AC-9; AC-1 (resolver half, closing T01's pointer); AC-3…AC-7 at rule level.
- **Decisions made:** none beyond the task. No execute-time spec edits.
- **Issues encountered:** the IDE Java language server writes ECJ stub classes into `marlo-web/target/classes`, which can make an in-tree Maven run read stubs; the clean-compile gate (wipe classes first) avoids it. Worth noting for T03–T05 briefs.
- **Final verification:** compile gate green; 21/21 tests; both falsifiers red; Checkstyle delta 0 — re-run VERIFIED.
- **Continue gate:** user approved commit and continue to T03 (2026-10-16); committed `db54da7ab9`.

### CHG-PWD-SWITCH-001-T03 — Code endpoints follow the rule

- **Final status:** PASS
- **Date:** 2026-10-16
- **Implementer attempts:** 1
- **Effort:** `xhigh` (security path, partial reversion of T21)
- **Skills assigned:** `tdd`, `marlo-verify`, `error-handling-patterns` (as listed in the task; no deviation)
- **Leader narrowing in the brief:** `isPasswordRefused` left to T05; `CrpByUserEmailAction` switching to `methodsFor` left to T04; `LoginAction` / `ValidateUserAction` untouched.
- **continuations:** 1 (design §11 `via=password_off` field on `auth.otp.request outcome=sent` — owed: §11 is in T03's Design list and the line belongs to the step-4 issue)

#### Attempt 1

- **Files changed:**
  - `marlo-web/src/main/java/org/cgiar/ccafs/marlo/security/otp/OtpEligibility.java` — password key in `SPECIFICITY_KEYS`; `CustomParameterManager` + `ParameterManager` injected back (7-arg constructor); `isOffered` admits the override via `SignInMethodRule`; new `eligibilityBasis()` → `Basis { NONE, UNIT_SETTINGS, PASSWORD_SWITCH_OFF }` with `isEligible` delegating to it (Cognito flag via `CognitoAuthSpecificity.isActiveFor` only on password-off units, before `getUserByEmail`; Cognito refusal after `existActiveCrpUser`); new `methodsFor(identifier, units, cognitoAccountByUnit)` → `Map<Long, SignInMethodRule.Methods>`, query always run; once-per-unit ERROR set (`ConcurrentHashMap.newKeySet()`) writing `auth.password.last_resort gu=<acronym> reason=code_key_unconfigured`
  - `marlo-web/src/main/java/org/cgiar/ccafs/marlo/security/otp/OtpLog.java` — `request(..., boolean viaPasswordOff)` overload; ` via=password_off` only on `SENT` (continuation)
  - `marlo-web/src/main/java/org/cgiar/ccafs/marlo/action/home/OtpRequestCodeAction.java` — step-4 Javadoc/comment; records the basis and passes it to `OtpLog` (continuation)
  - Tests: `OtpEligibilityTest` (32 → 63), `OtpRequestCodeActionTest` (49 → 62), `OtpVerifyCodeActionTest` (48 → 53), `OtpStrutsPackageTest` (8, constructor), `CrpByUserEmailActionTest` (14, constructor + three-key query literal only), `OtpLogTest` (→ 17)
- **Implementer verification:**
  - Red run: 5 new override tests failed on today's `isOffered` (`expected:<{status=sent, destination=a***@partner.org}> but was:<{status=domainNotEnabled}>`; `expected:<[sent]> but was:<[domainNotEnabled]>`), the other 81 passed.
  - Falsifiers (each applied, run, restored `cmp`-identical, re-run green): (a) Cognito flag read after the lookup → 3 red (`the flag is read before the lookup expected:<[custom:cognito_auth_active:7]> but was:<[]>`); (b) resolver on password-on units → 5 red (`zero Cognito reads expected:<[]> but was:<[custom:cognito_auth_active:7]>`); (c) early exit kept in `methodsFor` → 5 red (`the query runs without a key … but was:<[]>`); (d, extra) Cognito refusal before membership → 3 red; via dropped → red (`expected:<...ms=12[ via=password_off]> but was:<...ms=12[]>`); via on every eligible account → red.
  - T21 locks replaced: `theCodePathNeverReadsIsCgiarUserOrTheCognitoSpecificity` + `thePredicateHoldsNoManagerThatCouldReadTheCognitoSpecificity` → `onAPasswordOnUnitTheCognitoFlagIsNeverResolvedAndIsCgiarUserIsNeverRead` (counting doubles + `CountingUser`), with request/verify counterparts; constructor lock → 7 parameters; Spring-scan test registers both managers. Family-wide grep: no other locks of this shape.
  - Compile gate exit 0; full suite `mvn -q -o -pl marlo-web -am test` exit 0, 905 tests, 0 failures; Checkstyle 8.18 delta 0 on changed files; hygiene: no FIX (one REVIEW `LOG-NULL` on `unit.getAcronym()` — `unit` comes from the filtered non-null map).
- **Evidence re-run (Leader-inline, non-author):** **VERIFIED** — classes wiped, `mvn -o -pl marlo-web -am test` → `MVN_EXIT=0`, `Tests run: 905, Failures: 0, Errors: 0, Skipped: 0`; surefire per class: `OtpEligibilityTest` 63, `OtpRequestCodeActionTest` 62, `OtpVerifyCodeActionTest` 53, `CrpByUserEmailActionTest` 14, `OtpStrutsPackageTest` 8, `OtpLogTest` 17 — all 0 failures.
- **Reviewer verdicts (parallel lens mode — effort `xhigh` + security surface):**
  - **Security lens: PASS** — DD-4 order holds (resolver never called and `isCgiarUser` never read on password-on units); SEC-004: on a password-off unit every account resolves the Cognito flag before the lookup, a Cognito member pays a member's queries then gets `Basis.NONE` → decoy + neutral `sent`; SEC-003/FN-005: override off for Cognito accounts, verify re-check refuses after a setting change; `via` server-log only, only on `SENT`; type-less unit throw depends only on the unit → not an oracle; OPS-002 memory is per-unit, sanitised acronym, ids from `GlobalUnit` objects; FN-002 uncached.
  - **Reliability + test-validity lens: PASS** — every Tests item has a test that exercises it; counting doubles record arguments (type id vs unit id proven) with control tests; neutrality tests assert issued rows (disqualifier not triggered); `methodsFor` fits DD-6; `otpEnabledFor` unchanged for its current caller until T04; ERROR memory thread-safe; removed locks authorised by DD-4 item 1 / P-16.
- **runtime events:** none
- **spawns:** implementer 88 + 15 calls, 387006 + 424898 tokens (attempt + continuation; cumulative per host), ended complete; reviewer (security) 16 calls, 82976 tokens, ended complete; reviewer (reliability) 21 calls, 134814 tokens, ended complete

#### Not Done / Assumptions (Implementer, verbatim)

First report:

> - **`via=password_off` (design §11):** not delivered. It is not in T03's scope text and no task names it, so it needs an owner.
> - **`CrpByUserEmailActionTest`:** I edited it only for the 7-argument constructor and the three-key query literal. `CrpByUserEmailAction` and `otpEnabledFor` are untouched, so the `:314` early-exit lock stays until T04.
> - **Neutrality counts:** a Cognito account makes exactly a member's call list, and the Cognito reads are identical for all three accounts. An unknown account differs only by the membership call, which it cannot reach, as in T21.
> - **Store failures:** `methodsFor` lets them propagate, so T04's action owns the DD-6 fail-open and its ERROR.
> - **`isOffered` and the key:** it now checks `keys.isConfigured()` through the rule; before, the request action did it first.
> - **Log format:** the last-resort line writes the acronym as stored (sanitised), not lower-cased like `auth.otp.*` lines. T05's INFO line should match whichever convention is chosen.
> - **Type-less unit:** a password-off unit with no type and no own Cognito row makes the resolver throw. The action maps that to the neutral `sent`.
> - **Surefire:** 2.12.4 rejects multiple `Class#method` patterns, so the red run used whole classes.
> - **Environment:** a `run-marlo-java17.sh` / cargo server (pid 9154/11586, since 09:50) runs in this checkout, and the VS Code Java language server writes into `target/`. One unwiped run failed with `NoClassDefFoundError: OtpPending`; I discarded it and reran after wiping.

Continuation report:

> - Reading of §11/§13: `outcome=sent` is logged only when a real code is handed to the mailer. The neutral, decoy, Cognito and failed-predicate paths log `outcome=neutral` or `internal_error` and never carry `via`. The line is server-side only and the response is unchanged, so I added it on the real send only.
> - A code mailed under the override whose later session write fails is logged as `internal_error`, without `via`.
> - One build failed with `cannot access User` in the build tree, in files I did not touch. It passed unchanged after re-wiping.

Leader adjudication: the first report owed one item (`via=password_off`) → continuation 1 delivered it. Everything else is assumptions; none names a blocker. Task closable.

#### ADVISORY (Reviewers, 4R — recorded, not acted on)

- **Security lens — Risk:** password-off unit with no type and no own Cognito row → resolver throws → nobody gets a code; with T05's gate that unit has no working sign-in. Confirm `global_units.global_unit_type_id` NOT NULL or treat a missing type as `false`.
- **Security lens — Risk:** `methodsFor` is public; OPS-002's non-drivability depends on T04 passing only DB-loaded units, never request-built ids.
- **Security lens — Risk:** `isOffered` now returns `false` without a key; response unchanged because the action rejects an unconfigured key first; documented in Javadoc.
- **Security lens — Readability:** the 3-arg / 4-arg `isOffered` overloads (one forcing password `"true"`) are easy to swap; a name like `isOfferedWithPasswordOn` would help.
- **Reliability lens — Reliability:** "same query count for known, unknown and Cognito accounts" is proven as the same Cognito reads for all three; an unknown account still skips `existActiveCrpUser`, exactly as under T21 and as design §13 reads ("every account that reaches the lookup"). **Leader reading recorded:** this matches §13; `/akili-validate` should read the task line through §13.
- **Reliability lens — Resilience:** same type-less unit NPE (`CognitoAuthSpecificity.java:75`): both endpoints fail closed (neutral `sent` / `invalid`), but members of such a unit are silently locked out of the code with the password off.
- **Reliability lens — Risk (T04 handoff):** the T21 text lock on `CrpByUserEmailAction.otpEnabledByUnit` is gone with no action-level behavioural replacement yet.
- **Reliability lens — Readability:** `theBasisMakesExactlyTheCallsIsEligibleMakes…` cannot fail while `isEligible` delegates; it only guards future drift.
- **Reliability lens — Readability:** `isOffered` Javadoc could say the query still runs without a key.

#### Forward pointers

- **→ T04:** pass only units loaded from the database to `methodsFor`, never ids built from the request (OPS-002 non-drivability) `[advisory-grade]`.
- **→ T04:** the T21 lock on `CrpByUserEmailAction` text is gone — T04's tests should include an action-level zero-Cognito-read assertion on password-on units when it moves to `methodsFor` `[advisory-grade]`; T04's own scope already replaces the `CrpByUserEmailActionTest.java:314` lock. Whether `otpEnabledFor` becomes dead code after T04 is a cleanup proposal, not T04 scope.
- **→ T05:** the INFO line `auth.password.refused gate=password_switch gu=<acronym>` should use the same acronym convention as T03's last-resort line (as stored, sanitised).
- **→ T08:** type-less password-off unit locks code sign-in (resolver NPE) — candidate runbook prerequisite note `[advisory-grade]`.

- **Requirements covered:** FN-002, FN-004, FN-005, FN-006; SEC-003, SEC-004; OPS-002 (memory + ERROR line); AC-4, AC-5, AC-6, AC-8 at endpoint level; design §11 `via=password_off`.
- **Decisions made:** continuation scope set by the Leader (owed §11 item). Parallel lens review chosen per the 4R table (effort `xhigh`, security surface). No execute-time spec edits.
- **Issues encountered:** the user's own `run-marlo-java17.sh` / cargo server (since 09:50) runs in this checkout while builds wipe `target/classes` — reported to the user, not touched. VS Code language-server ECJ stubs and one `cannot access User` dirty-tree failure, both cleared by wiping classes.
- **Final verification:** wiped compile + full suite 905/0, all falsifiers red, Checkstyle delta 0 — re-run VERIFIED.
- **Continue gate:** user approved commit and continue to T04 (2026-10-16); committed `76f029feba`.

### CHG-PWD-SWITCH-001-T04 — Pre-auth lookup reports both flags

- **Final status:** PASS
- **Date:** 2026-10-16
- **Implementer attempts:** 1
- **Effort:** `high` (task is S, but it shapes a pre-auth response with a fail-open path — SEC-005)
- **Skills assigned:** `marlo-verify`, `api-design-principles` (response shape only) — as listed; no deviation
- **Leader brief items tagged `[advisory-grade]`:** DB-loaded units only to `methodsFor`; action-level "no extra Cognito read" assertion; keep `otpEnabledFor` (cleanup proposal, not scope)

#### Attempt 1

- **Files changed:**
  - `marlo-web/src/main/java/org/cgiar/ccafs/marlo/action/json/global/CrpByUserEmailAction.java` — pass 1 builds entries + `cognitoEnabled` and records `cognitoEnabled && isCgiarUser` per unit; one `methodsFor(userEmail, listed, cognitoAccountByUnit)` on the loaded+listed units; pass 2 writes `otpEnabled` and `passwordEnabled`; `RuntimeException` → ERROR with the exception, `otpEnabled=false`, `passwordEnabled = !cognitoAccount`
  - `marlo-web/src/test/java/org/cgiar/ccafs/marlo/action/json/global/CrpByUserEmailActionTest.java` — 14 → 23 tests; the `:314` "no specificity query when unconfigured" lock replaced by "exactly one query" (counted at `OtpChallengeManager.specificityValuesFor`, key list and unit ids asserted in listed order)
- **Implementer verification:**
  - Red run (before the action change): 8 of 23 failed (`Cognito, password on, code off: passwordEnabled expected:<false> but was:<null>`; `expected:<[...|[1, 2]]> but was:<[]>`; `L1 and L4 only ... but was:<[]>`).
  - Falsifier (a), executed as `methodsFor` with an empty Cognito map (= the pre-P-18 order, map not yet filled) → 3 red (`a Cognito account is not offered the last resort: passwordEnabled expected:<false> but was:<true>`; extra `gu=L3` last-resort line). Falsifier (b) `lastResortLogged.add(...) || true` → last-resort test saw L1/L4 five times → red. Both restored (`cmp`), suite green.
  - Compile gate exit 0; full suite `mvn -o -pl marlo-web -am test` exit 0, 914 / 0; Checkstyle 8.18 HEAD 0 / tree 0 → delta 0; hygiene exit 0.
- **Evidence re-run (Leader-inline, non-author):** **VERIFIED** — classes wiped, `mvn -o -pl marlo-web -am test` → `MVN_EXIT=0`, `Tests run: 914, Failures: 0, Errors: 0, Skipped: 0`, `CrpByUserEmailActionTest` 23 / 0; `.claude/skills/marlo-verify/scripts/checkstyle.sh` on both files → working tree 0 violations (re-run after the Reviewer noted the brief omitted the Checkstyle evidence).
- **Reviewer verdict:** **PASS** (lens checklist) — DD-6 two-pass shape; `methodsFor` once, only on `crpManager.crpUsers` units; fail-open matches the requirements Decision Log SEC-001 exception; empty-map falsifier is a faithful stand-in for "computed before the Cognito pass"; SEC-005/AC-11: `passwordEnabled` (and its failure value) is a function of unit settings, key state and `isCgiarUser && cognitoEnabled`, all already in the response (design §13 "Pre-auth disclosure"); NF-002: exactly one query for no key / username / several units, zero for zero units, counted at a boundary that maps 1:1 to the DAO native query (disqualifier not triggered); all three advisory-grade items met.
- **runtime events:** none
- **spawns:** implementer 27 calls, 146646 tokens, ended partial (`Not Done / Assumptions`, assumptions + cleanup proposal only); reviewer 15 calls, 83741 tokens, ended complete

#### Not Done / Assumptions (Implementer, verbatim)

> - `OtpEligibility.otpEnabledFor` is now unused by the action and I left it in place. Cleanup proposal: delete it, or leave it deliberately.
> - The action's `logger` is still `LoggerFactory.getLogger(FlaghshipsByCrpAction.class)`, so its ERROR lines carry the wrong class name. That predates this task and I did not change it. The tests read ERROR lines from the root logger so they do not depend on it.
> - Design §4 has no row table, so I took "each §4 row" from the design §1 rule table plus the requirements scenarios.
> - If a unit's entry fails in pass 1, it is left out of the response and is not passed to `methodsFor`.
> - `login.js` (T06) is not touched.

Leader adjudication: no blocker, no owed item; the "no new user field" test is covered by the existing exact key-set assertion (Reviewer confirmed). Task closable.

#### ADVISORY (Reviewer, 4R — recorded, not acted on)

- **Risk:** the Leader's Reviewer brief omitted the Checkstyle evidence — closed by the Leader re-run above (0 violations).
- **Resilience:** the failure-path ERROR is written on every failing call and is unauthenticated-triggerable (OPS-002 limits only the last-resort line; today's behaviour) — may flood logs with stack traces during a DB outage; throttling is a possible later improvement.
- **Readability:** `otpEnabledFor` and its Javadoc are now unused and still describe the `crpByEmail.do` variant.
- **Readability:** pre-existing wrong logger class (`FlaghshipsByCrpAction.class`, `CrpByUserEmailAction.java:75`).

#### Cleanup proposals (out of this spec's scope — for the user to decide)

- Delete `OtpEligibility.otpEnabledFor` (unused after T04), or keep it deliberately.
- Fix the `CrpByUserEmailAction` logger class name.
- Optionally throttle the `crpByEmail.do` failure-path ERROR.

- **Requirements covered:** FN-002, FN-006, FN-010 (data part); SEC-005; OPS-002 (decision in the lookup); NF-002; AC-7, AC-11 at the action level.
- **Decisions made:** none beyond the task. No execute-time spec edits.
- **Issues encountered:** none.
- **Final verification:** wiped full suite 914/0, both falsifiers red, Checkstyle 0 — re-run VERIFIED.
- **Continue gate:** user approved commit and continue to T05 (2026-10-16); committed `c5c807aaa7`.

### CHG-PWD-SWITCH-001-T05 — Password gate on the two endpoints

- **Final status:** PASS on attempt 3 (attempts 1 and 2 FAIL, security lens, SEC-002)
- **Date:** 2026-10-16
- **Effort:** attempt 1 `xhigh`. The effort dial says `max` for an auth path, and "never max a cheaper tier — escalate the tier"; escalating the Implementer to `opus` would put it on the Reviewer's model and collapse author ≠ auditor, so the Leader kept `sonnet` at `xhigh`.
- **Skills assigned:** `tdd`, `marlo-verify`, `error-handling-patterns` (as listed; no deviation)
- **Leader scope note in the brief:** `OtpEligibility.isPasswordRefused` (design §2) is in scope, since T03 left it to this task.

#### Attempt 1 — FAIL

- **Files changed:**
  - Main: `marlo-web/src/main/java/org/cgiar/ccafs/marlo/security/otp/OtpEligibility.java` (new `isPasswordRefused(String identifier, Long selectedUnitId)`), `marlo-web/src/main/java/org/cgiar/ccafs/marlo/action/json/global/ValidateUserAction.java`, `marlo-web/src/main/java/org/cgiar/ccafs/marlo/action/home/LoginAction.java`, `marlo-web/src/main/java/org/cgiar/ccafs/marlo/action/home/CognitoCallbackAction.java` (constructors), `marlo-web/src/main/java/org/cgiar/ccafs/marlo/action/home/OtpVerifyCodeAction.java` (super call)
  - Tests: `OtpEligibilityTest`, `ValidateUserActionGuardTest`, `LoginActionCgiarGuardTest`; constructor updates in `LoginActionFinishLoginTest`, `LoginActionLogoutTest`, `CognitoCallbackActionTest`, `CognitoLogHygieneTest`; new `marlo-web/src/test/java/org/cgiar/ccafs/marlo/security/otp/PasswordGateScopeTest.java`, `marlo-web/src/test/java/org/cgiar/ccafs/marlo/security/otp/UnconfiguredOtpEligibility.java`
- **Implementer verification:**
  - Red run (stub gate returning false): 27 of 113 failed on behaviour (`thePasswordSwitchRefusesAMemberOfAPasswordOffUnitAndMakesNoCredentialCall`: `expected:<[]> but was:<[jane.local@cgiar.org|whatever-password]>`; `aGlobalUnitIdTheAccountDoesNotBelongToCannotRoundThePasswordGate`; `aFailingSettingsQueryFailsOpenWithOneErrorLine`: `expected:<1> but was:<0>`).
  - Falsifiers: (a) trusting the named unit → 10 red; (b) gate after `userManager.login` in both actions → 14 red. Extra mutants: memberships by typed identifier → 5 red, fail-closed → 3 red, key check removed → 11 red. All restored (`cmp`).
  - Compile gate exit 0. Full suite exit 0, 951 / 0. Checkstyle `--baseline` delta 0. Hygiene: one FIX at `CognitoLogHygieneTest` L440, in an untouched Javadoc. `git diff --stat`: no CLARISA, Shiro, interceptor, struts, properties or FTL file.
- **Evidence re-run (Leader-inline, non-author):** **VERIFIED**.
  - Command: classes wiped, then `mvn -o -pl marlo-web -am test` → `MVN_EXIT=0`, `Tests run: 951, Failures: 0`.
  - Per class: `ValidateUserActionGuardTest` 18, `LoginActionCgiarGuardTest` 16, `LoginActionFinishLoginTest` 17, `LoginActionLogoutTest` 3, `CognitoLogHygieneTest` 17, `CognitoCallbackActionTest` 30, `CognitoLoginActionTest` 34, `OtpVerifyCodeActionTest` 53, `OtpEligibilityTest` 77, `PasswordGateScopeTest` 2. All have 0 failures.
  - Checkstyle `--baseline`: HEAD 0 / tree 0 / delta 0.
- **Reviewer verdicts (parallel lens mode: auth surface, effort `xhigh`):**
  - **Reliability + test-validity lens: PASS.** Every Tests item is exercised. `RecordingUserManager.loginCalls` records `email|password`. Equality is checked against a real wrong-password run of the action. Fail-open catches only `RuntimeException` and writes one ERROR. Null, blank and unknown identifiers are handled. Account resolution matches the realm. The C-2 bypass is closed. `LoginActionFinishLoginTest` has 17 real tests (an 18th `@Test` is inside a Javadoc), so the design §17 count is stale. The constructor change is complete family-wide.
  - **Security lens: FAIL** (verbatim):

> ISSUES:
> 1. **Discovered Issue:** The refusal always answers with `ADLoginMessages.ERROR_LOGON_FAILURE` ("Invalid CGIAR email or password, please try again"). That is the wrong-password answer only for LDAP accounts. The gate refuses accounts without Cognito, and most of those are local accounts (`is_cgiar_user = 0`). For them the realm calls `DBAuthenticator`, which sets no `LOGIN_MESSAGE` (`DBAuthenticator.java:48`, `APCustomRealm.java:187,195`). A real wrong password for a local account therefore gives:
>    - `validateUser.do`: no `messageEror` key at all (`excludeNullProperties=true`, `struts-home.xml:19`). The refusal includes the key. `login.js:908` then increments the reCAPTCHA counter only for the refusal, so even client behaviour differs.
>    - `login.do`: `login.error.userOrPass` = "Invalid email and/or password, please try again" (`LoginAction.java:388`). The refusal shows the CGIAR text.
>    - Inactive accounts: today they get `USER_DISABLED`. A CGIAR account without a nickname gets `ERROR_NO_SUCH_USER`. The gate answers `LOGON_FAILURE` for both.
>
>    The SEC-002 test misses this: its double sets the LDAP message for `localAccount()` (`LoginActionCgiarGuardTest.java:928-933`), which is not what production does for that account kind.
>    * **Violated Rule:** `requirements.md` §3.2 SEC-002 ("indistinguishable, in response and message, from a wrong password"); `design.md` §13 bullet 1. The Decision Log accepts timing only, not a different response.
>    * **Remediation Suggestion:** On refusal, produce exactly what the credential check would have produced for the resolved account:
>      - local: `messageEror` stays null in `validateUser.do`, and `login.error.userOrPass` in `login.do`;
>      - CGIAR: `ERROR_LOGON_FAILURE`;
>      - inactive: `USER_DISABLED`;
>      - CGIAR without a nickname: `ERROR_NO_SUCH_USER`.
>
>      The cleanest way is to have the gate return the account kind, or the realm-equivalent message, instead of a boolean. Rebuild the SEC-002 tests so the wrong-password side uses each kind's real authenticator behaviour.
>
> **Adjudications:**
> - **Item 4 (removed membership): Pivot candidate, not a T05 FAIL.** The gap is confirmed. `crpUsers` returns only active memberships (`GlobalUnitMySQLDAO.java:46`), but `finishLogin` grants the session with the activity-blind `existCrpUser` (`LoginAction.java:441`, `CrpUserMySQLDAO.java:67-68`). So a user whose membership in password-off unit X was removed, who names X and passes the credential check, gets a password session in X. That breaks SEC-001's outcome. However, DD-5 defines membership as "same narrowing … as the SEC-005 guard", which is active-only, so the code follows the spec. The root cause is older: removed members can already sign in with a password today. Closing it needs a spec decision: either make `finishLogin` use `existActiveCrpUser` (the shared tail, Cognito path included), or have the gate count any `crp_users` row. Neither fits inside T05's approved boundary.
> - **Item 3 (padding in the SEC-005 guard): ADVISORY, older than this change.** T05 leaves the guard untouched. The new gate resolves the account through the trimming `UserDAO.getUser` and reads memberships by the stored email, so this change does not make the flaw worse.
> - **Item 1 (signature): conforms in substance.** It resolves the account the same way the realm does (`@` means email, otherwise username; `APCustomRealm.java:167-172`), narrows by membership, and decides through `SignInMethodRule.methodsFor(..., cognitoAccountInG=false, ...)`. That is correct because the SEC-005 guard runs first. The design §2 signature text should be amended to match.

- **Leader adjudication of the FAIL:** upheld and in scope. The Leader checked the factual claim at source:
  - `DBAuthenticator.authenticate` puts only `LOGIN_STATUS` and never `LOGIN_MESSAGE`.
  - `APCustomRealm` sets `USER_DISABLED` for inactive accounts and `ERROR_NO_SUCH_USER` for CGIAR accounts without a nickname.
  - The test double `LoginActionCgiarGuardTest.java:928-933` injects `ERROR_LOGON_FAILURE` for every account.
  
  The two lenses disagree because the reliability lens compared against the double's wrong-password run, and the double does not reproduce the realm per account kind. This FAIL consumes attempt 1.
- **runtime events:** none
- **spawns:** implementer 96 calls, 384688 tokens, ended fail (Reviewer FAIL); reviewer (security) 29 calls, 109212 tokens, ended complete; reviewer (reliability) 26 calls, 137778 tokens, ended complete

#### Not Done / Assumptions (Implementer, verbatim)

> 1. **Signature:** it is `isPasswordRefused(String identifier, Long selectedUnitId)`, not `(account, unitOrNull)`. It resolves the account itself, by email when the identifier contains `@`, otherwise by username.
> 2. **Finding fixed in the gate:** the realm trims and lower-cases the identifier, but `crpUsers` compares it raw. The gate therefore reads memberships by the account's stored email; a padded `" jane@x.org"` would otherwise find no membership and skip the gate.
> 3. **Same flaw in `ValidateUserAction`'s SEC-005 guard, not fixed (out of scope):** the guard still passes `this.userEmail` raw to `crpUsers`, so a leading-space identifier may skip it. Not run live.
> 4. **Active-only memberships:** the gate counts only memberships `crpUsers` returns (active rows, same as the SEC-005 guard). `finishLogin` uses the activity-blind `existCrpUser`, so a user with a removed membership in a password-off unit can still reach the credential check for that unit.
> 5. **ERROR wording:** the design gives none, so I chose `auth.password.gate outcome=settings_unavailable fail_open=true`.
> 6. **Ordering:** the gate runs after the SEC-005 guard in both endpoints.
> 7. **Spring wiring:** not exercised in a live container; I did not touch your server. The `LoginAction`, `ValidateUserAction` and `CognitoCallbackAction` constructors rely on the same implicit constructor autowiring as before. It needs a smoke test of `login.do` and `validateUser.do` on the running stack.

#### ADVISORY (Reviewers, 4R — recorded, not acted on)

- **Security, Risk:** the ERROR line attaches the exception `e`. A future DAO message that echoes bound values could leak the identifier.
- **Security, Risk:** item 3 (the SEC-005 guard passes the raw `userEmail`) and item 4 (`finishLogin` ignores whether a membership is active) are candidates for a separate `bugfix/` spec. The OTP path already enforces `existActiveCrpUser`.
- **Reliability:** the ERROR label `outcome=settings_unavailable` is also written when the account or membership read fails, and it writes a stack trace on every password POST during an outage.
- **Test validity:** `PasswordGateScopeTest` greps only `userManager.login(`. The P-2 ledger also names `new UsernamePasswordToken`. The paths are relative to `marlo-web/`.
- **Readability:** design §17 and the `tasks.md` Consumers field say `LoginActionFinishLoginTest (18)`; the real count is 17. This is a spec-doc correction.
- **Resilience:** the Spring wiring of the new constructor parameter has not been run live; confirm it in the walkthrough.
- **Reliability:** the refusal path skips `getLoginMessages()`, as the accepted SEC-005 precedent does.

#### Pivot candidate (open — user decision required)

A user whose membership in password-off unit X was **removed** can name X, pass the gate (only active memberships count, as DD-5 says), pass the credential check, and get a password session in X. `finishLogin` checks `existCrpUser`, which ignores whether the membership is active. The behaviour predates this change (removed members can already sign in with a password today), but it defeats SEC-001's outcome for that edge.

**User decision (2026-10-16):** the removed-membership gap is **not a Pivot**. T05 stays within its approved scope, and the gap goes to a separate `docs/specs/bugfix/` spec together with the identifier-padding flaw in the SEC-005 guard (Implementer item 3). Attempt 2 fixes only SEC-002. Effort for attempt 2: `sonnet` at `xhigh`, chosen by the user so author ≠ auditor holds. The usual one-level effort bump is waived because the next level, `max`, would need a tier escalation.

#### Attempt 2

- **Feedback:** the security-lens FAIL above, verbatim.
- **Attempt History:** attempt 1 answered every refusal with `ERROR_LOGON_FAILURE`, and its SEC-002 double injected the LDAP message for every account kind. Do not repeat that.

- **Files changed (attempt 2, on top of attempt 1):**
  - Main: `OtpEligibility.java` (`isPasswordRefused` → `passwordGate(identifier, unitId)` returning `PasswordGateDecision{isRefused(), leaveRealmMessage(Session)}`; `realmMessageFor`: inactive → `USER_DISABLED`, active CGIAR → `ERROR_LOGON_FAILURE`, active local → null), `LoginAction.java` and `ValidateUserAction.java` (on refusal: skip `userManager.login`, leave the realm message, then run the unchanged `getLoginMessages()` and failure path; `validateUser.do` skips its email-bearing failure log line on refusal)
  - Tests: `OtpEligibilityTest`, `ValidateUserActionGuardTest`, `LoginActionCgiarGuardTest`, `PasswordGateScopeTest` (also checks `new UsernamePasswordToken`); new `marlo-web/src/test/java/org/cgiar/ccafs/marlo/security/RealCredentialPath.java` (runs the real `UserManagerImp.login`, `APCustomRealm` and `DBAuthenticator`; only the DAO, credential store and LDAP bind are stubbed)
- **Implementer verification:**
  - Falsifier (old constant forced for every kind): 5 red.
    - `ValidateUserActionGuardTest` local: `expected:<null> but was:<Invalid CGIAR email or password, please try again>`
    - inactive: `expected:<[Your account is disabled, please contact the MARLO staff]>`
    - `LoginActionCgiarGuardTest` local: `{loginMessage=[login.error.userOrPass]}` vs the CGIAR text
    - `OtpEligibilityTest`: `active local: no message, and an earlier one is cleared expected null`
  - Gate ignored: 17 red.
  - Both falsifiers restored (`cmp`).
  - Wiped compile exit 0; full suite 958 / 0; Checkstyle `--baseline` delta 0.
- **Evidence re-run (Leader-inline, non-author):** **VERIFIED**.
  - Classes wiped, `mvn -o -pl marlo-web -am test` → `MVN_EXIT=0`, `Tests run: 958, Failures: 0`.
  - Per class: `ValidateUserActionGuardTest` 20, `LoginActionCgiarGuardTest` 18, `LoginActionFinishLoginTest` 17, `OtpEligibilityTest` 79, `PasswordGateScopeTest` 3, `CognitoCallbackActionTest` 30, `OtpVerifyCodeActionTest` 53.
  - Checkstyle HEAD 0 / tree 0 / delta 0.
- **Reviewer verdicts (parallel lens mode):**
  - **Reliability + test-validity lens: PASS.** The attempt-1 defect is fixed. Local and inactive comparisons now run through production code. `leaveRealmMessage` clears like `APCustomRealm.java:195`. No Tests item was removed.
  - **Security lens: FAIL** (verbatim):

> **Attempt-1 issue (SEC-002 message): fixed.** I read the realm and checked each account kind:
> - **Local account:** the realm sets `LOGIN_MESSAGE` to null (`APCustomRealm.java:187,195`), and `leaveRealmMessage` does the same. In `validateUser.do`, `messageEror` gets the same value as a real failure (absent, or a leftover from an earlier attempt in both cases). In `login.do`, both show `login.error.userOrPass`.
> - **Active CGIAR account:** both give `ERROR_LOGON_FAILURE`, mapped to the same text on both endpoints.
> - **Inactive account:** both give `USER_DISABLED`.
> - **Account lookup:** the gate finds the account the same way the realm does (`APCustomRealm.java:167-172`).
>
> **The residual case.** `getCgiarNickname` really is a directory call: `service.searchUserByEmail` at `APCustomRealm.java:338`. It also saves a missing username. AC-10 forbids "no LDAP call" before a refusal, so the realm's `ERROR_NO_SUCH_USER` text (unmapped, so the real failure shows no message) cannot be reproduced without breaking AC-10. The same applies to directory states that only a bind reveals: locked out, password expired, `ERROR_LDAP_CONNECTION` during an outage.
> - **Who it affects:** active CGIAR accounts that have no directory entry (or whose directory account is locked or expired, or the directory is down), that belong to a unit with the password off and Cognito off.
> - **Verdict:** this is a spec-level conflict between SEC-002 and AC-10, not a T05 FAIL. It **needs user acceptance**: a Decision Log entry next to the timing entry. What leaks is that the switch is off in G, which step 3 already shows publicly. I could not count the affected accounts because this role has no database access.
>
> ISSUES:
> 1. **Discovered Issue:** A real failure goes through `Subject.login`. On failure, Shiro's `DefaultSecurityManager` calls `rememberMeFailedLogin`, then `CookieRememberMeManager.forgetIdentity`, which adds `Set-Cookie: rememberMe=deleteMe; Max-Age=0`.
>    - `MarloShiroConfiguration.java:111-116` builds a plain `DefaultWebSecurityManager`, which installs a `CookieRememberMeManager` by default. No code overrides it.
>    - `shiroFilter` covers `/*` ahead of struts2 (`web.xml:23`), so the subject is a web subject.
>    - Wrong passwords and unknown accounts (`UnknownAccountException`) both send the header. A refusal never calls `login`, so it never does.
>    - The result is a reliable header difference between "refused by the switch" and "wrong password or unknown account", on both endpoints.
>    - `RealCredentialPath` cannot see this: it installs a non-web `DefaultSecurityManager` (`RealCredentialPath.java:124`) and compares only messages.
>    - The existing SEC-005 guard has the same gap. Changing it is outside T05 (SEC-003), so it belongs in the bugfix spec.
>    - **Violated Rule:** `requirements.md` §3.2 SEC-002 ("indistinguishable, in response and message, from a wrong password"). The Decision Log entry (`requirements.md:356`) accepts timing only.
>    - **Remediation Suggestion:** Option (a), which fits inside T05: on refusal, call the configured security manager's `getRememberMeManager().onFailedLogin(subject, token, new IncorrectCredentialsException())`. This checks no credentials, so SEC-001 still holds. Then add a test on a `DefaultWebSecurityManager` with a mock response that compares `Set-Cookie` between a refusal and a real wrong password. Option (b): the user accepts the difference in the Decision Log, since it reveals the same public fact as the accepted timing difference.

- **Leader adjudication:** FAIL upheld. Checked at source: `MarloShiroConfiguration.securityManager` builds `new DefaultWebSecurityManager()` and sets only the realm and the session manager; no `RememberMeManager` override exists in `marlo-data` or `marlo-web`, so Shiro's default `CookieRememberMeManager` applies. Attempt 2 consumed.
- **runtime events:** none
- **spawns:** implementer (attempt 2, resumed worker) 31 calls, 480991 tokens (cumulative per host), ended fail (Reviewer FAIL); reviewer (security) 25 calls, 100439 tokens, ended complete; reviewer (reliability) 23 calls, 136335 tokens, ended complete

#### Not Done / Assumptions (Implementer attempt 2, verbatim)

> 1. **CGIAR account with no directory entry cannot be reproduced.** Telling it apart needs a directory search, and SEC-001/AC-10 forbid an LDAP call before a refusal. Such an account is answered like any other CGIAR account. Today the realm leaves the unmapped text `ADLoginMessages.ERROR_NO_SUCH_USER.getValue()` for it, so a real failure shows no message at all, the same as a local account. Please confirm this residual difference is accepted.
> 2. Your items 3 and 4 are untouched.

#### ADVISORY (attempt 2, 4R — recorded, not acted on)

- **Reliability lens, test validity:** `RealCredentialPath.directoryBind` returns `ERROR_LOGON_FAILURE`, the same value the gate hardcodes. The real value comes from `ADConexion`'s `con.getAuthenticationMessage()` (`LDAPAuthenticator.java:74`), so the CGIAR comparison partly compares the fixture with itself. It cannot be unit-tested around; the live walkthrough should compare a wrong CGIAR password with a refusal.
- **Reliability lens, resilience:** these CGIAR edges cannot be mirrored: AD answers that do not depend on the password (locked out, `ERROR_LDAP_CONNECTION`); a missing password parameter (`UserManagerImp.login:150` returns early, so there is no message today); an already-authenticated subject (`:173-176`). The SEC-005 guard shares all of them.
- **Both lenses, spec drift owed:** design §13 bullet 1 and DD-5 still say "the existing `ERROR_LOGON_FAILURE` text"; the §2 signature is now `passwordGate(String, Long)` → `PasswordGateDecision`; the §17 and `tasks.md` count for `LoginActionFinishLoginTest` is 17, not 18.
- **Reliability lens, test validity:** `nicknameFound=false` is never exercised; pin the residual with a test.
- **Reliability lens, reliability:** `PasswordGateScopeTest` paths are relative to `marlo-web/`.
- **Security lens, risk:** the LDAP stub's assumption about the wrong-AD-password message should be confirmed live.

## Budget Tripwire: T05

- **Delta:** 9 of 10 review rounds used with 4 of 8 tasks done. T05 attempt 3 needs at least 1 more round (2 in parallel lens mode), and T06, T07 and T08 need at least 3 more. Projection: **at least 13–15 rounds against a budget of 10**.
- **Cause:** parallel lens review on the security tasks T03 and T05 (2 verdicts each), plus two SEC-002 FAILs on T05. Each FAIL was a real response difference that the spec did not foresee: the per-kind realm message, then Shiro's remember-me cookie.
- **State:** T05 is `[~]` after 2 of 3 attempts, and the attempt-2 changes are uncommitted in the working tree. No HALT and no rollback: attempt 3 remains available.
- **Decisions needed from the user:**
  1. Whether to continue past the budget, and with what new ceiling.
  2. The rememberMe cookie: fix it inside T05 (option a) or accept it in the Decision Log (option b).
  3. Whether to accept the residual case (CGIAR account without a directory entry, or a directory state only a bind reveals) as a Decision Log entry, given the conflict between SEC-002 and AC-10.

**User decisions on the tripwire (2026-10-16):**
1. Continue past the budget. The review-round ceiling rises from **10 to 16**.
2. The rememberMe cookie is fixed inside T05, as **attempt 3**.
3. The CGIAR residual is accepted in the Decision Log and pinned with a `nicknameFound=false` test.

**Execute-time spec edits (Leader, 2026-10-16, after user approval):**
- `requirements.md` §10 Decision Log gains an entry "Execute-time decisions on T05" with three parts: the accepted SEC-002 residual, the remember-me cookie as part of SEC-002, and the bugfix-spec scope for `finishLogin` / the SEC-005 guard padding.
- `design.md` changes:
  - §2: the `isPasswordRefused(account, unitOrNull)` bullet becomes `passwordGate(identifier, unitIdOrNull)` → `PasswordGateDecision`.
  - §13 bullet 1: the per-kind response, the remember-me cookie, and the accepted residual.
  - DD-5: "the wrong-password response for the account's kind".
  - §17: `LoginActionFinishLoginTest` (18 → 17).
- `tasks.md` T05 Consumers: `LoginActionFinishLoginTest` (18 → 17).
- **Sweep:**
  - Forward: no other occurrence of `ERROR_LOGON_FAILURE` or `isPasswordRefused` in the spec folder outside `execution.md`.
  - Backward: no other document cites the changed bullets. `CHG-OTP-LOGIN-001` does not cite DD-5 or §13; its amendment is T08's.
  - The `git diff` of the two documents removes only the four intended lines.
- These sections are named conformance checks in the attempt-3 Reviewer brief and in the next task's.

#### Attempt 3

- **Feedback:** the attempt-2 security-lens FAIL above, verbatim.
- **Attempt History:**
  - Attempt 1 hard-coded `ERROR_LOGON_FAILURE`.
  - Attempt 2 fixed the per-kind message but skipped Shiro's failed-login handling, so no `rememberMe=deleteMe` cookie was sent. Do not repeat either.
- **Effort:** `xhigh` (user decision, kept).
- `design.md` §2 also names `leaveRememberMeFailure(...)` (execute-time edit during attempt 3, no change in meaning; Implementer assumption 1).

- **Files changed (attempt 3, on top of attempt 2):**
  - Main: `OtpEligibility.java` (`PasswordGateDecision.leaveRememberMeFailure(subject, securityManager, username, password)`: it finds the manager through `DefaultSecurityManager.getRememberMeManager()`, calls `onFailedLogin` with a `UsernamePasswordToken` and an `IncorrectCredentialsException` and checks no credential, clears the token in `finally`, does nothing when the manager is not a `DefaultSecurityManager` or is null, and contains a throwing manager with a WARN line), `LoginAction.java` and `ValidateUserAction.java` (each calls it right after `leaveRealmMessage`; the SEC-005 guard is untouched)
  - Tests: `OtpEligibilityTest`, `ValidateUserActionGuardTest`, `LoginActionCgiarGuardTest`, `PasswordGateScopeTest`, `RealCredentialPath` (`installWeb`: a real `DefaultWebSecurityManager` with a `DefaultWebSessionManager` and a `WebSubject` over a mock request and response). The full `Set-Cookie` list of a refusal is compared with a real wrong password, for local and CGIAR accounts, on both endpoints. The residual is pinned with `nicknameFound=false` on both endpoints.
- **Implementer verification:** removing `onFailedLogin` turns 4 tests red (`expected:<[JSESSIONID=<id>; ..., rememberMe=deleteMe; Path=/; Max-Age=0; ...]> but was:<[JSESSIONID=<id>; Path=/; HttpOnly; SameSite=lax]>`). The call was restored and `cmp` reports 0 differences. The wiped compile gate exits 0, the full suite runs 964 tests with 0 failures, and the Checkstyle delta is 0.
- **Evidence re-run (Leader-inline, non-author):** **VERIFIED**.
  - Command: wipe the classes, then `mvn -o -pl marlo-web -am test`. Result: `MVN_EXIT=0`, `Tests run: 964, Failures: 0`.
  - Per class: `ValidateUserActionGuardTest` 22, `LoginActionCgiarGuardTest` 20, `OtpEligibilityTest` 81, `PasswordGateScopeTest` 3.
  - Checkstyle: HEAD 0, tree 0, delta 0.
- **Reviewer verdicts (parallel lens mode):**
  - **Security lens: PASS.**
    - It traced what a failed `Subject.login` does in Shiro 1.13.0: it clears run-as identities (server-side only), notifies `AuthenticationListener`s (none exist in MARLO) and calls `RememberMeManager.onFailedLogin`, which the gate now makes. No custom `RememberMeManager` exists.
    - The token never reaches a realm or an authenticator, so there is no credential check or LDAP call (SEC-001, AC-10).
    - The WARN line carries no personal data.
    - Everything verified at attempt 2 still holds, and the code conforms to the named execute-time edits.
  - **Reliability + test-validity lens: PASS.**
    - The wrong-password side of the cookie comparison runs through the real `UserManagerImp.login`, then `WebSubject.login`, then Shiro's own failure path. Only the `JSESSIONID` value is masked, and the `deleteMe` count is asserted separately.
    - `ThreadContext`, the security manager and `ActionContext` are cleaned up after each test, and no scheduler thread starts.
    - The residual test asserts what production code answers.
    - No Tests item has been removed since attempt 2, and the named spec edits match the code.
- **runtime events:** none
- **spawns:** implementer (attempt 3, same worker resumed) 22 calls, 540151 tokens (cumulative for the host), ended complete; reviewer (security) 20 calls, 84889 tokens, ended complete; reviewer (reliability) 15 calls, 118870 tokens, ended complete

#### Not Done / Assumptions (Implementer attempt 3, verbatim)

> 1. Design §2 names `isRefused()` and `leaveRealmMessage`. The code adds `leaveRememberMeFailure`, so §2 needs a one-line update.
> 2. `OtpEligibility` now builds a `UsernamePasswordToken`. `PasswordGateScopeTest` lists it as a known builder and asserts the gate never calls `.login(`.
> 3. The test setup keeps Shiro's session-validation scheduler off, so no thread starts.

Leader adjudication: item 1 was applied as an execute-time edit (see above), and items 2 and 3 are assumptions only. No item names a blocker or an owed item, so the task can close.

#### ADVISORY (attempt 3, 4R — recorded, not acted on)

- **Security, risk (low): a request with no password parameter shows the refusal.**
  - `UserManagerImp.login:150` skips `Subject.login` when the password is `null`, so an allowed account gets no cookie and no session message.
  - A refused account always gets `deleteMe` and its kind's message.
  - Only a hand-built request can see this difference, since a browser always sends `""`. The fact it reveals is the one the Decision Log already accepts as public.
  - Cheap fix: skip the gate when the password is `null`. Candidate for the bugfix spec.
- **Security and reliability, risk (low): an already authenticated subject gets a different answer.**
  - `UserManagerImp.login:152-176` returns the current user without a check. The gate refuses first, so an authenticated session that posts the form for a password-off account gets a failure and `deleteMe` instead of "already logged in".
  - It is visible only to someone already signed in, and nobody is locked out. Check it in the T08 walkthrough.
- **Reliability:** the `JSESSIONID=[^;]*` mask would also hide a one-sided `JSESSIONID=deleteMe`.
- **Resilience:** `PasswordGateScopeTest` resolves its paths from the working directory, so it fails when run from the repo root in an IDE.
- **Readability:** the 10-line comment block is duplicated in both endpoints. A pointer to the `PasswordGateDecision` Javadoc would keep them from drifting.

#### Forward pointers

- **→ T08 (live walkthrough):** add these checks:
  - a wrong CGIAR (AD) password against a refusal, expecting the same text (the `ADConexion` message is only stubbed in tests);
  - a `Set-Cookie` comparison on the real stack;
  - the Spring wiring of the new constructor parameter (`login.do` and `validateUser.do` start and answer);
  - an already authenticated session posting the form;
  - the accepted CGIAR residual. `[advisory-grade]`
- **→ T08 (runbook):** a password-off unit whose `global_unit_type_id` is null cannot get a code (carried from T03). `[advisory-grade]`
- **→ bugfix spec (user decision, out of this spec):**
  - `finishLogin` checks `existCrpUser` and ignores whether the membership is active;
  - the SEC-005 guard compares the raw identifier;
  - the SEC-005 guard has the same remember-me cookie gap;
  - the null-password oracle;
  - the throttling of the `crpByEmail.do` failure ERROR (T04);
  - the dead `otpEnabledFor` method (T04).

- **Requirements covered:** SEC-001, SEC-002 (as amended; residual accepted), SEC-003, SEC-006, OPS-001; AC-10 and AC-12 at endpoint level. AC-10 and AC-12 are also checked live in T08.
- **Decisions made:**
  - The removed-membership gap was ruled not a Pivot and goes to the bugfix spec (user).
  - Effort stayed at `xhigh` on `sonnet` (user).
  - Budget tripwire: the ceiling went from 10 to 16 (user).
  - The cookie was fixed in T05 (user), and the CGIAR residual was accepted (user).
  - The execute-time spec edits are listed above: `requirements.md` §10; `design.md` §2, §13, DD-5 and §17; the `tasks.md` count.
- **Issues encountered:** two SEC-002 FAILs, each a real difference in the response that the spec had not foreseen (the message for each account kind, then the remember-me cookie).
- **Final verification:** wiped full suite 964/0, every falsifier red, Checkstyle delta 0. Re-run VERIFIED.
- **Continue gate:** user approved commit and continue to T06 (2026-10-16); committed `03d7e602af`.

### CHG-PWD-SWITCH-001-T06 — Step 3 views

- **Final status:** PASS on attempt 2 (attempt 1 FAIL: falsifier evidence)
- **Date:** 2026-10-17
- **Effort:** attempt 1 `medium`; attempt 2 `high` (one-level bump after a FAIL)
- **Skills:** `marlo-verify` and `frontend-design` from the task list, plus `claude-in-chrome` added by the Leader for the walkthrough
- **Environment:** the user authorised stopping their server. The stack runs on `aiccradb_otp_probe` (app pid 99447 on :8080, SMTP sink pid 53716 on :2525, `EMAIL_NOTIFICATION=support.probe@sink.test`, env in `scratchpad/env.sh`). It is left running for T07 and T08; T08 owns cleanup.

#### Attempt 1 — FAIL

- **Files changed:**
  - `marlo-web/src/main/webapp/global/js/login/login.js`
  - `marlo-web/src/main/webapp/WEB-INF/global/pages/loginForm.ftl` (FN-010 block)
  - `marlo-web/src/main/webapp/WEB-INF/global/views/login/login.ftl:4` and `marlo-web/src/main/webapp/WEB-INF/global/pages/error/401.ftl:4` (`?20261017`)
  - `marlo-web/src/main/resources/global.properties` (`login.otp.usernameOnly`)
  - `docs/specs/changes/password-login-switch/probes/output/t06-walkthrough.md`
- **runtime events:** provider/network death (`ENOTFOUND`) ×1. The Leader probed the tree (partial edits present, app and sink up) and recovered at **rung 3** (resume by message). No attempt was consumed.
- **continuations:** 1. The two design §5 COGNITO rows had been left unrun on the claim "no Cognito account". The Leader judged that claim cheap to test, and the rows were then walked with scratch `is_cgiar_user=1` on the copy, reverted afterwards.
- **Implementer verification:**
  - Walkthrough rows 1–16 PASS, with real clicks; rows 13–16 cover COGNITO with otp off and on, password on and off.
  - Red run: the HEAD `login.js` still showed the password on a password-off unit.
  - Falsifier: the `:523-524` read was declared unreachable, so a click-site falsifier at `:97` was substituted. It went red and was restored.
  - `cache-bust-check.sh` ok; `i18n-check.sh` no finding on the new line.
- **Evidence re-run (Leader-inline, non-author):** **VERIFIED**.
  - `login.ftl:4` and `401.ftl:4` are at `?20261017`. The two STALE hits in `loginForm.ftl` (:287, :312) are comment text.
  - The i18n FIX at `global.properties:6824` predates this change; the new line has no `'` and no `{`.
  - The served `target/cargo/.../login.js` is `cmp`-identical to `src`.
  - Live `crpByEmail.do` for user 3862 returns AICCRA `otpEnabled=true, passwordEnabled=false, cognitoEnabled=true` and AICCRA_III `otpEnabled=false, passwordEnabled=true`.
- **Reviewer verdict:** **FAIL** (verbatim):

> 1. **Discovered Issue:** `t06-walkthrough.md` §Falsifier says the `:523-524` read "is not reachable from the UI" because "`loadAvailableItems` / `secondForm` always click the card first". That is not true. In `loadAvailableItems`, the AICCRA auto-click (`login.js:751-752`) runs before the `$.each` that writes the card's `data-*` attributes (`:777-783`). On the first lookup in a fresh page, the click handler finds no attributes, so `selectedGlobalUnitId` stays `null` while the AICCRA card is still `.active` and visible. The reachable path: a user with more than one unit, AICCRA first in `data.crps`, and no `CRP` cookie (so the click at `:800` does not repeat after the attributes are written). That user presses "Log in" on the project step without clicking a card. `showPasswordStep()` then runs the `:523` branch, and that read is the only thing that picks up `passwordEnabled=false`. If it is removed, a password-off AICCRA unit shows the password. This is the main production unit, for a first-time user with two units. The Leader accepted the substituted falsifier on the strength of the unreachability claim, so the design-mandated second capture site has no behavioural evidence. The code at `:529` is correct as written.
>    * **Violated Rule:** `design.md` §5 ("read at **both** capture sites (`:96-97`, `:523-524`)"). The task's Falsifier line in `tasks.md` CHG-PWD-SWITCH-001-T06. The reviewer contract says a requirement resting on evidence that cannot evaluate it is a FAIL or a gap recorded with a correct reason, never a pass.
>    * **Remediation Suggestion:** No code change. Run the falsifier on the reachable path:
>      1. Use a fresh browser profile, or clear the `CRP` cookie.
>      2. Use fixture 3862 (or another member of 45 and 47). Confirm from `crpByEmail.do` that AICCRA comes first in `crps`; if it does not, pick a fixture where it does.
>      3. Type the email, then press "Log in" on the project step without clicking a card.
>      4. Serve the copy with `:529` removed and expect red (password shown on unit 45 with password off).
>      5. Restore it, confirm with `cmp`, and expect green (code only).
>
>      Record both runs. Replace the "cannot fail" paragraph in the walkthrough with this path. The click-site run at `:101` can stay as a second falsifier, but it does not replace this one.

- **Leader adjudication:** FAIL upheld. The Leader had accepted the substitution on the Implementer's unreachability claim without testing it, which is the same error the *Deferring a check* rule names. The code needs no change; the evidence must be redone. The live response confirms AICCRA comes first in `crps` for user 3862.
- **spawns:** implementer 115 + 150 calls, 234490 + 247360 tokens (attempt, continuation; cumulative for the host), ended died, then complete; reviewer 15 calls, 75879 tokens, ended complete

#### ADVISORY (Reviewer, 4R — recorded, not acted on)

- **Reliability:** because the auto-click at `:752` runs before the attribute loop at `:777`, a repeat lookup in the same page reads the previous lookup's stale `data-*`. This predates T06. It is a ticket candidate: move the auto-select click after the loop.
- **Readability:** the FN-010 arm's comment says "a username, or an email that is not one", while design §5 says "only possible for a username identifier". Align the two.
- **Accessibility:** `role="status"` sits on static text that is revealed by a class change, so screen readers may not announce it. Add `aria-describedby="login-method-username-info"` to the "Use a different email" button. Advisory only (`docs/ux-ui/design.md` §436-438).
- **Leader advisory-grade item met:** an email-shaped typo stops at step 1 with `emailNotFound`, so only a real username reaches the FN-010 view.
- **Old-page fallback:** simulated by removing the attribute from the DOM. Acceptable and disclosed.

#### Attempt 2

- **Feedback:** the FAIL above, verbatim.
- **Attempt History:** attempt 1 substituted the click-site falsifier on an untested claim that `:523-524` was unreachable. Do not repeat that.
- **Files changed (attempt 2):** `loginForm.ftl` gains `aria-describedby="login-method-username-info"` on the FN-010 "Use a different email" button, an advisory-grade item. The walkthrough Falsifier section is rewritten.
- **Implementer verification:** this ran on the reachable path: a member of units 45 and 47, AICCRA first in `crps`, no `CRP` cookie, a fresh page, and "Log in" pressed without a card click. Every step used real clicks.
  - F1 — `:529` removed: step 3 shows `gu 45`, `pwVar true`, `pwField 1`, and a choice menu with "Continue with your password" on a password-off unit. **RED.**
  - F2 — `:529` restored (`cmp` matches src): same path, now `pwVar false`, `pwField 0`, code only, `back false`. **GREEN.** This is also the positive row.
  - F3 — click-site `:101`, carried over from attempt 1: **RED.** It is kept as a second falsifier.
  - Gates: `cache-bust-check.sh` ok; `i18n-check.sh` reports no findings; the served `login.js` and `loginForm.ftl` match src under `cmp`.
- **Evidence re-run (Leader-inline, non-author):** **VERIFIED.**
  - Served `login.js` and `loginForm.ftl` are `cmp`-equal to src.
  - `login.ftl:4` and `401.ftl:4` carry `?20261017`.
  - The `loginForm.ftl` STALE hits are all comment lines that mention `login.js`: 14 at HEAD, 15 now, the extra one from the FN-010 block.
- **Reviewer verdict:** **PASS.**
  - The path matches the source: the `:751-752` auto-click runs before the `:777-783` loop, and `:800` is the only re-capture.
  - F1 and F2 read correctly against the control flow (`passwordEnabled` defaults to `true` at `:45` and resets at `:377`).
  - Clearing the cookie through `document.cookie` is test setup, so the disqualifier does not apply.
  - The false claim is withdrawn in the walkthrough.
  - The `aria-describedby` target id is unique.
  - Nothing from attempt 1 regressed.
- **runtime events:** none
- **spawns:** implementer 29 calls, 263018 tokens (cumulative per host), ended complete; reviewer 4 calls, 46397 tokens, ended complete

- **Requirements covered:** UI-001, UI-002, UI-004, UI-005, FN-010 (view); AC-3 and AC-15 in the live walkthrough (`probes/output/t06-walkthrough.md`, rows 1–16 plus F1–F3).
- **Decisions made:**
  - The Leader accepted the attempt-1 falsifier substitution without testing it. That was wrong, and attempt 2 reverses it.
  - `claude-in-chrome` was added as a skill.
  - No execute-time spec edits.
- **Issues encountered:**
  - The first `-am` build generated a bad `FundingSourceSummaryMapperImpl`. Wiping and rebuilding `marlo-data`, then `marlo-web`, fixed it. This is unrelated to T06.
  - The old-page fallback was simulated by removing the attribute from the DOM.
- **Scratch state on the probe copy (for T08 cleanup):**
  - user 3862 `username='probe.t06'`
  - `crp_users` row 5349 (user 3862, unit 47)
  - unit 45 `custom_parameters` for `system_otp_login_active=true` and `system_password_login_active=false`
  - `is_cgiar_user` restored to 0
- **Final verification:** walkthrough recorded, falsifiers red/green, cache-buster bumped, i18n gate clean. Re-run VERIFIED.
- **Continue gate:** waiting for the user (gated mode). The task's Review field calls for a visual check of the views at this HITL pause.
- **Continue gate outcome:** the user approved the commit and continuing to T07 (2026-10-17); committed as `33fa702aad`.

### CHG-PWD-SWITCH-001-T07 — Welcome email

- **Final status:** PASS on attempt 2 (attempt 1 FAIL: copy not compared with the mockup)
- **Date:** 2026-10-17
- **Effort:** attempt 1 `medium`, attempt 2 `high` (one level up after the FAIL)
- **Skills:** `marlo-verify` (as listed; no deviation)
- **Consumers (the task's grep, run by the Leader at the brief):** `CognitoCredentialLiteralScanTest` (3), `APCustomRealmDispatchTest` (8), `LoginActionFinishLoginTest` (17), `CenterManageUsersActionDirectoryTest` (5), `CrpUsersActionDirectoryTest` (9), `ManageUsersActionDirectoryTest` (5), `GuestUsersValidatorDirectoryTest` (4). All green.

#### Attempt 1 — FAIL

- **Files changed:**
  - new `marlo-web/src/main/java/org/cgiar/ccafs/marlo/utils/WelcomeSignInText.java`
  - the 7 senders (`CrpUsersAction`, `CrpProgamRegionsAction`, `CrpPpaPartnersAction`, `CrpSiteIntegrationAction`, `CrpAdminManagmentAction`, `ProjectPartnerAction`, `ClusterActivitiesAction`)
  - `global.properties`, `custom/aicrra.properties`, `custom/aiccra3.properties`, `custom/test.properties`
  - tests: new `WelcomeSignInTextTest` (9), new `CrpUsersActionWelcomeMailTest` (1), `CrpUsersActionDirectoryTest` (constructor)
  - renders: `probes/output/t07-welcome-aiccra.html`, `probes/output/t07-welcome-aiccra-password-on-control.html`
- **Implementer verification:**
  - Red run: test-compile exited 1 (`cannot find symbol class WelcomeSignInText`).
  - Falsifier 1: `ClusterActivitiesAction` back on the direct `getText` → grep finds 1 → restored to 0.
  - Falsifier 2: `CrpUsersAction` passes its local `globalUnit` instead of the `loggedCrp` parameter → `expected same:<CREATED> was not:<SELECTED>` → restored.
  - Full suite 974/0; Checkstyle 25/25, delta 0; i18n clean.
  - Render (sink): password off → no password row and a code-sign-in paragraph; password on → control. A password is still stored (FN-009).
- **Evidence re-run (Leader-inline, non-author):** **VERIFIED**
  - Wiped full suite: `MVN_EXIT=0`, 974/0. `WelcomeSignInTextTest` 9, `CrpUsersActionWelcomeMailTest` 1, `CrpUsersActionDirectoryTest` 9.
  - Grep gate: `direct=0 helper=1` in all 7 files.
  - Checkstyle: HEAD 25, tree 25, delta 0.
  - Placeholders: `part1.codeSignIn` matches `part1` in every file (global and test `{0}`–`{4}`; aicrra and aiccra3 `{0}`–`{3}`).
  - i18n: no FIX on the new lines.
- **Reviewer verdict:** **FAIL** (verbatim):

> 1. **Discovered Issue:** The task requires the AICCRA body "rendered once in the sink and compared with the approved mockup". The Implementer compared it only with the FN-008/UI-003 wording and wrote the copy themselves. The one committed record of the approved wording, `proposal.md:99`, says the block reads *"How to sign in: no password is needed…"*. The shipped copy in all four files says *"There is no password to remember. {3} signs you in with a one-time code…"*. The task's comparison was never done, and the only available evidence contradicts the copy.
>    - **Violated Rule:** `tasks.md` T07 Tests ("compared with the approved mockup", `tasks.md:180`); `proposal.md` §5 (`:99`); `requirements.md` §10 Decision Log 2026-10-16 (the welcome-email block is a user decision made after reviewing the rendered AICCRA email).
>    - **Remediation Suggestion:** Choose one:
>      - (a) Align the code-sign-in paragraph in `global`, `aicrra`, `aiccra3` and `test` with the proposal §5 lead-in ("How to sign in: no password is needed…"), re-render `t07-welcome-aiccra.html` from the sink, and keep the apostrophe and placeholder checks.
>      - (b) Have the Leader get the user's read of the current render against their local mockup and record that approval in `execution.md`.
>
>      Either way, T07 does not close until the user has read it.

- **Leader adjudication:** FAIL upheld. **User decision (2026-10-17): option (a)**, align with the mockup. The rest conforms: the unit per sender matches DD-7; the Cognito flag is read through the same `CognitoAuthSpecificity` resolver, which is not a fourth reader; the failure path falls back to today's text, which matches FN-008, DD-5 and DD-6.
- **runtime events:** none
- **spawns:** implementer 111 calls, 228263 tokens, ended fail (Reviewer FAIL); reviewer 21 calls, 98079 tokens, ended complete

#### Not Done / Assumptions (Implementer attempt 1, verbatim)

> - The approved mockup is not committed (proposal §8), so I compared against the FN-008 and UI-003 wording only. I wrote the code-sign-in copy myself, and it needs your read.
> - Only `CrpUsersAction` has a per-sender test. The other six are covered by the grep gate, which the task allowed.
> - The helper reads the Cognito flag through `CognitoAuthSpecificity` with `CustomParameterManager` and `ParameterManager`, because `OtpEligibility` has no public method for it. It does not use `OtpKeys` as DD-7 listed, since `methodsFor` already checks the key.
> - A fixed 6-digit numeric password is still generated and stored.
> - Mail goes to the support mailbox, so the sink's `To` header says `support.probe@sink.test`, not the new account.

- **Security note:** the Implementer echoed the local login credentials (`~/.marlo-local-credentials`) once in its own tool output. They do not appear in any report or artifact, but they remain in that subagent's local transcript. Reported to the user.

#### ADVISORY (Reviewer, 4R — recorded, not acted on)

- **Readability:** in `sendMailNewUser` the correct unit is the `loggedCrp` parameter, while a local `globalUnit` (`:996-998`) is the wrong one. A comment at `:1037` would prevent a future swap.
- **Reliability (falsifier):** the falsifier swapped in the local `globalUnit`, not the session unit the task names. `assertSame(created, …)` still rejects any other unit, so it stands as a sibling falsifier.
- **Reliability:** a unit with neither acronym nor name makes `{3}` print "null".
- **Risk (low):** the acronym is inserted unescaped, as the other arguments already are (super-admin data).
- **Readability:** the text comes from the session unit's custom bundle while the decision uses the unit passed in. This predates T07.
- **Spec drift:** DD-7 lists `OtpKeys`; the helper uses `CustomParameterManager` and `ParameterManager` instead.

#### Attempt 2

- **Feedback:** the FAIL above, verbatim, plus the user's choice of option (a).
- **Attempt History:** attempt 1 wrote its own copy without comparing it with the proposal §5 mockup lead-in. Do not repeat that.
- **Files changed (attempt 2):**
  - `global.properties`, `custom/aicrra.properties`, `custom/aiccra3.properties` and `custom/test.properties`: the code-sign-in paragraph now reads `<b>How to sign in</b>: no password is needed. Enter your email address at the link above and {3} will email you a one-time code each time you sign in.` The `global` and `test` files say "…there and…" instead of "at the link above".
  - `WelcomeSignInText.java`: `{3}` falls back from the acronym to the name, then to an empty string. This was an advisory-grade item.
  - `CrpUsersAction.java`: a comment at the `loggedCrp` use (advisory-grade).
  - `WelcomeSignInTextTest`: new test `aUnitWithNeitherAcronymNorNameNeverPrintsNull`.
  - Both `probes/output/t07-*.html` files were re-rendered from the sink.
- **Wording source:**
  - `proposal.md:99` is the only committed record, and it holds the lead-in only.
  - `requirements.md` (UI-003 and the Decision Log), `judgment.md`, `design.md` and `git log -p` were searched and hold nothing fuller.
  - So the continuation after the lead-in was written by the Implementer.
- **Implementer verification:** wiped full suite exit 0 with 975/0. `i18n-check.sh` exit 0. Checkstyle 25/25 (delta 0). Hygiene 0 FIX. Grep gate 0.
- **Evidence re-run (Leader-inline, non-author):** **VERIFIED.**
  - Wiped `mvn -o -pl marlo-web -am test`: `MVN_EXIT=0`, `Tests run: 975, Failures: 0`.
  - Grep gate: `direct=0 helper=1` in all 7 senders.
  - Placeholders of `part1.codeSignIn` match `part1` in every file.
  - Checkstyle delta 0.
  - The text extracted from the sent HTML reads "How to sign in: no password is needed. Enter your email address at the link above and AICCRA will email you a one-time code each time you sign in."
- **Reviewer verdict:** **PASS.**
  - The lead-in matches `proposal.md:99` word for word, and the old wording is gone from `marlo-web/src`.
  - The render is the email as captured at the sink.
  - Apostrophes and placeholders are correct.
  - The `unitLabel` fallback cannot throw, and the comment is accurate.
  - Nothing regressed.
- **ADVISORY:** reliability (low). A unit with neither acronym nor name renders "…and  will email you…". A neutral fallback word such as "MARLO" could replace `""` later.
- **runtime events:** none
- **spawns:** implementer: 122 calls, 240268 tokens (cumulative per host), ended complete. Reviewer: 8 calls, 71890 tokens, ended complete.
- **Environment:** app pid 37656 on :8080, sink pid 53716 on :2525, unit 45 with otp true and password false.
- **Scratch data for T08 cleanup:** users 3865–3868 on `aiccradb_otp_probe`, with their `crp_users` and guest `user_roles` rows. The sink mbox holds 9 messages. The temporary CRP-Admin role for `marlosadmin` has been removed.
- **Requirements covered:** FN-008, FN-009 and UI-003. AC-13 is covered by the sink render.
- **Decisions made:**
  - The user chose option (a), aligning the copy with the mockup.
  - The helper reads the Cognito flag through `CognitoAuthSpecificity` and does not use `OtpKeys`, which deviates from DD-7's dependency list. The Reviewer accepted this.
- **Closure:** pending the user's read of the copy. The attempt-1 FAIL requires it, so `tasks.md` stays `[~]` until the user approves.
- **User read (2026-10-17):** the user approved the code-sign-in copy as rendered ("si") and approved the commit. T07 is closed.

#### T07 reopened (2026-10-17, user decision)

- **Cause:** the user compared the shipped copy with the approved mockup they keep locally (`~/Downloads/marlo-welcome-email-proposal.png`, proposal §8, not committed). It differs in two ways.
  - **Wording.** The mockup reads: "**How to sign in**: no password is needed. Enter your email, choose AICCRA and select **Email me a sign-in code**. We will send a one-time code to this address; it is valid for 5 minutes." The shipped copy uses different wording.
  - **Placement.** In the mockup the line sits inside the *Sign in at* / *Email* block. The shipped copy puts it in a separate paragraph.
- **User decisions:**
  - Align the copy verbatim with the mockup.
  - Lock "5 minutes" to `OtpRequestCodeAction.CODE_VALIDITY` with a test.
  - Raise the review-round ceiling from **16 to 18**.
- **Status:** `tasks.md` T07 goes back to `[~]`; attempt 3 follows. This is a user-requested change, not a Reviewer FAIL, so effort stays `high`.

#### Attempt 3

- **Files changed:**
  - `email.newUser.part1.codeSignIn` in all four files now follows the mockup verbatim. In `aicrra` and `aiccra3` the "How to sign in" line sits in the same `<p>` as Sign in at / Email, after a `<br>`. In `global` and `test` it sits one `<br>` below the Email line. `{3}` = acronym and **Email me a sign-in code** is bold.
  - New `marlo-web/src/test/java/org/cgiar/ccafs/marlo/action/home/WelcomeEmailCodeValidityTest.java` checks that "valid for N minutes" in all four files equals `OtpRequestCodeAction.CODE_VALIDITY.toMinutes()`.
  - Both `probes/output/t07-*.html` files were re-rendered from the sink.
- **Implementer verification:**
  - Falsifier: with `CODE_VALIDITY` set to 6, the test fails with `AssertionError: /global.properties: the welcome copy and the real expiry cannot drift expected:<6> but was:<5>`. The value was restored afterwards.
  - Wiped full suite exits 0, with 976 tests and 0 failures. `i18n-check.sh` exits 0. Checkstyle delta 0. Grep gate 0.
- **Evidence re-run (Leader-inline, non-author):** **VERIFIED**.
  - Wiped `mvn -o -pl marlo-web -am test` gives `MVN_EXIT=0`, 976 tests and 0 failures. `WelcomeEmailCodeValidityTest` runs 1 test with 0 failures.
  - Placeholders match `part1` in each file.
  - Text extracted from the sent HTML: "Sign in at: … | Email: … | How to sign in: no password is needed. Enter your email, choose AICCRA and select Email me a sign-in code. We will send a one-time code to this address; it is valid for 5 minutes."
  - Checkstyle `--baseline` on the new test gives delta 0. The Leader ran it after the Reviewer pointed out that the brief had left it out.
- **Reviewer verdict:** **PASS**. The copy matches the mockup word for word, including the bold spans and the placement. The button label equals `login.otp.button` / `login.otp.method.code`. The lock test reads the shipped classpath files (there is no `src/test/resources` that could shadow them) and checks them against the constant that sets the expiry (`OtpRequestCodeAction.java:412`). Attempts 1–2 did not regress.
- **ADVISORY:**
  - Reliability: a missing resource produces a bare NPE in the lock test; an `assertNotNull` would name the file.
  - Readability: `:` vs `\:` differs between the files, but each file is consistent with its own lines.
- **runtime events:** none
- **spawns:** implementer: 131 calls, 255483 tokens (cumulative per host), ended complete. Reviewer: 10 calls, 62644 tokens, ended complete.
- **Environment:**
  - app pid 12735 on :8080; sink pid 53716 on :2525 (13 messages)
  - unit 45: otp true, password false
- **Scratch data:** users 3865–3870 on `aiccradb_otp_probe`
- **Final status (T07):** PASS on attempt 3. The copy is aligned with the user's approved mockup.
- **Continue gate:** waiting for the user (gated mode).
- **Continue gate outcome:** the user approved continuing to T08 (2026-10-17); T07 committed as `9b2448578d`.

---

### CHG-PWD-SWITCH-001-T08 — Cross-spec amendments, runbook, end-to-end evidence

- **Status:** in progress `[~]`. Attempt 1 reported `Not Done / Assumptions` with a blocker, so the task is escalated to the user (gated mode).
- **Date:** 2026-10-17
- **Effort:** attempt 1 `high` (cross-spec security text plus live probes)
- **Skills:** `marlo-verify` and `cognitive-doc-design`, as the task lists them
- **Lens mode:** the plan is one Reviewer, not parallel lenses. T08 changes no production code, and 16 of 18 review rounds are already used. Leader decision, recorded here.

#### Attempt 1

- **Files changed:**
  - `center-email-otp-login/requirements.md`:
    - Amendment notes on FN-001, FN-003, FN-005, FN-006, FN-015, SEC-009, DA-002 and UI-006.
    - The backward sweep also put notes on FN-019, UI-001, the "Eligible user" glossary entry, the "Local password users" persona row and AC-1.
    - DA-002 carries the DA-003 texts.
    - The Executive Summary is corrected.
    - New Decision Log entry for 2026-10-17, with the sweeps.
  - `center-email-otp-login/design.md`, `runbook.md` and `probes/README.md`: notes on the sentences that stated the old rule.
  - `center-email-otp-login/probes/output/`: `ac21-migration.pwd-switch-rerun*.out`.
  - New files in this folder: `runbook.md`, `probes/README.md`, `probes/ac-live.sh`, `probes/fixtures.sh`, `probes/sweeps.sh`, `probes/tools/` and 18 files under `probes/output/`.
  - `git diff -- marlo-data marlo-web marlo-utils` is empty. The Leader checked it.
- **Implementer verification** (all on `aiccradb_otp_probe`):

  | Check | Result |
  |---|---|
  | AC-2 `ac-probe.sh`, 10 stages, switch on | 68/70. The re-run of the 2 failed checks against a sink that writes the envelope in the format the probe reads gave 26/26 |
  | `ac21-migration.sh` re-run (T01 forward pointer) | 0 failed |
  | AC-10 | 23/23 |
  | AC-8 | 8/8 |
  | AC-7 | 11/11, with one ERROR per unit |
  | Override (AC-3 to AC-6) | 10/10 |
  | Runbook SQL | 5/5 |
  | AC-12 | CLARISA PASS. `/api/**` answers the same with the switch on and off. That `/api/**` authenticates is **NOT VERIFIED** |

- **Falsifier:** `probes/ac-live.sh ac8` with `setCacheable(true)` in `OtpChallengeMySQLDAO.findSpecificityValues` gave `8 checks, 5 failed` (`ac8.FALSIFIER-red.out`). After the revert and a clean rebuild it gave 8/8. A first falsifier through `CustomParameterManager` stayed green, because the cached query holds only entity ids. That output is recorded and is not counted as the red run.
- **Not Done / Assumptions (Implementer attempt 1, verbatim):**
  1. `/api/**` Basic authenticating (AC-12) is NOT VERIFIED. On this stack the correct password gets 401 with the switch on or off. The realm path fails with `HibernateException: Could not obtain transaction-synchronized Session`, in code this spec did not touch. It needs one check on an environment where `/api/**` authenticates.
  2. New risk, not decided: the code's per-email rate limit can now lock a user of a password-off unit out completely, where the password used to be a fallback. It is recorded in both runbooks and in two rows of the OTP `design.md`.
  3. Advisory items. Done: Set-Cookie comparison, Spring wiring of the new constructor (both endpoints answered), an authenticated session posting the form, and the null `global_unit_type_id` NPE (cannot occur, the column is NOT NULL). Not reachable: a wrong CGIAR password against a real Active Directory refusal. AD does not answer offline, but the accepted residual shows as expected.
  4. Not run: `ac-probe.sh emaillog`, `postlogin`, `smtp`, `blackhole`, `tz` and `nosecret`. They need extra fixtures or restarts and cover code this spec did not touch.
  5. Surviving siblings outside T08's files, not edited: `OtpDomainPolicy.java` lines 28 and 35 say CGIAR "plays no part (SEC-009)"; some historical `login.js` comments cite T14, T17 and T21.
  6. `marlo-dev.properties` hash: `51a818…` before the first build and after the last run. It differs from the Leader's earlier `dev.props.sha` (`955fa7…`); the run script rewrote it at some point before the Implementer started.
  7. One flaky build: the first clean build failed with `cannot access APConfig` in marlo-web test-compile and passed unchanged on retry.
  8. Sink swap: the Implementer replaced the Leader's SMTP sink with `tools/sink.py` mid-run.
- **Evidence re-run (Leader-inline, non-author):**
  - `probes/sweeps.sh` exits 0. Its output equals the recorded `sweeps.out` except for 10 backward-sweep lines that match `sweeps.out` itself, because the file now holds the clause ids. These are self-references, not new hits.
  - The probe summaries were read from `probes/output/`.
  - The live probes **could not be re-run**: the brief put cleanup (drop of `aiccradb_otp_probe`, app and sink stopped) inside the Implementer's attempt, before the re-run. That ordering is a Leader brief error; any re-brief keeps the environment until the re-run is done.
  - Result: VERIFIED for the deterministic sweep. The live probes rest on their recorded outputs only.
- **Reviewer:** not spawned yet. The task waits on the user's decision about the blocker.
- **runtime events:** none
- **spawns:** implementer: 173 calls, 449566 tokens, ended partial.
- **Environment after attempt 1:** app and sink stopped, `aiccradb_otp_probe` dropped, scratch credentials deleted.
- **User decisions (2026-10-17, on the blocker):**
  - **AC-12:** accepted as recorded. The CLARISA filter passes, and `/api/**` answers the same with the switch on and off. Whether `/api/**` authenticates is not verifiable on the local stack, because of a realm/session `HibernateException` this spec did not cause; it goes to a separate issue, alongside the API-authentication review already listed in design §16.
  - **Rate-limit lock-out risk:** record only. It stays documented in both runbooks and in the OTP `design.md`, and no spec edit is made. Whether it earns a proposal is decided later.
  - With both decisions the `Not Done` field holds no owed item and no blocker, so the task goes to the Reviewer.
- **Reviewer verdict (attempt 1):** **FAIL**, review round 17 of 18.
  - Every other check passed:
    - The eight DD-8 notes are present.
    - The DA-002 texts are byte-equal to DA-003 and to the shipped migration.
    - The Executive Summary is corrected, and the Decision Log records both sweeps.
    - The runbook covers OPS-003 and quotes the §11 lines exactly.
    - Every probe output names `aiccradb_otp_probe` or `pwd_probe_t01_*`, and each supports its AC claim.
    - The diff holds no production code.
  - **Issue 1 (verbatim summary):** several notes drop the "under the override" qualifier. They say that while password sign-in is off, a Cognito account in G is refused the code, with no condition. The places are FN-005, SEC-009, FN-015, the "Eligible user" glossary entry, the last Executive Summary bullet, and the design.md step 6 and step 8 notes.
  - **Violated rule:** design §1, "The rule": a Cognito account keeps the code when the code switch is on and its domain is not excluded. FN-005 says the same, and so does DD-8 ("refused under the override"). `OtpEligibility.java` lines 76–77 and 341 agree with the rule.
  - **Remediation:** a text-only fix in the seven places. Split FN-015, and reword the SEC-009 "every other account is eligible" clause.
- **ADVISORY (Reviewer, 4R; recorded, not acted on):**
  - The UI-006 note's "the single method is the code" is true only for an account without Cognito.
  - The center runbook's "Support may say … its domain is enabled" is left over from the allow-list.
  - Runbook §5 is missing the DD-6 `crpByEmail.do` settings ERROR and `welcome.email outcome=settings_unavailable`.
  - The siblings in `OtpDomainPolicy.java` lines 28 and 35 and in `login.js` need an owner in a follow-up task that may change source.

#### Attempt 2

- **Effort:** `xhigh`, one level up after the FAIL.
- **Files changed (attempt 2, text only):**
  - `center-email-otp-login/requirements.md`:
    - Executive Summary, the "Eligible user" glossary entry, FN-005, FN-015 (split in two), SEC-009, the OQ-4 note and the UI-006 note (advisory).
    - A correction line in the 2026-10-17 Decision Log entry.
  - `center-email-otp-login/design.md`: the step 4, struck-through Cognito check and step 8 notes, plus the `is_cgiar_user` bullet, which the family grep found.
  - `center-email-otp-login/runbook.md`: the A5 row, the pending TRD 8.1 text, and the support-sentence note (advisory).
  - `password-login-switch/runbook.md` §5: the `welcome.email outcome=settings_unavailable` ERROR row (advisory).
- **Implementer verification:**
  - `CODE_DIFF_EMPTY`.
  - A family grep (KZ-directory-abstraction-1) was run before and after the edit. The six lines that still match are each judged qualified, a note, or unrelated.
  - The DD-6 `crpByEmail.do` settings ERROR was not added. `CrpByUserEmailAction` has no LOG call, so the design wording is probably imprecise. This advisory was not done, and the spec was not edited.
- **Evidence re-run (Leader-inline):** **VERIFIED**.
  - `CODE_DIFF_EMPTY`.
  - The same family grep returns the same six lines.
  - `sweeps.sh` exits 0, and the `[CHECK]` lines are identical apart from line numbers (21 before, 21 after).
  - The recorded `sweeps.out` keeps its attempt-1 line numbers.
- **Reviewer verdict (attempt 2):** **FAIL**, review round 18 of 18.
  - The seven places from attempt 1 are now correct, and the six remaining grep hits are fine.
  - The FN-001, FN-003 and UI-001 notes, the runbook and the TRD 8.1 text agree with each other.
  - The new §5 line matches `WelcomeSignInText.java:115` character for character, and the diff has no production code.
  - **Issue 1:** two added lines that the family grep's wording missed still state the refusal outright:
    - the opening sentence of the 2026-10-17 Decision Log entry in `center-email-otp-login/requirements.md` ("…an account that uses Sign in with CGIAR in that unit is refused the code.");
    - the support row in `password-login-switch/runbook.md` §6 ("cannot use the code" → "Correct: such accounts keep Sign in with CGIAR"), which also contradicts §1 row 2 of the same runbook.
  - **Violated rule:** design §1, the "Code" row, and `.agents/reviewer.md` §3, "Correction-note sweep".
  - **Remediation:** reword both lines, then widen the sweep to `Sign in with CGIAR.{0,60}refused` and `cannot use the code`.
- **ADVISORY (Reviewer):** DD-8 of this spec's `design.md` reads "a Cognito account is refused under the override". That wording is the source of this defect class. It is outside T08's diff and does not gate the task; record it as a pending note.
- **Budget tripwire:** with 18 of 18 review rounds used and one rework attempt left, attempt 3 would need round 19. Execution stops and escalates to the user, per Step 2.4.
- **User decision (2026-10-17, tripwire):** raise the review-round ceiling from 18 to 19 and run attempt 3. If it fails, the task HALTs.

#### Attempt 3

- **Effort:** `xhigh` on the T2 Implementer. The bump would be `max`, but the dial rule "never `max` a cheaper tier" caps it at `xhigh`. The added work is a sweep over every added line, driven by the diff rather than by a grep pattern.
- **Files changed (attempt 3, text only):**
  - `center-email-otp-login/requirements.md`:
    - The opening sentence of the 2026-10-17 Decision Log entry.
    - The correction line now carries the widened patterns and the read-every-line method.
  - `password-login-switch/runbook.md`:
    - The §6 support row (cause and answer).
    - The §2 production-count line, which was ambiguous and was found by the line review.
  - New: `probes/output/t08-cognito-line-review.out`, with 66 lines reviewed (63 ok, 3 fixed).
- **Implementer verification:**
  - `CODE_DIFF_EMPTY`.
  - Every added line mentioning Cognito, CGIAR or "Sign in with" was read against design §1.
  - The widened regex gave 7 hits, all judged.
- **Evidence re-run (Leader-inline):** **VERIFIED**.
  - `CODE_DIFF_EMPTY`.
  - The line counts match: 45 added lines in `center-email-otp-login` and 21 in this folder's runbook and README, 66 in total.
  - The widened regex over the same files returns 10 lines, against the Implementer's 7. The extra hits are `center-email-otp-login/probes/README.md:84` (T21 history), FN-005 at `requirements.md:123` (the qualified text from attempt 2) and `requirements.md:464` (the Decision Log). All three are benign, and all three are in the review file's line set.
- **Reviewer verdict (attempt 3):** **PASS**, review round 19 of 19.
  - Both attempt-2 lines now match the design §1 "Code" row, and the extra §2 fix is correct.
  - The Reviewer re-read every added Cognito/CGIAR sentence by meaning. None refuses the code outright, and none lets the override admit a Cognito account.
  - The 3 extra regex hits are historical or quoted text.
  - No contradiction was introduced, and no production code changed.
- **ADVISORY (Reviewer; recorded, not acted on):**
  - Readability: `password-login-switch/runbook.md` §1 row 2 ("Cognito, plus the code under the code switch") leaves out "and the exclusion".
  - Readability: the 2026-10-17 note on the `crpByEmail.do` row in the center `design.md` could add "for an account without Cognito".
- **runtime events:** none on any attempt
- **spawns:**
  - implementer attempt 1: 173 calls, 449566 tokens, ended partial
  - reviewer attempt 1: 19 calls, 118229 tokens, ended complete
  - implementer attempt 2: 19 calls, 93651 tokens, ended complete
  - reviewer attempt 2: 6 calls, 73126 tokens, ended complete
  - implementer attempt 3: 11 calls, 94507 tokens, ended complete
  - reviewer attempt 3: 11 calls, 75713 tokens, ended complete
- **Requirements covered:**
  - OPS-003 (runbook).
  - OPS-001 and OPS-002 (live log lines).
  - FN-002 and AC-8 (live change, with the falsifier red then green).
  - FN-006 and AC-7 (last resort, one ERROR per unit).
  - AC-2 (`ac-probe.sh` with the switch on) and AC-10 (both endpoints refuse with the wrong-password text).
  - SEC-006 and AC-12: CLARISA passes, and `/api/**` answers the same with the switch on and off. Whether it authenticates is not verifiable locally (accepted by the user).
  - SEC-007: recorded, no code.
  - DD-8 amendments with forward and backward sweeps.
- **Decisions made:**
  - The user settled AC-12 and the rate-limit risk.
  - The user raised the ceiling to 19.
  - The Leader chose one Reviewer instead of parallel lenses.
  - The Leader capped effort at `xhigh` on attempt 3.
  - No execute-time spec edits.
- **Pending items (outside T08, no owner in this spec):**
  - DD-8 wording in this spec's `design.md` ("a Cognito account is refused under the override") is the source of the defect class. It could say "not admitted by the override; keeps the code switch and the exclusion as written".
  - The surviving source siblings: `OtpDomainPolicy.java` lines 28 and 35 ("plays no part (SEC-009)") and historical `login.js` comments.
  - `/api/**` Basic returns 401 locally because of `HibernateException` in `APCustomRealm` → `getUserByEmail`; this needs a separate issue.
  - Rate-limit lock-out with the password off: record only.
  - The "DD-6 crpByEmail.do settings ERROR" in the design has no matching log call in `CrpByUserEmailAction`; the design wording is probably imprecise.
- **Issues encountered:**
  - The Leader brief placed environment cleanup inside attempt 1, so the live probes could not be re-run by a non-author. They rest on their recorded outputs.
  - Two Reviewer FAILs came from one defect class: the override qualifier was dropped. A regex sweep missed siblings that only a read by meaning found.
- **Final verification:**
  - Sweeps were recorded (`probes/output/sweeps.out`, `t08-cognito-line-review.out`).
  - The live probes are green and recorded under `probes/output/`; `ac21-migration.sh` re-ran green.
  - The environment is cleaned.
  - `CODE_DIFF_EMPTY`.
- **Final status (T08):** PASS on attempt 3.
- **Continue gate:** waiting for the user (gated mode). This is the last task of the spec.
