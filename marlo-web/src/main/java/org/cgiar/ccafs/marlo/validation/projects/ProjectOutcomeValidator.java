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
import org.cgiar.ccafs.marlo.data.manager.CrpMilestoneManager;
import org.cgiar.ccafs.marlo.data.manager.CrpProgramOutcomeManager;
import org.cgiar.ccafs.marlo.data.manager.GlobalUnitManager;
import org.cgiar.ccafs.marlo.data.manager.ProjectManager;
import org.cgiar.ccafs.marlo.data.manager.SrfTargetUnitManager;
import org.cgiar.ccafs.marlo.data.model.GlobalUnit;
import org.cgiar.ccafs.marlo.data.model.Project;
import org.cgiar.ccafs.marlo.data.model.ProjectCommunication;
import org.cgiar.ccafs.marlo.data.model.ProjectComponentLesson;
import org.cgiar.ccafs.marlo.data.model.ProjectMilestone;
import org.cgiar.ccafs.marlo.data.model.ProjectNextuser;
import org.cgiar.ccafs.marlo.data.model.ProjectOutcome;
import org.cgiar.ccafs.marlo.data.model.ProjectOutcomeIndicator;
import org.cgiar.ccafs.marlo.data.model.ProjectSectionStatusEnum;
import org.cgiar.ccafs.marlo.utils.InvalidFieldsMessages;
import org.cgiar.ccafs.marlo.validation.BaseValidator;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

import javax.inject.Inject;
import javax.inject.Named;

@Named
public class ProjectOutcomeValidator extends BaseValidator {

  private final ProjectManager projectManager;
  private final CrpProgramOutcomeManager crpProgramOutcomeManager;
  private final CrpMilestoneManager crpMilestoneManager;
  private final SrfTargetUnitManager srfTargetUnitManager;


  // GlobalUnit Manager
  private GlobalUnitManager crpManager;

  @Inject
  public ProjectOutcomeValidator(ProjectManager projectManager, CrpProgramOutcomeManager crpProgramOutcomeManager,
    GlobalUnitManager crpManager, CrpMilestoneManager crpMilestoneManager, SrfTargetUnitManager srfTargetUnitManager) {

    this.projectManager = projectManager;
    this.crpProgramOutcomeManager = crpProgramOutcomeManager;
    this.crpManager = crpManager;
    this.crpMilestoneManager = crpMilestoneManager;
    this.srfTargetUnitManager = srfTargetUnitManager;
  }

  private Path getAutoSaveFilePath(ProjectOutcome project, long crpID, BaseAction action) {
    GlobalUnit crp = crpManager.getGlobalUnitById(crpID);
    String composedClassName = project.getClass().getSimpleName();
    String actionFile = ProjectSectionStatusEnum.OUTCOME.getStatus().replace("/", "_");
    String autoSaveFile = project.getId() + "_" + composedClassName + "_" + action.getActualPhase().getName() + "_"
      + action.getActualPhase().getYear() + "_" + crp.getAcronym() + "_" + actionFile + ".json";

    return Paths.get(config.getAutoSaveFolder() + autoSaveFile);
  }

  public void replaceAll(StringBuilder builder, String from, String to) {
    int index = builder.indexOf(from);
    while (index != -1) {
      builder.replace(index, index + from.length(), to);
      index += to.length(); // Move to the end of the replacement
      index = builder.indexOf(from, index);
    }
  }

  public void validate(BaseAction action, ProjectOutcome projectOutcome, boolean saving) {
    action.setInvalidFields(new HashMap<>());
    if (!saving) {
      Path path = this.getAutoSaveFilePath(projectOutcome, action.getCrpID(), action);

      if (path.toFile().exists()) {
        action.addMissingField("draft");
      }
    }

    if (projectOutcome.getExpectedUnit() != null) {
      if (projectOutcome.getExpectedUnit().getId() == null
        || projectOutcome.getExpectedUnit().getId().longValue() == -1) {
        projectOutcome.setExpectedUnit(null);
      } else {
        projectOutcome.setExpectedUnit(projectOutcome.getExpectedUnit());
      }
    }

    if (projectOutcome.getAchievedUnit() != null) {
      if (projectOutcome.getAchievedUnit().getId() == null
        || projectOutcome.getAchievedUnit().getId().longValue() == -1) {
        projectOutcome.setAchievedUnit(null);
      } else {
        projectOutcome.setAchievedUnit(projectOutcome.getAchievedUnit());
      }
    }

    Project project = projectManager.getProjectById(projectOutcome.getProject().getId());
    if (!(project.getProjecInfoPhase(action.getActualPhase()).getAdministrative() != null
      && project.getProjecInfoPhase(action.getActualPhase()).getAdministrative().booleanValue() == true)) {
      this.validateProjectOutcome(action, projectOutcome);
      if (!action.getFieldErrors().isEmpty()) {
        action.addActionError(action.getText("saving.fields.required"));
      } else if (action.getValidationMessage().length() > 0) {
        action.addActionMessage(
          " " + action.getText("saving.missingFields", new String[] {action.getValidationMessage().toString()}));
      }

      this.saveMissingFields(projectOutcome, action.getActualPhase().getDescription(),
        action.getActualPhase().getYear(), action.getActualPhase().getUpkeep(),
        ProjectSectionStatusEnum.OUTCOMES.getStatus(), action);
    } else {
      action.addMissingField("");
      this.saveMissingFields(projectOutcome, action.getActualPhase().getDescription(),
        action.getActualPhase().getYear(), action.getActualPhase().getUpkeep(),
        ProjectSectionStatusEnum.OUTCOMES.getStatus(), action);
    }


  }


  public void validateProjectMilestone(BaseAction action, ProjectMilestone projectMilestone, int i) {
    List<String> params = new ArrayList<String>();
    int counter = i + 1;
    params.add(String.valueOf(counter));
    if (projectMilestone != null) {
      projectMilestone
        .setCrpMilestone(crpMilestoneManager.getCrpMilestoneById(projectMilestone.getCrpMilestone().getId()));
      if (projectMilestone.getCrpMilestone().getYear() == action.getCurrentCycleYear()) {


        if (projectMilestone.getExpectedUnit() == null || projectMilestone.getExpectedUnit().getId() == null
          || projectMilestone.getExpectedUnit().getId() == -1) {
          // action.addMessage(action.getText("projectOutcomeMilestone.requeried.expectedUnit", params));
          projectMilestone.setExpectedUnit(null);
        } else {
          if (projectMilestone.getExpectedValue() == null
            || !this.isValidNumber(String.valueOf(projectMilestone.getExpectedValue()))) {
            action.addMessage(action.getText("projectOutcomeMilestone.requeried.expectedValue", params));
            action.getInvalidFields().put("input-projectOutcome.milestones[" + i + "].expectedValue",
              InvalidFieldsMessages.EMPTYFIELD);
          }
          if (projectMilestone.getExpectedValue() != null && projectMilestone.getExpectedValue() < 0) {
            action.getInvalidFields().put("input-projectOutcome.milestones[" + i + "].expectedValue",
              InvalidFieldsMessages.EMPTYFIELD);
          }
        }

        if (!action.isReportingActive()) {
          if (!(this.isValidString(projectMilestone.getNarrativeTarget())
            && this.wordCount(projectMilestone.getNarrativeTarget()) <= 100)) {
            action.addMessage(action.getText("projectOutcomeMilestone.requeried.expectedNarrative", params));
            action.getInvalidFields().put("input-projectOutcome.milestones[" + i + "].narrativeTarget",
              InvalidFieldsMessages.EMPTYFIELD);
          }
        }

        if (action.isReportingActive()) {
          if (!(this.isValidString(projectMilestone.getNarrativeAchieved())
            && this.wordCount(projectMilestone.getNarrativeAchieved()) <= 100)) {
            action.addMessage(action.getText("projectOutcomeMilestone.achievedNarrative", params));
            action.getInvalidFields().put("input-projectOutcome.milestones[" + i + "].narrativeAchieved",
              InvalidFieldsMessages.EMPTYFIELD);
          }
        }

      }
    }

  }


  public void validateProjectNextUser(BaseAction action, ProjectNextuser projectNextuser, int i) {
    List<String> params = new ArrayList<String>();
    params.add(String.valueOf(i + 1));


    if (!(this.isValidString(projectNextuser.getNextUser()) && this.wordCount(projectNextuser.getNextUser()) <= 20)) {
      action.addMessage(action.getText("projectOutcomeNextUser.requeried.title", params));
      action.getInvalidFields().put("input-projectOutcome.nextUsers[" + i + "].nextUser",
        InvalidFieldsMessages.EMPTYFIELD);
    }
    if (!(this.isValidString(projectNextuser.getKnowledge())
      && this.wordCount(projectNextuser.getKnowledge()) <= 100)) {
      action.addMessage(action.getText("projectOutcomeNextUser.requeried.knowledge", params));
      action.getInvalidFields().put("input-projectOutcome.nextUsers[" + i + "].knowledge",
        InvalidFieldsMessages.EMPTYFIELD);
    }
    if (!(this.isValidString(projectNextuser.getStrategies())
      && this.wordCount(projectNextuser.getStrategies()) <= 100)) {
      action.addMessage(action.getText("projectOutcomeNextUser.requeried.strategies", params));
      action.getInvalidFields().put("input-projectOutcome.nextUsers[" + i + "].strategies",
        InvalidFieldsMessages.EMPTYFIELD);
    }
    if (action.isReportingActive()) {
      if (!(this.isValidString(projectNextuser.getKnowledgeReport())
        && this.wordCount(projectNextuser.getKnowledgeReport()) <= 200)) {
        action.addMessage(action.getText("projectOutcomeNextUser.reportOnProgress.readText"));
        action.getInvalidFields().put("input-projectOutcome.nextUsers[" + i + "].knowledgeReport",
          InvalidFieldsMessages.EMPTYFIELD);
      }
      if (!(this.isValidString(projectNextuser.getStrategiesReport())
        && this.wordCount(projectNextuser.getStrategiesReport()) <= 100)) {
        action.addMessage(action.getText("projectOutcomeNextUser.strategiesEncourage.readText"));
        action.getInvalidFields().put("input-projectOutcome.nextUsers[" + i + "].strategiesReport",
          InvalidFieldsMessages.EMPTYFIELD);
      }
    }

  }


  public void validateProjectOutcome(BaseAction action, ProjectOutcome projectOutcome) {
    Project project = projectManager.getProjectById(projectOutcome.getProject().getId());
    int startYear = 0;
    int endYear = 0;
    Calendar startDate = Calendar.getInstance();
    startDate.setTime(project.getProjecInfoPhase(action.getActualPhase()).getStartDate());
    startYear = startDate.get(Calendar.YEAR);

    Calendar endDate = Calendar.getInstance();
    endDate.setTime(project.getProjecInfoPhase(action.getActualPhase()).getEndDate());
    endYear = endDate.get(Calendar.YEAR);

    projectOutcome.setCrpProgramOutcome(
      crpProgramOutcomeManager.getCrpProgramOutcomeById(projectOutcome.getCrpProgramOutcome().getId()));
    if (!(projectOutcome.getCrpProgramOutcome().getSrfTargetUnit() == null
      || projectOutcome.getCrpProgramOutcome().getSrfTargetUnit().getId() == -1)) {

      if (projectOutcome.getExpectedValue() == null || projectOutcome.getExpectedValue().longValue() < 0) {
        action.addMessage(action.getText("projectOutcome.expectedValue"));
        action.getInvalidFields().put("input-projectOutcome.expectedValue", InvalidFieldsMessages.EMPTYFIELD);
      }
    }


    if (!(this.isValidString(projectOutcome.getNarrativeTarget())
      && this.wordCount(projectOutcome.getNarrativeTarget()) <= 100)) {
      action.addMessage(action.getText("projectOutcome.narrativeTarget"));
      action.getInvalidFields().put("input-projectOutcome.narrativeTarget", InvalidFieldsMessages.EMPTYFIELD);
    }

    if (projectOutcome.getMilestones() != null && projectOutcome.getMilestones().size() > 0) {
      if (action.isPlanningActive()) {
        List<ProjectMilestone> milestones = projectOutcome.getMilestones().stream()
          .filter(c -> c != null && c.getYear() == action.getCurrentCycleYear()).collect(Collectors.toList());
        for (int i = 0; i < milestones.size(); i++) {
          this.validateProjectMilestone(action, milestones.get(i), i);
        }
      } else {
        for (int i = 0; i < projectOutcome.getMilestones().size(); i++) {
          this.validateProjectMilestone(action, projectOutcome.getMilestones().get(i), i);
        }
      }

    }


    if (action.hasSpecificities(APConstants.CRP_BASELINE_INDICATORS)) {
      // TODO: check validation for reporting 2018
      if (projectOutcome.getIndicators() != null) {
        for (int i = 0; i < projectOutcome.getIndicators().size(); i++) {
          this.validateProjectOutcomeIndicator(action, projectOutcome.getIndicators().get(i), i);
        }
      }
    }


    if (action.hasSpecificities(APConstants.CRP_NEXT_USERS)) {
      if (projectOutcome.getNextUsers() != null && projectOutcome.getNextUsers().size() > 0) {
        for (int i = 0; i < projectOutcome.getNextUsers().size(); i++) {
          this.validateProjectNextUser(action, projectOutcome.getNextUsers().get(i), i);
        }
      } else {
        action.addMessage(action.getText("projectOutcomeNextUsers"));
        action.getInvalidFields().put("input-projectOutcome.nextUsers",
          action.getText(InvalidFieldsMessages.EMPTYLIST, new String[] {"Next User"}));
      }
    }

    if (action.isReportingActive()) {
      if (!action.isProjectNew(project.getId())) {

        if (projectOutcome.getProjectComponentLesson() != null) {
          ProjectComponentLesson lesson = projectOutcome.getProjectComponentLesson();
          if (!(this.isValidString(lesson.getLessons()))) {
            action.addMessage(action.getText("projectOutcome.lessons.reporting.readText"));
            action.getInvalidFields().put("input-projectOutcome.projectComponentLesson.lessons",
              action.getText(InvalidFieldsMessages.EMPTYLIST));
          }
        }
      }
      if (project.getProjecInfoPhase(action.getActualPhase()).getEndDate() != null) {
        Calendar calendarEnd = Calendar.getInstance();
        calendarEnd.setTime(project.getProjecInfoPhase(action.getActualPhase()).getEndDate());

        if (action.getCurrentCycleYear() == calendarEnd.get(Calendar.YEAR)) {
          if (!(projectOutcome.getCrpProgramOutcome().getSrfTargetUnit() == null
            || projectOutcome.getCrpProgramOutcome().getSrfTargetUnit().getId() == -1)) {

            if (projectOutcome.getAchievedValue() == null || projectOutcome.getAchievedValue().longValue() < 0) {
              action.addMessage(action.getText("projectOutcome.achievedValue"));
              action.getInvalidFields().put("input-projectOutcome.achievedValue", InvalidFieldsMessages.EMPTYFIELD);
            }
          }

          if (!(this.isValidString(projectOutcome.getNarrativeAchieved())
            && this.wordCount(projectOutcome.getNarrativeAchieved()) <= 100)) {
            action.addMessage(action.getText("projectOutcome.narrativeAchieved"));
            action.getInvalidFields().put("input-projectOutcome.narrativeAchieved", InvalidFieldsMessages.EMPTYFIELD);
          }
        }
      }

      if (action.hasSpecificities(APConstants.CRP_SHOW_PROJECT_OUTCOME_COMMUNICATIONS)) {
        if (projectOutcome.getCommunications() != null && projectOutcome.getCommunications().size() > 0) {
          int i = 0;
          for (ProjectCommunication projectCommunication : projectOutcome.getCommunications()) {
            if (projectCommunication.getYear() == action.getCurrentCycleYear()) {
              if (!(this.isValidString(projectCommunication.getCommunication())
                && this.wordCount(projectCommunication.getCommunication()) <= 100)) {
                action.addMessage(action.getText("projectOutcome.communicationEngagement"));
                action.getInvalidFields().put("input-projectOutcome.communications[" + i + "].communication",
                  InvalidFieldsMessages.EMPTYFIELD);
              }
            }
            i++;
          }
        }
      }


    }


  }

  public void validateProjectOutcomeIndicator(BaseAction action, ProjectOutcomeIndicator projectOutcomeIndicator,
    int i) {
    List<String> params = new ArrayList<String>();
    params.add(String.valueOf(i + 1));

    if (action.isPlanningActive()) {
      if (!(this.isValidString(projectOutcomeIndicator.getNarrative())
        && this.wordCount(projectOutcomeIndicator.getNarrative()) <= 100)) {
        action.addMessage(action.getText("projectOutcomeIndicator.requeried.narrative", params));
        action.getInvalidFields().put("input-projectOutcome.indicators[" + i + "].narrative",
          InvalidFieldsMessages.EMPTYFIELD);
      }
    }

    if (projectOutcomeIndicator.getValue() == null || projectOutcomeIndicator.getValue().longValue() < 0) {
      action.addMessage(action.getText("projectOutcomeIndicator.value"));
      action.getInvalidFields().put("input-projectOutcome.indicators[" + i + "].value",
        InvalidFieldsMessages.EMPTYFIELD);
    }

    if (action.isReportingActive()) {
      if (!(this.isValidString(projectOutcomeIndicator.getAchievedNarrative())
        && this.wordCount(projectOutcomeIndicator.getAchievedNarrative()) <= 100)) {
        action.addMessage(action.getText("projectOutcomeIndicator.requeried.achievedNarrative", params));
        action.getInvalidFields().put("input-projectOutcome.indicators[" + i + "].achievedNarrative",
          InvalidFieldsMessages.EMPTYFIELD);
      }
      if (projectOutcomeIndicator.getValueReporting() == null
        || projectOutcomeIndicator.getValueReporting().longValue() < 0) {
        action.addMessage(action.getText("projectOutcomeIndicator.valueReporting"));
        action.getInvalidFields().put("input-projectOutcome.indicators[" + i + "].valueReporting",
          InvalidFieldsMessages.EMPTYFIELD);
      }
    }


  }
}
