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
import org.cgiar.ccafs.marlo.data.manager.InternalQaCommentableFieldsManager;
import org.cgiar.ccafs.marlo.data.manager.PhaseManager;
import org.cgiar.ccafs.marlo.data.manager.ProjectManager;
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

public class CommentableFieldsBySectionNameAndParents extends BaseAction {

  /**
   * 
   */
  private static final long serialVersionUID = -4335064142194555431L;
  private Map<String, Object> fieldsMap;
  private String parentName;
  private String parentId;
  private String sectionName;
  private PhaseManager phaseManager;
  private InternalQaCommentableFieldsManager internalQaCommentableFieldsManager;

  @Inject
  public CommentableFieldsBySectionNameAndParents(APConfig config, PhaseManager phaseManager,
    ProjectManager projectManager, InternalQaCommentableFieldsManager internalQaCommentableFieldsManager) {
    super(config);
    this.phaseManager = phaseManager;
    this.internalQaCommentableFieldsManager = internalQaCommentableFieldsManager;

  }


  @Override
  public String execute() throws Exception {

    List<InternalQaCommentableFields> fields;
    if (parentId != null && sectionName != null) {
      try {
        fields = internalQaCommentableFieldsManager.findAll().stream()
          .filter(qa -> qa != null && qa.isActive() && qa.getParentId() != null && qa.getParentId().equals(parentId)
            && qa.getSectionName() != null && qa.getSectionName().equals(sectionName))
          .collect(Collectors.toList());
      } catch (Exception e) {
        fields = new ArrayList<>();
      }

      if (fields != null && !fields.isEmpty()) {
        for (InternalQaCommentableFields field : fields) {
          this.fieldsMap = new HashMap<>();
          this.fieldsMap.put("fieldName", field.getFrontName());
          this.fieldsMap.put("description", field.getFieldName());
          this.fieldsMap.put("sectionName", field.getSectionName());
        }
      } else {
        this.fieldsMap = Collections.emptyMap();
      }

    }
    return SUCCESS;
  }


  public Map<String, Object> getFieldsMap() {
    return fieldsMap;
  }

  @Override
  public void prepare() throws Exception {
    Map<String, Parameter> parameters = this.getParameters();

    if (parameters.get(APConstants.PARENT_REQUEST_NAME).isDefined()) {
      parentName = StringUtils.trim(parameters.get(APConstants.PARENT_REQUEST_NAME).getMultipleValues()[0]);
    }
    if (parameters.get(APConstants.PARENT_REQUEST_ID).isDefined()) {
      parentId =
        StringUtils.trim(StringUtils.trim(parameters.get(APConstants.PARENT_REQUEST_ID).getMultipleValues()[0]));
    }
    if (parameters.get(APConstants.SECTION_REQUEST_NAME).isDefined()) {
      sectionName =
        StringUtils.trim(StringUtils.trim(parameters.get(APConstants.SECTION_REQUEST_NAME).getMultipleValues()[0]));
    }
  }

  public void setFieldsMap(Map<String, Object> fieldsMap) {
    this.fieldsMap = fieldsMap;
  }

}
