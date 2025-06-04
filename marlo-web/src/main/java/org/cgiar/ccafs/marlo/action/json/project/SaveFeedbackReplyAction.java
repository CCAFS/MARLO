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
import org.cgiar.ccafs.marlo.data.manager.PhaseManager;
import org.cgiar.ccafs.marlo.data.manager.UserManager;
import org.cgiar.ccafs.marlo.data.model.FeedbackQAReply;
import org.cgiar.ccafs.marlo.data.model.Phase;
import org.cgiar.ccafs.marlo.data.model.User;
import org.cgiar.ccafs.marlo.utils.APConfig;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;
import org.apache.struts2.dispatcher.Parameter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SaveFeedbackReplyAction extends BaseAction {

  /**
   * 
   */
  private static final long serialVersionUID = -4335064142194555431L;
  private final Logger logger = LoggerFactory.getLogger(SaveFeedbackReplyAction.class);
  private Map<String, Object> save;
  private Long phaseId;
  private Long replyId;
  private String reply;
  private Long userId;
  private Date date;
  private Long commentId;
  private FeedbackQAReplyManager commentReplyManager;
  private FeedbackQACommentManager commentQAManager;
  private UserManager userManager;
  private PhaseManager phaseManager;

  @Inject
  public SaveFeedbackReplyAction(APConfig config, FeedbackQACommentManager commentQAManager,
    FeedbackQAReplyManager commentReplyManager, UserManager userManager, PhaseManager phaseManager) {
    super(config);
    this.commentReplyManager = commentReplyManager;
    this.userManager = userManager;
    this.commentQAManager = commentQAManager;
    this.phaseManager = phaseManager;
  }

  @Override
  public String execute() throws Exception {
    // @param = reply/commentID/userID/phaseID
    // @param (optional) = replyID

    save = new HashMap<>();
    if (reply != null && commentId != null) {

      // Create feedback Comment save object
      FeedbackQAReply feedbackReply = new FeedbackQAReply();

      // get existing object from database
      try {
        if (replyId != null) {
          FeedbackQAReply replyDB = commentReplyManager.getFeedbackCommentById(replyId);
          if (replyDB != null && replyDB.getId() != null) {
            feedbackReply = replyDB;
          }
        }
      } catch (Exception e) {
        logger.error("unable to get existing Feedback comment object from DB", e);
      }

      feedbackReply.setComment(reply);

      if (userId != null) {
        try {
          User user = userManager.getUser(userId);
          if (user != null) {
            feedbackReply.setUser(this.getCurrentUser());
          }
        } catch (Exception e) {
          logger.error("unable to set User object", e);
        }
      }

      date = new Date();
      feedbackReply.setCommentDate(date);

      try {
        feedbackReply.setFeedbackComment(commentQAManager.getFeedbackQACommentById(commentId));
      } catch (Exception e) {
        logger.error("unable to set FeedbackQAComment object", e);
      }

      Phase phase = null;
      if (phaseId != null) {
        phase = phaseManager.getPhaseById(phaseId);
        feedbackReply.setPhase(phase);
      } else {
        try {
          phase = phaseManager.getPhaseById(feedbackReply.getFeedbackComment().getPhase().getId());
          feedbackReply.setPhase(phase);
        } catch (Exception e) {
          logger.error("unable to set Phase object", e);
        }
      }

      feedbackReply = commentReplyManager.saveFeedbackComment(feedbackReply);

      if (feedbackReply.getId() != null) {
        save.put("save", true);
        save.put("id", feedbackReply.getId());
      } else {
        save.put("save", false);
      }
    } else {
      save.put("save", false);
    }
    return SUCCESS;
  }

  public Map<String, Object> getSave() {
    return save;
  }

  @Override
  public void prepare() throws Exception {
    Map<String, Parameter> parameters = this.getParameters();

    try {
      if (parameters.get(APConstants.REPLY_ID_REQUEST).isDefined()) {
        replyId = Long.parseLong(
          StringUtils.trim(StringUtils.trim(parameters.get(APConstants.REPLY_ID_REQUEST).getMultipleValues()[0])));
      }
    } catch (Exception e) {
      logger.error("unable to get replyID", e);
    }
    try {
      if (parameters.get(APConstants.COMMENT_REQUEST_ID).isDefined()) {
        commentId = Long.parseLong(
          StringUtils.trim(StringUtils.trim(parameters.get(APConstants.COMMENT_REQUEST_ID).getMultipleValues()[0])));
      }
    } catch (Exception e) {
      logger.error("unable to get replyID", e);
    }
    try {
      if (parameters.get(APConstants.COMMENT_REPLY).isDefined()) {
        reply = StringUtils.trim(StringUtils.trim(parameters.get(APConstants.COMMENT_REPLY).getMultipleValues()[0]));
      }
    } catch (Exception e) {
      logger.error("unable to get comment", e);
    }
    try {
      if (parameters.get(APConstants.COMMENT_USER_ID).isDefined()) {
        userId = Long.parseLong(
          StringUtils.trim(StringUtils.trim(parameters.get(APConstants.COMMENT_USER_ID).getMultipleValues()[0])));
      }
    } catch (Exception e) {
      logger.error("unable to get user", e);
    }
    try {
      if (parameters.get(APConstants.PHASE_ID).isDefined()) {
        phaseId = Long
          .parseLong(StringUtils.trim(StringUtils.trim(parameters.get(APConstants.PHASE_ID).getMultipleValues()[0])));
      }
    } catch (Exception e) {
      logger.error("unable to get phaseID", e);
    }
  }

  public void setSave(Map<String, Object> save) {
    this.save = save;
  }

}
