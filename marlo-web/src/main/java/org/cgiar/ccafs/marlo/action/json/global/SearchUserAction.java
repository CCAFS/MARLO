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

package org.cgiar.ccafs.marlo.action.json.global;

import org.cgiar.ccafs.marlo.action.BaseAction;
import org.cgiar.ccafs.marlo.config.APConstants;
import org.cgiar.ccafs.marlo.data.manager.UserManager;
import org.cgiar.ccafs.marlo.data.model.CrpProgramLeader;
import org.cgiar.ccafs.marlo.data.model.CrpUser;
import org.cgiar.ccafs.marlo.data.model.LiaisonUser;
import org.cgiar.ccafs.marlo.data.model.Phase;
import org.cgiar.ccafs.marlo.data.model.ProjectPartnerPerson;
import org.cgiar.ccafs.marlo.data.model.User;
import org.cgiar.ccafs.marlo.data.model.UserRole;
import org.cgiar.ccafs.marlo.utils.APConfig;

import org.cgiar.ciat.auth.LDAPService;
import org.cgiar.ciat.auth.LDAPUser;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.inject.Inject;
import org.apache.commons.lang3.StringUtils;
import org.apache.struts2.dispatcher.Parameter;

/**
 * @author Hermes Jiménez - CIAT/CCAFS
 */
public class SearchUserAction extends BaseAction {


  private static final long serialVersionUID = 9059423997951037898L;


  private UserManager userManager;


  private String userEmail;


  private User user;

  private Map<String, Object> userFound;


  private List<Map<String, Object>> crpUserFound;


  @Inject
  public SearchUserAction(APConfig config, UserManager userManager) {
    super(config);
    this.userManager = userManager;
  }


  @Override
  public String execute() throws Exception {
    userFound = new HashMap<String, Object>();
    crpUserFound = new ArrayList<>();;
    Phase currentPhase = this.getActualPhase();
    boolean emailExists = false;
    // We need to validate that the email does not exist yet into our database.
    emailExists = userManager.getUserByEmail(userEmail) == null ? false : true;

    if (emailExists) {
      user = userManager.getUserByEmail(userEmail);

      userFound.put("newUser", false);
      userFound.put("id", user.getId());
      userFound.put("name", user.getFirstName());
      userFound.put("lastName", user.getLastName());
      userFound.put("username", user.getUsername());
      userFound.put("email", user.getEmail());
      userFound.put("cgiar", user.isCgiarUser());
      userFound.put("active", user.isActive());
      userFound.put("autosave", user.isAutoSave());

      List<CrpUser> crpUsers =
        new ArrayList<CrpUser>(user.getCrpUsers().stream().filter(c -> c.isActive()).collect(Collectors.toList()));

      if (!crpUsers.isEmpty()) {
        for (CrpUser crpUser : crpUsers) {
          Map<String, Object> crp = new HashMap<>();
          crp.put("crpUserId", crpUser.getId());
          crp.put("crpId", crpUser.getCrp().getId());
          crp.put("crpName", crpUser.getCrp().getName());
          crp.put("crpAcronym", crpUser.getCrp().getAcronym().toUpperCase());


          List<UserRole> userRoles = new ArrayList<>(user.getUserRoles().stream()
            .filter(ur -> ur.getRole().getCrp().getId() == crpUser.getCrp().getId()).collect(Collectors.toList()));

          List<Map<String, Object>> roleUserFound = new ArrayList<>();;
          for (UserRole userRole : userRoles) {
            Map<String, Object> role = new HashMap<>();

            role.put("role", userRole.getRole().getDescription());
            List<String> roleInfo = new ArrayList<>();
            switch (userRole.getRole().getAcronym()) {

              case "ML":
                List<LiaisonUser> liaisonMLUsers = new ArrayList<>(user.getLiasonsUsers().stream()
                  .filter(lu -> lu.isActive() && lu.getLiaisonInstitution().getCrpProgram() != null)
                  .collect(Collectors.toList()));
                for (LiaisonUser liaisonUser : liaisonMLUsers) {
                  roleInfo.add(liaisonUser.getLiaisonInstitution().getComposedName());
                }
                role.put("roleInfo", roleInfo);
                break;

              case "CP":
                List<LiaisonUser> liaisonCPUsers = new ArrayList<>(user.getLiasonsUsers().stream()
                  .filter(lu -> lu.isActive() && lu.getLiaisonInstitution().getCrpProgram() == null)
                  .collect(Collectors.toList()));
                for (LiaisonUser liaisonUser : liaisonCPUsers) {
                  roleInfo.add(liaisonUser.getLiaisonInstitution().getComposedName());
                }
                role.put("roleInfo", roleInfo);
                break;

              case "PL":
                List<ProjectPartnerPerson> partnerPLPersons = new ArrayList<>(user.getProjectPartnerPersons().stream()
                  .filter(pp -> pp.isActive() && pp.getContactType().equals("PL")).collect(Collectors.toList()));
                for (ProjectPartnerPerson partnerPerson : partnerPLPersons) {
                  roleInfo.add(partnerPerson.getProjectPartner().getProject().getComposedName(currentPhase));
                }
                role.put("roleInfo", roleInfo);
                break;

              case "PC":
                List<ProjectPartnerPerson> partnerPCPersons = new ArrayList<>(user.getProjectPartnerPersons().stream()
                  .filter(pp -> pp.isActive() && pp.getContactType().equals("PC")).collect(Collectors.toList()));
                for (ProjectPartnerPerson partnerPerson : partnerPCPersons) {
                  roleInfo.add(partnerPerson.getProjectPartner().getProject().getComposedName(currentPhase));
                }
                role.put("roleInfo", roleInfo);
                break;

              case "RPL":
                List<CrpProgramLeader> programRLeaders = new ArrayList<>(user.getCrpProgramLeaders().stream()
                  .filter(pl -> pl.isActive() && pl.getCrpProgram().getProgramType() == 2)
                  .collect(Collectors.toList()));
                for (CrpProgramLeader crpProgramLeader : programRLeaders) {
                  roleInfo.add(crpProgramLeader.getCrpProgram().getComposedName());
                }
                role.put("roleInfo", roleInfo);
                break;

              case "FPL":
                List<CrpProgramLeader> programFLeaders = new ArrayList<>(user.getCrpProgramLeaders().stream()
                  .filter(pl -> pl.isActive() && pl.getCrpProgram().getProgramType() == 1)
                  .collect(Collectors.toList()));
                for (CrpProgramLeader crpProgramLeader : programFLeaders) {
                  roleInfo.add(crpProgramLeader.getCrpProgram().getComposedName());
                }
                role.put("roleInfo", roleInfo);
                break;

            }

            roleUserFound.add(role);
          }
          crp.put("role", roleUserFound);

          crpUserFound.add(crp);

        }


      }

      return SUCCESS;
    } else {
      if (userEmail.toLowerCase().endsWith(APConstants.OUTLOOK_EMAIL)) {

        LDAPService service = new LDAPService();
        if (config.isProduction()) {
          service.setInternalConnection(false);
        } else {
          service.setInternalConnection(true);
        }

        LDAPUser userLDAP;
        try {
          userLDAP = service.searchUserByEmail(userEmail.toLowerCase());
        } catch (Exception e) {
          userLDAP = null;
        }

        if (userLDAP != null) {

          userFound.put("newUser", true);
          userFound.put("id", -1);
          userFound.put("name", userLDAP.getFirstName());
          userFound.put("lastName", userLDAP.getLastName());
          userFound.put("username", userLDAP.getLogin().toLowerCase());
          userFound.put("email", userLDAP.getEmail().toLowerCase());
          userFound.put("cgiar", true);
          userFound.put("active", false);
          userFound.put("autosave", false);

        } else {
          userFound.put("newUser", false);
          userFound.put("cgiar", false);
          userFound.put("cgiarNoExist", true);
        }
      } else {
        userFound.put("newUser", true);
        userFound.put("id", -1);
        userFound.put("email", userEmail.toLowerCase());
        userFound.put("cgiar", false);
      }
    }


    return SUCCESS;
  }


  public List<Map<String, Object>> getCrpUserFound() {
    return crpUserFound;
  }


  public String getUserEmail() {
    return userEmail;
  }


  public Map<String, Object> getUserFound() {
    return userFound;
  }


  @Override
  public void prepare() throws Exception {
    // Map<String, Object> parameters = this.getParameters();
    // userEmail = StringUtils.trim(((String[]) parameters.get(APConstants.USER_EMAIL))[0]);

    Map<String, Parameter> parameters = this.getParameters();
    userEmail = StringUtils.trim(parameters.get(APConstants.USER_EMAIL).getMultipleValues()[0]);
  }


  public void setCrpUserFound(List<Map<String, Object>> crpUserFound) {
    this.crpUserFound = crpUserFound;
  }


  public void setUserEmail(String userEmail) {
    this.userEmail = userEmail;
  }

  public void setUserFound(Map<String, Object> userFound) {
    this.userFound = userFound;
  }


}
