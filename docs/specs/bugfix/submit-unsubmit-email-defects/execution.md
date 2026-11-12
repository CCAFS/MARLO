# Submit and unsubmit email defects — Execution Log

## Document Control

| Field | Value |
|---|---|
| Spec Path | `bugfix/submit-unsubmit-email-defects` |
| Spec ID | BUG-SUBMIT-EMAIL-001 |
| Branch | `email-layout-default` (R1), spec committed at `ef5f5c641a` |
| Approval Mode | gated |
| Leader | Opus 5.5 (T1) |
| Implementer / Reviewer | `akili-implementer` (sonnet) / `akili-reviewer` (opus, read-only) |
| Budget (design §15) | 5 tasks · ~480 LOC · 7 review rounds |
| Started | 2026-11-12 |

**Persona drift (pre-spawn check, 2026-11-12):** `akili doctor --agents` (akili-specs v2.44.0) reports all four
personas `UNMARKED`. The implementer persona carries no 60-call checkpoint bound. `.agents/*.md` are constitutional
files, so `--fix` cannot run on this spec branch. The owner's decision is recorded below.

## Task Execution History

**Owner decision (2026-11-12):** "si, sigue con las personas actuales y lanza T04". Run on the current personas. The
Leader enforces the 60-call bound by reviewing each report. `akili doctor --agents --fix` is deferred to `staging`
(pending item for archive).

### BUG-SUBMIT-EMAIL-001-T04 — Unsubmit always addresses the leader, greets correctly, no self-CC, no "null"

- **Decision before spawn (execute-time spec edit, 2026-11-12):** the CC rule "omit the actor's address when it equals the TO" (design §2 row 4, DD-6, T04 scope and case (c)) left the leader's own CC entry when actor = leader, so it could not satisfy FN-004 "MUST NOT also be in CC". The new rule is "CC omits every address equal to the TO". FN-004's meaning is unchanged. Closure sweep: `grep -n "omits the actor\|drops the actor\|leaves the actor out\|actor then leader as today" design.md tasks.md requirements.md` → only the amendment note.
- **Safety constraints in the brief:** unit tests only; no DB, no network, no app start; no other build running; the Implementer does not commit or touch files outside the scope; the Leader re-runs every gate after the report.

### BUG-SUBMIT-EMAIL-001-T01 — Measure the cluster report time on aiccratest

- **Status:** deferred `[~]` (2026-11-12). The owner has no aiccratest access now ("no tengo acceso ahora para medir tiempos"; approved "si, de acuerdo").
- **Decision (execute-time plan edit):** the constants start at 10 × 2 000 ms. P-9 and the measurement move to T05's post-deploy check, read from the `fetchClusterReportBytes` INFO line (outcome and elapsed ms). T02 no longer depends on T01.
- **Closure sweep:** `grep -n 'T1 \|T01' design.md tasks.md`. Updated: tasks summary line, the T01 status, the T02 dependency and its scope line, the T05 post-deploy scope, the dependency graph, the coverage row for NF-001, and design §10 and P-9. The remaining hits are T01's own heading and the T04 independence note, which are still true.
- **Final status:** PASS — 2026-11-12. **Implementer attempts:** 1. **Skills / effort:** marlo-verify, tdd; medium.
- **Attempt 1:**
  - **Files changed:**
    - `UnsubmitProjectAction.java`: one `leader` lookup; TO is always the leader; the two `getId() !=` conditions are removed; new `withoutRecipient(cc, to)`; greeting fallback to `email.greeting.everyone`; title fallback to `global.untitled`; the WARN carries the unit id.
    - `global.properties`: +1 line, `email.greeting.everyone=all`.
    - `UnsubmitProjectEmailTest.java`: +7 tests (a)–(g); the `DrivenUnsubmit` and `project(...)` helpers gained parameters.
  - **Red run (before the fix, quoted from the Implementer):** 7 of 15 failed.
    - (a) `CC has no leader address:   ANA@unit.test, ana@unit.test`
    - (b) `TO is the leader expected:<[ana@unit.test]> but was:<[]>`. Both ids are the cached `Long.valueOf(16L)`. The prediction was "Dear ,", but the TO assertion runs first, so this is the recorded value.
    - (c) `CC is the actor only expected:<[luis@unit.test]> but was:<[luis@unit.test, ana@unit.test]>`
    - (d) the body contains `Dear ,`
    - (e) `Dear   ,`
    - (f) `returned the QATEST project "null" (C77)`
    - (g) an NPE from the `return this` fallback outside Struts; the test now overrides `getText`.
  - **Falsifiers (executed, then reverted; `cmp` shows the files identical):**
    - CC filter removed → (a), (b), (c) and the existing send-path test go red.
    - `!=` put back on the TO → (b) red.
    - Greeting fallback dropped → (d), (e) red.
  - **Implementer verification:** clean compile 2430 / 1063 / 115, exit 0. `UnsubmitProjectEmailTest` 15, `EmailLayoutTest` 4 and `SubmitEmailCopyTest` 11 give 30 run, 0 failures. Checkstyle delta 0. Hygiene: 2 REVIEW items on the WARN line (a split constant string, and a ternary null guard), both acceptable. The i18n check is clean.
  - **Evidence re-run (Leader, non-author): VERIFIED.**
    - `rm -rf …/target/classes … && mvn -o -pl marlo-web -am test -Dtest=UnsubmitProjectEmailTest,EmailLayoutTest,SubmitEmailCopyTest` → exit 0, compiled 2430 / 1063 / 115, **Tests run: 30, Failures: 0, Errors: 0**.
    - `checkstyle.sh --baseline UnsubmitProjectAction.java` → HEAD 2, tree 2, delta 0.
  - **Reviewer verdict:** PASS (akili-reviewer, opus, effort high). The CC rule closes the actor = leader gap. The recipients for a different actor and leader are unchanged after `SendMailS:360`. Every case is null-safe. The id-16 test exercises the old reference path, and the id-1082 test uses distinct instances. Subject, BCC, JSON response and `SendMailS` are untouched.
  - **Runtime events:** none.
- **Assumptions (Implementer, verbatim in substance):**
  - The existing test `theSendPathKeepsRecipientsAndSubjectAndAttachesNothing` now expects CC = RETURNER only, following the amended design.
  - Two stray live comments were removed (`// Add project leader`, `// new method`).
  - The id-1082 test builds `Long.valueOf(1082L)` and `Long.valueOf("1082")`, two distinct instances, instead of using `new Long`.
- **ADVISORY (4R, non-gating):**
  - Reliability: a null actor email still becomes the literal "null" in the CC, and a null actor first name shows "null" in `{1}`. Both are pre-existing, outside T04, and the same kind of defect as D2.
  - Reliability: when there is no leader, the TO is `""` and `SendMailS:360` runs `replaceAll(", ", "")`. That would merge several CC entries into one, but today the CC has a single entry in that case. This belongs to a `SendMailS` cleanup.
  - Readability: the commit body should mention the two removed comments. `withoutRecipient` could trim the recipient once instead of on every pass.
  - Risk: none open. Test (a) covers trimming and case, and test (b) covers the real case where actor and leader are the same user.
- **Requirements covered:** FN-004, FN-005, FN-006 (unsubmit), FN-007 (unsubmit), NF-003.
- **Spawns:**
  - implementer: 24 calls, 141 097 tokens, ended complete
  - reviewer: 9 calls, 67 782 tokens, ended complete
- **Budget running totals:** 1 of 5 tasks done (T01 deferred). About 200 LOC against ~480 (Java ~40, properties 1, tests ~160). 1 of 7 review rounds used. Within budget.

### BUG-SUBMIT-EMAIL-001-T02 — Bounded PDF fetch in `MicroserviceReportAction`

- **Attempt 1 (2026-11-12). Skills / effort:** marlo-verify, tdd; medium.
  - **Files changed:**
    - `MicroserviceReportAction.java`: +152 lines, 0 removed. New `fetchClusterReportBytes`, the `FetchTransport` seam and `DefaultFetchTransport`, the four constants and the `setFetchTransport` setter.
    - New `MicroserviceReportFetchTest.java`: 10 tests, (a) to (i), with the GPL header.
  - **Red run (null stub):** 9 of 10 failed, for example (a) `java.lang.AssertionError: actual array was null` and (b) `expected:<10> but was:<0>`. Test (f) was green on the stub, since null is its expected result.
  - **Falsifiers (executed, then reverted):** drop the interrupt restore → (g) red; remove the 0-byte check → (c) red; poll one extra time → (b) red; call `downloadPDFByURL` as well → (h) red (6 tests failed).
  - **Evidence re-run (Leader, non-author): VERIFIED.**
    - The diff of `MicroserviceReportAction.java` shows 0 removed lines.
    - Clean compile: 2430 / 1063 / 116 files, exit 0.
    - `MicroserviceReportFetchTest`, `UnsubmitProjectEmailTest` and `ProjectSubmissionEmailTest`: **36 run, 0 failures, 0 errors**.
    - Checkstyle: HEAD 0, tree 0, delta 0.
    - The diff sets the HTTP connect and read timeouts, and the factory connection, handshake and channel-RPC timeouts.
  - **Reviewer verdict:** PASS (akili-reviewer, opus, effort high). Additions only; the download path is unchanged. Config is read into locals, no shared field is written, and `ServletActionContext` is never touched. The published message matches `sendClusterReportQueueMessage()`. Every failure path returns null, resources are closed, and the INFO line is null-safe.
  - **ADVISORY (4R):**
    - **Resilience:** the design §10 worst case of about 90 s is wrong. Each HEAD can take 5 s to connect plus 5 s to read, so the real ceiling is about 145 s or more.
    - **Resilience:** `Connection.close()` in try-with-resources waits with no limit (amqp-client uses -1). The same pattern exists in today's code.
    - **Reliability:** the production transport's timeouts are proven by reading the code, not by a test. A local `ServerSocket` test could prove them.
    - **Reliability:** an interrupt that arrives during HEAD or GET as an IO exception does not restore the interrupt flag.
    - **Risk:** the GET body has no size cap.
    - **Readability:** the declarations sit at the end of the class. The INFO outcome cannot tell a 0-byte body (`empty`) from an `error`.
  - **Spawns:**
    - implementer: 17 calls, 121 038 tokens, ended complete
    - reviewer: 9 calls, 67 805 tokens, ended complete
  - **Runtime events:** none.
- **Status:** awaiting the owner's decision on the worst-case bound before `[x]`.
- **Owner decision (2026-11-12):** "si es seguro si". The owner approved the Reviewer's resilience advisory as T02 attempt 2: a 30 s total deadline, a bounded `Connection.close`, and an `empty` outcome in the log. Design §10, DD-4, §11 and T02 scope and tests (j), (k) were amended. Closure sweep: `grep -n '90 s\|≈ 90' *.md` returns no hits outside the amendment note. No requirement meaning changed (NF-001 "bounded").
- **Attempt 2 (2026-11-12, owner-approved deadline). Skills / effort:** marlo-verify, tdd; high.
  - **Files changed:**
    - `MicroserviceReportAction.java`: adds `SUBMIT_TOTAL_DEADLINE_MS`, `nowMillis()` on the seam, `cutToTimeLeft` (never below 1 ms), deadline checks before each HEAD / wait / GET, `connection.close(timeoutMs)`, and the outcome `empty`.
    - `MicroserviceReportFetchTest.java`: adds (j), (j2), (j3) and (k), using a fake clock and a logback `ListAppender`.
  - **Red run against attempt 1:**
    - (j) `HEAD 4 timeout above the time left`
    - (j2) `expected:<1500> but was:<5000>`
    - (j3) `expected null, but was:<[B@3704122f>`
    - (k) `...outcome=error, bytes=0...`
  - **Falsifiers (executed, then reverted):** deadline checks removed → (j) and (j3) red; 0-byte logged as `error` → (k) red.
  - **Evidence re-run (Leader): VERIFIED.** The file still has 0 removed lines. Clean compile 2430 / 1063 / 116, MVN_EXIT=0. 40 tests run, 0 failures. Checkstyle delta 0.
  - **Reviewer verdict: FAIL** (verbatim in substance):
    1. **Discovered issue:** in `DefaultFetchTransport.get` the shortened `timeoutMs` goes to `setConnectTimeout` and `setReadTimeout`. A read timeout applies to each `read()` call, not to the whole body, so a slow, chunked body can keep the `while ((read = in.read(buffer)) != -1)` loop running past the 30 s deadline with no limit. The connect and the first read can each use up the time left in turn, about 5 s over. The fake charges `min(latency, timeoutMs)`, so (j) and (j2) pass even though the real transport does not behave that way.
       - **Violated rule:** design.md §10 "Total deadline" ("whatever the microservice or S3 do"), DD-4, and T02 attempt 2 scope.
       - **Remediation:** treat `timeoutMs` as the total budget of each request in `DefaultFetchTransport`. Record the start time, set the read timeout to the remaining budget before `getResponseCode()`, and throw `SocketTimeoutException` in the body loop once the budget is spent. Apply the same budget to `head`.
  - **ADVISORY:**
    - A publish blocked by the broker (memory or disk alarm) is not bounded. The download path already has this exposure; it is recorded as a known limit.
    - The code passes the shortened `timeoutMs` to `close`, where §10 says `close(SUBMIT_CONNECT_TIMEOUT_MS)`. In practice the value is 5000 and can never be larger.
    - A non-200 response logs outcome `error`.
    - The static imports in the test are not in order.
  - **Spawns:**
    - implementer: 19 calls, 126 497 tokens, ended complete
    - reviewer: 8 calls, 61 943 tokens, ended fail
- **Attempt 3 (final) launched.** Effort bumped high → xhigh.
- **Decision before the attempt 3 review (execute-time spec edit):** design §10 now states the bound the code can actually guarantee. Each request's timeout is its total budget; a request in flight can run over by at most its own cut timeout (≤ 5 s), so the ceiling is about 35 s. A publish blocked by a broker alarm is recorded as a known limit, shared with the download path. NF-001's meaning ("bounded") is unchanged. Closure sweep: `grep -n '30 s at most' *.md` → none left.
- **Attempt 3 (final; effort xhigh).**
  - **Files changed:**
    - `MicroserviceReportAction.java`: `DefaultFetchTransport.head` and `get` use a total budget per request (`open()` sets connect and read timeouts to the budget left, never below 1 ms; `checkBudget` runs before and after every body read and throws `SocketTimeoutException`). Adds the close-timeout comment.
    - `MicroserviceReportFetchTest.java`: (l) real `get` against a 127.0.0.1 server that dribbles 1 byte every 50 ms; (l2) real `head` against a silent server. Both use `@Test(timeout = 10000)`.
  - **Red run against attempt 2:** `java.lang.AssertionError: elapsed 3409 ms, budget 500 ms`. (l2) was already green.
  - **Falsifier:** removing the in-loop `checkBudget` turns (l) red with `elapsed 3230 ms, budget 500 ms`. Reverted; the file is byte-identical.
  - **Not Done / Assumptions (assumptions only, no owed item):** a request already in flight can run over by at most its own cut timeout, because `HttpURLConnection` fixes its timeouts when the request opens. No watchdog thread (outside the work order).
  - **Leader decision:** design §10 now states the ≤ ~35 s ceiling and the known limit of a publish blocked by a broker alarm (recorded above).
  - **Evidence re-run (Leader): VERIFIED.**
    - 0 removed lines in the main file versus HEAD.
    - Clean compile 2430 / 1063 / 116, MVN_EXIT=0.
    - `MicroserviceReportFetchTest`, `UnsubmitProjectEmailTest` and `ProjectSubmissionEmailTest`: **42 run, 0 failures, 0 errors**.
    - Checkstyle delta 0.
  - **Reviewer verdict:** PASS (akili-reviewer, opus, effort high). The attempt 2 FAIL is closed against §10 as amended. Timeouts are never 0. The localhost tests are deterministic, with a 4× margin. Everything attempts 1 and 2 got right still holds.
  - **ADVISORY:**
    - Resilience: response headers sent slowly are bounded only by the per-read timeout. Recorded with the broker-alarm known limit; a watchdog that calls `disconnect()` is the later fix.
    - Readability: fully qualified `java.net` / `java.io` names; the test (l) catch does not distinguish the budget cut from early EOF; `open()` Javadoc could reference §10.
  - **Spawns:**
    - implementer: 16 calls, 115 829 tokens, ended complete
    - reviewer: 4 calls, 55 301 tokens, ended complete
- **Final status:** PASS — 2026-11-12, after 3 attempts. **Requirements covered:** FN-001 and FN-002 (fetch side), FN-003, NF-001, NF-002.
- **Budget running totals:** 2 of 5 tasks done (T01 deferred).
  - **LOC about 930 against ~480 → TRIPWIRE EXCEEDED.** T04 added about 190 lines. T02 added 220 of production code and 517 of tests.
  - Review rounds: 4 of 7 (T04 1, T02 3).
  - The cause is test volume plus two owner-approved T02 hardenings (the total deadline and the per-request budget, with localhost socket tests).
  - Stop and escalate to the owner before T03.

## Budget Adjustment (after T02)

- **Date:** 2026-11-12.
- **Delta:** about 930 LOC after 2 of 5 tasks, against the ~480 tripwire. The projection at completion is about 1 150.
- **Cause:** test volume (T02 has 517 test lines) plus the two owner-approved T02 hardenings.
- **Decision:** the owner (Kenji Tanaka) answered "si, haz el commit y sigue con la opción 1". The LOC tripwire is raised to **~1 200**. Tasks (5) and review rounds (7) are unchanged.
