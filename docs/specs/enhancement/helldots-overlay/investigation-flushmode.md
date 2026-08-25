> **Note added after review.** This note's section 4 attributes `FlushMode.MANUAL` to leaked
> thread-local state. That conclusion is **wrong** and was corrected during the final whole-branch
> review. The correct mechanism: Spring's `OpenSessionInViewFilter` (registered on `/*` in
> `web.xml`) sets `FlushMode.MANUAL`; `MARLOCustomPersistFilter` never touches flush mode; and
> `HibernateTransactionManager.doBegin()` flipping MANUAL to AUTO is what makes MARLO's other ~392
> managers work. The collision is specific to `/api/*` because a Struts request never reaches
> `MARLOCustomPersistFilter` at all. **There is no repo-wide data-loss bug.** The evidence below is
> sound and is what made the correct mechanism derivable; only the section 4 inference was mistaken.

# HellDots `comment:deleted` soft-delete bug — debug findings

Date: 2026-08-24

## Symptom

`POST /api/helldots/events` with a `comment:deleted` event returns `200`, but
`helldots_comments.is_active` never changes from `1` to `0`.

## Method

Systematic debugging per `superpowers:systematic-debugging`. Reproduced the
bug against a running instance with `show_sql=true`, added temporary
diagnostic logging (`System.out.println("DBGPROBE ...")`) at the four points
in the call chain that decide whether a write reaches the database, rebuilt,
reproduced again, captured the exact Hibernate SQL and probe output per
request by recording the log line count immediately before each `curl` call
and slicing the log from that offset, then reverted all instrumentation.

Temporary instrumentation was added to (all reverted, working tree is clean —
verified with `git status --porcelain` after revert):
- `marlo-web/.../rest/helldots/HelldotsController.java` — logged the entity's
  `isActive()` value right after `setActive(false)`.
- `marlo-data/.../dao/mysql/HelldotsCommentMySQLDAO.java` — logged the
  session identity and flush mode around `findByCommentId`.
- `marlo-data/.../dao/mysql/AbstractMarloDAO.java` — logged session identity,
  `session.contains(entity)`, and flush mode in `update(T)`.
- `marlo-web/.../web/filter/MARLOCustomPersistFilter.java` — logged session
  identity, flush mode, and `session.isDirty()` right before
  `beginTransaction()` and right before `tr.commit()`.

## Evidence

### 1. No UPDATE is ever emitted for the delete request

Fresh reproduction (row `dbg-1`, PK `id=4`, created via `comment:created`,
confirmed `is_active=1` in the DB beforehand). Log slice for the
`comment:deleted` request (log line offset recorded via `wc -l` immediately
before the `curl` call, then `sed -n "${MARK},\$p"`):

```
DBGPROBE filter begin: sessionId=124825975 flushMode=MANUAL for  [POST] .../api/helldots/events
Hibernate: select user0_... from users user0_ where user0_.id=?
DBGPROBE findByCommentId(): sessionId=124825975 flushModeBefore=MANUAL
Hibernate: select helldotsco0_... from helldots_comments helldotsco0_ where helldotsco0_.comment_id=? and helldotsco0_.is_active=1
DBGPROBE findByCommentId(): resultEntityId=1749675370 flushModeAfter=MANUAL
DBGPROBE softDelete(): entityId=1749675370 isActiveAfterSet=false commentPk=4
DBGPROBE update(): sessionId=124825975 entityId=1749675370 entityClass=org.cgiar.ccafs.marlo.data.model.HelldotsComment sessionContains=true flushMode=MANUAL sessionOpen=true txActive=true
DBGPROBE update(): early-return branch taken, relying on dirty checking at flush/commit
DBGPROBE filter pre-commit: sessionId=124825975 flushMode=MANUAL isDirty=true for  [POST] .../api/helldots/events
MARLOCustomPersistFilter [DEBUG]: clean up AuditLogHelper ...
```

There is **no `Hibernate: update helldots_comments ...` line anywhere in this
slice.** The request returned `200`. The row was checked in the database
immediately after:

```
comment_id  is_active  status  created_by  modified_by  active_since
dbg-1       1          open    1082        1082         2026-08-24 19:17:30
```

Unchanged from creation. No UPDATE reached the database.

### 2. The entity IS correctly loaded, mutated, and dirty-checked

- `resultEntityId=1749675370` (the object `findByCommentId` returned) is the
  exact same identity later seen in `softDelete()` and in `update()` —
  confirming `comment.setActive(false)` really did mutate the managed
  instance, not a detached copy.
- `isActiveAfterSet=false` — confirms the mutation stuck.
- `sessionContains=true` in `update()` — confirms the object is the same
  managed instance in the persistence context, so `AbstractMarloDAO.update()`
  takes the early-return branch (`if (session.contains(entity)) { return
  entity; }`, no `merge()` call).
- **`isDirty=true` at pre-commit** — this is the crucial data point.
  Hibernate's own dirty-checking machinery correctly detected the field
  change. The entity is not lost, not detached, not a different instance.
  The change was fully tracked in the persistence context.

### 3. Root cause: `session.getHibernateFlushMode()` is `MANUAL` for the entire request

`flushMode=MANUAL` is observed at every single probe point: at filter entry
(before `beginTransaction()` even runs, before any MARLO code touches the
session), before and after `findByCommentId()`, inside `update()`, and at
`tr.commit()` time.

Under Hibernate's `FlushMode.MANUAL`, `Session.flush()` is **only** invoked
when the application calls it explicitly. Unlike `AUTO` or `COMMIT`,
`MANUAL` disables the auto-flush that `Transaction.commit()` would otherwise
perform for a dirty, managed entity. So at `tr.commit()`
(`MARLOCustomPersistFilter.java` line ~102), Hibernate correctly sees
`isDirty=true` but is instructed not to flush automatically — the pending
`UPDATE` for `helldots_comments` is silently discarded when the transaction
commits and the persistence context is cleared.

This exactly answers the two questions posed:
- **Does an UPDATE reach the database?** No — none is emitted at all (not
  "with the wrong value"; it never fires).
- **Why doesn't the dirty-checking flush fire?** Because the session's flush
  mode is `MANUAL`, and `MANUAL` mode makes `Transaction.commit()` skip the
  automatic flush that `AUTO`/`COMMIT` would perform. `AbstractMarloDAO.update()`
  never calls `session.flush()` itself, and `MARLOCustomPersistFilter` never
  resets or forces the flush mode before committing — it just calls
  `tr.commit()` and trusts Hibernate's default auto-flush behavior, which
  does not apply here.

### 4. Why is the session's flush mode `MANUAL` in the first place?

This is secondary to the fix but explains the mechanism. The app is
configured with `LocalSessionFactoryBean` (`MarloDatabaseConfiguration.java`)
and never sets `hibernate.current_session_context_class` explicitly, so
Spring defaults `sessionFactory.getCurrentSession()` to
`org.springframework.orm.hibernate5.SpringSessionContext` (verified by
decompiling `spring-orm-5.3.39.jar`,
`org/springframework/orm/hibernate5/SpringSessionContext.class`).

Decompiled bytecode of `SpringSessionContext.currentSession()` shows: when no
`Session`/`SessionHolder` is yet bound to
`TransactionSynchronizationManager` for this thread but synchronization
*is* active, Spring opens a new `Session` and, **if the ambient Spring
transaction is read-only** (`TransactionSynchronizationManager.isCurrentTransactionReadOnly()`),
explicitly sets `FlushMode.MANUAL` on that new session before binding it.
`MARLOCustomPersistFilter` is unaware of any of this: it calls
`sessionFactory.getCurrentSession().beginTransaction()` using raw Hibernate
transaction APIs, on whatever `Session` `SpringSessionContext` hands back,
and never inspects or resets its flush mode. Since `MARLOCustomPersistFilter`
sits early in the filter chain (registered before Shiro, before Struts,
before the Spring MVC dispatch that runs `HelldotsController`), nothing in
*this* request's own processing could have started that read-only
synchronized transaction before the filter's very first line — the `MANUAL`
flush mode was already present on the very first probe, before
`beginTransaction()` executed. The most consistent explanation is that the
Tomcat worker thread previously served a request that went through some
`@Transactional(readOnly = true)` Spring-managed call, which established
(and evidently did not fully unwind) a read-only-tainted
`TransactionSynchronizationManager` binding for this `SessionFactory` on
that thread, so the next unrelated request picks up a stale, read-only-flavored
session/flush-mode from the leftover synchronization state. This deserves a
dedicated, separate investigation (see "Broader impact" below) but is not
required to fix the immediate symptom: the write path must not depend on
ambient flush-mode assumptions regardless of how they arise.

### 5. `upsert` is affected by the identical root cause (confirmed by test)

Per the request, tested whether `upsert()` — which also calls
`findByCommentId()` to load an existing row, mutates the managed instance in
place, then calls `helldotsCommentManager.save(comment)` — has the same
defect. Sent a `comment:status-changed` event for the existing `dbg-1`
row (`status: "open"` → `status: "resolved"`):

Log slice for that request:
```
DBGPROBE filter begin: sessionId=1559523644 flushMode=MANUAL ...
Hibernate: select user0_... 
DBGPROBE findByCommentId(): sessionId=1559523644 flushModeBefore=MANUAL
Hibernate: select helldotsco0_... where ... comment_id=? and is_active=1
DBGPROBE findByCommentId(): resultEntityId=316220989 flushModeAfter=MANUAL
DBGPROBE update(): sessionId=1559523644 entityId=316220989 ... sessionContains=true flushMode=MANUAL ...
DBGPROBE update(): early-return branch taken, relying on dirty checking at flush/commit
DBGPROBE filter pre-commit: sessionId=1559523644 flushMode=MANUAL isDirty=true ...
```

Again: no `Hibernate: update helldots_comments ...` line at all. Database
check afterward:

```
comment_id  id  is_active  status  payload
dbg-1       4   1          open    {"...","status": "open",...}
```

Both the `status` column **and** the `payload` column (which is
unconditionally reassigned on every `upsert()` call, `comment.setPayload(serialized)`)
are still the values from creation. The new `status: "resolved"` never
landed anywhere.

**Conclusion: this is not a soft-delete-specific bug.** It is a general
defect: any mutation made to an already-loaded `HelldotsComment` entity and
routed through `AbstractMarloDAO.update()`'s early-return branch is silently
dropped, whenever the current session's flush mode is `MANUAL` at commit
time. Both HellDots write paths that touch an *existing* row (`softDelete`
and `upsert`-on-existing-comment) are broken identically. Only the
*create*-a-new-comment path works, and only because `session.save()` with an
`IDENTITY` id generator (`HelldotsComments.hbm.xml`: `<generator
class="identity" />`) executes the `INSERT` **immediately**, independent of
flush mode — that is a Hibernate requirement for `IDENTITY` generators (the
generated key must be available right after `save()` returns), not a
property of the write path being "more correct."

## Recommended fix

**Minimal, targeted fix:** in `AbstractMarloDAO.update(T entity)`
(`marlo-data/src/main/java/org/cgiar/ccafs/marlo/data/dao/mysql/AbstractMarloDAO.java`,
lines ~528–544), call `session.flush()` explicitly before returning from the
already-managed (`session.contains(entity)`) branch:

```java
protected T update(T entity) {
  ensureAuditLogContext();
  if (entity == null) {
    return null;
  }

  Session session = sessionFactory.getCurrentSession();
  if (session.contains(entity)) {
    session.flush();
    return entity;
  }

  entity = (T) session.merge(entity);
  session.flush();
  return entity;
}
```

(Apply the same pattern to the other three `update(...)` overloads in the
same file, which have the identical early-return shape.)

Why this one over the alternatives considered:

1. **Reset the session's flush mode to `AUTO` in `MARLOCustomPersistFilter`**
   (either right after acquiring the session, or right before
   `tr.commit()`). This would also fix the bug (a proper auto-flush would
   fire at commit), and is worth doing as a defensive, complementary change,
   but it is a *request-wide* policy change affecting every DAO and every
   entity touched by any code running through this filter — i.e. all of
   `/api/*` plus anything else mapped to `NON_STATIC_RESOURCE_REQUESTS`, not
   just HellDots. That is a much larger blast radius than the reported bug
   warrants and risks surfacing unrelated flush timing regressions
   elsewhere in the app. It also doesn't explain or guard against the
   flush mode being clobbered again mid-request by another
   `SpringSessionContext`-mediated session acquisition. Good candidate for
   a *separate*, deliberate hardening change — not the fix for this ticket.

2. **Call `session.update(entity)` explicitly instead of relying on
   dirty-checking.** Rejected: the entity is already associated with the
   persistence context under the same identifier (that's exactly what
   `session.contains(entity)` is testing), so calling `session.update()` or
   `session.merge()` again on it is redundant at best; `session.update()` on
   an already-managed instance throws
   `NonUniqueObjectException`/`PersistentObjectException` in classic
   Hibernate semantics for a *different* object with the same id, and is
   simply unnecessary work for the *same* object reference. This is
   precisely why the existing code special-cased `session.contains()` to
   skip `merge()` — that part of the logic is correct; it's just missing
   the flush.

3. **Remove the `session.contains()` early return and always call
   `session.merge(entity)`.** This does not fix the bug by itself: `merge()`
   only reconciles state into the persistence context — it still defers the
   actual `UPDATE` to the next flush, so under `FlushMode.MANUAL` it would
   still silently vanish at commit. It would need `session.flush()` added
   too, at which point the `session.contains()` short-circuit becomes a
   pure (harmless) performance optimization, not a correctness fix — so
   there's no reason to remove it.

4. **Find and fix why the session ends up with `FlushMode.MANUAL` in the
   first place** (the ambient read-only `TransactionSynchronizationManager`
   state described in section 4). This is the truest, deepest root cause,
   but it lives in shared session/transaction plumbing used by every request
   through `MARLOCustomPersistFilter`, not something specific to HellDots.
   Understanding and fixing it correctly needs a dedicated investigation
   (why is synchronization/read-only state leaking across pooled Tomcat
   threads between unrelated requests?) and carries real regression risk if
   changed hastily. Recommend opening this as a separate, explicitly-scoped
   follow-up rather than folding it into the HellDots bugfix.

## Broader impact

`AbstractMarloDAO.update(T)` (and its three overloads) is the shared
save-or-update primitive used by many `ManagerImpl`/DAO pairs across MARLO,
not just HellDots. Any other write path that (a) loads an entity in the same
session via a `find`/HQL query, (b) mutates it in place, and (c) calls
`manager.save(...)` → DAO `update(...)`, is subject to the exact same
silent-no-op failure mode whenever the current session's flush mode happens
to be `MANUAL` at commit time (which — per section 4 — appears to be a
function of ambient, cross-request Spring transaction-synchronization state
rather than anything HellDots-specific). This was outside the assigned scope
to chase further here, but it is worth flagging for a wider audit: **any
"update an existing row" call anywhere in MARLO could be silently failing
under the same conditions**, which would be a serious, hard-to-notice class
of data-loss bug. Recommend a follow-up epic/spec to (a) audit
`ManagerImpl`/DAO update paths for reliance on implicit dirty-checking flush,
and (b) resolve why sessions handed out by `MARLOCustomPersistFilter` can
carry `FlushMode.MANUAL`.

## Cleanup performed

- All temporary `DBGPROBE` logging reverted via `git checkout --` on the four
  touched files; `git status --porcelain` confirms a clean working tree.
- Rebuilt and restarted the app from the clean (non-instrumented) source.
- `dbg-%` test rows deleted from `helldots_comments` after the investigation.
- No commits made.
