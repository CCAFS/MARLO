# Admin → Management — PMU Liaison Institution Missing Or Dangling — Tasks

**Spec ID:** BUG-ADMIN-PMULIAISON-001
**Status:** Done (T01–T07); Ready (T08–T11)
**Owner:** IBD Team
**Last Updated:** 2026-09-02
**Implements design:** docs/specs/bugfix/pmu-liaison-institution-missing/design.md
**Branching:** the fix landed on `A2-2452-Review-optimization-and-dead-code-cleanup-in-MARLO-BaseAction` and is now
on `staging`. The branch name does not describe this work (requirements §9).
**Target merge:** staging (then promoted to main per release process).

## 1. Execution Context

- Java 17, `scripts/run-marlo-java17.sh` (Cargo + embedded Tomcat 9, HTTP on port 8080).
- Spring profile `dev`, configured in `marlo-web/src/main/resources/config/marlo-dev.properties` (gitignored).
- Environment used for verification: Global Unit 47 `AICCRA_III` on the `aiccradb_icipe_test` schema, a freshly
  seeded database with 0 `crp_programs`, 0 `liaison_institutions`, 0 `liaison_users` and `crp_cu = 336`.
- Application log: `${log.folder}/marlo-${log.instance}.log`.
- **Clean compile is mandatory.** An incremental `mvn compile` reports SUCCESS on code that does not compile;
  remove `marlo-data/target/classes` and `marlo-web/target/classes` first.
- **Checkstyle:** `mvn checkstyle:check` throws in this checkout. Run the Checkstyle jar directly against
  `configuration/marlo-checkstyle.xml` and diff the findings against the base commit.

## 2. Pre-flight Checklist

- [x] Incident reproduced and the failing line identified from a real stack trace, not inferred.
- [x] Root cause confirmed against the live database (`crp_cu = 336` with no such row).
- [x] `requirements.md` and `design.md` written before the taxonomy was settled; both reviewed after the move.
- [ ] Reviewed by IBD team lead (pending).
- [x] Working tree clean of unrelated changes before committing.

## 3. Task List

### BUG-ADMIN-PMULIAISON-001-T01 — Confirm the root cause against the live data

- **Depends on:** —
- **Files:** none (investigation)
- **Steps:** read the stack trace in `marlo-dev.log`; map it to `CrpAdminManagmentAction.savePmuRoleData`; query
  `custom_parameters` for `crp_cu` joined to `liaison_institutions` for the affected Global Unit.
- **Acceptance:** the id in `crp_cu` demonstrably resolves to no row.
- **Verification:** `SELECT cp.value, li.id FROM custom_parameters cp JOIN parameters p ON p.id = cp.parameter_id
  LEFT JOIN liaison_institutions li ON li.id = cp.value WHERE p.\`key\` = 'crp_cu' AND cp.global_unit_id = 47;`
  returned `336 / NULL`.
- **Rollback:** not applicable.
- **Status:** Done.

### BUG-ADMIN-PMULIAISON-001-T02 — Harden the session parameter parsing

- **Depends on:** T01
- **Files:** `marlo-web/.../action/crp/admin/CrpAdminManagmentAction.java`
- **Steps:** add `parseSessionParameter(String key, String value)`; route `crp_pmu_rol` and `crp_cu` through it.
- **Acceptance:** a non-numeric value raises `IllegalStateException` naming the parameter, not `NumberFormatException`.
- **Verification:** clean compile.
- **Rollback:** revert the method and the two call sites.
- **Status:** Done.

### BUG-ADMIN-PMULIAISON-001-T03 — Implement the resolution chain and the parameter repair

- **Depends on:** T02
- **Files:** `CrpAdminManagmentAction.java`
- **Steps:** add `PMU_LIAISON_INSTITUTION_NAME`; add `resolvePmuLiaisonInstitution()` with the four steps of
  design §1; add `repairCrpCuParameter(Long)` writing the `CustomParameter`, the `cuId` field and the session key.
- **Acceptance:** FN-001 … FN-005; NF-002 (step 1 short-circuits with no side effect).
- **Verification:** T08.
- **Rollback:** revert both methods; the rows they created remain valid input for the previous code.
- **Status:** Done.

### BUG-ADMIN-PMULIAISON-001-T04 — Reorder the addition branch and guard the insert

- **Depends on:** T03
- **Files:** `CrpAdminManagmentAction.java` (`savePmuRoleData`)
- **Steps:** compute `hasNewUsers`; resolve once before the loop; on `null`, add
  `programManagement.pmuLiaisonInstitution.missing` to `invalidFields`, log an ERROR and return without writing.
- **Acceptance:** FN-006 — no `liaison_users`, no `user_roles`, no notification.
- **Verification:** T09.
- **Rollback:** restore the inline `getLiaisonInstitutionById(cuId)` call.
- **Status:** Done.

### BUG-ADMIN-PMULIAISON-001-T05 — Guard the leader paths

- **Depends on:** —
- **Files:** `CrpAdminManagmentAction.java` (`programLeaderData`),
  `CrpProgamRegionsAction.java` (new `saveLiaisonUsersForLeader`)
- **Steps:** skip liaison institutions with no id; WARN when the program has none. Extract the regional block into a
  private method (design DR-5).
- **Acceptance:** FN-007; the Checkstyle `MethodLength` finding in `CrpProgamRegionsAction` does not get worse.
- **Verification:** T07 — the method went from 234 to 227 lines.
- **Rollback:** inline the method back.
- **Status:** Done.

### BUG-ADMIN-PMULIAISON-001-T06 — Fix the two DAOs and the two managers

- **Depends on:** —
- **Files:** `LiaisonUserMySQLDAO.java`, `CrpProgramLeaderMySQLDAO.java`,
  `LiaisonUserManagerImpl.java`, `LiaisonInstitutionManagerImpl.java`
- **Steps:** return `super.findAll(query)` instead of `null` on an empty result; add `@Transactional` to
  `saveLiaisonUser` and `saveLiaisonInstitution`.
- **Acceptance:** FN-008, NF-001; design DR-3 and DR-4.
- **Verification:** T08 on a Global Unit with both tables empty.
- **Rollback:** revert; note that reverting reintroduces the `NullPointerException` on empty tables.
- **Status:** Done. **These files use CRLF line endings — preserve them or the whole file shows as changed.**

### BUG-ADMIN-PMULIAISON-001-T07 — i18n, clean compile and Checkstyle

- **Depends on:** T02–T06
- **Files:** `marlo-web/src/main/resources/global.properties`
- **Steps:** add `programManagement.pmuLiaisonInstitution.missing`; remove both `target/classes`; compile; run the
  Checkstyle jar against the changed files and against the same files at the base commit.
- **Acceptance:** UI-001; BUILD SUCCESS; no finding that is not already present at the base commit.
- **Verification:** only the two pre-existing `CrpProgamRegionsAction` warnings remained (line 650 — untouched — and
  the method length, which improved).
- **Rollback:** not applicable.
- **Status:** Done.

### BUG-ADMIN-PMULIAISON-001-T08 — Verify the repair path end to end

- **Depends on:** T02–T07
- **Steps:** restart with `scripts/run-marlo-java17.sh`; log in as a CRP Admin of the affected Global Unit; add a
  person to the Program Management Unit team; save.
- **Acceptance:** AC for FN-001, FN-002, FN-005, FN-003, FN-004.
- **Verification (executed 2026-09-02 11:16 against `aiccradb_icipe_test`):**
  ```
  WARN  The parameter crp_cu=336 of the Global Unit AICCRA_III does not point to an existing liaison institution.
  WARN  Created the Program Management Unit liaison institution 396 for the Global Unit AICCRA_III.
  DEBUG onPostUpdate CustomParameter [id=2232, parameter=crp_cu, value=396]
  WARN  Repaired the parameter crp_cu of the Global Unit AICCRA_III: 336 -> 396.
  INFO  Assigned the role 489 for the user 1 (h.f.tobon@cgiar.org)
  ```
  Resulting rows: `liaison_institutions` 396 (`global_unit_id` 47, `institution_id` NULL, `crp_program` NULL,
  name and acronym `PMU`, active); `custom_parameters` 2232 `= 396`; `liaison_users` 631 (`institution_id` 396,
  `global_unit_id` 47, active). No exception page, no exception e-mail.
- **Status:** Done.

### BUG-ADMIN-PMULIAISON-001-T09 — Verify the degraded path

- **Depends on:** T08
- **Steps:** point `crp_cu` at a non-existent id **and** prevent the fallbacks from resolving or creating; save.
- **Acceptance:** AC for FN-006 — the screen shows the i18n message, an ERROR is logged, and none of
  `liaison_users`, `user_roles` or the notification happens.
- **Status:** **Not executed.** The creation fallback always succeeds in a writable schema, so forcing this path
  needs a database whose `liaison_institutions` insert fails. Left as a gap; see design §16.6.

### BUG-ADMIN-PMULIAISON-001-T10 — Verify the no-op path

- **Depends on:** T08
- **Steps:** with `crp_cu` now resolving, add a second person to the team and save.
- **Acceptance:** AC for NF-002 — no liaison institution created, `crp_cu` unchanged, no repair WARN.
- **Status:** **Not executed.** Should be run before this reaches production.

### BUG-ADMIN-PMULIAISON-001-T11 — Write the module agent context

- **Depends on:** T08
- **Files:** `docs/specs/domain/admin/agent-context.md`
- **Acceptance:** the liaison institution model, the `crp_cu` lifecycle, the creation and visualisation points and
  the landmines are recorded, with an explicit statement of what the file does not cover.
- **Status:** Done.

## 4. Dependency Graph

```
T01 ──► T02 ──► T03 ──► T04 ──┐
                              ├──► T07 ──► T08 ──► T09 (not run)
T05 ──────────────────────────┤                └─► T10 (not run)
T06 ──────────────────────────┘                └─► T11
```

## 5. Testing Plan

- **Unit:** none added. `CrpAdminManagmentAction` has no test harness, and building one for a Struts action with
  thirteen injected managers is out of proportion to this fix. Recorded as design §16.6.
- **Integration:** none added, same reason.
- **Manual regression:**
  - A Global Unit whose `crp_cu` already resolves must behave exactly as before (T10).
  - Adding and then removing a Program Management Unit member must leave `liaison_users` soft-deleted
    (`is_active = 0`) and remove the `user_roles` row — exercised incidentally during verification, rows 630/631.
  - Assigning a flagship leader on a Global Unit with components must still create one `liaison_users` row per
    liaison institution of the program.
- **Known non-blocking behaviour:** on a Global Unit with no `crp_programs`, `validate()` always reports
  `list-flagshipsPrograms`, so the screen never shows `saving.saved` even when the save succeeded. Pre-existing.

## 6. Operational Steps

- No migration, no configuration change, no environment variable, no coordination with the BI or AI services.
- Environments whose `crp_cu` is dangling repair themselves the first time a CRP Admin saves the Management screen.
  Nothing has to be run against them.
- Anyone seeding a Global Unit outside `GlobalUnitCreationManagerImpl` must create the PMU liaison institution and
  derive `crp_cu` from its id, never copy the value from another schema (requirements DA-003).

## 7. Rollback Plan

- Revert the two implementation commits. No schema or data migration has to be undone.
- Rows already created — a PMU liaison institution with `acronym = 'PMU'` and a repaired `crp_cu` — are exactly what
  the pre-change code expects to find, so a revert leaves the affected environments working rather than broken.
- The one regression a revert reintroduces is the `NullPointerException` on empty `liaison_users` /
  `crp_program_leaders` tables.

## 8. Definition of Done

- [x] The reported incident no longer reproduces in the environment where it was reported (T08).
- [x] Clean compile and no new Checkstyle finding (T07).
- [x] Root cause fixed at its source, not worked around at the call site (design DR-3).
- [x] The durable knowledge is recorded where the Mandatory Agent-Context Rule will find it (T11).
- [ ] T09 and T10 executed.
- [ ] Spec reviewed by the IBD team lead.
- [ ] Open risks in design §16 triaged into follow-up work — in particular the cross-unit `crp_cu` (risk 1) and the
      unguarded role parameters (risk 2), which reproduce this same failure class on `user_roles`.
