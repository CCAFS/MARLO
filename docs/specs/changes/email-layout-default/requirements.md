# Branded email shell by default — Requirements

**Spec ID:** CHG-EMAIL-LAYOUT-001
**Status:** Approved 2026-11-11
**Owner:** Kenji Tanaka (IBD)
**Reviewers:** IBD tech lead; AICCRA PMU (copy in Appendix A)
**Last Updated:** 2026-11-11
**Type:** Change (embeds one defect: the hardcoded summary link, FN-007)
**Deliverable:** software
**Approval Mode:** gated
**Depth:** Standard
**Source:** `proposal.md` (approved 2026-11-11)
**Related PRD sections:** docs/prd.md — notifications to project leaders and PMU (submission workflow)
**Related UX/UI Design sections:** none — email is outside the screen inventory; the shell was approved in A2-2484 (`944b0f244a`)
**Related TRD sections:** none changed
**Companion ai-context docs:** none apply (no routing, validation, replication or composition contract changes)

> **Answer first.** Every Global Unit sends its user-facing notifications in the branded MARLO shell unless it
> opts out; nothing inside the shell repeats what its footer already says; the project submit and unsubmit emails
> join the shell with new copy and a summary link that points at the submitted project.

---

## 1. Overview

The branded shell (`email.layout`) reaches only AICCRA, AICCRA_III and TEST today, because a Global Unit receives
it only by overriding the key in its own `custom/*.properties` file — and a Global Unit created from the superadmin
screen never has one. This spec makes the shell the platform default, cleans the footer keys used inside it, moves
the project submit and unsubmit notifications into it, and fixes their summary link.

## 2. Problem Statement

Current behavior, each claim with its evidence (re-run on `staging` `460ecbd5be`):

| # | Today | Evidence |
|---|---|---|
| C1 | `email.layout` resolves to `{0}` (no shell) unless the unit's custom file overrides it | `global.properties:3894`; `EmailLayout.wrap` returns the body when the key yields `{0}` |
| C2 | Three files override it with one identical shell; the shell text is generic MARLO | md5 `6b52db6b…` for `aicrra`, `aiccra3`, `test`; shell text has no "AICCRA" |
| C3 | A unit created from the superadmin screen has `crp_custom_file` = lowercase acronym and no such file | `GlobalUnitCreateAction.java:68` (`ENABLE_CUSTOM_PROPERTIES_FILE_CREATION = false`); local DB units 49 `kt`, 50 `qatest`; no files in `custom/` |
| C4 | A missing custom file falls back to `global.properties` without error, in sessions and in the OTP path | `InternationalitazionFileInterceptor.intercept`; `UnitTextProvider.java:76-81` catches `MissingResourceException` |
| C5 | Submit and unsubmit emails are sent unwrapped, with the legacy footer `email.support.noCrpAdmins` / `email.getStarted` / `email.bye` | `ProjectSubmissionAction.java:317-319, 359, 362`; `UnsubmitProjectAction.java:283-285, 289` |
| C6 | The six submit overrides of `aicrra`, `aiccra3`, `pabra` link every submission to `projectID=102080&phaseID=407`; Java computes the real URL as `{6}` only outside planning | grep of the six keys; `ProjectSubmissionAction.java:304-307` |
| C7 | `email.role.*` footer keys carry a support line and a do-not-reply notice in `global.properties`, `ciat` and `pabra`, which the shell footer already shows | `grep -E '^email\.role\.' global.properties custom/*.properties` |
| C8 | The base submit copy names CRP-era roles ("PMU or the Flagship Leader") and uses `<br><br>` | `global.properties:3906-3910` |

Verified live 2026-11-11 (local, throwaway DB): submit/unsubmit work in AICCRA and AICCRA_III; emails
`email_logs` 18401-18407 were stored without the shell.

## 3. In-Scope Requirements

### Functional

- **CHG-EMAIL-LAYOUT-001-FN-001 — Shell by default.** Every notification that opts into the shell MUST be wrapped in it for any Global Unit whose custom file does not define `email.layout`, including a unit with no custom file at all.
- **…-FN-002 — Opt-out.** A Global Unit MUST be able to keep plain notifications, with their support address and do-not-reply notice, through an i18n opt-out block in its custom file (`email.layout={0}` plus the restored footer keys, design DD-9).
- **…-FN-003 — Unchanged for current shell users.** For AICCRA and AICCRA_III the visible content of role, welcome and OTP notifications MUST NOT change. *(TEST removed 2026-11-11, judgment JD-3: it has no `email.role.*` overrides of its own, so its footer follows the base keys and changes; accepted for a test unit.)*
- **…-FN-004 — One shell definition.** The shell markup MUST be defined once in the codebase.
- **…-FN-005 — No repeated footer.** A notification sent in the shell MUST show the support address and the do-not-reply notice exactly once each (the shell footer's), for every Global Unit.
- **…-FN-006 — Submit and unsubmit in the shell.** The project submit and unsubmit notifications MUST be wrapped in the shell under the same rules as FN-001/FN-002.
- **…-FN-007 — Correct summary link.** Every submit notification that offers a summary link MUST point to the submitted project and the phase it was submitted in, in both the planning and the reporting cycle.
- **…-FN-008 — AICCRA / AICCRA_III copy.** The submit (with and without attached PDF) and unsubmit notifications of AICCRA and AICCRA_III MUST use the approved copy of Appendix A.
- **…-FN-009 — Neutral base copy.** The base submit (both variants) and unsubmit copy MUST name no program-specific role and MUST use the paragraph style of the shell, per Appendix A.
- **…-FN-010 — Recipients, subject and attachment unchanged.** TO, CC, BCC, subject wording and the PDF attachment of submit and unsubmit notifications MUST NOT change. Where the server previously sent a subject in another unit's wording, FN-011 governs and the subject changes to the right unit.
- **…-FN-011 — Submit and unsubmit texts from their own unit.** The submit and unsubmit notifications — subject, body, footer and shell — MUST be written with the texts of the Global Unit each one names (submit: the unit being submitted in; unsubmit: the unit the project belongs to), whichever page, unit or user the server's previous request served. *(Added 2026-11-11 by design DD-6; extended to submit and to the subject by judgment JD-1/JD-4.)*

### Non-Functional

- **…-NF-001 — Text-only change for units.** Opting a unit in or out MUST require no Java change and no database change.
- **…-NF-002 — i18n correctness.** Every changed `.properties` line MUST render through `MessageFormat` as written: apostrophes doubled, no brace other than an argument.
- **…-NF-003 — Plain-text part.** The text/plain part generated for each changed notification MUST stay readable (no raw markup, arguments filled).

### Data / UI / Security

- **Data:** not applicable — no schema or data change.
- **UI:** not applicable — no screen changes; email rendering is covered by FN-005/FN-008/FN-009.
- **Security:** not applicable as a new requirement. Noted, out of scope: the unsubmit justification `{7}` is inserted into HTML unescaped today (§4).

## 4. Out-of-Scope

- Rewriting the role/welcome bodies in `global.properties` (only their footer keys change).
- Wrapping the other email-sending classes (20 of the 30 that send through `SendMailS` stay unwrapped after this spec — design P-14); technical alerts stay unwrapped.
- Creating a custom file at Global Unit creation.
- The legacy `email.bye` family used by unwrapped emails.
- Submit/unsubmit overrides of alliance, eib, bigdata, gender, pim, wheat, test; pabra gets only FN-007.
- Subject wording (the unit it is resolved from is fixed by FN-011).
- Findings of the 2026-11-11 test session: no server-side permission check on `unsubmitProject.do`; blank/missing justification accepted or 500; stale menu after submit; unescaped `{7}`; "Dear ," when the project leader is the user who unsubmits (`UnsubmitProjectAction` leaves `{0}` empty).

## 5. Personas Affected

| Persona | Effect |
|---|---|
| Project/cluster leader | Receives submit/unsubmit emails in the shell with clearer copy and a working summary link |
| PMU / PMC / PIU | Same, as CC recipients and as the people who return clusters |
| Users of a Global Unit without its own shell | Role, welcome, OTP, submit and unsubmit emails start arriving in the shell |
| Superadmin creating a Global Unit | Nothing to do for emails |

## 6. Acceptance Criteria

**AC FN-001 / FN-006 — unit with no custom file**
- GIVEN a Global Unit whose `crp_custom_file` names no existing file (local: QATEST)
- WHEN a role email, the welcome email, the OTP email, a submit and an unsubmit email are sent for it
- THEN each stored `email_logs.message` is wrapped in the shell
- BUT it must NOT lose any part of its body
- AND IT MUST contain `MARLOSupport@cgiar.org` exactly once and the do-not-reply notice exactly once.

**AC FN-002 — opt-out**
- GIVEN a unit whose custom file carries the opt-out block (`email.layout={0}` plus the restored footer keys, design DD-9)
- WHEN any of the five emails above is sent
- THEN the stored message is the body alone, with no shell markup
- AND IT MUST still contain the support address and the do-not-reply notice once each (JD-2).

**AC FN-003 — AICCRA unchanged**
- GIVEN AICCRA and AICCRA_III
- WHEN a role, welcome and OTP email is sent before and after the change
- THEN the text content of the two messages is identical
- BUT it must NOT differ in support line, greeting or sign-off.

**AC FN-004 — one definition**
- WHEN `grep -c '^email.layout=' global.properties custom/*.properties` is run
- THEN only `global.properties` defines the shell, and any custom file defining the key defines `{0}`.

**AC FN-005 — no repeated footer, every unit**
- GIVEN the base file and each custom file that overrides an `email.role.*` key (alliance, aicrra, aiccra3, ciat, pabra)
- WHEN a role email (keys `email.role.dear`, `.support`, `.support.noCrpAdmins`, `.getStarted`, `.bye`) or the OTP code email is assembled for that unit
- THEN the support address and the do-not-reply notice appear once each
- AND IT MUST hold for alliance, whose `email.role.getStarted` override is kept.

**AC FN-011 — texts from their own unit**
- GIVEN the server's previous request loaded AICCRA_III texts
- WHEN an AICCRA project is unsubmitted, and separately WHEN an AICCRA project is submitted in an AICCRA session
- THEN subject and body use the AICCRA copy ("cluster"; "PMC" in the submit copy only — the unsubmit copy is Appendix A.1 verbatim)
- BUT it must NOT contain "Technical Implementation Team" in the subject or the body
- AND IT MUST hold the other way round: an AICCRA_III submit after an AICCRA request uses the AICCRA_III copy (today it does not — `email_logs` 18405).

**AC FN-007 — no PDF, no "attached"**
- GIVEN a planning-cycle submission whose PDF generation fails
- WHEN the email is sent
- THEN it uses the `.noPDF` copy with the `{6}` link
- BUT it must NOT say the summary is attached.

**AC FN-007 — summary link (defect)**
- GIVEN a submission of project P in phase F, in AICCRA, AICCRA_III, PABRA and a unit without a custom file
- WHEN the submit email is assembled, in the planning cycle (PDF attached) and in the reporting cycle
- THEN every summary link carries `projectID=P` and `phaseID=F`
- BUT it must NOT contain `projectID=102080` or `phaseID=407`.

**AC FN-008 / FN-009 — copy**
- GIVEN AICCRA, AICCRA_III and a unit without a custom file
- WHEN a submit (each variant) and an unsubmit email are sent
- THEN the body matches Appendix A for that unit, with every argument filled
- AND IT MUST contain no "Flagship" in the base copy and no `<br><br>` in any of the six rewritten keys.

**AC FN-010 — envelope unchanged**
- GIVEN the same submission before and after the change
- THEN `to_email`, `cc_email`, `bbc_email`, `subject` and the attachment name are identical
- BUT the subject may differ where the "before" email carried another unit's wording (AC FN-011 case).

**AC NF-002 / NF-003**
- Every added or changed `.properties` line passes the i18n apostrophe/brace review, and the stored plain-text part of each AC FN-008/009 email reads as sentences with arguments filled.

### Defect classes and their gates

| Defect class this spec can produce | Gate | Automated? |
|---|---|---|
| Java does not compile (wrap call, new argument) | Clean compile (marlo-verify) | Yes |
| Style violation in the two actions | Checkstyle delta vs HEAD (marlo-verify) | Yes |
| Apostrophe or brace broken by `MessageFormat` | i18n line review (marlo-verify) + rendered body check below | Partly |
| Footer repeated inside the shell | SQL count over `email_logs.message` per sent email (FN-005 AC) | Yes |
| Wrong or fixed summary link | SQL/grep over the stored message for `projectID=`/`phaseID=` (FN-007 AC) | Yes |
| Shell missing or applied where opted out | Presence of a shell marker in the stored message per unit (FN-001/002 AC) | Yes |
| **Email looks wrong when rendered** (spacing, broken HTML, image path) | **No automated check.** Substitute: the stored HTML of each AC email opened in a real browser and reviewed by the owner at the execute HITL pause | No — human check |
| Copy not what was approved | Diff of the stored body against Appendix A | Yes |

## 7. Constitutional Compliance Checklist

- [x] Phase replication: not applicable — no phased data written.
- [x] Save validation: not applicable — no save path changes.
- [x] Permissions: not applicable — no new action.
- [x] Specificity: not applicable — the opt-out is an i18n override, not a behavior flag (NF-001).
- [x] Migrations: not applicable — no schema change.
- [x] i18n: all user-facing text stays in `global.properties` / `custom/*.properties` (NF-002).
- [x] License header: no new Java file.
- [ ] Code style: Checkstyle delta on the two actions — at execute.
- [x] REST: not applicable.
- [x] Audit: not applicable.
- [x] Dependency floors: untouched.
- [x] Branching: `email-layout-default` from `staging` `460ecbd5be`.

## 8. Open Questions

| # | Question | Owner | Blocks |
|---|---|---|---|
| Q1 | Which Global Units are active in production? Any that must keep plain emails gets the opt-out block (FN-002, design DD-9). | Kenji Tanaka | Answered 2026-11-11 by Kenji Tanaka: **every Global Unit receives the new shell; none opts out.** The production list is not needed: no unit gets the opt-out block. |
| Q2 | Approval of Appendix A copy. | Kenji Tanaka / AICCRA PMU | This gate |

## 9. Decision Log

- 2026-11-11 — Shell becomes opt-out (option A) — the only option that covers units created after this change; three copies become one.
- 2026-11-11 — Base submit/unsubmit copy neutralised (FN-009) — added by the owner; a new unit would otherwise tell users to contact a "Flagship Leader".
- 2026-11-11 — Unsubmit test-session findings kept out of scope — separate tickets; this spec changes copy and wrapping, not authorization or validation.
- 2026-11-11 — Amended by design (Phase 2): FN-011 added (unsubmit runs without the unit's i18n file, design DD-6); AC FN-005 now names `email.role.support` and the OTP email; A.4 gains `email.role.support`; ciat/pabra also drop `email.role.support`; A.5 added (base OTP body). Closure sweep run: forward `grep -n "email.role.bye override\|support.noCrpAdmins\` and \`email.role.getStarted\` overrides\|alliance, aicrra, aiccra3, ciat, pabra, test" requirements.md proposal.md` — the old ciat/pabra lists and the AC FN-005 file list updated here; proposal.md §3 P6 and §5 item 3 describe the same overrides in summary form ("override them with the same pre-shell text") and stay true; backward: no document cites A.4 or AC FN-005 yet.
- 2026-11-11 — Amended by judgment round 1 (`judgment.md`): FN-003 drops TEST (JD-3); FN-011 covers submit and the subject (JD-1, JD-4); AC FN-002 requires the footer for opted-out units (JD-2); AC FN-007 adds the failed-PDF case (JS-3); A.4 `email.role.support` empty (JD-6); Q1 gates execute (JD-5). Closure sweep: forward `grep -n -E "TEST|please contact \{0\}|FN-011|Blocks|before specify|before T7|byte-for-byte" requirements.md proposal.md` — hits updated here and in proposal §4, §12, §14; the remaining TEST hits (§1, §2, AC FN-004) describe today's shell owners and stay true; backward: design.md §2/§15/§16 rewritten in the same round.
- 2026-11-11 — Q1 answered: every Global Unit receives the new shell, none opts out (owner decision, so the production list of active units is not needed). FN-002's opt-out stays as a mechanism, used by no unit today; T06 ships only its test. Closure sweep: forward `grep -n "Q1\|P-11" *.md` — requirements Q1, proposal Q1/§14, design §14/§16, tasks gate/T01/T06/DoD updated; judgment.md is a frozen record and stays as is.
- 2026-11-11 — Amended at execute (T05 gate, owner approval): AC FN-011's THEN covered submit and unsubmit with ("cluster", "PMC"), but the approved A.1 `unsubmit.email.message` (required verbatim by FN-008) contains no "PMC"; the THEN now limits "PMC" to the submit copy. Meaning of FN-011 unchanged (own unit's copy, no "Technical Implementation Team"). Closure sweep: forward `grep -n '"PMC"' *.md` — requirements AC FN-011 and tasks T05 (a) updated; the A.1 heading and the A.2 substitution line describe the AICCRA vocabulary and stay true; execution.md is history; backward: no document cites AC FN-011's wording.

---

## Appendix A — Copy for approval

Paragraph style is the one A2-2484 uses (`<p style="margin:0 0 16px 0;">`); it is shown here as plain text with
`¶` between paragraphs. Arguments: submit `{0}` submitter full name, `{1}` program, `{2}` title, `{3}` id,
`{4}` year, `{5}` cycle; `{6}` summary URL. Unsubmit `{0}` leader first name, `{1}` returner first name, `{2}`
program, `{3}` title, `{4}` id, `{5}` year, `{6}` cycle, `{7}` justification. No apostrophes are used, so NF-002
holds without doubling.

### A.1 AICCRA (`aicrra.properties`) — "cluster", "PMC"

| Key | Copy |
|---|---|
| `submit.email.message` (planning, PDF attached) | Dear all, ¶ {0} has submitted the {1} cluster "{2}" ({3}) for review in the {4} {5} cycle. ¶ The cluster cannot be edited again in this cycle unless a member of the PMC or the cluster leader returns it for changes. ¶ The cluster summary is attached as a PDF. You can also [open it in MARLO]({6}) or generate it at any time from the Summaries section. |
| `submit.email.message.noPDF` (reporting) | Dear all, ¶ {0} has submitted the {1} cluster "{2}" ({3}) for review in the {4} {5} cycle. ¶ The cluster cannot be edited again in this cycle unless a member of the PMC or the cluster leader returns it for changes. ¶ You can [download the cluster summary as a PDF]({6}) or generate it at any time from the Summaries section. |
| `unsubmit.email.message` | Dear {0}, ¶ {1} has returned the {2} cluster "{3}" ({4}) to you for changes in the {5} {6} cycle. ¶ Reason given: ¶ *{7}* (indented quote block) ¶ Please make the requested changes and submit the cluster again. |

### A.2 AICCRA_III (`aiccra3.properties`) — "Technical Implementation Team", "PIU"

Same three texts as A.1 with "cluster" → "Technical Implementation Team", "the cluster leader" → "the Technical
Implementation Team leader", "PMC" → "PIU", and "cluster summary" → "Technical Implementation Team summary".

### A.3 Base (`global.properties`) — neutral

| Key | Copy |
|---|---|
| `submit.email.message` | Dear all, ¶ {0} has submitted the {1} project "{2}" ({3}) for review in the {4} {5} cycle. ¶ The project cannot be edited again in this cycle unless it is returned for changes. ¶ The project summary is attached as a PDF. You can also [open it in MARLO]({6}) or generate it at any time from the Summaries section. |
| `submit.email.message.noPDF` | Dear all, ¶ {0} has submitted the {1} project "{2}" ({3}) for review in the {4} {5} cycle. ¶ The project cannot be edited again in this cycle unless it is returned for changes. ¶ You can [download the project summary as a PDF]({6}) or generate it at any time from the Summaries section. |
| `unsubmit.email.message` | Dear {0}, ¶ {1} has returned the {2} project "{3}" ({4}) to you for changes in the {5} {6} cycle. ¶ Reason given: ¶ *{7}* (indented quote block) ¶ Please make the requested changes and submit the project again. |

### A.4 Footer keys inside the shell (base file; AICCRA/AICCRA_III already have these)

| Key | Today (base) | New (base) |
|---|---|---|
| `email.role.dear` | `Dear {0}, <br><br>` | Dear {0}, (bold 20px paragraph, as AICCRA) |
| `email.role.support` | "Should you have any questions… contact {0} or the technical MARLO team (MARLOSupport@cgiar.org)" | *(empty, as AICCRA — callers pass an admins list that can be empty, so "please contact {0}" could read "please contact ."; judgment JD-6)* |
| `email.role.support.noCrpAdmins` | "Should you have any questions… (MARLOSupport@cgiar.org)" | *(empty — the shell footer shows the address)* |
| `email.role.getStarted` | "Please sign in to MARLO to get started." | Same sentence, as a paragraph |
| `email.role.bye` | "Best regards, MARLO Team, *** Please do not reply… ***" | Thank you! ¶ Kind regards, MARLO Team (as AICCRA) |

`ciat.properties` drops its `email.role.support` and `email.role.bye` overrides and `pabra.properties` drops its
`email.role.support`, `email.role.support.noCrpAdmins` and `email.role.getStarted` overrides (the latter pointed
to `aiccra.marlo.cgiar.org`), so both inherit A.4. `alliance.properties` keeps its `email.role.getStarted`.

### A.5 OTP code email, base body (`login.otp.email.html`)

The last sentence, "Need help? Contact {0} support at {3}.", is removed: inside the shell the footer already shows
the same address (`footer.contactEmail` is `MARLOSupport@cgiar.org` everywhere). The rest of the body, and the
text part (`login.otp.email.text`), are unchanged. This is the AICCRA body's shape since A2-2484.
- 2026-11-11 — Amended by judgment round 2: FN-002 names the opt-out block; FN-010 / AC FN-010 keep the subject wording and defer a wrong-unit subject to FN-011; §4 counts (20 of 30 stay unwrapped) and subject wording. Visible change accepted (design DD-5, §14): a failed-PDF planning submission of alliance, bigdata, eib, gender, pim, test and wheat now sends their own `.noPDF` copy with its link. Closure sweep: `grep -n -E "11 wrap|19 (do|stay)|9 of the|198-558|unconditionally|312-313|275-289|303-307|four keys|Subject lines" design.md requirements.md proposal.md` → no hits.
