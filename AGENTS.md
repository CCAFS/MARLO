# MARLO AI Agents

## Language
- All code, identifiers, comments, and technical content must be written in English.
- Replies can be in the same language as the user prompt.

## Project Overview
MARLO (Managing Agricultural Research for Learning and Outcomes) is an online management platform for CGIAR Research Programs.

## Constitutional Baseline (AKILI-SPECS)

These documents are the project's constitution. Future `/akili-propose`, `/akili-specify`, `/akili-execute`, `/akili-validate`, `/akili-test`, and `/akili-archive` work relies on them. Changing any of them is a **constitutional event** — see `CLAUDE.md` → *Constitutional change process*.

| Document | What it is | Consult it when |
|---|---|---|
| [`docs/prd.md`](./docs/prd.md) | Product requirements — problem, personas, goals, scope, success metrics | Anchoring any product decision, scoping, or prioritization |
| [`docs/ux-ui/design.md`](./docs/ux-ui/design.md) | UI/UX system blueprint — IA, screens, navigation, layout patterns, design tokens, components, accessibility | Adding or changing a screen, component, or navigation |
| [`docs/trd/trd.md`](./docs/trd/trd.md) | Technical requirements — modules, data model, API surface, save pipeline, security, observability, testing, ADRs | Touching a save path, validator, interceptor, endpoint, entity, or manager chain |
| [`docs/infrastructure.md`](./docs/infrastructure.md) | Environments from laptop to PROD; **§6 is the Local Environment contract** | Starting/seeding/verifying the local stack, or reasoning about CI, deploys, or environments |
| [`docs/specs/general-setup/`](./docs/specs/general-setup/) | Methodology templates: `requirements.md`, `design.md`, `task.md`, `family.md` | Authoring or updating any module spec |
| [`.agents/`](./.agents/) | Multi-agent personas: `leader.md`, `implementer.md`, `reviewer.md`, `tester.md` | Running `/akili-execute` or `/akili-test` |

Module specs live under `docs/specs/` in four taxonomy folders — `domain/`, `enhancement/`, `bugfix/`, `epic/` — plus `kaizen/` (retrospective entries) and `audits/` (drift reports). Each spec folder carries `requirements.md`, `design.md`, `task.md`; optionally `agent-context.md` (read it first for routine work) and `family.md` (only when a proposal was split into child specs).

**Never skip this file.** `CLAUDE.md` is the entry-point index; this file is the operational ground truth for how MARLO code is actually written.

## Project Structure
- `marlo-core`: Core configuration and initialization.
- `marlo-data`: Data layer (JPA entities and repositories).
- `marlo-web`: Web app (actions, REST endpoints, FreeMarker/FTL views, JavaScript).
- `marlo-utils`: Utility classes.
- `marlo-parent`: Parent POM and dependency management.

## Technology Stack
- Java (backend and web layer)
- Maven (build and dependency management)
- Struts 2 (web framework)
- Hibernate/JPA (ORM)
- FreeMarker/FTL (server-side templating)
- Tomcat 9 (local container via Cargo)
- JavaScript (frontend)
- SQL migrations

## Web Layer: Struts 2 vs Spring MVC
- **Struts 2**: Traditional web actions (`.do`, `.json`), FreeMarker/FTL views, and interceptors. Main config: `marlo-web/src/main/resources/struts.xml` plus module-specific `struts-*.xml` files (e.g. `struts-projects.xml`, `struts-admin.xml`, `struts-api.xml`).
- **Spring MVC**: REST API under `/api/*`. These paths are excluded from Struts via `struts.action.excludePattern`. Controllers use `@RestController` and `@RequestMapping`.

## Configuration & Properties
- Location: `marlo-web/src/main/resources/config/`
- Files `marlo-dev.properties`, are in `.gitignore`; create them locally or use a template (e.g. `marlo-test.properties`).
- Spring profile selects the file: `marlo-${spring.profiles.active}.properties`. Active profiles: `dev`, `api`, `pro`, `test`.

## Required File Header (Java)
Use the GPL header for new Java files (as specified in the project setup guide):

```text
/*****************************************************************
 * This file is part of Managing Agricultural Research for Learning & 
 * Outcomes Platform (MARLO).
 * MARLO is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * at your option) any later version.
 * MARLO is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 * You should have received a copy of the GNU General Public License
 * along with MARLO. If not, see <http://www.gnu.org/licenses/>.
 *****************************************************************/
```

## Code Style & Formatting
### Java
- Formatter: `configuration/ccafs-java-style-config.xml`
- Import order: `configuration/ccafs-java-style.importorder`
- Indentation: 2 spaces
- Line length: 120
- Braces on the same line
- Use blocks for `if/while/for/do` (always)

### JavaScript
- Formatter: `configuration/ccafs-javascript-style.xml`
- Indentation: 2 spaces
- Braces on the same line
- Use blocks for `if/while/for/do` (always)

## Linting & Validation (Checkstyle)
- Config: `configuration/marlo-checkstyle.xml`
- Runs via `mvn checkstyle:checkstyle` or `mvn checkstyle:check`
- Key rules:
  - Max line length: 120
  - Max file length: 3500 lines
  - Naming conventions for methods, local variables, and type params
  - Package name regex: `^[a-z]+(\\.[a-z][a-z0-9]*)*$`
  - Padding rules for empty `for` initializer/iterator and method parameter spacing

## Database Migrations
- All schema changes must be done via migration scripts.
- Location: `marlo-web/src/main/resources/database/migrations/`
- Follow the existing naming pattern in that directory.
- Naming format (examples in repo): `V<major>_<minor>_<patch>_<YYYYMMDD>_<HHMM>__<Description>.sql`

## Specificity Implementation Guide
Use this workflow when creating a new specificity (feature flag based on `parameters` + `custom_parameters`).

### 1. Create migration for `parameters`
- Create one migration in `marlo-web/src/main/resources/database/migrations/`.
- Follow the same style used by existing specificity migrations (direct `INSERT ... VALUES`).
- Add one row per `global_unit_type_id` used by CRP/Platform/Center (`1`, `3`, `4`).
- Use `category = '2'` (Specificities) and `format = '1'` (boolean-like).

Template:

```sql
INSERT INTO parameters (global_unit_type_id, `key`, `description`, `format`, default_value, category)
VALUES ( '1', '<specificity_key>', '<Specificity description>', '1', 'false', '2');

INSERT INTO parameters (global_unit_type_id, `key`, `description`, `format`, default_value, category)
VALUES ( '3', '<specificity_key>', '<Specificity description>', '1', 'false', '2');

INSERT INTO parameters (global_unit_type_id, `key`, `description`, `format`, default_value, category)
VALUES ( '4', '<specificity_key>', '<Specificity description>', '1', 'false', '2');
```

### 2. Create/Update migration for `custom_parameters` values
- If the specificity must be enabled for a specific Global Unit, add inserts/updates in `custom_parameters`.
- Use `value = 'true'` or `value = 'false'` depending on rollout.
- Link using `parameter_id` from `parameters.key`.

Template:

```sql
INSERT INTO custom_parameters (`parameter_id`, `global_unit_id`, `value`, `created_by`, `is_active`, `active_since`, `modified_by`, `modification_justification`)
VALUES (
  (SELECT id FROM parameters WHERE `key` = '<specificity_key>' AND global_unit_type_id = 1),
  <global_unit_id>,
  'true',
  '3',
  '1',
  CURRENT_TIMESTAMP,
  '3',
  'Enable <specificity_key>'
);
```

### 3. Add constants in APConstants
- Add the new key in both constants files:
  - `marlo-data/src/main/java/org/cgiar/ccafs/marlo/config/APConstants.java`
  - `marlo-web/src/main/java/org/cgiar/ccafs/marlo/config/APConstants.java`
- Constant name should be uppercase snake case and value must match `parameters.key` exactly.

Template:

```java
public static final String <SPECIFICITY_CONSTANT_NAME> = "<specificity_key>";
```

### 4. Backend usage (Java)
- Prefer using APConstants constant instead of hardcoded strings.
- Typical usage is through `BaseAction.hasSpecificities(...)`.

Example:

```java
if (this.hasSpecificities(APConstants.<SPECIFICITY_CONSTANT_NAME>)) {
  // feature enabled behavior
} else {
  // fallback behavior
}
```

### 5. Frontend usage (FTL/JS)
- In FTL views, use `action.hasSpecificities('<specificity_key>')` to toggle sections.
- Keep behavior explicit with `[#if] ... [/#if]` blocks.
- Do not create new JSP views; JSP is limited to legacy/bootstrap entry points such as `index.jsp`.

Example:

```ftl
[#if action.hasSpecificities('<specificity_key>')]
  [#-- enabled content --]
[/#if]
```

or for hide-on-true behavior:

```ftl
[#if !action.hasSpecificities('<specificity_key>')]
  [#-- default visible content --]
[/#if]
```

### 6. Validation checklist
- Migration file name follows `V<major>_<minor>_<patch>_<YYYYMMDD>_<HHMM>__<Description>.sql`.
- `parameters.key` and APConstants value are identical.
- Constant added in both APConstants files.
- Backend uses APConstants (no hardcoded key literals in Java).
- Frontend condition matches expected behavior (`show when true` or `hide when true`).
- If rollout is required, corresponding `custom_parameters` values are created.

## File Organization (Quick Reference)
- Actions: `marlo-web/src/main/java/org/cgiar/ccafs/marlo/action/`
- Base Action: `marlo-web/src/main/java/org/cgiar/ccafs/marlo/action/BaseAction.java`
- REST APIs: `marlo-web/src/main/java/org/cgiar/ccafs/marlo/rest/`
- Validators: `marlo-web/src/main/java/org/cgiar/ccafs/marlo/validation/`
- Interceptors: `marlo-web/src/main/java/org/cgiar/ccafs/marlo/interceptor/`
- Struts config: `marlo-web/src/main/resources/struts.xml`, `struts-*.xml`
- i18n: `marlo-web/src/main/resources/global.properties`, `custom/*.properties` (per CRP)
- SQL scripts: `marlo-web/src/main/resources/database/`
- Web resources: `marlo-web/src/main/webapp/`

## Common Tasks
- When adding new features, check similar implementations first.
- When modifying DB schema, add a migration file in the migrations directory.
- When adding actions, follow the existing action structure.
- When adding REST endpoints, follow existing REST API patterns.

## Domain Notes
- MARLO content and workflows reference CGIAR Research Programs (CRPs) across multiple resources and actions.

## Run Scripts by Current Java Version (Local Development)
Scripts in `scripts/` run MARLO locally (build, update properties, start server). The current active Java version for this repository is Java 17.
- Use the Java 17 run script in `scripts/`:
  - macOS/Linux: `scripts/run-marlo-java17.sh`
  - Windows: `scripts/run-marlo-java17.bat` (if provided; otherwise use `.sh` in Git Bash)
- Use the Java 8 script only for legacy Java 8 branches or profiles that explicitly require it.
  - macOS/Linux: `scripts/run-marlo-java8.sh`
  - Windows: `scripts/run-marlo-java8.bat` (if provided; otherwise use `.sh` in Git Bash)

## Operational Context Documentation
Use these documents as the primary operational context for semi-autonomous work in critical modules:

### Frontend & Composition
- `reports/ai-context/frontend-composition-map.md`

### Save & Validation
- `reports/ai-context/save-validation-matrix.md`

### Persistence & Replication
- `reports/ai-context/persistence-replication-managerimpl.md`

### Routing & Interceptors
- `reports/ai-context/struts-critical-routing-catalog.md`
- `reports/ai-context/interceptor-validator-playbook.md`

### Scope Guardrails
- Internal MARLO flows should prioritize Struts `.do` actions and existing FTL composition.
- `struts-json` usage is punctual and should only be extended when there is an existing JSON pattern in the same module.
- `/api/*` (Spring MVC) is out of scope for internal flow implementation and remains reference-only in this context pack.

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

