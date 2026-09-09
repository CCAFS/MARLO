# Admin → Management — PMU Liaison Institution Missing Or Dangling — Design

**Spec ID:** BUG-ADMIN-PMULIAISON-001
**Status:** Implemented (partial — Open Risks §16)
**Owner:** IBD Team
**Last Updated:** 2026-09-02
**Implements requirements:** FN-001 … FN-008, NF-001, NF-002, DA-001 … DA-004, UI-001, SEC-001, OPS-001, OPS-002
**Touches modules:** marlo-web, marlo-data

## 1. Architecture Summary

The Management save gains a single resolution point for the PMU liaison institution, placed before any side effect
that commits on its own. Everything downstream — the role, the notification, the liaison row — depends on it
succeeding.

```
POST /admin/{crp}/management  (crpAdminStack)
        |
   prepare()            session crp_cu -> cuId   (parseSessionParameter: numeric or IllegalStateException)
        |
   validate()           fills invalidFields; does NOT abort
        |
   save()
        |
   savePmuRoleData()
        |
        +-- removals: liaisonUserManager.findAll() filtered by user + cuId  --> deleteLiaisonUser / deleteUserRole
        |
        +-- additions:
              hasNewUsers ?
                 |
            resolvePmuLiaisonInstitution()
                 |
             1. getLiaisonInstitutionById(cuId) ------------------> found: return as is (no repair, no WARN)
             2. findByAcronymAndCrp("PMU", crp)  --+
             3. first active, crpProgram == null  --+--> repairCrpCuParameter(id): custom_parameter + cuId + session
             4. create (crp, name/acronym "PMU")  --+
                 |
              null ? --> invalidFields + LOG.error, insert nothing, RETURN
                 |
              found --> per new UserRole: saveUserRole -> notify -> new LiaisonUser(crp, institution, user)
        |
   saveProgramsData()   per program: create/refresh its liaison institution, then leaders and managers
```

## 2. Module Footprint

### marlo-web

- Modified: `action/crp/admin/CrpAdminManagmentAction.java`
  - New `PMU_LIAISON_INSTITUTION_NAME` constant.
  - New `parseSessionParameter(String key, String value)`.
  - New `resolvePmuLiaisonInstitution()`.
  - New `repairCrpCuParameter(Long liaisonInstitutionId)`.
  - Changed `prepare()` — the two session ids go through `parseSessionParameter`.
  - Changed `savePmuRoleData()` — resolution hoisted above the loop; guard and early return.
  - Changed `programLeaderData()` — skip liaison institutions with no id; WARN when the program has none.
  - New import: `org.cgiar.ccafs.marlo.data.model.Parameter`.
- Modified: `action/crp/admin/CrpProgamRegionsAction.java`
  - New `saveLiaisonUsersForLeader(CrpProgram, User)` — extracted from the regional leader block, with the same
    guard and WARN.
- Modified: `resources/global.properties` — `programManagement.pmuLiaisonInstitution.missing`.

### marlo-data

- Modified: `data/dao/mysql/LiaisonUserMySQLDAO.java` — `findAll()` returns the list instead of `null`.
- Modified: `data/dao/mysql/CrpProgramLeaderMySQLDAO.java` — same.
- Modified: `data/manager/impl/LiaisonUserManagerImpl.java` — `@Transactional` on `saveLiaisonUser`.
- Modified: `data/manager/impl/LiaisonInstitutionManagerImpl.java` — `@Transactional` on `saveLiaisonInstitution`.

### marlo-core / marlo-utils

- Not applicable.

## 3. Data Model Changes

No schema change, therefore no Flyway migration (requirements DA-004). The design depends on constraints that already
exist:

```sql
CREATE TABLE `liaison_users` (
  `user_id`        bigint NOT NULL,
  `institution_id` bigint NOT NULL,   -- FK -> liaison_institutions(id), ON DELETE CASCADE
  `is_active`      tinyint(1) NOT NULL,
  `global_unit_id` bigint DEFAULT NULL
);

CREATE TABLE `liaison_institutions` (
  `institution_id` bigint DEFAULT NULL,  -- FK -> institutions(id)
  `name`           varchar(255) NOT NULL,
  `acronym`        varchar(255) DEFAULT NULL,
  `crp_program`    bigint DEFAULT NULL,   -- FK -> crp_programs(id)
  `is_active`      tinyint(1) NOT NULL DEFAULT '1',
  `global_unit_id` bigint DEFAULT NULL
);
```

Runtime data repair, not migration: `custom_parameters.value` for the `crp_cu` parameter of the affected Global Unit
is rewritten in place by `repairCrpCuParameter`. The `parameters` catalog row is never touched, per the parameters
domain contract.

### Entity invariants relied upon

- A PMU liaison institution is `crp_program IS NULL AND institution_id IS NULL` (DA-002).
- `LiaisonUserMySQLDAO.save()` sets `active = true` on insert, so `is_active` is never the missing value.
- `CrpProgram.liaisonInstitutions` is mapped `inverse="true"` on the `crp_program` column, so a PMU row is in no
  program's collection and no name/acronym resync loop reaches it.

## 4. API / Action Surface

No new action, no new mapping, no REST change. `struts-admin.xml` is untouched; `{crp}/management` keeps its
`crpAdminStack` interceptor reference and its `management.ftl` result.

Existing REST v2 consumers of the same record — `FinancialSummaryItem` and `ProgressTowardsItem`, through
`APConstants.CLARISA_ACRONYM_PMU` — are unchanged but are the reason FN-003 fixes the acronym.

## 5. Frontend Composition

No FTL or JS change. `management.ftl` submits only `user.id`, `role.id` and `id` per team member through its local
`userItem` macro; the liaison institution has never been part of that form and remains invisible to it.

The only user-visible addition is the `invalidFields` entry keyed `list-loggedCrp.programManagmenTeam`, rendered by
the existing loop in `save()` that turns `invalidFields` into action messages.

## 6. Persistence & Phase Replication Plan

Not applicable. None of `liaison_institutions`, `liaison_users`, `user_roles` or `custom_parameters` is phased; they
carry no `phase_id` and are not replicated forward. The constitutional forward-only rule does not engage here.

## 7. Validation & Save Pipeline

The screen deviates from the canonical `Action.validate()` → `Validator` → manager chain: `validate()` only calls
`setInvalidFields(...)` and never `addFieldError`, so Struts does not return `INPUT` and `save()` always runs. That
deviation predates this spec and is recorded in requirements §9 rather than corrected here.

Within that shape the design adds one rule: **a precondition that cannot be satisfied stops the write instead of
being carried into it.** `resolvePmuLiaisonInstitution()` runs before the loop; a `null` result adds to
`invalidFields`, logs an ERROR and returns from `savePmuRoleData()` without touching any table.

Ordering inside the addition branch is part of the contract:

```
resolve institution   (may create; may repair crp_cu)
  -> saveUserRole                  (commits on its own)
  -> addCrpUser / notifications    (send e-mail)
  -> saveLiaisonUser               (the row that used to fail)
```

Before this change the resolution sat between the notification and the insert, which is what produced a role plus an
e-mail plus no liaison row on every failed attempt.

## 8. Permissions & Edit Gates

Unchanged. `save()` is still guarded by `hasPermission("*")` against
`Permission.CRP_ADMIN_BASE_PERMISSION`, reached through `crpAdminStack` (`AccessibleAdminInterceptor`,
`EditCrpAdminInterceptor`, `ValidSessionCrpInterceptor`, `RequireUserInterceptor`, …).

SEC-001 is satisfied structurally: the creation and the parameter repair live inside that gated save and nowhere
else. They were deliberately not placed in `LoginAction` or `ValidSessionCrpInterceptor`, which are the two places
that read the parameter into the session and which run for every user on every Global Unit switch.

## 9. Specificity / Feature-Flag Strategy

No new specificity. `crp_cu` is an existing `parameters` row (`global_unit_type_id`-scoped, category 3, no default
value); this design writes only its `custom_parameters` value, which is what the Superadmin → Parameters screen also
does.

The behaviour is not flag-gated: a dangling parameter is a defect in every environment, and gating the repair would
leave the failure reachable.

## 10. Integration Points

- **REST v2** (`FinancialSummaryItem`, `ProgressTowardsItem`) — consumers of the same record by acronym. Not called
  from this path, but the reason for FN-003.
- **SendMailS** — the assignment notification, now ordered after a successful resolution.
- No CLARISA, CGSpace, BI, AI-service or S3 interaction.

## 11. Observability

| Level | Message | Emitted when |
|---|---|---|
| WARN | `The parameter crp_cu={} of the Global Unit {} does not point to an existing liaison institution.` | step 1 fails |
| WARN | `Created the Program Management Unit liaison institution {} for the Global Unit {}.` | step 4 runs |
| WARN | `Repaired the parameter crp_cu of the Global Unit {}: {} -> {}.` | any fallback succeeds |
| ERROR | `The Program Management Unit liaison institution of the Global Unit {} could not be resolved ({}={}).` | nothing resolves |
| WARN | `The program {} ({}) has no liaison institution, so the leader {} was saved without a liaison user.` | flagship leader with no liaison institution |
| WARN | `The region {} ({}) has no liaison institution, so the leader {} was saved without a liaison user.` | regional leader, same |

The traces are the only record: neither entity implements `IAuditLog` in a way the audit listener picks up
(`AuditColumnHibernateListener` logs `LiaisonUser is not a MarloAuditableEntity`).

## 12. Performance & Scalability

`resolvePmuLiaisonInstitution()` runs at most once per save, and only when the submitted team contains a member with
no id. Step 1 is a primary-key `find`; in the normal case it is the only query added and the method returns
immediately (NF-002).

Not addressed, and pre-existing: the removal branches call `liaisonUserManager.findAll()` — a full table read
filtered in memory — twice per existing team member. `LiaisonUserManager` already exposes
`getLiaisonUsersByUserId(userId, crpId)`, which would replace it, but switching also changes the filter semantics
(it applies `is_active = 1` and a Global Unit predicate), so it is left for a follow-up.

## 13. Security Considerations

- No credential, personal or sensitive data handling changes.
- The repair writes master data, which is why SEC-001 confines it to an admin-gated save.
- `parseSessionParameter` turns a malformed session value into an `IllegalStateException` with a message naming the
  parameter and the Global Unit, instead of a raw `NumberFormatException`. The message contains configuration
  identifiers only.

## 14. Backwards Compatibility & Rollout

- **Compatible by construction.** A Global Unit whose `crp_cu` resolves takes step 1 and behaves exactly as before:
  no creation, no repair, no new log line (NF-002).
- **No migration, no dual-running, no feature flag.** The change is inert until a save hits a dangling parameter.
- **Rollback** is a revert of the two commits. The rows already created stay valid: a PMU liaison institution with
  `acronym = 'PMU'` and a repaired `crp_cu` are exactly what the pre-change code expects to find, so reverting leaves
  the affected environments in a *better* state than before, not a broken one.
- The DAO change is the one with reach beyond this screen: five call sites across `CrpAdminManagmentAction` consume
  those two `findAll()` methods, all of which streamed the result directly and therefore could only have crashed on
  the old `null`.

## 15. Decision Records

**DR-1 — Resolution chain instead of a plain null check.**
A null check alone satisfies "do not crash" but leaves the administrator unable to proceed, because no screen in
MARLO creates a PMU liaison institution. The chain makes the screen self-healing while keeping creation as the last
resort, so an existing record is always preferred over a new one.

**DR-2 — Repair at save, not at session load.**
`LoginAction` and `ValidSessionCrpInterceptor` are where the parameter is first read and would give platform-wide
coverage. They were rejected: they run for every user on every switch, would make a read-only user create master
data implicitly, and would add writes to a hot path. Detection (a WARN) there is still worth adding — see §16.

**DR-3 — Fix the DAOs, not the call sites.**
`AbstractMarloDAO.findAll(Query)` returns `hibernateQuery.list()`, which is never null, and none of the five callers
tested for null. Returning the list is therefore strictly safer at the DAO. Scope was held to the two DAOs this
screen depends on.

**DR-4 — `@Transactional` on the two save methods.**
`saveLiaisonUser` and `saveLiaisonInstitution` had no transactional manager in the chain while their `delete`
counterparts did. Since this design now *creates* a `LiaisonInstitution` through that path, the missing annotation
would risk the row being rolled back by the connection pool with no error surfaced.

**DR-5 — Extract `saveLiaisonUsersForLeader` in the regional action only.**
The equivalent guard in `CrpAdminManagmentAction.programLeaderData()` was left inline because its method is not over
the length limit. In `CrpProgamRegionsAction` the enclosing method was already at 234 lines against a 150 limit, so
adding the guard inline would have made an existing Checkstyle violation worse; extracting brought it to 227.

## 16. Open Risks

1. **Step 1 trusts the id.** A `crp_cu` pointing at another Global Unit's record, or at a component or partner row,
   resolves and is used. AICCRA_III is in that state in the real databases, so this design does not fix it there.
   Closing it is two conditions plus a decision about the `liaison_users` already written against the shared record.
2. **The same failure class remains on `user_roles`.** `crp_fpl_rol`, `crp_fpm_rol`, `crp_rpl_rol`, `crp_rpm_rol`,
   `crp_pl_rol` and `crp_pc_rol` are resolved with no null check and assigned into a `NOT NULL` FK.
3. **Two definitions of a correct PMU** — `GlobalUnitCreationManagerImpl.createLiaisonInstitution()` and
   `resolvePmuLiaisonInstitution()` — will drift.
4. **431 DAOs still return `null` on an empty table**, against roughly 446 unguarded dereferences in `marlo-web`.
   Every module opened for the first time in a fresh Global Unit is exposed.
5. **Two sources of truth for the same record**: the `crp_cu` id and the `acronym = "PMU"` convention. Nothing keeps
   them consistent, and five consumers use the convention while only this screen uses the id.
6. **No automated test.** Verification was manual against a live environment (see `task.md` §5). The screen has no
   existing test harness, and building one was out of proportion to the fix.
