# Test Report — CHG-COGNITO-AUTH-001 (`auth-flow`)

## 1. Document Control

| Field | Value |
|---|---|
| Spec | `changes/migrate-ad-authentication-to-cognito/auth-flow` |
| Date | 2026-09-07 |
| Branch / HEAD | `staging-cognito-impl` · `72b9c3c1a9` · tree clean |
| Command | `mvn -pl marlo-web test` (`JAVA_HOME` = JDK 17) |
| Harness | AKILI Leader → Tester. **1 Tester spawned** (backend + integration). Frontend unit **stopped, no infrastructure**. E2E aggregated **inline** by the Leader |
| Skills | None beyond the Tester persona. **Deviation recorded:** `systematic-debugging` not assigned (no failures to diagnose); `ui-ux-pro-max` not assigned (the delegated suite touches no UI) |

## 2. Summary

**Overall: PASS on execution, FAIL on coverage.**

```
Tests run: 201, Failures: 0, Errors: 0, Skipped: 0   BUILD SUCCESS
```

**No `PRODUCT_BUG` was found.** Every finding is a coverage gap or a deferred automation, not a broken
implementation. 153 of the 201 tests are in this spec's scope; the rest belong to the sibling
`directory-abstraction` spec or predate both.

**16 requirements · 18 scenarios · 13 `AND IT MUST` · 4 `AND IT MUST NOT` · 11 `BUT` clauses.**

| Grade | Count | Meaning |
|---|---|---|
| **PASS** | 43 | A named test method proves the clause |
| **GAP** | 12 | No automated evidence; manual or inspection only |
| **AUTOMATION_DEFERRED** | 4 | Automatable, deliberately not automated yet |
| **NOT OBSERVED** | 3 | Requires the dedicated environment (§27.4 item 8) |

**Nothing in this report is classified as an accepted gap.** Section 9 explains why, and what it costs.

## 3. Backend Unit Tests

**PASS.** The strongest blocks are token validation and the credential-relay guards.

| Area | Tests | Note |
|---|---|---|
| `CognitoTokenValidatorTest` | 14 | Signature, `iss`, `aud`, `exp` on both sides of the 60 s leeway, `nonce`, access-token misuse, missing `iat`. `tokenClaimingATrustedKidButSignedByAnotherKeyIsRejected` is the only case reaching `signedJwt.verify()` — the file records that stubbing `hasValidSignature` left the other nine green |
| `CognitoLoginActionTest` | 32 | Six gates; `state` / `nonce` / `verifier` unguessable across 100 invocations; T19's return-URL closed set |
| `CognitoIdentityMappingTest` | 9 | Three gates; `unknownClaimIsRefusedAndCreatesNoRow` runs against an `ExplodingWriteUserManager` |
| `ValidateUserActionGuardTest` · `LoginActionCgiarGuardTest` | 13 | SEC-005 on **both** endpoints. `ExplodingLoginUserManager` makes the *absence* of the AD bind the assertion |
| `LogSanitizerTest`, `APConfigCognitoDefaultsTest`, others | 85 | |

## 4. Frontend Unit Tests

**BLOCKED — no test infrastructure. Not run, and not improvised.**

MARLO has no JS test runner, no config and no `test` script. Per this command's rules, choosing a runner is a
**TRD stack decision** implemented as a spec task, never a Tester's inner-loop choice. This is the recorded
blind spot **D-5**.

**Consequence:** every FN-001 DOM clause is a GAP with manual evidence only — see §7.

## 5. Integration Tests

**PASS.** JUnit, but genuinely multi-collaborator: real validator, real mapper and a real Shiro
`SecurityManager`, not doubles.

| Area | Note |
|---|---|
| `CognitoCallbackActionTest` (25) | Full round trip; session rotation; T20's nine `refuse()` branches; T21's three tail routes; T22's collapse. `fiveUnrelatedRejectionReasonsRedirectToTheByteIdenticalUrl` proves five unrelated reasons produce a **byte-identical** URL |
| `ShiroRequestSessionCacheResetterTest` (6) | `withoutTheHelperTheStaleSessionRejectsAWrite` is the mutation proof: without the helper the write throws Shiro's `InvalidSessionException` — V-2's exact shape |
| `CognitoLogHygieneTest` (15) | Secrets swept at **TRACE**, through the throwable cause chain, on the **rejection** path with real minted secrets. Two meta-tests prove the guards themselves bite |
| `CognitoUnloggedStackReachabilityTest` (1) | Drives the **real** `ValidCrpActionInterceptor` |

## 6. E2E Tests

**No automated E2E harness exists.** All E2E evidence is manual, performed by the user against the live Cognito
pool and correlated with server logs by the Leader.

| Validation | Evidence |
|---|---|
| Full federated sign-in, eleven-plus times | `execution.md` §30, §36, §42 |
| **Both coexistence directions, one account, one session** | §36.1 (`is_cgiar_user=1` → Cognito) and §36.2 (`=0` → local, with no Cognito action anywhere in the window) |
| V-4 — no second callback caused by the return URL | §42 |
| V-5 — a refused callback leaves the callback URL | §39, browser-observed `302 → /login.do` |
| V-7 — a gate-4 refusal redirects | §42, browser-observed `302 → /login.do` |
| V-6 — the rejection message displays and does not vanish | §45 |
| `agree_terms` and `last_login` written on the Cognito path | §27.4 item 4, read from the database |

## 7. Coverage & Traceability

Type: **U** unit · **I** integration · **M** manual/live · **—** none.

| Requirement | Scenario / clause | Type | Evidence | Result |
|---|---|---|---|---|
| FN-001 | S1 labelled CGIAR control, no external option | — | frontend | **GAP** (manual §36.1) |
| FN-001 | S1 password absent from DOM, not focusable, not submitted | — | frontend | **GAP** (§23.1, §33, §36.1) |
| FN-001 | S1 user never required to type a password | U | `#aMigratedCgiarAccountIsRefusedAndLoginIsNeverCalled` ×2 | **PASS** (server) / GAP (UI) |
| FN-001 | S1 **BUT NOT** change steps 1–2 | — | frontend | **GAP** (§36) |
| FN-001 | S1 **AND IT MUST** keep the unit visible | — | inspection | **GAP** |
| FN-001 | S2 "External user" heading, no CGIAR option | U | `CognitoI18nKeysTest` proves only that the key resolves | **GAP** |
| FN-001 | S2 email preserved | — | frontend | **GAP** |
| FN-001 | S2 unmodified `DBAuthenticator` | U | `#localUserProducesTheSameAuthenticationInfoAsBeforeTheChange` — written pre-change, so it is equivalence evidence | **PASS** |
| FN-001 | S2 **BUT NOT** contact Cognito or read its config | U · M | `#aLocalAccountIsUnchangedEvenWhenItsGlobalUnitHasTheFlagOn`; §36.2 | **PASS** |
| FN-001 | S3 T&C already accepted | U | `#anUnacceptedOrRevokedTermsRequestIsRefusedAndWritesNothing` — `false` **and** `null` | **PASS** |
| FN-001 | S3 checkbox not duplicated per method | — | inspection | **GAP** |
| FN-001 | S3 `agree_terms` written only after the callback | U · I | `#anAcceptedRequestMintsStateAndWritesNothing`, `#agreeTermsIsPersistedThroughSaveLastLoginNotSaveUser` | **PASS** |
| FN-002 | S4 signed in, scoped to the pre-selected unit | I | `#validRoundTripScopesTheSessionToTheGlobalUnitBoundAtMintTime` | **PASS** |
| FN-002 | S4 session carries `SESSION_USER`, custom parameters, colour | I | only `SESSION_CRP` is asserted | **GAP** — finding 2 |
| FN-002 | S4 `last_login` updated | I · M | double, plus the database at §27.4 item 4 | **PASS** |
| FN-002 | S4 **AND IT MUST** resolve to the pre-existing row | U | `#differentCasingStillResolvesTheSameRow` — calls the **real** `normalizeEmail` on both sides | **PASS** |
| FN-002 | S4 **BUT NOT** create or auto-provision a row | U | `#unknownClaimIsRefusedAndCreatesNoRow` | **PASS** |
| FN-002 | S5 `invalidUserCrp` shown | I | `#gate4MembershipFailureRouteARedirectsToLoginDo` | **PASS** |
| FN-002 | S5 session cleared, subject logged out | I | same, plus `#postActionAccessOnTheNonMemberBranchDoesNotThrow` | **PASS** |
| FN-002 | S5 **AND IT MUST** behave identically to the local case | — | — | **GAP + SPEC DRIFT** — finding 10 |
| FN-002 | S6 `USER_DISABLED` | U · I | `#inactiveAccountIsRefusedWithUserDisabled`; `#fiveUnrelatedRejectionReasonsRedirectToTheByteIdenticalUrl` | **PASS** |
| FN-002 | S6 **AND IT MUST** treat local `is_active` as authoritative | U | same — the token is fully valid, only the row is inactive | **PASS** |
| FN-003 | S7 session scoped to unit X | I | `#validRoundTripScopesTheSessionToTheGlobalUnitBoundAtMintTime` | **PASS** |
| FN-003 | S7 **BUT NOT** re-ask for a project | I | `#deepLinkFromStateLandsThereAndANullOneFallsThroughToTheDashboard` | **PASS** (server) |
| FN-003 | S7 **AND IT MUST** reject a value MARLO did not issue | I | `#tamperedInheritedGlobalUnitAndCrpFieldsAreIgnored` — binds hostile values through the **real** params interceptor | **PASS** |
| FN-004 | S8 lands on the original `.do` URL | I | `#deepLinkFromStateLandsThereAndANullOneFallsThroughToTheDashboard` | **PASS** |
| FN-004 | S8 **AND IT MUST** apply per-type routing | U · I | `#globalUnitTypeRoutingIsUnchangedForEveryType` (pre-extraction); `#t21PassThroughType2CentreDashboardUrlIsNotOverwritten` | **PASS** |
| FN-004 | S8 **BUT NOT** redirect to a `logout` URL | U | `#refererContainingLogoutFallsThroughToTypeRouting` | **PASS** |
| FN-005 | S9 login page with a message | I | `#theIdentityProviderReturnedAnErrorRefusesAndRedirects` | **PASS** |
| FN-005 | S9 **AND IT MUST** be an i18n key, never a literal | U | `#everyLoginI18nKeyResolvesInGlobalProperties` — 6 sources, per-source guard, rejects a blank value | **PASS** |
| FN-005 | S9 **BUT NOT** expose the provider error, code or token | I | `#callbackRejectionPathIsAlsoSweptForSecrets`; the 14-value `authError` sweep | **PASS** |
| FN-005 | S10 service-unavailable message | I · U | `#anUnreachableCognitoFailsClosedWithTheServiceUnavailableMessage`; `#aSuccessfulAuthorizeSetsNeitherPublicCategory` pins the negative | **PASS** |
| FN-005 | S10 **AND IT MUST** leave local login working | U · M | guard tests; §36.3 records four real `ConnectException`s with local login unaffected | **PASS** — isolation is structural, and the test says so |
| FN-006 | S11 `users.username` left exactly as it was | U | `#populatedUsernameIsPreservedByteForByte` — feeds the real U-3 claim shape | **PASS** |
| FN-006 | S11 still resolved by normalized email | U | `#differentCasingStillResolvesTheSameRow`; `UserMySQLDAOEmailNormalizationTest` (5) | **PASS** |
| FN-006 | S11 **AND IT MUST NOT** derive, prefix or strip | U | `#populatedUsernameIsPreservedByteForByte` | **PASS** |
| FN-006 | S12 null / blank stays null / blank | U | `#nullUsernameStaysNull`, `#blankUsernameStaysBlank` | **PASS** |
| FN-006 | S12 the LDAP path remains the only writer | U | `#cgiarUserStillRoutesToTheLdapBranch` — the nickname lookup is a stubbed seam | **GAP** — finding 8 |
| FN-006 | **AND IT MUST NOT** overwrite `users.email` | U | `#populatedUsernameIsPreservedByteForByte` | **PASS** |
| FN-007 | S13 session cleared, cached authorization invalidated | — | live only | **GAP** — finding 3 |
| FN-007 | S13 **AND IT MUST NOT** let a later page load restore the session | — | adjacent proofs only | **GAP** — finding 3 |
| SEC-001 | S14 signature, `iss`, `aud`, `exp` | U | six named methods | **PASS** |
| SEC-001 | S14 **AND IT MUST** verify `nonce` | U · I | `#missingOrWrongNonceIsRejected`, `#aBlankExpectedNonceRejects` | **PASS** |
| SEC-001 | S14 **BUT NOT** derive identity from an unverified token | U | `#unsignedTokenIsRejected`, `#anUnconfiguredValidatorConstructsButAcceptsNothing` | **PASS** |
| SEC-001 | S14 **AND IT MUST** reject unsigned / wrongly signed / **expired** / other `aud` / other `iss` / replayed `nonce`, with no session | U · I | one method per case, plus both sides of the leeway | **PASS — synthetic** |
| SEC-001 | **8a — expiry leeway with a real token and a real clock** | M | — | **NOT OBSERVED** |
| SEC-001 | **8b — NTP clock synchronisation** | M | — | **NOT OBSERVED** |
| SEC-001 | **8c — real JWKS rotation** | M | — | **NOT OBSERVED** |
| SEC-002 | unguessable `state` bound to the session | U · I | `#stateNonceAndVerifierAreEachUnguessableAcrossOneHundredInvocations`, plus replay and mismatch tests | **PASS** |
| SEC-002 | **the flow MUST use PKCE** | U | authorize side only | **AUTOMATION_DEFERRED** — finding 1 |
| SEC-002 | exact-match callback allowlist, no wildcards | — | Cognito app-client configuration | **AUTOMATION_DEFERRED** — finding 5 |
| SEC-002 | a code **MUST NOT** be accepted twice | I | `#replayingAConsumedStateIsRefusedOnTheMissingEntry` — read-and-delete makes replay unreachable | **PASS by proxy** |
| SEC-003 | a new session id is issued, the pre-auth session is not promoted | I | five methods plus `ShiroRequestSessionCacheResetterTest` (6) | **PASS** |
| SEC-004 | configuration lives in `marlo-<profile>.properties` | U | `APConfigCognitoDefaultsTest` (3); guards that the strip found exactly 8 keys | **PASS** (partial) |
| SEC-004 | no pool / client / secret / domain literal in any `.java` | — | manual grep, zero hits | **GAP** — finding 4 |
| SEC-004 | the client secret comes from the deployment's secret store | — | deployment property | **GAP** |
| SEC-005 | S15 refuse to authenticate against AD | U | both endpoints | **PASS** |
| SEC-005 | S15 **AND IT MUST** return the same generic failure shape | U | `#theRefusalIsByteIdenticalToAWrongPasswordRefusal` — the comparand is built by the endpoint's own real machinery | **PASS** |
| SEC-005 | S15 **BUT NOT** refuse a local account on the same endpoint | U | both endpoints | **PASS** |
| SEC-005 | a caller-named unit may narrow, never escape | U | `#aGlobalUnitIdTheAccountDoesNotBelongToCannotRoundTheGuard` | **PASS** |
| SEC-005 | **MUST NOT** log a CGIAR password | U · I | `CognitoLogHygieneTest` (15) and `LogSanitizerTest` (8) | **PASS** |
| SEC-006 | S16 refused, no session created | U | `#nonCgiarAccountIsRefusedOnGateTwoNotMembership` | **PASS** |
| SEC-006 | S16 **AND IT MUST** refuse on `is_cgiar_user`, not on membership | U | same — the mapper never reads `crp_users` | **PASS** |
| SEC-006 | S16 **BUT NOT** reveal the account exists under another mode | U · I | `#gateOneAndGateTwoRejectionsRenderTheIdenticalMessage`; `#fiveUnrelatedRejectionReasonsRedirectToTheByteIdenticalUrl` | **PASS** |
| MIG-001 | S17 flag off → LDAP on the next login | U | `#anActiveOverrideOfFalseBeatsACatalogDefaultOfTrue` — the only test that inverts the fixtures | **PASS** |
| MIG-001 | S17 **AND IT MUST NOT** require a code change, build or redeploy | M | — | **GAP + counter-observation** — finding 6 |
| MIG-001 | S17 **BUT NOT** affect any other Global Unit | U | `#mixedFlagsProduceTwoDistinctValuesNotOneSharedScalar` | **PASS** |
| MIG-001 | S18 the path is decided by the unit selected in step 2 | U · M | `#aMixedMembershipUserIsJudgedByTheUnitTheySelected` ×2; §36 | **PASS** |
| MIG-001 | S18 **AND IT MUST** resolve the flag before authentication | U | every flag test runs with an empty session | **PASS** |
| OPS-001 | every attempt logs outcome, path and Global Unit | I | nine named methods | **PASS** |
| OPS-001 | logs **MUST NOT** carry tokens, codes, `state`, `nonce` or passwords | I | TRACE sweep plus two meta-guards | **PASS** |
| OPS-002 | LDAP stays functional during rollout | U | three methods | **PASS** |
| OPS-002 | `adauth` removal **MUST NOT** happen here | inspection | still declared in three POMs | **PASS** |

## 8. Remediation

| # | Finding | Grade | What closes it | Owner |
|---|---|---|---|---|
| **1** | **The PKCE verifier and `redirect_uri` are never asserted at the token exchange.** Both `RecordingTokenExchangeClient` doubles discard all three arguments of `exchange(...)`. A regression passing `null` would leave all 201 tests green while breaking every CGIAR login | AUTOMATION_DEFERRED | Record the arguments in the double; assert the `codeVerifier` equals the pending verifier and the `redirectUri` equals the configured callback. Roughly ten lines, no new dependency | New task, this spec |
| **2** | FN-002 S4: `SESSION_USER`, the unit's custom parameters, `CRP_VISIBLE_TOP_GULIST` and the session colour are asserted by **nothing** | TEST_GAP | Four assertions inside the existing round-trip test | Same task |
| **3** | FN-007 logout has **no** automated coverage, on either clause | TEST_GAP | A Shiro-level test: logout clears the session, invalidates the cached `AuthorizationInfo`, and a later `isAuthenticated()` is false | Same task |
| **4** | SEC-004's D-6 literal scan has no standing test. The tree is clean today; a regression is ungated | TEST_GAP | A source-scanning test modelled on `CognitoI18nKeysTest`, which already reads `.java` from disk | Same task |
| **5** | SEC-002's exact-match callback allowlist is unverified, and nothing records anyone inspecting it for wildcards | AUTOMATION_DEFERRED | Inspect the app client's Allowed callback URLs and record the result | IBD / DevOps |
| **6** | MIG-001's "no redeploy" carries a **live counter-observation**: §24.3 records that enabling the flag by SQL or Flyway is invisible for up to an hour (3600 s query cache; Hibernate cannot see out-of-band writes) | AUTOMATION_DEFERRED | A HITL rollback exercise through `saveCustomParameter`, timed | IBD / DevOps + PMU |
| **7** | Every FN-001 UI clause — six across S1–S3 | AUTOMATION_DEFERRED | A frontend runner is a **TRD stack decision**, not an inner-loop improvisation | Tech lead |
| **8** | FN-006 S12: `getCgiarNickname` is a stubbed seam, so the write itself is unproven | TEST_GAP | Needs a directory and a schema | Sibling spec `directory-abstraction` |
| **9** | SEC-001 **8a / 8b / 8c** | NOT OBSERVED | The dedicated environment. **8c cannot be forced** — Cognito exposes no rotation trigger | DevOps / IBD, Infrastructure, QA |
| **10** | **SPEC DRIFT — FN-002 S5** still requires the Cognito membership failure to *"behave identically to the local-login case"*. T21 and T22 deliberately made them differ: local re-renders `INPUT`, Cognito redirects to `/login.do?authError=cognitoFailed`. The **mechanism** is identical — same key, same session teardown, same shared method — the **response** is deliberately better on the Cognito side. FN-001 and FN-006 were amended when they drifted; this clause was not | SPEC DRIFT | An amendment, or a Decision Log line | User + Leader |

## 9. Accepted Gaps

**None. Nothing in this report is classified as accepted.**

The user directed that the remaining environment validations **not** be pre-classified as accepted deferrals,
and that judgment is theirs to make after reading this report. Findings 1–10 are recorded as **open**, each
with a named owner.

**What that costs, stated plainly.** `/akili-validate` Phase 4 grades an `AUTOMATION_DEFERRED` or `GAP` entry
*without an accepted remediation* as **WARN — or FAIL when it covers a negative constraint or a strict
validation.** Findings 1, 5 and 10 touch `AND IT MUST` / `BUT MUST NOT` clauses, so as they stand they would
grade **FAIL**, and archive readiness requires that no unresolved FAIL remains.

Three routes are available, and the choice is the user's:

1. **Close finding 1 first** — roughly ten lines of test, and it is the sharpest of the three because a silent
   PKCE regression would break every CGIAR login with a fully green suite.
2. **Accept findings explicitly**, each with an owner and a follow-up, which downgrades them to accepted WARN.
3. **Run 8a and 8b in the dedicated environment first**, which removes finding 9 from the list entirely.
