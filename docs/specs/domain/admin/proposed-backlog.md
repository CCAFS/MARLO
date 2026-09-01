# Admin — Backlog Triage from the Roles & Permissions Audit

**Spec:** DOMAIN-ADMIN-001 · task `DOMAIN-ADMIN-001-T12`
**Source:** `roles-permissions-catalog.md` §11 (findings) and §13 (validation results)
**Status:** Draft — **no Jira issue created yet.**
**Last Updated:** 2026-09-01

The team confirmed that the audited database carries the same data as production, so the findings are production
facts and no reconfirmation step is pending (`task.md` T11 closed on that basis).

## Conclusion first

Of the 13 findings, **one justifies a ticket now and one is a cheap fix worth doing opportunistically.** The rest
are either latent with no current impact, conditional on a decision that has not been taken, or discretionary.

| Verdict | Items |
|---|---|
| **Create now** | B-01 — duplicate rows and missing unique constraints |
| **Cheap, low urgency** | B-02 — `isAiccra()` misclassifies global unit 46 |
| **No ticket: latent, fold into a decision** | `PL`/`CL` label collision (11.3), feedback seed migration (11.6), feedback matrix for GU 47 (11.7) |
| **No ticket: no impact today** | `{1}`-unresolved grants (11.5), `Genebank` `SL` outlier (11.1 outlier) |
| **No ticket: discretionary** | Admin-UI hint for permission-less roles (11.1) |
| **Not defects at all** | `SL` without grants (11.1), `PL`/`CL` split (11.11) — platform-wide design |
| **Decisions for PMU, not tickets** | 11.8, 11.9, 11.10, 11.13, plus the `CL` adoption question from 11.11 |

---

## B-01 — De-duplicate `role_permissions` and `user_roles`, then add unique constraints

- **Type:** Bug / Technical debt · **Priority:** High · **Closes:** finding 11.2
- **Component:** database
- **Verdict: create this one.** It is the only finding with a measured cost in production.

**Description.** Neither `role_permissions(role_id, permission_id)` nor `user_roles(user_id, role_id)` has a
unique index, and duplicates have accumulated: **15,608 duplicate rows across 4,192 distinct
`(role_id, permission_id)` pairs**, plus 123 duplicate rows across 13 `(user_id, role_id)` pairs.

The cost is not theoretical. `getPermissions` re-materialises the `user_permission` temp table on every
authorization cache miss, and duplicates multiply the rows written:

| User | Rows written | Distinct permissions | Wasted |
|---|---:|---:|---:|
| 3059 | 230 | 45 | **185 (80%)** |
| 1082 | 1,740 | 992 | **748 (43%)** |

It also distorts every audit: counting grants or role holders without `DISTINCT` gives the wrong answer, which is
how the first draft of this catalog reported `SL` as having 129 users when it has 13.

**Evidence.**
```sql
SELECT SUM(c-1) extra_rows, COUNT(*) affected_pairs
FROM (SELECT role_id, permission_id, COUNT(*) c FROM role_permissions
      GROUP BY role_id, permission_id HAVING c > 1) x;   -- 15608 / 4192

SELECT SUM(c-1), COUNT(*)
FROM (SELECT user_id, role_id, COUNT(*) c FROM user_roles
      GROUP BY user_id, role_id HAVING c > 1) y;         -- 123 / 13
```
Concrete cases: AICCRA `PMU` holds permission 448 (`crp:{0}:fundingSource:canEdit`) three times; `SL` on GU 45 has
129 `user_roles` rows for 13 distinct users. Separately, `permissions` contains `crp:{0}:fundingSource:*` twice
(ids 450 and 451); both ids are referenced, so that row is a related but distinct change.

**Acceptance criteria.**
- A Flyway migration removes duplicate rows from both tables, keeping the lowest `id` per pair.
- A unique index is added on `role_permissions(role_id, permission_id)` and `user_roles(user_id, role_id)`.
- The migration is idempotent and a no-op on a database with no duplicates.
- **No role loses an effective grant and no user loses a role:** distinct-set counts per role and per user are
  identical before and after. The assertion suite in the catalog §13.5 should pass unchanged.
- `permissions` ids 450/451 are either merged or explicitly deferred with a reason.

**Risk.** Medium — it deletes rows in the authorization tables. Needs a backup, a before/after distinct-set
comparison, and a maintenance window. The dedup itself is mechanical; the unique index is what prevents
regression.

---

## B-02 — `isAiccra()` misclassifies global unit 46 (Alliance) as AICCRA

- **Type:** Bug · **Priority:** Low · **Closes:** finding 11.4
- **Component:** `marlo-web` / BaseAction
- **Verdict: cheap fix, no urgency.** Do it when someone is next in this file.

**Description.** `BaseAction.isAiccra()` returns true for `getCurrentCrp().getId() >= 45`. Global unit 45 is
AICCRA and 47 is AICCRA_III, but **46 is `Alliance`**, which therefore inherits AICCRA role labels and cluster
terminology. `Role.getAiccraAcronymDimanic()` already uses the safe test
(`getCrp().getAcronym().contains("AICCRA")`), so the fix is to align the two, or to drive the behaviour from a
specificity parameter instead of an id comparison.

**Why the priority is low.** Alliance is a stub in production: **0 phases**, 1 `crp_users` row, and its only
role holder is a `SuperAdmin`, who bypasses every check through `*`. With no phases, `getPermissions` emits
nothing for it. So there is no live misbehaviour today.

**Why it is still worth fixing.** The predicate is an open-ended id range. AICCRA III was created as id 47, so
the next global unit will be 48 and will be silently classified as AICCRA the moment it is created.

**Acceptance criteria.**
- `isAiccra()` returns false for global unit 46, true for 45 and 47.
- No id-range comparison remains as the discriminator.
- A global unit with an id above 47 is not treated as AICCRA.

**Risk.** Low. Read-only predicate; blast radius is label and terminology rendering.

---

## Why the other findings do not need a ticket

### Latent — fold into the decision that would activate them

- **`PL`/`CL` label collision (11.3).** Two roles render "Cluster Leader", and
  `CrpUsersAction.getUserRoles()` de-duplicates by label. Invisible today because AICCRA assigns nobody to `CL`.
  It becomes real only if PMU decides AICCRA should start using `CL`; make it part of that work, not a standalone
  ticket.
- **Feedback seed migration `cluster_type_id` (11.6).** Production already holds the correct rows, and the
  migration is a no-op wherever AICCRA roles 420–433 are absent. The divergence would only surface in a new
  environment that later seeds those roles. Fix it inside B-08 below if feedback is ever enabled for GU 47; a
  standalone ticket buys nothing.
- **Feedback matrix missing for GU 47 (11.7).** `feedback_active` is `false` on GU 47, so nothing is broken. This
  is a prerequisite of enabling the module, not an open defect. When that decision is taken, the ticket is: seed
  the GU 47 matrix with the correct cluster-type ids, guarded by a join against `global_units` and `roles`.

### No impact today

- **`{1}`-unresolved grants (11.5).** Four permission families reach the runtime with the placeholder in place, so
  they never match. Nothing is broken by their presence: they grant nothing rather than granting too much, and in
  AICCRA the sections they target (`synthesisProgram`, `crpIndicators`) are not reachable anyway. They are held by
  87–127 roles platform-wide, so any cleanup is a wide-blast-radius change with no benefit beyond tidiness. Park
  it; revisit only if a role is reported as missing access to funding-source budgets or CRP indicators.
- **`Genebank` `SL` carrying 947 grants (11.1 outlier).** Zero users hold it and Genebank is a closed platform, so
  there is no exposure. Curiosity, not work.

### Discretionary

- **Admin-UI hint for permission-less roles (11.1).** Showing that a role grants nothing would stop
  administrators assigning `SL` expecting access. A genuine small improvement, but nobody has reported the
  confusion. Raise it only if the Admin-module epic has room.

### Not defects — platform-wide design

- **`SL` without grants (11.1).** Zero grants in 19 of the 21 global units where the role exists, including
  others with users. A marker role, working as intended.
- **`PL` submits / `CL` unsubmits (11.11).** The split is consistent across CCAFS, A4NH, Wheat, AICCRA and
  AICCRA III (PIM is the one variant), and `CL` has real users in nine global units. Intentional separation of
  duties, not a half-finished duplicate.

---

## Decisions for PMU — prerequisites, not tickets

Tracked as `task.md` T10. None of these is a defect; each needs a decision before any work exists.

| Finding | Question | If the answer is "change it" |
|---|---|---|
| 11.8 | Should `PMU`/PMC reach the Admin module? It has the widest editorial reach (`crp:{0}:project:*`) but no `admin:canAcess`. | Grant read-only admin access, or document the exclusion as intended. |
| 11.9 | Is `CRP-Admin`'s unqualified `crp:*` intended? It matches every global unit, making the role cross-tenant. | Replace with `crp:{0}:*`. Security-sensitive — review who holds the role first. |
| 11.10 | Which roles should be deactivated for AICCRA? `FM`, `DM`, `CL`, `ML`, `E`, `AR`, `ARW`, `CD` have no users on either AICCRA global unit. | **AICCRA rows only.** `CL` alone has users in nine other global units, and the `permissions` catalog is shared. |
| 11.11 | AICCRA never assigns `CL`, so no cluster-level role can reopen a submitted project. Adopt `CL`, or keep reopening above the cluster? | Admin configuration, plus B-05-style label disambiguation if `CL` is adopted. |
| 11.13 | Should `PC` — 145 users, the largest population — hold no workflow grant at all? | Grant one, or document the exclusion. |

---

## Creation checklist

1. Confirm the parent epic (proposed: **A2-2017** — AICCRA III · Improvements for admin module).
2. Create **B-01**. Add **B-02** if the epic has room for a low-priority cleanup.
3. Issues are written in English, in project `A2`.
4. Link each new issue to **A2-2022** so the trail back to the catalog survives.
5. Append the issue key to the corresponding finding in `roles-permissions-catalog.md` §11.
