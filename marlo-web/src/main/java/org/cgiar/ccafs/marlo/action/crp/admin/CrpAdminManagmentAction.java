/*****************************************************************
 * This file is part of CCAFS Planning and Reporting Platform.
 * CCAFS P&R is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * at your option) any later version.
 * CCAFS P&R is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 * You should have received a copy of the GNU General Public License
 * along with CCAFS P&R. If not, see <http://www.gnu.org/licenses/>.
 *****************************************************************/


package org.cgiar.ccafs.marlo.action.crp.admin;


import org.cgiar.ccafs.marlo.action.BaseAction;
import org.cgiar.ccafs.marlo.config.APConstants;
import org.cgiar.ccafs.marlo.data.manager.CrpManager;
import org.cgiar.ccafs.marlo.data.manager.CrpProgramManager;
import org.cgiar.ccafs.marlo.data.manager.RoleManager;
import org.cgiar.ccafs.marlo.data.manager.UserRoleManager;
import org.cgiar.ccafs.marlo.data.model.Crp;
import org.cgiar.ccafs.marlo.data.model.CrpProgram;
import org.cgiar.ccafs.marlo.data.model.ProgramType;
import org.cgiar.ccafs.marlo.data.model.Role;
import org.cgiar.ccafs.marlo.data.model.UserRole;
import org.cgiar.ccafs.marlo.security.Permission;
import org.cgiar.ccafs.marlo.utils.APConfig;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

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
  // Variables
  private Crp loggedCrp;
  private Role rolePmu;
  private long pmuRol;

  private List<CrpProgram> flagshipsPrograms;
  private List<CrpProgram> regionsPrograms;


  @Inject
  public CrpAdminManagmentAction(APConfig config, RoleManager roleManager, UserRoleManager userRoleManager,
    CrpProgramManager crpProgramManager, CrpManager crpManager) {
    super(config);
    this.roleManager = roleManager;
    this.userRoleManager = userRoleManager;
    this.crpManager = crpManager;
    this.crpProgramManager = crpProgramManager;
  }


  public List<CrpProgram> getFlagshipsPrograms() {
    return flagshipsPrograms;
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


  @SuppressWarnings("unchecked")
  @Override
  public void prepare() throws Exception {

    // Get the Users list that have the pmu role in this crp.
    loggedCrp = (Crp) this.getSession().get(APConstants.SESSION_CRP);
    loggedCrp = crpManager.getCrpById(loggedCrp.getId());
    pmuRol = Long.parseLong((String) this.getSession().get(APConstants.CRP_PMU_ROLE));
    rolePmu = roleManager.getRoleById(pmuRol);
    loggedCrp.setProgramManagmenTeam(new ArrayList<UserRole>(rolePmu.getUserRoles()));
    String params[] = {loggedCrp.getAcronym()};

    // Get the Flagship list of this crp
    flagshipsPrograms = loggedCrp.getCrpPrograms().stream()
      .filter(c -> c.getProgramType() == ProgramType.FLAGSHIP_PROGRAM_TYPE.getValue() && c.isActive())
      .collect(Collectors.toList());


    this.setBasePermission(this.getText(Permission.CRP_ADMIN_BASE_PERMISSION, params));
    if (this.isHttpPost()) {
      loggedCrp.getProgramManagmenTeam().clear();
      flagshipsPrograms.clear();
    }
  }


  @Override
  public String save() {
    if (this.hasPermission("*")) {

      Role rolePreview = roleManager.getRoleById(pmuRol);
      // Removing users roles
      for (UserRole userRole : rolePreview.getUserRoles()) {
        if (!loggedCrp.getProgramManagmenTeam().contains(userRole)) {
          userRoleManager.deleteUserRole(userRole.getId());
        }
      }
      // Add new Users roles
      for (UserRole userRole : loggedCrp.getProgramManagmenTeam()) {
        if (userRole.getId() == null) {
          userRoleManager.saveUserRole(userRole);
        }
      }

      List<CrpProgram> fgProgramsRewiev =
        crpProgramManager.findCrpProgramsByType(loggedCrp.getId(), ProgramType.FLAGSHIP_PROGRAM_TYPE.getValue());
      // Removing crp flagship program type


      if (fgProgramsRewiev != null) {
        for (CrpProgram crpProgram : fgProgramsRewiev) {
          if (!flagshipsPrograms.contains(crpProgram)) {
            crpProgramManager.deleteCrpProgram(crpProgram.getId());
          }
        }
      }

      // Add crp flagship program type
      for (CrpProgram crpProgram : flagshipsPrograms) {
        if (crpProgram.getId() == null) {
          crpProgram.setCrp(loggedCrp);
          crpProgram.setActive(true);
          crpProgramManager.saveCrpProgram(crpProgram);
        }
      }
      /*
       * List<CrpProgram> rgProgramsRewiev =
       * crpProgramManager.findCrpProgramsByType(loggedCrp.getId(), ProgramType.FLAGSHIP_PROGRAM_TYPE.getValue());
       * // Removing crp region program type
       * if (fgProgramsRewiev != null) {
       * for (CrpProgram crpProgram : rgProgramsRewiev) {
       * if (!regionsPrograms.contains(crpProgram)) {
       * crpProgramManager.deleteCrpProgram(crpProgram.getId());
       * }
       * }
       * }
       * // Add crp region program type
       * for (CrpProgram crpProgram : regionsPrograms) {
       * if (crpProgram.getId() == null) {
       * crpProgram.setCrp(loggedCrp);
       * crpProgram.setActive(true);
       * crpProgramManager.saveCrpProgram(crpProgram);
       * }
       * }
       */

      Collection<String> messages = this.getActionMessages();
      if (!messages.isEmpty()) {
        String validationMessage = messages.iterator().next();
        this.setActionMessages(null);
        this.addActionWarning(this.getText("saving.saved") + validationMessage);
      } else {
        this.addActionMessage(this.getText("saving.saved"));
      }
      return SUCCESS;
    } else {
      return NOT_AUTHORIZED;
    }

  }

  public void setFlagshipsPrograms(List<CrpProgram> flagshipsPrograms) {
    this.flagshipsPrograms = flagshipsPrograms;
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


}
