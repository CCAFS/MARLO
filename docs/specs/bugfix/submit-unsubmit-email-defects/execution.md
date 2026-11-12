# Submit and unsubmit email defects — Execution Log

## Document Control

| Field | Value |
|---|---|
| Spec Path | `bugfix/submit-unsubmit-email-defects` |
| Spec ID | BUG-SUBMIT-EMAIL-001 |
| Branch | `email-layout-default` (R1), spec committed at `ef5f5c641a` |
| Approval Mode | gated |
| Leader | Opus 5.5 (T1) |
| Implementer / Reviewer | `akili-implementer` (sonnet) / `akili-reviewer` (opus, read-only) |
| Budget (design §15) | 5 tasks · ~480 LOC · 7 review rounds |
| Started | 2026-11-12 |

**Persona drift (pre-spawn check, 2026-11-12):** `akili doctor --agents` (akili-specs v2.44.0) reports all four
personas `UNMARKED`. The implementer persona carries no 60-call checkpoint bound. `.agents/*.md` are constitutional
files, so `--fix` cannot run on this spec branch. The owner's decision is recorded below.

## Task Execution History

**Owner decision (2026-11-12):** "si, sigue con las personas actuales y lanza T04". Run on the current personas. The
Leader enforces the 60-call bound by reviewing each report. `akili doctor --agents --fix` is deferred to `staging`
(pending item for archive).

### BUG-SUBMIT-EMAIL-001-T04 — Unsubmit always addresses the leader, greets correctly, no self-CC, no "null"

- **Decision before spawn (execute-time spec edit, 2026-11-12):** the CC rule "omit the actor's address when it equals the TO" (design §2 row 4, DD-6, T04 scope and case (c)) left the leader's own CC entry when actor = leader, so it could not satisfy FN-004 "MUST NOT also be in CC". The new rule is "CC omits every address equal to the TO". FN-004's meaning is unchanged. Closure sweep: `grep -n "omits the actor\|drops the actor\|leaves the actor out\|actor then leader as today" design.md tasks.md requirements.md` → only the amendment note.
- **Safety constraints in the brief:** unit tests only; no DB, no network, no app start; no other build running; the Implementer does not commit or touch files outside the scope; the Leader re-runs every gate after the report.

### BUG-SUBMIT-EMAIL-001-T01 — Measure the cluster report time on aiccratest

- **Status:** deferred `[~]` (2026-11-12). The owner has no aiccratest access now ("no tengo acceso ahora para medir tiempos"; approved "si, de acuerdo").
- **Decision (execute-time plan edit):** the constants start at 10 × 2 000 ms. P-9 and the measurement move to T05's post-deploy check, read from the `fetchClusterReportBytes` INFO line (outcome and elapsed ms). T02 no longer depends on T01.
- **Closure sweep:** `grep -n 'T1 \|T01' design.md tasks.md`. Updated: tasks summary line, the T01 status, the T02 dependency and its scope line, the T05 post-deploy scope, the dependency graph, the coverage row for NF-001, and design §10 and P-9. The remaining hits are T01's own heading and the T04 independence note, which are still true.
- **Final status:** PASS — 2026-11-12. **Implementer attempts:** 1. **Skills / effort:** marlo-verify, tdd; medium.
- **Attempt 1:**
  - **Files changed:**
    - `UnsubmitProjectAction.java`: one `leader` lookup; TO is always the leader; the two `getId() !=` conditions are removed; new `withoutRecipient(cc, to)`; greeting fallback to `email.greeting.everyone`; title fallback to `global.untitled`; the WARN carries the unit id.
    - `global.properties`: +1 line, `email.greeting.everyone=all`.
    - `UnsubmitProjectEmailTest.java`: +7 tests (a)–(g); the `DrivenUnsubmit` and `project(...)` helpers gained parameters.
  - **Red run (before the fix, quoted from the Implementer):** 7 of 15 failed.
    - (a) `CC has no leader address:   ANA@unit.test, ana@unit.test`
    - (b) `TO is the leader expected:<[ana@unit.test]> but was:<[]>`. Both ids are the cached `Long.valueOf(16L)`. The prediction was "Dear ,", but the TO assertion runs first, so this is the recorded value.
    - (c) `CC is the actor only expected:<[luis@unit.test]> but was:<[luis@unit.test, ana@unit.test]>`
    - (d) the body contains `Dear ,`
    - (e) `Dear   ,`
    - (f) `returned the QATEST project "null" (C77)`
    - (g) an NPE from the `return this` fallback outside Struts; the test now overrides `getText`.
  - **Falsifiers (executed, then reverted; `cmp` shows the files identical):**
    - CC filter removed → (a), (b), (c) and the existing send-path test go red.
    - `!=` put back on the TO → (b) red.
    - Greeting fallback dropped → (d), (e) red.
  - **Implementer verification:** clean compile 2430 / 1063 / 115, exit 0. `UnsubmitProjectEmailTest` 15, `EmailLayoutTest` 4 and `SubmitEmailCopyTest` 11 give 30 run, 0 failures. Checkstyle delta 0. Hygiene: 2 REVIEW items on the WARN line (a split constant string, and a ternary null guard), both acceptable. The i18n check is clean.
  - **Evidence re-run (Leader, non-author): VERIFIED.**
    - `rm -rf …/target/classes … && mvn -o -pl marlo-web -am test -Dtest=UnsubmitProjectEmailTest,EmailLayoutTest,SubmitEmailCopyTest` → exit 0, compiled 2430 / 1063 / 115, **Tests run: 30, Failures: 0, Errors: 0**.
    - `checkstyle.sh --baseline UnsubmitProjectAction.java` → HEAD 2, tree 2, delta 0.
  - **Reviewer verdict:** PASS (akili-reviewer, opus, effort high). The CC rule closes the actor = leader gap. The recipients for a different actor and leader are unchanged after `SendMailS:360`. Every case is null-safe. The id-16 test exercises the old reference path, and the id-1082 test uses distinct instances. Subject, BCC, JSON response and `SendMailS` are untouched.
  - **Runtime events:** none.
- **Assumptions (Implementer, verbatim in substance):**
  - The existing test `theSendPathKeepsRecipientsAndSubjectAndAttachesNothing` now expects CC = RETURNER only, following the amended design.
  - Two stray live comments were removed (`// Add project leader`, `// new method`).
  - The id-1082 test builds `Long.valueOf(1082L)` and `Long.valueOf("1082")`, two distinct instances, instead of using `new Long`.
- **ADVISORY (4R, non-gating):**
  - Reliability: a null actor email still becomes the literal "null" in the CC, and a null actor first name shows "null" in `{1}`. Both are pre-existing, outside T04, and the same kind of defect as D2.
  - Reliability: when there is no leader, the TO is `""` and `SendMailS:360` runs `replaceAll(", ", "")`. That would merge several CC entries into one, but today the CC has a single entry in that case. This belongs to a `SendMailS` cleanup.
  - Readability: the commit body should mention the two removed comments. `withoutRecipient` could trim the recipient once instead of on every pass.
  - Risk: none open. Test (a) covers trimming and case, and test (b) covers the real case where actor and leader are the same user.
- **Requirements covered:** FN-004, FN-005, FN-006 (unsubmit), FN-007 (unsubmit), NF-003.
- **Spawns:**
  - implementer: 24 calls, 141 097 tokens, ended complete
  - reviewer: 9 calls, 67 782 tokens, ended complete
- **Budget running totals:** 1 of 5 tasks done (T01 deferred). About 200 LOC against ~480 (Java ~40, properties 1, tests ~160). 1 of 7 review rounds used. Within budget.
