# MARLO Terminology Glossary

MARLO is one codebase that serves several research programs. Each program sees the same entities under its own
names: what the code calls a `Project` can appear on screen as *Project*, *Cluster*, *Priority* or *Technical
Implementation Team*. This page maps the internal names to the labels each `custom/*.properties` file shows, so
that a person or an AI agent reading the code does not mistake one program's vocabulary for the product's.

> **Before reasoning about a program's terminology:** if a `REPO_IDENTITY.md` file exists at the repository root,
> read it first. It states which program(s) this checkout serves and which custom file they use. If it does not
> exist, use this page and do not assume any one program's vocabulary applies everywhere.

---

## 1. How the labels are chosen

| Step | What happens | Where |
|---|---|---|
| 1 | Every Global Unit has a `crp_custom_file` parameter in `custom_parameters` (a database value, not in this repository) | `APConstants.CRP_CUSTOM_FILE` |
| 2 | On each request, the interceptor loads `custom/<crp_custom_file>.properties` on top of `global.properties` | `InternationalitazionFileInterceptor` |
| 3 | A key missing from the custom file falls back to `global.properties`. A `crp_custom_file` that names a file that does not exist leaves only `global.properties` | Struts resource bundle chain |
| 4 | Any change to `global.properties` or `custom/*.properties` needs an application restart after deploy | `reports/ai-context/deployment-checklist.md` |

Consequences:

- **The code never uses a program label as an entity name.** The model class, tables and keys stay `Project`,
  `CrpProgram`, `CrpClusterOfActivity`, and so on; only the displayed value changes.
- **Which file an installation uses cannot be read from the code.** It is the `crp_custom_file` value in that
  installation's database. To check it:

  ```sql
  SELECT g.id, g.acronym, cp.value
  FROM custom_parameters cp
  JOIN parameters p ON p.id = cp.parameter_id
  JOIN global_units g ON g.id = cp.global_unit_id
  WHERE p.`key` = 'crp_custom_file' AND cp.is_active = 1 AND g.is_active = 1;
  ```

- File names are used exactly as stored: `aicrra.properties` is spelled that way on purpose (it is the value in the
  database), and `aiccra3.properties` is a different file.

---

## 2. Internal name → displayed label

Values read from the files in `marlo-web/src/main/resources/` (2026-11-13). `·` means the file does not override
the key, so `global.properties` applies. Files that override none of these keys (`a4nh`, `livestock`, `maize`,
`rice`, `wheat`, `wle`) are omitted.

| File | `global.project` | `global.flagship` | `global.managementLiaison` | `global.pmu` | `programManagement.role` | `global.CrpMilestone` | `global.crps` | `menu.impactPathway` |
|---|---|---|---|---|---|---|---|---|
| **global** (default) | Project | Flagship | Flagship Leader | PMU | Program Management Unit | Milestone | CRPs | Impact Pathways |
| aiccra3 | Technical Implementation Team | Component | Component Leader | Secretariat | PIU | Year Target | PTFs | Overall Performance Indicators |
| aicrra | Cluster | Component | Component Leader | Secretariat | PMC | Year Target | PTFs | Overall Performance Indicators |
| alliance | Project | Lever | Lever Director | PMU | Program Management Unit | Milestone | CRPs | Impact Pathways |
| bigdata | · | Module | Module Leader | · | Secretariat | · | · | · |
| ccafs | · | · | Management liaison | · | · | · | · | · |
| ciat | · | · | · | · | Program Management Unit | · | · | Impact Pathway |
| eib | · | Module | Module Leader | · | Secretariat | · | · | · |
| fta | Priority | · | · | · | · | · | · | · |
| gender | · | Module | Module Leader | · | Secretariat | · | · | · |
| pabra | Cluster | Component | Component Leader | Secretariat | PMC | Year Target | PTFs | Overall Performance Indicators |
| pim | · | · | · | · | · | · | · | · |
| reap | Technical Implementation Team | Component | Component Leader | Secretariat | PIU | Year Target | PTFs | Overall Performance Indicators |
| test | Project | Flagship | Flagship Leader | PMU | Program Management Unit | Milestone | CRPs | Impact Pathways |

How the keys map to the code:

| Key | Internal entity / concept |
|---|---|
| `global.project` (also `global.Project`, `global.projects`, `menu.projects`) | `Project` (`projects` table, `action/projects/`) |
| `global.flagship` | `CrpProgram` of the flagship type |
| `global.managementLiaison` | Leader role of a `CrpProgram` |
| `global.pmu`, `programManagement.role` | Program management unit / role |
| `global.CrpMilestone` | `CrpMilestone` |
| `global.crps` | `GlobalUnit` (plural label) |
| `menu.impactPathway` | Impact Pathway section (`action/impactpathway/`) |

---

## 3. Traps

- **"Cluster" means two different things.** In `aicrra` and `pabra`, *Cluster* is the label for `Project`. In every
  file, *Cluster of Activities* (`global.clusterOfActivities`) is a separate entity, `CrpClusterOfActivity`, that
  sits under a flagship. In `pim`, `global.clusterOfActivities` is shown as just *Clusters*. Read the key, not the
  word.
- **"Program" is overloaded.** `global.flagshipCenter` shows *Program*, `GlobalUnit` is often called a program in
  documentation, and `CrpProgram` is the flagship/regional entity.
- **Documentation examples are not universal.** `docs/prd.md`, `docs/trd/trd.md` and `docs/ux-ui/design.md` use
  *Cluster* and *Cluster Coordinator* because their examples come from one program. In code and data the entity is
  always `Project`.

---

## 4. Keeping this page current

- When a custom file renames one of the keys above, update its row in §2 in the same change.
- When a new `custom/*.properties` file is added, add its row (or list it among the omitted files).
- Do not record here which installation or repository uses which file: that belongs in the `REPO_IDENTITY.md` of
  the repository that serves it.
