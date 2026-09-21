# MARLO — Spec Family Manifest Template

**Purpose:** This file defines the *format and conventions* for `family.md`, the manifest a **spec family** uses to track child order, dependencies, and status. It is a methodology template, not a feature spec.

**What a spec family is:** a parent spec folder plus the child spec folders produced when its scope was chunked. `/akili-propose` or `/akili-specify` authors one `family.md` **per spec family**, and **only when a proposal was actually split**.

**Its absence means the spec is flat** — a single `requirements.md` / `design.md` / `task.md` triplet with zero added obligations. Do not create a `family.md` for a spec that was never chunked.

**Where it lives:** in the parent spec folder, alongside `requirements.md`, `design.md`, and `task.md`:

```
docs/specs/epic/phase-topology/
├── family.md              <-- the manifest
├── requirements.md
├── design.md
├── task.md
├── phase-inheritance/     <-- child spec (row 1)
│   ├── requirements.md
│   ├── design.md
│   └── task.md
└── phase-switcher-ui/     <-- child spec (row 2)
    ├── requirements.md
    ├── design.md
    └── task.md
```

---

## Front matter (top of every `family.md`)

```
# <Family Name> — Spec Family Manifest

**Parent spec path:** docs/specs/<taxonomy>/<family>/
**Spec Family ID:** <EPIC-PHASE-TOPOLOGY-FAMILY | DOMAIN-PROJECTS-FAMILY>
**Date Created:** YYYY-MM-DD
**Last Updated:** YYYY-MM-DD
**Spec-family status:** open | complete
**Owner:** <name / team>
**Related PRD sections:** docs/prd.md §<n>
```

`complete` is set only when every child row reads `done`.

---

## Required structure

1. **Document Control** — the front matter above.
2. **Scope Summary** — one paragraph: what the parent scope was and why it needed chunking.
3. **Child Table** — the manifest proper (schema below).
4. **Closed-Set Rule** — reproduce the rule verbatim (§ below). It MUST appear inside every `family.md`.
5. **Ordering Rationale** — why the build order is what it is; call out any MARLO-specific driver (phase replication, migration ordering, `APConstants` in both modules, interceptor stack changes).
6. **Decision Log** — append-only `YYYY-MM-DD — decision — rationale`.

---

## Child table schema

One row per child spec. Columns are fixed:

| Column | Values | Meaning |
|---|---|---|
| `#` | `1..n` | Build order |
| `Spec Path` | `<family>/<child>` | MUST correspond to a real folder |
| `Depends on` | spec path(s) \| `none` | Serial ordering constraint |
| `Parallel-safe` | `yes` / `no` | Fleet eligibility |
| `Status` | `pending` / `active` / `done` / `blocked` | Small vocabulary — phase detail lives in each child's own `task.md` |

Example:

```markdown
| # | Spec Path | Depends on | Parallel-safe | Status |
|---|---|---|---|---|
| 1 | phase-topology/phase-inheritance | none | no | done |
| 2 | phase-topology/phase-switcher-ui | phase-topology/phase-inheritance | no | active |
| 3 | phase-topology/admin-phase-crud | phase-topology/phase-inheritance | yes | pending |
```

**Status vocabulary is deliberately small.** Do not add `in review`, `blocked-on-QA`, or percentage columns — that detail belongs in the child's own `task.md`, and duplicating it here creates two sources of truth that drift.

---

## Closed-set rule

State this inside every `family.md`, verbatim:

> **Closed-set rule.** This table is the exhaustive child set of the spec family. No AKILI command creates a child spec folder without a prior manifest row. Adding a row is a HITL-approved manifest edit.

---

## MARLO-specific guidance on `Parallel-safe`

Mark a child `no` — not `yes` — whenever any of these is true. These are the MARLO conditions that make two otherwise-independent children collide:

| Condition | Why it serializes |
|---|---|
| Both children add Flyway migrations | Migration filenames carry a `V<major>_<minor>_<patch>_<YYYYMMDD>_<HHMM>` ordering; concurrent authors produce colliding or out-of-order versions |
| Both children touch the same `struts-*.xml` | Single-file concurrent writers; the routing catalog contract in `reports/ai-context/struts-critical-routing-catalog.md` assumes serialized edits |
| Both children add constants to `APConstants.java` | The constant must land in **both** `marlo-data/` and `marlo-web/` copies; parallel edits reliably desynchronize them |
| Both children touch the same `ManagerImpl` save/delete chain | Phase-replication ordering is order-sensitive (see `reports/ai-context/persistence-replication-managerimpl.md`) |
| Both children edit `global.properties` or the same `custom/*.properties` | Single-file concurrent writers on i18n keys |
| Either child changes the interceptor stack | Stack order is global; a concurrent second change makes a failure unattributable |

Two children that only add new FTL views under different section folders, or only touch different `Action` classes, are ordinarily `yes`.

---

## Update discipline

- The manifest is updated when a child's status transitions, and at no other time.
- `Last Updated` moves with every edit.
- Setting **spec-family status** to `complete` requires every child row at `done`.
- A child discovered mid-flight to be unnecessary is **not** deleted from the table — its row stays and the Decision Log records why it was dropped. The table is the family's history, not just its present.
