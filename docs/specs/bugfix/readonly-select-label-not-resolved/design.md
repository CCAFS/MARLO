# Read-Only Selects Never Resolve Their Label — Design

**Spec ID:** BUG-FORMS-READONLY-SELECT-001
**Status:** Draft
**Owner:** IBD Team — Kenji Tanaka
**Reviewers:** PMU lead, QA lead, Tech lead
**Last Updated:** 2026-09-14

---

## 1. Architecture Summary

The fix normalizes markup output back to a plain string where the map branch of `customForm.select` feeds a captured
value into a Struts tag attribute, and merges two consecutive `#if` blocks into one chain so that the sentinel check
and the value print become mutually exclusive. The entity branch has the same defect at `forms.ftl:316` and is
deliberately left alone — see §12 and `task.md` T03.

The failure chain, each link verified against the pinned dependency versions:

| Step | Where | What happens |
|---|---|---|
| 1 | `forms.ftl:292` | `[#assign key][@s.property .../][/#assign]` captures under HTML auto-escaping, producing a `TemplateHTMLOutputModel` |
| 2 | `forms.ftl:294` (map) and `:316` (entity) | The capture is interpolated into `value="..."`, so the whole attribute is markup output |
| 3 | `TagModel.unwrapParameters()` | Calls `BeansWrapper.unwrap()`, which returns `markupOutput(format=HTML, markup=targetUnitList[42])` |
| 4 | `<s:property>` | Evaluates that string as OGNL, fails, returns `null` |
| 5 | `forms.ftl:305` | `customValue` is empty, so the macro prints `${key}` — the raw id |

Step 3 was confirmed directly: `BeansWrapper.unwrap()` on a `TemplateHTMLOutputModel` does not throw, it returns the
wrapper's `toString()`.

## 2. Module Footprint

| Module | Files |
|---|---|
| `marlo-web` | `src/main/webapp/WEB-INF/global/macros/forms.ftl` |

No Java, no SQL, no JavaScript, no CSS. One file, `+5 / -3`.

## 3. Data Model Changes

None. Stored values are already correct; only their rendering was wrong.

## 4. API / Action Surface

None. No action, interceptor, validator or REST endpoint is touched. The hidden input that carries the value back on
submit is emitted at `forms.ftl:287`, before the changed lines, and is unaffected — so the POST payload is identical.

## 5. Frontend Composition

`customForm.select` (`forms.ftl:184`) has two rendering modes and, inside read-only, two label-resolution branches:

```
editable = true   -> <s:select ...>                       (already normalizes markup, lines 271-272)
editable = false
   displayFieldName == ""  -> map branch    : listName[key]
   displayFieldName != ""  -> entity branch : boundEntity.displayFieldName
```

Census of the 221 `customForm.select` call sites under `WEB-INF`:

| Branch | Call sites |
|---|---|
| Entity (`keyFieldName` + `displayFieldName`) | 179 |
| Map (neither) | 40 |
| Map with `stringKey=true` | 2 of the 40 |

**This change fixes the map branch only.** The entity branch carries the identical defect and the identical
remedy, but is deferred — see `requirements.md` §4 and `task.md` T03.

## 6. Persistence & Phase Replication Plan

Not applicable — no persistence path is touched, and no phase-sensitive behaviour changes.

## 7. Validation & Save Pipeline

Not applicable. The change is confined to the `editable=false` rendering branch, which emits no validation hooks.
`Action.validate()`, the validators and the manager chains are untouched.

## 8. Permissions & Edit Gates

Unchanged. Which users see read-only is decided before the macro is reached — `BaseAction` editability for the
section, and `OutcomesAction.canEditMileStone()` for individual milestones. This change only alters what read-only
renders, never who gets it.

## 9. Specificity / Feature-Flag Strategy

None. The defect and the fix are uniform across Global Units, so no `parameters` / `custom_parameters` entry is
warranted. Note that the *visible text* still varies by program, because `form.values.fieldEmpty` is overridden per
program (`Not provided` in `custom/aicrra.properties:141`, `aiccra3.properties:140` and `pabra.properties:140`;
`Prefilled if available` in `global.properties:151`). That variation predates this spec.

## 10. Integration Points

None. No external service, no CLARISA call, no REST consumer.

## 11. Observability

The defect was silent: `BeansWrapper.unwrap()` succeeds, so nothing was logged. After the fix the expressions
evaluate normally and still log nothing on success. A failed lookup remains silent and degrades to the fallback, so
neither before nor after does this path produce a log signal. Verification therefore has to be visual — see
`task.md` §5.

## 12. Performance & Scalability

This is the material risk of the change, and it is a direct consequence of expressions that never ran now running.

**Map branch.** Seven call sites pass a method call as `listName`:

| Call site | Expression | Cost |
|---|---|---|
| `innovationTemplates.ftl:18,144` | `getInnovationsYears(${innovationID})` | `projectInnovationInfoManager.findAll()` over 20,708 rows, up to twice |
| `projectPolicy.ftl:109` | `getPoliciesYears(${policyID})` | `projectPolicyInfoManager.findAll()` over 6,176 rows, up to twice |
| `studiesTemplates.ftl:697,701,704` | `getExpectedStudiesYears(...)`, `getYears(...)` | Scoped finder plus stream filter |
| `deliverableInfo.ftl:103` | `project.projectInfo.getYears(...)` | In-memory |

These were never evaluated in read-only mode before. The editable path already pays this cost today, so read-only
becomes as expensive as editable rather than newly expensive in absolute terms. It buys nothing for these fields:
their keys are years, and the `${key}` fallback already printed the correct year.

**Entity branch — not shipped, and this is why.** 74 of its 179 call sites take a macro-composed `name` (for
example `${name}.institution.id` in `projectPartners.ftl:343`), so they render once per row. Fixing that branch would
dereference a Hibernate association per row to read the display property: on a read-only Project Partners screen with
N partners, up to N additional selects, deduplicated by the session cache where rows repeat. The display properties
are cheap — `Institution.getComposedName()` is string concatenation — so the cost is proxy initialization, not the
getter. That profile has to be measured before it ships, which is why it is deferred to T03 rather than carried here.

## 13. Security Considerations

`?markup_string` returns the **escaped** markup, so a value containing HTML or quotes stays escaped. Two
consequences:

- No unescaped value reaches the page; there is no new XSS surface.
- A key containing an apostrophe produces an invalid OGNL expression rather than an injected one. The lookup returns
  `null` and the field degrades to the existing fallback. Verified as the failure mode, not an exploit path.

A detached Hibernate association is swallowed by the value stack: `findString` on a getter that throws
`LazyInitializationException` returns `null`, so the worst case is the `fieldEmpty` text, not a 500. Verified
directly against Struts 6.8.0.

## 14. Backwards Compatibility & Rollout

The POST payload is byte-identical, so there is no compatibility concern for saves, autosave drafts or the audit
log. The change is visible only in read-only rendering, and is reversible by reverting one file.

Around 20 fields across ~14 files change from showing a raw key to showing their label — the 40 map-branch call
sites, minus the year-based lists, whose keys are already their own labels and which render byte-identically. Every
change moves a field from wrong to right. The remaining ~180 entity-branch fields are untouched and keep their
current rendering.

## 15. Decision Records

**DR-001 — Fix the macro, not the screens.** Three local workarounds already exist for this defect (`forcedValue`,
the `projectDescription.ftl` bypass, and the raw-key fallback that happens to be correct for year lists). A fourth
would have left roughly 200 fields wrong and made the macro harder to reason about.

**DR-002 — Reuse the existing idiom.** `(x?is_markup_output)?then(x?markup_string, x)` is already in this file at
lines 271-272. A bare `?markup_string` would work today but would throw if the output format were ever switched to
plain, which is exactly the kind of latent coupling that produced this bug.

**DR-003 — Merge the sentinel check into the chain.** Leaving the two `#if` blocks consecutive would have produced
`Not provided Not Applicable` for any list containing a `-1` entry. Reproduced before accepting the change. All five
paths through the chain were walked to confirm the pre-existing behaviour is preserved.

**DR-004 — Do not add a `-1` guard to the entity branch.** It has never had one. Adding it would change behaviour
that nobody reported, in the branch with the widest reach.

**DR-005 — Ship the map branch alone.** The entity-branch fix was implemented and verified in this spec, then
withdrawn. It is two lines and it works, but it takes the blast radius from 40 call sites to 221 and turns on
per-row Hibernate proxy initialization across read-only list screens. The reported bug is fixed without it. A
performance change of that shape earns its own measurement and its own review, rather than riding along with a
display fix.

## 16. Open Risks

| Risk | Severity | Mitigation |
|---|---|---|
| Seven expensive `listName` method calls now evaluated in read-only | Low | Editable already pays the same cost; confirm in T04 |
| ~20 fields change appearance | Low | Ship as its own change with the QA list in `task.md` §5 |
| N+1 proxy initialization if the entity branch is later fixed | Medium | Retired from this change; measurement is the entry condition for T03 |
| Verification is a harness reconstruction, not the running app | Medium | T04 closes this. The harness uses a real Struts `ValueStack` and real FreeMarker, but stubs `<s:property>` |
