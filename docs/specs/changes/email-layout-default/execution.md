# Branded email shell by default — Execution Log

## Document Control

| Field | Value |
|---|---|
| Spec Path | `changes/email-layout-default` |
| Spec ID | CHG-EMAIL-LAYOUT-001 |
| Approval Mode | gated (inherited from `proposal.md`) |
| Branch | `email-layout-default` (base `staging` `460ecbd5be`) |
| Build location | worktree `../MARLO-wt-email-layout` (detached, fast-forwarded into the branch per commit); tasks.md: "build in a worktree, never in the main checkout" |
| Leader | Opus 5.5 (T1) |
| Implementer / Reviewer | `akili-implementer` (sonnet, T2) / `akili-reviewer` (opus, T3) |
| Started | 2026-11-11 |

## Task Execution History

### CHG-EMAIL-LAYOUT-001-T01 — The shell becomes the base value

- **Final status:** PASS
- **Date:** 2026-11-11
- **Implementer attempts:** 1
- **Skills / effort:** marlo-verify; effort medium (task list also names marlo-commit — the commit is made by the Leader after owner approval, so it was not given to the Implementer).

#### Attempt 1
- **Files changed:** `marlo-web/src/main/resources/global.properties`, `custom/aicrra.properties`, `custom/aiccra3.properties`, `custom/test.properties`; `marlo-web/src/main/java/org/cgiar/ccafs/marlo/utils/EmailLayout.java`, `utils/OtpMailSender.java` (Javadoc/comments only); tests `utils/EmailLayoutTest.java`, `utils/UnitTextProviderTest.java`, `utils/OtpMailSenderTest.java`, `action/home/OtpRequestCodeActionTest.java`. Diff: 10 files, +76 / −103.
- **Red run (rewritten tests against the unchanged properties):** 4 failures of 128 —
  - `UnitTextProviderTest.aMissingNullOrUnsafeFileNameLeavesTheUnitOnGlobalOnly`: `<null> resolves the base shell: BODY`
  - `UnitTextProviderTest.twoProvidersAreIndependentAndHoldNoSharedState`: `a later provider for another unit is not affected by the first`
  - `EmailLayoutTest.testBaseLayoutWrapsTheBodyInTheShell`: `AssertionError` (unmessaged last assertion)
  - `OtpRequestCodeActionTest.aUnitWithoutACustomFileGetsTheGlobalTextsAndTheBaseShell`: `the base shell wraps the body: BODY`
  - Baseline `grep -c '^email.layout='` → global 1, aiccra3 1, aicrra 1, test 1.
- **Implementer verification:**
  - G-compile: exit 0; compiled 2430 (marlo-data) / 1063 (marlo-web) / 111 (tests).
  - G-test (`EmailLayoutTest,UnitTextProviderTest,OtpMailSenderTest,OtpRequestCodeActionTest,EmailPlainTextTest`): 128 run, 0 failures.
  - G-check: delta 0 (one 121-char test line found on first run, wrapped).
  - G-i18n: no findings; shell moved verbatim; added comment lines contain no `'`.
  - md5 of the moved block: aicrra@HEAD = global after = `6b52db6bd9808a9361aa0d0e97c79985`.
  - Disqualifier: `grep -c '^email.layout='` → 0 in aicrra, aiccra3, test.
- **Falsifier (executed):** base `email.layout` back to `{0}` → 13 tests red (EmailLayoutTest ×2, UnitTextProviderTest ×5, OtpRequestCodeActionTest ×2, OtpMailSenderTest ×3, EmailPlainTextTest ×1); opt-out fixture value set to the shell → `testAUnitThatOptsOutSendsTheBodyAsItIs` red. Both reverted.
- **Evidence re-run (Leader inline, non-author):** VERIFIED — `mvn -o -pl marlo-web -am test -Dtest=<five classes>` exit 0, `Compiling 1063` + `Compiling 111`, `Tests run: 128, Failures: 0, Errors: 0`; md5 `6b52db6bd9808a9361aa0d0e97c79985` both sides; grep 0/0/0 in the three custom files, 1 in global.
- **Reviewer verdict:** PASS (checklist mode) — shell is the base value and byte-identical; DD-9 comment values equal the live `email.role.support`, `.support.noCrpAdmins`, `.bye`, `login.otp.email.html` verbatim; Java changes are comments only; no coverage lost (null / `noSuchFile` inputs still covered in `UnitTextProviderTest`; new opt-out fixtures test DD-9).
- **runtime events:** none

- **spawns:** implementer 19 calls, 117910 tokens, ended complete; reviewer 11 calls, 58834 tokens, ended complete
- **ADVISORY (4R, non-gating):**
  - Readability: `OtpMailSender.java` class Javadoc breaks early after "sends the bare body. The non-production" — could be reflowed.
  - Readability: `global.properties` comment says "together with the block below" while the block sits above the `email.layout=` line — "the block that follows" would be clearer.
  - Reliability: with the null/`noSuchFile` providers removed from `OtpMailSenderTest`, no sender-level test sends for a unit with no custom file and asserts the wrap; coverage remains at provider level.
- **Requirements covered:** FN-001, FN-002 (opt-out fixture), FN-003 (identical shell inherited), FN-004 (one definition).
- **Decisions made:** Implementer assumptions accepted — `OtpMailSenderTest` opt-out uses an `override("email.layout","{0}")` provider; the HTML-part "no http" check is scoped to `bareBody()` plus an assertion that the HTML contains it; the DD-9 comment keeps the `\:` escape of `login.otp.email.html`.
- **Issues encountered:** none.
- **Final verification:** green (re-run VERIFIED, Reviewer PASS).
- **Budget running totals:** tasks 1/8; LOC 179 changed (+76/−103) of ~380; review rounds 1/10. Within budget.
