# Admin — Roles & Permissions Documentation — Requirements

**Spec ID:** DOMAIN-ADMIN-001
**Status:** Draft
**Owner:** IBD Team — Kenji Tanaka
**Reviewers:** PMU lead, QA lead, Tech lead
**Last Updated:** 2026-09-01
**Jira:** A2-2022 (US3 — Document all user roles/profiles and permissions), epic A2-2017 (AICCRA III — Improvements for admin module)
**Related PRD sections:** docs/prd.md §3 (Target Personas), §6.4 (Program / CRP Admin), §6.5 (Super Admin)
**Related System Design sections:** docs/system-design/design.md (navigation and screen inventory)
**Related Detailed Design sections:** docs/detailed-design/detailed-design.md §8 (Security & Authorization Model)
**Companion ai-context docs:** reports/ai-context/interceptor-validator-playbook.md
**Deliverable:** `docs/specs/domain/admin/roles-permissions-catalog.md`

---

## 1. Overview

A2-2022 asks for a complete, validated description of every user role/profile in MARLO and the permissions each
one carries, so that access levels, responsibilities and security boundaries are explicit. This spec records the
requirements for that documentation and points to the catalog that satisfies them. It documents the system as
configured; it changes no role and no permission.

## 2. Problem Statement

MARLO's authorization model is spread across four layers — database grants, a 3 300-line stored procedure that
expands them at runtime, Shiro wildcard evaluation, and Java-level gates in `BaseAction`. No document enumerates
what a role can do. The practical consequences, all cited in the PRD problem area (docs/prd.md §2):

- Onboarding an admin requires reading SQL and Java to answer "what can a Theme Leader edit?".
- Access requests are granted by copying another user's roles, because the effect of each role is unknown.
- `docs/detailed-design/detailed-design.md` §8.2 lists a generic seven-role model that does not match the twenty
  roles actually configured for AICCRA, and names the join table `user_role` instead of `user_roles`.
- Misconfigurations stay invisible: this exercise found a role with users and no permissions, duplicate grants,
  and grants that cannot match at runtime (see the catalog §11).

## 3. In-Scope Requirements

### Functional

- **DOMAIN-ADMIN-001-FN-001** The documentation MUST enumerate every role configured for the AICCRA global units
  (45 `AICCRA`, 47 `AICCRA_III`), identified by acronym, database description and the label the UI renders.
- **DOMAIN-ADMIN-001-FN-002** For each role the documentation MUST state its purpose, its grant scope, the
  modules/sections it reaches, and its constraints or special rules.
- **DOMAIN-ADMIN-001-FN-003** The documentation MUST include a permissions matrix of roles × actions, split by
  grant scope (global unit, project, program) and using an explicit symbol legend.
- **DOMAIN-ADMIN-001-FN-004** The documentation MUST describe the four authorization layers and how a stored
  grant becomes an effective runtime permission, including phase scoping and the node-grant fallback in
  `BaseAction.hasPermission()`.
- **DOMAIN-ADMIN-001-FN-005** The documentation MUST record, per role, the screen or flow that grants it and the
  `custom_parameters` key that resolves its id.
- **DOMAIN-ADMIN-001-FN-006** The documentation MUST cover the feedback permission matrix
  (`feedback_roles_permissions`) as a separate subsystem.
- **DOMAIN-ADMIN-001-FN-007** The documentation MUST state, per module, whether a grant is actually reachable on
  AICCRA III given the current `custom_parameters` and menu definitions.
- **DOMAIN-ADMIN-001-FN-008** The documentation MUST include the read-only SQL needed to reproduce every table
  it presents, so it can be re-verified in any environment.
- **DOMAIN-ADMIN-001-FN-009** Discrepancies found while documenting MUST be recorded as findings with enough
  evidence to become tickets, without being fixed inside this spec.

### Non-functional

- **DOMAIN-ADMIN-001-NF-001** Every figure in the documentation MUST come from a named source: a database query,
  a file path with line reference, or a migration file. No inferred numbers.
- **DOMAIN-ADMIN-001-NF-002** The documentation MUST carry the environment and date of its verification snapshot.
- **DOMAIN-ADMIN-001-NF-003** The documentation MUST be written in English, per `AGENTS.md`.
- **DOMAIN-ADMIN-001-NF-004** The structure MUST be uniform across roles so that two roles can be compared
  without re-reading prose.

### Security

- **DOMAIN-ADMIN-001-SEC-001** Producing the documentation MUST NOT modify `roles`, `permissions`,
  `role_permissions`, `user_roles`, `custom_parameters` or `feedback_roles_permissions`.
- **DOMAIN-ADMIN-001-SEC-002** The documentation MUST NOT contain user names, e-mail addresses, credentials or
  connection strings. User populations are reported as counts only.

## 4. Out-of-Scope

- Changing, adding or retiring any role or permission.
- Center-type global units (`global_unit_type_id = 4`) and non-AICCRA CRPs/platforms, beyond the shared model.
- The Struts interceptor stack per se — covered by `reports/ai-context/interceptor-validator-playbook.md`.
- Updating `docs/detailed-design/detailed-design.md` §8.2, which is a constitutional change and needs its own
  epic-level proposal.
- Maintaining a copy outside the repository and Jira. The story named Notion as the store; the team redirected
  the deliverable to a comment on A2-2022 instead. The repo copy is the versioned source of truth and the Jira
  comment is a rendered summary of it.

## 5. Personas Affected

| Persona (docs/prd.md §3) | How |
|---|---|
| Program / CRP Admin | Primary consumer: grants roles from Admin → Users without a reference today. |
| Super Admin | Uses the catalog to audit cross-tenant grants and service accounts. |
| PMU / Program Lead | Confirms the separation of duties, including its own lack of Admin-module access. |
| QA Reviewer | Needs to know which role can submit, unsubmit and approve. |
| Cluster Coordinator | Indirect: understands why a section is read-only for them. |

## 6. Acceptance Criteria

- **AC-1** (FN-001, FN-002) Given the catalog, when a reader looks up any of the 20 AICCRA roles, then they find
  its acronym, UI label, purpose, scope, modules and constraints in one row.
- **AC-2** (FN-003) Given the matrix, when a reader picks a role and a section, then the cell states whether the
  role has full, edit, field-level, submit, view or no access.
- **AC-3** (FN-004) Given the model section, when a reader asks why an edit grant does not apply, then phase
  scoping and the node-grant fallback explain it, with file references.
- **AC-4** (FN-005) Given a role, when an admin needs to grant it, then the catalog names the screen and the
  parameter key that resolves the role id.
- **AC-5** (FN-008) Given the queries in §10, when they are run against a MARLO database, then they reproduce the
  role catalog, the grant list, the parameter values and the feedback matrix.
- **AC-6** (FN-009) Given the findings section, when a reviewer reads any finding, then it names the affected
  table, role or file and the observed values.
- **AC-7** (SEC-001) Given the verification session, when the audit log and the database are inspected, then no
  write occurred against the authorization tables.
- **AC-8** (NF-002) Given the catalog header, then it names the database and date of the snapshot.

## 6b. Coverage of the Jira acceptance criteria (A2-2022)

The story's own criteria, verbatim, mapped to where they are satisfied. Deliverable = `roles-permissions-catalog.md`
unless noted.

| Story criterion | Status | Where |
|---|---|---|
| "A dedicated Notion page or space is created specifically for documenting roles and permissions." | **Satisfied differently** | The team redirected the deliverable to a comment on A2-2022 (comments `41599`, `41605`). The versioned repo file is the source of truth. Recorded in the Decision Log. |
| "Each user role/profile is documented, including: Role name" | Satisfied | §2 role catalog — acronym, database description, and the label the UI renders |
| "…Purpose and scope" | Satisfied | §3, one row per role |
| "…Permissions (view, edit, delete, approve, configure, etc.)" | Satisfied | §4.5 capability summary by action, in those verbs; the literal grants are in Appendix A |
| "…Associated modules or sections" | Satisfied | §3 (modules column), §4.1–4.4 (per section), §9 (which are reachable on AICCRA III) |
| "…Any constraints or special rules" | Satisfied | §3 (constraints column), plus §1 for the three model-wide rules: phase scoping, node-grant cascade, per-global-unit scoping |
| "A permissions matrix (roles × actions) is created for quick reference." | Satisfied | §4.1–4.4 by grant scope, §4.5 by action, §13.3 workflow rosters |
| "All roles are validated with system owners or relevant team members." | **Deferred** | Deferred by the team on 2026-09-01; the seven questions stay recorded in §12. Tracked as T10. |
| "Documentation follows a clean, consistent structure across all roles." | Satisfied | One row per role in §2, §3 and §4.5; one table per role in Appendix A. No role is described in prose only. |
| "Permissions are verified against the current system configuration to ensure accuracy." | Satisfied | §13 — 43/43 static assertions plus runtime verification; the audited database was confirmed to carry production data |
| "The final documentation is accessible to all authorized team members." | Satisfied | Repository branch off `staging`, plus the Jira comments |
| *Assumption:* "No changes will be made to roles or permissions." | Honored | Read-only access only (`SELECT` and `CALL getPermissions`, which writes a session temp table); asserted in §13 |
| *Assumption:* "Admin access is available to review all roles and configurations." | Met by other means | Read-only database access was used instead of the Admin UI, which gives complete coverage rather than what the screens expose |
| *Assumption:* "Future updates to permissions will follow this same document structure." | Satisfied | §14 — what to update when each thing changes, and which sections are generated rather than hand-written |

## 7. Constitutional Compliance Checklist

| Rule (CLAUDE.md) | Status |
|---|---|
| 1. Phased data is forward-only | Not applicable — documentation only. Phase scoping is documented, not changed. |
| 2. Save pipeline pattern | Not applicable — no save path touched. |
| 3. Spring MVC owns `/api/*` | Not applicable — no endpoint added. The REST API roles are documented. |
| 4. Specificities via `parameters` + `custom_parameters` with constants in both `APConstants.java` | Honored — existing `crp_*_rol` keys are documented, none added. |
| 5. Schema changes ship as Flyway migrations | Not applicable — no schema change. |
| 6. GPL header on new Java files | Not applicable — no Java file added. |
| 7. Code style / Checkstyle | Not applicable — no code changed. |
| 8. English only, i18n keys for user-facing strings | Honored — the catalog is in English. |
| 9. Branching from `staging`, never commit to `main` | Honored — spec lands via a feature branch off `staging`. |
| 10. Java 17 run scripts | Not applicable. |
| 11. Dependency baseline | Not applicable. |
| 12. No credential files committed | Honored — the catalog contains no connection data; verification used the gitignored `marlo-dev.properties`. |
| Spec commit prefix `[SPEC:docs/specs/domain/admin]` | To honor at commit time. |

## 8. Open Questions

1. Is `SL` intended to be a permission-less label, or were its grants lost? (catalog §11.1)
2. Should `PMU`/PMC have read access to the Admin module? (catalog §11.8)
3. Is `CRP-Admin`'s unqualified `crp:*` grant, which reaches every global unit, intended? (catalog §11.9)
4. Which of the eight unused roles (`FM`, `DM`, `CL`, `ML`, `E`, `AR`, `ARW`, `CD`) should be retired for
   AICCRA III? (catalog §11.10)
5. Must the feedback matrix be seeded for global unit 47 before AICCRA III enables the module? (catalog §11.7)
6. Who re-publishes the Jira comment when the catalog changes materially, so the two do not drift?

## 9. Decision Log

- 2026-09-01 — Documentation is derived from code **and** verified against a database, not from code alone —
  the AICCRA role catalog (ids 420-433 on GU 45, 479-498 on GU 47) exists only in real databases; the repo
  migrations seed roles up to id 176 and global units up to 28 (as recorded in
  `V2_6_0_20250616_1500__InsertFeedbackRolesPermissions.sql`), so a code-only reading would have missed every
  AICCRA role. Snapshot taken from the local `aiccradb1` database on 2026-09-01.
- 2026-09-01 — The versioned repo file is the source of truth; the published copy is a rendered summary — a
  permissions document that is not versioned next to the code it describes drifts silently.
- 2026-09-01 — Published as a comment on A2-2022 rather than in Notion — the story named Notion, but the team
  redirected the deliverable to the Jira issue itself (comment `41599`). The symbol matrices and the 968-grant
  appendix stay in the repo file because they do not render legibly in a Jira comment.
- 2026-09-01 — Global unit 47 (`AICCRA_III`) is used as the reference for grant lists, with parity against 45
  verified by query — the epic targets AICCRA III, and the distinct permission set per role is identical on both.
- 2026-09-01 — Findings are recorded, not fixed, inside this spec — the story is explicitly documentation-only
  ("No changes will be made to roles or permissions"); each finding becomes its own ticket under A2-2017.
- 2026-09-01 — Every §3 claim was turned into a database assertion rather than reviewed by reading — the pass
  identified two wrong claims in the first draft (`CL` described as a subset of `PL`; the `FPL` vs `FPM` delta
  understated as two grants instead of thirteen), which a re-read would not have caught. The suite is published
  as SQL in the catalog §13.5 so it can be re-run in any environment.
- 2026-09-01 — `docs/detailed-design/detailed-design.md` §8.2 is left untouched despite being inaccurate —
  editing it is a constitutional event requiring an epic proposal and review; the divergence is recorded in §2.
