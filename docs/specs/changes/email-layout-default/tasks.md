# Branded email shell by default — Tasks

**Spec ID:** CHG-EMAIL-LAYOUT-001
**Status:** Approved 2026-11-11
**Owner:** Kenji Tanaka (IBD)
**Last Updated:** 2026-11-11
**Requirements:** `requirements.md` · **Design:** `design.md` (JUDGMENT: APPROVED, `judgment.md`)
**Branch:** `email-layout-default` from `staging` `460ecbd5be`
**Gate before T1:** Q1 answered (production active Global Units) — design §14, P-11.

> **Answer first.** Eight tasks: three on i18n data (shell, footer, copy), two on the actions (submit, unsubmit),
> one on the per-unit opt-out list from Q1, one on docs, one live verification. Every task carries the compile and
> Checkstyle gates; the visual defect class has only a human check (T8).

## Shared gates (cited by every task as "Gates")

| Gate | Command (failure-only form) |
|---|---|
| G-compile | `rm -rf marlo-web/target/classes marlo-web/target/test-classes marlo-data/target/classes && mvn -q -o -pl marlo-data,marlo-web -am test-compile -DskipTests` — count `Compiling N source files` (~2400 / ~1040), read Maven's own exit code |
| G-check | checkstyle 8.18 jar with `-c configuration/marlo-checkstyle.xml` on the changed `.java` files, compared with the same files at HEAD; only the delta counts |
| G-test | `mvn -q -o -pl marlo-web -am test -Dtest=<classes> -Dsurefire.failIfNoSpecifiedTests=false` (keep `-am`) |
| G-i18n | Every added/changed `.properties` line reviewed: no single `'`, no `{` that is not an argument (marlo-verify) |

All gates run after the Implementer reports, never beside it (CLAUDE.md Concurrency). Build in a worktree, never in
the main checkout.

---

### CHG-EMAIL-LAYOUT-001-T01 — The shell becomes the base value
- **Status:** pending · **Size:** S · **Depends on:** Q1 gate · **Module:** marlo-web (resources, utils Javadoc, tests)
- **Requirements:** FN-001, FN-002 (AC: shell for a file-less unit), FN-004, FN-003 (AICCRA/AICCRA_III inherit an identical shell)
- **Design:** §2 resources rows 1-3, DD-1, DD-8 (comment only), DD-10
- **Scope:**
  - `global.properties`: `email.layout` = the shell, moved verbatim from `custom/aicrra.properties`; rewrite the comment above it (default + how to opt out, the DD-9 block kept there verbatim as the old base values).
  - Delete `email.layout` from `custom/aicrra.properties`, `aiccra3.properties`, `test.properties`; update the three "shell of email.layout above" comments (aicrra ~3210, aiccra3 ~3232, test ~3307).
  - `EmailLayout.java` and `OtpMailSender.java`: Javadoc/comment wording (default + opt-out); no logic.
  - Tests: `EmailLayoutTest.testBaseLayoutSendsTheBodyAsItIs` → base wraps; new opt-out fixture (`email.layout={0}` over base → body as is); `UnitTextProviderTest.aMissingNullOrUnsafeFileNameLeavesTheUnitOnGlobalOnly` and `twoProvidersAreIndependentAndHoldNoSharedState` → base shell; `OtpRequestCodeActionTest.aUnitWithoutACustomFileGetsTheGlobalTextsAndTheBareShell` → base shell; `OtpMailSenderTest.aUnitWithoutAShellSendsTheBareBodyAsBefore` → opt-out provider; `OtpMailSenderTest.theRenderedEmailStatesEveryRequiredElement` → its "no http" check scoped to the body, not the shell's image URLs.
  - Backend route: none — project conventions (i18n data + tests).
- **Tests:** G-test `EmailLayoutTest,UnitTextProviderTest,OtpMailSenderTest,OtpRequestCodeActionTest,EmailPlainTextTest`.
- **Falsifier:** remove the new base `email.layout` line (back to `{0}`) → the rewritten `testBaseLayoutSendsTheBodyAsItIs` and the UnitTextProvider/OtpRequestCode base-shell assertions go red; set `email.layout={0}` in the opt-out fixture to the shell → the opt-out test goes red.
- **Red run:** observed at baseline 2026-11-11: `grep -c '^email.layout=' global.properties custom/*.properties` → global 1, aiccra3 1, aicrra 1, test 1, and `grep '^email.layout=' global.properties` → `email.layout={0}`. The rewritten tests: `predicted — greenfield artifact (observed in T01)` — T01 runs them against the unchanged properties first and quotes the red in `execution.md`.
- **Disqualifier:** a green suite while `grep -c '^email.layout=' custom/{aicrra,aiccra3,test}.properties` still returns 1 for any file proves nothing (the custom copy masks the base); an incremental compile that compiled far fewer than ~1040 marlo-web files is not evidence.
- **Consumers:** `EmailLayoutTest`, `UnitTextProviderTest`, `OtpMailSenderTest`, `OtpRequestCodeActionTest`, `EmailPlainTextTest` (P-9); `OtpMailSender` and `OtpRequestCodeAction` (read the base through `UnitTextProvider`).
- **Review:** checklist — data move plus test updates; the shell markup must be byte-identical (Reviewer compares md5 of the block before/after: `6b52db6b…`).
- **Done when:** base defines the shell; no custom file defines `email.layout`; md5 of the moved block equals `6b52db6b…`; the five test classes green; G-compile, G-check (no Java logic change → delta 0), G-i18n pass.
- **Skills:** marlo-verify, marlo-commit.

### CHG-EMAIL-LAYOUT-001-T02 — Footer-once in the base file and the two overriding units
- **Status:** pending · **Size:** S · **Depends on:** T01 · **Module:** marlo-web (resources, tests)
- **Requirements:** FN-005 (AC FN-005, every listed unit and the OTP email), FN-003 (AICCRA/AICCRA_III unchanged), NF-002
- **Design:** DD-2, DD-3, DD-10, Appendix A.4/A.5 (requirements)
- **Scope:**
  - `global.properties`: `email.role.dear` (bold paragraph), `email.role.support` = empty, `email.role.support.noCrpAdmins` = empty, `email.role.getStarted` (paragraph), `email.role.bye` ("Thank you! / Kind regards, MARLO Team") — the AICCRA values; `login.otp.email.html` without "Need help? Contact {0} support at {3}."
  - `custom/ciat.properties`: delete `email.role.support`, `email.role.bye`. `custom/pabra.properties`: delete `email.role.support`, `email.role.support.noCrpAdmins`, `email.role.getStarted`. `alliance.properties` untouched.
  - Tests: replace `EmailLayoutTest.testBaseRoleBlocksMatchTheSharedOnes` with "base role blocks are shell-aware; shared `email.*` blocks unchanged"; new footer-once test: for the base and every custom file that overrides an `email.role.*` key (computed by scanning `custom/*.properties`, not a fixed list), assemble dear + support + support.noCrpAdmins + getStarted + bye inside the resolved shell and count `MARLOSupport@cgiar.org` and the do-not-reply notice = 1 each; same count for the OTP html of the base and aicrra; `OtpMailSenderTest.everyDynamicValueIsHtmlEscapedInTheHtmlPartOnly` → the escaped `{3}` is no longer expected in the base html part (still in the text part).
  - Backend route: none — project conventions.
- **Tests:** G-test `EmailLayoutTest,OtpMailSenderTest,EmailPlainTextTest,UnitTextProviderTest`.
- **Falsifier:** restore pabra's `email.role.support.noCrpAdmins` override → the footer-once test goes red for pabra (count 2); re-add "Need help…" to the base OTP html → red for the base OTP count. The test scans the folder, so a new override file is covered without editing the test.
- **Red run:** observed at baseline: `grep -c -E '^email\.role\.(support|support\.noCrpAdmins|bye)=.*(MARLOSupport|Please do not reply)' global.properties` → 3; `grep -c '^login.otp.email.html=.*Need help' global.properties` → 1; ciat overrides `email.role.support`, `email.role.bye`; pabra overrides `email.role.support`, `.support.noCrpAdmins`, `.getStarted`. Footer-once test: `predicted — greenfield artifact (observed in T02)` — written and run before the property edits, red quoted in `execution.md`.
- **Disqualifier:** a footer-once test that iterates a hard-coded file list is not evidence (it cannot see a new override); a count taken on raw property text instead of the assembled, `MessageFormat`-formatted email is not evidence.
- **Consumers:** every wrapped sender reading `email.role.*` (7 actions + `SendEmails`, P-10); `OtpMailSender` (`login.otp.email.html`); `EmailLayoutTest`, `OtpMailSenderTest`, `EmailPlainTextTest`.
- **Review:** full — the change alters the visible footer of every unit's role emails; the Reviewer checks AICCRA/AICCRA_III keys are untouched (`git diff` shows no aicrra/aiccra3 `email.role` line).
- **Done when:** footer-once test green for base + every overriding unit + OTP; AICCRA/AICCRA_III `email.role.*` lines unchanged; G-i18n; G-test green.
- **Skills:** marlo-verify, marlo-commit.

### CHG-EMAIL-LAYOUT-001-T03 — Submit/unsubmit copy (Appendix A) and the summary link in the text
- **Status:** pending · **Size:** S · **Depends on:** T02 · **Module:** marlo-web (resources, tests)
- **Requirements:** FN-007 (text side: `{6}` in every rewritten key and in pabra), FN-008, FN-009, NF-002, NF-003
- **Design:** DD-5 (keys consuming `{6}`), Appendix A.1-A.3
- **Scope:**
  - `aicrra.properties`, `aiccra3.properties`: `submit.email.message`, `submit.email.message.noPDF`, `unsubmit.email.message` per Appendix A.1/A.2 (`<p style="margin:0 0 16px 0;">` paragraphs, quote block for `{7}`, link `<a href="{6}">`), no apostrophes.
  - `global.properties`: the same three keys per A.3 (no "Flagship", no "PMU").
  - `pabra.properties`: both submit keys — hardcoded link replaced by `{6}`, wording otherwise unchanged.
  - New copy test: for aicrra, aiccra3, pabra and base, format each key with sentinel arguments `{0}`..`{7}` → every sentinel present, `{6}` sentinel inside `href`, no `102080`/`407`, no `<br><br>` in the six rewritten keys, no "Flagship" in base; the text part (EmailPlainText) of each formatted body reads without markup.
  - Backend route: none — project conventions.
- **Tests:** G-test `<new copy test>,EmailLayoutTest,EmailPlainTextTest`.
- **Falsifier:** put the hardcoded URL back into aiccra3's `.noPDF` → copy test red ("102080 present", sentinel `{6}` missing); add a single apostrophe to a base key → the sentinel/format check red.
- **Red run:** observed at baseline: `grep -l 'projectID=102080' custom/*.properties` → aicrra, aiccra3, pabra (2 keys each); `grep -c -E '^(submit|unsubmit)\.email\.message.*Flagship' global.properties` → 2. Copy test: `predicted — greenfield artifact (observed in T03)`.
- **Disqualifier:** checking the raw property text for `{6}` instead of the formatted output is not evidence (a `'{6}'` is literal after MessageFormat); a copy diff against Appendix A done by eye only is not evidence for FN-008 — the test or a scripted diff is.
- **Consumers:** `ProjectSubmissionAction` (`submit.email.message`, `.noPDF`), `UnsubmitProjectAction` (`unsubmit.email.message`); every unit's submit email through `.noPDF` `{6}` (DD-5).
- **Review:** full — user-facing copy approved at the requirements gate; Reviewer diffs each key against Appendix A.
- **Done when:** nine keys match Appendix A (scripted diff of the formatted text); pabra has no hardcoded link; copy test green; G-i18n.
- **Skills:** marlo-verify, marlo-commit.

### CHG-EMAIL-LAYOUT-001-T04 — Submit email: own unit's texts, real link, honest PDF wording, shell
- **Status:** pending · **Size:** M · **Depends on:** T03 · **Module:** marlo-web (`action/projects/ProjectSubmissionAction.java`, test)
- **Requirements:** FN-006, FN-007 (AC FN-007 both cycles + failed PDF), FN-010, FN-011 (AC FN-011 submit cases)
- **Design:** DD-4, DD-5, DD-6, P-3, P-4, P-12
- **Scope:**
  - Inject `CustomParameterManager`; build a `UnitTextProvider` from `loggedCrp`'s `crp_custom_file` (WARN + fall back to `this` on failure, as `OtpRequestCodeAction.unitTexts`).
  - Resolve the subject (`:291`), body and footer with that provider; `{6}` computed in both cycles (`:304-307`); choose `submit.email.message` only when a PDF buffer exists after generation (`:327-352`), else `.noPDF`; footer = `email.role.support.noCrpAdmins` + `email.role.bye`; body wrapped with `EmailLayout.wrap(texts, getBaseUrlCdn(), body)` at both sends (`:359`, `:362`).
  - Extract the email assembly (texts, values, pdfAttached → subject and wrapped body) into a package-private static method so it is testable without Hibernate.
  - Backend route: none — project conventions; skills: error-handling-patterns not applicable (no save path change).
- **Tests:** new `ProjectSubmissionEmailTest` on the extracted method with real `UnitTextProvider`s: (a) aicrra, PDF attached → AICCRA planning copy with `{6}`; (b) aicrra, no PDF → `.noPDF` copy, no "attached"; (c) aiccra3 provider → "Technical Implementation Team" in subject and body; (d) provider for a missing file → base copy inside the shell; (e) footer once. G-test `ProjectSubmissionEmailTest`.
- **Falsifier:** make the method use a fixed `aicrra` provider → case (c) red; select the variant before PDF generation (today's order) → case (b) red; drop the `wrap` call → (d) red (no shell marker).
- **Red run:** observed at baseline: `grep -c EmailLayout ProjectSubmissionAction.java` → 0; `email_logs` 18405 (local, 2026-11-11) — AICCRA_III submit sent with AICCRA wording. Test: `predicted — greenfield artifact (observed in T04)` — written against today's assembly first and its red quoted.
- **Disqualifier:** a test that builds its own provider and never calls the extracted production method asserts nothing; a green test with mocks of `getText` that ignore the key is not evidence (assert the key/arguments received — KZ-…-auth-flow-2).
- **Consumers:** `{crp}/submit` (`struts-projects.xml:1140`); `ReportingSummaryAction` (PDF) unchanged; `email_logs` readers.
- **Review:** full — Java logic on a user flow; Reviewer checks recipients/attachment code paths untouched (FN-010).
- **Done when:** five test cases green; FN-010 envelope code (to/cc/bcc/attachment) unchanged in the diff; G-compile, G-check delta 0 on the file, G-test.
- **Skills:** marlo-verify, tdd, marlo-commit.

### CHG-EMAIL-LAYOUT-001-T05 — Unsubmit email: own unit's texts, role footer, shell
- **Status:** pending · **Size:** M · **Depends on:** T03 · **Module:** marlo-web (`action/json/project/UnsubmitProjectAction.java`, test)
- **Requirements:** FN-006, FN-008, FN-010, FN-011 (AC FN-011 unsubmit case)
- **Design:** DD-4, DD-6, P-3, P-12
- **Scope:**
  - Inject `CustomParameterManager`; `UnitTextProvider` from the project's origin unit (`:251`) `crp_custom_file`; WARN + `this` on failure.
  - Subject (`:261`), body (`:282`) and footer (`email.role.support.noCrpAdmins`, `email.role.getStarted`, `email.role.bye` replacing `:283-285`) from that provider; wrap at the send (`:289`).
  - Extract the assembly into a package-private static method, as T04.
  - Backend route: none — project conventions.
- **Tests:** new `UnsubmitProjectEmailTest`: (a) aicrra provider → "cluster", "PMC", justification in the quote block; (b) aiccra3 → "Technical Implementation Team"; (c) missing file → base copy in the shell; (d) footer once; (e) subject from the same provider as the body. G-test `UnsubmitProjectEmailTest`.
- **Falsifier:** resolve the subject with a different provider than the body → (e) red; keep `email.getStarted` (legacy) → (d) red for a unit whose legacy key carries a URL.
- **Red run:** observed at baseline: `grep -c EmailLayout UnsubmitProjectAction.java` → 0; `email_logs` 18407 (local) — AICCRA project unsubmitted with AICCRA_III wording. Test: `predicted — greenfield artifact (observed in T05)`.
- **Disqualifier:** as T04.
- **Consumers:** `unsubmitProject.do` (`struts-json.xml:678-685`), `projectSubmit.js` (response unchanged), `email_logs` readers.
- **Review:** full — Java logic on a user flow.
- **Done when:** five cases green; JSON response and recipients unchanged in the diff; G-compile, G-check delta 0, G-test.
- **Skills:** marlo-verify, tdd, marlo-commit.

### CHG-EMAIL-LAYOUT-001-T06 — Opt-out blocks from the Q1 answer
- **Status:** pending · **Size:** XS · **Depends on:** T02, Q1 · **Module:** marlo-web (resources)
- **Requirements:** FN-002 (AC FN-002), FN-005
- **Design:** DD-9, §14
- **Scope:** for each production unit the owner lists as "must stay plain" in Q1, add the DD-9 block to its custom file (`email.layout={0}` + pre-shell `email.role.support`, `.support.noCrpAdmins`, `.bye`, `login.otp.email.html`). If the answer lists none, the task closes with that answer quoted and no file change.
  - Backend route: none — project conventions.
- **Tests:** extend T02's footer-once test with an opt-out case: a unit file carrying the block → body as is, support and notice once each. G-test `EmailLayoutTest`.
- **Falsifier:** an opt-out block without `email.role.bye` → the opt-out case red (no notice).
- **Red run:** `predicted — greenfield artifact (observed in T06)`.
- **Disqualifier:** an opt-out decided without the owner's Q1 answer is not evidence of FN-002 scope.
- **Consumers:** the listed units' role, welcome, OTP, submit and unsubmit emails.
- **Review:** checklist — data only, values copied from the base comment written in T01.
- **Done when:** Q1 answer quoted in `execution.md`; each listed unit has the full block; opt-out test green; G-i18n.
- **Skills:** marlo-verify, marlo-commit.

### CHG-EMAIL-LAYOUT-001-T07 — Docs: ai-context catalog and the OTP spec note
- **Status:** pending · **Size:** XS · **Depends on:** T01, T02 · **Module:** docs
- **Requirements:** FN-002 (documented opt-out), design DD-8
- **Design:** §2 Docs, P-9
- **Scope:** `reports/ai-context/role-assignment-email-catalog.md` lines ~33-69 → shell is the base value, opt-out block, rollback = set the opt-out block; `docs/specs/changes/center-email-otp-login/requirements.md` → one Decision Log entry (DD-8). No shared file of the CLAUDE.md list is touched.
  - Backend route: none — documentation.
- **Tests:** n/a — documentation.
- **Falsifier:** `grep -n 'email.layout={0}' reports/ai-context/role-assignment-email-catalog.md` still describing it as the base → not done.
- **Red run:** observed at baseline: `grep -n 'email.layout' reports/ai-context/role-assignment-email-catalog.md` → lines 33, 42, 45, 49 ("Base … `email.layout={0}`"), 69 (rollback to `{0}`).
- **Disqualifier:** n/a — none beyond the falsifier.
- **Consumers:** agents reading the catalog for role-email work.
- **Review:** skip-eligible — claim to prove at execute: the diff touches only the two named docs and no code or shared constitutional file.
- **Done when:** both docs updated; closure sweep `grep -rn 'email.layout={0}' reports docs/specs --include=*.md` lists only historical OTP records and this spec.
- **Skills:** cognitive-doc-design, marlo-commit.

### CHG-EMAIL-LAYOUT-001-T08 — Live verification on a local copy, with the human render check
- **Status:** pending · **Size:** M · **Depends on:** T04, T05, T06, T07 · **Module:** none (verification)
- **Requirements:** every AC of requirements §6 that needs a real send: FN-001/FN-006 (QATEST), FN-003 (AICCRA, AICCRA_III), FN-005 (SQL counts), FN-007 (planning + reporting + failed PDF where reproducible), FN-010, FN-011 (cross-unit sequence), NF-003
- **Design:** §11, §14
- **Scope:** worktree of the branch + throwaway DB copy (as the 2026-11-11 session: `crp_email_support_team` true for the units used, so mail goes only to `email.notification`); send per unit a role email, the welcome email, an OTP code, a submit and an unsubmit; query `email_logs` for shell marker, support/notice counts, `projectID=`/`phaseID=`, subject; run the FN-011 sequence (load an AICCRA_III page, then submit/unsubmit in AICCRA and vice versa); open each stored HTML in a browser for the owner's review at the HITL pause. Clean up worktree and DB copy.
  - Backend route: none — verification.
- **Tests:** SQL checks quoted in `execution.md`; human render check recorded with the owner's verdict.
- **Falsifier:** an email of QATEST without the shell marker, a count ≠ 1, a link without the submitted project id, or an AICCRA email with "Technical Implementation Team" → FAIL.
- **Red run:** observed at baseline (2026-11-11, `staging`): `email_logs` 18401-18407 stored without the shell; 18405 and 18407 carried the other unit's wording.
- **Disqualifier:** a run against a DB where `crp_email_support_team` is not true for the units used must stop before any send (real recipients); a render check without the owner's verdict is not evidence for the visual class; a single unit tested is not evidence for FN-001/FN-005.
- **Consumers:** n/a — verification only.
- **Review:** checklist — Reviewer checks every AC row has a quoted query result or owner verdict.
- **Done when:** every listed AC has evidence in `execution.md`; owner approves the rendered emails; local artifacts removed.
- **Skills:** marlo-verify.

---

## Dependency Graph

```
Q1 ─► T01 ─► T02 ─► T03 ─┬─► T04 ─┐
              │          └─► T05 ─┤
              ├─► T06 ◄── Q1      ├─► T08
              └─► T07 ────────────┘
```
T04 and T05 are independent of each other (different files) but share the T03 copy.

## Coverage (scenario / clause → task)

| Requirement clause | Task |
|---|---|
| AC FN-001/006 file-less unit, five emails, BUT no body loss, AND support/notice once | T01 (shell), T02 (footer), T04/T05 (submit/unsubmit), T08 (live) |
| AC FN-002 opt-out, AND support/notice still once | T01 (fixture), T06 (blocks + test), T08 |
| AC FN-003 AICCRA/AICCRA_III unchanged, BUT no change to support/greeting/sign-off | T01 (md5), T02 (no aicrra/aiccra3 role diff), T08 |
| AC FN-004 one definition | T01 |
| AC FN-005 every overriding unit + OTP, AND alliance kept | T02 |
| AC FN-007 both cycles, BUT no 102080/407 | T03 (text), T04 (Java), T08 |
| AC FN-007 failed PDF, BUT no "attached" | T04 (case b), T08 if reproducible |
| AC FN-008/009 copy, AND no Flagship / no `<br><br>` | T03 |
| AC FN-010 envelope, BUT subject may differ in FN-011 case | T04, T05 (diff review), T08 |
| AC FN-011 own unit, BUT no "Technical Implementation Team" in AICCRA, AND the reverse | T04 (c), T05 (b,e), T08 |
| NF-001 no Java per unit | T06 (data only) |
| NF-002 i18n | T01-T03, T06 (G-i18n) |
| NF-003 text part | T03 (copy test), T08 |

## Testing Plan
- **Unit:** T01-T06 tests (real `.properties`, real `UnitTextProvider`, `MessageFormat` as Struts).
- **Integration / live:** T08.
- **Regression (manual):** owner render review in T08.
- **Non-functional / accessibility:** not applicable (email HTML already reviewed in A2-2484).

## Operational Steps
- No migration, no specificity. Release notes: the email look change for units without their own shell (marlo-release-notes skill).

## Rollback Plan
- **Code / data:** revert the branch's commits; no data to undo.
- **Per unit:** the DD-9 opt-out block, without a code release.

## Estimate and PR strategy
- ~380 LOC (Java ~110, properties ~60, tests ~180, docs ~30) — matches the design budget (8 tasks, 10 review rounds).
- One PR into `staging`, reviewed in task order (T01 → T03 data first, then T04/T05 Java, then docs).

## Definition of Done
- [ ] All tasks done with evidence in `execution.md`.
- [ ] G-compile, G-check, G-i18n green on the final tree; full `mvn -q -o -pl marlo-web -am test` green.
- [ ] Owner's render approval recorded (T08).
- [ ] Q1 answer quoted (T06).
