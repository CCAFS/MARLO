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

package org.cgiar.ccafs.marlo.action.superadmin;

import org.cgiar.ccafs.marlo.action.BaseAction;
import org.cgiar.ccafs.marlo.data.manager.FeedbackQACommentableFieldsManager;
import org.cgiar.ccafs.marlo.data.model.FeedbackQACommentableFields;
import org.cgiar.ccafs.marlo.utils.APConfig;
import org.cgiar.ccafs.marlo.validation.superadmin.FeedbackManagementValidator;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import javax.inject.Inject;


public class FeedbackManagementAction extends BaseAction {

  private static final long serialVersionUID = -793652591843623397L;

  private List<FeedbackQACommentableFields> feedbackFields;

  private final FeedbackQACommentableFieldsManager fieldsManager;
  private FeedbackManagementValidator validator;


  @Inject
  public FeedbackManagementAction(APConfig config, FeedbackQACommentableFieldsManager fieldsManager,
    FeedbackManagementValidator validator) {
    super(config);
    this.fieldsManager = fieldsManager;
    this.validator = validator;
  }

  public List<FeedbackQACommentableFields> getFeedbackFields() {
    return feedbackFields;
  }

  @Override
  public void prepare() throws Exception {

    feedbackFields = fieldsManager.findAll();

    if (this.isHttpPost()) {
      feedbackFields.clear();
    }
  }

  @Override
  public String save() {
    if (this.canAccessSuperAdmin()) {
      if (feedbackFields != null && !feedbackFields.isEmpty()) {

        List<Long> IDs = feedbackFields.stream().map(FeedbackQACommentableFields::getId).filter(Objects::nonNull)
          .collect(Collectors.toList());

        fieldsManager.findAll().stream()
          .filter(activityDB -> activityDB.getId() != null && !IDs.contains(activityDB.getId()))
          .map(FeedbackQACommentableFields::getId).forEach(fieldsManager::deleteInternalQaCommentableFields);

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

          fieldsManager.saveInternalQaCommentableFields(fieldSave);

        }
      }

      if (this.getUrl() == null || this.getUrl().isEmpty()) {
        Collection<String> messages = this.getActionMessages();
        if (!this.getInvalidFields().isEmpty()) {
          this.setActionMessages(null);
          // this.addActionMessage(Map.toString(this.getInvalidFields().toArray()));
          List<String> keys = new ArrayList<String>(this.getInvalidFields().keySet());
          for (String key : keys) {
            this.addActionMessage(key + ": " + this.getInvalidFields().get(key));
          }
        } else {
          this.addActionMessage("message:" + this.getText("saving.saved"));
        }
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
    this.feedbackFields = feedbackFields;
  }

  @Override
  public void validate() {
    if (save) {
      validator.validate(this, feedbackFields);
    }
  }

}