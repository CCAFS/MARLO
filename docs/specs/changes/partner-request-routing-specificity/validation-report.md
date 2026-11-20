# Validation Report — Partner request routing per Global Unit (CLARISA or MARLO)

> **Verdict: PASS with warnings. Archive-ready once the owner accepts the warnings below.**
> No FAIL. Every requirement is implemented and verified. 8 WARN items stay open, all external dependencies (P-9,
> P-10) or process findings; none blocks archive. W-5, a documentation drift, was fixed during validation.

## 1. Document Control

| Item | Value |
|---|---|
| Spec ID | CHG-PARTROUTE-001 |
| Spec path | `docs/specs/changes/partner-request-routing-specificity/` |
| Validated at | `3b34c2ceeb` (branch `revive-local-partner-request-approval`) |
| Code base compared | `d19d4096d6` (spec committed, before any code) → `3b34c2ceeb` |
| Date | 2026-11-20 |
| Validator | Claude Code session on Opus (T3). The Implementer ran on Sonnet, so the auditor and the author are different models. The validator also led execution: an independent re-reading of the diff was done, but the separation is weaker than with a fresh session |
| Deliverable / AI Agent | software / no, so there is no eval gate and no final look |
| `test-report.md` | Absent: `/akili-test` was not run. Coverage was verified directly from the tests written in execute |

## 2. Summary

| Area | Result |
|---|---|
| Task completion | PASS: 5 / 5 `[x]`, each with an `execution.md` entry and a closing Reviewer PASS |
| File existence | PASS: all 11 files of design §2 are present, with nothing unexpected |
| Build integrity | PASS: clean compile, 1385 tests / 0 failures, Checkstyle 0 new violations |
| Requirement coverage | PASS: 21 / 21 requirements. Two carry a WARN on external evidence (D2 live, P-9) |
| Code quality (4R) | PASS. 9 advisory notes |
| Design conformance | PASS. One documentation drift (WARN) |
| Test evidence | PASS. Unit and static tests plus live T05 evidence |
| Constitution impact | PASS: no module created or reshaped |

## 3. Task Completion

| Task | Status | Evidence | Result |
|---|---|---|---|
| T01 Specificity seed | `[x]` | `execution.md` T01: falsifiers (a)–(c) red then green, DB counts on a throwaway copy and an empty schema, Reviewer PASS | PASS |
| T02 Routing + both-mode tests | `[x]` | T02: falsifiers (a)–(f), two parallel lens Reviewers PASS, budget tripwire accepted by the owner | PASS |
| T03 Templates | `[x]` | T03: falsifiers (a)–(c), Reviewer PASS. The first Leader re-run hit a load-induced flake in `OtpMailSenderTest`, then came back green | PASS |
| T04 Admin agent-context | `[x]` | T04: sweep hits listed with an action each, Reviewer PASS | PASS |
| T05 Live proof | `[x]` | T05: attempt 1 FAIL (office screenshots collapsed), attempt 2 PASS. 9 steps plus the falsifier evidenced, with screenshot hashes recorded | PASS |

## 4. File Existence

| Design §2 entry | Present in `d19d4096d6..HEAD` |
|---|---|
| `PartnersSaveAction.java` (modified) | yes, +181 / −36 |
| `APConstants.java` (marlo-web, marlo-data) | yes, +3 each |
| `menu-superadmin.ftl`, `projectPartners.ftl`, `partnersSave.ftl` | yes |
| Migration `V2_6_0_<ts>__AddPartnerRequestsToClarisaSpecificity.sql` | yes, `V2_6_0_20261120_1306__…` |
| `PartnersSaveActionRequestTest.java` (modified) | yes |
| `PartnersSaveActionClarisaModeTest.java`, `PartnerRoutingSpecificityParityTest.java` (new) | yes |
| `docs/specs/domain/admin/agent-context.md` | yes |

No file outside the footprint changed. No struts XML, `.properties`, JS or CSS changed (P-13, P-14).

## 5. Build Integrity

All gates were run by the validator at `3b34c2ceeb` on 2026-11-20.

| Gate | Command | Result |
|---|---|---|
| Clean compile | wipe `target/classes` + `mvn -o -pl marlo-data,marlo-web -am test-compile -DskipTests` | exit 0. Compiled 2431 (marlo-data), 1065 (marlo-web) and 148 (tests) |
| Full suite | `mvn -o -pl marlo-web -am test` | `Tests run: 1385, Failures: 0, Errors: 0, Skipped: 0` |
| Checkstyle 8.18 | `.claude/skills/marlo-verify/scripts/checkstyle.sh` on the 6 changed Java files; jar run directly on the same files at `d19d4096d6` | 0 violations now, 0 at the base, so the delta is 0 |
| i18n | no `.properties` change | n/a (NF-003) |
| Boot smoke | done live in T05 (`run-marlo-java17.sh`, health check 302) | PASS |

## 6. Requirement Coverage

Each row was checked clause by clause against requirements §6.

| Requirement | Clauses checked | Evidence | Result |
|---|---|---|---|
| FN-001 | absent row → MARLO; resolved at submit; BUT `"TRUE "`, `yes`, empty → MARLO | `readClarisaMode()` (active row, exact key, `Boolean.parseBoolean`); D11 tests; T05 step 8 | PASS |
| FN-002 / NF-002 | stored and alerted; AND IT MUST NOT contact CLARISA | MARLO branch unchanged; rewritten pin + `testMarloModeNeverCallsClarisaEvenForAnAiccraUnit`; T05 step 3 | PASS |
| FN-003 | one request with every field; the unit's own source; BUT no unit literal; "Request sent" | D2 tests with literals, real `custom/aicrra.properties`; greps → 0; payload equals pre-revive fields (`3e4d08bd7e`) except the two designed changes | PASS |
| FN-004 | BUT no `partner_requests` row; AND no alert, on success or failure | D1 / D3 tests (manager never, mail never); T05 count unchanged at 5081 | PASS |
| FN-005 | non-2xx, exception, missing config, no acronym → failure with data; BUT no fallback | D3 tests; T05 401 and `HttpHostConnectException` live | PASS |
| FN-006 | stored parameters decide; the session is ignored | D11 session test; T05 falsifier and step 5 with no re-login | PASS |
| FN-007 | office requests local in CLARISA mode | static `RequestCountryOfficeAction` check kept; T05 step 6 (`is_office` 222 → 224) | PASS |
| FN-008 | row saved per unit; next submit follows; menu at next login; BUT other units untouched | T05 steps 4–6; unit 47 received a `false` row (P-6 behaviour), so its mode did not change | PASS |
| FN-009 | pending requests stay listed and decidable | T05 step 7 | PASS |
| FN-010 | no unit → `saveFailed`, one log line; BUT not stored, not sent | `testNoUnitStoresNothingSendsNothingAndLogsOnce`; `clarisaMode` stays false | PASS |
| UI-001 / SEC-001 | hidden in CLARISA; AND shown in MARLO; BUT guards unchanged | T03 pins (both branches); T05 s3 / s6 menu screenshots; step 9 (403, GET changes nothing) | PASS |
| UI-002 | mailto text; BUT no trigger; AND the MARLO trigger | T03 pins; T05 attempt 2 clips, triggers 15 → 0 | PASS |
| UI-003 | "could not be sent" in CLARISA mode; "could not be saved" in MARLO mode and with no unit | `isClarisaMode` test + T03 pin; CLARISA text shown live (s5). The MARLO text was not shown live (A-5) | PASS |
| SEC-002 | no credentials or email in logs or the page | D4 log appender tests; T05 grep `Authorization` 0, email 0 | PASS |
| OPS-001 | one single-line event from the action logger; no stack trace | D4 `onlyEvent()`; §11 texts match the code exactly | PASS |
| MIG-001 | one row per existing type 1–4; idempotent; clean DB; AND no `custom_parameters` | T01 DB runs; T05 Flyway on startup (4 rows, 0 custom) | PASS |
| NF-001 | no `isAiccra(`, no literal, constant in both files | greps → 0; parity test | PASS |
| NF-003 | i18n | no `.properties` change | PASS (n/a) |
| NF-004 | gates | §5 | PASS |
| D2 live (requirements §7) | one live call against a CLARISA test endpoint, or an accepted risk | live call reached the test host and got **401**; P-10 recorded as accepted risk | **WARN** (W-1) |
| Q1 / P-9 | `misAcronym` for non-AICCRA units | UNVERIFIED; not testable because of the 401 | **WARN** (W-2) |

## 7. Linting & Code Quality

The validator read the full `PartnersSaveAction` diff, the three templates and the test diffs on its own.

**Architecture:**
- The branch sits after `validate()` and after the type and country guard (design §7).
- The `postToClarisa` seam is protected and is the only network line (DD-4).
- Resources are closed with try-with-resources, and the timeouts are named constants (§10).

**Security:**
- The throwable is never logged (§11).
- Credentials appear only in the header.
- HttpClient logs through `commons-logging` 1.2 → JUL at INFO, with no `jcl-over-slf4j` bridge, so header and wire DEBUG never reach logback. Confirmed live: `Authorization` appears 0 times.

**4R advisory findings** (carried from `execution.md` and the validator's own reading; not gating):

| # | Lens | Finding |
|---|---|---|
| A-1 | Reliability | `org.json` `put(key, null)` drops the key, so a missing website, type or user omits the field instead of sending `null`. Confirm what CLARISA expects once P-10 is settled |
| A-2 | Resilience | Timeouts are not unit-tested (a recorded gap). Basic-auth header building is covered by no unit test; its only check is the live 401 |
| A-3 | Reliability | `sendToClarisa` checks the credentials, and `postToClarisa` reads them again from `config`, as the `(url, json)` seam forces |
| A-4 | Reliability | `host + CLARISA_CREATE_PATH`: a configured host with a trailing `/` yields `//api/...` |
| A-5 | Reliability | MARLO-mode "could not be saved" was proven by unit test and static pin only, not live |
| A-6 | Reliability | `theRequestPageReadsBothFailureKeysByMode` would still pass with the two branches swapped. The `isClarisaMode` unit test and the T05 s5 screenshot cover the real behaviour |
| A-7 | Readability | `CLARISA_DEFAULT_SOURCE` feeds both `requestSource` (fallback) and `externalUserComments`, but its comment names one use |
| A-8 | Risk | The first save on the Parameters screen writes default `custom_parameters` rows for every missing parameter of every unit, which is existing P-6 behaviour. Worth a release-note line |
| A-9 | Reliability | Two existing `LOG.error("Failed to get projectInfo…")` calls run before routing, so one submission can log more than one line on the action's logger. This is outside the OPS-001 count |

## 8. Design Conformance

| Check | Result |
|---|---|
| §1 flow (FN-010 guard → `readClarisaMode` → branch) | PASS: the code follows the flowchart |
| §9 / DD-2 (stored parameters, not session) | PASS: proven live by the falsifier and step 5 |
| §10 payload table | PASS: field names and sources match, including the two designed changes (`requestSource`, `misAcronym`) |
| §11 log lines | PASS: all six texts and levels match exactly |
| §5 templates | PASS: the CLARISA office markup matches the description; `#requestModal` stays rendered |
| Deviation recorded | `Base64` instead of `BasicScheme`, so the header is identical (T02 *Not Done*). A blank acronym is treated like a null one (stricter). Both are recorded in `execution.md` |
| Proposal alignment | PASS: scope, non-goals (office requests local, route not blocked) and Visual Reference ("None, no new screen") hold |
| **Cross-document figures** | **WARN (W-5):** `design.md` names the live-proof task **"T6"** 4 times (P-6, P-9, P-10, §17 D2). `tasks.md` re-counted to 5 tasks, so it is **T05**. The budget's "5 tasks" and the requirements' "one row per existing type 1–4" agree across all documents |
| Budget | WARN (W-3): 1,047 LOC against ~470 (the tripwire fired at T02; the owner accepted ~1,050); review rounds 7 / 7; tasks 5 / 5 |

**Design review (UI):** the spec is behaviour-only (design §5, proposal §8: no visual change), so `impeccable-bridge`
was not run and there is no detector gate. The rendered states were checked by screenshot in T05.

## 9. Test Evidence Summary

| Layer | Tests | Status |
|---|---|---|
| Parity / static | `PartnerRoutingSpecificityParityTest`, 7 tests (constants, migration, apostrophe, 3 template pins) | green |
| Behaviour, CLARISA mode | `PartnersSaveActionClarisaModeTest`, about 20 cases (D1–D4, D11, `isClarisaMode`) | green |
| Behaviour, MARLO mode | `PartnersSaveActionRequestTest` (DD-5 rewrite, plus the never-calls-CLARISA case) | green |
| Regression | the full marlo-web suite, 1385 tests, including the CHG-PARTREQ-001 tests | green |
| Live | T05: 9 steps plus the falsifier on throwaway DBs, 4 key clip screenshots, SHA-256 in `execution.md` | evidenced |

The falsifiers were observed red, then green, for every task: 3 + 6 + 3, plus T05's DB falsifier.

## 10. Agent Guide / Constitution Impact

There is no `## Constitution Impact` block in `execution.md`: no module was created or reshaped. The module guide
`docs/specs/domain/admin/agent-context.md` was updated in T04 and matches the code. Nothing is pending for archive
beyond a CodeGraph re-index.

## 11. Remediation

| # | Level | Finding | Recommended action |
|---|---|---|---|
| W-1 | WARN | P-10: the CLARISA test host answers 401 to the configured credentials, so the 2024 contract is not proven live | Owner: get valid CLARISA credentials per environment before switching any unit on. Accepted risk until then |
| W-2 | WARN | P-9: the `misAcronym` value for non-AICCRA units and for `AICCRA_III` is unknown | Owner: confirm with the CLARISA team before switching such a unit (tasks §7 already says so) |
| W-3 | WARN | Budget overrun, 1,047 against ~470 LOC | Accepted by the owner 2026-11-20. Kaizen: test LOC was under-estimated |
| W-4 | WARN | Process: T05 made **3** live CLARISA calls against the **1** approved, and the Implementer reported 2 | All got 401, so nothing was created. Kaizen: a running cargo app reads its config from the war; editing `marlo-dev.properties` does not redirect it |
| W-5 | ~~WARN~~ **Resolved** | Documentation drift: `design.md` said "T6" (6 times, on 4 lines: P-6, P-9, P-10, §17 D2), but the live-proof task is T05 | **Fixed 2026-11-20** on the owner's request. Correction closure: forward `grep -n "\bT6\b" *.md` over the spec folder → 0 hits in `design.md` (the remaining hits are this report quoting the finding); backward `grep -n "P-6\|P-9\|P-10\|D2 payload" requirements.md tasks.md \| grep T6` → 0 hits. `git diff design.md`: 4 lines changed, only `T6` → `T05` |
| W-6 | WARN | `test-report.md` absent | Optional: `/akili-test` would mostly re-run what execute already proved. Accepting the execute evidence is reasonable |
| W-7 | WARN | Persona drift: `akili doctor --agents` reports all 4 `.agents/` personas UNMARKED | Fix on `staging` (shared-file discipline); not part of this branch |
| W-8 | WARN | `OtpMailSenderTest` is timing-flaky under heavy machine load | Outside this spec; kaizen or a separate fix |
| W-9 | WARN | The validator also led execution: a weaker independence than a fresh T3 session | The Reviewers in execute were independent (Opus vs Sonnet). For a stronger audit, re-run `/akili-validate` from a fresh session |

There are no FAIL or BLOCKED findings.

## 12. Archive Readiness Recommendation

**Ready for `/akili-archive changes/partner-request-routing-specificity`**, provided the owner:
1. accepts W-1 and W-2 as open external dependencies, already listed in tasks §7 *Operational Steps*;
2. ~~decides on W-5~~ (resolved: the "T6" references now read T05);
3. remembers the merge constraint: this branch ships **with or after** the unmerged revive work (`CHG-PARTREQ-001`),
   into `staging`.
