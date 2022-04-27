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
import org.cgiar.ccafs.marlo.data.manager.InternalQaCommentableFieldsManager;
import org.cgiar.ccafs.marlo.data.model.FeedbackQAComment;
import org.cgiar.ccafs.marlo.data.model.InternalQaCommentableFields;
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
  private InternalQaCommentableFieldsManager internalQaCommentableFieldsManager;
  private FeedbackQACommentManager commentManager;


  @Inject
  public FeedbackQACommentsAction(APConfig config,
    InternalQaCommentableFieldsManager internalQaCommentableFieldsManager, FeedbackQACommentManager commentManager) {
    super(config);
    this.internalQaCommentableFieldsManager = internalQaCommentableFieldsManager;
    this.commentManager = commentManager;
  }

  @Override
  public String execute() throws Exception {
    comments = new ArrayList<Map<String, Object>>();
    Map<String, Object> fieldsMap;
    Long fieldId = null;
    List<FeedbackQAComment> feedbackComments = new ArrayList<>();
    List<InternalQaCommentableFields> fields = new ArrayList<>();

    // @param = sectionName/parentID/phaseID
    if (sectionName != null && parentId != null && phaseId != null) {
      try {
        fields = internalQaCommentableFieldsManager.findAll().stream()
          .filter(
            qa -> qa != null && qa.isActive() && qa.getSectionName() != null && qa.getSectionName().equals(sectionName))
          .collect(Collectors.toList());

        if (fields != null && !fields.isEmpty()) {
          for (InternalQaCommentableFields field : fields) {
            fieldId = field.getId();
            long fieldIdLocal = fieldId;

            // Get comments for field
            if (fieldId != null && commentManager.findAll() != null) {
              feedbackComments.addAll(commentManager.findAll().stream()
                .filter(c -> c.getField() != null && c.getField().getId() != null
                  && c.getField().getId().equals(fieldIdLocal) && c.getPhase() != null && c.getPhase().getId() != null
                  && c.getPhase().getId().equals(phaseId) && c.getObject() == parentId)
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
      for (FeedbackQAComment comment : feedbackComments) {

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
        if (comment.getStatus() != null) {
          fieldsMap.put("status", comment.getStatus());
        } else {
          fieldsMap.put("status", "");
        }
        if (comment.getField() != null && comment.getField().getFrontName() != null) {
          fieldsMap.put("frontName", comment.getField().getFrontName());
        } else {
          fieldsMap.put("frontName", "");
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
