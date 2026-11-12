# Judgment Day — design.md (CHG-EMAIL-LAYOUT-001)

| Field | Value |
|---|---|
| Target | `design.md` (draft of 2026-11-11) against `requirements.md` and `proposal.md` |
| Code base | `staging` `460ecbd5be` |
| Judges | two blind read-only judges, model `sonnet` (design authored on `opus`) |
| Round | 1 — findings frozen, awaiting the owner's decision on correction |
| Skill resolution | judgment-day (AKILI integration); reference files not packaged — contract from SKILL.md |

## Premise settled by the orchestrator (single re-run)

- **P-3, second half — contradicted.** `{crp}/submit` (`struts-projects.xml:1140-1148`) declares no interceptor; package
  `projects` extends `marlo-default`, whose `default-interceptor-ref` is `marloDefaultStack` (`struts.xml:591`),
  which has no `i18nFile` (stacks with it: `struts.xml:198-558`, all named). Live corroboration: `email_logs` 18405
  (local, 2026-11-11) — an AICCRA_III submission sent with AICCRA wording, subject
  "[AICCRA] AICCRA_III cluster [C102093] has been submitted".

## Confirmed (both judges)

| ID | Sev | Finding | Judge A | Judge B |
|---|---|---|---|---|
| JD-1 | severe | Submit also runs without `i18nFile`; DD-7 is false. Submit must resolve its texts with the unit provider, as DD-6 does for unsubmit. | J-4 | J-1 |
| JD-2 | severe | A unit that opts out (`email.layout={0}`) loses the support line and the do-not-reply notice, because footer-once empties the base keys. FN-002 has no AC for it. | J-2 | J-2 |
| JD-3 | severe | TEST has no `email.role.*` overrides, so DD-2 changes its emails; FN-003 (and proposal §4) promise it unchanged. P-5 omits TEST. | J-1 | J-3 |
| JD-4 | severe/warning | DD-6 does not say the subject goes through the unit provider; subject and body could come from different units. | J-3 | J-5 |
| JD-5 | severe/warning | P-11 (production units) is rated Low and its gate differs across the three documents. | J-5 | J-11 |
| JD-6 | warning | Base `email.role.support` keeps "contact {0}"; callers pass an empty admins list, so a unit without admins reads "please contact ." (ciat/pabra overrides had no `{0}`). | J-8 | J-7 |
| JD-7 | warning | Consumer tests incomplete: `EmailLayoutTest.testBaseLayoutSendsTheBodyAsItIs`, `UnitTextProviderTest.twoProvidersAreIndependentAndHoldNoSharedState`, `OtpMailSenderTest.theRenderedEmailStatesEveryRequiredElement` (no "http") and `everyDynamicValueIsHtmlEscapedInTheHtmlPartOnly` (escaped `{3}`); an opt-out fixture must keep `{0}` covered. | J-10 | J-4 |
| JD-8 | warning | The shell hardcodes `MARLOSupport@cgiar.org`; removing `{3}` from the OTP body makes `footer.contactEmail` irrelevant for shell units — an invariant to state. | J-12 | J-13 |

## Suspect (one judge)

| ID | Sev | Finding | Judge |
|---|---|---|---|
| JS-1 | severe | Line citations do not reproduce (submit footer at 317/319; unsubmit getText 266-285). | A J-7 |
| JS-2 | severe | `{6}` is consumed only by the six overrides plus the new base copy; other units' planning overrides never show it — scope must say so. | A J-6 |
| JS-3 | warning | Planning copy says "attached" even when PDF generation fails (`buffer == null`). | B J-8 |
| JS-4 | warning | Missing consumers: `reports/ai-context/role-assignment-email-catalog.md` (describes base `{0}` and rollback); stale "shell of email.layout above" comments in aicrra/aiccra3/test. | A J-9 |
| JS-5 | warning | Side effects of DD-4 on units outside Appendix A (legacy `<br>` copy inside the shell; aicrra loses "To begin…" — intended) not enumerated. | A J-11 |
| JS-6 | warning | For a Center (type 4) session, `InternationalitazionFileInterceptor` resolves the file by origin-unit acronym, not `crp_custom_file`. | B J-6 |
| JS-7 | warning | Email-sending class counts inconsistent (30 / 21 / 9 do not reproduce; 28 or 34 depending on pattern). | B J-9 |
| JS-8 | warning | Proposal P6 omits ciat `email.role.support`; the Decision Log says P6 "stays true". | B J-10 |
| JS-9 | suggestion | Budget "9 review rounds" vs judgment fix cap; wording "8 wrapped" = 7 actions + SendEmails. | A J-13, J-14 |
| JS-10 | suggestion | Mention the `ProjectLeaderEditAction` / `ValidateProjectSectionAction` precedent (actions adding bundles themselves) as a rejected alternative. | B J-12 |

## Contradictions
None. (Judge A rated P-3's claim confirmed with a race caveat; Judge B contradicted its second half; the orchestrator re-run settles it as contradicted — JD-1.)

## Counts
Confirmed 8 (severe 5, warning 3) · Suspect 10 · Contradictions 0 · INFO 0.

## Status
Round 1 frozen. Owner chose **Fix and Re-judge** (2026-11-11).

## Fix delta — round 1

| Finding | Fix |
|---|---|
| JD-1 | design DD-6 covers both actions; DD-7 withdrawn; P-3 rewritten (both lack `i18nFile`) with live evidence; P-12 added; requirements FN-011 extended to submit |
| JD-2 | design DD-9 opt-out block (restores footer keys + OTP sentence); requirements AC FN-002 requires the footer for opted-out units |
| JD-3 | requirements FN-003 / AC FN-003 and proposal §4 drop TEST; design DD-1 and P-5 state TEST uses the base keys |
| JD-4 | DD-6 and FN-011: subject, body, footer and shell from the same provider; AC FN-011 checks subject and body |
| JD-5 | P-11 Impact High, single gate "before `/akili-execute` starts" in design §14, requirements Q1, proposal §12/§14 |
| JD-6 | DD-2 and Appendix A.4: base `email.role.support` empty; P-13 added |
| JD-7 | design §2 test table lists each affected method, opt-out fixture, new copy test |
| JD-8 | DD-10 invariant |
| JS-1 | line citations re-run (`:291, :304-314, :317-319, :327-352, :359, :362`; unsubmit `:251, :261, :282-285, :289`) |
| JS-2 | DD-5 states which keys consume `{6}` |
| JS-3 | DD-5 and AC FN-007: variant chosen after PDF generation |
| JS-4 | design §2 Docs: ai-context catalog; three stale comments |
| JS-5 | design §14 accepts the look of units outside Appendix A |
| JS-6 | design §16 divergence note |
| JS-7 | P-14 with commands; requirements §4 and proposal §6 counts |
| JS-8 | proposal P6 lists ciat/pabra `email.role.support` |
| JS-9 | budget rounds explained; P-10 "7 actions + SendEmails" |
| JS-10 | DD-6 names the precedent as rejected |

## Scoped re-judgment — round 1 result

| Finding | Judge A | Judge B |
|---|---|---|
| JD-1, JD-3, JD-4, JD-5, JD-6, JD-7, JD-8, JS-3..JS-6, JS-8..JS-10 | resolved | resolved |
| JD-2 | partially (FN-002 / proposal wording) | resolved |
| JS-1 | partially (old citations in requirements C5/C6, proposal P4/P5) | partially |
| JS-2 | not resolved | not resolved |
| JS-7 | resolved | partially (counts off by one) |

Fix-caused defects (merged):

| ID | Sev | Finding | A | B |
|---|---|---|---|---|
| RJ-1 | severe (B) / warning (A) | DD-5 says other units ignore `{6}`; seven units' `.noPDF` already use it and their failed-PDF emails change | R-1 | R-1 |
| RJ-2 | warning | P-3 range `198-558` should be `198-573` | R-2 | R-6 |
| RJ-3 | warning | Stale line citations in requirements C5/C6, proposal P4/P5 | R-5 | R-3 |
| RJ-4 | warning | Proposal §11 counts ("four keys", "three overrides") stale | R-4 | R-4 |
| RJ-5 | warning | FN-010 "subject must not change" vs FN-011 changing a wrong-unit subject | R-8 | R-5 |
| RJ-6 | warning | FN-002 and proposal still say `{0}` alone opts out; DD-9 silent on `dear`/`getStarted` | R-3 | — |
| RJ-7 | warning | Counts: `OtpMailSender` wraps but is not one of the 30 senders → 8 of 30 wrap, 20 stay unwrapped | — | R-2 (orchestrator re-ran: confirmed) |
| RJ-8 | suggestion | DD-2 "unconditionally" overstated; P-12 URL is `getCurrentCrp()`; §16 High count; P-9 docs list and `-E` escaping; DD-6 precedent lines | R-6, R-7, R-9 | R-7, R-8 |

## Fix delta — round 2 (final round)

| Finding | Fix |
|---|---|
| RJ-1 / JS-2 | DD-5 states every `.noPDF` reads `{6}` and the seven units' failed-PDF change; §14 accepts it |
| RJ-2 | P-3: `198-573`, plus `struts-home.xml:40, 93`, `marloDefaultStack` body `:584-589` |
| RJ-3 / JS-1 | requirements C5/C6 and proposal P4/P5 re-cited (`:317-319`, `:304-307`, `:283-285, 289`) |
| RJ-4 | proposal §11: five `email.role.*` keys + OTP body; five overrides in two files |
| RJ-5 | FN-010 and AC FN-010: subject wording unchanged; FN-011 governs a wrong-unit subject; §4 / proposal §6 reworded |
| RJ-6 / JD-2 | FN-002, proposal §4/§9/§13 name the opt-out block; DD-9 explains why `dear`/`getStarted` are not in it |
| RJ-7 / JS-7 | P-14, requirements §4, proposal §3 P4 and §6: 30 senders, 8 wrap, `OtpMailSender` separate, 10 wrap and 20 do not after the spec |
| RJ-8 | DD-2 wording, P-12 (`:149-150`, `getCurrentCrp()` = session unit), §16 High rows listed, P-9 docs and `-e` commands, DD-6 precedent `:186-208` / `:722-744` |

## Final scoped re-judgment — round 2 result

Both judges: 11 of 11 open items resolved; severe remaining 0 (A and B). Fix-caused notes, recorded as INFO and
applied as housekeeping (records only, no design change): design Status line; requirements Decision Log round-2
entry (with the seven-unit failed-PDF change); proposal Status; DD-9 note that the restored `email.role.support`
keeps today's empty-admins gap for opted-out units.

## Receipt

| Field | Value |
|---|---|
| Target | `design.md` (+ `requirements.md`, `proposal.md` as amended) |
| Rounds | 2 fix rounds, 2 scoped re-judgments (cap reached, not exceeded) |
| Confirmed severe (round 1) | 5 — all resolved |
| Suspect (round 1) | 10 — all resolved |
| Fix-caused (round 1 re-judgment) | 8 merged — all resolved |
| Fix-caused (final) | 0 severe, 1 warning, 4 suggestions — INFO, housekeeping applied |
| Contradictions | 0 (P-3 settled by the orchestrator's re-run) |

JUDGMENT: APPROVED ✅
