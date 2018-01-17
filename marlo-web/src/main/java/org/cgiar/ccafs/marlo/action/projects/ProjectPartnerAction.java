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
import org.cgiar.ccafs.marlo.data.manager.CrpUserManager;
import org.cgiar.ccafs.marlo.data.manager.DeliverablePartnershipManager;
import org.cgiar.ccafs.marlo.data.manager.InstitutionLocationManager;
import org.cgiar.ccafs.marlo.data.manager.InstitutionManager;
import org.cgiar.ccafs.marlo.data.manager.InstitutionTypeManager;
import org.cgiar.ccafs.marlo.data.manager.LocElementManager;
import org.cgiar.ccafs.marlo.data.manager.ProjectComponentLessonManager;
import org.cgiar.ccafs.marlo.data.manager.ProjectManager;
import org.cgiar.ccafs.marlo.data.manager.ProjectPartnerContributionManager;
import org.cgiar.ccafs.marlo.data.manager.ProjectPartnerLocationManager;
import org.cgiar.ccafs.marlo.data.manager.ProjectPartnerManager;
import org.cgiar.ccafs.marlo.data.manager.ProjectPartnerOverallManager;
import org.cgiar.ccafs.marlo.data.manager.ProjectPartnerPersonManager;
import org.cgiar.ccafs.marlo.data.manager.RoleManager;
import org.cgiar.ccafs.marlo.data.manager.UserManager;
import org.cgiar.ccafs.marlo.data.manager.UserRoleManager;
import org.cgiar.ccafs.marlo.data.model.Activity;
import org.cgiar.ccafs.marlo.data.model.Crp;
import org.cgiar.ccafs.marlo.data.model.CrpClusterActivityLeader;
import org.cgiar.ccafs.marlo.data.model.CrpClusterOfActivity;
import org.cgiar.ccafs.marlo.data.model.CrpPpaPartner;
import org.cgiar.ccafs.marlo.data.model.CrpProgram;
import org.cgiar.ccafs.marlo.data.model.CrpProgramLeader;
import org.cgiar.ccafs.marlo.data.model.CrpUser;
import org.cgiar.ccafs.marlo.data.model.Deliverable;
import org.cgiar.ccafs.marlo.data.model.DeliverablePartnership;
import org.cgiar.ccafs.marlo.data.model.Institution;
import org.cgiar.ccafs.marlo.data.model.InstitutionLocation;
import org.cgiar.ccafs.marlo.data.model.InstitutionType;
import org.cgiar.ccafs.marlo.data.model.LocElement;
import org.cgiar.ccafs.marlo.data.model.Project;
import org.cgiar.ccafs.marlo.data.model.ProjectComponentLesson;
import org.cgiar.ccafs.marlo.data.model.ProjectPartner;
import org.cgiar.ccafs.marlo.data.model.ProjectPartnerContribution;
import org.cgiar.ccafs.marlo.data.model.ProjectPartnerLocation;
import org.cgiar.ccafs.marlo.data.model.ProjectPartnerOverall;
import org.cgiar.ccafs.marlo.data.model.ProjectPartnerPerson;
import org.cgiar.ccafs.marlo.data.model.ProjectStatusEnum;
import org.cgiar.ccafs.marlo.data.model.Role;
import org.cgiar.ccafs.marlo.data.model.User;
import org.cgiar.ccafs.marlo.data.model.UserRole;
import org.cgiar.ccafs.marlo.security.APCustomRealm;
import org.cgiar.ccafs.marlo.security.Permission;
import org.cgiar.ccafs.marlo.utils.APConfig;
import org.cgiar.ccafs.marlo.utils.AutoSaveReader;
import org.cgiar.ccafs.marlo.utils.HistoryComparator;
import org.cgiar.ccafs.marlo.utils.HistoryDifference;
import org.cgiar.ccafs.marlo.utils.SendMailS;
import org.cgiar.ccafs.marlo.validation.projects.ProjectPartnersValidator;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

import javax.inject.Inject;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ProjectPartnerAction extends BaseAction {


  /**
   * 
   */
  private static final long serialVersionUID = 7833194831832715444L;
  private static Logger LOG = LoggerFactory.getLogger(ProjectPartnerAction.class);

  /**
   * Helper method to read a stream into memory.
   * 
   * @param stream
   * @return
   * @throws IOException
   */
  public static byte[] readFully(InputStream stream) throws IOException {
    byte[] buffer = new byte[8192];
    ByteArrayOutputStream baos = new ByteArrayOutputStream();

    int bytesRead;
    while ((bytesRead = stream.read(buffer)) != -1) {
      baos.write(buffer, 0, bytesRead);
    }
    return baos.toByteArray();
  }

  private final ProjectPartnerManager projectPartnerManager;

  private final ProjectPartnerPersonManager projectPartnerPersonManager;

  private DeliverablePartnershipManager deliverablePartnershipManager;
  private final ProjectPartnerContributionManager projectPartnerContributionManager;
  private final ProjectPartnerOverallManager projectPartnerOverallManager;
  private final InstitutionManager institutionManager;
  private final InstitutionTypeManager institutionTypeManager;
  private final LocElementManager locationManager;
  private final UserManager userManager;
  private final UserRoleManager userRoleManager;
  private final RoleManager roleManager;
  private final ProjectManager projectManager;
  private final CrpPpaPartnerManager crpPpaPartnerManager;
  private final ProjectPartnerLocationManager projectPartnerLocationManager;
  private final InstitutionLocationManager institutionLocationManager;

  private final CrpManager crpManager;

  private final CrpUserManager crpUserManager;
  private final ProjectPartnersValidator projectPartnersValidator;
  private long projectID;
  private Crp loggedCrp;

  private Project project;
  private ProjectComponentLesson projectComponentLesson;
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
  private final AuditLogManager auditLogManager;
  private String transaction;
  private final HistoryComparator historyComparator;

  // Util
  private final SendMailS sendMail;

  @Inject
  public ProjectPartnerAction(APConfig config, ProjectPartnerManager projectPartnerManager,
    InstitutionManager institutionManager, LocElementManager locationManager, ProjectManager projectManager,
    CrpPpaPartnerManager crpPpaPartnerManager, CrpManager crpManager,
    ProjectPartnerOverallManager projectPartnerOverallManager, UserManager userManager,
    InstitutionTypeManager institutionTypeManager, SendMailS sendMail, RoleManager roleManager,
    ProjectPartnerContributionManager projectPartnerContributionManager, UserRoleManager userRoleManager,
    ProjectPartnerPersonManager projectPartnerPersonManager, AuditLogManager auditLogManager,
    ProjectPartnersValidator projectPartnersValidator, HistoryComparator historyComparator,
    ProjectComponentLessonManager projectComponentLessonManager, CrpUserManager crpUserManager,
    ProjectPartnerLocationManager projectPartnerLocationManager,
    DeliverablePartnershipManager deliverablePartnershipManager,
    InstitutionLocationManager institutionLocationManager) {
    super(config);
    this.projectPartnersValidator = projectPartnersValidator;
    this.auditLogManager = auditLogManager;
    this.projectPartnerManager = projectPartnerManager;
    this.institutionManager = institutionManager;
    this.deliverablePartnershipManager = deliverablePartnershipManager;
    this.institutionTypeManager = institutionTypeManager;
    this.projectPartnerLocationManager = projectPartnerLocationManager;
    this.locationManager = locationManager;
    this.historyComparator = historyComparator;
    this.projectManager = projectManager;
    this.userManager = userManager;
    this.crpManager = crpManager;
    this.institutionLocationManager = institutionLocationManager;
    this.crpPpaPartnerManager = crpPpaPartnerManager;
    this.projectPartnerOverallManager = projectPartnerOverallManager;
    this.sendMail = sendMail;
    this.roleManager = roleManager;
    this.projectPartnerContributionManager = projectPartnerContributionManager;
    this.userRoleManager = userRoleManager;
    this.projectPartnerPersonManager = projectPartnerPersonManager;
    this.crpUserManager = crpUserManager;

  }

  public void addCrpUser(User user) {
    user = userManager.getUser(user.getId());
    CrpUser crpUser = new CrpUser();
    crpUser.setUser(user);
    crpUser.setCrp(loggedCrp);

    List<CrpUser> userCrp = user.getCrpUsers().stream().filter(cu -> cu.isActive() && cu.getCrp().equals(loggedCrp))
      .collect(Collectors.toList());

    if (userCrp == null || userCrp.isEmpty()) {
      crpUser.setActive(true);
      crpUser.setActiveSince(new Date());
      crpUser.setCreatedBy(this.getCurrentUser());
      crpUser.setModifiedBy(this.getCurrentUser());
      crpUser.setModificationJustification("");
      crpUserManager.saveCrpUser(crpUser);
    }
  }

  @Override
  public String cancel() {

    Path path = this.getAutoSaveFilePath();

    if (path.toFile().exists()) {

      boolean fileDeleted = path.toFile().delete();
    }

    this.setDraft(false);
    Collection<String> messages = this.getActionMessages();
    if (!messages.isEmpty()) {
      String validationMessage = messages.iterator().next();
      this.setActionMessages(null);
      this.addActionMessage("draft:" + this.getText("cancel.autoSave"));
    } else {
      this.addActionMessage("draft:" + this.getText("cancel.autoSave"));
    }
    messages = this.getActionMessages();

    return SUCCESS;
  }

  public void checkCrpUserByRole(User user) {
    user = userManager.getUser(user.getId());
    List<UserRole> crpUserRoles =
      user.getUserRoles().stream().filter(ur -> ur.getRole().getCrp().equals(loggedCrp)).collect(Collectors.toList());
    if (crpUserRoles == null || crpUserRoles.isEmpty()) {
      List<CrpUser> crpUsers = user.getCrpUsers().stream().filter(cu -> cu.isActive() && cu.getCrp().equals(loggedCrp))
        .collect(Collectors.toList());
      for (CrpUser crpUser : crpUsers) {
        crpUserManager.deleteCrpUser(crpUser.getId());
      }
    }
  }


  /**
   * This method clears the cache and re-load the user permissions in the next iteration.
   */
  @Override
  public void clearPermissionsCache() {
    ((APCustomRealm) securityContext.getRealm())
      .clearCachedAuthorizationInfo(securityContext.getSubject().getPrincipals());
  }

  public List<Activity> getActivitiesLedByUser(long userID) {
    Project project = projectManager.getProjectById(projectID);
    List<Activity> activities = project.getActivities().stream()
      .filter(c -> c.isActive() && c.getProjectPartnerPerson() != null
        && c.getProjectPartnerPerson().getId().longValue() == userID && c.getPhase().equals(this.getActualPhase()))
      .collect(Collectors.toList());
    return activities;

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
    // get the class simple name
    String composedClassName = project.getClass().getSimpleName();
    // get the action name and replace / for _
    String actionFile = this.getActionName().replace("/", "_");
    // concatane name and add the .json extension
    String autoSaveFile = project.getId() + "_" + composedClassName + "_" + this.getActualPhase().getDescription() + "_"
      + this.getActualPhase().getYear() + "_" + actionFile + ".json";

    return Paths.get(config.getAutoSaveFolder() + autoSaveFile);
  }


  public List<LocElement> getCountries() {
    return countries;
  }


  public List<Deliverable> getDeliverablesLedByPartner(long projectPartnerID) {
    List<Deliverable> deliverablesLeads = new ArrayList<>();
    if (projectPartnerID != 0) {
      ProjectPartner projectPartner = projectPartnerManager.getProjectPartnerById(projectPartnerID);
      if (projectPartner != null) {
        List<DeliverablePartnership> deliverablePartnerships = projectPartner.getDeliverablePartnerships().stream()
          .filter(c -> c.isActive() && c.getPhase() != null && c.getPhase().equals(this.getActualPhase()))
          .collect(Collectors.toList());
        for (DeliverablePartnership deliverablePartnership : deliverablePartnerships) {
          Deliverable deliverable = deliverablePartnership.getDeliverable();
          deliverable.setDeliverableInfo(deliverable.getDeliverableInfo(this.getActualPhase()));
          if (!deliverablesLeads.contains(deliverable)) {
            if (deliverable.getDeliverableInfo().getYear() >= this.getActualPhase().getYear()) {

              deliverablesLeads.add(deliverable);
            } else {
              if (deliverable.getDeliverableInfo().getStatus().intValue() == Integer
                .parseInt(ProjectStatusEnum.Extended.getStatusId())) {
                if (deliverable.getDeliverableInfo().getNewExpectedYear() != null
                  && deliverable.getDeliverableInfo().getNewExpectedYear() >= this.getActualPhase().getYear()) {

                  deliverablesLeads.add(deliverable);
                }
              }
            }
          }

        }
      }

    }
    return deliverablesLeads;
  }

  public List<Deliverable> getDeliverablesLedByUser(long userID) {

    /*
     * Project project = projectManager.getProjectById(projectID);
     * List<Deliverable> deliverablesLeads = new ArrayList<>();
     * List<Deliverable> deliverables =
     * project.getDeliverables().stream().filter(c -> c.isActive()).collect(Collectors.toList());
     * for (Deliverable deliverable : deliverables) {
     * if (!deliverable.getDeliverablePartnerships().stream()
     * .filter(c -> c.isActive() && c.getPhase() != null && c.getPhase().equals(this.getActualPhase())
     * && c.getProjectPartnerPerson() != null && c.getProjectPartnerPerson().getId().longValue() == userID)
     * .collect(Collectors.toList()).isEmpty()) {
     * deliverable.setDeliverableInfo(deliverable.getDeliverableInfo(this.getActualPhase()));
     * deliverablesLeads.add(deliverable);
     * }
     * }
     * return deliverablesLeads;
     */
    List<Deliverable> deliverablesLeads = new ArrayList<>();
    ProjectPartnerPerson partnerPerson = projectPartnerPersonManager.getProjectPartnerPersonById(userID);
    if (partnerPerson != null) {
      List<DeliverablePartnership> deliverablePartnerships = partnerPerson.getDeliverablePartnerships().stream()
        .filter(c -> c.isActive() && c.getPhase() != null && c.getPhase().equals(this.getActualPhase()))
        .collect(Collectors.toList());
      for (DeliverablePartnership deliverablePartnership : deliverablePartnerships) {
        Deliverable deliverable = deliverablePartnership.getDeliverable();
        deliverable.setDeliverableInfo(deliverable.getDeliverableInfo(this.getActualPhase()));
        if (deliverable.getDeliverableInfo().getStatus() == Integer.parseInt(ProjectStatusEnum.Extended.getStatusId())
          || deliverable.getDeliverableInfo().getStatus() == Integer
            .parseInt(ProjectStatusEnum.Ongoing.getStatusId())) {
          if (!deliverablesLeads.contains(deliverable)) {
            if (deliverable.getDeliverableInfo().getYear() >= this.getActualPhase().getYear()) {

              deliverablesLeads.add(deliverable);
            } else {
              if (deliverable.getDeliverableInfo().getStatus().intValue() == Integer
                .parseInt(ProjectStatusEnum.Extended.getStatusId())) {
                if (deliverable.getDeliverableInfo().getNewExpectedYear() != null
                  && deliverable.getDeliverableInfo().getNewExpectedYear() >= this.getActualPhase().getYear()) {

                  deliverablesLeads.add(deliverable);
                }
              }
            }
          }
        }


      }
    }
    return deliverablesLeads;

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


  /**
   * This method will validate if the user is deactivated. If so, it will send an email indicating the credentials to
   * access.
   * 
   * @param leader is a PartnerPerson object that could be the leader or the coordinator.
   */
  private void notifyNewUserCreated(User user) {

    if (user != null && user.getId() != null) {
      user = userManager.getUser(user.getId());

      if (!user.isActive()) {
        String toEmail = user.getEmail();
        String ccEmail = "";
        String bbcEmails = this.config.getEmailNotification();
        String subject = this.getText("email.newUser.subject", new String[] {user.getFirstName()});
        // Setting the password
        String password = this.getText("email.outlookPassword");
        if (!user.isCgiarUser()) {
          // Generating a random password.
          password = RandomStringUtils.randomNumeric(6);
          // Applying the password to the user.
          user.setPassword(password);
        }

        // Building the Email message:
        StringBuilder message = new StringBuilder();
        message.append(this.getText("email.dear", new String[] {user.getFirstName()}));

        // get CRPAdmin contacts
        String crpAdmins = "";
        long adminRol = Long.parseLong((String) this.getSession().get(APConstants.CRP_ADMIN_ROLE));
        Role roleAdmin = roleManager.getRoleById(adminRol);
        List<UserRole> userRoles = roleAdmin.getUserRoles().stream()
          .filter(ur -> ur.getUser() != null && ur.getUser().isActive()).collect(Collectors.toList());
        for (UserRole userRole : userRoles) {
          if (crpAdmins.isEmpty()) {
            crpAdmins += userRole.getUser().getFirstName() + " (" + userRole.getUser().getEmail() + ")";
          } else {
            crpAdmins += ", " + userRole.getUser().getFirstName() + " (" + userRole.getUser().getEmail() + ")";
          }
        }

        message.append(this.getText("email.newUser.part1", new String[] {this.getText("email.newUser.listRoles"),
          config.getBaseUrl(), user.getEmail(), password, this.getText("email.support", new String[] {crpAdmins})}));
        message.append(this.getText("email.bye"));

        // Saving the new user configuration.
        user.setActive(true);
        userManager.saveUser(user, this.getCurrentUser());

        // Send UserManual.pdf
        String contentType = "application/pdf";
        String fileName = "Introduction_To_MARLO_v2.1.pdf";
        byte[] buffer = null;
        InputStream inputStream = null;

        try {
          inputStream = this.getClass().getResourceAsStream("/manual/Introduction_To_MARLO_v2.1.pdf");
          buffer = readFully(inputStream);
        } catch (FileNotFoundException e) {
          // TODO Auto-generated catch block
          e.printStackTrace();
        } catch (IOException e) {
          // TODO Auto-generated catch block
          e.printStackTrace();
        } finally {
          if (inputStream != null) {
            try {
              inputStream.close();
            } catch (IOException e) {
              // TODO Auto-generated catch block
              e.printStackTrace();
            }
          }
        }

        if (buffer != null && fileName != null && contentType != null) {
          sendMail.send(toEmail, ccEmail, bbcEmails, subject, message.toString(), buffer, contentType, fileName, true);
        } else {
          sendMail.send(toEmail, ccEmail, bbcEmails, subject, message.toString(), null, null, null, true);
        }
      }
    }

  }

  /**
   * This method notify the user that is been assigned as Project Leader/Coordinator for a specific project.
   * 
   * @param userAssigned is the user that is being assigned.
   * @param role is the role (Project Leader or Project Coordinator).
   */
  private void notifyRoleAssigned(User userAssigned, Role role) {
    userAssigned = userManager.getUser(userAssigned.getId());
    Project project = projectManager.getProjectById(this.projectID);

    // TO will be the new user
    String toEmail = userAssigned.getEmail();
    // CC will be the user who is making the modification.
    String ccEmail = this.getCurrentUser().getEmail();
    // CC will be also the CRP Admins
    String crpAdmins = "";
    String crpAdminsEmail = "";
    long adminRol = Long.parseLong((String) this.getSession().get(APConstants.CRP_ADMIN_ROLE));
    Role roleAdmin = roleManager.getRoleById(adminRol);
    List<UserRole> userRoles = roleAdmin.getUserRoles().stream()
      .filter(ur -> ur.getUser() != null && ur.getUser().isActive()).collect(Collectors.toList());
    for (UserRole userRole : userRoles) {
      if (crpAdmins.isEmpty()) {
        crpAdmins += userRole.getUser().getFirstName() + " (" + userRole.getUser().getEmail() + ")";
        crpAdminsEmail += userRole.getUser().getEmail();

      } else {
        crpAdmins += ", " + userRole.getUser().getFirstName() + " (" + userRole.getUser().getEmail() + ")";
        crpAdminsEmail += ", " + userRole.getUser().getEmail();
      }
    }
    if (!crpAdminsEmail.isEmpty()) {
      if (ccEmail.isEmpty()) {
        ccEmail += crpAdminsEmail;
      } else {
        ccEmail += ", " + crpAdminsEmail;
      }
    }

    // Copy to FL, CL and FM depending on CRP_EMAIL_CC_FL_FM_CL specificity
    if (this.hasSpecificities(APConstants.CRP_EMAIL_CC_FL_FM_CL)) {
      // CC for leaders and coordinators
      // CC will be also the Management Liaison associated with the flagship(s), if is PMU only the PMU contact
      Long crpPmuRole = Long.parseLong((String) this.getSession().get(APConstants.CRP_PMU_ROLE));
      Role roleCrpPmu = roleManager.getRoleById(crpPmuRole);
      // If Managment liason is PMU
      if (project.getProjecInfoPhase(this.getActualPhase()).getLiaisonInstitution() != null
        && project.getProjecInfoPhase(this.getActualPhase()).getLiaisonUser() != null) {
        if (project.getProjecInfoPhase(this.getActualPhase()).getLiaisonInstitution().getAcronym()
          .equals(roleCrpPmu.getAcronym())) {
          if (ccEmail.isEmpty()) {
            ccEmail += project.getProjecInfoPhase(this.getActualPhase()).getLiaisonUser().getUser().getEmail();
          } else {
            ccEmail += ", " + project.getProjecInfoPhase(this.getActualPhase()).getLiaisonUser().getUser().getEmail();
          }
        } else if (project.getProjecInfoPhase(this.getActualPhase()).getLiaisonInstitution() != null
          && project.getProjecInfoPhase(this.getActualPhase()).getLiaisonInstitution().getCrpProgram() != null
          && project.getProjecInfoPhase(this.getActualPhase()).getLiaisonInstitution().getCrpProgram()
            .getProgramType() == 1) {
          // If Managment liason is FL
          List<CrpProgram> crpPrograms = project
            .getCrp().getCrpPrograms().stream().filter(cp -> cp.getId() == project
              .getProjecInfoPhase(this.getActualPhase()).getLiaisonInstitution().getCrpProgram().getId())
            .collect(Collectors.toList());
          if (crpPrograms != null) {
            if (crpPrograms.size() > 1) {
              LOG.warn("Crp programs should be 1");
            }
            CrpProgram crpProgram = crpPrograms.get(0);
            for (CrpProgramLeader crpProgramLeader : crpProgram.getCrpProgramLeaders().stream()
              .filter(cpl -> cpl.getUser().isActive() && cpl.isActive()).collect(Collectors.toList())) {
              if (ccEmail.isEmpty()) {
                ccEmail += crpProgramLeader.getUser().getEmail();
              } else {
                ccEmail += ", " + crpProgramLeader.getUser().getEmail();
              }
            }
            // CC will be also other Cluster Leaders
            for (CrpClusterOfActivity crpClusterOfActivity : crpProgram.getCrpClusterOfActivities().stream()
              .filter(cl -> cl.isActive() && cl.getPhase().equals(this.getActualPhase()))
              .collect(Collectors.toList())) {
              for (CrpClusterActivityLeader crpClusterActivityLeader : crpClusterOfActivity
                .getCrpClusterActivityLeaders().stream().filter(cl -> cl.isActive()).collect(Collectors.toList())) {
                if (ccEmail.isEmpty()) {
                  ccEmail += crpClusterActivityLeader.getUser().getEmail();
                } else {
                  ccEmail += ", " + crpClusterActivityLeader.getUser().getEmail();
                }
              }
            }
          }
        }
      }
    }

    // BBC will be our gmail notification email.
    String bbcEmails = this.config.getEmailNotification();

    // Subject
    String projectRole = null;
    if (role.getId() == plRole.getId()) {
      projectRole = this.getText("email.project.assigned.PL");
    } else {
      projectRole = this.getText("email.project.assigned.PC");
    }
    String crp = loggedCrp.getAcronym() != null && !loggedCrp.getAcronym().isEmpty() ? loggedCrp.getAcronym()
      : loggedCrp.getName();

    String subject = this.getText("email.project.assigned.subject",
      new String[] {projectRole, crp, project.getStandardIdentifier(Project.EMAIL_SUBJECT_IDENTIFIER)});


    // message
    StringBuilder message = new StringBuilder();
    // Building the Email message:
    message.append(this.getText("email.dear", new String[] {userAssigned.getFirstName()}));
    message.append(this.getText("email.project.assigned",
      new String[] {projectRole, crp, project.getProjecInfoPhase(this.getActualPhase()).getTitle(),
        project.getStandardIdentifier(Project.EMAIL_SUBJECT_IDENTIFIER)}));
    if (role.getId() == plRole.getId()) {
      message.append(this.getText("email.project.leader.responsabilities"));
    } else {
      message.append(this.getText("email.project.coordinator.responsabilities"));
    }
    message.append(this.getText("email.support", new String[] {crpAdmins}));
    message.append(this.getText("email.getStarted"));
    message.append(this.getText("email.bye"));

    sendMail.send(toEmail, ccEmail, bbcEmails, subject, message.toString(), null, null, null, true);
  }

  /**
   * This method notify the the user that he/she stopped contributing to a specific project.
   * 
   * @param userUnassigned is the user that stopped contribution.
   * @param role is the user role that stopped contributing (Project Leader or Project Coordinator).
   */
  private void notifyRoleUnassigned(User userUnassigned, Role role) {
    // Send email to the new user and the P&R notification email.
    // TO
    String toEmail = userUnassigned.getEmail();
    // CC will be the user who is making the modification.
    String ccEmail = this.getCurrentUser().getEmail();
    // CC will be also the CRP Admins
    String crpAdmins = "";
    String crpAdminsEmail = "";
    long adminRol = Long.parseLong((String) this.getSession().get(APConstants.CRP_ADMIN_ROLE));
    Role roleAdmin = roleManager.getRoleById(adminRol);
    List<UserRole> userRoles = roleAdmin.getUserRoles().stream()
      .filter(ur -> ur.getUser() != null && ur.getUser().isActive()).collect(Collectors.toList());
    for (UserRole userRole : userRoles) {
      if (crpAdmins.isEmpty()) {
        crpAdmins += userRole.getUser().getFirstName() + " (" + userRole.getUser().getEmail() + ")";
        crpAdminsEmail += userRole.getUser().getEmail();

      } else {
        crpAdmins += ", " + userRole.getUser().getFirstName() + " (" + userRole.getUser().getEmail() + ")";
        crpAdminsEmail += ", " + userRole.getUser().getEmail();
      }
    }
    if (!crpAdminsEmail.isEmpty()) {
      if (ccEmail.isEmpty()) {
        ccEmail += crpAdminsEmail;
      } else {
        ccEmail += ", " + crpAdminsEmail;
      }
    }

    // Copy to FL, CL and FM depending on CRP_EMAIL_CC_FL_FM_CL specificity
    if (this.hasSpecificities(APConstants.CRP_EMAIL_CC_FL_FM_CL)) {
      // CC for leaders and coordinators
      // CC will be also the Management Liaison associated with the flagship(s), if is PMU only the PMU contact
      Long crpPmuRole = Long.parseLong((String) this.getSession().get(APConstants.CRP_PMU_ROLE));
      Role roleCrpPmu = roleManager.getRoleById(crpPmuRole);
      // If Managment liason is PMU
      if (project.getProjecInfoPhase(this.getActualPhase()).getLiaisonInstitution() != null
        && project.getProjecInfoPhase(this.getActualPhase()).getLiaisonUser() != null) {
        if (project.getProjecInfoPhase(this.getActualPhase()).getLiaisonInstitution().getAcronym()
          .equals(roleCrpPmu.getAcronym())) {
          if (ccEmail.isEmpty()) {
            ccEmail += project.getProjecInfoPhase(this.getActualPhase()).getLiaisonUser().getUser().getEmail();
          } else {
            ccEmail += ", " + project.getProjecInfoPhase(this.getActualPhase()).getLiaisonUser().getUser().getEmail();
          }
        } else if (project.getProjecInfoPhase(this.getActualPhase()).getLiaisonInstitution() != null
          && project.getProjecInfoPhase(this.getActualPhase()).getLiaisonInstitution().getCrpProgram() != null
          && project.getProjecInfoPhase(this.getActualPhase()).getLiaisonInstitution().getCrpProgram()
            .getProgramType() == 1) {
          // If Managment liason is FL
          List<CrpProgram> crpPrograms = project
            .getCrp().getCrpPrograms().stream().filter(cp -> cp.getId() == project
              .getProjecInfoPhase(this.getActualPhase()).getLiaisonInstitution().getCrpProgram().getId())
            .collect(Collectors.toList());
          if (crpPrograms != null) {
            if (crpPrograms.size() > 1) {
              LOG.warn("Crp programs should be 1");
            }
            CrpProgram crpProgram = crpPrograms.get(0);
            for (CrpProgramLeader crpProgramLeader : crpProgram.getCrpProgramLeaders().stream()
              .filter(cpl -> cpl.getUser().isActive() && cpl.isActive()).collect(Collectors.toList())) {
              if (ccEmail.isEmpty()) {
                ccEmail += crpProgramLeader.getUser().getEmail();
              } else {
                ccEmail += ", " + crpProgramLeader.getUser().getEmail();
              }
            }
            // CC will be also other Cluster Leaders
            for (CrpClusterOfActivity crpClusterOfActivity : crpProgram.getCrpClusterOfActivities().stream()
              .filter(cl -> cl.isActive() && cl.getPhase().equals(this.getActualPhase()))
              .collect(Collectors.toList())) {
              for (CrpClusterActivityLeader crpClusterActivityLeader : crpClusterOfActivity
                .getCrpClusterActivityLeaders().stream().filter(cl -> cl.isActive()).collect(Collectors.toList())) {
                if (ccEmail.isEmpty()) {
                  ccEmail += crpClusterActivityLeader.getUser().getEmail();
                } else {
                  ccEmail += ", " + crpClusterActivityLeader.getUser().getEmail();
                }
              }
            }
          }
        }
      }
    }

    // BBC will be our gmail notification email.
    String bbcEmails = this.config.getEmailNotification();

    // subject
    String projectRole = null;
    Project project = projectManager.getProjectById(this.projectID);
    if (role.getId() == plRole.getId()) {
      projectRole = this.getText("email.project.assigned.PL");
    } else {
      projectRole = this.getText("email.project.assigned.PC");
    }
    String crp = loggedCrp.getAcronym() != null && !loggedCrp.getAcronym().isEmpty() ? loggedCrp.getAcronym()
      : loggedCrp.getName();
    String subject = this.getText("email.project.unAssigned.subject",
      new String[] {projectRole, crp, project.getStandardIdentifier(Project.EMAIL_SUBJECT_IDENTIFIER)});


    userUnassigned = userManager.getUser(userUnassigned.getId());

    // message
    StringBuilder message = new StringBuilder();
    // Building the Email message:
    message.append(this.getText("email.dear", new String[] {userUnassigned.getFirstName()}));

    if (role.getId() == plRole.getId().longValue()) {
      message.append(this.getText("email.project.leader.unAssigned",
        new String[] {projectRole, crp, project.getProjecInfoPhase(this.getActualPhase()).getTitle(),
          project.getStandardIdentifier(Project.EMAIL_SUBJECT_IDENTIFIER)}));
    } else {
      message.append(this.getText("email.project.coordinator.unAssigned",
        new String[] {projectRole, crp, project.getProjecInfoPhase(this.getActualPhase()).getTitle(),
          project.getStandardIdentifier(Project.EMAIL_SUBJECT_IDENTIFIER)}));
    }

    message.append(this.getText("email.support", new String[] {crpAdmins}));
    message.append(this.getText("email.bye"));

    sendMail.send(toEmail, ccEmail, bbcEmails, subject, message.toString(), null, null, null, true);
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
        project
          .setPartners(project.getProjectPartners().stream().filter(c -> c.isActive()).collect(Collectors.toList()));
        ProjectPartner leader = project.getLeader();
        if (leader != null) {
          // First we remove the element from the array.
          project.getPartners().remove(leader);
          // then we add it to the first position.
          project.getPartners().add(0, leader);

          // Or you could use a comparator and sort or just order the elements in javascript!
        }

        Collections.sort(project.getPartners(),
          (p1, p2) -> Boolean.compare(this.isPPA(p2.getInstitution()), this.isPPA(p1.getInstitution())));


        List<HistoryDifference> differences = new ArrayList<>();
        Map<String, String> specialList = new HashMap<>();
        int i = 0;
        for (ProjectPartner projectPartner : project.getPartners()) {
          int[] index = new int[1];
          index[0] = i;
          differences.addAll(historyComparator.getDifferencesList(projectPartner, transaction, specialList,
            "project.partners[" + i + "]", "project", 1));
          int j = 0;
          for (ProjectPartnerPerson partnerPerson : projectPartner.getProjectPartnerPersons()) {
            int[] indexPartners = new int[2];
            indexPartners[0] = i;
            indexPartners[1] = j;
            differences.addAll(historyComparator.getDifferencesList(partnerPerson, transaction, specialList,
              "project.partners[" + i + "].partnerPersons[" + j + "]", "project.projectPartner", 2));
            j++;
          }
          int k = 0;

          for (ProjectPartnerContribution projectPartnerContribution : projectPartner
            .getProjectPartnerContributions()) {
            differences
              .addAll(historyComparator.getDifferencesList(projectPartnerContribution, transaction, specialList,
                "project.partners[" + i + "].partnerContributors[" + k + "]", "project.partnerContributors", 2));
            k++;
          } ;

          List<ProjectPartnerOverall> overalls =
            projectPartner.getProjectPartnerOveralls().stream().filter(c -> c.isActive()).collect(Collectors.toList());
          if (!overalls.isEmpty()) {
            if (!historyComparator
              .getDifferencesList(overalls.get(0), transaction, specialList,
                "project.partners[" + i + "].partnerContributors[" + k + "]", "project.partnerContributors", 2)
              .isEmpty()) {
              if (!differences.contains("project.overall")) {
                differences.add(new HistoryDifference(UUID.randomUUID().toString(), "project.overall", true, "", ""));
              }
            }
          }

          i++;
        }

        if (this.isLessonsActive()) {
          this.loadLessons(loggedCrp, project);
        }
        if (project.getProjectComponentLesson() != null) {
          differences.addAll(historyComparator.getDifferencesList(project.getProjectComponentLesson(), transaction,
            specialList, "project.projectComponentLesson", "project", 1));
        }


        this.setDifferences(differences);

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
        reader.close();


        AutoSaveReader autoSaveReader = new AutoSaveReader();

        project = (Project) autoSaveReader.readFromJson(jReader);
        Project projectDb = projectManager.getProjectById(project.getId());
        project.setProjectInfo(projectDb.getProjecInfoPhase(this.getActualPhase()));
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
                pp.getInstitution().setLocations(pp.getInstitution().getInstitutionsLocations().stream()
                  .filter(c -> c.isActive()).collect(Collectors.toList()));
              }
            }


          }

          if (pp.getSelectedLocations() != null) {

            List<InstitutionLocation> locElements = new ArrayList<>();
            for (InstitutionLocation locElement : pp.getSelectedLocations()) {
              LocElement locElementDB =
                locationManager.getLocElementByISOCode(locElement.getLocElement().getIsoAlpha2());
              InstitutionLocation institutionLocation = institutionLocationManager.findByLocation(locElementDB.getId(),
                pp.getInstitution().getId().longValue());
              locElements.add(institutionLocation);
            }
            pp.getSelectedLocations().clear();
            pp.getSelectedLocations().addAll(locElements);
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

        this.setDraft(true);
      } else {

        this.setDraft(false);

        project.setProjectInfo(project.getProjecInfoPhase(this.getActualPhase()));
        if (project.getProjecInfoPhase(this.getActualPhase()).isProjectEditLeader()) {
          project.setPartners(project.getProjectPartners().stream()
            .filter(c -> c.isActive() && c.getPhase().equals(this.getActualPhase())).collect(Collectors.toList()));

        } else {
          List<ProjectPartner> partnes = new ArrayList<>();
          for (ProjectPartner projectPartner : project.getProjectPartners().stream()
            .filter(c -> c.isActive() && c.getPhase().equals(this.getActualPhase())).collect(Collectors.toList())) {
            Institution inst = institutionManager.getInstitutionById(projectPartner.getInstitution().getId());
            if (!inst.getCrpPpaPartners().stream()
              .filter(insti -> insti.isActive() && insti.getCrp().getId().longValue() == this.getCrpID().longValue())
              .collect(Collectors.toList()).isEmpty()) {
              partnes.add(projectPartner);
            }

          }
          project.setPartners(partnes);

        }
        if (!project.getPartners().isEmpty()) {
          if (this.isReportingActive()) {

            ProjectPartner partner =
              project.getProjectPartners().stream().filter(pp -> pp.isActive()).collect(Collectors.toList()).get(0);

            List<ProjectPartnerOverall> overalls = partner.getProjectPartnerOveralls().stream()
              .filter(c -> c.isActive() && c.getYear() == this.getActualPhase().getYear()).collect(Collectors.toList());
            if (!overalls.isEmpty()) {
              project.setOverall(overalls.get(0).getOverall());
              partnerOverall = overalls.get(0);
            }
          }

        }
        for (ProjectPartner projectPartner : project.getPartners()) {
          projectPartner.setSelectedLocations(new ArrayList<>());
          for (ProjectPartnerLocation projectPartnerLocation : projectPartner.getProjectPartnerLocations().stream()
            .filter(c -> c.isActive()).collect(Collectors.toList())) {
            projectPartner.getSelectedLocations().add(projectPartnerLocation.getInstitutionLocation());
          }


          projectPartner.getInstitution().setLocations(projectPartner.getInstitution().getInstitutionsLocations()
            .stream().filter(c -> c.isActive()).collect(Collectors.toList()));
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

    for (ProjectPartner projectPartner : project.getPartners()) {

      Institution institution = projectPartner.getInstitution();
      if (institution != null) {
        List<InstitutionLocation> institutionLocations = new ArrayList<>();
        institutionLocations.addAll(institution.getLocations());
        for (InstitutionLocation institutionLocation : institutionLocations) {
          if (projectPartner.getSelectedLocations() != null) {
            if (projectPartner.getSelectedLocations().contains(institutionLocation)) {
              institution.getLocations().remove(institutionLocation);

            }
          }

        }
      }

    }

    String params[] = {loggedCrp.getAcronym(), project.getId() + ""};
    this.setBasePermission(this.getText(Permission.PROJECT_PARTNER_BASE_PERMISSION, params));
    plRole = roleManager.getRoleById(Long.parseLong((String) this.getSession().get(APConstants.CRP_PL_ROLE)));
    pcRole = roleManager.getRoleById(Long.parseLong((String) this.getSession().get(APConstants.CRP_PC_ROLE)));

    // Getting the list of all institutions

    if (!project.getProjecInfoPhase(this.getActualPhase()).isProjectEditLeader()) {
      allInstitutions = new ArrayList<>();
      for (CrpPpaPartner crpPpaPartner : crpPpaPartnerManager.findAll().stream()
        .filter(c -> c.getCrp().getId().longValue() == loggedCrp.getId().longValue() && c.isActive()
          && c.getPhase().equals(this.getActualPhase()))
        .collect(Collectors.toList())) {
        allInstitutions.add(crpPpaPartner.getInstitution());
      }

    } else {
      allInstitutions = institutionManager.findAll().stream().filter(c -> c.isActive()).collect(Collectors.toList());

    }
    allInstitutions.sort((p1, p2) -> p1.getName().compareTo(p2.getName()));
    // Getting the list of all PPA institutions
    allPPAInstitutions = new ArrayList<>();
    for (CrpPpaPartner crpPpaPartner : crpPpaPartnerManager.findAll().stream()
      .filter(c -> c.getCrp().getId().longValue() == loggedCrp.getId().longValue() && c.isActive()
        && c.getPhase().equals(this.getActualPhase()))
      .collect(Collectors.toList())) {
      allPPAInstitutions.add(crpPpaPartner.getInstitution());
    }
    allPPAInstitutions.sort((p1, p2) -> p1.getName().compareTo(p2.getName()));
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

  /**
   * Delete projectPartner if it is not in the list of partners sent back from the UI.
   * 
   * @param previouslyEnteredPartner
   */
  private void removeDeletedPartners(ProjectPartner previouslyEnteredPartner) {
    if (project.getPartners() == null || !project.getPartners().contains(previouslyEnteredPartner)) {
      Project previousProject = projectManager.getProjectById(project.getId());
      ProjectPartnerPerson previousLeader = previousProject.getLeaderPerson(this.getActualPhase());
      List<ProjectPartnerPerson> previousCoordinators = previousProject.getCoordinatorPersons(this.getActualPhase());

      for (ProjectPartner previousPartner : previousProject.getProjectPartners().stream()
        .filter(c -> c.isActive() && c.getPhase().equals(this.getActualPhase())).collect(Collectors.toList())) {
        if (project.getProjecInfoPhase(this.getActualPhase()).isProjectEditLeader()) {
          projectPartnerManager.deleteProjectPartner(previouslyEnteredPartner.getId());


        } else {
          // Check to see if the user has priviliges for this crp
          Institution inst = institutionManager.getInstitutionById(previouslyEnteredPartner.getInstitution().getId());
          if (!inst.getCrpPpaPartners().stream()
            .filter(insti -> insti.isActive() && insti.getCrp().getId().longValue() == this.getCrpID().longValue())
            .collect(Collectors.toList()).isEmpty()) {
            projectPartnerManager.deleteProjectPartner(previouslyEnteredPartner.getId());
          }

        }
      }
    }
  }

  private void removeProjectPartnerPersons(ProjectPartner projectPartnerClient, ProjectPartner projectPartnerDB) {
    for (ProjectPartnerPerson partnerPerson : projectPartnerDB.getProjectPartnerPersons()) {
      if (projectPartnerClient.getPartnerPersons() == null
        || !projectPartnerClient.getPartnerPersons().contains(partnerPerson)) {
        for (DeliverablePartnership deliverablePartnership : partnerPerson.getDeliverablePartnerships().stream()
          .filter(c -> c.isActive()).collect(Collectors.toList())) {
          deliverablePartnershipManager.deleteDeliverablePartnership(deliverablePartnership.getId());
        }
        projectPartnerPersonManager.deleteProjectPartnerPerson(partnerPerson.getId());
      }
    }
  }

  @Override
  public String save() {
    if (this.hasPermission("canEdit")) {
      Project projectDB = projectManager.getProjectById(projectID);
      List<ProjectPartnerPerson> previousCoordinators = projectDB.getCoordinatorPersonsDB(this.getActualPhase());
      ProjectPartnerPerson previousLeader = projectDB.getLeaderPersonDB(this.getActualPhase());

      List<ProjectPartner> partnersDB = projectDB.getProjectPartners().stream()
        .filter(c -> c.isActive() && c.getPhase().equals(this.getActualPhase())).collect(Collectors.toList());


      for (ProjectPartner projectPartnerDB : partnersDB) {
        this.removeDeletedPartners(projectPartnerDB);
      }
      if (project.getPartners() != null) {

        // Looping through the UI partner list
        for (ProjectPartner projectPartnerClient : project.getPartners()) {
          ProjectPartner projectPartnerDB = null;
          if (projectPartnerClient.getId() == null) {


            projectPartnerClient.setActive(true);

            projectPartnerClient.setCreatedBy(this.getCurrentUser());
            projectPartnerClient.setModifiedBy(this.getCurrentUser());
            projectPartnerClient.setModificationJustification("");
            projectPartnerClient.setActiveSince(new Date());
            projectPartnerClient.setProject(project);
            projectPartnerClient.setPhase(this.getActualPhase());
            projectPartnerDB = projectPartnerManager.saveProjectPartner(projectPartnerClient);
          } else {
            projectPartnerDB = projectPartnerManager.getProjectPartnerById(projectPartnerClient.getId());
            projectPartnerDB.setActive(true);
            projectPartnerDB.setProject(project);
            projectPartnerDB.setCreatedBy(projectPartnerDB.getCreatedBy());
            projectPartnerDB.setResponsibilities(projectPartnerClient.getResponsibilities());
            projectPartnerDB.setModifiedBy(this.getCurrentUser());
            projectPartnerDB.setModificationJustification("");
            projectPartnerDB.setPhase(projectPartnerDB.getPhase());
            projectPartnerDB.setActiveSince(projectPartnerDB.getActiveSince());
            projectPartnerDB.setPartnerPersons(projectPartnerClient.getPartnerPersons());
            projectPartnerDB.setSelectedLocations(projectPartnerClient.getSelectedLocations());
            projectPartnerDB.setPartnerContributors(projectPartnerDB.getPartnerContributors());
            projectPartnerDB = projectPartnerManager.saveProjectPartner(projectPartnerDB);
          }


          // projectPartnerDB = projectPartnerManager.getProjectPartnerById(projectPartnerClient.getId());

          this.removeProjectPartnerPersons(projectPartnerClient, projectPartnerDB);
          this.saveProjectPartnerPersons(projectPartnerClient, projectPartnerDB);

          this.saveProjectPartnerContributions(projectPartnerClient, projectPartnerDB);
          this.saveLocations(projectPartnerClient, projectPartnerDB);


        }
      }
      if (this.isReportingActive()) {
        Project projectReporting = projectManager.getProjectById(projectID);

        List<ProjectPartner> partnersReporting = new ArrayList<>(
          projectReporting.getProjectPartners().stream().filter(p -> p.isActive()).collect(Collectors.toList()));

        if (!partnersReporting.isEmpty()) {
          for (ProjectPartner partner : partnersReporting) {
            List<ProjectPartnerOverall> overalls = new ArrayList<>(partner.getProjectPartnerOveralls().stream()
              .filter(ppo -> ppo.isActive() && ppo.getYear() == this.getActualPhase().getYear())
              .collect(Collectors.toList()));

            ProjectPartnerOverall overall = new ProjectPartnerOverall();
            if (overalls.isEmpty()) {
              overall.setProjectPartner(partner);
              overall.setYear(this.getActualPhase().getYear());
            } else {
              overall = overalls.get(0);
            }

            if (project.getOverall() != null) {
              overall.setOverall(project.getOverall());
              projectPartnerOverallManager.saveProjectPartnerOverall(overall);
            }
          }
        }

      }


      if (this.isLessonsActive()) {
        this.saveLessons(loggedCrp, project);
      }

      ProjectPartnerPerson leader = project.getLeaderPerson(this.getActualPhase());
      // Notify user if the project leader was created.

      this.updateRoles(previousLeader, leader, plRole);


      this.updateRoles(previousCoordinators, project.getCoordinatorPersons(this.getActualPhase()), pcRole);
      // project = projectManager.getProjectById(projectID);
      projectDB.setActiveSince(new Date());
      projectDB.setCreatedBy(this.getCurrentUser());
      projectDB.setModifiedBy(this.getCurrentUser());

      List<String> relationsName = new ArrayList<>();
      relationsName.add(APConstants.PROJECT_PARTNERS_RELATION);
      relationsName.add(APConstants.PROJECT_LESSONS_RELATION);
      relationsName.add(APConstants.PROJECT_INFO_RELATION);

      projectManager.saveProject(projectDB, this.getActionName(), relationsName, this.getActualPhase());
      Path path = this.getAutoSaveFilePath();
      if (path.toFile().exists()) {
        path.toFile().delete();
      }
      if (this.getUrl() == null || this.getUrl().isEmpty()) {
        Collection<String> messages = this.getActionMessages();
        if (!this.getInvalidFields().isEmpty()) {
          this.setActionMessages(null);
          // this.addActionMessage(Map.toString(this.getInvalidFields().toArray()));
          List<String> keys = new ArrayList<String>(this.getInvalidFields().keySet());
          for (String key : keys) {
            this.addActionMessage(key + ": " + this.getInvalidFields().get(key));
          }

        } else {
          this.addActionMessage("message:" + this.getText("saving.saved"));
        }
        return SUCCESS;
      } else {
        this.addActionMessage("");
        this.setActionMessages(null);
        return REDIRECT;
      }
    }
    return NOT_AUTHORIZED;

  }

  /**
   * @param partner - the projectPartner edited in the UI
   */
  public void saveLocations(ProjectPartner projectPartnerClient, ProjectPartner projectPartnerDB) {

    /**
     * This is a small optimization to return the locations pre-fetched rather than get them one by one.
     */
    projectPartnerDB = projectPartnerManager.getProjectPartnerByIdAndEagerFetchLocations(projectPartnerDB.getId());

    List<ProjectPartnerLocation> projectPartnerLocationsDB =
      projectPartnerDB.getProjectPartnerLocations().stream().filter(c -> c.isActive()).collect(Collectors.toList());
    for (ProjectPartnerLocation projectPartnerLocationDB : projectPartnerLocationsDB) {
      String isoAlpha2 = projectPartnerLocationDB.getInstitutionLocation().getLocElement().getIsoAlpha2();
      // Check to see if an element in the collection has the same isoAplha2 code
      if (projectPartnerClient.getSelectedLocations().stream()
        .filter(c -> c.getLocElement().getIsoAlpha2().equals(isoAlpha2)).collect(Collectors.toList()).isEmpty()) {
        // The location does not exist anymore so delete it.
        LOG.debug("Deleting : " + projectPartnerLocationDB);
        projectPartnerLocationManager.deleteProjectPartnerLocation(projectPartnerLocationDB.getId());
      }
    }
    for (InstitutionLocation updatedInstitutionLocationClient : projectPartnerClient.getSelectedLocations()) {
      String isoAlpha2 = updatedInstitutionLocationClient.getLocElement().getIsoAlpha2();
      // Check to see if the location is already saved by comparing the isoAplpha2 codes.
      if (projectPartnerLocationsDB.stream()
        .filter(c -> c.isActive() && isoAlpha2.equals(c.getInstitutionLocation().getLocElement().getIsoAlpha2()))
        .collect(Collectors.toList()).isEmpty()) {
        LocElement locElement =
          locationManager.getLocElementByISOCode(updatedInstitutionLocationClient.getLocElement().getIsoAlpha2());
        InstitutionLocation institutionLocation =
          institutionLocationManager.findByLocation(locElement.getId(), projectPartnerClient.getInstitution().getId());
        ProjectPartnerLocation partnerLocation = new ProjectPartnerLocation();
        partnerLocation.setInstitutionLocation(institutionLocation);
        partnerLocation.setActive(true);
        partnerLocation.setActiveSince(new Date());
        partnerLocation.setCreatedBy(this.getCurrentUser());
        partnerLocation.setModificationJustification("");
        partnerLocation.setModifiedBy(this.getCurrentUser());
        partnerLocation.setProjectPartner(projectPartnerDB);
        partnerLocation = projectPartnerLocationManager.saveProjectPartnerLocation(partnerLocation);
        LOG.debug("Saving : " + partnerLocation);
      }
    }


  }

  private ProjectPartner saveProjectPartner(ProjectPartner projectPartnerClient, Project projectDB) {
    if (projectPartnerClient.getId() == null) {
      // New entity
      projectPartnerClient.setActive(true);
      projectPartnerClient.setCreatedBy(this.getCurrentUser());
      projectPartnerClient.setModifiedBy(this.getCurrentUser());
      projectPartnerClient.setModificationJustification("");
      projectPartnerClient.setActiveSince(new Date());
      projectPartnerClient.setProject(projectDB);

      projectPartnerClient = projectPartnerManager.saveProjectPartner(projectPartnerClient);

    } else {
      // Existing entity
      ProjectPartner projectPartnerDB = projectPartnerManager.getProjectPartnerById(projectPartnerClient.getId());
      projectPartnerDB.setActive(true);
      projectPartnerDB.setProject(projectDB);
      projectPartnerDB.setModifiedBy(this.getCurrentUser());
      projectPartnerDB.setModificationJustification("");
      projectPartnerDB.setResponsibilities(projectPartnerClient.getResponsibilities());
      projectPartnerDB.setSubDepartment(projectPartnerClient.getSubDepartment());
      projectPartnerDB = projectPartnerManager.saveProjectPartner(projectPartnerDB);

      return projectPartnerDB;
    }
    return projectPartnerClient;
  }

  private void saveProjectPartnerContributions(ProjectPartner projectPartnerClient, ProjectPartner projectPersonDB) {

    List<ProjectPartnerContribution> partnerContributions =
      projectPersonDB.getProjectPartnerContributions().stream().filter(c -> c.isActive()).collect(Collectors.toList());

    for (ProjectPartnerContribution partnerContribution : partnerContributions) {
      if (projectPartnerClient.getPartnerContributors() == null
        || !projectPartnerClient.getPartnerContributors().contains(partnerContribution)) {
        projectPartnerContributionManager.deleteProjectPartnerContribution(partnerContribution.getId());
      }
    }
    if (projectPartnerClient.getPartnerContributors() != null) {

      for (ProjectPartnerContribution partnerContributionClient : projectPartnerClient.getPartnerContributors()) {
        /**
         * Do nothing if the id is notn ull as we do not have the ability to update any details for the
         * PartnerContributors
         */
        if (partnerContributionClient.getId() == null) {
          partnerContributionClient.setActive(true);

          partnerContributionClient.setCreatedBy(this.getCurrentUser());
          partnerContributionClient.setModifiedBy(this.getCurrentUser());
          partnerContributionClient.setModificationJustification("");
          partnerContributionClient.setActiveSince(new Date());
          partnerContributionClient.setProjectPartner(projectPartnerClient);
          // This looks like we are setting the partnerContribution to the first PPA partner???
          if (partnerContributionClient.getProjectPartnerContributor().getId() == null) {
            Long institutionId = partnerContributionClient.getProjectPartnerContributor().getInstitution().getId();
            List<ProjectPartner> partenerContributor = project.getPartners().stream()
              .filter(c -> c.getInstitution().getId().equals(institutionId)).collect(Collectors.toList());
            if (!partenerContributor.isEmpty()) {
              partnerContributionClient.getProjectPartnerContributor().setId(partenerContributor.get(0).getId());
              LOG.debug("User didn't select a ProjectPartnerContributor for projectPartner : " + projectPersonDB.getId()
                + ", setting the projectPartnerContributor to projectPartner with id = "
                + partenerContributor.get(0).getId());
            }

          }
          partnerContributionClient =
            projectPartnerContributionManager.saveProjectPartnerContribution(partnerContributionClient);
        }
      }
    }
  }

  private ProjectPartnerPerson saveProjectPartnerPerson(ProjectPartner projectPartnerDB,
    ProjectPartnerPerson partnerPersonClient) {
    if (partnerPersonClient.getUser() == null
      || (partnerPersonClient.getUser().getId() == null || partnerPersonClient.getUser().getId().longValue() == -1)) {
      partnerPersonClient.setUser(null);
      return partnerPersonClient;
    } else {

      if (partnerPersonClient.getId() == null) {
        partnerPersonClient.setActive(true);

        partnerPersonClient.setCreatedBy(this.getCurrentUser());
        partnerPersonClient.setModifiedBy(this.getCurrentUser());
        partnerPersonClient.setModificationJustification("");
        partnerPersonClient.setActiveSince(new Date());
        partnerPersonClient.setProjectPartner(projectPartnerDB);
        if (partnerPersonClient.getUser() == null || (partnerPersonClient.getUser().getId() == null
          || partnerPersonClient.getUser().getId().longValue() == -1)) {
          partnerPersonClient.setUser(null);
        } else {
          partnerPersonClient.setUser(userManager.getUser(partnerPersonClient.getUser().getId()));
        }
        if (partnerPersonClient.getContactType().equals(APConstants.PROJECT_PARTNER_PL)
          || partnerPersonClient.getContactType().equals(APConstants.PROJECT_PARTNER_PC)) {
          this.notifyNewUserCreated(partnerPersonClient.getUser());
        }
        partnerPersonClient = projectPartnerPersonManager.saveProjectPartnerPerson(partnerPersonClient);

      } else {

        ProjectPartnerPerson dbPerson =
          projectPartnerPersonManager.getProjectPartnerPersonById(partnerPersonClient.getId());
        dbPerson.setActive(true);
        dbPerson.setModifiedBy(this.getCurrentUser());
        dbPerson.setModificationJustification("");
        dbPerson.setContactType(partnerPersonClient.getContactType());
        if (partnerPersonClient.getUser() == null || (partnerPersonClient.getUser().getId() == null
          || partnerPersonClient.getUser().getId().longValue() == -1)) {
          dbPerson.setUser(null);
        } else {
          dbPerson.setUser(userManager.getUser(partnerPersonClient.getUser().getId()));
        }
        if (dbPerson.getUser() != null && partnerPersonClient.getContactType().equals(APConstants.PROJECT_PARTNER_PL)
          || partnerPersonClient.getContactType().equals(APConstants.PROJECT_PARTNER_PC)) {
          this.notifyNewUserCreated(dbPerson.getUser());
        }
        dbPerson = projectPartnerPersonManager.saveProjectPartnerPerson(dbPerson);
        return dbPerson;
      }
    }

    return partnerPersonClient;
  }

  /**
   * @param projectPartnerClient - the projectPartner edited in the UI
   * @param projectPartnerDB - the projectPartner entity retrieved from the database.
   */
  private void saveProjectPartnerPersons(ProjectPartner projectPartnerClient, ProjectPartner projectPartnerDB) {
    if (projectPartnerClient.getPartnerPersons() != null) {
      for (ProjectPartnerPerson partnerPersonClient : projectPartnerClient.getPartnerPersons()) {
        if (partnerPersonClient.getUser() != null && partnerPersonClient.getUser().getId() != null) {
          ProjectPartnerPerson projectPartnerPersonDB =
            this.saveProjectPartnerPerson(projectPartnerDB, partnerPersonClient);
        }
      }

      for (ProjectPartnerPerson partnerPerson : projectPartnerClient.getPartnerPersons()) {

        if (partnerPerson.getUser() != null
          && (partnerPerson.getUser().getId() != null && partnerPerson.getUser().getId().longValue() != -1)) {

          User userDB = userManager.getUser(partnerPerson.getUser().getId());
          // Not sure what we are doing here - creating a new CrpUser for some reason?
          if (userDB.getCrpUsers().stream().filter(c -> c.getCrp().getId().equals(loggedCrp.getId()))
            .collect(Collectors.toList()).isEmpty()) {
            CrpUser crpUser = new CrpUser();
            crpUser.setUser(userDB);
            crpUser.setActiveSince(new Date());
            crpUser.setCreatedBy(this.getCurrentUser());
            crpUser.setCrp(loggedCrp);
            crpUser.setModificationJustification("");
            crpUser.setModifiedBy(this.getCurrentUser());
            crpUserManager.saveCrpUser(crpUser);

          }
        }


      }
    }
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
  private void updateRoles(List<ProjectPartnerPerson> previousPartnerPerson, List<ProjectPartnerPerson> partnerPerson,
    Role role) {
    long roleId = role.getId();

    String roleAcronym = role.getAcronym();
    if (previousPartnerPerson != null && partnerPerson != null) {
      for (ProjectPartnerPerson projectPartnerPerson : partnerPerson) {

        if (projectPartnerPerson.getUser() != null && projectPartnerPerson.getUser().getId() != null) {
          if (!previousPartnerPerson.contains(projectPartnerPerson)) {
            UserRole userRole = new UserRole();
            userRole.setRole(role);
            userRole.setUser(projectPartnerPerson.getUser());

            role = roleManager.getRoleById(role.getId());
            if (!role.getUserRoles().contains(userRole)) {
              userRoleManager.saveUserRole(userRole);
              this.addCrpUser(projectPartnerPerson.getUser());
            }


            // Notifying user is assigned as Project Leader/Coordinator.
            if (projectPartnerPerson.getUser() != null) {
              this.notifyRoleAssigned(projectPartnerPerson.getUser(), role);
            }

          }
        }

      }

      for (ProjectPartnerPerson projectPartnerPerson : previousPartnerPerson) {
        if (projectPartnerPerson.getUser() != null && projectPartnerPerson.getUser().getId() != null) {
          if (!partnerPerson.contains(projectPartnerPerson)) {

            List<UserRole> rolesUser = userRoleManager.getUserRolesByUserId(projectPartnerPerson.getUser().getId());
            if (rolesUser != null) {
              rolesUser =
                rolesUser.stream().filter(c -> c.getRole().getId().longValue() == roleId).collect(Collectors.toList());
              if (!rolesUser.isEmpty()) {
                if (projectPartnerPerson.getUser().getProjectPartnerPersons().stream()
                  .filter(c -> c.isActive() && c.getContactType().equals(roleAcronym)
                    && c.getProjectPartner().getProject() != null && c.getProjectPartner().getProject().getId()
                      .longValue() != projectPartnerPerson.getProjectPartner().getProject().getId().longValue())
                  .collect(Collectors.toList()).size() == 0) {
                  userRoleManager.deleteUserRole(rolesUser.get(0).getId());
                  this.checkCrpUserByRole(projectPartnerPerson.getUser());
                }
              }
            }
            // Notifying user that is not the project leader anymore
            this.notifyRoleUnassigned(projectPartnerPerson.getUser(), role);
          }
        }
      }
    }
    /*
     * else if (previousPartnerPerson != null && partnerPerson == null) {
     * List<UserRole> rolesUser = userRoleManager.getUserRolesByUserId(previousPartnerPerson.getUser().getId());
     * if (rolesUser != null) {
     * rolesUser =
     * rolesUser.stream().filter(c -> c.getRole().getId().longValue() == roleId).collect(Collectors.toList());
     * if (!rolesUser.isEmpty()) {
     * if (previousPartnerPerson.getUser().getProjectPartnerPersons().stream()
     * .filter(c -> c.isActive() && c.getContactType().equals(roleAcronym) && c.getProjectPartner().getProject()
     * .getId().longValue() != previousPartnerPerson.getProjectPartner().getProject().getId().longValue())
     * .collect(Collectors.toList()).size() == 0) {
     * userRoleManager.deleteUserRole(rolesUser.get(0).getId());
     * this.checkCrpUserByRole(previousPartnerPerson.getUser());
     * }
     * }
     * }
     * // Notifying user that is not the project leader anymore
     * this.notifyRoleUnassigned(previousPartnerPerson.getUser(), role);
     * } else if (previousPartnerPerson != null && partnerPerson != null) {
     * if (!partnerPerson.getUser().getId().equals(previousPartnerPerson.getUser().getId())) {
     * UserRole userRole = new UserRole();
     * userRole.setRole(role);
     * userRole.setUser(partnerPerson.getUser());
     * role = roleManager.getRoleById(role.getId());
     * if (!role.getUserRoles().contains(userRole)) {
     * userRoleManager.saveUserRole(userRole);
     * this.addCrpUser(partnerPerson.getUser());
     * }
     * // Notifying user is assigned as Project Leader/Coordinator.
     * this.notifyRoleAssigned(partnerPerson.getUser(), role);
     * // Deleting role.
     * List<UserRole> rolesUser = userRoleManager.getUserRolesByUserId(previousPartnerPerson.getUser().getId());
     * if (rolesUser != null) {
     * rolesUser =
     * rolesUser.stream().filter(c -> c.getRole().getId().longValue() == roleId).collect(Collectors.toList());
     * if (!rolesUser.isEmpty()) {
     * if (previousPartnerPerson.getUser().getProjectPartnerPersons().stream()
     * .filter(c -> c.isActive() && c.getContactType().equals(roleAcronym) && c.getProjectPartner().getProject()
     * .getId().longValue() != previousPartnerPerson.getProjectPartner().getProject().getId().longValue())
     * .collect(Collectors.toList()).size() == 0) {
     * userRoleManager.deleteUserRole(rolesUser.get(0).getId());
     * this.checkCrpUserByRole(previousPartnerPerson.getUser());
     * }
     * }
     * }
     * // Notifying user that is not the project leader anymore
     * this.notifyRoleUnassigned(previousPartnerPerson.getUser(), role);
     * }
     * }
     */
    // this.clearPermissionsCache();
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
        if (userRole.getUser() != null) {
          userRoleManager.saveUserRole(userRole);
          this.addCrpUser(partnerPerson.getUser());
        }

      }


      if (partnerPerson.getUser() != null) {
        this.notifyRoleAssigned(partnerPerson.getUser(), role);
      }
      // Notifying user is assigned as Project Leader/Coordinator.

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
            this.checkCrpUserByRole(previousPartnerPerson.getUser());
          }

        }
      }

      // Notifying user that is not the project leader anymore
      this.notifyRoleUnassigned(previousPartnerPerson.getUser(), role);
    } else if (previousPartnerPerson != null && partnerPerson != null && partnerPerson.getUser() != null
      && partnerPerson.getUser().getId() != null && previousPartnerPerson.getUser() != null) {
      if (!partnerPerson.getUser().getId().equals(previousPartnerPerson.getUser().getId())) {
        UserRole userRole = new UserRole();
        userRole.setRole(role);
        userRole.setUser(partnerPerson.getUser());

        role = roleManager.getRoleById(role.getId());
        if (!role.getUserRoles().contains(userRole)) {
          userRoleManager.saveUserRole(userRole);
          this.addCrpUser(partnerPerson.getUser());
        }
        // Notifying user is assigned as Project Leader/Coordinator.
        if (partnerPerson.getUser() != null) {
          this.notifyRoleAssigned(partnerPerson.getUser(), role);
        } // Deleting role.
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
              this.checkCrpUserByRole(previousPartnerPerson.getUser());
            }
          }
        }
        // Notifying user that is not the project leader anymore
        this.notifyRoleUnassigned(previousPartnerPerson.getUser(), role);
      }
    }
    // this.clearPermissionsCache();
  }


  @Override
  public void validate() {
    if (save) {
      projectPartnersValidator.validate(this, project, true);
      if (projectPartnersValidator.isHasErros()) {
        if (project.getPartners() != null) {
          for (ProjectPartner projectPartner : project.getPartners()) {

            if (projectPartner.getInstitution() != null && projectPartner.getInstitution().getId() != null) {
              projectPartner
                .setInstitution(institutionManager.getInstitutionById(projectPartner.getInstitution().getId()));

            }

            if (projectPartner.getPartnerPersons() != null) {
              for (ProjectPartnerPerson projectPartnerPerson : projectPartner.getPartnerPersons()) {
                if (projectPartnerPerson.getUser() != null && projectPartnerPerson.getUser().getId() != null
                  && projectPartnerPerson.getUser().getId().longValue() != -1) {
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
