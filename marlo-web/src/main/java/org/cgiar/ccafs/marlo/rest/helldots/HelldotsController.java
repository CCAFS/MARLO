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
