# Execution Log — ENH-LOGGING-STANDARDIZATION-001

## Document Control

| Field | Value |
|---|---|
| Spec | `docs/specs/enhancement/logging-standardization` |
| Log started | 2026-10-27 |
| Branch | `logging-json-appender`, created from `staging` |
| Approval Mode | `gated` (the spec has no `proposal.md`, so no pre-approval is recorded) |
| Task file | `task.md` (pre-AKILI name for `tasks.md`; read and updated as the task list, see the decision below) |

Tasks T01 to T07 and T09 to T13 were completed before this log existed, outside `/akili-execute`. Their status
lines in `task.md` record the commits and dates; this log does not reconstruct them.

## Task Execution History

### Pre-run decisions (2026-10-27)

- **Task file name.** The spec predates the AKILI file names and carries `task.md`, not `tasks.md`. It holds the same
  content (one block per task with its status), so `/akili-execute` reads and updates it in place. Renaming it is
  left out of this run: it would touch every pointer to the file and is not part of T08.
- **No `Review` field.** No task carries a `Review` field, so every task gets a conformance Reviewer.
- **Leader draft discarded.** Before the user asked for AKILI, the Leader had written a first draft of T08 in the
  working tree (the encoder dependency in both POMs and `logging/MarloMdcJsonProvider.java`). It was deleted before
  the first spawn, so that the Implementer authors the whole diff and the Reviewer audits a single author. What it
  established stays as a fact for the brief: `logstash-logback-encoder` 6.6 resolves online and its tree pulls
  Jackson 2.18.9 from the parent, with no downgrade.

### ENH-LOGGING-STANDARDIZATION-001-T08 — JSON appender

- **Final status:** PASS (attempt 1)
- **Date:** 2026-10-27
- **Implementer attempts:** 1
- **Requirements covered:** FN-001, FN-002, FN-003, NF-001, NF-004, NF-005, NF-006, SEC-001, SEC-002, SEC-003, OPS-002.
- **Skills:** `marlo-verify`. Effort `high` (personal data under SEC-001, and a dependency that can only fail at
  runtime).

#### Attempt 1

- **Files changed:** `marlo-parent/pom.xml`, `marlo-web/pom.xml`, `marlo-web/src/main/resources/logback.xml`,
  `marlo-web/src/main/resources/config/marlo-test.properties`, the local gitignored `marlo-dev.properties`
  (`log.json=true`, not committed), new `logging/MarloMdcJsonProvider.java`, new tests
  `logging/MarloMdcJsonProviderTest.java` (9) and `logging/JsonLogAppenderTest.java` (8).
- **Implementer verification:** clean compile 6 / 2415 / 1063 / 112 files; full suite with `-am` 1122 tests,
  0 failures; Checkstyle delta 0 on the three Java files; hygiene OK. Started application with `log.json=true`
  (mail and CLARISA pointed at 127.0.0.1:9): `jq -e .` exit 0 on every line; `select(.status_code >= 500)` selected
  the 500s; 0 quoted `status_code` / `user_id`; 0 lines with `user_name` / `user_email` below 400 or without a
  status; 0 `?` in `controller_affected`; 0 `service_affected`; `stack_trace` at most 4777 characters; text log
  unchanged. With `log.json=false`: no JSON file, text log still written.
- **Evidence re-run (Leader-inline, non-author):** VERIFIED. Same compile counts and 1122 / 0 in a clean worktree.
  On fresh requests to the running application (home, a 404, a 500, an anonymous 401), 153 new lines: all parse,
  152 carry `request_id` and `controller_affected`, 16 `user_id` and `tool_name`, 4 select as `status_code >= 500`,
  0 quoted numbers, 4 lines with personal data (all at 404), 0 below 400, 0 query strings, 0 `service_affected`.
  The `log.json=false` and undefined cases are covered by two tests of `JsonLogAppenderTest` that list the whole
  temporary folder.
- **Reviewer verdicts** (parallel lens reviewers, `opus`; Implementer `sonnet`):
  - Conformance + risk/security: **PASS**. SEC-001 has no second path: the stock `<mdc>` provider includes only
    `request_id`, `tool_name` and `controller_affected`, and there is no arguments, markers or context provider.
  - Conformance + reliability/resilience: **PASS**. The provider cannot throw on bad MDC input, the gate fails closed
    on an unparseable status, the text appenders are byte-for-byte untouched, and the tests falsify what they claim.
  - Both judged the Implementer's five calls conformant: `FILE-JSON` declared inside `<if log.json>` (needed so that
    `log.json=false` writes no file), the production-only INFO threshold (same shape as `FILE-PRODUCTION`), no
    `<pattern>` element, no `payload` provider, and `log.json=true` in the tracked `marlo-test.properties`.
- **runtime events:** none.
- **spawns:** implementer 48 calls, 172710 tokens, ended complete; reviewer (security) 15 calls, 79976 tokens, ended
  complete; reviewer (reliability) 20 calls, 85185 tokens, ended complete.

#### Not Done / Assumptions (Implementer report, verbatim)

- **Upstream gap, not from this task:** the 500 on `centerSummaries/centerOutcomes.do` carries no `user_id`,
  `user_name` or `tool_name`, in the text log as well as the JSON. The user context is not populated on that route,
  so the provider had nothing to publish. The 404 on a normal project route carried them all. This probably deserves
  its own look under T04 to T07.
- **Declared only when `log.json` is true.** I wrapped the whole `FILE-JSON` appender in `<if log.json>` instead of
  only the root reference. Logback creates the file of every appender it declares, so the first attempt left an empty
  `marlo-json-dev.log` with the switch off. A unit test caught it. A missing `log.json` therefore means off.
- **INFO threshold on production only.** I added a nested `<if log.production>` that applies a `ThresholdFilter` at
  INFO, as `FILE-PRODUCTION` does. The task did not ask for it. It is covered by
  `aProductionServerDropsEventsBelowInfo` but was not exercised in a started production-profile app.
- **No `<pattern>` element in the JSON appender.** The existing `LogEnvironmentDefinerTest` asserts every `<pattern>`
  in `logback.xml` carries `environment=`. The timestamp therefore uses the encoder's default ISO format with
  `<timeZone>UTC</timeZone>`, which printed `2026-10-27T20:57:34.195Z`.
- **`payload` provider not added.** It was not in Done-when and nothing emits one yet.
- **Not run:** the AC's two-user MDC crossover check.

Leader adjudication: no item names a blocker or work owed by T08. The two-user crossover check belongs to NF-003 and
the request-context tasks (T05), not to the T08 verification line.

#### Decisions made

- **Execute-time spec edit:** `design.md` §11 "Appender" named `LogstashEncoder`; it now names
  `LoggingEventCompositeJsonEncoder` and says why `LogstashEncoder` must not replace it (it writes every MDC entry, so
  `user_name` and `user_email` would reach every line and break SEC-001). Requested by both reviewers; the meaning of
  no requirement changes. The edit is carried to the next task's Reviewer brief.
- The Leader's pre-run draft was discarded (see *Pre-run decisions*).

#### ADVISORY (recorded, not acted on)

- No `logback-test.xml` exists under `marlo-web/src/test/resources`, so the test suite loads the production
  `logback.xml` and, with a local `marlo-dev.properties`, writes fixture events into the developer's real text log,
  and now also into the JSON log when `log.json=true`. Pre-existing for the text log; observed during the re-run.
- The timestamp format relies on the encoder default; an explicit `yyyy-MM-dd'T'HH:mm:ss.SSS'Z'` pattern would pin it
  against a future encoder upgrade.
- `{"environment":"${environment:-UNKNOWN}"}` is built by text substitution: an operator override of
  `marlo.environment` containing `"` or `\` would make the custom field invalid and `environment` would disappear.
- SEC-003 bounds `stack_trace` only; `message` has no length bound.
- When `payload` is added, a plain `<arguments>` provider would bypass the SEC-002 allow-list.
- The encoder adds `java-uuid-generator` 4.0.1 to the WAR, a jar ADR-4 does not mention.
- With `log.production=true` and `log.test=true` together, `FILE-TEST` keeps DEBUG while `FILE-JSON` drops it.
- Upstream: routes such as `centerSummaries/centerOutcomes.do` publish no user context, so their 5xx lines cannot
  name the affected user.

#### Running totals

The design carries no budget, so the tripwire does not apply. T08: 1 task, about 94 changed lines in existing files
plus 535 in three new Java files, 2 Reviewer verdicts in one round.

- **Final verification:** PASS — compile, full suite, Checkstyle delta 0, and a parsed JSON file from a started
  application.

### ENH-LOGGING-STANDARDIZATION-001-T15 — ai-context document

- **Final status:** PASS (attempt 2)
- **Date:** 2026-10-27
- **Implementer attempts:** 2
- **Requirements covered:** OPS-005.
- **Skills:** `cognitive-doc-design`. Effort `medium`, raised to `high` for the rework.

#### Attempt 1

- **Files changed:** `reports/ai-context/logging-standard.md` (new).
- **Implementer verification:** every MARLO class, method, `LogContext` key constant, logback element and property
  the document names exists in the working tree; `LoggingAspect.java` and `LoggingContextFilter.java` are absent, as
  the document states.
- **Evidence re-run (Leader-inline):** VERIFIED. The only names without a class file are log levels, JDK or library
  classes, and the two classes the document itself declares absent.
- **Reviewer verdict:** **FAIL**. One issue: the Statement Rules row on null-safe log arguments was marked
  `UNVERIFIED` and credited to a private memory note, although the repository states the rule in
  `.claude/skills/marlo-verify/SKILL.md` Gate `LOG-NULL`, checked by
  `.claude/skills/marlo-verify/scripts/java-hygiene-check.sh`. Violated: T15 Verification ("using only this
  document") and OPS-005. Everything else was confirmed accurate against the code and complete on the Done-when list.

#### Attempt 2

- **Files changed:** the same file; only that row.
- **Implementer verification:** `grep -n "memory note\|UNVERIFIED"` on the document returns nothing (exit 1); both
  cited paths exist.
- **Evidence re-run (Leader-inline):** VERIFIED (same grep exit 1, both paths listed, only that file untracked).
- **Reviewer verdict:** **PASS**. The row matches the skill line by line, including the receivers the checker skips
  (`e`, `ex`, `*Exception`, static utilities); the rest of the document, the Done-when list and the T08 encoder check
  still hold.
- **runtime events:** none.
- **spawns:** implementer 16 calls, 126396 tokens, ended complete; reviewer 11 calls, 60027 tokens, ended fail;
  implementer 4 calls, 65330 tokens, ended complete; reviewer 4 calls, 41476 tokens, ended complete.

#### Decisions made

- **Spec correction approved by the user (2026-10-27), shipped with T15.** The Implementer and the attempt-1 Reviewer
  found the spec stale against the code: `design.md` §1 diagram, §2, §8, §11 schema table, §12, ADR-5 and §16, and
  `agent-context.md` §2 and §3, still described the `LoggingContextFilter` T05 replaced on 2026-10-06 and credited
  `user_id` to `AddUserIdFilter`. They now describe what exists: the `LogContext` `@WebListener` clears the context
  when a request starts and ends and sets `request_id` and `controller_affected`; `RequireUserInterceptor` (Struts)
  and `AddSessionToRestRequestFilter` (REST) set `user_id`, `user_name`, `user_email` and `tool_name`;
  `ErrorStatusLogInterceptor`, `UnhandledExceptionAction` and `ExceptionTranslator` set `status_code`.
  `requirements.md` §7 (license-header row) and two `task.md` references (the dependency graph label and the testing
  line for T05) were aligned. No requirement changes meaning. Closure sweep: forward, every remaining
  `LoggingContextFilter` / `AddUserIdFilter` mention in the spec and in `logging-standard.md` either explains the
  replacement or describes `AddUserIdFilter` as it is; backward, `logging-standard.md` already follows the code.
- The T05 and T06 task blocks keep their original file lists as history; their status lines already record the
  shape that shipped.

#### ADVISORY (recorded, not acted on)

- `ExceptionTranslator:86` logs the REST 401 of `AuthorizationException` at ERROR, while the shared handler logs
  401 at INFO; the document states INFO. Code inconsistency outside T15.
- The `service_affected` row of the document could cite ADR-9.

- **Final verification:** PASS — the document is accurate against the code and self-contained.

### ENH-LOGGING-STANDARDIZATION-001-T09 — Cleanup: the save chain

- **Final status:** PASS (attempt 1 of this run; the task was partly done on 2026-10-26, outside `/akili-execute`)
- **Date:** 2026-10-27
- **Implementer attempts:** 1
- **Requirements covered:** FN-006, FN-009 (save chain).
- **Skills:** `marlo-verify`. Effort `low`.

#### Attempt 1

- **Leader scan before the spawn:** all 35 files of the task's list (`AbstractMarloDAO`, `AuditLogMySQLDao`,
  `HibernateAuditLogListener` and the 32 `ProjectInnovation*ManagerImpl`): 0 prints, 0 concatenated log messages,
  and two `catch` blocks that logged without the exception, both in `ProjectInnovationPartnershipManagerImpl`.
- **Files changed:** `marlo-data/.../manager/impl/ProjectInnovationPartnershipManagerImpl.java` (two log lines, LF
  file); new test `marlo-web/.../logging/SaveChainFailureLoggingTest.java`.
  - `getUserList`: `logger.error("unable to get partners")` → `"Unable to get the partners of project {} and
    institution {}", projectID, institutionId, e`.
  - `saveProjectInnovationPartnership`: `logger.error(e.getMessage())` → `"Unable to find the partnerships of the
    innovation being saved", e` (no id: the getter chain is what may have thrown).
- **Implementer verification:** compile 6 / 2415 / 1063 / 113; full suite with `-am` 1123 tests, 0 failures;
  Checkstyle delta 0 (3 → 3); hygiene no findings. Save-failure evidence: a JUnit test builds a context from the
  shipped `logback.xml` with `log.json=true` in a temporary folder, makes `getUserList` fail through a `Proxy` stub of
  `ProjectPartnerManager`, and asserts an ERROR line with the ids, a `stack_trace` carrying the exception, an empty
  `System.err`, and the unchanged empty-list result. The running server was not used: it predates the change, and a
  real forced save failure would risk the local database.
- **Evidence re-run (Leader-inline):** VERIFIED. The 35-file scan reports 0 offenders; the diff is the two lines only;
  same compile counts and 1123 / 0 in a clean worktree. Falsifier run by the Leader: with the old `getUserList` line
  restored, the new test fails ("the failure must be in the JSON log"), so it detects the defect.
- **Reviewer verdict:** **PASS**. Control flow unchanged (the empty list and the `null` are kept); both statements pass
  the exception last, use placeholders and log no request value. The JSON-appender test satisfies the task's
  save-failure check together with T08's started-app evidence; the second line needs no test of its own.
- **runtime events:** none.
- **spawns:** implementer 11 calls, 80836 tokens, ended complete; reviewer 8 calls, 44796 tokens, ended complete.

#### Decisions made

- The task's status line said the `ProjectInnovation*ManagerImpl` files "had no offender left"; this run found the two
  above. The status line is corrected in `task.md`.
- "Force a save failure" is met by the JUnit test plus T08's runtime evidence that stack traces reach `stack_trace`,
  instead of a forced failure on the local database.

#### ADVISORY (recorded, not acted on)

- `SaveChainFailureLoggingTest`: `context.stop()` runs only when the `try` succeeds; moving it into `finally` would
  release the file handle on a failed assertion. `java.io.File` is written in full instead of imported, and a
  missing `logback.xml` would surface as a bare NPE rather than an assertion message.
- `getUserList` is a lookup inside a save-chain manager rather than a save; the integration item for "a code path
  fixed in T09–T12" covers it.

- **Final verification:** PASS.

### ENH-LOGGING-STANDARDIZATION-001-T09 — Cleanup: the save chain (reopened)

- **Final status:** PASS (attempt 2 overall)
- **Date:** 2026-10-27
- **Why reopened:** the T14 rework widened the console rule to `System\.(out|err)\b` and found a `System.out` /
  `.println` statement split over two lines in `ProjectInnovationContributingOrganizationManagerImpl`, a T09 file.
  The T09 closing scan had used `System\.(out|err)\.print` on single lines and missed it, so T09's Done-when did not
  hold. The task was reopened rather than suppressing the hit in T14.

#### Attempt 2

- **Files changed:** `marlo-data/.../manager/impl/ProjectInnovationContributingOrganizationManagerImpl.java`: the
  four-line loop that only printed each id before the deletion loop is removed (a per-iteration development trace,
  removed rather than migrated per `.claude/skills/marlo-verify/SKILL.md`). The deletion loop and the next-phase
  recursion are unchanged; LF endings kept.
- **Implementer verification:** 35-file scan with multi-line patterns `System\.(out|err)\b` and
  `printStackTrace\(\s*(\)|System\b)` outside comments: 0 and 0; Checkstyle delta 0 (2 → 2); `LoggingSystemOut` on
  the file with its suppression entry removed: 0; hygiene ok. Its full-suite run was inconclusive: a `cargo:run`
  server was running in the same checkout.
- **Evidence re-run (Leader-inline):** VERIFIED in a clean worktree with the diff applied: compile 6 / 2415 / 1063 /
  113, 1123 tests, 0 failures.
- **Reviewer verdict:** **PASS**. The removed loop had no side effect; removal follows the project's convention for
  development traces; the Reviewer's own scan (`System\s*\.\s*(out|err)\b|printStackTrace\s*\(`, comments included)
  found 0 matches in the 35 files.
- **runtime events:** none (the inconclusive suite run was environment contention, recovered by the Leader's
  worktree re-run).
- **spawns:** implementer 11 calls, 83444 tokens, ended complete; reviewer 6 calls, 36292 tokens, ended complete.

#### Decisions made

- Closing scans now use multi-line patterns; a single-line regex is not evidence that a file holds no print.

### ENH-LOGGING-STANDARDIZATION-001-T14 — Checkstyle guardrail

- **Final status:** PASS (attempt 2)
- **Date:** 2026-10-27
- **Implementer attempts:** 2, plus one continuation (the suppressions regenerated after the reopened T09)
- **Requirements covered:** OPS-004, FN-009.
- **Skills:** `marlo-verify`. Effort `medium`, raised to `high` for the rework.

#### Attempt 1

- **Files changed:** `configuration/marlo-checkstyle.xml`, new `configuration/marlo-checkstyle-suppressions.xml`.
- **Implementer verification:** rules `LoggingPrintStackTrace` (`\.printStackTrace\(\s*\)`) and `LoggingSystemOut`
  (`System\.(out|err)\.print`), both `ignoreComments=true`; `SuppressionFilter` relative to the repository root,
  `optional=true`; 6 hits in 3 marlo-utils files suppressed and `src/test` excluded entirely; red probe 3 hits, green 0.
- **Evidence re-run (Leader-inline):** VERIFIED (own probe 3 hits; `ExternalPostUtils` 0; 3484 main files 0).
- **Reviewer verdict:** **FAIL**. (1) `printStackTrace(System.err)`, `printStackTrace(System.out)` and
  `System.err.write/append/format` passed both rules (OPS-004, FN-009, TRD §9.1). (2) The blanket `src/test`
  exclusion broke "existing occurrences MUST be suppressed explicitly" and the OPS-004 acceptance criterion. Leader
  adjudication: remediation (a), explicit suppression of the test files with occurrences; option (b), narrowing
  OPS-004 to production code, would change the spec and was not taken.

#### Attempt 2

- **Files changed:** the same two files. `LoggingPrintStackTrace` = `\.printStackTrace\(\s*(\)|System\b)`;
  `LoggingSystemOut` = `System\.(out|err)\b`. The wider rule surfaced a split `System.out` in a T09 file; T09 was
  reopened and fixed (entry above), and a continuation regenerated the suppressions from the result.
- **Suppressions (explicit, by file and rule id):** marlo-utils `ExternalPostUtils` (2 + 2), `AESConvert` (1),
  `SendMail` (1); marlo-web tests `ClientIpResolverTest` (3, `System.out`) and `SaveChainFailureLoggingTest` (1, the
  `System.err` capture it asserts on). 11 hits in 5 files without suppressions, 0 with them.
- **Implementer verification:** red probe reports `printStackTrace()`, `printStackTrace(System.err)` and
  `(System.out)`, `System.out.println`, `System.err.write`, `System.out.format`; comments and
  `printStackTrace(new PrintWriter(w))` are not reported; existing rules unchanged (delta 0).
- **Evidence re-run (Leader-inline):** VERIFIED. Own probe: every console form reported, including a `System.out` /
  `.println` split over two lines; the comment and the writer form not reported. All 3597 Java files under
  `src/main` and `src/test` of the three modules: 0 `Logging*` hits with the suppressions.
- **Reviewer verdict:** **PASS**. Both attempt-1 issues resolved; the Reviewer's own search found the same 11 hits
  in the same 5 files, each suppressed by name and id; the `SaveChainFailureLoggingTest` entry is a legitimate use.
- **runtime events:** none.
- **spawns:** implementer 22 calls, 92515 tokens, ended complete; reviewer 11 calls, 49540 tokens, ended fail;
  implementer 10 calls, 85013 tokens, ended complete; implementer (continuation) 8 calls, 72410 tokens, ended
  complete; reviewer 4 calls, 40370 tokens, ended complete.
- **continuations:** 1 (suppressions regenerated after the T09 fix).

#### Decisions made

- `reports/ai-context/logging-standard.md` (T15's deliverable) said the console rule was not yet enforced because T14
  was open. With T14 closed that was false; the Leader updated its two statements: the rules now apply to every file,
  run through `checkstyle.sh` from the repository root, and a cleaned file leaves the suppressions list.

#### ADVISORY (recorded, not acted on)

- The suppressions path is relative to the working directory and `optional=true`: a repaired
  `maven-checkstyle-plugin` would skip it silently and report the 11 known hits; switch to `${config_loc}` then.
- `checkstyle.sh` does not change to the repository root, so running it from a subdirectory drops the suppressions on
  both sides of the delta; it always exits 0, so the gate holds only if the delta is read.
- `System\.(out|err)\b` also matches inside string literals; acceptable for a guardrail.
- The whole-file suppressions of the three marlo-utils files would hide a new print added to them; `ExternalPostUtils`
  (POST bodies on stdout) is the first to clean.
- The logging block in `marlo-checkstyle.xml` is indented one tab deeper than its neighbours.

- **Final verification:** PASS.

### ENH-LOGGING-STANDARDIZATION-001-T16 — QA pass against the acceptance criteria

- **Final status:** open — blocked on user decisions (not closable: Done-when not met)
- **Date:** 2026-10-27
- **Worker:** `akili-tester` (`opus`, effort high), 53 calls, 176586 tokens, ended complete.
- **Environment:** local app from HEAD `0a17b19671` under `dev`, `log.json=true`, mail and CLARISA pointed at a
  dead address during startup; unit suite in a throwaway worktree (1123 tests, 0 failures); only one local account.

#### Result (31 rows: 12 acceptance-criteria rows and every Testing Plan item)

- **Demonstrated (20, after the Leader re-run below):** every JSON line parses (283/283 and 4641/4641);
  `status_code` and `user_id` are JSON numbers; no `user_name`/`user_email` on a successful request (0 lines);
  no context crossover in 480 interleaved logged-in and anonymous requests over all 10 Tomcat threads; the same 500
  twice gives one mail hand-off and one "already reported" suppression; `controller_affected` carries no query
  string; `stack_trace` is bounded; `log.json=false` writes no JSON file and keeps the text log; the text log keeps
  its format; `LoggingAspect` is gone; the Checkstyle guardrail reports a new print; `environment` follows the
  profile (unit test).
- **Leader re-run (non-author):** a logged-in Struts 500 on `summaries/AICCRA/POWBSummaryPLT2019.do` carries
  `status_code` 500, `user_id`, `tool_name`, `user_name` and `user_email` (7 lines), with 0 personal-data lines
  below 400. SEC-001's positive case and Integration 5 are therefore demonstrated on a normal route; the Tester's
  failing case used `centerSummaries/centerOutcomes.do`, a legacy route whose stack has no `RequireUserInterceptor`
  and so never publishes the user.
- **Not demonstrated (product findings):**
  1. AC FN-001/FN-003 and Integration 2: "each line MUST carry non-empty `tool_name`" — 244 of 283 lines have none:
     the lines a request writes before `RequireUserInterceptor` runs, `login.do`, anonymous requests and the legacy
     `centerSummaries` routes. The design says `tool_name` is populated "once the global unit is known", so the AC
     and the design disagree (spec tension).
  2. AC FN-006/FN-009, third clause ("no `catch` … MUST log an error and return `null`"):
     `rest/controller/v2/controllist/ProjectPage.findProjectById` throws `NotFoundException` inside its own `try`,
     catches it, logs ERROR without a status and returns `null`, so the client gets an empty 200 instead of a 404.
     The `return null` predates this spec; T11 (`5568058c22`) changed only the log line.
  3. Legacy `centerSummaries` routes publish no user context (no `user_id`, `tool_name`, `user_name`,
     `user_email`), so their errors cannot name the affected user.
- **Not demonstrable locally (8):** REST runtime checks FN-004, FN-005, Integration 1 and 4 (a session is redirected
  away from `/api/*` and there is no local REST credential); OPS-001 under the `test` and `pro` profiles with a
  started app (unit test only); the QA team's manual regression; both before/after performance comparisons.
- **Also found (code reading):** `ExceptionTranslator` logs the REST 401 of `AuthorizationException` at ERROR while
  every other 401 is INFO; an anonymous `/api/*` call is refused by Shiro with no log event.

#### Escalation

T16 stays open. The user decides: (1) amend the FN-003 acceptance criterion to the design's "once the global unit is
known", or populate `tool_name` earlier in code; (2) whether `ProjectPage` becomes its own task (it changes a REST
response from an empty 200 to a 404); (3) whether the legacy `centerSummaries` gap is accepted as a known limit; (4)
who runs the checks that are not demonstrable locally (REST credential, `test`/`pro` startup, QA regression,
performance).
