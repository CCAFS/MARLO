# Archive Summary — Password Login Switch

**Outcome: delivered and validated.** A super admin can turn password sign-in off per Global Unit. Accounts that do
not use Sign in with CGIAR then sign in with the emailed code, which becomes their default method. The switch ships
**on** everywhere, so deploying changes nothing until a super admin turns it off. The `/akili-validate` verdict was
PASS with no FAIL. No product bug was found.

## 1. Document Control

| Field | Value |
|---|---|
| Spec ID | `CHG-PWD-SWITCH-001` |
| Branch | `A2-2631-Implement-Center-Email-OTP-Authentication` |
| Spec commits | `5f11bc37e2` (spec) … `f1611e669b` (validation), 12 commits |
| Archived by | `/akili-archive`, Claude Code session (`opus`) |
| Approval mode | `gated` |

## 2. Original Spec Path

`docs/specs/changes/password-login-switch/`

## 3. Archive Date

2026-10-18 → `docs/specs/archive/2026-10-18-changes--password-login-switch/`

## 4. Final Status

| Phase | Result |
|---|---|
| Propose / Specify | Approved 2026-10-16. Judgment Day APPROVED after two correction rounds (`judgment.md`) |
| Execute | 8 of 8 tasks `[x]`, each closed with a Reviewer PASS. 19 review rounds against a budget of 10; the user approved all three raises |
| Test | PASS with accepted gaps: 983 unit tests and a non-author live re-run (`test-report.md`) |
| Validate | PASS, no FAIL. WARN-1 to WARN-4 fixed in `f1611e669b`; WARN-5 sent to kaizen (`validation-report.md`) |

## 5. Requirements Delivered

All 30 requirements are delivered: FN-001–010, SEC-001–007, OPS-001–003, DA-001–003, UI-001–005, NF-001–002.

| Capability | Requirements |
|---|---|
| Per-unit switch `system_password_login_active`, default on, read live with no cache | FN-001, FN-002, DA-001–003 |
| One rule (`SignInMethodRule`) for Cognito, code and password, used by every consumer | NF-001, FN-003–007 |
| The code as default method with the switch off, including when the code switch is off or the domain is excluded; Cognito accounts unchanged | FN-004, FN-005, SEC-003, SEC-004 |
| Last-resort password when the code key is missing, with one ERROR per unit per start *(Corrected 2026-10-21: written at the first `crpByEmail.do` lookup or welcome-email build that answers for that unit, not at startup; `OtpEligibility.logLastResortOnce`)* | FN-006, OPS-002 |
| Server-side password refusal before any credential or LDAP check, answered like a wrong password | SEC-001, SEC-002, OPS-001 |
| Step 3 shows the code alone, or "use your email" when the user typed a username | UI-001, UI-002, UI-004, UI-005, FN-010 |
| The welcome email explains code sign-in and still stores a password | FN-008, FN-009, UI-003 |
| `CHG-OTP-LOGIN-001` amended, not forked (DD-8); runbook written | OPS-003 |

## 6. Files Changed Summary

Counted with `git diff 5f11bc37e2..f1611e669b`:

| Area | Files | Lines |
|---|---|---|
| Production Java (`marlo-web`, `marlo-data`) | 19 *(Corrected 2026-10-21: was 21; `git diff --numstat 5f11bc37e2 f1611e669b` over `marlo-web/src/main` and `marlo-data/src/main` Java lists 19)*, including 2 new: `SignInMethodRule`, `WelcomeSignInText` | +896 / −85 |
| Webapp (`login.js`, `loginForm.ftl`, 2 cache-busters) | 4 | +61 / −10 |
| Resources (migration `V2_6_0_20261016_1525__AddPasswordLoginSwitch.sql`, 4 properties files) | 5 | +45 |
| Tests | 23, including 10 new *(Corrected 2026-10-21: was 9; `git diff --diff-filter=A` lists 10: 8 test classes plus 2 helpers without `@Test`, `RealCredentialPath` and `UnconfiguredOtpEligibility`)* | +4859 / −94 |
| Docs and probes (this folder, plus amendments in `center-email-otp-login`) | — | ~+4,300 |

## 7. Test Evidence Summary

- **Unit:** 983 run, 0 failures, re-run at validation on HEAD `9835384963`.
- **Live**, on the throwaway copy `aiccradb_otp_probe`, re-run by a non-author:

| Probe | Result |
|---|---|
| AC-2 | 70/0 |
| AC-10 | 23/0 |
| AC-8 | 8/0. Stale-value falsifier B red 3/8 |
| Override | 10/0 |
| Runbook SQL | 5/0 |
| AC-7 | 11/0 |
| CLARISA | PASS |
| FN-009 | 14/0 |

- **UI:** T06 browser walkthrough, with the user's visual check.
- **Accepted gaps:**
  - AC-13 live behaviour for 6 of the 7 senders: static lock only.
  - No JS test runner.
  - `/api/**` part of AC-12: N/A.

## 8. Validation Summary

| Check | Result |
|---|---|
| Compile | 2415 / 1066 / 88 files |
| Checkstyle | 0 → 0 on 42 files |
| i18n | Clean |
| Requirement coverage | 30 / 30 traced, clause level included |
| Drift found | WARN-1 to WARN-4, fixed in `f1611e669b`: AC-12 N/A note, DD-8 wording, runbook ERROR line, Definition of Done |
| WARN-5 | Budget overrun, recorded as kaizen lesson `KZ-changes--password-login-switch-2` |

## 9. Accepted Warnings Or Follow-Ups

| # | Item | Owner / where |
|---|---|---|
| F-1 | Switch stays **on** in production. Turning it off is an operational decision behind the runbook §2 prerequisites (mail relay, delivery data, OQ-3 counts) | PMU / support, `runbook.md` |
| F-2 | `CHG-OTP-LOGIN-001` cites this folder at its old path in 5 places: `requirements.md:462`, `:467`, `:470`, `runbook.md:14`, `probes/README.md:318`. After this move they should read `docs/specs/archive/2026-10-18-changes--password-login-switch/` | `center-email-otp-login`, at its next edit (it is still active) *(Corrected 2026-10-21: resolved. A grep for `changes/password-login-switch` in `docs/specs/changes/center-email-otp-login/` now finds only the validation-report V-11 entries that record the fix)* |
| F-3 | Harness fixes R1–R3. The scripts in `probes/` (`sweeps.sh`, `ac1-ac14-migration.sh`) also hard-code the pre-archive path, so they are history and need path edits before any re-run | Next live re-run |
| F-4 | Advisory A-1 (the `CrpByUserEmailAction` logger is named `FlaghshipsByCrpAction`) and A-3 (`OtpDomainPolicy` comments lines 28 and 35) | Optional small code change |
| F-5 | `finishLogin` uses the activity-blind `existCrpUser`, and the SEC-005 guard compares the raw identifier (older than this spec) | Separate bugfix spec (requirements Decision Log, T05) |
| F-6 | Rate-limit lock-out risk with the password off | Recorded only (user decision) |
| F-7 | `docs/trd/trd.md` §8.1 and the root guides' test counts | Kaizen pending items, applied on `staging` |

## 10. Historical Notes

- The design conversation's "rule 3c" became "rule 3d", after the user said "si todos están apagados debe quedar por defecto el código".
- Judgment Day caught 4 severe findings (C-1 to C-4) and one on re-judgment (N-1) before any code was written. C-2, the foreign-unit bypass of the password gate, would have been a security defect.
- The T08 FAILs had one root cause: DD-8 compressed a conditional rule into "a Cognito account is refused under the override". The fix needed a read of every added line by meaning; a regex sweep was not enough.
- At validation, a T08 pending note claiming the DD-6 ERROR had no log call turned out to be false. It had searched for `LOG`, and the action logs through `this.logger`.
