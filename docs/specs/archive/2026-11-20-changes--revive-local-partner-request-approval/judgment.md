# Judgment Day — Design review

**Target:** `design.md` (against `requirements.md`, `proposal.md`), spec `changes/revive-local-partner-request-approval`
**Mode:** judgment_day · `/akili-specify` Step 2.5 *Review Design*
**Verified at:** `staging` `3e4d08bd7e` (spec documents uncommitted)
**Judges:** two blind read-only Explore agents on `fable` (author `opus` ≠ auditor), identical prompt and scope
**Skill resolution:** `impeccable-bridge` (+ `references/design-routing.md`) passed as context to both judges
**Round:** 1 of 2 fixed (owner chose *Fix and Re-judge*) · **Status:** APPROVED after scoped re-judgment 2

## Counts (round 1)

| Class | Count |
|---|---|
| Confirmed SEVERE (both judges) | 4 |
| Suspect SEVERE (one judge) | 1 — re-read by the architect, holds |
| Contradictions between judges | 0 |
| INFO (warnings / suggestions) | 15 |

## Confirmed SEVERE

| ID | Finding | Judge A | Judge B | Evidence |
|---|---|---|---|---|
| JD-1 | `findForUpdate` locks the row but does not refresh the entity. `PartnerRequestAction.prepare()` has already loaded every active request into the request-wide session, so a racing second accept reads stale `active=true` and creates a duplicate institution. DD-3 is not a no-op guarantee | J-1 | J-1 | `PartnerRequestAction.java:259-268` (`findAll()` in `prepare`); `web.xml` OpenSessionInViewFilter; `PartnerRequests.hbm.xml:7` `optimistic-lock="none"`; no `LockMode` use in `marlo-data` |
| JD-2 | No premise row for the transaction model DD-2 / DD-4 rest on. `MARLOCustomPersistFilter` begins a transaction per request and commits after the chain; whether a manager `@Transactional` commits at method return is undetermined. P-5 cites `delete()` / `doesTableExist()`, not the save path | J-4 | J-2 | `MARLOCustomPersistFilter.java:115-122`; `AbstractMarloDAO.java:95,110` vs save `:407-411, :519-533`; conflicting repo docs (`center-email-otp-login/probes/README.md:184-185`, `bugfix/funding-source-phase-replication/design.md:28`) |
| JD-3 | Consumer row missing: the kept CRP-admin view `crp/views/admin/marloInstitutions.ftl` imports the three rewritten macros and loads the rewritten CSS/JS; it is absent from the footprint and the ledger | J-2 | J-3 | admin view `:4-11, :24, :54, :57` |
| JD-4 | P-4 does not reproduce (grep → 8 files, not 9) and omits readers outside the manager (`Institutions.java`, `InstitutionMapper.java`, `CountryOfficePOJO.java`, FTL readers). `InstitutionItem` (REST) also accepts / rejects with no pending guard | J-3 | J-4 | grep as written → 8; `grep -rln 'PartnerRequest\b'`; `InstitutionItem.java:360,465` |

## Suspect SEVERE (one judge)

| ID | Finding | Judge | Architect re-read |
|---|---|---|---|
| JS-1 | Edit is outside the lock and the pending check. With no `dynamic-update` and no optimistic lock, an edit loaded before an accept commits can flush a full-row UPDATE that restores `is_active=1, acepted=NULL` | B J-5 | **Holds** — `PartnerRequests.hbm.xml:6-7` has no `dynamic-update` and `optimistic-lock="none"`; `EditPartnerRequestAction.execute()` saves the whole entity |

## INFO (not auto-fixed)

| ID | Finding | Judges |
|---|---|---|
| I-1 | Notification switch: unit and check location undesigned (alerts vs decisions; `SendMailS` never suppresses mail to `email.notification`) | A J-5, B J-6 |
| I-2 | Request creation stays two transactions → orphan original on a second-save failure; reused copy says "could not be sent", UI-005 wants "could not be saved" | A J-7, B J-12 |
| I-3 | Stale cross-document prose: proposal Spec ID; "Phase 1 approved" vs header; OPS-001 vs OPS-002 in requirements §7; proposal POST scope (2 vs 5); proposal §7 footprint; proposal §9 REMOVED vs DD-8; proposal §13 no UI criterion; requirements §8 "every touched action"; design §12 "(R5)"; task ids cited before tasks exist | A J-8, B J-15 |
| I-4 | Reused classes not real as named: `.ptn-note` has no variants; `.phaseBadge--upcoming` not in `crpPhases.css`; `.ptn-*` / `.inf-ro` use `--ptn-*` / `--dlf-*`, needs a token mapping | A J-10, B J-10 |
| I-5 | Dialog a11y asserted, not designed (trap, ARIA, owner function); redirect → inline note path undesigned; no i18n key list for UI/JS strings | A J-11, B J-11 |
| I-6 | DA-001 "failure mid-way" cannot be proven by a mock test; needs a live rollback check on a throwaway DB | A J-13, B J-13 |
| I-7 | P-7 refuted: `getClarisaAPI*` and `ExternalPostUtils` become dead after DD-1 | A J-16, B J-16 |
| I-8 | Office JSON results serialise only `success`; `PartnerRequestOutcome` does not fit multi-row office methods | A J-6 |
| I-9 | "46 rows … consecutive ids 11863–11897" is inconsistent (35 ids; 2026-10-29 min id 10504) | A J-9 |
| I-10 | Restored superadmin menu entry depends on the `institutions` specificity value | A J-12 |
| I-11 | Per-action `interceptor-ref` order: put `superAdminValidation` before the stack so `prepare()` does not run first (precedent order) | A J-14 |
| I-12 | `BaseAction.isHttpPost()` already exists — reuse | A J-15 |
| I-13 | `SendMailS.send` is asynchronous: SMTP failures never reach the action (OPS-001 scope; DD-4 rationale) | B J-7 |
| I-14 | `jsonStack` has no `i18nFile`; unit subject prefix / layout may resolve to another unit in JSON-sent emails; subject arguments not listed; escape body arguments only | B J-8 |
| I-15 | Submit `name="save"` missing from the kept binding contract (`BaseAction.execute()` calls `save()` only when it arrives); refusal guards incomplete (original id, office row to accept-partner, wrong institution); P-17 line 10 → 11; P-19 "only" too strong | B J-9, B J-14, B J-16 |

## Premise Ledger re-run (merged)

| Row | Result |
|---|---|
| P-1, P-2, P-3, P-6, P-8, P-10, P-13, P-14, P-17, P-18, P-20 | confirmed (P-17 line 10 → 11; P-20 consumer list incomplete — JD-3) |
| P-4 | not reproducible (JD-4) |
| P-5 | weak — wrong lines; transaction model unrowed (JD-2) |
| P-7 (UNVERIFIED) | refuted — no caller outside `PartnersSaveAction` |
| P-9 (UNVERIFIED) | confirmed; `isHttpPost()` exists |
| P-11 | code confirmed; DB not re-run |
| P-12 | confirmed for `i18nFile` stacks only (I-14) |
| P-15 (UNVERIFIED) | not refuted — `ValidPhaseInterceptor` passes without `phaseID` |
| P-16 | not re-run (DB); figure inconsistent (I-9) |
| P-19 | partly contradicted (`.ui-dialog.deliverableTypesDialog` also restyled) |

Mechanisms both judges found sound: `superAdminValidation` returns the mapped 403; `crpAdminStack` + `superAdminValidation` resolves in the `admin` package; menu line citations; `email.notification` as recipient; numeric totals (20 ledger rows, 11 tasks, 1,450 LOC, six emails, five actions, four methods). No hard-rule violation.

## Round 1 — fix delta (applied by the architect, 2026-11-19)

| Finding | Fix | Where |
|---|---|---|
| JD-1 | `findForUpdate` = load + `session.refresh(entity, PESSIMISTIC_WRITE)`; native `SELECT … FOR UPDATE` fallback; new UNVERIFIED High row P-21 owned by the first backend task (live concurrency check) | design §2 marlo-data, §6, DD-3, P-21, R-8 |
| JD-2 | P-5 rewritten with primary sources (`WebAppInitializer.java:76`, `web.xml:41-47`, probe README `:185-186`, save path `AbstractMarloDAO.java:407-411, 519-533`); UNVERIFIED High for `addPartner.do`, owned by the first backend task (forced-failure rollback + commit-before-mail check); §6 transaction model paragraph | design §6, P-5, DD-2 |
| JD-3 | CRP-admin route redirects to `/superadmin/marloInstitutions`; admin view kept unedited and unrendered; consumer row P-22 | design §2, §4, DD-9, P-22 |
| JD-4 | P-4 re-run (8 files) and full reader list incl. REST, mapper, POJO, FTL; REST writer as R-7 | design P-4, §10, §13, R-7 |
| JS-1 | `editPartnerRequest` moved into the manager with lock + refresh + pending check; FN-007 and its AC gain the "not pending → nothing changes" clause | design §2, §6, DD-2; requirements FN-007, AC FN-007 |
| I-1 | Switch gate `PartnerRequestEmails.isEnabled(unit)` in the action, unit per email (session for alerts, request's for decisions) | design §10 |
| I-2 | `createPartnerRequest` transactional; copy key `marloRequestCreation.saveFailed` | design §2, §6, §7, DD-8 |
| I-3 | Stale prose swept in all three docs (Spec ID, status, OPS-002, POST scope, footprint, REMOVED, UI success criterion, §8 wording, R-4 ref, task ids) | proposal, requirements, design |
| I-4 | Component table with sources; "new" variants; token mapping table | design §5.2 |
| I-5 | `prqDialog` helper (role, aria-modal, labelledby, trap, Escape, return focus); redirect → action-message note; text catalogue | design §5.3–5.5 |
| I-6 | Live rollback / concurrency checks named as the DA-001 gate | design P-5, P-21, R-8 |
| I-7 | P-7 recorded as refuted → verified "no other caller"; dead code listed as follow-up | design P-7, §10, DD-1 challenge |
| I-8 | Office results carry `outcome`; `PartnerRequestOutcome` per request, office methods return a list | design §2, §4 |
| I-9 | Figure corrected: 46 rows, ids 10504–11897 (35 of them 11863–11897) | requirements C11, proposal R1, design P-16 |
| I-10 | P-24: no `institutions` parameter row → visible | design §5.3, P-24 |
| I-11 | Guard before `defaultStack` (precedent order) | design §4, DD-6 |
| I-12 | Reuse `BaseAction.isHttpPost()` | design §8, DD-7, P-9 |
| I-13 | OPS-001 scoped to build / dispatch; SMTP stays with the mail thread | requirements OPS-001, design §10, §11 |
| I-14 | Unit texts via `UnitTextProvider` (P-23); subject arguments listed; body-only escaping | design §10, P-23 |
| I-15 | Binding contract includes `name="save"`; refusal kinds `WRONG_KIND` / per-id office rules; skipped-location outcome; P-17 line 11; P-19 widened | design §5.3, §6, P-17, P-19 |

## Scoped re-judgment 1 (both judges, `fable`)

All five severe findings resolved (JD-1..JD-4, JS-1); I-3, I-4, I-6, I-8 partially. New findings:

| ID | Severity | Judges | Finding |
|---|---|---|---|
| RJ-1 | **SEVERE (confirmed)** | A R-1, B R-1 | Token mapping named tokens that do not exist (`--marlo-body`, `--marlo-muted`, `--ptn-brand`, `--ptn-body`, `--ptn-req`, `--dlf-req`, `--dlf-border*`); required colour mapped to the wrong value |
| RJ-2 | WARNING | A R-2, B R-6 | Office `SKIPPED_EXISTING` requester no longer mailed, contradicting AC FN-008 and today's code |
| RJ-3 | WARNING | A R-3, B R-5 | requirements §7 / proposal §13 still name a unit test as the DA-001 gate |
| RJ-4 | WARNING | A R-4, B R-4 | `outcome` JSON shape undefined; entity graph could serialise; keys missing for `notAllowed` / `notFound` / `wrongKind`; 403 branch in JS undesigned |
| RJ-5 | SUGGESTION | A R-5 | Component line citations drifted |
| RJ-6 | SUGGESTION | A R-6 | Admin route guard after `crpAdminStack` runs `prepare()` first |
| RJ-7 | SUGGESTION | A R-7 | Proposal §5/§11 still describe one transactional method |
| RJ-8 | WARNING | B R-2 | P-9 grep returns 10 REST hits as written |
| RJ-9 | WARNING | B R-3 | P-5 probe / save line citations off by one / two |
| RJ-10 | SUGGESTION | B R-7 | "four review writes" should be five |
| RJ-11 | SUGGESTION | B R-8 | Office batch lock order / concurrent duplicate location |

## Round 2 — fix delta (final round)

| Finding | Fix | Where |
|---|---|---|
| RJ-1 | Token mapping rewritten from the real `:root` blocks (`--marlo-text`, `--marlo-text-muted`, `--ptn-primary`, `--ptn-ink-soft`, `--ptn-required`, `--dlf-line*`, `--dlf-required` → `--marlo-danger-solid` `#c7401c`, radius / shadow names; focus ring kept literal) | design §5.2 |
| RJ-2 | Office `SKIPPED_EXISTING` requesters are mailed (as today, AC FN-008) | design §6, DD-4 |
| RJ-3 | Gate = refusal-rule unit tests + live rollback / concurrency checks | requirements §7, proposal §13 |
| RJ-4 | JSON contract table: plain status strings, `outcomes` map for offices, `includeProperties` per action, outcome never serialised, 403 → `notAuthorized` in the AJAX error handler; catalogue keys for all statuses | design §2, §4, §5.5, §8 |
| RJ-5 | Line citations corrected (`.container:not(.loginPage)` `:122`, `.ptn-tag` `:159-171`, `.ptn-chips` `:447-458`, `.dlf .inf-ro` `:1931-1932`) | design §5.2 |
| RJ-6 | Admin route: `superAdminValidation` before `crpAdminStack`; redirect precedent `struts-home.xml:158` | design §2, §4 |
| RJ-7 | Proposal §5 / §11 describe the six methods | proposal |
| RJ-8 | P-9 grep narrowed to resources / action / interceptor → 0 | design P-9 |
| RJ-9 | P-5 cites probe `:184-185`, save `:407-413` | design P-5 |
| RJ-10 | "five review writes" | design answer-first |
| RJ-11 | Ascending-id lock order; concurrent duplicate location accepted as R-9 | design §6, R-9 |

## Scoped re-judgment 2 (final, both judges, `fable`)

RJ-1..RJ-11 resolved per both judges. **No severe issue remains** (both verdicts). Non-severe observations:

| ID | Severity | Judges | Observation | Disposition |
|---|---|---|---|---|
| N-1 | WARNING | A N-2, B N-1 | §2 footprint not aligned with the §4 JSON contract (`outcomes` map, edit / reject `includeProperties`) | Editorial alignment applied (§2 rows) |
| N-2 | WARNING / SUGGESTION | A N-1, B N-2 | Admin route guard ahead of `requireUser` / `i18nFile` turns an expired session into a 403 | Applied: `logErrorStatus`, `i18nFile`, `requireUser`, `superAdminValidation`, then `crpAdminStack` |
| N-3 | SUGGESTION | A N-3, B N-5 | "Bare 403 with no body" inaccurate; 401 unmapped in the JS | Wording fixed; 401 mapped to the not-authorized note |
| N-4 | SUGGESTION | A N-4, B N-3 | `--ptn-*` / `--dlf-*` are scoped, not `:root`; info note mapped to a different value | Wording fixed; info note mapped to `--marlo-brand-tint*` (same values) |
| N-5 | SUGGESTION | B N-4 | P-9 "10 parameters" is 5 declarations + 5 uses | Wording fixed |

These post-approval edits are editorial alignments of non-severe observations; they change no design decision.

**Correction work units:** round 1 — 20 fix rows (5 severe, 15 info); round 2 — 11 fix rows (1 severe); post-approval editorial — 5.

JUDGMENT: APPROVED ✅
