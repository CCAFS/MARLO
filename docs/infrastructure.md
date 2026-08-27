# MARLO — Infrastructure & Environments Blueprint

**Status:** Living document. Version 1.0 (Constitutional Baseline).
**Owner:** IBD Team — Alliance of Bioversity International and CIAT.
**Last Updated:** 2026-08-27
**Related:** [docs/trd/trd.md](./trd/trd.md) §1.2, §13.2, §14 · [docs/prd.md](./prd.md) §7.3 · [AGENTS.md](../AGENTS.md) · [CLAUDE.md](../CLAUDE.md)

> **Architecture tier — this document derives from it, never precedes it.** Per
> [`docs/trd/trd.md`](./trd/trd.md) §13.2 and **ADR-14**, MARLO's **transactional core is LITE**: one
> deployable WAR, one primary MySQL, one session cache, no orchestrator, no per-service scaling, and
> no infrastructure-as-code layer. Analytics (Microsoft Fabric + Power BI) and AI services (AWS
> Bedrock + OpenSearch + Lambda) are **read-side ROBUST escalations** that never write to the
> transactional schema. Every shape in this document is a consequence of that decision, not an
> independent choice.

**What is documented vs. what is open.** Sections 1–3 and 6 rest on in-repository authority
(TRD §1.2, PRD §7.3, and the workflow/script/Dockerfile artifacts) and are safe to act on. Section 4
is partly open — application-level security is documented, network-level security is not.
§ *Confirmation Needed* lists every remaining gap; nothing in this document presents an unknown as
settled.

---

## Boundary rule (read this first)

| Environment class | Status | Who may act |
|---|---|---|
| **Local** (§6) | **Disposable** | Agents may freely start, seed, and reset it to verify work |
| **Test / Staging / Production** (§1–§5) | **Governed** | Deployments follow this document. **Agents never improvise them** |

An agent that needs to verify a change uses the local stack. An agent that believes a deployment is
required reports that to the user; it does not trigger one. Everything in §1–§5 was decided at
constitution time precisely so it is not re-decided mid-task.

---

## 1. Target Environment

**Hybrid: AWS for Production, on-premise CIAT Palmira for Test, AWS-mirroring Staging in build-out.**

This is **documented**, not inferred — the authoritative in-repository sources are
[`docs/trd/trd.md`](./trd/trd.md) §1.2 (deployment topology) and [`docs/prd.md`](./prd.md) §7.3.4
(operational acceptance). This document restates their shape and adds the pipeline and local detail;
where they and the repository's build artifacts disagree, § *Discrepancies* below says so rather than
picking a winner.

| Environment | Location | Access | Role |
|---|---|---|---|
| **Production** | **AWS, Virginia region** | Public | Live system of record for all Global Units |
| **Staging** | AWS, mirroring Production | **GlobalProtect VPN** | Pre-production validation. **In build-out** — not yet a full mirror |
| **Test** | **On-premise, CIAT Palmira** | **FortiClient VPN** | First validation target. PRD §7.3.4: new features MUST be validated in Test before reaching `staging` |
| **Local** | Developer machine | — | §6. Disposable |

**Production composition** (TRD §1.2): multiple EC2 instances with dedicated roles — core
application, frontend, CLARISA integration, automation, backup — plus AWS Lambda for specific
AI/automation tasks, RDS MySQL as the system of record, and S3 for backups and documents.

**The promotion path has three gates, and they are not the same as the branch triggers.** PRD §7.3.4
requires Test validation before `staging`; PRD §7.3.2 requires `main` to be merge-only from
`staging`. The Jenkins triggers in §3 are the *mechanism*; these are the *policy*.

### Discrepancies between documented topology and repository artifacts

Three artifacts in this checkout are not accounted for in TRD §1.2. Recording them as open questions
is deliberate — asserting either reading would make this document wrong in a way agents would act on.

| Artifact | What it implies | Status |
|---|---|---|
| `Dockerfile` (Tomcat 9 + JDK 17, WAR as `ROOT.war`, Glowroot and Tomcat config baked in) | A container deployment path | TRD §1.2 describes EC2 instances, not containers. **Whether any governed environment runs this image is unconfirmed** — it may be a local/experimental path or an undocumented deployment route |
| `memcached-session-manager` + `spymemcached` + kryo serializers on the Tomcat classpath (`Docker/*.jar`) | Externalized HTTP sessions across multiple Tomcat instances | Consistent with TRD §1.2's multi-EC2 Production, but **memcached is not named in TRD §1.2 or §7 (Integration Points)**. Its endpoints and failure behavior are undocumented (TRD §14.9 item 4) |
| Glowroot agent (`Docker/glowroot/`, attached in `Docker/setenv.sh`) | JVM-level APM | Confirmed by TRD §9.4 and scenario OB-2. Attached via the image's `setenv.sh` — so **on a non-container deployment its attachment mechanism is unconfirmed** |

The common thread: `Docker/` and `Dockerfile` describe a deployment shape that the TRD's topology
section does not mention. One of the two is stale. Resolving which is § *Confirmation Needed* item 1.

---

## 2. Core Components

| Component | Version / detail | Source |
|---|---|---|
| Servlet container | Tomcat **9** | TRD §1; `Dockerfile`; Cargo local runner. Dependency floor Catalina ≥ 9.0.96 (TRD §8.5) |
| JVM | JDK **17** (Temurin in the image) | `marlo-parent/pom.xml` is the **authoritative** source for the active Java level |
| Application artifact | Single WAR. In the container image: `ROOT.war`, with a copy retained at `/usr/local/tomcat/backup/ROOT.war` | `Dockerfile` |
| System of record | **MySQL on AWS RDS** (Production). Schema managed by **Flyway** under `marlo-web/src/main/resources/database/migrations/` | TRD §1.2, §3.1 |
| Compute (Production) | **Multiple EC2 instances** by role (core app, frontend, CLARISA integration, automation, backup) + **AWS Lambda** for AI/automation | TRD §1.2 |
| Object store | **AWS S3** — daily backups and document storage | TRD §1.2; PRD §4.2 (RPO < 24 h), §7.3.3 |
| Session store | **memcached** via `memcached-session-manager` 2.3.2, kryo-serialized | Repository artifacts only — **not in TRD §1.2/§7**. See § Discrepancies |
| APM | **Glowroot** | TRD §9.4; scenario OB-2 |
| Analytics | **Microsoft Fabric** Lakehouse (Bronze/Silver/Gold) → **Power BI** | TRD ADR-7, ADR-14; PRD §4.2 refresh SLAs |
| AI services | **AWS Bedrock** (Claude, Titan) + **Amazon OpenSearch** vector indices + Lambda | TRD ADR-8, ADR-14 |
| CI orchestration | **Jenkins** (`automation.prms.cgiar.org`, job `marlo-java-17`), triggered by GitHub Actions | `.github/workflows/jenkins-trigger-java-*.yml`; TRD §1.2 |
| Auth directory | **CGIAR Active Directory** via Apache Shiro realm | TRD §8.1 |

### Why the session store is load-bearing

`memcached-session-manager` is not a cache optimization — it externalizes HTTP session state so any
Tomcat instance can serve any request. That is the **statelessness tactic** behind scalability
scenario SC-4 and availability scenario AV-3 (TRD §14). Two consequences bind design work:

1. **Every object placed in the session must be kryo-serializable.** A non-serializable object fails
   at replication time — in a multi-instance environment, and **not** locally on a single Cargo
   instance. This is scenario **TS-3**: a defect class that currently reaches a governed environment
   before it becomes detectable.
2. **Memcached is a hard runtime dependency** of any multi-instance environment, yet its unavailable
   behavior is undefined (TRD scenario **AV-4**, open item 4).

### Architecture tier consequence

Per TRD §13.2 (ADR-14): the **transactional core is LITE** — one deployable WAR, one primary
database, one session cache, no orchestrator and no per-service scaling. Fabric/Power BI and the AWS
AI services are **read-side ROBUST escalations** that never write to the transactional schema. That
read-only boundary is why the platform's component count does not make the core robust-tier, and it is
the boundary this document expects deployments to preserve.

---

## 3. Deployment Strategy

```
git push origin <dev | staging | main>
        │
        ▼
GitHub Actions
  dev     → .github/workflows/jenkins-trigger-java-dev.yml      (on push to "dev")
  staging → .github/workflows/jenkins-trigger-java-staging.yml  (on push to "staging")
  main    → .github/workflows/jenkins-trigger-java-prod.yml     (on push to "main")
        │  authenticated POST, secrets JENKINS_USERNAME / JENKINS_API_TOKEN
        ▼
Jenkins  automation.prms.cgiar.org  job "marlo-java-17"
        │  mvn clean install  →  marlo-web/target/*.war
        ▼
Target environment  (Tomcat 9 / JDK 17)   ← topology per §1
```

**Confirmed properties:**

- **Branch-triggered promotion.** Three workflow files, one per branch. **The production trigger is
  `main`, not a branch named `prod`** — the file is `jenkins-trigger-java-prod.yml` but its trigger is
  `push: branches: ["main"]`. The filename and the branch differ; do not infer a `prod` branch exists.
- **The branch *is* the deployment gate.** A push to `dev`, `staging`, or `main` triggers a deployment.
  This is why Hard rule 9 (never commit directly to `main`; feature branches from `staging`) is an
  operational safeguard and not a style preference — **a careless push is a deployment.**
- **All three triggers POST the same Jenkins URL with no branch parameter.** Each workflow computes a
  `BRANCH_NAME` (stripping an `aiccra-` prefix) and logs it, but never sends it — so **Jenkins, not the
  workflow, decides what it builds and where it deploys.** Anyone reasoning about which environment a
  push updates must read the Jenkins job configuration; this repository does not contain that answer
  (§ Confirmation Needed item 5).
- **`workflow_dispatch` is enabled on all three**, so any of the three can also be triggered manually
  from the GitHub UI — including the one that targets production.
- **Tests are skipped in the image build** (`mvn clean install -Dmaven.test.skip=true` in the
  `Dockerfile`). Given the state of the suite (TRD §10, scenario TS-2), CI is a **build gate, not a
  correctness gate**. The real quality gates are Checkstyle and human review (PRD §7.2).

**Policy layered on top of the mechanism** — the triggers above do not enforce these; people and
review do:

1. New features MUST be validated in **Test** (CIAT Palmira) before reaching `staging` (PRD §7.3.4).
2. `main` is **merge-only from `staging`**. Direct commits to production are forbidden (PRD §7.3.2).
3. Feature branches start from `staging` and merge back into it. `dev` is unstable, for integration
   experiments only (Hard rule 9).
4. Merges to integration branches produce a Slack success/failure notification (PRD §7.3.1,
   scenario AV-5).

**Explicitly absent — do not assume otherwise:**

- No Terraform, CDK, Pulumi, Helm, or Kubernetes manifests anywhere in the repository.
- No `docker-compose*.yml`.
- No blue/green, canary, or automated-rollback mechanism in version control. The retained
  `backup/ROOT.war` copy in the container image is the only rollback affordance visible here, and
  applying it is manual.
- No database migration gate in CI: Flyway runs against the application's configured datasource, so
  **who runs migrations against a governed environment is not answered by this repository**.

**Infrastructure is therefore not reproducible from this repository.** Server provisioning and the
Jenkins job definition live outside version control. This is the single largest infrastructure risk
recorded here (§ Confirmation Needed item 10), and it is what blocks the partner-self-hosting goal
(PRD §4.1 goal 7, TRD scenario SC-3).

---

## 4. Network & Security Architecture

**Confirmed from the repository:**

| Control | Detail |
|---|---|
| CI credentials | `JENKINS_USERNAME` / `JENKINS_API_TOKEN` as GitHub Actions secrets. Never in the repository |
| Runtime credentials | `marlo-${spring.profiles.active}.properties` under `marlo-web/src/main/resources/config/`. **Gitignored.** Bootstrap locally from `marlo-test.properties` |
| Spring profiles | `dev`, `api`, `pro`, `test` — the active profile selects the properties file |
| Application authorization | Apache Shiro + the interceptor stack. See `docs/trd/trd.md` § Security & Authorization Model and `reports/ai-context/interceptor-validator-playbook.md` |
| Multi-tenancy | Per-Global-Unit behavior via `parameters` + `custom_parameters` (specificities). **An authorization boundary, not only a feature toggle** — see `AGENTS.md` "Specificity Implementation Guide" |
| Dependency floors | Security-aligned minimums are pinned in `marlo-parent/pom.xml` and MUST NOT be downgraded: Tomcat Catalina ≥ 9.0.96, Spring Framework ≥ 5.3.39, Jackson ≥ 2.17.x. See `CLAUDE.md` Hard rule 11 |
| Known pinned exceptions | HikariCP 2.4.6 and Groovy 2.4.8 are deliberate, unresolved modernization exceptions. Do not claim newer versions |

**Hard rule, repeated here because this is where it gets violated:** never commit a
`marlo-*.properties` profile file. It carries database credentials.

**VPN access is confirmed** (TRD §1.2, PRD §7.3.4): Test (CIAT Palmira) is reached via **FortiClient
VPN**, Staging via **GlobalProtect VPN**. Production is publicly reachable.

**Unconfirmed and needed** (see § Confirmation Needed): TLS termination point and certificate
management; load-balancer product; network segmentation between the Tomcat tier, RDS, and memcached;
whether RDS is reachable outside its VPC; RTO and the restore procedure; secret delivery and rotation
policy; who holds Jenkins job-configuration access.

---

## 5. Infrastructure Rules & Constraints

1. **Governed environments are never touched by an agent.** An agent may build, run, and reset the
   local stack (§6) without asking. It may not trigger Jenkins, push to `dev`, `staging`, or `main`, or
   modify a deployed environment.
2. **Branch discipline gates deployment.** Never commit directly to `main`. Feature branches start
   from `staging` and merge back into it. `dev` is unstable, for integration experiments only.
   (`CLAUDE.md` Hard rule 9.) Because pushes to these branches *are* deployment triggers, a careless
   push is a deployment.
3. **Schema changes ship only as Flyway migrations**, named
   `V<major>_<minor>_<patch>_<YYYYMMDD>_<HHMM>__<Description>.sql`. Never modify a schema out of band,
   and never edit a migration that has already run in a governed environment.
4. **Java 17 is the active level.** `marlo-parent/pom.xml` is the verification source, not this
   document and not a run script.
5. **No dependency downgrades** without explicit approval (§4).
6. **Session-stored objects must be kryo-serializable** (§2). A violation surfaces only in a
   multi-instance environment.
7. **Infrastructure changes are a constitutional event.** They require a Decision Log entry and a
   revision of this document — never an undocumented server-side adjustment.

---

## 6. Local Environment

The methodology defines a **contract, not a tool**. MARLO has no Docker Compose file, so the native
Maven + Cargo route below is the **primary** route, and the container route is the fallback.

### 6.1 Primary route — Maven + embedded Cargo/Tomcat

| Element | Value |
|---|---|
| Command (macOS / Linux) | `./scripts/run-marlo-java17.sh` — **run from the repo root** |
| Command (Windows) | `scripts\run-marlo-java17.bat`, or the `.sh` under Git Bash |
| Pre-check | JDK 17 must be resolvable. The script probes `JAVA_HOME`, then `/usr/libexec/java_home -v 17`, then `/usr/lib/jvm/java-17-openjdk`, then `/Library/Java/JavaVirtualMachines/*`. On failure it exits with install instructions — it does not fail silently |
| Database | **Not started by the script.** Requires a reachable MySQL configured in `marlo-web/src/main/resources/config/marlo-dev.properties` |
| Seed / reset data | No seeding script exists in the repository. Schema comes from Flyway migrations; **obtain a development dataset from the IBD team** |
| Health check | `curl -s -o /dev/null -w "%{http_code}" http://localhost:8080/marlo-web/` returns `2xx` or `3xx`. The script already polls this and opens a browser when ready |
| URL | `http://localhost:8080/marlo-web/` |
| Port | **8080**, HTTP |

**What the script does before building — this is why it is destructive:**

1. Detects and `pkill`s any running `cargo:run` process.
2. Deletes `marlo-utils/target`, `marlo-data/target`, `marlo-web/target`, with retries when locked
   (macOS: clears extended attributes and ACLs).
3. Rewrites `marlo-dev.properties` via `scripts/update-marlo-dev-java17.sh` so base URLs are
   `http://localhost:8080`.
4. Runs `mvn install -DskipTests -pl marlo-web -am` (no `clean` phase — step 2 already cleaned).
5. Starts `mvn -pl marlo-web cargo:run` and opens a browser once the app responds.

> **Never run this while another agent is working in the same checkout.** Steps 1–3 kill processes,
> delete build output, and mutate configuration. See the `## Concurrency` section in `AGENTS.md` /
> `CLAUDE.md`.

**Java 8 legacy variant:** `scripts/run-marlo-java8.sh` exists for legacy Java 8 branches and uses
**HTTPS on port 8443** (`https://localhost:8443/marlo-web/`). Do not use it on Java 17 branches — it
rewrites `marlo-dev.properties` to the 8443/HTTPS convention, which then breaks a Java 17 run until
the Java 17 update script is run again.

### 6.2 Fallback route — container image

| Element | Value |
|---|---|
| Pre-check | `docker info`. If the daemon is off or Docker is absent, surface it and offer the §6.1 route — never block silently |
| Build | `docker build -t marlo:local .` |
| Run | `docker run -p 8080:8080 marlo:local` |
| URL | `http://localhost:8080/` (the WAR deploys as `ROOT.war`, so **no `/marlo-web/` path prefix** — unlike §6.1) |
| Database | Still external. The image contains no database and no memcached |
| Caveat | The image bakes `Docker/*.xml` Tomcat config and the Glowroot agent, and builds with `-Dmaven.test.skip=true`. Useful for verifying deployment shape; slower than §6.1 for an edit-compile-check loop |

### 6.3 Verification without running the app

Most agent verification does not need a running server. Prefer these — they are the gates recorded in
the `## Agent-Lean Verification Commands` section of the root guides:

| Gate | Command |
|---|---|
| Compile | `mvn -q install -DskipTests -pl marlo-web -am` |
| Checkstyle | `mvn -q checkstyle:check` |
| Unit tests | `mvn -q -pl marlo-web test` — only meaningful when the task authored tests |

`-q` suppresses passing noise only. **Failure output is evidence: print it complete and verbatim.**

### 6.4 Known local-vs-governed gaps

An agent verifying locally must know what local cannot show:

| Gap | Consequence |
|---|---|
| Single Tomcat instance, **no memcached** | Kryo session-serialization failures cannot reproduce locally |
| **No load balancer** | Sticky-session and proxy-header behavior is untested |
| **HTTP, not HTTPS** (Java 17 route) | Mixed-content and secure-cookie issues cannot reproduce locally |
| Context path `/marlo-web/` locally vs `ROOT` in the image | A hardcoded absolute path may work in one and fail in the other |
| **No Glowroot agent** | Performance characteristics are not comparable |
| Development dataset, not production scale | Query-performance and phase-replication timing differ |

---

## Confirmation Needed

Sections 1–3 and 6 rest on in-repository authority. These are the remaining gaps. Until each is
resolved, no spec should cite it as settled.

| # | Open item | Why it matters |
|---|---|---|
| 1 | **`Dockerfile` / `Docker/` vs. TRD §1.2.** Does any governed environment run the container image, or is it a local/experimental path? One of the two is stale | An agent reasoning about deployment shape currently has two incompatible answers in the repository |
| 2 | **memcached** endpoints and topology per environment, and behavior when unavailable | An undocumented hard runtime dependency. TRD scenario **AV-4** / open item 4 |
| 3 | **Load balancer**: product, TLS termination point, certificate issuance and renewal | Not named anywhere in the repository, yet implied by multi-EC2 Production |
| 4 | **Network segmentation**: is RDS reachable outside its VPC? Which security groups gate the Tomcat tier? | Determines the blast radius in security scenario **SE-5** |
| 5 | **Jenkins job `marlo-java-17`**: what it builds and where it deploys, given all three workflows POST the same URL with no branch parameter. Who holds configuration access? | The pipeline's actual behavior is not derivable from this repository (§3) |
| 6 | **RTO and the restore procedure.** RPO is set at < 24 h (PRD §4.2); recovery *time* has no target and the procedure is undocumented | TRD scenario **AV-1** / open item 1 |
| 7 | **Rollback** beyond the manually retained `backup/ROOT.war` | No automated rollback exists in version control (§3) |
| 8 | **Secrets delivery and rotation**: how `marlo-*.properties` reaches a governed environment, and the rotation SLA | TRD scenario **SE-5** / open item 5 |
| 9 | **Development dataset**: the sanctioned way to obtain one and its anonymization status | Blocks §6.1 for any new developer or agent environment |
| 10 | **Infrastructure reproducibility.** No Terraform/CDK/Helm exists, so provisioning and the Jenkins job definition live outside version control | Blocks scalability scenario **SC-3** (partner self-hosting), a stated PRD goal (§4.1 goal 7) |

Item 10 is the largest structural risk recorded here: **infrastructure is not reproducible from this
repository.** Items 1–5 are all downstream of it — each is a fact that would be self-documenting if
the environments were declared as code.

---

## Decision Log

- **2026-08-27** — Created during `/akili-constitution` (Active AKILI-SPECS / Safe Update mode).
  *Rationale:* the repository already contained the authoritative topology in `docs/trd/trd.md` §1.2
  and `docs/prd.md` §7.3.4, but no single document connected it to the CI pipeline, the security
  baseline, and the local-run contract. This document is that connection, not a new decision.
- **2026-08-27** — Sections 1–3 and 6 are stated as documented; §4 network-level security and the ten
  items above are recorded as **explicitly open** rather than inferred. *Rationale:* an infrastructure
  document is acted on by agents. A confidently wrong topology is worse than an acknowledged gap,
  because the gap gets resolved in the next cycle while the fabrication gets built against.
- **2026-08-27** — The native Maven + Cargo route is recorded as the **primary** local route and the
  container route as **fallback**, inverting the methodology's usual Docker-first preference.
  *Rationale:* no `docker-compose*.yml` exists; the `Dockerfile` builds a *deployment* image rather
  than a development stack (no database, no memcached, tests skipped); and `scripts/run-marlo-java17.sh`
  is the route the team already documents in `scripts/README.md`.
- **2026-08-27** — Three artifacts (`Dockerfile`, memcached JARs, Glowroot placement) are recorded as
  **discrepancies** against TRD §1.2 rather than reconciled. *Rationale:* picking a winner between two
  in-repository sources without owner input would erase the signal that one of them is stale.
