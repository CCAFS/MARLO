# Validate design gate - Impeccable critique and audit

Spec `changes/revive-local-partner-request-approval`, HEAD `2380642ca0`, 2026-11-20. Both screens are page mode **Operate**, ENV-2 (spec `design.md` §5.1). They are judged against the shipped `--marlo-*` tokens in `marlo-redesign.css`. `docs/ux-ui/design.md` §7 is stale (DD-13, follow-up 3).

## Provenance

| Item | Value |
|---|---|
| Skill source | `vendored (4.8.0)`. Every file listed in `UPSTREAM.md` matches its hash, `SKILL.md` included with the `metadata:` block stripped. One file is not listed: the engine binary `scripts/bin/darwin-arm64/impeccable` (environment finding, not a design finding) |
| Engine | `ready (location 2, engine 0.1.18)`, which equals `scripts/VERSION`. Nothing was downloaded. Every call ran with `DO_NOT_TRACK=1 IMPECCABLE_NO_UPDATE_CHECK=1` |
| Assessment A (design review) | An isolated sub-agent. It worked only from the screenshots and source files and never saw any detector output |
| Assessment B (detector + browser) | Run in the parent session: `validate-detector.out` and `validate-first-visit.out` |
| Score source | Two isolated assessments, so this is not a degraded run |
| Suppressed steps | No snapshot was written to `.impeccable/critique/`, because the evidence lives in this folder. No `live-server` and no overlay inject: the detector ran inside the page through `--cdp`. No context loader was run. `Questions skipped: validation report is the deliverable (/akili-validate)` |
| Waivers | `docs/ux-ui/design.md` has no *Design Waivers* section, so there are 0 waivers |

## Scores (Assessment A)

| Screen | Specificity | Nielsen (0-4 x 10) | Cognitive load |
|---|---|---|---|
| Review (`marloInstitutions.do`) | Pass: built for this job (diff of the reviewer's changes, existing-office chips, source/unit/date) | 26/40. Weakest at 2: consistency, recognition over recall, flexibility, help | 2 failures: repeated per-country detail blocks; the rejection reason must be looked up in one dialog and retyped in another |
| Request page (`partnerSave.do`) | Generic but appropriate: a short form | 28/40. Weakest at 2: user control, error prevention | 1 failure: the page gives no way to check whether the institution already exists |

**Strengths.**
- Every colour, radius and shadow comes from a `--marlo-*` token.
- The dialogs trap focus, close on Escape and return focus to the button that opened them; outcome notes are announced.
- The diff lists only the fields that changed.
- Field errors appear under each field, and focus moves to the first invalid one.
- The success state is clear.

## Findings

Every finding is listed. `/akili-validate` has no `false positive`, so each one must be fixed or waived.

**Known** means the finding is already recorded in `execution.md` `## Follow-ups` (F-n) or in the T11b findings in this folder.

### Detector (Assessment B)

| ID | Screen | Severity | Finding | Scope | Known? |
|---|---|---|---|---|---|
| D1 | Request page, both widths | P2 | `nested-cards` (primary): the info note `div.prq-note.prq-note--info` sits inside the form card (`partnersSave.ftl`) | Changed code | New |
| D2 | Review, both widths | P2 | `low-contrast` x10 (primary). Instances: <br>• `span.label.label-danger` ("Testing Environment"), 4.0:1<br>• breadcrumb `li.active`, 4.1:1<br>• online-user badge, 1.7:1<br>• footer `infoLinks strong` x3, 3.4:1<br>• footer `copyRight` spans, 3.9:1 and 2.7:1<br>• debug-panel tabs, 2.8:1 and 2.6:1 | Shared chrome / test-only debug panel. None inside `.prq` | New (outside the spec) |
| D3 | Review, both widths | P3 | `undersized-ui-text` x8-9 and `tiny-text` x2-3 (primary): debug-panel text at 9.975px; at 390 also `#usersOnline` at 9px | Test-only debug panel / shared chrome | New (outside the spec) |
| D4 | Both screens | P3 (advisory) | `low-contrast`: cookie banner "Got it!" 4.3:1, breadcrumb link 4.2:1, header `a > b` 4.3:1 | Shared chrome | New (outside the spec) |
| D5 | Review, 390 | P3 (advisory) | `straggler` on `#prqSummary`; `unbalanced-heading` on the edited card's `h3` | Changed code | New |

### First visit (Assessment B)

| ID | Screen | Severity | Finding | Known? |
|---|---|---|---|---|
| FV1 | Request page | **FAIL** (no waiver) | At 375x667 the "Request to add new institution" button is covered by the cookie banner (`div.cc-window`) | F-11 (site-wide banner) |
| FV2 | Review | **FAIL** (no waiver) | At 390x844 and 375x667 the first Accept is below the fold (y=1293), because the shared admin side menu stacks above the content | New as a first-visit outcome. Relates to A-I6 |

### Critique and audit (Assessment A)

| ID | Screen | Severity | Category | Finding | Known? |
|---|---|---|---|---|---|
| A-I1 | Review | P1 (as reported) | Audit, integrity | `institutionRequestMacro.ftl:174` renders the requester's free-text website straight into `href`. `partnersSave.ftl:95` is free text, and `PartnersSaveAction.validate()` does not check the scheme, so a `javascript:` value becomes a live link for the superadmin. Pre-existing since `b4f2c94f6d`; the redesign kept it. Not verified live: `target="_blank"` may limit the impact | New |
| A-I2 | Request page | P2 | Error prevention | No check for existing or similar institutions before submitting; duplicates reach the reviewer | New |
| A-I3 | Review | P2 | Recognition | "Common reasons" in the Reject dialog are read-only text; the reviewer retypes them | New |
| A-I4 | Review | P2 | Copy | The accept outcome says only "The request was processed.", without naming what was added or that the requester was notified | New |
| A-I5 | Review | P2 | Consistency | The source reads "Cluster C102076 - ..." on partner cards but "Project: (102076) - ..." on office rows | T11b fixes2 finding (`RequestCountryOfficeAction:116`), T12 candidate. Not in `## Follow-ups` |
| A-I6 | Review | P2 | Responsive | At 390 the admin menu comes before the content, so the first request is about 1000px down (shared layout) | New. Same cause as FV2 |
| A-I7 | Request page | P2 | User control | No Cancel on the form. "Close" uses `window.close()`, which does nothing if the page was not opened as a popup | New |
| A-I8 | Review | P2 | Efficiency | No select-all for office countries; no filter, sort or bulk action for partner requests | New |
| A-I9 | Both | P3 | Consistency | Label drift between the two screens: Website / Web page, Headquarters country / Country, "Field required." / "This field is required.", dialog titles with and without "?" | New |
| A-I10 | Request page | P3 | Copy | Three phrasings of one action: the title "Request creation of new institution", the `<title>` "Request a new institution" and the button "Request to add new institution"; also "fill-in" | New |
| A-I11 | Request page | P3 | Hierarchy | In the error state the amber summary sits above the blue help note while field errors are red: two error colours | New |
| A-I12 | Review | P3 | Hierarchy | A "Pending" badge on every card of an all-pending queue | New |
| A-I13 | Review | P3 | Hierarchy | Office rows repeat identical Requester / Source / Unit / Date blocks | New |
| A-I14 | Review | P3 | Hierarchy | The Reject dialog has a brand-blue header and a red confirm button | New |
| A-I15 | Review | P3 | Hierarchy | The diff repeats "Original" / "Edited" on every row; "Changes made by the reviewer" gives no editor or date | New |
| A-I16 | Review | P3 | Responsive | At 390 the badges wrap below the meta line, away from the title | New |
| A-I17 | Both | P3 | Responsive | Controls are 36px tall: they pass the 24px minimum but fall short of 44px for touch | New |
| A-I18 | Both | P3 | Tokens | The `.prq` button, field and note rules are duplicated in both CSS files; font sizes such as 13.5px are hardcoded (no type-scale token exists) | New (related to F-3, DD-13) |
| A-I19 | Review | P3 | Copy | Three different counts: "1 institution with office requests", section count 1, card badge "2 requested" | New |
| A-I20 | Review | P3 | Integrity | The macros receive `canEdit` but ignore it | New |
| A-I21 | Both | P3 | Shared chrome | The debug panel overlaps the header (test environment only). The cookie banner takes about 30% of the 375px popup fold | Banner: F-11 |
| A-x1 | Review | P3 | a11y | The review page has no `<main>` landmark (the request page has one). Found by a parent DOM check | New |

## Reading for the gate

| Check | Result |
|---|---|
| Detector | Exit `2` on all 8 URL passes, with no waivers. The source scan returned exit `0` |
| First visit | FAIL at request 375x667 (covered) and review 390x844 / 375x667 (below fold) |
| Unevaluated captures | None, so the gate is not held by an unverified first visit |
| Detector finding inside the changed markup | 1 primary: D1 `nested-cards` |
| Detector findings in shared chrome or the test-only debug panel | Everything else |
