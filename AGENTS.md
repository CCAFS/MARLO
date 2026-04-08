# MARLO AI Agents

## Language
- All code, identifiers, comments, and technical content must be written in English.
- Replies can be in the same language as the user prompt.

## Project Overview
MARLO (Managing Agricultural Research for Learning and Outcomes) is an online management platform for CGIAR Research Programs.

## Project Structure
- `marlo-core`: Core configuration and initialization.
- `marlo-data`: Data layer (JPA entities and repositories).
- `marlo-web`: Web app (actions, REST endpoints, JSPs, JavaScript).
- `marlo-utils`: Utility classes.
- `marlo-parent`: Parent POM and dependency management.

## Technology Stack
- Java (backend and web layer)
- Maven (build and dependency management)
- Struts 2 (web framework)
- Hibernate/JPA (ORM)
- JSP (server-side templating)
- Tomcat 9 (local container via Cargo)
- JavaScript (frontend)
- SQL migrations

## Web Layer: Struts 2 vs Spring MVC
- **Struts 2**: Traditional web actions (`.do`, `.json`), JSPs, and interceptors. Main config: `marlo-web/src/main/resources/struts.xml` plus module-specific `struts-*.xml` files (e.g. `struts-projects.xml`, `struts-admin.xml`, `struts-api.xml`).
- **Spring MVC**: REST API under `/api/*`. These paths are excluded from Struts via `struts.action.excludePattern`. Controllers use `@RestController` and `@RequestMapping`.

## Configuration & Properties
- Location: `marlo-web/src/main/resources/config/`
- Files `marlo-dev.properties`, are in `.gitignore`; create them locally or use a template (e.g. `marlo-test.properties`).
- Spring profile selects the file: `marlo-${spring.profiles.active}.properties`. Active profiles: `dev`, `api`, `pro`, `test`.

## Required File Header (Java)
Use the GPL header for new Java files (as specified in the project setup guide):

```text
/*****************************************************************
 * This file is part of Managing Agricultural Research for Learning & 
 * Outcomes Platform (MARLO).
 * MARLO is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * at your option) any later version.
 * MARLO is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 * You should have received a copy of the GNU General Public License
 * along with MARLO. If not, see <http://www.gnu.org/licenses/>.
 *****************************************************************/
```

## Code Style & Formatting
### Java
- Formatter: `configuration/ccafs-java-style-config.xml`
- Import order: `configuration/ccafs-java-style.importorder`
- Indentation: 2 spaces
- Line length: 120
- Braces on the same line
- Use blocks for `if/while/for/do` (always)

### JavaScript
- Formatter: `configuration/ccafs-javascript-style.xml`
- Indentation: 2 spaces
- Braces on the same line
- Use blocks for `if/while/for/do` (always)

## Linting & Validation (Checkstyle)
- Config: `configuration/marlo-checkstyle.xml`
- Runs via `mvn checkstyle:checkstyle` or `mvn checkstyle:check`
- Key rules:
  - Max line length: 120
  - Max file length: 3500 lines
  - Naming conventions for methods, local variables, and type params
  - Package name regex: `^[a-z]+(\\.[a-z][a-z0-9]*)*$`
  - Padding rules for empty `for` initializer/iterator and method parameter spacing

## Database Migrations
- All schema changes must be done via migration scripts.
- Location: `marlo-web/src/main/resources/database/migrations/`
- Follow the existing naming pattern in that directory.
- Naming format (examples in repo): `V<major>_<minor>_<patch>_<YYYYMMDD>_<HHMM>__<Description>.sql`

## Specificity Implementation Guide
Use this workflow when creating a new specificity (feature flag based on `parameters` + `custom_parameters`).

### 1. Create migration for `parameters`
- Create one migration in `marlo-web/src/main/resources/database/migrations/`.
- Follow the same style used by existing specificity migrations (direct `INSERT ... VALUES`).
- Add one row per `global_unit_type_id` used by CRP/Platform/Center (`1`, `3`, `4`).
- Use `category = '2'` (Specificities) and `format = '1'` (boolean-like).

Template:

```sql
INSERT INTO parameters (global_unit_type_id, `key`, `description`, `format`, default_value, category)
VALUES ( '1', '<specificity_key>', '<Specificity description>', '1', 'false', '2');

INSERT INTO parameters (global_unit_type_id, `key`, `description`, `format`, default_value, category)
VALUES ( '3', '<specificity_key>', '<Specificity description>', '1', 'false', '2');

INSERT INTO parameters (global_unit_type_id, `key`, `description`, `format`, default_value, category)
VALUES ( '4', '<specificity_key>', '<Specificity description>', '1', 'false', '2');
```

### 2. Create/Update migration for `custom_parameters` values
- If the specificity must be enabled for a specific Global Unit, add inserts/updates in `custom_parameters`.
- Use `value = 'true'` or `value = 'false'` depending on rollout.
- Link using `parameter_id` from `parameters.key`.

Template:

```sql
INSERT INTO custom_parameters (`parameter_id`, `global_unit_id`, `value`, `created_by`, `is_active`, `active_since`, `modified_by`, `modification_justification`)
VALUES (
  (SELECT id FROM parameters WHERE `key` = '<specificity_key>' AND global_unit_type_id = 1),
  <global_unit_id>,
  'true',
  '3',
  '1',
  CURRENT_TIMESTAMP,
  '3',
  'Enable <specificity_key>'
);
```

### 3. Add constants in APConstants
- Add the new key in both constants files:
  - `marlo-data/src/main/java/org/cgiar/ccafs/marlo/config/APConstants.java`
  - `marlo-web/src/main/java/org/cgiar/ccafs/marlo/config/APConstants.java`
- Constant name should be uppercase snake case and value must match `parameters.key` exactly.

Template:

```java
public static final String <SPECIFICITY_CONSTANT_NAME> = "<specificity_key>";
```

### 4. Backend usage (Java)
- Prefer using APConstants constant instead of hardcoded strings.
- Typical usage is through `BaseAction.hasSpecificities(...)`.

Example:

```java
if (this.hasSpecificities(APConstants.<SPECIFICITY_CONSTANT_NAME>)) {
  // feature enabled behavior
} else {
  // fallback behavior
}
```

### 5. Frontend usage (FTL/JSP/JS)
- In FTL views, use `action.hasSpecificities('<specificity_key>')` to toggle sections.
- Keep behavior explicit with `[#if] ... [/#if]` blocks.

Example:

```ftl
[#if action.hasSpecificities('<specificity_key>')]
  [#-- enabled content --]
[/#if]
```

or for hide-on-true behavior:

```ftl
[#if !action.hasSpecificities('<specificity_key>')]
  [#-- default visible content --]
[/#if]
```

### 6. Validation checklist
- Migration file name follows `V<major>_<minor>_<patch>_<YYYYMMDD>_<HHMM>__<Description>.sql`.
- `parameters.key` and APConstants value are identical.
- Constant added in both APConstants files.
- Backend uses APConstants (no hardcoded key literals in Java).
- Frontend condition matches expected behavior (`show when true` or `hide when true`).
- If rollout is required, corresponding `custom_parameters` values are created.

## File Organization (Quick Reference)
- Actions: `marlo-web/src/main/java/org/cgiar/ccafs/marlo/action/`
- Base Action: `marlo-web/src/main/java/org/cgiar/ccafs/marlo/action/BaseAction.java`
- REST APIs: `marlo-web/src/main/java/org/cgiar/ccafs/marlo/rest/`
- Validators: `marlo-web/src/main/java/org/cgiar/ccafs/marlo/validation/`
- Interceptors: `marlo-web/src/main/java/org/cgiar/ccafs/marlo/interceptor/`
- Struts config: `marlo-web/src/main/resources/struts.xml`, `struts-*.xml`
- i18n: `marlo-web/src/main/resources/global.properties`, `custom/*.properties` (per CRP)
- SQL scripts: `marlo-web/src/main/resources/database/`
- Web resources: `marlo-web/src/main/webapp/`

## Common Tasks
- When adding new features, check similar implementations first.
- When modifying DB schema, add a migration file in the migrations directory.
- When adding actions, follow the existing action structure.
- When adding REST endpoints, follow existing REST API patterns.

## Domain Notes
- MARLO content and workflows reference CGIAR Research Programs (CRPs) across multiple resources and actions.

## Run Scripts by Branch Java Version (Local Development)
Scripts in `scripts/` run MARLO locally (build, update properties, start server). Choose by Java version:
- If the branch name contains `java17` or `java_17`, use the Java 17 run script in `scripts/`:
  - macOS/Linux: `scripts/run-marlo-java17.sh`
  - Windows: `scripts/run-marlo-java17.bat` (if provided; otherwise use `.sh` in Git Bash)
- Otherwise, use the Java 8 run script in `scripts/`:
  - macOS/Linux: `scripts/run-marlo-java8.sh`
  - Windows: `scripts/run-marlo-java8.bat` (if provided; otherwise use `.sh` in Git Bash)
