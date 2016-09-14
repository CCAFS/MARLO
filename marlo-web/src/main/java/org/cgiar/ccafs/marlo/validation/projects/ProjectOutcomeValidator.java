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
import org.cgiar.ccafs.marlo.data.manager.ProjectManager;
import org.cgiar.ccafs.marlo.data.model.Project;
import org.cgiar.ccafs.marlo.data.model.ProjectMilestone;
import org.cgiar.ccafs.marlo.data.model.ProjectNextuser;
import org.cgiar.ccafs.marlo.data.model.ProjectOutcome;
import org.cgiar.ccafs.marlo.validation.BaseValidator;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.stream.Collectors;

import com.google.inject.Inject;

public class ProjectOutcomeValidator extends BaseValidator {

  private ProjectManager projectManager;

  @Inject
  public ProjectOutcomeValidator(ProjectManager projectManager) {

    this.projectManager = projectManager;
  }


  public void replaceAll(StringBuilder builder, String from, String to) {
    int index = builder.indexOf(from);
    while (index != -1) {
      builder.replace(index, index + from.length(), to);
      index += to.length(); // Move to the end of the replacement
      index = builder.indexOf(from, index);
    }
  }

  public void validate(BaseAction action, ProjectOutcome projectOutcome) {

    this.validateProjectOutcome(action, projectOutcome);
    if (!action.getFieldErrors().isEmpty()) {
      System.out.println(action.getFieldErrors());
      action.addActionError(action.getText("saving.fields.required"));
    } else if (validationMessage.length() > 0) {
      action
        .addActionMessage(" " + action.getText("saving.missingFields", new String[] {validationMessage.toString()}));
    }

    if (action.isReportingActive()) {
      this.saveMissingFields(projectOutcome.getProject(), APConstants.REPORTING, action.getPlanningYear(),
        "description");
    } else {
      this.saveMissingFields(projectOutcome.getProject(), APConstants.PLANNING, action.getPlanningYear(),
        "description");
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
          }
        }


        if (!(this.isValidString(projectMilestone.getNarrativeTarget())
          && this.wordCount(projectMilestone.getNarrativeTarget()) <= 100)) {
          this.addMessage(action.getText("projectOutcomeMilestone.requeried.expectedNarrative", params));
        }

        if (!(this.isValidString(projectMilestone.getExpectedGender())
          && this.wordCount(projectMilestone.getExpectedGender()) <= 100)) {
          this.addMessage(action.getText("projectOutcomeMilestone.requeried.expectedGenderSocialNarrative", params));
        }
      }


    }


  }


  public void validateProjectNextUser(BaseAction action, ProjectNextuser projectNextuser, int i) {
    List<String> params = new ArrayList<String>();
    params.add(String.valueOf(i + 1));


    if (!(this.isValidString(projectNextuser.getNextUser()) && this.wordCount(projectNextuser.getNextUser()) <= 20)) {
      this.addMessage(action.getText("projectOutcomeNextUser.requeried.title", params));
    }
    if (!(this.isValidString(projectNextuser.getKnowledge()) && this.wordCount(projectNextuser.getKnowledge()) <= 50)) {
      this.addMessage(action.getText("projectOutcomeNextUser.requeried.knowledge", params));
    }
    if (!(this.isValidString(projectNextuser.getStrategies())
      && this.wordCount(projectNextuser.getStrategies()) <= 50)) {
      this.addMessage(action.getText("projectOutcomeNextUser.requeried.strategies", params));
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

    this.validateLessonsLearnOutcome(action, projectOutcome);
    if (this.validationMessage.toString().contains("Lessons")) {
      this.replaceAll(validationMessage, "Lessons",
        "Lessons regarding partnerships and possible implications for the coming planning cycle");
    }
    if (action.isPlanningActive()) {

      if (projectOutcome.getExpectedUnit() == null || projectOutcome.getExpectedUnit().getId() == -1) {
        this.addMessage(action.getText("projectOutcome.expectedUnit"));
        projectOutcome.setExpectedUnit(null);
      }
      if (projectOutcome.getExpectedValue() == 0) {
        this.addMessage(action.getText("projectOutcome.expectedValue"));
      }

      if (!(this.isValidString(projectOutcome.getNarrativeTarget())
        && this.wordCount(projectOutcome.getNarrativeTarget()) <= 100)) {
        this.addMessage(action.getText("projectOutcome.narrativeTarget"));
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
    }


    if (projectOutcome.getNextUsers() != null || projectOutcome.getNextUsers().size() > 0) {
      for (int i = 0; i < projectOutcome.getNextUsers().size(); i++) {
        this.validateProjectNextUser(action, projectOutcome.getNextUsers().get(i), i);
      }
    } else {
      this.addMessage(action.getText("projectOutcomeNextUsers"));
    }

  }
}
