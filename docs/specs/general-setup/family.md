# MARLO — Spec Family Manifest Template

A **spec family** is a parent spec folder plus the child spec folders produced when its scope was
chunked. `family.md` is the manifest that tracks build order, dependencies, and status across those
children.

**When this file is authored:** `/akili-propose` or `/akili-specify` writes one `family.md` — at the
*parent* folder — **only when a proposal is actually split into children**. Its absence means the
spec is flat and carries zero added obligations. Never scaffold an empty `family.md` "just in case".

**Where it lives:** `docs/specs/<taxonomy>/<family>/family.md`, alongside the parent spec's own
`requirements.md` / `design.md` / `task.md`.

---

## Front matter (top of every `family.md`)

```markdown
# <Family Name> — Spec Family Manifest

| Field | Value |
|---|---|
| Parent spec path | `docs/specs/<taxonomy>/<family>` |
| Date created | YYYY-MM-DD |
| Last updated | YYYY-MM-DD |
| Spec-family status | `open` \| `complete` |
| Owner | <IBD / PMU / QA lead> |
```

`Spec-family status` is `complete` only when every child row is `done`.

---

## Child table

One row per child spec. This table is the **exhaustive** child set of the family.

| Column | Values | Meaning |
|---|---|---|
| `#` | `1..n` | Build order |
| `Spec Path` | `<family>/<child>` | Must correspond to a real folder on disk |
| `Depends on` | spec path(s) \| `none` | Serial ordering constraint |
| `Parallel-safe` | `yes` / `no` | Fleet eligibility — `no` when the child writes files another child also writes |
| `Status` | `pending` / `active` / `done` / `blocked` | Small vocabulary on purpose |

`Status` stays deliberately coarse. Phase-level detail (which task is `[~]`, which Reviewer FAILed)
lives in each child's own `task.md` and `execution.md`, never here — duplicating it guarantees drift.

### Closed-set rule

> **This table is the exhaustive child set of the spec family.** No AKILI command creates a child
> spec folder without a prior manifest row. Adding a row is a HITL-approved manifest edit, not a
> side effect of execution.

Copy that rule verbatim into every `family.md` you author.

---

## MARLO-specific guidance

- **Parallel-safe is usually `no` for migrations.** Two children that each add a Flyway migration
  under `marlo-web/src/main/resources/database/migrations/` collide on ordering even when the SQL is
  disjoint. Serialize them, or have one child own all schema work for the family.
- **`APConstants.java` is a shared writer.** Any child adding a specificity key touches *both*
  `marlo-data/.../config/APConstants.java` and `marlo-web/.../config/APConstants.java`. Two such
  children are never `parallel-safe: yes`.
- **`struts-*.xml` is per-module.** Children touching *different* `struts-<module>.xml` files are
  parallel-safe; children touching the same one are not.
- **`global.properties` is a shared writer.** i18n additions from two children conflict.

---

## Sample `family.md` skeleton (copy when a proposal is split)

```markdown
# Innovations — Scaling Readiness — Spec Family Manifest

| Field | Value |
|---|---|
| Parent spec path | `docs/specs/domain/innovations` |
| Date created | 2026-08-24 |
| Last updated | 2026-08-24 |
| Spec-family status | `open` |
| Owner | IBD Team |

## Children

| # | Spec Path | Depends on | Parallel-safe | Status |
|---|---|---|---|---|
| 1 | `domain/innovations/schema` | none | no | `done` |
| 2 | `domain/innovations/manager-chain` | `domain/innovations/schema` | no | `active` |
| 3 | `domain/innovations/wizard-ui` | `domain/innovations/manager-chain` | yes | `pending` |
| 4 | `domain/innovations/qa-checks` | `domain/innovations/manager-chain` | yes | `pending` |

> **This table is the exhaustive child set of the spec family.** No AKILI command creates a child
> spec folder without a prior manifest row. Adding a row is a HITL-approved manifest edit, not a
> side effect of execution.

## Decision Log

| Date | Decision | Rationale |
|---|---|---|
| 2026-08-24 | Split schema from manager chain | Migration must land and be verified before the replication chain is written against it. |
```
