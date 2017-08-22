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
import org.cgiar.ccafs.marlo.data.manager.CrpProgramCountryManager;
import org.cgiar.ccafs.marlo.data.manager.CrpProgramLeaderManager;
import org.cgiar.ccafs.marlo.data.manager.CrpProgramManager;
import org.cgiar.ccafs.marlo.data.manager.CrpSitesLeaderManager;
import org.cgiar.ccafs.marlo.data.manager.CrpUserManager;
import org.cgiar.ccafs.marlo.data.manager.CrpsSiteIntegrationManager;
import org.cgiar.ccafs.marlo.data.manager.CustomParameterManager;
import org.cgiar.ccafs.marlo.data.manager.LiaisonInstitutionManager;
import org.cgiar.ccafs.marlo.data.manager.LiaisonUserManager;
import org.cgiar.ccafs.marlo.data.manager.LocElementManager;
import org.cgiar.ccafs.marlo.data.manager.RoleManager;
import org.cgiar.ccafs.marlo.data.manager.UserManager;
import org.cgiar.ccafs.marlo.data.manager.UserRoleManager;
import org.cgiar.ccafs.marlo.data.model.Crp;
import org.cgiar.ccafs.marlo.data.model.CrpProgram;
import org.cgiar.ccafs.marlo.data.model.CrpProgramCountry;
import org.cgiar.ccafs.marlo.data.model.CrpProgramLeader;
import org.cgiar.ccafs.marlo.data.model.CrpSitesLeader;
import org.cgiar.ccafs.marlo.data.model.CrpUser;
import org.cgiar.ccafs.marlo.data.model.CrpsSiteIntegration;
import org.cgiar.ccafs.marlo.data.model.CustomParameter;
import org.cgiar.ccafs.marlo.data.model.LiaisonInstitution;
import org.cgiar.ccafs.marlo.data.model.LiaisonUser;
import org.cgiar.ccafs.marlo.data.model.LocElement;
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
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import com.google.inject.Inject;
import org.apache.commons.lang3.RandomStringUtils;

/**
 * This action is part of the CRP admin backend.
 * 
 * @author Christian Garcia
 */
public class CrpProgamRegionsAction extends BaseAction {

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
  private CustomParameterManager crpParameterManager;
  private CrpUserManager crpUserManager;
  // Variables
  private Crp loggedCrp;
  private Role rolePmu;
  private long pmuRol;
  private List<LocElement> countriesList;
  private List<CrpProgram> regionsPrograms;
  private List<CustomParameter> parameters;
  private LocElementManager locElementManger;
  private CrpProgramLeaderManager crpProgramLeaderManager;
  private CrpProgramCountryManager crpProgramCountryManager;
  private CrpSitesLeaderManager crpSitesLeaderManager;
  private CrpsSiteIntegrationManager crpsSiteIntegrationManager;
  private UserManager userManager;
  private LiaisonUserManager liaisonUserManager;
  private LiaisonInstitutionManager liaisonInstitutionManager;

  private Role rplRole;
  private Role rpmRole;


  private Long slRoleid;


  private Role slRole;

  // Util
  private SendMailS sendMail;

  @Inject
  public CrpProgamRegionsAction(APConfig config, RoleManager roleManager, UserRoleManager userRoleManager,
    CrpProgramManager crpProgramManager, CrpManager crpManager, CustomParameterManager crpParameterManager,
    CrpProgramLeaderManager crpProgramLeaderManager, UserManager userManager, LocElementManager locElementManger,
    CrpProgramCountryManager crpProgramCountryManager, CrpSitesLeaderManager crpSitesLeaderManager,
    CrpsSiteIntegrationManager crpsSiteIntegrationManager, SendMailS sendMail, LiaisonUserManager liaisonUserManager,
    LiaisonInstitutionManager liaisonInstitutionManager, CrpUserManager crpUserManager) {
    super(config);
    this.roleManager = roleManager;
    this.userRoleManager = userRoleManager;
    this.crpManager = crpManager;
    this.crpProgramManager = crpProgramManager;
    this.crpParameterManager = crpParameterManager;
    this.userManager = userManager;
    this.locElementManger = locElementManger;
    this.crpProgramLeaderManager = crpProgramLeaderManager;
    this.crpProgramCountryManager = crpProgramCountryManager;
    this.crpSitesLeaderManager = crpSitesLeaderManager;
    this.crpsSiteIntegrationManager = crpsSiteIntegrationManager;
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

  private void deleteSiteIntegration(CrpProgramCountry crpProgramCountry) {
    boolean hasCountry = false;
    Crp crp = crpManager.getCrpById(loggedCrp.getId());
    List<CrpProgram> crpPrograms = crp.getCrpPrograms().stream()
      .filter(cp -> cp.isActive() && cp.getProgramType() == ProgramType.REGIONAL_PROGRAM_TYPE.getValue())
      .collect(Collectors.toList());

    for (CrpProgram crpProgram : crpPrograms) {
      List<CrpProgramCountry> countries = crpProgram.getCrpProgramCountries().stream()
        .filter(pc -> pc.isActive() && pc.getLocElement().getId() == crpProgramCountry.getLocElement().getId())
        .collect(Collectors.toList());
      if (!countries.isEmpty()) {
        hasCountry = true;
        break;
      } else {
        hasCountry = false;
      }
    }


    if (!hasCountry) {
      for (CrpsSiteIntegration siteIntegration : crpProgramCountry.getLocElement().getCrpsSitesIntegrations().stream()
        .filter(si -> si.getCrp().equals(loggedCrp) && si.isActive()).collect(Collectors.toList())) {


        for (CrpSitesLeader crpSitesLeader : siteIntegration.getCrpSitesLeaders().stream().filter(sl -> sl.isActive())
          .collect(Collectors.toList())) {

          crpSitesLeaderManager.deleteCrpSitesLeader(crpSitesLeader.getId());
          User user = userManager.getUser(crpSitesLeader.getUser().getId());

          List<CrpSitesLeader> existsUserLeader =
            user.getCrpSitesLeaders().stream().filter(u -> u.isActive()).collect(Collectors.toList());

          if (existsUserLeader == null || existsUserLeader.isEmpty()) {

            if (crpSitesLeader.getCrpsSiteIntegration().equals(siteIntegration)) {
              List<UserRole> slUserRoles =
                user.getUserRoles().stream().filter(ur -> ur.getRole().equals(slRole)).collect(Collectors.toList());
              if (slUserRoles != null) {
                for (UserRole userRole : slUserRoles) {
                  // site leader aqui notificar
                  userRoleManager.deleteUserRole(userRole.getId());
                }
                this.checkCrpUserByRole(user);
              }
            }
          }
        }
        LocElement element = siteIntegration.getLocElement();
        element.setIsSiteIntegration(false);
        locElementManger.saveLocElement(element);
        crpsSiteIntegrationManager.deleteCrpsSiteIntegration(siteIntegration.getId());
      }
    }
  }

  private void deleteSiteIntegrationLeader(CrpProgramCountry crpProgramCountry, User user) {

    for (CrpSitesLeader sitesLeader : user.getCrpSitesLeaders().stream().filter(sl -> sl.isActive())
      .collect(Collectors.toList())) {
      Long locElementSL = sitesLeader.getCrpsSiteIntegration().getLocElement().getId();
      Long locElementCP = crpProgramCountry.getLocElement().getId();
      if (locElementSL == locElementCP) {

        boolean hasCountry = false;
        Crp crp = crpManager.getCrpById(loggedCrp.getId());
        List<CrpProgram> crpPrograms = crp.getCrpPrograms().stream()
          .filter(cp -> cp.isActive() && cp.getProgramType() == ProgramType.REGIONAL_PROGRAM_TYPE.getValue())
          .collect(Collectors.toList());

        for (CrpProgram crpProgram : crpPrograms) {
          List<CrpProgramCountry> countries = crpProgram.getCrpProgramCountries().stream()
            .filter(pc -> pc.isActive() && pc.getLocElement().getId() == crpProgramCountry.getLocElement().getId())
            .collect(Collectors.toList());

          List<CrpProgramLeader> leaders = crpProgram.getCrpProgramLeaders().stream()
            .filter(pl -> pl.isActive() && pl.getUser().equals(sitesLeader.getUser())).collect(Collectors.toList());
          if (!countries.isEmpty() && !leaders.isEmpty()) {
            hasCountry = true;
            break;
          } else {
            hasCountry = false;
          }
        }

        if (!hasCountry) {
          crpSitesLeaderManager.deleteCrpSitesLeader(sitesLeader.getId());
        }

        List<CrpSitesLeader> existsSiteLeader =
          user.getCrpSitesLeaders().stream().filter(u -> u.isActive()).collect(Collectors.toList());

        if (existsSiteLeader == null || existsSiteLeader.isEmpty()) {
          List<UserRole> slUserRoles =
            user.getUserRoles().stream().filter(ur -> ur.getRole().equals(slRole)).collect(Collectors.toList());
          if (slUserRoles != null) {
            for (UserRole userRole : slUserRoles) {
              userRoleManager.deleteUserRole(userRole.getId());
            }
            this.checkCrpUserByRole(user);
          }
        }
      }
    }
  }

  public List<LocElement> getCountriesList() {
    return countriesList;
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


  public Role getRplRole() {
    return rplRole;
  }

  public Role getRpmRole() {
    return rpmRole;
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

  /**
   * This method notify the user that is been assigned as Program Leader for an specific Regional Program
   * 
   * @param userAssigned is the user been assigned
   * @param role is the role(Program Leader)
   * @param crpProgram is the Region Program where the user is set
   * @param crpProgramCountries
   */
  private void notifyRoleAssigned(User userAssigned, Role role, CrpProgram crpProgram) {
    userAssigned = userManager.getUser(userAssigned.getId());
    // TO will be the new user
    String toEmail = userAssigned.getEmail();
    // CC will be the user who is making the modification.
    String ccEmail = this.getCurrentUser().getEmail();
    // CC will be also others leaders
    crpProgram = crpProgramManager.getCrpProgramById(crpProgram.getId());
    for (CrpProgramLeader crpProgramLeader : crpProgram.getCrpProgramLeaders().stream().filter(cp -> cp.isActive())
      .collect(Collectors.toList())) {
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

    // BBC will be our gmail notification email.
    String bbcEmails = this.config.getEmailNotification();

    String subject =
      this.getText("email.region.assigned.subject", new String[] {crpProgram.getAcronym(), loggedCrp.getName()});

    userAssigned = userManager.getUser(userAssigned.getId());
    StringBuilder message = new StringBuilder();
    // Building the Email message:
    message.append(this.getText("email.dear", new String[] {userAssigned.getFirstName()}));
    message.append(this.getText("email.region.assigned",
      new String[] {crpProgram.getAcronym(), crpProgram.getName(), loggedCrp.getName()}));
    message.append(this.getText("email.support", new String[] {crpAdmins}));
    message.append(this.getText("email.getStarted"));
    message.append(this.getText("email.bye"));

    sendMail.send(toEmail, ccEmail, bbcEmails, subject, message.toString(), null, null, null, true);

  }

  private void notifyRoleUnassigned(User userAssigned, Role role, CrpProgram crpProgram) {
    userAssigned = userManager.getUser(userAssigned.getId());
    String regionRole = role.getDescription();
    String regionRoleAcronym = this.getText("regionalMapping.CrpProgram.leaders.acronym");
    userAssigned = userManager.getUser(userAssigned.getId());
    StringBuilder message = new StringBuilder();
    // Building the Email message:
    message.append(this.getText("email.dear", new String[] {userAssigned.getFirstName()}));
    message.append(this.getText("email.region.unassigned",
      new String[] {regionRole, crpProgram.getName(), crpProgram.getAcronym()}));
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
    if (role.equals(rplRole)) {
      sendMail.send(toEmail, ccEmail, bbcEmails,
        this.getText("email.region.unassigned.subject", new String[] {loggedCrp.getName(), crpProgram.getAcronym()}),
        message.toString(), null, null, null, true);
    } else {
      sendMail.send(toEmail, ccEmail, bbcEmails, this.getText("email.regionmanager.unassigned.subject",
        new String[] {loggedCrp.getName(), crpProgram.getAcronym()}), message.toString(), null, null, null, true);
    }
  }


  @Override
  public void prepare() throws Exception {

    // Get the Users list that have the pmu role in this crp.
    loggedCrp = (Crp) this.getSession().get(APConstants.SESSION_CRP);
    loggedCrp = crpManager.getCrpById(loggedCrp.getId());
    pmuRol = Long.parseLong((String) this.getSession().get(APConstants.CRP_PMU_ROLE));
    rolePmu = roleManager.getRoleById(pmuRol);
    loggedCrp.setProgramManagmenTeam(new ArrayList<UserRole>(rolePmu.getUserRoles()));
    String params[] = {loggedCrp.getAcronym()};
    if (this.getSession().containsKey(APConstants.CRP_RPL_ROLE)) {
      rplRole = roleManager.getRoleById(Long.parseLong((String) this.getSession().get(APConstants.CRP_RPL_ROLE)));
      rpmRole = roleManager.getRoleById(Long.parseLong((String) this.getSession().get(APConstants.CRP_RPM_ROLE)));
    }

    if (this.getSession().containsKey(APConstants.CRP_SL_ROLE)) {
      slRoleid = Long.parseLong((String) this.getSession().get(APConstants.CRP_SL_ROLE));
      slRole = roleManager.getRoleById(slRoleid);

    }

    List<LocElement> locs =
      locElementManger.findAll().stream().filter(c -> c.getLocElementType().getId() == 2).collect(Collectors.toList());
    Collections.sort(locs, (l1, l2) -> l1.getName().compareTo(l2.getName()));
    countriesList = locs;
    // Get the Flagship list of this crp
    regionsPrograms = loggedCrp.getCrpPrograms().stream()
      .filter(c -> c.getProgramType() == ProgramType.REGIONAL_PROGRAM_TYPE.getValue() && c.isActive())
      .collect(Collectors.toList());

    for (CrpProgram crpProgram : regionsPrograms) {
      crpProgram.setLeaders(crpProgram.getCrpProgramLeaders().stream().filter(c -> c.isActive() && !c.isManager())
        .collect(Collectors.toList()));
      crpProgram.setManagers(crpProgram.getCrpProgramLeaders().stream().filter(c -> c.isActive() && c.isManager())
        .collect(Collectors.toList()));
      List<String> countriesSelected = new ArrayList<>();
      for (CrpProgramCountry crpProgramCountry : crpProgram.getCrpProgramCountries().stream().filter(c -> c.isActive())
        .collect(Collectors.toList())) {
        countriesSelected.add(crpProgramCountry.getLocElement().getIsoAlpha2());
      }
      crpProgram.setSelectedCountries(countriesSelected);

    }

    this.setBasePermission(this.getText(Permission.CRP_ADMIN_BASE_PERMISSION, params));
    if (this.isHttpPost()) {
      loggedCrp.getProgramManagmenTeam().clear();

      regionsPrograms.clear();
    }
  }

  private void programManagerData() {
    for (CrpProgram crpProgram : regionsPrograms) {
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

            if (crpProgramPrev.getProgramType() == ProgramType.REGIONAL_PROGRAM_TYPE.getValue()) {
              List<UserRole> fplUserRoles =
                user.getUserRoles().stream().filter(ur -> ur.getRole().equals(rpmRole)).collect(Collectors.toList());
              if (fplUserRoles != null || !fplUserRoles.isEmpty()) {
                for (UserRole userRole : fplUserRoles) {
                  userRoleManager.deleteUserRole(userRole.getId());
                  userRole.setUser(userManager.getUser(userRole.getUser().getId()));
                  // Notifiy user been unasigned Program Leader to Flagship
                  this.notifyRoleUnassigned(userRole.getUser(), userRole.getRole(), crpProgram);
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

            if (crpProgram.getProgramType() == ProgramType.REGIONAL_PROGRAM_TYPE.getValue()) {
              userRole.setRole(rpmRole);
            }

            if (!user.getUserRoles().contains(userRole)) {
              userRoleManager.saveUserRole(userRole);
              userRole.setUser(userManager.getUser(userRole.getUser().getId()));
              this.notifyNewUserCreated(userRole.getUser());
              // Notifiy user been asigned Program Leader to Flagship
              // New Science officer
              this.notifyRoleAssigned(userRole.getUser(), userRole.getRole(), crpProgram);
            }

            this.addCrpUser(user);
          }
        }
      }
    }
  }


  @Override
  public String save() {
    if (this.hasPermission("*")) {

      List<CrpProgram> rgProgramsRewiev =
        crpProgramManager.findCrpProgramsByType(loggedCrp.getId(), ProgramType.REGIONAL_PROGRAM_TYPE.getValue());
      // Removing crp flagship program type
      if (rgProgramsRewiev != null) {
        for (CrpProgram crpProgram : rgProgramsRewiev) {
          if (!regionsPrograms.contains(crpProgram)) {
            CrpProgram crpProgramBD = crpProgramManager.getCrpProgramById(crpProgram.getId());

            if (crpProgramBD.getCrpProgramLeaders().stream().filter(c -> c.isActive()).collect(Collectors.toList())
              .isEmpty()
              && crpProgramBD.getCrpProgramCountries().stream().filter(c -> c.isActive()).collect(Collectors.toList())
                .isEmpty()) {
              for (LiaisonInstitution institution : crpProgram.getLiaisonInstitutions().stream()
                .filter(c -> c.isActive()).collect(Collectors.toList())) {
                liaisonInstitutionManager.deleteLiaisonInstitution(institution.getId());
              }

              crpProgramManager.deleteCrpProgram(crpProgram.getId());
            }
          }
        }
      }
      // Add crp flagship program type
      for (CrpProgram crpProgram : regionsPrograms) {
        if (crpProgram.getId() == null) {
          crpProgram.setCrp(loggedCrp);
          crpProgram.setActive(true);
          crpProgram.setCreatedBy(this.getCurrentUser());
          crpProgram.setModifiedBy(this.getCurrentUser());
          crpProgram.setModificationJustification("");
          crpProgram.setActiveSince(new Date());
          crpProgram.setProgramType(ProgramType.REGIONAL_PROGRAM_TYPE.getValue());
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
          crpProgram.setProgramType(ProgramType.REGIONAL_PROGRAM_TYPE.getValue());
          crpProgramManager.saveCrpProgram(crpProgram);
          for (LiaisonInstitution liasonInstitution : crpProgram.getLiaisonInstitutions()) {
            liasonInstitution.setAcronym(crpProgram.getAcronym());
            liasonInstitution.setName(crpProgram.getName());
            liaisonInstitutionManager.saveLiaisonInstitution(liasonInstitution);

          }
        }
      }

      for (CrpProgram crpProgram : regionsPrograms) {
        CrpProgram crpProgramPrev = crpProgramManager.getCrpProgramById(crpProgram.getId());
        for (CrpProgramLeader leaderPreview : crpProgramPrev.getCrpProgramLeaders().stream()
          .filter(c -> c.isActive() && !c.isManager()).collect(Collectors.toList())) {

          if (crpProgram.getLeaders() == null) {
            crpProgram.setLeaders(new ArrayList<>());
          }

          if (!crpProgram.getLeaders().contains(leaderPreview)) {
            Set<LiaisonInstitution> liaisonInstitutions = crpProgramPrev.getLiaisonInstitutions();
            for (LiaisonInstitution liaisonInstitution : liaisonInstitutions) {
              List<LiaisonUser> liaisonUsers = liaisonInstitution.getLiaisonUsers().stream()
                .filter(c -> c.getUser().getId().equals(leaderPreview.getUser().getId())).collect(Collectors.toList());
              for (LiaisonUser liaisonUser : liaisonUsers) {
                liaisonUserManager.deleteLiaisonUser(liaisonUser.getId());
              }
            }
            crpProgramLeaderManager.deleteCrpProgramLeader(leaderPreview.getId());
            User user = userManager.getUser(leaderPreview.getUser().getId());

            for (CrpProgramCountry crpProgramCountry : crpProgramPrev.getCrpProgramCountries().stream()
              .filter(pc -> pc.isActive()).collect(Collectors.toList())) {
              this.deleteSiteIntegrationLeader(crpProgramCountry, user);
            }

            List<CrpProgramLeader> existsUserLeader = user.getCrpProgramLeaders().stream()
              .filter(u -> u.isActive() && u.getCrpProgram().getProgramType() == crpProgramPrev.getProgramType())
              .collect(Collectors.toList());

            if (existsUserLeader == null || existsUserLeader.isEmpty()) {

              if (crpProgramPrev.getProgramType() == ProgramType.REGIONAL_PROGRAM_TYPE.getValue()) {
                List<UserRole> fplUserRoles =
                  user.getUserRoles().stream().filter(ur -> ur.getRole().equals(rplRole)).collect(Collectors.toList());
                if (fplUserRoles != null) {
                  for (UserRole userRole : fplUserRoles) {
                    userRoleManager.deleteUserRole(userRole.getId());
                    this.notifyRoleUnassigned(userRole.getUser(), userRole.getRole(), crpProgram);
                  }
                  this.checkCrpUserByRole(user);
                }
              }
            }
          }
        }

        if (crpProgram.getSelectedCountries() != null) {
          CrpProgram crpProgramPrevLeaders = crpProgramManager.getCrpProgramById(crpProgram.getId());

          List<CrpProgramCountry> crpProgramCountriesPreview = crpProgramPrevLeaders.getCrpProgramCountries().stream()
            .filter(c -> c.isActive()).collect(Collectors.toList());
          for (CrpProgramCountry crpProgramCountry : crpProgramCountriesPreview) {
            String alpha2 = crpProgramCountry.getLocElement().getIsoAlpha2();
            if (!crpProgram.getSelectedCountries().contains(alpha2)) {

              crpProgramCountryManager.deleteCrpProgramCountry(crpProgramCountry.getId());
              this.deleteSiteIntegration(crpProgramCountry);
            }
          }

          for (String isoCode : crpProgram.getSelectedCountries()) {
            LocElement locElement = locElementManger.getLocElementByISOCode(isoCode);

            if (crpProgramPrevLeaders.getCrpProgramCountries().stream()
              .filter(c -> c.isActive() && c.getLocElement().getId().equals(locElement.getId()))
              .collect(Collectors.toList()).isEmpty()) {
              CrpProgramCountry crpProgramCountry = new CrpProgramCountry();
              crpProgramCountry.setActive(true);
              crpProgramCountry.setLocElement(locElement);
              crpProgramCountry.setCrpProgram(crpProgram);
              crpProgramCountry.setCreatedBy(this.getCurrentUser());
              crpProgramCountry.setModifiedBy(this.getCurrentUser());
              crpProgramCountry.setModificationJustification("");
              crpProgramCountry.setActiveSince(new Date());
              crpProgramCountryManager.saveCrpProgramCountry(crpProgramCountry);
              // Here is calling to saveSiteLeaderBySiteIntegration
              this.saveSiteIntegration(locElement, crpProgramPrevLeaders);
            }

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
                .filter(
                  c -> c.isActive() && !c.isManager() && c.getCrpProgram().equals(crpProgramLeader.getCrpProgram())
                    && c.getUser().equals(crpProgramLeader.getUser()))
                .collect(Collectors.toList()).isEmpty()) {
                crpProgramLeaderManager.saveCrpProgramLeader(crpProgramLeader);

                for (LiaisonInstitution liasonInstitution : crpProgramPrevLeaders.getLiaisonInstitutions()) {

                  LiaisonUser liaisonUser = new LiaisonUser();
                  liaisonUser.setCrp(loggedCrp);
                  liaisonUser.setLiaisonInstitution(liasonInstitution);
                  liaisonUser.setUser(crpProgramLeader.getUser());
                  liaisonUserManager.saveLiaisonUser(liaisonUser);
                }

                for (CrpProgramCountry crpProgramCountry : crpProgramPrevLeaders.getCrpProgramCountries().stream()
                  .filter(pc -> pc.isActive()).collect(Collectors.toList())) {
                  // Here is called saveSiteLeaderByProgramCountry
                  this.saveSiteLeaderByProgramCountry(crpProgramCountry, crpProgramLeader.getUser());
                }

                // Notify assigned role
                UserRole userRole = new UserRole(rplRole, crpProgramLeader.getUser());
                if (!crpProgramLeader.getUser().getUserRoles().contains(userRole)) {
                  this.notifyNewUserCreated(crpProgramLeader.getUser());
                  // new RPL
                  this.notifyRoleAssigned(crpProgramLeader.getUser(), userRole.getRole(),
                    crpProgramLeader.getCrpProgram());
                }

              }

              User user = userManager.getUser(crpProgramLeader.getUser().getId());
              UserRole userRole = new UserRole();
              userRole.setUser(user);

              if (crpProgramPrevLeaders.getProgramType() == ProgramType.REGIONAL_PROGRAM_TYPE.getValue()) {
                userRole.setRole(rplRole);
              }

              if (!user.getUserRoles().contains(userRole)) {
                userRoleManager.saveUserRole(userRole);
                this.addCrpUser(user);
              }
            }
          }
        }


      }
      this.programManagerData();
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

  private void saveSiteIntegration(LocElement locElement, CrpProgram crpProgram) {
    Crp crp = crpManager.getCrpById(loggedCrp.getId());
    List<CrpsSiteIntegration> siteIntegrations = crp.getCrpsSitesIntegrations().stream()
      .filter(si -> si.isActive() && si.getLocElement().equals(locElement)).collect(Collectors.toList());
    if (siteIntegrations == null || siteIntegrations.isEmpty()) {
      CrpsSiteIntegration crpsSiteIntegration = new CrpsSiteIntegration();
      crpsSiteIntegration.setCrp(loggedCrp);
      crpsSiteIntegration.setLocElement(locElement);
      crpsSiteIntegration.setActive(true);
      crpsSiteIntegration.setModifiedBy(this.getCurrentUser());
      crpsSiteIntegration.setCreatedBy(this.getCurrentUser());
      crpsSiteIntegration.setModificationJustification("");
      crpsSiteIntegration.setActiveSince(new Date());
      crpsSiteIntegration.setRegional(true);

      crpsSiteIntegration = crpsSiteIntegrationManager.saveCrpsSiteIntegration(crpsSiteIntegration);

      locElement.setIsSiteIntegration(true);
      locElementManger.saveLocElement(locElement);

      this.saveSiteLeaderBySiteIntegration(crpProgram, crpsSiteIntegration);


    } else {
      for (CrpsSiteIntegration siteIntegration : siteIntegrations) {
        if (!siteIntegration.isRegional()) {
          siteIntegration.setRegional(true);
          crpsSiteIntegrationManager.saveCrpsSiteIntegration(siteIntegration);
        }
        this.saveSiteLeaderBySiteIntegration(crpProgram, siteIntegration);
      }
    }
  }

  private void saveSiteLeaderByProgramCountry(CrpProgramCountry crpProgramCountry, User user) {

    for (CrpsSiteIntegration siteIntegration : crpProgramCountry.getLocElement().getCrpsSitesIntegrations().stream()
      .filter(si -> si.isActive()).collect(Collectors.toList())) {
      if (siteIntegration.getCrpSitesLeaders().stream().filter(sl -> sl.isActive() && sl.getUser().equals(user))
        .collect(Collectors.toList()).isEmpty()) {
        CrpSitesLeader sitesLeader = new CrpSitesLeader();
        sitesLeader.setCrpsSiteIntegration(siteIntegration);
        sitesLeader.setUser(user);
        sitesLeader.setActive(true);
        sitesLeader.setModifiedBy(this.getCurrentUser());
        sitesLeader.setCreatedBy(this.getCurrentUser());
        sitesLeader.setModificationJustification("");
        sitesLeader.setActiveSince(new Date());
        sitesLeader.setRegional(true);

        List<CrpSitesLeader> siLeaders = null;
        if (siteIntegration.getSiteLeaders() != null) {
          siLeaders = siteIntegration.getSiteLeaders().stream()
            .filter(sl -> sl.isActive() && sl.getUser().equals(sitesLeader.getUser())).collect(Collectors.toList());
        }
        if (siLeaders == null || siLeaders.isEmpty()) {
          crpSitesLeaderManager.saveCrpSitesLeader(sitesLeader);

          UserRole userRole = new UserRole(slRole, user);
          if (!user.getUserRoles().contains(userRole)) {
            userRoleManager.saveUserRole(userRole);
            this.addCrpUser(user);
          }
        }
      }
    }
  }

  private void saveSiteLeaderBySiteIntegration(CrpProgram crpProgram, CrpsSiteIntegration crpSiteIntegration) {
    if (crpProgram.getCrpProgramLeaders() != null) {
      for (CrpProgramLeader programLeader : crpProgram.getCrpProgramLeaders().stream().filter(pl -> pl.isActive())
        .collect(Collectors.toList())) {
        User userSiteLeader = userManager.getUser(programLeader.getUser().getId());

        CrpSitesLeader sitesLeader = new CrpSitesLeader();
        sitesLeader.setCrpsSiteIntegration(crpSiteIntegration);
        sitesLeader.setUser(userSiteLeader);
        sitesLeader.setActive(true);
        sitesLeader.setModifiedBy(this.getCurrentUser());
        sitesLeader.setCreatedBy(this.getCurrentUser());
        sitesLeader.setModificationJustification("");
        sitesLeader.setActiveSince(new Date());
        sitesLeader.setRegional(true);

        List<CrpSitesLeader> siLeaders = null;
        if (crpSiteIntegration.getSiteLeaders() != null) {
          siLeaders = crpSiteIntegration.getSiteLeaders().stream()
            .filter(sl -> sl.isActive() && sl.getUser().equals(sitesLeader.getUser())).collect(Collectors.toList());
        }

        if (siLeaders == null || siLeaders.isEmpty()) {
          crpSitesLeaderManager.saveCrpSitesLeader(sitesLeader);

          UserRole userRole = new UserRole(slRole, userSiteLeader);
          if (!userSiteLeader.getUserRoles().contains(userRole)) {
            userRoleManager.saveUserRole(userRole);
            this.addCrpUser(userRole.getUser());
          }
        }
      }
    }

  }

  public void setCountriesList(List<LocElement> countriesList) {
    this.countriesList = countriesList;
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

  public void setRplRole(Role fplRole) {
    this.rplRole = fplRole;
  }

  public void setRpmRole(Role rpmRole) {
    this.rpmRole = rpmRole;
  }

  @Override
  public void validate() {
    if (save) {
      HashMap<String, String> error = new HashMap<>();

      if (regionsPrograms == null || regionsPrograms.isEmpty()) {

        error.put("list-regionsPrograms",
          this.getText(InvalidFieldsMessages.EMPTYLIST, new String[] {"Regional Programs"}));
        // invalidFields.add(gson.toJson(gson));
      } else {
        int index = 0;
        for (CrpProgram crpProgram : regionsPrograms) {
          if (crpProgram.getLeaders() == null || crpProgram.getLeaders().isEmpty()) {
            error.put("list-regionsPrograms[" + index + "].leaders",
              this.getText(InvalidFieldsMessages.EMPTYLIST, new String[] {"Regional Programs Leaders"}));
          }
          index++;
        }
      }


      this.setInvalidFields(error);
    }
  }
}
