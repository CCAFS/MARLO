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


package org.cgiar.ccafs.marlo.action.json.project;

import org.cgiar.ccafs.marlo.action.BaseAction;
import org.cgiar.ccafs.marlo.config.APConstants;
import org.cgiar.ccafs.marlo.data.manager.FeedbackQACommentManager;
import org.cgiar.ccafs.marlo.data.manager.FeedbackQAReplyManager;
import org.cgiar.ccafs.marlo.data.model.FeedbackQAComment;
import org.cgiar.ccafs.marlo.data.model.FeedbackQAReply;
import org.cgiar.ccafs.marlo.utils.APConfig;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;
import org.apache.struts2.dispatcher.Parameter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FeedbackQARepliesAction extends BaseAction {

  /**
   * 
   */
  private static final long serialVersionUID = -4335064142194555431L;
  private final Logger logger = LoggerFactory.getLogger(FeedbackQARepliesAction.class);
  private List<Map<String, Object>> comments;
  private Long parentId;
  private String sectionName;
  private Long phaseId;
  private Long commentId;
  private FeedbackQACommentManager commentManager;
  private FeedbackQAReplyManager replyManager;


  @Inject
  public FeedbackQARepliesAction(APConfig config, FeedbackQACommentManager commentManager,
    FeedbackQAReplyManager replyManager) {
    super(config);
    this.commentManager = commentManager;
  }

  @Override
  public String execute() throws Exception {
    comments = new ArrayList<Map<String, Object>>();
    Map<String, Object> fieldsMap;
    FeedbackQAReply reply = new FeedbackQAReply();
    // @param = commentID
    if (commentId != null) {
      try {
        FeedbackQAComment comment = commentManager.getFeedbackQACommentById(commentId);
        if (comment != null && comment.getReply() != null && comment.getReply().getId() != null) {
          reply = replyManager.getFeedbackCommentById(comment.getReply().getId());
        }
      } catch (Exception e) {
        logger.error("unable to get feedback replies", e);
      }
    }

    if (reply != null) {

      fieldsMap = new HashMap<String, Object>();
      if (reply.getComment() != null) {
        fieldsMap.put("reply", reply.getComment());
      } else {
        fieldsMap.put("reply", "");
      }
      if (reply.getId() != null) {
        fieldsMap.put("id", reply.getId());
      } else {
        fieldsMap.put("id", "");
      }
      if (reply.getUser() != null && reply.getUser().getFirstName() != null) {
        fieldsMap.put("userName", reply.getUser().getFirstName() + " " + reply.getUser().getLastName());
      } else {
        fieldsMap.put("userName", "");
      }
      if (reply.getCommentDate() != null && reply.getCommentDate().toString() != null) {
        String dateString = reply.getCommentDate().toString();
        fieldsMap.put("date", dateString);
      } else {
        fieldsMap.put("date", "");
      }
      this.comments.add(fieldsMap);

    } else {
      fieldsMap = Collections.emptyMap();
    }


    return SUCCESS;
  }

  public List<Map<String, Object>> getComments() {
    return comments;
  }

  @Override
  public void prepare() throws Exception {
    Map<String, Parameter> parameters = this.getParameters();
    if (parameters.get(APConstants.PARENT_REQUEST_ID).isDefined()) {
      parentId = Long.parseLong(
        StringUtils.trim(StringUtils.trim(parameters.get(APConstants.PARENT_REQUEST_ID).getMultipleValues()[0])));
    }
    if (parameters.get(APConstants.PHASE_ID).isDefined()) {
      phaseId =
        Long.parseLong(StringUtils.trim(StringUtils.trim(parameters.get(APConstants.PHASE_ID).getMultipleValues()[0])));
    }
    if (parameters.get(APConstants.COMMENT_REQUEST_ID).isDefined()) {
      commentId = Long.parseLong(
        StringUtils.trim(StringUtils.trim(parameters.get(APConstants.COMMENT_REQUEST_ID).getMultipleValues()[0])));
    }
  }

  public void setComments(List<Map<String, Object>> comments) {
    this.comments = comments;
  }

}
