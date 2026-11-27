# Partner request approval per Global Unit — Tasks

**Spec ID:** CHG-PARTUNIT-001
**Status:** Ready — approved 2026-11-27 (owner: "continúa con la checklist")
**Owner:** Kenji Tanaka (IBD)
**Last Updated:** 2026-11-27
**Implements design:** docs/specs/changes/partner-request-approval-per-unit/design.md (approved 2026-11-27; judgment APPROVED, round 2)
**Branching:** the existing branch `revive-local-partner-request-approval`, reused by owner decision and fast-forwarded to `origin/staging` (`a54160ab47`)
**Target merge:** `staging`. Two PRs (§ 9)

> **Answer first.** 8 tasks, in dependency order.
> 1. **T01** defines who administers a unit: two native queries and the `ReviewerScope` value.
> 2. **T02** makes the manager check the scope after the row lock and lists by scope.
> 3. **T03** adds the duplicate guard, and settles P-18 first with a two-session probe.
> 4. **T04** wires the scope into the three actions and `BaseAction`, with the listing refusal and the log lines.
> 5. **T05** opens the routes: the new interceptor in the `json` package and the two admin routes.
> 6. **T06** redirects the alerts and adds the office link, including the two per-unit texts.
> 7. **T07** builds the shared review body, the two shells, the menu entry, the chips and the filter.
> 8. **T08** proves it live on a throwaway DB and updates the three docs.
>
> Baselines below were **observed on `e365e6a950`** (2026-11-27). Reds of tests that do not exist yet are marked
> `predicted — greenfield artifact (observed in <task>)` and must be quoted in `execution.md` by that task.

---

## 1. Execution Context

| Item | Value |
|---|---|
| Java / build | Java 17; `marlo-parent/pom.xml` |
| Compile gate | `rm -rf marlo-web/target/classes marlo-web/target/test-classes marlo-data/target/classes && mvn -q -o -pl marlo-data,marlo-web -am test-compile -DskipTests` (expect ~2400 files compiled in marlo-data, ~1040 in marlo-web) |
| Checkstyle | checkstyle 8.18 jar with `configuration/marlo-checkstyle.xml` on the changed files, delta against HEAD (`marlo-verify`) |
| Tests | `mvn -q -o -pl marlo-web -am test` (keep `-am`) |
| Local run | `scripts/run-marlo-java17.sh`. Never while another agent builds (CLAUDE.md *Concurrency*) |
| Throwaway DB | A copy of the local AICCRA schema made for this spec (e.g. `partunit_probe`), never `partroute_local` or the source databases. Created in T01, reused by T02, T03 and T08 |
| Browser | Playwright or Chrome automation; wait for the page JS before clicking (memory: browser save tests). Local login per `~/.marlo-local-credentials` |

## 2. Pre-flight Checklist

- [ ] `requirements.md`, `design.md` approved; `judgment.md` = APPROVED.
- [ ] On `revive-local-partner-request-approval`, clean tree apart from this spec folder.
- [ ] Spec documents committed with `[SPEC:changes/partner-request-approval-per-unit]` (`marlo-commit`; ask before committing).
- [ ] Throwaway DB copy created (T01, first step).

## 3. Task List

**Common Done criteria for every code task** (not repeated below):
- Clean compile gate green, with the expected file counts.
- Checkstyle delta 0 on changed files (`BaseAction.java` already exceeds the file-length rule at HEAD; the delta is what counts).
- GPL header on new Java files; English only; 2-space indent; 120 columns.
- `marlo-verify` hygiene: CRLF preserved, null-safe log arguments, no email or request content in logs, no `org.jfree.util.Log`.
- The full `marlo-web` suite green with `-am` at the end of the task, every `CHG-PARTREQ-001` and `CHG-PARTROUTE-001` test included (NF-002).

**Backend route line** (`software-architect` → `references/backend-routing.md`): every backend task below carries
`Backend route: stack other; implement lead existing design support none — project conventions; error-model lead error-handling-patterns support none — project conventions; test-first lead tdd support none — project conventions`.

---

### CHG-PARTUNIT-001-T01 — "Administers": the two queries and `ReviewerScope`

- **Status:** [ ] · **Size:** M · **Depends on:** — · **Module:** marlo-data (+ one marlo-web test double)
- **Requirements:** SEC-001 (the definition: active user, active membership, `crp_admin_active`, `CRP-Admin` role or a role granted `crp:{0}:admin:*`); SEC-004 (`permits(null)` only for `any`); FN-009 (administrators of a unit, non-blank email, distinct); NF-001.
- **Design refs:** § 2 marlo-data, § 7.1, § 7.2 (fail closed), DD-1, P-5, P-6, P-7, P-8, P-21, P-28, P-30.
- **Scope:**
  - First step: create the throwaway DB copy and record its name in `execution.md`.
  - `RoleDAO` / `RoleMySQLDAO`: `findAdministeredUnitIds(long userId)` and `findUnitAdministrators(long unitId)`, native SELECTs following `RoleMySQLDAO.java:72` (P-8). The permission text comes from `Permission.CRP_ADMIN_EDIT_PRIVILEGES`, the parameter key from the existing constant for `crp_admin_active` (`APConstants.CRP_ADMIN_ACTIVE`), never literals.
  - `RoleManager` / `RoleManagerImpl` pass-throughs (read-only).
  - `ReviewerScope` (new, `data/model`): `any()`, `units(Set<Long>)` (defensive copy, null-safe), `permits(GlobalUnit)`, `isEmpty()`.
  - `CrpUsersActionDirectoryTest.NoOpRoleManager` (`:540`) gains the two methods (P-14).
  - `Backend route:` as above.
- **Tests:**
  - `ReviewerScopeTest` (unit): `any().permits(null)` true; `units({45}).permits(null)` false; `units({45})` permits 45, not 47; `units(null)` and `units({})` permit nothing and are empty.
  - Query fixture on the throwaway DB, one row per condition, each the only difference from a positive case: user X `CRP-Admin` of 45, active, active membership, `crp_admin_active=true` → 45 listed; then separately: user inactive; membership inactive; membership absent; `crp_admin_active=false`; parameter row inactive; role of another unit; role without 421 and not `CRP-Admin` (e.g. `DM`, holds 422 only); role with 421 under another acronym → listed. `findUnitAdministrators(45)`: blank email excluded; a user with two memberships appears once.
  - The fixture is driven by a probe script under the spec folder (`probes/t01-*`), output quoted in `execution.md`.
- **Verification:** compile gate; `ReviewerScopeTest`; the probe output; `grep -n "'CRP-Admin'\|crp:{0}:admin\|crp_admin_active" RoleMySQLDAO.java` shows the role acronym literal only (the other two come from constants).
- **Falsifier:**
  - (a) Drop the `crp_users.is_active` condition → the "membership inactive" fixture row is listed.
  - (b) Drop the `crp_admin_active` join → the "admin area off" row is listed.
  - (c) Match permission 422 instead of 421 → the `DM` row is listed.
  - (d) `permits(null)` returns true for `units(...)` → the SEC-004 unit case red.
- **Red run:** baseline observed: `grep -rl ReviewerScope marlo-web/src marlo-data/src | wc -l` → `0`; the two DAO methods do not exist. Falsifier reds: `predicted — greenfield artifact (observed in T01)`.
- **Disqualifier:** a fixture whose negative rows differ from the positive one in more than one condition (a red then proves nothing about the condition named); a probe run on `partroute_local` or a source DB.
- **Consumers:** `CrpUsersActionDirectoryTest` (`NoOpRoleManager`); `CrpUsersActionWelcomeMailTest` (`forbidden(RoleManager.class)` proxy, unaffected); `RoleManagerImpl` the only main implementer.
- **Review:** `full` — the authorization definition every other task relies on.
- **Done when:** falsifiers (a)–(d) observed red then green and quoted; probe output in `execution.md`.
- **Skills:** `marlo-verify`, `error-handling-patterns`, `tdd`, `marlo-commit`.

---

### CHG-PARTUNIT-001-T02 — Manager: scope after the lock, listing by scope

- **Status:** [ ] · **Size:** M · **Depends on:** T01 · **Module:** marlo-data (+ manager test in marlo-web)
- **Requirements:** SEC-001 and AC "cross-unit refusal" (nothing written); SEC-002 (the outcome carries no request); SEC-004 and its AC; FN-008 and its AC (mixed batch); FN-001 / FN-004 listing part; NF-002 (refusal order, lock).
- **Design refs:** § 2, § 7.2, § 7.4 (DAO part), DD-3, J-16, P-4, P-13.
- **Scope:**
  - `PartnerRequestOutcome`: `Status.NOT_AUTHORIZED`, `Status.DUPLICATE` (added here; used by T03); neither notifiable; unpublished `requestUnitId` (getter only, set by a new factory for `NOT_AUTHORIZED`).
  - `PartnerRequestManager(Impl)`: the five decision methods take `ReviewerScope scope`; after `findForUpdate`, order `NOT_FOUND` → `NOT_AUTHORIZED` → `WRONG_KIND` → `ALREADY_HANDLED`; a refused row is never saved.
  - `PartnerRequestDAO` / `PartnerRequestMySQLDAO`: `findPendingForReview(ReviewerScope)`; null → `IllegalArgumentException`; `any()` → every pending row (null-unit included); `units(s)` → rows of those units only; empty → empty list without a query. Same row filters as `PartnerRequestAction.java:251-255`.
  - `PartnerRequestManager.findPendingForReview(scope)` pass-through.
  - `Backend route:` as above.
- **Tests** (`PartnerRequestManagerGuardTest`, extended):
  - Each of the five methods with a request of unit 47 and `units({45})`: `NOT_AUTHORIZED`, `getRequest() == null`, `getInstitution() == null`, `getRequestUnitId() == 47`, and the recording DAO saw no `save` (arguments asserted).
  - Null-unit request with `units({45})` → `NOT_AUTHORIZED`; with `any()` → proceeds.
  - Order: a request of 47 that is already handled, under `units({45})` → `NOT_AUTHORIZED`, not `ALREADY_HANDLED`; a missing id → `NOT_FOUND`.
  - Mixed office batch ids {45-row, 47-row} under `units({45})` → one `DONE`, one `NOT_AUTHORIZED`, only the 45 row saved.
  - Existing 15 tests updated to pass `ReviewerScope.any()`; the `answering` proxy (`:80-89`) answers any new DAO call they reach.
  - DAO: `findPendingForReview` on the throwaway DB, run through the T01 probe pattern: 45 only, 47 only, `any()` (null-unit row present), empty; null scope throws.
- **Verification:** compile gate; the manager suite; the DAO probe output.
- **Falsifier:**
  - (a) Move the scope check after the kind / pending checks → the order case red.
  - (b) Attach the request to the `NOT_AUTHORIZED` outcome → the SEC-002 null-request assertion red.
  - (c) Treat a null unit as permitted → the SEC-004 case red.
  - (d) Return every row for an empty scope → the DAO empty case lists rows.
- **Red run:** baseline observed: `PartnerRequestOutcome.Status` has no `NOT_AUTHORIZED` (`grep -c NOT_AUTHORIZED PartnerRequestOutcome.java` → `0`); manager suite `@Test` count `15`. New cases: `predicted — greenfield artifact (observed in T02)`.
- **Disqualifier:** a test that builds the expected outcome from the same request object it passes in (KZ directory-abstraction-2); a mixed-batch test whose two rows share a unit.
- **Consumers:** `PartnerRequestAction`, `RejectPartnerRequestAction`, `EditPartnerRequestAction` (the only callers of the five methods, judge A round 1) — they stop compiling until T04; T02 and T04 land in the same PR and T02 adds a temporary `any()` at those three call sites, removed by T04. `PartnerRequestManagerGuardTest`.
- **Review:** `full` — the authority check of SEC-001.
- **Done when:** falsifiers observed red then green and quoted; the temporary `any()` call sites listed in `execution.md` for T04 to remove.
- **Skills:** `marlo-verify`, `error-handling-patterns`, `tdd`, `marlo-commit`.

---

### CHG-PARTUNIT-001-T03 — Duplicate guard (settles P-18 first)

- **Status:** [ ] · **Size:** M · **Depends on:** T02 · **Module:** marlo-data
- **Requirements:** FN-006 and its AC (normalization cases; BUT other country; AND inactive; AND IT MUST NOT offer override); FN-007; OPS-002 (data part: the outcome carries the matched institution); NF-002 (one institution per accept under concurrent clicks still holds).
- **Design refs:** § 7.3, DD-6, P-15, P-16, P-17, P-18, P-19, J-15, J-18, J-23, J-24, R-1, R-6.
- **Scope:**
  - **First step — P-18 probe** on the throwaway DB: two sessions, each running the exact § 7.3 sequence (lock request copy, lock country, locking join, insert institution + HQ location) for two **different** requests with the same name and country. Without the country lock → two institutions (red); with it → one, the second session's join sees the first's row. Also record `@@transaction_isolation` as the app's connection reports it. Output in `probes/t03-*` and `execution.md`. A green without the red is not evidence. If the probe stays red with the lock → **Pivot** (R-1), stop and ask.
  - `LocElementDAO.lockForUpdate(long)`; `InstitutionDAO.findActiveHeadquarteredIn(long)`: one HQL block from `InstitutionLocation` joined to `Institution`, `headquater = true`, `active = true`, `PESSIMISTIC_READ`, no `DISTINCT`, no subquery.
  - The normalization function (lower case `Locale.ROOT`, `[\s\p{Z}]+` → one space, trim); accent-sensitive.
  - `acceptPartnerRequest`: after the existing checks, lock country → candidates → compare → `DUPLICATE` with the matched institution, nothing written, copy stays pending.
  - `Backend route:` as above.
- **Tests** (`PartnerRequestManagerGuardTest` + a normalization test):
  - Normalization table: `"World  Vision International "` vs `"world vision international"` match; tab, NBSP (U+00A0), narrow NBSP (U+202F) inside → match; `"Asociación"` vs `"Asociacion"` → no match (accent-sensitive); empty / null name → no match, no exception.
  - Guard: same name same country → `DUPLICATE`, institution DAO `save` never called, copy still pending; same name other country → accepted; inactive match → accepted (the query excludes it — assert the query is asked for the request's country id); multi-HQ institution matching on its second HQ row → `DUPLICATE`.
  - Call order asserted on the recording doubles: `findForUpdate` → `lockForUpdate(countryId)` → `findActiveHeadquarteredIn(countryId)` → `save`.
- **Verification:** the probe (red without lock, green with); the suites; compile gate.
- **Falsifier:**
  - (a) Remove the country lock → probe red (two institutions).
  - (b) Rewrite the query as `EXISTS` / subquery → probe red (snapshot read), per J-15.
  - (c) Use `String.trim()` + `\\s+` only → the NBSP case red.
  - (d) Compare without lower-casing → the case-difference case red.
  - (e) Compare names only, ignoring the country → "other country" case red.
- **Red run:** baseline observed: `grep -c "lockForUpdate" LocElementDAO.java` → `0`; `acceptPartnerRequest` inserts with no lookup (`PartnerRequestManagerImpl.java:125-132`). Probe and test reds: `predicted — greenfield artifact (observed in T03)`.
- **Disqualifier:** a probe whose two sessions do not overlap in time (the second must start before the first commits — log both timestamps); a probe on a non-throwaway DB; a unit test standing in for the probe (no unit test can exercise D5).
- **Consumers:** `PartnerRequestManagerGuardTest` accept tests (`answering` proxy must answer the two new DAO methods); `InstitutionMySQLDAO`, `LocElementMySQLDAO` the only implementers.
- **Review:** `full` — concurrency and data integrity.
- **Done when:** probe red then green quoted; falsifiers (a)–(e) observed; isolation level recorded; P-18 marked verified (or the Pivot taken) in `execution.md`.
- **Skills:** `marlo-verify`, `error-handling-patterns`, `tdd`, `marlo-commit`.

---

### CHG-PARTUNIT-001-T04 — Actions: scope, listing refusal, outcomes, log lines

- **Status:** [ ] · **Size:** L · **Depends on:** T02, T03 · **Module:** marlo-web
- **Requirements:** SEC-001 and both its ACs (cross-unit; session unit does not decide); SEC-002 (response carries no request data); SEC-003 and AC FN-001 (refused before any partner request is read); OPS-001 / OPS-002 (exactly one line, replacing the generic WARN); FN-002, FN-003, FN-005, FN-006 / UI-004 (duplicate message names name and id, id as `String`), FN-008; SEC-005 (POST-only unchanged); FN-004 (superadmin namespace lists every unit).
- **Design refs:** § 2 marlo-web (actions, `BaseAction`), § 4 outcome strings, § 7.2, § 7.4, § 12, DD-2, DD-9, P-10, P-13, P-28, P-32, P-33, P-37, J-11, RB2-1.
- **Scope:**
  - `PartnerRequestAction`, `RejectPartnerRequestAction`, `EditPartnerRequestAction`: constructor gains `RoleManager`; scope built in `prepare()` from the current user (superadmin → `any()`; else `units(findAdministeredUnitIds)`; query failure → empty, logged); passed to the manager; the temporary `any()` of T02 removed.
  - `PartnerRequestAction.prepare()`: listing scope from `protected String invokedNamespace()` (default `ServletActionContext.getActionMapping().getNamespace()`); `/admin` → refuse a non-administrator of the session unit (`getCurrentGlobalUnit()`, never `getCrpID()`) before calling `findPendingForReview`, `execute()` then returns `BaseAction.NOT_AUTHORIZED`; `/superadmin` → `any()`; else `units({})`.
  - `statusName` in all three: `NOT_AUTHORIZED` → `notAuthorized`, `DUPLICATE` → `duplicate`.
  - Log lines: `NOT_AUTHORIZED` → one WARN (action, user id, request id, `requestUnitId`) **instead of** the generic WARN at `PartnerRequestAction.java:193`, `:404`, `EditPartnerRequestAction.java:89`, `RejectPartnerRequestAction.java:121`; `DUPLICATE` → one INFO (request id, matched id) instead of the generic WARN.
  - `addPartner` on `DUPLICATE`: action error from `…ui.outcome.duplicate` with the name and the id **as a `String`**; no email.
  - `BaseAction.isPartnerRequestReviewer()`: superadmin, or session unit ∈ administered; memoized in a `transient Boolean` field of the action instance, never static, never in the session (RB2-1).
  - `Backend route:` as above.
- **Tests** (`PartnerRequestReviewGuardTest`, extended; new cases for the two JSON actions):
  - D1: each of the five entry points, admin of 45 only, request of 47 → outcome `notAuthorized`, manager called with `units({45})` (argument asserted), mail double never called.
  - D2 pair: user `CRP-Admin` of 47 with session 45 → allowed (scope from the database); admin of 45 with session 47 → refused for a 47 request.
  - D3: `invokedNamespace()` overridden to `/admin`, non-administrator → `findPendingForReview` never called, `execute()` = `"403"`; `/admin` administrator of 45 → called with `units({45})`; `/superadmin` → `any()`; `/` → `units({})`; a request parameter `crpID=47` does not change the listing scope.
  - D9: for a refused request, the JSON published properties (`success`, `outcome`, `outcomes.*`) hold no name, acronym, requester or unit; the log appender holds exactly **one** event for the refusal, with the request unit id and no email (logger of the action, not root).
  - Duplicate: `addPartner` → one action error containing the name and `"4623"` (no `"4,623"`), one INFO event, no WARN, no mail.
  - Memo: two calls in one instance → one query (counting double); a new instance → a new query.
  - Existing 29 tests updated for the constructor; the two `superAdminValidation` pins are T05's.
- **Verification:** suites; compile gate; `grep -n "getCrpID()" PartnerRequestAction.java` → no use in the scope or listing code.
- **Falsifier:**
  - (a) Build the scope from the session unit → D2 pair red.
  - (b) Read the listing unit with `getCrpID()` → the `crpID=47` case red.
  - (c) Keep the generic WARN as well → the "exactly one event" case red.
  - (d) Format the id as a number → the `"4,623"` case red.
  - (e) Call `findPendingForReview` before the administrator check → the D3 "never called" case red.
- **Red run:** baseline observed: `statusName` returns neither `notAuthorized` nor `duplicate` (grep count `0` in all three files); `PartnerRequestReviewGuardTest` `@Test` count `29`; `grep -c isPartnerRequestReviewer BaseAction.java` → `0`. New cases: `predicted — greenfield artifact (observed in T04)`.
- **Disqualifier:** a log assertion on the root logger; a D2 test where the user's roles are mocked through Shiro instead of the T01 query; a listing test that sets the namespace through a request parameter.
- **Consumers:** `PartnerRequestReviewGuardTest` (29 tests, constructor); `marloInstitutions.js` reads `outcome` strings (`notAuthorized` exists, P-23; `duplicate` is page-only); `menu-admin.ftl` (T07) reads `isPartnerRequestReviewer()`.
- **Review:** `full` — authorization on five write paths.
- **Done when:** falsifiers (a)–(e) observed red then green and quoted; the temporary `any()` sites from T02 gone (`grep -n "ReviewerScope.any()" action/` → superadmin branch only).
- **Skills:** `marlo-verify`, `error-handling-patterns`, `tdd`, `marlo-commit`.

---

### CHG-PARTUNIT-001-T05 — Routes: `partnerRequestReviewer` and the two admin routes

- **Status:** [ ] · **Size:** S · **Depends on:** T04 · **Module:** marlo-web
- **Requirements:** SEC-001 (coarse gate; 403 for users who administer nothing); SEC-003 (admin route reachable by administrators); SEC-005 (POST-only kept); UI-002 (decisions return to the admin screen; admin accept route); FN-002.
- **Design refs:** § 2 (`struts-json.xml`, `struts-admin.xml`, interceptor), § 4, § 9, DD-5, DD-8, P-9, P-26, P-30, P-36, P-37, J-1, J-21, RA2-4.
- **Scope:**
  - `PartnerRequestReviewerInterceptor` (new, `interceptor/admin`): `setSession(invocation.getInvocationContext().getSession())` first, then superadmin or a non-empty `findAdministeredUnitIds(currentUser)`; else `BaseAction.NOT_AUTHORIZED`. Query failure → refuse, logged.
  - `struts-json.xml`: declare `partnerRequestReviewer` **inside the `json` package's `<interceptors>`**; replace `superAdminValidation` with it in the four routes, same position. The `json` package's own `403` result (`struts-json.xml:56`) serves the refusal.
  - `struts-admin.xml`: `{crp}/marloInstitutions` drops `superAdminValidation`, `input` → the admin shell; new `{crp}/addPartner` (`method="addPartner"`, `crpAdminStack`, `success` redirect to `${crpSession}/marloInstitutions` with `edit=true`, `phaseID`; `input` → admin shell).
  - Rewrite `PartnerRequestReviewGuardTest` pins `:927` and `:956` to assert `partnerRequestReviewer` in that position, its declaration inside the `json` package block, and the admin routes' refs.
  - `Backend route:` as above.
- **Tests:** interceptor unit test: user in the session but no `setSession` call path → would read null (falsifier); superadmin → invoke; administrator of one unit → invoke; administrator of none → `"403"`, `invoke()` never called; query throws → `"403"`. Static pins as above; `ValidPhaseStackCoverageTest` and `ErrorStatusLogStackCoverageTest` stay green.
- **Verification:** suites; compile gate; **a server start on the throwaway DB** in T08 is the only proof the Struts configuration loads (J-1) — T05 records "config load: deferred to T08 step 1".
- **Falsifier:**
  - (a) Declare the interceptor in `struts.xml` only → the static pin "declared inside the json package" red (and T08 step 1 startup fails).
  - (b) Remove the `setSession` call → the "administrator of one unit → invoke" case red.
  - (c) Leave `superAdminValidation` on the admin route → the admin-route pin red.
- **Red run:** baseline observed: the four JSON routes and `{crp}/marloInstitutions` carry `superAdminValidation` (awk over each `<action>` block → `yes` ×5); `grep -c 'name="{crp}/addPartner"' struts-admin.xml` → `0`; `partnerRequestReviewer` in any struts XML → `0` files. New cases: `predicted — greenfield artifact (observed in T05)`.
- **Disqualifier:** a static pin that greps the whole file instead of the `json` package block or the route's `<action>` block.
- **Consumers:** `PartnerRequestReviewGuardTest` (`:927`, `:956`); `ValidPhaseStackCoverageTest`, `ErrorStatusLogStackCoverageTest` (read every `struts-json.xml` route); `marloInstitutions.js` `statusOfError` (403 → `notAuthorized`).
- **Review:** `full` — route guards.
- **Done when:** falsifiers observed and quoted; the config-load check handed to T08 step 1.
- **Skills:** `marlo-verify`, `error-handling-patterns`, `tdd`, `marlo-commit`.

---

### CHG-PARTUNIT-001-T06 — Alerts: recipients, links, per-unit texts

- **Status:** [ ] · **Size:** M · **Depends on:** T01 · **Module:** marlo-web
- **Requirements:** FN-009 and its AC (To administrators, CC support, link; no administrator → support alone; BUT notifications off → nothing); FN-010 and its AC (decision emails unchanged); NF-003.
- **Design refs:** § 7.5, DD-7, P-20, P-24, P-25, P-27, J-2, J-10, J-17, R-4, R-5.
- **Scope:**
  - `PartnersSaveAction`, `RequestCountryOfficeAction`: constructor gains `RoleManager`; `findUnitAdministrators(unit)`; To = their emails joined with `", "`, CC = `config.getEmailNotification()`, BCC = null; empty → To = support, CC = null; one INFO on fallback (unit id); review URL `<baseUrl>/admin/<unit acronym>/marloInstitutions.do` for both.
  - `PartnerRequestEmails.newOffice` gains the review URL parameter.
  - `…mail.newOffice` gains the link placeholder in `global.properties`, `custom/aicrra.properties:2232`, `custom/aiccra3.properties:2233` (apostrophes doubled, no bare braces).
  - `Backend route:` as above.
- **Tests:** `PartnersSaveActionRequestTest` (`:271-276` rewritten: to = administrator, cc = NOTIFY, bcc null, body contains `/admin/AICCRA/marloInstitutions.do`); a no-administrator case (to = NOTIFY); notifications off → no mail. `RequestCountryOfficeActionAlertTest`: same three cases, **for the global text and both custom files** (the body contains the link and still "Requested from cluster: C77" for the custom ones, `:127`). `PartnerRequestEmailsTest`: the new signature. Decision-email tests untouched and green.
- **Verification:** suites; compile gate; i18n gate on the three changed lines.
- **Falsifier:**
  - (a) Edit only `global.properties` → the custom-file cases red.
  - (b) Keep `to = NOTIFY` → the administrator case red.
  - (c) Separate addresses with `","` without the space → the CC assertion (split on `", "`) — assert the exact To string.
- **Red run:** baseline observed: `grep -c 'superadmin/marloInstitutions.do' PartnersSaveAction.java` → `1`; both alerts call `sendMail.send(config.getEmailNotification(), null, config.getEmailNotification(), …)` (2 hits); `newOffice` placeholders `{0}`–`{4}` in all three files. New cases: `predicted — greenfield artifact (observed in T06)`.
- **Disqualifier:** a mail double that does not record to / cc / bcc separately; a test that loads only the global texts.
- **Consumers:** `PartnersSaveActionRequestTest`, `PartnersSaveActionClarisaModeTest` (constructor), `RequestCountryOfficeActionAlertTest`, `PartnerRequestEmailsTest`; `role-assignment-email-catalog.md` §17 (T08).
- **Review:** `checklist` — mechanical recipient change with pinned tests; no authorization logic.
- **Done when:** falsifiers observed and quoted; i18n gate clean.
- **Skills:** `marlo-verify`, `tdd`, `marlo-commit`.

---

### CHG-PARTUNIT-001-T07 — Views: shared body, shells, menu, chips, filter

- **Status:** [ ] · **Size:** M · **Depends on:** T04, T05 · **Module:** marlo-web (webapp)
- **Requirements:** UI-001 and its AC (shown to administrators in MARLO mode; BUT hidden in CLARISA mode; AND IT MUST NOT show to non-administrators); UI-002 (same screen; stays on admin); UI-003 (unit on partner cards and office groups; filter offers only units present); UI-004 (message rendered); RM-001 and its AC; FN-004 (superadmin filter).
- **Design refs:** § 5, § 2 webapp rows, DD-8, P-11, P-12, P-22, P-29, P-31, P-35 (MARLO's `APFreemarkerManager` only calls `super`, so the Struts policy holds — judgment RA2-3), J-9, J-19, RA2-2 (the § 1 table's "escaped notes" means the inherited auto-escaping, nothing added).
- **Scope:**
  - `global/views/partnerRequests/reviewBody.ftl` (new): today's `#prqPage` block moved from `superadmin/marloInstitutions.ftl`; notes keep `${message}`; **no `?html`** (P-35).
  - Shells: `superadmin/marloInstitutions.ftl` (superadmin menu, `prqReviewBase = "superadmin"`, `prqUnitFilter = true`); `crp/views/admin/marloInstitutions.ftl` replaced (main menu, admin menu, `prqReviewBase = "admin/${crpSession}"`).
  - `institutionRequestMacro.ftl`: accept form → `${prqReviewBase}/addPartner`; unit chip on partner cards and one chip per distinct unit on office groups; `data-prq-unit` on cards and office rows.
  - `menu-admin.ftl`: entry re-enabled in the rendered branch (`:29`) with the superadmin entry's condition (P-31) ∧ `action.isPartnerRequestReviewer()`.
  - `marloInstitutions.js`: unit select built from `data-prq-unit`; hides cards and office rows; hides empty office groups; office batches send only visible checked ids.
  - `marloInstitutions.css`: chip and select.
  - `global.properties`: `…ui.outcome.duplicate`, filter label, chip label (apostrophes doubled).
  - Both shells: bump `?<date>` on the JS and CSS includes.
  - `Design route:` per `impeccable-bridge` → `design-routing.md` *Design route line*: `platform web; page mode existing (`.prq` redesign of CHG-PARTREQ-001); lead existing design; ENV per impeccable-bridge Step 0` — the Implementer completes the ENV field from the bridge's probe.
- **Tests:** static pins (Java, like the revive spec's template pins): the accept form uses `prqReviewBase`; `menu-admin.ftl` entry condition contains `isPartnerRequestReviewer` and `partner_requests_to_clarisa`; `reviewBody.ftl` contains no `?html`; both shells include `reviewBody.ftl`; the old admin view no longer duplicates the body. **Render probe (RB2-2):** a template render of `reviewBody` with an action error containing `<b>` shows `&lt;b&gt;` — run in T08 step 2 on the live stack if no FreeMarker test harness exists, quoted in `execution.md`.
- **Verification:** static pins; i18n gate; cache-bust grep; the HITL pause in T08 (D8 has no automated gate).
- **Falsifier:**
  - (a) Use `${message?html}` → the "no `?html`" pin red (and the page fails to parse in T08).
  - (b) Drop `isPartnerRequestReviewer()` from the menu condition → the menu pin red.
  - (c) Leave the literal `superadmin/addPartner` → the form pin red.
- **Red run:** baseline observed: `grep -c 'action="superadmin/addPartner"' institutionRequestMacro.ftl` → `1`; `data-prq-unit` → `0`; `menu-admin.ftl:29` is a commented line (`<#--` count `1`). New pins: `predicted — greenfield artifact (observed in T07)`.
- **Disqualifier:** a pin that only proves presence of a string the page never renders — the behaviour is proven only at the T08 HITL check (recorded gap).
- **Consumers:** `marloInstitutions.js` selectors (`#prqPage`, `data-prq-*`), `allInstitutionsPopup.ftl`, `rejectInstitutionPopup.ftl` (included by both shells).
- **Review:** `checklist` — template wiring; the visual check is T08's HITL.
- **Done when:** pins green; the render probe handed to T08 step 2; cache-bust bumped.
- **Skills:** `impeccable-bridge`, `marlo-verify`, `marlo-commit`.

---

### CHG-PARTUNIT-001-T08 — Live proof on a throwaway DB, and the docs

- **Status:** [ ] · **Size:** M · **Depends on:** T01–T07 · **Module:** — (local stack, docs)
- **Requirements:** every AC with a live or visual part: FN-001, FN-002/003, FN-004/005, FN-006/007, FN-009, FN-011, SEC-001 (both), SEC-003, SEC-004, SEC-005, UI-001…UI-004, RM-001; D7 config load; D8.
- **Design refs:** § 18 gate map (D7, D8), § 15, P-30, P-35.
- **Scope (steps, each with a screenshot or log excerpt in `execution.md`):**
  1. Start the stack on the throwaway DB → the Struts configuration loads (J-1); the four JSON routes answer.
  2. Render probe (RB2-2): a duplicate refusal with an institution named `A <b>B</b>` shows the markup as text.
  3. As the `CRP-Admin` of 45, MARLO mode: menu entry visible; list shows only 45; accept, reject, edit, accept offices, reject offices; each stays on the admin screen.
  4. Crafted POST for a 47 request as the admin of 45 → `notAuthorized`, nothing written (SQL before / after).
  5. A user holding only `DM` (422) in 45 → no menu entry; the admin URL → 403; reads no request.
  6. Superadmin: global list with chips and the unit filter, including a mixed office group; decides a 47 request.
  7. Duplicate: accept a request whose name and country match an existing active institution → refused with the message.
  8. Alerts: submit a partner and an office request as a user of 45 → the mail log shows To = admin, CC = support, link to the admin route, for the AICCRA custom texts.
  9. CLARISA mode for 45 → the admin entry hidden after re-login.
  10. A unit with `crp_admin_active = false` → its admin sees no entry and the alert goes to support only.
- **Docs:** `docs/specs/domain/admin/agent-context.md` § *Partner Requests* (definition, routes, guards, listing scope, duplicate guard, log lines); `reports/ai-context/role-assignment-email-catalog.md` §17 (alert recipients, office link); `reports/ai-context/struts-critical-routing-catalog.md` (two admin routes, the JSON guard). These three are not shared constitutional files; they are this spec's deliverables.
- **Tests:** n/a (live). **Verification:** the ten steps' evidence; docs diff read against the design.
- **Falsifier:** step 4 — revert T04's scope to `any()` → the crafted POST succeeds (observed once, then restored, on the throwaway DB only).
- **Red run:** `predicted — greenfield artifact (observed in T08)` for step 4's falsifier.
- **Disqualifier:** any step run against `partroute_local` or a source DB; a screenshot taken before the page JS initialised; step 8 judged from the code instead of the mail log.
- **Consumers:** n/a.
- **Review:** `checklist` — evidence completeness; the owner sees the screenshots at the HITL pause.
- **Done when:** all ten steps evidenced; the owner confirms the screenshots (HITL); docs updated.
- **Skills:** `marlo-verify`, `cognitive-doc-design`, `marlo-commit`.

---

## 4. Dependency Graph

```
T01 (administers) ──► T02 (manager scope) ──► T03 (duplicate guard) ──► T04 (actions) ──► T05 (routes) ──► T07 (views) ──► T08 (live + docs)
        └──────────────────────────────────────────────────────────────────► T06 (alerts) ─────────────────────────────┘
```

No cycles. T06 may run in parallel with T02–T05. T02 leaves temporary `any()` call sites that T04 removes; both
ship in PR 1.

## 5. Coverage (scenario and clause → task)

| Requirement / clause | Task(s) |
|---|---|
| FN-001 / SEC-003 AC: admin sees only 45; BUT not 47; AND IT MUST refuse a non-administrator before any request is read | T02 (DAO scope), T04 (D3 "never called"), T08 steps 3, 5 |
| FN-002 / FN-003 AC: one institution + HQ; requester emailed with admin in CC; AND IT MUST NOT require further approval; AND the other four decisions behave as for a superadmin | T02, T04 (existing cases under the admin scope), T08 step 3 |
| SEC-001 / SEC-002 / OPS-001 AC: nothing written, no email; "not allowed" with no request data; exactly one WARN with unit 47; BUT the session unit does not matter | T02 (manager), T04 (D1, D9), T08 step 4 |
| SEC-001 AC "session unit does not decide": 47-admin in session 45 allowed; 45-admin in session 47 refused | T04 (D2 pair) |
| FN-004 / FN-005 / UI-003 AC: both units listed with acronym; filter to 47; superadmin decides either | T02 (`any()`), T04 (`/superadmin`), T07, T08 step 6 |
| FN-006 / FN-007 / UI-004 / OPS-002 AC: refused, pending, message names institution and id; one INFO; BUT other country accepted; AND inactive accepted; AND IT MUST NOT offer override | T03, T04 (message, INFO), T07 (no override control), T08 steps 2, 7 |
| FN-008 AC: mixed batch → 45 applied, 47 refused | T02, T07 (visible ids only) |
| FN-009 AC: To admin, CC support, admin link; no admin → support alone; BUT notifications off → none | T01 (administrators), T06, T08 steps 8, 10 |
| FN-010 AC: decision emails unchanged | T06 (untouched tests green) |
| FN-011 AC: carry-over without data script | T02 (DAO), T08 step 3 |
| SEC-004 AC: null-unit request refused for admins; superadmin list only | T01 (`permits`), T02 |
| SEC-005 AC: GET → nothing written, "not allowed" | T04 (existing POST tests under the admin scope) |
| UI-001 / UI-002 AC: entry shown and opens the admin route; stays on admin; BUT hidden in CLARISA mode; AND IT MUST NOT show to non-administrators | T04 (`isPartnerRequestReviewer`), T05 (routes), T07 (menu), T08 steps 3, 5, 9 |
| RM-001 AC: one template serves both routes | T07 |
| NF-001 | T01 (constants, role acronym only), every task's review |
| NF-002 | Common Done criteria (full suite); T02 refusal order; T03 probe |
| NF-003 | T06, T07 (i18n gate) |
| NF-004 | Common Done criteria |

## 6. Testing Plan

- **Unit:** T01 `ReviewerScopeTest`; T02 / T03 manager suite; T04 action suites (D1, D2, D3, D9, log counts); T05 interceptor test and static pins; T06 alert suites (global + both custom texts); T07 template pins.
- **Probes (throwaway DB):** T01 query fixture; T02 listing DAO; T03 two-session concurrency probe (P-18, D5).
- **Live / HITL:** T08, ten steps, screenshots, owner confirmation (D8 has no automated gate).
- **Regression:** full `marlo-web` suite with `-am` after every task.
- **Accessibility:** the unit select gets a label and joins the existing toolbar's keyboard order; checked in T08 step 6.

## 7. Operational Steps

- **No migration.**
- **Before release:** list, per production unit, whether `crp_admin_active` is on and who holds `CRP-Admin` with an email, so the owner knows which units will receive alerts (only units with administrators; the rest keep the support mailbox).
- **Release note items** (later, `marlo-release-notes`): unit administrators review their own requests; superadmins keep the global list; duplicate accepts are refused; alert recipients changed.

## 8. Rollback Plan

- **Code:** revert the spec's commits. Nothing persisted depends on them.
- **Data:** nothing to undo; decisions made by admins are ordinary decisions.
- **Partial:** removing only the menu entry hides the feature from admins; the superadmin path is unchanged by design.

## 9. PR Strategy

~1,450 LOC (design budget), above the ~400 threshold. Two PRs on the same branch, merged in order:

| PR | Tasks | Review first | Out of scope for this PR |
|---|---|---|---|
| PR 1 — authorization core | T01, T02, T03, T04, T05 | T02 (manager check) → T04 (actions) → T05 (routes) → T03 (guard + probe) → T01 | Screens, alerts |
| PR 2 — surfaces | T06, T07, T08 | T07 (views) → T06 (alerts) → T08 evidence | Authorization logic (PR 1) |

PR 1 is releasable alone only behind the unchanged superadmin menu: without T07 the admin entry stays hidden, so no
admin reaches the routes through the UI. PR descriptions follow `cognitive-doc-design` review rules and link each other.

## 10. Definition of Done

- [ ] All acceptance criteria verified (suites + T08 evidence, owner-confirmed screenshots).
- [ ] Compile gate, Checkstyle delta 0, full suite with `-am` green.
- [ ] P-18 verified by the T03 probe (or the Pivot resolved with the owner).
- [ ] Docs updated (T08).
- [ ] Each task marked done with verification notes in `execution.md`.
- [ ] Merged to `staging`.
