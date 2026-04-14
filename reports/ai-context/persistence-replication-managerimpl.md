# Persistence Replication in ManagerImpl

## Goal
Document how save operations replicate across phases in manager implementation logic.

## Primary Verified Case
### Deliverable Funding Source Replication
- File: `marlo-data/src/main/java/org/cgiar/ccafs/marlo/data/manager/impl/DeliverableFundingSourceManagerImpl.java`

### Save flow
1. `saveDeliverableFundingSource(...)` persists current entity.
2. Reads current phase (`phaseDao.find(...)`).
3. If phase is `PLANNING` and has next phase, replication is triggered with `addDeliverableFundingSourcePhase(...)`.
4. If phase is `REPORTING`, replication targets upkeep phase (`currentPhase.getNext().getNext()`) when available.
5. Replication recurses through subsequent phases (`if (phase.getNext() != null) ...`).

### Delete flow parity
1. `deleteDeliverableFundingSource(...)` removes current record.
2. Applies phase-aware delete propagation:
   - from `PLANNING` to next phases
   - from `REPORTING` to upkeep chain
3. Recurses with `deleteDeliverableFundingSource(...)` across phase chain.

### Constraints in logic
- Publication-specific condition: replication is skipped for publication deliverables (`isPublication`).
- Duplicate prevention: target phase list is filtered before insert.

## Additional Verified Replication Patterns
1. `ProjectInnovationOrganizationManagerImpl`
   - phase-aware save/delete propagation methods:
   - `saveInnovationOrganizationPhase(...)`
   - `deleteProjectInnovationOrganizationPhase(...)`
2. `ProjectInnovationComplementarySolutionManagerImpl`
   - phase-aware save/delete propagation methods:
   - `saveProjectInnovationComplementarySolutionPhase(...)`
   - `deleteProjectInnovationComplementarySolutionPhase(...)`

## Standard Pattern to Expect in MARLO ManagerImpl
1. Determine current phase (`PLANNING` or `REPORTING`).
2. Compute target phase(s): next or upkeep (`next.next`).
3. Clone or remove related entities in target phases.
4. Recurse while `phase.getNext() != null`.

## Risk Checklist for Changes
1. Do not break recursive propagation guards (`phase.getNext()`).
2. Keep parity between save and delete replication paths.
3. Preserve duplicate filters when cloning.
4. Validate publication or section-specific skip rules.
5. Recheck downstream phase consistency after any manager change.

## Expected Behavior by Phase
- Planning save: record replicated to all next phases.
- Planning delete: record removed from all next phases.
- Reporting save: record replicated to upkeep phase (`next.next`).
- Reporting delete: record removed from upkeep phase chain.
