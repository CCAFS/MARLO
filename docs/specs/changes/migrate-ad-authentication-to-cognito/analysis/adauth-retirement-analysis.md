# `adauth` Retirement — Full Analysis

**Analysis ID:** `CHG-COGNITO-ADAUTH-RETIREMENT-001`
**Revision:** **3** — realigned 2026-08-26 to the *functional-first* retirement principle (see *Revision note*)
**Scope:** the whole MARLO checkout — every path that reaches CGIAR Active Directory, for any reason
**Method:** static analysis of the working tree on branch `staging-cognito`, plus bytecode inspection of `adauth-5.7.jar`
**Companions:** [`impact-analysis.md`](./impact-analysis.md) · [`touchpoint-inventory.md`](./touchpoint-inventory.md)
**Related specs:** [`../proposal.md`](../proposal.md) · [`../family.md`](../family.md) · [`../auth-flow/requirements.md`](../auth-flow/requirements.md) · [`../auth-flow/design.md`](../auth-flow/design.md)

> **The question this document answers:** how can MARLO replace **both** the authentication and the
> corporate-user-lookup capabilities that `adauth` provides today, while continuing to create users in
> MARLO's own database — **including corporate users who have never logged into MARLO** — and then
> safely remove `adauth` only after the replacement has been stabilized?

---

## Terminology

The words *removal*, *retirement*, *replacement*, and *dependency* are used precisely throughout, and
mean different things. Mixing them is what produced the sequencing errors in earlier revisions.

| Term | Means | Reached at |
|---|---|---|
| **Functional retirement** | MARLO **never invokes `adauth` at runtime**. Zero authentication calls, zero lookups, zero `LDAPService` calls, zero `ADConexion` calls. The library is **still declared in Maven, still on the classpath, still in the WAR, and its legacy classes still compile** — deliberately, as a rollback safety net | **Step 4** → **Gate 1** |
| **Stabilization** | An agreed calendar period during which MARLO runs in that state and produces evidence that it works. **Mandatory, not optional.** `adauth` stays physically present the whole time | **Step 5** |
| **Physical retirement** | The Maven dependency, the JARs, the legacy classes, the obsolete constants, the service account, and the firewall rules are deleted | **Step 6** → **Gate 2** |
| **Replacement** | A capability that took over from an `adauth` capability. Two are needed: one for authentication, one for corporate user lookup. **They are different problems with different answers** | Steps 1 and 3 |
| **Dependency** | Qualified every time it appears: a **runtime dependency** (MARLO calls it), a **build dependency** (Maven declares it), or a **network dependency** (packets to `*.cgiarad.org`). Functional retirement kills the first. Physical retirement kills the other two | — |

> **The migration objective is functional retirement.** Physical retirement is the cleanup that
> follows it, not the goal that drives it.

### Evidence tags

| Tag | Meaning |
|---|---|
| **[V]** | **Verified from code** — a file, line, migration, or class file in this checkout says so |
| **[V-AWS]** | **Verified against AWS's documented service model** — *not* verifiable from this repository, because it contains zero Cognito code [V]. Stated separately so the two kinds of certainty are never confused |
| **[I]** | **Inference** — a conclusion drawn from [V] / [V-AWS] facts, stated as reasoning, not as fact |
| **[OQ]** | **Open Question** — cannot be determined here; needs a query, a runtime probe, or a person |

### Revision note

| Revision 2 said | Revision 3 says | Why |
|---|---|---|
| A "P0.5 security fix" deleted `ContactPersonAction`'s AD code and the four AD constants **before** the migration | **Split.** Eliminating the `ADConexion` construction is **functional retirement** (it is a live runtime call site) and moves into **Step 2**. Deleting the now-unused constants is **physical retirement** and moves to **Step 6**. Only the **credential value** is neutralized early, on a separate security track | A `new ADConexion(...)` on a reachable action is runtime usage, not dead weight. Classifying it as "early deletion" mislabelled the one thing that actually belongs in the functional phase |
| Microsoft Graph was marked **"Preferred"**, option 6 **"strongest fallback"** | **No option is selected.** All six are **candidates**, ranked only *conditionally* on an unanswered question | Revision 2's own text said the choice was blocked, then ranked the options as if it were not |
| Bucket B priced as one blended number, with §1.5 (35) contradicting §7.3 (41) | **B is split into B-common (option-independent) + a conditional per-option increment.** One coherent set of numbers | An open architectural question is not a reason to inflate an estimate. It is a reason to give a conditional one |
| Bucket C's 14 days sat in the headline total as if it were full-time work | **C is ~11 days of effort spread across an 8-week window at 0.5–1 day/week**, and is reported separately from FTE-equivalent effort | A stabilization window is calendar duration. Counting it as continuous engineering overstates cost and understates duration |
| Phases P0–P7 | **Steps 0–6**, matching the six-step narrative that is now the document's primary model | The phase list was correct but did not read as a strategy |
| Total 106 expected | **88–97 expected, option-dependent** | Lower, because the open-question inflation and the stabilization mispricing are both removed |

**Every verified repository finding from revisions 1 and 2 is preserved.** §2, §3, and §8–§10 are
substantively unchanged; the code did not change, only the conclusions and the sequencing drawn from it.

---

## 1. Executive Summary

### 1.1 The strategy in one picture

```
BEFORE                              TARGET — FUNCTIONAL (Gate 1)         AFTER (Gate 2)

MARLO                               MARLO                                MARLO
 └── adauth                          ├── Cognito ──► authentication       ├── Cognito
      ├── authentication             └── new mechanism ──► corporate      └── new mechanism
      └── corporate user lookup                          user lookup
                                                                          adauth ── GONE
                                     adauth ── 0 runtime usage                      · Maven
                                              · still in Maven                      · JARs
                                              · JARs still present                  · classes
                                              · classes still compile               · credentials
                                              · ROLLBACK AVAILABLE                  · firewall

           ──────────────────────── STABILIZATION WINDOW ────────────────────────►
```

**MARLO's `users` table remains the system of record under every option.** Cognito is an
authentication provider, not MARLO's user database, and nothing in this plan provisions users *into*
Cognito.

### 1.2 The six-step migration narrative

```
  1. Introduce Cognito authentication
          ↓
  2. Isolate all remaining adauth usage
          ↓
  3. Replace corporate-user lookup
          ↓
  4. Reach 0 runtime usage of adauth          ◄── GATE 1 — functional independence
          ↓
  5. Stabilize and validate
          ↓
  6. Physically remove adauth and legacy dependencies   ◄── GATE 2 — physical retirement
```

Detailed phases sit underneath this model in §6. **Nothing is physically deleted before Step 6.**

### 1.3 The two capabilities

**`adauth` provides MARLO with two capabilities, not one. Cognito replaces the first completely and
the second not at all.**

| | Capability A — **Authentication** | Capability B — **Corporate user lookup** |
|---|---|---|
| Today | `LDAPService.authenticateUser(email, password)` → `ADConexion` bind | `LDAPService.searchUserByEmail(email)` → `LDAPUser` |
| Signature | `authenticateCorporateUser()` | `findCorporateUserByEmail(email)` |
| Call sites | **2** [V] | **6** [V] |
| Purpose | Prove the person is who they claim | Discover a corporate person's `email`, `first_name`, `last_name`, `username`, so **MARLO can create a row in its own `users` table** |
| Trigger | The person is present, typing their password | The person is **absent**. An administrator typed their email |
| Replacement | **Amazon Cognito**, federated to the corporate IdP | **Undetermined — see §4** |
| Status | Designed, specified, ready to build | **Open architectural decision** |

**The distinction that matters: Capability A always involves the person. Capability B never does.**
Every Cognito mechanism — the authorization-code flow, ID-token claims, every Lambda trigger — is
built around an authentication event for a present user. Capability B has no authentication event to
attach to.

**The lookup requirement, stated exactly:**

> An administrator provides the email of a corporate user who **may not yet exist in MARLO**, and
> **may never have authenticated through Cognito**. MARLO must obtain that person's corporate
> identity information and **create the user in MARLO's own `users` table**.

This is not "provision the user into Cognito". It never becomes that under any option below.

### 1.4 Cognito cannot answer Capability B

Investigated in full in §4.1. The short answer:

| Question | Answer | Tag |
|---|---|---|
| Can `ListUsers` / `AdminGetUser` find a corporate user who has **never signed in**? | **No.** They read the User Pool's own directory. A federated identity gets a pool profile **only on first successful sign-in** | **[V-AWS]** |
| Can Cognito delegate or trigger a lookup **into** the external IdP? | **No.** Federation is an authentication-time redirect protocol (SAML/OIDC). Cognito exposes no directory-query surface over the IdP | **[V-AWS]** |
| Is there **any** AWS-supported mechanism to retrieve an external-IdP user without first provisioning them into the pool? | **No** | **[V-AWS]** |
| Can the **User Migration Lambda trigger** do it? | Not usefully. It fires on **native** `USER_PASSWORD_AUTH` / forgot-password, **not** on federated sign-in — and the Lambda itself would have to query LDAP, **relocating the AD dependency rather than removing it**. `family.md` already rejected this shape when resolving OQ-2 | **[V-AWS]** + **[V]** |
| Can claims (`email`, `given_name`, `family_name`, `preferred_username`) replace `LDAPUser`? | **Yes — completely,** but **only for the person who just authenticated** | **[V-AWS]** |
| Does MARLO have the IAM credentials the admin APIs need? | **No.** There is **no AWS SDK, no IAM principal, no SigV4, and no AWS credential mechanism anywhere in the repository** | **[V]** |

**Cognito federation solves authentication. It does not replace `searchUserByEmail()` for a
never-authenticated user.** That is the gap this programme has to close, and it is the reason
Capability B is treated as an open decision rather than a design.

### 1.5 No Capability B solution is selected

Six candidates are compared in §4.3 on the criteria that matter — never-logged-in users, a fresh
MARLO instance with an empty database, external dependencies, credentials, security, and migration
cost. **None is selected here, and none should be selected until the blocking questions are answered.**

| # | Candidate | Never-logged-in | Fresh/empty DB | Status |
|---|---|---|---|---|
| 1 | Cognito admin APIs | ❌ | ❌ | **Cannot satisfy the requirement alone** [V-AWS] |
| 2 | Corporate directory API (e.g. Microsoft Graph) | ✅ | ✅ | **Candidate — availability depends on OQ-3b.** No repository evidence [V] |
| 3 | CLARISA or another CGIAR service | ❓ | ✅ | **Candidate — cheapest if it exists.** No people endpoint appears in this repository [V] → **OQ-15** |
| 4 | New direct LDAP client, without `adauth` | ✅ | ✅ | **Candidate — but retains Active Directory.** Removes the library, not the dependency |
| 5 | Local `ad_user` cache | ❌ | ❌ | **Not a source.** Optional cache only — it fails both defining constraints |
| 6 | Invitation + just-in-time provisioning **into MARLO's `users`** | ✅ | ✅ | **Candidate — no new external dependency.** Costs a UX change → **OQ-20** |

**The decision is blocked on two questions this repository cannot answer** — OQ-3b (which corporate
IdP CGIAR runs, which determines whether a queryable directory API even exists) and OQ-15 (whether
CLARISA exposes a people endpoint). §4.4 gives the decision tree; it deliberately stops short of a
recommendation.

**What *is* decided:** `ad_user` is **not** the architecture. A claim-fed local mirror contains only
people who have already signed in, so it serves neither a never-logged-in user nor a fresh instance.
It survives only as an optional cache in front of whatever real source is chosen.

### 1.6 Effort — separated by responsibility

| Bucket | Best | **Expected** | Worst | Nature |
|---|---|---|---|---|
| **A — Cognito authentication** *(already planned)* | 21 | **35** | 60 | Engineering |
| **B — Functional replacement of remaining `adauth` capabilities** | 16 | **27 – 36** | 65 | Engineering — **conditional on the Capability B candidate** |
| **C — Stabilization** | 6 | **11** | 24 | **Part-time across an 8-week window**, not FTE |
| **D — Physical retirement** | 9 | **16** | 29 | Engineering |
| **TOTAL** | **52** | **89 – 98** | **178** | |
| **INCREMENTAL BEYOND A** *(B + C + D)* | **31** | **54 – 63** | **118** | |

> **Making MARLO functionally independent of `adauth` and then physically removing it costs 54–63
> business days beyond the Cognito authentication migration already planned** — the range being the
> difference between the cheapest and most expensive Capability B candidate.

**Elapsed calendar: ~21 weeks (~5 months)** with 2 engineers — **dominated by the 8-week stabilization
window, not by engineering.** Effort is *lower* than revision 2 (89–98 vs 106) because the
open-question inflation and the stabilization mispricing are both removed; calendar is unchanged,
because it is set by the window.

Per-option numbers are in §7.3 and §7.6. **Bucket B is not one number and should not be quoted as
one.**

### 1.7 Bottom line

| | |
|---|---|
| **Objective** | **Functional independence from `adauth`** — zero runtime usage. Physical deletion is the cleanup that follows |
| **Capability A** | Solved. Cognito + corporate IdP federation. The `auth-flow` spec is sound |
| **Capability B** | **Open decision.** Six candidates, choice conditional on OQ-3b and OQ-15 |
| **MARLO's user database** | **Unchanged.** `users` remains the system of record; nothing is provisioned into Cognito |
| **Files to change** | **26** |
| **Files to create** | **~11**, varying with the Capability B option |
| **Files to delete** | **6 Java files + 27 jars + 2 file-repo trees — all in Step 6, none before** |
| **Runtime AD call sites eliminated** | **8 of 8**, by end of Step 4 |
| **Config properties to remove** | **0** — there are none. AD config is hardcoded Java constants and defaults baked into the jar [V] |
| **Schema changes** | **0** required for the abstraction; **1 new table** only under candidate 6 |
| **Hard blockers** | **OQ-3** (will CGIAR federate) and **OQ-3b** (which IdP — decides Capability B) |

---

## 2. Current AD Dependency Map

*Unchanged from revision 1. This section is repository evidence, and the code did not change.*

### 2.1 The dependency itself

| Item | Value | Evidence |
|---|---|---|
| Coordinates | `org.cgiar.ciat.auth:adauth:5.7` | `marlo-parent/pom.xml:14`, `:299-303` [V] |
| Declared by | `marlo-data/pom.xml:26-30`, `marlo-web/pom.xml:75-78` | [V] |
| Resolved from | `file://${basedir}/src/main/resources/libs/` — a committed file repository | `marlo-data/pom.xml:199-204`, `marlo-web/pom.xml:635-639` [V] |
| Versions committed | **16** in `marlo-data` (1.1 → 5.7), **11** in `marlo-web` (1.1 → 2.2). **27 jars, 6.0 MB** | [V] |
| `marlo-web` resolution quirk | `marlo-web` declares version 5.7 but its own file repo stops at **2.2**. It resolves only because `marlo-data` builds first in the reactor and caches 5.7 into `~/.m2` | [V/I] |
| Packaged into the artifact? | **Yes.** `libs/` sits under `src/main/resources`, so `target/classes/libs/` contains it and the WAR ships all 27 jars | `marlo-web/target/classes/libs/` [V] |
| Upstream origin | `https://repos.ciat.cgiar.org/repository/maven-releases/`, published as `org.cgiar.ciat:ad-auth` — **different coordinates than MARLO declares**. The `adauth-5.7.pom` in the file repo is a hand-written re-coordination stub | embedded `pom.xml` inside the jar [V] |

### 2.2 The library's actual API surface — 3 public classes

Bytecode inspection of `adauth-5.7.jar`:

| Class | Members MARLO uses | Capability |
|---|---|---|
| `org.cgiar.ciat.auth.LDAPService` | `setInternalConnection(boolean)` | both |
| | `authenticateUser(String, String)` | **A** |
| | **`searchUserByEmail(String)`** | **B** |
| `org.cgiar.ciat.auth.ADConexion` | ctor `(user, pass, host, port)`, `getLogin()`, `getAuthenticationMessage()`, `closeContext()` | A |
| `org.cgiar.ciat.auth.LDAPUser` | `getFirstName()`, `getLastName()`, `getLogin()`, `getEmail()`, `getAttributes()` | **B** |
| *(internal)* `org.cgiar.ciat.params.Parameters` | Hardcodes the whole AD topology — see §2.5 | both |

**A 3-class, 4-method surface** [I] — and the entire Capability B problem is the semantics of one
method, `searchUserByEmail`.

> `WSMarlo.java:100-126` references `org.cgiar.ciat.abw.*` in comments and annotations. That is the
> **OCS web-service** artifact, unrelated to `adauth`. Any `grep org.cgiar.ciat` gate must exclude it. [V]

### 2.3 All 8 direct call sites

| # | File | Line | Method / member | Purpose | Dep. | **Capability** |
|---|---|---|---|---|---|---|
| 1 | `marlo-data/.../security/APCustomRealm.java` | `:287` (`new LDAPService()`), `:295` (`searchUserByEmail`) | `getCgiarNickname(User)` `:285` | Resolve `sAMAccountName` during login and **write it to the DB** (`:302`) | Direct | **A** (uses a B call) |
| 2 | `marlo-data/.../security/authentication/LDAPAuthenticator.java` | `:61` (`new LDAPService()`), `:53` (`authenticateUser`) | `authenticate(String, String)` `:54` | The credential bind | Direct | **A** |
| 3 | `marlo-web/.../action/BaseAction.java` | `:4798`, `:4806` | `getOutlookUser(String)` `:4797` | Shared directory helper; **returns `LDAPUser`** — the adauth type leaks into the public API of a 9,748-line base class | Direct | **B** |
| 4 | `marlo-web/.../action/json/global/SearchUserAction.java` | `:193`, `:202` | `execute()` `:74` | User-search picker; falls back to AD when the email is not in `users` and ends with `cgiar.org` | Direct | **B** |
| 5 | `marlo-web/.../action/center/json/global/ManageUsersAction.java` | `:249`, `:255` | `validateOutlookUser(String)` `:248` | Center user autofill | Direct | **B — unreachable** |
| 6 | `marlo-web/.../action/center/capdev/ContactPersonAction.java` | `:86`, `:93` | `searchADUser()` `:84` | **Constructs `LDAPService` and `ADConexion` and never uses them.** The real search is `adUsermanager.searchUsers()` at `:99` | Direct | **runtime waste — see §5.3** |
| 7 | `marlo-web/.../utils/searchUsersUtil.java` | `:14` | `main(String[])` `:8` | Developer scratch file. No GPL header, no package-mates, no callers | Direct | **unreachable** |
| 8 | `marlo-web/.../validation/superadmin/GuestUsersValidator.java` | `:37`, `:45` | `getOutlookUser(String)` `:36` — its **own copy** of #3 | AD presence relaxes the first/last-name requirement (`:56-61`, `:71-79`) | Direct | **B** |

**Note site #1.** `getCgiarNickname` is an *authentication-path* call that uses a *Capability B*
operation. It disappears with Capability A — the ID token supplies the login directly — which is why
Step 1 reduces the Capability B surface for free.

**Note site #6.** `searchContact.do` **is registered** in `struts-json.xml:1042` [V], so
`new ADConexion(...)` at `:93` executes on every hit. It is unused, but it is not dead at runtime —
which is why eliminating it belongs to **Step 2 (functional)**, not Step 6 (physical). See §5.3.

### 2.4 Indirect dependencies

| # | File | Line | Reaches AD via | Capability |
|---|---|---|---|---|
| 9 | `marlo-web/.../action/crp/admin/CrpUsersAction.java` | `:630` | `BaseAction.getOutlookUser()` | **B** + provisioning |
| 10 | `marlo-web/.../action/json/global/ManageUsersAction.java` | `:151` | `BaseAction.getOutlookUser()` | **B** + provisioning |
| 11 | `marlo-web/.../validation/superadmin/GuestUsersValidator.java` | `:55` | its own `getOutlookUser()` | **B** + validation |
| 12 | `marlo-data/.../MarloShiroConfiguration.java` | `:44-49` | `@Bean apCustomRealm(..., LDAPAuthenticator, ...)` — **the concrete type is in the bean signature** | A |
| 13 | `marlo-data/.../security/APCustomRealm.java` | `:83`, `:88`, `:93`, `:138` | `@Named("LDAP") Authenticator ldapAuthenticator` | A |
| 14 | `marlo-data/.../data/model/ADLoginMessages.java` | whole file (50 LOC) | The AD status-code vocabulary mapped from `ADConexion.getAuthenticationMessage()` | A |
| 15 | `marlo-web/.../action/home/LoginAction.java` | `:118-149` | 10-case `switch` over the AD status vocabulary | A |
| 16 | `marlo-web/.../action/json/global/ValidateUserAction.java` | `:102-133` | The same 10-case `switch` — duplicated | A |

**Type leakage.** `org.cgiar.ciat.auth.LDAPUser` appears in the **imports of 6 `marlo-web` classes**
[V] — including `CrpUsersAction:48` and `json/global/ManageUsersAction:24`, which never touch
`LDAPService` themselves. Removing the jar therefore breaks compilation in files that make no AD call.
**This is why Step 2 (isolation) must precede Step 6 (deletion), and why Step 2 is worth doing even
while `adauth` is still the implementation behind the abstraction.**

### 2.5 Credentials, hosts, and ports

**Committed credentials — pre-existing security finding, duplicated:** [V]

```java
public static final String GENERICUSER_AD     = "ldapuser";
public static final String GENERICPASSWORD_AD = "ldap2005";
public static final String HOSTNAME_AD        = "ciatroot1.ciat.cgiarad.org";
public static final String PORT_AD            = "3268";
```

| Location | Lines |
|---|---|
| `marlo-data/.../config/APConstants.java` | `:645-649` |
| `marlo-web/.../config/APConstants.java` | `:705-709` |

**All four constants have exactly one consumer: `ContactPersonAction:88-91`** [V].

> **Revision 3 sequencing.** Deleting these constants is *physical retirement* and belongs to
> **Step 6**. But a live service-account password committed in source is a **security exposure that
> is not part of the migration at all** — it is true today and would be true if the migration were
> cancelled. It is therefore handled on a **separate security track** (§5.1) that neutralizes the
> credential *value* without deleting code, and it is deliberately excluded from the step sequence so
> that it neither delays nor is delayed by the migration.

**The AD topology is compiled into the jar, not configured.** Constant-pool extraction from
`org/cgiar/ciat/params/Parameters.class`: [V]

| Item | Value |
|---|---|
| Domain / Base DN | `CGIARAD.ORG` / `DC=CGIARAD,DC=ORG` |
| Internal-scope controllers | `ciatroot4.CGIARAD.ORG`, `ciatroot5.CGIARAD.ORG` |
| External-scope controllers | `azcgccroot1.cgiarad.org`, `azcgccroot4.cgiarad.org` |
| Primary hosts | `ciatroot4.CGIARAD.ORG`, `azcgccroot1.CGIARAD.ORG` |
| **Ports** | **`3268`** (Global Catalog, **plaintext**) and **`636`** (LDAPS) |
| Transport | JNDI — `com.sun.jndi.ldap.LdapCtxFactory`, `ldap://` and `ldaps://` |
| Attributes read | `mail`, `givenName`, `sn`, `initials`, `mailNickname`, `sAMAccountName`, `userAccountControl` |

Scope is selected by `service.setInternalConnection(!config.isProduction())` — present identically at
all 5 live `LDAPService` sites [V]. **Production uses the external (`azcgcc*`) controllers.** [I]

**These five hostnames and two ports are the complete network dependency**, and they are what Step 5
must prove is unused before Step 6 closes it.

### 2.6 Configuration properties — there are none

**Verified:** `APConfig` (`marlo-utils`) declares 63 `@Value` fields; **zero** are AD or LDAP. No
`marlo-*.properties` file in the repository contains an LDAP key. No Struts XML, Docker file, CI
workflow, or shell script references AD. [V]

**Consequence:** there is no configuration switch that turns AD off today. The only way to stop MARLO
calling AD is to change code, and **the only way to prove it stopped is at the network and log
layer** (§9.4, §10.3). This is what makes Step 5 necessary rather than ceremonial — and it is why
Step 3 introduces `directory.source`, so that the Step 4 cutover *becomes* a configuration switch.

### 2.7 MARLO has no AWS-native integration today

Material to §4.1, because it prices the Cognito admin-API candidate honestly:

| Fact | Evidence |
|---|---|
| AWS SDK dependency in any of the 5 modules | **None** [V] |
| IAM credential, SigV4 signer, `AWSCredentialsProvider`, access-key config | **None anywhere** [V] |
| How MARLO reaches S3 / SQS today | An **HTTP + AMQP microservice façade** — `microservice.s3.url`, `microservice.queueUrl`, `microservice.apiKey`, Basic username/password (`MicroserviceReportAction:220-230`) [V] |
| Reusable HTTP client | `ExternalPostUtils` — **which installs a trust-all `X509TrustManager` and disables SNI** (`makeSecureRequest`) [V] |

Two consequences:

1. **Using Cognito's admin APIs would be MARLO's first AWS-native integration.** It needs the AWS SDK,
   an IAM principal, and a credential-delivery mechanism — **none of which exists**, and none of which
   is in the supplied configuration set.
2. **`ExternalPostUtils` must not be reused for any identity lookup.** A trust-all TLS client fetching
   corporate directory data is a downgrade, not a reuse. Whichever Capability B candidate is chosen,
   it needs a properly validating HTTP client. Priced into §7's bucket B.

### 2.8 Test coverage of everything above

| Fact | Value | Evidence |
|---|---|---|
| Test classes in the entire repository | **3** | `ProjectPartnerTest`, `ProjectPageItemTest`, `URLShortenerTest` [V] |
| Test roots in `marlo-data` / `marlo-core` / `marlo-utils` | **none** | [V] |
| Tests covering authentication or directory lookup | **0** | [V] |
| Mocking framework | **none** — JUnit 4.13.2 + `struts2-junit-plugin` only. No Mockito, PowerMock, Hamcrest, AssertJ, WireMock | `marlo-parent/pom.xml` [V] |
| Frontend / E2E harness | **none** | [V] |

`mvn test` passing proves nothing about this change.

---

## 3. Functional Use Cases

### 3.1 The two capabilities, separated

```
CAPABILITY A — Authentication                    CAPABILITY B — Corporate user lookup
authenticateCorporateUser()                      findCorporateUserByEmail(email)

  The person is PRESENT                            The person is ABSENT
  They typed their password                        An admin typed their email
         │                                                │
         ▼                                                ▼
  adauth: LDAPService                              adauth: LDAPService
          .authenticateUser(email, pw)                     .searchUserByEmail(email)
         │                                                │
         ▼                                                ▼
  ADConexion bind → CGIARAD.ORG                    LDAPUser {firstName, lastName,
         │                                                    login, email}
         ▼                                                │
  Session established                                     ▼
                                                   MARLO CREATES A users ROW
                                                   first_name · last_name ·
                                                   username · is_cgiar_user
                                                   ── in MARLO's OWN database ──

  ──► REPLACED BY: Cognito + corporate IdP         ──► REPLACEMENT UNDETERMINED — §4
```

**They must not be merged.** Revision 1's error was treating "the AD dependency" as one thing. The
consequence was proposing a claim-fed mirror — a Capability A artifact — as the answer to Capability B.

### 3.2 Capability A — Authentication

`APCustomRealm:136-142` → `getCgiarNickname()` (a **B** call) → `ldapAuthenticator.authenticate()` (an
**A** call). **Two AD round trips per login** [V].

Reached from **four** surfaces [V]:

| Surface | Path | Notes |
|---|---|---|
| `login.do` | `LoginAction.login()` → `userManager.login()` → `Subject.login()` | |
| `validateUser.do` | `ValidateUserAction:71` → `userManager.login()` | **No interceptor stack** — package `homeJson` extends `json-default` |
| `/api/**` | Shiro `authcBasic` → same realm | `MarloShiroConfiguration:113` |
| `/swagger/*.html` | `ClarisaPublicAccesFilter:79-82` → config-supplied service account | Registered at `WebAppInitializer:126` |

A local login authenticates **twice** — `validateUser.do` establishes the Shiro session at
`UserManagerImp:138`, then `login.do` repeats it [V].

**A hard precondition, verified:** `APCustomRealm:134` is `if (user != null) { … } return null;` — a
null return produces `UnknownAccountException`, caught at `UserManagerImp:147` [V]. **A `users` row
must exist before anyone can log in.** Any just-in-time provisioning design must create that row in
the Cognito callback *before* `Subject.login()`. This is the constraint that prices §4.3 candidate 6.

### 3.3 Capability B — Corporate user lookup

**"Does this email belong to a corporate person, and what are their name and login?"**

| Consumer | Trigger | Live? |
|---|---|---|
| `json/global/ManageUsersAction.create()` → `createUser.do` | The shared create-user popup — `usersPopup.ftl` imported by **15 FTL pages**, `usersManagement.js` loaded by **20+** | **Yes** [V] |
| `CrpUsersAction.save()` → `{crp}/crpUsers` | CRP-admin guest-user creation | **Yes** [V] |
| `GuestUsersValidator.validate()` | The validator half of the same save | **Yes** [V] |
| `SearchUserAction.execute()` | No XML registration, no frontend caller | **Probably not** [I] — OQ-12 |
| `center/.../ManageUsersAction.validateOutlookUser()` | No XML registration anywhere | **No** [V] |
| `ContactPersonAction.searchADUser()` → `searchContact.do` | **Registered** in `struts-json.xml:1042`; zero frontend callers; the AD objects it builds are never used | **Constructs AD objects at runtime** [V] |

**Three live lookup consumers, two of them on the same save path.** The surface is small; the
consequence is not.

### 3.4 What Capability B actually decides

`adauth` is the authority on three things the database cannot regenerate. [V]

| What it decides | Where | Why it matters after removal |
|---|---|---|
| **Is this person corporate?** → `users.is_cgiar_user` | `CrpUsersAction:630-650`, `json/global/ManageUsersAction:151-163` | The flag decides the auth path, **and** whether MARLO generates and emails a local password (`SendEmails:284`, 6 more sites) |
| **What is their name?** → `first_name`, `last_name` | Same sites | `crpUsers.js:274-283` **hides** those inputs for `@cgiar.org` addresses, expecting the lookup to fill them. Remove it with no replacement and corporate guest users are created with **null names** |
| **What is their login?** → `users.username` | `APCustomRealm:300-304`, 3 provisioning sites | Sent to **two external systems**: CLARISA `POST /api/partner-requests/create` as `externalUserName` (`PartnersSaveAction:553`) and the QA token service (`QAReportsAction:59`). The column is `unique="true"` (`Users.hbm.xml:19`) |

**Neither `impact-analysis.md`, `touchpoint-inventory.md`, nor the spec family records the CLARISA/QA
leak of `users.username`.** It is the reason "let the admin type the names" is not a complete answer:
nobody types a `sAMAccountName`.

### 3.5 Validation — a fourth use of the same call

`GuestUsersValidator.validate()` [V]:

```java
LDAPUser LDAPUser = this.getOutlookUser(user.getEmail());   // :55  — Capability B round trip
if (LDAPUser != null) { isCGIARUser = true; } else { isCGIARUser = false; }
this.validateGuestUsers(action, user, selectedGlobalUnitAcronym, isCGIARUser);   // :61
```

The parameter reassignment **is** live — `validateGuestUsers:71-79` suppresses the name-required
errors when `isCGIARUser` is true. Combined with `CrpUsersAction.save()`, **creating one guest user
performs two Capability B round trips**, plus two more (one A, one B) on that user's first login. [I]

### 3.6 `is_cgiar_user` — the flag stays, its source changes

The flag is read in **~10 places unrelated to logging in** [V]:

| Consumer | Line | Behavior it drives |
|---|---|---|
| `UserManagerImp.saveUser` | `:173` | MD5-hash the password **only** for non-corporate users |
| `SendEmails` | `:284` | Generate a random password, or tell the user to use their Outlook password |
| `BaseAction` | `:698` | Set a password when activating a user |
| `CrpAdminManagmentAction` / `CrpPpaPartnersAction` / `CrpProgamRegionsAction` / `CrpSiteIntegrationAction` / `ClusterActivitiesAction` / `ProjectPartnerAction` | `:232` / `:319` / `:344` / `:222` / `:281` / `:798`, `:1008` | Same password/e-mail branch |
| `TipDinamicUrlGenerationAction` | `:62-70` | TIP deep-link construction |

**The flag stays. Only its source of truth changes.** Every Capability B candidate in §4.3 must be
able to establish it.

---

## 4. Proposed Replacement Architecture

### 4.1 Can Cognito serve Capability B? — the full re-evaluation

**A note on certainty.** This repository contains **zero Cognito code** [V], so nothing about Cognito
can be verified *from it*. The findings below are **[V-AWS]** — verified against AWS's documented
service model. Where a claim depends on CGIAR's specific configuration, it is **[OQ]**.

#### 4.1.1 What the Cognito APIs can actually provide

| API | Reads from | Finds a never-signed-in external-IdP user? |
|---|---|---|
| `AdminGetUser` | The **User Pool's own directory** | **No** [V-AWS] |
| `ListUsers` (incl. `filter="email = \"…\""`) | The **User Pool's own directory** | **No** [V-AWS] |
| `AdminLinkProviderForUser` | Links a native profile to an external identity — **requires you to already know the attributes** | **No** — circular [V-AWS] |
| `AdminCreateUser` | **Writes** a profile. This is *provisioning into Cognito*, which is **explicitly not the objective** | n/a [V-AWS] |
| `GetUser` / `userInfo` | The **access token** of a signed-in user | **No** — requires a live session [V-AWS] |

**The decisive mechanic:** in a user pool with an external SAML/OIDC IdP, a user profile is created in
the pool **at the moment of the first successful federated sign-in**, not before. Until then the
person exists only in the corporate directory, and **no Cognito API can see them.** [V-AWS]

#### 4.1.2 Can Cognito delegate or trigger a lookup into the IdP?

**No.** [V-AWS] Federation is an authentication-time redirect protocol. Cognito speaks SAML and OIDC
to the IdP — protocols that carry an *assertion about an authenticating subject*. Neither carries a
directory query, and Cognito exposes no API that would issue one.

The Lambda triggers, explicitly:

| Trigger | Fires when | Usable for `findCorporateUserByEmail`? |
|---|---|---|
| `PreSignUp` | During sign-up / first federation | **No** — an authentication event for a present user [V-AWS] |
| `PreAuthentication` / `PostAuthentication` | During sign-in | **No** — same [V-AWS] |
| `PreTokenGeneration` | While minting tokens | **No** — same [V-AWS] |
| **`UserMigration`** | On `USER_PASSWORD_AUTH` or forgot-password **for a user not found in the pool** | **See below** |

**The User Migration trigger is the only one that can pull an unknown user's attributes from an
external source — and it is not a route to AD independence:**

1. It fires on **native username/password authentication**, not on federated sign-in [V-AWS]. Option A
   (federation) is the architecture already chosen, so the trigger would never fire.
2. It is still an **authentication** event, requiring the user's password. Capability B has no
   password — the person is not there.
3. **The Lambda itself would have to query LDAP.** That moves the AD dependency from MARLO's JVM into
   an AWS Lambda. The service account, the firewall rule, and the LDAP protocol all survive; only the
   *location* changes.
4. `family.md` already rejected exactly this shape when resolving OQ-2: *"Option B would have required
   abandoning federation and running a User Migration Lambda against LDAP with no defined removal
   date."* [V]

#### 4.1.3 Is there any AWS-supported mechanism to retrieve an external-IdP user without provisioning?

**No.** [V-AWS] Cognito's model is: the IdP is an authentication authority, not a queryable directory.
Reading the IdP's directory is done **against the IdP's own API**, not through Cognito — which is what
§4.3 candidate 2 proposes, and why that candidate's availability depends on which IdP CGIAR runs
rather than on anything about Cognito.

#### 4.1.4 Can claims replace `LDAPUser`?

**Yes, completely — for the authenticating user.** [V-AWS]

| `LDAPUser` member | Claim | Note |
|---|---|---|
| `getEmail()` | `email` | Standard OIDC |
| `getFirstName()` | `given_name` | Standard OIDC |
| `getLastName()` | `family_name` | Standard OIDC |
| `getLogin()` (`sAMAccountName`) | `preferred_username`, or a custom mapped claim | **Requires deliberate attribute mapping** at the IdP and in the Cognito provider config — it is not automatic [V-AWS] |
| — (evidence for `is_cgiar_user`) | the `identities` attribute / the issuer itself | A token issued via the corporate IdP **is** the evidence [I] |

**This is the single most useful Cognito finding.** It means:

- **Capability A is fully solved**, and it also solves `getCgiarNickname` — call site #1 disappears,
  because the ID token supplies the login. [I]
- Any user who signs in **populates their own MARLO record completely**, with better data than a
  mirror could hold. *(Their record lives in MARLO's `users` table. Nothing is written to Cognito.)*
- But **it still requires the person to sign in.** It cannot serve an administrator creating a record
  for someone who is not there — which is Capability B's entire premise.

**Action item regardless of which candidate wins:** the `preferred_username` / login claim mapping
must be requested from CGIAR IT **when federation is configured**, not later. Retrofitting an
attribute mapping after users have signed in means their profiles carry a null login until they sign
in again. → **OQ-18**.

#### 4.1.5 IAM requirements, if the admin APIs are used anyway

| Requirement | MARLO's current state |
|---|---|
| `cognito-idp:ListUsers`, `cognito-idp:AdminGetUser` on `arn:aws:cognito-idp:{region}:{acct}:userpool/{poolId}` | No IAM principal exists for MARLO [V] |
| The AWS SDK on the classpath | **No AWS SDK in any of the 5 modules** [V] |
| A credential chain — instance role, IRSA, or static keys | **No AWS credential mechanism anywhere** [V] |
| Credentials in the config set | The supplied set contains **no AWS credential** [V] |

**A disclosure, not a blocker:** the admin APIs are not "already available because we have Cognito".
They are a new AWS-native integration with its own IAM story — and since §4.1.1 shows they cannot
answer the requirement anyway, **the cost buys very little.** → **OQ-17**.

#### 4.1.6 Verdict on Cognito for Capability B

| Sub-capability | Cognito | Tag |
|---|---|---|
| Populate a corporate user's MARLO record **when they sign in** | **Fully** — claims are richer than `LDAPUser` | **[V-AWS]** |
| Look up a corporate user **who has signed in before** | **Yes**, via `ListUsers`, at the cost of a new AWS-native integration | **[V-AWS]** |
| Look up a corporate user **who has never signed in** | **No. No mechanism exists** | **[V-AWS]** |
| Serve a **fresh MARLO instance with an empty database** | **No** — an empty database implies an empty pool | **[I]** |

**Cognito is the answer to Capability A and half the answer to Capability B.** The missing half is the
half MARLO actually uses today.

### 4.2 Why `ad_user` is not the architecture

| Requirement | Claim-fed `ad_user` mirror |
|---|---|
| Serves a corporate user who has **never logged into MARLO** | **No** — the mirror is written *at login*. Never logged in ⇒ never mirrored |
| Serves a **fresh MARLO instance with an empty database** | **No** — a new instance starts with an empty `users` **and** an empty `ad_user` |
| Independent of historical local data | **No** — it *is* historical local data |

**A cache cannot be a source.** `ad_user` survives only as an optional accelerator in front of
whatever real source is chosen, and only if that source is slow, rate-limited, or expensive enough to
justify the staleness. If the chosen source is a fast HTTP API, the correct amount of caching is
probably none.

Its repository facts remain valid [V]: the table exists (2017), `AdUserManager` / `AdUserDAO` /
`AdUserMySQLDAO` / `AdUser.hbm.xml` are complete, **nothing in MARLO writes it**, and its only reader
is the capdev path. Its DAO also concatenates HQL (`:75`, `:97-98`) [V] — harmless only while it stays
effectively unreachable.

### 4.3 Capability B — the candidate comparison

Six candidates. **None is selected.**

#### Candidate 1 — Cognito User Pool admin APIs

| Attribute | Assessment |
|---|---|
| **Never logged into MARLO** | ❌ **No** — §4.1.1 [V-AWS] |
| **Fresh/empty MARLO instance** | ❌ **No** — an empty pool has nothing to list [I] |
| External dependencies | Cognito (already required for A) |
| Credentials / IAM | **New**: AWS SDK, IAM principal, `cognito-idp:ListUsers` + `AdminGetUser`, a credential chain. None exists [V] |
| Operational complexity | Medium — MARLO's first AWS-native integration |
| Security | Good — IAM-scoped, TLS, no service password |
| Migration complexity | Low code, medium infrastructure |
| **Assessment** | **Cannot satisfy the requirement alone.** At most a secondary link that avoids a round trip for already-known users — which the local `users` table already does, for free |

#### Candidate 2 — Corporate directory API (e.g. Microsoft Graph, if the IdP is Entra ID)

| Attribute | Assessment |
|---|---|
| **Never logged into MARLO** | ✅ **Yes** — it queries the corporate directory itself |
| **Fresh/empty MARLO instance** | ✅ **Yes** — no local state involved |
| External dependencies | Entra ID tenant + `graph.microsoft.com` egress |
| Credentials / IAM | An Entra **app registration**, client credentials, and the `User.Read.All` **application** permission — granted by CGIAR IT, with admin consent |
| Operational complexity | Low — one HTTPS call: `GET /users?$filter=mail eq '…'&$select=mail,givenName,surname,onPremisesSamAccountName` |
| Security | **Better than today** — TLS, OAuth2 client credentials, a rotatable secret, least-privilege scope. Replaces a plaintext-LDAP service account |
| Migration complexity | Low — a direct functional substitute for `searchUserByEmail` |
| **Assessment** | **Satisfies every stated requirement**, and `onPremisesSamAccountName` even preserves the exact `users.username` semantics CLARISA and QA depend on. **But it exists only if CGIAR runs Entra ID, and there is no repository evidence either way** [V] → **OQ-3b**. **Not selected** |

#### Candidate 3 — CLARISA or another existing CGIAR service

| Attribute | Assessment |
|---|---|
| **Never logged into MARLO** | ❓ Unknown — depends on CLARISA's data source |
| **Fresh/empty MARLO instance** | ✅ Yes, if the endpoint exists |
| External dependencies | CLARISA — **already a live dependency** [V] |
| Credentials / IAM | **Already present**: `clarisa.api.host` / `.username` / `.password`, used for `POST /api/partner-requests/create` with Basic auth (`PartnersSaveAction:537-564`) [V] |
| Operational complexity | **Lowest of all candidates** — the credential path, the host config, and the getters all exist today |
| Security | Basic auth over HTTPS — same posture as the existing call. **Must not reuse `ExternalPostUtils`** (trust-all TLS, §2.7) |
| Migration complexity | Low |
| **Assessment** | **The cheapest candidate if it exists — and the cheapest question to ask.** But **no people/user endpoint appears anywhere in this repository** [V] → **OQ-15**. **Not selected**; do not design against it until the CLARISA team confirms one exists |

#### Candidate 4 — A new direct LDAP client, without `adauth`

| Attribute | Assessment |
|---|---|
| **Never logged into MARLO** | ✅ Yes |
| **Fresh/empty MARLO instance** | ✅ Yes |
| External dependencies | **CGIAR Active Directory — unchanged** |
| Credentials / IAM | **The same AD service account**, still required |
| Operational complexity | Medium — Spring LDAP or JNDI, plus connection and failover handling `adauth` currently hides |
| Security | **No improvement.** Retains the service account, the firewall rule, ports 3268/636, and the JNDI surface |
| Migration complexity | Medium |
| **Assessment** | **Achieves functional retirement of the *library* while keeping the *directory*.** Gate 1 and Gate 2 would both pass on their letter, and MARLO would still depend on Active Directory. Defensible only as an explicitly time-boxed bridge with a removal date — otherwise it becomes permanent |

#### Candidate 5 — Local `ad_user` cache / mirror

| Attribute | Assessment |
|---|---|
| **Never logged into MARLO** | ❌ **No**, unless pre-populated from a real source |
| **Fresh/empty MARLO instance** | ❌ **No** — the disqualifying constraint |
| External dependencies | None |
| Credentials / IAM | None |
| Operational complexity | Low code; **high data-governance cost** — staleness, leavers, renames, retention of a personnel dataset |
| Security | A local copy of corporate personnel data is a new asset to protect. `AdUserMySQLDAO` also concatenates HQL [V] |
| Migration complexity | Low |
| **Assessment** | **Not a source under any circumstances.** Optional cache only. A one-time AD export into `ad_user` before the service account is decommissioned remains a useful **transitional safety net for the existing instance** — but it is not an architecture, and it does nothing for a new one |

#### Candidate 6 — Invitation + just-in-time provisioning into MARLO's `users`

The admin invites by **email**. No lookup happens. **The MARLO `users` row is created at the person's
first Cognito login, from claims** — richer than `LDAPUser`. Pending role assignments are applied at
that moment. **Nothing is written to Cognito; `users` remains the system of record.**

| Attribute | Assessment |
|---|---|
| **Never logged into MARLO** | ✅ **Yes** — precisely the case it is designed for |
| **Fresh/empty MARLO instance** | ✅ **Yes** — no local or external state required |
| External dependencies | **None beyond Cognito** — the only candidate that adds no new external system |
| Credentials / IAM | **None** |
| Operational complexity | Low at runtime; **the cost is product, not infrastructure** |
| Security | **Best of all candidates.** No directory read, no service account, no personnel data at rest, no new egress. Identity data arrives signed, from the IdP, about the person themselves |
| Migration complexity | **Highest.** Needs: a `user_invitation` table; role assignment deferred to first login; JIT row creation in the Cognito callback **before `Subject.login()`** (`APCustomRealm:134` requires a pre-existing row [V]); and admin-UI changes — the admin no longer sees the person's name at invitation time |
| **Assessment** | **The only candidate that removes the corporate-directory dependency rather than relocating it.** Its cost is a **UX change requiring business sign-off** → **OQ-20**. **Not selected** |

#### Comparison summary

| # | Candidate | Never-logged-in | Fresh DB | New external dep | New credentials | Security | Blocked on |
|---|---|---|---|---|---|---|---|
| 1 | Cognito admin APIs | ❌ | ❌ | — | AWS SDK + IAM | Good | — *(insufficient regardless)* |
| 2 | Corporate directory API | ✅ | ✅ | Entra ID | App registration | Better than today | **OQ-3b** |
| 3 | CLARISA people endpoint | ❓ | ✅ | *(already a dependency)* | **none — exist today** | Same as today | **OQ-15** |
| 4 | New LDAP client | ✅ | ✅ | **AD — unchanged** | **AD service account** | No improvement | — *(keeps AD)* |
| 5 | `ad_user` mirror | ❌ | ❌ | — | — | New data asset | — *(cache only)* |
| 6 | Invitation + JIT | ✅ | ✅ | **none** | **none** | Best | **OQ-20** |

### 4.4 The decision is a tree, and it is not resolved here

**Capability B's architecture cannot be selected from this repository.** What follows maps answers to
consequences; it does not pick a branch.

```
                    OQ-3b: which corporate IdP does CGIAR run?
                                    │
          ┌─────────────────────────┼──────────────────────────┐
          ▼                         ▼                          ▼
     Entra ID                  ADFS / on-prem AD          Not yet known
          │                         │                          │
          │                         │                          ▼
          │                         │                   ══ BLOCKED ══
          │                         │             Do not design Capability B.
          │                         │             Step 2 is still safe to build.
          ▼                         ▼
   ┌──────────────┐          ┌──────────────┐
   │ OQ-15 first: │          │ OQ-15 first: │      OQ-15 is worth asking on BOTH
   │ CLARISA?     │          │ CLARISA?     │      branches — it is the cheapest
   └──────┬───────┘          └──────┬───────┘      possible answer, because the
      yes │  no                 yes │  no          credentials already exist [V]
          │  │                      │  │
          ▼  ▼                      ▼  ▼
    Cand. 3   Cand. 2         Cand. 3  ┌─────────────────────────┐
                                       │ OQ-20: is the UX change │
                                       │ acceptable?             │
                                       └────────┬────────────────┘
                                            yes │ no
                                                ▼ ▼
                                        Cand. 6   Cand. 4
                                        (invite)  (LDAP bridge — keeps AD,
                                                   needs a removal date)
```

**In every branch, candidate 5 (`ad_user`) is at most a cache and candidate 1 at most an
optimization.** Ask **OQ-15 first, on both branches** — one email, and if the answer is yes it wins on
cost alone.

### 4.5 The abstraction is the same under every candidate

**This is what makes Step 2 safe to build before the decision is made** — and it is the highest-value
early work in the programme.

```
                        marlo-web consumers
   ManageUsersAction · CrpUsersAction · GuestUsersValidator · BaseAction
                                │
                                │  DirectoryPerson — never LDAPUser
                                ▼
              ┌──────────────────────────────────────────┐
              │  DirectoryService        (marlo-data)    │
              │  DirectoryPerson findByEmail(String)     │
              └──────────────────┬───────────────────────┘
                                 │  exactly one active implementation,
                                 │  chosen by  directory.source
      ┌──────────────┬───────────┴────────┬──────────────┬──────────────┐
      ▼              ▼                    ▼              ▼              ▼
 LdapDirectory   <Directory API>    ClarisaDirectory  Invitation   (cache decorator)
 Service         Service            Service           Directory    AdUserCaching
 [Step 2 —       [candidate 2]      [candidate 3]     Service      DirectoryService
  delegates to                                        [candidate   [candidate 5 —
  adauth,                                              6]           optional, wraps
  removed in                                                        any of the above]
  Step 6]
```

| Type | Contents |
|---|---|
| `DirectoryPerson` | `{ found, email, login, firstName, lastName, source }` — immutable |
| `DirectorySource` | `LDAP` \| `DIRECTORY_API` \| `CLARISA` \| `COGNITO_CLAIMS` \| `AD_MIRROR` \| `INVITATION` \| `NOT_FOUND` |

`source` is deliberately part of the contract: a caller — and a log line, and a support ticket — can
tell "the corporate directory confirmed this person" apart from "we assumed it from the email domain".

**Step 2 delivers this interface with `LdapDirectoryService` behind it and zero behavior change.** It
is worth building even if OQ-3b never resolves, because it removes `LDAPUser` from six `marlo-web`
classes [V] and turns an unbounded refactor into a one-file swap.

### 4.6 Cognito configuration mapping

Your standard set, mapped onto MARLO's `APConfig` conventions. **Every key uses the `${key:default}`
form** — `APConfig`'s 63 existing `@Value` fields use none, and
`PropertySourcesPlaceholderConfigurer` runs with `ignoreUnresolvablePlaceholders = false`, so a bare
placeholder makes the application **fail to boot** in every environment whose gitignored
`marlo-${profile}.properties` lacks the key. [V, `auth-flow/design.md` §9.3]

| Your variable | MARLO property | Used for | Capability |
|---|---|---|---|
| `COGNITO_CLIENT_ID` | `cognito.client.id` | `/oauth2/authorize`, `/oauth2/token`, ID-token `aud` check | **A** |
| `COGNITO_CLIENT_SECRET` | `cognito.client.secret` | Basic auth at the token endpoint (confidential client) | **A** |
| `COGNITO_LINK` | `cognito.domain` | Hosted-UI base for `/oauth2/authorize`, `/oauth2/token`, `/logout` | **A** |
| `COGNITO_REGION` | `cognito.region` | Issuer URL, JWKS URL | **A** |
| `COGNITO_REDIRECT_URI` | `cognito.redirect.uri` | `redirect_uri` — must equal `cognitoCallback.do` exactly | **A** |
| `COGNITO_POOL_ID` | `cognito.pool.id` | `iss` = `https://cognito-idp.{region}.amazonaws.com/{poolId}`; JWKS at `…/.well-known/jwks.json` | **A** |

**The supplied set is complete and sufficient for Capability A**, and contains nothing for Capability
B — correctly, because §4.1 shows Cognito has nothing to offer there.

Additions this analysis recommends:

| Property | Why | Capability |
|---|---|---|
| `cognito.logout.uri` | Cognito `/logout` needs its own registered sign-out URL; MARLO's session-invalidation path has no equivalent today | A |
| **`directory.source`** | `LDAP` \| `DIRECTORY_API` \| `CLARISA` \| `INVITATION`. **The Step 4 cutover and its rollback are this one property.** Without it, "stop using adauth" is a deploy instead of a config flip | **B** |
| `directory.cgiar.domains` | `cgiar.org` today; a property, not a constant, so an added corporate domain is a config change rather than a code change | B |
| `directory.cache.enabled` | Gates the optional `ad_user` decorator. Default **`false`** | B |
| *(candidate 2)* directory-API tenant / client id / client secret | The app registration | B |
| *(candidate 3)* — | **Nothing.** `clarisa.api.*` already exists [V] | B |

**Do not put `COGNITO_CLIENT_SECRET` — or any other secret — in `marlo-test.properties`.** That file is
tracked; real values belong in the gitignored `marlo-${profile}.properties` (hard rule 12). Add the
**keys with empty defaults** to the template, never the values. [V]

---

## 5. File-by-File Change Inventory

Grouped by the step that touches them.

> **Nothing is physically deleted before Step 6**, with one deliberate and separately-justified
> exception: §5.3's elimination of runtime AD object construction, which is *functional retirement*
> and is required for Gate 1.

### 5.1 Security track — outside the migration sequence

Not a migration step. It is true today, would be true if the migration were cancelled, and should
neither delay nor be delayed by anything below.

| # | File / lines | Change | Why it is not a step |
|---|---|---|---|
| S1 | `APConstants.java` (**data** `:645-649`, **web** `:705-709`) | **Neutralize the credential value only** — replace the `"ldapuser"` / `"ldap2005"` literals with `APConfig` getters reading `${ad.service.user:}` / `${ad.service.password:}`. **Do not delete the constants** — that is Step 6 | The exposure is a committed service-account password [V], not a migration artifact. The *code* stays until Step 6; only the *secret* leaves now |
| S2 | CGIAR IT ticket | **Rotate the AD service-account password** | The literal is in git history; deleting it from the working tree does not revoke it. Rotation is the only real remedy, and it has CGIAR IT lead time → start now |

**~1 day of engineering, plus a ticket.** No dependency on any other work.

### 5.2 Step 1 — Introduce Cognito authentication *(Capability A)*

*From the `auth-flow` spec; listed for completeness, not re-scoped here.*

| File | Change |
|---|---|
| `marlo-data/.../security/CognitoAuthenticationToken.java` | **New** — Shiro token carrying a validated assertion |
| `marlo-data/.../security/CognitoAssertion.java` | **New** — immutable validated identity |
| `marlo-data/.../security/CognitoTokenValidator.java` (+ `impl/`) | **New** — JWKS fetch, cache, full ID-token validation |
| `marlo-data/.../security/APCustomRealm.java` | **Modify** — `instanceof` guard **above** the cast at `:115`. Everything below preserved byte-for-byte |
| `marlo-data/.../MarloShiroConfiguration.java` | **Modify** — wire the validator into the hand-built realm (`:44-49`) |
| `marlo-web/.../action/home/CognitoLoginAction.java` | **New** — state, nonce, PKCE, authorize redirect |
| `marlo-web/.../action/home/CognitoCallbackAction.java` | **New** — code exchange, validation, gates, session rotation |
| `marlo-web/.../action/home/LoginAction.java` | **Modify** — extract `finishLogin(...)`; add the missing `Referer` null guard at `:290-295` |
| `marlo-web/.../action/json/global/CrpByUserEmailAction.java` | **Modify** — `cognitoEnabled` per `crps[]` entry; move the `user` map out of the loop (`:72`, `:89`) |
| `marlo-web/.../action/json/global/ValidateUserAction.java` | **Modify** — refuse corporate accounts on flag-enabled units |
| `marlo-utils/.../utils/APConfig.java` | **Modify** — Cognito getters, **all with `${key:default}`** |
| `marlo-parent/pom.xml` | **Modify** — add a JOSE/JWT library. **No downgrades. Do not remove `adauth`** |
| `marlo-web/.../config/APConstants.java` + `marlo-data/.../config/APConstants.java` | **Modify** — the specificity constant, identical value |
| `resources/struts-home.xml` | **Modify** — register both actions with a named stack |
| `resources/global.properties` | **Modify** — new keys **plus `login.error.invalidUserCrp`**, missing today [V] |
| `webapp/WEB-INF/global/pages/loginForm.ftl` | **Modify** — corporate step-3 block. **Verify on both host pages** — `login/login.ftl:17` **and** `error/401.ftl:19` [V] |
| `webapp/global/js/login/login.js` | **Modify** — compose the mode; **`.remove()`** the password node, never `.hide()` |
| `webapp/global/css/customLogin.css` | **Modify** — **not `global.css`** |
| `resources/config/marlo-test.properties` | **Modify** — Cognito keys, empty values |
| `database/migrations/V*__AddCognitoAuthSpecificity.sql` | **New** — 3 `parameters` rows, no `custom_parameters` |
| `global.properties` + `custom/*.properties` — **login copy only** | **Modify** — the moment the password field disappears, "(Your Outlook password)" is wrong on screen |

**Bonus:** call site #1 (`getCgiarNickname`) becomes unnecessary the moment the ID token carries the
login claim, because the claim supplies what the AD search supplied. [I]

### 5.3 Step 2 — Isolate all remaining `adauth` usage

Two kinds of work, both **functional**, both required for Gate 1.

**(a) Route every remaining call through one interface — zero behavior change**

| # | File | LOC | Change |
|---|---|---|---|
| N1 | `.../security/directory/DirectoryService.java` | **New** | `DirectoryPerson findByEmail(String email)` |
| N2 | `.../security/directory/DirectoryPerson.java` | **New** | Immutable `{found, email, login, firstName, lastName, source}` |
| N3 | `.../security/directory/DirectorySource.java` | **New** | The provenance enum |
| N4 | `.../security/directory/impl/LdapDirectoryService.java` | **New** | **Wraps today's `LDAPService`.** After this step it is the *only* Capability B adauth call site. Removed in Step 6 |
| C1 | `marlo-web/.../action/BaseAction.java` | 9,748 | `getOutlookUser(String) → LDAPUser` becomes `findCorporateUser(String) → DirectoryPerson`, delegating to the injected service. Drop imports `:103-104`. **Shared writer — serialize** |
| C2 | `marlo-web/.../action/crp/admin/CrpUsersAction.java` | 994 | `:630-658` consumes `DirectoryPerson`; drop import `:48` |
| C3 | `marlo-web/.../action/json/global/ManageUsersAction.java` | 285 | `:151-175` same substitution; drop import `:24` |
| C4 | `marlo-web/.../validation/superadmin/GuestUsersValidator.java` | 87 | Replace its private `getOutlookUser()` `:36-50` with the injected service at `:55`; drop imports `:23-24` |
| C5 | `marlo-web/.../action/json/global/SearchUserAction.java` | 276 | `:191-223` consumes `DirectoryPerson`; drop imports `:30-31` |
| C6 | `marlo-web/.../action/center/json/global/ManageUsersAction.java` | 265 | Unreachable [V], but it must still compile once `LDAPUser` is gone from `marlo-web`. Migrate it to `DirectoryService` — **do not delete it here** |

**(b) Eliminate runtime AD object construction that serves no purpose**

| # | File / lines | Change | Why this is Step 2, not Step 6 |
|---|---|---|---|
| E1 | `ContactPersonAction.java:86`, `:93` + imports `:24-25` | Remove the `new LDAPService()` and `new ADConexion(genericUser, genericPassword, hostName, port)` construction. The live search at `:99` (`adUsermanager.searchUsers()`) is untouched | **`searchContact.do` is registered** (`struts-json.xml:1042`) [V], so this constructs AD objects on every hit. It is a **runtime `ADConexion` call**, and Gate 1 requires zero of those. **Zero observable behavior change** — `adConection` is never read, and `getADFilter`'s only call is commented out at `:96-97` [V] |
| E2 | `ContactPersonAction.java:58-71` (`getADFilter`) | **Leave in place.** It builds a string and calls nothing | Dead code with no runtime effect → Step 6 |
| E3 | `searchUsersUtil.java` | **Leave in place.** A `main()` never invoked by the container | No runtime effect → Step 6 |

**Gate for Step 2:** `grep -rn "org.cgiar.ciat.auth" marlo-web/src` returns only
`searchUsersUtil.java` (unreachable) and `ContactPersonAction.java` if E2 keeps its imports;
`marlo-data` returns `LDAPAuthenticator` (Capability A) and `LdapDirectoryService` (Capability B).
**Every *reachable* Capability B path now goes through one interface.**

### 5.4 Step 3 — Replace corporate-user lookup *(shape depends on §4.4)*

| Candidate | New files |
|---|---|
| **2 — directory API** | `impl/<Api>DirectoryService.java`, a client-credentials token provider + cache, a **properly validating** HTTP client — **not `ExternalPostUtils`** (§2.7) |
| **3 — CLARISA** | `impl/ClarisaDirectoryService.java`, reusing `clarisa.api.*` config [V], same HTTP-client caveat |
| **4 — LDAP bridge** | `impl/SpringLdapDirectoryService.java` + connection config. **Must carry a removal date** |
| **6 — Invitation** | `impl/InvitationDirectoryService.java`; `UserInvitation` model + DAO + manager + `.hbm.xml`; a **new migration** for `user_invitation`; JIT provisioning in `CognitoCallbackAction` **before `Subject.login()`** (`APCustomRealm:134` [V]); admin-UI changes. **Creates rows in MARLO's `users`, not in Cognito** |
| **5 — cache** *(optional, any candidate)* | `impl/AdUserCachingDirectoryService.java` decorator; **HQL-injection fix in `AdUserMySQLDAO`** `:75`, `:97-98` [V]; an index on `ad_user.email` |

Common to every candidate:

| # | File | Purpose |
|---|---|---|
| N5 | `.../security/directory/UsernameAllocator.java` | Deterministic `users.username` synthesis with uniqueness retry — needed wherever the source does not supply a corporate login. **Not needed under candidate 2** if the API returns `onPremisesSamAccountName` |
| N6 | `.../security/directory/CorporateDomainPolicy.java` | Establishes `is_cgiar_user` from `directory.cgiar.domains` when the source is silent |

**Frontend and provisioning changes** (required under every candidate):

| File | Change | Why |
|---|---|---|
| `webapp/crp/js/admin/crpUsers.js` | `validateCGIAR()` `:274-283` must stop assuming the backend fills names. Show the inputs; prefill when `DirectoryPerson.found`; leave editable when not | Otherwise corporate guest users get null names |
| `webapp/WEB-INF/crp/views/admin/crpUsers.ftl` | `:64`, `:67` — the `required=` expressions and the `help="Not required for CGIAR emails"` copy | The help text becomes false |
| `webapp/global/js/usersManagement.js` | `:129-139` — the same assumption on the shared popup | 20+ pages load this file [V] |
| `webapp/WEB-INF/global/macros/usersPopup.ftl` | Name inputs gated by `.tickBox-toggle` | 15 FTL pages import it [V] |
| `global.properties` + `custom/*.properties` — **directory copy only** | "…obtained from the active directory" becomes false here | Login copy already moved in Step 1 |
| *(candidate 6 only)* new invitation admin UI | Invite-by-email, pending list, revoke | The UX change behind OQ-20 |

> **Pre-existing bug worth fixing here:** `crpUsers.ftl:64` reads `isCgiarUser` while `:67` reads
> `isCGIARUser`. The action exposes `isCGIARUser()` (`CrpUsersAction:442`), so `:64`'s expression
> silently falls through to its `!true` default [V].

### 5.5 Step 4 — Reach 0 runtime usage

**No production file changes.** Step 4 is `directory.source` moving off `LDAP` and the Cognito
specificity reaching 100% of Global Units, per environment. That is the point: **the cutover is a
config flip, and so is its rollback.**

The only additions are the tripwires that make Gate 1 provable:

| # | File | Purpose |
|---|---|---|
| T1 | `LdapDirectoryService.java` | An **ERROR-level tripwire with a stack trace** at every entry point: `"ADAUTH-TRIPWIRE: Capability B call reached adauth"` |
| T2 | `LDAPAuthenticator.java` | The same for Capability A: `"ADAUTH-TRIPWIRE: Capability A call reached adauth"` |
| T3 | `logback.xml` | A dedicated appender + alert on `ADAUTH-TRIPWIRE` |
| T4 | Glowroot config | Custom instrumentation on `org.cgiar.ciat.auth.LDAPService.*`. **Glowroot is already in the repo** (`Docker/glowroot/`) [V]; whether it runs in production is [OQ] |

**These are additions, not deletions.** They exist so Step 6 can be justified by evidence rather than
confidence, and they are removed with `adauth` itself.

### 5.6 Step 5 — Stabilize

**No file changes.** §9.4 defines the evidence pack.

### 5.7 Step 6 — Physically remove `adauth` and legacy dependencies

**Everything below happens only after Gate 1 is signed and the Step 5 window closes clean.**

| # | File | Change |
|---|---|---|
| A1 | `marlo-data/.../security/APCustomRealm.java` | Delete `getCgiarNickname()` `:285-312`, the `isCgiarUser()` branch `:136-142`, the `ldapAuthenticator` field/parameter `:83`, `:88`, `:93`, imports `:28-29` |
| A2 | `marlo-data/.../MarloShiroConfiguration.java` | Remove `LDAPAuthenticator` from the `apCustomRealm` bean signature `:44-49` and its import `:31` |
| A3 | `marlo-data/.../security/authentication/LDAPAuthenticator.java` | **Delete, 92 LOC** |
| A4 | `marlo-data/.../security/directory/impl/LdapDirectoryService.java` | **Delete** — the last Capability B adauth call site |
| A5 | `marlo-data/.../data/model/ADLoginMessages.java` | **Delete, 50 LOC** — or rename to `LoginMessages`, keeping only `LOGON_SUCCESS` and `USER_DISABLED` |
| A6 | `marlo-web/.../action/home/LoginAction.java` | Reduce the 10-case `switch` `:118-149` to what the DB authenticator can still produce |
| A7 | `marlo-web/.../action/json/global/ValidateUserAction.java` | Same reduction `:102-133` — both must move together |
| A8 | `marlo-web/.../utils/searchUsersUtil.java` | **Delete, 32 LOC** [V] |
| A9 | `marlo-web/.../action/center/capdev/ContactPersonAction.java` | Delete the residual `getADFilter` `:58-71` and any remaining imports |
| A10 | `marlo-web/.../action/center/json/global/ManageUsersAction.java` | **Delete, 265 LOC** — subject to OQ-12 |
| A11 | `APConstants.java` ×2 | Delete `GENERICUSER_AD`, `GENERICPASSWORD_AD`, `HOSTNAME_AD`, `PORT_AD` (**data** `:645-649`, **web** `:705-709`) and the `ad.service.*` config keys added by S1 |
| A12 | `marlo-data/.../security/authentication/Authenticator.java` | **UNCHANGED.** `DBAuthenticator` remains its only implementation. Verify with `git diff --stat` |
| M1 | `marlo-parent/pom.xml` | Delete `<ciat-adauth.version>` `:14` and the `dependencyManagement` entry `:299-303` |
| M2 | `marlo-data/pom.xml` | Delete the dependency `:26-30` **and the entire `<repositories>` block `:199-204`** — that libs tree contains **only** `org/cgiar/ciat` [V] |
| M3 | `marlo-web/pom.xml` | Delete the dependency `:75-78`. **Keep** the `<repositories>` block `:635-639` — that tree also holds `pentaho`, `rhino`, `org/fife` [V] |
| M4 | `marlo-data/src/main/resources/libs/` | **Delete the whole tree** — 16 versions, 3.6 MB |
| M5 | `marlo-web/src/main/resources/libs/org/cgiar/` | **Delete** — 11 versions, 2.4 MB. Do **not** touch `libs/org/fife` or `libs/org/pentaho` |
| M6 | Deploy scripts / `Dockerfile` | Verify nothing enumerates `libs/`. Nothing in `Docker/`, `scripts/`, `configuration/`, or `.github/` references AD [V] |
| G1 | `global.properties` + 18 `custom/*.properties` | Any residual AD copy not already handled in Steps 1 and 3 |
| T1–T4 | The Step 4 tripwires | **Delete** — they have no target left |

**The i18n residue.** ~40 key occurrences across 19 files [V], most already moved in Steps 1 and 3:

| i18n key | Files | Moves in |
|---|---|---|
| `email.outlookPassword` | **7** | Step 1 |
| `login.popup.descriptionAR` / `…AWPB` / `…MY`, `oicrsList.popup.descriptionAR` | **5** each | Step 1 |
| `manageUsers.email.doesNotExist` | **7** | Step 3 |
| `crpGuestUsers.help` | **6** | Step 3 |
| `users.isCCAFS` | **7** | Step 3 |

### 5.8 Database changes

| Change | Required? | Step | Detail |
|---|---|---|---|
| Schema change for the abstraction | **No** | — | `users` already carries every column needed [V] |
| Cognito specificity `parameters` rows | **Yes** | 1 | 3 rows, no `custom_parameters` |
| **`user_invitation` table** | **Only under candidate 6** | 3 | `{email, global_unit_id, role_id, invited_by, expires_at, consumed_at}` |
| Index on `ad_user.email` | **Only if candidate 5 is used as a cache** | 3 | The 2017 migration created **no index** on `email` [V] |
| HQL-injection fix, `AdUserMySQLDAO` `:75`, `:97-98` | **Only if `ad_user` becomes reachable** | 3 | Harmless while unreachable; a real vulnerability once `createUser.do` reaches it [V] |
| One-time AD export into `ad_user` | **Optional safety net for the existing instance** | 3 | **Must run while the service account still works** — that window closes at Step 6. It does nothing for a new instance |
| `users.password` for corporate users | **Unchanged** | — | `UserManagerImp:173` already skips hashing for them [V] |

### 5.9 Tests to add

| Suite | Step | Assertions |
|---|---|---|
| `CognitoTokenValidatorTest` | 1 | Signature, `iss`, `aud`, `exp`, `nonce`, `token_use`; a tampered token is rejected |
| `RealmTokenDispatchTest` | 1 | `CognitoAuthenticationToken` → assertion path; `UsernamePasswordToken` → **byte-identical** legacy path |
| `DirectoryServiceContractTest` | 2 | **One suite run against every implementation.** `found`/`not found`; a null or malformed email never throws; `source` always populated |
| `LdapDirectoryServiceTest` | 2 | The Step 2 wrapper returns exactly what `getOutlookUser` returned — the no-behavior-change proof |
| `ContactPersonActionTest` | 2 | `searchContact.do` returns the same JSON from `ad_user`, **and constructs no AD object** |
| `<Selected>DirectoryServiceTest` | 3 | The chosen provider: a hit, a miss, a timeout, a 4xx, a 5xx. **A directory outage must not break user creation** |
| `UsernameAllocatorTest` | 3 | Determinism; uniqueness retry; no collision with an existing `users.username` |
| `CorporateDomainPolicyTest` | 3 | `is_cgiar_user` established correctly from every configured domain |
| `ManageUsersCreateTest` | 3 | Corporate email + hit → autofill; corporate email + **miss** → names required, `is_cgiar_user` still `1`, username allocated; non-corporate email unchanged |
| `GuestUsersValidatorTest` | 3 | `found == true` → names optional; `found == false` → names required |
| *(candidate 6)* `InvitationJitProvisioningTest` | 3 | The **MARLO `users`** row is created **before** `Subject.login()`; roles applied; an expired invitation is refused |
| `AdUserMySQLDAOInjectionTest` | 3 | *(only if candidate 5 is used)* an email containing `'` or `or 1=1` returns no row |
| **`NoAdauthRuntimeCallTest`** | **4** | **Gate 1's proof.** The tripwires never fire across the full functional suite |
| **`NoAdauthOnClasspathTest`** | **6** | **Gate 2's proof.** `Class.forName("org.cgiar.ciat.auth.LDAPService")` throws `ClassNotFoundException` |

**No mocking framework exists** [V]. The collaborators (`UserManager`, `AdUserManager`,
`CustomParameterManager`) are constructor-injected interfaces, so hand-rolled doubles work; the
`BaseAction` hooks are public and non-final, so a test subclass works. **Decide on a test-scoped
Mockito dependency in Step 0**, not during authoring.

---

## 6. Migration Plan

### 6.1 The six steps

| Step | Name | Delivers | `adauth` state | Depends on | Reversible? |
|---|---|---|---|---|---|
| **0** | **Discovery** | OQ-3, **OQ-3b**, OQ-15, OQ-12, OQ-16, OQ-18, OQ-20 answered; **the Capability B candidate chosen**; mocking decision | in use | — | n/a |
| **1** | **Introduce Cognito authentication** | Capability A, in full. Login copy updated | **in use for B; unused for A on flagged units** | Step 0 (OQ-3) | **Yes — flag flip, seconds** |
| **2** | **Isolate all remaining `adauth` usage** | `DirectoryService` + `LdapDirectoryService`; `LDAPUser` gone from reachable `marlo-web` code; **runtime AD object construction eliminated** (E1). Zero behavior change | **in use, behind one interface** | — | Yes — normal revert |
| **3** | **Replace corporate-user lookup** | The chosen candidate, implemented and validated. Provisioning + frontend changes. Directory copy updated | in use *(still the default)* | Step 0 (OQ-3b), Step 2 | Yes — normal revert |
| **4** | **Reach 0 runtime usage** | `directory.source` flipped per environment; Cognito at 100% of Global Units. Tripwires armed. **← GATE 1** | **INSTALLED, UNUSED** | Steps 1, 3 | **Yes — config flip, no deploy** |
| **5** | **Stabilize and validate** | An agreed window proving zero runtime AD contact | **INSTALLED, UNUSED** | Step 4 | **Yes** throughout |
| **6** | **Physically remove `adauth` and legacy dependencies** | A1–A12, M1–M6, i18n residue, tripwires removed, firewall closed, service account disabled. **← GATE 2** | **GONE** | Step 5 clean | **No** — code revert + redeploy, then a CGIAR IT ticket |

*Plus, outside the sequence:* the **security track** (§5.1) — neutralize the committed credential and
request rotation. Start immediately; blocks nothing, blocked by nothing.

### 6.2 Dependency graph

```
Step 0 ──┬──────────────────────► Step 1 ──┐
         │  (OQ-3)                         │
         │                                 ├──► Step 4 ──► Step 5 ──► Step 6
         └──► Step 2 ──────────► Step 3 ───┘      ▲          ▲
                    (OQ-3b)                       │          │
                                              GATE 1     GATE 2

  security track ── independent, start now ──────────────────────────►
```

**Critical path:** `Step 0 → Step 1 → Step 4 → Step 5 → Step 6`, with Step 3 converging before Step 4.

**Four hard edges, each for a concrete reason:**

| Edge | Why it cannot be reordered |
|---|---|
| Step 0 → Step 3 | The Capability B candidate is decided by OQ-3b. Building before it is answered means building the wrong thing. **Step 2 is not blocked** — the abstraction is identical under every candidate |
| Step 2 → Step 6 | `LDAPUser` is imported by **6 `marlo-web` classes** [V]. Removing the jar before the abstraction breaks compilation in files that make no AD call |
| Step 1 → Step 4 | `APCustomRealm.getCgiarNickname()` calls `LDAPService` on **every** corporate login. Capability A must be live everywhere before adauth can go unused |
| **Step 5 → Step 6** | **The core principle.** Deletion is irreversible; "stop using it" is not. The window converts confidence into evidence |

### 6.3 What Gate 1 means, precisely

| Assertion | How it is true |
|---|---|
| **0 authentication calls through `adauth`** | `LDAPAuthenticator` is still wired but unreachable — every Global Unit has the Cognito flag on, and no corporate user takes the LDAP branch |
| **0 corporate-user lookups through `adauth`** | `directory.source != LDAP` in every environment |
| **0 runtime `LDAPService` calls** | The T1/T2 tripwires never fire |
| **0 runtime `ADConexion` calls** | Same — and E1 removed the last construction site outside `LDAPAuthenticator` |
| `adauth` **may still be declared in Maven** | **Intentional** — `marlo-parent`, `marlo-data`, `marlo-web` all still declare it |
| `adauth` **JARs may still exist** | **Intentional** — all 27, all still in the WAR |
| **Legacy classes may still exist** | **Intentional** — `LDAPAuthenticator`, `LdapDirectoryService`, `ADLoginMessages` all compile and are wired |

**That combination is the point.** A rollback at any moment during Step 5 is `directory.source = LDAP`
plus a specificity flag flip — **seconds, no deploy, no rebuild.**

### 6.4 Shared-file serialization

Per `.agents/leader.md` → *MARLO Directory Boundaries*:

| Shared writer | Written by |
|---|---|
| `marlo-data/.../APConstants.java` | security track (S1), Step 1 (specificity), Step 6 (A11) |
| `marlo-web/.../APConstants.java` | same |
| `marlo-parent/pom.xml` | Step 1 (JOSE), Step 3 (HTTP client), Step 6 (adauth removal) |
| `marlo-web/.../action/BaseAction.java` | Step 2 (C1), Step 6 |
| `global.properties` + 18 `custom/*.properties` | Step 1 (login copy), Step 3 (directory copy), Step 6 (residue) |
| `struts-home.xml` | Step 1 (two new actions) |
| `database/migrations/` | Step 1 (specificity), Step 3 (invitation / index) — **ordering** collides, not content |

**Two tasks each running `mvn` contend for one `target/`.** Disjoint source files are necessary but
**not sufficient** for parallel execution. Use `git worktree`.

---

## 7. Effort Estimate

### 7.1 Assumptions

1. **One engineer per work package**, familiar with MARLO but not with Cognito.
2. Java 17, Struts 2, Shiro 1.13 — no framework upgrade in scope.
3. `mvn checkstyle:check` is a gate; every new file needs the GPL header and 2-space/120-char style.
4. **No mocking framework exists** [V]. Test packages are priced for hand-rolled doubles.
5. **No E2E harness** [V]. All regression is manual and priced as such.
6. **CGIAR IT and CLARISA lead times are excluded** from engineering days and tracked separately.
7. **An unresolved architectural question is not priced as risk.** Bucket B is split into an
   option-independent part and a **conditional per-candidate increment** (§7.3). No contingency is
   added for the decision itself beyond a small, explicit spike.
8. **Bucket C is effort, not duration.** The window is calendar (§7.5); the effort inside it is
   part-time monitoring and defect response.
9. Numbers are **business days of engineering effort**.

### 7.2 Bucket A — Cognito authentication *(Step 1; already planned)*

| WP | Work package | Best | **Expected** | Worst |
|---|---|---|---|---|
| A1 | Cognito backend: token type, assertion, JWKS validator + cache, realm guard, Shiro wiring, 2 Struts actions, PKCE/state/nonce, session rotation, `APConfig` | 8 | **13** | 22 |
| A2 | Cognito frontend: `loginForm.ftl` ×2 host pages, `login.js`, CSS, `crpByEmail.do` rewrite, `validateUser.do` guard | 4 | **6** | 10 |
| A3 | Specificity flag: migration + `APConstants` ×2 + resolution | 1 | **2** | 3 |
| A4 | Deployment: app client, per-environment properties, rollback rehearsal | 2 | **4** | 7 |
| A5 | Auth unit tests | 3 | **5** | 9 |
| A6 | Auth regression + staged per-unit rollout + login i18n copy | 3 | **5** | 9 |
| | **BUCKET A** | **21** | **35** | **60** |

### 7.3 Bucket B — functional replacement of remaining `adauth` capabilities *(Steps 2–3)*

**B-common — required under every candidate**

| WP | Work package | Best | **Expected** | Worst |
|---|---|---|---|---|
| B1 | Security track (§5.1) — neutralize the credential, raise the rotation ticket | 1 | **1** | 2 |
| B2 | Directory abstraction (N1–N4, C1–C6) — zero behavior change | 4 | **7** | 12 |
| B3 | Eliminate runtime AD object construction (E1) + `ContactPersonActionTest` | 1 | **1** | 3 |
| B4 | Spike: prove the chosen source end-to-end before committing to it | 1 | **2** | 4 |
| B5 | Provisioning: `UsernameAllocator`, `CorporateDomainPolicy`, `is_cgiar_user` sourcing, frontend name inputs across `crpUsers` + `usersPopup` | 3 | **6** | 11 |
| B6 | Directory contract + provisioning tests | 3 | **5** | 9 |
| B7 | Directory i18n/config | 1 | **1** | 2 |
| | **B-COMMON** | **14** | **23** | **43** |

**B-option — conditional increment, on top of B-common**

| Candidate | Best | **Expected** | Worst | Driver |
|---|---|---|---|---|
| **3 — CLARISA** | 2 | **4** | 7 | Credentials, host, and getters already exist [V]. Needs a properly validating HTTP client |
| **2 — Directory API** | 3 | **6** | 11 | + client-credentials token provider and cache; coordination on the app registration |
| **4 — LDAP bridge** | 3 | **5** | 9 | Connection/failover handling `adauth` hides. **Adds a permanent obligation** |
| **1 — Cognito admin APIs** | 4 | **7** | 12 | First AWS SDK + IAM integration. **Cannot satisfy the requirement alone** |
| **6 — Invitation + JIT** | 8 | **13** | 22 | New table + migration, JIT before `Subject.login()`, admin UI. Product and UAT time **not** counted here |
| *(5 — `ad_user` cache, add-on)* | +2 | **+3** | +5 | Decorator + HQL fix + index + optional one-time export |

**Bucket B totals by candidate**

| Candidate | Best | **Expected** | Worst |
|---|---|---|---|
| 3 — CLARISA | 16 | **27** | 50 |
| 2 — Directory API | 17 | **29** | 54 |
| 4 — LDAP bridge | 17 | **28** | 52 |
| 6 — Invitation + JIT | 22 | **36** | 65 |

> **Quote bucket B as a range, never as a single number.** Until OQ-3b and OQ-15 are answered,
> **27–36 expected** is the honest statement.

### 7.4 Bucket C — Stabilization *(Step 5)*

| WP | Work package | Best | **Expected** | Worst |
|---|---|---|---|---|
| C1 | Instrumentation: tripwires T1–T4, logback appender + alert, Glowroot instrumentation, network-capture setup | 2 | **3** | 6 |
| C2 | Monitoring + periodic re-verification through the window | 2 | **4** | 8 |
| C3 | Defect response contingency — issues surfacing only under real corporate use | 2 | **4** | 10 |
| | **BUCKET C** | **6** | **11** | **24** |

> **This is not 11 consecutive days of work.** C1 is a short burst before the window opens; C2 and C3
> are **~0.5–1 day per week across roughly 8 weeks**. **Do not staff it as an 11-day work package, and
> do not let its calendar duration be read as 11 days of cost.** The window's *duration* is the real
> constraint, and it is calendar, not effort.

**Window duration:** recommendation **one full reporting cycle, minimum 8 weeks**, so the seasonal
peaks in user creation and guest onboarding are actually exercised. Duration and acceptance authority
are **OQ-19**.

### 7.5 Bucket D — Physical retirement *(Step 6)*

| WP | Work package | Best | **Expected** | Worst |
|---|---|---|---|---|
| D1 | Remove `LDAPAuthenticator`, `LdapDirectoryService`, the realm branch, `ADLoginMessages`, both `switch` blocks, dead actions, AD constants (A1–A12) | 3 | **5** | 8 |
| D2 | Maven + jars: M1–M6, 27 jars, 2 repo trees, 5-module build, WAR inspection | 1 | **2** | 4 |
| D3 | Final full regression (§9.3) | 2 | **4** | 7 |
| D4 | Infrastructure decommission: evidence pack, CGIAR IT tickets, post-closure verification | 1 | **2** | 5 |
| D5 | Documentation: TRD §8.1/§8.4, `infrastructure.md`, `reports/ai-context/*`, spec updates | 2 | **3** | 5 |
| | **BUCKET D** | **9** | **16** | **29** |

### 7.6 Totals

| Bucket | Best | **Expected** | Worst |
|---|---|---|---|
| A — Cognito authentication | 21 | **35** | 60 |
| B — Functional replacement *(candidate-dependent)* | 16 | **27 – 36** | 65 |
| C — Stabilization *(part-time across the window)* | 6 | **11** | 24 |
| D — Physical retirement | 9 | **16** | 29 |
| **TOTAL** | **52** | **89 – 98** | **178** |
| **INCREMENTAL BEYOND A** *(B + C + D)* | **31** | **54 – 63** | **118** |

**By candidate, expected case:**

| Candidate | A | B | C | D | Total | **Incremental beyond A** |
|---|---|---|---|---|---|---|
| 3 — CLARISA | 35 | 27 | 11 | 16 | **89** | **54** |
| 4 — LDAP bridge | 35 | 28 | 11 | 16 | **90** | **55** |
| 2 — Directory API | 35 | 29 | 11 | 16 | **91** | **56** |
| 6 — Invitation + JIT | 35 | 36 | 11 | 16 | **98** | **63** |

> **Making MARLO functionally independent of `adauth` and then physically removing it costs 54–63
> business days beyond the Cognito authentication migration already planned.** The spread across
> candidates is **9 days** — small enough that **the Capability B decision should be made on
> suitability and security, not on cost.**

### 7.7 Elapsed calendar time

| Step | 1 engineer | **2 engineers** |
|---|---|---|
| 0 — Discovery | 1 wk | 1 wk *(mostly waiting on others)* |
| 1 — Cognito authentication | 8 wk | **5 wk** |
| 2 — Isolate | 2 wk | *(overlaps Step 1)* |
| 3 — Replace lookup | 5 wk | **3 wk** *(partly overlaps)* |
| 4 — Cutover | 1 wk | **1 wk** |
| **5 — Stabilize** | **8 wk** | **8 wk — fixed, does not shrink with staffing** |
| 6 — Physical retirement | 4 wk | **3 wk** |
| **TOTAL ELAPSED** | **~29 wk** | **~21 wk (~5 months)** |

Plus **external lead time not counted above**: CGIAR IT federation (OQ-3), the directory-API app
registration or CLARISA endpoint confirmation (OQ-3b / OQ-15), and the service-account decommission.
Historically weeks, not days. **Start all three requests on day one.**

**Effort fell from revision 2 (106 → 89–98) while calendar held at ~21 weeks.** That is expected: the
calendar is set by the stabilization window, which is a duration, not a cost.

### 7.8 Critical path

```
OQ-3 ──► A1 (13) ──► A2 (6) ──► A6 (5) ──► Step 4 ──► C2 window (8 wk) ──► D1 (5) ──► D2 (2) ──► D4 (2)
                                              ▲
OQ-3b ──► B4 (2) ──► B-option (4–13) ──► B5 (6) ──┘
```

Two chains converge at the Step 4 cutover. **The authentication chain is longer**, so Capability B has
slack — but only if OQ-3b is answered early. **If OQ-3b lags OQ-3 by more than ~4 weeks, or candidate
6 is chosen, the Capability B chain becomes critical instead.**

### 7.9 Parallelizable

| Can run in parallel | With | Why it is safe |
|---|---|---|
| **B1** (security track) | anything | Touches two constant values |
| **B2, B3** (Step 2) | A1, A2 | `marlo-web` action bodies vs. `marlo-data` security package — **except `APConstants` and `BaseAction`** |
| B4, B5, B-option | A1, A2 | Different packages, different concerns |
| A5, B6 (tests) | their own packages | Test roots are untouched by production work |
| A4 (deployment) | A1, A2 | AWS console + properties files |
| D5 (docs) | everything | Docs only |
| **C2, C3** | **D1, D2 preparation** | Prepare the removal branch during the window; **merge it only after Gate 1** |

### 7.10 Cannot be parallelized

| Conflict | Packages | Shared file |
|---|---|---|
| Constants | B1, A3, D1 | `APConstants.java` **×2** |
| POM | A1, B-option, D2 | `marlo-parent/pom.xml` |
| Base class | B2, D1 | `BaseAction.java` — 9,748 LOC |
| i18n | A6, B7, D1 | `global.properties` + 18 `custom/*.properties` |
| Routing | A1, A2 | `struts-home.xml` |
| Migrations | A3, B-option | `database/migrations/` — ordering |
| **Gate ordering** | **C → D** | **Not a file conflict — a risk conflict.** D must not merge before Gate 1 |
| **Build** | **any two** | one `target/` per checkout |

---

## 8. Risks and Open Questions

### 8.1 Risk register

| # | Risk | Rating | Evidence | Mitigation |
|---|---|---|---|---|
| **R1** | **CGIAR IT does not federate MARLO.** The programme is void; no fallback removes AD | 🔴 **Critical** | OQ-3 | Resolve in Step 0 **before any code** |
| **R2** | **Capability B has no available replacement** — the IdP is on-prem AD with no HTTP API, CLARISA has no people endpoint, and the business rejects the invitation UX. The only remaining candidate keeps AD | 🔴 **Critical** | §4.4 [I] | **Resolve OQ-3b, OQ-15, OQ-20 in Step 0.** If all three fail, the honest answer is that `adauth` can be retired and **Active Directory cannot** — and that must be said before Step 1, not after |
| **R3** | **Editing the realm prologue locks out every user.** `APCustomRealm:115`'s cast runs on every login | 🔴 **Critical** | [V] | `instanceof` guard **above** the cast; line-by-line diff review; the `UsernamePasswordToken` path preserved byte-for-byte |
| **R4** | **Provisioning breaks silently.** A corporate guest user is created with null `first_name`, `last_name`, `username` — the form hides those inputs and the backend no longer fills them | 🔴 **Critical** | `crpUsers.js:274-283`, `crpUsers.ftl:64,67` [V] | B5's frontend change **must ship with** Step 3. Never flip `directory.source` before it |
| **R5** | **`users.username` reaches CLARISA and the QA service.** A null or synthesized value may be rejected or silently corrupt records downstream | 🔴 **Critical** | `PartnersSaveAction:553`, `QAReportsAction:59` [V] | OQ-14. Candidate 2's `onPremisesSamAccountName` preserves exact semantics; otherwise `UsernameAllocator` is the floor |
| **R6** | **Deleting `adauth` before the replacement is proven** | 🔴 **Critical** *(mitigated by design)* | §6.1 | **This is what Step 5 exists for.** Gate 1 and Gate 2 are separated by a full reporting cycle, and `adauth` stays installed throughout |
| **R7** | **A directory outage breaks user creation.** Every candidate except 5 and 6 puts a network call on the create-user path, where `adauth` sat before | 🟠 **High** | [I] | Timeouts, circuit-breaker, and **degrade to manual entry rather than fail**. Explicit test in B6 |
| **R8** | **The login claim is not mapped.** If `preferred_username` is not configured at federation time, corporate users authenticate with a null `username` | 🟠 **High** | **[V-AWS]** + `Users.hbm.xml:19` [V] | **OQ-18 — request the mapping when federation is configured, not later** |
| **R9** | **Swagger public access breaks.** `ClarisaPublicAccesFilter` logs in a config-supplied account through the same realm | 🟠 **High** | `ClarisaPublicAccesFilter:76-82`; `clarisa.publicUser=XXXXXX` masked [V] | **One SQL query in Step 0** (OQ-16). *Reduced from Critical:* every seeded service account in the repo is `is_cgiar_user = 0` [V] |
| **R10** | **`/api/**` Basic-auth consumers.** Federated identities cannot use Basic auth | 🟠 **High** | `MarloShiroConfiguration:113` [V] | **Substantially de-risked:** seeded API accounts `mel-system@no-reply` and `my-cifor@no-reply` are `is_cgiar_user = 0` with MD5 passwords and non-`@cgiar.org` emails [V]. OQ-4 narrows to production-added accounts |
| **R11** | **The 401 page carries its own copy of the login wizard** | 🟠 **High** | `error/401.ftl:19` [V] | Run every manual login check on **both** host pages |
| **R12** | **No test coverage, no mocking framework.** The assertions that prove Gate 1 are "this collaborator was never called" | 🟠 **High** | 3 test classes, 0 auth tests [V] | Decide Mockito in Step 0. **The tripwires (T1–T4) are the cheaper and more convincing proof** |
| **R13** | **`ExternalPostUtils` gets reused for the directory client.** It installs a trust-all `X509TrustManager` and disables SNI | 🟠 **High** | [V] | Explicit prohibition in the B-option task; a review gate |
| **R14** | **Type-2/5 Global Units can never hold the specificity**, so their corporate users stay on LDAP permanently and Step 4 is unreachable for them | 🟠 **High** | `auth-flow/design.md` §3, OQ-11 [V] | Resolve OQ-11 in Step 0 |
| **R15** | **HQL injection in `AdUserMySQLDAO`** — harmless today, live once `createUser.do` reaches it | 🟠 **High** | `:75`, `:97-98` [V] | Fix in the same commit that makes it reachable. *(`UserMySQLDAO.searchUser:144-156` has the same shape and is already reachable [V] — pre-existing, out of scope, worth a separate ticket)* |
| **R16** | **The stabilization window gets cut short** under delivery pressure, and Gate 2 lands on confidence rather than evidence | 🟠 **High** | [I] | Make Gate 1's evidence pack (§9.4) an explicit, signed artifact. **OQ-19 fixes the duration before Step 4**, when it is cheap to agree |
| **R17** | **`users.username` is unique.** Synthesized usernames can collide | 🟡 Medium | `Users.hbm.xml:19` [V] | `UsernameAllocator` with retry + a uniqueness test |
| **R18** | **i18n drift across 19 files** | 🟡 Medium | 40 key occurrences [V] | Copy moves with the step that invalidates it (login → Step 1, directory → Step 3); a grep gate at Gate 2 |
| **R19** | **The convention plugin may expose "dead" actions.** `struts2-convention-plugin` is on the classpath with `mapAllMatches=true` | 🟡 Medium | `marlo-web/pom.xml:89`, `struts.xml:25-28` [V/I] | OQ-12: probe before deleting in Step 6. **Step 2 migrates them rather than assuming** |
| **R20** | **Candidate 5's data governance.** A local copy of corporate personnel data brings staleness, leavers, and retention obligations | 🟡 Medium | [I] | Prefer no cache. If used, TTL + a documented retention policy |
| **R21** | **Users with zero Global Units get a malformed `crpByEmail.do` response** | 🟡 Medium | `CrpByUserEmailAction:72`, `:89` [V] | Already scoped in `auth-flow`; verify it lands |
| **R22** | **WAR contents change by 6 MB and 27 jars** | 🟡 Medium | `target/classes/libs/` [V] | D2 verifies build + deploy; nothing references `libs/` [V] |
| **R23** | **The AD password is in git history.** The security track removes the working-tree copy, not the history | 🟡 Medium | [V] | Rotation by CGIAR IT is the only real remedy — **request it in Step 0** |
| **R24** | **`ad_user` is empty.** If nothing ever populated it, the optional cache and the safety net both start at zero | 🟡 Medium | No writer in MARLO [V]; external population is OQ-5 | The one-time export, run **before** Step 6 |
| **R25** | **Candidate 4 becomes permanent.** A "time-boxed bridge" with no removal date is just the status quo with more code | 🟡 Medium | [I] | If candidate 4 is chosen, the removal date is part of the approval |
| **R26** | **TRD and infrastructure docs are already wrong about AD.** TRD §8.1 says `adauth-5.6.jar`; it is **5.7**. TRD §8.4 says `/api/*` uses `QAToken`; it uses `authcBasic` | 🟢 Low | [V] | D5. *`impact-analysis.md` F-5 also claims the jar is not under `resources/libs` — it **is**, as a committed file repository* |

### 8.2 Open Questions

OQ-1 … OQ-11 are defined in `family.md` and `auth-flow/`. Revision 1 added OQ-12 … OQ-17; revision 2
added OQ-3b and OQ-18 … OQ-20.

| # | Question | Status | Blocks |
|---|---|---|---|
| **OQ-3** | Which IdP does CGIAR run, and will CGIAR IT federate MARLO? | **Open — hard blocker** | Everything |
| **OQ-3b** | **Is that IdP one with a queryable HTTP directory API (e.g. Entra ID / Graph), or on-prem AD / ADFS with none?** | **Hard blocker for Capability B.** OQ-3 decides whether Capability A is possible; **OQ-3b decides whether Capability B has any answer other than "keep AD"** | **Step 3.** *Not Step 2* |
| **OQ-15** | Does CLARISA expose a **people / user** endpoint? | **Open — ask first.** MARLO already calls CLARISA with Basic auth [V], so if one exists it is the cheapest candidate. **No such endpoint appears anywhere in this repository** [V] | Step 3 |
| **OQ-20** | **Is the invitation + JIT UX change acceptable to the business?** The admin would no longer see the person's name at invitation time | **Open.** Needed if OQ-3b and OQ-15 both fail — but **ask it early**, because it is the difference between removing AD and merely removing `adauth` | Step 3 |
| **OQ-18** | **Will the corporate login (`sAMAccountName`) be mapped to `preferred_username` or a custom claim at federation time?** | **Open.** Retrofitting later leaves already-signed-in users with a null `username` until they sign in again | Step 1 — **raise it with the federation request** |
| **OQ-19** | **How long is the Step 5 window, and who signs Gate 1?** | **Open.** Recommendation: one full reporting cycle, **minimum 8 weeks**. Agree it **before Step 4** | Step 5 → Step 6 |
| OQ-17 | Will AWS IAM credentials or an instance role be provisioned for the Cognito admin APIs? | **Open, low priority.** §4.1 shows the admin APIs cannot answer the requirement anyway | Candidate 1 only |
| OQ-11 | Do type-2/5 Global Units have corporate users? | **Open** | Step 4 reachability |
| OQ-12 | Does the convention plugin expose `SearchUserAction` and `center/.../ManageUsersAction`? | **Open.** Neither is in any Struts XML [V]; probe a running instance. **Step 2 migrates them either way**, so this only gates Step 6's deletion | Step 6 (A10) |
| OQ-14 | Do CLARISA `partner-requests` and the QA token service accept a **synthesized** username? | **Open.** Both receive `users.username` today [V]; neither contract is in this repository | Step 3 |
| OQ-16 | Is `clarisa.publicUser` an `is_cgiar_user = 1` account? | **Open.** One SQL query | Step 4 |
| OQ-4 | Who consumes `/api/**` with Basic auth, and are any corporate users? | **Narrowed.** All seeded API accounts are `is_cgiar_user = 0` with `@no-reply` emails [V] | Step 4 |
| OQ-5 | Is `ad_user` populated by a live job or a stale import? | **Half-answered.** **Nothing in MARLO writes it** [V] | Candidate 5 only |
| OQ-6 | Microsoft Graph or the `ad_user` mirror? | **Superseded by OQ-3b.** Revision 1 answered "the mirror"; §4.2 shows that was wrong. The mirror is at most a cache | — |

---

## 9. Testing Strategy

### 9.1 The honest starting point

**Zero tests cover authentication or directory lookup today** [V]. `mvn test` green means "the three
existing tests still pass".

### 9.2 Layers

| Layer | Step | Content |
|---|---|---|
| **Unit** | 1–3 | §5.9's suites. `DirectoryServiceContractTest` runs against **every** implementation, so the Step 4 swap is covered by construction |
| **Integration** | 1–3 | Realm dispatch across all 4 surfaces; the full create-user path against a seeded database |
| **Regression (manual)** | 4, 6 | §9.3 — there is no E2E harness to automate it |
| **Security** | 3 | HQL-injection cases; TLS validation on the new client; no credential literal in any `.java` |
| **Stabilization** | **5** | §9.4 — **the evidence that Gate 1 is real** |
| **Network** | 5, 6 | The only proof that MARLO stopped talking to AD |

### 9.3 The manual regression matrix

Run in full at **Step 4** (Gate 1) and **Step 6** (Gate 2).

| # | Scenario | Expected | Why it is on the list |
|---|---|---|---|
| 1 | Corporate user, flag **on** — `login.do` | Cognito redirect, session established, correct Global Unit | Capability A |
| 2 | Corporate user, flag **off** | Unchanged *(pre-Step-4 only)* | Staged rollout |
| 3 | **Local (non-corporate) user** — `login.do` | Unchanged. MD5 path, same messages | R3 |
| 4 | Local user — `validateUser.do` then `login.do` | Both succeed; **two authentications** as today | The local path authenticates twice [V] |
| 5 | **Session expiry → 401 page → re-authenticate** | Same branch as the login page | **R11** |
| 6 | `/api/**` Basic auth, each known consumer | Unchanged | R10 |
| 7 | **Anonymous** `/swagger/index.html`, `home.html`, `api.html`, `generalListReference.html` | Renders, no login prompt | **R9** |
| 8 | Create user, corporate email, **directory hit** | Names + username prefilled, `is_cgiar_user = 1`, **row created in MARLO's `users`** | Capability B |
| 9 | **Create user, corporate email, person has NEVER logged into MARLO** | **Record created successfully.** Names required and editable if the source cannot supply them; `is_cgiar_user = 1`; username allocated | **The defining requirement.** If this fails, the migration has failed |
| 10 | **Create user on a FRESH MARLO instance — empty `users`, empty `ad_user`** | Identical to 9 | **The second defining requirement.** This is the scenario that disqualified the mirror |
| 11 | **An existing MARLO user who has never authenticated through Cognito logs in for the first time** | Succeeds; `username` populated or preserved; roles unchanged | The first-federation path for the existing population |
| 12 | Create user, non-corporate email | Unchanged: names required, random password generated and emailed | §3.6 |
| 13 | Guest user creation — the full `{crp}/crpUsers` save | Validator + save agree; `crp_users` + `user_roles` written; email sent | §3.5 |
| 14 | Create user from **each** of the 15 `usersPopup.ftl` pages | Identical behavior | The widest surface [V] |
| 15 | **Directory source unreachable** (timeout / 5xx) during user creation | **Degrades to manual entry. Does not fail** | **R7** |
| 16 | **Partner request → CLARISA** by a newly provisioned corporate user | Accepted with the allocated username | **R5** |
| 17 | **QA report link** by the same user | Token generated and accepted | **R5** |
| 18 | Login by **username** rather than email | Resolves via `users.username` | `APCustomRealm:128` [V] |
| 19 | User with **zero** Global Units | Clear message, no malformed response | R21 |
| 20 | Inactive user (`is_active = 0`) | Refused with the disabled message | `APCustomRealm:146-149` [V] |
| 21 | `searchContact.do` | Same JSON from `ad_user` as before, **and no AD object constructed** | E1 |
| 22 | **Two users, same email local-part, different domains** | No `users.username` collision | R17 |
| 23 | Authorization after a Cognito login | Roles and permissions identical to the LDAP login | `doGetAuthorizationInfo` untouched [V] |
| 24 | *(candidate 6)* Invite → the person logs in for the first time | **MARLO `users`** row created **before** `Subject.login()`; roles applied; `is_cgiar_user = 1` | §3.2's precondition [V] |

### 9.4 Step 5 — the Gate 1 evidence pack

**This is the deliverable that justifies Step 6.** Without it, deletion rests on confidence.

| # | Evidence | Method | Threshold |
|---|---|---|---|
| E1 | **Application tripwires never fire** | T1/T2 log an ERROR with a stack trace at every `adauth` entry point; T3 alerts on `ADAUTH-TRIPWIRE` | **Zero occurrences** across the whole window |
| E2 | **No LDAP method is entered** | Glowroot custom instrumentation on `org.cgiar.ciat.auth.LDAPService.*` (present in the repo [V]; production use is [OQ]) | **Zero invocations** |
| E3 | **No network egress to AD** | Firewall hit counters on `*.cgiarad.org:3268` and `:636`, **with the rule still open** | **Zero packets** across the window |
| E4 | **No JNDI LDAP context creation** | Log inspection for `com.sun.jndi.ldap`; a temporary DEBUG logger enabled for the window | **Zero contexts** |
| E5 | **Every scenario exercised** | §9.3 run at the start and end of the window; scenarios 9, 10, 11, 14, 15 repeated monthly | **All pass, both runs** |
| E6 | **Real corporate usage occurred** | Count distinct corporate logins and corporate users created during the window | **Non-zero, and representative.** A quiet window proves nothing |
| E7 | **Both peaks covered** | The window spans at least one full reporting cycle | Guest onboarding and user creation actually happened |
| E8 | **Rollback rehearsed** | `directory.source = LDAP` flipped and reverted in a non-production environment | Confirmed working, **twice** |

> **E6 is the one most likely to be skipped and most likely to matter.** A stabilization window during
> a quiet period proves the code was not exercised, not that it works. If corporate user creation did
> not happen in the window, **extend the window** — do not pass the gate.

### 9.5 Gates

| Gate | Command / check |
|---|---|
| Build | `mvn -q install -DskipTests -pl marlo-web -am` |
| Style (**mandatory**) | `mvn -q checkstyle:check` |
| Unit | `mvn -q test -pl marlo-web` |
| Regression surface unchanged | `git diff --stat` shows **no** change to `Authenticator.java`, `DBAuthenticator.java`, `MD5Convert.java`, or `doGetAuthorizationInfo()` |
| **Gate 1 proof** | `NoAdauthRuntimeCallTest` + the §9.4 evidence pack |
| **Gate 2 proof** | `NoAdauthOnClasspathTest` + the §11.2 grep suite |

---

## 10. Infrastructure Cleanup

### 10.1 What can be retired, and only after what

**Nothing here happens before Gate 1, and none of it before Step 6.**

| Item | Retire after | Owner | Reversible? |
|---|---|---|---|
| **AD service account `ldapuser`** | **Step 6 in production, observed clean** | **CGIAR IT** | Re-provisioning ticket |
| **Outbound firewall: `*.CGIARAD.ORG` port 3268** (plaintext GC) | Same | Network ops | Firewall change |
| **Outbound firewall: `*.cgiarad.org` port 636** (LDAPS) | Same | Network ops | Firewall change |
| **DNS** for `ciatroot4/5.CGIARAD.ORG`, `azcgccroot1/4.cgiarad.org`, `ciatroot1.ciat.cgiarad.org` | Same | Network ops | — |
| **6 MB / 27 jars** in the WAR | Step 6 | MARLO eng | Normal revert |
| **`marlo-data/src/main/resources/libs/`** and its `<repositories>` block | Step 6 | MARLO eng | Normal revert |
| **`repos.ciat.cgiar.org` Nexus** as an `adauth` source | Step 6 | CIAT infra | — |

> **Critical ordering.** The firewall rule must stay **open** through the whole Step 5 window — it is
> the instrument that produces evidence E3. Closing it early replaces "AD is not being called" with
> "AD cannot be called", which proves nothing about the code and hides regressions behind a network
> error.

### 10.2 What must be added

| Item | Capability | Why |
|---|---|---|
| Outbound HTTPS to `cognito-idp.{region}.amazonaws.com` | A | JWKS retrieval |
| Outbound HTTPS to the Cognito hosted-UI domain (`COGNITO_LINK`) | A | Token exchange |
| Cognito app client with `COGNITO_REDIRECT_URI` registered per environment | A | The callback fails silently on a mismatch |
| A registered sign-out URL for `cognito.logout.uri` | A | MARLO's session invalidation has no Cognito equivalent today |
| **The corporate login claim mapped** to `preferred_username` or a custom claim | A → B | **OQ-18.** Without it, `users.username` is null for every corporate user |
| *(candidate 2)* Outbound HTTPS to the directory API + an app registration with a read-users scope | B | The lookup itself |
| *(candidate 3)* Nothing — `clarisa.api.*` egress already exists [V] | B | — |
| Secret storage for `COGNITO_CLIENT_SECRET` and any directory client secret | both | Never in a tracked file (hard rule 12) |

### 10.3 Proving MARLO stopped calling AD

Because AD configuration is **compiled in, not configured** (§2.6), grepping properties proves
nothing. The proof is §9.4's evidence pack, executed in this order:

```
  1. Arm the tripwires (T1–T4), firewall rule STILL OPEN
  2. Flip directory.source; Cognito at 100% of Global Units       ← Step 4 / GATE 1
  3. Observe for the full window: E1, E2, E3, E4, E6, E7          ← Step 5
  4. Re-run the regression matrix                                  ← E5
  5. GATE 1 signed
  6. Delete the code and the jars                                  ← Step 6
  7. Observe again — 2 weeks, rule still open
  8. Close the firewall rule
  9. Observe again — 1 week
 10. Disable the AD service account                                ← GATE 2 complete
```

**Steps 7–10 are deliberately sequential.** Closing the firewall before disabling the account means a
failure surfaces as a network error, which is diagnosable. Reversing them means it surfaces as an
authentication error, which looks like an application bug and costs a day of the wrong investigation.

---

## 11. Definition of Done

Two gates, separated by the Step 5 window. **Gate 1 is the migration. Gate 2 is the cleanup.**

### 11.1 GATE 1 — Functional AD / `adauth` independence

> **`adauth` is still declared in Maven. Its JARs are still in the WAR. Its legacy classes still
> compile and are still wired. All of that is intentional** — it is the rollback safety net that makes
> Step 4 a config flip instead of a redeploy. **Gate 1 is about runtime behavior only.**

| # | Check | Evidence |
|---|---|---|
| 1 | **Corporate authentication uses Cognito** | Scenario 1, every Global Unit, flag on |
| 2 | **Corporate user lookup works without `adauth`** | Scenario 8, `directory.source != LDAP` |
| 3 | **Users can be created in MARLO even if they were never previously in MARLO** | **Scenario 9** — the defining requirement |
| 4 | **A fresh MARLO instance does not depend on historical `users` / `ad_user` data** | **Scenario 10** — run against an empty database |
| 5 | **Users who have never authenticated through Cognito are handled correctly** | **Scenario 11** |
| 6 | **No runtime `LDAPService` calls** | E1 + E2 — tripwires and Glowroot, zero across the window |
| 7 | **No runtime `ADConexion` calls** | E1 + E2, and scenario 21 for the `searchContact.do` path |
| 8 | **No CGIAR password is processed by MARLO** | Code review of `validateUser.do` and `login.do` + a spy asserting `userManager.login()` is never called for a flag-enabled corporate account |
| 9 | **Local / non-CGIAR authentication continues working** | Scenarios 3, 4, 18, 20 — unchanged behavior, unchanged messages |
| 10 | **User provisioning / autofill continues working** | Scenarios 8, 9, 14 across all 15 `usersPopup.ftl` pages |
| 11 | **User search continues working** | Scenarios 14, 21 |
| 12 | **Guest-user creation continues working** | Scenario 13 |
| 13 | **`is_cgiar_user` is set correctly** | Scenarios 8, 9, 12; the ~10 non-auth consumers behave as before; no corporate user receives a generated-password email |
| 14 | **`users.username` is populated for new corporate users** | Scenario 9 + a DB check. **This is the check that catches R4 and R5** |
| 15 | **CLARISA / QA integrations still receive the required identity information** | Scenarios 16, 17 |
| 16 | **`/api/**` behavior unchanged** | Scenario 6 |
| 17 | **Swagger / public access unchanged** | Scenario 7 |
| 18 | **Session handling unchanged** | 30-minute timeout, `JSESSIONID` `httpOnly` path `/`, session-ID rotation on login; scenario 5 for 401 re-auth |
| 19 | **A directory outage degrades rather than fails** | Scenario 15 |
| 20 | **Logs are clean** | E1 + E4 — no tripwire, no JNDI LDAP context |
| 21 | **No network connections to AD** | E3 — firewall counters at zero, **with the rule still open** |
| 22 | **Authorization is byte-identical** | Scenario 23; `git diff` shows no change to `doGetAuthorizationInfo()` |
| 23 | **Rollback works** | E8 — rehearsed twice |
| 24 | **Real corporate usage occurred in the window** | E6 + E7 — a quiet window does not pass |
| 25 | **Build, style, and unit gates green** | §9.5 |

**Gate 1 signed by the authority named in OQ-19.** Only then does Step 6 begin.

### 11.2 GATE 2 — Physical retirement

| # | Check | Command / evidence |
|---|---|---|
| 26 | **No `adauth` Maven dependency** | `mvn dependency:tree -Dincludes=org.cgiar.ciat.auth` empty for all 5 modules |
| 27 | **No `adauth` declaration** | `grep -rn "adauth" marlo-*/pom.xml` → empty |
| 28 | **No `adauth` JARs** | `marlo-data/src/main/resources/libs/` absent; `marlo-web/.../libs/org/cgiar/` absent; `libs/org/fife` and `libs/org/pentaho` **still present** |
| 29 | **No `LDAPService` references** | `grep -rn "LDAPService" marlo-*/src` → empty |
| 30 | **No `LDAPUser` references** | `grep -rn "LDAPUser" marlo-*/src` → empty |
| 31 | **No `ADConexion` references** | `grep -rn "ADConexion" marlo-*/src` → empty |
| 32 | **No obsolete imports** | `grep -rn "org.cgiar.ciat.auth" marlo-*/src` → empty *(`org.cgiar.ciat.abw` in `WSMarlo.java` is the OCS artifact — excluded)* |
| 33 | **No obsolete AD credentials** | `grep -rn "GENERICUSER_AD\|GENERICPASSWORD_AD\|ldap2005\|ldapuser\|ad.service." marlo-*/src` → empty |
| 34 | **No obsolete AD host/port constants or configuration** | `grep -rn "HOSTNAME_AD\|PORT_AD\|cgiarad\|ciatroot\|azcgccroot" marlo-*/src` → empty; `grep -rni "ldap" marlo-*/src/main/resources` → empty |
| 35 | **`marlo-data`'s `<repositories>` block removed** | `grep -n "repositories" marlo-data/pom.xml` → empty |
| 36 | **The WAR contains no adauth artifact** | `unzip -l marlo-web/target/*.war \| grep -i adauth` → empty |
| 37 | **Obsolete LDAP/AD classes removed** | `LDAPAuthenticator`, `LdapDirectoryService`, `ADLoginMessages`, `searchUsersUtil` absent; `MarloShiroConfiguration.apCustomRealm` no longer names the type |
| 38 | **The realm has no directory call** | `APCustomRealm` contains no `getCgiarNickname` and no `ldapAuthenticator` |
| 39 | **Classpath proof** | `NoAdauthOnClasspathTest` green |
| 40 | **Tripwires removed** | T1–T4 gone; the logback appender and Glowroot instrumentation removed |
| 41 | **No AD service-account requirement** | Written confirmation from CGIAR IT that `ldapuser` is disabled |
| 42 | **No MARLO-specific firewall / network requirement toward AD** | Rules removed; the application runs 24 h clean afterwards |
| 43 | **Regression tests pass** | The full §9.3 matrix, all 24 scenarios, signed off |
| 44 | **No AD copy remains in the UI** | `grep -rniE "active directory\|outlook" marlo-web/src/main/resources/*.properties marlo-web/src/main/resources/custom/*.properties` → empty *(all 19 files)* |
| 45 | **HQL injection fixed** *(if candidate 5 was used)* | `AdUserMySQLDAO` uses parameter binding; the injection test is green |
| 46 | **GPL header on every new file** | Checkstyle |
| 47 | **Docs corrected** | TRD §8.1, TRD §8.4, `infrastructure.md`, `reports/ai-context/*` |

---

## 12. Final Assessment

### 12.1 Direct answer to the question

> **How can MARLO replace both the authentication and corporate-user-lookup capabilities currently
> provided by `adauth`, while continuing to create users in its own database — including users who
> have never previously logged into MARLO — and then safely remove `adauth` only after the
> replacement has been stabilized?**

| | |
|---|---|
| **The objective** | **Functional independence** — zero runtime `adauth` usage. Physical deletion is the cleanup that follows, not the goal |
| **Capability A — authentication** | **Cognito, federated to the corporate IdP.** Solved. ID-token claims are *richer* than `LDAPUser`, and they also eliminate `getCgiarNickname` — one of the two authentication-path AD calls — for free |
| **Capability B — corporate user lookup** | **Not solved by Cognito, and not selected here.** No Cognito API reaches a user who has never signed in; no mechanism delegates a search to the external IdP; the User Migration trigger relocates the AD dependency rather than removing it **[V-AWS]** |
| **The replacement for `searchUserByEmail`** | **An open architectural decision** with six candidates, gated on OQ-3b and OQ-15. **None is selected in this document**, and none should be until those questions are answered |
| **`ad_user`** | **Not the architecture.** It cannot serve a never-logged-in user or a fresh instance. Optional cache only |
| **MARLO's user database** | **Unchanged under every candidate.** `users` remains the system of record. Nothing is provisioned into Cognito — Cognito is an authentication provider, not MARLO's user store |
| **Safe removal** | **Two gates separated by a mandatory stabilization window.** Gate 1: zero runtime usage, `adauth` still installed, rollback is a config flip. Gate 2: physical deletion, after a full reporting cycle of evidence |
| **Incremental cost beyond the planned auth migration** | **54–63 business days expected** (31 best / 118 worst), the range being the Capability B candidate |
| **Elapsed** | **~21 weeks with 2 engineers**, dominated by the 8-week stabilization window |

### 12.2 What revision 3 corrected

1. **A runtime call site was mislabelled as dead code.** `ContactPersonAction:93` constructs an
   `ADConexion` on every hit of a **registered** action [V]. Revision 2 scheduled its removal as an
   early "security cleanup"; it is actually **the one piece of deletion that belongs in the functional
   phase**, because Gate 1 requires zero `ADConexion` calls. Meanwhile the genuinely dead parts
   (`getADFilter`, `searchUsersUtil`) moved *out* of the early phase and into Step 6, where physical
   deletion belongs.

2. **The document ranked options it had declared blocked.** Revision 2 said Capability B was an open
   question and then marked Microsoft Graph "Preferred". Both cannot be true. All six are now
   **candidates**, ordered only by a decision tree whose root is unanswered.

3. **Effort was inflated by an open question and by mispriced stabilization.** A decision that has not
   been made is not a cost; it is a spike. And an 8-week window at 0.5–1 day/week is not 14 days of
   continuous engineering. Correcting both drops the expected total from 106 to **89–98** while
   leaving elapsed time unchanged at ~21 weeks — which is the correct shape, because the calendar is
   set by the window and the window is duration, not cost.

4. **The credential fix and the code deletion were entangled.** They are now separate: the *secret*
   leaves immediately on a security track; the *constants* leave at Step 6 with everything else.

### 12.3 What this programme may not be able to deliver

**If OQ-3b answers "on-prem AD / ADFS", OQ-15 answers "no people endpoint", and OQ-20 answers "the UX
change is not acceptable", then `adauth` can be retired and Active Directory cannot.**

The only remaining candidate is a new LDAP client — which keeps the service account, the firewall
rule, ports 3268 and 636, and the JNDI surface. Gate 1 and Gate 2 would both pass on their letter
while the programme's purpose would not be met.

**That outcome is knowable in Step 0, from three conversations.** It must not be discovered in Step 3,
after the auth migration has shipped and the business believes AD is going away.

### 12.4 Verdict

**Capability A is ready to build. Capability B is not ready to design. Step 2 is ready to build
regardless** — the abstraction is identical under all six candidates, it is behavior-preserving, and
it removes `LDAPUser` from every reachable `marlo-web` class [V], turning an unbounded refactor into a
one-file swap once the decision lands.

> Cognito replaces how corporate users **log in**. It does not replace how MARLO **finds** them.
> Making MARLO functionally independent of `adauth` — and only then deleting it — costs about
> **54–63 days beyond** the authentication migration and takes about **five months** end to end, most
> of which is a stabilization window rather than engineering. Its feasibility rests on one question
> nobody has answered: **which corporate identity provider does CGIAR run.**

**Recommended immediate actions, in order:**

1. **Ask OQ-3 and OQ-3b together** — one conversation with CGIAR IT. OQ-3b is the one that decides
   whether AD independence is achievable at all.
2. **Ask OQ-15** — one email to the CLARISA team. If a people endpoint exists it is the cheapest
   candidate, because the host, credentials, and getters are already in `APConfig` [V].
3. **Start the security track today** (§5.1). One day plus a rotation ticket; independent of everything.
4. **Raise OQ-18 with the federation request** — the login-claim mapping must be configured up front,
   not retrofitted.
5. **Start Step 2 now.** Behavior-preserving, required under every candidate, blocked by no open
   question.
6. **Agree the Step 5 window and its signing authority (OQ-19) before Step 4 begins**, not when
   delivery pressure arrives.
7. **Decide Capability B on suitability and security, not cost** — the spread across candidates is 9
   days out of ~90.
