# Deployment Checklist

## Goal
Give a single, ordered checklist for promoting MARLO to test / staging / production, with the
i18n restart requirement stated explicitly so a deploy cannot silently ship raw resource-bundle
key names to users.

## Rule 0 — Any change to i18n properties requires an application restart

> **Restart the application after any change to `global.properties` or `custom/*.properties`
> (i18n keys). A browser refresh is not sufficient.**

If a deploy ships new or renamed i18n keys and the application is not restarted, Struts resolves
those keys against a bundle it cached at startup, finds nothing, and renders **the key name itself**
as visible page text (for example `dashboard.schedule.title` instead of `Schedule`). This is a
user-facing regression on every screen that uses the new keys.

Files covered by this rule:

- `marlo-web/src/main/resources/global.properties` — default bundle.
- `marlo-web/src/main/resources/custom/<acronym>.properties` — per-Global-Unit overrides.

### Why a browser refresh does not help

1. Both files are **classpath resources** packaged into the WAR, not runtime configuration. They are
   read through `java.util.ResourceBundle`, whose cache is keyed by
   `(baseName, locale, classloader)` — only a new webapp classloader or a JVM restart invalidates it.
2. `marlo-web/src/main/resources/struts.properties` sets `struts.devMode=false`, so Struts does not
   reload bundles between requests the way it does in development.
3. `InternationalitazionFileInterceptor` calls
   `MarloLocalizedTextProvider.resetResourceBundles()` on every request, which looks like a reload but
   is not: it clears the list of **bundle names** registered on the current thread context so a user
   switching Global Unit does not inherit another program's overrides. It never re-reads the
   `.properties` files from disk.
4. Nothing in the request path re-reads the properties files. There is no admin action, no cache-bust
   parameter and no session flag that can do it.

### What does and does not pick up a changed i18n key

| Action | Picks up new / changed i18n keys? | Notes |
|---|---|---|
| Browser hard refresh, cache clear, private window | **No** | Server-side resolution; the client is not involved. |
| Static asset cache-bust query string (`?20260819`) | **No** | Only affects CSS / JS delivery. |
| Toggling the `crp_refresh` custom parameter | **No** | Reloads `custom_parameters` and phases into the session only. Common false friend. |
| Re-running the Jenkins job **without** a context restart | **No** | New file on disk, old bundle still cached in the running JVM. This is the production failure mode. |
| Tomcat **context reload / redeploy** of the WAR | Yes | New webapp classloader, bundle re-read. |
| Full **Tomcat / JVM restart** | Yes | Safest option; use this when in doubt. |
| `struts.devMode=true` | Yes | Development only. Never enable in a deployed environment. |

## Pre-deploy

1. Confirm the branch is merged into `staging` and CI is green (`mvn checkstyle:check`, `mvn test`,
   SonarCloud, Snyk).
2. Diff the release against the currently deployed revision and classify what it touches:
   ```bash
   git diff --stat <deployed-sha>..HEAD -- \
     marlo-web/src/main/resources/global.properties \
     marlo-web/src/main/resources/custom/ \
     marlo-web/src/main/resources/database/migrations/
   ```
3. **i18n:** if either properties path appears in that diff, the deploy is a **restart-required**
   deploy. Record it in the deploy ticket. List the affected key prefixes — they are the input to the
   post-deploy smoke check below:
   ```bash
   git diff <deployed-sha>..HEAD -- marlo-web/src/main/resources/global.properties \
     | grep '^+[A-Za-z0-9_.-]*=' | sed 's/^+//; s/=.*//'
   ```
4. **Migrations:** confirm every new `.sql` file under `database/migrations/` follows the
   `V<major>_<minor>_<patch>_<YYYYMMDD>_<HHMM>__<Description>.sql` naming and that no already-applied
   migration was edited. Flyway runs automatically on Tomcat startup and a checksum mismatch fails the
   whole application, not just the migration.
5. **Specificities:** if the release adds a `parameters` key, confirm the constant exists in *both*
   `APConstants.java` files and that `custom_parameters` rows exist for the target Global Units.
6. Confirm which Global Units are affected and whether any feature flag has to be switched on after
   the deploy.

## Deploy

1. Deploy the WAR through the environment's Jenkins job (`marlo-<branch-suffix>`).
2. Wait for Flyway to finish on startup and check the log for migration errors before testing anything
   in the browser. A failed migration surfaces as a site-wide error page, not as a partial failure.
3. **If the pre-deploy step 3 flagged i18n changes, restart the application** (context reload or full
   Tomcat restart) and confirm from the log that the context came up again. Do not treat "the WAR was
   copied" as done.

## Post-deploy smoke check

1. Run the raw-key check against the affected screen. It reads the key names straight from
   `global.properties`, so it stays correct as keys are added:
   ```bash
   scripts/post-deploy-smoke-i18n.sh https://<host>/dashboard.do dashboard.
   ```
   The dashboard requires an authenticated session. Either pass a cookie jar:
   ```bash
   SMOKE_COOKIE=/tmp/marlo-cookies.txt scripts/post-deploy-smoke-i18n.sh https://<host>/dashboard.do
   ```
   or save the page from an already-logged-in browser and check the file:
   ```bash
   scripts/post-deploy-smoke-i18n.sh /tmp/dashboard.html dashboard.
   ```
   A non-zero exit means raw keys are rendering — restart the application and re-run.
2. Load the homepage for one affected Global Unit and confirm no label reads as a dotted key name.
3. Open one save-heavy section (a project section, a deliverable) and save once, to confirm the
   interceptor stack and validators are intact.
4. If the release touched a phased save path, confirm the write replicated to the current and future
   phases and left past phases untouched.

## Rollback

1. Redeploy the previous WAR through the same Jenkins job, then restart the context — the same restart
   rule applies in reverse, since rolling back also changes the packaged properties files.
2. Flyway migrations are **not** rolled back automatically. A migration that must be undone needs a new
   forward migration; never delete or edit an applied one.
3. Where a release is gated behind a specificity, switching the `custom_parameters` value off is a
   faster partial mitigation than a full rollback and needs no restart.

## Common Pitfalls

1. Treating an i18n change as a front-end change and only clearing the browser cache.
2. Assuming the `crp_refresh` custom parameter refreshes translations. It does not.
3. Copying the WAR into a running exploded webapp and never reloading the context — the file on disk is
   new, the cached bundle is old, and the page shows raw keys.
4. Adding a key to `global.properties` but not to the `custom/<acronym>.properties` file of a program
   that overrides that section, so only that program shows the raw key.
5. Testing on an environment that happens to have been restarted for an unrelated reason, which hides
   the missing restart step in the deploy procedure itself.
6. Editing an already-applied Flyway migration instead of adding a new one, which fails the entire
   application at startup.

## Provenance

Raised as A2-2427 during QA regression testing of A2-2398 (homepage schedule card redesign), where 34
new `dashboard.schedule.*` keys were shipped and only rendered correctly because the test environment
had already been restarted.
