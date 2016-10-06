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
import org.cgiar.ccafs.marlo.utils.SendMail;

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

/**
 * This action is part of the CRP admin backend.
 * 
 * @author Christian Garcia
 */
public class CrpAdminManagmentAction extends BaseAction {

  private static final long serialVersionUID = 3355662668874414548L;

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

  private List<CrpParameter> parameters;
  private CrpProgramLeaderManager crpProgramLeaderManager;
  private LiaisonUserManager liaisonUserManager;
  private LiaisonInstitutionManager liaisonInstitutionManager;
  private UserManager userManager;
  private Role fplRole;

  // Util
  private SendMail sendMail;

  @Inject
  public CrpAdminManagmentAction(APConfig config, RoleManager roleManager, UserRoleManager userRoleManager,
    CrpProgramManager crpProgramManager, CrpManager crpManager, CrpParameterManager crpParameterManager,
    CrpProgramLeaderManager crpProgramLeaderManager, UserManager userManager, SendMail sendMail,
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


  public Crp getLoggedCrp() {
    return loggedCrp;
  }


  public long getPmuRol() {
    return pmuRol;
  }


  public Role getRolePmu() {
    return rolePmu;
  }

  /**
   * This method notify the user that is been assigned as Program Leader for an specific Flagship
   * 
   * @param user userAssigned is the user been assigned
   * @param role is the role(Program Leader)
   * @param crpProgram is the Flagship where is assigned
   */
  private void notifyRoleFlagshipAssigned(User userAssigned, Role role, CrpProgram crpProgram) {
    String flasgshipRole = this.getText("programManagement.flagship.role");
    String flasgshipRoleAcronym = this.getText("programManagement.flagship.role.acronym");

    userAssigned = userManager.getUser(userAssigned.getId());
    StringBuilder message = new StringBuilder();
    // Building the Email message:
    message.append(this.getText("email.dear", new String[] {userAssigned.getFirstName()}));
    message.append(this.getText("email.flagship.assigned",
      new String[] {flasgshipRole, crpProgram.getName(), crpProgram.getAcronym()}));
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
      this.getText("email.flagship.assigned.subject", new String[] {loggedCrp.getName(), crpProgram.getAcronym()}),
      message.toString(), null, null, null, true);

  }

  private void notifyRoleFlagshipUnassigned(User userAssigned, Role role, CrpProgram crpProgram) {
    String flasgshipRole = this.getText("programManagement.flagship.role");
    String flasgshipRoleAcronym = this.getText("programManagement.flagship.role.acronym");

    userAssigned = userManager.getUser(userAssigned.getId());
    StringBuilder message = new StringBuilder();
    // Building the Email message:
    message.append(this.getText("email.dear", new String[] {userAssigned.getFirstName()}));
    message.append(this.getText("email.flagship.unassigned",
      new String[] {flasgshipRole, crpProgram.getName(), crpProgram.getAcronym()}));
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
      this.getText("email.flagship.unassigned.subject", new String[] {loggedCrp.getName(), crpProgram.getAcronym()}),
      message.toString(), null, null, null, true);

  }


  /**
   * This method notify the user that is been assigned as Program Leader for an specific Regional Program
   * 
   * @param userAssigned is the user been assigned
   * @param role is the role(Program Management)
   */
  private void notifyRoleProgramManagementAssigned(User userAssigned, Role role) {

    String managementRole = this.getText("programManagement.role");
    String managementRoleAcronym = this.getText("programManagement.role.acronym");

    userAssigned = userManager.getUser(userAssigned.getId());
    StringBuilder message = new StringBuilder();
    // Building the Email message:
    message.append(this.getText("email.dear", new String[] {userAssigned.getFirstName()}));
    message
      .append(this.getText("email.programManagement.assigned", new String[] {managementRole, loggedCrp.getName()}));
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
            this.getInvalidFields().put("loggedCrp.programManagmenTeam[" + i + "].id", "PMU, can not be deleted");

          }


        }
        if (deletePmu) {
          userRoleManager.deleteUserRole(userRole.getId());
          this.notifyRoleProgramManagementUnassigned(userRole.getUser(), userRole.getRole());
        }

        // Notifiy user been unassigned to Program Management


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
    // Get the Flagship list of this crp
    flagshipsPrograms = loggedCrp.getCrpPrograms().stream()


      // flagshipsPrograms = crpProgramManager.findAll().stream()
      .filter(c -> c.getProgramType() == ProgramType.FLAGSHIP_PROGRAM_TYPE.getValue() && c.isActive())
      .collect(Collectors.toList());


    for (CrpProgram crpProgram : flagshipsPrograms) {
      crpProgram
        .setLeaders(crpProgram.getCrpProgramLeaders().stream().filter(c -> c.isActive()).collect(Collectors.toList()));
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
      for (CrpProgramLeader leaderPreview : crpProgramPrev.getCrpProgramLeaders().stream().filter(c -> c.isActive())
        .collect(Collectors.toList())) {

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
            .filter(u -> u.isActive() && u.getCrpProgram().getProgramType() == crpProgramPrev.getProgramType())
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
        this.addActionMessage(this.getText("saving.saved"));
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

  public void setLoggedCrp(Crp loggedCrp) {
    this.loggedCrp = loggedCrp;
  }


  public void setPmuRol(long pmuRol) {
    this.pmuRol = pmuRol;
  }

  public void setRolePmu(Role rolePmu) {
    this.rolePmu = rolePmu;
  }

  @Override
  public void validate() {
    Gson gson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();
    if (save) {
      HashMap<String, String> error = new HashMap<>();
      if (flagshipsPrograms == null || flagshipsPrograms.isEmpty()) {

        error.put("flagshipsPrograms", "Please add a Flagship");
        // invalidFields.add(gson.toJson(gson));
      }
      this.setInvalidFields(error);
    }
  }
}
