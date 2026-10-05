# Retire The COVID-19 Impact Section — Execution Log

## Document Control

| Field | Value |
|---|---|
| **Spec Path** | `changes/retire-covid19-impact-section` |
| **Spec ID** | CHG-RETIRE-COVID19 |
| **Approval Mode** | `gated` |
| **Branch** | `A2-2578-Fix-Cluster-Description-and-Managing-Partners-bugs` — user decision 2026-10-01, overriding `tasks.md` *Branching: staging* |
| **Started** | 2026-10-01, at `dc16189ea6` |
| **Leader** | `opus` (session) · Implementer `akili-implementer` (`sonnet`) · Reviewer `akili-reviewer` (`opus`) |
| **Budget** | 5 tasks · ≤ 60 LOC added · 1 review round per task (`design.md` §12) |

### Pre-flight

| Item | State |
|---|---|
| Spec approved, `judgment.md` APPROVED ✅ | Confirmed |
| Working tree clean | Confirmed (`git status --short` empty). The branch's own A2-2578 commits touch `global.properties`, `custom/aiccra3.properties`, `custom/aicrra.properties` — only T05 touches those, and it removes different keys |
| FN-004 baseline | **Pending — owner: the user**, asked at the first HITL pause (`tasks.md` §2) |
| Line endings of T01's edited files | `struts-projects.xml`, `menu-projects.ftl`, `boardSummaries.ftl`: 0 CR each (LF) |

## Task Execution History

### CHG-RETIRE-COVID19-T01 — Remove the screen, the report and their routes

| Field | Value |
|---|---|
| **Final status** | PASS |
| **Date** | 2026-10-01 |
| **Attempts** | 1 |
| **Requirements covered** | FN-002 (scenario *Summaries board*); FN-003 (scenario *Retired routes* — route removal only; the 404 is observed in T05) |

**Attempt 1**

- **Files changed:** deleted `marlo-web/src/main/java/org/cgiar/ccafs/marlo/action/projects/ProjectImpactsAction.java`,
  `marlo-web/src/main/java/org/cgiar/ccafs/marlo/action/summaries/ImpactCovid19SummaryAction.java`,
  `marlo-web/src/main/webapp/WEB-INF/crp/views/projects/projectImpacts.ftl`,
  `marlo-web/src/main/webapp/crp/js/projects/projectImpacts.js`, `marlo-web/src/main/resources/pentaho/crp/ImpactCovid19.prpt`;
  edited `marlo-web/src/main/resources/struts-projects.xml`, `marlo-web/src/main/webapp/WEB-INF/crp/views/projects/menu-projects.ftl`,
  `marlo-web/src/main/webapp/WEB-INF/crp/views/summaries/boardSummaries.ftl`. 8 files, 792 deletions, 0 insertions.
- **Implementer verification:**
  - `~/.claude/skills/marlo-verify/scripts/clean-compile.sh` → `BUILD SUCCESS (marlo-data,marlo-web, clean recompile)`;
    2416 marlo-data, 1044 marlo-web, 41 test sources compiled.
  - `grep -rnI -E "ProjectImpactsAction|ImpactCovid19SummaryAction|projectImpacts\.(ftl|js)|ImpactCovid19\.prpt|impactCovid19Summary|'covid19'|\{crp\}/impacts\b" marlo-web/src/main`
    → 12 hits, all `summaries.board.report.impactCovid19Summary[.description]` in `global.properties` and
    `custom/{aicrra,alliance,aiccra3,pabra,test}.properties`; with `| grep -v '\.properties:'` → 0.
- **Evidence re-run (Leader-inline, non-author):** VERIFIED — same compile result and file counts; sweep 12 / 0;
  `grep -c $'\r'` → 0 on all three edited files (LF kept).
- **Review intensity:** Reviewer owed — `Consumers` is not `none` (condition 3), and override (b) applies (a closed
  routing config and a stored enum-adjacent menu slug).
- **Reviewer (`akili-reviewer`, opus):** PASS — the diff matches T01's file list and `design.md` §3 marlo-web exactly;
  nothing outside the task touched; FN-002/FN-003 route removal satisfied; DD-1 order held; the sweep amendment fixes
  a spec defect and does not change FN-002, FN-003 or DD-4.
- **runtime events:** none
- **spawns:** Implementer 30 calls, 89,291 tokens, ended complete; Reviewer 9 calls, 55,109 tokens, ended complete.

**ADVISORY (Reviewer, 4R — recorded, not gating)**

- *Risk:* do not amend as a blanket `grep -v '\.properties:'`; keep the unfiltered 12-hit result as the evidence —
  **applied** in the amendment below.
- *Readability:* the *Done when* line needed the same correction as *Verification* — **applied**.
- *Reliability:* hand the 12-hit key list to T05 as its starting input; T05 still re-runs its own per-key grep.

**Decisions made**

- **Execute-time spec edit — `tasks.md` T01 *Done when* and *Verification* (2026-10-01).** Former text quoted:
  *"Done when: clean recompile succeeds, and the sweep below returns 0."* and *"… marlo-web/src/main` → 0 hits."*
  Reason: the pattern contains `impactCovid19Summary`, which necessarily matches the i18n keys that DD-4 and T05
  step 1 remove only after the code is gone, so "→ 0" was unreachable at T01. Amended to: the unfiltered sweep
  returns only those i18n keys, and the same command `| grep -v '\.properties:'` returns 0. No requirement's meaning
  changes (Reviewer-confirmed). Carry to the T02 Reviewer brief as a named conformance check.
- Added `[ ]` / `[x]` status markers to the five task headings of `tasks.md`; the approved file had none.
- **Forward pointer → T05:** the 12 i18n hits above (`summaries.board.report.impactCovid19Summary`,
  `summaries.board.report.impactCovid19Summary.description` × 6 files) are T05 step 1's starting list, not its result.
- **Forward pointer → T03:** check whether T03's sweep pattern (`ProjectImpacts|project_impact|…`) hits `.properties`
  the same way before its *Done when* is judged.

**Issues encountered:** the Implementer staged its changes (`git rm` / index), not only the working tree; harmless,
noted for the commit step.

**Final verification:** clean recompile green; sweep 0 outside `.properties`.

### CHG-RETIRE-COVID19-T02 — Remove the section from web validation and completeness

| Field | Value |
|---|---|
| **Final status** | PASS |
| **Date** | 2026-10-01 |
| **Attempts** | 1 |
| **Requirements covered** | FN-001 (scenarios *Project menu of an active unit*, *A stale section name reaches the validator* — fully true once T03 removes `IMPACTS`, DD-5); FN-004 (diff read; human check in T05) |

**Attempt 1**

- **Files changed:** deleted `marlo-web/src/main/java/org/cgiar/ccafs/marlo/validation/projects/ProjectImpactsValidator.java`;
  edited `marlo-web/src/main/java/org/cgiar/ccafs/marlo/validation/projects/ProjectSectionValidator.java`,
  `marlo-web/src/main/java/org/cgiar/ccafs/marlo/validation/BaseValidator.java`,
  `marlo-web/src/main/java/org/cgiar/ccafs/marlo/action/json/project/ValidateProjectSectionAction.java`,
  `marlo-web/src/main/java/org/cgiar/ccafs/marlo/action/BaseAction.java`,
  `marlo-web/src/main/java/org/cgiar/ccafs/marlo/config/APConstants.java`. 6 files, 1 insertion, 231 deletions.
- **Implementer verification:**
  - `clean-compile.sh` → BUILD SUCCESS; 2416 marlo-data, 1043 marlo-web (= 1044 − the deleted validator), 41 test.
  - `checkstyle.sh --baseline` on the five edited files → `HEAD=13  working tree=13  delta=0`.
  - `java-hygiene-check.sh` → one line, pre-existing: `FIX JFREE L1697 org.jfree.util.Log imported, 1 call(s) discarded at runtime (lines 1697); move them to the class slf4j LOG` in `ProjectSectionValidator.java`; the same call is at HEAD:1705 and is not in the diff. Reported, not fixed (outside scope).
  - `git diff -U0` of `ValidateProjectSectionAction.java` and `ProjectSectionValidator.java`: only the two `case IMPACTS`
    blocks (3 + 16 lines), 2 imports, 2 fields, 2 constructor parameters (rejoined into 1 added line), 2 assignments
    and `validateProjectImpactCovid()`.
  - `ValidateProjectSectionAction.java:690` `validSection = ProjectSectionStatusEnum.value(sectionName) != null;` unchanged.
  - CR: `ValidateProjectSectionAction.java` 720/720 (CRLF kept; was 739/739); the other four 0 (LF kept).
- **Not Done / Assumptions (Implementer, verbatim):** *"`BaseAction.isYearToShowSectionCovid19()` had **no Javadoc**
  preceding it in the working tree … only the method body was removed."* · *"`java-hygiene-check.sh` returned exit 1
  due to a pre-existing `JFREE` finding … reported, not fixed, per the task's explicit instruction not to widen scope."*
  · *"Out-of-scope items (marlo-data `APConstants`/`Permission`/`ProjectSectionStatusEnum.IMPACTS`/
  `SectionStatusManager.getSectionStatusByProjectImpacts`, and `.properties`) were left untouched as directed."*
  Assumptions only, no owed item and no blocker → no continuation.
- **Evidence re-run (Leader-inline, non-author):** VERIFIED — same compile counts, Checkstyle `HEAD=13 working tree=13
  delta=0`, same single pre-existing JFREE line, same CR counts, `:690` unchanged.
- **Review intensity:** Reviewer owed — `Review: full`, `Consumers` not `none`, overrides (a)/(b) (shared validator,
  `BaseAction`, constants).
- **Reviewer (`akili-reviewer`, opus):** PASS — every removed line belongs to the impacts hook; no fall-through
  (`SAFEGUARDS`→`ACTIVITIES` and `LEVERAGES`→`default` keep their `break`); no import left unused or wrongly removed;
  constructor parameters and assignments removed in pairs; no surviving reader of the three constants in marlo-web.
  Named check (T01 amendment) unaffected.
- **runtime events:** none
- **spawns:** Implementer 70 calls, 141,979 tokens, ended complete; Reviewer 12 calls, 52,532 tokens, ended complete.

**ADVISORY (Reviewer, 4R — recorded, not gating)**

- *Risk:* while T02 is in and T03 is not, `IMPACTS` still exists: a stale `impacts` request passes `validSection`,
  matches no `execute()` case and falls to `default:` in completeness — no error, but "treated as unknown" holds only
  after DD-5. **Do not deploy T02 without T03** (they ship together on this branch).
- *Readability:* a commented-out impacts-style block above `case LEVERAGES` (`ValidateProjectSectionAction.java`
  ~510–524) is dead history; candidate for a later cleanup, correctly left alone here.
- *Reliability:* the jfree `Log.error` at `ProjectSectionValidator.java:1697` is discarded at runtime — pending item,
  outside this spec.

**Decisions made:** Implementer effort `medium`; skills `marlo-verify`, `error-handling-patterns` as listed.

**Final verification:** clean recompile green; Checkstyle delta 0; hygiene clean apart from the pre-existing line.

### Safety check after T01 + T02 (user request, 2026-10-01)

Leader-inline, read-only, on the staged tree; none of it is a task's gate, all of it is additional evidence.

| Check | Command (as run) | Result |
|---|---|---|
| Removed Java symbols named from FTL / JS / XML / `.properties` / JSP / HTML | `grep -rnI -E "isYearToShowSectionCovid19\|validateProjectImpactCovid\|ProjectImpactsValidator\|ProjectImpactsAction\|ImpactCovid19SummaryAction\|CRP_COVID_REQUIRED\|CRP_SHOW_SECTION_IMPACT_COVID19" marlo-web/src/main marlo-data/src/main --include='*.ftl' --include='*.js' --include='*.xml' --include='*.properties' --include='*.jsp' --include='*.html'` | 0 hits |
| Includes or links to the deleted views / report / routes | `grep -rnI -E "projectImpacts\.(ftl\|js)\|ImpactCovid19\.prpt\|impactCovid19Summary\.do\|/impacts\.do\|['\"]impacts['\"]" marlo-web/src/main` (same includes) | 3 hits, all `center/views/impactPathway/programImpacts.ftl` `name="impacts"` — an unrelated field name (P-5) |
| XML bean wiring of the deleted classes | `grep -rlI -E "ProjectImpacts(Action\|Validator)\|ImpactCovid19" marlo-web/src/main/resources marlo-web/src/main/webapp/WEB-INF --include='*.xml'` | 0 files |
| Specificity keys as string literals | `grep -rnI -E "crp_covid_required\|crp_show_section_impact_covid19" marlo-web/src/main marlo-data/src/main marlo-web/src/test \| grep -v /database/migrations/` | only `marlo-data/.../config/APConstants.java:118,120` — T03's scope |
| Tests referencing removed code | `grep -rlI -E "ProjectImpacts\|Covid19\|IMPACTS\|isYearToShowSectionCovid19" marlo-web/src/test marlo-data/src/test` | 0 files |
| String action names in Java | `grep -rnI -E "\"impacts\"\|'impacts'" marlo-web/src/main/java` | 0 hits |
| Routing XML well-formed | `xmllint --noout marlo-web/src/main/resources/struts-projects.xml` | well-formed |
| Unit test suite (regression) | `mvn -q -o -pl marlo-web -am test` | `Tests run: 296, Failures: 0, Errors: 0, Skipped: 0`, exit 0 |
| Files touched | `git diff --cached --name-only` + `git diff --name-only` | exactly T01 + T02 file lists, plus `tasks.md` |

Not covered here, by design: the runtime 404 on the retired routes and the FN-004 menu / completeness comparison —
both stay with T05 step 4 (local stack).

### CHG-RETIRE-COVID19-T03 — Remove the persistence layer in marlo-data

| Field | Value |
|---|---|
| **Final status** | PASS |
| **Date** | 2026-10-01 |
| **Attempts** | 1 |
| **Requirements covered** | FN-001 (DD-5: `IMPACTS` gone, so a stale `impacts` name is now unknown), NF-002, DA-004 (tables kept, only unmapped) |

Ran in parallel with T04 (disjoint files; T04 ran no Maven). The Leader's re-run waited until both workers had reported.

**Attempt 1**

- **Files changed (marlo-data/src/main):** deleted `java/org/cgiar/ccafs/marlo/data/model/ProjectImpacts.java`,
  `…/model/ProjectImpactsCategories.java`, `…/model/ReportProjectImpactsCovid19DTO.java`, `…/dao/ProjectImpactsDAO.java`,
  `…/dao/ProjectImpactsCategoriesDAO.java`, `…/dao/mysql/ProjectImpactsMySQLDAO.java`,
  `…/dao/mysql/ProjectImpactsCategoriesMySQLDAO.java`, `…/manager/ProjectImpactsManager.java`,
  `…/manager/ProjectImpactsCategoriesManager.java`, `…/manager/impl/ProjectImpactsManagerImpl.java`,
  `…/manager/impl/ProjectImpactsCategoriesManagerImpl.java`, `resources/xmls/ProjectImpacts.hbm.xml`,
  `resources/xmls/ProjectImpactsCategories.hbm.xml`; edited `java/org/cgiar/ccafs/marlo/config/APConstants.java`,
  `…/data/dao/SectionStatusDAO.java`, `…/data/dao/mysql/SectionStatusMySQLDAO.java`,
  `…/data/manager/SectionStatusManager.java`, `…/data/manager/impl/SectionStatusManagerImpl.java`,
  `…/data/model/ProjectSectionStatusEnum.java`, `…/data/model/SectionStatus.java`, `…/security/Permission.java`,
  `resources/hibernate.cfg.xml`, `resources/xmls/SectionStatuses.hbm.xml`. 23 files, 2 insertions, 1308 deletions.
- **Implementer verification:** `clean-compile.sh` → BUILD SUCCESS, 2405 marlo-data (= 2416 − 11 deleted `.java`),
  1043 marlo-web, 41 test · `grep -rnI -E "ProjectImpacts|project_impact|IMPACTS\(|PROJECT_COVID19|CRP_SHOW_SECTION_IMPACT_COVID19" marlo-data/src/main marlo-web/src/main | grep -v "/database/"` → 0 hits ·
  `checkstyle.sh --baseline` (8 files) → `HEAD=0 working tree=0 delta=0` · hygiene: touched files ok; only the
  pre-existing marlo-web JFREE line (see T02) · CR = line count on the 5 CRLF files, 0 on the LF files.
- **Not Done / Assumptions (Implementer, verbatim gist):** *"In `Permission.java` I deleted lines 169–172 … I inserted
  one CRLF blank line back in to match the file's established spacing convention"* · the pre-existing JFREE finding is
  reported, not fixed · no T04 file touched. Assumptions only → no continuation.
- **Evidence re-run (Leader-inline, non-author):** VERIFIED — same compile counts, sweep 0, Checkstyle delta 0, same CR
  counts; `xmllint --noout` on `hibernate.cfg.xml` and `SectionStatuses.hbm.xml` → well-formed.
- **Review intensity:** Reviewer owed — `Consumers` not `none`; override (b) (stored enum value, exported symbols).
- **Reviewer (`akili-reviewer`, opus):** PASS — diff equals the T03 file list; `projectPolicy` many-to-one still closed;
  enum separators intact and no `\bIMPACTS\b` left in any `.java`; `toString()` well-formed; only
  `getSectionStatusByProjectImpacts()` removed from the `SectionStatus` chain (no save/delete change); no mapping, HQL
  or entity still references the retired classes; no DDL. Named check (T01 amendment, last carry) unaffected.
- **runtime events:** none
- **spawns:** Implementer 71 calls, 124,647 tokens, ended complete; Reviewer 18 calls, 54,510 tokens, ended complete.

**ADVISORY (Reviewer, 4R — recorded, not gating)**

- *Risk:* no compile or grep proves Hibernate starts with the trimmed mappings while `section_statuses.project_impact_id`
  and its FK stay in the schema — **T05 step 4 (local stack) must not be skipped**.
- *Readability:* T03's sweep is case-sensitive; the six `breadCrumb.menu.projectImpacts` keys (lowercase `p`) are why
  it reads 0. Forward pointer → T05: those keys are on its i18n list; its closing sweep already includes
  `projectImpacts` in the pattern.

**Final verification:** clean recompile green; sweep 0; Checkstyle delta 0.

### CHG-RETIRE-COVID19-T04 — Migration that removes the configuration rows

| Field | Value |
|---|---|
| **Final status** | PASS (attempt 2) |
| **Date** | 2026-10-01 |
| **Attempts** | 2 — one rework round (within the Budget: the tripwire is a *second* rework round) |
| **Requirements covered** | DA-001, DA-002, DA-003, DA-004, MIG-001 (both scenarios, incl. the exact-match and prefix-decoy clauses) |
| **File** | `marlo-web/src/main/resources/database/migrations/V2_6_0_20261001_0821__RetireCovid19ImpactSection.sql` (name from `new-migration.sh`, real clock) |

Ran in parallel with T03 (disjoint; no Maven). Review mode: **parallel lens Reviewers** (data-loss surface) — lens A
reliability + risk, lens B readability + resilience, both with baseline conformance. Effort `xhigh` on both attempts:
the dial's bump to `max` is forbidden on a T2 tier, and the rework was a prescribed, narrow edit — recorded deviation.

**Attempt 1**

- **Files changed:** the migration file (new).
- **Implementer verification (`zz_mig_test`, copies from `aiccradb2` + the 5 P-8 FKs + decoys):** run 1 removed 9
  parameters, 53 custom_parameters, 2 permissions, 276 role grants, 0 center grants, 1 synthetic `impacts` status;
  decoys and `project_impacts` (9) / `project_impacts_categories` (5) kept; run 2 → 0 rows, exit 0; empty-schema run
  exit 0. Red: LIKE → decoy 1→0. Swap `role_permissions`/`permissions` did **not** fail — the real grant FKs are
  `ON DELETE CASCADE`.
- **Evidence re-run (Leader-inline, `zz_mig_verify`, grant FKs re-added as NO ACTION on purpose):** VERIFIED —
  BEFORE `params3keys=9 custom3keys=53 perms2=2 rolegrants=276 centergrants=0 impactsStatus=1 decoyParam=1 decoyPerm=1 decoyGrant=1 project_impacts=9 project_impacts_categories=5`;
  RED-A `ERROR 1451 (23000) at line 2: Cannot delete or update a parent row: a foreign key constraint fails (`zz_mig_verify`.`role_permissions`, CONSTRAINT `fk4` …)`;
  RED-B `decoyParam_after_LIKE 0`; RUN 1 all targets 0, decoys 1/1/1, data tables 9/5; RUN 2 identical. (A first
  attempt at this re-run was discarded: a stray `$=Q` in the Leader's shell script opened an interactive client and the
  decoy seeding failed on quoting — no evidence taken from it.)
- **Reviewer lens A (opus): FAIL** — *Discovered Issue:* `category = 2` added to the parameters / custom_parameters
  deletes and backup SELECTs can only under-delete in an unseen database; *Violated Rule:* `requirements.md` DA-001,
  §6 *First run* "IT MUST match rows by key …", `design.md` DD-3; *Remediation:* remove it from both deletes and both
  backup queries, rewrite the justification. Advisories: `A2-2578` header points at the wrong ticket; `1452`→`1451`;
  record the CASCADE / RESTRICT reality and correct the T04 falsifier; Flyway parsing supported by precedent only.
- **Reviewer lens B (opus): FAIL** — *Discovered Issue:* the header said "The seed migrations are not a rollback path
  … Restore from the backup query above instead"; *Violated Rule:* `tasks.md` §7 *Rollback Plan*; *Remediation:*
  rewrite as a refinement — backup first, §7 seed route as fallback, with the real reason it is less faithful.
  Judged `category = 2` conformant. Advisories A1–A7 (spec path instead of `A2-2578`, drop ids `562/563`, framing of
  the filter, de-duplicate, `1451`, "referenced by", "queries").
- **Leader adjudication:** both FAILs in scope. On the cross-lens split over `category = 2`, lens A wins — DA-001 says
  "MUST no longer exist", and a filter the spec does not name can only leave rows behind. Rework attempt 2 briefed
  with both reports verbatim.
- **runtime events:** none
- **spawns:** Implementer 36 calls, 143,847 tokens, ended complete; Reviewer A 19 calls, 59,636 tokens, ended
  complete; Reviewer B 15 calls, 59,165 tokens, ended complete.

**Attempt 2**

- **Files changed:** the same migration file — `category` filters removed (deletes and backup SELECTs); header now
  opens with the spec path `(shipped under A2-2578)`; ids `562/563` dropped; `1451`; rollback passage rewritten as
  backup-first, §7 seeds as fallback (`parameter_id = 200`, `contributionsLP6` grants as of 2020); CASCADE / RESTRICT
  note; repetitions trimmed.
- **Implementer verification (`zz_mig_test`, FKs with the real delete rules):** red (i) `parameters` before
  `custom_parameters` → `ERROR 1451 … custom_parameters_ibfk_1`; red (ii) LIKE → decoy 1→0; run 1 9 / 53 / 2 / 276 /
  0 / 1 removed, decoys 1/1/1 and 9/5 kept; run 2 → 0 rows; dropped; grep: no `category`, `LIKE`, `562`, `563` in an
  executable line.
- **Evidence re-run (Leader-inline, `zz_leader_verify`, real delete rules, own decoys):** VERIFIED —
  BEFORE `params3keys=9 custom3keys=53 perms2=2 rolegrants=276 impactsStatus=1 decoyParam=1 decoyPerm=1 decoyGrant=1 pi=9 pic=5 params_total=332 perms_total=246 rp_total=35549 cp_total=1719 ss_total=28487`;
  RED order `ERROR 1451 (23000) at line 2: Cannot delete or update a parent row: a foreign key constraint fails (`zz_leader_verify`.`custom_parameters`, CONSTRAINT `fk3` FOREIGN KEY (`parameter_id`) REFERENCES `parameters` (`id`))`;
  RED LIKE `decoyParam_after_LIKE 0`; AFTER RED = BEFORE;
  RUN 1 exit 0 `params3keys=0 custom3keys=0 perms2=0 rolegrants=0 impactsStatus=0 decoyParam=1 decoyPerm=1 decoyGrant=1 pi=9 pic=5 params_total=323 perms_total=244 rp_total=35273 cp_total=1666 ss_total=28486`;
  RUN 2 exit 0, identical. Dropped; `SHOW DATABASES LIKE 'zz%'` → none.
  (An earlier re-run was discarded: the Implementer had overwritten the Leader's `counts.sql` / `decoys.sql` in the
  shared session scratchpad. The valid re-run used a `scratchpad/leader/` subfolder.)
- **Reviewer lens A (opus): PASS** — `category` gone, matching by key only (DA-001); rollback agrees with §7; order,
  exact matching, DML-only, idempotency, DA-002/003/004, P-12, Hard rule 5 all hold; falsifier amendment accurate and
  changes no requirement.
- **Reviewer lens B (opus): PASS** — the attempt-1 FAIL is resolved; fallback claim checked against the seeds
  (`V2_6_0_20200504_0849` L29, `V2_6_0_20200509_1015` L10, `V2_6_0_20200518_1348` L10/L15); house layout followed.
- **runtime events:** none
- **spawns:** Implementer 35 calls, 124,045 tokens, ended complete; Reviewer A 16 calls, 48,051 tokens, ended
  complete; Reviewer B 9 calls, 46,611 tokens, ended complete.

**Additional Leader evidence after the PASS (lens A advisories 1–2, inline, `zz_leader_center`):** grant FKs re-added
**without** cascade and one synthetic center grant per retired permission plus one on the decoy.
BEFORE `centerTarget=2 centerDecoy=1 center_total=9 rolegrants=276 perms2=2`; RED (skip the center delete)
`ERROR 1451 (23000) at line 3: … center_role_permissions, CONSTRAINT fk5 …`; RUN 1 exit 0
`centerTarget=0 centerDecoy=1 center_total=7 rolegrants=0 perms2=0`; RUN 2 identical. So the grants-before-permissions
order and the `center_role_permissions` statement are now both exercised on real rows. Schema dropped.

**ADVISORY (recorded, not gating)**

- *Lens B:* the fallback sentence implies every 2020 seed copies `custom_parameters` from `parameter_id = 200`; only
  `V2_6_0_20200504_0849` and `V2_6_0_20200509_1015` do (`crp_covid_required` was seeded with a default value only).
  Low impact, fallback-only.
- *Lens B:* some repetition remains (DA-004 ×3, no-id ×2, idempotency ×2).
- *Lens B:* "shipped under A2-2578" is right only if that ticket carries this work — for the user to confirm.
- *Lens A:* lines 27–28 "On the real schema" would read better as "On aiccradb2 (measured 2026-10-01)".
- *Lens A:* Flyway parsing of the file is supported by precedent (`…20260924_1418` uses the same `DELETE … JOIN`
  form), not measured — covered by T05 step 4's local start-up.

**Decisions made**

- **Execute-time spec edit — `tasks.md` T04 *Falsifier* (2026-10-01).** Former text: *"swapping the `role_permissions`
  and `permissions` deletes makes run 1 fail on the FK."* False on the real schema (grant FKs `ON DELETE CASCADE`:
  `V2_0_0_20161103_1105__View_Permission10.sql:182`, `V2_0_0_20170912_1520__CenterPermissionsSL.sql:40`). Amended to
  the `custom_parameters` / `parameters` swap on the `ON DELETE RESTRICT` FK (`V2_0_0_20170517_1107__Parameters.sql:23`).
  No requirement's meaning changes (lens A confirmed).
- **Execute-time spec edit — `tasks.md` §7 *Rollback Plan* (2026-10-01).** Former text: *"Restoring them means
  re-running the 2020 migrations' inserts (…), with the descriptions later rewritten by `V2_6_0_20260925_1543` in a new
  migration."* Amended to backup-first, seeds as fallback — advised by both lenses so the spec and the file agree.
  Closure sweep over the spec folder for the superseded text: no other site.
- Cross-lens adjudication on `category = 2`: removed (above).
- **Pending item (outside this spec):** P-8 lists FKs without their delete rules; noted here, not edited
  (`design.md` is approved and this changes no decision).

**Final verification:** two independent double-runs green; four reds observed (LIKE; RESTRICT order; NO ACTION grant
order; NO ACTION center order); no throwaway schema left.
