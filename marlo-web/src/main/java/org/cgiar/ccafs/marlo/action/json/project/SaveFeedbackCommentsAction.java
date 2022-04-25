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
import org.cgiar.ccafs.marlo.data.manager.InternalQaCommentableFieldsManager;
import org.cgiar.ccafs.marlo.data.manager.PhaseManager;
import org.cgiar.ccafs.marlo.data.model.FeedbackComment;
import org.cgiar.ccafs.marlo.data.model.FeedbackQAComment;
import org.cgiar.ccafs.marlo.data.model.InternalQaCommentableFields;
import org.cgiar.ccafs.marlo.data.model.Phase;
import org.cgiar.ccafs.marlo.utils.APConfig;

import java.util.HashMap;
import java.util.Map;

import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;
import org.apache.struts2.dispatcher.Parameter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SaveFeedbackCommentsAction extends BaseAction {

  /**
   * 
   */
  private static final long serialVersionUID = -4335064142194555431L;
  private final Logger logger = LoggerFactory.getLogger(SaveFeedbackCommentsAction.class);
  private Map<String, Object> save;
  private Long phaseId;
  private Long fieldId;
  private String comment;
  private String status;
  private Long replyId;
  private Long commentId;
  private InternalQaCommentableFieldsManager internalQaCommentableFieldsManager;
  private FeedbackQACommentManager commentQAManager;
  private FeedbackCommentManager commentManager;
  private PhaseManager phaseManager;


  @Inject
  public SaveFeedbackCommentsAction(APConfig config,
    InternalQaCommentableFieldsManager internalQaCommentableFieldsManager, FeedbackQACommentManager commentQAManager,
    FeedbackCommentManager commentManager, PhaseManager phaseManager) {
    super(config);
    this.internalQaCommentableFieldsManager = internalQaCommentableFieldsManager;
    this.commentQAManager = commentQAManager;
    this.commentManager = commentManager;
    this.phaseManager = phaseManager;
  }

  @Override
  public String execute() throws Exception {
    // @param = comment/parentID/phaseID/fieldID
    // @param (optional) = commentID

    save = new HashMap<String, Object>();
    if (fieldId != null) {

      // Create feedback Comment save object
      FeedbackQAComment qaComment = new FeedbackQAComment();

      // get existing object from database
      try {
        if (commentId != null) {
          FeedbackQAComment qaCommentDB = commentQAManager.getFeedbackQACommentById(commentId);
          if (qaCommentDB != null && qaCommentDB.getId() != null) {
            qaComment = qaCommentDB;
          }
        }
      } catch (Exception e) {
        logger.error("unable to get existing Feedback QA comment object from DB", e);
      }

      qaComment.setComment(comment);


      if (phaseId != null) {
        Phase phase = phaseManager.getPhaseById(phaseId);
        qaComment.setPhase(phase);
      }

      if (status != null) {
        qaComment.setStatus(status);
      }

      if (replyId != null) {
        FeedbackComment reply = commentManager.getFeedbackCommentById(replyId);
        qaComment.setReply(reply);
      }

      if (fieldId != null) {
        InternalQaCommentableFields field =
          internalQaCommentableFieldsManager.getInternalQaCommentableFieldsById(fieldId);
        qaComment.setField(field);
      }

      qaComment.setScreen(0);
      qaComment.setObject(0);
      qaComment = commentQAManager.saveFeedbackQAComment(qaComment);

      if (qaComment.getId() != null) {
        save.put("save", true);
        save.put("id", qaComment.getId());
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


    if (parameters.get(APConstants.COMMENT_REQUEST).isDefined()) {
      comment = StringUtils.trim(StringUtils.trim(parameters.get(APConstants.COMMENT_REQUEST).getMultipleValues()[0]));
    }
    if (parameters.get(APConstants.STATUS_REQUEST).isDefined()) {
      status = StringUtils.trim(StringUtils.trim(parameters.get(APConstants.STATUS_REQUEST).getMultipleValues()[0]));
    }
    try {
      if (parameters.get(APConstants.PHASE_ID).isDefined()) {
        phaseId = Long
          .parseLong(StringUtils.trim(StringUtils.trim(parameters.get(APConstants.PHASE_ID).getMultipleValues()[0])));
      }
    } catch (Exception e) {
      logger.error("unable to get phaseID", e);
    }
    try {
      if (parameters.get(APConstants.FIELD_REQUEST_ID).isDefined()) {
        fieldId = Long.parseLong(
          StringUtils.trim(StringUtils.trim(parameters.get(APConstants.FIELD_REQUEST_ID).getMultipleValues()[0])));
      }
    } catch (Exception e) {
      logger.error("unable to get fieldID", e);
    }
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
  }

  public void setSave(Map<String, Object> save) {
    this.save = save;
  }

}
