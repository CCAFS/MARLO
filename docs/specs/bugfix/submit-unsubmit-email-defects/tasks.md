# Submit and unsubmit email defects — Tasks

**Spec ID:** BUG-SUBMIT-EMAIL-001
**Status:** Approved 2026-11-12
**Owner:** Kenji Tanaka (IBD)
**Last Updated:** 2026-11-12
**Requirements:** `requirements.md` · **Design:** `design.md` (judgment round 1 APPROVED, Adjust round 1 applied)
**Branch:** `email-layout-default` (R1 answered 2026-11-12 by the owner: same branch)
**Depth:** Lite (Bug Mode). Every defect gets a regression test that is red before the fix and green after

> **Answer first.** Five tasks:
> - T1 measures the real report time. Deferred 2026-11-12 to the post-deploy log read in T05.
> - T2 builds the bounded PDF fetch.
> - T3 wires the submit to a per-submit summary instance.
> - T4 fixes unsubmit.
> - T5 verifies everything locally and leaves the live attachment check for after the deploy.

## Shared gates (cited by every code task as "Gates")

| Gate | Command (failure-only form) |
|---|---|
| G-compile | `rm -rf marlo-web/target/classes marlo-web/target/test-classes marlo-data/target/classes && mvn -q -o -pl marlo-data,marlo-web -am test-compile -DskipTests`. Count `Compiling N source files` (~2430 / ~1063) and read Maven's own exit code |
| G-check | marlo-verify `checkstyle.sh` on each changed `.java`, compared with the same file at the branch base. Only the delta counts |
| G-test | `mvn -q -o -pl marlo-web -am test -Dtest=<classes> -Dsurefire.failIfNoSpecifiedTests=false` (keep `-am`) |
| G-i18n | Every added or changed `.properties` line reviewed: no single `'`, no `{` that is not an argument |
| G-hygiene | marlo-verify `java-hygiene-check.sh` on the changed files (null-safe log arguments, no Spanish, no CRLF flip) |

All gates run after the Implementer reports, never alongside it.

---

### BUG-SUBMIT-EMAIL-001-T01 — Measure the cluster report time on aiccratest
- **Status:** [~] deferred 2026-11-12 (owner: no aiccratest access now; "si, de acuerdo"). The measurement moves to T05's post-deploy check, read from the `fetchClusterReportBytes` INFO line (elapsed ms). Constants start at 10 × 2 000 ms · **Size:** XS · **Depends on:** none · **Module:** none (measurement)
- **Requirements:** NF-001 (sets the budget values)
- **Design:** §10, P-9
- **Scope:**
  - The owner downloads the cluster summary (`reportingSummary.do`) for 3 clusters on aiccratest and times each
    request, from click to the first PDF byte (browser network tab).
  - The Leader records the three times and sets `SUBMIT_POLL_ATTEMPTS` × `SUBMIT_POLL_WAIT_MS`: about 1.5 × the
    slowest time, at least 20 s, at most 40 s.
  - Backend route: none — measurement.
- **Tests:** n/a — measurement.
- **Falsifier:** a recorded time above 40 s → the budget cannot cover it. Escalate to the owner (most submits would fall back to `.noPDF`).
- **Red run:** n/a — measurement, no code.
- **Disqualifier:** a single timing, or timings that vary by more than 2×, is not evidence. Take 3 or more and record
  the spread. A download that returned an error or a 0-byte file is not a timing.
- **Consumers:** none.
- **Review:** checklist — Reviewer checks that the three times and the derived constants are quoted in `execution.md`.
- **Done when:** three timings quoted; constants decided; P-9 settled (verified, or refuted → Pivot Protocol if > 40 s).
- **Skills:** none.

### BUG-SUBMIT-EMAIL-001-T02 — Bounded PDF fetch in `MicroserviceReportAction`
- **Status:** [ ] · **Size:** S · **Depends on:** none (T01 deferred 2026-11-12; constants 10 × 2 000 ms, re-set from the post-deploy log in T05) · **Module:** marlo-web (`action/report/MicroserviceReportAction.java`, test)
- **Requirements:** FN-001 (bytes obtained), FN-002 (null on every failure), FN-003, NF-001, NF-002
- **Design:** DD-2, DD-3, DD-4, §2 row 1, §10, §11
- **Scope:**
  - New `fetchClusterReportBytes(String json, String reportName)` exactly as design §2:
    - config copied from `APConfig` into locals; never `loadData()`;
    - return null at once when json, name, queue URL or S3 URL is null or blank;
    - publish with 5 s connect and handshake timeouts;
    - HEAD poll with 10 × 2 000 ms (re-set in T05 from the post-deploy log), then GET into `byte[]`, each request with 5 s connect and read timeouts;
    - return null on non-200, exception or 0 bytes;
    - on interruption, restore the flag and return null;
    - one INFO line: name, outcome, byte length, elapsed ms.
  - Package-private seams (publish, HEAD, GET, wait) with production defaults.
  - **Do not modify** `sendClusterReportQueueMessage`, `downloadPDFByURL`, `loadData` or any existing field.
  - Backend route: none — project conventions.
- **Tests:** new `MicroserviceReportFetchTest`:
  - (a) ready at attempt 3 → returns exactly the GET bytes; 3 HEADs; 2 waits of `SUBMIT_POLL_WAIT_MS`;
  - (b) never ready → null after exactly `SUBMIT_POLL_ATTEMPTS` HEADs, no GET;
  - (c) GET returns 0 bytes → null;
  - (d) GET non-200 or throws → null;
  - (e) publish throws → null, no HEAD;
  - (f) null or blank json, name or S3 URL → null, no publish;
  - (g) interrupted during the wait → null and `Thread.currentThread().isInterrupted()` is true;
  - (h) a recording `HttpServletResponse` installed in `ServletActionContext` receives no call in any case;
  - (i) the timeouts handed to the publish and HTTP seams equal the constants.
  - G-test `MicroserviceReportFetchTest`.
- **Falsifier:**
  - drop the `isInterrupted` restore → (g) red;
  - call `downloadPDFByURL` instead of the GET seam → (h) red;
  - remove the 0-byte check → (c) red;
  - poll one extra time → (b) red on the HEAD count.
- **Red run:** `predicted — greenfield artifact (observed in T02)`. The tests are written first against a stub
  returning null, and the red of (a) is quoted in `execution.md`.
- **Disqualifier:**
  - a test whose fake ignores the URL or the timeouts it receives proves nothing (assert them, KZ-…auth-flow-2);
  - a green run with real network access is not evidence (the seams must be faked);
  - a test that never installs the response double cannot prove (h).
- **Consumers:** `ReportingSummaryAction:7869` (T03 wires the call); the innovation, study and cluster download
  paths must stay byte-identical. G-review: `git diff` of `MicroserviceReportAction.java` shows only additions.
- **Review:** full — a network path inside a user request.
- **Done when:** (a)–(i) green; the diff of the file is additions only; G-compile, G-check delta 0, G-hygiene.
- **Skills:** marlo-verify, tdd, marlo-commit.

### BUG-SUBMIT-EMAIL-001-T03 — Submit attaches the real PDF from a per-submit summary instance
- **Status:** [ ] · **Size:** M · **Depends on:** T02 · **Module:** marlo-web (`ReportingSummaryAction.java`, `ProjectSubmissionAction.java`, tests)
- **Requirements:** FN-001, FN-002, FN-003 (the submit side), FN-006 (submit), FN-007 (submit), NF-002
- **Design:** DD-1, DD-2, DD-5, DD-8, DD-9, P-3, P-5, P-10
- **Scope:**
  - **First step, before anything is built on it (P-10):** add the `ApplicationContext` constructor parameter in
    place of `ReportingSummaryAction` and run G-compile. Record the result.
  - `ReportingSummaryAction`:
    - add private `pdfForSubmit` and `submitPdf`;
    - add public `generatePdfBytes()`;
    - in `generateAndSendJson()` change only the call at `:7869` to the guarded form (design §2 row 2). Nothing
      else in the file changes.
  - `ProjectSubmissionAction`:
    - add `newSummaryAction()` (`createBean`) and `summaryPdf()` (the same setters as `:366-379` on the fresh
      instance, then `generatePdfBytes()`; an exception is logged and returns null);
    - `pdfAttached` = non-null and non-empty bytes; the send attaches those bytes;
    - title fallback to the unit's `global.untitled` when `projectInfo` or its title is null or blank;
    - `unitTexts()` WARN gains `loggedCrp` id, as a null-safe argument.
  - Backend route: none — project conventions.
- **Tests:**
  - **`ProjectSubmissionEmailTest`.** `FakeSummary` now behaves like production: `execute()` leaves `getBytesPDF()`
    empty, and `generatePdfBytes()` returns known bytes B or null/empty per case. `DrivenSubmission` passes null for
    the context and overrides `newSummaryAction()`.
    - (a) planning, B returned → the send carries exactly B (`assertArrayEquals`), file name and `application/pdf`,
      planning copy;
    - (b) null → `.noPDF` copy, attachment arguments null, no "attached";
    - (c) empty array → same as (b);
    - (d) two submits → `newSummaryAction()` is called twice, and each summary received its own project and phase
      through the setters;
    - (e) null title → "Untitled", no `"null"`;
    - (f) lookup throws → the WARN contains the unit id (log appender), and the email is still sent;
    - existing cases stay green.
  - **New `ReportingSummarySubmitFlagTest`.** On a `ReportingSummaryAction` with a fake `MicroserviceReportAction`:
    `generatePdfBytes()` calls `fetchClusterReportBytes` with the built json and name and returns its bytes. A plain
    `generateAndSendJson()` calls `sendClusterReportQueueMessage(json, name)` and never the fetch (NF-002).
  - G-test `ProjectSubmissionEmailTest,ReportingSummarySubmitFlagTest,UnsubmitProjectEmailTest,SubmitEmailCopyTest,EmailLayoutTest`.
- **Falsifier:**
  - keep `buffer = ByteBuffer.wrap(getBytesPDF())` after `execute()` (today's code) → (a) red, because the
    attachment is empty (observed red, see Red run);
  - reuse a field-held summary instead of `newSummaryAction()` → (d) red;
  - drop the flag guard → `ReportingSummarySubmitFlagTest` download case red.
- **Red run:** (a) and (e) are written first and run against today's `ProjectSubmissionAction`. Expected red for (a): the
  attachment argument is the empty array from `getBytesPDF()`, not B. Expected red for (e): the body contains
  `"null"`. `predicted — greenfield artifact (observed in T03)` for the `FakeSummary` that behaves like production
  (it does not exist yet). The observed reds are quoted verbatim in `execution.md`.
- **Disqualifier:**
  - a `FakeSummary` that still answers `getBytesPDF()` with B would make (a) pass on today's code. That is the
    inert-fixture trap, so the red must be observed;
  - a test that overrides `summaryPdf()` itself skips the setters and does not count for (d);
  - an incremental compile that compiled far fewer than ~1063 marlo-web files is not evidence.
- **Consumers:** `ProjectSubmissionEmailTest:150-215` (the `FakeSummary` and `DrivenSubmission` constructors change);
  `EmailSendDoubles`; `{crp}/submit` (`struts-projects.xml:1140`, built by Struts with constructor autowiring);
  `ReportingSummaryAction.execute()` → `generateAndSendJson()` (download path, NF-002).
- **Review:** full — Java logic on a user flow, Spring wiring, a shared-state fix.
- **Done when:**
  - P-10 compile step recorded;
  - (a)–(f) and the flag test green, with observed reds quoted;
  - the `ReportingSummaryAction` diff shows only the two fields, `generatePdfBytes()` and the guarded line;
  - G-compile, G-check delta 0, G-hygiene, G-test.
- **Skills:** marlo-verify, tdd, marlo-commit.

### BUG-SUBMIT-EMAIL-001-T04 — Unsubmit always addresses the leader, greets correctly, no self-CC, no "null"
- **Status:** [x] done 2026-11-12 (execution.md T04) · **Size:** S · **Depends on:** none (independent of T02/T03; different file) · **Module:** marlo-web (`UnsubmitProjectAction.java`, `global.properties`, test)
- **Requirements:** FN-004, FN-005, FN-006 (unsubmit), FN-007 (unsubmit), NF-003
- **Design:** DD-6, DD-7, DD-8, P-2, P-8, P-11
- **Scope:**
  - One `leader` lookup. TO = the leader's email when present.
  - `values[0]` = the leader's first name, or the unit's `email.greeting.everyone`.
  - Remove both `getId() !=` conditions.
  - CC drops every address equal to the TO (trimmed, case-insensitive) — design §2 row 4 as amended 2026-11-12.
  - Title fallback to `global.untitled` when `projectInfo` or its title is null or blank.
  - The WARN gains the unit id, as a null-safe argument.
  - `global.properties`: add `email.greeting.everyone=all` next to the other `email.*` greeting keys.
  - Backend route: none — project conventions.
- **Tests:** `UnsubmitProjectEmailTest` (send path, recording doubles):
  - (a) the leader is the actor with id 1082, built as two distinct `Long` instances (`new Long(1082)` vs
    `Long.valueOf(1082)`) → TO = leader, "Dear <first>,", CC has no leader address;
  - (b) the same with id 16 → same result;
  - (c) a different leader and actor → TO = leader, CC argument = actor only (today `"actor, leader"`, which `SendMailS.send` reduces to the actor before sending, so the delivered recipients are unchanged);
  - (d) no leader → "Dear all,", no "Dear ,";
  - (e) the leader with a blank first name → "Dear all,";
  - (f) null title → "Untitled", no `"null"`;
  - (g) lookup throws → the WARN contains the unit id;
  - existing cases stay green.
  - G-test `UnsubmitProjectEmailTest,EmailLayoutTest,SubmitEmailCopyTest`.
- **Falsifier:**
  - put back `getId() != getCurrentUser().getId()` → (b) red ("Dear ," and an empty TO, because 16 is a cached
    `Long`);
  - remove the CC filter → (a) red (the leader is in CC);
  - drop the greeting fallback → (d) red.
- **Red run:** (a), (b), (d) and (f) are written first and run against today's code. Expected reds:
  - (a): CC contains the leader's address;
  - (b): the greeting is "Dear ,";
  - (d): the greeting is "Dear ,";
  - (f): the body contains `"null"`.
  The observed reds are quoted verbatim in `execution.md`.
- **Disqualifier:**
  - ids built as `long` literals boxed through the same `Long.valueOf` cache path for 1082 would not reproduce the
    reference inequality. Build them as distinct instances and assert that `a != b` holds in the test setup;
  - a CC assertion on the raw string, without trimming or case handling, is not evidence of "not in CC".
- **Consumers:** `unsubmitProject.do` (`struts-json.xml:678`); `projectSubmit.js` (JSON response unchanged);
  `UnsubmitProjectEmailTest`; `email_logs` readers.
- **Review:** full — Java logic on a user flow; recipients change for one case.
- **Done when:** (a)–(g) green with observed reds quoted; the JSON response and the other recipients are unchanged in
  the diff; G-compile, G-check delta 0, G-i18n, G-hygiene, G-test.
- **Skills:** marlo-verify, tdd, marlo-commit.

### BUG-SUBMIT-EMAIL-001-T05 — Local end-to-end check and the post-deploy acceptance
- **Status:** [ ] · **Size:** S · **Depends on:** T03, T04 · **Module:** none (verification)
- **Requirements:** FN-002, FN-003 (live), FN-004 / FN-005 (live), NF-002 (download smoke), P-10
- **Design:** §10, §11, P-10
- **Scope:**
  - **Local**, following the `docs/infrastructure.md` §6 Local Environment contract, on a throwaway DB copy with
    `crp_email_support_team` true for the units used (mail goes only to the notification address). Nothing else
    may run during this: no concurrent Maven or Cargo.
    - One planning submit. The microservice is unreachable locally, so the expected result is: the submission page
      renders; `email_logs` holds the `.noPDF` copy with no attachment; the log has the `fetchClusterReportBytes`
      outcome line and no `downloadPDFByURL` line.
    - One unsubmit where the leader is the actor, and one of a project without a leader. The stored messages show
      the greeting and recipients as specified.
    - One summary download (`reportingSummary.do`), which must still follow its own path: the log shows
      `downloadPDFByURL` / the queue error as today.
    - Clean up the DB copy.
  - **Post-deploy (owner, after the branch reaches the aiccratest environment):** read the `fetchClusterReportBytes` INFO lines (outcome + elapsed ms) of the first planning submits; this settles P-9 and T01 (re-set the constants if the slowest successful fetch is near the budget). One planning submit. The email
    carries a non-empty PDF that opens, and the page is the submission page. This also answers Q2. It is recorded
    as an open acceptance item if the deploy happens after archive.
  - Backend route: none — verification.
- **Tests:** SQL and log extracts quoted in `execution.md`; the owner's verdict on the post-deploy check.
- **Falsifier:**
  - the submit page shows a PDF or a 404 → FN-003 fails;
  - a planning email with an attachment of 0 bytes → FN-002 fails;
  - the download path shows the fetch log line → NF-002 fails.
- **Red run:** observed at baseline: archived `changes/email-layout-default` T08. The planning submit attached a
  0-byte PDF, and `email_logs` 18395 holds a "Dear ," email.
- **Disqualifier:**
  - a run where `crp_email_support_team` is not true for the units used must stop before any send;
  - a local run cannot prove FN-001 (there is no microservice), so only the post-deploy check closes it;
  - a local check alone is not acceptance of the attachment.
- **Consumers:** n/a — verification.
- **Review:** checklist — the Reviewer checks that every listed outcome has a quoted extract.
- **Done when:** the local outcomes are quoted; P-10 settled; local artifacts removed; the post-deploy check is
  recorded, or tracked as open.
- **Skills:** marlo-verify.

---

## Dependency Graph

```
T01 (deferred to T05 post-deploy)
T02 ─► T03 ─┐
                   ├─► T05
T04 ───────────────┘
```

T04 is independent of T01–T03 (different file) and can run in parallel with T02.

## Coverage (scenario / clause → task)

| Requirement clause | Task |
|---|---|
| AC FN-001 THEN exact bytes, file name, type, planning copy; AND IT MUST be the fetched bytes | T03 (a); T02 (a) |
| AC FN-002 THEN `.noPDF` with the link on failure, timeout or 0 bytes; BUT no attachment and no "attached" | T03 (b)(c); T02 (b)(c)(d)(e)(f)(g); T05 local |
| AC FN-003 THEN nothing written to the response; AND IT MUST hold when the file is not ready | T02 (h)(b); T05 local |
| AC NF-002 THEN downloads still stream with 60 s; AND IT MUST hold with the one guarded line | T02 (additions only); T03 flag test; T05 download smoke |
| AC FN-004 THEN TO = leader and greeting by name for ids 1082 and 16; BUT the leader not in CC | T04 (a)(b)(c) |
| AC FN-005 THEN "Dear all,"; BUT no "Dear ," | T04 (d)(e) |
| AC FN-006 THEN "Untitled"; BUT no `"null"` (submit and unsubmit) | T03 (e); T04 (f) |
| AC FN-007 THEN the WARN contains the unit id and the email is still sent | T03 (f); T04 (g) |
| NF-001 budget bounded and measured | T02 (b)(i); T05 post-deploy log (T01 deferred) |
| NF-003 i18n | T04 (G-i18n) |

## Testing Plan

- **Unit:** T02–T04, using real `.properties`, real `UnitTextProvider` and the recording doubles.
- **Local live:** T05.
- **Post-deploy:** T05, by the owner.

## Rollback Plan

Revert the commits. No data to undo.

## Estimate and PR strategy

- ~480 LOC (Java ~170, properties 1, tests ~290, docs ~20), matching the design budget (5 tasks, 7 review rounds).
- No separate PR: the work lands on `email-layout-default` and ships in its PR to `staging` (R1). Reviewers read it
  after the email-layout commits, in task order (T02 → T03 → T04).

## Definition of Done

- [ ] All tasks done with evidence in `execution.md`, including every observed red.
- [ ] G-compile, G-check, G-i18n and G-hygiene green on the final tree; the full `mvn -q -o -pl marlo-web -am test` green.
- [ ] `MicroserviceReportAction` diff shows additions only; the `ReportingSummaryAction` diff shows two fields, one
  method and one guarded line.
- [ ] The post-deploy attachment check is recorded, or tracked as open.
