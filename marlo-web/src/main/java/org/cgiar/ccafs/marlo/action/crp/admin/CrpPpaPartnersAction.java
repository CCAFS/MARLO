/*****************************************************************
 * \ * This file is part of Managing Agricultural Research for Learning &
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
import org.cgiar.ccafs.marlo.data.manager.CrpPpaPartnerManager;
import org.cgiar.ccafs.marlo.data.manager.CrpUserManager;
import org.cgiar.ccafs.marlo.data.manager.GlobalUnitManager;
import org.cgiar.ccafs.marlo.data.manager.InstitutionManager;
import org.cgiar.ccafs.marlo.data.manager.LiaisonInstitutionManager;
import org.cgiar.ccafs.marlo.data.manager.LiaisonUserManager;
import org.cgiar.ccafs.marlo.data.manager.RoleManager;
import org.cgiar.ccafs.marlo.data.manager.UserManager;
import org.cgiar.ccafs.marlo.data.manager.UserRoleManager;
import org.cgiar.ccafs.marlo.data.model.CrpPpaPartner;
import org.cgiar.ccafs.marlo.data.model.CrpUser;
import org.cgiar.ccafs.marlo.data.model.GlobalUnit;
import org.cgiar.ccafs.marlo.data.model.Institution;
import org.cgiar.ccafs.marlo.data.model.LiaisonInstitution;
import org.cgiar.ccafs.marlo.data.model.LiaisonUser;
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
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import javax.inject.Inject;

import org.apache.commons.lang3.RandomStringUtils;
import org.apache.struts2.ServletActionContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * CrpPpaPartnersAction:
 * 
 * @author Hermes Jiménez - CIAT/CCAFS
 * @author avalencia - CCAFS
 * @date Oct 26, 2017
 * @time 11:24:16 AM Add cpRole as a flag to avoid contact points
 */
public class CrpPpaPartnersAction extends BaseAction {

  private static final long serialVersionUID = -8561096521514225205L;
  private static final Logger LOG = LoggerFactory.getLogger(CrpPpaPartnersAction.class);

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
  private InstitutionManager institutionManager;

  private GlobalUnitManager crpManager;

  private CrpPpaPartnerManager crpPpaPartnerManager;
  private LiaisonUserManager liaisonUserManager;
  private LiaisonInstitutionManager liaisonInstitutionManager;
  private UserRoleManager userRoleManager;
  private RoleManager roleManager;
  private Role cpRole;
  private CrpUserManager crpUserManager;
  private UserManager userManager;
  // Variables
  private List<Institution> institutions;

  private List<Institution> crpInstitutions;
  private GlobalUnit loggedCrp;
  // Util
  private SendMailS sendMail;


  @Inject
  public CrpPpaPartnersAction(APConfig config, InstitutionManager institutionManager, GlobalUnitManager crpManager,
    CrpPpaPartnerManager crpPpaPartnerManager, LiaisonUserManager liaisonUserManager,
    LiaisonInstitutionManager liaisonInstitutionManager, UserRoleManager userRoleManager, RoleManager roleManager,
    UserManager userManager, CrpUserManager crpUserManager, SendMailS sendMail) {
    super(config);
    this.institutionManager = institutionManager;
    this.crpManager = crpManager;
    this.crpPpaPartnerManager = crpPpaPartnerManager;
    this.liaisonUserManager = liaisonUserManager;
    this.liaisonInstitutionManager = liaisonInstitutionManager;
    this.userRoleManager = userRoleManager;
    this.roleManager = roleManager;
    this.userManager = userManager;
    this.crpUserManager = crpUserManager;
    this.sendMail = sendMail;
  }

  /**
   * Add Crp User if there is not any active.
   * 
   * @author avalencia - CCAFS
   * @date Dec 20, 2017
   * @time 9:56:52 AM
   * @param user
   */
  private void addCrpUserIfNotExist(User user) {
    if (!crpUserManager.existActiveCrpUser(user.getId(), loggedCrp.getId())) {
      CrpUser crpUser = new CrpUser(loggedCrp, user);
      crpUserManager.saveCrpUser(crpUser);
    }
  }

  /**
   * Add cpRole as a flag to avoid contact points
   * 
   * @author avalencia - CCAFS
   * @date Oct 26, 2017
   * @time 11:22:37 AM
   * @param partnerDB
   */
  private void checkChangesCrpPpaPartnerContactPoints(CrpPpaPartner partnerDB) {
    for (CrpPpaPartner crpPpaPartner : loggedCrp.getCrpInstitutionsPartners().stream()
      .filter(c -> c.getId().longValue() == (partnerDB.getId().longValue())).collect(Collectors.toList())) {
      // fill contactPoints
      if (cpRole != null) {
        this.fillContactPoints(partnerDB);
      }
      // Check disabled contact points
      if (cpRole != null) {
        for (LiaisonUser liaisonUser : partnerDB.getContactPoints()) {
          if (crpPpaPartner.getContactPoints() == null || crpPpaPartner.getContactPoints().isEmpty()
            || !crpPpaPartner.getContactPoints().contains(liaisonUser)) {
            // Disable liaisonUser, liaisonInstitution and 1 UserRole
            if (liaisonUser.getUser() != null && liaisonUser.getUser().getId() != null && cpRole != null) {
              List<UserRole> userRoles = userRoleManager.getUserRolesByUserId(liaisonUser.getUser().getId()).stream()
                .filter(ur -> ur.getRole().equals(cpRole)).collect(Collectors.toList());
              if (userRoles != null && userRoles.size() > 0) {
                UserRole userRole = userRoles.get(0);
                userRoleManager.deleteUserRole(userRole.getId());
                this.notifyRoleContactPointUnassigned(userRole, partnerDB);
              }
            }
            // Disable LiaisonUser
            liaisonUserManager.deleteLiaisonUser(liaisonUser.getId());
          }
        }
      }
      // Check Added liaisonUsers
      if (cpRole != null) {
        if (crpPpaPartner.getContactPoints() != null && !crpPpaPartner.getContactPoints().isEmpty()) {
          for (LiaisonUser liaisonUser : crpPpaPartner.getContactPoints()) {
            // new User?
            if (liaisonUser.getId() == null || !partnerDB.getContactPoints().contains(liaisonUser)) {
              LiaisonInstitution liaisonInstitution = liaisonInstitutionManager
                .getLiasonInstitutionByInstitutionId(crpPpaPartner.getInstitution().getId(), loggedCrp.getId());
              Institution institution = institutionManager.getInstitutionById(crpPpaPartner.getInstitution().getId());
              // Add LiaisonInstitution if don't exists
              if (liaisonInstitution == null && institution != null) {
                liaisonInstitution = new LiaisonInstitution();
                liaisonInstitution.setInstitution(institution);
                liaisonInstitution.setCrp(loggedCrp);
                liaisonInstitution.setActive(true);
                liaisonInstitution.setName(institution.getName());
                liaisonInstitution.setAcronym(institution.getAcronym());
                liaisonInstitutionManager.saveLiaisonInstitution(liaisonInstitution);
              }
              // Add liaisonUser
              LiaisonUser liaisonUserSave =
                new LiaisonUser(liaisonInstitution, userManager.getUser(liaisonUser.getUser().getId()));
              liaisonUserSave.setCrp(loggedCrp);
              liaisonUserSave.setActive(true);
              liaisonUserManager.saveLiaisonUser(liaisonUserSave);
              // If is new user active it
              if (!liaisonUser.getUser().isActive()) {
                this.notifyNewUserCreated(liaisonUser.getUser());
              }
              this.addCrpUserIfNotExist(liaisonUser.getUser());
              // add userRole
              if (cpRole != null) {
                UserRole userRole = new UserRole(cpRole, liaisonUserSave.getUser());
                userRoleManager.saveUserRole(userRole);
                this.notifyRoleContactPointAssigned(userRole, crpPpaPartner);
              }
              partnerDB.getContactPoints().add(liaisonUserSave);
            }
          }
        }
      }
    }
  }

  /**
   * Add cpRole as a flag to avoid contact points
   * 
   * @author avalencia - CCAFS
   * @date Oct 26, 2017
   * @time 11:23:00 AM
   * @param partner
   */
  private void disableCrpPpaPartnerContactPoints(CrpPpaPartner partner) {
    // Disable liaisonUser, liaisonInstitution and UserRoles
    LiaisonInstitution liaisonInstitution = liaisonInstitutionManager
      .getLiasonInstitutionByInstitutionId(partner.getInstitution().getId(), loggedCrp.getId());
    // Disable liaisonInstitution
    if (liaisonInstitution != null && liaisonInstitution.isActive()) {
      liaisonInstitutionManager.deleteLiaisonInstitution(liaisonInstitution.getId());
      // Disable LiaisonUsers
      if (cpRole != null && liaisonInstitution.getLiaisonUsers() != null
        && !liaisonInstitution.getLiaisonUsers().isEmpty()) {
        for (LiaisonUser liaisonUser : liaisonInstitution.getLiaisonUsers().stream().filter(lu -> lu.isActive())
          .collect(Collectors.toList())) {
          // Delete CP UserRole
          if (liaisonUser.getUser() != null && liaisonUser.getUser().getId() != null && cpRole != null) {
            List<UserRole> userRoles = userRoleManager.getUserRolesByUserId(liaisonUser.getUser().getId()).stream()
              .filter(ur -> ur.getRole().equals(cpRole)).collect(Collectors.toList());
            for (UserRole userRole : userRoles) {
              userRoleManager.deleteUserRole(userRole.getId());
              this.notifyRoleContactPointUnassigned(userRole, partner);
            }
          }
          // Disable LiaisonUser
          liaisonUserManager.deleteLiaisonUser(liaisonUser.getId());
        }
      }
    }

  }

  /**
   * Add cpRole as a flag to avoid contact points
   * 
   * @author avalencia - CCAFS
   * @date Oct 26, 2017
   * @time 11:23:44 AM
   * @param crpPpaPartner
   */
  private void fillContactPoints(CrpPpaPartner crpPpaPartner) {

    LiaisonInstitution liaisonInstitution = liaisonInstitutionManager
      .getLiasonInstitutionByInstitutionId(crpPpaPartner.getInstitution().getId(), loggedCrp.getId());
    if (cpRole != null && liaisonInstitution != null && liaisonInstitution.isActive()) {
      crpPpaPartner.setContactPoints(liaisonInstitution.getLiaisonUsers().stream()
        .filter(lu -> lu.isActive() && lu.getUser() != null && lu.getUser().isActive() && lu.getCrp() != null
          && lu.getCrp().equals(loggedCrp))
        .sorted((lu1, lu2) -> lu1.getUser().getLastName().compareTo(lu2.getUser().getLastName()))
        .collect(Collectors.toList()));
    } else {
      crpPpaPartner.setContactPoints(new ArrayList<LiaisonUser>());
    }
  }

  public Role getCpRole() {
    return cpRole;
  }

  public List<Institution> getCrpInstitutions() {
    return crpInstitutions;
  }

  public List<Institution> getInstitutions() {
    return institutions;
  }

  public GlobalUnit getLoggedCrp() {
    return loggedCrp;
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

      // Saving crpUser
      Map<String, Object> mapUser = new HashMap<>();
      mapUser.put("user", user);
      mapUser.put("password", password);
      this.getUsersToActive().add(mapUser);
      this.addCrpUserIfNotExist(user);

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
        e.printStackTrace();
      } catch (IOException e) {
        e.printStackTrace();
      } finally {
        if (inputStream != null) {
          try {
            inputStream.close();
          } catch (IOException e) {
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
   * This method notify the user that is been assigned as Contact Point for an specific PPA / Managing Partner
   * 
   * @author avalencia - CCAFS
   * @date Oct 30, 2017
   * @time 9:33:38 AM
   * @param userRoleAssigned is the user and role been assigned
   * @param crpPpaPartner is the PPA / Managing Partner where is assigned
   */
  private void notifyRoleContactPointAssigned(UserRole userRoleAssigned, CrpPpaPartner crpPpaPartner) {
    crpPpaPartner = crpPpaPartnerManager.getCrpPpaPartnerById(crpPpaPartner.getId());
    // Email send to the user assigned
    String toEmail = userRoleAssigned.getUser().getEmail();
    // CC will be the user who is making the modification and the CRP Admins
    String ccEmail = "";
    if (this.getCurrentUser() != null) {
      ccEmail = this.getCurrentUser().getEmail();
    }

    // Adding CRP Admins to CC
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
    String ppaPartner =
      crpPpaPartner.getInstitution().getAcronym() != null && !crpPpaPartner.getInstitution().getAcronym().isEmpty()
        ? crpPpaPartner.getInstitution().getAcronym() : crpPpaPartner.getInstitution().getName();

    String subject = this.getText("email.contactpoint.assigned.subject", new String[] {ppaPartner, crp});

    StringBuilder message = new StringBuilder();
    // Building the Email message:
    message.append(this.getText("email.dear", new String[] {userRoleAssigned.getUser().getFirstName()}));
    message.append(this.getText("email.contactpoint.assigned",
      new String[] {ppaPartner, crp, this.getText("email.contactpoint.responsabilities")}));

    message.append(this.getText("email.support", new String[] {crpAdmins}));
    message.append(this.getText("email.getStarted"));
    message.append(this.getText("email.bye"));
    if (this.validateEmailNotification()) {
      sendMail.send(toEmail, ccEmail, bbcEmails, subject, message.toString(), null, null, null, true);
    }
  }

  /**
   * This method notify the user that is been unassigned as Contact Point for an specific PPA / Managing Partner
   * 
   * @author avalencia - CCAFS
   * @date Oct 30, 2017
   * @time 9:34:14 AM
   * @param userRoleUnassigned is the user and role been unassigned
   * @param crpPpaPartner is the PPA / Managing Partner where is assigned
   */
  private void notifyRoleContactPointUnassigned(UserRole userRoleUnassigned, CrpPpaPartner crpPpaPartner) {
    // Email send to the user unassigned
    String toEmail = userRoleUnassigned.getUser().getEmail();
    // CC will be the user who is making the modification and the CRP Admins
    String ccEmail = "";
    if (this.getCurrentUser() != null) {
      ccEmail = this.getCurrentUser().getEmail();
    }

    // Adding CRP Admins to CC
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
    Institution institution = institutionManager.getInstitutionById(crpPpaPartner.getInstitution().getId());

    String crp = loggedCrp.getAcronym() != null && !loggedCrp.getAcronym().isEmpty() ? loggedCrp.getAcronym()
      : loggedCrp.getName();
    String ppaPartner = institution.getAcronym() != null && !institution.getAcronym().isEmpty()
      ? institution.getAcronym() : institution.getName();

    String subject = this.getText("email.contactpoint.unassigned.subject", new String[] {crp, ppaPartner});

    StringBuilder message = new StringBuilder();
    // Building the Email message:
    message.append(this.getText("email.dear", new String[] {userRoleUnassigned.getUser().getFirstName()}));
    message.append(this.getText("email.contactpoint.unassigned", new String[] {crp, ppaPartner}));

    message.append(this.getText("email.support", new String[] {crpAdmins}));
    message.append(this.getText("email.bye"));
    if (this.validateEmailNotification()) {
      sendMail.send(toEmail, ccEmail, bbcEmails, subject, message.toString(), null, null, null, true);
    }

  }

  /**
   * Add cpRole as a flag to avoid contact points
   * 
   * @author avalencia - CCAFS
   * @date Oct 26, 2017
   * @time 11:23:59 AM
   * @throws Exception
   */
  @Override
  public void prepare() throws Exception {
    super.prepare();
    loggedCrp = (GlobalUnit) this.getSession().get(APConstants.SESSION_CRP);
    loggedCrp = crpManager.getGlobalUnitById(loggedCrp.getId());

    String params[] = {loggedCrp.getAcronym()};

    // Check if the CRP has Contact Point and ContactPointRole, if not cpRole will be null (it will be used as a flag)
    if (this.hasSpecificities(APConstants.CRP_HAS_CP)
      && roleManager.getRoleById(Long.parseLong((String) this.getSession().get(APConstants.CRP_CP_ROLE))) != null) {
      cpRole = roleManager.getRoleById(Long.parseLong((String) this.getSession().get(APConstants.CRP_CP_ROLE)));
    }

    // IMPORTANTE: Solo cargar partners desde BD en GET, no en POST
    if (loggedCrp.getCrpPpaPartners() != null && !this.isHttpPost()) {
      loggedCrp.setCrpInstitutionsPartners(new ArrayList<CrpPpaPartner>(loggedCrp.getCrpPpaPartners().stream()
        .filter(ppa -> ppa.isActive() && ppa.getPhase().equals(this.getActualPhase())).collect(Collectors.toList())));
      loggedCrp.getCrpInstitutionsPartners()
        .sort((p1, p2) -> p1.getInstitution().getName().compareTo(p2.getInstitution().getName()));
      // Fill Managing Partners with contact persons
      if (cpRole != null) {
        Set<CrpPpaPartner> crpPpaPartners = new HashSet<CrpPpaPartner>(0);
        for (CrpPpaPartner crpPpaPartner : loggedCrp.getCrpInstitutionsPartners()) {
          this.fillContactPoints(crpPpaPartner);
          crpPpaPartners.add(crpPpaPartner);
        }
        loggedCrp.setCrpPpaPartners(crpPpaPartners);
      }
    } else if (this.isHttpPost()) {
      // En POST, inicializar lista vacía para que Struts pueda usar auto-growth
      // Struts necesita que la lista exista (no sea null) para agregar elementos indexados
      loggedCrp.setCrpInstitutionsPartners(new ArrayList<>());
    }
    institutions = institutionManager.findAll().stream().filter(c -> c.isActive()).collect(Collectors.toList());
    institutions.sort((i1, i2) -> i1.getName().compareTo(i2.getName()));

    this.setBasePermission(this.getText(Permission.CRP_ADMIN_BASE_PERMISSION, params));
  }

  @Override
  public String save() {
    if (this.hasPermission("*")) {
      // DIAGNÓSTICO: Imprimir parámetros HTTP recibidos
      LOG.info("=== DIAGNÓSTICO: PARÁMETROS HTTP RECIBIDOS ===");
      java.util.Map<String, String[]> params = ServletActionContext.getRequest().getParameterMap();
      for (String key : params.keySet()) {
        if (key.contains("crpInstitutionsPartners")) {
          LOG.info("  Parámetro: " + key + " = " + java.util.Arrays.toString(params.get(key)));
        }
      }
      LOG.info("=== FIN DIAGNÓSTICO ===");
      
      // BINDING MANUAL: Extraer contactPoints de parámetros HTTP
      // Struts 6.4.0 no hace auto-growth de listas anidadas
      this.manualBindContactPoints();
      
      // DIAGNÓSTICO: Ver qué partners están llegando
      LOG.info("=== PARTNERS RECIBIDOS ===");
      if (loggedCrp.getCrpInstitutionsPartners() != null) {
        LOG.info("Total partners: " + loggedCrp.getCrpInstitutionsPartners().size());
        for (int i = 0; i < loggedCrp.getCrpInstitutionsPartners().size(); i++) {
          CrpPpaPartner p = loggedCrp.getCrpInstitutionsPartners().get(i);
          LOG.info("  Partner[" + i + "]: id=" + p.getId() + 
                   ", institution.id=" + (p.getInstitution() != null ? p.getInstitution().getId() : "null") +
                   ", contactPoints=" + (p.getContactPoints() != null ? p.getContactPoints().size() : "null"));
        }
      } else {
        LOG.info("crpInstitutionsPartners es NULL");
      }
      LOG.info("=== FIN PARTNERS ===");
      
      this.setUsersToActive(new ArrayList<>());
      List<CrpPpaPartner> ppaPartnerReview =
        new ArrayList<>(crpPpaPartnerManager.findAll().stream().filter(ppa -> ppa.isActive()
          && ppa.getCrp() != null && ppa.getCrp().getId() == loggedCrp.getId() && ppa.getPhase().equals(this.getActualPhase()))
          .collect(Collectors.toList()));
      if (ppaPartnerReview != null) {

        for (CrpPpaPartner partner : ppaPartnerReview.stream()
          .filter(ppa -> ppa.getCrp() != null && ppa.getCrp().equals(loggedCrp) && ppa.getPhase().equals(this.getActualPhase()))
          .collect(Collectors.toList())) {
          if (!loggedCrp.getCrpInstitutionsPartners().contains(partner)) {
            crpPpaPartnerManager.deleteCrpPpaPartner(partner.getId());

            this.disableCrpPpaPartnerContactPoints(partner);
          }
        }
      }


      for (CrpPpaPartner partner : loggedCrp.getCrpInstitutionsPartners()) {
        if (partner.getId() == null) {
          partner.setCrp(loggedCrp);
          Institution institution = institutionManager.getInstitutionById(partner.getInstitution().getId());
          partner.setInstitution(institution);
          partner.setPhase(this.getActualPhase());
          partner = crpPpaPartnerManager.saveCrpPpaPartner(partner);
          // save liaison institution if don't exists

          LiaisonInstitution liaisonInstitution = liaisonInstitutionManager
            .getLiasonInstitutionByInstitutionId(partner.getInstitution().getId(), loggedCrp.getId());
          // Add LiaisonInstitution if don't exists
          if (liaisonInstitution == null) {
            liaisonInstitution = new LiaisonInstitution();
            liaisonInstitution.setInstitution(institution);
            liaisonInstitution.setCrp(loggedCrp);
            liaisonInstitution.setActive(true);
            liaisonInstitution.setName(partner.getInstitution().getName());
            liaisonInstitution.setAcronym(partner.getInstitution().getAcronym());
            liaisonInstitution = liaisonInstitutionManager.saveLiaisonInstitution(liaisonInstitution);
          }
          
          // Guardar contactPoints para partner nuevo
          if (partner.getContactPoints() != null && partner.getContactPoints().size() > 0 && liaisonInstitution != null) {
            for (LiaisonUser liaisonUser : partner.getContactPoints()) {
              if (liaisonUser != null && liaisonUser.getUser() != null && liaisonUser.getUser().getId() != null) {
                LiaisonUser liaisonUserSave =
                  new LiaisonUser(liaisonInstitution, userManager.getUser(liaisonUser.getUser().getId()));
                liaisonUserSave.setCrp(loggedCrp);
                liaisonUserSave.setActive(true);
                liaisonUserSave.setLiaisonInstitution(liaisonInstitution);
                liaisonUserManager.saveLiaisonUser(liaisonUserSave);
                
                this.notifyNewUserCreated(liaisonUser.getUser());
                this.addCrpUserIfNotExist(liaisonUser.getUser());
                
                if (cpRole != null) {
                  UserRole userRole = new UserRole(cpRole, liaisonUserSave.getUser());
                  userRoleManager.saveUserRole(userRole);
                  this.notifyRoleContactPointAssigned(userRole, partner);
                }
              }
            }
          }

        } else {
          LiaisonInstitution liaisonInstitution = liaisonInstitutionManager
            .getLiasonInstitutionByInstitutionId(partner.getInstitution().getId(), loggedCrp.getId());

          if (liaisonInstitution == null) {
            partner.setInstitution(institutionManager.getInstitutionById(partner.getInstitution().getId()));
            liaisonInstitution = new LiaisonInstitution();
            liaisonInstitution.setInstitution(partner.getInstitution());
            liaisonInstitution.setCrp(loggedCrp);
            liaisonInstitution.setActive(true);
            liaisonInstitution.setName(partner.getInstitution().getName());
            liaisonInstitution.setAcronym(partner.getInstitution().getAcronym());
            liaisonInstitution = liaisonInstitutionManager.saveLiaisonInstitution(liaisonInstitution);
          }
          if (liaisonInstitution != null) {

            List<LiaisonUser> usersDB =
              liaisonInstitution.getLiaisonUsers().stream().filter(c -> c.isActive()).collect(Collectors.toList());
            if (partner.getContactPoints() == null) {
              partner.setContactPoints(new ArrayList<>());
            }
            
            LOG.info("=== DEPURACIÓN ELIMINACIÓN - Partner[{}] (id={}) ===", 
                     loggedCrp.getCrpInstitutionsPartners().indexOf(partner), partner.getId());
            LOG.info("  LiaisonInstitution: {} (id={})", 
                     liaisonInstitution.getName(), liaisonInstitution.getId());
            LOG.info("  UsersDB activos: {}", usersDB.size());
            LOG.info("  ContactPoints en formulario: {}", partner.getContactPoints().size());
            
            // Agrupar usersDB por usuario para manejar duplicados
            Map<Long, List<LiaisonUser>> usersDBByUserId = usersDB.stream()
              .collect(Collectors.groupingBy(lu -> lu.getUser().getId()));
            
            for (Map.Entry<Long, List<LiaisonUser>> entry : usersDBByUserId.entrySet()) {
              Long userId = entry.getKey();
              List<LiaisonUser> userLiaisonUsers = entry.getValue();
              
              LOG.info("    Verificando Usuario: id={}, name={}, LiaisonUsers duplicados: {}", 
                       userId, userLiaisonUsers.get(0).getUser().getFirstName(), userLiaisonUsers.size());
              
              // Verificar si este usuario está en el formulario
              List<LiaisonUser> liaisonUsersResult = partner.getContactPoints().stream()
                .filter(c -> c.getUser().getId().longValue() == userId.longValue())
                .collect(Collectors.toList());
              LOG.info("    Encontrado en formulario: {}", liaisonUsersResult.size());
              
              if (liaisonUsersResult.isEmpty()) {
                // Usuario NO está en formulario: eliminar TODOS sus LiaisonUser
                LOG.info("    >>> ELIMINANDO TODOS los LiaisonUser del usuario {} (cantidad: {})", userId, userLiaisonUsers.size());
                for (LiaisonUser liaisonUser : userLiaisonUsers) {
                  liaisonUserManager.deleteLiaisonUser(liaisonUser.getId());
                  // Disable LiaisonUsers
                  if (liaisonUser.getUser() != null && liaisonUser.getUser().getId() != null && cpRole != null) {
                    List<UserRole> userRoles = userRoleManager.getUserRolesByUserId(liaisonUser.getUser().getId())
                      .stream().filter(ur -> ur.getRole().equals(cpRole)).collect(Collectors.toList());
                    for (UserRole userRole : userRoles) {
                      userRoleManager.deleteUserRole(userRole.getId());
                      this.notifyRoleContactPointUnassigned(userRole, partner);
                    }
                  }
                }
              } else {
                // Usuario SÍ está en formulario: mantener solo UNO, eliminar los duplicados
                if (userLiaisonUsers.size() > 1) {
                  LOG.info("    >>> ELIMINANDO {} LiaisonUser duplicados del usuario {}, manteniendo 1", 
                           userLiaisonUsers.size() - 1, userId);
                  // Eliminar todos excepto el primero
                  for (int i = 1; i < userLiaisonUsers.size(); i++) {
                    LiaisonUser liaisonUser = userLiaisonUsers.get(i);
                    liaisonUserManager.deleteLiaisonUser(liaisonUser.getId());
                  }
                } else {
                  LOG.info("    >>> Manteniendo LiaisonUser del usuario {} (sin duplicados)", userId);
                }
              }
            }
            LOG.info("=== FIN DEPURACIÓN ELIMINACIÓN ===");
            
            if (partner.getContactPoints() != null && partner.getContactPoints().size() > 0) {

              for (LiaisonUser liaisonUser : partner.getContactPoints()) {
                // Verificar si ya existe un LiaisonUser activo para este usuario en esta institución
                boolean alreadyExists = usersDB.stream()
                  .anyMatch(lu -> lu.getUser() != null && liaisonUser.getUser() != null 
                    && lu.getUser().getId().longValue() == liaisonUser.getUser().getId().longValue());
                
                // Solo crear si no existe ya
                if (!alreadyExists) {
                  // Add liaisonUser
                  LiaisonUser liaisonUserSave =
                    new LiaisonUser(liaisonInstitution, userManager.getUser(liaisonUser.getUser().getId()));
                  liaisonUserSave.setCrp(loggedCrp);
                  liaisonUserSave.setActive(true);
                  liaisonUserSave.setLiaisonInstitution(liaisonInstitution);
                  if (liaisonInstitution != null) {
                    liaisonUserManager.saveLiaisonUser(liaisonUserSave);
                    // If is new user active it

                    this.notifyNewUserCreated(liaisonUser.getUser());

                    this.addCrpUserIfNotExist(liaisonUser.getUser());
                    // add userRole
                    if (cpRole != null) {
                      UserRole userRole = new UserRole(cpRole, liaisonUserSave.getUser());
                      userRoleManager.saveUserRole(userRole);
                      this.notifyRoleContactPointAssigned(userRole, partner);
                    }
                  }


                }
              }
            }
          }

        }
      }

      this.addUsers();

      Collection<String> messages = this.getActionMessages();
      if (!messages.isEmpty()) {
        String validationMessage = messages.iterator().next();
        this.setActionMessages(null);
        this.addActionWarning(this.getText("saving.saved") + validationMessage);
      } else {
        this.addActionMessage("message:" + this.getText("saving.saved"));
      }
      messages = this.getActionMessages();
      return SUCCESS;
    } else {
      return NOT_AUTHORIZED;
    }
  }


  public void setCpRole(Role cpRole) {
    this.cpRole = cpRole;
  }


  public void setCrpInstitutions(List<Institution> crpInstitutions) {
    this.crpInstitutions = crpInstitutions;
  }

  public void setInstitutions(List<Institution> institutions) {
    this.institutions = institutions;
  }

  public void setLoggedCrp(GlobalUnit loggedCrp) {
    this.loggedCrp = loggedCrp;
  }

  /**
   * Binding manual para extraer contactPoints de los parámetros HTTP.
   * Struts 6.4.0 no hace auto-growth de listas anidadas, por lo que debemos
   * parsear manualmente los parámetros del formulario.
   */
  private void manualBindContactPoints() {
    java.util.Map<String, String[]> params = ServletActionContext.getRequest().getParameterMap();
    
    // Mapa para almacenar: partnerIndex -> Map<contactPointIndex, userId>
    java.util.Map<Integer, java.util.Map<Integer, Long>> contactPointsMap = new java.util.HashMap<>();
    
    // Buscar parámetros con el patrón: loggedCrp.crpInstitutionsPartners[X].contactPoints[Y].user.id
    java.util.regex.Pattern pattern = java.util.regex.Pattern.compile(
      "loggedCrp\\.crpInstitutionsPartners\\[(\\d+)\\]\\.contactPoints\\[(\\d+)\\]\\.user\\.id"
    );
    
    for (String key : params.keySet()) {
      java.util.regex.Matcher matcher = pattern.matcher(key);
      if (matcher.matches()) {
        int partnerIndex = Integer.parseInt(matcher.group(1));
        int contactIndex = Integer.parseInt(matcher.group(2));
        String[] values = params.get(key);
        
        if (values != null && values.length > 0 && values[0] != null && !values[0].trim().isEmpty()) {
          try {
            Long userId = Long.parseLong(values[0].trim());
            
            contactPointsMap.computeIfAbsent(partnerIndex, k -> new java.util.HashMap<>())
              .put(contactIndex, userId);
            
            LOG.info("Manual binding: Partner[{}].contactPoints[{}].user.id = {}", 
                     partnerIndex, contactIndex, userId);
          } catch (NumberFormatException e) {
            LOG.warn("No se pudo parsear user.id: {} para partner[{}].contactPoints[{}]", 
                     values[0], partnerIndex, contactIndex);
          }
        }
      }
    }
    
    // Asignar los contactPoints a los partners correspondientes
    if (loggedCrp.getCrpInstitutionsPartners() != null) {
      // Primero inicializar todos los partners con lista vacía
      for (int i = 0; i < loggedCrp.getCrpInstitutionsPartners().size(); i++) {
        CrpPpaPartner partner = loggedCrp.getCrpInstitutionsPartners().get(i);
        if (partner != null) {
          partner.setContactPoints(new ArrayList<>());
        }
      }
      
      // Luego asignar los contactPoints de los parámetros HTTP
      for (java.util.Map.Entry<Integer, java.util.Map<Integer, Long>> entry : contactPointsMap.entrySet()) {
        int partnerIndex = entry.getKey();
        java.util.Map<Integer, Long> contacts = entry.getValue();
        
        if (partnerIndex < loggedCrp.getCrpInstitutionsPartners().size()) {
          CrpPpaPartner partner = loggedCrp.getCrpInstitutionsPartners().get(partnerIndex);
          
          if (partner != null) {
            List<LiaisonUser> contactPoints = new ArrayList<>();
            
            // Ordenar por índice y crear los LiaisonUser
            List<Integer> sortedIndices = new ArrayList<>(contacts.keySet());
            java.util.Collections.sort(sortedIndices);
            
            for (Integer contactIndex : sortedIndices) {
              Long userId = contacts.get(contactIndex);
              if (userId != null) {
                LiaisonUser liaisonUser = new LiaisonUser();
                User user = new User();
                user.setId(userId);
                liaisonUser.setUser(user);
                contactPoints.add(liaisonUser);
                
                LOG.info("Asignado contactPoint a Partner[{}] (id={}): user.id={}", 
                         partnerIndex, partner.getId(), userId);
              }
            }
            
            partner.setContactPoints(contactPoints);
          }
        } else {
          LOG.warn("Partner index {} fuera de rango (size={})", 
                   partnerIndex, loggedCrp.getCrpInstitutionsPartners().size());
        }
      }
    }
    
    LOG.info("=== FIN BINDING MANUAL - Total partners con contactPoints asignados: {} ===", 
             contactPointsMap.size());
  }

}