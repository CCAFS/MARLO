# Validation Report — Password Login Switch

**Verdict: PASS. No FAIL.** WARN-1 to WARN-4 were fixed on 2026-10-18 (§11). WARN-5 is a process note for kaizen. The implementation does what the spec asks.

- Gates: clean compile, all 983 tests green, Checkstyle delta 0, i18n clean.
- Every requirement is owned by a task and has evidence. There are no product bugs.

The five warnings are documentation drift and process notes, not product defects. The spec is ready to archive.

## 1. Document Control

| Field | Value |
|---|---|
| Spec ID | `CHG-PWD-SWITCH-001` |
| Spec path | `docs/specs/changes/password-login-switch` |
| Branch / HEAD validated | `A2-2631-Implement-Center-Email-OTP-Authentication` / `9835384963` |
| Spec baseline | `5f11bc37e2` (the spec commit). Every diff below is `5f11bc37e2..HEAD` |
| Date | 2026-10-18 |
| Validator | Claude Code session, `opus` (T3). Implementers were `sonnet`, so author ≠ auditor holds |
| Inputs | `proposal.md`, `requirements.md`, `design.md`, `tasks.md`, `execution.md` (Document Control, budget table, T08 entry, advisories), `test-report.md`, `runbook.md` |
| Evidence reuse | The `test-report.md` matrix is cross-checked against `requirements.md`, not re-derived. Build gates were re-run here |

## 2. Summary

| Area | Result | Note |
|---|---|---|
| Task completion | PASS | 8 of 8 `[x]`, each closed with a Reviewer PASS |
| File existence | PASS | Every file in design §2 exists and changed as described |
| Build integrity | PASS | Compile 2415 / 1066 / 88 files; 983 tests, 0 failures; Checkstyle 0 → 0 on 42 files; i18n clean |
| Requirement coverage | PASS | 30 of 30 requirements traced. AC-12 `/api/**` is N/A by user decision (WARN-1) |
| Code quality (4R) | PASS | Advisory notes only (§7) |
| Design conformance | PASS, 4 WARN | Doc drift: WARN-1 to WARN-4. Budget overrun: WARN-5 |
| Test evidence | PASS | Matches `test-report.md`. Gaps are accepted and documented |
| Constitution impact | PASS | No `## Constitution Impact` entries; no shared file edited |

## 3. Task Completion

| Task | Status | Closing record | Attempts | Verification evidence in `execution.md` |
|---|---|---|---|---|
| T01 Constants and migration | PASS | Reviewer PASS | 1 | Migration gate twice, plus databases without units 45/47 and without types; byte comparison; falsifiers red |
| T02 The method rule | PASS | Reviewer PASS | 1 | Exhaustive enumeration; red run on the T21 stub |
| T03 Code endpoints | PASS | Two lens PASSes | 1 | Full suite with `-am`; falsifiers red |
| T04 Pre-auth lookup | PASS | Reviewer PASS (checklist) | 1 | One-query tests; once-per-unit ERROR |
| T05 Password gate | PASS | Both lenses PASS | 3 | Budget tripwire; ceiling raised to 16 by the user |
| T06 Step 3 views | PASS | Reviewer PASS | 2 | Browser walkthrough `probes/output/t06-walkthrough.md`; user visual check |
| T07 Welcome email | PASS | Reviewer PASS | 3 (one reopen) | Rendered AICCRA body compared with the approved mockup; user read the copy |
| T08 Amendments, runbook, live | PASS | Reviewer PASS | 3 | Budget tripwire; ceiling raised to 19 by the user |

`tasks.md` → "Definition of Done" still shows its 5 boxes unticked (WARN-4).

## 4. File Existence

All files named in design §2 and tasks "Files" are present in `5f11bc37e2..HEAD`:

| Group | Files | Result |
|---|---|---|
| New Java | `security/otp/SignInMethodRule.java`, `utils/WelcomeSignInText.java` (GPL header present) | PASS |
| Changed Java | `OtpEligibility`, `OtpRequestCodeAction`, `OtpVerifyCodeAction` (constructor only), `CrpByUserEmailAction`, `ValidateUserAction`, `LoginAction`, `CognitoCallbackAction` (constructor), both `APConstants`, the 7 senders | PASS |
| Also changed (not in §2) | `security/otp/OtpLog.java` (+26 lines, log-line helpers) | PASS. Recorded in `execution.md`; within the scope of OPS-001 and OPS-002 |
| Migration | `V2_6_0_20261016_1525__AddPasswordLoginSwitch.sql` | PASS. DML only, `NOT EXISTS`, no `global_unit_id` |
| Webapp | `login.js`, `loginForm.ftl`, `login.ftl:4` and `401.ftl:4` (both `?20261017`) | PASS |
| Resources | `email.newUser.part1.codeSignIn` in `global`, `custom/aicrra`, `custom/aiccra3`, `custom/test`; `login.otp.usernameOnly` in `global` | PASS |
| Docs | `runbook.md` (new); `center-email-otp-login/{requirements,design,runbook}.md` and `probes/README.md` amended | PASS |
| Not edited (as required) | `V2_6_0_20261009_1025`, `V2_6_0_20261015_1418`, `ClarisaPublicAccesFilter`, `MarloShiroConfiguration`, `ValidSessionCrpInterceptor` | PASS |

## 5. Build Integrity

All gates were re-run here at HEAD `9835384963`, with no other build or server running.

| Gate | Command | Result |
|---|---|---|
| Compile + unit tests | `rm -rf marlo-web/target/classes marlo-web/target/test-classes marlo-data/target/classes && mvn -o -pl marlo-web -am test` | BUILD SUCCESS, exit 0. Compiled 2415 (marlo-data) / 1066 (marlo-web) / 88 (tests) files. **983 run, 0 failures, 0 errors, 0 skipped** |
| Checkstyle | checkstyle 8.18 run directly with `configuration/marlo-checkstyle.xml` on the 42 added or modified `.java` files, against the same files at `5f11bc37e2` | 0 → 0, delta 0. Sanity check: `BaseAction.java` reports its known 9 |
| i18n | Read of every added `.properties` line | PASS. The apostrophe is doubled (`project''s`), and `{0}`–`{4}` are the only braces. `WelcomeCodeSignInCopyTest` guards the rendering |
| Environment boot | Not re-run. Two independent live runs are on record (T08 and `/akili-test` Suite 2), and the probe database is dropped | n/a |

## 6. Requirement Coverage

The coverage evidence is the `test-report.md` §7 matrix. Each row was checked against the clause text of `requirements.md`.
**Test** means a test whose body was read in `/akili-test`.

| Requirement | Task(s) | Code evidence | Test evidence | Result |
|---|---|---|---|---|
| FN-001 default on, missing reads on | T01, T02 | `SignInMethodRule.isPasswordSwitchOn`; seed in the migration | `SignInMethodRuleTest#ac1…` (×3); live `ac1-ac14-migration` | PASS |
| FN-002 live change | T03, T08 | Uncached key list (`OtpEligibility`) | Unit `#ac8…`; live AC-8 8/0, falsifier B red 3/8 | PASS |
| FN-003 switch on = T21 | T03, T08 | Zero Cognito reads on password-on units | Unit zero-call tests; live `ac-probe.sh` 70/0 | PASS |
| FN-004 code only, even code off / excluded | T02, T03, T06 | `SignInMethodRule` rule 3d | AC-3/4/5 unit; live override 10/0; T06 walkthrough | PASS |
| FN-005 / SEC-003 Cognito unchanged; BUT no code under the override | T02, T03, T05 | `isEligible` refusal after `existActiveCrpUser` | `#ac6…` at rule, eligibility, request and verify level | PASS |
| FN-006 last resort + ERROR | T02, T04, T05, T08 | `methodsFor` → `logLastResortOnce` | Unit OPS-002 ×3; live AC-7 11/0 | PASS |
| FN-007 no account without a method | T02 | Rule | Exhaustive enumeration, 6 inputs | PASS |
| FN-010 username → use your email; MUST NOT show password | T02, T06 | `needsEmail`; `loginForm.ftl` block; `login.otp.usernameOnly` | Unit `#fn010…`; T06 walkthrough | PASS |
| FN-008 welcome email; unchanged otherwise | T07 | `WelcomeSignInText.part1` | `WelcomeSignInTextTest` (6), `WelcomeCodeSignInCopyTest`, static lock on all 7 senders, live `CrpUsersAction` | PASS |
| FN-009 password still stored | T07 | Senders' storage untouched (diff) | Live `suite2-fn009.out` 14/0 | PASS |
| SEC-001 refuse before credentials and LDAP (fail-open exception) | T05 | Gate before `userManager.login` in `ValidateUserAction` and `LoginAction` (read in the diff) | Unit "no credential call" doubles; `PasswordGateScopeTest`; live AC-10 23/0 | PASS |
| SEC-002 same as a wrong password | T05 | `leaveRealmMessage`, `leaveRememberMeFailure` | Byte-equal body, status and `Set-Cookie`, live and unit | PASS |
| SEC-004 code endpoints neutral | T03 | Cognito read before the lookup on password-off units | Neutrality unit tests (queries + rows) | PASS |
| SEC-005 no new disclosure | T04 | `passwordEnabled` is a function of the unit settings | `#everyPreExistingKeyAndValueIsUnchangedOnAndOff`, `#ac11…` (added in test) | PASS |
| SEC-006 CLARISA and `/api/**` untouched | T05, T08 | Files not touched (static test) | CLARISA live PASS; `/api/**` **N/A** (user, 2026-10-18) | PASS (see WARN-1) |
| SEC-007 session switch unchanged | T08 | `ValidSessionCrpInterceptor` untouched | Static | PASS (accepted risk) |
| OPS-001 INFO line | T05 | `auth.password.refused gate=password_switch gu=…` | Unit asserts one line, no email or password; live | PASS |
| OPS-002 ERROR once per unit per start, not repeatable | T03, T04, T08 | `lastResortLogged` set; driven only by DB-loaded units | Unit ×5; live | PASS |
| OPS-003 runbook | T08 | `runbook.md` §2–§5 | Reviewer PASS; runbook SQL 5/0 live | PASS (see WARN-3) |
| DA-001 migration, constants in both files | T01 | Migration; `PASSWORD_LOGIN_ACTIVE` ×2 | Live migration gate; `PasswordSwitchConstantParityTest` | PASS |
| DA-002 / DA-003 texts byte for byte | T01 | Migration | Live desc check; re-checked here (all 3 texts are substrings of the SQL) | PASS |
| UI-001 / UI-002 / UI-004 / UI-005 | T06 | `login.js` code-only and FN-010 arms; cache-buster `?20261017` ×2 | T06 walkthrough (author evidence, user visual check) | PASS |
| UI-003 copy in 4 files, apostrophes doubled | T07 | 4 properties files | i18n read; `WelcomeCodeSignInCopyTest` | PASS |
| NF-001 one rule | T02–T07 | All consumers call `SignInMethodRule` | Static lock on senders; eligibility tests | PASS |
| NF-002 at most one query | T04 | `methodsFor` single query | `#oneQueryPerCall…`, no-key and username cases | PASS |

**Clause level:** every `BUT` and `AND IT MUST` clause of the 8 scenarios maps to a named test row in `test-report.md` §7.
One clause has thinner evidence than the others:

- AC-4 "CGIAR or not" is proven at the predicate (`OtpEligibility`), not at the endpoint. The endpoint calls the same `isEligible`. Accepted in `test-report.md`.

## 7. Linting & Code Quality

Checkstyle and compile are in §5. Advisory 4R findings, labeled **ADVISORY**. They do not drive the verdict.

| # | Lens | Finding | Source |
|---|---|---|---|
| A-1 | Readability | The DD-6 fail-open ERROR in `CrpByUserEmailAction.java:181` logs through a logger named after `FlaghshipsByCrpAction` (`:75`, older than this spec). An operator filtering by class will not find it | New in this validation |
| A-2 | Reliability | `WelcomeSignInText`: a unit with neither acronym nor name renders "…choose  and select…" | `execution.md` T07 |
| A-3 | Readability | `OtpDomainPolicy.java` lines 28 and 35 still say the domain "plays no part (SEC-009)". Some `login.js` comments are historical | `execution.md` T08 pending |
| A-4 | Accessibility | The FN-010 message uses `role="status"` on static text revealed by a class change; `aria-describedby` was added as mitigation | `execution.md` T06 |
| A-5 | Risk | Rate-limit lock-out with the password off: one user's code requests can exhaust the limit. Record only (user decision) | `execution.md` T08 |
| A-6 | Readability | The center `design.md` `crpByEmail.do` note and runbook §1 row 2 omit "for an account without Cognito" and "and the exclusion" | `execution.md` T08 attempt 3 |
| A-7 | Risk | `finishLogin` uses the activity-blind `existCrpUser`, and the SEC-005 guard compares the raw identifier. Both are older than this spec, routed to a separate bugfix spec | requirements Decision Log (T05) |

## 8. Design Conformance

The implementation matches design §1 (the rule), §2 (footprint), §3 (migration and fixed texts), §4 (actions), §5 (views),
§9 (specificity), §11 (log lines) and DD-1 to DD-7. It also matches the proposal: intent, non-goals and the 6 success
criteria of §13, with the CLARISA criterion proven live. The Visual Reference (HTML mockups) was the comparison target for
T07 and T06.

Drift found:

| ID | Severity | Drift | Recommendation |
|---|---|---|---|
| **WARN-1** | Low | `requirements.md` AC-12 still says "an `/api/**` Basic request still authenticate[s]", and design §13 and §16 still describe `/api/**` as an accepted risk "to be reviewed in a separate issue". On 2026-10-18 the user ruled `/api/**` Basic unused in MARLO, and `test-report.md` marks it N/A. The spec does not record that decision | Add a Decision Log line and an amendment note on AC-12 (and SEC-006): "`/api/**` part N/A, not used in MARLO (user, 2026-10-18)" |
| **WARN-2** | Low | DD-8 (design §15) and the 2026-10-16 cross-spec Decision Log entry say "a Cognito account is refused under the override". This is literally true but easy to misread as an unconditional refusal. That misreading caused both T08 FAILs | Reword: "a Cognito account is not admitted by the override; the code switch and the exclusion still apply to it as written" |
| **WARN-3** | Low | The `execution.md` T08 pending item says the DD-6 `crpByEmail.do` ERROR "has no matching log call". **That is false:** `CrpByUserEmailAction.java:181` logs it at ERROR ("unable to resolve the sign-in methods; answering the password for accounts without Cognito"). Runbook §5 does not list this line | Add the line to runbook §5. `execution.md` is append-only, so record the correction here and leave its text unchanged |
| **WARN-4** | Low | The `tasks.md` "Definition of Done" boxes are unticked and "Last Updated" reads 2026-10-16, although every item is met: T01–T08 PASS, gates green, live evidence recorded, amendments with sweeps, switch on by default | Tick the 5 boxes, cite this report, update the date |
| **WARN-5** | Process | Budget overrun. The design §18 tripwire ran on review rounds only (10 → 19, both raises approved by the user). LOC was never tracked: production code is +896/−85 Java plus 106 webapp/resource lines (design estimate ~420), tests are +4859 (estimate ~700), and docs and probes are +4241 | No code action. Record for kaizen at archive time: LOC estimates for security tasks with exhaustive tests run about 3× (production) and 7× (tests) over |

**Cross-document figures checked:** 7 senders (requirements §6, design §2, tasks T07, test static lock); 9 properties
files = 4 copied + 5 falling back (design P-11, DD-7); 18 premises (design §17 header vs the table); 983 = 976 + 7 tests
(test report, re-run here). All are consistent. The budget figures are covered in WARN-5.

## 9. Test Evidence Summary

| Suite | Status | Source |
|---|---|---|
| Backend unit | PASS, 983 / 0. Re-run here at HEAD | §5 |
| Frontend unit | Not available. MARLO has no JS runner (TRD decision) | `test-report.md` §4 |
| Integration (live) | PASS, non-author re-run: AC-2 70/0, AC-10 23/0, AC-8 8/0, override 10/0, runbook SQL 5/0, AC-7 11/0, CLARISA PASS, FN-009 14/0 | `test-report.md` §5 |
| E2E | T06 walkthrough cited (author evidence plus user visual check) | `test-report.md` §6 |

There is no `PRODUCT_BUG`. The accepted gaps carried from the test report:

- AC-13, live behaviour of 6 of the 7 senders: static lock only.
- FN-009 "usable" password: storage proven, use proven by the control run.
- No JS runner.
- `/api/**` N/A.

The harness items R1–R3 (fixture row, `runbook-sql` path, falsifier A description) are deferred to the next live re-run.

## 10. Agent Guide / Constitution Impact

- `execution.md` has no `## Constitution Impact` section, and no task created a module or moved a boundary.
- No root guide, persona, template or `docs/trd/trd.md` was edited on this branch.
- Pending for `/akili-archive` (applied on `staging`): `docs/trd/trd.md` §8.1 should describe the password switch. The pending TRD 8.1 text is drafted in `center-email-otp-login/runbook.md`.

## 11. Remediation

| # | Item | Type | Effort |
|---|---|---|---|
| 1 | **Done 2026-10-18.** WARN-1: AC-12 / SEC-006 amendment note plus a Decision Log line (`/api/**` N/A, user 2026-10-18); align design §13 and §16 | Spec text | Small |
| 2 | **Done 2026-10-18.** WARN-2: reword DD-8 and the 2026-10-16 cross-spec Decision Log sentence. Correction closure: sweep this folder for "refused under the override" forward, and for references to DD-8 backward | Spec text | Small |
| 3 | **Done 2026-10-18.** WARN-3: add the `crpByEmail.do` fail-open ERROR line to runbook §5 | Doc | Small |
| 4 | **Done 2026-10-18.** WARN-4: tick the Definition of Done in `tasks.md` and update "Last Updated" | Spec text | Trivial |
| 5 | A-1, A-3 (logger name, `OtpDomainPolicy` comments) | Code, optional | Separate small change; not needed for archive |
| 6 | R1–R3 harness fixes | Probes | Deferred (user, 2026-10-18) |
| 7 | WARN-5 LOC lesson | Kaizen | At `/akili-archive` |

## 12. Archive Readiness Recommendation

**Ready to archive.** The user chose to apply remediation items 1–4 (2026-10-18). They are done, with correction closure:

- Forward sweep: "refused under the override" no longer appears outside the history files (`execution.md`, probe outputs).
- Forward sweep: "separate issue" for `/api/**` remains only in the superseded Judgment Day C-1 log entry, which the new 2026-10-18 line overrides. The runbook §7 row is fixed.
- Backward sweep: `center-email-otp-login/requirements.md:462` cites DD-8 and already uses the qualified wording from T08.

WARN-5 goes to kaizen at archive time.

- All 8 tasks are `[x]`, and no FAIL remains.
- The tests cover every key requirement, and the gaps are accepted.
- The drift is limited to four documentation lines and one process note.

Next command: `/akili-archive changes/password-login-switch`
