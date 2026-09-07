# Logging Standardization — Requirements

**Spec ID:** ENH-LOGGING-STANDARDIZATION-001
**Status:** Draft
**Owner:** IBD Team — Alliance of Bioversity International and CIAT
**Reviewers:** PMU lead, QA lead, Tech lead
**Last Updated:** 2026-09-07
**Related PRD sections:** docs/prd.md §8 (assumptions, dependencies, constraints), §9 (open questions)
**Related System Design sections:** Not applicable — no user-facing surface changes.
**Related Detailed Design sections:** docs/detailed-design/detailed-design.md §9 (error handling & observability), §9.1 (logging), §9.3 (REST error handling), §9.4 (observability), §10.1 (static analysis)
**Companion ai-context docs:** reports/ai-context/logging-standard.md (new, delivered by this spec), reports/ai-context/deployment-checklist.md

---

## 1. Overview

The CGIAR DevOps group agreed on 29/08/2025 that every tool in the portfolio — STAR, PRMS, MARLO/AICCRA,
PDFs MS — emits its logs in one common JSON schema so that errors can be aggregated centrally and notified
consistently. MARLO cannot currently participate: it emits unstructured text, and five of the eleven agreed
fields never reach the log at all.

This spec covers **only the part MARLO can build, compile and verify inside this repository**. It makes MARLO
emit a structured, correlatable log and closes the places where errors leave no record. It does not build, and
does not depend on, the queue, the pre-processor, the log collector, the central dashboards or the cross-tool
field-name agreement. Those are recorded in §4.

It continues the work started in A2-2435 (logging noise reduction, Part 1 merged) rather than replacing it.

## 2. Problem Statement

MARLO's log cannot answer the questions it is consulted for. Measured on this checkout
(`marlo-web` + `marlo-data`, 3,453 `.java` files):

| Finding | Volume |
|---|---|
| Files with an SLF4J logger | 315 (~9%). `manager/impl` 16/457, `dao/mysql` 8/456, `rest` 39/386 |
| `printStackTrace()` / `System.out.print*` | 207 / 266 occurrences — bypass Logback, never reach the log file |
| `LOG.error("…" + e.getMessage())` | 135 — reach the file with the stack trace discarded, so the origin is unknown |
| `LOG.error("…", e)` — the correct form | 123 |
| MDC usages, and `%X{}` in any appender pattern | 0 and 0 — no per-request context exists |
| `ExceptionTranslator` `@ExceptionHandler` methods vs log calls | 15 vs 1 |
| Logger variable names / declaration variants | `LOG` 253, `logger` 59, `log` 5, across 8 declaration forms |

Three consequences follow directly, and each is a distinct operational failure:

1. **REST errors leave no trace.** `rest/errors/ExceptionTranslator.java` converts an exception into an
   `ErrorDTO` with its `HttpStatus` and returns it to the client. Fourteen of its fifteen handlers log nothing,
   so a 500 returned to an integration partner is invisible on the server. This is precisely the
   "errors with `status_code >= 400` are not always reported" problem the DevOps agreement names, and in MARLO
   it is literal.
2. **The one centralized logging component advises nothing, and is the wrong place to fix finding 1.**
   `logging/LoggingAspect.java` declares `@Pointcut("within(org.cgiar.ccafs.marlo.rest.*)")`. No `.java` file
   lives directly in that package — all 386 are in subpackages — and `within(pkg.*)` does not match
   subpackages. The aspect has therefore never run since it was added in January 2018, and
   `ExceptionTranslator.java:47-48` documents an assumption ("just for exceptions that don't get processed by the
   LoggingAspect") that has never held. Findings 1 and 2 each cite the other as the reason it does not log.
   Repairing the pointcut would not help: the aspect observes the exception and never the response, so it
   cannot know the HTTP status FN-004 requires, while `ExceptionTranslator:138` handles `Exception` and no
   REST exception escapes the one place that does know it. Fixing the pointcut would therefore log every REST
   error twice, and mark every legitimate 404 as an ERROR. FN-005 removes the aspect instead.
3. **No log line can be attributed to a request or a user.** With no MDC, two concurrent users produce
   interleaved lines that cannot be separated, and an error line cannot be tied to whoever hit it.

`docs/detailed-design/detailed-design.md` §9.1 already prescribes the intended standard — SLF4J + Logback, a
per-class logger, the level semantics, and "No `System.out` / `printStackTrace` in production code". Nothing
enforces it: `configuration/marlo-checkstyle.xml` has eleven rules, none about logging, all at
`severity=warning`. This spec implements §9.1 and adds the missing enforcement.

## 3. In-Scope Requirements

### Functional

- ENH-LOGGING-STANDARDIZATION-001-FN-001 — The application MUST emit every log event as a single-line JSON
  object, in addition to the existing human-readable text log.
- ENH-LOGGING-STANDARDIZATION-001-FN-002 — Every JSON event MUST carry a `request_id` that identifies the HTTP
  request that produced it, so that all events of one request can be selected together.
- ENH-LOGGING-STANDARDIZATION-001-FN-003 — Every JSON event MUST carry `level`, `timestamp`, `environment`,
  `tool_name`, `module_section`, `controller_affected` and `message`.
- ENH-LOGGING-STANDARDIZATION-001-FN-004 — Every error response produced by the REST layer MUST produce
  exactly one log event carrying the HTTP status code returned to the client (see §2, finding 1).
- ENH-LOGGING-STANDARDIZATION-001-FN-005 — The REST layer MUST NOT retain a logging component that advises
  nothing. `LoggingAspect` MUST be removed and REST error logging consolidated in `ExceptionTranslator`, the
  only place where the HTTP status FN-004 requires is known (see §2, finding 2).
- ENH-LOGGING-STANDARDIZATION-001-FN-006 — Every log event that reports a caught exception MUST carry the
  exception's stack trace, not only its message.
- ENH-LOGGING-STANDARDIZATION-001-FN-007 — An error notification sent to the support team MUST identify the
  `request_id` and `status_code` of the event that caused it, so a mail and a log line can be correlated.
- ENH-LOGGING-STANDARDIZATION-001-FN-008 — Repeated occurrences of the same error MUST NOT each produce a
  notification.
- ENH-LOGGING-STANDARDIZATION-001-FN-009 — No code path on the save chain, the authentication path, the
  servlet filters or the REST layer may report a failure through `System.out`, `System.err` or
  `printStackTrace()`.

### Non-Functional

- ENH-LOGGING-STANDARDIZATION-001-NF-001 — The existing text log MUST keep its current format and content.
  The JSON output is additive; the text log remains what the team reads over SSH
  (see reports/ai-context/deployment-checklist.md:80,83, which instruct reading it).
- ENH-LOGGING-STANDARDIZATION-001-NF-002 — Populating the per-request context MUST NOT issue any additional
  database query. Every value MUST come from data the request already holds.
- ENH-LOGGING-STANDARDIZATION-001-NF-003 — The per-request context MUST NOT leak between requests served by
  the same pooled Tomcat thread.
- ENH-LOGGING-STANDARDIZATION-001-NF-004 — The JSON field names MUST be declared in a single location, so
  adopting the names the DevOps group finally agrees is one edit and not a search across the codebase.
- ENH-LOGGING-STANDARDIZATION-001-NF-005 — A failure inside the logging path MUST NOT fail the request that
  produced it.
- ENH-LOGGING-STANDARDIZATION-001-NF-006 — Fields whose values are numeric — `status_code` and `user_id` —
  MUST be emitted as JSON numbers, not as quoted strings, so a consumer can filter on a range without a cast.
  This does not come for free: SLF4J's MDC is a `Map<String,String>` and the encoder pinned by ADR-4 has no
  typed-MDC writer, so it requires the provider named in design §11.

### Data

- Not applicable. This spec adds no table, column, index, enum or Flyway migration. Logging state is
  per-request and in-memory; nothing is persisted to the database.

### UI

- Not applicable. No FTL view, macro, JS module or i18n key changes. The spec has no user-facing surface;
  the only human-visible artefact is the support notification email, whose template gains two identifiers
  and is not user-facing content.

### Security

- ENH-LOGGING-STANDARDIZATION-001-SEC-001 — `user_id` MAY be emitted on every event. `user_name` and
  `user_email` MUST be emitted only on events whose `status_code` is 400 or above — the only case where
  someone needs to contact the affected user. The restriction MUST be enforced **at emission, not at
  population**: both values enter the request context at the start of the request, while the status code is
  known only at the end, so no context-based rule can express it. Design §11 names the single component that
  enforces it.
- ENH-LOGGING-STANDARDIZATION-001-SEC-002 — `payload` MUST be an allow-list of non-identifying fields
  (entity ids, section, phase). It MUST NOT contain the raw request or response body, and MUST NOT contain a
  password, token, authorization header, cookie or session identifier.
- ENH-LOGGING-STANDARDIZATION-001-SEC-003 — `stack_trace` MUST be truncated to a bounded length, so that an
  oversized event cannot be silently rejected by a consumer.
- ENH-LOGGING-STANDARDIZATION-001-SEC-004 — No log event may contain a credential. Cleaning up the
  authentication path (§3 Operations) MUST NOT introduce logging of the values it handles.

### Operations / Observability

- ENH-LOGGING-STANDARDIZATION-001-OPS-001 — `environment` MUST be derived from the Spring active profile,
  which is already set per environment. Emitting it MUST NOT require a new server-side property, because
  `marlo-pro.properties` is gitignored and absent from the repository.
- ENH-LOGGING-STANDARDIZATION-001-OPS-002 — JSON output MUST be switchable per environment through a
  configuration property, without a code change or a redeploy of a different artefact.
- ENH-LOGGING-STANDARDIZATION-001-OPS-003 — No shipped configuration file may default `log.folder` to a
  developer-specific absolute path. It is `/Users/ktanaka/Documents/logs` in
  `config/marlo-dev.properties:99` and `/home/julianrodriguez/logs` in `config/marlo-test.properties:139`
  today, so a fresh checkout logs to a directory that does not exist.
- ENH-LOGGING-STANDARDIZATION-001-OPS-004 — A new occurrence of `printStackTrace()` or `System.out`/
  `System.err` printing MUST fail the static-analysis gate. Existing occurrences MUST be suppressed
  explicitly, so the gate is actionable on day one instead of firing ~473 times and being ignored.
- ENH-LOGGING-STANDARDIZATION-001-OPS-005 — The logging conventions MUST be recorded in an ai-context
  document, so that routine work does not need to re-derive them from this spec.

## 4. Out-of-Scope

Everything below is part of the DevOps agreement but requires infrastructure work, a new component, or a
decision by another team. It is listed here to stay traceable, not because it is unimportant.

- **The message queue and the Pre-Processor component.** The Notion design routes every tool's logs through a
  queue into a normalizing pre-processor. Neither exists, and the pre-processor has no owner or repository.
- **Any log collector configuration.** No Promtail target, scrape config or agent deployment. A2-1470 left an
  agent on `marlotest` only, tailing `/opt/tomcat-aiccra/logs/catalina.out`; production has none. This spec
  neither uses nor modifies that setup.
- **Loki retention, Grafana dashboards and Loki-side alert rules.** Including the retention and access policy
  that emitting user identifiers into a central store would require.
- **The cross-tool field-name and mandatory-field agreement.** The Notion page names the fields twice with two
  different sets of names (`tool`/`module` in the schema block, `tool_name`/`module_section` in the field
  descriptions) and its mandatory-field list ends in "etc.". §9 records the names MARLO adopts meanwhile.
- **The status-code alert control list.** Still an open TODO in the source document; until it exists,
  "alerting" has no agreed definition across tools.
- **The prescribed human-readable error line.** The source document requires every tool to print
  `[Nest] 218 - 09/26/2025, 10:15:09 AM ERROR [STAR/PRMS] [Controller] [function] [method] [USER:1]: /my/routes (ErrorStack)`.
  That is a NestJS console line: the `[Nest] <pid>` prefix and the US-formatted timestamp are emitted by
  NestJS's own logger, which MARLO does not run. NF-001 keeps MARLO's text pattern unchanged because the
  team reads that file over SSH and `reports/ai-context/deployment-checklist.md:80,83` instructs them to.
  The same information — controller, method, user, route, stack — is carried by the JSON event instead.
  Whether the line is normative for non-Nest tools is §8.7.
- **`docs/detailed-design/detailed-design.md` §9.** Not needed: §9.1 already prescribes SLF4J + Logback, a
  per-class logger, the level semantics and the `System.out`/`printStackTrace` prohibition. This spec
  implements that section rather than contradicting it, so no constitutional change is triggered. §9.4's
  "dedicated APM is an open gap" also remains true after this work.
- **The `action/summaries` and `utils` cleanup.** The largest concentration of the anti-patterns
  (168 `printStackTrace`/`System.out` plus 63 message-only error logs in `action/summaries`, 84 in `utils`).
  Deferred to its own commit series under A2-2435 to keep this spec reviewable.
- **The `maven-checkstyle-plugin` version mismatch.** `mvn checkstyle:check` cannot run in this checkout
  (plugin 2.9.1 against checkstyle 8.18, `NoSuchMethodError: Checker.setClassloader`). Separate ticket; this
  spec runs the gate through the direct-invocation script instead.
- **Application performance monitoring and metrics.** Logs only; no APM agent, no metrics endpoint.

## 5. Personas Affected

- **IBD developer / Tech lead (primary).** The direct consumer. Gains a log where one request's lines can be
  selected together, where a REST 500 leaves a record, and where a stack trace points at its origin.
- **MARLO Support (`Marlosupport@cgiar.org`) (primary).** Receives the exception notifications. Gains the
  `request_id` and `status_code` needed to find the event, and stops receiving one mail per repetition of the
  same fault.
- **QA Reviewer (indirect).** A reproducible defect report can cite a `request_id` instead of a screenshot and
  a timestamp.
- **Cluster Coordinator, PMU (no visible change).** No UI, wording or behaviour change. Their identifiers
  appear in the log under the restriction in SEC-001.

## 6. Acceptance Criteria

**AC for FN-001, FN-003 and NF-001:**
- Given the application running under the `dev` profile with `log.json=true`,
- When a user logs in and opens a project section,
- Then every line of the JSON log MUST parse as a JSON object (`jq -e .` succeeds on each),
- And each line MUST carry non-empty `level`, `timestamp`, `environment`, `tool_name` and `controller_affected`,
- And the existing text log MUST be unchanged in format from the same run before this change.

**AC for FN-002 and NF-003:**
- Given two different users hitting the application in sequence on the same Tomcat worker thread,
- When their requests complete,
- Then all events of a single request MUST share one `request_id`,
- And no event of the second request MUST carry the first user's `user_id`,
- And an event produced outside any request MUST carry no `user_id` at all, rather than a stale one.

**AC for FN-004:**
- Given a REST call to an existing endpoint with an identifier that does not exist,
- When the endpoint returns `404 NOT_FOUND`,
- Then exactly one log event MUST be emitted carrying `status_code: 404`,
- And the same MUST hold for each of the fifteen handlers in `ExceptionTranslator`, at the level its status
  implies.

**AC for FN-005:**
- Given an exception thrown inside a class under `org.cgiar.ccafs.marlo.rest.controller.v2.controllist`,
- When it propagates out of the controller,
- Then exactly one log event MUST be emitted, by `ExceptionTranslator`, carrying the status returned to the
  client,
- And no `LoggingAspect` class and no aspect bean MUST remain in the source tree,
- And this MUST be demonstrated by an inspected log line, not by reading the code.

**AC for FN-006 and FN-009:**
- Given a caught exception on the save chain, the authentication path, a servlet filter or the REST layer,
- When it is reported,
- Then the event MUST carry the stack trace in `stack_trace`,
- And no `printStackTrace()`, `System.out` or `System.err` call MUST remain in those paths,
- And no `catch` block in those paths MUST log an error and return `null` without the caller being able to
  distinguish "empty" from "failed".

**AC for FN-007 and FN-008:**
- Given an unhandled exception that triggers a support notification,
- When the same exception is triggered a second time from the same module and status,
- Then the first notification MUST contain the `request_id` and `status_code`,
- And exactly one notification MUST have been sent, not two.

**AC for SEC-001:**
- Given a successful request by an authenticated user,
- When its events are inspected,
- Then `user_id` MAY be present and `user_name`/`user_email` MUST be absent,
- And on an event with `status_code` 400 or above, both MUST be present.

**AC for NF-006:**
- Given an event carrying `status_code` and `user_id`,
- When the line is inspected, neither value MUST appear quoted,
- And `jq -e 'select(.status_code >= 500)'` MUST select it without a `tonumber` cast.

**AC for SEC-002 and SEC-003:**
- Given a login request and a request carrying an authorization header,
- When their events are inspected,
- Then no event MUST contain a password, token, authorization header value, cookie or session identifier,
- And a deeply nested exception MUST produce a `stack_trace` no longer than the configured bound.

**AC for OPS-001 and OPS-002:**
- Given the application started under each of the `dev`, `test` and `pro` profiles with no
  `marlo.environment` property set anywhere,
- Then `environment` MUST be `DEV`, `TEST` and `PROD` respectively,
- And with `log.json=false` no JSON file MUST be written, while the text log continues unchanged.

**AC for OPS-004:**
- Given a newly added `printStackTrace()` in any file,
- When the Checkstyle gate runs,
- Then it MUST report a violation for that occurrence,
- And it MUST NOT report the pre-existing occurrences listed in the suppressions file.

## 7. Constitutional Compliance Checklist

- [x] **Phase replication:** Not applicable — no phased entity is written. Logging state is per-request and
      in-memory; the spec persists nothing to the database.
- [x] **Save validation:** Not applicable — no new save path, `Action.validate()` or `Validator`. The spec
      only adds logging to the existing REST error handler and reads the session in existing filters.
- [x] **Permissions:** Not applicable — no new Struts action and no new interceptor stack. The new servlet
      filter is registered on `/*` and performs no authorization decision.
- [x] **Specificity:** Not applicable — the change ships unconditionally for all Global Units. Enablement is
      per environment through `log.json`, an operational property, not a Global Unit specificity.
- [x] **Migrations:** Not applicable — no schema change (see §3 Data).
- [x] **i18n:** Not applicable — no user-facing string. Log messages are English-only per `AGENTS.md` and are
      not user-visible; the support notification is internal.
- [x] **License header:** The one new Java file (`LoggingContextFilter.java`) carries the GPL header from
      `AGENTS.md`.
- [x] **Code style:** Checkstyle passes with no new violations; 2-space indent, 120-char lines. Verified with
      `~/.claude/skills/marlo-verify/scripts/checkstyle.sh --baseline`, since `mvn checkstyle:check` cannot run
      in this checkout (§4).
- [x] **REST:** No new `/api/*` endpoint. The change is confined to the existing `rest/errors/` handlers, which
      is where `detailed-design.md` §9.3 already places centralized REST error handling.
- [x] **Audit:** No change to `IAuditLog` / `HibernateAuditLogListener`. `AuditLogContextProvider` is read for
      its existing push/pop pattern and is not modified.
- [x] **Dependency floors:** No dependency is downgraded. One is added — `logstash-logback-encoder` 6.6, pinned
      to 6.x because 7.x requires logback 1.3+/SLF4J 2.x and this checkout is logback 1.2.13. Jackson is
      already at the ≥2.17.x floor.
- [x] **Branching:** Work continues on `logging-standardization`, branched from `staging`; merge target is
      `staging`.

## 8. Open Questions

Questions that do not block this spec but shape its successor. Each belongs to the DevOps group, not to MARLO.

1. **Which field names are normative?** The source document names them twice, differently
   (`tool`/`module` vs `tool_name`/`module_section`). NF-004 keeps the cost of switching at one edit; the
   answer is still needed.
2. **Is `status_code` mandatory, and what carries severity when there is no HTTP status?** Scheduled jobs,
   queue consumers, Flyway migrations at startup and every INFO flow event have no HTTP status. As written,
   the pre-processor would flag all of them as incomplete. MARLO's position: `level` is the mandatory severity
   field and `status_code` is nullable.
3. **What does `service_affected` hold for a monolith?** MARLO has no microservices, so the field has no
   natural value. It is currently emitted empty.
4. **Which status codes actually alert?** Still a TODO in the source document. MARLO's position: from 500.
   177 of the `HttpStatus` usages in MARLO's REST layer are `NOT_FOUND`, mostly legitimate "no record with
   that id" responses, so an alert threshold of 400 would bury the real failures.
5. **Is the PII restriction in SEC-001 acceptable to the group?** It is a deliberate divergence from a schema
   that requires `user.name` and `user.email` on every event.
6. **What happens to an event that fails validation?** The source document says "discard or flag". A discarded
   event is exactly the one needed during an incident; MARLO's position is a quarantine stream, never a drop.
7. **Is the prescribed console line normative for tools that are not NestJS?** The required format carries a
   `[Nest] <pid>` prefix and a NestJS-formatted timestamp, neither of which a Java/Logback application
   produces. MARLO's position: the JSON event is the normative artefact and the text log stays in the format
   the team already reads; if a shared human-readable line is genuinely required, it needs a framework-neutral
   specification. See §4.
8. **Is `user` a nested object or three flat keys?** The schema block nests `user_id`, `name` and `email`
   under `user`; SLF4J's MDC holds flat string keys only, so MARLO emits `user_id`, `user_name` and
   `user_email` at the top level. Either the pre-processor nests them, or the agreed schema flattens. See §9.

## 9. Decision Log

- 2026-09-07 — **Scope limited to what this repository can deliver alone.** — Rationale: the queue, the
  pre-processor and the collector are not owned by this team and have no delivery date. Emitting a structured,
  correlatable log is independently valuable — it is queryable with `jq` on the machine that produces it — and
  the remaining integration becomes configuration rather than code.
- 2026-09-07 — **A dedicated rolling JSON file, not stdout and not a broker.** — Rationale: writing JSON to
  stdout would let the existing `catalina.out` collector pick it up with no external change, but it makes the
  file unreadable over SSH and mixes MARLO's events with Tomcat's own output. Publishing to RabbitMQ would
  couple the log to a broker whose own failures are currently invisible (`MicroserviceReportAction` reports
  them with `System.out.println`), so a broker outage would take the logging with it, silently. Alternatives
  considered: JSON on the console appender; a custom AMQP appender.
- 2026-09-07 — **`environment` derived from the Spring active profile.** — Rationale: the profile is already
  set per environment and already resolves both the properties file and `logback.xml`. A new property would
  have to be set on each server, and `marlo-pro.properties` is gitignored and absent from the repository, so
  production would silently emit nothing. An optional `marlo.environment` override remains for the case where
  one profile serves two machines.
- 2026-09-07 — **Field names decided here, in one place.** — Rationale: waiting for the cross-tool agreement
  would block delivery indefinitely. The descriptive variant from the field descriptions is adopted, plus
  `level` and `request_id`, which the agreed schema omits and which are the two most useful fields in any
  aggregated log. NF-004 keeps a later rename to a single edit.
- 2026-09-07 — **`user_name`/`user_email` only on error events.** — Rationale: the agreed schema puts staff
  identifiers on every event, which makes any central store a personal-data store and duplicates them into
  every alert mail. Identifying the user matters when someone must contact them, which is the error case.
  Recorded as a deliberate divergence; see §8.5.
- 2026-09-07 — **Alert from 500, not from 400.** — Rationale: 177 of the `HttpStatus` usages in MARLO's REST
  layer are `NOT_FOUND`, overwhelmingly legitimate responses. An alert channel that fires on those is muted
  within a day, which is worse than no channel.
- 2026-09-07 — **Cleanup limited to the critical paths, with a suppressions file for the rest.** — Rationale:
  a single sweep over ~150 files would be unreviewable and would conflict with every open branch. Ordering by
  criticality (save chain, authentication, filters, REST) fixes the paths where a swallowed exception corrupts
  data first. The Checkstyle suppressions file makes the gate bite on new code immediately without demanding
  the whole backlog be cleared first.
- 2026-09-07 — **`user` emitted as three flat keys, not as a nested object.** — Rationale: the agreed schema
  nests `user_id`, `name` and `email` under a `user` object. SLF4J's MDC is a flat `Map<String, String>`, so a
  nested object would require either a custom `JsonProvider` or serialising a JSON fragment into an MDC value
  and hoping the encoder does not escape it. Both put the field structure in Java instead of in the one
  `<providers>` block NF-004 requires. `user_id` / `user_name` / `user_email` are emitted at the top level and
  the pre-processor nests them if the group keeps the nested shape — a mapping in one component rather than a
  custom provider in every tool. Recorded as a deliberate divergence; see §8.8.
  **Superseded in part by the provider entry below:** NF-006 and SEC-001 put a MARLO-owned `JsonProvider` in
  the tree anyway, so "it would need a custom provider" is no longer a cost that distinguishes the two shapes.
  The decision stands on the pre-processor being the right place to reshape a cross-tool schema; if §8.8
  settles on nesting, it is now a small change in one class.
- 2026-09-07 — **The prescribed console error line is not adopted.** — Rationale: the format in the source
  document is a NestJS console line, including a `[Nest] <pid>` prefix and a NestJS-formatted timestamp that a
  Logback pattern does not produce. Reproducing it would mean changing the text log the team reads over SSH
  (NF-001) to imitate the output of a framework MARLO does not run, while the JSON event already carries every
  field it contains. Recorded as a divergence rather than an omission; see §4 and §8.7.
- 2026-09-07 — **`LoggingAspect` removed rather than repaired.** — Rationale: its pointcut has matched
  nothing since January 2018, and repairing it would make things worse, not better. The aspect sees the
  exception but never the response, so it cannot supply the `status_code` FN-004 requires, and
  `ExceptionTranslator:138` handles `Exception`, so every REST exception already reaches the one component
  that does know the status. A working pointcut would therefore log each REST error twice — once without the
  status — flag every legitimate 404 as ERROR, wrap ~190 Spring beans in proxies, and, because `<root>` is
  `ALL` and the encoder's own `isDebugEnabled()` guard is consequently always true, stringify every REST
  argument and result into the log. Alternatives considered: fix the pointcut and DEBUG-gate the `@Around`;
  fix the pointcut and add a class-scoped `<logger>` so the existing guard finally works. Both keep a
  component whose only correct output duplicates `ExceptionTranslator`. Consequence: T01 becomes a deletion
  and the substance of FN-004 moves entirely into T02.
- 2026-09-07 — **A MARLO-owned `JsonProvider` emits the context fields, rather than the encoder's MDC
  provider.** — Rationale: three separate requirements cannot be met by the stock providers. NF-006 needs
  `status_code` and `user_id` as JSON numbers, and MDC is a `Map<String,String>` with no typed writer in the
  6.6 encoder ADR-4 pins — the `mdcEntryWriter` that would solve it arrived in 7.3, which needs logback 1.3.
  SEC-001 needs `user_name`/`user_email` suppressed unless the status is 400 or above, a decision that can
  only be taken when the event is written, since the values are populated at the start of the request and the
  status is known at the end. And `service_affected` has no value in a monolith (§8.3) and should be omitted
  rather than emitted empty on every line. One class takes all three decisions in one place, which also keeps
  NF-004 true. Alternatives considered: `StructuredArguments.keyValue` at each call site, which covers
  `status_code` but not `user_id`, since that applies to every event of a request rather than one call;
  emitting strings and casting in a downstream pre-processor, which is out of scope and does not exist, and
  would leave `jq` — the consumer ADR-1 accepts as the baseline — needing a cast on the field that decides
  alerting.
- 2026-09-07 — **`docs/detailed-design/detailed-design.md` §9 left unchanged.** — Rationale: §9.1 already
  prescribes exactly what this spec implements. Editing it would make the work a constitutional event under
  `CLAUDE.md`, requiring an epic spec and external review, for no change in content. The conventions land in
  `reports/ai-context/logging-standard.md` instead (OPS-005).
