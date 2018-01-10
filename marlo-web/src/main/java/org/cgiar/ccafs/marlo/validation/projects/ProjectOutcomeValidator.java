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
import org.cgiar.ccafs.marlo.data.manager.CrpManager;
import org.cgiar.ccafs.marlo.data.manager.CrpProgramOutcomeManager;
import org.cgiar.ccafs.marlo.data.manager.ProjectManager;
import org.cgiar.ccafs.marlo.data.model.Crp;
import org.cgiar.ccafs.marlo.data.model.Project;
import org.cgiar.ccafs.marlo.data.model.ProjectMilestone;
import org.cgiar.ccafs.marlo.data.model.ProjectNextuser;
import org.cgiar.ccafs.marlo.data.model.ProjectOutcome;
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
  private final CrpManager crpManager;

  @Inject
  public ProjectOutcomeValidator(ProjectManager projectManager, CrpProgramOutcomeManager crpProgramOutcomeManager,
    CrpManager crpManager) {
    super();
    this.crpManager = crpManager;
    this.projectManager = projectManager;
    this.crpProgramOutcomeManager = crpProgramOutcomeManager;
  }

  private Path getAutoSaveFilePath(ProjectOutcome project, long crpID) {
    Crp crp = crpManager.getCrpById(crpID);
    String composedClassName = project.getClass().getSimpleName();
    String actionFile = ProjectSectionStatusEnum.OUTCOME.getStatus().replace("/", "_");
    String autoSaveFile =
      project.getId() + "_" + composedClassName + "_" + crp.getAcronym() + "_" + actionFile + ".json";

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
    // BaseValidator does not Clean this variables.. so before validate the section, it be clear these variables
    this.missingFields.setLength(0);
    this.validationMessage.setLength(0);
    action.setInvalidFields(new HashMap<>());
    if (!saving) {
      Path path = this.getAutoSaveFilePath(projectOutcome, action.getCrpID());

      if (path.toFile().exists()) {
        this.addMissingField("draft");
      }
    }

    Project project = projectManager.getProjectById(projectOutcome.getProject().getId());
    if (!(project.getAdministrative() != null && project.getAdministrative().booleanValue() == true)) {
      this.validateProjectOutcome(action, projectOutcome);
      if (!action.getFieldErrors().isEmpty()) {
        action.addActionError(action.getText("saving.fields.required"));
      } else if (validationMessage.length() > 0) {
        action
          .addActionMessage(" " + action.getText("saving.missingFields", new String[] {validationMessage.toString()}));
      }

      if (action.isReportingActive()) {
        this.saveMissingFields(projectOutcome, APConstants.REPORTING, action.getReportingYear(),
          ProjectSectionStatusEnum.OUTCOMES.getStatus());
      } else {
        this.saveMissingFields(projectOutcome, APConstants.PLANNING, action.getPlanningYear(),
          ProjectSectionStatusEnum.OUTCOMES.getStatus());
      }
    } else {
      this.addMissingField("");
      if (action.isReportingActive()) {
        this.saveMissingFields(projectOutcome, APConstants.REPORTING, action.getReportingYear(),
          ProjectSectionStatusEnum.OUTCOMES.getStatus());
      } else {
        this.saveMissingFields(projectOutcome, APConstants.PLANNING, action.getPlanningYear(),
          ProjectSectionStatusEnum.OUTCOMES.getStatus());
      }
    }


  }


  public void validateProjectMilestone(BaseAction action, ProjectMilestone projectMilestone, int i) {
    List<String> params = new ArrayList<String>();
    int counter = i + 1;
    params.add(String.valueOf(counter));
    if (projectMilestone != null) {
      if (projectMilestone.getYear() == action.getCurrentCycleYear()) {

        if (projectMilestone.getExpectedUnit() == null || projectMilestone.getExpectedUnit().getId() == null
          || projectMilestone.getExpectedUnit().getId() == -1) {
          // this.addMessage(action.getText("projectOutcomeMilestone.requeried.expectedUnit", params));
          projectMilestone.setExpectedUnit(null);
        } else {
          if (projectMilestone.getExpectedValue() == null
            || !this.isValidNumber(String.valueOf(projectMilestone.getExpectedValue()))) {
            this.addMessage(action.getText("projectOutcomeMilestone.requeried.expectedValue", params));
            action.getInvalidFields().put("input-projectOutcome.milestones[" + i + "].expectedValue",
              InvalidFieldsMessages.EMPTYFIELD);
          }
          if (projectMilestone.getExpectedValue() != null && projectMilestone.getExpectedValue() < 0) {
            action.getInvalidFields().put("input-projectOutcome.milestones[" + i + "].expectedValue",
              InvalidFieldsMessages.EMPTYFIELD);
          }
        }


        if (!(this.isValidString(projectMilestone.getNarrativeTarget())
          && this.wordCount(projectMilestone.getNarrativeTarget()) <= 100)) {
          this.addMessage(action.getText("projectOutcomeMilestone.requeried.expectedNarrative", params));
          action.getInvalidFields().put("input-projectOutcome.milestones[" + i + "].narrativeTarget",
            InvalidFieldsMessages.EMPTYFIELD);
        }


      }


    }


  }


  public void validateProjectNextUser(BaseAction action, ProjectNextuser projectNextuser, int i) {
    List<String> params = new ArrayList<String>();
    params.add(String.valueOf(i + 1));


    if (!(this.isValidString(projectNextuser.getNextUser()) && this.wordCount(projectNextuser.getNextUser()) <= 20)) {
      this.addMessage(action.getText("projectOutcomeNextUser.requeried.title", params));
      action.getInvalidFields().put("input-projectOutcome.nextUsers[" + i + "].nextUser",
        InvalidFieldsMessages.EMPTYFIELD);
    }
    if (!(this.isValidString(projectNextuser.getKnowledge())
      && this.wordCount(projectNextuser.getKnowledge()) <= 100)) {
      this.addMessage(action.getText("projectOutcomeNextUser.requeried.knowledge", params));
      action.getInvalidFields().put("input-projectOutcome.nextUsers[" + i + "].knowledge",
        InvalidFieldsMessages.EMPTYFIELD);
    }
    if (!(this.isValidString(projectNextuser.getStrategies())
      && this.wordCount(projectNextuser.getStrategies()) <= 100)) {
      this.addMessage(action.getText("projectOutcomeNextUser.requeried.strategies", params));
      action.getInvalidFields().put("input-projectOutcome.nextUsers[" + i + "].strategies",
        InvalidFieldsMessages.EMPTYFIELD);
    }

  }

  public void validateProjectOutcome(BaseAction action, ProjectOutcome projectOutcome) {
    Project project = projectManager.getProjectById(projectOutcome.getProject().getId());
    int startYear = 0;
    int endYear = 0;
    Calendar startDate = Calendar.getInstance();
    startDate.setTime(project.getStartDate());
    startYear = startDate.get(Calendar.YEAR);

    Calendar endDate = Calendar.getInstance();
    endDate.setTime(project.getEndDate());
    endYear = endDate.get(Calendar.YEAR);

    if (!action.isProjectNew(project.getId())) {
      this.validateLessonsLearnOutcome(action, projectOutcome);
      if (this.validationMessage.toString().contains("Lessons")) {
        this.replaceAll(validationMessage, "Lessons",
          "Lessons regarding partnerships and possible implications for the coming planning cycle");
        action.getInvalidFields().put("input-projectOutcome.projectComponentLesson.lessons",
          InvalidFieldsMessages.EMPTYFIELD);
      }
    }
    if (action.isPlanningActive()) {
      projectOutcome.setCrpProgramOutcome(
        crpProgramOutcomeManager.getCrpProgramOutcomeById(projectOutcome.getCrpProgramOutcome().getId()));
      if (!(projectOutcome.getCrpProgramOutcome().getSrfTargetUnit() == null
        || projectOutcome.getCrpProgramOutcome().getSrfTargetUnit().getId() == -1)) {

        if (projectOutcome.getExpectedValue() == null || projectOutcome.getExpectedValue().longValue() < 0) {
          this.addMessage(action.getText("projectOutcome.expectedValue"));
          action.getInvalidFields().put("input-projectOutcome.expectedValue", InvalidFieldsMessages.EMPTYFIELD);
        }
      }


      if (!(this.isValidString(projectOutcome.getNarrativeTarget())
        && this.wordCount(projectOutcome.getNarrativeTarget()) <= 100)) {
        this.addMessage(action.getText("projectOutcome.narrativeTarget"));
        action.getInvalidFields().put("input-projectOutcome.narrativeTarget", InvalidFieldsMessages.EMPTYFIELD);
      }

      // TODO: Validate outcome gender here

      if (project.getCrossCuttingGender() != null && project.getCrossCuttingGender().booleanValue() == true) {

        if (!(this.isValidString(projectOutcome.getGenderDimenssion())
          && this.wordCount(projectOutcome.getGenderDimenssion()) <= 100)) {
          this.addMessage(action.getText("projectOutcome.genderDimenssion"));
          action.getInvalidFields().put("input-projectOutcome.genderDimenssion", InvalidFieldsMessages.EMPTYFIELD);
        }
      }


      if (project.getCrossCuttingYouth() != null && project.getCrossCuttingYouth().booleanValue() == true) {

        if (!(this.isValidString(projectOutcome.getYouthComponent())
          && this.wordCount(projectOutcome.getYouthComponent()) <= 100)) {
          this.addMessage(action.getText("projectOutcome.youthComponent"));
          action.getInvalidFields().put("input-projectOutcome.youthComponent", InvalidFieldsMessages.EMPTYFIELD);
        }
      }


    }


    if (projectOutcome.getMilestones() != null || projectOutcome.getMilestones().size() > 0) {
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

    } else {
      this.addMessage(action.getText("projectOutcome.milestones"));
      action.getInvalidFields().put("list-projectOutcome.milestones",
        action.getText(InvalidFieldsMessages.EMPTYLIST, new String[] {"Milestones"}));
    }


    if (action.hasSpecificities(APConstants.CRP_NEXT_USERS)) {
      if (projectOutcome.getNextUsers() != null && projectOutcome.getNextUsers().size() > 0) {
        for (int i = 0; i < projectOutcome.getNextUsers().size(); i++) {
          this.validateProjectNextUser(action, projectOutcome.getNextUsers().get(i), i);
        }
      } else {
        this.addMessage(action.getText("projectOutcomeNextUsers"));
        action.getInvalidFields().put("input-projectOutcome.nextUsers",
          action.getText(InvalidFieldsMessages.EMPTYLIST, new String[] {"Next User"}));
      }
    }


  }
}
