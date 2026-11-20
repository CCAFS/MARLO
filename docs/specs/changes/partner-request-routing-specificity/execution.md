# Partner request routing per Global Unit (CLARISA or MARLO) — Execution Log

## Document Control

| Item | Value |
|---|---|
| Spec ID | CHG-PARTROUTE-001 |
| Spec path | `docs/specs/changes/partner-request-routing-specificity/` |
| Branch | `revive-local-partner-request-approval` |
| Approval Mode | gated (requirements Document Control) |
| Started | 2026-11-20 |
| Leader | Claude Code session, Opus (T1) |
| Implementer | `akili-implementer` wrapper → `sonnet` (T2) |
| Reviewer | `akili-reviewer` wrapper → `opus` (T3), tools `Read, Grep, Glob` |
| Budget (design §15) | 5 tasks · ~470 LOC · 7 review rounds |

**Persona drift (recorded before the first spawn).** `akili doctor --agents` (akili-specs v2.45.0) reports all
four personas in `.agents/` as `UNMARKED` (no section markers). The deployed `implementer.md` does not state the
60-call checkpoint bound. `--fix` was not run: personas are constitutional files and this is a spec branch
(CLAUDE.md *Shared-File Write Discipline*). The drift was reported to the user, and is a pending item for `staging`.

## Task Execution History

### CHG-PARTROUTE-001-T01 — Specificity seed: migration, constants, parity test

- **Final status:** PASS
- **Date:** 2026-11-20
- **Implementer attempts:** 1
- **Effort:** medium · **Skills:** `marlo-migration`, `marlo-verify` (the task's `marlo-commit` is kept by the Leader,
  who commits)

**Attempt 1**

- Files changed:
  - `marlo-web/src/main/resources/database/migrations/V2_6_0_20261120_1306__AddPartnerRequestsToClarisaSpecificity.sql` (new)
  - `marlo-data/src/main/java/org/cgiar/ccafs/marlo/config/APConstants.java`
  - `marlo-web/src/main/java/org/cgiar/ccafs/marlo/config/APConstants.java`
  - `marlo-web/src/test/java/org/cgiar/ccafs/marlo/action/projects/PartnerRoutingSpecificityParityTest.java` (new, 4 tests)
- Implementer verification:
  - Clean compile exit 0: 2431 files in marlo-data, 1065 in marlo-web, 147 test files.
  - `mvn -o -pl marlo-web -am test`: 1364 run, 0 failures, 0 errors.
  - Checkstyle delta 0. Both `APConstants.java` are LF and stayed LF.
- DB runs (throwaway schemas, source `aiccradb` read only, because `aiccradb1` does not exist locally):

  | Run | `parameters` rows for the key | `custom_parameters` |
  |---|---|---|
  | Copy (types 1–5), before | none | 1616 |
  | Copy, run 1 | 1 per type 1–4 | 1616 |
  | Copy, run 2 | 1 per type 1–4 | 1616 |
  | Empty schema, no types, runs 1 and 2 | 0, no error | n/a |
  | Empty schema, types 1, 2, 3, 5, runs 1 and 2 | 1 per type 1, 2, 3 | 0 |

  Throwaway schemas `partroute_t01_tmp` and `partroute_t01_empty` dropped; `SHOW DATABASES LIKE 'partroute%'` empty.
- Red run: baseline `grep -c partner_requests_to_clarisa` → 0 in both `APConstants.java` and in every migration
  (observed at `0edf846105` by the spec). The parity test was not run against the pre-change tree; falsifier (a) is
  its observed red.
- Falsifiers (observed red, then green after revert):
  - (a) marlo-data constant `partner_request_to_clarisa` →
    `ComparisonFailure: ../marlo-data/src/main/java/org/cgiar/ccafs/marlo/config/APConstants.java PARTNER_REQUESTS_TO_CLARISA expected:<partner_request[s]_to_clarisa> but was:<partner_request[]_to_clarisa>`
  - (b) `User's` in the description →
    `AssertionError: the description holds no apostrophe: When on, partner requests … User's Country-office requests always stay in MARLO …`
  - (c) `NOT EXISTS` dropped → second run returns `1 2`, `2 2`, `3 2`, `4 2` (2 rows per type).
- Evidence re-run (Leader inline, non-author): clean compile exit 0 with 2431 / 1065 / 147 files; full suite
  `mvn -o -pl marlo-web -am test` → `Tests run: 1364, Failures: 0, Errors: 0, Skipped: 0`, BUILD SUCCESS →
  **VERIFIED**.
- Reviewer verdict: **PASS**. The description matches design §3 word for word; the expected key is a literal in the
  test (Disqualifier respected); the four properties are asserted and each falsifier turns them red; the migration
  assumes no ids, is DML only and idempotent, and its filename follows AGENTS.md.
- runtime events: none
- spawns: implementer 27 calls, 128577 tokens, ended complete; reviewer 9 calls, 51440 tokens, ended complete

**ADVISORY (4R, not gating)**
- Reliability: the parity test counts every migration that contains the quoted key. A future description-rewrite
  migration for this key (a precedent exists: `V2_6_0_20260925_1543`, `V2_6_0_20260928_0748`) would turn it red
  without a real defect. Counting only `INSERT INTO parameters` would be narrower.
- Readability: the `NOT EXISTS` assertion checks only that the text is present, not that it filters on type and key.
  The DB run covers the behaviour.
- Risk: the apostrophe regex depends on the tail `1, 'false', 2`, so a later change to the format, default or category
  fails with a less clear message.

**Not Done / Assumptions (Implementer, verbatim summary)**
- The first version of the apostrophe check passed falsifier (b) wrongly, because a stray `'` ended the regex early.
  It was rewritten to anchor on `', <n>, 'false', 2 FROM` and then went red.
- The migration assumes no Global Unit, user or parameter id. A missing type gets no row.
- `rm -rf` of `target/` sometimes printed "Directory not empty", so the wipe was repeated. A `-pl marlo-web` run
  without `-am` once corrupted the generated mapstruct sources, and a full wipe fixed it.

No owed item and no blocker, so this is not a continuation.

- **Requirements covered:** MIG-001 and its AC, NF-001 (constants) and the second clause of AC NF-001.
- **Decisions made:** the parity test lives in `action/projects/` per design §2. Falsifiers (a) and (b) were run
  through JUnitCore on pre-compiled classes, because repeated incremental Maven runs reported spurious
  `APConfig not found` errors (a dirty-tree symptom, see memory *no-concurrent-maven-builds*).
- **Issues encountered:** `aiccradb1` is absent locally, so `aiccradb` was the source of the copy.
- **Budget running totals:** tasks 1 / 5 · LOC 148 / ~470 · review rounds 1 / 7. Within budget.
- **Final verification:** compile and the full suite green (Leader re-run), Checkstyle delta 0, DB counts as above.
