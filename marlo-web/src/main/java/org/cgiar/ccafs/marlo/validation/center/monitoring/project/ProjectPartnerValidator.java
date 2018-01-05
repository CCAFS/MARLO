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

package org.cgiar.ccafs.marlo.validation.center.monitoring.project;

import org.cgiar.ccafs.marlo.action.BaseAction;
import org.cgiar.ccafs.marlo.data.manager.GlobalUnitManager;
import org.cgiar.ccafs.marlo.data.model.CenterProgram;
import org.cgiar.ccafs.marlo.data.model.CenterProject;
import org.cgiar.ccafs.marlo.data.model.CenterProjectPartner;
import org.cgiar.ccafs.marlo.data.model.GlobalUnit;
import org.cgiar.ccafs.marlo.data.model.ProjectSectionsEnum;
import org.cgiar.ccafs.marlo.utils.InvalidFieldsMessages;
import org.cgiar.ccafs.marlo.validation.BaseValidator;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

/**
 * @author Hermes Jiménez - CIAT/CCAFS
 */
@Named
public class ProjectPartnerValidator extends BaseValidator {

  // GlobalUnit Manager
  private GlobalUnitManager centerService;

  @Inject
  public ProjectPartnerValidator(GlobalUnitManager centerService) {
    this.centerService = centerService;
  }

  private Path getAutoSaveFilePath(CenterProject project, long centerID) {
    GlobalUnit center = centerService.getGlobalUnitById(centerID);
    String composedClassName = project.getClass().getSimpleName();
    String actionFile = ProjectSectionsEnum.PARTNERS.getStatus().replace("/", "_");
    String autoSaveFile =
      project.getId() + "_" + composedClassName + "_" + center.getAcronym() + "_" + actionFile + ".json";

    return Paths.get(config.getAutoSaveFolder() + autoSaveFile);
  }

  public void validate(BaseAction baseAction, CenterProject project, CenterProgram selectedProgram, boolean saving) {

    baseAction.setInvalidFields(new HashMap<>());

    if (!saving) {
      Path path = this.getAutoSaveFilePath(project, baseAction.getCenterID());

      if (path.toFile().exists()) {
        this.addMissingField("programImpact.action.draft");
      }
    }

    if (!baseAction.getFieldErrors().isEmpty()) {
      baseAction.addActionError(baseAction.getText("saving.fields.required"));
    }

    if (project.getPartners() != null) {
      if (project.getPartners().size() == 0) {
        this.addMessage(baseAction.getText("output.action.partner.required"));
        baseAction.getInvalidFields().put("list-output.partners",
          baseAction.getText(InvalidFieldsMessages.EMPTYLIST, new String[] {"partners"}));
      }
    } else {
      this.addMessage(baseAction.getText("output.action.partner.required"));
      baseAction.getInvalidFields().put("list-output.partners",
        baseAction.getText(InvalidFieldsMessages.EMPTYLIST, new String[] {"partners"}));
    }


    for (int i = 0; i < project.getPartners().size(); i++) {
      CenterProjectPartner partner = project.getPartners().get(i);
      this.validateOutputPartner(baseAction, partner, i);
    }


    this.saveMissingFields(selectedProgram, project, "projectPartners");

  }

  public void validateOutputPartner(BaseAction baseAction, CenterProjectPartner partner, int i) {

    List<String> params = new ArrayList<String>();
    params.add(String.valueOf(i + 1));

    if (partner.getUsers() != null) {
      if (partner.getUsers().size() == 0) {
        this.addMessage(baseAction.getText("output.action.partner.user"));
        baseAction.getInvalidFields().put("list-project.partners[" + i + "].users",
          baseAction.getText(InvalidFieldsMessages.EMPTYLIST, new String[] {"users"}));

      }
    } else {
      this.addMessage(baseAction.getText("output.action.partner.user"));
      baseAction.getInvalidFields().put("list-project.partners[" + i + "].users",
        baseAction.getText(InvalidFieldsMessages.EMPTYLIST, new String[] {"users"}));


    }
  }


}
