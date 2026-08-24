# Claude / AI Agent Guide for MARLO

This file is the entry point for Claude Code (and any AI assistant) working on MARLO. It is intentionally short. It points to the documents that hold the real content.

> **Constitutional baseline:** the repository follows a Spec-Driven Development (SDD) methodology rooted in the documents listed below. New work MUST be aligned with this baseline. Deviations require an explicit, justified Decision Log entry inside the relevant module spec.

---

## Read first, in this order

1. **[AGENTS.md](./AGENTS.md)** — the operational ground truth for this repo: language, file headers, code style, Checkstyle config, migration naming, specificity workflow, file organization, run scripts. *Never skip this file.*
2. **[docs/prd.md](./docs/prd.md)** — what MARLO is, who it serves, what success looks like. Use it to anchor product decisions.
3. **[docs/ux-ui/design.md](./docs/ux-ui/design.md)** — UI / UX system blueprint: information architecture, screen inventory, navigation, layout patterns, component inventory, accessibility commitments.
4. **[docs/trd/trd.md](./docs/trd/trd.md)** — technical blueprint: modules, data model, API surface, save pipeline, security model, observability, testing strategy, ADR snapshots.
5. **[docs/infrastructure.md](./docs/infrastructure.md)** — environments blueprint from laptop to PROD, plus the **Local Environment contract** (§6). Consult it instead of guessing how to start or verify the stack.
6. **[docs/specs/general-setup/](./docs/specs/general-setup/)** — methodology templates for module specs (`requirements.md`, `design.md`, `task.md`, and `family.md` for spec families).
7. **[.agents/](./.agents/)** — the AKILI multi-agent personas (`leader.md`, `implementer.md`, `reviewer.md`, `tester.md`) that power `/akili-execute` and `/akili-test`.
8. **[reports/ai-context/](./reports/ai-context/)** — operational runbooks for the most touched flows (frontend composition, save validation matrix, persistence replication, struts routing, interceptor playbook). Treat these as authoritative companions when modifying critical sections.
9. **[EXPANDABLE_BLOCKS_AGENT_INSTRUCTIONS.md](./EXPANDABLE_BLOCKS_AGENT_INSTRUCTIONS.md)** — debugging runbook for accordion-style list UIs.

---

## When to consult which document

| Task | Primary document(s) |
|---|---|
| Understand product scope, personas, success metrics | `docs/prd.md` |
| Add a UI screen, component, or navigation change | `docs/ux-ui/design.md` + `reports/ai-context/frontend-composition-map.md` |
| Touch a save path, validator, interceptor stack, or REST endpoint | `docs/trd/trd.md` + `reports/ai-context/save-validation-matrix.md` + `reports/ai-context/interceptor-validator-playbook.md` + `reports/ai-context/struts-critical-routing-catalog.md` |
| Touch a `ManagerImpl` save / delete chain | `docs/trd/trd.md` §3, §5 + `reports/ai-context/persistence-replication-managerimpl.md` |
| Add a feature flag conditional on Global Unit | `AGENTS.md` "Specificity Implementation Guide" |
| Add a database column, table, index, or migration | `AGENTS.md` "Database Migrations" + `docs/trd/trd.md` §3 |
| Add or change i18n strings | `AGENTS.md` "File Organization" + `marlo-web/src/main/resources/global.properties` (and `custom/*.properties` per program) |
| Work in an existing domain module | `docs/specs/domain/<module>/agent-context.md` first, when present; then inspect the target source files |
| Build or update a module spec | `docs/specs/general-setup/requirements.md` + `design.md` + `task.md` |
| Split a proposal into child specs | `docs/specs/general-setup/family.md` (spec-family manifest) |
| Start, seed, or verify the local stack | `docs/infrastructure.md` §6 — **Local Environment** |
| Deploy, or reason about environments / CI | `docs/infrastructure.md` §1–5 |
| Onboard a new spec area | Pick the right taxonomy folder under `docs/specs/`: `domain/`, `enhancement/`, `bugfix/`, or `epic/` |

---

## Mandatory Agent-Context Rule

Before implementing or editing code in any existing domain module, Claude (and any AI assistant) MUST check for and
read `docs/specs/domain/<module>/agent-context.md` first when the file exists.

- If `agent-context.md` exists: treat it as the module's first-stop operational guide.
- If it does not exist: continue with `AGENTS.md`, this guide, and target source inspection.
- For broad, architectural, or high-risk changes: after reading `agent-context.md`, also read the full module spec
	(`requirements.md`, `design.md`, `task.md`).

---

## Spec taxonomy under `docs/specs/`

- `docs/specs/general-setup/` — methodology templates (read-only for individual specs; update only as a constitutional change).
- `docs/specs/domain/<module>/` — module-level specs aligned with MARLO domain areas (e.g., `domain/projects/`, `domain/deliverables/`, `domain/innovations/`, `domain/oicrs/`, `domain/powb/`, `domain/annual-report/`, `domain/qa/`, `domain/admin/`, `domain/auth/`, `domain/bi/`, `domain/ai-services/`).
- `docs/specs/enhancement/<feature>/` — cross-cutting enhancements that don't belong to a single domain (e.g., `enhancement/dark-mode/`, `enhancement/design-tokens/`, `enhancement/a11y-automation/`).
- `docs/specs/bugfix/<slug>/` — structured bug-driven specs that need explicit traceability beyond a normal commit.
- `docs/specs/epic/<name>/` — multi-spec initiatives (e.g., `epic/java-17-cutover/`, `epic/tenant-onboarding/`, `epic/legacy-modules-retirement/`).
- `docs/specs/kaizen/` — one kaizen entry file per spec, written by the `kaizen` skill's Record phase. Its `README.md` is scaffolding, never an entry.
- `docs/specs/audits/` — one drift report per `/akili-audit` run. Its `README.md` is scaffolding, never a report.

Each spec folder MUST contain three files: `requirements.md`, `design.md`, `task.md`, all following the templates under `docs/specs/general-setup/`. A spec folder MAY also contain `agent-context.md`: a compact, agent-first summary for routine work. When it exists, read `agent-context.md` before the longer spec files and open the longer files only when the change is broad, architectural, risky, or needs formal traceability.

A spec folder MAY also contain `family.md` — but **only when a proposal was actually split into child specs**. It is the manifest tracking build order, dependencies, and status across the family (template: `docs/specs/general-setup/family.md`). Its absence means the spec is flat and carries zero added obligations.

---

## Hard rules (do not violate without explicit user approval)

1. **Phased data is forward-only.** Saves replicate to current and future phases; past phases are immutable.
2. **Save pipeline pattern is non-negotiable** for critical sections: `Action.validate()` guarded by `if (save)` → `Validator` → manager save chain.
3. **Spring MVC owns `/api/*`.** Struts is excluded from this prefix. Do not introduce new `*.json` Struts paths unless an existing pattern in the same module already requires it.
4. **Specificities go through `parameters` + `custom_parameters`** with constants in *both* `APConstants.java` files (in `marlo-data/` and `marlo-web/`). The constant value MUST equal the `parameters.key`.
5. **All schema changes ship as Flyway migrations** under `marlo-web/src/main/resources/database/migrations/` with the `V<major>_<minor>_<patch>_<YYYYMMDD>_<HHMM>__<Description>.sql` naming.
6. **GPL header on every new Java file** (template in `AGENTS.md`).
7. **Code style:** 2-space indent, 120 char line limit, braces on same line, mandatory blocks for `if/while/for/do`, max file length 3500 lines. Checkstyle (`mvn checkstyle:check`) is a gate.
8. **English only** in code, identifiers, and inline comments. User-facing strings MUST be i18n-keyed.
9. **Branching:** never commit directly to `main`. Feature branches start from `staging` and merge back into it. `dev` is unstable and used only for integration experiments.
10. **Run scripts:** MARLO currently uses Java 17. Use `scripts/run-marlo-java17.sh` (or `.bat`) for local runs. `marlo-parent/pom.xml` is the verification source for the active Java level. Use `scripts/run-marlo-java8.sh` only for legacy Java 8 branches/profiles.
11. **Dependency baseline:** do not downgrade dependency versions declared in `marlo-parent/pom.xml` without explicit approval. Struts2 version must be validated against `marlo-parent/pom.xml` property `struts2.version` whenever it is updated. Current security-aligned floors in this checkout include Tomcat Catalina ≥ 9.0.96, Spring Framework ≥ 5.3.39, and Jackson ≥ 2.17.x. Modernization exceptions still present in the POM are HikariCP 2.4.6 and Groovy 2.4.8; do not claim HikariCP ≥ 5.x or Groovy ≥ 2.4.21 until those upgrades are implemented and validated.
12. **Do not commit credential files.** `marlo-${profile}.properties` is gitignored; bootstrap from `marlo-test.properties`.

---

## How to start a new piece of work

1. Read this file and `AGENTS.md`.
2. If the target domain has `docs/specs/domain/<module>/agent-context.md`, read it first for routine work.
3. Open the relevant ai-context docs only for the contracts the change touches (routing, validation, replication, composition, expandable blocks).
4. For broad, architectural, risky, or formally tracked work, locate or create the spec folder under `docs/specs/...`.
5. Draft or update `requirements.md` (use `docs/specs/general-setup/requirements.md` as the template).
6. Draft or update `design.md` (use `docs/specs/general-setup/design.md` as the template).
7. Draft or update `task.md` (use `docs/specs/general-setup/task.md` as the template).
8. Have the spec reviewed before implementation begins when the change requires formal approval.
9. Implement against the task plan; keep `task.md` up to date with verification notes when a formal task plan exists.
10. Update relevant `reports/ai-context/*.md` files when the change alters routing, validation, replication, or composition contracts.

---

## Constitutional change process

A change to any of these documents is a constitutional event:

- `docs/prd.md`
- `docs/ux-ui/design.md`
- `docs/trd/trd.md`
- `docs/specs/general-setup/*`
- `AGENTS.md`
- This file (`CLAUDE.md`)

Constitutional changes MUST:

1. Be proposed via an `epic` spec under `docs/specs/epic/<name>/`.
2. Include an explicit Decision Log entry in `requirements.md`.
3. Be reviewed by the IBD team lead and at least one of: PMU lead, QA lead, Tech lead.
4. Land in `staging` only after approval; production promotion follows the standard release pipeline.

---

## AKILI-SPECS Operating Rules

These bind **every** session that opens this repository, including ones that never load an
`.agents/` persona.

### Default Branch: staging

`staging` is the branch every AKILI command's branch test compares the checked-out branch against.

This is deliberate and differs from git's own default (`origin/HEAD → origin/main`): per hard rule
9, nobody commits to `main` directly — feature branches start from `staging` and merge back into
`staging`, and `main` receives release promotions only. Lifecycle side-effect writes therefore land
on `staging`, not on `main`.

> **Push policy (current):** commits stay **local**. Do not `git push` unless the user explicitly
> asks in that session.

### Shared-file write discipline (spec branches)

On a spec branch, **lifecycle side-effect writes never edit shared files.** Kaizen standardizations,
`/akili-archive` guide/TRD syncs, and `/akili-audit` outputs must not touch:

- `CLAUDE.md`, `AGENTS.md` (root or nested)
- `.agents/*.md` personas
- packaged AKILI templates
- `docs/trd/trd.md`

Each would-be edit is **recorded as a pending item** and applied on `staging`.

**Exemption:** files that an approved `tasks.md` names as the spec's own deliverable. Those are the
product, protected by the normal review flow — not a side effect. Apply the test in that order: if
the file is named in the approved task, write it; if it is not, it is a side effect.

### Concurrency convention

| Rule | Why |
|---|---|
| **One AKILI session per checkout.** Additional sessions use `git worktree` | Two Leaders in one tree interleave commits, overwrite each other's `tasks.md` transitions, and append to the same `execution.md`. The failure is filesystem-level — no diff review catches it |
| **No measurement command while a delegated agent is active** — no build, benchmark, or E2E run | A `mvn install` running concurrently with a worker's own build contends for CPU and for `target/`, and produces numbers that measure the contention, not the change |
| **Serialize anything touching a shared writer** (see `.agents/leader.md` → *MARLO Directory Boundaries*) | `APConstants.java` ×2, `global.properties`, `BaseAction.java`, `struts.xml`, and the migrations directory collide across tasks |

### Verification commands (agent-lean)

Canonical commands, in their failure-only variant. A green run is one summary line.

| Purpose | Command |
|---|---|
| Build | `mvn -q install -DskipTests -pl marlo-web -am` |
| Checkstyle gate (**mandatory**) | `mvn -q checkstyle:check` |
| Unit tests | `mvn -q test -pl marlo-web` |
| Run locally | `./scripts/run-marlo-java17.sh` — see `docs/infrastructure.md` §6 |

**The asymmetry is the rule:** `-q` suppresses only Maven's passing INFO stream. **Failures print
complete and verbatim** — they are evidence, and they are never trimmed.

> **Test-suite reality:** `marlo-web/src/test/java/` holds **three** JUnit 4 classes; no other module
> has a test root, and there is no frontend or E2E harness. `mvn test` passing proves that nothing
> already covered broke — not that a new feature works. Treat behavioral claims resting on it alone
> as unproven.

### CodeGraph

`.codegraph/` **is initialized** in this repository (4,251 files · 92,602 nodes · 261,275 edges ·
144 routes). Agents should prefer it over broad file scanning:

| Need | Use |
|---|---|
| Find a symbol | `codegraph query <name>` / `codegraph_search` |
| Read one symbol + its caller/callee trail | `codegraph node <name>` / `codegraph_node` |
| Understand an area | `codegraph explore <query>` / `codegraph_context` |
| Blast radius before changing a shared symbol | `codegraph impact <symbol>` |
| Refresh after large changes | `codegraph sync` |

**Staleness:** the graph indexes the last re-index, not the current working tree. For files this
spec has already changed, the working tree is the truth — a graph answer contradicting the diff is
stale, not a defect.

**Never commit the generated database.** `.codegraph/.gitignore` covers it; only durable config such
as `.codegraph/config.json` may be committed.

---

## Module Guides

Root `CLAUDE.md` and `AGENTS.md` are the parent guides and always apply. A module gets a child guide
**only when its conventions genuinely diverge from the root** — a different stack, test runner,
boundary, or domain rule. Child guides stay thin and never restate root rules.

| Child guide | Scope |
|---|---|
| _(none today)_ | All five Maven modules share the root conventions: Java 17, 2-space/120-char style, Checkstyle, GPL header, layered persistence. No divergence warrants a child guide |

Agents load the root guides plus the child guide of the module they are touching. **A child guide
not listed in this index is drift** — add the row when you add the guide.

---

## Model Routing

Model selection is **criteria-first**: match the model to the phase's dominant demand, not to habit.
Guiding principles — *ARCHITECT = BUILDER* (the model that designed it can build it);
**author ≠ auditor** (the Reviewer must run on a different model than the Implementer); reserve
deep-reasoning tiers for propose / specify / verify **and the orchestrating Leader**; fast-and-cheap
is for archive and formatting only — **`tasks.md` decomposition is T1, not cheap formatting.**

### Capability tiers

| Tier | Name | Demand it serves |
|---|---|---|
| **T1** | Architect | Architecture reasoning, **task decomposition**, and **live orchestration judgment** — decomposition in flight, runtime skill selection, FAIL adjudication, pivot decisions |
| **T2** | Coder | Maximum implementation and test-authoring throughput |
| **T3** | Auditor | Deep, independent, adversarial review |
| **T4** | Context-Ingest | Large-repository ingestion and summarization |
| **T5** | Fast-Cheap | Mechanical formatting, archival, status sweeps |
| **T6** | Multimodal | Vision — screenshots, design comps, diagram reading |

### Phase → tier mapping

| Phase | Tier | Effort |
|---|---|---|
| `/akili-constitution` — repo ingest | T4 | `medium` |
| `/akili-constitution` — baseline synthesis | T1 | `high` |
| `/akili-propose` | T1 | `high` |
| `/akili-specify` — requirements + design | T1 | `high` |
| `/akili-specify` — `tasks.md` decomposition | **T1** (not T5) | `high` |
| `/akili-execute` — **Leader** | **T1** — orchestration judgment: writes no code, but selects skills, adjudicates FAILs, decides pivots | `high` |
| `/akili-execute` — **Implementer** | T2 | `medium`, flex by task |
| `/akili-execute` — **Reviewer** | T3 — **must differ from the Implementer model** | `high` |
| `/akili-test` — **Leader** | T1 | `high` |
| `/akili-test` — **Tester(s)** | T2 — **prefer a different model than the Implementer** (author ≠ tester) | `medium` |
| `/akili-validate` | T3 | `high` |
| `/akili-audit` | T4 → T3 | `high` |
| `/akili-archive` | T5 | `low` |
| `/akili-quick` | T2 | `low` |

### Model registry

**Updated: 2026-08**

| Tier | Claude Code | OpenCode | Antigravity | Fallback |
|---|---|---|---|---|
| **T1** Architect | `opus` | `<CONFIRM SLUG>` | `pro` `<CONFIRM>` | `sonnet` |
| **T2** Coder | `sonnet` | `<CONFIRM SLUG>` | `flash` `<CONFIRM>` | `opus` |
| **T3** Auditor | `opus` | `<CONFIRM SLUG>` | `pro` `<CONFIRM>` | `sonnet` |
| **T4** Context-Ingest | `sonnet` | `<CONFIRM SLUG>` | `flash` `<CONFIRM>` | `opus` |
| **T5** Fast-Cheap | `haiku` | `<CONFIRM SLUG>` | `flash` `<CONFIRM>` | `sonnet` |
| **T6** Multimodal | `opus` | `<CONFIRM SLUG>` | `pro` `<CONFIRM>` | `sonnet` |

**CLI invocation per host**

| Host | Command |
|---|---|
| Claude Code | `claude` |
| OpenCode | `<CONFIRM CLI>` |
| Antigravity | `<CONFIRM CLI — reportedly `agy`, not `antigravity`>` |

**Cross-host dispatch:** T6 Multimodal → Antigravity (Gemini vision), when a task genuinely needs
image understanding beyond the session's own column. Reach across hosts before degrading within one
— but only for a real capability gap. A cross-host spawn costs a fresh context, which a one-tier
difference does not repay. *(Which dispatcher is installed is a property of the machine, not of this
project, and is deliberately not recorded here.)*

> **To change models, edit only this registry table.** Never pin a dated model name where a floating
> alias exists — `opus` / `sonnet` / `haiku` always resolve to the latest generation, so the registry
> survives model churn with zero edits. Pin a dated ID only to deliberately freeze a version, and
> record why. **Model selection is guidance only in command prompts — never add `model:` to command
> frontmatter.** Enforced bindings live only in the agent wrappers under `.claude/agents/`.

### Effort dial

Effort is the **second, per-task** routing dimension, orthogonal to the tier. The tier picks the
model; effort picks how hard it thinks on *this* task.

| Signal | Effort |
|---|---|
| Trivial / mechanical (rename, copy edit, one-line guard) | `low` |
| Standard scope | `medium` |
| Complex — algorithm, concurrency, security, genuine ambiguity | `xhigh` |
| Correctness-critical — phase replication, save pipeline, migration, auth | `max` |

**Default effort by role:** T1 propose/specify/Leader `high` · T2 Implementer/Tester `medium`
(flex by task) · T3 Reviewer `high` · T5 archive `low`.

- **Rework rule:** bump effort one level on every retry.
- **Tier ↔ effort rule:** never `max` a cheaper tier — escalate the tier instead.
- **Under-specified rule:** a task arriving under-specified — a `[~]` resume, a post-Pivot retry —
  starts one level higher.
- **Re-baseline rule:** these defaults are **per-generation**. The tier mapping survives model churn;
  these numbers do not. Sweep them (`medium` / `high` / `xhigh` on a real spec) whenever the
  underlying model generation changes.
- **Effort is not a verbosity dial.** Lowering effort does not reliably shorten output. Fix long
  reports in the brief — via `caveman` or `cognitive-doc-design` — never by dropping effort.

---

## Skill Map

Stack-dependent skills are **never hard-referenced by AKILI commands** — this map is how they reach
the agents.

| Skill | Applies To | When to load |
|---|---|---|
| `api-design-principles` | `marlo-web/src/main/java/.../rest/` (Spring MVC `/api/v2/*`) | Any new or changed REST endpoint, contract, or response shape |
| `error-handling-patterns` | `marlo-web` actions, validators, REST controllers | Designing error paths, exception mapping, or REST error responses |
| `aws-serverless` | Lambda / S3 / Bedrock touchpoints (TRD §7, ADR-8) | Work crossing into the AWS automation or AI plane |
| `ai-agent-development` | `struts-ai.xml` flows, Bedrock RAG pipelines (TRD ADR-8) | Building or changing AI service behavior, prompts, or retrieval |
| `ui-ux-pro-max` | `marlo-web/src/main/webapp/` — FTL, CSS, JS | Any screen, component, layout, or accessibility change |
| `systematic-debugging` | Anywhere | Any bug, test failure, or unexpected behavior — **before** proposing a fix |
| `tdd` | Logic-heavy units with a clean seam (validators, utils, entity logic) | Assigned per task by the Leader when the unit is testable without a database |
| `software-architect` | `docs/trd/trd.md` | TRD changes, NFR scenarios, ADRs, C4 views |
| `product-manager-toolkit` | `docs/prd.md` | PRD changes, personas, success metrics |
| `cognitive-doc-design` | All persistent docs | Authoring or restructuring any document a human will read |
| `caveman` | Transient inter-agent output only | Compressing Implementer/Reviewer/Tester reports — **never** persistent documents or HITL approval gates |
| `kaizen` | `docs/specs/kaizen/` | Retrospective at `/akili-archive`, and applying the pending backlog on `staging` |

**Deliberately absent:** `angular-developer`, `nestjs-expert`, `shadcn-ui`, `tailwind-design-system`,
`react-doctor`, `vercel-react-best-practices`. MARLO's frontend is **FreeMarker + jQuery + Bootstrap
3**, served by Struts 2 — none of those stacks are present, and listing them would send agents
looking for a framework that does not exist here.

**Environment-provided rows:** none confirmed. Add a row (with its availability condition in *When to
load*) only after the user confirms the tooling is installed — the map is committed and shared, while
such tooling is per-developer. Never vendor an environment-provided skill into this repository.

> **Usage:** during `/akili-specify`, derive each task's required skills from this map. During
> `/akili-execute` and `/akili-test`, the **Leader assigns** these skills and the Implementer/Tester
> **must load them** before writing code or tests. The Leader's assignment supersedes the task file's
> recommended list.

---

## Contact

- IBD Team — Alliance of Bioversity International and CIAT.
- MARLO Support: `Marlosupport@cgiar.org`.
- GitHub: `https://github.com/CCAFS/MARLO`.
