# Archive Summary — CHG-COGNITO-AUTH-001 (`auth-flow`)

## 1. Document Control

| Field | Value |
|---|---|
| Spec ID | `CHG-COGNITO-AUTH-001` |
| Parent spec | `changes/migrate-ad-authentication-to-cognito` — child **2 of 3** in `CHG-COGNITO-FAMILY` |
| Original path | `docs/specs/changes/migrate-ad-authentication-to-cognito/auth-flow/` |
| Archive date | 2026-09-07 |
| Branch at archive | `staging-cognito-impl` — **a spec branch. Not merged, not deployed, not promoted.** |
| Final status | **Complete and validated. Not released.** |

## 2. Final Status

**Migrate CGIAR user authentication from direct Active Directory / LDAP binds to Amazon Cognito OIDC,
gated per Global Unit so both paths coexist.**

| | |
|---|---|
| Tasks | **25 of 25 `[x]`** (T00–T22, plus T11b and one declared coverage extension) |
| Automated tests | **207**, 0 failures |
| Validation | **PASS with WARN** — 0 FAIL, 0 `PRODUCT_BUG` |
| Live E2E | Both coexistence directions validated by the user against the live Cognito pool |

**What this spec does not do:** it does not remove `org.cgiar.ciat.auth`. LDAP remains the path for every
CGIAR user whose Global Unit has not enabled the flag, which is every unit but AICCRA today. Retirement is
child 3, `directory-retirement`, and is not authorised.

## 3. Requirements Delivered

| ID | Requirement | State |
|---|---|---|
| FN-001 | Branch the login wizard by user type | Delivered — **amended, then re-amended** 2026-09-04 |
| FN-002 | Authenticate a CGIAR user through Cognito | Delivered — S5 **re-amended** 2026-09-07 |
| FN-003 | Preserve Global Unit selection across the redirect | Delivered |
| FN-004 | Post-login redirect parity | Delivered |
| FN-005 | Failure handling | Delivered |
| FN-006 | Username synchronization | **Amended 2026-09-04 — the premise was false.** The federated ID token carries no CGIAR login, so the write was removed |
| FN-007 | Logout | Delivered — local logout only; the corporate SSO session is never ended (OQ-8) |
| SEC-001…006 | Token validation, request integrity, session fixation, secret handling, no credential relay, no local-account unlock | Delivered |
| MIG-001 | Specificity-gated rollout | Delivered — S17 **amended 2026-09-07** to name the rollback route that is genuinely immediate |
| OPS-001, OPS-002 | Observability; LDAP available during rollout | Delivered |

## 4. Files Changed Summary

**Production**, from `execution.md`:

| Area | Files |
|---|---|
| New — security core | `CognitoTokenValidator(+Impl)`, `CognitoIdentityMapper(+Impl)`, `CognitoAssertion`, `CognitoAuthenticationToken`, `CognitoAuthSpecificity`, `TokenExchangeClient(+Impl)` |
| New — web | `CognitoLoginAction`, `CognitoCallbackAction`, `ShiroRequestSessionCacheResetter`, `LogSanitizer` |
| Modified | `LoginAction` (the `finishLogin` extraction, T01), `APCustomRealm`, `ValidateUserAction`, `CrpByUserEmailAction`, `APConfig`, both `APConstants`, `UserMySQLDAO`, `loginForm.ftl`, `login.js`, `global.properties`, `struts-home.xml`, `marlo-test.properties` |
| **Not** modified | `org.cgiar.ciat.auth`, `LDAPAuthenticator`, `DBAuthenticator`, `doGetAuthorizationInfo` |

**Tests:** 29 files, 207 tests. **Nothing in `marlo-parent/pom.xml`, `configuration/`, the TRD or the root
guides was touched** — shared-file discipline held throughout.

## 5. Test Evidence Summary

`test-report.md`, second pass 2026-09-07. **73 matrix rows: 58 PASS · 10 GAP · 2 AUTOMATION_DEFERRED · 3 NOT
OBSERVED.** No `PRODUCT_BUG`.

Frontend unit testing was **stopped, not improvised** — MARLO has no JS runner, and choosing one is a TRD
stack decision. That is the recorded blind spot **D-5**.

E2E is manual: eleven-plus real corporate sign-ins, both coexistence directions on one account in one session,
and V-4, V-5, V-6 and V-7 each observed in a browser.

## 6. Validation Summary

`validation-report.md`, 2026-09-07, all eight phases. **PASS with WARN. 0 FAIL.**

The sharpest finding came from the cross-document figure check: the OQ-3 closure entry claimed **7**
`cognito.*` variables when there are **8**, because T15 added `cognito.identity.provider` the day after the
entry was written. **Corrected 2026-09-07** with a two-directional closure sweep.

## 7. Accepted Warnings and Follow-Ups

Reviewed and accepted by the user on 2026-09-07.

| Item | Disposition | Owner |
|---|---|---|
| **WARN-1 — Checkstyle** | **Accepted repository-level debt. Explicitly NOT PASS.** `CLAUDE.md:184` calls it a required hard gate; it is doubly inert — the plugin pairing throws, and `severity="warning"` repo-wide means it would enforce nothing if it ran | `marlo-parent/pom.xml` owner / Tech lead |
| **WARN-2 — §27.4 items 8a, 8b, 8c** | **NOT OBSERVED, held open.** Must not be converted to PASS on synthetic coverage | DevOps/IBD, Infrastructure, QA |
| **WARN-3 — OQ-3 key count** | Corrected | — (closed) |
| FN-001 UI clauses (7 + 1 hybrid) | Accepted — no JS framework to be introduced as part of this work | Tech lead |
| FN-006 S12 `getCgiarNickname` | Accepted — needs a directory and schema | `directory-abstraction` |
| SEC-004 deployment secret store | Accepted | IBD / DevOps |
| SEC-002 callback allowlist | Accepted — inspect for wildcards and record | IBD / DevOps |

**Independence limitation, carried verbatim:** `author ≠ auditor` holds for the code — every task was
implemented on `sonnet` and independently audited. **It does not hold for four artefacts written by the Leader
who also wrote the validation:** the FN-002 S5 and MIG-001 amendments, the three T17 javadoc corrections, the
Category A reclassification, and `test-report.md` itself.

## 8. Historical Notes

**Seven live-environment findings, and only one was a code defect.**

| Finding | What it actually was |
|---|---|
| V-1 `cognito.domain` | A configuration contract with key **names** recorded and **formats** not |
| **V-2** stale Shiro session | **The one genuine code defect.** It slipped past two audits because the test double simulated the symptom instead of the mechanism |
| U-3 `users.username` | A requirement built on a premise nobody could verify without a real token |
| V-4 return URL | A pre-existing `.do` heuristic meeting its first single-use URL |
| V-5, V-7 | Refusals rendering in place, leaving the authorization code in the address bar |
| V-6 | Rejection messages computed and discarded — the login view renders no server-side error at all |

**The dominant defect shape, nine occurrences:** *correct in isolation, dead through the real framework,
certified green by a double gentler than production.* The last instance was found at validation time, not by
the suite: two token-exchange doubles discarded all three arguments, so nothing proved the PKCE verifier ever
reached the exchange.

**Nine tasks carry a FAIL verdict** in the execution trail and were reworked. Six implementers correctly
corrected instructions they were given; five of those instructions were the Leader's.

**Three corrections were applied to the instances a finding cited and not swept** — T18's FN-001 preamble, the
T15/T16 task ledger, and the OQ-3 key count. Each was caught later by a different mechanism.
