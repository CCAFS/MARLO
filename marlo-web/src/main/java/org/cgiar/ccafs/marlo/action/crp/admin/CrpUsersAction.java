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


package org.cgiar.ccafs.marlo.action.crp.admin;

import org.cgiar.ccafs.marlo.action.BaseAction;
import org.cgiar.ccafs.marlo.config.APConstants;
import org.cgiar.ccafs.marlo.data.manager.CrpUserManager;
import org.cgiar.ccafs.marlo.data.manager.GlobalUnitManager;
import org.cgiar.ccafs.marlo.data.manager.PhaseManager;
import org.cgiar.ccafs.marlo.data.manager.ProjectManager;
import org.cgiar.ccafs.marlo.data.manager.RoleManager;
import org.cgiar.ccafs.marlo.data.manager.UserManager;
import org.cgiar.ccafs.marlo.data.manager.UserRoleManager;
import org.cgiar.ccafs.marlo.data.model.CrpClusterActivityLeader;
import org.cgiar.ccafs.marlo.data.model.CrpProgramLeader;
import org.cgiar.ccafs.marlo.data.model.CrpSitesLeader;
import org.cgiar.ccafs.marlo.data.model.CrpUser;
import org.cgiar.ccafs.marlo.data.model.GlobalUnit;
import org.cgiar.ccafs.marlo.data.model.LiaisonUser;
import org.cgiar.ccafs.marlo.data.model.Phase;
import org.cgiar.ccafs.marlo.data.model.ProgramType;
import org.cgiar.ccafs.marlo.data.model.Project;
import org.cgiar.ccafs.marlo.data.model.ProjectInfo;
import org.cgiar.ccafs.marlo.data.model.ProjectPartnerPerson;
import org.cgiar.ccafs.marlo.data.model.ProjectPhase;
import org.cgiar.ccafs.marlo.data.model.ProjectStatusEnum;
import org.cgiar.ccafs.marlo.data.model.Role;
import org.cgiar.ccafs.marlo.data.model.User;
import org.cgiar.ccafs.marlo.data.model.UserRole;
import org.cgiar.ccafs.marlo.utils.APConfig;
import org.cgiar.ccafs.marlo.utils.SendMailS;
import org.cgiar.ccafs.marlo.validation.superadmin.GuestUsersValidator;

import org.cgiar.ciat.auth.LDAPUser;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import javax.inject.Inject;

import org.apache.commons.lang.RandomStringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CrpUsersAction extends BaseAction {

  private static final long serialVersionUID = 4072844056573550689L;

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

  private Phase phase;

  private final Logger LOG = LoggerFactory.getLogger(CrpUsersAction.class);
  // Managers
  private GlobalUnitManager globalUnitManager;
  private CrpUserManager crpUserManager;
  private final SendMailS sendMailS;
  private UserManager userManager;
  private ProjectManager projectManager;
  private List<Project> phasesProjects;
  private PhaseManager phaseManager;
  private RoleManager roleManager;

  private UserRoleManager userRoleManager;
  // Parameters
  private User user;
  private String selectedGlobalUnitAcronym;
  private String message;
  private GuestUsersValidator validator;
  private String emailSend;
  private boolean isCGIARUser;
  private List<UserRole> users;
  private List<Role> rolesCrp;


  private List<GlobalUnit> crps;

  @Inject
  public CrpUsersAction(APConfig config, GlobalUnitManager globalUnitManager, CrpUserManager crpUserManager,
    UserManager userManager, ProjectManager projectManager, PhaseManager phaseManager, RoleManager roleManager,
    UserRoleManager userRoleManager, SendMailS sendMailS, GuestUsersValidator validator) {
    super(config);
    this.projectManager = projectManager;
    this.phaseManager = phaseManager;
    this.userManager = userManager;
    this.roleManager = roleManager;
    this.userRoleManager = userRoleManager;
    this.validator = validator;
    this.userRoleManager = userRoleManager;
    this.sendMailS = sendMailS;
    this.globalUnitManager = globalUnitManager;
    this.crpUserManager = crpUserManager;
  }

  public String getEmailSend() {
    return emailSend;
  }

  public String getRelations(long userID, long roleID) {

    User user = userManager.getUser(userID);
    Role role = roleManager.getRoleById(roleID);
    List<Object> relations = new ArrayList<>();
    switch (role.getAcronym()) {
      case "FPL":


        for (CrpProgramLeader crpProgramsLeader : user.getCrpProgramLeaders().stream()
          .filter(
            c -> c.isActive() && c.getCrpProgram().getProgramType() == ProgramType.FLAGSHIP_PROGRAM_TYPE.getValue()
              && !c.isManager() && c.getCrpProgram().getCrp().getId().longValue() == this.getCrpID().longValue())
          .collect(Collectors.toList())) {
          relations.add(crpProgramsLeader.getCrpProgram().getAcronym());
        }
        break;

      case "FPM":


        for (CrpProgramLeader crpProgramsLeader : user.getCrpProgramLeaders().stream()
          .filter(
            c -> c.isActive() && c.getCrpProgram().getProgramType() == ProgramType.FLAGSHIP_PROGRAM_TYPE.getValue()
              && c.isManager() && c.getCrpProgram().getCrp().getId().longValue() == this.getCrpID().longValue())
          .collect(Collectors.toList())) {
          relations.add(crpProgramsLeader.getCrpProgram().getAcronym());
        }
        break;


      case "RPL":
        for (CrpProgramLeader crpProgramsLeader : user.getCrpProgramLeaders().stream()
          .filter(
            c -> c.isActive() && c.getCrpProgram().getProgramType() == ProgramType.REGIONAL_PROGRAM_TYPE.getValue()
              && !c.isManager() && c.getCrpProgram().getCrp().getId().longValue() == this.getCrpID().longValue())
          .collect(Collectors.toList())) {
          relations.add(crpProgramsLeader.getCrpProgram().getAcronym());
        }
        break;

      case "RPM":
        for (CrpProgramLeader crpProgramsLeader : user.getCrpProgramLeaders().stream()
          .filter(
            c -> c.isActive() && c.getCrpProgram().getProgramType() == ProgramType.REGIONAL_PROGRAM_TYPE.getValue()
              && c.isManager() && c.getCrpProgram().getCrp().getId().longValue() == this.getCrpID().longValue())
          .collect(Collectors.toList())) {
          relations.add(crpProgramsLeader.getCrpProgram().getAcronym());
        }
        break;

      case "ML":

        for (LiaisonUser liaisonUser : user.getLiasonsUsers().stream().filter(c -> c.isActive()
          && c.getLiaisonInstitution().isActive() && c.getCrp().getId().longValue() == this.getCrpID().longValue())
          .collect(Collectors.toList())) {
          relations.add(liaisonUser.getLiaisonInstitution().getAcronym());
        }
        break;
      case "CP":

        for (LiaisonUser liaisonUser : user.getLiasonsUsers().stream().filter(c -> c.isActive()
          && c.getLiaisonInstitution().isActive() && c.getLiaisonInstitution().getCrpProgram() == null)
          .collect(Collectors.toList())) {
          if (!relations.contains(liaisonUser.getLiaisonInstitution().getAcronym())) {
            relations.add(liaisonUser.getLiaisonInstitution().getAcronym());
          }

        }
        break;
      case "PL":

        for (ProjectPartnerPerson projectPartnerPerson : user.getProjectPartnerPersons().stream()
          .filter(c -> c.isActive() && c.getProjectPartner().isActive() && c.getContactType().equalsIgnoreCase("PL")
            && c.getProjectPartner().getPhase() != null
            && c.getProjectPartner().getPhase().equals(this.getActualPhase())
            && phasesProjects.contains(c.getProjectPartners().getProject())
            && c.getProjectPartners().getProject().isActive()
            && c.getProjectPartners().getProject().getProjecInfoPhase(this.getActualPhase()) != null
            && c.getProjectPartners().getProject().getProjecInfoPhase(this.getActualPhase()).isActive())
          .collect(Collectors.toList())) {
          if (projectPartnerPerson.getProjectPartner().getProject().getProjecInfoPhase(this.getActualPhase())
            .getEndDate() != null) {
            Calendar cal = Calendar.getInstance();

            cal.setTime(projectPartnerPerson.getProjectPartner().getProject().getProjecInfoPhase(this.getActualPhase())
              .getEndDate());
            Integer year = cal.get(Calendar.YEAR);

            if (year != null && (year >= this.getActualPhase().getYear())) {

              if (projectPartnerPerson.getProjectPartner().getProject().getProjecInfoPhase(this.getActualPhase())
                .getStatus() == Integer.parseInt(ProjectStatusEnum.Ongoing.getStatusId())
                || projectPartnerPerson.getProjectPartner().getProject().getProjecInfoPhase(this.getActualPhase())
                  .getStatus() == Integer.parseInt(ProjectStatusEnum.Extended.getStatusId())) {
                relations.add(projectPartnerPerson.getProjectPartner().getProject()
                  .getStandardIdentifier(Project.EMAIL_SUBJECT_IDENTIFIER));
              }
            }
          }
        }
        break;
      case "PC":

        for (ProjectPartnerPerson projectPartnerPerson : user.getProjectPartnerPersons().stream()
          .filter(c -> c.isActive() && c.getProjectPartner().isActive() && c.getContactType().equalsIgnoreCase("PC")
            && c.getProjectPartner().getPhase() != null
            && c.getProjectPartner().getPhase().equals(this.getActualPhase())
            && phasesProjects.contains(c.getProjectPartners().getProject()))
          .collect(Collectors.toList())) {
          if (projectPartnerPerson.getProjectPartner().getProject().getProjecInfoPhase(this.getActualPhase())
            .getStatus() == Integer.parseInt(ProjectStatusEnum.Ongoing.getStatusId())
            || projectPartnerPerson.getProjectPartner().getProject().getProjecInfoPhase(this.getActualPhase())
              .getStatus() == Integer.parseInt(ProjectStatusEnum.Extended.getStatusId())) {
            relations.add(projectPartnerPerson.getProjectPartner().getProject()
              .getStandardIdentifier(Project.EMAIL_SUBJECT_IDENTIFIER));
          }

        }
        break;
      case "CL":
        for (CrpClusterActivityLeader crpClusterActivityLeader : user.getCrpClusterActivityLeaders().stream()
          .filter(c -> c.isActive() && c.getCrpClusterOfActivity().isActive()
            && c.getCrpClusterOfActivity().getPhase() != null
            && c.getCrpClusterOfActivity().getPhase().equals(this.getActualPhase())
            && c.getCrpClusterOfActivity().getCrpProgram().isActive()
            && c.getCrpClusterOfActivity().getCrpProgram().getCrp().getId().longValue() == this.getCrpID().longValue())
          .collect(Collectors.toList())) {
          relations.add(crpClusterActivityLeader.getCrpClusterOfActivity().getIdentifier());
        }
        break;

      case "SL":
        for (CrpSitesLeader crpSitesLeader : user.getCrpSitesLeaders().stream()
          .filter(
            c -> c.isActive() && c.getCrpsSiteIntegration().getCrp().getId().longValue() == this.getCrpID().longValue())
          .collect(Collectors.toList())) {
          relations.add(crpSitesLeader.getCrpsSiteIntegration().getLocElement().getIsoAlpha2());
        }
        break;

    }
    if (!relations.isEmpty()) {
      return relations.toString();
    }

    return "";
  }

  public List<Role> getRolesCrp() {
    return rolesCrp;
  }

  public User getUser() {
    return user;
  }

  public List<UserRole> getUsers() {
    return users;
  }

  public List<User> getUsersByRole(long roleID) {
    Set<User> usersRolesSet = new HashSet<>();
    List<User> usersRoles = new ArrayList<>();

    List<UserRole> userRolesBD = userRoleManager.getUserRolesByRoleId(roleID);

    for (UserRole userRole : userRolesBD) {

      if (userRole.getUser().isActive()) {
        if (this.users.contains(userRole)) {

          if (this.hasRelations(userRole.getRole().getAcronym()) != null) {
            if (!this.getRelations(userRole.getUser().getId().longValue(), userRole.getRole().getId().longValue())
              .isEmpty()) {
              usersRolesSet.add(userRole.getUser());
            }
          } else {
            usersRolesSet.add(userRole.getUser());
          }


        }
      }

    }

    usersRoles.addAll(usersRolesSet);
    return usersRoles;
  }

  public String hasRelations(String acronym) {
    String ret = null;
    switch (acronym.toUpperCase()) {
      case "CRP-ADMIN":
      case "G":
      case "FM":
      case "PMU":
      case "SUPERADMIN":
      case "DM":
        ret = null;
        break;
      case "PL":
      case "PC":
        ret = "Projects";
        break;

      case "ML":
        ret = "Institutions";
        break;

      case "CP":
        ret = "Centers";
        break;
      case "FPM":
      case "FPL":
        ret = "Flagships";
        break;

      case "RPM":
      case "RPL":
        ret = "Regions";
        break;

      case "CL":
        ret = "Cluster of Activities";
        break;
      case "SL":
        ret = "Site Integrations";
        break;
    }
    return ret;

  }

  public boolean isCGIARUser() {
    return isCGIARUser;
  }

  /**
   * @param userAssigned is the user been assigned
   * @param role is the role(Guest)
   */
  private void notifyRoleAssigned(User userAssigned) {
    // Send email to the new user and the P&R notification email.
    // TO
    String toEmail = userAssigned.getEmail();
    String ccEmail = null;
    String bbcEmails = this.config.getEmailNotification();


    // get CRPAdmin contacts
    String crpAdmins = "";
    GlobalUnit globalUnit = null;
    if (selectedGlobalUnitAcronym != null) {
      globalUnit = globalUnitManager.findGlobalUnitByAcronym(selectedGlobalUnitAcronym);
    }

    /*
     * long adminRol = globalUnit.getRoles().stream().filter(r -> r.getAcronym().equals("CRP-Admin"))
     * .collect(Collectors.toList()).get(0).getId();
     * Role roleAdmin = roleManager.getRoleById(adminRol);
     * List<UserRole> userRoles = roleAdmin.getUserRoles().stream()
     * .filter(ur -> ur.getUser() != null && ur.getUser().isActive()).collect(Collectors.toList());
     * for (UserRole userRole : userRoles) {
     * if (crpAdmins == null || crpAdmins.isEmpty()) {
     * crpAdmins += userRole.getUser().getComposedCompleteName() + " (" + userRole.getUser().getEmail() + ")";
     * } else {
     * crpAdmins += ", " + userRole.getUser().getComposedCompleteName() + " (" + userRole.getUser().getEmail() + ")";
     * }
     * if (userRole.getUser().getEmail() != null) {
     * if (ccEmail == null || ccEmail.isEmpty()) {
     * ccEmail = userRole.getUser().getEmail();
     * } else {
     * ccEmail += "; " + userRole.getUser().getEmail();
     * }
     * }
     * }
     */

    // New method for CC emails
    if (this.getCurrentUser() != null && this.getCurrentUser().getEmail() != null) {
      ccEmail = this.getCurrentUser().getEmail();
    }

    // Subject
    String subject = this.getText("email.guest.assigned.subject", new String[] {globalUnit.getAcronym()});

    // Message
    userAssigned = userManager.getUser(userAssigned.getId());
    StringBuilder message = new StringBuilder();
    // Building the Email message:
    message.append(this.getText("email.dear", new String[] {userAssigned.getFirstName()}));
    message.append(this.getText("email.guest.assigned", new String[] {globalUnit.getAcronym()}));
    message.append(this.getText("email.support.noCrpAdmins"));
    message.append(this.getText("email.getStarted"));
    message.append(this.getText("email.bye"));
    if (this.validateEmailNotification(globalUnit)) {
      sendMailS.send(toEmail, ccEmail, bbcEmails, subject, message.toString(), null, null, null, true);
    }

  }

  @Override
  public void prepare() throws Exception {
    selectedGlobalUnitAcronym = this.getCurrentCrp().getAcronym();
    phase = phaseManager.findCycle(this.getCurrentCycle(), this.getCurrentCycleYear(),
      this.getActualPhase().getUpkeep(), this.getCrpID());
    phasesProjects = new ArrayList<Project>();
    for (ProjectPhase projectPhase : phase.getProjectPhases()) {
      phasesProjects.add(projectManager.getProjectById(projectPhase.getProject().getId()));
    }
    users = new ArrayList<UserRole>();

    this.rolesCrp = roleManager
      .findAll().stream().filter(c -> !c.getUserRoles().isEmpty()
        && c.getCrp().getId().longValue() == this.getCrpID().longValue() && c.getId().longValue() != 17)
      .collect(Collectors.toList());
    rolesCrp.sort((p1, p2) -> p1.getOrder().compareTo(p2.getOrder()));
    for (Role role : rolesCrp) {
      if (role.getAcronym().equals("PL") || role.getAcronym().equals("PC")) {

        for (UserRole userRole : role.getUserRoles()) {
          User user = userRole.getUser();


          for (ProjectPartnerPerson projectPartnerPerson : user.getProjectPartnerPersons().stream()
            .filter(
              c -> c.getContactType().equals(role.getAcronym()) && c.getProjectPartner().isActive() && c.isActive())
            .collect(Collectors.toList())) {
            if (phasesProjects.contains(projectPartnerPerson.getProjectPartner().getProject())) {
              if (projectPartnerPerson.getProjectPartner().getProject()
                .getProjecInfoPhase(this.getActualPhase()) != null
                && projectPartnerPerson.getProjectPartner().getProject().getProjecInfoPhase(this.getActualPhase())
                  .getStatus() == Integer.parseInt(ProjectStatusEnum.Ongoing.getStatusId())
                || projectPartnerPerson.getProjectPartner().getProject()
                  .getProjecInfoPhase(this.getActualPhase()) != null
                  && projectPartnerPerson.getProjectPartner().getProject().getProjecInfoPhase(this.getActualPhase())
                    .getStatus() == Integer.parseInt(ProjectStatusEnum.Extended.getStatusId())) {
                users.add(userRole);
              }

            }
          }
        }
      }


      else {

        if (role.getAcronym().equals("ML")) {

          for (UserRole userRole : role.getUserRoles()) {
            User user = userRole.getUser();


            for (LiaisonUser liaisonUser : user.getLiasonsUsers().stream().filter(c -> c.isActive())
              .collect(Collectors.toList())) {
              for (ProjectInfo project : liaisonUser.getProjects().stream()
                .filter(c -> c.isActive() && c.getPhase().equals(this.getActualPhase())).collect(Collectors.toList())) {
                if (phasesProjects.contains(project.getProject())) {
                  if (project.getStatus() == Integer.parseInt(ProjectStatusEnum.Ongoing.getStatusId())
                    || project.getStatus() == Integer.parseInt(ProjectStatusEnum.Extended.getStatusId())) {
                    users.add(userRole);
                  }

                }
              }

            }
          }
        } else {
          for (UserRole userRole : role.getUserRoles()) {
            users.add(userRole);
          }
        }

      }

    }
    Set<UserRole> userSet = new HashSet<>();

    userSet.addAll(users);
    users.clear();
    users.addAll(userSet);
  }

  @Override
  public String save() {
    int error = 0;

    GlobalUnit globalUnit = null;

    if (this.canAcessCrpAdmin()) {

      // Check if the email is valid
      if (user.getEmail() != null && this.isValidEmail(user.getEmail()) && user.getEmail().length() > 0) {

        boolean emailExists = false;
        // We need to validate that the email does not exist yet into our database.
        emailExists = userManager.getUserByEmail(user.getEmail()) == null ? false : true;

        // If email doesn't exists.
        if (!emailExists) {

          if (selectedGlobalUnitAcronym != null) {
            globalUnit = globalUnitManager.findGlobalUnitByAcronym(selectedGlobalUnitAcronym);
            User newUser = new User();
            // newUser.setFirstName(user.getFirstName());
            // newUser.setLastName(user.getLastName());
            newUser.setEmail(user.getEmail());
            newUser.setModificationJustification("User created in MARLO " + this.getActionName().replace("/", "-"));
            newUser.setAutoSave(true);
            newUser.setId(null);
            newUser.setActive(true);

            // Get the user if it is a CGIAR email.
            LDAPUser LDAPUser = this.getOutlookUser(newUser.getEmail());

            String password = this.getText("email.outlookPassword");
            if (LDAPUser != null) {
              // CGIAR user
              isCGIARUser = true;
              newUser.setFirstName(LDAPUser.getFirstName());
              newUser.setLastName(LDAPUser.getLastName());
              newUser.setUsername(LDAPUser.getLogin().toLowerCase());
              newUser.setCgiarUser(true);
              newUser = userManager.saveUser(newUser);
              message = this.getText("saving.saved.guestRole");
              this.addActionMessage("message:" + this.getText("saving.saved.guestRole"));
            } else {
              // Non CGIAR user
              isCGIARUser = false;
              if (user.getFirstName() != null && user.getLastName() != null) {
                isCGIARUser = false;
                newUser.setFirstName(user.getFirstName());
                newUser.setLastName(user.getLastName());
                newUser.setCgiarUser(false);
                password = RandomStringUtils.randomNumeric(6);
                newUser.setPassword(password);
                newUser.setModificationJustification("User created in MARLO " + this.getActionName().replace("/", "-"));
                newUser = userManager.saveUser(newUser);
                message = this.getText("saving.saved.guestRole");
                this.addActionMessage("message:" + this.getText("saving.saved.guestRole"));
              }
            }

            try {

              this.sendMailNewUser(newUser, globalUnit, password);
              this.notifyRoleAssigned(newUser);

            } catch (NoSuchAlgorithmException e) {
              e.printStackTrace();
              LOG.error(e.getMessage());
            }

            // Add Crp Users
            CrpUser crpUser = new CrpUser();
            crpUser.setUser(newUser);
            crpUser.setCrp(globalUnit);
            crpUser = crpUserManager.saveCrpUser(crpUser);

            // Add guest user role
            UserRole userRole = new UserRole();
            Role guestRole = globalUnit.getRoles().stream().filter(r -> r.getAcronym().equals("G"))
              .collect(Collectors.toList()).get(0);
            userRole.setRole(guestRole);
            userRole.setUser(newUser);
            userRole = userRoleManager.saveUserRole(userRole);
            message = this.getText("saving.saved.guestRole");
            this.addActionMessage("message:" + this.getText("saving.saved.guestRole"));

          } else {
            this.addActionMessage("message:" + "login.error.selectCrp");
            LOG.warn(this.getText("login.error.selectCrp"));
            error++;
          }

        } else {
          // if email exist
          isCGIARUser = true;
          User existingUser = userManager.getUserByEmail(user.getEmail());
          List<CrpUser> crpUserList = new ArrayList<CrpUser>();
          List<UserRole> userRoleList = new ArrayList<UserRole>();
          List<Role> roleListCRP = new ArrayList<Role>();
          crpUserList = null;
          userRoleList = null;
          if (selectedGlobalUnitAcronym != null) {
            final GlobalUnit globalUnitE = globalUnitManager.findGlobalUnitByAcronym(selectedGlobalUnitAcronym);

            // Get user permisions for selected CRP
            crpUserList = crpUserManager.findAll().stream()
              .filter(
                u -> u.getUser().getId().equals(existingUser.getId()) && u.getCrp().getId().equals(globalUnitE.getId()))
              .collect(Collectors.toList());

            if (crpUserList == null || crpUserList.isEmpty()) {
              // if the user doens't have permission for selected CRP

              // Add Crp Users
              CrpUser crpUser = new CrpUser();
              crpUser.setUser(existingUser);
              crpUser.setCrp(globalUnitE);

              User user = new User();
              user = userManager.getUser(existingUser.getId());

              if (user.isActive() == false) {
                user.setActive(true);
                userManager.saveUser(user);
              }
              crpUser = crpUserManager.saveCrpUser(crpUser);
            }

            Role guestRole = globalUnitE.getRoles().stream().filter(r -> r.getAcronym().equals("G"))
              .collect(Collectors.toList()).get(0);

            // get user roles
            userRoleList = userRoleManager.findAll().stream()
              .filter(u -> u.getUser().getId().equals(existingUser.getId())).collect(Collectors.toList());

            // get roles for selected CRP
            roleListCRP = roleManager.findAll().stream().filter(r -> r.getCrp().getId().equals(globalUnitE.getId()))
              .collect(Collectors.toList());
            boolean containsRol = false;

            if (userRoleList != null) {
              // Search for Rol for the user in the selected CRP
              for (UserRole userRol : userRoleList) {
                if (roleListCRP.contains(userRol.getRole())) {
                  containsRol = true;
                }
              }

              if (containsRol == false) {
                // If no exist any role for this user in the selected CRP

                // Add guest user role
                UserRole userRole = new UserRole();
                userRole.setRole(guestRole);
                userRole.setUser(existingUser);
                userRole = userRoleManager.saveUserRole(userRole);
                message = this.getText("saving.saved.guestRole");
                this.addActionMessage("message:" + this.getText("saving.saved.guestRole"));

                // Send email message for Guest rol assignation in selected CRP
                try {
                  this.notifyRoleAssigned(existingUser);
                } catch (Exception e) {
                  e.printStackTrace();
                  LOG.error(e.getMessage());
                }

              } else {
                // If already exist a role for this user in the selected CRP
                LOG.warn(this.getText("manageUsers.email.roleExisting"));
                message = this.getText("manageUsers.email.roleExisting");
                // this.addActionMessage("message:" + this.getText("manageUsers.email.roleExisting"));
                this.getInvalidFields().put("input-user.email", this.getText("manageUsers.email.roleExisting"));
                error++;
              }
            }
          }
        }

      } else {
        LOG.warn(this.getText("manageUsers.email.notValid"));
        message = this.getText("manageUsers.email.notValid");
        this.addActionMessage("message:" + this.getText("manageUsers.email.notValid"));
        error++;
      }
      // check if there is a url to redirect
      if (this.getUrl() == null || this.getUrl().isEmpty()) {
        // check if there are missing field
        Collection<String> messages = this.getActionMessages();
        if (!this.getInvalidFields().isEmpty()) {
          this.setActionMessages(null);
          // this.addActionMessage(Map.toString(this.getInvalidFields().toArray()));
          List<String> keys = new ArrayList<String>(this.getInvalidFields().keySet());
          for (String key : keys) {
            this.addActionMessage(key + ": " + this.getInvalidFields().get(key));
          }

        } else {
          // this.addActionMessage("message:" + this.getText("saving.saved.guestRole"));
        }
        if (error != 0) {
          return INPUT;
        } else {
          return SUCCESS;
        }
      } else {
        // No messages to next page

        this.addActionMessage("");
        this.setActionMessages(null);
        // redirect the url select by user
        return REDIRECT;
      }

    } else {
      // no permissions to edit
      return NOT_AUTHORIZED;
    }
  }


  public void sendMailNewUser(User user, GlobalUnit loggedCrp, String password) throws NoSuchAlgorithmException {
    String toEmail = user.getEmail();
    String ccEmail = "";
    String bbcEmails = this.config.getEmailNotification();
    String subject = this.getText("email.newUser.subject", new String[] {user.getFirstName()});

    // get CRPAdmin contacts
    String crpAdmins = "";
    GlobalUnit globalUnit = null;
    if (selectedGlobalUnitAcronym != null) {
      globalUnit = globalUnitManager.findGlobalUnitByAcronym(selectedGlobalUnitAcronym);
    }

    long adminRol = globalUnit.getRoles().stream().filter(r -> r.getAcronym().equals("CRP-Admin"))
      .collect(Collectors.toList()).get(0).getId();

    // long adminRol = Long.parseLong((String) this.getSession().get(APConstants.CRP_ADMIN_ROLE));
    Role roleAdmin = roleManager.getRoleById(adminRol);
    List<UserRole> userRoles = roleAdmin.getUserRoles().stream()
      .filter(ur -> ur.getUser() != null && ur.getUser().isActive()).collect(Collectors.toList());
    for (UserRole userRole : userRoles) {
      if (crpAdmins.isEmpty()) {
        crpAdmins += userRole.getUser().getComposedCompleteName() + " (" + userRole.getUser().getEmail() + ")";
      } else {
        crpAdmins += ", " + userRole.getUser().getComposedCompleteName() + " (" + userRole.getUser().getEmail() + ")";
      }
    }

    // Building the Email message:
    StringBuilder message = new StringBuilder();
    message.append(this.getText("email.dear", new String[] {user.getFirstName()}));
    String crp = loggedCrp.getAcronym() != null && !loggedCrp.getAcronym().isEmpty() ? loggedCrp.getAcronym()
      : loggedCrp.getName();
    /*
     * message.append(
     * this.getText("email.newUser.part2", new String[] {this.getText("global.sClusterOfActivities").toLowerCase(),
     * config.getBaseUrl(), crp, user.getEmail(), password, this.getText("email.support", new String[] {crpAdmins})}));
     */
    message.append(this.getText("email.newUser.part1", new String[] {this.getText("email.newUser.listRoles"),
      config.getBaseUrl(), user.getEmail(), password, this.getText("email.support.noCrpAdmins")}));
    message.append(this.getText("email.bye"));

    // Send pdf
    String contentType = "application/pdf";
    String fileName;
    if (this.isAiccra()) {
      fileName = APConstants.AICCRA_PDF_MANUAL_NAME;
    } else {
      fileName = APConstants.MARLO_PDF_MANUAL_NAME;
    }
    byte[] buffer = null;
    InputStream inputStream = null;

    try {
      inputStream = this.getClass().getResourceAsStream("/manual/" + fileName);
      buffer = readFully(inputStream);
    } catch (FileNotFoundException e) {
      LOG.error(e.getMessage());
      e.printStackTrace();
    } catch (IOException e) {
      LOG.error(e.getMessage());
      e.printStackTrace();
    } finally {
      if (inputStream != null) {
        try {
          inputStream.close();
        } catch (IOException e) {
          LOG.error(e.getMessage());
          e.printStackTrace();
        }
      }
    }
    if (this.validateEmailNotification(loggedCrp)) {
      if (buffer != null && fileName != null && contentType != null) {

        sendMailS.send(toEmail, ccEmail, bbcEmails, subject, message.toString(), buffer, contentType, fileName, true);
      } else {
        sendMailS.send(toEmail, ccEmail, bbcEmails, subject, message.toString(), null, null, null, true);
      }
    }
  }

  public void setCGIARUser(boolean isCGIARUser) {
    this.isCGIARUser = isCGIARUser;
  }


  public void setEmailSend(String emailSend) {
    this.emailSend = emailSend;
  }


  public void setRolesCrp(List<Role> rolesCrp) {
    this.rolesCrp = rolesCrp;
  }


  public void setUser(User user) {
    this.user = user;
  }


  public void setUsers(List<UserRole> users) {
    this.users = users;
  }

  @Override
  public void validate() {
    if (save) {
      validator.validate(this, user, selectedGlobalUnitAcronym, isCGIARUser, true);
    }
  }


  private boolean validateEmailNotification(GlobalUnit globalUnit) {

    Boolean crpNotification = globalUnit.getCustomParameters().stream()
      .filter(c -> c.getParameter().getKey().equalsIgnoreCase(APConstants.CRP_EMAIL_NOTIFICATIONS))
      .allMatch(t -> (t.getValue() == null) ? true : t.getValue().equalsIgnoreCase("true"));
    return crpNotification;
  }


}
