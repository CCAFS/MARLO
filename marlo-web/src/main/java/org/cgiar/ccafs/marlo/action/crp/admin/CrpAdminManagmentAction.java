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
import org.cgiar.ccafs.marlo.data.manager.CrpManager;
import org.cgiar.ccafs.marlo.data.manager.CrpParameterManager;
import org.cgiar.ccafs.marlo.data.manager.CrpProgramLeaderManager;
import org.cgiar.ccafs.marlo.data.manager.CrpProgramManager;
import org.cgiar.ccafs.marlo.data.manager.CrpUserManager;
import org.cgiar.ccafs.marlo.data.manager.LiaisonInstitutionManager;
import org.cgiar.ccafs.marlo.data.manager.LiaisonUserManager;
import org.cgiar.ccafs.marlo.data.manager.RoleManager;
import org.cgiar.ccafs.marlo.data.manager.UserManager;
import org.cgiar.ccafs.marlo.data.manager.UserRoleManager;
import org.cgiar.ccafs.marlo.data.model.Crp;
import org.cgiar.ccafs.marlo.data.model.CrpParameter;
import org.cgiar.ccafs.marlo.data.model.CrpProgram;
import org.cgiar.ccafs.marlo.data.model.CrpProgramLeader;
import org.cgiar.ccafs.marlo.data.model.CrpUser;
import org.cgiar.ccafs.marlo.data.model.LiaisonInstitution;
import org.cgiar.ccafs.marlo.data.model.LiaisonUser;
import org.cgiar.ccafs.marlo.data.model.ProgramType;
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
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.inject.Inject;
import org.apache.commons.lang3.RandomStringUtils;

/**
 * This action is part of the CRP admin backend.
 * 
 * @author Christian Garcia
 */
public class CrpAdminManagmentAction extends BaseAction {

  private static final long serialVersionUID = 3355662668874414548L;

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
  private CrpManager crpManager;
  private CrpParameterManager crpParameterManager;
  private CrpUserManager crpUserManager;
  // Variables
  private Crp loggedCrp;
  private Role rolePmu;
  private long pmuRol;
  private long cuId;
  private List<CrpProgram> flagshipsPrograms;


  private List<CrpProgram> regionsPrograms;


  private List<CrpParameter> parameters;

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
    CrpProgramManager crpProgramManager, CrpManager crpManager, CrpParameterManager crpParameterManager,
    CrpProgramLeaderManager crpProgramLeaderManager, UserManager userManager, SendMailS sendMail,
    LiaisonUserManager liaisonUserManager, LiaisonInstitutionManager liaisonInstitutionManager,
    CrpUserManager crpUserManager) {
    super(config);
    this.roleManager = roleManager;
    this.userRoleManager = userRoleManager;
    this.crpManager = crpManager;
    this.crpProgramManager = crpProgramManager;
    this.crpParameterManager = crpParameterManager;
    this.userManager = userManager;
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
      crpUser.setActive(true);
      crpUser.setActiveSince(new Date());
      crpUser.setCreatedBy(this.getCurrentUser());
      crpUser.setModifiedBy(this.getCurrentUser());
      crpUser.setModificationJustification("");
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

  public Crp getLoggedCrp() {
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

      user.setActive(true);
      // Building the Email message:
      StringBuilder message = new StringBuilder();
      message.append(this.getText("email.dear", new String[] {user.getFirstName()}));
      String password = this.getText("email.outlookPassword");
      if (!user.isCgiarUser()) {
        // Generating a random password.
        password = RandomStringUtils.randomNumeric(6);
        // Applying the password to the user.
        user.setPassword(password);
      }
      message
        .append(this.getText("email.newUser.part1", new String[] {config.getBaseUrl(), user.getEmail(), password}));
      message.append(this.getText("email.support"));
      message.append(this.getText("email.bye"));

      // Saving the new user configuration.
      userManager.saveUser(user, this.getCurrentUser());

      // Send pdf
      String contentType = "application/pdf";
      String fileName = "MARLO_UserManual_V1.1.pdf";
      byte[] buffer = null;
      InputStream inputStream = null;

      try {
        inputStream = this.getClass().getResourceAsStream("/manual/MARLO_UserManual_20170111_AV_HT_AW.pdf");
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
      String bbcEmails = this.config.getEmailNotification();

      if (buffer != null && fileName != null && contentType != null) {
        sendMail.send(user.getEmail(), null, bbcEmails,
          this.getText("email.newUser.subject", new String[] {user.getComposedName()}), message.toString(), buffer,
          contentType, fileName, true);
      } else {
        sendMail.send(user.getEmail(), null, bbcEmails,
          this.getText("email.newUser.subject", new String[] {user.getComposedName()}), message.toString(), null, null,
          null, true);
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
    crpProgram = crpProgramManager.getCrpProgramById(crpProgram.getId());
    String flasgshipRole = role.getDescription();
    String flasgshipRoleAcronym = this.getText("programManagement.flagship.role.acronym");

    userAssigned = userManager.getUser(userAssigned.getId());
    StringBuilder message = new StringBuilder();
    // Building the Email message:
    message.append(this.getText("email.dear", new String[] {userAssigned.getFirstName()}));
    message.append(this.getText("email.flagship.assigned",
      new String[] {flasgshipRole, crpProgram.getName(), crpProgram.getAcronym(), loggedCrp.getName()}));
    message.append(this.getText("email.flagship.responsabilities", new String[] {flasgshipRole}));
    message.append(this.getText("email.support"));
    message.append(this.getText("email.bye"));

    // Email send to the user assigned
    String toEmail = userAssigned.getEmail();
    // CC will be the user who is making the modification.

    String ccEmail = "";
    if (this.getCurrentUser() != null) {
      ccEmail = this.getCurrentUser().getEmail() + ", ";
    }
    // CC will be also the others FL already assigned to the Flagship
    for (CrpProgramLeader crpProgramLeader : crpProgram.getCrpProgramLeaders().stream()
      .filter(cpl -> cpl.getUser().isActive() && cpl.isActive()).collect(Collectors.toList())) {
      ccEmail += crpProgramLeader.getUser().getEmail() + ", ";
    }
    // Detect if a last ; was added to CC and remove it
    if (ccEmail != null && ccEmail.length() > 0 && ccEmail.charAt(ccEmail.length() - 2) == ',') {
      ccEmail = ccEmail.substring(0, ccEmail.length() - 2);
    }
    // BBC will be our gmail notification email.
    String bbcEmails = this.config.getEmailNotification();

    if (role.equals(fplRole)) {
      sendMail.send(toEmail, ccEmail, bbcEmails,
        this.getText("email.flagship.assigned.subject", new String[] {crpProgram.getAcronym(), loggedCrp.getName()}),
        message.toString(), null, null, null, true);
    } else {
      sendMail.send(toEmail, ccEmail, bbcEmails, this.getText("email.flagshipmanager.assigned.subject",
        new String[] {crpProgram.getAcronym(), loggedCrp.getName()}), message.toString(), null, null, null, true);
    }


  }

  private void notifyRoleFlagshipUnassigned(User userRemoved, Role role, CrpProgram crpProgram) {
    crpProgram = crpProgramManager.getCrpProgramById(crpProgram.getId());
    String flasgshipRole = role.getDescription();
    String flasgshipRoleAcronym = role.getDescription();

    userRemoved = userManager.getUser(userRemoved.getId());
    StringBuilder message = new StringBuilder();
    // Building the Email message:
    message.append(this.getText("email.dear", new String[] {userRemoved.getFirstName()}));
    message.append(this.getText("email.flagship.unassigned",
      new String[] {flasgshipRole, crpProgram.getName(), crpProgram.getAcronym(), loggedCrp.getName()}));
    message.append(this.getText("email.support"));
    message.append(this.getText("email.bye"));

    // Email send to the user assigned
    String toEmail = userRemoved.getEmail();
    // CC will be the user who is making the modification.
    String ccEmail = "";
    if (this.getCurrentUser() != null) {
      ccEmail = this.getCurrentUser().getEmail() + ", ";
    }
    // CC will be also the others FL already assigned to the Flagship
    for (CrpProgramLeader crpProgramLeader : crpProgram.getCrpProgramLeaders().stream()
      .filter(cpl -> cpl.getUser().isActive() && cpl.isActive()).collect(Collectors.toList())) {
      ccEmail += crpProgramLeader.getUser().getEmail() + ", ";
    }
    // Detect if a last ; was added to CC and remove it
    if (ccEmail != null && ccEmail.length() > 0 && ccEmail.charAt(ccEmail.length() - 2) == ',') {
      ccEmail = ccEmail.substring(0, ccEmail.length() - 2);
    }
    // BBC will be our gmail notification email.
    String bbcEmails = this.config.getEmailNotification();
    if (role.equals(fplRole)) {
      sendMail.send(toEmail, ccEmail, bbcEmails,
        this.getText("email.flagship.unassigned.subject", new String[] {crpProgram.getAcronym(), loggedCrp.getName()}),
        message.toString(), null, null, null, true);
    } else {
      sendMail.send(toEmail, ccEmail, bbcEmails, this.getText("email.flagshipmanager.unassigned.subject",
        new String[] {crpProgram.getAcronym(), loggedCrp.getName()}), message.toString(), null, null, null, true);
    }
  }


  /**
   * This method notify the user that is been assigned as Program Leader for an specific Regional Program
   * 
   * @param userAssigned is the user been assigned
   * @param role is the role(Program Management)
   */
  private void notifyRoleProgramManagementAssigned(User userAssigned, Role role) {

    String managementRole =
      this.getText("programManagement.role") + " (" + this.getText("programManagement.role.acronym") + ")";
    String managementRoleAcronym = this.getText("programManagement.role.acronym");

    userAssigned = userManager.getUser(userAssigned.getId());
    StringBuilder message = new StringBuilder();
    // Building the Email message:
    message.append(this.getText("email.dear", new String[] {userAssigned.getFirstName()}));
    message
      .append(this.getText("email.programManagement.assigned", new String[] {managementRole, loggedCrp.getName()}));
    message.append(this.getText("email.programManagement.responsibilities"));
    message.append(this.getText("email.support"));
    message.append(this.getText("email.bye"));

    // Email send to the user assigned
    String toEmail = userAssigned.getEmail();
    // CC will be the user who is making the modification.
    String ccEmail = this.getCurrentUser().getEmail();
    // BBC will be our gmail notification email.
    String bbcEmails = this.config.getEmailNotification();

    sendMail.send(toEmail, ccEmail, bbcEmails, this.getText("email.programManagement.assigned.subject",
      new String[] {loggedCrp.getName(), managementRoleAcronym}), message.toString(), null, null, null, true);

  }

  /**
   * This method notify the user that is been assigned as Program Leader for an specific Regional Program
   * 
   * @param userAssigned is the user been assigned
   * @param role is the role(Program Management)
   */
  private void notifyRoleProgramManagementUnassigned(User userAssigned, Role role) {
    String managementRole = this.getText("programManagement.role");
    String managementRoleAcronym = this.getText("programManagement.role.acronym");

    userAssigned = userManager.getUser(userAssigned.getId());
    StringBuilder message = new StringBuilder();
    // Building the Email message:
    message.append(this.getText("email.dear", new String[] {userAssigned.getFirstName()}));
    message
      .append(this.getText("email.programManagement.unassigned", new String[] {managementRole, loggedCrp.getName()}));
    message.append(this.getText("email.support"));
    message.append(this.getText("email.bye"));

    // Email send to nobody
    String toEmail = null;
    // CC will be the CRP Admins.
    String ccEmail = "";
    Long crpAdminRole = Long.parseLong((String) this.getSession().get(APConstants.CRP_ADMIN_ROLE));
    Role roleCrpAdmin = roleManager.getRoleById(crpAdminRole);
    for (UserRole userRole : roleCrpAdmin.getUserRoles().stream().filter(ur -> ur.getUser().isActive())
      .collect(Collectors.toList())) {
      ccEmail += userRole.getUser().getEmail() + ", ";
    }
    // Detect if a last ; was added to CC and remove it
    if (ccEmail != null && ccEmail.length() > 0 && ccEmail.charAt(ccEmail.length() - 2) == ',') {
      ccEmail = ccEmail.substring(0, ccEmail.length() - 2);
    }

    // BBC will be our gmail notification email.
    String bbcEmails = this.config.getEmailNotification();

    sendMail.send(toEmail, ccEmail, bbcEmails, this.getText("email.programManagement.unassigned.subject",
      new String[] {loggedCrp.getName(), managementRoleAcronym}), message.toString(), null, null, null, true);

  }


  private void pmuRoleData() {
    Gson gson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();
    Role rolePreview = roleManager.getRoleById(pmuRol);
    // Removing users roles
    int i = 0;
    for (UserRole userRole : rolePreview.getUserRoles()) {
      if (!loggedCrp.getProgramManagmenTeam().contains(userRole)) {

        userRole.setUser(userManager.getUser(userRole.getUser().getId()));
        List<LiaisonUser> liaisonUsers = liaisonUserManager.findAll().stream()
          .filter(c -> c.getUser().getId().longValue() == userRole.getUser().getId().longValue()
            && c.getLiaisonInstitution().getId().longValue() == cuId)
          .collect(Collectors.toList());
        if (liaisonUsers.isEmpty()) {

          userRoleManager.deleteUserRole(userRole.getId());
        }
        boolean deletePmu = true;
        for (LiaisonUser liaisonUser : liaisonUsers) {
          if (liaisonUser.getProjects().isEmpty()) {
            liaisonUserManager.deleteLiaisonUser(liaisonUser.getId());

          } else {
            deletePmu = false;
            HashMap<String, String> error = new HashMap<>();
            this.getInvalidFields().put("input-loggedCrp.programManagmenTeam[" + i + "].id", "PMU, can not be deleted");

          }


        }
        if (deletePmu) {
          userRoleManager.deleteUserRole(userRole.getId());
          this.notifyRoleProgramManagementUnassigned(userRole.getUser(), userRole.getRole());

        }
        this.checkCrpUserByRole(userRole.getUser());
      }
      i++;
    }
    // Add new Users roles
    for (UserRole userRole : loggedCrp.getProgramManagmenTeam()) {
      if (userRole.getId() == null) {
        if (rolePreview.getUserRoles().stream().filter(ur -> ur.getUser().equals(userRole.getUser()))
          .collect(Collectors.toList()).isEmpty()) {
          userRoleManager.saveUserRole(userRole);
          userRole.setUser(userManager.getUser(userRole.getUser().getId()));

          this.addCrpUser(userRole.getUser());
          this.notifyNewUserCreated(userRole.getUser());
          // Notifiy user been assigned to Program Management
          this.notifyRoleProgramManagementAssigned(userRole.getUser(), userRole.getRole());

          LiaisonInstitution cuLiasonInstitution;

          cuLiasonInstitution = liaisonInstitutionManager.getLiaisonInstitutionById(cuId);
          LiaisonUser liaisonUser = new LiaisonUser();
          liaisonUser.setCrp(loggedCrp);
          liaisonUser.setLiaisonInstitution(cuLiasonInstitution);
          liaisonUser.setUser(userRole.getUser());
          liaisonUserManager.saveLiaisonUser(liaisonUser);
        }
      }
    }

  }

  @Override
  public void prepare() throws Exception {

    // Get the Users list that have the pmu role in this crp.
    loggedCrp = (Crp) this.getSession().get(APConstants.SESSION_CRP);
    loggedCrp = crpManager.getCrpById(loggedCrp.getId());

    pmuRol = Long.parseLong((String) this.getSession().get(APConstants.CRP_PMU_ROLE));
    cuId = Long.parseLong((String) this.getSession().get(APConstants.CRP_CU));
    rolePmu = roleManager.getRoleById(pmuRol);
    loggedCrp.setProgramManagmenTeam(new ArrayList<UserRole>(rolePmu.getUserRoles()));
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


    parameters = loggedCrp.getCrpParameters().stream().filter(c -> c.getKey().equals(APConstants.CRP_HAS_REGIONS)
      && c.isActive() && c.getCrp().getId().equals(loggedCrp.getId())).collect(Collectors.toList());
    if (parameters.size() == 0) {
      loggedCrp.setHasRegions(false);
    } else {
      boolean param = Boolean.parseBoolean(parameters.get(0).getValue());
      loggedCrp.setHasRegions(param);
    }

    this.setBasePermission(this.getText(Permission.CRP_ADMIN_BASE_PERMISSION, params));
    if (this.isHttpPost()) {
      loggedCrp.getProgramManagmenTeam().clear();
      flagshipsPrograms.clear();

    }
  }

  private void programLeaderData() {
    for (CrpProgram crpProgram : flagshipsPrograms) {
      CrpProgram crpProgramPrev = crpProgramManager.getCrpProgramById(crpProgram.getId());
      for (CrpProgramLeader leaderPreview : crpProgramPrev.getCrpProgramLeaders().stream()
        .filter(c -> c.isActive() && !c.isManager()).collect(Collectors.toList())) {

        if (crpProgram.getLeaders() == null) {
          crpProgram.setLeaders(new ArrayList<>());
        }
        if (!crpProgram.getLeaders().contains(leaderPreview)) {
          crpProgramLeaderManager.deleteCrpProgramLeader(leaderPreview.getId());
          Set<LiaisonInstitution> liaisonInstitutions = crpProgramPrev.getLiaisonInstitutions();
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
              && u.getCrpProgram().getProgramType() == crpProgramPrev.getProgramType())
            .collect(Collectors.toList());


          if (existsUserLeader == null || existsUserLeader.isEmpty()) {

            if (crpProgramPrev.getProgramType() == ProgramType.FLAGSHIP_PROGRAM_TYPE.getValue()) {
              List<UserRole> fplUserRoles =
                user.getUserRoles().stream().filter(ur -> ur.getRole().equals(fplRole)).collect(Collectors.toList());
              if (fplUserRoles != null || !fplUserRoles.isEmpty()) {
                for (UserRole userRole : fplUserRoles) {
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


      if (crpProgram.getLeaders() != null) {
        for (CrpProgramLeader crpProgramLeader : crpProgram.getLeaders()) {
          if (crpProgramLeader.getId() == null) {
            crpProgramLeader.setActive(true);
            crpProgramLeader.setCrpProgram(crpProgram);
            crpProgramLeader.setCreatedBy(this.getCurrentUser());
            crpProgramLeader.setModifiedBy(this.getCurrentUser());
            crpProgramLeader.setModificationJustification("");
            crpProgramLeader.setActiveSince(new Date());
            crpProgramLeader.setManager(false);
            CrpProgram crpProgramPrevLeaders = crpProgramManager.getCrpProgramById(crpProgram.getId());
            if (crpProgramPrevLeaders.getCrpProgramLeaders().stream()
              .filter(c -> c.isActive() && c.getCrpProgram().equals(crpProgramLeader.getCrpProgram())
                && c.getUser().equals(crpProgramLeader.getUser()))
              .collect(Collectors.toList()).isEmpty()) {

              for (LiaisonInstitution liasonInstitution : crpProgramPrevLeaders.getLiaisonInstitutions()) {

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
              this.notifyNewUserCreated(userRole.getUser());
              // Notifiy user been asigned Program Leader to Flagship
              this.notifyRoleFlagshipAssigned(userRole.getUser(), userRole.getRole(), crpProgram);
            }

            this.addCrpUser(user);
          }
        }
      }
    }
  }


  private void programManagerData() {
    for (CrpProgram crpProgram : flagshipsPrograms) {
      CrpProgram crpProgramPrev = crpProgramManager.getCrpProgramById(crpProgram.getId());
      for (CrpProgramLeader leaderPreview : crpProgramPrev.getCrpProgramLeaders().stream()
        .filter(c -> c.isActive() && c.isManager()).collect(Collectors.toList())) {

        if (crpProgram.getManagers() == null) {
          crpProgram.setManagers(new ArrayList<>());
        }
        if (!crpProgram.getManagers().contains(leaderPreview)) {
          crpProgramLeaderManager.deleteCrpProgramLeader(leaderPreview.getId());


          User user = userManager.getUser(leaderPreview.getUser().getId());


          List<CrpProgramLeader> existsUserLeader = user.getCrpProgramLeaders().stream()
            .filter(u -> u.isActive() && u.getCrpProgram().getCrp().getId().longValue() == loggedCrp.getId().longValue()
              && u.getCrpProgram().getProgramType() == crpProgramPrev.getProgramType())
            .collect(Collectors.toList());


          if (existsUserLeader == null || existsUserLeader.isEmpty()) {

            if (crpProgramPrev.getProgramType() == ProgramType.FLAGSHIP_PROGRAM_TYPE.getValue()) {
              List<UserRole> fplUserRoles =
                user.getUserRoles().stream().filter(ur -> ur.getRole().equals(fpmRole)).collect(Collectors.toList());
              if (fplUserRoles != null || !fplUserRoles.isEmpty()) {
                for (UserRole userRole : fplUserRoles) {
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


      if (crpProgram.getManagers() != null) {
        for (CrpProgramLeader crpProgramLeader : crpProgram.getManagers()) {
          if (crpProgramLeader.getId() == null) {
            crpProgramLeader.setActive(true);
            crpProgramLeader.setCrpProgram(crpProgram);
            crpProgramLeader.setCreatedBy(this.getCurrentUser());
            crpProgramLeader.setModifiedBy(this.getCurrentUser());
            crpProgramLeader.setModificationJustification("");
            crpProgramLeader.setManager(true);

            crpProgramLeader.setActiveSince(new Date());
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
              this.notifyNewUserCreated(userRole.getUser());
              // Notifiy user been asigned Program Leader to Flagship
              this.notifyRoleFlagshipAssigned(userRole.getUser(), userRole.getRole(), crpProgram);
            }

            this.addCrpUser(user);
          }
        }
      }
    }
  }


  private void programsData() {
    List<CrpProgram> fgProgramsRewiev =
      crpProgramManager.findCrpProgramsByType(loggedCrp.getId(), ProgramType.FLAGSHIP_PROGRAM_TYPE.getValue());
    // Removing crp flagship program type
    if (fgProgramsRewiev != null) {
      for (CrpProgram crpProgram : fgProgramsRewiev) {
        if (!flagshipsPrograms.contains(crpProgram)) {
          CrpProgram crpProgramBD = crpProgramManager.getCrpProgramById(crpProgram.getId());
          if (crpProgramBD.getCrpProgramLeaders().stream().filter(c -> c.isActive()).collect(Collectors.toList())
            .isEmpty()) {
            for (LiaisonInstitution institution : crpProgram.getLiaisonInstitutions().stream().filter(c -> c.isActive())
              .collect(Collectors.toList())) {
              liaisonInstitutionManager.deleteLiaisonInstitution(institution.getId());
            }

            crpProgramManager.deleteCrpProgram(crpProgram.getId());
          }

        }
      }
    }
    // Add crp flagship program type
    for (CrpProgram crpProgram : flagshipsPrograms) {
      if (crpProgram.getId() == null) {
        crpProgram.setCrp(loggedCrp);
        crpProgram.setActive(true);
        crpProgram.setCreatedBy(this.getCurrentUser());
        crpProgram.setModifiedBy(this.getCurrentUser());
        crpProgram.setModificationJustification("");
        crpProgram.setActiveSince(new Date());
        crpProgramManager.saveCrpProgram(crpProgram);
        LiaisonInstitution liasonInstitution = new LiaisonInstitution();
        liasonInstitution.setAcronym(crpProgram.getAcronym());
        liasonInstitution.setCrp(loggedCrp);
        liasonInstitution.setCrpProgram(crpProgram);
        liasonInstitution.setName(crpProgram.getName());


        liaisonInstitutionManager.saveLiaisonInstitution(liasonInstitution);

      } else {
        CrpProgram crpProgramDb = crpProgramManager.getCrpProgramById(crpProgram.getId());
        crpProgram.setCrp(loggedCrp);
        crpProgram.setActive(true);
        crpProgram.setCreatedBy(crpProgramDb.getCreatedBy());
        crpProgram.setModifiedBy(this.getCurrentUser());
        crpProgram.setModificationJustification("");
        crpProgram.setActiveSince(crpProgramDb.getActiveSince());
        crpProgramManager.saveCrpProgram(crpProgram);
        for (LiaisonInstitution liasonInstitution : crpProgram.getLiaisonInstitutions()) {
          liasonInstitution.setAcronym(crpProgram.getAcronym());
          liasonInstitution.setName(crpProgram.getName());
          liaisonInstitutionManager.saveLiaisonInstitution(liasonInstitution);

        }

      }

    }
  }


  @Override
  public String save() {
    if (this.hasPermission("*")) {

      this.pmuRoleData();
      this.programsData();
      this.programLeaderData();
      this.programManagerData();

      CrpParameter parameter = null;
      if (parameters.size() == 0) {
        parameter = new CrpParameter();
        parameter.setActive(true);
        parameter.setCrp(loggedCrp);
        parameter.setKey(APConstants.CRP_HAS_REGIONS);

      } else {
        parameter = parameters.get(0);
      }
      parameter.setValue(loggedCrp.isHasRegions() + "");
      parameter.setCreatedBy(this.getCurrentUser());
      parameter.setModifiedBy(this.getCurrentUser());
      parameter.setModificationJustification("");
      parameter.setActiveSince(new Date());
      crpParameterManager.saveCrpParameter(parameter);
      this.getSession().put(parameter.getKey(), parameter.getValue());

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
            crpProgramManager.deleteCrpProgram(crpProgram.getId());
          }
        }
      }

      Collection<String> messages = this.getActionMessages();
      if (!this.getInvalidFields().isEmpty()) {

        this.setActionMessages(null);
        // this.addActionMessage(Map.toString(this.getInvalidFields().toArray()));
        List<String> keys = new ArrayList<String>(this.getInvalidFields().keySet());

        for (String key : keys) {

          this.addActionMessage(key + ": " + this.getInvalidFields().get(key));
        }


        // this.addActionWarning(this.getText("saving.saved") + Arrays.toString(this.getInvalidFields().toArray()));
      } else {
        this.addActionMessage("message:" + this.getText("saving.saved"));
      }
      messages = this.getActionMessages();
      return SUCCESS;
    } else {
      return NOT_AUTHORIZED;
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

  public void setLoggedCrp(Crp loggedCrp) {
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
