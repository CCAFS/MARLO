/*
 * This file is part of Managing Agricultural Research for Learning&*Outcomes Platform(MARLO).
 ** MARLO is free software:you can redistribute it and/or modify
 ** it under the terms of the GNU General Public License as published by
 ** the Free Software Foundation,either version 3 of the License,or*at your option)any later version.
 ** MARLO is distributed in the hope that it will be useful,
 ** but WITHOUT ANY WARRANTY;without even the implied warranty of*MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.See
 * the
 ** GNU General Public License for more details.
 ** You should have received a copy of the GNU General Public License
 ** along with MARLO.If not,see<http:// www.gnu.org/licenses/>.
 *****************************************************************/

package org.cgiar.ccafs.marlo.action.crp.admin;

import org.cgiar.ccafs.marlo.action.BaseAction;
import org.cgiar.ccafs.marlo.data.manager.FeedbackQACommentableFieldsManager;
import org.cgiar.ccafs.marlo.data.model.FeedbackQACommentableFields;
import org.cgiar.ccafs.marlo.data.model.ProjectSectionsEnum;
import org.cgiar.ccafs.marlo.utils.APConfig;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import javax.inject.Inject;


public class FeedbackManagementAction extends BaseAction {

  private static final long serialVersionUID = -793652591843623397L;

  private List<FeedbackQACommentableFields> feedbackFields;
  private List<String> projectSections;

  private final FeedbackQACommentableFieldsManager fieldsManager;


  @Inject
  public FeedbackManagementAction(APConfig config, FeedbackQACommentableFieldsManager fieldsManager) {
    super(config);
    this.fieldsManager = fieldsManager;
  }

  public List<FeedbackQACommentableFields> getFeedbackFields() {
    return feedbackFields;
  }

  public List<String> getProjectSections() {
    return projectSections;
  }

  @Override
  public void prepare() throws Exception {
    ProjectSectionsEnum[] projectSectionsArray = ProjectSectionsEnum.values();
    projectSections = new ArrayList<>();
    if (projectSectionsArray != null && (projectSectionsArray.length > 0)) {
      for (ProjectSectionsEnum section : projectSectionsArray) {
        if (section != null && section.getStatus() != null) {
          projectSections.add(section.getStatus());
        }
      }
    }

    if (!this.isHttpPost()) {
      // For GET: Load feedback fields from DB
      feedbackFields = fieldsManager.findAllByGlobalUnit(this.getCurrentGlobalUnit().getId());
    } else {
      // For POST: Will bind feedback fields manually from request in save()
      feedbackFields = new ArrayList<>();
    }
  }

  /**
   * Manually bind feedback fields from HTTP request parameters.
   * This is necessary because Struts 6 cannot automatically populate lists when items are deleted.
   */
  private void bindFeedbackFieldsFromRequest() {
    feedbackFields = new ArrayList<>();
    int index = 0;
    boolean hasMore = true;

    while (hasMore) {
      String idParam = this.getRequest().getParameter("feedbackFields[" + index + "].id");

      if (idParam != null && !idParam.trim().isEmpty()) {
        try {
          FeedbackQACommentableFields field = new FeedbackQACommentableFields();

          // ID
          Long id = Long.parseLong(idParam);
          field.setId(id);

          // Field Name
          String fieldName = this.getRequest().getParameter("feedbackFields[" + index + "].fieldName");
          field.setFieldName(fieldName);

          // Field Description
          String fieldDescription =
            this.getRequest().getParameter("feedbackFields[" + index + "].fieldDescription");
          field.setFieldDescription(fieldDescription);

          // Section Name
          String sectionName = this.getRequest().getParameter("feedbackFields[" + index + "].sectionName");
          field.setSectionName(sectionName);

          // Section Description
          String sectionDescription =
            this.getRequest().getParameter("feedbackFields[" + index + "].sectionDescription");
          field.setSectionDescription(sectionDescription);

          // Parent Field Identifier
          String parentFieldIdentifier =
            this.getRequest().getParameter("feedbackFields[" + index + "].parentFieldIdentifier");
          field.setParentFieldIdentifier(parentFieldIdentifier);

          // Parent Field Description
          String parentFieldDescription =
            this.getRequest().getParameter("feedbackFields[" + index + "].parentFieldDescription");
          field.setParentFieldDescription(parentFieldDescription);

          feedbackFields.add(field);
          index++;
        } catch (Exception e) {
          hasMore = false;
        }
      } else {
        hasMore = false;
      }
    }
  }

  @Override
  public String save() {
    if (this.hasPermission("*")) {
      // Manually bind feedback fields from request parameters
      bindFeedbackFieldsFromRequest();

      if (feedbackFields != null && !feedbackFields.isEmpty()) {

        // FIRST: Save/update all feedback fields from the form
        for (FeedbackQACommentableFields fields : feedbackFields) {

          // New Activity
          FeedbackQACommentableFields fieldSave = new FeedbackQACommentableFields();

          if (fields.getId() != null) {
            fieldSave = fieldsManager.getInternalQaCommentableFieldsById(fields.getId());
          }
          if (fields.getFieldName() != null) {
            fieldSave.setFieldName(fields.getFieldName());
          }
          if (fields.getFieldDescription() != null) {
            fieldSave.setFieldDescription(fields.getFieldDescription());
          }
          if (fields.getSectionName() != null) {
            fieldSave.setSectionName(fields.getSectionName());
          }
          if (fields.getSectionDescription() != null) {
            fieldSave.setSectionDescription(fields.getSectionDescription());
          }
          if (fields.getParentFieldIdentifier() != null) {
            fieldSave.setParentFieldIdentifier(fields.getParentFieldIdentifier());
          }
          if (fields.getParentFieldDescription() != null) {
            fieldSave.setParentFieldDescription(fields.getParentFieldDescription());
          }

          if (fields.getGlobalUnit() != null) {
            fieldSave.setGlobalUnit(fields.getGlobalUnit());
          } else {
            fieldSave.setGlobalUnit(this.getCurrentGlobalUnit());
          }

          fieldsManager.saveInternalQaCommentableFields(fieldSave);

        }

        // THEN: Delete feedback fields not present in the form
        List<Long> IDs = feedbackFields.stream().map(FeedbackQACommentableFields::getId).filter(Objects::nonNull)
          .collect(Collectors.toList());

        List<FeedbackQACommentableFields> existingFields =
          fieldsManager.findAllByGlobalUnit(this.getCurrentGlobalUnit().getId());
        if (existingFields != null && !existingFields.isEmpty()) {
          for (FeedbackQACommentableFields activityDB : existingFields) {
            if (activityDB.getId() != null && !IDs.contains(activityDB.getId())) {
              fieldsManager.deleteInternalQaCommentableFields(activityDB.getId());
            }
          }
        }
      }

      if (this.getUrl() == null || this.getUrl().isEmpty()) {
        Collection<String> messages = this.getActionMessages();
        if (this.getInvalidFields() != null && !this.getInvalidFields().isEmpty()) {
          this.setActionMessages(null);
          // this.addActionMessage(Map.toString(this.getInvalidFields().toArray()));
          List<String> keys = new ArrayList<String>(this.getInvalidFields().keySet());
          for (String key : keys) {
            this.addActionMessage(key + ": " + this.getInvalidFields().get(key));
          }
        } else {
          // this.addActionMessage("message:" + this.getText("saving.saved"));
        }
        this.addActionMessage("message:" + this.getText("saving.saved"));
        return SUCCESS;
      } else {
        this.addActionMessage("");
        this.setActionMessages(null);
        return REDIRECT;
      }

    } else {
      return NOT_AUTHORIZED;
    }
  }

  public void setFeedbackFields(List<FeedbackQACommentableFields> feedbackFields) {
    // Note: This setter is kept for JSP/FTL access but is not used for Struts binding
    // We bind feedback fields manually in save() using bindFeedbackFieldsFromRequest()
    this.feedbackFields = feedbackFields;
  }

  public void setProjectSections(List<String> projectSections) {
    this.projectSections = projectSections;
  }

  @Override
  public void validate() {
    if (save) {
    }
  }
}