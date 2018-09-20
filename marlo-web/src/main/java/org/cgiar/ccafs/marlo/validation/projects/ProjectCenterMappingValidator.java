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


package org.cgiar.ccafs.marlo.validation.projects;

import org.cgiar.ccafs.marlo.action.BaseAction;
import org.cgiar.ccafs.marlo.config.APConstants;
import org.cgiar.ccafs.marlo.data.manager.GlobalUnitManager;
import org.cgiar.ccafs.marlo.data.manager.PhaseManager;
import org.cgiar.ccafs.marlo.data.model.GlobalUnit;
import org.cgiar.ccafs.marlo.data.model.Phase;
import org.cgiar.ccafs.marlo.data.model.Project;
import org.cgiar.ccafs.marlo.data.model.SharedProjectSectionStatusEnum;
import org.cgiar.ccafs.marlo.utils.InvalidFieldsMessages;
import org.cgiar.ccafs.marlo.validation.BaseValidator;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;

import javax.inject.Inject;
import javax.inject.Named;

/**
 * @author Christian David Garcia -CIAT/CCAFS
 */
@Named
public class ProjectCenterMappingValidator extends BaseValidator {

  @Inject
  // GlobalUnit Manager
  private GlobalUnitManager crpManager;
  @Inject
  private PhaseManager phaseManager;

  @Inject
  public ProjectCenterMappingValidator() {

  }

  private Path getAutoSaveFilePath(Project project, long crpID, BaseAction action) {
    GlobalUnit crp = crpManager.getGlobalUnitById(crpID);
    String composedClassName = project.getClass().getSimpleName();
    String actionFile = SharedProjectSectionStatusEnum.CENTER_MAPPING.getStatus().replace("/", "_");
    String autoSaveFile = project.getId() + "_" + composedClassName + "_" + action.getActualPhase().getDescription()
      + "_" + action.getActualPhase().getYear() + "_" + crp.getAcronym() + "_" + actionFile + ".json";


    return Paths.get(config.getAutoSaveFolder() + autoSaveFile);
  }

  public void validate(BaseAction action, Project project, boolean saving, long SharedPhase) {
    action.setInvalidFields(new HashMap<>());
    if (!saving) {
      Path path = this.getAutoSaveFilePath(project, action.getCrpID(), action);

      if (path.toFile().exists()) {
        action.addMissingField("draft");
      }
    }

    this.validateDescription(action, project, SharedPhase);


    if (!action.getFieldErrors().isEmpty()) {
      action.addActionError(action.getText("saving.fields.required"));
    } else if (action.getValidationMessage().length() > 0) {
      action.addActionMessage(
        " " + action.getText("saving.missingFields", new String[] {action.getValidationMessage().toString()}));
    }

    this.saveMissingFields(project, action.getActualPhase().getDescription(), action.getActualPhase().getYear(),
      action.getActualPhase().getUpkeep(), SharedProjectSectionStatusEnum.CENTER_MAPPING.getStatus(), action);
  }

  public void validateDescription(BaseAction action, Project project, long SharedPhase) {

    Phase phase = phaseManager.getPhaseById(SharedPhase);

    if (project.getProjecInfoPhase(phase).getLiaisonInstitutionCenter() != null) {
      if (project.getProjecInfoPhase(phase).getLiaisonInstitutionCenter().getId() == -1) {
        action.addMessage(action.getText("project.liaisonInstitutionCenter"));
        action.getInvalidFields().put("input-project.projectInfo.liaisonInstitutionCenter.id",
          InvalidFieldsMessages.EMPTYFIELD);
      }
    } else {
      action.addMessage(action.getText("project.liaisonInstitutionCenter"));
      action.getInvalidFields().put("input-project.projectInfo.liaisonInstitutionCenter.id",
        InvalidFieldsMessages.EMPTYFIELD);
    }


    if (project.getFlagshipValue() == null || project.getFlagshipValue().length() == 0) {
      action.addMessage(action.getText("projectDescription.flagships"));
      action.getInvalidFields().put("input-project.flagshipValue", InvalidFieldsMessages.EMPTYFIELD);
    }

    if (action.getSession().containsKey(APConstants.CRP_HAS_REGIONS)
      && action.getSession().get(APConstants.CRP_HAS_REGIONS).toString().equals("true")) {
      if ((project.getRegionsValue() == null || project.getRegionsValue().length() == 0)
        && (project.getProjecInfoPhase(action.getActualPhase()).getNoRegional() == null
          || project.getProjecInfoPhase(action.getActualPhase()).getNoRegional().booleanValue() == false)) {
        action.addMessage(action.getText("projectDescription.regions"));
        action.getInvalidFields().put("input-project.regionsValue", InvalidFieldsMessages.EMPTYFIELD);
      }
    }

    if (project.getCenterOutcomes() == null || project.getCenterOutcomes().isEmpty()) {
      action.addMessage(action.getText("projectDescription.researchOutcomes"));
      action.getInvalidFields().put("input-project.centerOutcomes", InvalidFieldsMessages.EMPTYFIELD);
    }


  }

}
