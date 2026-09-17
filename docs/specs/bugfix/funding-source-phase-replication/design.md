# Funding Sources — Phase Replication Blocks Save — Design

**Spec ID:** BUG-FUNDINGSOURCE-SAVE-001
**Status:** Draft
**Owner:** IBD Team — Kenji Tanaka
**Reviewers:** PMU lead, QA lead, Tech lead
**Last Updated:** 2026-08-20
**Related TRD sections:** docs/trd/trd.md §3, §5
**Companion ai-context docs:** reports/ai-context/persistence-replication-managerimpl.md

---

## 1. Architecture Summary

The funding source save is one Struts request wrapped in a single Hibernate transaction. Any exception raised while
the action runs, or while the transaction is committed, discards every write of that request. The fix keeps the
existing pipeline and closes the two places where it throws or mutates the wrong row.

```
POST fundingSource.do?save
  editFSStack -> FundingSourceInterceptor (canEdit)
  prepare()      loads the FS, resolves the info row for the actual phase   [snapshot taken here]
  params         Struts binds the form onto the entity graph
  validate()     FundingSourceAction.validate() -> FundingSourceValidator -> saveSectionStatus (auto-flush)
  save()         info + budgets + institutions + divisions + locations      [current-phase row resolved here]
                 -> FundingSourceInfoManager.saveFundingSourceInfo()
                      -> saveInfoPhase(next ...) recursive forward replication   [insert fixed here]
  MARLOCustomPersistFilter.commit()   <-- any failure above rolls back the whole request
```

## 2. Module Footprint

| Module | File | Change |
|---|---|---|
| marlo-data | `data/manager/impl/FundingSourceInfoManagerImpl.java` | Set `modificationJustification` on the rows created and on rows found without one; two private helpers. |
| marlo-web | `action/funding/FundingSourceAction.java` | Snapshot of the previous-phase info in `prepare()`; creation of the current-phase row in `save()` plus restore of the previous row; grouped null checks in `validate()`; `FileDB` import. |

No FTL, JS, properties or migration files are touched.

## 3. Data Model Changes

None. `funding_sources_info.modification_justification` already exists and is already `NOT NULL` in both the DDL
(`V2_1_0_20170918_1000__FundingSourcesAnnuality.sql`) and the mapping
(`FundingSourceInfo.hbm.xml`, `not-null="true"`). No Flyway migration is required.

## 4. API / Action Surface

Not applicable — no new action, no route change, no REST endpoint.

## 5. Frontend Composition

Not applicable — no view change. `fundingSource.ftl` keeps binding to
`fundingSource.fundingSourceInfo.id`; when the current phase has no row, the id it submits belongs to the previous
phase and `save()` resolves the correct target server-side.

## 6. Persistence & Phase Replication Plan

**Save path.** `saveFundingSourceInfo()` still replicates forward only, only from a PLANNING phase, and only when
an end date exists (unchanged). The insert branch of `saveInfoPhase()` now sets the justification:

- prefer `fundingSourceInfo.getModificationJustification()`, so the audit log keeps the user's reason;
- fall back to `"Funding source info replicated to phase <description> <year>"` when it is blank.

The update branch fills the justification only when the target row has none, so existing audit values are not
rewritten.

**Current phase row.** `save()` compares the phase of the row loaded by id against `getActualPhase()`. When they
differ it builds a new `FundingSourceInfo` from the pristine database state, assigns the managed `FundingSource` and
the actual phase, and lets the existing code apply the form values on top. The row is inserted by the
`saveFundingSourceInfo()` call already at the end of the method — no extra save call, no extra replication pass.

**Previous phase row.** `prepare()` runs before parameter binding, so it snapshots the row while it still holds
database values. `save()` restores that snapshot onto the managed instance after the form values have been read out
of it, so the committed state of the past phase is unchanged. Known residue: the auto-flush inside `validate()`
writes the form values to that row and the restore reverses them, which leaves two no-net-change audit entries. The
definitive removal of that residue means not binding onto a foreign-phase row at all, which requires the FTL to
support an empty `fundingSourceInfo.id` — out of scope here.

**Delete path.** Unchanged.

## 7. Validation & Save Pipeline

`editFSStack` (i18nFile, validCrp, requireUser, validSessionCrp, editFunding, keepRedirectMessages,
accessibleStage, trimInputs, defaultStack) is unchanged, and so is the `if (save)` guard in
`FundingSourceAction.validate()`. Only the two file checks change: the conditions

```java
a != null && a.getId() == null || a.getId().longValue() == -1
```

are regrouped as `a != null && (a.getId() == null || a.getId().longValue() == -1)`. The original precedence
evaluates the third term after the first proved `a` null, which is a NullPointerException raised before `save()` can
run. It is masked today only because `forms.ftl` always renders the hidden file id input.

## 8. Permissions & Edit Gates

Unchanged. `save()` is still gated by `hasPermission("canEdit")` over the base permission set in `prepare()`, and
`FundingSourceInterceptor` still owns `canEdit`, the closed-CRP rule, the cancelled/complete rule, the REPORTING
rule and the W1 rule.

## 9. Specificity / Feature-Flag Strategy

Not applicable — the fix is unconditional. `CRP_HAS_RESEARCH_HUMAN` keeps guarding the `fileResearch` block exactly
as before.

## 10. Integration Points

Not applicable — no CLARISA, CGSpace, BI, AI service, S3 or Pusher interaction. The OCS sync is untouched.

## 11. Observability

The generated justification makes replicated rows identifiable in `funding_sources_info` history. No new logging is
added: the failure this spec removes was already logged by `MARLOCustomPersistFilter`
("Exception occurred when trying to commit transaction") and stored in the session `exception` attribute.

## 12. Performance & Scalability

Neutral. The replication issues the same statements as before plus, at most, one insert per phase that was
previously failing. The snapshot is an in-memory copy of one entity.

## 13. Security Considerations

None. No change to authentication, authorization or sensitive data handling.

## 14. Backwards Compatibility & Rollout

Compatible with existing data. Funding sources that already have rows in every phase follow the same code path as
before. Rollout is a normal `staging` deploy; rollback is a revert of the two files, which restores the previous
behaviour without any data migration to undo. Rows created by the fix are valid rows and stay valid after a revert.

## 15. Decision Records

- **ADR-1: keep the not-null mapping.** Setting the justification at the source is correct; relaxing
  `FundingSourceInfo.hbm.xml` would let unjustified audit rows through and would only move the failure to MySQL
  whenever the server runs in strict mode.
- **ADR-2: insert the current-phase row from `save()`, not from `prepare()`.** Creating rows during a GET would
  write on a read and would make funding sources appear in the phase listing just because someone opened them.
- **ADR-3: reuse the final `saveFundingSourceInfo()` call.** Persisting the new row early would run the forward
  replication twice per save.
- **ADR-4: restore the previous-phase row instead of evicting it.** The manager layer exposes no `refresh`/`evict`,
  and a snapshot copy stays inside the existing API.

## 16. Open Risks

- The two no-net-change audit entries on the previous-phase row described in §6 remain until the binding target is
  fixed at the view level.
- Funding sources whose current phase has no info row and no previous PLANNING row at all still break in
  `prepare()`, because `fundingSource.fundingSourceInfo.id` is rendered without a FreeMarker default in
  `buttons-fundingSources.ftl`. Not introduced here, not fixed here.
- The `budgetType` wipe described in the requirements Out-of-Scope list still applies to synced funding sources
  that do have project budgets.
