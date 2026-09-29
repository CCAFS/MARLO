# Retire The COVID-19 Impact Section — Judgment Day

| Field | Value |
|---|---|
| **Target** | `design.md` (against `requirements.md` and `proposal.md`), draft of 2026-09-29, repository at `12e063e323` |
| **Moment** | `/akili-specify` Step 2.5 — *Review Design* |
| **Judges** | Two blind, read-only judges on `sonnet` (the design was authored on `opus`) |
| **Round** | 1 |
| **State** | **approved** — round 1 fixed and re-judged, 2026-09-29 |

## Verdicts

| Judge | Verdict | Premise rows |
|---|---|---|
| A | FAIL | confirmed P-1–P-9, P-11, P-12; **refuted P-10** |
| B | PASS | confirmed P-1–P-9, P-11; not re-run P-10, P-12 |

## Frozen Ledger

| ID | Severity | Status | Finding | Evidence |
|---|---|---|---|---|
| JD-1 | **severe** | **Confirmed by architect re-run** (judge A severe, judge B the same fact as a warning) | P-10 is false. `default-action-ref` resolves only inside the requested namespace, and `login` is declared in package `home`, namespace `/`. A request to a retired `/projects` route ends in a `ConfigurationException`, answered with **404**, not the login. FN-003 ("never a 500") still holds; §5, the reversion challenge and P-10 state the wrong mechanism, and T05 would test for the wrong result | Re-run by the architect: `struts2-core-6.8.0-sources.jar` `DefaultConfiguration.java:604-625` (`actions = namespaceActionConfigs.get(namespace)`, then `actions.get(defaultActionRef)`); `Dispatcher.java:746-748` (`catch (ConfigurationException e)` → `SC_NOT_FOUND`); `struts-home.xml:25,79`; every action in `struts-projects.xml` is `{crp}/<literal>` except `reportingSummary`; no `unknown-handler` in `struts*.xml` |
| JD-2 | warning | **Confirmed by both judges** | `docs/ux-ui/design.md:161` lists `projects/projectImpacts.ftl` in its Screen Inventory. It is outside §3 and outside the out-of-scope list, so the NF-001 closing sweep would fail; it also contradicts the requirements header "Related UX/UI Design sections: not applicable". `docs/ux-ui/design.md` is a **constitutional document** | `grep -n "projectImpacts" docs/ux-ui/design.md` → `161` |
| JD-3 | warning | Judge A only (suspect) | The Budget says "2,103 in the 18 whole files", but §3 deletes 19 whole files. The 19th is `ImpactCovid19.prpt`, a binary Pentaho zip, so a line count means nothing for it | `wc -l` on the 18 text files → 2,103 (architect's run); §3 lists 19 files |
| JD-4 | warning | Judge A only (suspect) | §2 says "five constants"; §3 removes seven (3 in marlo-web `APConstants`, 2 in marlo-data `APConstants`, 2 in `Permission`) | `APConstants.java` marlo-web `:51,127-128`, marlo-data `:122-123`; `Permission.java:170-171` |
| JD-5 | suggestion | Judge A only | `SectionStatus.toString()` also concatenates `projectImpact` (`SectionStatus.java:271`); §3 names only the field, getter and setter. The clean recompile would catch it | `grep -n "projectImpact" SectionStatus.java` → 67, 127, 219-220, 271 |
| JD-6 | suggestion | Both judges | DD-1's "38 files" is unsourced; both judges count about 40–43 | Judges' own tallies |

## Premise Rows Strengthened By The Review

| Row | Change |
|---|---|
| P-2 | Holds in all four local databases, not only `aiccradb2`/`aiccradb3` (both judges) |
| P-12 | Primary source found: `flyway-core-4.0.1-sources.jar` `SqlMigrationExecutor.executeInTransaction()` returns `true` (re-run by the architect). Moves from `UNVERIFIED` to verified |

## Correction Work Units (applied — user chose *Fix and Re-judge*; CW-2 resolved as a task deliverable)

| Unit | Covers | Scope |
|---|---|---|
| CW-1 | JD-1 | Rewrite P-10 as refuted-and-corrected (404 via `ConfigurationException`), fix §5 and the reversion challenge, and set T05's expected result to 404 |
| CW-2 | JD-2 | User decision 2026-09-29: the `docs/ux-ui/design.md:161` line is removed as this spec's own deliverable (`design.md` §3 *Documentation*) |
| CW-3 | JD-3, JD-4, JD-5, JD-6, P-2, P-12 | Wording and count corrections; no decision changes |

## Scoped Re-Judgment (round 1)

| Judge | JD-1 … JD-6, P-2, P-12 | Fix-caused defects | Verdict |
|---|---|---|---|
| A | all resolved | none | PASS |
| B | all resolved | none | PASS |

Both judges re-ran every changed citation at the source (the Struts 6.8.0 and Flyway 4.0.1 source jars, all four
local databases, `wc -l` on the 18 text files) and found no stale value in the backward check.

## Terminal Receipt

| Field | Value |
|---|---|
| Rounds used | 1 of 2 |
| Confirmed severe | 1 (JD-1), fixed |
| Suspect | 0 remaining |
| Contradictions | 0 — JD-1's severity split was settled by the architect's single re-run |
| INFO | JD-3 … JD-6 fixed as wording |

**JUDGMENT: APPROVED ✅**
