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
import org.cgiar.ccafs.marlo.data.manager.GlobalUnitManager;
import org.cgiar.ccafs.marlo.data.model.GlobalUnit;
import org.cgiar.ccafs.marlo.data.model.PolicyMilestone;
import org.cgiar.ccafs.marlo.data.model.Project;
import org.cgiar.ccafs.marlo.data.model.ProjectPolicy;
import org.cgiar.ccafs.marlo.data.model.ProjectPolicyCrossCuttingMarker;
import org.cgiar.ccafs.marlo.data.model.ProjectPolicyGeographicScope;
import org.cgiar.ccafs.marlo.data.model.ProjectPolicySubIdo;
import org.cgiar.ccafs.marlo.data.model.ProjectSectionStatusEnum;
import org.cgiar.ccafs.marlo.utils.InvalidFieldsMessages;
import org.cgiar.ccafs.marlo.validation.BaseValidator;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;

import javax.inject.Inject;
import javax.inject.Named;

/**
 * @author Hermes Jiménez - CIAT/CCAFS
 */
@Named
public class ProjectPolicyValidator extends BaseValidator {

  private final GlobalUnitManager crpManager;
  private BaseAction baseAction;

  @Inject
  public ProjectPolicyValidator(GlobalUnitManager crpManager) {
    super();
    this.crpManager = crpManager;
  }

  private Path getAutoSaveFilePath(ProjectPolicy policy, long crpID, BaseAction action) {
    GlobalUnit crp = crpManager.getGlobalUnitById(crpID);
    String composedClassName = policy.getClass().getSimpleName();
    String actionFile = ProjectSectionStatusEnum.POLICY.getStatus().replace("/", "_");
    String autoSaveFile = policy.getId() + "_" + composedClassName + "_" + action.getActualPhase().getName() + "_"
      + action.getActualPhase().getYear() + "_" + crp.getAcronym() + "_" + actionFile + ".json";
    return Paths.get(config.getAutoSaveFolder() + autoSaveFile);
  }

  public void validate(BaseAction action, Project project, ProjectPolicy policy, boolean saving) {

    action.setInvalidFields(new HashMap<>());
    baseAction = action;

    if (!saving) {
      Path path = this.getAutoSaveFilePath(policy, action.getCrpID(), action);
      if (path.toFile().exists()) {
        action.addMissingField("draft");
      }
    }

    if (policy.getProjectPolicyInfo(action.getActualPhase()).isRequired()) {
      this.validateProjectPolicy(action, policy);
    }

    if (!action.getFieldErrors().isEmpty()) {
      action.addActionError(action.getText("saving.fields.required"));
    } else if (action.getValidationMessage().length() > 0) {
      action.addActionMessage(
        " " + action.getText("saving.missingFields", new String[] {action.getValidationMessage().toString()}));
    }

    this.saveMissingFields(project, policy, action.getActualPhase().getDescription(), action.getActualPhase().getYear(),
      action.getActualPhase().getUpkeep(), ProjectSectionStatusEnum.POLICIES.getStatus(), action);


  }

  private void validateCrossCuttingMarkers(BaseAction action, ProjectPolicyCrossCuttingMarker crossCuttingMarker,
    int i) {

    // Validate each Cross Cutting Markers
    if (crossCuttingMarker.getRepIndGenderYouthFocusLevel() != null) {
      if (crossCuttingMarker.getRepIndGenderYouthFocusLevel().getId() == null
        || crossCuttingMarker.getRepIndGenderYouthFocusLevel().getId() == -1) {
        action.addMessage(action.getText("CrossCutting Markers"));
        action.addMissingField("policy.crossCuttingMarkers");
        action.getInvalidFields().put("input-policy.crossCuttingMarkers[" + i + "].repIndGenderYouthFocusLevel.id",
          InvalidFieldsMessages.EMPTYFIELD);
      }
    } else {
      action.addMessage(action.getText("CrossCutting Markers"));
      action.addMissingField("policy.crossCuttingMarkers");
      action.getInvalidFields().put("input-policy.crossCuttingMarkers[" + i + "].repIndGenderYouthFocusLevel.id",
        InvalidFieldsMessages.EMPTYFIELD);
    }

  }

  private void validateProjectPolicy(BaseAction action, ProjectPolicy projectPolicy) {

    // Validate Title
    if (!(this.isValidString(projectPolicy.getProjectPolicyInfo(baseAction.getActualPhase()).getTitle())
      && this.wordCount(projectPolicy.getProjectPolicyInfo(baseAction.getActualPhase()).getTitle()) <= 30)) {
      action.addMessage(action.getText("Title"));
      action.addMissingField("projectPolicy.title");
      action.getInvalidFields().put("input-policy.projectPolicyInfo.title", InvalidFieldsMessages.EMPTYFIELD);
    }

    // AR2021 adjustment: Validate description
    if (!(this.isValidString(projectPolicy.getProjectPolicyInfo(baseAction.getActualPhase()).getDescription())
      && this.wordCount(projectPolicy.getProjectPolicyInfo(baseAction.getActualPhase()).getDescription()) <= 30)) {
      action.addMessage(action.getText("Description"));
      action.addMissingField("projectPolicy.description");
      action.getInvalidFields().put("input-policy.projectPolicyInfo.description", InvalidFieldsMessages.EMPTYFIELD);
    }

    // Validate Policy Investment Type
    if (projectPolicy.getProjectPolicyInfo(baseAction.getActualPhase()).getRepIndPolicyInvestimentType() != null) {
      if (projectPolicy.getProjectPolicyInfo(baseAction.getActualPhase()).getRepIndPolicyInvestimentType()
        .getId() == null
        || projectPolicy.getProjectPolicyInfo(baseAction.getActualPhase()).getRepIndPolicyInvestimentType()
          .getId() == -1) {
        action.addMessage(action.getText("Policy Investment Type"));
        action.addMissingField("projectPolicy.repIndPolicyInvestimentType");
        action.getInvalidFields().put("input-policy.projectPolicyInfo.repIndPolicyInvestimentType.id",
          InvalidFieldsMessages.EMPTYFIELD);
      }
    } else {
      action.addMessage(action.getText("Policy Investment Type"));
      action.addMissingField("projectPolicy.repIndPolicyInvestimentType");
      action.getInvalidFields().put("input-policy.projectPolicyInfo.repIndPolicyInvestimentType.id",
        InvalidFieldsMessages.EMPTYFIELD);
    }

    // Validate Organization Type
    /*
     * if (projectPolicy.getProjectPolicyInfo(baseAction.getActualPhase()).getRepIndOrganizationType() != null) {
     * if (projectPolicy.getProjectPolicyInfo(baseAction.getActualPhase()).getRepIndOrganizationType().getId() == null
     * || projectPolicy.getProjectPolicyInfo(baseAction.getActualPhase()).getRepIndOrganizationType().getId() == -1) {
     * action.addMessage(action.getText("Organization Type"));
     * action.addMissingField("projectPolicy.repIndOrganizationType");
     * action.getInvalidFields().put("input-policy.projectPolicyInfo.repIndOrganizationType.id",
     * InvalidFieldsMessages.EMPTYFIELD);
     * }
     * } else {
     * action.addMessage(action.getText("Organization Type"));
     * action.addMissingField("projectPolicy.repIndOrganizationType");
     * action.getInvalidFields().put("input-policy.projectPolicyInfo.repIndOrganizationType.id",
     * InvalidFieldsMessages.EMPTYFIELD);
     * }
     */

    // Validate Evidences or narrative evidences when level 1 is selected

    if (projectPolicy.getProjectPolicyInfo(baseAction.getActualPhase()).getRepIndStageProcess() != null
      && projectPolicy.getProjectPolicyInfo().getRepIndStageProcess().getId() == 3) {

      // Validate Evidences
      if (projectPolicy.getEvidences() == null || projectPolicy.getEvidences().isEmpty()) {
        if (!(this.isValidString(projectPolicy.getProjectPolicyInfo(action.getActualPhase()).getNarrativeEvidence()))) {
          action.addMessage(action.getText("Narrative of Evidence"));
          action.addMissingField("policy.narrative");
          action.getInvalidFields().put("input-policy.projectPolicyInfo.narrativeEvidence",
            InvalidFieldsMessages.EMPTYFIELD);
        }

        // action.addMessage(action.getText("Evidences List"));
        // action.addMissingField("policy.evidence");
        // action.getInvalidFields().put("list-policy.evidences",
        // action.getText(InvalidFieldsMessages.EMPTYLIST, new String[] {"expectedStudyList"}));
      }
    }

    // Validate Maturity Process
    if (projectPolicy.getProjectPolicyInfo(baseAction.getActualPhase()).getRepIndStageProcess() != null)

    {
      if (projectPolicy.getProjectPolicyInfo(baseAction.getActualPhase()).getRepIndStageProcess().getId() == null
        || projectPolicy.getProjectPolicyInfo(baseAction.getActualPhase()).getRepIndStageProcess().getId() == -1) {
        action.addMessage(action.getText("Maturity Process"));
        action.addMissingField("projectPolicy.repIndStageProcess");
        action.getInvalidFields().put("input-policy.projectPolicyInfo.repIndStageProcess.id",
          InvalidFieldsMessages.EMPTYFIELD);
      } else {
        if (projectPolicy.getProjectPolicyInfo(baseAction.getActualPhase()).getRepIndStageProcess().getId() != 3) {
          // Validate Evidences
          if (projectPolicy.getEvidences() == null || projectPolicy.getEvidences().isEmpty()) {
            action.addMessage(action.getText("Evidences List"));
            action.addMissingField("policy.evidence");
            action.getInvalidFields().put("list-policy.evidences",
              action.getText(InvalidFieldsMessages.EMPTYLIST, new String[] {"expectedStudyList"}));
          }
        }
      }
    } else {
      action.addMessage(action.getText("Maturity Process"));
      action.addMissingField("projectPolicy.repIndStageProcess");
      action.getInvalidFields().put("input-policy.projectPolicyInfo.repIndStageProcess.id",
        InvalidFieldsMessages.EMPTYFIELD);
    }

    // Validate Owners
    if (projectPolicy.getOwners() == null || projectPolicy.getOwners().isEmpty()) {
      action.addMessage(action.getText("policyTypes"));
      action.addMissingField("policy.policyOwners");
      action.getInvalidFields().put("list-policy.owners",
        action.getText(InvalidFieldsMessages.EMPTYLIST, new String[] {"policyTypes"}));
    }


    // Validate Crps
    /*
     * if (projectPolicy.getCrps() == null || projectPolicy.getCrps().isEmpty()) {
     * action.addMessage(action.getText("expectedStudyList"));
     * action.addMissingField("policy.contributingCrpsPtfs");
     * action.getInvalidFields().put("list-policy.crps",
     * action.getText(InvalidFieldsMessages.EMPTYLIST, new String[] {"crps"}));
     * }
     */
    if (projectPolicy.getCenters() == null || projectPolicy.getCenters().isEmpty()) {
      action.addMessage(action.getText("centers"));
      action.addMissingField("policy.centers");
      action.getInvalidFields().put("list-policy.centers",
        action.getText(InvalidFieldsMessages.EMPTYLIST, new String[] {"centers"}));
    }

    // Validate Sub-Idos
    if (projectPolicy.getSubIdos() == null || projectPolicy.getSubIdos().isEmpty()) {
      action.addMessage(action.getText("subIdos"));
      action.addMissingField("policy.subIdos");
      action.getInvalidFields().put("list-policy.subIdos",
        action.getText(InvalidFieldsMessages.EMPTYLIST, new String[] {"subIdos"}));
    } else {
      // Validate primary sub-IDO
      if (projectPolicy.getSubIdos().size() > 1) {
        // AR2021 adjustment: validate max. 2
        if (projectPolicy.getSubIdos().size() > 2) {
          action.addMessage(action.getText("subIdos"));
          action.addMissingField("policy.subIdos");
          action.getInvalidFields().put("list-policy.subIdos",
            action.getText(InvalidFieldsMessages.WRONGVALUE, new String[] {"subIdos"}));
        }

        int count = 0;
        for (ProjectPolicySubIdo studySubIdo : projectPolicy.getSubIdos()) {
          if (studySubIdo.getPrimary() != null && studySubIdo.getPrimary()) {
            count++;
          }
        }

        if (count == 0) {
          action.addMessage(action.getText("subIdos"));
          action.addMissingField("policy.subIdos");
          action.getInvalidFields().put("list-policy.subIdos",
            action.getText(InvalidFieldsMessages.EMPTYLIST, new String[] {"subIdos"}));
        }
      }
    }

    // Validate Milestones
    if (projectPolicy.getProjectPolicyInfo(baseAction.getActualPhase()) != null
      && projectPolicy.getProjectPolicyInfo().getHasMilestones() == null) {
      action.addMessage(action.getText("milestoneList"));
      action.addMissingField("policy.milestones");
      action.getInvalidFields().put("list-policy.milestones",
        action.getText(InvalidFieldsMessages.EMPTYLIST, new String[] {"milestones"}));
    }

    if (projectPolicy.getProjectPolicyInfo(baseAction.getActualPhase()) != null
      && projectPolicy.getProjectPolicyInfo().getHasMilestones() != null
      && projectPolicy.getProjectPolicyInfo().getHasMilestones() == true && projectPolicy.getMilestones() != null
      && !projectPolicy.getMilestones().isEmpty()) {
      if (!action.isSelectedPhaseAR2021()) {
        int countPrimaries = 0;
        for (PolicyMilestone policyMilestone : projectPolicy.getMilestones()) {
          if (policyMilestone != null && policyMilestone.getCrpMilestone() != null
            && policyMilestone.getCrpMilestone().getId() != null && policyMilestone.getPrimary() != null
            && policyMilestone.getPrimary().booleanValue() == true) {
            countPrimaries++;
          }
        }

        if (countPrimaries != 1) {
          action.addMessage(action.getText("milestoneList"));
          action.addMissingField("policy.milestones");
          action.getInvalidFields().put("list-policy.milestones",
            action.getText(InvalidFieldsMessages.WRONGVALUE, new String[] {"milestones"}));
        }
      }
    }

    // Validate Cross Cutting
    if (projectPolicy.getCrossCuttingMarkers() == null || projectPolicy.getCrossCuttingMarkers().isEmpty()) {
      action.addMessage(action.getText("crossCuttingMarkers"));
      action.getInvalidFields().put("list-outcome.crossCuttingMarkers",
        action.getText(InvalidFieldsMessages.EMPTYLIST, new String[] {"crossCuttingMarkers"}));
    } else {
      for (int i = 0; i < projectPolicy.getCrossCuttingMarkers().size(); i++) {
        this.validateCrossCuttingMarkers(action, projectPolicy.getCrossCuttingMarkers().get(i), i);
      }

    }

    // Validate Geographic Scope

    boolean haveRegions = false;
    boolean haveCountries = false;

    if (projectPolicy.getGeographicScopes() == null || projectPolicy.getGeographicScopes().isEmpty()) {
      action.addMessage(action.getText("geographicScopes"));
      action.addMissingField("policy.geographicScope");
      action.getInvalidFields().put("list-policy.geographicScopes",
        action.getText(InvalidFieldsMessages.EMPTYLIST, new String[] {"geographicScopes"}));
    } else {
      for (ProjectPolicyGeographicScope projectPolicyGeographicScope : projectPolicy.getGeographicScopes()) {
        if (projectPolicyGeographicScope.getRepIndGeographicScope().getId() == 2) {
          haveRegions = true;
        }
        if (projectPolicyGeographicScope.getRepIndGeographicScope().getId() != 1
          && projectPolicyGeographicScope.getRepIndGeographicScope().getId() != 2) {
          haveCountries = true;
        }
      }
    }


    if (haveRegions) {
      // Validate Regions
      if (projectPolicy.getRegions() == null || projectPolicy.getRegions().isEmpty()) {
        action.addMessage(action.getText("regions"));
        action.addMissingField("policy.regions");
        action.getInvalidFields().put("list-policy.regions",
          action.getText(InvalidFieldsMessages.EMPTYLIST, new String[] {"regions"}));
      }
    }

    if (haveCountries) {
      // Validate Countries
      if (projectPolicy.getCountriesIds() == null || projectPolicy.getCountriesIds().isEmpty()) {
        action.addMessage(action.getText("countries"));
        action.addMissingField("policy.countries");
        action.getInvalidFields().put("input-policy.countriesIds",
          action.getText(InvalidFieldsMessages.EMPTYLIST, new String[] {"countries"}));
      }
    }
  }

}
