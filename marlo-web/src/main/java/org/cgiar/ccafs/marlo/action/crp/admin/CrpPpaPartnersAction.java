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
import org.cgiar.ccafs.marlo.data.manager.CrpManager;
import org.cgiar.ccafs.marlo.data.manager.CrpPpaPartnerManager;
import org.cgiar.ccafs.marlo.data.manager.CrpUserManager;
import org.cgiar.ccafs.marlo.data.manager.InstitutionManager;
import org.cgiar.ccafs.marlo.data.manager.LiaisonInstitutionManager;
import org.cgiar.ccafs.marlo.data.manager.LiaisonUserManager;
import org.cgiar.ccafs.marlo.data.manager.RoleManager;
import org.cgiar.ccafs.marlo.data.manager.UserManager;
import org.cgiar.ccafs.marlo.data.manager.UserRoleManager;
import org.cgiar.ccafs.marlo.data.model.Crp;
import org.cgiar.ccafs.marlo.data.model.CrpPpaPartner;
import org.cgiar.ccafs.marlo.data.model.CrpUser;
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
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import com.google.inject.Inject;
import org.apache.commons.lang3.RandomStringUtils;

/**
 * CrpPpaPartnersAction:
 * @author Hermes Jiménez - CIAT/CCAFS
 * @author avalencia - CCAFS
 * @date Oct 26, 2017
 * @time 11:24:16 AM Add cpRole as a flag to avoid contact points
 */
public class CrpPpaPartnersAction extends BaseAction {


  private static final long serialVersionUID = -8561096521514225205L;

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
  private CrpManager crpManager;
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
  private Crp loggedCrp;
  // Util
  private SendMailS sendMail;

  @Inject
  public CrpPpaPartnersAction(APConfig config, InstitutionManager institutionManager, CrpManager crpManager,
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
   * Add cpRole as a flag to avoid contact points
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
            // Disable liaisonUser, liaisonInstitution and UserRole
            if (liaisonUser.getUser() != null && liaisonUser.getUser().getId() != null && cpRole != null) {
              List<UserRole> userRoles = userRoleManager.getUserRolesByUserId(liaisonUser.getUser().getId()).stream()
                .filter(ur -> ur.getRole().equals(cpRole)).collect(Collectors.toList());
              for (UserRole userRole : userRoles) {
                userRoleManager.deleteUserRole(userRole.getId());
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
              // Add LiaisonInstitution if don't exists
              if (liaisonInstitution == null) {
                liaisonInstitution = new LiaisonInstitution();
                liaisonInstitution.setInstitution(crpPpaPartner.getInstitution());
                liaisonInstitution.setCrp(loggedCrp);
                liaisonInstitution.setActive(true);
                liaisonInstitution.setName(crpPpaPartner.getInstitution().getName());
                liaisonInstitution.setAcronym(crpPpaPartner.getInstitution().getAcronym());
                liaisonInstitutionManager.saveLiaisonInstitution(liaisonInstitution);
              }
              // Add liaisonUser
              LiaisonUser liaisonUserSave =
                new LiaisonUser(liaisonInstitution, userManager.getUser(liaisonUser.getUser().getId()));
              liaisonUserSave.setCrp(loggedCrp);
              liaisonUserSave.setActive(true);
              liaisonUserManager.saveLiaisonUser(liaisonUserSave);
              // add userRole
              if (cpRole != null) {
                UserRole userRole = new UserRole(cpRole, liaisonUserSave.getUser());
                userRoleManager.saveUserRole(userRole);
              }
              // If is new user active it
              if (!liaisonUser.getUser().isActive()) {
                this.notifyNewUserCreated(liaisonUser.getUser());
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

  public Crp getLoggedCrp() {
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
      // Saving crpUser
      CrpUser crpUser = new CrpUser(loggedCrp, user);
      crpUser.setActive(true);
      crpUser.setCreatedBy(this.getCurrentUser());
      crpUser.setActiveSince(new Date());
      crpUser.setModifiedBy(this.getCurrentUser());
      crpUser.setModificationJustification("");
      crpUserManager.saveCrpUser(crpUser);

      // Send UserManual.pdf
      String contentType = "application/pdf";
      String fileName = "Introduction_To_MARLO_v2.1.pdf";
      byte[] buffer = null;
      InputStream inputStream = null;

      try {
        inputStream = this.getClass().getResourceAsStream("/manual/Introduction_To_MARLO_v2.1.pdf");
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

      if (buffer != null && fileName != null && contentType != null) {
        sendMail.send(toEmail, ccEmail, bbcEmails, subject, message.toString(), buffer, contentType, fileName, true);
      } else {
        sendMail.send(toEmail, ccEmail, bbcEmails, subject, message.toString(), null, null, null, true);
      }
    }
  }

  /**  
   * Add cpRole as a flag to avoid contact points
   * @author avalencia - CCAFS
   * @date Oct 26, 2017
   * @time 11:23:59 AM
   * @throws Exception
   */
  @Override
  public void prepare() throws Exception {
    super.prepare();
    loggedCrp = (Crp) this.getSession().get(APConstants.SESSION_CRP);
    loggedCrp = crpManager.getCrpById(loggedCrp.getId());

    String params[] = {loggedCrp.getAcronym()};

    // Check if the CRP has Contact Point and ContactPointRole, if not cpRole will be null (it will be used as a flag)
    if (this.hasSpecificities(APConstants.CRP_HAS_CP)
      && roleManager.getRoleById(Long.parseLong((String) this.getSession().get(APConstants.CRP_CP_ROLE))) != null) {
      cpRole = roleManager.getRoleById(Long.parseLong((String) this.getSession().get(APConstants.CRP_CP_ROLE)));
    }

    if (loggedCrp.getCrpPpaPartners() != null) {
      loggedCrp.setCrpInstitutionsPartners(new ArrayList<CrpPpaPartner>(
        loggedCrp.getCrpPpaPartners().stream().filter(ppa -> ppa.isActive()).collect(Collectors.toList())));
      loggedCrp.getCrpInstitutionsPartners()
        .sort((p1, p2) -> p1.getInstitution().getName().compareTo(p2.getInstitution().getName()));
      // Fill Managing/PPA Partners with contact persons
      if (cpRole != null) {
        Set<CrpPpaPartner> crpPpaPartners = new HashSet<CrpPpaPartner>(0);
        for (CrpPpaPartner crpPpaPartner : loggedCrp.getCrpInstitutionsPartners()) {
          this.fillContactPoints(crpPpaPartner);
          crpPpaPartners.add(crpPpaPartner);
        }
        loggedCrp.setCrpPpaPartners(crpPpaPartners);
      }
    }
    institutions = institutionManager.findAll().stream().filter(c -> c.isActive()).collect(Collectors.toList());
    institutions.sort((i1, i2) -> i1.getName().compareTo(i2.getName()));


    this.setBasePermission(this.getText(Permission.CRP_ADMIN_BASE_PERMISSION, params));
    if (this.isHttpPost()) {
      loggedCrp.getCrpInstitutionsPartners().clear();
    }
  }


  @Override
  public String save() {
    if (this.hasPermission("*")) {
      List<CrpPpaPartner> ppaPartnerReview;
      // Check new institutions
      for (CrpPpaPartner partner : loggedCrp.getCrpInstitutionsPartners()) {
        if (partner.getId() == null) {
          partner.setCrp(loggedCrp);
          Institution institution = institutionManager.getInstitutionById(partner.getInstitution().getId());
          partner.setInstitution(institution);
          partner.setActive(true);
          partner.setCreatedBy(this.getCurrentUser());
          partner.setModifiedBy(this.getCurrentUser());
          partner.setModificationJustification("");
          partner.setActiveSince(new Date());
          crpPpaPartnerManager.saveCrpPpaPartner(partner);
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
            liaisonInstitutionManager.saveLiaisonInstitution(liaisonInstitution);
          }
        }
      }
      // Check Changes add and/or disable crpPPaPartner and contact points
      if (crpPpaPartnerManager.findAll() != null) {
        ppaPartnerReview = crpPpaPartnerManager.findAll();
        for (CrpPpaPartner partnerDB : ppaPartnerReview.stream().filter(ppa -> ppa.getCrp().equals(loggedCrp))
          .collect(Collectors.toList())) {
          partnerDB = crpPpaPartnerManager.getCrpPpaPartnerById(partnerDB.getId());
          // Check if the CrpPpaPartner was disabled
          if (!loggedCrp.getCrpInstitutionsPartners().contains(partnerDB)) {
            // Disable Contact Points of a CrpPpaPartner
            this.disableCrpPpaPartnerContactPoints(partnerDB);
            crpPpaPartnerManager.deleteCrpPpaPartner(partnerDB.getId());
          } else {
            // Check changes in the crpPpaPartner contactPoints
            this.checkChangesCrpPpaPartnerContactPoints(partnerDB);
          }
        }
      }

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

  public void setLoggedCrp(Crp loggedCrp) {
    this.loggedCrp = loggedCrp;
  }

}
