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

**PASS on execution. Coverage: 58 clauses proven, 15 open with named owners.**

```
Tests run: 207, Failures: 0, Errors: 0, Skipped: 0   BUILD SUCCESS
```

**No `PRODUCT_BUG` was found**, in either pass. Every finding is a coverage gap or a deferred automation, not
a broken implementation.

**16 requirements · 18 scenarios · 13 `AND IT MUST` · 4 `AND IT MUST NOT` · 11 `BUT` clauses · 73 matrix rows.**

| Grade | Count | Meaning |
|---|---|---|
| **PASS** | 58 | A named test method proves the clause |
| **GAP** | 10 | No automated evidence; manual or inspection only |
| **AUTOMATION_DEFERRED** | 2 | Automatable, deliberately not automated |
| **NOT OBSERVED** | 3 | Requires the dedicated environment (§27.4 item 8) |

> **These counts are derived from the matrix in §7, not typed.** The first issue of this report carried
> hand-counted numbers — 43 / 12 / 4 / 3 against an actual 52 / 16 / 2 / 3 — written by eye from a table
> sitting directly above them. Corrected 2026-09-07 and now computed.

**Second pass, 2026-09-07.** Five coverage findings were closed by authorised test work (201 → **207** tests,
+6). Two requirements were **corrected rather than obeyed**: no production code was changed to make an
obsolete clause true. §9 records what is accepted and what still blocks nothing.

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
| FN-002 | S4 session carries `SESSION_USER`, custom parameters, colour | I | `#validRoundTripSendsThePkceVerifierAndPopulatesTheWholeSession` — five assertions incl. the **inactive** parameter's absence | **PASS** (closed 2026-09-07) |
| FN-002 | S4 `last_login` updated | I · M | double, plus the database at §27.4 item 4 | **PASS** |
| FN-002 | S4 **AND IT MUST** resolve to the pre-existing row | U | `#differentCasingStillResolvesTheSameRow` — calls the **real** `normalizeEmail` on both sides | **PASS** |
| FN-002 | S4 **BUT NOT** create or auto-provision a row | U | `#unknownClaimIsRefusedAndCreatesNoRow` | **PASS** |
| FN-002 | S5 `invalidUserCrp` shown | I | `#gate4MembershipFailureRouteARedirectsToLoginDo` | **PASS** |
| FN-002 | S5 session cleared, subject logged out | I | same, plus `#postActionAccessOnTheNonMemberBranchDoesNotThrow` | **PASS** |
| FN-002 | S5 **AND IT MUST** apply the same mechanism as the local case | I · U | `#gate4MembershipFailureRouteARedirectsToLoginDo` (key + session emptied); `LoginActionFinishLoginTest` for the local tail | **PASS** — clause **re-amended 2026-09-07**; presentation diverges by design (V-5/V-7) |
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
| FN-007 | S13 session cleared, cached authorization invalidated | I | `LoginActionLogoutTest#logoutClearsTheShiroSessionAndInvalidatesTheCachedAuthorizationInfo` — cache proven by **counting** lookups: 1 → 1 → logout → 2 | **PASS** (closed 2026-09-07) |
| FN-007 | S13 **AND IT MUST NOT** let a later page load restore the session | I | `LoginActionLogoutTest#afterLogoutALaterRequestFindsNoAuthenticatedSubject` — rebuilds a `Subject` from the session id, with a **positive control** before logout | **PASS** (closed 2026-09-07) |
| SEC-001 | S14 signature, `iss`, `aud`, `exp` | U | six named methods | **PASS** |
| SEC-001 | S14 **AND IT MUST** verify `nonce` | U · I | `#missingOrWrongNonceIsRejected`, `#aBlankExpectedNonceRejects` | **PASS** |
| SEC-001 | S14 **BUT NOT** derive identity from an unverified token | U | `#unsignedTokenIsRejected`, `#anUnconfiguredValidatorConstructsButAcceptsNothing` | **PASS** |
| SEC-001 | S14 **AND IT MUST** reject unsigned / wrongly signed / **expired** / other `aud` / other `iss` / replayed `nonce`, with no session | U · I | one method per case, plus both sides of the leeway | **PASS — synthetic** |
| SEC-001 | **8a — expiry leeway with a real token and a real clock** | M | — | **NOT OBSERVED** |
| SEC-001 | **8b — NTP clock synchronisation** | M | — | **NOT OBSERVED** |
| SEC-001 | **8c — real JWKS rotation** | M | — | **NOT OBSERVED** |
| SEC-002 | unguessable `state` bound to the session | U · I | `#stateNonceAndVerifierAreEachUnguessableAcrossOneHundredInvocations`, plus replay and mismatch tests | **PASS** |
| SEC-002 | **the flow MUST use PKCE** | I | `#validRoundTripSendsThePkceVerifierAndPopulatesTheWholeSession` — the verifier is **read back out of the session** and compared, plus `redirect_uri`, `authorizationCode` and `exchangeCallCount == 1` | **PASS** (closed 2026-09-07) |
| SEC-002 | exact-match callback allowlist, no wildcards | — | Cognito app-client configuration | **AUTOMATION_DEFERRED** — finding 5 |
| SEC-002 | a code **MUST NOT** be accepted twice | I | `#replayingAConsumedStateIsRefusedOnTheMissingEntry` — read-and-delete makes replay unreachable | **PASS by proxy** |
| SEC-003 | a new session id is issued, the pre-auth session is not promoted | I | five methods plus `ShiroRequestSessionCacheResetterTest` (6) | **PASS** |
| SEC-004 | configuration lives in `marlo-<profile>.properties` | U | `APConfigCognitoDefaultsTest` (3); guards that the strip found exactly 8 keys | **PASS** (partial) |
| SEC-004 | no pool / client / secret / domain literal in any `.java` | U | `CognitoCredentialLiteralScanTest` (3) — **string literals only**, 3458 files across 3 roots, four self-guards | **PASS** (closed 2026-09-07) |
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
| MIG-001 | S17 **AND IT MUST NOT** require a code change, build or redeploy | M | — | **AUTOMATION_DEFERRED** — clause **amended 2026-09-07** to name the route that satisfies it; D-4 keeps it unprovable by test |
| MIG-001 | S17 **BUT NOT** affect any other Global Unit | U | `#mixedFlagsProduceTwoDistinctValuesNotOneSharedScalar` | **PASS** |
| MIG-001 | S18 the path is decided by the unit selected in step 2 | U · M | `#aMixedMembershipUserIsJudgedByTheUnitTheySelected` ×2; §36 | **PASS** |
| MIG-001 | S18 **AND IT MUST** resolve the flag before authentication | U | every flag test runs with an empty session | **PASS** |
| OPS-001 | every attempt logs outcome, path and Global Unit | I | nine named methods | **PASS** |
| OPS-001 | logs **MUST NOT** carry tokens, codes, `state`, `nonce` or passwords | I | TRACE sweep plus two meta-guards | **PASS** |
| OPS-002 | LDAP stays functional during rollout | U | three methods | **PASS** |
| OPS-002 | `adauth` removal **MUST NOT** happen here | inspection | still declared in three POMs | **PASS** |

## 8. Remediation

### Closed 2026-09-07 — test coverage only, no production change

| # | Finding | How it was closed |
|---|---|---|
| **17** | **PKCE never asserted at the token exchange.** Both `RecordingTokenExchangeClient` doubles discarded all three arguments of `exchange(...)`, so a regression passing `null`, a stale verifier or the wrong `redirect_uri` would have left the whole suite green while breaking every CGIAR login | `#validRoundTripSendsThePkceVerifierAndPopulatesTheWholeSession`. The verifier is **read back out of the Shiro session** before the callback consumes it — not copied from a literal — and compared. `redirect_uri` is asserted twice, once against `APConfig` and once against the literal URL, so the first cannot pass by comparing an empty string to an empty string. Plus `authorizationCode` and `exchangeCallCount == 1`. **The recording fields default to null, so the assertion cannot be vacuous** |
| **9** | FN-002 S4 session attributes asserted by nothing | Five assertions in the same test: `SESSION_USER` id **and** email, the active custom parameter, the **inactive** one being absent, `CRP_VISIBLE_TOP_GULIST`, and the colour. The inactive parameter is the `BUT` half — an assertion that "a parameter arrived" would also pass against a loop with no `isActive` guard |
| **11** | FN-007 logout: session cleared, cached authorization invalidated | `LoginActionLogoutTest`, real `DefaultSecurityManager` and real `APCustomRealm`. The cache is proven by **counting** permission lookups: read gives 1, read again still 1 — the guard-the-guard, since with caching off the final assertion would pass while proving nothing — then logout, then read again gives **2** |
| **12** | FN-007 `AND IT MUST NOT` let a later page load restore the session | Models a later request the way Shiro own filter does: strip the thread, rebuild a `Subject` from the session id. Carries a **positive control** — the same rebuild *before* logout must find an authenticated subject, so "not authenticated" cannot pass for an unrelated reason |
| **13** | SEC-004 literal scan had no standing test | `CognitoCredentialLiteralScanTest` (3). **String literals only**, 3458 files across three roots, five patterns, and **four self-guards**: every root must contribute at least one file, at least 1000 files total, more than zero literals extracted, and each pattern must match a representative credential **and not** match the templates production legitimately builds URLs from |

### Corrected 2026-09-07 — the requirement was wrong, not the code

| # | Finding | Correction |
|---|---|---|
| **16** | FN-002 S5 required the Cognito refusal to *behave identically* to the local one. T21 and T22 deliberately made them differ | Clause re-amended to require the same **mechanism** — same key, same `getSession().clear()`, same `Subject.logout()`, same shared `finishLogin` tail — and to record that the **presentation** diverges by design. Rendering in place left the authorization code and `state` in the address bar and in history and poisoned the `Referer`: that is V-4, closed as V-5 and V-7. Decision Log 2026-09-07 |
| **15** | MIG-001 S17 required a flag flip to take effect *immediately* | Amended to name the route that satisfies it. `CustomParameterMySQLDAO:90` marks the lookup `setCacheable(true)` with a 3600 s TTL and Hibernate cannot see out-of-band writes, so direct SQL or a Flyway migration can take **up to an hour** (§24.3). Through `saveCustomParameter`, or after a restart, it is immediate. Decision Log 2026-09-07 |

**No production code was changed to make either obsolete requirement true.** T18 was FAILed twice for the
opposite mistake — an amendment left alive without the task that matched it.

### Discovered while closing finding 11, and recorded rather than fixed

`LoginAction.java:518-528` — the block whose comment reads `// Hack for cleaning cached authorization` — is
**inert**. It calls `clearCachedAuthorizationInfo(SecurityUtils.getSubject().getPrincipals())` *after*
`SecurityUtils.getSubject().logout()` has already nulled the principals, so `AuthorizingRealm` null guard
returns immediately. FN-007 is satisfied **entirely by Shiro**, which passes the still-live principals to
`onLogout` on every realm before clearing them.

**Not a `PRODUCT_BUG`** — the clause holds, and the code is pre-existing and untouched by this spec. But it
reads as the safety net and is not one, and anyone later simplifying Shiro logout path would assume it was
covering them. The new test asserts the **effect**, so it stays green whichever of the two does the work.

## 9. Accepted Gaps

Accepted by the user on 2026-09-07, each with a named owner and a concrete follow-up. **An accepted gap does
not block `/akili-validate`**; an unaccepted one covering a strict or negative clause would.

| # | Gap | Owner | Follow-up |
|---|---|---|---|
| **1–7 + hybrid** | Every FN-001 DOM clause: the CGIAR control, the password input being absent and non-focusable, steps 1 and 2 unchanged, the unit visible, the *External user* heading, the email preserved, the T&C checkbox not duplicated. The hybrid clause is **PASS on the server**, GAP on the UI | **Tech lead** | Choose a frontend test runner as a **TRD stack decision**, then a spec task to scaffold it. **No JS framework is to be introduced as part of this work.** Until then the evidence is the user manual validation, §36.1 |
| **10** | FN-006 S12 — `getCgiarNickname` is a stubbed seam, so "the LDAP path remains the only writer" is unproven | Sibling spec **`directory-abstraction`** | It owns that surface. Needs a directory and a schema |
| **14** | SEC-004 — the client secret comes from the deployment secret store | **IBD / DevOps** | A deployment property, not code. Confirm and record |
| **18** | SEC-002 — the Cognito app client **Allowed callback URLs** are unverified; nothing records anyone checking them for wildcards. **Do not credit `sameOriginOrNull` here** — its 17 tests protect the *return* URL, a different value | **IBD / DevOps** | Inspect the app-client configuration and record the result. A wildcard would let an authorization code be redirected to an attacker-controlled host |

### Not accepted — held open for the dedicated environment

| # | Item | Owner |
|---|---|---|
| **19** | **8a** — expiry leeway with a real token and a real clock. The `AND IT MUST` clause **is** covered synthetically, on both sides of the 60 s leeway; what is unobserved is real-token behaviour. Reachable by shortening the app-client token lifetime — **not** by shifting the server clock | DevOps/IBD + QA |
| **20** | **8b** — NTP clock synchronisation. A property of the environment, not of the application | Infrastructure |
| **21** | **8c** — real JWKS rotation. **Cannot be forced**; Cognito exposes no trigger. `#tokenSignedWithAnUntrustedKeyIsRejected` asserts `fetchCount >= 2` on an unknown `kid` — that is the re-fetch **mechanism**, not a rotation observation | DevOps/IBD |

**These three stay `NOT OBSERVED`. They are not to be converted to PASS on synthetic coverage**, and if no
rotation occurs within the project window, 8c is recorded as not observed rather than passed.

## 10. Environment note

`mvn checkstyle:check` **cannot run in this checkout**. It fails on every module, including ones this work
never touched, with `NoSuchMethodError: Checker.setClassloader` — `maven-checkstyle-plugin:2.9.1` against
`checkstyle:8.18`. This is the pre-existing **EB-1**.

Two consequences worth stating: `CLAUDE.md` lists it as a **required hard gate** and it is currently inert;
and it would not have covered this work anyway, because no `includeTestSourceDirectory` is configured, so it
checks `src/main/java` only. The three test files were verified by hand — no line over 120 characters, no
tabs, 2-space indent, GPL header on both new files. **Fixing the plugin means editing `marlo-parent/pom.xml`**,
a shared file governed by the dependency-baseline rule, and is out of scope here.