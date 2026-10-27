# Logging Standard

## Goal
Let a developer write a correctly shaped log statement, and change the log context, without opening the spec. Source of truth: `docs/specs/enhancement/logging-standardization/` (ENH-LOGGING-STANDARDIZATION-001); where this file and the code disagree, the code wins and this file is stale.

## Quick Path: Adding A Log Statement
1. Declare `private static final Logger LOG = LoggerFactory.getLogger(MyClass.class);` (SLF4J, `org.slf4j`). Never `org.jfree.util.Log`: no `LogTarget` is registered, so every call goes nowhere.
2. Write a constant message with `{}` placeholders and pass the exception **last**: `LOG.error("saveX() > Could not save deliverable {}", id, e);` (FN-006: the stack trace must survive; `e.getMessage()` concatenation loses it).
3. Pick the level from the table below.
4. Do not touch the MDC. `request_id`, `tool_name`, `user_id`, `controller_affected` and `status_code` are already in the context and the appenders print them.
5. If the failure has a known HTTP status, call `LogContext.putStatusCode(status)` **before** the error line (see "Status code").

## Level Semantics
| Level | Use | Source |
|---|---|---|
| `ERROR` | A failure someone must act on; carries the stack trace | TRD §9.1; `ExceptionTranslator.logHandledError` (5xx) |
| `WARN` | Recoverable anomaly; a 4xx the client caused; a 403 for a logged-in user | TRD §9.1; `ExceptionTranslator.logHandledError`; `ErrorStatusLogInterceptor.logErrorStatus` |
| `INFO` | High-level flow; routine outcomes (401, 404, a failed login) | TRD §9.1; same two methods |
| `DEBUG` | Detail nobody should see in production; 429 throttling | `ExceptionTranslator.logHandledError` |

Production appenders (`FILE-PRODUCTION`, `CONSOLE-PRODUCTION`, and `FILE-JSON` when `log.production=true`) drop everything below INFO. Root is `ALL`; framework noise is scoped per `<logger>` in `logback.xml`.

## Statement Rules (each with its source)
| Rule | Source |
|---|---|
| Exception is the last argument, never `e.getMessage()` concatenated | FN-006 (`requirements.md` §3) |
| No `System.out`, `System.err`, `printStackTrace()` on the save chain, authentication path, filters, REST layer | FN-009; not yet a Checkstyle rule (T14 is open): nothing stops a new one, so a reviewer must |
| Name a user by id in message text, never by email or name | `design.md` §13; email and name are fields that SEC-001 publishes on failed requests only, and text bypasses that rule |
| Never write a credential, or a login form value before the account is verified | SEC-004 |
| A value that came from the request is not written into the log text of a 4xx; log the exception class instead | `ExceptionTranslator.logHandledError` javadoc (the message "can echo back the values the client sent") |
| If a 5xx exception message may echo the request, log `LogContext.withoutLineBreaks(ex)` | `ExceptionTranslator.logHandledError`; `LogContext.withoutLineBreaks` |
| No method call on a possibly-null receiver in log arguments (`x.getId()` can throw out of the catch it sits in): prove the receiver non-null (early return, `instanceof`, ternary) or guard it. A bare variable is safe (slf4j prints `null`), so do not wrap plain arguments in null checks. Calls on the caught throwable (`e.getMessage()`) and on static utilities are not flagged, so do not over-guard them | `.claude/skills/marlo-verify/SKILL.md` Gate `LOG-NULL` (REVIEW, changed lines), checked by `.claude/skills/marlo-verify/scripts/java-hygiene-check.sh` |
| Log where you handle, throw where you detect; not both for one event | `agent-context.md` §5 of the spec |
| Logging must never fail the request: a logging helper catches `RuntimeException` and logs at DEBUG | NF-005; every `LogContext` method |

## The Schema (JSON log)
One JSON object per line in `${log.folder}/marlo-json-${log.instance}.log`, written by appender `FILE-JSON`, declared only when `log.json=true` (`marlo-dev.properties`, `marlo-test.properties`; production keeps its own gitignored file). The text log keeps its pattern (NF-001) and carries the same context as `key=value` pairs.

| Field | Type | Populated by | When |
|---|---|---|---|
| `timestamp` | string | Logback `<timestamp>`, UTC | always |
| `level` | string | call site | always |
| `request_id` | string | `LogContext.requestInitialized` (16 hex chars) | every event inside a request |
| `environment` | string | `LogEnvironmentDefiner` via `<define>` in `logback.xml`; `marlo.environment` overrides the Spring profile | always (`UNKNOWN` if the definer failed) |
| `tool_name` | string | `LogContext.putGlobalUnit`: Struts from `RequireUserInterceptor`; REST from `AddSessionToRestRequestFilter` | once the Global Unit is known |
| `module_section` | string | logger name (the emitting class) | always |
| `controller_affected` | string | `LogContext.putRoute`, from `getRequestURI()`: no query string, `;` path parameters dropped, max 200 chars | every event inside a request |
| `message` | string | call site | always |
| `status_code` | **number** | `LogContext.putStatusCode` (see "Status code") | error events whose status is known |
| `user_id` | **number** | `LogContext.putUserId`: `RequireUserInterceptor` (Struts), `AddSessionToRestRequestFilter` (REST, Shiro principal) | authenticated requests |
| `user_name`, `user_email` | string | `LogContext.putUserName(user)` (same two callers) | **only** when `status_code >= 400` (SEC-001) |
| `stack_trace` | string | Logback `ShortenedThrowableConverter`: 30 frames per throwable, 8192 chars, root cause first | when an exception is passed |
| `payload` | object | no call site writes it today | reserved; allow-list only (SEC-002): ids, section, phase |
| `service_affected` | none | omitted on purpose, meaningless for a monolith | never |

All field names live in one `<providers>` block of `FILE-JSON` in `logback.xml` (NF-004). `user_id`, `user_name`, `user_email` are three flat keys, not a nested `user` object (design ADR-7).

## MDC Contract
- **Keys:** `LogContext` constants `REQUEST_ID`, `TOOL_NAME`, `USER_ID`, `CONTROLLER_AFFECTED`, `STATUS_CODE`, `USER_NAME`, `USER_EMAIL`. Values are strings, because the MDC is a string map.
- **Write only through `LogContext`.** Its `put*` methods validate and sanitize (Global Unit acronym pattern, route percent-encoding, name and email filters that stop a value splitting a line) and swallow failures. Reading goes through `LogContext.get(key)`, which never throws.
- **`LogContext` is the request listener** (`@WebListener`, `ServletRequestListener`): `requestInitialized` calls `clear()` then sets `request_id` and the route; `requestDestroyed` calls `clear()`. There is no `LoggingContextFilter` (T05 shipped it as `LogContext` instead).
- **Typing and conditions are applied when the line is written, not when the context is filled.** The pinned encoder (`logstash-logback-encoder` 6.6, design ADR-4) cannot write typed MDC values, so `logging/MarloMdcJsonProvider` writes `status_code` and `user_id` as numbers, gates `user_name`/`user_email` on `status_code >= 400`, and omits `service_affected`. The three string fields (`request_id`, `tool_name`, `controller_affected`) go through the stock `<mdc>` provider with an explicit `includeMdcKeyName` list. Encoder: `LoggingEventCompositeJsonEncoder`. Never `LogstashEncoder`: it writes every MDC entry and would publish `user_name`/`user_email` on every line (design §11, corrected 2026-10-27).
- **A new field** that must be a number, conditional, or omitted when empty goes through `MarloMdcJsonProvider`; a plain string goes in the `<mdc>` key list plus a `LogContext` constant and `put*` method, and must be removed in `LogContext.clear()`.
- **No database query to fill it** (NF-002): use values the request already holds (the Shiro principal, the session `User`, the Global Unit already resolved).

### Status code
`LogContext.putStatusCode(int)` accepts 100-599 and, at 400 or more, publishes the pending name and email into the MDC. Call it only where the status is certain, right before the error line, because Struts renders 401/403/404 and unhandled-exception pages with HTTP 200 (the response cannot be read back).

| Path | Who sets it |
|---|---|
| Struts 401/403/404 | `ErrorStatusLogInterceptor` (from the result code; must be first in every stack: registered in `struts.xml` and `struts-json.xml`) |
| Struts unhandled exception | `UnhandledExceptionAction.execute()`: 500; a `ClientAbortException` calls `LogContext.removeStatusCode()` and logs WARN, no status, no email |
| REST | `ExceptionTranslator`: `logHandledError(status, ex)` per handler, and the 401 `AuthorizationException` handler directly |

## The Trap: Pooled Threads Keep The MDC
Tomcat reuses worker threads, so a value left in the MDC is attributed to the next request on that thread (a privacy defect that does not reproduce single-user). The control is `LogContext.clear()` at request start and end.
- `clear()` removes **only the seven keys above** and the pending name/email thread-locals. An `MDC.put` of any other key, or any `MDC.put` outside a request (a scheduled job, an executor thread), is never cleaned. Do not call `MDC.put` directly.
- The listener is found by annotation scanning. It announces itself at startup (`Log context listener registered ...`); if that INFO line is missing from the startup log, scanning is off and nothing clears the context.
- Verify by driving the app as two users in sequence on one thread and grepping the JSON log for crossover (`task.md` T05).

## What Already Exists That This Must Not Duplicate
- **`AuditLogContextProvider` / `AuditLogContext`** (`utils/`): the existing per-request push/pop primitive, used by `MARLOCustomPersistFilter` and `RequireUserInterceptor`. It feeds the audit tables, not the log. Do not extend it to carry log fields and do not add a third context object; `LogContext` is the log's.
- **`UnhandledExceptionAction` + `SendMailS`**: the alerting path (Struts). REST 5xx uses the same `SendMailS` and throttle through `ExceptionTranslator.notifySupport`. Alerts fire from 500, never from 4xx (design ADR-3). Repeats are decided by `logging/ErrorNotificationThrottle` (key: tool_name, route, exception class, throwing frame, status code; one hour; suppressed count reported in the next mail). Mail bodies come from `logging/ExceptionEmail`. Extend these; never add a second notifier or a second throttle.
- **`ExceptionTranslator`** (`rest/errors/`): the REST logging hook and the only place the REST status is known. Add a handler there and route it through `logHandledError`; do not add per-controller try/catch logging. Do not change the `ErrorDTO` shape.
- **`LoggingAspect` no longer exists.** Deleted in T01 (FN-005, design ADR-8). Its pointcut `within(org.cgiar.ccafs.marlo.rest.*)` matched no class (all REST classes are in subpackages), it never saw the response so it could not carry `status_code`, and a working pointcut would have double-logged every REST error and recorded every legitimate 404 as an ERROR. Do not re-create it.
- **Framework logger levels in `logback.xml`** (Struts, Shiro, Hibernate, Spring, ehcache...) are logger-scoped and apply to every appender, `FILE-JSON` included. Silence framework noise with another `<logger>` entry, never per appender.

## Known Limits
- `payload` has no producer yet; `SEC-002` governs the first one.
- The Checkstyle guardrail for `System.out`/`printStackTrace()` (OPS-004, T14) was not done when this file was written, and `mvn checkstyle:check` cannot run in this checkout.
- Field naming against the cross-tool agreement (`tool`/`module` vs `tool_name`/`module_section`) is an open question owned by the DevOps group (`requirements.md` §8); keep any rename a single edit in the `<providers>` block.

## Source References
- `marlo-web/src/main/resources/logback.xml`
- `marlo-web/src/main/java/org/cgiar/ccafs/marlo/logging/LogContext.java`
- `marlo-web/src/main/java/org/cgiar/ccafs/marlo/logging/MarloMdcJsonProvider.java`
- `marlo-web/src/main/java/org/cgiar/ccafs/marlo/logging/LogEnvironmentDefiner.java`
- `marlo-web/src/main/java/org/cgiar/ccafs/marlo/logging/ErrorNotificationThrottle.java`
- `marlo-web/src/main/java/org/cgiar/ccafs/marlo/logging/ExceptionEmail.java`
- `marlo-web/src/main/java/org/cgiar/ccafs/marlo/interceptor/ErrorStatusLogInterceptor.java`
- `marlo-web/src/main/java/org/cgiar/ccafs/marlo/interceptor/RequireUserInterceptor.java`
- `marlo-web/src/main/java/org/cgiar/ccafs/marlo/web/filter/AddSessionToRestRequestFilter.java`
- `marlo-web/src/main/java/org/cgiar/ccafs/marlo/action/UnhandledExceptionAction.java`
- `marlo-web/src/main/java/org/cgiar/ccafs/marlo/rest/errors/ExceptionTranslator.java`
- `marlo-web/src/main/java/org/cgiar/ccafs/marlo/utils/AuditLogContextProvider.java`
- `marlo-web/src/main/java/org/cgiar/ccafs/marlo/utils/SendMailS.java`
- `docs/specs/enhancement/logging-standardization/` (`agent-context.md`, `requirements.md`, `design.md` §11, §13, §15)
