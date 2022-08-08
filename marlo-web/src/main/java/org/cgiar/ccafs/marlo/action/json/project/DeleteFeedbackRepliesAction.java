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

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;
import org.apache.struts2.dispatcher.Parameter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DeleteFeedbackRepliesAction extends BaseAction {

  /**
   * 
   */
  private static final long serialVersionUID = -4335064142194555431L;
  private final Logger logger = LoggerFactory.getLogger(DeleteFeedbackRepliesAction.class);
  private Map<String, Object> delete;
  private Long commentId;
  private FeedbackQAReplyManager replyQAManager;
  private FeedbackQACommentManager commentManager;

  @Inject
  public DeleteFeedbackRepliesAction(APConfig config, FeedbackQAReplyManager replyQAManager,
    FeedbackQACommentManager commentManager) {
    super(config);
    this.replyQAManager = replyQAManager;
    this.commentManager = commentManager;
  }

  @Override
  public String execute() throws Exception {
    // @param = commentID

    delete = new HashMap<String, Object>();
    if (commentId != null) {
      FeedbackQAReply qaReply = new FeedbackQAReply();
      try {
        qaReply = replyQAManager.getFeedbackCommentById(commentId);

        try {
          if (qaReply != null && qaReply.getId() != null) {
            long localID = qaReply.getId();

            FeedbackQAComment comment = new FeedbackQAComment();
            comment = commentManager.findAll().stream().filter(c -> c != null && c.getReply() != null
              && c.getReply().getId() != null && c.getReply().getId().equals(localID)).collect(Collectors.toList())
              .get(0);
            if (comment != null && comment.getId() != null) {
              comment.setStatus(null);
              comment.setApprovalDate(null);
              comment.setReply(null);
              comment.setUserApproval(null);
              commentManager.saveFeedbackQAComment(comment);
            }
          }
        } catch (Exception e) {
          logger.error("unable to remove reaction", e);
        }
      } catch (Exception e) {
        delete.put("delete", false);
        logger.error("unable to get qaComment", e);
      }
      if (qaReply != null && qaReply.getId() != null) {
        replyQAManager.deleteFeedbackComment(qaReply.getId());
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
