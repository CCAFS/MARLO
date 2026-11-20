# Revive in-MARLO approval of partner requests — Tasks

**Spec ID:** CHG-PARTREQ-001
**Status:** Ready — approved 2026-11-19
**Owner:** Kenji Tanaka (IBD)
**Last Updated:** 2026-11-19
**Implements design:** docs/specs/changes/revive-local-partner-request-approval/design.md (judgment APPROVED, round 2)
**Branching:** feature branch `revive-local-partner-request-approval` from `staging`
**Target merge:** `staging` (two PRs: PR 1 = T01–T07 + T11a, PR 2 = T08–T10 + T11b + T12)

> **Answer first.** 12 tasks in two PRs. PR 1 builds the transactional manager, the email builder, the action wiring,
> the security config, and proves atomicity and locking live. PR 2 rebuilds the two pages in the redesign language and
> runs the end-to-end check with the owner's visual review. Baselines below were **observed on `3e4d08bd7e`**, so every
> "red" for existing code is a quoted reading, not a prediction.

---

## 1. Execution Context

| Item | Value |
|---|---|
| Java / build | Java 17; `marlo-parent/pom.xml` |
| Compile gate | `rm -rf marlo-web/target/classes marlo-web/target/test-classes marlo-data/target/classes && mvn -q -o -pl marlo-data,marlo-web -am test-compile -DskipTests` (expect ~2400 files marlo-data, ~1040 marlo-web) |
| Checkstyle | checkstyle 8.18 jar with `configuration/marlo-checkstyle.xml` on changed files, delta vs HEAD (`marlo-verify`) |
| Tests | `mvn -q -o -pl marlo-web -am test` (keep `-am`) |
| Local run | `scripts/run-marlo-java17.sh` — never while another agent builds (CLAUDE.md *Concurrency*) |
| Live DB | a **throwaway copy** of `aiccradb2` (never `aiccradb1-4` themselves); local login per `~/.marlo-local-credentials` |
| Browser | Playwright / Chrome automation for rendered checks; wait for page JS init before clicking Save |

## 2. Pre-flight Checklist

- [ ] `requirements.md`, `design.md` approved; `judgment.md` = APPROVED.
- [ ] `git checkout staging && git pull`; `git checkout -b revive-local-partner-request-approval`.
- [ ] Spec documents committed first with `[SPEC:changes/revive-local-partner-request-approval]` (`marlo-commit`; ask before committing).
- [ ] Throwaway DB copy created for T04 / T11.

## 3. Task List

Common Done criteria for every code task (not repeated below): clean compile gate green with the expected file
counts; Checkstyle delta 0 on changed files; GPL header on new Java files; English only; `marlo-verify` hygiene pass
(CRLF preserved, no `org.jfree.util.Log`, null-safe log arguments, no email in logs); CSS/JS cache-busters bumped when
those files change; `.properties` apostrophes doubled.

---

### CHG-PARTREQ-001-T01 — Transactional manager, locking DAO and outcome type

- **Status:** [x] done 2026-11-19 (PASS attempt 1 — `execution.md`) · **Size:** M · **Depends on:** — · **Module:** marlo-data (+ test in marlo-web)
- **Requirements:** FN-004 (create institution + HQ, close rows), FN-005 (accept once), FN-006 (reject fields, close both), FN-007 (edit copy only; BUT not pending → nothing), FN-008 (office add once, skip existing, reject justification), DA-001 (atomic), DA-002 (no schema), AC FN-001 (two rows created together)
- **Design refs:** §2 marlo-data, §6 (method table, "copy"/"pending" definitions, lock order), DD-2, DD-3, P-14, P-21
- **Scope:**
  - `PartnerRequestDAO` / `PartnerRequestMySQLDAO`: `findForUpdate(long id)` = load + `session.refresh(entity, LockMode.PESSIMISTIC_WRITE)`.
  - `PartnerRequestOutcome` (new): statuses `DONE`, `ALREADY_HANDLED`, `NOT_FOUND`, `WRONG_KIND`, `SKIPPED_EXISTING`; request; institution.
  - `PartnerRequestManager` / `Impl`: the six `@Transactional` methods exactly as design §6; office ids locked in ascending order; skipped existing location = `acepted=false` + justification.
  - `Backend route: stack other; implement lead existing design support none — project conventions; error-model lead error-handling-patterns support none — project conventions; test-first lead tdd support none — project conventions`
- **Tests:** `marlo-web/src/test/.../PartnerRequestManagerGuardTest` on DAO doubles (no Mockito; follow `EmailSendDoubles.answering`): accept / reject / edit refuse `WRONG_KIND` (original row, office row), `NOT_FOUND`, `ALREADY_HANDLED` (inactive, `acepted` set) and write nothing; accept builds the institution from the **copy**; office methods refuse other-institution / non-office ids per id; locks requested in ascending id order; double asserts the arguments it receives (KZ auth-flow-2).
- **Verification:** compile gate; `mvn -q -o -pl marlo-web -am test -Dtest=PartnerRequestManagerGuardTest`.
- **Falsifier:** (1) remove the pending check in `acceptPartnerRequest` → the "already handled writes nothing" test goes red; (2) build the institution from the original instead of the copy with a fixture whose copy name differs from the original → red; (3) lock ids in caller order with fixture `[9, 3]` → order assertion red.
- **Red run:** predicted — greenfield artifact (observed in T01). Baseline: `grep -c "acceptPartnerRequest\|createPartnerRequest\|findForUpdate"` on the manager and DAO interfaces → `0`, `0`.
- **Disqualifier:** a test whose double returns the same object it is handed and asserts on it (echo fixture) is no evidence; a green with the refusal branches never executed (check each test hits its branch) is no evidence. This task does **not** prove rollback or locking — T04 does.
- **Consumers:** `PartnerRequestManager` readers (design P-4): `PartnersSaveAction`, `PartnerRequestAction`, `EditPartnerRequestAction`, `RejectPartnerRequestAction`, `RequestCountryOfficeAction`, `SearchInstitutionsNameAction`, `InstitutionItem`, `CountryOfficeRequestItem` — existing methods unchanged; test files pinning them: none (`grep -rln PartnerRequestManager marlo-web/src/test` → 0).
- **Review:** `full` — transaction and lock semantics, correctness-critical.
- **Done when:** the three falsifiers executed and observed red, then reverted green; quoted in `execution.md`.
- **Skills:** `marlo-verify`, `error-handling-patterns`, `tdd`, `marlo-commit`.

---

### CHG-PARTREQ-001-T02 — `PartnerRequestEmails` builder and email keys

- **Status:** [x] done 2026-11-19 (PASS attempt 1 — `execution.md`) · **Size:** M · **Depends on:** — · **Module:** marlo-web
- **Requirements:** FN-002, FN-003 (alert content, subject from i18n), FN-009 (six emails in the shell, i18n only), FN-010 (switch), NF-002, AC FN-009/FN-010 (no legacy footer; none sent when off)
- **Design refs:** §10 email catalogue, §5.5 Emails row, DD-5, P-12, P-23
- **Scope:** `utils/PartnerRequestEmails.java` (new): one method per email returning subject + `EmailLayout.wrap(texts, assetsUrl, body)`, body = `email.role.dear` + body key + `email.role.bye`; body arguments HTML-escaped, subject arguments not; `isEnabled(GlobalUnit)` = today's `validateEmailNotification` rule; `unitTexts(GlobalUnit)` via `UnitTextProvider.forCustomFile` (as `UnsubmitProjectAction`). Keys in `global.properties`: `marloRequestInstitution.mail.newPartner`, `.newOffice`, `.accepted`, `.rejected`, `.officeAccepted`, `.officeRejected`, `.supportName`, written in the `email.role.*` inline-style copy (catalog `reports/ai-context/role-assignment-email-catalog.md`).
  - `Backend route: stack other; implement lead existing design support none — project conventions; test-first lead tdd support none — project conventions`
- **Tests:** `PartnerRequestEmailsTest`: each of the six builds a body containing the shell marker, the `email.role.dear` / `.bye` output, and **none** of `email.support.noCrpAdmins` / `email.bye` texts; a justification `O'Brien <b>` arrives escaped in the body and plain in no subject; subjects use the reused keys with the argument counts of design §10; `isEnabled` false when the custom parameter is `false`; unit texts come from the unit's custom file (two units, two subject prefixes).
- **Verification:** compile gate; `mvn -q -o -pl marlo-web -am test -Dtest=PartnerRequestEmailsTest`; i18n gate on added lines.
- **Falsifier:** (1) drop `EmailLayout.wrap` → shell-marker assertion red; (2) skip escaping → `<b>` assertion red; (3) use the shared provider instead of the unit's → the two-unit prefix assertion red (fixture: `aicrra` "[AICCRA" vs `global` "[MARLO").
- **Red run:** predicted — greenfield artifact (observed in T02). Baseline: `grep -c "^marloRequestInstitution.mail\." global.properties` → `0`.
- **Disqualifier:** a test that builds the expected string with the same builder it checks is tautological; asserting only "contains Dear" proves nothing about the shell.
- **Consumers:** old keys `marloRequestInstitution.accept.email` / `.reject.email` stay (read by `InstitutionItem`, design P-6); no existing test pins these keys (`grep -rln marloRequestInstitution marlo-web/src/test` → 0).
- **Review:** `checklist` — pure assembly, well covered by its tests.
- **Done when:** falsifiers observed red then green; owner-facing copy listed in `execution.md` for the T11 review.
- **Skills:** `marlo-verify`, `tdd`, `marlo-commit`.

---

### CHG-PARTREQ-001-T03 — `PartnerRequestAction` on the manager (accept partner, office accept / reject)

- **Status:** [x] done 2026-11-19 (PASS attempt 2 — `execution.md`) · **Size:** M · **Depends on:** T01, T02 · **Module:** marlo-web
- **Requirements:** FN-004, FN-005 (superadmin told "already handled"), FN-008 (each requester one email per Global Unit — amended 2026-11-19; SKIPPED_EXISTING mailed; notify on reject optional), FN-010, SEC-002 (accept partner / office on POST), OPS-001, AC FN-004/FN-005/DA-001 (repeat → no institution, message), AC FN-008
- **Design refs:** §2 action rows, §4 JSON contract, §6 recipients rule, §7, §8, §10 envelope, DD-4, DD-7
- **Scope:** `addPartner` → `isHttpPost()` else action error; `acceptPartnerRequest`; action message per status; email only for `DONE`. `addCountryOffices` / `rejectCountryOffices` → `isHttpPost()`; manager office methods; null check of `countryOfficePOJO` before use; `outcome` + `outcomes` (`Map<String,String>` id → status string) properties; emails for `DONE` and office `SKIPPED_EXISTING`, gated by `isEnabled(request.getCrp())`; failures logged with action + request id (no email); remove the three private legacy senders.
  - `Backend route: stack other; implement lead existing design support none — project conventions; error-model lead error-handling-patterns support none — project conventions; test-suite lead the spec's scenarios support none — project conventions`
- **Tests:** extend `PartnerRequestReviewGuardTest`: a GET to `addPartner` / office methods writes nothing (manager double records zero calls) and sets `notAllowed`; an `ALREADY_HANDLED` outcome sends no email (recording `SendMailS` double from `EmailSendDoubles`); a `SKIPPED_EXISTING` office requester is mailed; `outcomes` holds only strings.
- **Verification:** compile gate; `-Dtest=PartnerRequestReviewGuardTest`.
- **Falsifier:** (1) delete the `isHttpPost()` check → GET test red (manager called once); (2) mail on every outcome → "already handled sends nothing" red; (3) put the `PartnerRequestOutcome` object in `outcomes` → string-only assertion red.
- **Red run:** baseline observed: `grep -c "EmailLayout" PartnerRequestAction.java` → `0`; `grep -c "isHttpPost" PartnerRequestAction.java` → `0` (expected after: ≥ 3). Test reds: predicted — greenfield artifact (observed in T03).
- **Disqualifier:** a pass where the double never receives the call under test (assert call counts, not only absence of errors).
- **Consumers:** `struts-json.xml` `acceptOfficesRequest` / `rejectOfficesRequest` (`includeProperties` changed in T07); `marloInstitutions.js` office handlers (T09); `superadmin/addPartner` result in `struts-superadmin.xml` (unchanged).
- **Review:** `full` — security and email-ordering contract.
- **Done when:** falsifiers observed red then green.
- **Skills:** `marlo-verify`, `error-handling-patterns`, `marlo-commit`.

---

### CHG-PARTREQ-001-T04 — Live proof of atomicity and locking (settles P-5, P-21)

- **Status:** [x] done 2026-11-19 (PASS attempt 1 — `execution.md`) · **Size:** S · **Depends on:** T01, T03 · **Module:** local run (no prod code unless a Pivot)
- **Requirements:** DA-001 (all or nothing), FN-005 (concurrent repeat), AC FN-004/FN-005/DA-001 "AND IT MUST leave no institution behind"
- **Design refs:** §6 transaction model, P-5, P-21, R-8, DD-3 fallback
- **Scope:** on the throwaway DB: (a) temporary, uncommitted fault injection after the institution save in `acceptPartnerRequest` → POST `addPartner` → `institutions` and `institution_locations` unchanged, request still pending; (b) remove the fault; two concurrent POSTs to `addPartner` for the same request → exactly one new institution, the other `ALREADY_HANDLED`; MySQL general log / Hibernate SQL shows `for update`; (c) a second DB connection sees the institution row before the decision email is queued (`email_logs` timestamp after commit). If (a), (b) or (c) fails → **Pivot**: switch `findForUpdate` to the native `SELECT … FOR UPDATE` state read (design DD-3) and re-run.
- **Tests:** live probe; outputs saved under the spec folder `probes/` (counts before / after, SQL log excerpt).
- **Verification:** SQL counts quoted in `execution.md`.
- **Falsifier:** run (b) with the `refresh` line removed (lock only) → two institutions appear (the JD-1 defect reproduced).
- **Red run:** predicted — greenfield artifact (observed in T04): the lock-only variant must be run first and its duplicate quoted.
- **Disqualifier:** concurrency that is not truly parallel (requests serialised by the client) proves nothing — show overlapping request timestamps; a probe on a real `aiccradb*` schema is invalid.
- **Consumers:** none.
- **Review:** `full` — it settles the two High premises.
- **Done when:** P-5 and P-21 rows in `design.md` updated from `UNVERIFIED` to their citations (or the Pivot recorded); fault injection reverted (`git diff` clean of it).
- **Skills:** `marlo-verify`, `systematic-debugging`.

---

### CHG-PARTREQ-001-T05 — Edit and reject JSON actions on the manager

- **Status:** [x] done 2026-11-19 (PASS attempt 1 — `execution.md`) · **Size:** S · **Depends on:** T01, T02 · **Module:** marlo-web
- **Requirements:** FN-006 (justification, user, date, close both; notify optional), FN-007 (+ BUT not pending), SEC-002 (edit / reject POST), OPS-001, AC FN-006, AC FN-007
- **Design refs:** §2, §4 JSON contract, §6, DD-7
- **Scope:** `EditPartnerRequestAction`: `isHttpPost()`; `editPartnerRequest`; `outcome` string; log failures. `RejectPartnerRequestAction`: `isHttpPost()`; `rejectPartnerRequest`; reject email via the builder when `sendNotification` and `DONE`, gated by `isEnabled(request.getCrp())`; remove the legacy sender.
  - `Backend route: stack other; implement lead existing design support none — project conventions; error-model lead error-handling-patterns support none — project conventions; test-suite lead the spec's scenarios support none — project conventions`
- **Tests:** `PartnerRequestReviewGuardTest`: GET edit / reject → no manager call, `notAllowed`; edit on non-pending → `alreadyHandled`, no change; reject with `sendNotification=false` → no email; with `true` and `DONE` → one email to the requester.
- **Verification:** compile gate; `-Dtest=PartnerRequestReviewGuardTest`.
- **Falsifier:** (1) ignore `sendNotification` → "unchecked sends nothing" red; (2) remove `isHttpPost()` in edit → GET test red.
- **Red run:** baseline observed: `grep -c "isHttpPost" EditPartnerRequestAction.java RejectPartnerRequestAction.java` → `0`, `0`. Test reds: predicted — greenfield artifact (observed in T05).
- **Disqualifier:** as T03.
- **Consumers:** `struts-json.xml` results (T07); `marloInstitutions.js` edit / reject handlers (T09).
- **Review:** `checklist` — same pattern as T03, smaller.
- **Done when:** falsifiers observed red then green.
- **Skills:** `marlo-verify`, `error-handling-patterns`, `marlo-commit`.

---

### CHG-PARTREQ-001-T06 — Local request creation and the two alerts (remove CLARISA)

- **Status:** [x] done 2026-11-19 (PASS attempt 1 — `execution.md`) · **Size:** M · **Depends on:** T01, T02 · **Module:** marlo-web
- **Requirements:** FN-001 (+ BUT no CLARISA call; AND same in non-AICCRA unit), FN-002, FN-003 (subject from i18n), FN-010 (alerts on the session unit), NF-001 (no unit check), OPS-001, AC FN-001, AC FN-002/FN-003 (BUT switch off → stored, not sent)
- **Design refs:** §2, §7, §10 (alert rows), DD-1, DD-8, P-1, P-7
- **Scope:** `PartnersSaveAction`: delete `sendPartnerRequest`, the `isAiccra()` branch, imports (`ExternalPostUtils`, Apache HTTP, `JSONObject`) and the commented legacy mail block; call `createPartnerRequest`; on exception log + `requestFailed` + `INPUT`; alert via the builder gated by `isEnabled(loggedCrp)`. Add `marloRequestCreation.saveFailed` and switch the FTL message key (FTL redesign itself is T10). `RequestCountryOfficeAction`: subject + body via the builder; log failures; keep its result contract.
  - `Backend route: stack other; implement lead existing design support none — project conventions; error-model lead error-handling-patterns support none — project conventions; test-suite lead the spec's scenarios support none — project conventions`
- **Tests:** `PartnersSaveActionRequestIdTest` still green; new cases: `save()` with a manager double that throws → `requestFailed` and no email; switch off → manager called, no email; no reference to CLARISA remains.
- **Verification:** compile gate; tests; `grep -c "isAiccra()\|sendPartnerRequest\|ExternalPostUtils" PartnersSaveAction.java` → `0`; `grep -c '"\[MARLO-"' RequestCountryOfficeAction.java` → `0`.
- **Falsifier:** reintroduce `if (this.isAiccra()) return SUCCESS;` before the save → the AICCRA-unit "two rows created" case red.
- **Red run:** baseline observed: `grep -c "isAiccra()\|sendPartnerRequest\|ExternalPostUtils" PartnersSaveAction.java` → `5`; `grep -c '"\[MARLO-"' RequestCountryOfficeAction.java` → `1`; `grep -c "\* sendMail.send(config.getEmailNotification()" PartnersSaveAction.java` → `1`.
- **Disqualifier:** a test that stubs `isAiccra()` false only — the case must run with the unit reporting AICCRA.
- **Consumers:** `PartnersSaveActionRequestIdTest` (pins `readRequestId`); `partnersSave.ftl` (`requestFailed`, `saveFailed` key); `getClarisaAPI*` / `ExternalPostUtils` become dead (follow-up, not deleted — P-7).
- **Review:** `full` — removes a delivered integration.
- **Done when:** falsifier observed red then green; dead-code follow-up recorded in `execution.md`.
- **Skills:** `marlo-verify`, `error-handling-patterns`, `marlo-commit`.

---

### CHG-PARTREQ-001-T07 — Struts security config and menus

- **Status:** [x] done 2026-11-19 (PASS; reopened for the edit-field bridge at the T11a gate, PASS — `execution.md`) · **Size:** S · **Depends on:** T03, T05 · **Module:** marlo-web (XML, FTL)
- **Requirements:** SEC-001, SEC-003, FN-011, AC SEC (non-superadmin refused, no row changes; no CRP-admin entry), AC FN-011
- **Design refs:** §4 interceptor and JSON tables, DD-6, DD-9, P-10, P-22, P-24
- **Scope:** `struts-json.xml`: the four review actions get `logErrorStatus`, `requireUser`, `validPhase`, `superAdminValidation`, `defaultStack`, `SecurityControl`; `includeProperties` `success,outcome` (edit / reject) and `success,outcome,outcomes.*` (offices). `struts-admin.xml` `{crp}/marloInstitutions`: `i18nFile`, `requireUser`, `superAdminValidation`, `crpAdminStack` (`logErrorStatus` comes from the stack — amended 2026-11-19); result `redirectAction` namespace `/superadmin`, action `marloInstitutions`. `menu-superadmin.ftl:34` uncommented; `menu-admin.ftl:14` removed. **Moved from T08 (owner, 2026-11-19, T03 gate):** add to `global.properties` the seven action-message keys `addPartner` already uses — `marloRequestInstitution.ui.outcome.done`, `.alreadyHandled`, `.notFound`, `.wrongKind`, `.skippedExisting`, `.notAllowed`, `.failed` — so PR 1 renders no raw key; `.outcome.notAuthorized` (JS only) stays in T08. **Also moved into T07 (owner, 2026-11-19, T05 gate) — minimal POST bridge so PR 1 works on the current screen:** `marloInstitutions.js` — add `type: 'POST'` to the four AJAX calls (`rejectPartnerRequest`, `editPartnerRequest`, `rejectOfficesRequest`, `acceptOfficesRequest`) and post `sendNotification` as the literal `true` / `false`; `institutionRequestMacro.ftl` — the accept-partner GET link becomes a minimal POST form to `superadmin/addPartner` with the same `requestID`, keeping the existing confirm step. No restyling; T08 / T09 rewrite these lines in the redesign. Cache-buster of `marloInstitutions.js` bumped. **Edit-field bridge (owner, 2026-11-19, T11a gate):** the edit AJAX call sends the fields under the names `EditPartnerRequestAction` reads (`institutionName`, `institutionAcronym`, `institutionWebPage`, `institutionTypeID`, `countryID`, `justification`, plus the request id parameter) — the current form names never matched, on `staging` either (T11a FN-007); T09 must keep these names.
- **Tests:** `PartnerRequestReviewGuardTest` static check reading `struts-json.xml` / `struts-admin.xml`: each of the five routes lists `superAdminValidation` before `defaultStack` / `crpAdminStack`. POST bridge: static `grep -c "type: *'POST'" marloInstitutions.js` → `4`; the accept control in `institutionRequestMacro.ftl` is a `method="post"` form; live check in T11a from the current screen.
- **Verification:** compile gate; test; app boots (Struts config parses) in T11a.
- **Falsifier:** move `superAdminValidation` after `defaultStack` in one action → static-order test red.
- **Red run:** baseline observed: `superAdminValidation` count inside each of the four actions → `0 0 0 0`; admin route → `0`; `menu-superadmin.ftl:34` begins `[#--`; `menu-admin.ftl:14` is the live `institutions` entry.
- **Disqualifier:** the static test proves order, not behavior — the 403 is proven live in T11a.
- **Consumers:** `marloInstitutions.js` (403 / 401 handling, T09); admin view `crp/views/admin/marloInstitutions.ftl` (no longer rendered).
- **Review:** `checklist` — config, behavior proven in T11a.
- **Done when:** falsifier observed red then green.
- **Skills:** `marlo-verify`, `marlo-commit`.

---

### CHG-PARTREQ-001-T08 — Review screen markup and CSS (`.prq`)

- **Status:** [x] done 2026-11-19 (PASS attempt 2 — `execution.md`) · **Size:** L · **Depends on:** T03, T05, T07 · **Module:** marlo-web (FTL, CSS)
- **Requirements:** UI-001 (+ BUT no legacy markup), UI-002 (header + counts, cards, empty states), UI-003 (changed fields only), UI-006 (focus, contrast, 390 px), AC UI-001/002/003
- **Design refs:** §5.1–5.2 (component mapping, token mapping), §5.3 (accept POST form, redirect notes), §5.5 Review keys, §5.6, DD-10, DD-11, P-17, P-18, P-20
- **Scope:** rewrite superadmin `marloInstitutions.ftl`, `institutionRequestMacro.ftl`, `rejectInstitutionPopup.ftl`, `allInstitutionsPopup.ftl` markup and `marloInstitutions.css` in the `.prq` scope with the design's token mapping; accept as POST form; action messages as `.prq-note`; `marloRequestInstitution.ui.*` page keys (except the seven `.outcome.*` keys moved to T07); drop the diff libs from `pageLibs`; new cache-busters.
  - `Design route: load impeccable-bridge → design-routing.md; playbook layout, typeset, colorize, harden; ENV-2; mode Operate; polish light; motion none`
- **Tests:** rendered checks in T11b; static: `grep -c "list-group\|btn-success\|btn-danger\|glyphicon"` on the four FTL → `0`.
- **Verification:** static grep; **baseline first** — measure `document.documentElement.scrollWidth` vs `clientWidth` on the current (old) page at 390×844 and record it, so a pre-existing overflow is not blamed on or hidden by the change; then screenshots at 1280×800 and 390×844 attached to `execution.md` and `scrollWidth <= clientWidth` at both widths (effective CSS px, browser zoom 100 %), with Figtree confirmed loaded.
- **Falsifier:** leave one `btn-success` in the macro → static grep `1`; set a card `min-width: 600px` → 390 px overflow check red.
- **Red run:** baseline observed: legacy-markup grep → macro `13`, superadmin view `1`, reject popup `3`, all-institutions popup `0`.
- **Disqualifier:** class presence is not visual fidelity — that is the owner's screenshot review (T11b) and `/akili-validate`'s design gate; a screenshot taken before Figtree loads is invalid (assert `document.fonts.check('600 16px Figtree')`).
- **Consumers:** `marloInstitutions.js` selectors (T09 must follow the new markup); admin view not rendered (DD-9); the diff libs removed from this page's `pageLibs` stay in use by `historyDiff` elsewhere (P-20) — do not touch their library definitions.
- **Review:** `full` — first redesign of a superadmin screen.
- **Done when:** completion report names the playbooks run, polish light and its result, and `finish review: not run (AKILI execute)`; the Leader logs it in `execution.md` and names that entry in the Reviewer brief.
- **Skills:** `impeccable-bridge`, `marlo-verify`, `marlo-commit`.

---

### CHG-PARTREQ-001-T09 — Review screen behaviour: `prqDialog`, POST, inline outcomes

- **Status:** pending · **Size:** M · **Depends on:** T08 · **Module:** marlo-web (JS, FTL data attributes)
- **Requirements:** UI-004 (inline edit; dialogs; reject justification required; notify option; outcome notes; BUT no `confirm`/`alert`), UI-006 (dialog keyboard), SEC-002 (AJAX POST), AC UI-004, AC UI-006 dialogs clause
- **Design refs:** §4 JSON contract (statuses, 403 / 401), §5.3, §5.4, §5.5, DD-12
- **Scope:** `marloInstitutions.js`: `type: 'POST'` on the four calls and the edit-call parameter names (both bridged in T07 — keep them through the rewrite); `prqDialog` (role, aria-modal, aria-labelledby, focus in, Tab trap, Escape, return focus); status → note key mapping incl. `error` handler for 403 / 401; strings from `data-` attributes; no `confirm(` / `alert(` / `noty(`.
  - `Design route: load impeccable-bridge → design-routing.md; playbook harden, clarify; ENV-2; mode Operate; polish light; motion none`
- **Tests:** browser checks in T11b (keyboard: Tab cycles inside the dialog, Escape closes, focus returns to the opener); static `grep -c "confirm(\|alert(\|noty(" marloInstitutions.js` → `0`, `grep -c "type: *'POST'"` → `4`.
- **Verification:** static greps; live keyboard walk recorded (GIF or step log).
- **Falsifier:** remove the Tab trap → focus leaves the dialog on the 4th Tab in the live check; keep one `confirm(` → grep `1`.
- **Red run:** baseline observed: `grep -c "confirm(\|alert(\|noty("` → `6`; `grep -c "type: *'POST'"` → `0` at `3e4d08bd7e` (→ `4` after the T07 POST bridge; T09 must keep `4`).
- **Disqualifier:** a keyboard check done with the mouse focusing the dialog first is invalid — start from the opener button with keyboard only.
- **Consumers:** the T08 markup; JSON fields from T03 / T05 / T07.
- **Review:** `full` — security-relevant (POST) and accessibility.
- **Done when:** completion report as T08 (playbooks, polish light, finish review not run).
- **Skills:** `impeccable-bridge`, `marlo-verify`, `marlo-commit`.

---

### CHG-PARTREQ-001-T10 — Partner request page redesign

- **Status:** pending · **Size:** M · **Depends on:** T06 · **Module:** marlo-web (FTL, CSS, JS, i18n)
- **Requirements:** UI-001, UI-005 (form card, field errors, success, saveFailed), UI-006, AC UI-005, AC UI-006 width clause
- **Design refs:** §5.2 request page rows, §5.3 binding contract, §5.5 Request page keys, §5.6, DD-8
- **Scope:** `partnersSave.ftl`, `partnersSave.css`, `partnersSave.js` in `.prq`; keep every bound name, the hidden ids, `context`, `phaseID` and the submit `name="save"`; help slideshow → one help note; hardcoded English success copy → `marloRequestCreation.success.*` keys; cache-busters.
  - `Design route: load impeccable-bridge → design-routing.md; playbook layout, clarify, harden; ENV-2; mode Operate; polish light; motion none`
- **Tests:** live in T11b: submit with missing name / type / country → field errors, nothing stored; valid submit → success card and two rows; forced save failure → `saveFailed` note.
- **Verification:** `grep -c "sent succesfully" partnersSave.ftl` → `0`; `grep -c 'name="save"\|name=.save' partnersSave.ftl` ≥ 1; legacy-markup grep → `0`; screenshots 1280 / 390.
- **Falsifier:** drop `name="save"` from the submit → valid submit stores nothing (live check red).
- **Red run:** baseline observed: `grep -c "sent succesfully" partnersSave.ftl` → `1`; legacy-markup grep on `partnersSave.ftl` → `3`.
- **Disqualifier:** as T08 (fonts loaded; class presence ≠ fidelity).
- **Consumers:** every page that opens the request popup (projects, funding sources, studies, innovations, capdev, POWB, AR — `global.js` and the views listed in design P-4) — the URL and parameters are unchanged; `PartnersSaveAction` binding.
- **Review:** `full` — the page is reached from seven modules.
- **Done when:** completion report as T08.
- **Skills:** `impeccable-bridge`, `marlo-verify`, `marlo-commit`.

---

### CHG-PARTREQ-001-T11 — End-to-end verification on a throwaway DB (T11a after PR 1, T11b after PR 2)

- **Status:** [~] T11a done 2026-11-19 (PASS attempt 3 — `execution.md`); T11b pending (PR 2) · **Size:** M · **Depends on:** T11a: T01–T07; T11b: T08–T10, T11a · **Module:** local run
- **Requirements:** every AC; FN-010 / AC switch-off; SEC-001 / SEC-002 / SEC-003 live; OPS-001 (forced failure → one log line, no email address); UI-001…006 rendered; FN-009 render
- **Design refs:** requirements §6 and §7; design R-2, R-6, P-15, P-24
- **Scope:** **T11a (PR 1):** as a non-superadmin user, hand-crafted POSTs to the five review routes → 403 and no row change; GET to each as superadmin → nothing changes; AICCRA request → two rows, alert in `email_logs` inside the shell, no CLARISA call (network / log check); accept / reject / edit / offices flows → DB state per ACs and decision emails; switch off → stored, no email; repeat accept → `alreadyHandled`; menu entry in AICCRA superadmin, absent in CRP admin, `admin/{crp}/marloInstitutions` redirects; review URL works without `phaseID` (P-15). **T11b (PR 2):** the same flows through the redesigned UI; screenshots of both pages at 1280×800, 390×844, and short heights 1366×768 / 375×667 for the primary action; keyboard dialog walk; the six emails rendered (from `email_logs` HTML) for the **owner's visual approval at the HITL pause**.
- **Tests:** live; evidence under the spec folder `probes/`.
- **Verification:** each AC ticked in `execution.md` with its evidence row.
- **Falsifier:** run the non-superadmin POST against `staging` code first → it is accepted (C8 reproduced), then against the branch → 403.
- **Red run:** predicted — greenfield artifact (observed in T11a): the staging run must be executed and quoted.
- **Disqualifier:** checks against a real `aiccradb*` schema are invalid; an email render judged from the raw `.properties` text instead of the sent HTML is invalid; a save test clicked before the page JS initialised is invalid.
- **Consumers:** none.
- **Review:** `checklist` — evidence review.
- **Done when:** all ACs evidenced; owner approves the page screenshots and the six email renders.
- **Skills:** `marlo-verify`, `playwright-cli`, `impeccable-bridge` (T11b).

---

### CHG-PARTREQ-001-T12 — Documentation

- **Status:** pending · **Size:** S · **Depends on:** T11b · **Module:** docs
- **Requirements:** OPS-002 (release note text), traceability
- **Design refs:** §2 Docs, R-7, DD-13, P-7 follow-ups
- **Scope:** `docs/specs/domain/admin/agent-context.md` — new `marloInstitutions` section (flow, manager methods, guard order, JSON contract, REST writer caveat R-7); `reports/ai-context/role-assignment-email-catalog.md` — the six partner-request emails; follow-up list in `execution.md`: dead `getClarisaAPI*` / both `ExternalPostUtils` copies, unrendered admin view, `docs/ux-ui/design.md` §7 drift (epic), CLARISA REST guards (R-7); release-note line for OPS-002.
- **Tests:** n/a — documentation.
- **Verification:** every file / method named in the new section exists (`grep` each).
- **Falsifier:** a section naming a method that does not exist → its grep returns 0.
- **Red run:** baseline observed: `grep -c "marloInstitutions" docs/specs/domain/admin/agent-context.md` → `1` (only in the "Does not cover" list).
- **Disqualifier:** a summary copied from the design instead of the shipped code.
- **Consumers:** none.
- **Review:** `checklist`.
- **Done when:** sections exist; follow-ups recorded.
- **Skills:** `cognitive-doc-design`, `marlo-commit`.

## 4. Dependency Graph

```
PR 1
T01 (manager/DAO) ──┬── T03 (PartnerRequestAction) ──┬── T04 (live atomicity + lock)
T02 (emails) ───────┤                                │
                    ├── T05 (edit / reject) ─────────┼── T07 (struts + menus) ── T11a (E2E, PR 1)
                    └── T06 (request + alerts) ──────┘
PR 2
T07 ── T08 (review markup + CSS) ── T09 (review JS) ──┐
T06 ── T10 (request page) ────────────────────────────┴── T11b (E2E + owner visual) ── T12 (docs)
```

T01 ∥ T02 can run in parallel. T04 gates PR 1's merge: a Pivot there changes T01 before anything else builds on it.

## 5. Coverage (scenario / clause → task)

| Clause | Owner |
|---|---|
| FN-001 two rows; BUT no CLARISA call; AND same in non-AICCRA | T06 (code), T11a (live) |
| FN-002 / FN-003 alert content, review link, i18n subject; BUT switch off → stored, not sent | T02, T06, T11a |
| FN-004 one institution + HQ, accepted fields, both inactive, requester mailed | T01, T03, T11a |
| FN-005 repeat → nothing + message (double click, back, replayed POST) | T01, T03, T04 (concurrent), T11a |
| DA-001 AND IT MUST leave nothing on mid-way failure | T04 |
| FN-006 reject fields, both inactive, email with justification; BUT unchecked → no email | T01, T05, T11a |
| FN-007 edit copy, original unchanged; BUT not pending → nothing | T01, T05, T11a |
| FN-008 A existing → only B added, all closed, each requester one email per Global Unit (incl. skipped; amended 2026-11-19); reject justification + optional email | T01, T03, T11a |
| FN-009 shell, no legacy footer, i18n only | T02, T11b (render) |
| FN-010 / switch off → none sent | T02, T03, T05, T06, T11a |
| FN-011 AICCRA superadmin menu entry | T07, T11a |
| SEC-001 non-superadmin refused, no row changes | T07, T11a |
| SEC-002 GET to any of the five changes nothing | T03, T05, T07, T09, T11a |
| SEC-003 no CRP-admin entry; route refuses / redirects | T07, T11a |
| DA-002 no schema change | T01 (no migration file), PR review |
| OPS-001 one log line, request id, no email | T03, T05, T06, T11a |
| OPS-002 sync precondition | T12 (release note); owner, before release |
| UI-001 redesign language; BUT no legacy markup | T08, T10, T11b |
| UI-002 header counts, cards, empty states | T08, T11b |
| UI-003 changed fields only | T08, T11b |
| UI-004 dialogs, required justification, notify, outcome notes; BUT no confirm / alert | T09, T11b |
| UI-005 field errors, success, saveFailed | T06 (key), T10, T11b |
| UI-006 no horizontal scroll at 390 / 1280, focus ring, dialog trap + Escape | T08, T09, T10, T11b |
| NF-001 no unit-specific Java | T06 |
| NF-002 i18n hygiene | T02, T06, T08, T10 |
| NF-003 gates | every code task |

## 6. Testing Plan

| Layer | What |
|---|---|
| Unit | `PartnerRequestManagerGuardTest` (T01), `PartnerRequestEmailsTest` (T02), `PartnerRequestReviewGuardTest` (T03, T05, T07), `PartnersSaveActionRequestIdTest` kept green (T06) |
| Live integration | T04 rollback + concurrency; T11a security, flows, emails, switch |
| Rendered / a11y | T08–T10 screenshots and overflow; T11b keyboard dialog walk, four viewports, owner visual review |
| Design gate | `/akili-validate` Impeccable critique + audit (detector if the engine is consented) |
| Regression | the request popup from each opener module (projects, funding sources, studies, innovations, capdev, POWB, AR) opens and submits (T11b) |

## 7. Operational Steps

- **Before release (OPS-002):** owner runs on production `SELECT DATE(added), COUNT(*), MIN(id), MAX(id) FROM institutions WHERE added >= '<last known sync>' GROUP BY 1;` — must return no rows. Otherwise stop.
- **Before release (P-25, added 2026-11-19 at the T04 gate, owner approval):** owner runs on production `SELECT @@GLOBAL.sql_mode;` (and `SHOW CREATE TABLE institutions`). If it contains `STRICT_TRANS_TABLES` (or `STRICT_ALL_TABLES`), stop: every accept would fail on `institutions.created_by` (NOT NULL, no default, unmapped) — reopen the spec for a guarded migration or an entity mapping (needs a DA-002 amendment).
- No migration, no specificity, no config change.
- Release note: AICCRA partner requests are approved inside MARLO again; the support mailbox receives alerts.

## 8. Rollback Plan

- Code: revert the merge commits on `staging`; redeploy.
- Data: none to undo. Requests and institutions created locally stay valid.
- If the CLARISA sync turns out to be running after release: stop approving in MARLO, compare new local ids with CLARISA codes, decide with the CLARISA owner.

## 9. Definition of Done

- [ ] All ACs evidenced (T11a, T11b); owner approved screenshots and the six email renders.
- [ ] Compile, Checkstyle delta 0, tests with `-am` green; i18n gate clean.
- [ ] P-5 and P-21 settled in `design.md` (T04).
- [ ] `agent-context.md` and the email catalog updated (T12); follow-ups recorded.
- [ ] Both PRs merged to `staging` (owner merges); OPS-002 confirmed before production.
