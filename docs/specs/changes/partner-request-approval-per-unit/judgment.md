# Judgment Day — CHG-PARTUNIT-001 design

| Field | Value |
|---|---|
| Target | `design.md` (SHA-256 `0364e7b6e965…`), against `requirements.md` (`e2e5d222a558…`) and `proposal.md` (`5c6646cd8f8e…`), frozen copies in the session scratchpad |
| Repository | `e365e6a950`, branch `revive-local-partner-request-approval` |
| Mode | Blind dual review, read-only judges, run in parallel |
| Judges | A and B, both `sonnet` (the design was authored on `opus`: author ≠ auditor) |
| Round | 1 of 2 |
| Status | **Approved** after round 2 (terminal) |
| Date | 2026-11-27 |

## Summary

| Bucket | Count | IDs |
|---|---|---|
| Severe, confirmed by both judges | 3 | J-1, J-2, J-3 |
| Same defect, severity contradicted (one severe, one lower) | 2 | J-4, J-5 |
| Warning, reported by both | 9 | J-6 … J-14 |
| Warning or suggestion, one judge only (suspect / info) | 10 | J-15 … J-24 |

**P-18 settled at the source by both judges:** Hibernate 5.6.15.Final (`marlo-parent/pom.xml:15`), active dialect
`MySQL8Dialect` (`MarloDatabaseConfiguration.java:105`, applied after `hibernate.cfg.xml:7`'s `MySQLDialect`);
`getReadLockString` → `" for share"` (`MySQLDialect` → `" lock in share mode"`). Both are locking reads that see rows
committed after the snapshot. The snapshot already exists when the guard runs (`PartnerRequestMySQLDAO.java:75`,
plain `session.get` before the refresh), so the locking read is required. Open part (J-15): the lock clause covers
only the outer query block, so the institutions–locations condition must be a join in that block, not a subquery;
the T03 probe must run that exact shape.

## Confirmed severe (both judges)

| ID | Judges | Location | Finding | Evidence (as run by the judges) | Fix |
|---|---|---|---|---|---|
| J-1 | A-1, B-1 | design § 1, § 2 (`struts.xml` row), § 4, DD-5 | `partnerRequestReviewer` declared in `struts.xml` cannot be referenced from the four JSON routes | `struts-json.xml:6` `package name="json" … extends="json-default"`, not `marlo-default`; the package declares its own interceptors (`:12-45`, its own `superAdminValidation` at `:21`). A ref to a name declared only in `marlo-default` fails configuration load at startup | Declare the interceptor inside `struts-json.xml`'s `<interceptors>`; drop the `struts.xml` row; add a premise row |
| J-2 | A-2, B-3 | design § 2 (`global.properties` row), § 7.5, P-24; FN-009 | The office alert link never reaches units 45 and 47 | `custom/aicrra.properties:2232` and `custom/aiccra3.properties:2233` override `marloRequestInstitution.mail.newOffice` with `{0}`–`{4}` only; units 45 → `aicrra`, 47 → `aiccra3`; `PartnerRequestEmails.unitTexts(unit)` resolves the custom file; `RequestCountryOfficeActionAlertTest.java:127` pins the custom body | Edit both custom files and the pinning test; add a premise row for per-unit text overrides |
| J-3 | A-3, B-4 | design § 7.2, § 12; OPS-001, OPS-002 | "Exactly one" log line per refusal is contradicted by WARNs the design leaves in place | Generic non-DONE WARNs: `PartnerRequestAction.java:193` (`addPartner`), `:404` (`recordOutcomes`), `EditPartnerRequestAction.java:89`, `RejectPartnerRequestAction.java:121` | State that the generic WARN is replaced, not added to, for `NOT_AUTHORIZED` and `DUPLICATE`; test the line count |

## Severity contradicted (escalated to the owner)

| ID | Judges | Location | Finding | Evidence | Fix proposed |
|---|---|---|---|---|---|
| J-4 | B-2 **severe**, A-7 warning | design § 9, § 5 (menu), P-10; SEC-003, UI-001 | The admin listing gate (`accessibleAdmin`) is wider than the decision rule: it admits `crp:{0}:admin:canAcess` (permission 422) and the substring `isRole("CRP-Admin")`. A holder of 422 without 421 (role `DM` in 45 and 47) would see the menu entry and the list with requester data, while every decision answers `notAuthorized`. Contradicts the AC "refuse … before any request is read" and "MUST NOT show the entry to a user who cannot administer the unit". Also falsifies § 7.2's "a user with no administered unit who passed the coarse gate cannot exist" on the admin routes | `AccessibleAdminInterceptor:42`; `BaseAction.java:731-735` (`CRP_ADMIN_VISIBLE_PRIVILEGES`), `:8095-8100` (`contains`); SQL: `DM` holds 422, not 421, in 45 and 47 (0 users there, 3 in unit 1) | Gate the admin listing and the menu entry with the same "administers" definition (session unit ∈ administered units, or superadmin) |
| J-5 | B-5 **severe**, A-18 suggestion | design § 17 P-14 | P-14's consumer sweep misses consumers the change breaks | `CrpUsersActionDirectoryTest.java:540` `NoOpRoleManager implements RoleManager` (two new interface methods → compile break, B only); `PartnerRequestManagerGuardTest.answering()` Proxy throws on unanswered DAO methods, so the new `lockForUpdate` / `findActiveWithHeadquartersIn` calls break the accept happy-path tests; constructor changes in `PartnersSaveAction`, `RequestCountryOfficeAction` (new `RoleManager`) touch `PartnersSaveActionRequestTest`, `PartnersSaveActionClarisaModeTest`, `RequestCountryOfficeActionAlertTest` | Widen the P-14 grep to `RoleManager`, `RoleDAO`, `InstitutionDAO`, `LocElementDAO`, constructor callers; list every hit |

## Warnings reported by both judges (info; not auto-fixed)

| ID | Judges | Finding |
|---|---|---|
| J-6 | A-5, B-6 | P-15 does not reproduce: 61 groups confirmed, but 46 / 15 (B) or 46 / 15 and 44 / 17 (A), not 45 / 16. The SQL collation (`utf8mb3_general_ci`, accent-insensitive) and `[[:space:]]` (collapses NBSP) are not the Java normalization; under a binary collation the count is 57 |
| J-7 | A-6, B-11 | `crpAdminStack` includes `accessibleStage`: every `/admin` route needs the unit's `crp_admin_active = true`. No premise row; a unit without it gets alerts linking to a screen it cannot open, while the JSON routes still accept decisions |
| J-8 | A-10, B-8 | The duplicate message prints an institution name unescaped (`${message}` in the notes list) and formats the id with grouping separators (`4,623`) |
| J-9 | A-11, B-9 | UI-003 "each office group shows the unit acronym" is not designed (chip on partner cards only); filter semantics on a mixed-unit office group undefined; `marloInstitutions.css` missing from § 2 |
| J-10 | A-9, B-17 | "Active administrator" is undefined (`users.is_active` vs `crp_users.is_active`); 8 of 39 `CRP-Admin` holders lack an active `crp_users` row (inactive units); `SendMailS.java:360` uses the To list inside a regex |
| J-11 | A-12, B-14 | The namespace read has no premise row and no test seam; `ValidSessionCrpInterceptor` switches the session unit to the URL's `{crp}`; `setCrpID` is bindable from a request parameter, so the session unit must come from the session, never `getCrpID()` |
| J-12 | A-17, A-20, B-13, B-19 | The foreign row is X-locked and loaded before the scope check; `notFound` vs `notAuthorized` is an id-existence oracle; the log-only second lookup can be replaced by an unpublished unit id on the outcome |
| J-13 | A-15, B-15 | Budget: "12 rounds = 1.5 per task, two security tasks at 2 each" does not add up (10 or 13); LOC omits the moved FTL and docs; "indexed query of pending rows" — no index on `is_active` / `acepted` |
| J-14 | A-14, B-18 | P-25's grep also returns asset paths; P-14's pin lines are `:927` / `:956`, not the method headers `:942` / `:951`. Conclusions hold |

## One judge only (suspect / info)

| ID | Judge | Severity | Finding |
|---|---|---|---|
| J-15 | A-4 | warning | P-18 query shape: the locking clause covers the outer block only; an `EXISTS` subquery on `institutions_locations` would read the snapshot. Use a join in the same block or a native `FOR SHARE` |
| J-16 | A-8 | warning | `findPendingForReview(Long unitId)` with `null` = all units is fail-open: one missed null check lists every unit to an admin. Pass a scope or use two methods |
| J-17 | A-13 | warning | Decision emails are gated by the decider's session unit inside `SendMailS` (`:149-166`), not the request's unit |
| J-18 | B-7 | warning | Lock footprint: the share lock covers every `institutions_locations` row of the country (904 HQ institutions in US); office accepts for the same country wait on a partner accept (FK shared lock on the country row) |
| J-19 | B-10 | warning | The superadmin menu condition also ANDs the `institutions` specificity; the admin condition omits it |
| J-20 | B-12 | warning | The scope is computed twice on JSON routes (interceptor and action), outside the manager transaction; the hand-off is unspecified |
| J-21 | A-16 | suggestion | Admin `addPartner` result mapping: no `input` result; the superadmin redirect carries `edit` and `phaseID` |
| J-22 | A-19 | suggestion | `menu-admin.ftl`'s non-AICCRA branch is dead (`isAiccra()` always true); editing the `else` branch suffices |
| J-23 | B-16 | suggestion | Specify the Java normalization regex (NBSP, tabs) |
| J-24 | A-5 (part) | suggestion | Accent-only differences (ids 3400 / 6702) are not duplicates under Java normalization; decide whether that is intended |

## Premise Ledger, as judged

| Row | A | B |
|---|---|---|
| P-1 … P-9, P-12, P-13, P-16, P-19 … P-23 | confirmed | confirmed |
| P-10 | confirmed; predicate differs from SEC-001 (J-4) | citation confirmed, interpretation contradicted (J-4) |
| P-11 | confirmed; live path incomplete (J-7) | confirmed |
| P-14 | confirmed with omissions (J-5) | confirmed with omissions (J-5) |
| P-15 | does not reproduce in part (J-6) | does not reproduce for 45 / 16 (J-6) |
| P-17 | confirmed, local server only | confirmed |
| P-18 | settled in part (J-15) | settled at source; probe still owed |
| P-24 | confirmed; incomplete (J-2) | confirmed; incomplete (J-2) |
| P-25 | extra hits (J-14) | partially reproduces (J-14) |

Missing rows named by the judges: the json package's interceptor scope (J-1), per-unit text overrides (J-2),
`crp_admin_active` (J-7), session switch and namespace read (J-11).

## Round log

- 2026-11-27 — Round 1 judged. 3 severe confirmed, 2 severity contradictions escalated. Awaiting the owner.
- 2026-11-27 — Owner: "corrige y re-juzga, incluye los avisos" — J-4 and J-5 treated as severe; all warnings and suggestions (J-6 … J-24) included in the fix. Fix applied by the parent (inline fix actor) to `design.md` (rewritten: § 2, § 4, § 5, § 7.1–7.5, § 9, § 12–14, DD-1, DD-5, DD-7, DD-9 new, budget, Premise Ledger P-10/P-14/P-15/P-18/P-20/P-25 amended, P-26 … P-34 new, R-5/R-6 new) and to `requirements.md` (SEC-001, SEC-003, § 2 counts, Q-6 row, Decision Log with closure sweep). Delta frozen as `fix-delta.diff` (SHA-256 `6c7b0c5b1ff9…`) with post-fix `design.md` (`bd6d01ed6841…`) and `requirements.md` (`b1bc9369de29…`). Scoped re-judgment launched: both judges see only this ledger plus the delta.
- 2026-11-27 — Scoped re-judgment 1. **Both judges: J-1 … J-24 resolved or accepted-as-documented (J-12, J-17, J-18, J-24); 0 unresolved severe.** New findings on the delta:
  - **Confirmed severe (contradicted premise, both):** RA-1 = RB-6 — P-8 / § 2 said `RoleMySQLDAO` has no native SELECT; `RoleMySQLDAO.java:72-73` has one.
  - **Contradiction settled by the parent with one re-run:** RA-7 (use `?html`) vs RB-2 (`?html` is a parse error, notes already escaped). Source: `struts2-core-6.8.0-sources.jar` `FreemarkerManager.java:370-371` enables auto-escaping with `HTMLOutputFormat`; `pom.xml:25` `struts2.version` 6.8.0. **RB-2 holds; RA-7 is withdrawn; J-8's XSS half was a false premise.**
  - Warnings, both: RA-3 = RB-1 (`notAuthorized` is not a result name; `BaseAction.NOT_AUTHORIZED` = `"403"`), RA-2 = RB-4 (`requirements.md:154` "45 existing"), RA-5 = RB-5 (P-33 `:55` → `:56`). One judge: RA-4 (UI-001 text, Q-1 row, owner approval of the narrowing), RB-3 (the coarse interceptor must `setSession` before reading the user).
  - Suggestions: RA-6 = RB-7 (membership is stricter than `existCrpUser`), RA-8 = RB-9 (memoize the menu check; "no partner request" wording; P-29 wording), RB-8 (P-21 citation).
- 2026-11-27 — Round 2 fix (final bounded round, parent inline): all of the above applied. `design.md`: § 2 (RoleDAO, reviewBody, interceptor, BaseAction memo, struts-admin row), § 4, § 5 escaping, § 7.1 rule 1, § 7.4, § 13, DD-1, P-8, P-21, P-28, P-29, P-33 amended; P-35 (auto-escaping), P-36 (interceptor session), P-37 (`"403"`) new; count line 36 / 1. `requirements.md`: § 4 "42", UI-001 text, Q-1 row, Decision Log with a widened sweep. Delta frozen as `fix-delta-2.diff`. Scoped re-judgment 2 launched.
- 2026-11-27 — Scoped re-judgment 2. **Both judges: RA-1 … RA-8 and RB-1 … RB-9 resolved; RA-7 withdrawn, settlement confirmed independently by both (`FreemarkerManager.java:370-371`, `APFreemarkerManager` only calls `super`). 0 unresolved severe, 0 fix-caused severe.** Premise rows P-8, P-21, P-28, P-29, P-33, P-35, P-36, P-37 confirmed by both; count line 36 + 1 = 37 rows confirmed.
  - Info carried to `tasks.md` (not fixed in the design, per protocol): RA2-1 (warning, A) — other templates still use `?html`; B found the crp one unreferenced, and the project's own record holds that the legacy `?html` templates fail to parse under Struts 6, which corroborates P-35 → the T-task that builds `reviewBody` runs a render probe (RB2-2). RA2-2 — § 1 table wording "escaped notes" reads as an added step. RA2-3 — P-35 can name `APFreemarkerManager` (calls `super`). RA2-4 — the JSON routes' 403 result lives in the `json` package (`struts-json.xml:56`), not only `struts.xml`. RB2-1 — the menu memo must be a per-instance `transient Boolean`, never cached across requests.

## Terminal receipt

| Item | Value |
|---|---|
| Target | `design.md` of `changes/partner-request-approval-per-unit`, final SHA-256 `09b8d5135493…` (with `requirements.md` `36231ede9ba0…`) |
| Rounds | 2 fix rounds, 2 scoped re-judgments (ceiling reached, not exceeded) |
| Round 1 | 3 severe confirmed, 2 severity contradictions (owner: treat as severe), 19 warnings / suggestions — all fixed |
| Re-judgment 1 | 0 unresolved; 1 fix-caused severe (both), 1 judge contradiction settled by source, 8 warnings, 7 suggestions — all fixed |
| Re-judgment 2 | 0 unresolved severe, 0 fix-caused severe, 1 warning, 5 suggestions — carried to `tasks.md` as info |
| Correction work units | Parent inline fix actor, twice (`fix-delta.diff`, `fix-delta-2.diff`) |
| Owner decisions still pending | The narrowing of SEC-001 (active membership, `crp_admin_active`) — at the Phase 2 gate |
| Skills | `judgment-day`; judges ran without project skills (spec review, no code) |

**JUDGMENT: APPROVED ✅**

