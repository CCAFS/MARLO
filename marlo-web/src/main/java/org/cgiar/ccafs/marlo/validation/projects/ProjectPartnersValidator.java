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
import org.cgiar.ccafs.marlo.data.manager.GlobalUnitManager;
import org.cgiar.ccafs.marlo.data.manager.InstitutionManager;
import org.cgiar.ccafs.marlo.data.manager.ProjectManager;
import org.cgiar.ccafs.marlo.data.model.GlobalUnit;
import org.cgiar.ccafs.marlo.data.model.Institution;
import org.cgiar.ccafs.marlo.data.model.Project;
import org.cgiar.ccafs.marlo.data.model.ProjectPartner;
import org.cgiar.ccafs.marlo.data.model.ProjectPartnerPerson;
import org.cgiar.ccafs.marlo.data.model.ProjectSectionStatusEnum;
import org.cgiar.ccafs.marlo.utils.InvalidFieldsMessages;
import org.cgiar.ccafs.marlo.validation.BaseValidator;
import org.cgiar.ccafs.marlo.validation.model.ProjectValidator;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.stream.Collectors;

import javax.inject.Inject;
import javax.inject.Named;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * @author Hernán David Carvajal B. - CIAT/CCAFS
 * @author Héctor Fabio Tobón R. - CIAT/CCAFS
 */
@Named
public class ProjectPartnersValidator extends BaseValidator {

  private static final Logger LOG = LoggerFactory.getLogger(ProjectPartnersValidator.class);


  // GlobalUnit Manager
  private GlobalUnitManager crpManager;

  private ProjectManager projectManager;

  private ProjectValidator projectValidator;

  private InstitutionManager institutionManager;

  @Inject
  public ProjectPartnersValidator(ProjectValidator projectValidator, GlobalUnitManager crpManager,
    InstitutionManager institutionManager) {
    super();
    this.projectValidator = projectValidator;
    this.crpManager = crpManager;
    this.institutionManager = institutionManager;
  }

  private Path getAutoSaveFilePath(Project project, long crpID, BaseAction action) {
    GlobalUnit crp = crpManager.getGlobalUnitById(crpID);
    String composedClassName = project.getClass().getSimpleName();
    String actionFile = ProjectSectionStatusEnum.PARTNERS.getStatus().replace("/", "_");
    String autoSaveFile = project.getId() + "_" + composedClassName + "_" + action.getActualPhase().getDescription()
      + "_" + action.getActualPhase().getYear() + "_" + crp.getAcronym() + "_" + actionFile + ".json";

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


  /**
   * Returns false if no errors and false if there are errors
   * 
   * @param action
   * @param project
   * @param saving
   * @return
   */
  public boolean validate(BaseAction action, Project project, boolean saving) {
    boolean hasErros = false;
    action.setInvalidFields(new HashMap<>());

    if (project != null) {
      if (!saving) {
        Path path = this.getAutoSaveFilePath(project, action.getCrpID(), action);

        if (path.toFile().exists()) {
          action.addMissingField("draft");
        }
      }

      if (project.getPartners() != null && !project.getPartners().isEmpty()) {

        if (action.isReportingActive() && project.getProjecInfoPhase(action.getActualPhase()).isProjectEditLeader()) {
          if (!this.isValidString(project.getOverall())) {
            action.addMessage(
              action.getText("Please provide Partnerships overall performance over the last reporting period"));
            action.addMissingField("project.partners.overall");
            action.getInvalidFields().put("input-project.overall", InvalidFieldsMessages.EMPTYFIELD);
          }
        }

      }


      if (project.getPartners() == null || project.getPartners().isEmpty()) {
        action.addMissingField("project.partners.empty");
        action.getInvalidFields().put("list-project.partners",
          action.getText(InvalidFieldsMessages.EMPTYLIST, new String[] {"Partners"}));
      }
      if (project.getProjecInfoPhase(action.getActualPhase()).isProjectEditLeader()) {
        /*
         * if (!action.isProjectNew(project.getId())) {
         * this.validateLessonsLearn(action, project);
         * if (action.getValidationMessage().toString().contains("Lessons")) {
         * this.replaceAll(action.getValidationMessage(), "Lessons",
         * "Lessons regarding partnerships and possible implications for the coming planning cycle");
         * action.getInvalidFields().put("input-project.projectComponentLesson.lessons",
         * InvalidFieldsMessages.EMPTYFIELD);
         * }
         * }/
         */

        if (project.getProjectInfo().getNewPartnershipsPlanned() == null
          || project.getProjectInfo().getNewPartnershipsPlanned().trim().isEmpty()) {
          action.addMissingField("project.projectInfo.newPartnershipsPlanned");
          action.getInvalidFields().put("input-project.projectInfo.newPartnershipsPlanned",
            action.getText("Please provide new partnerships  planned for " + action.getActualPhase().getYear()));
        }
      }

      this.validateCCAFSProject(action, project);

      if (!action.getFieldErrors().isEmpty()) {
        hasErros = true;
        action.addActionError(action.getText("saving.fields.required"));
      } else if (action.getValidationMessage().length() > 0) {
        action.addActionMessage(
          " " + action.getText("saving.missingFields", new String[] {action.getValidationMessage().toString()}));
      }

      this.saveMissingFields(project, action.getActualPhase().getDescription(), action.getActualPhase().getYear(),
        ProjectSectionStatusEnum.PARTNERS.getStatus(), action);
    }

    return hasErros;
  }

  private void validateCCAFSProject(BaseAction action, Project project) {
    this.validateInstitutionsEmpty(action, project);
    this.validateProjectLeader(action, project);
    if (action.hasSpecificities(APConstants.CRP_MANAGING_PARTNERS_CONTACT_PERSONS)) {
      this.validateContactPersons(action, project);
    }
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
          if (project.getProjecInfoPhase(action.getActualPhase()).isProjectEditLeader()) {
            if (action.hasSpecificities(APConstants.CRP_PARTNER_CONTRIBUTIONS)) {
              this.validatePersonResponsibilities(action, c, partner);
            }
          }
          if (project.getProjecInfoPhase(action.getActualPhase()).isProjectEditLeader()) {
            Institution inst = institutionManager.getInstitutionById(partner.getInstitution().getId());
            if (inst.getCrpPpaPartners().stream()
              .filter(insti -> insti.isActive() && insti.getCrp().getId().longValue() == action.getCrpID().longValue())
              .collect(Collectors.toList()).isEmpty()) {


              if (partner.getPartnerContributors() == null || partner.getPartnerContributors().isEmpty()) {
                action.addMissingField("project.partners[" + c + "].partnerContributors");
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
              action.addMissingField("project.partner[" + c + "].contactPersons.empty");
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
          }
          c++;
        }
      }
    } catch (Exception e) {
      LOG.error("unable to validate contact persons for project " + project,
        e);/**
            * Original code swallows the exception and didn't even log it. Now we at least log it,
            * but we need to revisit to see if we should continue processing or re-throw the exception.
            */
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

        action.addMissingField("project.projectPartners[" + c + "].selectedLocations");
        action.getInvalidFields().put("list-project.partners[" + c + "].selectedLocations",
          action.getText(InvalidFieldsMessages.EMPTYLIST, new String[] {"Offices"}));
      } else {

        if (partner.getSelectedLocations().isEmpty()) {
          action.addMissingField("project.projectPartners[" + c + "].selectedLocations");
          action.getInvalidFields().put("list-project.partners[" + c + "].selectedLocations",
            action.getText(InvalidFieldsMessages.EMPTYLIST, new String[] {"Offices"}));
        }
      }

      c++;
    }

  }

  private void validatePersonResponsibilities(BaseAction action, int partnerCounter, ProjectPartner partner) {
    if (!projectValidator.isValidPersonResponsibilities(partner.getResponsibilities())) {

      action.addMissingField("project.projectPartners[" + partnerCounter + "].responsibilities");
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
    if (!projectValidator.isValidLeader(project.getLeader())) {
      action.addMessage(action.getText("projectPartners.types.PL").toLowerCase());
      action.getInvalidFields().put("list-project.partners", action.getText("projectPartners.types.PL"));
      action.addMissingField("project.leader");
    }
  }

  private void validateUser(BaseAction action, int partnerCounter, int personCounter, ProjectPartnerPerson person) {
    try {
      if (person.getUser() == null) {

        // action.addFieldError("partner-" + partnerCounter + "-person-" + personCounter,
        // action.getText("validation.required", new String[] {action.getText("projectPartners.contactPersonEmail")}));
        // No need to add missing fields because field error doesn't allow to save into the database.
        action.addMessage(action.getText("input-partner-" + partnerCounter + "-person-" + personCounter));
        action.getInvalidFields().put("input-partner-" + partnerCounter + "-person-" + personCounter,
          InvalidFieldsMessages.EMPTYFIELD);

      } else {
        if (person.getUser().getId() == null || person.getUser().getId() == -1) {
          action.addMessage(action.getText("input-partner-" + partnerCounter + "-person-" + personCounter));
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
