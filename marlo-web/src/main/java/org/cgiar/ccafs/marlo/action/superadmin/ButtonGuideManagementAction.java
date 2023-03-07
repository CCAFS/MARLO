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
import org.cgiar.ccafs.marlo.data.manager.ButtonGuideContentManager;
import org.cgiar.ccafs.marlo.data.model.ButtonGuideContent;
import org.cgiar.ccafs.marlo.utils.APConfig;
import org.cgiar.ccafs.marlo.validation.superadmin.ButtonGuideContentValidator;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import javax.inject.Inject;


public class ButtonGuideManagementAction extends BaseAction {

  private static final long serialVersionUID = -793652591843623397L;

  private List<ButtonGuideContent> buttonGuideContents;

  private final ButtonGuideContentManager buttonGuideContentManager;
  private ButtonGuideContentValidator validator;


  @Inject
  public ButtonGuideManagementAction(APConfig config, ButtonGuideContentManager buttonGuideContentManager,
    ButtonGuideContentValidator validator) {
    super(config);
    this.buttonGuideContentManager = buttonGuideContentManager;
    this.validator = validator;
  }

  public List<ButtonGuideContent> getButtonGuideContents() {
    return buttonGuideContents;
  }

  @Override
  public void prepare() throws Exception {

    buttonGuideContents = buttonGuideContentManager.findAll();

    if (this.isHttpPost()) {
      buttonGuideContents.clear();
    }
  }

  @Override
  public String save() {
    if (this.canAccessSuperAdmin()) {
      if (buttonGuideContents != null && !buttonGuideContents.isEmpty()) {

        List<Long> IDs = buttonGuideContents.stream().map(ButtonGuideContent::getId).filter(Objects::nonNull)
          .collect(Collectors.toList());

        buttonGuideContentManager.findAll().stream()
          .filter(activityDB -> activityDB.getId() == null || IDs.contains(activityDB.getId()))
          .map(ButtonGuideContent::getId).forEach(buttonGuideContentManager::deleteButtonGuideContent);

        for (ButtonGuideContent fields : buttonGuideContents) {

          // New Activity
          ButtonGuideContent fieldSave = new ButtonGuideContent();

          if (fields.getId() != null) {
            fieldSave = buttonGuideContentManager.getButtonGuideContentById(fields.getId());
          }
          if (fields.getActionName() != null) {
            fieldSave.setActionName(fields.getActionName());
          }
          if (fields.getContent() != null) {
            fieldSave.setContent(fields.getContent());
          }
          if (fields.getIdentifier() != null) {
            fieldSave.setIdentifier(fields.getIdentifier());
          }
          if (fields.getSectionName() != null) {
            fieldSave.setSectionName(fields.getSectionName());
          }
          buttonGuideContentManager.saveButtonGuideContent(fieldSave);

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

  public void setButtonGuideContents(List<ButtonGuideContent> buttonGuideContents) {
    this.buttonGuideContents = buttonGuideContents;
  }

  @Override
  public void validate() {
    if (save) {
      validator.validate(this, buttonGuideContents);
    }
  }

}