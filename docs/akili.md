# AKILI-SPECS in MARLO — the lifecycle guide

MARLO's spec-driven work runs on **AKILI-SPECS**, a methodology distributed as an npm package: eleven `/akili-*` commands, four agent personas, and a fixed set of artifacts each phase writes into `docs/specs/`. This document is the map of that lifecycle — what each command does, what it produces, and where a human approves.

**This document describes the process. It does not restate the rules.** MARLO's binding rules live in [`CLAUDE.md`](../CLAUDE.md) and [`AGENTS.md`](../AGENTS.md); this guide links to them rather than copying them, because a third copy would drift.

| Field | Value |
|---|---|
| Methodology | AKILI-SPECS by Juan Carlos Cadavid — [jcadavid.com](https://jcadavid.com/es/methodology/) |
| License | MIT (the methodology). MARLO itself is GPL — see [`LICENSE`](../LICENSE) |
| Package | `akili-specs`, installed version **2.23.2** |
| Confirmed host | Claude Code only. OpenCode and Antigravity are recorded in the model registry but unverified here |
| Versioned in this repo | `.agents/*.md` (personas) and `.claude/agents/akili-*.md` (model wrappers) — **only these**. Commands and skills come from the package |

---

## Quick path

```bash
npm install -g akili-specs   # needs sudo on this machine (/usr/local/lib)
akili install --tool claude  # copies 11 commands + 24 skills into ~/.claude
akili doctor --tool claude   # verify
```

Then, for a bounded change:

```
/akili-propose <name>     → /akili-specify <spec-path>   → /akili-execute <spec-path>
     proposal.md               requirements + design + tasks      code, execution.md, commits
/akili-test <spec-path>   → /akili-validate <spec-path>  → /akili-archive <spec-path>
     test-report.md            validation-report.md              kaizen + archive/
```

You do not always start at the beginning — see [Where to start](#where-to-start).

---

## The eleven commands

| Command | What it does | What it writes | Tier |
|---|---|---|---|
| `/akili-constitution` | Establishes or upgrades the project baseline | `docs/prd.md`, `docs/ux-ui/design.md`, `docs/trd/trd.md`, `docs/specs/general-setup/`, root guides, `.agents/` | T4 + T1 |
| `/akili-propose` | Captures a bounded intent as a reviewable proposal; classifies bugs onto the Bug Track | `<spec>/proposal.md` | T1 |
| `/akili-specify` | Turns an approved intent into behavior contracts, design, and executable tasks | `<spec>/requirements.md`, `design.md`, `tasks.md` | T1 (T6 when visual design is in scope) |
| `/akili-execute` | Runs the Leader → Implementer → Reviewer harness, one task at a time | `<spec>/execution.md`, updates `tasks.md`, commits `[SPEC:<path>]` | T1 / T2 / T3 |
| `/akili-test` | Leader partitions suites; Tester(s) author and run them | `<spec>/test-report.md` | T1 / T2 |
| `/akili-validate` | Independent conformance audit against the requirements | `<spec>/validation-report.md` | T3 |
| `/akili-archive` | Kaizen retrospective, then moves the spec into history | `docs/specs/kaizen/<slug>.md`, `<spec>/archive-summary.md`, `docs/specs/archive/<date>-<slug>/` | T5 |
| `/akili-audit` | Drift between the code and the documented baselines | `docs/specs/audits/drift-<YYYY-MM-DD>.md` | T4 + T3 |
| `/akili-resume` | Rebuilds a session from files after a break; multi-spec dashboard | nothing (read-only) | T5 |
| `/akili-quick` | Fast-track for a genuinely trivial change; auto-escalates when it is not | `docs/specs/quick/quick-log.md` | T2 |
| `/akili-seo` | Technical SEO and generative-engine visibility audit | audit output | T3 + T5 |

Tiers map to models in the `## Model Routing` registry in [`CLAUDE.md`](../CLAUDE.md#model-routing). **Change models there and nowhere else.**

---

## Where to start

| Situation | Entry point |
|---|---|
| A small, obvious change and the baseline is strong | `/akili-specify <spec-path>` |
| Unclear, risky, cross-functional, or stakeholder-sensitive | `/akili-propose <name>` |
| A bug — start from the symptom, not the intent | `/akili-propose` (it detects the Bug Track and requires a regression test at specify time) |
| A cosmetic or copy-only tweak | `/akili-quick` |
| New repo, stale docs, or a major pivot | `/akili-constitution` |
| Returning after a break, unsure what is in flight | `/akili-resume` |

### Documentation depth

Use the lightest depth that still makes the work verifiable. This is the mechanism that keeps a label change from paying for a full spec.

| Depth | For | Capture |
|---|---|---|
| **Quick** | Trivial cosmetic/copy changes | No spec docs — one line in `quick-log.md` and a `[SPEC:quick/<name>]` commit |
| **Lite** | Small bugfixes, narrow UI tweaks | Problem, scenario, focused task, verification command |
| **Standard** | Normal features and enhancements | Requirements, scenarios, design decisions, tasks, tests |
| **Full** | Risky, cross-cutting, API, data, auth, or migration work | Alternatives, rollout, risks, observability, rollback, explicit traceability |

Lite is not lighter rigor: scenarios, done criteria, and evidence still apply. Only `/akili-quick` skips the spec documents, and it escalates anything that turns out not to be trivial.

---

## What a spec folder accumulates

```text
docs/specs/<spec-path>/
├── proposal.md            /akili-propose
├── requirements.md        /akili-specify
├── design.md              /akili-specify
├── tasks.md               /akili-specify        ← plural; the template is task.md, singular
├── execution.md           /akili-execute        ← append-only; cite headings, never line numbers
├── test-report.md         /akili-test
├── validation-report.md   /akili-validate
├── archive-summary.md     /akili-archive
├── judgment.md            the judgment-day skill, when it runs
├── agent-context.md       optional — the agent-first summary read before the long files
└── family.md              only when the spec was chunked into children
```

Not every folder holds all of these — a folder carries what its lifecycle has reached. The taxonomy (`domain/`, `enhancement/`, `bugfix/`, `epic/`, `changes/`) and the non-spec carve-outs are defined in [`CLAUDE.md`](../CLAUDE.md#spec-taxonomy-under-docsspecs).

---

## Review gates

AKILI keeps a human in control at each transition. Nothing advances past a gate on the agent's own authority.

| Gate | Confirm before moving on |
|---|---|
| Constitution | The baseline reflects the actual product and architecture |
| Proposal | Problem, scope, non-goals, and recommended approach are approved |
| Requirements | Observable behavior and scenarios are testable |
| Design | The technical approach fits this repository |
| Tasks | Work is small enough to execute and verify incrementally |
| Execution | Every completed task carries verification evidence |
| Testing | Key requirements have automated or accepted manual evidence |
| Validation | No unresolved FAIL findings remain |
| Archive | Warnings accepted; Kaizen lessons recorded and standardizations approved or deferred |

---

## The execute harness

`/akili-execute` is a three-role loop, not a single agent. The roles live in [`.agents/`](../.agents/); their model bindings live in `.claude/agents/akili-*.md`.

```text
Leader picks the next task → spawns Implementer
Implementer writes code, runs verification → reports
Leader extracts the git diff → spawns Reviewer
Reviewer returns PASS, FAIL, or FATAL_FAIL

PASS        → append execution.md, update tasks.md, commit, advance
FAIL (< 3)  → respawn Implementer with the Reviewer's structured findings
FATAL_FAIL  → abort, mark the task [~], trigger the Pivot Protocol
3× FAIL     → HALT, mark [~], present the audit trail
```

**`author ≠ auditor` is structural on two axes in MARLO:** the Reviewer runs a different model than the Implementer, *and* it is the only wrapper with a tool allowlist (`Read, Grep, Glob`) — an auditor tempted to fix what it audits is stopped by configuration, not by discipline. Details in [`CLAUDE.md`](../CLAUDE.md#enforced-bindings-agent-wrappers).

**Pivot Protocol** — when discovery proves the spec itself wrong: mark the task `[~]`, append a `## Pivot Record: <Task ID>` to `execution.md`, update the spec documents, and get sign-off before resuming. Rework attempts are never spent on a broken spec.

---

## MARLO-specific bindings

Everything here is defined elsewhere; this table is the index.

| Concern | Binding | Defined in |
|---|---|---|
| Default branch | `staging` — the destination for every lifecycle side-effect write | [`CLAUDE.md`](../CLAUDE.md#default-branch--shared-file-write-discipline) |
| Shared-file write discipline | On a spec branch, shared files are never edited; each would-be edit becomes a pending item applied on `staging` | same |
| Continuous improvement | Retrospective writes `docs/specs/kaizen/<slug>.md` on any branch; **Apply Mode runs only on `staging`** and is the sole writer of the `## Active Lessons` digest in [`kaizen-log.md`](specs/kaizen-log.md) | the `kaizen` skill |
| Verification gates | Clean compile + Checkstyle. A green test run is evidence for the code it covers, not a substitute for those gates | [`CLAUDE.md`](../CLAUDE.md#agent-lean-verification-commands) |
| Skills per task | The `## Skill Map` — the Leader assigns, the worker loads before writing | [`CLAUDE.md`](../CLAUDE.md#skill-map) |
| Traceability | Every `/akili-execute` commit is prefixed `[SPEC:<spec-path>]`; complex call sites may carry a `// @akili-spec <spec-path>` comment | this file |
| Concurrency | One AKILI session per checkout; additional sessions use `git worktree` | [`CLAUDE.md`](../CLAUDE.md#concurrency) |

---

## What is not wired here

Recorded so a future session does not mistake absence for breakage.

| Gap | Consequence |
|---|---|
| The `akili` binary is not on PATH (global npm install needs sudo) | Commands and skills work; `akili update` / `doctor` must wait for a sudo install |
| `codegraph` CLI is not installed | Commands fall back to Glob/Grep with lower-confidence scans — report degraded confidence, never a narrowed scope |
| No `.agents/agents/` nested wrappers | Google Antigravity cannot discover the personas. Claude Code is unaffected |
| `/akili-audit` has never run | `docs/specs/audits/` holds only its README. There is no drift baseline yet |
| `/akili-quick` has never run | `docs/specs/quick/` does not exist; it is created on first use |
| `// @akili-spec` comments: zero in the codebase | Half the traceability contract is unused — commits carry `[SPEC:]`, source files carry nothing |

---

## Checklist before starting AKILI work

- [ ] `akili doctor --tool claude` reports HEALTHY
- [ ] The checked-out branch is correct — `staging` for lifecycle writes, a feature branch for spec work
- [ ] No other AKILI session is active in this checkout
- [ ] `docs/specs/kaizen-log.md` `## Active Lessons` read — the lessons prior specs paid for
- [ ] The spec's depth (Quick / Lite / Standard / Full) is chosen deliberately, not by default

## Next step

Run `/akili-resume` to see what is in flight, or `/akili-propose <name>` to open a new change.
