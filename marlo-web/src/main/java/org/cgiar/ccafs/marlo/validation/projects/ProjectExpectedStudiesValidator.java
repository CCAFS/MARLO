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
import org.cgiar.ccafs.marlo.data.manager.AllianceLeverManager;
import org.cgiar.ccafs.marlo.data.manager.GlobalUnitManager;
import org.cgiar.ccafs.marlo.data.manager.SDGContributionManager;
import org.cgiar.ccafs.marlo.data.model.AllianceLever;
import org.cgiar.ccafs.marlo.data.model.AllianceLeverOutcome;
import org.cgiar.ccafs.marlo.data.model.GlobalTarget;
import org.cgiar.ccafs.marlo.data.model.GlobalUnit;
import org.cgiar.ccafs.marlo.data.model.Project;
import org.cgiar.ccafs.marlo.data.model.ProjectExpectedStudy;
import org.cgiar.ccafs.marlo.data.model.ProjectExpectedStudyGeographicScope;
import org.cgiar.ccafs.marlo.data.model.ProjectExpectedStudyPartnership;
import org.cgiar.ccafs.marlo.data.model.ProjectExpectedStudyPublication;
import org.cgiar.ccafs.marlo.data.model.ProjectExpectedStudyQuantification;
import org.cgiar.ccafs.marlo.data.model.ProjectExpectedStudyReference;
import org.cgiar.ccafs.marlo.data.model.ProjectExpectedStudySubIdo;
import org.cgiar.ccafs.marlo.data.model.ProjectSectionStatusEnum;
import org.cgiar.ccafs.marlo.data.model.ProjectStatusEnum;
import org.cgiar.ccafs.marlo.data.model.SDGContribution;
import org.cgiar.ccafs.marlo.utils.InvalidFieldsMessages;
import org.cgiar.ccafs.marlo.utils.Patterns;
import org.cgiar.ccafs.marlo.validation.BaseValidator;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

import javax.inject.Inject;
import javax.inject.Named;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Christian Garcia - CIAT/CCAFS
 */
@Named
public class ProjectExpectedStudiesValidator extends BaseValidator {

  private static final Logger LOG = LoggerFactory.getLogger(ProjectExpectedStudiesValidator.class);

  private final GlobalUnitManager crpManager;
  private BaseAction baseAction;
  private final AllianceLeverManager allianceLeverManager;
  private final SDGContributionManager sDGContributionManager;

  @Inject
  public ProjectExpectedStudiesValidator(GlobalUnitManager crpManager, AllianceLeverManager allianceLeverManager,
    SDGContributionManager sDGContributionManager) {
    this.crpManager = crpManager;
    this.allianceLeverManager = allianceLeverManager;
    this.sDGContributionManager = sDGContributionManager;
  }

  private Path getAutoSaveFilePath(ProjectExpectedStudy expectedStudy, long crpID, BaseAction action) {
    GlobalUnit crp = crpManager.getGlobalUnitById(crpID);
    String composedClassName = expectedStudy.getClass().getSimpleName();
    String actionFile = ProjectSectionStatusEnum.EXPECTEDSTUDY.getStatus().replace("/", "_");
    String autoSaveFile = expectedStudy.getId() + "_" + composedClassName + "_" + action.getActualPhase().getName()
      + "_" + action.getActualPhase().getYear() + "_" + crp.getAcronym() + "_" + actionFile + ".json";
    return Paths.get(config.getAutoSaveFolder() + autoSaveFile);
  }

  /**
   * Validate all OICR data
   *
   * @param action base action
   * @param project related project
   * @param projectExpectedStudy An specific projectExpectedStudy
   * @param saving related action
   */
  public void validate(BaseAction action, Project project, ProjectExpectedStudy projectExpectedStudy, boolean saving) {

    action.setInvalidFields(new HashMap<>());

    baseAction = action;

    if (!saving) {
      Path path = this.getAutoSaveFilePath(projectExpectedStudy, action.getCrpID(), action);
      if (path.toFile().exists()) {
        // action.addMissingField("draft");
      }
    }

    this.validateGeneralInformation(action, project, projectExpectedStudy, saving);
    this.validateAllianceAlignment(action, project, projectExpectedStudy, saving);
    this.validateOneCgiarAlignment(action, project, projectExpectedStudy, saving);
    this.validateCommunications(action, project, projectExpectedStudy, saving);

    if (!action.getFieldErrors().isEmpty()) {
      action.addActionError(action.getText("saving.fields.required"));
    } else if (action.getValidationMessage().length() > 0) {
      action.addActionMessage(
        " " + action.getText("saving.missingFields", new String[] {action.getValidationMessage().toString()}));
    }


    this.saveMissingFields(project, projectExpectedStudy, action.getActualPhase().getDescription(),
      action.getActualPhase().getYear(), action.getActualPhase().getUpkeep(),
      ProjectSectionStatusEnum.EXPECTEDSTUDIES.getStatus(), action);


  }


  /**
   * Validate the data of the AllianceAlignment tab
   *
   * @param action base action
   * @param project related project
   * @param projectExpectedStudy An specific projectExpectedStudy
   * @param saving related action
   */
  public void validateAllianceAlignment(BaseAction action, Project project, ProjectExpectedStudy projectExpectedStudy,
    boolean saving) {

    // Validate if the Alliance institution is selected in center section to validate the Alliance Tab
    boolean isAllianceSelected = false;
    if (projectExpectedStudy != null && projectExpectedStudy.getCenters() != null) {
      for (ProjectExpectedStudyPartnership center : projectExpectedStudy.getCenters()) {
        if (center != null && center.getId() != null && center.getId() == APConstants.ALLIANCE_INSTITUTION_ID) {
          isAllianceSelected = true;
        }
      }
    }

    if (isAllianceSelected && projectExpectedStudy != null) {

      // Validate primary levers
      AllianceLever allianceLeverTemp = null;
      if (projectExpectedStudy.getAllianceLever() != null && projectExpectedStudy.getAllianceLever().getId() != null) {
        allianceLeverTemp = allianceLeverManager.getAllianceLeverById(projectExpectedStudy.getAllianceLever().getId());
        projectExpectedStudy.getAllianceLever().setName(allianceLeverTemp.getName());
        projectExpectedStudy.getAllianceLever().setDescription(allianceLeverTemp.getDescription());
      }
      if (projectExpectedStudy.getAllianceLever() == null || (projectExpectedStudy.getAllianceLever() != null
        && projectExpectedStudy.getAllianceLever().getId() == null)) {
        action.addMessage(action.getText("expectedStudy.allianceLever"));
        action.getInvalidFields().put("input-expectedStudy.allianceLever.id", InvalidFieldsMessages.EMPTYFIELD);
      }

      // Validate Other field lever selection
      if (projectExpectedStudy.getAllianceLever() != null && projectExpectedStudy.getAllianceLever().getName() != null
        && projectExpectedStudy.getAllianceLever().getName().equalsIgnoreCase("Other")) {
        action.addMessage(action.getText("expectedStudy.leverComments"));
        action.getInvalidFields().put("input-expectedStudy.allianceLever.leverComments",
          InvalidFieldsMessages.EMPTYFIELD);
      }

      if (projectExpectedStudy.getAllianceLever() != null && projectExpectedStudy.getAllianceLever().getId() != null
        && projectExpectedStudy.getAllianceLever().getName() != null
        && !projectExpectedStudy.getAllianceLever().getName().equalsIgnoreCase("Other")) {


        if (projectExpectedStudy.getAllianceLever().getLeverOutcomes() == null) {
          action.addMessage(action.getText("expectedStudy.leverOutcomes"));
          action.getInvalidFields().put("input-expectedStudy.allianceLever.leverOutcomes[0].id",
            InvalidFieldsMessages.EMPTYFIELD);
        } else {
          // Validate each outcome
          if (projectExpectedStudy.getAllianceLever().getLeverOutcomes() != null) {
            for (AllianceLeverOutcome outcome : projectExpectedStudy.getAllianceLever().getLeverOutcomes()) {
              if (outcome != null && outcome.getId() == null) {
                action.addMessage(action.getText("expectedStudy.leverOutcomes"));
                action.getInvalidFields().put("input-expectedStudy.allianceLever.leverOutcomes[0].id",
                  InvalidFieldsMessages.CHECKBOX);
              }
            }
          } else {
            action.addMessage(action.getText("expectedStudy.leverOutcomes"));
            action.getInvalidFields().put("input-expectedStudy.allianceLever.leverOutcomes[0].id",
              InvalidFieldsMessages.CHECKBOX);
          }
        }

        // Validate SDG contributions selection
        if (projectExpectedStudy.getAllianceLever().getSdgContributions() == null) {
          // Add message for missing SDG contributions
          action.addMessage(action.getText("expectedStudy.sdgContributions"));
          action.getInvalidFields().put("input-expectedStudy.allianceLever.sdgContributions[0].id",
            InvalidFieldsMessages.CHECKBOX);
        } else {
          // Validate each SDG contribution
          for (SDGContribution sdgContribution : projectExpectedStudy.getAllianceLever().getSdgContributions()) {
            if (sdgContribution != null && sdgContribution.getId() == null) {
              // Add message for missing SDG contribution ID
              action.addMessage(action.getText("expectedStudy.sdgContributions"));
              action.getInvalidFields().put("input-expectedStudy.allianceLever.sdgContributions[0].id",
                InvalidFieldsMessages.CHECKBOX);
            }
          }
        }
      }

      // Validate related levers
      List<AllianceLever> allianceLeverList = this.allianceLeverManager.findAll();
      List<SDGContribution> sDGContributionList = new ArrayList<>();

      // get outcomes alliance lever control list
      int allianceLeverSize = 0;

      if (allianceLeverList != null) {
        for (final AllianceLever allianceLeverTmp : allianceLeverList) {
          if (allianceLeverTmp.getAllianceLeverOutcomes() != null) {
            allianceLeverTmp.setOutcomes(new ArrayList<>(allianceLeverTmp.getAllianceLeverOutcomes().stream()
              .filter(o -> (o != null) && (o.getId() != null) && o.isActive()).collect(Collectors.toList())));
          }
        }
        allianceLeverSize = allianceLeverList.size();
      }


      if (projectExpectedStudy.getAllianceLevers() != null && !projectExpectedStudy.getAllianceLevers().isEmpty()) {
        boolean isAllianceLeverSelected = false;
        int allianceLeverIndex = 0;
        for (AllianceLever allianceLever : projectExpectedStudy.getAllianceLevers()) {
          if (allianceLever != null && allianceLever.getId() != null) {
            allianceLeverTemp = allianceLeverManager.getAllianceLeverById(allianceLever.getId());
            allianceLever.setName(allianceLeverTemp.getName());
            allianceLever.setDescription(allianceLeverTemp.getDescription());

            sDGContributionList = this.sDGContributionManager.findSDGcontributionByExpectedPhaseAndLever(
              action.getActualPhase().getId(), projectExpectedStudy.getId(), allianceLeverTemp.getId(), 1);
            allianceLever.setSdgContributions(sDGContributionList);


            // Validate Other field lever selection
            if (allianceLever.getName() != null && allianceLever.getName().equalsIgnoreCase("Other")) {
              action.addMessage(action.getText("expectedStudy.leverComments"));
              action.getInvalidFields().put(
                "input-expectedStudy.allianceLevers[" + allianceLeverIndex + "].leverComments",
                InvalidFieldsMessages.EMPTYFIELD);
            }

            isAllianceLeverSelected = true;

            // Validate SDG contributions selection
            if (allianceLever.getSdgContributions() == null) {
              // Add message for missing SDG contributions
              action.addMessage(action.getText("expectedStudy.sdgContributions"));
              for (int i = 0; i < sDGContributionList.size(); i++) {
                action.getInvalidFields().put(
                  "input-expectedStudy.allianceLevers[" + allianceLeverIndex + "].sdgContributions[" + i + "].id",
                  InvalidFieldsMessages.CHECKBOX);
              }
            } else {
              // Validate each SDG contribution
              for (SDGContribution sdgContribution : allianceLever.getSdgContributions()) {
                if (sdgContribution != null && sdgContribution.getId() == null) {
                  // Add message for missing SDG contribution ID
                  action.addMessage(action.getText("expectedStudy.sdgContributions"));

                  for (int i = 0; i < sDGContributionList.size(); i++) {
                    action.getInvalidFields().put(
                      "input-expectedStudy.allianceLevers[" + allianceLeverIndex + "].sdgContributions[" + i + "].id",
                      InvalidFieldsMessages.CHECKBOX);
                  }
                }
              }
            }

          }
          allianceLeverIndex++;
        }

        // If no alliance lever was selected, mark as missing
        if (!isAllianceLeverSelected) {

          for (int i = 0; i < allianceLeverSize; i++) {
            action.getInvalidFields().put("input-expectedStudy.allianceLevers[" + i + "].id",
              InvalidFieldsMessages.EMPTYFIELD);
          }
          action.addMessage(action.getText("expectedStudy.allianceLevers"));

        }
      } else {
        action.addMessage(action.getText("expectedStudy.allianceLevers"));
        // If the alliance levers list is null or empty, mark as missing
        for (int i = 0; i < allianceLeverSize; i++) {
          action.getInvalidFields().put("input-expectedStudy.allianceLevers[" + i + "].id",
            InvalidFieldsMessages.EMPTYFIELD);
        }
      }
    }
  }


  /**
   * Validate the data of the Communications tab
   *
   * @param action base action
   * @param project related project
   * @param projectExpectedStudy An specific projectExpectedStudy
   * @param saving related action
   */
  public void validateCommunications(BaseAction action, Project project, ProjectExpectedStudy projectExpectedStudy,
    boolean saving) {

    try {

      this.validateProjectExpectedStudyCommunications(projectExpectedStudy, action);


      action.setOicrCommunicationsComplete(true);
      if (this.validateCommunicationsFields(action.getMissingFields().toString())) {
        action.setOicrCommunicationsComplete(false);
      }
    } catch (Exception e) {
      LOG.error(" error in validateCommunications function " + e.getMessage());
    }

  }

  /**
   * Validate that the missing fields contain the fields of the communication section
   *
   * @param missingFields data related to missing fields
   */
  public boolean validateCommunicationsFields(String missingFields) {
    try {
      if (missingFields.contains("study.contacts")) {
        return true;
      }
      return false;
    } catch (Exception e) {
      LOG.error(" error in validateCommunicationsFields function " + e.getMessage());
      return false;
    }
  }

  /**
   * Validate the data of the general information tab
   *
   * @param action base action
   * @param project related project
   * @param projectExpectedStudy An specific projectExpectedStudy
   * @param saving related action
   */
  public void validateGeneralInformation(BaseAction action, Project project, ProjectExpectedStudy projectExpectedStudy,
    boolean saving) {

    this.validateProjectExpectedStudyGeneralInformation(projectExpectedStudy, action);


    if (action.getMissingFields().toString().length() == 0) {
      action.setOicrGeneralInformationComplete(true);
    }


  }


  /**
   * Validate if the current phase is progress
   *
   * @param action base action
   * @param projectExpectedStudy An specific projectExpectedStudy
   * @return validation result
   */
  public boolean validateIsProgressAndNotCompleteStatus(BaseAction action, ProjectExpectedStudy projectExpectedStudy) {
    boolean result = false;
    try {
      if (action.isProgressActive() && projectExpectedStudy.getProjectExpectedStudyInfo().getStatus().getId() != Integer
        .parseInt(ProjectStatusEnum.Complete.getStatusId())) {
        result = true;
      }
      return result;
    } catch (Exception e) {
      LOG.error(" error in validateIsProgressAndNotStatus function [ProjectExpectedStudiesValidator]");
      return result;
    }
  }

  /**
   * Validate the data of the OneCgiarAlignment tab
   *
   * @param action base action
   * @param project related project
   * @param projectExpectedStudy An specific projectExpectedStudy
   * @param saving related action
   */
  public void validateOneCgiarAlignment(BaseAction action, Project project, ProjectExpectedStudy projectExpectedStudy,
    boolean saving) {
    if (projectExpectedStudy.getProjectExpectedStudyInfo(baseAction.getActualPhase()) != null && projectExpectedStudy
      .getProjectExpectedStudyInfo(baseAction.getActualPhase()).getHasCgiarContribution() == null) {
      action.addMessage(action.getText("expectedStudy.hasCgiarContribution"));
      action.getInvalidFields().put("input-expectedStudy.projectExpectedStudyInfo.hasCgiarContribution",
        InvalidFieldsMessages.EMPTYFIELD);
    } else {
      // When the has CGIAR contribution question is true
      if (projectExpectedStudy.getProjectExpectedStudyInfo(baseAction.getActualPhase()) != null
        && projectExpectedStudy.getProjectExpectedStudyInfo(baseAction.getActualPhase()).getHasCgiarContribution()) {

        if (projectExpectedStudy.getImpactArea() == null) {
          action.addMessage(action.getText("expectedStudy.impactArea"));
          action.getInvalidFields().put("input-expectedStudy.impactArea.id", InvalidFieldsMessages.EMPTYFIELD);
        } else {
          if (projectExpectedStudy.getImpactArea() != null && projectExpectedStudy.getImpactArea().getId() == null) {
            action.addMessage(action.getText("expectedStudy.impactArea"));
            action.getInvalidFields().put("input-expectedStudy.impactArea.id", InvalidFieldsMessages.EMPTYFIELD);

            // When the Impact area is not null
          } else if (projectExpectedStudy.getImpactArea() != null
            && projectExpectedStudy.getImpactArea().getId() != null) {
            if (projectExpectedStudy.getImpactArea().getGlobalTargets() == null) {
              action.addMessage(action.getText("expectedStudy.globalTargets"));
              action.getInvalidFields().put("input-expectedStudy.impactArea.globalTargets[0].id",
                InvalidFieldsMessages.EMPTYFIELD);
            } else if (projectExpectedStudy.getImpactArea().getGlobalTargets() != null) {
              int globalTargetIndex = 0;
              for (GlobalTarget globalTarget : projectExpectedStudy.getImpactArea().getGlobalTargets()) {
                if (globalTarget == null || (globalTarget != null && globalTarget.getId() == null)) {
                  action.addMessage(action.getText("expectedStudy.globalTargets"));
                  action.getInvalidFields().put(
                    "input-expectedStudy.impactArea.globalTargets[" + globalTargetIndex + "].id",
                    InvalidFieldsMessages.EMPTYFIELD);
                } 
                globalTargetIndex++;
              }

            }

          }
        }
        // When the CGIAR contribution is false
      } else if (projectExpectedStudy.getProjectExpectedStudyInfo(baseAction.getActualPhase()) != null
        && projectExpectedStudy.getProjectExpectedStudyInfo(baseAction.getActualPhase())
          .getHasCgiarContribution() == false) {
        if (projectExpectedStudy.getProjectExpectedStudyInfo(baseAction.getActualPhase())
          .getReasonNotCgiarContribution() != null
          || (projectExpectedStudy.getProjectExpectedStudyInfo(baseAction.getActualPhase())
            .getReasonNotCgiarContribution() != null
            && projectExpectedStudy.getProjectExpectedStudyInfo(baseAction.getActualPhase())
              .getReasonNotCgiarContribution().isEmpty())) {
          action.addMessage(action.getText("expectedStudy.reasonNotCgiarContribution"));
          action.getInvalidFields().put("input-expectedStudy.projectExpectedStudyInfo.reasonNotCgiarContribution",
            InvalidFieldsMessages.EMPTYFIELD);
        }

      }
    }
  }


  public void validateProjectExpectedStudy(ProjectExpectedStudy projectExpectedStudy, BaseAction action) {

    boolean resultProgessValidate = false;
    resultProgessValidate = this.validateIsProgressAndNotCompleteStatus(action, projectExpectedStudy);

    // Validate Study type
    if (projectExpectedStudy.getProjectExpectedStudyInfo(baseAction.getActualPhase()).getStudyType() != null) {
      if (projectExpectedStudy.getProjectExpectedStudyInfo(baseAction.getActualPhase()).getStudyType().getId() == null
        || projectExpectedStudy.getProjectExpectedStudyInfo(baseAction.getActualPhase()).getStudyType().getId() == -1) {
        action.addMessage(action.getText("Study Type"));
        action.getInvalidFields().put("input-expectedStudy.projectExpectedStudyInfo.studyType.id",
          InvalidFieldsMessages.EMPTYFIELD);
      }
    } else {
      action.addMessage(action.getText("Study Type"));
      action.getInvalidFields().put("input-expectedStudy.projectExpectedStudyInfo.studyType.id",
        InvalidFieldsMessages.EMPTYFIELD);
    }

    // Validate Status
    if (projectExpectedStudy.getProjectExpectedStudyInfo(baseAction.getActualPhase()).getStatus() != null) {
      if (projectExpectedStudy.getProjectExpectedStudyInfo(baseAction.getActualPhase()).getStatus().getId() == -1) {
        action.addMessage(action.getText("Status"));
        action.getInvalidFields().put("input-expectedStudy.projectExpectedStudyInfo.status",
          InvalidFieldsMessages.EMPTYFIELD);
      }
    } else {
      action.addMessage(action.getText("Status"));
      action.getInvalidFields().put("input-expectedStudy.projectExpectedStudyInfo.status",
        InvalidFieldsMessages.EMPTYFIELD);
    }


    // Validate Title
    if ((!this.isValidString(projectExpectedStudy.getProjectExpectedStudyInfo(baseAction.getActualPhase()).getTitle()))
      || this
        .wordCount(projectExpectedStudy.getProjectExpectedStudyInfo(baseAction.getActualPhase()).getTitle()) > 30) {
      action.addMessage(action.getText("Title"));
      action.getInvalidFields().put("input-expectedStudy.projectExpectedStudyInfo.title",
        InvalidFieldsMessages.EMPTYFIELD);
    }


    if (!action.isPOWB()) {

      // Validate Sub-Idos
      if (!action.isAiccra()) {
        if (projectExpectedStudy.getSubIdos() == null || projectExpectedStudy.getSubIdos().isEmpty()) {
          action.addMessage(action.getText("subIdos"));
          action.getInvalidFields().put("list-expectedStudy.subIdos",
            action.getText(InvalidFieldsMessages.EMPTYLIST, new String[] {"subIdos"}));
        } else {
          // Validate primary sub-IDO
          if (projectExpectedStudy.getSubIdos().size() > 1) {
            int count = 0;
            for (ProjectExpectedStudySubIdo studySubIdo : projectExpectedStudy.getSubIdos()) {
              if (studySubIdo.getPrimary() != null && studySubIdo.getPrimary()) {
                count++;
              }
            }

            if (count == 0) {
              action.addMessage(action.getText("subIdos"));
              action.getInvalidFields().put("list-expectedStudy.subIdos",
                action.getText(InvalidFieldsMessages.EMPTYLIST, new String[] {"subIdos"}));
            }
          }
        }
      }


      if (!resultProgessValidate) {
        // validate Milestones
        if (projectExpectedStudy.getProjectExpectedStudyInfo(baseAction.getActualPhase()) != null
          && baseAction.getActualPhase().getName() != null
          && projectExpectedStudy.getProjectExpectedStudyInfo().getStudyType() != null
          && projectExpectedStudy.getProjectExpectedStudyInfo().getStudyType().getId().intValue() == 1) {
          if (projectExpectedStudy.getProjectExpectedStudyInfo(baseAction.getActualPhase()) != null
            && projectExpectedStudy.getProjectExpectedStudyInfo().getHasMilestones() == null) {
            action.addMessage(action.getText("hasMilestones"));
            action.getInvalidFields().put("input-expectedStudy.projectExpectedStudyInfo.hasMilestones",
              InvalidFieldsMessages.EMPTYFIELD);
          }
        }
      }
      /*
       * if (projectExpectedStudy.getProjectExpectedStudyInfo(baseAction.getActualPhase()) != null
       * && baseAction.getActualPhase().getName() != null
       * && projectExpectedStudy.getProjectExpectedStudyInfo().getStudyType() != null
       * && projectExpectedStudy.getProjectExpectedStudyInfo().getStudyType().getId().intValue() == 1) {
       * if (projectExpectedStudy.getProjectExpectedStudyInfo(baseAction.getActualPhase()) != null
       * && (projectExpectedStudy.getProjectExpectedStudyInfo().getHasMilestones() != null
       * && projectExpectedStudy.getProjectExpectedStudyInfo().getHasMilestones() == true
       * && (projectExpectedStudy.getProjectOutcomes() == null
       * || projectExpectedStudy.getProjectOutcomes().isEmpty()))
       * || projectExpectedStudy.getProjectExpectedStudyInfo().getHasMilestones() == null) {
       * action.addMessage(action.getText("projectOutcomes"));
       * //action.addMissingField("expectedStudy.projectOutcomes");
       * action.getInvalidFields().put("list-expectedStudy.projectOutcomes",
       * action.getText(InvalidFieldsMessages.EMPTYLIST, new String[] {"projectOutcomes"}));
       * }
       * }
       */
      if (!resultProgessValidate) {
        if (projectExpectedStudy.getProjectExpectedStudyInfo(baseAction.getActualPhase()) != null
          && baseAction.getActualPhase().getName() != null
          && projectExpectedStudy.getProjectExpectedStudyInfo().getStudyType() != null
          && projectExpectedStudy.getProjectExpectedStudyInfo().getStudyType().getId().intValue() == 1) {
          if (projectExpectedStudy.getProjectExpectedStudyInfo(baseAction.getActualPhase()) != null
            && (projectExpectedStudy.getProjectExpectedStudyInfo().getHasMilestones() != null
              && projectExpectedStudy.getProjectExpectedStudyInfo().getHasMilestones() == true
              && (projectExpectedStudy.getCrpOutcomes() == null || projectExpectedStudy.getCrpOutcomes().isEmpty()))
            || projectExpectedStudy.getProjectExpectedStudyInfo().getHasMilestones() == null) {

            action.addMessage(action.getText("expectedStudy.crpOutcomes"));
            action.getInvalidFields().put("list-expectedStudy.crpOutcomes",
              action.getText(InvalidFieldsMessages.EMPTYLIST, new String[] {"expectedStudy.crpOutcomes"}));
          }
        }
      }

      if (!resultProgessValidate) {
        // Validate Centers
        if (projectExpectedStudy.getProjectExpectedStudyInfo() != null
          && projectExpectedStudy.getProjectExpectedStudyInfo().getStudyType() != null
          && projectExpectedStudy.getProjectExpectedStudyInfo().getStudyType().getId() == 1
          && (projectExpectedStudy.getCenters() == null || projectExpectedStudy.getCenters().isEmpty())) {
          action.addMessage(action.getText("expectedStudy.contributingCenters"));
          action.getInvalidFields().put("list-expectedStudy.centers",
            action.getText(InvalidFieldsMessages.EMPTYLIST, new String[] {"centers"}));
        }
      }


      // Validate Geographic Scope
      boolean haveRegions = false;
      boolean haveCountries = false;

      if (!resultProgessValidate) {
        if (projectExpectedStudy.getGeographicScopes() == null
          || projectExpectedStudy.getGeographicScopes().isEmpty()) {
          action.addMessage(action.getText("geographicScopes"));
          action.getInvalidFields().put("list-expectedStudy.geographicScopes",
            action.getText(InvalidFieldsMessages.EMPTYLIST, new String[] {"geographicScopes"}));
        } else {
          for (ProjectExpectedStudyGeographicScope projectPolicyGeographicScope : projectExpectedStudy
            .getGeographicScopes()) {
            if (projectPolicyGeographicScope.getRepIndGeographicScope().getId() == 2) {
              haveRegions = true;
            }
            if (projectPolicyGeographicScope.getRepIndGeographicScope().getId() != 1
              && projectPolicyGeographicScope.getRepIndGeographicScope().getId() != 2) {
              haveCountries = true;
            }
          }
        }
      }

      if (haveRegions) {
        // Validate Regions
        if (projectExpectedStudy.getStudyRegions() == null || projectExpectedStudy.getStudyRegions().isEmpty()) {
          action.addMessage(action.getText("regions"));
          action.getInvalidFields().put("list-expectedStudy.studyRegions",
            action.getText(InvalidFieldsMessages.EMPTYLIST, new String[] {"regions"}));
        }
      }

      if (haveCountries) {
        // Validate Countries
        if (projectExpectedStudy.getCountriesIds() == null || projectExpectedStudy.getCountriesIds().isEmpty()) {
          action.addMessage(action.getText("countries"));
          action.getInvalidFields().put("input-expectedStudy.countriesIds",
            action.getText(InvalidFieldsMessages.EMPTYLIST, new String[] {"countries"}));
        }
      }


      if (!(baseAction.isReportingActive() || baseAction.isUpKeepActive())) {
        // Validate Srf Targets Selection
        if (projectExpectedStudy.getProjectExpectedStudyInfo(baseAction.getActualPhase()).getIsSrfTarget() == null) {
          action.addMessage(action.getText("targets"));
          action.getInvalidFields().put("input-expectedStudy.projectExpectedStudyInfo.isSrfTarget",
            InvalidFieldsMessages.EMPTYFIELD);
        } else {
          if (projectExpectedStudy.getProjectExpectedStudyInfo(baseAction.getActualPhase()).getIsSrfTarget() != null
            && projectExpectedStudy.getProjectExpectedStudyInfo(baseAction.getActualPhase()).getIsSrfTarget()
              .equals("targetsOptionYes")) {
            // Validate Srf Targets
            if (projectExpectedStudy.getSrfTargets() == null || projectExpectedStudy.getSrfTargets().isEmpty()) {
              action.addMessage(action.getText("targets"));
              action.getInvalidFields().put("list-expectedStudy.srfTargets",
                action.getText(InvalidFieldsMessages.EMPTYLIST, new String[] {"targets"}));
            }
          }

          if (projectExpectedStudy.getProjectExpectedStudyInfo(baseAction.getActualPhase()) != null
            && projectExpectedStudy.getProjectExpectedStudyInfo(baseAction.getActualPhase()).getStudyType() != null
            && projectExpectedStudy.getProjectExpectedStudyInfo(baseAction.getActualPhase()).getStudyType()
              .getId() != 1) {
            // Validate Commissioning Study
            if (!this.isValidString(
              projectExpectedStudy.getProjectExpectedStudyInfo(baseAction.getActualPhase()).getCommissioningStudy())
              && this.wordCount(projectExpectedStudy.getProjectExpectedStudyInfo(baseAction.getActualPhase())
                .getCommissioningStudy()) <= 20) {
              action.addMessage(action.getText("Commissioning Study"));
              action.getInvalidFields().put("input-expectedStudy.projectExpectedStudyInfo.commissioningStudy",
                InvalidFieldsMessages.EMPTYFIELD);
            }

            if ((projectExpectedStudy.getProjectExpectedStudyInfo(baseAction.getActualPhase())
              .getCommissioningStudy() != null
              && projectExpectedStudy.getProjectExpectedStudyInfo(baseAction.getActualPhase()).getCommissioningStudy()
                .isEmpty())
              || (projectExpectedStudy.getProjectExpectedStudyInfo(baseAction.getActualPhase())
                .getCommissioningStudy() == null)) {
              action.addMessage(action.getText("Commissioning Study"));
              action.getInvalidFields().put("input-expectedStudy.projectExpectedStudyInfo.commissioningStudy",
                InvalidFieldsMessages.EMPTYFIELD);
            }
          }
        }


      } else {

        if (projectExpectedStudy.getProjectExpectedStudyInfo(baseAction.getActualPhase()).getStudyType() != null
          && projectExpectedStudy.getProjectExpectedStudyInfo(baseAction.getActualPhase()).getStudyType()
            .getId() == 1) {
          // Validate Outcome/Impact Statement
          /*
           * if ((!this.isValidString(
           * projectExpectedStudy.getProjectExpectedStudyInfo(baseAction.getActualPhase()).getOutcomeImpactStatement()))
           * || this.wordCount(projectExpectedStudy.getProjectExpectedStudyInfo(baseAction.getActualPhase())
           * .getOutcomeImpactStatement()) > 80) {
           * action.addMessage(action.getText("Outcome/Impact Statement"));
           * //action.addMissingField("study.outcomeStatement");
           * action.getInvalidFields().put("input-expectedStudy.projectExpectedStudyInfo.outcomeImpactStatement",
           * InvalidFieldsMessages.EMPTYFIELD);
           * }
           */
          // Validate Comunications Material
          /*
           * if ((!this.isValidString(
           * projectExpectedStudy.getProjectExpectedStudyInfo(baseAction.getActualPhase()).getComunicationsMaterial()))
           * || this.wordCount(projectExpectedStudy.getProjectExpectedStudyInfo(baseAction.getActualPhase())
           * .getComunicationsMaterial()) > 400) {
           * action.addMessage(action.getText("Outcome story for communications"));
           * //action.addMissingField("study.comunicationsMaterial");
           * action.getInvalidFields().put("input-expectedStudy.projectExpectedStudyInfo.comunicationsMaterial",
           * InvalidFieldsMessages.EMPTYFIELD);
           * }
           */

          // Validate CGIAR Innovation
          if (!resultProgessValidate) {
            if (!this.isValidString(
              projectExpectedStudy.getProjectExpectedStudyInfo(baseAction.getActualPhase()).getCgiarInnovation())) {
              action.addMessage(action.getText("CGIAR innovation"));
              action.getInvalidFields().put("input-expectedStudy.projectExpectedStudyInfo.cgiarInnovation",
                InvalidFieldsMessages.EMPTYFIELD);
            }
          }

          // Validate Is Contribution Radio Button (Yes/No)
          if (!action.isAiccra()) {
            if (projectExpectedStudy.getProjectExpectedStudyInfo(baseAction.getActualPhase())
              .getIsContribution() == null) {

              action.addMessage(action.getText("Involve a contribution of the CGIAR"));
              action.getInvalidFields().put("input-expectedStudy.projectExpectedStudyInfo.isContribution",
                InvalidFieldsMessages.EMPTYFIELD);

              // this.validateHidden(projectExpectedStudy, action);

            } else {
              if (projectExpectedStudy.getProjectExpectedStudyInfo(baseAction.getActualPhase()).getIsContribution()) {

                // Validate Policies
                if (projectExpectedStudy.getPolicies() == null || projectExpectedStudy.getPolicies().isEmpty()) {
                  action.addMessage(action.getText("policyList"));
                  action.getInvalidFields().put("list-expectedStudy.policies",
                    action.getText(InvalidFieldsMessages.EMPTYLIST, new String[] {"policyList"}));
                }
              }
            }
          }

          if (!resultProgessValidate) {
            // Validate Stage Study
            if (projectExpectedStudy.getProjectExpectedStudyInfo(baseAction.getActualPhase())
              .getRepIndStageStudy() != null) {
              if (projectExpectedStudy.getProjectExpectedStudyInfo(baseAction.getActualPhase()).getRepIndStageStudy()
                .getId() == null
                || projectExpectedStudy.getProjectExpectedStudyInfo(baseAction.getActualPhase()).getRepIndStageStudy()
                  .getId() == -1) {
                action.addMessage(action.getText("Stage Study"));
                action.getInvalidFields().put("input-expectedStudy.projectExpectedStudyInfo.repIndStageStudy.id",
                  InvalidFieldsMessages.EMPTYFIELD);
              }
            } else {
              action.addMessage(action.getText("Stage Study"));
              action.getInvalidFields().put("input-expectedStudy.projectExpectedStudyInfo.repIndStageStudy.id",
                InvalidFieldsMessages.EMPTYFIELD);
            }
          }

          if (!resultProgessValidate) {

            // Validate Srf Targets Selection
            if (projectExpectedStudy.getProjectExpectedStudyInfo(baseAction.getActualPhase())
              .getIsSrfTarget() == null) {
              action.addMessage(action.getText("targets"));
              action.getInvalidFields().put("input-expectedStudy.projectExpectedStudyInfo.isSrfTarget",
                InvalidFieldsMessages.EMPTYFIELD);
            } else {
              if (projectExpectedStudy.getProjectExpectedStudyInfo(baseAction.getActualPhase()).getIsSrfTarget()
                .equals("targetsOptionYes")) {
                // Validate Srf Targets
                if (projectExpectedStudy.getSrfTargets() == null || projectExpectedStudy.getSrfTargets().isEmpty()) {
                  action.addMessage(action.getText("targets"));
                  action.getInvalidFields().put("list-expectedStudy.srfTargets",
                    action.getText(InvalidFieldsMessages.EMPTYLIST, new String[] {"targets"}));
                }
              }
            }
          }

          if (!resultProgessValidate) {
            // Validate Elaboration Outcomes
            if ((!this.isValidString(projectExpectedStudy.getProjectExpectedStudyInfo(baseAction.getActualPhase())
              .getElaborationOutcomeImpactStatement()))
              || this.wordCount(projectExpectedStudy.getProjectExpectedStudyInfo(baseAction.getActualPhase())
                .getElaborationOutcomeImpactStatement()) > 400) {
              action.addMessage(action.getText("Elaboration Outcome"));
              action.getInvalidFields().put(
                "input-expectedStudy.projectExpectedStudyInfo.elaborationOutcomeImpactStatement",
                InvalidFieldsMessages.EMPTYFIELD);
            }
          }

          // Validate References Cited
          if (action.getActualPhase() != null) {
            if (projectExpectedStudy.getReferences() != null) {
              boolean validReferences = true;
              for (int i = 0; i < projectExpectedStudy.getReferences().size(); i++) {
                ProjectExpectedStudyReference reference = projectExpectedStudy.getReferences().get(i);
                if (reference == null || !this.isValidString(reference.getReference())) {
                  validReferences = false;
                  action.addMessage(action.getText("References Cited"));
                  action.getInvalidFields().put("input-expectedStudy.references[" + i + "].reference",
                    InvalidFieldsMessages.EMPTYFIELD);
                }


                if (reference != null
                  && (reference.getLink() == null || !Patterns.WEB_URL.matcher(reference.getLink()).find())) {
                  validReferences = false;
                }
              }

              if (!validReferences) {
                action.addMessage(action.getText("References Cited"));
                action.getInvalidFields().put("expectedStudy.projectExpectedStudyInfo.referencesText",
                  InvalidFieldsMessages.EMPTYFIELD);
              }
            } else {
              action.addMessage(action.getText("References Cited"));
              action.getInvalidFields().put("expectedStudy.projectExpectedStudyInfo.referencesText",
                InvalidFieldsMessages.EMPTYFIELD);
            }
          }
          /*
           * if (!this.isValidString(
           * projectExpectedStudy.getProjectExpectedStudyInfo(baseAction.getActualPhase()).getReferencesText())) {
           * action.addMessage(action.getText("References Cited"));
           * //action.addMissingField("study.referencesCited");
           * action.getInvalidFields().put("input-expectedStudy.projectExpectedStudyInfo.referencesText",
           * InvalidFieldsMessages.EMPTYFIELD);
           * }
           */
          // Validate Describe Gender
          if (projectExpectedStudy.getProjectExpectedStudyInfo(baseAction.getActualPhase()).getGenderLevel() != null
            && (projectExpectedStudy.getProjectExpectedStudyInfo(baseAction.getActualPhase()).getGenderLevel()
              .getId() != 1
              && projectExpectedStudy.getProjectExpectedStudyInfo(baseAction.getActualPhase()).getGenderLevel()
                .getId() != 4)) {
            if (!this.isValidString(
              projectExpectedStudy.getProjectExpectedStudyInfo(baseAction.getActualPhase()).getDescribeGender())
              && this.wordCount(projectExpectedStudy.getProjectExpectedStudyInfo(baseAction.getActualPhase())
                .getDescribeGender()) <= 100) {
              action.addMessage(action.getText("Describe Gender"));
              action.getInvalidFields().put("input-expectedStudy.projectExpectedStudyInfo.describeGender",
                InvalidFieldsMessages.EMPTYFIELD);
            }
          }

          // Validate Describe Youth
          if (projectExpectedStudy.getProjectExpectedStudyInfo(baseAction.getActualPhase()).getYouthLevel() != null
            && (projectExpectedStudy.getProjectExpectedStudyInfo(baseAction.getActualPhase()).getYouthLevel()
              .getId() != 1
              && projectExpectedStudy.getProjectExpectedStudyInfo(baseAction.getActualPhase()).getYouthLevel()
                .getId() != 4)) {
            if (!this.isValidString(
              projectExpectedStudy.getProjectExpectedStudyInfo(baseAction.getActualPhase()).getDescribeYouth())
              && this.wordCount(projectExpectedStudy.getProjectExpectedStudyInfo(baseAction.getActualPhase())
                .getDescribeYouth()) <= 100) {
              action.addMessage(action.getText("Describe Youth"));
              action.getInvalidFields().put("input-expectedStudy.projectExpectedStudyInfo.describeYouth",
                InvalidFieldsMessages.EMPTYFIELD);
            }
          }
          // Validate Describe Capdev
          if (projectExpectedStudy.getProjectExpectedStudyInfo(baseAction.getActualPhase()).getCapdevLevel() != null
            && (projectExpectedStudy.getProjectExpectedStudyInfo(baseAction.getActualPhase()).getCapdevLevel()
              .getId() != 1
              && projectExpectedStudy.getProjectExpectedStudyInfo(baseAction.getActualPhase()).getCapdevLevel()
                .getId() != 4)) {
            if (!this.isValidString(
              projectExpectedStudy.getProjectExpectedStudyInfo(baseAction.getActualPhase()).getDescribeCapdev())
              && this.wordCount(projectExpectedStudy.getProjectExpectedStudyInfo(baseAction.getActualPhase())
                .getDescribeCapdev()) <= 100) {
              action.addMessage(action.getText("Describe Capdev"));
              action.getInvalidFields().put("input-expectedStudy.projectExpectedStudyInfo.describeCapdev",
                InvalidFieldsMessages.EMPTYFIELD);
            }
          }

          // Validate Describe ClimateChange
          if (projectExpectedStudy.getProjectExpectedStudyInfo(baseAction.getActualPhase())
            .getClimateChangeLevel() != null
            && (projectExpectedStudy.getProjectExpectedStudyInfo(baseAction.getActualPhase()).getClimateChangeLevel()
              .getId() != 1
              && projectExpectedStudy.getProjectExpectedStudyInfo(baseAction.getActualPhase()).getClimateChangeLevel()
                .getId() != 4)) {
            if (!this.isValidString(
              projectExpectedStudy.getProjectExpectedStudyInfo(baseAction.getActualPhase()).getDescribeClimateChange())
              && this.wordCount(projectExpectedStudy.getProjectExpectedStudyInfo(baseAction.getActualPhase())
                .getDescribeClimateChange()) <= 100) {
              action.addMessage(action.getText("Describe Climate Change"));
              action.getInvalidFields().put("input-expectedStudy.projectExpectedStudyInfo.describeClimateChange",
                InvalidFieldsMessages.EMPTYFIELD);
            }
          }

          if (!resultProgessValidate) {
            // Validate Contacts
            if (!this.isValidString(
              projectExpectedStudy.getProjectExpectedStudyInfo(baseAction.getActualPhase()).getContacts())) {
              action.addMessage(action.getText("Contacts"));
              // action.addMissingField("study.contacts");
              action.getInvalidFields().put("input-expectedStudy.projectExpectedStudyInfo.contacts",
                InvalidFieldsMessages.EMPTYFIELD);
            }
          }


          // Validate Gender Radio Button
          if (projectExpectedStudy.getProjectExpectedStudyInfo(baseAction.getActualPhase()).getGenderLevel() == null) {
            action.addMessage(action.getText("Gender Relevance"));
            action.getInvalidFields().put("input-expectedStudy.projectExpectedStudyInfo.genderLevel.id",
              InvalidFieldsMessages.EMPTYFIELD);
          }


          // Validate Youth Radio Button
          if (projectExpectedStudy.getProjectExpectedStudyInfo(baseAction.getActualPhase()).getYouthLevel() == null) {
            action.addMessage(action.getText("Youth Relevance"));
            action.getInvalidFields().put("input-expectedStudy.projectExpectedStudyInfo.youthLevel.id",
              InvalidFieldsMessages.EMPTYFIELD);
          }


          // Validate Capdev Radio Button
          if (projectExpectedStudy.getProjectExpectedStudyInfo(baseAction.getActualPhase()).getCapdevLevel() == null) {
            action.addMessage(action.getText("Capdev Relevance"));
            action.getInvalidFields().put("input-expectedStudy.projectExpectedStudyInfo.capdevLevel.id",
              InvalidFieldsMessages.EMPTYFIELD);
          }

          // Validate Quantification
          if (projectExpectedStudy.getQuantifications() != null) {
            for (int i = 0; i < projectExpectedStudy.getQuantifications().size(); i++) {
              this.validateQuantifications(action, projectExpectedStudy.getQuantifications().get(i), i);
            }
          }

          // REMOVED FOR AR 2020
          // Validate Evidence Tag
          /*
           * if (projectExpectedStudy.getProjectExpectedStudyInfo(baseAction.getActualPhase()).getEvidenceTag() == null)
           * {
           * action.addMessage(action.getText("Evidence Tag"));
           * //action.addMissingField("study.evidenceTag");
           * action.getInvalidFields().put("input-expectedStudy.projectExpectedStudyInfo.evidenceTag.id",
           * InvalidFieldsMessages.EMPTYFIELD);
           * }
           */

        } else {
          // Validate Srf Targets Selection
          if (projectExpectedStudy.getProjectExpectedStudyInfo(baseAction.getActualPhase()).getIsSrfTarget() == null) {
            action.addMessage(action.getText("targets"));
            action.getInvalidFields().put("input-expectedStudy.projectExpectedStudyInfo.isSrfTarget",
              InvalidFieldsMessages.EMPTYFIELD);
          } else {
            if (projectExpectedStudy.getProjectExpectedStudyInfo(baseAction.getActualPhase()).getIsSrfTarget()
              .equals("targetsOptionYes")) {
              // Validate Srf Targets
              if (projectExpectedStudy.getSrfTargets() == null || projectExpectedStudy.getSrfTargets().isEmpty()) {
                action.addMessage(action.getText("targets"));
                action.getInvalidFields().put("list-expectedStudy.srfTargets",
                  action.getText(InvalidFieldsMessages.EMPTYLIST, new String[] {"targets"}));
              }
            }
          }


          // Validate Comments (TopLevel)
          if (!this.isValidString(
            projectExpectedStudy.getProjectExpectedStudyInfo(baseAction.getActualPhase()).getTopLevelComments())) {
            action.addMessage(action.getText("Comments"));
            action.getInvalidFields().put("input-expectedStudy.projectExpectedStudyInfo.topLevelComments",
              InvalidFieldsMessages.EMPTYFIELD);
          }

          // Validate Commissioning Study
          if (projectExpectedStudy.getProjectExpectedStudyInfo(baseAction.getActualPhase()) != null
            && projectExpectedStudy.getProjectExpectedStudyInfo(baseAction.getActualPhase()).getStudyType() != null
            && projectExpectedStudy.getProjectExpectedStudyInfo(baseAction.getActualPhase()).getStudyType()
              .getId() != 1) {
            if (!this.isValidString(
              projectExpectedStudy.getProjectExpectedStudyInfo(baseAction.getActualPhase()).getCommissioningStudy())
              && this.wordCount(projectExpectedStudy.getProjectExpectedStudyInfo(baseAction.getActualPhase())
                .getCommissioningStudy()) <= 20) {
              action.addMessage(action.getText("Commissioning Study"));
              action.getInvalidFields().put("input-expectedStudy.projectExpectedStudyInfo.commissioningStudy",
                InvalidFieldsMessages.EMPTYFIELD);
            }
          }
        }
      }
    }
  }


  public void validateProjectExpectedStudyCommunications(ProjectExpectedStudy projectExpectedStudy, BaseAction action) {

    boolean resultProgessValidate = false;
    resultProgessValidate = this.validateIsProgressAndNotCompleteStatus(action, projectExpectedStudy);

    if (!action.isPOWB()) {

      if (baseAction.isReportingActive() || baseAction.isUpKeepActive()) {
        if (projectExpectedStudy.getProjectExpectedStudyInfo(baseAction.getActualPhase()).getStudyType() != null
          && projectExpectedStudy.getProjectExpectedStudyInfo(baseAction.getActualPhase()).getStudyType()
            .getId() == 1) {

          if (!resultProgessValidate) {
            // Validate Contacts
            if (!this.isValidString(
              projectExpectedStudy.getProjectExpectedStudyInfo(baseAction.getActualPhase()).getContacts())) {
              action.addMessage(action.getText("Contacts"));
              action.getInvalidFields().put("input-expectedStudy.projectExpectedStudyInfo.contacts",
                InvalidFieldsMessages.EMPTYFIELD);
            }
          }

        }
      }
    }

    // Validate publications
    int index = 0;
    if (projectExpectedStudy.getPublications() == null
      || (projectExpectedStudy.getPublications() != null && projectExpectedStudy.getPublications().isEmpty())) {
      action.addMessage(action.getText("expectedStudy.publications.name"));
      action.addMessage(action.getText("expectedStudy.publications.affiliation"));
      action.addMessage(action.getText("expectedStudy.publications.position"));

      action.getInvalidFields().put("input-expectedStudy.publications[0].name", InvalidFieldsMessages.EMPTYFIELD);
      action.getInvalidFields().put("input-expectedStudy.publications[0].affiliation",
        InvalidFieldsMessages.EMPTYFIELD);
      action.getInvalidFields().put("input-expectedStudy.publications[0].position", InvalidFieldsMessages.EMPTYFIELD);
    } else {
      if (projectExpectedStudy.getPublications() != null && !projectExpectedStudy.getPublications().isEmpty()) {
        for (ProjectExpectedStudyPublication publication : projectExpectedStudy.getPublications()) {
          if (publication.getName() == null || (publication.getName() != null && publication.getName().isEmpty())) {
            action.addMessage(action.getText("expectedStudy.publications[" + index + "].name"));
            action.getInvalidFields().put("input-expectedStudy.publications[" + index + "].name",
              InvalidFieldsMessages.EMPTYFIELD);
          }

          if (publication.getPosition() == null
            || (publication.getPosition() != null && publication.getPosition().isEmpty())) {
            action.addMessage(action.getText("expectedStudy.publications[" + index + "].position"));
            action.getInvalidFields().put("input-expectedStudy.publications[" + index + "].position",
              InvalidFieldsMessages.EMPTYFIELD);

          }
          if (publication.getAffiliation() == null
            || (publication.getAffiliation() != null && publication.getAffiliation().isEmpty())) {
            action.addMessage(action.getText("expectedStudy.publications[" + index + "].affiliation"));
            action.getInvalidFields().put("input-expectedStudy.publications[" + index + "].affiliation",
              InvalidFieldsMessages.EMPTYFIELD);
          }
          index++;
        }
      }
    }

  }

  public void validateProjectExpectedStudyGeneralInformation(ProjectExpectedStudy projectExpectedStudy,
    BaseAction action) {

    boolean resultProgessValidate = false;
    resultProgessValidate = this.validateIsProgressAndNotCompleteStatus(action, projectExpectedStudy);

    // Validate Study type
    if (projectExpectedStudy.getProjectExpectedStudyInfo(baseAction.getActualPhase()).getStudyType() != null) {
      if (projectExpectedStudy.getProjectExpectedStudyInfo(baseAction.getActualPhase()).getStudyType().getId() == null
        || projectExpectedStudy.getProjectExpectedStudyInfo(baseAction.getActualPhase()).getStudyType().getId() == -1) {
        action.addMessage(action.getText("Study Type"));
        action.getInvalidFields().put("input-expectedStudy.projectExpectedStudyInfo.studyType.id",
          InvalidFieldsMessages.EMPTYFIELD);
      }
    } else {
      action.addMessage(action.getText("Study Type"));
      action.getInvalidFields().put("input-expectedStudy.projectExpectedStudyInfo.studyType.id",
        InvalidFieldsMessages.EMPTYFIELD);
    }

    // Validate Status
    if (projectExpectedStudy.getProjectExpectedStudyInfo(baseAction.getActualPhase()).getStatus() != null) {
      if (projectExpectedStudy.getProjectExpectedStudyInfo(baseAction.getActualPhase()).getStatus().getId() == -1) {
        action.addMessage(action.getText("Status"));
        action.getInvalidFields().put("input-expectedStudy.projectExpectedStudyInfo.status",
          InvalidFieldsMessages.EMPTYFIELD);
      }
    } else {
      action.addMessage(action.getText("Status"));
      action.getInvalidFields().put("input-expectedStudy.projectExpectedStudyInfo.status",
        InvalidFieldsMessages.EMPTYFIELD);
    }

    // Validate Alliance OICR ID - only mandatory to AWPB phases
    if (action.isAWPBActive()) {
      if ((!this
        .isValidString(projectExpectedStudy.getProjectExpectedStudyInfo(baseAction.getActualPhase()).getAllianceOicr()))
        || this.wordCount(
          projectExpectedStudy.getProjectExpectedStudyInfo(baseAction.getActualPhase()).getAllianceOicr()) > 30) {
        action.addMessage(action.getText("AllianceOicr"));
        action.getInvalidFields().put("input-expectedStudy.projectExpectedStudyInfo.allianceOicr",
          InvalidFieldsMessages.EMPTYFIELD);
      }
    }

    // Validate Tag as

    /*
     * if (projectExpectedStudy.getProjectExpectedStudyInfo(baseAction.getActualPhase()).getTag() == null
     * || projectExpectedStudy.getProjectExpectedStudyInfo(baseAction.getActualPhase()).getTag().getId() == -1) {
     * action.addMessage(action.getText("tag"));
     * //action.addMissingField("study.tag");
     * action.getInvalidFields().put("input-expectedStudy.projectExpectedStudyInfo.tag.id",
     * InvalidFieldsMessages.EMPTYFIELD);
     * }
     */


    // Validate Title
    if ((!this.isValidString(projectExpectedStudy.getProjectExpectedStudyInfo(baseAction.getActualPhase()).getTitle()))
      || this
        .wordCount(projectExpectedStudy.getProjectExpectedStudyInfo(baseAction.getActualPhase()).getTitle()) > 30) {
      action.addMessage(action.getText("Title"));
      action.getInvalidFields().put("input-expectedStudy.projectExpectedStudyInfo.title",
        InvalidFieldsMessages.EMPTYFIELD);
    }


    if (!action.isPOWB()) {

      // Validate Sub-Idos
      if (!action.isAiccra()) {
        if (projectExpectedStudy.getSubIdos() == null || projectExpectedStudy.getSubIdos().isEmpty()) {
          action.addMessage(action.getText("subIdos"));
          action.getInvalidFields().put("list-expectedStudy.subIdos",
            action.getText(InvalidFieldsMessages.EMPTYLIST, new String[] {"subIdos"}));
        } else {
          // Validate primary sub-IDO
          if (projectExpectedStudy.getSubIdos().size() > 1) {
            int count = 0;
            for (ProjectExpectedStudySubIdo studySubIdo : projectExpectedStudy.getSubIdos()) {
              if (studySubIdo.getPrimary() != null && studySubIdo.getPrimary()) {
                count++;
              }
            }

            if (count == 0) {
              action.addMessage(action.getText("subIdos"));
              action.getInvalidFields().put("list-expectedStudy.subIdos",
                action.getText(InvalidFieldsMessages.EMPTYLIST, new String[] {"subIdos"}));
            }
          }
        }
      }


      if (!resultProgessValidate) {
        // validate Milestones
        if (projectExpectedStudy.getProjectExpectedStudyInfo(baseAction.getActualPhase()) != null
          && baseAction.getActualPhase().getName() != null
          && projectExpectedStudy.getProjectExpectedStudyInfo().getStudyType() != null
          && projectExpectedStudy.getProjectExpectedStudyInfo().getStudyType().getId().intValue() == 1) {
          if (projectExpectedStudy.getProjectExpectedStudyInfo(baseAction.getActualPhase()) != null
            && projectExpectedStudy.getProjectExpectedStudyInfo().getHasMilestones() == null) {
            action.addMessage(action.getText("hasMilestones"));
            action.getInvalidFields().put("input-expectedStudy.projectExpectedStudyInfo.hasMilestones",
              InvalidFieldsMessages.EMPTYFIELD);
          }
        }
      }

      if (!resultProgessValidate) {
        if (projectExpectedStudy.getProjectExpectedStudyInfo(baseAction.getActualPhase()) != null
          && baseAction.getActualPhase().getName() != null
          && projectExpectedStudy.getProjectExpectedStudyInfo().getStudyType() != null
          && projectExpectedStudy.getProjectExpectedStudyInfo().getStudyType().getId().intValue() == 1) {
          if (projectExpectedStudy.getProjectExpectedStudyInfo(baseAction.getActualPhase()) != null
            && (projectExpectedStudy.getProjectExpectedStudyInfo().getHasMilestones() != null
              && projectExpectedStudy.getProjectExpectedStudyInfo().getHasMilestones() == true
              && (projectExpectedStudy.getCrpOutcomes() == null || projectExpectedStudy.getCrpOutcomes().isEmpty()))
            || projectExpectedStudy.getProjectExpectedStudyInfo().getHasMilestones() == null) {

            action.addMessage(action.getText("expectedStudy.crpOutcomes"));
            action.getInvalidFields().put("list-expectedStudy.crpOutcomes",
              action.getText(InvalidFieldsMessages.EMPTYLIST, new String[] {"expectedStudy.crpOutcomes"}));
          }
        }
      }

      if (!resultProgessValidate) {
        // Validate Centers
        if (projectExpectedStudy.getProjectExpectedStudyInfo() != null
          && projectExpectedStudy.getProjectExpectedStudyInfo().getStudyType() != null
          && projectExpectedStudy.getProjectExpectedStudyInfo().getStudyType().getId() == 1
          && (projectExpectedStudy.getCenters() == null || projectExpectedStudy.getCenters().isEmpty())) {
          action.addMessage(action.getText("expectedStudy.contributingCenters"));
          action.getInvalidFields().put("list-expectedStudy.centers",
            action.getText(InvalidFieldsMessages.EMPTYLIST, new String[] {"centers"}));
        }
      }


      // Validate Geographic Scope
      boolean haveRegions = false;
      boolean haveCountries = false;

      if (!resultProgessValidate) {
        if (projectExpectedStudy.getGeographicScopes() == null
          || projectExpectedStudy.getGeographicScopes().isEmpty()) {
          action.addMessage(action.getText("geographicScopes"));
          action.getInvalidFields().put("list-expectedStudy.geographicScopes",
            action.getText(InvalidFieldsMessages.EMPTYLIST, new String[] {"geographicScopes"}));
        } else {
          for (ProjectExpectedStudyGeographicScope projectPolicyGeographicScope : projectExpectedStudy
            .getGeographicScopes()) {
            if (projectPolicyGeographicScope.getRepIndGeographicScope().getId() == 2) {
              haveRegions = true;
            }
            if (projectPolicyGeographicScope.getRepIndGeographicScope().getId() != 1
              && projectPolicyGeographicScope.getRepIndGeographicScope().getId() != 2) {
              haveCountries = true;
            }
          }
        }
      }

      if (haveRegions) {
        // Validate Regions
        if (projectExpectedStudy.getStudyRegions() == null || projectExpectedStudy.getStudyRegions().isEmpty()) {
          action.addMessage(action.getText("regions"));
          action.getInvalidFields().put("list-expectedStudy.studyRegions",
            action.getText(InvalidFieldsMessages.EMPTYLIST, new String[] {"regions"}));
        }
      }

      if (haveCountries) {
        // Validate Countries
        if (projectExpectedStudy.getCountriesIds() == null || projectExpectedStudy.getCountriesIds().isEmpty()) {
          action.addMessage(action.getText("countries"));
          action.getInvalidFields().put("input-expectedStudy.countriesIds",
            action.getText(InvalidFieldsMessages.EMPTYLIST, new String[] {"countries"}));
        }
      }


      if (!(baseAction.isReportingActive() || baseAction.isUpKeepActive())) {
        // Validate Srf Targets Selection
        if (projectExpectedStudy.getProjectExpectedStudyInfo(baseAction.getActualPhase()).getIsSrfTarget() == null) {
          action.addMessage(action.getText("targets"));
          action.getInvalidFields().put("input-expectedStudy.projectExpectedStudyInfo.isSrfTarget",
            InvalidFieldsMessages.EMPTYFIELD);
        } else {
          if (projectExpectedStudy.getProjectExpectedStudyInfo(baseAction.getActualPhase()).getIsSrfTarget() != null
            && projectExpectedStudy.getProjectExpectedStudyInfo(baseAction.getActualPhase()).getIsSrfTarget()
              .equals("targetsOptionYes")) {
            // Validate Srf Targets
            if (projectExpectedStudy.getSrfTargets() == null || projectExpectedStudy.getSrfTargets().isEmpty()) {
              action.addMessage(action.getText("targets"));
              action.getInvalidFields().put("list-expectedStudy.srfTargets",
                action.getText(InvalidFieldsMessages.EMPTYLIST, new String[] {"targets"}));
            }
          }

          if (projectExpectedStudy.getProjectExpectedStudyInfo(baseAction.getActualPhase()) != null
            && projectExpectedStudy.getProjectExpectedStudyInfo(baseAction.getActualPhase()).getStudyType() != null
            && projectExpectedStudy.getProjectExpectedStudyInfo(baseAction.getActualPhase()).getStudyType()
              .getId() != 1) {
            // Validate Commissioning Study
            if (!this.isValidString(
              projectExpectedStudy.getProjectExpectedStudyInfo(baseAction.getActualPhase()).getCommissioningStudy())
              && this.wordCount(projectExpectedStudy.getProjectExpectedStudyInfo(baseAction.getActualPhase())
                .getCommissioningStudy()) <= 20) {
              action.addMessage(action.getText("Commissioning Study"));
              action.getInvalidFields().put("input-expectedStudy.projectExpectedStudyInfo.commissioningStudy",
                InvalidFieldsMessages.EMPTYFIELD);
            }

            if ((projectExpectedStudy.getProjectExpectedStudyInfo(baseAction.getActualPhase())
              .getCommissioningStudy() != null
              && projectExpectedStudy.getProjectExpectedStudyInfo(baseAction.getActualPhase()).getCommissioningStudy()
                .isEmpty())
              || (projectExpectedStudy.getProjectExpectedStudyInfo(baseAction.getActualPhase())
                .getCommissioningStudy() == null)) {
              action.addMessage(action.getText("Commissioning Study"));
              action.getInvalidFields().put("input-expectedStudy.projectExpectedStudyInfo.commissioningStudy",
                InvalidFieldsMessages.EMPTYFIELD);
            }
          }
        }


      } else {

        if (projectExpectedStudy.getProjectExpectedStudyInfo(baseAction.getActualPhase()).getStudyType() != null
          && projectExpectedStudy.getProjectExpectedStudyInfo(baseAction.getActualPhase()).getStudyType()
            .getId() == 1) {


          // Validate CGIAR Innovation
          if (!resultProgessValidate) {
            if (!this.isValidString(
              projectExpectedStudy.getProjectExpectedStudyInfo(baseAction.getActualPhase()).getCgiarInnovation())) {
              action.addMessage(action.getText("CGIAR innovation"));
              action.getInvalidFields().put("input-expectedStudy.projectExpectedStudyInfo.cgiarInnovation",
                InvalidFieldsMessages.EMPTYFIELD);
            }
          }

          // Validate Is Contribution Radio Button (Yes/No)
          if (!action.isAiccra()) {
            if (projectExpectedStudy.getProjectExpectedStudyInfo(baseAction.getActualPhase())
              .getIsContribution() == null) {

              action.addMessage(action.getText("Involve a contribution of the CGIAR"));
              action.getInvalidFields().put("input-expectedStudy.projectExpectedStudyInfo.isContribution",
                InvalidFieldsMessages.EMPTYFIELD);

            } else {
              if (projectExpectedStudy.getProjectExpectedStudyInfo(baseAction.getActualPhase()).getIsContribution()) {

                // Validate Policies
                if (projectExpectedStudy.getPolicies() == null || projectExpectedStudy.getPolicies().isEmpty()) {
                  action.addMessage(action.getText("policyList"));
                  action.getInvalidFields().put("list-expectedStudy.policies",
                    action.getText(InvalidFieldsMessages.EMPTYLIST, new String[] {"policyList"}));
                }
              }
            }
          }

          if (!resultProgessValidate) {
            // Validate Stage Study
            if (projectExpectedStudy.getProjectExpectedStudyInfo(baseAction.getActualPhase())
              .getRepIndStageStudy() != null) {
              if (projectExpectedStudy.getProjectExpectedStudyInfo(baseAction.getActualPhase()).getRepIndStageStudy()
                .getId() == null
                || projectExpectedStudy.getProjectExpectedStudyInfo(baseAction.getActualPhase()).getRepIndStageStudy()
                  .getId() == -1) {
                action.addMessage(action.getText("Stage Study"));
                action.getInvalidFields().put("input-expectedStudy.projectExpectedStudyInfo.repIndStageStudy.id",
                  InvalidFieldsMessages.EMPTYFIELD);
              }
            } else {
              action.addMessage(action.getText("Stage Study"));
              action.getInvalidFields().put("input-expectedStudy.projectExpectedStudyInfo.repIndStageStudy.id",
                InvalidFieldsMessages.EMPTYFIELD);
            }
          }

          if (!resultProgessValidate) {

            // Validate Srf Targets Selection
            if (projectExpectedStudy.getProjectExpectedStudyInfo(baseAction.getActualPhase())
              .getIsSrfTarget() == null) {
              action.addMessage(action.getText("isSrfTarget"));
              action.getInvalidFields().put("input-expectedStudy.projectExpectedStudyInfo.isSrfTarget",
                InvalidFieldsMessages.EMPTYFIELD);
            } else {
              if (projectExpectedStudy.getProjectExpectedStudyInfo(baseAction.getActualPhase()).getIsSrfTarget()
                .equals("targetsOptionYes")) {
                // Validate Srf Targets
                if (projectExpectedStudy.getSrfTargets() == null || projectExpectedStudy.getSrfTargets().isEmpty()) {
                  action.addMessage(action.getText("srfTargets"));
                  action.getInvalidFields().put("list-expectedStudy.srfTargets",
                    action.getText(InvalidFieldsMessages.EMPTYLIST, new String[] {"targets"}));
                }
              }
            }
          }

          if (!resultProgessValidate) {
            // Validate Elaboration Outcomes
            if ((!this.isValidString(projectExpectedStudy.getProjectExpectedStudyInfo(baseAction.getActualPhase())
              .getElaborationOutcomeImpactStatement()))
              || this.wordCount(projectExpectedStudy.getProjectExpectedStudyInfo(baseAction.getActualPhase())
                .getElaborationOutcomeImpactStatement()) > 400) {
              action.addMessage(action.getText("Elaboration Outcome"));
              action.getInvalidFields().put(
                "input-expectedStudy.projectExpectedStudyInfo.elaborationOutcomeImpactStatement",
                InvalidFieldsMessages.EMPTYFIELD);
            }
          }

          // Validate References Cited
          if (action.getActualPhase() != null) {
            if (projectExpectedStudy.getReferences() != null) {
              boolean validReferences = true;
              for (int i = 0; i < projectExpectedStudy.getReferences().size(); i++) {
                ProjectExpectedStudyReference reference = projectExpectedStudy.getReferences().get(i);
                if (reference == null || !this.isValidString(reference.getReference())) {
                  validReferences = false;
                  action.addMessage(action.getText("References Cited"));
                  action.getInvalidFields().put("input-expectedStudy.references[" + i + "].reference",
                    InvalidFieldsMessages.EMPTYFIELD);
                }


                if (reference != null
                  && (reference.getLink() == null || !Patterns.WEB_URL.matcher(reference.getLink()).find())) {
                  validReferences = false;
                }
              }

              if (!validReferences) {
                action.addMessage(action.getText("References Cited"));
                action.getInvalidFields().put("expectedStudy.projectExpectedStudyInfo.referencesText",
                  InvalidFieldsMessages.EMPTYFIELD);
              }
            } else {
              action.addMessage(action.getText("References Cited"));
              action.getInvalidFields().put("expectedStudy.projectExpectedStudyInfo.referencesText",
                InvalidFieldsMessages.EMPTYFIELD);
            }
          }

          // Validate Describe Gender
          if (projectExpectedStudy.getProjectExpectedStudyInfo(baseAction.getActualPhase()).getGenderLevel() != null
            && (projectExpectedStudy.getProjectExpectedStudyInfo(baseAction.getActualPhase()).getGenderLevel()
              .getId() != 1
              && projectExpectedStudy.getProjectExpectedStudyInfo(baseAction.getActualPhase()).getGenderLevel()
                .getId() != 4)) {
            if (!this.isValidString(
              projectExpectedStudy.getProjectExpectedStudyInfo(baseAction.getActualPhase()).getDescribeGender())
              && this.wordCount(projectExpectedStudy.getProjectExpectedStudyInfo(baseAction.getActualPhase())
                .getDescribeGender()) <= 100) {
              action.addMessage(action.getText("Describe Gender"));
              action.getInvalidFields().put("input-expectedStudy.projectExpectedStudyInfo.describeGender",
                InvalidFieldsMessages.EMPTYFIELD);
            }
          }

          // Validate Describe Youth
          if (projectExpectedStudy.getProjectExpectedStudyInfo(baseAction.getActualPhase()).getYouthLevel() != null
            && (projectExpectedStudy.getProjectExpectedStudyInfo(baseAction.getActualPhase()).getYouthLevel()
              .getId() != 1
              && projectExpectedStudy.getProjectExpectedStudyInfo(baseAction.getActualPhase()).getYouthLevel()
                .getId() != 4)) {
            if (!this.isValidString(
              projectExpectedStudy.getProjectExpectedStudyInfo(baseAction.getActualPhase()).getDescribeYouth())
              && this.wordCount(projectExpectedStudy.getProjectExpectedStudyInfo(baseAction.getActualPhase())
                .getDescribeYouth()) <= 100) {
              action.addMessage(action.getText("Describe Youth"));
              action.getInvalidFields().put("input-expectedStudy.projectExpectedStudyInfo.describeYouth",
                InvalidFieldsMessages.EMPTYFIELD);
            }
          }
          // Validate Describe Capdev
          if (projectExpectedStudy.getProjectExpectedStudyInfo(baseAction.getActualPhase()).getCapdevLevel() != null
            && (projectExpectedStudy.getProjectExpectedStudyInfo(baseAction.getActualPhase()).getCapdevLevel()
              .getId() != 1
              && projectExpectedStudy.getProjectExpectedStudyInfo(baseAction.getActualPhase()).getCapdevLevel()
                .getId() != 4)) {
            if (!this.isValidString(
              projectExpectedStudy.getProjectExpectedStudyInfo(baseAction.getActualPhase()).getDescribeCapdev())
              && this.wordCount(projectExpectedStudy.getProjectExpectedStudyInfo(baseAction.getActualPhase())
                .getDescribeCapdev()) <= 100) {
              action.addMessage(action.getText("Describe Capdev"));
              action.getInvalidFields().put("input-expectedStudy.projectExpectedStudyInfo.describeCapdev",
                InvalidFieldsMessages.EMPTYFIELD);
            }
          }

          // Validate Describe ClimateChange
          if (projectExpectedStudy.getProjectExpectedStudyInfo(baseAction.getActualPhase())
            .getClimateChangeLevel() != null
            && (projectExpectedStudy.getProjectExpectedStudyInfo(baseAction.getActualPhase()).getClimateChangeLevel()
              .getId() != 1
              && projectExpectedStudy.getProjectExpectedStudyInfo(baseAction.getActualPhase()).getClimateChangeLevel()
                .getId() != 4)) {
            if (!this.isValidString(
              projectExpectedStudy.getProjectExpectedStudyInfo(baseAction.getActualPhase()).getDescribeClimateChange())
              && this.wordCount(projectExpectedStudy.getProjectExpectedStudyInfo(baseAction.getActualPhase())
                .getDescribeClimateChange()) <= 100) {
              action.addMessage(action.getText("Describe Climate Change"));
              action.getInvalidFields().put("input-expectedStudy.projectExpectedStudyInfo.describeClimateChange",
                InvalidFieldsMessages.EMPTYFIELD);
            }
          }

          if (!resultProgessValidate) {
            // Validate Contacts
            if (!this.isValidString(
              projectExpectedStudy.getProjectExpectedStudyInfo(baseAction.getActualPhase()).getContacts())) {
              action.addMessage(action.getText("Contacts"));
              action.getInvalidFields().put("input-expectedStudy.projectExpectedStudyInfo.contacts",
                InvalidFieldsMessages.EMPTYFIELD);
            }
          }


          // Validate Gender Radio Button
          if (projectExpectedStudy.getProjectExpectedStudyInfo(baseAction.getActualPhase()).getGenderLevel() == null) {
            action.addMessage(action.getText("Gender Relevance"));
            action.getInvalidFields().put("input-expectedStudy.projectExpectedStudyInfo.genderLevel.id",
              InvalidFieldsMessages.EMPTYFIELD);
          }


          // Validate Youth Radio Button
          if (projectExpectedStudy.getProjectExpectedStudyInfo(baseAction.getActualPhase()).getYouthLevel() == null) {
            action.addMessage(action.getText("Youth Relevance"));
            action.getInvalidFields().put("input-expectedStudy.projectExpectedStudyInfo.youthLevel.id",
              InvalidFieldsMessages.EMPTYFIELD);
          }


          // Validate Capdev Radio Button
          if (projectExpectedStudy.getProjectExpectedStudyInfo(baseAction.getActualPhase()).getCapdevLevel() == null) {
            action.addMessage(action.getText("Capdev Relevance"));
            action.getInvalidFields().put("input-expectedStudy.projectExpectedStudyInfo.capdevLevel.id",
              InvalidFieldsMessages.EMPTYFIELD);
          }

          // Validate Quantification
          if (projectExpectedStudy.getQuantifications() != null) {
            for (int i = 0; i < projectExpectedStudy.getQuantifications().size(); i++) {
              this.validateQuantifications(action, projectExpectedStudy.getQuantifications().get(i), i);
            }
          }


        } else {
          // Validate Srf Targets Selection
          if (projectExpectedStudy.getProjectExpectedStudyInfo(baseAction.getActualPhase()).getIsSrfTarget() == null) {
            action.addMessage(action.getText("isSrfTarget"));
            action.getInvalidFields().put("input-expectedStudy.projectExpectedStudyInfo.isSrfTarget",
              InvalidFieldsMessages.EMPTYFIELD);
          } else {
            if (projectExpectedStudy.getProjectExpectedStudyInfo(baseAction.getActualPhase()).getIsSrfTarget()
              .equals("targetsOptionYes")) {
              // Validate Srf Targets
              if (projectExpectedStudy.getSrfTargets() == null || projectExpectedStudy.getSrfTargets().isEmpty()) {
                action.addMessage(action.getText("study.stratgicResultsLink.srfTargets"));
                action.getInvalidFields().put("list-expectedStudy.srfTargets",
                  action.getText(InvalidFieldsMessages.EMPTYLIST, new String[] {"targets"}));
              }
            }
          }


          // Validate Comments (TopLevel)
          if (!this.isValidString(
            projectExpectedStudy.getProjectExpectedStudyInfo(baseAction.getActualPhase()).getTopLevelComments())) {
            action.addMessage(action.getText("topLevelComments"));
            action.getInvalidFields().put("input-expectedStudy.projectExpectedStudyInfo.topLevelComments",
              InvalidFieldsMessages.EMPTYFIELD);
          }

          // Validate Commissioning Study
          if (projectExpectedStudy.getProjectExpectedStudyInfo(baseAction.getActualPhase()) != null
            && projectExpectedStudy.getProjectExpectedStudyInfo(baseAction.getActualPhase()).getStudyType() != null
            && projectExpectedStudy.getProjectExpectedStudyInfo(baseAction.getActualPhase()).getStudyType()
              .getId() != 1) {
            if (!this.isValidString(
              projectExpectedStudy.getProjectExpectedStudyInfo(baseAction.getActualPhase()).getCommissioningStudy())
              && this.wordCount(projectExpectedStudy.getProjectExpectedStudyInfo(baseAction.getActualPhase())
                .getCommissioningStudy()) <= 20) {
              action.addMessage(action.getText("Commissioning Study"));
              action.getInvalidFields().put("input-expectedStudy.projectExpectedStudyInfo.commissioningStudy",
                InvalidFieldsMessages.EMPTYFIELD);
            }
          }
        }
      }
    }
  }

  private void validateQuantifications(BaseAction action,
    ProjectExpectedStudyQuantification projectExpectedStudyQuantification, int i) {


    if (projectExpectedStudyQuantification.getQuantificationType() == null) {
      action.addMessage(action.getText("Type Quantification"));
      action.getInvalidFields().put("input-expectedStudy.quantifications[" + i + "].quantificationType.id",
        InvalidFieldsMessages.EMPTYFIELD);
    }
    // Validate Type of Quantification
    if (projectExpectedStudyQuantification.getQuantificationType() != null) {
      if (projectExpectedStudyQuantification.getQuantificationType().getId() == -1) {
        action.addMessage(action.getText("Type Quantification"));
        action.getInvalidFields().put("input-expectedStudy.quantifications[" + i + "].quantificationType.id",
          InvalidFieldsMessages.EMPTYFIELD);
      }
    }


    // Validate Number
    if (!this.isValidNumber(String.valueOf(projectExpectedStudyQuantification.getNumber()))) {
      action.addMessage(action.getText("quantifications.Number"));
      action.getInvalidFields().put("input-expectedStudy.quantifications[" + i + "].number",
        InvalidFieldsMessages.EMPTYFIELD);
    }

    // Validate Unit
    if (!this.isValidString(projectExpectedStudyQuantification.getTargetUnit())) {
      action.addMessage(action.getText("quantifications.Unit"));
      action.getInvalidFields().put("input-expectedStudy.quantifications[" + i + "].targetUnit",
        InvalidFieldsMessages.EMPTYFIELD);
    }

    // Validate Unit
    if (!this.isValidString(projectExpectedStudyQuantification.getComments())) {
      action.addMessage(action.getText("quantifications.Comments"));
      action.getInvalidFields().put("input-expectedStudy.quantifications[" + i + "].comments",
        InvalidFieldsMessages.EMPTYFIELD);
    }


  }
}
