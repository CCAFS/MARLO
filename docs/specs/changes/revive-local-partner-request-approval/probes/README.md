# CHG-PARTREQ-001 — probes

## T04 — Live proof of atomicity and locking (settles P-5 and P-21)

Run on 2026-11-19, 16:48–17:03 local (UTC-5), base commit `d4dd9c8a3b`, branch `revive-local-partner-request-approval`.
**Verdict: PASS.** (a) rollback, (b) one institution under two concurrent accepts, (c) commit before the mail, and the
office batch, all passed; the falsifier (lock without refresh) reproduced the JD-1 duplicate. No production code is changed
in the checkout: the three code variants were built and run from a throwaway `git worktree` (see "Setup").

### Files

| File | What it is | Result |
|---|---|---|
| `t04-probe.py` | The probe. Real HTTP requests (login, POST) plus separate MySQL connections. Cases `accept-once`, `accept-race [gate]`, `accept-order`, `office-batch`, and the readers `sqllog`, `email-log`. Refuses to run unless the database name contains `partreq_test`; the login is read from `~/.marlo-local-credentials` at run time and is never printed or written | — |
| `t04-falsifier-lockonly-gate.out` | **Falsifier, run first (red).** `findForUpdate` with the `refresh` replaced by `buildLockRequest(PESSIMISTIC_WRITE).lock(entity)`: the row is locked, the state is not re-read. Command: `python3 -I t04-probe.py accept-race "T04 probe lockonly 2 gate" gate`. Two POSTs released together while a third connection holds the row lock | **2 institutions** (11898, 11899) and 2 headquarters locations for one request; `institutions` 10974 → 10976, `institutions_locations` 11295 → 11297. The defect JD-1 is reproduced |
| `t04-a-fault-injection.out` | **(a)** Real `findForUpdate` (refresh). A temporary `throw new IllegalStateException` after the institution, location and copy saves in `acceptPartnerRequest`. Command: `python3 -I t04-probe.py accept-once "T04 probe fault 1"` | `institutions` 10976 → 10976, `institutions_locations` 11297 → 11297, `partner_requests` 5083 → 5083, request still pending (`active=1 acepted=NULL`). The log shows the institution (11900) and the location inserted, then `ROLLBACK` |
| `t04-b-concurrent-gate.out` | **(b)** Clean code. Two POSTs for one pending request released together from a barrier (separate sessions) while a third connection holds `SELECT … FOR UPDATE` on the row, so both requests are provably blocked inside the manager. Command: `python3 -I t04-probe.py accept-race "T04 probe clean 1 gate" gate` | Start spread 1.3 ms, each started before the other ended; 2 sessions waiting on the row lock when released; **1 institution** (11901), 1 location (`institutions` 10976 → 10977, `institutions_locations` 11297 → 11298); the other request `ALREADY_HANDLED` (application log line at the end of the file) |
| `t04-b-concurrent-natural.out` | **(b)** Same, no gate, three runs (`T04 probe clean natural 1..3`) | Start spread 0.1 ms each, both `for update` statements issued within 33 µs of each other; each run +1 institution, +1 location; the other request `ALREADY_HANDLED` each time (3 log lines at the end of the file) |
| `t04-c-ordering.out` | **(c)** Clean code. One POST while a second connection polls the institution row (208 polls, about 3 ms apart). Command: `python3 -I t04-probe.py accept-order "T04 probe order 1"`, then `email-log` | The first poll that sees the row also sees its location and `acepted=1` (+523.1 ms; the previous poll, +517.5 ms, saw nothing): visible at the `COMMIT` (17:02:27.484), atomically. The message was built at 17:02:27.541 (Message-ID epoch milliseconds, application log `SendMailS` 22:02:27.542Z): **58 ms after the commit**; `email_logs` row 18271 written by the mail thread at 17:02:28, `succes_email=1` |
| `t04-office-batch.out` | Forward pointer of T01 / T06. Two pending OFFICE requests of institution 11897 for country 78 (France), one batch to `/acceptOfficesRequest.do`. Command: `python3 -I t04-probe.py office-batch "T04 probe office 1"` | `{"outcome":"done","outcomes":{"5143":"done","5144":"skippedExisting"},"success":true}`; `institutions_locations` 11302 → 11303, exactly **one** location of the institution in the country; request 5143 `acepted=1`, 5144 `acepted=0` with the justification "The InstitutionLocation ID:12915 already exist in the system" (relies on the identity insert running at once, `findByLocation` seeing it in the same transaction) |

### SQL excerpt (`for update`)

From `t04-b-concurrent-gate.out` (MySQL general log, `log_output=TABLE`; the lines of the first run of the two request
connections, 27989 and 27990, and of the holder, 28006):

```
17:01:53.687039  thr 27990  select partnerreq0_.id as id1_200_0_ ... from partner_requests partnerreq0_ where partnerreq0_.id=5134 for update
17:01:53.687087  thr 27989  select partnerreq0_.id as id1_200_0_ ... from partner_requests partnerreq0_ where partnerreq0_.id=5134 for update
17:01:53.718159  thr 28006  COMMIT                      (the holder releases the row)
17:01:53.727669  thr 27990  insert into institutions ...
17:01:53.733675  thr 27990  insert into institutions_locations ...
17:01:53.829108  thr 27990  COMMIT
17:01:53.890438  thr 27989  COMMIT                      (the loser: no insert, no update)
```

The plain load of the request is satisfied from the Hibernate session (OpenSessionInView loaded it in `prepare()`), so no
second query appears: the `for update` refresh is the only read inside the transaction, as T01's forward pointer expected.
When the row is not in the session (not exercised here) `findForUpdate` issues the plain load first.

### What this settles

- **P-21.** `session.refresh(entity, LockMode.PESSIMISTIC_WRITE)` is issued as a full-row `SELECT … FOR UPDATE`, the
  blocked request re-reads the committed state, and the pending check then refuses it. Without the refresh (falsifier) the
  same lock gives two institutions: the stale state from `prepare()` passes the check. DD-3 stands; the native
  `SELECT … FOR UPDATE` fallback is not needed.
- **P-5.** On `addPartner.do` the manager's `@Transactional` is the only transaction. In every run the sequence per request is
  `SET autocommit=1` reads of `prepare()`, then `SET autocommit=0` immediately before the `for update`, and `COMMIT` (or `ROLLBACK`
  in (a)) at the return of the manager method; the mail is built after it.

### Setup

- **Database.** `aiccradb_partreq_test`, a full copy of `aiccradb2` (623 tables, institutions 10974, institutions_locations
  11295, partner_requests 5079 at the start); `aiccradb2` was checked unchanged afterwards (10974 / 5079). Every write went to
  the copy. `marlo-dev.properties` pointed at it only for the run and was restored byte for byte (diff against the backup
  empty, checksum `e2b171ae9775`).
- **One change to the copy's schema.** `ALTER TABLE institutions ALTER COLUMN created_by SET DEFAULT 0`. The `Institution`
  entity does not map `created_by`, and the column is `NOT NULL` with no default, so on a MySQL in strict mode (the local
  8.0.43, `STRICT_TRANS_TABLES`) the very first accept failed with `Field 'created_by' doesn't have a default value` and rolled back.
  The data shows that production inserts relied on the implicit `0` (7075 of 10974 institutions have `created_by = 0`). The
  default `0` reproduces that. **Finding for the Leader, outside T04:** accept works only on a server that is not in strict mode
  or on a schema whose column has a default; `aiccradb2` has no default. See the report.
- **Code variants.** Falsifier, fault and clean were each built with `scripts/run-marlo-java17.sh` (clean, `mvn install
  -DskipTests`, `cargo:run`) from a throwaway `git worktree` at `d4dd9c8a3b` outside the repository folder, with a copy of the
  gitignored `marlo-dev.properties`. The first attempt in the main checkout produced a web application that did not start
  (`WebAppInitializer` and about twenty other classes in `marlo-web/target/classes` were Eclipse-compiler stubs: "Unresolved
  compilation problems: MarloDatabaseConfiguration cannot be resolved"), written after the Maven compile by the VS Code Java
  language server that watches that folder; the worktree is outside its reach. The worktree was removed afterwards; the checkout's
  `git diff -- marlo-data marlo-web` is empty.
- **MySQL general log** (a global setting, default `OFF`, `log_output=TABLE`): switched on only for the duration of each request
  and off again before the report; `@@global.general_log` is `0` again. It was not truncated: the table held 434 earlier rows,
  the probe reads its own rows by time and thread.
- **Mail.** The local environment sends: `isProduction=false` replaces every recipient with `email.notification`, so each accept
  sent one test mail to that mailbox (clean runs: 5 mails). `email_logs` is written by the mail thread after the send outcome.
- **Requests.** `POST /superadmin/addPartner.do` with `requestID`, `edit=true`, `phaseID=403`; `POST /acceptOfficesRequest.do` with
  `countryOfficePOJO.institution.id`, `countryOfficePOJO.ids`, `phaseID`, `edit=true`. Login: `POST /login.do` (a GET of `login.do`
  sets no cookie).
- **Falsifier reading.** The brief says "refresh line removed (lock only)". Removing the line alone would leave neither a lock nor
  a refresh; the variant run is the one that keeps the lock and drops the refresh (`buildLockRequest(...).lock(entity)`), which is
  the JD-1 defect as written in DD-3.

### Leader notes (T04 review, 2026-11-19)

- **Missing earlier run.** `partner_requests` was already 5081 before the falsifier run labelled "lockonly 2". The earlier pending pair belongs to the first attempt, which failed on `created_by` (see Setup) before reaching the race. That run produced no evidence and is not indexed.
- **Doubled lines in the falsifier SQL log.** In `t04-falsifier-lockonly-gate.out` each statement appears twice, for example the institution `insert` on thread 27851 at `.123105` and `.123891`. This is an artifact of how the log table was read. The row counts (10974 → 10976) and the two distinct ids (11898, 11899) show two inserts, one per request, not four.

## T11a — End-to-end verification of PR 1 (branch code `cad7c5d4e4`) on a throwaway database

Run on 2026-11-19, 17:46–18:05 local (UTC-5). Application built and run from a throwaway `git worktree` outside the repository
(`run-marlo-java17.sh`, `cargo:run`); the falsifier from a second worktree at `staging` (`3e4d08bd7e`). Both worktrees were removed;
the checkout's `git diff -- marlo-data marlo-web` is empty and `marlo-dev.properties` of the checkout was never edited (the worktrees
carried their own gitignored copy pointing at `aiccradb_partreq_test`).

**Verdict:** every PR-1 AC passes (`execution.md`, T11a, attempt 3).

FN-007 was red on the first run: the edit form sent different field names from the ones the action reads (`t11a-ui-edit.out`). That defect also exists on `staging`. It passes after the T07 edit-field bridge (`t11a-ui-edit-rerun.out`).

### Setup

- **Database.** `aiccradb_partreq_test` only (keeps `institutions.created_by DEFAULT 0`, P-25). `aiccradb2` re-read at the end: unchanged
  (institutions 10974, partner_requests 5079).
- **Users on the COPY (restored at the end from a saved copy; users differing from `aiccradb2` afterwards: 0).** Local password set for
  COPY users 2491 (requester, UI submitter; email is the notification mailbox) and 22 (the non-superadmin attacker, CRP-Admin of unit 45,
  no SuperAdmin role anywhere); email of user 1082 (the superadmin test login, the approver) and of user 22 temporarily set to an
  `example.invalid` address. The superadmin login is read from `~/.marlo-local-credentials` and never printed.
- **User 2491 is not a valid non-superadmin attacker.** It holds the PC role in unit 45 but SuperAdmin roles in units 1, 23, 28, 29, and
  the five routes accepted it (`t11a-sec-nonsa-branch-user2491-superadmin-elsewhere.out`): superadmin authority is not per unit. Kept as
  an observation; the SEC-001 evidence is the run with user 22.
- **Email safety.** Every probe request was created by COPY user 2491 (email = the notification mailbox) with `external_user_mail` empty.
  `marlo.production=false`: `SendMailS` addresses every message to `email.notification` only, with no CC and no BCC (`setTestingRecipient`).
  Every `email_logs` row of the run has `to_email` = that mailbox (shown as `<notification mailbox>`).
- **Switch.** `custom_parameters` 2184 (unit 45 `crp_enable_email_notification`) set to `false` for one run, restored to `true`.
- **MySQL `general_log`** was not enabled (stays `0` / `TABLE`).
- **Browser.** Playwright (`playwright-cli`), session cookie from `t11a-cookie.py`; helpers in `t11a-ui-helpers.sh`. Page JS initialised
  before every click (`jQuery.isReady`, select2 present). The legacy `confirm()` of the accept form and the office accept is accepted with
  `dialog-accept`; the cookie banner is dismissed first (a click under it sends nothing).
- **Probe.** `t11a-probe.py` (`PROBE_MYSQL`, `PROBE_NONSA_USER`, `PROBE_NONSA_PW`, `PROBE_APPLOG`); refuses a database not named `*partreq_test*`.

### Files

| File | What it is | Result |
|---|---|---|
| `t11a-falsifier-staging.out` | **Falsifier, run first.** `staging` code, same COPY, same script and fixtures, non-superadmin user 22: five hand-crafted POSTs | **All five accepted**: reject, edit and both office decisions answer `success:true`, the accept creates institution 11906; rows changed (C8 reproduced) |
| `t11a-sec-nonsa-branch.out` | Same script on the branch code | reject / edit / accept offices / reject offices **HTTP 403**; `addPartner` answers a `Permission denied!` page (HTTP 200); **no row changed** |
| `t11a-sec-nonsa-branch-user2491-superadmin-elsewhere.out` | Same script, attacker 2491 (SuperAdmin in other units) | Accepted: not a non-superadmin; see Setup |
| `t11a-sec-sa-get-branch.out` | Superadmin, GET to the five routes | JSON routes answer `notAllowed`, `addPartner` redirects; fingerprint unchanged |
| `t11a-menus-branch.out` | Menu entry, P-15, CRP-admin route and menu | AICCRA superadmin menu shows *Request Institutions*; the page opens with no `phaseID`; `/admin/AICCRA/marloInstitutions.do` redirects a superadmin to `/superadmin/marloInstitutions.do` and refuses the CRP admin; no entry in the CRP-admin menu (AICCRA, CCAFS) |
| `t11a-fn001-no-clarisa.out`, `t11a-fn001-non-aiccra.out` | FN-001 | Two rows, active; no CLARISA reference in the request's log lines or the action; the same for unit CCAFS |
| `t11a-fn002-new-partner-alert.html`, `t11a-emails-fn009.out` | The six emails as sent (`email_logs.message`) | All six in the shell, no legacy footer, no raw key, no unresolved placeholder |
| `t11a-ui-accept.out`, `t11a-ui-reject.out`, `t11a-ui-office-accept.out`, `t11a-ui-office-reject.out` | Decisions from the **current screen** in a real browser | Accept, replay (`alreadyHandled` text shown after the redirect), reject with / without notification, office accept (Kenya added once, France skipped) and office reject |
| `t11a-ui-edit.out`, `t11a-edit-api.out` | Edit | **From the screen: `outcome:failed`, nothing changes** (field names); with the names the action reads: copy edited, original untouched, late edit `alreadyHandled`, unknown id `notFound` |
| `t11a-switch-off.out` | Switch off | Both requests stored, all four decisions applied, 0 emails; switch restored |
| `t11a-office-multiunit.out` | FN-008 per-unit email | Two emails per batch (unit 45 and unit 47), accept and reject |
| `t11a-ops001.out` | OPS-001 | One ERROR line per forced failure, with the action and the request id, no email address |
| `t11a-ui-edit-rerun.out` | **FN-007 re-run** after the uncommitted edit-field bridge (`t07b.diff`: the screen now posts `institutionName` / `institutionAcronym` / `institutionWebPage` / `institutionTypeID` / `countryID` / `justification`), HEAD `8dd63c27a9` + patch, real browser. `t11a-ui-edit.out` stays as the red run | **PASS** (4 of 4): edit → `done`, copy changed and marked modified, original unchanged, page reload shows the edited values; late edit → `alreadyHandled`, no change; empty justification → client-side check, no request sent |
| `t11a-fn001-message.out` | **FN-001 'request sent' message**, real browser, AICCRA and CCAFS, email switch off for both units on the COPY, rows read right after each submit | **PASS**: the page shows 'The new partner request was sent succesfully. You will receive a confirmation message as soon as it has been processed.' (literal in `partnersSave.ftl`, no i18n key); two rows each (original `partner_request_id` NULL, copy pointing to it, both active); 0 emails |

In the helper files `t11a-cookie.py` and `t11a-ui-helpers.sh`, `<home>` and `<scratchpad>` stand for the local home and scratch paths; names of the COPY users are shown as `<requester>`.

## T08 - Review screen markup and CSS (`.prq`)

Run on 2026-11-19, 20:20-21:05 local (UTC-5), base commit `d7378d5cda` plus the uncommitted T08 change. The application was built and run from a
throwaway `git worktree` outside the repository, pointed at `aiccradb_partreq_test` (a copy of `aiccradb2`); the worktree was removed afterwards
and the checkout's `marlo-dev.properties` was never edited. Browser: Chrome through `playwright-cli`, device pixel ratio 1, effective CSS px.

**Fixtures on the copy (removed afterwards).** Two pending partner requests (one edited: name, institution type and country differ from the original)
and one institution with two pending office requests, all named `T08 probe ...`. For the capture every other pending request on the copy was
temporarily set inactive (restored afterwards) and the requester shown is the copy user renamed `Test Requester` (restored afterwards). The
Debug Panel and the cookie banner of the test environment were hidden in the screenshots.

### Files

| File | What it is | Result |
|---|---|---|
| `t08-baseline-old.out` | **Baseline, taken before editing.** The old page (HEAD) with the same fixtures: `scrollWidth` vs `clientWidth` at 390x844 and 1280x800, Figtree check | 390: 390 vs 390; 1280: 1280 vs 1280. The old page did not overflow; `document.fonts.check('600 16px Figtree')` true |
| `t08-measure-new.out` | The new page, same fixtures, same measurement | 390: 390 vs 390 (`overflow` false); 1280: 1280 vs 1280; Figtree 600 and 400 loaded |
| `t08-measure-empty.out` | The new page with nothing pending (both lists empty), plus its text | No overflow at either width; "0 partner requests · 0 institutions with office requests" and one empty state per list |
| `t08-falsifier.out` | **Falsifiers, executed and reverted.** (1) one `btn-success` left in the macro; (2) `.prq-card { min-width: 600px }` | (1) legacy grep `1`, back to `0` after the revert; (2) at 390 px `scrollWidth` 632 vs `clientWidth` 390, `overflow` true (red), and 390 vs 390 again after the revert |
| `t08-edge-null-and-escape.out` | A request with no institution type, no country, no requester and a name containing `<b>`, `&` and quotes (temporary fixture) | The card renders ("Unknown", "Not provided"), the edit selects fall back to the placeholder, the name is escaped (no injected `<b>`), and no "changes" block is drawn when nothing differs. The first run of this probe found a defect (the requester helper received a string where it expects a hash, HTTP 500); fixed |
| `t08-focus-contrast.out` | Focus ring on every control type, contrast ratios of the main text/background pairs, JS errors on load | Every control matches `:focus-visible` with a 2 px brand outline (the checkbox had the browser's default ring until its rule was raised); ratios 4.67-6.98 (all at or above 4.5:1); the only console errors are the external chat widget (CORS) |
| `t08-review-1280.png`, `t08-review-390.png` | Full-page screenshots of the review page with the fixtures | Header with counts, two partner cards (one edited, original vs edited for the three changed fields only), one office card |
| `t08-edit-form-1280.png` | The edited card with its inline edit form open (the form is `hidden` until T09's JS opens it; opened here from the console) | Fields prefilled from the copy |
| `t08-empty-1280.png`, `t08-empty-390.png` | Both empty states | One empty state per list |
| `t08-dialog-reject-390.png`, `t08-dialog-reject-1280.png` | The reject dialog markup, opened from the console (`hidden` removed) because the JS that opens it is T09 | Required marker, help text, notify checkbox (checked), common reasons |

Not measured here: the detector (`impeccable detect`) was unevaluated, the launcher failed with `spawnSync ... EAGAIN` on every call (`engine-probe`
answered `impeccable-engine 0.1.18` but `detect` did not run).

## T11b - End-to-end verification of PR 2 and material for the owner's visual review

Run on 2026-11-20, 08:13-09:20 local (UTC-5), HEAD `efbf01c7c7` (no working-tree change to product code). Application built and run from a throwaway `git worktree`
outside the repository, pointed at `aiccradb_partreq_test`; the worktree was removed and the checkout's `marlo-dev.properties` was never edited. Browser: Chromium through
`playwright-cli`; every click came after `jQuery.isReady`; the keyboard walk never focused a dialog with the mouse. `aiccradb2` re-read at the end: unchanged (institutions 10974,
partner_requests 5079).

**Fixtures on the copy (all removed or restored).** Requester = COPY user 2491 renamed "Test Requester" (its email is the notification mailbox, `external_user_mail` empty on every request);
approver = COPY user 1082 renamed "Test Approver" with an `example.invalid` email. Every other pending request was set inactive during the run and reactivated (62 ids, checksum equal
to the saved list); probe institutions, locations and requests (ids above the starting maxima) were deleted: institutions 10990, locations 11316, partner_requests max id 5272 as at the start.
User rows differ from the saved originals: 0. Phase 405 `editable` set to 1 for one opener attempt and restored to 0. `general_log` stayed 0/TABLE.

**Email safety.** `marlo.production=false`: `SendMailS` addresses every message to `email.notification` only. All 16 `email_logs` rows of the run (18304-18319) have `to_email` = the notification
mailbox, no cc, no bcc, `succes_email=1`. One alert (18316, the annual-report opener) was created by user 1082 (not 2491) before the session was switched; it was an alert to the mailbox
and the request was never decided.

### Files

| File | What it is | Result |
|---|---|---|
| `t11b-live.out` | Measurements (overflow, primary-action outcome with the consent banner visible, legacy-markup check, `<title>`, opener regression, flow results, email ids) | See below |
| `t11b-keyboard-walk.out` | Keyboard-only walk of the reject and accept dialogs, plus the checkbox focus ring | Focus moves in, is trapped, Escape closes and returns focus; empty justification shows the error and sends nothing |
| `t11b-review-{1280,390,1366x768,375x667}.png`, `t11b-empty-{1280,390}.png`, `t11b-similar-card-1280.png` | Review screen at four viewports, empty states, similar-institutions note | No horizontal overflow at any width |
| `t11b-request-{1280,390,1366x768,375x667}.png`, `t11b-request-errors-1280.png`, `t11b-request-success-1280.png` | Request page at four viewports and its states | Primary action covered by the cookie banner at 375x667 only |
| `t11b-dialog-*.png`, `t11b-outcome-*.png`, `t11b-expired-session-1280.png`, `t11b-focus-checkbox-1280.png`, `t11b-office-chip-nolocations-1280.png` | Dialogs, inline outcomes, expired session, focus ring, office chip case | See findings |
| `t11b-email-<kind>.html` / `.png`, `t11b-assets/` | The six emails as sent (`email_logs.message`, mailbox written as `<notification mailbox>`, images made relative), and their renders | All in the shell, no raw key, no unresolved placeholder |
| `t11b-review.html` | Review sheet for the owner: screenshots, the six emails side by side with their subjects, open visual questions | Open in a browser from this folder |
| `t11b-ui-helpers.sh`, `t11b-cookie.py` | The helpers used (paths shown as `<repo>`, `<scratchpad>`) | - |

### AC evidence

| AC | Evidence | Result |
|---|---|---|
| FN-001 (through the redesigned page) | opener regression in `t11b-live.out`: project, AR (real clicks), POWB and capdev (link target) each stored an original + copy, both active | PASS |
| FN-002 / FN-003 | emails 18304, 18307 (subjects and bodies in the sheet) | PASS |
| FN-004 / FN-005 / DA-001 | keyboard accept of 5294: +1 institution, +1 headquarters location, copy `acepted=1`; replay: "already handled", nothing created | PASS |
| FN-006 | reject with notify (email 18308), reject without notify (no new email row) | PASS |
| FN-007 | edit from the new inline form: copy changed and marked modified, original unchanged, card shows the diff | PASS |
| FN-008 | accept batch (skippedExisting + done), reject batch across two units (emails 18314, 18315) | PASS |
| FN-009 | six emails rendered from `email_logs.message` | PASS (owner to approve the look) |
| FN-010, SEC-001..003, OPS-001, FN-011 | not re-run: the redesign touches views, JS and CSS only (T11a evidence stands) | not re-run |
| UI-001 | rendered DOM: 0 `list-group`, 0 `btn-success`, 0 `btn-danger`; no `confirm(`/`alert(`/`noty` in the JS; 1 glyphicon in the shared header | PASS, pending the owner's reading (shared header out of the two pages' scope — review sheet question 8) |
| UI-002 / UI-003 | review screenshots, similar-institutions card | PASS |
| UI-004 | dialogs, empty justification error, inline outcomes done / already handled / not authorized (401) | PASS |
| UI-005 | request page screenshots, errors, success; save failure state evidenced in T10; an empty submit sends a POST and stores nothing (server validation) | PASS, pending the owner's reading ("not sent" = no request stored — review sheet question 9) |
| UI-006 | scrollWidth = clientWidth at 390, 375, 1280, 1366; focus ring; keyboard walk | PASS; but see the cookie banner finding |

### Findings (outside the ACs)

1. The consent banner covers the primary action at 375x667 (request page) and the first card's Accept at 1366x768 (review). Both clear after one click on the banner.
2. After a session expires the server answers 401 with an empty body and the page shows "You are not allowed to review these requests." (no "sign in again").
3. Accepting offices for an institution with no offices shows no "Existing offices" chip until reload.
4. The office-accepted email lists a skipped (already present) country as added.
5. Subjects: alerts read `[AICCRA - AICCRA]`; the office decision emails of both units (AICCRA, AICCRA_III) read `[AICCRA]`.
6. The new-partner alert prints an empty "Website:" line when no website was given.
7. A decision made with the keyboard moves focus to the success note (documented in `t11b-keyboard-walk.out`).
8. Validation on the request page is a server round trip (a POST is sent and nothing is stored), not a client-side block.

### T11b fixes after the owner's review (2026-11-20)

Four changes, re-checked live on a throwaway worktree (HEAD + the uncommitted diff) against `aiccradb_partreq_test`; worktree and app removed, fixtures restored. Email safety as above:
`marlo.production=false`, the requester's address is the notification mailbox, no cc/bcc. Email ids 18320 and 18321 (both to the notification mailbox, `succes_email=1`).

| File | What it is | Result |
|---|---|---|
| `t11b-fixes-live.out` | Computed styles, the two alert subjects and bodies, the 401 note, falsifier (b) | Outline Reject; subjects without the repeated unit; "Website: Not provided"; 401 shows the session-expired note |
| `t11b-fixes-falsifier-a.out` | Website default removed: the new test goes red | 1 failure |
| `t11b-fixes-review-{1280,390}.png`, `t11b-fixes-dialog-reject-{1280,390}.png`, `t11b-fixes-dialog-reject-offices-1280.png` | Review screen and dialogs | Outline Reject on both card kinds; dialog confirm stays solid |
| `t11b-fixes-expired-session-1280.png`, `t11b-fixes-falsifier-401-old-mapping-1280.png` | A real 401 (session cleared) with the fix, and with the old mapping | New text / old text |
| `t11b-fixes-email-{newpartner,newoffice}.{html,png}` | The two alerts from `email_logs.message` (mailbox replaced) | Subjects `[AICCRA] Partner verification - ...` / `[AICCRA] Add office location (KE, PE) to ...` |
| `t11b-fixes-dialog.sh`, `t11b-fixes-expire.sh` | Helpers used | - |

`t11b-review.html` now opens with "Changes after your review" and marks each of the nine questions (changed / follow-up T12 / agreed).

### T11b fixes, second round (2026-11-20)

AICCRA and AICCRA_III say "cluster" with the letter C in the new-office alert and in the project request source; every other unit says "project" with the letter P. All of it comes from i18n
(`marloRequestInstitution.mail.newOffice`, `marloRequestCreation.source.project`, global value plus overrides in `custom/aicrra.properties` and `custom/aiccra3.properties`); no unit check in Java (NF-001).
Re-checked live on a throwaway worktree (HEAD + the uncommitted diff) against `aiccradb_partreq_test`; worktree and app removed, fixtures restored. Email safety as above (`marlo.production=false`,
requester address = notification mailbox, `external_user_mail` empty). Email ids 18322, 18323, 18325 (deleted from the copy at cleanup).

| File | What it is | Result |
|---|---|---|
| `t11b-fixes2-live.out` | The three live cases: stored source, alert subject and body, review card | (i) "Cluster C102076 - ..." (ii) "Requested from cluster: C102076" (iii) "Project P1 - ..." |
| `t11b-fixes2-email-{newpartner-aiccra,newoffice-aiccra,newpartner-ccafs}.{html,png}` | The three alerts from `email_logs.message` (mailbox replaced) | As above |
| `t11b-fixes2-review-1280.png` | Review screen with the partner cards of AICCRA and CCAFS and one office card | Source line follows the unit's word |

Finding for the owner: the stored source of an office request is still `Project: (<id>) - <title>` for AICCRA too (`RequestCountryOfficeAction`, line 116), so the office card on the review screen says
"Project: (102076)" while the alert says "cluster C102076". Not changed here (not in the task); candidate for T12.

