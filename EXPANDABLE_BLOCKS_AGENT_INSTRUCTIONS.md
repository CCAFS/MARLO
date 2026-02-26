# Expandable Blocks AI Agent Instructions

## Purpose
Provide a standardized runbook for debugging CRUD issues in UI sections that use expandable blocks (accordion-style list + hidden template + indexed fields) in MARLO.

## Scope
Applies to any admin UI with:
- A repeated list of blocks rendered from a collection.
- A hidden template block used for new entries.
- Indexed field names (for example, `items[0].id`).
- Add, update, and delete handled via a single form submit.

Reference examples:
- Template and list structure: [marlo-web/src/main/webapp/WEB-INF/crp/views/admin/portfoliosManagement.ftl](marlo-web/src/main/webapp/WEB-INF/crp/views/admin/portfoliosManagement.ftl)
- Working baseline: [marlo-web/src/main/webapp/WEB-INF/crp/views/admin/timelineManagement.ftl](marlo-web/src/main/webapp/WEB-INF/crp/views/admin/timelineManagement.ftl)
- Client handlers: [marlo-web/src/main/webapp/crp/js/admin/portfolioManagement.js](marlo-web/src/main/webapp/crp/js/admin/portfolioManagement.js)
- Server action: [marlo-web/src/main/java/org/cgiar/ccafs/marlo/action/crp/admin/PortfolioManagementAction.java](marlo-web/src/main/java/org/cgiar/ccafs/marlo/action/crp/admin/PortfolioManagementAction.java)
- Struts routing: [marlo-web/src/main/resources/struts-admin.xml](marlo-web/src/main/resources/struts-admin.xml)

## Current Add Flow (Document the Baseline)
- User clicks the Add button in the expandable block list.
- JavaScript clones a hidden template block and removes the template ID.
- The new block fields are cleared, and content is shown.
- Indexing logic rewrites all field names and IDs (for example, `items[0].name`).
- Select2/datepicker widgets are re-initialized for the new block.
- Form submit posts the indexed collection to a Struts action.
- The action creates new records when item IDs are null and persists them.
- The page reloads with the new block rendered from the backend list.

## Verification Runbook
Use the sections below to identify why update/delete fails while add succeeds.

### Expandable Blocks
- Confirm each block has a unique container element and uses the expected class (for example, `srfSlo`).
- Verify expand/collapse toggles do not remove or disable inputs.
- Ensure blocks added after load receive event handlers (add/remove, expand/collapse).

### Hidden Template
- Ensure the template block is hidden and not submitted.
- Confirm the template uses placeholder index (for example, `items[-1]`).
- Verify the submit hook disables template fields so they are not posted.

### Indexed Fields
- Confirm each block contains a hidden ID input: `items[i].id`.
- After add/remove, verify all fields were reindexed consistently.
- Check that `setNameIndexes` touches all inputs (text, hidden, select).
- Ensure no field is missing a `name` attribute after cloning.

### Submission Hooks
- Identify whether submit is full form post or AJAX.
- Verify the submit hook runs (no JS errors) and does not disable real fields.
- Confirm the payload includes IDs for existing items and omits deleted ones.

### Action Routing
- Confirm the form posts to the correct Struts action and method.
- Confirm the action uses a collection property with proper getter/setter.
- Verify the action deletes items not present in posted IDs.

### Persistence Checks
- Confirm update path loads existing entity by ID and then overwrites fields.
- Confirm delete path removes entities not in the submitted ID list.
- Validate any required fields do not block updates silently.

## Investigation Plan for Update/Delete Failures
1. Verify POST payload in browser devtools:
   - IDs must be present for edited items.
   - Removed items should not be present in the POST.
2. Verify indexed names in the DOM after delete:
   - IDs and field names should be sequential and consistent.
3. Verify submit hook behavior:
   - Template fields are disabled.
   - Real fields are not disabled accidentally.
4. Verify backend binding:
   - Collection size equals number of posted blocks.
   - Each item has the expected ID and field values.
5. Verify delete logic:
   - The action compares posted IDs against existing DB records.
   - Items missing from the POST are deleted.
6. Compare against a known working module:
   - Use the working expandable block section and diff the template and JS.

## Logging Points (Temporary)
Add temporary logs to confirm binding and delete decisions.
- Action save method: log list size and each item ID on submit.
- Action delete step: log IDs to delete and IDs to keep.
- Action update step: log ID and changed fields before save.

## Cross-Module Comparison Checklist
Use a working expandable-block module as baseline.
- Template pattern matches: hidden template, list, remove button, hidden ID.
- JS handlers match: add, remove, reindex, submit hook.
- Action pattern matches: delete missing IDs, update existing, create new.

## Output Expectations
- Keep notes in English.
- Use structure-based headings (Expandable Blocks, Hidden Template, Indexed Fields).
- Link to files with markdown links.
- Record what failed, why, and where (path + short explanation).
