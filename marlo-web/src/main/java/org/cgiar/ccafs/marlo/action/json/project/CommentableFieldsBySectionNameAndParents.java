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
import org.cgiar.ccafs.marlo.data.manager.FeedbackQACommentableFieldsManager;
import org.cgiar.ccafs.marlo.data.model.FeedbackQACommentableFields;
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

public class CommentableFieldsBySectionNameAndParents extends BaseAction {

  /**
   * 
   */
  private static final long serialVersionUID = -4335064142194555431L;
  private final Logger logger = LoggerFactory.getLogger(CommentableFieldsBySectionNameAndParents.class);
  private List<Map<String, Object>> fieldsMap;
  private String parentId;
  private String sectionName;
  private FeedbackQACommentableFieldsManager feedbackQACommentableFieldsManager;


  @Inject
  public CommentableFieldsBySectionNameAndParents(APConfig config,
    FeedbackQACommentableFieldsManager feedbackQACommentableFieldsManager) {
    super(config);
    this.feedbackQACommentableFieldsManager = feedbackQACommentableFieldsManager;
  }


  @Override
  public String execute() throws Exception {
    fieldsMap = new ArrayList<Map<String, Object>>();
    Map<String, Object> fieldsMap;

    List<FeedbackQACommentableFields> fields = new ArrayList<>();
    if (parentId != null && sectionName != null) {
      try {
        fields = feedbackQACommentableFieldsManager.findAll().stream()
          .filter(qa -> qa != null && qa.isActive() && qa.getParentId() != null && qa.getParentId().equals(parentId)
            && qa.getSectionName() != null && qa.getSectionName().equals(sectionName))
          .collect(Collectors.toList());
      } catch (Exception e) {
        logger.error("unable to get fields - with parentID parameter", e);
        fields = new ArrayList<>();
      }
    }

    if (sectionName != null && feedbackQACommentableFieldsManager.findAll() != null) {
      try {
        fields = feedbackQACommentableFieldsManager.findAll().stream()
          .filter(
            qa -> qa != null && qa.isActive() && qa.getSectionName() != null && qa.getSectionName().equals(sectionName))
          .collect(Collectors.toList());
      } catch (Exception e) {
        logger.error("unable to get fields", e);
        fields = new ArrayList<>();
      }
    }


    if (fields != null && !fields.isEmpty()) {
      for (FeedbackQACommentableFields field : fields) {
        fieldsMap = new HashMap<String, Object>();
        if (field.getFrontName() != null) {
          fieldsMap.put("fieldName", field.getFrontName());
        } else {
          fieldsMap.put("fieldName", "");
        }
        if (field.getFieldName() != null) {
          fieldsMap.put("description", field.getFieldName());
        } else {
          fieldsMap.put("description", "");
        }
        if (field.getSectionName() != null) {
          fieldsMap.put("sectionName", field.getSectionName());
        } else {
          fieldsMap.put("sectionName", "");
        }
        if (field.getId() != null) {
          fieldsMap.put("fieldID", field.getId());
        } else {
          fieldsMap.put("fieldID", "");
        }
        this.fieldsMap.add(fieldsMap);
      }
    } else {
      fieldsMap = Collections.emptyMap();
    }


    return SUCCESS;
  }

  public List<Map<String, Object>> getFieldsMap() {
    return fieldsMap;
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
      parentId =
        StringUtils.trim(StringUtils.trim(parameters.get(APConstants.PARENT_REQUEST_ID).getMultipleValues()[0]));
    }
    if (parameters.get(APConstants.SECTION_REQUEST_NAME).isDefined()) {
      sectionName =
        StringUtils.trim(StringUtils.trim(parameters.get(APConstants.SECTION_REQUEST_NAME).getMultipleValues()[0]));
    }
  }

  public void setFieldsMap(List<Map<String, Object>> fieldsMap) {
    this.fieldsMap = fieldsMap;
  }

}
