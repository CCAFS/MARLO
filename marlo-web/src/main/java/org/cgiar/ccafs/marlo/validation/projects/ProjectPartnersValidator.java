/*****************************************************************
 * This file is part of CCAFS Planning and Reporting Platform.
 * CCAFS P&R is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * at your option) any later version.
 * CCAFS P&R is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 * You should have received a copy of the GNU General Public License
 * along with CCAFS P&R. If not, see <http://www.gnu.org/licenses/>.
 *****************************************************************/

package org.cgiar.ccafs.marlo.validation.projects;


import org.cgiar.ccafs.marlo.action.BaseAction;
import org.cgiar.ccafs.marlo.config.APConstants;
import org.cgiar.ccafs.marlo.data.manager.CrpManager;
import org.cgiar.ccafs.marlo.data.manager.UserManager;
import org.cgiar.ccafs.marlo.data.model.Crp;
import org.cgiar.ccafs.marlo.data.model.Project;
import org.cgiar.ccafs.marlo.data.model.ProjectPartner;
import org.cgiar.ccafs.marlo.data.model.ProjectPartnerPerson;
import org.cgiar.ccafs.marlo.data.model.ProjectSectionStatusEnum;
import org.cgiar.ccafs.marlo.validation.BaseValidator;
import org.cgiar.ccafs.marlo.validation.model.ProjectValidator;

import java.nio.file.Path;
import java.nio.file.Paths;

import com.google.inject.Inject;


/**
 * @author Hernán David Carvajal B. - CIAT/CCAFS
 * @author Héctor Fabio Tobón R. - CIAT/CCAFS
 */

public class ProjectPartnersValidator extends BaseValidator {

  private boolean hasErros;
  private ProjectValidator projectValidator;
  private UserManager userManager;

  @Inject
  private CrpManager crpManager;

  @Inject
  public ProjectPartnersValidator(ProjectValidator projectValidator, UserManager userManager) {
    super();
    this.projectValidator = projectValidator;
    this.userManager = userManager;
  }

  private Path getAutoSaveFilePath(Project project, long crpID) {
    Crp crp = crpManager.getCrpById(crpID);
    String composedClassName = project.getClass().getSimpleName();
    String actionFile = ProjectSectionStatusEnum.PARTNERS.getStatus().replace("/", "_");
    String autoSaveFile =
      project.getId() + "_" + composedClassName + "_" + crp.getAcronym() + "_" + actionFile + ".json";

    return Paths.get(config.getAutoSaveFolder() + autoSaveFile);
  }

  public boolean isHasErros() {
    return hasErros;
  }


  public void replaceAll(StringBuilder builder, String from, String to) {
    int index = builder.indexOf(from);
    while (index != -1) {
      builder.replace(index, index + from.length(), to);
      index += to.length(); // Move to the end of the replacement
      index = builder.indexOf(from, index);
    }
  }


  public void setHasErros(boolean hasErros) {
    this.hasErros = hasErros;
  }

  public void validate(BaseAction action, Project project, boolean saving) {


    hasErros = false;
    if (project != null) {
      if (!saving) {
        Path path = this.getAutoSaveFilePath(project, action.getCrpID());

        if (path.toFile().exists()) {
          this.addMissingField("draft");
        }
      }
      if (!project.getPartners().isEmpty() && (project.isCoreProject() || project.isCoFundedProject())) {

        if (action.isReportingActive()) {
          if (!this.isValidString(project.getOverall())) {
            this.addMessage(
              action.getText("Please provide Partnerships overall performance over the last reporting period"));
            this.addMissingField("project.partners.overall");
          }
        }

      }

      if (project.isProjectEditLeader()) {
        this.validateLessonsLearn(action, project);
        if (this.validationMessage.toString().contains("Lessons")) {
          this.replaceAll(validationMessage, "Lessons",
            "Lessons regarding partnerships and possible implications for the coming planning cycle");
        }
      }
      this.validateCCAFSProject(action, project);

      if (!action.getFieldErrors().isEmpty()) {
        hasErros = true;
        action.addActionError(action.getText("saving.fields.required"));
      } else if (validationMessage.length() > 0) {
        action
          .addActionMessage(" " + action.getText("saving.missingFields", new String[] {validationMessage.toString()}));
      }
      if (action.isReportingActive()) {
        this.saveMissingFields(project, APConstants.REPORTING, action.getPlanningYear(),
          ProjectSectionStatusEnum.PARTNERS.getStatus());
      } else {
        this.saveMissingFields(project, APConstants.PLANNING, action.getPlanningYear(),
          ProjectSectionStatusEnum.PARTNERS.getStatus());
      }
      // Saving missing fields.

    }
  }

  private void validateCCAFSProject(BaseAction action, Project project) {
    this.validateInstitutionsEmpty(action, project);
    this.validateProjectLeader(action, project);
    this.validateContactPersons(action, project);
  }

  /**
   * This method validates all the required fields within contact person.
   * 
   * @param action this action.
   * @param project the project with the partners on it.
   */
  private void validateContactPersons(BaseAction action, Project project) {
    if (project != null) {
      int c = 0, j = 0;
      for (ProjectPartner partner : project.getPartners()) {
        j = 0;
        // Validating that the partner has a least one contact person
        if (partner.getPartnerPersons() == null || partner.getPartnerPersons().isEmpty()) {
          action.addActionMessage(action.getText("planning.projectPartners.contactPersons.empty",
            new String[] {partner.getInstitution().getName()}));
          this.addMissingField("project.partner[" + c + "].contactPersons.empty");
        } else {
          j = 0;
          // iterating all the contact persons.
          for (ProjectPartnerPerson person : partner.getPartnerPersons()) {
            this.validatePersonType(action, c, j, person);
            this.validateUser(action, c, j, person);
            if (project.isProjectEditLeader()) {
              this.validatePersonResponsibilities(action, c, j, person);
            }

            j++;
          }
        }
        c++;
      }
    }
  }

  // Validate that an institution is selected.
  private void validateInstitutionsEmpty(BaseAction action, Project project) {
    int c = 0;
    for (ProjectPartner partner : project.getPartners()) {
      if (partner.getInstitution() == null || partner.getInstitution().getId() == -1) {

        action.addFieldError("project.projectPartners[" + c + "].institution",
          action.getText("validation.required", new String[] {action.getText("projectPartners.partner.name")}));
        // No need to add missing fields because field error doesn't allow to save into the database.


      }
      c++;
    }
  }

  private void validatePersonResponsibilities(BaseAction action, int partnerCounter, int personCounter,
    ProjectPartnerPerson person) {
    if (!projectValidator.isValidPersonResponsibilities(person.getResponsibilities())) {
      if (person.getUser() != null && (person.getUser() != null || person.getUser().getId() != -1)) {
        person.setUser(userManager.getUser(person.getUser().getId()));
        this.addMessage(action.getText("projectPartners.responsibilities.for",
          new String[] {person.getUser().getFirstName() + " " + person.getUser().getLastName()}));
      }
      this.addMissingField(
        "project.projectPartners[" + partnerCounter + "].partnerPersons[" + personCounter + "].responsibilities");
    }

  }

  private void validatePersonType(BaseAction action, int partnerCounter, int personCounter,
    ProjectPartnerPerson person) {
    if (person.getContactType() == null || person.getContactType().isEmpty()) {
      action.addFieldError("project.projectPartners[" + partnerCounter + "].partnerPersons[" + personCounter + "].type",
        action.getText("validation.required", new String[] {action.getText("projectPartners.personType")}));
      // No need to add missing fields because field error doesn't allow to save into the database.
    }
  }


  private void validateProjectLeader(BaseAction action, Project project) {
    // All projects must specify the project leader
    if (!projectValidator.isValidLeader(project.getLeader(), project.isBilateralProject())) {
      this.addMessage(action.getText("projectPartners.types.PL").toLowerCase());
      this.addMissingField("project.leader");
    }
  }

  private void validateUser(BaseAction action, int partnerCounter, int personCounter, ProjectPartnerPerson person) {
    try {
      if (person.getUser() == null) {

        action.addFieldError("partner-" + partnerCounter + "-person-" + personCounter,
          action.getText("validation.required", new String[] {action.getText("projectPartners.contactPersonEmail")}));
        // No need to add missing fields because field error doesn't allow to save into the database.


      } else {
        if (person.getUser().getId() == null || person.getUser().getId() == -1) {
          action.addFieldError("partner-" + partnerCounter + "-person-" + personCounter,
            action.getText("validation.required", new String[] {action.getText("projectPartners.contactPersonEmail")}));
        }
      }
    } catch (Exception e) {

      e.printStackTrace();
    }
  }


}
