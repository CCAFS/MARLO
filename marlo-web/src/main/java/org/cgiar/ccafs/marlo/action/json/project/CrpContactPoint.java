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

package org.cgiar.ccafs.marlo.action.json.project;

import org.cgiar.ccafs.marlo.action.BaseAction;
import org.cgiar.ccafs.marlo.config.APConstants;
import org.cgiar.ccafs.marlo.data.manager.FundingSourceManager;
import org.cgiar.ccafs.marlo.data.manager.GlobalUnitManager;
import org.cgiar.ccafs.marlo.data.manager.PhaseManager;
import org.cgiar.ccafs.marlo.data.manager.RoleManager;
import org.cgiar.ccafs.marlo.data.model.GlobalUnit;
import org.cgiar.ccafs.marlo.data.model.Role;
import org.cgiar.ccafs.marlo.security.Permission;
import org.cgiar.ccafs.marlo.utils.APConfig;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;
import org.apache.struts2.dispatcher.Parameter;


public class CrpContactPoint extends BaseAction {


  private static final long serialVersionUID = 6304226585314276677L;


  List<Map<String, Object>> sources;

  private long phaseID;
  private String financeCode;

  // GlobalUnit Manager
  private GlobalUnitManager crpManager;
  private String queryParameter;
  private FundingSourceManager fundingSourceManager;
  private PhaseManager phaseManager;
  private Long cpCrpID;
  private RoleManager roleManager;


  @Inject
  public CrpContactPoint(APConfig config, FundingSourceManager fundingSourceManager, GlobalUnitManager crpManager,
    PhaseManager phaseManager, RoleManager roleManager) {
    super(config);
    this.fundingSourceManager = fundingSourceManager;
    this.crpManager = crpManager;
    this.phaseManager = phaseManager;
    this.roleManager = roleManager;
  }


  @Override
  public String execute() throws Exception {
    sources = new ArrayList<>();

    GlobalUnit loggedCrp = crpManager.getGlobalUnitById(this.getCrpID());

    /**
     * Read only summary objects (not hibernate entities)
     */
    List<String> summaries = null;

    this.getCrpContactPoint();
    if (cpCrpID != null) {
      summaries.add(cpCrpID.toString());
      String permission =
        this.generatePermission(Permission.PROJECT_FUNDING_W1_BASE_PERMISSION, loggedCrp.getAcronym());

      boolean hasPermission = this.hasPermissionNoBase(permission);

      // sources.add(summaries.convertToMap());

    }
    return SUCCESS;
  }


  public void getCrpContactPoint() {

    // Check if the CRP has Contact Point and ContactPointRole, if not cpRole will be null (it will be used as a flag)

    Role cpRol = null;
    if (this.hasSpecificities(APConstants.CRP_HAS_CP)
      && roleManager.getRoleById(Long.parseLong((String) this.getSession().get(APConstants.CRP_CP_ROLE))) != null) {
      cpRol = roleManager.getRoleById(Long.parseLong((String) this.getSession().get(APConstants.CRP_CP_ROLE)));
    }

    List<Role> roles = new ArrayList<>();
    roles = this.getRolesList();

    if (roles.contains(cpRol)) {
      cpCrpID = cpRol.getCrp().getId();
    } else {
      cpCrpID = (long) 0;
    }
  }

  public List<Map<String, Object>> getSources() {
    return sources;
  }


  @Override
  public void prepare() throws Exception {
    Map<String, Parameter> parameters = this.getParameters();
    financeCode = StringUtils.trim(parameters.get(APConstants.FINANCE_CODE).getMultipleValues()[0]);
  }

  public void setSources(List<Map<String, Object>> sources) {
    this.sources = sources;
  }

}
