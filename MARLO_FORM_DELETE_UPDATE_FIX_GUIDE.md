# MARLO: Delete/Update Not Persisting - AI Agent Guide

## Scope: When to Use This Guide

Use this guide only when ALL of the following are true:

1. The feature is a **form that edits a list** of items (e.g., portfolios, projects, activities) where users can add/remove rows dynamically.
2. The UI removal works (the row disappears), but after saving and reloading, the deleted item reappears.
3. The backend uses **Struts 2/6** actions and the list is bound via `items[index].field` style parameters.
4. The delete/update logic runs through **Spring + Hibernate** (manager/DAO pattern).

Do NOT use this guide when:
- The problem is only on the frontend (no POST request).
- You are not using Struts list binding (e.g., JSON API only).
- Deletes are done via separate endpoints (single-item delete API).
- The issue is a permissions or validation failure.

## Summary of the Fix

This issue happens because Struts list binding fails when list indexes are missing (due to a deleted row), and/or because bulk HQL updates run outside a transaction.

**The fix has three required parts:**
1. **Manual request binding** in the Action to reconstruct the list from request parameters.
2. **Delete detection** by comparing IDs from the form vs IDs in the database.
3. **@Transactional** on manager methods that execute HQL update/delete queries.

If any of the three parts is missing, deletes or updates will silently fail or reappear after reload.

---

## Part 1: Manual Binding in the Action

### Why
Struts list binding fails when indexes are missing after a delete. The action receives an empty or partial list.

### Pattern

```java
/**
 * Manually bind items from HTTP request parameters.
 * Needed because Struts cannot auto-populate lists after deletions.
 */
private void bindItemsFromRequest() {
  items = new ArrayList<>();
  int index = 0;
  boolean hasMore = true;

  while (hasMore) {
    String idParam = this.getRequest().getParameter("items[" + index + "].id");

    if (idParam != null && !idParam.trim().isEmpty()) {
      try {
        Item item = new Item();
        Long id = Long.parseLong(idParam);
        item.setId(id);

        String name = this.getRequest().getParameter("items[" + index + "].name");
        item.setName(name);

        String startDate = this.getRequest().getParameter("items[" + index + "].startDate");
        if (startDate != null && !startDate.trim().isEmpty()) {
          try {
            item.setStartDate(java.sql.Date.valueOf(startDate));
          } catch (Exception e) {
            logger.warn("Error parsing startDate at index {}: {}", index, e.getMessage());
          }
        }

        String[] selectedValuesParam = this.getRequest().getParameterValues(
          "items[" + index + "].selectedValues"
        );
        if (selectedValuesParam != null && selectedValuesParam.length > 0) {
          List<Long> selectedValues = new ArrayList<>();
          for (String value : selectedValuesParam) {
            if (value != null && !value.trim().isEmpty()) {
              try {
                selectedValues.add(Long.parseLong(value));
              } catch (NumberFormatException e) {
                logger.warn("Error parsing value '{}' at index {}", value, index);
              }
            }
          }
          item.setSelectedValues(selectedValues);
        } else {
          item.setSelectedValues(new ArrayList<>());
        }

        items.add(item);
        index++;
      } catch (Exception e) {
        logger.error("Error binding item at index {}: {}", index, e.getMessage(), e);
        hasMore = false;
      }
    } else {
      hasMore = false;
    }
  }
}
```

### Use in `save()`

```java
@Override
public String save() {
  if (this.hasPermission("*")) {
    bindItemsFromRequest();
    saveItems();
    return SUCCESS;
  }
  return NOT_AUTHORIZED;
}
```

---

## Part 2: Delete Detection in the Action

### Why
The backend must identify which items were removed from the form by comparing:
- IDs sent by the form, vs
- IDs already stored in the database.

### Pattern

```java
public void saveItems() {
  if (items != null) {
    List<Long> inputIds = items.stream()
      .map(Item::getId)
      .filter(Objects::nonNull)
      .collect(Collectors.toList());

    List<Item> allExisting = itemManager.getItemsByGlobalUnitId(
      this.getCurrentGlobalUnit().getId()
    );

    // Save first
    if (!items.isEmpty()) {
      for (Item item : items) {
        Item itemToSave = (item.getId() != null)
          ? itemManager.getItemById(item.getId())
          : new Item();

        itemToSave.setName(item.getName());
        itemToSave.setStartDate(item.getStartDate());
        itemToSave.setEndDate(item.getEndDate());
        itemToSave.setGlobalUnit(
          item.getGlobalUnit() != null ? item.getGlobalUnit() : this.getCurrentGlobalUnit()
        );

        itemManager.saveItem(itemToSave);
      }
    }

    // Then delete removed items
    if (allExisting != null && !allExisting.isEmpty()) {
      List<Item> toDelete = allExisting.stream()
        .filter(existing -> existing.getId() != null && !inputIds.contains(existing.getId()))
        .collect(Collectors.toList());

      for (Item itemToDelete : toDelete) {
        itemManager.deleteItem(itemToDelete.getId());
      }
    }
  }
}
```

---

## Part 3: Transaction Management in Manager

### Why
Bulk HQL updates/deletes require an active transaction. Without `@Transactional`, Hibernate throws `TransactionRequiredException` or silently fails.

### Pattern

```java
import org.springframework.transaction.annotation.Transactional;

@Transactional
public void deleteItem(long id) {
  String hql = "update Item set active = false where id = :id";
  this.getSessionFactory().getCurrentSession()
    .createQuery(hql)
    .setParameter("id", id)
    .executeUpdate();
}

@Transactional
public Item saveItem(Item item) {
  return itemDao.save(item);
}
```

---

## Quick Troubleshooting Checklist

- The POST payload contains only the remaining list elements.
- The action receives a non-empty list after `bindItemsFromRequest()`.
- The delete detection finds the removed IDs.
- `@Transactional` is applied on the manager methods calling HQL update/delete.

---

## Concrete MARLO Reference

- Action: `marlo-web/src/main/java/org/cgiar/ccafs/marlo/action/crp/admin/PortfolioManagementAction.java`
- Manager: `marlo-data/src/main/java/org/cgiar/ccafs/marlo/data/manager/impl/PortfolioManagerImpl.java`
- DAO: `marlo-data/src/main/java/org/cgiar/ccafs/marlo/data/dao/mysql/PortfolioMySQLDAO.java`
- JS (optional debug only): `marlo-web/src/main/webapp/crp/js/admin/portfolioManagement.js`
