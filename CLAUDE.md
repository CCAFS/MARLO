# Claude / AI Agent Guide for MARLO

This file is the entry point for Claude Code (and any AI assistant) working on MARLO. It is intentionally short. It points to the documents that hold the real content.

> **Constitutional baseline:** the repository follows a Spec-Driven Development (SDD) methodology rooted in the documents listed below. New work MUST be aligned with this baseline. Deviations require an explicit, justified Decision Log entry inside the relevant module spec.

---

## Read first, in this order

1. **[AGENTS.md](./AGENTS.md)** — the operational ground truth for this repo: language, file headers, code style, Checkstyle config, migration naming, specificity workflow, file organization, run scripts. *Never skip this file.*
2. **[docs/prd.md](./docs/prd.md)** — what MARLO is, who it serves, what success looks like. Use it to anchor product decisions.
3. **[docs/ux-ui/design.md](./docs/ux-ui/design.md)** — UI / UX system blueprint: information architecture, screen inventory, navigation, layout patterns, component inventory, accessibility commitments.
4. **[docs/trd/trd.md](./docs/trd/trd.md)** — technical blueprint: modules, data model, API surface, save pipeline, security model, observability, testing strategy, ADR snapshots.
5. **[docs/infrastructure.md](./docs/infrastructure.md)** — environments blueprint: target environment, core components, deployment strategy, network & security, and the **Local Environment contract** (how to start the local stack). Consult it instead of guessing run commands.
6. **[docs/specs/general-setup/](./docs/specs/general-setup/)** — methodology templates for module specs (`requirements.md`, `design.md`, `task.md`, and `family.md` for spec families).
7. **[reports/ai-context/](./reports/ai-context/)** — operational runbooks for the most touched flows (frontend composition, save validation matrix, persistence replication, struts routing, interceptor playbook). Treat these as authoritative companions when modifying critical sections.
8. **[EXPANDABLE_BLOCKS_AGENT_INSTRUCTIONS.md](./EXPANDABLE_BLOCKS_AGENT_INSTRUCTIONS.md)** — debugging runbook for accordion-style list UIs.

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
| Onboard a new spec area | Pick the right taxonomy folder under `docs/specs/`: `domain/`, `enhancement/`, `bugfix/`, or `epic/` |
| Start the local stack (db / backend / frontend) | `docs/infrastructure.md` §6 "Local Environment" — never guess a run command |
| Change deployment, environments, or infrastructure | `docs/infrastructure.md` §1–§5 (governed; not improvised by agents) |
| Split a proposal into child specs | `docs/specs/general-setup/family.md` + author `family.md` in the parent spec folder |
| Decide which model to run a phase on | `## Model Routing` in this file or `AGENTS.md` |
| Decide which skills a task needs | `## Skill Map` in this file or `AGENTS.md` |

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
- `docs/specs/domain/<module>/` — module-level specs aligned with MARLO domain areas (e.g., `domain/projects/`, `domain/deliverables/`, `domain/innovations/`, `domain/oicrs/`, `domain/powb/`, `domain/annual-report/`, `domain/qa/`, `domain/admin/`, `domain/auth/`, `domain/bi/`, `domain/ai-services/`, `domain/parameters/`).
- `docs/specs/enhancement/<feature>/` — cross-cutting enhancements that don't belong to a single domain (e.g., `enhancement/dark-mode/`, `enhancement/design-tokens/`, `enhancement/a11y-automation/`).
- `docs/specs/bugfix/<slug>/` — structured bug-driven specs that need explicit traceability beyond a normal commit.
- `docs/specs/epic/<name>/` — multi-spec initiatives (e.g., `epic/java-17-cutover/`, `epic/tenant-onboarding/`, `epic/legacy-modules-retirement/`).
- `docs/specs/kaizen/` — **not a spec folder.** One kaizen entry file per spec, written by the `kaizen` skill's Record phase. Its `README.md` is scaffolding and is never counted as an entry.
- `docs/specs/audits/` — **not a spec folder.** One drift report per `/akili-audit` run. Its `README.md` is scaffolding and is never counted as a report.

Each spec folder MUST contain three files: `requirements.md`, `design.md`, `task.md`, all following the templates under `docs/specs/general-setup/`. A spec folder MAY also contain `agent-context.md`: a compact, agent-first summary for routine work. When it exists, read `agent-context.md` before the longer spec files and open the longer files only when the change is broad, architectural, risky, or needs formal traceability.

A spec folder that was **chunked into child specs** MUST also contain `family.md` — the manifest tracking child order, dependencies, and status (template: `docs/specs/general-setup/family.md`). Its absence means the spec is flat, with no added obligations. The manifest is a closed set: no AKILI command creates a child spec folder without a prior manifest row.

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

## The `.agents/` multi-agent harness

`.agents/` holds the four personas that power the AKILI harness: `leader.md`, `implementer.md`,
`reviewer.md`, and `tester.md`. `/akili-execute` runs the Leader → Implementer → Reviewer rework loop
from them; `/akili-test` runs the Leader → Tester(s) harness. They are plain Markdown and
tool-agnostic, resolved relative to the current working directory.

The personas carry **scoped** MARLO context, not one shared bundle — each holds only what its role
consumes, so a change to the build command touches one file rather than four. The Reviewer is
independent by construction: a different model than the Implementer, and no write tools.

Model bindings live in `.claude/agents/akili-*.md` (thin wrappers that read these personas). Editing
a persona needs no wrapper change; changing a model needs only the wrapper.

---

## Constitutional change process

A change to any of these documents is a constitutional event:

- `docs/prd.md`
- `docs/ux-ui/design.md`
- `docs/trd/trd.md`
- `docs/infrastructure.md`
- `.agents/*.md` (Leader, Implementer, Reviewer, Tester personas)
- `docs/specs/general-setup/*`
- `AGENTS.md`
- This file (`CLAUDE.md`)

Constitutional changes MUST:

1. Be proposed via an `epic` spec under `docs/specs/epic/<name>/`.
2. Include an explicit Decision Log entry in `requirements.md`.
3. Be reviewed by the IBD team lead and at least one of: PMU lead, QA lead, Tech lead.
4. Land in `staging` only after approval; production promotion follows the standard release pipeline.

---

## Default Branch & Shared-File Write Discipline

**Default Branch: staging**

This pin is the primary source every AKILI command's branch test compares the checked-out branch against. It is `staging`, not `main`, deliberately: `main` is release-only and never receives direct commits (Hard rule 9), so `staging` is MARLO's real integration branch and the only correct destination for lifecycle side-effect writes.

**On a spec branch, lifecycle side-effect writes never touch shared files.** Kaizen standardizations, `/akili-archive` guide and TRD syncs, and `/akili-audit` outputs MUST NOT edit:

- root `CLAUDE.md` / `AGENTS.md`
- `.agents/*.md` personas
- packaged AKILI templates
- `docs/trd/trd.md`

Each would-be edit is **recorded as a pending item** and applied on `staging`.

**Exemption:** a file that the spec's *approved* `tasks.md` names as its own deliverable is the spec's product, not a side effect — implement it under the normal Implementer → Reviewer flow. The boundary was drawn at specify time, where it was reviewable. Never dispatch a shared-file edit the approved task list does not name; never withhold one it does.

This rule binds every command and every agent, including sessions that never load a persona.

---

## Concurrency

These failures happen in the filesystem, not in the diff, so no code review catches them. They bind every session that opens this repo.

| Rule | Why |
|---|---|
| **One AKILI session per checkout.** Additional sessions use `git worktree` | Two Leaders in one tree interleave commits, overwrite each other's `tasks.md` transitions, and append to the same `execution.md` — the audit trail stops being an account of what happened |
| **Never run a measurement command while a delegated agent is active** | A Maven build, a Cargo/Tomcat run, or an E2E pass is not read-only: it competes for `target/` directories, port 8080, and the `marlo-dev.properties` the run scripts rewrite. A build taken while an Implementer is compiling is not a slow measurement — it is a **wrong** one |
| **Measure after the worker reports, never beside it** | You already wait for the completion report. Take the measurement in that quiet window |

MARLO-specific sharpener: `scripts/run-marlo-java17.sh` **kills any `cargo:run` process, deletes `marlo-{utils,data,web}/target`, and rewrites `marlo-dev.properties`** before it builds. Running it while another agent works does not merely perturb that agent's environment — it destroys its build output and mutates its configuration mid-task. Never run it concurrently.

---

## Agent-Lean Verification Commands

Record these in their failure-only form. A green run should cost one summary line; everything above it is waste paid on every verification of every task.

| Gate | Command | Status |
|---|---|---|
| Compile | `mvn -q install -DskipTests -pl marlo-web -am` | **Required.** The authoritative gate — MARLO is a compiled monolith and a broken build is the failure that matters |
| Checkstyle | `mvn -q checkstyle:check` | **Required.** Config `configuration/marlo-checkstyle.xml`. A hard gate, not advisory |
| Unit tests | `mvn -q -pl marlo-web test` | Run and claim **only** when the task actually authored tests |

**The asymmetry rule travels with the commands:** `-q` suppresses *passing* noise only. **Failures always print complete and verbatim** — they are evidence. Never truncate, summarize, or paraphrase a compile error, a Checkstyle violation, or a test failure.

> **Honest state of testing in MARLO (verified 2026-08-27):** 3 JUnit 4 test files exist in the whole repository (`marlo-web/src/test/java/`), one with its only test body commented out. There is no Surefire configuration, no JaCoCo, and every run script builds with `-DskipTests`. **A green test run is not meaningful verification evidence here.** Do not report a task verified on that basis. Closing this gap is tracked as an open question in `docs/trd/trd.md`.

---

## Module Guides

Root `CLAUDE.md` and `AGENTS.md` are the parent guides and always apply. A module gets a child guide **only when its conventions genuinely diverge from the root** — a different stack, test runner, boundary set, or domain rule. Child guides stay thin and never duplicate root rules: inheritance means children only *add* or *narrow*.

| Child guide | Scope |
|---|---|
| _(none)_ | MARLO's five Maven modules share one stack, one build, one style config, and one Checkstyle gate, so no module's conventions diverge from the root. No child guides exist. |

A child guide that is not listed above is **drift**. Agents load the root guides plus the child guide of the module they are touching.

---

## CodeGraph

`.codegraph/codegraph.db` exists in this checkout (indexed 2026-08-24). Prefer CodeGraph over broad file scanning for symbol lookup, architecture context, callers/callees, and impact analysis — MARLO is large and repetitive, which is exactly where a graph beats `grep`.

**The `codegraph` CLI is not currently on PATH.** Install with `npm install -g @colbymchenry/codegraph`, then `codegraph init -i` to refresh the index. Until then, fall back to `Glob` / `Grep` and file reads — **absence changes the tool, never the scope.** A scan that silently shrank because the graph was missing is the failure this note exists to prevent; report degraded confidence instead.

Do not commit generated CodeGraph databases. `.codegraph/.gitignore` already excludes the `.db`.

---

## Model Routing

**Criteria-first:** match the model to the *dominant cognitive demand* of the phase, not to a hardcoded name. Guiding principles:

- **ARCHITECT = BUILDER** — the model that designed it is capable of building it.
- **author ≠ auditor** — the Reviewer MUST run on a different model than the Implementer. This is a correctness constraint, not a preference.
- Reserve deep-reasoning tiers for propose / specify / verify **and the orchestrating Leader**.
- Fast & cheap is for archive and formatting **only** — `tasks.md` decomposition is **T1**, not cheap formatting.

### Capability tiers

| Tier | Demand |
|---|---|
| **T1 Architect** | Architecture reasoning, **task decomposition**, and **live orchestration judgment** (decomposition in flight, runtime skill selection, FAIL adjudication, pivot decisions) |
| **T2 Coder** | Maximum coding / test-authoring throughput |
| **T3 Auditor** | Deep, independent review — must differ from the T2 model that wrote the code |
| **T4 Context-Ingest** | Large-context repository ingestion and summarization |
| **T5 Fast-Cheap** | Archive, formatting, mechanical rewrites |
| **T6 Multimodal** | Image / screenshot / visual-diff reasoning |

### Phase to tier

| AKILI phase | Tier |
|---|---|
| `/akili-constitution` — repo ingestion | T4 |
| `/akili-constitution` — baseline synthesis | T1 |
| `/akili-propose` | T1 |
| `/akili-specify` — requirements, design, **tasks decomposition** | T1 |
| `/akili-execute` — **Leader** | T1 (orchestration judgment: writes no code, but selects skills, adjudicates FAILs, decides pivots) |
| `/akili-execute` — **Implementer** | T2 |
| `/akili-execute` — **Reviewer** | T3 — **must differ from the Implementer model** |
| `/akili-test` — **Leader** | T1 (orchestration) |
| `/akili-test` — **Tester(s)** | T2 — prefer a model different from the Implementer (author ≠ tester) |
| `/akili-validate` | T3 |
| `/akili-audit` | T4 then T1 |
| `/akili-archive` | T5 |
| Visual / screenshot verification | T6 |

### Model registry

**Updated: 2026-08** · Confirmed host: **Claude Code only**.

| Tier | Claude Code | OpenCode | Antigravity | Fallback |
|---|---|---|---|---|
| T1 Architect | `opus` | `<CONFIRM SLUG>` | `<CONFIRM SLUG>` (Gemini Pro family) | `sonnet` |
| T2 Coder | `sonnet` | `<CONFIRM SLUG>` | `<CONFIRM SLUG>` (Gemini Flash family) | `opus` |
| T3 Auditor | `opus` | `<CONFIRM SLUG>` | `<CONFIRM SLUG>` (Gemini Pro family) | `sonnet` |
| T4 Context-Ingest | `opus` (1M context) | `<CONFIRM SLUG>` | `<CONFIRM SLUG>` | `sonnet` |
| T5 Fast-Cheap | `haiku` | `<CONFIRM SLUG>` | `<CONFIRM SLUG>` (Gemini Flash family) | `sonnet` |
| T6 Multimodal | `opus` | `<CONFIRM SLUG>` | `<CONFIRM SLUG>` (Gemini Pro family) | `sonnet` |

**CLI invocation per host** — a registry naming *which* host without naming *how* forces every future session to guess a binary, and the product name is not reliably the command:

| Host | Invocation |
|---|---|
| Claude Code | `claude` |
| OpenCode | `<CONFIRM INVOCATION>` — not installed / not confirmed |
| Antigravity | `agy` (**not** `antigravity`) — not installed / not confirmed |

**Cross-host dispatch:** reach across hosts before degrading within one, but **only for a real capability gap** — a cross-host spawn costs a fresh context, which a one-tier difference does not repay. The standing case is *T6 Multimodal to Antigravity (Gemini vision)*. This records the routing **preference only, never the dispatcher**: whether a given machine has an agent orchestrator installed is a property of that machine, not of this project.

**Alias-first rule.** The Claude Code column uses floating aliases (`opus`, `sonnet`, `haiku`) on purpose: they always resolve to the latest generation, so this registry survives model churn with zero edits. Pin a dated model ID only to deliberately freeze a version, and record why.

**To change models, edit only this registry table.** Never pin a dated model name where a floating alias exists. Model selection is **guidance only** in command prompts — never add `model:` to command frontmatter. Enforced bindings live only in the agent wrappers under `.claude/agents/`.

> Two columns are placeholders because only Claude Code is confirmed for this project. **The columns are never dropped** — this repository outlives any one tool, and a teammate or a later session opening MARLO in another host needs a row to read.


### Enforced bindings (agent wrappers)

The registry above is guidance. These wrappers make it enforcement for the multi-agent fan-out, where
most tokens are spent. Each is a thin file that reads its persona from `.agents/` — the persona stays
the single source of truth, so editing a persona needs no wrapper change.

| Wrapper | Persona | Model | Tools |
|---|---|---|---|
| `.claude/agents/akili-leader.md` | `.agents/leader.md` | `opus` (T1) | unrestricted |
| `.claude/agents/akili-implementer.md` | `.agents/implementer.md` | `sonnet` (T2) | unrestricted |
| `.claude/agents/akili-reviewer.md` | `.agents/reviewer.md` | `opus` (T3) | **`Read, Grep, Glob`** |
| `.claude/agents/akili-tester.md` | `.agents/tester.md` | `opus` (T2 fallback) | unrestricted |

**`author != auditor` is structural on both axes.** Model axis: the Reviewer runs `opus` while the
Implementer runs `sonnet`. Write axis: the Reviewer is the **only** wrapper with a `tools` allowlist,
so an auditor tempted to fix what it is auditing is stopped by configuration rather than by
discipline. It gets no `Bash` (the Leader extracts and passes the diff) and no `Write`/`Edit` (the
whole point); `Read`/`Grep`/`Glob` remain because the persona permits opening a source file when the
diff alone is genuinely ambiguous.

**The Tester runs the T2 *fallback* (`opus`), not the T2 primary.** Claude Code exposes three aliases,
so `sonnet` for both Implementer and Tester would collapse author != tester. `haiku` would be
under-capable here: MARLO has no test precedent for anything touching Hibernate, Struts actions, or
the save pipeline, so the Tester is authoring the first one of its kind rather than copying a pattern.

**Restrict the Reviewer and nowhere else.** A Leader, Implementer, or Tester carrying an allowlist is
a broken role, not a stricter one.
### Effort dial

Effort is the second, **per-task** routing dimension, orthogonal to the tier: the tier picks the model, effort picks how hard it thinks on *this* task.

**By signal:**

| Signal | Effort |
|---|---|
| Trivial / mechanical (i18n key, label change, a `custom_parameters` value) | `low` |
| Standard scope (a new section field, a validator rule, a straightforward specificity) | `medium` |
| Complex — algorithm, concurrency, security, ambiguity (phase replication, interceptor stack, save-pipeline ordering) | `xhigh` |
| Correctness-critical (a migration touching production data, an auth path) | `max` |

**Default by role:** T1 propose / specify / Leader is `high`. T2 Implementer / Tester is `medium`, flex by task. T3 Reviewer is `high`. T5 archive is `low`.

**Rework rule:** bump effort one level on every retry.

**Tier and effort rule:** never `max` a cheaper tier — escalate the **tier** instead.

**Re-baseline rule:** these effort defaults are **per-generation** and must be swept — `medium` / `high` / `xhigh` on a real spec — whenever the underlying model generation changes. The tier mapping survives model churn; these defaults do not. A task arriving under-specified (a `[~]` resume, a post-Pivot retry) starts one level higher.

**Effort is not a verbosity dial.** Lowering effort does not reliably shorten output. Fix long reports in the brief — via the `caveman` or `cognitive-doc-design` skills — never by dropping effort.

---

## Skill Map

AKILI binds skills at three levels. `core` and `conditional` skills are already wired into the command prompts; **`stack` skills are never hard-referenced by commands** — this map is how they reach the agents.

MARLO's stack is Java 17 / Struts 2 / Hibernate-JPA / FreeMarker / jQuery / Maven / Tomcat 9. The packaged frontend and cloud stack skills (`angular-developer`, `nestjs-expert`, `shadcn-ui`, `tailwind-design-system`, `react-doctor`, `vercel-react-best-practices`, `aws-serverless`) **do not apply** and are deliberately absent from this table.

| Skill | Applies To | When to load |
|---|---|---|
| `api-design-principles` | The Spring MVC REST layer under `marlo-web/src/main/java/.../rest/` (`/api/*`) | Load when adding or changing a REST endpoint, resource shape, or response contract. Not for Struts `.do` actions |
| `error-handling-patterns` | The save pipeline (`Action.validate()` then `Validator` then manager save chain) plus the interceptor stack | Load when adding a validator, changing validation flow, or touching interceptor error paths. Pair with `reports/ai-context/save-validation-matrix.md` and `reports/ai-context/interceptor-validator-playbook.md` |

**During `/akili-specify`, derive each task's required skills from this map. During `/akili-execute` and `/akili-test`, the Leader assigns these skills and the Implementer / Tester MUST load them before writing code or tests.**

Non-stack skills that remain relevant to MARLO work and are reached the normal way (by trigger, not by this map): `systematic-debugging` for any bug or unexpected behavior, `software-architect` for TRD changes, `cognitive-doc-design` for documentation, `tdd` when the Leader assigns it to a logic-heavy task, and `kaizen` at archive time.

---

## Contact

- IBD Team — Alliance of Bioversity International and CIAT.
- MARLO Support: `Marlosupport@cgiar.org`.
- GitHub: `https://github.com/CCAFS/MARLO`.
