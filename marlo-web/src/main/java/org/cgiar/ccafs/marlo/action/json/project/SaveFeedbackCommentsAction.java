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

import java.util.Collections;
import java.util.List;
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
  private List<Map<String, Object>> comments;
  private String phaseId;
  private String fieldId;
  private String comment;
  private String status;
  private String replyId;
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
    // @param = comment/parentID/phaseID

    Map<String, Object> fieldsMap;
    if (comments != null) {

      // Create feedback Comment save object
      FeedbackQAComment qaComment = new FeedbackQAComment();
      qaComment.setComment(comment);
      if (phaseId != null) {
        Phase phase = phaseManager.getPhaseById(Long.getLong(phaseId));
        qaComment.setPhase(phase);
      }

      if (status != null) {
        qaComment.setStatus(status);
      }

      if (replyId != null) {
        FeedbackComment reply = commentManager.getFeedbackCommentById(Long.getLong(replyId));
        qaComment.setReply(reply);
      }

      if (fieldId != null) {
        InternalQaCommentableFields field =
          internalQaCommentableFieldsManager.getInternalQaCommentableFieldsById(Long.getLong(fieldId));
        qaComment.setField(field);
      }

      qaComment.setScreen(0);
      qaComment.setObject(0);
      commentQAManager.saveFeedbackQAComment(qaComment);
    }

    fieldsMap = Collections.emptyMap();
    return SUCCESS;
  }

  public List<Map<String, Object>> getComments() {
    return comments;
  }

  @Override
  public void prepare() throws Exception {
    Map<String, Parameter> parameters = this.getParameters();

    if (parameters.get(APConstants.PHASE_ID).isDefined()) {
      phaseId = StringUtils.trim(StringUtils.trim(parameters.get(APConstants.PHASE_ID).getMultipleValues()[0]));
    }
    if (parameters.get(APConstants.COMMENT_REQUEST).isDefined()) {
      comment = StringUtils.trim(StringUtils.trim(parameters.get(APConstants.COMMENT_REQUEST).getMultipleValues()[0]));
    }
    if (parameters.get(APConstants.STATUS_REQUEST).isDefined()) {
      status = StringUtils.trim(StringUtils.trim(parameters.get(APConstants.STATUS_REQUEST).getMultipleValues()[0]));
    }
    if (parameters.get(APConstants.FIELD_REQUEST_ID).isDefined()) {
      fieldId = StringUtils.trim(StringUtils.trim(parameters.get(APConstants.FIELD_REQUEST_ID).getMultipleValues()[0]));
    }
    if (parameters.get(APConstants.REPLY_ID_REQUEST).isDefined()) {
      replyId = StringUtils.trim(StringUtils.trim(parameters.get(APConstants.REPLY_ID_REQUEST).getMultipleValues()[0]));
    }
  }

  public void setComments(List<Map<String, Object>> comments) {
    this.comments = comments;
  }

}
