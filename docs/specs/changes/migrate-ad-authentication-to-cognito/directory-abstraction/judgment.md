# Directory Abstraction — Judgment Day Ledger

**Target:** `design.md` (immutable during judgment)
**Transaction:** `JD-DIRABS-001`
**Mode:** `judgment_day` — blind dual review
**Round:** 1 of max 2
**Date:** 2026-08-27
**Terminal state:** `approved` — round-one fixes applied, no scoped re-judgment (user elected *Fix only*)

## Protocol record

| Element | Value |
|---|---|
| Judges | 2, blind to each other, identical scope and criteria |
| Judge model | `sonnet` — **different from the design's author** (`opus`). Author ≠ auditor, per the skill's adaptation rule |
| Judge access | Structurally read-only agent type (no `Write`/`Edit`), not read-only by instruction |
| Persistence | Parent orchestrator only. Judges wrote nothing |
| In-scope corroboration set | `requirements.md`, `proposal.md`, `family.md`, all 4 `analysis/*.md`, root `CLAUDE.md` + `AGENTS.md`, **and the live source code** |
| Judge A | severe=1 · warning=4 · suggestion=2 · verified=27 · 74 tool calls |
| Judge B | severe=3 · warning=3 · suggestion=2 · verified=22 · 71 tool calls |
| `review-refuter` | Not launched — two-judge agreement is the corroboration mechanism |

---

## Merged findings

### CONFIRMED SEVERE — both judges

#### JD-1 — `DIRABS-NF-002` and the `D4` gate are unsatisfiable as written

**Reported by:** Judge A `[S-1]` · Judge B `[S-3]`, `[G-2]`
**Parent verification:** ✅ confirmed against the code.

`requirements.md` `DIRABS-NF-002` states the post-completion expectation as *"`marlo-data/src` exactly
**two** (`LDAPAuthenticator`, `LdapDirectoryService`)"*, and `§9` `D4` bills the grep as the spec's one
*"genuinely falsifiable"* gate. **Two independent defects make it fail on a correct implementation.**

**(a) `APCustomRealm` was omitted from the `marlo-data` count.**

```
grep -rn "^import org.cgiar.ciat" marlo-data/src --include="*.java"
  APCustomRealm.java:28      import org.cgiar.ciat.auth.LDAPService;
  APCustomRealm.java:29      import org.cgiar.ciat.auth.LDAPUser;
  LDAPAuthenticator.java:21  import org.cgiar.ciat.auth.ADConexion;
  LDAPAuthenticator.java:22  import org.cgiar.ciat.auth.LDAPService;
```

Two files today; adding `LdapDirectoryService` makes **three**, not two. `design.md` §2.2 *itself*
documents `APCustomRealm` as untouched *"including its own `LDAPService` call at `:287`"* — the
evidence was in the same document, one section from the wrong count.

**(b) The grep pattern false-positives.** The command matches the bare string `org.cgiar.ciat`, and
`marlo-web/.../ocs/ws/client/WSMarlo.java` contains `org.cgiar.ciat.abw.control.logic` in JAX-WS
annotation **string literals** with **no import at all**. Today the gate returns **9** `marlo-web`
files, not the 8 the documents assert; after completion it would return **2**, not 1.

**Inherited, not invented here.** `EXEC-040` in the approved execution plan lists the same expected
`marlo-data` output and likewise omits `APCustomRealm`. The runbook carries the defect too.

**Why it is severe:** a Reviewer following the spec literally will read a correct implementation as
failed, or will "fix" the discrepancy by loosening the expected file list — destroying the only gate
`§9` identified as genuinely falsifiable.

**Required fix:** scope the pattern to `^import org.cgiar.ciat.auth`, and correct both expected
lists to `marlo-web` → `searchUsersUtil` (1 file) and `marlo-data` → `APCustomRealm`,
`LDAPAuthenticator`, `LdapDirectoryService` (3 files). Flag the `EXEC-040` defect for the execution
plan at archive time.

---

### CONFIRMED — both judges, severity split

#### JD-2 — Line-number regressions in `design.md` and `requirements.md`

**Reported by:** Judge B `[S-2]` (severe) · Judge A `[W-2]`, `[G-2]` (warning / suggestion)
**Parent verification:** ✅ confirmed. Judge B's severity assessment is the better read of *provenance*;
Judge A's is the better read of *blast radius*. Both are recorded.

| Citation in the two new docs | Actual | Used as |
|---|---|---|
| `ContactPersonAction:98` — the live search | **`:99`** (line 98 is blank) | The protected boundary in DD-7, `FN-008`, §2.2 |
| `getADFilter :56-70` | **`:58-71`** | Protected region |
| `validateOutlookUser :248-262` | **`:248-263`** | §6.2 change scope |

**This is a regression, not inherited drift.** `proposal.md` and the execution plan both carry the
**correct** values (`:99`, `:58-71`, `:248-263`); only `requirements.md` and `design.md` — the two
documents that assert *"All line numbers verified against `staging-cognito-impl` on 2026-08-27"* —
have the wrong ones. The claim of verification was not honored for these three.

**Mitigation (Judge A):** line 98 is blank, so *"protect `:98` and below"* still covers `:99` in
practice. The defect is in the document's credibility, not yet in an implementer's behavior.

#### JD-3 — `BaseAction` line count

**Reported by:** Judge A `[W-1]` · Judge B `[W-3]` · **Parent verification:** `wc -l` → **9753**.

All four family documents say **9,748**, carried from the analysis. Load-bearing context for DD-2, and
never re-derived — the same copy-forward failure Step 2.3 exists to catch.

#### JD-4 — "private duplicate" contradicts the target's own C-3

**Reported by:** Judge A `[W-3]` · Judge B `[W-2]` · **Parent verification:** ✅ `GuestUsersValidator.java:36`
is `public LDAPUser getOutlookUser(String email)`.

`design.md` §3 and §6.2 call it *"the private duplicate"*. `design.md` §10.0 **C-3**, four sections
later, opens with *"The method is **`public`**, not private."* The C-3 reasoning is correct; the
labels in §3/§6.2 are wrong, and `requirements.md` and `proposal.md` repeat the wrong one.

**Why it is more than terminology:** a reader skimming §6.2 would audit for missed callers among
`private` methods only, and skip the OGNL/external-caller check that C-3 correctly performed.

---

### CONTRADICTION — resolved by parent verification

#### JD-5 — `json/global/ManageUsersAction` `toLowerCase` line

| | Claim |
|---|---|
| Judge B `[W-1]` | `:156`, not `:155` |
| Judge A | Listed `:155` in **VERIFIED CORRECT** |

**Parent verification — Judge B is right:**

```
151: LDAPUser LDAPUser = this.getOutlookUser(newUser.getEmail());
154:   newUser.setFirstName(LDAPUser.getFirstName());
155:   newUser.setLastName(LDAPUser.getLastName());
156:   newUser.setUsername(LDAPUser.getLogin().toLowerCase());
```

`design.md` §4.3 — the table explicitly billed as *"the table a reviewer checks line by line"* — says
`:155`. Wrong.

> **Judge A produced a false verification.** It asserted `:155` as confirmed. This is worth recording
> beyond the fix itself: a judge's `VERIFIED CORRECT` section is not self-certifying, and the
> contradiction is what exposed it. Escalating a settleable fact to a human would have been the wrong
> move; verifying it directly cost one `grep`.

---

### SUSPECT — one judge only · **no auto-fix per protocol**

#### JD-6 — "six migrated callers" contradicts the target's own diagram *(Judge B `[S-1]`, severe)*

**Parent verification:** ✅ **the finding is correct.**

`design.md` §1 opens *"One interface, one implementation, **six** migrated callers."* But:

- §2.1's diagram lists **5** callers.
- §3's tree tags **5** files `← migrated`.
- `BaseAction` is **deleted, not migrated** — DD-2's entire argument is that it calls `DirectoryService`
  never and gains no dependency.
- `ContactPersonAction` has its AD code **deleted**, not migrated (DD-7).

So exactly **5** classes call `DirectoryService.findByEmail` after this spec. The "6" is inherited from
the execution plan's `EXEC-034…039` (6 *tasks*, one of which is the `BaseAction` deletion) and from the
family documents' 6-row tables — **DD-2 is what made it wrong**, and the headline sentence was not
updated.

**Judge A did not report it.** Recorded as suspect per the Hard Rules; recommended for fix because the
parent verified it independently and it is the document's first sentence.

#### JD-7 — the `ERROR` propagation mechanism is unspecified *(Judge A `[W-4]`, warning)*

**Parent assessment:** ✅ **a real design gap, and the most substantive single-judge finding.**

DD-3 says `center/…/ManageUsersAction` *"propagates"* on `source == ERROR`, *"preserving today's
observable outcome exactly."* But:

1. The contract **never throws** (§5.1, reaffirmed in DD-3's rejected-alternatives table), so the
   consumer must **synthesize** an exception to propagate at all.
2. **`design.md` never names the exception type.** An implementer has no instruction for this branch.
3. *"Exactly"* overstates it: today's throwable is whatever `adauth` raised natively; a manufactured
   one is a different class with a different message and stack.

**Judge A's mitigation, which the design does not make:** `struts.xml:540-546`'s only relevant
`global-exception-mapping` is on `java.lang.Exception`, so the Struts-level observable result is
likely unaffected by the subtype. **The equivalence claim currently rests on an unstated assumption
rather than a verified one.**

**Judge B did not report it.** Recorded as suspect; recommended for fix — it is the one finding that
would leave an implementer genuinely unable to proceed.

#### JD-8 — `proposal.md` cites `struts-json.xml:1042`; actual is `:1041` *(Judge A `[G-1]`)*

`requirements.md` has it right. Two same-date documents in the corroboration set disagree, and only one
is correct — a clean instance of the *"agreement is not corroboration"* rule.

#### JD-9 — *"the only `outlook` hit in `webapp/`"* overclaims *(Judge B `[G-1]`)*

A case-insensitive search also matches a PDF under `global/documents/`. Judge A independently reported
the hits as *"person emails in `jquery/AUTHORS.txt` (2 lines)"* — the judges differ on the inventory of
an incidental term. **Both agree the claim that matters is true:** zero hits for
`getOutlookUser` / `outlookUser` / `OutlookUser` anywhere under `webapp/`. Scope the phrasing or drop it.

---

## Coverage — what the judges confirmed as correct

Both judges independently verified, and the parent spot-checked:

| Claim | Status |
|---|---|
| `BaseAction.getOutlookUser` at `:4802-4816`, imports `:103-104` | ✅ both |
| **Exactly 2 external callers**, zero references in all **303** templates | ✅ both, exhaustive full-text search |
| DD-3's central premise: `create():127-131` maps `null` → `manageUsers.email.doesNotExist` | ✅ both, exact |
| DD-3's premise: `validateOutlookUser:255` has **no `try/catch`** | ✅ both |
| C-4 finding 2: convention plugin on classpath (`pom.xml:89`), `mapAllMatches=true` (`struts.xml:25-28`), **no locator restriction anywhere** | ✅ both |
| `center/…/ManageUsersAction` in no `struts-*.xml` | ✅ both |
| 8 real `adauth` importers in `marlo-web` today | ✅ both (WSMarlo confirmed a false positive, not a 9th) |
| The analysis's `DirectorySource` baseline is genuinely 7 values → "8, not 7" is accurate | ✅ both, at `analysis:783` |
| Budget task count **16** (`EXEC-030…041` + `EXEC-050…053`) | ✅ both, against the plan's own headers |
| `Users.hbm.xml:19` `unique="true"` — DD-4's blast-radius argument | ✅ both |
| `searchUsersUtil:25` sole reader of `getAttributes()` in the repo | ✅ both |
| DI chain: `@ComponentScan:45`, `struts.xml:9-12`, `BaseValidator:52-53`, `ReportSynthesisSectionValidator:82` | ✅ both, exact |
| 3 test files repo-wide; no `src/test` in `marlo-data`/`core`/`utils`; `Dockerfile` skips tests | ✅ both — DD-10 / D8 stands |
| `SearchUserAction`'s 9 `userFound` keys and their insertion order | ✅ both, exact |
| Constitutional compliance (no `/api/*`, no JSP, no migration, no POM change, English-only) | ✅ both |

**No judge challenged DD-1, DD-5, DD-6, DD-9, or DD-10.** No judge challenged the substance of DD-2 or
DD-3 — both audited their premises against the code and confirmed them. The confirmed defects are
**bookkeeping and one unspecified mechanism**, not the architecture.

---


---

## Round-one correction — APPLIED

**User decision: "Fix only".** All nine findings applied; **no scoped re-judgment run.**

| # | Finding | Confirmed by | Applied |
|---|---|---|---|
| JD-1 | Broken `NF-002` / `D4` gate | both judges | ✅ Pattern scoped to `^import org.cgiar.ciat.auth`; `marlo-web` → 1 file, `marlo-data` → **3** files. A box in `requirements.md` §9 records both failure modes and flags the inherited `EXEC-040` defect |
| JD-2 | Line-number regressions | both judges | ✅ `:98`→`:99`, `:56-70`→`:58-71`, `:248-262`→`:248-263`, in both documents |
| JD-3 | `BaseAction` 9,748 → **9,753** | both judges | ✅ corrected in all four family documents |
| JD-4 | "private duplicate" → `public` | both judges | ✅ corrected in `design.md` §3/§6.2, `requirements.md`, `proposal.md` |
| JD-5 | `:155` → **`:156`** | contradiction, parent-resolved in B's favor | ✅ `design.md` §4.3 |
| JD-6 | "six migrated callers" → **five** | Judge B only, parent-verified | ✅ `design.md` §1 |
| JD-7 | `ERROR` propagation unspecified | Judge A only | ✅ **New design content: DD-3a + `DirectoryLookupException`.** See below |
| JD-8 | `:1042` → `:1041` | Judge A only | ✅ `proposal.md` |
| JD-9 | "only outlook hit" overclaim | Judge B only | ✅ re-scoped to the search that matters |

### JD-7's fix added design content, and that is worth flagging

The other eight fixes were bookkeeping. This one introduced a **fifth new production file**:

| Added | Why forced, not chosen |
|---|---|
| `DirectoryLookupException extends RuntimeException` | **Unchecked is forced by the code:** `validateOutlookUser:248` declares no `throws` clause, so a checked exception would not compile without rippling into `create()` |
| Must **not** extend `AuthorizationException` | `struts.xml:543-545` maps that to **403**, not 500 — extending it would be a silent behavior change. Verified 2026-08-27: exactly two global mappings exist |
| Named rather than `IllegalStateException` | Makes the branch assertable by type via JUnit 4 `@Test(expected = …)`. `IllegalStateException` could be raised by unrelated code, so a test asserting it would pass for the wrong reason |

**Budget revised: ~650 → ~700 LOC.** `design.md` §9 and both Decision Logs record it.

**The overstatement is also corrected.** DD-3's *"preserving today's observable outcome exactly"* became
the verified boundary: **same handling and same 500 page, different exception subtype.** The subtype
difference is unavoidable once the seam exists, and saying so is more useful than claiming otherwise.

---

## Terminal receipt

| Field | Value |
|---|---|
| Target | `design.md` |
| Rounds used | **1** fix round of 2 · **0** scoped re-judgments of 2 |
| Confirmed (both judges) | 5 — all applied |
| Suspect (one judge) | 4 — all applied on recommendation after parent verification |
| Contradictions | 1 (JD-5) — resolved by parent verification, not escalated |
| Architecture findings | **0.** No judge challenged any design decision's substance |
| Judge false positives | 1 — Judge A verified `:155` as correct; it is `:156` |

**JUDGMENT: APPROVED ✅**

> **Qualified approval, stated honestly.** The user elected *Fix only*, so **the fix delta was not
> independently re-judged.** Eight of nine fixes are mechanical and self-evident from the citations in
> this ledger. **The ninth (JD-7 / DD-3a) is new design content that no judge has seen.** If it warrants
> independent review, the protocol permits one more scoped re-judgment over the frozen ledger plus the
> fix delta — 1 fix round and 2 re-judgments remain unused.
