# Validation Report — Revive in-MARLO approval of partner requests

> **Verdict (re-validated 2026-11-20): ARCHIVE-READY WITH ACCEPTED WARNINGS — 0 FAIL, 7 WARN.**
>
> The first pass found 3 FAILs. All three are now closed:
>
> - **F2 (P1 security):** fixed on the whole review page, in the card links and in the all-institutions popup. Proven live with a falsifier.
> - **F3 (`nested-cards`):** fixed. The detector re-scan exits `0`.
> - **F1 (first-visit and shared chrome):** waived by the owner. The waivers are pending the authority write on `staging` (§11.1), so the design gate reads **WARN**, not PASS, until `docs/ux-ui/design.md` carries them.
>
> First-pass findings are kept below as history; §13 records the re-validation.

## 1. Document Control

| Item | Value |
|---|---|
| Spec | `changes/revive-local-partner-request-approval` (CHG-PARTREQ-001) |
| Validated at | `2380642ca0` (branch `revive-local-partner-request-approval`; base `staging` `3e4d08bd7e`) |
| Date | 2026-11-20 |
| Validator | Claude Code session on Opus 5.5 (T3). Implementers ran on `sonnet` (author ≠ auditor holds). The design review sub-agent ran on `opus` (akili-tester) |
| Inputs | `proposal.md`, `requirements.md`, `design.md`, `tasks.md`, `execution.md`, `judgment.md`, `probes/` (T04, T11a, T11b, `validate-*`). No `test-report.md` exists (`/akili-test` was not run) |

## 2. Summary

| Area | Result |
|---|---|
| Task completion | PASS: 12 / 12 `[x]`, each with an `execution.md` entry and a closing Reviewer PASS |
| File existence | PASS: 25 / 25 design §2 files changed; admin view untouched (DD-9); no migration (DA-002) |
| Build integrity | PASS: clean compile 2431 / 1065 / 146; `Tests run: 1360, Failures: 0, Errors: 0`; Checkstyle 3 → 0 on the 16 changed Java files |
| Requirement coverage | PASS for FN-001…011, SEC-001…003, DA-001/002, OPS-001, UI-001…006 (with the owner's recorded readings), NF-001…003. **WARN** for OPS-002 (release precondition, owner) |
| Quality / security | PASS after remediation (F2 fixed, §13). First pass: FAIL |
| Design gate | **WARN** after remediation: F3 fixed; F1 owner-waived, pending the authority write (§11.1, §13). First pass: FAIL |
| Design conformance | PASS. All drift is recorded as execute-time amendments with closure sweeps |
| Constitution impact | WARN: `docs/ux-ui/design.md` §7 drift (DD-13) and no *Design Waivers* section; pending for staging |

## 3. Task Completion

| Task | Status | Closing record |
|---|---|---|
| T01 manager / DAO / outcome | PASS | Reviewer PASS (2 lenses), falsifiers ×3 |
| T02 email builder | PASS | Reviewer PASS, falsifiers ×3 |
| T03 `PartnerRequestAction` | PASS | Attempt 2 (OPS-001 log ids), falsifiers ×3 + log-capture |
| T04 live atomicity / locking | PASS | Lock-only falsifier reproduced the duplicate; P-5 / P-21 settled |
| T05 edit / reject actions | PASS | Reviewer PASS, falsifiers ×2 |
| T06 local request + alerts (CLARISA removed) | PASS | Reviewer PASS, falsifier with an AICCRA unit |
| T07 Struts security + menus + POST bridge | PASS | Reopened once (edit-field bridge), PASS |
| T08 review markup / CSS | PASS | Attempt 2 (cache-buster) |
| T09 review JS / `prqDialog` | PASS | Attempt 2 (cache-buster) |
| T10 request page | PASS | Reopened once (label and title keys), PASS |
| T11 E2E (T11a + T11b) | PASS | Owner visual approval 2026-11-20; three rounds of owner fixes reviewed |
| T12 documentation | PASS | Attempt 2 (two sentences corrected against the code) |

`tasks.md` §9 *Definition of Done* checkboxes are still unticked → **WARN W5** (bookkeeping).

## 4. File Existence

| Check | Result |
|---|---|
| Design §2 files (marlo-data 5, marlo-web 20) | 25 / 25 changed vs `3e4d08bd7e` |
| New files | `PartnerRequestOutcome.java`, `PartnerRequestEmails.java`, plus tests `PartnerRequestManagerGuardTest`, `PartnerRequestEmailsTest`, `PartnerRequestReviewGuardTest`, `PartnersSaveActionRequestTest`, `RequestCountryOfficeActionAlertTest` |
| Must stay untouched | `crp/views/admin/marloInstitutions.ftl` untouched (DD-9) ✔ |
| Migrations | none (DA-002) ✔ |
| Extra files | `custom/aicrra`, `custom/aiccra3` and `custom/pabra` `.properties`, from the owner-approved T11b fixes (Decision Log) ✔ |

## 5. Build Integrity

| Gate | Command | Result |
|---|---|---|
| Compile + suite | clean wipe (incl. `maven-status`) + `mvn -o -pl marlo-data,marlo-web -am test` | exit 0; Compiling 2431 / 1065 / 146; **1360 / 0 / 0** |
| Checkstyle | `checkstyle.sh` on the 16 added or modified Java files, HEAD vs a `3e4d08bd7e` worktree | base 3 → HEAD **0** (delta −3) |
| i18n / cache-bust / hygiene | per task (see `execution.md`) | clean; Gate 3 `ok` on every touched reference |
| Boot smoke | the app was booted on the throwaway DB by T04, T11a, T11b and the design gate | healthy |

## 6. Requirement Coverage

Evidence comes from `execution.md`, the unit suites and the live probes. There is no `test-report.md`, so coverage was verified directly (**WARN W1**: run `/akili-test` if a formal test report is wanted).

| Requirement / clause | Owner tasks | Evidence | Result |
|---|---|---|---|
| FN-001 two rows; BUT no CLARISA; AND non-AICCRA | T06, T11a | `t11a-fn001-*`, `PartnersSaveActionRequestTest` (AICCRA unit), grep 0 | PASS |
| FN-002 / FN-003 alerts; BUT switch off | T02, T06, T11a/b | `t11a-switch-off.out`, `t11b-email-*` | PASS |
| FN-004 / FN-005 / DA-001 accept once; AND IT MUST leave nothing | T01, T03, T04 | `t04-a-fault-injection.out`, `t04-b-concurrent-*`, `t11a-ui-accept.out` | PASS |
| FN-006 reject; BUT unchecked → no mail | T05, T11a | `t11a-ui-reject.out`, guard tests | PASS |
| FN-007 edit; BUT not pending → nothing | T01, T05, T07 bridge, T11a | `t11a-ui-edit-rerun.out` | PASS |
| FN-008 offices (one email per requester **per Global Unit**, amended) | T01, T03, T04 | `t04-office-batch.out`, `t11a-office-multiunit.out` | PASS |
| FN-009 shell, no legacy footer, i18n | T02, T11b | `t11b-email-*.html` (sent HTML) | PASS |
| FN-010 switch | T02, T03, T05, T06 | `t11a-switch-off.out` | PASS |
| FN-011 menu entry | T07, T11a | `t11a-menus-branch.out` | PASS |
| SEC-001 / SEC-002 / SEC-003 | T03, T05, T07, T09, T11a | staging falsifier `t11a-falsifier-staging.out` → branch 403 | PASS |
| DA-002 no schema change | T01 | no migration | PASS |
| OPS-001 one log line, request id, no email | T03, T05, T06, T11a | log-capture tests, `t11a-ops001.out` | PASS |
| OPS-002 sync precondition | T12 | release note written; the production check is the owner's | **WARN W2** (pending, pre-release) |
| UI-001 (reading: the two pages' own markup) | T08, T10, T11b | legacy-markup grep 0; owner reading (Decision Log) | PASS |
| UI-002 / UI-003 / UI-004 | T08, T09, T11b | screenshots, keyboard walk | PASS |
| UI-005 (reading: "not sent" = nothing stored) | T10, T11b | `t10-live.out`, owner reading | PASS |
| UI-006 390 / 1280, focus, dialogs | T08–T10, T11b | overflow measurements, `t11b-keyboard-walk.out` | PASS (the first-visit gate is separate, §8) |
| NF-001 no unit-specific Java | T06, T11b fixes | grep `isAiccra` 0 in the touched actions; unit words via i18n | PASS |
| NF-002 i18n hygiene | all | i18n gate per task | PASS |
| NF-003 gates | all | §5 | PASS |

**P-25 (High, UNVERIFIED):** accept relies on non-strict SQL mode in production. This is a release precondition owned by the owner → **WARN W3**.

## 7. Linting & Code Quality

| # | Finding | Result |
|---|---|---|
| F2 | `institutionRequestMacro.ftl:174` renders `<a href="${partner.webPage}">`. FreeMarker auto-escaping does not block a `javascript:` scheme, and `PartnersSaveAction.validate()` / `editPartnerRequest` accept any text. A requester could plant a script URL that runs when a superadmin clicks it. **Pre-existing** (`staging` macro line 15), carried into the rewritten markup | **FAIL (P1 security)** |

**Advisory (4R, non-gating), carried forward:** see `execution.md` `## Follow-ups` (20 items). Most relevant:
- the CLARISA REST writer has no guard (R-7);
- `SendMailS` evaluates the session unit's switch (follow-up 20);
- the edit `sessionStorage` fallback removes the card;
- unused managers and the shared `statusName`;
- `isEnabled` Javadoc.

## 8. Design Conformance

### Design review

| Item | Value |
|---|---|
| Skill source | `vendored (4.8.0, modified: 1 file: scripts/bin/darwin-arm64/impeccable)`. Every `UPSTREAM.md` hash matches; the only unlisted file is the engine binary. This is an environment finding. Copy `~/.claude/skills/impeccable`; other copy `~/.agents/skills/impeccable` (4.8.0) |
| Engine | `ready (location 2, engine 0.1.18)`, pinned with `DO_NOT_TRACK=1` and `IMPECCABLE_NO_UPDATE_CHECK=1`; no download |
| Waivers | `docs/ux-ui/design.md` has **no Design Waivers section**, so there are none |
| Evidence | `probes/validate-detector.out`, `validate-first-visit.out`, `validate-critique.md`, `validate-*.png` |

**Source scan** (the 9 changed UI files): exit `0`, 0 findings.

**Scan matrix.** Viewports are the defaults, because the design authority declares none. Runs used `detect --cdp` with a superadmin session.

| Screen | 1280x800 | 390x844 | Location of the primary findings |
|---|---|---|---|
| Review | exit 2: low-contrast 10, undersized-ui-text 8, tiny-text 2 | exit 2: low-contrast 10, undersized-ui-text 9, tiny-text 3 | All in **shared site chrome** (test-environment label, breadcrumb, online-user badge, footer) or the test-only debug panel; none in the review screen's markup |
| Request | exit 2: `nested-cards` 1 | exit 2: `nested-cards` 1 | **This spec's code:** the info note nested in the form card (**F3**) |

**First-visit pass** (Operate screens; fresh context, no stored consent, cookie banner visible; CTA method = the named main action):

| Screen | 1280x800 | 390x844 | 1366x768 | 375x667 |
|---|---|---|---|---|
| Review (first card's Accept) | pass | **below fold (y=1293)** | pass | **below fold (y=1293)** |
| Request (submit) | pass | pass | pass | **covered (site-wide cookie banner)** |

The review screen's mobile `below fold` comes from the shared admin side menu rendering above the content. The request page's `covered` comes from the site-wide cookie banner (owner's T11b Q6: leave it, follow-up). Without a `first-visit` waiver, both are **FAIL** under the bridge.

**Critique / audit** (isolated sub-agent; review 26/40, request 28/40):
- **P1:** see F2.
- **P2:** no duplicate-institution check before submit; the Reject dialog's "Common reasons" cannot be inserted; the accept outcome only says "processed"; no Cancel on the request form; no select-all for office countries; the shared admin menu comes before the content on mobile; `nested-cards` (F3).
- **P3, 16 items:** wording drift between the two screens; a "Pending" badge on every card; `.prq` rules duplicated across the two CSS files; no `<main>` landmark on the review page; and others (`validate-critique.md`).

The P2 and P3 items are UX improvements beyond the approved requirements → **advisory**, candidate follow-ups (W6).

### Spec drift

Every intentional difference from the approved design is recorded as an execute-time amendment with a closure sweep (`requirements.md` Decision Log, 2026-11-19 / 20):
- AC FN-008 counts one email per Global Unit;
- budget amended twice;
- outcome keys and the POST bridge moved to T07;
- the admin route takes no explicit `logErrorStatus`;
- the T08 summary, input and scrim notes;
- `aria-disabled`;
- the label and title keys;
- the UI-001 / UI-005 readings;
- the T11b fixes, rounds 1–3.

There is no unrecorded drift.

**Cross-document figure check:**
- Premise ledger: 25 rows = "Verified 24 · UNVERIFIED 1" ✔.
- Task count: 12 in all three documents ✔.
- Product diff +5,501 LOC against the amended ~5,800 ceiling ✔.

**Proposal alignment:** intent (MARLO is the source of truth; superadmin-only; branded emails) delivered; non-goals respected (CLARISA REST endpoints and admin-view deletion untouched).

## 9. Test Evidence Summary

| Layer | Evidence |
|---|---|
| Unit | 5 new or extended suites; 1,360 total green; falsifiers executed red-then-green per task (`execution.md`) |
| Live integration | T04 (rollback; lock + refresh; office batch), T11a (staging falsifier C8 → 403; every AC), T11b (redesigned UI; six sent emails) |
| Rendered / a11y | Overflow at 390 / 1280; keyboard dialog walk; focus ring; owner visual approval |
| Design gate | Detector, first-visit and critique (§8) |
| Missing | No `test-report.md` (W1) |

## 10. Agent Guide / Constitution Impact

| Item | Result |
|---|---|
| Module guide | `docs/specs/domain/admin/agent-context.md` gained the `marloInstitutions` section (T12), verified against the code |
| Catalog | `reports/ai-context/role-assignment-email-catalog.md` §17 ✔ |
| Constitution impact | No new module and no boundary move. **WARN W4:** `docs/ux-ui/design.md` §7 still describes the pre-token design (DD-13), declares no viewports and has no *Design Waivers* section. Pending `/akili-constitution` / epic on `staging` |
| CodeGraph | re-index pending (`/akili-archive`) |

## 11. Remediation

| ID | Severity | Fix | Where | Effort |
|---|---|---|---|---|
| **F2** | FAIL (P1 security) | Render the website as a link only when it starts with `http://` or `https://`, otherwise as plain text. Optionally normalise or validate the scheme on save in `PartnersSaveAction.validate()` and `editPartnerRequest` | `institutionRequestMacro.ftl` (+ action / manager) | XS |
| **F3** | FAIL (detector, own code) | Make the request page's info note a non-card element (no border or radius card inside the form card), then re-scan | `partnersSave.ftl` / `partnersSave.css` | XS |
| **F1-a** | FAIL (first-visit, request @ 375x667) | Site-wide cookie banner (owner Q6: leave it). Needs an owner-approved `first-visit` waiver row (`partnersSave @ 375x667`) in `docs/ux-ui/design.md`, a constitutional file written on `staging`, **or** a site-wide banner change outside this spec | owner + `/akili-constitution` | S |
| **F1-b** | FAIL (first-visit, review @ 390x844 and 375x667) | The shared admin side menu renders above the content on mobile. Either a waiver (`marloInstitutions @ 390x844, 375x667`) or move the menu after the content on mobile (shared admin layout; outside this spec) | owner | S |
| **F1-c** | FAIL (detector exit 2, review) | All primary findings are in shared site chrome. Either waiver rows by selector in `docs/ux-ui/design.md`, or a site-chrome contrast fix (epic) | owner + `/akili-constitution` | S |
| W1 | WARN | Run `/akili-test` for a formal `test-report.md` (optional) | — | — |
| W2, W3 | WARN | Owner runs the OPS-002 and P-25 production checks before release (`execution.md` `## Release note`) | owner | — |
| W4 | WARN | `docs/ux-ui/design.md`: §7 tokens, viewports, a *Design Waivers* section (epic) | `staging` | — |
| W5 | WARN | Tick `tasks.md` §9 Definition of Done once the FAILs close | spec | XS |
| W6 | WARN (advisory) | Record the critique's P2 items as follow-ups | `execution.md` | XS |

### 11.1 Owner-approved waivers (2026-11-20) — pending authority write

The owner answered "arregla todo" to the remediation options. That answer approves the F1 waivers below. Each row follows the bridge's *Design Waivers* shape and is **pending the authority write**: it is written into `docs/ux-ui/design.md` § *Design Waivers* on `staging` through `/akili-constitution`, because constitutional files are never edited on a spec branch. Until that write lands, re-validation reads each matching finding as `waived (approved, pending authority write)`, not `waived (design.md)`.

| Rule | Scope | Reason | Approver | Date |
|---|---|---|---|---|
| `first-visit` | `partnersSave @ 375x667` | Site-wide cookie banner covers the submit on first visit only; one click clears it (owner T11b Q6). Site-wide banner fix is a separate follow-up | owner | 2026-11-20 |
| `first-visit` | `marloInstitutions @ 390x844`, `marloInstitutions @ 375x667` | The shared admin side menu renders above the content on mobile, so Accept falls below the fold. Shared admin layout, out of this spec | owner | 2026-11-20 |
| `low-contrast`, `undersized-ui-text`, `tiny-text` | Shared site chrome on `marloInstitutions`: test-environment label, breadcrumb, online-user badge, footer, test-only debug panel (selectors in `probes/validate-detector.out`) | Shared chrome outside the spec's markup; a site-chrome contrast fix is an epic (with DD-13) | owner | 2026-11-20 |

The `nested-cards` finding (F3) and the security finding (F2) are **fixed, not waived**.

## 12. Archive Readiness Recommendation

**Not ready.** The path to archive:

1. Fix **F2** and **F3** on this branch: Implementer → Reviewer, then re-scan the request page.
2. Owner decision on **F1**: approve `first-visit` and shared-chrome waivers. They are written on `staging` through `/akili-constitution`, because `docs/ux-ui/design.md` is constitutional and is never edited on a spec branch. Recording the approved waivers in this report with their pending status lets validation re-read them as `waived (pending authority write)`. Alternatively, scope the shared-chrome fixes as their own spec.
3. Re-run `/akili-validate changes/revive-local-partner-request-approval`.
4. Then `/akili-archive changes/revive-local-partner-request-approval`.

## 13. Re-validation (2026-11-20)

| Finding | Remediation | Evidence | Result |
|---|---|---|---|
| F2: unvalidated `href` | `institutionRequestMacro.ftl` gains a `websiteLink` macro: a link only for trimmed, lower-cased `http://` / `https://`, otherwise escaped text. It is used at both card sites (partner `webPage`, office `institution.websiteLink`). **Attempt 2:** the Reviewer found the same sink in `global/js/allInstitutionsPopup.js`, one of the review page's scripts. Five unsafe sites were fixed there: the link is built with `.attr()` and the same scheme check, with `rel="noopener noreferrer"`; the name is a text node; flag icons are built with `.attr()`; acronym / id / type are HTML-escaped. Its cache-buster went from `?20201123` to `?20261120` | `probes/validate-fix-*`, `validate-fix2-*`. Live: `javascript:alert(1)`, `x' onmouseover='alert(2)` and `<img src=x onerror=alert(3)>` render as text with 0 dialogs; `https://example.org` renders as a link. **Falsifier:** the HEAD script re-injects all three (red) | **PASS** (Reviewer attempt 2) |
| F3: `nested-cards` | The request page's help note became `.prq-help`: no border, radius, shadow or fill; `--marlo-info-text` at about 7:1. Cache-buster `?20261120-1` | Detector source scan exit 0; request page URL scans at 1280x800 / 390x844, default and first-visit, exit **0**; one advisory, the shared cookie-banner `low-contrast` (covered by the waiver class) | **PASS** |
| F1: first-visit + shared chrome | Owner-approved waivers (§11.1) | `validate-first-visit.out`, `validate-detector.out` | **WARN W7**: `waived (approved, pending authority write)`. Becomes PASS once the rows land in `docs/ux-ui/design.md` on `staging` |

**Gates after remediation:**
- Gate 3: `ok … partnersSave.ftl:5 - ?20261120-1` and `ok … allInstitutionsPopup.ftl:29 - ?20261120`.
- `node --check` OK.
- No Java changed, so the build and the 1,360-test suite from §5 still stand.
- The probe files contain no personal data, the environment is clean, and `aiccradb2` is unchanged.

**Open WARNs (accepted or followed up):**

| ID | Item |
|---|---|
| W1 | No `test-report.md` |
| W2 | OPS-002 pre-release check (owner) |
| W3 | P-25 pre-release check (owner) |
| W4 | `docs/ux-ui/design.md` drift: §7 tokens, viewports, no *Design Waivers* section |
| W5 | `tasks.md` §9 Definition of Done boxes |
| W6 | Critique P2 follow-ups |
| W7 | Waivers pending the authority write |

All of them are recorded in `execution.md` `## Follow-ups` (items 21–25) or under `## Release note`.

**New follow-ups (advisory):**
- `swagger/dist/js/request.js:524` builds an `href` the same unsafe way, outside the review page.
- The website scheme is not checked on save, so every future render site of that value has to repeat the guard.
- `escapeCell` must not be reused inside HTML attributes, because it leaves quotes unescaped.

**Archive readiness:** ready, provided the owner accepts W1–W7. Next: `/akili-archive changes/revive-local-partner-request-approval`. The W7 waivers, the W4 drift and the release checks W2 / W3 must still be done on `staging` and in production.

