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
import org.cgiar.ccafs.marlo.data.manager.FeedbackQAReplyManager;
import org.cgiar.ccafs.marlo.data.manager.FeedbackStatusManager;
import org.cgiar.ccafs.marlo.data.manager.UserManager;
import org.cgiar.ccafs.marlo.data.model.FeedbackQAReply;
import org.cgiar.ccafs.marlo.data.model.FeedbackStatus;
import org.cgiar.ccafs.marlo.data.model.FeedbackStatusEnum;
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

public class SaveReplyStatusAction extends BaseAction {

  /**
   * 
   */
  private static final long serialVersionUID = -4335064142194555431L;
  private final Logger logger = LoggerFactory.getLogger(SaveReplyStatusAction.class);
  private Map<String, Object> save;
  private Long userId;
  private String status;
  private Date date;
  private Long commentId;
  private FeedbackQAReplyManager feedbackQAReplyManager;
  private UserManager userManager;
  private FeedbackQAReply replySave;
  private FeedbackStatusManager feedbackStatusManager;

  @Inject
  public SaveReplyStatusAction(APConfig config, FeedbackStatusManager feedbackStatusManager, UserManager userManager,
    FeedbackQAReplyManager feedbackQAReplyManager) {
    super(config);
    this.userManager = userManager;
    this.feedbackStatusManager = feedbackStatusManager;
    this.feedbackQAReplyManager = feedbackQAReplyManager;
  }

  @Override
  public String execute() throws Exception {
    // @param = status/replyID/userID

    save = new HashMap<String, Object>();
    if (status != null && commentId != null) {

      replySave = new FeedbackQAReply();

      // get existing object from database
      try {
        FeedbackQAReply replyDB = feedbackQAReplyManager.getFeedbackCommentById(commentId);
        if (replyDB != null && replyDB.getId() != null) {
          replySave = replyDB;
        }
      } catch (Exception e) {
        logger.error("unable to get existing Feedback comment object from DB", e);
      }
      String statusText = null;
      if (status.equals("0")) {
        statusText = FeedbackStatusEnum.Disagreed.getStatus();
      }
      if (status.equals("1")) {
        statusText = FeedbackStatusEnum.Agreed.getStatus();
      }
      if (status.equals("2")) {
        statusText = FeedbackStatusEnum.ClarificatioNeeded.getStatus();
      }
      if (status.equals("3")) {
        statusText = FeedbackStatusEnum.Draft.getStatus();
      }
      if (status.equals("4")) {
        statusText = FeedbackStatusEnum.Admitted.getStatus();
      }
      if (status.equals("5")) {
        statusText = FeedbackStatusEnum.Disagreed.getStatus();
      }
      if (status.equals("6")) {
        statusText = FeedbackStatusEnum.Dismissed.getStatus();
      }
      if (status == null) {
        statusText = FeedbackStatusEnum.Draft.getStatus();
      }
      // replySave.setStatus(statusText);
      this.setFeedbackStatus();

      replySave = feedbackQAReplyManager.saveFeedbackComment(replySave);

      if (replySave.getId() != null) {
        save.put("save", true);
        save.put("id", replySave.getId());
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
        commentId = Long.parseLong(
          StringUtils.trim(StringUtils.trim(parameters.get(APConstants.REPLY_ID_REQUEST).getMultipleValues()[0])));
      }
    } catch (Exception e) {
      logger.error("unable to get replyID", e);
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
      if (parameters.get(APConstants.STATUS_REQUEST).isDefined()) {
        status = StringUtils.trim(StringUtils.trim(parameters.get(APConstants.STATUS_REQUEST).getMultipleValues()[0]));
      }
    } catch (Exception e) {
      logger.error("unable to get user", e);
    }
  }

  /**
   * Set feedback status id relation with feedback status table
   */
  public void setFeedbackStatus() {
    User user = new User();
    if (userId != null) {
      try {
        user = userManager.getUser(userId);
      } catch (Exception e) {
        logger.error("unable to set User object", e);
      }
    }

    date = new Date();

    if (status != null) {
      long idStatus;
      try {
        if (status.equals("0")) {
          idStatus = 5;
        } else {
          idStatus = Long.valueOf(status);
        }

        if (user != null) {
          replySave.setUserApproval(this.getCurrentUser());
        }
        replySave.setApprovalDate(date);


        FeedbackStatus feedbackStatus = feedbackStatusManager.getFeedbackStatusById(idStatus);
        replySave.setFeedbackStatus(feedbackStatus);
        // replySave = commentQAManager.saveFeedbackQAComment(replySave);
      } catch (Exception e) {
        logger.error("unable to get feedback status id", e);
      }

    }
  }

  public void setSave(Map<String, Object> save) {
    this.save = save;
  }

}
