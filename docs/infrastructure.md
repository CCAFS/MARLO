# MARLO — Infrastructure Blueprint

> **Constitutional baseline.** This document describes MARLO's environments from a developer's
> laptop to PROD. It is derived from the TRD's architecture-tier decision and never precedes it.
>
> **Tier decision:** **Robust** — see [`docs/trd/trd.md`](./trd/trd.md) §1.2 (Deployment topology)
> and §12 ADR-9 (Jenkins-driven CI/CD). MARLO is a multi-tenant, phase-versioned system of record
> for CGIAR reporting with regulated audit obligations; the robust tier is what its availability,
> auditability, and data-durability requirements buy.

**Scope note:** sections 1–5 are **governed** — they define what exists and how it is changed.
Section 6 is **disposable** — agents may freely start, seed, and reset the local stack.

---

## 1. Target Environment

| Environment | Host | Access | Purpose |
|---|---|---|---|
| **Production** | AWS (us-east-1, Virginia) | Public + VPN for admin | Live system of record for all Global Units |
| **Staging** | AWS — mirrors Production | GlobalProtect VPN | Pre-production validation; being built out to mirror PROD |
| **Test** | On-premise, CIAT Palmira | FortiClient VPN | QA regression, integration testing |
| **Local** | Developer laptop | n/a | See section 6 |

Primary cloud is **AWS**. Analytics is a second plane: **Microsoft Fabric Lakehouse + Power BI**
(TRD ADR-7). AI services run on **AWS Bedrock + OpenSearch** (TRD ADR-8).

---

## 2. Core Cloud Components

| Component | Service | Role |
|---|---|---|
| Application server | EC2 — Tomcat 9 / JDK 17 | Serves the `marlo-web` WAR at ROOT |
| Frontend / static | EC2 + CDN (`cdn.url` property) | Static assets, uploads |
| Database | RDS MySQL | System of record — phase-versioned relational schema |
| Object storage | S3 | Backups, uploaded documents, autosave payloads |
| Session store | memcached (`memcached-session-manager` on Tomcat) | Session replication across app instances |
| Serverless | AWS Lambda | AI/automation tasks (see TRD §7) |
| AI | AWS Bedrock (Claude + Titan), OpenSearch | RAG pipelines behind `/api/*` and `struts-ai.xml` flows |
| Integration | CLARISA integration EC2 instance | Reference-data sync |
| APM | Glowroot (agent shipped in `Docker/glowroot/`) | JVM / application profiling |
| Analytics | Microsoft Fabric Lakehouse → Power BI | Bronze / Silver / Gold; 8h refresh (Results), 30min (QA) |

**Container image:** the root [`Dockerfile`](../Dockerfile) is the deployment artifact definition —
a two-stage build (`maven:3.9-eclipse-temurin-17` → `tomcat:9.0-jdk17-temurin`) that drops the WAR at
`/usr/local/tomcat/webapps/ROOT.war`, keeps a copy under `/usr/local/tomcat/backup/`, and layers in
the memcached/kryo session JARs plus Tomcat config from `Docker/`.

---

## 3. Deployment Strategy

```
GitHub push  →  .github/workflows/jenkins-trigger-java-{dev,staging,prod}.yml
             →  Jenkins (https://automation.prms.cgiar.org/)
             →  mvn build → WAR → container / EC2 deploy
             →  Slack notification
```

| Element | Value |
|---|---|
| CI orchestrator | Jenkins, triggered by GitHub Actions |
| Trigger workflows | `.github/workflows/jenkins-trigger-java-dev.yml`, `-staging.yml`, `-prod.yml` |
| Build | `mvn clean install -Dmaven.test.skip=true` (see `Dockerfile`) |
| Artifact | `marlo-web/target/*.war` deployed as `ROOT.war` |
| Schema | **Flyway migrations** under `marlo-web/src/main/resources/database/migrations/`, applied on deploy |
| IaC | **Not committed to this repo.** Infrastructure is managed outside the application repository |
| Notifications | Slack (TRD ADR-9) |

**Branch → environment mapping** (mirrors `CLAUDE.md` hard rule #9):

| Branch | Deploys to |
|---|---|
| `main` | Production (release-tagged promotion only) |
| `staging` | Staging — **the integration branch; feature branches start here and merge back here** |
| `dev` | Dev — unstable, integration experiments only |

**Migration ordering is a deploy-time constraint, not a code-review one.** Two branches that each
add a Flyway migration merge cleanly and then collide at apply time. Spec-level guidance for this
lives in `docs/specs/general-setup/family.md` → *MARLO-specific guidance*.

---

## 4. Network & Security Architecture

| Layer | Control |
|---|---|
| Authentication | Apache Shiro; CGIAR Active Directory as primary realm, internal MD5 fallback for legacy accounts (TRD ADR-6, §8.1) |
| Authorization | Shiro roles + per-section edit gates (TRD §8.2–8.3) |
| REST auth | Separate scheme for `/api/v2/*` (TRD §8.4) |
| Transport | HTTPS terminated ahead of Tomcat in PROD / Staging |
| Non-prod access | Test = FortiClient VPN; Staging = GlobalProtect VPN |
| Secrets | `marlo-${profile}.properties` — **gitignored**; never committed. Bootstrap from `marlo-test.properties` |
| Dependency floors | Enforced in `marlo-parent/pom.xml`; see TRD §8.5 and `CLAUDE.md` hard rule #11 |
| Rate limiting | TRD §9.5 |
| Backups | Dedicated backup EC2 instance + S3; DB snapshots via RDS |

---

## 5. Infrastructure Rules & Constraints

1. **Schema changes ship only as Flyway migrations.** No manual DDL against any shared environment.
   Naming: `V<major>_<minor>_<patch>_<YYYYMMDD>_<HHMM>__<Description>.sql`.
2. **Phased data is forward-only.** Past phases are immutable in every environment — a data fix that
   rewrites a closed phase is an infrastructure incident, not a routine migration.
3. **Credential files are never committed.** `.gitignore` covers `marlo-${profile}.properties`.
4. **Dependency versions in `marlo-parent/pom.xml` are floors, not suggestions.** Downgrades require
   explicit approval (`CLAUDE.md` hard rule #11).
5. **Java level is 17.** `marlo-parent/pom.xml` (`<java.version>`) is the verification source.
   Tomcat is 9.x in every environment, local included.
6. **Cloud / PROD deployment is governed, never improvised.** Agents do not deploy. An agent that
   believes a cloud change is required raises it; a human executes it through Jenkins.
7. **IaC lives outside this repository.** Do not add Terraform/CDK here without a constitutional
   change (an `epic` spec).
8. **Do not commit CodeGraph databases.** `.codegraph/.gitignore` handles this; only durable config
   such as `.codegraph/config.json` may be committed.

---

## 6. Local Environment

**The local stack is disposable.** Agents may start it, seed it, break it, and reset it freely to
verify work. Nothing in this section is governed by section 3.

MARLO has **no `docker-compose.yml`**. The contract below reflects what the repository actually
ships: a script-driven Maven + Cargo/Tomcat run against a MySQL instance you supply.

| Element | Value |
|---|---|
| **Primary route (recommended)** | `./scripts/run-marlo-java17.sh` (macOS/Linux) · `scripts\run-marlo-java17.bat` (Windows) — run **from the repo root** |
| **Fallback route** | `mvn install -DskipTests -pl marlo-web -am`, then `mvn -pl marlo-web cargo:run` |
| **Legacy route** | `./scripts/run-marlo-java8.sh` — **only** for Java 8 branches/profiles. Uses HTTPS on port 8443 |
| **Pre-check** | `java -version` must report 17, or `JAVA_HOME` must point at a JDK 17. The run script detects JDK 17 and **exits with instructions** when it cannot find one — surface that message, do not work around it |
| **Database** | MySQL, supplied by the developer (local server or a shared dev instance). Connection comes from `marlo-web/src/main/resources/config/marlo-dev.properties` (`mysql.host`, `mysql.port`, `mysql.user`, `mysql.password`, `mysql.database`) |
| **Seed / reset data** | Bootstrap `marlo-dev.properties` from `marlo-test.properties` in the same folder, then point it at your MySQL. Schema is created and advanced by the Flyway migrations in `marlo-web/src/main/resources/database/migrations/`. **There is no committed seed dump** — obtain a sanitized dump from the IBD team |
| **Health check** | `curl -s -o /dev/null -w "%{http_code}" http://localhost:8080/marlo-web/` returns `2xx` / `3xx`. The run script polls this and opens a browser when ready |
| **URLs / ports** | App: `http://localhost:8080/marlo-web/` (Java 17, HTTP). Java 8 legacy: `https://localhost:8443/marlo-web/`. MySQL: per `marlo-dev.properties` |

### What the run script does, in order

1. Kills any process matching `cargo:run`.
2. Cleans `marlo-utils/target`, `marlo-data/target`, `marlo-web/target` — with retries, because
   target dirs get locked by IDEs on Windows and macOS.
3. Resolves a JDK 17, or exits with instructions.
4. Runs `scripts/update-marlo-dev-java17.sh`, which normalizes `marlo-dev.properties` to
   **HTTP on port 8080** so generated links resolve.
5. `mvn install -DskipTests -pl marlo-web -am` — no `clean` phase, since step 2 already did it.
6. `mvn -pl marlo-web cargo:run`.

### Verification commands (agent-lean)

These are the canonical commands. **Failures print complete and verbatim** — they are evidence.
Only passing noise is suppressed.

| Purpose | Command |
|---|---|
| Compile / build | `mvn -q install -DskipTests -pl marlo-web -am` |
| Checkstyle gate | `mvn -q checkstyle:check` |
| Unit tests | `mvn -q test -pl marlo-web` |

`-q` suppresses Maven's INFO stream; errors, Checkstyle violations, and test failures still print in
full. A green run is one summary line.

> **Test-suite reality:** the repository currently contains **three** JUnit 4 test classes under
> `marlo-web/src/test/java/`. There is no meaningful automated regression suite. Verification for
> most changes is Checkstyle + build + manual QA. `/akili-test` should expect to *author* a suite for
> a module, not to extend an existing one.

### Docker

The root `Dockerfile` builds the **deployment** image, not a local dev loop — it has no database and
no volume mounts, so a code change means a full rebuild. Use the run script for development. A
development compose file (MySQL + app) does not exist; adding one would be an `enhancement` spec.

---

## 7. Open Questions

| # | Question | Owner |
|---|---|---|
| INF-1 | Is IaC (Terraform/CDK) maintained in a separate repository, or is infrastructure managed manually? Record the location once known. | IBD / DevOps |
| INF-2 | Is there a sanctioned, sanitized local seed dump agents may reference by name? | IBD |
| INF-3 | Staging is documented as "being built out to mirror Production" — is that complete? | IBD / DevOps |
| INF-4 | SonarCloud is referenced in the TRD, but no workflow file exists in this checkout. Is static analysis running inside the Jenkins job? | Tech lead |
