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
import org.cgiar.ccafs.marlo.data.manager.UserManager;
import org.cgiar.ccafs.marlo.data.model.GlobalUnit;
import org.cgiar.ccafs.marlo.data.model.HelldotsComment;
import org.cgiar.ccafs.marlo.data.model.User;
import org.cgiar.ccafs.marlo.security.Permission;
import org.cgiar.ccafs.marlo.utils.APConfig;

import java.util.ArrayList;
import java.util.Date;
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
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@RestController
@RequestMapping("/helldots")
public class HelldotsController {

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
