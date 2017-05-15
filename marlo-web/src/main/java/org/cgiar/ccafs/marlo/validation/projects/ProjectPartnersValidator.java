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
import org.cgiar.ccafs.marlo.data.manager.InstitutionManager;
import org.cgiar.ccafs.marlo.data.manager.ProjectManager;
import org.cgiar.ccafs.marlo.data.manager.UserManager;
import org.cgiar.ccafs.marlo.data.model.Crp;
import org.cgiar.ccafs.marlo.data.model.Institution;
import org.cgiar.ccafs.marlo.data.model.InstitutionLocation;
import org.cgiar.ccafs.marlo.data.model.Project;
import org.cgiar.ccafs.marlo.data.model.ProjectPartner;
import org.cgiar.ccafs.marlo.data.model.ProjectPartnerPerson;
import org.cgiar.ccafs.marlo.data.model.ProjectSectionStatusEnum;
import org.cgiar.ccafs.marlo.utils.InvalidFieldsMessages;
import org.cgiar.ccafs.marlo.validation.BaseValidator;
import org.cgiar.ccafs.marlo.validation.model.ProjectValidator;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.stream.Collectors;

import com.google.inject.Inject;


/**
 * @author Hernán David Carvajal B. - CIAT/CCAFS
 * @author Héctor Fabio Tobón R. - CIAT/CCAFS
 */

public class ProjectPartnersValidator extends BaseValidator {

  @Inject
  private CrpManager crpManager;
  @Inject
  private ProjectManager projectManager;


  @Inject
  private InstitutionManager institutionManager;
  private boolean hasErros;
  private ProjectValidator projectValidator;

  private UserManager userManager;

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

    action.setInvalidFields(new HashMap<>());
    hasErros = false;
    if (project != null) {
      if (!saving) {
        Path path = this.getAutoSaveFilePath(project, action.getCrpID());

        if (path.toFile().exists()) {
          this.addMissingField("draft");
        }
      }

      Project projectDb = projectManager.getProjectById(project.getId());
      if (project.getPartners() != null && !project.getPartners().isEmpty()) {

        if (action.isReportingActive() && project.isProjectEditLeader()) {
          if (!this.isValidString(project.getOverall())) {
            this.addMessage(
              action.getText("Please provide Partnerships overall performance over the last reporting period"));
            this.addMissingField("project.partners.overall");
            action.getInvalidFields().put("input-project.overall", InvalidFieldsMessages.EMPTYFIELD);
          }
        }

      }


      if (project.getPartners() == null || project.getPartners().isEmpty()) {
        this.addMissingField("project.partners.empty");
        action.getInvalidFields().put("list-project.partners",
          action.getText(InvalidFieldsMessages.EMPTYLIST, new String[] {"Partners"}));
      }
      if (project.isProjectEditLeader()) {
        if (!action.isProjectNew(project.getId())) {
          this.validateLessonsLearn(action, project);
          if (this.validationMessage.toString().contains("Lessons")) {
            this.replaceAll(validationMessage, "Lessons",
              "Lessons regarding partnerships and possible implications for the coming planning cycle");
            action.getInvalidFields().put("input-project.projectComponentLesson.lessons",
              InvalidFieldsMessages.EMPTYFIELD);
          }
        }

      }
      this.validateCCAFSProject(action, project);

      if (!action.getFieldErrors().isEmpty()) {
        hasErros = true;
        action.addActionError(action.getText("saving.fields.required"));
        System.out.println(action.getFieldErrors());


      } else if (validationMessage.length() > 0) {
        action
          .addActionMessage(" " + action.getText("saving.missingFields", new String[] {validationMessage.toString()}));
        if (action.isReportingActive()) {
          this.saveMissingFields(project, APConstants.REPORTING, action.getReportingYear(),
            ProjectSectionStatusEnum.PARTNERS.getStatus());
        } else {
          this.saveMissingFields(project, APConstants.PLANNING, action.getPlanningYear(),
            ProjectSectionStatusEnum.PARTNERS.getStatus());
        }
      }


    }
  }

  private void validateCCAFSProject(BaseAction action, Project project) {
    this.validateInstitutionsEmpty(action, project);
    this.validateProjectLeader(action, project);
    this.validateContactPersons(action, project);
    if (action.hasSpecificities(APConstants.CRP_PARTNERS_OFFICE)) {
      this.validateOffices(action, project);
    }
  }


  /**
   * This method validates all the required fields within contact person.
   * 
   * @param action this action.
   * @param project the project with the partners on it.
   */
  private void validateContactPersons(BaseAction action, Project project) {
    try {
      if (project != null) {
        int c = 0, j = 0;
        for (ProjectPartner partner : project.getPartners()) {
          j = 0;
          // Validating that the partner has a least one contact person
          if (project.isProjectEditLeader()) {
            if (action.hasSpecificities(APConstants.CRP_PARTNER_CONTRIBUTIONS)) {

              this.validatePersonResponsibilities(action, c, partner);
            }
          }
          if (project.isProjectEditLeader()) {
            Institution inst = institutionManager.getInstitutionById(partner.getInstitution().getId());
            if (inst.getCrpPpaPartners().stream()
              .filter(insti -> insti.isActive() && insti.getCrp().getId().longValue() == action.getCrpID().longValue())
              .collect(Collectors.toList()).isEmpty()) {


              if (partner.getPartnerContributors() == null || partner.getPartnerContributors().isEmpty()) {
                this.addMissingField("project.partners[" + c + "].partnerContributors");
                action.getInvalidFields().put("list-project.partners[" + c + "].partnerContributors",
                  action.getText(InvalidFieldsMessages.EMPTYLIST, new String[] {"Partner Contribution"}));

              }


            }
          }
          Institution inst = institutionManager.getInstitutionById(partner.getInstitution().getId());


          if (partner.getPartnerPersons() == null || partner.getPartnerPersons().isEmpty()) {
            if (!inst.getCrpPpaPartners().stream()
              .filter(insti -> insti.isActive() && insti.getCrp().getId().longValue() == action.getCrpID().longValue())
              .collect(Collectors.toList()).isEmpty()) {
              action.addActionMessage(action.getText("planning.projectPartners.contactPersons.empty",
                new String[] {partner.getInstitution().getName()}));
              this.addMissingField("project.partner[" + c + "].contactPersons.empty");
            }

          } else {
            j = 0;
            // iterating all the contact persons.
            for (ProjectPartnerPerson person : partner.getPartnerPersons()) {
              if (!inst.getCrpPpaPartners().stream()
                .filter(
                  insti -> insti.isActive() && insti.getCrp().getId().longValue() == action.getCrpID().longValue())
                .collect(Collectors.toList()).isEmpty()) {
                this.validatePersonType(action, c, j, person);

                this.validateUser(action, c, j, person);
              }

              j++;

            }


            c++;
          }
        }
      }
    } catch (Exception e) {

    }
  }

  // Validate that an institution is selected.
  private void validateInstitutionsEmpty(BaseAction action, Project project) {
    int c = 0;
    for (ProjectPartner partner : project.getPartners()) {
      if (partner.getInstitution() == null || partner.getInstitution().getId() == -1) {

        action.addFieldError("project.partners[" + c + "].institution.id",
          action.getText("validation.required", new String[] {action.getText("projectPartners.partner.name")}));
        // No need to add missing fields because field error doesn't allow to save into the database.


      }
      c++;
    }
  }

  private void validateOffices(BaseAction action, Project project) {
    int c = 0;
    for (ProjectPartner partner : project.getPartners()) {
      if (partner.getSelectedLocations() == null) {
        partner.setSelectedLocations(new ArrayList<InstitutionLocation>());
      }
      if (partner.getSelectedLocations().isEmpty()) {
        this.addMissingField("project.projectPartners[" + c + "].selectedLocations");
        action.getInvalidFields().put("list-project.partners[" + c + "].selectedLocations",
          action.getText(InvalidFieldsMessages.EMPTYLIST, new String[] {"Offices"}));
      }
      c++;
    }

  }

  private void validatePersonResponsibilities(BaseAction action, int partnerCounter, ProjectPartner partner) {
    if (!projectValidator.isValidPersonResponsibilities(partner.getResponsibilities())) {

      this.addMissingField("project.projectPartners[" + partnerCounter + "].responsibilities");
      action.getInvalidFields().put("input-project.partners[" + partnerCounter + "].responsibilities",
        InvalidFieldsMessages.EMPTYFIELD);
    }

  }

  private void validatePersonType(BaseAction action, int partnerCounter, int personCounter,
    ProjectPartnerPerson person) {
    if (person.getContactType() == null || person.getContactType().isEmpty() || person.getContactType().equals("-1")) {
      action.addFieldError("project.partners[" + partnerCounter + "].partnerPersons[" + personCounter + "].contactType",
        action.getText("validation.required", new String[] {action.getText("projectPartners.personType")}));
      // No need to add missing fields because field error doesn't allow to save into the database.
    }
  }


  private void validateProjectLeader(BaseAction action, Project project) {
    // All projects must specify the project leader
    if (!projectValidator.isValidLeader(project.getLeader(), project.isBilateralProject())) {
      this.addMessage(action.getText("projectPartners.types.PL").toLowerCase());
      action.getInvalidFields().put("list-project.partners", action.getText("projectPartners.types.PL"));
      this.addMissingField("project.leader");
    }
  }

  private void validateUser(BaseAction action, int partnerCounter, int personCounter, ProjectPartnerPerson person) {
    try {
      if (person.getUser() == null) {

        // action.addFieldError("partner-" + partnerCounter + "-person-" + personCounter,
        // action.getText("validation.required", new String[] {action.getText("projectPartners.contactPersonEmail")}));
        // No need to add missing fields because field error doesn't allow to save into the database.
        this.addMessage(action.getText("input-partner-" + partnerCounter + "-person-" + personCounter));
        action.getInvalidFields().put("input-partner-" + partnerCounter + "-person-" + personCounter,
          InvalidFieldsMessages.EMPTYFIELD);

      } else {
        if (person.getUser().getId() == null || person.getUser().getId() == -1) {
          this.addMessage(action.getText("input-partner-" + partnerCounter + "-person-" + personCounter));
          action.getInvalidFields().put("input-partner-" + partnerCounter + "-person-" + personCounter,
            InvalidFieldsMessages.EMPTYFIELD);
          person.setUser(null);

        }
      }
    } catch (Exception e) {

      e.printStackTrace();
    }
  }


}
