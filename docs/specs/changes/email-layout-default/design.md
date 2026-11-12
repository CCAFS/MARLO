# Branded email shell by default — Design

**Spec ID:** CHG-EMAIL-LAYOUT-001
**Status:** Approved 2026-11-11 — judgment rounds 1 and 2 applied; JUDGMENT: APPROVED (`judgment.md`)
**Owner:** Kenji Tanaka (IBD)
**Last Updated:** 2026-11-11
**Requirements:** `requirements.md` (approved 2026-11-11; amended by this design — see its Decision Log)
**Base:** `staging` `460ecbd5be`, branch `email-layout-default`
**Depth:** Standard

> **Answer first.** One shell in `global.properties`; every footer key read inside it is emptied or trimmed in the
> base file and in the two custom files that override it; a unit that opts out restores its footer with a
> documented block. The submit and unsubmit actions build their whole email — subject, body, footer and shell —
> from the texts of the unit they already name, because neither action loads that unit's i18n file today. No
> schema, no new action, no new dependency.

---

## 1. Architecture Summary

```mermaid
flowchart LR
  A[Action] --> T[UnitTextProvider of the action's unit]
  T --> B[subject + body + footer keys]
  B --> W[EmailLayout.wrap texts, assetsUrl, body]
  W --> K{email.layout for that unit}
  K -->|custom file defines {0}| C[opt-out: body as is + restored footer block]
  K -->|not defined / no file| G[global.properties: THE shell]
  C --> M[SendMailS.send -> email_logs]
  G --> M
```

Resolution order does not change (custom file over `global.properties`, P-1). What changes is the base value of
`email.layout` (from `{0}` to the shell), the base value of every footer key read inside it, and — for submit and
unsubmit only — which provider resolves the texts.

## 2. Module Footprint

### marlo-web — resources
| File | Change | Requirements |
|---|---|---|
| `global.properties` | `email.layout` = the shell (moved verbatim) and its comment rewritten (opt-out); `email.role.dear/getStarted/bye` shell-aware, `email.role.support` and `.support.noCrpAdmins` empty; `login.otp.email.html` without its support sentence; neutral `submit.email.message`, `.noPDF`, `unsubmit.email.message` | FN-001, FN-004, FN-005, FN-009 |
| `custom/aicrra.properties`, `custom/aiccra3.properties` | Remove `email.layout` and update the comment that refers to "the shell of email.layout above" (aicrra ~3210, aiccra3 ~3232); new submit/unsubmit copy (Appendix A) with `{6}` | FN-003, FN-004, FN-007, FN-008 |
| `custom/test.properties` | Remove `email.layout`; update its "shell of email.layout above" comment (~3307) | FN-004 |
| `custom/ciat.properties` | Remove `email.role.support`, `email.role.bye` overrides | FN-005 |
| `custom/pabra.properties` | Remove `email.role.support`, `.support.noCrpAdmins`, `.getStarted` overrides; `{6}` in both submit keys | FN-005, FN-007 |

### marlo-web — Java
| File | Change | Requirements |
|---|---|---|
| `action/projects/ProjectSubmissionAction.java` | Texts of the email (subject `:291`, body `:311-314`, footer `:317-319`) from a `UnitTextProvider` of `loggedCrp`; `{6}` set in both cycles (`:304-307`); the body variant chosen **after** PDF generation, `.noPDF` whenever no PDF is attached; footer from `email.role.support.noCrpAdmins` + `email.role.bye`; body wrapped with `EmailLayout.wrap(TextProvider, getBaseUrlCdn(), body)` at both sends (`:359`, `:362`); `CustomParameterManager` injected | FN-006, FN-007, FN-008, FN-010, FN-011 |
| `action/json/project/UnsubmitProjectAction.java` | Texts of the email (subject `:261`, body `:282`, footer `:283-285`) from a `UnitTextProvider` of the project's origin unit (the unit its `crp` argument already names, `:251`); footer from the `email.role.*` keys; body wrapped at the send (`:289`); `CustomParameterManager` injected | FN-006, FN-008, FN-010, FN-011 |
| `utils/EmailLayout.java`, `utils/OtpMailSender.java` | Javadoc/comment wording only: the shell is the default, `{0}` is the opt-out (no logic) | FN-002 |

### marlo-web — tests (consumers, P-9)
| Test | Assertion that changes |
|---|---|
| `utils/EmailLayoutTest` | `testBaseLayoutSendsTheBodyAsItIs` → base wraps; `testBaseRoleBlocksMatchTheSharedOnes` → replaced (role blocks shell-aware, shared `email.*` blocks unchanged); `testAiccraShellHoldsTheBodyAndTheImages` keeps passing (files fall back to base); **new**: footer-once across base + every custom file overriding `email.role.*`; opt-out fixture `{0}` |
| `utils/UnitTextProviderTest` | `aMissingNullOrUnsafeFileNameLeavesTheUnitOnGlobalOnly` and `twoProvidersAreIndependentAndHoldNoSharedState` (global answers the shell, not `BODY`) |
| `utils/OtpMailSenderTest` | `aUnitWithoutAShellSendsTheBareBodyAsBefore` → becomes the opt-out fixture; `theRenderedEmailStatesEveryRequiredElement` (asserts no "http" in the html part — the shell adds image URLs and a mailto); `everyDynamicValueIsHtmlEscapedInTheHtmlPartOnly` (expects the escaped `{3}` the base body no longer uses) |
| `action/home/OtpRequestCodeActionTest` | `aUnitWithoutACustomFileGetsTheGlobalTextsAndTheBareShell` → the base shell |
| `utils/EmailPlainTextTest` | none expected (loads aicrra over base); kept in the run |
| **new** copy test | Appendix A bodies per unit: arguments filled, no `<br><br>` in the six rewritten keys, no "Flagship" in base, no `102080`/`407` anywhere |

### Docs
| File | Change |
|---|---|
| `reports/ai-context/role-assignment-email-catalog.md` | Lines ~49-69 describe base `email.layout={0}` and the rollback; updated to the opt-out model |
| `changes/center-email-otp-login/requirements.md` | Decision Log entry: its "unit without a shell → bare body" now means "unit that opts out" (DD-8) |

### marlo-data / marlo-utils
Not applicable.

## 3. Data Model Changes
Not applicable — no table, column or seed change. The opt-out is an i18n override (NF-001).

## 4. API / Action Surface
No new action or endpoint. `{crp}/submit` (`marloDefaultStack`) and `unsubmitProject.do` (`jsonStackWithAudit`) keep
their routes, stacks and responses. The stacks are deliberately not changed (DD-6).

## 5. Frontend Composition
Not applicable — email HTML lives in `.properties` values.

## 6. Persistence & Phase Replication Plan
Not applicable — the existing `submissions` and `email_logs` writes are unchanged.

## 7. Validation & Save Pipeline
Not applicable — the email is built after the save, as today.

## 8. Permissions & Edit Gates
Not applicable — unchanged (the missing check on `unsubmitProject.do` is out of scope).

## 9. Specificity / Feature-Flag Strategy
Not applicable — opting out is an i18n block in the unit's custom file (DD-9), not a `parameters` row.

## 10. Integration Points
SMTP via `SendMailS` (unchanged; non-production still sends only to the support mailbox). Shell images from
`getBaseUrlCdn()` as today (P-6).

## 11. Observability
`email_logs` stores the wrapped HTML per send and is the verification surface. When an action cannot read its
unit's `crp_custom_file`, it logs one WARN naming the role of the unit ("the submitting unit" / "the unit owning the
project") with the exception, and falls back to its own `getText` (today's behavior). The message does not carry the
unit id *(corrected at validate 2026-11-12, W1; adding the id is left to the submit/unsubmit bugfix proposal)*.

## 12. Performance & Scalability
`UnitTextProvider` bundles are cached by `ResourceBundle` (P-7); one `custom_parameters` read per send. Negligible.

## 13. Security Considerations
No new input. The unescaped `{7}` justification is unchanged and out of scope.

## 14. Backwards Compatibility & Rollout
- **Gate:** Q1 answered 2026-11-11 — every Global Unit receives the shell, none opts out (P-11 settled by owner decision).
- **Visible change** for every unit without its own `email.layout`: role, welcome, OTP, submit and unsubmit emails gain the shell. Units outside Appendix A keep their own submit/unsubmit wording (legacy `<br>` copy) inside the shell, with the new footer; aicrra/aiccra3 already send no "To begin…" line (empty `email.role.getStarted`). Accepted.
- **Failed-PDF planning submissions** of every unit now use that unit's `.noPDF` copy with the real `{6}` link (DD-5) — a visible change for alliance, bigdata, eib, gender, pim, test, wheat as well. Accepted.
- **Opt-out** before release for any unit that must stay plain: the DD-9 block in its custom file.
- **Rollback:** revert the branch's commits; no data to undo.

## 15. Decision Records

| ID | Decision | Rationale / rejected alternatives | Req |
|---|---|---|---|
| DD-1 | The shell moves verbatim to `global.properties`; the three custom copies are deleted, not set to `{0}`. | AICCRA/AICCRA_III inherit an identical value (P-2). TEST inherits it too, but its footer keys are the base ones (P-5), so TEST's look changes — accepted, TEST is a test unit (JD-3). | FN-001, FN-003, FN-004 |
| DD-2 | Footer-once is enforced in the base data: `email.role.support` and `.support.noCrpAdmins` empty, `.bye` without the do-not-reply notice, `.getStarted` as a paragraph, `login.otp.email.html` without "Need help? Contact … at {3}". Same values AICCRA ships since A2-2484. | Code-side stripping hides text from translators. Keeping "contact {0}" in `email.role.support` was rejected: callers pass an admins list built from the unit's admin role, which is empty when the unit has no active admins (`SendEmails.java:323`, `ProjectPartnerAction.java:815`, `CrpPpaPartnersAction.java:370`), and the sentence would read "please contact ." (JD-6). | FN-005 |
| DD-3 | `ciat` and `pabra` `email.role.*` overrides are deleted so they inherit DD-2; `alliance`'s `getStarted` stays. | Their values restate the support address/notice (P-5); pabra's `getStarted` points at the AICCRA domain. | FN-005 |
| DD-4 | Submit/unsubmit switch their footer from `email.support.noCrpAdmins`/`email.getStarted`/`email.bye` to the `email.role.*` keys. | The legacy keys feed unwrapped emails too (P-10). | FN-005, FN-006 |
| DD-5 | `{6}` computed in both cycles; the planning (PDF) variant is used only when a PDF is actually attached, otherwise `.noPDF`. `{6}` is read by every `.noPDF` copy (base, alliance, bigdata, eib, gender, pim, test, wheat already use it; aicrra, aiccra3, pabra will) and by the rewritten planning copies of aicrra, aiccra3 and the base; the other units' planning copies ignore it, unchanged. Consequence for the seven units with their own `.noPDF`: a failed-PDF planning submission now sends their `.noPDF` text with a working link instead of a text claiming an attachment (§14). | Today `{6}` is null in planning (P-4) and "attached" is claimed even when PDF generation fails (`:327-352`, catch leaves `buffer == null`). | FN-007 |
| DD-6 | Both actions resolve the **whole email** — subject, body, footer keys and the shell — with `UnitTextProvider.forCustomFile(<unit crp_custom_file>)`, the unit being the one each action already names in its `crp` argument: `loggedCrp` for submit (P-12), the project's origin unit for unsubmit. On a lookup failure: one WARN and the action's own `getText`. | Neither action runs `i18nFile` (P-3), so `getText` reads whichever custom bundle the shared provider last held — live evidence `email_logs` 18405. Rejected: adding `i18nFile` to the stacks (changes every action of `marloDefaultStack`/`jsonStackWithAudit`; still mutates a shared provider); adding bundles in the action as `ProjectLeaderEditAction:186-208` / `ValidateProjectSectionAction:722-744` do (same race). Same pattern as `OtpRequestCodeAction.unitTexts`. | FN-008, FN-010, FN-011 |
| DD-7 | *(withdrawn after judgment JD-1: its premise "submit runs i18nFile" was false; superseded by DD-6.)* | — | — |
| DD-8 | The OTP spec's "unit without a shell → bare body" becomes "unit that opts out → bare body"; recorded as a Decision Log entry there. | The OTP code path is unchanged; only the base value moves. | FN-001, FN-002 |
| DD-9 | **Opt-out block.** A unit that must stay plain sets, in its custom file, `email.layout={0}` **and** restores the pre-shell footer keys (`email.role.support`, `.support.noCrpAdmins`, `.bye` with the do-not-reply notice) and `login.otp.email.html` with its support sentence — the current base values, kept verbatim in the comment above `email.layout` in `global.properties`. The restored `email.role.support` keeps "contact {0}", so an opted-out unit with no active admins keeps today's "please contact ." gap — accepted for opt-out, unchanged from today. `email.role.dear` and `.getStarted` are intentionally not part of the block: they carry neither the support address nor the notice, so the new base values read correctly without the shell. | Without them an opted-out unit would send no support address and no notice (JD-2). A code switch was rejected (NF-001: no Java change per unit). | FN-002, FN-005 |
| DD-10 | Invariant: the shell hardcodes `MARLOSupport@cgiar.org`; a unit that needs another address overrides `email.layout`. | `footer.contactEmail` is the same in every file today (P-8); after A.5 the base OTP html no longer reads it (JD-8). | FN-005 |

### Reversion challenge (Step 2.3)
DD-1/DD-2/DD-3/DD-5 remove delivered behavior. *What does removing this break?*
- Plain emails for non-shell units: replaced by the shell; opt-out keeps the old look with its footer (DD-9).
- Support sentence / admins clause in the body: the shell footer carries the support address; the admins clause is dropped (DD-2), as AICCRA did — the empty-list case made it unsafe.
- TEST's current look (DD-1): changes, accepted.
- "Attached" wording when no PDF: replaced by `.noPDF` (DD-5) — the false claim goes away.
- Tests pinning the old default (§2) change on purpose; `testBaseRoleBlocksMatchTheSharedOnes` is replaced because DD-4 needs role and shared blocks to differ.
No unaddressed breakage.

### Budget (Step 2.4) — tripwire for `/akili-execute`
| Expected tasks | Expected LOC (prod + test + docs) | Expected review rounds |
|---|---|---|
| 8 | ~380 (Java ~110, properties ~60, tests ~180, docs ~30) | 10 (one per task + 2 margin; Implementer→Reviewer loop, separate from the two-round judgment cap) |

Depth check: Standard holds.

## 16. Premise Ledger

Count: 14 rows — 13 verified, 1 settled by owner decision (P-11), 0 `UNVERIFIED`. Rows with Impact High overall: P-1, P-2, P-5, P-8, P-11.
Blast-radius triggers: `live-path` (submit/unsubmit user actions — P-3, P-4, P-12), `shared-state` (base
`email.layout` and `email.role.*` read by every wrapped email — P-5, P-13), `consumer` (tests and docs — P-9).

| # | Claim | Class | Citation (as run) | Verified at | If false | Settled by |
|---|---|---|---|---|---|---|
| P-1 | Texts resolve custom file first, then `global.properties`; a missing file falls back silently | location | `InternationalitazionFileInterceptor.intercept` (addDefaultResourceBundle); `UnitTextProvider.java:76-81` catches `MissingResourceException` | 460ecbd5be | DD-1 fails for file-less units (High) | — |
| P-2 | The three shells are identical and generic, with the support mailto | data-env | `awk '/^email.layout=/{f=1} f{print} f&&!/\\$/{exit}' custom/{aicrra,aiccra3,test}.properties \| md5` → `6b52db6b…` ×3 | 460ecbd5be | DD-1 changes AICCRA output (High) | — |
| P-3 | **Neither** `{crp}/submit` nor `unsubmitProject.do` runs `i18nFile` | live-path | `struts-projects.xml:7-8` package `projects` extends `marlo-default`; `:1140-1148` submit declares no interceptor; `struts.xml:591` `default-interceptor-ref marloDefaultStack` (body `:584-589`, no `i18nFile`; stacks with it: `struts.xml:198-573`, plus `struts-home.xml:40, 93`); `struts-json.xml:6` `json` extends `json-default`, `:36-43` `jsonStackWithAudit` without it; live: `email_logs` 18405 AICCRA_III submit with AICCRA wording | 460ecbd5be | DD-6 unnecessary (Low) | — (corrected after JD-1) |
| P-4 | Submit sets `{6}` only outside planning and picks the variant before generating the PDF | live-path | `ProjectSubmissionAction.java:304-314` (`{6}` and variant), `:327-352` (PDF; catch leaves `buffer` null), `:359/:362` sends | 460ecbd5be | DD-5 scope changes (Low) | — |
| P-5 | `email.role.*` overrides: ciat (`support`, `bye`), pabra (`support`, `support.noCrpAdmins`, `getStarted`), alliance (`getStarted`), aicrra/aiccra3 (all, shell-aware); **test none** — it uses the base keys | shared-state | `grep -H -E '^email\.role\.' global.properties custom/*.properties` (no `test.properties` line) | 460ecbd5be | A unit repeats its footer, or TEST is miscounted (High) | — |
| P-6 | `EmailLayout.wrap(TextProvider, String, String)` exists and both actions extend `BaseAction` (`getBaseUrlCdn()`) | location | `EmailLayout.java:45-57`; both class declarations `extends BaseAction` | 460ecbd5be | Another wrap path needed (Low) | — |
| P-7 | `UnitTextProvider` is thread-safe, cached, and answers `getText(key)`, `getText(key, String[])`, `hasKey` | data-env | `UnitTextProvider.java` class Javadoc and methods | 460ecbd5be | DD-6 needs another provider (Low) | — |
| P-8 | `footer.contactEmail` = `MARLOSupport@cgiar.org` in every file defining it | data-env | `grep -H '^footer.contactEmail=' global.properties custom/*.properties` → 5 hits, same value | 460ecbd5be | DD-2/DD-10 drop a different address (High) | — |
| P-9 | Consumers: the five test files and the methods listed in §2; `reports/ai-context/role-assignment-email-catalog.md` (updated); `changes/center-email-otp-login/{requirements,design,tasks,execution}.md` (historical; only its `requirements.md` gets the DD-8 Decision Log entry) | consumer | `grep -rln -e 'email\.layout' -e 'EmailLayout' -e 'email\.role\.' marlo-web/src/test` → 5 files; `grep -rln 'email.layout' reports docs` → the catalog, the four OTP spec files, this spec | 460ecbd5be | A test or doc goes stale unnoticed (Low) | — |
| P-10 | `email.bye` is read by 12 classes, `email.role.bye` by 8 (7 actions + `SendEmails`) | consumer | `grep -rl 'getText("email.bye")' marlo-web/src/main/java \| wc -l` → 12; `'getText("email.role.bye")'` → 8 | 460ecbd5be | DD-4's rejected alternative becomes viable (Low) | — |
| P-11 | Production active Global Units | data-env | Not needed — owner decision 2026-11-11: every unit receives the shell, so no opt-out list depends on which units are active (local copy: 45, 47, 48, 49, 50 active; 17 legacy custom files exist) | — | Opt-out list (empty), and the visible change for every legacy unit — accepted (High) | Q1 — Kenji Tanaka, answered 2026-11-11 |
| P-12 | Submit names the session unit: `{1}` from `loggedCrp`, the summary URL from `getCurrentCrp()` — both the session's `SESSION_CRP`; unsubmit names the project's origin unit | location | `ProjectSubmissionAction.java:149-150` (`loggedCrp` = session `SESSION_CRP`), `:281`, `:305` (`getCurrentCrp()`); `UnsubmitProjectAction.java:251` (`globalUnitProject` origin) | 460ecbd5be | DD-6 picks a different unit (Low) | — |
| P-13 | Callers pass `email.role.support` an admins list that can be empty | shared-state | `SendEmails.java:300-323`; `ProjectPartnerAction.java:815, 995, 1125`; `CrpPpaPartnersAction.java:370` | 460ecbd5be | DD-2 could keep the admins clause (Low) | — |
| P-14 | 30 classes send through `SendMailS`; 8 of them wrap their body; `OtpMailSender` also wraps but sends through its own transport (9 wrapping files, 39 call sites); after this spec 10 of the 30 wrap and 20 do not | data-env | `grep -rl -e 'sendMail\.send' -e 'sendMailS\.send' -e 'sendMail\.sendTemporal' marlo-web/src/main/java \| wc -l` → 30; `grep -rl 'EmailLayout\.wrap' marlo-web/src/main/java` → 9 files, of which `OtpMailSender` is not among the 30; `grep -rn 'EmailLayout\.wrap' marlo-web/src/main/java \| wc -l` → 39 | 460ecbd5be | Scope counts in requirements §4 wrong (Low) | — |

Divergence noted (JD/JS-6): for a Center (type 4) session `InternationalitazionFileInterceptor` picks the file by the
origin unit's acronym; DD-6 reads `crp_custom_file`, as `OtpRequestCodeAction` does. Submit/unsubmit are CRP
flows; no Center unit uses them today (not verified in production; Q1 was settled by decision, so this stays an assumption).

## 17. Open Risks

| Risk | Mitigation |
|---|---|
| A legacy unit active in production changes look unexpectedly | Accepted by the owner (Q1, 2026-11-11): every unit changes look; DD-9 opt-out stays available |
| Email clients render the shell differently | In production for AICCRA since A2-2484; human render check in the verification task |
| `MessageFormat` mangles new copy | Appendix A has no apostrophes; marlo-verify i18n gate; copy test |
