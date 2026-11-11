# Proposal — Branded email shell by default, and submit/unsubmit emails inside it

> **Answer first.** Today the branded MARLO email shell reaches only AICCRA, AICCRA_III and TEST, because each
> Global Unit has to opt in through its own `custom/*.properties` file, and a Global Unit created from the
> superadmin screen never gets one. This change makes the shell the default in `global.properties`, cleans the
> footer keys so nothing prints twice inside it, and moves the project submit and unsubmit emails into the shell
> with rewritten copy and a working PDF summary link.

## 1. Document Control

| Field | Value |
|---|---|
| Spec Path | `changes/email-layout-default` |
| Slug | `email-layout-default` — derived from the free-text argument (first token) |
| Type | **Change** (it carries one embedded defect: the hardcoded summary link, §3 row P5) |
| Deliverable | software |
| Approval Mode | gated |
| Status | Approved 2026-11-11 (specified 2026-11-11) |
| Date | 2026-11-11 |
| Base | `staging` at `460ecbd5be` |
| Requirement source | Owner conversation of 2026-11-11 (local submit/unsubmit test session); no Jira ticket yet |
| Visual source | None new — the shell already approved in A2-2484 (`944b0f244a`) is reused as is |
| Depends on | none |
| Parallel-safe | yes — touches no migration, no shared Java contract; only i18n files and two actions |
| Related work | A2-2484 (`944b0f244a`, shell + role copy for AICCRA); `0eb8bcc05f` (exception report frame) |

## 2. Intent

Every Global Unit — existing or created tomorrow — sends its user-facing notifications in the same branded
MARLO shell, without anyone having to copy HTML into a new properties file. The project submit and unsubmit
notifications, which users receive at the two most important moments of a cycle, join that shell.

## 3. Problem / Current Behavior

| # | What happens today | Evidence |
|---|---|---|
| P1 | The shell lives in the i18n key `email.layout`. Its base value is `{0}` (body as is, no shell); a Global Unit gets the shell only by overriding the key. | `global.properties:3894` and the comment above it; `EmailLayout.java` class Javadoc |
| P2 | Only three files override it, all with the same 26-line shell (identical md5). The shell contains no AICCRA wording: logo, "Managing Agricultural Research for Learning and Outcomes", a do-not-reply notice and `MARLOSupport@cgiar.org`. | `custom/aicrra.properties:3182`, `custom/aiccra3.properties`, `custom/test.properties` (md5 `6b52db6b…` ×3) |
| P3 | Text resolution is `custom/<crp_custom_file>.properties` on top of `global.properties`. A Global Unit created from the superadmin screen gets `crp_custom_file` = its lowercase acronym, but the file is never created (`ENABLE_CUSTOM_PROPERTIES_FILE_CREATION = false`; when enabled it would copy `global.properties` into `src/`, which a deployed WAR does not have). So it falls back to `global.properties` and gets `{0}`. | `InternationalitazionFileInterceptor.intercept`; `GlobalUnitCreateAction.java:68, 442-467`; local DB `aiccradb_actsave`: units 49 `kt` and 50 `qatest`, no such files in `custom/` |
| P4 | Of the 30 classes that send through `SendMailS`, 8 wrap their body with `EmailLayout.wrap` (role, welcome); `OtpMailSender` wraps through its own path (design P-14). `ProjectSubmissionAction` and `UnsubmitProjectAction` send the body unwrapped, and append the legacy footer `email.support.noCrpAdmins` + `email.bye` (+ `email.getStarted` on unsubmit), which carries its own support line and do-not-reply notice. | `ProjectSubmissionAction.java:317-319, 359, 362`; `UnsubmitProjectAction.java:283-285, 289`; `grep -rl EmailLayout.wrap` = 9 files |
| P5 | **Defect.** `submit.email.message` and `submit.email.message.noPDF` in `aicrra`, `aiccra3` **and `pabra`** hardcode the summary link `…reportingSummary.do?projectID=102080&phaseID=407`. Every submission email of those units links to the same cluster. Java already computes the real URL as `{6}`, but only outside planning, and none of the six overrides uses it. | `ProjectSubmissionAction.java:304-307`; the six keys checked with grep; `global.properties` `.noPDF` uses `{6}` correctly |
| P6 | The shell-aware footer keys `email.role.*` are adapted in AICCRA/AICCRA_III only. In `global.properties` they still carry the support sentence and the do-not-reply notice, so with the shell they would print twice. `ciat.properties` (`email.role.support`, `email.role.bye`) and `pabra.properties` (`email.role.support`, `email.role.support.noCrpAdmins`, plus `email.role.getStarted` pointing to `aiccra.marlo.cgiar.org`) override them with the same pre-shell text. `alliance.properties` overrides `email.role.getStarted` with its own URL, which is fine. | `grep -E '^email\.role\.' global.properties custom/*.properties` |
| P7 | Submit/unsubmit copy in `aicrra` and `aiccra3` predates the A2-2484 rewrite (`Dear all,<br><br>…`), so it reads differently from the role emails sent in the shell. | `custom/aicrra.properties:3223-3228`, `custom/aiccra3.properties:3245-3250` |
| P8 | The base submit copy tells the reader the project can only be returned "by a member of the PMU or the Flagship Leader" — CRP-era roles a new Global Unit may not have — and all three base keys use `<br><br>`. Units with no override of their own read it: for submit, ccafs, a4nh, fta, livestock, maize, rice, wle, ciat and every new unit; for unsubmit, every unit except alliance, aicrra, aiccra3, pabra and test. | `global.properties:3906-3910`; `grep -l '^submit.email.message=' custom/*.properties` (10 files), `'^unsubmit.email.message='` (5 files) |

Verified live on 2026-11-11 (local `staging`, throwaway DB copy): submit and unsubmit work for AICCRA and
AICCRA_III and log their emails (`email_logs` 18401-18407), all sent without the shell.

## 4. Proposed Outcome

- Any Global Unit without its own `email.layout` sends role, welcome, OTP, submit and unsubmit emails in the shell. A unit that must not have it adds the opt-out block to its file: `email.layout={0}` plus its restored footer keys (design DD-9).
- No email sent in the shell repeats the support address or the do-not-reply notice that the shell footer already shows.
- Submit and unsubmit emails of every unit go through `EmailLayout.wrap`; for AICCRA and AICCRA_III their copy matches the A2-2484 tone.
- Every submission email that carries a summary link points to the submitted project and phase.
- AICCRA and AICCRA_III role/welcome/OTP emails keep their visible content. *(TEST removed at design judgment, JD-3: it inherits the base footer keys, which change.)*

## 5. Scope

1. Move the shell from the three custom files into `global.properties` (`email.layout`), and delete the three copies.
2. Adapt the `email.role.*` footer keys in `global.properties` the way AICCRA did (no support sentence, no do-not-reply notice, styled `dear` / `bye`).
3. Adapt the pre-shell `email.role.*` overrides in `ciat.properties` and `pabra.properties` (and fix pabra's `aiccra.marlo.cgiar.org` getStarted URL).
4. `ProjectSubmissionAction` and `UnsubmitProjectAction`: wrap the body with `EmailLayout.wrap`, and build the footer from the shell-aware `email.role.*` keys instead of `email.support.noCrpAdmins` / `email.getStarted` / `email.bye`.
5. Summary link: always pass the real URL as `{6}`; replace the hardcoded link with `{6}` in the six overrides (`aicrra`, `aiccra3`, `pabra`; message and `.noPDF`).
6. Rewrite `submit.email.message`, `submit.email.message.noPDF` and `unsubmit.email.message` for `aicrra` and `aiccra3` in the A2-2484 style (copy to be approved by the owner/PMU at specify time).
7. Fix the shell comment in `global.properties` and the `EmailLayout` Javadoc, which describe opt-in.
8. Neutralise the base submit/unsubmit copy in `global.properties` (`submit.email.message`,
   `submit.email.message.noPDF`, `unsubmit.email.message`): drop the CRP-era "PMU or the Flagship Leader" in favour
   of role-neutral wording, and move from `<br><br>` to the `<p style>` paragraphs the shell-aware keys use. Same
   arguments `{0}`–`{7}`, same `{6}` link, subjects unchanged, no Java change. Added by the owner on 2026-11-11.

## 6. Non-Goals

- Rewriting the bodies of the role/welcome emails in `global.properties` (only their footer keys change; the base
  submit/unsubmit bodies are in scope, §5 item 8).
- Changing the submit/unsubmit overrides of units other than AICCRA and AICCRA_III (alliance, eib, bigdata, gender,
  pim, pabra, wheat, test keep their own copy; pabra only gets the `{6}` link fix).
- Wrapping the other email-sending classes (30 send through `SendMailS`: 8 wrap today, 10 after this spec, 20 stay unwrapped — design P-14; technical alerts must stay without the shell; the rest is a later decision).
- Creating a `custom/` file when a Global Unit is created.
- Changing the legacy `email.bye` family, which other unwrapped emails still use.
- The unsubmit findings of the same test session (no server-side permission check on `unsubmitProject.do`, blank/missing justification, stale menu after submit) — separate tickets.
- Subject wording (`[AICCRA] …` prefix in `aiccra3`) — noted, not changed here; the unit a subject is resolved from is fixed by FN-011.

## 7. Affected Users, Systems, And Specs

| Area | Impact |
|---|---|
| Users of units without their own shell (local: KT, QATEST; production: **to confirm**) | Their role, welcome, OTP, submit and unsubmit emails start arriving in the shell |
| Users of CIAT, PABRA, Alliance (if active in production) | Same, with their overrides cleaned so nothing repeats |
| AICCRA / AICCRA_III project leaders, PMU/PIU | Submit/unsubmit emails in the shell with new copy and a correct summary link |
| Code | `global.properties`, `custom/{aicrra,aiccra3,test,ciat,pabra}.properties`, `ProjectSubmissionAction`, `UnsubmitProjectAction`, `EmailLayout` (Javadoc) |
| Specs | `changes/center-email-otp-login` (OTP email reads `email.layout` through `UnitTextProvider` — must render the same) |
| Plain-text part | Generated automatically since A2-2484; must stay readable for the rewritten bodies |

## 8. Visual Reference

- Source: None new.
- Location: the existing shell in `custom/aicrra.properties` (`email.layout`), approved in A2-2484.
- Notes: the change reuses that shell verbatim; only body copy changes, reviewed as text at specify time.

## 9. Requirement Delta Preview

### ADDED Requirements
- A Global Unit with no `email.layout` of its own sends shell-wrapped notifications.
- Submit and unsubmit emails are shell-wrapped.

### MODIFIED Requirements
- The shell becomes opt-out (`email.layout={0}` plus the restored footer keys, design DD-9) instead of opt-in.
- The summary link in submission emails always targets the submitted project and phase.
- The `email.role.*` footer in `global.properties`, `ciat` and `pabra` no longer repeats what the shell footer shows.
- Submit/unsubmit footers use the `email.role.*` keys.
- The base submit/unsubmit copy names no CRP-era role and uses the shell's paragraph style.

### REMOVED Requirements
- The three duplicated copies of the shell in `aicrra`, `aiccra3` and `test`.
- The hardcoded `projectID=102080&phaseID=407` link.

## 10. Approach Options

| Option | What | For | Against |
|---|---|---|---|
| **A. Shell as the default in `global.properties`** | Scope above | Every unit, new or old, is covered with no per-unit step; one copy of the shell instead of three | Changes the look of emails for every unit without an override, so production units must be checked first |
| B. Keep opt-in, add the shell to each unit's file by hand | Copy `email.layout` into `ciat`, `pabra`, `alliance`, … and into each new unit's file | No unit changes unless chosen | A new unit still gets nothing until someone adds a file and deploys; the shell keeps multiplying |
| C. Generate the unit's file at creation time | Enable `copyInternationalizationFileIfNeeded` with the shell | Automatic on paper | Writes into `src/` at runtime: it never reaches a deployed WAR, so it does not work on a server |

## 11. Recommended Approach

**Option A** — chosen by the owner on 2026-11-11. It is the only option that fixes the root cause (P3) for
units that do not exist yet, and it reduces three copies of the shell to one. Its one risk (P6, duplicated
footers) is bounded and fully enumerated: five `email.role.*` keys and the OTP body in `global.properties`, and five overrides in two files (ciat 2, pabra 3).

## 12. Risks, Dependencies, And Open Questions

| # | Item | Handling |
|---|---|---|
| Q1 | **Which Global Units are active in production?** The local copy shows only 45, 47, 48, 49, 50 active. | Answered 2026-11-11 by Kenji Tanaka: **every Global Unit receives the new shell; none opts out.** |
| Q2 | Approval of the new submit/unsubmit copy for AICCRA / AICCRA_III, and of the role-neutral base copy in `global.properties` (§5 item 8) | Draft in `/akili-specify`, approved by the owner (PMU/PIU wording) |
| R5 | The neutral base copy changes the submit/unsubmit emails of legacy units without an override (ccafs, a4nh, fta, livestock, maize, rice, wle, ciat) if any is still active | Accepted at Q1 (2026-11-11: every unit gets the shell); arguments `{0}`–`{7}` are kept, so only wording and spacing change |
| R1 | A footer key missed in the enumeration prints twice | Enumeration recorded in §3 P6 with the command used (KZ-…-directory-abstraction-1: every sibling, not only the named ones); verification renders each email type in a unit with and without its own file |
| R2 | `getText` runs `MessageFormat`: apostrophes must be `''`, braces only as arguments | marlo-verify i18n gate on every changed line |
| R3 | The OTP email (`changes/center-email-otp-login`) resolves the shell outside a request through `UnitTextProvider` | Verify the OTP email for a unit with and without its own file |
| R4 | Planning-cycle submissions attach the PDF and today get no `{6}` | Pass `{6}` in both cycles; the copy decides whether the link is shown when the PDF is attached |

## 13. Success Criteria

- In a unit with no own file (e.g. QATEST), a role email, the welcome email, the OTP email, a submit and an unsubmit email are all stored in `email_logs` inside the shell, each with exactly one support line and one do-not-reply notice.
- In AICCRA, the same role/welcome/OTP emails render the same visible content as before the change.
- A submission email of AICCRA, AICCRA_III and PABRA links to the submitted project and phase (no `102080` / `407`).
- In a unit with no own file, the submit and unsubmit bodies contain no "Flagship" / "PMU" wording and use the same paragraph style as their greeting and footer.
- `grep '^email.layout=' custom/*.properties` returns only units that deliberately opt out, each with the DD-9 footer keys beside it.
- Compile and Checkstyle gates pass; i18n lines reviewed for `''`.

## 14. Next Step

```text
/akili-specify changes/email-layout-default
```

Standard depth. Q1 answered 2026-11-11: every unit receives the new shell, none opts out.
