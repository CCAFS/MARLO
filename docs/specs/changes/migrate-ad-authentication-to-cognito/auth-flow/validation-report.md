# Validation Report — CHG-COGNITO-AUTH-001 (`auth-flow`)

## 1. Document Control

| Field | Value |
|---|---|
| Spec | `changes/migrate-ad-authentication-to-cognito/auth-flow` |
| Date | 2026-09-07 |
| Branch / HEAD | `staging-cognito-impl` · `6db4c653f5` · tree clean |
| Primary coverage evidence | `test-report.md` (2026-09-07, second pass) — reused, cross-checked, not re-derived |
| Phases run | 0–8, all |
| **Auditor independence** | `author ≠ auditor` **holds for the code**: every task was implemented on `sonnet` and audited on the T3 tier. **It does not hold for four artefacts** — see §12 |

## 2. Summary

**Verdict: PASS with WARN. No FAIL. No `PRODUCT_BUG`.**

**Nothing blocks archive except the user's own review**, which the readiness guidance requires and which has not happened yet.

| Phase | Check | Result |
|---|---|---|
| 1 | Task completion | **PASS** |
| 2 | File existence | **PASS** |
| 3 | Build integrity — compile | **PASS** |
| 3 | Build integrity — tests | **PASS** |
| 3 | Build integrity — Checkstyle | **WARN** (repo-scope, pre-existing) |
| 4 | Requirement coverage | **PASS with WARN** |
| 5 | Quality audit | **PASS**, 6 advisories carried |
| 6 | Design conformance | **WARN** — one figure contradiction |
| 6 | Constitution impact | **PASS** |

**Findings: 0 FAIL · 3 WARN · 6 advisory.**

## 3. Task Completion — PASS

**25 of 25 tasks are `[x]`.** No task is `[ ]` or `[~]`.

`execution.md` carries **48 numbered sections**, and **42 task entries cross-reference it** for evidence. Every
task closed since 2026-09-03 names either an audit verdict, a measured mutation, or real E2E evidence.

Eight tasks FAILed audit at least once and were reworked — T06 and T11 twice each. That history is recorded
rather than smoothed over, which is the condition this check exists to confirm.

## 4. File Existence — PASS

`design.md` names **20 distinct files**. All 20 resolve in the working tree. No file the design claims was
created is missing, and no deletion the design claims was made is outstanding.

## 5. Build Integrity

| Command | Result |
|---|---|
| `mvn -q install -DskipTests -pl marlo-web -am` | **PASS** — clean, no output |
| `mvn -pl marlo-web test` | **PASS** — `Tests run: 207, Failures: 0, Errors: 0` |
| `mvn -q checkstyle:check` | **WARN — cannot execute** |

### WARN-1 — the Checkstyle gate is doubly inert, repo-wide

`CLAUDE.md:184` lists Checkstyle as **"Required. A hard gate, not advisory."** It is neither, today, for two
independent reasons:

1. **It cannot run.** `maven-checkstyle-plugin:2.9.1` against `checkstyle:8.18` throws
   `NoSuchMethodError: Checker.setClassloader`. **Verified on `marlo-utils`, a module this spec never touched**,
   so the cause is the repository's plugin pairing and not this work.
2. **If it ran, it would enforce nothing.** `configuration/marlo-checkstyle.xml:7` sets
   `<property name="severity" value="warning"/>` repo-wide, so violations would be reported and the build would
   still pass. This is the pre-existing **EB-3**.

A third fact bounds the impact: no `includeTestSourceDirectory` is configured, so Checkstyle reads
`src/main/java` only — it would not have covered the three test files added in the second pass regardless.

- **Severity:** WARN, not FAIL. **Scope: the repository, not this spec.**
- **Ownership:** whoever governs `marlo-parent/pom.xml`. Fixing it means changing a dependency pairing under
  `CLAUDE.md`'s dependency-baseline rule, and flipping `severity` to `error` would surface pre-existing
  violations across every module.
- **Blocks archive? No.** Grading a spec FAIL for a repo-wide condition it cannot fix from its own branch would
  be the wrong instrument. **But it must not be recorded as "Checkstyle passed"** — it did not run.
- **Compensating evidence:** style was verified by hand on every file this spec touched — 2-space indent, no
  line over 120 characters, no tabs, GPL header on each new file.

## 6. Requirement Coverage — PASS with WARN

`test-report.md`'s matrix was reused as primary evidence and cross-checked against `requirements.md` at
**scenario and clause granularity**. **16 requirements · 18 scenarios · 13 `AND IT MUST` · 4 `AND IT MUST NOT`
· 11 `BUT` · 73 matrix rows.**

| Grade | Count | Disposition |
|---|---|---|
| **PASS** | 58 | A named test method or cited manual evidence proves the clause |
| **GAP** | 10 | **All accepted** by the user 2026-09-07, each with a named owner and follow-up |
| **AUTOMATION_DEFERRED** | 2 | **Both accepted**, owners named |
| **NOT OBSERVED** | 3 | Held open for the dedicated environment |

**No `PRODUCT_BUG` is recorded**, so no requirement inherits a FAIL from the test evidence.

**Every `GAP` and `AUTOMATION_DEFERRED` entry carries an accepted remediation.** Under Phase 4's grading rule,
an entry *without* one covering a strict or negative clause would be **FAIL**; three such entries existed at
the first pass and all three are resolved — finding 17 closed by test, finding 18 accepted with an owner,
finding 16 corrected by amendment.

### WARN-2 — three environment validations remain NOT OBSERVED

| # | Item | Owner |
|---|---|---|
| 19 | **8a** expiry leeway with a real token and clock | DevOps/IBD + QA |
| 20 | **8b** NTP clock synchronisation | Infrastructure |
| 21 | **8c** real JWKS rotation — **cannot be forced** | DevOps/IBD |

**These are correctly *not* PASS.** SEC-001's `AND IT MUST` clause requiring rejection of an expired token **is**
covered synthetically, on both sides of the 60 s leeway; what is unobserved is behaviour against a real token,
a real clock and a real key rotation. The two facts are graded separately, which is why this is a WARN on
environment observation and not a FAIL on the clause.

**Blocks archive? No** — they are explicitly held open with named owners, which the readiness guidance permits
for WARN. They must not be converted to PASS on synthetic coverage, and if no rotation occurs within the
project window, 8c is recorded as not observed rather than passed.

## 7. Linting & Code Quality

Checkstyle is covered in §5. The 4R sweep found no spec violation. Six advisory findings are carried forward
from `execution.md` so they surface here rather than dying in the audit trail.

| # | Lens | Advisory | Source |
|---|---|---|---|
| A1 | Risk | **`LoginAction.java:518-528` is inert.** The block commented `// Hack for cleaning cached authorization` clears the cache using principals `logout()` has already nulled. FN-007 is satisfied entirely by Shiro. It reads as a safety net and is not one | §48.6 |
| A2 | Risk | **`getAuthError()` is dead** and returns raw user-controlled text next to templates that do not auto-escape. Nothing reads it; Struts binds through the setter | §44.3 |
| A3 | Resilience | **Deep-link fidelity narrowed.** `new URI(String)` throws on `\|` and `^`, which browsers do not percent-encode, so a link like `…/projectList.do?filter=a\|b` now routes to the dashboard | §35.3 |
| A4 | Reliability | **`getBaseUrl()`'s production behaviour is unverified.** The test double returns a fixed value; `APConfig` returns `null` on an unconfigured `BASE_URL` and force-prefixes `http://`. An http/https mismatch behind a proxy would discard **every** deep link with the suite still green | §35.3 |
| A5 | Reliability | **Browser autofill watch-item.** Credential autofill could dispatch `change` and hide the rejection message. **It did not reproduce** in the live check | §44.3, §45.1 |
| A6 | Readability | `loginForm.ftl`'s comment at `:133` still says "login.js shows exactly one of these at a time", now false for the file as a whole since T22 added two server-selected slots | §44.3 |

**Security boundaries were audited and hold.** SEC-005 is enforced on both endpoints with the refusal proven
byte-identical to a wrong password; SEC-006's collapse is proven across five unrelated rejection reasons
producing a byte-identical URL; PKCE is now asserted at the exchange, not only at authorize.

## 8. Design Conformance — WARN

The implementation matches `design.md` §5.2, §8, §13.1 and §13.3. Three deliberate deviations are each
explained in the design or the execution notes, as Phase 6 requires: the `cognitoUnloggedStack` deviation from
§8, the T16 reflection workaround, and the T21 call-site adaptation.

### WARN-3 — a figure contradicted by another document's own prose

`requirements.md:486` and `:506` — the **OQ-3 closure entry**, dated 2026-09-02 — both state that deployed
environments supply **7** `cognito.*` environment variables, and `:506` adds *"verified 2026-09-02 that those 7
names match `APConfig`'s `@Value` fields and `marlo-test.properties` exactly"*.

**There are eight.**

| Source | Count |
|---|---|
| `requirements.md:486`, `:506` (OQ-3) | **7** |
| `marlo-test.properties` | **8** |
| `marlo-dev.properties` | **8** |
| `tasks.md:790` | calls `cognito.identity.provider` **"the eighth setting"** |

The entry was accurate when written. **T15 added the eighth on 2026-09-03 and the OQ-3 entry was never
updated** — the same half-applied-amendment shape that FAILed T18 twice and that §40.6 records for the task
ledger.

- **Severity: WARN, and operationally the sharpest finding in this report.** A deployment provisioning seven
  variables would omit `cognito.identity.provider`, and T15 exists precisely because without it users land on
  the Cognito hosted-UI provider chooser instead of going straight to the CGIAR IdP. The failure is visible
  rather than silent, but it is a real degradation configured from a document that reads as verified.
- **Ownership:** the Leader. It is a documentation correction, not a code change.
- **Blocks archive? Not on its own** — but it should be corrected before archive, because archiving freezes a
  document that states a verified-sounding falsehood about deployment configuration.
- **Not remediated here**, per the user's instruction to stop after reporting.

## 9. Test Evidence Summary

| Suite | Result |
|---|---|
| Backend unit + integration | **PASS** — 207 tests, 0 failures |
| Frontend unit | **BLOCKED — no infrastructure.** Stopped rather than improvised. Defect class **D-5** |
| E2E | **Manual only.** Eleven-plus real corporate sign-ins; both coexistence directions on one account in one session; V-4, V-5, V-6, V-7 each browser-observed |

**Mutation evidence exists where it matters.** T19, T20, T21 and T22 each measured mutations one at a time and
recorded which tests reddened. The PKCE assertion added in the second pass needs no mutation probe: the
recording fields default to `null`, so a production regression supplying nothing already fails `assertNotNull`.

## 10. Agent Guide / Constitution Impact — PASS

No `## Constitution Impact` block in `execution.md` requires a child guide. `CLAUDE.md`'s **Module Guides**
index correctly states that MARLO's five Maven modules share one stack and one style config, so no child guide
exists and none is owed.

**Shared-file discipline was respected throughout.** The TRD §8.4 correction — it states `/api/*` authenticates
"via tokens (e.g. `QAToken`)" while `MarloShiroConfiguration.java:113` says `authcBasic` — is **queued, not
applied**, per `CLAUDE.md`'s rule that a spec branch records shared-file edits rather than making them. It is
pending work for `/akili-archive` Step 3.

## 11. Remediation

| # | Finding | Severity | Owner | Blocks archive? |
|---|---|---|---|---|
| **WARN-3** | OQ-3 says 7 `cognito.*` variables; there are 8 | WARN | Leader | Not on its own — **but correct it first** |
| **WARN-1** | Checkstyle is a required gate and is doubly inert, repo-wide | WARN | `marlo-parent/pom.xml` owner / Tech lead | No |
| **WARN-2** | 8a / 8b / 8c NOT OBSERVED | WARN | DevOps/IBD, Infrastructure, QA | No |
| A1–A6 | Advisories, no spec violation | advisory | various | No |

**No FAIL findings. Nothing requires code change.**

## 12. Archive Readiness Recommendation

| Gate | Status |
|---|---|
| All required tasks `[x]` | **MET** — 25/25 |
| No unresolved FAIL findings | **MET** — zero FAIL |
| WARN findings accepted or assigned follow-up | **MET** — all three have named owners |
| Tests cover the key requirements and scenarios | **MET** — 58 clauses proven; every gap accepted or held open |
| Implementation drift reflected in the docs | **MET, with WARN-3 outstanding** |
| **The user has reviewed the validation summary** | **NOT MET — this is the remaining gate** |

**Recommendation: archive-ready once the user reviews this report**, and preferably after WARN-3 is corrected.

### Auditor independence — the limit of this report

`author ≠ auditor` holds for the code: every task was implemented on `sonnet` and independently audited. **It
does not hold for four artefacts, all written by the Leader who is also writing this validation:** the FN-002
S5 and MIG-001 amendments, the three T17 javadoc corrections, the Category A reclassification, and
`test-report.md` itself. Those are documentation, not behaviour, and each is traceable to evidence a reader can
re-check — but a validation report is the wrong place to leave that unsaid.

### Remaining steps before `/akili-archive`

1. **Correct WARN-3** — the 7-versus-8 count in the OQ-3 entry, with a correction-closure sweep.
2. **User review of this report** — the one readiness gate not met.
3. Optionally decide WARN-1's disposition: fix the plugin pairing, or record it as an accepted repo-level debt.

Then, and only then:

```text
/akili-archive changes/migrate-ad-authentication-to-cognito/auth-flow
```
