# MARLO: Delete/Update Not Persisting - AI Agent Guide

## Scope: When to Use This Guide

Use this guide only when ALL of the following are true:

1. The feature is a form that edits a list of items (for example portfolios, projects, activities, target units) with dynamic add/remove rows.
2. UI removal works (row disappears), but after save and reload, deleted rows reappear.
3. Backend uses Struts actions with list-style form binding (`items[index].field`).
4. Persistence uses Spring + Hibernate with manager/DAO layers.

Do NOT use this guide when:
- The issue is frontend-only (no POST or malformed payload).
- Deletes are done through separate single-item API endpoints.
- The root cause is authorization/validation failure.

## Core Fix Strategy

Delete-not-persisting usually requires all of these:

1. Reliable list binding in the Action.
2. Deterministic delete detection (compare incoming IDs vs DB IDs).
3. Correct transaction boundaries in manager/service layer.

If one part is missing, deletes can silently fail.

---

## Part 1: Reliable Binding in Action

### Why

Struts list binding can fail when indexes are missing after row removal.

### Pattern

```java
private void bindItemsFromRequest() {
  items = new ArrayList<>();
  int index = 0;

  while (true) {
    String idParam = this.getRequest().getParameter("items[" + index + "].id");
    if (idParam == null || idParam.trim().isEmpty()) {
      break;
    }

    Item item = new Item();
    item.setId(Long.parseLong(idParam));
    item.setName(this.getRequest().getParameter("items[" + index + "].name"));
    items.add(item);
    index++;
  }
}
```

### Save Entry Point

```java
@Override
public String save() {
  if (!this.hasPermission("*")) {
    return NOT_AUTHORIZED;
  }

  bindItemsFromRequest();
  saveItems();
  return SUCCESS;
}
```

---

## Part 2: Delete Detection by IDs (Never by Entity Contains)

### Why

`list.contains(entity)` is fragile with detached entities/proxies. Compare IDs instead.

### Pattern

```java
public void saveItems() {
  List<Long> incomingIds = items.stream()
    .map(Item::getId)
    .filter(Objects::nonNull)
    .toList();

  List<Item> existing = itemManager.getItemsByGlobalUnitId(this.getCurrentGlobalUnit().getId());

  for (Item input : items) {
    Item toSave = (input.getId() == null)
      ? new Item()
      : itemManager.getItemById(input.getId());

    toSave.setName(input.getName());
    toSave.setGlobalUnit(this.getCurrentGlobalUnit());
    itemManager.saveItem(toSave);
  }

  if (existing != null) {
    List<Item> removed = existing.stream()
      .filter(db -> db.getId() != null && !incomingIds.contains(db.getId()))
      .toList();

    for (Item item : removed) {
      itemManager.deleteItem(item.getId());
    }
  }
}
```

---

## Part 3: Transaction Placement (Critical for Struts)

### Why

In MARLO, some Struts interceptors cast actions to `BaseAction`. If `@Transactional` is added on Action methods, Spring may generate a JDK proxy (`jdk.proxy...`) and interceptor casts can fail with:

`ClassCastException: jdk.proxy... cannot be cast to BaseAction`

### Mandatory Rule

- Do NOT put `@Transactional` on Struts Action methods.
- Put `@Transactional` on manager/service methods that execute save/delete/update operations.

### Correct Example

```java
// Action (NO @Transactional)
@Override
public String save() {
  manager.saveItem(...);
  manager.deleteItem(...);
  return SUCCESS;
}

// Manager (WITH @Transactional)
@Transactional
public void deleteItem(long id) {
  itemDao.deleteItem(id);
}

@Transactional
public Item saveItem(Item item) {
  return itemDao.save(item);
}
```

---

## Part 4: prepare() Guard for POST Binding

### Why

If the Action clears list fields during POST in `prepare()`, Struts cannot bind submitted rows.

### Rule

- Initialize list on GET.
- On POST, do not clear bound list before binding.

### Pattern

```java
@Override
public void prepare() {
  if (!this.isHttpPost()) {
    this.items = loadFromDb();
  } else if (this.items == null) {
    this.items = new ArrayList<>();
  }
}
```

---

## Part 5: TargetUnit-Specific Sequence (BoardAction-like Cases)

When the list has parent-child relations (for example `SrfTargetUnit` -> `CrpTargetUnit`):

1. Build incoming parent IDs from POST.
2. Load active parent records from DB.
3. For removed parent IDs:
   - soft-delete active child links first,
   - then soft-delete parent.
4. Save/update incoming parents.
5. Ensure all manager delete/save methods are transactional.

---

## Troubleshooting Checklist

- POST contains only currently visible rows.
- Action receives/binds list rows correctly.
- Delete detection is ID-based.
- Action has no `@Transactional` on `save()`.
- Manager/service save/delete methods use `@Transactional`.
- No interceptor cast errors (`proxy` to `BaseAction`).

---

## MARLO Reference Examples

- Action example: `marlo-web/src/main/java/org/cgiar/ccafs/marlo/action/crp/admin/PortfolioManagementAction.java`
- Manager example: `marlo-data/src/main/java/org/cgiar/ccafs/marlo/data/manager/impl/PortfolioManagerImpl.java`
- DAO example: `marlo-data/src/main/java/org/cgiar/ccafs/marlo/data/dao/mysql/PortfolioMySQLDAO.java`
- Frontend list index update example: `marlo-web/src/main/webapp/crp/js/admin/portfolioManagement.js`
