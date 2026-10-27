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
