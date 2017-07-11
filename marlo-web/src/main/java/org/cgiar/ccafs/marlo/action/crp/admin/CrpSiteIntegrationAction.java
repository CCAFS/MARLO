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
import org.cgiar.ccafs.marlo.data.manager.CrpSitesLeaderManager;
import org.cgiar.ccafs.marlo.data.manager.CrpUserManager;
import org.cgiar.ccafs.marlo.data.manager.CrpsSiteIntegrationManager;
import org.cgiar.ccafs.marlo.data.manager.LocElementManager;
import org.cgiar.ccafs.marlo.data.manager.RoleManager;
import org.cgiar.ccafs.marlo.data.manager.UserManager;
import org.cgiar.ccafs.marlo.data.manager.UserRoleManager;
import org.cgiar.ccafs.marlo.data.model.Crp;
import org.cgiar.ccafs.marlo.data.model.CrpProgramCountry;
import org.cgiar.ccafs.marlo.data.model.CrpSitesLeader;
import org.cgiar.ccafs.marlo.data.model.CrpUser;
import org.cgiar.ccafs.marlo.data.model.CrpsSiteIntegration;
import org.cgiar.ccafs.marlo.data.model.LocElement;
import org.cgiar.ccafs.marlo.data.model.Role;
import org.cgiar.ccafs.marlo.data.model.User;
import org.cgiar.ccafs.marlo.data.model.UserRole;
import org.cgiar.ccafs.marlo.security.Permission;
import org.cgiar.ccafs.marlo.utils.APConfig;
import org.cgiar.ccafs.marlo.utils.SendMailS;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import com.google.inject.Inject;
import org.apache.commons.lang3.RandomStringUtils;

/**
 * @author Hermes Jiménez - CIAT/CCAFS
 */
public class CrpSiteIntegrationAction extends BaseAction {


  private static final long serialVersionUID = 1323996683605051647L;

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

  private CrpManager crpManager;
  private LocElementManager locElementManager;
  private CrpsSiteIntegrationManager crpsSiteIntegrationManager;
  private CrpSitesLeaderManager crpSitesLeaderManager;
  private RoleManager roleManager;
  private UserRoleManager userRoleManager;
  private UserManager userManager;
  private CrpUserManager crpUserManager;
  private Crp loggedCrp;
  private List<LocElement> countriesList;
  private Long slRoleid;

  private Role slRole;

  // Util
  private SendMailS sendMail;

  @Inject
  public CrpSiteIntegrationAction(APConfig config, CrpManager crpManager, LocElementManager locElementManager,
    CrpsSiteIntegrationManager crpsSiteIntegrationManager, CrpSitesLeaderManager crpSitesLeaderManager,
    RoleManager roleManager, UserRoleManager userRoleManager, UserManager userManager, SendMailS sendMail,
    CrpUserManager crpUserManager) {
    super(config);
    this.crpManager = crpManager;
    this.locElementManager = locElementManager;
    this.crpsSiteIntegrationManager = crpsSiteIntegrationManager;
    this.crpSitesLeaderManager = crpSitesLeaderManager;
    this.roleManager = roleManager;
    this.userRoleManager = userRoleManager;
    this.userManager = userManager;
    this.sendMail = sendMail;
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

  public List<LocElement> getCountriesList() {
    return countriesList;
  }

  public Crp getLoggedCrp() {
    return loggedCrp;
  }

  public Role getSlRole() {
    return slRole;
  }

  public Long getSlRoleid() {
    return slRoleid;
  }

  private void loadData() {
    if (loggedCrp.getCrpsSitesIntegrations() != null) {
      loggedCrp.setSiteIntegrations(new ArrayList<CrpsSiteIntegration>(loggedCrp.getCrpsSitesIntegrations().stream()
        .filter(si -> si.isActive() && si.getCrp().equals(loggedCrp)).collect(Collectors.toList())));


      for (int i = 0; i < loggedCrp.getSiteIntegrations().size(); i++) {

        CrpsSiteIntegration siteInt = loggedCrp.getSiteIntegrations().get(i);
        if (siteInt.isRegional()) {
          loggedCrp.getSiteIntegrations().get(i).setProgramName(new ArrayList<String>());
          for (CrpProgramCountry crpProgramCountry : siteInt.getLocElement().getCrpProgramCountries().stream()
            .filter(pc -> pc.isActive() && pc.getCrpProgram().getCrp().equals(loggedCrp))
            .collect(Collectors.toList())) {

            loggedCrp.getSiteIntegrations().get(i).getProgramName().add(crpProgramCountry.getCrpProgram().getAcronym());
          }
        }

        loggedCrp.getSiteIntegrations().get(i)
          .setSiteLeaders(new ArrayList<CrpSitesLeader>(loggedCrp.getSiteIntegrations().get(i).getCrpSitesLeaders()
            .stream().filter(sl -> sl.isActive()).collect(Collectors.toList())));
      }
    }
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
        inputStream = this.getClass().getResourceAsStream("/custom/Introduction_To_MARLO_v2.1.pdf");
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
   * This method notify the user that is been assigned as Site Leaders for an specific Country
   * 
   * @param userAssigned is the user been assigned
   * @param role is the role(Program Leader)
   * @param crpsSiteIntegration is the Site where the user is set
   */
  private void notifyRoleAssigned(User userAssigned, Role role, CrpsSiteIntegration crpsSiteIntegration) {
    String siteRole = this.getText("siteIntegration.leader");
    String siteRoleAcronym = this.getText("siteIntegration.leader.acronym");


    userAssigned = userManager.getUser(userAssigned.getId());
    StringBuilder message = new StringBuilder();
    // Building the Email message:
    message.append(this.getText("email.dear", new String[] {userAssigned.getFirstName()}));
    message.append(this.getText("email.siteIntegration.assigned", new String[] {siteRole,
      crpsSiteIntegration.getLocElement().getName(), crpsSiteIntegration.getLocElement().getIsoAlpha2()}));
    message.append(this.getText("email.support"));
    message.append(this.getText("email.getStarted"));
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
      this.getText("email.siteIntegration.assigned.subject",
        new String[] {loggedCrp.getName(), siteRoleAcronym, crpsSiteIntegration.getLocElement().getName()}),
      message.toString(), null, null, null, true);
  }


  @Override
  public void prepare() throws Exception {
    loggedCrp = (Crp) this.getSession().get(APConstants.SESSION_CRP);
    loggedCrp = crpManager.getCrpById(loggedCrp.getId());
    if (this.getSession().containsKey(APConstants.CRP_SL_ROLE)) {
      slRoleid = Long.parseLong((String) this.getSession().get(APConstants.CRP_SL_ROLE));
      slRole = roleManager.getRoleById(slRoleid);
    }

    this.loadData();

    countriesList = locElementManager.findAll().stream().filter(le -> le.getLocElementType().getId() == 2)
      .collect(Collectors.toList());
    Collections.sort(countriesList, (lc1, lc2) -> lc1.getName().compareTo(lc2.getName()));


    String params[] = {loggedCrp.getAcronym()};
    this.setBasePermission(this.getText(Permission.CRP_ADMIN_BASE_PERMISSION, params));

    if (this.isHttpPost()) {
      loggedCrp.getSiteIntegrations().clear();
    }

  }

  @Override
  public String save() {

    if (this.hasPermission("*")) {

      this.siteIntegrationPreviusData();
      this.siteIntegrationNewData();
      this.loadData();

      Collection<String> messages = this.getActionMessages();
      if (!messages.isEmpty()) {
        String validationMessage = messages.iterator().next();
        this.setActionMessages(null);
        this.addActionWarning(this.getText("saving.saved") + validationMessage);
      } else {
        this.addActionMessage(this.getText("saving.saved"));
      }
      messages = this.getActionMessages();
      return SUCCESS;
    } else {
      return NOT_AUTHORIZED;
    }
  }

  public void setCountriesList(List<LocElement> countriesList) {
    this.countriesList = countriesList;
  }


  public void setLoggedCrp(Crp loggedCrp) {
    this.loggedCrp = loggedCrp;
  }

  public void setSlRole(Role slRole) {
    this.slRole = slRole;
  }

  public void setSlRoleid(Long slRoleid) {
    this.slRoleid = slRoleid;
  }


  private void siteIntegrationNewData() {

    for (CrpsSiteIntegration siteIntegration : loggedCrp.getSiteIntegrations()) {
      if (siteIntegration.getId() == null) {
        LocElement locElement =
          locElementManager.getLocElementByISOCode(siteIntegration.getLocElement().getIsoAlpha2());

        siteIntegration.setLocElement(locElement);
        siteIntegration.setCrp(loggedCrp);
        siteIntegration.setActive(true);
        siteIntegration.setModifiedBy(this.getCurrentUser());
        siteIntegration.setCreatedBy(this.getCurrentUser());
        siteIntegration.setModificationJustification("");
        siteIntegration.setActiveSince(new Date());

        locElement.setIsSiteIntegration(true);
        locElementManager.saveLocElement(locElement);

        Long newSiteIntegrationId = crpsSiteIntegrationManager.saveCrpsSiteIntegration(siteIntegration);

        if (siteIntegration.getSiteLeaders() != null) {
          for (CrpSitesLeader sitesLeader : siteIntegration.getSiteLeaders()) {
            User userSiteLeader = userManager.getUser(sitesLeader.getUser().getId());
            CrpsSiteIntegration crpSiteIntegration =
              crpsSiteIntegrationManager.getCrpsSiteIntegrationById(newSiteIntegrationId);

            sitesLeader.setCrpsSiteIntegration(crpSiteIntegration);
            sitesLeader.setUser(userSiteLeader);
            sitesLeader.setActive(true);
            sitesLeader.setModifiedBy(this.getCurrentUser());
            sitesLeader.setCreatedBy(this.getCurrentUser());
            sitesLeader.setModificationJustification("");
            sitesLeader.setActiveSince(new Date());
            crpSitesLeaderManager.saveCrpSitesLeader(sitesLeader);

            UserRole userRole = new UserRole(slRole, userSiteLeader);
            if (!userSiteLeader.getUserRoles().contains(userRole)) {
              userRoleManager.saveUserRole(userRole);
              this.addCrpUser(userRole.getUser());
              this.notifyNewUserCreated(userRole.getUser());
              // this.notifyRoleAssigned(userSiteLeader, userRole.getRole(), sitesLeader.getCrpsSiteIntegration());
            }
          }
        }
      } else {
        if (siteIntegration.getSiteLeaders() != null) {
          for (CrpSitesLeader sitesLeader : siteIntegration.getSiteLeaders()) {
            if (sitesLeader.getId() == null) {
              User userSiteLeader = userManager.getUser(sitesLeader.getUser().getId());
              CrpsSiteIntegration crpSiteIntegration =
                crpsSiteIntegrationManager.getCrpsSiteIntegrationById(siteIntegration.getId());

              sitesLeader.setCrpsSiteIntegration(crpSiteIntegration);
              sitesLeader.setUser(userSiteLeader);
              sitesLeader.setActive(true);
              sitesLeader.setModifiedBy(this.getCurrentUser());
              sitesLeader.setCreatedBy(this.getCurrentUser());
              sitesLeader.setModificationJustification("");
              sitesLeader.setActiveSince(new Date());
              crpSitesLeaderManager.saveCrpSitesLeader(sitesLeader);

              UserRole userRole = new UserRole(slRole, userSiteLeader);
              if (!userSiteLeader.getUserRoles().contains(userRole)) {
                userRoleManager.saveUserRole(userRole);
                this.addCrpUser(userSiteLeader);
                this.notifyNewUserCreated(userRole.getUser());
                // this.notifyRoleAssigned(userSiteLeader, userRole.getRole(), sitesLeader.getCrpsSiteIntegration());
              }
            }
          }
        }
      }
    }
  }

  private void siteIntegrationPreviusData() {
    List<CrpsSiteIntegration> siteIntegrationPrew;

    if (crpsSiteIntegrationManager.findAll() != null) {
      siteIntegrationPrew = crpsSiteIntegrationManager.findAll().stream().filter(si -> si.getCrp().equals(loggedCrp))
        .collect(Collectors.toList());

      for (CrpsSiteIntegration crpsSiteIntegration : siteIntegrationPrew) {

        if (!loggedCrp.getSiteIntegrations().contains(crpsSiteIntegration)) {
          for (CrpSitesLeader crpSitesLeader : crpsSiteIntegration.getCrpSitesLeaders()) {

            crpSitesLeaderManager.deleteCrpSitesLeader(crpSitesLeader.getId());
            User user = userManager.getUser(crpSitesLeader.getUser().getId());

            List<CrpSitesLeader> existsUserLeader =
              user.getCrpSitesLeaders().stream().filter(u -> u.isActive()).collect(Collectors.toList());

            if (existsUserLeader == null || existsUserLeader.isEmpty()) {

              if (crpSitesLeader.getCrpsSiteIntegration().equals(crpsSiteIntegration)) {
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
          LocElement locElement = crpsSiteIntegration.getLocElement();
          locElement.setIsSiteIntegration(true);
          locElementManager.saveLocElement(locElement);
          crpsSiteIntegrationManager.deleteCrpsSiteIntegration(crpsSiteIntegration.getId());
        } else {
          if (crpsSiteIntegration.getCrpSitesLeaders() != null) {
            CrpsSiteIntegration siteIntegration = loggedCrp.getSiteIntegrations().stream()
              .filter(sl -> sl.equals(crpsSiteIntegration)).collect(Collectors.toList()).get(0);
            for (CrpSitesLeader crpSitesLeader : crpsSiteIntegration.getCrpSitesLeaders()) {
              if (siteIntegration.getSiteLeaders() == null) {

                crpSitesLeaderManager.deleteCrpSitesLeader(crpSitesLeader.getId());
                User user = userManager.getUser(crpSitesLeader.getUser().getId());

                List<CrpSitesLeader> existsUserLeader =
                  user.getCrpSitesLeaders().stream().filter(u -> u.isActive()).collect(Collectors.toList());

                if (existsUserLeader == null || existsUserLeader.isEmpty()) {

                  if (crpSitesLeader.getCrpsSiteIntegration().equals(crpsSiteIntegration)) {
                    List<UserRole> slUserRoles = user.getUserRoles().stream().filter(ur -> ur.getRole().equals(slRole))
                      .collect(Collectors.toList());
                    if (slUserRoles != null) {
                      for (UserRole userRole : slUserRoles) {
                        userRoleManager.deleteUserRole(userRole.getId());

                      }
                      this.checkCrpUserByRole(user);
                    }
                  }
                }

              } else if (!siteIntegration.getSiteLeaders().contains(crpSitesLeader)) {

                crpSitesLeaderManager.deleteCrpSitesLeader(crpSitesLeader.getId());
                User user = userManager.getUser(crpSitesLeader.getUser().getId());

                List<CrpSitesLeader> existsUserLeader =
                  user.getCrpSitesLeaders().stream().filter(u -> u.isActive()).collect(Collectors.toList());

                if (existsUserLeader == null || existsUserLeader.isEmpty()) {

                  if (crpSitesLeader.getCrpsSiteIntegration().equals(crpsSiteIntegration)) {
                    List<UserRole> slUserRoles = user.getUserRoles().stream().filter(ur -> ur.getRole().equals(slRole))
                      .collect(Collectors.toList());
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
          }
        }
      }
    }
  }
}
