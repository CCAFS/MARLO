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
import org.cgiar.ccafs.marlo.data.model.FeedbackQAComment;
import org.cgiar.ccafs.marlo.utils.APConfig;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;
import org.apache.struts2.dispatcher.Parameter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SaveTrackingStatusAction extends BaseAction {

  /**
   * 
   */
  private static final long serialVersionUID = -4335064142194555431L;
  private final Logger logger = LoggerFactory.getLogger(SaveTrackingStatusAction.class);
  private Map<String, Object> save;
  private String status;
  private Date date;
  private Long commentId;
  private FeedbackQACommentManager commentQAManager;
  private FeedbackQAComment commentSave;

  @Inject
  public SaveTrackingStatusAction(APConfig config, FeedbackQACommentManager commentQAManager) {
    super(config);
    this.commentQAManager = commentQAManager;
  }

  @Override
  public String execute() throws Exception {
    // @param = status/commentID/userID

    save = new HashMap<String, Object>();
    if (status != null && commentId != null) {

      commentSave = new FeedbackQAComment();

      // get existing object from database
      try {
        FeedbackQAComment commentDB = commentQAManager.getFeedbackQACommentById(commentId);
        if (commentDB != null && commentDB.getId() != null) {
          commentSave = commentDB;

          date = new Date();

          if (status.equals("0") || status.equals("false")) {
            commentSave.setTracking(false);
            if (commentSave.getStartTrackDate() != null) {
              commentSave.setEndTrackDate(date);
            }
          }
          if (status.equals("1") || status.equals("true")) {
            commentSave.setTracking(true);
            commentSave.setStartTrackDate(date);
            if (commentSave.getEndTrackDate() != null) {
              commentSave.setEndTrackDate(null);
            }
          }

          commentSave = commentQAManager.saveFeedbackQAComment(commentSave);

          if (commentSave.getId() != null) {
            save.put("save", true);
            save.put("id", commentSave.getId());
          } else {
            save.put("save", false);
          }
        }
      } catch (Exception e) {
        logger.error("unable to get existing Feedback comment object from DB", e);
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
      if (parameters.get(APConstants.COMMENT_REQUEST_ID).isDefined()) {
        commentId = Long.parseLong(
          StringUtils.trim(StringUtils.trim(parameters.get(APConstants.COMMENT_REQUEST_ID).getMultipleValues()[0])));
      }
    } catch (Exception e) {
      logger.error("unable to get replyID", e);
    }

    try {
      if (parameters.get(APConstants.STATUS_REQUEST).isDefined()) {
        status = StringUtils.trim(StringUtils.trim(parameters.get(APConstants.STATUS_REQUEST).getMultipleValues()[0]));
      }
    } catch (Exception e) {
      logger.error("unable to get user", e);
    }
  }

  public void setSave(Map<String, Object> save) {
    this.save = save;
  }

}
