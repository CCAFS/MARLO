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
import org.cgiar.ccafs.marlo.data.manager.CrpManager;
import org.cgiar.ccafs.marlo.data.manager.CrpUserManager;
import org.cgiar.ccafs.marlo.data.manager.RoleManager;
import org.cgiar.ccafs.marlo.data.manager.UserManager;
import org.cgiar.ccafs.marlo.data.manager.UserRoleManager;
import org.cgiar.ccafs.marlo.data.model.Crp;
import org.cgiar.ccafs.marlo.data.model.CrpUser;
import org.cgiar.ccafs.marlo.data.model.Role;
import org.cgiar.ccafs.marlo.data.model.User;
import org.cgiar.ccafs.marlo.data.model.UserRole;
import org.cgiar.ccafs.marlo.utils.APConfig;
import org.cgiar.ccafs.marlo.utils.SendMailS;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

import com.google.inject.Inject;
import org.apache.commons.lang3.StringUtils;

/**
 * @author Hermes Jiménez - CIAT/CCAFS
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

  private UserManager userManager;


  private CrpManager crpManager;

  private UserRoleManager userRoleManager;

  private CrpUserManager crpUserManager;
  private RoleManager roleManager;

  private SendMailS sendMailS;

  private User user;

  private boolean cigarUser;


  private Boolean isNewUser;

  private List<Crp> crps;

  private long userID;


  @Inject
  public GuestUsersAction(APConfig config, UserManager userManager, CrpManager crpManager,
    CrpUserManager crpUserManager, UserRoleManager userRoleManager, SendMailS sendMailS, RoleManager roleManager) {
    super(config);
    this.userManager = userManager;
    this.crpManager = crpManager;
    this.crpUserManager = crpUserManager;
    this.userRoleManager = userRoleManager;
    this.sendMailS = sendMailS;
    this.roleManager = roleManager;
  }

  public List<Crp> getCrps() {
    return crps;
  }

  public Boolean getIsNewUser() {
    return isNewUser;
  }

  public User getUser() {
    return user;
  }


  public long getUserID() {
    return userID;
  }

  public boolean isCigarUser() {
    return cigarUser;
  }

  @Override
  public void prepare() throws Exception {

    try {
      userID = Long.parseLong(StringUtils.trim(this.getRequest().getParameter(APConstants.USER_ID)));
      user = userManager.getUser(userID);
      System.out.println("");
    } catch (Exception e) {

    }

    crps = new ArrayList<>(
      crpManager.findAll().stream().filter(c -> c.isActive() && c.isMarlo()).collect(Collectors.toList()));

    if (this.isHttpPost()) {
      isNewUser = null;
    }

  }


  @Override
  public String save() {
    User newUser;
    long newUserID;

    if (isNewUser) {

      newUser = new User();

      newUser.setActiveSince(new Date());
      newUser.setFirstName(user.getFirstName());
      newUser.setLastName(user.getLastName());
      newUser.setUsername(user.getUsername());
      newUser.setActive(user.isActive());
      newUser.setCgiarUser(user.isCgiarUser());
      newUser.setAutoSave(user.isAutoSave());
      newUser.setEmail(user.getEmail());
      newUser.setModificationJustification(" ");
      newUser.setModifiedBy(this.getCurrentUser());


      if (!user.isCgiarUser()) {
        newUser.setPassword(user.getPassword());
      }
      newUserID = userManager.saveUser(newUser, this.getCurrentUser());


    } else {

      newUser = userManager.getUser(user.getId());

      newUser.setActive(user.isActive());
      newUser.setAutoSave(user.isAutoSave());
      newUser.setModificationJustification(" ");
      newUser.setModifiedBy(this.getCurrentUser());

      if (!user.isCgiarUser()) {
        newUser.setPassword(user.getPassword());
      }
      newUserID = userManager.saveUser(newUser, this.getCurrentUser());
    }


    if (newUserID != -1) {
      userID = newUserID;
      newUser = userManager.getUser(newUserID);

      if (user.getCrpUser() != null) {
        for (CrpUser crpUser : user.getCrpUser()) {
          if (crpUser.getId() == -1) {

            Crp crp = crpManager.getCrpById(crpUser.getCrp().getId());

            CrpUser newCrpUser = new CrpUser();
            newCrpUser.setCrp(crp);
            newCrpUser.setUser(newUser);
            newCrpUser.setActiveSince(new Date());
            newCrpUser.setCreatedBy(this.getCurrentUser());
            newCrpUser.setModifiedBy(this.getCurrentUser());
            newCrpUser.setModificationJustification(" ");
            newCrpUser.setActive(true);

            long newCrpUserID = crpUserManager.saveCrpUser(newCrpUser);

            if (newCrpUserID != -1) {

              newCrpUser = crpUserManager.getCrpUserById(newCrpUserID);

              UserRole userRole = new UserRole();

              List<Role> roles = new ArrayList<>(crp.getRoles());

              Role guestRole =
                roles.stream().filter(r -> r.getAcronym().equals("G")).collect(Collectors.toList()).get(0);

              userRole.setRole(guestRole);
              userRole.setUser(newUser);

              long userRoleID = userRoleManager.saveUserRole(userRole);

              if (isNewUser && userRoleID != -1) {
                try {
                  this.sendMailNewUser(newUser, crp);
                } catch (NoSuchAlgorithmException e) {
                  e.printStackTrace();
                }
              }
            }
          }
        }
      }
    }

    this.setInvalidFields(new HashMap<>());
    this.addActionMessage(this.getText("saving.saved"));
    return SUCCESS;
  }

  public void sendMailNewUser(User user, Crp loggedCrp) throws NoSuchAlgorithmException {
    String toEmail = user.getEmail();
    String ccEmail = null;
    String bbcEmails = this.config.getEmailNotification();
    String subject = this.getText("email.newUser.subject", new String[] {user.getFirstName()});
    // Setting the password
    String password = this.getText("email.outlookPassword");
    if (!user.isCgiarUser()) {
      password = this.user.getPassword();
      // Applying the password to the user.
      user.setPassword(password);
    }

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
      sendMailS.send(toEmail, ccEmail, bbcEmails, subject, message.toString(), buffer, contentType, fileName, true);
    } else {
      sendMailS.send(toEmail, ccEmail, bbcEmails, subject, message.toString(), null, null, null, true);
    }

  }

  public void setCrps(List<Crp> crps) {
    this.crps = crps;
  }


  public void setIsNewUser(Boolean isNewUser) {
    this.isNewUser = isNewUser;
  }

  public void setNewUser(boolean isNewUser) {
    this.isNewUser = isNewUser;
  }

  public void setUser(User user) {
    this.user = user;
  }

  public void setUserID(long userID) {
    this.userID = userID;
  }

}
