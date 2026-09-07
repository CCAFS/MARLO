# Relations Popup Pattern

## Scope

How MARLO builds a **relations popup**: the small button that reports how many related records an
element has, plus the modal with the searchable, sortable, paginated, filterable table behind it.

Use this document when you add a popup to a new section, or when you upgrade an existing one. It
covers the markup contract, the shared JS/CSS, the non-obvious DataTables behaviour these popups
depend on, and how to test the result without a database.

It does not cover business rules. A relations popup **reports** a relation; it never decides
whether a record can be saved or deleted.

## Reference Implementations

| Section | View | Style |
|---|---|---|
| Admin > Activities | `marlo-web/src/main/webapp/WEB-INF/crp/views/admin/activityManager.ftl` | current, data passed in |
| Admin > Managing Partners | `marlo-web/src/main/webapp/WEB-INF/crp/views/admin/ppaPartners.ftl` | legacy, data fetched inside the macro |
| Cluster contributions | `marlo-web/src/main/webapp/WEB-INF/crp/views/projects/projectContributionsCrpList.ftl` | legacy, own page JS |

Prefer the **Activities** one for new work. The two styles differ in one decision:

- Legacy (`relationsMacro`): the macro calls six or more `action.get...` methods itself, one per
  related entity type. Every call is a query, executed while rendering, for every element on the page.
- Current (`activityTitleRelationsMacro`): the action resolves everything **once per request** and the
  macro receives a ready list as a parameter. The macro only formats.

Pass the data in. It keeps the query count bounded and makes the macro testable by inspection.

## The Three Moving Parts

| Part | File | Owns |
|---|---|---|
| Macro | `marlo-web/src/main/webapp/WEB-INF/crp/macros/relationsPopupMacro.ftl` | button, modal, table markup |
| Behaviour | `marlo-web/src/main/webapp/global/js/relationsModalDataTables.js` | DataTables init, search box, column filters |
| Styling | `marlo-web/src/main/webapp/crp/css/admin/relationsModalTables.css` | modal width, search row, filter row |

The JS is delegated on `document` and keyed off `shown.bs.modal`, so a page gets the whole behaviour
just by loading the assets. There is no per-page init code to write.

## Recipe: Add a Relations Popup

### 1. Resolve the data in the action, once

Expose one read-only getter that the view calls per element, backed by a single query for the whole
page and memoised on the action instance (actions are per request):

```java
private Map<Long, List<MyRelation>> relationsByKey;

public List<MyRelation> getMyRelations(long elementId) {
  if (relationsByKey == null) {
    relationsByKey = this.loadRelations();
  }
  List<MyRelation> relations = relationsByKey.get(elementId);
  return relations == null ? Collections.emptyList() : relations;
}
```

Memoise lazily, not in `prepare()`: `prepare()` commonly skips work on POST
(`if (!this.isHttpPost())`), which would make the popups disappear after a save.

Project scalar columns and return `List<Map<String, Object>>` from the DAO (precedent:
`FundingSourceDAO`, `ICenterOutcomeDAO`). Do not hydrate entities to render a report: the lazy loads
of `project`, `phase` and their collections are where the N+1 comes from. Map to a small read-only
view model in `marlo-web` so `marlo-data` stays free of view types.

### 2. Group phase-replicated data before rendering

Most MARLO records replicate forward: one logical item exists as one row **per phase**. Rendering raw
rows makes the same item repeat many times, and the button count stops matching the table.

Group by the identity that survives replication. For activities that is `activities.composed_id`
(`projectId-activityId`, propagated unchanged by `ActivityManagerImpl.saveActvityPhase`). Keep the
grouping in a package-private `static` method so it is unit testable without a database:

```java
static Map<Long, List<MyRelation>> groupRelations(List<Map<String, Object>> rows, long currentPhaseId)
```

Handle these, they all occur in real data:

- the identity column is nullable on legacy rows: fall back to the record id so two items never merge;
- JDBC hands back `String` or `BigInteger` where you expect `Long`: coerce, never cast;
- order phases by `year`, then `start_date`, then `id`. A year holds up to three phases (`POWB`, `AR`,
  `UpKeep`, see `phases.name`) and their ids were reassigned by
  `V2_6_0_20180808_1542__UpdatePhases_UpKeep.sql`, so id order is not chronological.

### 3. Write the markup

```html
<div id="MyElement-${id}" class="form-group elementRelations MyElement">
  <button type="button" class="btn btn-default btn-xs"
          data-toggle="modal" data-target="#modal-mine-MyElement-${id}">
    <span class="icon-20 project"></span> <strong>${count}</strong> label
  </button>

  <div class="modal fade" id="modal-mine-MyElement-${id}" tabindex="-1" role="dialog">
    <div class="modal-dialog modal-lg" role="document">
      <div class="modal-content">
        <div class="modal-header">...</div>
        <div class="modal-body">
          <div class="relationsModalFilters" data-filter-columns="1,4" data-label-all="All"></div>
          <table class="table table-striped table-hover" width="100%"
                 data-page-length="10" data-order='[[0, "asc"]]'>
            <thead>...</thead>
            <tbody>...</tbody>
          </table>
        </div>
        <div class="modal-footer">...</div>
      </div>
    </div>
  </div>
</div>
```

Wrap everything in `.elementRelations` so the popup styling has a scope to hang from. Count **distinct
related parents** in the button and make sure the table agrees with that number.

### 4. Load the assets and cache bust them

```
[#assign customJS = [
  "//cdn.datatables.net/1.13.1/js/jquery.dataTables.min.js",
  "${baseUrlCdn}/global/js/relationsModalDataTables.js?YYYYMMDD",
  ...
  ] /]
[#assign customCSS = [
  "${baseUrlMedia}/css/admin/relationsModalTables.css?YYYYMMDD",
  "//cdn.datatables.net/1.13.1/css/jquery.dataTables.min.css"
  ] /]
```

Whenever you edit either shared file, bump `?YYYYMMDD` in **every** view that references it, not only
the one you are working on. Both files are shared.

## Markup Contract

| Hook | Where | Effect |
|---|---|---|
| `.elementRelations` | wrapper `div` | scope for the popup styling |
| `.modal-body table` | any table in a modal | gets DataTables on first open |
| `.relationsModalFilters` | `div` before the table | opts the table into per column filters |
| `data-filter-columns` | on that `div` | zero based column indexes to build selects for |
| `data-label-all` | on that `div` | text of the empty option |
| `data-page-length` | on the `table` | page size, default 25 |
| `data-order` | on the `table` | initial sort, JSON, e.g. `[[0, "asc"]]` |

Filter labels are read from the column `<th>`, so they follow i18n with no duplicated key.
A column whose values are all identical gets **no** select: a single option filters nothing.

## Hard Rules

These come from reading the DataTables source shipped in the repo
(`marlo-web/src/main/webapp/WEB-INF/swagger/dist/DataTables/DataTables-1.10.22/js/jquery.dataTables.js`,
same code paths as the 1.13.1 build loaded from the CDN).

1. **A filtered column must contain the plain value and nothing else.**
   `_fnFilterData` (line ~4527) decodes HTML entities and strips newlines, and that is all: it does
   **not** strip markup, because `DataTable.ext.type.search` is empty by default. A `<span>` badge or a
   trailing `(8)` counter becomes part of the filter data, so one logical value shows up as several
   filter options and exact matching breaks. Put decoration in a cell attribute such as `title`, or in a
   different, unfiltered column.

2. **DataTables merges the table `data-*` attributes over the init options.**
   `_fnCamelToHungarian(defaults, $.extend(oInit, $this.data()), true)` (line ~901). That is how
   `data-page-length` and `data-order` win over the shared defaults, and it is also a trap: any
   `data-*` you invent on the `<table>` is interpreted as an init option. Put custom attributes on a
   wrapper element instead, which is why the filter configuration lives on the `div`.

3. **Cell content is trimmed on read.** `_fnGetRowElements` does `(cell.innerHTML).trim()` (line
   ~3032), so FTL indentation inside a `<td>` is harmless.

4. **Initialise on `shown.bs.modal`, never on page load.** A table in a hidden modal measures its
   columns as collapsed, and a page with N elements would build N DataTables nobody has opened. The
   shared script handles this; do not add your own init.

5. **The shared CSS has selectors broader than its name.** `.modal .modal-dialog` (width) and
   `.modal .modal-body table thead th` are not scoped to relations popups, so any other Bootstrap modal
   on a page that loads this stylesheet inherits them. Today no reachable modal is affected on either
   consumer page. If you add a modal to one of them, scope those rules to `.elementRelations` first.

## Testing Without a Database

Three layers, all runnable locally.

**Grouping** — plain JUnit on the static method, no Spring, no DB. See
`marlo-web/src/test/java/org/cgiar/ccafs/marlo/action/crp/admin/CrpActivityActionTest.java`.

```
mvn -o -pl marlo-data,marlo-web test -Dtest=CrpActivityActionTest -Dsurefire.failIfNoSpecifiedTests=false
```

If you get `java.lang.Error: Unresolved compilation problem`, that is an Eclipse-compiled stale class
in `target/classes`, not your code: add `clean`. Also beware `mvn ... | tail && echo OK`, which reports
the exit status of `tail`; use `set -o pipefail` and `${PIPESTATUS[0]}`.

**Popup behaviour** — jsdom with real jQuery and the repo copy of DataTables, loading
`relationsModalDataTables.js` and firing `shown.bs.modal`. This is what catches the filter traps:
build the table markup you actually render, then assert the built selects, their options, the filtered
row counts, the initial order, and that reopening the modal does not duplicate anything. jsdom starts
at `readyState: loading`, so wait for jQuery's ready queue before triggering the event.

**SQL** — parse with `sqlglot` in the `mysql` dialect and check every referenced column against the
Hibernate mappings (`marlo-data/src/main/resources/xmls/*.hbm.xml`). Verify the projected aliases match
the keys the grouping code reads. This catches typos and drift; it does not replace running the query.

Always say explicitly whether the query has been executed against a database.

## Related Context

- `reports/ai-context/frontend-composition-map.md` — how FTL pages compose macros
- `reports/ai-context/persistence-replication-managerimpl.md` — forward replication, the reason grouping is needed
