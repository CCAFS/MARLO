# Activities - Agent Context

Read this before editing Activities. It is a compact context file for low-token sessions.
Use it to orient, then open only the files you will change.

## Scope

The activities domain has two halves that are easy to confuse:

- **Catalog**, in Admin: the list of activity names a Global Unit offers.
- **Reporting**, in a cluster(project): the activities a cluster actually reports, per phase.

This context covers both, plus the deletion rule that connects them. It does not cover the
generic relations popup UI, which lives in `reports/ai-context/relations-popup-pattern.md`.

## The Naming Trap

| Entity | Table | Meaning | Scope |
|---|---|---|---|
| `ActivityTitle` | `activities_titles` | catalog entry, just a name | one Global Unit |
| `Activity` | `activities` | reported activity | one cluster, one phase |

`Activity.title` is **not** the catalog name by reference. For AICCRA,
`ProjectActivitiesAction` copies the catalog title into it on save, so the two strings are
usually identical and `Activity.title` carries no extra information. The field a user actually
writes is `Activity.description`.

The link is `activities.title_id -> activities_titles.id`.

## Primary Files

Catalog (Admin):
- Route: `marlo-web/src/main/resources/struts-admin.xml` (`{crp}/activityManager`)
- Action: `marlo-web/src/main/java/org/cgiar/ccafs/marlo/action/crp/admin/CrpActivityAction.java`
- View model: `marlo-web/src/main/java/org/cgiar/ccafs/marlo/action/crp/admin/ActivityTitleRelation.java`
- View: `marlo-web/src/main/webapp/WEB-INF/crp/views/admin/activityManager.ftl`
- JS: `marlo-web/src/main/webapp/crp/js/admin/activity.js`
- Test: `marlo-web/src/test/java/org/cgiar/ccafs/marlo/action/crp/admin/CrpActivityActionTest.java`

Reporting (cluster):
- Route: `marlo-web/src/main/resources/struts-projects.xml` (`{crp}/activities`, namespaces
  `/projects` and `/clusters`)
- Action: `marlo-web/src/main/java/org/cgiar/ccafs/marlo/action/projects/ProjectActivitiesAction.java`
- Validator: `marlo-web/src/main/java/org/cgiar/ccafs/marlo/validation/projects/ProjectActivitiesValidator.java`
- View: `marlo-web/src/main/webapp/WEB-INF/crp/views/projects/projectActivities.ftl`
- JS: `marlo-web/src/main/webapp/crp/js/projects/projectActivities.js`

Data:
- `marlo-data/src/main/java/org/cgiar/ccafs/marlo/data/dao/ActivityDAO.java` and
  `marlo-data/src/main/java/org/cgiar/ccafs/marlo/data/dao/mysql/ActivityMySQLDAO.java`
- `marlo-data/src/main/java/org/cgiar/ccafs/marlo/data/dao/ActivityTitleDAO.java` and
  `marlo-data/src/main/java/org/cgiar/ccafs/marlo/data/dao/mysql/ActivityTitleMySQLDAO.java`
- Managers and impls under `marlo-data/src/main/java/org/cgiar/ccafs/marlo/data/manager/`
- Mappings: `marlo-data/src/main/resources/xmls/Activities.hbm.xml`, `ActivitiesTitles.hbm.xml`

Superadmin: `marlo-web/src/main/java/org/cgiar/ccafs/marlo/action/superadmin/ActivitiesReplicationAction.java` re-runs replication for
selected activities.

## Route Map

- `/admin/{crp}/activityManager` -> `CrpActivityAction` -> `crpAdminStack` -> `activityManager.ftl`
- `/clusters/{crp}/activities` -> `ProjectActivitiesAction` -> `editProjectsStack` -> `projectActivities.ftl`

The `clusters` and `ai` packages extend `projects`, so the same action serves all three namespaces.

## Forward Replication

A reported activity is **not** a single row. `ActivityManagerImpl.saveActivity` propagates on save
and `saveActvityPhase` recurses over every following phase:

```java
if (phase.getNext() != null) {
  this.saveActvityPhase(phase.getNext(), projecID, activity);
}
```

`ProjectInfoManagerImpl` also copies activities when a cluster is replicated into a new phase.

The identity that survives replication is `activities.composed_id`, built as
`projectId + "-" + activityId` and propagated unchanged to every copy. **Group by `composed_id`
whenever you report on activities**, or the same activity appears once per phase. The column is
nullable on legacy rows, so fall back to the record id.

`Project.getActivities()` is not phase filtered: it returns the activities of every phase.

## Deletion Rule for Catalog Entries

`BaseAction.canBeDeleted(id, "...ActivityTitle")` returns false when any **active** activity, in
any phase, belongs to a cluster that is active and whose `projects_info` for the actual phase
spans the phase year. Two non-obvious halves:

- activities are **not** filtered by phase, so an activity from an older phase blocks deletion;
- clusters **are** filtered by date range, via `getActiveProjectsByPhase(phase, phase.getYear(), null)`.

Facts that matter before touching this:

- `activities_titles` has **no `is_active`**: the column was dropped by
  `V2_6_0_20210514_1557__AlterTableActivities.sql`. `ActivityTitleMySQLDAO.deleteActivityTitle`
  therefore does a **physical delete**.
- The only real protection is the FK `activities_ibfk_6 (activities.title_id) ON DELETE RESTRICT`,
  and the FK **ignores `is_active`**: while any row references the title, MySQL rejects the delete.
- `CrpActivityAction.saveActivities()` deletes every catalog entry missing from the POST **without
  consulting `canBeDeleted`**. The rule lives only in the view, which hides the remove button.

### Decision Log

- 2026-08-28 - The rule was reviewed and left as is. The narrower phase/year scope and the
  server side gap in `saveActivities()` are known and accepted. Do not change the rule without
  reopening this decision.

## Phase Semantics

Needed whenever you sort or label phases:

- `phases.name` is `POWB`, `AR` or `UpKeep`; `phases.description` is `Planning` or `Reporting`.
- A single `year` holds up to three phases, so the year alone does not identify one.
- **Phase ids are not chronological.** `V2_6_0_20180808_1542__UpdatePhases_UpKeep.sql` reassigned
  them. Order by `year`, then `start_date`, then `id`.
- `Phase.getComposedName()` returns `name + " - " + year`. Do not reuse it inside another
  dash-joined string; compose your own `name + " " + year` label instead.

## Latent Traps

- `activities_titles.start_year` / `end_year` exist (NOT NULL, defaults 2020/2029) and
  `ActivityTitleDAO.findByCurrentYear` filters on them, but **that method has no callers**:
  `ProjectActivitiesAction` uses `findByGlobalUnit`. Meanwhile `saveActivities()` never sets the
  years, so titles created from Admin are stored with `start_year = 0, end_year = 0`. Harmless
  today because nothing filters by year; it breaks the moment someone wires `findByCurrentYear`.
- Titles predating `V2_6_0_20260724_1433__UpdateActivityTitlesTable.sql` may have a null
  `global_unit_id`, and `findByGlobalUnit` will not list them.
- `activityManager.ftl` renders a hidden template item cloned by `activity.js`; keep the add
  button inside `.program-block` or the clone breaks, and keep new blocks out of the template.

## Reporting on Activities

The Admin page shows, per catalog entry, which clusters use it. It is resolved with a single
projection query for the whole page (`ActivityDAO.getActivityTitleRelations`), grouped by
`composed_id` in `CrpActivityAction.groupRelations`, which is package private and static so it is
unit testable without a database. Do not hydrate entities to build this kind of report: the lazy
loads of `project`, `phase` and their collections are where the N+1 comes from.

The popup itself follows `reports/ai-context/relations-popup-pattern.md`. Read that before
changing its markup, especially the rule that a filtered column must hold the plain value only.

## Quick Debug Checklist

- Confirm which half you are in: catalog (`activities_titles`) or reporting (`activities`).
- An activity appearing many times in a report means the grouping by `composed_id` is missing.
- A remove button that errors on save is the FK: some `activities` row still references the title.
- A newly created catalog entry not showing in a cluster: check `global_unit_id`, then whether
  anything started filtering by `start_year` / `end_year`.
- Save not persisting in the cluster page: `ProjectActivitiesAction.validate()` only validates
  under `if (save)`; inspect `invalidFields` and `actionMessages`.
- Rebuild: `mvn -o -pl marlo-data,marlo-web compile -DskipTests`. If you see
  `Unresolved compilation problem`, that is a stale Eclipse class in `target/`; add `clean`.

## Related Context

- `reports/ai-context/relations-popup-pattern.md` - the popup UI pattern
- `reports/ai-context/persistence-replication-managerimpl.md` - forward replication
- `reports/ai-context/save-validation-matrix.md` - save pipeline and validators
- `reports/ai-context/struts-critical-routing-catalog.md` - routing contracts

## Token-Saving Usage

1. Read this file first.
2. Decide catalog or reporting, and open only that action plus the exact view section.
3. Open the full DataTables or popup detail only when changing the popup.
4. Run the targeted unit test rather than the whole build.
