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
import org.cgiar.ccafs.marlo.data.manager.ICenterManager;
import org.cgiar.ccafs.marlo.data.model.Center;
import org.cgiar.ccafs.marlo.data.model.CenterProgram;
import org.cgiar.ccafs.marlo.data.model.CenterProject;
import org.cgiar.ccafs.marlo.data.model.CenterProjectFundingSource;
import org.cgiar.ccafs.marlo.data.model.ProjectSectionsEnum;
import org.cgiar.ccafs.marlo.utils.InvalidFieldsMessages;
import org.cgiar.ccafs.marlo.validation.BaseValidator;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;

import com.google.inject.Inject;

/**
 * @author Hermes Jiménez - CIAT/CCAFS
 */
public class CenterProjectDescriptionValidator extends BaseValidator {

  private ICenterManager centerService;

  @Inject
  public CenterProjectDescriptionValidator(ICenterManager centerService) {
    this.centerService = centerService;
  }

  public Boolean bolValue(String value) {
    if (value == null || value.isEmpty() || value.toLowerCase().equals("null")) {
      return null;
    }
    return Boolean.valueOf(value);
  }

  private Path getAutoSaveFilePath(CenterProject project, long centerID) {
    Center center = centerService.getCrpById(centerID);
    String composedClassName = project.getClass().getSimpleName();
    String actionFile = ProjectSectionsEnum.DESCRIPTION.getStatus().replace("/", "_");
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

    this.validateProjectDescription(baseAction, project);

    this.saveMissingFields(selectedProgram, project, "projectDescription");

  }


  public void validateFundingSource(BaseAction baseAction, CenterProjectFundingSource fundingSource, int i) {
    // if (fundingSource.getFundingSourceType() == null) {
    // this.addMessage(
    // baseAction.getText("projectDescription.action.fundingSources.fundingSourceType.id", String.valueOf(i + 1)));
    // baseAction.getInvalidFields().put("input-project.fundingSources[" + i + "].fundingSourceType.id",
    // InvalidFieldsMessages.EMPTYFIELD);
    // } else {
    // if (fundingSource.getFundingSourceType().getId() == null || fundingSource.getFundingSourceType().getId() == -1) {
    // this.addMessage(
    // baseAction.getText("projectDescription.action.fundingSources.fundingSourceType.id", String.valueOf(i + 1)));
    // baseAction.getInvalidFields().put("input-project.fundingSources[" + i + "].fundingSourceType.id",
    // InvalidFieldsMessages.EMPTYFIELD);
    // }
    // }

  }

  public void validateProjectDescription(BaseAction baseAction, CenterProject project) {
    // if (project.getOcsCode() != null) {
    // if (!this.isValidString(project.getOcsCode()) && this.wordCount(project.getOcsCode()) <= 15) {
    // this.addMessage(baseAction.getText("projectDescription.action.ocsCode"));
    // baseAction.getInvalidFields().put("input-project.ocsCode", InvalidFieldsMessages.EMPTYFIELD);
    // }
    // } else {
    // this.addMessage(baseAction.getText("projectDescription.action.ocsCode"));
    // baseAction.getInvalidFields().put("input-project.ocsCode", InvalidFieldsMessages.EMPTYFIELD);
    // }

    if (project.getName() != null) {
      if (!this.isValidString(project.getName()) && this.wordCount(project.getName()) <= 50) {
        this.addMessage(baseAction.getText("projectDescription.action.title"));
        baseAction.getInvalidFields().put("input-project.name", InvalidFieldsMessages.EMPTYFIELD);
      }
    } else {
      this.addMessage(baseAction.getText("projectDescription.action.title"));
      baseAction.getInvalidFields().put("input-project.name", InvalidFieldsMessages.EMPTYFIELD);
    }

    if (project.getDescription() != null) {
      if (!this.isValidString(project.getDescription()) && this.wordCount(project.getDescription()) <= 50) {
        this.addMessage(baseAction.getText("projectDescription.action.description"));
        baseAction.getInvalidFields().put("input-project.description", InvalidFieldsMessages.EMPTYFIELD);
      }
    } else {
      this.addMessage(baseAction.getText("projectDescription.action.description"));
      baseAction.getInvalidFields().put("input-project.description", InvalidFieldsMessages.EMPTYFIELD);
    }

    // if (project.getOriginalDonor() != null) {
    // if (!this.isValidString(project.getOriginalDonor()) && this.wordCount(project.getOriginalDonor()) <= 100) {
    // this.addMessage(baseAction.getText("projectDescription.action.originalDonor"));
    // baseAction.getInvalidFields().put("input-project.originalDonor", InvalidFieldsMessages.EMPTYFIELD);
    // }
    // } else {
    // this.addMessage(baseAction.getText("projectDescription.action.originalDonor"));
    // baseAction.getInvalidFields().put("input-project.originalDonor", InvalidFieldsMessages.EMPTYFIELD);
    // }

    if (project.getsGlobal() != null) {
      if (this.bolValue(project.getsGlobal()) != null) {
        if (!this.bolValue(project.getsGlobal())) {
          if (project.getProjectCountries() == null || project.getProjectCountries().isEmpty()) {
            this.addMessage(baseAction.getText("projectDescription.action.countries"));
            baseAction.getInvalidFields().put("list-project.countries",
              baseAction.getText(InvalidFieldsMessages.EMPTYLIST, new String[] {"CenterProject Countries"}));
          }
        }
      } else {
        this.addMessage(baseAction.getText("projectDescription.action.global"));
        baseAction.getInvalidFields().put("input-project.sGlobal", InvalidFieldsMessages.EMPTYFIELD);
      }
    } else {
      this.addMessage(baseAction.getText("projectDescription.action.global"));
      baseAction.getInvalidFields().put("input-project.sGlobal", InvalidFieldsMessages.EMPTYFIELD);
    }

    if (project.getsRegion() != null) {
      if (this.bolValue(project.getsRegion()) != null) {
        if (this.bolValue(project.getsRegion())) {
          if (project.getProjectRegions() == null || project.getProjectRegions().isEmpty()) {
            this.addMessage(baseAction.getText("projectDescription.action.regions"));
            baseAction.getInvalidFields().put("list-project.regions",
              baseAction.getText(InvalidFieldsMessages.EMPTYLIST, new String[] {"CenterProject Regions"}));
          }
        }
      } else {
        this.addMessage(baseAction.getText("projectDescription.action.region"));
        baseAction.getInvalidFields().put("input-project.sRegion", InvalidFieldsMessages.EMPTYFIELD);
      }
    } else {
      this.addMessage(baseAction.getText("projectDescription.action.region"));
      baseAction.getInvalidFields().put("input-project.sRegion", InvalidFieldsMessages.EMPTYFIELD);
    }

    if (project.getStartDate() == null) {
      this.addMessage(baseAction.getText("projectDescription.action.startDate"));
      baseAction.getInvalidFields().put("input-project.startDate", InvalidFieldsMessages.EMPTYFIELD);
    }

    if (project.getProjectLeader() == null) {
      this.addMessage(baseAction.getText("projectDescription.action.projectLeader"));
      baseAction.getInvalidFields().put("input-project.projectLeader.composedName", InvalidFieldsMessages.EMPTYFIELD);
    } else {
      if (project.getProjectLeader().getId() != null) {
        if (project.getProjectLeader().getId() != null || project.getProjectLeader().getId() != -1) {
          this.addMessage(baseAction.getText("projectDescription.action.projectLeader"));
          baseAction.getInvalidFields().put("input-project.projectLeader.composedName",
            InvalidFieldsMessages.EMPTYFIELD);
        }
      } else {
        this.addMessage(baseAction.getText("projectDescription.action.projectLeader"));
        baseAction.getInvalidFields().put("input-project.projectLeader.composedName", InvalidFieldsMessages.EMPTYFIELD);
      }
    }

    /*
     * Temporally unavailable
     * if (project.getFundingSources() == null || project.getFundingSources().isEmpty()) {
     * this.addMessage(baseAction.getText("projectDescription.action.fundingSources"));
     * baseAction.getInvalidFields().put("list-project.fundingSources",
     * baseAction.getText(InvalidFieldsMessages.EMPTYLIST, new String[] {"Funding source"}));
     * } else {
     * for (int i = 0; i < project.getFundingSources().size(); i++) {
     * this.validateFundingSource(baseAction, project.getFundingSources().get(i), i);
     * }
     * }
     */

    if (project.getOutputs() == null || project.getOutputs().isEmpty()) {
      this.addMessage(baseAction.getText("projectDescription.actio.outputs"));
      baseAction.getInvalidFields().put("list-project.outputs",
        baseAction.getText(InvalidFieldsMessages.EMPTYLIST, new String[] {"Outputs"}));
    }


  }


}
