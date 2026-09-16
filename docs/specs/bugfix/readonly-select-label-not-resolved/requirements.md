# Read-Only Selects Never Resolve Their Label — Requirements

**Spec ID:** BUG-FORMS-READONLY-SELECT-001
**Status:** Draft
**Owner:** IBD Team — Kenji Tanaka
**Reviewers:** PMU lead, QA lead, Tech lead
**Last Updated:** 2026-09-14
**Related PRD sections:** docs/prd.md — reporting and review of closed phases
**Related System Design sections:** docs/system-design/design.md — component inventory, form controls
**Related Detailed Design sections:** docs/detailed-design/detailed-design.md — frontend composition
**Companion ai-context docs:** reports/ai-context/frontend-composition-map.md

---

## 1. Overview

Every `customForm.select` in MARLO renders as a read-only `<p>` when `editable=false`. That read-only path has never
been able to translate the stored key into its human label. Depending on the branch it falls into, the field shows
the raw key (a numeric id, or a code such as `CP`) or the `form.values.fieldEmpty` text. The defect is in the shared
macro, not in any one screen, so it affects every closed phase, every user without edit permission, and every
milestone whose year precedes the active phase.

The trigger for this spec was a report that Target Unit in Impact Pathway showed a number instead of the unit name.
Diagnosis showed the field was not special: it is one of roughly 200 instances of the same macro-level defect.

## 2. Problem Statement

MARLO renders FreeMarker with the HTML output format and auto-escaping enabled. This is not optional —
`forms.ftl:295` calls `?markup_string`, which throws on a plain string, so the template only parses at all because
the output format is markup.

Under auto-escaping, a captured block such as `[#assign key][@s.property value="${name}"/][/#assign]` does not
produce a `String`. It produces a `TemplateHTMLOutputModel`. The read-only branch then interpolates that captured
value back into a Struts tag attribute:

```ftl
[@s.property value="${listName}[${key}]"/]          <!-- map branch -->
[@s.property value="${customName}.${displayFieldName}"/]   <!-- entity branch -->
```

Because the interpolated value is markup output, the whole attribute becomes markup output. When Struts receives it,
`TagModel.unwrapParameters()` calls `BeansWrapper.unwrap()`, which for a markup model returns its debug
representation rather than its text:

```
markupOutput(format=HTML, markup=targetUnitList[42])
```

`<s:property>` evaluates that as OGNL, fails, and yields `null`. `customValue` is therefore **always** empty in the
read-only branch, regardless of the list contents or the key type. The macro then falls through to `forms.ftl:305`
and prints the raw key, or to the `fieldEmpty` text when there is no key.

Two consequences explain long-standing oddities in the codebase:

- `forcedValue` is used in exactly one call site (`outcomes.ftl:476`). It is not a stylistic touch: it is the only
  mechanism that has ever made an entity-branch select show a value, because it is interpolated straight from the
  model and never round-trips through a tag.
- `projectDescription.ftl:206-217` hand-writes a `[#list]` loop to find a status label, bypassing the macro. That is
  a previous developer working around this same defect locally.

The correct fix already exists in the same file. `forms.ftl:271-272` normalizes markup output before use, for the
editable branch only:

```ftl
[#assign currentValue = (customValue?is_markup_output)?then(customValue?markup_string, customValue)]
```

The read-only branch was never given the same treatment.

## 3. In-Scope Requirements

### Functional

- **BUG-ROSEL-F-001** — A read-only `customForm.select` whose stored key exists in its list MUST display the list
  label for that key, not the key.
- **BUG-ROSEL-F-002** *(deferred — see Out-of-Scope)* — A read-only `customForm.select` that declares
  `displayFieldName` MUST display the named property of the bound entity.
- **BUG-ROSEL-F-003** — The sentinel key `-1` MUST continue to display the `form.values.fieldEmpty` text in the map
  branch, and MUST NOT additionally display a resolved label.
- **BUG-ROSEL-F-004** — An absent or null key MUST continue to display the `form.values.fieldEmpty` text.
- **BUG-ROSEL-F-005** — A key that is present but absent from the list MUST keep the current fallback behaviour
  (map branch prints the key; entity branch resolves from the entity).
- **BUG-ROSEL-F-006** — `stringKey=true` MUST keep quoting the key, so non-numeric keys such as `CP` resolve.

### Non-Functional

- **BUG-ROSEL-N-001** — No change to the editable rendering path of `customForm.select`.
- **BUG-ROSEL-N-002** — A failure to resolve a label MUST degrade to the current behaviour, never to an exception or
  an error page.

### Data

- **BUG-ROSEL-D-001** — No schema change and no data migration. The defect is presentation-only; stored values are
  already correct.

### UI

- **BUG-ROSEL-UI-001** — No layout, class, or markup-structure change beyond the text now rendered inside the
  existing `<p>`.
- **BUG-ROSEL-UI-002** — All user-facing text continues to come from i18n keys (`form.values.fieldEmpty`,
  `form.values.required`). No literal strings are introduced.

### Security

- **BUG-ROSEL-SEC-001** — The resolved label MUST remain HTML-escaped. No value may reach the page unescaped as a
  result of this change.

## 4. Out-of-Scope

- **The entity branch (`displayFieldName != ""`), covering 179 of the 221 call sites.** It carries the identical
  defect and the identical two-line remedy, and it was implemented and verified during this spec before being
  deliberately withdrawn. It is deferred because 74 of those call sites render once per row, so fixing them
  initializes a Hibernate proxy per row on read-only list screens — a performance profile that has to be measured
  before it ships, and that has nothing to do with the reported bug. Tracked as T03 in `task.md`, with the verified
  patch recorded there so the follow-up starts from evidence rather than from scratch.
- Removing the `forcedValue` at `outcomes.ftl:476`. It remains load-bearing while the entity branch is unfixed.
- Removing the manual bypass at `projectDescription.ftl:206-217`. Same reason.
- Adding a `-1` sentinel guard to the entity branch, which has never had one.
- Any change to `OutcomesAction` or `outcomes.ftl`. Target Unit is fixed by the macro alone.

## 5. Personas Affected

| Persona | Effect |
|---|---|
| Cluster Coordinator | Reads closed phases constantly. Sees labels instead of ids in every section with a select |
| QA Reviewer | Reviews submitted sections in read-only mode. Today cannot tell which option was chosen without opening the DB |
| PMU | Same as QA reviewer, across programs |
| Admin | Read-only admin screens (Target Units, Deliverables) gain their labels |

## 6. Acceptance Criteria

**AC for F-001** — Given an outcome in a read-only phase whose `target_unit_id` is `42`, and given `42` is active in
`crp_target_units` for the logged Global Unit, When the Impact Pathway Outcomes page renders, Then the Target Unit
field shows `# of`.

**AC for F-002** — Deferred with the requirement. Entity-branch fields keep rendering the `form.values.fieldEmpty`
text in read-only mode; that is the unchanged baseline, not a regression introduced here.

**AC for F-003** — Given an outcome whose `target_unit_id` is `-1`, When the page renders read-only, Then the Target
Unit field shows exactly the `form.values.fieldEmpty` text once, with no label appended. Verified for both a list
that contains a `-1` entry and a list that does not.

**AC for F-004** — Given an outcome whose `target_unit_id` is `NULL`, When the page renders read-only, Then the field
shows the `form.values.fieldEmpty` text.

**AC for F-006** — Given a project partner person whose `contactType` is `CP`, When Project Partners renders
read-only, Then the field shows `Contact Point`.

**AC for N-002** — Given an entity association that cannot be initialized, When the field renders, Then the field
shows the `form.values.fieldEmpty` text and the page renders normally.

## 7. Constitutional Compliance Checklist

| Rule | Status |
|---|---|
| Phased data is forward-only | Not applicable — no write path touched |
| Save pipeline pattern | Not applicable — no validator, action or manager touched |
| Spring MVC owns `/api/*` | Not applicable |
| Specificities via `parameters` + `custom_parameters` | Not applicable — behaviour is uniform across Global Units |
| Schema changes ship as Flyway migrations | Not applicable — no schema change |
| GPL header on new Java files | Not applicable — no new Java file |
| Code style / Checkstyle | Not applicable to `.ftl`; Checkstyle covers Java only |
| English only in code and comments | Honored |
| Branching from `staging` | Honored |
| Run scripts / Java 17 | Unchanged |
| Dependency baseline | Unchanged |
| No credential files committed | Honored |

## 8. Open Questions

1. Should the entity branch gain a `-1` sentinel guard for symmetry with the map branch? It has never had one, so
   adding it changes behaviour nobody has asked for. Deferred.
2. Should `forcedValue` be retired now that it is redundant? Leaving it is harmless — it acts as a fallback — but it
   is now a misleading pattern for anyone reading `outcomes.ftl:476` as an example to copy.
3. Does the added lazy-association traversal on read-only list screens need an eager-fetch or a projection? Depends
   on the measurement in `task.md` T04.

## 9. Decision Log

- 2026-09-14 — Fix in `forms.ftl` rather than per screen — the defect is in the shared macro; fixing it per screen
  would be the third local workaround (after `forcedValue` and the `projectDescription.ftl` bypass) and would leave
  roughly 200 fields wrong.
- 2026-09-14 — Reuse the `(x?is_markup_output)?then(x?markup_string, x)` idiom from `forms.ftl:271-272` rather than a
  bare `?markup_string` — it already exists in this file, and the guard keeps the template working if the output
  format is ever changed to plain.
- 2026-09-14 — Restructure the two consecutive `#if` blocks at `forms.ftl:295-297` into one `#if/#elseif` chain — once
  `customValue` resolves, the standalone `-1` check and the value print both fire and the field renders
  `Not provided Not Applicable`. Reproduced before the change was accepted.
- 2026-09-14 — Ship the map branch only, deferring the entity branch — the entity fix was written and verified, but
  it widens the blast radius from 40 call sites to 221 and introduces per-row Hibernate proxy initialization on
  read-only list screens. The reported bug does not need it, and a performance risk of that shape should be measured
  on its own rather than carried by a display fix.
- 2026-09-14 — An earlier hypothesis, that `HashMap<Long, String>` keys could not be resolved by OGNL, was tested
  against a real Struts `ValueStack` and disproved: `XWorkMapPropertyAccessor` converts the key from the declared
  generic type, and `Long`, `Integer` and `String` keyed maps all resolve. The change based on it was reverted.
