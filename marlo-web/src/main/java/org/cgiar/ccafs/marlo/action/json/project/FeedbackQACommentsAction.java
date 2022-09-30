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
import org.cgiar.ccafs.marlo.data.manager.FeedbackQACommentableFieldsManager;
import org.cgiar.ccafs.marlo.data.manager.FeedbackQAReplyManager;
import org.cgiar.ccafs.marlo.data.model.FeedbackQAComment;
import org.cgiar.ccafs.marlo.data.model.FeedbackQACommentableFields;
import org.cgiar.ccafs.marlo.data.model.FeedbackQAReply;
import org.cgiar.ccafs.marlo.data.model.FeedbackStatusEnum;
import org.cgiar.ccafs.marlo.utils.APConfig;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;
import org.apache.struts2.dispatcher.Parameter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FeedbackQACommentsAction extends BaseAction {

  /**
   * 
   */
  private static final long serialVersionUID = -4335064142194555431L;
  private final Logger logger = LoggerFactory.getLogger(FeedbackQACommentsAction.class);
  private List<Map<String, Object>> comments;
  private Long parentId;
  private String sectionName;
  private String frontName;
  private Long phaseId;
  private Long fieldId;
  private FeedbackQACommentableFieldsManager feedbackQACommentableFieldsManager;
  private FeedbackQACommentManager commentManager;
  private FeedbackQAReplyManager feedbackQAReplyManager;


  @Inject
  public FeedbackQACommentsAction(APConfig config,
    FeedbackQACommentableFieldsManager feedbackQACommentableFieldsManager, FeedbackQACommentManager commentManager,
    FeedbackQAReplyManager feedbackQAReplyManager) {
    super(config);
    this.feedbackQACommentableFieldsManager = feedbackQACommentableFieldsManager;
    this.commentManager = commentManager;
    this.feedbackQAReplyManager = feedbackQAReplyManager;
  }

  @Override
  public String execute() throws Exception {
    comments = new ArrayList<Map<String, Object>>();
    Map<String, Object> fieldsMap;
    Long fieldId = null;
    List<FeedbackQAComment> feedbackComments = new ArrayList<>();
    List<FeedbackQACommentableFields> fields = new ArrayList<>();

    // @param = sectionName/parentID/phaseID
    if (sectionName != null && parentId != null && phaseId != null
      && feedbackQACommentableFieldsManager.findAll() != null) {
      try {
        fields = feedbackQACommentableFieldsManager.findAll().stream()
          .filter(
            qa -> qa != null && qa.isActive() && qa.getSectionName() != null && qa.getSectionName().equals(sectionName))
          .collect(Collectors.toList());

        if (fields != null && !fields.isEmpty()) {
          for (FeedbackQACommentableFields field : fields) {
            fieldId = field.getId();
            long fieldIdLocal = fieldId;

            // Get comments for field
            if (fieldId != null && commentManager.findAll() != null) {
              feedbackComments.addAll(commentManager.findAll().stream()
                .filter(c -> c.getField() != null && c.getField().getId() != null
                  && c.getField().getId().equals(fieldIdLocal) && c.getPhase() != null && c.getPhase().getId() != null
                  && c.getPhase().getId().equals(phaseId) && c.getParentId() == parentId)
                .collect(Collectors.toList()));

            }
          }
        }
      } catch (Exception e) {
        logger.error("unable to get feedback comments", e);
        fields = new ArrayList<>();
      }
    }


    if (feedbackComments != null && !feedbackComments.isEmpty()) {

      Map<String, Object> countMap = new HashMap<>();
      int count = 0;
      for (FeedbackQAComment comment : feedbackComments) {
        countMap.put("count", count);
        count++;

        fieldsMap = new HashMap<String, Object>();
        if (comment.getComment() != null) {
          fieldsMap.put("commentId", comment.getId());
        } else {
          fieldsMap.put("commentId", "");
        }
        if (comment.getComment() != null) {
          fieldsMap.put("comment", comment.getComment());
        } else {
          fieldsMap.put("comment", "");
        }
        if (comment.getReply() != null && comment.getReply().getComment() != null) {
          fieldsMap.put("reply", comment.getReply().getComment());
        } else {
          fieldsMap.put("reply", "");
        }


        if (comment.getFeedbackStatus() != null) {

          String statusText = null;
          if (comment.getFeedbackStatus().getId().equals(Long.parseLong(FeedbackStatusEnum.Disagreed.getStatusId()))) {
            statusText = "0";
          }
          if (comment.getFeedbackStatus().getId().equals(Long.parseLong(FeedbackStatusEnum.Agreed.getStatusId()))) {
            statusText = FeedbackStatusEnum.Agreed.getStatusId();
          }
          if (comment.getFeedbackStatus().getId()
            .equals(Long.parseLong(FeedbackStatusEnum.ClarificatioNeeded.getStatusId()))) {
            statusText = FeedbackStatusEnum.ClarificatioNeeded.getStatusId();
          }
          if (comment.getFeedbackStatus().getId().equals(Long.parseLong(FeedbackStatusEnum.Draft.getStatusId()))) {
            // statusText = FeedbackStatusEnum.Draft.getStatusId();
            statusText = "";
          }
          if (comment.getFeedbackStatus().getId().equals(Long.parseLong(FeedbackStatusEnum.Admitted.getStatusId()))) {
            statusText = FeedbackStatusEnum.Admitted.getStatusId();
          }
          /*
           * if (comment.getStatus().equalsIgnoreCase("rejected")) {
           * statusText = "5";
           * }
           */
          if (comment.getFeedbackStatus().getId().equals(Long.parseLong(FeedbackStatusEnum.Dismissed.getStatusId()))) {
            statusText = FeedbackStatusEnum.Dismissed.getStatusId();
          }

          fieldsMap.put("status", statusText);
        } else {
          fieldsMap.put("status", "");
        }
        if (comment.getField() != null && comment.getField().getFieldDescription() != null) {
          fieldsMap.put("frontName", comment.getField().getFieldDescription());
        } else {
          fieldsMap.put("frontName", "");
        }
        if (comment.getUser() != null && comment.getUser().getFirstName() != null
          && comment.getUser().getLastName() != null) {
          fieldsMap.put("userName", comment.getUser().getFirstName() + " " + comment.getUser().getLastName());
        } else {
          fieldsMap.put("userName", "");
        }
        if (comment.getCommentDate() != null && comment.getCommentDate().toString() != null) {
          String dateString = comment.getCommentDate().toString();
          fieldsMap.put("date", dateString);
        } else {
          fieldsMap.put("date", "");
        }
        if (comment.getUserApproval() != null && comment.getUserApproval().getFirstName() != null
          && comment.getUserApproval().getLastName() != null) {
          fieldsMap.put("approvalUserName",
            comment.getUserApproval().getFirstName() + " " + comment.getUserApproval().getLastName());
        } else {
          fieldsMap.put("approvalUserName", "");
        }
        if (comment.getApprovalDate() != null && comment.getApprovalDate().toString() != null) {
          String dateString = comment.getApprovalDate().toString();
          fieldsMap.put("approvalDate", dateString);
        } else {
          fieldsMap.put("approvalDate", "");
        }

        // Editor user
        if (comment.getUserEditor() != null && comment.getUserEditor().getFirstName() != null
          && comment.getUserEditor().getLastName() != null) {
          fieldsMap.put("editorUsername",
            comment.getUserEditor().getFirstName() + " " + comment.getUserEditor().getLastName());
        } else {
          fieldsMap.put("editorUsername", "");
        }
        if (comment.getEditionDate() != null && comment.getEditionDate().toString() != null) {
          String dateString = comment.getEditionDate().toString();
          fieldsMap.put("editionDate", dateString);
        } else {
          fieldsMap.put("editionDate", "");
        }
        if (comment.getReply() != null && comment.getReply().getId() != null) {
          FeedbackQAReply reply = new FeedbackQAReply();
          if (feedbackQAReplyManager.existFeedbackComment(comment.getReply().getId())) {
            reply = feedbackQAReplyManager.getFeedbackCommentById(comment.getReply().getId());
          }
          if (reply != null) {
            if (reply.getUser() != null && reply.getUser().getFirstName() != null
              && reply.getUser().getLastName() != null) {
              fieldsMap.put("userName_reply", comment.getUser().getFirstName() + " " + comment.getUser().getLastName());
            } else {
              fieldsMap.put("userName_reply", "");
            }
            if (reply.getCommentDate() != null && comment.getCommentDate().toString() != null) {
              String dateString = comment.getCommentDate().toString();
              fieldsMap.put("date_reply", dateString);
            } else {
              fieldsMap.put("date_reply", "");
            }
          }
        }
        this.comments.add(fieldsMap);
      }
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
    /*
     * if (parameters.get(APConstants.PARENT_REQUEST_NAME).isDefined()) {
     * parentName = StringUtils.trim(parameters.get(APConstants.PARENT_REQUEST_NAME).getMultipleValues()[0]);
     * }
     */
    if (parameters.get(APConstants.PARENT_REQUEST_ID).isDefined()) {
      parentId = Long.parseLong(
        StringUtils.trim(StringUtils.trim(parameters.get(APConstants.PARENT_REQUEST_ID).getMultipleValues()[0])));
    }
    if (parameters.get(APConstants.SECTION_REQUEST_NAME).isDefined()) {
      sectionName =
        StringUtils.trim(StringUtils.trim(parameters.get(APConstants.SECTION_REQUEST_NAME).getMultipleValues()[0]));
    }
    if (parameters.get(APConstants.FRONT_REQUEST_NAME).isDefined()) {
      frontName =
        StringUtils.trim(StringUtils.trim(parameters.get(APConstants.FRONT_REQUEST_NAME).getMultipleValues()[0]));
    }
    if (parameters.get(APConstants.PHASE_ID).isDefined()) {
      phaseId =
        Long.parseLong(StringUtils.trim(StringUtils.trim(parameters.get(APConstants.PHASE_ID).getMultipleValues()[0])));
    }
    if (parameters.get(APConstants.FIELD_REQUEST_ID).isDefined()) {
      fieldId = Long.parseLong(
        StringUtils.trim(StringUtils.trim(parameters.get(APConstants.FIELD_REQUEST_ID).getMultipleValues()[0])));
    }
  }

  public void setComments(List<Map<String, Object>> comments) {
    this.comments = comments;
  }

}
