# Role Assignment Email Catalog

Operational catalog of every email MARLO sends when a **role is assigned to (or removed from) a
user**. It answers two questions without reading Java: *what does the user actually receive*, and
*which key must be edited to change it*.

- **Verified:** 2026-09-10 against `staging`.
- **Source of truth for the text:** `marlo-web/src/main/resources/global.properties` (base) and
  `marlo-web/src/main/resources/custom/<crp>.properties` (per-Global-Unit override).
- **Source of truth for the assembly:** the `notifyRole*` methods listed per section.
- **Completeness:** every `getText` key requested inside the eight notification methods was extracted
  from source — **61 live keys plus 2 reachable only from a commented-out call**. All 61 exist in
  `custom/aicrra.properties`, so the AICCRA column is always a real override, never a fallback to
  `global.properties`. Each of those texts appears verbatim in this file.

This file is a **catalog, not a template store.** Editing the text here changes nothing — edit the
`.properties` key. Editing the key without updating this file is drift.

---

## 1. How a role email is resolved

Every role email is assembled at runtime as a concatenation of i18n keys, never from a stored
template:

```
subject = getText("email.<role>.assigned.subject", args)
body    = getText("email.dear", {firstName})
        + getText("email.<role>.assigned", args)      // may embed a *.responsabilities key
        + getText("email.support")  |  getText("email.support.noCrpAdmins")
        + getText("email.getStarted")                 // most, not all
        + getText("email.bye")
```

Key resolution order is `custom/<file>.properties` first, then `global.properties`
(`InternationalitazionFileInterceptor`). The custom file name comes from the session value
`crp_custom_file` (`APConstants.CRP_CUSTOM_FILE`), under `custom/` (`APConstants.PATH_CUSTOM_FILES`).

**AICCRA note.** Two AICCRA custom files exist — `custom/aicrra.properties` and
`custom/aiccra3.properties`. Their **role-assignment keys are byte-identical**; they diverge only on
`email.change.deliverableStatus.*`. The AICCRA text quoted below therefore applies to both. Which
one is loaded is a DB fact (`custom_parameters` row for key `crp_custom_file`), not a repo fact.

### Shared blocks

| Key | Base text (`global.properties`) | AICCRA override |
|---|---|---|
| `email.dear` | `Dear {0}, <br><br>` | same |
| `email.support` | `Should you have any questions, please do not hesitate to contact {0} or the technical MARLO team (MARLOSupport@cgiar.org)` — `{0}` = list of CRP Admins with emails | `Should you have any questions, please do not hesitate to contact the MARLO technical team at MARLOSupport@cgiar.org` (`{0}` unused) |
| `email.support.noCrpAdmins` | `Should you have any questions, please do not hesitate to contact the technical MARLO team (MARLOSupport@cgiar.org)` | `Should you have any questions, please do not hesitate to contact the MARLO technical team at MARLOSupport@cgiar.org` |
| `email.getStarted` | `<br><br>Please go to https://aiccra.marlo.cgiar.org to get started.` | `<br><br>To begin, please visit https://aiccra.marlo.cgiar.org .` |
| `email.bye` | `<br><br>Best regards, <br><br><b>MARLO Team</b> <br><br> <p>*** Please do not reply to this email as this is an automated notification ***</p>` | same |

> The base `email.getStarted` already hardcodes the **AICCRA** URL in `global.properties`. That is a
> known wart, not a typo in this catalog: any non-AICCRA Global Unit that does not override the key
> sends users to the AICCRA instance.

---

## 2. Index of role emails

| # | Role | Assign | Unassign | Built in |
|---|---|---|---|---|
| 1 | Guest | ✅ | — | `CrpUsersAction.notifyRoleAssigned` (:450) |
| 2 | PMU / PMC (Program Management) | ✅ | ✅ | `CrpAdminManagmentAction` (:662 / :716) |
| 3 | Flagship Leader (AICCRA: Theme Leader) | ✅ | ✅ | `CrpAdminManagmentAction` (:326 / :587) |
| 4 | Flagship Manager (AICCRA: Theme Manager) | ✅ | ✅ | `CrpAdminManagmentAction` (:407 / :500) |
| 5 | Regional Program Leader | ✅ | ✅ | `CrpProgamRegionsAction` (:424 / :529) |
| 6 | Regional Program Manager | ✅ | ✅ (subject key only) | `CrpProgamRegionsAction` (:424 / :529) |
| 7 | Cluster of Activities Leader | ✅ | ✅ | `ClusterActivitiesAction` (:358 / :437) |
| 8 | Project Leader (AICCRA: Cluster Leader) | ✅ | ✅ | `ProjectPartnerAction` (:850 / :987) |
| 9 | Project Coordinator (AICCRA: Cluster Coordinator) | ✅ | ✅ | `ProjectPartnerAction` (:850 / :987) |
| 10 | Contact Point (PPA partner) | ✅ | ✅ | `CrpPpaPartnersAction` (:404 / :471) |
| 11 | Site Integration Leader | ✅ | — | `CrpSiteIntegrationAction` (:301) |
| — | New user account (not a role) | ✅ | — | 8 copies — see §3 |

All paths are under `marlo-web/src/main/java/org/cgiar/ccafs/marlo/`.

---

## 3. Account creation email (precedes every role email)

**Eight copies of this method exist.** Seven are named `notifyNewUserCreated` — six in actions
(`CrpAdminManagmentAction` :230, `CrpProgamRegionsAction` :337, `CrpPpaPartnersAction` :309,
`CrpSiteIntegrationAction` :212, `ClusterActivitiesAction` :272, `ProjectPartnerAction` :754) and one
in `utils/SendEmails.java` :270 for the non-Struts path. The eighth is `CrpUsersAction.sendMailNewUser`
(:821), which sends the same mail under a different name and is the only copy that attaches the PDF.

**Subject** — `email.newUser.subject`
- Base: `[MARLO] Welcome to MARLO - {0}` · AICCRA: `[AICCRA] Welcome to MARLO - {0}`
- `{0}` = user first name.

**Body** = `email.dear` + `email.newUser.part1` + `email.bye`.

`email.newUser.part1` arguments: `{0}` = `email.newUser.listRoles`, `{1}` = base URL, `{2}` = user
email, `{3}` = password (or `email.outlookPassword` = `(Your Outlook Password)` for CGIAR accounts),
`{4}` = `email.support.noCrpAdmins`.

- Base `email.newUser.listRoles`: `program management units, flagship and cluster leaders, flagship managers, project leaders and project coordinators;`
- AICCRA: `Project Management Committee, Theme leaders, Theme Managers, cluster Leaders, and cluster Coordinators`

The base body explicitly announces the role mail that follows: *"You will receive a second email with
information about the role you are assigned in the system."*

`email.newUser.part2` exists and is fully translated per program but **no action calls it** — the call
site in `CrpUsersAction` (:854-858) is commented out. Treat it as dead text.

---

## 4. Guest

`CrpUsersAction.notifyRoleAssigned(User)` — Admin → Users. Sent only when
`validateEmailNotification(globalUnit)` passes. **TO** the assigned user, **CC** the acting user,
**BCC** `config.getEmailNotification()`.

| | Base | AICCRA |
|---|---|---|
| **Subject** `email.guest.assigned.subject` | `[MARLO] You have been assigned a Guest role in {0}` | `[AICCRA] You have been assigned a Guest role in MARLO for {0}` |
| **Body** `email.guest.assigned` | `You have been assigned a role in MARLO as guest in {0}. <br><br>` | `You have been assigned a role as guest in MARLO for {0}. <br><br>` |

`{0}` = Global Unit acronym. Closing: `email.support.noCrpAdmins` + `email.getStarted` + `email.bye`.
No responsibilities block.

---

## 5. PMU / Program Management Committee

`CrpAdminManagmentAction.notifyRoleProgramManagementAssigned` (:662).

**Subject** `email.programManagement.assigned.subject` — `{0}` = CRP acronym
- Base: `[MARLO] You have been assigned a role in the {0} PMU`
- AICCRA: `[AICCRA] You have been assigned a role in the {0} Project Management Committee`

**Body** `email.programManagement.assigned` — `{0}` = CRP acronym, `{1}` = `email.programManagement.responsibilities`
- Base: `You have been assigned a role in the Program Management Unit (PMU) for {0} in MARLO.<br><br>People with a PMU role may be responsible for the following tasks:<ul>{1}</ul><br>`
- AICCRA: `You have been assigned a role in the {0} Project Management Committee (PMC) for MARLO.<br><br>People with a PMC role may be responsible for the following tasks:<ul>{1}</ul><br>`

**Responsibilities** `email.programManagement.responsibilities`
- Base: set up and edit flagship impact pathways; pre-set projects; enter detailed information about
  management projects at planning and reporting; set up and edit funding sources and assign them to
  projects.
- AICCRA: `<li>Set up and edit component Overall Performance Indicators.</li><li>Enter detailed information about management clusters (led by the PMC) at the planning and reporting stages.</li>`

Closing: `email.support.noCrpAdmins` + `email.getStarted` + `email.bye`.

**Unassignment** (:716) — subject `email.programManagement.unassigned.subject`
(`[MARLO]/[AICCRA] Your role in the {0} {1} has been removed`), body
`email.programManagement.unassigned` (base `Your role in MARLO "{0} for {1}" has been removed.<br><br>`;
AICCRA `Your role in MARLO as a "{0} for {1}" has been removed.<br><br>`) +
`email.support.noCrpAdmins` + `email.bye`. **No `getStarted` on any unassignment mail.**

---

## 6. Flagship Leader / Theme Leader

`CrpAdminManagmentAction.notifyRoleFlagshipAssigned` (:326). **CC** = acting user + every other active
Flagship Leader of that program + every active CRP Admin. **BCC** = `config.getEmailNotification()`.

**Subject** `email.flagship.assigned.subject` — `{0}` = flagship acronym, `{1}` = CRP acronym
- Base: `[MARLO] You have been assigned a role as Flagship {0} leader in {1}`
- AICCRA: `[AICCRA] You have been assigned a role as Theme {0} leader in MARLO for {1}`

**Body** `email.flagship.assigned` — `{0}` acronym, `{1}` program name, `{2}` CRP, `{3}` = `email.flagship.responsabilities`
- Base: `You have been assigned a role in MARLO as flagship leader for {0} {1} in {2}.<br><br>{3}<br>`
- AICCRA: `You have been assigned a role in MARLO as Theme leader for ["{0} {1}"] in {2}.<br><br>{3}<br>`

**Responsibilities** `email.flagship.responsabilities` (note the misspelled key — it is the real key)
- Base: reviewing and submitting the flagship's impact pathway; reviewing and approving projects
  during planning and reporting; synthesizing the flagship's work.
- AICCRA: reviewing and approving clusters; synthesizing the cluster's work; overall coherence and
  delivery of the AICCRA outcomes; technical backstopping of activity clusters; producing and/or
  facilitating high-quality science products and processes.

Closing: `email.support.noCrpAdmins` + `email.getStarted` + `email.bye`.

**Unassignment** (:587) — `email.flagship.unassigned.subject` /
`email.flagship.unassigned` (`Your role in MARLO as {0} for Flagship {1} {2} in {3} has been removed.`).

---

## 7. Flagship Manager / Theme Manager

`CrpAdminManagmentAction.notifyRoleFlagshipManagerAssigned` (:407). Same CC set as the Leader **plus**
every active Cluster Leader under that program in the current phase.

**Subject** `email.flagshipmanager.assigned.subject` — args `{0}` flagship acronym, `{1}` CRP
- Base: `[MARLO] You have been assigned a role as Flagship {0} manager in {1}`
- AICCRA: `[AICCRA] You have been assigned a role as Theme manager (Science Officer) in {1}` —
  **`{0}` is passed but never rendered**, so the flagship acronym is missing from the AICCRA subject.

**Body** `email.flagshipmanager.assigned` — `{0}` acronym, `{1}` name, `{2}` CRP,
`{3}` = `email.flagshipmanager.responsabilities`, `{4}` = `email.flagshipmanager.note`
- Base: `You have been assigned a role in MARLO as flagship manager for Flagship {0} {1} in {2}.<br><br>{3}<br>{4}`
- AICCRA: `You have been assigned to the role of Theme manager for Component [{0} {1}] in {2}.<br><br>{3}<br>{4}`

`email.flagshipmanager.note` is **empty** in base and in AICCRA. It exists as a per-program extension
point (A4NH, CCAFS, FTA, PIM and others populate it).

**Responsibilities** — base: assisting the Flagship Leader with impact-pathway review, project review,
and synthesis. AICCRA: assisting the Theme Leader with review of overall performance and delivery of
AICCRA outcomes, review of clusters, and synthesis of the indicator's work.

**Unassignment** (:500) — `email.flagshipmanager.unassigned.subject` /
`email.flagshipmanager.unassigned`. The AICCRA body still says **"Component manager"** while the
assign body says "Theme manager" — inconsistent wording, verified in `custom/aiccra3.properties`.

---

## 8. Regional Program Leader

`CrpProgamRegionsAction.notifyRoleAssigned` with `role == rplRole` (:424). This action is **not**
gated by `validateEmailNotification` — it sends unconditionally.

**Subject** `email.region.assigned.subject` — `{0}` region acronym, `{1}` CRP
- Base: `[MARLO] You have been assigned a role as Regional Program Leader {0} in {1}`
- AICCRA: `[AICCRA] You have been assigned a role as Regional Program Leader {0} in {1}`

**Body** `email.region.assigned` — `{0}` acronym, `{1}` region name, `{2}` CRP. The responsibilities
are inlined in this key rather than in a separate one.
- Base: set up and edit funding sources and assign them to projects; review detailed project
  information submitted by project leaders and liaise with the project leader for edits; submit
  syntheses about the regional work to the Flagship leaders at reporting stage.
- AICCRA: provide strategic directions and coordinate actions at the regional level with Thematic and
  Country Leaders; build on existing strategic partnerships; support country teams implementing
  AICCRA; review detailed cluster information at planning and reporting and liaise with the cluster
  leader for edits.

Closing: `email.support.noCrpAdmins` + `email.getStarted` + `email.bye`.

**Unassignment** (:529) — `email.region.unassigned.subject` / `email.region.unassigned`
(`This email is to kindly inform you that your <b>{0}</b> role for <b>{1} ({2})</b> has been
unassigned.` — AICCRA: `... has been removed.`).

---

## 9. Regional Program Manager

Same method as the Regional Program Leader, branch `role == rpmRole`.

**Subject** `email.regionmanager.assigned.subject` — `{0}` region acronym, `{1}` CRP
- Base: `[MARLO] {0} - new regional manager for {1}`
- AICCRA: `[AICCRA] You have been assigned a role as Regional Program Manager {0} in {1}`

**Body** `email.regionmanager.assigned` — `{0}` acronym, `{1}` region name, `{2}` CRP
- Base: `You have been assigned a <b>{0}</b> role for <b>{1} ({2})</b> in the MARLO platform <http://marlodev.ciat.cgiar.org>` — **the base text points at the dev server and mislabels `{0}` as the role name when it actually receives the region acronym.**
- AICCRA: `You have been assigned the role of Regional Program Manager for [{0} - {1}] in {2} <br><br>Regional program managers will be responsible for reviewing detailed cluster information submitted at the planning and reporting stages. If necessary, you will collaborate with the cluster leader to make any required edits.<br><br>`

**Unassignment** — only `email.regionmanager.unassigned.subject` exists; the body reuses
`email.region.unassigned`.

---

## 10. Cluster of Activities Leader

`ClusterActivitiesAction.notifyRoleAssigned` (:358).

**Subject** `email.cluster.assigned.subject` — `{0}` cluster identifier, `{1}` CRP
- Base: `[MARLO] You have been assigned a role in MARLO as cluster leader for {0} in {1}`
- AICCRA: `[AICCRA] You have been assigned a role in AICCRA as cluster leader for {0} in {1}`

**Body** `email.cluster.assigned` — `{0}` identifier, `{1}` cluster description, `{2}` CRP,
`{3}` = `email.cluster.responsabilities`
- Base: `You have been assigned a role in MARLO as cluster leader for {0} {1} in {2}. <br><br>{3}<br><br>`
- AICCRA: same sentence with `AICCRA` in place of the first `MARLO`.

`email.cluster.responsabilities` is **empty** in base and in AICCRA — FTA, MAIZE, PIM and WHEAT fill
it. So this email currently carries no responsibilities block for AICCRA.

Closing: `email.support` (with the CRP Admin list) + `email.getStarted` + `email.bye`.

**Unassignment** (:437) — `email.cluster.unassigned.subject` / `email.cluster.unassigned`.

---

## 11. Project Leader and Project Coordinator

`ProjectPartnerAction.notifyRoleAssigned` (:850). One method, two roles, selected by
`role.getId() == plRole.getId()`.

Role noun comes from `email.project.assigned.PL` = `leader` / `email.project.assigned.PC` =
`coordinator` (identical in base and AICCRA).

**Subject** `email.project.assigned.subject` — `{0}` role noun, `{1}` CRP, `{2}` project acronym
(or `C<projectId>` when the project has no acronym)
- Base: `[MARLO] You have been assigned a role as {0} of {1} project {2}`
- AICCRA: `[AICCRA] You have been assigned a role as {0} of the {1} cluster [{2}]`

**Body** `email.project.assigned` — `{0}` role noun, `{1}` CRP, `{2}` project title,
`{3}` = `project.getStandardIdentifier(Project.EMAIL_SUBJECT_IDENTIFIER)`
- Base: `You have been assigned a role in MARLO as {0} of the {1} project "{2}" ({3}).<br><br>`
- AICCRA: `You have been assigned a role in AICCRA as {0} of the cluster "{2}" ({3}).<br><br>` —
  `{1}` is passed but not rendered.

**Responsibilities** — `email.project.leader.responsabilities` or
`email.project.coordinator.responsabilities`:

| | Leader | Coordinator |
|---|---|---|
| Base | Entering detailed information about projects during planning and reporting and submitting them for review. | Assisting the project leader with entering detailed information during planning and reporting. *Note: Only the project leader can submit the project information for review by the flagship leader(s).* |
| AICCRA | Entering detailed information about clusters during planning and reporting and submitting them for review. | Supporting the cluster leader in entering detailed information related to their respective clusters during planning and reporting. *Note: Only the cluster leader can submit the cluster information for review.* |

Closing: `email.support` (CRP Admin list) + `email.getStarted` + `email.bye`.

**Unassignment** (:987) — subject `email.project.unAssigned.subject`, body
`email.project.leader.unAssigned` / `email.project.coordinator.unAssigned`. Note the camel-cased
`unAssigned` here versus lowercase `unassigned` everywhere else.

A second, near-duplicate implementation of this notification lives in
`utils/SendEmails.notifyRoleAssigned` (:362) for the non-Struts path. It hardcodes a personal address
into CC (`c.d.garcia@cgiar.org`) and honours the `CRP_EMAIL_CC_FL_FM_CL` specificity for the CC list.

---

## 12. Contact Point (PPA Partner)

`CrpPpaPartnersAction.notifyRoleContactPointAssigned` (:404).

**Subject** `email.contactpoint.assigned.subject` — `{0}` partner acronym (or name), `{1}` CRP
- Base / AICCRA: `[MARLO] / [AICCRA] You have been assigned a role as Contact Point for {0} in {1}`

**Body** `email.contactpoint.assigned` — `{0}` partner, `{1}` CRP, `{2}` = `email.contactpoint.responsabilities`
- Base: `You have been assigned a role in MARLO as contact point for {0} in {1}.<br><br>Contact Points may be responsible for the following tasks:<br><ul>{2}</ul><br>`
- AICCRA: same with `AICCRA` in place of `MARLO`.

**Responsibilities** — base: set up and edit funding sources and assign them to projects; assist the
flagship leader in preparing syntheses about the center's work at reporting stage. AICCRA: same, with
"clusters" replacing "projects".

Closing: `email.support` (CRP Admin list) + `email.getStarted` + `email.bye`.

**Unassignment** (:471) — `email.contactpoint.unassigned.subject` / `email.contactpoint.unassigned`.

---

## 13. Site Integration Leader

`CrpSiteIntegrationAction.notifyRoleAssigned` (:301). **Only mails a real user when
`config.isProduction()`** — outside production TO/CC stay null.

**Subject** `email.siteIntegration.assigned.subject` — `{0}` CRP, `{1}` = `siteIntegration.leader.acronym`,
`{2}` country name → base and AICCRA both: `[MARLO] / [AICCRA] {0} {1} for {2}`

**Body** `email.siteIntegration.assigned` — `{0}` = `siteIntegration.leader` label, `{1}` country name,
`{2}` ISO alpha-2 → `You have been assigned a <b>{0}</b> role for the site <b>{1} ({2})</b> in the
MARLO platform <http://marlodev.ciat.cgiar.org>` — identical in base and AICCRA, **and it also points
at the dev server.**

Closing: `email.support` **called with no argument**, so `{0}` renders literally as `{0}` in the base
text. AICCRA's `email.support` has no placeholder, so AICCRA is unaffected. Then `email.getStarted` +
`email.bye`.

---

## 14. Injected labels

Some placeholders are filled by a **second property**, not by a database value. These are the texts
that land in those slots; changing one changes every message that uses it.

| Key | Fills | Base | AICCRA |
|---|---|---|---|
| `programManagement.role` | `email.programManagement.unassigned` `{0}` | `Program Management Unit` | `PMC` |
| `programManagement.role.acronym` | `email.programManagement.unassigned.subject` `{1}` | `PMU` | `PMC` |
| `programManagement.flagship.role` | `email.flagship.unassigned` `{0}` | `Flagship Leader` | `Theme Leader` |
| `siteIntegration.leader` | `email.siteIntegration.assigned` `{0}` | `CGIAR Country Collaboration Leader` | `Country Collaboration Leader` |
| `siteIntegration.leader.acronym` | `email.siteIntegration.assigned.subject` `{1}` | `SL` | `SL` |
| `email.project.assigned.PL` | `email.project.assigned` / `.subject` / `email.project.leader.unAssigned` `{0}` | `leader` | `leader` |
| `email.project.assigned.PC` | the same three keys, for a coordinator `{0}` | `coordinator` | `coordinator` |
| `email.outlookPassword` | `email.newUser.part1` `{3}`, for CGIAR accounts | `(Your Outlook Password)` | `(Your Outlook Password)` |
| `email.flagshipmanager.note` | `email.flagshipmanager.assigned` `{4}` | *(empty)* | *(empty)* |
| `regionalMapping.CrpProgram.leaders.acronym` | nothing — read into a local at `CrpProgamRegionsAction:594`, never used | `SL` | `SL` |
| `global.sClusterOfActivities` | `email.newUser.part2` `{0}` — dead, that call is commented out | `Cluster of activity` | `Cluster of activity` |

### Loaded but never sent

`email.newUser.part2` — its only call site (`CrpUsersAction.java:854-858`) is commented out.

- **Base:** `Welcome to <b>MARLO</b> - <b>M</b>anaging <b>A</b>gricultural <b>R</b>esearch for <b>L</b>earning and <b>O</b>utcomes. MARLO is an online platform designed to assist CRPs, Platforms and CIAT in their strategic, results-based program planning and reporting of research. MARLO covers the entire project cycle from planning to reporting. Features are built into the system to support learning and synthesis at the level of flagship, {0}, and cross-cutting area. Outcome-focused programmatic reports can be generated from the information entered into MARLO.<br><br> We have created an account for you. Please use the following credentials to access MARLO: <br><br><b>Link</b>: {1} <br><b>Select {2} CRP</b> <br><b>Email</b>: {3}<br><b>Password</b>: {4}<br><br>In addition, kindly find attached a short user manual that describes MARLO’s main functionalities, user roles and responsibilities, and a brief explanation of the workflow.<br><br>{5}`
- **AICCRA:** identical except *project cycle* becomes *cluster cycle* and *flagship* becomes *component*.

---

## 15. Known defects surfaced while cataloguing

Recorded, not fixed. Each is a text/config bug, not a code bug.

| # | Where | Problem |
|---|---|---|
| 1 | `global.properties` `email.getStarted` | Hardcodes the AICCRA URL as the *base* text for every Global Unit that does not override it |
| 2 | `email.regionmanager.assigned`, `email.siteIntegration.assigned` | Point users to `http://marlodev.ciat.cgiar.org` (dev server) |
| 3 | `CrpSiteIntegrationAction:312` | `email.support` invoked without the CRP Admin argument — base text renders a literal `{0}` |
| 4 | AICCRA `email.flagshipmanager.assigned.subject` | Ignores `{0}`, so the theme acronym never reaches the subject |
| 5 | AICCRA `email.flagshipmanager.unassigned` | Says "Component manager" where the assign mail says "Theme manager" |
| 6 | AICCRA `email.project.leader.unAssigned` | Unbalanced bracket: `of the cluster ["<em>{2}</em>" ({3}) has been removed` |
| 7 | `email.cluster.responsabilities` | Empty in base and AICCRA — cluster leaders receive no responsibilities block |
| 8 | `utils/SendEmails.notifyRoleAssigned:375` | Hardcoded personal address in CC |
| 9 | `CrpProgamRegionsAction:594` | Reads `regionalMapping.CrpProgram.leaders.acronym` into a local that is never used — the removal body takes the role description from the database |

---

## 16. Changing a role email

1. Identify the key from the section above.
2. Edit `custom/<crp>.properties` for a single-program change; edit `global.properties` **only** when
   the change must apply to every Global Unit that has not overridden the key.
3. Placeholder count and order are fixed by the Java call site — adding a `{n}` the action does not
   pass renders literally. Check the call site before adding one.
4. Non-ASCII characters go in as `\uXXXX` escapes, matching the existing file.
5. Update this catalog in the same commit.
