# Partner request routing per Global Unit (CLARISA or MARLO) — Tasks

**Spec ID:** CHG-PARTROUTE-001
**Status:** Ready — approved 2026-11-20 (owner: "sí, continúa con la checklist")
**Owner:** Kenji Tanaka (IBD)
**Last Updated:** 2026-11-20
**Implements design:** docs/specs/changes/partner-request-routing-specificity/design.md (judgment APPROVED, round 2)
**Branching:** the existing branch `revive-local-partner-request-approval` (the MARLO-mode code is not on `staging` yet; proposal R3)
**Target merge:** `staging`, with or after the revive work. One PR (§9)

> **Answer first.** 5 tasks.
> 1. **T01** seeds the specificity: migration, both constants and a parity test.
> 2. **T02** adds the routing to `PartnersSaveAction` with its tests, and rewrites the test that pinned "no CLARISA".
> 3. **T03** wires the three templates.
> 4. **T04** updates the admin agent-context.
> 5. **T05** proves both modes live on a throwaway DB, and settles P-10 first.
>
> Baselines quoted below were **observed on `0edf846105`**. Reds of tests that do not exist yet are marked
> `predicted — greenfield` and must be quoted in `execution.md` by the task that creates them.

---

## 1. Execution Context

| Item | Value |
|---|---|
| Java / build | Java 17; `marlo-parent/pom.xml` |
| Compile gate | `rm -rf marlo-web/target/classes marlo-web/target/test-classes marlo-data/target/classes && mvn -q -o -pl marlo-data,marlo-web -am test-compile -DskipTests` (expect ~2400 files in marlo-data, ~1040 in marlo-web) |
| Checkstyle | checkstyle 8.18 jar with `configuration/marlo-checkstyle.xml` on changed files, delta vs HEAD (`marlo-verify`) |
| Tests | `mvn -q -o -pl marlo-web -am test` (keep `-am`) |
| Local run | `scripts/run-marlo-java17.sh`. Never run it while another agent builds (CLAUDE.md *Concurrency*) |
| Live DB | A **throwaway copy** of the local AICCRA schema, never the source databases. Local login per `~/.marlo-local-credentials` |
| Browser | Playwright / Chrome automation. Wait for the page JS to initialise before clicking Save |

## 2. Pre-flight Checklist

- [ ] `requirements.md` and `design.md` approved; `judgment.md` = APPROVED.
- [ ] On branch `revive-local-partner-request-approval`, clean tree.
- [ ] Spec documents committed first with `[SPEC:changes/partner-request-routing-specificity]` (`marlo-commit`; ask before committing).
- [ ] Throwaway DB copy created for T01 (migration runs) and T05.

## 3. Task List

**Common Done criteria for every code task** (not repeated below):
- Clean compile gate green, with the expected file counts.
- Checkstyle delta 0 on changed files.
- GPL header on new Java files; English only.
- `marlo-verify` hygiene pass: CRLF preserved, null-safe log arguments, no email in logs, no `org.jfree.util.Log`.
- No `.properties` change, so the i18n gate is n/a (design P-14).

---

### CHG-PARTROUTE-001-T01 — Specificity seed: migration, constants, parity test

- **Status:** [x] · **Size:** S · **Depends on:** — · **Module:** marlo-web, marlo-data
- **Requirements:**
  - MIG-001 and AC MIG-001 ("exactly one row per existing type 1–4"; "AND `custom_parameters` gains no rows").
  - NF-001 (constant in both `APConstants`, value = `parameters.key`).
  - AC NF-001 (second clause).
- **Design refs:** §3, §9, DD-1, P-12, J-3.
- **Scope:**
  - Migration `V2_6_0_<date +%Y%m%d_%H%M>__AddPartnerRequestsToClarisaSpecificity.sql`, built with `INSERT … SELECT FROM global_unit_types WHERE id IN (1,2,3,4) AND NOT EXISTS`.
  - The migration carries the design §3 description verbatim (no apostrophe) and a header comment stating the guards.
  - `PARTNER_REQUESTS_TO_CLARISA = "partner_requests_to_clarisa"` in both `APConstants.java`.
  - New `PartnerRoutingSpecificityParityTest`, modelled on `OtpSpecificityConstantParityTest`. It asserts:
    - both constants equal the key;
    - exactly one migration seeds the key;
    - that migration writes no `custom_parameters`;
    - its description holds no `'`.
  - `Backend route: stack other; implement lead existing design support none — project conventions; test-suite lead the spec's scenarios support none — project conventions`
- **Tests:** the parity test (above).
- **Verification:**
  - Compile gate and the test suite.
  - `marlo-migration` checklist.
  - Run the SQL **twice** on the throwaway copy and once on an empty schema holding only the `global_unit_types` and `parameters` DDL.
  - After each run, `SELECT global_unit_type_id, COUNT(*) FROM parameters WHERE \`key\`='partner_requests_to_clarisa' GROUP BY 1` returns 1 per existing type, and `custom_parameters` row count is unchanged.
- **Falsifier:**
  - (a) Set the marlo-data constant to `partner_request_to_clarisa` → parity red, naming the file.
  - (b) Put `user's` in the description → parity red.
  - (c) Drop the `NOT EXISTS` guard → the second DB run returns `2` per type.
- **Red run:**
  - Baseline observed at `0edf846105`: `grep -c "partner_requests_to_clarisa"` → `0` in both `APConstants.java`, and `0` migrations contain it.
  - The parity test itself: `predicted — greenfield artifact (observed in T01)`.
- **Disqualifier:** a parity test whose expected key is read from one of the files it checks (KZ directory-abstraction-2). The key must be a literal in the test.
- **Consumers:**
  - `OtpSpecificityConstantParityTest` and `PasswordSwitchConstantParityTest` read `APConstants` for their own keys only. They must stay green (design P-7).
- **Review:** `checklist` — mechanical seed following a proven precedent.
- **Done when:**
  - Falsifiers (a)–(c) observed red, then green, and quoted in `execution.md`.
  - The DB counts are recorded.
- **Skills:** `marlo-migration`, `marlo-verify`, `marlo-commit`.

---

### CHG-PARTROUTE-001-T02 — Routing in `PartnersSaveAction` with both-mode tests

- **Status:** [x] · **Size:** L · **Depends on:** T01 · **Module:** marlo-web
- **Requirements:**
  - FN-001 and its AC (absent row → MARLO; BUT `"TRUE "` / `yes` / empty → MARLO).
  - FN-002 and AC FN-002 / NF-002 (AND IT MUST NOT contact CLARISA).
  - FN-003, FN-004 and AC FN-003 / FN-004:
    - exactly one request with every field;
    - AND IT MUST carry the unit's own source text (AICCRA `Cluster C…`, global `Project P…`);
    - BUT no unit word as a Java literal;
    - BUT no `partner_requests` row;
    - AND no support alert.
  - FN-005 and AC FN-005 / OPS-001:
    - non-2xx, exception, missing config and no acronym → failure with the entered data;
    - exactly one line from the action's logger;
    - BUT no MARLO fallback;
    - AND no alert;
    - AND IT MUST NOT log the email or credentials.
  - FN-006 and its AC (the stored parameters decide; the session `true` is ignored).
  - FN-010 and its AC (no unit → nothing stored or sent, "could not be saved", one log line).
  - SEC-002, OPS-001.
  - NF-001 (no `isAiccra()`, no unit literal, constant used).
  - NF-002 (MARLO-mode tests green; the pinned test rewritten).
- **Design refs:** §1, §9, §10, §11, §17 rows D1–D4 and D11, DD-2, DD-4, DD-5, P-2, P-4, P-5, P-15, P-16, P-17.
- **Scope:**
  - `PartnersSaveAction`:
    - FN-010 guard first.
    - `readClarisaMode()` from `loggedCrp.getCustomParameters()`, skipping null rows, parameters and keys; active rows only; key equal to the constant; `Boolean.parseBoolean`. The result goes into the `clarisaMode` field, read by public `isClarisaMode()`.
    - CLARISA branch:
      - config read once (host, username, password blank → failure);
      - null acronym → failure;
      - `buildClarisaPayload(...)` with the fields of design §10 (`misAcronym` = acronym lower-cased with `Locale.ROOT`; `requestSource` = the source the `add*Message` methods set, fallback `"Request made from MARLO"`);
      - `protected int postToClarisa(String url, String json)` (Basic auth, JSON headers, 10/10/30 s timeouts in named constants, try-with-resources);
      - 2xx → `messageSent`; anything else → `requestFailed` + `INPUT`;
      - log lines exactly as design §11, class name only, never the throwable.
    - MARLO branch unchanged.
  - Rewrite `PartnersSaveActionRequestTest.testNoReferenceToTheExternalServiceRemains` (DD-5):
    - keep the `RequestCountryOfficeAction` checks;
    - for `PartnersSaveAction`, keep only the `isAiccra()` ban;
    - add a behavioural MARLO-mode case in `DrivenAction`: override `postToClarisa` to record and fail if called.
  - New `PartnersSaveActionClarisaModeTest`:
    - its own `APConfig` subclass answering the CLARISA getters (P-17);
    - `loggedCrp` set by reflection as in the existing suite.
  - `Backend route: stack other; implement lead existing design support none — project conventions; error-model lead error-handling-patterns support none — project conventions; test-first lead tdd support none — project conventions`
- **Tests** (design §17 gate map):
  - **D1:** CLARISA mode → seam called once, manager never, mail never. MARLO mode, AICCRA unit → manager once, seam never.
  - **D2:** the URL is `host + /api/partner-requests/create`. Every JSON field is asserted against **literals**:
    - unit 45 `AICCRA` → `misAcronym` `aiccra`;
    - unit 47 `AICCRA_III` → `aiccra_iii`;
    - with `custom/aicrra.properties` loaded → `requestSource` starts `Cluster C`;
    - with the global texts only → `Project P`.
  - **D3:** each of these gives `requestFailed`, `INPUT`, manager never and mail never: seam returns 400, seam throws `IOException`, seam throws `SocketTimeoutException`, blank host / username / password (seam never called), null acronym (seam never called), `loggedCrp` null.
  - **D4:**
    - Every failure produces exactly **one** event on the action's logger.
    - No event contains the requester email, the username, the password, or `Authorization`. The test's config uses distinctive values for all of them, and the thrown exception's message contains the password and the email.
    - The thrown event has no throwable attached.
  - **D11:**
    - session `true`, stored parameters say `false` → stored, seam never;
    - stored `"TRUE "`, `yes`, `""` → MARLO;
    - stored `true` on an inactive row → MARLO.
  - The page sees the mode: `isClarisaMode()` is `true` after a CLARISA failure and `false` after a MARLO one.
- **Verification:**
  - Compile gate and the test suite.
  - `grep -c "isAiccra()" PartnersSaveAction.java` → `0`.
  - `grep -cE "\"(Cluster|AICCRA|aiccra)\"" PartnersSaveAction.java` → `0`.
- **Falsifier:**
  - (a) Invert the branch (`if (!clarisaMode)`) → the D1 cases red on both suites.
  - (b) Read the mode with `this.hasSpecificities(...)` → the D11 session case red.
  - (c) Pass `e` to `LOG.error` → the D4 "no throwable" case red.
  - (d) Hardcode `"aiccra"` → the unit 47 case red.
  - (e) Build the source from `getText` on the global texts only → the `custom/aicrra.properties` case red.
  - (f) Store before branching → the CLARISA-mode "manager never" case red.
- **Red run:**
  - Baseline observed at `0edf846105`: `grep -ci "clarisa" PartnersSaveAction.java` → `0`, and the pin test `testNoReferenceToTheExternalServiceRemains` asserts that (`PartnersSaveActionRequestTest.java:337-348`).
  - The new tests: `predicted — greenfield artifact (observed in T02)`. Each falsifier's red is quoted in `execution.md`.
- **Disqualifier:**
  - A seam double whose expected JSON is built from the same request object the test submits (KZ directory-abstraction-2).
  - A green with only the global texts loaded.
  - A log assertion attached to the root logger, which would also count `APConfig`'s lines (OPS-001 counts the action's logger).
  - Timeouts are not exercised by any unit test. The test proves that a timeout **exception** fails visibly; that the constants are applied is a **presence** read in review, and is recorded as a gap.
- **Consumers:**
  - `PartnersSaveActionRequestTest`: positional constructor `:89-98`; `DrivenAction.isAiccra()`; the pin at `:337-348`, rewritten here.
  - `PartnersSaveActionRequestIdTest` (`readRequestId`), which must stay green.
  - `partnersSave.ftl` reads `requestFailed` (T03 adds `clarisaMode`).
  - The `superadmin/marloInstitutions` review link in the MARLO-mode alert stays unchanged.
- **Review:** `full` — integration restored behind a switch; secrets and routing.
- **Done when:**
  - Falsifiers (a)–(f) observed red, then green, and quoted in `execution.md`.
  - The whole `marlo-web` suite is green with `-am`.
- **Skills:** `marlo-verify`, `error-handling-patterns`, `tdd`, `marlo-commit`.

---

### CHG-PARTROUTE-001-T03 — Templates: menu entry, office link, failure text

- **Status:** [x] · **Size:** S · **Depends on:** T01, T02 · **Module:** marlo-web (FTL)
- **Requirements:**
  - UI-001 and its AC: CLARISA → hidden; AND IT MUST show in MARLO mode; BUT the guards are unchanged.
  - UI-002 and its AC: CLARISA → `country.email` + `mailto:MARLOSupport@cgiar.org` + "."; BUT no `#requestModal` trigger; AND MARLO keeps the trigger.
  - UI-003 (MARLO and no unit → `saveFailed`; CLARISA → `sendFailed`).
  - SEC-001 (no guard touched).
- **Design refs:** §5, P-13, P-14, P-15, J-12, J-13.
- **Scope:**
  - `menu-superadmin.ftl`: append `&& !action.hasSpecificities('partner_requests_to_clarisa')` to the `institutions` entry's `visible` expression, in **both** branches (lines 21 and 34).
  - `projectPartners.ftl:541-544`: in CLARISA mode, the pre-revive markup from `66947636a6~2:…/projectPartners.ftl:531`; otherwise today's trigger.
  - `partnersSave.ftl:37-39`: the key follows `action.clarisaMode`.
  - Extend `PartnerRoutingSpecificityParityTest` with static pins that read these three files.
  - `Design route: none — behaviour-only visibility toggle of existing elements, no visual change (design §5)`
- **Tests:** the static pins:
  - each `institutions` entry contains the condition;
  - the office block has the key in an `[#if]` whose true branch holds `mailto:MARLOSupport@cgiar.org` and no `data-target="#requestModal"`;
  - `partnersSave.ftl` references both `saveFailed` and `sendFailed`.
- **Verification:**
  - The test suite.
  - `grep -c "partner_requests_to_clarisa"` → `2` in `menu-superadmin.ftl` and `1` in `projectPartners.ftl`.
  - `grep -c "sendFailed" partnersSave.ftl` → `1`.
  - The struts XML is unchanged (`git diff --stat` shows no `struts-*.xml`).
- **Falsifier:**
  - (a) Edit only the `[#else]` branch → the "both branches" pin red.
  - (b) Leave the trigger inside the CLARISA branch → the office pin red.
  - (c) Key the failure text on `requestFailed` only → the `sendFailed` pin red.
- **Red run:** baseline observed at `0edf846105`:
  - `grep -c "partner_requests_to_clarisa"` → `0` in all three templates.
  - `grep -c "sendFailed" partnersSave.ftl` → `0`.
  - `grep -c "mailto:" projectPartners.ftl` → `1` (`:672`, a person email, unrelated).
- **Disqualifier:** static pins prove that the condition is present, not how the page renders (no FTL render harness exists; requirements §7 D8). Rendering is proven only in T05.
- **Consumers:**
  - `projectPartners.js` keeps the `#requestModal` dialog, which still renders (design §5).
  - No other template has the trigger (P-13).
  - Design P-8: the `institutions` menu entry is defined only in `menu-superadmin.ftl`. The other `marloInstitutions` readers (the screens, their macros and styles, the struts mappings, `menu-admin.ftl` with its entry already removed, and the alert's `reviewUrl`) need no change; the MARLO-mode alert link stays.
- **Review:** `checklist` — three conditions; behaviour proven live in T05.
- **Done when:**
  - Falsifiers (a)–(c) observed red, then green.
  - Completion report line: `Design route: none — no playbook run`.
- **Skills:** `marlo-verify`, `marlo-commit`.

---

### CHG-PARTROUTE-001-T04 — Admin agent-context: the two modes

- **Status:** [x] · **Size:** XS · **Depends on:** T02 · **Module:** docs
- **Requirements:** FN-008 (the switch and its timing documented); proposal §5 "Docs".
- **Design refs:** §9, §14, DD-2, DD-3, R-4, R-5.
- **Scope:** `docs/specs/domain/admin/agent-context.md` § *Partner requests*. Add:
  - the key and the constant;
  - what each mode does;
  - that the submit reads the stored parameters while the templates read the session (the lag);
  - that office requests stay local;
  - the leftover pending requests (FN-009);
  - the open `misAcronym` question for non-AICCRA units (P-9).
- **Tests:** none (documentation).
- **Verification:** every claim cites a `file:line` or a design section. Sweep the section for statements contradicted by this spec, e.g. "never sent to CLARISA" and "every Global Unit": `grep -n "CLARISA\|every Global Unit" docs/specs/domain/admin/agent-context.md`, then read each hit.
- **Falsifier:** leave a sentence that says requests are "never sent to CLARISA" → the sweep finds it.
- **Red run:** baseline observed at `0edf846105`: the section exists (`agent-context.md:10-11` points to it). Hit count quoted in `execution.md` before editing.
- **Disqualifier:** a sweep that greps only for the new key; the old absolute claims must be searched.
- **Consumers:** agents reading the admin module; none in code.
- **Review:** `checklist` — rules text read by agents.
- **Done when:** sweep hits listed with an action each.
- **Skills:** `cognitive-doc-design`, `marlo-commit`.

---

### CHG-PARTROUTE-001-T05 — Live proof of both modes (settles P-10 first)

- **Status:** [x] · **Size:** M · **Depends on:** T01, T02, T03 · **Module:** — (verification)
- **Requirements:** requirements §7 D2 (live call), D8, D9 and D11 runtime. Each AC below is checked on the running app:
  - **FN-008:** Parameters screen saves per unit; the next submit follows the change without a new login; BUT other units are untouched.
  - **FN-009:** pending requests stay listed and decidable.
  - **FN-007:** office request stays local in CLARISA mode.
  - **UI-001 / SEC-001:**
    - entry hidden in CLARISA mode on the `[#else]` branch (P-15);
    - shown in MARLO mode;
    - the route still refuses a non-superadmin;
    - a GET still changes nothing.
  - **UI-002:** the office link per mode.
  - **UI-003:** the failure text per mode.
- **Design refs:** §14, §17 rows D2, D8, D9, D11, P-6, P-9, P-10, P-15.
- **Scope**, in order:
  1. **First step, settles P-10:** check for a CLARISA test host and credentials in the owner's local, gitignored `marlo-dev.properties`. Never copy or print the values.
     - If present: one live CLARISA-mode submit against it; record the status.
     - If absent or rejected: record `P-10: accepted risk` with the reason, and run the CLARISA-mode submit against an unreachable host to prove the visible failure.
  2. Start the app on the throwaway copy (Flyway applies T01).
  3. MARLO mode:
     - submit a request → one original + one copy stored, alert dispatched;
     - check the menu entry and the office link.
  4. Superadmin → *Parameters* → set the unit to `true` → save → confirm the `custom_parameters` row, and that other units have no row or keep theirs.
  5. **Without a new login:** submit again → nothing stored (count unchanged), CLARISA outcome as in step 1.
  6. Log out and back in → the menu entry is hidden, the office link shows the mailto, the office request still stores locally.
  7. Pending requests from step 3 are still listed via the URL and decidable.
  8. Switch to a second unit that has no row → submit → stored in MARLO (D11 runtime).
  9. A non-superadmin opening the review URL is refused.
- **Tests:** none new (live evidence).
- **Verification:** for every step, a screenshot plus the DB count or log line, recorded in `execution.md`. Owner visual check at the HITL pause.
- **Falsifier:** before step 4, temporarily set the specificity to `true` directly in the DB for the session unit **without** logging in again → step 5 must already route to CLARISA. If it does not, DD-2 is not live and the check fails.
- **Red run:** baseline observed at `0edf846105`: on the branch HEAD every unit stores locally, and no `partner_requests_to_clarisa` row exists. `partner_requests` row count quoted before step 3.
- **Disqualifier:**
  - A step 5 measured after a re-login (proves the session path, not DD-2).
  - A Save clicked before the page JS has initialised.
  - Evidence on a non-throwaway database.
- **Consumers:** n/a.
- **Review:** `checklist` — evidence review; the owner approves visuals.
- **Done when:**
  - All 9 steps evidenced.
  - P-10 recorded as settled or accepted risk.
  - P-6 confirmed live.
- **Skills:** `marlo-verify`, `playwright-cli`.

## 4. Dependency Graph

```
T01 (seed) ──► T02 (routing + tests) ──► T03 (templates) ──► T05 (live proof)
                     └────────────────► T04 (docs)
```

No cycles. T04 may run in parallel with T03.

## 5. Coverage (scenario and clause → task)

| Requirement / clause | Task(s) |
|---|---|
| FN-001 AC: absent row → MARLO; resolve at submit; BUT non-`true` ≠ CLARISA | T02 (D11 cases) |
| FN-002 / NF-002 AC: stored + alert; AND IT MUST NOT contact CLARISA | T02 (rewritten pin + behavioural case) |
| FN-003 / FN-004 AC: one request with all fields; AND IT MUST carry the unit's source; BUT no unit literal; BUT no row; AND no alert | T02 (D1, D2) |
| FN-005 / UI-003 / OPS-001 AC: failure + data; one action-logger line; BUT no fallback; AND no alert; AND IT MUST NOT log secrets | T02 (D3, D4); T03 (text); T05 step 1 |
| FN-006 (stored parameters decide) | T02 (D11); T05 steps 5 and 8 |
| FN-007 AC (office local) | T05 step 6; `RequestCountryOfficeAction` static check kept in T02 |
| FN-008 AC: row saved; next submit follows; menu at next login; BUT other units untouched | T05 steps 4–6 |
| FN-009 AC (pending stay decidable) | T05 step 7 |
| FN-010 AC: no unit → failure text; one log line; BUT not stored; AND not sent | T02 (D3); T03 (`saveFailed`) |
| UI-001 / SEC-001 AC: hidden in CLARISA; AND IT MUST show in MARLO; BUT guards unchanged | T03 (pins); T05 steps 3, 6, 9 |
| UI-002 AC: mailto text; BUT no trigger; AND MARLO trigger | T03 (pins); T05 steps 3, 6 |
| SEC-002 | T02 (D4) |
| MIG-001 AC: one row per type; AND no `custom_parameters` | T01 |
| NF-001 AC: no `isAiccra(`, no literal, both constants | T01 (parity); T02 (greps) |
| NF-003 | n/a — no `.properties` change (design P-14) |
| NF-004 | Common Done criteria, every code task |

## 6. Testing Plan

- **Unit:** T01 parity; T02 both-mode suite and the rewritten pin; T03 static template pins.
- **Integration / live:** T05 on a throwaway DB, both modes, Parameters screen, CLARISA live call or accepted risk.
- **Regression:** the full `marlo-web` suite with `-am`, including every `CHG-PARTREQ-001` test (`PartnerRequestReviewGuardTest`, `PartnerRequestManagerGuardTest`, `PartnerRequestEmailsTest`).
- **Accessibility:** n/a. No new markup beyond the restored link, which uses the existing `ptn-help` styles.

## 7. Operational Steps

- **Migration:** Flyway on startup; DML only; no rollout rows.
- **Switching a unit:** Superadmin → *Parameters* → *Specificities* → `partner_requests_to_clarisa`. The submit follows at once; the menu and link follow at the next login or unit switch.
- **Before switching a non-AICCRA unit or `AICCRA_III`:** the owner confirms with the CLARISA team that its lower-case acronym is an accepted `misAcronym` (P-9).
- **Per environment:** `clarisa.api.host/username/password` must be set wherever a unit is switched to CLARISA (R-2).
- **Release note items** (written later with `marlo-release-notes`):
  - the switch;
  - the login lag of the menu and link;
  - leftover pending requests (R6);
  - the `AICCRA_III` caveat;
  - the CLARISA config requirement.

## 8. Rollback Plan

- **Specificity:** set the unit's value to `false`; the next submit is local again.
- **Code:** revert the spec's commits; the `parameters` rows are inert without the code.
- **Data:** nothing to undo. CLARISA mode stores nothing in MARLO.

## 9. PR Strategy

One PR. The ~470 LOC are about 270 of tests, 130 of Java in one class, plus SQL, FTL and docs, and they are tightly
coupled: T02's tests pin T03's getter. Review order: T02 (routing) → T01 (seed) → T03 (templates) → T05 evidence.
It ships on the revive branch, so its PR merges with or after the revive work.

## 10. Definition of Done

- [ ] All acceptance criteria verified (T02 suite + T05 evidence).
- [ ] Compile gate, Checkstyle delta 0, and the full suite with `-am` all green.
- [ ] P-10 settled or recorded as accepted risk; P-6 confirmed live; P-9 handed to the owner.
- [ ] `docs/specs/domain/admin/agent-context.md` updated (T04).
- [ ] Each task marked done with verification notes in `execution.md`.
- [ ] Merged to `staging` with or after the revive work.
