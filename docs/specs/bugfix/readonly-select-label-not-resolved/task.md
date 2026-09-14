# Read-Only Selects Never Resolve Their Label — Tasks

**Spec ID:** BUG-FORMS-READONLY-SELECT-001
**Status:** In Progress
**Owner:** IBD Team — Kenji Tanaka
**Reviewers:** PMU lead, QA lead, Tech lead
**Last Updated:** 2026-09-14

---

## 1. Execution Context

- Java 17, run script `scripts/run-marlo-java17.sh`. Java level verified in `marlo-parent/pom.xml`.
- Pinned versions this analysis was carried out against: Struts `6.8.0`, OGNL `3.3.4`, FreeMarker `2.3.32`
  (`marlo-parent/pom.xml` lines 23, 738, 742).
- The change touches one `.ftl` file. **No repository gate applies:** there is no Java to compile, Checkstyle covers
  Java only, and the cache-busting rule covers `.css` / `.js`.
- Verification was therefore done with a purpose-built harness (see §5) plus a browser pass (T04).

## 2. Pre-flight Checklist

- [x] `forms.ftl:271-272` confirmed as the existing precedent for the idiom.
- [x] Confirmed MARLO runs FreeMarker with HTML output format — `?markup_string` at `forms.ftl:295` throws on a plain
      string, so the template could not parse otherwise.
- [x] Confirmed `BeansWrapper.unwrap()` on a markup model returns its `toString()` rather than throwing.
- [x] Confirmed Struts resolves map keys across `Long`, `Integer` and `String` declared key types, so no action-side
      type change is needed.
- [x] Confirmed the hidden input at `forms.ftl:287` is emitted before the changed lines, so the POST payload is
      unchanged.

## 3. Task List

### BUG-FORMS-READONLY-SELECT-001-T01 — Normalize the key in the map branch

**Status:** Done
**Files:** `marlo-web/src/main/webapp/WEB-INF/global/macros/forms.ftl`

Insert a normalization step before the lookup expression is built, and use it in both halves of the `stringKey`
conditional so quoted and unquoted keys both benefit.

```ftl
[#assign cleanKey = (key?is_markup_output)?then(key?markup_string, key)]
[#assign customValue][#if !stringKey][@s.property value="${listName}[${cleanKey}]"/][#else][@s.property value="${listName}['${cleanKey}']"/][/#if][/#assign]
```

**Verification:** harness render, map branch — `id=42` resolves to `# of`; `id=-1` and a null association both render
the `fieldEmpty` text; a key absent from the list still prints the key.

---

### BUG-FORMS-READONLY-SELECT-001-T02 — Make the sentinel check and the value print mutually exclusive

**Status:** Done
**Files:** `marlo-web/src/main/webapp/WEB-INF/global/macros/forms.ftl`
**Depends on:** T01

T01 makes `customValue` resolve, which turns the two consecutive `#if` blocks into a double print whenever the list
contains a `-1` entry. Merge them into one chain:

```ftl
[#if (key?markup_string == "-1") || (customValue?markup_string == "-1")]
  ${requiredText}   [@s.text name="form.values.fieldEmpty" /]
[#elseif customValue?has_content]
  ${customValue}
[#else]
  ... existing block, unchanged ...
[/#if]
```

**Verification:** reproduced `Not provided Not Applicable` before the change; after it, a list containing `-1`
renders the `fieldEmpty` text once. All five paths through the chain walked against the previous behaviour.

---

### BUG-FORMS-READONLY-SELECT-001-T03 — Normalize the bound name in the entity branch

**Status:** Deferred — implemented and verified during this spec, then deliberately withdrawn
**Files:** `marlo-web/src/main/webapp/WEB-INF/global/macros/forms.ftl`

`customName` is built by a captured block at `forms.ftl:312` / `:314`, so it carries the same defect. The patch is
two lines and is recorded here verbatim so the follow-up does not start from scratch:

```ftl
[#assign cleanName = (customName?is_markup_output)?then(customName?markup_string, customName)]
[#assign customValue][@s.property value="${cleanName}.${displayFieldName}"/][/#assign]
```

**Verified while applied:** harness render, entity branch — the bound entity's display property resolved
(`# of`, `Hectares`) where the branch had always rendered the `fieldEmpty` text.

**Why it is not shipped.** It moves the blast radius from 40 call sites to 221, and 74 of the added ones render once
per row, so each initializes a Hibernate proxy per row on read-only list screens. Nothing about the reported bug
needs it.

**Entry condition for picking it up.** Measure the query count on a read-only Project Partners screen with a
realistic row count, with and without the patch. If the delta is acceptable, ship it with its own QA pass over the
entity-branch screens. If it is not, the remedy is an eager fetch or a projection on those screens first — not a
narrower macro.

---

### BUG-FORMS-READONLY-SELECT-001-T04 — Runtime verification and performance measurement

**Status:** Not started — **release gate**

Two things the harness cannot establish, both requiring the running application.

**Correctness.** Open each of these read-only and confirm the label renders:

| Screen | Field | Expected |
|---|---|---|
| Impact Pathway → Outcomes | Target Unit, on a milestone with year < the active phase year | The unit name; `Not provided` when the stored id is `-1` |
| Project Partners | Partner person type (`contactType`) | `Contact Point` for `CP` — this is the `stringKey=true` path |
| Funding Sources | Budget type | The budget type label |
| Annual Report | Status fields | The status label |
| Deliverables | Status | The status label |

All five are map-branch fields. Entity-branch fields on the same screens keep rendering the `fieldEmpty` text; that
is the unchanged baseline, not a regression.

For AICCRA there are 82 milestones in phase 431 whose year precedes 2026, so the Impact Pathway case is reachable
without setting anything up.

**Performance.** With SQL logging on, load a read-only innovation or policy screen and confirm the seven
method-call `listName` expressions (§ `design.md` 12) do not make the page noticeably slower. These are the only
expressions this change newly evaluates. The entity branch, which carried the per-row risk, is not part of this
change.

---

### BUG-FORMS-READONLY-SELECT-001-T05 — Update the frontend composition runbook

**Status:** Not started
**Files:** `reports/ai-context/frontend-composition-map.md`

Record the rule this bug came down to, because it is not discoverable from the macro and it has now produced three
separate workarounds: **a captured FreeMarker block is markup output; printing it is safe, but feeding it back into a
Struts tag attribute is not.** Note the `(x?is_markup_output)?then(x?markup_string, x)` idiom as the remedy.

---

### BUG-FORMS-READONLY-SELECT-001-T06 — Retire the workarounds once the entity branch is fixed

**Status:** Blocked on T03 — follow-up, not a release gate

`forcedValue` at `outcomes.ftl:476` and the hand-written `[#list]` at `projectDescription.ftl:206-217` both exist to
work around this defect. **They are still load-bearing after this change**, because both sit on the entity branch,
which T03 leaves unfixed — removing either now would blank the field. Retire them only after T03 ships.

Until then they are worth a comment pointing at this spec, so the next reader does not copy them as a pattern.

## 4. Dependency Graph

```
T01 ──> T02 ──> T04 ──> T05
                  │
                  └──> T03 (deferred) ──> T06
```

T02 depends on T01 — without it there is no double print to prevent. T04 gates release. T03 is out of this change;
T06 depends on it, because `forcedValue` and the manual bypass stay load-bearing while the entity branch is unfixed.

## 5. Testing Plan

MARLO has no meaningful automated test coverage (3 JUnit files, no Surefire configuration), and none of it reaches
FreeMarker rendering, so a green test run would prove nothing here. Verification was done instead with a harness that
composes the real libraries:

- A real Struts `OgnlValueStack`, built through `ConfigurationManager` + `StrutsDefaultConfigurationProvider`.
- Real FreeMarker 2.3.32 with `HTMLOutputFormat` and auto-escaping, matching production.
- The actual `forms.ftl` imported from disk, unmodified.
- `<s:property>` stubbed as `stack.findString(value)`, with attribute conversion modelled on the bytecode of
  `TagModel.unwrapParameters()`.

The stub is the harness's one weakness and is why T04 exists.

Matrix exercised, for both a list containing `-1` and one without:

| Stored value | Map branch (shipped) | Entity branch (unchanged) |
|---|---|---|
| Key present in the list | Label | `fieldEmpty` text — baseline |
| Key absent from the list | Key (fallback, unchanged) | `fieldEmpty` text — baseline |
| `-1` | `fieldEmpty` text, once | `fieldEmpty` text — baseline |
| Null association | `fieldEmpty` text | `fieldEmpty` text — baseline |

Additionally verified: `ContainUtil.contains` is type-insensitive, so the editable branch's selected-option detection
is unaffected; and a getter throwing `LazyInitializationException` is swallowed by `findString`, so a detached
association degrades to `fieldEmpty` rather than an error page.

## 6. Operational Steps

None. No migration, no configuration change, no cache to clear beyond the normal deploy. FreeMarker templates are
read from the WAR, so a redeploy is sufficient.

## 7. Rollback Plan

`git revert` of the single commit. No data is written by this change, so there is nothing to unwind. Reverting
restores the previous rendering exactly.

## 8. Definition of Done

- [x] T01 and T02 implemented in `forms.ftl` (`+5 / -3`).
- [x] T03 withdrawn, with its verified patch and its entry condition recorded above.
- [x] Harness matrix green for the map branch, with and without a `-1` entry in the list.
- [x] Entity branch confirmed unchanged from its current baseline.
- [x] Editable rendering confirmed unaffected.
- [x] POST payload confirmed unchanged.
- [ ] T04 browser pass across the five map-branch screens listed.
- [ ] T04 timing check on a read-only innovation or policy screen.
- [ ] T05 runbook updated.
- [ ] Reviewed by a second pair of eyes.
