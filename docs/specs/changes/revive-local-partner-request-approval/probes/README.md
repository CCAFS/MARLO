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
