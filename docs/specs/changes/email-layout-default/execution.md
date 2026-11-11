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

### CHG-EMAIL-LAYOUT-001-T02 — Footer-once in the base file and the two overriding units

- **Final status:** PASS
- **Date:** 2026-11-11
- **Implementer attempts:** 1
- **Skills / effort:** marlo-verify; Implementer effort medium; Reviewer full (high).

#### Attempt 1
- **Files changed:** `marlo-web/src/main/resources/global.properties`, `custom/ciat.properties`, `custom/pabra.properties`; tests `utils/EmailLayoutTest.java`, `utils/OtpMailSenderTest.java`. Diff: 5 files, +119 / −17.
- **Red run (footer-once tests written before the property edits):** support address / do-not-reply counts in the assembled, MessageFormat-formatted, wrapped email — role email of global, alliance, ciat, pabra: 3 / 2 each, for admins `""` and `"Jane Admin"`; base OTP: 2 / 1; aicrra, aiccra3 role and the aicrra/aiccra3/test OTP overrides passed; `testBaseRoleBlocksAreShellAwareAndTheSharedOnesAreUnchanged` failed on `dear`. Baseline greps: role keys with support/notice in global = 3; `login.otp.email.html` with "Need help" = 1.
- **Implementer verification:** G-compile exit 0 (2430 / 1063 / 111); G-test five suites 130 run, 0 failures; G-check delta 0 on the two test classes; G-i18n no findings; LF preserved.
- **Falsifiers (executed):** restoring pabra's `email.role.support.noCrpAdmins` → role footer-once red for pabra only (2 / 1); re-adding "Need help…" to the base OTP html → OTP footer-once red for global.properties (2 / 1). Both reverted.
- **Evidence re-run (Leader inline, non-author):** VERIFIED — `mvn -o -pl marlo-web -am test -Dtest=EmailLayoutTest,OtpMailSenderTest,EmailPlainTextTest,UnitTextProviderTest,OtpRequestCodeActionTest` exit 0, `Compiling 2430` / `1063` / `111`, `Tests run: 130, Failures: 0, Errors: 0`; `git diff` of aicrra/aiccra3/alliance = 0 lines; base greps 0 / 0.
- **Reviewer verdict:** PASS (full) — base `dear`, `support`, `support.noCrpAdmins`, `getStarted`, `bye` and the OTP html match Appendix A.4/A.5 verbatim; ciat/pabra deletions are the A.4 list; AICCRA/AICCRA_III/alliance absent from the diff (FN-003); tests assemble the real email through `getText`/`MessageFormat` and `EmailLayout.wrap`; the scan reads `custom/`, no hard-coded list; T01's DD-9 comment block untouched; no AICCRA wording in base values.
- **runtime events:** none

- **spawns:** implementer 18 calls, 113980 tokens, ended complete; reviewer 6 calls, 58965 tokens, ended complete
- **ADVISORY (4R, non-gating):**
  - Reliability (coverage): the scan finds files overriding `email.role.*` after the change (alliance, aicrra, aiccra3). ciat and pabra no longer override any role key, so they are covered by the base run (their role email resolves identically to the base). A test comment saying so would prevent a later reader from thinking two AC FN-005 units went untested; T08's live SQL counts cover ciat/pabra explicitly if they are among the units sent.
  - Readability (counting): counts are on tag-stripped visible text (the shell's `mailto:` href + link text would otherwise double-count); a body `mailto:` under different link text, or "don't reply" wording, would not be counted. Not a defect today (FN-005 is about what the email shows).
  - Resilience: `getResource("/custom").toURI()` as `File` works only while test resources are a directory; the role test has a `units.size() > 1` guard, the OTP test has none.
  - Readability: line ~204 of the new test sits at ~120 chars (Checkstyle delta 0).
  - Risk: low — TEST, ciat, pabra now get the base greeting and sign-off (accepted: JD-3, A.4).
- **Requirements covered:** FN-005 (base + overriding units + OTP), FN-003 (AICCRA/AICCRA_III untouched), NF-002.
- **Decisions made:** Implementer assumptions accepted — OTP footer-once scans every file overriding `login.otp.email.html` (aicrra, aiccra3, test; superset of "base and aicrra"); `{3}` passed as the unit's `footer.contactEmail`; base `email.role.getStarted` = current sentence as a paragraph per A.4 (not aicrra's empty value — confirmed by the Reviewer). The Reviewer diff was passed by scratchpad path rather than inline (225 lines) to avoid transcription error; the wrapper Reviewer keeps `Read`.
- **Issues encountered:** none.
- **Final verification:** green (re-run VERIFIED, Reviewer PASS).
- **Budget running totals:** tasks 2/8; LOC 315 changed (179 + 136) of ~380; review rounds 2/10. LOC trending above budget — tests (+119 here) carry most of it; to watch at T03.

## Budget Adjustment (after T02)

- **Date:** 2026-11-11
- **Delta:** 315 LOC after 2 of 8 tasks against a design budget of ~380; projected ~700–800 at completion.
- **Cause:** test volume required by the tasks' own disqualifiers (folder scan, both admins variants, counts on the assembled email) — production code within estimate.
- **Decision:** owner (Kenji Tanaka) approved raising the LOC tripwire to **~800** and keeping the tests as written; tasks (8) and review rounds (10) unchanged.
