# Admin — Roles & Permissions Documentation — Task Plan

**Spec ID:** DOMAIN-ADMIN-001
**Status:** In Progress — T01…T09 done, T10…T12 pending review/validation
**Owner:** IBD Team — Kenji Tanaka
**Last Updated:** 2026-09-01
**Target merge:** staging (then promoted to main per release process).

---

## 1. Execution Context

- **Environment:** local workstation, macOS, MARLO checkout on branch `staging`.
- **Java level:** 17 — `scripts/run-marlo-java17.sh` (not needed for this spec; no build required).
- **Database:** local MySQL 8 (`/usr/local/mysql/bin/mysql`, socket `/tmp/mysql.sock`), schema `aiccradb1`,
  credentials from the gitignored `marlo-web/src/main/resources/config/marlo-dev.properties`.
- **Access mode:** read-only. `SELECT` plus `CALL getPermissions(<user_id>)`, which writes only to the
  session-local `user_permission` temporary table.
- **Spring profile:** not applicable, the application is not started.

## 2. Pre-flight Checklist

- [x] `requirements.md` drafted for DOMAIN-ADMIN-001.
- [x] `design.md` drafted for DOMAIN-ADMIN-001.
- [x] Confirmed no `docs/specs/domain/admin/` spec existed before (the folder was absent).
- [x] Confirmed the AICCRA role catalog is absent from repo migrations, so a database snapshot is required.
- [x] Latest `staging` pulled.
- [ ] Feature branch created off `staging` (`A2-2022-document-all-user-roles-profiles-and-permissions`).
- [ ] `requirements.md` and `design.md` approved by a reviewer.

## 3. Task List

### DOMAIN-ADMIN-001-T01 — Map the authorization model in code

- **Depends on:** —
- **Module:** docs (reads marlo-data, marlo-web)
- **Files touched:** none (read-only inspection)
- **Constitutional checks:** English-only notes; no source modified.
- **Tests:** not applicable.
- **Done when:** the four authorization layers are identified with file references.
- **Verification:** `Permission.java` (245 templates), `APCustomRealm.doGetAuthorizationInfo()`,
  `UserMySQLDAO.getPermission():68` → `getPermissions` procedure, `BaseAction.hasPermission():6348`. **Done.**

### DOMAIN-ADMIN-001-T02 — Extract the role catalog from the database

- **Depends on:** T01
- **Module:** docs
- **Files touched:** none
- **Constitutional checks:** read-only access; no credentials copied into the repo.
- **Tests:** not applicable.
- **Done when:** every role of global units 45 and 47 is listed with population and grant count.
- **Verification:** 20 roles per global unit; ids 417-436 on GU 45 and 479-498 on GU 47. **Done.**

### DOMAIN-ADMIN-001-T03 — Verify parity between AICCRA (45) and AICCRA III (47)

- **Depends on:** T02
- **Module:** docs
- **Files touched:** none
- **Constitutional checks:** none applicable.
- **Tests:** not applicable.
- **Done when:** it is established whether one grant list can serve both global units.
- **Verification:** the query comparing `COUNT(DISTINCT permission_id)` per acronym returned **zero rows**, i.e.
  identical distinct grant sets on both global units. **Done.**

### DOMAIN-ADMIN-001-T04 — Build the roles × actions matrix

- **Depends on:** T02, T03
- **Module:** docs
- **Files touched:** `roles-permissions-catalog.md` §4
- **Constitutional checks:** English-only; symbol legend declared before first use.
- **Tests:** not applicable.
- **Done when:** grants are classified by scope (`permissions.type`) and rendered as three matrices plus the
  portfolio and REST API tables.
- **Verification:** 968 distinct grant rows classified; every row lands in exactly one matrix cell. **Done.**

### DOMAIN-ADMIN-001-T05 — Verify effective permissions at runtime

- **Depends on:** T01, T02
- **Module:** docs
- **Files touched:** none
- **Constitutional checks:** read-only; `getPermissions` writes only to a temp table.
- **Tests:** not applicable.
- **Done when:** the expansion of `{0}` and `{1}` is observed, not inferred.
- **Verification:** `CALL getPermissions(1)` yielded `PC` with 69 project-scoped rows on project `102093`, and
  `CALL getPermissions(3059)` yielded `FPM` with `impactPathway:190:canEdit` resolved from
  `crp_program_leaders`. Four grant families were observed with `{1}` left unresolved (catalog §11.5). **Done.**

### DOMAIN-ADMIN-001-T06 — Document per-role purpose, scope and constraints

- **Depends on:** T04, T05
- **Module:** docs
- **Files touched:** `roles-permissions-catalog.md` §3
- **Constitutional checks:** English-only; AICCRA labels taken from `Role.getAiccraAcronymDimanic()`, not invented.
- **Tests:** not applicable.
- **Done when:** all 20 roles have purpose, scope, modules and constraints.
- **Verification:** each row cross-checked against that role's grant list in Appendix A. **Done.**

### DOMAIN-ADMIN-001-T07 — Document role assignment paths and parameter keys

- **Depends on:** T06
- **Module:** docs
- **Files touched:** `roles-permissions-catalog.md` §7
- **Constitutional checks:** specificity rule — parameter keys documented as they exist in both `APConstants.java`.
- **Tests:** not applicable.
- **Done when:** every role names the screen that grants it and its `crp_*_rol` key where one exists.
- **Verification:** `ProjectPartnerAction:1501` (PL/PC), `CrpAdminManagmentAction` (FPL/FPM),
  `CrpProgamRegionsAction` (RPL/RPM), `CrpSiteIntegrationAction` (SL), `CrpPpaPartnersAction` (CP),
  `crpUsers` (the rest). `CL` maps to no screen. **Done.**

### DOMAIN-ADMIN-001-T08 — Document the feedback permission subsystem

- **Depends on:** T02
- **Module:** docs
- **Files touched:** `roles-permissions-catalog.md` §5
- **Constitutional checks:** none applicable.
- **Tests:** not applicable.
- **Done when:** the `feedback_roles_permissions` matrix is rendered with cluster-type resolution.
- **Verification:** 25 rows on global unit 45, none on 47; `cluster_types` ids confirmed against the catalog
  table, revealing the seed-migration divergence recorded in catalog §11.6. **Done.**

### DOMAIN-ADMIN-001-T09 — Record findings and verification queries

- **Depends on:** T04, T05, T07, T08
- **Module:** docs
- **Files touched:** `roles-permissions-catalog.md` §10, §11, §12
- **Constitutional checks:** findings recorded, not fixed (story is documentation-only).
- **Tests:** not applicable.
- **Done when:** every anomaly carries table/role/file evidence, and §10 reproduces every table shown.
- **Verification:** 10 findings and 5 open questions recorded; §10 queries re-run and matched. **Done.**

### DOMAIN-ADMIN-001-T10 — Validate the catalog with system owners

- **Depends on:** T09
- **Module:** docs
- **Files touched:** `roles-permissions-catalog.md` §12, `requirements.md` §8
- **Constitutional checks:** review by IBD lead plus PMU or QA lead, per the story's acceptance criteria.
- **Tests:** not applicable.
- **Done when:** the five open questions are answered and the role purposes are confirmed by PMU.
- **Verification:** review comments recorded in the Decision Log. **Pending.**

### DOMAIN-ADMIN-001-T11 — Reconfirm the snapshot against production

- **Depends on:** T09
- **Module:** docs
- **Files touched:** `roles-permissions-catalog.md` header and §11
- **Constitutional checks:** read-only queries only, run by an authorized DBA/admin.
- **Tests:** not applicable.
- **Done when:** §10.1, §10.5, §10.6 and §10.7 have been run against production and the deltas recorded.
- **Verification:** populations, open phases and the feedback matrix confirmed or corrected. **Pending** —
  three findings (§11.1, §11.7, §11.10) depend on production state and must not be acted on before this.

### DOMAIN-ADMIN-001-T12 — Raise tickets for the accepted findings

- **Depends on:** T10, T11
- **Module:** docs / Jira
- **Files touched:** none
- **Constitutional checks:** issues created in project `A2` under epic A2-2017, written in English.
- **Tests:** not applicable.
- **Done when:** each accepted finding has a Jira issue linked back to A2-2022.
- **Verification:** issue keys appended to the corresponding finding in the catalog. **Pending.**

### DOMAIN-ADMIN-001-T13 — Validate every role against the database

- **Depends on:** T09
- **Module:** docs
- **Files touched:** `roles-permissions-catalog.md` §2, §3, §11.11, §11.12, §13
- **Constitutional checks:** read-only access; findings recorded, not fixed.
- **Tests:** 43 static assertions plus runtime expansion checks — see §13.1 and §13.2.
- **Done when:** every claim in §3 is expressed as an assertion and evaluated, and a re-runnable SQL suite exists
  so the same checks can be executed in another environment.
- **Verification:** 43/43 assertions pass. Two catalog claims were **wrong and were corrected**: `CL` is not a
  subset of `PL` (it uniquely holds `project:{1}:unsubmitted`), and the `FPL` vs `FPM` difference is 13 grants,
  not the two originally stated. Runtime validation covered the 8 roles that have users on global unit 47 and
  showed configured grant counts are an upper bound (`RPL`: 102 configured, 38 effective). Two findings were
  added (§11.11 PL/CL submit-unsubmit asymmetry, §11.12 wildcard holders invisible to naive audit queries) and
  a re-runnable SQL suite published as §13.5. **Done.**

## 4. Dependency Graph

```
T01 (model in code)
  ├── T02 (role catalog from DB)
  │     ├── T03 (parity 45 vs 47)
  │     │     └── T04 (matrices)
  │     │           └── T06 (purpose/scope/constraints)
  │     │                 └── T07 (assignment paths)
  │     └── T08 (feedback subsystem)
  └── T05 (runtime verification)
        └── T06

T04, T05, T07, T08 ──> T09 (findings + queries)
                          ├── T13 (validate every role against the DB)
                          ├── T10 (validation with owners)
                          ├── T11 (production reconfirmation)
                          └── T12 (tickets)  [after T10, T11]
```

## 5. Testing Plan

There is no code to unit-test. Verification is evidence-based:

| Kind | What | Result |
|---|---|---|
| Data reproduction | Re-run §10.1 and §10.2 and diff against the rendered tables | Matched (20 roles, 968 distinct grants) |
| Runtime check | `CALL getPermissions(<id>)` for a project-scoped role, a program-scoped role and an admin role | Matched: `PC` project-scoped, `FPM` program-scoped, `CRP-Admin` global |
| Cross-check | Every AICCRA label in §3 traced to `Role.getAiccraAcronymDimanic()` | Matched |
| Cross-check | Every reachability claim in §9 traced to a `custom_parameters` value or a menu definition | Matched |
| Negative check | Confirm the authorization tables were not written | Only `SELECT` / `CALL` issued; no DDL/DML |
| Assertions | 43 static assertions derived from every §3 claim | 43/43 pass; 2 catalog errors found and fixed (T13) |
| Runtime | Placeholder expansion for all 8 roles with users on GU 47 | Matched; grant counts confirmed to be upper bounds |
| Re-runnable suite | SQL assertion suite in §13.5 executed on the snapshot | All checks PASS |
| Review | PMU / QA walkthrough of §3 and §12 | Pending (T10) |
| Environment | Production reconfirmation of populations, phases and feedback rows | Pending (T11) |

## 6. Operational Steps

- No migration, no configuration change, no service restart.
- T11 requires read-only production database access, or a DBA to run the §10 queries and return the output.
- Publication: the rendered summary is posted as a comment on Jira A2-2022 (comment `41599`), generated from
  the repo file. Re-publish it whenever the catalog changes materially.

## 7. Rollback Plan

Revert the documentation commit. Nothing else changes: no schema, no configuration, no code.

## 8. Definition of Done

- [x] All 20 AICCRA roles documented with purpose, scope, modules and constraints (FN-001, FN-002).
- [x] Roles × actions matrix published per grant scope with a legend (FN-003).
- [x] The four authorization layers documented with file references (FN-004).
- [x] Assignment path and parameter key recorded per role (FN-005).
- [x] Feedback subsystem documented (FN-006).
- [x] Module reachability on AICCRA III documented (FN-007).
- [x] Reproduction queries published (FN-008).
- [x] Findings recorded with evidence, no configuration changed (FN-009, SEC-001).
- [x] Snapshot environment and date stated in the header (NF-002).
- [ ] Roles validated with system owners (T10) — the story's "All roles are validated with system owners".
- [ ] Snapshot reconfirmed against production (T11).
- [ ] Tickets raised for accepted findings (T12).
- [x] Every role validated against the database; 43/43 assertions pass and a re-runnable SQL suite is published
      (T13). This closes the story's criterion "Permissions are verified against the current system configuration".
- [x] Rendered summary published on Jira A2-2022 (comment `41599`).
- [ ] Spec reviewed and merged into `staging`.
