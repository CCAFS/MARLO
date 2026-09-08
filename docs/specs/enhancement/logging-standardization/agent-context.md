# Logging Standardization — Agent Context

**Spec:** ENH-LOGGING-STANDARDIZATION-001
**Last Updated:** 2026-09-07
**Read this first.** Open `requirements.md` / `design.md` / `task.md` only for broad, architectural or
formally tracked work.

---

## 1. The One Thing To Know First

MARLO logs through **SLF4J + Logback** (`marlo-web/src/main/resources/logback.xml`). The `log4j-api`,
`log4j-core` and `log4j-slf4j2-impl` jars are also in the WAR and **never bind** — `log4j-slf4j2-impl` needs
SLF4J 2.x and this checkout resolves SLF4J 1.7.x through logback-classic 1.2.13. There is no `log4j2.xml`.
Do not "fix" the log by editing a Log4j2 config; there isn't one, and Logback is the active provider.

Second thing: **`org.jfree.util.Log` is used as a logger in 21 files** and no `LogTarget` is ever registered,
so every one of those calls goes nowhere. If a class seems to log and nothing appears, check its import first.

## 2. The Schema, In One Table

One JSON object per line, written by the `FILE-JSON` appender to
`${log.folder}/marlo-json-${log.instance}.log`, alongside the unchanged text log.

| Field | Type | Where it comes from | When |
|---|---|---|---|
| `timestamp` | str | Logback | always, ISO 8601 UTC, ms |
| `level` | str | call site | always |
| `request_id` | str | `LoggingContextFilter`, generated | every event inside a request |
| `environment` | str | `APConfig.getEnvironment()` ← Spring active profile | always |
| `tool_name` | str | `BaseAction.getCurrentCrp()` / `SESSION_CRP`; REST: `AddSessionToRestRequestFilter.addCrpToSession()` | once the global unit is known |
| `module_section` | str | logger name (the emitting class) | always |
| `controller_affected` | str | request path **without query string**, `LoggingContextFilter` | every event inside a request |
| `message` | str | call site | always |
| `status_code` | **num** | `ExceptionTranslator` per handler; 500 from `UnhandledExceptionAction` | error events only |
| `user_id` | **num** | `AddUserIdFilter`, Shiro principal | authenticated requests |
| `user_name`, `user_email` | str | `RequireUserInterceptor`; REST: `AddSessionToRestRequestFilter` | **only** when `status_code >= 400` |
| `stack_trace` | str | Logback throwable provider, truncated | when an exception is passed |
| `payload` | obj | call site, allow-list only | rarely, explicitly |
| `service_affected` | — | — | **omitted**; no meaning for a monolith |

`user_id` / `user_name` / `user_email` are three flat keys, not a nested `user` object — the MDC is a flat
string map. Field names live in **one** `<providers>` block in `logback.xml`. The cross-tool naming is not settled
(`tool`/`module` vs `tool_name`/`module_section` in the source agreement), so a rename must stay a single edit.

**The two numeric fields are not free, and that flat string map is why.** The pinned encoder (6.6, because 7.x
needs logback 1.3) has no typed-MDC writer, so anything read straight from MDC serializes as `"404"` — and
`status_code` is the field every query over the log filters on, where `"404" >= "500"` compares text.
`logging/MarloMdcJsonProvider.java` exists for exactly that, and it is also the **only** place that can
enforce SEC-001: `user_name`/`user_email` enter the
context when the request starts and `status_code` is known when it ends, so "only on errors" can only be
applied when the line is written. The same class omits `service_affected`. If you are changing any of those
three behaviours, that file is the one to open — not `logback.xml`, not the filters.

## 3. Where The Context Comes From, And Why It's Split

Two insertion points, because the Shiro subject does not exist before the Shiro filter runs. That is also why
`AddUserIdFilter` already exists in the chain.

```
RemoveSessionFromUrlFilter → CORSFilter
  → LoggingContextFilter   [owns MDC: request_id, environment, tool_name, controller_affected; clear() in finally]
    → MARLOCustomPersistFilter   (AuditLogContext push/pop + tx — the pattern this copies)
    → Shiro filter
    → AddUserIdFilter      [user_id — subject available only from here on]
      → Struts: RequireUserInterceptor  [user_name/user_email from session]
      → REST:   AddSessionToRestRequestFilter  [user_name/user_email + tool_name]
```

Nothing here issues a database query. Every value already exists in the request: the Shiro principal
`AddUserIdFilter` reads, the `User` in the Struts session, the `User` the REST filter already resolves through
`UserManager.getUser(id)`. **Keep it that way** — adding a lookup puts a query on every request.

## 4. The Trap: MDC Leaks Across Pooled Threads

Tomcat reuses worker threads. An MDC left populated attributes one user's `user_id` to the next request served
by that thread. It is a privacy defect and **it does not reproduce under single-user testing.**

- `MDC.clear()` belongs in the `finally` of `LoggingContextFilter`, the outermost MARLO filter.
- Any new `MDC.put` must be inside a scope that filter already covers.
- Verify by driving the app as two different users in sequence and grepping the JSON log for crossover.

## 5. Conventions For Any Log Statement

From A2-2435, now enforced by Checkstyle:

- `private static final Logger LOG = LoggerFactory.getLogger(MyClass.class);` — the repo has 8 declaration
  variants and 3 names (`LOG` 253, `logger` 59, `log` 5). Use `LOG`, `static final`.
- `LOG.error("saveX() > Could not save deliverable {}", id, e)` — placeholders, exception **last**. Never
  concatenate `e.getMessage()`: 135 call sites do, and they lose the originating line.
- Never `printStackTrace()` or `System.out.println()`. They write to `System.err`, bypass Logback entirely, and
  are lost on the server.
- Never swallow an exception and return `null`. Either propagate, or return an explicit empty result and stop
  calling it an ERROR.
- **ERROR means someone must intervene.** A failed login is INFO.
- Log where you handle, throw where you detect. Not both for the same event.

## 6. Known Gaps And Traps

- **`LoggingAspect` advised nothing for years, and is now deleted.** Its pointcut was
  `within(org.cgiar.ccafs.marlo.rest.*)`, and no `.java` file lives directly in that package — all 386 are in
  subpackages, which `pkg.*` does not match. It was **not** repaired: the aspect never sees the response, so
  it cannot carry `status_code`, and `ExceptionTranslator:138` handles `Exception` and already receives every
  REST exception. A working pointcut would double-log every REST error, record every legitimate 404 at ERROR,
  proxy ~190 beans, and stringify every argument and result — the `@Around` guard reads `isDebugEnabled()`,
  which is always true because `logback.xml:114` is `<root level="ALL">` and nothing scopes
  `org.cgiar.ccafs.marlo`. If you are looking for the REST-side logging hook, it is `ExceptionTranslator`.
- **`ExceptionTranslator` had 15 handlers and 1 log call.** REST errors were returned to clients with no
  server-side record. It is also the only place in the codebase where the HTTP status is known, so it is the
  only place `status_code` can be populated.
- **`log.folder` shipped pointing at developer home directories** (`marlo-dev.properties:99`,
  `marlo-test.properties:139`). Production values live in a gitignored `marlo-pro.properties` this repo cannot
  see.
- **`APConfig` has no profile accessor.** `isProduction()` and `getBaseUrl()` exist; `getEnvironment()` is new
  and derives from `spring.profiles.active` precisely so no new server-side key is needed.
- **`mvn checkstyle:check` cannot run here.** Plugin 2.9.1 against checkstyle 8.18 throws
  `NoSuchMethodError: Checker.setClassloader`. Use
  `~/.claude/skills/marlo-verify/scripts/checkstyle.sh --baseline`. Separate ticket.
- **Some `.java` files use CRLF.** A scripted whole-file rewrite flips them to LF and buries the real diff.
  Edit lines, not files.
- **`UserMySQLDAO:171` concatenates email and password into the HQL query.** Out of scope here (recorded in
  A2-2435) — do not obscure it while cleaning that file.
- **`action/summaries` (168 offenders) and `utils` (84) are deliberately deferred.** Largest concentration,
  its own commit series.

## 7. Change Recipes

**Adding a log statement anywhere:** follow §5. Nothing else — the context fields are already in MDC and the
appender picks them up.

**Adding a new field to the schema:** put it in the `<providers>` block in `logback.xml` and, if it is
per-request, populate it in `LoggingContextFilter`. Check §4 before adding any `MDC.put`. If the field must be
a number, must be conditional on another field, or must be omitted when empty, it goes through
`MarloMdcJsonProvider` instead — `logback.xml` alone cannot express any of those.

**Adding a REST error handler:** log it in `ExceptionTranslator` with the status its `@ResponseStatus`
declares — 5xx at ERROR, 4xx at WARN or INFO. Do not change the `ErrorDTO` shape; clients depend on it.

**Changing what alerts:** `UnhandledExceptionAction` + `SendMailS`. Alerts fire from **500**, not 400 — 177 of
the `HttpStatus` usages in the REST layer are `NOT_FOUND`, overwhelmingly legitimate. Deduplication is keyed
on `tool_name + module_section + status_code`; adding a channel means changing that key, not adding a notifier.

**Silencing framework noise:** add a logger-scoped `<logger>` entry in `logback.xml`, as A2-2435 Part 1 did for
the two `net.sf.ehcache.pool.sizeof.*` classes (31,983 lines, 37% of a dev log). Logger-scoped entries apply to
every appender, so never duplicate them per appender.

## 8. What Already Exists That This Must Not Duplicate

- **`AuditLogContextProvider` / `AuditLogContext`** — the existing per-request propagation primitive, pushed
  and popped at `MARLOCustomPersistFilter:109,164` and `RequireUserInterceptor:52,69`. It feeds the audit
  tables, **not** the log. Copy its push/pop shape; do not extend it to carry log fields, and do not add a
  third context object.
- **`UnhandledExceptionAction` + `SendMailS`** — the alerting path. Already emails the support team with user,
  CRP, phase and `actionName`. Extend it; never add a parallel notifier.
- **`ExceptionTranslator`** — the REST-side hook, and the only component that knows the HTTP status. Add
  handlers and log there rather than adding per-controller try/catch logging. `LoggingAspect` used to be
  described as this hook; it never worked and has been deleted (§6).
- **The framework logger levels at `logback.xml:57-146`** — Struts, Shiro, Hibernate, Tomcat, Spring, ehcache
  are already tuned. Check there before concluding something "doesn't log".

## 9. Boundaries — What This Spec Does *Not* Cover

The queue and the Pre-Processor from the cross-tool agreement; any Promtail target, scrape config or agent
(A2-1470 left one on `marlotest` only, tailing `catalina.out`; production has none); Loki retention, Grafana
dashboards and Loki-side alert rules; the cross-tool field-name and mandatory-field agreement; the status-code
alert control list; `detailed-design.md` §9, which already prescribes what this implements; APM and metrics.
Also not adopted: the source document's prescribed `[Nest] ...` console error line — it is NestJS output, the
text pattern stays as it is (NF-001), and the JSON event carries the same fields.

See `requirements.md` §4 for why each is out, and §8 for the questions still owned by the DevOps group.

## 10. Verification Log

Recorded as tasks complete. Every entry states what was run and what it printed — see `task.md` §8.
