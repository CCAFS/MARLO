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
