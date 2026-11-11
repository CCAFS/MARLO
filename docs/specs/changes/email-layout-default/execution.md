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

### CHG-EMAIL-LAYOUT-001-T03 — Submit/unsubmit copy (Appendix A) and the summary link in the text

- **Final status:** PASS
- **Date:** 2026-11-11
- **Implementer attempts:** 1
- **Skills / effort:** marlo-verify; Implementer effort medium; Reviewer full (high).

#### Attempt 1
- **Files changed:** `marlo-web/src/main/resources/global.properties`, `custom/aicrra.properties`, `custom/aiccra3.properties` (3 keys each), `custom/pabra.properties` (2 submit keys, `href` only); new `marlo-web/src/test/java/org/cgiar/ccafs/marlo/utils/SubmitEmailCopyTest.java` (108 lines, 4 tests, GPL header). Diff: +11/−11 in properties, +108 test.
- **Red run (copy test before the property edits):** `Tests run: 4, Failures: 4` — aicrra / aiccra3 / global `submit.email.message lacks @6@`; pabra `submit.email.message must link {6}`. Baseline greps: `projectID=102080` in aicrra, aiccra3, pabra; Flagship in global submit/unsubmit = 2.
- **Implementer verification:** G-compile exit 0 (counted classes; `-q` hid the compile lines); G-test `SubmitEmailCopyTest,EmailLayoutTest,EmailPlainTextTest,OtpMailSenderTest,UnitTextProviderTest` exit 0, 67 tests, 0 failures; G-check new file 0 violations; G-i18n no findings. Scripted Appendix diff = the test: `appendix(key, noun, summary, returner)` rebuilds A.1–A.3 and `assertEquals` it against each key formatted with sentinels `@0@`..`@7@` (`{6}` = `https://s6`).
- **Falsifiers (executed):** hardcoded URL back in aiccra3 `.noPDF` → `AssertionError: aiccra3 submit.email.message.noPDF lacks https://s6`; single `'` in base `submit.email.message` → `AssertionError: global submit.email.message lacks @0@` (first attempt edited the wrong spot and wrongly passed; redone anchored on line start, then red). Both reverted.
- **Not Done / Assumptions (Implementer, carried verbatim in substance):** (1) planning `submit.email.message` now carries `<a href="{6}">` but `ProjectSubmissionAction` fills `values[6]` only in reporting — empty link in planning until T04 (T04 scope). (2) Quote block markup chosen as `<blockquote style="margin:0 0 16px 0;padding:0 0 0 16px;border-left:3px solid #cccccc;"><em>{7}</em></blockquote>` (Appendix says only "indented quote block"). (3) Unsubmit indices taken from the Appendix without opening `UnsubmitProjectAction`. (4) pabra unsubmit and other units' keys left as they were. — No owed item, no blocker: not a continuation (item (1) is T04's approved scope).
- **Evidence re-run (Leader inline, non-author):** VERIFIED — `mvn -o -pl marlo-web -am test -Dtest=<five classes>` exit 0, `Compiling 2430` / `1063` / `112`, `Tests run: 67, Failures: 0, Errors: 0`; `grep -l 'projectID=102080' custom/*.properties` empty; Flagship count 0.
- **Reviewer verdict:** PASS (full) — all nine keys and the test's `appendix(...)` template diffed by the Reviewer against requirements.md Appendix A.1–A.3 verbatim (independent of the Implementer's transcription); A.2 substitutions applied; submit `{0}`–`{6}` match `ProjectSubmissionAction.java:296-314`, unsubmit `{0}`–`{7}` match `UnsubmitProjectAction.java:266-282` (`{6}` there is the cycle); quote block conforms; pabra only `href`; GPL header matches the AGENTS.md template.
- **runtime events:** none

- **spawns:** implementer 22 calls, 116224 tokens, ended partial (Not Done / Assumptions field, no owed item); reviewer 12 calls, 58623 tokens, ended complete
- **ADVISORY (4R, non-gating):**
  - Risk (cross-task dependency): until T04, planning submits render `<a href="null">`. T03 must not reach `staging`/`dev` without T04 — keep them together on this branch, never cherry-pick T03 alone.
  - Reliability: the plain-text assertion checks only the last argument; the HTML `assertEquals` already covers all of them.
  - Reliability: `contains("407")` could false-positive on future copy; `"phaseID=407"` / `"projectID=102080"` would be more precise.
  - Readability: `#cccccc` in the blockquote is a hardcoded colour (UX tokens do not apply to inline email styles).
- **Requirements covered:** FN-007 (text side), FN-008, FN-009, NF-002, NF-003.
- **Decisions made:** quote-block markup accepted (Reviewer confirmed conformance).
- **Issues encountered:** none.
- **Final verification:** green (re-run VERIFIED, Reviewer PASS).
- **Budget running totals:** tasks 3/8; LOC 445 changed (315 + 130) of ~800 (owner-adjusted); review rounds 3/10. Within budget.

### CHG-EMAIL-LAYOUT-001-T04 — Submit email: own unit's texts, real link, honest PDF wording, shell

- **Final status:** PASS
- **Date:** 2026-11-11
- **Implementer attempts:** 1
- **Skills / effort:** marlo-verify, tdd; Implementer effort high (Java on a live user flow — above the medium default); Reviewer full (high).

#### Attempt 1
- **Files changed:** `marlo-web/src/main/java/org/cgiar/ccafs/marlo/action/projects/ProjectSubmissionAction.java` (+61/−24, LF preserved); new `marlo-web/src/test/java/org/cgiar/ccafs/marlo/action/projects/ProjectSubmissionEmailTest.java` (~105 lines, GPL header).
- **Change:** `CustomParameterManager` added to the `@Inject` constructor; private `unitTexts()` reads `APConstants.CRP_CUSTOM_FILE` for `loggedCrp` → `UnitTextProvider.forCustomFile`, any `RuntimeException` → one slf4j WARN, fallback `this`; package-private `static SubmitEmail assembleEmail(TextProvider, String assetsUrl, String crp, String projectAcronym, String[] values, boolean pdfAttached)` (`// @akili-spec changes/email-layout-default`) resolves subject, picks `submit.email.message` only when `pdfAttached` else `.noPDF`, footer `email.role.support.noCrpAdmins` + `email.role.bye`, wraps with `EmailLayout.wrap`; call site computes `{6}` in both cycles and `pdfAttached` after PDF generation.
- **Red run:** test written against a stub of `assembleEmail` keeping today's behaviour (legacy `email.support.noCrpAdmins`/`email.bye`, no wrap) → `Tests run: 5, Failures: 5`, exit 1, each `java.lang.AssertionError: <case> is not inside the shell` (content assertions before it passed). Baseline `grep -c EmailLayout ProjectSubmissionAction.java` → 0.
- **Implementer verification:** G-compile exit 0 (2430 / 1063 / 113); G-check `ProjectSubmissionAction.java` 1 at HEAD, 1 now (pre-existing `MethodLength` on `sendNotficationEmail`; method shrank ~13 lines), new test 0 → delta 0; G-test 22 tests, 0 failures; hygiene ok.
- **Falsifiers (executed):** fixed `aicrra` provider → 2 red (aiccra3 case, base case); `pdfAttached` forced `true` → case (b) red; `wrap` dropped → 5 red. All reverted (diff against saved copy).
- **FN-010 envelope:** to/cc/bcc construction and PDF/attachment fields absent from the diff; both `sendMail.send` calls changed only the subject/body arguments.
- **Not Done / Assumptions (Implementer):** (1) falsifier 2 tests the static method's choice, not the call-site order — call-site order is review-only; (2) footer counted as `mailto:MARLOSupport@cgiar.org` once, one "automated notification", one "Kind regards", no "technical MARLO team" sentence; (3) missing-file case uses `forCustomFile("noSuchFile")`; (4) `unitTexts()` and call-site wiring untested (need Hibernate) — compile + review only. No owed item, no blocker: not a continuation.
- **Evidence re-run (Leader inline, non-author):** VERIFIED — `mvn -o -pl marlo-web -am test -Dtest=ProjectSubmissionEmailTest,SubmitEmailCopyTest,EmailLayoutTest,EmailPlainTextTest` exit 0, `Compiling 2430` / `1063` / `113`, `Tests run: 22, Failures: 0, Errors: 0`.
- **Reviewer verdict:** PASS (full) — `pdfAttached` set after the PDF try/catch with the old three-way non-null test (both failure paths leave `buffer` null → `.noPDF`); `{6}` same expression, reporting-only guard removed (DD-5); envelope untouched; `unitTexts()` same key as the OTP exemplar, null-safe via the catch, slf4j, exception-only log argument; injection consistent, `validate()` untouched; MethodLength pre-existing; tests call the production method with real providers, asserted wording comes from the real `.properties`; subject through the unit provider (FN-011 c).
- **runtime events:** none

- **spawns:** implementer 27 calls, 145456 tokens, ended partial (Not Done / Assumptions, no owed item); reviewer 9 calls, 63590 tokens, ended complete
- **ADVISORY (4R, non-gating):**
  - Reliability: under the microservice path `ReportingSummaryAction.execute()` swallows its own errors (L1392–1397), so `getBytesPDF()` can return an empty array — `pdfAttached` true, 0-byte PDF attached, email says "attached". Pre-existing, outside AC FN-007; `buffer.remaining() > 0` would close it but needs a spec decision. Raised to the owner at the T04 gate.
  - Readability: call-site ordering covered by review only (static method cannot test it).
  - Risk: `unitTexts()` does one DB read even when `validateEmailNotification()` is false; negligible, not in `validate()`.
- **Requirements covered:** FN-006, FN-007 (both cycles, failed PDF), FN-010, FN-011 (submit).
- **Decisions made:** effort raised to high (Leader); test placed under `action/projects/` beside the action (package-private access).
- **Issues encountered:** none.
- **Final verification:** green (re-run VERIFIED, Reviewer PASS).
- **Budget running totals:** tasks 4/8; LOC ~635 changed (445 + 85 prod + ~105 test) of ~800 (owner-adjusted); review rounds 4/10. Within budget; T05 (similar size) will bring it near the cap.

## Budget Adjustment (after T04)

- **Date:** 2026-11-11
- **Delta:** ~635 LOC after 4 of 8 tasks against the ~800 cap set after T02; projected ~870–900 at completion.
- **Cause:** test volume (T04 production ~85 LOC, within estimate; T05 is symmetric to T04).
- **Decision:** owner (Kenji Tanaka) approved raising the LOC tripwire to **~950**; tasks (8) and review rounds (10) unchanged.

## Deferred (owner decision at the T04 gate)

- **Empty PDF from the microservice path:** `ReportingSummaryAction.execute()` swallows its own errors, so `getBytesPDF()` can return an empty array; the submit email then attaches a 0-byte PDF and says "attached". Pre-existing, outside AC FN-007. Owner decision 2026-11-11: **not in this spec** — candidate for a separate proposal (possible fix: `buffer.remaining() > 0` in `pdfAttached`).
