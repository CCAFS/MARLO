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

import java.util.HashMap;
import java.util.Map;

import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;
import org.apache.struts2.dispatcher.Parameter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DeleteFeedbackCommentsAction extends BaseAction {

  /**
   * 
   */
  private static final long serialVersionUID = -4335064142194555431L;
  private final Logger logger = LoggerFactory.getLogger(DeleteFeedbackCommentsAction.class);
  private Map<String, Object> delete;
  Long commentId;
  private FeedbackQACommentManager commentQAManager;

  @Inject
  public DeleteFeedbackCommentsAction(APConfig config, FeedbackQACommentManager commentQAManager) {
    super(config);
    this.commentQAManager = commentQAManager;
  }

  @Override
  public String execute() throws Exception {
    // @param = commentID

    delete = new HashMap<String, Object>();
    if (commentId != null) {
      FeedbackQAComment qaComment = new FeedbackQAComment();
      try {
        qaComment = commentQAManager.getFeedbackQACommentById(commentId);
      } catch (Exception e) {
        delete.put("delete", false);
        logger.error("unable to get qaComment", e);
      }
      if (qaComment != null && qaComment.getId() != null) {
        commentQAManager.deleteFeedbackQAComment(qaComment.getId());
        delete.put("delete", true);
      } else {
        delete.put("delete", false);
      }

    } else {
      delete.put("delete", false);
    }
    return SUCCESS;
  }

  public Map<String, Object> getDelete() {
    return delete;
  }

  @Override
  public void prepare() throws Exception {
    Map<String, Parameter> parameters = this.getParameters();

    try {
      if (parameters.get(APConstants.COMMENT_REQUEST_ID).isDefined()) {
        commentId =
          Long.parseLong(StringUtils.trim(parameters.get(APConstants.COMMENT_REQUEST_ID).getMultipleValues()[0]));
      }
    } catch (Exception e) {
      logger.error("unable to get commentID", e);
    }
  }

  public void setDelete(Map<String, Object> delete) {
    this.delete = delete;
  }

}
