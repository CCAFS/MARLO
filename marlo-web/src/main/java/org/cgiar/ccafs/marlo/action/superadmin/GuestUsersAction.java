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

package org.cgiar.ccafs.marlo.action.superadmin;

import org.cgiar.ccafs.marlo.action.BaseAction;
import org.cgiar.ccafs.marlo.config.APConstants;
import org.cgiar.ccafs.marlo.data.manager.CrpUserManager;
import org.cgiar.ccafs.marlo.data.manager.GlobalUnitManager;
import org.cgiar.ccafs.marlo.data.manager.RoleManager;
import org.cgiar.ccafs.marlo.data.manager.UserManager;
import org.cgiar.ccafs.marlo.data.manager.UserRoleManager;
import org.cgiar.ccafs.marlo.data.model.CrpUser;
import org.cgiar.ccafs.marlo.data.model.GlobalUnit;
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
import java.util.List;
import java.util.stream.Collectors;

import javax.inject.Inject;

import org.apache.commons.lang.RandomStringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Hermes Jiménez - CIAT/CCAFS
 * @author Andres Valencia - CIAT/CCAFS
 */
public class GuestUsersAction extends BaseAction {

  private static final long serialVersionUID = 6860177996446505143L;

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

  private final Logger LOG = LoggerFactory.getLogger(GuestUsersAction.class);
  // Managers
  private GlobalUnitManager globalUnitManager;
  private UserManager userManager;
  private CrpUserManager crpUserManager;
  private UserRoleManager userRoleManager;
  private RoleManager roleManager;
  private final SendMailS sendMailS;
  // Parameters
  private List<GlobalUnit> crps;
  private User user;
  private long selectedGlobalUnitID;
  private String message;
  private GuestUsersValidator validator;

  @Inject
  public GuestUsersAction(APConfig config, GlobalUnitManager globalUnitManager, UserManager userManager,
    CrpUserManager crpUserManager, UserRoleManager userRoleManager, RoleManager roleManager, SendMailS sendMailS,
    GuestUsersValidator validator) {
    super(config);
    this.globalUnitManager = globalUnitManager;
    this.userManager = userManager;
    this.crpUserManager = crpUserManager;
    this.userRoleManager = userRoleManager;
    this.roleManager = roleManager;
    this.sendMailS = sendMailS;
    this.validator = validator;
  }


  public List<GlobalUnit> getCrps() {
    return crps;
  }


  /**
   * @return the message
   */
  public String getMessage() {
    return message;
  }


  /**
   * @return the selectedGlobalUnitID
   */
  public long getSelectedGlobalUnitID() {
    return selectedGlobalUnitID;
  }

  public User getUser() {
    return user;
  }

  @Override
  public void prepare() throws Exception {
    crps = new ArrayList<>(
      globalUnitManager.findAll().stream().filter(c -> c.isActive() && c.isMarlo()).collect(Collectors.toList()));
  }


  @Override
  public String save() {
    if (this.canAccessSuperAdmin()) {
      if (selectedGlobalUnitID != -1) {
        GlobalUnit globalUnit = globalUnitManager.getGlobalUnitById(selectedGlobalUnitID);

        User newUser = new User();
        newUser.setFirstName(user.getFirstName());
        newUser.setLastName(user.getLastName());
        newUser.setEmail(user.getEmail());
        newUser.setModificationJustification("User created in MARLO " + this.getActionName().replace("/", "-"));
        newUser.setAutoSave(true);
        newUser.setId(null);
        newUser.setActive(true);

        // Check if the email is valid
        if (newUser.getEmail() != null && this.isValidEmail(newUser.getEmail())) {
          boolean emailExists = false;
          // We need to validate that the email does not exist yet into our database.
          emailExists = userManager.getUserByEmail(newUser.getEmail()) == null ? false : true;

          // If email already exists.
          if (emailExists) {
            // If email already exists into our database.
            newUser = null;
            LOG.warn(this.getText("manageUsers.email.existing"));
            message = this.getText("manageUsers.email.existing");
            return SUCCESS;
          }

          // Get the user if it is a CGIAR email.
          LDAPUser LDAPUser = this.getOutlookUser(newUser.getEmail());

          String password = this.getText("email.outlookPassword");
          if (LDAPUser != null) {
            // CGIAR user
            newUser.setFirstName(LDAPUser.getFirstName());
            newUser.setLastName(LDAPUser.getLastName());
            newUser.setUsername(LDAPUser.getLogin().toLowerCase());
            newUser.setCgiarUser(true);
            newUser = userManager.saveUser(newUser);
            message = this.getText("saving.saved");
          } else {
            // Non CGIAR user
            if (newUser.getFirstName() != null && newUser.getLastName() != null
              && newUser.getFirstName().trim().length() > 0 && newUser.getLastName().trim().length() > 0) {
              newUser.setCgiarUser(false);
              newUser.setModificationJustification("User created in MARLO " + this.getActionName().replace("/", "-"));
              password = RandomStringUtils.randomNumeric(6);
              newUser.setPassword(password);
              newUser = userManager.saveUser(newUser);
              message = this.getText("saving.saved");
            }
          }

          try {
            this.sendMailNewUser(newUser, globalUnit, password);
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
          Role guestRole =
            globalUnit.getRoles().stream().filter(r -> r.getAcronym().equals("G")).collect(Collectors.toList()).get(0);
          userRole.setRole(guestRole);
          userRole.setUser(newUser);
          userRole = userRoleManager.saveUserRole(userRole);


        } else {
          LOG.warn(this.getText("manageUsers.email.notValid"));
          message = this.getText("manageUsers.email.notValid");
        }
      } else {
        message = this.getText("login.error.selectCrp");
        LOG.warn(this.getText("login.error.selectCrp"));
      }

      this.addActionMessage("message:" + this.getText("saving.saved"));
      return SUCCESS;

    } else {
      return NOT_AUTHORIZED;
    }
  }


  public void sendMailNewUser(User user, GlobalUnit loggedCrp, String password) throws NoSuchAlgorithmException {
    String toEmail = this.config.getEmailNotification();
    String ccEmail = null;
    String bbcEmails = this.config.getEmailNotification();
    String subject = this.getText("email.newUser.subject", new String[] {user.getFirstName()});

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

    // Building the Email message:
    StringBuilder message = new StringBuilder();
    message.append(this.getText("email.dear", new String[] {user.getFirstName()}));
    String crp = loggedCrp.getAcronym() != null && !loggedCrp.getAcronym().isEmpty() ? loggedCrp.getAcronym()
      : loggedCrp.getName();
    message.append(
      this.getText("email.newUser.part2", new String[] {this.getText("global.sClusterOfActivities").toLowerCase(),
        config.getBaseUrl(), crp, user.getEmail(), password, this.getText("email.support", new String[] {crpAdmins})}));
    message.append(this.getText("email.bye"));

    // Send pdf
    String contentType = "application/pdf";
    String fileName = APConstants.MARLO_PDF_MANUAL_NAME;
    byte[] buffer = null;
    InputStream inputStream = null;

    try {
      inputStream = this.getClass().getResourceAsStream("/manual/" + APConstants.MARLO_PDF_MANUAL_NAME);
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

    if (buffer != null && fileName != null && contentType != null) {
      sendMailS.send(toEmail, ccEmail, bbcEmails, subject, message.toString(), buffer, contentType, fileName, true);
    } else {
      sendMailS.send(toEmail, ccEmail, bbcEmails, subject, message.toString(), null, null, null, true);
    }

  }


  public void setCrps(List<GlobalUnit> crps) {
    this.crps = crps;
  }


  /**
   * @param selectedGlobalUnitID the selectedGlobalUnitID to set
   */
  public void setSelectedGlobalUnitID(long selectedGlobalUnitID) {
    this.selectedGlobalUnitID = selectedGlobalUnitID;
  }

  public void setUser(User user) {
    this.user = user;
  }

  @Override
  public void validate() {
    if (save) {
      validator.validate(this, user, selectedGlobalUnitID, true);
    }
  }

}
