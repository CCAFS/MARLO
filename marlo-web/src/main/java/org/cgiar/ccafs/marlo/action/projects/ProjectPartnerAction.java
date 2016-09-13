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
import org.cgiar.ccafs.marlo.data.manager.AuditLogManager;
import org.cgiar.ccafs.marlo.data.manager.CrpManager;
import org.cgiar.ccafs.marlo.data.manager.CrpPpaPartnerManager;
import org.cgiar.ccafs.marlo.data.manager.InstitutionManager;
import org.cgiar.ccafs.marlo.data.manager.InstitutionTypeManager;
import org.cgiar.ccafs.marlo.data.manager.LocElementManager;
import org.cgiar.ccafs.marlo.data.manager.ProjectComponentLessonManager;
import org.cgiar.ccafs.marlo.data.manager.ProjectManager;
import org.cgiar.ccafs.marlo.data.manager.ProjectPartnerContributionManager;
import org.cgiar.ccafs.marlo.data.manager.ProjectPartnerManager;
import org.cgiar.ccafs.marlo.data.manager.ProjectPartnerOverallManager;
import org.cgiar.ccafs.marlo.data.manager.ProjectPartnerPersonManager;
import org.cgiar.ccafs.marlo.data.manager.RoleManager;
import org.cgiar.ccafs.marlo.data.manager.UserManager;
import org.cgiar.ccafs.marlo.data.manager.UserRoleManager;
import org.cgiar.ccafs.marlo.data.model.Crp;
import org.cgiar.ccafs.marlo.data.model.CrpPpaPartner;
import org.cgiar.ccafs.marlo.data.model.Institution;
import org.cgiar.ccafs.marlo.data.model.InstitutionType;
import org.cgiar.ccafs.marlo.data.model.LocElement;
import org.cgiar.ccafs.marlo.data.model.Project;
import org.cgiar.ccafs.marlo.data.model.ProjectComponentLesson;
import org.cgiar.ccafs.marlo.data.model.ProjectPartner;
import org.cgiar.ccafs.marlo.data.model.ProjectPartnerContribution;
import org.cgiar.ccafs.marlo.data.model.ProjectPartnerOverall;
import org.cgiar.ccafs.marlo.data.model.ProjectPartnerPerson;
import org.cgiar.ccafs.marlo.data.model.Role;
import org.cgiar.ccafs.marlo.data.model.User;
import org.cgiar.ccafs.marlo.data.model.UserRole;
import org.cgiar.ccafs.marlo.security.APCustomRealm;
import org.cgiar.ccafs.marlo.security.Permission;
import org.cgiar.ccafs.marlo.utils.APConfig;
import org.cgiar.ccafs.marlo.utils.AutoSaveReader;
import org.cgiar.ccafs.marlo.utils.SendMail;
import org.cgiar.ccafs.marlo.validation.projects.ProjectPartnersValidator;

import java.io.BufferedReader;
import java.io.FileReader;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.inject.Inject;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;

public class ProjectPartnerAction extends BaseAction {


  /**
   * 
   */
  private static final long serialVersionUID = 7833194831832715444L;

  private ProjectPartnerManager projectPartnerManager;
  private ProjectComponentLesson projectComponentLesson;
  private ProjectPartnerPersonManager projectPartnerPersonManager;
  private ProjectPartnerContributionManager projectPartnerContributionManager;

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

  private ProjectPartnersValidator projectPartnersValidator;
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
  private ProjectPartnerOverall partnerOverall;
  private AuditLogManager auditLogManager;
  private String transaction;
  // Util
  private SendMail sendMail;

  @Inject
  public ProjectPartnerAction(APConfig config, ProjectPartnerManager projectPartnerManager,
    InstitutionManager institutionManager, LocElementManager locationManager, ProjectManager projectManager,
    CrpPpaPartnerManager crpPpaPartnerManager, CrpManager crpManager,
    ProjectPartnerOverallManager projectPartnerOverallManager, UserManager userManager,
    InstitutionTypeManager institutionTypeManager, SendMail sendMail, RoleManager roleManager,
    ProjectPartnerContributionManager projectPartnerContributionManager, UserRoleManager userRoleManager,
    ProjectPartnerPersonManager projectPartnerPersonManager, AuditLogManager auditLogManager,
    ProjectComponentLesson projectComponentLesson, ProjectPartnersValidator projectPartnersValidator,
    ProjectComponentLessonManager projectComponentLessonManager) {
    super(config);
    this.projectPartnersValidator = projectPartnersValidator;
    this.auditLogManager = auditLogManager;
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
    this.projectPartnerContributionManager = projectPartnerContributionManager;
    this.userRoleManager = userRoleManager;
    this.projectPartnerPersonManager = projectPartnerPersonManager;
    this.projectComponentLesson = projectComponentLesson;

  }


  @Override
  public String cancel() {

    Path path = this.getAutoSaveFilePath();

    if (path.toFile().exists()) {

      boolean fileDeleted = path.toFile().delete();
      System.out.println(fileDeleted);
    }

    this.setDraft(false);
    Collection<String> messages = this.getActionMessages();
    if (!messages.isEmpty()) {
      String validationMessage = messages.iterator().next();
      this.setActionMessages(null);
      this.addActionWarning(this.getText("cancel.autoSave") + validationMessage);
    } else {
      this.addActionMessage(this.getText("cancel.autoSave"));
    }
    messages = this.getActionMessages();

    return SUCCESS;
  }


  /**
   * This method clears the cache and re-load the user permissions in the next iteration.
   */
  public void clearPermissionsCache() {
    ((APCustomRealm) securityContext.getRealm())
      .clearCachedAuthorizationInfo(securityContext.getSubject().getPrincipals());
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


  private Path getAutoSaveFilePath() {
    String composedClassName = project.getClass().getSimpleName();
    String actionFile = this.getActionName().replace("/", "_");
    String autoSaveFile = project.getId() + "_" + composedClassName + "_" + actionFile + ".json";

    return Paths.get(config.getAutoSaveFolder() + autoSaveFile);
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


  public String getTransaction() {
    return transaction;
  }

  public boolean isPPA(Institution institution) {
    if (institution == null) {
      return false;
    }

    if (institution.getId() != null) {
      institution = institutionManager.getInstitutionById(institution.getId());
      if (institution != null) {
        if (institution.getCrpPpaPartners().stream().filter(c -> c.isActive()).collect(Collectors.toList())
          .size() > 0) {
          return true;
        }
      }

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
    user = userManager.getUser(user.getId());
    Project project = projectManager.getProjectById(this.projectID);
    if (!user.isActive()) {

      user.setActive(true);
      // Building the Email message:
      StringBuilder message = new StringBuilder();
      message.append(this.getText("email.dear", new String[] {user.getFirstName()}));
      message.append(this.getText("email.newUser.part1"));
      message.append(this.getText("email.newUser.part2"));

      String password = this.getText("email.outlookPassword");
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


  /**
   * This method notify the user that is been assigned as Project Leader/Coordinator for a specific project.
   * 
   * @param userAssigned is the user that is being assigned.
   * @param role is the role (Project Leader or Project Coordinator).
   */
  private void notifyRoleAssigned(User userAssigned, Role role) {
    String projectRole = null;
    Project project = projectManager.getProjectById(this.projectID);
    if (role.getId() == plRole.getId()) {
      projectRole = this.getText("projectPartners.types.PL");
    } else {
      projectRole = this.getText("projectPartners.types.PC");
    }
    userAssigned = userManager.getUser(userAssigned.getId());
    StringBuilder message = new StringBuilder();
    // Building the Email message:
    message.append(this.getText("email.dear", new String[] {userAssigned.getFirstName()}));
    message.append(this.getText("email.project.assigned",
      new String[] {projectRole, loggedCrp.getAcronym().toUpperCase(), project.getTitle()}));
    message.append(this.getText("email.support"));
    message.append(this.getText("email.bye"));

    String toEmail = null;
    String ccEmail = null;
    if (config.isProduction()) {
      // Send email to the new user and the P&R notification email.
      // TO
      toEmail = userAssigned.getEmail();
      // CC will be the user who is making the modification.
      if (this.getCurrentUser() != null) {
        ccEmail = this.getCurrentUser().getEmail();
      }
    }
    // BBC will be our gmail notification email.
    String bbcEmails = this.config.getEmailNotification();
    sendMail.send(toEmail, ccEmail, bbcEmails,
      this.getText("email.project.assigned.subject",
        new String[] {projectRole, loggedCrp.getAcronym().toUpperCase(),
          project.getStandardIdentifier(Project.EMAIL_SUBJECT_IDENTIFIER)}),
      message.toString(), null, null, null, true);
  }

  /**
   * This method notify the the user that he/she stopped contributing to a specific project.
   * 
   * @param userUnassigned is the user that stopped contribution.
   * @param role is the user role that stopped contributing (Project Leader or Project Coordinator).
   */
  private void notifyRoleUnassigned(User userUnassigned, Role role) {
    userUnassigned = userManager.getUser(userUnassigned.getId());
    Project project = projectManager.getProjectById(this.projectID);
    String projectRole = null;
    if (role.getId() == plRole.getId().longValue()) {
      projectRole = this.getText("projectPartners.types.PL");
    } else {
      projectRole = this.getText("projectPartners.types.PC");
    }
    StringBuilder message = new StringBuilder();
    // Building the Email message:
    message.append(this.getText("email.dear", new String[] {userUnassigned.getFirstName()}));
    message.append(this.getText("email.project.unAssigned",
      new String[] {projectRole, loggedCrp.getAcronym().toUpperCase(), project.getTitle()}));
    message.append(this.getText("email.support"));
    message.append(this.getText("email.bye"));

    String toEmail = null;
    String ccEmail = null;
    if (config.isProduction()) {
      // Send email to the new user and the P&R notification email.
      // TO
      toEmail = userUnassigned.getEmail();
      // CC will be the user who is making the modification.
      if (this.getCurrentUser() != null) {
        ccEmail = this.getCurrentUser().getEmail();
      }
    }
    // BBC will be our gmail notification email.
    String bbcEmails = this.config.getEmailNotification();
    sendMail.send(toEmail, ccEmail, bbcEmails,
      this.getText("email.project.unAssigned.subject",
        new String[] {projectRole, loggedCrp.getAcronym().toUpperCase(),
          project.getStandardIdentifier(Project.EMAIL_SUBJECT_IDENTIFIER)}),
      message.toString(), null, null, null, true);
  }


  @Override
  public void prepare() throws Exception {
    projectID = Long.parseLong(StringUtils.trim(this.getRequest().getParameter(APConstants.PROJECT_REQUEST_ID)));
    loggedCrp = (Crp) this.getSession().get(APConstants.SESSION_CRP);
    loggedCrp = crpManager.getCrpById(loggedCrp.getId());


    if (this.getRequest().getParameter(APConstants.TRANSACTION_ID) != null) {


      transaction = StringUtils.trim(this.getRequest().getParameter(APConstants.TRANSACTION_ID));
      Project history = (Project) auditLogManager.getHistory(transaction);
      if (history != null) {
        project = history;
      } else {
        this.transaction = null;

        this.setTransaction("-1");
      }

    } else {
      project = projectManager.getProjectById(projectID);
    }


    if (project != null) {
      Path path = this.getAutoSaveFilePath();

      if (path.toFile().exists() && this.getCurrentUser().isAutoSave()) {

        BufferedReader reader = null;

        reader = new BufferedReader(new FileReader(path.toFile()));

        Gson gson = new GsonBuilder().create();


        JsonObject jReader = gson.fromJson(reader, JsonObject.class);

        AutoSaveReader autoSaveReader = new AutoSaveReader();

        project = (Project) autoSaveReader.readFromJson(jReader);
        Project projectDb = projectManager.getProjectById(project.getId());
        project.setProjectEditLeader(projectDb.isProjectEditLeader());
        this.projectPPAPartners = new ArrayList<ProjectPartner>();
        for (ProjectPartner pp : project.getPartners()) {


          if (pp.getInstitution() != null) {

            if (pp.getInstitution().getId() != null || pp.getInstitution().getId() != -1) {
              Institution inst = institutionManager.getInstitutionById(pp.getInstitution().getId());
              if (inst != null) {
                if (inst.getCrpPpaPartners().stream().filter(c -> c.isActive()).collect(Collectors.toList())
                  .size() > 0) {
                  this.projectPPAPartners.add(pp);

                }
                pp.setInstitution(inst);
              }
            }


          }

          if (pp.getPartnerPersons() != null) {
            for (ProjectPartnerPerson projectPartnerPerson : pp.getPartnerPersons()) {

              if (projectPartnerPerson.getUser().getId() != null) {
                projectPartnerPerson.setUser(userManager.getUser(projectPartnerPerson.getUser().getId()));

              }
            }
          }

          if (pp.getPartnerContributors() != null) {
            for (ProjectPartnerContribution projectPartnerContribution : pp.getPartnerContributors()) {

              if (projectPartnerContribution.getProjectPartnerContributor().getInstitution().getId() != null) {
                projectPartnerContribution.getProjectPartnerContributor()
                  .setInstitution(institutionManager.getInstitutionById(
                    projectPartnerContribution.getProjectPartnerContributor().getInstitution().getId()));
              }

            }
          }


        }
        reader.close();
        this.setDraft(true);
      } else {

        this.setDraft(false);
        project
          .setPartners(project.getProjectPartners().stream().filter(c -> c.isActive()).collect(Collectors.toList()));
        if (!project.getPartners().isEmpty()) {
          if (this.isReportingActive()) {

            List<ProjectPartnerOverall> overalls = project.getPartners().get(0).getProjectPartnerOveralls().stream()
              .filter(c -> c.isActive() && c.getYear() == this.getReportingYear()).collect(Collectors.toList());
            if (!overalls.isEmpty()) {
              project.setOverall(overalls.get(0).getOverall());
              partnerOverall = overalls.get(0);
            }
          }

        }
        for (ProjectPartner projectPartner : project.getPartners()) {
          projectPartner.setPartnerPersons(
            projectPartner.getProjectPartnerPersons().stream().filter(c -> c.isActive()).collect(Collectors.toList()));
        }
        this.projectPPAPartners = new ArrayList<ProjectPartner>();
        for (ProjectPartner pp : project.getPartners()) {

          if (this.isPPA(pp.getInstitution())) {
            this.projectPPAPartners.add(pp);

          }

          List<ProjectPartnerContribution> contributors = new ArrayList<>();


          List<ProjectPartnerContribution> partnerContributions =
            pp.getProjectPartnerContributions().stream().filter(c -> c.isActive()).collect(Collectors.toList());
          for (ProjectPartnerContribution projectPartnerContribution : partnerContributions) {
            contributors.add(projectPartnerContribution);
          }
          pp.setPartnerContributors(contributors);
        }

        if (this.isLessonsActive()) {
          this.loadLessons(loggedCrp, project);
        }


      }
    }

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


    ProjectPartner leader = project.getLeader();
    if (leader != null) {
      // First we remove the element from the array.
      project.getPartners().remove(leader);
      // then we add it to the first position.
      project.getPartners().add(0, leader);
    }

    Collections.sort(project.getPartners(),
      (p1, p2) -> Boolean.compare(this.isPPA(p2.getInstitution()), this.isPPA(p1.getInstitution())));

    partnerPersonTypes = new HashMap<>();
    partnerPersonTypes.put(APConstants.PROJECT_PARTNER_CP, this.getText("projectPartners.types.CP"));

    if (this.hasPermission("leader")) {
      partnerPersonTypes.put(APConstants.PROJECT_PARTNER_PL, this.getText("projectPartners.types.PL"));
    }
    if (this.hasPermission("coordinator")) {
      partnerPersonTypes.put(APConstants.PROJECT_PARTNER_PC, this.getText("projectPartners.types.PC"));
    }

    if (this.isHttpPost()) {


      project.getPartners().clear();

    }

  }

  @Override
  public String save() {
    if (this.hasPermission("canEdit")) {

      previousProject = projectManager.getProjectById(projectID);

      for (ProjectPartner previousPartner : previousProject.getProjectPartners().stream().filter(c -> c.isActive())
        .collect(Collectors.toList())) {
        if (project.getPartners() == null || !project.getPartners().contains(previousPartner)) {
          projectPartnerManager.deleteProjectPartner(previousPartner.getId());
          // budgetManager.deleteBudgetsByInstitution(project.getId(), previousPartner.getInstitution(),
          // this.getCurrentUser(), this.getJustification());
        }
      }
      if (project.getPartners() != null) {


        for (ProjectPartner projectPartner : project.getPartners()) {
          if (projectPartner.getId() == null) {
            projectPartner.setActive(true);

            projectPartner.setCreatedBy(this.getCurrentUser());
            projectPartner.setModifiedBy(this.getCurrentUser());
            projectPartner.setModificationJustification("");
            projectPartner.setActiveSince(new Date());
            projectPartner.setProject(project);

            projectPartnerManager.saveProjectPartner(projectPartner);
          }


          ProjectPartner db = projectPartnerManager.getProjectPartnerById(projectPartner.getId());
          for (ProjectPartnerPerson partnerPerson : db.getProjectPartnerPersons()) {
            if (projectPartner.getPartnerPersons() == null
              || !projectPartner.getPartnerPersons().contains(partnerPerson)) {
              projectPartnerPersonManager.deleteProjectPartnerPerson(partnerPerson.getId());
            }
          }
          if (projectPartner.getPartnerPersons() != null) {
            for (ProjectPartnerPerson partnerPerson : projectPartner.getPartnerPersons()) {
              if (partnerPerson.getId() == null) {
                partnerPerson.setActive(true);

                partnerPerson.setCreatedBy(this.getCurrentUser());
                partnerPerson.setModifiedBy(this.getCurrentUser());
                partnerPerson.setModificationJustification("");
                partnerPerson.setActiveSince(new Date());

              } else {

                ProjectPartnerPerson dbPerson;

                dbPerson = projectPartnerPersonManager.getProjectPartnerPersonById(partnerPerson.getId());
                partnerPerson.setActive(true);
                partnerPerson.setCreatedBy(dbPerson.getCreatedBy());
                partnerPerson.setModifiedBy(this.getCurrentUser());
                partnerPerson.setModificationJustification("");
                partnerPerson.setActiveSince(dbPerson.getActiveSince());


              }
              partnerPerson.setProjectPartner(projectPartner);


              projectPartnerPersonManager.saveProjectPartnerPerson(partnerPerson);


            }
          }


          db = projectPartnerManager.getProjectPartnerById(projectPartner.getId());
          List<ProjectPartnerContribution> contributors = new ArrayList<>();


          List<ProjectPartnerContribution> partnerContributions =
            db.getProjectPartnerContributions().stream().filter(c -> c.isActive()).collect(Collectors.toList());
          for (ProjectPartnerContribution projectPartnerContribution : partnerContributions) {
            contributors.add(projectPartnerContribution);
          }
          db.setPartnerContributors(contributors);
          for (ProjectPartnerContribution partnerContribution : db.getPartnerContributors()) {
            if (projectPartner.getPartnerContributors() == null
              || !projectPartner.getPartnerContributors().contains(partnerContribution)) {
              projectPartnerContributionManager.deleteProjectPartnerContribution(partnerContribution.getId());
            }
          }
          if (projectPartner.getPartnerContributors() != null) {
            for (ProjectPartnerContribution partnerContribution : projectPartner.getPartnerContributors()) {


              if (partnerContribution.getId() == null) {
                partnerContribution.setActive(true);

                partnerContribution.setCreatedBy(this.getCurrentUser());
                partnerContribution.setModifiedBy(this.getCurrentUser());
                partnerContribution.setModificationJustification("");
                partnerContribution.setActiveSince(new Date());
                partnerContribution.setProjectPartner(projectPartner);
                if (partnerContribution.getProjectPartnerContributor().getId() == null) {
                  List<ProjectPartner> partenerContributor = project.getPartners().stream()
                    .filter(c -> c.getInstitution().getId()
                      .equals(partnerContribution.getProjectPartnerContributor().getInstitution().getId()))
                    .collect(Collectors.toList());
                  if (!partenerContributor.isEmpty()) {
                    partnerContribution.getProjectPartnerContributor().setId(partenerContributor.get(0).getId());
                  }

                }
                projectPartnerContributionManager.saveProjectPartnerContribution(partnerContribution);


              }

            }
          }


        }
      }
      if (this.isReportingActive()) {
        ProjectPartnerOverall overall;
        if (partnerOverall != null) {
          overall = partnerOverall;

        } else {
          overall = new ProjectPartnerOverall();
          overall.setProjectPartner(project.getPartners().get(0));
          overall.setYear(this.getReportingYear());
        }
        overall.setOverall(project.getOverall());
        projectPartnerOverallManager.saveProjectPartnerOverall(overall);
      }


      if (this.isLessonsActive()) {
        this.saveLessons(loggedCrp, project);
      }

      ProjectPartnerPerson leader = project.getLeaderPerson();
      // Notify user if the project leader was created.
      if (leader != null) {
        this.notifyNewUserCreated(leader.getUser());
      }
      this.updateRoles(previousProject.getLeaderPerson(), leader, plRole);
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

      if (coordinator != null) {

        this.notifyNewUserCreated(coordinator.getUser());


      }


      this.updateRoles(previousCoordinator, coordinator, pcRole);
      project = projectManager.getProjectById(projectID);
      project.setActiveSince(new Date());
      project.setModifiedBy(this.getCurrentUser());

      List<String> relationsName = new ArrayList<>();
      relationsName.add(APConstants.PROJECT_PARTNERS_RELATION);
      relationsName.add(APConstants.PROJECT_LESSONS_RELATION);

      projectManager.saveProject(project, this.getActionName(), relationsName);

      Path path = this.getAutoSaveFilePath();

      if (path.toFile().exists()) {
        path.toFile().delete();
      }

      Collection<String> messages = this.getActionMessages();

      if (!messages.isEmpty()) {
        String validationMessage = messages.iterator().next();
        this.setActionMessages(null);
        this.addActionWarning(this.getText("saving.saved") + validationMessage);
      } else {
        this.addActionMessage(this.getText("saving.saved"));
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

  public void setTransaction(String transaction) {
    this.transaction = transaction;
  }


  /**
   * This method updates the role for each user (Leader/Coordinator) into the database, and notifies by email what has
   * been done.
   * 
   * @param previousPartnerPerson is the previous leader/coordinator that has assigned the project before.
   * @param partnerPerson the current leader/coordinator associated to the project.
   * @param role is the new role assignated (leader/coordinator).
   */
  private void updateRoles(ProjectPartnerPerson previousPartnerPerson, ProjectPartnerPerson partnerPerson, Role role) {
    long roleId = role.getId();

    String roleAcronym = role.getAcronym();
    if (previousPartnerPerson == null && partnerPerson != null) {

      UserRole userRole = new UserRole();
      userRole.setRole(role);
      userRole.setUser(partnerPerson.getUser());

      role = roleManager.getRoleById(role.getId());
      if (!role.getUserRoles().contains(userRole)) {
        userRoleManager.saveUserRole(userRole);
      }


      // Notifying user is assigned as Project Leader/Coordinator.
      this.notifyRoleAssigned(partnerPerson.getUser(), role);
    } else if (previousPartnerPerson != null && partnerPerson == null) {

      List<UserRole> rolesUser = userRoleManager.getUserRolesByUserId(previousPartnerPerson.getUser().getId());
      if (rolesUser != null) {
        rolesUser =
          rolesUser.stream().filter(c -> c.getRole().getId().longValue() == roleId).collect(Collectors.toList());
        if (!rolesUser.isEmpty()) {
          if (previousPartnerPerson.getUser().getProjectPartnerPersons().stream()
            .filter(c -> c.isActive() && c.getContactType().equals(roleAcronym) && c.getProjectPartner().getProject()
              .getId().longValue() != previousPartnerPerson.getProjectPartner().getProject().getId().longValue())
            .collect(Collectors.toList()).size() == 0) {
            userRoleManager.deleteUserRole(rolesUser.get(0).getId());
          }

        }
      }

      // Notifying user that is not the project leader anymore
      this.notifyRoleUnassigned(previousPartnerPerson.getUser(), role);
    } else if (previousPartnerPerson != null && partnerPerson != null) {
      if (!partnerPerson.getUser().getId().equals(previousPartnerPerson.getUser().getId())) {
        UserRole userRole = new UserRole();
        userRole.setRole(role);
        userRole.setUser(partnerPerson.getUser());

        role = roleManager.getRoleById(role.getId());
        if (!role.getUserRoles().contains(userRole)) {
          userRoleManager.saveUserRole(userRole);
        }
        // Notifying user is assigned as Project Leader/Coordinator.
        this.notifyRoleAssigned(partnerPerson.getUser(), role);
        // Deleting role.
        List<UserRole> rolesUser = userRoleManager.getUserRolesByUserId(previousPartnerPerson.getUser().getId());
        if (rolesUser != null) {
          rolesUser =
            rolesUser.stream().filter(c -> c.getRole().getId().longValue() == roleId).collect(Collectors.toList());
          if (!rolesUser.isEmpty()) {
            if (previousPartnerPerson.getUser().getProjectPartnerPersons().stream()
              .filter(c -> c.isActive() && c.getContactType().equals(roleAcronym) && c.getProjectPartner().getProject()
                .getId().longValue() != previousPartnerPerson.getProjectPartner().getProject().getId().longValue())
              .collect(Collectors.toList()).size() == 0) {

              userRoleManager.deleteUserRole(rolesUser.get(0).getId());
            }
          }
        }
        // Notifying user that is not the project leader anymore
        this.notifyRoleUnassigned(previousPartnerPerson.getUser(), role);
      }
    }
    this.clearPermissionsCache();
  }

  @Override
  public void validate() {
    if (save) {
      projectPartnersValidator.validate(this, project);
      if (projectPartnersValidator.isHasErros()) {
        if (project.getPartners() != null) {
          for (ProjectPartner projectPartner : project.getPartners()) {
            if (projectPartner.getInstitution().getId() != null) {
              projectPartner
                .setInstitution(institutionManager.getInstitutionById(projectPartner.getInstitution().getId()));

            }

            if (projectPartner.getPartnerPersons() != null) {
              for (ProjectPartnerPerson projectPartnerPerson : projectPartner.getPartnerPersons()) {
                if (projectPartnerPerson.getUser() != null) {
                  projectPartnerPerson.setUser(userManager.getUser(projectPartnerPerson.getUser().getId()));
                }
              }
            }

            if (projectPartner.getPartnerContributors() != null) {
              for (ProjectPartnerContribution projectPartnerContribution : projectPartner.getPartnerContributors()) {

                projectPartnerContribution.getProjectPartnerContributor()
                  .setInstitution(institutionManager.getInstitutionById(
                    projectPartnerContribution.getProjectPartnerContributor().getInstitution().getId()));
              }
            }
          }
        }

      }
    }
  }
}
