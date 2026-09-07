# Admin — Backlog Triage from the Roles & Permissions Audit

**Spec:** DOMAIN-ADMIN-001 · task `DOMAIN-ADMIN-001-T12`
**Source:** `roles-permissions-catalog.md` §11 (findings, 11.1–11.15) and §13 (validation results, including
the §13.6 code cross-check)
**Status:** Draft — **no Jira issue created yet.**
**Last Updated:** 2026-09-03

The team confirmed that the audited database carries the same data as production, so the findings are production
facts and no reconfirmation step is pending (`task.md` T11 closed on that basis).

## Conclusion first

Of the 15 findings, **two justify a ticket now and one is a cheap fix worth doing opportunistically.** The rest
are either already fixed, latent with no current impact, conditional on a decision that has not been taken, or
discretionary.

| Verdict | Items |
|---|---|
| **Create now** | B-01 — duplicate rows and missing unique constraints · B-03 — grants no code path checks |
| **Cheap, low urgency** | B-02 — `isAiccra()` misclassifies global unit 46 |
| **Already fixed elsewhere** | Admin-module phase lockout (11.14) — specced and fixed on branch `A2-2022-admin-menu-phase-lockout` |
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

## B-03 — Reconcile the permission strings in the database with the strings the code checks

- **Type:** Bug / Technical debt · **Priority:** Medium · **Closes:** finding 11.15
- **Component:** database + `marlo-data` / `Permission.java`
- **Verdict: create this one.** It is the second finding with a measured production cost, and the only one that
  makes the access configuration actively misleading.

**Description.** 67 of the 195 distinct permission strings held by AICCRA roles imply no string the application
ever tests — **682 of 1,936 role-grant pairs, 35%**. Two causes:

- **The section name diverged.** `role_permissions` says `outcomes`, `Permission.java` says `outcomesPandR`.
  Also `partner` vs `partners`, `contributionCrps` vs `contributionCrp`, `innovationsList` vs `innovations`,
  `policyList` vs `policies`, `studies` vs `expectedStudies`, and `crp:{0}:impactPathway:{1}:canAcess` vs
  `crp:{0}:impactPathway:canAcess`. Two families have no counterpart at all: `project:{1}:evaluation:*` (no
  action, FTL or Struts mapping exists) and `project:{1}:safeguards:canEdit` (`SafeguardAction:500` gates the
  section on `description:canEdit` instead).
- **The field is no longer checked.** Field-level grants such as `description:workplan`,
  `highlights:addHighlight`, `outputs:briefSummary` and the five `otherContributions:*` sit under a base the
  code does reference, but the action only ever asks for `canEdit`. Field-level control was configured and the
  code that consumed it is gone.

Separately, **30 of the 153 constants in `Permission.java` are declared and never referenced** — 17 of them the
REST API family (`api:institutions:*`, `api:crps:*`, `api:crpProgram:*`).

**Why it matters even though nothing is broken.** No role loses access today: every holder of a dead grant also
holds a covering grant (`description:*` or `description:canEdit`, `outcomesPandR`, `partners…`), and the one
exception, `CL`, has no users. The cost is elsewhere:

- **The configuration cannot be read correctly.** An administrator granting `safeguards:canEdit` believes they
  granted something. The first draft of the catalog made exactly that mistake, and so does the Admin UI.
- **It inflates `getPermissions`.** These rows are materialised into the `user_permission` temp table on every
  authorization cache miss, on top of the duplicates in B-01.
- **A rename can silently revoke access.** The next time a section is renamed in `Permission.java` without a
  matching migration, the failure is invisible: both sides look correct in isolation. That is how these 67 got
  here.

**Evidence.** Catalog §11.15, reproducible with the script in §13.6, which exits non-zero on any dead grant.

**Acceptance criteria.**
- Each of the 67 strings is classified: rename the database row to match the code, delete it, or add the code
  check that was intended. A row may also be explicitly kept with a recorded reason.
- Renames ship as a Flyway migration that updates `permissions.permission` in place, so `role_permissions` and
  `user_roles` are untouched.
- **No role gains or loses an effective grant.** Compare `CALL getPermissions(<user_id>)` output before and
  after for at least one holder of each affected role; the §13.5 suite passes unchanged.
- The 30 unreferenced constants are removed or documented as reserved.
- §13.6 is added to CI, or its expected counts are recorded so a future rise is noticed.
- The catalog §4 matrices and Appendix A are regenerated afterwards.

**Risk.** Medium-high, and higher than B-01 despite touching fewer rows. Renaming a permission string changes
authorization behaviour: get one wrong in the direction of *more* reach and it is a privilege escalation, in the
direction of *less* and a role silently loses a section. Do it family by family, each with a before/after
`getPermissions` diff, not as one migration. The safest first slice is the two families with no code counterpart
at all (`evaluation`, `safeguards`), where deletion cannot grant anything new.

**Suggested sequencing.** Land B-01 first. De-duplicating and adding the unique constraints shrinks the surface
this ticket has to reason about, and both touch the same two tables.

---

## Why the other findings do not need a ticket

### Already fixed elsewhere

- **Admin-module phase lockout (11.14).** `CRP-Admin` holders on AICCRA cannot reach the Admin module — or the
  top bar at all — because the role's nine grants are phase-gated and AICCRA has no editable phase, which puts
  the only screen that can reopen a phase behind the phase that is closed. This is the most severe finding in the
  catalog, and it is **already specced and fixed** on branch `A2-2022-admin-menu-phase-lockout`
  (`docs/specs/bugfix/admin-menu-phase-lockout/`, commit `5a62b86bfb`), because it changes the authorization
  procedure for every global unit and so did not belong to a documentation spec. No ticket is needed from this
  triage; link that branch's issue to A2-2022 instead.


### Latent — fold into the decision that would activate them

- **`PL`/`CL` label collision (11.3).** Two roles render "Cluster Leader", and
  `CrpUsersAction.getUserRoles()` de-duplicates by label. Invisible today because AICCRA assigns nobody to `CL`.
  It becomes real only if PMU decides AICCRA should start using `CL`; make it part of that work, not a standalone
  ticket.
- **Feedback seed migration `cluster_type_id` (11.6).** Production already holds the correct rows, and the
  migration is a no-op wherever AICCRA roles 420–433 are absent. The divergence would only surface in a new
  environment that later seeds those roles. Fix it as part of enabling feedback on GU 47 — the 11.7 bullet
  immediately below — if that decision is ever taken; a standalone ticket buys nothing.
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

> **Deferred — 2026-09-01.** The team decided that understanding the configuration as it stands comes first.
> These are recorded for later evaluation; nobody is waiting on them and no recommendation is offered.

| Finding | Question | If the answer is "change it" |
|---|---|---|
| 11.8 | Should `PMU`/PMC reach the Admin module? It has the widest editorial reach (`crp:{0}:project:*`) but no `admin:canAcess`. | Grant read-only admin access, or document the exclusion as intended. |
| 11.9 | Is `CRP-Admin`'s unqualified `crp:*` intended? It matches every global unit, making the role cross-tenant. | Replace with `crp:{0}:*`. Security-sensitive — review who holds the role first. |
| 11.10 | Which roles should be deactivated for AICCRA? `FM`, `DM`, `CL`, `ML`, `E`, `AR`, `ARW`, `CD` have no users on either AICCRA global unit. | **AICCRA rows only.** `CL` alone has users in nine other global units, and the `permissions` catalog is shared. |
| 11.11 | AICCRA never assigns `CL`, so no cluster-level role can reopen a submitted project. Adopt `CL`, or keep reopening above the cluster? | Admin configuration, plus the label disambiguation described under 11.3 above, which only becomes visible if `CL` is adopted. |
| 11.13 | Should `PC` — 145 users, the largest population — hold no workflow grant at all? | Grant one, or document the exclusion. |

---

## Creation checklist

1. Confirm the parent epic (proposed: **A2-2017** — AICCRA III · Improvements for admin module).
2. Create **B-01**, then **B-03** (which is easier to scope once B-01 has landed). Add **B-02** if the epic has
   room for a low-priority cleanup.
3. Issues are written in English, in project `A2`.
4. Link each new issue to **A2-2022** so the trail back to the catalog survives. Also link the existing
   `A2-2022-admin-menu-phase-lockout` issue, so 11.14 is visibly accounted for rather than looking untriaged.
5. Append the issue key to the corresponding finding in `roles-permissions-catalog.md` §11.
