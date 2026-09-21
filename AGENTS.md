# MARLO AI Agents

## Language
- All code, identifiers, comments, and technical content must be written in English.
- Replies can be in the same language as the user prompt.

## Project Overview
MARLO (Managing Agricultural Research for Learning and Outcomes) is an online management platform for CGIAR Research Programs.

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
