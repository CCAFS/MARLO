# Role Assignment Email Catalog

Operational catalog of every email MARLO sends when a **role is assigned to (or removed from) a
user**. It answers two questions without reading Java: *what does the user actually receive*, and
*which key must be edited to change it*.

- **Verified:** 2026-09-10 against `staging`; updated 2026-10-01 for A2-2484 (role-email rewrite); §17 added
  2026-11-20 for CHG-PARTREQ-001 (the six partner-request emails, which are **not** role emails).
- **Source of truth for the text:** `marlo-web/src/main/resources/global.properties` (base) and
  `marlo-web/src/main/resources/custom/<crp>.properties` (per-Global-Unit override).
- **Source of truth for the assembly:** the `notifyRole*` methods listed per section.
- **Completeness:** every `getText` key requested inside the eight notification methods was extracted
  from source — **61 live keys plus 2 reachable only from a commented-out call**. All 61 exist in
  `custom/aicrra.properties`, so the AICCRA column is always a real override, never a fallback to
  `global.properties`. Each of those texts appears verbatim in this file, as written in the `.properties` file: an apostrophe shows as `''` and a non-ASCII character as its `\uXXXX` escape, the way they must be edited.

This file is a **catalog, not a template store.** Editing the text here changes nothing — edit the
`.properties` key. Editing the key without updating this file is drift.

---

## 1. How a role email is resolved

Every role email is assembled at runtime as a concatenation of i18n keys, never from a stored
template:

```
subject = getText("email.<role>.assigned.subject", args)
body    = getText("email.role.dear", {firstName})
        + getText("email.<role>.assigned", args)      // may embed a *.responsabilities key
        + getText("email.role.support")  |  getText("email.role.support.noCrpAdmins")
        + getText("email.role.getStarted")            // most, not all
        + getText("email.role.bye")
sent    = EmailLayout.wrap(action, body)              // getText("email.layout", {body, baseUrlCdn})
```

As of A2-2484, all 8 role/welcome senders read the `email.role.*` copies of `dear`/`bye`/`support`/
`support.noCrpAdmins`/`getStarted` instead of the shared `email.*` keys. The copies exist so the role
and welcome notifications can be restyled (or, for AICCRA, have their support/get-started lines moved
into the shell footer) without touching the ~12 other notifications — submissions, comments,
deliverables — that still call the original `email.*` keys unchanged.

### The shell (`email.layout`)

Every role email and every copy of the welcome email (§3) goes out through
`utils/EmailLayout.wrap()`, which puts the body inside the shell held by the key `email.layout`:
`{0}` = the body above, `{1}` = `getBaseUrlCdn()` (the base URL when there is no CDN), from which the
shell loads its images.

- **Base** (`global.properties`): `email.layout` IS the branded shell, the default for every Global Unit
  (moved verbatim from `aicrra` by `changes/email-layout-default`, md5
  `6b52db6bd9808a9361aa0d0e97c79985`). No custom file defines it: `aicrra`, `aiccra3` and `test` inherit it.
  It is the MARLO mockup in Figma: logo and two-line lockup, light-grey card holding `{0}`, rolling-hills
  band, footer note with the "Please do not reply to this email, as it is an automated notification."
  line and the `mailto:` support link. Table layout with inline CSS, 600 px wide. The shell hardcodes
  `MARLOSupport@cgiar.org` (DD-10); a unit that needs another address overrides `email.layout`.
- **Footer once** (DD-2): so the support address and the notice are not printed twice, the base
  `email.role.support` and `email.role.support.noCrpAdmins` are empty, `email.role.bye` has no
  do-not-reply notice, `email.role.getStarted` is a paragraph, and `login.otp.email.html` has no
  "Need help..." sentence.
- **Opt-out** (DD-9): a unit that must stay plain sets, in its custom file, `email.layout={0}` together
  with the restored pre-shell `email.role.support`, `email.role.support.noCrpAdmins`, `email.role.bye` and
  `login.otp.email.html`. The values are kept verbatim in the comment above `email.layout` in
  `global.properties`. For the OTP email of an opted-out unit the notice may be absent. No unit opts out
  today (Q1, 2026-11-11).
- **Images:** `marlo-web/src/main/webapp/global/images/email/` (`marlo-logo.png`, `email-band.png`).
  They are served without a session, as the login page's logo is.
- **Opt-in, on purpose.** `SendMailS` also sends the technical alerts and every other notification;
  only the call sites listed in §2 and §3 wrap their body (10 of the 30 mail senders wrap after
  `email-layout-default`: submit and unsubmit, `ProjectSubmissionAction` and `UnsubmitProjectAction`, now
  wrap too, with their own unit's texts (DD-6) and the `email.role.*` footer (DD-4)). A new role email must call
  `EmailLayout.wrap()` too, or it goes out without the shell.
  The exception reports to the support team (`UnhandledExceptionAction`, `ExceptionTranslator`) do not
  use this key: `logging/ExceptionEmail` draws the same frame in code, for every Global Unit, with a
  support-team footer instead of the registered-user one, and masks credential parameters.
- **Editing the shell:** it is split over continuation lines (`\` at the end of each line). It is a
  `MessageFormat` pattern, so it must hold no `'` and no `{`/`}` other than `{0}` and `{1}`.
  `EmailLayoutTest` loads the real files and fails on an unfilled placeholder.
- **Rollback:** per unit, add the opt-out block above to its custom file; for the whole change, revert the
  `email-layout-default` branch.
- **Plain-text part (every email, not only role ones):** `SendMailS.buildContent()` sends each message as
  `multipart/alternative` with a `text/plain` part before the `text/html` one; with an attachment, that
  alternative sits inside a `multipart/mixed` next to the file. The text comes from
  `utils/EmailPlainText.fromHtml()` (jsoup): paragraphs stay apart, `<li>` becomes a `- ` line, a web link
  keeps its URL, images are dropped. So the shell must keep its wording in live text, never in an image
  — the plain-text part and the images-blocked view both read only the text.

Key resolution order is `custom/<file>.properties` first, then `global.properties`
(`InternationalitazionFileInterceptor`). The custom file name comes from the session value
`crp_custom_file` (`APConstants.CRP_CUSTOM_FILE`), under `custom/` (`APConstants.PATH_CUSTOM_FILES`).

**AICCRA note.** Two AICCRA custom files exist — `custom/aicrra.properties` (AICCRA phase II/III
infrastructure file, referred to below simply as "AICCRA") and `custom/aiccra3.properties` (AICCRA
phase III). As of A2-2484 their role-assignment keys are **no longer byte-identical** — this was also
not true before A2-2484, the earlier "byte-identical" claim in this file was wrong. `aiccra3` carries
the same copy as `aicrra` but in its own vocabulary: "Component" where `aicrra` says "Theme",
"Technical Implementation Team(s)" where `aicrra` says "cluster(s)" (except the Cluster of Activities
keys `email.cluster.*`, which stay identical in both files), and "team" as the short second-mention
form. The AICCRA text quoted below is `aicrra`'s; read `aiccra3` by substituting that vocabulary.
Which file is loaded is a DB fact (`custom_parameters` row for key `crp_custom_file`), not a repo fact.

### Shared blocks

The role/welcome senders read the `email.role.*` keys (right-hand half of the table); the ~12 other
notifications (submissions, comments, deliverables) still read the original `email.*` keys, unchanged
by A2-2484, shown on the left for reference.

| Key | Base text (`global.properties`) | AICCRA override | `email.role.*` equivalent | AICCRA override |
|---|---|---|---|---|
| `dear` | `Dear {0}, <br><br>` | — (no override) | `email.role.dear` — base: `<p style="margin:0 0 24px 0;font-size:20px;line-height:28px;font-weight:bold;">Dear {0},</p>` | `<p style="margin:0 0 24px 0;font-size:20px;line-height:28px;font-weight:bold;">Dear {0},</p>` |
| `support` | `Should you have any questions, please do not hesitate to contact {0} or the technical MARLO team (MARLOSupport@cgiar.org)` — `{0}` = list of CRP Admins with emails | unchanged by A2-2484 | `email.role.support` — base: **empty** (the footer lives in the shell, `email.layout`); opt-out restores the text above | **empty** — the support line now lives in the shell footer |
| `support.noCrpAdmins` | `Should you have any questions, please do not hesitate to contact the technical MARLO team (MARLOSupport@cgiar.org)` | unchanged by A2-2484 | `email.role.support.noCrpAdmins` — base: **empty**; opt-out restores the text above | **empty** |
| `getStarted` | `<br><br>Please sign in to MARLO to get started.` (defect 1, fixed by A2-2484 — no longer hardcodes a URL) | unchanged by A2-2484 | `email.role.getStarted` — base: `<p style="margin:0 0 16px 0;">Please sign in to MARLO to get started.</p>` | **empty** — the sign-in call to action was dropped from the body |
| `bye` | `<br><br>Best regards, <br><br><b>MARLO Team</b> <br><br> <p>*** Please do not reply to this email as this is an automated notification ***</p>` | unchanged by A2-2484 | `email.role.bye` — base: `<p style="margin:8px 0 24px 0;">Thank you!</p><p style="margin:0;">Kind regards,<br>MARLO Team</p>` (no do-not-reply notice; the shell carries it); opt-out restores the `email.bye` text above | `<p style="margin:8px 0 24px 0;">Thank you!</p><p style="margin:0;">Kind regards,<br>MARLO Team</p>` |

`email-layout-default` (footer once, DD-2, DD-3): the `ciat` and `pabra` `email.role.*` overrides were
removed; `alliance` keeps only its own `email.role.getStarted`. Every unit now falls back to the
`global.properties` `email.role.*` texts above, except AICCRA (`aicrra`, `aiccra3`), which keeps its
own overrides.

> Defect 1 (base `email.getStarted` hardcoding the AICCRA URL) is fixed by A2-2484: the base text is
> now the neutral "Please sign in to MARLO to get started." with no URL.

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
- Base: `[MARLO] Welcome to MARLO - {0}` · AICCRA: `[AICCRA] Welcome to MARLO, {0}`
- `{0}` = user first name.

**Body** = `email.role.dear` + `email.newUser.part1` + `email.role.bye`.

`email.newUser.part1` arguments: `{0}` = `email.newUser.listRoles`, `{1}` = base URL, `{2}` = user
email, `{3}` = password (or `email.outlookPassword` = `(Your Outlook Password)` for CGIAR accounts).
As of A2-2484 the key **no longer takes a `{4}`** — the old `email.support.noCrpAdmins` argument was
dropped along with the paragraph that used it.

- Base `email.newUser.listRoles`: `program management units, flagship and cluster leaders, flagship managers, project leaders and project coordinators;`
- AICCRA: `Project Management Committee, Theme Leaders, Theme Managers, cluster Leaders and cluster Coordinators`

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
| **Subject** `email.guest.assigned.subject` | `[MARLO] You have been assigned a Guest role in {0}` | `[AICCRA] You now have guest access to MARLO for {0}` |
| **Body** `email.guest.assigned` | `You have been assigned a role in MARLO as guest in {0}. <br><br>` | `You now have <b>guest access</b> to MARLO for {0}. As a guest you can browse the information in the platform, without editing it.` |

`{0}` = Global Unit acronym. Closing: `email.role.support.noCrpAdmins` (empty in AICCRA) +
`email.role.getStarted` (empty in AICCRA) + `email.role.bye`. No responsibilities block.

---

## 5. PMU / Program Management Committee

`CrpAdminManagmentAction.notifyRoleProgramManagementAssigned` (:662).

**Subject** `email.programManagement.assigned.subject` — `{0}` = CRP acronym
- Base: `[MARLO] You have been assigned a role in the {0} PMU`
- AICCRA: `[AICCRA] You''re now part of the {0} Project Management Committee`

**Body** `email.programManagement.assigned` — `{0}` = CRP acronym, `{1}` = `email.programManagement.responsibilities`
- Base: `You have been assigned a role in the Program Management Unit (PMU) for {0} in MARLO.<br><br>People with a PMU role may be responsible for the following tasks:<ul>{1}</ul><br>`
- AICCRA: `<p style="margin:0 0 16px 0;">You''ve been added to the <b>Project Management Committee (PMC)</b> for {0} in MARLO.</p>{1}` —
  the intro paragraph is now self-closing (`</p>`) and the responsibilities list, including its own
  intro sentence, is entirely inside `{1}` (see below). This is why an empty `responsibilities` key can
  no longer leave the body without a list intro: the intro moved with the list.

**Responsibilities** `email.programManagement.responsibilities`
- Base: set up and edit flagship impact pathways; pre-set projects; enter detailed information about
  management projects at planning and reporting; set up and edit funding sources and assign them to
  projects.
- AICCRA: `<p style="margin:0 0 8px 0;">As a PMC member, you may be asked to:</p><ul style="margin:0 0 16px 0;padding:0 0 0 22px;"><li style="margin:0 0 6px 0;">Set up and edit the component Overall Performance Indicators.</li><li style="margin:0 0 6px 0;">Enter the detailed information for the management clusters the PMC leads, at planning and reporting.</li></ul>`

Closing: `email.role.support.noCrpAdmins` (empty in AICCRA) + `email.role.getStarted` (empty in AICCRA)
+ `email.role.bye`.

**Unassignment** (:716) — subject and body keys (`email.programManagement.unassigned.subject`,
`email.programManagement.unassigned`) were rewritten in AICCRA, not removed:
- Subject — base `[MARLO]/[AICCRA] Your role in the {0} {1} has been removed`; AICCRA now
  `[AICCRA] Your {1} role in {0} has been removed` (`{1}` = `programManagement.role.acronym` = `PMC`).
- Body — AICCRA now `<p style="margin:0 0 16px 0;">Your role in the <b>Project Management Committee (PMC)</b> for {1} in MARLO has been removed. The sections that came with it are no longer available to you.</p>`. This body ignores `{0}` (`programManagement.role` = `PMC`, unused) and names the
  committee in full instead.

Closing: `email.role.support.noCrpAdmins` + `email.role.bye`. **No `getStarted` on any unassignment
mail.**

---

## 6. Flagship Leader / Theme Leader

`CrpAdminManagmentAction.notifyRoleFlagshipAssigned` (:326). **CC** = acting user + every other active
Flagship Leader of that program + every active CRP Admin. **BCC** = `config.getEmailNotification()`.

**Subject** `email.flagship.assigned.subject` — `{0}` = flagship acronym, `{1}` = CRP acronym
- Base: `[MARLO] You have been assigned a role as Flagship {0} leader in {1}`
- AICCRA: `[AICCRA] You''re now the Theme {0} Leader in {1}`

**Body** `email.flagship.assigned` — `{0}` acronym, `{1}` program name, `{2}` CRP, `{3}` = `email.flagship.responsabilities`
- Base: `You have been assigned a role in MARLO as flagship leader for {0} {1} in {2}.<br><br>{3}<br>`
- AICCRA: `<p style="margin:0 0 16px 0;">You''ve been assigned as <b>Theme Leader</b> for {0} \u2014 {1} in {2}.</p>{3}`

**Responsibilities** `email.flagship.responsabilities` (note the misspelled key — it is the real key)
- Base: reviewing and submitting the flagship's impact pathway; reviewing and approving projects
  during planning and reporting; synthesizing the flagship's work.
- AICCRA: `<p style="margin:0 0 8px 0;">As a Theme Leader, you may be asked to:</p><ul style="margin:0 0 16px 0;padding:0 0 0 22px;"><li style="margin:0 0 6px 0;">Review and approve clusters during planning and reporting.</li><li style="margin:0 0 6px 0;">Synthesize the theme''s work at planning and reporting.</li><li style="margin:0 0 6px 0;">Steer the overall coherence and delivery of the AICCRA outcomes.</li><li style="margin:0 0 6px 0;">Provide technical backstopping to activity clusters, so their outputs and outcomes hold up.</li><li style="margin:0 0 6px 0;">Produce, or help produce, high-quality science products and processes.</li></ul>`

Closing: `email.role.support.noCrpAdmins` (empty in AICCRA) + `email.role.getStarted` (empty in AICCRA)
+ `email.role.bye`.

**Unassignment** (:587) — rewritten in AICCRA, not removed. Subject
`email.flagship.unassigned.subject` is now `[AICCRA] Your Theme {0} Leader role in {1} has been removed`. Body `email.flagship.unassigned` is now `<p style="margin:0 0 16px 0;">Your role as <b>{0}</b> for {1} \u2014 {2} in {3} has been removed.</p>` (`{0}` = `programManagement.flagship.role` =
`Theme Leader`).

---

## 7. Flagship Manager / Theme Manager

`CrpAdminManagmentAction.notifyRoleFlagshipManagerAssigned` (:407). Same CC set as the Leader **plus**
every active Cluster Leader under that program in the current phase.

**Subject** `email.flagshipmanager.assigned.subject` — args `{0}` flagship acronym, `{1}` CRP
- Base: `[MARLO] You have been assigned a role as Flagship {0} manager in {1}`
- AICCRA: `[AICCRA] You''re now the Theme {0} Manager (Science Officer) in {1}` — defect 4 (the AICCRA
  subject dropping `{0}`) is **fixed by A2-2484**: the flagship acronym now renders.

**Body** `email.flagshipmanager.assigned` — `{0}` acronym, `{1}` name, `{2}` CRP,
`{3}` = `email.flagshipmanager.responsabilities`, `{4}` = `email.flagshipmanager.note`
- Base: `You have been assigned a role in MARLO as flagship manager for Flagship {0} {1} in {2}.<br><br>{3}<br>{4}`
- AICCRA: `<p style="margin:0 0 16px 0;">You''ve been assigned as <b>Theme Manager (Science Officer)</b> for {0} \u2014 {1} in {2}.</p>{3}{4}`

`email.flagshipmanager.note` stays **empty** in base and in AICCRA after A2-2484 — unchanged, still a
per-program extension point (A4NH, CCAFS, FTA, PIM and others populate it).

**Responsibilities** — base: assisting the Flagship Leader with impact-pathway review, project review,
and synthesis. AICCRA: `<p style="margin:0 0 8px 0;">As a Theme Manager, you may be asked to:</p><ul style="margin:0 0 16px 0;padding:0 0 0 22px;"><li style="margin:0 0 6px 0;">Support the Theme Leader in reviewing the overall performance and delivery of the AICCRA outcomes.</li><li style="margin:0 0 6px 0;">Support the Theme Leader in reviewing clusters during planning and reporting.</li><li style="margin:0 0 6px 0;">Help the Theme Leader synthesize the theme''s work at planning and reporting.</li></ul>`

**Unassignment** (:500) — rewritten in AICCRA, not removed. Subject
`email.flagshipmanager.unassigned.subject` is now `[AICCRA] Your Theme {0} Manager role in {1} has been removed`. Body `email.flagshipmanager.unassigned` is now `<p style="margin:0 0 16px 0;">Your role as <b>Theme Manager</b> for {0} \u2014 {1} in {2} has been removed.</p>` — defect 5 (the AICCRA
unassign body saying "Component manager" while the assign body said "Theme manager") is **fixed by
A2-2484**: both now consistently say "Theme Manager".

---

## 8. Regional Program Leader

`CrpProgamRegionsAction.notifyRoleAssigned` with `role == rplRole` (:424). This action is **not**
gated by `validateEmailNotification` — it sends unconditionally.

**Subject** `email.region.assigned.subject` — `{0}` region acronym, `{1}` CRP
- Base: `[MARLO] You have been assigned a role as Regional Program Leader {0} in {1}`
- AICCRA: `[AICCRA] You''re now the Regional Program Leader for {0} in {1}`

**Body** `email.region.assigned` — `{0}` acronym, `{1}` region name, `{2}` CRP. The responsibilities
are inlined in this key rather than in a separate one.
- Base: set up and edit funding sources and assign them to projects; review detailed project
  information submitted by project leaders and liaise with the project leader for edits; submit
  syntheses about the regional work to the Flagship leaders at reporting stage.
- AICCRA: `<p style="margin:0 0 16px 0;">You''ve been assigned as <b>Regional Program Leader</b> for {0} \u2014 {1} in {2}.</p><p style="margin:0 0 8px 0;">As a Regional Program Leader, you may be asked to:</p><ul style="margin:0 0 16px 0;padding:0 0 0 22px;"><li style="margin:0 0 6px 0;">Set the strategic direction and coordinate action at regional level, together with Theme and Country Leaders.</li><li style="margin:0 0 6px 0;">Build on the region''s existing partnerships to secure the ones that matter for delivery.</li><li style="margin:0 0 6px 0;">Support country teams so AICCRA is implemented coherently across the region.</li><li style="margin:0 0 6px 0;">Review the cluster information submitted at planning and reporting, and work with cluster leaders on any edits.</li></ul>`

Closing: `email.role.support.noCrpAdmins` (empty in AICCRA) + `email.role.getStarted` (empty in AICCRA)
+ `email.role.bye`.

**Unassignment** (:529) — rewritten in AICCRA, not removed. Subject `email.region.unassigned.subject`
is now `[AICCRA] Your Regional Program Leader role for {1} in {0} has been removed`. Body
`email.region.unassigned` is now `<p style="margin:0 0 16px 0;">Your <b>{0}</b> role for <b>{1} ({2})</b> has been removed.</p>`.

---

## 9. Regional Program Manager

Same method as the Regional Program Leader, branch `role == rpmRole`.

**Subject** `email.regionmanager.assigned.subject` — `{0}` region acronym, `{1}` CRP
- Base: `[MARLO] {0} - new regional manager for {1}`
- AICCRA: `[AICCRA] You''re now the Regional Program Manager for {0} in {1}`

**Body** `email.regionmanager.assigned` — `{0}` acronym, `{1}` region name, `{2}` CRP
- Base: `You have been assigned a <b>{0}</b> role for <b>{1} ({2})</b> in the MARLO platform <http://marlodev.ciat.cgiar.org>` — **the base text points at the dev server and mislabels `{0}` as the role name when it actually receives the region acronym.** (Defect 2, still open for the base text.)
- AICCRA: `<p style="margin:0 0 16px 0;">You''ve been assigned as <b>Regional Program Manager</b> for {0} \u2014 {1} in {2}.</p><p style="margin:0 0 16px 0;">As a Regional Program Manager, you''ll review the cluster information submitted at planning and reporting, and work with cluster leaders on any edits.</p>` — defect 2 is **fixed for AICCRA**: the dev-server link is gone.

**Unassignment** — only `email.regionmanager.unassigned.subject` exists; the body still reuses
`email.region.unassigned`. AICCRA's subject is now `[AICCRA] Your Regional Program Manager role for {1} in {0} has been removed`.

---

## 10. Cluster of Activities Leader

`ClusterActivitiesAction.notifyRoleAssigned` (:358).

**Subject** `email.cluster.assigned.subject` — `{0}` cluster identifier, `{1}` CRP
- Base: `[MARLO] You have been assigned a role in MARLO as cluster leader for {0} in {1}`
- AICCRA: `[AICCRA] You''re now the leader of cluster {0} in {1}`

**Body** `email.cluster.assigned` — `{0}` identifier, `{1}` cluster description, `{2}` CRP,
`{3}` = `email.cluster.responsabilities`
- Base: `You have been assigned a role in MARLO as cluster leader for {0} {1} in {2}. <br><br>{3}<br><br>`
- AICCRA: `<p style="margin:0 0 16px 0;">You''ve been assigned as <b>leader of cluster {0}</b> \u2014 {1} in {2}.</p>{3}`

`email.cluster.responsabilities` is still **empty in base**, but defect 7 is **fixed by A2-2484** for
AICCRA, where it is now filled: `<p style="margin:0 0 8px 0;">As a cluster leader, you may be asked to:</p><ul style="margin:0 0 16px 0;padding:0 0 0 22px;"><li style="margin:0 0 6px 0;">Keep the cluster''s information up to date at planning and reporting.</li><li style="margin:0 0 6px 0;">Submit the cluster for review once it is complete.</li><li style="margin:0 0 6px 0;">Coordinate the partners and activities that sit under the cluster.</li></ul>` (this key is identical in `aicrra` and
`aiccra3` — the one `email.cluster.*` exception to the vocabulary split noted in §1).

Closing: `email.role.support` (empty in AICCRA; base still takes the CRP Admin list) +
`email.role.getStarted` (empty in AICCRA) + `email.role.bye`.

**Unassignment** (:437) — rewritten in AICCRA, not removed: `email.cluster.unassigned.subject` is now
`[AICCRA] Your role as leader of cluster {0} in {1} has been removed`, `email.cluster.unassigned` is
now `<p style="margin:0 0 16px 0;">Your role as <b>leader of cluster {0}</b> \u2014 {1} in {2} has been removed.</p>`.

---

## 11. Project Leader and Project Coordinator

`ProjectPartnerAction.notifyRoleAssigned` (:850). One method, two roles, selected by
`role.getId() == plRole.getId()`.

Role noun comes from `email.project.assigned.PL` = `leader` / `email.project.assigned.PC` =
`coordinator` (identical in base and AICCRA).

**Subject** `email.project.assigned.subject` — `{0}` role noun, `{1}` CRP, `{2}` project acronym
(or `C<projectId>` when the project has no acronym)
- Base: `[MARLO] You have been assigned a role as {0} of {1} project {2}`
- AICCRA: `[AICCRA] You''re now the {0} of the {1} cluster {2}`

**Body** `email.project.assigned` — `{0}` role noun, `{1}` CRP, `{2}` project title,
`{3}` = `project.getStandardIdentifier(Project.EMAIL_SUBJECT_IDENTIFIER)`
- Base: `You have been assigned a role in MARLO as {0} of the {1} project "{2}" ({3}).<br><br>`
- AICCRA: `<p style="margin:0 0 16px 0;">You''ve been assigned as <b>{0}</b> of the {1} cluster "{2}" ({3}).</p>` — `{1}` is now rendered (unlike the earlier catalog note for the pre-A2-2484 text).

**Responsibilities** — `email.project.leader.responsabilities` or
`email.project.coordinator.responsabilities`:

| | Leader | Coordinator |
|---|---|---|
| Base | Entering detailed information about projects during planning and reporting and submitting them for review. | Assisting the project leader with entering detailed information during planning and reporting. *Note: Only the project leader can submit the project information for review by the flagship leader(s).* |
| AICCRA | `<p style="margin:0 0 8px 0;">As a cluster leader, you''re responsible for:</p><ul style="margin:0 0 16px 0;padding:0 0 0 22px;"><li style="margin:0 0 6px 0;">Entering the cluster''s detailed information at planning and reporting, and submitting it for review.</li></ul>` | `<p style="margin:0 0 16px 0;">As a cluster coordinator, you support the cluster leader in entering the cluster''s detailed information at planning and reporting.</p><p style="margin:0 0 16px 0;">Note: only the cluster leader can submit the cluster for review.</p>` |

Closing: `email.role.support` (empty in AICCRA; base still takes the CRP Admin list) +
`email.role.getStarted` (empty in AICCRA) + `email.role.bye`.

**Unassignment** (:987) — subject `email.project.unAssigned.subject` (note the camel-cased
`unAssigned` here versus lowercase `unassigned` everywhere else), body
`email.project.leader.unAssigned` / `email.project.coordinator.unAssigned` — rewritten in AICCRA, not
removed:
- Subject AICCRA: `[AICCRA] Your role as {0} of the {1} cluster {2} has been removed`.
- Body AICCRA (leader and coordinator keys are identical text): `<p style="margin:0 0 16px 0;">Your role as <b>{0}</b> of the {1} cluster "<em>{2}</em>" ({3}) has been removed.</p>` — defect 6 (the
  earlier unbalanced bracket in this body) is **fixed by A2-2484**.

A second, near-duplicate implementation of this notification lives in
`utils/SendEmails.notifyRoleAssigned` (:362) for the non-Struts path. It hardcodes a personal address
into CC (`c.d.garcia@cgiar.org`) and honours the `CRP_EMAIL_CC_FL_FM_CL` specificity for the CC list.

---

## 12. Contact Point (PPA Partner)

`CrpPpaPartnersAction.notifyRoleContactPointAssigned` (:404).

**Subject** `email.contactpoint.assigned.subject` — `{0}` partner acronym (or name), `{1}` CRP
- Base: `[MARLO] You have been assigned a role as Contact Point for {0} in {1}`
- AICCRA: `[AICCRA] You''re now the Contact Point for {0} in {1}`

**Body** `email.contactpoint.assigned` — `{0}` partner, `{1}` CRP, `{2}` = `email.contactpoint.responsabilities`
- Base: `You have been assigned a role in MARLO as contact point for {0} in {1}.<br><br>Contact Points may be responsible for the following tasks:<br><ul>{2}</ul><br>`
- AICCRA: `<p style="margin:0 0 16px 0;">You''ve been assigned as <b>Contact Point</b> for {0} in {1}.</p>{2}`
  — like PMC (§5), the intro paragraph is self-closing and the responsibilities list, with its own
  intro sentence, lives entirely inside `{2}`.

**Responsibilities** `email.contactpoint.responsabilities` — base: set up and edit funding sources and
assign them to projects; assist the flagship leader in preparing syntheses about the center's work at
reporting stage. AICCRA: `<p style="margin:0 0 8px 0;">As a Contact Point, you may be asked to:</p><ul style="margin:0 0 16px 0;padding:0 0 0 22px;"><li style="margin:0 0 6px 0;">Set up and edit funding sources, and assign them to clusters.</li><li style="margin:0 0 6px 0;">Help the Theme Leader prepare the synthesis of your center''s work at reporting.</li></ul>`

Closing: `email.role.support` (empty in AICCRA; base still takes the CRP Admin list) +
`email.role.getStarted` (empty in AICCRA) + `email.role.bye`.

**Unassignment** (:471) — rewritten in AICCRA, not removed: `email.contactpoint.unassigned.subject` is
now `[AICCRA] Your Contact Point role for {1} in {0} has been removed`,
`email.contactpoint.unassigned` is now `<p style="margin:0 0 16px 0;">Your role as <b>Contact Point</b> for {1} in {0} has been removed.</p>`.

---

## 13. Site Integration Leader

`CrpSiteIntegrationAction.notifyRoleAssigned` (:301). **Only mails a real user when
`config.isProduction()`** — outside production TO/CC stay null.

**Subject** `email.siteIntegration.assigned.subject` — `{0}` CRP, `{1}` = `siteIntegration.leader.acronym`,
`{2}` country name
- Base: `[MARLO] {0} {1} for {2}`
- AICCRA: `[AICCRA] You''re now the Country Collaboration Leader for {2} in {0}` — the subject now
  ignores `{1}` (the `SL` acronym from `siteIntegration.leader.acronym`) and names the role in full
  instead.

**Body** `email.siteIntegration.assigned` — `{0}` = `siteIntegration.leader` label, `{1}` country name,
`{2}` ISO alpha-2
- Base: `You have been assigned a <b>{0}</b> role for the site <b>{1} ({2})</b> in the MARLO platform
  <http://marlodev.ciat.cgiar.org>` — still points at the dev server (defect 2, open for the base text).
- AICCRA: `<p style="margin:0 0 16px 0;">You''ve been assigned as <b>{0}</b> for <b>{1} ({2})</b> in MARLO.</p>` — defect 2 is **fixed for AICCRA**: the dev-server link is gone.

Closing: `email.role.support.noCrpAdmins` was fixed to be called here — defect 3 (`CrpSiteIntegrationAction`
calling `email.support` with no argument, so the base text rendered a literal `{0}`) is **fixed by
A2-2484**: the call site now uses `email.role.support.noCrpAdmins`, which takes no argument, so there
is no longer a mismatch. Then `email.role.getStarted` (empty in AICCRA) + `email.role.bye`.

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

Most are fixed by A2-2484 (2026-10-01). The remaining two (8, 9) are code bugs, out of scope for a
text-only ticket, and still open.

| # | Where | Problem | Status |
|---|---|---|---|
| 1 | `global.properties` `email.getStarted` | Hardcoded the AICCRA URL as the *base* text for every Global Unit that does not override it | **Fixed by A2-2484** (2026-10-01) — base text is now the neutral "Please sign in to MARLO to get started." |
| 2 | `email.regionmanager.assigned`, `email.siteIntegration.assigned` | Point users to `http://marlodev.ciat.cgiar.org` (dev server) | **Fixed for AICCRA** by A2-2484 — the AICCRA bodies no longer link to the dev server; the base text still does |
| 3 | `CrpSiteIntegrationAction:312` (now ~:315) | `email.support` invoked without the CRP Admin argument — base text renders a literal `{0}` | **Fixed by A2-2484** — the call site now uses `email.role.support.noCrpAdmins`, which takes no argument |
| 4 | AICCRA `email.flagshipmanager.assigned.subject` | Ignored `{0}`, so the theme acronym never reached the subject | **Fixed by A2-2484** |
| 5 | AICCRA `email.flagshipmanager.unassigned` | Said "Component manager" where the assign mail said "Theme manager" | **Fixed by A2-2484** |
| 6 | AICCRA `email.project.leader.unAssigned` | Unbalanced bracket: `of the cluster ["<em>{2}</em>" ({3}) has been removed` | **Fixed by A2-2484** |
| 7 | `email.cluster.responsabilities` | Empty in base and AICCRA — cluster leaders received no responsibilities block | **Fixed by A2-2484** for AICCRA (now filled, identical in `aicrra` and `aiccra3`); base still empty |
| 8 | `utils/SendEmails.notifyRoleAssigned:375` | Hardcoded personal address in CC | Still open — out of scope for A2-2484 |
| 9 | `CrpProgamRegionsAction:594` | Reads `regionalMapping.CrpProgram.leaders.acronym` into a local that is never used — the removal body takes the role description from the database | Still open — out of scope for A2-2484 |

---

## 16. Changing a role email

1. Identify the key from the section above.
2. Edit `custom/<crp>.properties` for a single-program change; edit `global.properties` **only** when
   the change must apply to every Global Unit that has not overridden the key.
3. Placeholder count and order are fixed by the Java call site — adding a `{n}` the action does not
   pass renders literally. Check the call site before adding one.
4. Non-ASCII characters go in as `\uXXXX` escapes, matching the existing file.
5. Update this catalog in the same commit.

---

## 17. Partner and country-office request emails

Six emails of the request flow (`changes/revive-local-partner-request-approval`; flow in
`docs/specs/domain/admin/agent-context.md` § *Partner requests*). They are **not** role emails, but they use the
same shell and the `email.role.dear` / `email.role.bye` blocks, so they are catalogued here.

**Assembly** — `utils/PartnerRequestEmails` (one static method per email, returns subject + body):

```
subject = unitTexts.getText(<subject key>, plain args)          // not HTML-escaped: it is a header
body    = getText("email.role.dear", {dearName}) + getText(<body key>, escaped args) + getText("email.role.bye")
sent    = EmailLayout.wrap(texts, assetsUrl, body)
```

- **Texts of the request's own unit.** `PartnerRequestEmails.unitTexts(unit)` reads that unit's `crp_custom_file`
  (`UnitTextProvider.forCustomFile`); the JSON actions that send these have no i18n interceptor. A unit with no
  custom file gets the `global.properties` texts. Of the six new `mail.*` bodies only `mail.newOffice` is overridden
  (`custom/aicrra.properties`, `custom/aiccra3.properties`: `Requested from cluster: C{4}`); the five other bodies are
  base text for every unit. The subjects are overridden per unit (`[AICCRA] …`, `[PABRA] …`, `[MARLO] …`).
- **Escaping.** Every body argument goes through `StringEscapeUtils.escapeHtml4`; a null becomes an empty string.
- **Switch.** Callers check `PartnerRequestEmails.isEnabled(unit)` first (parameter `crp_enable_email_notification`, `APConstants.CRP_EMAIL_NOTIFICATIONS`). The code
  is `getValue() == null || getValue().equalsIgnoreCase("true")` over the unit's rows of that parameter: no row, a
  null value or `true` sends; an **empty string or any other value does not**; a missing unit (or one with no
  parameter list) sends nothing. `SendMailS.send` applies the same rule again to any message that is not addressed only
  to the notification mailbox, so the answers 3 to 6 are also dropped there (logged at INFO); the alerts 1 and 2, which
  go only to the mailbox, are never dropped by it, so for them the caller's check is the only switch.
- **Decision before mail.** The four answers are sent only after the manager call returned (committed); a failure is
  logged and never reaches the superadmin.

| # | Email | Trigger (sender) | To / CC / BCC |
|---|---|---|---|
| 1 | New partner request | `PartnersSaveAction.sendNewPartnerAlert`, after `createPartnerRequest` | to `<notification mailbox>`, no CC, BCC `<notification mailbox>` |
| 2 | New country-office request | `RequestCountryOfficeAction.sendNewOfficeAlert`, after the rows are saved | same as 1 |
| 3 | Partner accepted | `PartnerRequestAction.sendPartnerAcceptedEmail`, only when the outcome is `DONE` | to requester (creator, or `externalUserMail` with the creator added to CC), CC approver, BCC `<notification mailbox>` |
| 4 | Partner rejected | `RejectPartnerRequestAction.sendRejectedNotificationEmail`, `DONE` and the posted `sendNotification` is true | as 3, CC is the rejecting user |
| 5 | Offices accepted | `PartnerRequestAction.sendOfficeEmails(…, true)`, for `DONE` and `SKIPPED_EXISTING` | one email per mailbox per Global Unit, with the full country list; CC approver, BCC `<notification mailbox>` |
| 6 | Offices rejected | same, `false`, only when `sendNotification` is true | as 5, with the justification |

Greeting name: for 1 and 2 `marloRequestInstitution.mail.supportName` ("MARLO support team"); for 3 to 6 the
requester's first name (or `externalUserName`). Emails 3 and 4: a request with no recipient mail logs a warning and sends nothing. Emails 5 and 6: a request with no
recipient is skipped silently (no log line); if no request of the unit has one, nothing is sent.

### Subjects (plain arguments)

| Email | Key | Args | Base | AICCRA (`aicrra`, `aiccra3`) |
|---|---|---|---|---|
| 1 | `marloRequestInstitution.email.subject` | `{0}` unit acronym, `{1}` partner name | `[MARLO - {0}] Partner verification - {1}` | `[AICCRA] Partner verification - {1}` |
| 2 | `marloRequestInstitution.office.email.subject` | `{0}` unit acronym, `{1}` country ISO codes, `{2}` institution | `[MARLO - {0}] Add office location ({1}) to {2}` | `[AICCRA] Add office location ({1}) to {2}` |
| 3 | `marloRequestInstitution.accept.email.subject` | `{0}` partner name | `[MARLO] Partner verification - Approved {0}` | `[AICCRA] Partner verification - Approved {0}` |
| 4 | `marloRequestInstitution.reject.email.subject` | `{0}` partner name | `[MARLO] Partner verification - Rejected {0}` | `[AICCRA] Partner verification - Rejected {0}` |
| 5 | `marloRequestInstitution.office.accept.email.subject` | `{0}` institution | `[MARLO] Country office(s) verification - Approved {0}` | `[AICCRA] Country office(s) verification - Approved {0}` |
| 6 | `marloRequestInstitution.office.reject.email.subject` | `{0}` institution | `[MARLO] Country office(s) verification - Rejected {0}` | `[AICCRA] Country office(s) verification - Rejected {0}` |

### Bodies (escaped arguments)

Markup is inline-styled paragraphs and lists; only the wording is shown. Copy the exact value from the key.

| Email | Key | Args | Wording (base) |
|---|---|---|---|
| 1 | `marloRequestInstitution.mail.newPartner` | `{0}` requester name, `{1}` requester email, `{2}` institution, `{3}` acronym (else `marloRequestInstitution.mail.notProvided` = "Not provided"), `{4}` type, `{5}` country, `{6}` website (else "Not provided"), `{7}` source, `{8}` review URL (`<base URL>/superadmin/marloInstitutions.do`) | "A new partner has been requested in MARLO and is waiting for your review." + list of the seven fields + "Review the request in Superadmin, Institutions: {8}" |
| 2 | `marloRequestInstitution.mail.newOffice` | `{0}` requester name, `{1}` requester email, `{2}` institution, `{3}` country names, `{4}` project id | "A new country office has been requested in MARLO and is waiting for your review." + list; last line `Requested from project: P{4}` (AICCRA: `Requested from cluster: C{4}`) |
| 3 | `marloRequestInstitution.mail.accepted` | `{0}` institution (composed name of the created institution) | "We have added the following institution to MARLO, as you requested:" + `{0}` bold |
| 4 | `marloRequestInstitution.mail.rejected` | `{0}` partner name, `{1}` justification | "We were unable to approve your request to add the institution {0}. The reason given was:" + quoted `{1}` |
| 5 | `marloRequestInstitution.mail.officeAccepted` | `{0}` institution, `{1}` countries | "We have added the following country office(s) of {0} to MARLO, as you requested:" + `{1}` |
| 6 | `marloRequestInstitution.mail.officeRejected` | `{0}` institution, `{1}` countries, `{2}` justification | "We were unable to approve your request to add the following country office(s) of {0}:" + `{1}` + "The reason given was:" + quoted `{2}` |

### Related keys that are not these emails

- `marloRequestInstitution.accept.email` and `.reject.email` (plain-text bodies) are still read, by the REST writer
  `InstitutionItem` together with the `marloRequestInstitution.clarisa.*` subject keys. `office.accept.email` and
  `office.reject.email` are loaded in `global.properties` and every custom file but no Java code reads them any more.
- `marloRequestInstitution.ui.outcome.*` are screen texts, not email texts (see the admin agent context).
- Changing one of these emails: edit the key above, keep `''` for an apostrophe and no `{` other than the numbered
  arguments, and update this section in the same commit.
