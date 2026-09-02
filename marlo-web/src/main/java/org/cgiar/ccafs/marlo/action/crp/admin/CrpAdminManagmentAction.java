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
import org.cgiar.ccafs.marlo.data.manager.CrpProgramLeaderManager;
import org.cgiar.ccafs.marlo.data.manager.CrpProgramManager;
import org.cgiar.ccafs.marlo.data.manager.CrpUserManager;
import org.cgiar.ccafs.marlo.data.manager.CustomParameterManager;
import org.cgiar.ccafs.marlo.data.manager.GlobalUnitManager;
import org.cgiar.ccafs.marlo.data.manager.LiaisonInstitutionManager;
import org.cgiar.ccafs.marlo.data.manager.LiaisonUserManager;
import org.cgiar.ccafs.marlo.data.manager.ParameterManager;
import org.cgiar.ccafs.marlo.data.manager.RoleManager;
import org.cgiar.ccafs.marlo.data.manager.UserManager;
import org.cgiar.ccafs.marlo.data.manager.UserRoleManager;
import org.cgiar.ccafs.marlo.data.model.CrpClusterActivityLeader;
import org.cgiar.ccafs.marlo.data.model.CrpClusterOfActivity;
import org.cgiar.ccafs.marlo.data.model.CrpProgram;
import org.cgiar.ccafs.marlo.data.model.CrpProgramLeader;
import org.cgiar.ccafs.marlo.data.model.CrpUser;
import org.cgiar.ccafs.marlo.data.model.CustomParameter;
import org.cgiar.ccafs.marlo.data.model.GlobalUnit;
import org.cgiar.ccafs.marlo.data.model.LiaisonInstitution;
import org.cgiar.ccafs.marlo.data.model.LiaisonUser;
import org.cgiar.ccafs.marlo.data.model.Parameter;
import org.cgiar.ccafs.marlo.data.model.ProgramType;
import org.cgiar.ccafs.marlo.data.model.ProjectStatusEnum;
import org.cgiar.ccafs.marlo.data.model.Role;
import org.cgiar.ccafs.marlo.data.model.User;
import org.cgiar.ccafs.marlo.data.model.UserRole;
import org.cgiar.ccafs.marlo.security.Permission;
import org.cgiar.ccafs.marlo.utils.APConfig;
import org.cgiar.ccafs.marlo.utils.InvalidFieldsMessages;
import org.cgiar.ccafs.marlo.utils.SendMailS;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import javax.inject.Inject;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.RandomStringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This action is part of the CRP admin backend.
 * 
 * @author Christian Garcia
 */
public class CrpAdminManagmentAction extends BaseAction {

  private static final Logger LOG = LoggerFactory.getLogger(CrpAdminManagmentAction.class);

  private static final long serialVersionUID = 3355662668874414548L;

  /** Name and acronym used when the Program Management Unit liaison institution has to be created. */
  private static final String PMU_LIAISON_INSTITUTION_NAME = "PMU";


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

  // Managers
  private RoleManager roleManager;

  private UserRoleManager userRoleManager;

  private CrpProgramManager crpProgramManager;
  // GlobalUnit Manager
  private GlobalUnitManager crpManager;
  private CustomParameterManager crpParameterManager;
  private ParameterManager parameterManager;
  private CrpUserManager crpUserManager;
  // Variables
  private GlobalUnit loggedCrp;

  private Role rolePmu;
  private long pmuRol;
  private long cuId;
  private List<CrpProgram> flagshipsPrograms;
  private List<CrpProgram> regionsPrograms;
  private List<CustomParameter> parameters;


  private CrpProgramLeaderManager crpProgramLeaderManager;


  private LiaisonUserManager liaisonUserManager;

  private LiaisonInstitutionManager liaisonInstitutionManager;
  private UserManager userManager;
  private Role fplRole;
  private Role fpmRole;
  // Util
  private SendMailS sendMail;


  @Inject
  public CrpAdminManagmentAction(APConfig config, RoleManager roleManager, UserRoleManager userRoleManager,
    CrpProgramManager crpProgramManager, GlobalUnitManager crpManager, CustomParameterManager crpParameterManager,
    CrpProgramLeaderManager crpProgramLeaderManager, UserManager userManager, SendMailS sendMail,
    LiaisonUserManager liaisonUserManager, LiaisonInstitutionManager liaisonInstitutionManager,
    CrpUserManager crpUserManager, ParameterManager parameterManager) {
    super(config);
    this.roleManager = roleManager;
    this.userRoleManager = userRoleManager;
    this.crpManager = crpManager;
    this.crpProgramManager = crpProgramManager;
    this.crpParameterManager = crpParameterManager;
    this.userManager = userManager;
    this.parameterManager = parameterManager;
    this.crpProgramLeaderManager = crpProgramLeaderManager;
    this.sendMail = sendMail;
    this.liaisonUserManager = liaisonUserManager;
    this.liaisonInstitutionManager = liaisonInstitutionManager;
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
      crpUserManager.saveCrpUser(crpUser);
    }
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

  public List<CrpProgram> getFlagshipsPrograms() {
    return flagshipsPrograms;
  }

  public Role getFplRole() {
    return fplRole;
  }

  public Role getFpmRole() {
    return fpmRole;
  }

  public GlobalUnit getLoggedCrp() {
    return loggedCrp;
  }


  public long getPmuRol() {
    return pmuRol;
  }


  public List<CrpProgram> getRegionsPrograms() {
    return regionsPrograms;
  }


  public Role getRolePmu() {
    return rolePmu;
  }


  /**
   * This method will validate if the user is deactivated. If so, it will send an email indicating the credentials to
   * access.
   * 
   * @param user is a User object that could be the leader.
   */
  private void notifyNewUserCreated(User user) {
    user = userManager.getUser(user.getId());

    if (!user.isActive()) {
      String toEmail = user.getEmail();
      String ccEmail = null;
      String bbcEmails = this.config.getEmailNotification();
      String subject = this.getText("email.newUser.subject", new String[] {user.getFirstName()});
      // Setting the password
      String password = this.getText("email.outlookPassword");
      if (!user.isCgiarUser()) {
        // Generating a random password.
        password = RandomStringUtils.randomNumeric(6);
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
          crpAdmins += userRole.getUser().getComposedCompleteName() + " (" + userRole.getUser().getEmail() + ")";
        } else {
          crpAdmins += ", " + userRole.getUser().getComposedCompleteName() + " (" + userRole.getUser().getEmail() + ")";
        }
      }

      message.append(this.getText("email.newUser.part1", new String[] {this.getText("email.newUser.listRoles"),
        config.getBaseUrl(), user.getEmail(), password, this.getText("email.support.noCrpAdmins")}));
      message.append(this.getText("email.bye"));

      // Saving the new user configuration.
      /** Leaving this for now as there is some strangeness in regards to the active/inactive flag for users. **/
      user.setActive(true);
      user = userManager.saveUser(user);

      Map<String, Object> mapUser = new HashMap<>();
      mapUser.put("user", user);
      mapUser.put("password", password);
      this.getUsersToActive().add(mapUser);
      // Send UserManual.pdf
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
        // The welcome email is still sent, only without the manual attached, so this is the only trace of it.
        LOG.error("The user manual {} was not found, so the welcome email of {} goes out without it", fileName,
          user.getEmail(), e);
      } catch (IOException e) {
        LOG.error("The user manual {} could not be read, so the welcome email of {} goes out without it", fileName,
          user.getEmail(), e);
      } finally {
        if (inputStream != null) {
          try {
            inputStream.close();
          } catch (IOException e) {
            LOG.warn("Could not close the stream of the user manual {}", fileName, e);
          }
        }
      }

      if (this.validateEmailNotification()) {
        if (buffer != null && fileName != null && contentType != null) {
          sendMail.send(toEmail, ccEmail, bbcEmails, subject, message.toString(), buffer, contentType, fileName, true);
        } else {
          sendMail.send(toEmail, ccEmail, bbcEmails, subject, message.toString(), null, null, null, true);
        }
      }
    }

  }


  /**
   * This method notify the user that is been assigned as Program Leader for an specific Flagship
   * 
   * @param user userAssigned is the user been assigned
   * @param role is the role(Program Leader)
   * @param crpProgram is the Flagship where is assigned
   */
  private void notifyRoleFlagshipAssigned(User userAssigned, Role role, CrpProgram crpProgram) {
    // Email send to the user assigned
    String toEmail = userAssigned.getEmail();
    // CC will be the user who is making the modification.
    String ccEmail = "";
    if (this.getCurrentUser() != null) {
      ccEmail = this.getCurrentUser().getEmail();
    }

    crpProgram = this.crpProgramManager.getCrpProgramById(crpProgram.getId());
    // CC will be also the others FL already assigned to the Flagship
    for (CrpProgramLeader crpProgramLeader : crpProgram.getCrpProgramLeaders().stream()
      .filter(cpl -> cpl.getUser().isActive() && cpl.isActive()).collect(Collectors.toList())) {
      if (ccEmail.isEmpty()) {
        ccEmail += crpProgramLeader.getUser().getEmail();
      } else {
        ccEmail += ", " + crpProgramLeader.getUser().getEmail();
      }
    }

    // CC will be also the CRP Admins
    String crpAdmins = "";
    String crpAdminsEmail = "";
    long adminRol = Long.parseLong((String) this.getSession().get(APConstants.CRP_ADMIN_ROLE));
    Role roleAdmin = roleManager.getRoleById(adminRol);
    List<UserRole> userRoles = roleAdmin.getUserRoles().stream()
      .filter(ur -> ur.getUser() != null && ur.getUser().isActive()).collect(Collectors.toList());
    for (UserRole userRole : userRoles) {
      if (crpAdmins.isEmpty()) {
        crpAdmins += userRole.getUser().getComposedCompleteName() + " (" + userRole.getUser().getEmail() + ")";
        crpAdminsEmail += userRole.getUser().getEmail();

      } else {
        crpAdmins += ", " + userRole.getUser().getComposedCompleteName() + " (" + userRole.getUser().getEmail() + ")";
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

    // BBC will be our gmail notification email.
    String bbcEmails = this.config.getEmailNotification();
    String crp = loggedCrp.getAcronym() != null && !loggedCrp.getAcronym().isEmpty() ? loggedCrp.getAcronym()
      : loggedCrp.getName();

    String subject = this.getText("email.flagship.assigned.subject", new String[] {crpProgram.getAcronym(), crp});

    crpProgram = crpProgramManager.getCrpProgramById(crpProgram.getId());

    userAssigned = userManager.getUser(userAssigned.getId());
    StringBuilder message = new StringBuilder();
    // Building the Email message:
    message.append(this.getText("email.dear", new String[] {userAssigned.getFirstName()}));
    message.append(this.getText("email.flagship.assigned", new String[] {crpProgram.getAcronym(), crpProgram.getName(),
      crp, this.getText("email.flagship.responsabilities")}));

    message.append(this.getText("email.support.noCrpAdmins"));
    message.append(this.getText("email.getStarted"));
    message.append(this.getText("email.bye"));

    if (this.validateEmailNotification()) {
      if (role.equals(fplRole)) {
        sendMail.send(toEmail, ccEmail, bbcEmails, subject, message.toString(), null, null, null, true);
      } else {
        sendMail.send(toEmail, ccEmail, bbcEmails, subject, message.toString(), null, null, null, true);
      }
    }
  }

  /**
   * This method notify the user that is been assigned as Program Leader for an specific Flagship
   * 
   * @param user userAssigned is the user been assigned
   * @param role is the role(Program Leader)
   * @param crpProgram is the Flagship where is assigned
   */
  private void notifyRoleFlagshipManagerAssigned(User userAssigned, Role role, CrpProgram crpProgram) {
    // Email send to the user assigned
    String toEmail = userAssigned.getEmail();
    // CC will be the user who is making the modification.
    String ccEmail = "";
    if (this.getCurrentUser() != null) {
      ccEmail = this.getCurrentUser().getEmail();
    }

    crpProgram = this.crpProgramManager.getCrpProgramById(crpProgram.getId());
    // CC will be also the others FL already assigned to the Flagship
    for (CrpProgramLeader crpProgramLeader : crpProgram.getCrpProgramLeaders().stream()
      .filter(cpl -> cpl.getUser().isActive() && cpl.isActive()).collect(Collectors.toList())) {
      if (ccEmail.isEmpty()) {
        ccEmail += crpProgramLeader.getUser().getEmail();
      } else {
        ccEmail += ", " + crpProgramLeader.getUser().getEmail();
      }
    }

    // CC will be also the CRP Admins
    String crpAdmins = "";
    String crpAdminsEmail = "";
    long adminRol = Long.parseLong((String) this.getSession().get(APConstants.CRP_ADMIN_ROLE));
    Role roleAdmin = roleManager.getRoleById(adminRol);
    List<UserRole> userRoles = roleAdmin.getUserRoles().stream()
      .filter(ur -> ur.getUser() != null && ur.getUser().isActive()).collect(Collectors.toList());
    for (UserRole userRole : userRoles) {
      if (crpAdmins.isEmpty()) {
        crpAdmins += userRole.getUser().getComposedCompleteName() + " (" + userRole.getUser().getEmail() + ")";
        crpAdminsEmail += userRole.getUser().getEmail();
      } else {
        crpAdmins += ", " + userRole.getUser().getComposedCompleteName() + " (" + userRole.getUser().getEmail() + ")";
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

    // CC will be also other Cluster Leaders
    for (CrpClusterOfActivity crpClusterOfActivity : crpProgram.getCrpClusterOfActivities().stream()
      .filter(cl -> cl.isActive() && cl.getPhase() != null && cl.getPhase().equals(this.getActualPhase()))
      .collect(Collectors.toList())) {
      for (CrpClusterActivityLeader crpClusterActivityLeader : crpClusterOfActivity.getCrpClusterActivityLeaders()
        .stream()
        .filter(cl -> cl.isActive() && cl.getUser().isActive()
          && cl.getCrpClusterOfActivity().getPhase().getId().equals(this.getActualPhase().getId()))
        .collect(Collectors.toList())) {

        if (ccEmail.isEmpty()) {
          ccEmail += crpClusterActivityLeader.getUser().getEmail();
        } else {
          ccEmail += ", " + crpClusterActivityLeader.getUser().getEmail();
        }

      }
    }

    // BBC will be our gmail notification email.
    String bbcEmails = this.config.getEmailNotification();
    String crp = loggedCrp.getAcronym() != null && !loggedCrp.getAcronym().isEmpty() ? loggedCrp.getAcronym()
      : loggedCrp.getName();

    String subject =
      this.getText("email.flagshipmanager.assigned.subject", new String[] {crpProgram.getAcronym(), crp});

    crpProgram = crpProgramManager.getCrpProgramById(crpProgram.getId());

    userAssigned = userManager.getUser(userAssigned.getId());
    StringBuilder message = new StringBuilder();
    // Building the Email message:
    message.append(this.getText("email.dear", new String[] {userAssigned.getFirstName()}));
    message.append(
      this.getText("email.flagshipmanager.assigned", new String[] {crpProgram.getAcronym(), crpProgram.getName(), crp,
        this.getText("email.flagshipmanager.responsabilities"), this.getText("email.flagshipmanager.note")}));

    message.append(this.getText("email.support.noCrpAdmins"));
    message.append(this.getText("email.getStarted"));
    message.append(this.getText("email.bye"));
    if (this.validateEmailNotification()) {
      if (role.equals(fplRole)) {
        sendMail.send(toEmail, ccEmail, bbcEmails, subject, message.toString(), null, null, null, true);
      } else {
        sendMail.send(toEmail, ccEmail, bbcEmails, subject, message.toString(), null, null, null, true);
      }
    }
  }

  private void notifyRoleFlagshipManagerUnassigned(User userRemoved, Role role, CrpProgram crpProgram) {
    // Email send to the user assigned
    String toEmail = userRemoved.getEmail();
    // CC will be the user who is making the modification.
    String ccEmail = "";
    if (this.getCurrentUser() != null) {
      ccEmail = this.getCurrentUser().getEmail();
    }
    // CC will be also the others FL already assigned to the Flagship
    crpProgram = this.crpProgramManager.getCrpProgramById(crpProgram.getId());
    for (CrpProgramLeader crpProgramLeader : crpProgram.getCrpProgramLeaders().stream()
      .filter(cpl -> cpl.getUser().isActive() && cpl.isActive()).collect(Collectors.toList())) {
      if (ccEmail.isEmpty()) {
        ccEmail += crpProgramLeader.getUser().getEmail();
      } else {
        ccEmail += ", " + crpProgramLeader.getUser().getEmail();
      }
    }

    // get CRPAdmin contacts
    String crpAdmins = "";
    String crpAdminsEmail = "";
    long adminRol = Long.parseLong((String) this.getSession().get(APConstants.CRP_ADMIN_ROLE));
    Role roleAdmin = roleManager.getRoleById(adminRol);
    List<UserRole> userRoles = roleAdmin.getUserRoles().stream()
      .filter(ur -> ur.getUser() != null && ur.getUser().isActive()).collect(Collectors.toList());
    for (UserRole userRole : userRoles) {
      if (crpAdmins.isEmpty()) {
        crpAdmins += userRole.getUser().getComposedCompleteName() + " (" + userRole.getUser().getEmail() + ")";
        crpAdminsEmail += userRole.getUser().getEmail();
      } else {
        crpAdmins += ", " + userRole.getUser().getComposedCompleteName() + " (" + userRole.getUser().getEmail() + ")";
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

    // CC will be also other Cluster Leaders
    for (CrpClusterOfActivity crpClusterOfActivity : crpProgram.getCrpClusterOfActivities().stream()
      .filter(cl -> cl.isActive() && cl.getPhase() != null && cl.getPhase().equals(this.getActualPhase()))
      .collect(Collectors.toList())) {
      for (CrpClusterActivityLeader crpClusterActivityLeader : crpClusterOfActivity.getCrpClusterActivityLeaders()
        .stream().filter(cl -> cl.isActive()).collect(Collectors.toList())) {
        if (ccEmail.isEmpty()) {
          ccEmail += crpClusterActivityLeader.getUser().getEmail();
        } else {
          ccEmail += ", " + crpClusterActivityLeader.getUser().getEmail();
        }

      }
    }

    // BBC will be our gmail notification email.
    String bbcEmails = this.config.getEmailNotification();
    String crp = loggedCrp.getAcronym() != null && !loggedCrp.getAcronym().isEmpty() ? loggedCrp.getAcronym()
      : loggedCrp.getName();
    String subject =
      this.getText("email.flagshipmanager.unassigned.subject", new String[] {crpProgram.getAcronym(), crp});
    crpProgram = crpProgramManager.getCrpProgramById(crpProgram.getId());

    userRemoved = userManager.getUser(userRemoved.getId());
    StringBuilder message = new StringBuilder();
    // Building the Email message:
    message.append(this.getText("email.dear", new String[] {userRemoved.getFirstName()}));
    message.append(this.getText("email.flagshipmanager.unassigned",
      new String[] {crpProgram.getAcronym(), crpProgram.getName(), crp}));


    message.append(this.getText("email.support.noCrpAdmins"));
    message.append(this.getText("email.bye"));
    if (this.validateEmailNotification()) {
      if (role.equals(fplRole)) {
        sendMail.send(toEmail, ccEmail, bbcEmails, subject, message.toString(), null, null, null, true);
      } else {
        sendMail.send(toEmail, ccEmail, bbcEmails, subject, message.toString(), null, null, null, true);
      }
    }
  }


  private void notifyRoleFlagshipUnassigned(User userRemoved, Role role, CrpProgram crpProgram) {
    // Email send to the user assigned
    String toEmail = userRemoved.getEmail();
    // CC will be the user who is making the modification.
    String ccEmail = "";
    if (this.getCurrentUser() != null) {
      ccEmail = this.getCurrentUser().getEmail();
    }
    // CC will be also the others FL already assigned to the Flagship
    crpProgram = this.crpProgramManager.getCrpProgramById(crpProgram.getId());
    for (CrpProgramLeader crpProgramLeader : crpProgram.getCrpProgramLeaders().stream()
      .filter(cpl -> cpl.getUser().isActive() && cpl.isActive()).collect(Collectors.toList())) {
      if (ccEmail.isEmpty()) {
        ccEmail += crpProgramLeader.getUser().getEmail();
      } else {
        ccEmail += ", " + crpProgramLeader.getUser().getEmail();
      }

    }
    // get CRPAdmin contacts
    String crpAdmins = "";
    String crpAdminsEmail = "";
    long adminRol = Long.parseLong((String) this.getSession().get(APConstants.CRP_ADMIN_ROLE));
    Role roleAdmin = roleManager.getRoleById(adminRol);
    List<UserRole> userRoles = roleAdmin.getUserRoles().stream()
      .filter(ur -> ur.getUser() != null && ur.getUser().isActive()).collect(Collectors.toList());
    for (UserRole userRole : userRoles) {
      if (crpAdmins.isEmpty()) {
        crpAdmins += userRole.getUser().getComposedCompleteName() + " (" + userRole.getUser().getEmail() + ")";
        crpAdminsEmail += userRole.getUser().getEmail();
      } else {
        crpAdmins += ", " + userRole.getUser().getComposedCompleteName() + " (" + userRole.getUser().getEmail() + ")";
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

    // BBC will be our gmail notification email.
    String bbcEmails = this.config.getEmailNotification();
    String crp = loggedCrp.getAcronym() != null && !loggedCrp.getAcronym().isEmpty() ? loggedCrp.getAcronym()
      : loggedCrp.getName();
    String subject = this.getText("email.flagship.unassigned.subject", new String[] {crpProgram.getAcronym(), crp});

    crpProgram = crpProgramManager.getCrpProgramById(crpProgram.getId());

    userRemoved = userManager.getUser(userRemoved.getId());
    StringBuilder message = new StringBuilder();
    // Building the Email message:
    message.append(this.getText("email.dear", new String[] {userRemoved.getFirstName()}));
    message.append(this.getText("email.flagship.unassigned", new String[] {
      this.getText("programManagement.flagship.role"), crpProgram.getAcronym(), crpProgram.getName(), crp}));
    message.append(this.getText("email.support.noCrpAdmins"));
    message.append(this.getText("email.bye"));
    if (this.validateEmailNotification()) {
      if (role.equals(fplRole)) {
        sendMail.send(toEmail, ccEmail, bbcEmails, subject, message.toString(), null, null, null, true);
      } else {
        sendMail.send(toEmail, ccEmail, bbcEmails, subject, message.toString(), null, null, null, true);
      }
    }
  }

  /**
   * This method notify the user that is been assigned as Program Leader for an specific Regional Program
   * 
   * @param userAssigned is the user been assigned
   * @param role is the role(Program Management)
   */
  private void notifyRoleProgramManagementAssigned(User userAssigned, Role role) {
    // Email send to the user assigned
    String toEmail = userAssigned.getEmail();
    // get CRPAdmin contacts
    String crpAdmins = "";
    String crpAdminsEmail = "";
    long adminRol = Long.parseLong((String) this.getSession().get(APConstants.CRP_ADMIN_ROLE));
    Role roleAdmin = roleManager.getRoleById(adminRol);
    List<UserRole> userRoles = roleAdmin.getUserRoles().stream()
      .filter(ur -> ur.getUser() != null && ur.getUser().isActive()).collect(Collectors.toList());
    for (UserRole userRole : userRoles) {
      if (crpAdmins.isEmpty()) {
        crpAdmins += userRole.getUser().getComposedCompleteName() + " (" + userRole.getUser().getEmail() + ")";
        crpAdminsEmail += userRole.getUser().getEmail();
      } else {
        crpAdmins += ", " + userRole.getUser().getComposedCompleteName() + " (" + userRole.getUser().getEmail() + ")";
        crpAdminsEmail += ", " + userRole.getUser().getEmail();
      }
    }
    // CC will be the user who is making the modification.
    String ccEmail = this.getCurrentUser().getEmail();
    if (this.config.getEmail_pmu() != null) {
      // ccEmail += ", " + crpAdminsEmail;
      ccEmail += ", " + this.config.getEmail_pmu();
    }
    // BBC will be our gmail notification email.
    String bbcEmails = this.config.getEmailNotification();
    String crp = loggedCrp.getAcronym() != null && !loggedCrp.getAcronym().isEmpty() ? loggedCrp.getAcronym()
      : loggedCrp.getName();
    // Subject
    String subject = this.getText("email.programManagement.assigned.subject", new String[] {crp});


    userAssigned = userManager.getUser(userAssigned.getId());
    StringBuilder message = new StringBuilder();
    // Building the Email message:
    message.append(this.getText("email.dear", new String[] {userAssigned.getFirstName()}));
    message.append(this.getText("email.programManagement.assigned",
      new String[] {crp, this.getText("email.programManagement.responsibilities")}));
    message.append(this.getText("email.support.noCrpAdmins"));
    message.append(this.getText("email.getStarted"));
    message.append(this.getText("email.bye"));

    if (this.validateEmailNotification()) {
      sendMail.send(toEmail, ccEmail, bbcEmails, subject, message.toString(), null, null, null, true);
    }
  }

  /**
   * This method notify the user that is been assigned as Program Leader for an specific Regional Program
   * 
   * @param userAssigned is the user been assigned
   * @param role is the role(Program Management)
   */
  private void notifyRoleProgramManagementUnassigned(User userAssigned, Role role) {
    // Email send to nobody
    String toEmail = userAssigned.getEmail();
    // get CRPAdmin contacts
    String crpAdmins = "";
    String crpAdminsEmail = "";
    long adminRol = Long.parseLong((String) this.getSession().get(APConstants.CRP_ADMIN_ROLE));
    Role roleAdmin = roleManager.getRoleById(adminRol);
    List<UserRole> userRoles = roleAdmin.getUserRoles().stream()
      .filter(ur -> ur.getUser() != null && ur.getUser().isActive()).collect(Collectors.toList());
    for (UserRole userRole : userRoles) {
      if (crpAdmins.isEmpty()) {
        crpAdmins += userRole.getUser().getComposedCompleteName() + " (" + userRole.getUser().getEmail() + ")";
        crpAdminsEmail += userRole.getUser().getEmail();
      } else {
        crpAdmins += ", " + userRole.getUser().getComposedCompleteName() + " (" + userRole.getUser().getEmail() + ")";
        crpAdminsEmail += ", " + userRole.getUser().getEmail();
      }
    }
    // CC will be the user who is making the modification.
    String ccEmail = this.getCurrentUser().getEmail();
    if (this.config.getEmail_pmu() != null) {
      // ccEmail += ", " + crpAdminsEmail;
      ccEmail += ", " + this.config.getEmail_pmu();
    }
    // BBC will be our gmail notification email.
    String bbcEmails = this.config.getEmailNotification();
    String crp = loggedCrp.getAcronym() != null && !loggedCrp.getAcronym().isEmpty() ? loggedCrp.getAcronym()
      : loggedCrp.getName();
    String subject = this.getText("email.programManagement.unassigned.subject",
      new String[] {crp, this.getText("programManagement.role.acronym")});

    userAssigned = userManager.getUser(userAssigned.getId());
    StringBuilder message = new StringBuilder();
    // Building the Email message:
    message.append(this.getText("email.dear", new String[] {userAssigned.getFirstName()}));
    message.append(
      this.getText("email.programManagement.unassigned", new String[] {this.getText("programManagement.role"), crp}));
    message.append(this.getText("email.support.noCrpAdmins"));
    message.append(this.getText("email.bye"));
    if (this.validateEmailNotification()) {
      sendMail.send(toEmail, ccEmail, bbcEmails, subject, message.toString(), null, null, null, true);
    }
  }


  /**
   * Reads a numeric Global Unit parameter that Admin Management stores in the session. A non numeric value is
   * reported as a configuration problem instead of leaking a NumberFormatException to the exception page.
   */
  private long parseSessionParameter(String key, String value) {
    try {
      return Long.parseLong(value.trim());
    } catch (NumberFormatException e) {
      throw new IllegalStateException("The CRP parameter " + key + " for Admin Management is not a valid identifier: '"
        + value + "'. Fix the custom parameter for this Global Unit and re-login.", e);
    }
  }


  /**
   * Resolves the liaison institution that represents the Program Management Unit of the logged Global Unit, the one
   * the {@code crp_cu} parameter points to. The parameter is seeded per Global Unit and can end up dangling (for
   * instance when a database is cloned without its liaison_institutions rows), and every liaison user created here
   * needs a non null institution. So when the parameter does not resolve, the PMU institution is looked up and, as a
   * last resort, created the same way {@code GlobalUnitCreationManagerImpl} does it, and {@code crp_cu} is repaired.
   *
   * @return the liaison institution to use, or null when it could not be resolved nor created.
   */
  private LiaisonInstitution resolvePmuLiaisonInstitution() {
    LiaisonInstitution liaisonInstitution = liaisonInstitutionManager.getLiaisonInstitutionById(cuId);
    if (liaisonInstitution != null) {
      return liaisonInstitution;
    }

    LOG.warn("The parameter {}={} of the Global Unit {} does not point to an existing liaison institution. "
      + "Trying to resolve the Program Management Unit institution.", APConstants.CRP_CU, cuId,
      loggedCrp.getAcronym());

    liaisonInstitution = liaisonInstitutionManager.findByAcronymAndCrp(PMU_LIAISON_INSTITUTION_NAME, loggedCrp.getId());

    if (liaisonInstitution == null && loggedCrp.getLiaisonInstitutions() != null) {
      liaisonInstitution = loggedCrp.getLiaisonInstitutions().stream()
        .filter(c -> c.isActive() && c.getCrpProgram() == null).findFirst().orElse(null);
    }

    if (liaisonInstitution == null) {
      liaisonInstitution = new LiaisonInstitution();
      liaisonInstitution.setCrp(loggedCrp);
      liaisonInstitution.setName(PMU_LIAISON_INSTITUTION_NAME);
      liaisonInstitution.setAcronym(PMU_LIAISON_INSTITUTION_NAME);
      liaisonInstitution = liaisonInstitutionManager.saveLiaisonInstitution(liaisonInstitution);
      if (liaisonInstitution != null && liaisonInstitution.getId() != null) {
        LOG.warn("Created the Program Management Unit liaison institution {} for the Global Unit {}.",
          liaisonInstitution.getId(), loggedCrp.getAcronym());
      }
    }

    if (liaisonInstitution == null || liaisonInstitution.getId() == null) {
      return null;
    }

    this.repairCrpCuParameter(liaisonInstitution.getId());
    return liaisonInstitution;
  }


  /**
   * Points the {@code crp_cu} custom parameter of the logged Global Unit to the given liaison institution and
   * refreshes the session, so the next requests no longer hit the dangling value.
   */
  private void repairCrpCuParameter(Long liaisonInstitutionId) {
    String value = String.valueOf(liaisonInstitutionId);
    CustomParameter customParameter = null;
    if (loggedCrp.getCustomParameters() != null) {
      customParameter = loggedCrp.getCustomParameters().stream()
        .filter(c -> c.getParameter() != null && APConstants.CRP_CU.equals(c.getParameter().getKey()) && c.isActive())
        .findFirst().orElse(null);
    }

    if (customParameter == null) {
      Parameter parameter =
        parameterManager.getParameterByKey(APConstants.CRP_CU, loggedCrp.getGlobalUnitType().getId());
      if (parameter == null) {
        LOG.error("The parameter {} is not defined for the Global Unit type {}. The value {} could not be persisted.",
          APConstants.CRP_CU, loggedCrp.getGlobalUnitType().getId(), value);
        cuId = liaisonInstitutionId;
        this.getSession().put(APConstants.CRP_CU, value);
        return;
      }
      customParameter = new CustomParameter();
      customParameter.setCrp(loggedCrp);
      customParameter.setParameter(parameter);
    }

    customParameter.setValue(value);
    crpParameterManager.saveCustomParameter(customParameter);

    LOG.warn("Repaired the parameter {} of the Global Unit {}: {} -> {}.", APConstants.CRP_CU, loggedCrp.getAcronym(),
      cuId, value);

    cuId = liaisonInstitutionId;
    this.getSession().put(APConstants.CRP_CU, value);
  }


  @Override
  public void prepare() throws Exception {

    // Get the Users list that have the pmu role in this crp.
    loggedCrp = (GlobalUnit) this.getSession().get(APConstants.SESSION_CRP);
    loggedCrp = crpManager.getGlobalUnitById(loggedCrp.getId());

    String pmuRoleParam = (String) this.getSession().get(APConstants.CRP_PMU_ROLE);
    String cuParam = (String) this.getSession().get(APConstants.CRP_CU);
    if (pmuRoleParam == null || pmuRoleParam.trim().isEmpty() || cuParam == null || cuParam.trim().isEmpty()) {
      throw new IllegalStateException("Missing required CRP parameters in session for Admin Management: "
        + APConstants.CRP_PMU_ROLE + " and/or " + APConstants.CRP_CU
        + ". Re-login after ensuring custom parameters are configured for this Global Unit.");
    }
    pmuRol = this.parseSessionParameter(APConstants.CRP_PMU_ROLE, pmuRoleParam);
    cuId = this.parseSessionParameter(APConstants.CRP_CU, cuParam);
    rolePmu = roleManager.getRoleById(pmuRol);
    if (rolePmu != null && rolePmu.getUserRoles() != null) {
      loggedCrp.setProgramManagmenTeam(new ArrayList<UserRole>(rolePmu.getUserRoles()));
    }
    String params[] = {loggedCrp.getAcronym()};
    fplRole = roleManager.getRoleById(Long.parseLong((String) this.getSession().get(APConstants.CRP_FPL_ROLE)));
    fpmRole = roleManager.getRoleById(Long.parseLong((String) this.getSession().get(APConstants.CRP_FPM_ROLE)));
    // Get the Flagship list of this CRP


    flagshipsPrograms = loggedCrp.getCrpPrograms().stream()
      .filter(c -> c.getProgramType() == ProgramType.FLAGSHIP_PROGRAM_TYPE.getValue() && c.isActive())
      .collect(Collectors.toList());
    flagshipsPrograms.sort((p1, p2) -> p1.getAcronym().compareTo(p2.getAcronym()));
    // Get the regions list of this CRP
    regionsPrograms = loggedCrp.getCrpPrograms().stream()
      .filter(c -> c.getProgramType() == ProgramType.REGIONAL_PROGRAM_TYPE.getValue() && c.isActive())
      .collect(Collectors.toList());


    for (CrpProgram crpProgram : flagshipsPrograms) {
      crpProgram.setLeaders(crpProgram.getCrpProgramLeaders().stream().filter(c -> c.isActive() && !c.isManager())
        .collect(Collectors.toList()));
      crpProgram.setManagers(crpProgram.getCrpProgramLeaders().stream().filter(c -> c.isActive() && c.isManager())
        .collect(Collectors.toList()));
    }


    parameters =
      loggedCrp.getCustomParameters().stream().filter(c -> c.getParameter().getKey().equals(APConstants.CRP_HAS_REGIONS)
        && c.isActive() && c.getCrp().getId().equals(loggedCrp.getId())).collect(Collectors.toList());
    if (parameters.isEmpty()) {
      loggedCrp.setHasRegions(false);
    } else {
      boolean param = Boolean.parseBoolean(parameters.get(0).getValue());
      loggedCrp.setHasRegions(param);
    }

    this.setBasePermission(this.getText(Permission.CRP_ADMIN_BASE_PERMISSION, params));
    if (this.isHttpPost()) {
      if (loggedCrp.getProgramManagmenTeam() != null) {
        loggedCrp.getProgramManagmenTeam().clear();
        loggedCrp.setProgramManagmenTeam(null);
      }
      if (flagshipsPrograms != null) {
        flagshipsPrograms.clear();
        flagshipsPrograms = (null);
      }


    }
  }

  /**
   * Records a change this section makes to a user role. The management section assigns and removes roles through the
   * plain DAO save and delete, which carry no action name and therefore never reach the audit log, so this trace is
   * the only record that the assignment changed.
   * 
   * @param change what is being done to the role, used to open the message.
   * @param userRole the role assignment that changed.
   */
  private void logUserRoleChange(String change, UserRole userRole) {
    User user = userRole.getUser();
    LOG.info("{} the role {} for the user {} ({}) in the management section of {}", change,
      userRole.getRole() == null ? null : userRole.getRole().getId(), user == null ? null : user.getId(),
      user == null ? null : user.getEmail(), loggedCrp.getAcronym());
  }

  private void programLeaderData(CrpProgram crpProgramDb, CrpProgram crpProgram) {
    if (crpProgram.getLeaders() != null) {
      for (CrpProgramLeader crpProgramLeader : crpProgram.getLeaders()) {
        if (crpProgramLeader.getId() == null) {
          crpProgramLeader.setCrpProgram(crpProgram);
          crpProgramLeader.setManager(false);
          CrpProgram crpProgramPrevLeaders = crpProgramManager.getCrpProgramById(crpProgram.getId());
          if (crpProgramPrevLeaders.getCrpProgramLeaders().stream()
            .filter(c -> c.isActive() && c.getCrpProgram().equals(crpProgramLeader.getCrpProgram())
              && c.getUser().equals(crpProgramLeader.getUser()))
            .collect(Collectors.toList()).isEmpty()) {

            if (crpProgramPrevLeaders.getLiaisonInstitutions() == null
              || crpProgramPrevLeaders.getLiaisonInstitutions().isEmpty()) {
              LOG.warn("The program {} ({}) has no liaison institution, so the leader {} was saved without a liaison "
                + "user.", crpProgramPrevLeaders.getId(), crpProgramPrevLeaders.getAcronym(),
                crpProgramLeader.getUser() == null ? null : crpProgramLeader.getUser().getId());
            }

            for (LiaisonInstitution liasonInstitution : crpProgramPrevLeaders.getLiaisonInstitutions()) {
              if (liasonInstitution == null || liasonInstitution.getId() == null) {
                continue;
              }

              LiaisonUser liaisonUser = new LiaisonUser();
              liaisonUser.setCrp(loggedCrp);
              liaisonUser.setLiaisonInstitution(liasonInstitution);
              liaisonUser.setUser(crpProgramLeader.getUser());
              liaisonUserManager.saveLiaisonUser(liaisonUser);
            }


            crpProgramLeaderManager.saveCrpProgramLeader(crpProgramLeader);
          }


          User user = userManager.getUser(crpProgramLeader.getUser().getId());
          UserRole userRole = new UserRole();
          userRole.setUser(user);

          if (crpProgram.getProgramType() == ProgramType.FLAGSHIP_PROGRAM_TYPE.getValue()) {
            userRole.setRole(fplRole);
          }

          if (!user.getUserRoles().contains(userRole)) {
            userRoleManager.saveUserRole(userRole);
            userRole.setUser(userManager.getUser(userRole.getUser().getId()));
            this.logUserRoleChange("Assigned", userRole);
            this.notifyNewUserCreated(userRole.getUser());
            // Notifiy user been asigned Program Leader to Flagship
            this.notifyRoleFlagshipAssigned(userRole.getUser(), userRole.getRole(), crpProgram);
          }

          this.addCrpUser(user);
        }
      }

    }
    /*
     * Temporally soluction
     */
    for (CrpProgramLeader leaderPreview : crpProgramLeaderManager.findAll().stream()
      .filter(c -> c.getCrpProgram().equals(crpProgramDb) && c.isActive() && !c.isManager())
      .collect(Collectors.toList())) {
      /*
       * crpProgramDb.getCrpProgramLeaders().stream()
       * .filter(c -> c.isActive() && !c.isManager()).collect(Collectors.toList())) {
       */
      if (crpProgram.getLeaders() == null) {
        crpProgram.setLeaders(new ArrayList<>());
      }
      if (!crpProgram.getLeaders().contains(leaderPreview)) {
        crpProgramLeaderManager.deleteCrpProgramLeader(leaderPreview.getId());
        Set<LiaisonInstitution> liaisonInstitutions = crpProgramDb.getLiaisonInstitutions();
        for (LiaisonInstitution liaisonInstitution : liaisonInstitutions) {
          List<LiaisonUser> liaisonUsers = liaisonInstitution.getLiaisonUsers().stream()
            .filter(c -> c.getUser().getId().equals(leaderPreview.getUser().getId())).collect(Collectors.toList());
          for (LiaisonUser liaisonUser : liaisonUsers) {
            liaisonUserManager.deleteLiaisonUser(liaisonUser.getId());
          }

        }

        User user = userManager.getUser(leaderPreview.getUser().getId());


        List<CrpProgramLeader> existsUserLeader = user.getCrpProgramLeaders().stream()
          .filter(u -> u.isActive() && u.getCrpProgram().getCrp().getId().longValue() == loggedCrp.getId().longValue()
            && u.getCrpProgram().getProgramType() == crpProgramDb.getProgramType())
          .collect(Collectors.toList());


        if (existsUserLeader == null || existsUserLeader.isEmpty()) {

          if (crpProgramDb.getProgramType() == ProgramType.FLAGSHIP_PROGRAM_TYPE.getValue()) {
            List<UserRole> fplUserRoles =
              user.getUserRoles().stream().filter(ur -> ur.getRole().equals(fplRole)).collect(Collectors.toList());
            if (CollectionUtils.isNotEmpty(fplUserRoles)) {
              for (UserRole userRole : fplUserRoles) {
                this.logUserRoleChange("Removing", userRole);
                userRoleManager.deleteUserRole(userRole.getId());
                userRole.setUser(userManager.getUser(userRole.getUser().getId()));
                // Notifiy user been unasigned Program Leader to Flagship
                this.notifyRoleFlagshipUnassigned(userRole.getUser(), userRole.getRole(), crpProgram);
              }
            }
          }
        }

        this.checkCrpUserByRole(user);
      }
    }
  }

  private void programManagerData(CrpProgram crpProgramDb, CrpProgram crpProgram) {

    for (CrpProgramLeader leaderPreview : crpProgramLeaderManager.findAll().stream()
      .filter(c -> c.getCrpProgram().equals(crpProgramDb) && c.isActive() && c.isManager())
      .collect(Collectors.toList())) {

      if (crpProgram.getManagers() == null) {
        crpProgram.setManagers(new ArrayList<>());
      }
      if (!crpProgram.getManagers().contains(leaderPreview)) {
        crpProgramLeaderManager.deleteCrpProgramLeader(leaderPreview.getId());


        User user = userManager.getUser(leaderPreview.getUser().getId());


        List<CrpProgramLeader> existsUserLeader = user.getCrpProgramLeaders().stream()
          .filter(u -> u.isActive() && u.getCrpProgram().getCrp().getId().longValue() == loggedCrp.getId().longValue()
            && u.getCrpProgram().getProgramType() == crpProgramDb.getProgramType())
          .collect(Collectors.toList());


        if (existsUserLeader == null || existsUserLeader.isEmpty()) {

          if (crpProgramDb.getProgramType() == ProgramType.FLAGSHIP_PROGRAM_TYPE.getValue()) {
            List<UserRole> fplUserRoles =
              user.getUserRoles().stream().filter(ur -> ur.getRole().equals(fpmRole)).collect(Collectors.toList());
            if (CollectionUtils.isNotEmpty(fplUserRoles)) {
              for (UserRole userRole : fplUserRoles) {
                this.logUserRoleChange("Removing", userRole);
                userRoleManager.deleteUserRole(userRole.getId());
                userRole.setUser(userManager.getUser(userRole.getUser().getId()));
                // Notifiy user been unasigned Program Leader to Flagship

                this.notifyRoleFlagshipManagerUnassigned(userRole.getUser(), userRole.getRole(), crpProgram);
              }
            }
          }
        }

        this.checkCrpUserByRole(user);
      }
    }


    if (crpProgram.getManagers() != null) {
      for (CrpProgramLeader crpProgramLeader : crpProgram.getManagers()) {
        if (crpProgramLeader.getId() == null) {
          crpProgramLeader.setCrpProgram(crpProgram);
          crpProgramLeader.setManager(true);
          CrpProgram crpProgramPrevLeaders = crpProgramManager.getCrpProgramById(crpProgram.getId());
          if (crpProgramPrevLeaders.getCrpProgramLeaders().stream()
            .filter(c -> c.isActive() && c.getCrpProgram().equals(crpProgramLeader.getCrpProgram())
              && c.getUser().equals(crpProgramLeader.getUser()))
            .collect(Collectors.toList()).isEmpty()) {


            crpProgramLeaderManager.saveCrpProgramLeader(crpProgramLeader);
          }


          User user = userManager.getUser(crpProgramLeader.getUser().getId());
          UserRole userRole = new UserRole();
          userRole.setUser(user);

          if (crpProgram.getProgramType() == ProgramType.FLAGSHIP_PROGRAM_TYPE.getValue()) {
            userRole.setRole(fpmRole);
          }

          if (!user.getUserRoles().contains(userRole)) {
            userRoleManager.saveUserRole(userRole);
            userRole.setUser(userManager.getUser(userRole.getUser().getId()));
            this.logUserRoleChange("Assigned", userRole);
            this.notifyNewUserCreated(userRole.getUser());
            // Notifiy user been asigned Program Leader to Flagship
            this.notifyRoleFlagshipManagerAssigned(userRole.getUser(), userRole.getRole(), crpProgram);
          }

          this.addCrpUser(user);
        }
      }

    }
  }


  @Override
  public String save() {
    if (this.hasPermission("*")) {
      LOG.info("The user {} is saving the management section of {}", this.getCurrentUser().getEmail(),
        loggedCrp.getAcronym());
      this.setUsersToActive(new ArrayList<>());

      this.savePmuRoleData();
      this.saveProgramsData();


      CustomParameter parameter = null;
      if (parameters.size() == 0) {
        parameter = new CustomParameter();
        parameter.setCrp(loggedCrp);

        parameter.setParameter(
          parameterManager.getParameterByKey(APConstants.CRP_HAS_REGIONS, loggedCrp.getGlobalUnitType().getId()));

      } else {
        parameter = parameters.get(0);
      }
      parameter.setValue(loggedCrp.isHasRegions() + "");

      crpParameterManager.saveCustomParameter(parameter);
      this.getSession().put(parameter.getParameter().getKey(), parameter.getValue());

      /*
       * Desactive regions
       */
      if (!loggedCrp.isHasRegions()) {
        List<CrpProgram> rgProgramsRewiev =
          crpProgramManager.findCrpProgramsByType(loggedCrp.getId(), ProgramType.REGIONAL_PROGRAM_TYPE.getValue());
        rgProgramsRewiev =
          crpProgramManager.findCrpProgramsByType(loggedCrp.getId(), ProgramType.REGIONAL_PROGRAM_TYPE.getValue());

        if (rgProgramsRewiev != null) {
          for (CrpProgram crpProgram : rgProgramsRewiev) {
            LOG.info("Removing the program {} of {}", crpProgram.getAcronym(), loggedCrp.getAcronym());
            crpProgramManager.deleteCrpProgram(crpProgram.getId());
          }
        }
      }


      this.addUsers();
      Collection<String> messages = this.getActionMessages();
      if (!this.getInvalidFields().isEmpty()) {

        this.setActionMessages(null);
        // this.addActionMessage(Map.toString(this.getInvalidFields().toArray()));
        List<String> keys = new ArrayList<String>(this.getInvalidFields().keySet());

        for (String key : keys) {

          this.addActionMessage(key + ": " + this.getInvalidFields().get(key));
          // These rejections are reported on the screen and the action still returns SUCCESS, so without this line
          // a save the user saw fail leaves no trace in the log.
          LOG.warn("The management section of {} rejected the field {} of the user {}: {}", loggedCrp.getAcronym(), key,
            this.getCurrentUser().getEmail(), this.getInvalidFields().get(key));
        }


        // this.addActionWarning(this.getText("saving.saved") + Arrays.toString(this.getInvalidFields().toArray()));
      } else {
        this.addActionMessage("message:" + this.getText("saving.saved"));
        LOG.info("The management section of {} was saved by {}", loggedCrp.getAcronym(),
          this.getCurrentUser().getEmail());
      }
      messages = this.getActionMessages();
      
      return SUCCESS;
    } else {
      LOG.warn("The user {} tried to save the management section of {} without the permission to do so",
        this.getCurrentUser().getEmail(), loggedCrp.getAcronym());
      return NOT_AUTHORIZED;
    }

  }

  private void savePmuRoleData() {
    Role rolePreview = roleManager.getRoleById(pmuRol);
    // Removing users roles
    int i = 0;
    for (UserRole userRole : rolePreview.getUserRoles()) {
      if (loggedCrp.getProgramManagmenTeam() != null) {
        if (!loggedCrp.getProgramManagmenTeam().contains(userRole)) {

          List<LiaisonUser> liaisonUsers = liaisonUserManager.findAll().stream()
            .filter(c -> c.getUser().getId().longValue() == userRole.getUser().getId().longValue()
              && c.getLiaisonInstitution().getId().longValue() == cuId)
            .collect(Collectors.toList());
          if (liaisonUsers.isEmpty()) {

            this.logUserRoleChange("Removing", userRole);
            userRoleManager.deleteUserRole(userRole.getId());
          } else {
            boolean deletePmu = true;
            for (LiaisonUser liaisonUser : liaisonUsers) {
              if (liaisonUser.getProjects().stream()
                .filter(c -> c.isActive() && c.getPhase().equals(this.getActualPhase()) && c.getStatus() != null
                  && (c.getStatus().intValue() == Integer.parseInt(ProjectStatusEnum.Ongoing.getStatusId())
                    || c.getStatus().intValue() == Integer.parseInt(ProjectStatusEnum.Extended.getStatusId())))
                .collect(Collectors.toList()).isEmpty()) {
                liaisonUserManager.deleteLiaisonUser(liaisonUser.getId());

              } else {
                deletePmu = false;
                HashMap<String, String> error = new HashMap<>();
                this.getInvalidFields().put("input-loggedCrp.programManagmenTeam[" + i + "].id",
                  "PMU, can not be deleted");

              }


            }
            if (deletePmu) {

              this.notifyRoleProgramManagementUnassigned(userRole.getUser(), userRole.getRole());
              this.logUserRoleChange("Removing", userRole);
              userRoleManager.deleteUserRole(userRole.getId());

            }
          }
          this.checkCrpUserByRole(userRole.getUser());
        }

      } else {

        List<LiaisonUser> liaisonUsers = liaisonUserManager.findAll().stream()
          .filter(c -> c.getUser().getId().longValue() == userRole.getUser().getId().longValue()
            && c.getLiaisonInstitution().getId().longValue() == cuId)
          .collect(Collectors.toList());
        if (liaisonUsers.isEmpty()) {

          this.logUserRoleChange("Removing", userRole);
          userRoleManager.deleteUserRole(userRole.getId());
        } else {
          boolean deletePmu = true;
          for (LiaisonUser liaisonUser : liaisonUsers) {
            if (liaisonUser.getProjects().stream()
              .filter(c -> c.isActive() && c.getPhase().equals(this.getActualPhase()) && c.getStatus() != null
                && (c.getStatus().intValue() == Integer.parseInt(ProjectStatusEnum.Ongoing.getStatusId())
                  || c.getStatus().intValue() == Integer.parseInt(ProjectStatusEnum.Extended.getStatusId())))
              .collect(Collectors.toList()).isEmpty()) {
              liaisonUserManager.deleteLiaisonUser(liaisonUser.getId());

            } else {
              deletePmu = false;
              HashMap<String, String> error = new HashMap<>();
              this.getInvalidFields().put("input-loggedCrp.programManagmenTeam[" + i + "].id",
                "PMU, can not be deleted");

            }


          }
          if (deletePmu) {

            this.notifyRoleProgramManagementUnassigned(userRole.getUser(), userRole.getRole());
            this.logUserRoleChange("Removing", userRole);
            userRoleManager.deleteUserRole(userRole.getId());

          }
        }
        this.checkCrpUserByRole(userRole.getUser());

      }
      i++;
    }
    // Add new Users roles
    if ((loggedCrp.getProgramManagmenTeam() != null)) {
      /*
       * liaison_users.institution_id is mandatory, so the liaison institution is resolved before any role is saved:
       * a dangling crp_cu parameter must stop the assignment instead of leaving the role persisted and the
       * notification sent behind a liaison user that could not be created.
       */
      boolean hasNewUsers = loggedCrp.getProgramManagmenTeam().stream().anyMatch(ur -> ur.getId() == null);
      LiaisonInstitution cuLiasonInstitution = hasNewUsers ? this.resolvePmuLiaisonInstitution() : null;

      if (hasNewUsers && cuLiasonInstitution == null) {
        LOG.error("The Program Management Unit liaison institution of the Global Unit {} could not be resolved "
          + "({}={}). No user was added to the Program Management Unit.", loggedCrp.getAcronym(), APConstants.CRP_CU,
          cuId);
        this.getInvalidFields().put("list-loggedCrp.programManagmenTeam",
          this.getText("programManagement.pmuLiaisonInstitution.missing"));
        return;
      }

      for (UserRole userRole : loggedCrp.getProgramManagmenTeam()) {
        if (userRole.getId() == null) {
          if (rolePreview.getUserRoles().stream().filter(ur -> ur.getUser().equals(userRole.getUser()))
            .collect(Collectors.toList()).isEmpty()) {
            userRoleManager.saveUserRole(userRole);
            userRole.setUser(userManager.getUser(userRole.getUser().getId()));
            this.logUserRoleChange("Assigned", userRole);

            this.addCrpUser(userRole.getUser());
            this.notifyNewUserCreated(userRole.getUser());
            // Notifiy user been assigned to Program Management
            this.notifyRoleProgramManagementAssigned(userRole.getUser(), userRole.getRole());

            LiaisonUser liaisonUser = new LiaisonUser();
            liaisonUser.setCrp(loggedCrp);
            liaisonUser.setLiaisonInstitution(cuLiasonInstitution);
            liaisonUser.setUser(userRole.getUser());
            liaisonUserManager.saveLiaisonUser(liaisonUser);
          }
        }
      }
    }

  }

  private boolean isDeletedProgramGhost(CrpProgram crpProgram) {
    if (crpProgram == null || crpProgram.getId() != null) {
      return false;
    }

    boolean emptyAcronym = crpProgram.getAcronym() == null || crpProgram.getAcronym().trim().isEmpty();
    boolean emptyName = crpProgram.getName() == null || crpProgram.getName().trim().isEmpty();
    boolean emptyLeaders = crpProgram.getLeaders() == null || crpProgram.getLeaders().isEmpty();
    boolean emptyManagers = crpProgram.getManagers() == null || crpProgram.getManagers().isEmpty();
    boolean emptyCountries = crpProgram.getSelectedCountries() == null || crpProgram.getSelectedCountries().isEmpty();

    return emptyAcronym && emptyName && emptyLeaders && emptyManagers && emptyCountries;
  }

  private void saveProgramsData() {
    List<CrpProgram> fgProgramsRewiev =
      crpProgramManager.findCrpProgramsByType(loggedCrp.getId(), ProgramType.FLAGSHIP_PROGRAM_TYPE.getValue());

    Set<Long> submittedFlagshipProgramIds = new HashSet<>();
    if (flagshipsPrograms != null) {
      for (CrpProgram program : flagshipsPrograms) {
        if (program != null && program.getId() != null) {
          submittedFlagshipProgramIds.add(program.getId());
        }
      }
    }

    // Removing crp flagship program type
    if (fgProgramsRewiev != null) {
      for (CrpProgram crpProgram : fgProgramsRewiev) {
        if (!submittedFlagshipProgramIds.contains(crpProgram.getId())) {
          CrpProgram crpProgramBD = crpProgramManager.getCrpProgramById(crpProgram.getId());

          List<CrpProgramLeader> activeLeaders = crpProgramBD.getCrpProgramLeaders().stream()
            .filter(c -> c.isActive()).collect(Collectors.toList());

          if (activeLeaders.isEmpty()) {

            List<LiaisonInstitution> activeInstitutions = crpProgram.getLiaisonInstitutions().stream()
              .filter(c -> c.isActive()).collect(Collectors.toList());

            for (LiaisonInstitution institution : activeInstitutions) {
              liaisonInstitutionManager.deleteLiaisonInstitution(institution.getId());
            }

            LOG.info("Removing the program {} of {}", crpProgram.getAcronym(), loggedCrp.getAcronym());
            crpProgramManager.deleteCrpProgram(crpProgram.getId());
          } else {
            for (CrpProgramLeader leader : activeLeaders) {

              // Remove user roles for this leader
              List<UserRole> userRoles = leader.getUser().getUserRoles().stream()
                .filter(ur -> ur.getRole().equals(fplRole) || ur.getRole().equals(fpmRole))
                .collect(Collectors.toList());

              for (UserRole userRole : userRoles) {
                this.logUserRoleChange("Removing", userRole);
                userRoleManager.deleteUserRole(userRole.getId());
              }

              // Remove liaison users for this leader
              List<LiaisonUser> liaisonUsers = liaisonUserManager.findAll().stream()
                .filter(lu -> lu.getUser().getId().equals(leader.getUser().getId())
                  && lu.getCrp().getId().equals(loggedCrp.getId()))
                .collect(Collectors.toList());

              for (LiaisonUser liaisonUser : liaisonUsers) {
                liaisonUserManager.deleteLiaisonUser(liaisonUser.getId());
              }

              // Remove the program leader itself
              crpProgramLeaderManager.deleteCrpProgramLeader(leader.getId());
            }

            // Now proceed with program deletion

            List<LiaisonInstitution> activeInstitutions = crpProgram.getLiaisonInstitutions().stream()
              .filter(c -> c.isActive()).collect(Collectors.toList());

            for (LiaisonInstitution institution : activeInstitutions) {
              liaisonInstitutionManager.deleteLiaisonInstitution(institution.getId());
            }

            LOG.info("Removing the program {} of {}", crpProgram.getAcronym(), loggedCrp.getAcronym());
            crpProgramManager.deleteCrpProgram(crpProgram.getId());
          }
        }
      }
    }

    Set<Long> validFlagshipProgramIds = new HashSet<>();
    List<CrpProgram> remainingFlagshipPrograms =
      crpProgramManager.findCrpProgramsByType(loggedCrp.getId(), ProgramType.FLAGSHIP_PROGRAM_TYPE.getValue());
    if (remainingFlagshipPrograms != null) {
      for (CrpProgram remainingProgram : remainingFlagshipPrograms) {
        if (remainingProgram != null && remainingProgram.getId() != null) {
          validFlagshipProgramIds.add(remainingProgram.getId());
        }
      }
    }

    if (flagshipsPrograms != null) {
      flagshipsPrograms = flagshipsPrograms.stream().filter(crpProgram -> crpProgram != null)
        .filter(crpProgram -> !this.isDeletedProgramGhost(crpProgram))
        .filter(crpProgram -> crpProgram.getId() == null || validFlagshipProgramIds.contains(crpProgram.getId()))
        .collect(Collectors.toList());
    }

    CrpProgram crpProgramDb = null;
    // Add crp flagship program type
    if (flagshipsPrograms != null) {
      for (CrpProgram crpProgram : flagshipsPrograms) {
        if (crpProgram.getId() == null) {
          // Validate that name is not null before saving
          if (crpProgram.getName() == null || crpProgram.getName().trim().isEmpty()) {
            HashMap<String, String> error = new HashMap<>();
            error.put("list-flagshipsPrograms", "Program name cannot be null");
            this.getInvalidFields().putAll(error);
            continue;
          }
          
          crpProgram.setCrp(loggedCrp);
          crpProgramDb = crpProgramManager.saveCrpProgram(crpProgram);
          
          LiaisonInstitution liasonInstitution = new LiaisonInstitution();
          liasonInstitution.setAcronym(crpProgramDb.getAcronym());
          liasonInstitution.setCrp(loggedCrp);
          liasonInstitution.setCrpProgram(crpProgramDb);
          liasonInstitution.setName(crpProgramDb.getName());

          liaisonInstitutionManager.saveLiaisonInstitution(liasonInstitution);

        } else {
          crpProgramDb = crpProgramManager.getCrpProgramById(crpProgram.getId());
          crpProgramDb.setCrp(loggedCrp);
          crpProgramDb.setAcronym(crpProgram.getAcronym());
          
          // Protection against null names
          if (crpProgram.getName() != null && !crpProgram.getName().trim().isEmpty()) {
            crpProgramDb.setName(crpProgram.getName());
          }
          
          crpProgramDb.setBaseLine(crpProgram.getBaseLine());
          crpProgramDb.setColor(crpProgram.getColor());
          crpProgramDb = crpProgramManager.saveCrpProgram(crpProgramDb);

          /**
           * One day I will understand what the need is for having the same data duplicated from the CrpProgram in the
           * Liasion Institution, but that day is not today.
           */
          for (LiaisonInstitution liasonInstitutionDb : crpProgramDb.getLiaisonInstitutions()) {
            liasonInstitutionDb.setAcronym(crpProgram.getAcronym());
            liasonInstitutionDb.setName(crpProgram.getName());
            liaisonInstitutionManager.saveLiaisonInstitution(liasonInstitutionDb);

          }

        }
        this.programLeaderData(crpProgramDb, crpProgram);
        this.programManagerData(crpProgramDb, crpProgram);
      }
    }
  }


  public void setFlagshipsPrograms(List<CrpProgram> flagshipsPrograms) {
    this.flagshipsPrograms = flagshipsPrograms;
  }

  public void setFplRole(Role fplRole) {
    this.fplRole = fplRole;
  }

  public void setFpmRole(Role fpmRole) {
    this.fpmRole = fpmRole;
  }


  public void setLoggedCrp(GlobalUnit loggedCrp) {
    this.loggedCrp = loggedCrp;
  }


  public void setPmuRol(long pmuRol) {
    this.pmuRol = pmuRol;
  }


  public void setRegionsPrograms(List<CrpProgram> regionsPrograms) {
    this.regionsPrograms = regionsPrograms;
  }

  public void setRolePmu(Role rolePmu) {
    this.rolePmu = rolePmu;
  }

  @Override
  public void validate() {
    Gson gson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();
    if (save) {
      HashMap<String, String> error = new HashMap<>();
      if (loggedCrp.getProgramManagmenTeam() == null || loggedCrp.getProgramManagmenTeam().isEmpty()) {

        error.put("list-loggedCrp.programManagmenTeam", InvalidFieldsMessages.EMPTYUSERLIST);
        // invalidFields.add(gson.toJson(gson));
      }
      if (flagshipsPrograms == null || flagshipsPrograms.isEmpty()) {

        error.put("list-flagshipsPrograms", this.getText(InvalidFieldsMessages.EMPTYLIST, new String[] {"Flagships"}));
        // invalidFields.add(gson.toJson(gson));
      } else {
        int index = 0;
        for (CrpProgram crpProgram : flagshipsPrograms) {
          if (crpProgram.getName() == null || crpProgram.getName().trim().isEmpty()) {
            error.put("list-flagshipsPrograms[" + index + "].name",
              this.getText("CrpProgram.inputName.required"));
          }
          if (crpProgram.getAcronym() == null || crpProgram.getAcronym().trim().isEmpty()) {
            error.put("list-flagshipsPrograms[" + index + "].acronym",
              this.getText("CrpProgram.inputAcronym.required"));
          }
          if (crpProgram.getLeaders() == null || crpProgram.getLeaders().isEmpty()) {
            error.put("list-flagshipsPrograms[" + index + "].leaders",
              this.getText(InvalidFieldsMessages.EMPTYLIST, new String[] {"Flagship Leaders"}));
          }
          index++;
        }
      }


      this.setInvalidFields(error);
    }
  }

}
