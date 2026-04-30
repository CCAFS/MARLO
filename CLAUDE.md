# Claude / AI Agent Guide for MARLO

This file is the entry point for Claude Code (and any AI assistant) working on MARLO. It is intentionally short. It points to the documents that hold the real content.

> **Constitutional baseline:** the repository follows a Spec-Driven Development (SDD) methodology rooted in the documents listed below. New work MUST be aligned with this baseline. Deviations require an explicit, justified Decision Log entry inside the relevant module spec.

---

## Read first, in this order

1. **[AGENTS.md](./AGENTS.md)** — the operational ground truth for this repo: language, file headers, code style, Checkstyle config, migration naming, specificity workflow, file organization, run scripts. *Never skip this file.*
2. **[docs/prd.md](./docs/prd.md)** — what MARLO is, who it serves, what success looks like. Use it to anchor product decisions.
3. **[docs/system-design/design.md](./docs/system-design/design.md)** — UI / UX system blueprint: information architecture, screen inventory, navigation, layout patterns, component inventory, accessibility commitments.
4. **[docs/detailed-design/detailed-design.md](./docs/detailed-design/detailed-design.md)** — technical blueprint: modules, data model, API surface, save pipeline, security model, observability, testing strategy, ADR snapshots.
5. **[docs/specs/general-setup/](./docs/specs/general-setup/)** — methodology templates for module specs (`requirements.md`, `design.md`, `task.md`).
6. **[reports/ai-context/](./reports/ai-context/)** — operational runbooks for the most touched flows (frontend composition, save validation matrix, persistence replication, struts routing, interceptor playbook). Treat these as authoritative companions when modifying critical sections.
7. **[EXPANDABLE_BLOCKS_AGENT_INSTRUCTIONS.md](./EXPANDABLE_BLOCKS_AGENT_INSTRUCTIONS.md)** — debugging runbook for accordion-style list UIs.

---

## When to consult which document

| Task | Primary document(s) |
|---|---|
| Understand product scope, personas, success metrics | `docs/prd.md` |
| Add a UI screen, component, or navigation change | `docs/system-design/design.md` + `reports/ai-context/frontend-composition-map.md` |
| Touch a save path, validator, interceptor stack, or REST endpoint | `docs/detailed-design/detailed-design.md` + `reports/ai-context/save-validation-matrix.md` + `reports/ai-context/interceptor-validator-playbook.md` + `reports/ai-context/struts-critical-routing-catalog.md` |
| Touch a `ManagerImpl` save / delete chain | `docs/detailed-design/detailed-design.md` §3, §5 + `reports/ai-context/persistence-replication-managerimpl.md` |
| Add a feature flag conditional on Global Unit | `AGENTS.md` "Specificity Implementation Guide" |
| Add a database column, table, index, or migration | `AGENTS.md` "Database Migrations" + `docs/detailed-design/detailed-design.md` §3 |
| Add or change i18n strings | `AGENTS.md` "File Organization" + `marlo-web/src/main/resources/global.properties` (and `custom/*.properties` per program) |
| Build or update a module spec | `docs/specs/general-setup/requirements.md` + `design.md` + `task.md` |
| Onboard a new spec area | Pick the right taxonomy folder under `docs/specs/`: `domain/`, `enhancement/`, `bugfix/`, or `epic/` |

---

## Spec taxonomy under `docs/specs/`

- `docs/specs/general-setup/` — methodology templates (read-only for individual specs; update only as a constitutional change).
- `docs/specs/domain/<module>/` — module-level specs aligned with MARLO domain areas (e.g., `domain/projects/`, `domain/deliverables/`, `domain/innovations/`, `domain/oicrs/`, `domain/powb/`, `domain/annual-report/`, `domain/qa/`, `domain/admin/`, `domain/auth/`, `domain/bi/`, `domain/ai-services/`).
- `docs/specs/enhancement/<feature>/` — cross-cutting enhancements that don't belong to a single domain (e.g., `enhancement/dark-mode/`, `enhancement/design-tokens/`, `enhancement/a11y-automation/`).
- `docs/specs/bugfix/<slug>/` — structured bug-driven specs that need explicit traceability beyond a normal commit.
- `docs/specs/epic/<name>/` — multi-spec initiatives (e.g., `epic/java-17-cutover/`, `epic/tenant-onboarding/`, `epic/legacy-modules-retirement/`).

Each spec folder MUST contain three files: `requirements.md`, `design.md`, `task.md`, all following the templates under `docs/specs/general-setup/`.

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
9. **Branching:** never commit directly to `AICCRA`. Feature branches start from `aiccra-staging` and merge back into it. `aiccra-dev` is unstable and used only for integration experiments.
10. **Run scripts:** branches containing `java17` / `java_17` use `scripts/run-marlo-java17.sh`; otherwise `scripts/run-marlo-java8.sh`.
11. **Dependency floors (post-Jan 2026 SETI baseline) MUST NOT be downgraded:** Struts2 ≥ 6.4.0, Tomcat Catalina ≥ 9.0.96, Spring Framework ≥ 5.3.39, Jackson ≥ 2.17.x, HikariCP ≥ 5.x, Springdoc OpenAPI (replaces Springfox/Swagger 2.9.2), Groovy ≥ 2.4.21.
12. **Do not commit credential files.** `marlo-${profile}.properties` is gitignored; bootstrap from `marlo-test.properties`.

---

## How to start a new piece of work

1. Read this file, `AGENTS.md`, and the relevant ai-context docs.
2. Locate or create the spec folder under `docs/specs/...`.
3. Draft `requirements.md` (use `docs/specs/general-setup/requirements.md` as the template).
4. Draft `design.md` (use `docs/specs/general-setup/design.md` as the template).
5. Draft `task.md` (use `docs/specs/general-setup/task.md` as the template).
6. Have the spec reviewed before implementation begins.
7. Implement against the task plan; keep `task.md` up to date with verification notes.
8. Update relevant `reports/ai-context/*.md` files when the change alters routing, validation, replication, or composition contracts.

---

## Constitutional change process

A change to any of these documents is a constitutional event:

- `docs/prd.md`
- `docs/system-design/design.md`
- `docs/detailed-design/detailed-design.md`
- `docs/specs/general-setup/*`
- `AGENTS.md`
- This file (`CLAUDE.md`)

Constitutional changes MUST:

1. Be proposed via an `epic` spec under `docs/specs/epic/<name>/`.
2. Include an explicit Decision Log entry in `requirements.md`.
3. Be reviewed by the IBD team lead and at least one of: PMU lead, QA lead, Tech lead.
4. Land in `aiccra-staging` only after approval; production promotion follows the standard release pipeline.

---

## Contact

- IBD Team — Alliance of Bioversity International and CIAT.
- MARLO Support: `Marlosupport@cgiar.org`.
- GitHub: `https://github.com/CCAFS/MARLO`.
