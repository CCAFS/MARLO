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
import org.cgiar.ccafs.marlo.data.manager.FeedbackCommentManager;
import org.cgiar.ccafs.marlo.data.manager.FeedbackQACommentManager;
import org.cgiar.ccafs.marlo.data.manager.UserManager;
import org.cgiar.ccafs.marlo.data.model.FeedbackQAReply;
import org.cgiar.ccafs.marlo.data.model.FeedbackQAComment;
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
  private Long replyId;
  private String reply;
  private Long userId;
  private Date date;
  private Long commentId;
  private FeedbackCommentManager commentManager;
  private FeedbackQACommentManager commentQAManager;
  private UserManager userManager;

  @Inject
  public SaveFeedbackReplyAction(APConfig config, FeedbackQACommentManager commentQAManager,
    FeedbackCommentManager commentManager, UserManager userManager) {
    super(config);
    this.commentManager = commentManager;
    this.userManager = userManager;
    this.commentQAManager = commentQAManager;
  }

  @Override
  public String execute() throws Exception {
    // @param = reply/commentID/userID
    // @param (optional) = replyID

    save = new HashMap<String, Object>();
    if (reply != null && commentId != null) {

      // Create feedback Comment save object
      FeedbackQAReply feedbackReply = new FeedbackQAReply();

      // get existing object from database
      try {
        if (replyId != null) {
          FeedbackQAReply replyDB = commentManager.getFeedbackCommentById(replyId);
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
      if (date != null) {
        feedbackReply.setCommentDate(date);
      }

      feedbackReply = commentManager.saveFeedbackComment(feedbackReply);

      if (feedbackReply.getId() != null) {
        FeedbackQAComment comment = new FeedbackQAComment();
        try {
          comment = commentQAManager.getFeedbackQACommentById(commentId);
          comment.setReply(feedbackReply);
          commentQAManager.saveFeedbackQAComment(comment);
        } catch (Exception e) {
          logger.error("unable to set the reply to comment", e);
        }

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
  }

  public void setSave(Map<String, Object> save) {
    this.save = save;
  }

}
