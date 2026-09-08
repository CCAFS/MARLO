# Logging Standardization — Design

**Spec ID:** ENH-LOGGING-STANDARDIZATION-001
**Status:** Draft
**Owner:** IBD Team — Alliance of Bioversity International and CIAT
**Last Updated:** 2026-09-07
**Implements requirements:** FN-001 … FN-009, NF-001 … NF-005, SEC-001 … SEC-004, OPS-001 … OPS-005
**Touches modules:** marlo-web, marlo-data, marlo-utils

---

## 1. Architecture Summary

A log event is assembled from three sources: the call site supplies the message, level and exception; the
request supplies the context; the appender renders both into JSON. MARLO has the first and the third within
reach and is missing the second entirely — there is no MDC usage anywhere in the codebase and no `%X{}` in any
appender pattern.

The design adds one filter that owns the per-request context, enriches it from the two places that already
know the user, and renders it through a second Logback appender that runs alongside the existing text ones.
Nothing about the text log changes (NF-001).

```
HTTP request
  │
  ├─ RemoveSessionFromUrlFilter ─ CORSFilter
  │
  ├─ LoggingContextFilter            [NEW]  MDC.put request_id, environment, tool_name,
  │    │                                    controller_affected  ·  MDC.clear() in finally
  │    ├─ MARLOCustomPersistFilter          (existing: AuditLogContext push/pop, tx)
  │    ├─ Shiro filter
  │    ├─ AddUserIdFilter            [MOD]  MDC.put user_id   (subject available from here on)
  │    │
  │    ├── Struts path ──► RequireUserInterceptor  [MOD]  user_name / user_email from session
  │    │                        └─► Action ──► Manager ──► DAO
  │    │                              └─ UnhandledExceptionAction [MOD] request_id + status_code, dedup
  │    │
  │    └── REST path  ──► AddSessionToRestRequestFilter [MOD]  user_name / user_email + tool_name
  │                            └─► @RestController
  │                                  ├─ LoggingAspect        [DEL] advised nothing; see ADR-8
  │                                  └─ ExceptionTranslator  [MOD] one log per handler, with status_code
  │
  └─ MDC.clear()
                        │
   SLF4J ──► Logback ───┼──► FILE-TEST / FILE-PRODUCTION / CONSOLE-*   text, unchanged
                        └──► FILE-JSON                        [NEW]    one JSON object per line
```

Three things in the existing code are broken or unused and are preconditions rather than improvements: the
REST logging aspect has never matched anything and is removed rather than repaired (ADR-8), the REST error
handler logs almost nothing, and `log.folder` defaults to a developer's home directory. They are dealt with
first (§15, ADR-2).

## 2. Module Footprint

### marlo-web

- New: `web/filter/LoggingContextFilter.java` — `OncePerRequestFilter`, `@Named`, owns the MDC lifecycle.
- Modified: `WebAppInitializer.java` — register the new filter on `/*`, immediately after `CORSFilter`.
- Modified: `web/filter/AddUserIdFilter.java` — put `user_id` into MDC (already reads the Shiro principal at
  `:50-53`).
- Modified: `web/filter/AddSessionToRestRequestFilter.java` — put `user_name`, `user_email` and `tool_name`
  into MDC (already resolves the user at `:180-183` and the global unit at `:94-115`).
- Modified: `interceptor/RequireUserInterceptor.java` — put `user_name`, `user_email` into MDC on the Struts
  path (already holds the `User` from the session at `:47-76`).
- Removed: `logging/LoggingAspect.java` and its `@Bean` in `MarloRestApiConfig.java:78-81` — the pointcut has
  matched nothing since 2018 and repairing it would duplicate `ExceptionTranslator` (§15, ADR-8).
- New: `logging/MarloMdcJsonProvider.java` — the single `JsonProvider` that writes the context fields, types
  `status_code` and `user_id` as numbers (NF-006), suppresses `user_name`/`user_email` unless the status is
  400 or above (SEC-001) and omits `service_affected` (§15, ADR-9).
- Modified: `rest/errors/ExceptionTranslator.java` — one log call per `@ExceptionHandler`, carrying the status.
- Modified: `action/UnhandledExceptionAction.java` — `request_id` and `status_code` in the notification, plus
  deduplication.
- Modified: `resources/logback.xml` — new `FILE-JSON` appender, wired into the conditional `<root>` block.
- Modified: `resources/config/marlo-dev.properties`, `resources/config/marlo-test.properties` — fix
  `log.folder`, add `log.json`.
- Modified: the Phase-5 cleanup files listed in `task.md` (`rest/controller/v2/controllist/**`,
  `rest/services/deliverables`, `web/filter`, `action/report/MicroserviceReportAction.java`).

### marlo-data

- Modified: `data/dao/mysql/AbstractMarloDAO.java`, `data/dao/mysql/AuditLogMySQLDao.java`,
  `data/HibernateAuditLogListener.java` and the five `data/manager/impl/ProjectInnovation*ManagerImpl.java`
  offenders — replace `printStackTrace()` / `System.out` and message-only error logs.
- Modified: `data/dao/mysql/UserMySQLDAO.java`, `data/manager/impl/UserManagerImp.java` — same, on the
  authentication path.

### marlo-utils

- Modified: `utils/APConfig.java` — add `getEnvironment()`.

### Repository root

- Modified: `marlo-parent/pom.xml` — add `logstash-logback-encoder` 6.6 to `dependencyManagement` and the
  dependency to `marlo-web`.
- Modified: `configuration/marlo-checkstyle.xml` — two `RegexpSinglelineJava` rules plus a `SuppressionFilter`.
- New: `configuration/marlo-checkstyle-suppressions.xml`.
- New: `reports/ai-context/logging-standard.md`.

## 3. Data Model Changes

Not applicable. This spec adds no table, column, index, enum, foreign key or Flyway migration. Logging state
is per-request and in-memory; nothing reaches the database. The audit tables and
`HibernateAuditLogListener` are read for their existing push/pop pattern and are not modified.

## 4. API / Action Surface

### Struts actions (.do)

No new action and no mapping change. `UnhandledExceptionAction`, the existing global fallback registered in
`struts.xml`, gains two fields in the notification it already sends and a deduplication check.

### Spring MVC REST

No new endpoint, no DTO change, no OpenAPI change. `rest/errors/ExceptionTranslator` keeps its fifteen
`@ExceptionHandler` methods, their `@ResponseStatus` annotations and their `ErrorDTO` return shape exactly as
they are; each gains one log statement. The response a client receives is byte-for-byte unchanged, which is
what makes this safe to ship without partner coordination.

### Existing JSON endpoints

Not applicable. No `*.json` Struts path is added (per `AGENTS.md` and `CLAUDE.md` rule 3).

## 5. Frontend Composition

Not applicable. No FTL view, macro, include, JS module, CSS file or i18n key changes, so no cache-busting
parameter bump applies either. The only rendered artefact touched is the internal support notification body
built in `UnhandledExceptionAction`, which is not a user-facing view.

## 6. Persistence & Phase Replication Plan

Not applicable. No phased entity is written, so there is no save or delete propagation to design. The
`ManagerImpl` classes listed in §2 are touched only to replace their error-reporting statements; their save
and delete chains and their replication behaviour are unchanged.

Constraint that does apply: the cleanup MUST NOT alter control flow. Where a `catch` block currently logs and
returns `null`, the logging is corrected in place and the `null` is left alone unless a caller is verified to
handle the alternative. Changing that return value is a behavioural fix and belongs to its own task
(see `task.md`, T09 note).

## 7. Validation & Save Pipeline

Not applicable. No `Action.validate()`, no `Validator` class and no interceptor-stack change. The spec adds
no user-visible error surface; failures continue to reach the user exactly as they do today, through the
existing page-level message banner and the existing `ErrorDTO` shape.

`RequireUserInterceptor` is modified, but only to write MDC keys inside the `try` it already has. Its
authorization behaviour, its `AuditLogContext` push and its `finally` pop are untouched, and the MDC writes
happen after the existing session read so a request without a user still behaves as before.

## 8. Permissions & Edit Gates

Not applicable as a change; relevant as a constraint.

- `LoggingContextFilter` is registered on `/*` and makes **no** authorization decision. It runs before the
  Shiro filter, so no subject is available to it and none is required.
- The `user_id` enrichment lives in `AddUserIdFilter`, which is already registered after the Shiro filter on
  `*.do, *.json, /, /api/*, /swagger/*`, precisely because the principal is not available earlier. This split
  is why the design uses two insertion points instead of one.
- No interceptor is added to or removed from any Struts stack, and no `canEdit*` gate changes.

## 9. Specificity / Feature-Flag Strategy

Not applicable — the change ships unconditionally for all Global Units. Enablement is per environment through
the `log.json` property in `marlo-${profile}.properties`, which is an operational switch, not a Global Unit
specificity. No `parameters` / `custom_parameters` row and no `APConstants` key is added.

## 10. Integration Points

- **None added.** No CLARISA, CGSpace, BI, AI-service, S3 or Pusher call is introduced. This is deliberate:
  it is what makes the spec deliverable without an external dependency (see `requirements.md` §4).
- **RabbitMQ** — present in the stack (`spring-amqp` 3.1.8 and `amqp-client`, used by
  `action/report/MicroserviceReportAction.java`) and explicitly **not** used as a log transport (§15, ADR-1).
  That file is touched only to stop reporting its own connection failures through `System.out.println`
  (`:307`, `:316-322`, `:385`, `:479`) — which is also why it is a poor candidate to carry the log.
- **Downstream consumer** — the JSON file is a plain rolling file. Any future collector reads it without a
  code change, which is the whole point of choosing a file over a broker.

## 11. Observability

This section is the substance of the spec.

### The emitted schema

| Field | JSON type | Source | When populated | MDC key |
|---|---|---|---|---|
| `timestamp` | string | Logback | always, ISO 8601 UTC with milliseconds | — |
| `level` | string | call site | always | — |
| `request_id` | string | `LoggingContextFilter`, generated per request | every event inside a request | `request_id` |
| `environment` | string | `APConfig.getEnvironment()`, from the Spring active profile | always | `environment` |
| `tool_name` | string | `BaseAction.getCurrentCrp()` / `SESSION_CRP` (Struts); `AddSessionToRestRequestFilter.addCrpToSession()` (REST) | once the global unit is known | `tool_name` |
| `module_section` | string | logger name, i.e. the emitting class | always | — |
| `controller_affected` | string | request URI **without the query string**, from `LoggingContextFilter` | every event inside a request | `controller_affected` |
| `message` | string | call site | always | — |
| `status_code` | **number** | `ExceptionTranslator` per handler; 500 from `UnhandledExceptionAction` | error events only | `status_code` |
| `user_id` | **number** | `AddUserIdFilter`, Shiro principal | authenticated requests | `user_id` |
| `user_name`, `user_email` | string | `RequireUserInterceptor` (Struts); `AddSessionToRestRequestFilter` (REST) | only when `status_code >= 400` (SEC-001) | `user_name`, `user_email` |
| `stack_trace` | string | Logback throwable provider, truncated (SEC-003) | when an exception is passed | — |
| `payload` | object | call site, allow-list only (SEC-002) | rarely, explicitly | — |
| `service_affected` | — | — | **omitted**; no meaning for a monolith (`requirements.md` §8.3) | — |

Field names live in one `<providers>` block in `logback.xml` (NF-004).

The query string is excluded from `controller_affected` deliberately: it carries entity ids and occasionally a
token, which SEC-002 keeps out of the log. The path alone is what a dashboard groups by.

### Typing and conditional emission — one provider, three requirements

Three requirements cannot be satisfied by the encoder's stock providers, and all three are decisions about how
an event is *written* rather than what the request *knows*:

- **NF-006, numeric fields.** SLF4J's MDC is a `Map<String,String>`. The 6.6 encoder ADR-4 pins has no typed
  MDC writer — `mdcEntryWriter` arrived in 7.3, which requires logback 1.3 — so every MDC-sourced field would
  serialize as `"404"` rather than `404`. `status_code` is what any query over the log filters on, starting
  with the `jq` baseline ADR-1 accepts, and `"404" >= "500"` is a string comparison. This does not affect
  ADR-3's mail alert, which is decided in Java before the event is written.
- **SEC-001, conditional PII.** `user_name` and `user_email` enter the context at the start of the request;
  `status_code` is known at the end. No rule expressed at population time can say "only when the status ends
  up 400 or above". It can only be applied when the line is written.
- **`service_affected`.** Emitting an always-empty field on every line costs bytes and teaches consumers to
  ignore a field name that the cross-tool schema declares meaningful.

`logging/MarloMdcJsonProvider.java` takes all three decisions. It reads the MDC, writes `status_code` and
`user_id` through `writeNumberField`, drops `user_name`/`user_email` when `status_code` is absent or below 400,
and omits `service_affected` entirely. `status_code` and `user_id` remain MDC keys rather than call-site
`StructuredArguments`, because `user_id` applies to every event of a request and not to one call, and keeping
both in the same mechanism keeps NF-004 true — the field names stay in one place.

A malformed or missing MDC value must not throw: the provider treats an unparseable `status_code` as absent,
which is also what NF-005 requires of anything on the logging path.

`user_id`, `user_name` and `user_email` are emitted as three top-level keys. The agreed schema nests them
under a `user` object. Note that ADR-9's provider removes the original reason for not nesting — with a provider
in the tree, nesting is no longer expensive — so the decision now rests only on the pre-processor being the
right place to reshape a cross-tool schema, and on §8.8 still being open. If the group settles on the nested
shape, ADR-7 should be revisited rather than worked around: the change is now local to
`MarloMdcJsonProvider`.

The text appenders keep their existing pattern (NF-001). The source document's prescribed error line is a
NestJS console format MARLO does not produce; the same fields are carried by the JSON event
(`requirements.md` §4, §8.7).

### Appender

`FILE-JSON`, a `RollingFileAppender` with `LogstashEncoder`, writing
`${log.folder}/marlo-json-${log.instance}.log` with a `TimeBasedRollingPolicy` and 30-day history — the same
shape as the four existing appenders. Wired into the existing conditional `<root>` block behind a new
`log.json` property (OPS-002). The four text appenders and their pattern
`%d{yyyy-MM-dd_HH:mm:ss.SSS} - [%thread]%logger{0} [%-5level]: %msg%n` are untouched (NF-001).

The framework logger levels already in `logback.xml:57-146` — including the two `net.sf.ehcache.pool.sizeof.*`
loggers raised to ERROR by A2-2435 Part 1 to stop 31,983 lines of `SizeOf` noise — apply to the JSON appender
automatically, since they are logger-scoped rather than appender-scoped. No duplication is needed.

### What starts being observable

- REST errors. Fourteen of fifteen `ExceptionTranslator` handlers are silent today; a 500 returned to a
  partner leaves no record.
- The 473 `printStackTrace` / `System.out` calls in the critical paths, which currently write to `System.err`
  and never enter Logback, so they are absent from the file and lost on rollover.
- The 135 error logs that discard the stack trace, whose originating line is currently unknowable.

### Notification

`UnhandledExceptionAction` + `SendMailS` already email the support team with user, CRP, phase and
`actionName`. They gain `request_id` and `status_code` (FN-007) and a deduplication key of
`tool_name + module_section + status_code` (FN-008). REST 5xx routes into the same notifier so both paths
share one channel; 4xx does not notify (§15, ADR-3).

### Audit

Unchanged. `IAuditLog`, `HibernateAuditLogListener` and the audit columns are not touched.
`AuditLogContextProvider` is read as the precedent for the push/pop shape and is not modified — note that
A2-2435 Part 1 already removed its false ERROR on the login path, so the two concerns do not overlap.

## 12. Performance & Scalability

- **Per request:** one UUID generation, a handful of `MDC.put` calls (a `ThreadLocal` map) and one
  `MDC.clear()`. No additional database query (NF-002) — every value comes from data the request already
  holds: the Shiro principal `AddUserIdFilter` already reads, the `User` already in the Struts session, and
  the `User` `AddSessionToRestRequestFilter` already resolves.
- **Per event:** JSON serialization of a small flat object. The dominant cost is the second file write, since
  every event is now rendered twice. Acceptable because the volume is already dominated by what A2-2435 Part 1
  removed; if it becomes a concern, `AsyncAppender` around `FILE-JSON` is the mitigation and does not change
  the schema.
- **Disk:** a second rolling file with the same 30-day retention as the existing ones. `log.json` (OPS-002)
  allows switching it off per environment if space is constrained.
- **Bounded fields:** `stack_trace` truncation (SEC-003) is a performance control as much as a compatibility
  one — an unbounded trace on a hot error path is the realistic way this floods a disk.
- **`LoggingAspect` removed.** Its `@Around` advice stringified every argument and every result, and its
  `isDebugEnabled()` guard does not protect anything: `logback.xml:114` is `<root level="ALL">` and no
  `<logger>` scopes `org.cgiar.ccafs.marlo`, so the guard is always true. On `dev` and `test` the text
  appenders carry no `ThresholdFilter`, so the output would be written; on production the `ThresholdFilter
  INFO` discards the event *after* the strings have been built. Repairing the pointcut was the one change in
  this spec with a real performance risk, and deleting the class removes it instead of managing it
  (§15, ADR-8).
- **The JSON provider** does no work beyond a map lookup and, for two fields, an `Integer.parseInt`. It adds
  nothing measurable next to the second file write.

## 13. Security Considerations

- **Personal data.** The agreed schema puts `user.name` and `user.email` on every event. SEC-001 restricts
  them to events with `status_code >= 400`, the only case where someone must contact the affected user. This
  is a deliberate divergence from the cross-tool schema, recorded in `requirements.md` §8.5 and §9.
- **`payload` allow-list.** SEC-002 permits entity ids, section and phase only. The raw request or response
  body is never emitted, which keeps passwords out of login events and tokens out of authenticated ones. An
  explicit deny-list (`password`, `token`, `authorization`, `cookie`, session identifiers) backs the allow-list
  as a second barrier.
- **Authentication-path cleanup.** `UserMySQLDAO` and `UserManagerImp` are in the Phase-5 cleanup. SEC-004
  applies: their error reporting is corrected without logging the values they handle. Note that
  `UserMySQLDAO:171` concatenates the email and password into the HQL query — that is a separate defect,
  already recorded as out of scope in A2-2435, and the cleanup must not obscure it.
- **Thread-local leakage.** NF-003. Tomcat pools worker threads, so an MDC left populated attributes one
  user's identity to the next request served by that thread. `MDC.clear()` in the `finally` of the outermost
  filter is the control, and the paired verification is in `task.md`.
- **No new attack surface.** No endpoint, no parameter, no deserialization of external input. The JSON file
  inherits the filesystem permissions of the existing log directory.
- **Log injection.** Message content reaching the JSON encoder is escaped by the encoder rather than
  concatenated into a line, which removes the newline-injection risk the current text pattern carries.

## 14. Backwards Compatibility & Rollout

- **No migration, no data change, nothing to reverse in the database.**
- **Client-visible behaviour is unchanged.** `ExceptionTranslator` keeps its statuses and its `ErrorDTO`
  shape; only logging is added. No REST consumer needs to be told.
- **Dual-running by construction.** The text log continues exactly as before and the JSON file is additive.
  Anyone reading `marlo.log` over SSH is unaffected, and `reports/ai-context/deployment-checklist.md:80,83`
  still works as written.
- **Rollout:** deploy with `log.json=true` on `dev` first, then `test`, then production. No coordination with
  another team is required at any step, which is the point of `requirements.md` §4.
- **Instant off switch:** set `log.json=false`. The text log keeps working, so the fallback is the current
  behaviour rather than no logging.
- **Ordering constraint:** Phase 1 (the three broken things) and Phase 2 (MDC) must land before Phase 3, or
  the JSON appender emits a schema with empty context fields and looks like a regression.
- **Checkstyle rules land last.** Adding them before the Phase-5 cleanup would require a suppressions file
  covering ~473 occurrences, most of which the cleanup is about to remove; the file would be stale on arrival.

## 15. Decision Records

### ADR-ENH-LOGGING-STANDARDIZATION-001-1 — A rolling JSON file, not a queue and not stdout

- **Decision:** Logback writes the JSON to a dedicated rolling file next to the existing text log.
- **Rationale:** A file needs nothing outside this repository, is readable with `jq` the day it ships, and can
  be picked up later by any collector as a configuration change rather than a code change.
- **Alternatives considered:** *JSON on the console appender* — the existing `catalina.out` collector would
  pick it up with no external change, but `catalina.out` stops being human-readable and MARLO's events mix
  with Tomcat's own output. *A custom AMQP appender publishing to RabbitMQ*, as the source design proposes —
  it couples the log to a broker whose own failures are currently invisible, so a broker outage silently takes
  the logging with it; Promtail also has no AMQP target, so the queue would not connect to the intended
  collector anyway.
- **Status:** Accepted.

### ADR-ENH-LOGGING-STANDARDIZATION-001-2 — Fix the existing plumbing before adding the format

- **Decision:** The `LoggingAspect` pointcut, the silent `ExceptionTranslator` handlers and the personal
  `log.folder` paths are corrected first, in their own commits, before the JSON appender exists.
- **Rationale:** All three are already-broken behaviour rather than new functionality, so they are reviewable
  on their own and valuable even if the rest slips. `status_code` cannot be populated at all until
  `ExceptionTranslator` logs, since that is the only place in the codebase where the HTTP status is known.
- **Consequence:** The aspect turned out not to be repairable in a way that helps, so "fix" became "remove"
  for that one item; see ADR-8. The other two are unchanged and remain the first commits.
- **Status:** Accepted, amended by ADR-8.

### ADR-ENH-LOGGING-STANDARDIZATION-001-3 — Alert from 500, reusing the existing notifier

- **Decision:** Extend `UnhandledExceptionAction` + `SendMailS`; alert on 5xx only; deduplicate on
  `tool_name + module_section + status_code`.
- **Rationale:** The notifier already exists and needs no external component. The source design's
  `status_code >= 400` threshold would fire on legitimate traffic: 177 of the `HttpStatus` usages in MARLO's
  REST layer are `NOT_FOUND`, overwhelmingly "no record with that id" responses. Without deduplication, one
  broken page hit 500 times sends 500 mails, and the channel is muted within a day.
- **Alternatives considered:** A Loki alert rule (out of scope, `requirements.md` §4); a second in-app
  notifier (rejected — two channels for one concern).
- **Status:** Accepted.

### ADR-ENH-LOGGING-STANDARDIZATION-001-4 — `logstash-logback-encoder` pinned to 6.6

- **Decision:** Add `logstash-logback-encoder` 6.6 rather than the current 7.x.
- **Rationale:** 7.x requires logback 1.3+ and SLF4J 2.x; this checkout is logback 1.2.13 on SLF4J 1.7.x
  transitively. That same version floor is why the `log4j-slf4j2-impl` and `log4j-core` jars shipped in the
  WAR never bind and Logback is the active provider — analysis recorded in A2-2435. Jackson is already at the
  ≥2.17.x floor, so no other dependency moves and nothing is downgraded (`CLAUDE.md` rule 11).
- **Alternatives considered:** A hand-written `Encoder` with no new dependency — rejected as reimplementing a
  well-understood library, and it would put the field names in Java instead of in one XML block (NF-004).
  Upgrading logback to 1.3+ — a much larger change with its own binding implications, out of scope here.
- **Status:** Accepted.

### ADR-ENH-LOGGING-STANDARDIZATION-001-5 — Two MDC insertion points, not one

- **Decision:** `LoggingContextFilter` owns the request-scoped keys and the `MDC.clear()`; the user keys are
  written later, by `AddUserIdFilter`, `RequireUserInterceptor` and `AddSessionToRestRequestFilter`.
- **Rationale:** The Shiro subject does not exist before the Shiro filter runs, which is exactly why
  `AddUserIdFilter` exists in the chain today. A single filter would either run too early to know the user or
  too late to own the lifecycle. Reusing the three places that already read the user satisfies NF-002 with no
  additional query.
- **Status:** Accepted.

### ADR-ENH-LOGGING-STANDARDIZATION-001-6 — `detailed-design.md` §9 left unchanged

- **Decision:** Do not edit the technical blueprint; record the conventions in
  `reports/ai-context/logging-standard.md`.
- **Rationale:** §9.1 already prescribes SLF4J + Logback, a per-class logger, the level semantics and the
  `System.out`/`printStackTrace` prohibition. This spec implements that section; there is nothing to correct.
  Editing it would make the work a constitutional event under `CLAUDE.md`, requiring an epic spec and external
  review, for no change in content.
- **Status:** Accepted.

### ADR-ENH-LOGGING-STANDARDIZATION-001-7 — Flat user keys, and the text pattern left alone

- **Decision:** Emit `user_id` / `user_name` / `user_email` at the top level rather than nested under `user`,
  and do not adopt the prescribed `[Nest] ...` console line.
- **Rationale:** Both follow from the same constraint — the MDC is a flat string map and the text pattern is
  what the team reads over SSH. Nesting would require a custom `JsonProvider`, which moves the field structure
  out of the single `<providers>` block NF-004 depends on. The console line is NestJS output, `[Nest] <pid>`
  prefix included, from a framework MARLO does not run; every field it carries is already in the JSON event.
- **Alternatives considered:** *Serialising a JSON fragment into an MDC value* — rejected, the encoder escapes
  it and the result is a string, not an object. *A Logback pattern imitating the Nest line* — rejected under
  NF-001; it changes the file the deployment checklist tells the team to read, to imitate another framework.
- **Consequence:** If the group keeps the nested shape, the pre-processor maps three keys into one object —
  one change in one component.
- **Amended by ADR-9.** The cost argument no longer holds: NF-006 and SEC-001 put a MARLO-owned provider in
  the tree regardless, so nesting `user` became a small change inside a class that already exists rather than
  a new component, and "a custom provider in every tool" is no longer what the alternative costs. The
  flat-keys decision stands on the remaining ground — reshaping a cross-tool schema belongs in the
  pre-processor, once, rather than in each tool — but on one leg instead of two. §8.8 settles it.
- **Status:** Accepted, amended by ADR-9. Both are open questions for the DevOps group
  (`requirements.md` §8.7, §8.8).

### ADR-ENH-LOGGING-STANDARDIZATION-001-8 — Remove `LoggingAspect` instead of fixing its pointcut

- **Decision:** `logging/LoggingAspect.java` and its `@Bean` in `MarloRestApiConfig.java:78-81` are deleted.
  REST error logging is consolidated in `ExceptionTranslator`.
- **Rationale:** The pointcut `within(org.cgiar.ccafs.marlo.rest.*)` has matched nothing since January 2018,
  because all 386 classes live in subpackages. Repairing it makes things worse rather than better. The aspect
  observes the exception and never the response, so it cannot supply the `status_code` FN-004 requires, and
  `ExceptionTranslator:138` handles `Exception`, so no REST exception escapes the one component that does know
  the status. A working pointcut would log each REST error twice — once without the status — record every
  legitimate 404 at ERROR against §5's "ERROR means someone must intervene", wrap ~190 Spring beans
  (21 `@RestController`, 1 `@Controller`, 62 `@Named`, 105 MapStruct `@Mapper` implementations) in proxies,
  and stringify every REST argument and result, since the `@Around` guard is always true (§12).
- **Alternatives considered:** fix the pointcut and DEBUG-gate the `@Around`; fix the pointcut and add a
  class-scoped `<logger>` so the existing guard works. Both preserve a component whose only correct output
  duplicates `ExceptionTranslator`, and both keep the proxying cost.
- **Consequence:** T01 becomes a deletion, and the substance of FN-004 moves entirely into T02, which now
  carries the whole REST-error requirement rather than half of it. The stale comment at
  `ExceptionTranslator:47-48` is removed with the thing it referred to. The `logging/` package survives, holding
  the provider from ADR-9.
- **Status:** Accepted.

### ADR-ENH-LOGGING-STANDARDIZATION-001-9 — A MARLO-owned `JsonProvider` for the context fields

- **Decision:** `logging/MarloMdcJsonProvider.java` writes the MDC-sourced fields: `status_code` and `user_id`
  as JSON numbers, `user_name`/`user_email` only when `status_code >= 400`, and no `service_affected`.
- **Rationale:** Three requirements — NF-006, SEC-001 and the empty-field decision in `requirements.md` §8.3 —
  are all decisions taken at write time, and none is expressible with the encoder's stock providers. MDC is
  `Map<String,String>` and 6.6 has no typed MDC writer, so `status_code` — the field every query over the log
  filters on — would serialize as a string, and a range filter would compare text. SEC-001's condition depends
  on a value known only after the components that populate the PII have returned. One class holds all three,
  which also keeps NF-004 true.
- **Alternatives considered:** `StructuredArguments.keyValue` at each call site, which types `status_code` but
  cannot type `user_id`, a per-request field rather than a per-call one, and which would scatter the field
  names NF-004 requires in one place; upgrading to encoder 7.3 for `mdcEntryWriter`, blocked by logback 1.3;
  emitting strings and casting downstream, where the pre-processor that would cast is out of scope and does
  not exist, leaving `jq` — the baseline consumer under ADR-1 — needing a cast on the alerting field.
- **Consequence:** T08 gains one new file, and SEC-001 gains an enforcement point it did not have. A field
  rename is still one edit, now spread across the `<providers>` block and this class.
- **Status:** Accepted.

## 16. Open Risks

- **Risk: MDC leaks across pooled threads.** A missing or misplaced `MDC.clear()` attributes one user's
  `user_id` to the next request on that worker. It is a privacy defect, not a cosmetic one, and it does not
  reproduce under single-user testing.
  *Mitigation:* `clear()` in the `finally` of the outermost filter; the paired two-user verification in
  `task.md` is a required step, not an optional one.
- **Risk: removing `LoggingAspect` drops logging someone relies on.** It has advised nothing since 2018, so
  there is nothing to lose, but the claim must be demonstrated rather than asserted.
  *Mitigation:* T02 lands the replacement coverage in the same series; verify a forced REST exception produces
  exactly one event carrying its status, before and after the deletion.
- **Risk: `logstash-logback-encoder` 6.6 against Jackson 2.18.9.** The encoder was built against a 2.12-era
  Jackson and this checkout pins 2.18.9 (`marlo-parent/pom.xml:55`). An incompatibility here surfaces at
  runtime as a failing appender, not at compile time.
  *Mitigation:* T08's verification is a started application and a parsed file, not a green build. `log.json`
  (OPS-002) is the off switch if it fails on an environment, and the text log keeps working regardless
  (NF-001).
- **Risk: the cleanup changes behaviour.** Some `catch` blocks log and return `null`, and callers may depend
  on it. A well-intentioned fix can turn a silent wrong answer into an exception in production.
  *Mitigation:* the cleanup corrects the logging statement only; control-flow changes are separate tasks with
  their own verification (§6).
- **Risk: field names change after the DevOps group decides.** The names in §11 are MARLO's choice, not an
  agreement.
  *Mitigation:* NF-004 — one `<providers>` block. The consequence of being wrong is one edit.
- **Risk: the JSON log is never collected.** If no Promtail target is ever added, the file stays local.
  *Mitigation:* accepted deliberately. The file is queryable with `jq` on the machine, which is already better
  than today; nothing in this spec depends on collection happening.
- **Risk: `log.folder` still points somewhere unwritable in production.** OPS-003 fixes the two shipped
  files, but the production value lives in a gitignored `marlo-pro.properties` this repository cannot see.
  *Mitigation:* the JSON appender writes to the same directory as the existing text appender, so if one works
  the other does; and `log.json=false` is the off switch if it does not.
