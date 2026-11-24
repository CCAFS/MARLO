# Local PDF Report Engine — Judgment Day (design)

**Target:** `design.md` (with `requirements.md`, `spike.md`, `proposal.md` as context) · **Mode:** judgment_day,
`/akili-specify` Step 2.5 under `pre-approved` · **Judges:** two blind read-only judges, model `sonnet`
(author: `opus`) · **Skills passed:** none beyond the target documents and the cloned `reports-microservice`.

## Escalation note (owner mandate)

The skill's decision gate asks before round-one correction, and `/akili-specify` lists severe judgment-day findings
among the escalations that stop under `pre-approved`. The owner was unavailable and had mandated
*"sigue todo el proceso con las opciones recomendadas"*. The recommended option for every severe finding here was
**Fix and Re-judge**, and every fix was a bounded documentation correction. The run therefore applied that option
instead of halting, and records it here so the owner can overrule it. No finding was dismissed.

## Round 0 — initial judgment

| ID | Severity | Judges | Finding | Disposition |
|---|---|---|---|---|
| J-1 | severe | A + B (confirmed) | `pdf.module.ts` registers 17 helpers, not 18 | Fixed — 17 everywhere, P-14 |
| A:J-2 | severe | A (suspect → verified by one re-run: `git grep -n "generateAndSendJson("`) | `StudiesSummaryAction` never reaches the OICR call site | Fixed — §7.6, P-2 |
| B:J-2 | severe | B (suspect → verified: `ValidSessionCrpInterceptor.java:100-116`, precedent migration) | No explicit `'false'` rows → stale `true` across unit switch | Fixed — §5, P-6, §13 |
| B:J-3 / A:J-9 | severe (B) / warning (A) | both | Helper semantics not JavaScript's | Fixed — §7.1, §7.2 |
| A:J-7, A:J-8, B:J-7 | warning | both | Resource-loading hooks and HTML→DOM step unspecified | Fixed — §7.3, §11 |
| A:J-6, B:J-5 | warning | both | Execution model gaps (queue, budget, deadline vs proxy) | Fixed — §7.5 |
| A:J-10, B:J-4 | warning | both | Cache bounded by count only | Fixed — §11 |
| A:J-11, B:J-10a | warning | both | Null service with flag on would publish | Fixed — §7.6 |
| A:J-5, B:J-6 | warning | both | Public stacks / anonymous sessions | Fixed — §7.6 |
| B:J-9 | warning | B | Write-failure path | Fixed — §7.5 |
| A:J-12, A:J-13 | warning | A | Status claim; unsourced figures | Fixed — status, spike F1/F13 |
| A:J-14 / B:J-10e, A:J-15 / B:J-10b, A:J-16, B:J-10f | suggestion | both / one | Java 17 floor, XRLog, own constant, blank-template deviation | Applied |
| A:J-3 | warning | A | Host list configurable | INFO — kept constant; blocked-URI count logged |
| B:J-10c | suggestion | B | Serif / monospace families | INFO — not changed |

## Round 1 — scoped re-judgment of the fix delta

Both judges: all round-0 items resolved except one new defect.

| ID | Severity | Judges | Finding | Disposition (fix round 2) |
|---|---|---|---|---|
| N-1 | severe | A + B (confirmed) | `yesNoHelper` two-argument case inverted | Fixed — §7.2 row rewritten from `pdf.module.ts:89-97`; T02 vectors cover both call shapes |
| B:N-2 | warning | B | Units created after the migration have no row | Fixed — §5 cites `GlobalUnitCreationManagerImpl.java:646-686` (rows are created on unit creation); other unit types noted as the general specificity limit |
| N-3 | warning | A + B | Layout not bounded | Fixed — §7.5 states it; accepted risk in §17 |
| A:N-2 | warning | A | Status line cited a missing `judgment.md` | Fixed — this file |
| N-4 | suggestion | A + B | §13 wording, "usually", row order, `publicStudyStack` line | Applied |

## Round 2 — scoped re-judgment

Both judges: every round-1 item resolved, no new blocking defect. Verdicts: **A APPROVE, B APPROVE**.

INFO items applied as wording after approval (no design change): §7.5 budget sentence clarified (fetches only);
§5 states that a unit created from a template unit with the flag `'true'` inherits it (accepted). P-9 stays
`UNVERIFIED` — both judges checked it statically with `javap` against the 3.14.0 / 1.10.0 jars; a citation as run
in this session does not exist, so T02/T03 settle it at runtime.

Counts: confirmed severe 2 (J-1, N-1), suspect-verified severe 2 (A:J-2, B:J-2), INFO 2, fix rounds 2/2.

JUDGMENT: APPROVED ✅

> *2026-11-24 note (T17):* B:J-10c's "Serif / monospace families … not changed" is superseded — PhantomJS (Pivot 2) falls back to DejaVu Serif/Mono like production, so those fonts are now bundled (`execution.md` T17).
