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


package org.cgiar.ccafs.marlo.action.projects;

import org.cgiar.ccafs.marlo.action.BaseAction;
import org.cgiar.ccafs.marlo.config.APConstants;
import org.cgiar.ccafs.marlo.data.manager.CrpManager;
import org.cgiar.ccafs.marlo.data.manager.CrpPpaPartnerManager;
import org.cgiar.ccafs.marlo.data.manager.InstitutionManager;
import org.cgiar.ccafs.marlo.data.manager.InstitutionTypeManager;
import org.cgiar.ccafs.marlo.data.manager.LocElementManager;
import org.cgiar.ccafs.marlo.data.manager.ProjectManager;
import org.cgiar.ccafs.marlo.data.manager.ProjectPartnerManager;
import org.cgiar.ccafs.marlo.data.manager.ProjectPartnerOverallManager;
import org.cgiar.ccafs.marlo.data.manager.RoleManager;
import org.cgiar.ccafs.marlo.data.manager.UserManager;
import org.cgiar.ccafs.marlo.data.manager.UserRoleManager;
import org.cgiar.ccafs.marlo.data.model.Crp;
import org.cgiar.ccafs.marlo.data.model.CrpPpaPartner;
import org.cgiar.ccafs.marlo.data.model.Institution;
import org.cgiar.ccafs.marlo.data.model.InstitutionType;
import org.cgiar.ccafs.marlo.data.model.LocElement;
import org.cgiar.ccafs.marlo.data.model.Project;
import org.cgiar.ccafs.marlo.data.model.ProjectPartner;
import org.cgiar.ccafs.marlo.data.model.ProjectPartnerContribution;
import org.cgiar.ccafs.marlo.data.model.ProjectPartnerOverall;
import org.cgiar.ccafs.marlo.data.model.ProjectPartnerPerson;
import org.cgiar.ccafs.marlo.data.model.Role;
import org.cgiar.ccafs.marlo.data.model.User;
import org.cgiar.ccafs.marlo.data.model.UserRole;
import org.cgiar.ccafs.marlo.security.Permission;
import org.cgiar.ccafs.marlo.utils.APConfig;
import org.cgiar.ccafs.marlo.utils.SendMail;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.google.inject.Inject;
import com.opensymphony.xwork2.ActionContext;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;

public class ProjectPartnerAction extends BaseAction {


  /**
   * 
   */
  private static final long serialVersionUID = 7833194831832715444L;

  private ProjectPartnerManager projectPartnerManager;
  private ProjectPartnerOverallManager projectPartnerOverallManager;
  private InstitutionManager institutionManager;
  private InstitutionTypeManager institutionTypeManager;
  private LocElementManager locationManager;
  private UserManager userManager;
  private UserRoleManager userRoleManager;
  private RoleManager roleManager;
  private ProjectManager projectManager;
  private CrpPpaPartnerManager crpPpaPartnerManager;
  private CrpManager crpManager;
  private String overall;
  private long projectID;
  private Crp loggedCrp;
  private Project previousProject;

  private Project project;
  // Model for the view
  private List<InstitutionType> intitutionTypes;
  private Map<String, String> partnerPersonTypes; // List of partner person types (CP, PL, PC).

  private List<LocElement> countries;
  private List<Institution> allInstitutions; // Is used to list all the partner institutions that have the system.
  private List<Institution> allPPAInstitutions; // Is used to list all the PPA partners institutions
  private List<ProjectPartner> projectPPAPartners; // Is used to list all the PPA partners that belongs to the project.
  private List<User> allUsers; // will be used to list all the project leaders that have the system.
  private Role plRole;
  private Role pcRole;


  // Util
  private SendMail sendMail;

  @Inject
  public ProjectPartnerAction(APConfig config, ProjectPartnerManager projectPartnerManager,
    InstitutionManager institutionManager, LocElementManager locationManager, ProjectManager projectManager,
    CrpPpaPartnerManager crpPpaPartnerManager, CrpManager crpManager,
    ProjectPartnerOverallManager projectPartnerOverallManager, UserManager userManager,
    InstitutionTypeManager institutionTypeManager, SendMail sendMail, RoleManager roleManager) {
    super(config);

    this.projectPartnerManager = projectPartnerManager;
    this.institutionManager = institutionManager;
    this.institutionTypeManager = institutionTypeManager;
    this.locationManager = locationManager;
    this.projectManager = projectManager;
    this.userManager = userManager;
    this.crpManager = crpManager;
    this.crpPpaPartnerManager = crpPpaPartnerManager;
    this.projectPartnerOverallManager = projectPartnerOverallManager;
    this.sendMail = sendMail;
    this.roleManager = roleManager;
  }


  public List<Institution> getAllInstitutions() {
    return allInstitutions;
  }

  public List<Institution> getAllPPAInstitutions() {
    return allPPAInstitutions;
  }

  public List<User> getAllUsers() {
    return allUsers;
  }


  public List<LocElement> getCountries() {
    return countries;
  }


  public List<InstitutionType> getIntitutionTypes() {
    return intitutionTypes;
  }


  public Crp getLoggedCrp() {
    return loggedCrp;
  }


  public String getOverall() {
    return overall;
  }


  public Map<String, String> getPartnerPersonTypes() {
    return partnerPersonTypes;
  }


  public Project getProject() {
    return project;
  }


  public long getProjectID() {
    return projectID;
  }


  public List<ProjectPartner> getProjectPPAPartners() {
    return projectPPAPartners;
  }


  public boolean isPPA(Institution institution) {
    if (institution == null) {
      return false;
    }
    institution = institutionManager.getInstitutionById(institution.getId());
    if (institution.getCrpPpaPartners().stream().filter(c -> c.isActive()).collect(Collectors.toList()).size() > 0) {
      return true;
    }
    return false;
  }


  /**
   * This method will validate if the user is deactivated. If so, it will send an email indicating the credentials to
   * access.
   * 
   * @param leader is a PartnerPerson object that could be the leader or the coordinator.
   */
  private void notifyNewUserCreated(User user) {

    if (!user.isActive()) {

      user.setActive(true);
      // Building the Email message:
      StringBuilder message = new StringBuilder();
      message.append(this.getText("email.dear", new String[] {user.getFirstName()}));
      message.append(this.getText("email.newUser.part1"));
      message.append(this.getText("email.newUser.part2"));

      String password = this.getText("planning.manageUsers.email.outlookPassword");
      if (!user.isCgiarUser()) {
        // Generating a random password.
        password = RandomStringUtils.randomNumeric(6);
        // Applying the password to the user.
        user.setPassword(password);
      }
      message
        .append(this.getText("email.newUser.part3", new String[] {config.getBaseUrl(), user.getEmail(), password}));
      message.append(this.getText("email.support"));
      message.append(this.getText("email.bye"));

      // Saving the new user configuration.
      userManager.saveUser(user, this.getCurrentUser());

      String toEmail = null;
      if (config.isProduction()) {
        // Send email to the new user and the P&R notification email.
        // TO
        toEmail = user.getEmail();
      }
      // BBC
      String bbcEmails = this.config.getEmailNotification();
      sendMail.send(toEmail, null, bbcEmails,
        this.getText("email.newUser.subject", new String[] {user.getComposedName()}), message.toString(), null, null,
        null, false);
    }
  }

  @Override
  public void prepare() throws Exception {
    projectID = Long.parseLong(StringUtils.trim(this.getRequest().getParameter(APConstants.PROJECT_REQUEST_ID)));
    loggedCrp = (Crp) this.getSession().get(APConstants.SESSION_CRP);
    loggedCrp = crpManager.getCrpById(loggedCrp.getId());

    // Getting the project identified with the id parameter.
    project = projectManager.getProjectById(projectID);
    String params[] = {loggedCrp.getAcronym(), project.getId() + ""};
    this.setBasePermission(this.getText(Permission.PROJECT_PARTNER_BASE_PERMISSION, params));
    plRole = roleManager.getRoleById(Long.parseLong((String) this.getSession().get(APConstants.CRP_PL_ROLE)));
    pcRole = roleManager.getRoleById(Long.parseLong((String) this.getSession().get(APConstants.CRP_PC_ROLE)));

    // Getting the list of all institutions
    allInstitutions = institutionManager.findAll();

    // Getting the list of all PPA institutions
    allPPAInstitutions = new ArrayList<>();
    for (CrpPpaPartner crpPpaPartner : crpPpaPartnerManager.findAll().stream().filter(c -> c.isActive())
      .collect(Collectors.toList())) {
      allPPAInstitutions.add(crpPpaPartner.getInstitution());
    }

    // Getting all the countries
    countries = locationManager.findAll().stream().filter(c -> c.isActive() && c.getLocElementType().getId() == 2)
      .collect(Collectors.toList());

    // Getting all partner types
    intitutionTypes = institutionTypeManager.findAll();

    project.setPartners(project.getProjectPartners().stream().filter(c -> c.isActive()).collect(Collectors.toList()));
    if (!project.getPartners().isEmpty()) {
      List<ProjectPartnerOverall> overalls = project.getPartners().get(0).getProjectPartnerOveralls().stream()
        .filter(c -> c.isActive()).collect(Collectors.toList());
      if (!overalls.isEmpty()) {
        overall = overalls.get(0).getOverall();
      }
    }
    for (ProjectPartner projectPartner : project.getPartners()) {
      projectPartner.setPartnerPersons(
        projectPartner.getProjectPartnerPersons().stream().filter(c -> c.isActive()).collect(Collectors.toList()));


    }
    ProjectPartner leader = project.getLeader();
    if (leader != null) {
      // First we remove the element from the array.
      project.getPartners().remove(leader);
      // then we add it to the first position.
      project.getPartners().add(0, leader);
    }
    // Getting the list of PPA Partners for this project
    this.projectPPAPartners = new ArrayList<ProjectPartner>();
    for (ProjectPartner pp : project.getPartners()) {

      if (pp.getInstitution().getCrpPpaPartners().stream().filter(c -> c.isActive()).collect(Collectors.toList())
        .size() > 0) {
        this.projectPPAPartners.add(pp);

      }

      List<ProjectPartner> contributors = new ArrayList<>();


      List<ProjectPartnerContribution> partnerContributions =
        pp.getProjectPartnerContributions().stream().filter(c -> c.isActive()).collect(Collectors.toList());
      for (ProjectPartnerContribution projectPartnerContribution : partnerContributions) {
        contributors.add(projectPartnerContribution.getProjectPartnerContributor());
      }
      pp.setPartnerContributors(contributors);
    }

    partnerPersonTypes = new HashMap<>();
    partnerPersonTypes.put(APConstants.PROJECT_PARTNER_CP, this.getText("projectPartners.types.CP"));

    if (this.hasPermission("leader")) {
      partnerPersonTypes.put(APConstants.PROJECT_PARTNER_PL, this.getText("projectPartners.types.PL"));
    }
    if (this.hasPermission("coordinator")) {
      partnerPersonTypes.put(APConstants.PROJECT_PARTNER_PC, this.getText("projectPartners.types.PC"));
    }

    if (this.isHttpPost()) {

      if (ActionContext.getContext().getName().equals("partners") && project.getPartners() != null) {
        project.getProjectPartners().clear();
      }
    }

  }


  @Override
  public String save() {
    if (this.hasPermission("update")) {

      previousProject = projectManager.getProjectById(projectID);
      for (ProjectPartner previousPartner : previousProject.getProjectPartners()) {
        if (!project.getPartners().contains(previousPartner)) {
          projectPartnerManager.deleteProjectPartner(previousPartner.getId());
          // budgetManager.deleteBudgetsByInstitution(project.getId(), previousPartner.getInstitution(),
          // this.getCurrentUser(), this.getJustification());
        }

        for (ProjectPartner projectPartner : project.getPartners()) {
          if (projectPartner.getId() == null) {
            projectPartner.setActive(true);

            projectPartner.setCreatedBy(this.getCurrentUser());
            projectPartner.setModifiedBy(this.getCurrentUser());
            projectPartner.setModificationJustification("");
            projectPartner.setActiveSince(new Date());

          } else {
            ProjectPartner db = projectPartnerManager.getProjectPartnerById(projectPartner.getId());
            projectPartner.setActive(true);
            projectPartner.setCreatedBy(db.getCreatedBy());
            projectPartner.setModifiedBy(this.getCurrentUser());
            projectPartner.setModificationJustification("");
            projectPartner.setActiveSince(db.getActiveSince());
          }

          ProjectPartnerPerson leader = project.getLeaderPerson();
          // Notify user if the project leader was created.
          if (leader != null && previousProject.getLeaderPerson() == null) {
            this.notifyNewUserCreated(leader.getUser());
            UserRole userRole = new UserRole();
            userRole.setRole(plRole);
            userRole.setUser(leader.getUser());

            plRole = roleManager.getRoleById(plRole.getId());
            if (!plRole.getUserRoles().contains(userRole)) {
              userRoleManager.saveUserRole(userRole);
            }


          }

          ProjectPartnerPerson previousCoordinator = null;
          if (previousProject.getCoordinatorPersons().size() > 0) {
            previousCoordinator = previousProject.getCoordinatorPersons().get(0);
          }
          ProjectPartnerPerson coordinator = null;
          if (project.getCoordinatorPersons() != null) {
            if (project.getCoordinatorPersons().size() > 0) {
              coordinator = project.getCoordinatorPersons().get(0);
            }
          }

          if (coordinator != null && previousCoordinator == null) {
            if (!previousCoordinator.equals(coordinator)) {
              this.notifyNewUserCreated(coordinator.getUser());
              UserRole userRole = new UserRole();
              userRole.setRole(pcRole);
              userRole.setUser(leader.getUser());

              pcRole = roleManager.getRoleById(pcRole.getId());
              if (!pcRole.getUserRoles().contains(userRole)) {
                userRoleManager.saveUserRole(userRole);
              }
            }

          }

          projectPartnerManager.saveProjectPartner(projectPartner);
          Collection<String> messages = this.getActionMessages();
          if (!messages.isEmpty()) {
            String validationMessage = messages.iterator().next();
            this.setActionMessages(null);
            this.addActionWarning(this.getText("saving.saved") + validationMessage);
          } else {
            this.addActionMessage(this.getText("saving.saved"));
          }
        }
      }
      return SUCCESS;
    }
    return NOT_AUTHORIZED;
  }


  public void setAllInstitutions(List<Institution> allInstitutions) {
    this.allInstitutions = allInstitutions;
  }


  public void setAllPPAInstitutions(List<Institution> allPPAInstitutions) {
    this.allPPAInstitutions = allPPAInstitutions;
  }


  public void setAllUsers(List<User> allUsers) {
    this.allUsers = allUsers;
  }


  public void setCountries(List<LocElement> countries) {
    this.countries = countries;
  }


  public void setIntitutionTypes(List<InstitutionType> intitutionTypes) {
    this.intitutionTypes = intitutionTypes;
  }


  public void setLoggedCrp(Crp loggedCrp) {
    this.loggedCrp = loggedCrp;
  }


  public void setOverall(String overall) {
    this.overall = overall;
  }


  public void setPartnerPersonTypes(Map<String, String> partnerPersonTypes) {
    this.partnerPersonTypes = partnerPersonTypes;
  }


  public void setProject(Project project) {
    this.project = project;
  }


  public void setProjectID(long projectID) {
    this.projectID = projectID;
  }

  public void setProjectPPAPartners(List<ProjectPartner> projectPPAPartners) {
    this.projectPPAPartners = projectPPAPartners;
  }


}
