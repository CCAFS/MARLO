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

package org.cgiar.ccafs.marlo.rest.helldots;

import org.cgiar.ccafs.marlo.config.APConstants;
import org.cgiar.ccafs.marlo.data.manager.HelldotsCommentManager;
import org.cgiar.ccafs.marlo.data.manager.HelldotsScreenshotManager;
import org.cgiar.ccafs.marlo.data.manager.UserManager;
import org.cgiar.ccafs.marlo.data.model.GlobalUnit;
import org.cgiar.ccafs.marlo.data.model.HelldotsComment;
import org.cgiar.ccafs.marlo.data.model.HelldotsScreenshot;
import org.cgiar.ccafs.marlo.data.model.User;
import org.cgiar.ccafs.marlo.security.Permission;
import org.cgiar.ccafs.marlo.utils.APConfig;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.inject.Inject;
import javax.servlet.http.HttpSession;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.subject.Subject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/helldots")
public class HelldotsController {

  private static final Logger LOG = LoggerFactory.getLogger(HelldotsController.class);
  private static final int MAX_PAYLOAD_CHARS = 200000;
  private static final String SCREENSHOT_FOLDER = "helldots";
  private static final int MAX_COMMENT_ID_LENGTH = 64;

  /** The only two kinds the widget emits: an automatic capture, or something the user deliberately attached. */
  private static final Set<String> SCREENSHOT_KINDS =
    Collections.unmodifiableSet(new HashSet<>(Arrays.asList("context", "attachment")));

  private final HelldotsCommentManager helldotsCommentManager;
  private final UserManager userManager;
  private final HelldotsScreenshotManager helldotsScreenshotManager;
  private final APConfig config;
  private final ObjectMapper objectMapper = new ObjectMapper();

  @Inject
  public HelldotsController(HelldotsCommentManager helldotsCommentManager, UserManager userManager,
    HelldotsScreenshotManager helldotsScreenshotManager, APConfig config) {
    this.helldotsCommentManager = helldotsCommentManager;
    this.userManager = userManager;
    this.helldotsScreenshotManager = helldotsScreenshotManager;
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
    // commentId is a nanoid; anything longer than the column is malformed rather than truncatable.
    if (commentId != null && commentId.length() > MAX_COMMENT_ID_LENGTH) {
      LOG.warn("Rejected HellDots upload with oversized commentId ({} chars)", commentId.length());
      return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    }

    // NF-004: the stored name is generated here; nothing from the client reaches the path.
    String fileName = HelldotsUploadValidator.generateFileName(file.getContentType());
    String relativePath = SCREENSHOT_FOLDER + File.separator + fileName;
    // Column-safe: isAllowedContentType already required the raw header to normalise to one of these two
    // values, so re-deriving from the extension it just produced yields the same value without persisting
    // the unbounded raw header (which could carry trailing parameters past the column width).
    String normalizedContentType = fileName.endsWith(".png") ? MediaType.IMAGE_PNG_VALUE : MediaType.IMAGE_JPEG_VALUE;
    String validatedKind = this.validated(kind, SCREENSHOT_KINDS, "context");

    Path folder = Paths.get(config.getUploadsBaseFolder(), SCREENSHOT_FOLDER);
    Path storedFile = folder.resolve(fileName);
    try {
      Files.createDirectories(folder);
      file.transferTo(storedFile.toFile());
    } catch (Exception e) {
      LOG.error("Could not store HellDots screenshot {}", fileName, e);
      return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
    }

    HelldotsScreenshot screenshot = new HelldotsScreenshot();
    screenshot.setCommentId(commentId);
    screenshot.setKind(validatedKind);
    screenshot.setFileName(fileName);
    screenshot.setRelativePath(relativePath);
    screenshot.setContentType(normalizedContentType);
    screenshot.setByteSize(Long.valueOf(file.getSize()));
    screenshot.setActive(true);
    screenshot.setActiveSince(new Date());
    screenshot.setCreatedBy(currentUser);
    try {
      helldotsScreenshotManager.save(screenshot);
    } catch (Exception e) {
      LOG.error("Could not save HellDots screenshot record for {}; deleting orphaned file", fileName, e);
      try {
        Files.deleteIfExists(storedFile);
      } catch (Exception deleteError) {
        LOG.error("Could not delete orphaned HellDots screenshot file {}", fileName, deleteError);
      }
      return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
    }

    // Served by the GET below, not as a static file: nothing maps a URL onto the uploads directory,
    // and keeping it outside the webroot means nothing writable is web-served.
    String url = config.getBaseUrl() + "/api/helldots/screenshots/" + fileName;
    return new ResponseEntity<>("{\"url\":\"" + url + "\"}", HttpStatus.OK);
  }

  @RequestMapping(value = "/screenshots/{fileName:.+}", method = RequestMethod.GET)
  public ResponseEntity<byte[]> getScreenshot(@PathVariable("fileName") String fileName) {
    if (this.isDisabled()) {
      return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }
    if (!this.isAuthenticated()) {
      return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
    }
    // Only names this application generated are servable, so no traversal or arbitrary read is reachable.
    if (!HelldotsUploadValidator.isGeneratedFileName(fileName)) {
      return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    }

    Path file = Paths.get(config.getUploadsBaseFolder(), SCREENSHOT_FOLDER, fileName);
    if (!Files.isRegularFile(file)) {
      return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }
    try {
      byte[] bytes = Files.readAllBytes(file);
      HttpHeaders headers = new HttpHeaders();
      headers.setContentType(fileName.endsWith(".png") ? MediaType.IMAGE_PNG : MediaType.IMAGE_JPEG);
      headers.setCacheControl("private, max-age=86400");
      return new ResponseEntity<>(bytes, headers, HttpStatus.OK);
    } catch (Exception e) {
      LOG.error("Could not read HellDots screenshot {}", fileName, e);
      return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
    }
  }

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

    Object rawComment = event.get("comment");
    if (!(rawComment instanceof Map)) {
      return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    }
    @SuppressWarnings("unchecked")
    Map<String, Object> payload = (Map<String, Object>) rawComment;
    return this.upsert(payload, currentUser);
  }

  private boolean canMutate(HelldotsComment comment, User currentUser) {
    if (this.isAdmin()) {
      return true;
    }
    return comment.getAuthorUser() != null && comment.getAuthorUser().getId().equals(currentUser.getId());
  }

  private User getCurrentUser() {
    Subject subject = SecurityUtils.getSubject();
    if (subject == null || subject.getPrincipal() == null) {
      return null;
    }
    Long principal = (Long) subject.getPrincipal();
    return userManager.getUser(principal);
  }

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

  private boolean isAdmin() {
    Subject subject = SecurityUtils.getSubject();
    return subject != null && subject.isPermitted(Permission.FULL_PRIVILEGES);
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

  private Date orNow(Date value) {
    return value == null ? new Date() : value;
  }

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

  private String validated(String value, Set<String> allowed, String fallback) {
    if (value != null && allowed.contains(value)) {
      return value;
    }
    return fallback;
  }
}
