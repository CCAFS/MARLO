# Logging Standardization — Tasks

**Spec ID:** ENH-LOGGING-STANDARDIZATION-001
**Status:** Draft
**Owner:** IBD Team — Alliance of Bioversity International and CIAT
**Last Updated:** 2026-09-07
**Implements design:** docs/specs/enhancement/logging-standardization/design.md
**Branching:** `logging-standardization`, branched from `staging` (continues the A2-2435 work already merged there).
**Target merge:** staging (then promoted to main per release process).

---

## 1. Execution Context

- **Java:** 17. `marlo-parent/pom.xml` is the verification source for the active level.
- **Run script:** `scripts/run-marlo-java17.sh`.
- **Spring profile:** `dev` for local work; `test` and `pro` exercised for OPS-001 only.
- **Local properties:** `marlo-dev.properties` bootstrapped from `marlo-test.properties`; credential files are
  gitignored and never committed (`CLAUDE.md` rule 12).
- **Verification gates:** `~/.claude/skills/marlo-verify/scripts/clean-compile.sh` and
  `checkstyle.sh --baseline`. `mvn checkstyle:check` **cannot run in this checkout** —
  `maven-checkstyle-plugin` 2.9.1 against `checkstyle` 8.18 throws
  `NoSuchMethodError: Checker.setClassloader`. That is a plugin mismatch, not a code problem, and fixing it is
  a separate ticket. A plain `mvn compile` reports BUILD SUCCESS on code that does not compile, so the clean
  recompile is mandatory rather than advisory.
- **Related Jira:** new Enhancement under epic **A2-2055**, linked to **A2-2435** (logging noise reduction,
  Part 1 merged, Part 2 continued here) and **A2-1470** (Promtail on `marlotest`, background only).

## 2. Pre-flight Checklist

- [ ] `requirements.md` and `design.md` reviewed and moved to Approved.
- [ ] `git pull` on `staging`; confirm `logging-standardization` is not behind it.
- [ ] Baseline captured before any edit, for the deltas the tasks below assert:
      `printStackTrace()` 207, `System.out.print*` 266, `System.err.print*` 4, message-only error logs 135,
      files with an SLF4J logger 315 of 3,453.
- [ ] Baseline Checkstyle counts captured for every file each task touches
      (`checkstyle.sh --baseline`), since the codebase carries pre-existing violations —
      `BaseAction.java` alone is 9 at HEAD, including `FileLength`.
- [ ] A `dev` log file from a full login-and-browse session kept for the before/after volume comparison in T01.

## 3. Task List

### ENH-LOGGING-STANDARDIZATION-001-T01 — Fix the REST logging aspect

- **Depends on:** —
- **Module:** marlo-web
- **Files touched:**
  - `logging/LoggingAspect.java` (modified)
- **Constitutional checks:** English-only comments; 2-space indent, 120-char lines; no new file, so no GPL
  header needed.
- **Tests:**
  - Unit/integration: an exception thrown from a class under `rest.controller.v2.controllist` reaches the
    `@AfterThrowing` advice.
- **Done when:**
  - Pointcut is `within(org.cgiar.ccafs.marlo.rest..*)`, not `rest.*`.
  - The `@Around` advice is removed or gated so it cannot run outside DEBUG.
  - Clean recompile green; no new Checkstyle violations.
- **Verification:** Compare the log volume of an identical `dev` session before and after. The advice now
  applies to 386 classes instead of 0; if the `@Around` survives ungated, the volume explodes and the task has
  failed even though it compiles. Confirm the `@AfterThrowing` line appears for a forced REST exception.

### ENH-LOGGING-STANDARDIZATION-001-T02 — Log every REST error handler with its status

- **Depends on:** —
- **Module:** marlo-web
- **Files touched:**
  - `rest/errors/ExceptionTranslator.java` (modified)
- **Constitutional checks:** `detailed-design.md` §9.3 — REST error handling stays centralized in
  `rest/errors/`; never expose a stack trace to the client; the `ErrorDTO` shape and every `@ResponseStatus`
  are unchanged.
- **Tests:**
  - Integration: each of the 15 handlers produces exactly one log event carrying its status code.
- **Done when:**
  - All 15 `@ExceptionHandler` methods log (1 does today).
  - 5xx logs at ERROR, 4xx at WARN or INFO — a client asking for a missing record is not an ERROR.
  - Every log call passes the exception as the last argument, never `e.getMessage()` concatenated.
  - The stale comment at `:46` claiming the `LoggingAspect` handles these is corrected — after T01 it is true
    for a different reason and must not stay misleading.
- **Verification:** Call a v2 endpoint with a non-existent id; confirm one line with `status_code: 404` where
  today there are none. Confirm the HTTP response body is byte-for-byte identical to before.

### ENH-LOGGING-STANDARDIZATION-001-T03 — Fix the shipped `log.folder` defaults

- **Depends on:** —
- **Module:** marlo-web (resources)
- **Files touched:**
  - `resources/config/marlo-dev.properties:99` (modified)
  - `resources/config/marlo-test.properties:139` (modified)
- **Constitutional checks:** No credential value added; these files stay the bootstrap templates.
- **Tests:** Not applicable — configuration only.
- **Done when:** Neither file names a developer's home directory
  (`/Users/ktanaka/Documents/logs`, `/home/julianrodriguez/logs` today). The default resolves to a path that
  exists in a fresh checkout.
- **Verification:** Fresh clone, `run-marlo-java17.sh`, confirm the log file is created without a manual edit.

### ENH-LOGGING-STANDARDIZATION-001-T04 — `APConfig.getEnvironment()`

- **Depends on:** —
- **Module:** marlo-utils
- **Files touched:**
  - `utils/APConfig.java` (modified)
- **Constitutional checks:** Existing `@Named` / `@Value` bean conventions preserved; no dependency added.
- **Tests:**
  - Unit: `dev|test|pro|fast|api` → `DEV|TEST|PROD`; unknown profile → a safe default, never null.
- **Done when:**
  - `getEnvironment()` returns the environment derived from the Spring active profile
    (`ApplicationContextConfig.java:58-62` defines the profile names).
  - An optional `marlo.environment` override exists for the case where one profile serves two machines.
  - **No new property is required for the default path** (OPS-001) — `marlo-pro.properties` is gitignored and
    absent from this repository, so a mandatory key would leave production emitting nothing.
- **Verification:** Start under `dev` and under `test`; confirm the two values differ with no property set.

### ENH-LOGGING-STANDARDIZATION-001-T05 — `LoggingContextFilter` and its registration

- **Depends on:** T04
- **Module:** marlo-web
- **Files touched:**
  - `web/filter/LoggingContextFilter.java` (**new**)
  - `WebAppInitializer.java` (modified)
- **Constitutional checks:** **GPL header from `AGENTS.md` on the new file.** `OncePerRequestFilter` + `@Named`,
  matching the four existing MARLO filters. No authorization decision (design §8).
- **Tests:**
  - Unit: `MDC.clear()` runs even when the chain throws.
  - Integration: two sequential requests on one worker thread share no context.
- **Done when:**
  - Registered on `/*` immediately after `CORSFilter`, so it is the outermost MARLO filter.
  - Sets `request_id`, `environment`, `tool_name` (when resolvable) and `controller_affected`.
  - `MDC.clear()` is in a `finally` (NF-003). Follows the push/pop shape already used at
    `MARLOCustomPersistFilter:109,164`.
  - A failure inside the filter cannot fail the request (NF-005).
- **Verification:** Log in as user A, log out, log in as user B, and confirm no event of B's requests carries
  A's identity. This does not reproduce single-user; run it explicitly.

### ENH-LOGGING-STANDARDIZATION-001-T06 — `user_id` into the context

- **Depends on:** T05
- **Module:** marlo-web
- **Files touched:**
  - `web/filter/AddUserIdFilter.java` (modified)
- **Constitutional checks:** No additional query (NF-002) — the filter already reads
  `SecurityUtils.getSubject().getPrincipal()` at `:50-53`.
- **Tests:**
  - Integration: authenticated request carries `user_id`; anonymous request carries none.
- **Done when:** `user_id` is present on authenticated requests and **absent**, not stale or zero, on
  anonymous ones.
- **Verification:** Hit a public page and an authenticated page; inspect both.

### ENH-LOGGING-STANDARDIZATION-001-T07 — User name/email and tool enrichment

- **Depends on:** T05, T06
- **Module:** marlo-web
- **Files touched:**
  - `interceptor/RequireUserInterceptor.java` (modified — Struts path)
  - `web/filter/AddSessionToRestRequestFilter.java` (modified — REST path)
- **Constitutional checks:** SEC-001 — `user_name`/`user_email` only on events with `status_code >= 400`.
  NF-002 — no new query: the interceptor already holds the `User` from the session at `:47-76`, the filter
  already resolves it at `:180-183`. The interceptor's authorization behaviour, `AuditLogContext` push and
  `finally` pop are untouched.
- **Tests:**
  - Integration: a successful request carries no `user_name`/`user_email`; an error event carries both.
- **Done when:** Both paths populate the keys; the PII restriction holds; `tool_name` is populated on the REST
  path from `addCrpToSession():94-115`.
- **Verification:** Inspect a successful and a failing request for the same user, on both the `.do` and the
  `/api/*` path.

### ENH-LOGGING-STANDARDIZATION-001-T08 — JSON appender

- **Depends on:** T04, T05, T06, T07
- **Module:** marlo-web, repository root
- **Files touched:**
  - `marlo-parent/pom.xml` (modified — `logstash-logback-encoder` 6.6)
  - `marlo-web/pom.xml` (modified — dependency)
  - `marlo-web/src/main/resources/logback.xml` (modified — `FILE-JSON`)
  - `resources/config/marlo-dev.properties`, `marlo-test.properties` (modified — `log.json`)
- **Constitutional checks:** `CLAUDE.md` rule 11 — no dependency downgraded; 6.6 pinned because 7.x requires
  logback 1.3+/SLF4J 2.x and this checkout is logback 1.2.13 (design ADR-4). NF-001 — the four text appenders
  and their pattern are untouched. NF-004 — field names in one `<providers>` block.
- **Tests:**
  - Integration: every emitted line parses as JSON and carries the fields in design §11.
- **Done when:**
  - `FILE-JSON` writes `${log.folder}/marlo-json-${log.instance}.log`, `TimeBasedRollingPolicy`, 30-day
    history, matching the shape of the existing appenders.
  - Wired into the existing conditional `<root>` block behind `log.json` (OPS-002).
  - `stack_trace` truncation configured (SEC-003).
  - The existing framework logger levels — including the two `net.sf.ehcache.pool.sizeof.*` loggers raised to
    ERROR by A2-2435 Part 1 — apply to the new appender without duplication, since they are logger-scoped.
- **Verification:** `cat marlo-json-dev.log | jq -e .` on a full session — every line must parse. Confirm
  `marlo-dev.log` is unchanged in shape. Confirm `log.json=false` produces no JSON file and leaves the text log
  working.

### ENH-LOGGING-STANDARDIZATION-001-T09 — Cleanup: the save chain

- **Depends on:** T08 (so the effect is visible in the JSON log)
- **Module:** marlo-data
- **Files touched:**
  - `data/dao/mysql/AbstractMarloDAO.java`, `data/dao/mysql/AuditLogMySQLDao.java`,
    `data/HibernateAuditLogListener.java`
  - the five `data/manager/impl/ProjectInnovation*ManagerImpl.java` offenders
- **Constitutional checks:** Layered pattern untouched. Design §6 — **control flow must not change**: correct
  the logging statement in place; where a `catch` logs and returns `null`, leave the `null` unless a caller is
  verified to handle the alternative, and split that into its own task if so. Some files in this tree use CRLF
  endings; a scripted whole-file rewrite flips them to LF and buries the real diff — edit the lines.
- **Tests:**
  - Existing `marlo-data` unit tests unchanged and green.
- **Done when:** No `printStackTrace()` or `System.out`/`System.err` printing remains in these files; every
  `catch` that logs passes the exception as the last argument; `{}` placeholders replace concatenation.
- **Verification:** `git diff` shows no change to any `return`, `throw` or conditional. Force a save failure
  and confirm the stack trace now appears in `stack_trace` instead of on `System.err`.

### ENH-LOGGING-STANDARDIZATION-001-T10 — Cleanup: the authentication path

- **Depends on:** T09
- **Module:** marlo-data
- **Files touched:**
  - `data/dao/mysql/UserMySQLDAO.java`, `data/manager/impl/UserManagerImp.java`
- **Constitutional checks:** SEC-004 — no credential value may be logged. `UserMySQLDAO:171` concatenates the
  email and password into the HQL query; that defect is **out of scope** (recorded in A2-2435) and this task
  must not obscure it — leave it visible and referenced.
- **Tests:**
  - Integration: a failed login produces one event at INFO, not ERROR (a wrong password is not an incident),
    and contains no credential.
- **Done when:** Both files report through SLF4J only; a failed login is not an ERROR.
- **Verification:** Attempt a login with a bad password; inspect the event level and assert no credential
  substring appears in the JSON log.

### ENH-LOGGING-STANDARDIZATION-001-T11 — Cleanup: filters and the REST layer

- **Depends on:** T09
- **Module:** marlo-web
- **Files touched:**
  - `web/filter/*` (3 occurrences)
  - `rest/controller/v2/controllist/**` (≈50 occurrences)
  - `rest/services/deliverables/*` (8 occurrences)
- **Constitutional checks:** As T09. No `ErrorDTO` shape or HTTP status changes.
- **Tests:**
  - Integration: the v2 endpoints behave identically; responses unchanged.
- **Done when:** No `printStackTrace()` or `System.out` remains in these paths.
- **Verification:** Smoke the v2 endpoints that were touched; confirm identical responses and that failures now
  produce a JSON event.

### ENH-LOGGING-STANDARDIZATION-001-T12 — Cleanup: `MicroserviceReportAction`

- **Depends on:** T09
- **Module:** marlo-web
- **Files touched:**
  - `action/report/MicroserviceReportAction.java` (modified — `:307`, `:316-322`, `:385`, `:479`)
- **Constitutional checks:** As T09. The three publisher methods keep their behaviour; only reporting changes.
- **Tests:**
  - Integration: a queue connection failure produces an ERROR event with the stack trace.
- **Done when:** RabbitMQ connection and publish failures reach Logback instead of `System.out`.
- **Verification:** Point `microservice.queueUrl` at an unreachable host and confirm the failure appears in the
  JSON log. Today it appears nowhere — which is also the reason the broker was rejected as a log transport
  (design ADR-1).

### ENH-LOGGING-STANDARDIZATION-001-T13 — Notification: identifiers, deduplication, REST 5xx

- **Depends on:** T02, T08
- **Module:** marlo-web
- **Files touched:**
  - `action/UnhandledExceptionAction.java` (modified)
  - `rest/errors/ExceptionTranslator.java` (modified — route 5xx to the same notifier)
- **Constitutional checks:** No new notifier and no new channel — `SendMailS` is reused. Never expose a stack
  trace to the client (`detailed-design.md` §9.2); the trace goes to the internal mail and the log only.
- **Tests:**
  - Integration: two identical faults produce one mail.
  - Integration: a REST 5xx reaches the notifier; a REST 4xx does not.
- **Done when:**
  - The mail carries `request_id` and `status_code` (FN-007).
  - Deduplication on `tool_name + module_section + status_code` (FN-008).
  - 5xx notifies; 4xx does not (design ADR-3 — 177 of the REST layer's `HttpStatus` usages are `NOT_FOUND`).
  - Existing behaviour preserved: always notify for AICCRA, production-only otherwise.
- **Verification:** Trigger the same exception twice; confirm one mail, and that its `request_id` finds the
  event in the JSON log.

### ENH-LOGGING-STANDARDIZATION-001-T14 — Checkstyle guardrail

- **Depends on:** T09, T10, T11, T12 (so the suppressions file is not stale on arrival)
- **Module:** repository root
- **Files touched:**
  - `configuration/marlo-checkstyle.xml` (modified)
  - `configuration/marlo-checkstyle-suppressions.xml` (**new**)
- **Constitutional checks:** `detailed-design.md` §10.1 names Checkstyle the merge gate; §9.1 already forbids
  `System.out`/`printStackTrace`, so this adds the missing enforcement rather than a new rule. The config
  currently has 11 rules, none about logging, all at `severity=warning`.
- **Tests:**
  - A deliberately added `printStackTrace()` is reported; the pre-existing ones are not.
- **Done when:**
  - Two `RegexpSinglelineJava` rules cover `printStackTrace` and `System\.(out|err)\.print`.
  - A `SuppressionFilter` lists the files that still carry them, regenerated after T09–T12.
  - The gate is actionable on day one rather than firing ~473 times and being ignored.
- **Verification:** `checkstyle.sh` on a scratch file with a `printStackTrace()` — must report it. Then on a
  suppressed file — must not.

### ENH-LOGGING-STANDARDIZATION-001-T15 — ai-context document

- **Depends on:** T08, T13
- **Module:** docs
- **Files touched:**
  - `reports/ai-context/logging-standard.md` (**new**)
- **Constitutional checks:** OPS-005. English only. `detailed-design.md` §9 is **not** edited (design ADR-6).
- **Tests:** Not applicable.
- **Done when:** The document carries the schema table, where each field is populated, the MDC contract, the
  thread-reuse trap, and a "what already exists that this must not duplicate" section naming
  `AuditLogContextProvider`, `UnhandledExceptionAction`/`SendMailS` and `LoggingAspect`.
- **Verification:** A developer unfamiliar with the spec can add a correctly-shaped log statement using only
  this document.

### ENH-LOGGING-STANDARDIZATION-001-T16 — QA pass against the acceptance criteria

- **Depends on:** T01 … T15
- **Module:** —
- **Files touched:** None; `task.md` updated with verification notes.
- **Constitutional checks:** Full checklist from `requirements.md` §7 confirmed in the PR.
- **Tests:** The Testing Plan below, executed end to end.
- **Done when:** Every acceptance criterion in `requirements.md` §6 is demonstrated, including the negative
  ones (no `user_email` on success, no MDC crossover, no notification duplicate).
- **Verification:** Recorded per criterion in this file.

## 4. Dependency Graph

```
T01 (aspect pointcut) ──┐
T02 (REST handlers) ────┼──────────────────────────► T13 (notification)
T03 (log.folder) ───────┤                                  ▲
T04 (getEnvironment) ───┴─► T05 (LoggingContextFilter)      │
                                └─► T06 (user_id)           │
                                      └─► T07 (name/email)  │
                                            └─► T08 (JSON appender) ──┘
                                                  └─► T09 (save chain)
                                                        ├─► T10 (auth)
                                                        ├─► T11 (filters + REST)
                                                        └─► T12 (MicroserviceReportAction)
                                                              └─► T14 (Checkstyle + suppressions)
                                                                    └─► T15 (ai-context)
                                                                          └─► T16 (QA pass)
```

T01, T02, T03 and T04 are independent and can run in parallel. T14 must come after T09–T12 or its suppressions
file is stale the moment it lands. T08 must come after T05–T07, or the JSON ships with empty context fields and
reads as a regression.

## 5. Testing Plan

### Unit

- `APConfig.getEnvironment()`: each profile maps to its environment; unknown profile yields a safe default,
  never null (T04).
- `LoggingContextFilter`: `MDC.clear()` runs even when the downstream chain throws (T05).
- `LoggingAspect`: the `@AfterThrowing` advice fires for a class in a `rest` **subpackage** — the case the old
  pointcut missed (T01).
- Existing `marlo-data` unit tests stay green through T09–T10; the cleanup must not touch behaviour.

### Integration

Run locally with `scripts/run-marlo-java17.sh` under `dev`, `log.json=true`:

1. Log in, open a project section, call an `/api/*` endpoint.
2. `cat marlo-json-dev.log | jq -e .` — every line parses. Assert with `jq` that `request_id`, `level`,
   `environment`, `tool_name` and `user_id` are present on each.
3. All events of one request share one `request_id`; an anonymous request carries **no** `user_id`.
4. A REST call with a non-existent id emits exactly one event with `status_code: 404`. Today it emits none.
5. An unhandled Struts exception emits `status_code: 500` with `user_name`/`user_email` present — and both are
   **absent** on the successful requests from step 1.
6. `marlo-dev.log` is unchanged in shape (NF-001).
7. A code path fixed in T09–T12 puts its trace in `stack_trace` instead of on `System.err`.
8. The same fault twice sends one mail, carrying a `request_id` that locates the event.
9. `log.json=false` writes no JSON file and leaves the text log working.

### Regression (manual, QA team)

- Login, logout and session expiry — T06, T07 and T10 all touch the authentication path.
- One save in each of Project Description, Deliverables and Innovations — T09 touches the save chain and the
  `ProjectInnovation*ManagerImpl` classes.
- The v2 REST endpoints touched by T11: responses must be byte-for-byte unchanged.
- The report queue flow in `MicroserviceReportAction` (T12).
- Confirm the support mailbox still receives exception notifications, and that they are not duplicated.

### Non-functional

- Log volume of an identical `dev` session before and after T01, to prove the aspect fix did not turn into a
  flood (design §12).
- Every event is now written twice, once per format. Compare page render time on a warm Tomcat before and
  after T08; if the second write is material, wrap `FILE-JSON` in an `AsyncAppender` — that does not change
  the schema.
- Two concurrent users driving the app for several minutes, then inspect for MDC crossover. This is the check
  that does not reproduce single-user.

### Accessibility

Not applicable — no user-facing surface. No FTL, macro, JS or i18n change (design §5).

## 6. Operational Steps

### Migration deploy

Not applicable — no Flyway migration and no schema change (design §3). Nothing appears in
`flyway_schema_history`.

### Specificity rollout

Not applicable — the change ships unconditionally for all Global Units. Enablement is per environment through
`log.json`, an operational property, not a `parameters` / `custom_parameters` row (design §9).

### BI / AI coordination

Not applicable — no column, DTO or endpoint changes, so no Bronze refresh and no AI-service contract is
affected.

### Configuration changes

- `log.json` added per environment. Default it to `true` on `dev` and `test`; enable on production after the
  first clean run there.
- `marlo.environment` is **optional** and only needed where one profile serves two machines (T04).
- Production values live in a gitignored `marlo-pro.properties` this repository cannot see. The JSON appender
  writes to the same `log.folder` as the existing text appender, so if one works the other does.
- Disk: a second rolling file with the same 30-day retention. Confirm headroom on the production log volume
  before enabling.

### Backups

Not applicable to the data — nothing is persisted. Normal deploy-time DB backup verification still applies as
part of the release process.

### Notifications

- Slack `#marlo-deploys` on Jenkins success/failure, existing convention.
- Tell MARLO Support before enabling on production that the exception mails now carry a `request_id` and a
  `status_code`, and that repeats are collapsed — otherwise a drop in mail volume reads as a broken alert.
- **No external team is on the critical path.** The Promtail target, Loki retention, dashboards and the
  cross-tool field-name agreement are out of scope (`requirements.md` §4); a handoff note may be sent, and
  nothing here waits on a reply.

## 7. Rollback Plan

### Code

- Revert the merge commit on `staging`; re-deploy the previous artefact via Jenkins.
- Per phase, the revert is independent: T08 alone removes the JSON output and leaves T01–T07 in place, which
  are all fixes to already-broken behaviour and are worth keeping on their own.

### Data

Nothing to roll back — no migration, no column, no row written (design §3, §14).

### Specificity

Not applicable. The equivalent instant off switch is `log.json=false`, which stops the JSON output and leaves
the text log working, so the fallback is today's behaviour rather than no logging.

## 8. Definition of Done

- [ ] Every acceptance criterion in `requirements.md` §6 verified, negative cases included.
- [ ] Every item in the `requirements.md` §7 constitutional checklist confirmed in the PR.
- [ ] Clean recompile green: `clean-compile.sh` reports ~2,403 files in `marlo-data` and ~1,043 in `marlo-web`.
      A materially lower `marlo-web` count means Maven reused stale classes and the run proves nothing.
- [ ] `checkstyle.sh --baseline` shows no new violations on any touched file.
- [ ] The new Checkstyle rules reject a fresh `printStackTrace()` and stay silent on the suppressed backlog.
- [ ] Anti-pattern counts reduced against the T02 baseline in the critical paths, with the remaining
      `action/summaries` and `utils` backlog explicitly listed as deferred.
- [ ] No CSS/JS touched, so no cache-busting bump applies — stated rather than assumed.
- [ ] MDC crossover test run with two users and recorded as passed.
- [ ] Documentation: this `task.md` has verification notes on every task;
      `reports/ai-context/logging-standard.md` published; `detailed-design.md` deliberately unchanged, with the
      reason recorded (design ADR-6).
- [ ] Merged to `staging`.
- [ ] Promoted to `main` via the release pipeline.
- [ ] Production run confirmed: JSON file present, parsing, rotating, and the support mailbox receiving
      deduplicated notifications.
