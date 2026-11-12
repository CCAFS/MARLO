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

### CHG-EMAIL-LAYOUT-001-T05 — Unsubmit email: own unit's texts, role footer, shell

- **Final status:** PASS
- **Date:** 2026-11-11
- **Implementer attempts:** 1
- **Skills / effort:** marlo-verify, tdd; Implementer effort high; Reviewer full (high). Exemplar: T04 (`ProjectSubmissionAction` + `ProjectSubmissionEmailTest`).

#### Attempt 1
- **Files changed:** `marlo-web/src/main/java/org/cgiar/ccafs/marlo/action/json/project/UnsubmitProjectAction.java` (+60/−9, LF preserved); new `marlo-web/src/test/java/org/cgiar/ccafs/marlo/action/json/project/UnsubmitProjectEmailTest.java` (105 lines, GPL header).
- **Change:** `CustomParameterManager` injected, private slf4j `LOG`; `unitTexts(GlobalUnit)` from the origin unit's `crp_custom_file` (`APConstants.CRP_CUSTOM_FILE`), `RuntimeException` → WARN, fallback `this`; package-private static `assembleEmail(...)` (`@akili-spec`) builds subject, body, footer (`email.role.support.noCrpAdmins` → `email.role.getStarted` → `email.role.bye`) from one provider and wraps with `EmailLayout.wrap`; `UnsubmitEmail` holder mirrors T04.
- **Red run:** test against a stub of today's assembly (legacy keys, no shell) → 4 of 5 failed (3 on `... is not inside the shell`, 1 on a wording assertion); subject case (e) passed on the stub — only its falsifier turns it red. Baseline `grep -c EmailLayout UnsubmitProjectAction.java` → 0.
- **Implementer verification:** G-compile exit 0 (2430 / 1063 / 114); G-check 2 at HEAD, 2 now (pre-existing LineLength on commented-out lines), new test 0 → delta 0; G-test 22 tests, 0 failures; hygiene ok.
- **Falsifiers (executed):** subject via `forCustomFile(null)` → (e) red (expected `[AICCRA] AICCRA cluster [P77] has been returned to the cluster leader for edits`, got `[MARLO] AICCRA project P77 has been returned to the project leader for edits`); legacy `email.getStarted` → first attempt stayed **green** (footer assertions did not look at the legacy line) — `assertShellOnce` strengthened with a `To begin, please visit` count = 0, rerun → 3 red (`aicrra legacy get-started link expected:<0> but was:<1>`, aiccra3, …). Both reverted (`cmp` against backup).
- **Recipients / JSON:** only `sendMail.send(... subject, message.toString() ...)` → `(... email.subject, email.body ...)`; `toEmail`, `ccEmail`, `bbcEmails`, `mStatus` and `execute()` unchanged.
- **Not Done / Assumptions (Implementer):** (1) "PMC" assertion dropped from case (a) — the approved aicrra `unsubmit.email.message` (Appendix A.1) contains no "PMC"; asserted "cluster", "submit the cluster again", the quote block, no "Technical Implementation Team" instead; (2) base `getStarted` once, none for aicrra/aiccra3; (3) private `LOG`, no clash with `BaseAction`; (4) `globalUnitProject.get(0)` pre-existing, untouched; scratch files written to `/tmp` (outside repo). No owed item, no blocker: not a continuation.
- **Evidence re-run (Leader inline, non-author):** VERIFIED — `mvn -o -pl marlo-web -am test -Dtest=UnsubmitProjectEmailTest,ProjectSubmissionEmailTest,SubmitEmailCopyTest,EmailLayoutTest` exit 0, `Compiling 2430` / `1063` / `114`, `Tests run: 22, Failures: 0, Errors: 0`. Leader also confirmed `PMC` absent from aicrra `unsubmit.email.message` and present in Appendix A.1 only in the submit rows.
- **Reviewer verdict:** PASS (full) — whole email from one provider of the origin unit (`isActive() && isOrigin()`, DD-6/P-12); legacy footer keys gone; recipients, response, `validateEmailNotification` guard unchanged; `unitTexts` null-safe, slf4j, same key as T04; tests call the production method with real providers; strengthened `assertShellOnce` is meaningful (exact legacy `email.getStarted` text of aicrra/aiccra3/pabra).
- **runtime events:** none

- **spawns:** implementer 19 calls, 127803 tokens, ended partial (Not Done / Assumptions, no owed item); reviewer 10 calls, 65691 tokens, ended complete
- **ADVISORY (4R, non-gating):**
  - Risk: `{7}` (justification) and probably `{3}` (title) go raw into HTML, now inside `<blockquote><em>`; pre-existing, authenticated returners only; escaping belongs in a separate ticket (design §13 keeps it out of scope).
  - Reliability: `projectInfo.getTitle()` and `.get(0)` on the origin filter can throw before the email is built (pre-existing) — the submission is already saved, so an "ok" unsubmit would answer 500.
  - Readability: `LOG` is `static final` here, `static` in `ProjectSubmissionAction`.
  - Spec: record the AC FN-011 vs Appendix A.1 tension (below).
- **Spec tension (Pivot Detection, lightest form — raised at the T05 gate):** AC FN-011 (`requirements.md` ~L128-130) gives one THEN for submit and unsubmit: "subject and body use the AICCRA copy ("cluster", "PMC")"; Appendix A.1 `unsubmit.email.message` (~L220), required verbatim by FN-008 and AC FN-008/009 (~L149), contains no "PMC". Both cannot hold for unsubmit; the code follows the approved Appendix. Proposed one-line amendment pending owner answer.
- **Requirements covered:** FN-006, FN-008, FN-010, FN-011 (unsubmit).
- **Decisions made:** none beyond the Implementer's accepted assumptions; the tension's resolution is the owner's.
- **Issues encountered:** falsifier 2 initially did not bite — test strengthened within the attempt.
- **Final verification:** green (re-run VERIFIED, Reviewer PASS).
- **Budget running totals:** tasks 5/8; LOC ~809 changed (635 + 69 prod + 105 test) of ~950 (owner-adjusted); review rounds 5/10. Within budget.
- **Tension resolved (owner, 2026-11-11, T05 gate):** amendment accepted — `requirements.md` AC FN-011 THEN now reads ("cluster"; "PMC" in the submit copy only — the unsubmit copy is Appendix A.1 verbatim); `tasks.md` T05 (a) updated; Decision Log entry with closure sweep added. Execute-time spec edit: `requirements.md#ac-fn-011` — carried as a named conformance check in the next Reviewer brief (T06). Amended-doc diffs checked: the only removed lines are the two intended ones.

### CHG-EMAIL-LAYOUT-001-T06 — Opt-out blocks from the Q1 answer

- **Final status:** PASS
- **Date:** 2026-11-11
- **Implementer attempts:** 1
- **Skills / effort:** marlo-verify; Implementer effort low (XS, test only); Reviewer checklist (medium).
- **Q1 answer (quoted from `tasks.md` gate / `design.md` §14):** "Q1 answered 2026-11-11 — every Global Unit receives the shell, none opts out." → no unit custom file receives the DD-9 block; the task ships only the opt-out test.

#### Attempt 1
- **Files changed:** `marlo-web/src/test/java/org/cgiar/ccafs/marlo/utils/EmailLayoutTest.java` only (+47): helper `optOutUnit(Properties base)` and `testUnitThatCopiesTheDocumentedOptOutBlockSendsPlainEmailsWithSupportAndNoticeOnce`. The fixture reads the commented DD-9 lines (`# email.role.*`, `# login.otp.email.html=`) from `global.properties` (ISO-8859-1), uncomments them, adds `email.layout={0}`, layers over base; nothing added under `custom/`.
- **Red run:** block without `email.role.bye` → role `support address x2, do-not-reply notice x0` (admins `""` and `"Jane Admin"`), OTP `support x1, notice x0`; 9 run, 1 failure.
- **Implementer verification:** G-compile exit 0 (2430 / 1063 / 114); G-check delta 0; G-test `EmailLayoutTest,OtpMailSenderTest` 51 run, 0 failures; `git status`: only the test file; hygiene ok.
- **Falsifier (executed):** drop `email.role.bye` from the block → `role (admins=''): support x1, notice x0` (and `'Jane Admin'`), red; reverted.
- **Implementer deviations:** (1) role assembly uses `support.noCrpAdmins` when admins is empty and `support` otherwise, never both (T02's `checkFooterOnce` concatenates both, harmless there because both base values are empty); (2) OTP check asserts support once only — the documented pre-shell OTP copy has no notice.
- **Evidence re-run (Leader inline, non-author):** VERIFIED — `mvn -o -pl marlo-web -am test -Dtest=EmailLayoutTest,OtpMailSenderTest` exit 0, `Compiling 2430` / `1063` / `114`, `Tests run: 51, Failures: 0, Errors: 0`; `git status`: only `EmailLayoutTest.java`.
- **Reviewer verdict:** PASS (checklist) — deviation (1) matches real callers (`CrpUsersAction.java:589` uses only `.noCrpAdmins`; `ProjectPartnerAction.java:815`, `SendEmails.java:323, :442` only `.support`); fixture reads the real comment block (pattern matches only `global.properties` L3897-3900); scope test-only. Named conformance check `requirements.md#ac-fn-011` as amended 2026-11-11: meaning of FN-011 (L63) unchanged, no conflict.
- **runtime events:** none

- **spawns:** implementer 8 calls, 92309 tokens, ended complete; reviewer 15 calls, 52651 tokens, ended complete
- **ADVISORY (4R, non-gating):**
  - Readability: the fixture supplies `email.layout={0}` itself (DD-9 wording L3894 says so); a comment saying where `{0}` comes from would help.
  - Reliability: the fixture finds the block by line prefix; a later `# email.role.*` comment elsewhere in `global.properties` would be pulled in — scan could be bounded between "Opt-out block" and `email.layout=`.
- **Spec tension (Pivot Detection, lightest form — raised at the T06 gate):** AC FN-002 (`requirements.md` L105-109) requires the support address **and the do-not-reply notice** once each in "any of the five emails" of an opted-out unit (OTP included, AC FN-001 L100); the DD-9 block (FN-002 L54, design §15 DD-9) restores the pre-shell `login.otp.email.html` (`global.properties:3900`), which carries `{3}` and no notice. Both cannot hold for the OTP email. Reviewer options: (a) amend AC FN-002 — OTP of an opted-out unit needs the support address once, notice at most once (no behaviour change, matches the test); (b) add the notice to the documented OTP opt-out line and strengthen the test. Pending owner answer.
- **Requirements covered:** FN-002 (mechanism proven, no unit opts out), FN-005.
- **Decisions made:** Implementer deviations accepted.
- **Issues encountered:** none.
- **Final verification:** green (re-run VERIFIED, Reviewer PASS).
- **Budget running totals:** tasks 6/8; LOC ~856 changed (809 + 47) of ~950 (owner-adjusted); review rounds 6/10. Within budget.
- **Tension resolved (owner, 2026-11-11, T06 gate):** option (a) accepted — `requirements.md` AC FN-002 AND now requires, for the OTP email of an opted-out unit, the support address once and the notice at most once; `tasks.md` T06 tests line updated; Decision Log entry with closure sweep added (the tasks.md coverage row "AND support/notice still once" is a summary and stays as is). Execute-time spec edit: `requirements.md#ac-fn-002` — carried as a named conformance check in the T07 Reviewer brief. Amended-doc diffs checked: the only removed lines are the intended ones.

### CHG-EMAIL-LAYOUT-001-T07 — Docs: ai-context catalog and the OTP spec note

- **Final status:** PASS
- **Date:** 2026-11-11
- **Implementer attempts:** 1
- **Skills / effort:** cognitive-doc-design; Implementer effort low; Reviewer checklist (medium).
- **Review intensity:** `tasks.md` claims `skip-eligible`; **not earned** — condition 3 fails (`Consumers`: "agents reading the catalog for role-email work", not `none`), so a conformance Reviewer ran. Reported at the T07 gate.

#### Attempt 1
- **Files changed:** `reports/ai-context/role-assignment-email-catalog.md`; `docs/specs/changes/center-email-otp-login/requirements.md` (one Decision Log entry, §10). Diff: 2 files, +32 / −21.
- **Change:** catalog shell section — "Base" bullet now states `email.layout` in `global.properties` IS the shell (md5 `6b52db6bd9808a9361aa0d0e97c79985`, no custom file defines it, hardcoded `MARLOSupport@cgiar.org`, DD-10); new "Footer once" (DD-2) and "Opt-out" (DD-9, OTP-notice caveat per amended AC FN-002, no unit opts out — Q1) bullets; senders that wrap: 10 of 30 incl. submit/unsubmit (DD-6, DD-4); Rollback: per unit add the opt-out block, whole change revert the branch. Extra within the same catalog: §1 base-text table cells for `email.role.dear/support/support.noCrpAdmins/getStarted/bye` (described as copies of `email.*`, false after T01/T02) and the ciat/pabra/alliance paragraph (DD-3). OTP spec: DD-8 entry ("unit without a shell → bare body" now means "unit that opts out → bare body"; code path unchanged).
- **Red run (baseline):** `grep -n 'email.layout' reports/ai-context/role-assignment-email-catalog.md` → lines 33, 42, 45, 49 ("Base … `email.layout={0}`"), 69 (rollback to `{0}`).
- **Falsifier / Implementer verification:** after the edit `grep -n 'email.layout={0}'` on the catalog → L60 only (Opt-out bullet). Closure sweep `grep -rn 'email.layout={0}' reports docs/specs --include='*.md'` (glob quoted for zsh) → catalog L60 (current opt-out); `center-email-otp-login/requirements.md:184` (FN-017, historical) and `:483` (new DD-8 entry); all other hits in this spec's own files.
- **Not Done / Assumptions (Implementer):** OTP spec FN-017 (L184) and AC-20 (L356) left as written (historical; the DD-8 entry says so); "10 of 30" taken from P-14. No owed item, no blocker: not a continuation.
- **Evidence re-run (Leader inline, non-author):** VERIFIED — catalog grep → L60 only; sweep files: catalog, OTP requirements, this spec only; recount `grep -rl 'EmailLayout\.wrap' marlo-web/src/main/java` → 11 files, 10 of them among the 30 senders (`sendMail.send` / `sendMailS.send` / `sendMail.sendTemporal`) → "10 of 30" holds.
- **Reviewer verdict:** PASS (checklist) — every factual claim checked against design §15/§16 and the tree (`global.properties` L3885-3901, L7038; alliance L2689; no `email.role.*` in ciat/pabra; aicrra/aiccra3 keep five overrides); §1 table edit in scope (P-9 names the catalog as a consumer to update; corrects the named document, no new deliverable); DD-8 entry matches the §10 format, no other edit; stale FN-017/AC-20 are historical records allowed by Done-when; no shared/constitutional file touched. Named conformance check `requirements.md#ac-fn-002` as amended: FN-002's meaning unchanged, catalog consistent.
- **runtime events:** none

- **spawns:** implementer 9 calls, 94030 tokens, ended partial (Not Done / Assumptions, no owed item); reviewer 14 calls, 59761 tokens, ended complete
- **ADVISORY (4R, non-gating):**
  - Readability: catalog L36–40 (outside the diff, "As of A2-2484") still says submissions call the original `email.*` keys; after DD-4 submit/unsubmit read `email.role.*` — add a one-clause note when the catalog is next touched (the shell section L68–70 already states it).
- **Requirements covered:** FN-002 (documented opt-out), DD-8.
- **Decisions made:** §1 table correction accepted as in scope (Reviewer).
- **Issues encountered:** the task's sweep command `--include=*.md` fails unquoted in zsh — run quoted.
- **Final verification:** green (re-run VERIFIED, Reviewer PASS).
- **Budget running totals:** tasks 7/8; LOC ~909 changed (856 + 53) of ~950 (owner-adjusted); review rounds 7/10. Within budget; T08 is verification (no LOC expected beyond `execution.md`).

### CHG-EMAIL-LAYOUT-001-T08 — Live verification on a local copy, with the human render check

- **Final status:** PASS
- **Date:** 2026-11-11
- **Implementer attempts:** 2 (attempt 1 Reviewer FAIL on evidence gaps; attempt 2 PASS)
- **Skills / effort:** marlo-verify; attempt 1 effort high, attempt 2 xhigh (bump on FAIL). Reviewer checklist both rounds.
- **Environment pre-check (Leader, before any spawn):** MySQL up (socket); `crp_email_support_team` = true for AICCRA (45), AICCRA_III (47), QATEST (50) in `aiccradb_actsave`; port 8080 free, no cargo/Maven running; `email.notification` = the owner's own address.

#### Attempt 1
- **Setup:** throwaway DB `aiccradb_t08_email` copied from `aiccradb_actsave`; pre-send check repeated in the copy (Disqualifier) — true for 45/47/50; app built and run from worktree `../MARLO-wt-email-layout` at `c24addcdde` (`package -DskipTests`, `cargo:run`, default ports); copy's `email_logs` max id 18400 → new rows 18401–18425, all to the notification address. App driven over HTTP (curl, the same form posts as the browser) and Playwright for login (credentials served by a one-shot 127.0.0.1 server, never in the transcript).
- **Copy-only data changes:** `system_otp_login_active=true` for 45/47/50 (OTP request refuses otherwise); three throwaway Guest users; submissions 4996–5003 created, then unsubmitted.
- **Records used:** planning submit/unsubmit — QATEST 102101/501, AICCRA 102076/429, AICCRA_III 102093/444 (+102095/444); reporting — QATEST 102101/503, AICCRA 102076/431, AICCRA_III 102093/446. Role + welcome (`crpUsers.do`) and OTP (`otpRequestCode.do`) per unit.
- **Query A (shell / footer / wording; Leader re-run):** marker = `message` contains `global/images/email/marlo-logo.png` AND `email-band.png`. **Support count = `supp_raw − mailto`**: the shell prints `MARLOSupport@cgiar.org` as the `mailto:` href and once as visible text, so `supp_raw 2` / `mailto 1` means the address is shown once (not a falsifier hit). `notice` = lower-case `do not reply`; `tit` = "Technical Implementation Team".
  ```
  id     unit       src             shell supp_raw mailto notice tit flagship
  18401  QATEST     crpUsers role   1     2        1      1      0   0
  18402  QATEST     crpUsers welc   1     2        1      1      0   0
  18403  AICCRA     crpUsers role   1     2        1      1      0   0
  18404  AICCRA     crpUsers welc   1     2        1      1      0   0
  18405  AICCRA_III crpUsers role   1     2        1      1      0   0
  18406  AICCRA_III crpUsers welc   1     2        1      1      3   0   (AICCRA_III own copy)
  18407  QATEST     otp             1     2        1      1      0   0
  18408  AICCRA     otp             1     2        1      1      0   0
  18409  AICCRA_III otp             1     2        1      1      0   0
  18410  QATEST     submit          1     2        1      1      0   0
  18411  QATEST     unsubmit        1     2        1      1      0   0
  18412  AICCRA     submit          1     2        1      1      0   0
  18413  AICCRA     unsubmit        1     2        1      1      0   0
  18414  AICCRA_III submit          1     2        1      1      4   0
  18415  AICCRA_III unsubmit        1     2        1      1      2   0
  18416-18425 (reporting submits/unsubmits, extra AICCRA_III and QATEST pairs): shell 1, supp_raw 2, mailto 1, notice 1, flagship 0; tit 0 on QATEST/AICCRA, 2-4 on AICCRA_III
  ```
- **Query B (summary link; Leader re-run):** all `has_bad` (`projectID=102080` or `phaseID=407`) = 0.
  ```
  18410 QATEST     projectID=102101&phaseID=501  Full_Cluster_Report-QATEST-C102101-20261111-1550.pdf
  18412 AICCRA     projectID=102076&phaseID=429  Full_Cluster_Report-AICCRA-C102076-20261111-1551.pdf
  18414 AICCRA_III projectID=102093&phaseID=444  Full_Cluster_Report-AICCRA_III-C102093-20261111-1552.pdf
  18416 QATEST     projectID=102101&phaseID=503  (reporting, no attachment)
  18417 AICCRA     projectID=102076&phaseID=431  (reporting, no attachment)
  18418 AICCRA_III projectID=102093&phaseID=446  (reporting, no attachment)
  18422 AICCRA_III projectID=102095&phaseID=444  pdf ; 18423 QATEST projectID=102101&phaseID=501 pdf
  ```
- **Query C (PDF wording; Leader re-run):** "… summary is attached as a PDF" present exactly on the rows with `file_name` (18410, 18412, 18414, 18422, 18423); absent on reporting submits and on every unsubmit.
- **FN-011 sequence (Implementer account):** AICCRA_III page loaded, then AICCRA submit (18412: "cluster", "PMC", tit 0) and unsubmit (18413: "cluster", no PMC per Appendix A.1, tit 0); AICCRA page loaded, then AICCRA_III submit (18414, TIT/PIU copy) and unsubmit (18415). The page-load ordering is the Implementer's account, not a logged fact (Reviewer advisory).
- **FN-003:** AICCRA role/welcome 18403/18404 against pre-change 18300/18301 (2026-10-09), tag-stripped text diff: only name, user email and password differ. AICCRA OTP and AICCRA_III: no pre-change email exists; covered by the T01 md5 (`6b52db6bd9808a9361aa0d0e97c79985`) plus the owner's render review — owner acceptance quoted below.
- **FN-007 failed PDF:** `.noPDF` path not reproducible live (the attachment never fails to be produced locally); covered by T04 unit case (b).
- **Evidence re-run (Leader inline, non-author):** VERIFIED — Queries A–C re-run against the copy, same values.
- **Reviewer verdict:** FAIL (checklist). Findings (verbatim, condensed to the two issues):
  1. NF-003 had neither a quoted result nor an owner verdict ("EmailPlainText.fromHtml on stored HTML reads as sentences" was an Implementer prose claim, not re-run). Violated: `tasks.md` T08 Review ("every AC row has a quoted query result or owner verdict"); `requirements.md` §6 AC NF-002/NF-003. Remediation: run `EmailPlainText.fromHtml` on submit (PDF), submit (reporting) and unsubmit rows per unit; quote the output.
  2. FN-010 evidence did not support "unchanged": only `to_email` = notification and `cc_email` NULL quoted; no before/after for `subject`, `bbc_email`, attachment name. Violated: `requirements.md` §6 AC FN-010. Remediation: quote pre-change submit/unsubmit rows per unit, compare subject template and attachment-name pattern, classify each difference; record TO/CC/BCC as covered by the T04/T05 diff review plus the local gap (`isRestrictedToSupport`).
- **runtime events:** none

#### Attempt 2 (rework; no re-send, no app start)
- **NF-003:** production `EmailPlainText.fromHtml` run over the stored `message` of nine rows. Leader re-run of the mechanical check (`markup` = lines matching `<[a-zA-Z/]` or `&[a-z]+;`; `bracedigit` = `\{[0-9]`; `link` = `reportingSummary.do?projectID=`):
  ```
  18410 markup=0 bracedigit=0 link=1   QATEST submit (PDF)
  18412 markup=0 bracedigit=0 link=1   AICCRA submit (PDF)
  18414 markup=0 bracedigit=0 link=1   AICCRA_III submit (PDF)
  18416 markup=0 bracedigit=0 link=1   QATEST submit (reporting)
  18417 markup=0 bracedigit=0 link=1   AICCRA submit (reporting)
  18418 markup=0 bracedigit=0 link=1   AICCRA_III submit (reporting)
  18411 markup=0 bracedigit=0 link=0   QATEST unsubmit
  18413 markup=0 bracedigit=0 link=0   AICCRA unsubmit
  18415 markup=0 bracedigit=0 link=0   AICCRA_III unsubmit
  ```
  Excerpt 18410: "MARLO SuperAdmin has submitted the QATEST project "QA Test Cluster Project" (C102101) for review in the 2026 planning cycle. … The project summary is attached as a PDF. You can also open it in MARLO (…/projects/QATEST/reportingSummary.do?projectID=102101&phaseID=501) or generate it at any time from the Summaries section." Footer once in every output.
- **FN-010 (Leader re-run of the pre-change rows):**
  ```
  17500 2024-10-28 Alliance-AICCRA-KDS@cgiar.org NULL NULL [AICCRA] AICCRA cluster [Senegal] has been submitted  Full_Cluster_Report-AICCRA-C102076-20241028-2014.pdf
  17894 2025-07-11 Alliance-AICCRA-KDS@cgiar.org NULL NULL [AICCRA] AICCRA_III cluster [C102093] has been submitted  Full_Cluster_Report-AICCRA_III-C102093-20250711-2030.pdf
  17895 2025-07-11 Alliance-AICCRA-KDS@cgiar.org NULL NULL [AICCRA] AICCRA_III cluster [C102093] has been returned to the cluster leader for edits  NULL
  18199 2025-12-17 Alliance-AICCRA-KDS@cgiar.org NULL NULL [AICCRA] AICCRA cluster [EA] has been returned to the cluster leader for edits  NULL
  18393 2026-10-29 <notification> NULL NULL [MARLO] QATEST project C102102 has been submitted  Full_Cluster_Report-QATEST-C102102-20261029-0010.pdf
  18395 2026-10-29 <notification> NULL NULL [MARLO] QATEST project C102102 has been returned to the project leader for edits  NULL
  ```
  - **Subject:** AICCRA identical before/after apart from the project (17500 vs 18412, 18199 vs 18413); QATEST identical apart from the project id. AICCRA_III "cluster" → "Technical Implementation Team" is **not this spec**: `git show 460ecbd5be:…/custom/aiccra3.properties` already carries "Technical Implementation Team" in `submit.email.subject` / `unsubmit.email.subject`, introduced by `a304236b35` (2026-09-28); `git diff 460ecbd5be c24addcdde` changes 0 subject lines in `.properties`.
  - **Attachment name:** pattern `Full_Cluster_Report-<unit>-C<id>-<yyyyMMdd-HHmm>.pdf` identical; 0 changed Java lines building the file name.
  - **TO / CC / BCC — checked against the code diff, not live.** Locally `SendMailS.send()` replaces them (`isRestrictedToSupport()` → to = notification, cc = null, bcc = null) because `crp_email_support_team` is true; the QATEST pair shows the same replacement on both sides, so identical local recipient values are **not** proof of the real ones. The real rules rest on the diff: in both actions only the `sendMail.send(...)` subject/body arguments changed (T04 entry "envelope untouched"; T05 entry "Recipients / JSON … unchanged").
- **Evidence re-run (Leader inline, non-author):** VERIFIED — mechanical check over the nine outputs, pre-change rows, base-commit subject lines and `a304236b35` re-run; worktree `git status` 0 lines.
- **Reviewer verdict:** PASS (checklist) — both issues closed; no new problem; "Dear ," in 18415 comes from `UnsubmitProjectAction` lines outside the spec diff and also appears pre-change (17895, 18395).
- **runtime events:** none

- **Owner verdicts (HITL, 2026-11-11):** render check of the 25 exported emails — "1 aprobado"; FN-003 for AICCRA OTP and AICCRA_III accepted on the T01 md5 plus the render review — "2 y 3 sí"; deferral of the live findings below to separate work — "2 y 3 sí".
- **Live findings, deferred (owner, 2026-11-11 — not in this spec):**
  - **Empty PDF reproduced live.** The local microservice is unreachable (RabbitMQ connect timeout, ~60 s per planning submit); `ReportingSummaryAction` swallows the error and `getBytesPDF()` returns an empty array, so the email attaches a 0-byte `Full_Cluster_Report-…pdf` and says "attached". Leader-confirmed: the summary link downloaded as HTTP 200 `application/pdf`, 0 bytes. Same item as §"Deferred (owner decision at the T04 gate)", now observed live.
  - **"null" title.** 18422/18424 (project 102095, `projects_info.title` NULL in phase 444) render `"null"`; the `getTitle()` arguments are unchanged since `460ecbd5be`. Proposed together with the empty PDF as one bugfix of the submit/unsubmit emails.
  - **`email_logs.fileContent` NULL** on attachment rows since at least 2026-10-09 (also pre-change 18301, 18393); separate investigation.
  - **"Dear ,"** in unsubmit emails without a leader name — pre-existing (Reviewer advisory).
- **DoD gates (Leader, worktree at `c24addcdde`, after the app stopped):** clean compile 2430 / 1063 / 114; full `mvn -o -pl marlo-web -am test` exit 0 — `Tests run: 1125, Failures: 0, Errors: 0, Skipped: 0`; Checkstyle via the marlo-verify script, branch vs `460ecbd5be`: `ProjectSubmissionAction` 1/1, `UnsubmitProjectAction` 2/2, all other changed files 0/0 → delta 0 (a first direct-jar run reported 0 everywhere because the jar failed to start on a missing picocli class — discarded); G-i18n: 43 added `.properties` lines, 0 single apostrophes, 0 non-argument braces.
- **Cleanup:** cargo stopped, Playwright closed; `DROP DATABASE aiccradb_t08_email` (schemas left: aiccradb1-4, aiccradb_actsave, aiccradb_wt_test — the last belongs to another session, untouched); `git worktree remove ../MARLO-wt-email-layout` (worktree list: main checkout only).
- **spawns:** implementer 120 calls, 226428 tokens, ended partial (Not Done / Assumptions: owner verdict pending, uncovered ACs — resolved by owner answers and attempt 2); reviewer 10 calls, 53809 tokens, ended complete (FAIL); implementer 25 calls, 129412 tokens, ended complete; reviewer 8 calls, 41860 tokens, ended complete (PASS)
- **ADVISORY (4R, non-gating):**
  - Risk: FN-010 TO/CC/BCC are code-diff evidence, not live (recorded above).
  - Reliability: the FN-011 page-load ordering is the Implementer's account; access-log timestamps would make it evidence.
  - Reliability: "Dear ," on unsubmit without a leader name is pre-existing; track as its own bugfix if wanted.
- **Requirements covered:** FN-001, FN-003 (with owner acceptance), FN-005 (live for base/aicrra/aiccra3), FN-006, FN-007 (link, both cycles; failed PDF by T04 test), FN-010, FN-011, NF-003.
- **Decisions made:** owner accepted FN-003 evidence basis; owner deferred the live findings; T08 left the copy-only data changes in the throwaway DB, dropped with it.
- **Issues encountered:** first Checkstyle run gave a false green (jar classpath) — redone with the marlo-verify script; the Implementer's abridged table attributed `tit=3` to 18405 instead of 18406 — corrected by the Leader re-run.
- **Final verification:** green (re-runs VERIFIED, Reviewer PASS, owner render approved).
- **Budget running totals:** tasks 8/8; LOC ~909 (no code in T08) of ~950 (owner-adjusted); review rounds 9/10. Within budget.

## Summary

- **Status:** all 8 tasks PASS (2026-11-11). No HALT, no Pivot; two spec tensions resolved by amendment (AC FN-011 at T05, AC FN-002 at T06); budget raised once by the owner (LOC ~800 → ~950 after T04), finished at ~909 LOC, 9 of 10 review rounds.
- **Delivered:** the branded shell is the base `email.layout` for every unit (none opts out, Q1); footer once in the base and the two overriding units; submit/unsubmit copy rewritten with the summary link, sent from the submitting unit's own texts inside the shell; opt-out block documented and tested; ai-context catalog and the OTP spec note updated.
- **Definition of Done:** all tasks with evidence; compile, Checkstyle delta, i18n and the full suite (1125 tests) green on `c24addcdde`; owner render approval recorded (T08); Q1 quoted (T06).
- **Out of this spec (owner, 2026-11-11):** empty PDF from the microservice path + "null" title → one bugfix proposal for the submit/unsubmit emails; `email_logs.fileContent` NULL → separate investigation; "Dear ," on unsubmit without a leader → optional bugfix.
- **Next:** `/akili-test`, `/akili-validate`, `/akili-archive`.
