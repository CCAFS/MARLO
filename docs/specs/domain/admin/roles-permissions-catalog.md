# MARLO — Roles & Permissions Catalog (AICCRA)

**Spec:** DOMAIN-ADMIN-001 (Jira A2-2022 / US3, epic A2-2017 "AICCRA III — Improvements for admin module")
**Status:** Draft — pending validation by PMU / QA
**Owner:** IBD Team
**Last Updated:** 2026-09-01
**Source of truth:** `roles`, `role_permissions`, `permissions`, `user_roles`, `custom_parameters`,
`feedback_roles_permissions` (global units 45 `AICCRA` and 47 `AICCRA_III`), plus the code paths listed in §8.
**Verification snapshot:** database `aiccradb1`, 2026-09-01 — confirmed by the team to carry the same data as
production, so the figures below are production facts. Re-run the queries in §10 after any role or permission
change.

> This catalog is descriptive, not prescriptive. It documents the access model **as configured today**.
> No role or permission was changed while producing it.

---

## 1. How authorization actually works

MARLO authorization has four independent layers. A role's real-world capability is the union of all four,
which is why reading the `role_permissions` table alone is not enough.

```
 ┌─ 1. DB grant ─────────┐   roles ─ role_permissions ─ permissions
 │                       │   per global unit; permission strings carry {0} and {1} placeholders
 ├─ 2. Runtime expansion ┤   stored procedure getPermissions(user_id)
 │                       │   {0} -> "<gu acronym>:<phase description>:<phase year>"
 │                       │   {1} -> project id / program id, resolved per user's assignments
 ├─ 3. Shiro check ──────┤   APCustomRealm -> SimpleAuthorizationInfo -> WildcardPermission
 │                       │   BaseAction.hasPermission(field) = base:field OR base
 └─ 4. Code-level gates ─┘   BaseAction.canAccessSuperAdmin(), isAdmin(), canAcess*(), feedback matrix
```

**Key consequences**

- **Permissions are phase-scoped.** `getPermissions` joins `phases` with `editable = 1`. A role holding
  `crp:{0}:project:{1}:description:*` can only edit while a phase is open. When every phase of a global unit is
  closed, the role grants nothing — this is how AICCRA phase 2 (GU 45) is frozen today while AICCRA III
  (GU 47, `Planning 2026`, phase 444) is open.
- **Holding a node grants everything under it.** `BaseAction.hasPermission(fieldName)` returns true when the
  subject has `basePermission + ":" + fieldName` **or** `basePermission` itself
  (`marlo-web/.../action/BaseAction.java:6348`). A grant that stops at the section (e.g.
  `crp:{0}:reportSynthesis:{1}:crpProgress`) therefore unlocks that whole section, not just a field.
- **Roles are per global unit.** `roles.global_unit_id` scopes every role. The same acronym exists once per
  global unit with a different id, so AICCRA (45) and AICCRA III (47) have two parallel, independent role sets.
- **Role ids are configuration, not constants.** Code never hardcodes a role id; it reads
  `custom_parameters` keys (`crp_pmu_rol`, `crp_pl_rol`, …) — see §7.

---

## 2. Role catalog

20 roles per AICCRA global unit. `Perms` counts distinct rows in `role_permissions`. **`Perms` is an upper
bound, not what a user gets:** project- and program-scoped grants only materialize for the objects a user is
actually attached to. Verified at runtime — `RPL` carries 102 configured grants but resolved to 38 effective rows
for a user with no projects in the region (see §13.2).
`AICCRA label` is what the UI actually renders (Admin → Users tabs and the header role list), produced by
`Role.getAiccraAcronymDimanic()` (`marlo-data/.../data/model/Role.java`); when that method has no mapping it
falls back to `roles.description`.

| # | Acronym | DB description | AICCRA label | Perms | Users AICCRA (GU 45) | Users AICCRA III (GU 47) |
|---|---|---|---|---:|---:|---:|
| 1 | `E` | External Evaluator | External Evaluator | 1 | 0 | 0 |
| 2 | `SuperAdmin` | Super Admin | Super Admin | 3 | 11 | 1 |
| 3 | `CRP-Admin` | MARLO Administrators | MARLO Administrators | 9 | 1 | 1 |
| 4 | `PMU` | Program Management Unit | PMC | 65 | 13 | 1 |
| 5 | `FM` | Finance person | Finance person | 23 | 0 | 0 |
| 6 | `DM` | Data Manager | Data Manager | 4 | 0 | 0 |
| 7 | `FPL` | Flagship Leaders | Theme Leader | 144 | 7 | 1 |
| 8 | `RPL` | Regional Program Leaders | Regional Program Leaders | 102 | 12 | 1 |
| 9 | `CP` | Contact point | Contact Point | 79 | 21 | 0 |
| 10 | `CL` | Cluster Leader | Cluster Leader | 58 | 0 | 0 |
| 11 | `FPM` | Flagship Manager | Theme Manager | 132 | 5 | 2 |
| 12 | `SL` | Site Integration Leader | Site Integration Leader | 0 | 13 | 0 |
| 13 | `ML` | Management Liaison | Management Liaison | 90 | 0 | 0 |
| 14 | `RPM` | Regional Manager | Regional Manager | 99 | 3 | 2 |
| 15 | `PL` | Project leader | Cluster Leader | 81 | 21 | 0 |
| 16 | `PC` | Project coordinator | Cluster Coordinator | 70 | 145 | 1 |
| 17 | `G` | Guest | Guest | 1 | 37 | 0 |
| 18 | `AR` | API Read | API Read | 1 | 0 | 0 |
| 19 | `ARW` | API Read-Write | API Read-Write | 4 | 0 | 0 |
| 20 | `CD` | CapDev Manager | CapDev Manager | 2 | 0 | 0 |


---

## 3. Purpose, scope and constraints per role

> **`PC` and `CP` are different roles and are easy to confuse.** `PC` = Project coordinator (Cluster Coordinator
> in AICCRA, 145 users on GU 45, project-scoped). `CP` = Contact point (institutional, 21 users). Their workflow
> capability differs: `CP` can submit **and** unsubmit a project; `PC` can do neither.

| Role | Purpose | Grant scope | Modules / sections | Constraints & special rules |
|---|---|---|---|---|
| `SuperAdmin` | Platform operator. | `*` — unlimited, across every global unit | Everything, including the superadmin console and the full REST API | Only role that bypasses every check (`BaseAction.canAccessSuperAdmin()`). Also the only role with `api:*`. |
| `CRP-Admin` | Program administrator for one global unit. | `crp:*` + `crp:{0}:*` | Admin module (`admin:*`), shared projects, impact pathway submit / unsubmit per program | Holds `crp:*`, i.e. the grant is not limited to its own global unit. Sole owner of the Admin module besides `SuperAdmin`. |
| `PMU` (AICCRA label **PMC**) | Program Management Committee — program-wide editorial authority. | `crp:{0}:project:*` (every project) + program-level synthesis | All project sections, impact pathway, POWB synthesis, annual report synthesis, studies, publications, funding sources | **No Admin-module access.** Cannot manage users, phases or institutions. |
| `FPL` (AICCRA label **Theme Leader**) | Leads one theme (flagship). | Projects and programs linked to the theme via `crp_program_leaders` | Impact pathway for the theme, all project sections, annual report synthesis, POWB, summaries | Project-level rows only materialize for projects mapped to the leader's program. |
| `FPM` (AICCRA label **Theme Manager**) | Operational manager of a theme. | Same resolution path as `FPL` | Like `FPL` minus the 13 grants listed at right (132 vs 144) | Lacks, versus `FPL`: `impactPathway:{1}:*`, `:submit`, `:unsubmitted`; `powbSynthesis:{1}:manage:canSubmmit`; `reportSynthesis:{1}:submit`; the four `project:{1}:budgetByFlagship` flags (`bilateral`, `canEdit`, `center`, `w3`); `contributionsLP6` (+`:canEdit`); `impacts` (+`:canEdit`). Holds one grant `FPL` does not: `project:{1}:fundingSource:gender`. So it cannot submit or unsubmit the theme's impact pathway, nor submit the annual report synthesis. |
| `RPL` | Leads one region. | Projects and programs mapped to the region | Project sections, impact pathway edit, publications, studies, POWB collaboration | No `budgetByFlagship`, no `budgetByCoAs`. |
| `RPM` | Operational manager of a region. | Same resolution path as `RPL` | Project sections incl. full `budgetByPartners`, publications, studies | Only role with a wildcard on `budgetByPartners`. |
| `PL` (AICCRA label **Cluster Leader**) | Leads one cluster / project. | Single project, resolved from `project_partner_persons` | All editable sections of that project + submit | Granted implicitly when the user is set as project leader in the Partners section; see §7. |
| `PC` (AICCRA label **Cluster Coordinator**) | Coordinates one cluster / project. | Single project, same resolution as `PL` | Same sections as `PL` minus `manage` (submit), `partner`, `evaluation`, `deleteProject` | **Can neither submit nor unsubmit the project** — it holds no workflow grant at all (§11.13). Largest population in AICCRA (145 users on GU 45). Not to be confused with `CP`. |
| `CP` (AICCRA label **Contact Point**) | Institutional contact point for a PPA partner. | Projects where the user's institution participates | Project sections; both submit and `unsubmitted` | Assigned from Admin → PPA Partners; gated by `crp_has_contact_point`. |
| `ML` | Management Liaison for a liaison institution. | Projects attached to the liaison institution | Project sections, publications, summaries, `deleteProject` | 0 users in both AICCRA global units — inactive in practice. |
| `CL` | Cluster Leader (parallel definition to `PL`). | Project-scoped | 58 grants: 57 shared with `PL`, 1 exclusive | **Not a subset of `PL`.** It uniquely holds `project:{1}:unsubmitted`, which `PL` lacks, and lacks 24 grants `PL` holds. Net effect: `CL` can unsubmit but not submit, `PL` can submit but not unsubmit (§11.11). 0 users in both AICCRA global units. Its label collides with `PL`'s AICCRA label — see §11.3. |
| `FM` | Finance person. | Funding sources + project budget sections | `fundingSource:*`, `budgetByPartners`, `budgetByFlagship`, `partner`, `projectSwitch` | Explicitly excluded from the generic branch of `getPermissions` (`r.acronym != 'FM'`); resolved through dedicated branches keyed on the finance person of an institution. 0 users. |
| `DM` | Data Manager. | Global unit | `admin:canAcess` (read-only entry to the Admin module), publication add, `synthesisProgram` | Second role with Admin-module visibility, without `admin:*`. 0 users. |
| `SL` | Site Integration Leader. | — | **None** | 0 rows in `role_permissions`. The role is a label only; 13 distinct users hold it on GU 45. See §11.1. |
| `E` | External Evaluator. | Single project | `project:{1}:evaluation:canEdit` only | 0 users. Paired with `evaluation:accessEE` held by PMU/leaders. |
| `G` | Guest | Global unit | `impactPathway:canAcess` — read-only visibility of the impact pathway menu | 37 users on GU 45. |
| `CD` | CapDev Manager | Global unit | `capDev:*`, impact pathway view | CapDev is a Center-type module; `crp_capdev_active` is `true` on GU 47 but the CapDev menu entry only renders for Center global units. |
| `AR` | API Read | REST API | `api:*:read` | Service account role; no UI access. |
| `ARW` | API Read-Write | REST API | `api:*:read/create/update/delete` | Service account role; no UI access. |

---

## 4. Permissions matrix (roles × actions)

**Legend**

| Symbol | Meaning |
|---|---|
| `●` | Full — wildcard on the node, or the node itself is granted (unlocks everything below it) |
| `◐` | Edit — `canEdit` on the section, optionally plus field-level flags |
| `◔` | Field-level only — specific fields, no section-wide edit |
| `▲` | Submit / unsubmit workflow action |
| `○` | View / access only (`canAcess`) |
| `·` | No grant |

Columns follow `roles.order` (the order the Admin → Users screen uses). Roles with no grant in a given scope are
omitted from that table.

### 4.1 Global-unit scope (`permissions.type = 0`)

The first four rows are whole-tree grants rather than sections; they are listed with their literal permission
string because their reach is the point.

| Global-unit scope | SuperAdmin | CRP-Admin | PMU | FM | DM | FPL | RPL | CP | CL | FPM | ML | RPM | PL | PC | G | CD |
|---|---|---|---|---|---|---|---|---|---|---|---|---|---|---|---|---|
| `crp:*` — every global unit | · | ● | · | · | · | · | · | · | · | · | · | · | · | · | · | · |
| `*` — everything, platform-wide | ● | · | · | · | · | · | · | · | · | · | · | · | · | · | · | · |
| `superadmin:canEdit` — superadmin console | ◐ | · | · | · | · | · | · | · | · | · | · | · | · | · | · | · |
| `crp:{0}:*` — entire own global unit | · | ● | · | · | · | · | · | · | · | · | · | · | · | · | · | · |
| `admin` | · | ● | · | · | ○ | · | · | · | · | · | · | · | · | · | · | · |
| `capDev` | · | · | · | · | · | · | · | · | · | · | · | · | · | · | · | ● |
| `fundingSource` | · | · | ◐ | ● | · | ◐ | ◐ | ◐ | · | ◐ | ◐ | ◐ | · | · | · | · |
| `impactPathway` | · | · | ● | · | ○ | ○ | ○ | ○ | ○ | ○ | ○ | ○ | ○ | ○ | ○ | ○ |
| `powbSynthesis` | · | · | ○ | · | · | ○ | ○ | · | · | ○ | · | ○ | · | · | · | · |
| `publication` | · | · | ◔ | · | ◔ | ● | ● | ◔ | · | ● | ● | ● | · | · | · | · |
| `reportSynthesis` | · | · | ● | · | · | · | · | · | · | · | · | · | · | · | · | · |
| `sharedProjects` | · | ◐ | · | · | · | · | · | · | · | · | · | · | · | · | · | · |
| `studies` | · | · | ● | · | · | ◔ | ◔ | · | · | ◔ | · | ◔ | · | · | · | · |
| `summaries` | · | · | · | · | · | ● | ● | · | · | ● | ● | ● | · | · | · | · |

### 4.2 Project / cluster scope (`permissions.type = 1`)

`{1}` is replaced with a project id per user assignment, so a row here means "for the projects this role reaches".

| Project / cluster section | E | PMU | FM | FPL | RPL | CP | CL | FPM | ML | RPM | PL | PC |
|---|---|---|---|---|---|---|---|---|---|---|---|---|
| `*` | · | ● | · | · | · | · | · | · | · | · | · | · |
| `activities` | · | · | · | ● | ● | ● | ◐ | ● | ● | ● | ◐ | ◐ |
| `budgetByCoAs` | · | · | · | ◐ | · | ◐ | ◐ | ◐ | ◐ | · | ◐ | ◐ |
| `budgetByFlagship` | · | ◐ | ◐ | ◐ | · | ◐ | · | · | ◐ | · | ◐ | · |
| `budgetByPartners` | · | · | ◐ | ◐ | · | ◐ | ◐ | ◐ | ◐ | ● | ◐ | ◐ |
| `caseStudies` | · | · | · | ● | ● | ● | · | ● | ● | ● | ● | ● |
| `ccafsOutcomes` | · | · | · | ● | ● | ● | · | ● | ● | ● | ● | ● |
| `contributionCrp` | · | · | · | ● | ● | ● | ● | ● | ● | ● | ● | ● |
| `contributionCrps` | · | · | · | ◔ | ◔ | ◔ | ◔ | ◔ | ◔ | ◔ | ◔ | ◔ |
| `contributionsCrpList` | · | · | · | ● | ● | ● | ● | ● | ● | ● | ● | ● |
| `contributionsLP6` | · | ● | · | ● | ● | ● | · | · | ● | · | ● | ● |
| `deleteProject` | · | · | · | ● | ● | · | · | ● | ● | ● | · | · |
| `deliverable` | · | · | · | ◐ | ◐ | ◐ | ◐ | ◐ | ◐ | ◐ | ◐ | ◐ |
| `deliverableList` | · | · | · | ◐ | ◐ | ◐ | ◐ | ◐ | ◐ | ◐ | ◐ | ◐ |
| `description` | · | · | · | ● | ● | ● | ◐ | ● | ● | ● | ◐ | ◐ |
| `evaluation` | ◐ | ◐ | · | ◐ | ◐ | · | · | ◐ | · | ◐ | ◐ | · |
| `expectedStudies` | · | · | · | ● | ● | ● | ● | ● | ● | ● | ● | ● |
| `fundingSource` | · | · | ◔ | ◔ | ◔ | ◔ | ◔ | ◔ | ◔ | ◔ | ◔ | ◔ |
| `highlights` | · | · | · | ◐ | ◐ | ◐ | ◐ | ◐ | ◐ | ◐ | ◐ | ◐ |
| `impacts` | · | ● | · | ● | ● | ● | · | · | ● | · | ● | ● |
| `innovations` | · | · | · | ◐ | ◐ | ◐ | ◐ | ◐ | ◐ | ◐ | ◐ | ◐ |
| `innovationsList` | · | ● | · | ● | ● | ● | ● | ● | ● | ● | ● | ● |
| `leverages` | · | · | · | ● | ● | ● | · | ● | ● | ● | ● | ● |
| `locations` | · | · | · | ● | ● | ● | ● | ● | ● | ● | ● | ● |
| `manage` | · | · | · | ▲ | ▲ | ▲ | · | ▲ | ▲ | ▲ | ▲ | · |
| `otherContributions` | · | · | · | ◐ | ◐ | ◐ | ◐ | ◐ | ◐ | ◐ | ◐ | ◐ |
| `outcomes` | · | · | · | ◐ | ◐ | ◐ | ◐ | ◐ | ◐ | ◐ | ◐ | ◐ |
| `outcomesPandR` | · | · | · | ● | ● | ● | · | ● | ● | ● | ● | ● |
| `outputs` | · | · | · | ◐ | ◐ | ◐ | ◐ | ◐ | ◐ | ◐ | ◐ | ◐ |
| `partner` | · | · | ◐ | ◐ | ◐ | ◐ | · | ◐ | ◐ | ◐ | ◐ | · |
| `partners` | · | · | ◐ | ◐ | ◐ | ◐ | ◐ | ◐ | ◐ | ◐ | ◐ | ◐ |
| `policies` | · | ● | · | ● | ● | ● | · | ● | ● | ● | ● | ● |
| `policyList` | · | ● | · | ● | ● | ● | · | ● | ● | ● | ● | ● |
| `projectSwitch` | · | · | ● | ● | ● | · | · | ● | ● | ● | · | · |
| `safeguards` | · | · | · | ◐ | · | · | · | ◐ | · | · | ◐ | ◐ |
| `studies` | · | ● | · | ● | ● | ● | ● | ● | ● | ● | ● | ● |
| `unsubmitted` | · | · | · | ● | ● | ● | ● | ● | · | ● | · | · |

### 4.3 Program / theme / region scope (`permissions.type = 3`)

`{1}` is replaced with a `crp_programs` id (theme, region or cluster of activities).

| Program / theme / region section | CRP-Admin | PMU | FM | DM | FPL | RPL | CP | FPM | ML | RPM |
|---|---|---|---|---|---|---|---|---|---|---|
| `crpIndicators` | · | · | · | · | ● | ● | ● | ● | ● | ● |
| `fundingSource` | · | · | ◐ | · | ◐ | ◐ | ◐ | ◐ | ◐ | ◐ |
| `impactPathway` | ● | · | · | · | ● | ◐ | · | ◐ | · | ◐ |
| `powbSynthesis` | · | ◐ | · | · | ◐ | ◔ | · | ◐ | · | ◔ |
| `powbSynthesis › collaboration` | · | ● | · | · | ● | ◐ | · | ● | · | ◐ |
| `powbSynthesis › crossCuting` | · | ◐ | · | · | ◐ | · | · | ◐ | · | · |
| `powbSynthesis › crpStaffing` | · | ◐ | · | · | · | · | · | · | · | · |
| `powbSynthesis › evidences` | · | ◐ | · | · | ◐ | · | · | ◐ | · | · |
| `powbSynthesis › expectedProgress` | · | ◐ | · | · | ◐ | · | · | ◐ | · | · |
| `powbSynthesis › financialPlan` | · | ◐ | · | · | · | · | · | · | · | · |
| `powbSynthesis › flagshipPlans` | · | · | · | · | ◐ | · | · | ◐ | · | · |
| `powbSynthesis › managementGovernance` | · | ◐ | · | · | ◐ | · | · | ◐ | · | · |
| `powbSynthesis › managementRisk` | · | ◐ | · | · | ◐ | · | · | ◐ | · | · |
| `powbSynthesis › monitoringEvaluationLearning` | · | ◐ | · | · | ◐ | · | · | ◐ | · | · |
| `powbSynthesis › tocAdjustments` | · | ◐ | · | · | ◐ | · | · | ◐ | · | · |
| `publication` | · | ● | · | · | ● | ● | ● | ● | · | ● |
| `reportSynthesis` | · | ◐ | · | · | ◐ | · | · | ◐ | · | · |
| `reportSynthesis › ccDimensions` | · | ● | · | · | ● | · | · | ● | · | · |
| `reportSynthesis › control` | · | ● | · | · | ● | · | · | ● | · | · |
| `reportSynthesis › crossPartnerships` | · | ● | · | · | ● | · | · | ● | · | · |
| `reportSynthesis › crpProgress` | · | ● | · | · | ● | · | · | ● | · | · |
| `reportSynthesis › efficiency` | · | ● | · | · | ● | · | · | ● | · | · |
| `reportSynthesis › externalPartnerships` | · | ● | · | · | ● | · | · | ● | · | · |
| `reportSynthesis › financial` | · | ● | · | · | ● | · | · | ● | · | · |
| `reportSynthesis › flagshipProgress` | · | ● | · | · | ● | · | · | ● | · | · |
| `reportSynthesis › fundingUse` | · | ● | · | · | ● | · | · | ● | · | · |
| `reportSynthesis › governance` | · | ● | · | · | ● | · | · | ● | · | · |
| `reportSynthesis › influence` | · | ● | · | · | ● | · | · | ● | · | · |
| `reportSynthesis › intellectualAssets` | · | ● | · | · | ● | · | · | ● | · | · |
| `reportSynthesis › melia` | · | ● | · | · | ● | · | · | ● | · | · |
| `reportSynthesis › narrative` | · | ● | · | · | ● | · | · | ● | · | · |
| `reportSynthesis › plannedVariance` | · | ● | · | · | ● | · | · | ● | · | · |
| `reportSynthesis › risks` | · | ● | · | · | ● | · | · | ● | · | · |
| `reportSynthesis › srfProgress` | · | ● | · | · | ● | · | · | ● | · | · |
| `studies` | · | ◐ | · | · | ◐ | ◐ | · | ◐ | · | ◐ |
| `synthesisProgram` | · | · | · | ● | ● | ● | · | ● | · | ● |

### 4.4 Portfolio-level list actions and REST API

| Portfolio list action | PMU | FPL | RPL | CP | FPM | RPM |
|---|---|---|---|---|---|---|
| `*` | ● | · | · | · | · | · |
| `synthesis` | · | ● | ● | ● | ● | ● |

| REST API permission | SuperAdmin | AR | ARW |
|---|---|---|---|
| `api:*` (everything) | ● | · | · |
| `api:*:read` | ● | ● | ● |
| `api:*:create` | ● | · | ● |
| `api:*:update` | ● | · | ● |
| `api:*:delete` | ● | · | ● |

---

## 5. Feedback permissions (separate subsystem)

Comment / feedback capability does **not** live in `role_permissions`. It has its own matrix in
`feedback_roles_permissions` (role × `feedback_permissions` × `cluster_types` × global unit), managed from
Admin → Feedback Permissions Management (`feedbackRolesPermissionsManagement`) and evaluated by
`BaseAction.canLeaveComments()`, `canApproveComments()`, `canManageFeedback()`, `canTrackComments()`.

`cluster_types`: `1` Country, `2` Theme, `3` Management, `4` Regional. A `NULL` cluster type means "all clusters".

| Role | can_leave_comments | can_approve_comments | can_react_comments | can_track_comments |
|---|---|---|---|---|
| `SuperAdmin` | all | all | all | all |
| `PMU` | all | all | · | all |
| `FPL` | Country, Regional | · | Theme | all |
| `FPM` | Country, Regional | · | Theme | all |
| `RPL` | Country | Country | Regional | all |
| `RPM` | Country | Country | Regional | all |
| `PL` | · | · | all (own cluster) | · |
| `PC` | · | · | all (own cluster) | · |

**Constraints**

- Configured for global unit **45 only** (25 rows). Global unit 47 (AICCRA III) has **no** rows, so on AICCRA III
  every feedback check falls back to `SuperAdmin`-only.
- The whole subsystem is gated by `feedback_active`, which is `false` on GU 47.
- `CRP-Admin`, `CP`, `CL`, `ML`, `SL`, `FM`, `DM`, `E`, `G`, `CD` have no feedback grants.

---

## 6. Code-level gates (not in the database)

These checks are evaluated in Java and can grant or deny independently of `role_permissions`.
All live in `marlo-web/src/main/java/org/cgiar/ccafs/marlo/action/BaseAction.java` unless noted.

| Check | Rule | Effect |
|---|---|---|
| `canAccessSuperAdmin()` | `hasAllPermissions("*")` | Gates the BI module menu entry, the QA section and every superadmin screen. |
| `isAdmin()` | `hasRole("Admin")` | Legacy Center-type check; no AICCRA role uses the `Admin` acronym. |
| `canAcessCrpAdmin()` | `crp:{0}:admin:canAcess` | Admin module entry. `CRP-Admin` and `DM` only. |
| `canAcessSumaries()` | `canAcessCrpAdmin() OR canAccessSuperAdmin() OR user holds the role id in crp_pmu_rol` | PMU reaches Summaries through a role-id comparison, not through a permission string. |
| `canAcessImpactPathway()` | `crp:{0}:impactPathway:canAcess` | Impact pathway menu. Held by every role except `CRP-Admin`, `FM` and `SuperAdmin` (the latter via `*`). |
| `canAcessFunding()` | `crp:{0}:fundingSource:canEdit` | Funding sources module. |
| `canAcessPublications()` | `crp:{0}:publication:add` | Additional Reporting → Publications. |
| `canAcessPOWB()` | `crp:{0}:powbSynthesis:manage:canAcess` | POWB synthesis. |
| `hasPersmissionSubmit(projectId)` | `crp:{0}:project:{1}:manage:submitProject` **and** `!phase.upkeep` | Submit button; suppressed during upkeep phases. |
| `hasPermission(field)` | `base:field` OR `base` OR the phase-qualified variant | Explains why a section-level grant unlocks all its fields. |
| `isAiccra()` | `getCurrentCrp().getId() >= 45` | Switches role labels and cluster terminology. See §11.4. |

---

## 7. How each role is granted

| Role | Granted from | Writes to | Parameter that resolves the role id |
|---|---|---|---|
| `SuperAdmin`, `CRP-Admin`, `PMU`, `DM`, `G`, `AR`, `ARW`, `CD`, `E` | Admin → Users (`crpUsers`) | `user_roles` | `crp_admin_rol`, `crp_pmu_rol`, `crp_cd_rol` |
| `FPL`, `FPM` | Admin → Program Management (`management`, `CrpAdminManagmentAction`) | `crp_program_leaders` + `user_roles` | `crp_fpl_rol`, `crp_fpm_rol` |
| `RPL`, `RPM` | Admin → Regional Mapping (`regionalMapping`, `CrpProgamRegionsAction`) | `crp_program_leaders` + `user_roles` | `crp_rpl_rol`, `crp_rpm_rol` |
| `SL` | Admin → Site Integration (`siteIntegration`, `CrpSiteIntegrationAction`) | `user_roles` | `crp_sl_rol` |
| `CP` | Admin → PPA Partners (`ppaPartners`, `CrpPpaPartnersAction`) | `user_roles` | `crp_cp_role` |
| `PL`, `PC` | Project → Partners section (`ProjectPartnerAction:1501`) | `project_partner_persons` + `user_roles` | `crp_pl_rol`, `crp_pc_rol` |
| `CL` | Not wired to any screen | — | `crp_cl_rol` |
| `ML` | Assigned through the project description Management Liaison field | `user_roles` | — |

Current parameter values:

| Parameter | GU 45 (AICCRA) | GU 47 (AICCRA III) |
|---|---:|---:|
| `crp_admin_rol` | 417 | 479 |
| `crp_pmu_rol` | 427 | 489 |
| `crp_fpl_rol` | 425 | 487 |
| `crp_fpm_rol` | 432 | 494 |
| `crp_rpl_rol` | 424 | 486 |
| `crp_rpm_rol` | 433 | 495 |
| `crp_pl_rol` | 420 | 482 |
| `crp_pc_rol` | 422 | 484 |
| `crp_cp_role` | 419 | 481 |
| `crp_cl_rol` | 431 | 493 |
| `crp_sl_rol` | 428 | 490 |
| `crp_cd_rol` | 434 | 496 |

Constants for these keys live in **both** `APConstants.java` files (`marlo-data` and `marlo-web`), per the
constitutional specificity rule.

---

## 8. Source references

| Concern | File |
|---|---|
| Permission string templates | `marlo-data/src/main/java/org/cgiar/ccafs/marlo/security/Permission.java` |
| Role entity + AICCRA labels | `marlo-data/src/main/java/org/cgiar/ccafs/marlo/data/model/Role.java` |
| Shiro realm / authorization assembly | `marlo-data/src/main/java/org/cgiar/ccafs/marlo/security/APCustomRealm.java` |
| Permission lookup | `marlo-data/src/main/java/org/cgiar/ccafs/marlo/data/dao/mysql/UserMySQLDAO.java:68` |
| Runtime expansion procedure | `marlo-web/src/main/resources/database/migrations/V2_6_0_20240827_1048__SPPermissions19.sql` |
| Code-level gates | `marlo-web/src/main/java/org/cgiar/ccafs/marlo/action/BaseAction.java` |
| Role parameter keys | `marlo-web/src/main/java/org/cgiar/ccafs/marlo/config/APConstants.java` |
| Admin module screens | `marlo-web/src/main/webapp/WEB-INF/crp/views/admin/` |
| Feedback matrix seed | `marlo-web/src/main/resources/database/migrations/V2_6_0_20250616_1500__InsertFeedbackRolesPermissions.sql` |

---

## 9. Which sections a role can actually reach in AICCRA III

Several permission families in §4 target modules that AICCRA III does not expose. A grant on a disabled module
is inert. Status below is from `custom_parameters` for global unit 47 plus the menu definition in
`marlo-web/src/main/webapp/WEB-INF/global/pages/main-menu.ftl`.

| Module | Reachable on AICCRA III | Why |
|---|---|---|
| Impact Pathway | Yes | `crp_impPath_active = true` |
| Clusters (projects) | Yes | AICCRA renders the `/clusters` namespace instead of `/projects` |
| Funding Sources | Yes | Always visible when logged in |
| Summaries | Yes | Always visible when logged in |
| BI | Yes | `crp_bi_module_active = true` |
| Admin | Yes | `crp_admin_active = true`; requires `admin:canAcess` |
| Innovations | Yes | `innovation_section_active = true` |
| Activities | Yes | `crp_activities_module = true` |
| Publications / Additional Reporting | **No** | Menu entry hardcoded to `visible: false && …` |
| POWB synthesis | **No** | Menu entry hardcoded to `visible: false && …` |
| Annual Report synthesis | **No** | Menu entry hardcoded to `visible: false && …` |
| Quality Assessment (QA) | **No** | Nested under the disabled Synthesis entry |
| TIP | **No** | `tip_section_active = false` |
| AI section | **No** | `ai_section_active = false` |
| Feedback / comments | **No** | `feedback_active = false` |
| Leverages, LP6, COVID impacts, Lessons | **No** | `crp_leverages_module`, `crp_lp6_active`, `crp_lessons_active` are `false` |
| CapDev | **No** | Menu entry renders only for Center global units |

---

## 10. Verification queries

Run these against the target environment to refresh or audit this catalog. Replace `45` with the global unit id
under review. All queries are read-only.

```sql
-- 10.1 Role catalog with population and grant count
SELECT r.id, r.acronym, r.description, r.`order`,
       (SELECT COUNT(DISTINCT ur.user_id) FROM user_roles ur WHERE ur.role_id = r.id)          AS users,
       (SELECT COUNT(DISTINCT rp.permission_id) FROM role_permissions rp WHERE rp.role_id = r.id) AS perms
FROM roles r
WHERE r.global_unit_id = 45
ORDER BY r.`order`, r.id;

-- 10.2 Full role x permission grant list
SELECT r.acronym, p.type, p.permission, p.description
FROM roles r
JOIN role_permissions rp ON rp.role_id = r.id
JOIN permissions p       ON p.id = rp.permission_id
WHERE r.global_unit_id = 45
GROUP BY r.acronym, p.type, p.permission, p.description
ORDER BY r.`order`, p.permission;

-- 10.3 Role id parameters
SELECT cp.global_unit_id, p.`key`, cp.value
FROM custom_parameters cp
JOIN parameters p ON p.id = cp.parameter_id
WHERE cp.global_unit_id = 45 AND p.`key` LIKE '%rol%'
ORDER BY p.`key`;

-- 10.4 Effective, fully expanded permissions for one user (runtime truth)
CALL getPermissions(<user_id>);
SELECT ro_acronym, project_id, permission FROM user_permission ORDER BY ro_acronym, permission;

-- 10.5 Feedback matrix
SELECT r.acronym, fp.name, IFNULL(ct.name, 'ALL') AS cluster_type
FROM feedback_roles_permissions frp
JOIN roles r                ON r.id  = frp.role_id
JOIN feedback_permissions fp ON fp.id = frp.feedback_permission_id
LEFT JOIN cluster_types ct   ON ct.id = frp.cluster_type_id
WHERE frp.global_unit_id = 45
ORDER BY r.`order`, fp.id;

-- 10.6 Data-hygiene audit: duplicate grants and duplicate role assignments
SELECT role_id, permission_id, COUNT(*) c FROM role_permissions
GROUP BY role_id, permission_id HAVING c > 1;
SELECT user_id, role_id, COUNT(*) c FROM user_roles
GROUP BY user_id, role_id HAVING c > 1;
SELECT permission, COUNT(*) c FROM permissions GROUP BY permission HAVING c > 1;

-- 10.7 Which phases are open (a closed phase makes every edit grant inert)
SELECT id, description, year, editable, visible FROM phases
WHERE global_unit_id = 45 ORDER BY year, id;
```

---

## 11. Findings raised while documenting

These are observations, not changes. Each was checked against the whole platform, not only AICCRA, which
reclassified two of them from "possible AICCRA defect" to "platform-wide design".

| # | Finding | Class |
|---|---|---|
| 11.1 | `SL` has users and no permissions | **By design, platform-wide** — document, do not "fix" |
| 11.2 | No uniqueness on the join tables | **Defect** — 15,608 duplicate grant rows platform-wide |
| 11.3 | `PL` and `CL` render the same label | **Defect (cosmetic)** — latent in AICCRA |
| 11.4 | `isAiccra()` matches every global unit id ≥ 45 | **Defect** — Alliance (GU 46) misclassified |
| 11.5 | Grants that can never match at runtime | **Defect** — needs platform-wide check before removal |
| 11.6 | Feedback seed migration diverges from production | **Defect** — migration only |
| 11.7 | AICCRA III has no feedback configuration | **Decision** — blocks enabling the module |
| 11.8 | `PMU` cannot reach the Admin module | **Decision** — separation of duties |
| 11.9 | `CRP-Admin` is granted `crp:*` | **Decision** — security posture |
| 11.10 | Roles configured but unused in AICCRA | **Decision** — see the caveat below |
| 11.11 | `PL` submits, `CL` unsubmits | **By design, platform-wide** — AICCRA never adopted `CL` |
| 11.12 | Wildcard holders invisible to naive audits | **Methodology** — affects every future audit |
| 11.13 | `PC` holds no workflow grant | **Decision** — largest population |

### 11.1 `SL` is a role with users and no permissions — and that is the platform norm

`SL` (Site Integration Leader) has **0** rows in `role_permissions` on both AICCRA global units, yet 13 distinct
users hold it on GU 45. The first reading was that its grants had been lost. Checking every global unit shows
otherwise: **`SL` holds zero grants in 19 of the 21 global units where it exists**, including others that have
users (CCAFS 12 users, CIAT 1 user). Two outliers: `Genebank` (role 345) carries **947** grants with 0 users, and
`CIAT50` (role 137) carries 1 grant with 10 users.

So a permission-less `SL` is the intended shape, not an AICCRA defect: the role marks a person as a site
integration leader for display and selection purposes, and access comes from whatever other role they hold. Two
things still deserve attention:

- The Admin UI gives no hint that the role grants nothing, so an administrator may assign it expecting access.
- `Genebank`'s 947 grants look like an accident and should be reviewed separately.

### 11.2 No uniqueness on the join tables

Neither `user_roles(user_id, role_id)` nor `role_permissions(role_id, permission_id)` has a unique index, and
duplicates exist today:

- `user_roles`: `SL` on GU 45 has **129 rows for 13 users**; `FPM` and `RPM` on GU 47 have 5 and 6 rows for 2 users.
- `role_permissions`: `PMU` holds permission `448` (`crp:{0}:fundingSource:canEdit`) **three times**.
- `permissions`: `crp:{0}:fundingSource:*` exists twice (ids `450` and `451`).

Platform-wide the scale is much larger than the AICCRA sample suggests: **15,608 duplicate rows across 4,192
distinct `(role_id, permission_id)` pairs** in `role_permissions`, and 123 duplicate rows across 13
`(user_id, role_id)` pairs in `user_roles`. Duplicates inflate every count, multiply the rows `getPermissions`
writes into the `user_permission` temp table on every authorization cache miss, and make audits unreliable. This
is the one finding with a measurable runtime cost.

### 11.3 `PL` and `CL` render the same label

`Role.getAiccraAcronymDimanic()` maps `PL` → "Cluster Leader", while `CL`'s `roles.description` is literally
"Cluster Leader". `CrpUsersAction.getUserRoles()` de-duplicates by label, so a user holding both would show one
entry. `CL` has 0 users in AICCRA, which is why this has not surfaced.

### 11.4 `isAiccra()` matches every global unit id ≥ 45

`BaseAction.isAiccra()` returns true for `getCurrentCrp().getId() >= 45`. Global unit **46 is `Alliance`**, not
AICCRA, so Alliance inherits AICCRA role labels and cluster terminology. AICCRA III (47) is matched correctly by
accident of ordering. `Role.getAiccraAcronymDimanic()` uses the safer test (`acronym.contains("AICCRA")`).

### 11.5 Grants that can never match at runtime

`getPermissions` leaves `{1}` unreplaced in four permission families, which then cannot match any Shiro check:

```
crp:<GU>:<phase>:fundingSource:{1}:canEdit
crp:<GU>:<phase>:fundingSource:{1}:budget
crp:<GU>:<phase>:crpIndicators:{1}:*
crp:<GU>:<phase>:synthesisProgram:{1}:*
```

Observed for `FPL`, `RPL` and `SuperAdmin` (harmless for `SuperAdmin`, which holds `*`). Either the branch that
should resolve the id is missing, or these grants are dead.

The four permission rows are held far beyond AICCRA — `fundingSource:{1}:canEdit` (id 438) by 127 roles,
`fundingSource:{1}:budget` (462) by 124, `crpIndicators:{1}:*` (468) by 101, `synthesisProgram:{1}:*` (464) by 87.
Non-resolution was verified only for the AICCRA roles, so a platform-wide check is required before removing
anything: another branch of `getPermissions` may resolve `{1}` for a role type AICCRA does not use.

### 11.6 The feedback seed migration does not match production

`V2_6_0_20250616_1500__InsertFeedbackRolesPermissions.sql` assigns `cluster_type_id` `2` to "regional", `3` to
"country" and `1` to "thematic". Production uses `4` Regional, `1` Country, `2` Theme, matching the
`cluster_types` catalog. The migration is a no-op on databases that lack roles 420-433, which is why the
divergence has not broken anything — but any environment where it does apply gets the wrong matrix.

### 11.7 AICCRA III has no feedback configuration

`feedback_roles_permissions` has rows for global unit 45 only. If the feedback module is enabled on AICCRA III
(`feedback_active` is currently `false`), only `SuperAdmin` will be able to comment until the matrix is seeded
for global unit 47.

### 11.8 `PMU` cannot reach the Admin module

`PMU`/PMC has the widest editorial reach in the platform (`crp:{0}:project:*`) but no `admin:canAcess`, so it
cannot manage users, phases, institutions or partners. Worth confirming with PMU whether this is the intended
separation of duties.

### 11.9 `CRP-Admin` is granted `crp:*`

Besides `crp:{0}:*` (its own global unit), `CRP-Admin` holds the unqualified `crp:*`. Under Shiro wildcard
semantics that matches any global unit, so the role is effectively cross-tenant.

### 11.10 Eight roles are configured but unused in AICCRA

`FM`, `DM`, `CL`, `ML`, `E`, `AR`, `ARW`, `CD` have 0 users on both AICCRA global units while carrying up to 132
grants. They should be either deactivated for AICCRA, or documented as reserved so audits do not read them as
active access.

**Caveat: unused in AICCRA does not mean dead.** `CL` alone has users in nine other global units (`A4NH` 22,
`PIM` 24, `Wheat` 22, `Maize` 21, `Livestock` 21, `FTA` 21, `CCAFS` 7, `WLE` 13, `Rice` 13). Roles are per global
unit, so anything done here must target the AICCRA rows only and must never touch the shared `permissions`
catalog.

### 11.11 `PL` submits and `CL` unsubmits — a deliberate split AICCRA never adopted

`PL` holds `project:{1}:manage:submitProject` and **not** `project:{1}:unsubmitted`. `CL` holds
`project:{1}:unsubmitted` and **not** the submit grant. The first reading was that `CL` looked like a
half-finished duplicate of `PL`; the platform says otherwise. Sampling six global units:

| Global unit | `PL` submit / unsubmit | `CL` submit / unsubmit | `CL` users |
|---|---|---|---:|
| CCAFS | yes / no | no / yes | 7 |
| A4NH | yes / no | no / yes | 22 |
| Wheat | yes / no | no / yes | 22 |
| PIM | yes / no | **yes** / yes | 24 |
| AICCRA | yes / no | no / yes | 0 |
| AICCRA III | yes / no | no / yes | 0 |

The submit/unsubmit split between `PL` and `CL` is therefore an intentional separation of duties, consistent
across the platform (PIM being the one variant), and `CL` is a role in real use elsewhere.

**The AICCRA-specific consequence stands:** because AICCRA never assigns `CL`, no cluster-level role can reopen a
submitted project. That falls to `CP`, a leader role (`FPL`, `FPM`, `RPL`, `RPM`), or `PMU` through its wildcard.
The question for PMU is whether AICCRA should start using `CL`, or whether reopening is meant to sit above the
cluster on purpose.

### 11.12 Wildcard holders are invisible to naive audit queries

Querying `role_permissions` for a specific action under-reports who can perform it, because wildcard grants match
without appearing. Shiro's `WildcardPermission` treats a granted permission with fewer parts than the checked one
as implying the remainder, so `crp:{0}:project:*` (held by `PMU`) implies
`crp:<GU>:<phase>:project:<id>:manage:submitProject`, `…:unsubmitted` and `…:deleteProject` for **every** project,
even though `PMU` holds none of those grants explicitly. The same applies to `CRP-Admin` (`crp:*`) and
`SuperAdmin` (`*`). Any access audit must expand wildcards before drawing conclusions — see the rosters in §13.3,
which are annotated accordingly.

### 11.13 `PC` holds no workflow grant despite being the largest population

`PC` (Cluster Coordinator, 145 users on GU 45 — more than every other role combined) holds neither
`project:{1}:manage:submitProject` nor `project:{1}:unsubmitted`. It can edit its project's sections but cannot
move the project through the workflow in either direction; that depends on `PL`, `CP` or a leader role. Note this
is `PC`, not `CP` — the two acronyms are easy to transpose and their capabilities differ (see the note in §3).

---

## 12. Open questions for validation

> **Status: deferred — 2026-09-01.** The team's position is that the priority for now is to understand the
> configuration as it stands, which §1–§11 and §13 document. These questions are recorded so they are not lost;
> they will be evaluated later. No recommendation is offered here, and none of them blocks the catalog: every
> statement in this document describes the system as configured, not as it should be.

1. `SL` is permission-less by platform convention (§11.1). Should the Admin UI say so, so administrators stop
   assigning it expecting access? And separately, why does `Genebank` carry 947 grants on that role?
2. Should `PMU`/PMC gain read access to the Admin module (§11.8)?
3. Is `CRP-Admin`'s cross-tenant `crp:*` grant intended (§11.9)?
4. Which of the eight unused roles should be retired for AICCRA III (§11.10)?
5. Should the feedback matrix be seeded for global unit 47 before AICCRA III enables the module (§11.7)?
6. AICCRA never assigns `CL`, so no cluster-level role can reopen a submitted project (§11.11). Should AICCRA
   start using `CL`, or is reopening meant to sit above the cluster deliberately?
7. Is it intended that `PC`, the role with by far the most users, holds no workflow grant at all (§11.13)?


---

## 13. Validation results (2026-09-01)

This section records the technical validation of the catalog: every claim in §3 was turned into an assertion and
evaluated against the database. It closes the acceptance criterion *"Permissions are verified against the current
system configuration to ensure accuracy"*. The remaining criterion — *"All roles are validated with system owners"*
— is a human review, tracked as `task.md` T10.

### 13.1 Static assertions — 43 / 43 passed

Assertions cover, per role: exact grant sets for the small roles (`SuperAdmin`, `E`, `G`, `AR`, `ARW`, `CD`, `DM`,
`SL`), wildcard reach (`CRP-Admin`, `PMU`, `RPM`), documented exclusions (`RPL` budgets, `FM` project sections,
`PMU` admin access, `PC` submit/partner/evaluation/delete), pairwise differences (`FPL` vs `FPM`, `PL` vs `PC`,
`CL` vs `PL`), the admin-entry roster, and grant-set parity between global units 45 and 47.

Two claims in the first draft of this catalog were **wrong and have been corrected**:

| Claim as first written | What the database shows | Fixed in |
|---|---|---|
| "`CL` … 58 grants, a subset of `PL`" | `CL` is **not** a subset: 57 grants are shared, but `CL` uniquely holds `project:{1}:unsubmitted`, and it lacks 24 grants `PL` holds. | §3, §11.11 |
| "`FPM` … lacks `impactPathway:{1}:submit` and `powbSynthesis:{1}:manage`" | Imprecise and incomplete. The grant is `powbSynthesis:{1}:manage:canSubmmit`, and `FPM` lacks 13 grants in total while holding one `FPL` does not (`project:{1}:fundingSource:gender`). | §3 |

### 13.2 Runtime validation — `CALL getPermissions(user_id)`

Executed for every role that has users on global unit 47, confirming placeholder expansion end to end.
"Phase-qualified" counts rows carrying the `crp:AICCRA_III:Planning:2026:` prefix; "unresolved `{1}`" counts rows
that reached the runtime with the placeholder still in place (finding §11.5).

| Role | User | Configured grants | Effective rows | Phase-qualified | Project-scoped | Unresolved `{1}` |
|---|---:|---:|---:|---:|---:|---:|
| `SuperAdmin` | 3861 | 3 | 505 | 0 (holds bare `*`) | 0 | 4 |
| `CRP-Admin` | 1082 | 9 | 4 | 4 | 0 | 0 |
| `PMU` | 1 | 65 | 131 | 131 | 1 | 0 |
| `FPL` | 1 | 144 | 170 | 167 | 0 | 4 |
| `RPL` | 1 | 102 | 38 | 35 | 0 | 4 |
| `FPM` | 3059 | 132 | 38 | 38 | 0 | 4 |
| `RPM` | 3059 | 99 | 19 | 19 | 0 | 4 |
| `PC` | 1 | 70 | 70 | 70 | 69 | 0 |

What this confirms:

- **Effective access is driven by attachments, not by grant count.** `RPL` (102 configured) resolved to 38 rows
  for a user with no projects in the region, while `FPL` (144 configured) resolved to 170 rows because each
  program-scoped grant expands once per program the user leads. The `Perms` column in §2 is a ceiling.
- **Project scoping works as documented.** `PC` produced 69 project-scoped rows, all for the single project where
  the user is coordinator (`102093`).
- **Program scoping works as documented.** `FPM` produced `impactPathway:190:canEdit`, with the program id
  resolved from `crp_program_leaders`.
- **Finding §11.5 reproduces consistently.** Exactly four grant families reach the runtime with `{1}` unresolved,
  for every role that holds them.

Roles with no users on either AICCRA global unit (`FM`, `DM`, `CL`, `ML`, `E`, `AR`, `ARW`, `CD`, and `SL` which
has no grants at all) cannot be runtime-validated here; their static grant sets were asserted instead. `SL` also
has 13 users on global unit 45, but that global unit has no editable phase, so `getPermissions` emits nothing
for it.

### 13.3 Validated workflow capability rosters

Who can perform each workflow action. **Explicit** lists roles holding the literal grant; **via wildcard** lists
roles that also match through a broader grant, per §11.12.

| Action | Explicit grant holders | Also allowed via wildcard |
|---|---|---|
| Submit a project | `CP`, `FPL`, `FPM`, `ML`, `PL`, `RPL`, `RPM` | `PMU`, `CRP-Admin`, `SuperAdmin` |
| Unsubmit a project | `CL`, `CP`, `FPL`, `FPM`, `RPL`, `RPM` | `PMU`, `CRP-Admin`, `SuperAdmin` |
| Delete a project | `FPL`, `FPM`, `ML`, `RPL`, `RPM` | `PMU`, `CRP-Admin`, `SuperAdmin` |
| Submit an impact pathway | `CRP-Admin`, `FPL`, `RPL` | `PMU` (holds `impactPathway:*`), `SuperAdmin` |
| Submit the annual report synthesis | `FPL`, `PMU` | `CRP-Admin`, `SuperAdmin` |
| Reach Summaries | `FPL`, `FPM`, `ML`, `RPL`, `RPM` | `PMU` (via `crp_pmu_rol` role-id check in `canAcessSumaries()`), `CRP-Admin`, `SuperAdmin` |

Note that `PL` is absent from the unsubmit roster and `PC` from the submit roster — both by configuration, see
§11.11 and §3.

### 13.4 What is validated, and what is not

The audited database was confirmed to carry the same data as production, so populations, phases and the feedback
matrix are production values rather than a possibly-stale copy. What remains outside the reach of a data audit:

| Item | Why | How to close |
|---|---|---|
| Runtime rows for 9 roles | No users hold them on either AICCRA global unit, so `getPermissions` emits nothing | Nothing to do — the static grant sets were asserted instead (§13.1) |
| Node-grant cascade (`base` implies `base:field`) | Shiro evaluation happens in the running application | Confirmed by code reading (`BaseAction.java:6348`); a UI walkthrough would confirm behaviourally |
| Role purposes and intent | Not derivable from data at all | PMU / QA review (`task.md` T10) |
| Whether each finding should be acted on | A judgement call, not a fact | Triaged in `proposed-backlog.md`; decisions listed there |

A snapshot is still a point in time: any role or permission change after 2026-09-01 invalidates the counts, which
is what §13.5 is for.

### 13.5 Re-runnable assertion suite (SQL)

The checks below were executed against `aiccradb1` on 2026-09-01 and all returned `PASS`. They need only the
`mysql` client, so whoever holds production access can close `task.md` T11 by running them there. Set `@gu` to the
global unit under review.

```sql
SET @gu = 47;

SELECT 'Admin-module entry limited to CRP-Admin and DM' AS check_name,
  CASE WHEN GROUP_CONCAT(DISTINCT r.acronym ORDER BY r.acronym) = 'CRP-Admin,DM'
       THEN 'PASS' ELSE CONCAT('FAIL: ', GROUP_CONCAT(DISTINCT r.acronym ORDER BY r.acronym)) END AS result
FROM roles r JOIN role_permissions rp ON rp.role_id = r.id JOIN permissions p ON p.id = rp.permission_id
WHERE r.global_unit_id = @gu AND p.permission = 'crp:{0}:admin:canAcess';

SELECT 'PMU holds no admin grant' AS check_name,
  CASE WHEN COUNT(*) = 0 THEN 'PASS' ELSE CONCAT('FAIL: ', COUNT(*), ' admin grants') END AS result
FROM roles r JOIN role_permissions rp ON rp.role_id = r.id JOIN permissions p ON p.id = rp.permission_id
WHERE r.global_unit_id = @gu AND r.acronym = 'PMU' AND p.permission LIKE '%admin%';

SELECT 'PMU holds the all-projects wildcard' AS check_name,
  CASE WHEN COUNT(*) = 1 THEN 'PASS' ELSE 'FAIL: wildcard absent' END AS result
FROM roles r JOIN role_permissions rp ON rp.role_id = r.id JOIN permissions p ON p.id = rp.permission_id
WHERE r.global_unit_id = @gu AND r.acronym = 'PMU' AND p.permission = 'crp:{0}:project:*';

SELECT 'CRP-Admin holds the cross-tenant crp:* grant' AS check_name,
  CASE WHEN COUNT(*) >= 1 THEN 'PASS (cross-tenant, see 11.9)' ELSE 'FAIL: absent' END AS result
FROM roles r JOIN role_permissions rp ON rp.role_id = r.id JOIN permissions p ON p.id = rp.permission_id
WHERE r.global_unit_id = @gu AND r.acronym = 'CRP-Admin' AND p.permission = 'crp:*';

SELECT 'SuperAdmin grant set is exactly {*, api:*, superadmin:canEdit}' AS check_name,
  CASE WHEN GROUP_CONCAT(DISTINCT p.permission ORDER BY p.permission) = '*,api:*,superadmin:canEdit'
       THEN 'PASS' ELSE CONCAT('FAIL: ', GROUP_CONCAT(DISTINCT p.permission ORDER BY p.permission)) END AS result
FROM roles r JOIN role_permissions rp ON rp.role_id = r.id JOIN permissions p ON p.id = rp.permission_id
WHERE r.global_unit_id = @gu AND r.acronym = 'SuperAdmin';

SELECT 'SL has zero grants' AS check_name,
  CASE WHEN COUNT(rp.id) = 0 THEN 'PASS (see 11.1)' ELSE CONCAT('FAIL: ', COUNT(rp.id)) END AS result
FROM roles r LEFT JOIN role_permissions rp ON rp.role_id = r.id
WHERE r.global_unit_id = @gu AND r.acronym = 'SL';

SELECT 'RPM is the only role with a budgetByPartners wildcard' AS check_name,
  CASE WHEN GROUP_CONCAT(DISTINCT r.acronym ORDER BY r.acronym) = 'RPM'
       THEN 'PASS' ELSE CONCAT('FAIL: ', GROUP_CONCAT(DISTINCT r.acronym ORDER BY r.acronym)) END AS result
FROM roles r JOIN role_permissions rp ON rp.role_id = r.id JOIN permissions p ON p.id = rp.permission_id
WHERE r.global_unit_id = @gu AND p.permission = 'crp:{0}:project:{1}:budgetByPartners:*';

SELECT 'PL can submit but not unsubmit' AS check_name,
  CASE WHEN SUM(p.permission = 'crp:{0}:project:{1}:manage:submitProject') = 1
        AND SUM(p.permission = 'crp:{0}:project:{1}:unsubmitted')          = 0
       THEN 'PASS (see 11.11)' ELSE 'FAIL' END AS result
FROM roles r JOIN role_permissions rp ON rp.role_id = r.id JOIN permissions p ON p.id = rp.permission_id
WHERE r.global_unit_id = @gu AND r.acronym = 'PL';

SELECT 'CL can unsubmit but not submit' AS check_name,
  CASE WHEN SUM(p.permission = 'crp:{0}:project:{1}:unsubmitted')          = 1
        AND SUM(p.permission = 'crp:{0}:project:{1}:manage:submitProject') = 0
       THEN 'PASS (see 11.11)' ELSE 'FAIL' END AS result
FROM roles r JOIN role_permissions rp ON rp.role_id = r.id JOIN permissions p ON p.id = rp.permission_id
WHERE r.global_unit_id = @gu AND r.acronym = 'CL';

SELECT 'PC cannot submit the project' AS check_name,
  CASE WHEN SUM(p.permission = 'crp:{0}:project:{1}:manage:submitProject') = 0
       THEN 'PASS' ELSE 'FAIL' END AS result
FROM roles r JOIN role_permissions rp ON rp.role_id = r.id JOIN permissions p ON p.id = rp.permission_id
WHERE r.global_unit_id = @gu AND r.acronym = 'PC';

SELECT 'Grant sets are identical on global units 45 and 47' AS check_name,
  CASE WHEN COUNT(*) = 0 THEN 'PASS'
       ELSE CONCAT('FAIL: ', COUNT(*), ' asymmetric grants') END AS result
FROM (
  SELECT g.acronym, g.permission
  FROM (
    SELECT r.acronym, p.permission, r.global_unit_id
      FROM roles r
      JOIN role_permissions rp ON rp.role_id = r.id
      JOIN permissions p       ON p.id = rp.permission_id
     WHERE r.global_unit_id IN (45, 47)
     GROUP BY r.acronym, p.permission, r.global_unit_id
  ) g
  GROUP BY g.acronym, g.permission
  HAVING COUNT(DISTINCT g.global_unit_id) <> 2
) asymmetric;

SELECT 'Roles held by nobody, per global unit' AS check_name,
  CONCAT('GU45: ', IFNULL((SELECT GROUP_CONCAT(r.acronym ORDER BY r.acronym) FROM roles r
           WHERE r.global_unit_id = 45 AND NOT EXISTS (SELECT 1 FROM user_roles ur WHERE ur.role_id = r.id)), 'none'),
         ' | GU47: ', IFNULL((SELECT GROUP_CONCAT(r.acronym ORDER BY r.acronym) FROM roles r
           WHERE r.global_unit_id = 47 AND NOT EXISTS (SELECT 1 FROM user_roles ur WHERE ur.role_id = r.id)), 'none')) AS result;
```

Result on the 2026-09-01 snapshot: every check `PASS`; the population check returned
`GU45: AR,ARW,CD,CL,DM,E,FM,ML | GU47: AR,ARW,CD,CL,CP,DM,E,FM,G,ML,PL,SL`, which is the evidence behind
finding §11.10.

---

## Appendix A — Complete grant list per role (global unit 47, identical to 45)

`type` legend: `0` global unit, `1` project-scoped (`{1}` = project id), `3` program-scoped (`{1}` = `crp_programs` id).


### `E` — External Evaluator  (1 grant)

| type | Permission | Description |
|---|---|---|
| 1 | `crp:{0}:project:{1}:evaluation:canEdit` | Can update project Evaluation |

### `SuperAdmin` — Super Admin  (3 grants)

| type | Permission | Description |
|---|---|---|
| 0 | `*` | Super Admin Permission |
| 0 | `api:*` | full access to REST Api |
| 0 | `superadmin:canEdit` | Super admin can edit |

### `CRP-Admin` — MARLO Administrators  (9 grants)

| type | Permission | Description |
|---|---|---|
| 0 | `crp:*` | Full Crp Access |
| 0 | `crp:{0}:*` | Full privileges on all the platform |
| 0 | `crp:{0}:admin:*` | Can edit crp admin |
| 0 | `crp:{0}:admin:canAcess` | Can view menu |
| 0 | `crp:{0}:sharedProjects:canEdit` | Can edit MALRO shared projects sections |
| 3 | `crp:{0}:impactPathway:{1}:*` | Can edit crp impactPathway |
| 3 | `crp:{0}:impactPathway:{1}:canAcess` | can view ImpactPathway Menu |
| 3 | `crp:{0}:impactPathway:{1}:submit` | Can submit Impact |
| 3 | `crp:{0}:impactPathway:{1}:unsubmitted` | UnSubmit Impact Pathway |

### `PMU` — Program Management Unit  (65 grants)

| type | Permission | Description |
|---|---|---|
| 0 | `crp:{0}:fundingSource:canEdit` | Can Acess |
| 0 | `crp:{0}:fundingSource:w3` | Can add and select w3 funding source |
| 0 | `crp:{0}:impactPathway:*` | All Access in Impact Pathway |
| 0 | `crp:{0}:impactPathway:canAcess` | Can view all impactPathways |
| 0 | `crp:{0}:powbSynthesis:manage:canAcess` | Can Acess to  POWB |
| 0 | `crp:{0}:project:*` | — |
| 0 | `crp:{0}:publication:add` | add publications |
| 0 | `crp:{0}:studies:*` | Full privilegies in studies without Projects |
| 0 | `crp:{0}:studies:add` | Can create Studies without Projects |
| 1 | `crp:{0}:powbSynthesis:{1}:canEdit` | Can edit in POWB Synthesis |
| 1 | `crp:{0}:powbSynthesis:{1}:collaboration:*` | Collaboration full permissions |
| 1 | `crp:{0}:powbSynthesis:{1}:collaboration:canEdit` | Can edit in POWB collaboration |
| 1 | `crp:{0}:powbSynthesis:{1}:crossCuting:canEdit` | Can edit in POWB crossCuting |
| 1 | `crp:{0}:powbSynthesis:{1}:crpStaffing:canEdit` | Can edit POWB Crp Staffing |
| 1 | `crp:{0}:powbSynthesis:{1}:evidences:canEdit` | Can edit in POWB Evidences |
| 1 | `crp:{0}:powbSynthesis:{1}:expectedProgress:canEdit` | Can edit in POWB expectedProgress |
| 1 | `crp:{0}:powbSynthesis:{1}:financialPlan:canEdit` | Can edit POWB Financial Plan |
| 1 | `crp:{0}:powbSynthesis:{1}:manage:canSubmmit` | Can submmit the crp POWB |
| 1 | `crp:{0}:powbSynthesis:{1}:managementGovernance:canEdit` | Can edit in POWB Management And Governance |
| 1 | `crp:{0}:powbSynthesis:{1}:managementRisk:canEdit` | Can edit in POWB Management Risk |
| 1 | `crp:{0}:powbSynthesis:{1}:monitoringEvaluationLearning:canEdit` | Can edit in POWB MEL |
| 1 | `crp:{0}:powbSynthesis:{1}:tocAdjustments:canEdit` | Can edit in POWB ToC Adjustments |
| 1 | `crp:{0}:project:{1}:*` | Can update all the planning section contents |
| 1 | `crp:{0}:project:{1}:budgetByFlagship:bilateral` | Can Edit Project Budget Flagship - Bilateral |
| 1 | `crp:{0}:project:{1}:budgetByFlagship:canEdit` | Can Edit Project Budget Flagship |
| 1 | `crp:{0}:project:{1}:budgetByFlagship:center` | Can Edit Project Budget Flagship - Center |
| 1 | `crp:{0}:project:{1}:budgetByFlagship:w1w2` | Can Edit Project Budget Flagship - W1/W2 |
| 1 | `crp:{0}:project:{1}:budgetByFlagship:w3` | Can Edit Project Budget Flagship - W3 |
| 1 | `crp:{0}:project:{1}:contributionsLP6` | Base Permission to Project Contribution LP6 section in the reporting round. |
| 1 | `crp:{0}:project:{1}:contributionsLP6:canEdit` | Can make changes in the Project Contribution LP6 section section in the reporting round. |
| 1 | `crp:{0}:project:{1}:evaluation:accessCU` | Can view Coordination Unit evaluations |
| 1 | `crp:{0}:project:{1}:evaluation:accessEE` | Can view External evaluator evaluations |
| 1 | `crp:{0}:project:{1}:evaluation:accessFPL` | Can view Flagship Program evaluations |
| 1 | `crp:{0}:project:{1}:evaluation:accessPL` | Can view Project Leaders evaluations |
| 1 | `crp:{0}:project:{1}:evaluation:accessRPL` | Can view Regional Program evaluations |
| 1 | `crp:{0}:project:{1}:evaluation:canEdit` | Can update project Evaluation |
| 1 | `crp:{0}:project:{1}:impacts` | Base Permission to COVID-19 section in the reporting round. |
| 1 | `crp:{0}:project:{1}:impacts:canEdit` | Can make changes in the COVID-19 section section in the reporting round. |
| 1 | `crp:{0}:project:{1}:innovationsList` | Can make changes in the Project Innovation List section in the reporting round. |
| 1 | `crp:{0}:project:{1}:policies` | Base Permission to the Project Policies section in the reporting round. |
| 1 | `crp:{0}:project:{1}:policies:canEdit` | Can make changes in the Project Policies section in the reporting round. |
| 1 | `crp:{0}:project:{1}:policyList` | Can make changes in the Project Policies List section in the reporting round. |
| 1 | `crp:{0}:project:{1}:studies` | Can make changes in the Project Studies List section in the reporting round. |
| 3 | `crp:{0}:publication:{1}:*` | canEdit priveligies |
| 3 | `crp:{0}:reportSynthesis:*` | Can edit in Annual Report Synthesis |
| 3 | `crp:{0}:reportSynthesis:{1}:canEdit` | Can edit in Annual Report Synthesis |
| 3 | `crp:{0}:reportSynthesis:{1}:ccDimensions` | Can edit in Annual Report Synthesis Cross Cutting Dimensions |
| 3 | `crp:{0}:reportSynthesis:{1}:control` | Can edit in Annual Report Synthesis Control Indicator |
| 3 | `crp:{0}:reportSynthesis:{1}:crossPartnerships` | Can edit in Annual Report Synthesis Cross CGIAR Partnerships |
| 3 | `crp:{0}:reportSynthesis:{1}:crpProgress` | Can edit in Annual Report Synthesis Crp Progress |
| 3 | `crp:{0}:reportSynthesis:{1}:efficiency` | Can edit in Annual Report Synthesis Efficiency |
| 3 | `crp:{0}:reportSynthesis:{1}:externalPartnerships` | Can edit in Annual Report Synthesis external Partnerships |
| 3 | `crp:{0}:reportSynthesis:{1}:financial` | Can edit in Annual Report Synthesis Management Governance |
| 3 | `crp:{0}:reportSynthesis:{1}:flagshipProgress` | Can edit in Annual Report Synthesis Flagship Progress |
| 3 | `crp:{0}:reportSynthesis:{1}:fundingUse` | Can edit in Annual Report Synthesis Funding Use |
| 3 | `crp:{0}:reportSynthesis:{1}:governance` | Can edit in Annual Report Synthesis Management Governance |
| 3 | `crp:{0}:reportSynthesis:{1}:influence` | Can edit in Annual Report Synthesis Influence Indicator |
| 3 | `crp:{0}:reportSynthesis:{1}:intellectualAssets` | Can edit in Annual Report 2018 Synthesis Intellectual Assets |
| 3 | `crp:{0}:reportSynthesis:{1}:melia` | Can edit in Annual Report Synthesis MELIA |
| 3 | `crp:{0}:reportSynthesis:{1}:narrative` | Can edit in Annual Report 2018 Synthesis Narratives |
| 3 | `crp:{0}:reportSynthesis:{1}:plannedVariance` | Can edit in Annual Report Synthesis Program Variance |
| 3 | `crp:{0}:reportSynthesis:{1}:risks` | Can edit in Annual Report Synthesis Management Risk |
| 3 | `crp:{0}:reportSynthesis:{1}:srfProgress` | Can edit in Annual Report Synthesis Srf Progress |
| 3 | `crp:{0}:reportSynthesis:{1}:submit` | Can edit in Annual Report Synthesis Srf Progress |
| 3 | `crp:{0}:studies:{1}:canEdit` | Can edit Studies without Projects |

### `FM` — Finance person  (23 grants)

| type | Permission | Description |
|---|---|---|
| 0 | `crp:{0}:fundingSource:*` | Full acess |
| 0 | `crp:{0}:fundingSource:budget` | can create funding source with budget |
| 0 | `crp:{0}:fundingSource:canEdit` | Can Acess |
| 0 | `crp:{0}:fundingSource:w1` | Can add and select w1 funding source |
| 0 | `crp:{0}:fundingSource:w3` | Can add and select w3 funding source |
| 0 | `crp:{0}:fundingSource:{1}:budget` | Can Edit Budget |
| 0 | `crp:{0}:fundingSource:{1}:canEdit` | Cen edit Project Bilateral CoFunded |
| 1 | `crp:{0}:project:{1}:budgetByFlagship:canEdit` | Can Edit Project Budget Flagship |
| 1 | `crp:{0}:project:{1}:budgetByFlagship:w1w2` | Can Edit Project Budget Flagship - W1/W2 |
| 1 | `crp:{0}:project:{1}:budgetByPartners:annualW1w2` | Can update the W1/W2 budget in the project budget section in planning round |
| 1 | `crp:{0}:project:{1}:budgetByPartners:annualW1w2:canEdit` | Can update the W1/W2 budget in the project budget section in reporting round |
| 1 | `crp:{0}:project:{1}:budgetByPartners:canEdit` | Can update the planning project budget section in reporting round |
| 1 | `crp:{0}:project:{1}:budgetByPartners:execution:1` | Can Edit Project Budget Execution W1/W2 |
| 1 | `crp:{0}:project:{1}:budgetByPartners:execution:2` | Can Edit Project Budget Execution W3 |
| 1 | `crp:{0}:project:{1}:budgetByPartners:execution:3` | Can Edit Project Budget Execution Bilateral |
| 1 | `crp:{0}:project:{1}:budgetByPartners:execution:4` | Can Edit Project Budget Execution Center Funds |
| 1 | `crp:{0}:project:{1}:budgetByPartners:execution:canEdit` | Can edit in project budgetByPartners Expenditure |
| 1 | `crp:{0}:project:{1}:fundingSource:w3` | Pl and pc permission to w3 |
| 1 | `crp:{0}:project:{1}:partner:cordinator:canEdit` | Can update the planning project partners cordinator |
| 1 | `crp:{0}:project:{1}:partner:leader:canEdit` | Can update the planning project partners leader |
| 1 | `crp:{0}:project:{1}:partners:canEdit` | Can update some content in project partners section in reporting round. |
| 1 | `crp:{0}:project:{1}:partners:ppa` | Can udpate the PPA partners in reporting round. |
| 1 | `crp:{0}:project:{1}:projectSwitch` | Acces to Switch Acces to the PL |

### `DM` — Data Manager  (4 grants)

| type | Permission | Description |
|---|---|---|
| 0 | `crp:{0}:admin:canAcess` | Can view menu |
| 0 | `crp:{0}:impactPathway:canAcess` | Can view all impactPathways |
| 0 | `crp:{0}:publication:add` | add publications |
| 0 | `crp:{0}:synthesisProgram:{1}:*` | Synthesis by Mog Permission |

### `FPL` — Flagship Leaders  (144 grants)

| type | Permission | Description |
|---|---|---|
| 0 | `crp:{0}:crpIndicators:{1}:*` | Can edit crp Indicators |
| 0 | `crp:{0}:fundingSource:budget` | can create funding source with budget |
| 0 | `crp:{0}:fundingSource:canEdit` | Can Acess |
| 0 | `crp:{0}:fundingSource:w3` | Can add and select w3 funding source |
| 0 | `crp:{0}:fundingSource:{1}:budget` | Can Edit Budget |
| 0 | `crp:{0}:fundingSource:{1}:canEdit` | Cen edit Project Bilateral CoFunded |
| 0 | `crp:{0}:impactPathway:canAcess` | Can view all impactPathways |
| 0 | `crp:{0}:powbSynthesis:manage:canAcess` | Can Acess to  POWB |
| 0 | `crp:{0}:project:synthesis:crpIndicators:*` | Can update everything on CRP Indicatoris in reporting round. |
| 0 | `crp:{0}:project:synthesis:outcomeSynthesis:canEdit` | Can edit on Outcome Synthesis in reporting round. |
| 0 | `crp:{0}:project:synthesis:outcomeSynthesis:fplSynthesis` | Can edit on Flagship Synthesis in reporting round. |
| 0 | `crp:{0}:project:synthesis:synthesisByMog:canEdit` | Can edit on Synthesis by MOG in reporting round. |
| 0 | `crp:{0}:publication:*` | full privelegies publications |
| 0 | `crp:{0}:publication:add` | add publications |
| 0 | `crp:{0}:studies:add` | Can create Studies without Projects |
| 0 | `crp:{0}:summaries:*` | Can update all the summaries section contents |
| 0 | `crp:{0}:synthesisProgram:{1}:*` | Synthesis by Mog Permission |
| 1 | `crp:{0}:powbSynthesis:{1}:canEdit` | Can edit in POWB Synthesis |
| 1 | `crp:{0}:powbSynthesis:{1}:collaboration:*` | Collaboration full permissions |
| 1 | `crp:{0}:powbSynthesis:{1}:collaboration:canEdit` | Can edit in POWB collaboration |
| 1 | `crp:{0}:powbSynthesis:{1}:crossCuting:canEdit` | Can edit in POWB crossCuting |
| 1 | `crp:{0}:powbSynthesis:{1}:evidences:canEdit` | Can edit in POWB Evidences |
| 1 | `crp:{0}:powbSynthesis:{1}:expectedProgress:canEdit` | Can edit in POWB expectedProgress |
| 1 | `crp:{0}:powbSynthesis:{1}:flagshipPlans:canEdit` | Can edit in POWB Flagship Plans |
| 1 | `crp:{0}:powbSynthesis:{1}:manage:canSubmmit` | Can submmit the crp POWB |
| 1 | `crp:{0}:powbSynthesis:{1}:managementGovernance:canEdit` | Can edit in POWB Management And Governance |
| 1 | `crp:{0}:powbSynthesis:{1}:managementRisk:canEdit` | Can edit in POWB Management Risk |
| 1 | `crp:{0}:powbSynthesis:{1}:monitoringEvaluationLearning:canEdit` | Can edit in POWB MEL |
| 1 | `crp:{0}:powbSynthesis:{1}:tocAdjustments:canEdit` | Can edit in POWB ToC Adjustments |
| 1 | `crp:{0}:project:{1}:activities:*` | Can update the Project Activities section in the reporting round. |
| 1 | `crp:{0}:project:{1}:activities:description` | Can update the description of the activities section in the reporting round. |
| 1 | `crp:{0}:project:{1}:budgetByCoAs:canEdit` | Can update the planning project budget section in reporting round |
| 1 | `crp:{0}:project:{1}:budgetByFlagship:bilateral` | Can Edit Project Budget Flagship - Bilateral |
| 1 | `crp:{0}:project:{1}:budgetByFlagship:canEdit` | Can Edit Project Budget Flagship |
| 1 | `crp:{0}:project:{1}:budgetByFlagship:center` | Can Edit Project Budget Flagship - Center |
| 1 | `crp:{0}:project:{1}:budgetByFlagship:w3` | Can Edit Project Budget Flagship - W3 |
| 1 | `crp:{0}:project:{1}:budgetByPartners:canEdit` | Can update the planning project budget section in reporting round |
| 1 | `crp:{0}:project:{1}:budgetByPartners:cofundedNew` | Can Create Project CoFunded |
| 1 | `crp:{0}:project:{1}:caseStudies:*` | Can update the Outcomes Case Studies section in the planning round. |
| 1 | `crp:{0}:project:{1}:ccafsOutcomes:*` | Can update the project ccafs outcome section in reporting round. |
| 1 | `crp:{0}:project:{1}:contributionCrp:*` | Can update everything in the CCAFS outcomes section in the reporting round. |
| 1 | `crp:{0}:project:{1}:contributionCrp:achieved` | Can update the target achieved in ccafs outcomes section in reporting round. |
| 1 | `crp:{0}:project:{1}:contributionCrp:add` | Can add a Project Outcome |
| 1 | `crp:{0}:project:{1}:contributionCrp:canEdit` | Can make changes in the ccafs outcomes section in reporting round. |
| 1 | `crp:{0}:project:{1}:contributionCrp:communications` | Can edit Comunnications |
| 1 | `crp:{0}:project:{1}:contributionCrp:delete` | Can delete a Project Outcome |
| 1 | `crp:{0}:project:{1}:contributionCrp:description` | Can update the expected annual contribution in ccafs outcomes section in reporting round. |
| 1 | `crp:{0}:project:{1}:contributionCrp:gender` | Can update the expected gender contribution in ccafs outcomes section in reporting round. |
| 1 | `crp:{0}:project:{1}:contributionCrp:narrativeGender` | Can update the gender contribution achieved in ccafs outcomes section in reporting round. |
| 1 | `crp:{0}:project:{1}:contributionCrp:narrativeTargets` | Can update the annual contribution achieved in ccafs outcomes section in reporting round. |
| 1 | `crp:{0}:project:{1}:contributionCrp:target` | Can update the target value in ccafs outcomes section in reporting round. |
| 1 | `crp:{0}:project:{1}:contributionCrps:milestones` | Can edit milestones |
| 1 | `crp:{0}:project:{1}:contributionsCrpList:*` | Can view list |
| 1 | `crp:{0}:project:{1}:contributionsLP6` | Base Permission to Project Contribution LP6 section in the reporting round. |
| 1 | `crp:{0}:project:{1}:contributionsLP6:canEdit` | Can make changes in the Project Contribution LP6 section section in the reporting round. |
| 1 | `crp:{0}:project:{1}:deleteProject` | Can use the "Delete project" button in any section in reporting round. |
| 1 | `crp:{0}:project:{1}:deliverable:canEdit` | Can make changes in a particular deliverable in the reporting round. |
| 1 | `crp:{0}:project:{1}:deliverable:main` | Can make changes in the main fields (title, start date) for a particular deliverable in reporting round. |
| 1 | `crp:{0}:project:{1}:deliverable:other` | Can make changes in the rest of the fields that are not part of "main" permission in reporting round. |
| 1 | `crp:{0}:project:{1}:deliverableList:addDeliverable` | Can add new deliverables in the deliverables list section in reporting round. |
| 1 | `crp:{0}:project:{1}:deliverableList:canEdit` | Can make changes in the deliverables list section in reporting round. |
| 1 | `crp:{0}:project:{1}:description:*` | Can update all the reporting section in project description. |
| 1 | `crp:{0}:project:{1}:description:activities` | Can update activites |
| 1 | `crp:{0}:project:{1}:description:annualReportDonor` | Can upload the report to the donor (bilateral project) in reporting round. |
| 1 | `crp:{0}:project:{1}:description:bilateralContract` | Can update the bilateral contract in reporting round. |
| 1 | `crp:{0}:project:{1}:description:endDate` | Can update the field end date in reporting round. |
| 1 | `crp:{0}:project:{1}:description:flagships` | Can update the flagships selections in reporting round. |
| 1 | `crp:{0}:project:{1}:description:managementLiaison` | Can update the ML in reporting round. |
| 1 | `crp:{0}:project:{1}:description:regions` | Can update the regions selections in reporting round. |
| 1 | `crp:{0}:project:{1}:description:startDate` | Can update the field start date in reporting round. |
| 1 | `crp:{0}:project:{1}:description:statusDescription` | Can update the project status in reporting round. |
| 1 | `crp:{0}:project:{1}:description:summary` | Can update the project summary in reporting round. |
| 1 | `crp:{0}:project:{1}:description:title` | Can update the project title in reporting round |
| 1 | `crp:{0}:project:{1}:description:workplan` | Can upload the workplan (ccafs projects) in reporting round. |
| 1 | `crp:{0}:project:{1}:evaluation:accessCU` | Can view Coordination Unit evaluations |
| 1 | `crp:{0}:project:{1}:evaluation:accessEE` | Can view External evaluator evaluations |
| 1 | `crp:{0}:project:{1}:evaluation:accessPL` | Can view Project Leaders evaluations |
| 1 | `crp:{0}:project:{1}:evaluation:accessRPL` | Can view Regional Program evaluations |
| 1 | `crp:{0}:project:{1}:evaluation:canEdit` | Can update project Evaluation |
| 1 | `crp:{0}:project:{1}:expectedStudies:*` | Can edit Expeted Studies |
| 1 | `crp:{0}:project:{1}:fundingSource:w3` | Pl and pc permission to w3 |
| 1 | `crp:{0}:project:{1}:highlights:addHighlight` | Can add new Project Highlights in the reporting round. |
| 1 | `crp:{0}:project:{1}:highlights:canEdit` | Can make changes in the Project Highlights List section in the reporting round. |
| 1 | `crp:{0}:project:{1}:highlights:removeHighlight` | Can remove Project Highlights in the reporting round. |
| 1 | `crp:{0}:project:{1}:impacts` | Base Permission to COVID-19 section in the reporting round. |
| 1 | `crp:{0}:project:{1}:impacts:canEdit` | Can make changes in the COVID-19 section section in the reporting round. |
| 1 | `crp:{0}:project:{1}:innovations:canEdit` | Can make changes in the Project Highlights List section in the reporting round. |
| 1 | `crp:{0}:project:{1}:innovationsList` | Can make changes in the Project Innovation List section in the reporting round. |
| 1 | `crp:{0}:project:{1}:leverages:*` | Can update the Leverages section in the planning round. |
| 1 | `crp:{0}:project:{1}:locations:*` | Can update everything in project locations in reporting round. |
| 1 | `crp:{0}:project:{1}:manage:submitProject` | Can use the "Submit project" button in any section in reporting round. |
| 1 | `crp:{0}:project:{1}:otherContributions:additionalContribution` | Can update the contribuition to another center activity in other contributions section in reporting round. |
| 1 | `crp:{0}:project:{1}:otherContributions:canEdit` | Can make changes in the other contributions section in reporting round. |
| 1 | `crp:{0}:project:{1}:otherContributions:explainAchieved` | Can update the ahieved outcome in other contributions section in reporting round. |
| 1 | `crp:{0}:project:{1}:otherContributions:otherContributionDescription` | Can update the description of the contribution to the indicator in other contributions section in reporting round. |
| 1 | `crp:{0}:project:{1}:otherContributions:otherContributionIndicator` | Can update the indicator in other contributions section un reporting round. |
| 1 | `crp:{0}:project:{1}:otherContributions:otherContributionTarget` | Can update the target in other contributions section in reporting round. |
| 1 | `crp:{0}:project:{1}:outcomes:annualProgress` | Can update annual progress in project outcomes in reporting round. |
| 1 | `crp:{0}:project:{1}:outcomes:canEdit` | Can update project outcomes in reporting round |
| 1 | `crp:{0}:project:{1}:outcomes:communicationEngagement` | Can update communication engagement in project outcomes in reporting round. |
| 1 | `crp:{0}:project:{1}:outcomes:uploadSummary` | Can upload a summary file in project outcomes in reporting round. |
| 1 | `crp:{0}:project:{1}:outcomesPandR:*` | Can update the project project  outcome section in reporting round. |
| 1 | `crp:{0}:project:{1}:outputs:briefSummary` | Can update the actual contribution in overview by mogs section in the reporting round. |
| 1 | `crp:{0}:project:{1}:outputs:canEdit` | Can make changes in the overview by mogs section in the reporting round. |
| 1 | `crp:{0}:project:{1}:outputs:summaryGender` | Can update the actual gender contribution in the overview by mogs section in the reporting round. |
| 1 | `crp:{0}:project:{1}:partner:cordinator:canEdit` | Can update the planning project partners cordinator |
| 1 | `crp:{0}:project:{1}:partner:leader:canEdit` | Can update the planning project partners leader |
| 1 | `crp:{0}:project:{1}:partners:canEdit` | Can update some content in project partners section in reporting round. |
| 1 | `crp:{0}:project:{1}:partners:coordinator` | Can update the project coordinator (PC) in reporting round. |
| 1 | `crp:{0}:project:{1}:partners:leader` | Can update the project leader (PL) in reporting round. |
| 1 | `crp:{0}:project:{1}:partners:ppa` | Can udpate the PPA partners in reporting round. |
| 1 | `crp:{0}:project:{1}:policies` | Base Permission to the Project Policies section in the reporting round. |
| 1 | `crp:{0}:project:{1}:policies:canEdit` | Can make changes in the Project Policies section in the reporting round. |
| 1 | `crp:{0}:project:{1}:policyList` | Can make changes in the Project Policies List section in the reporting round. |
| 1 | `crp:{0}:project:{1}:projectSwitch` | Acces to Switch Acces to the PL |
| 1 | `crp:{0}:project:{1}:safeguards:canEdit` | Can make changes in Safeguards section |
| 1 | `crp:{0}:project:{1}:studies` | Can make changes in the Project Studies List section in the reporting round. |
| 1 | `crp:{0}:project:{1}:unsubmitted` | Ubsumit project permission |
| 3 | `crp:{0}:impactPathway:{1}:*` | Can edit crp impactPathway |
| 3 | `crp:{0}:impactPathway:{1}:canAcess` | can view ImpactPathway Menu |
| 3 | `crp:{0}:impactPathway:{1}:canEdit` | Can submit Impact |
| 3 | `crp:{0}:impactPathway:{1}:submit` | Can submit Impact |
| 3 | `crp:{0}:impactPathway:{1}:unsubmitted` | UnSubmit Impact Pathway |
| 3 | `crp:{0}:publication:{1}:*` | canEdit priveligies |
| 3 | `crp:{0}:reportSynthesis:{1}:canEdit` | Can edit in Annual Report Synthesis |
| 3 | `crp:{0}:reportSynthesis:{1}:ccDimensions` | Can edit in Annual Report Synthesis Cross Cutting Dimensions |
| 3 | `crp:{0}:reportSynthesis:{1}:control` | Can edit in Annual Report Synthesis Control Indicator |
| 3 | `crp:{0}:reportSynthesis:{1}:crossPartnerships` | Can edit in Annual Report Synthesis Cross CGIAR Partnerships |
| 3 | `crp:{0}:reportSynthesis:{1}:crpProgress` | Can edit in Annual Report Synthesis Crp Progress |
| 3 | `crp:{0}:reportSynthesis:{1}:efficiency` | Can edit in Annual Report Synthesis Efficiency |
| 3 | `crp:{0}:reportSynthesis:{1}:externalPartnerships` | Can edit in Annual Report Synthesis external Partnerships |
| 3 | `crp:{0}:reportSynthesis:{1}:financial` | Can edit in Annual Report Synthesis Management Governance |
| 3 | `crp:{0}:reportSynthesis:{1}:flagshipProgress` | Can edit in Annual Report Synthesis Flagship Progress |
| 3 | `crp:{0}:reportSynthesis:{1}:fundingUse` | Can edit in Annual Report Synthesis Funding Use |
| 3 | `crp:{0}:reportSynthesis:{1}:governance` | Can edit in Annual Report Synthesis Management Governance |
| 3 | `crp:{0}:reportSynthesis:{1}:influence` | Can edit in Annual Report Synthesis Influence Indicator |
| 3 | `crp:{0}:reportSynthesis:{1}:intellectualAssets` | Can edit in Annual Report 2018 Synthesis Intellectual Assets |
| 3 | `crp:{0}:reportSynthesis:{1}:melia` | Can edit in Annual Report Synthesis MELIA |
| 3 | `crp:{0}:reportSynthesis:{1}:narrative` | Can edit in Annual Report 2018 Synthesis Narratives |
| 3 | `crp:{0}:reportSynthesis:{1}:plannedVariance` | Can edit in Annual Report Synthesis Program Variance |
| 3 | `crp:{0}:reportSynthesis:{1}:risks` | Can edit in Annual Report Synthesis Management Risk |
| 3 | `crp:{0}:reportSynthesis:{1}:srfProgress` | Can edit in Annual Report Synthesis Srf Progress |
| 3 | `crp:{0}:reportSynthesis:{1}:submit` | Can edit in Annual Report Synthesis Srf Progress |
| 3 | `crp:{0}:studies:{1}:canEdit` | Can edit Studies without Projects |

### `RPL` — Regional Program Leaders  (102 grants)

| type | Permission | Description |
|---|---|---|
| 0 | `crp:{0}:crpIndicators:{1}:*` | Can edit crp Indicators |
| 0 | `crp:{0}:fundingSource:budget` | can create funding source with budget |
| 0 | `crp:{0}:fundingSource:canEdit` | Can Acess |
| 0 | `crp:{0}:fundingSource:w3` | Can add and select w3 funding source |
| 0 | `crp:{0}:fundingSource:{1}:budget` | Can Edit Budget |
| 0 | `crp:{0}:fundingSource:{1}:canEdit` | Cen edit Project Bilateral CoFunded |
| 0 | `crp:{0}:impactPathway:canAcess` | Can view all impactPathways |
| 0 | `crp:{0}:powbSynthesis:manage:canAcess` | Can Acess to  POWB |
| 0 | `crp:{0}:project:synthesis:crpIndicators:*` | Can update everything on CRP Indicatoris in reporting round. |
| 0 | `crp:{0}:project:synthesis:outcomeSynthesis:canEdit` | Can edit on Outcome Synthesis in reporting round. |
| 0 | `crp:{0}:project:synthesis:outcomeSynthesis:rplSynthesis` | Can edit on Regional Synthesis in reporting round. |
| 0 | `crp:{0}:project:synthesis:synthesisByMog:canEdit` | Can edit on Synthesis by MOG in reporting round. |
| 0 | `crp:{0}:publication:*` | full privelegies publications |
| 0 | `crp:{0}:publication:add` | add publications |
| 0 | `crp:{0}:studies:add` | Can create Studies without Projects |
| 0 | `crp:{0}:summaries:*` | Can update all the summaries section contents |
| 0 | `crp:{0}:synthesisProgram:{1}:*` | Synthesis by Mog Permission |
| 1 | `crp:{0}:powbSynthesis:{1}:collaboration:canEdit` | Can edit in POWB collaboration |
| 1 | `crp:{0}:powbSynthesis:{1}:collaboration{2}:effort` | Region Can Edit  |
| 1 | `crp:{0}:project:{1}:activities:*` | Can update the Project Activities section in the reporting round. |
| 1 | `crp:{0}:project:{1}:activities:description` | Can update the description of the activities section in the reporting round. |
| 1 | `crp:{0}:project:{1}:caseStudies:*` | Can update the Outcomes Case Studies section in the planning round. |
| 1 | `crp:{0}:project:{1}:ccafsOutcomes:*` | Can update the project ccafs outcome section in reporting round. |
| 1 | `crp:{0}:project:{1}:contributionCrp:*` | Can update everything in the CCAFS outcomes section in the reporting round. |
| 1 | `crp:{0}:project:{1}:contributionCrp:achieved` | Can update the target achieved in ccafs outcomes section in reporting round. |
| 1 | `crp:{0}:project:{1}:contributionCrp:add` | Can add a Project Outcome |
| 1 | `crp:{0}:project:{1}:contributionCrp:canEdit` | Can make changes in the ccafs outcomes section in reporting round. |
| 1 | `crp:{0}:project:{1}:contributionCrp:communications` | Can edit Comunnications |
| 1 | `crp:{0}:project:{1}:contributionCrp:delete` | Can delete a Project Outcome |
| 1 | `crp:{0}:project:{1}:contributionCrp:description` | Can update the expected annual contribution in ccafs outcomes section in reporting round. |
| 1 | `crp:{0}:project:{1}:contributionCrp:gender` | Can update the expected gender contribution in ccafs outcomes section in reporting round. |
| 1 | `crp:{0}:project:{1}:contributionCrp:narrativeGender` | Can update the gender contribution achieved in ccafs outcomes section in reporting round. |
| 1 | `crp:{0}:project:{1}:contributionCrp:narrativeTargets` | Can update the annual contribution achieved in ccafs outcomes section in reporting round. |
| 1 | `crp:{0}:project:{1}:contributionCrp:target` | Can update the target value in ccafs outcomes section in reporting round. |
| 1 | `crp:{0}:project:{1}:contributionCrps:milestones` | Can edit milestones |
| 1 | `crp:{0}:project:{1}:contributionsCrpList:*` | Can view list |
| 1 | `crp:{0}:project:{1}:contributionsLP6` | Base Permission to Project Contribution LP6 section in the reporting round. |
| 1 | `crp:{0}:project:{1}:contributionsLP6:canEdit` | Can make changes in the Project Contribution LP6 section section in the reporting round. |
| 1 | `crp:{0}:project:{1}:deleteProject` | Can use the "Delete project" button in any section in reporting round. |
| 1 | `crp:{0}:project:{1}:deliverable:canEdit` | Can make changes in a particular deliverable in the reporting round. |
| 1 | `crp:{0}:project:{1}:deliverable:main` | Can make changes in the main fields (title, start date) for a particular deliverable in reporting round. |
| 1 | `crp:{0}:project:{1}:deliverable:other` | Can make changes in the rest of the fields that are not part of "main" permission in reporting round. |
| 1 | `crp:{0}:project:{1}:deliverableList:addDeliverable` | Can add new deliverables in the deliverables list section in reporting round. |
| 1 | `crp:{0}:project:{1}:deliverableList:canEdit` | Can make changes in the deliverables list section in reporting round. |
| 1 | `crp:{0}:project:{1}:description:*` | Can update all the reporting section in project description. |
| 1 | `crp:{0}:project:{1}:description:activities` | Can update activites |
| 1 | `crp:{0}:project:{1}:description:annualReportDonor` | Can upload the report to the donor (bilateral project) in reporting round. |
| 1 | `crp:{0}:project:{1}:description:bilateralContract` | Can update the bilateral contract in reporting round. |
| 1 | `crp:{0}:project:{1}:description:endDate` | Can update the field end date in reporting round. |
| 1 | `crp:{0}:project:{1}:description:flagships` | Can update the flagships selections in reporting round. |
| 1 | `crp:{0}:project:{1}:description:managementLiaison` | Can update the ML in reporting round. |
| 1 | `crp:{0}:project:{1}:description:regions` | Can update the regions selections in reporting round. |
| 1 | `crp:{0}:project:{1}:description:startDate` | Can update the field start date in reporting round. |
| 1 | `crp:{0}:project:{1}:description:statusDescription` | Can update the project status in reporting round. |
| 1 | `crp:{0}:project:{1}:description:summary` | Can update the project summary in reporting round. |
| 1 | `crp:{0}:project:{1}:description:title` | Can update the project title in reporting round |
| 1 | `crp:{0}:project:{1}:description:workplan` | Can upload the workplan (ccafs projects) in reporting round. |
| 1 | `crp:{0}:project:{1}:evaluation:accessEE` | Can view External evaluator evaluations |
| 1 | `crp:{0}:project:{1}:evaluation:accessPL` | Can view Project Leaders evaluations |
| 1 | `crp:{0}:project:{1}:evaluation:canEdit` | Can update project Evaluation |
| 1 | `crp:{0}:project:{1}:expectedStudies:*` | Can edit Expeted Studies |
| 1 | `crp:{0}:project:{1}:fundingSource:w3` | Pl and pc permission to w3 |
| 1 | `crp:{0}:project:{1}:highlights:addHighlight` | Can add new Project Highlights in the reporting round. |
| 1 | `crp:{0}:project:{1}:highlights:canEdit` | Can make changes in the Project Highlights List section in the reporting round. |
| 1 | `crp:{0}:project:{1}:highlights:removeHighlight` | Can remove Project Highlights in the reporting round. |
| 1 | `crp:{0}:project:{1}:impacts` | Base Permission to COVID-19 section in the reporting round. |
| 1 | `crp:{0}:project:{1}:impacts:canEdit` | Can make changes in the COVID-19 section section in the reporting round. |
| 1 | `crp:{0}:project:{1}:innovations:canEdit` | Can make changes in the Project Highlights List section in the reporting round. |
| 1 | `crp:{0}:project:{1}:innovationsList` | Can make changes in the Project Innovation List section in the reporting round. |
| 1 | `crp:{0}:project:{1}:leverages:*` | Can update the Leverages section in the planning round. |
| 1 | `crp:{0}:project:{1}:locations:*` | Can update everything in project locations in reporting round. |
| 1 | `crp:{0}:project:{1}:manage:submitProject` | Can use the "Submit project" button in any section in reporting round. |
| 1 | `crp:{0}:project:{1}:otherContributions:additionalContribution` | Can update the contribuition to another center activity in other contributions section in reporting round. |
| 1 | `crp:{0}:project:{1}:otherContributions:canEdit` | Can make changes in the other contributions section in reporting round. |
| 1 | `crp:{0}:project:{1}:otherContributions:explainAchieved` | Can update the ahieved outcome in other contributions section in reporting round. |
| 1 | `crp:{0}:project:{1}:otherContributions:otherContributionDescription` | Can update the description of the contribution to the indicator in other contributions section in reporting round. |
| 1 | `crp:{0}:project:{1}:otherContributions:otherContributionIndicator` | Can update the indicator in other contributions section un reporting round. |
| 1 | `crp:{0}:project:{1}:otherContributions:otherContributionTarget` | Can update the target in other contributions section in reporting round. |
| 1 | `crp:{0}:project:{1}:outcomes:annualProgress` | Can update annual progress in project outcomes in reporting round. |
| 1 | `crp:{0}:project:{1}:outcomes:canEdit` | Can update project outcomes in reporting round |
| 1 | `crp:{0}:project:{1}:outcomes:communicationEngagement` | Can update communication engagement in project outcomes in reporting round. |
| 1 | `crp:{0}:project:{1}:outcomes:uploadSummary` | Can upload a summary file in project outcomes in reporting round. |
| 1 | `crp:{0}:project:{1}:outcomesPandR:*` | Can update the project project  outcome section in reporting round. |
| 1 | `crp:{0}:project:{1}:outputs:briefSummary` | Can update the actual contribution in overview by mogs section in the reporting round. |
| 1 | `crp:{0}:project:{1}:outputs:canEdit` | Can make changes in the overview by mogs section in the reporting round. |
| 1 | `crp:{0}:project:{1}:outputs:summaryGender` | Can update the actual gender contribution in the overview by mogs section in the reporting round. |
| 1 | `crp:{0}:project:{1}:partner:cordinator:canEdit` | Can update the planning project partners cordinator |
| 1 | `crp:{0}:project:{1}:partner:leader:canEdit` | Can update the planning project partners leader |
| 1 | `crp:{0}:project:{1}:partners:canEdit` | Can update some content in project partners section in reporting round. |
| 1 | `crp:{0}:project:{1}:partners:coordinator` | Can update the project coordinator (PC) in reporting round. |
| 1 | `crp:{0}:project:{1}:partners:leader` | Can update the project leader (PL) in reporting round. |
| 1 | `crp:{0}:project:{1}:partners:ppa` | Can udpate the PPA partners in reporting round. |
| 1 | `crp:{0}:project:{1}:policies` | Base Permission to the Project Policies section in the reporting round. |
| 1 | `crp:{0}:project:{1}:policies:canEdit` | Can make changes in the Project Policies section in the reporting round. |
| 1 | `crp:{0}:project:{1}:policyList` | Can make changes in the Project Policies List section in the reporting round. |
| 1 | `crp:{0}:project:{1}:projectSwitch` | Acces to Switch Acces to the PL |
| 1 | `crp:{0}:project:{1}:studies` | Can make changes in the Project Studies List section in the reporting round. |
| 1 | `crp:{0}:project:{1}:unsubmitted` | Ubsumit project permission |
| 3 | `crp:{0}:impactPathway:{1}:canEdit` | Can submit Impact |
| 3 | `crp:{0}:impactPathway:{1}:submit` | Can submit Impact |
| 3 | `crp:{0}:publication:{1}:*` | canEdit priveligies |
| 3 | `crp:{0}:studies:{1}:canEdit` | Can edit Studies without Projects |

### `CP` — Contact point  (79 grants)

| type | Permission | Description |
|---|---|---|
| 0 | `crp:{0}:crpIndicators:{1}:*` | Can edit crp Indicators |
| 0 | `crp:{0}:fundingSource:budget` | can create funding source with budget |
| 0 | `crp:{0}:fundingSource:canEdit` | Can Acess |
| 0 | `crp:{0}:fundingSource:{1}:budget` | Can Edit Budget |
| 0 | `crp:{0}:fundingSource:{1}:canEdit` | Cen edit Project Bilateral CoFunded |
| 0 | `crp:{0}:impactPathway:canAcess` | Can view all impactPathways |
| 0 | `crp:{0}:project:synthesis:crpIndicators:*` | Can update everything on CRP Indicatoris in reporting round. |
| 0 | `crp:{0}:publication:add` | add publications |
| 1 | `crp:{0}:project:{1}:activities:*` | Can update the Project Activities section in the reporting round. |
| 1 | `crp:{0}:project:{1}:budgetByCoAs:canEdit` | Can update the planning project budget section in reporting round |
| 1 | `crp:{0}:project:{1}:budgetByFlagship:bilateral` | Can Edit Project Budget Flagship - Bilateral |
| 1 | `crp:{0}:project:{1}:budgetByFlagship:canEdit` | Can Edit Project Budget Flagship |
| 1 | `crp:{0}:project:{1}:budgetByFlagship:center` | Can Edit Project Budget Flagship - Center |
| 1 | `crp:{0}:project:{1}:budgetByFlagship:w3` | Can Edit Project Budget Flagship - W3 |
| 1 | `crp:{0}:project:{1}:budgetByPartners:annualBilateral` | Can update the W3/Bilateral budget in the project budget section in reporting round |
| 1 | `crp:{0}:project:{1}:budgetByPartners:canEdit` | Can update the planning project budget section in reporting round |
| 1 | `crp:{0}:project:{1}:budgetByPartners:cofundedNew` | Can Create Project CoFunded |
| 1 | `crp:{0}:project:{1}:budgetByPartners:execution:4:liaison:{2}` | Can Edit Project Budget Execution Center Funds for a specific liaison institution |
| 1 | `crp:{0}:project:{1}:caseStudies:*` | Can update the Outcomes Case Studies section in the planning round. |
| 1 | `crp:{0}:project:{1}:ccafsOutcomes:*` | Can update the project ccafs outcome section in reporting round. |
| 1 | `crp:{0}:project:{1}:contributionCrp:*` | Can update everything in the CCAFS outcomes section in the reporting round. |
| 1 | `crp:{0}:project:{1}:contributionCrp:achieved` | Can update the target achieved in ccafs outcomes section in reporting round. |
| 1 | `crp:{0}:project:{1}:contributionCrp:add` | Can add a Project Outcome |
| 1 | `crp:{0}:project:{1}:contributionCrp:canEdit` | Can make changes in the ccafs outcomes section in reporting round. |
| 1 | `crp:{0}:project:{1}:contributionCrp:communications` | Can edit Comunnications |
| 1 | `crp:{0}:project:{1}:contributionCrp:delete` | Can delete a Project Outcome |
| 1 | `crp:{0}:project:{1}:contributionCrp:description` | Can update the expected annual contribution in ccafs outcomes section in reporting round. |
| 1 | `crp:{0}:project:{1}:contributionCrp:gender` | Can update the expected gender contribution in ccafs outcomes section in reporting round. |
| 1 | `crp:{0}:project:{1}:contributionCrp:narrativeGender` | Can update the gender contribution achieved in ccafs outcomes section in reporting round. |
| 1 | `crp:{0}:project:{1}:contributionCrp:narrativeTargets` | Can update the annual contribution achieved in ccafs outcomes section in reporting round. |
| 1 | `crp:{0}:project:{1}:contributionCrp:target` | Can update the target value in ccafs outcomes section in reporting round. |
| 1 | `crp:{0}:project:{1}:contributionCrps:milestones` | Can edit milestones |
| 1 | `crp:{0}:project:{1}:contributionsCrpList:*` | Can view list |
| 1 | `crp:{0}:project:{1}:contributionsLP6` | Base Permission to Project Contribution LP6 section in the reporting round. |
| 1 | `crp:{0}:project:{1}:contributionsLP6:canEdit` | Can make changes in the Project Contribution LP6 section section in the reporting round. |
| 1 | `crp:{0}:project:{1}:deliverable:canEdit` | Can make changes in a particular deliverable in the reporting round. |
| 1 | `crp:{0}:project:{1}:deliverable:other` | Can make changes in the rest of the fields that are not part of "main" permission in reporting round. |
| 1 | `crp:{0}:project:{1}:deliverableList:addDeliverable` | Can add new deliverables in the deliverables list section in reporting round. |
| 1 | `crp:{0}:project:{1}:deliverableList:canEdit` | Can make changes in the deliverables list section in reporting round. |
| 1 | `crp:{0}:project:{1}:description:*` | Can update all the reporting section in project description. |
| 1 | `crp:{0}:project:{1}:expectedStudies:*` | Can edit Expeted Studies |
| 1 | `crp:{0}:project:{1}:fundingSource:gender` | Permission to change gender |
| 1 | `crp:{0}:project:{1}:fundingSource:{2}:w3` | Cp add and select w3 |
| 1 | `crp:{0}:project:{1}:highlights:addHighlight` | Can add new Project Highlights in the reporting round. |
| 1 | `crp:{0}:project:{1}:highlights:canEdit` | Can make changes in the Project Highlights List section in the reporting round. |
| 1 | `crp:{0}:project:{1}:highlights:removeHighlight` | Can remove Project Highlights in the reporting round. |
| 1 | `crp:{0}:project:{1}:impacts` | Base Permission to COVID-19 section in the reporting round. |
| 1 | `crp:{0}:project:{1}:impacts:canEdit` | Can make changes in the COVID-19 section section in the reporting round. |
| 1 | `crp:{0}:project:{1}:innovations:canEdit` | Can make changes in the Project Highlights List section in the reporting round. |
| 1 | `crp:{0}:project:{1}:innovationsList` | Can make changes in the Project Innovation List section in the reporting round. |
| 1 | `crp:{0}:project:{1}:leverages:*` | Can update the Leverages section in the planning round. |
| 1 | `crp:{0}:project:{1}:locations:*` | Can update everything in project locations in reporting round. |
| 1 | `crp:{0}:project:{1}:manage:submitProject` | Can use the "Submit project" button in any section in reporting round. |
| 1 | `crp:{0}:project:{1}:otherContributions:additionalContribution` | Can update the contribuition to another center activity in other contributions section in reporting round. |
| 1 | `crp:{0}:project:{1}:otherContributions:canEdit` | Can make changes in the other contributions section in reporting round. |
| 1 | `crp:{0}:project:{1}:otherContributions:explainAchieved` | Can update the ahieved outcome in other contributions section in reporting round. |
| 1 | `crp:{0}:project:{1}:otherContributions:otherContributionDescription` | Can update the description of the contribution to the indicator in other contributions section in reporting round. |
| 1 | `crp:{0}:project:{1}:otherContributions:otherContributionIndicator` | Can update the indicator in other contributions section un reporting round. |
| 1 | `crp:{0}:project:{1}:otherContributions:otherContributionTarget` | Can update the target in other contributions section in reporting round. |
| 1 | `crp:{0}:project:{1}:outcomes:annualProgress` | Can update annual progress in project outcomes in reporting round. |
| 1 | `crp:{0}:project:{1}:outcomes:canEdit` | Can update project outcomes in reporting round |
| 1 | `crp:{0}:project:{1}:outcomes:communicationEngagement` | Can update communication engagement in project outcomes in reporting round. |
| 1 | `crp:{0}:project:{1}:outcomes:uploadSummary` | Can upload a summary file in project outcomes in reporting round. |
| 1 | `crp:{0}:project:{1}:outcomesPandR:*` | Can update the project project  outcome section in reporting round. |
| 1 | `crp:{0}:project:{1}:outputs:briefSummary` | Can update the actual contribution in overview by mogs section in the reporting round. |
| 1 | `crp:{0}:project:{1}:outputs:canEdit` | Can make changes in the overview by mogs section in the reporting round. |
| 1 | `crp:{0}:project:{1}:outputs:summaryGender` | Can update the actual gender contribution in the overview by mogs section in the reporting round. |
| 1 | `crp:{0}:project:{1}:partner:cordinator:canEdit` | Can update the planning project partners cordinator |
| 1 | `crp:{0}:project:{1}:partner:leader:canEdit` | Can update the planning project partners leader |
| 1 | `crp:{0}:project:{1}:partners:canEdit` | Can update some content in project partners section in reporting round. |
| 1 | `crp:{0}:project:{1}:partners:coordinator` | Can update the project coordinator (PC) in reporting round. |
| 1 | `crp:{0}:project:{1}:partners:leader` | Can update the project leader (PL) in reporting round. |
| 1 | `crp:{0}:project:{1}:partners:ppa` | Can udpate the PPA partners in reporting round. |
| 1 | `crp:{0}:project:{1}:policies` | Base Permission to the Project Policies section in the reporting round. |
| 1 | `crp:{0}:project:{1}:policies:canEdit` | Can make changes in the Project Policies section in the reporting round. |
| 1 | `crp:{0}:project:{1}:policyList` | Can make changes in the Project Policies List section in the reporting round. |
| 1 | `crp:{0}:project:{1}:studies` | Can make changes in the Project Studies List section in the reporting round. |
| 1 | `crp:{0}:project:{1}:unsubmitted` | Ubsumit project permission |
| 3 | `crp:{0}:publication:{1}:*` | canEdit priveligies |

### `CL` — Cluster Leader  (58 grants)

| type | Permission | Description |
|---|---|---|
| 0 | `crp:{0}:impactPathway:canAcess` | Can view all impactPathways |
| 1 | `crp:{0}:project:{1}:activities:activityProgress` | Can update the status justification of the activities section in the reporting round. |
| 1 | `crp:{0}:project:{1}:activities:activityStatus` | Can update the status of the activities section in the reporting round. |
| 1 | `crp:{0}:project:{1}:activities:addActivity` | Can add new activities in the reporting round. |
| 1 | `crp:{0}:project:{1}:activities:canEdit` | Can make changes in the activities section in the reporting round. |
| 1 | `crp:{0}:project:{1}:activities:endDate` | Can update the end date of the activities section in the reporting round. |
| 1 | `crp:{0}:project:{1}:activities:leader` | Can update the leader of the activities section in the reporting round. |
| 1 | `crp:{0}:project:{1}:budgetByCoAs:canEdit` | Can update the planning project budget section in reporting round |
| 1 | `crp:{0}:project:{1}:budgetByPartners:canEdit` | Can update the planning project budget section in reporting round |
| 1 | `crp:{0}:project:{1}:budgetByPartners:cofunded` | Can select Project Cofunded |
| 1 | `crp:{0}:project:{1}:budgetByPartners:gender` | Can update W1/W2 gender % |
| 1 | `crp:{0}:project:{1}:contributionCrp:*` | Can update everything in the CCAFS outcomes section in the reporting round. |
| 1 | `crp:{0}:project:{1}:contributionCrp:achieved` | Can update the target achieved in ccafs outcomes section in reporting round. |
| 1 | `crp:{0}:project:{1}:contributionCrp:add` | Can add a Project Outcome |
| 1 | `crp:{0}:project:{1}:contributionCrp:canEdit` | Can make changes in the ccafs outcomes section in reporting round. |
| 1 | `crp:{0}:project:{1}:contributionCrp:communications` | Can edit Comunnications |
| 1 | `crp:{0}:project:{1}:contributionCrp:delete` | Can delete a Project Outcome |
| 1 | `crp:{0}:project:{1}:contributionCrp:description` | Can update the expected annual contribution in ccafs outcomes section in reporting round. |
| 1 | `crp:{0}:project:{1}:contributionCrp:gender` | Can update the expected gender contribution in ccafs outcomes section in reporting round. |
| 1 | `crp:{0}:project:{1}:contributionCrp:narrativeGender` | Can update the gender contribution achieved in ccafs outcomes section in reporting round. |
| 1 | `crp:{0}:project:{1}:contributionCrp:narrativeTargets` | Can update the annual contribution achieved in ccafs outcomes section in reporting round. |
| 1 | `crp:{0}:project:{1}:contributionCrp:target` | Can update the target value in ccafs outcomes section in reporting round. |
| 1 | `crp:{0}:project:{1}:contributionCrps:milestones` | Can edit milestones |
| 1 | `crp:{0}:project:{1}:contributionsCrpList:*` | Can view list |
| 1 | `crp:{0}:project:{1}:deliverable:canEdit` | Can make changes in a particular deliverable in the reporting round. |
| 1 | `crp:{0}:project:{1}:deliverable:other` | Can make changes in the rest of the fields that are not part of "main" permission in reporting round. |
| 1 | `crp:{0}:project:{1}:deliverableList:addDeliverable` | Can add new deliverables in the deliverables list section in reporting round. |
| 1 | `crp:{0}:project:{1}:deliverableList:canEdit` | Can make changes in the deliverables list section in reporting round. |
| 1 | `crp:{0}:project:{1}:description:annualReportDonor` | Can upload the report to the donor (bilateral project) in reporting round. |
| 1 | `crp:{0}:project:{1}:description:canEdit` | Can update the project description section in reporting round. |
| 1 | `crp:{0}:project:{1}:description:scale` | Can update scale project |
| 1 | `crp:{0}:project:{1}:description:scope` | Can scope project |
| 1 | `crp:{0}:project:{1}:description:status` | Can update all the reporting section in project description |
| 1 | `crp:{0}:project:{1}:description:statusDescription` | Can update the project status in reporting round. |
| 1 | `crp:{0}:project:{1}:description:summary` | Can update the project summary in reporting round. |
| 1 | `crp:{0}:project:{1}:expectedStudies:*` | Can edit Expeted Studies |
| 1 | `crp:{0}:project:{1}:fundingSource:gender` | Permission to change gender |
| 1 | `crp:{0}:project:{1}:highlights:canEdit` | Can make changes in the Project Highlights List section in the reporting round. |
| 1 | `crp:{0}:project:{1}:highlights:removeHighlight` | Can remove Project Highlights in the reporting round. |
| 1 | `crp:{0}:project:{1}:innovations:canEdit` | Can make changes in the Project Highlights List section in the reporting round. |
| 1 | `crp:{0}:project:{1}:innovationsList` | Can make changes in the Project Innovation List section in the reporting round. |
| 1 | `crp:{0}:project:{1}:locations:*` | Can update everything in project locations in reporting round. |
| 1 | `crp:{0}:project:{1}:otherContributions:additionalContribution` | Can update the contribuition to another center activity in other contributions section in reporting round. |
| 1 | `crp:{0}:project:{1}:otherContributions:canEdit` | Can make changes in the other contributions section in reporting round. |
| 1 | `crp:{0}:project:{1}:otherContributions:explainAchieved` | Can update the ahieved outcome in other contributions section in reporting round. |
| 1 | `crp:{0}:project:{1}:otherContributions:otherContributionDescription` | Can update the description of the contribution to the indicator in other contributions section in reporting round. |
| 1 | `crp:{0}:project:{1}:otherContributions:otherContributionIndicator` | Can update the indicator in other contributions section un reporting round. |
| 1 | `crp:{0}:project:{1}:otherContributions:otherContributionTarget` | Can update the target in other contributions section in reporting round. |
| 1 | `crp:{0}:project:{1}:outcomes:annualProgress` | Can update annual progress in project outcomes in reporting round. |
| 1 | `crp:{0}:project:{1}:outcomes:canEdit` | Can update project outcomes in reporting round |
| 1 | `crp:{0}:project:{1}:outcomes:communicationEngagement` | Can update communication engagement in project outcomes in reporting round. |
| 1 | `crp:{0}:project:{1}:outcomes:uploadSummary` | Can upload a summary file in project outcomes in reporting round. |
| 1 | `crp:{0}:project:{1}:outputs:briefSummary` | Can update the actual contribution in overview by mogs section in the reporting round. |
| 1 | `crp:{0}:project:{1}:outputs:canEdit` | Can make changes in the overview by mogs section in the reporting round. |
| 1 | `crp:{0}:project:{1}:outputs:summaryGender` | Can update the actual gender contribution in the overview by mogs section in the reporting round. |
| 1 | `crp:{0}:project:{1}:partners:canEdit` | Can update some content in project partners section in reporting round. |
| 1 | `crp:{0}:project:{1}:studies` | Can make changes in the Project Studies List section in the reporting round. |
| 1 | `crp:{0}:project:{1}:unsubmitted` | Ubsumit project permission |

### `FPM` — Flagship Manager  (132 grants)

| type | Permission | Description |
|---|---|---|
| 0 | `crp:{0}:crpIndicators:{1}:*` | Can edit crp Indicators |
| 0 | `crp:{0}:fundingSource:budget` | can create funding source with budget |
| 0 | `crp:{0}:fundingSource:canEdit` | Can Acess |
| 0 | `crp:{0}:fundingSource:w3` | Can add and select w3 funding source |
| 0 | `crp:{0}:fundingSource:{1}:budget` | Can Edit Budget |
| 0 | `crp:{0}:fundingSource:{1}:canEdit` | Cen edit Project Bilateral CoFunded |
| 0 | `crp:{0}:impactPathway:canAcess` | Can view all impactPathways |
| 0 | `crp:{0}:powbSynthesis:manage:canAcess` | Can Acess to  POWB |
| 0 | `crp:{0}:project:synthesis:crpIndicators:*` | Can update everything on CRP Indicatoris in reporting round. |
| 0 | `crp:{0}:project:synthesis:outcomeSynthesis:canEdit` | Can edit on Outcome Synthesis in reporting round. |
| 0 | `crp:{0}:project:synthesis:outcomeSynthesis:fplSynthesis` | Can edit on Flagship Synthesis in reporting round. |
| 0 | `crp:{0}:project:synthesis:synthesisByMog:canEdit` | Can edit on Synthesis by MOG in reporting round. |
| 0 | `crp:{0}:publication:*` | full privelegies publications |
| 0 | `crp:{0}:publication:add` | add publications |
| 0 | `crp:{0}:studies:add` | Can create Studies without Projects |
| 0 | `crp:{0}:summaries:*` | Can update all the summaries section contents |
| 0 | `crp:{0}:synthesisProgram:{1}:*` | Synthesis by Mog Permission |
| 1 | `crp:{0}:powbSynthesis:{1}:canEdit` | Can edit in POWB Synthesis |
| 1 | `crp:{0}:powbSynthesis:{1}:collaboration:*` | Collaboration full permissions |
| 1 | `crp:{0}:powbSynthesis:{1}:collaboration:canEdit` | Can edit in POWB collaboration |
| 1 | `crp:{0}:powbSynthesis:{1}:crossCuting:canEdit` | Can edit in POWB crossCuting |
| 1 | `crp:{0}:powbSynthesis:{1}:evidences:canEdit` | Can edit in POWB Evidences |
| 1 | `crp:{0}:powbSynthesis:{1}:expectedProgress:canEdit` | Can edit in POWB expectedProgress |
| 1 | `crp:{0}:powbSynthesis:{1}:flagshipPlans:canEdit` | Can edit in POWB Flagship Plans |
| 1 | `crp:{0}:powbSynthesis:{1}:managementGovernance:canEdit` | Can edit in POWB Management And Governance |
| 1 | `crp:{0}:powbSynthesis:{1}:managementRisk:canEdit` | Can edit in POWB Management Risk |
| 1 | `crp:{0}:powbSynthesis:{1}:monitoringEvaluationLearning:canEdit` | Can edit in POWB MEL |
| 1 | `crp:{0}:powbSynthesis:{1}:tocAdjustments:canEdit` | Can edit in POWB ToC Adjustments |
| 1 | `crp:{0}:project:{1}:activities:*` | Can update the Project Activities section in the reporting round. |
| 1 | `crp:{0}:project:{1}:activities:description` | Can update the description of the activities section in the reporting round. |
| 1 | `crp:{0}:project:{1}:budgetByCoAs:canEdit` | Can update the planning project budget section in reporting round |
| 1 | `crp:{0}:project:{1}:budgetByPartners:canEdit` | Can update the planning project budget section in reporting round |
| 1 | `crp:{0}:project:{1}:budgetByPartners:cofundedNew` | Can Create Project CoFunded |
| 1 | `crp:{0}:project:{1}:caseStudies:*` | Can update the Outcomes Case Studies section in the planning round. |
| 1 | `crp:{0}:project:{1}:ccafsOutcomes:*` | Can update the project ccafs outcome section in reporting round. |
| 1 | `crp:{0}:project:{1}:contributionCrp:*` | Can update everything in the CCAFS outcomes section in the reporting round. |
| 1 | `crp:{0}:project:{1}:contributionCrp:achieved` | Can update the target achieved in ccafs outcomes section in reporting round. |
| 1 | `crp:{0}:project:{1}:contributionCrp:add` | Can add a Project Outcome |
| 1 | `crp:{0}:project:{1}:contributionCrp:canEdit` | Can make changes in the ccafs outcomes section in reporting round. |
| 1 | `crp:{0}:project:{1}:contributionCrp:communications` | Can edit Comunnications |
| 1 | `crp:{0}:project:{1}:contributionCrp:delete` | Can delete a Project Outcome |
| 1 | `crp:{0}:project:{1}:contributionCrp:description` | Can update the expected annual contribution in ccafs outcomes section in reporting round. |
| 1 | `crp:{0}:project:{1}:contributionCrp:gender` | Can update the expected gender contribution in ccafs outcomes section in reporting round. |
| 1 | `crp:{0}:project:{1}:contributionCrp:narrativeGender` | Can update the gender contribution achieved in ccafs outcomes section in reporting round. |
| 1 | `crp:{0}:project:{1}:contributionCrp:narrativeTargets` | Can update the annual contribution achieved in ccafs outcomes section in reporting round. |
| 1 | `crp:{0}:project:{1}:contributionCrp:target` | Can update the target value in ccafs outcomes section in reporting round. |
| 1 | `crp:{0}:project:{1}:contributionCrps:milestones` | Can edit milestones |
| 1 | `crp:{0}:project:{1}:contributionsCrpList:*` | Can view list |
| 1 | `crp:{0}:project:{1}:deleteProject` | Can use the "Delete project" button in any section in reporting round. |
| 1 | `crp:{0}:project:{1}:deliverable:canEdit` | Can make changes in a particular deliverable in the reporting round. |
| 1 | `crp:{0}:project:{1}:deliverable:main` | Can make changes in the main fields (title, start date) for a particular deliverable in reporting round. |
| 1 | `crp:{0}:project:{1}:deliverable:other` | Can make changes in the rest of the fields that are not part of "main" permission in reporting round. |
| 1 | `crp:{0}:project:{1}:deliverableList:addDeliverable` | Can add new deliverables in the deliverables list section in reporting round. |
| 1 | `crp:{0}:project:{1}:deliverableList:canEdit` | Can make changes in the deliverables list section in reporting round. |
| 1 | `crp:{0}:project:{1}:description:*` | Can update all the reporting section in project description. |
| 1 | `crp:{0}:project:{1}:description:activities` | Can update activites |
| 1 | `crp:{0}:project:{1}:description:annualReportDonor` | Can upload the report to the donor (bilateral project) in reporting round. |
| 1 | `crp:{0}:project:{1}:description:bilateralContract` | Can update the bilateral contract in reporting round. |
| 1 | `crp:{0}:project:{1}:description:endDate` | Can update the field end date in reporting round. |
| 1 | `crp:{0}:project:{1}:description:flagships` | Can update the flagships selections in reporting round. |
| 1 | `crp:{0}:project:{1}:description:managementLiaison` | Can update the ML in reporting round. |
| 1 | `crp:{0}:project:{1}:description:regions` | Can update the regions selections in reporting round. |
| 1 | `crp:{0}:project:{1}:description:startDate` | Can update the field start date in reporting round. |
| 1 | `crp:{0}:project:{1}:description:statusDescription` | Can update the project status in reporting round. |
| 1 | `crp:{0}:project:{1}:description:summary` | Can update the project summary in reporting round. |
| 1 | `crp:{0}:project:{1}:description:title` | Can update the project title in reporting round |
| 1 | `crp:{0}:project:{1}:description:workplan` | Can upload the workplan (ccafs projects) in reporting round. |
| 1 | `crp:{0}:project:{1}:evaluation:accessCU` | Can view Coordination Unit evaluations |
| 1 | `crp:{0}:project:{1}:evaluation:accessEE` | Can view External evaluator evaluations |
| 1 | `crp:{0}:project:{1}:evaluation:accessPL` | Can view Project Leaders evaluations |
| 1 | `crp:{0}:project:{1}:evaluation:accessRPL` | Can view Regional Program evaluations |
| 1 | `crp:{0}:project:{1}:evaluation:canEdit` | Can update project Evaluation |
| 1 | `crp:{0}:project:{1}:expectedStudies:*` | Can edit Expeted Studies |
| 1 | `crp:{0}:project:{1}:fundingSource:gender` | Permission to change gender |
| 1 | `crp:{0}:project:{1}:fundingSource:w3` | Pl and pc permission to w3 |
| 1 | `crp:{0}:project:{1}:highlights:addHighlight` | Can add new Project Highlights in the reporting round. |
| 1 | `crp:{0}:project:{1}:highlights:canEdit` | Can make changes in the Project Highlights List section in the reporting round. |
| 1 | `crp:{0}:project:{1}:highlights:removeHighlight` | Can remove Project Highlights in the reporting round. |
| 1 | `crp:{0}:project:{1}:innovations:canEdit` | Can make changes in the Project Highlights List section in the reporting round. |
| 1 | `crp:{0}:project:{1}:innovationsList` | Can make changes in the Project Innovation List section in the reporting round. |
| 1 | `crp:{0}:project:{1}:leverages:*` | Can update the Leverages section in the planning round. |
| 1 | `crp:{0}:project:{1}:locations:*` | Can update everything in project locations in reporting round. |
| 1 | `crp:{0}:project:{1}:manage:submitProject` | Can use the "Submit project" button in any section in reporting round. |
| 1 | `crp:{0}:project:{1}:otherContributions:additionalContribution` | Can update the contribuition to another center activity in other contributions section in reporting round. |
| 1 | `crp:{0}:project:{1}:otherContributions:canEdit` | Can make changes in the other contributions section in reporting round. |
| 1 | `crp:{0}:project:{1}:otherContributions:explainAchieved` | Can update the ahieved outcome in other contributions section in reporting round. |
| 1 | `crp:{0}:project:{1}:otherContributions:otherContributionDescription` | Can update the description of the contribution to the indicator in other contributions section in reporting round. |
| 1 | `crp:{0}:project:{1}:otherContributions:otherContributionIndicator` | Can update the indicator in other contributions section un reporting round. |
| 1 | `crp:{0}:project:{1}:otherContributions:otherContributionTarget` | Can update the target in other contributions section in reporting round. |
| 1 | `crp:{0}:project:{1}:outcomes:annualProgress` | Can update annual progress in project outcomes in reporting round. |
| 1 | `crp:{0}:project:{1}:outcomes:canEdit` | Can update project outcomes in reporting round |
| 1 | `crp:{0}:project:{1}:outcomes:communicationEngagement` | Can update communication engagement in project outcomes in reporting round. |
| 1 | `crp:{0}:project:{1}:outcomes:uploadSummary` | Can upload a summary file in project outcomes in reporting round. |
| 1 | `crp:{0}:project:{1}:outcomesPandR:*` | Can update the project project  outcome section in reporting round. |
| 1 | `crp:{0}:project:{1}:outputs:briefSummary` | Can update the actual contribution in overview by mogs section in the reporting round. |
| 1 | `crp:{0}:project:{1}:outputs:canEdit` | Can make changes in the overview by mogs section in the reporting round. |
| 1 | `crp:{0}:project:{1}:outputs:summaryGender` | Can update the actual gender contribution in the overview by mogs section in the reporting round. |
| 1 | `crp:{0}:project:{1}:partner:cordinator:canEdit` | Can update the planning project partners cordinator |
| 1 | `crp:{0}:project:{1}:partner:leader:canEdit` | Can update the planning project partners leader |
| 1 | `crp:{0}:project:{1}:partners:canEdit` | Can update some content in project partners section in reporting round. |
| 1 | `crp:{0}:project:{1}:partners:coordinator` | Can update the project coordinator (PC) in reporting round. |
| 1 | `crp:{0}:project:{1}:partners:leader` | Can update the project leader (PL) in reporting round. |
| 1 | `crp:{0}:project:{1}:partners:ppa` | Can udpate the PPA partners in reporting round. |
| 1 | `crp:{0}:project:{1}:policies` | Base Permission to the Project Policies section in the reporting round. |
| 1 | `crp:{0}:project:{1}:policies:canEdit` | Can make changes in the Project Policies section in the reporting round. |
| 1 | `crp:{0}:project:{1}:policyList` | Can make changes in the Project Policies List section in the reporting round. |
| 1 | `crp:{0}:project:{1}:projectSwitch` | Acces to Switch Acces to the PL |
| 1 | `crp:{0}:project:{1}:safeguards:canEdit` | Can make changes in Safeguards section |
| 1 | `crp:{0}:project:{1}:studies` | Can make changes in the Project Studies List section in the reporting round. |
| 1 | `crp:{0}:project:{1}:unsubmitted` | Ubsumit project permission |
| 3 | `crp:{0}:impactPathway:{1}:canAcess` | can view ImpactPathway Menu |
| 3 | `crp:{0}:impactPathway:{1}:canEdit` | Can submit Impact |
| 3 | `crp:{0}:publication:{1}:*` | canEdit priveligies |
| 3 | `crp:{0}:reportSynthesis:{1}:canEdit` | Can edit in Annual Report Synthesis |
| 3 | `crp:{0}:reportSynthesis:{1}:ccDimensions` | Can edit in Annual Report Synthesis Cross Cutting Dimensions |
| 3 | `crp:{0}:reportSynthesis:{1}:control` | Can edit in Annual Report Synthesis Control Indicator |
| 3 | `crp:{0}:reportSynthesis:{1}:crossPartnerships` | Can edit in Annual Report Synthesis Cross CGIAR Partnerships |
| 3 | `crp:{0}:reportSynthesis:{1}:crpProgress` | Can edit in Annual Report Synthesis Crp Progress |
| 3 | `crp:{0}:reportSynthesis:{1}:efficiency` | Can edit in Annual Report Synthesis Efficiency |
| 3 | `crp:{0}:reportSynthesis:{1}:externalPartnerships` | Can edit in Annual Report Synthesis external Partnerships |
| 3 | `crp:{0}:reportSynthesis:{1}:financial` | Can edit in Annual Report Synthesis Management Governance |
| 3 | `crp:{0}:reportSynthesis:{1}:flagshipProgress` | Can edit in Annual Report Synthesis Flagship Progress |
| 3 | `crp:{0}:reportSynthesis:{1}:fundingUse` | Can edit in Annual Report Synthesis Funding Use |
| 3 | `crp:{0}:reportSynthesis:{1}:governance` | Can edit in Annual Report Synthesis Management Governance |
| 3 | `crp:{0}:reportSynthesis:{1}:influence` | Can edit in Annual Report Synthesis Influence Indicator |
| 3 | `crp:{0}:reportSynthesis:{1}:intellectualAssets` | Can edit in Annual Report 2018 Synthesis Intellectual Assets |
| 3 | `crp:{0}:reportSynthesis:{1}:melia` | Can edit in Annual Report Synthesis MELIA |
| 3 | `crp:{0}:reportSynthesis:{1}:narrative` | Can edit in Annual Report 2018 Synthesis Narratives |
| 3 | `crp:{0}:reportSynthesis:{1}:plannedVariance` | Can edit in Annual Report Synthesis Program Variance |
| 3 | `crp:{0}:reportSynthesis:{1}:risks` | Can edit in Annual Report Synthesis Management Risk |
| 3 | `crp:{0}:reportSynthesis:{1}:srfProgress` | Can edit in Annual Report Synthesis Srf Progress |
| 3 | `crp:{0}:studies:{1}:canEdit` | Can edit Studies without Projects |

### `SL` — Site Integration Leader  (0 grants)

_No grants in `role_permissions`._

### `ML` — Management Liaison  (90 grants)

| type | Permission | Description |
|---|---|---|
| 0 | `crp:{0}:crpIndicators:{1}:*` | Can edit crp Indicators |
| 0 | `crp:{0}:fundingSource:budget` | can create funding source with budget |
| 0 | `crp:{0}:fundingSource:canEdit` | Can Acess |
| 0 | `crp:{0}:fundingSource:{1}:budget` | Can Edit Budget |
| 0 | `crp:{0}:fundingSource:{1}:canEdit` | Cen edit Project Bilateral CoFunded |
| 0 | `crp:{0}:impactPathway:canAcess` | Can view all impactPathways |
| 0 | `crp:{0}:publication:*` | full privelegies publications |
| 0 | `crp:{0}:summaries:*` | Can update all the summaries section contents |
| 1 | `crp:{0}:project:{1}:activities:*` | Can update the Project Activities section in the reporting round. |
| 1 | `crp:{0}:project:{1}:activities:description` | Can update the description of the activities section in the reporting round. |
| 1 | `crp:{0}:project:{1}:budgetByCoAs:canEdit` | Can update the planning project budget section in reporting round |
| 1 | `crp:{0}:project:{1}:budgetByFlagship:bilateral` | Can Edit Project Budget Flagship - Bilateral |
| 1 | `crp:{0}:project:{1}:budgetByFlagship:canEdit` | Can Edit Project Budget Flagship |
| 1 | `crp:{0}:project:{1}:budgetByFlagship:center` | Can Edit Project Budget Flagship - Center |
| 1 | `crp:{0}:project:{1}:budgetByFlagship:w3` | Can Edit Project Budget Flagship - W3 |
| 1 | `crp:{0}:project:{1}:budgetByPartners:canEdit` | Can update the planning project budget section in reporting round |
| 1 | `crp:{0}:project:{1}:budgetByPartners:cofundedNew` | Can Create Project CoFunded |
| 1 | `crp:{0}:project:{1}:caseStudies:*` | Can update the Outcomes Case Studies section in the planning round. |
| 1 | `crp:{0}:project:{1}:ccafsOutcomes:*` | Can update the project ccafs outcome section in reporting round. |
| 1 | `crp:{0}:project:{1}:contributionCrp:*` | Can update everything in the CCAFS outcomes section in the reporting round. |
| 1 | `crp:{0}:project:{1}:contributionCrp:achieved` | Can update the target achieved in ccafs outcomes section in reporting round. |
| 1 | `crp:{0}:project:{1}:contributionCrp:add` | Can add a Project Outcome |
| 1 | `crp:{0}:project:{1}:contributionCrp:canEdit` | Can make changes in the ccafs outcomes section in reporting round. |
| 1 | `crp:{0}:project:{1}:contributionCrp:communications` | Can edit Comunnications |
| 1 | `crp:{0}:project:{1}:contributionCrp:delete` | Can delete a Project Outcome |
| 1 | `crp:{0}:project:{1}:contributionCrp:description` | Can update the expected annual contribution in ccafs outcomes section in reporting round. |
| 1 | `crp:{0}:project:{1}:contributionCrp:gender` | Can update the expected gender contribution in ccafs outcomes section in reporting round. |
| 1 | `crp:{0}:project:{1}:contributionCrp:narrativeGender` | Can update the gender contribution achieved in ccafs outcomes section in reporting round. |
| 1 | `crp:{0}:project:{1}:contributionCrp:narrativeTargets` | Can update the annual contribution achieved in ccafs outcomes section in reporting round. |
| 1 | `crp:{0}:project:{1}:contributionCrp:target` | Can update the target value in ccafs outcomes section in reporting round. |
| 1 | `crp:{0}:project:{1}:contributionCrps:milestones` | Can edit milestones |
| 1 | `crp:{0}:project:{1}:contributionsCrpList:*` | Can view list |
| 1 | `crp:{0}:project:{1}:contributionsLP6` | Base Permission to Project Contribution LP6 section in the reporting round. |
| 1 | `crp:{0}:project:{1}:contributionsLP6:canEdit` | Can make changes in the Project Contribution LP6 section section in the reporting round. |
| 1 | `crp:{0}:project:{1}:deleteProject` | Can use the "Delete project" button in any section in reporting round. |
| 1 | `crp:{0}:project:{1}:deliverable:canEdit` | Can make changes in a particular deliverable in the reporting round. |
| 1 | `crp:{0}:project:{1}:deliverable:main` | Can make changes in the main fields (title, start date) for a particular deliverable in reporting round. |
| 1 | `crp:{0}:project:{1}:deliverable:other` | Can make changes in the rest of the fields that are not part of "main" permission in reporting round. |
| 1 | `crp:{0}:project:{1}:deliverableList:addDeliverable` | Can add new deliverables in the deliverables list section in reporting round. |
| 1 | `crp:{0}:project:{1}:deliverableList:canEdit` | Can make changes in the deliverables list section in reporting round. |
| 1 | `crp:{0}:project:{1}:description:*` | Can update all the reporting section in project description. |
| 1 | `crp:{0}:project:{1}:description:activities` | Can update activites |
| 1 | `crp:{0}:project:{1}:description:annualReportDonor` | Can upload the report to the donor (bilateral project) in reporting round. |
| 1 | `crp:{0}:project:{1}:description:bilateralContract` | Can update the bilateral contract in reporting round. |
| 1 | `crp:{0}:project:{1}:description:endDate` | Can update the field end date in reporting round. |
| 1 | `crp:{0}:project:{1}:description:flagships` | Can update the flagships selections in reporting round. |
| 1 | `crp:{0}:project:{1}:description:managementLiaison` | Can update the ML in reporting round. |
| 1 | `crp:{0}:project:{1}:description:regions` | Can update the regions selections in reporting round. |
| 1 | `crp:{0}:project:{1}:description:startDate` | Can update the field start date in reporting round. |
| 1 | `crp:{0}:project:{1}:description:statusDescription` | Can update the project status in reporting round. |
| 1 | `crp:{0}:project:{1}:description:summary` | Can update the project summary in reporting round. |
| 1 | `crp:{0}:project:{1}:description:title` | Can update the project title in reporting round |
| 1 | `crp:{0}:project:{1}:description:workplan` | Can upload the workplan (ccafs projects) in reporting round. |
| 1 | `crp:{0}:project:{1}:expectedStudies:*` | Can edit Expeted Studies |
| 1 | `crp:{0}:project:{1}:fundingSource:w3` | Pl and pc permission to w3 |
| 1 | `crp:{0}:project:{1}:highlights:addHighlight` | Can add new Project Highlights in the reporting round. |
| 1 | `crp:{0}:project:{1}:highlights:canEdit` | Can make changes in the Project Highlights List section in the reporting round. |
| 1 | `crp:{0}:project:{1}:highlights:removeHighlight` | Can remove Project Highlights in the reporting round. |
| 1 | `crp:{0}:project:{1}:impacts` | Base Permission to COVID-19 section in the reporting round. |
| 1 | `crp:{0}:project:{1}:impacts:canEdit` | Can make changes in the COVID-19 section section in the reporting round. |
| 1 | `crp:{0}:project:{1}:innovations:canEdit` | Can make changes in the Project Highlights List section in the reporting round. |
| 1 | `crp:{0}:project:{1}:innovationsList` | Can make changes in the Project Innovation List section in the reporting round. |
| 1 | `crp:{0}:project:{1}:leverages:*` | Can update the Leverages section in the planning round. |
| 1 | `crp:{0}:project:{1}:locations:*` | Can update everything in project locations in reporting round. |
| 1 | `crp:{0}:project:{1}:manage:submitProject` | Can use the "Submit project" button in any section in reporting round. |
| 1 | `crp:{0}:project:{1}:otherContributions:additionalContribution` | Can update the contribuition to another center activity in other contributions section in reporting round. |
| 1 | `crp:{0}:project:{1}:otherContributions:canEdit` | Can make changes in the other contributions section in reporting round. |
| 1 | `crp:{0}:project:{1}:otherContributions:explainAchieved` | Can update the ahieved outcome in other contributions section in reporting round. |
| 1 | `crp:{0}:project:{1}:otherContributions:otherContributionDescription` | Can update the description of the contribution to the indicator in other contributions section in reporting round. |
| 1 | `crp:{0}:project:{1}:otherContributions:otherContributionIndicator` | Can update the indicator in other contributions section un reporting round. |
| 1 | `crp:{0}:project:{1}:otherContributions:otherContributionTarget` | Can update the target in other contributions section in reporting round. |
| 1 | `crp:{0}:project:{1}:outcomes:annualProgress` | Can update annual progress in project outcomes in reporting round. |
| 1 | `crp:{0}:project:{1}:outcomes:canEdit` | Can update project outcomes in reporting round |
| 1 | `crp:{0}:project:{1}:outcomes:communicationEngagement` | Can update communication engagement in project outcomes in reporting round. |
| 1 | `crp:{0}:project:{1}:outcomes:uploadSummary` | Can upload a summary file in project outcomes in reporting round. |
| 1 | `crp:{0}:project:{1}:outcomesPandR:*` | Can update the project project  outcome section in reporting round. |
| 1 | `crp:{0}:project:{1}:outputs:briefSummary` | Can update the actual contribution in overview by mogs section in the reporting round. |
| 1 | `crp:{0}:project:{1}:outputs:canEdit` | Can make changes in the overview by mogs section in the reporting round. |
| 1 | `crp:{0}:project:{1}:outputs:summaryGender` | Can update the actual gender contribution in the overview by mogs section in the reporting round. |
| 1 | `crp:{0}:project:{1}:partner:cordinator:canEdit` | Can update the planning project partners cordinator |
| 1 | `crp:{0}:project:{1}:partner:leader:canEdit` | Can update the planning project partners leader |
| 1 | `crp:{0}:project:{1}:partners:canEdit` | Can update some content in project partners section in reporting round. |
| 1 | `crp:{0}:project:{1}:partners:coordinator` | Can update the project coordinator (PC) in reporting round. |
| 1 | `crp:{0}:project:{1}:partners:leader` | Can update the project leader (PL) in reporting round. |
| 1 | `crp:{0}:project:{1}:partners:ppa` | Can udpate the PPA partners in reporting round. |
| 1 | `crp:{0}:project:{1}:policies` | Base Permission to the Project Policies section in the reporting round. |
| 1 | `crp:{0}:project:{1}:policies:canEdit` | Can make changes in the Project Policies section in the reporting round. |
| 1 | `crp:{0}:project:{1}:policyList` | Can make changes in the Project Policies List section in the reporting round. |
| 1 | `crp:{0}:project:{1}:projectSwitch` | Acces to Switch Acces to the PL |
| 1 | `crp:{0}:project:{1}:studies` | Can make changes in the Project Studies List section in the reporting round. |

### `RPM` — Regional Manager  (99 grants)

| type | Permission | Description |
|---|---|---|
| 0 | `crp:{0}:crpIndicators:{1}:*` | Can edit crp Indicators |
| 0 | `crp:{0}:fundingSource:budget` | can create funding source with budget |
| 0 | `crp:{0}:fundingSource:canEdit` | Can Acess |
| 0 | `crp:{0}:fundingSource:w3` | Can add and select w3 funding source |
| 0 | `crp:{0}:fundingSource:{1}:budget` | Can Edit Budget |
| 0 | `crp:{0}:fundingSource:{1}:canEdit` | Cen edit Project Bilateral CoFunded |
| 0 | `crp:{0}:impactPathway:canAcess` | Can view all impactPathways |
| 0 | `crp:{0}:powbSynthesis:manage:canAcess` | Can Acess to  POWB |
| 0 | `crp:{0}:project:synthesis:crpIndicators:*` | Can update everything on CRP Indicatoris in reporting round. |
| 0 | `crp:{0}:project:synthesis:outcomeSynthesis:canEdit` | Can edit on Outcome Synthesis in reporting round. |
| 0 | `crp:{0}:project:synthesis:outcomeSynthesis:rplSynthesis` | Can edit on Regional Synthesis in reporting round. |
| 0 | `crp:{0}:project:synthesis:synthesisByMog:canEdit` | Can edit on Synthesis by MOG in reporting round. |
| 0 | `crp:{0}:publication:*` | full privelegies publications |
| 0 | `crp:{0}:publication:add` | add publications |
| 0 | `crp:{0}:studies:add` | Can create Studies without Projects |
| 0 | `crp:{0}:summaries:*` | Can update all the summaries section contents |
| 0 | `crp:{0}:synthesisProgram:{1}:*` | Synthesis by Mog Permission |
| 1 | `crp:{0}:powbSynthesis:{1}:collaboration:canEdit` | Can edit in POWB collaboration |
| 1 | `crp:{0}:powbSynthesis:{1}:collaboration{2}:effort` | Region Can Edit  |
| 1 | `crp:{0}:project:{1}:activities:*` | Can update the Project Activities section in the reporting round. |
| 1 | `crp:{0}:project:{1}:activities:description` | Can update the description of the activities section in the reporting round. |
| 1 | `crp:{0}:project:{1}:budgetByPartners:*` | Can update everything on project budgets in reporting round |
| 1 | `crp:{0}:project:{1}:budgetByPartners:canEdit` | Can update the planning project budget section in reporting round |
| 1 | `crp:{0}:project:{1}:caseStudies:*` | Can update the Outcomes Case Studies section in the planning round. |
| 1 | `crp:{0}:project:{1}:ccafsOutcomes:*` | Can update the project ccafs outcome section in reporting round. |
| 1 | `crp:{0}:project:{1}:contributionCrp:*` | Can update everything in the CCAFS outcomes section in the reporting round. |
| 1 | `crp:{0}:project:{1}:contributionCrp:achieved` | Can update the target achieved in ccafs outcomes section in reporting round. |
| 1 | `crp:{0}:project:{1}:contributionCrp:add` | Can add a Project Outcome |
| 1 | `crp:{0}:project:{1}:contributionCrp:canEdit` | Can make changes in the ccafs outcomes section in reporting round. |
| 1 | `crp:{0}:project:{1}:contributionCrp:communications` | Can edit Comunnications |
| 1 | `crp:{0}:project:{1}:contributionCrp:delete` | Can delete a Project Outcome |
| 1 | `crp:{0}:project:{1}:contributionCrp:description` | Can update the expected annual contribution in ccafs outcomes section in reporting round. |
| 1 | `crp:{0}:project:{1}:contributionCrp:gender` | Can update the expected gender contribution in ccafs outcomes section in reporting round. |
| 1 | `crp:{0}:project:{1}:contributionCrp:narrativeGender` | Can update the gender contribution achieved in ccafs outcomes section in reporting round. |
| 1 | `crp:{0}:project:{1}:contributionCrp:narrativeTargets` | Can update the annual contribution achieved in ccafs outcomes section in reporting round. |
| 1 | `crp:{0}:project:{1}:contributionCrp:target` | Can update the target value in ccafs outcomes section in reporting round. |
| 1 | `crp:{0}:project:{1}:contributionCrps:milestones` | Can edit milestones |
| 1 | `crp:{0}:project:{1}:contributionsCrpList:*` | Can view list |
| 1 | `crp:{0}:project:{1}:deleteProject` | Can use the "Delete project" button in any section in reporting round. |
| 1 | `crp:{0}:project:{1}:deliverable:canEdit` | Can make changes in a particular deliverable in the reporting round. |
| 1 | `crp:{0}:project:{1}:deliverable:main` | Can make changes in the main fields (title, start date) for a particular deliverable in reporting round. |
| 1 | `crp:{0}:project:{1}:deliverable:other` | Can make changes in the rest of the fields that are not part of "main" permission in reporting round. |
| 1 | `crp:{0}:project:{1}:deliverableList:addDeliverable` | Can add new deliverables in the deliverables list section in reporting round. |
| 1 | `crp:{0}:project:{1}:deliverableList:canEdit` | Can make changes in the deliverables list section in reporting round. |
| 1 | `crp:{0}:project:{1}:description:*` | Can update all the reporting section in project description. |
| 1 | `crp:{0}:project:{1}:description:activities` | Can update activites |
| 1 | `crp:{0}:project:{1}:description:annualReportDonor` | Can upload the report to the donor (bilateral project) in reporting round. |
| 1 | `crp:{0}:project:{1}:description:bilateralContract` | Can update the bilateral contract in reporting round. |
| 1 | `crp:{0}:project:{1}:description:endDate` | Can update the field end date in reporting round. |
| 1 | `crp:{0}:project:{1}:description:flagships` | Can update the flagships selections in reporting round. |
| 1 | `crp:{0}:project:{1}:description:managementLiaison` | Can update the ML in reporting round. |
| 1 | `crp:{0}:project:{1}:description:regions` | Can update the regions selections in reporting round. |
| 1 | `crp:{0}:project:{1}:description:startDate` | Can update the field start date in reporting round. |
| 1 | `crp:{0}:project:{1}:description:statusDescription` | Can update the project status in reporting round. |
| 1 | `crp:{0}:project:{1}:description:summary` | Can update the project summary in reporting round. |
| 1 | `crp:{0}:project:{1}:description:title` | Can update the project title in reporting round |
| 1 | `crp:{0}:project:{1}:description:workplan` | Can upload the workplan (ccafs projects) in reporting round. |
| 1 | `crp:{0}:project:{1}:evaluation:accessEE` | Can view External evaluator evaluations |
| 1 | `crp:{0}:project:{1}:evaluation:accessPL` | Can view Project Leaders evaluations |
| 1 | `crp:{0}:project:{1}:evaluation:canEdit` | Can update project Evaluation |
| 1 | `crp:{0}:project:{1}:expectedStudies:*` | Can edit Expeted Studies |
| 1 | `crp:{0}:project:{1}:fundingSource:w3` | Pl and pc permission to w3 |
| 1 | `crp:{0}:project:{1}:highlights:addHighlight` | Can add new Project Highlights in the reporting round. |
| 1 | `crp:{0}:project:{1}:highlights:canEdit` | Can make changes in the Project Highlights List section in the reporting round. |
| 1 | `crp:{0}:project:{1}:highlights:removeHighlight` | Can remove Project Highlights in the reporting round. |
| 1 | `crp:{0}:project:{1}:innovations:canEdit` | Can make changes in the Project Highlights List section in the reporting round. |
| 1 | `crp:{0}:project:{1}:innovationsList` | Can make changes in the Project Innovation List section in the reporting round. |
| 1 | `crp:{0}:project:{1}:leverages:*` | Can update the Leverages section in the planning round. |
| 1 | `crp:{0}:project:{1}:locations:*` | Can update everything in project locations in reporting round. |
| 1 | `crp:{0}:project:{1}:manage:submitProject` | Can use the "Submit project" button in any section in reporting round. |
| 1 | `crp:{0}:project:{1}:otherContributions:additionalContribution` | Can update the contribuition to another center activity in other contributions section in reporting round. |
| 1 | `crp:{0}:project:{1}:otherContributions:canEdit` | Can make changes in the other contributions section in reporting round. |
| 1 | `crp:{0}:project:{1}:otherContributions:explainAchieved` | Can update the ahieved outcome in other contributions section in reporting round. |
| 1 | `crp:{0}:project:{1}:otherContributions:otherContributionDescription` | Can update the description of the contribution to the indicator in other contributions section in reporting round. |
| 1 | `crp:{0}:project:{1}:otherContributions:otherContributionIndicator` | Can update the indicator in other contributions section un reporting round. |
| 1 | `crp:{0}:project:{1}:otherContributions:otherContributionTarget` | Can update the target in other contributions section in reporting round. |
| 1 | `crp:{0}:project:{1}:outcomes:annualProgress` | Can update annual progress in project outcomes in reporting round. |
| 1 | `crp:{0}:project:{1}:outcomes:canEdit` | Can update project outcomes in reporting round |
| 1 | `crp:{0}:project:{1}:outcomes:communicationEngagement` | Can update communication engagement in project outcomes in reporting round. |
| 1 | `crp:{0}:project:{1}:outcomes:uploadSummary` | Can upload a summary file in project outcomes in reporting round. |
| 1 | `crp:{0}:project:{1}:outcomesPandR:*` | Can update the project project  outcome section in reporting round. |
| 1 | `crp:{0}:project:{1}:outputs:briefSummary` | Can update the actual contribution in overview by mogs section in the reporting round. |
| 1 | `crp:{0}:project:{1}:outputs:canEdit` | Can make changes in the overview by mogs section in the reporting round. |
| 1 | `crp:{0}:project:{1}:outputs:summaryGender` | Can update the actual gender contribution in the overview by mogs section in the reporting round. |
| 1 | `crp:{0}:project:{1}:partner:cordinator:canEdit` | Can update the planning project partners cordinator |
| 1 | `crp:{0}:project:{1}:partner:leader:canEdit` | Can update the planning project partners leader |
| 1 | `crp:{0}:project:{1}:partners:canEdit` | Can update some content in project partners section in reporting round. |
| 1 | `crp:{0}:project:{1}:partners:coordinator` | Can update the project coordinator (PC) in reporting round. |
| 1 | `crp:{0}:project:{1}:partners:leader` | Can update the project leader (PL) in reporting round. |
| 1 | `crp:{0}:project:{1}:partners:ppa` | Can udpate the PPA partners in reporting round. |
| 1 | `crp:{0}:project:{1}:policies` | Base Permission to the Project Policies section in the reporting round. |
| 1 | `crp:{0}:project:{1}:policies:canEdit` | Can make changes in the Project Policies section in the reporting round. |
| 1 | `crp:{0}:project:{1}:policyList` | Can make changes in the Project Policies List section in the reporting round. |
| 1 | `crp:{0}:project:{1}:projectSwitch` | Acces to Switch Acces to the PL |
| 1 | `crp:{0}:project:{1}:studies` | Can make changes in the Project Studies List section in the reporting round. |
| 1 | `crp:{0}:project:{1}:unsubmitted` | Ubsumit project permission |
| 3 | `crp:{0}:impactPathway:{1}:canEdit` | Can submit Impact |
| 3 | `crp:{0}:publication:{1}:*` | canEdit priveligies |
| 3 | `crp:{0}:studies:{1}:canEdit` | Can edit Studies without Projects |

### `PL` — Project leader  (81 grants)

| type | Permission | Description |
|---|---|---|
| 0 | `crp:{0}:impactPathway:canAcess` | Can view all impactPathways |
| 1 | `crp:{0}:project:{1}:activities:activityProgress` | Can update the status justification of the activities section in the reporting round. |
| 1 | `crp:{0}:project:{1}:activities:activityStatus` | Can update the status of the activities section in the reporting round. |
| 1 | `crp:{0}:project:{1}:activities:addActivity` | Can add new activities in the reporting round. |
| 1 | `crp:{0}:project:{1}:activities:canEdit` | Can make changes in the activities section in the reporting round. |
| 1 | `crp:{0}:project:{1}:activities:endDate` | Can update the end date of the activities section in the reporting round. |
| 1 | `crp:{0}:project:{1}:activities:leader` | Can update the leader of the activities section in the reporting round. |
| 1 | `crp:{0}:project:{1}:budgetByCoAs:canEdit` | Can update the planning project budget section in reporting round |
| 1 | `crp:{0}:project:{1}:budgetByFlagship:bilateral` | Can Edit Project Budget Flagship - Bilateral |
| 1 | `crp:{0}:project:{1}:budgetByFlagship:canEdit` | Can Edit Project Budget Flagship |
| 1 | `crp:{0}:project:{1}:budgetByFlagship:center` | Can Edit Project Budget Flagship - Center |
| 1 | `crp:{0}:project:{1}:budgetByFlagship:w3` | Can Edit Project Budget Flagship - W3 |
| 1 | `crp:{0}:project:{1}:budgetByPartners:canEdit` | Can update the planning project budget section in reporting round |
| 1 | `crp:{0}:project:{1}:budgetByPartners:centerFounds` | Can update Center Found |
| 1 | `crp:{0}:project:{1}:budgetByPartners:cofunded` | Can select Project Cofunded |
| 1 | `crp:{0}:project:{1}:budgetByPartners:gender` | Can update W1/W2 gender % |
| 1 | `crp:{0}:project:{1}:caseStudies:*` | Can update the Outcomes Case Studies section in the planning round. |
| 1 | `crp:{0}:project:{1}:ccafsOutcomes:*` | Can update the project ccafs outcome section in reporting round. |
| 1 | `crp:{0}:project:{1}:contributionCrp:*` | Can update everything in the CCAFS outcomes section in the reporting round. |
| 1 | `crp:{0}:project:{1}:contributionCrp:achieved` | Can update the target achieved in ccafs outcomes section in reporting round. |
| 1 | `crp:{0}:project:{1}:contributionCrp:add` | Can add a Project Outcome |
| 1 | `crp:{0}:project:{1}:contributionCrp:canEdit` | Can make changes in the ccafs outcomes section in reporting round. |
| 1 | `crp:{0}:project:{1}:contributionCrp:communications` | Can edit Comunnications |
| 1 | `crp:{0}:project:{1}:contributionCrp:delete` | Can delete a Project Outcome |
| 1 | `crp:{0}:project:{1}:contributionCrp:description` | Can update the expected annual contribution in ccafs outcomes section in reporting round. |
| 1 | `crp:{0}:project:{1}:contributionCrp:gender` | Can update the expected gender contribution in ccafs outcomes section in reporting round. |
| 1 | `crp:{0}:project:{1}:contributionCrp:narrativeGender` | Can update the gender contribution achieved in ccafs outcomes section in reporting round. |
| 1 | `crp:{0}:project:{1}:contributionCrp:narrativeTargets` | Can update the annual contribution achieved in ccafs outcomes section in reporting round. |
| 1 | `crp:{0}:project:{1}:contributionCrp:target` | Can update the target value in ccafs outcomes section in reporting round. |
| 1 | `crp:{0}:project:{1}:contributionCrps:milestones` | Can edit milestones |
| 1 | `crp:{0}:project:{1}:contributionsCrpList:*` | Can view list |
| 1 | `crp:{0}:project:{1}:contributionsLP6` | Base Permission to Project Contribution LP6 section in the reporting round. |
| 1 | `crp:{0}:project:{1}:contributionsLP6:canEdit` | Can make changes in the Project Contribution LP6 section section in the reporting round. |
| 1 | `crp:{0}:project:{1}:deliverable:canEdit` | Can make changes in a particular deliverable in the reporting round. |
| 1 | `crp:{0}:project:{1}:deliverable:other` | Can make changes in the rest of the fields that are not part of "main" permission in reporting round. |
| 1 | `crp:{0}:project:{1}:deliverableList:addDeliverable` | Can add new deliverables in the deliverables list section in reporting round. |
| 1 | `crp:{0}:project:{1}:deliverableList:canEdit` | Can make changes in the deliverables list section in reporting round. |
| 1 | `crp:{0}:project:{1}:description:annualReportDonor` | Can upload the report to the donor (bilateral project) in reporting round. |
| 1 | `crp:{0}:project:{1}:description:canEdit` | Can update the project description section in reporting round. |
| 1 | `crp:{0}:project:{1}:description:scale` | Can update scale project |
| 1 | `crp:{0}:project:{1}:description:scope` | Can scope project |
| 1 | `crp:{0}:project:{1}:description:status` | Can update all the reporting section in project description |
| 1 | `crp:{0}:project:{1}:description:statusDescription` | Can update the project status in reporting round. |
| 1 | `crp:{0}:project:{1}:description:summary` | Can update the project summary in reporting round. |
| 1 | `crp:{0}:project:{1}:evaluation:canEdit` | Can update project Evaluation |
| 1 | `crp:{0}:project:{1}:expectedStudies:*` | Can edit Expeted Studies |
| 1 | `crp:{0}:project:{1}:fundingSource:gender` | Permission to change gender |
| 1 | `crp:{0}:project:{1}:fundingSource:w3` | Pl and pc permission to w3 |
| 1 | `crp:{0}:project:{1}:highlights:addHighlight` | Can add new Project Highlights in the reporting round. |
| 1 | `crp:{0}:project:{1}:highlights:canEdit` | Can make changes in the Project Highlights List section in the reporting round. |
| 1 | `crp:{0}:project:{1}:highlights:removeHighlight` | Can remove Project Highlights in the reporting round. |
| 1 | `crp:{0}:project:{1}:impacts` | Base Permission to COVID-19 section in the reporting round. |
| 1 | `crp:{0}:project:{1}:impacts:canEdit` | Can make changes in the COVID-19 section section in the reporting round. |
| 1 | `crp:{0}:project:{1}:innovations:canEdit` | Can make changes in the Project Highlights List section in the reporting round. |
| 1 | `crp:{0}:project:{1}:innovationsList` | Can make changes in the Project Innovation List section in the reporting round. |
| 1 | `crp:{0}:project:{1}:leverages:*` | Can update the Leverages section in the planning round. |
| 1 | `crp:{0}:project:{1}:locations:*` | Can update everything in project locations in reporting round. |
| 1 | `crp:{0}:project:{1}:manage:submitProject` | Can use the "Submit project" button in any section in reporting round. |
| 1 | `crp:{0}:project:{1}:otherContributions:additionalContribution` | Can update the contribuition to another center activity in other contributions section in reporting round. |
| 1 | `crp:{0}:project:{1}:otherContributions:canEdit` | Can make changes in the other contributions section in reporting round. |
| 1 | `crp:{0}:project:{1}:otherContributions:explainAchieved` | Can update the ahieved outcome in other contributions section in reporting round. |
| 1 | `crp:{0}:project:{1}:otherContributions:otherContributionDescription` | Can update the description of the contribution to the indicator in other contributions section in reporting round. |
| 1 | `crp:{0}:project:{1}:otherContributions:otherContributionIndicator` | Can update the indicator in other contributions section un reporting round. |
| 1 | `crp:{0}:project:{1}:otherContributions:otherContributionTarget` | Can update the target in other contributions section in reporting round. |
| 1 | `crp:{0}:project:{1}:outcomes:annualProgress` | Can update annual progress in project outcomes in reporting round. |
| 1 | `crp:{0}:project:{1}:outcomes:canEdit` | Can update project outcomes in reporting round |
| 1 | `crp:{0}:project:{1}:outcomes:communicationEngagement` | Can update communication engagement in project outcomes in reporting round. |
| 1 | `crp:{0}:project:{1}:outcomes:uploadSummary` | Can upload a summary file in project outcomes in reporting round. |
| 1 | `crp:{0}:project:{1}:outcomesPandR:*` | Can update the project project  outcome section in reporting round. |
| 1 | `crp:{0}:project:{1}:outputs:briefSummary` | Can update the actual contribution in overview by mogs section in the reporting round. |
| 1 | `crp:{0}:project:{1}:outputs:canEdit` | Can make changes in the overview by mogs section in the reporting round. |
| 1 | `crp:{0}:project:{1}:outputs:summaryGender` | Can update the actual gender contribution in the overview by mogs section in the reporting round. |
| 1 | `crp:{0}:project:{1}:partner:cordinator:canEdit` | Can update the planning project partners cordinator |
| 1 | `crp:{0}:project:{1}:partners:canEdit` | Can update some content in project partners section in reporting round. |
| 1 | `crp:{0}:project:{1}:partners:coordinator` | Can update the project coordinator (PC) in reporting round. |
| 1 | `crp:{0}:project:{1}:partners:ppa` | Can udpate the PPA partners in reporting round. |
| 1 | `crp:{0}:project:{1}:policies` | Base Permission to the Project Policies section in the reporting round. |
| 1 | `crp:{0}:project:{1}:policies:canEdit` | Can make changes in the Project Policies section in the reporting round. |
| 1 | `crp:{0}:project:{1}:policyList` | Can make changes in the Project Policies List section in the reporting round. |
| 1 | `crp:{0}:project:{1}:safeguards:canEdit` | Can make changes in Safeguards section |
| 1 | `crp:{0}:project:{1}:studies` | Can make changes in the Project Studies List section in the reporting round. |

### `PC` — Project coordinator  (70 grants)

| type | Permission | Description |
|---|---|---|
| 0 | `crp:{0}:impactPathway:canAcess` | Can view all impactPathways |
| 1 | `crp:{0}:project:{1}:activities:activityProgress` | Can update the status justification of the activities section in the reporting round. |
| 1 | `crp:{0}:project:{1}:activities:activityStatus` | Can update the status of the activities section in the reporting round. |
| 1 | `crp:{0}:project:{1}:activities:addActivity` | Can add new activities in the reporting round. |
| 1 | `crp:{0}:project:{1}:activities:canEdit` | Can make changes in the activities section in the reporting round. |
| 1 | `crp:{0}:project:{1}:activities:endDate` | Can update the end date of the activities section in the reporting round. |
| 1 | `crp:{0}:project:{1}:activities:leader` | Can update the leader of the activities section in the reporting round. |
| 1 | `crp:{0}:project:{1}:budgetByCoAs:canEdit` | Can update the planning project budget section in reporting round |
| 1 | `crp:{0}:project:{1}:budgetByPartners:canEdit` | Can update the planning project budget section in reporting round |
| 1 | `crp:{0}:project:{1}:budgetByPartners:cofunded` | Can select Project Cofunded |
| 1 | `crp:{0}:project:{1}:budgetByPartners:gender` | Can update W1/W2 gender % |
| 1 | `crp:{0}:project:{1}:caseStudies:*` | Can update the Outcomes Case Studies section in the planning round. |
| 1 | `crp:{0}:project:{1}:ccafsOutcomes:*` | Can update the project ccafs outcome section in reporting round. |
| 1 | `crp:{0}:project:{1}:contributionCrp:*` | Can update everything in the CCAFS outcomes section in the reporting round. |
| 1 | `crp:{0}:project:{1}:contributionCrp:achieved` | Can update the target achieved in ccafs outcomes section in reporting round. |
| 1 | `crp:{0}:project:{1}:contributionCrp:add` | Can add a Project Outcome |
| 1 | `crp:{0}:project:{1}:contributionCrp:canEdit` | Can make changes in the ccafs outcomes section in reporting round. |
| 1 | `crp:{0}:project:{1}:contributionCrp:communications` | Can edit Comunnications |
| 1 | `crp:{0}:project:{1}:contributionCrp:delete` | Can delete a Project Outcome |
| 1 | `crp:{0}:project:{1}:contributionCrp:description` | Can update the expected annual contribution in ccafs outcomes section in reporting round. |
| 1 | `crp:{0}:project:{1}:contributionCrp:gender` | Can update the expected gender contribution in ccafs outcomes section in reporting round. |
| 1 | `crp:{0}:project:{1}:contributionCrp:narrativeGender` | Can update the gender contribution achieved in ccafs outcomes section in reporting round. |
| 1 | `crp:{0}:project:{1}:contributionCrp:narrativeTargets` | Can update the annual contribution achieved in ccafs outcomes section in reporting round. |
| 1 | `crp:{0}:project:{1}:contributionCrp:target` | Can update the target value in ccafs outcomes section in reporting round. |
| 1 | `crp:{0}:project:{1}:contributionCrps:milestones` | Can edit milestones |
| 1 | `crp:{0}:project:{1}:contributionsCrpList:*` | Can view list |
| 1 | `crp:{0}:project:{1}:contributionsLP6` | Base Permission to Project Contribution LP6 section in the reporting round. |
| 1 | `crp:{0}:project:{1}:contributionsLP6:canEdit` | Can make changes in the Project Contribution LP6 section section in the reporting round. |
| 1 | `crp:{0}:project:{1}:deliverable:canEdit` | Can make changes in a particular deliverable in the reporting round. |
| 1 | `crp:{0}:project:{1}:deliverable:other` | Can make changes in the rest of the fields that are not part of "main" permission in reporting round. |
| 1 | `crp:{0}:project:{1}:deliverableList:addDeliverable` | Can add new deliverables in the deliverables list section in reporting round. |
| 1 | `crp:{0}:project:{1}:deliverableList:canEdit` | Can make changes in the deliverables list section in reporting round. |
| 1 | `crp:{0}:project:{1}:description:annualReportDonor` | Can upload the report to the donor (bilateral project) in reporting round. |
| 1 | `crp:{0}:project:{1}:description:canEdit` | Can update the project description section in reporting round. |
| 1 | `crp:{0}:project:{1}:description:scale` | Can update scale project |
| 1 | `crp:{0}:project:{1}:description:scope` | Can scope project |
| 1 | `crp:{0}:project:{1}:description:status` | Can update all the reporting section in project description |
| 1 | `crp:{0}:project:{1}:description:summary` | Can update the project summary in reporting round. |
| 1 | `crp:{0}:project:{1}:expectedStudies:*` | Can edit Expeted Studies |
| 1 | `crp:{0}:project:{1}:fundingSource:gender` | Permission to change gender |
| 1 | `crp:{0}:project:{1}:fundingSource:w3` | Pl and pc permission to w3 |
| 1 | `crp:{0}:project:{1}:highlights:addHighlight` | Can add new Project Highlights in the reporting round. |
| 1 | `crp:{0}:project:{1}:highlights:canEdit` | Can make changes in the Project Highlights List section in the reporting round. |
| 1 | `crp:{0}:project:{1}:highlights:removeHighlight` | Can remove Project Highlights in the reporting round. |
| 1 | `crp:{0}:project:{1}:impacts` | Base Permission to COVID-19 section in the reporting round. |
| 1 | `crp:{0}:project:{1}:impacts:canEdit` | Can make changes in the COVID-19 section section in the reporting round. |
| 1 | `crp:{0}:project:{1}:innovations:canEdit` | Can make changes in the Project Highlights List section in the reporting round. |
| 1 | `crp:{0}:project:{1}:innovationsList` | Can make changes in the Project Innovation List section in the reporting round. |
| 1 | `crp:{0}:project:{1}:leverages:*` | Can update the Leverages section in the planning round. |
| 1 | `crp:{0}:project:{1}:locations:*` | Can update everything in project locations in reporting round. |
| 1 | `crp:{0}:project:{1}:otherContributions:additionalContribution` | Can update the contribuition to another center activity in other contributions section in reporting round. |
| 1 | `crp:{0}:project:{1}:otherContributions:canEdit` | Can make changes in the other contributions section in reporting round. |
| 1 | `crp:{0}:project:{1}:otherContributions:explainAchieved` | Can update the ahieved outcome in other contributions section in reporting round. |
| 1 | `crp:{0}:project:{1}:otherContributions:otherContributionDescription` | Can update the description of the contribution to the indicator in other contributions section in reporting round. |
| 1 | `crp:{0}:project:{1}:otherContributions:otherContributionIndicator` | Can update the indicator in other contributions section un reporting round. |
| 1 | `crp:{0}:project:{1}:otherContributions:otherContributionTarget` | Can update the target in other contributions section in reporting round. |
| 1 | `crp:{0}:project:{1}:outcomes:annualProgress` | Can update annual progress in project outcomes in reporting round. |
| 1 | `crp:{0}:project:{1}:outcomes:canEdit` | Can update project outcomes in reporting round |
| 1 | `crp:{0}:project:{1}:outcomes:communicationEngagement` | Can update communication engagement in project outcomes in reporting round. |
| 1 | `crp:{0}:project:{1}:outcomes:uploadSummary` | Can upload a summary file in project outcomes in reporting round. |
| 1 | `crp:{0}:project:{1}:outcomesPandR:*` | Can update the project project  outcome section in reporting round. |
| 1 | `crp:{0}:project:{1}:outputs:briefSummary` | Can update the actual contribution in overview by mogs section in the reporting round. |
| 1 | `crp:{0}:project:{1}:outputs:canEdit` | Can make changes in the overview by mogs section in the reporting round. |
| 1 | `crp:{0}:project:{1}:outputs:summaryGender` | Can update the actual gender contribution in the overview by mogs section in the reporting round. |
| 1 | `crp:{0}:project:{1}:partners:canEdit` | Can update some content in project partners section in reporting round. |
| 1 | `crp:{0}:project:{1}:policies` | Base Permission to the Project Policies section in the reporting round. |
| 1 | `crp:{0}:project:{1}:policies:canEdit` | Can make changes in the Project Policies section in the reporting round. |
| 1 | `crp:{0}:project:{1}:policyList` | Can make changes in the Project Policies List section in the reporting round. |
| 1 | `crp:{0}:project:{1}:safeguards:canEdit` | Can make changes in Safeguards section |
| 1 | `crp:{0}:project:{1}:studies` | Can make changes in the Project Studies List section in the reporting round. |

### `G` — Guest  (1 grant)

| type | Permission | Description |
|---|---|---|
| 0 | `crp:{0}:impactPathway:canAcess` | Can view all impactPathways |

### `AR` — API Read  (1 grant)

| type | Permission | Description |
|---|---|---|
| 0 | `api:*:read` | Read-only access to all REST Api services |

### `ARW` — API Read-Write  (4 grants)

| type | Permission | Description |
|---|---|---|
| 0 | `api:*:create` | Create access to all REST Api services |
| 0 | `api:*:delete` | Delete access to all REST Api services |
| 0 | `api:*:read` | Read-only access to all REST Api services |
| 0 | `api:*:update` | Update access to all REST Api services |

### `CD` — CapDev Manager  (2 grants)

| type | Permission | Description |
|---|---|---|
| 0 | `crp:{0}:capDev:*` | Can edit CapDev Section |
| 0 | `crp:{0}:impactPathway:canAcess` | Can view all impactPathways |
