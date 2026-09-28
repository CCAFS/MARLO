# Kaizen Log

Continuous-improvement record for this project. The `## Active Lessons` digest below is
refreshed only by the `kaizen` skill's Apply Mode, on the default branch (`staging`). Other AKILI
commands read only this table — keep it at 10 rows or fewer. Per-retrospective entries
live in `docs/specs/kaizen/`, one file per spec.

## Active Lessons

| ID | Lesson | Source Spec | Severity | Target | Standardized In | Status |
|---|---|---|---|---|---|---|
| KZ-changes--migrate-ad-authentication-to-cognito--directory-abstraction-1 | A correction note is a self-declared index of a defect class: harvest every note quoting its own former text, grep that text family-wide, and treat any hit outside the note as a surviving sibling | changes/migrate-ad-authentication-to-cognito/directory-abstraction · **recurred in** auth-flow | **High** *(raised on recurrence)* | Product + Methodology | `.agents/reviewer.md` | Applied 2026-09-17 |
| KZ-changes--migrate-ad-authentication-to-cognito--directory-abstraction-2 | A fixture must not supply the value its assertion later checks — if the double is built from the same input the test passes in, the assertion cannot distinguish a correct mapping from an echo | changes/migrate-ad-authentication-to-cognito/directory-abstraction | Medium | Product + Methodology | `.agents/tester.md` | Applied 2026-09-17 |
| KZ-changes--migrate-ad-authentication-to-cognito--directory-abstraction-3 | In an append-only audit log, absolute intra-file line citations invalidate themselves — cite section headings | changes/migrate-ad-authentication-to-cognito/directory-abstraction | Medium | Product + Methodology | `docs/specs/general-setup/task.md` | Applied 2026-09-17 |
| KZ-changes--migrate-ad-authentication-to-cognito--auth-flow-1 | An amendment must record the closure sweep it ran — forward for the superseded value, backward for documents citing the amended section. An unswept correction is relocated, not applied | changes/migrate-ad-authentication-to-cognito/auth-flow | Medium | Product | `docs/specs/general-setup/requirements.md` | Applied 2026-09-17 |
| KZ-changes--migrate-ad-authentication-to-cognito--auth-flow-2 | Assert the arguments a test double receives — a double that discards them looks identical to one that checks them, and proves nothing | changes/migrate-ad-authentication-to-cognito/auth-flow | High | Methodology | `.agents/tester.md` | Applied 2026-09-17 |

> **ID note.** The `auth-flow` entry file numbers its own lessons `L1` / `L2`. Those labels are left
> untouched there; the digest carries them under the canonical `KZ-<safe-spec-slug>-<n>` grammar so the
> ID column stays uniform. `-1` is that entry's L1, `-2` is its L2.

> **Recurrence (AF-P5, applied 2026-09-17).** The `directory-abstraction-1` root cause — *every
> enumeration in the run was scoped to classes a reviewer had already named* — recurred twice in
> `auth-flow`, both times in the Leader's own work: T11b's *"there is no third route"* (a third existed
> in `ClarisaPublicAccesFilter:79`), and the `execution.md` §37.1 enumeration of nine `refuse()` callers
> that missed three refusals returning from the shared tail, which became V-7. Severity raised and
> `auth-flow` added as a source spec rather than opening a duplicate lesson.
