# Judgment Day — design review

**Target:** `design.md` (draft, Phase 2) against `requirements.md` and `proposal.md`, at HEAD `0edf846105`
**Mode:** `/akili-specify` Step 2.5 — Review Design, selected by the owner 2026-11-20
**Judges:** A and B, blind, read-only (Explore), on `sonnet`. The design was written on `opus` (author ≠ auditor).
**Round:** 1 (judgment). No fix has been applied yet.

## Ledger

| ID | Status | Severity | Judges | Finding | Orchestrator check (as run) |
|---|---|---|---|---|---|
| J-1 | **confirmed** | severe | A-1, B-1 | P-14 is false: `marloRequestCreation.sendFailed` already exists, unused. "Restore" would add a duplicate key | `grep -n sendFailed global.properties` → `2684:marloRequestCreation.sendFailed=The request could not be sent…`. The architect misread a grep output while writing the row |
| J-2 | **confirmed** | severe | A-2, B-4 | P-4 citation does not reproduce: the grep returns 8 hits in 5 files, not 6. The conclusion holds | both judges ran it and got 8 |
| J-3 | **confirmed**, severity split | A: warning, B: severe | A-6, B-2 | The §3 migration description contains "user's", an apostrophe inside a single-quoted SQL literal, while the same paragraph says "No apostrophes" | `design.md` §3 text |
| J-4 | **suspect → verified** | severe | B-3 | AICCRA's own text for the project source is `Cluster C{0} - {1}`, so FN-003's "the request source must NOT contain Cluster" fails for AICCRA, the unit most likely to switch | `custom/aicrra.properties:2245`, `custom/aiccra3.properties:2246`; global `:2677` = `Project P{0} - {1}`. The word comes from the unit's i18n file, not from hardcoded Java |
| J-5 | confirmed (both) | warning | A-5, B-8 | `GlobalUnit.isAiccra()` returns `true` for every unit. The `[#if !action.isAiccra()]` branch of `menu-superadmin.ftl` never renders, and "both variants" cannot be checked live. No ledger row | `GlobalUnit.java:333-336` "Temporary compatibility condition; … return true" |
| J-6 | confirmed (both) | warning | A-3, B-5 | `APConfig.getClarisaAPI*` each log an ERROR when the value is null, so a missing config gives up to 4 lines, not 1 (OPS-001). A test on the action's logger would not see them | `marlo-utils/…/APConfig.java:321-343` (judges) |
| J-7 | confirmed (both) | warning | A-4, B-6 | The test double `EnvelopeConfig` cannot answer the CLARISA getters, so the success and payload tests need a config subclass. Missing from §2, no row | `EmailSendDoubles.java:90` (judges) |
| J-8 | confirmed (both) | warning | A-9, B-7 | `isClarisaMode()` has no stated null rule. `prepare()` swallows failures and can leave `loggedCrp` null or as the stale session copy. "Same pattern as `isEnabled`" is loose (no `isActive` filter there) | `PartnersSaveAction.java:349-355` (judges) |
| J-9 | confirmed (both) | warning / suggestion | A-5, B-13 | "AICCRA still sends `aiccra`" does not hold for AICCRA_III (unit 47 → `aiccra_iii`). Pre-revive every unit sent the literal `aiccra` | judges: `V2_6_0_20261008_1200` |
| J-10 | confirmed (both) | warning / suggestion | A-8, B-12 | The OPS-001 exception line should name the exception class in its message. SEC-002 "HttpClient exceptions carry no auth header" is unproved by a fixed-message test | — |
| J-11 | suspect (B) | warning | B-10 | `HttpClients.createDefault()` has no timeout, so a hung CLARISA leaves the submit hanging, and FN-005's "cannot be reached" never fires | — |
| J-12 | suspect (B) | warning | B-9 | UI-002 markup is underspecified: `country.email=Email` has no address. `footer.contactEmail` exists | judges: `global.properties:7452`, `:2692` |
| J-13 | suspect (B) | suggestion | B-11 | The FTL re-reads the mode at render time. A flag set at the branch is safer, and `isClarisaMode()` must be public | — |
| J-14 | suspect (A) | warning | A-7 | A CLARISA test host and an AICCRA-specific account exist in the local `marlo-dev.properties`, which bears on P-9 / P-10 | `git check-ignore` → the file is gitignored (not committed, contrary to A-7). Never copy its values |
| J-15 | suspect (B) | suggestion | B-14 | §14 says "next login"; the proposal says "next login or crp refresh" | — |
| J-16 | suspect (A) | suggestion | A-11 | Unit types 1–4 deviate from AGENTS.md's 1, 3, 4, and the deviation is not recorded in a DD | — |
| J-17 | suspect (A) | suggestion | A-10 | requirements §2 cites `:531` (call) and the design cites `:588` (definition); both are correct | no action |

**Ledger header:** the "12 verified" count in design §16 is wrong. After J-1 it is 11 verified, 1 contradicted, 2 `UNVERIFIED`.

## Counts

Confirmed severe: 2 (J-1, J-2). Severity split: 1 (J-3). Single-judge suspect verified by the orchestrator: 1 (J-4).
Confirmed warnings/suggestions: 6 (J-5 to J-10). Suspects: 7 (J-11 to J-17).

## Decision needed before round-one correction

J-4 changes a requirement (FN-003 and its AC), so it is the owner's call:
- **(a)** Send the unit's own text. "Cluster" comes from AICCRA's i18n file, not from hardcoded Java. The FN-003 constraint becomes "no hardcoded unit wording", and the AC asserts the unit's text.
- **(b)** Always send the global text (`Project P{0} - {1}`) to CLARISA.

## Owner decision (2026-11-20)

"sí, aplica la ronda con (a) y re-judge": J-4 option (a); correction scope J-1 … J-16 (J-17 needs none); Fix and Re-judge.

## Fix round 1 → scoped re-judgment 1

All of J-1 … J-16 were applied to `requirements.md` and `design.md`. Both re-judges marked J-1 … J-16 RESOLVED, except
J-8 (A: partial) and J-10 (B: partial), which fed the findings below.

| ID | Judges | Severity | Finding (fix-caused) | Fix round 2 |
|---|---|---|---|---|
| R-1 | RA-1, RB-1 | A: warning, B: severe | The new FN-010 (`unitReadFromDb`) refused MARLO-mode requests whenever the DB re-read had failed, contradicting NF-002. The existing suite never sets the flag | FN-010 narrowed to "no unit at all"; a failed re-read falls back to the session unit's login-time parameters; NF-002 lists FN-010 as its one exception; flag removed |
| R-2 | RA-2, RB-2 | warning | UI-003 did not name the FN-010 text | UI-003 names "could not be saved" for FN-010 |
| R-3 | RA-3, RB-4 | warning | The null-acronym failure was not in FN-005 and had no log row | FN-005 + §11 row |
| R-4 | RA-4, RB-3 | warning | R-6 asserted HttpClient exception content without a source, while the design logged the throwable | The throwable is no longer logged (class name only); R-6 closed |
| R-5 | RA-5 | suggestion | Timeout values were unsourced | Named, owner-adjustable constants; HttpClient 4.3+ cited |
| R-6 | RA-6 | suggestion | Migration text narrower than FN-008 | "next login or unit switch" |
| R-7 | RA-7 | suggestion | The §8 i18n item described a new string that does not exist | Reworded |
| R-8 | RB-5, RB-6, RB-7 | suggestion | D2 omitted the live call; the Decision Log said "below"; OPS-001 said "line" while the design said "event" | Fixed |

## Fix round 2 → scoped re-judgment 2 (final)

Both re-judges: every R-row RESOLVED; **no severe finding**; no requirement/design contradiction; no non-reproducing
citation; ledger header "17 rows — 15 verified, 2 UNVERIFIED (1 High, 1 Low)" correct.

| ID | Judges | Severity | Finding | Disposition |
|---|---|---|---|---|
| I-1 | FA-1, FB-1 | info (moderate / nit) | FN-010's stated cause omits a third path: the session has a unit, but the re-read *returns* null (the row is missing), which overwrites `loggedCrp` (`PartnersSaveAction.java:352`). FN-010 still fires, so the behaviour is fail-safe | Editorial wording widened after the verdict (FN-010, its AC, design §9). No behaviour change |
| I-2 | FB-2 | info (nit) | Label "(unit unknown)" left in design §5 | Editorial: "(no unit)" |
| I-3 | FA-2 | info (nit) | The closure-sweep grep names a phrase that still appears as a negative statement | No change |

## Receipt

- Target: `design.md` + `requirements.md`, `changes/partner-request-routing-specificity`, at HEAD `0edf846105`
- Rounds: 2 fix rounds, 2 scoped re-judgments (ceiling reached, not exceeded)
- Confirmed severe: 2 (J-1, J-2) + 1 split (J-3) + 1 verified suspect (J-4), all resolved; fix-caused severe split (R-1), resolved
- Remaining: 3 INFO rows (I-1 … I-3)
- Skills resolved: `marlo-migration`, `marlo-verify` (passed to both judges)

JUDGMENT: APPROVED ✅
