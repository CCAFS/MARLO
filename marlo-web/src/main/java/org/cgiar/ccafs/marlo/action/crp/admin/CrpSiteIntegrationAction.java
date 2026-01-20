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
import org.cgiar.ccafs.marlo.data.manager.CrpSitesLeaderManager;
import org.cgiar.ccafs.marlo.data.manager.CrpUserManager;
import org.cgiar.ccafs.marlo.data.manager.CrpsSiteIntegrationManager;
import org.cgiar.ccafs.marlo.data.manager.GlobalUnitManager;
import org.cgiar.ccafs.marlo.data.manager.LocElementManager;
import org.cgiar.ccafs.marlo.data.manager.RoleManager;
import org.cgiar.ccafs.marlo.data.manager.UserManager;
import org.cgiar.ccafs.marlo.data.manager.UserRoleManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.cgiar.ccafs.marlo.data.model.CrpProgramCountry;
import org.cgiar.ccafs.marlo.data.model.CrpSitesLeader;
import org.cgiar.ccafs.marlo.data.model.CrpUser;
import org.cgiar.ccafs.marlo.data.model.CrpsSiteIntegration;
import org.cgiar.ccafs.marlo.data.model.GlobalUnit;
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.inject.Inject;

import org.apache.commons.lang3.RandomStringUtils;

/**
 * @author Hermes Jiménez - CIAT/CCAFS
 */
public class CrpSiteIntegrationAction extends BaseAction {

  private static final Logger LOG = LoggerFactory.getLogger(CrpSiteIntegrationAction.class);
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

  // GlobalUnit Manager
  private GlobalUnitManager crpManager;

  private LocElementManager locElementManager;

  private CrpsSiteIntegrationManager crpsSiteIntegrationManager;

  private CrpSitesLeaderManager crpSitesLeaderManager;
  private RoleManager roleManager;
  private UserRoleManager userRoleManager;
  private UserManager userManager;
  private CrpUserManager crpUserManager;
  private GlobalUnit loggedCrp;
  private List<LocElement> countriesList;
  private Long slRoleid;
  private Role slRole;
  // Util
  private SendMailS sendMail;

  @Inject
  public CrpSiteIntegrationAction(APConfig config, GlobalUnitManager crpManager, LocElementManager locElementManager,
    CrpsSiteIntegrationManager crpsSiteIntegrationManager, CrpSitesLeaderManager crpSitesLeaderManager,
    RoleManager roleManager, UserRoleManager userRoleManager, UserManager userManager, SendMailS sendMail,
    CrpUserManager crpUserManager) {
    super(config);
    LOG.info("=== CRP SITE INTEGRATION ACTION CONSTRUCTOR CALLED ===");
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


  public GlobalUnit getLoggedCrp() {
    return loggedCrp;
  }

  public Role getSlRole() {
    return slRole;
  }


  public Long getSlRoleid() {
    return slRoleid;
  }

  private void loadData() {
    LOG.info("=== LOAD DATA CALLED - HTTP POST: {} ===", this.isHttpPost());
    
    if (this.isHttpPost()) {
      // Initialize empty list for Struts data binding
      if (loggedCrp.getSiteIntegrations() == null) {
        loggedCrp.setSiteIntegrations(new ArrayList<CrpsSiteIntegration>());
      }
      LOG.info("=== LOAD DATA - INITIALIZED EMPTY LIST FOR BINDING ===");
    } else {
      // Load from database for GET requests
      if (loggedCrp.getCrpsSitesIntegrations() != null) {
        loggedCrp.setSiteIntegrations(new ArrayList<CrpsSiteIntegration>(loggedCrp.getCrpsSitesIntegrations().stream()
          .filter(si -> si.isActive() && si.getCrp().equals(loggedCrp)).collect(Collectors.toList())));
        LOG.info("=== LOAD DATA - LOADED {} INTEGRATIONS FROM DB ===", loggedCrp.getSiteIntegrations().size());


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
        config.getBaseUrl(), user.getEmail(), password, this.getText("email.support", new String[] {crpAdmins})}));
      message.append(this.getText("email.bye"));

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
   * This method notify the user that is been assigned as Site Leaders for an specific CountryOCS
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
    String crp = loggedCrp.getAcronym() != null && !loggedCrp.getAcronym().isEmpty() ? loggedCrp.getAcronym()
      : loggedCrp.getName();
    // BBC will be our gmail notification email.
    String bbcEmails = this.config.getEmailNotification();
    sendMail.send(toEmail, ccEmail, bbcEmails,
      this.getText("email.siteIntegration.assigned.subject",
        new String[] {crp, siteRoleAcronym, crpsSiteIntegration.getLocElement().getName()}),
      message.toString(), null, null, null, true);
  }

  @Override
  public void prepare() throws Exception {
    loggedCrp = (GlobalUnit) this.getSession().get(APConstants.SESSION_CRP);
    loggedCrp = crpManager.getGlobalUnitById(loggedCrp.getId());
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

  }

  @Override
  public String execute() throws Exception {
    LOG.info("=== EXECUTE METHOD CALLED ===");
    return super.execute();
  }

  @Override
  public void validate() {
    LOG.info("=== VALIDATE METHOD CALLED ===");
    
    // Log all request parameters
    if (this.getRequest() != null) {
      LOG.info("=== REQUEST PARAMETERS ===");
      this.getRequest().getParameterMap().forEach((key, values) -> {
        LOG.info("=== PARAM: {} = {} ===", key, String.join(", ", values));
      });
    }
    
    // Pre-create objects for Struts data binding
    if (this.isHttpPost() && loggedCrp.getSiteIntegrations() != null) {
      LOG.info("=== PREPARING DATA BINDING ===");
      
      // Find the maximum index from request parameters
      final int[] maxIndexHolder = {-1};
      this.getRequest().getParameterMap().keySet().forEach(key -> {
        if (key.startsWith("loggedCrp.siteIntegrations[")) {
          try {
            int index = Integer.parseInt(key.substring(key.indexOf('[') + 1, key.indexOf(']')));
            if (index > maxIndexHolder[0]) {
              maxIndexHolder[0] = index;
            }
          } catch (NumberFormatException e) {
            // Ignore
          }
        }
      });
      
      int maxIndex = maxIndexHolder[0];
      
      LOG.info("=== MAX INDEX FROM REQUEST: {} ===", maxIndex);
      
      // Ensure list has enough elements
      while (loggedCrp.getSiteIntegrations().size() <= maxIndex) {
        CrpsSiteIntegration integration = new CrpsSiteIntegration();
        integration.setSiteLeaders(new ArrayList<>());
        // Pre-create LocElement for nested property binding
        integration.setLocElement(new LocElement());
        loggedCrp.getSiteIntegrations().add(integration);
        LOG.info("=== CREATED INTEGRATION OBJECT AT INDEX {} ===", loggedCrp.getSiteIntegrations().size() - 1);
      }
      
      LOG.info("=== LIST SIZE AFTER PREPARATION: {} ===", loggedCrp.getSiteIntegrations().size());
    }
    
    if (this.hasFieldErrors()) {
      LOG.info("=== FIELD ERRORS: {} ===", this.getFieldErrors());
    }
    if (this.hasActionErrors()) {
      LOG.info("=== ACTION ERRORS: {} ===", this.getActionErrors());
    }
    super.validate();
  }

  @Override
  public String save() {

    if (this.hasPermission("*")) {
      
      if (loggedCrp.getSiteIntegrations() != null) {
        for (int i = 0; i < loggedCrp.getSiteIntegrations().size(); i++) {
          CrpsSiteIntegration si = loggedCrp.getSiteIntegrations().get(i);
          if (si != null && si.getId() == null && si.getLocElement() != null 
              && si.getLocElement().getIsoAlpha2() != null) {
            // Check if LocElement exists in database
            LocElement locElement = locElementManager.getLocElementByISOCode(si.getLocElement().getIsoAlpha2());
            if (locElement == null) {
              this.addFieldError("loggedCrp.siteIntegrations[" + i + "].locElement.isoAlpha2", 
                "Country with ISO code '" + si.getLocElement().getIsoAlpha2() + "' not found in the database. Please select a valid country from the list.");
            }
          }
        }
      }
      
      // Manual binding for nested objects
      this.manualBindingSiteIntegrations();
      
      this.setUsersToActive(new ArrayList<>());
      this.siteIntegrationPreviusData();
      this.siteIntegrationNewData();
      this.loadData();
      this.addUsers();
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

  public void setLoggedCrp(GlobalUnit loggedCrp) {
    this.loggedCrp = loggedCrp;
  }

  public void setSlRole(Role slRole) {
    this.slRole = slRole;
  }


  public void setSlRoleid(Long slRoleid) {
    this.slRoleid = slRoleid;
  }

  private void siteIntegrationNewData() {
    LOG.info("=== SITE INTEGRATION NEW DATA CALLED ===");
    LOG.info("=== LOGGED CRP SITE INTEGRATIONS SIZE: {} ===", 
      loggedCrp.getSiteIntegrations() != null ? loggedCrp.getSiteIntegrations().size() : "NULL");

    for (CrpsSiteIntegration siteIntegration : loggedCrp.getSiteIntegrations()) {
      if (siteIntegration.getId() == null) {
        LocElement locElement = null;
        if (siteIntegration.getLocElement() != null && siteIntegration.getLocElement().getIsoAlpha2() != null) {
          String isoCode = siteIntegration.getLocElement().getIsoAlpha2();
          locElement = locElementManager.getLocElementByISOCode(isoCode);
          
          if (locElement == null) {
            LOG.warn("=== LOC ELEMENT NOT FOUND FOR ISO CODE: {} ===", isoCode);
            this.addFieldError("loggedCrp.siteIntegrations[" + loggedCrp.getSiteIntegrations().indexOf(siteIntegration) + "].locElement.isoAlpha2", 
              "Country with ISO code '" + isoCode + "' not found in the database. Please select a valid country from the list.");
          }
        }

        // Only save if we have a valid LocElement
        if (locElement != null) {
          siteIntegration.setLocElement(locElement);
          siteIntegration.setCrp(loggedCrp);

          locElement.setIsSiteIntegration(true);
          locElementManager.saveLocElement(locElement);

          siteIntegration = crpsSiteIntegrationManager.saveCrpsSiteIntegration(siteIntegration);

          if (siteIntegration.getSiteLeaders() != null) {
            for (CrpSitesLeader sitesLeader : siteIntegration.getSiteLeaders()) {
              User userSiteLeader = userManager.getUser(sitesLeader.getUser().getId());

              sitesLeader.setCrpsSiteIntegration(siteIntegration);
              sitesLeader.setUser(userSiteLeader);
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
          LOG.warn("=== SKIPPING SITE INTEGRATION - NO VALID LOC ELEMENT FOUND ===");
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
  
  /**
   * Manual binding for siteIntegrations from request parameters
   * Based on pattern from commit 5f576ac2e1819e73886230c20a59d75e57a43902
   */
  private void manualBindingSiteIntegrations() {
    if (this.getRequest() != null && loggedCrp.getSiteIntegrations() != null) {
      Map<String, String[]> params = this.getRequest().getParameterMap();
      
      for (String key : params.keySet()) {
        try {
          // Bind for locElement.isoAlpha2
          if (key.matches("loggedCrp\\.siteIntegrations\\[\\d+\\]\\.locElement\\.isoAlpha2")) {
            int index = extractIndex(key);
            String[] values = params.get(key);
            
            if (values != null && values.length > 0 && index < loggedCrp.getSiteIntegrations().size()) {
              String isoAlpha2 = values[0];
              if (isoAlpha2 != null && !isoAlpha2.trim().isEmpty()) {
                CrpsSiteIntegration integration = loggedCrp.getSiteIntegrations().get(index);
                
                // If integration is null, create new one
                if (integration == null) {
                  integration = new CrpsSiteIntegration();
                  integration.setSiteLeaders(new ArrayList<>());
                  loggedCrp.getSiteIntegrations().set(index, integration);
                }
                
                if (integration.getLocElement() == null) {
                  integration.setLocElement(new LocElement());
                }
                integration.getLocElement().setIsoAlpha2(isoAlpha2.trim());
              }
            }
          }
          
          // Bind for siteLeaders
          if (key.matches("loggedCrp\\.siteIntegrations\\[\\d+\\]\\.siteLeaders\\[\\d+\\]\\.user\\.id")) {
            int integrationIndex = extractIndex(key);
            int leaderIndex = extractSecondIndex(key);
            String[] values = params.get(key);
            
            if (values != null && values.length > 0 && integrationIndex < loggedCrp.getSiteIntegrations().size()) {
              CrpsSiteIntegration integration = loggedCrp.getSiteIntegrations().get(integrationIndex);
              
              // If integration is null, create new one
              if (integration == null) {
                integration = new CrpsSiteIntegration();
                integration.setSiteLeaders(new ArrayList<>());
                loggedCrp.getSiteIntegrations().set(integrationIndex, integration);
              }
              
              if (integration.getSiteLeaders() == null) {
                integration.setSiteLeaders(new ArrayList<>());
              }
              
              // Ensure list has enough elements
              while (integration.getSiteLeaders().size() <= leaderIndex) {
                CrpSitesLeader leader = new CrpSitesLeader();
                leader.setUser(new User());
                integration.getSiteLeaders().add(leader);
              }
              
              String userId = values[0];
              if (userId != null && !userId.trim().isEmpty()) {
                try {
                  Long id = Long.parseLong(userId.trim());
                  integration.getSiteLeaders().get(leaderIndex).getUser().setId(id);
                } catch (NumberFormatException e) {
                  LOG.warn("Error parsing user id: {}", userId);
                }
              }
            }
          }
          
        } catch (Exception e) {
          LOG.error("Error in manual binding for key: {}", key, e);
        }
      }
    }
  }
  
  /**
   * Extracts the first index from a parameter with format "object[index].property"
   */
  private int extractIndex(String key) {
    int startIdx = key.indexOf('[') + 1;
    int endIdx = key.indexOf(']');
    return Integer.parseInt(key.substring(startIdx, endIdx));
  }
  
  /**
   * Extracts the second index from a parameter with format "object[index1].object2[index2].property"
   */
  private int extractSecondIndex(String key) {
    int firstClose = key.indexOf(']');
    int secondStart = key.indexOf('[', firstClose) + 1;
    int secondEnd = key.indexOf(']', firstClose + 1);
    return Integer.parseInt(key.substring(secondStart, secondEnd));
  }

}
