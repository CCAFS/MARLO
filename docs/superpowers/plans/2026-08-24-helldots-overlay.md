# HellDots Comment Overlay Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** Mount the HellDots comment overlay for authenticated MARLO users in non-production environments, persisting comments and screenshots through MARLO's own backend.

**Architecture:** A version-pinned UMD bundle plus a small adapter script are emitted from `footer.ftl` behind a two-part guard. The adapter wires the library's callbacks to five Spring MVC endpoints. Comments land in one table as indexable columns plus a `json` payload; screenshots are uploaded as files and referenced by URL. All widget UI lives in the library's Shadow DOM.

**Tech Stack:** Java 17, Struts 6.8 + FreeMarker (views), Spring MVC 5.3 (`/api/*`), Hibernate 5 with XML mappings, Shiro (session), MySQL 8, Flyway, JUnit 4.13.2 + Hamcrest, HellDots 0.7.0 (UMD).

## Global Constraints

- **Spec:** `docs/specs/enhancement/helldots-overlay/` — ENH-HELLDOTS-OVERLAY-001. Requirement IDs referenced per task.
- **Java level:** 17. Run with `scripts/run-marlo-java17.sh` (HTTP, `localhost:8080`, `cargo:run`). `run-marlo-java8.sh` fails with `invalid flag: --release` on this branch.
- **Database:** local MySQL — `mysql.host=localhost`, `mysql.database=aiccradb1`, port 3306.
- **Code style:** 2-space indent, 120-column limit, braces on the same line, mandatory blocks for `if/while/for/do`. `mvn checkstyle:check` is a gate.
- **GPL header** verbatim at the top of every new `.java` file:
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
- **English only** in code, identifiers and comments.
- **Migrations:** `marlo-web/src/main/resources/database/migrations/V<major>_<minor>_<patch>_<YYYYMMDD>_<HHMM>__<Description>.sql`. Additive only.
- **Hibernate:** `hibernate.cfg.xml` sets `hbm2ddl.auto=validate`. Every new `.hbm.xml` MUST be registered with a `<mapping resource="xmls/X.hbm.xml"/>` line **and** must match the DDL exactly, or the app fails to start.
- **No `pom.xml` changes.** The widget is a static asset, not a Maven dependency. No Mockito is available — tests are plain JUnit 4 against pure functions.
- **No `?html` in any FreeMarker template** — it is a parse error under Struts 6.8. Auto-escaping is already on.
- **Never commit** `marlo-dev.properties`, `tomcat/context.xml`, or a `C:` directory.
- **Commits** use the gitmoji/semantic convention: `<emoji> <type>(<scope>): <subject>`.

---

## Pre-flight

- [ ] **P1: Fix the local uploads folder**

`marlo-web/src/main/resources/config/marlo-dev.properties` (gitignored) has
`file.uploads.baseFolder=C:/xampp/htdocs/marlo` — a Windows path on macOS. Uploads land in a literal `C:`
directory at the repo root and are unreachable at `file.downloads=http://localhost:8080/marlo-web/data`.

Point it at a real directory that the downloads URL maps to, for example the exploded webapp's `data` folder.
Confirm the two agree: a file written under `file.uploads.baseFolder` must be fetchable under `file.downloads`.

Verify with an existing feature before writing any code — upload a project highlight image and fetch its URL.
Task 8 cannot pass until this is true.

- [ ] **P2: Confirm the branch**

Run: `git branch --show-current`

`feedback-overlay` carries ~20 commits of unrelated A2-2398 homepage work. Either accept that, or cut
`helldots-overlay` from `staging` first. Decide before Task 1.

- [ ] **P3: Baseline run**

Run: `bash scripts/run-marlo-java17.sh`
Expected: app starts, `http://localhost:8080/marlo-web/` serves the login page, sign-in works.

---

## File Structure

**marlo-data** — persistence, one responsibility per file, mirroring the module's existing convention:

| File | Responsibility |
|---|---|
| `data/model/HelldotsComment.java` | Entity: projected columns + raw payload string |
| `data/model/HelldotsScreenshot.java` | Entity: uploaded file registry row |
| `data/dao/HelldotsCommentDAO.java` + `dao/mysql/HelldotsCommentMySQLDAO.java` | Query by `comment_id`, by `page`, all active |
| `data/dao/HelldotsScreenshotDAO.java` + `dao/mysql/HelldotsScreenshotMySQLDAO.java` | Save and find screenshot rows |
| `data/manager/HelldotsCommentManager.java` + `impl/…Impl.java` | Transactional boundary over the comment DAO |
| `data/manager/HelldotsScreenshotManager.java` + `impl/…Impl.java` | Transactional boundary over the screenshot DAO |
| `resources/xmls/HelldotsComments.hbm.xml`, `HelldotsScreenshots.hbm.xml` | Hibernate mappings |

**marlo-web** — transport and presentation:

| File | Responsibility |
|---|---|
| `rest/helldots/HelldotsProjection.java` | **Pure functions.** Payload map → column values; the ten event types → an action. No Spring, no Hibernate, no session. This is where the testable logic lives. |
| `rest/helldots/HelldotsUploadValidator.java` | **Pure functions.** MIME and size validation, server-side filename generation. |
| `rest/helldots/HelldotsController.java` | The five endpoints. Session identity, authorisation, delegation to managers. |
| `webapp/global/js/vendor/helldots-0.7.0.umd.js` | Vendored library, unmodified |
| `webapp/global/js/helldots-init.js` | Adapter: config → overlay → callbacks |

Splitting `HelldotsProjection` and `HelldotsUploadValidator` out of the controller is deliberate: no Mockito is
available in this repo, so logic reachable only through a Spring controller cannot be unit tested at all. Pure
static functions can.

---

## Task 1: Database migration

**Requirements:** ENH-HELLDOTS-NF-008, AC-012
**Files:**
- Create: `marlo-web/src/main/resources/database/migrations/V2_6_0_20260824_1000__CreateHelldotsTables.sql`

**Interfaces:**
- Consumes: nothing
- Produces: tables `helldots_comments` and `helldots_screenshots`; every later task depends on these column names

- [ ] **Step 1: Write the migration**

Rename the file's timestamp to the actual creation time if 20260824_1000 has passed.

```sql
CREATE TABLE helldots_comments (
  id bigint NOT NULL AUTO_INCREMENT,
  comment_id varchar(64) NOT NULL,
  page varchar(500) NOT NULL,
  page_query varchar(1000) DEFAULT NULL,
  author_user_id bigint DEFAULT NULL,
  author_name varchar(255) DEFAULT NULL,
  status varchar(20) NOT NULL,
  type varchar(20) DEFAULT NULL,
  priority varchar(10) DEFAULT NULL,
  created_at datetime NOT NULL,
  edited_at datetime DEFAULT NULL,
  resolved_at datetime DEFAULT NULL,
  schema_version int DEFAULT NULL,
  global_unit_id bigint DEFAULT NULL,
  payload json NOT NULL,
  is_active tinyint(1) NOT NULL DEFAULT '1',
  active_since timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  created_by bigint DEFAULT NULL,
  modified_by bigint DEFAULT NULL,
  modification_justification text CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci,
  PRIMARY KEY (id),
  UNIQUE KEY helldots_comments_UN (comment_id),
  KEY helldots_comments_page_IDX (page),
  KEY helldots_comments_status_IDX (status),
  KEY helldots_comments_author_FK (author_user_id),
  KEY helldots_comments_users_FK (created_by),
  KEY helldots_comments_users_FK_1 (modified_by),
  KEY helldots_comments_global_units_FK (global_unit_id),
  CONSTRAINT helldots_comments_author_FK FOREIGN KEY (author_user_id) REFERENCES users (id) ON DELETE RESTRICT ON UPDATE RESTRICT,
  CONSTRAINT helldots_comments_users_FK FOREIGN KEY (created_by) REFERENCES users (id) ON DELETE RESTRICT ON UPDATE RESTRICT,
  CONSTRAINT helldots_comments_users_FK_1 FOREIGN KEY (modified_by) REFERENCES users (id) ON DELETE RESTRICT ON UPDATE RESTRICT,
  CONSTRAINT helldots_comments_global_units_FK FOREIGN KEY (global_unit_id) REFERENCES global_units (id) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE helldots_screenshots (
  id bigint NOT NULL AUTO_INCREMENT,
  comment_id varchar(64) DEFAULT NULL,
  kind varchar(20) NOT NULL,
  file_name varchar(255) NOT NULL,
  relative_path varchar(500) NOT NULL,
  content_type varchar(50) NOT NULL,
  byte_size bigint DEFAULT NULL,
  is_active tinyint(1) NOT NULL DEFAULT '1',
  active_since timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  created_by bigint DEFAULT NULL,
  PRIMARY KEY (id),
  KEY helldots_screenshots_comment_IDX (comment_id),
  KEY helldots_screenshots_users_FK (created_by),
  CONSTRAINT helldots_screenshots_users_FK FOREIGN KEY (created_by) REFERENCES users (id) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
```

- [ ] **Step 2: Apply it**

Run: `bash scripts/run-marlo-java17.sh`

`MarloFlywayConfiguration` runs `repair()` before `migrate()`, so a failed attempt is cleared automatically on
the next start.

- [ ] **Step 3: Verify both tables exist**

```bash
mysql -h localhost -u <user> -p aiccradb1 -e "SHOW COLUMNS FROM helldots_comments; SHOW COLUMNS FROM helldots_screenshots;"
```
Expected: both tables listed with the columns above.

- [ ] **Step 4: Verify Flyway recorded success**

```bash
mysql -h localhost -u <user> -p aiccradb1 -e "SELECT version, description, success FROM flyway_schema_history WHERE description LIKE '%Helldots%';"
```
Expected: one row, `success = 1`.

- [ ] **Step 5: Commit**

```bash
git add marlo-web/src/main/resources/database/migrations/V2_6_0_20260824_1000__CreateHelldotsTables.sql
git commit -m ":sparkles: feat(db): Add HellDots comment and screenshot tables"
```

---

## Task 2: Entities and Hibernate mappings

**Requirements:** ENH-HELLDOTS-FN-008, FN-009
**Files:**
- Create: `marlo-data/src/main/java/org/cgiar/ccafs/marlo/data/model/HelldotsComment.java`
- Create: `marlo-data/src/main/java/org/cgiar/ccafs/marlo/data/model/HelldotsScreenshot.java`
- Create: `marlo-data/src/main/resources/xmls/HelldotsComments.hbm.xml`
- Create: `marlo-data/src/main/resources/xmls/HelldotsScreenshots.hbm.xml`
- Modify: `marlo-data/src/main/resources/hibernate.cfg.xml`

**Interfaces:**
- Consumes: the tables from Task 1
- Produces: `HelldotsComment` with getters/setters `getCommentId()/setCommentId(String)`, `getPage()/setPage(String)`, `getPageQuery()/setPageQuery(String)`, `getAuthorUser()/setAuthorUser(User)`, `getAuthorName()/setAuthorName(String)`, `getStatus()/setStatus(String)`, `getType()/setType(String)`, `getPriority()/setPriority(String)`, `getCreatedAt()/setCreatedAt(Date)`, `getEditedAt()/setEditedAt(Date)`, `getResolvedAt()/setResolvedAt(Date)`, `getSchemaVersion()/setSchemaVersion(Integer)`, `getGlobalUnit()/setGlobalUnit(GlobalUnit)`, `getPayload()/setPayload(String)`; plus `getId()/setId(Long)`, `isActive()/setActive(boolean)`, `getCreatedBy()/setCreatedBy(User)`, `getModifiedBy()/setModifiedBy(User)`, `getActiveSince()/setActiveSince(Date)` inherited from `MarloAuditableEntity`. `HelldotsScreenshot` with `getCommentId()/setCommentId(String)`, `getKind()/setKind(String)`, `getFileName()/setFileName(String)`, `getRelativePath()/setRelativePath(String)`, `getContentType()/setContentType(String)`, `getByteSize()/setByteSize(Long)`.

`payload` is deliberately a `String`, not a parsed structure. Hibernate stores it into the MySQL `json` column
as text; parsing happens in `HelldotsProjection` (Task 4), which keeps the entity free of Jackson.

- [ ] **Step 1: Write `HelldotsComment.java`**

GPL header, then:

```java
package org.cgiar.ccafs.marlo.data.model;

import java.util.Date;

import com.google.gson.annotations.Expose;

public class HelldotsComment extends MarloAuditableEntity implements java.io.Serializable {

  private static final long serialVersionUID = 1L;

  @Expose
  private String commentId;
  @Expose
  private String page;
  @Expose
  private String pageQuery;
  private User authorUser;
  @Expose
  private String authorName;
  @Expose
  private String status;
  @Expose
  private String type;
  @Expose
  private String priority;
  @Expose
  private Date createdAt;
  @Expose
  private Date editedAt;
  @Expose
  private Date resolvedAt;
  @Expose
  private Integer schemaVersion;
  private GlobalUnit globalUnit;
  @Expose
  private String payload;

  public HelldotsComment() {
  }

  public String getAuthorName() {
    return authorName;
  }

  public User getAuthorUser() {
    return authorUser;
  }

  public String getCommentId() {
    return commentId;
  }

  public Date getCreatedAt() {
    return createdAt;
  }

  public Date getEditedAt() {
    return editedAt;
  }

  public GlobalUnit getGlobalUnit() {
    return globalUnit;
  }

  public String getPage() {
    return page;
  }

  public String getPageQuery() {
    return pageQuery;
  }

  public String getPayload() {
    return payload;
  }

  public String getPriority() {
    return priority;
  }

  public Date getResolvedAt() {
    return resolvedAt;
  }

  public Integer getSchemaVersion() {
    return schemaVersion;
  }

  public String getStatus() {
    return status;
  }

  public String getType() {
    return type;
  }

  public void setAuthorName(String authorName) {
    this.authorName = authorName;
  }

  public void setAuthorUser(User authorUser) {
    this.authorUser = authorUser;
  }

  public void setCommentId(String commentId) {
    this.commentId = commentId;
  }

  public void setCreatedAt(Date createdAt) {
    this.createdAt = createdAt;
  }

  public void setEditedAt(Date editedAt) {
    this.editedAt = editedAt;
  }

  public void setGlobalUnit(GlobalUnit globalUnit) {
    this.globalUnit = globalUnit;
  }

  public void setPage(String page) {
    this.page = page;
  }

  public void setPageQuery(String pageQuery) {
    this.pageQuery = pageQuery;
  }

  public void setPayload(String payload) {
    this.payload = payload;
  }

  public void setPriority(String priority) {
    this.priority = priority;
  }

  public void setResolvedAt(Date resolvedAt) {
    this.resolvedAt = resolvedAt;
  }

  public void setSchemaVersion(Integer schemaVersion) {
    this.schemaVersion = schemaVersion;
  }

  public void setStatus(String status) {
    this.status = status;
  }

  public void setType(String type) {
    this.type = type;
  }

}
```

- [ ] **Step 2: Write `HelldotsScreenshot.java`**

GPL header, then the same shape with fields `commentId` (String), `kind`, `fileName`, `relativePath`,
`contentType` (all String) and `byteSize` (Long), extending `MarloAuditableEntity`, `serialVersionUID = 1L`,
a no-arg constructor, and alphabetically ordered getters then setters.

- [ ] **Step 3: Write `HelldotsComments.hbm.xml`**

```xml
<?xml version="1.0" encoding="UTF-8" standalone="no"?>
<!DOCTYPE hibernate-mapping PUBLIC "-//Hibernate/Hibernate Mapping DTD 3.0//EN" "http://www.hibernate.org/dtd/hibernate-mapping-3.0.dtd">
<hibernate-mapping>
  <class name="org.cgiar.ccafs.marlo.data.model.HelldotsComment" table="helldots_comments" optimistic-lock="none">
    <id name="id" type="java.lang.Long">
      <column name="id" />
      <generator class="identity" />
    </id>
    <property name="commentId" type="string">
      <column name="comment_id" length="64" not-null="true" />
    </property>
    <property name="page" type="string">
      <column name="page" length="500" not-null="true" />
    </property>
    <property name="pageQuery" type="string">
      <column name="page_query" length="1000" />
    </property>
    <many-to-one name="authorUser" class="org.cgiar.ccafs.marlo.data.model.User" fetch="select">
      <column name="author_user_id" />
    </many-to-one>
    <property name="authorName" type="string">
      <column name="author_name" length="255" />
    </property>
    <property name="status" type="string">
      <column name="status" length="20" not-null="true" />
    </property>
    <property name="type" type="string">
      <column name="type" length="20" />
    </property>
    <property name="priority" type="string">
      <column name="priority" length="10" />
    </property>
    <property name="createdAt" type="timestamp">
      <column name="created_at" length="19" not-null="true" />
    </property>
    <property name="editedAt" type="timestamp">
      <column name="edited_at" length="19" />
    </property>
    <property name="resolvedAt" type="timestamp">
      <column name="resolved_at" length="19" />
    </property>
    <property name="schemaVersion" type="java.lang.Integer">
      <column name="schema_version" />
    </property>
    <many-to-one name="globalUnit" class="org.cgiar.ccafs.marlo.data.model.GlobalUnit" fetch="select">
      <column name="global_unit_id" />
    </many-to-one>
    <property name="payload" type="string">
      <column name="payload" sql-type="json" not-null="true" />
    </property>
    <property name="active" type="boolean">
      <column name="is_active" not-null="true" />
    </property>
    <property name="activeSince" type="timestamp" update="false">
      <column name="active_since" length="19" not-null="true" />
    </property>
    <property name="modificationJustification" type="string">
      <column name="modification_justification" sql-type="TEXT" />
    </property>
    <many-to-one name="createdBy" class="org.cgiar.ccafs.marlo.data.model.User" fetch="select" update="false">
      <column name="created_by" />
    </many-to-one>
    <many-to-one name="modifiedBy" class="org.cgiar.ccafs.marlo.data.model.User" fetch="select">
      <column name="modified_by" />
    </many-to-one>
  </class>
</hibernate-mapping>
```

- [ ] **Step 4: Write `HelldotsScreenshots.hbm.xml`**

Same structure, `table="helldots_screenshots"`, mapping `commentId`→`comment_id` (length 64, nullable),
`kind`→`kind` (20, not-null), `fileName`→`file_name` (255, not-null), `relativePath`→`relative_path` (500,
not-null), `contentType`→`content_type` (50, not-null), `byteSize`→`byte_size` (`java.lang.Long`), plus
`active`, `activeSince` and `createdBy` exactly as above. There is no `modified_by` or
`modification_justification` column on this table, so do not map them.

- [ ] **Step 5: Register both mappings**

In `marlo-data/src/main/resources/hibernate.cfg.xml`, add beside the existing `<mapping resource=…/>` lines:

```xml
<mapping resource="xmls/HelldotsComments.hbm.xml"/>
<mapping resource="xmls/HelldotsScreenshots.hbm.xml"/>
```

Missing this is the single most likely failure in this task: `hbm2ddl.auto=validate` means a mapping that
disagrees with the DDL — or an entity with no mapping at all — stops the app at startup.

- [ ] **Step 6: Compile**

Run: `mvn clean install -DskipTests -pl marlo-data -am`
Expected: BUILD SUCCESS.

- [ ] **Step 7: Verify Hibernate validates the mappings**

Run: `bash scripts/run-marlo-java17.sh`
Expected: the app starts with no `SchemaManagementException` and no "missing table/column" error in the log.
A mismatch between the `.hbm.xml` and the DDL surfaces here, not at compile time.

- [ ] **Step 8: Checkstyle**

Run: `mvn checkstyle:check -pl marlo-data`
Expected: no violations.

- [ ] **Step 9: Commit**

```bash
git add marlo-data/src/main/java/org/cgiar/ccafs/marlo/data/model/HelldotsComment.java \
        marlo-data/src/main/java/org/cgiar/ccafs/marlo/data/model/HelldotsScreenshot.java \
        marlo-data/src/main/resources/xmls/HelldotsComments.hbm.xml \
        marlo-data/src/main/resources/xmls/HelldotsScreenshots.hbm.xml \
        marlo-data/src/main/resources/hibernate.cfg.xml
git commit -m ":sparkles: feat(db): Map HellDots entities to Hibernate"
```

---

## Task 3: DAOs and Managers

**Requirements:** ENH-HELLDOTS-FN-003, FN-005, FN-008
**Files:**
- Create: `marlo-data/src/main/java/org/cgiar/ccafs/marlo/data/dao/HelldotsCommentDAO.java`
- Create: `marlo-data/src/main/java/org/cgiar/ccafs/marlo/data/dao/mysql/HelldotsCommentMySQLDAO.java`
- Create: `marlo-data/src/main/java/org/cgiar/ccafs/marlo/data/dao/HelldotsScreenshotDAO.java`
- Create: `marlo-data/src/main/java/org/cgiar/ccafs/marlo/data/dao/mysql/HelldotsScreenshotMySQLDAO.java`
- Create: `marlo-data/src/main/java/org/cgiar/ccafs/marlo/data/manager/HelldotsCommentManager.java`
- Create: `marlo-data/src/main/java/org/cgiar/ccafs/marlo/data/manager/impl/HelldotsCommentManagerImpl.java`
- Create: `marlo-data/src/main/java/org/cgiar/ccafs/marlo/data/manager/HelldotsScreenshotManager.java`
- Create: `marlo-data/src/main/java/org/cgiar/ccafs/marlo/data/manager/impl/HelldotsScreenshotManagerImpl.java`

**Interfaces:**
- Consumes: `HelldotsComment`, `HelldotsScreenshot` from Task 2
- Produces: `HelldotsCommentManager` with `findByCommentId(String commentId) → HelldotsComment` (null when absent or inactive), `findByPage(String page) → List<HelldotsComment>` (never null; empty list when none), `findAllActive() → List<HelldotsComment>` (never null), `save(HelldotsComment) → HelldotsComment`. `HelldotsScreenshotManager` with `save(HelldotsScreenshot) → HelldotsScreenshot`.

- [ ] **Step 1: Write `HelldotsCommentDAO.java`**

GPL header, then:

```java
package org.cgiar.ccafs.marlo.data.dao;

import org.cgiar.ccafs.marlo.data.model.HelldotsComment;

import java.util.List;

public interface HelldotsCommentDAO {

  public HelldotsComment find(long id);

  public List<HelldotsComment> findAllActive();

  public HelldotsComment findByCommentId(String commentId);

  public List<HelldotsComment> findByPage(String page);

  public HelldotsComment save(HelldotsComment helldotsComment);
}
```

- [ ] **Step 2: Write `HelldotsCommentMySQLDAO.java`**

GPL header, then:

```java
package org.cgiar.ccafs.marlo.data.dao.mysql;

import org.cgiar.ccafs.marlo.data.dao.HelldotsCommentDAO;
import org.cgiar.ccafs.marlo.data.model.HelldotsComment;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

import org.hibernate.SessionFactory;
import org.hibernate.query.Query;

@Named
public class HelldotsCommentMySQLDAO extends AbstractMarloDAO<HelldotsComment, Long>
  implements HelldotsCommentDAO {

  @Inject
  public HelldotsCommentMySQLDAO(SessionFactory sessionFactory) {
    super(sessionFactory);
  }

  @Override
  public HelldotsComment find(long id) {
    return super.find(HelldotsComment.class, id);
  }

  @Override
  public List<HelldotsComment> findAllActive() {
    String hql = "select hc from HelldotsComment hc where hc.active = true order by hc.createdAt desc";
    List<HelldotsComment> list = super.findAll(hql);
    if (list == null) {
      return new ArrayList<>();
    }
    return list;
  }

  @Override
  public HelldotsComment findByCommentId(String commentId) {
    String hql = "select hc from HelldotsComment hc where hc.commentId = :commentId and hc.active = true";
    Query<HelldotsComment> createQuery = this.getSessionFactory().getCurrentSession().createQuery(hql);
    createQuery.setParameter("commentId", commentId);
    return super.findSingleResult(HelldotsComment.class, createQuery);
  }

  @Override
  public List<HelldotsComment> findByPage(String page) {
    String hql =
      "select hc from HelldotsComment hc where hc.page = :page and hc.active = true order by hc.createdAt asc";
    Query<HelldotsComment> createQuery = this.getSessionFactory().getCurrentSession().createQuery(hql);
    createQuery.setParameter("page", page);
    List<HelldotsComment> list = super.findAll(createQuery);
    if (list == null) {
      return new ArrayList<>();
    }
    return list;
  }

  @Override
  public HelldotsComment save(HelldotsComment helldotsComment) {
    if (helldotsComment.getId() == null) {
      super.saveEntity(helldotsComment);
    } else {
      helldotsComment = super.update(helldotsComment);
    }
    return helldotsComment;
  }
}
```

Named parameters (`:commentId`, `:page`) are mandatory here — `commentId` and `page` both arrive from the
client. Never build these queries by concatenation.

This matches `ActivityTitleMySQLDAO.findByCurrentYear` exactly: `this.getSessionFactory()` is package-private
on `AbstractMarloDAO` and therefore reachable from `dao.mysql`, `createQuery(hql)` takes no class argument, and
`super.findAll(createQuery)` / `super.findSingleResult(...)` / `super.saveEntity(...)` / `super.update(...)`
all exist with these signatures. The one deliberate divergence: the repo's DAOs return `null` for an empty
list; these return an empty list, because the controller feeds the result straight to a JSON array.

- [ ] **Step 3: Write `HelldotsScreenshotDAO.java` and `HelldotsScreenshotMySQLDAO.java`**

Same shape, but only `find(long id)` and `save(HelldotsScreenshot)`. No query takes client input.

- [ ] **Step 4: Write the two manager interfaces**

`HelldotsCommentManager` declares exactly the five methods of `HelldotsCommentDAO`.
`HelldotsScreenshotManager` declares `find(long id)` and `save(HelldotsScreenshot)`.

- [ ] **Step 5: Write the two `ManagerImpl` classes**

GPL header, then, for the comment manager:

```java
package org.cgiar.ccafs.marlo.data.manager.impl;

import org.cgiar.ccafs.marlo.data.dao.HelldotsCommentDAO;
import org.cgiar.ccafs.marlo.data.manager.HelldotsCommentManager;
import org.cgiar.ccafs.marlo.data.model.HelldotsComment;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

import org.springframework.transaction.annotation.Transactional;

@Named
public class HelldotsCommentManagerImpl implements HelldotsCommentManager {

  private HelldotsCommentDAO helldotsCommentDAO;

  @Inject
  public HelldotsCommentManagerImpl(HelldotsCommentDAO helldotsCommentDAO) {
    this.helldotsCommentDAO = helldotsCommentDAO;
  }

  @Override
  public HelldotsComment find(long id) {
    return helldotsCommentDAO.find(id);
  }

  @Override
  public List<HelldotsComment> findAllActive() {
    return helldotsCommentDAO.findAllActive();
  }

  @Override
  public HelldotsComment findByCommentId(String commentId) {
    return helldotsCommentDAO.findByCommentId(commentId);
  }

  @Override
  public List<HelldotsComment> findByPage(String page) {
    return helldotsCommentDAO.findByPage(page);
  }

  @Override
  @Transactional
  public HelldotsComment save(HelldotsComment helldotsComment) {
    return helldotsCommentDAO.save(helldotsComment);
  }
}
```

`@Transactional` goes on writes only, matching `FeedbackStatusManagerImpl`.

- [ ] **Step 6: Compile and checkstyle**

Run: `mvn clean install -DskipTests -pl marlo-data -am && mvn checkstyle:check -pl marlo-data`
Expected: BUILD SUCCESS, no violations.

- [ ] **Step 7: Verify the beans wire**

Run: `bash scripts/run-marlo-java17.sh`
Expected: app starts; no `NoSuchBeanDefinitionException` or unsatisfied-dependency error for either manager.

- [ ] **Step 8: Commit**

```bash
git add marlo-data/src/main/java/org/cgiar/ccafs/marlo/data/dao marlo-data/src/main/java/org/cgiar/ccafs/marlo/data/manager
git commit -m ":sparkles: feat(db): Add HellDots DAOs and managers"
```

---

## Task 4: Pure projection and validation logic (TDD)

**Requirements:** ENH-HELLDOTS-FN-006, NF-004
**Files:**
- Create: `marlo-web/src/main/java/org/cgiar/ccafs/marlo/rest/helldots/HelldotsProjection.java`
- Create: `marlo-web/src/main/java/org/cgiar/ccafs/marlo/rest/helldots/HelldotsUploadValidator.java`
- Test: `marlo-web/src/test/java/org/cgiar/ccafs/marlo/rest/helldots/HelldotsProjectionTest.java`
- Test: `marlo-web/src/test/java/org/cgiar/ccafs/marlo/rest/helldots/HelldotsUploadValidatorTest.java`

**Interfaces:**
- Consumes: nothing — these are pure functions with no Spring, Hibernate or session dependency
- Produces: `HelldotsProjection.actionFor(String eventType) → HelldotsProjection.Action` (enum `UPSERT`, `SOFT_DELETE`, `UNKNOWN`); `HelldotsProjection.commentIdOf(Map<String, Object> comment) → String`; `HelldotsProjection.stringField(Map<String, Object> comment, String key) → String`; `HelldotsProjection.intField(Map<String, Object> comment, String key) → Integer`; `HelldotsProjection.dateField(Map<String, Object> comment, String key) → Date`; `HelldotsProjection.pathOf(String page) → String`; `HelldotsProjection.STATUSES`, `TYPES`, `PRIORITIES` as `Set<String>`. `HelldotsUploadValidator.isAllowedContentType(String) → boolean`; `HelldotsUploadValidator.isWithinSize(long bytes, long maxBytes) → boolean`; `HelldotsUploadValidator.generateFileName(String contentType) → String`.

This is the task where real tests are possible. No Mockito exists in this repo, so anything reachable only
through a Spring controller cannot be unit tested — which is exactly why this logic lives here.

- [ ] **Step 1: Write the failing tests for `HelldotsProjection`**

```java
package org.cgiar.ccafs.marlo.rest.helldots;

import java.util.HashMap;
import java.util.Map;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

public class HelldotsProjectionTest {

  @Test
  public void deletedEventSoftDeletes() {
    assertEquals(HelldotsProjection.Action.SOFT_DELETE, HelldotsProjection.actionFor("comment:deleted"));
  }

  @Test
  public void mutatingEventsUpsert() {
    String[] types = {"comment:created", "comment:edited", "comment:status-changed", "comment:updated",
      "comment:anchor-lost", "reply:added", "reply:deleted", "reply:edited", "reaction:toggled"};
    for (String type : types) {
      assertEquals(type, HelldotsProjection.Action.UPSERT, HelldotsProjection.actionFor(type));
    }
  }

  @Test
  public void unknownEventIsRejected() {
    assertEquals(HelldotsProjection.Action.UNKNOWN, HelldotsProjection.actionFor("comment:exploded"));
    assertEquals(HelldotsProjection.Action.UNKNOWN, HelldotsProjection.actionFor(null));
  }

  @Test
  public void stringFieldReadsAndTolerates() {
    Map<String, Object> comment = new HashMap<>();
    comment.put("status", "open");
    comment.put("type", null);
    assertEquals("open", HelldotsProjection.stringField(comment, "status"));
    assertNull(HelldotsProjection.stringField(comment, "type"));
    assertNull(HelldotsProjection.stringField(comment, "absent"));
  }

  @Test
  public void pathOfStripsQueryAndFragment() {
    assertEquals("/dashboard.do", HelldotsProjection.pathOf("/dashboard.do?projectID=123"));
    assertEquals("/dashboard.do", HelldotsProjection.pathOf("/dashboard.do#anchor"));
    assertEquals("/dashboard.do", HelldotsProjection.pathOf("/dashboard.do"));
  }

  @Test
  public void enumerationsMatchTheLibrary() {
    assertTrue(HelldotsProjection.STATUSES.contains("in_review"));
    assertTrue(HelldotsProjection.TYPES.contains("improvement"));
    assertTrue(HelldotsProjection.PRIORITIES.contains("medium"));
    assertEquals(4, HelldotsProjection.STATUSES.size());
    assertEquals(4, HelldotsProjection.TYPES.size());
    assertEquals(3, HelldotsProjection.PRIORITIES.size());
  }
}
```

- [ ] **Step 2: Run the tests and confirm they fail**

Run: `mvn test -pl marlo-web -Dtest=HelldotsProjectionTest`
Expected: FAIL — `HelldotsProjection` does not exist (compilation error).

- [ ] **Step 3: Write `HelldotsProjection.java`**

GPL header, then:

```java
package org.cgiar.ccafs.marlo.rest.helldots;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.TimeZone;

/**
 * Pure translation between a HellDots serialized comment and the columns projected out of it.
 * Deliberately free of Spring, Hibernate and session state so it can be unit tested directly.
 */
public final class HelldotsProjection {

  public enum Action {
    UPSERT, SOFT_DELETE, UNKNOWN
  }

  public static final Set<String> STATUSES =
    Collections.unmodifiableSet(new HashSet<>(Arrays.asList("open", "in_progress", "in_review", "resolved")));

  public static final Set<String> TYPES =
    Collections.unmodifiableSet(new HashSet<>(Arrays.asList("bug", "suggestion", "question", "improvement")));

  public static final Set<String> PRIORITIES =
    Collections.unmodifiableSet(new HashSet<>(Arrays.asList("high", "medium", "low")));

  private static final Set<String> UPSERT_EVENTS = Collections.unmodifiableSet(new HashSet<>(
    Arrays.asList("comment:created", "comment:edited", "comment:status-changed", "comment:updated",
      "comment:anchor-lost", "reply:added", "reply:deleted", "reply:edited", "reaction:toggled")));

  private static final String ISO_FORMAT = "yyyy-MM-dd'T'HH:mm:ss.SSSXXX";

  public static Action actionFor(String eventType) {
    if (eventType == null) {
      return Action.UNKNOWN;
    }
    if ("comment:deleted".equals(eventType)) {
      return Action.SOFT_DELETE;
    }
    if (UPSERT_EVENTS.contains(eventType)) {
      return Action.UPSERT;
    }
    return Action.UNKNOWN;
  }

  public static String commentIdOf(Map<String, Object> comment) {
    return stringField(comment, "id");
  }

  /**
   * Parses an ISO-8601 timestamp as written by the widget. Returns null for an absent or unparseable value:
   * a clock the server does not control is not worth failing a write over.
   */
  public static Date dateField(Map<String, Object> comment, String key) {
    String raw = stringField(comment, key);
    if (raw == null || raw.isEmpty()) {
      return null;
    }
    SimpleDateFormat format = new SimpleDateFormat(ISO_FORMAT);
    format.setTimeZone(TimeZone.getTimeZone("UTC"));
    try {
      return format.parse(raw);
    } catch (ParseException e) {
      return null;
    }
  }

  public static Integer intField(Map<String, Object> comment, String key) {
    if (comment == null) {
      return null;
    }
    Object value = comment.get(key);
    if (value instanceof Number) {
      return Integer.valueOf(((Number) value).intValue());
    }
    return null;
  }

  /**
   * The widget records location.pathname, but a defensive strip keeps a query or fragment out of the
   * indexed column if a host ever passes a fuller URL.
   */
  public static String pathOf(String page) {
    if (page == null) {
      return null;
    }
    String path = page;
    int query = path.indexOf('?');
    if (query >= 0) {
      path = path.substring(0, query);
    }
    int fragment = path.indexOf('#');
    if (fragment >= 0) {
      path = path.substring(0, fragment);
    }
    return path;
  }

  public static String stringField(Map<String, Object> comment, String key) {
    if (comment == null) {
      return null;
    }
    Object value = comment.get(key);
    if (value == null) {
      return null;
    }
    return String.valueOf(value);
  }

  private HelldotsProjection() {
  }
}
```

- [ ] **Step 4: Run the tests and confirm they pass**

Run: `mvn test -pl marlo-web -Dtest=HelldotsProjectionTest`
Expected: PASS, 6 tests.

- [ ] **Step 5: Write the failing tests for `HelldotsUploadValidator`**

```java
package org.cgiar.ccafs.marlo.rest.helldots;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;

public class HelldotsUploadValidatorTest {

  @Test
  public void acceptsJpegAndPng() {
    assertTrue(HelldotsUploadValidator.isAllowedContentType("image/jpeg"));
    assertTrue(HelldotsUploadValidator.isAllowedContentType("image/png"));
  }

  @Test
  public void rejectsEverythingElse() {
    assertFalse(HelldotsUploadValidator.isAllowedContentType("application/pdf"));
    assertFalse(HelldotsUploadValidator.isAllowedContentType("image/svg+xml"));
    assertFalse(HelldotsUploadValidator.isAllowedContentType("text/html"));
    assertFalse(HelldotsUploadValidator.isAllowedContentType(null));
  }

  @Test
  public void sizeBoundaryIsInclusive() {
    assertTrue(HelldotsUploadValidator.isWithinSize(100L, 100L));
    assertFalse(HelldotsUploadValidator.isWithinSize(101L, 100L));
    assertFalse(HelldotsUploadValidator.isWithinSize(0L, 100L));
  }

  @Test
  public void screenshotCapIsFiveMegabytes() {
    assertEquals(5L * 1024L * 1024L, HelldotsUploadValidator.MAX_SCREENSHOT_BYTES);
    assertTrue(HelldotsUploadValidator.isWithinSize(120000L, HelldotsUploadValidator.MAX_SCREENSHOT_BYTES));
    assertFalse(
      HelldotsUploadValidator.isWithinSize(20L * 1024L * 1024L, HelldotsUploadValidator.MAX_SCREENSHOT_BYTES));
  }

  @Test
  public void generatedNameCarriesTheRightExtensionAndIsUnique() {
    String first = HelldotsUploadValidator.generateFileName("image/jpeg");
    String second = HelldotsUploadValidator.generateFileName("image/jpeg");
    assertTrue(first.endsWith(".jpg"));
    assertTrue(HelldotsUploadValidator.generateFileName("image/png").endsWith(".png"));
    assertNotEquals(first, second);
  }

  @Test
  public void generatedNameContainsNoPathSeparator() {
    String name = HelldotsUploadValidator.generateFileName("image/png");
    assertEquals(-1, name.indexOf('/'));
    assertEquals(-1, name.indexOf('\\'));
    assertEquals(-1, name.indexOf(".."));
  }
}
```

- [ ] **Step 6: Run the tests and confirm they fail**

Run: `mvn test -pl marlo-web -Dtest=HelldotsUploadValidatorTest`
Expected: FAIL — `HelldotsUploadValidator` does not exist.

- [ ] **Step 7: Write `HelldotsUploadValidator.java`**

GPL header, then:

```java
package org.cgiar.ccafs.marlo.rest.helldots;

import java.util.UUID;

/**
 * Upload guards for HellDots screenshots. The generated name never derives from client input, so a
 * crafted filename cannot reach a filesystem path.
 */
public final class HelldotsUploadValidator {

  /**
   * Cap for a single screenshot. Deliberately not `file.maxSizeAllowed.bytes`: no Java in this repository
   * reads that property, and MARLO's general document cap is orders of magnitude larger than any capture the
   * widget produces (an automatic one is around 33 KB).
   */
  public static final long MAX_SCREENSHOT_BYTES = 5L * 1024L * 1024L;

  private static final String JPEG = "image/jpeg";
  private static final String PNG = "image/png";

  public static String generateFileName(String contentType) {
    String extension = PNG.equals(contentType) ? ".png" : ".jpg";
    return "helldots-" + UUID.randomUUID().toString() + extension;
  }

  public static boolean isAllowedContentType(String contentType) {
    return JPEG.equals(contentType) || PNG.equals(contentType);
  }

  public static boolean isWithinSize(long bytes, long maxBytes) {
    return bytes > 0L && bytes <= maxBytes;
  }

  private HelldotsUploadValidator() {
  }
}
```

- [ ] **Step 8: Run the tests and confirm they pass**

Run: `mvn test -pl marlo-web -Dtest=HelldotsUploadValidatorTest`
Expected: PASS, 6 tests.

- [ ] **Step 9: Run both test classes together and checkstyle**

Run: `mvn test -pl marlo-web -Dtest='Helldots*Test' && mvn checkstyle:check -pl marlo-web`
Expected: 12 tests pass, no checkstyle violations.

- [ ] **Step 10: Commit**

```bash
git add marlo-web/src/main/java/org/cgiar/ccafs/marlo/rest/helldots marlo-web/src/test/java/org/cgiar/ccafs/marlo/rest/helldots
git commit -m ":sparkles: feat(api): Add HellDots projection and upload validation"
```

---

## Task 5: Map the DispatcherServlet to /api/*

**Requirements:** ENH-HELLDOTS-OQ-001, design §4
**Files:**
- Modify: `marlo-web/src/main/java/org/cgiar/ccafs/marlo/WebAppInitializer.java:142`

**Interfaces:**
- Consumes: nothing
- Produces: `/api/*` served by Spring MVC in non-production. Tasks 6, 7 and 8 return 404 without this.

Blocked on pre-flight decision P2 and on ENH-HELLDOTS-OQ-001 being resolved or accepted.

- [ ] **Step 1: Record the current behaviour**

Run the app and confirm the baseline, so the change has something to be compared against:

```bash
curl -s -o /dev/null -w "%{http_code}\n" http://localhost:8080/marlo-web/api/
```
Expected: `404`.

- [ ] **Step 2: Add the mapping**

In `WebAppInitializer.java`, the block currently reads:

```java
      ServletRegistration.Dynamic dispatcher =
      servletContext.addServlet("dispatcher", new DispatcherServlet(dispatcherContext));
      dispatcher.setLoadOnStartup(1);
      dispatcher.addMapping(REST_SWAGGER_REQUESTS);
```

Add one line after the existing mapping:

```java
      ServletRegistration.Dynamic dispatcher =
      servletContext.addServlet("dispatcher", new DispatcherServlet(dispatcherContext));
      dispatcher.setLoadOnStartup(1);
      dispatcher.addMapping(REST_SWAGGER_REQUESTS);
      dispatcher.addMapping(REST_API_REQUESTS);
```

Do not move the block, do not change the surrounding `if (!activeEnv.equals(SPRING_PROFILE_PRODUCTION))` guard,
and do not touch the filter registrations above it.

- [ ] **Step 3: Restart and verify `/api/*` is served**

Run: `bash scripts/run-marlo-java17.sh`, then:

```bash
curl -s -o /dev/null -w "%{http_code}\n" http://localhost:8080/marlo-web/api/
```
Expected: no longer `404` from the container — a Spring-produced status (`401`, `403` or `404` with a JSON body)
means the dispatcher is now handling the prefix.

- [ ] **Step 4: Verify nothing else broke**

Check each, expecting unchanged behaviour:
- `http://localhost:8080/marlo-web/` — login page renders.
- Sign in, open the dashboard and two other `.do` pages — all render.
- `http://localhost:8080/marlo-web/swagger/` — still responds as before.

This is risk R-001 in the spec. If any `.do` page changes behaviour, stop and report before continuing.

- [ ] **Step 5: Commit**

```bash
git add marlo-web/src/main/java/org/cgiar/ccafs/marlo/WebAppInitializer.java
git commit -m ":wrench: chore(api): Map the Spring dispatcher to /api/* outside production"
```

---

## Task 6: Read endpoints

**Requirements:** ENH-HELLDOTS-FN-003, FN-004, FN-005, NF-005
**Files:**
- Create: `marlo-web/src/main/java/org/cgiar/ccafs/marlo/rest/helldots/HelldotsController.java`

**Interfaces:**
- Consumes: `HelldotsCommentManager` (Task 3), `HelldotsProjection` (Task 4), `APConfig` (existing `@Named` bean), `/api/*` mapping (Task 5)
- Produces: `GET /api/helldots/comments?page=`, `?all=true`, `GET /api/helldots/comments/{commentId}`. Each returns a JSON array (or object) whose elements are the stored payloads, ready for `loadComments()` with no client-side transformation. Task 9's adapter depends on this shape.

- [ ] **Step 1: Write the controller with the read endpoints**

GPL header, then:

```java
package org.cgiar.ccafs.marlo.rest.helldots;

import org.cgiar.ccafs.marlo.data.manager.HelldotsCommentManager;
import org.cgiar.ccafs.marlo.data.model.HelldotsComment;
import org.cgiar.ccafs.marlo.utils.APConfig;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.subject.Subject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/helldots")
public class HelldotsController {

  private static final Logger LOG = LoggerFactory.getLogger(HelldotsController.class);

  private final HelldotsCommentManager helldotsCommentManager;
  private final APConfig config;
  private final ObjectMapper objectMapper = new ObjectMapper();

  @Inject
  public HelldotsController(HelldotsCommentManager helldotsCommentManager, APConfig config) {
    this.helldotsCommentManager = helldotsCommentManager;
    this.config = config;
  }

  @RequestMapping(value = "/comments", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<String> getComments(@RequestParam(value = "page", required = false) String page,
    @RequestParam(value = "all", required = false, defaultValue = "false") boolean all) {
    if (this.isDisabled()) {
      return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }
    if (!this.isAuthenticated()) {
      return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
    }
    List<HelldotsComment> comments;
    if (all) {
      comments = helldotsCommentManager.findAllActive();
    } else {
      comments = helldotsCommentManager.findByPage(HelldotsProjection.pathOf(page));
    }
    return new ResponseEntity<>(this.toPayloadArray(comments), HttpStatus.OK);
  }

  @RequestMapping(value = "/comments/{commentId}", method = RequestMethod.GET,
    produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<String> getOneComment(@PathVariable("commentId") String commentId) {
    if (this.isDisabled()) {
      return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }
    if (!this.isAuthenticated()) {
      return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
    }
    HelldotsComment comment = helldotsCommentManager.findByCommentId(commentId);
    if (comment == null) {
      return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }
    return new ResponseEntity<>(comment.getPayload(), HttpStatus.OK);
  }

  private boolean isAuthenticated() {
    Subject subject = SecurityUtils.getSubject();
    return subject != null && subject.getPrincipal() != null;
  }

  /**
   * ENH-HELLDOTS-NF-005. Defence in depth: the dispatcher is not even registered in production, but an
   * environment that somehow reaches these endpoints must still be refused.
   */
  private boolean isDisabled() {
    return config.isProduction();
  }

  /**
   * The stored payload is already the widget's own serialized shape, so it is concatenated rather than
   * re-serialized: parsing and re-emitting would only risk changing it.
   */
  private String toPayloadArray(List<HelldotsComment> comments) {
    List<String> payloads = new ArrayList<>();
    for (HelldotsComment comment : comments) {
      if (comment.getPayload() != null) {
        payloads.add(comment.getPayload());
      }
    }
    return "[" + String.join(",", payloads) + "]";
  }
}
```

The class-level `@RequestMapping("/helldots")` combines with the dispatcher's `/api/*` mapping to give
`/api/helldots/...`. `APConfig` is `@Named`, so it injects like any other bean; `isProduction()` backs the
NF-005 guard and `getUploadsBaseFolder()` / `getDownloadURL()` are used in Task 8.

- [ ] **Step 2: Insert one comment by hand to read back**

```bash
mysql -h localhost -u <user> -p aiccradb1 -e \
"INSERT INTO helldots_comments (comment_id, page, status, created_at, payload, is_active, active_since)
 VALUES ('test-1', '/dashboard.do', 'open', NOW(), '{\"id\":\"test-1\",\"text\":\"hello\",\"page\":\"/dashboard.do\",\"status\":\"open\"}', 1, NOW());"
```

- [ ] **Step 3: Verify the page query**

Sign in through the browser first so the session cookie exists, then reuse it:

```bash
curl -s -b "JSESSIONID=<your-session-id>" \
  "http://localhost:8080/marlo-web/api/helldots/comments?page=%2Fdashboard.do"
```
Expected: `[{"id":"test-1","text":"hello","page":"/dashboard.do","status":"open"}]`

- [ ] **Step 4: Verify the single-comment and all-comments queries**

```bash
curl -s -b "JSESSIONID=<id>" "http://localhost:8080/marlo-web/api/helldots/comments/test-1"
curl -s -b "JSESSIONID=<id>" "http://localhost:8080/marlo-web/api/helldots/comments?all=true"
curl -s -o /dev/null -w "%{http_code}\n" "http://localhost:8080/marlo-web/api/helldots/comments/nope"
```
Expected: the object; an array containing it; `404` for the unknown id.

- [ ] **Step 5: Verify the unauthenticated case**

```bash
curl -s -o /dev/null -w "%{http_code}\n" "http://localhost:8080/marlo-web/api/helldots/comments?all=true"
```
Expected: `401` with no cookie.

- [ ] **Step 6: Clean up the fixture**

```bash
mysql -h localhost -u <user> -p aiccradb1 -e "DELETE FROM helldots_comments WHERE comment_id = 'test-1';"
```

- [ ] **Step 7: Checkstyle and commit**

```bash
mvn checkstyle:check -pl marlo-web
git add marlo-web/src/main/java/org/cgiar/ccafs/marlo/rest/helldots/HelldotsController.java
git commit -m ":sparkles: feat(api): Add HellDots comment read endpoints"
```

---

## Task 7: Event endpoint

**Requirements:** ENH-HELLDOTS-FN-006, FN-008, FN-009, NF-002, NF-003, AC-008, AC-009, AC-011
**Files:**
- Modify: `marlo-web/src/main/java/org/cgiar/ccafs/marlo/rest/helldots/HelldotsController.java`

**Interfaces:**
- Consumes: `HelldotsProjection.actionFor`, `stringField`, `commentIdOf`, `intField`, `dateField`, `pathOf`; `HelldotsCommentManager.findByCommentId`, `save`; `UserManager.getUser(Long)`; the `APConfig` and `isDisabled()` introduced in Task 6
- Produces: `POST /api/helldots/events`. Task 9's `onChange` handler posts to it.

- [ ] **Step 1: Add the constructor dependencies**

Add `UserManager` to the constructor, keeping `HelldotsCommentManager` and the `APConfig` that Task 6
introduced. The whole field block and constructor become:

```java
  private static final Logger LOG = LoggerFactory.getLogger(HelldotsController.class);
  private static final int MAX_PAYLOAD_CHARS = 200000;

  private final HelldotsCommentManager helldotsCommentManager;
  private final UserManager userManager;
  private final APConfig config;
  private final ObjectMapper objectMapper = new ObjectMapper();

  @Inject
  public HelldotsController(HelldotsCommentManager helldotsCommentManager, UserManager userManager,
    APConfig config) {
    this.helldotsCommentManager = helldotsCommentManager;
    this.userManager = userManager;
    this.config = config;
  }
```

No `GlobalUnitManager` is needed: the global unit is read off the session in Task 10, not looked up by id.

Add the imports: `org.cgiar.ccafs.marlo.data.manager.UserManager`, `org.cgiar.ccafs.marlo.data.model.User`,
`org.cgiar.ccafs.marlo.data.model.GlobalUnit`, `org.cgiar.ccafs.marlo.security.Permission`,
`org.springframework.web.bind.annotation.RequestBody`, `java.util.Date`, `java.util.Map`, `java.util.Set`.

- [ ] **Step 2: Add the current-user helper**

Mirrors `QAToken.getCurrentUser()`, which is the established way to reach the session user from a Spring MVC
controller in this codebase:

```java
  private User getCurrentUser() {
    Subject subject = SecurityUtils.getSubject();
    if (subject == null || subject.getPrincipal() == null) {
      return null;
    }
    Long principal = (Long) subject.getPrincipal();
    return userManager.getUser(principal);
  }

  private boolean isAdmin() {
    Subject subject = SecurityUtils.getSubject();
    return subject != null && subject.isPermitted(Permission.FULL_PRIVILEGES);
  }
```

`Permission.FULL_PRIVILEGES` is the same constant `BaseAction.canAccessSuperAdmin()` checks, reached directly
through Shiro because a Spring controller has no `BaseAction`.

- [ ] **Step 3: Add the event endpoint**

```java
  @RequestMapping(value = "/events", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE,
    produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<String> postEvent(@RequestBody Map<String, Object> event) {
    if (this.isDisabled()) {
      return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }
    User currentUser = this.getCurrentUser();
    if (currentUser == null) {
      return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
    }

    String eventType = HelldotsProjection.stringField(event, "type");
    HelldotsProjection.Action action = HelldotsProjection.actionFor(eventType);
    if (action == HelldotsProjection.Action.UNKNOWN) {
      LOG.warn("Rejected unknown HellDots event type: {}", eventType);
      return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    }

    if (action == HelldotsProjection.Action.SOFT_DELETE) {
      String deletedId = HelldotsProjection.stringField(event, "id");
      return this.softDelete(deletedId, currentUser);
    }

    @SuppressWarnings("unchecked")
    Map<String, Object> payload = (Map<String, Object>) event.get("comment");
    if (payload == null) {
      return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    }
    return this.upsert(payload, currentUser);
  }
```

The key names come from `ChangeEvent` in the published `dist/index.d.ts`, which is a discriminated union:
`{ type: "comment:deleted"; id: CommentId }` carries the id at the top level under `id`, while every other
variant carries the whole record under `comment`. That is why the delete branch reads `event.get("id")` and
the upsert branch reads `event.get("comment")`.

- [ ] **Step 4: Add the upsert**

```java
  private ResponseEntity<String> upsert(Map<String, Object> payload, User currentUser) {
    String commentId = HelldotsProjection.commentIdOf(payload);
    if (commentId == null || commentId.isEmpty()) {
      return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    }

    String serialized;
    try {
      serialized = objectMapper.writeValueAsString(payload);
    } catch (Exception e) {
      LOG.error("Could not serialize HellDots payload for {}", commentId, e);
      return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    }
    if (serialized.length() > MAX_PAYLOAD_CHARS) {
      LOG.warn("Rejected oversized HellDots payload for {} ({} chars)", commentId, serialized.length());
      return new ResponseEntity<>(HttpStatus.PAYLOAD_TOO_LARGE);
    }

    HelldotsComment comment = helldotsCommentManager.findByCommentId(commentId);
    boolean isNew = comment == null;
    if (isNew) {
      comment = new HelldotsComment();
      comment.setCommentId(commentId);
      comment.setActive(true);
      comment.setActiveSince(new Date());
      comment.setCreatedBy(currentUser);
      // NF-002: identity comes from the session, never from the payload.
      comment.setAuthorUser(currentUser);
      comment.setAuthorName(currentUser.getComposedName());
      comment.setGlobalUnit(this.getSessionGlobalUnit());
    } else if (!this.canMutate(comment, currentUser)) {
      // NF-003: only the author or an admin may change an existing comment.
      LOG.warn("User {} attempted to mutate comment {} owned by another user", currentUser.getId(), commentId);
      return new ResponseEntity<>(HttpStatus.FORBIDDEN);
    }

    comment.setPage(HelldotsProjection.pathOf(HelldotsProjection.stringField(payload, "page")));
    comment.setStatus(this.validated(HelldotsProjection.stringField(payload, "status"),
      HelldotsProjection.STATUSES, "open"));
    comment.setType(this.validated(HelldotsProjection.stringField(payload, "type"),
      HelldotsProjection.TYPES, null));
    comment.setPriority(this.validated(HelldotsProjection.stringField(payload, "priority"),
      HelldotsProjection.PRIORITIES, null));
    comment.setCreatedAt(this.orNow(HelldotsProjection.dateField(payload, "createdAt")));
    comment.setEditedAt(HelldotsProjection.dateField(payload, "editedAt"));
    comment.setResolvedAt(HelldotsProjection.dateField(payload, "resolvedAt"));
    comment.setSchemaVersion(HelldotsProjection.intField(payload, "schemaVersion"));
    comment.setPayload(serialized);
    comment.setModifiedBy(currentUser);

    helldotsCommentManager.save(comment);
    return new ResponseEntity<>(isNew ? HttpStatus.CREATED : HttpStatus.OK);
  }

  private boolean canMutate(HelldotsComment comment, User currentUser) {
    if (this.isAdmin()) {
      return true;
    }
    return comment.getAuthorUser() != null && comment.getAuthorUser().getId().equals(currentUser.getId());
  }

  private Date orNow(Date value) {
    return value == null ? new Date() : value;
  }

  private String validated(String value, Set<String> allowed, String fallback) {
    if (value != null && allowed.contains(value)) {
      return value;
    }
    return fallback;
  }
```

`MAX_PAYLOAD_CHARS` was added with the field block in Step 1. 200 000 characters is generous once screenshots
are URLs rather than base64, and still bounds a hostile request.

- [ ] **Step 5: Add the soft delete**

```java
  private ResponseEntity<String> softDelete(String commentId, User currentUser) {
    if (commentId == null || commentId.isEmpty()) {
      return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    }
    HelldotsComment comment = helldotsCommentManager.findByCommentId(commentId);
    if (comment == null) {
      return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }
    if (!this.canMutate(comment, currentUser)) {
      return new ResponseEntity<>(HttpStatus.FORBIDDEN);
    }
    comment.setActive(false);
    comment.setModifiedBy(currentUser);
    helldotsCommentManager.save(comment);
    return new ResponseEntity<>(HttpStatus.OK);
  }

  private GlobalUnit getSessionGlobalUnit() {
    // The session CRP is placed on the request by AddSessionToRestRequestFilter; a null here simply means
    // the comment was left outside a global-unit context, which is allowed by the schema.
    return null;
  }
```

Wire `getSessionGlobalUnit()` to the real session attribute (`APConstants.SESSION_CRP`) once Task 10 confirms
what that filter puts there for this request path. Leaving it null is correct behaviour, not a placeholder:
the column is nullable and FN-009 is satisfied as soon as the lookup is wired.

- [ ] **Step 6: Verify a create**

```bash
curl -s -o /dev/null -w "%{http_code}\n" -b "JSESSIONID=<id>" \
  -H "Content-Type: application/json" \
  -d '{"type":"comment:created","origin":"user","comment":{"id":"e2e-1","text":"hi","page":"/dashboard.do","status":"open","type":null,"priority":null,"tags":[],"replies":[],"createdAt":"2026-08-24T10:00:00.000Z","author":"Tester","schemaVersion":1}}' \
  http://localhost:8080/marlo-web/api/helldots/events
```
Expected: `201`. Then confirm the row:

```bash
mysql -h localhost -u <user> -p aiccradb1 -e \
"SELECT comment_id, page, status, author_user_id, author_name FROM helldots_comments WHERE comment_id='e2e-1';"
```
Expected: one row whose `author_user_id` is **your** user id, not anything from the payload.

- [ ] **Step 7: Verify the identity overwrite (AC-008)**

Repeat the POST with `"authorId":"999999","author":"Someone Else"` inside the comment object and a new id.
Expected: `201`, and the stored `author_user_id` is still your session user's id.

- [ ] **Step 8: Verify the soft delete (AC-011)**

```bash
curl -s -o /dev/null -w "%{http_code}\n" -b "JSESSIONID=<id>" -H "Content-Type: application/json" \
  -d '{"type":"comment:deleted","origin":"user","id":"e2e-1"}' \
  http://localhost:8080/marlo-web/api/helldots/events
mysql -h localhost -u <user> -p aiccradb1 -e "SELECT comment_id, is_active FROM helldots_comments WHERE comment_id='e2e-1';"
```
Expected: `200`, and the row present with `is_active = 0`.

- [ ] **Step 9: Verify the unknown-type rejection**

```bash
curl -s -o /dev/null -w "%{http_code}\n" -b "JSESSIONID=<id>" -H "Content-Type: application/json" \
  -d '{"type":"comment:exploded","origin":"user","comment":{"id":"x"}}' \
  http://localhost:8080/marlo-web/api/helldots/events
```
Expected: `400`.

- [ ] **Step 10: Clean up, checkstyle, commit**

```bash
mysql -h localhost -u <user> -p aiccradb1 -e "DELETE FROM helldots_comments WHERE comment_id LIKE 'e2e-%';"
mvn checkstyle:check -pl marlo-web
git add marlo-web/src/main/java/org/cgiar/ccafs/marlo/rest/helldots/HelldotsController.java
git commit -m ":sparkles: feat(api): Add HellDots event upsert endpoint"
```

---

## Task 8: Screenshot upload endpoint

**Requirements:** ENH-HELLDOTS-FN-007, NF-004, AC-007, AC-010
**Files:**
- Modify: `marlo-web/src/main/java/org/cgiar/ccafs/marlo/rest/helldots/HelldotsController.java`

**Interfaces:**
- Consumes: `HelldotsUploadValidator` (Task 4, including `MAX_SCREENSHOT_BYTES`), `HelldotsScreenshotManager` (Task 3), `APConfig.getUploadsBaseFolder()` and `APConfig.getDownloadURL()` (injected in Task 6)
- Produces: `POST /api/helldots/screenshots` returning `{"url":"…"}`. Task 9's `transformScreenshot` consumes it.

Blocked on pre-flight P1 — with the Windows uploads path in place, files are written where nothing can serve
them and this task cannot be verified.

- [ ] **Step 1: Add the dependencies**

Add `HelldotsScreenshotManager` as a third constructor argument, keeping `HelldotsCommentManager`,
`UserManager` and `APConfig`:

```java
  @Inject
  public HelldotsController(HelldotsCommentManager helldotsCommentManager, UserManager userManager,
    HelldotsScreenshotManager helldotsScreenshotManager, APConfig config) {
    this.helldotsCommentManager = helldotsCommentManager;
    this.userManager = userManager;
    this.helldotsScreenshotManager = helldotsScreenshotManager;
    this.config = config;
  }
```

Add imports:
`org.cgiar.ccafs.marlo.data.manager.HelldotsScreenshotManager`,
`org.cgiar.ccafs.marlo.data.model.HelldotsScreenshot`, `org.cgiar.ccafs.marlo.utils.APConfig`,
`org.springframework.web.multipart.MultipartFile`,
`org.springframework.web.bind.annotation.RequestPart`, `java.io.File`, `java.nio.file.Files`,
`java.nio.file.Path`, `java.nio.file.Paths`.

- [ ] **Step 2: Add the endpoint**

```java
  private static final String SCREENSHOT_FOLDER = "helldots";

  @RequestMapping(value = "/screenshots", method = RequestMethod.POST,
    consumes = MediaType.MULTIPART_FORM_DATA_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<String> postScreenshot(@RequestPart("file") MultipartFile file,
    @RequestParam(value = "kind", required = false, defaultValue = "context") String kind,
    @RequestParam(value = "commentId", required = false) String commentId) {
    if (this.isDisabled()) {
      return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }
    User currentUser = this.getCurrentUser();
    if (currentUser == null) {
      return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
    }
    if (file == null || file.isEmpty()) {
      return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    }
    if (!HelldotsUploadValidator.isAllowedContentType(file.getContentType())) {
      LOG.warn("Rejected HellDots upload with content type {}", file.getContentType());
      return new ResponseEntity<>(HttpStatus.UNSUPPORTED_MEDIA_TYPE);
    }
    if (!HelldotsUploadValidator.isWithinSize(file.getSize(), HelldotsUploadValidator.MAX_SCREENSHOT_BYTES)) {
      LOG.warn("Rejected HellDots upload of {} bytes", file.getSize());
      return new ResponseEntity<>(HttpStatus.PAYLOAD_TOO_LARGE);
    }

    // NF-004: the stored name is generated here; nothing from the client reaches the path.
    String fileName = HelldotsUploadValidator.generateFileName(file.getContentType());
    String relativePath = SCREENSHOT_FOLDER + File.separator + fileName;

    try {
      Path folder = Paths.get(config.getUploadsBaseFolder(), SCREENSHOT_FOLDER);
      Files.createDirectories(folder);
      file.transferTo(folder.resolve(fileName).toFile());
    } catch (Exception e) {
      LOG.error("Could not store HellDots screenshot {}", fileName, e);
      return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
    }

    HelldotsScreenshot screenshot = new HelldotsScreenshot();
    screenshot.setCommentId(commentId);
    screenshot.setKind(kind);
    screenshot.setFileName(fileName);
    screenshot.setRelativePath(relativePath);
    screenshot.setContentType(file.getContentType());
    screenshot.setByteSize(Long.valueOf(file.getSize()));
    screenshot.setActive(true);
    screenshot.setActiveSince(new Date());
    screenshot.setCreatedBy(currentUser);
    helldotsScreenshotManager.save(screenshot);

    String url = config.getDownloadURL() + "/" + SCREENSHOT_FOLDER + "/" + fileName;
    return new ResponseEntity<>("{\"url\":\"" + url + "\"}", HttpStatus.OK);
  }
```

`config.getUploadsBaseFolder()` and `config.getDownloadURL()` both exist on `APConfig`. The size cap comes from
`HelldotsUploadValidator.MAX_SCREENSHOT_BYTES` rather than from `file.maxSizeAllowed.bytes`: no Java in this
repository reads that property, so there is no getter to call, and a 5 MB screenshot cap is the right bound
anyway.

- [ ] **Step 3: Register a multipart resolver if one is absent**

Check `MarloRestApiConfig` for a `MultipartResolver` bean. If there is none, add:

```java
  @Bean(name = "multipartResolver")
  public org.springframework.web.multipart.commons.CommonsMultipartResolver multipartResolver() {
    org.springframework.web.multipart.commons.CommonsMultipartResolver resolver =
      new org.springframework.web.multipart.commons.CommonsMultipartResolver();
    resolver.setMaxUploadSize(20971520L);
    return resolver;
  }
```

The bean name must be exactly `multipartResolver` — Spring MVC looks it up by name.

- [ ] **Step 4: Verify a good upload**

```bash
curl -s -b "JSESSIONID=<id>" -F "file=@/path/to/small.jpg;type=image/jpeg" -F "kind=context" \
  http://localhost:8080/marlo-web/api/helldots/screenshots
```
Expected: `{"url":"http://localhost:8080/marlo-web/data/helldots/helldots-<uuid>.jpg"}`

- [ ] **Step 5: Verify the URL actually serves the file**

```bash
curl -s -o /dev/null -w "%{http_code}\n" "<the url returned above>"
```
Expected: `200`. A `404` means pre-flight P1 was not completed — the uploads folder and the downloads URL do
not point at the same place.

- [ ] **Step 6: Verify the rejections (AC-010)**

```bash
curl -s -o /dev/null -w "%{http_code}\n" -b "JSESSIONID=<id>" \
  -F "file=@/path/to/doc.pdf;type=application/pdf" http://localhost:8080/marlo-web/api/helldots/screenshots
```
Expected: `415`. Then repeat with a file larger than 5 MB and expect `413`.

- [ ] **Step 7: Checkstyle and commit**

```bash
mvn checkstyle:check -pl marlo-web
git add marlo-web/src/main/java/org/cgiar/ccafs/marlo/rest/helldots/HelldotsController.java
git commit -m ":sparkles: feat(api): Add HellDots screenshot upload endpoint"
```

---

## Task 9: Frontend — vendor, adapter, mount, fonts

**Requirements:** ENH-HELLDOTS-FN-001..007, NF-001, NF-006, AC-001..AC-003, AC-006
**Files:**
- Create: `marlo-web/src/main/webapp/global/js/vendor/helldots-0.7.0.umd.js`
- Create: `marlo-web/src/main/webapp/global/js/helldots-init.js`
- Modify: `marlo-web/src/main/webapp/WEB-INF/global/pages/footer.ftl`
- Modify: `marlo-web/src/main/webapp/WEB-INF/global/pages/header.ftl:41`

**Interfaces:**
- Consumes: all five endpoints from Tasks 6–8
- Produces: `window.marloHelldots` — the overlay instance, for `notifyNavigation()` after AJAX DOM rebuilds

- [ ] **Step 1: Vendor the bundle**

```bash
cd /tmp
curl -sL "https://registry.npmjs.org/helldots/-/helldots-0.7.0.tgz" -o helldots.tgz
tar xzf helldots.tgz
cp package/dist/helldots.umd.js \
  /Users/kevincollazos/Code/cgiar/MARLO/marlo-web/src/main/webapp/global/js/vendor/helldots-0.7.0.umd.js
```

- [ ] **Step 2: Verify the vendored file**

```bash
head -c 200 marlo-web/src/main/webapp/global/js/vendor/helldots-0.7.0.umd.js
wc -c < marlo-web/src/main/webapp/global/js/vendor/helldots-0.7.0.umd.js
```
Expected: the MIT banner, `var HellDots=` shortly after, and 180400 bytes.

- [ ] **Step 3: Write `helldots-init.js`**

```js
/**
 * HellDots adapter for MARLO. Mounted only for authenticated users outside production; see footer.ftl.
 */
(function () {
  "use strict";

  var config = document.getElementById("helldots-config");
  if (!config || !window.HellDots) {
    return;
  }

  var api = config.dataset.baseUrl.replace(/\/+$/, "") + "/api/helldots";

  function request(method, url, body) {
    return fetch(url, {
      method: method,
      credentials: "same-origin",
      headers: body ? { "Content-Type": "application/json" } : {},
      body: body ? JSON.stringify(body) : undefined
    }).then(function (response) {
      if (!response.ok) {
        throw new Error(method + " " + url + " -> " + response.status);
      }
      return response.status === 204 ? null : response.json();
    });
  }

  var overlay = window.HellDots.createCommentOverlay({
    user: { name: config.dataset.userName, id: config.dataset.userId },
    locale: "en",

    transformScreenshot: function (dataUrl, info) {
      return fetch(dataUrl)
        .then(function (response) { return response.blob(); })
        .then(function (blob) {
          var form = new FormData();
          form.append("file", blob);
          form.append("kind", info.kind);
          if (info.commentId) {
            form.append("commentId", info.commentId);
          }
          return fetch(api + "/screenshots", {
            method: "POST",
            credentials: "same-origin",
            body: form
          });
        })
        .then(function (response) {
          if (!response.ok) {
            throw new Error("upload failed: " + response.status);
          }
          return response.json();
        })
        .then(function (result) { return result.url; });
    },

    onReady: function (instance) {
      request("GET", api + "/comments?page=" + encodeURIComponent(window.location.pathname))
        .then(function (comments) {
          instance.loadComments(comments || []);
        })
        .catch(function (error) {
          console.warn("[helldots] could not load comments", error);
        });
    },

    onCommentRequested: function (id) {
      return request("GET", api + "/comments/" + encodeURIComponent(id)).then(function (comment) {
        if (comment) {
          overlay.loadComments([comment]);
        }
      });
    },

    onChange: function (event) {
      // Our own writes are echoed back as origin "host"; forwarding them would loop forever.
      if (event.origin === "host") {
        return;
      }
      request("POST", api + "/events", event).catch(function (error) {
        console.warn("[helldots] could not persist event", event.type, error);
      });
    },

    onError: function (error, context) {
      console.warn("[helldots]", context, error);
    }
  });

  // Exposed so AJAX sections can re-anchor after rebuilding their DOM.
  window.marloHelldots = overlay;
})();
```

`transformScreenshot` is fail-open by contract: a rejected upload keeps the data URL and surfaces
`onError(error, "transform")`, so a failed upload degrades the record instead of losing the comment.

- [ ] **Step 4: Add the mount block to `footer.ftl`**

Immediately before `[/#compress]`, add:

```ftl
    [#-- HellDots comment overlay: authenticated users, non-production only --]
    [#if (currentUser??)!false && !config.production]
      <div id="helldots-config"
           data-user-id="${currentUser.id?c}"
           data-user-name="${(currentUser.composedName)!'Unknown'}"
           data-base-url="${baseUrl}"></div>
      <script defer src="${baseUrlCdn}/global/js/vendor/helldots-0.7.0.umd.js"></script>
      <script defer src="${baseUrlCdn}/global/js/helldots-init.js?20260824"></script>
    [/#if]
```

Identity goes in `data-*` attributes, never interpolated into a `<script>` body: FreeMarker auto-escaping is
on under Struts 6.8, so `${...}` inside JavaScript would turn `O'Brien` into `O&#39;Brien` and break the
literal. Do not add `?html` anywhere — it is a parse error in this version.

- [ ] **Step 5: Add `crossorigin` to the fonts link**

In `header.ftl:41`, change:

```ftl
    <link rel="stylesheet" href="https://fonts.googleapis.com/css2?family=Figtree:wght@400;500;600;700&display=swap" />
```

to:

```ftl
    <link rel="stylesheet" crossorigin href="https://fonts.googleapis.com/css2?family=Figtree:wght@400;500;600;700&display=swap" />
```

Without it, reading `cssRules` on that stylesheet throws `SecurityError`, the `@font-face` never reaches the
screenshot renderer, and captured text comes out in a fallback face whose metrics differ — a drag-crop tight
around a few words can return the wrong ones.

- [ ] **Step 6: Verify the mount, signed out (AC-001)**

Restart, open `http://localhost:8080/marlo-web/` signed out, and view source.
Expected: no `helldots-config` element and no `helldots` script tag.

- [ ] **Step 7: Verify the mount, signed in (AC-001)**

Sign in. Expected: the HellDots toolbar appears at the bottom of the page. `Alt`+`C` toggles comment mode.

- [ ] **Step 8: Verify the production gate (AC-002)**

Set `marlo.production=true` in `marlo-dev.properties`, restart, sign in.
Expected: no HellDots asset requested, no `#helldots-config` in the DOM.
**Then set it back to `false` and restart.**

- [ ] **Step 9: Verify the echo guard (AC-006)**

Leave a comment. Open DevTools, Network tab, then in the console:

```js
marloHelldots.setCommentStatus(marloHelldots.comments[0].id, "resolved")
```
Expected: the status changes in the UI and **no** POST to `/events` appears in the Network tab.

- [ ] **Step 10: Verify the apostrophe case (AC-003)**

Sign in as a user whose display name contains an apostrophe, or temporarily edit one to `O'Brien`.
Expected: the name renders correctly on a new comment and the console shows no JavaScript error.

- [ ] **Step 11: Commit**

```bash
git add marlo-web/src/main/webapp/global/js/vendor/helldots-0.7.0.umd.js \
        marlo-web/src/main/webapp/global/js/helldots-init.js \
        marlo-web/src/main/webapp/WEB-INF/global/pages/footer.ftl \
        marlo-web/src/main/webapp/WEB-INF/global/pages/header.ftl
git commit -m ":sparkles: feat(ui): Mount the HellDots overlay for signed-in non-production users"
```

---

## Task 10: End-to-end verification and spec sign-off

**Requirements:** all twelve acceptance criteria, ENH-HELLDOTS-FN-009, risk R-001
**Files:**
- Modify: `docs/specs/enhancement/helldots-overlay/task.md` — the results table in §5
- Modify: `marlo-web/src/main/java/org/cgiar/ccafs/marlo/rest/helldots/HelldotsController.java` — wire `getSessionGlobalUnit()`

- [ ] **Step 1: Wire the global unit (FN-009)**

Replace the `getSessionGlobalUnit()` stub. The session CRP lives under `APConstants.SESSION_CRP`, which
`AddSessionToRestRequestFilter` populates for `/api/*` requests. Read it from the current request:

```java
  private GlobalUnit getSessionGlobalUnit() {
    ServletRequestAttributes attributes =
      (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
    if (attributes == null) {
      return null;
    }
    HttpSession session = attributes.getRequest().getSession(false);
    if (session == null) {
      return null;
    }
    Object crp = session.getAttribute(APConstants.SESSION_CRP);
    if (crp instanceof GlobalUnit) {
      return (GlobalUnit) crp;
    }
    return null;
  }
```

Imports: `javax.servlet.http.HttpSession`, `org.springframework.web.context.request.RequestContextHolder`,
`org.springframework.web.context.request.ServletRequestAttributes`,
`org.cgiar.ccafs.marlo.config.APConstants`.

**Verify:** leave a new comment through the widget, then:

```bash
mysql -h localhost -u <user> -p aiccradb1 -e \
"SELECT comment_id, global_unit_id FROM helldots_comments ORDER BY id DESC LIMIT 1;"
```
Expected: `global_unit_id` matches the CRP you were signed into.

- [ ] **Step 2: Full acceptance pass**

Walk AC-001 through AC-012 as written in `requirements.md` §6. Several were already verified in earlier tasks;
re-run them here against the assembled system rather than trusting the earlier result:

| Criterion | Verified in | Re-check |
|---|---|---|
| AC-001 mount gate | Task 9 S6–S7 | signed out and signed in |
| AC-002 production gate | Task 9 S8 | flag flipped and restored |
| AC-003 apostrophe name | Task 9 S10 | no console error |
| AC-004 per-page load, 0 orphaned | here | leave 3 comments, reload, `loadComments` reports `orphaned: 0` |
| AC-005 cross-page deep link | here | copy a link from page A, open it on page B |
| AC-006 echo guard | Task 9 S9 | no POST on a host-driven change |
| AC-007 screenshot as URL | Task 8 S4–S5 | `payload` holds a URL, no `data:` string |
| AC-008 forged authorId | Task 7 S7 | session identity wins |
| AC-009 cross-user edit | here | second user, non-admin, gets 403 |
| AC-010 bad upload | Task 8 S6 | 415 and 413, comment survives |
| AC-011 soft delete | Task 7 S8 | `is_active = 0` |
| AC-012 migration | Task 1 S3–S4 | tables exist, Flyway success |

For AC-007 specifically:

```bash
mysql -h localhost -u <user> -p aiccradb1 -e \
"SELECT comment_id, payload NOT LIKE '%data:image%' AS no_base64 FROM helldots_comments ORDER BY id DESC LIMIT 3;"
```
Expected: `no_base64 = 1` for every row.

- [ ] **Step 3: Anchor durability spot-check**

Leave comments on a page containing an expandable block. Expand it, collapse it, then run
`marloHelldots.notifyNavigation()` in the console. Reload the page.
Expected: comments inside the rebuilt subtree re-anchor; ones whose element is genuinely gone are reported
orphaned, not dropped. Check the `loadComments()` return value against what is on screen.

- [ ] **Step 4: Verify the endpoint production guard (NF-005)**

The mount gate was checked in Task 9. This checks the second, independent gate — that the endpoints themselves
refuse, not merely that the widget is absent.

Set `marlo.production=true` in `marlo-dev.properties`, restart, sign in, then:

```bash
curl -s -o /dev/null -w "%{http_code}\n" -b "JSESSIONID=<id>" \
  "http://localhost:8080/marlo-web/api/helldots/comments?all=true"
```
Expected: `404` — either because the dispatcher is unregistered in production, or because `isDisabled()`
refused. Both are correct; the guard exists so neither is load-bearing alone.

**Then set it back to `false` and restart.**

- [ ] **Step 5: Regression sweep (risk R-001)**

The `/api/*` mapping is the only change reaching shared infrastructure. Check:
- Four or five `.do` pages across different modules render and save as before.
- An existing file upload (project highlight or deliverable) still works.
- `/swagger/` still responds.
- A page using `${baseUrlCdn}` assets still loads its CSS and JS.

Record anything unexpected against R-001 in `design.md` §16 rather than fixing it silently.

- [ ] **Step 6: Record the results in the spec**

Update the results table in `docs/specs/enhancement/helldots-overlay/task.md` §5, replacing each `pending`
with the observation that proves it. Set the spec `Status:` fields from `Draft` to `Implemented` in all three
files only if every criterion passed.

- [ ] **Step 7: Final gates**

```bash
mvn clean install -DskipTests -pl marlo-web -am
mvn test -pl marlo-web -Dtest='Helldots*Test'
mvn checkstyle:check
git status --short
```
Expected: BUILD SUCCESS; 12 tests pass; no checkstyle violations; `git status` shows no
`marlo-dev.properties`, no `tomcat/context.xml`, and no `C:` directory.

- [ ] **Step 8: Commit**

```bash
git add docs/specs/enhancement/helldots-overlay marlo-web/src/main/java/org/cgiar/ccafs/marlo/rest/helldots/HelldotsController.java
git commit -m ":white_check_mark: test(qa): Verify HellDots overlay against acceptance criteria"
```

---

## Task Dependency Graph

```
P1 (uploads path) ────────────────────────┐
P2 (branch) ─┐                            │
P3 (baseline)┴─ T1 (migration)            │
                 └─ T2 (entities+hbm)     │
                     └─ T3 (dao+manager) ─┼─ T6 (read)  ─┐
                         └─ T4 (pure+tests)              ├─ T9 (frontend) ─ T10 (e2e)
P2 ─ T5 (/api/* mapping) ─────────────────┼─ T7 (events)─┤
                                          └─ T8 (upload)─┘
```

T5 gates T6, T7 and T8: without the `/api/*` mapping they return 404 no matter how correct they are.
P1 gates T8 for the same reason on the storage side. T4 has no dependencies at all and can run first or in
parallel — it is pure functions and their tests.

---

## Notes for the implementer

**What is genuinely testable here.** This repo has three test files and no Mockito. Task 4 is the only place
with real unit tests, which is why the projection and validation logic was pulled out of the controller into
pure static functions. Everything else is verified by running the app and observing it — the `curl` and
`mysql` commands in each task are the test suite for that task. Run them; do not assume.

**The two things most likely to go wrong:**
1. **Forgetting the `<mapping resource=…/>` lines** in `hibernate.cfg.xml` (Task 2, Step 5). With
   `hbm2ddl.auto=validate`, a mapping mismatch or a missing registration stops the app at startup with an
   error that does not obviously point at HellDots.
2. **The uploads path** (pre-flight P1). Uploads appear to succeed and the files are simply unreachable.

**Where to check the library's real contract.** `dist/index.d.ts` in the published tarball is authoritative for
the `SerializedComment` and `ChangeEvent` shapes. Where this plan and that file disagree, the file wins — read
it rather than guessing at a field name.
